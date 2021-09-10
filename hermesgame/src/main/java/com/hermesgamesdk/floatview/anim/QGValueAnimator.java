package com.hermesgamesdk.floatview.anim;

import android.os.CountDownTimer;

/**
 * 自定义动画，为了应对手机系统关闭动画功能
 * 如果需要使用属性动画ValueAnimator，删除动画相关类的QG前缀
 */
public class QGValueAnimator extends QGAnimator {
    
    public static final String TAG = "QGValueAnimator";
    
    private static final long PERIOD = 20;//定时器间隔，单位毫秒
    
    private long duration = 300;
    
    private int valueStart;
    private int valueLength;
    private int valueCurrent;
    
    private CountDownTimer downTimer;
    
    private double increase;
    public interface QGAnimatorUpdateListener {
        void onAnimationUpdate(QGValueAnimator animation);
        
    }
    
    public static QGValueAnimator ofInt(int start, int end) {
        QGValueAnimator anim = new QGValueAnimator();
        anim.setIntValues(start, end);
        return anim;
    }
    
    private void setIntValues(int start, int end) {
        valueStart = start;
        valueLength = end - start;
        valueCurrent = start;
//        Log.d(TAG, "valueStart=" + valueStart + "  valueEnd=" + valueEnd + "  valueLength=" + valueLength);
    }
    
    /**
     * 设置动画时间，毫秒
     */
    public QGValueAnimator setDuration(long duration) {
        this.duration = duration;
        increase = valueLength / (1.0 * this.duration / PERIOD);
//        Log.d(TAG, "duration=" + duration + "  increase=" + increase);
        return this;
    }
    
    public Object getAnimatedValue() {
//        Log.d(TAG, "getAnimatedValue valueCurrent = " + valueCurrent);
        return Math.min(valueCurrent, valueLength);
    }
    
    
    /**
     * 启动
     */
    public void start() {
        
        if (downTimer != null)
            return;
        
        valueCurrent = (int) (valueStart + increase);
        listener.onAnimationStart(QGValueAnimator.this);
        updateListener.onAnimationUpdate(QGValueAnimator.this);
//        Log.d(TAG, "onStart  time=" + getCurrentTime());
        
        downTimer = new CountDownTimer(duration, PERIOD) {
            int counter = 1;
            
            @Override
            public void onTick(long millisUntilFinished) {
                counter++;
                valueCurrent = (int) (valueStart + increase * counter);
//                Log.d(TAG, "onTick valueCurrent=" + valueCurrent + "  counter=" + counter + "  time=" + getCurrentTime());
                updateListener.onAnimationUpdate(QGValueAnimator.this);
            }
            
            @Override
            public void onFinish() {
                counter++;
                valueCurrent = (int) (valueStart + increase * counter);
//                Log.d(TAG, "onFinish valueCurrent=" + valueCurrent + "  counter=" + counter + "  time=" + getCurrentTime());
                updateListener.onAnimationUpdate(QGValueAnimator.this);
                listener.onAnimationEnd(QGValueAnimator.this);
                downTimer = null;
                
            }
        }.start();
        
        
    }
    
    @SuppressWarnings("unused")
	private long getCurrentTime() {
        return System.currentTimeMillis() % 100000;
    }
    
    //Listener
    
    private QGAnimatorUpdateListener updateListener = null;
    
    public void addUpdateListener(QGAnimatorUpdateListener listener) {
        this.updateListener = listener;
    }
    
    private QGAnimatorListener listener;
    
    
    public void addListener(QGAnimatorListener listener) {
        this.listener = listener;
    }
    
    public interface QGAnimatorListener {
        void onAnimationStart(QGAnimator animation);
        
        void onAnimationEnd(QGAnimator animation);
        
    }
    
}
