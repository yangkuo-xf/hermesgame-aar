package com.hermesgamesdk.manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.activity.QGSdkCoinsPayActivity;
import com.hermesgamesdk.activity.TempActivty;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.gamebox.db.DBHelper;
import com.hermesgamesdk.gamebox.db.ThreadDAO;
import com.hermesgamesdk.gamebox.db.ThreadDAOImple;
import com.hermesgamesdk.gamebox.entity.FileInfo;
import com.hermesgamesdk.gamebox.entity.ThreadInfo;
import com.hermesgamesdk.gamebox.service.DownloadService;
import com.hermesgamesdk.utils.PhotoPickerActivity;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.AlertDialog;
import com.hermesgamesdk.view.ToastGame;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class SliderBarV2Manager {
	public static SliderBarV2Manager sliderBarV2Manager;
	public static WebView mSliderWeb;
	public static Activity mActivity;
	public String type;
	public String rate;
	public String maxNum;
	public String screenType;
	private boolean isShowing = false;
	private boolean isShowed = false;
	private JSONObject params;
	public boolean isErro = false;
	private String baseSliderURL;
	private String sliderURL;
	public LinearLayout success, fail;
	private boolean isRetry = false;
	QGUserInfo userInfo ;
	public FrameLayout blankPart;
	public RelativeLayout rightPart;
	public LinearLayout layout_slider;
	public LinearLayout webPart;
	private String tempRef;
	private String mAction;
	public static String businessUrl;
	private Activity sliderBarActivityV2;


	String test = "http://10.0.22.65:84/userCenter/goodsList?sitetype=qgame&fullScreen=1&portrait=1&companyId=1&companyId=1&screenType=1&authToken=@112@160@171@135@105@103@110@166@132@138@171@140@134@153@170@125@138@151@163@134@126@137@156@149@134@158@154@163@117@107@157@163@175@160@171@156@136@135@155@139@104@129@132@128@151@141@172@155@123@136@164@168@167@128@131@116@130@124@113@162@99@123@151@159@105@159@178@160@130@159@104@129@176@138@139@127@152@103@143@115@147@96@168@169@129@163@158@101@114@170@147@142@167@126@110@135@168@170@111@176@154@125@96@126@125@143@144@153@132@123@127@100@144@142@165@128@132@130@102@139@135@174@143@145@146@109@118@99@110@102@106@152@92@105@132@152@107@108@130@103@118@173@155@120@169@118@179@174@155@102@137@155@128@128@141@121@103@140@134@171@168@123@155@140@105@171@128@92@164@131@178@131@139@121@133@131@166@108@122@174@163@154@165@105@145@124@147@132@149@143@153@164@177@172@170@144@161@158@155@163@166@106@104@122@125@143@164@133@156@158@167@175@125@119@114@110&deviceId=H5Browser&productCode=8cf0c4ea388c7d53435c1e361949f154&channelCode=default&productId=8225&fromSite=usercenter";



	private ThreadDAO mDao;
	private FileInfo fileInfo;
	private Intent mBoxIntent;
	private List<PackageInfo> installedList;
	private PackageManager packageManager;
	private UIRecive mRecive;
	private ApkRecive aRecive;

	public static SliderBarV2Manager getInstance(Activity activity) {
		mActivity = activity;
		if (sliderBarV2Manager == null) {
			sliderBarV2Manager = new SliderBarV2Manager();
		}
		return sliderBarV2Manager;
	}
	public void registReciver(){
		mRecive = new UIRecive();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DownloadService.ACTION_UPDATE);
		intentFilter.addAction(DownloadService.ACTION_DOWNLOADEND);
		intentFilter.addAction(DownloadService.ACTION_FINISHED);
		intentFilter.addAction(DownloadService.ACTION_START);
		mActivity.registerReceiver(mRecive, intentFilter);

		aRecive = new ApkRecive();
		IntentFilter apkInstallfilter = new IntentFilter();
		apkInstallfilter.addAction("PACKAGECHANGE");
		apkInstallfilter.addAction("ALIPAYCALLBACK");
		mActivity.registerReceiver(aRecive, apkInstallfilter);
	}
	public void unRegitReciver(){
		if (mRecive!=null&&aRecive!=null){
			mActivity.unregisterReceiver(mRecive);
			mActivity.unregisterReceiver(aRecive);
		}

	}
	public void init() {

		mSliderWeb = new WebView(mActivity);

		mDao = new ThreadDAOImple(mActivity);
		packageManager=mActivity.getPackageManager();
		mBoxIntent =new Intent(mActivity,DownloadService.class);
		installedList = packageManager.getInstalledPackages(0);



		mSliderWeb.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		mSliderWeb.getSettings().setUserAgentString(mSliderWeb.getSettings().getUserAgentString() + "hermesgameAndroid");
		mSliderWeb.getSettings().setJavaScriptEnabled(true);
		mSliderWeb.getSettings().setAllowFileAccess(true);
		mSliderWeb.getSettings().setDomStorageEnabled(true);
		mSliderWeb.getSettings().setSupportZoom(false);
		mSliderWeb.getSettings().setBuiltInZoomControls(false);
		mSliderWeb.addJavascriptInterface(new hermesgameJsInterface(), "JObject");
		mSliderWeb.setWebViewClient(new MyWebViewClient());

		// 禁止长按复制
		/*
		 * mSliderWeb.setOnLongClickListener(new View.OnLongClickListener() {
		 * 
		 * @Override public boolean onLongClick(View v) { return true; } });
		 */
		userInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		String authToken = "";
		if (userInfo != null) {
			authToken = userInfo.getAuthtoken();
		}
		if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			screenType = "1";
		} else {
			screenType = "0";
		}
	/*	if (mActivity.getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			screenType = "1";
		} else {
			screenType = "0";
		}*/
		InitData aData = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);

		if (aData.getProductconfig().getUcentUrl() == null || aData.getProductconfig().getUcentUrl().equals("")) {
			// baseSliderURL = Constant.SLIDER;
			baseSliderURL = Constant.HOST + "/userCenter/play/";
			Log.d("hermesgame", "baseSliderURL from ip:" + baseSliderURL);
		} else {
			//去除拼接userCenter0724
			baseSliderURL = aData.getProductconfig().getUcentUrl() ;
			if (!baseSliderURL.contains("userCenter")){
				baseSliderURL= aData.getProductconfig().getUcentUrl()+"/userCenter/play/" ;
			}
			Log.d("hermesgame", "baseSliderURL from dashbord:" + baseSliderURL);
			//以下链接测试竖屏全屏
		}

		sliderURL = baseSliderURL + "?authToken=" + authToken + "&screenType=" + screenType + "&deviceId="
				+ QGSdkUtils.getDeviceID(mActivity) + "&imei=" + QGSdkUtils.getImei(mActivity) + "&productCode="
				+ Constant.PRODUCT_ID + "&channelCode=" + Constant.CHANNEL_ID;
		Log.d("hermesgame", "sliderURL : " + sliderURL);
		QGSdkUtils.saveString(mActivity,"screenType","");
		QGSdkUtils.saveString(mActivity,"windowRate","");
		QGSdkUtils.saveString(mActivity,"barColor","");
		//sliderURL=test;
		Log.d("hermesgame", "Agent : " + 	mSliderWeb.getSettings().getUserAgentString());
//sliderURL="http://10.0.22.65:84/userCenter/play?screenType=1&deviceId=H5Browser&productCode=8cf0c4ea388c7d53435c1e361949f154&authToken=@112@160@171@135@105@103@110@166@132@138@171@140@134@153@170@125@138@151@163@134@126@137@156@149@134@158@154@163@117@107@157@163@175@160@171@156@136@135@155@139@104@129@132@128@151@141@172@155@123@136@164@168@167@128@131@116@130@124@113@162@99@123@151@159@105@159@178@160@130@159@104@129@176@138@139@127@152@103@143@115@147@96@168@169@129@163@158@101@114@170@147@142@167@126@110@135@168@170@111@176@154@125@96@126@125@143@144@153@132@123@127@100@144@142@165@128@132@130@102@139@135@174@143@145@146@109@118@99@110@102@106@152@92@105@132@152@107@108@130@103@118@173@155@120@169@118@179@174@155@102@137@155@128@128@141@121@103@140@134@171@168@123@155@140@105@171@128@92@164@131@178@131@139@121@133@131@166@108@122@174@163@154@165@105@145@124@147@132@149@143@153@164@177@172@170@144@161@158@155@163@166@106@104@122@125@143@164@133@156@158@167@175@125@119@114@110";
		mSliderWeb.loadUrl(sliderURL);
		//mSliderWeb.loadUrl("http://10.0.18.52/testajax.html");
	}
	public void setAction(String action){
		mAction=action;
	}
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void show(LinearLayout successsLayout, LinearLayout failLayout) {

		success = successsLayout;
		fail = failLayout;
		String urls[]=sliderURL.split("[?]");
		String urlParams;

		if (urls.length>1){
			urlParams=urls[1];
			String ss[]=urlParams.split("&");
			for (int i=0;i<ss.length;i++){

				if (ss[i].contains("initScreenSet")){

					try{
						String screenparam[]=ss[i].split("=")[1].split("\\|");
						if (screenparam[0].equals("landscape")){
							resetScreen(mActivity,"1",Double.valueOf(screenparam[1]));
						}else{
							resetScreen(mActivity,"0",Double.valueOf(screenparam[1]));
						}

					}catch (Exception e){
						e.printStackTrace();
						Log.e("hermesgame","initScreenSet Exception: "+e.toString());
					}
				}
			}
		}

		if (isErro || !QGSdkUtils.isNetworkAvailable(mActivity)) {
			failLayout.setVisibility(View.VISIBLE);
		} else {
			//展示上次的界面   初次展示要还原为默认比例  isShowed=false
			if (!QGSdkUtils.getString(mActivity,"screenType").equals("")&&isShowed){
				String screenType=QGSdkUtils.getString(mActivity,"screenType");
				String windowRate=QGSdkUtils.getString(mActivity,"windowRate");
				Log.e("hermesgame","show  screenType： "+screenType+"  windowRate: "+windowRate+"  barColor: "+QGSdkUtils.getString(mActivity,"barColor"));
				//只处理竖屏全屏的保存状态
				if (screenType.equals("0")&&windowRate.equals("1.0")){
					//转为竖屏全屏  隐藏空白页面也关闭按钮
					blankPart.setVisibility(View.GONE);
					rightPart.setVisibility(View.GONE);

					if (mActivity.getResources().getConfiguration().orientation==1){
						//竖-->竖
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
									layout_slider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
									success.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
									mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
									setHideVirtualKey(mActivity.getWindow());
									mActivity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
										@Override
										public void onSystemUiVisibilityChange(int visibility) {
											if (mActivity.getResources().getConfiguration().orientation==1){
												setHideVirtualKey(mActivity.getWindow());
											}
										}
									});


							}
						});
					}else{
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//横-》竖    判断转为全屏时  需要兼容转屏时的虚拟键冒出来
								mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
								success.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
								mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
								setHideVirtualKey(mActivity.getWindow());
								mActivity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
										@Override
										public void onSystemUiVisibilityChange(int visibility) {
											if (mActivity.getResources().getConfiguration().orientation==1){
												setHideVirtualKey(mActivity.getWindow());
											}
										}
									});
							}
						});
					}
				}
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			mSliderWeb.setLayoutParams(params);
			successsLayout.removeAllViews();
			successsLayout.addView(mSliderWeb);

			isShowing = true;
			isShowed=true;

			if (mAction!=null){
				try {
					mSliderWeb.evaluateJavascript("javascript:clickToRedpk()", new ValueCallback<String>() {

						@Override
						public void onReceiveValue(String value) {
							Log.d("hermesgame","onReceiveValue "+value);
						}
					});
					setAction(null);
				}catch (Exception e){

				}
			}
			else{
				Log.e("hermesgame", "GameSliderBarActivityV2 action is null");
			}
		}
	}

	/**
	 *
	 * @param v web展示View
	 * @param v2 空白part
	 * @param v3 右边关闭按钮区域
	 * @param v4 整个个人中心区域
	 */
 	public void initOtherView(Activity gameSliderBarActivityV2,LinearLayout v, FrameLayout v2, RelativeLayout v3,LinearLayout v4){
		sliderBarActivityV2=gameSliderBarActivityV2;
		webPart=v;
		blankPart=v2;
		rightPart=v3;
		layout_slider=v4;
 	}
	public void retry() {
		QGSdkUtils.saveString(mActivity,"screenType","");
		QGSdkUtils.saveString(mActivity,"windowRate","");
		QGSdkUtils.saveString(mActivity,"barColor","");
		isErro = false;
		isRetry = true;
		mSliderWeb.reload();
	}
	public void hide(LinearLayout layout) {
		//userCenterClosed();
		layout.removeAllViews();
		isShowing = false;
		isRetry = false;
	}

	/**
	 *隐藏整个Activity
	 */
	public void  destorySliderBar(){
		if (sliderBarActivityV2!=null){
			sliderBarActivityV2.finish();
		}
	}

/*	public void reload() {
		reloadPage();
	}*/
	public void reloadWebView(){
		mSliderWeb.reload();
	}


	class hermesgameJsInterface {
		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		@JavascriptInterface
		public String callAndroidFunction(String json) {
			Log.d("hermesgame", "callAndroidFunction: " + json);
			String action="";
			try {
				JSONObject jObject = new JSONObject(json);
				// JSONObject params = jObject.getJSONObject("params");
				// JS的行为
				 action = jObject.optString("action");
				try {
					params = jObject.getJSONObject("params");
				} catch (Exception e) {
					params = null;
				}

				Intent intent = new Intent();
				Bundle bundle = new Bundle();

				if (action.equalsIgnoreCase("showChangePassword")) {
					bundle.putString("from", "slider_modify");
					intent.putExtras(bundle);
					intent.setClass(mActivity, TempActivty.class);
					mActivity.startActivityForResult(intent, 10001);
					return "";
				}

				else if(action.equalsIgnoreCase("resetPaypassword")){
					bundle.putString("from", "slider_resetPayPassWord");
					intent.putExtras(bundle);
					intent.setClass(mActivity, TempActivty.class);
					mActivity.startActivityForResult(intent, 10001);
					return "";
				}//resetScreenOrientation
				else if(action.equalsIgnoreCase("resetScreenOrientation")){
					final String screenOrientation = params.getString("screenOrientation");
					final Double rate =Double.valueOf(params.getString("windowRate")) ;
					//存储
					QGSdkUtils.saveString(mActivity,"screenType",screenOrientation);
					QGSdkUtils.saveString(mActivity,"windowRate",""+rate);
					resetScreen(mActivity,screenOrientation,rate);
					return "";
				}
				else if (action.equalsIgnoreCase("setUsername")) {
					bundle.putString("from", "slider_setName");
					intent.putExtras(bundle);
					intent.setClass(mActivity, TempActivty.class);
					mActivity.startActivityForResult(intent, 10001);
					return "";
				} else if (action.equalsIgnoreCase("showBindPhone")) {
					bundle.putString("from", "slider_bind");
					intent.putExtras(bundle);
					intent.setClass(mActivity, TempActivty.class);
					mActivity.startActivityForResult(intent, 10001);
					return "";
				} else if (action.equalsIgnoreCase("showUnBindPhone")) {
					showUnBindView(mActivity);
					return "";

				} else if (action.equalsIgnoreCase("showTrueName") || action.equalsIgnoreCase("showBindIDCard")) {
					bundle.putString("from", "slider_cert");
					intent.putExtras(bundle);
					intent.setClass(mActivity, TempActivty.class);
					mActivity.startActivityForResult(intent, 10001);
					return "";
				}
				else if (action.equalsIgnoreCase("showSwitchAccount")) {
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							LoginManager.getInstance().logout(mActivity);
						}
					});
					mActivity.finish();
					return "";
				}
				else if (action.equalsIgnoreCase("showForgetPassword")) {
					if (QGConfig.isSupportPhone()) {
						bundle.putString("from", "slider_find");
						intent.putExtras(bundle);
						intent.setClass(mActivity, TempActivty.class);
						mActivity.startActivityForResult(intent, 10001);
					} else {
						ToastGame.makeText(mActivity, "暂不支持手机登陆,所以无法找回密码", Toast.LENGTH_LONG).show();
					}
					return "";
				} else if (action.equalsIgnoreCase("showServiceIM")) {
					if (QGConfig.isSupportIM()) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								InitManager.getInstance().showCustomService(mActivity);
							}
						});

					} else {
						ToastGame.makeText(mActivity, "暂不支持IM客服", Toast.LENGTH_SHORT).show();
					}
					return "";
				} else if (action.equalsIgnoreCase("getPackageName")) {
					getMyPackageName();
					return "";
				} else if (action.equalsIgnoreCase("getVersionNo")) {
					getGameVersion();
					return "";
				} else if (action.equalsIgnoreCase("getVersionInfo")) {
					getVersionInfo();
					return "";
				} else if (action.equalsIgnoreCase("exitApp")) {
					DBHelper.getInstance(mActivity).getReadableDatabase().close();
					mActivity.finish();
					System.exit(0);
					return "";

				} else if (action.equalsIgnoreCase("isSupportService")) {
					isSupportService();
					return "";
				} else if (action.equalsIgnoreCase("selectImage")) {
					// JSONObject params = jObject.getJSONObject("params");
					//屏蔽 20200603  艾游需求
					type = params.getString("type");
					rate = params.getString("rate");
					maxNum = params.getString("maxSelectNum");
					getImage();
					return "";
				} else if (action.equalsIgnoreCase("closeUserCenter")) {
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (mSliderWeb.getUrl().startsWith(baseSliderURL)) {

								mActivity.finish();
							} else {
								mSliderWeb.loadUrl(sliderURL);
								mActivity.finish();
							}
						}
					});
					return "";
				} else if (action.equalsIgnoreCase("destoryUserCenter")) {
					return "";
				} else if (action.equalsIgnoreCase("onBackButtonClick")) {
					if (isShowing) {
						mActivity.finish();
					} else {

					}
					return "";
				} else if (action.equalsIgnoreCase("setUserCenterSize")) {
					return "";
				} else if (action.equalsIgnoreCase("copyText")) {
					try {
						// JSONObject params = jObject.getJSONObject("params");
						String content = params.getString("content");
						clipText(content);
					} catch (Exception e) {
						Log.e("hermesgame", "copyText  Exception: " + e.toString());
					}
					return "";
				}

			/*	else if(action.equalsIgnoreCase("resetWindowParams")){
					String rate = params.getString("windowRate");
					success.setLayoutParams(new LinearLayout.LayoutParams((int) (mActivity.getWindowManager().getDefaultDisplay().getWidth() * 0.7), ViewGroup.LayoutParams.MATCH_PARENT));
				}*/



				//以下为游戏盒子action
				//下载
				else if(action.equals("downloadApk")){
					JSONObject data = jObject.getJSONObject("data");
					String packageName = data.getString("packageName");
					String downUrl = data.getString("downUrl");
					String icon = data.getString("icon");
					String showName = data.getString("showName");
					return downloadApk(mActivity,packageName,downUrl,icon,showName);
				}else if (action.equals("QQ_joinGroup")){
					String key=params.getString("key");
					joinQQGroup(mActivity,key);

					//followWX(mActivity,"");
					return "";
				}else if (action.equals("QQ_joinNum")){
					String key=params.getString("key");
					contactQQ(mActivity,key);

					//followWX(mActivity,"");
					return "";
				}
				else if (action.equals("wechatFollow")){

				}else if(action.equals("shareToWX")){
					String icon=params.getString("icon");
					String title=params.getString("title");
					String desc=params.getString("desc");
					String url=params.getString("url");
					sharedToWx(mActivity,icon,title,desc,url);
					return "";
				}
				else	if(action.equals("startAppTask")){
					//查询所有未完成的任务
					List<ThreadInfo> unEndTask = mDao.queryThreadUnEnd();

					for(int i = 0 ; i < unEndTask.size();i++){

						String packageName = unEndTask.get(i).getPackageName();
						String downUrl = unEndTask.get(i).getUrl();
						String icon = unEndTask.get(i).getIcon();
						String showName = unEndTask.get(i).getShowName();
						return downloadApk(mActivity,packageName,downUrl,icon,showName);
					}
				}

				else if(action.equals("pauseDownloadApk")){

					JSONObject data = jObject.getJSONObject("data");
					String packageName = data.getString("packageName");
					String downUrl = data.getString("downUrl");
					String icon="";
					String showName="";
					if (data.has("icon")){
						icon = data.getString("icon");
					}
					if (data.has("showName")){
						showName = data.getString("showName");
					}
					return pauseDownloadApk(mActivity,packageName,downUrl,icon,showName);
				}

				else	if(action.equals("installApk")){
					JSONObject data = jObject.getJSONObject("data");
					String packageName = data.getString("packageName");
					String downUrl = data.getString("downUrl");
					return installApk(packageName,downUrl);
				}

				else	if(action.equals("getRuningDownTask")){

					return getRuningDownTask();
				}

				else	if(action.equals("isApkInstall")){
					JSONObject data = jObject.getJSONObject("data");
					String packageName = data.getString("packageName");
					boolean checkInstall = checkAppInstalled(mActivity, packageName);
					return checkInstall ? "1" : "0";
				}



				else	if(action.equals("getAppDownloadStates")){
					//0 未下载   1下载中 2下载完成 3已安装
					JSONObject data = jObject.getJSONObject("data");
					String downUrl = data.getString("downUrl");

					List<ThreadInfo> tinfo = mDao.queryThreads(downUrl);

					if(tinfo.size() == 0){
						return "{\"status\":0,\"rate\":0}";
					}

					int isDownEnd = tinfo.get(0).getIsDownloaded();
					File f = new File(tinfo.get(0).getSavePath());
					if(1 == isDownEnd && f.exists()){
						return "{\"status\":2,\"rate\":0}";
					}else if(1 == isDownEnd && !f.exists()){
						return "{\"status\":0,\"rate\":0}";
					}else{
						int nowFinish = tinfo.get(0).getFinished();
						int allBytes = tinfo.get(0).getEnd();
						float downLoadRate = ((float)nowFinish / allBytes)*100;
						Log.i("Test", String.format("%.2f", downLoadRate));
						return "{\"status\":1,\"rate\":\""+ String.format("%.2f", downLoadRate) +"\"}";
					}
				}

				else	if(action.equals("openApp")){
					JSONObject data = jObject.getJSONObject("data");
					String packageName = data.getString("packageName");
					PackageManager packageManager = mActivity.getPackageManager();
					Intent mIntent=new Intent();
					mIntent =packageManager.getLaunchIntentForPackage(packageName);
					if(mIntent!=null){
						mActivity.startActivity(mIntent);
					}
					return "";
				}

				else	if(action.equals("clearCache")){
					//v.clearCache(true);
					mActivity.deleteDatabase("webview.db");
					mActivity.deleteDatabase("webviewCache.db");
					File appCacheDir = new File(mActivity.getExternalCacheDir().getAbsolutePath() + "/webcache");
					File webviewCacheDir = new File(mActivity.getCacheDir().getAbsolutePath()+"/webviewCache");

					//删除webview 缓存目录
					if(webviewCacheDir.exists()){
						deleteFile(webviewCacheDir);
					}
					//删除webview 缓存 缓存目录
					if(appCacheDir.exists()){
						deleteFile(appCacheDir);
					}
					return "1";
				}

				else	if(action.equals("deleteDownload")){
					JSONObject data = jObject.getJSONObject("data");
					String id = data.getString("id");
					mDao.deleteThreadById(id);
					return "";
				}

				//获取网络类型
				else	if(action.equals("getNetType")){
					String netType = Integer.toString(getNetWorkStatus(mActivity));
					return netType;
				}

				else	if(action.equals("getVersion")){
					String nowVersionCode = getLocalVersion(mActivity);
					return nowVersionCode;
				}
				else	if(action.equals("dopay")){
					String url=new String(Base64.decode(params.getString("payParams"),Base64.DEFAULT));
					Log.d("hermesgame","");
					String payType=params.getString("payType");
					QGPayManager.getInstance().showPayViewFromSlider(mActivity,url,Integer.valueOf(payType));
					//	QGPayManager.getInstance().showPayViewFromSlider(mActivity,url,1);
					return "";
				}
				else	if(action.equals("setActionBarColor")   ){
					final String color=params.getString("color");
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mActivity.getWindow().setStatusBarColor(Color.parseColor(color));
							QGSdkUtils.saveString(mActivity,"barColor",color);
						}
					});

				}
				else	if (action.equals("callAliPay")) {
					JSONObject data = params.getJSONObject("data");
					String url = data.getString("url");
					Log.e("gameBox","callAliPay: "+url);
					final PayTask task = new PayTask(mActivity);
					boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
						@Override
						public void onPayResult(final H5PayResultModel result) {
							Log.e("gameBox","callAliPay  onPayResult "+result.getResultCode());
							final	String url = result.getReturnUrl();
							final String code = result.getResultCode();

						/*	Intent i = new Intent("ALIPAYCALLBACK");
							i.putExtra("code", code);
							i.putExtra("url", url);
							mActivity.sendBroadcast(i);*/
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								callJsAlipayCallback(url,code);
							}
						});


						}
					});

					if (!isIntercepted) {
						return "{\"status\":0,\"message\":\"can not call alipay sdk\"}";
					} else {
						return "{\"status\":1,\"message\":\"can not call alipay sdk\"}";
					}

				}
				else	if (action.equals("callWxPay")) {
					JSONObject data = params.getJSONObject("data");
					String url = data.getString("url");
					QGPayManager.getInstance().showPayViewFromSlider(mActivity,url,88);
				}
				else	if (action.equals("setWXReferer")){
					String referer=params.getString("referer");
					businessUrl=referer;
				}
				else	if (action.equals("doInputPayPass")){
					Intent doInputPayPass=new Intent();
					double amount=params.getDouble("amount");
					String banlance=params.getString("userWalletAmount");
					//double amount=Double.valueOf();
					doInputPayPass.setClass(mActivity, QGSdkCoinsPayActivity.class);
					doInputPayPass.putExtra("from","Slider");
					doInputPayPass.putExtra("amount",amount);
					doInputPayPass.putExtra("banlance",banlance);
					mActivity.startActivityForResult(doInputPayPass,10009);
				}else{
					return "no";
				}
			} catch (Exception e) {
				Log.e("hermesgame", "callAndroidFunction  Exception action: "+ action+"    "+ e.toString());
				return "";
			}
			return "";
		}
	}

	private class MyWebViewClient extends WebViewClient {
		@TargetApi(Build.VERSION_CODES.KITKAT)
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			Log.d("hermesgame", "GameSliderBarActivityV2 onPageFinished");
			if (isRetry) {
				show(success, fail);
			}

			if (businessUrl == null) {
				mSliderWeb.evaluateJavascript("javascript:getReferer()", new ValueCallback<String>() {

					@Override
					public void onReceiveValue(String referer) {
						Log.e("hermesgame","onReceiveValue referer:"+referer);
						businessUrl = referer;
						if (!TextUtils.isEmpty(referer)) {
							businessUrl = referer.substring(1, referer.length() - 1);
						}

						businessUrl += "&appid=" +Constant.PRODUCT_ID + "&platform=android";
						Log.d("hermesgame", "businessUrl: " + businessUrl);

					}
				});
			}
			if (url.contains("referer")) {
				String []aa=url.split("referer");
				tempRef=aa[1].substring(1,aa[1].length());

			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.e("hermesgame", "onReceivedError");
			isErro = true;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
		/*	if (url != null) {
				if (url.startsWith("http://")||url.startsWith("https://")||url.startsWith("weixin://")||url.startsWith("alipay://")){
					view.loadUrl(url);
				}
			}*/
			Log.d("hermesgame","shouldOverrideUrlLoading: "+url);

			if (url.startsWith("weixin://wap/pay?")) {
				try {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					mActivity.startActivityForResult(intent, 1);
				} catch (Exception e) {
					if (e instanceof ActivityNotFoundException) {
						Toast.makeText(mActivity,"请安装最新版微信客户端",Toast.LENGTH_LONG).show();
					} else {
						e.printStackTrace();
					}
				}
				return true;
			}
			if(url.startsWith("https://wx.tenpay.com")){
		/*		HashMap<String, String> map = new HashMap<String, String>();
				if (tempRef!=null) {
					map.put("Referer", tempRef);
					Log.e("hermesgame","Referer1:"+map.get("Referer"));
				}else {
					map.put("Referer", businessUrl);
					Log.e("hermesgame","Referer2:"+map.get("Referer"));
				}
56465444
				WebSettings s = view.getSettings();
				s.setJavaScriptEnabled(true);
				view.loadUrl(url, map);*/
				QGPayManager.getInstance().showPayViewFromSlider(mActivity,url,88);
				return true;
			}
			if (url.startsWith("alipays:") || url.startsWith("alipay") || url.startsWith("https://openapi.")) {
				Log.e("hermesgame", "启动支付宝");
				try {
					mActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
				} catch (Exception e) {
					new android.app.AlertDialog.Builder(mActivity).setMessage("未检测到支付宝客户端，请安装后重试。")
							.setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									Uri alipayUrl = Uri.parse("https://d.alipay.com");
									mActivity.startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
								}
							}).setNegativeButton("取消", null).show();
				}
				return true;
			}	else {
				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

			try {
				String[] fileName = QGSdkUtils.getFileNamesArray(mActivity, "localsource");
				String[] urlLastFileName = url.split("/");
				if (fileName.length != 0) {
					for (int i = 0; i < fileName.length; i++) {
						if (urlLastFileName[urlLastFileName.length - 1].startsWith(fileName[i])) {
							if (fileName[i].contains(".png")) {
								return new WebResourceResponse("image/png", "utf-8", mActivity.getBaseContext()
										.getAssets().open("localsource/" + fileName[i]));
							} else if (fileName[i].contains(".css")) {
								return new WebResourceResponse("text/css", "utf-8", mActivity.getBaseContext()
										.getAssets().open("localsource/" + fileName[i]));
							} else if (fileName[i].contains(".js")) {
								return new WebResourceResponse("application/x-javascript", "utf-8", mActivity
										.getBaseContext().getAssets().open("localsource/" + fileName[i]));
							}
							else{
								Log.e("hermesgame.slider", "localsource资源 no:"+urlLastFileName[urlLastFileName.length - 1]);
							}
						}
					}
				} else {
					Log.e("hermesgame", "未导入assets资源");
				}
			} catch (Exception e) {

			}
			return super.shouldInterceptRequest(view, url);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

		}
	}

	public void onPhoneBackButtonClick() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "onBackButtonClick");
			jsonObject.put("params", params);
		} catch (JSONException e) {
			Log.e("hermesgame", "onBackButtonClick Exception: " + e.toString());
		}
		callJsFunc(jsonObject);
	}

	public void getImage() {
		Intent intent = new Intent();
		intent.setClass(mActivity, PhotoPickerActivity.class);
		intent.putExtra("maxNum", Integer.valueOf(maxNum));
		mActivity.startActivityForResult(intent, 10002);
	}

	@SuppressLint("NewApi")
	public void uploadImagePath(Intent data) {
		try {
			String[] path = null;
			JSONArray jsonArray = null;
			if (data != null) {
				path = new String[data.getStringArrayListExtra("photo_picker_photo_url").size()];
				for (int i = 0; i < data.getStringArrayListExtra("photo_picker_photo_url").size(); i++) {
					path[i] = QGSdkUtils.imgToBase64(data.getStringArrayListExtra("photo_picker_photo_url").get(i),
							Integer.valueOf(rate));
				}
				jsonArray = new JSONArray(path);
			} else {
				jsonArray = new JSONArray();
			}

			JSONObject jsonObject = new JSONObject();

			JSONObject params = new JSONObject();
			params.put("type", type);
			params.put("path", jsonArray);
			jsonObject.put("action", "selectImage");
			jsonObject.put("params", params);
			Log.e("hermesgame", "uploadImagePath jsonObject: " + jsonObject.toString());
			callJsFunc(jsonObject);
		} catch (JSONException e) {
			Log.e("hermesgame", "uploadImagePath Exception: " + e.toString());
		}

	}

	public void reloadPage() {
		// 刷新界面
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "reloadUserCenterHome");
			jsonObject.put("params", params);
		} catch (JSONException e) {
			Log.e("hermesgame", "reloadPage Exception: " + e.toString());
		}
		callJsFunc(jsonObject);
	}

	public void getMyPackageName() {
		// 获取包名
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "getPackageName");
			params.put("packName", mActivity.getPackageName());
			jsonObject.put("params", params);
		} catch (JSONException e) {
			Log.e("hermesgame", "getMyPackageName Exception: " + e.toString());
		}
		callJsFunc(jsonObject);
	}

	public void getGameVersion() {
		// 获取版号
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "getVersionNo");
			params.put("gameVersion", Constant.VERSION_CODE);
			jsonObject.put("params", params);
		} catch (JSONException e) {
			Log.e("hermesgame", "getGameVersion Exception: " + e.toString());
		}
		callJsFunc(jsonObject);
	}

	public void getVersionInfo() {
		// 获取版号
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "getVersionInfo");
			params.put("gameVersionCode", Constant.VERSION_CODE);
			params.put("gameVersionName", Constant.VERSION_NAME);
			params.put("sdkVersionName", Constant.SDK_VERSION);
			params.put("sdkVersionCode", Constant.SDK_VERSION_NAME);
			jsonObject.put("params", params);
		} catch (JSONException e) {
			Log.e("hermesgame", "getGameVersion Exception: " + e.toString());
		}
		callJsFunc(jsonObject);
	}


	public void resetMobVer(String phoneNum,boolean isBind) {
		//重置手机绑定状态
		JSONObject jsonObject = new JSONObject();
		 HashMap<String, Object> mParams=new HashMap<String, Object>();
		try {
			jsonObject.put("action", "resetMobVer");
			mParams.put("mobile", phoneNum);
			if (isBind){
				mParams.put("doAct", "bind");
			}else{
				mParams.put("doAct", "unbind");
			}

			jsonObject.put("params", mParams);
		} catch (JSONException e) {
			Log.e("hermesgame", "resetMobVer Exception: " + e.toString());
		}
		callJsFunc(jsonObject);
		retry();
	}
	public void clipText(String text) {
		// 获取剪贴板管理器：
		ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(mActivity.CLIPBOARD_SERVICE);
		// 创建普通字符型ClipData
		ClipData mClipData = ClipData.newPlainText("Label", text);
		// 将ClipData内容放到系统剪贴板里。
		cm.setPrimaryClip(mClipData);

		// Toast.makeText(GameSliderBarActivityV2.this, "复制成功",
		// Toast.LENGTH_SHORT).show();
	}

	public void isSupportService() {
		String isSupport = "";
		if (QGConfig.isSupportIM()) {
			isSupport = "1";
		} else {
			isSupport = "0";
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "isSupportService");
			params.put("isSupportService", isSupport);
			jsonObject.put("params", params);
		} catch (JSONException e) {
			Log.e("hermesgame", "getGameVersion Exception: " + e.toString());
		}
		callJsFunc(jsonObject);
	}

	public void userCenterClosed() {


		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "userCenterClosed");
		} catch (JSONException e) {
			Log.e("hermesgame", "getGameVersion Exception: " + e.toString());
		}
		callJsFunc(jsonObject);
	}
	public void  completePayPassword(String pass){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "completePayPassword");
			params.put("payPass", QGSdkUtils.getMD5Str(pass));
			jsonObject.put("params", params);
		} catch (JSONException e) {
			Log.e("hermesgame", "completePayPassword Exception: " + e.toString());
		}
		callJsFunc(jsonObject);
	}


	/*
	 * 调用JS函数的接口
	 */
	private void callJsFunc(final JSONObject jsonObject) {
		try {
			// 所有的webview的方法必须在一个线程调用，否则会出现异常
			Log.d("hermesgame", "GameSliderBarActivityV2 callJsFunc" + jsonObject.toString());
			mSliderWeb.post(new Runnable() {
				@Override
				public void run() {
					String jsonMsg = Base64.encodeToString(jsonObject.toString().getBytes(), Base64.NO_WRAP);
					mSliderWeb.loadUrl("javascript:onNativeCallback('" + jsonMsg + "')");
				}
			});

		} catch (Exception e) {
			Log.e("hermesgame", "GameSliderBarActivityV2  调用JS方法出现异常：" + e.toString());

		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void callJsAlipayCallback(String code, String returnUrl) {
		String javascriptString = "onAliPayCallback(" + code + ",\"" + returnUrl + "\")";
		if (Build.VERSION.SDK_INT < 18) {
			mSliderWeb.loadUrl("javascript:" + javascriptString);
		} else {
			mSliderWeb.evaluateJavascript(javascriptString, new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
				}
			});
		}
	}

	public void destory() {
		isRetry = false;
		sliderBarV2Manager = null;
	}



	public String downloadApk(Activity activity,String packageName,String downUrl,String icon,String showName){
		// check权限
		if ((ContextCompat.checkSelfPermission(activity,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
		) {
			// 没有 ， 申请权限 权限数组
			ActivityCompat.requestPermissions(activity, new String[]{
					Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
		}
		List<ThreadInfo> urlDbInfo = checkIsDownloaded(downUrl);
		if(urlDbInfo.size() > 0){
			ThreadInfo firstLog = urlDbInfo.get(0);
			String fileSavePath = firstLog.getSavePath();

			if(fileSavePath != null){
				File f = new File(fileSavePath);
				//调用安装
				if(firstLog.getIsDownloaded() == 1 && f.exists()){
					callJsDownSuccess(urlDbInfo.get(0));
					return "{status:true}";
				}

				//如果文件不存在
				if(! f.exists()){
					mDao.deleteThread(downUrl);
				}
			}

		}
		String fileLastName = this.getfileName(downUrl);
		fileInfo = new FileInfo(0, downUrl, fileLastName,packageName, 0, 0,icon,showName);
		mBoxIntent.setAction(DownloadService.ACTION_START);
		mBoxIntent.putExtra("fileInfo", fileInfo);
		activity.startService(mBoxIntent);
		return "100";
	}
	/****************
	 *
	 * 发起添加群流程。群号：渠道接入小分队Quick(562856728) 的 key 为： -gkNnzvM4ONql3CDmFo4_x9Ahz_Zbefk
	 * 调用 joinQQGroup(-gkNnzvM4ONql3CDmFo4_x9Ahz_Zbefk) 即可发起手Q客户端申请加群 渠道接入小分队Quick(562856728)
	 *
	 * @param key 由官网生成的key
	 * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	 ******************/
	public boolean joinQQGroup(Activity activity,String key) {
		if (checkApkExist(activity,"com.tencent.mobileqq")){
			Intent intent = new Intent();
			intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
			// 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			try {
				mActivity.startActivity(intent);
				return true;
			} catch (Exception e) {
				// 未安装手Q或安装的版本不支持
				return false;
			}
		}else{
			return false;
		}
	}
	public static void contactQQ(final Context context, String qq) {
		try {
			final String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
			//uin是发送过去的qq号码
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				}
			});

		} catch (Exception e) {
			Log.e("hermesgame", "GameSliderBarActivityV2 contactQQ异常：" + e.toString());
		}
	}
	public void sharedToWx(Activity activity,String iconUrl,String title,String desc,String url){
		//初始化一个 WXTextObject 对象，填写分享的文本内容
		IWXAPI api = WXAPIFactory.createWXAPI(activity,  QGConfig.getWXAppId(), false);
		api.registerApp( QGConfig.getWXAppId());
		// 初始化一个WXWebpageObject对象
		WXWebpageObject webpageObject = new WXWebpageObject();
		// 填写网页的url
		webpageObject.webpageUrl =url;

		// 用WXWebpageObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage(webpageObject);
		// 填写网页标题、描述、位图
		msg.title = title;
		msg.description =desc;
		// 如果没有位图，可以传null，会显示默认的图片
		msg.setThumbImage(getBitmap(iconUrl));


		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		// transaction用于唯一标识一个请求（可自定义）
		req.transaction = "webpage";
		// 上文的WXMediaMessage对象
		req.message = msg;
		// SendMessageToWX.Req.WXSceneSession是分享到好友会话
		// SendMessageToWX.Req.WXSceneTimeline是分享到朋友圈
		req.scene = SendMessageToWX.Req.WXSceneTimeline;

		// 向微信发送请求
		api.sendReq(req);


	}

	/*public boolean followWX(Activity activity,String url){

	*//*	Intent i = new Intent(Intent.ACTION_VIEW); //声明要打开另一个VIEW.

		 //url  这是你公共帐号的二维码的实际内容。可以用扫描软件扫一下就得到了。这是我的公共帐号地址。

		i.setData(Uri.parse("http://weixin.qq.com/r/hSi_pqDEFP1rreTF932X")); //设置要传递的内容。

		i.setPackage("com.tencent.mm"); //直接制定要发送到的程序的包名。也可以不制定。就会弹出程序选择器让你手动选木程序。

		i.putExtra(Intent.EXTRA_SUBJECT,"Share");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		activity.startActivity(i); //当然要在Activity界面 调用了。*//*
		String appId = "wxce12bb34938c402a";//开发者平台ID
		IWXAPI api = WXAPIFactory.createWXAPI(activity, appId, false);

		if (api.isWXAppInstalled()) {
			Jump
			JumpToBizProfile.Req req = new JumpToBizProfile.Req();
			req.toUserName = "gh_4241371646be"; // 公众号原始ID
			req.extMsg = "";
			req.profileType = JumpToBizProfile.JUMP_TO_NORMAL_BIZ_PROFILE; // 普通公众号
			api.sendReq(req);
		}else{
			Toast.makeText(activity, "微信未安装", Toast.LENGTH_SHORT).show();
		}
		return true;
	}*/

	private boolean joinWXGroup(Activity context) {

		if (checkApkExist(context, "com.tencent.mm")) {
			Intent intent = new Intent();
			ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(cmp);
			context.startActivity(intent);
			return true;
		} else {
			ToastGame.makeText(context,"本机未安装微信",Toast.LENGTH_SHORT);
			return false;
		}
	}


		public boolean checkApkExist(Context context, String packageName) {
			if (packageName == null || "".equals(packageName))
				return false;
			try {
				ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
						PackageManager.GET_UNINSTALLED_PACKAGES);
				return true;
			} catch (PackageManager.NameNotFoundException e) {
				return false;
			}
		}



	//check the url is downloaded
	public List<ThreadInfo> checkIsDownloaded(String downUrl){

		List<ThreadInfo> threadList= mDao.queryThreads(downUrl);
		return threadList;
	}


	private String getfileName(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void callJsDownSuccess(String packageName, String url){

		String javascriptString = "onDownLoadSuccess(\""+packageName+"\",\"" + url + "\")";
		if (Build.VERSION.SDK_INT < 18) {
			mSliderWeb.loadUrl("javascript:" + javascriptString);
		}else{
			mSliderWeb.evaluateJavascript(javascriptString, new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
				}
			});

		}
	}
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void callJsDownSuccess(ThreadInfo t){

		String javascriptString = "onDownLoadSuccess(\""+t.getPackageName()+"\",\"" + t.getUrl() + "\")";
		if (Build.VERSION.SDK_INT < 18) {
			mSliderWeb.loadUrl("javascript:" + javascriptString);
		}else{
			mSliderWeb.evaluateJavascript(javascriptString, new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
				}
			});
		}
	}

	public String pauseDownloadApk(Activity activity,String packageName, String downUrl, String icon, String showName) {

		List<ThreadInfo> urlDbInfo = checkIsDownloaded(downUrl);
		if (urlDbInfo.size() <= 0) {
			Log.e("hermesgame","urlDbInfo.size() <= 0");
			return "0";
		}

		String fileLastName = this.getfileName(downUrl);
		fileInfo = new FileInfo(0, downUrl, packageName + ".apk", packageName, 0, 0, icon, showName);

		mBoxIntent.setAction(DownloadService.ACTION_STOP);
		mBoxIntent.putExtra("fileInfo", fileInfo);
		activity.startService(mBoxIntent);
		Log.e("hermesgame","return 100");
		return "100";
	}

	public void stopDownLoad(){
		mBoxIntent.setAction(DownloadService.ACTION_STOP);
		mActivity.startService(mBoxIntent);
	}


	public String installApk(String packageName,String downUrl){

		List<ThreadInfo> urlDbInfo = checkIsDownloaded(downUrl);
		if(urlDbInfo.size() > 0){

			ThreadInfo firstLog = urlDbInfo.get(0);
			String fileSavePath = firstLog.getSavePath();
			File f = new File(fileSavePath);

			//调用安装
			if(firstLog.getIsDownloaded() == 1 && f.exists()){
				installCallSystem(fileSavePath);
				return "{status:true}";
			}

			//如果文件不存在
			if(! f.exists()){
				this.mDao.deleteThread(downUrl);
				return "{status:false,message:\"package has not exists\"}";
			}

		}

		return "{status:false,message:\"package has not exists\"}";
	}



	private void installCallSystem(String filePath) {

		File apkFile = new File(filePath);

		boolean fileExist = apkFile.exists();

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= 24) {
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri contentUri = FileProvider.getUriForFile(mActivity, mActivity.getPackageName()+".qgfileprovider", apkFile);
			intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
		} else {
			intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
		}
		mActivity.startActivity(intent);
	}


	public String getRuningDownTask(){

		List<ThreadInfo> taskList = mDao.getRuningDownTask();
		String returnString = "[";
		for(int i = 0 ; i < taskList.size();i++){
			returnString += taskList.get(i).toString();
			if(i < taskList.size() - 1){
				returnString += ",";
			}
		}
		returnString += "]";

		return returnString;
	}
	private boolean checkAppInstalled(Context context,String pkgName) {
		if (pkgName== null || pkgName.isEmpty()) {
			return false;
		}
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if(packageInfo == null) {
			return false;
		} else {
			return true;//true为安装了，false为未安装
		}
	}
	class ApkRecive extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("PACKAGECHANGE")) {
				//重新获取应用
				installedList = packageManager.getInstalledPackages(0);
			}

		}

	}
	class UIRecive extends BroadcastReceiver {

		@TargetApi(Build.VERSION_CODES.KITKAT)
		@Override
		public void onReceive(Context context, Intent intent) {

			if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
				float finished = intent.getFloatExtra("finished", (float) 0.0);
				String packageName = intent.getStringExtra("packageName");

				String javascriptString = "onDownProcessChange(\""+packageName+"\"," + String.format("%.2f", finished) + ")";
				Log.d("hermesgame","onReceive: "+javascriptString);

				if (Build.VERSION.SDK_INT < 18) {
					mSliderWeb.loadUrl("javascript:" + javascriptString);
				}else{

					mSliderWeb.evaluateJavascript(javascriptString, new ValueCallback<String>() {
						@Override
						public void onReceiveValue(String value) {
							Log.d("hermesgame","onReceiveValue： "+value);
						}
					});
				}
			}else if (DownloadService.ACTION_DOWNLOADEND.equals(intent.getAction())) {

				String packageName = intent.getStringExtra("packageName");
				String url = intent.getStringExtra("downUrl");
				callJsDownSuccess(packageName,url);

			}else if (DownloadService.ACTION_FINISHED.equals(intent.getAction())){
				// 下载结束的时候
				//FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			}

		}

	}

	public void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		}
	}
	public int getNetWorkStatus(Context context) {
		int netWorkType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			int type = networkInfo.getType();

			if (type == ConnectivityManager.TYPE_WIFI) {
				netWorkType = 5;
			} else if (type == ConnectivityManager.TYPE_MOBILE) {
				netWorkType = getNetWorkClass(context);
			}
		}

		return netWorkType;
	}
	public int getNetWorkClass(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType()) {
			case TelephonyManager.NETWORK_TYPE_IDEN:
				return 2;
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				return 3;
			case TelephonyManager.NETWORK_TYPE_LTE:
				return 4;
			default:
				return 6;
		}
	}
	public String getLocalVersion(Context ctx) {
		int localVersion = 0;
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			localVersion = packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return Integer.toString(localVersion);
	}
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void callJsLoadsuccess(){
		String javascriptString = "onPageLoadSuccess()";
		if (Build.VERSION.SDK_INT < 18) {
			mSliderWeb.loadUrl("javascript:" + javascriptString);
		}else{
			mSliderWeb.evaluateJavascript(javascriptString, new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
				}
			});
		}

	}

	private void showStatusBar() {
		WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
		attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		mActivity.getWindow().setAttributes(attrs);
	}

	public Bitmap getBitmap(String s){
		Bitmap bitmap = null;
		try
		{
			URL url = new URL(s);
			bitmap = BitmapFactory.decodeStream(url.openStream());
		} catch (Exception e)
		{
// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}

	public void showUnBindView(Activity activity){
		final Intent intent = new Intent();
		final Bundle bundle = new Bundle();
		QGUserInfo mInfo=(QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		if (mInfo.getUserdata().getMobile()!=null&&!mInfo.getUserdata().getMobile().isEmpty()&&mInfo.getUserdata().getIsMbUser()==1){
			final AlertDialog alertDialog=new AlertDialog(activity,null,"提示","     "+activity.getString(QGSdkUtils.getResId(activity,"R.string.qg_unbind_mobile_tips")),"取消","下一步") {
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
					bundle.putString("from", "slider_mobile_bind_user");
					intent.putExtras(bundle);
					intent.setClass(mActivity, TempActivty.class);
					mActivity.startActivityForResult(intent, 10001);
					alertDialog.dismiss();
				}
			});
			alertDialog.show();
		}else{
			bundle.putString("from", "slider_unbind");
			intent.putExtras(bundle);
			intent.setClass(mActivity, TempActivty.class);
			mActivity.startActivityForResult(intent, 10001);
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void setHideVirtualKey(Window window) {
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
		if (Build.VERSION.SDK_INT >= 19) {
			uiOptions |= 0x00001000;
		} else {
			uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
		}

		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		if (!QGSdkUtils.getString(mActivity,"barColor").equals("")){
			window.setStatusBarColor(Color.parseColor(QGSdkUtils.getString(mActivity,"barColor")));
		}else {
			window.setStatusBarColor(Color.parseColor("#FFFFFF"));
		}
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.getDecorView().setSystemUiVisibility(uiOptions);

	}
	public void resetScreen(Activity activity,final String screenOrientation,final double rate){
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (rate==1){
					blankPart.setVisibility(View.GONE);
					rightPart.setVisibility(View.GONE);
				}else{
					blankPart.setVisibility(View.VISIBLE);
					rightPart.setVisibility(View.VISIBLE);
				}
				if (Integer.valueOf(screenOrientation)==1){
					//横-->横
					if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								success.setLayoutParams(new LinearLayout.LayoutParams((int)(QGSdkUtils.getPhoneWidthPixels(mActivity)*rate), ViewGroup.LayoutParams.MATCH_PARENT));
							}
						});
					}else {
						//竖-->横
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Log.e("hermesgame","竖-->横"+mActivity.getComponentName());
								mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
								mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

								success.setLayoutParams(new LinearLayout.LayoutParams((int)(QGSdkUtils.getPhoneHeightPixels(mActivity)*rate),LinearLayout.LayoutParams.MATCH_PARENT ));
							}
						});
					}

				}else if (Integer.valueOf(screenOrientation)==0){

					if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
						//竖-->竖
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (rate==1){
									layout_slider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
									success.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
									mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
									setHideVirtualKey(mActivity.getWindow());
									mActivity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
										@Override
										public void onSystemUiVisibilityChange(int visibility) {
											if (mActivity.getResources().getConfiguration().orientation==1){
												setHideVirtualKey(mActivity.getWindow());
											}
										}
									});
								}else{
									mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
									layout_slider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
									success.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (mActivity.getWindowManager().getDefaultDisplay().getHeight() * rate)));
								}

							}
						});
					}else{
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//横-》竖    判断转为全屏时  需要兼容转屏时的虚拟键冒出来
								mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
								if (rate==1){
									success.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
									mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
									setHideVirtualKey(mActivity.getWindow());
									mActivity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
										@Override
										public void onSystemUiVisibilityChange(int visibility) {
											if (mActivity.getResources().getConfiguration().orientation==1){
												setHideVirtualKey(mActivity.getWindow());
											}
										}
									});

								}else{
									success.setLayoutParams(new LinearLayout.LayoutParams(mActivity.getWindowManager().getDefaultDisplay().getHeight(), (int)(rate*mActivity.getWindowManager().getDefaultDisplay().getWidth())));
								}


							}
						});

					}
				}

			}
		});
	}


}
