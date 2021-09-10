package com.hermesgamesdk.activity;

import android.support.v4.app.Fragment;
import com.hermesgamesdk.fragment.pay.QGScanPayFragment;

import android.os.Bundle;

public class ScanPayActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getResId("R.layout.qg_activity_layout"));
	}

	@Override
	protected Fragment getDefaultFragment() {
		// TODO Auto-generated method stub
		return new QGScanPayFragment();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
