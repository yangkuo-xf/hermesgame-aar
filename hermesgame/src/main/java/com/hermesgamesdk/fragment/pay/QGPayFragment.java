package com.hermesgamesdk.fragment.pay;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.hermesgamesdk.activity.QGSdkCoinsPayActivity;
import com.hermesgamesdk.callback.QGCallBack;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.entity.InitData.Paytypes;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.entity.WalletInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.QGPayManager;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.QGPayListAdapter;

import static android.view.View.GONE;

/**
 * Created by user on 2017/9/12.
 */

public class QGPayFragment extends BaseFragment {
	private Button startPay;
	private TextView qg_goods_name  ,qg_amount ,qg_rabate ,qg_pay_way , qg_real_amount,qg_voucher_content;
	private ImageView qg_voucher_jump;
	private LinearLayout qg_pay_cancle_button;
	private LinearLayout  qg_voucher_jump_layout;

	private List<Paytypes> payTypes;
	private List<Paytypes> removedPayTypes=null;
	private boolean ischecked = false;
	// 支付方式 支付宝客户端 1 支付宝网页版 2 微信客户端 7 微信web版 88 ;
	private int payType = 999;
	// 支付状态 0：只吊起支付界面 1：选择具体支付方式 用于判断吊起却取消支付
	public int payStatus = 0;

	public int tempPayTypes=0;

	private ListView listView = null;
	private QGPayListAdapter qgPayListAdapter;
	private List<WalletInfo.Vouchers> vouchersList;
	private int []typeArray={1,2,7,88,130,165,166,167,173,176,178,179,180,181,182,183,184,193,194,199,201,202,203,208,214,220,221,223,224,225,226,227,228};
	Handler handler=new Handler();
	@Override
	protected String getRootName() {
		return "R.layout.qg_pay_layout";
	}

	@Override
	protected void onRootViewInflated(View root) {
		initView(root);
		switchToCerificationFragment(CerificationNode.ON_PAY);
	}

	@Override
	protected String getTitle() {
		return "R.string.qg_startpay_check_orderinfo";
	}

	public void initView(View rootView) {

		/*
		 * 你下单之前先检查rabate字段下面rateConfig数组是否为空，如果为空你就取rabate下的rate，
		 * 如果不为空你就遍历rateConfig下面的列表，取对应的rate,如果子列表一个都没匹配到就取rabate下的rate
		 */

		// 判断支付方式的显示和隐藏
		if (mActivity!=null){

		}else {
			Log.e("hermesgame QGPay", "mActivity  is  null");
			//QGPayManager.getInstance().mPayCallBack.onFailed("mActivity  is  null");
			forceBack();
			mActivity.finish();
		}
		try {
			InitData aData = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
			payTypes = aData.getPaytypes();
			if (payTypes==null||payTypes.size()==0){
				Log.e("hermesgame.QGP", "payTypes  is  null || size==0");
			}
		} catch (Exception e) {
			Log.e("hermesgame.QGP", "InitData  is  null");
			//QGPayManager.getInstance().mPayCallBack.onFailed("InitData  is  null");
			forceBack();
			mActivity.finish();
		}
		QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		startPay = (Button) findView("R.id.qg_btn_startPay");
		startPay.setEnabled(true);
		listView = (ListView) findView("R.id.qg_payType_lv");
		//  qg_goods_name  ,qg_amount ,qg_rabate ,qg_pay_way , qg_real_amount
		qg_goods_name=(TextView)findView("R.id.qg_goods_name");
		qg_amount=(TextView)findView("R.id.qg_amount");
		qg_rabate=(TextView)findView("R.id.qg_rabate");
		qg_pay_way=(TextView)findView("R.id.qg_pay_way");
		qg_real_amount=(TextView)findView("R.id.qg_real_amount");
		qg_pay_cancle_button=(LinearLayout)findView("R.id.qg_pay_cancle_button");
		qg_voucher_content=(TextView)findView("R.id.qg_voucher_content");
		qg_voucher_jump=(ImageView) findView("R.id.qg_voucher_jump");
		qg_voucher_jump_layout=(LinearLayout) findView("R.id.qg_voucher_jump_layout");
		vouchersList= ((WalletInfo) DataManager.getInstance().getData(Constant.WALLET_KEY)).getVouchers();
		//没有代金券或者代金券不可用
		if (vouchersList!=null){
			if (vouchersList.size()==0||!checkVoucher(vouchersList)){
				qg_voucher_content.setText(getString("R.string.qg_dont_have_voucher"));
				qg_voucher_content.setTextColor(Color.parseColor("#FF7A7A7A"));
			}else{
				qg_voucher_content.setText(getString("R.string.qg_have_voucher"));
				qg_voucher_content.setTextColor(Color.parseColor("#FF774e"));
			}
		}else{
			qg_voucher_jump_layout.setVisibility(GONE);
		}
		listView = (ListView) findView("R.id.qg_payType_lv");
		if (payTypes!=null&&payTypes.size()!=0&&!QGSdkUtils.getString(mActivity,"payType").equals("")){

			payType=payTypes.get(0).getPaytypeid();

			Log.d("hermesgame","payType initView "+payType);
		}
		removeNewType(payTypes);
		qgPayListAdapter=new QGPayListAdapter(mActivity, payTypes, QGPayManager.getInstance().getOrderInfo().getAmount());
		listView.setAdapter(qgPayListAdapter);

		qg_goods_name.setText(getString("R.string.qg_pay_goodsname")+": "+QGPayManager.getInstance().getOrderInfo().getOrderSubject());
		qg_amount.setText(QGPayManager.getInstance().getOrderInfo().getAmount());
		qg_real_amount.setText("¥ " +QGPayManager.getInstance().getOrderInfo().getAmount());
		//获取默认支付方式 存在shared里面的  没有默认方式 则 取支付列表首位
		try{
			tempPayTypes=Integer.valueOf(QGSdkUtils.getString(mActivity,"payType"));
			if(!isHasPayTypeID(payTypes,tempPayTypes)){
				tempPayTypes=0;
				payType=payTypes.get(0).getPaytypeid();
				Log.d("hermesgame","!isHasPayTypeID ");
			}else{
				payType=tempPayTypes;
				if (payType==173&&(double)info.getSdkCoinNum()< Double.valueOf(QGPayManager.getInstance().getOrderInfo().getAmount())){
					payType=payTypes.get(0).getPaytypeid();
				}
			}
		}catch(Exception e){
			tempPayTypes=0;
			payType=payTypes.get(0).getPaytypeid();
			Log.e("hermesgame","!isHasPayTypeID   Exception ");
		}
		//设置默认折扣价 和 折扣值
		if(tempPayTypes==0){
			qg_pay_way.setText(getString("R.string.qg_pay_paytype")+":   "+payTypes.get(0).getPayname());
			if(payTypes.get(0).getRebate().getRate().isEmpty()||payTypes.get(0).getRebate().getRateval().toString().equals("10折")){
				qg_real_amount.setText("¥ " +QGPayManager.getInstance().getOrderInfo().getAmount());
				qg_rabate.setVisibility(GONE);
			}else{
				Log.e("hermesgame","hermesgame 2");
				BigDecimal a = new BigDecimal(payTypes.get(0).getRebate().getRate());
				BigDecimal b = new BigDecimal(QGPayManager.getInstance().getOrderInfo().getAmount());

				if (payTypes.get(0).getRebate().getRateval().isEmpty()){
					qg_rabate.setVisibility(GONE);
				}else{

					qg_rabate.setVisibility(View.VISIBLE);
					qg_rabate.setText(getString("R.string.qg_pay_rate")+" :   "+payTypes.get(0).getRebate().getRateval());
				}
				qg_real_amount.setText("¥ " + a.multiply(b));
			}
		}else{
			for(int i=0;i<payTypes.size();i++){
				if(payTypes.get(i).getPaytypeid()==tempPayTypes){
					qg_pay_way.setText(getString("R.string.qg_pay_paytype")+":   "+payTypes.get(i).getPayname());

					if(payTypes.get(i).getRebate().getRate().isEmpty()||payTypes.get(i).getRebate().getRateval().toString().equals("10折")){
						qg_real_amount.setText("¥ " +QGPayManager.getInstance().getOrderInfo().getAmount());
						qg_rabate.setVisibility(GONE);

					}else{
						BigDecimal a = new BigDecimal(payTypes.get(i).getRebate().getRate());
						BigDecimal b = new BigDecimal(QGPayManager.getInstance().getOrderInfo().getAmount());
						if (payTypes.get(0).getRebate().getRateval().isEmpty()){
							qg_rabate.setVisibility(GONE);
						}else{
							qg_rabate.setVisibility(View.VISIBLE);

							qg_rabate.setText(getString("R.string.qg_pay_rate")+" :   "+payTypes.get(i).getRebate().getRateval());
						}
						qg_real_amount.setText("¥ " + a.multiply(b));
					}
				}
			}
		}

		listView.setOnItemClickListener(new OnItemClickListener() {
			QGPayListAdapter.Item tempItem;

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				//QGPayListAdapter.Item item = (QGPayListAdapter.Item) view.getTag();
				qgPayListAdapter.tempItem.qg_payType_check.setBackgroundResource(QGSdkUtils.getResId(mActivity, "R.drawable.qg_choose_pay_way_null"));
				if (tempItem != null) {
					tempItem.qg_payType_check.setBackgroundResource(QGSdkUtils.getResId(mActivity, "R.drawable.qg_choose_pay_way_null"));
					ischecked = false;
				}

				QGPayListAdapter.Item item = (QGPayListAdapter.Item) view.getTag();
				tempItem = item;
				qg_pay_way.setText(getString("R.string.qg_pay_paytype")+":   "+tempItem.qg_payType_name.getText());

				if (ischecked) {
					ischecked = false;
					payType = 999;
					item.qg_payType_check.setBackgroundResource(QGSdkUtils.getResId(mActivity, "R.drawable.qg_choose_pay_way_null"));

				} else {
					ischecked = true;
					payType = payTypes.get(position).getPaytypeid();
					item.qg_payType_check.setBackgroundResource(QGSdkUtils.getResId(mActivity, "R.drawable.qg_choose_pay_way_selected"));
				}

				if (tempItem.payRate2.isEmpty()) {
					qg_rabate.setVisibility(GONE);
					qg_real_amount.setText("¥ " +(Double.valueOf(QGPayManager.getInstance().getOrderInfo().getAmount())-Double.valueOf(QGPayManager.getInstance().getVoucherAmount())));
				} else {
					BigDecimal a = new BigDecimal(tempItem.payRate2);
					BigDecimal b = new BigDecimal(QGPayManager.getInstance().getOrderInfo().getAmount());
					qg_rabate.setText(getString("R.string.qg_pay_rate")+" :   "+tempItem.qg_payRate_num.getText());
					// qg_real_amount.setText("¥ " + a.multiply(b));
					//qg_real_amount.setText("¥ " +(-Double.valueOf(QGPayManager.getInstance().getVoucherAmount())));
					qg_real_amount.setText("¥ " + 	(a.multiply(b).subtract(new BigDecimal(QGPayManager.getInstance().getVoucherAmount()))));
					qg_rabate.setVisibility(View.VISIBLE);
				}

			}
		});

		startPay.setOnClickListener(listener);
		qg_pay_cancle_button.setOnClickListener(listener);
		qg_voucher_jump_layout.setOnClickListener(listener);
		// 设置商品名和总价


	}

	@Override
	public void onClicks(int id) {
		setMyActivity(this.getActivity());
		if (id == startPay.getId()) {
			startPay.setEnabled(false);
			payStatus = 1;
			Log.d("hermesgame", "payType： " + payType);
			if (payType == 999) {
				Toast.makeText(mActivity, "请选择支付方式!", Toast.LENGTH_SHORT).show();
			} else if(payType == 173){
				QGSdkUtils.saveString(mActivity,"payType",String.valueOf(payType));
				Intent intent=new Intent(mActivity, QGSdkCoinsPayActivity.class);
				mActivity.startActivityForResult(intent,10001);
			} else{
				QGSdkUtils.saveString(mActivity,"payType",String.valueOf(payType));
				QGPayManager.getInstance().createOrder(mActivity, payType);
			}

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					startPay.setEnabled(true);
				}
			}, 1500);


		}else if (id==qg_pay_cancle_button.getId()){
			mActivity.finish();
		}
		else if (id==qg_voucher_jump_layout.getId()){

			if (vouchersList!=null&&vouchersList.size()!=0){
				QGPayManager.getInstance().startVoucherActivity(getActivity(), new QGCallBack() {
					@Override
					public void onSuccess() {
						Log.d("hermesgame","onSuccess: ");
						qg_voucher_content.setText(getString("R.string.qg_have_voucher"));
						qg_voucher_content.setTextColor(Color.parseColor("#FF774e"));
						QGPayManager.getInstance().setVoucherAmount("0");
						QGPayManager.getInstance().setVoucherCode("");
						qg_real_amount.setText("¥ " +QGPayManager.getInstance().getOrderInfo().getAmount());
					}

					@Override
					public void onFailed(String msg) {

						String a[]=msg.split("QG");
						final String code=a[0];
						final String amount=a[1];
						Log.e("hermesgame","onFailed: "+code+"   amount: "+amount);
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								qg_voucher_content.setText(amount+getString("R.string.qg_voucher_desc"));
								qg_voucher_content.setTextColor(Color.parseColor("#FF774e"));
								qg_real_amount.setText("¥ " +(Double.valueOf(QGPayManager.getInstance().getOrderInfo().getAmount())-Double.valueOf(amount)));
								QGPayManager.getInstance().setVoucherAmount(amount);
								QGPayManager.getInstance().setVoucherCode(code);
							}
						});
					}
				});
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (payStatus != 1) {
			QGPayManager.getInstance().mPayCallBack.onFailed("取消支付");
		}
		QGPayManager.instance = null;
	}

	public boolean isHasPayTypeID(List<Paytypes>list ,int id){
		boolean isExist=false;
		if (list!=null&&list.size()!=0){
			for (int i=0;i<list.size();i++){
				if (list.get(i).getPaytypeid()==id){
					isExist=true;
				}
			}
			return isExist;
		}else {
			return false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	public void removeNewType( List<Paytypes> mPayTypes){
		removedPayTypes=new ArrayList<Paytypes>();
		for (int i=0;i<mPayTypes.size();i++){
			for (int x=0;x<typeArray.length;x++){
				if (typeArray[x]==mPayTypes.get(i).getPaytypeid()){
					removedPayTypes.add(mPayTypes.get(i));
				}
			}
		}
		payTypes=removedPayTypes;
	}

	public boolean checkVoucher(List<WalletInfo.Vouchers> list){
		if (list==null||list.size()==0){
			return false;
		}else{
			boolean isHave=false;
			for (int i=0;i<list.size();i++){
				if (list.get(i).getIsViable()==1){
					isHave=true;
				}
			}
			Log.d("hermesgame","checkVoucher: "+isHave );
			return isHave;
		}

	}
}
