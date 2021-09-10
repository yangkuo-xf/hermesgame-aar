package com.hermesgamesdk;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.hermesgamesdk.constant.Constant;
import com.iqiyi.passportsdk.interflow.safe.SignChecker;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.manager.DataManager;

public class QGConfig {
	private static String mProductCode;
	private static String mQQAppId;
	private static String mWXAppId;
	private static String mIQYAppId;
	private static String mWXAppSecret;



	private static String mALPKey;
	private static String mTapID;

	public static String getmTapKey() {
		return mTapKey;
	}

	public static void setmTapKey(String mTapKey) {
		QGConfig.mTapKey = mTapKey;
	}

	private static String mTapKey;
	private static String mAdAppId;
	private static int mServiceState = 0;
	private static boolean mShowFloat = true;
	private static boolean mAutoOpenAgreement = false;
	private static HashMap<String, Object> mExtraConfig;

	public static HashMap<String, Object> getmExtraConfig() {
		if (mExtraConfig != null) {
			return mExtraConfig;
		} else {
			return null;
		}
	}

	public static String getmALPKey() {
		return mALPKey;
	}

	public static void setmALPKey(String mALPKey) {
		QGConfig.mALPKey = mALPKey;
	}
	public static String getmTapID() {
		return mTapID;
	}

	public static void setmTapID(String mTapID) {
		QGConfig.mTapID = mTapID;
	}
	public static void setmExtraConfig(HashMap<String, Object> mExtraConfig) {
		QGConfig.mExtraConfig = mExtraConfig;
	}

	public static String getAdAppId() {
		return mAdAppId;
	}

	public static String getWXAppSecret() {
		return mWXAppSecret;
	}

	public static int getServiceState() {
		return mServiceState;
	}

	public static String getProductCode() {
		return mProductCode;
	}

	public static String getQQAppId() {

		return mQQAppId;
	}

	public static void setWXAppSecret(String appSecret) {
		mWXAppSecret = appSecret;
	}

	public static String getWXAppId() {
		return mWXAppId;
	}

	public static String getIQYAppId() {
		return mIQYAppId;
	}

	public static boolean isAutoOpenAgreement() {
		return mAutoOpenAgreement;
	}

	// 初始化成功之后调用
	public static void init(Context context) {
		try {

			mProductCode = getMetaData(context, "com.qk.plugin.customservice.productCode");
			mQQAppId = getMetaData(context, "QQ_APP_ID");
			mAdAppId = getMetaData(context, "AD_APP_ID");
			mWXAppId = getMetaData(context, "WX_APP_ID");
			mIQYAppId = getMetaData(context, "IQY_APP_ID");
			mWXAppSecret = getMetaData(context, "WX_APP_SECRET");
			mALPKey=getMetaData(context,"ALP_KEY");
			mTapID=getMetaData(context,"TAP_ID");
			mTapKey=getMetaData(context,"TAP_KEY");
			InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
			String tmp = data.getProductconfig().getUseservicecenter();
			//
			mServiceState = "2".equals(tmp) ? 2 : "1".equals(tmp) ? 1 : "3".equals(tmp) ? 3 : 0;

			// 1不显示悬浮窗，其他显示悬浮窗
			mShowFloat = "0".equals(data.getProductconfig().getIsShowFloat());
			mAutoOpenAgreement = "1".equals(data.getProductconfig().getAutoOpenAgreement());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isShowFloat() {
		return mShowFloat;
	}

	/**
	 * 是否支持客服，mProductCode="0"则不支持客服登录
	 */
	public static boolean isSupportCustomService() {
		return mServiceState != 0;
	}

	/**
	 * 是否支持客服，mProductCode="0"则不支持客服登录
	 */
	public static boolean isSupportIM() {
		return !mProductCode.equals("0");
	}

	/**
	 * 是否支持QQ登录，app_id="0"则不支持QQ登录
	 */
	public static boolean isSupportQQLogin() {
		return !"0".equals(mQQAppId);
	}

	public static boolean isSupportAD() {
		return !"0".equals(mAdAppId);
	}

	/**
	 * 是否支持微信登录，mWXAppId="0"则不支持微信登录
	 */

	public static boolean isSupportWXLogin() {
		return !"0".equals(mWXAppId);
	}

	public static boolean isSupportALPLogin() {
		if (mALPKey!=null){
			return !"0".equals(mALPKey)&&!mALPKey.contains("{{");
		}else {
			return false;
		}

	}

	public static boolean isSupportTAPLogin() {
		if (mTapID!=null){
			return !"0".equals(mTapID)&&!mTapID.contains("{{");
		}else {
			return false;
		}
	}

	public static boolean isSupportIQYLogin(Activity activity) {
		if (SignChecker.checkIqiyiSign(activity) && can2QiyiAuthLogin(activity) && "1".equals(mIQYAppId)) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isSupportOKLogin(Activity activity) {
		return true;
	}

	/**
	 * 是否支持手机功能
	 */
	public static boolean isSupportPhone() {
		String isSupportPhone = "0";
		try {
			isSupportPhone = ((InitData) DataManager.getInstance().getData(Constant.INIT_KEY)).getProductconfig()
					.getUsesms();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isSupportPhone.equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param context
	 * @param key
	 * @return application节点 meta的值没有获取到正确的值返回"0"
	 */
	private static String getMetaData(Context context, String key) {
		try {
			String str = context.getPackageName();
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			Bundle metaData = appInfo.metaData;
			if (metaData == null) {
				return "0";
			}
			String value = metaData.getString(key);
			if (value != null && value.length() > 1) {
				value = value.substring(2, value.length());
			}
			value = TextUtils.isEmpty(value) ? "0" : value;
			return value;

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		// 当解析错误默认为"0"
		return "0";
	}

	public static void destory() {
		mProductCode = null;
		mQQAppId = null;
		mWXAppId = null;
		mAdAppId = null;
		mALPKey=null;
		mServiceState = 0;
		mShowFloat = true;
		mAutoOpenAgreement = false;
	}

	public static boolean can2QiyiAuthLogin(Context context) {
		return isAppNewVersion("9.9.5", getIQIYIAPPVersionName((Activity) context));
	}

	public static boolean isAppNewVersion(String localVersion, String onlineVersion) {
		if (localVersion.equals(onlineVersion)) {
			return true;
		}
		String[] localArray = localVersion.split("\\.");
		String[] onlineArray = onlineVersion.split("\\.");

		int length = localArray.length < onlineArray.length ? localArray.length : onlineArray.length;

		for (int i = 0; i < length; i++) {
			if (parseInt(onlineArray[i]) > parseInt(localArray[i])) {
				return true;
			} else if (parseInt(onlineArray[i]) < parseInt(localArray[i])) {
				return false;
			}
			// 相等 比较下一组值
		}
		return true;
	}

	public static int parseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static String getIQIYIAPPVersionName(Activity activity) {
		PackageManager packageManager = activity.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo("com.qiyi.video", 0);
			if (packageInfo != null) {
				return packageInfo.versionName;
			} else {
				return "0";
			}
		} catch (PackageManager.NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "0";
		}
	}
}
