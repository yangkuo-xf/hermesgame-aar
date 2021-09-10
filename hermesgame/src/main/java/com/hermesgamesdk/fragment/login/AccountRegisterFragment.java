package com.hermesgamesdk.fragment.login;


import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hermesgamesdk.QGManager;
import com.hermesgamesdk.activity.NoticeActivity;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.QGEditText;
import com.quickjoy.adplus.ADP;

/**
 * Created by Administrator on 2017/9/11.
 */

public class AccountRegisterFragment extends BaseFragment {
    private QGEditText usernameView;
    private QGEditText passwordView;
    private Button registerView;
    private String username;
    private String password;
    private ImageView mUsernameCheckView;

    private ImageView mSwitchTextType;
    private TextView userAgreementView;

    private TextView privateAgreementView;
    private TextView ed_title_rgst_account,ed_title_rgst_pwd;
    private Button qg_line_rgst_account,qg_line_rgst_pwd;

    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_account_register";
    }

    @Override
    protected void onRootViewInflated(View root) {
        initView(root);
       /* if (QGConfig.isAutoOpenAgreement()) {
            userAgreementView.performClick();
        }*/
    }

    @Override
    protected String getTitle() {
        return getString("R.string.qg_account_regist");
    }

    private void initView(View root) {
        usernameView = (QGEditText) findView("R.id.qg_register_username");
        passwordView = (QGEditText) findView("R.id.qg_register_password");
        registerView = (Button) findView("R.id.qg_register");
        mUsernameCheckView = (ImageView) findView("R.id.qg_username_check");
        mSwitchTextType = (ImageView) findView("R.id.qg_switch_text_type");
        userAgreementView = (TextView) findView("R.id.qk_tv_user_agreement");
        privateAgreementView = (TextView) findView("R.id.qk_tv_user_private");
        ed_title_rgst_account=(TextView)  findView("R.id.ed_title_rgst_account");
        ed_title_rgst_pwd=(TextView)  findView("R.id.ed_title_rgst_pwd");
        qg_line_rgst_account = (Button) findView("R.id.qg_line_rgst_account");
        qg_line_rgst_pwd = (Button) findView("R.id.qg_line_rgst_pwd");

        userAgreementView.setOnClickListener(listener);
        privateAgreementView.setOnClickListener(listener);
        mSwitchTextType.setOnClickListener(listener);
        registerView.setOnClickListener(listener);
        usernameView
                .addTextChangedListener(new QGEditText.TextChangedListener() {
                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        int len = s.length();

                        // 避免和电话号码冲突
                        if (len >= 4 && len <= 20 && !(len == 11 && isNumeric(s.toString()))) {
                            mUsernameCheckView
                                    .setImageResource(getResId("R.drawable.qg_check_ok"));

                        } else {
                            mUsernameCheckView
                                    .setImageResource(getResId("R.drawable.qg_error_deleate"));

                        }
                        ed_title_rgst_account.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (usernameView.getText().length() >=4) {
                            qg_line_rgst_account.setEnabled(true);
                        } else {
                            qg_line_rgst_account.setEnabled(false);
                        }

                        changeButtonColor();
                    }
                });

        usernameView.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange( boolean hasFocus) {
                if (!hasFocus){
                    ed_title_rgst_account.setVisibility(View.GONE);
                    qg_line_rgst_account.setEnabled(false);
                }else {
                    if (usernameView.getText().length()>0){
                        ed_title_rgst_account.setVisibility(View.VISIBLE);
                        qg_line_rgst_account.setEnabled(true);
                    }
                }

            }
        });
        passwordView.addTextChangedListener(new QGEditText.TextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_title_rgst_pwd.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordView.getText().length() > 0) {
                    qg_line_rgst_pwd.setEnabled(true);
                } else {
                    qg_line_rgst_pwd.setEnabled(false);
                }
                changeButtonColor();
            }
        });
        passwordView.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange( boolean hasFocus) {
                if (!hasFocus){
                    ed_title_rgst_pwd.setVisibility(View.GONE);
                    qg_line_rgst_pwd.setEnabled(false);
                }else {
                    if (passwordView.getText().length()>0){
                        ed_title_rgst_pwd.setVisibility(View.VISIBLE);
                        qg_line_rgst_pwd.setEnabled(true);
                    }
                }

            }
        });

        InitData initData = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
        int realNameNode = 1;
        if (initData != null) {
            realNameNode = initData.getRealNameNode();
        }
        if (realNameNode == Constant.CRETIFICATION_DISPLAY_ON_LOGIN) {
            registerView.setText(getResId("R.string.qg_register_nextstep"));
        }
        mTitleBar.hideCloseIcon();

    }

    public void changeButtonColor(){
        if (passwordView.getText().length()>0&&usernameView.getText().length()>0){
            registerView.setEnabled(true);
        }
    }
    @Override
    public void onClicks(int id) {
        if (id == userAgreementView.getId()) {
		/*	BaseFragment  fragment = new QGWebViewFragment();
			fragment.setTag(this);
			QGFragmentManager.getInstance(mActivity).add(fragment);*/
            Intent intent = new Intent();
            intent.putExtra("from", "REGIST");
            intent.setClass(mActivity, NoticeActivity.class);
            mActivity.startActivity(intent);

        }
        if (id == privateAgreementView.getId()) {
		/*	BaseFragment  fragment = new QGWebViewFragment();
			fragment.setTag(this);
			QGFragmentManager.getInstance(mActivity).add(fragment);*/
            Intent intent = new Intent();
            intent.putExtra("from", "REGIST_P");
            intent.setClass(mActivity, NoticeActivity.class);
            mActivity.startActivity(intent);

        }

        if (id == mSwitchTextType.getId())
            changeEyeState(mSwitchTextType, passwordView);
        if (id == registerView.getId())
            register();
    }

    private void register() {

        username = usernameView.getText().toString().trim();
        password = passwordView.getText().toString().trim();
        if(username.length()<4){
            Toast.makeText(mActivity,getString("R.string.qg_account_regist_lengthshort"),Toast.LENGTH_LONG).show();
            return;
        }
        if (password.length() < 6 || password.length() > 20) {
            showToast("R.string.toast_text_register_notice_length_error");
            return;
        }
        if (username.length() == 11 && isNumeric(username)) {
            showToast("R.string.toast_text_register_account_not_number");
            return;
        }

        String param = new QGParameter(mActivity)
                .addParameter("username", username)
                .addParameter("password", QGSdkUtils.getMD5Str(password))
                .create();
        HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
            @Override
            public void onSuccess(QGUserInfo bean) {
                try {
                    // 保存账号密码
                    saveAccountInfo(username, password);
                    if (isNeedActive(bean)) {
                        BaseFragment fragment = new ActiveFragment();
                        fragment.setTag(bean.getAuthtoken());
                        QGFragmentManager.getInstance(mActivity).add(fragment);
                    } else {
                        switchToCerificationFragment(CerificationNode.ON_LOGIN);
                    }
                    LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_ACCOUNT;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(int id,String message) {
                showToast(message);
            }
        }.post().setUrl(Constant.HOST + Constant.REGISTER).addParameter(param);
        DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);

    }

}
