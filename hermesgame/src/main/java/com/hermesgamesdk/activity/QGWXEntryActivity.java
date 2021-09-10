package com.hermesgamesdk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.hermesgamesdk.manager.QGPayManager;
import com.hermesgamesdk.manager.ThirdManager;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class QGWXEntryActivity extends Activity implements IWXAPIEventHandler {
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IWXAPI api = ThirdManager.getInstance().getIWXAPI();
        if (api != null)
            api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
    	 int errorCode = resp.errCode;
    	Log.e("hermesgame.WXonResp", ""+errorCode+"  type :"+resp.getType());

        if (resp.getType()== ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){
            this.finish();
        }
        if(resp.getType()==ConstantsAPI.COMMAND_PAY_BY_WX){
            Log.d("hermesgame.WXonRes","onPayFinish,errCode="+resp.errCode);
            if (resp.errCode==0){
                QGPayManager.getInstance().mPayCallBack.onSuccess();
            }else {
                QGPayManager.getInstance().mPayCallBack.onFailed("支付失败");
            }

            this.finish();
            QGPayManager.getInstance().mActivity.finish();
        }
         switch (errorCode) {
         case BaseResp.ErrCode.ERR_OK:
             //用户同意
             String code = ((SendAuth.Resp) resp).code;
             ThirdManager.getInstance().onWxGranted(code);
             break;
         case BaseResp.ErrCode.ERR_AUTH_DENIED:
             //用户拒绝
             break;
         case BaseResp.ErrCode.ERR_USER_CANCEL:
             //用户取消
             break;
         default:
             break;
         }
         finish();
    }
}
