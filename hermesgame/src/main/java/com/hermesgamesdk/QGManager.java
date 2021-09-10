package com.hermesgamesdk;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.applog.GameReportHelper;
import com.bytedance.applog.WhalerGameHelper;
import com.hermesgamesdk.activity.GameSliderBarActivityV2;
import com.hermesgamesdk.activity.TempActivty;
import com.hermesgamesdk.callback.QGCallBack;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.QGOrderInfo;
import com.hermesgamesdk.entity.QGRoleInfo;
import com.hermesgamesdk.entity.QGUserExtraInfo;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.ExitManager;
import com.hermesgamesdk.manager.InitManager;
import com.hermesgamesdk.manager.LoginManager;
import com.hermesgamesdk.manager.QGFloatViewManager;
import com.hermesgamesdk.manager.QGPayManager;
import com.hermesgamesdk.manager.SliderBarV2Manager;
import com.hermesgamesdk.skin.manager.loader.SkinManager;
import com.hermesgamesdk.utils.MiitHelper;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.quickjoy.adplus.ADP;

/**
 * Created by Administrator on 2017/9/1.
 */
// TODO 加注释
public class QGManager {
	private static MiitHelper miit;

	/**
	 * 设置注销监听，此方法需在初始化之前调用
	 */
	public static void setLogoutCallback(QGCallBack callBack) {
		LoginManager.getInstance().setLogoutCallback(callBack);
	}

	public static void initMsa(Application application) {
		try {

			miit = new MiitHelper(new MiitHelper.AppIdsUpdater() {

				@Override
				public void OnIdsAvalid(String oaid, String vaid, String aaid) {
					// TODO Auto-generated method stub
					Log.d("hermesgame", "oaid: " + oaid + "   vaid: " + vaid + "   aaid:" + aaid);
					Constant.OAID = oaid;
					Constant.AAID = aaid;
					Constant.VAID = vaid;
				}
			});
			miit.getDeviceIds(application);
			SkinManager.getInstance().init(application);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("hermesgame", "init MSA  Erro: " + e.toString());
			e.printStackTrace();
		}
	}

	public static void init(final Activity activity,String appId, final String productId, final QGCallBack callBack) {
		try {

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (QGSdkUtils.isNetworkAvailable(activity)){
						InitManager.getInstance().init(activity,appId, productId, callBack);
					}else{
						callBack.onFailed("net erro");
					}

				}
			});

		} catch (Exception e) {

		}

	}

	public static void login(final Activity activity, final QGCallBack callBack) {
		try {

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					LoginManager.getInstance().login(activity, callBack);
				}
			});

		} catch (Exception e) {
			Log.e("hermesgame", "login Exception: " + e.toString());
		}
	}

	public static void slienceLogin(final Activity activity, final QGCallBack callBack) {
		try {

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					LoginManager.getInstance().slienceLogin(activity, callBack);
				}
			});

		} catch (Exception e) {
			Log.e("hermesgame", "slienceLogin Exception: " + e.toString());
		}
	}

	public static String getChannelId() {
		return Constant.CHANNEL_ID;
	}

	public static String getUID() {
		QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		if (info == null) {
			return "";
		}
		if (info.getUserdata() == null) {
			return "";
		}
		return info.getUserdata().getUid();
	}

	public static String getUserName() {
		QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		if (info == null) {
			return "";
		}
		if (info.getUserdata() == null) {
			return "";
		}
		return info.getUserdata().getUsername();
	}

	public static int getAge() {

		try{
			QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
			if (info == null) {
				return 0;
			}
			if (info.getUserdata() == null) {
				return 0;
			}
			return info.getuAge();
		}catch (Exception e){
			return 0;
		}
	}

	public static boolean getRealName() {
		try{
			int flag = ((QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY)).getCheckrealname();
			if (flag == 0) {
				return true;
			} else {
				return false;
			}
		}catch (Exception e){
			return true;
		}

	}


	public static String getLoginToken() {
		QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);

		if (info == null) {
			return "";
		}
		if (info.getUserdata() == null) {
			return "";
		}
		return info.getUserdata().getToken();
	}
	public static String getAuthToken() {
		QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);

		if (info == null) {
			return "";
		}
		if (info.getUserdata() == null) {
			return "";
		}
		return info.getAuthtoken();
	}

	//
	public static void pay(final Activity activity, final QGRoleInfo mRoleInfo, final QGOrderInfo mOrderInfo,
			final QGCallBack callBack) {
		try {
			if (DataManager.getInstance().getData(Constant.USERINFO_KEY) == null) {
				Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
				return;
			} else {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						QGPayManager.getInstance().showPayView(activity, mRoleInfo, mOrderInfo, callBack);
					}
				});

			}
		} catch (Exception e) {
			Log.e("hermesgame", "pay Exception: " + e.toString());
		}
	}

	public static void logout(final Activity activity) {
		try {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					LoginManager.getInstance().logout(activity);
				}
			});

		} catch (Exception e) {
			Log.e("hermesgame", "logout Exception: " + e.toString());
		}
	}

	public static boolean isBindPhone() {
		QGUserExtraInfo userExtraInfo = ((QGUserExtraInfo) DataManager.getInstance()
				.getData(Constant.USEREXTRAINFO_KEY));

		if (userExtraInfo.getIsBindPhone() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static void showBind(Activity activity) {

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("from", "slider_bind");
		intent.putExtras(bundle);
		intent.setClass(activity, TempActivty.class);
		activity.startActivityForResult(intent, 10001);
	}

	public static void showFloat(boolean isLeft) {
			QGFloatViewManager.getInstance().show(isLeft);
	}

	public static void hideFloat( ) {
			QGFloatViewManager.getInstance().hide();
	}

	public static void exit(Activity activity, QGCallBack callBack) {
		try {
			ExitManager.getInstance().exit(activity, callBack);
		} catch (Exception e) {
			Log.e("hermesgame", "exit Exception: " + e.toString());
		}
	}

	// 用于客服插件角色信息上传
	// ad插件，必须调用
	public static void setGameRoleInfo(Activity activity, QGRoleInfo roleInfo) {

		LoginManager.getInstance().setGameRoleInfo(activity, roleInfo);
		if (QGConfig.isSupportAD()) {
			ADP.getInstance().active(getUID(), getUserName(),roleInfo.getRoleId());
			  ADP.getInstance().role(getUID(), getUserName(),
			  roleInfo.getRoleId(), roleInfo.getRoleName(),
			  roleInfo.getServerName(), roleInfo.getRoleLevel(),
			  roleInfo.getVipLevel(), roleInfo.getBalance());
		}



	}

	/**
	 * 获取登录方式
	 *自动登录 0
	 * 账号登录 1
	 * 验证码登录 2
	 * qq 4
	 * 微信 5
	 * 阿里云一键授权 6
	 * taptap 7
	 */

	public static String getLoginType() {
		return LoginManager.getInstance().getLoginType();
	}

	public static void setProductCode(String productCode) {
		productCode = productCode == null ? "" : productCode;
		Constant.PRODUCT_CODE = productCode;
	}
	public static String getProductCode() {
		return Constant.PRODUCT_ID;
	}

	// AD统计接口，用于支付成功后上传支付信息，是否支付成功需要游戏请求服务端确认
	public static void adPayStatistics(String userID, String userName, String roleID, String orderID, String goodsID,
			String goodsName, float amount, String currency) {
		if (QGConfig.isSupportAD()) {
			 ADP.getInstance().pay(userID, userName, roleID, orderID, goodsID,
			 goodsName, amount, currency);
		}
	}

	public static QGUserInfo.ExtInfo getExtInfo() {
		if (DataManager.getInstance().getData(Constant.USERINFO_KEY) == null) {
			return new QGUserInfo.ExtInfo();
		}
		return ((QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY)).getExtInfo();
	}
	public static boolean isGuest(){
		QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		if(info.getUserdata().getIsGuest()==0){
			return false;
		}else{
			return true;
		}
	}

	public static String getOaid(){
		return Constant.OAID;
	}

	public static String getSDKOrderID(){
		return QGPayManager.getInstance().getOrderID();
	}
	public static void showUserCenter(Activity activity){
		Intent intent=new Intent();
		intent.setClass(activity, GameSliderBarActivityV2.class);

		activity.startActivity(intent);
		if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			activity. overridePendingTransition(QGSdkUtils.getResId(activity,"R.anim.slide_bar_in"),QGSdkUtils.getResId(activity,"R.anim.slide_bar_out"));
		} else {
			activity.overridePendingTransition(QGSdkUtils.getResId(activity,"R.anim.slide_bar_in_bottom"),QGSdkUtils.getResId(activity,"R.anim.slide_bar_out_bottom"));
		}
	}
	@Deprecated
	public static void showUserCenter(Activity activity, @Nullable String action){
		Intent intent=new Intent();
		intent.setClass(activity, GameSliderBarActivityV2.class);
		if (action!=null){
			intent.putExtra("action",action);
		}
		activity.startActivity(intent);
		if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			activity. overridePendingTransition(QGSdkUtils.getResId(activity,"R.anim.slide_bar_in"),QGSdkUtils.getResId(activity,"R.anim.slide_bar_out"));
		} else {
			activity.overridePendingTransition(QGSdkUtils.getResId(activity,"R.anim.slide_bar_in_bottom"),QGSdkUtils.getResId(activity,"R.anim.slide_bar_out_bottom"));
		}
	}
	public static void closeUserCenter(Activity activity){

		SliderBarV2Manager.getInstance(activity).destorySliderBar();
	}
	public static void getDeviceBindAccountResult(Activity activity,QGCallBack callBack){
			InitManager.getInstance().getDeviceBindAccountResult(activity,callBack);
	}

	/**
	 * 拉起taptap评论页面
	 * @param activity
	 * @param appid  taptap游戏评论页id
	 * @param callBack
	 */
	@Deprecated
	public static void launchTapTap(Activity activity,String appid,QGCallBack callBack){
		if (QGSdkUtils.isTapTap(activity)){
			String scheme="taptap://taptap.com/app?tab_name=review&app_id="+appid;
			activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(scheme)));
			callBack.onSuccess();
		}else {
			callBack.onFailed("This device not install TAPTAP App");
		}
	}
}
