package com.hermesgamesdk.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.QGProgressBar;
import com.mygson.gson.Gson;
import com.quickjoy.lib.jkhttp.HttpClient;
import com.quickjoy.lib.jkhttp.Response;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 根据HttpRequest发起网络请求 成功或失败通知到主线程 解析获取的json数据为泛型对象，并存入mDatas 提供其他类访问数据的接口
 */
public class DataManager {
    private HashMap<String, Object> mDatas;
    private ExecutorService mThreadPool;
    private static DataManager mDataManager;

    private final int RESPONSE = 1;
    private final int DOWANLOAD = 2;
    private final int EXCEPTION = 3;
    private final int DOWANLOAD_INIT = 5;
    private final int FAILED = 4;
    private int FailedCounts = 0;
    private int mFinished = 0;
    private Activity mContext;
    private DownloadUpdateListener mListener;
    private int mFailCode;
    public static final int PARESE_JSON_FAILED = -1;
    public static final int RESPONE_NULL = -2;
    public static final int REQUEST_FAILED = -3;

    public void init(Activity context) {
        this.mContext = context;
    }

    public void destroy() {
        mDataManager = null;
    }

    /**
     * 初始化mDatas，创建线程池
     */
    private DataManager() {
        if (mDatas == null)
            mDatas = new HashMap<String, Object>();
        if (mThreadPool == null)
            mThreadPool = Executors.newCachedThreadPool();
    }

    // 用于接受网络请求的异步通知，并在主线程解析数据
    // 解析json为泛型对象后存入mDtas后回调通知
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // 网络请求结束后处理消息
            if (msg.what == RESPONSE) {
                dealResponse(msg);
            }

            if (msg.what == DOWANLOAD) {
                DownloadHolder holder = (DownloadHolder) msg.obj;
                DownloadUpdateListener listener = holder.listener;

                if (holder.error != null) {
                    listener.onDownloadError(holder.error);
                } else {
                    listener.onUpdate(holder.progress, holder.downloadLength, holder.totalLength);
                }

            }
            if (msg.what == DOWANLOAD_INIT) {
                JSONObject jsonObject=(JSONObject)msg.obj;
                try {
                    download(jsonObject.getString("url"), jsonObject.getString("path"),null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (msg.what == EXCEPTION) {
                Object[] res = (Object[]) msg.obj;
                HttpRequest request = (HttpRequest) res[0];
                String[] key = (String[]) res[1];
                requestHttp(request, key);
            }
            if (msg.what == FAILED) {
                ((HttpRequest) msg.obj).onFailed(
                        40000,
                        mContext.getString(mContext.getResources().getIdentifier("toast_text_getInternet_error",
                                "string", mContext.getPackageName())) + "40000");
            }

        }
    };

    /**
     * 用于子线程和主线程数据传递
     */
    private class DataHolder {
        public HttpRequest request;
        public Response response;
        public Class bean;
        public String[] key;

    }


    /**
     * @param request
     * @param key     key.lenth()为0时不存取数据 ，大于0时key[1]作为键存入mDatas ，建议传入可变参数长度小于2
     */
    public <T> void requestHttp(final HttpRequest<T> request, final String... key) {
        // 网络请求时显示进度条
        if (mContext != null)
            // QGProgressBar.show(mContext);
            // 网络请求在子线程中执行
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    // 构建网络请求真正用到的HttpClient对象
                    HttpClient client = new HttpClient.Builder().build();
                    Response response = null;

                    try {
                        // 网络请求，此方法为阻塞方法
                        response = client.newCall(request.getRequest()).excute();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("hermesgame", "requestHttp Exception: mFailCode"+mFailCode+"  " + e.toString());


                        // 异常 or 超时 请求备用域名 或者 终止
				/*		if (request.getUrl().startsWith(Constant.HOST_RE)) {
							Message message = mHandler.obtainMessage();
							message.what = FAILED;
							message.obj = request;
							mHandler.sendMessage(message);
							return;
						}*/
                        if (request.getRequest().getUrl().contains("changePass")||request.getRequest().getUrl().contains("checkRealName")){
                            Log.e("hermesgame", "requestHttp changePass");
                            request.onSuccess((T) new String("ExSuccess"));

                            return;
                        }
                        if (FailedCounts < 5) {
                            FailedCounts += 1;
                            Message message = mHandler.obtainMessage();
                            message.what = EXCEPTION;

                            Object[] myObj = new Object[2];
                            myObj[0] = request;
                            myObj[1] = key;
                            message.obj = myObj;
                            mHandler.sendMessage(message);
                            return;
                        }
					/*	if (Constant.HOST != null && !"".equals(Constant.HOST)
								&& QGSdkUtils.isUrl(Constant.HOST)) {
							Message message = mHandler.obtainMessage();
							message.what = EXCEPTION;
							Object[] myObj = new Object[2];
							myObj[0] = request;
							myObj[1] = key;
							message.obj = myObj;
							mHandler.sendMessage(message);
							return;
						} */
                        else {
                            Message message = mHandler.obtainMessage();
                            message.what = FAILED;
                            message.obj = request;
                            mHandler.sendMessage(message);
                            return;
                        }
                    }
                    // 数据封装
                    FailedCounts = 0;
                    Message message = mHandler.obtainMessage();
                    DataHolder dataHolder = new DataHolder();
                    dataHolder.request = request;
                    dataHolder.response = response;
                    Class<T> bean = request.getClz();
                    dataHolder.bean = bean;
                    dataHolder.key = key;
                    message.what = RESPONSE;
                    message.obj = dataHolder;
                    // 返回网络请求结果后通知到主线程 由主线程处理
                    mHandler.sendMessage(message);
                }
            });

    }

    public static DataManager getInstance() {
        if (mDataManager == null)
            mDataManager = new DataManager();
        return mDataManager;
    }

    /**
     * @param key 根据存数据传入的键取出对象数据
     * @return
     */
    public Object getData(String key) {
	/*	if (mDatas.get(key)!=null&&!mDatas.get(key).equals("")){
			return mDatas.get(key);
		}else{
			if (key.equals("timestamp")){
				return System.currentTimeMillis();
			}else{
				return "";
			}
		}*/
        return mDatas.get(key);
    }

    /**
     * @param key
     * @param data 可以用于存入非网络返回的数据 用于数据传递
     */
    public void putData(String key, Object data) {
        mDatas.put(key, data);
    }

    /**
     * 清空mDatas数据 如注销和退出时调用
     */
    public void clear() {
        mDatas.clear();
    }

    /**
     * @param key 根据业务需要移除数据
     */
    public void remove(String key) {
        mDatas.remove(key);
    }

    public void dealResponse(Message msg) {

        QGProgressBar.hide();
        // dataHolder 获取异步通知传递的对象
        // request用于回调通知
        // response 获取网络请求结果
        // bean 获取网络请求传入的泛型类型
        // key为 httpRequest传入的可变参数
        DataHolder dataHolder = (DataHolder) msg.obj;
        HttpRequest request = dataHolder.request;

        Response response = dataHolder.response;
        Class bean = dataHolder.bean;
        String[] key = dataHolder.key;

        if (response == null) {
            mFailCode = RESPONE_NULL;
            Log.e("hermesgame", "RESPONE_NULL");
            request.onFailed(
                    mFailCode,
                    mContext.getString(mContext.getResources().getIdentifier("toast_text_getInternet_error", "string",
                            mContext.getPackageName())) + "  RESPONE_NULL");
            return;
        }

        JSONObject json;
        // 将response转换成JSON对象
        try {
            json = response.asJsonObject();
            Log.e("hermesgame", "response : " + json.toString());
            //存时间  用作防沉迷计时
            //Log.d("hermesgame",""+request.getUrl()+"  "+(response.getHeaders().getHeaderValue("timestamp").isEmpty()? System.currentTimeMillis()/1000:response.getHeaders().getHeaderValue("timestamp")));
            if (request.getUrl().contains("getUserInfo")) {
			/*	if (response.getHeaders().getHeaderValue("timestamp").isEmpty()){

				}else{

				}*/
                getInstance().putData("timestamp", response.getHeaders().getHeaderValue("timestamp").isEmpty() ? String.valueOf(System.currentTimeMillis() / 1000) : response.getHeaders().getHeaderValue("timestamp"));
                Log.e("hermesgameee", "" + getInstance().getData("timestamp"));
            }

            // 执行此处表示返回为json格式
        } catch (JSONException e) {
            e.printStackTrace();
            // 执行至此处表示返回为非json格式
            Log.e("hermesgame", "DataManager Erro: " + response);
            mFailCode = PARESE_JSON_FAILED;
            request.onFailed(mFailCode,
                    mContext.getString(QGSdkUtils.getResId(mContext, "R.string.toast_text_getInternet_error")) + "  JSONException");
            return;
        }
        if (response.isSuccessful()) {
            Gson gson = new Gson();
            String result = "";
            String data = "";
            // url不是以Constant.HOST开始，直接返回字符串
            if ("java.lang.String".equals(bean.getName()) && !request.getUrl().startsWith(Constant.HOST)) {
                request.onSuccess(json.toString());
                return;
            }
            try {
                result = json.getString("result");
                data = json.getString("data");
            } catch (Exception e) {
                e.printStackTrace();
                // 走到该异常说明，数据格式发生变化
            }

            // 返回服务端对请求的处理结果,"true"表示成功处理请求
            if ("true".equals(result)) {
                Object obj = data;
                // 当泛型对象为java.lang.String时返回data json字符串
                // 为其他泛型时解析为相应对象
                if (!"java.lang.String".equals(bean.getName())) {
                    try {
                        obj = gson.fromJson(data, bean);
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.onFailed(40000, "gson.fromJson erro");
                        return;
                    }

                }
                // 存取数据
                if (key.length >= 1) {
                    mDatas.put(key[0], obj);
                    if (key[0].equalsIgnoreCase(Constant.INIT_KEY)) {
                        QGSdkUtils.saveString(mContext, Constant.INIT_KEY, data);
                    }
                }

                // 成功回调通知
                request.onSuccess(obj);
            } else if ("false".equals(result)) {
                // "false"表示服务端处理请求失败
                String message = "";

                try {
                    String error = json.getString("error");
                    JSONObject jsonError = new JSONObject(error);
                    message = jsonError.getString("message");
                    mFailCode = jsonError.getInt("id");
                    if (mFailCode == 40040) {
                        Object obj = data;
                        if (!"java.lang.String".equals(bean.getName())) {
                            obj = gson.fromJson(data, bean);
                        }
                        // 存取数据
                        if (key.length >= 1) {

                            mDatas.put(key[0], obj);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 失败回调通知，message为error字段值
                request.onFailed(mFailCode, message);
            }

        } else {
            mFailCode = REQUEST_FAILED;
            request.onFailed(mFailCode, json.toString());
        }
    }



    public static abstract class DownloadUpdateListener {
        public abstract void onUpdate(int progress, int downloadLength, int totalLength);

        public abstract void onDownloadError(String message);

        public boolean beforeDownload(int size) {
            return true;
        }
    }

    private static class DownloadHolder {
        public String error;
        public DownloadUpdateListener listener;
        public int progress;
        public int downloadLength;
        public int totalLength;
    }

    public void download(final String urlStr, final String savePath, final DownloadUpdateListener listener) {
        mThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection httpURLConnection =null;
                    if (url.getProtocol().toUpperCase().equals("HTTPS")) {
                        trustAllHosts();
                        HttpsURLConnection https = (HttpsURLConnection) url
                                .openConnection();
                        https.setHostnameVerifier(DO_NOT_VERIFY);
                        httpURLConnection = https;
                    } else {
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                    }
                    //HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(30000);
                    httpURLConnection.setReadTimeout(30000);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setInstanceFollowRedirects(true);
                    File outputFile = new File(savePath);
                    if (!outputFile.getParentFile().exists()) {
                        outputFile.getParentFile().mkdirs();
                    }
                    int outputFileSum = 0;
                    if (outputFile.exists()){
                        outputFileSum=(int)outputFile.length();

                  if (!QGSdkUtils.getString(mContext,"conetentLength").equals("")){
                            if (outputFileSum==Integer.valueOf(QGSdkUtils.getString(mContext,"conetentLength"))){
                                DownloadHolder holder = new DownloadHolder();
                                holder.listener = listener;
                                int progress =100;
                                holder.progress = progress;
                                holder.downloadLength = (int)outputFileSum;
                                holder.totalLength =  (int)outputFileSum;
                                Message message = mHandler.obtainMessage();
                                message.what = DOWANLOAD;
                                message.obj = holder;
                                mHandler.sendMessage(message);
                                return;
                            }
                        }

                    }
                    if (!QGSdkUtils.getString(mContext,"conetentLength").equals("")){
                        outputFileSum=Integer.valueOf(QGSdkUtils.getString(mContext,"conetentLength"));
                    }
                    httpURLConnection.setRequestProperty("Range", "bytes=" + outputFile.length() + "-");
                    int response = httpURLConnection.getResponseCode();
                    Log.e("hermesgame","DataManger download getResponseCode:"+response);
                    if (response == HttpURLConnection.HTTP_OK||response==HttpURLConnection.HTTP_PARTIAL) {

                        int conetentLength = httpURLConnection.getContentLength()+(int)outputFileSum;
                        QGSdkUtils.saveString(mContext,"conetentLength",""+conetentLength);
                        Log.e("hermesgame","DataManger download conetentLength:"+conetentLength+" outputFileSum: "+outputFileSum);
                        if (listener.beforeDownload(conetentLength)) {

                            InputStream in = httpURLConnection.getInputStream();

                            byte[] bs = new byte[1024 * 1024];

                            int len = 0;

                            FileOutputStream out = new FileOutputStream(outputFile,true);

                            DownloadHolder holder = new DownloadHolder();
                            holder.listener = listener;
                            int downloadLength = (int)outputFileSum;
                            while ((len = in.read(bs)) != -1) {
                                downloadLength += len;
                                int progress = (int) ((downloadLength / (conetentLength * 1.0f)) * 100);
                                holder.progress = progress;
                                holder.downloadLength = downloadLength;
                                holder.totalLength = conetentLength;
                                Message message = mHandler.obtainMessage();
                                message.what = DOWANLOAD;
                                message.obj = holder;
                                mHandler.sendMessage(message);
                                out.write(bs, 0, len);
                            }
                            out.close();

                        } else {
                            httpURLConnection.disconnect();
                        }

                    }else {
                        throw new RuntimeException("网络连接错误");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = mHandler.obtainMessage();
                    message.what = DOWANLOAD;
                    DownloadHolder holder = new DownloadHolder();
                    holder.error = e.getMessage() == null ? "" : e.getMessage();
                    holder.listener = listener;
                    message.obj = holder;
                    mHandler.sendMessage(message);

                }
            }
        });
    }
    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
