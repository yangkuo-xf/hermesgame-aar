package com.hermesgamesdk.view;



import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.hermesgamesdk.utils.QGSdkUtils;

/**
 * Created by cebaoyu on 2017/7/15.
 */

public class ToastGame {
    private Toast mToast;
    private Context mContext;

    private ToastGame(Context context, CharSequence text, int duration) {
        this.mContext = context;
        if (mContext == null) {
            return;
        }
        View v = LayoutInflater.from(context).inflate(QGSdkUtils.getResId(mContext,"R.layout.qg_toast_layout"), null);
        TextView textView = (TextView) v.findViewById(QGSdkUtils.getResId(mContext,"R.id.toast_game"));
        textView.setText(text);
        mToast = new Toast(context);
        mToast.setDuration(duration);
        mToast.setView(v);
    }

    public static ToastGame makeText(Context context, CharSequence text, int duration) {
        return new ToastGame(context, text, duration);
    }

    public void show() {
        if (mToast != null && mContext != null) {
            setToastGravity();
            mToast.show();
        }
    }

    public void setToastGravity() {
        if (mToast != null) {
            Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
            // 获取屏幕高度
            int height = display.getHeight();
            mToast.setGravity(Gravity.BOTTOM, 0, height / 4);
        }
    }
}
