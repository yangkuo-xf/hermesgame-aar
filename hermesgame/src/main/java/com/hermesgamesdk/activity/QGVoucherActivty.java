package com.hermesgamesdk.activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.WalletInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.QGPayManager;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.VoucherAdapter;

import java.util.List;

public class QGVoucherActivty extends FragmentActivity {
    GridView voucher;
    List<WalletInfo.Vouchers> vouchersList;
    public int lastPosition=0;
    VoucherAdapter adapter;
    ImageView close;
    int isClicked=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(QGSdkUtils.getResId(this,"R.layout.qg_voucher_list"));
        vouchersList= ((WalletInfo) DataManager.getInstance().getData(Constant.WALLET_KEY)).getVouchers();
        voucher=findViewById(QGSdkUtils.getResId(this,"R.id.qg_voucher_grid"));
        close=findViewById(QGSdkUtils.getResId(this,"R.id.qg_voucher_cancle_button"));
        adapter=new VoucherAdapter(this,vouchersList);
        voucher.setAdapter(adapter);
        voucher.setSelector(new ColorDrawable(Color.TRANSPARENT));
        voucher.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (vouchersList.get(position).getIsViable()==0){
                    return;
                }
                if(position==lastPosition){
                    if (isClicked==0){
                        VoucherAdapter.ViewHolder holder=( VoucherAdapter.ViewHolder )view.getTag();
                        holder.check.setVisibility(View.GONE);
                        isClicked=1;

                    }else {
                        VoucherAdapter.ViewHolder holder=( VoucherAdapter.ViewHolder )view.getTag();
                        holder.check.setVisibility(View.VISIBLE);
                        isClicked=0;

                    }
                }else if (lastPosition!=position){
                    isClicked=0;
                    VoucherAdapter.ViewHolder lastHolder=( VoucherAdapter.ViewHolder )adapter.getItem(lastPosition);
                    lastHolder.check.setVisibility(View.GONE);
                    VoucherAdapter.ViewHolder holder=( VoucherAdapter.ViewHolder )view.getTag();
                    holder.check.setVisibility(View.VISIBLE);

                }
                lastPosition=position;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked==0){
                    Intent result=new Intent();
                    result.putExtra("id",vouchersList.get(lastPosition).getCode());
                    result.putExtra("amount",vouchersList.get(lastPosition).getAmount());
                    setResult(QGPayManager.VOUCHER_SUCCESS,result);
                }else{
                    setResult(QGPayManager.VOUCHER_FAIL);
                }
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isClicked==0){
            Intent result=new Intent();
            result.putExtra("id",vouchersList.get(lastPosition).getCode());
            result.putExtra("amount",vouchersList.get(lastPosition).getAmount());
            setResult(QGPayManager.VOUCHER_SUCCESS,result);
        }else{
            setResult(QGPayManager.VOUCHER_FAIL);
        }
        super.onBackPressed();
    }
}
