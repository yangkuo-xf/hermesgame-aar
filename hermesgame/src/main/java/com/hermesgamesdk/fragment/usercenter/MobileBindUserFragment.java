package com.hermesgamesdk.fragment.usercenter;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.QGEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MobileBindUserFragment extends BaseFragment {

    private QGEditText mBindUsername;
    private QGEditText mBindPassword;
    private QGEditText mPhoneView;
    private Button mSubmit;
    private boolean mCanSubmit = false;
    private Button qg_line_setpwd;
    private Button qg_line_setaccount;
    private TextView ed_title_setaccount;
    private TextView ed_title_setpwd;
    QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_mobile_bind_user";
    }

    @Override
    protected String getTitle() {
        return "绑定账号";
    }

    @Override
    protected void onRootViewInflated(View root) {
        initView(root);
    }

    public void initView(View view){
        mPhoneView = (QGEditText) findView("R.id.qg_phone_user_bind_num");
        mBindUsername = (QGEditText) findView("R.id.qg_bind_user_bind_username");
        mBindPassword = (QGEditText) findView("R.id.qg_bind_user_bind_password");

        mSubmit = (Button) findView("R.id.qg_user_bind_confirm");

        ed_title_setaccount= (TextView)findView("R.id.ed_title_user_bind_setaccount") ;
        ed_title_setpwd= (TextView)findView("R.id.ed_title_user_bind_setpwd") ;
        qg_line_setpwd= (Button) findView("R.id.qg_line_user_bind_setpwd") ;
        qg_line_setaccount= (Button)findView("R.id.qg_line_user_bind_setaccount") ;
        mSubmit.setOnClickListener(listener);
        mTitleBar.hideCloseIcon();
        if(info.getUserdata().getMobile()!=null&&!info.getUserdata().getMobile().isEmpty()){
            mPhoneView.setText(info.getUserdata().getMobile());
            mPhoneView.getEt().setFocusableInTouchMode(false);
            mPhoneView.getEt().setKeyListener(null);
            mPhoneView.getEt().setFocusable(false);
            mPhoneView.getClose().setVisibility(View.GONE);
        }
        mPhoneView.addTextChangedListener(new QGEditText.TextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPhoneView
                .addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(boolean hasFocus) {

                    }
                });
        mBindUsername.addTextChangedListener(new QGEditText.TextChangedListener() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int len = s.length();

                // 避免和电话号码冲突
                if (len == 11 && !isNumeric(s.toString())) {

                    mCanSubmit = true;
                } else {

                    mCanSubmit = false;
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

        mBindPassword.addTextChangedListener(new QGEditText.TextChangedListener() {
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

       mSubmit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               requestBindUsername();
           }
       });

    }
    public void changeButtonState() {
        if (mBindPassword.getText().length() > 0 && mBindUsername.getText().length() > 0&&mPhoneView.getText().length()>0) {
            mSubmit.setEnabled(true);
        } else {
            mSubmit.setEnabled(false);
        }
    }

    public void requestBindUsername(){
        final String password=mBindPassword.getText().trim();
        final String account= mBindUsername.getText().trim();
        String parameter = new QGParameter(mActivity)
                .addParameter("uid", info.getUserdata().getUid())
                .addParameter("phone", info.getUserdata().getMobile())
                .addParameter("username",account)
                .addParameter("password", QGSdkUtils.getMD5Str(password)).create();
        HttpRequest<String> request = new HttpRequest<String>() {
            @Override
            public void onSuccess(String bean) {
                QGSdkUtils.saveString(mActivity,"tempUser",account);
                QGSdkUtils.saveString(mActivity,"tempPass",password);
                takeScreenShot(mRoot);
                QGFragmentManager.getInstance(mActivity).add(new PhoneUnbindFragment());
            }

            @Override
            public void onFailed(int id,String message) {
                showToast("绑定失败:"
                        + message);
            }
        }.addParameter(parameter).post()
                .setUrl(Constant.HOST + Constant.MOBILE_BIND_USER);

        DataManager.getInstance().requestHttp(request);
    }

    public void takeScreenShot(View v) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
        String fname = Environment.getExternalStorageDirectory() + File.separator + "DCIM/Camera";
        File f = new File(fname);
        if (!f.exists())
            f.mkdirs();
        fname = fname + File.separator + sdf.format(new Date()) + ".jpg";
        View root = v;

        root.setDrawingCacheEnabled(true);
        root.buildDrawingCache();
        Bitmap bitmap = root.getDrawingCache();
        if (bitmap != null) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(fname);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
             //   showToast("账号保存成功，新账号截图在您相册之中~");
            } catch (Exception e) {
            }
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
            }
        }
    }
}
