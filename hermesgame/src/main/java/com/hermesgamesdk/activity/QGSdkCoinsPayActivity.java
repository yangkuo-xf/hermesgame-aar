package com.hermesgamesdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hermesgamesdk.fragment.pay.QGSdkCoinsPayFragment;

public class QGSdkCoinsPayActivity extends BaseActivity {
    private static final int PAY_SUCCESS=1;
    private static final int PAY_FAIL=0;
    @Override
    protected Fragment getDefaultFragment() {
        return new QGSdkCoinsPayFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getResId("R.layout.qg_activity_fullscreen_layout"));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //平台币
            switch (resultCode) {
                case PAY_SUCCESS:
                    setResult(PAY_SUCCESS);
                    this.finish();
                    break;
                case PAY_FAIL:
                    setResult(PAY_FAIL);
                    this.finish();
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
/*
      String from=QGSdkCoinsPayActivity.this.getIntent().getStringExtra("from");
        if (from!=null&&from.equals("Slider")){
            setResult(10009);
        }*/
        super.onDestroy();
    }
}
