package com.hermesgamesdk.floatview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ScaleBitmapFactory {

	private static float scale = 1;

	public static void setScale(float scale) {
		ScaleBitmapFactory.scale = scale;
	}
	
	public static Bitmap decodeResource(Resources resources, int id, float scale) {
		Bitmap bmp = BitmapFactory.decodeResource(resources, id);
		return scaleBitmap(bmp, scale);
	}

	public static Bitmap decodeResource(Resources resources, int id) {
		return decodeResource(resources, id, scale);
	}

	/**
	 * 按比例缩放图片
	 * 
	 * @param origin 原图
	 * @param ratio 比例
	 * @return 新的bitmap
	 */
	private static Bitmap scaleBitmap(Bitmap origin, float ratio) {
		if (origin == null) {
			return null;
		}
		int width = origin.getWidth();
		int height = origin.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(ratio, ratio);
		Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
		if (newBM.equals(origin)) {
			return newBM;
		}
		origin.recycle();
		return newBM;
	}

}
