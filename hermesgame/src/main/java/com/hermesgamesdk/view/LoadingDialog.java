package com.hermesgamesdk.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hermesgamesdk.utils.QGSdkUtils;

public class LoadingDialog extends Dialog {
    private  ImageView img;
    private  AnimationDrawable animationDrawable;


    public LoadingDialog(@NonNull Context context) {

        super(context, QGSdkUtils.getResId(context, "R.style.qg_dialog_style_fullscreen"));
        setContentView(QGSdkUtils.getResId(context, "R.layout.qg_loading_dialog"));
        img=(ImageView)findViewById(QGSdkUtils.getResId(context,"R.id.qg_loading_dialog_img"));
        img.setBackgroundResource(QGSdkUtils.getResId(context, "R.drawable.qg_loading_dialog_anim"));
        animationDrawable = (AnimationDrawable) img.getBackground();
        // 按返回键是否取消
        this.setCancelable(false);
        // 点击外部是否取消
        this.setCanceledOnTouchOutside(false);
        // 设置居中
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        // 设置背景层透明度
        lp.dimAmount = 0.2f;
        this.getWindow().setAttributes(lp);
        animationDrawable.start();
    }




/*    *//**
     * 弹出自定义LoadingDialog
     *
     * @param context        上下文
     * @return 自定义的LoadingDialog
     *//*
    public static LoadingDialog show(Context context) {
        LoadingDialog dialog = new LoadingDialog(context,QGSdkUtils.getResId(context, "R.style.qg_dialog_style_fullscreen"));
        dialog.setTitle("");

        dialog.setContentView(QGSdkUtils.getResId(context, "R.layout.qg_loading_dialog"));

        // 按返回键是否取消
        dialog.setCancelable(false);
        // 点击外部是否取消
        dialog.setCanceledOnTouchOutside(false);
        // 设置居中
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        // 设置背景层透明度
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        animationDrawable.start();
        dialog.show();
        return dialog;
    }*/
}
