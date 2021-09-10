package com.hermesgamesdk.view;

import android.content.Context;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.InitManager;
import com.hermesgamesdk.utils.QGSdkUtils;

import org.json.JSONObject;

import java.util.List;

public class SwitchAccountAdapter extends BaseAdapter {
    private List<QGUserInfo.BindUsers> bindUsers;
    private Context context;
    private LayoutInflater layoutInflater;

    public SwitchAccountAdapter(Context context, List<QGUserInfo.BindUsers> bindUsers) {
        this.context = context;
        this.bindUsers = bindUsers;
        this.layoutInflater = LayoutInflater.from(context);
    }
    public static class Item {
        public TextView switch_account_uid;
        public TextView norole;
        public ImageView switch_account_lastchoose;
        public ImageView switchaccount_rolelogo_bg;
        public TextView switch_account_lv;
        public TextView switch_account_sname;
        public TextView switch_account_rname;
        public Button switch_account_btn_choose;
        public LinearLayout switch_account_roleinfo_layout;

    }
    @Override
    public int getCount() {
        return bindUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return bindUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = null;

        if (convertView == null) {
            item = new Item();
            // 获得组件，实例化组件
            convertView = layoutInflater.inflate(QGSdkUtils.getResId(context, "R.layout.qg_switch_account_list_item"), parent,
                    false);
            item.switch_account_btn_choose = (Button) convertView.findViewById(QGSdkUtils.getResId(context,
                    "R.id.switch_account_btn_choose"));
            item.norole = (TextView) convertView.findViewById(QGSdkUtils.getResId(context,
                    "R.id.switch_account_norole"));
            item.switch_account_lastchoose = (ImageView) convertView.findViewById(QGSdkUtils.getResId(context,
                    "R.id.switch_account_lastchoose"));
            item.switch_account_lv = (TextView) convertView.findViewById(QGSdkUtils.getResId(context,
                    "R.id.switch_account_lv"));
            item.switch_account_rname = (TextView) convertView.findViewById(QGSdkUtils.getResId(context,
                    "R.id.switch_account_rname"));
            item.switch_account_sname = (TextView) convertView.findViewById(QGSdkUtils.getResId(context,
                    "R.id.switch_account_sname"));
            item.switch_account_uid=(TextView) convertView.findViewById(QGSdkUtils.getResId(context,
                    "R.id.switch_account_uid"));
            item.switch_account_roleinfo_layout=(LinearLayout) convertView.findViewById(QGSdkUtils.getResId(context,
                    "R.id.switch_account_roleinfo_layout"));
            item.switchaccount_rolelogo_bg=(ImageView) convertView.findViewById(QGSdkUtils.getResId(context,
                    "R.id.switchaccount_rolelogo_bg"));

            convertView.setTag(item);
        } else {
            item = (Item) convertView.getTag();
        }
        if (bindUsers.size()!=0){
            item.switch_account_uid.setText(bindUsers.get(position).getUsername());
        }
        if (InitManager.getInstance().useAvatar) {
            Uri uri = Uri.parse(InitManager.getInstance().cpuseAvatarPath);

            item.switchaccount_rolelogo_bg.setImageURI(uri);
        }
        if (bindUsers.get(position).getRoleName()!=null&&!bindUsers.get(position).getRoleName().equals("")){
            item.switch_account_roleinfo_layout.setVisibility(View.VISIBLE);
            item.switch_account_lv.setText(bindUsers.get(position).getLevel());
            item.switch_account_sname.setText(bindUsers.get(position).getServerName());
            item.switch_account_rname.setText(bindUsers.get(position).getRoleName());
        }

        if (!QGSdkUtils.getString(context, bindUsers.get(position).getUid()).equals("")){
            try {
                    JSONObject roleInfo=new JSONObject(QGSdkUtils.getString(context,bindUsers.get(position).getUid()));
                         item.norole.setVisibility(View.GONE);
                   // item.switch_account_roleinfo_layout.setVisibility(View.VISIBLE);
                    item.switch_account_lv.setText(roleInfo.getString("gameRoleLevel"));
                    item.switch_account_sname.setText(roleInfo.getString("serverName"));
                    item.switch_account_rname.setText(roleInfo.getString("gameRoleName"));

            }catch (Exception e){
                Log.e("hermesgame","parse role info erro :"+e.toString());
            }
        }else{

            Log.e("hermesgame"," role info empty ");
        }
        if (!QGSdkUtils.getString(context,"lastChooseUid").equals("")){
            if (bindUsers.get(position).getUid().equals(QGSdkUtils.getString(context,"lastChooseUid"))){
                item.switch_account_lastchoose.setVisibility(View.VISIBLE);
            }else{
                item.switch_account_lastchoose.setVisibility(View.GONE);
            }
        }
        return convertView;
    }
}
