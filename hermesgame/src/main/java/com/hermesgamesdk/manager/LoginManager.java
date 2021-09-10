package com.hermesgamesdk.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.applog.GameReportHelper;
import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.QGManager;
import com.hermesgamesdk.activity.GameSliderBarActivityV2;
import com.hermesgamesdk.activity.LoginActivity;
import com.hermesgamesdk.activity.TempActivty;
import com.hermesgamesdk.callback.QGCallBack;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.entity.QGRoleInfo;
import com.hermesgamesdk.entity.QGUserExtraInfo;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.utils.WriteTimeUtils;
import com.hermesgamesdk.view.AlertDialog;
import com.hermesgamesdk.view.ToastLoginSuccess;
import com.hermesgamesdk.aidl.GetToken;

import com.quickjoy.adplus.ADP;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/9/5.
 */

public class LoginManager {
    public static LoginManager instance;

    private QGCallBack mLoginCallBack = null;

    private QGCallBack mLogoutCallBack = null;

    public Activity mActivity = null;

    public boolean isAutoLogin = true;
    public boolean isFromSDK = true;
    public boolean isFromLimited = false;
    public boolean isSilenceLogin = false;
    public GetToken getToken;
    public String loginType = "0";

    private static Tencent mTencent;
    private IWXAPI mWXApi;
    private QGRoleInfo mRoleInfo;
    // 在线时长统计
    /*
     * Handler handler = new Handler(); Runnable runnable = new Runnable() {
     *
     * @Override public void run() { HttpRequest<String> request = new
     * HttpRequest<String>() {
     *
     * @Override public void onSuccess(String bean) { Log.d("hermesgame",
     * "time online success");
     *
     * }
     *
     * @Override public void onFailed(String message) { Log.d("hermesgame",
     * "time online failed： " + message); } }.addParameter(new
     * QGParameter(mActivity).create()).post().setUrl(Constant.HOST +
     * Constant.ONLINETIME);
     *
     * DataManager.getInstance().requestHttp(request, "");
     * handler.postDelayed(this, 5 * 60 * 1000); } };
     */
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent();
            intent.putExtra("from", "notice");
            intent.setClass(mActivity, TempActivty.class);
            mActivity.startActivity(intent);
        }
    };

    public QGRoleInfo getRoleInfo() {
        return mRoleInfo == null ? new QGRoleInfo() : mRoleInfo;
    }

    public void destroy() {
        instance = null;

    }


    public static LoginManager getInstance() {
        if (instance == null) {
            instance = new LoginManager();
        }
        return instance;
    }

    public String getLoginType() {
        return loginType;
    }

    public void login(Activity activity, QGCallBack callBack) {
        mActivity = activity;
        mLoginCallBack = callBack;
        //停止计时 否则会线程干扰
        WriteTimeUtils.getInstance().stopWriteTime();
        InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
        if (data == null) {
            QGSdkUtils.showToast(
                    activity,
                    activity.getString(activity.getResources().getIdentifier("toast_text_not_init", "string",
                            activity.getPackageName())));
            notifyLoginFiled(mActivity.getString(mActivity.getResources().getIdentifier("toast_text_not_init",
                    "string", mActivity.getPackageName())));
        } else {
            if (QGConfig.isSupportWXLogin()) {
                LoginManager.getInstance().initWXLogin(activity);
            }
            //禁止模拟器
            if (data.getForbidEmt().equals("1") && QGSdkUtils.getIsEumlator(activity)) {
                String message = activity.getString(QGSdkUtils.getResId(activity, "R.string.qg_login_emt_limit"));
                final AlertDialog alert = new AlertDialog(InitManager.getInstance().mActivity, null, "提示", message, "确认",
                        "") {

                    @Override
                    public void onDismiss() {
                    }
                };
                alert.hideClose();
                alert.setCancelable(false);
                alert.setClickListener(new AlertDialog.onClick() {
                    @Override
                    public void onLeftClick() {
                        alert.dismiss();
                        notifyLoginFiled(mActivity.getString(mActivity.getResources().getIdentifier("qg_login_emt_limit",
                                "string", mActivity.getPackageName())));
                    }

                    @Override
                    public void onRightClick() {
                    }
                });
                alert.show();

            } else {

                Intent intent = new Intent();
                intent.setClass(activity, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("from", "login_manager");
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }


        }

    }

    public void slienceLogin(final Activity activity, final QGCallBack callBack) {
        mActivity = activity;
        mLoginCallBack = callBack;
        if (DataManager.getInstance().getData(Constant.INIT_KEY) == null) {
            QGSdkUtils.showToast(
                    activity,
                    activity.getString(activity.getResources().getIdentifier("toast_text_not_init", "string",
                            activity.getPackageName())));
            callBack.onFailed(mActivity.getString(mActivity.getResources().getIdentifier("toast_text_not_init",
                    "string", mActivity.getPackageName())));


        } else {
            HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
                @Override
                public void onSuccess(QGUserInfo bean) {
                    LoginManager.getInstance().loginType = Constant.LOGIN_TYPE_ACCOUNT;
                    SliderBarV2Manager.getInstance(activity).init();
                    if (bean.getId() == 40040 && bean.getCheckrealname() == 0) {
                        showLimitedDialog(activity, bean.getMessage());
                        callBack.onFailed(bean.getMessage());
                    } else {
                        InitData initData = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
                        int realNameNode = 1;
                        if (initData != null) {
                            realNameNode = initData.getRealNameNode();
                        }
                        String username = bean.getUserdata().getUsername();
                        String password = bean.getUserdata().getUpwd();
                        if (TextUtils.isEmpty(password)) {
                            password = bean.getAuthtoken();
                        }
                        saveAccountInfo(activity, username, password);
                        if (password.length() < 32) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("from", "slienceLogin_tryplay");
                            intent.putExtras(bundle);
                            intent.setClass(activity, TempActivty.class);
                            mActivity.startActivity(intent);
                        }
                        if (realNameNode == Constant.CRETIFICATION_DISPLAY_ON_LOGIN) {
                            int flag = ((QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY)).getCheckrealname();
                            if (flag != Constant.CRETIFICATION_HAS_CERTIFIED && flag != Constant.CRETIFICATION_NOT_NEED) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString("from", "slienceLogin_cert");
                                intent.putExtras(bundle);
                                intent.setClass(activity, TempActivty.class);
                                mActivity.startActivity(intent);
                            }
                        }

                        callBack.onSuccess();
                        requestQGUserExtraInfo();
                    }
                }

                @Override
                public void onFailed(int id, String message) {
                    callBack.onFailed(message);
                    Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
                }
            }.addParameter(new QGParameter(mActivity).create()).post().setUrl(Constant.HOST + Constant.GUEST_LOGIN);
            DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);
        }


    }

    public void notifyLoginSuccessed() {

        mLoginCallBack.onSuccess();
        isAutoLogin = true;

        // 登录成功后请求用户详细信息，用于之后悬浮窗UI显示,并且在其内开始防沉迷计时
        requestQGUserExtraInfo();
        //QGPayManager.getInstance().checkSDKCoins(mActivity);
        String modifyUsername = QGManager.getUserName();
        if (modifyUsername.length() > 11) {
            modifyUsername = modifyUsername.substring(0, 10) + "..";
        }
        ToastLoginSuccess.makeText(mActivity, mActivity.getResources().getString(QGSdkUtils.getResId(mActivity, "R.string.qg_account_welcome")) + "," + modifyUsername, Toast.LENGTH_LONG).show();
        if (QGConfig.isSupportAD()) {
             ADP.getInstance().account(QGManager.getUID(), QGManager.getUserName());
            ADP.getInstance().active(QGManager.getUID(), QGManager.getUserName(),"0");
        }
        //内置事件: “注册” ，属性：注册方式，是否成功，属性值为：wechat ，true
        GameReportHelper.onEventRegister("wechat",true);


        if (Constant.noticeShowNode == Constant.NOTICE_NODE_AFTER_LOGIN) {
            handler.postDelayed(runnable, 2000);
        }
        InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
        if (!data.getProductconfig().getRpDialogSwitch().equals("1")) {
            QGSdkUtils.showRedPack(mActivity);
        }
        if (!data.getProductconfig().getRpScrollSwitch().equals("1")) {
            QGSdkUtils.showMarQ(mActivity);
        }
    }

    /*
     * public void stopOnLine() { handler.removeCallbacks(runnable); }
     */
    public void notifyLoginFiled(String msg) {
        mLoginCallBack.onFailed(msg);
    }

    public void setLogoutCallback(QGCallBack qgCallBack) {
        mLogoutCallBack = qgCallBack;
    }

    public void logout(Activity activity) {
        // stopOnLine();
        Log.d("hermesgame", "logout");
        postSessionId();
        WriteTimeUtils.getInstance().stopWriteTime();
        Object data = DataManager.getInstance().getData("initData");
        if ("0".equals(((InitData) data).getProductconfig().getIsShowFloat())) {
            if (activity != null) {
                SliderBarV2Manager.getInstance(activity).stopDownLoad();
                SliderBarV2Manager.getInstance(activity).destorySliderBar();
            }
        }
        DataManager.getInstance().clear();
        DataManager.getInstance().putData("initData", data);
        isAutoLogin = false;
        QGSdkUtils.saveString(mActivity, "screenType", "");
        QGSdkUtils.saveString(mActivity, "windowRate", "");
        QGSdkUtils.saveString(mActivity, "barColor", "");
        if (mLogoutCallBack != null) {
            mLogoutCallBack.onSuccess();
        }
        if (activity instanceof GameSliderBarActivityV2) {
            activity.finish();
        }
    }

    /**
     * 请求用户详细信息（绑定的手机号等）
     */
    public void requestQGUserExtraInfo() {
        String uid = QGManager.getUID();

        String parameter = new QGParameter(mActivity).addParameter("uid", uid).create();
        HttpRequest<QGUserExtraInfo> request = new HttpRequest<QGUserExtraInfo>() {
            @Override
            public void onSuccess(QGUserExtraInfo info) {
                startCountTime();
            }

            @Override
            public void onFailed(int id, String message) {
                Log.e("hermesgame", message);
            }
        }.addParameter(parameter).post().setUrl(Constant.HOST + Constant.USER_EXTRA_INFO);
        // QGProgressBar.disable();
        DataManager.getInstance().requestHttp(request, Constant.USEREXTRAINFO_KEY);
    }

    /**
     * 投递防沉迷sessionId
     */
    public void postSessionId() {
        QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
        String sessionId = info.getUserdata().getSiId();
        String uid = QGManager.getUID();

        String parameter = new QGParameter(mActivity).addParameter("sessionId", sessionId).addParameter("uid", uid).create();
        HttpRequest<String> request = new HttpRequest<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onFailed(int id, String message) {
                Log.e("hermesgame", message);
            }
        }.addParameter(parameter).post().setUrl(Constant.HOST + Constant.ADDICTION_PREVENTION);

        DataManager.getInstance().requestHttp(request);
    }

    public void setGameRoleInfo(final Activity activity, QGRoleInfo roleInfo) {
        mRoleInfo = roleInfo;
        HttpRequest<String> request = new HttpRequest<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("hermesgame", "setGameRoleInfo Success");
                try {
                    JSONObject roleInfoObject = new JSONObject();
                    roleInfoObject.put("gameRoleName", mRoleInfo.getRoleName());
                    roleInfoObject.put("serverName", mRoleInfo.getServerName());
                    roleInfoObject.put("gameRoleLevel", mRoleInfo.getRoleLevel());

                    QGSdkUtils.saveString(activity, QGManager.getUID(), roleInfoObject.toString());

                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailed(int id, String message) {
                Log.e("hermesgame", "setGameRoleInfo onFailed: id=" + id + "    msg:" + message);

            }
        }.addParameter(
                new QGParameter(mActivity).addParameter("uid", QGManager.getUID())
                        .addParameter("gameRoleId", roleInfo.getRoleId())
                        .addParameter("gameRoleName", roleInfo.getRoleName())
                        .addParameter("serverId", roleInfo.getServerId())
                        .addParameter("serverName", roleInfo.getServerName())
                        .addParameter("gameRoleLevel", roleInfo.getRoleLevel())
                        .addParameter("vipLevel", roleInfo.getVipLevel())
                        .addParameter("partyName", roleInfo.getPartyName())
                        .addParameter("rolePower", roleInfo.getRolePower())
                        .addParameter("gameRoleBalance", roleInfo.getBalance()).create()).post()
                .setUrl(Constant.HOST + Constant.GAMEROLE);
        DataManager.getInstance().requestHttp(request);
    }

    public void initIqyLogin(Activity context) {
        ThirdManager.getInstance().initiqy(context);
    }

    public void initQQLogin(Context context) {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(QGConfig.getQQAppId(), context);
        }
    }


    public void initOKLogin(Context context) {
        ThirdManager.getInstance().initOKLogin(context);
    }

    public void initTAPLogin(Context context) {
        ThirdManager.getInstance().initTaptap(context);
    }


    public void initQQLogin(Context context, String appId) {
        isFromSDK = false;
        if (mTencent == null) {
            mTencent = Tencent.createInstance(appId, context);
        }
    }

    public void initWXLogin(Context context) {
        if (mWXApi == null) {
            mWXApi = WXAPIFactory.createWXAPI(context, QGConfig.getWXAppId());
            mWXApi.registerApp(QGConfig.getWXAppId());
            ThirdManager.getInstance().setIWXAPI(mWXApi);
        }
    }

    public void initWXLogin(Context context, String appId) {
        isFromSDK = false;
        if (mWXApi == null) {
            mWXApi = WXAPIFactory.createWXAPI(context, appId);
            mWXApi.registerApp(appId);
        }
    }

    public IWXAPI getIWXAPI() {
        return mWXApi;
    }

    public Tencent getTencent() {
        return mTencent;
    }


    public void startCountTime() {
        QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
        String cacheDirPath = "";
        int age = -1;
        if (info.getuAge() != -1) {
            age = info.getuAge();
        }

        int realNameFlag = info.getCheckrealname();
        if (realNameFlag == Constant.CRETIFICATION_HAS_CERTIFIED && age >= 18) {
            Log.d("hermesgame", "账号已实名并且大于18岁");
            return;
        } else {
            cacheDirPath = Environment.getExternalStorageDirectory() + File.separator + "QGDir/"
                    + getStringDate() + "/" + Constant.PRODUCT_ID + "/"
                    + Constant.CHANNEL_ID + "/" + info.getUserdata().getUid();
            if (info.getCkPlayTime() == 1) {
                Log.d("hermesgame", "cacheDirPath： " + cacheDirPath);
                WriteTimeUtils.getInstance().startWriteTime();
                WriteTimeUtils.getInstance().writeTime(mActivity, cacheDirPath);
                WriteTimeUtils.getInstance().showDiaLog(mActivity, 7);
            } else {
                Log.e("hermesgame", "未在后台开启计时功能");
            }
        }

	/*	//计时条件  1. 已实名   2. 游客 并且关闭游客实名  和 开启产品实名
		if(info!=null&&(realNameFlag ==Constant.CRETIFICATION_HAS_CERTIFIED||(info.getUserdata().getIsGuest()==1&&info.getGuestRealName()==0&&realNameFlag!=-1))){
			age=info.getuAge();
			cacheDirPath=Environment.getExternalStorageDirectory() + File.separator + "QGDir/"
					+ getStringDate() + "/" + Constant.PRODUCT_ID+ "/"
					+ Constant.CHANNEL_ID + "/" + info.getUserdata().getUid();
		}else{
			Log.d("hermesgame"," userInfo is null or not cert:"+ ((QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY)).getCheckrealname());
			return;
		}
		if (age>=18||age==-1){

			return;
		}else if ((age>0&&age<18)||(info.getUserdata().getIsGuest()==1&&info.getGuestRealName()==0&&realNameFlag!=-1)){
			if (info.getCkPlayTime()==1){
				WriteTimeUtils.getInstance().startWriteTime();
				WriteTimeUtils.getInstance().writeTime(mActivity, cacheDirPath);
			}else{
				Log.e("hermesgame"," info.getCkPlayTime()==0");
			}
		}*/
    }

    // 获取时间地址
    public static String getStringDate() {
        /*
         * Date currentTime = new Date(); SimpleDateFormat formatter = new
         * SimpleDateFormat("yyyyMMdd"); String dateString =
         * formatter.format(currentTime);
         */
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateformat.format(Long.valueOf((String) DataManager.getInstance().getData("timestamp")) * 1000);
        return dateStr;
    }

    /**
     * 解绑手机之后，删除置顶的账号缓存
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void deleteTopAccount(Activity activity) {
        JSONArray jsonArray = readAccountInfoFromFile(activity);

        jsonArray.remove(jsonArray.length() - 1);
        if (jsonArray.length() != 0) {
            QGSdkUtils.saveString(activity, Constant.SP_ACCOUNT_INFO, jsonArray.toString());
        } else {
            QGSdkUtils.saveString(activity, Constant.SP_ACCOUNT_INFO, "");
        }
    }

    /**
     * @return 从文件中读取json格式存储的账号信息
     */
    protected static JSONArray readAccountInfoFromFile(Activity activity) {
        String accountInfo = QGSdkUtils.getString(activity, Constant.SP_ACCOUNT_INFO);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(accountInfo);
        } catch (JSONException e) {
            return new JSONArray();
        }
        return jsonArray;
    }

    public void showLimitedDialog(final Activity activity, String message) {

        String leftName = "我知道了";
        String rightName = "退出游戏";
        int flag = ((QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY)).getCheckrealname();
        if (flag == Constant.CRETIFICATION_HAS_CERTIFIED) {
            leftName = "";

        }

        final AlertDialog alertDialog = new AlertDialog(mActivity, null, "防沉迷提示", message, leftName, rightName) {
            @Override
            public void onDismiss() {

            }
        };
        alertDialog.setClickListener(new AlertDialog.onClick() {
            @Override
            public void onLeftClick() {
                alertDialog.dismiss();

            }

            @Override
            public void onRightClick() {
                alertDialog.dismiss();
                mLoginCallBack.onFailed("防沉迷管控");
                activity.finish();
                System.exit(0);
            }
        });
        alertDialog.hideClose();
        alertDialog.show();

    }

    public void saveAccountInfo(Activity activity, String account, String password) {

        try {

            JSONArray jsonArray = readAccountInfoFromFile(activity);
            JSONArray noRepeat = new JSONArray();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String accountFromFile = item.getString(Constant.SP_ACCOUNT);
                if (!account.equals(accountFromFile)) {
                    noRepeat.put(item);
                } else {
                    String prePasswd = item.getString(Constant.SP_PASSWORD);
                    if (prePasswd != null && "".equals(password)) {
                        password = QGSdkUtils.decryptAES(prePasswd, Constant.signkey);
                    }
                }
            }
            // 删除account的账号信息
            if (password == null) {
                QGSdkUtils.saveString(activity, Constant.SP_ACCOUNT_INFO, noRepeat.toString());
                return;
            }
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(Constant.SP_ACCOUNT, account);
            jsonObj.put(Constant.SP_PASSWORD, QGSdkUtils.encryptAES(password, Constant.signkey));

            if (noRepeat.length() >= 5) {
                noRepeat.put(0, jsonArray.get(1));
                noRepeat.put(1, jsonArray.get(2));
                noRepeat.put(2, jsonArray.get(3));
                noRepeat.put(3, jsonArray.get(4));
                noRepeat.put(4, jsonObj);
            } else {
                noRepeat.put(jsonObj);
            }
            QGSdkUtils.saveString(activity, Constant.SP_ACCOUNT_INFO, noRepeat.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
