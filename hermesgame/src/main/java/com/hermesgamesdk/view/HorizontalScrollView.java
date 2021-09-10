package com.hermesgamesdk.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/3/7.
 */

public class HorizontalScrollView extends LinearLayout {

    private float mPreRawX;
    private float mPreRawY;
    private float mTranslocationX;
    private Activity mActivity;
    private int mTotalX;
    private float mBeforeOffsetY = Float.NaN;
    private float tan = Float.NaN;
    private boolean mIsScrollHorizontal;
    private boolean mIsFirstPage;
    private long mDownTime;
    private int mSpeedX = 1;

    public void setIsFirstPage(boolean isFirstPage) {
        mIsFirstPage = isFirstPage;
    }

    public HorizontalScrollView(Context context) {
        super(context);
    }

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreRawX = event.getRawX();
                mPreRawY = event.getRawY();
                mTranslocationX = getWidth() / 2;
                mDownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                //第一次触发move事件
                if (Float.isNaN(tan)) {
                    float denominator = rawX - mPreRawX;
                    //分母为0时，tan取最大值
                    tan = denominator == 0 ? Float.MAX_VALUE : Math.abs((rawY - mPreRawY) / denominator);
                    if (tan < 0.5f) {
                        mIsScrollHorizontal = true;

                    } else {
                        //第一次事件tan值大于0.5f标识后续move不在触发横向移动
                        mIsScrollHorizontal = false;
                    }
                }
                //允许垂直滑动，禁止水平滑动,主页面不做水平滑动
                if (!mIsScrollHorizontal || mIsFirstPage)
                    break;
                //计算水平偏移距离
                int offset = (int) (rawX - mPreRawX);
                mPreRawX = rawX;
                mPreRawY = rawY;
                //如果偏移后的的值小于0，mTotalX>0,偏移距离为-mTotalX
                if (offset + mTotalX < 0 && mTotalX > 0)
                    offset = -mTotalX;
                //偏移后的距离大于等于0，则允许水平位移
                if (mTotalX + offset >= 0) {
                    //将第一次位移时的y轴坐标保存在mBeforeOffsetY变量中，位移结束后设置到event对象中
                    //这样触摸事件前后webview的y坐标未改变，从而使得水平位移时，y方向不能位移
                    if (mBeforeOffsetY == Float.NaN)
                        mBeforeOffsetY = y;
                    //每次偏移距离
                    scrollBy(-offset, 0);
                    //偏移的总位移
                    mTotalX += offset;
                }
                //将发生水平位移前的y坐标设置到event中
                if (mTotalX > 0 && mBeforeOffsetY != Float.NaN)
                    event.setLocation(x, mBeforeOffsetY);
                break;
            case MotionEvent.ACTION_UP:
                //当x水平向右滑动距离大于mTranslocationX，触发返回事件
                long time = System.currentTimeMillis() - mDownTime;
                int speedX = time==0?0:(int) (mTotalX / (time));
                //是否返回取决于滑动素质和位移
                if (mTotalX >= mTranslocationX || speedX > mSpeedX)
                    finishActivity();
                else if (mTotalX > 0)
                    restore();//回复原位置，需要一定的持续回复时间
                boolean canDispatchTouchEvent = mTotalX == 0;
                mBeforeOffsetY = Float.NaN;
                tan = Float.NaN;
                //当在水平滑动中，直接返回false，不分发ACTION_UP事件，因而不会触发点击事件
                return canDispatchTouchEvent && super.dispatchTouchEvent(event);
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private void finishActivity() {
        final int width = getWidth();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //根据300ms位移屏幕宽度计算出动画位移速度
                        int offset = width / 30;
                        if (mTotalX < width) {
                            mTotalX += offset;
                            scrollBy(-offset, 0);
                        } else {
                            timer.cancel();
                            mActivity.finish();

                        }

                    }
                });

            }
        }, 0, 10);


    }

    private void restore() {
        final int width = getWidth();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int offset = width / 30;
                        if (mTotalX > 0) {
                            if (mTotalX - offset < 0)
                                offset = mTotalX;
                            mTotalX -= offset;
                            scrollBy(offset, 0);
                        } else {
                            timer.cancel();
                        }
                    }
                });

            }
        }, 0, 10);
    }
}
