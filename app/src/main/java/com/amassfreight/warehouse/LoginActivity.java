package com.amassfreight.warehouse;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.LogonFormData;
import com.amassfreight.domain.LogonUser;
import com.amassfreight.domain.NormalUser;
import com.amassfreight.domain.SelectDict;
import com.amassfreight.utils.DateDeserializer;
import com.amassfreight.utils.SessionHelper;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.ui.AppSettingActivity;
import com.amassfreight.widget.SquaredPassWord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

public class LoginActivity extends BaseActivity {

	private EditText userNameEdit;
	private Spinner depotList;
//	private Button btnLogin;
	private TextView textNetworkStatus;
	private Button btnRefreshNetwork;
	private SquaredPassWord passwordEdit;
	public static String LOGIN_EXTRA_DATA = "LoginUserData";
	private ApkVer apkVer;
	private AsyncHttpClient httpClient = new AsyncHttpClient();
	private String pw;
	private String host;
	private static String BASE_URI = "warehouse";
	private static String BASE_HOST = "192.168.48.206";
	//private static String BASE_HOST = "192.168.48.149"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		userNameEdit = (EditText) findViewById(R.id.edit_login_username);
		passwordEdit = (SquaredPassWord) findViewById(R.id.passwordView);
		textNetworkStatus = (TextView) findViewById(R.id.textRefreshNetwork);
		btnRefreshNetwork = (Button) findViewById(R.id.refreshButton);
		passwordEdit
				.setOnCompleteListener(new SquaredPassWord.OnCompleteListener() {

					@Override
					public void onComplete(String password) {
						onLogin(password);
					}
				});

		textNetworkStatus.setVisibility(View.GONE);
		btnRefreshNetwork.setVisibility(View.GONE);
		passwordEdit.setVisibility(View.GONE);

		btnRefreshNetwork.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				textNetworkStatus.setVisibility(View.GONE);
				btnRefreshNetwork.setVisibility(View.GONE);
				passwordEdit.setVisibility(View.GONE);
				InitDepotList();
			}
		});
//		btnLogin = (Button) findViewById(R.id.btn_login);
//		btnLogin.setEnabled(false);

		LogonFormData loginData = (LogonFormData) getIntent()
				.getSerializableExtra(LOGIN_EXTRA_DATA);
		// if(loginData == null){
		// SessionHelper.getInstance().getAuthenUser();
		// }
		if (loginData == null) {
			loginData = getLoginInfo();
		}
		if (loginData != null) {
			userNameEdit.setText(loginData.getUserId());
		}
		depotList = (Spinner) findViewById(R.id.depotno_list);

		InitDepotList();
		getSysTime();
	}

	private void getSysTime(){
		Map<String, Object> params = new HashMap<String, Object>();
		NetworkHelper.getInstance().postJsonData(_thisActivity, "Sys_getSysTime", params, Date.class, new AmassHttpResponseHandler<Date>(){

			@Override
			protected void OnSuccess(Date response) {
				SessionHelper.getInstance().initSysTime(response);
			}
			
		}, false);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	private void InitDepotList() {
		Type a = new TypeToken<List<SelectDict>>() {
		}.getType();
		// List<SelectDict> b = new ArrayList<SelectDict>();

		NetworkHelper.getInstance().postData(this, "GetDepotNoList", null, a,
				new AmassHttpResponseHandler<List<SelectDict>>() {
					@Override
					protected void OnSuccess(List<SelectDict> response) {
						super.OnSuccess(response);
						List<SelectDict> listData = response;
						ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
								_thisActivity,
								android.R.layout.simple_spinner_item, listData);
						selectList
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						depotList.setAdapter(selectList);
						if (SessionHelper.getInstance().getAuthenUser() != null) {
							SelectDict curPos = new SelectDict();
							curPos.setId(SessionHelper.getInstance()
									.getAuthenUser().getDepotNo());

							depotList.setSelection(selectList
									.getPosition(curPos));
						}
						passwordEdit.setVisibility(View.VISIBLE);
					}

					@Override
					protected void onFailure(int statusCode, Header[] headers,
							String responseBody, Throwable e) {
						super.onFailure(statusCode, headers, responseBody, e);
						textNetworkStatus.setVisibility(View.VISIBLE);
						btnRefreshNetwork.setVisibility(View.VISIBLE);
						passwordEdit.setVisibility(View.GONE);
					}

				}, true);

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.login, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// Intent intent = new Intent(Intent.ACTION_VIEW);
	// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	// switch (item.getItemId()) {
	// // case R.id.menu_share:
	// // intent.setClassName(this, ShareActivity.class.getName());
	// // startActivity(intent);
	// // break;
	// // case R.id.menu_history:
	// // intent.setClassName(this, HistoryActivity.class.getName());
	// // startActivityForResult(intent, HISTORY_REQUEST_CODE);
	// // break;
	// case R.id.action_settings:
	// intent.setClassName(this, AppSettingActivity.class.getName());
	// startActivity(intent);
	// break;
	// // case R.id.menu_help:
	// // intent.setClassName(this, HelpActivity.class.getName());
	// // startActivity(intent);
	// // break;
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// return true;
	// }

	private void saveLoginInfo(LogonFormData u) {
		SharedPreferences sharedPref = getSharedPreferences(
				"com.amassfreight.loginUser", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("userId", u.getUserId());
		editor.putString("depotNo", u.getDepotNo());
		editor.commit();
	}

	private LogonFormData getLoginInfo() {
		SharedPreferences sharedPref = getSharedPreferences(
				"com.amassfreight.loginUser", Context.MODE_PRIVATE);
		String userId = sharedPref.getString("userId", null);
		if (userId == null) {
			return null;
		} else {
			LogonFormData u = new LogonFormData();
			u.setUserId(userId);
			u.setDepotNo(sharedPref.getString("depotNo", null));
			return u;
		}
	}

	// private void updateFiles(){
	// RequestParams params = new RequestParams();
	// File dir = new File(Environment
	// .getExternalStorageDirectory(), "amass");
	// dir.mkdir();
	// dir = new File(Environment.getExternalStorageDirectory(),
	// "amass/pics");
	// dir.mkdir();
	// File f = new File(dir, "2f0a83e5-d450-4949-a1ee-d268d84742b5.jpg");
	//
	// try {
	// for(int n = 0; n <4; n++){
	// params.put(String.format("file%d.jpg", n), f);
	//
	// }
	//
	// } catch (FileNotFoundException e) {
	// 
	// e.printStackTrace();
	// }
	// NetworkHelper.getInstance().postFilesData(this,
	// "UploadFiles", "login", params, null,
	// new AmassHttpResponseHandler() {
	// }, true);
	//
	// }
	//

	// private void onLogin(password)
	// {
	//
	// }
	// public void Btn_Login_OnClick(View view) {
	// updateFiles();
	// if(true)
	// return;
	private void onLogin(String password) {
		
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		host = sharedPrefs.getString(AppSettingActivity.KEY_SERVEER_URL,
				BASE_HOST);
		if (host == null || host.isEmpty()) {
			host = BASE_HOST;
		}
		String apkVerUrl = String.format("http://%s/%s/Mobile/apkver.json",host, BASE_URI);
		//String apkVerUrl = String.format("http://%s/freight/Mobile/apkver.json", host);
		String msg = "取版本号URL"+apkVerUrl;
		Toast toast = Toast.makeText(_thisActivity,msg, Toast.LENGTH_LONG);                  
		toast.show(); 
		pw = password;
		httpClient.get(apkVerUrl, new VerCheckHttpResponseHandler());
		//finishedCheck();

	}

	class VerCheckHttpResponseHandler extends TextHttpResponseHandler {
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseBody) {
			Gson gson = new GsonBuilder().registerTypeAdapter(Date.class,
					new DateDeserializer()).create();

			JsonReader reader = new JsonReader(new StringReader(responseBody));
			reader.setLenient(true);
			apkVer = gson.fromJson(reader, ApkVer.class);
			String msg = "服务器版本号："+apkVer.verCode+"；本地版本号："+Utils.getVerCode(_thisActivity);
			Toast toast = Toast.makeText(_thisActivity,msg, Toast.LENGTH_LONG);                  
			toast.show();             
			//System.out.println(msg);
			if (apkVer.verCode != Utils.getVerCode(_thisActivity)) {
				passwordEdit.clearPassword();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						new ContextThemeWrapper(_thisActivity,
								android.R.style.Theme_Holo_Light));
				builder.setIcon(R.drawable.ic_launcher);
				builder.setCancelable(false);
				builder.setMessage(R.string.msg_login_error_002);
				builder.setTitle(R.string.app_name);
				builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						_thisActivity.finish();
					}
				});
				builder.create().show();				
			} else {
				finishedCheck();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseBody, Throwable error) {
			if (statusCode == 404) {
				finishedCheck();
			} else {
				String errMsg;
				if (error instanceof SocketTimeoutException) {
					errMsg = getString(R.string.msg_login_error_003);
				} else {
					errMsg = String.format("%s:%d:%s", getString(R.string.msg_login_error_004), statusCode,
							error.getLocalizedMessage());
				}
				Utils.showAlertDialog(_thisActivity,errMsg);				
			}
		}

	}
	
	private void finishedCheck() {
		passwordEdit.clearPassword();
		LogonFormData u = new LogonFormData();
		u.setUserId(userNameEdit.getText().toString());
		u.setPassword(pw);
		SelectDict depotNoItem = (SelectDict) depotList.getSelectedItem();
		u.setDepotNo(depotNoItem.getId());
		if (u.getUserId().isEmpty()) {
			Utils.showAlertDialogIntMsg(this, R.string.msg_login_error_001);
			userNameEdit.requestFocus();
			return;
		}
		SessionHelper.getInstance().setAuthenUser(u);
		saveLoginInfo(u);
		NetworkHelper.getInstance().postData(this, "Login", u, LogonUser.class,
				new AmassHttpResponseHandler<LogonUser>() {

					@Override
					protected void OnSuccess(LogonUser response) {
						super.OnSuccess(response);
						// textViewBarCode.setText(response.toString());
						NormalUser user = new NormalUser();
						// BaseDataList<FunctionMenu> menus =
						// (BaseDataList<FunctionMenu>) response;
						LogonUser userLogon = (LogonUser) response;
						user.setMenus(userLogon.getMenus());
						user.setUserId(userLogon.getUserId());
						user.setUserName(userLogon.getUserName());
						// user.setRoles(userLogon.getRoles());

						SessionHelper.getInstance().setUser(user);
						SessionHelper.getInstance().initDictList(_thisActivity);
						Bundle bundle = _thisActivity.getIntent().getExtras();
						Object callFrom = null;
						if (bundle != null) {
							callFrom = bundle.get(Utils.CALL_FROM_SET);
						}
						if (callFrom == null) {
							Intent intent = new Intent(_thisActivity,
									MainScreenActivity.class);
							startActivity(intent);
						}
						_thisActivity.finish();
					}

					@Override
					protected void onFailure(int statusCode, Header[] headers,
							String responseBody, Throwable e) {
						super.onFailure(statusCode, headers, responseBody, e);
						String msg = String.format("%d:%s", statusCode,
								e.getLocalizedMessage());
						Log.d("Login", msg);
						Utils.showAlertDialog(_thisActivity, msg);
					}

				}, true);
	}
	
	class ApkVer {
		public ApkVer() {
		}

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
	// private File cameraTempFile;
	// public void Barcode_OnClick(View arg0) {
	// Intent intent = new Intent(this, CaptureActivity.class);
	// intent.putExtra("SCAN_MODE", "SCAN_MODE");
	// startActivityForResult(intent, 2);
	// // Intent intent = new Intent();
	// // // 指定开启系统相机的Action
	// // intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
	// // intent.addCategory(Intent.CATEGORY_DEFAULT);
	// // File dir = new File(Environment.getExternalStorageDirectory(),
	// "amass");
	// // dir.mkdir();
	// // dir = new
	// File(Environment.getExternalStorageDirectory(),"amass/pics");
	// // dir.mkdir();
	// // cameraTempFile = new File(dir, UUID.randomUUID() + ".jpg");
	// // intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(cameraTempFile));
	// // startActivityForResult(intent, 3);
	//
	// // File dir = new File(Environment.getExternalStorageDirectory(),
	// "amass/提单打印与发送.xls");
	// // File dir = new File(Environment.getExternalStorageDirectory(),
	// "amass/precsum201306271634.txt");
	// // File dir = new File(Environment.getExternalStorageDirectory(),
	// "amass/funtest1234.zip");
	// // Utils.openFile(this, dir.getAbsolutePath());
	// //
	// //
	// // Map<String, Object> p = new HashMap<String, Object>();
	// // p.put("nPage", "1");
	// // NetworkHelper.getInstance().postJsonReturnBinary(this, "GetMoreData",
	// // p, new AmassHttpResponseHandler(){
	// //
	// // @Override
	// // protected void OnSuccess(byte[] response) {
	// // 
	// // super.OnSuccess(response);
	// // File dir = new File(Environment
	// // .getExternalStorageDirectory(), "amass");
	// // dir.mkdir();
	// // dir = new File(Environment
	// // .getExternalStorageDirectory(), "amass/pics");
	// // dir.mkdir();
	// // File f = new File(dir, UUID.randomUUID() + ".txt");
	// // try {
	// // FileOutputStream fs = new FileOutputStream(f);
	// // fs.write(response);
	// // fs.close();
	// //
	// // } catch (Exception e) {
	// // 
	// // e.printStackTrace();
	// // }
	// //
	// // }
	// //
	// // }, false);
	// }
	//
	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// 
	// // super.onActivityResult(requestCode, resultCode, data);
	// super.onActivityResult(requestCode, resultCode, data);
	// if (resultCode != RESULT_OK) {
	// return;
	// }
	// switch (requestCode) {
	//
	// case 2:
	// if (resultCode == RESULT_OK) {
	// String barCode = data.getStringExtra("SCAN_RESULT");
	// // textView.setText(barCode);
	// userNameEdit.setText(barCode);
	//
	// } else {
	// // textViewBarCode.setText("");
	// }
	// break;
	//
	// case 3:
	// if (resultCode == Activity.RESULT_OK) {
	// String path = cameraTempFile.getAbsolutePath();
	// Utils.compressImage(path);
	// }
	// break;
	// }
	//
	// }
}
