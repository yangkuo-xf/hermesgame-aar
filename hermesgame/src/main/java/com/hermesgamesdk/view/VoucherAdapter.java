package com.hermesgamesdk.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hermesgamesdk.entity.WalletInfo;
import com.hermesgamesdk.utils.QGSdkUtils;

import java.util.ArrayList;
import java.util.List;

public class VoucherAdapter extends BaseAdapter {
    private List<WalletInfo.Vouchers> vouchersList;
    private Activity mActivity;
    public List<ViewHolder> holderList=new ArrayList<ViewHolder>();
    public VoucherAdapter(Activity activity, List<WalletInfo.Vouchers> mVouchers){
        vouchersList=mVouchers;
        mActivity=activity;
    }
    @Override
    public int getCount() {
        return vouchersList.size();
    }

    @Override
    public Object getItem(int position) {
        return holderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView == null) {
            holder=new ViewHolder();
            // 获得组件，实例化组件
            convertView = LayoutInflater.from(mActivity).inflate(QGSdkUtils.getResId(mActivity, "R.layout.qg_voucher_item"), parent,
                    false);
            holder.check = (ImageView) convertView.findViewById(QGSdkUtils.getResId(mActivity,
                    "R.id.qg_voucher_item_check"));
            holder.amount = (TextView) convertView.findViewById(QGSdkUtils.getResId(mActivity,
                    "R.id.qg_voucher_item_amount"));
            holder.timelimit = (TextView) convertView.findViewById(QGSdkUtils.getResId(mActivity,
                    "R.id.qg_voucher_item_timelimit"));
            holder.desc = (TextView) convertView.findViewById(QGSdkUtils.getResId(mActivity,
                    "R.id.qg_voucher_item_desc"));
            holder.qg_voucher_item_bg = (RelativeLayout) convertView.findViewById(QGSdkUtils.getResId(mActivity,
                    "R.id.qg_voucher_item_bg"));

            holder.amount.setText("¥"+vouchersList.get(position).getAmount());
            holder.desc.setText(vouchersList.get(position).getLimitDesc());
            holder.timelimit.setText("有效期至: "+QGSdkUtils.stampToDate(vouchersList.get(position).getEtimeUnix()));
            if (position==0&&vouchersList.get(position).getIsViable()==1){
                holder.check.setVisibility(View.VISIBLE);
            }
            if (vouchersList.get(position).getIsViable()==0){
                Log.e("hermesgame","mActivity.getRequestedOrientation()： "+mActivity.getRequestedOrientation());
                if (mActivity.getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                    holder.qg_voucher_item_bg.setBackgroundResource(QGSdkUtils.getResId(mActivity,"R.drawable.qg_voucher_item_background_dissable_por"));
                }else if (mActivity.getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    holder.qg_voucher_item_bg.setBackgroundResource(QGSdkUtils.getResId(mActivity,
                            "R.drawable.qg_voucher_item_background_dissable"));
                }

            }
            convertView.setTag(holder);
            holderList.add(holder);
        } else {
            holder =(ViewHolder) convertView.getTag();
        }

      return  convertView;
    }
    public static class ViewHolder{
        public ImageView check;
        public TextView amount,timelimit,desc;
        public RelativeLayout qg_voucher_item_bg;
    }


}
