package com.hermesgamesdk.view;

import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.utils.QGSdkUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class AlertDialog extends Dialog {
	private Context mContext;
	private BaseFragment mFragment;
	private TextView message;
	private TextView title;
	private ImageView closeButton;
	private Button btnLeft, btnRight;
	private String mTitle="";
	private String mMsg="";
	private String mLeftName="";
	private String mRightName="";
	private onClick mListener;

	public AlertDialog(Context context, @Nullable BaseFragment fragment,String title,String msg,String leftName,String rightName) {
		super(context, QGSdkUtils.getResId(context, "R.style.qg_dialog_style_fullscreen"));
		setHideVirtualKey(getWindow());
		setContentView(QGSdkUtils.getResId(context, "R.layout.qg_fragment_limited"));
		mContext = context;
		mFragment = fragment;
		mTitle=title;
		mMsg=msg;
		mLeftName=leftName;
		mRightName=rightName;
		init();
	}

	public void init() {
		btnRight = (Button) findViewById(QGSdkUtils.getResId(mContext, "R.id.qg_dialog_btn_right"));
		btnLeft = (Button) findViewById(QGSdkUtils.getResId(mContext, "R.id.qg_dialog_btn_left"));
		message = (TextView) findViewById(QGSdkUtils.getResId(mContext, "R.id.qg_limited_msg"));
		closeButton = (ImageView) findViewById(QGSdkUtils.getResId(mContext, "R.id.qg_limited_close"));
		title = (TextView) findViewById(QGSdkUtils.getResId(mContext, "R.id.qg_dialog_title"));
		message.setText(QGSdkUtils.ToDBC(mMsg));
		title.setText(mTitle);
		message.setMovementMethod(ScrollingMovementMethod.getInstance());
		


		if(mLeftName.equals("")){
			btnLeft.setVisibility(View.GONE);
		}else{
			btnLeft.setText(mLeftName);
		}
		if(mRightName.equals("")){
			btnRight.setVisibility(View.GONE);
		}else{
			btnRight.setText(mRightName);
		}
		if (btnRight != null) {
			btnRight.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mLeftName!=null){
						mListener.onRightClick();
					}

				}
			});
		}
		if (btnLeft != null) {
			btnLeft.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mLeftName!=null){
						mListener.onLeftClick();
					}
				}
			});
		}
		if (closeButton != null) {
			closeButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					onDismiss();
					AlertDialog.this.dismiss();
				}
			});
		}
		setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				 onDismiss();
			}
		});
	}

	@Override
	public void show() {
		super.show();
	}
	public void hideClose(){
		closeButton.setVisibility(View.GONE);
	}
	public void setClickListener(onClick listener){
		mListener=listener;
	}

	public abstract void onDismiss();

	public interface onClick{
		void onLeftClick();
		void onRightClick();
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
