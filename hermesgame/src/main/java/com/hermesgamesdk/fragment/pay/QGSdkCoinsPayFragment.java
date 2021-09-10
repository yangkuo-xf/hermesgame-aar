package com.hermesgamesdk.fragment.pay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.hermesgamesdk.activity.QGPaySuccessActivity;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.QGManager;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.manager.QGPayManager;
import com.hermesgamesdk.manager.SliderBarV2Manager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.AlertDialog;
import com.hermesgamesdk.view.LoadingDialog;
import com.hermesgamesdk.view.PassWordEditText;

import java.util.Timer;
import java.util.TimerTask;

public class QGSdkCoinsPayFragment extends BaseFragment {
    private PassWordEditText passWordEditText;
    private TextView sdkCoinAmount,sdkCoinForget,sdkCoinBalance;
    private boolean isOrdering=false;
    double sliderAmount;
    String from;
    QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
    private static final int PAY_LIMIT=-2;
    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_sdkcoins_pay_layout";
    }

    @Override
    protected String getTitle() {
        return "R.string.qg_sdkcoin_pay";
    }

    @Override
    protected void onRootViewInflated(View root) {
         initView(root);
         mTitleBar.hideCloseIcon();
    }
    public void initView(View view){
        passWordEditText=(PassWordEditText) findView("R.id.qg_sdkcoins_edit");
        sdkCoinAmount=(TextView) findView("R.id.qg_sdkcoins_amount");
        sdkCoinForget=(TextView) findView("R.id.qg_sdkcoins_forgetPassword");
        sdkCoinBalance=(TextView) findView("R.id.qg_sdkcoins_balance");
        from=mActivity.getIntent().getStringExtra("from");
        if (from!=null&&from.equals("Slider")){
            sliderAmount=mActivity.getIntent().getDoubleExtra("amount",1.00);
            sdkCoinAmount.setText(""+sliderAmount);
        }else{
            sdkCoinAmount.setText(""+(Double.valueOf(QGPayManager.getInstance().getOrderInfo().getAmount())-Double.valueOf(QGPayManager.getInstance().getVoucherAmount())));
        }
        passWordEditText.setOnPasswordFullListener(new PassWordEditText.PasswordFullListener() {
            @Override
            public void passwordFull(String password) {
                if (from!=null&&from.equals("Slider")){
                    if(isOrdering){
                    }else{
                        isOrdering=true;//是否正在支付 防止二次回调这里
                        SliderBarV2Manager.getInstance(mActivity).completePayPassword(password);
                        mActivity.finish();
                    }
                }else{
                    if(isOrdering){

                    }else{
                        isOrdering=true;//是否正在支付 防止二次回调这里
                        orderSDKCoins(password);
                    }
                }


            }

            @Override
            public void passwordChanged(String password) {

            }
        });

        sdkCoinBalance.setText(	"余额("+info.getSdkCoinNum()+")");

        sdkCoinForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QGFragmentManager.getInstance(mActivity).add(new QGSetPayPasswordFragment());
            }
        });
        passWordEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        passWordEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        passWordEditText.setFocusable(true);
        passWordEditText.setFocusableInTouchMode(true);
        passWordEditText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager)passWordEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        inputManager.showSoftInput(passWordEditText, 0);

        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
        public void orderSDKCoins(String pass){
            // 向服务器请求下单
            String param = new QGParameter(mActivity)
                    .addParameter("uid", QGManager.getUID())
                    .addParameter("orderSubject", QGPayManager.getInstance().getOrderInfo().getOrderSubject())
                    // 商品描述
                    .addParameter("productOrderNo",QGPayManager.getInstance(). getOrderInfo().getProductOrderId())
                    .addParameter("amount", QGPayManager.getInstance().getOrderInfo().getAmount())
                    // 总价：必须传String格式
                    .addParameter("payType",173)
                    .addParameter("tradeType", "")
                    .addParameter("password",QGSdkUtils.getMD5Str(pass))
                    .addParameter("voucherCode",QGPayManager.getInstance().getVoucherCode())
                    // 支付方式： 支付宝 1 微信 7
                    .addParameter("extrasParams",QGPayManager.getInstance(). getOrderInfo().getExtrasParams())
                    .addParameter("serverName",QGPayManager.getInstance(). getRoleInfo().getServerName())
                    .addParameter("roleName",QGPayManager.getInstance(). getRoleInfo().getRoleName())
                    .addParameter("roleLevel", QGPayManager.getInstance().getRoleInfo().getRoleLevel() + "").create();
            HttpRequest<String> requestPay = new HttpRequest<String>() {
                @Override
                public void onSuccess(String bean) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            passWordEditText.setText("");
                        }
                    });
                    isOrdering=false;

              	final LoadingDialog loadingDialog=new LoadingDialog(mActivity);
							loadingDialog.show();
							TimerTask task = new TimerTask() {
								@Override
								public void run() {

									loadingDialog.dismiss();
									Intent intent=new Intent();
									intent.putExtra("result","success");
									intent.setClass(mActivity , QGPaySuccessActivity.class);
									mActivity.startActivityForResult(intent,10009);

								}
							};
							Timer timer = new Timer();
							timer.schedule(task, 1500);//3秒后执行TimeTask的run方法

                }

                @Override
                public void onFailed(int id, String message) {
                    passWordEditText.setText("");
                    isOrdering=false;
                    if ((id >= 50006 && id <= 50010)||id==50012) {
                        showLimitDiaLog(id,message);
                    } else if(id==50011){
                        showPassWordErroDialog(mActivity,message);
                    } else{
                        showErroDialog(message);
                    }


                }
            }.addParameter(param).post().setUrl(Constant.HOST + Constant.START_PAY);
            DataManager.getInstance().requestHttp(requestPay);
        }

    public void showLimitDiaLog(int id,String message) {
        String leftName="";
        if (id==50007&&info.getUserdata().getIsGuest()==1){
            leftName="个人中心";
        }
        if (id==50012){
            leftName="个人中心";
            message="根据相关规定未实名账号不能使用支付服务,请前往个人中心进行实名认证.";
        }
        final AlertDialog alert = new AlertDialog(mActivity, null,"防沉迷提示", message, leftName,
                "") {

            @Override
            public void onDismiss() {
                QGPayManager.getInstance().mPayCallBack.onFailed("防沉迷支付管控");
            }
        };
        alert.setClickListener(new AlertDialog.onClick() {
            @Override
            public void onLeftClick() {
                alert.dismiss();

                mActivity.setResult(PAY_LIMIT);
                mActivity.finish();

            }

            @Override
            public void onRightClick() {
                alert.dismiss();
            }
        });
        alert.show();
    }
    public void showErroDialog(String message) {
        final AlertDialog alertDialog=new AlertDialog(mActivity,null,"提示",message,"重试","") {
            @Override
            public void onDismiss() {
                passWordEditText.setText("");
            }
        };
        alertDialog.setClickListener(new AlertDialog.onClick() {
            @Override
            public void onLeftClick() {
                passWordEditText.setText("");
                alertDialog.dismiss();
            }

            @Override
            public void onRightClick() {
                passWordEditText.setText("");
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
   public void showPassWordErroDialog(Activity activity,String erro){
       LayoutInflater inflater = activity.getLayoutInflater();
       int id = QGSdkUtils.getResId(activity, "R.layout.qg_sdk_paypassworderro_dialog");
       View root = inflater.inflate(id, null, false);
       int style = QGSdkUtils.getResId(activity,
               "R.style.qg_dialog_style_fullscreen");
       final Dialog dialog = new Dialog(activity, style);
       dialog.setContentView(root);
       dialog.setCanceledOnTouchOutside(true);
       dialog.show();
       TextView message =(TextView)root.findViewById(QGSdkUtils.getResId(
               activity, "R.id.qg_sdk_pay_erro_message"));
       message.setText(erro);
       TextView  forget= (TextView) root.findViewById(QGSdkUtils.getResId(
               activity, "R.id.qg_sdk_pay_erro_forget"));
       forget.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               passWordEditText.setText("");
               dialog.dismiss();
               QGFragmentManager.getInstance(mActivity).add(new QGSetPayPasswordFragment());

           }
       });
       TextView retry = (TextView) root.findViewById(QGSdkUtils.getResId(
               activity, "R.id.qg_sdk_pay_erro_retry"));

       retry.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               passWordEditText.setText("");
               dialog.dismiss();
           }
       });

   }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.finish();
    }
}
