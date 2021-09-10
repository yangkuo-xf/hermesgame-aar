package com.hermesgamesdk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import com.hermesgamesdk.utils.QGSdkUtils;

public class AliWebPayActivity extends Activity {

	private static final String TAG_PAY_SUCCESS = "www.quicksdk.success";
	private static final String TAG_PAY_STOP = "www.quicksdk.stop";
	private static final String TAG_PAY_SUCCESS_2 ="payStatusCheck/success";
	private static final String TAG_PAY_STOP_2 ="payStatusCheck/stop";

	WebView mWebView;
	private  String mUrl;
	private static  boolean isSlider;//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(QGSdkUtils.getResId(this, "R.layout.qg_activity_webpay"));
		mWebView = (WebView) findViewById(QGSdkUtils.getResId(this, "R.id.qg_webview_pay"));
		mUrl = getIntent().getStringExtra("url");
		isSlider = getIntent().getBooleanExtra("isSlider",false);
		if (mUrl == null) {
			onError();
		}
		if (mUrl.startsWith("alipays:") ){
			Log.d("hermesgame", "启动支付宝");
			try {
				Intent intent=new Intent("android.intent.action.VIEW", Uri.parse(mUrl));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				AliWebPayActivity.this.startActivity(intent);
				onSuccess();
			} catch (Exception e) {
				new AlertDialog.Builder(AliWebPayActivity.this).setMessage("未检测到支付宝客户端，请安装后重试。")
						.setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								Uri alipayUrl = Uri.parse("https://d.alipay.com");
								AliWebPayActivity.this.startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
							}
						}).setNegativeButton("取消", null).show();
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
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("hermesgame", "shouldOverrideUrlLoading: "+url);
				if (url != null) {
					if (url.contains(TAG_PAY_SUCCESS)||url.contains(TAG_PAY_SUCCESS_2)) {
						onSuccess();
					} else if (url.contains(TAG_PAY_STOP)||url.contains(TAG_PAY_STOP_2)) {
						onCancel();
					} else {
						if (url.startsWith("http")) {
							mWebView.loadUrl(url);
						}
					}
					if (url.startsWith("alipays:") || url.startsWith("alipay") || url.startsWith("https://openapi.")) {
						try {
							AliWebPayActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
							onSuccess();
						} catch (Exception e) {
							new AlertDialog.Builder(AliWebPayActivity.this).setMessage("未检测到支付宝客户端，请安装后重试。")
									.setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											Uri alipayUrl = Uri.parse("https://d.alipay.com");
											AliWebPayActivity.this.startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
										}
									}).setNegativeButton("取消", null).show();
						}
						return true;
					}
				}
				return true;
			}
		});
		WebSettings s = mWebView.getSettings();
		s.setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
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
					onCancel();
					break;
				case DialogInterface.BUTTON_POSITIVE:
					break;

				}
			}
		};
	}

	private void onSuccess() {
		if (isSlider){
			//Toast.makeText(this,"支付成功",Toast.LENGTH_LONG).show();
			finish();
		}else{
			setResult(1);
			finish();
		}

	}

	private void onCancel() {
		if (isSlider){
			//Toast.makeText(this,"支付失败",Toast.LENGTH_LONG).show();
			finish();
		}else{
			setResult(0);
			finish();
		}

	}

	private void onError() {
		if (isSlider){
			//Toast.makeText(this,"支付失败",Toast.LENGTH_LONG).show();
			finish();

		}else{
			setResult(-1);
			finish();
		}

	}

}
