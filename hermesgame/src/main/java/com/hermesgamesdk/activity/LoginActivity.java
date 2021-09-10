package com.hermesgamesdk.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hermesgamesdk.manager.ThirdManager;
import com.iqiyi.passportsdk.interflow.InterflowConstants;
import com.iqiyi.passportsdk.interflow.api.InterflowApi;
import com.iqiyi.passportsdk.interflow.core.InterflowObj;
import com.iqiyi.passportsdk.interflow.safe.DataEncryptor;
import com.iqiyi.passportsdk.register.RequestCallback;
import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.QGManager;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.fragment.login.AccountLoginFragment;
import com.hermesgamesdk.fragment.login.AccountRegisterFragment;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.manager.SliderBarV2Manager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.quickgamesdk.iqiyilogin.UserCache;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

public class LoginActivity extends BaseActivity {
	private static final int REQUEST_CODE = 111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getResId("R.layout.qg_activity_layout"));

		if (QGConfig.isSupportWXLogin()) {
			LoginManager.getInstance().initWXLogin(this);
		}
		if (QGConfig.isSupportQQLogin()) {
			LoginManager.getInstance().initQQLogin(this);
		}
		if (QGConfig.isSupportIQYLogin(this)) {
			LoginManager.getInstance().initIqyLogin(this);
		}
		if (QGConfig.isSupportALPLogin()) {
			LoginManager.getInstance().initOKLogin(this);
		}

		if (QGConfig.isSupportTAPLogin()) {
			LoginManager.getInstance().initTAPLogin(this);
		}
		// 初始化手机授权登录

		if (checkUpdate(this)) {
			startActivityForResult(new Intent(this, DownloadActivity.class), REQUEST_CODE);
			LoginManager.getInstance().isAutoLogin = false;
		} else {
			// 跳转到公告界面，节点为登录前
			if (Constant.noticeShowNode == Constant.NOTICE_NODE_BEFORE_LOGIN) {
				LoginManager.getInstance().isAutoLogin = false;
				Intent intent = new Intent();
				intent.putExtra("from", "notice");
				intent.setClass(this, TempActivty.class);
				startActivity(intent);
			}
		}
	}

	@Override
	protected Fragment getDefaultFragment() {
		return new AccountLoginFragment();
	}

	protected boolean checkUpdate(FragmentActivity context) {
		try {
			InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
			if (data != null) {
				int newVersion = Integer.parseInt(data.getVersion().getVersionNo());
				PackageManager manager = context.getPackageManager();
				PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
				int nowVersion = info.versionCode;
				if (newVersion > nowVersion) {
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("hermesgame", "LoginActivity onActivityResult"+requestCode+"  "+resultCode);
		// 处理qq授权回调
		if (requestCode == Constants.REQUEST_LOGIN)
			Tencent.onActivityResultData(requestCode, resultCode, data, ThirdManager.getInstance().tencentListener);
		// 从DownloadActivity返回结果，然后跳转到NoticeActivity，节点为登录前
		if (resultCode == DownloadActivity.RESULT_CODE) {
			if (Constant.noticeShowNode == Constant.NOTICE_NODE_BEFORE_LOGIN) {
				Intent intent = new Intent();
				intent.putExtra("from", "notice");
				intent.setClass(this, TempActivty.class);
				startActivity(intent);
			}
		}
		// 处理cp授权回调
		if (requestCode == Constant.CP_LOGIN_REQUEST) {
			if (resultCode == Constant.CP_LOGIN_OK) {
				LoginManager.getInstance().notifyLoginSuccessed();
				LoginActivity.this.finish();
			}
		}

		if (requestCode == 1001 && resultCode == 1) {
			AccountLoginFragment baseFragment = (AccountLoginFragment) getDefaultFragment();
			baseFragment.setMyActivity(LoginActivity.this);
			baseFragment.tryPlay();
		} else if (requestCode == 1001 && resultCode == 2) {
			QGFragmentManager.getInstance(LoginActivity.this).add(new AccountRegisterFragment());
		}
		if (requestCode==10009){
			if (resultCode==1){
				Log.d("hermesgame.LoginActivity", "auth resultCode 1: "+data.getStringExtra("authToken"));
				if(data.getStringExtra("authToken")!=null&&!("").equals(data.getStringExtra("authToken"))){
					AccountLoginFragment baseFragment = (AccountLoginFragment) getDefaultFragment();
					baseFragment.setMyActivity(LoginActivity.this);
					baseFragment.autoLoginByToken(data.getStringExtra("authToken"),1);
				}else{
					Toast.makeText(LoginActivity.this,"授权失败,token为空",Toast.LENGTH_SHORT).show();
				}
			}else {
				Log.d("hermesgame.LoginActivity", "resultCode 0");
				Toast.makeText(LoginActivity.this,"授权失败,取消授权",Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onStart() {
		Log.d("hermesgame.LoginActivity", "LoginActivity onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.d("hermesgame.LoginActivity", "LoginActivity onRestart");
		super.onRestart();
	}

	@Override
	protected void onStop() {
		Log.d("hermesgame.LoginActivity", "LoginActivity onStop");
		super.onStop();
	}

	@Override
	protected void onResume() {
		Log.d("hermesgame.LoginActivity", "LoginActivity onResume");
		super.onResume();
		((BaseFragment) getDefaultFragment()).setMyActivity(LoginActivity.this);
	}
	@Override
	protected void onDestroy() {
		Log.d("hermesgame.LoginActivity", "LoginActivity onDestroy");
		if (!TextUtils.isEmpty(QGManager.getUID())) {
			try {
				SliderBarV2Manager.getInstance(this).init();
			}catch (Exception e){
				Log.e("hermesgame.LoginActivity", "LoginActivity onDestroy Exception: "+e.toString());
			}
			LoginManager.getInstance().notifyLoginSuccessed();

		} else {
			LoginManager.getInstance().notifyLoginFiled("用户关闭登陆界面");
		}
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("hermesgame.LoginActivity", "LoginActivity onNewIntent");
		if (ThirdManager.iqiyiLoginKey > 0) {
			InterflowObj interflowObj = (InterflowObj) getParcelableExtra(intent,
					InterflowConstants.EXTRA_INTERFLOW_OBJ);
			if (interflowObj != null && !TextUtils.isEmpty(interflowObj.interflowToken)) {
				final String token = DataEncryptor.decrypt(interflowObj.interflowToken, ThirdManager.iqiyiLoginKey);
				if ("TOKEN_FAILED".equals(token)) {
					Log.e("hermesgame_iqy", "TOKEN_FAILED");
				} else {
					InterflowApi.opt_login(token, new RequestCallback() {
						@Override
						public void onSuccess() {

							final String uidString = UserCache.getInstance().getUid();
							final String authcookie = UserCache.getInstance().getAuthCookie();
							Log.e("hermesgame_iqy", "  opt_login   onSuccess uid: " + uidString + "    cookie: "
									+ authcookie);
							LoginActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									iqiyiAuth(uidString, token, authcookie);
								}
							});

						}

						@Override
						public void onFailed(String code, String failMsg) {
							Log.e("hermesgame_iqy", "  opt_login   onFailed" + "   code: " + code + "     msg："
									+ failMsg);
							Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onNetworkError() {
							Log.e("hermesgame_iqy", "  opt_login   onNetworkError");
							Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}

	}

	private void iqiyiAuth(String uid, String token, String cookie) {
		String param = new QGParameter(LoginActivity.this).addParameter("openType", "15")
				.addParameter("userOpenId", uid).addParameter("access_token", token).addParameter("authcookie", cookie)
				.create();

		HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
			@Override
			public void onSuccess(QGUserInfo bean) {
				// LoginManager.getInstance().notifyLoginSuccessed();
				finish();
			}

			@Override
			public void onFailed(int id, String message) {
				LoginManager.getInstance().notifyLoginFiled("onNewIntent iqiyiAuth  onFailed");
			}
		}.addParameter(param).post().setUrl(Constant.HOST + Constant.THIRD_LOGIN);

		DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);

	}

	private Parcelable getParcelableExtra(Intent intent, String name) {
		Parcelable val = null;
		try {
			val = intent.getParcelableExtra(name);
		} catch (Exception e) {
			Log.d("hermesgame_iqy", "getParcelableExtra E:" + e.getMessage());
		}
		return val;
	}

}
