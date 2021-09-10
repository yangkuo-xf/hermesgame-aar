package com.hermesgamesdk.view;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.InitData.Paytypes;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.QGPayManager;
import com.hermesgamesdk.utils.QGSdkUtils;

public class QGPayListAdapter extends BaseAdapter {
	private List<Paytypes> payTypes;
	private Context context;
	private LayoutInflater layoutInflater;
	private String amount;
	private String payRate = "";
	public int normalPayType = 0;
	public int normalPosition = 0;
	public int x = 0;
	public Item tempItem;

	QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
	public QGPayListAdapter(Context context, List<Paytypes> payTypes, String amount) {
		this.context = context;
		this.payTypes = payTypes;
		this.layoutInflater = LayoutInflater.from(context);
		this.amount = amount;
	}

	/**
	 * 移除不支持的新支付方式
	 */

	public static class Item {
		public ImageView qg_payType_icon;
		public TextView qg_payType_name;
		public ImageView qg_payType_check;
		public ImageView qg_payRate_img;
		public TextView qg_payRate_num;
		public TextView qg_sdkcoinsNum;
		public String payRate2;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return payTypes.size();
	}
	//设置余额为0不能选择
	@Override
	public boolean isEnabled(int position) {
		if(payTypes.get(position).getPaytypeid()==173){
			if (info.getSdkCoinNum()==0||(double)info.getSdkCoinNum()< Double.valueOf(QGPayManager.getInstance().getOrderInfo().getAmount())){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}

	@Override
	public Object getItem(int pos) {
		return payTypes.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Item item = null;

		if (convertView == null) {
			item = new Item();
			// 获得组件，实例化组件
			convertView = layoutInflater.inflate(QGSdkUtils.getResId(context, "R.layout.qg_paylist_item"), parent,
					false);
			item.qg_payType_icon = (ImageView) convertView.findViewById(QGSdkUtils.getResId(context,
					"R.id.qg_payType_icon"));
			item.qg_payType_name = (TextView) convertView.findViewById(QGSdkUtils.getResId(context,
					"R.id.qg_payType_name"));
			item.qg_payType_check = (ImageView) convertView.findViewById(QGSdkUtils.getResId(context,
					"R.id.qg_payType_check"));
			item.qg_payRate_img = (ImageView) convertView.findViewById(QGSdkUtils.getResId(context,
					"R.id.qg_payRate_img"));
			item.qg_payRate_num = (TextView) convertView.findViewById(QGSdkUtils.getResId(context,
					"R.id.qg_payRate_num"));
			item.qg_sdkcoinsNum=(TextView) convertView.findViewById(QGSdkUtils.getResId(context,
					"R.id.qg_sdkcoinsNum"));
			item.payRate2 = "";
			convertView.setTag(item);
		} else {
			item = (Item) convertView.getTag();
		}
		// 绑定数据
		if (payTypes.get(position).getPaytypeid() == 1 || payTypes.get(position).getPaytypeid() == 2
				|| payTypes.get(position).getPaytypeid() == 165 || payTypes.get(position).getPaytypeid() == 167
				|| payTypes.get(position).getPaytypeid() == 180 || payTypes.get(position).getPaytypeid() == 181
				|| payTypes.get(position).getPaytypeid() == 182 || payTypes.get(position).getPaytypeid() == 194
				|| payTypes.get(position).getPaytypeid() == 208|| payTypes.get(position).getPaytypeid() == 221
				|| payTypes.get(position).getPaytypeid() == 223|| payTypes.get(position).getPaytypeid() == 225
				|| payTypes.get(position).getPaytypeid() == 228) {
			item.qg_payType_icon.setBackgroundResource(QGSdkUtils.getResId(context, "R.drawable.qg_pay_way_ali"));
		} else if (payTypes.get(position).getPaytypeid() == 184) {
			// 爱贝logo
			item.qg_payType_icon.setBackgroundResource(QGSdkUtils.getResId(context, "R.drawable.qg_pay_way_aibei"));
		} else if (payTypes.get(position).getPaytypeid() == 214) {
			// 天谕logo
			item.qg_payType_icon.setBackgroundResource(QGSdkUtils.getResId(context, "R.drawable.qg_pay_way_tianyu"));
		} else if (payTypes.get(position).getPaytypeid() == 220) {
			// 疯狂体育logo
			item.qg_payType_icon.setBackgroundResource(QGSdkUtils.getResId(context, "R.drawable.qg_pay_way_fkty"));
		}
		else if (payTypes.get(position).getPaytypeid() == 173) {
			// SDK 官方平台币
			item.qg_payType_icon.setBackgroundResource(QGSdkUtils.getResId(context, "R.drawable.qg_pay_way_sdkcoins"));

			try{

				item.qg_sdkcoinsNum.setVisibility(View.VISIBLE);
				item.qg_sdkcoinsNum.setText(" (¥"+info.getSdkCoinNum()+")");
				if(info.getSdkCoinNum()==0||(double)info.getSdkCoinNum()< Double.valueOf(QGPayManager.getInstance().getOrderInfo().getAmount())){

					item.qg_payType_check.setEnabled(false);
				}
			}catch (Exception e){
				Log.e("hermesgame","setCoins UI Erro: "+e.toString());
			}

		}else {
			item.qg_payType_icon.setBackgroundResource(QGSdkUtils.getResId(context, "R.drawable.qg_pay_way_wechat"));
		}
		if (payTypes != null && payTypes.size() > 0) {
			try {
				normalPayType = Integer.valueOf(QGSdkUtils.getString(context, "payType"));
				Log.e("QGPayListAdapter", "normalPayType : " + normalPayType);

				if (!isHasPayTypeID(payTypes,normalPayType)||isSDKCOinNotEnought(normalPayType)) {
					normalPayType = 0;
				}
				Log.e("QGPayListAdapter", "getNormalPayType Line153: " + normalPayType);
			} catch (Exception e) {
				Log.e("QGPayListAdapter", "getNormalPayType Exception: " + e.toString());
				normalPayType = 0;
			}
			if (normalPayType == 0 && position == 0) {
				Log.e("QGPayListAdapter", "getNormalPayType normalPayType: " + 0);
				tempItem = item;
				item.qg_payType_check.setBackgroundResource(QGSdkUtils.getResId(context,
						"R.drawable.qg_choose_pay_way_selected"));
			} else {
				if (payTypes.get(position).getPaytypeid() == normalPayType) {
					item.qg_payType_check.setBackgroundResource(QGSdkUtils.getResId(context,
							"R.drawable.qg_choose_pay_way_selected"));
					tempItem = item;
				}
			}
		}

		item.qg_payType_name.setText(payTypes.get(position).getPayname());
		if (payTypes.get(position).getRebate().getRateConfig()!=null&&payTypes.get(position).getRebate().getRateConfig().size() != 0) {
			Log.e("qg.pay", "getRateConfig !=null");
			for (int i = 0; i < payTypes.get(position).getRebate().getRateConfig().size(); i++) {
				String min = payTypes.get(position).getRebate().getRateConfig().get(i).getMinval();
				String max = payTypes.get(position).getRebate().getRateConfig().get(i).getMaxval();
				if (Double.valueOf(amount) >= Double.valueOf(min) && Double.valueOf(max) >= Double.valueOf(amount)
						&& Double.valueOf(payTypes.get(position).getRebate().getRateConfig().get(i).getRate()) > 0
						&& Double.valueOf(payTypes.get(position).getRebate().getRateConfig().get(i).getRate()) < 1) {
					payRate = payTypes.get(position).getRebate().getRateConfig().get(i).getRateval();
					item.payRate2 = payTypes.get(position).getRebate().getRateConfig().get(i).getRate();
					item.qg_payRate_img.setVisibility(View.VISIBLE);
					item.qg_payRate_num.setVisibility(View.VISIBLE);
					item.qg_payRate_num.setText(payRate);
					break;
				} else {

				}
			}
			if (payRate.isEmpty() && Double.valueOf(payTypes.get(position).getRebate().getRate()) > 0
					&& Double.valueOf(payTypes.get(position).getRebate().getRate()) < 1) {
				payRate = payTypes.get(position).getRebate().getRateval();
				item.payRate2 = payTypes.get(position).getRebate().getRate();
				item.qg_payRate_img.setVisibility(View.VISIBLE);
				item.qg_payRate_num.setVisibility(View.VISIBLE);
				item.qg_payRate_num.setText(payRate);
			}

		} else if (Double.valueOf(payTypes.get(position).getRebate().getRate()) > 0
				&& Double.valueOf(payTypes.get(position).getRebate().getRate()) < 1) {
			payRate = payTypes.get(position).getRebate().getRateval();
			item.payRate2 = payTypes.get(position).getRebate().getRate();
			item.qg_payRate_img.setVisibility(View.VISIBLE);
			item.qg_payRate_num.setVisibility(View.VISIBLE);
			item.qg_payRate_num.setText(payRate);
		}
		return convertView;
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
	public boolean isSDKCOinNotEnought(int id){
		if (id==173&&(info.getSdkCoinNum()==0||info.getSdkCoinNum()<Integer.valueOf(QGPayManager.getInstance().getOrderInfo().getAmount()))){
				return true;
		}else{
			return false;
		}
	}


}
