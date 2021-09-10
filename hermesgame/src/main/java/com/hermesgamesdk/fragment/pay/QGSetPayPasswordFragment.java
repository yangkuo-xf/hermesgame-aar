package com.hermesgamesdk.fragment.pay;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.SliderBarV2Manager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.QGEditText;

public class QGSetPayPasswordFragment extends BaseFragment {
    private QGEditText mPhoneView;
    private QGEditText mPhoneBindIdentfy;
    private QGEditText mPassword;
    private Button mIdentfyingCodeView;
    private Button mSetPassWordubmit;
    private Button line_phone;
    private Button line_code;
    private Button line_pass;

    private TextView ed_title_phone;
    private TextView ed_title_code;
    private TextView ed_title_pass;
    QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);

    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_set_pay_password";
    }

    @Override
    protected String getTitle() {
        return "设置支付密码";
    }

    @Override
    protected void onRootViewInflated(View root) {
        initView(root);
    }

    public void initView(View root){
        mPhoneView = (QGEditText) findView("R.id.qg_set_pay_password_num");
        mPhoneBindIdentfy = (QGEditText) findView("R.id.qg_set_pay_password_code");
        mPassword= (QGEditText) findView("R.id.qg_set_pay_password_password");
        mIdentfyingCodeView = (Button) findView("R.id.qg_set_pay_password_button");
        mSetPassWordubmit = (Button) findView("R.id.qg_set_pay_password_submit");
        line_phone = (Button) findView("R.id.qg_line_phone");
        line_code = (Button) findView("R.id.qg_line_code");
        line_pass = (Button) findView("R.id.qg_line_password");
        ed_title_phone = (TextView) findView("R.id.ed_title_phone");
        ed_title_code = (TextView) findView("R.id.ed_title_code");
        ed_title_pass= (TextView) findView("R.id.ed_title_password");
        mPhoneView.setInputType(InputType.TYPE_CLASS_PHONE);
        mPhoneBindIdentfy.setInputType(InputType.TYPE_CLASS_NUMBER);
        mPhoneView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        mPhoneBindIdentfy.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        mPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        if(info.getUserdata().getMobile()!=null&&!info.getUserdata().getMobile().isEmpty()){
            mPhoneView.setText(info.getUserdata().getMobile());
            mPhoneView.getEt().setFocusableInTouchMode(false);
            mPhoneView.getEt().setKeyListener(null);
            mPhoneView.getEt().setFocusable(false);
            mPhoneView.getClose().setVisibility(View.GONE);
            line_phone.setEnabled(true);
            mIdentfyingCodeView.setEnabled(true);
        }
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
        mPassword
                .addTextChangedListener(new QGEditText.TextChangedListener() {
                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        ed_title_pass.setVisibility(s.length() > 0 ? View.VISIBLE
                                : View.GONE);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (mPhoneBindIdentfy.getText().length() > 0) {
                            line_pass.setEnabled(true);
                        } else {
                            line_pass.setEnabled(false);
                        }
                        changeButtonColor();
                    }
                });
        mPassword
                .addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(boolean hasFocus) {
                        if (!hasFocus) {
                            ed_title_pass.setVisibility(View.GONE);
                            line_pass.setEnabled(false);
                        } else {
                            if (mPhoneBindIdentfy.getText().length() > 0) {
                                ed_title_pass.setVisibility(View.VISIBLE);
                                line_pass.setEnabled(true);
                            }
                        }

                    }
                });
        mTitleBar.hideCloseIcon();
        // 验证码按钮
        mIdentfyingCodeView.setOnClickListener(listener);
        // 提交按钮
        mSetPassWordubmit.setOnClickListener(listener);
    }

    private void changeButtonColor() {
        if (mPhoneView.getText().length() > 0
                && mPhoneBindIdentfy.getText().length() > 0&&mPassword.getText().length()>0) {
            mSetPassWordubmit.setEnabled(true);

        } else {
            mSetPassWordubmit.setEnabled(false);
        }
    }

    @Override
    public void onClicks(int id) {
        if (id == mIdentfyingCodeView.getId()) {
            // 发送验证码

            String  mPhoneNumber = mPhoneView.getText().trim();
            requestIdentfyingCode(mPhoneView,mIdentfyingCodeView, mPhoneNumber,
                    Constant.SMS_TYPE_LOGIN,1);
        }
        if (id == mSetPassWordubmit.getId()) {
            setPass();
        }
    }
    private void setPass() {

        String mPhoneNumber = mPhoneView.getText().trim();
        String mIdentfyingCode = mPhoneBindIdentfy.getText().trim();
        String password = mPassword.getText().trim();
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

        String parameter = new QGParameter(mActivity)
                .addParameter("uid", userInfo.getUserdata().getUid())
                .addParameter("phone", mPhoneNumber)
                .addParameter("code", mIdentfyingCode)
                .addParameter("password", QGSdkUtils.getMD5Str(password)).create();
        HttpRequest<String> request = new HttpRequest<String>() {
            @Override
            public void onSuccess(String bean) {
                showToast("设置成功~");
                if (mActivity.getIntent().getStringExtra("from")!=null){
                    if (mActivity.getIntent().getStringExtra("from").equals("slider_resetPayPassWord")){
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("hermesgame","设置支付密码成功");
                                SliderBarV2Manager.getInstance(mActivity).retry();
                            }
                        });
                    }
                }
                   forceBack();
            }

            @Override
            public void onFailed(int id,String message) {
                showToast("设置失败:"
                        + message);
            }
        }.addParameter(parameter).post()
                .setUrl(Constant.HOST + Constant.SET_PAY_PASS);

        DataManager.getInstance().requestHttp(request);
    }
}
