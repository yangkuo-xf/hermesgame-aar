package com.hermesgamesdk.utils;

import java.io.File;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.hermesgamesdk.activity.TempActivty;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.view.AlertDialog;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.InitManager;

public class WriteTimeUtils {

	private Activity mActivity;
	private static WriteTimeUtils instance = null;
	private final String TAG = "hermesgame.WriteTimeUtil";

	private String oltFlag = "hermesgame_olTime";
	private String oltFileName = "hermesgame.olt.txt";

	private String customDirPath="";
	private String customFileName = "hermesgame.cfn.txt";
	private String cfnFlag = "hermesgame_cfn";
	private String olt = "0";
	public boolean isStop = false;
	private boolean isGeted = false;
	// public boolean isLogin = false;

	public String mPath = "";
	public SharedPreferences settings;
	private final String SHARED_OLT_DIR = "hermesgame_shared_olt";

	public final static int HANDLER_MESSAGE_CONTINUE = 1;
	public final static int HANDLER_MESSAGE_STOP = 0;
	public final static int HANDLER_MESSAGE_SHOW = 2;
	public int hours; // 累计 小时
	public int min; // 累计分钟
	private int count = 0; // 分钟计算器
	private String message="";
	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_MESSAGE_CONTINUE:

					FileUtils.getInstance(mActivity).saveFlag2Internal(mPath, oltFileName, oltFlag, olt);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(mPath, olt);
					editor.commit();

				writeTime(mActivity, mPath);

				checkTenMinuteRelease();
				break;
			case HANDLER_MESSAGE_STOP:
				isStop = true;
				break;
			case HANDLER_MESSAGE_SHOW:
				isStop = true;
				break;
			default:
				break;
			}
			;
		}
	};

	public static WriteTimeUtils getInstance() {
		if (instance == null) {
			synchronized (WriteTimeUtils.class) {
				if (instance == null) {
					instance = new WriteTimeUtils();
				}
			}
		}
		return instance;
	}

	/**
	 * 寫入時間
	 * 
	 * @param activity
	 * @param path
	 *            文件地址
	 */
	public void writeTime(final Activity activity, final String path) {
		/*
		 * if (!isLogin) return;
		 */
		mActivity=activity;
		mPath = path;

		if (isGeted) {

		} else {
			settings = activity.getSharedPreferences(SHARED_OLT_DIR, 0);

			SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
			String dateStr = dateformat.format(Long.valueOf((String)DataManager.getInstance().getData("timestamp"))* 1000);
			// 获取小时
			hours = Integer.valueOf(dateStr.split(":")[0]);
			min = Integer.valueOf(dateStr.split(":")[1]);
			Log.d(TAG,"hours: "+hours+"   mins:"+min);

			isGeted = true;
		}

		if (isStop) {
			// 停止
		} else {
				QGUserInfo mcustomInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
				int realNameFlag=mcustomInfo.getCheckrealname();
				if (realNameFlag!=-1&&realNameFlag!=0){
					//游客&未实名
					customDirPath=Environment.getExternalStorageDirectory() + File.separator + "QGDir/"
							+ "/" + Constant.PRODUCT_ID+ "/"
							+ Constant.CHANNEL_ID + "/" + mcustomInfo.getUserdata().getUid();

					if (!FileUtils.getInstance(activity).getFlagFromInternal(customDirPath,customFileName,cfnFlag).equals("")) {
						long hours=System.currentTimeMillis()/1000;

						if (Integer.valueOf(FileUtils.getInstance(activity).getFlagFromInternal(customDirPath,customFileName,cfnFlag))-	((int)hours)<=15*24*60*60){
							showDiaLog(mActivity,6);
						}
					}

				}
				olt = FileUtils.getInstance(activity).getFlagFromInternal(path, oltFileName, oltFlag);
				Log.d(TAG, "writeTime olt from file :" + olt+ path);
				if ("".equals(olt) || "0".equals(olt)) {
					// 文件不存在，或者文件内容为空 读取缓存里面的
					olt = settings.getString(path, "");
					if (TextUtils.isEmpty(olt)) {
						// 缓存里面的也为空
						olt="60";
				/*		FileUtils.getInstance(activity).saveFlag2Internal(path, oltFileName, oltFlag, "0");// 创建文件写入默认值0
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(path, "0");
						editor.commit();*/
						mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_CONTINUE, 1000 * 60);
					} else {
						// 缓存不为空
						Log.d(TAG, "writeTime olt from shared 1:" + olt);
						olt = "" + (Integer.valueOf(olt) + 60);
						// 不为空 增加分钟数 分钟数>=60 小时+1 小时大于22 显示时间
						count = count + 1;
						if (min + count >= 60) {
							hours = hours + 1;
							min = 0;
							count = 0;
						}
						QGUserInfo mInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
						int mRealNameFlag=mInfo.getCheckrealname();
						if (hours == 22) {
							if (mInfo.getuAge()>=18){
									//实名且成年
							}else{
								showDiaLog(activity, 4);
							}

							mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOW, 0);
						}
						if (HolidayUtils.getInstance().isHolidays((String)DataManager.getInstance().getData("timestamp"))) {
							Log.d(TAG, "WriteTimeUtils   是节假日");
							if (Integer.valueOf(olt)>=3600&&Integer.valueOf(olt)<10800){

								if (mRealNameFlag!=-1&&mRealNameFlag!=0){
									//游客限制
									showDiaLog(activity, 6);
									mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOW, 0);
								}else{
									// 存入新时间
						/*			FileUtils.getInstance(activity).saveFlag2Internal(path, oltFileName, oltFlag, olt);
									SharedPreferences.Editor editor = settings.edit();
									editor.putString(path, olt);
									editor.commit();*/
									mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_CONTINUE, 1000 * 60);
								}
							}
							else if (Integer.valueOf(olt) >= 10800) {
								// 达到限时 展示dialog
								showDiaLog(activity, 2);
								mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOW, 0);
							} else {
								// 存入新时间
						/*		FileUtils.getInstance(activity).saveFlag2Internal(path, oltFileName, oltFlag, olt);
								SharedPreferences.Editor editor = settings.edit();
								editor.putString(path, olt);
								editor.commit();*/
								mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_CONTINUE, 1000 * 60);
							}
						} else {
							Log.d(TAG, "WriteTimeUtils   不是节假日");
							if (Integer.valueOf(olt)>=3600&&Integer.valueOf(olt)<5400){

								if (mRealNameFlag!=-1&&mRealNameFlag!=0){
									showDiaLog(activity, 6);
									mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOW, 0);
								}else{
							/*		FileUtils.getInstance(activity).saveFlag2Internal(path, oltFileName, oltFlag, olt);
									SharedPreferences.Editor editor = settings.edit();
									editor.putString(path, olt);
									editor.commit();*/
									mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_CONTINUE, 1000 * 60);
								}
							}
							else if (Integer.valueOf(olt) >= 5400) {
								// 达到限时 展示dialog
								showDiaLog(activity, 1);
								mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOW, 0);
							} else {
								// 存入新时间
						/*		FileUtils.getInstance(activity).saveFlag2Internal(path, oltFileName, oltFlag, olt);
								SharedPreferences.Editor editor = settings.edit();
								editor.putString(path, olt);
								editor.commit();*/
								mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_CONTINUE, 1000 * 60);
							}
						}

					}

				}
				else {
					// 不为空 时间+60s
					olt = "" + (Integer.valueOf(olt) + 60);
					// 不为空 增加分钟数 分钟数>=60 小时+1 小时大于22 显示时间
					count = count + 1;

					if (min + count >= 60) {
						hours = hours + 1;
						min = 0;
						count = 0;
					}
					QGUserInfo mInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
					int mRealNameFlag=mInfo.getCheckrealname();
					if (hours == 22) {
						if (mInfo.getuAge()>=18){

						}else{
							showDiaLog(activity, 4);
						}

						mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOW, 0);
					}
					if (HolidayUtils.getInstance().isHolidays((String)DataManager.getInstance().getData("timestamp"))) {
						Log.d(TAG, "WriteTimeUtils   是节假日");
						if (Integer.valueOf(olt)>=3600&&Integer.valueOf(olt)<5400){

							if (mRealNameFlag!=-1&&mRealNameFlag!=0){
								showDiaLog(activity, 6);
								mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOW, 0);
							}else{
								// 存入新时间
								/*FileUtils.getInstance(activity).saveFlag2Internal(path, oltFileName, oltFlag, olt);
								SharedPreferences.Editor editor = settings.edit();
								editor.putString(path, olt);
								editor.commit();*/
								mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_CONTINUE, 1000 * 60);
							}
						}
						else if (Integer.valueOf(olt) >= 10800) {
							// 达到限时 展示dialog
							showDiaLog(activity, 2);
							mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOW, 0);
						} else {
							// 存入新时间
				/*			FileUtils.getInstance(activity).saveFlag2Internal(path, oltFileName, oltFlag, olt);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString(path, olt);
							editor.commit();*/
							mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_CONTINUE, 1000 * 60);
						}
					} else {
						Log.d(TAG, "WriteTimeUtils   不是节假日");
						//changeeeeeee 3600-->30
						if (Integer.valueOf(olt)>=3600&&Integer.valueOf(olt)<5400){
							Log.d(TAG, "WriteTimeUtils   time>=3600");

						//	if (mInfo.getUserdata().getIsGuest()==1&&mRealNameFlag!=-1&&mRealNameFlag!=0){
							if (mRealNameFlag!=-1&&mRealNameFlag!=0){
								Log.d(TAG, "WriteTimeUtils   time>=3600 &&IsGuest");
								showDiaLog(activity, 6);
								customDirPath=Environment.getExternalStorageDirectory() + File.separator + "QGDir/"
										+ "/" + Constant.PRODUCT_ID+ "/"
										+ Constant.CHANNEL_ID + "/" + mInfo.getUserdata().getUid();

								long hours=System.currentTimeMillis()/1000;

								FileUtils.getInstance(activity).saveFlag2Internal(customDirPath, customFileName,cfnFlag,String.valueOf((int)hours));
								mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOW, 1000 * 60);
							}else{
								// 存入新时间
								Log.d(TAG, "WriteTimeUtils   time>=3600 && not Guest");
					/*			FileUtils.getInstance(activity).saveFlag2Internal(path, oltFileName, oltFlag, olt);
								SharedPreferences.Editor editor = settings.edit();
								editor.putString(path, olt);
								editor.commit();*/
								mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_CONTINUE, 1000 * 60);
							}
						}
						else if (Integer.valueOf(olt) >= 5400) {
							// 达到限时 展示dialog
							Log.d(TAG, "WriteTimeUtils   time>=5400");
							showDiaLog(activity, 1);
							mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOW, 0);
						}
						else {
							// 存入新时间
						/*	FileUtils.getInstance(activity).saveFlag2Internal(path, oltFileName, oltFlag, olt);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString(path, olt);
							editor.commit();*/
							mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_CONTINUE, 1000 * 60);

						}
					}

				}

		}

	}

	public void stopWriteTime() {
		Log.d(TAG, "stopWriteTime");
		isStop = true;
		mHandler.removeMessages(HANDLER_MESSAGE_CONTINUE);
	}

	public void startWriteTime() {
		Log.d(TAG, "startWriteTime");
		isStop = false;
	}

    String rightName="下线休息";
	public void showDiaLog(final Activity activity, final int tag) {
		InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
		QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		int  realNameFlag=info.getCheckrealname();
		String leftName="";

		switch (tag) {
			case 1:
				//在计时过程中实名 且年龄大于18
				if (realNameFlag==Constant.CRETIFICATION_HAS_CERTIFIED&&info.getuAge()>=18){
					message="0";
				}else {
                    rightName="下线休息";
					if((realNameFlag==1||realNameFlag==2)){
						leftName="实名认证";
					}
					if (data.getProductconfig().getFcmTips().getMinorTimeTip()!=null&&!data.getProductconfig().getFcmTips().getMinorTimeTip().equals("")){
						message=data.getProductconfig().getFcmTips().getMinorTimeTip();
					}else {
						message="根据《国家新闻出版署关于防止未成年人沉迷网络游戏的通知》规定，本游戏向未成年人提供游戏服务的时长在法定节假日每日累计不得超过3小时，其他时间每日累计不得超过1.5小时。请合理安排游戏时间,劳逸结合。";
					}
				}

				break;
			case 2:
				//在计时过程中实名 且年龄大于18
				if (realNameFlag==Constant.CRETIFICATION_HAS_CERTIFIED&&info.getuAge()>=18){
					message="0";
				}else {
					if((realNameFlag==1||realNameFlag==2)){
						leftName="实名认证";
					}
                    rightName="下线休息";
					message="根据《国家新闻出版署关于防止未成年人沉迷网络游戏的通知》规定，本游戏向未成年人提供游戏服务的时长在法定节假日每日累计不得超过3小时，其他时间每日累计不得超过1.5小时。请合理安排游戏时间,劳逸结合。";
				}

				break;
			case 3:
				//实名之后小于18岁
				message="您今日累计游戏时间已经超过未成年限制,根据防沉迷规则,您今日无法继续游戏,请合理安排游戏时间,劳逸结合。";
                rightName="下线休息";
				break;
			case 4:
				//22-8
				if (info.getuAge()==-1||info.getuAge()==0){
					leftName="实名认证";
				}
				message="您暂时不能继续游戏，根据《国家新闻出版署关于防止未成年人沉迷网络游戏的通知》规定，您在每日22:00-次日8:00无法进入游戏，下次可进入游戏时间为明日8:00。请合理安排游戏时间,劳逸结合.";
                rightName="下线休息";
				break;

			case 6:
				leftName="实名认证";
                rightName="下线休息";
				if (data.getProductconfig().getFcmTips().getGuestTimeTip()!=null&&!data.getProductconfig().getFcmTips().getGuestTimeTip().equals("")){
					message=data.getProductconfig().getFcmTips().getGuestTimeTip();
				}else{
					message="您当前为游客模式，根据《国家新闻出版署关于防止未成年人沉迷网络游戏的通知》规定，本游戏对15天内使用同一硬件设备的用户开放不超过60分钟的游客体验模式，游客体验模式不可进行充值和付费功能。";
				}
				break;
            case 7:
                rightName="我知道了";

                int s=getTime();
                int releaseMin=0;
                String releaseTime="0";
                //游客 未实名

                if (realNameFlag!=-1&&realNameFlag!=0){

					if (info.getuAge()==-1||info.getuAge()==0){
						Log.d(TAG, "showDiaLog7.2 ");
						leftName="实名认证";
					}
                	releaseMin=(3600-s)/60;
                	if (releaseMin==1){
                		releaseMin=0;
					}
					releaseTime="\r\n剩余游戏时间: "+releaseMin+"分钟";
                	if (data.getProductconfig().getFcmTips().getGuestLoginTip()!=null&&!data.getProductconfig().getFcmTips().getGuestLoginTip().equals("")){
						message=data.getProductconfig().getFcmTips().getGuestLoginTip()+releaseTime;
					}else{
						message="您当前为游客模式，根据《国家新闻出版署关于防止未成年人沉迷网络游戏的通知》规定，本游戏对15天内使用同一硬件设备的用户开放不超过60分钟的游客体验模式，游客体验模式不可进行充值和付费功能。"+releaseTime;
					}

                }
				else if(realNameFlag==Constant.CRETIFICATION_HAS_CERTIFIED){

					if (HolidayUtils.getInstance().isHolidays((String)DataManager.getInstance().getData("timestamp"))) {

						releaseMin=(10800-s)/60;
					}else{
						releaseMin=(5400-s)/60;
					}
					if (releaseMin==1){
						releaseMin=0;
					}
					releaseTime="\r\n剩余游戏时间: "+releaseMin+"分钟";
					if (data.getProductconfig().getFcmTips().getMinorLoginTip()!=null&&!data.getProductconfig().getFcmTips().getMinorLoginTip().equals("")){
						message=data.getProductconfig().getFcmTips().getMinorLoginTip()+releaseTime;
					}else{
						message="根据《国家新闻出版署关于防止未成年人沉迷网络游戏的通知》规定，本游戏向未成年人提供游戏服务的时长在法定节假日每日累计不得超过3小时，其他时间每日累计不得超过1.5小时。"+releaseTime;
					}

				}
                break;
			case 8:
				rightName="我知道了";
				leftName="实名认证";
				message="您当前为游客模式，根据《国家新闻出版署关于防止未成年人沉迷网络游戏的通知》规定，游客体验模式不可进行充值和付费功能，请先进行实名认证。";
				break;
			case 9:
				rightName="好的";
				leftName="";
				if (info.getuAge()==-1||info.getuAge()==0){
					leftName="实名认证";
				}

				String releaseTimes="\r\n剩余游戏时间: "+9+"分钟";
				message="您好，当前剩余游戏时间已不足10分钟。"+releaseTimes;
				break;
			default:
				break;
		}

			final AlertDialog alert = new AlertDialog(InitManager.getInstance().mActivity, null,"防沉迷提示", message, leftName,
					rightName) {

				@Override
				public void onDismiss() {
				}
			};

		alert.hideClose();
		alert.setCancelable(false);
		alert.setClickListener(new AlertDialog.onClick() {
			@Override
			public void onLeftClick() {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("from", "guest_cert");
				intent.putExtras(bundle);
				intent.setClass(activity, TempActivty.class);
				activity.startActivityForResult(intent, 10001);
				alert.dismiss();
			}

			@Override
			public void onRightClick() {

                alert.dismiss();
			    if(tag==7||tag==8||tag==9){
                }else{
                    activity.finish();
                    System.exit(0);
                }
			}
		});
		if(message.equals("0")||message.isEmpty()){
			return;
		}else{
			alert.show();
		}

	}



	// 获取时间
	public int getTime() {
		int seconds=0;
		SharedPreferences settings=mActivity.getSharedPreferences(SHARED_OLT_DIR, 0);
		//秒
		if (settings.getString(mPath,"0").equals("")||settings.getString(mPath,"0")==null){
			seconds=0;
			Log.d(TAG, "getTime"+mPath);
		}else{
			seconds=Integer.valueOf(settings.getString(mPath, "0"));
			Log.d(TAG, "getTime");
		}

		return seconds;
	}
	public void checkTenMinuteRelease(){
		if (isStop) {
			// 停止
			return;
		}
		QGUserInfo info = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);

		int  realNameFlag=info.getCheckrealname();
		int s=getTime();
		int releaseMin=0;


		if (realNameFlag!=-1&&realNameFlag!=0){
			releaseMin=(3600-s)/60;
			Log.d("hermesgame","releaseMin: "+releaseMin);
			if (releaseMin==10){

				showDiaLog(mActivity,9);
			}else{
				return;
			}
		}
		else if(realNameFlag==Constant.CRETIFICATION_HAS_CERTIFIED){
			if (HolidayUtils.getInstance().isHolidays((String)DataManager.getInstance().getData("timestamp"))) {
				releaseMin=(10800-s)/60;
			}else{
				releaseMin=(5400-s)/60;
			}
			Log.d("hermesgame","releaseMin: "+releaseMin);
			if (releaseMin==10){

				showDiaLog(mActivity,9);
			}else{
				return;
			}

		}

	}
}
