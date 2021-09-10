package com.hermesgamesdk.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.bytedance.hume.readapk.HumeSDK;
import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.RangersAppLog;
import com.hermesgamesdk.callback.QGCallBack;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.entity.QGRoleInfo;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.skin.manager.listener.ILoaderListener;
import com.hermesgamesdk.skin.manager.loader.SkinManager;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.aidl.GetToken;
import com.hermesgamesdk.manager.DataManager.DownloadUpdateListener;
import com.quickjoy.adplus.ADP;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * cp调用init接口时的业务处理 包括读取初始化Channel_Id
 */
public class InitManager {

    public static InitManager initManager;

    private String product_code = "";
    private String appId = "";

    public Activity mActivity = null;

    private InitManager() {
    }

    public void destroy() {
        initManager = null;
    }

    public static InitManager getInstance() {
        if (initManager == null)
            initManager = new InitManager();
        return initManager;
    }

    /**
     * 登录框是否使用CP配置的logo
     */
    public boolean useCPLogo = false;

    /**
     * 悬浮窗图标是否使用CP配置的logo
     */
    public boolean useCPFloatLogo = false;

    /**
     * 登录界面是否展示盒子授权登录
     */
    public boolean useGameboxLogo = false;


    /**
     * 登录界面是否展示默认头像
     */
    public boolean useAvatar = false;


    /**
     * 下载下来的头像logo的本地存放地址
     */
    public String cpuseAvatarPath = "";
    /**
     * 下载下来的logo的本地存放地址
     */
    public String cpLogPath = "";
    /**
     * 下载下来的悬浮窗logo的本地存放地址
     */
    public String cpFloatLogPath = "";
    /**
     * 下载下来的盒子授权登录logo的本地存放地址
     */
    public String cpGameBoxPath = "";


    /**
     * @param activity     cp传入
     * @param product_id   cp传入 用于区分应用
     * @param initCallback cp传入 初始化成功失败后回调
     */
    public void init(final Activity activity, String appId, final String product_id, final QGCallBack initCallback) {
        mActivity = activity;
        product_code = product_id;
        this.appId = appId;
        try {
            Constant.reSetHost(activity);
            initData(activity, product_id);
            DataManager.getInstance().init(activity);
            getExConfig(activity);

            HttpRequest<InitData> request = new HttpRequest<InitData>() {
                @Override
                public void onSuccess(final InitData bean) {
                    QGConfig.init(activity);
                    if (QGConfig.isSupportAD()) {
                         ADP.getInstance().init(activity, QGConfig.getAdAppId());
                        ADP.getInstance().active("0", "0","0");
                    }

                    if (QGConfig.getServiceState() == 2) {
                        lauchCustomService(activity);
                    }
                    QGFloatViewManager.getInstance().init(activity);
                    initCallback.onSuccess();
                    downloadLoginLogo(activity, bean.getProductconfig().getLogo());
                    downloadFloatLogo(activity, bean.getProductconfig().getFloatLogo());
                    if (bean.getAppAuthInfo() != null && !bean.getAppAuthInfo().getAppLogo().equals("")) {
                        downGameBoxLogo(activity, bean.getAppAuthInfo().getAppLogo());
                    }
                    if (bean.getAppAuthInfo() != null && !bean.getAppAuthInfo().getDefaultAvatar().equals("")) {
                        downDefaultAvatar(activity, bean.getAppAuthInfo().getDefaultAvatar());
                    }

                    if (bean.getAppAuthInfo() != null && !bean.getAppAuthInfo().getTheme().equals("")) {
                        loadSkin(bean.getAppAuthInfo().getTheme());
                    }
                    gainNotice(activity);
                    bindTokenService(activity);
                    dealWithWechatPlugin(activity, bean);
                }

                @Override
                public void onFailed(int id, String message) {
                    QGSdkUtils.showToast(activity, message);
                    initCallback.onFailed(message);
                }
            }.addParameter(getInitParameter(activity)).post().setUrl(Constant.HOST + Constant.INIT).addHeader("Content-Type", "multipart/form-data");
            // QGProgressBar.disable();

            Log.d("hermesgame", "init request");
            DataManager.getInstance().requestHttp(request, Constant.INIT_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("hermesgame", "init request Exception:" + e.toString());
            initCallback.onFailed(e.getMessage());
        }


    }

    /**
     * 用于重新加载配置;
     *
     * @param activity     cp传入
     * @param initCallback cp传入 初始化成功失败后回调
     */
    public void reInit(final Activity activity, final QGCallBack initCallback) {
        Constant.PRODUCT_ID = product_code;
        Constant.reSetHost(activity);
        initData(activity, product_code);

        DataManager.getInstance().init(activity);
        HttpRequest<InitData> request = new HttpRequest<InitData>() {
            @Override
            public void onSuccess(final InitData bean) {
                /*
                 * QGConfig.init(activity); if (QGConfig.isSupportAD()) { //
                 * ADP.getInstance().init(activity, QGConfig.getAdAppId()); } if
                 * (QGConfig.getServiceState() == 2) {
                 * lauchCustomService(activity); }
                 * QGFloatViewManager.getInstance().init(activity);
                 *
                 * downloadLogo(activity, bean.getProductconfig().getLogo());
                 * gainNotice(activity);
                 */
                initCallback.onSuccess();
            }

            @Override
            public void onFailed(int id, String message) {
                QGSdkUtils.showToast(activity, message);
                initCallback.onFailed(message);
            }
        }.addParameter(getInitParameter(activity)).post().setUrl(Constant.HOST + Constant.INIT);
        // QGProgressBar.disable();
        DataManager.getInstance().requestHttp(request, Constant.INIT_KEY);
    }

    public void gainNotice(Activity activity) {
        String parameter = new QGParameter(activity).create();

        HttpRequest<String> request = new HttpRequest<String>() {
            @Override
            public void onSuccess(String info) {
                try {
                    JSONObject notice = new JSONObject(info);
                    Constant.noticeShowNode = notice.getInt("showNode");
                    Constant.noticeContent = notice.getString("content");
                    Constant.noticeTitle = notice.getString("title");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(int id, String message) {

            }
        }.addParameter(parameter).post().setUrl(Constant.HOST + Constant.NOTICE);
        DataManager.getInstance().requestHttp(request);

    }

    /**
     * @param context
     * @param product_id 初始化数据 赋值到Constant常量类以备后续调用
     */
    private void initData(Context context, String product_id) {

        Constant.PRODUCT_ID = product_id;
        // 获取应用的版本号和版本名称
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            if (info != null) {
                Constant.VERSION_CODE = info.versionCode;
                Constant.VERSION_NAME = info.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 获取channel_id
        Constant.CHANNEL_ID = initChannelID(context);

        Constant.BYTEDANCE_ID = getByteDanceCpsCode(context);

        RangersAppLog.init(context, appId, Constant.BYTEDANCE_ID);

    }

    public String getByteDanceCpsCode(Context context) {
        String channel = HumeSDK.getChannel(context);
        if (channel != null && !channel.equals("")) {
            Log.d("hermesgame", "getByteDanceCpsCode  is ： " + channel);
            return channel;
        }
        return null;
    }

    /**
     * @param context
     * @return 有效channel_id: 不为 "" null(忽略大小写) 0 default的字符串
     */
    public String initChannelID(Context context) {
        String cfgChannelId = null;

        try {
            // 读取assets目录下channel_id.txt文件
            InputStream open = context.getAssets().open("hermesgame_sdk/channel_id.txt");

            cfgChannelId = readFromStream(open);
        } catch (IOException e) {
            cfgChannelId = "nochannel";
            Log.d("hermesgame", "channel_id.txt is not exist");
        }
        String packageChannelId = null;
        // 有效channel_id在设备中的存储路径
        String channelFilePath = Environment.getExternalStorageDirectory() + File.separator + "hermesgame"
                + File.separator + context.getPackageName() + File.separator + "cid.qg";
        try {
            InputStream packIn = new FileInputStream(channelFilePath);
            // 读取设备中的channel_id
            packageChannelId = readFromStream(packIn);
        } catch (FileNotFoundException e) {
            packageChannelId = null;
            Log.e("hermesgame", channelFilePath + " is not exist");
        }
        // 1 如果assets文件和设备中获取的channel_id无效则 channel_id="default"
        // 2 如果assets文件channel_id无效设备中获取的channel_id有效则返回设备中有效的channe_id
        // 3
        // 如果assets中channe_id有效则返回assets中的channel_id，并用assets的channel_id覆盖设备中的channel_id
        if (checkChannelId(cfgChannelId)) {
            if (TextUtils.isEmpty(packageChannelId))
                return "default";
            else
                return packageChannelId;
        } else {
            saveToChannelIdToPackage(cfgChannelId, channelFilePath);
            return cfgChannelId.trim();
        }
    }

    /**
     * @param channelId
     * @return 判断channe_id是否有效 true无效 ，false有效
     */
    private boolean checkChannelId(String channelId) {
        return TextUtils.isEmpty(channelId) || "default".equalsIgnoreCase(channelId.trim())
                || "null".equalsIgnoreCase(channelId.trim()) || "".equals(channelId.trim())
                || "0".equalsIgnoreCase(channelId.trim());
    }

    /**
     * @param cfgChannelId
     * @param path         将assets中有效的channel_id写入设备文件中
     */
    private void saveToChannelIdToPackage(String cfgChannelId, String path) {
        try {
            File file = new File(path);
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if (file.exists())
                file.delete();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(cfgChannelId.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取特殊配置
    public void getExConfig(Context ctx) {
        HashMap<String, Object> mExtraConfig = new HashMap<String, Object>();
        String[] configs;
        String configString = "";
        String line = "";
        try {
            InputStream open = ctx.getAssets().open("hermesgame_sdk/sdkconfig.txt");
            InputStreamReader isr = new InputStreamReader(open);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                configString += line + "\n";
            }

            configs = configString.split("\n");
            if (!configs[0].isEmpty()) {
                for (int i = 0; i < configs.length; i++) {
                    Log.d("hermesgame", "XXX:" + i);
                    String key = configs[i].split("=")[0];
                    String value = configs[i].split("=")[1];
                    Log.e("hermesgame", "sdkconfig key ：" + key + "   value:" + value);
                    mExtraConfig.put(key, value);
                }
            }
            if (mExtraConfig != null && mExtraConfig.size() != 0) {
                QGConfig.setmExtraConfig(mExtraConfig);
            }
            open.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("hermesgame", "sdkconfig.txt is not exist: " + e.toString());
            return;
        }
    }

    // 获取ip 及 备用ip
    public String readConfig(Context ctx) {

        String config = "";
        String line = "";
        int i = 0;
        try {
            InputStream open = ctx.getAssets().open("hermesgame_sdk/sdkconfig.txt");
            InputStreamReader isr = new InputStreamReader(open);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                config += line;
            }
            open.close();
            return config;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("hermesgame", "sdkconfig.txt is not exist");
            return null;
        }
    }

    public String readFromStream(InputStream in) {
        StringBuffer sb = new StringBuffer();
        byte[] buf = new byte[100];
        int len;
        try {
            while ((len = in.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, "utf-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("hermesgame", " readFromStream: " + sb.toString());
        return sb.toString();
    }

    // 获取ip 及 备用ip
    public String[] getIPS(Context ctx) {
        String[] ips;
        String ipString = "";
        String line = "";
        int i = 0;
        try {
            InputStream open = ctx.getAssets().open("hermesgame_sdk/qg_ipconfig.txt");
            InputStreamReader isr = new InputStreamReader(open);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                ipString += line + "\n";
            }
            ips = ipString.split("\n");
            open.close();
            return ips;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("hermesgame", "qg_ipconfig.txt is not exist");
            return null;
        }
    }

    /**
     * @param ctx
     * @return 初始化过程中需要上传信息用于统计 构建网络请求的数据格式 字符串
     */
    public static String getInitParameter(Context ctx) {
        QGParameter parameter = new QGParameter(ctx);
        parameter.addParameter("deviceName", Build.MODEL);// 移动设备名称
        String imei = QGSdkUtils.getImei(ctx);
        // String imei = QGSdkUtils.deviceUniqueId(ctx);
        // 是否为移动设备 0为非移动设备 ，1为移动设备
        if (TextUtils.isEmpty(imei)) {
            parameter.addParameter("ismobiledevice", "0");
        } else {
            parameter.addParameter("ismobiledevice", "1");
        }
        int[] cfg = QGSdkUtils.getScreenCfg(ctx);
        parameter.addParameter("isjailbroken", "0")
                // 是否越狱
                .addParameter("pushToken", "")
                // imei
                .addParameter("imei", imei)
                // 国际移动设备身份码
                .addParameter("flashversion", "")
                // 获取flash version
                .addParameter("countryCode", QGSdkUtils.getCountryCode(ctx))
                // 获取国家代码
                .addParameter("bluetoothMac", QGSdkUtils.getBluetoothAddress(ctx))// 蓝牙设备的mac地址
                .addParameter("osVersion", Build.VERSION.SDK_INT + "")// android版本
                .addParameter("javasupport", "1") // 是否支持javascript脚本 1为支持
                .addParameter("osName", "android")// 操作系统名称
                .addParameter("wifimac", QGSdkUtils.getWifiAddress(ctx)) // wifi连接是的wifi地址
                .addParameter("defaultbrowser", "")// 使用的默认浏览器
                .addParameter("osLanguage", QGSdkUtils.getLanguate())// 操作系统语言
                .addParameter("screenWidth", cfg[0] + "")// 屏幕宽
                .addParameter("screenHeight", cfg[1] + "")// 屏幕高
                .addParameter("dpi", cfg[2] + "")// 屏幕像素密度（DPI是指每英寸的像素）
                .addParameter("imsi", QGSdkUtils.getImsi(ctx))// 移动用户识别码
                .addParameter("netType", QGSdkUtils.getNetworkType(ctx) + "")// 连接网络时的网络类型
                .addParameter("isEmt", QGSdkUtils.getIsEumlator((Activity) ctx) ? "1" : "0")
                .addParameter("longitude", "0").addParameter("latitude", "0");// 经度(地理上的经度纬度)

        return parameter.create();
    }

    /**
     * 启动客服，即打开客服界面
     */
    public void showCustomService(Context ctx) {

        QGRoleInfo roleInfo = LoginManager.getInstance().getRoleInfo();

        try {
            Class<?> beanClazz = Class.forName("com.qk.plugin.customservice.CustomServiceBean");
            Object bean = beanClazz.newInstance();
            Class<?> serviceClazz = Class.forName("com.qk.plugin.customservice.QKCustomService");
            QGUserInfo userInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
            if (userInfo != null) {
                invoke(bean, "setUid", false, userInfo.getUserdata().getUid());
                invoke(bean, "setUsername", false, userInfo.getUserdata().getUsername());
            }
            invoke(bean, "setProductCode", false, Constant.PRODUCT_CODE);
            invoke(bean, "setRoleBalance", false, roleInfo.getBalance());
            invoke(bean, "setRoleId", false, roleInfo.getRoleId());
            invoke(bean, "setRoleName", false, roleInfo.getRoleName());
            invoke(bean, "setRolePartyName", false, roleInfo.getPartyName());
            invoke(bean, "setRoleServerName", false, roleInfo.getServerName());
            invoke(bean, "setVipLevel", false, roleInfo.getVipLevel());
            Object service = invoke(serviceClazz, "getInstance", true);

            invoke(service, "showCustomService", false, ctx, bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object invoke(Object instant, String methodName, boolean isStatic, Object... parameters) {

        try {
            Class<?>[] parametersType = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] instanceof Activity) {
                    parametersType[i] = Activity.class;
                } else if (parameters[i] instanceof String) {
                    parametersType[i] = String.class;
                } else {
                    parametersType[i] = parameters[i].getClass();
                }

            }
            Class<?> clazz = null;
            if (!(instant instanceof Class<?>)) {
                clazz = instant.getClass();
            } else {
                clazz = (Class<?>) instant;
            }
            Method method = clazz.getDeclaredMethod(methodName, parametersType);
            Object obj = isStatic ? null : instant;
            return method.invoke(obj, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param activity 启动客服
     */
    public void lauchCustomService(final Activity activity) {
        if (QGConfig.isSupportCustomService()) {

            new Thread() {
                public void run() {
                    try {
                        Class<?> beanClazz = Class.forName("com.qk.plugin.customservice.CustomServiceBean");
                        Object bean = beanClazz.newInstance();

                        invoke(bean, "setProductCode", false, Constant.PRODUCT_CODE);

                        Class<?> serviceClazz = Class.forName("com.qk.plugin.customservice.QKCustomService");
                        Object service = invoke(serviceClazz, "getInstance", true);
                        invoke(service, "launch", false, activity, bean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                ;
            }.start();
        }

    }

    /**
     * 下载CP后台配置的登录logo图片
     */
    public void downloadLoginLogo(Context context, String strUrl) {

        if (TextUtils.isEmpty(strUrl)) {
            Log.d("hermesgame", "CP没有配置登录logo图片");
            useCPLogo = false;
            return;
        }
        String fileName = strUrl.substring(strUrl.lastIndexOf("/") + 1);
        File filesDir = context.getFilesDir();
        File mLogoFile = new File(filesDir, fileName);
        String path = mLogoFile.getAbsolutePath();
        cpLogPath = path;

        if (mLogoFile.exists()) {
            Log.d("qg", "图片已存在");
            useCPLogo = true;
            return;
        }

        DataManager.getInstance().download(strUrl, path, new DownloadUpdateListener() {
            @Override
            public void onUpdate(int progress, int downloadLength, int totalLength) {
                useCPLogo = true;
            }

            @Override
            public void onDownloadError(String message) {
                useCPLogo = false;
            }
        });
    }

    /**
     * 下载CP后台配置的logo图片
     */
    public void downloadFloatLogo(Context context, String strUrl) {

        if (TextUtils.isEmpty(strUrl)) {
            Log.d("hermesgame", "CP没有配置悬浮窗logo图片");
            useCPFloatLogo = false;
            return;
        }
        String fileName = strUrl.substring(strUrl.lastIndexOf("/") + 1);
        File filesDir = context.getFilesDir();
        File mLogoFile = new File(filesDir, fileName);
        String path = mLogoFile.getAbsolutePath();
        cpFloatLogPath = path;

        if (mLogoFile.exists()) {
            Log.d("qg", "图片已存在");
            useCPFloatLogo = true;
            return;
        }

        DataManager.getInstance().download(strUrl, path, new DownloadUpdateListener() {
            @Override
            public void onUpdate(int progress, int downloadLength, int totalLength) {
                useCPFloatLogo = true;
            }

            @Override
            public void onDownloadError(String message) {
                useCPFloatLogo = false;
            }
        });
    }

    /**
     * 下载CP后台配置的盒子登录logo图片
     */
    public void downDefaultAvatar(Context context, String strUrl) {

        if (TextUtils.isEmpty(strUrl)) {
            Log.d("hermesgame", "CP没有配置默认头像logo图片");
            useAvatar = false;
            return;
        }
        String fileName = strUrl.substring(strUrl.lastIndexOf("/") + 1);
        File filesDir = context.getFilesDir();
        File mLogoFile = new File(filesDir, fileName);
        String path = mLogoFile.getAbsolutePath();
        cpuseAvatarPath = path;
        if (mLogoFile.exists()) {
            Log.d("qg", "图片已存在");
            useAvatar = true;
            return;
        }

        DataManager.getInstance().download(strUrl, path, new DownloadUpdateListener() {
            @Override
            public void onUpdate(int progress, int downloadLength, int totalLength) {
                useAvatar = true;
            }

            @Override
            public void onDownloadError(String message) {
                useAvatar = false;
            }
        });
    }

    /**
     * 下载CP后台配置的盒子登录logo图片
     */
    public void downGameBoxLogo(Context context, String strUrl) {

        if (TextUtils.isEmpty(strUrl)) {
            Log.d("hermesgame", "CP没有配置盒子logo图片");
            useGameboxLogo = false;
            return;
        }
        String fileName = strUrl.substring(strUrl.lastIndexOf("/") + 1);
        File filesDir = context.getFilesDir();
        File mLogoFile = new File(filesDir, fileName);
        String path = mLogoFile.getAbsolutePath();
        cpGameBoxPath = path;
        if (mLogoFile.exists()) {
            Log.d("hermesgame", "图片已存在");
            useGameboxLogo = true;
            return;
        }

        DataManager.getInstance().download(strUrl, path, new DownloadUpdateListener() {
            @Override
            public void onUpdate(int progress, int downloadLength, int totalLength) {
                useGameboxLogo = true;
                Log.d("hermesgame", "图片已存在1");
            }

            @Override
            public void onDownloadError(String message) {
                useGameboxLogo = false;
                Log.d("hermesgame", "图片已存在2" + message);
            }
        });
    }

    //获取游戏盒子Service里面的token
    public void bindTokenService(final Context context) {
        InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LoginManager.getInstance().getToken = GetToken.Stub.asInterface(service);
                try {
                    final JSONObject jsonObject = new JSONObject(LoginManager.getInstance().getToken.getToken());
                    if (!jsonObject.getString("authToken").equals("") && !jsonObject.getString("username").equals("")) {
                        Log.d("hermesgame", "游戏盒子服务绑定成功 ， onServiceConnected  username: " + jsonObject.getString("username"));

                        try {
                            if (mActivity != null) {
                                LoginManager.getInstance().saveAccountInfo(mActivity, jsonObject.getString("username"), jsonObject.getString("authToken"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LoginManager.getInstance().isAutoLogin = true;


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("hermesgame", "onServiceConnected  Exception: " + e.toString());
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = new Intent();
        intent.setAction("com.gamebox.getToken");
        //ComponentName componentName=new ComponentName("com.quicksdk.gamebox","com.hermesgame.and.TokenService");
        ComponentName componentName = new ComponentName(data.getAppAuthInfo().getAppPackage(), "com.hermesgame.and.TokenService");
        intent.setComponent(componentName);
        if (data.getProductconfig().getUseAppAuth() != null && data.getProductconfig().getUseAppAuth().equals("1")) {
            intent.setPackage(data.getAppAuthInfo().getAppPackage());
        }
        context.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    public void loadSkin(final String name) {
        SkinManager.getInstance().load(name + ".skin", new ILoaderListener() {
            @Override
            public void onStart() {
                Log.d("hermesgame", "load skin ");
            }

            @Override
            public void onSuccess() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //	Toast.makeText(mActivity,"皮肤加载成功："+name,Toast.LENGTH_LONG).show();
                    }
                });
                Log.d("hermesgame", "load skin onSuccess");
            }

            @Override
            public void onFailed() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(mActivity,"皮肤加载失败："+name,Toast.LENGTH_LONG).show();
                        Log.e("hermesgame", "skin load failed ：" + name);
                    }
                });
            }
        });
    }

    /**
     * @param activity cp传入
     */

    public void getDeviceBindAccountResult(final Activity activity, final QGCallBack callBack) {
        final CountDownLatch count = new CountDownLatch(1);


        final HttpRequest<String> request = new HttpRequest<String>() {
            @Override
            public void onSuccess(final String bean) {

                callBack.onSuccess();

            }

            @Override
            public void onFailed(int id, String message) {
                Log.e("hermesgame", "getDeviceBindAccountResult failed: " + message);

                if (id == 40050) {
                    callBack.onFailed("该设备已注册过账号");
                } else {
                    callBack.onSuccess();
                }

            }
        }.addParameter(getInitParameter(activity)).post().setUrl(Constant.HOST + Constant.CHECK_DEVICE_BIND);
        // QGProgressBar.disable();
        DataManager.getInstance().requestHttp(request);

    }

    public void dealWithWechatPlugin(final Context context, final InitData bean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < bean.getPaytypes().size(); i++) {
                    if (bean.getPaytypes().get(i).getPaytypeid() == 226) {
                        try {
                            InputStream open = context.getAssets().open("hermesgame_sdk/QGPayPlugin.apk");
                            if (open != null) {
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "hermesgame"
                                        + File.separator + mActivity.getPackageName() + File.separator;
                                File pathFile = new File(path);
                                if (!pathFile.exists()) {
                                    pathFile.mkdirs();
                                }
                                File outFile = new File(path, "QGPayPlugin.apk");

                                FileOutputStream fos = new FileOutputStream(outFile);
                                byte[] buffer = new byte[1024];
                                int byteCount;
                                while ((byteCount = open.read(buffer)) != -1) {
                                    fos.write(buffer, 0, byteCount);
                                }
                                fos.flush();
                                open.close();
                                fos.close();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("hermesgame", "dealWithWechatPlugin ex: " + e.toString());
                            return;
                        }
                    }
                }
            }
        }).start();

    }

}
