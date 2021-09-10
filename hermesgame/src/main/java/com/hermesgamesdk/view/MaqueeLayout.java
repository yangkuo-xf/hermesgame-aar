package com.hermesgamesdk.view;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hermesgamesdk.utils.QGSdkUtils;

import java.util.List;

public class MaqueeLayout extends View {
    private MarqueeView marqueeView;
    private ImageView close;
    private Activity mContext;
    private View mLayout;

    private ViewGroup mGroup;
    private OnClickListener mListener;

    public MaqueeLayout(Context context) {
        super(context);
    }
    public MaqueeLayout(Activity context, List<String> messages, final ViewGroup v){
        super(context,null);
        mContext=context;
        mLayout= LayoutInflater.from(context).inflate(QGSdkUtils.getResId(mContext,"R.layout.qg_marqueetext_layout"), v);
        marqueeView = (MarqueeView) mLayout.findViewById(QGSdkUtils.getResId(mContext,"R.id.marqueeView"));
        close= (ImageView) mLayout.findViewById(QGSdkUtils.getResId(mContext,"R.id.close"));
        marqueeView.setFocusable(true);
        marqueeView.startWithList(messages);

    }
    public void setListener(final OnClickListener listener){
        mListener=listener;
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });
        marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
             //   listener.onClick(textView);
            }
        });
    }

}
