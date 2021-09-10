package com.hermesgamesdk.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hermesgamesdk.fragment.QGWebViewFragment;

public class NoticeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getResId("R.layout.qg_activity_fullscreen_layout"));
	}

	@Override
	protected Fragment getDefaultFragment() {
		QGWebViewFragment fragment = new QGWebViewFragment();
		if (getIntent().getStringExtra("from").equals("LOGIN")){
			fragment.setFlag(QGWebViewFragment.USER_AGREEMENT_LOGIN);
		}else if (getIntent().getStringExtra("from").equals("REGIST")){
			fragment.setFlag(QGWebViewFragment.USER_AGREEMENT_REGIST);
		}else if (getIntent().getStringExtra("from").equals("REGIST_P")){
			fragment.setFlag(QGWebViewFragment.USER_AGREEMENT_PRIVATE);
		}
		else if(getIntent().getStringExtra("from").equals("NOTICE")){
			fragment.setFlag(QGWebViewFragment.NITICE);
		}
		else if(getIntent().getStringExtra("from").equals("CERT")){
			fragment.setFlag(QGWebViewFragment.CERT_LIMIT);
		}
		return fragment;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
