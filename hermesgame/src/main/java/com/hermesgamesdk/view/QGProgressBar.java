package com.hermesgamesdk.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import android.widget.FrameLayout;

/**
 * 进度条控件，主要用于网络访问时不能操作界面，避免多次点击按钮重复网络请求
 * 
 */


public class QGProgressBar {
	private static QGProgressBar mQgProgressBar = new QGProgressBar();
	// DecorView
	private ViewGroup mVP;
	// 进度条控件
	private QGContainer mBar;

	// 默认mBar没有被添加至mVP

	private boolean mIsAdd = false;

	// 默认下次show有效
	private boolean mDisable = false;

	private QGProgressBar() {
	}

	/**
	 * 调用一次后，下次show()不会显示进度条 mDisable为true，表示当前显示失效，并将mDisable置为false
	 * 使下次调用show时生效
	 */
	public static void disable() {
		mQgProgressBar.mDisable = true;
	}

	/**
	 * @param context
	 *            将进度条控件QGContainer添加至DecorView显示出来，
	 *            mIsAdd为false时mBar被添加至QGContainer
	 *            mIsAdd置为true表示控件已被添加至DecorView
	 */
	public static void show(Activity context) {

		if (mQgProgressBar.mDisable) {
			mQgProgressBar.mDisable = false;
			return;
		}

		if (mQgProgressBar.mBar == null)
			mQgProgressBar.mBar = new QGContainer(context);

		if (!mQgProgressBar.mIsAdd) {
			mQgProgressBar.mVP = (ViewGroup) context.getWindow().getDecorView();
			mQgProgressBar.mVP.addView(mQgProgressBar.mBar);
		}
		mQgProgressBar.mIsAdd = true;
	}

	/**
	 * 隐藏进度条 mIsAdd为true时候移除mBar，并将mIsAdd置为false表示已经被移除
	 */
	public static void hide() {
		if (mQgProgressBar.mIsAdd)
			mQgProgressBar.mVP.removeView(mQgProgressBar.mBar);
		mQgProgressBar.mIsAdd = false;
	}

	/**
	 * QGBar 被添加至 QGContainer 屏蔽QGContainer 的 onTouchEvent事件，使点击事件失效
	 * 
	 */
	static class QGContainer extends FrameLayout {

		public QGContainer(Context context, AttributeSet attrs) {
			super(context, attrs);

		}

		public QGContainer(Context context) {
			super(context);
			LayoutParams fl1 = new LayoutParams(-1, -1);
			setLayoutParams(fl1);
			QGBar bar = new QGBar(context);
			addView(bar);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			return true;
		}

	}

}
