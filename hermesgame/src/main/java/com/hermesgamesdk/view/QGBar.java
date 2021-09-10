package com.hermesgamesdk.view;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

/**
 * QGBar 进度条被创建时就显示动画效果
 * 
 */

// 所有控件尺寸改为dp单位，下一个版本更新
public class QGBar extends View {
	// 进度条实时转动角度
	private float mRotateAngle;
	private int mDuration;
	private int mWidth;
	private int mHeight;
	// 绘制的圆弧弧度
	private float mArcLength;
	private Paint mPaint;
	private int mColor;
	private int mArcWidth;
	private RectF mRectF;
	private ObjectAnimator mAnimaion;

	/**
	 * @param context
	 *            在构造函数创建画笔 创建属性动画并启动
	 */

	public QGBar(Context context) {
		this(context, null);
	}

	public QGBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		mWidth = mHeight = (int) ((context.getResources().getDisplayMetrics().density) * 40);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(mWidth, mHeight);
		fl.gravity = Gravity.CENTER;
		setLayoutParams(fl);

		mDuration = 2000;
		mColor = 0xFF09A498;

		mPaint = new Paint();
		mArcWidth = 8;

		mPaint.setAntiAlias(true);
		mPaint.setColor(mColor);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(mArcWidth);

		mAnimaion = new ObjectAnimator();
		// 圆弧弧度由0->270->0规律无限循环
		mAnimaion.setIntValues(0, 270, 0);
		
		mAnimaion.setEvaluator(new TypeEvaluator<Integer>() {

			@Override
			public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
				int num = endValue - startValue;

				mArcLength = num >= 0 ? (endValue - startValue) * fraction : 270 - (startValue - endValue) * fraction;
				mRotateAngle = 360 * fraction;
				mRectF = new RectF(mArcWidth, mArcWidth, mWidth - mArcWidth * 2, mHeight - mArcWidth * 2);

				// 循环更新mArcLength，mRotateAngle
				// 创建矩形对象 mRectF
				// 通知onDraw重绘
				invalidate();
				return endValue;
			}

		});
		// 每个动画周期时间
		mAnimaion.setDuration(mDuration);
		// 无限循环
		mAnimaion.setRepeatCount(Animation.INFINITE);
		// 插值器,动画进度线性变化
		mAnimaion.setInterpolator(new LinearInterpolator());
		mAnimaion.setTarget(this);
		mAnimaion.start();
	}

	protected void onDraw(Canvas canvas) {
		canvas.drawArc(mRectF, mRotateAngle, mArcLength, false, mPaint);

		super.onDraw(canvas);
	}

}
