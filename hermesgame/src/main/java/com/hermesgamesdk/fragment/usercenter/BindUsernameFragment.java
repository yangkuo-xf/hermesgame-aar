package com.hermesgamesdk.fragment.usercenter;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.hermesgamesdk.activity.TempActivty;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.SliderBarV2Manager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.QGEditText;
import com.hermesgamesdk.view.QGEditText.TextChangedListener;

public class BindUsernameFragment extends BaseFragment {
    private QGEditText mBindUsername;
    private QGEditText mBindPassword;
   // private QGEditText mInputAgain;
    private Button mSubmit;
    private ImageView mShowPassword;
    private ImageView mShowPassword1;
    private boolean mCanSubmit = false;
    private ImageView mUsernameCheck;

    private Button qg_line_setpwd;
    private Button qg_line_setaccount;
    private TextView ed_title_setaccount;
    private TextView ed_title_setpwd;


    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_bind_username";
    }

    @Override
    protected String getTitle() {
        return "R.string.qg_setup_username";
    }

    @Override
    protected void onRootViewInflated(View root) {
        initView();
    }

    private void initView() {
        mBindUsername = (QGEditText) findView("R.id.qg_bind_username");
        mBindPassword = (QGEditText) findView("R.id.qg_bind_password");
    //    mInputAgain = (QGEditText) findView("R.id.qg_input_agin");
        mSubmit = (Button) findView("R.id.qg_setup_register");
        mShowPassword = (ImageView) findView("R.id.qg_switch_passwd");
        mShowPassword1 = (ImageView) findView("R.id.qg_switch_passwd1");
        mUsernameCheck = (ImageView) findView("R.id.qg_setup_username_check");
        ed_title_setaccount= (TextView)findView("R.id.ed_title_setaccount") ;
        ed_title_setpwd= (TextView)findView("R.id.ed_title_setpwd") ;
        qg_line_setpwd= (Button) findView("R.id.qg_line_setpwd") ;
        qg_line_setaccount= (Button)findView("R.id.qg_line_setaccount") ;
        mShowPassword.setOnClickListener(listener);
        mShowPassword1.setOnClickListener(listener);
        mSubmit.setOnClickListener(listener);
        mBindUsername.addTextChangedListener(new TextChangedListener() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int len = s.length();

                // 避免和电话号码冲突
                if (len==11 && isNumeric(s.toString())) {

                    mCanSubmit = false;
                } else {

                    mCanSubmit = true;
                }
                ed_title_setaccount.setVisibility(s.length()>0?View.VISIBLE:View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mBindUsername.getText().length()>0){
                    qg_line_setaccount.setEnabled(true);
                }else {
                    qg_line_setaccount.setEnabled(false);
                }
                changeButtonState();
            }
        });


        mBindUsername.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange( boolean hasFocus) {
                if (!hasFocus){
                    ed_title_setaccount.setVisibility(View.GONE);
                    qg_line_setaccount.setEnabled(false);
                }else {
                    if (mBindPassword.getText().length()>0){
                        ed_title_setaccount.setVisibility(View.VISIBLE);
                        qg_line_setaccount.setEnabled(true);
                    }
                }

            }
        });

        mBindPassword.addTextChangedListener(new TextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_title_setpwd.setVisibility(s.length()>0?View.VISIBLE:View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mBindPassword.getText().length()>0){
                    qg_line_setpwd.setEnabled(true);
                }else {
                    qg_line_setpwd.setEnabled(false);
                }
                changeButtonState();
            }
        });

        mBindPassword.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange( boolean hasFocus) {
                if (!hasFocus){
                    ed_title_setpwd.setVisibility(View.GONE);
                    qg_line_setpwd.setEnabled(false);
                }else {
                    if (mBindPassword.getText().length()>0){
                        ed_title_setpwd.setVisibility(View.VISIBLE);
                        qg_line_setpwd.setEnabled(true);
                    }
                }

            }
        });

    }
    public void changeButtonState(){
        if (mBindPassword.getText().length()>0&&mBindUsername.getText().length()>0){
            mSubmit.setEnabled(true);
        }else {
            mSubmit.setEnabled(false);
        }
    }

    @Override
    public void onClicks(int id) {
        if (id == mShowPassword.getId()) {
            changeEyeState(mShowPassword, mBindPassword);
        }
      /*  if (id == mShowPassword1.getId()) {
            changeEyeState(mShowPassword1, mInputAgain);
        }*/
        if (id == mSubmit.getId()) {
            submit();
        }
    }

    private void submit() {

        if (!mCanSubmit){
            Toast.makeText(mActivity,"用户名不能是11位纯数字，请重新输入！",Toast.LENGTH_LONG).show();
            return;
        }

        final String username = mBindUsername.getText().toString().trim();
        final String passwd = mBindPassword.getText().toString().trim();
       // String passwdAgain = mInputAgain.getText().toString().trim();
        if (TextUtils.isEmpty(passwd) ) {
            showToast("R.string.qg_setup_empty_prompt");
            return;
        }
        if (passwd.length() < 6 || passwd.length() > 16) {
            showToast("R.string.toast_text_register_notice_length_error");
            return;
        }
       /* if (!passwdAgain.equals(passwd)) {
            showToast("R.string.qg_setup_passwd_verify");
            return;
        }*/

        QGUserInfo userInfo = (QGUserInfo) DataManager.getInstance().getData(
                Constant.USERINFO_KEY);
        String uid = "";
        if (userInfo != null) {
            uid = userInfo.getUserdata().getUid();
        }
        HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {

            @Override
            public void onSuccess(QGUserInfo bean) {
                saveAccountInfo(username, passwd);
                showToast("R.string.qg_setup_bind_success");


                if (getActivity() instanceof TempActivty) {
                    getActivity().setResult(1);
                    getActivity().finish();
                } else {
                    switchToCerificationFragment(CerificationNode.ON_LOGIN);
                }
                SliderBarV2Manager.getInstance(mActivity).retry();
            }

            @Override
            public void onFailed(int id,String message) {
                showToast(message);
            }
        };
        String parameters = new QGParameter(mActivity).addParameter("uid", uid)
                .addParameter("username", username)
                .addParameter("password", QGSdkUtils.getMD5Str(passwd))
                .create();
        request.addParameter(parameters).post().setUrl(Constant.HOST + Constant.BIND_USERNAME);
        DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);
    }
}
