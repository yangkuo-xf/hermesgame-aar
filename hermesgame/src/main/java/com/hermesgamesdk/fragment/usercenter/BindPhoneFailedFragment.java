package com.hermesgamesdk.fragment.usercenter;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.QGManager;
import com.hermesgamesdk.manager.QGFragmentManager;


public class BindPhoneFailedFragment extends BaseFragment {
    public Button back;
    public Button other;
    public TextView tips;
    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_bindphonefailed_layout";
    }

    @Override
    protected String getTitle() {
        return "提示";
    }

    @Override
    protected void onRootViewInflated(View root) {
        mTitleBar.hideBackIcon();
        back=(Button)findView("R.id.qg_btn_switchotherphone");
        other=(Button)findView("R.id.qg_btn_switchtouserbind");
          tips=(TextView)findView("R.id.qg_txt_bf_tips");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    if (!QGManager.isGuest()){
        other.setVisibility(View.GONE);
        tips.setText("    此手机号已绑定其他账号，请切换绑定手机号码！");
    }
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          /*      Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("from", "slider_setName");
                intent.putExtras(bundle);
                intent.setClass(mActivity, TempActivty.class);
                mActivity.startActivityForResult(intent, 10001);*/
                QGFragmentManager
                        .getInstance(mActivity).add(new BindUsernameFragment());
            }
        });
    }


}
