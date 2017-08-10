package com.amassfreight.base.net;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.amassfreight.domain.BaseRequestData;
import com.amassfreight.domain.MobileError;
import com.amassfreight.utils.DateDeserializer;
import com.amassfreight.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * net api 处理
 *
 * @author U11001548
 */
public class NetworkHelper {
    private static String BASE_URL;// =
    // "http://192.168.48.194/freight/Mobile/FreightMobile.asmx/";
    // private static String BASE_URL_BINARY =
    // "http://192.168.48.194/freight/Mobile/FreightMobileHandler.ashx/";

    private static AsyncHttpClient httpClient = new AsyncHttpClient();
    private static NetworkHelper _NetworkHelper;

    public static NetworkHelper getInstance() {
        if (_NetworkHelper == null) {
            _NetworkHelper = new NetworkHelper();
            httpClient.setTimeout(20 * 1000);
            httpClient.setMaxRetriesAndTimeout(3, 60 * 1000);
        }
        return _NetworkHelper;
    }

    /**
     * 检查是否联网
     *
     * @param context
     * @return
     */
    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }


    /**
     * 持久化cookie
     *
     * @param url
     * @param context
     */
    public static void initUrl(String url, Context context) {
        BASE_URL = url;
        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        httpClient.setCookieStore(cookieStore);
    }

    /**
     * UI线程接受，交由用户自己处理
     */
    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            MessageHandlerObject msgObject = (MessageHandlerObject) msg.obj;
            if (msgObject != null) {
                msgObject.callHttpHandler();
            }
        }

    };

    /**
     * 封装响应处理，提供对外方法，形成接口回调
     *
     * @param <T>
     * @author U11001548
     */
    class MessageHandlerObject<T> {
        private static final int JSON_TYPE = 1;
        private static final int BINARY_TYPE = 2;
        private static final int ERR_TYPE = 3;

        private AmassHttpResponseHandler<T> httpHandler;
        private T response;
        private boolean success;
        private int statusCode;
        private Header[] headers;
        private String responseBody;
        private Throwable e;
        private MobileError err;
        private byte[] binaryData;
        private int responseType;

        public MessageHandlerObject(AmassHttpResponseHandler<T> httpHandler,
                                    T response) {
            this.responseType = JSON_TYPE;
            this.success = true;
            this.httpHandler = httpHandler;
            this.response = response;
        }

        public MessageHandlerObject(AmassHttpResponseHandler<T> httpHandler,
                                    int statusCode, Header[] headers, String responseBody,
                                    Throwable e) {
            this.responseType = JSON_TYPE;
            this.success = false;
            this.httpHandler = httpHandler;
            this.statusCode = statusCode;
            this.e = e;
            this.headers = headers;
            this.responseBody = responseBody;
        }

        public MessageHandlerObject(AmassHttpResponseHandler<T> httpHandler,
                                    int statusCode, Header[] headers, byte[] binaryData, Throwable e) {
            this.responseType = BINARY_TYPE;
            this.success = false;
            this.e = e;
            this.httpHandler = httpHandler;
            this.binaryData = binaryData;
        }

        public MessageHandlerObject(AmassHttpResponseHandler<T> httpHandler,
                                    int statusCode, Header[] headers, byte[] binaryData) {
            this.responseType = BINARY_TYPE;
            this.success = true;
            this.httpHandler = httpHandler;
            this.binaryData = binaryData;
        }

        public MessageHandlerObject(AmassHttpResponseHandler<T> httpHandler,
                                    MobileError err) {
            this.success = false;
            this.httpHandler = httpHandler;
            this.err = err;
            this.responseType = ERR_TYPE;
        }

        public void callHttpHandler() {
            if (httpHandler == null) {
                return;
            }
            if (success) {
                if (this.responseType == JSON_TYPE) {
                    httpHandler.OnSuccess(response);
                } else {
                    httpHandler.OnSuccess(binaryData);
                }
            } else {
                switch (this.responseType) {
                    case JSON_TYPE:
                        httpHandler.onFailure(statusCode, headers, responseBody, e);
                        break;

                    case BINARY_TYPE:
                        httpHandler.onFailure(statusCode, headers, binaryData, e);
                        break;

                    case ERR_TYPE:
                        httpHandler.onErrMsg(this.err);
                        break;
                }
            }
        }

    }

    //################################# net处理   ###########################################

    /**
     * post发送返回list集合
     *
     * @param context
     * @param methodUrl
     * @param requestData
     * @param responseType
     * @param httpHandler
     * @param showDialog
     */
    public void postData(Context context, String methodUrl,
                         BaseRequestData requestData, Type responseType,
                         AmassHttpResponseHandler httpHandler, boolean showDialog) {
        if (!isNetworkConnected(context)) {
            if (showDialog) {
                Toast.makeText(context, "网络未连接。", Toast.LENGTH_LONG).show();
            }
            return;
        }
        Dialog dlg = null;
        if (showDialog) {
            dlg = Utils.createConnectDialg(context, "Connect...");
            dlg.show();
        }
        RequestParams params = new RequestParams();

        if (requestData != null) {
            requestData.setRequestData(params);
        }
        httpClient.post(context, BASE_URL + methodUrl, params,
                new AmassJsonHttpResponseHandler(context, responseType, dlg,
                        httpHandler));
    }

    public void postFilesData(Context context, String methodUrl, String funId,
                              RequestParams params, Type responseType,
                              AmassHttpResponseHandler httpHandler, boolean showDialog) {
        if (!isNetworkConnected(context)) {
            //if(showDialog){
            Toast.makeText(context, "网络未连接。", Toast.LENGTH_LONG).show();
            //}
            return;
        }

        Dialog dlg = null;
        if (showDialog) {
            dlg = Utils.createConnectDialg(context, "Connect...");
            dlg.show();
        }

        params.put("FUN_ID", funId);
        httpClient.post(context, BASE_URL + methodUrl, params,
                new AmassJsonHttpResponseHandler(context, responseType, dlg,
                        httpHandler));
    }

    /**
     * post 请求返回二进制数据
     *
     * @param context
     * @param methodUrl
     * @param requestData
     * @param httpHandler
     * @param showDialog
     */
    public void postDataReturnBinary(Context context, String methodUrl,
                                     BaseRequestData requestData, // BaseResponseData responseData,
                                     AmassHttpResponseHandler httpHandler, boolean showDialog) {
        if (!isNetworkConnected(context)) {
            if (showDialog) {
                Toast.makeText(context, "网络未连接。", Toast.LENGTH_LONG).show();
            }
            return;
        }

        Dialog dlg = null;
        if (showDialog) {
            dlg = Utils.createConnectDialg(context, "Connect...");
            dlg.show();
        }
        RequestParams params = new RequestParams();

        if (requestData != null) {
            requestData.setRequestData(params);
        }
        // params.put("methodUrl", methodUrl);
        httpClient.post(context, BASE_URL + methodUrl, params,
                new AmassBinaryHttpResponseHandler(context, dlg, httpHandler));
    }

    /**
     * post发送json数据返回二进制流
     *
     * @param context
     * @param methodUrl
     * @param requestData
     * @param httpHandler
     * @param showDialog
     */
    public void postJsonReturnBinary(Context context, String methodUrl,
                                     Map<String, Object> requestData,
                                     AmassHttpResponseHandler httpHandler, boolean showDialog) {
        if (!isNetworkConnected(context)) {
            if (showDialog) {
                Toast.makeText(context, "网络未连接。", Toast.LENGTH_LONG).show();
            }
            return;
        }

        Dialog dlg = null;
        if (showDialog) {
            dlg = Utils.createConnectDialg(context, "Connect...");
            dlg.show();
        }
        // JSONObject params = new JSONObject();
        // try {
        // requestData.setRequestData(params);
        // } catch (JSONException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        StringEntity entity;
        try {
            Gson gson = new Gson();
            entity = new StringEntity(gson.toJson(requestData), "utf-8");
            httpClient.post(context, BASE_URL + methodUrl, entity,
                    "application/json", new AmassBinaryHttpResponseHandler(
                            context, dlg, httpHandler));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // httpClient.post(BASE_URL + methodUrl, params,
        // new AmassJsonHttpResponseHandler(responseData, progressDlg,
        // httpHandler));
    }

    /**
     * post请求接口无返回值
     *
     * @param context
     * @param methodUrl
     * @param httpHandler
     */
    public void postNoData(Context context, String methodUrl,
                           ResponseHandlerInterface httpHandler) {

        httpClient.post(context, BASE_URL + methodUrl, null, httpHandler);
    }


    /**
     * post发送json数据返回object
     *
     * @param context
     * @param methodUrl
     * @param requestData
     * @param responseType
     * @param httpHandler
     * @param showDialog
     */
    public void postJsonData(Context context, String methodUrl,
                             Map<String, Object> requestData, Type responseType,
                             AmassHttpResponseHandler httpHandler, boolean showDialog) {
        if (!isNetworkConnected(context)) {
            //if(showDialog){
            Toast.makeText(context, "网络未连接。", Toast.LENGTH_LONG).show();
            //}
            return;
        }

        Dialog dlg = null;
        if (showDialog) {
            dlg = Utils.createConnectDialg(context, "Connect...");
            dlg.show();
        }
        // JSONObject params = new JSONObject();
        // try {
        // requestData.setRequestData(params);
        // } catch (JSONException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        StringEntity entity;
        try {
            Gson gson = new Gson();
            entity = new StringEntity(gson.toJson(requestData), "utf-8");
            httpClient.post(context, BASE_URL + methodUrl, entity,
                    "application/json",
                    new AmassJsonHttpResponseHandler<Class<?>>(context,
                            responseType, dlg, httpHandler));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // httpClient.post(BASE_URL + methodUrl, params,
        // new AmassJsonHttpResponseHandler(responseData, progressDlg,
        // httpHandler));
    }


    //########################## 响应HttpResponseHandler，进行响应结果的发送  ################################

    class AmassJsonHttpResponseHandler<T> extends TextHttpResponseHandler {
        private Type responseType;
        private Dialog dlg;
        private Context context;
        private AmassHttpResponseHandler<T> httpHandler;

        // public AmassJsonHttpResponseHandler(String encoding,
        // BaseResponseData responseData, Dialog dlg) {
        // super(encoding);
        // this.responseData = responseData;
        // this.dlg = dlg;
        // }

        public AmassJsonHttpResponseHandler(Context context, Type responseType,
                                            Dialog dlg, AmassHttpResponseHandler<T> httpHandler) {
            super();
            this.context = context;
            this.responseType = responseType;
            this.dlg = dlg;
            this.httpHandler = httpHandler;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String response) {
            T responseData = null;
            // super.onSuccess(statusCode, headers, response);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class,
                    new DateDeserializer()).create();

            JsonReader reader = new JsonReader(new StringReader(response));
            reader.setLenient(true);
            try {
                MobileError err = gson.fromJson(reader, MobileError.class);
                switch (err.getErrCode()) {
                    case 700:
                    case 400:
                    case 701:
                        // self check error
                        if (httpHandler != null) {
                            Message msg = new Message();
                            msg.obj = new MessageHandlerObject<T>(httpHandler, err);
                            handler.sendMessage(msg);
                        }
                        if (dlg != null) {
                            Utils.showAlertDialog(context, err.getErrMsg());
                        } else {
                            // Toast.makeText(context, err.getErrMsg(),
                            // Toast.LENGTH_SHORT).show();
                        }
                        return;

                    case 401:
                        if (httpHandler != null) {
                            Message msg = new Message();
                            msg.obj = new MessageHandlerObject<T>(httpHandler, err);
                            handler.sendMessage(msg);
                        }
                        Utils.showAlertDialogRestart(context, err.getErrMsg());
                        return;
                    //

                    // default:
                    // if (httpHandler != null) {
                    // Message msg = new Message();
                    // msg.obj = new MessageHandlerObject<T>(httpHandler, err);
                    // handler.sendMessage(msg);
                    // }
                    // break;
                }
            } catch (Exception ex) {

            }
            if (responseType != null) {
                reader = new JsonReader(new StringReader(response));
                reader.setLenient(true);
                responseData = gson.fromJson(reader, responseType);
            }
            if (httpHandler != null) {
                Message msg = new Message();
                msg.obj = new MessageHandlerObject<T>(httpHandler, responseData);
                handler.sendMessage(msg);
            }

        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            super.onFinish();
            // progressLay.setVisibility(View.GONE);
            // v.setVisibility(View.VISIBLE);
            if (dlg != null) {
                dlg.dismiss();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseBody, Throwable e) {
            // TODO Auto-generated method stub
            // super.onFailure(statusCode, headers, responseBody, e);
            String errMsg;
            if (e instanceof SocketTimeoutException) {
                errMsg = "网络连接超时，请重新连接。";
            } else {
                errMsg = String.format("%d : %s", statusCode, e.getMessage());
            }
            if (dlg != null) {
                Utils.showAlertDialog(context, errMsg);
            }
            // Utils.showAlertDialogRestart(context, errMsg);
            // // textView.setText(id + e.getMessage());
            // // restartApp(context);
            if (httpHandler != null) {
                Message msg = new Message();
                msg.obj = new MessageHandlerObject<T>(httpHandler, statusCode,
                        headers, responseBody, e);
                handler.sendMessage(msg);
                // httpHandler.onFailure(statusCode, headers, responseBody, e);
            }
        }
    }

    class AmassBinaryHttpResponseHandler extends TextHttpResponseHandler {
        // private BaseResponseData responseData;
        private Dialog dlg;
        private AmassHttpResponseHandler<Type> httpHandler;
        private Context context;

        public AmassBinaryHttpResponseHandler(Context context, Dialog dlg,
                                              AmassHttpResponseHandler<Type> httpHandler) {
            super();// new String[] { "application/octet-stream" });
            // this.responseData = responseData;
            this.context = context;
            this.dlg = dlg;
            this.httpHandler = httpHandler;
        }

        // @Override
        // public void onFailure(Throwable e, JSONArray errorResponse) {
        // // TODO Auto-generated method stub
        // super.onFailure(e, errorResponse);
        // button.setText(id+ errorResponse.toString());
        // }

        // @Override
        // public void onFailure(String responseBody, Throwable error) {
        // // TODO Auto-generated method stub
        // super.onFailure(responseBody, error);
        // button.setText(id+ error.getMessage());
        // // progressdialog.dismiss();
        //
        // }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              byte[] binaryData) {
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class,
                    new DateDeserializer()).create();
            String s = new String(binaryData);
            JsonReader reader = new JsonReader(new StringReader(s));
            reader.setLenient(true);
            try {
                MobileError err = gson.fromJson(reader, MobileError.class);
                switch (err.getErrCode()) {
                    case 700:
                    case 400:
                    case 701:
                        // self check error
                        if (dlg != null) {
                            Utils.showAlertDialog(context, err.getErrMsg());
                        } else {
                            // Toast.makeText(context, err.getErrMsg(),
                            // Toast.LENGTH_SHORT).show();
                        }
                        return;

                    case 401:
                        Utils.showAlertDialogRestart(context, err.getErrMsg());
                        return;
                    //
                }
            } catch (Exception ex) {

            }
            if (httpHandler != null) {
                Message msg = new Message();

                msg.obj = new MessageHandlerObject(httpHandler, statusCode,
                        headers, binaryData);
                handler.sendMessage(msg);
                // httpHandler.OnSuccess(responseData);
            }
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            super.onFinish();
            // progressLay.setVisibility(View.GONE);
            // v.setVisibility(View.VISIBLE);
            if (dlg != null) {
                dlg.dismiss();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              byte[] binaryData, Throwable error) {
            // Utils.restartApp(context);
            super.onFailure(statusCode, headers, binaryData, error);
            String errMsg;
            if (error instanceof SocketTimeoutException) {
                errMsg = "网络连接超时，请重新连接。";
            } else {
                errMsg = String.format("%d : %s", statusCode,
                        error.getMessage());
            }
            if (dlg != null) {
                Utils.showAlertDialog(context, errMsg);
            }
            // Utils.showAlertDialogRestart(context, errMsg);
            // // textView.setText(id + e.getMessage());
            // if (httpHandler != null) {
            // Message msg = new Message();
            // msg.obj = new MessageHandlerObject(httpHandler, statusCode,
            // headers, binaryData, error);
            // handler.sendMessage(msg);
            // // httpHandler.onFailure(statusCode, headers, responseBody, e);
            // }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            // TODO Auto-generated method stub

        }
    }
}
