package com.xhs.jlcs;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hermesgamesdk.QGManager;
import com.hermesgamesdk.callback.QGCallBack;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.QGOrderInfo;
import com.hermesgamesdk.entity.QGRoleInfo;

import java.math.BigDecimal;


public class MainActivity extends Activity {


    private boolean isLogin = false;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{
                Manifest.permission.READ_PHONE_STATE}, 1);


        String test = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("dd", test);

        QGManager.setLogoutCallback(new QGCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
//隐藏浮窗
                QGManager.hideFloat();
            }

            @Override
            public void onFailed(String msg) {
                Toast.makeText(MainActivity.this, "注销失败", Toast.LENGTH_SHORT).show();

            }
        });


//        InitConfig config = new InitConfig(appId, your_channel);


        QGManager.init(this, "251934", "76794852342605514089011364498025", new QGCallBack() {
            @Override
            public void onSuccess() {
//                Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
                Log.e("QGManager", "初始化成功");
            }

            @Override
            public void onFailed(String msg) {
//                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                Log.e("QGManager", msg);
            }
        });
         RangersAppLog.init(this, "251934", Constant.BYTEDANCE_ID);




        //提交角色信息
        findViewById(R.id.btn_uploadUserInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin) {
                    Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                QGRoleInfo roleInfo = new QGRoleInfo();
                roleInfo.setBalance("9999");
                roleInfo.setPartyName("天地");
                roleInfo.setRoleId("10086");
                roleInfo.setRoleName("白富美");
                roleInfo.setServerName("888");
                roleInfo.setVipLevel("999");
                roleInfo.setRoleLevel("999");
                QGManager.setGameRoleInfo(MainActivity.this, roleInfo);
            }

        });

        //退出游戏
        findViewById(R.id.btn_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QGManager.exit(MainActivity.this, new QGCallBack() {
                    @Override
                    public void onSuccess() {
                        finish();
                    }

                    @Override
                    public void onFailed(String msg) {
                    }
                });
            }

        });

        //注销
        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QGManager.logout(MainActivity.this);
            }

        });

        //支付
        findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QGOrderInfo mOrderInfo = new QGOrderInfo();
                QGRoleInfo mRoleInfo = new QGRoleInfo();
//角色信息
                mRoleInfo.setRoleId("123546421321");
                mRoleInfo.setRoleLevel("1");
                mRoleInfo.setRoleName("hhaha");
                mRoleInfo.setServerName("zzz");
                mRoleInfo.setVipLevel("666");
// 订单信息
                mOrderInfo.setAmount("0.01");
                mOrderInfo.setCount(1);
                mOrderInfo.setExtrasParams("2017110403");
                mOrderInfo.setPayParam("eedwd");
                mOrderInfo.setOrderSubject("钻石");
                mOrderInfo.setProductOrderId("2017110403");


                QGManager.pay(MainActivity.this, mRoleInfo, mOrderInfo, new QGCallBack() {
                    @Override
                    public void onSuccess() {
                        //内置事件 “支付”，属性：商品类型，商品名称，商品ID，商品数量，支付渠道，币种，是否成功（必传），金额（必传）
                        RangersAppLog.onEventPurchase(mOrderInfo.getOrderSubject(),mOrderInfo.getOrderSubject(), mOrderInfo.getProductOrderId(),mOrderInfo.getCount(),
                                "wechat","¥", true, new BigDecimal(mOrderInfo.getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
                        
                        
                        
                        Log.e("QGManager", "支付成功");
                    }

                    @Override
                    public void onFailed(String msg) {
                        Log.e("QGManager", msg);
                    }
                });
            }

        });


        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                QGManager.login(MainActivity.this, new QGCallBack() {
                    @Override
                    public void onSuccess() {
                        QGManager.getUID();
                        QGManager.getUserName();
                        QGManager.getLoginToken();
                        isLogin = true;

                        Log.e("QGManager", "登录成功");
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        QGManager.hideFloat();
        RangersAppLog.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RangersAppLog.onResume(this);
        if (!TextUtils.isEmpty(QGManager.getUID())) {
            QGManager.showFloat(true);
        }

    }


}