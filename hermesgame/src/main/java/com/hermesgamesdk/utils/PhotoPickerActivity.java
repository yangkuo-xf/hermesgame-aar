package com.hermesgamesdk.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class PhotoPickerActivity extends Activity implements View.OnClickListener {

	private Button btnBack, btnSend;
	private GridView gvPhotoPicker;
	private PhotoPickerAdapter adapter;

	private ArrayList<ImageItem> imageItems;// 存储图片路径的集合
	private ArrayList<String> imgPath;

	private AlbumHelper helper;
	private List<ImageBucket> contentList;
	private int maxNum;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(QGSdkUtils.getResId(this, "R.layout.qg_activity_photo_picker"));
		maxNum=getIntent().getIntExtra("maxNum",9);
		Log.e("hermesgame","maxSelectNum: "+maxNum);
		initViews();
		setPhotoGrid();
	}

	private void setPhotoGrid() {
		adapter = new PhotoPickerAdapter(this, imageItems, imgPath, btnSend, gvPhotoPicker, getIntent().getIntExtra("currentColor", 0),maxNum);
		gvPhotoPicker.setAdapter(adapter);
	}

	private void initViews() {
		//设置主题颜色
		findViewById(QGSdkUtils.getResId(this, "R.id.rl_photo_picker_title")).setBackgroundResource(getIntent().getIntExtra("currentColor", 0));
		
		imageItems = new ArrayList<>();
		imgPath = new ArrayList<>();
		btnBack = (Button) findViewById(QGSdkUtils.getResId(this, "R.id.btn_photo_picker_back"));
		btnBack.setOnClickListener(this);
		btnSend = (Button) findViewById(QGSdkUtils.getResId(this, "R.id.btn_photo_picker_send"));
		btnSend.setOnClickListener(this);
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(QGSdkUtils.getResId(this, "R.string.qg_send"))).append("(").append(String.valueOf(imgPath.size()))
				.append("/"+maxNum).append(")");
		btnSend.setText(sb.toString());

		gvPhotoPicker = (GridView) findViewById(QGSdkUtils.getResId(this, "R.id.gv_photo_picker"));

		helper = AlbumHelper.getHelper();
		helper.init(this);
		contentList = helper.getImagesBucketList(true);
		for (int i = 0; i < contentList.size(); i++) {
			imageItems.addAll(contentList.get(i).imageList);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == QGSdkUtils.getResId(this, "R.id.btn_photo_picker_back")) {
			exit();
		} else if (v.getId() == QGSdkUtils.getResId(this, "R.id.btn_photo_picker_send")) {
			if (imgPath.size() <= 0) {
				//CommonUtils.showToast(this, ));
				Toast.makeText(PhotoPickerActivity.this,getResources().getString(QGSdkUtils.getResId(this, "R.string.qg_at_least_one")),Toast.LENGTH_LONG).show();
			} else {
				Intent it = new Intent();
				it.putStringArrayListExtra("photo_picker_photo_url", imgPath);// 选中图片的url集合
				setResult(RESULT_OK, it);
				exit();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 101) {// 拍照之后
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, adapter.getPhotoUri()));

				ImageItem item = new ImageItem();
				item.setImagePath(adapter.getTakePhotoPath());
				imageItems.add(item);
				
				/* 清空所选图片的集合 */
				clearPath();
				setPhotoGrid();
			}
		}
	}

	private void clearPath() {
		imgPath.clear();
		btnSend.setBackgroundResource(QGSdkUtils.getResId(this, "R.drawable.qg_shape_button_reply_button_unclickable"));
		btnSend.setTextColor(getResources().getColor(QGSdkUtils.getResId(this, "R.color.qg_reply_button_text_disable")));
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(QGSdkUtils.getResId(this, "R.string.qg_send"))).append("(").append(String.valueOf(imgPath.size()))
				.append("/"+maxNum).append(")");
		btnSend.setText(sb.toString());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		exit();
	}

	private void exit() {
		for (ImageBucket bucket : contentList) {
			bucket.imageList.clear();
		}
		finish();
		overridePendingTransition(QGSdkUtils.getAndroidAnim(this, "qg_remain"), QGSdkUtils.getAndroidAnim(this, "qg_out_from_bottom"));
	}
}
