package com.hermesgamesdk.manager;

import android.content.Context;
import android.util.Log;

import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.floatview.QGFloatView;

/**
 * 悬浮窗
 */
public final class QGFloatViewManager {

	private static QGFloatViewManager instance = null;
	private QGFloatView floatView;

	public static QGFloatViewManager getInstance() {
		if (instance == null) {
			instance = new QGFloatViewManager();
		}
		return instance;
	}

	public void destroy() {
		instance = null;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(final Context context) {
		Log.d("qg.float", "init");
		if (floatView == null) {
			floatView = new QGFloatView(context);
			floatView.setAnimDuration(0);
			floatView.setHideDelay(3500);
		}
	}

	/**
	 * 显示
	 */
	public void show(boolean isLeft) {
		Log.d("qg.float", "show");
		if (floatView != null&& QGConfig.isShowFloat()) {
			floatView.showFloatView(isLeft);
		}
	}

	/**
	 * 隐藏
	 */
	public void hide( ) {
		Log.d("qg.float", "hide");
		if (floatView != null) {
			floatView.hideFloatView();
			//SliderBarV2Manager.getInstance(activity).userCenterClosed();
		}
	}

	/**
	 * 回收资源
	 */
	@Deprecated
	public void recycle() {
		floatView.recycle();
	}

}
