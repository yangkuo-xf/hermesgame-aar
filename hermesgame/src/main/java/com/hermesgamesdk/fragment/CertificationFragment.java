package com.hermesgamesdk.fragment;

import org.json.JSONObject;

import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import android.widget.TextView;

import com.hermesgamesdk.activity.LoginActivity;
import com.hermesgamesdk.activity.NoticeActivity;
import com.hermesgamesdk.activity.TempActivty;
import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.fragment.login.SwitchAccountFragment;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.manager.SliderBarV2Manager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.WriteTimeUtils;
import com.hermesgamesdk.view.QGEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/9/12.
 */

public class CertificationFragment extends BaseFragment {
    private QGEditText mRealNameView;
    private QGEditText mIdentifyingCode;
    //private QGEditText mPhineNumber;
    private Button mSubmit;
    private QGUserInfo userInfo;
    private TextView ed_title_realname;
    private TextView qg_certification_text;

    private TextView ed_title_idcode;
    private Button qg_line_idcode;
    private Button qg_line_realname;

    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_certification";
    }

    @Override
    protected String getTitle() {
        return "R.string.string_notice_name_authentication";
    }

    @Override
    protected void onRootViewInflated(View root) {
        initView(root);
    }

    private void initView(View root) {
        mRealNameView = (QGEditText) findView("R.id.qg_ed_real_name");
        mIdentifyingCode = (QGEditText) findView("R.id.qg_ed_identifying_code");
        ed_title_realname=(TextView) findView("R.id.ed_title_realname");
        ed_title_idcode=(TextView) findView("R.id.ed_title_idcode");
        qg_line_idcode=(Button) findView("R.id.qg_line_idcode");
        qg_line_realname=(Button) findView("R.id.qg_line_realname");
        qg_certification_text=(TextView) findView("R.id.qg_certification_text");
        //	mPhineNumber = (QGEditText) findView("R.id.qg_ed_phonenumber_code");
        mSubmit = (Button) findView("R.id.qg_btn_submit");
        mSubmit.setOnClickListener(listener);
        mIdentifyingCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
        try{
            if(QGConfig.getmExtraConfig().get("registTitle")!=null){
                qg_certification_text.setText(QGConfig.getmExtraConfig().get("registTitle").toString());
            }
        }catch(Exception e){
            Log.e("hermesgame","qg_certification_text.setText erro:"+e.toString());
        }
        qg_certification_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("from", "CERT");
                intent.setClass(mActivity, NoticeActivity.class);
                mActivity.startActivity(intent);
            }
        });


        mRealNameView.addTextChangedListener(new QGEditText.TextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_title_realname.setVisibility(s.length()>0?View.VISIBLE:View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mRealNameView.getText().length()>0){
                    qg_line_realname.setEnabled(true);
                }else {
                    qg_line_realname.setEnabled(false);
                }
                changeButtonState();
            }
        });

        mRealNameView.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange( boolean hasFocus) {
                if (!hasFocus){
                    ed_title_realname.setVisibility(View.GONE);
                    qg_line_realname.setEnabled(false);
                }else {
                    if (mRealNameView.getText().length()>0){
                        ed_title_realname.setVisibility(View.VISIBLE);
                        qg_line_realname.setEnabled(true);
                    }
                }

            }
        });
        mIdentifyingCode.addTextChangedListener(new QGEditText.TextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_title_idcode.setVisibility(s.length()>0?View.VISIBLE:View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mIdentifyingCode.getText().length()>0){
                    qg_line_idcode.setEnabled(true);
                }else {
                    qg_line_idcode.setEnabled(false);
                }
                changeButtonState();
            }
        });

        mIdentifyingCode.addFocusChangeListener(new QGEditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange( boolean hasFocus) {
                if (!hasFocus){
                    ed_title_idcode.setVisibility(View.GONE);
                    qg_line_idcode.setEnabled(false);
                }else {
                    if (mIdentifyingCode.getText().length()>0){
                        ed_title_idcode.setVisibility(View.VISIBLE);
                        qg_line_idcode.setEnabled(true);
                    }
                }

            }
        });
        userInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);

        if (userInfo.getCheckrealname() == Constant.CRETIFICATION_CAN_NOT_CLOSE) {
            mTitleBar.hideCloseIcon();
            mIsSupportBack = false;
            mTitleBar.hideBackIcon();
            return;
        }else {
            mTitleBar.hideBackIcon();
        }
        if (LoginManager.getInstance().isFromLimited){
            mTitleBar.hideCloseIcon();
            mTitleBar.openBackIcon();
        }

	/*	if (getActivity().getIntent().getStringExtra("from")!=null){
			if (getActivity().getIntent().getStringExtra("from").equals("guest_cert")){
				mIsSupportBack = false;
				mTitleBar.hideCloseIcon();
				mTitleBar.hideBackIcon();
			}
		}*/
		/*	if (mActivity instanceof LoginActivity) {
			mIsSupportBack = false;
			//mTitleBar.hideBackIcon();
		} else if (mActivity instanceof PayActivity) {
			mTitleBar.hideCloseIcon();
		}
*/
    }
    public void changeButtonState(){
        if (mIdentifyingCode.getText().length()>0&&mRealNameView.getText().length()>0){
            mSubmit.setEnabled(true);
        }else {
            mSubmit.setEnabled(false);
        }
    }
    @Override
    public void onBackInvoke() {
        // TODO Auto-generated method stub
        super.onBackInvoke();
        LoginManager.getInstance().isFromLimited=false;
    }
    @Override
    public void onClicks(int id) {
        if (id == mSubmit.getId())
            identification();
    }

    private void identification() {
        if (userInfo == null) {
            return;
        }
        String realName = mRealNameView.getText().trim();
        final String peopleId = mIdentifyingCode.getText().trim();
        if ("".equals(realName) || "".equals(peopleId)) {
            showToast("R.string.toast_text_error_msg_cert");
            return;
        }
        String parameter = new QGParameter(mActivity).addParameter("realName", realName)
                .addParameter("peopleId", peopleId).addParameter("uid", userInfo.getUserdata().getUid()).create();
        HttpRequest<String> request = new HttpRequest<String>() {
            @Override
            public void onSuccess(String bean) {
                String age="0";
                if (userInfo != null) {
                    userInfo.setCheckrealname(Constant.CRETIFICATION_HAS_CERTIFIED);
                    try{
                        if (bean.equals("ExSuccess")){

                            userInfo.setuAge( getAge(peopleId));
                        }else{
                            JSONObject json=new JSONObject(bean);
                            if (json.has("age")){
                                age=json.optString("age");
                                userInfo.setuAge(Integer.valueOf(age));
                            }
                        }


                        if (LoginManager.getInstance().isFromLimited){
                            mActivity.finish();
                            LoginManager.getInstance().isFromLimited=false;
                        }
                        if (mActivity instanceof LoginActivity &&age!=null&&Integer.valueOf(age)<18&&userInfo.getCkPlayTime()==1){
                            SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
                            String dateStr = dateformat.format(System.currentTimeMillis());
                            // 获取小时
                            int hours = Integer.valueOf(dateStr.split(":")[0]);
                            if (hours>=22||hours<8){
                                WriteTimeUtils.getInstance().showDiaLog(mActivity,4);
                            }
                        }
                    }catch(Exception e){

                    }

                }
                if (mActivity instanceof LoginActivity) {
                    if(LoginManager.getInstance().isFromLimited){
                        forceBack();
                        LoginManager.getInstance().isFromLimited=false;
                    }else{
                        List<QGUserInfo.BindUsers> bindUsers=((QGUserInfo)DataManager.getInstance().getData(Constant.USERINFO_KEY)).getUserdata().getBindUsers();
                        if (bindUsers!=null&&bindUsers.size()!=0){
                            QGFragmentManager.getInstance(mActivity).add(new SwitchAccountFragment());
                        }else{
                            mActivity.finish();
                        }
                      //  QGFragmentManager.getInstance(mActivity).add(new SwitchAccountFragment());
                    }

                } else if (mActivity instanceof TempActivty) {
                    if (mActivity.getIntent().getStringExtra("from").equals("slider_cert")){
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SliderBarV2Manager.getInstance(mActivity).retry();
                            }
                        });

                    }else if (mActivity.getIntent().getStringExtra("from").equals("guest_cert")){
                        if (Integer.valueOf(age)>0&&Integer.valueOf(age)<18){
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    WriteTimeUtils.getInstance().showDiaLog(mActivity,3);
                                }
                            });
                        }
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SliderBarV2Manager.getInstance(mActivity).reloadWebView();
                            }
                        });
                    }
                    mActivity.setResult(1);
                    mActivity.finish();
                }else {
                    showToast("R.string.toast_text_authentication_success");
                    forceBack();
                }
            }

            @Override
            public void onFailed(int id,String message) {
                showToast(message);
            }
        }.addParameter(parameter).post().setUrl(Constant.HOST + Constant.IDENTIFICATION);

        DataManager.getInstance().requestHttp(request);
    }
    public int getAge(String idCard){
        int age=0;
        try{
            int year = Integer.valueOf(idCard.substring(6,10));
            int month= Integer.valueOf(idCard.substring(10,12));
            Calendar cal = Calendar.getInstance();
            int yearNow = cal.get(Calendar.YEAR);

            age=yearNow-year;
            if (month>=6){
                age=age+1;
            }
        }catch (Exception e){
            age=0;
        }
        return age;
    }
}
