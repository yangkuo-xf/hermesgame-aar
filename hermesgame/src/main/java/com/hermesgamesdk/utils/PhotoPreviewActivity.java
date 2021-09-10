package com.hermesgamesdk.utils;

import java.io.IOException;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

public class PhotoPreviewActivity extends Activity implements OnClickListener, OnViewTapListener{

	private PhotoView imgPhoto;
	private Button btnBack;
	private RelativeLayout rlTitle;

	private AlphaAnimation alphaAnimation;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(QGSdkUtils.getResId(this, "R.layout.qg_activity_photo_preview"));

		initViews();
	}

	private void initViews() {
		rlTitle = (RelativeLayout) findViewById(QGSdkUtils.getResId(this, "R.id.rl_photo_preview_title"));
		rlTitle.setBackgroundResource(getIntent().getIntExtra("currentColor", 0));
		
		imgPhoto = (PhotoView) findViewById(QGSdkUtils.getResId(this, "R.id.img_photo_preview"));
		imgPhoto.setOnViewTapListener(this);
		try {
			imgPhoto.setImageBitmap(Bimp.revitionImageSize(getIntent().getStringExtra("img_path")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		btnBack = (Button) findViewById(QGSdkUtils.getResId(this, "R.id.btn_photo_preview_back"));
		btnBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == QGSdkUtils.getResId(this, "R.id.btn_photo_preview_back")){
			finish();
			overridePendingTransition(QGSdkUtils.getAndroidAnim(this, "qg_remain"), QGSdkUtils.getAndroidAnim(this, "qg_out_from_bottom"));
		}
	}
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(QGSdkUtils.getAndroidAnim(this, "qg_remain"), QGSdkUtils.getAndroidAnim(this, "qg_out_from_bottom"));
	}

	@Override
	public void onViewTap(View view, float x, float y) {
		if(view.getId() == QGSdkUtils.getResId(this, "R.id.img_photo_preview")){
			if(rlTitle.getVisibility() == View.VISIBLE){
				rlTitle.setVisibility(View.GONE);
				alphaAnimation = new AlphaAnimation(1, 0);
				alphaAnimation.setDuration(300);
				rlTitle.startAnimation(alphaAnimation);
			} else {
				rlTitle.setVisibility(View.VISIBLE);
				alphaAnimation = new AlphaAnimation(0, 1);
				alphaAnimation.setDuration(300);
				rlTitle.startAnimation(alphaAnimation);
			}
		}
	}


}
