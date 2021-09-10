package com.hermesgamesdk.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.hermesgamesdk.utils.QGSdkUtils;

public class PassWordEditText extends EditText {
    public Context mContext;
    private static final int lineW=3;
    private static final int borderW=3;
    private static final int rectW=45;
    private static final String lineColor="#FFD7D7D7";
    private static final String passwordCircleColor="#FF939393";
    Paint lineP=new Paint();
    Paint rectP=new Paint();
    Paint textP=new Paint();



    public PassWordEditText(Context context) {
        super(context);
        initPaint();
    }

    public PassWordEditText(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext=context;
        initPaint();

    }

    public PassWordEditText(Context context, AttributeSet attrs, int defStyleAttr, Paint line) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        initPaint();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PassWordEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, Paint line) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext=context;
        initPaint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        drawBackGround(canvas);
        drawPasswordCircle(canvas);
    }
    public void initPaint(){
        // 初始化密码边框的画笔
        rectP = new Paint();
        // 抗锯齿
        rectP.setAntiAlias(true);
        // 防抖动
        rectP.setDither(true);
        // 给画笔设置大小
        rectP.setStrokeWidth(borderW);
        // 设置背景的颜色
        rectP.setColor(Color.parseColor(lineColor));
        // 画空心
        rectP.setStyle(Paint.Style.STROKE);

        // 初始化分隔线的画笔
        lineP = new Paint();
        // 抗锯齿
        lineP.setAntiAlias(true);
        // 防抖动
        lineP.setDither(true);
        // 分割线画笔设置大小
        lineP.setStrokeWidth(lineW);
        // 设置分割线的颜色
        lineP.setColor(Color.parseColor(lineColor));

        // 初始化密码数字的画笔
        textP = new Paint();
        // 抗锯齿
        textP.setAntiAlias(true);
        // 防抖动
        textP.setDither(true);
        // 设置分割线的颜色
        textP.setColor(Color.parseColor(passwordCircleColor));
    }
    public void drawBackGround(Canvas canvas){
        //画方框
        Path path = new Path();
        //除以一半  防止（0,0） 开始的边线一半异常
        RectF rectF = new RectF(lineW/2, lineW/2, QGSdkUtils.dp2px(mContext,rectW*6), QGSdkUtils.dp2px(mContext,rectW));
        //带圆角矩形
        path.addRoundRect(rectF, 6, 6, Path.Direction.CCW);
        canvas.drawPath(path, rectP);
        //画间隔线
        for (int i=0;i<5;i++){
             canvas.drawLine(QGSdkUtils.dp2px(mContext,rectW*(i+1)),0,QGSdkUtils.dp2px(mContext,rectW*(i+1)),QGSdkUtils.dp2px(mContext,rectW),lineP);
        }
    }

    public void drawPasswordCircle(Canvas canvas){
        String password=getText().toString().trim();

        // 获取密码的长度
        int passwordLength = password.length();
        // 不断的绘制密码
        for (int i = 0; i < passwordLength; i++) {
            //距离Y
            int posY = getHeight() / 2;
            //距离X=间隔线*条数+中心距离25+格子数*数量
            int  postX=QGSdkUtils.dp2px(mContext,rectW/2)+QGSdkUtils.dp2px(mContext,rectW)*i;
        /* int cx = mBorderStrokeSize
                    + i * mPasswordItemWidth + i * mDivisionLineSize + mPasswordItemWidth / 2;*/
            canvas.drawCircle(postX,posY,14,textP);
        }
        // 当前密码是不是满了
        if (mListener != null) {
            if (password.length() == 6) {
                mListener.passwordFull(password);
                password="";
            } else {
                mListener.passwordChanged(password);
            }
        }
    }

    // 设置当前密码是否已满的接口回掉
    private PasswordFullListener mListener;

    public void setOnPasswordFullListener(PasswordFullListener listener) {
        this.mListener = listener;
    }
    /**
     * 添加一个密码
     */
    public void addPassword(String number) {
        // 把之前的密码取出来
        String password = getText().toString().trim();
        if (password.length() >= 6) {
            // 密码不能超过当前密码个输
            return;
        }
        // 密码叠加
        password += number;
        setText(password);
    }

    /**
     * 删除最后一位密码
     */
    public void deleteLastPassword() {
        String password = getText().toString().trim();
        // 判断当前密码是不是空
        if (TextUtils.isEmpty(password)) {
            return;
        }
        password = password.substring(0, password.length() - 1);
        setText(password);
    }
    /**
     * 密码已经全部填满
     */
    public interface PasswordFullListener {
        void passwordFull(String password);
        void passwordChanged(String password);
    }
}
