package com.hermesgamesdk.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hermesgamesdk.skin.manager.base.BaseActivity;
import com.hermesgamesdk.utils.QGSdkUtils;

import java.util.Timer;
import java.util.TimerTask;


public class QGPaySuccessActivity extends BaseActivity {
    private static final int PAY_SUCCESS=1;
    private static final int PAY_FAIL=0;
       Button qg_pay_success_confirmbt;
       TextView qg_pay_success_seconds;
       String result="";
       Handler handler=new Handler(){
             @Override
             public void handleMessage(Message msg) {
                if (msg.what==1){
                    qg_pay_success_seconds.setText("1S");
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            if (result.equals("success")){
                                setResult(PAY_SUCCESS);
                            }else{
                                setResult(PAY_FAIL);
                            }

                            QGPaySuccessActivity.this.finish();
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 1000);//3秒后执行TimeTask的run方法
                }
             }
         };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(QGSdkUtils.getResId(this,"R.layout.qg_activity_notifi_pay_success"));
        setHideVirtualKey(getWindow());
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                setHideVirtualKey(getWindow());
            }
        });
        result=getIntent().getStringExtra("result");
        qg_pay_success_confirmbt =(Button)findViewById(QGSdkUtils.getResId(this,"R.id.qg_pay_success_confimbt"));
        qg_pay_success_seconds=(TextView)findViewById(QGSdkUtils.getResId(this,"R.id.qg_pay_success_seconds"));
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000);//3秒后执行TimeTask的run方法
        qg_pay_success_confirmbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.equals("success")){
                    setResult(PAY_SUCCESS);
                }else{
                    setResult(PAY_FAIL);
                }
                QGPaySuccessActivity.this.finish();
            }
        });

    }

    public void setHideVirtualKey(Window window) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19) {
            uiOptions |= 0x00001000;
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }
}
