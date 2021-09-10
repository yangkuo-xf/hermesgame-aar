package com.hermesgamesdk.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.utils.QGSdkUtils;

public class ToastLoginSuccess {
    private Toast mToast;
    private Context mContext;
    private ImageView mIcon;
    private LinearLayout toast_login_layout;

    private ToastLoginSuccess(Context context, CharSequence text, int duration) {
        this.mContext = context;
        View v = LayoutInflater.from(context).inflate(QGSdkUtils.getResId(mContext,"R.layout.qg_toast_login_success"), null);
        mToast = new Toast(context);
        mToast.setDuration(duration);
        mToast.setView(v);

        TextView textView = (TextView) v.findViewById(QGSdkUtils.getResId(mContext,"R.id.toast_game"));
        TextView qg_toast_user_limit = (TextView) v.findViewById(QGSdkUtils.getResId(mContext,"R.id.qg_toast_user_limit"));
        QGUserInfo userInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);

        toast_login_layout = (LinearLayout) v.findViewById(QGSdkUtils.getResId(mContext,"R.id.toast_login_layout"));

        mIcon=(ImageView)v.findViewById(QGSdkUtils.getResId(mContext,"R.id.qg_toast_login_success_icon"));
        textView.setText(text);
      if (userInfo.getCheckrealname()==Constant.CRETIFICATION_HAS_CERTIFIED&&userInfo.getCkPlayTime()==1){

            int age=userInfo.getuAge();
            String limitText="";
            double hours=1.5;
          /*  if (HolidayUtils.getInstance().isHolidays((String)DataManager.getInstance().getData("timestamp"))){
                hours=3;
            }*/
            if (age>0&&age<8){
                qg_toast_user_limit.setVisibility(View.VISIBLE);
                limitText="您的游戏时长限制为"+hours+"小时,无法使用充值功能";
                qg_toast_user_limit.setText(limitText);
            }else if(age>=8&&age<16){
                qg_toast_user_limit.setVisibility(View.VISIBLE);
                 limitText="您的游戏时长限制为"+hours+"小时,充值限额为200元/月";
                qg_toast_user_limit.setText(limitText);
            }else if (age>=16&&age<18){
                qg_toast_user_limit.setVisibility(View.VISIBLE);
                limitText="您的游戏时长限制为"+hours+"小时,充值限额为400元/月";
                qg_toast_user_limit.setText(limitText);
            }else{

            }
            //2020.11.12. 置为空  改为弹窗
          qg_toast_user_limit.setText("");
      }

        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), 0);
            Drawable icon = info.loadIcon(pm);
            mIcon.setImageDrawable(icon);
        }catch (Exception e){
            mIcon.setVisibility(View.GONE);
        }

    }

    public static ToastLoginSuccess makeText(Context context, CharSequence text, int duration) {
        return new ToastLoginSuccess(context, text, duration);
    }

    public void show() {
        if (mToast != null && mContext != null) {
            setToastGravity();
            mToast.show();
        }
    }

    public void setToastGravity() {
        if (mToast != null) {
            mToast.setGravity(Gravity.TOP, 0, 0);
        }
    }
}
