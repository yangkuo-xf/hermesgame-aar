package com.hermesgamesdk.view;

import java.lang.reflect.Field;



import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hermesgamesdk.manager.QGFragmentManager;
import com.hermesgamesdk.utils.QGSdkUtils;


/**
 * 组合标题控件，便于多次复用
 *
 */
public class QGTitleBar extends RelativeLayout {
	//返回控件
	private ImageView mBackView;
	//关闭控件
	private ImageView mCloseView;
	//标题控件
	private TextView mTitleView;
	//标题文本
	private String mTitle;
	//标题颜色
	private int mTextColor;
	//标题大小
	private float mTextSize;
	//icon的大小
	private float mImageSize;
	//icon和控件的边距,增加点击面积
	private float mImagePadding;
	//QGTitleBar所在Activity
	private FragmentActivity mActivity;

	public QGTitleBar(Context context) {
		this(context, null);
	}
	public QGTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (context instanceof FragmentActivity) {
			mActivity = (FragmentActivity) context;
		}
		// 动态获取资源 styleable需要通个反射R文件获取资源id
		TypedArray ta = context.obtainStyledAttributes(attrs, (int[]) getResourceId(context, "QGTitleBar", "styleable"));
		mTitle = ta.getString((Integer) getResourceId(context, "QGTitleBar_title", "styleable"));
		mTextColor = ta.getColor((Integer) getResourceId(context, "QGTitleBar_textColor", "styleable"), 0xff333333);
		mTextSize = px2sp(context, ta.getDimension((Integer) getResourceId(context, "QGTitleBar_textSize", "styleable"), 25));
		mImageSize = ta.getDimension((Integer) getResourceId(context, "QGTitleBar_imageSize", "styleable"), 0);
		mImagePadding = ta.getDimension((Integer) getResourceId(context, "QGTitleBar_imagePadding", "styleable"), 0);
		ta.recycle();
		createView(context);
	}
	public QGTitleBar(Context context, AttributeSet attrs,int defStyleRes) {
		super(context, attrs,defStyleRes);
		if (context instanceof FragmentActivity) {
			mActivity = (FragmentActivity) context;
		}
		// 动态获取资源 styleable需要通个反射R文件获取资源id
		TypedArray ta = context.obtainStyledAttributes(attrs, (int[]) getResourceId(context, "QGTitleBar", "styleable"),0,defStyleRes);
		mTitle = ta.getString((Integer) getResourceId(context, "QGTitleBar_title", "styleable"));
		mTextColor = ta.getColor((Integer) getResourceId(context, "QGTitleBar_textColor", "styleable"),0xff333333);
		mTextSize = px2sp(context, ta.getDimension((Integer) getResourceId(context, "QGTitleBar_textSize", "styleable"), 25));
		mImageSize = ta.getDimension((Integer) getResourceId(context, "QGTitleBar_imageSize", "styleable"), 0);
		mImagePadding = ta.getDimension((Integer) getResourceId(context, "QGTitleBar_imagePadding", "styleable"), 0);
		ta.recycle();
		createView(context);
	}

	/**
	 * @param context
	 * 创建组合控件所需要的子控件，设置属性监听，最后添加到QGTitleBar控件容器中
	 */
	private void createView(Context context) {
		// 如果没有设置属性，为自适应
		int iconSize = (int) mImageSize;
		int iconPadding = (int) mImagePadding;
		// 创建返回键
		mBackView = new QGImageView(context);
		mBackView.setImageResource(QGSdkUtils.getResId(context, "R.drawable.qg_dialog_back"));
		LayoutParams rp1;
		if (iconSize == 0)
			rp1 = new LayoutParams(-2, -2);
		else
			rp1 = new LayoutParams(iconSize, iconSize);
		rp1.addRule(RelativeLayout.CENTER_VERTICAL);
		rp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rp1.leftMargin=24;
		mBackView.setLayoutParams(rp1);
		mBackView.setScaleType(ImageView.ScaleType.FIT_XY);
		if (iconPadding != 0)
			mBackView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
		// 创建关闭键
		mCloseView = new QGImageView(context);
		mCloseView.setImageResource(QGSdkUtils.getResId(context, "R.drawable.qg_dialog_close"));
		LayoutParams rp2;
		if (iconSize == 0)
			rp2 = new LayoutParams(-2, -2);
		else
			rp2 = new LayoutParams(iconSize, iconSize);

		rp2.addRule(RelativeLayout.CENTER_VERTICAL);
		rp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rp2.rightMargin=24;
		mCloseView.setLayoutParams(rp2);
		mCloseView.setScaleType(ImageView.ScaleType.FIT_XY);
		if (iconPadding != 0)
			mCloseView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
		// 创建标题
		mTitleView = new TextView(context);
		mTitleView.setText(mTitle);
		mTitleView.setTextSize(mTextSize);
		mTitleView.setTextColor(mTextColor);
		LayoutParams rp3 = new LayoutParams(-2, -2);
		rp3.addRule(RelativeLayout.CENTER_IN_PARENT);
		rp3.topMargin=10;
		mTitleView.setGravity(Gravity.CENTER);
		mTitleView.setLayoutParams(rp3);
		// 添加子控件
		addView(mBackView);
		addView(mTitleView);
		addView(mCloseView);

		// 设置监听
		mBackView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				back();
			}
		});
		mCloseView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				close();
			}
		});

	}

	/**
	 * 返回上一个显示界面
	 */
	public void back() {
		if (mActivity != null)
			QGFragmentManager.getInstance(mActivity).back();
	}

	/**
	 * 关闭当前控件所在的activity
	 * 
	 */
	public void close() {
		mActivity.finish();
	}
	/**
	 * 隐藏返回图标
	 */
	public void hideBackIcon() {
		mBackView.setVisibility(View.INVISIBLE);
	}
	public void openBackIcon() {
		mBackView.setVisibility(View.VISIBLE);
	}


	/**
	 * 隐藏关闭图标
	 */
	public void hideCloseIcon() {
		mCloseView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示返回图标
	 */
	public void showBackIcon() {
		mBackView.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示关闭图标
	 */
	public void showCloseIcon() {
		mCloseView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * @param title 标题
	 * 设置标题
	 */
	public void setTitle(String title) {
		this.mTitle = title;
		if (mTitleView != null)
			mTitleView.setText(mTitle);
	}

	/**
	 * @param textColor 文本颜色
	 * 设置文字颜色
	 */
	public void setTextColor(int textColor) {
		mTextColor = textColor;
		if (mTitleView != null)
			mTitleView.setTextColor(mTextColor);
	}

	/**
	 * @param textSize 文本大小
	 * 设置文字大小
	 */
	public void setTextSize(int textSize) {
		mTextSize = textSize;
		if (mTitleView != null)
			mTitleView.setTextSize(mTextSize);
	}

	/**
	 * @param ctx
	 * @param spValue  像素值
	 * @return 像素对应的sp
	 */
	public int px2sp(Context ctx, float pxValue) {

		final float fontScale = ctx.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.3f);

	}

	/**
	 * @param context 
	 * @param name 资源名称
	 * @param type 资源类型
	 * @return 资源Id
	 * 注意在Android Studio gradle中的 ApplicationId 必须和Manifest中 package属性值一致，不一致会造成读取的R文件路径和实际R文件路径不一致
	 */
	private Object getResourceId(Context context, String name, String type) {
		String className = context.getPackageName() + ".R";
		try {
			Class<?> cls = Class.forName(className);
			for (Class<?> childClass : cls.getClasses()) {
				String simple = childClass.getSimpleName();
				if (simple.equals(type)) {
					for (Field field : childClass.getFields()) {
						String fieldName = field.getName();
						if (fieldName.equals(name)) {
							System.out.println(fieldName);
							return field.get(null);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("exception", e.getCause().toString());
		}
		return null;
	}

	/**
	 * 覆写onTouchEvent，MotionEvent.ACTION_DOWN、MotionEvent.ACTION_UP改变控件的透明度
	 *
	 */
	class QGImageView extends ImageView {

		public QGImageView(Context context) {
			super(context);
		}

		public QGImageView(Context context,  AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				this.setAlpha(0.5f);

				break;
			case MotionEvent.ACTION_UP:
				this.setAlpha(1f);
				break;
			}
			return super.onTouchEvent(event);
		}
	}
	/**
	 * @param ctx
	 * @param spValue
	 * @return 像素值
	 */
	public int dp2px(Context ctx, float spValue) {

		final float fontScale = ctx.getResources().getDisplayMetrics().density;
		return (int) (spValue * fontScale + 0.5f);

	}

}
