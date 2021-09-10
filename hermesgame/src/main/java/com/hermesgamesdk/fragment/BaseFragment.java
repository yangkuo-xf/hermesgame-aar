package com.hermesgamesdk.fragment;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.fragment.login.SwitchAccountFragment;
import com.hermesgamesdk.fragment.usercenter.BindPhoneFailedFragment;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.AlertDialog;
import com.hermesgamesdk.view.QGEditText;
import com.hermesgamesdk.view.QGTitleBar;

import java.util.List;

public abstract class BaseFragment extends com.hermesgamesdk.skin.manager.base.BaseFragment {
    // Fragment所在activity对象
    protected static FragmentActivity mActivity;

    // Fragment根布局对象
    protected View mRoot;
    protected LayoutInflater mInflater;
    // 标题栏
    protected QGTitleBar mTitleBar;
    // 默认支持返回
    protected boolean mIsSupportBack = true;
    protected Object mTag;


    /**
     * @return 是否支持back事务 ，true支持 false不支持
     */
    public void setTag(Object tag) {
        this.mTag = tag;

    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter){
            return AnimationUtils.loadAnimation(mActivity,QGSdkUtils.getResId(mActivity,"R.anim.fragment_fade_in"));
        }else {
            return AnimationUtils.loadAnimation(mActivity,QGSdkUtils.getResId(mActivity,"R.anim.fragment_fade_out"));
        }
    }



    public void setMyActivity(FragmentActivity activity){
        mActivity=activity;
    }

    /**
     * @return 是否支持back事务 ，true支持 false不支持
     */
    public boolean isSupportBack() {
        return mIsSupportBack;
    }

    /**
     * @return Fragment的布局名称
     */
    protected abstract String getRootName();

    /**
     * @return 返回标题
     */
    protected abstract String getTitle();

    /**
     * @param root Fragment 根布局对象 onCreateView中被调用
     */
    protected abstract void onRootViewInflated(View root);

    /**
     * 在调QGFragmentManager的back中被调用
     */
    public void onBackInvoke() {

    }

    @Override
    @Deprecated
    public void onAttach(Activity activity) {
    
    	 mActivity =(FragmentActivity) activity;
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
    }
    @Override
    public void onAttach(Context context) {
    	Log.d("hermesgame","use normal method onAttach");
    	mActivity =(FragmentActivity) context;
        super.onAttach(context);
      
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        if (isAdded()){
            mRoot = mInflater.inflate(getResId(getRootName()), container, false);
        }
        mTitleBar = (QGTitleBar) mRoot.findViewById(getResId("R.id.qg_title_bar"));
        onRootViewInflated(mRoot);
        //mTitleBar.hideCloseIcon();
        return mRoot;
    }

    /**
     * 设置标题名称
     */
    public void updateTitle() {
        if (mTitleBar != null)
            mTitleBar.setTitle(getString(getTitle()));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTitle();
    }

    public static int getResId(String parameter) {
        String[] parameters = parameter.split("\\.");
        String resType = parameters[1];
        String resName = parameters[2];

        return mActivity.getResources().getIdentifier(resName, resType, mActivity.getPackageName());
    }

    public View findView(String id) {
        return mRoot.findViewById(getResId(id));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 回收Fragment对象
        QGFragmentManager.getInstance(mActivity).onFragmentDestroy(this);
    }

    /**
     * 返回上一个fragment
     */
    public void forceBack() {
        forceBack(null);
    }

    public void forceBack(Class<?> clazz) {
        mIsSupportBack = true;
        back(clazz);
    }

    public void back() {
        back(null);
    }

    public void back(Class<?> clazz) {
        QGFragmentManager.getInstance(mActivity).back(clazz);
    }

    /**
     * @return 从文件中读取json格式存储的账号信息
     */
    protected static JSONArray readAccountInfoFromFile() {
        String accountInfo = QGSdkUtils.getString(mActivity, Constant.SP_ACCOUNT_INFO);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(accountInfo);
        } catch (JSONException e) {
            return new JSONArray();
        }
        return jsonArray;
    }

    /**
     * @param account
     * @param password为nul时表示删除account的账号信息 向文件中存储json格式的账号信息 password 长度大于30表示存入的为token
     */
    protected static void saveAccountInfo(String account, String password) {

        try {

            JSONArray jsonArray = readAccountInfoFromFile();
            JSONArray noRepeat = new JSONArray();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String accountFromFile = item.getString(Constant.SP_ACCOUNT);
                if (!account.equals(accountFromFile)) {
                    noRepeat.put(item);
                } else {
                    String prePasswd = item.getString(Constant.SP_PASSWORD);
                    if (prePasswd != null && "".equals(password)) {
                        password = QGSdkUtils.decryptAES(prePasswd, Constant.signkey);
                    }
                }
            }
            // 删除account的账号信息
            if (password == null) {
                QGSdkUtils.saveString(mActivity, Constant.SP_ACCOUNT_INFO, noRepeat.toString());
                return;
            }
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(Constant.SP_ACCOUNT, account);
            jsonObj.put(Constant.SP_PASSWORD, QGSdkUtils.encryptAES(password, Constant.signkey));

            if (noRepeat.length() >= 5) {
                noRepeat.put(0, jsonArray.get(1));
                noRepeat.put(1, jsonArray.get(2));
                noRepeat.put(2, jsonArray.get(3));
                noRepeat.put(3, jsonArray.get(4));
                noRepeat.put(4, jsonObj);
            } else {
                noRepeat.put(jsonObj);
            }
            QGSdkUtils.saveString(mActivity, Constant.SP_ACCOUNT_INFO, noRepeat.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static Toast toast;

    public void showToast(String message) {
        if (message.startsWith("R.string")) {
            message = getString(message);
        }
        if (toast == null) {
            toast = Toast.makeText(mActivity.getApplicationContext(), message, Toast.LENGTH_LONG);
        } else {
            toast.setText(message);
        }
        toast.show();
    }
    public void showLimitedDialog(final BaseFragment fragment, String message){

        String leftName="进行实名";
        int flag = ((QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY)).getCheckrealname();
        if(flag==Constant.CRETIFICATION_HAS_CERTIFIED){
            leftName="";
        }

        final AlertDialog alertDialog=new AlertDialog(mActivity, fragment, "防沉迷提示", message, leftName, "切换帐号") {
            @Override
            public void onDismiss() {

            }
        };
        alertDialog.setClickListener(new AlertDialog.onClick() {
            @Override
            public void onLeftClick() {
                alertDialog.dismiss();
                fragment.switchToCerificationFragment(CerificationNode.ON_LIMITED);
            }

            @Override
            public void onRightClick() {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }
    

    /**
     * 当返回从后台转到前台的时候被调用
     */
    public void onBackForeground() {

    }

    protected View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            onClicks(id);
        }
    };

    /**
     * @param id 控件id 当控件的监听设置为listener时，发生点击事件调用此方法 由子类覆写，处理相关逻辑
     */
    public void onClicks(int id) {

    }

    private Button mGetIdentfyingCodeButton = null;
    myCountDownTimer timer;

    /**
     * 请求验证码，包括按钮的UI更新
     */
    public void requestIdentfyingCode(final QGEditText editText, Button GetIdentfyingCodeButton, String phoneNumber, int sendType,int setPass) {
        mGetIdentfyingCodeButton = GetIdentfyingCodeButton;
        mGetIdentfyingCodeButton.setClickable(false);
        mGetIdentfyingCodeButton.setEnabled(false);
        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("R.string.toast_text_input_phonenumb");
            mGetIdentfyingCodeButton.setClickable(true);
            return;
        }
        HttpRequest<String> request = new HttpRequest<String>() {
            @SuppressWarnings("deprecation")
            @Override
            public void onSuccess(String message) {
			/*	mGetIdentfyingCodeButton
						.setBackgroundResource(getResId("R.drawable.qg_btn_get_identfying_code_gray_background"));
				mGetIdentfyingCodeButton
						.setTextColor(mActivity.getResources().getColor(getResId("R.color.qgColorGray")));*/
                mGetIdentfyingCodeButton
                        .setText(getString("R.string.string_notice_reget_verificationcode_60seconds_latter"));
                timer = new myCountDownTimer(60 * 1000, 1 * 1000);
                timer.start();

            }

            @Override
            public void onFailed(int id,String message) {

                if ("该手机已被绑定".equals(message)) {
                  //  showToast("R.string.qg_phonebind_failure");
                    editText.setText("");
                QGFragmentManager.getInstance(mActivity).add(new BindPhoneFailedFragment());

                } else {
                    showToast(message);
                }

                mGetIdentfyingCodeButton.setClickable(true);
                mGetIdentfyingCodeButton.setEnabled(true);

            }
        };
        request.addParameter(new QGParameter(mActivity).addParameter("phone", phoneNumber)
                .addParameter("sendType", sendType + "").addParameter("setPass",""+setPass).create()).post()
                .setUrl(Constant.HOST + Constant.GET_IDENTFING_CODE);

        DataManager.getInstance().requestHttp(request);

    }

    public class myCountDownTimer extends CountDownTimer {

        public myCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            try {
                mGetIdentfyingCodeButton.setText(millisUntilFinished / 1000
                        + getString("R.string.string_notice_reget_verificationcode_xseconds_latter"));
            } catch (Exception e) {
                // e.printStackTrace();
                cancel();
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onFinish() {
            try {
                mGetIdentfyingCodeButton.setClickable(true);
                mGetIdentfyingCodeButton.setEnabled(true);
			/*	Resources resource = mActivity.getResources();
				@SuppressWarnings("deprecation")
				ColorStateList csl = resource
						.getColorStateList(getResId("R.drawable.qg_get_identifying_code_text_color_selector"));
				mGetIdentfyingCodeButton.setTextColor(csl);
				mGetIdentfyingCodeButton
						.setBackgroundResource(getResId("R.drawable.qg_btn_get_identifying_code_selector"));*/
                mGetIdentfyingCodeButton.setText(getString("R.string.bassfragment_get_verificationcode"));

            } catch (Exception e) {
                // e.printStackTrace();
                cancel();
            }

        }
    }

    /**
     * 登录
     * 支付
     * 悬浮窗
     * 防沉迷
     */
    public enum CerificationNode {
        ON_LOGIN, ON_PAY, ON_FLAOTVIEW,ON_LIMITED
    }

    /**
     * 跳转到实名认证界面，登录成功后就调用此方法。如果不需要显示实名认证界面，调此方法会自动关闭当前Activity。
     */
    public static void switchToCerificationFragment(CerificationNode node) {
    	Log.d("hermesgame", "BaseFragment switchToCerificationFragment");
        List<QGUserInfo.BindUsers> bindUsers=((QGUserInfo)DataManager.getInstance().getData(Constant.USERINFO_KEY)).getUserdata().getBindUsers();
        InitData initData = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
        int realNameNode = 1;
        if (initData != null) {
            realNameNode = initData.getRealNameNode();
        }
        if (node == CerificationNode.ON_LOGIN && realNameNode != Constant.CRETIFICATION_DISPLAY_ON_LOGIN) {
         if (bindUsers!=null&&bindUsers.size()!=0){
                QGFragmentManager.getInstance(mActivity).add(new SwitchAccountFragment());
            }else{
                mActivity.finish();
            }
           //QGFragmentManager.getInstance(mActivity).add(new SwitchAccountFragment());
            return;
        }
        if (node == CerificationNode.ON_PAY && realNameNode != Constant.CRETIFICATION_DISPLAY_ON_PAY)
            return;
        if(node==CerificationNode.ON_LIMITED)
        	LoginManager.getInstance().isFromLimited=true;
        if (DataManager.getInstance().getData(Constant.USERINFO_KEY) != null) {
            int flag = ((QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY)).getCheckrealname();
            Log.d("hermesgame", "realNamestatus=" + flag);
            switch (flag) {
                case Constant.CRETIFICATION_HAS_CERTIFIED:
                    if (node == CerificationNode.ON_FLAOTVIEW)
                        //	showToast("R.string.toast_text_array_authentication");
                        Toast.makeText(mActivity, "已认证", Toast.LENGTH_SHORT).show();
                case Constant.CRETIFICATION_NOT_NEED:
                    if (node == CerificationNode.ON_LOGIN){
                       if (bindUsers!=null&&bindUsers.size()!=0){
                            QGFragmentManager.getInstance(mActivity).add(new SwitchAccountFragment());
                        }else{
                            mActivity.finish();
                        }
                     //  QGFragmentManager.getInstance(mActivity).add(new SwitchAccountFragment());
                    }
                    break;
                default:
                    QGUserInfo user=(QGUserInfo)DataManager.getInstance().getData(Constant.USERINFO_KEY);
                        //判断游客放过
                    if (user.getUserdata().getIsGuest()==1&&user.getGuestRealName()==0&&node!=CerificationNode.ON_LIMITED){
                       if(node == CerificationNode.ON_PAY){

                       }else{
                           if (bindUsers!=null&&bindUsers.size()!=0){
                               QGFragmentManager.getInstance(mActivity).add(new SwitchAccountFragment());
                           }else{
                               mActivity.finish();
                           }
                         // QGFragmentManager.getInstance(mActivity).add(new SwitchAccountFragment());
                       }
                       // LoginManager.getInstance().startCountTime();
                    }else {
                        QGFragmentManager.getInstance(mActivity).add(new CertificationFragment());
                    }
                    break;
            }
        }else{
        	  Log.e("hermesgame", "USERINFO_KEY = null" );
        }
    }

    /**
     * 眼睛的点击事件，用于明文或密文显示密码
     */

    protected boolean mPasswdState = false;

    // TODO 如果根据 ImageView 的 内容来判断呢？
    public void changeEyeState(ImageView mEye, QGEditText mPasswordEdt) {
        mPasswdState = !mPasswdState;
        if (mPasswdState) {
            mEye.setImageResource(getResId("R.drawable.qg_eye_open"));
            mPasswordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            mEye.setImageResource(getResId("R.drawable.qg_eye_close"));
            mPasswordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public boolean isNumeric(String str) {
        try {
            for (int i = 0; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public static boolean isNeedActive(QGUserInfo info) {
        if (info != null && info.getUserdata().getNeedActive() == 1) {
            return true;
        }
        return false;

    }

    protected String getString(String str) {
        if (str != null && str.startsWith("R.string")) {
            return getString(getResId(str));
        }
        return str;
    }

}
