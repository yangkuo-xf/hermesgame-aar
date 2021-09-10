package com.hermesgamesdk.view;

import android.content.Context;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.hermesgamesdk.utils.QGSdkUtils;


/**
 * 由于界面中多次使用EditText，切有相同的样式和功能，自定义QGEditText便于复用
 * QGEditText 为组合控件，当EditText有文本时显示清楚文本icon
 * 点击icon时候清楚文本，icon影藏
 */

public class QGEditText extends RelativeLayout {
    //文本输入框
    private EditText et;
    //清除文本控件
    private ImageView iv;

    private TextChangedListener listener;
    private OnFocusChangeListener mlistener;

    public QGEditText(Context context) {
        this(context, null);
    }

    public QGEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    /**
     * @param context
     * @param attrs   创建子控件并添加至QGEditText中
     */
    private void initView(Context context, AttributeSet attrs) {
        //创建清除文本控件
        iv = new ImageView(context);
        LayoutParams rp1 = new LayoutParams(dp2px(context, 33), dp2px(context, 33));
        rp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rp1.addRule(RelativeLayout.CENTER_VERTICAL);
        iv.setLayoutParams(rp1);
        iv.setImageResource(QGSdkUtils.getResId(context, "R.drawable.qg_delete"));
        iv.setVisibility(View.GONE);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int padding = dp2px(context, 9);
        //设置ImageVeiw边距使icon易于点击
        iv.setPadding(dp2px(context, 9), padding, dp2px(context, 9), padding);

        //创建输入文本控件，传入attrs，使xml中定义的属性值生效
        et = new EditText(context, attrs);
        LayoutParams rp2 = new LayoutParams(-1, -1);
        et.setLayoutParams(rp2);
        et.setImeOptions(EditorInfo.IME_ACTION_DONE);
      //  et.setTextColor(getResources().getColor(QGSdkUtils.getResId(context,"R.color.qg_ed_text_color")));
        et.setHintTextColor(getResources().getColor(QGSdkUtils.getResId(context,"R.color.qg_ed_hint_text_color")));

        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                iv.setVisibility(s.length() > 0 ? VISIBLE : GONE);
                if (listener != null)
                    listener.onTextChanged(s, start, before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null)
                    listener.afterTextChanged(s);
            }
        });
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mlistener.onFocusChange(hasFocus);
            }
        });

        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                et.setText("");
            }
        });

        addView(et);
        addView(iv);
    }

    public void setTransformationMethod(TransformationMethod method) {
        if (et != null) {
            et.setTransformationMethod(method);
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

    /**
     * @param listener 设置文本变化的监听
     */
    public void addTextChangedListener(TextChangedListener listener) {
        this.listener = listener;
    }
    /**
     * @param listener 设置文本变化的监听
     */
    public void addFocusChangeListener(OnFocusChangeListener listener) {
        this.mlistener = listener;
    }

    public interface TextChangedListener {
        //EditText文本发送变化时触发回调
        void onTextChanged(CharSequence s, int start, int before, int count);

        void afterTextChanged(Editable s);
    }

    public interface  OnFocusChangeListener{
         void onFocusChange(boolean hasFocus);
    }
    /**
     * @param inputType 设置EditText输入文本
     */
    public void setInputType(int inputType) {
        if (et != null)
            et.setInputType(inputType);
    }
    public void setFilters(InputFilter[] filters) {
        if (et != null)
            et.setFilters(filters);
    }


    /**
     * @param text 设置EditText文本
     */
    public void setText(String text) {
        if (et != null && text != null)
            et.setText(text);
    }

    /**
     * @return EditText 的文本
     */
    public String getText() {
        if (et != null)
            return et.getText().toString();
        return "";
    }

    /**
     * @return EditText的InputType
     */
    public int getInputType() {
        return et.getInputType();
    }

public EditText getEt(){
        return et;
}
    public ImageView getClose(){
        return iv;
    }
}
