package com.hermesgamesdk.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.manager.QGPayManager;
import com.hermesgamesdk.manager.SliderBarV2Manager;
import com.hermesgamesdk.utils.QGSdkUtils;

public class WeChatWebPayActivity extends Activity {

	public static final int RESULT_ERROR = 3;
	private boolean startWx;

	String TAG_PAY_SUCCESS = "www.quicksdk.success";
	String TAG_PAY_STOP = "www.quicksdk.stop";
	String TAG_PAY_SUCCESS_2 = "payStatusCheck/success";
	String TAG_PAY_STOP_2 = "payStatusCheck/stop";

	private WebView mWebView;
	String mUrl;
	int payType;
	String mOrderNum;
	boolean isSlider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(QGSdkUtils.getResId(this, "R.layout.qg_activity_webpay"));
		mWebView = (WebView) findViewById(QGSdkUtils.getResId(this, "R.id.qg_webview_pay"));
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setDatabaseEnabled(true);
		String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
		mWebView.getSettings().setAppCachePath(appCachePath);
		mWebView.getSettings().setAppCacheEnabled(true);
		mUrl = getIntent().getStringExtra("url");
		isSlider = getIntent().getBooleanExtra("isSlider",false);
		Log.e("hermesgame", "WeChatWebPayActivity-Oncreat: " + mUrl);
		payType = getIntent().getIntExtra("payType", 88);
		if (mUrl == null) {
			onError();
		}
		if (mUrl.startsWith("weixin://wap/pay?")||mUrl.startsWith("weixin://dl/business")){
			Log.e("hermesgame", "启动微信");
			try {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(mUrl));
				startWx = true;
				startActivity(intent);
			} catch (Exception e) {
				if (e instanceof ActivityNotFoundException) {
					Toast.makeText(WeChatWebPayActivity.this, "请安装最新的微信客户端", Toast.LENGTH_LONG).show();
					onError();
				} else {
					e.printStackTrace();
				}
			}
		}else{
			loadWebView();
		}


	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadWebView() {
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Log.d("hermesgame", "WeChatWebPayActivity-onPageFinished: " + mUrl);
				if ((payType == 203 || payType == 202) && url.contains("paySuccessful")) {
					if (isSlider){
						Toast.makeText(WeChatWebPayActivity.this,"支付成功",Toast.LENGTH_LONG).show();
						finish();
					}else {
						QGPayManager.getInstance().mPayCallBack.onSuccess();
						finish();
					}

				} else if ((payType == 203 || payType == 202) && url.contains("payFailed")) {
					if (isSlider){
						Toast.makeText(WeChatWebPayActivity.this,"支付失败",Toast.LENGTH_LONG).show();
					}else {
						QGPayManager.getInstance().mPayCallBack.onFailed("支付失败");
						finish();
					}

				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url != null) {
					Log.e("hermesgame", "shouldOverrideUrlLoading: " + url);
					// ------ 对alipays:相关的scheme处理 -------
					if (url.startsWith("alipays:") || url.startsWith("alipay") || url.startsWith("https://openapi.")) {
						Log.e("hermesgame", "启动支付宝");
						try {
							WeChatWebPayActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
							if (payType != 202) {
								onSuccess();
							}
						} catch (Exception e) {
							new AlertDialog.Builder(WeChatWebPayActivity.this).setMessage("未检测到支付宝客户端，请安装后重试。").setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									Uri alipayUrl = Uri.parse("https://d.alipay.com");
									WeChatWebPayActivity.this.startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
								}
							}).setNegativeButton("取消", null).show();
						}
						return true;
					}
					if (url.startsWith("weixin://wap/pay?")||url.startsWith("weixin://dl/business")) {
						Log.e("hermesgame", "启动微信");
						try {
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(url));
							startWx = true;
							startActivity(intent);
						} catch (Exception e) {
							if (e instanceof ActivityNotFoundException) {
								Toast.makeText(WeChatWebPayActivity.this, "请安装最新的微信客户端", Toast.LENGTH_LONG).show();
								onError();
							} else {
								e.printStackTrace();
							}
						}
					} else {
						if (("4.4.3".equals(android.os.Build.VERSION.RELEASE)) || ("4.4.4".equals(android.os.Build.VERSION.RELEASE))) {
							// 敲黑板
							return false;
						} else {

						}

					}

					if (url.startsWith("mqqapi://forward")) {
						try {
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(url));
							startActivityForResult(intent, 2);

						} catch (Exception e) {
							if (e instanceof ActivityNotFoundException) {
								Toast.makeText(WeChatWebPayActivity.this, "请安装最新的QQ客户端", Toast.LENGTH_LONG).show();
							} else {
								e.printStackTrace();
							}
						}
						return true;
					}

					// ------ 对alipays:相关的scheme处理 -------
					if (url.startsWith("https://myclient.alipay") || url.startsWith("alipay")) {
						try {
							startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
						} catch (Exception e) {
							new AlertDialog.Builder(WeChatWebPayActivity.this).setMessage("未检测到支付宝客户端，请安装后重试。").setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									Uri alipayUrl = Uri.parse("https://d.alipay.com");
									startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
								}
							}).setNegativeButton("取消", null).show();
						}
						return true;
					}
					if (url.startsWith("https://wx.tenpay") && (payType == 202 || payType == 196 || payType == 88||payType==224)) {
						HashMap<String, String> map = new HashMap<String, String>();
						if (payType == 196) {
							map.put("Referer", "https://hykjh5.heemoney.com");
						} else if (payType == 202) {
							map.put("Referer", "http://www.9buff.com");
						} else {
							if (isSlider){
								map.put("Referer", SliderBarV2Manager.getInstance(WeChatWebPayActivity.this).businessUrl);
							}else{
								String[] hosts = Constant.HOST.split("\\.");
								int hasCode = 0;
								String regex1 = ".*[a-zA-z].*";
								for (int i = 1; i < hosts.length; i++) {
									if (hosts[i].matches(regex1)) {
										hasCode = hasCode + 1;
									}
								}
								if (hasCode > 0) {
									map.put("Referer", Constant.HOST + "/gameCenter/wxReferer?productCode=" + Constant.PRODUCT_ID + "&platform=android&appid=weixin");
								} else {
									map.put("Referer", "http://hermesgame.sdk.quicksdk.net" + "/gameCenter/wxReferer?productCode=" + Constant.PRODUCT_ID + "&platform=android&appid=weixin");
								}
							}

						}
						Log.d("hermesgame", "Refr:  " + map.get("Referer").toString());
						WebSettings s = mWebView.getSettings();
						s.setJavaScriptEnabled(true);
						mWebView.loadUrl(url, map);
						return true;
					}

					if (url.contains(TAG_PAY_SUCCESS) || url.contains(TAG_PAY_SUCCESS_2)) {
						onSuccess();
					} else if (url.contains(TAG_PAY_STOP) || url.contains(TAG_PAY_STOP_2)) {
						onCancel();
					} else {
						if (url.startsWith("http")) {
							mWebView.loadUrl(url);
						}
					}
				} else {
				}
				return true;
			}
		});
		if (payType == 88 || payType == 196) {
			HashMap<String, String> map = new HashMap<String, String>();
			String[] hosts = Constant.HOST.split("\\.");
			int hasCode = 0;
			String regex1 = ".*[a-zA-z].*";
			for (int i = 1; i < hosts.length; i++) {
				if (hosts[i].matches(regex1)) {
					hasCode = hasCode + 1;
				}
			}

			if (("4.4.3".equals(android.os.Build.VERSION.RELEASE)) || ("4.4.4".equals(android.os.Build.VERSION.RELEASE))) {

				if (hasCode > 0) {
					map.put("Referer ", Constant.HOST + "/gameCenter/wxReferer?productCode=" + Constant.PRODUCT_ID + "&platform=android&appid=weixin");

					WebSettings s = mWebView.getSettings();
					s.setJavaScriptEnabled(true);
					mWebView.loadDataWithBaseURL(Constant.HOST + "/gameCenter/wxReferer?productCode=" + Constant.PRODUCT_ID + "&platform=android&appid=weixin",
							"<script>window.location.href=\"" + mUrl + "\";</script>", "text/html", "utf-8", null);
				} else {
					WebSettings s = mWebView.getSettings();
					s.setJavaScriptEnabled(true);
					mWebView.loadDataWithBaseURL("http://hermesgame.sdk.quicksdk.net" + "/gameCenter/wxReferer?productCode=" + Constant.PRODUCT_ID
							+ "&platform=android&appid=weixin", "<script>window.location.href=\"" + mUrl + "\";</script>", "text/html", "utf-8", null);
				}

			} else {
				if (hasCode > 0) {
					map.put("Referer", Constant.HOST + "/gameCenter/wxReferer?productCode=" + Constant.PRODUCT_ID + "&platform=android&appid=weixin");
				} else {
					map.put("Referer", "http://hermesgame.sdk.quicksdk.net" + "/gameCenter/wxReferer?productCode=" + Constant.PRODUCT_ID + "&platform=android&appid=weixin");
				}

				Log.d("hermesgame","Referrrr: "+map.get("Referer"));
				WebSettings s = mWebView.getSettings();
				s.setJavaScriptEnabled(true);
				//mUrl="https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=wx1718493243007920b5f12dde8b02590000&package=882602674";
				mWebView.loadUrl(mUrl, map);
			}

		} else if (payType == 201 || payType == 130 && mUrl.startsWith("weixin://wap/pay?")) {

			try {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(mUrl));
				startWx = true;
				startActivity(intent);
			} catch (Exception e) {
				if (e instanceof ActivityNotFoundException) {
					Toast.makeText(WeChatWebPayActivity.this, "请安装最新的微信客户端", Toast.LENGTH_LONG).show();
					onError();
				} else {
					e.printStackTrace();
				}
			}
		} else if (payType == 165 || payType == 166 || payType == 167 || payType == 179 || payType == 178 || payType == 184 || payType == 193 || payType == 194 || payType == 202
				|| payType == 203 || payType == 208||payType==221||payType==223||payType==224||payType==225||payType==227||payType==228) {

			WebSettings s = mWebView.getSettings();
			s.setJavaScriptEnabled(true);
			mWebView.loadUrl(mUrl);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.e("hermesgame", " KeyEvent.KEYCODE_BACK");
			if (mWebView.canGoBack() && payType != 165) {
				mWebView.goBack();
				return true;
			} else if (payType == 165) {
				Log.e("hermesgame", " KeyEvent.KEYCODE_BACK&&payType==165");
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("温馨提示！").setMessage("是否取消此次交易！").setNegativeButton("确定", setOnclick()).setPositiveButton("取消", setOnclick());
				alert.show();
				return true;
			} else {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("温馨提示！").setMessage("是否取消此次交易！").setNegativeButton("确定", setOnclick()).setPositiveButton("取消", setOnclick());
				alert.show();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private DialogInterface.OnClickListener setOnclick() {

		return new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				switch (arg1) {
					case DialogInterface.BUTTON_NEGATIVE:
						if (payType == 202) {
							QGPayManager.getInstance().mPayCallBack.onFailed("支付取消");
							finish();
						} else {
							onCancel();
						}
						break;
					case DialogInterface.BUTTON_POSITIVE:
						break;

				}
			}
		};
	}

	private void onSuccess() {
		if (isSlider){
			Toast.makeText(this,"支付成功",Toast.LENGTH_LONG).show();
			finish();
		}else{
			setResult(1);
			finish();
		}

	}

	private void onCancel() {
		if (isSlider){
			finish();
		}else{
			setResult(0);
			finish();
		}

	}

	private void onError() {
		if (isSlider){
			Toast.makeText(this,"支付失败",Toast.LENGTH_LONG).show();
			finish();
		}else{
			setResult(-1);
			finish();
		}

	}

	@Override
	protected void onResume() {
		if (startWx && payType != 202) {
			finish();
		}
		super.onResume();
	}

}