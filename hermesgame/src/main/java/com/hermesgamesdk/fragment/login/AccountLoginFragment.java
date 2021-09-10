package com.hermesgamesdk.fragment.login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hermesgamesdk.activity.CpLoginActivity;
import com.hermesgamesdk.activity.NoticeActivity;
import com.hermesgamesdk.callback.QGCallBack;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.manager.ThirdManager;
import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.InitManager;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.QGAccountWindow;
import com.hermesgamesdk.view.QGAccountWindow.ItemCickListenner;
import com.hermesgamesdk.view.QGEditText;
import com.hermesgamesdk.view.QGEditText.TextChangedListener;
import com.hermesgamesdk.view.ToastGame;

/**
 * Created by Administrator on 2017/9/11.
 */

public class AccountLoginFragment extends BaseFragment {
	private QGEditText mAccountEdt;
	private QGEditText mPasswordEdt;
	private Button mAccountLoginButton;
	private String mAccount;
	private String mPassword;
	private ImageView mEye;
	private ImageView mQQLogin;
	private ImageView mIQYLogin;
	private ImageView mWXLogin;
	private ImageView mCPLogin;
	private ImageView mTAPLogin;
	private ImageView mOKLogin;
	private ImageView mGameBoxLogin;
	private ImageView mAccountLogin;
	private ImageView mAccountLoginLogo;
	private ImageView mPhoneLoginView;
	private TextView mFindPasswordView;
	private ImageView tryPlayView;
	private TextView registerView;
	private TextView customView;
	private TextView ed_title_account;
	private TextView ed_title_pwd;
	private LinearLayout mMoreAccount;
	private LinearLayout mFiveLoginLayout;
	private RelativeLayout qg_tryPlayLogin;
	private LinearLayout qg_only_try_play;
	private LinearLayout ll_qg_only_try_play;
	private Button line_account;
	private Button line_pwd;
	private JSONObject[] mAccountInfos;
	private String[] mAccounts;
	private LinearLayout qg_login_layout_main;
	private LinearLayout phone_fram;
	private LinearLayout account_fram;
	private LinearLayout qg_other_login_way_contanier;

	private Button mGetIdentfyingCodeButton;
	private QGEditText mPhoneView;
	private QGEditText mIdentfyingCodeView;
	private String mPhoneNumber;
	private String mIdentfyingCode;
	private TextView ed_title_loginphone, ed_title_logincode;
	private Button qg_line_loginphone, qg_line_logincode;

	private boolean isAccountLogin = true;

	@Override
	public String getRootName() {
		return "R.layout.qg_fragment_account_login";
	}

	@Override
	protected String getTitle() {
		return "";
	}

	@Override
	public boolean isSupportBack() {
		return false;
	}

	@Override
	public void onRootViewInflated(final View root) {
		initAccountView(root);
		initPhoneView(root);
		initBaseFram();
		InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);

		autoLogin(null);
		//首次登陆且无账号情况
		if (data.getProductconfig().getMainLoginType().equals("3")&&mAccountInfos.length==0&&QGConfig.isSupportALPLogin()){
			qg_login_layout_main.setVisibility(View.GONE);
			showOKLogin();
		}
	}

	private void initBaseFram() {
		// 电话登录
		if (QGConfig.isSupportPhone()) {
			mPhoneLoginView.setVisibility(View.VISIBLE);
			mFindPasswordView.setVisibility(View.VISIBLE);
			mPhoneLoginView.setOnClickListener(listener);
			mFindPasswordView.setOnClickListener(listener);
		} else {
			mPhoneLoginView.setVisibility(View.GONE);
			mFindPasswordView.setVisibility(View.INVISIBLE);
		}
		InitData aData = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
		if (aData.getProductconfig().getMainLoginType().equals("1")) {
			if (QGConfig.isSupportPhone()) {
				mPhoneLoginView.setVisibility(View.VISIBLE);
			} else {
				mPhoneLoginView.setVisibility(View.GONE);
			}
			phone_fram.setVisibility(View.GONE);
			account_fram.setVisibility(View.VISIBLE);
			mAccountLogin.setVisibility(View.GONE);
			isAccountLogin = true;
		} else {
			if (QGConfig.isSupportPhone()) {
				account_fram.setVisibility(View.GONE);
				phone_fram.setVisibility(View.VISIBLE);
				mAccountLogin.setVisibility(View.VISIBLE);
				mPhoneLoginView.setVisibility(View.GONE);
				isAccountLogin = false;
			} else {
				phone_fram.setVisibility(View.GONE);
				account_fram.setVisibility(View.VISIBLE);
				mAccountLogin.setVisibility(View.GONE);
				mPhoneLoginView.setVisibility(View.GONE);
				isAccountLogin = true;
			}
		}
		// qq登录
		if (QGConfig.isSupportQQLogin()) {
			mQQLogin.setVisibility(View.VISIBLE);
			mQQLogin.setOnClickListener(listener);
		}
		// 微信登录
		if (QGConfig.isSupportWXLogin()) {
			mWXLogin.setVisibility(View.VISIBLE);
			mWXLogin.setOnClickListener(listener);
		}
		if (QGConfig.isSupportALPLogin()) {
			mOKLogin.setVisibility(View.VISIBLE);
			mOKLogin.setOnClickListener(listener);
		}
		// 爱奇艺登录
		if (QGConfig.isSupportIQYLogin(mActivity)) {
			mIQYLogin.setVisibility(View.VISIBLE);
			mIQYLogin.setOnClickListener(listener);
		}
		// taptap登录
		if (QGConfig.isSupportTAPLogin()) {
			mTAPLogin.setVisibility(View.VISIBLE);
			mTAPLogin.setOnClickListener(listener);
		}
		// CP自定义登录
		if (aData.getProductconfig().getUseCpLogin() != null && aData.getProductconfig().getUseCpLogin().equals("1")) {
			mCPLogin.setVisibility(View.VISIBLE);
			mCPLogin.setOnClickListener(listener);
			mQQLogin.setVisibility(View.GONE);
		}

		if (QGConfig.isSupportQQLogin() || QGConfig.isSupportWXLogin() || QGConfig.isSupportIQYLogin(mActivity)
				|| QGConfig.isSupportPhone()) {
			mFiveLoginLayout.setVisibility(View.VISIBLE);
			ll_qg_only_try_play.setVisibility(View.GONE);
		} else {
			mFiveLoginLayout.setVisibility(View.GONE);
			qg_other_login_way_contanier.setVisibility(View.GONE);
			ll_qg_only_try_play.setVisibility(View.VISIBLE);
			qg_only_try_play.setOnClickListener(listener);
		}
		try {
			if (QGConfig.getmExtraConfig().get("removeGuest").equals("1")) {
				qg_tryPlayLogin.setVisibility(View.GONE);
				ll_qg_only_try_play.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			Log.d("hermesgame", "getmExtraConfig().get(removeGuest) erro ");
		}

	}

	public void initAccountView(View root) {
		InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
		String tmp = data.getProductconfig().getUseservicecenter();
		mAccountLoginLogo = (ImageView) findView("R.id.qg_qccount_login_logo");
		qg_login_layout_main = (LinearLayout) findView("R.id.qg_login_layout_main");
		phone_fram = (LinearLayout) findView("R.id.layout_phoneLogin");
		account_fram = (LinearLayout) findView("R.id.qg_account_login_fram");
		mWXLogin = (ImageView) findView("R.id.qg_wx_login");
		mQQLogin = (ImageView) findView("R.id.qg_qq_login");
		mCPLogin = (ImageView) findView("R.id.qg_cp_login");
		mIQYLogin = (ImageView) findView("R.id.qg_iqiyi_login");
		mOKLogin = (ImageView) findView("R.id.onekey_phone");
		mTAPLogin= (ImageView) findView("R.id.qg_taptap_login");
		mGameBoxLogin = (ImageView) findView("R.id.qg_gamebox_login");
		mAccountLogin = (ImageView) findView("R.id.qg_account_login");
		mAccountEdt = (QGEditText) findView("R.id.qg_ed_account");

		qg_tryPlayLogin = (RelativeLayout) findView("R.id.qg_tryPlayLogin");
		customView = (TextView) findView("R.id.qg_login_custom");
		if (tmp.equals("0") || tmp.equals("2")) {
			customView.setVisibility(View.GONE);
		}
		if(data.getProductconfig().getUseAppAuth()!=null&&data.getProductconfig().getUseAppAuth().equals("1")){
			if (QGSdkUtils.checkApkExist(mActivity,data.getAppAuthInfo().getAppPackage())){
				mGameBoxLogin.setVisibility(View.VISIBLE);
				try{
					Uri uri = Uri.parse(InitManager.getInstance().cpGameBoxPath);
					Log.d("hermesgame.AccountLoginF","cpGameBoxPath: "+uri);

					mGameBoxLogin.setImageURI(uri);
					mGameBoxLogin.setOnClickListener(listener);
				}catch (Exception e){
					Log.e("hermesgame.AccountLoginF","cpGameBoxPath: "+e.toString());
				}

			}
		}else{
			Log.d("hermesgame","getUseAppAuth: "+data.getProductconfig().getUseAppAuth());
		}
		qg_other_login_way_contanier = (LinearLayout) findView("R.id.qg_other_login_way_contanier");

		mPasswordEdt = (QGEditText) findView("R.id.qg_ed_password");
		mEye = (ImageView) findView("R.id.qg_img_eye");
		mAccountLoginButton = (Button) findView("R.id.qg_btn_login");
		line_account = (Button) findView("R.id.qg_line_account");
		line_pwd = (Button) findView("R.id.qg_line_pwd");

		mFindPasswordView = (TextView) findView("R.id.qg_txt_find_password");
		registerView = (TextView) findView("R.id.qg_txt_register_now");
		tryPlayView = (ImageView) findView("R.id.qg_try_play");
		mPhoneLoginView = (ImageView) findView("R.id.phone_login");
		mMoreAccount = (LinearLayout) findView("R.id.qg_more_account");

		mFiveLoginLayout = (LinearLayout) findView("R.id.qg_five_login_layout");
		qg_only_try_play = (LinearLayout) findView("R.id.qg_only_try_play");
		ll_qg_only_try_play = (LinearLayout) findView("R.id.ll_qg_only_try_play");
		ed_title_account = (TextView) findView("R.id.ed_title_account");
		ed_title_pwd = (TextView) findView("R.id.ed_title_pwd");

		mEye.setOnClickListener(listener);
		mMoreAccount.setOnClickListener(listener);
		tryPlayView.setOnClickListener(listener);
		registerView.setOnClickListener(listener);
		mAccountLoginButton.setOnClickListener(listener);
		mAccountLogin.setOnClickListener(listener);
		customView.setOnClickListener(listener);
		mOKLogin.setOnClickListener(listener);


		pareseAccounts();
		if (mAccounts.length != 0) {
			mMoreAccount.setVisibility(View.VISIBLE);
		}
		if (InitManager.getInstance().useCPLogo) {
			Uri uri = Uri.parse(InitManager.getInstance().cpLogPath);
			mAccountLoginLogo.setBackground(null);
			mAccountLoginLogo.setImageURI(uri);
		}

		mAccountEdt.addTextChangedListener(new TextChangedListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ed_title_account.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mAccountEdt.getText().length() > 0) {
					line_account.setEnabled(true);
				} else {
					line_account.setEnabled(false);
				}
				changeButtonColor();
			}
		});
		mAccountEdt.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
			@Override
			public void onFocusChange(boolean hasFocus) {
				if (!hasFocus) {
					ed_title_account.setVisibility(View.GONE);
					line_account.setEnabled(false);
				} else {
					if (mAccountEdt.getText().length() > 0) {
						ed_title_account.setVisibility(View.VISIBLE);
						line_account.setEnabled(true);
					}
				}

			}
		});

		mPasswordEdt.addTextChangedListener(new TextChangedListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ed_title_pwd.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mPasswordEdt.getText().length() > 0) {
					line_pwd.setEnabled(true);
				} else {
					line_pwd.setEnabled(false);
				}
				changeButtonColor();
			}
		});
		mPasswordEdt.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
			@Override
			public void onFocusChange(boolean hasFocus) {
				if (!hasFocus) {
					ed_title_pwd.setVisibility(View.GONE);
					line_pwd.setEnabled(false);
				} else {
					if (mPasswordEdt.getText().length() > 0) {
						line_pwd.setEnabled(true);
						ed_title_pwd.setVisibility(View.VISIBLE);
					}
				}
			}
		});

	}

	public void initPhoneView(View root) {
		mPhoneView = (QGEditText) findView("R.id.qg_ed_phone");
		mPhoneView.setInputType(InputType.TYPE_CLASS_PHONE);
		mIdentfyingCodeView = (QGEditText) findView("R.id.qg_ed_identifying_code");
		mIdentfyingCodeView.setInputType(InputType.TYPE_CLASS_NUMBER);

		mPhoneView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
		mIdentfyingCodeView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
		mGetIdentfyingCodeButton = (Button) findView("R.id.qg_btn_get_identifying_code");

		ed_title_loginphone = (TextView) findView("R.id.ed_title_loginphone");
		ed_title_logincode = (TextView) findView("R.id.ed_title_logincode");
		qg_line_loginphone = (Button) findView("R.id.qg_line_loginphone");
		qg_line_logincode = (Button) findView("R.id.qg_line_logincode");
		mGetIdentfyingCodeButton.setOnClickListener(listener);

		mPhoneView.addTextChangedListener(new TextChangedListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ed_title_loginphone.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mPhoneView.getText().length() > 0) {
					qg_line_loginphone.setEnabled(true);
				} else {
					qg_line_loginphone.setEnabled(false);
				}
				if (mPhoneView.getText().length() == 11) {
					mGetIdentfyingCodeButton.setEnabled(true);
				} else {
					mGetIdentfyingCodeButton.setEnabled(false);
				}
				changeButtonColor();
			}
		});
		mPhoneView.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
			@Override
			public void onFocusChange(boolean hasFocus) {
				if (!hasFocus) {
					ed_title_loginphone.setVisibility(View.GONE);
					qg_line_loginphone.setEnabled(false);
				} else {
					if (mPhoneView.getText().length() > 0) {
						qg_line_loginphone.setEnabled(true);
						ed_title_loginphone.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		mIdentfyingCodeView.addTextChangedListener(new TextChangedListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ed_title_logincode.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mIdentfyingCodeView.getText().length() > 0) {
					qg_line_logincode.setEnabled(true);
				} else {
					qg_line_logincode.setEnabled(false);
				}
				changeButtonColor();
			}
		});
		mIdentfyingCodeView.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
			@Override
			public void onFocusChange(boolean hasFocus) {
				if (!hasFocus) {
					ed_title_logincode.setVisibility(View.GONE);
					qg_line_logincode.setEnabled(false);
				} else {
					if (mIdentfyingCodeView.getText().length() > 0) {
						ed_title_logincode.setVisibility(View.VISIBLE);
						qg_line_logincode.setEnabled(true);

					}
				}
			}
		});

	}

	private void changeButtonColor() {
		if ((mAccountEdt.getText().length() > 0 && mPasswordEdt.getText().length() > 0)
				|| (mPhoneView.getText().length() > 0 && mIdentfyingCodeView.getText().length() > 0)) {
			mAccountLoginButton.setEnabled(true);

		} else {
			mAccountLoginButton.setEnabled(false);
		}

	}

	private void autoLogin(@Nullable JSONObject jsonObject) {
		try {

			//这里会导致无法注销 一直调盒子登录
		/*	if (LoginManager.getInstance().getToken!=null&&!LoginManager.getInstance().getToken.getToken().equals("")){
				//有盒子的token
				autoLoginByToken(LoginManager.getInstance().getToken.getToken(),1);
				return;
			}*/
			pareseAccounts();
			if(mAccountInfos.length==0){
				return;
			}
			JSONObject obj = mAccountInfos[0];
			if (jsonObject != null) {
				obj = jsonObject;
			}else{

			}
			String account = obj.getString(Constant.SP_ACCOUNT);
			String password = obj.getString(Constant.SP_PASSWORD);
			password = QGSdkUtils.decryptAES(password, Constant.signkey);

			DataManager.getInstance().putData("accountFromFile", account);
			DataManager.getInstance().putData("passwordFromFile", password);
		/*	if (LoginManager.getInstance().getToken!=null&&!LoginManager.getInstance().getToken.getToken().equals("")){
				QGFragmentManager.getInstance(mActivity).add(new AutoLoginFragment());
				return;
			}else{
				Log.e("hermesgame","token: "+LoginManager.getInstance().getToken.getToken());
			}*/
			mAccountEdt.setText(account);
			//*QGsecret* 作为密码占位符
			// 密码长度应少于20位，若长度大于30代表存入的为手机登录的token，不显示token
			mPasswordEdt.setText(password.length() > 30 ? "*QGsecret*" : password);

			if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password) && LoginManager.getInstance().isAutoLogin) {
				QGFragmentManager.getInstance(mActivity).add(new AutoLoginFragment());
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	// 第三方授权
	ThirdManager.ThirdLoginCallBack mLoginCallBack = new ThirdManager.ThirdLoginCallBack() {

		@Override
		public void onLoginSuccessed(QGUserInfo bean) {
			try {
				if (bean.getId() == 40040&&bean.getCheckrealname()==0) {

					showLimitedDialog(AccountLoginFragment.this, bean.getMessage());
				} else {
					// // 保存账号密码
					String username = bean.getUserdata().getUsername();
					String token = bean.getAuthtoken();
					saveAccountInfo(username, token);
					if (isNeedActive(bean)) {
						BaseFragment fragment = new ActiveFragment();
						fragment.setTag(bean.getAuthtoken());
						QGFragmentManager.getInstance(mActivity).add(fragment);
					} else {
						switchToCerificationFragment(CerificationNode.ON_LOGIN);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onLoginFailed(int id, String message) {
			showToast(message);
			//防止阿里云授权失败显示不出登录界面
			if (qg_login_layout_main.getVisibility()==View.GONE){
				qg_login_layout_main.setVisibility(View.VISIBLE);
			}

		}
	};

	@Override
	public void onClicks(int id) {
		if (id == mEye.getId())
			changeEyeState(mEye, mPasswordEdt);
		// tryPlay();
		if (id == tryPlayView.getId() || id == qg_only_try_play.getId()) {
			InitData aData = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
			// 判断是否主动弹出
			if (aData.getProductconfig().getAutoOpenAgreement().equals("0")) {
				tryPlay();
			} else {
				Intent intent = new Intent();
				intent.putExtra("from", "LOGIN");
				intent.setClass(mActivity, NoticeActivity.class);
				getActivity().startActivityForResult(intent, 1001);
			}

		}
		if (id == registerView.getId())
			if (QGConfig.isAutoOpenAgreement()) {
				Intent intent = new Intent();
				intent.putExtra("from", "REGIST");
				intent.setClass(mActivity, NoticeActivity.class);
				mActivity.startActivityForResult(intent, 1001);
			} else {
				QGFragmentManager.getInstance(mActivity).add(new AccountRegisterFragment());
			}

		if (id == mFindPasswordView.getId())
			QGFragmentManager.getInstance(mActivity).add(new FindPasswordFragment());
		if (id == mAccountLoginButton.getId()) {
			if (isAccountLogin) {
				if(mPasswordEdt.getText().equalsIgnoreCase("*QGsecret*")){
					if (mAccountInfos.length!=0){
						JSONObject lastAccount=mAccountInfos[0];
						try {
							for(int i=0;i<mAccountInfos.length;i++){
								JSONObject localAccount=mAccountInfos[i];
								String account=localAccount.getString(Constant.SP_ACCOUNT);
								if (account.equalsIgnoreCase(mAccountEdt.getText())){
									lastAccount=localAccount;
									break;
								}else {
									lastAccount=null;
								}
							}
							if (lastAccount!=null){
								LoginManager.getInstance().isAutoLogin=true;
								autoLogin(lastAccount);
							}else {
								accountLogin();
							}


							}catch (Exception e){

							}

						}else{
							accountLogin();
						}
					}else{

						accountLogin();
					}

			} else {
				phoneLogin();
			}
		}

		if (id == mWXLogin.getId())
			ThirdManager.getInstance().wxLogin(mActivity, mLoginCallBack);
		if (id == mQQLogin.getId())
			ThirdManager.getInstance().qqLogin(mActivity, mLoginCallBack);
		if (id == mIQYLogin.getId()) {
			ThirdManager.getInstance().Auth(mActivity);
		}
		if (id == mCPLogin.getId()) {
			Intent intent = new Intent();
			intent.setClass(mActivity, CpLoginActivity.class);
			mActivity.startActivityForResult(intent, Constant.CP_LOGIN_REQUEST);
		}

		if (id == mTAPLogin.getId()) {
			ThirdManager.getInstance().showTaptap(mActivity,mLoginCallBack);
			//TapBootstrap.login(mActivity, LoginType.TAPTAP, "public_profile");
		}
		if (id == mMoreAccount.getId()) {
			displayMoreAccount();
		}
		if (id == mGetIdentfyingCodeButton.getId()) {
			mPhoneNumber = mPhoneView.getText().toString();
			requestIdentfyingCode(mPhoneView,mGetIdentfyingCodeButton, mPhoneNumber, Constant.SMS_TYPE_LOGIN,0);
		}
		if (id == mAccountLogin.getId()) {
			account_fram.setVisibility(View.VISIBLE);
			phone_fram.setVisibility(View.GONE);
			mPhoneLoginView.setVisibility(View.VISIBLE);
			mAccountLogin.setVisibility(View.GONE);
			isAccountLogin = true;
		}
		if (id == mPhoneLoginView.getId()) {
			phone_fram.setVisibility(View.VISIBLE);
			account_fram.setVisibility(View.GONE);
			mPhoneLoginView.setVisibility(View.GONE);
			mAccountLogin.setVisibility(View.VISIBLE);
			isAccountLogin = false;
		}
		if (id == customView.getId()) {
			InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
			String tmp = data.getProductconfig().getUseservicecenter();
			if (tmp.equals("1") || tmp.equals("3")) {
				QGFragmentManager.getInstance(mActivity).add(new CustomInLoginFragment());
			} else if (tmp.equals("2")) {
				if (QGConfig.isSupportIM()) {
					InitManager.getInstance().showCustomService(mActivity);
				} else {
					ToastGame.makeText(mActivity, "暂不支持IM客服", Toast.LENGTH_SHORT).show();
				}
			}
		}
		if (id == mOKLogin.getId()) {
			showOKLogin();
		}

		InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
		if(data.getProductconfig().getUseAppAuth()!=null&&data.getProductconfig().getUseAppAuth().equals("1")){
			if(id==mGameBoxLogin.getId()){

				try {
					final String url = data.getAppAuthInfo().getAppPackage()+"://hermesgamesdk:8000/authlogin";

					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mActivity.startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(url)),10009);
						}
					});

				} catch (Exception e) {
					Log.e("hermesgame", "getUseAppAuth异常：" + e.toString());
				}
			}
		}


	}

	private void displayMoreAccount() {
		final QGAccountWindow window = new QGAccountWindow(mActivity, line_account,
				mAccounts);
		window.setOnItemCickListenner(new ItemCickListenner() {

			@Override
			public void onItemCick(View item, ViewGroup parent, int index, View clickedView, int type) {
				try {
					JSONObject obj = mAccountInfos[index];
					// 点击删除
					if (type == QGAccountWindow.TYPE_DELETE) {
						window.removeItem(item);
						saveAccountInfo(obj.getString(Constant.SP_ACCOUNT), null);
					} else {
						// 点击文本
						String password = obj.getString(Constant.SP_PASSWORD);
						password = QGSdkUtils.decryptAES(password, Constant.signkey);
						if (obj.getString(Constant.SP_ACCOUNT).matches("[0-9]+")
								&& obj.getString(Constant.SP_ACCOUNT).length() == 11) {
							LoginManager.getInstance().isAutoLogin = true;
							autoLogin(obj);
						} else if (password.length() > 30) {
							LoginManager.getInstance().isAutoLogin = true;
							autoLogin(obj);
						} else {
							mAccountEdt.setText(obj.getString(Constant.SP_ACCOUNT));
							mPasswordEdt.setText(password.length() > 30 ? "" : password);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	// 试玩
	public void tryPlay() {
		HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
			@Override
			public void onSuccess(QGUserInfo bean) {
				try {

					if (bean.getId() == 40040&&bean.getCheckrealname()==0) {
						int mRealNameFlag=bean.getCheckrealname();
						if (mRealNameFlag!=-1&&mRealNameFlag!=0){

						}else{
							showLimitedDialog(AccountLoginFragment.this, bean.getMessage());
						}

					} else {
						String username = bean.getUserdata().getUsername();
						String password = bean.getUserdata().getUpwd();
						if (TextUtils.isEmpty(password)) {
							password = bean.getAuthtoken();
						}
						saveAccountInfo(username, password);
						if (isNeedActive(bean)) {
							BaseFragment fragment = new ActiveFragment();
							fragment.setTag(bean.getAuthtoken());
							QGFragmentManager.getInstance(mActivity).add(fragment);
						} else {	// 密码大于30位表示为自动登录，直接跳转到实名认证界面
							if (TextUtils.isEmpty(password) || password.length() > 30) {
								switchToCerificationFragment(CerificationNode.ON_LOGIN);
							} else {
								QGFragmentManager.getInstance(mActivity).add(new TryPlayTipsFragment());
							}

							/*
							 * QGFragmentManager.getInstance(mActivity).add( new
							 * TryPlayTipsFragment());
							 */
						}
					}
					LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_ACCOUNT;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailed(int id, String message) {

				Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();

			}
		}.addParameter(new QGParameter(mActivity).create()).post().setUrl(Constant.HOST + Constant.GUEST_LOGIN);
		DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);

	}

	private void pareseAccounts() {
		JSONArray jsonArray = readAccountInfoFromFile();
		mAccounts = new String[jsonArray.length()];
		mAccountInfos = new JSONObject[jsonArray.length()];
		int j = -1;
		for (int i = jsonArray.length() - 1; i >= 0; i--) {
			try {
				j++;
				JSONObject item = jsonArray.getJSONObject(i);
				mAccountInfos[j] = item;
				mAccounts[j] = item.getString(Constant.SP_ACCOUNT);

			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
	}

	// 账号登录
	public void accountLogin() {
		mAccount = mAccountEdt.getText().toString();
		mPassword = mPasswordEdt.getText().toString();

		if (TextUtils.isEmpty(mAccount)) {
			showToast("R.string.toast_text_input_username");
			return;
		}
		if (TextUtils.isEmpty(mPassword)) {
			showToast("R.string.toast_text_input_psw");
			return;
		}

		final HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
			@Override
			public void onSuccess(QGUserInfo bean) {
				try {

					if (bean.getId() == 40040&&bean.getCheckrealname()==0) {
						int mRealNameFlag=bean.getCheckrealname();
						if (mRealNameFlag!=-1&&mRealNameFlag!=0){

						}else{
							showLimitedDialog(AccountLoginFragment.this, bean.getMessage());
						}
					} else {
						// 保存账号密码
						saveAccountInfo(mAccount, mPassword);
						if (isNeedActive(bean)) {
							BaseFragment fragment = new ActiveFragment();
							fragment.setTag(bean.getAuthtoken());
							QGFragmentManager.getInstance(mActivity).add(fragment);
						} else {
							switchToCerificationFragment(CerificationNode.ON_LOGIN);
						}
					}
					LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_ACCOUNT;

				} catch (Exception e) {
					e.printStackTrace();

				}

			}

			@Override
			public void onFailed(int id, String message) {
				showToast(message);
			}
		}.addParameter(
				new QGParameter(mActivity).addParameter("username", mAccount)
						.addParameter("password", QGSdkUtils.getMD5Str(mPassword)).create()).post()
				.setUrl(Constant.HOST + Constant.ACCOUNT_LOGIN);
		DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);
	}

	// 手机登录
	public void phoneLogin() {
		mPhoneNumber = mPhoneView.getText().toString();
		mIdentfyingCode = mIdentfyingCodeView.getText().toString();
		if (TextUtils.isEmpty(mPhoneNumber)) {
			showToast("R.string.toast_text_input_phonenumb");
			return;
		}

		if (TextUtils.isEmpty(mIdentfyingCode)) {
			showToast("R.string.toast_text_input_verificationcode");
			return;
		}

		HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
			@Override
			public void onSuccess(QGUserInfo bean) {
				LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_VERIFYCODE;
				if (bean.getId() == 40040&&bean.getCheckrealname()==0) {
					int mRealNameFlag=bean.getCheckrealname();
					if (mRealNameFlag!=-1&&mRealNameFlag!=0){
						saveAccountInfo(mPhoneNumber, bean.getAuthtoken());
						if (isNeedActive(bean)) {
							BaseFragment fragment = new ActiveFragment();
							fragment.setTag(bean.getAuthtoken());
							QGFragmentManager.getInstance(mActivity).add(fragment);
						} else {
							switchToCerificationFragment(CerificationNode.ON_LOGIN);
						}
					}else{
						showLimitedDialog(AccountLoginFragment.this, bean.getMessage());
					}
				} else {
					saveAccountInfo(mPhoneNumber, bean.getAuthtoken());
					if (isNeedActive(bean)) {
						BaseFragment fragment = new ActiveFragment();
						fragment.setTag(bean.getAuthtoken());
						QGFragmentManager.getInstance(mActivity).add(fragment);
					} else {
						switchToCerificationFragment(CerificationNode.ON_LOGIN);
					}
				}
			}

			@Override
			public void onFailed(int id, String message) {
				showToast(message);
			}
		}.addParameter(
				new QGParameter(mActivity).addParameter("phone", mPhoneNumber).addParameter("code", mIdentfyingCode)
						.create()).post().setUrl(Constant.HOST + Constant.PHOEN_LOGIN);

		DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);

	}
	public void autoLoginByToken(String authToken,int isGameBoxAuth) {
		// QGProgressBar.disable();
		HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
			@Override
			public void onSuccess(QGUserInfo bean) {
				try {
					Log.d("hermesgame.AutoLoginFrag","bean.token: "+bean.getAuthtoken());

					LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_AUTO;
					if (bean.getId() == 40040&&bean.getCheckrealname()==0) {
						showLimitedDialog(AccountLoginFragment.this,bean.getMessage());
						mIsSupportBack = true;
						forceBack();
					} else {
						saveAccountInfo(bean.getUserdata().getUsername(), bean.getAuthtoken());
						if (isNeedActive(bean)) {
							BaseFragment fragment = new ActiveFragment();
							fragment.setTag(bean.getAuthtoken());
							QGFragmentManager.getInstance(mActivity).add(fragment);
						} else {
							switchToCerificationFragment(CerificationNode.ON_LOGIN);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailed(int id, String message) {
				showToast(message);
				mIsSupportBack = true;
				forceBack();
			}
		}.addParameter(new QGParameter(mActivity).addParameter("authToken", authToken).addParameter("isGameBoxAuth",isGameBoxAuth).create()).post()
				.setUrl(Constant.HOST + Constant.AUTO_TOKEN);

		DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);

	}

	public void showOKLogin(){
		mOKLogin.setEnabled(false);
		QGCallBack okloginCallback=new QGCallBack() {
			@Override
			public void onSuccess() {

			}

			@Override
			public void onFailed(String msg) {
				Log.d("hermesgame.OKLogin.Fail",msg);
				mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (QGConfig.isSupportPhone()) {
								mPhoneLoginView.setVisibility(View.GONE);
								mAccountLogin.setVisibility(View.VISIBLE);
								phone_fram.setVisibility(View.VISIBLE);
								account_fram.setVisibility(View.GONE);
								isAccountLogin = false;
							} else {
								mPhoneLoginView.setVisibility(View.GONE);
								mAccountLogin.setVisibility(View.GONE);
								phone_fram.setVisibility(View.GONE);
								account_fram.setVisibility(View.VISIBLE);
								isAccountLogin = true;
							}
							qg_login_layout_main.setVisibility(View.VISIBLE);
						}
					});
			}
		};
		if (ThirdManager.getInstance().checkOKLogin(okloginCallback)) {
			ThirdManager.getInstance().showOKLogin(mActivity,mLoginCallBack);
		} else {
			Toast.makeText(mActivity, "请检查是否插入SIM卡和是否打开移动网络,或尝试验证码登录方式~", Toast.LENGTH_LONG).show();
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (QGConfig.isSupportPhone()) {
							mPhoneLoginView.setVisibility(View.GONE);
							mAccountLogin.setVisibility(View.VISIBLE);
							phone_fram.setVisibility(View.VISIBLE);
							account_fram.setVisibility(View.GONE);
							isAccountLogin = false;
						} else {
							mPhoneLoginView.setVisibility(View.GONE);
							mAccountLogin.setVisibility(View.GONE);
							phone_fram.setVisibility(View.GONE);
							account_fram.setVisibility(View.VISIBLE);
							isAccountLogin = true;
						}
						qg_login_layout_main.setVisibility(View.VISIBLE);
					}
				});

		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mOKLogin.setEnabled(true);
			}
		},3000); // 延时1秒
	}
}
