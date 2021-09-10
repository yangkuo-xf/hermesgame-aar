package com.hermesgamesdk.floatview.content;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

/**
 * 悬浮框单元，每个对象对应一个图标和功能
 */
class QGUnit {
    
    private int index;
    private int id; //图像ID
    private int width;//图片宽度
    private int height;//图片高度
    private int x;//相对父控件x坐标
    private int y;//相对父控件y坐标
    private Rect srcRect;
    private Rect dstRect;
    private Bitmap bitmap;//图片对应的bitmap
    private boolean isTouchValid;//有效触摸，用于判断点击事件是否有效
    private View.OnClickListener listener;//点击事件监听
    
    public QGUnit(int index, int id, Bitmap bitmap, int x, int y, int width, int height, Rect srcRect, Rect dstRect, View.OnClickListener listener) {
        this.index = index;
        this.id = id;
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.srcRect = srcRect;
        this.dstRect = dstRect;
        this.listener = listener;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
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
    
    public Rect getSrcRect() {
        return srcRect;
    }
    
    public void setSrcRect(Rect srcRect) {
        this.srcRect = srcRect;
    }
    
    public Rect getDstRect() {
        return dstRect;
    }
    
    public void setDstRect(Rect dstRect) {
        this.dstRect = dstRect;
    }
    
    public Bitmap getBitmap() {
        return bitmap;
    }
    
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    
    public boolean isTouchValid() {
        return isTouchValid;
    }
    
    public void setTouchValid(boolean touchValid) {
        isTouchValid = touchValid;
    }
    
    public View.OnClickListener getListener() {
        return listener;
    }
    
    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    
    @Override
    public String toString() {
        return "QGUnit {" +
                "index=" + index +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
    
    /**
     * 包含点
     *
     * @param x
     * @param y
     * @return
     */
    public boolean contain(float x, float y) {
        return x > getX() && x < getX() + getWidth() &&
                y > getY() && y < getY() + getHeight();
    }
    
    /**
     * 扩展区域后包含点
     *
     * @param x
     * @param y
     * @param extend 区域变化，正数扩大区域，负数减小区域
     * @return
     */
    public boolean containExtend(float x, float y, int extend) {
        return x > getX() - extend && x < getX() + getWidth() + extend &&
                y > getY() - extend && y < getY() + getHeight() + extend;
    }
    
    
}
