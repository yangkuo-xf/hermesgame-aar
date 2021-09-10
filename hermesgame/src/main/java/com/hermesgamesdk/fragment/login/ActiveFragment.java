package com.hermesgamesdk.fragment.login;


import android.text.Editable;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.QGUserInfo;

import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.view.QGEditText;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActiveFragment extends BaseFragment {

    private TextView mTips;
    private Button mActive;
    private QGEditText mEditActive;
    private String mTipsTxt;
    private String mUid;
    private Button qg_line_active_code;
    private TextView ed_title_active_code;


    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_active_code";
    }

    @Override
    protected String getTitle() {
        return "R.string.qg_active_title";
    }

    @Override
    protected void onRootViewInflated(View root) {
        initView();
    }

    private void initView() {
        mTips = (TextView) findView("R.id.active_tips");
        qg_line_active_code = (Button) findView("R.id.qg_line_active_code");
        ed_title_active_code = (TextView) findView("R.id.ed_title_active_code");
        mActive = (Button) findView("R.id.qg_active");
        mEditActive = (QGEditText) findView("R.id.qg_ed_active");
        mTips.setOnClickListener(listener);
        mActive.setOnClickListener(listener);
        QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
        if (info != null) {
            mTipsTxt = info.getUserdata().getActiveTips();
            mUid = info.getUserdata().getUid();
        }

        mEditActive.addTextChangedListener(new QGEditText.TextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_title_active_code.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEditActive.getText().length() > 0) {
                    qg_line_active_code.setEnabled(true);
                } else {
                    qg_line_active_code.setEnabled(false);
                }
                changeButtonColor();
            }
        });
        mEditActive.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange( boolean hasFocus) {
                
            }
        });
        mTitleBar.hideBackIcon();
        DataManager.getInstance().remove(Constant.USERINFO_KEY);
    }
    private void changeButtonColor() {
        if (mEditActive.getText().length()>0){
            mActive.setEnabled(true);

        }else{
            mActive.setEnabled(false);
        }
    }

    @Override
    public void onClicks(int id) {
        if (id == mActive.getId()) {
            active();
        }
        if (id == mTips.getId()) {
            showToast(mTipsTxt);
        }
    }

    private void active() {
        String activeCode = mEditActive.getText();
        String param = new QGParameter(mActivity).addParameter("uid", mUid).addParameter("activeCode", activeCode).create();
        HttpRequest<String> request = new HttpRequest<String>() {

            @Override
            public void onSuccess(String bean) {
                /*
                 * HttpRequest<?> loginRequest = (HttpRequest<?>) mTag;
                 * loginRequest.setUrl(Constant.HOST+Constant.ACCOUNT_LOGIN);
                 * DataManager.getInstance().requestHttp(loginRequest,
                 * Constant.USERINFO_KEY);
                 */
                String tokenString = (String) mTag;
                autoLoginByToken(tokenString);
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(int id,String message) {
                showToast(message);

            }
        }.post().setUrl(Constant.HOST + Constant.ACTIVE_PLAYER).addParameter(param);

        DataManager.getInstance().requestHttp(request);

    }

    private void autoLoginByToken(String authToken) {

        HttpRequest<QGUserInfo> request = new HttpRequest<QGUserInfo>() {
            @Override
            public void onSuccess(QGUserInfo bean) {
                try {
                    LoginManager.getInstance().loginType=Constant.LOGIN_TYPE_AUTO;
                    //saveAccountInfo(bean.getUserdata().getUsername(), bean.getAuthtoken());
                    switchToCerificationFragment(CerificationNode.ON_LOGIN);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int id,String message) {

                showToast(message);
                mIsSupportBack = true;
                forceBack();

            }
        }.addParameter(new QGParameter(mActivity).addParameter("authToken", authToken).create()).post().setUrl(Constant.HOST + Constant.AUTO_TOKEN);

        DataManager.getInstance().requestHttp(request, Constant.USERINFO_KEY);

    }

}
