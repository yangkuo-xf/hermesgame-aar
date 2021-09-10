package com.hermesgamesdk.fragment.login;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.QGManager;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.view.AddAccountDialog;
import com.hermesgamesdk.view.SwitchAccountAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SwitchAccountFragment extends BaseFragment {
   private TextView mainUid,qg_add_account,qg_main_account_enter;
    private ListView accountList;
    private List<QGUserInfo.BindUsers> bindUsers;
    private SwitchAccountAdapter adapter;

    @Override
    protected String getRootName() {
        return "R.layout.qg_switch_account_fragment";
    }

    @Override
    protected String getTitle() {
        return "R.string.qg_choose_littleaccount";
    }

    @Override
    protected void onRootViewInflated(View root) {
        initViews(root);

    }
    public void initViews(View root){
        mainUid=(TextView) findView("R.id.qg_switch_account_main_uid");
        qg_add_account=(TextView) findView("R.id.qg_add_account");
        qg_main_account_enter=(TextView) findView("R.id.qg_main_account_enter");
        mainUid.setText(QGManager.getUserName());
        accountList=(ListView)findView("R.id.qg_switch_account_listview");
        bindUsers=((QGUserInfo)DataManager.getInstance().getData(Constant.USERINFO_KEY)).getUserdata().getBindUsers();
        if (bindUsers!=null&&bindUsers.size()!=0){
            adapter=new SwitchAccountAdapter(mActivity,bindUsers);
            accountList.setAdapter(adapter);
        }

        mTitleBar.hideBackIcon();
        mTitleBar.hideCloseIcon();
        accountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //记录上次选择
                QGSdkUtils.saveString(mActivity,"lastChooseUid",bindUsers.get(position).getUid());
                QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
                info.setAuthtoken(bindUsers.get(position).getAuthToken());
                info.getUserdata().setUid(bindUsers.get(position).getUid());
                info.getUserdata().setUsername(bindUsers.get(position).getUsername());
                info.getUserdata().setToken(bindUsers.get(position).getAuthToken());
             //   saveAccountInfo(bindUsers.get(position).getUid(),bindUsers.get(position).getAuthToken());
                mActivity.finish();
            }
        });

        qg_add_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAccountDialog dialog=new AddAccountDialog(getActivity()) {
                    @Override
                    public void onDismiss(String s) {
                        Log.d("hermesgame","mDefaultUsername: "+s);
                        addAccount(getActivity(),s);
                    }
                };
                dialog.show();
            }
        });
        qg_main_account_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }


    public void  addAccount(Context ctx , String username){
        String param = new QGParameter(ctx).addParameter("username", username).addParameter("password",username).create();
        HttpRequest<String > requestPay = new HttpRequest<String>(){
            @Override
            public void onSuccess(String bean) {
                Log.d("hermesgame","addAccount: "+bean);
                try{

                    JSONObject jsonObject=new JSONObject(bean);
                    QGUserInfo.BindUsers newUser=new QGUserInfo.BindUsers();
                    newUser.setAuthToken(jsonObject.getString("authToken"));
                    newUser.setUid(jsonObject.getString("uid"));
                    newUser.setUsername(jsonObject.getString("username"));
                    if (bindUsers==null||bindUsers.size()==0){
                        bindUsers=new ArrayList<QGUserInfo.BindUsers>();
                        bindUsers.add(newUser);
                        adapter=new SwitchAccountAdapter(mActivity,bindUsers);
                        accountList.setAdapter(adapter);
                    }else{
                        bindUsers.add(newUser);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }catch (Exception e){
                    Log.d("hermesgame","addAccount: "+e.toString());
                }


            }

            @Override
            public void onFailed(final int code, final String message) {
                Log.e("hermesgame","checkSDKCoins Failed   code: "+code+"    msg:"+message);

            }
        }.addParameter(param).post().setUrl(Constant.HOST + Constant.ADD_ACCOUNT);
        DataManager.getInstance().requestHttp(requestPay);
    }

}
