package com.hermesgamesdk.floatview.content;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.hermesgamesdk.floatview.ScaleBitmapFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

/**
 * 悬浮框中的单元管理类，包含各个图标单元对象
 */
public class QGContent implements Iterable<QGUnit> {


	private List<QGUnit> list;
	private Context context;
	private int width;// 所有单元框宽度和
	private int height;// 高度
	private int x;// 相对父控件
	private int y;// 相对父控件
	private double intervalPercent = 0.25; // 用于单元框间隔，值代表与高度比值

	// 扩展点击区域
	private boolean isClickAreaExtend = false;

	public QGContent(Context context) {
		this.context = context;
		init(context);
	}

	/**
	 * 初始化，默认开启部分功能，如果有需要可移至外部调用
	 * 
	 * @param context
	 */
	private void init(Context context) {
		createListIfNull();
		setClickAreaExtend(true);
	}

	private void createListIfNull() {
		if (list == null) {
			list = new LinkedList<QGUnit>();
		}
	}

	public int getIndexById(int id) {
		QGUnit unit = getUnitById(id);
		return unit == null ? -1 : unit.getId();
	}

	public int getIdByIndex(int index) {
		QGUnit unit = getUnitByIndex(index);
		return unit == null ? -1 : unit.getIndex();
	}

	/**
	 * 添加单元
	 * 
	 * @param imgID
	 * @param listener
	 */
	public void addUnit(int imgID, View.OnClickListener listener) {
		createListIfNull();
		if (!isUnitExisted(imgID))
			addUnit(list.size(), imgID, listener);
	}

	/**
	 * 添加单元
	 * 
	 * @param index
	 * @param id
	 * @param listener
	 */
	private void addUnit(int index, int id, View.OnClickListener listener) {
		createListIfNull();
		Bitmap img = ScaleBitmapFactory.decodeResource(context.getResources(), id);
		int x = 0;
		int y = 0;
		int width = img.getWidth();
		int height = img.getHeight();
		// img.recycle();
		Rect srcRect = new Rect(x, y, x + width, y + height);
		Rect dstRect = new Rect(x, y, x + width, y + height);

		Log.d("QGUNIT", "newImg.getWidth() =" + img.getWidth() + "   newImg.getHeight() =" + img.getHeight());

		QGUnit newUnit = new QGUnit(index, id, img, x + getWidthsWithInterval() + getInterval(), y
				+ getHeightsWithInterval(), width, height, srcRect, dstRect, listener);

		// Log.d(TAG, "QGFloat addUnit:" + newUnit.toString());
		list.add(index, newUnit);

		// 重新计算宽度
		this.width = getWidthsWithInterval();
		this.height = getHeightMax();
	}

	/**
	 * 检查单元是否已存在，保证只添加一项
	 * 
	 * @param imgID
	 * @return 唯一为true
	 */
	private boolean isUnitExisted(int imgID) {
		createListIfNull();
		for (QGUnit unit : list) {
			if (unit.getId() == imgID) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 打开扩展点击区域功能
	 * 
	 * @param clickAreaExtend
	 */
	public void setClickAreaExtend(boolean clickAreaExtend) {
		isClickAreaExtend = clickAreaExtend;
	}

	/**
	 * 获取单元
	 * 
	 * @param id
	 * @return
	 */
	public QGUnit getUnitById(int id) {
		if (list == null)
			return null;
		for (QGUnit unit : list) {
			if (unit.getId() == id) {
				return unit;
			}
		}
		return null;
	}

	public QGUnit getUnitByIndex(int index) {
		if (list == null)
			return null;
		QGUnit unit = list.get(index);
		if (unit.getIndex() == index) {
			return unit;
		}
		else {
			for (QGUnit u : list) {
				if (u.getIndex() == index) {
					return u;
				}
			}
			return unit;
		}

	}

	/**
	 * 移除单元框
	 * 
	 * @param index
	 */
	public boolean removeUnitByIndex(int index) {
		Iterator<QGUnit> iterator = list.iterator();
		while (iterator.hasNext()) {
			QGUnit unit = iterator.next();
			if (unit.getIndex() == index) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * 移除单元框
	 * 
	 * @param imgID
	 */
	public boolean removeUnitById(int imgID) {
		Iterator<QGUnit> iterator = list.iterator();
		while (iterator.hasNext()) {
			QGUnit unit = iterator.next();
			if (unit.getId() == imgID) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * 单元宽度之和
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 单元宽度之和，包含间隔，类似：w1+i+w2+i+w3
	 */
	private int getWidthsWithInterval() {
		if (list == null || list.size() == 0) {
			return 0;
		}
		int widths = 0;
		for (QGUnit unit : list) {
			widths += unit.getWidth() + getInterval();
		}
		return widths - getInterval();
	}

	/**
	 * 获取高度
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 获取高度
	 * 
	 * @return
	 */
	private int getHeightMax() {
		if (list == null || list.size() == 0) {
			return 0;
		}
		int height = 0;
		for (QGUnit unit : list) {
			height = Math.max(height, unit.getHeight());
		}
		return height;
	}

	/**
	 * 获取间隔宽度
	 * 
	 * @return
	 */
	private int getInterval() {
		return (int) (getHeightMax() * intervalPercent);
	}

	/**
	 * 设置unit图标间隔（高度百分比），在addUnit之前调用
	 * 
	 * @param percent percent>=0
	 */
	public void setIntervalPercent(double percent) {
		if (percent >= 0)
			intervalPercent = percent;
	}

	/**
	 * 设置
	 */

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	/**
	 * 单元数量
	 * 
	 * @return
	 */
	public int size() {
		return list == null ? 0 : list.size();
	}

	/**
	 * 清空
	 */
	public void clear() {
		list.clear();
		list = null;
		context = null;
	}

	/**
	 * 获取点击的单元
	 * 
	 * @param x
	 * @param y
	 * @param shiftX 相对父控件X坐标轴偏移
	 * @param shiftY 相对父控件Y坐标轴偏移
	 * @return
	 */
	public QGUnit getTouchedUnit(int x, int y, int shiftX, int shiftY) {
		QGUnit touchedUnit = null;
		for (QGUnit unit : list) {
			if (unit.containExtend(x - shiftX, y - shiftY, getLengthOfClickAreaExtend())) {
				touchedUnit = unit;
				// Log.d(TAG, "getTouchedUnit  " + unit.toString() + " x=" + x +
				// ",y=" + y + ", shiftX=" + shiftX + ", shiftY=" + shiftY);
				break;
			}
		}
		return touchedUnit;
	}

	/**
	 * 获取点击时扩展区域延长的长度
	 * 
	 * @return
	 */
	public int getLengthOfClickAreaExtend() {
		return isClickAreaExtend ? getInterval() / 2 : 0;
	}

	private int getHeightsWithInterval() {
		return 0;
	}

	/**
	 * 迭代器Iterator
	 * 
	 * @return
	 */
	public Iterator<QGUnit> iterator() {
		return new QGContentUnitIterator();
	}

	/**
	 * 自定义迭代器
	 */
	private class QGContentUnitIterator implements Iterator<QGUnit> {

		private int cursor = -1;

		@Override
		public boolean hasNext() {
			return this.cursor + 1 < QGContent.this.list.size();
		}

		@Override
		public QGUnit next() {
			return list.get(++cursor);
		}

		@Override
		public void remove() {

		}
	}

}
