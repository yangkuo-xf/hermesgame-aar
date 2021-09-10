package com.hermesgamesdk;

import android.content.Context;
import android.util.Log;

import com.bytedance.applog.AppLog;
import com.bytedance.applog.ILogger;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.util.UriConstants;
import com.hermesgame.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class RangersAppLog {


    public static void init(Context context, String appId, String your_channel) {
        // appid和渠道，appid须保证与广告后台申请记录一致，渠道可自定义，如有多个马甲包建议设置渠道号唯一标识一个马甲包。
        final InitConfig config = new InitConfig(appId, your_channel);

        //上报域名可根据业务情况自己设置上报域名，国内版本只支持上报到DEFAULT，海外GDRP版本只支持SINGAPORE、AMERICA
        /* 国内: DEFAULT */
        config.setUriConfig(UriConstants.DEFAULT);
        if (BuildConfig.DEBUG) {
            config.setLogger(new ILogger() {
                @Override
                public void log(String s, Throwable throwable) {
                    if (throwable == null) {
                        Log.d("AppLog", s);
                    }else {
                        Log.e("AppLog", s);
                    }
                }
            });

        }

        config.setEnablePlay(true); // 是否开启游戏模式，游戏APP建议设置为 true
        config.setAbEnable(true); // 是否开启A/B Test功能

        config.setAutoStart(true);
        AppLog.init(context.getApplicationContext(), config);
        /* 初始化结束 */

              /* 自定义 “用户公共属性”（可选，初始化后调用, key相同会覆盖）
              关于自定义 “用户公共属性” 请注意：1. 上报机制是随着每一次日志发送进行提交，默认的日志发送频率是1分钟，所以如果在一分钟内连续修改自定义用户公共属性，，按照日志发送前的最后一次修改为准， 2. 不推荐高频次修改，如每秒修改一次 */
        Map headerMap = new HashMap();
        headerMap.put("level", 8);
        headerMap.put("gender", "female");
        AppLog.setHeaderInfo((HashMap) headerMap);
    }


}
