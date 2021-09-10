package com.hermesgamesdk.fragment.usercenter;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;

import com.hermesgamesdk.activity.TempActivty;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.QGUserExtraInfo;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.manager.SliderBarV2Manager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.QGEditText;

/**
 * 手机解绑
 */
public class PhoneUnbindFragment extends BaseFragment {

	private static final String TAG = "hermesgame";
	private QGEditText mPhoneNumberEdt;
	private QGEditText mIdentfyCodeEdt;
	private Button mPhoneUnbindIdentfyButton;
	private Button mPhoneUnbindSubmit;
	private String identifyCode;
	private TextView ed_title_pNum;
	private TextView ed_title_pCode;
	private Button qg_line_pNum;
	private Button qg_line_pCode;
	QGUserInfo userInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);

	@Override
	protected String getRootName() {
		return "R.layout.qg_fragment_phone_unbind";
	}

	@Override
	protected String getTitle() {
		return "R.string.qg_phoneunbind_phone_unbind";
	}

	@Override
	protected void onRootViewInflated(View root) {
		initView(root);
		initData();
	}

	private void initData() {

		QGUserExtraInfo qgUserExtraInfo = (QGUserExtraInfo) DataManager.getInstance().getData(
				Constant.USEREXTRAINFO_KEY);
		if (qgUserExtraInfo != null&&qgUserExtraInfo.getPhone()!=null&&!qgUserExtraInfo.getPhone().equals("")){
			mPhoneNumberEdt.setText(qgUserExtraInfo.getPhone());
			mPhoneNumberEdt.setEnabled(false);
		}

	}

	private void initView(View root) {
		mPhoneNumberEdt = (QGEditText) findView("R.id.qg_phone_unbind_num");
		mIdentfyCodeEdt = (QGEditText) findView("R.id.qg_phone_unbind_identify");
		mPhoneUnbindIdentfyButton = (Button) findView("R.id.qg_phone_unbind_identify_button");
		mPhoneNumberEdt.setInputType(InputType.TYPE_CLASS_PHONE);
		mIdentfyCodeEdt.setInputType(InputType.TYPE_CLASS_NUMBER);
		mPhoneNumberEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
		mIdentfyCodeEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
		mPhoneUnbindSubmit = (Button) findView("R.id.qg_phone_unbind_submit");
		mPhoneUnbindIdentfyButton.setOnClickListener(listener);
		mPhoneUnbindSubmit.setOnClickListener(listener);
		ed_title_pNum= (TextView) findView("R.id.ed_title_pNum");
		ed_title_pCode= (TextView) findView("R.id.ed_title_pCode");
		qg_line_pNum= (Button) findView("R.id.qg_line_pNum");
		qg_line_pCode= (Button) findView("R.id.qg_line_pCode");

		/*if (mActivity instanceof FloatActivity) {
			mTitleBar.hideCloseIcon();
		}*/
		mTitleBar.hideCloseIcon();
		if(userInfo.getUserdata().getMobile()!=null&&!userInfo.getUserdata().getMobile().isEmpty()){
			mPhoneNumberEdt.setText(userInfo.getUserdata().getMobile());
			mPhoneNumberEdt.getEt().setFocusableInTouchMode(false);
			mPhoneNumberEdt.getEt().setKeyListener(null);
			mPhoneNumberEdt.getEt().setFocusable(false);
			mPhoneNumberEdt.getClose().setVisibility(View.GONE);

		}
		mPhoneNumberEdt.addTextChangedListener(new QGEditText.TextChangedListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ed_title_pNum.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(mPhoneNumberEdt.getText().length()>0){
					qg_line_pNum.setEnabled(true);
				}else {
					qg_line_pNum.setEnabled(false);
				}
				if (mPhoneNumberEdt.getText().length()==11){
					mPhoneUnbindIdentfyButton.setEnabled(true);
				}else {
					mPhoneUnbindIdentfyButton.setEnabled(false);
				}

				changeButtonColor();
			}
		});
		mPhoneNumberEdt.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
			@Override
			public void onFocusChange( boolean hasFocus) {
				if (!hasFocus){
					ed_title_pNum.setVisibility(View.GONE);
					qg_line_pNum.setEnabled(false);
				}else {
					if (mPhoneNumberEdt.getText().length()>0){
						ed_title_pNum.setVisibility(View.VISIBLE);
						qg_line_pNum.setEnabled(true);
					}
				}

			}
		});
		mIdentfyCodeEdt.addTextChangedListener(new QGEditText.TextChangedListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ed_title_pCode.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(mIdentfyCodeEdt.getText().length()>0){
					qg_line_pCode.setEnabled(true);
				}else {
					qg_line_pCode.setEnabled(false);
				}
				changeButtonColor();
			}
		});
		mIdentfyCodeEdt.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
			@Override
			public void onFocusChange( boolean hasFocus) {
				if (!hasFocus){
					ed_title_pCode.setVisibility(View.GONE);
					qg_line_pCode.setEnabled(false);
				}else {
					if (mIdentfyCodeEdt.getText().length()>0){
						ed_title_pCode.setVisibility(View.VISIBLE);
						qg_line_pCode.setEnabled(true);
					}
				}

			}
		});
	}
	public void changeButtonColor(){
		if (mPhoneNumberEdt.getText().length()>0&&mIdentfyCodeEdt.getText().length()>0){
			mPhoneUnbindSubmit.setEnabled(true);

		}else{
			mPhoneUnbindSubmit.setEnabled(false);
		}
	}

	@Override
	public void onClicks(int id) {
		if (id == mPhoneUnbindIdentfyButton.getId()) {
			// 发送验证码
			requestIdentfyingCode(mPhoneNumberEdt,mPhoneUnbindIdentfyButton, mPhoneNumberEdt.getText().toString().trim(),
					Constant.SMS_TYPE_UNBIND,0);
		}
		if (id == mPhoneUnbindSubmit.getId())
			unbindPhone();
	}

	private void unbindPhone() {
		if (!checkIdentifyCode()) {
			return;
		}

		if (userInfo == null) {
			showToast("R.string.toast_text_get_userinfo_failed");
			return;
		}
		String account="";
		String password="";
		int resetUser=0;
		if (mActivity.getIntent().getStringExtra("from").equals("slider_mobile_bind_user")){
			try{
				account= QGSdkUtils.getString(mActivity,"tempUser");
				password= QGSdkUtils.getString(mActivity,"tempPass");
			}catch (Exception e){
				password="";
				account="";
			}
			resetUser=1;

		}else{

		}
		Log.d(TAG, "  from:"+mActivity.getIntent().getStringExtra("from"));
		String parameter = new QGParameter(mActivity)
				.addParameter("username",account)
				.addParameter("password",QGSdkUtils.getMD5Str(password))
				.addParameter("uid", userInfo.getUserdata().getUid())
				.addParameter("resetUser",resetUser)
				.addParameter("code", identifyCode).create();
		HttpRequest<String> request = new HttpRequest<String>() {
			@Override
			public void onSuccess(String bean) {
				showToast("R.string.toast_text_phone_unbind_success");

				// 修改
				QGUserExtraInfo userExtraInfo = ((QGUserExtraInfo) DataManager.getInstance().getData(
						Constant.USEREXTRAINFO_KEY));
				userExtraInfo.setPhone("");
				userExtraInfo.setIsBindPhone(QGUserExtraInfo.IS_UNBIND_PHONE);
				userInfo.getUserdata().setMobile("");
				if (mActivity instanceof TempActivty) {
					if(mActivity.getIntent().getStringExtra("from").equals("slider_mobile_bind_user")){
						mActivity.setResult(2);
						LoginManager.getInstance().deleteTopAccount(mActivity);
						LoginManager.getInstance().logout(mActivity);
					}else{
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//SliderBarV2Manager.getInstance(mActivity).resetMobVer("",false);
								SliderBarV2Manager.getInstance(mActivity).retry();
							}
						});
						mActivity.setResult(1);
					}
					mActivity.finish();
				}else {
					forceBack();
				}
			
			}

			@Override
			public void onFailed(int id,String message) {
				showToast(getString("R.string.toast_text_phone_unbind_failed") + message);
				Log.d(TAG, "解绑失败 :" + message);
			}
		}.addParameter(parameter).post().setUrl(Constant.HOST + Constant.PHONE_UNBIND);
		DataManager.getInstance().requestHttp(request);
	}

	/**
	 * 验证码检查
	 */
	private boolean checkIdentifyCode() {
		identifyCode = mIdentfyCodeEdt.getText().trim();
		if (TextUtils.isEmpty(identifyCode)) {
			showToast("R.string.toast_text_input_verificationcode");
			return false;
		}
		return true;
	}

}
