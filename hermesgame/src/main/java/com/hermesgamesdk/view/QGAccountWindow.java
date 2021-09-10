package com.hermesgamesdk.view;

import com.hermesgamesdk.utils.QGSdkUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint("ViewConstructor") public class QGAccountWindow extends PopupWindow {
	private LinearLayout mRoot;
	private int mCount;
	private int mWidth;
	private ItemCickListenner mListenner;
	private LayoutInflater mInflater;
	public static final int TYPE_DELETE = 0;
	public static final int TYPE_TEXT = 1;

	/**
	 * @param activity	
	 *            
	 * @param view window显示在view正下方
	 * @param datas 显示到窗口中的文本数据集合          
	 */
	@SuppressWarnings("deprecation")
	public QGAccountWindow(Activity activity, View view, String[] datas) {
		super();
		mWidth = view.getWidth();
		mInflater = activity.getLayoutInflater();
		mRoot = (LinearLayout) mInflater.inflate(QGSdkUtils.getResId(activity,
				"R.layout.qg_window_more_account"), null, true);
		mCount = datas.length;
		
		for (int i = 0; i < mCount; i++) {
			final View item = getItem(activity, datas[i]);

			View[] childs = (View[]) item.getTag();
			TextView textView = (TextView) childs[0];
			ImageView deleteView = (ImageView) childs[1];

			deleteView.setTag(i);
			deleteView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View deleteView) {			
					if (mListenner != null) {
						int index = (Integer) deleteView.getTag();
						mListenner.onItemCick(item, mRoot, index, deleteView,
								TYPE_DELETE);
					}

				}
			});
			textView.setTag(i);
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View textView) {
					dismiss();
					if (mListenner != null) {
						int index = (Integer) textView.getTag();
						mListenner.onItemCick(item, mRoot, index, textView,
								TYPE_TEXT);
					}
				}
			});

			mRoot.addView(item);

		}

		setContentView(mRoot);
		//popwindow宽度与view一致
		setWidth(mWidth);
		setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		//设置此属性 点击popwindow之外，popwindow自动消失
		setBackgroundDrawable(new BitmapDrawable());

		setOutsideTouchable(true);
		setTouchable(true);
		setFocusable(true);
		//popwindow显示在view控件的正下方
		showAsDropDown(view);
		//showAtLocation(view, Gravity.CENTER,0,0);

	}

	/**
	 * @param activity
	 * @param text item文本
	 * @return inflate item布局
	 */
	private View getItem(Activity activity, String text) {
		View item = mInflater.inflate(
				QGSdkUtils.getResId(activity, "R.layout.qg_window_item"), null,
				true);

		TextView tv = (TextView) item.findViewById(QGSdkUtils.getResId(
				activity, "R.id.qg_item_account"));
		ImageView delete = (ImageView) item.findViewById(QGSdkUtils.getResId(
				activity, "R.id.qg_item_delete"));
		tv.setText(text);
		View[] childs = { tv, delete };
		item.setTag(childs);
		return item;
	}

	public static interface ItemCickListenner {
		/**
		 * @param item 被点击控件所属的单元
		 * @param parent window的根布局
		 * @param index item在根布局中的索引位置
		 * @param clickedView 被点击控件
		 * @param type	被点击控件类型
		 */
		void onItemCick(View item, ViewGroup parent, int index,
                        View clickedView, int type);
	}

	public void setOnItemCickListenner(ItemCickListenner listenner) {
		mListenner = listenner;
	}

	/**
	 * @param item 被移除的item
	 * 将窗口中的单元移除
	 */
	public void removeItem(View item) {
		mRoot.removeView(item);
	}

	/**
	 * @param index 被移除的itme索引
	 */
	public void removeItem(int index) {
		int childCount = mRoot.getChildCount();
		for (int i = 0; i < childCount; i++) {
			if (index == i) {
				View child = mRoot.getChildAt(i);
				mRoot.removeView(child);
			}
		}
	}
}
