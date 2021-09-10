package com.hermesgamesdk.manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.iqiyi.passportsdk.Passport;
import com.iqiyi.passportsdk.external.PassportCallback;
import com.iqiyi.passportsdk.external.PassportConfig;

import com.iqiyi.passportsdk.external.empty.EmptyImplClient;
import com.iqiyi.passportsdk.interflow.InterflowConstants;
import com.iqiyi.passportsdk.interflow.InterflowSdk;
import com.iqiyi.passportsdk.interflow.api.IInterflowApi;
import com.iqiyi.passportsdk.interflow.callback.GameRegisterSignCallback;
import com.iqiyi.passportsdk.interflow.callback.GetIqiyiUserInfoCallback;
import com.iqiyi.passportsdk.interflow.safe.DataEncryptor;
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;
import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.callback.QGCallBack;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.fragment.login.ActiveFragment;


import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.quickgamesdk.iqiyilogin.Getter;
import com.quickgamesdk.iqiyilogin.HttpConstants;
import com.quickgamesdk.iqiyilogin.Proxy;
import com.quickgamesdk.iqiyilogin.UserCache;
import com.tapsdk.bootstrap.TapBootstrap;
import com.tapsdk.bootstrap.account.LoginType;
import com.tapsdk.bootstrap.account.TapLoginResultListener;
import com.tapsdk.bootstrap.exceptions.TapError;
import com.tds.common.entities.AccessToken;
import com.tds.common.entities.TapConfig;
import com.tds.common.models.TapRegionType;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class ThirdManager {
	private String mGetAuthInfoUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
	private Activity mActivity;
	private static ThirdManager mThreadManager;
	private IWXAPI mWxapi;
	private ThirdLoginCallBack mCallBack;
	private QGCallBack mOKLoginCallback;
	public static long iqiyiLoginKey;
	public String webQQAppId = "";
	public String webWXAppId = "";
	public String webWXSec = "";

	// 手机一键授权
	private PhoneNumberAuthHelper  helper;
	private int mScreenWidthDp;
	private int mScreenHeightDp;
	private TokenResultListener tokenResultListener;


	private ThirdManager() {

	}

	public void destroy() {
		mThreadManager = null;
	}

	public static ThirdManager getInstance() {
		if (mThreadManager == null)
			mThreadManager = new ThirdManager();
		return mThreadManager;
	}

	public void initiqy(Activity activity) {
		PassportConfig config = new PassportConfig.Builder().setGetter(new Getter()).setHttpProxy(new Proxy())
				.setCache(UserCache.getInstance()).setClient(new EmptyImplClient()).setBaseCore(null).build();
		Passport.init(activity.getApplicationContext(), config);
		Passport.setCallback(new PassportCallback() {
			@Override
			public void onLogin() {
				Log.e("hermesgame_iqy", "onLogin");
			}

			@Override
			public void onLoginUserInfoChanged() {
				Log.e("hermesgame_iqy", "onLoginUserInfoChanged");
			}

			@Override
			public void onLogout() {
				Log.e("hermesgame_iqy", "onLogout");
			}
		});
		Passport.addHttpApi(IInterflowApi.class);

		InterflowSdk.gameRegisterSign(new GameRegisterSignCallback() {

			@Override
			public void onSuccess() {
				Log.e("hermesgame_iqy", "gameRegisterSign  onSuccess");

			}

			@Override
			public void onFail(String arg0) {
				// TODO Auto-generated method stub
				Log.e("hermesgame_iqy", "gameRegisterSign  onFail" + arg0);

			}
		});

		if (InterflowSdk.isIqiyiSupport(activity)) {// 基线是否支持
			InterflowSdk.getIqiyiUserInfo(new GetIqiyiUserInfoCallback() {// 获取爱奇艺用户信息
						@Override
						public void onGetIqiyiUserInfo(Bundle bundle) {
							Log.e("hermesgame_iqy", "getIqiyiUserInfo  onGetIqiyiUserInfo");
							boolean isIqiyiLogin = bundle.getBoolean(InterflowConstants.KEY_INFO_ISLOGIN);
							// 用户名，可用于ui展示
							String iqiyiLoginName = bundle.getString(InterflowConstants.KEY_INFO_UNAME);
							// 用户头像url，可用于ui展示
							String iqiyiUserIcon = bundle.getString(InterflowConstants.KEY_INFO_UICON);

							Log.e("hermesgame_iqy", "getIqiyiUserInfo  iqiyiLoginName" + iqiyiLoginName);

						}

						@Override
						public void onFail() {
							Log.e("hermesgame_iqy", "getIqiyiUserInfo  onFail");
						}
					});
		} else {
			Log.e("hermesgame_iqy", "isIqiyiSupport  else");
		}
	}

	public void Auth(Activity activity) {
		Intent intent = new Intent();
		iqiyiLoginKey = DataEncryptor.generateRequestKey();
		intent.setPackage(InterflowConstants.QIYI_PACKAGE_NAME);
		intent.setClassName(InterflowConstants.QIYI_PACKAGE_NAME, HttpConstants.CLASSNAME_GAME_TRANSFER_ACTIVITY);
		intent.putExtra(InterflowConstants.EXTRA_INTERFLOW_VERSION, InterflowSdk.VERSION);
		intent.putExtra(InterflowConstants.EXTRA_INTERFLOW_PACKAGE, activity.getPackageName());
		intent.putExtra(InterflowConstants.EXTRA_INTERFLOW_REQUEST_ID, iqiyiLoginKey);
		intent.putExtra(HttpConstants.EXTRA_INTERFLOW_BIZ, HttpConstants.BIZ_GAME);
		// 接受onNewIntent的ActivityName，需要是singleTask
		intent.putExtra(HttpConstants.EXTRA_INTERFLOW_ENTRY, activity.getClass().getName());
		activity.startActivityForResult(intent, 12);
	}

	public void qqLogin(Activity activity, ThirdLoginCallBack callBack) {
		mCallBack = callBack;
		mActivity = activity;
		LoginManager.getInstance().getTencent().login(activity, "all", tencentListener);
	}

	public IUiListener tencentListener = new IUiListener() {
		@Override
		public void onComplete(Object o) {
			Log.d("hermesgame", " qq success");
			JSONObject obj = (JSONObject) o;
			try {
				String openId = obj.getString("openid");
				String accessToken = obj.getString("access_token");
				if (LoginManager.getInstance().isFromSDK) {
					Log.d("hermesgame", " qq success isFromSDK  " + 1);
					thridLogin(Constant.OPEN_TYPE_QQ, openId, accessToken);
				} else {
					Log.d("hermesgame", " qq success isFromSDK  " + 2);
					mCallBack.onLoginFailed(1, obj.toString());
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onError(UiError uiError) {
			Log.d("hermesgame", " qq onError " + 1);
		}

		@Override
		public void onCancel() {
			Log.d("hermesgame", " qq onCancel " + 1);
		}
	};

	public void thridLogin(final String openType, String userOpenId, String accessToken) {
		String mOpenType=openType;
		String mUserOpenId=userOpenId;
		String mAccessToken=accessToken;

		String param;
			if (mActivity==null){
			Log.d("hermesgame", " thridLogin mActivity==null" );
			}

			if (openType.equals(Constant.OPEN_TYPE_TAPTAP)){
				param = new QGParameter(mActivity).addParameter("openType", mOpenType)
						.addParameter("userOpenId", mUserOpenId).addParameter("access_token", mAccessToken)
						.addParameter("mac_key",mUserOpenId)
						.addParameter("appid",QGConfig.getQQAppId().trim()).create();
			}else{
				param = new QGParameter(mActivity).addParameter("openType", mOpenType)
						.addParameter("userOpenId", mUserOpenId).addParameter("access_token", mAccessToken)
						.addParameter("appid",QGConfig.getQQAppId().trim()).create();
			}

			HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
				@Override
				public void onSuccess(QGUserInfo bean) {
					if (openType.equals(Constant.OPEN_TYPE_TAPTAP)){
						LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_TAPTAP;
					}else if (openType.equals(Constant.OPEN_TYPE_QQ)){
						LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_QQ;
					}
					else if (openType.equals(Constant.OPEN_TYPE_WX)){
						LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_WEIXIN;
					}
					else if (openType.equals(Constant.OPEN_TYPE_ALYUN)){
						LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_ALYUN;
					}
					if (mCallBack != null && LoginManager.getInstance().isFromSDK) {

						mCallBack.onLoginSuccessed(bean);
					} else if (!LoginManager.getInstance().isFromSDK) {
						try {
							// // 保存账号密码
							String username = bean.getUserdata().getUsername();
							String token = bean.getAuthtoken();
							saveAccountInfo(mActivity, username, token);
							if (isNeedActive(bean)) {
								BaseFragment fragment = new ActiveFragment();
								fragment.setTag(bean.getAuthtoken());
								QGFragmentManager.getInstance((FragmentActivity) mActivity).add(fragment);
							} else {
								// switchToCerificationFragment(CerificationNode.ON_LOGIN);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
				@Override
				public void onFailed(int id, String message) {
					Log.e("hermesgame", "thridLogin onFailed ： " + message);
					if (mCallBack != null && LoginManager.getInstance().isFromSDK) {
						mCallBack.onLoginFailed(-1, message);
					} else if (!LoginManager.getInstance().isFromSDK) {
						LoginManager.getInstance().notifyLoginFiled(message);
					}
				}
			}.addParameter(param).post().setUrl(Constant.HOST + Constant.THIRD_LOGIN);
			DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);
	}

	public void wxLogin(Activity activity, ThirdLoginCallBack callBack) {

		mCallBack = callBack;
		mActivity = activity;
		mWxapi = LoginManager.getInstance().getIWXAPI();

		if (!mWxapi.isWXAppInstalled()) {
			QGSdkUtils.showToast(
					activity,
					activity.getString(activity.getResources().getIdentifier("toast_text_have_no_weixin", "string",
							activity.getPackageName()))+"1");
		//	return;
		}
		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo_test";
		mWxapi.sendReq(req);
	}

	public void wxLogin(Activity activity) {
		mActivity = activity;
		mWxapi = LoginManager.getInstance().getIWXAPI();

		if (!mWxapi.isWXAppInstalled()) {
			QGSdkUtils.showToast(
					activity,
					activity.getString(activity.getResources().getIdentifier("toast_text_have_no_weixin", "string",
							activity.getPackageName()))+"2");
			return;
		}
		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo_test";
		mWxapi.sendReq(req);
	}

	public IWXAPI getIWXAPI() {
		return mWxapi;
	}
	public void setIWXAPI(IWXAPI api) {
		if (api!=null){
			mWxapi=api;
		}
	}

	/**
	 * 失败的type 0标识微信 1标识qq 默认或未知为-1
	 * 
	 * @author chenjia02
	 * 
	 */
	public static interface ThirdLoginCallBack {
		void onLoginSuccessed(QGUserInfo bean);

		void onLoginFailed(int Type, String msg);
	}

	public void onWxGranted(String code) {
		String url = "";
		if (LoginManager.getInstance().isFromSDK) {
			url = String.format(mGetAuthInfoUrl, QGConfig.getWXAppId(), QGConfig.getWXAppSecret(), code);
		} else {
			url = String.format(mGetAuthInfoUrl, webWXAppId, webWXSec, code);
		}

		Log.e("hermesgame.onWxGranted", url);
		HttpRequest<String> request = new HttpRequest<String>() {

			@Override
			public void onSuccess(String json) {
				try {
					JSONObject jsonObj = new JSONObject(json);
					String accessToken = jsonObj.getString("access_token");

					String openId = jsonObj.getString("openid");

					if (LoginManager.getInstance().isFromSDK) {
						thridLogin(Constant.OPEN_TYPE_WX, openId, accessToken);
					} else {
						mCallBack.onLoginFailed(0, json);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailed(int id, String message) {
				QGSdkUtils.showToast(mActivity, message);
			}
		}.get().setUrl(url);
		DataManager.getInstance().requestHttp(request);
	}


	public void initOKLogin(final Context ctx) {
		tokenResultListener=new TokenResultListener() {
			@Override
			public void onTokenSuccess(String s) {
				Log.d("hermesgame", "PhoneNumberAuthHelper onTokenSuccess: " + s);
				try {
					JSONObject json = new JSONObject(s);
					String code = json.optString("code");
					if (code.equals("600000")) {
						helper.quitLoginPage();
						String token = json.optString("token");
						mThreadManager.thridLogin(Constant.OPEN_TYPE_ALYUN, "0", token);
					} else {
						//helper.quitLoginPage();
						String msg = json.optString("smg");
						//Toast.makeText(ctx, "请求失败  code: " + code + "  , msg: " + msg, Toast.LENGTH_LONG).show();
						//此处会多次回调 暂不处理
						//mOKLoginCallback.onFailed(s);
					}
				} catch (Exception e) {
					// todo
				}
			}

			@Override
			public void onTokenFailed(String s) {
				Log.e("hermesgame", "onTokenFailed: " + s);
				helper.hideLoginLoading();
				helper.quitLoginPage();
				Toast.makeText(ctx, "授权失败,请尝其余登录方式", Toast.LENGTH_LONG).show();
				mOKLoginCallback.onFailed(s);
			}
		};
		helper = PhoneNumberAuthHelper.getInstance(ctx, tokenResultListener);
		helper.getReporter().setLoggerEnable(true);
		helper.setAuthSDKInfo(QGConfig.getmALPKey());
	}

	public boolean checkOKLogin(QGCallBack okloginCallback) {
		mOKLoginCallback=okloginCallback;
		try {
			if (helper.checkEnvAvailable()) {
				Log.d("hermesgame", "checkEnvAvailable true");
				return true;
			} else {
				Log.e("hermesgame", "checkEnvAvailable false");
				return false;
			}
		} catch (Exception e) {
			Log.e("hermesgame", "checkEnvAvailable erro : " + e.toString());
			return false;
		}


	}

	public void showOKLogin(final Context ctx,ThirdLoginCallBack callBack) {
		mCallBack=callBack;
		helper = PhoneNumberAuthHelper.getInstance(ctx, tokenResultListener);
		mActivity=(Activity)ctx;
				configLoginTokenLand(ctx);
				helper.getLoginToken(ctx, 5000);

	}

	private void configLoginTokenLand(final Context ctx) {
		helper.removeAuthRegisterXmlConfig();
		helper.removeAuthRegisterViewConfig();
		int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		if (((Activity)ctx).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){

			authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

		}
		if (Build.VERSION.SDK_INT == 26) {
			//setRequestedOrientation(authPageOrientation);
			authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
		}

		updateScreenSize(ctx, authPageOrientation);

		final int dialogWidth = 326;
		final int dialogHeight = 300;
/*		helper.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder().setLayout(
				QGSdkUtils.getResId(ctx, "R.layout.custom_port_dialog_action_bar"), new AbstractPnsViewDelegate() {
					@Override
					public void onViewCreated(View view) {
						ImageView btn_close= (ImageView) findViewById(QGSdkUtils.getResId(ctx, "R.id.btn_close"));
						findViewById(QGSdkUtils.getResId(ctx, "R.id.btn_close")).setOnClickListener(
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										helper.quitLoginPage();

									}
								});
					}
				}).build());*/

		helper.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder().setLayout(
				QGSdkUtils.getResId(ctx, "R.layout.qg_custom_land_dialog"), new AbstractPnsViewDelegate() {
					@Override
					public void onViewCreated(View view) {

						findViewById(QGSdkUtils.getResId(ctx, "R.id.btn_close")).setOnClickListener(
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										helper.quitLoginPage();
										mOKLoginCallback.onFailed("close");
									}
								});
						findViewById(QGSdkUtils.getResId(ctx, "R.id.qg_goto_other")).setOnClickListener(
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										helper.quitLoginPage();
										mOKLoginCallback.onFailed("other");
									}
								});

					}
				}).build());

		helper.setAuthUIConfig(new AuthUIConfig.Builder()
				.setPrivacyState(false)
				.setSwitchAccHidden(true)
				.setSloganHidden(true)
				.setNavHidden(true)
				.setStatusBarHidden(true)
				.setCheckboxHidden(true)
				.setNumFieldOffsetY(40)
				.setNumberSize(22)
				.setNumberFieldOffsetX(0)
				.setNumberColor(Color.parseColor("#333333"))
				.setLogBtnOffsetY(95)
				.setLogBtnText("本机号码一键登录")
				.setLogBtnWidth(284)
				.setLogBtnHeight(38)
				.setLogBtnBackgroundPath("qg_btn_selector")
				.setDialogWidth(dialogWidth)
				.setDialogHeight(dialogHeight)
				.setAuthPageActIn("in_activity", "out_activity")
				.setAuthPageActOut("in_activity", "out_activity")
				.setVendorPrivacyPrefix("《")
				.setVendorPrivacySuffix("》")
				.setNavReturnHidden(false)
				.setPrivacyOffsetY(160)
				.setScreenOrientation(authPageOrientation)
				.create());

	}
	public void initTaptap(Context context){
		TapConfig tapConfig = new TapConfig.Builder()
				.withAppContext(context)
				.withClientId(QGConfig.getmTapID()) // 开发者中心获取到的 Client ID
				.withClientSecret(QGConfig.getmTapKey())
				.withRegionType(TapRegionType.CN) // TapRegionType.CN: 中国大陆  TapRegionType.IO: 国际
				.build();
		TapBootstrap.init((Activity)context, tapConfig);
		mActivity=(Activity)context;
		TapBootstrap.registerLoginResultListener(new TapLoginResultListener() {
			@Override
			public void loginSuccess(final AccessToken accessToken) {
				Log.d("hermesgame", "TapBootstrap onLoginSuccess: " + accessToken.toJSON());
				try{
					ThirdManager.getInstance().thridLogin(Constant.OPEN_TYPE_TAPTAP,accessToken.macKey,accessToken.accessToken);
				}catch (Exception e){
					e.printStackTrace();
					Log.e("hermesgame.ThirdManager","thridLogin exception"+e.toString());
				}

			}

			@Override
			public void loginFail(TapError tapError) {
				Log.d("hermesgame", " TapBootstrap onLoginError: " + tapError.getMessage());
			}

			@Override
			public void loginCancel() {
				Log.d("hermesgame", "TapBootstrap onLoginCancel");
			}
		});
	}
	public void showTaptap(Activity activity,ThirdLoginCallBack callBack){
		mActivity=activity;
		mCallBack=callBack;
		TapBootstrap.login(mActivity, LoginType.TAPTAP, "public_profile");
	}

	/**
	 * @param account

	 *            向文件中存储json格式的账号信息 password 长度大于30表示存入的为token
	 */
	protected static void saveAccountInfo(Activity activity, String account, String password) {

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

	public static boolean isNeedActive(QGUserInfo info) {
		if (info != null && info.getUserdata().getNeedActive() == 1) {
			return true;
		}
		return false;

	}


	protected void updateScreenSize(Context context,int authPageScreenOrientation) {
		int screenHeightDp = QGSdkUtils.px2dp(context, QGSdkUtils.getPhoneHeightPixels(context));
		int screenWidthDp = QGSdkUtils.px2dp(context, QGSdkUtils.getPhoneWidthPixels(context));
		int rotation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
		if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_BEHIND) {
			authPageScreenOrientation =( (Activity)context).getRequestedOrientation();
		}
		if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				|| authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
				|| authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE) {
			rotation = Surface.ROTATION_90;
		} else if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
				|| authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
				|| authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT) {
			rotation = Surface.ROTATION_180;
		}
		switch (rotation) {
			case Surface.ROTATION_0:
			case Surface.ROTATION_180:
				mScreenWidthDp = screenWidthDp;
				mScreenHeightDp = screenHeightDp;
				break;
			case Surface.ROTATION_90:
			case Surface.ROTATION_270:
				mScreenWidthDp = screenHeightDp;
				mScreenHeightDp = screenWidthDp;
				break;
			default:
				break;
		}
	}

}
