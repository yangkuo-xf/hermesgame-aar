package com.hermesgamesdk.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.hermesgamesdk.fragment.AnnouncementFragment;
import com.hermesgamesdk.fragment.CertificationFragment;
import com.hermesgamesdk.fragment.PhoneBindFragment;
import com.hermesgamesdk.fragment.login.FindPasswordFragment;
import com.hermesgamesdk.fragment.login.TryPlayFragment;
import com.hermesgamesdk.fragment.pay.QGSetPayPasswordFragment;
import com.hermesgamesdk.fragment.usercenter.BindUsernameFragment;
import com.hermesgamesdk.fragment.usercenter.MobileBindUserFragment;
import com.hermesgamesdk.fragment.usercenter.ModifyPasswordFragment;
import com.hermesgamesdk.fragment.usercenter.PhoneUnbindFragment;

public class TempActivty extends BaseActivity {
	public String from = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(getResId("R.layout.qg_activity_layout"));
		from = getIntent().getExtras().getString("from");
		Log.d("hermesgame", "from: " + from);
	}

	@Override
	protected Fragment getDefaultFragment() {
		from = getIntent().getExtras().getString("from");
		// TODO Auto-generated method stub
		if (from.equals("slider_find")) {
			return new FindPasswordFragment();
		} else if (from.equals("slider_bind")) {
			return new PhoneBindFragment();
		} else if (from.equals("slider_unbind")) {
			return new PhoneUnbindFragment();
		} else if (from.equals("slider_cert")) {
			return new CertificationFragment();
		} else if (from.equals("slienceLogin_cert")) {
			return new CertificationFragment();
		} else if (from.equals("slider_modify")) {
			return new ModifyPasswordFragment();
		} else if (from.equals("slider_setName")) {
			return new BindUsernameFragment();
		} else if (from.equals("notice")) {
			return new AnnouncementFragment();
		} else if(from.equals("slider_mobile_bind_user")){
			return new MobileBindUserFragment();
		} else if(from.equals("slider_resetPayPassWord")){
			return new QGSetPayPasswordFragment();
		} else if(from.equals("guest_cert")){
			return new CertificationFragment();
		} else if(from.equals("slienceLogin_tryplay")){
			return new TryPlayFragment();
		}
		else{
			return null;
		}
	}

}
