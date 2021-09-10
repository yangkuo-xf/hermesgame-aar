package com.hermesgamesdk.activity;

import com.hermesgamesdk.fragment.download.DownloadTipsFragement;

import android.support.v4.app.Fragment;
import android.os.Bundle;

public class DownloadActivity extends BaseActivity {
	
	public static final int RESULT_CODE=1111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getResId("R.layout.qg_activity_layout"));
	}

	@Override
	protected Fragment getDefaultFragment() {
		return new DownloadTipsFragement();
	}
	@Override
	public void finish() {
		setResult(RESULT_CODE,getIntent());
		super.finish();
	}


}
