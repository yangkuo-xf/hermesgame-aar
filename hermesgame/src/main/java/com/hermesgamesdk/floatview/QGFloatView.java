package com.hermesgamesdk.floatview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.hermesgamesdk.activity.GameSliderBarActivityV2;
import com.hermesgamesdk.floatview.content.QGContentState;
import com.hermesgamesdk.floatview.content.QGFloatContent;
import com.hermesgamesdk.floatview.logo.QGFloatLogo;
import com.hermesgamesdk.utils.QGSdkUtils;

import static com.hermesgamesdk.floatview.content.QGContentState.ANIM_LEFT_HIDDEN;
import static com.hermesgamesdk.floatview.content.QGContentState.ANIM_LEFT_SHOWING;
import static com.hermesgamesdk.floatview.content.QGContentState.ANIM_RIGHT_HIDDEN;
import static com.hermesgamesdk.floatview.content.QGContentState.ANIM_RIGHT_SHOWING;
import static com.hermesgamesdk.floatview.content.QGContentState.LEFT_HIDE;
import static com.hermesgamesdk.floatview.content.QGContentState.LEFT_SHOW;
import static com.hermesgamesdk.floatview.content.QGContentState.RIGHT_HIDE;
import static com.hermesgamesdk.floatview.content.QGContentState.RIGHT_SHOW;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_LEFT_HIDE;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_LEFT_OPEN;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_RIGHT_HIDE;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_RIGHT_OPEN;

// 悬浮窗示意图，左侧展开状态

// ------@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// ---@---------@-------------------@@@-------------------@@@--------@
// -@-------------@--------------@-------@-------------@-------@--------@
// @---------------@-----------@-----------@---------@-----------@--------@
// @-----logo------@-----------@----unit---@---------@----unit---@--------@
// @---------------@-----------@-----------@---------@-----------@--------@
// -@------------@---------------@-------@-------------@-------@--------@-
// ---@---------@-------------------@@@-------------------@@@---------@
// ------@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

// 各种View宽度

// QGLogoView宽度
// @=====logo======@-----------@====unit===@---------@====unit===@--------@
// |<=============>|-------------------------------------------------------

// QGContentView宽度
// @=====logo======@-----------@====unit===@---------@====unit===@--------@
// |<====================================================================>|

// QGContent宽度
// @=====logo======@-----------@====unit===@---------@====unit===@--------@
// ----------------------------|<===============================>|---------

// QGUnit宽度
// @=====logo======@-----------@====unit===@---------@====unit===@--------@
// ----------------------------|<=========>|-------------------------------

// 各种变量意义

// 类QGFloatContent 成员spaceToLogoX:
// @=====logo======@-----------@====unit===@---------@====unit===@--------@
// ----------------|<=========>|-------------------------------------------

// 类QGFloatContent 成员spaceToEdgeX:
// @=====logo======@-----------@====unit===@---------@====unit===@--------@
// --------------------------------------------------------------|<======>|

// 类QGFloatContent 成员shiftToParent:
// @=====logo======@-----------@====unit===@---------@====unit===@--------@
// |<=====>|---------------------------------------------------------------

/**
 * 悬浮窗控件
 */
public final class QGFloatView {

	private static final String TAG = "QGFloatView";

	private int hideDelay = 1500;

	//设置悬浮窗高度,宽高等比列,单位dp
	private float floatViewHeightDP = 40f;

	private Context context;
	private WindowManager wm;
	private DisplayMetrics metrics;

	private QGFloatLogo floatLogo;
	private QGFloatContent floatContent;

	private Handler handler;
	private HiddenTask stateTask;

	public QGFloatView(Context context) {
		this.context = context;
		init();
	}

	private void init() {

		// 缩放图片大小
		Bitmap logo = BitmapFactory.decodeResource(context.getResources(),
				QGSdkUtils.getResId(context, "R.drawable.qg_float_view_logo"));
		int diameter = Math.max(logo.getWidth(), logo.getHeight());
		logo.recycle();
		ScaleBitmapFactory.setScale(context.getResources().getDisplayMetrics().density * floatViewHeightDP / diameter);

		metrics = new DisplayMetrics();
		
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		//wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		
		wm.getDefaultDisplay().getMetrics(metrics);

		handler = new Handler(context.getMainLooper());
		stateTask = new HiddenTask();

		floatLogo = new QGFloatLogo(context, metrics, wm);
		floatContent = new QGFloatContent(context, metrics, wm);

		floatLogo.updateLogoLocation(getFloatViewLocation());
		floatContent.updateContentLocation(getFloatViewLocation());

		// WindowManager添加view
		addFloatView();

		handler.postDelayed(stateTask, hideDelay);

		initCallback();

	}

	/**
	 * 设置监听事件，目的在于控制logo和content需要交互的逻辑，没有交互的逻辑在其自身内部处理
	 */
	private void initCallback() {
		floatLogo.setLogoActionListener(new LogoActionListener());
		floatLogo.setLogoAnimListener(new LogoAnimListener());
		floatContent.setContentActionListener(new ContentActionListener());
		floatContent.setContentAnimListener(new ContentAnimListener());
		floatContent.setContentStateChangeListener(new ContentStateChangeListener());
	}

	private class LogoActionListener implements QGFloatLogo.LogoActionListener {

		@Override
		public void onTouchValid(MotionEvent v) {

			handler.removeCallbacks(stateTask);
		}

		@Override
		public void onClickValid(View v) {

			// Logo点击事件
			animContentView();
		}
	}

	private class LogoAnimListener implements QGFloatLogo.LogoAnimListener {

		@Override
		public void onAnimatetart() {
			handler.removeCallbacks(stateTask);
		}

		@Override
		public void onAnimateEnd() {
			handler.removeCallbacks(stateTask);
			handler.postDelayed(stateTask, hideDelay);
			saveFloatViewLocation(floatLogo.getLogoViewParams());
		}
	}

	/**
	 * 当content有效触摸和点击时，需要关闭设置的隐藏任务，无效点击时
	 */
	private class ContentActionListener implements QGFloatContent.OnContentActionListener {
		@Override
		public void onTouchValid(boolean valid) {
			handler.removeCallbacks(stateTask);
		}

		@Override
		public void onClickValid(boolean valid) {
			handler.removeCallbacks(stateTask);
			if (!valid) {
				handler.postDelayed(stateTask, hideDelay);
			}

		}
	}

	/**
	 * 当content动画完成时，需要设置隐藏任务，让content自动隐藏
	 */
	private class ContentAnimListener implements QGFloatContent.OnContentAnimListener {
		@Override
		public void onAnimteStart() {

		}

		@Override
		public void onAnimateEnd() {
			handler.removeCallbacks(stateTask);
			handler.postDelayed(stateTask, hideDelay);
		}
	}

	/**
	 * 当content的状态改变时，需要设置logo触摸点击是否有效
	 */
	private class ContentStateChangeListener implements QGFloatContent.OnStateChangeListener {
		@Override
		public void onStateChange(QGContentState state) {
			// Log.d(TAG, "ContentStateChangeListener onStateChange state=" + state);
			if (state == ANIM_LEFT_SHOWING || state == ANIM_RIGHT_SHOWING || state == ANIM_LEFT_HIDDEN
					|| state == ANIM_RIGHT_HIDDEN) {
				floatLogo.setTouchValid(false);
				floatLogo.setClickValid(false);
			}
			else if (state == LEFT_SHOW || state == RIGHT_SHOW) {
				floatLogo.setTouchValid(false);
				floatLogo.setClickValid(true);
			}
			else if (state == LEFT_HIDE || state == RIGHT_HIDE) {
				floatLogo.setTouchValid(true);
				floatLogo.setClickValid(true);
			}
		}
	}

	/**
	 * 显示悬浮窗
	 */
	public void showFloatView(boolean isLeft) {
		addFloatView();
		floatContent.show();
		if (!isLeft){
			//floatLogo.changeLogoState(LOGO_RIGHT_HIDE);
		}
		floatLogo.show(new QGFloatLogo.ShowCallback() {
			@Override
			public void complete() {
				handler.removeCallbacks(stateTask);
				handler.postDelayed(stateTask, hideDelay);
			}
		});
	}

	/**
	 * 隐藏悬浮窗
	 */
	public void hideFloatView() {
		removeFloatView();
		floatLogo.hide();
		floatContent.hide();
	}

	/**
	 * WindowManager添加View
	 */
	public void addFloatView() {
		floatContent.addFloatView();
		floatLogo.addFloatView();
	}

	/**
	 * WindowManager移除View
	 */
	public void removeFloatView() {
		floatContent.removeFloatView();
		floatLogo.removeFloatView();
	}

	/**
	 * 添加单元并设置点击事件
	 * 
	 * @param imgId
	 * @param listener
	 */
	public QGFloatView addContent(int imgId, View.OnClickListener listener) {
		floatContent.addContent(imgId, listener);
		return this;
	}

	/**
	 * 回收资源
	 */
	@Deprecated
	public void recycle() {

	}

	/**
	 * 设置动画时间，单位：毫秒
	 */
	public void setAnimDuration(int duration) {
		if (floatContent != null)
			floatContent.setAnimDuration(duration);
	}

	/**
	 * 设置状态改变等待时间，单位：毫秒
	 */
	public void setHideDelay(int delay) {
		this.hideDelay = delay;
	}

	/**
	 * 延迟一定时间就改变悬浮窗状态，例如：content为展开状态---3s后--->content内容隐藏---3s后--->logo靠边隐藏
	 */
	private class HiddenTask implements Runnable {

		@Override
		public void run() {
			Log.d(TAG,
					"[HiddenTask] LogoState = " + floatLogo.getLogoState() + ", ContentState = "
							+ floatContent.getContentState());

			if (floatLogo.getLogoState() == LOGO_LEFT_HIDE || floatLogo.getLogoState() == LOGO_RIGHT_HIDE) {
				return;
			}
			switch (floatLogo.getLogoState()) {
				case LOGO_LEFT_EDGE:
					floatLogo.changeLogoState(LOGO_LEFT_HIDE);
					break;
				case LOGO_RIGHT_EDGE:
					floatLogo.changeLogoState(LOGO_RIGHT_HIDE);
					break;
				case LOGO_LEFT_OPEN:
					if (floatContent.getContentState() == LEFT_SHOW) {
						floatContent.hideContent();
					}
					else if (floatContent.getContentState() == LEFT_HIDE
							|| floatContent.getContentState() == RIGHT_HIDE) {
						floatLogo.setLogoHalfAtLeft();
					}
					break;
				case LOGO_RIGHT_OPEN:
					if (floatContent.getContentState() == RIGHT_SHOW) {
						floatContent.hideContent();
					}
					else if (floatContent.getContentState() == RIGHT_HIDE
							|| floatContent.getContentState() == LEFT_HIDE) {
						floatLogo.setLogoHalfAtRight();
					}
					break;
				default:
					break;
			}
			saveFloatViewLocation(floatLogo.getLogoViewParams());
		}
	}

	/**
	 * 获取之前保存的悬浮窗位置
	 */
	private Point getFloatViewLocation() {
		SharedPreferences sharedPreferences = context.getSharedPreferences("floatview..location", Context.MODE_PRIVATE);
		//最右
		//  int x = sharedPreferences.getInt("floatview.location.x",metrics.widthPixels );
		int x = sharedPreferences.getInt("floatview.location.x",0 );
		int y = sharedPreferences.getInt("floatview.location.y", 80);

		// Log.d(TAG, "getFloatViewLocation x=" + x + " y=" + y);
		return new Point(x, y);
	}

	/**
	 * 保存悬浮窗位置
	 */
	private void saveFloatViewLocation(WindowManager.LayoutParams layoutParams) {
		// Log.d(TAG, "saveFloatViewLocation x=" + layoutParams.x + " y=" + layoutParams.y);
		SharedPreferences sharedPreferences = context.getSharedPreferences("floatview..location", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("floatview.location.x", layoutParams.x);
		editor.putInt("floatview.location.y", layoutParams.y);
		editor.apply();
	}

	/**
	 * QGContentView动画，显示或者隐藏
	 */
	public void animContentView() {
		int x = -1;
		int y = -1;
		Log.d(TAG,"animContentView  floatLogo = " + floatLogo.getLogoState() + ",  floatContent = "
						+ floatContent.getContentState());
		if (floatContent.getContentState() == LEFT_SHOW || floatContent.getContentState() == RIGHT_SHOW) {
			floatContent.hideContent();
		}
		else if (floatContent.getContentState() == LEFT_HIDE || floatContent.getContentState() == RIGHT_HIDE) {
			if (floatLogo.getLogoState() == LOGO_LEFT_OPEN) {
				floatContent.changeState(LEFT_HIDE);
				x = floatLogo.getLogoViewParams().x;
			}
			if (floatLogo.getLogoState() == LOGO_RIGHT_OPEN) {
				floatContent.changeState(RIGHT_HIDE);
				x = metrics.widthPixels - floatContent.getFloatContentWidth();
			}
			y = floatLogo.getLogoViewParams().y;
	/*		floatContent.setViewParam(x, y);
			floatContent.showContent();*/
			Intent intent=new Intent();
			intent.setClass((Activity)context, GameSliderBarActivityV2.class);

			context.startActivity(intent);
            if (((Activity)context).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ((Activity)context). overridePendingTransition(QGSdkUtils.getResId(context,"R.anim.slide_bar_in"),QGSdkUtils.getResId(context,"R.anim.slide_bar_out"));
            } else {
                ((Activity)context).overridePendingTransition(QGSdkUtils.getResId(context,"R.anim.slide_bar_in_bottom"),QGSdkUtils.getResId(context,"R.anim.slide_bar_out_bottom"));
            }
		}
	}




}
