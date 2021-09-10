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
	 * 调用JS函数的接口
	 */
	private void callJsFunc(final String jsString) {
		try {
			// 所有的webview的方法必须在一个线程调用，否则会出现异常
			Log.d("hermesgame", "CustomLoginFragment callJsFunc" + jsString.toString());
			custom.post(new Runnable() {
				@Override
				public void run() {
				//	String jsonMsg = Base64.encodeToString(jsonObject.toString().getBytes(), Base64.NO_WRAP);
					custom.loadUrl("javascript:"+jsString);
				}
			});

		} catch (Exception e) {
			Log.e("hermesgame", "GameSliderBarActivityV2  调用JS方法出现异常：" + e.toString());

		}
	}
	/****************
	 *
	 * 发起添加群流程。群号：渠道接入小分队Quick(562856728) 的 key 为： -gkNnzvM4ONql3CDmFo4_x9Ahz_Zbefk
	 * 调用 joinQQGroup(-gkNnzvM4ONql3CDmFo4_x9Ahz_Zbefk) 即可发起手Q客户端申请加群 渠道接入小分队Quick(562856728)
	 *
	 * @param key 由官网生成的key
	 * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	 ******************/
	public boolean joinQQGroup(Activity activity, String key) {
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
				// JS的行为
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
