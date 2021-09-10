package com.hermesgamesdk.manager;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.QGManager;
import com.hermesgamesdk.callback.QGCallBack;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.quickjoy.adplus.ADP;


/**
 * 用于退出业务逻辑和退出回调结果处理
 * 
 */
public class ExitManager {
	private static ExitManager mExitManager;
	Dialog dialog;

	private ExitManager() {
	}

	public static ExitManager getInstance() {
		if (mExitManager == null)
			mExitManager = new ExitManager();
		return mExitManager;
	}

	/**
	 * @param activity
	 * @param exitCallBack
	 *            cp传入的回调对象 游戏调用调用退出接口后创建退出框 退出框消失后发送回调通知，确认退出时清楚sdk保存在内存中的数据
	 */
	public void exit(final Activity activity, final QGCallBack exitCallBack) {

		LayoutInflater inflater = activity.getLayoutInflater();
		int id = QGSdkUtils.getResId(activity, "R.layout.qg_exit_dialog");
		View root = inflater.inflate(id, null, false);
		int style = QGSdkUtils.getResId(activity,
				"R.style.qg_dialog_style");

		dialog = new Dialog(activity, style);
		dialog.setContentView(root);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);

		TextView cancel = (TextView) root.findViewById(QGSdkUtils.getResId(
				activity, "R.id.qg_exit_cancel"));
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				// 取消退出发送退出取消通知
				exitCallBack.onFailed("exit cancel");

			}
		});
		TextView sure = (TextView) root.findViewById(QGSdkUtils.getResId(
				activity, "R.id.qg_exit_sure"));

		sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!QGManager.getUID().equals("")){
					LoginManager.getInstance().postSessionId();
				}
				dialog.dismiss();
				releaseResource(activity);
				exitCallBack.onSuccess();
				if (QGConfig.isSupportAD()) {
					ADP.getInstance().exit();
				}

			}
		});
		if (activity==null||activity.isFinishing()){

		}else{
			dialog.show();
		}

	}

	/**
	 * 
	 * 释放资源，关闭客服服务，销毁所有manager，清除数据等
	 */
	private void releaseResource(Activity activity) {
		try {
			Class<?> serviceClazz = Class
					.forName("com.qk.plugin.customservice.QKCustomService");
			Method getInstance = serviceClazz.getDeclaredMethod("getInstance");
			Object service = getInstance.invoke(null);
			Method onDestroy = service.getClass().getDeclaredMethod(
					"onDestroy", Activity.class);
			onDestroy.invoke(service, activity);
		} catch (Exception e) {
			e.printStackTrace();
		}

		InitManager.getInstance().destroy();
		LoginManager.getInstance().destroy();
		QGPayManager.getInstance().destroy();
		ThirdManager.getInstance().destroy();
		DataManager.getInstance().destroy();
		QGFloatViewManager.getInstance().destroy();
		SliderBarV2Manager.getInstance(activity).destory();
		Constant.destory();
		QGConfig.destory();

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
