package com.hermesgamesdk.manager;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bytedance.applog.GameReportHelper;
import com.hermesgamesdk.QGManager;
import com.hermesgamesdk.activity.AliWebPayActivity;
import com.hermesgamesdk.activity.GameSliderBarActivityV2;
import com.hermesgamesdk.activity.PayActivity;
import com.hermesgamesdk.activity.QGPaySuccessActivity;
import com.hermesgamesdk.activity.QGVoucherActivty;
import com.hermesgamesdk.activity.ScanPayActivity;
import com.hermesgamesdk.activity.WeChatWebPayActivity;
import com.hermesgamesdk.callback.QGCallBack;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.entity.QGOrderInfo;
import com.hermesgamesdk.entity.QGRoleInfo;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.entity.WalletInfo;
import com.hermesgamesdk.net.HttpRequest;
import com.hermesgamesdk.net.QGParameter;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.hermesgamesdk.utils.WriteTimeUtils;
import com.hermesgamesdk.view.AlertDialog;
import com.hermesgamesdk.view.LoadingDialog;
import com.ipaynow.wechatpay.plugin.api.WechatPayPlugin;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by user on 2017/9/7.
 */

public class QGPayManager {

	public static QGPayManager instance;
	public QGCallBack mPayCallBack = null;
	public QGCallBack mVoucherCallBack = null;
	public Activity mActivity;
	public QGOrderInfo mOrderInfo = null;
	public QGRoleInfo mRoleInfo = null;
	private List<InitData.Paytypes> payTypes;
    public String orderId="0";

	public String voucherAmount="0";
	public String voucherCode="";

	public static final int REQUEST_VOUCHER= 10010;
	public static final int REQUEST_PLUGIN= 10011;
	public static final int VOUCHER_SUCCESS= 1;
	public static final int VOUCHER_FAIL= 0;

	public static QGPayManager getInstance() {
		if (instance == null) {
			instance = new QGPayManager();
		}
		return instance;
	}

	public void destroy() {
		instance = null;
	}

	public void showPayView(final Activity activity, QGRoleInfo mRoleInfo, QGOrderInfo mOrderInfo,
			QGCallBack payCallBack) {
			mActivity=activity;
		this.mPayCallBack = payCallBack;
		if (mOrderInfo!=null){
			this.mOrderInfo = mOrderInfo;
		}else{
			Toast.makeText(activity,"orderInfo is null",Toast.LENGTH_LONG).show();
		}

		this.mRoleInfo = mRoleInfo;

		try {
			InitData aData = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
			payTypes = aData.getPaytypes();
			if(payTypes==null||payTypes.size()==0){
				Toast.makeText(activity, "还未配置支付方式 payTypes==null", Toast.LENGTH_SHORT);
				payCallBack.onFailed("还未配置支付方式 payTypes==null");
				return;
			}
			QGUserInfo mInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
			int  realNameFlag=mInfo.getCheckrealname();
			if (mInfo.getCkPlayTime()==1&&realNameFlag!=-1&&realNameFlag!=0){
				WriteTimeUtils.getInstance().showDiaLog(activity,8);
				payCallBack.onFailed("未进行实名认证");
				return;
			}

			checkSDKCoins(activity,mOrderInfo.getAmount(),new QGCallBack(){

				@Override
				public void onSuccess() {
					if (payTypes.size() == 1 && payTypes.get(0).getPaytypeid() == 202) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								createOrder(activity, 202);
							}
						});

					} else {
						// 启动PayActivity

						Intent payIntent = new Intent(activity, PayActivity.class);
						activity.startActivity(payIntent);
					}
				}

				@Override
				public void onFailed(String msg) {
					if (msg!=null&&!msg.equals("")){
						showDiaLog(mActivity,50013,msg);
					}else{
						if (payTypes.size() == 1 && payTypes.get(0).getPaytypeid() == 202) {
							activity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									createOrder(activity, 202);
								}
							});

						} else {
							// 启动PayActivity

							Intent payIntent = new Intent(activity, PayActivity.class);
							activity.startActivity(payIntent);
						}
					}

				}
			});

		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(activity, "还未配置支付方式 Exception", Toast.LENGTH_SHORT);

			Log.e("hermesgame","showPayView Exception ："+e.toString());
			e.printStackTrace();
			payCallBack.onFailed("还未配置支付方式 Exception");
		}

	}

	public QGRoleInfo getRoleInfo() {
		return mRoleInfo;
	}

	public QGOrderInfo getOrderInfo() {
		if (mOrderInfo!=null){
			return mOrderInfo;
		}else{
			QGOrderInfo orderInfo=new QGOrderInfo();
			orderInfo.setAmount("Please check amount");
			//Toast.makeText(mActivity,"orderInfo is null",Toast.LENGTH_LONG).show();
			return orderInfo;
		}
	}

	public void createOrder(final Activity activity, final int payType) {
		mActivity = activity;

		String tradeType = "";
		if (payType == 88) {
			tradeType = "MWEB";
		} else if (payType == 130) {
			tradeType = "APP";
		}
		// 向服务器请求下单
		String param = new QGParameter(activity)
				.addParameter("uid", QGManager.getUID())
				.addParameter("orderSubject", getOrderInfo().getOrderSubject())
				// 商品描述
				.addParameter("productOrderNo", getOrderInfo().getProductOrderId())
				.addParameter("amount", getOrderInfo().getAmount())
				// 总价：必须传String格式
				.addParameter("payType", payType + "")
				.addParameter("tradeType", tradeType)
				.addParameter("voucherCode",QGPayManager.getInstance().getVoucherCode())
				// 支付方式： 支付宝 1 微信 7
				.addParameter("extrasParams", getOrderInfo().getExtrasParams())
				.addParameter("serverName", getRoleInfo().getServerName())
				.addParameter("roleName", getRoleInfo().getRoleName())
				.addParameter("roleLevel", getRoleInfo().getRoleLevel() + "").create();
		HttpRequest<String> requestPay = new HttpRequest<String>() {
			@Override
			public void onSuccess(String bean) {
				try {
					String pay = new JSONObject(bean).getString("payParams");
					String orderNo = new JSONObject(bean).getString("orderNo");
                    orderId=orderNo;
					if (payType == 1) {
						startALiPay(activity, pay,false);
					} else if (payType == 2) {
						startAliWebPay(activity, pay,false);
					} else if (payType == 7) {
						startWeChatPay(activity, pay);
					} else if (payType == 88) {
						startWeChatWebPay(activity, pay, 88,false);
					} else if (payType == 130) {
						startWeChatWebPay(activity, pay, 130,false);
					} else if (payType == 165) {
						startWeChatWebPay(activity, pay, 165,false);
					} else if (payType == 166) {
						startWeChatWebPay(activity, pay, 166,false);
					} else if (payType == 167) {
						startWeChatWebPay(activity, pay, 167,false);
					} else if (payType == 176) {
						startWechatApp(activity, pay);
					} else if (payType == 178) {
						startScanPay(activity, pay, 178, orderNo);
					} else if (payType == 179) {
						startScanPay(activity, pay, 179, orderNo);
					} else if (payType == 180) {
						startScanPay(activity, pay, 180, orderNo);
					} else if (payType == 181) {
						startScanPay(activity, pay, 181, orderNo);
					} else if (payType == 182) {
						startWeChatWebPay(activity, pay, 182,false);
					} else if (payType == 183) {
						startWeChatWebPay(activity, pay, 183,false);
					} else if (payType == 184) {
						startWeChatWebPay(activity, pay, 184,false);
					} else if (payType == 193) {
						startWeChatWebPay(activity, pay, 193,false);
					} else if (payType == 194) {
						startWeChatWebPay(activity, pay, 194,false);
					} else if (payType == 199) {
						startWeChatWebPay(activity, pay, 199,false);
					} else if (payType == 201) {
						startWeChatWebPay(activity, pay, 201,false);
					} else if (payType == 202) {
						startWeChatWebPay(activity, pay, 202,false);
					} else if (payType == 203) {
						startWeChatWebPay(activity, pay, 203,false);
					} else if (payType == 208) {
						startWeChatWebPay(activity, pay, 208,false);
					}else if (payType == 221) {
						startWeChatWebPay(activity, pay, 221,false);
					}else if (payType == 214||payType==220) {
						if (pay.equalsIgnoreCase("true")) {

							final LoadingDialog loadingDialog=new LoadingDialog(mActivity);
							loadingDialog.show();
							TimerTask task = new TimerTask() {
								@Override
								public void run() {
									loadingDialog.dismiss();
									Intent intent=new Intent();
									intent.putExtra("result","success");
									intent.setClass(mActivity , QGPaySuccessActivity.class);
									mActivity.startActivityForResult(intent,10001);
								}
							};
							Timer timer = new Timer();
							timer.schedule(task, 1500);//3秒后执行TimeTask的run方法


						} else {
							final AlertDialog alert = new AlertDialog(mActivity, null, "支付中心","平台币扣款失败", "",
									"确定") {
								@Override
								public void onDismiss() {
									mPayCallBack.onSuccess();
									//内置事件 “支付”，属性：商品类型，商品名称，商品ID，商品数量，支付渠道，币种，是否成功（必传），金额（必传）
									GameReportHelper.onEventPurchase(getOrderInfo().getOrderSubject(),getOrderInfo().getOrderSubject(), getOrderInfo().getProductOrderId(),getOrderInfo().getCount(),
											"wechat","¥", true, new BigDecimal(getOrderInfo().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());


									mActivity.finish();
								}
							};
							alert.setClickListener(new AlertDialog.onClick() {
								@Override
								public void onLeftClick() {

								}

								@Override
								public void onRightClick() {
									alert.dismiss();
								}
							});
							alert.show();

						}
					}
					else if (payType==223||payType==224){
						startWeChatWebPay(activity, pay, payType,false);
					}
					else if (payType==225){
						startWeChatWebPay(activity, pay, payType,false);
					}
					else if (payType==226){
						startWeChatAppPay(activity, pay);
					}
					else if (payType==227){
						startWeChatWebPay(activity, pay, payType,false);
					}
					else if (payType==228){
						startWeChatWebPay(activity, pay, payType,false);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailed(int id, String message) {
				if ((id >= 50006 && id <= 50010)||id==50012) {
					showDiaLog(activity,id,message);
				} else {
					QGSdkUtils.showToast(activity, message);
				}
			}
		}.addParameter(param).post().setUrl(Constant.HOST + Constant.START_PAY);
		DataManager.getInstance().requestHttp(requestPay);
	}

	// 支付宝回调参数：resultStatus 支付成功：9000 取消：60001 未装支付宝：4000 其余：失败
	public void startALiPay(final Activity context, final String payString, final boolean isSlider) {
		Runnable payRunnable = new Runnable() {
			@Override
			public void run() {
				PayTask alipay = new PayTask(context);

				Map<String, String> rawResult = alipay.payV2(payString, true);
				String resultStatus = "";
				for (String key : rawResult.keySet()) {
					if (TextUtils.equals(key, "resultStatus")) {
						resultStatus = rawResult.get(key);
						Log.e("resultStatus", "resultStatus" + resultStatus);
					} else if (TextUtils.equals(key, "result")) {
					} else if (TextUtils.equals(key, "memo")) {
					}
				}
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					if (!isSlider){
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								QGPayManager.getInstance().mPayCallBack.onSuccess();
								//内置事件 “支付”，属性：商品类型，商品名称，商品ID，商品数量，支付渠道，币种，是否成功（必传），金额（必传）
								GameReportHelper.onEventPurchase(getOrderInfo().getOrderSubject(),getOrderInfo().getOrderSubject(), getOrderInfo().getProductOrderId(),getOrderInfo().getCount(),
										"wechat","¥", true, new BigDecimal(getOrderInfo().getAmount()).intValue());
								mActivity.finish();
							}
						});
					}else{
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity,"支付成功",Toast.LENGTH_LONG).show();

							}
						});
					}

				} else if (TextUtils.equals(resultStatus, "6001")) {
					if (!isSlider){
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {

								QGPayManager.getInstance().mPayCallBack.onFailed(mActivity.getString(mActivity
										.getResources().getIdentifier("toast_text_paycancel", "string",
												mActivity.getPackageName())));
								mActivity.finish();
							}
						});
					}else{
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity,"支付取消",Toast.LENGTH_LONG).show();
							}
						});

					}


				} else if (TextUtils.equals(resultStatus, "6002")) {
					if (!isSlider){
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								QGPayManager.getInstance().mPayCallBack.onFailed(mActivity.getString(mActivity
										.getResources().getIdentifier("toast_text_getInternet_error", "string",
												mActivity.getPackageName())));
							}
						});
						mActivity.finish();
					}else{
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity,"支付失败",Toast.LENGTH_LONG).show();
							}
						});
					}

				} else if (TextUtils.equals(resultStatus, "6004")) {
					if (!isSlider){
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								QGPayManager.getInstance().mPayCallBack.onFailed(mActivity.getString(mActivity
										.getResources().getIdentifier("toast_text_pay_unknow_result", "string",
												mActivity.getPackageName())));
							}
						});
						mActivity.finish();
					}else{
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity,"支付失败",Toast.LENGTH_LONG).show();
							}
						});
					}

				} else if (TextUtils.equals(resultStatus, "8000")) {
					if (!isSlider){
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								QGPayManager.getInstance().mPayCallBack.onFailed(mActivity.getString(mActivity
										.getResources().getIdentifier("toast_text_pay_indeal", "string",
												mActivity.getPackageName())));
							}
						});
						mActivity.finish();
					}else{
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity,"支付失败",Toast.LENGTH_LONG).show();
							}
						});
					}

				} else if (TextUtils.equals(resultStatus, "4000")) {
					if (!isSlider){
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								QGPayManager.getInstance().mPayCallBack.onFailed(mActivity.getString(mActivity
										.getResources().getIdentifier("toast_text_pay_failed_need_install_alipay",
												"string", mActivity.getPackageName())));
							}
						});
						mActivity.finish();
					}else{
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity,"支付失败",Toast.LENGTH_LONG).show();
							}
						});
					}

				} else if (TextUtils.equals(resultStatus, "5000")) {
					if (!isSlider){
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								QGPayManager.getInstance().mPayCallBack.onFailed(mActivity.getString(mActivity
										.getResources().getIdentifier("toast_text_pay_failed_much", "string",
												mActivity.getPackageName())));
							}
						});
						mActivity.finish();
					}else{
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity,"支付失败",Toast.LENGTH_LONG).show();
							}
						});
					}

				} else {
					if (!isSlider){
						// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								QGPayManager.getInstance().mPayCallBack.onFailed(mActivity.getString(mActivity
										.getResources().getIdentifier("toast_text_payfailed", "string",
												mActivity.getPackageName())));
							}
						});
						mActivity.finish();
					}else{
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity,"支付失败",Toast.LENGTH_LONG).show();
							}
						});
					}

				}
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	public void startWeChatAppPay(final Activity context, final String payString) {
			InitData data=(InitData)DataManager.getInstance().getData(Constant.INIT_KEY);
		try {
			String switchPlugin=data.getProductconfig().getSwitchWxAppPlug();
			//默认值为0 走插件模式
			if (switchPlugin.equals("0")){
				if (QGSdkUtils.checkAppInstalled(context,"com.haoyou.plugin")){
					Intent intent=new Intent();
					intent.setClassName("com.haoyou.plugin","com.haoyou.plugin.wxapi.WeChatPayActivity");

					if (intent!=null){
						intent.putExtra("payParams",payString);
						Log.d("hermesgame","start pay plugin");
						context.startActivityForResult(intent,QGPayManager.REQUEST_PLUGIN);
					}
				}else{
					String message="使用微信支付方式，请安装微信安全支付插件.";
					final AlertDialog alert = new AlertDialog(context, null,"温馨提示", message, "安装",
							"选择别的方式") {

						@Override
						public void onDismiss() {

						}
					};
					alert.setClickListener(new AlertDialog.onClick() {
						@Override
						public void onLeftClick() {
							alert.dismiss();
							String path= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "hermesgame"
									+ File.separator + mActivity.getPackageName()+File.separator+"QGPayPlugin.apk";
							QGSdkUtils.installApk(context,path);
						}

						@Override
						public void onRightClick() {
							alert.dismiss();
						}
					});
					alert.show();
				}
			}else{
				final IWXAPI msgApi;
				if (ThirdManager.getInstance().getIWXAPI()==null){
					ThirdManager.getInstance().setIWXAPI(WXAPIFactory.createWXAPI(context, null));
				}
				msgApi=ThirdManager.getInstance().getIWXAPI();
				JSONObject params=new JSONObject(payString);
				String appid=params.getString("appid");
				String partnerid=params.getString("partnerid");
				String timeStamp=params.getString("timestamp");
				String nonceStr=params.getString("noncestr");
				String packageValue=params.getString("package");
				String paySign=params.getString("paySign");
				String prepayid=params.getString("prepayid");
				Log.e("hermesgame.WeChatAppPay",appid+"  "+partnerid+"   "+timeStamp+"   "+nonceStr+"  "+packageValue+"    "+paySign+"    "+prepayid);

				msgApi.registerApp(appid);
				PayReq request = new PayReq();
				request.appId = appid;
				request.partnerId =partnerid;
				request.prepayId=prepayid;
				request.packageValue = packageValue;
				request.nonceStr= nonceStr;
				request.signType="MD5";
				request.timeStamp= timeStamp;
				request.sign=paySign;
				msgApi.sendReq(request);
			}
		}catch (Exception e){
			QGPayManager.getInstance().mPayCallBack.onFailed("支付失败");
			mActivity.finish();
		}

	}
	public void startWeChatPay(final Activity context, final String payString) {
		WechatPayPlugin.getInstance().init(context);
		WechatPayPlugin.getInstance().setCallResultActivity(context)// 传入继承了通知接口的类
				.pay(payString);// 传入请求数据
	}

	public void startWeChatWebPay(final Activity context, final String payString, int payType,boolean isSlider) {
		Intent intent = new Intent(mActivity, WeChatWebPayActivity.class);
		intent.putExtra("url", payString);
		intent.putExtra("payType", payType);
		intent.putExtra("isSlider",isSlider);
		Log.e("hermesgame", "WeChatWebPayActivity-Oncreat: " + payString);
		mActivity.startActivityForResult(intent, 22);
	}

	public void startScanPay(final Activity context, final String payString, int payType, String orderNo) {
		Intent intent = new Intent(mActivity, ScanPayActivity.class);
		intent.putExtra("codeUrl", payString);
		intent.putExtra("amount", getOrderInfo().getAmount());
		intent.putExtra("payType", "" + payType);
		intent.putExtra("orderNo", orderNo);
		mActivity.startActivityForResult(intent, 23);
	}

	public void startAliWebPay(final Activity context, final String payString,boolean isSlider) {
		Intent intent = new Intent(mActivity, AliWebPayActivity.class);
		intent.putExtra("url", payString);
		intent.putExtra("isSlider",isSlider);
		mActivity.startActivityForResult(intent, 21);
	}

	public void startWechatApp(final Activity context, final String payString) {
		try {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(payString));
			context.startActivityForResult(intent, 77);
		} catch (Exception e) {
			if (e instanceof ActivityNotFoundException) {
				Toast.makeText(context, "请安装最新的微信客户端", Toast.LENGTH_LONG).show();
			} else {
				e.printStackTrace();
			}
		}
	}

	public void showDiaLog(final Activity activity,int id,String message) {
		QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		String leftName="";
		String rightName="";
		if (id==50007&&info.getUserdata().getIsGuest()==1){
			leftName="个人中心";
		}else if (id==50012){
			leftName="个人中心";
			message="根据相关规定未实名账号不能使用支付服务,请前往个人中心进行实名认证.";
		}else if (id==50013){
			leftName="";
			rightName="我知道了";
		}
		final AlertDialog alert = new AlertDialog(activity, null,"防沉迷提示", message, leftName,
				rightName) {

			@Override
			public void onDismiss() {
				QGPayManager.getInstance().mPayCallBack.onFailed("防沉迷支付管控");
			}
		};
		alert.setClickListener(new AlertDialog.onClick() {
			@Override
			public void onLeftClick() {
				alert.dismiss();
				Intent intent=new Intent();
				intent.setClass(activity, GameSliderBarActivityV2.class);
				activity.startActivity(intent);
				if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					activity. overridePendingTransition(QGSdkUtils.getResId(activity,"R.anim.slide_bar_in"),QGSdkUtils.getResId(activity,"R.anim.slide_bar_out"));
				} else {
					activity.overridePendingTransition(QGSdkUtils.getResId(activity,"R.anim.slide_bar_in_bottom"),QGSdkUtils.getResId(activity,"R.anim.slide_bar_out_bottom"));
				}
				QGPayManager.getInstance().mPayCallBack.onFailed("支付取消");
				activity.finish();

			}

			@Override
			public void onRightClick() {
				alert.dismiss();
			}
		});
		alert.show();
	}

	public void  checkSDKCoins(Context ctx ,String amount,final QGCallBack callBack){
		String param = new QGParameter(ctx).addParameter("uid", QGManager.getUID()).addParameter("amount",""+amount).create();
		HttpRequest<WalletInfo> requestPay = new HttpRequest<WalletInfo>(){
			@Override
			public void onSuccess(WalletInfo bean) {
				Log.d("hermesgame","checkSDKCoins: "+bean);
				try{
					QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
					info.setSdkCoinNum(Double.valueOf(bean.getAmount()));
					((InitData) DataManager.getInstance().getData(Constant.INIT_KEY)).setPaytypes(bean.getPayTypes());
					if (bean.getErrTips()!=null&&!bean.getErrTips().equals("")){
						callBack.onFailed(bean.getErrTips());
					}else{
						callBack.onSuccess();
					}



				}catch (Exception e){
					callBack.onSuccess();
					Log.d("hermesgame","setSDKCoinsException: "+e.toString());
				}


			}

			@Override
			public void onFailed(final int code, final String message) {
				Log.e("hermesgame","checkSDKCoins Failed   code: "+code+"    msg:"+message);
				callBack.onFailed("");
			}
		}.addParameter(param).post().setUrl(Constant.HOST + Constant.CHECK_SDK_COINS);
		DataManager.getInstance().requestHttp(requestPay,Constant.WALLET_KEY);
	}

	public void showPayViewFromSlider(Activity activity,String pay,int payType){
		mActivity=activity;
		if (payType == 1) {
			startALiPay(activity, pay,true);
		} else if (payType == 2) {
			startAliWebPay(activity, pay,true);
		} else if (payType == 7) {
			startWeChatPay(activity, pay);
		} else if (payType == 88||payType==130||payType==165||payType==166||payType==167||payType==182||payType==183||payType==184||payType==193||payType==194||payType==199||payType==201||payType==202||payType==203||payType==208||payType==221||payType==223||payType==224) {
			startWeChatWebPay(activity, pay, payType,true);
		}else if (payType == 176) {
			startWechatApp(activity, pay);
		} else if (payType == 214||payType==220) {
			if (pay.equalsIgnoreCase("true")) {
				final 	LoadingDialog  loadingDialog=new LoadingDialog(mActivity);
				loadingDialog.show();
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						loadingDialog.dismiss();
						Intent intent=new Intent();
						intent.putExtra("result","success");
						intent.setClass(mActivity , QGPaySuccessActivity.class);
						mActivity.startActivityForResult(intent,10001);
					}
				};
				Timer timer = new Timer();
				timer.schedule(task, 1500);//3秒后执行TimeTask的run方法


			} else {
				final AlertDialog alert = new AlertDialog(mActivity, null, "支付中心","平台币扣款失败", "",
						"确定") {
					@Override
					public void onDismiss() {
						Toast.makeText(mActivity,"支付成功",Toast.LENGTH_LONG).show();
					}
				};
				alert.setClickListener(new AlertDialog.onClick() {
					@Override
					public void onLeftClick() {

					}

					@Override
					public void onRightClick() {
						alert.dismiss();
					}
				});
				alert.show();
			}
		}
	}

	public String  getOrderID(){
	    return orderId;
    }

    public void startVoucherActivity(Activity activity,QGCallBack  callBack){
		mVoucherCallBack=callBack;
		Intent intent=new Intent(activity, QGVoucherActivty.class);
		activity.startActivityForResult(intent,REQUEST_VOUCHER);

	}

	public String getVoucherAmount() {
		return voucherAmount;
	}

	public void setVoucherAmount(String voucherAmount) {
		this.voucherAmount = voucherAmount;
	}
	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}
}
