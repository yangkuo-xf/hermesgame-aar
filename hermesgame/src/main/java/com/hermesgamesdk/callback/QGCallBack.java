package com.hermesgamesdk.callback;

/**
 * Created by Administrator on 2017/9/1.
 */

//TODO 支付，登录 回调信息
public interface QGCallBack {
    void onSuccess();

    void onFailed(String msg);
}
