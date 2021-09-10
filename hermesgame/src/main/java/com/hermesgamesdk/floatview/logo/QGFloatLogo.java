package com.hermesgamesdk.floatview.logo;

import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_LEFT_EDGE;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_LEFT_HIDE;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_LEFT_OPEN;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_MOVING;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_RIGHT_EDGE;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_RIGHT_HIDE;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_RIGHT_OPEN;
import static com.hermesgamesdk.floatview.logo.QGLogoState.LOGO_UNDEFINE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.hermesgamesdk.floatview.ScaleBitmapFactory;
import com.hermesgamesdk.manager.InitManager;

/**
 * 悬浮窗图标logo
 */
public class QGFloatLogo {

	private static final String TAG = "QGFloatLogo";

	QGLogoState logoState = LOGO_UNDEFINE; // 初始化状态

	private Context context;
	private WindowManager wm;
	private DisplayMetrics metrics;
	private LayoutParams logoViewParams;

	private ImageView logoView;

	public int logoHeight;
	public int logoWidth;
	public int logoHalfWidth;
	public int logoHalfHeight;
	public int logoHeightOffset;
	public int logoWidthOffset;
	public int screenWidth;//metrics.widthPixels  screenWidth
	public int screenHigth;

	public enum LogoView {
		MAIN, LELT, RIGHT
	}

	public QGFloatLogo(Context context, DisplayMetrics metrics, WindowManager wm) {
		this.context = context;
		this.metrics = metrics;
		this.wm = wm;
		init();
		
	}

	@SuppressLint("NewApi") private void init() {
		initWH();
		initLogoView();
		initLogoViewParams();
		WindowManager windowManager = (WindowManager) ((Activity) context).getApplication().getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();
		Point outPoint = new Point();

		if (Build.VERSION.SDK_INT >= 19) {
			// 可能有虚拟按键的情况

			display.getRealSize(outPoint);
		} else {
			// 不可能有虚拟按键

			display.getSize(outPoint);
		}

		screenWidth = outPoint.x;
		screenHigth = outPoint.y;


	}

	private void initWH() {
		// LOGO
		Bitmap logo = ScaleBitmapFactory.decodeResource(context.getResources(), getResId("qg_float_view_logo", "drawable"));
		logoHeight = logo.getHeight();
		logoWidth = logo.getWidth();
		logo.recycle();
		// LOGO半图
		Bitmap logo2 = ScaleBitmapFactory.decodeResource(context.getResources(), getResId("qg_float_view_logo_half_right", "drawable"));
		logoHalfHeight = logo2.getHeight();
		logoHalfWidth = logo2.getWidth();
		logo2.recycle();
		// 差值
		logoWidthOffset = (logoHeight - logoHalfWidth) / 2;
		logoHeightOffset = (logoHeight - logoHalfHeight) / 2;
	}

	private void initLogoView() {

		logoView = new ImageView(context);
		logoView.setScaleType(ScaleType.CENTER_CROP);
		setImageView(LogoView.MAIN);

		// ViewGroup.LayoutParams lp = logoView.getLayoutParams();
		// lp.width = logoWidth;
		// lp.height = logoHeight;
		// logoView.setLayoutParams(lp);

		logoView.setClickable(true);
		logoView.setEnabled(true);


		logoView.setOnTouchListener(new LogoTouchListener());
		logoView.setOnClickListener(new LogoClickListener());

	}

	private void setImageView(LogoView view) {

		switch (view) {
		case MAIN:
			if (InitManager.getInstance().useCPFloatLogo&&!InitManager.getInstance().cpFloatLogPath.equals("")){
				Uri uri = Uri.parse(InitManager.getInstance().cpFloatLogPath);
				logoView.setImageURI(uri);
			}else{
				logoView.setImageResource(getResId("qg_float_view_logo", "drawable"));
			}

			logoView.setLayoutParams(new ViewGroup.LayoutParams(logoWidth, logoHeight));
			break;
		case LELT:
			if (InitManager.getInstance().useCPFloatLogo&&!InitManager.getInstance().cpFloatLogPath.equals("")) {
				BitmapFactory.Options o= new BitmapFactory.Options();
				Bitmap logBitmap = BitmapFactory.decodeFile(InitManager.getInstance().cpFloatLogPath,o );
				Bitmap logRirhtBitmap = Bitmap.createBitmap(logBitmap, logBitmap.getWidth() / 2, 0, logBitmap.getWidth() / 2, logBitmap.getHeight());
				logoView.setImageBitmap(logRirhtBitmap);
			} else {
				logoView.setImageResource(getResId("qg_float_view_logo_half_left", "drawable"));
			}

			logoView.setLayoutParams(new ViewGroup.LayoutParams(logoHalfWidth, logoHalfHeight));
			break;
		case RIGHT:
			if (InitManager.getInstance().useCPFloatLogo&&!InitManager.getInstance().cpFloatLogPath.equals("")) {
				BitmapFactory.Options o= new BitmapFactory.Options();

				Bitmap logBitmap = BitmapFactory.decodeFile(InitManager.getInstance().cpFloatLogPath,o );
				Bitmap logLeftBitmap = Bitmap.createBitmap(logBitmap, 0, 0, logBitmap.getWidth() / 2, logBitmap.getHeight());
				logoView.setImageBitmap(logLeftBitmap);
			} else {
				logoView.setImageResource(getResId("qg_float_view_logo_half_right", "drawable"));
			}

			logoView.setLayoutParams(new ViewGroup.LayoutParams(logoHalfWidth, logoHalfHeight));
			break;
		}

	}

	private void initLogoViewParams( ) {

		logoViewParams = new LayoutParams();

		// logoViewParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
		logoViewParams.format = 1;// PixelFormat.RGBA_8888
		logoViewParams.flags |= 8;// WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		logoViewParams.gravity = Gravity.LEFT | Gravity.TOP;

		logoViewParams.width = logoHeight;
		logoViewParams.height = logoHeight;

	}

	/**
	 * 更新位置
	 */
	public void updateLogoLocation(Point location) {
		logoViewParams.x = location.x;
		logoViewParams.y = location.y;
		logoState = logoViewParams.x < screenWidth / 2 ? LOGO_LEFT_OPEN : LOGO_RIGHT_OPEN;
		if (logoView.getParent() != null) {
			updateLogoParams();
		}
	}

	public void addFloatView() {
		if (logoView.getParent() == null) {
			logoView.setVisibility(View.GONE);
			wm.addView(logoView, logoViewParams);
		}
	}

	public void removeFloatView() {
		if (logoView.getParent() != null) {
			logoView.setVisibility(View.GONE);
			wm.removeView(logoView);
		}
	}

	/**
	 * 更新
	 */
	public void updateLogoParams() {
		if (logoView != null && logoView.getVisibility() != View.GONE) {
			wm.updateViewLayout(logoView, logoViewParams);
		}
	}

	/**
	 * 触摸和点击是否有效
	 */
	boolean isTouchValid = true;
	boolean isClickValid = true;

	public boolean isTouchValid() {
		return isTouchValid;
	}

	public void setTouchValid(boolean touchValid) {
		isTouchValid = touchValid;
	}

	public boolean isClickValid() {
		return isClickValid;
	}

	public void setClickValid(boolean clickValid) {
		isClickValid = clickValid;
	}

	/**
	 * 设置Logo，在屏幕左边
	 */
	private void setLogoAtLeft() {
		if (logoView != null) {
			setImageView(LogoView.MAIN);
		}
		logoViewParams.y = logoViewParams.y + logoHeightOffset;
		logoViewParams.width = logoWidth;
		logoViewParams.height = logoHeight;
		updateLogoParams();
	}

	/**
	 * 设置Logo，在屏幕右边
	 */
	private void setLogoAtRight() {
		if (logoView != null) {
			setImageView(LogoView.MAIN);
		}
		logoViewParams.x = screenWidth - logoWidth;
		logoViewParams.y = logoViewParams.y + logoHeightOffset;
		logoViewParams.width = logoWidth;
		logoViewParams.height = logoHeight;

		updateLogoParams();
	}

	/**
	 * 设置LogoHalf，在屏幕左边
	 */
	public void setLogoHalfAtLeft() {
		if (logoView != null) {
			setImageView(LogoView.LELT);
		}
		logoViewParams.y = logoViewParams.y + logoHeightOffset;
		logoViewParams.width = logoHalfWidth;
		logoViewParams.height = logoHalfHeight;
		changeLogoState(LOGO_LEFT_HIDE);
		updateLogoParams();
	}

	/**
	 * 设置LogoHalf，在屏幕右边
	 */
	public void setLogoHalfAtRight() {
		if (logoView != null) {
			setImageView(LogoView.RIGHT);
		}
		logoViewParams.x = logoViewParams.x + logoWidthOffset;
		logoViewParams.y = logoViewParams.y + logoHeightOffset;
		logoViewParams.width = logoHalfWidth;
		logoViewParams.height = logoHalfHeight;

		updateLogoParams();
		changeLogoState(LOGO_RIGHT_HIDE);
	}



	public QGLogoState getLogoState() {
		return logoState;
	}

	public ImageView getLogoView() {
		return logoView;
	}

	public LayoutParams getLogoViewParams() {
		return logoViewParams;
	}

	public void setTransparent() {
		if (logoView != null && logoView.getVisibility() != View.GONE) {
			logoView.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
	}

	public void hide() {
		logoView.setVisibility(View.GONE);
	}

	public interface ShowCallback {
		void complete();
	}

	public void show(ShowCallback callback) {
		if (logoView.getParent() != null) {
			if (logoView.getVisibility() != View.VISIBLE) {
				logoView.setVisibility(View.VISIBLE);
				if (logoState == LOGO_LEFT_HIDE || logoState == LOGO_LEFT_EDGE || logoState == LOGO_LEFT_OPEN) {
					changeLogoState(LOGO_LEFT_OPEN);
				} else if (logoState == LOGO_RIGHT_OPEN || logoState == LOGO_RIGHT_EDGE) {
					changeLogoState(LOGO_RIGHT_OPEN);
				}
			}
			callback.complete();
		}
	}

	/**
	 * 触摸点击监听
	 */
	public interface LogoActionListener {
		void onTouchValid(MotionEvent v); // 接触有效

		void onClickValid(View v);
	}

	/**
	 * 动画监听
	 */
	public interface LogoAnimListener {
		void onAnimatetart();

		void onAnimateEnd();
	}

	private LogoActionListener logoActionListener;
	private LogoAnimListener logoAnimListener;

	public void setLogoActionListener(LogoActionListener logoActionListener) {
		this.logoActionListener = logoActionListener;
	}

	public void setLogoAnimListener(LogoAnimListener logoAnimListener) {
		this.logoAnimListener = logoAnimListener;
	}

	private class LogoTouchListener implements OnTouchListener {
		int totalDistance = 0;
		int offsetX = 0;
		int offsetY = 0;

		float touchStartX;
		float touchStartY;

		int compensateY = 0;

		@Override
		public boolean onTouch(View v, MotionEvent event) {



			// 是否可接触
			if (!isTouchValid()) {
				return false;
			}

			// Log.d(TAG, "onTouch logoState=" + logoState);

			if (logoState == LOGO_LEFT_HIDE || logoState == LOGO_RIGHT_HIDE || logoState == LOGO_LEFT_EDGE || logoState == LOGO_RIGHT_EDGE)
				changeLogoState(LOGO_MOVING);

			logoActionListener.onTouchValid(event);

			boolean result = false;

			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				// Log.d(TAG, "onTouch ACTION_DOWN" + "  [" + event.getRawX() +
				// ", " + event.getRawY() + "]");

				totalDistance = 0;
				touchStartX = event.getRawX();
				touchStartY = event.getRawY();
				offsetX = (int) event.getX();
				offsetY = (int) event.getY();

				// 获取图标y坐标，保证值y>=0
				int iconY = Math.max(logoViewParams.y, 0);
				// 获取y坐标轴偏移的修正量
				compensateY = (int) (touchStartY - offsetY - iconY);

				break;

			case MotionEvent.ACTION_MOVE:
				// Log.d(TAG, "onTouch ACTION_MOVE" + "  [" + event.getRawX() +
				// ", " + event.getRawY() + "]");

				float xDistance = event.getRawX() - touchStartX;
				float yDistance = event.getRawY() - touchStartY;
				float distance = xDistance * xDistance + yDistance * yDistance;
				totalDistance += Math.sqrt(distance);

				touchStartX = event.getRawX();
				touchStartY = event.getRawY();

				logoViewParams.x = (int) (event.getRawX() - offsetX);
				logoViewParams.y = (int) (event.getRawY() - offsetY - compensateY);

				updateLogoParams();

				break;

			case MotionEvent.ACTION_UP:
				// Log.d(TAG, "onTouch ACTION_UP" + "  [" + event.getRawX() +
				// ", " + event.getRawY() + "]");

				// 大于边界条件5则认为是滑动事件，消耗该事件，不执行点击操作
				if (totalDistance > 20) {
					result = true;
				}

				totalDistance = 0;
				touchStartX = 0;
				touchStartY = 0;
				offsetX = 0;
				offsetY = 0;

				animateToEdge_L(logoViewParams.x);

				// 点击在此处执行？
				if (!result) {
					if (logoViewParams.x < screenWidth / 2) {
						changeLogoState(LOGO_LEFT_OPEN);
					} else {
						changeLogoState(LOGO_RIGHT_OPEN);
					}
				}

			}
			return result;
		}
	}

	private long lastClick;

	private class LogoClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {


			// 点击事件是否有效
			if (!isClickValid()) {
				return;
			}
			// 间隔是否过短，防止快速点击执行2次
			if (System.currentTimeMillis() - lastClick < 50) {
				return;
			}
			lastClick = System.currentTimeMillis();

			// Log.d(TAG, "onClick logoState = " + logoState);

			switch (getLogoState()) {
			case LOGO_LEFT_HIDE:
			case LOGO_LEFT_EDGE:
				setTransparent();
				changeLogoState(LOGO_LEFT_OPEN);
				break;
			case LOGO_RIGHT_HIDE:
			case LOGO_RIGHT_EDGE:
				setTransparent();
				changeLogoState(LOGO_RIGHT_OPEN);
				break;
			case LOGO_LEFT_OPEN:
			case LOGO_RIGHT_OPEN:
				break;
			default:
				break;
			}

			logoActionListener.onClickValid(logoView);
		}
	}

	private void animateToEdge_L(int currentX) {

		if (currentX >= screenWidth / 2) {
			logoViewParams.x = screenWidth - logoHeight;
			changeLogoState(LOGO_RIGHT_OPEN);
		} else {
			logoViewParams.x = 0;
			changeLogoState(LOGO_LEFT_OPEN);
		}
		updateLogoParams();
		logoAnimListener.onAnimateEnd();
	}

	/**
	 * 改变状态
	 * 
	 * @param targetLogoState
	 */
	public void changeLogoState(QGLogoState targetLogoState) {

		Log.d(TAG, "changeLogoState logoState = " + logoState + ", targetLogoState = " + targetLogoState);

		switch (targetLogoState) {
		case LOGO_LEFT_HIDE:
			if (logoView != null && logoView.getVisibility() != View.GONE) {
				setImageView(LogoView.LELT);
			}
			logoViewParams.width = logoHalfWidth;
			logoViewParams.x = 0;
			if (Build.VERSION.SDK_INT >= 11) {
				if (logoView != null && logoView.getVisibility() != View.GONE) {
					logoView.setAlpha(0.8f);
				}
			}
			updateLogoParams();
			break;
		case LOGO_RIGHT_HIDE:
			if (logoView != null && logoView.getVisibility() != View.GONE) {
				setImageView(LogoView.RIGHT);
			}
			logoViewParams.width = logoHalfWidth;
			logoViewParams.x = screenWidth - logoHalfWidth;
			if (Build.VERSION.SDK_INT >= 11) {
				if (logoView != null && logoView.getVisibility() != View.GONE) {
					logoView.setAlpha(0.8f);
				}
			}
			updateLogoParams();
			break;
		case LOGO_LEFT_EDGE:
			if (logoView != null && logoView.getVisibility() != View.GONE) {
				setImageView(LogoView.LELT);
			}
			logoViewParams.width = logoHalfWidth;
			logoViewParams.x = 0;
			if (Build.VERSION.SDK_INT >= 11) {
				if (logoView != null && logoView.getVisibility() != View.GONE) {
					logoView.setAlpha(1f);
				}
			}
			updateLogoParams();
			break;
		case LOGO_RIGHT_EDGE:
			if (logoView != null && logoView.getVisibility() != View.GONE) {
				setImageView(LogoView.RIGHT);
			}
			logoViewParams.width = logoHalfWidth;
			logoViewParams.x = screenWidth - logoHalfWidth;
			if (Build.VERSION.SDK_INT >= 11) {
				if (logoView != null && logoView.getVisibility() != View.GONE) {
					logoView.setAlpha(1f);
				}
			}
			updateLogoParams();
			break;
		case LOGO_LEFT_OPEN:
			if (logoState == LOGO_LEFT_HIDE || logoState == LOGO_LEFT_EDGE) {
				logoViewParams.width = logoHeight;
				logoViewParams.height = logoHeight;
				logoViewParams.y = logoViewParams.y - logoHeightOffset;
				updateLogoParams();
				if (logoView != null && logoView.getVisibility() != View.GONE) {
					setImageView(LogoView.MAIN);
				}
			}
			if (logoView != null && logoView.getVisibility() != View.GONE) {
				setImageView(LogoView.MAIN);
			}

			if (Build.VERSION.SDK_INT >= 11) {
				if (logoView != null && logoView.getVisibility() != View.GONE) {
					logoView.setAlpha(1f);
				}
			}
			break;
		case LOGO_RIGHT_OPEN:
			if (logoState == LOGO_RIGHT_HIDE || logoState == LOGO_RIGHT_EDGE) {
				logoViewParams.width = logoHeight;
				logoViewParams.height = logoHeight;
				logoViewParams.x = screenWidth - logoHeight;
				logoViewParams.y = logoViewParams.y - logoHeightOffset;
				updateLogoParams();
				if (logoView != null && logoView.getVisibility() != View.GONE) {
					setImageView(LogoView.MAIN);
				}
			}
			if (logoView != null && logoView.getVisibility() != View.GONE) {
				setImageView(LogoView.MAIN);
			}
			if (Build.VERSION.SDK_INT >= 11) {
				if (logoView != null && logoView.getVisibility() != View.GONE) {
					logoView.setAlpha(1f);
				}
			}
			break;
		case LOGO_MOVING:
			if (logoView != null && logoView.getVisibility() != View.GONE) {
				setImageView(LogoView.MAIN);
				if (Build.VERSION.SDK_INT >= 11)
					logoView.setAlpha(1f);
				updateLogoParams();
			}
			if (logoState == LOGO_LEFT_HIDE || logoState == LOGO_LEFT_EDGE) {
				setLogoAtLeft();
			}
			if (logoState == LOGO_RIGHT_HIDE || logoState == LOGO_RIGHT_EDGE) {
				setLogoAtRight();
			}
		default:
			break;
		}

		logoState = targetLogoState;
	}

	public int getResId(String resName, String resType) {
		return context.getResources().getIdentifier(resName, resType, context.getPackageName());
	}
}
