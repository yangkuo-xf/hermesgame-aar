package com.hermesgamesdk.fragment;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
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
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.fragment.usercenter.BindUsernameFragment;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.manager.SliderBarV2Manager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.view.AlertDialog;
import com.hermesgamesdk.view.QGEditText;

/**
 * Created by user on 2017/9/13.
 */

public class PhoneBindFragment extends BaseFragment {

	public static final String TAG = "PhoneBindFragment";

	private QGEditText mPhoneView;
	private QGEditText mPhoneBindIdentfy;
	private Button mIdentfyingCodeView;
	private Button mPhoneBindSubmit;
	private String mPhoneNumber;
	private String mIdentfyingCode;
	private TextView ed_title_phone;
	private TextView ed_title_code;

	private Button line_phone;
	private Button line_code;

	@Override
	protected String getRootName() {
		return "R.layout.qg_fragment_phone_bind";
	}

	@Override
	protected String getTitle() {
		return "R.string.qg_phonebind_bind_phonenumb";
	}

	@Override
	protected void onRootViewInflated(View root) {
		initView(root);
	}

	private void initView(View root) {
		mPhoneView = (QGEditText) findView("R.id.qg_phone_bind_num");
		mPhoneBindIdentfy = (QGEditText) findView("R.id.qg_phone_bind_identify");
		mIdentfyingCodeView = (Button) findView("R.id.qg_phone_bind_identify_button");
		mPhoneBindSubmit = (Button) findView("R.id.qg_phone_bind_submit");
		line_phone = (Button) findView("R.id.qg_line_phone");
		line_code = (Button) findView("R.id.qg_line_code");
		ed_title_phone = (TextView) findView("R.id.ed_title_phone");
		ed_title_code = (TextView) findView("R.id.ed_title_code");
		mPhoneView.setInputType(InputType.TYPE_CLASS_PHONE);
		mPhoneBindIdentfy.setInputType(InputType.TYPE_CLASS_NUMBER);
		mPhoneView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
		mPhoneBindIdentfy.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
		mPhoneView.addTextChangedListener(new QGEditText.TextChangedListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				ed_title_phone.setVisibility(s.length() > 0 ? View.VISIBLE
						: View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mPhoneView.getText().length() > 0) {
					line_phone.setEnabled(true);
				} else {
					line_phone.setEnabled(false);
				}
				if (mPhoneView.getText().length() == 11) {
					mIdentfyingCodeView.setEnabled(true);
				} else {
					mIdentfyingCodeView.setEnabled(false);
				}

				changeButtonColor();
			}
		});
		mPhoneView
				.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
					@Override
					public void onFocusChange(boolean hasFocus) {
						if (!hasFocus) {
							ed_title_phone.setVisibility(View.GONE);
							line_phone.setEnabled(false);
						} else {
							if (mPhoneView.getText().length() > 0) {
								ed_title_phone.setVisibility(View.VISIBLE);
								line_phone.setEnabled(true);
							}
						}

					}
				});
		mPhoneBindIdentfy
				.addTextChangedListener(new QGEditText.TextChangedListener() {
					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						ed_title_code.setVisibility(s.length() > 0 ? View.VISIBLE
								: View.GONE);
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (mPhoneBindIdentfy.getText().length() > 0) {
							line_code.setEnabled(true);
						} else {
							line_code.setEnabled(false);
						}
						changeButtonColor();
					}
				});
		mPhoneBindIdentfy
				.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
					@Override
					public void onFocusChange(boolean hasFocus) {
						if (!hasFocus) {
							ed_title_code.setVisibility(View.GONE);
							line_code.setEnabled(false);
						} else {
							if (mPhoneBindIdentfy.getText().length() > 0) {
								ed_title_code.setVisibility(View.VISIBLE);
								line_code.setEnabled(true);
							}
						}

					}
				});
		mTitleBar.hideCloseIcon();
		// 验证码按钮
		mIdentfyingCodeView.setOnClickListener(listener);
		// 提交按钮
		mPhoneBindSubmit.setOnClickListener(listener);
		/*
		 * if (mActivity instanceof FloatActivity) { mTitleBar.hideCloseIcon();
		 * }
		 */

	}

	@Override
	public void onClicks(int id) {
		if (id == mIdentfyingCodeView.getId()) {
			// 发送验证码

			mPhoneNumber = mPhoneView.getText().trim();
			requestIdentfyingCode(mPhoneView,mIdentfyingCodeView, mPhoneNumber,
					Constant.SMS_TYPE_BIND,0);
		}
		if (id == mPhoneBindSubmit.getId()) {
			bindPhone();
		}
	}

	private void changeButtonColor() {
		if (mPhoneView.getText().length() > 0
				&& mPhoneBindIdentfy.getText().length() > 0) {
			mPhoneBindSubmit.setEnabled(true);

		} else {
			mPhoneBindSubmit.setEnabled(false);
		}
	}

	private void bindPhone() {

		mPhoneNumber = mPhoneView.getText().toString();
		mIdentfyingCode = mPhoneBindIdentfy.getText().toString();
		if (TextUtils.isEmpty(mPhoneNumber)) {
			showToast("R.string.toast_text_input_phonenumb");
			return;
		}

		if (TextUtils.isEmpty(mIdentfyingCode)) {
			showToast("R.string.toast_text_input_verificationcode");
			return;
		}

		QGUserInfo userInfo = (QGUserInfo) DataManager.getInstance().getData(
				Constant.USERINFO_KEY);
		if (userInfo == null) {
			showToast("R.string.toast_text_get_verificationcode_failed");
			return;
		}
		final String username = userInfo.getUserdata().getUsername();
		final int userState = userInfo.getUserdata().getIsGuest();
		Log.d(TAG, "mIdentfyingCode = " + mIdentfyingCode);
		String parameter = new QGParameter(mActivity)
				.addParameter("uid", userInfo.getUserdata().getUid())
				.addParameter("phone", mPhoneNumber)
				.addParameter("code", mIdentfyingCode).create();
		HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
			@Override
			public void onSuccess(QGUserInfo bean) {
				showToast("R.string.toast_text_bind_success");
				Log.d(TAG, "绑定成功");
				// 修改
				LoginManager.getInstance().requestQGUserExtraInfo();
				if (userState == Constant.GUEST_ACCOUNT) {
					String authToken = bean.getAuthtoken();
					saveAccountInfo(mPhoneNumber, authToken);
					saveAccountInfo(username, null);
				}
				if (mActivity instanceof TempActivty) {
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							SliderBarV2Manager.getInstance(mActivity).resetMobVer(mPhoneNumber,true);
						}
					});

					getActivity().setResult(1);
					mActivity.finish();
				} else {
					mActivity.finish();
				}

			}

			@Override
			public void onFailed(int id,String message) {
				Log.d(TAG, "绑定失败:" + message);
				if (id==40017){
					showChooseDialog(mActivity,message);
				}else{
					if (mActivity instanceof TempActivty) {
						getActivity().setResult(0);
						mActivity.finish();
					}
				}

			}
		}.addParameter(parameter).post()
				.setUrl(Constant.HOST + Constant.PHONE_BIND);

		DataManager.getInstance().requestHttp(request,
				Constant.USERINFO_KEY);
	}

	public void showChooseDialog(final Activity activity,String message ){
		QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		String rightName="";
		String mMessage="该手机号已绑定其他账号,请切换其他手机号进行绑定。";
		if (info.getUserdata().getIsGuest()==1){
			rightName="绑定帐号";
			mMessage="该手机号已绑定其他账号,请切换其他手机号进行绑定或者使用用户名绑定。";
		}
		final AlertDialog alertDialog=new AlertDialog(activity, null, "温馨提示", mMessage, "更换手机", rightName) {
			@Override
			public void onDismiss() {
				mPhoneView.getEt().setText("");
				mPhoneBindIdentfy.getEt().setText("");
			}
		};
		alertDialog.setClickListener(new AlertDialog.onClick() {
			@Override
			public void onLeftClick() {
				mPhoneView.getEt().setText("");
				mPhoneBindIdentfy.getEt().setText("");
				alertDialog.dismiss();
			}

			@Override
			public void onRightClick() {
				mPhoneView.getEt().setText("");
				mPhoneBindIdentfy.getEt().setText("");
				alertDialog.dismiss();
				QGFragmentManager.getInstance((FragmentActivity) activity).add(new BindUsernameFragment());
			}
		});
		alertDialog.show();
	}

}
