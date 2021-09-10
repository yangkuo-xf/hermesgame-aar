package com.hermesgamesdk.activity;


import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.hermesgamesdk.fragment.pay.QGPayFragment;
import com.hermesgamesdk.manager.QGPayManager;
import com.hermesgamesdk.utils.QGSdkUtils;

/**
 * Created by user on 2017/9/12.
 */

public class PayActivity extends BaseActivity {
	private static final int PAY_SUCCESS=1;
	private static final int PAY_FAIL=0;
	private static final int PAY_CANCLE=-1;
	private static final int PAY_LIMIT=-2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getResId("R.layout.qg_activity_fullscreen_layout"));
	}

	@Override
	protected Fragment getDefaultFragment() {
		return new QGPayFragment();
	}

	// 微信及支付宝网页版支付结果回调 微信0 支付宝网页 21
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("hermesgame","onActivityResult requestCode:"+requestCode+"  resultCode:"+resultCode);
		if (requestCode == 0) {
			String respCode = data.getExtras().getString("respCode");
			String respMsg = data.getExtras().getString("respMsg");
			if (TextUtils.isEmpty(respCode)) {
				QGPayManager.getInstance().mPayCallBack.onFailed("");
			} else {
				if (respCode.equals("00")) {
					QGPayManager.getInstance().mPayCallBack.onSuccess();
					this.finish();
				} else if (respCode.equals("01")) {
					QGPayManager.getInstance().mPayCallBack.onFailed(respMsg);
					this.finish();
				} else if (respCode.equals("03")) {
					QGPayManager.getInstance().mPayCallBack.onFailed(getString(getResId("R.string.toast_text_pay_failed_need_install_weixinpay")));
					this.finish();
				} else if (respCode.equals("02")) {
					QGPayManager.getInstance().mPayCallBack.onFailed(getString(getResId("R.string.toast_text_payfailed")));
					this.finish();
				}
			}

		}
		if (requestCode == 77) {
			QGPayManager.getInstance().mPayCallBack.onSuccess();
			this.finish();
		}
		//支付宝微信网页
		if (requestCode == 21||requestCode==22||requestCode==23) {
			switch (resultCode) {
			case PAY_SUCCESS:
				QGPayManager.getInstance().mPayCallBack.onSuccess();
				this.finish();
				break;
			case PAY_FAIL:
				QGPayManager.getInstance().mPayCallBack.onFailed("支付取消");
				this.finish();
				break;
			case PAY_CANCLE:
			default:
				QGPayManager.getInstance().mPayCallBack.onFailed("支付失败");
				this.finish();
				break;
			}
		}

		//平台币
		if (requestCode==10001){
			switch (resultCode) {
				case PAY_SUCCESS:
					QGPayManager.getInstance().mPayCallBack.onSuccess();
					this.finish();
					break;
				case PAY_FAIL:
					QGPayManager.getInstance().mPayCallBack.onFailed("支付失败");
					this.finish();
					break;
					//防沉迷
				case PAY_LIMIT:
					Intent intent=new Intent();
					intent.setClass(PayActivity.this, GameSliderBarActivityV2.class);
					PayActivity.this.startActivity(intent);
					if (PayActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						 overridePendingTransition(QGSdkUtils.getResId(PayActivity.this,"R.anim.slide_bar_in"),QGSdkUtils.getResId(PayActivity.this,"R.anim.slide_bar_out"));
					} else {
					overridePendingTransition(QGSdkUtils.getResId(PayActivity.this,"R.anim.slide_bar_in_bottom"),QGSdkUtils.getResId(PayActivity.this,"R.anim.slide_bar_out_bottom"));
					}
					QGPayManager.getInstance().mPayCallBack.onFailed("支付取消");
					this.finish();
					break;
				default:
					QGPayManager.getInstance().mPayCallBack.onFailed("支付失败");
					this.finish();
					break;
			}
		}
		if (requestCode==QGPayManager.REQUEST_VOUCHER){
			if (resultCode==0){
				QGPayManager.getInstance().mVoucherCallBack.onSuccess();
			}else {
				QGPayManager.getInstance().mVoucherCallBack.onFailed(data.getStringExtra("id")+"QG"+data.getStringExtra("amount"));
			}
		}
		if (requestCode==QGPayManager.REQUEST_PLUGIN){
			if (resultCode==0){
				QGPayManager.getInstance().mPayCallBack.onSuccess();
			}else {
				QGPayManager.getInstance().mPayCallBack.onFailed("微信支付失败");
			}
			PayActivity.this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
