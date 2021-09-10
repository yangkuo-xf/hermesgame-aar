package com.hermesgamesdk.fragment.login;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;

import com.hermesgamesdk.activity.TempActivty;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.QGEditText;

/**
 * Created by Administrator on 2017/9/12.
 */

public class FindPasswordFragment extends BaseFragment {
	private Button mGetIdentfyingCodeButton;
	private QGEditText mPhoneView;
	private QGEditText mIdentfyingCodeView;
	private QGEditText mPasswordEdt;
	private String mPhoneNumber;
	private String mIdentfyingCode;
	private String mPassword;
	private ImageView mEye;
	private Button mConfirmBtn;

	private TextView ed_title_resetPhone,ed_title_resetCode,ed_title_newCode;
	private Button qg_line_resetPhone,qg_line_reseCode,qg_line_newCode;

	@Override
	protected String getRootName() {
		return "R.layout.qg_fragment_find_password";
	}

	@Override
	protected String getTitle() {
		return "R.string.qg_findpsw_reset";
	}

	@Override
	protected void onRootViewInflated(View root) {
		initView(root);
	}

	public void initView(View root) {
		mPhoneView = (QGEditText) findView("R.id.qg_ed_phone");
		mIdentfyingCodeView = (QGEditText) findView("R.id.qg_ed_identifying_code");
		mPasswordEdt = (QGEditText) findView("R.id.qg_ed_password");

		mPhoneView.setInputType(InputType.TYPE_CLASS_PHONE);
		mIdentfyingCodeView.setInputType(InputType.TYPE_CLASS_NUMBER);
		mPhoneView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
		mIdentfyingCodeView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

		mEye = (ImageView) findView("R.id.qg_img_eye");
		mGetIdentfyingCodeButton = (Button) findView("R.id.qg_btn_get_identifying_code");
		mConfirmBtn = (Button) findView("R.id.qg_btn_confirm");
		mGetIdentfyingCodeButton.setOnClickListener(listener);
		mEye.setOnClickListener(listener);
		mConfirmBtn.setOnClickListener(listener);

		ed_title_resetPhone=(TextView)findView("R.id.ed_title_resetPhone");
		ed_title_resetCode=(TextView)findView("R.id.ed_title_resetCode");
		ed_title_newCode=(TextView)findView("R.id.ed_title_newCode");

		qg_line_resetPhone=(Button) findView("R.id.qg_line_resetPhone");
		qg_line_reseCode=(Button)findView("R.id.qg_line_reseCode");
		qg_line_newCode=(Button)findView("R.id.qg_line_newCode");

		mPhoneView.addTextChangedListener(new QGEditText.TextChangedListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ed_title_resetPhone.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(mPhoneView.getText().length()>0){
					qg_line_resetPhone.setEnabled(true);
				}else {
					qg_line_resetPhone.setEnabled(false);
				}
				if (mPhoneView.getText().length()==11){
					mGetIdentfyingCodeButton.setEnabled(true);
				}else {
					mGetIdentfyingCodeButton.setEnabled(false);
				}

				changeButtonColor();
			}
		});

		mPhoneView.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
			@Override
			public void onFocusChange( boolean hasFocus) {
				if (!hasFocus){
					ed_title_resetPhone.setVisibility(View.GONE);
					qg_line_resetPhone.setEnabled(false);
				}else {
					if (mPhoneView.getText().length()>0){
						ed_title_resetPhone.setVisibility(View.VISIBLE);
						qg_line_resetPhone.setEnabled(true);
					}
				}

			}
		});
		mPasswordEdt.addTextChangedListener(new QGEditText.TextChangedListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ed_title_newCode.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(mPasswordEdt.getText().length()>0){
					qg_line_newCode.setEnabled(true);
				}else {
					qg_line_newCode.setEnabled(false);
				}
				changeButtonColor();
			}
		});
		mPasswordEdt.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
			@Override
			public void onFocusChange( boolean hasFocus) {
				if (!hasFocus){
					ed_title_newCode.setVisibility(View.GONE);
					qg_line_newCode.setEnabled(false);
				}else {
					if (mPhoneView.getText().length()>0){
						ed_title_newCode.setVisibility(View.VISIBLE);
						qg_line_newCode.setEnabled(true);
					}
				}

			}
		});

		mIdentfyingCodeView.addTextChangedListener(new QGEditText.TextChangedListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ed_title_resetCode.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(mIdentfyingCodeView.getText().length()>0){
					qg_line_reseCode.setEnabled(true);
				}else {
					qg_line_reseCode.setEnabled(false);
				}
				changeButtonColor();
			}
		});
		mIdentfyingCodeView.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
			@Override
			public void onFocusChange( boolean hasFocus) {
				if (!hasFocus){
					ed_title_resetCode.setVisibility(View.GONE);
					qg_line_reseCode.setEnabled(false);
				}else {
					if (mIdentfyingCodeView.getText().length()>0){
						ed_title_resetCode.setVisibility(View.VISIBLE);
						qg_line_reseCode.setEnabled(true);
					}
				}

			}
		});
		mTitleBar.hideCloseIcon();

	}
	private void changeButtonColor() {
		if (mPhoneView.getText().length()>0&&mPasswordEdt.getText().length()>0&&mIdentfyingCodeView.getText().length()>0){
			mConfirmBtn.setEnabled(true);

		}else{
			mConfirmBtn.setEnabled(false);
		}
	}
	@Override
	public void onClicks(int id) {
		if (id == mGetIdentfyingCodeButton.getId()) {
			mPhoneNumber = mPhoneView.getText().toString();
			requestIdentfyingCode(mPhoneView,mGetIdentfyingCodeButton, mPhoneNumber, Constant.SMS_TYPE_FIND_PASSWORD,0);
		}

		if (id == mEye.getId())
			changeEyeState(mEye, mPasswordEdt);
		if (id == mConfirmBtn.getId())
			findPassword();
	}

	public void findPassword() {
		mPhoneNumber = mPhoneView.getText().toString();
		mIdentfyingCode = mIdentfyingCodeView.getText().toString();
		mPassword = mPasswordEdt.getText().toString();
		if (TextUtils.isEmpty(mPhoneNumber)) {
			showToast("R.string.toast_text_input_phonenumb");
			return;
		}

		if (TextUtils.isEmpty(mIdentfyingCode)) {
			showToast("R.string.toast_text_input_verificationcode");
			return;
		}

		if (TextUtils.isEmpty(mPassword)) {
			showToast("R.string.toast_text_input_psw");
			return;
		}

		if (mPassword.length() < 6) {
			showToast("R.string.toast_text_register_notice_length_error");
			return;
		}

		HttpRequest<String> request = new HttpRequest<String>() {
			@Override
			public void onSuccess(String message) {
				try {
					showToast("R.string.toast_text_register_psw_success");
					saveAccountInfo(mPhoneNumber, mPassword);
					if (getActivity() instanceof TempActivty) {
						getActivity().setResult(1);
						getActivity().finish();
					}else {
						forceBack();
					}
				
				} catch (Exception e) {
				}
			}

			@Override
			public void onFailed(int id,String message) {
				showToast(message);
			}
		}.addParameter(
				new QGParameter(mActivity)
				.addParameter("phone", mPhoneNumber)
				.addParameter("code", mIdentfyingCode)
				.addParameter("newPassword", QGSdkUtils.getMD5Str(mPassword))
				.create()).post()
				.setUrl(Constant.HOST + Constant.FIND_PASSWORD);

		DataManager.getInstance().requestHttp(request);

	}
}
