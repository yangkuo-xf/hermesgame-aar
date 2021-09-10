package com.hermesgamesdk.fragment.login;

import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;

/**
 * Created by Administrator on 2017/9/21.
 */

public class AutoLoginFragment extends BaseFragment {
	private TextView username;
	private TextView timer;
	private TextView switchAccount;
	private String accountFromFile;
	private String passwordFromFile;
	private ImageView qg_autologin_pic;
	AnimationDrawable animationDrawable;

	@Override
	protected String getRootName() {
		return "R.layout.qg_fragment_auto_login";
	}

	@Override
	protected String getTitle() {
		return null;
	}

	@Override
	protected void onRootViewInflated(View root) {
		initView();
		downTimer.start();
	}

	private CountDownTimer downTimer = new CountDownTimer(3000, 1000) {
		@Override
		public void onTick(long millisUntilFinished) {
			timer.setText(getString("R.string.qg_autologin_timer") + millisUntilFinished / 1000 + getString("R.string.qg_autologin_seconds") );
		}

		@Override
		public void onFinish() {
			mIsSupportBack = false;
			if (mIsSupportBack) {

			}
			if (passwordFromFile.length() <= 30) {
				login(accountFromFile, passwordFromFile);
			} else {
				autoLoginByToken(passwordFromFile);
			}

		}

	};

	private void autoLoginByToken(String authToken) {
		// QGProgressBar.disable();
		HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
			@Override
			public void onSuccess(QGUserInfo bean) {
				try {
					LoginManager.getInstance().loginType= Constant.LOGIN_TYPE_AUTO;
					if (bean.getId() == 40040&&bean.getCheckrealname()==0) {
						showLimitedDialog(AutoLoginFragment.this,bean.getMessage());
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
		}.addParameter(new QGParameter(mActivity).addParameter("authToken", authToken).create()).post()
				.setUrl(Constant.HOST + Constant.AUTO_TOKEN);

		DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);

	}

	private void initView() {
		username = (TextView) findView("R.id.qg_auto_login_username");
		timer = (TextView) findView("R.id.qg_auto_login_timer");
		switchAccount = (TextView) findView("R.id.qg_switch_account");
		switchAccount.setOnClickListener(listener);
		accountFromFile = (String) DataManager.getInstance().getData("accountFromFile");
		passwordFromFile = (String) DataManager.getInstance().getData("passwordFromFile");
		username.setText(getString("R.string.qg_account_name")+"： " + accountFromFile);
		qg_autologin_pic = (ImageView) findView("R.id.qg_autologin_pic");
		//qg_autologin_pic.setBackgroundResource(QGSdkUtils.getResId(mActivity, "R.drawable.auto_login_anim"));
		animationDrawable = (AnimationDrawable) qg_autologin_pic.getBackground();
	}

	@Override
	public void onStart() {
		super.onStart();
		animationDrawable.start();
	}

	@Override
	public void onClicks(int id) {
		if (id == switchAccount.getId()) {
			LoginManager.getInstance().isAutoLogin = false;
			back();
			downTimer.cancel();
		}
	}

	private void login(final String account, final String password) {
		// QGProgressBar.disable();
		HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
			@Override
			public void onSuccess(QGUserInfo bean) {
				try {
					if (bean.getId() == 40040&&bean.getCheckrealname()==0) {
						showLimitedDialog(AutoLoginFragment.this,bean.getMessage());
						mIsSupportBack = true;
						forceBack();
					}else{
							// 保存账号密码
							saveAccountInfo(account, password);
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
				LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_ACCOUNT;
			}

			@Override
			public void onFailed(int id, String message) {
				showToast(message);
				mIsSupportBack = true;
				forceBack();
			}
		}.addParameter(
				new QGParameter(mActivity).addParameter("username", account)
						.addParameter("password", QGSdkUtils.getMD5Str(password)).create()).post()
				.setUrl(Constant.HOST + Constant.ACCOUNT_LOGIN);

		DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);

	}

	@Override
	public void onBackInvoke() {
		downTimer.cancel();
	}

	@Override
	public void onBackForeground() {
		forceBack();
	}

}
