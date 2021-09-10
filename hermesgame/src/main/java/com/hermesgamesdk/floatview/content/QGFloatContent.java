package com.hermesgamesdk.floatview.content;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.hermesgamesdk.floatview.ScaleBitmapFactory;
import com.hermesgamesdk.floatview.anim.QGAnimator;
import com.hermesgamesdk.floatview.anim.QGAnimatorListenerAdapter;
import com.hermesgamesdk.floatview.anim.QGValueAnimator;
import com.hermesgamesdk.utils.QGSdkUtils;

/**
 * 悬浮菜单内容
 */
public class QGFloatContent extends View implements OnTouchListener {

	public static final String TAG = "QGFloatContent";

	private int changeStateDelayTime = 0;
	private int animDuration = 500; // 动画时间，毫秒

	private Context context;
	private WindowManager wm;
	private WindowManager.LayoutParams contentViewParam;
	private DisplayMetrics metrics;

	private QGContentState contentState = QGContentState.UNDEFINE;
	private QGContentState contentStateOnDraw = QGContentState.UNDEFINE;

	private int logoWidth;
	private int logoHeight;

	private Paint paint;
	private Paint bgPaint;
	private Rect clipRect;
	private RectF bgRect;

	private OnContentActionListener onContentActionListener;
	private OnContentAnimListener onContentAnimListener;
	private OnStateChangeListener onStateChangeListener;

	private QGContent content;

	private Timer timer;

	private int spaceToLogoX;// content内容到logo边界的距离
	private int spaceToEdgeX;// content内容到背景边界的距离
	private int shiftToParent;

	public QGFloatContent(Context context, DisplayMetrics metrics, WindowManager wm) {
		super(context);

		setWillNotDraw(false);

		this.context = context;
		this.metrics = metrics;
		this.wm = wm;

		initParams();
		initWH();
		initPaint();

		content = new QGContent(context);
		content.setIntervalPercent(0.4);

		timer = new Timer();

		setOnTouchListener(this);
	}

	private void initPaint() {
		paint = new Paint();
		bgPaint = new Paint();
		bgPaint.setColor(Color.WHITE); // 设置画笔颜色
		bgPaint.setStyle(Paint.Style.FILL);// 设置填充样式
		bgPaint.setStrokeWidth(1);// 设置画笔宽度
		bgPaint.setAntiAlias(true);
	}

	private void initWH() {
		Bitmap logo = ScaleBitmapFactory.decodeResource(context.getResources(),
				QGSdkUtils.getResId(context, "R.drawable.qg_float_view_logo"));
		logoWidth = logo.getWidth();
		logoHeight = logo.getHeight();
		logo.recycle();
		// 以下3项暂不修改，否则出错
		spaceToLogoX = (int) (logoWidth * 0.25);
		spaceToEdgeX = (int) (logoWidth * 0.5);
		shiftToParent = (int) (logoWidth * 0.5);
	}

	public void initParams() {
		contentViewParam = new WindowManager.LayoutParams();

		//contentViewParam.type = WindowManager.LayoutParams.TYPE_PHONE;
		contentViewParam.format = 1;
		contentViewParam.flags |= 8;
		contentViewParam.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;


		contentViewParam.gravity = Gravity.LEFT | Gravity.TOP;

		contentViewParam.x = 0;
		contentViewParam.y = 80;

		contentViewParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		contentViewParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;

	}

	/**
	 * 添加单元并设置点击事件
	 * 
	 * @param imgId
	 * @param listener
	 */
	public void addContent(int imgId, OnClickListener listener) {
		content.addUnit(imgId, listener);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawRoundRect(bgRect, bgRect.height() / 2, bgRect.height() / 2, bgPaint);

		canvas.clipRect(clipRect);

		switch (contentStateOnDraw) {
			case ANIM_LEFT_SHOWING:
				canvas.translate(clipRect.right - content.getWidth(), 0);
				break;
			case ANIM_RIGHT_SHOWING:
				canvas.translate((getFloatContentWidth() - shiftToParent) - clipRect.width(), 0);
				break;
			case ANIM_LEFT_HIDDEN:
				canvas.translate(clipRect.right - content.getWidth(), 0);
				break;
			case ANIM_RIGHT_HIDDEN:
				canvas.translate(clipRect.left, 0);
				break;
			default:
				break;
		}

		for (QGUnit unit : content) {
			canvas.save();
			canvas.translate(unit.getX(), (logoHeight - unit.getHeight()) / 2);
			canvas.drawBitmap(unit.getBitmap(), unit.getSrcRect(), unit.getDstRect(), paint);
			canvas.restore();
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getFloatContentWidth(), getFloatContentHeight());
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

	}

	public int getFloatContentWidth() {
		return logoWidth + spaceToLogoX + content.getWidth() + spaceToEdgeX;
	}

	public int getFloatContentHeight() {
		return logoHeight;
	}

	/**
	 * 延迟更新状态，保证onDraw能够正常执行
	 * 
	 * @param state
	 * @param delay
	 */
	private void changeStateDelay(final QGContentState state, int delay) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				changeState(state);
				if (state == QGContentState.LEFT_HIDE || state == QGContentState.RIGHT_HIDE) {
					((Activity) context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setVisibility(GONE);
						}
					});
				}
			}
		}, delay);
	}

	/**
	 * 显示动画
	 */
	public synchronized void showContent() {
		switch (contentState) {
			case LEFT_HIDE:
				showContentFromLeft();
				break;
			case RIGHT_HIDE:
				showContentFromRight();
				break;
			default:
				break;
		}
	}

	/**
	 * 显示动画，在左侧向右侧移动
	 */
	public void showContentFromLeft() {
		Log.d(TAG, "showContentFromLeft");
		bgRect = new RectF(0, 0, logoWidth, logoHeight);
		clipRect = new Rect(shiftToParent, 0, logoWidth + spaceToLogoX + content.getWidth(), logoHeight);
		QGValueAnimator animator = QGValueAnimator.ofInt(0, clipRect.width());
		animator.setDuration(animDuration);
		animator.addUpdateListener(new QGValueAnimator.QGAnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(QGValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				bgRect.right = bgRect.left + logoWidth + value;
				clipRect.right = clipRect.left + value;
				contentStateOnDraw = contentState;
				invalidate();
			}
		});
		animator.addListener(new QGAnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(QGAnimator animation) {
				super.onAnimationEnd(animation);
				changeStateDelay(QGContentState.LEFT_SHOW, changeStateDelayTime);
				if (onContentAnimListener != null)
					onContentAnimListener.onAnimateEnd();
			}

			@Override
			public void onAnimationStart(QGAnimator animation) {
				super.onAnimationStart(animation);
				changeState(QGContentState.ANIM_LEFT_SHOWING);
			}
		});
		animator.start();
	}

	/**
	 * 显示动画，在右侧向左侧移动
	 */
	public void showContentFromRight() {
		Log.d(TAG, "showContentFromRight");
		bgRect = new RectF(spaceToEdgeX + content.getWidth() + spaceToLogoX, 0, getFloatContentWidth(), logoHeight);
		clipRect = new Rect(spaceToEdgeX, 0, getFloatContentWidth() - shiftToParent, logoHeight);
		QGValueAnimator animator = QGValueAnimator.ofInt(0, clipRect.width());
		animator.setDuration(animDuration);
		animator.addUpdateListener(new QGValueAnimator.QGAnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(QGValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				bgRect.left = bgRect.right - logoWidth - value;
				clipRect.left = clipRect.right - value;
				contentStateOnDraw = contentState;
				invalidate();
			}
		});
		animator.addListener(new QGAnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(QGAnimator animation) {
				super.onAnimationEnd(animation);
				changeStateDelay(QGContentState.RIGHT_SHOW, changeStateDelayTime);
				if (onContentAnimListener != null)
					onContentAnimListener.onAnimateEnd();
			}

			@Override
			public void onAnimationStart(QGAnimator animation) {
				super.onAnimationStart(animation);
				changeState(QGContentState.ANIM_RIGHT_SHOWING);

			}
		});
		animator.start();
	}

	/**
	 * 隐藏动画
	 */
	public synchronized void hideContent() {
		switch (contentState) {
			case LEFT_SHOW:
				hideContentToLeft();
				break;
			case RIGHT_SHOW:
				hideContentToRight();
				break;
			default:
				break;
		}

	}

	/**
	 * 隐藏动画，在左侧向左移动
	 */
	private void hideContentToLeft() {
		Log.d(TAG, "hideContentToLeft");
		bgRect = new RectF(0, 0, getFloatContentWidth(), logoHeight);
		clipRect = new Rect(shiftToParent, 0, logoWidth + spaceToLogoX + content.getWidth(), logoHeight);
		QGValueAnimator animator = QGValueAnimator.ofInt(0, clipRect.width());
		animator.setDuration(animDuration);
		animator.addUpdateListener(new QGValueAnimator.QGAnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(QGValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				bgRect.right = getFloatContentWidth() - value;
				clipRect.right = (int) (bgRect.right - spaceToEdgeX);
				contentStateOnDraw = contentState;
				invalidate();
			}
		});
		animator.addListener(new QGAnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(QGAnimator animation) {
				super.onAnimationEnd(animation);
				changeStateDelay(QGContentState.LEFT_HIDE, changeStateDelayTime);
				if (onContentAnimListener != null)
					onContentAnimListener.onAnimateEnd();
			}

			@Override
			public void onAnimationStart(QGAnimator animation) {
				super.onAnimationStart(animation);
				changeState(QGContentState.ANIM_LEFT_HIDDEN);

			}
		});
		animator.start();
	}

	/**
	 * 隐藏动画，在右向右侧移动
	 */
	public void hideContentToRight() {
		Log.d(TAG, "hideContentToRight");
		bgRect = new RectF(getFloatContentWidth() - logoWidth, 0, getFloatContentWidth(), logoHeight);
		clipRect = new Rect(spaceToEdgeX, 0, getFloatContentWidth() - shiftToParent, logoHeight);
		QGValueAnimator animator = QGValueAnimator.ofInt(0, clipRect.width());
		animator.setDuration(animDuration);
		animator.addUpdateListener(new QGValueAnimator.QGAnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(QGValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				bgRect.left = value;
				clipRect.left = logoWidth / 2 + value;
				contentStateOnDraw = contentState;
				invalidate();
			}
		});
		animator.addListener(new QGAnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(QGAnimator animation) {
				super.onAnimationEnd(animation);
				changeStateDelay(QGContentState.RIGHT_HIDE, changeStateDelayTime);
				if (onContentAnimListener != null)
					onContentAnimListener.onAnimateEnd();
			}

			@Override
			public void onAnimationStart(QGAnimator animation) {
				super.onAnimationStart(animation);
				changeState(QGContentState.ANIM_RIGHT_HIDDEN);
			}
		});
		animator.start();
	}

	private QGUnit touchedUnit = null; // 被点击的单元

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Log.d(TAG, "onTouch");
		float x = event.getX();
		float y = event.getY();

		// Log.d(TAG, "x=" + x + ", y=" + y);

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// Log.d(TAG, "ACTION_DOWN");
				switch (contentState) {
					case LEFT_SHOW:
						touchedUnit = content.getTouchedUnit((int) x, (int) y, logoWidth + spaceToLogoX, 0);
						break;
					case RIGHT_SHOW:
						touchedUnit = content.getTouchedUnit((int) x, (int) y, logoWidth / 2, 0);
						break;
					default:
						break;
				}
				if (touchedUnit != null) {
					touchedUnit.setTouchValid(true);
					// Log.d(TAG, "touchedUnit = " + touchedUnit.toString());
				}
				if (onContentActionListener != null) {
					onContentActionListener.onTouchValid(true);
				}
				// 支持接触时改变图标才做此处修改
				// invalidate();
				break;
			case MotionEvent.ACTION_MOVE:

				// Log.d(TAG, "ACTION_MOVE");
				if (touchedUnit == null || !touchedUnit.isTouchValid()) {
					touchedUnit = null;
					break;
				}

				// Log.d(TAG, "touchedUnit = " + touchedUnit.toString());
				int touchUnitShiftX = contentState == QGContentState.LEFT_SHOW ? logoWidth + spaceToLogoX
						: contentState == QGContentState.RIGHT_SHOW ? logoWidth / 2 : 0;
				if (touchedUnit.containExtend(x - touchUnitShiftX, y, content.getLengthOfClickAreaExtend())) {
					touchedUnit.setTouchValid(true);
				}
				// 已移动出单元区域
				else {
					touchedUnit.setTouchValid(false);
				}
				break;
			case MotionEvent.ACTION_UP:
				// Log.d(TAG, "ACTION_UP");
				// 点击外部区域
				if (touchedUnit == null || !touchedUnit.isTouchValid()) {
					touchedUnit = null;
					if (onContentActionListener != null) {
						onContentActionListener.onClickValid(false);
					}
					break;
				}
				// Log.d(TAG, "touchedUnit = " + touchedUnit.toString());
				int touchUnitShiftX2 = contentState == QGContentState.LEFT_SHOW ? logoWidth + spaceToLogoX
						: contentState == QGContentState.RIGHT_SHOW ? logoWidth / 2 : 0;
				// 确认点击了内部区域
				if (touchedUnit.containExtend(x - touchUnitShiftX2, y, content.getLengthOfClickAreaExtend())) {
					if (onContentActionListener != null) {
						onContentActionListener.onTouchValid(true);
					}
					// 点击事件
					if (touchedUnit.isTouchValid()) {
						// Log.d(TAG, "isClicked");
						setVisibility(GONE);
						if (contentState == QGContentState.LEFT_SHOW) {
							changeState(QGContentState.LEFT_HIDE);
						}
						else if (contentState == QGContentState.RIGHT_SHOW) {
							changeState(QGContentState.RIGHT_HIDE);
						}
						// 单元点击监听事件回调
						touchedUnit.getListener().onClick(null);
						if (onContentActionListener != null) {
							onContentActionListener.onClickValid(true);
						}

					}
				}
				else {
					if (onContentActionListener != null) {
						onContentActionListener.onClickValid(false);
					}
				}
				// 重置
				touchedUnit = null;
				break;
			default:
				break;
		}
		return true;

	}

	public void addFloatView() {
		
		if (getParent() == null) {
			setVisibility(View.GONE);
			wm.addView(this, contentViewParam);
		}
	}

	public void removeFloatView() {
		if (getParent() != null) {
			setVisibility(View.GONE);
			wm.removeView(this);
		}
	}

	public void updateViewLayout() {
		wm.updateViewLayout(this, contentViewParam);
	}

	public void show() {
		if (getParent() != null) {
			setVisibility(View.GONE);
		}
	}

	public void hide() {
		if (getParent() != null) {
			setVisibility(View.GONE);
		}
	}

	public void setViewParam(int x, int y) {
		contentViewParam.x = x;
		contentViewParam.y = y;
		setVisibility(View.VISIBLE);
		updateViewLayout();
	}

	public void setAnimDuration(int duration) {
		animDuration = duration;
	}

	/**
	 * 更新位置
	 */
	public void updateContentLocation(Point location) {
		contentState = location.x < metrics.widthPixels ? QGContentState.LEFT_HIDE : QGContentState.RIGHT_HIDE;
	}

	/**
	 * 状态改变事件回调
	 */
	public void setContentStateChangeListener(OnStateChangeListener listener) {
		onStateChangeListener = listener;
	}

	public interface OnStateChangeListener {
		void onStateChange(QGContentState state);
	}

	/**
	 * 动画事件回调
	 */
	public interface OnContentAnimListener {
		void onAnimteStart();

		void onAnimateEnd();
	}

	public void setContentAnimListener(OnContentAnimListener contentAnimListener) {
		this.onContentAnimListener = contentAnimListener;
	}

	/**
	 * 触摸和点击事件回调
	 */
	public interface OnContentActionListener {
		void onTouchValid(boolean valid);

		void onClickValid(boolean valid);
	}

	public void setContentActionListener(OnContentActionListener onContentActionListener) {
		this.onContentActionListener = onContentActionListener;
	}

	/**
	 * 修改状态
	 * 
	 * @param state
	 */
	public void changeState(QGContentState state) {
		this.contentState = state;
		if (onStateChangeListener != null) {
			onStateChangeListener.onStateChange(state);
		}
	}

	public QGContentState getContentState() {
		return contentState;
	}

}
