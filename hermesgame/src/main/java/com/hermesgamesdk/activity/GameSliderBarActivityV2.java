package com.hermesgamesdk.activity;
import java.lang.reflect.Field;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.hermesgamesdk.manager.SliderBarV2Manager;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.ToastGame;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class GameSliderBarActivityV2 extends Activity {

    public FrameLayout blank;//空白域
    public RelativeLayout right_close_layout;

    public LinearLayout qg_web_load,qg_web_load_erro,layout_slider;
    public Button qg_web_load_erro_close,qg_sliderbar_v2_close,qg_web_load_erro_retry;

    private static final int REQUEST_CODE_UPLOAD=10002;//选择图片
    private static final int REQUEST_CODE_SCAN_RESULT=23;//扫码 支付
    private static final int REQUEST_CODE_FROM_TEMP=10001;
    public static String  action;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState!=null){
            finish();
        }
        //防止28的时候因为orientation闪退
        if (Build.VERSION.SDK_INT == 26) {
            boolean result = fixOrientation();         
        }
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra("action")!=null){

            action=getIntent().getStringExtra("action");
            SliderBarV2Manager.getInstance(this).setAction(action);
        }
        QGSdkUtils.setHideVirtualKey(getWindow());

        setContentView(QGSdkUtils.getResId(this, "R.layout.qg_sliderbar_v2"));
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                QGSdkUtils.setHideVirtualKey(getWindow());
            }
        });
        initViews();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        QGSdkUtils.setHideVirtualKey(getWindow());
        super.onConfigurationChanged(newConfig);
    }
    public void initViews() {
        qg_web_load = (LinearLayout) findViewById(QGSdkUtils.getResId(this, "R.id.frame_slider"));
        right_close_layout = (RelativeLayout) findViewById(QGSdkUtils.getResId(this, "R.id.right_close_layout"));
        layout_slider= (LinearLayout) findViewById(QGSdkUtils.getResId(this, "R.id.layout_slider"));
        qg_web_load_erro= (LinearLayout) findViewById(QGSdkUtils.getResId(this, "R.id.qg_web_load_erro"));
        blank = (FrameLayout) findViewById(QGSdkUtils.getResId(this, "R.id.slider_blank"));
        qg_web_load_erro_close = (Button) findViewById(QGSdkUtils.getResId(this, "R.id.qg_web_load_erro_close"));
        qg_sliderbar_v2_close= (Button) findViewById(QGSdkUtils.getResId(this, "R.id.qg_btn_sliderbar_v2_close"));
        qg_web_load_erro_retry= (Button) findViewById(QGSdkUtils.getResId(this, "R.id.qg_web_load_erro_retry"));
        blank.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            qg_web_load.setLayoutParams(new LinearLayout.LayoutParams((int) (getWindowManager().getDefaultDisplay().getWidth() * 0.7), ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            qg_web_load.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.6)));
        }

        SliderBarV2Manager.getInstance(this).initOtherView(GameSliderBarActivityV2.this,qg_web_load,blank,right_close_layout,layout_slider);
        try {
            SliderBarV2Manager.getInstance(this).show(qg_web_load,qg_web_load_erro);
        }catch(Exception e){
            Log.e("hermesgame","show SliderBar Exception: "+e.toString());
            e.printStackTrace();
        }


        qg_web_load_erro_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        qg_sliderbar_v2_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        qg_web_load_erro_retry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                qg_web_load_erro.setVisibility(View.GONE);
                qg_web_load.setVisibility(View.VISIBLE);
                SliderBarV2Manager.getInstance(GameSliderBarActivityV2.this).retry();
            }
        });

        SliderBarV2Manager.getInstance(GameSliderBarActivityV2.this).registReciver();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("hermesgame","SliderBarV2Activity onActivityResult: "+resultCode);
        if (requestCode == REQUEST_CODE_FROM_TEMP) {
            if (resultCode == 1) {
                SliderBarV2Manager.getInstance(this).reloadPage();
            }else if(resultCode==2){
               this.finish();
            }
        } else if (requestCode == REQUEST_CODE_UPLOAD) {
                SliderBarV2Manager.getInstance(this).uploadImagePath(data);
        }else if (requestCode==REQUEST_CODE_SCAN_RESULT){
            switch (resultCode) {
                case 0:
                    ToastGame.makeText(this,"支付成功", Toast.LENGTH_LONG).show();

                    break;
                case -1:
                    ToastGame.makeText(this,"支付失败", Toast.LENGTH_LONG).show();

                    this.finish();
                    break;
                case 3:
                default:
                    ToastGame.makeText(this,"支付失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        SliderBarV2Manager.getInstance(this).hide(qg_web_load);
        SliderBarV2Manager.getInstance(GameSliderBarActivityV2.this).unRegitReciver();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        //进出动画
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            overridePendingTransition(QGSdkUtils.getResId(this,"R.anim.slide_bar_in"),QGSdkUtils.getResId(this,"R.anim.slide_bar_out"));
        } else {
            overridePendingTransition(QGSdkUtils.getResId(this,"R.anim.slide_bar_in_bottom"),QGSdkUtils.getResId(this,"R.anim.slide_bar_out_bottom"));
        }

    }

    @Override
    public void onBackPressed() {
        SliderBarV2Manager.getInstance(this).onPhoneBackButtonClick();
    }


    private boolean fixOrientation(){
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo)field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public void setRequestedOrientation(int requestedOrientation) {
    	// TODO Auto-generated method stub
        if (Build.VERSION.SDK_INT == 26) {
           
            return;
        }
    	super.setRequestedOrientation(requestedOrientation);
    }

}
