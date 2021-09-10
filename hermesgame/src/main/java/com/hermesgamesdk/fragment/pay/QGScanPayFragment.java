package com.hermesgamesdk.fragment.pay;

import org.json.JSONObject;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;

public class QGScanPayFragment extends BaseFragment {
	private WebView scanpay;
	private TextView iv_amount;
	private TextView iv_paytype;
	private Button ensure;

	private String orderNo, amount, payTypes, codeUrl;

	private int isClickCheckResult = 0;

	@Override
	protected String getRootName() {
		// TODO Auto-generated method stub
		return "R.layout.qg_fragment_scanpay";
	}

	@Override
	protected String getTitle() {
		// TODO Auto-generated method stub
		return "R.string.qg_scanpay_title";
	}

	@Override
	protected void onRootViewInflated(View root) {
		// TODO Auto-generated method stub
		initView(root);

	}

	public void initView(View view) {
		Intent intent = getActivity().getIntent();
		amount = intent.getStringExtra("amount");
		payTypes = intent.getStringExtra("payType");
		codeUrl = intent.getStringExtra("codeUrl");
		orderNo = intent.getStringExtra("orderNo");
		scanpay = (WebView) findView("R.id.iv_scanpay");
		iv_amount = (TextView) findView("R.id.tv_pay_amount");
		iv_paytype = (TextView) findView("R.id.tv_pay_message");
		ensure = (Button) findView("R.id.qg_ensure_order");
		if (!codeUrl.isEmpty()) {
			scanpay.loadUrl(codeUrl);
		}
		if (!amount.isEmpty()) {
			iv_amount.setText("¥" + Double.valueOf(amount));
		}
		if (payTypes.equals("180")||payTypes.equals("181")) {
			iv_paytype.setText("支付宝扫一扫");
		} else {
			iv_paytype.setText("微信扫一扫");
		}
		ensure.setText("支付完毕");
		ensure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ensure.setText("正在查询支付结果，请稍等");
				isClickCheckResult = 1;
				ensure.setEnabled(false);
				callPaySuccessForFiveTimes2(orderNo);
			}
		});
	}

	private void callPaySuccessForFiveTimes2(final String sdkOrderID) {

		Log.d("hermesgame", "start requst result  orderID is: " + sdkOrderID);
		String params = new QGParameter(getActivity()).addParameter("orderNo", orderNo).create();
		HttpRequest<String> requstResult = new HttpRequest<String>() {
			@Override
			public void onSuccess(String bean) {
				Log.d("hermesgame", "payStatus: " + bean);
				String pay_status = "0";
				try {
					JSONObject jsonObject = new JSONObject(bean);
					pay_status = jsonObject.getString("pyStatus");
				} catch (Exception e) {
					payfailed();
				}
				if (pay_status.equalsIgnoreCase("1")) {
					Log.d("hermesgame", "check result ok");
					paySuccess();
				} else {
					payfailed();
					Log.d("hermesgame", "check result is 0");
				}
			}

			@Override
			public void onFailed(int id,String message) {
				Log.d("hermesgame", "check result is message: "+message);
				payfailed();
			}
		}.addParameter(params).post().setUrl(Constant.HOST + Constant.REQUST_PAY_RESULT);
		DataManager.getInstance().requestHttp(requstResult);

	}

	@Override
	public void onDestroy() {
		if (isClickCheckResult == 0) {
			payCancle();
		}		
		super.onDestroy();
	}

	public void paySuccess() {
		mActivity.setResult(1);
		mActivity.finish();
	}

	public void payfailed() {
		mActivity.setResult(0);
		mActivity.finish();
	}
	public void payCancle() {
		mActivity.setResult(-1);
	}
}
