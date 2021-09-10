package com.hermesgamesdk.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.hermesgamesdk.manager.ThirdManager;
import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class CpLoginActivity extends FragmentActivity {
	private WebView mWebView;
	private JSONObject params;
	//	private String loginUrl = "http://qkgamesdk.quickapi.net/test/demo";  //quick测试地址
	// private String loginUrl = "http://tcs.fengkuangtiyu.cn/module/expert/yuecaiLogin/login.html?client=quick";  //fkty 测试地址
	private String loginUrl= "http://t.fengkuangtiyu.cn/module/expert/yuecaiLogin/login.html?client=quick&productCode=";
	private String accessToken="123456789";
	private String callbackFunction;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(QGSdkUtils.getResId(this,
				"R.layout.qg_activity_weblogin"));
		mWebView = (WebView) findViewById(QGSdkUtils.getResId(this,
				"R.id.qg_webview_login"));
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		mWebView.getSettings().setJavaScriptEnabled(true);
		
		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mWebView.getSettings().setUseWideViewPort(true);
		

		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setDatabaseEnabled(true);
		String appCachePath = getApplicationContext().getCacheDir()
				.getAbsolutePath();
		mWebView.getSettings().setAppCachePath(appCachePath);
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.addJavascriptInterface(new hermesgameJsInterface(), "JObject");
		
		mWebView.loadUrl(loginUrl+Constant.PRODUCT_ID);
	}

	class hermesgameJsInterface {
		@JavascriptInterface
		public void callAndroidFunction(String json) {
			try {
				Log.e("hermesgame", "hermesgameJsInterface: " + json);
				JSONObject jObject = new JSONObject(json);

				String action = jObject.optString("action");
				try {
					params = jObject.getJSONObject("params");
				} catch (Exception e) {
					params = null;
				}
				if (action.equalsIgnoreCase("openWX")) {
					String wxAppid = params.getString("appId");
					String wxAppSec = params.getString("appSecret");
					 ThirdManager.getInstance().webWXAppId=wxAppid;
					 ThirdManager.getInstance().webWXSec=wxAppSec;
					callbackFunction = params.getString("callbackFunction");

					QGConfig.setWXAppSecret(wxAppSec);
					LoginManager.getInstance().initWXLogin(
							CpLoginActivity.this, wxAppid);
					ThirdManager.getInstance().wxLogin(CpLoginActivity.this,
							mLoginCallBack);

				} else if (action.equalsIgnoreCase("openQQ")) {
					 String qqAppid = params.getString("appId");
					 ThirdManager.getInstance().webQQAppId=qqAppid;
					callbackFunction = params.getString("callbackFunction");

					LoginManager.getInstance().initQQLogin(
							CpLoginActivity.this, qqAppid);
					ThirdManager.getInstance().qqLogin(CpLoginActivity.this,
							mLoginCallBack);
				} else if (action.equalsIgnoreCase("setOpenID")) {

					final String openid = params.getString("openid");
					CpLoginActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub.				
							ThirdManager.getInstance().thridLogin(Constant.OPEN_TYPE_CP, openid, accessToken);
							
						}
					});
				
				} else if (action.equalsIgnoreCase("closeWebView")) {

					setResult(Constant.CP_LOGIN_OK);
					CpLoginActivity.this.finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	



	ThirdManager.ThirdLoginCallBack mLoginCallBack = new ThirdManager.ThirdLoginCallBack() {

		@Override
		public void onLoginSuccessed(QGUserInfo bean) {

		}

		@Override
		public void onLoginFailed(int code, String msg) {
			try {
				Log.d("hermesgame", "ThirdLoginCallBack onLoginFailed: " + msg);
				JSONObject jsonObj = new JSONObject(msg);
				accessToken = jsonObj.getString("access_token");
				String openId = jsonObj.getString("openid");

				if (code == 1) {
					postQQMsg(openId, accessToken);
				} else if (code == 0) {
					postWXMsg(openId, accessToken);
				}
			} catch (Exception e) {
				Log.e("hermesgame",	"ThirdLoginCallBack onLoginFailed Exception : "+ e.toString());
			}

		}
	};

	public void postWXMsg(String appid, String wxSec) {
		// 获取包名
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "openWX");
			params.put("openid", appid);
			params.put("access_token", wxSec);
			jsonObject.put("params", params);
		} catch (JSONException e) {
			Log.e("hermesgame", "postWXMsg Exception: " + e.toString());
		}
		callJsFunc(jsonObject, callbackFunction);
	}

	public void postQQMsg(String appid, String qqSec) {
		// 获取包名
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "openQQ");
			params.put("openid", appid);
			params.put("access_token", qqSec);
			jsonObject.put("params", params);
		} catch (JSONException e) {
			Log.e("hermesgame", "postQQMsg Exception: " + e.toString());
		}
		callJsFunc(jsonObject, callbackFunction);
	}

	/*
	 * 调用JS函数的接口
	 */

	private void callJsFunc(final JSONObject jsonObject, final String methodName) {
		try {
			// 所有的webview的方法必须在一个线程调用，否则会出现异常
			Log.d("hermesgame", "callJsFunc: " + methodName);
			mWebView.post(new Runnable() {
				@Override
				public void run() {
					
					mWebView.loadUrl("javascript:" + methodName + "('"
							+ jsonObject.toString() + "')");								
					
				}
			});

		} catch (Exception e) {
			Log.e("hermesgame", "调用JS方法出现异常：" + e.toString());

		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		  if (requestCode == Constants.REQUEST_LOGIN){
			    Tencent.onActivityResultData(requestCode, resultCode, data, ThirdManager.getInstance().tencentListener);
		  }        
	}

}
