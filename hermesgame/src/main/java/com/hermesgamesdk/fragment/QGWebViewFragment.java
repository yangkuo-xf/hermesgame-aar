package com.hermesgamesdk.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.WebViewLoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/9/14.
 */

@SuppressLint("SetJavaScriptEnabled")
public class QGWebViewFragment extends BaseFragment {

    public static final int USER_AGREEMENT_LOGIN = 0;
    public static final int USER_AGREEMENT_REGIST = 1;//用户协议
    public static final int USER_AGREEMENT_PRIVATE = 2;//隐私协议
    public static final int NITICE = 2;
    public static final int CERT_LIMIT = 3;
    private int flag;

    Button mAccept, mRefuse;

    private WebView webview;
    private ImageView qg_agreement_cancle_button;
    private LinearLayout qg_agreement_container;

    private TextView qg_webview_title;

    private WebViewLoadingDialog loadingDialog;
    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_webview";
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    protected String getTitle() {
     return "";
    }

    @Override
    protected void onRootViewInflated(View root) {
        initView();
        if (flag == USER_AGREEMENT_LOGIN || flag == USER_AGREEMENT_REGIST) {
            displayAgreement(false);
        } else if (flag == USER_AGREEMENT_PRIVATE){
            displayAgreement(true);
        }
        else if(flag == CERT_LIMIT){
            displayLimit();
        }else {
            displayNotice();
        }

    }

    @Override
    public void onClicks(int id) {

        if (mRefuse.getId() == id) {
      /*      if (getActivity().getIntent().getStringExtra("from").equals("LOGIN")) {
                forceBack();
            }else if (getActivity().getIntent().getStringExtra("from").equals("REGIST")){
                getActivity().setResult(2);
                mActivity.finish();
            }*/
            forceBack();

        } else if (mAccept.getId() == id) {
            if (getActivity().getIntent().getStringExtra("from").equals("LOGIN")) {
                getActivity().setResult(1);
                mActivity.finish();
            } else if (getActivity().getIntent().getStringExtra("from").equals("REGIST")) {
                getActivity().setResult(2);
                mActivity.finish();
            } else {
                forceBack();
            }

        } else if (qg_agreement_cancle_button.getId() == id) {
            forceBack();
        }
    }

    private void initView() {
        webview = (WebView) findView("R.id.qg_webview");
        mAccept = (Button) findView("R.id.qg_accept_agreement");
        mRefuse = (Button) findView("R.id.qg_refuse_agreement");
        qg_webview_title= (TextView) findView("R.id.qg_webview_title");
        qg_agreement_cancle_button = (ImageView) findView("R.id.qg_agreement_cancle_button");
        qg_agreement_container = (LinearLayout) findView("R.id.qg_agreement_container");
        webview.getSettings().setJavaScriptEnabled(true);
        if (flag == USER_AGREEMENT_LOGIN || flag == USER_AGREEMENT_REGIST) {
            qg_webview_title.setText(getString("R.string.qk_useragreement_user_agreement"));
        } else if(flag == USER_AGREEMENT_PRIVATE){
            qg_webview_title.setText(getString("R.string.qk_priavateagreement_priavate_agreement"));
        } else if(flag ==  CERT_LIMIT){
            qg_webview_title.setText("关于防止未成年人沉迷网络游戏的通知");
            mRefuse.setText("返回");
            mAccept.setText("确定");
        } else{
            qg_webview_title.setText(getString("R.string.qg_notice"));
        }

        mRefuse.setOnClickListener(listener);
        mAccept.setOnClickListener(listener);
        qg_agreement_cancle_button.setOnClickListener(listener);
        loadingDialog =new WebViewLoadingDialog(mActivity);
        loadingDialog.show();
        webview.setWebViewClient(new MyQGWebViewClient());

    }


    private void displayAgreement(final boolean isPrivate) {
       /* if (flag == USER_AGREEMENT_REGIST) {
            qg_agreement_container.setVisibility(View.GONE);
        } else {
            qg_agreement_container.setVisibility(View.VISIBLE);
        }*/

        HttpRequest<String> request = new HttpRequest<String>() {
            @Override
            public void onSuccess(String bean) {
                try {
                    JSONObject json = new JSONObject(bean);
                    String agreement ="";
                    if (isPrivate){
                        if(json.getString("privacy")!=null&&!json.getString("privacy").equals("")){
                            agreement =   json.getString("privacy");
                        }else{
                            agreement =   json.getString("agreement");
                        }

                    }else {
                        agreement =   json.getString("agreement");
                    }


                    webview.loadDataWithBaseURL("", agreement, "text/html", "utf-8", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(int id,String message) {

            }
        }.post().setUrl(Constant.HOST + Constant.AGREEMENT).addParameter(new QGParameter(mActivity).create());

        DataManager.getInstance().requestHttp(request);
    }

    private void displayNotice() {
        mRefuse.setVisibility(View.GONE);
        mAccept.setText("我知道了");
        webview.loadDataWithBaseURL("", Constant.noticeContent, "text/html", "utf-8", "");
    }
    private void displayLimit() {
        mRefuse.setVisibility(View.GONE);
        mAccept.setText("我知道了");
        webview.loadDataWithBaseURL("", "      "+getString(QGSdkUtils.getResId(mActivity,"R.string.qg_limit_message")), "text/html", "utf-8", "");
    }
    class MyQGWebViewClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismiss();
                }
            });

        }
    }

}
