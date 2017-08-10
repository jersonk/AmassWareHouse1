package com.amassfreight.warehouse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.utils.DateDeserializer;
import com.amassfreight.utils.SessionHelper;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.ui.AppSettingActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

public class SplashActivity extends BaseActivity {

    // private static String BASE_HOST = "192.168.48.194";

    private static String BASE_URI = "warehouse";
    private static String BASE_HOST = "192.168.48.206";

    private static String APK_ZARCHIVER = "ZArchiver.apk";
    private static String APK_WPSOFFICE = "WPSOffice.apk";

    private AsyncHttpClient httpClient = new AsyncHttpClient();
    private String host;//域名
    private ApkVer apkVer;

    private Button btnCheck;
    private TextView textStatus;
    private ProgressBar progressBar;//旋转的进度条
    private ProgressBar mProgress;//下载进度条
    private Button btnDownloadZar;
    private Button btnDownloadOffice;
    private Boolean bolSysRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
        InitUrl();

        bolSysRun = false;
        getSysRun();
    }

    private void initView() {
        btnCheck = (Button) findViewById(R.id.btnCheckVersion);
        btnCheck.setVisibility(View.GONE);

        btnDownloadZar = (Button) findViewById(R.id.btnDownloadZar);
        btnDownloadOffice = (Button) findViewById(R.id.btnDownloadOffice);

        textStatus = (TextView) findViewById(R.id.textViewStatus);
        textStatus.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //点击检查 version 版本
        btnCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                checkApkVersion(host);
            }
        });

        // 点击下载插件
        btnDownloadZar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btnDownloadZar.setVisibility(View.GONE);
                showDownloadNeedsDialog(getString(R.string.install_apk_zip), APK_ZARCHIVER);
            }
        });

        btnDownloadOffice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btnDownloadOffice.setVisibility(View.GONE);
                showDownloadNeedsDialog(getString(R.string.install_apk_office), APK_WPSOFFICE);
            }
        });
    }

    private void InitUrl() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        host = sharedPrefs.getString(AppSettingActivity.KEY_SERVEER_URL,
                BASE_HOST);
        if (host == null || host.isEmpty()) {
            host = BASE_HOST;
        }

        checkApkVersion(host);

        checkNeedPackages();
        String sUrl = String.format("http://%s/%s/Mobile/FreightMobile.asmx/",
                host, BASE_URI);
        NetworkHelper.initUrl(sUrl, this);
    }

    /**
     * 检查服务器的版本
     * url 地址：http://192.168.48.206/warehouse/Mobile/apkver.json
     *
     * @param host
     * @return boolean
     */
    private boolean checkApkVersion(String host) {
        this.host = host;
        progressBar.setVisibility(View.VISIBLE);
        textStatus.setVisibility(View.GONE);
        btnCheck.setVisibility(View.GONE);
        String apkVerUrl = String.format("http://%s/%s/Mobile/apkver.json",
                host, BASE_URI);

        httpClient.get(apkVerUrl, new VerCheckHttpResponseHandler());

        return true;
    }

    //检测是否需要安装 三方包
    private boolean checkNeedPackages() {
        boolean rcZar = checkInstallZarApk();
        boolean rcOffice = checkInsallOfficeApk();
        if (rcZar && rcOffice && bolSysRun) {
            return true;
        } else {
            return false;
        }

    }

    private boolean checkInstallZarApk() {
        if (!isAppInstalled("ru.zdevs.zarchiver")) {
            btnDownloadZar.setVisibility(View.VISIBLE);
            return false;
        } else {
            btnDownloadZar.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean checkInsallOfficeApk() {
        if (!isAppInstalled("cn.wps.moffice_eng")) {
            btnDownloadOffice.setVisibility(View.VISIBLE);
            return false;
        } else {
            btnDownloadOffice.setVisibility(View.GONE);
            return true;
        }
    }

    private void finishedCheck() {

        if (checkNeedPackages()) {
            Intent intent = new Intent(_thisActivity, LoginActivity.class);
            intent.putExtra(LoginActivity.LOGIN_EXTRA_DATA, getIntent()
                    .getSerializableExtra(LoginActivity.LOGIN_EXTRA_DATA));
            startActivity(intent);
            finish();
        } else {
            textStatus.setText("检查到必要的软件没有安装，请安装。");
            textStatus.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            btnCheck.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 检查更新响应
     *
     * @author U11001548
     */
    class VerCheckHttpResponseHandler extends TextHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseBody) {
            //检查更新
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class,
                    new DateDeserializer()).create();
            JsonReader reader = new JsonReader(new StringReader(responseBody));
            reader.setLenient(true);
            apkVer = gson.fromJson(reader, ApkVer.class);

            //检查版本号
            if (apkVer.verCode != Utils.getVerCode(_thisActivity)) {
                showDownloadDialog();
            } else {
                finishedCheck();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseBody, Throwable error) {
            // super.onFailure(statusCode, headers, responseBody, error);
            if (statusCode == 404) {
                finishedCheck();
            } else {
                String errMsg;
                if (error instanceof SocketTimeoutException) {
                    errMsg = getString(R.string.network_time_out_msg);
                } else {
                    errMsg = String.format("%s:%d:%s", getString(R.string.network_err_msg), statusCode,
                            error.getLocalizedMessage());
                }
                textStatus.setText(errMsg);
                textStatus.setVisibility(View.VISIBLE);
                btnCheck.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }

    }

    /**
     * 版本更新
     *
     * @author U11001548
     */
    class ApkVer {

        private String appname;
        private String apkname;
        private String verName;
        private int verCode;

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getApkname() {
            return apkname;
        }

        public void setApkname(String apkname) {
            this.apkname = apkname;
        }

        public String getVerName() {
            return verName;
        }

        public void setVerName(String verName) {
            this.verName = verName;
        }

        public int getVerCode() {
            return verCode;
        }

        public void setVerCode(int verCode) {
            this.verCode = verCode;
        }
    }

    //########################### 下载更新   ################################

    private Dialog downloadDialog;

    private String apkUrl;

    /**
     * 下载相应的工具包
     *
     * @param appName
     * @param apkName
     */
    private void showDownloadNeedsDialog(String appName, String apkName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light));
        builder.setTitle(appName);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.updateprogress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        builder.setView(v);
        builder.setCancelable(false);
        downloadDialog = builder.show();
        downloadNeedsApk(host, apkName);
    }

    /**
     * 下载插件相关apk
     *
     * @param host
     * @param apkName
     */
    private void downloadNeedsApk(String host, final String apkName) {
        apkUrl = String.format("http://%s/%s/Mobile/%s", host, BASE_URI,
                apkName);
        httpClient.get(apkUrl, new TextHttpResponseHandler() {

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                mProgress.setProgress(bytesWritten * 100 / totalSize);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] binaryData) {
                File dir = new File(Environment.getExternalStorageDirectory(),
                        "amass/apk/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File apkfile = new File(dir, apkName);
                try {
                    FileOutputStream fos = new FileOutputStream(apkfile);
                    fos.write(binaryData);
                    fos.close();
                    installApk(apkfile, 2);

                } catch (Exception e) {
                    Log.e("InstallApk", e.getMessage(), e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                downloadDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                finishedCheck();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {
                // TODO Auto-generated method stub

            }

        });
    }

    /**
     * show download amass.apk progress
     */
    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light));
        builder.setTitle("软件版本更新");
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.updateprogress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        builder.setView(v);
        downloadDialog = builder.create();
        downloadDialog.show();
        downloadApk(host);
    }

    /**
     * 下载amass.apk
     *
     * @param host
     */
    private void downloadApk(String host) {
        apkUrl = String.format("http://%s/%s/Mobile/%s", host, BASE_URI,
                apkVer.apkname);
        httpClient.get(apkUrl, new TextHttpResponseHandler() {

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                mProgress.setProgress(bytesWritten * 100 / totalSize);

                //增加相应的状态栏提示
//				NotificationManager notificationManager = mcon
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] binaryData) {
                File dir = new File(Environment.getExternalStorageDirectory(),
                        "amass/apk/");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File apkfile = new File(dir, apkVer.apkname);
                try {
                    FileOutputStream fos = new FileOutputStream(apkfile);
                    fos.write(binaryData);
                    fos.close();
                    installApk(apkfile, 1);
                    // finish();

                } catch (Exception e) {
                    Log.e("UpdateApk", e.getMessage(), e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                downloadDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                finishedCheck();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {
                // TODO Auto-generated method stub

            }

        });
    }

    /**
     * 安装apk
     *
     * @param apkfile
     * @param requestCode 增加相应的request code | 插件apk安装完
     */
    private void installApk(File apkfile, int requestCode) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                if (resultCode != RESULT_OK) {
                    Utils.showAlertDialog(this, "系统检查到新版本，请更新到最新版本后继续工作。");
                    progressBar.setVisibility(View.GONE);
                    btnCheck.setVisibility(View.VISIBLE);
                    return;
                }
                break;

            case 2:
                // if (resultCode != RESULT_OK) {
                // Utils.showAlertDialog(this, "请安装好必须得工具后继续工作。");
                // finishedCheck();
                // }
                break;
        }
    }

    private boolean isAppInstalled(String appUri) {
        // ru.zdevs.zarchiver
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(appUri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    private void getSysRun() {
        Map<String, Object> params = new HashMap<String, Object>();
        NetworkHelper.getInstance().postJsonData(_thisActivity, "GetMcommRun",
                params, String.class, new AmassHttpResponseHandler<String>() {

                    @Override
                    protected void OnSuccess(String response) {
                        SessionHelper.getInstance().setSysRun(response);
                        bolSysRun = true;
                        finishedCheck();
                    }
                }, false);
    }

}
