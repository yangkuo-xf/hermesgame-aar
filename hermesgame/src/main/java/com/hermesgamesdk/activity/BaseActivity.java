package com.hermesgamesdk.activity;


import java.lang.reflect.Field;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.skin.manager.base.BaseFragmentActivity;
import com.hermesgamesdk.utils.QGSdkUtils;

public abstract class BaseActivity extends BaseFragmentActivity {

    /**
     * 返回默认的Fragment
     */
    protected abstract Fragment getDefaultFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == 26) {
            boolean result = fixOrientation();         
        }
        super.onCreate(savedInstanceState);
         QGSdkUtils. setHideVirtualKey(getWindow());
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                QGSdkUtils.setHideVirtualKey(getWindow());
            }
        });
        DataManager.getInstance().init(this);
        QGFragmentManager.getInstance(this).add(getDefaultFragment());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QGFragmentManager.getInstance(this).onActivityDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            QGFragmentManager.getInstance(this).back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取资源ID，参数
     */
    public int getResId(String parameter) {
        String[] parameters = parameter.split("\\.");
        String resType = parameters[1];
        String resName = parameters[2];
        return getResources().getIdentifier(resName, resType, getPackageName());
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
