package com.hermesgamesdk.fragment.usercenter;

import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;

import com.hermesgamesdk.activity.TempActivty;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.LoginManager;

import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.QGEditText;

/**
 * 修改密码
 */

public class ModifyPasswordFragment extends BaseFragment {


    private QGEditText passworOld, passwordNew, passwordVer;
    private Button btn_yes;
    private String oldPwd;
    private String newPwd;
    private ImageView modify_eye1, modify_eye2;

    private Button qg_line_confirm ,qg_line_oldpwd ,qg_line_newpwd;
    private TextView ed_title_oldpwd,ed_title_newpwd,ed_title_confirm;

    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_modify_password";
    }

    @Override
    protected String getTitle() {
        return "R.string.qg_modifypsw_reset_password";
    }

    @Override
    protected void onRootViewInflated(View root) {

        initView(root);
    }

    private void initView(View root) {
        passworOld = (QGEditText) findView("R.id.qg_password_old");
        passwordNew = (QGEditText) findView("R.id.qg_passward_new");
        passwordVer = (QGEditText) findView("R.id.qg_password_ver");
        btn_yes = (Button) findView("R.id.qg_btn_yes");
        modify_eye1 = (ImageView) findView("R.id.qg_img_modify_eye1");
        modify_eye2 = (ImageView) findView("R.id.qg_img_modify_eye2");

        qg_line_confirm=(Button)findView("R.id.qg_line_confirm");
        qg_line_oldpwd=(Button)findView("R.id.qg_line_oldpwd");
        qg_line_newpwd=(Button)findView("R.id.qg_line_newpwd");

        ed_title_oldpwd=(TextView)findView("R.id.ed_title_oldpwd");
        ed_title_newpwd=(TextView)findView("R.id.ed_title_newpwd");
        ed_title_confirm=(TextView)findView("R.id.ed_title_confirm");

        btn_yes.setOnClickListener(listener);
        modify_eye1.setOnClickListener(listener);
        modify_eye2.setOnClickListener(listener);
        passworOld.addTextChangedListener(new QGEditText.TextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_title_oldpwd.setVisibility(s.length()>0?View.VISIBLE:View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passworOld.getText().length()>0){
                    qg_line_oldpwd.setEnabled(true);
                }else{
                    qg_line_oldpwd.setEnabled(false);
                }
                changeButtonColor();
            }
        });
        passworOld.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange( boolean hasFocus) {
                if (!hasFocus){
                    ed_title_oldpwd.setVisibility(View.GONE);
                    qg_line_oldpwd.setEnabled(false);
                }else {
                    if (passworOld.getText().length()>0){
                        ed_title_oldpwd.setVisibility(View.VISIBLE);
                        qg_line_oldpwd.setEnabled(true);
                    }
                }

            }
        });
        passwordNew.addTextChangedListener(new QGEditText.TextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_title_newpwd.setVisibility(s.length()>0?View.VISIBLE:View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordNew.getText().length()>0){
                    qg_line_newpwd.setEnabled(true);
                }else{
                    qg_line_newpwd.setEnabled(false);
                }
                changeButtonColor();
            }
        });
        passwordNew.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange( boolean hasFocus) {
                if (!hasFocus){
                    ed_title_newpwd.setVisibility(View.GONE);
                    qg_line_newpwd.setEnabled(false);
                }else {
                    if (passwordNew.getText().length()>0){
                        ed_title_newpwd.setVisibility(View.VISIBLE);
                        qg_line_newpwd.setEnabled(true);
                    }
                }

            }
        });
        passwordVer.addTextChangedListener(new QGEditText.TextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_title_confirm.setVisibility(s.length()>0?View.VISIBLE:View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordVer.getText().length()>0){
                    qg_line_confirm.setEnabled(true);
                }else{
                    qg_line_confirm.setEnabled(false);
                }
                changeButtonColor();
            }
        });
        passwordVer.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange( boolean hasFocus) {
                if (!hasFocus){
                    ed_title_confirm.setVisibility(View.GONE);
                    qg_line_confirm.setEnabled(false);
                }else {
                    if (passwordVer.getText().length()>0){
                        ed_title_confirm.setVisibility(View.VISIBLE);
                        qg_line_confirm.setEnabled(true);
                    }
                }

            }
        });
        mTitleBar.hideCloseIcon();

    }
    public void changeButtonColor(){
        if (passworOld.getText().length()>0&&passwordNew.getText().length()>0&&passwordVer.getText().length()>0){
            btn_yes.setEnabled(true);

        }else{
            btn_yes.setEnabled(false);
        }

    }

    @Override
    public void onClicks(int id) {
        if (id == btn_yes.getId()) {
            if (checkText())
                updatePassword(oldPwd, newPwd);
        }
        if (id == modify_eye1.getId()) {
            changeEyeState(modify_eye1, passwordNew);
        }
        if (id == modify_eye2.getId()) {
            changeEyeState(modify_eye2, passwordVer);
        }
    }

    private boolean checkText() {
        oldPwd = passworOld.getText().toString();
        newPwd = passwordNew.getText().toString();
        String yesPwd = passwordVer.getText().toString();
        if (!newPwd.equals(yesPwd)) {
            showToast("R.string.toast_text_diffrent_input");
            return false;
        }
        if (newPwd.length() < 6 || newPwd.length() > 16) {
            showToast("R.string.toast_text_register_notice_length_error");
            return false;
        }
        return true;
    }

    public void updatePassword(final String oldPassword, final String newPassword) {
        QGUserInfo payuser = (QGUserInfo) DataManager.getInstance().getData("userInfo");
        String param = new QGParameter(mActivity).addParameter("uid", payuser.getUserdata().getUid())
                .addParameter("oldPassword", QGSdkUtils.getMD5Str(oldPassword)).addParameter("newPassword", QGSdkUtils.getMD5Str(newPassword))
                .create();
        HttpRequest<String> modify = new HttpRequest<String>() {
            @Override
            public void onSuccess(String bean) {
                try {
                    showToast("R.string.toast_text_reset_success");
                    QGUserInfo userInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
                    saveAccountInfo(userInfo.getUserdata().getUsername(), newPassword);
                    if (mActivity instanceof TempActivty) {

                        getActivity().setResult(1);
                        mActivity.finish();
                    } else {
                        mActivity.finish();
                    }
                    LoginManager.getInstance().logout(mActivity);


                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(int id,String message) {
                showToast(message);
            }
        }.addParameter(param).post().setUrl(Constant.HOST + Constant.MODIFY_PASSWORD);
        DataManager.getInstance().requestHttp(modify);
    }

}
