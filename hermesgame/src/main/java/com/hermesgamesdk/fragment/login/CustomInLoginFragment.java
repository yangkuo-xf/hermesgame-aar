package com.hermesgamesdk.fragment.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.manager.DataManager;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/9/13.
 */

public class CustomInLoginFragment extends BaseFragment {
	private WebView custom;
	private String jsFunction="var clickObject=document.getElementsByClassName(\"QQJoinGroup\");for(var i=0;i<clickObject.length;i++){clickObject[i].onclick=function(){var groupId=this.getAttribute(\"groupId\");var sendObject=new Object;sendObject.action=\"QQ_joinGroup\";sendObject.params={key:groupId};var ua=navigator.userAgent;if(ua.indexOf(\"hermesgameAndroid\")!=-1||ua.indexOf(\"QuickBrowserAndroid\")!=-1){if(typeof(JObject)==\"object\"){return JObject.callAndroidFunction(JSON.stringify(sendObject))}}}};";

	@Override
	protected String getRootName() {
		return "R.layout.qg_fragment_custom";
	}

	@Override
	protected String getTitle() {
		return "R.string.qk_freeservices_tocs";
	}

	@Override
	protected void onRootViewInflated(View root) {
		initView(root);
	}

	private void initView(View root) {
		custom = (WebView) findView("R.id.qg_login_customweb");
		custom.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		custom.getSettings().setUserAgentString(custom.getSettings().getUserAgentString() + "hermesgameAndroid");
		custom.getSettings().setJavaScriptEnabled(true);
		custom.getSettings().setAllowFileAccess(true);
		custom.getSettings().setDomStorageEnabled(true);
		custom.getSettings().setSupportZoom(false);
		custom.getSettings().setBuiltInZoomControls(false);
		custom.addJavascriptInterface(new CustomLoginJsInterface(), "JObject");
		custom.setWebViewClient(new MyWebViewClient());
		mTitleBar.hideCloseIcon();
		InitData data = (InitData) DataManager.getInstance().getData(
				Constant.INIT_KEY);
		if (data != null) {
			String serviceInfo = data.getProductconfig().getServiceinfo();

			if (serviceInfo.startsWith("http://")
					|| serviceInfo.startsWith("https://")) {
			/*	custom.setWebViewClient(new WebViewClient(){
					@Override
					public boolean shouldOverrideUrlLoading(WebView view,
							String url) {
						// TODO Auto-generated method stub
						return false;
					}
				});*/
				custom.loadUrl(serviceInfo);
				
			} else {
				custom.loadDataWithBaseURL("", serviceInfo, "text/html",
						"utf-8", "");
			}

		}

	}

	/*
	 * ??????JS???????????????
	 */
	private void callJsFunc(final String jsString) {
		try {
			// ?????????webview????????????????????????????????????????????????????????????
			Log.d("hermesgame", "CustomLoginFragment callJsFunc" + jsString.toString());
			custom.post(new Runnable() {
				@Override
				public void run() {
				//	String jsonMsg = Base64.encodeToString(jsonObject.toString().getBytes(), Base64.NO_WRAP);
					custom.loadUrl("javascript:"+jsString);
				}
			});

		} catch (Exception e) {
			Log.e("hermesgame", "GameSliderBarActivityV2  ??????JS?????????????????????" + e.toString());

		}
	}
	/****************
	 *
	 * ??????????????????????????????????????????????????????Quick(562856728) ??? key ?????? -gkNnzvM4ONql3CDmFo4_x9Ahz_Zbefk
	 * ?????? joinQQGroup(-gkNnzvM4ONql3CDmFo4_x9Ahz_Zbefk) ???????????????Q????????????????????? ?????????????????????Quick(562856728)
	 *
	 * @param key ??????????????????key
	 * @return ??????true???????????????Q???????????????fals??????????????????
	 ******************/
	public boolean joinQQGroup(Activity activity, String key) {
		if (checkApkExist(activity,"com.tencent.mobileqq")){
			Intent intent = new Intent();
			intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
			// ???Flag??????????????????????????????????????????????????????????????????????????????????????????Q???????????????????????????????????????????????????????????????    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			try {
				mActivity.startActivity(intent);
				return true;
			} catch (Exception e) {
				// ????????????Q???????????????????????????
				return false;
			}
		}else{
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

	private class MyWebViewClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			callJsFunc(jsFunction);
		}
	}

	class CustomLoginJsInterface{
		@JavascriptInterface
		public String callAndroidFunction(String json) {
			Log.e("hermesgame","CustomLoginJsInterface:"+json);
			try {
				JSONObject jObject = new JSONObject(json);
				// JSONObject params = jObject.getJSONObject("params");
				// JS?????????
				String action = jObject.optString("action");
				JSONObject params = jObject.getJSONObject("params");
				String key=params.getString("key");
				joinQQGroup(mActivity,key);
			}catch (Exception e){

			}
			return "";
		}

	}
}
