package com.hermesgamesdk.net;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.utils.QGSdkUtils;


/**
 * hermesgame每一次网络请求都有公共参数，网络参数都是以data=value&sign=value形式提交
 * QGParameter将特殊参数和公共参数合并，并以data=value&sign=value组合返回字符串 特殊参数:没一次网络请求传递的非公共参数
 */
public class QGParameter {

	// 用于存取公共参数和特殊参数
	private HashMap<String, Object> mParams;

	private Context mContext;

	private boolean addPublic = true;

	private String signKey = Constant.signkey;

	public static final int ENCODE_BASE64 = 1;
	public static final int ENCODE_AES = 2;
	private int encodeType = ENCODE_AES;

	/**
	 * @param ctx
	 *            初始化mParams
	 */
	public QGParameter(Context ctx) {
		mParams = new HashMap<String, Object>();
		mContext = ctx;

	}

	/**
	 * 将公共参数添加至mParams中 公共参数大多通过Constant获取 Constant中的公共参数在InitManager init方法中被赋值
	 */
	@SuppressLint("MissingPermission")
	private void addPublicParameter() {
		// hermesgame的版本号
		mParams.put("sdkVersion", Constant.SDK_VERSION);
		// 接入hermesgame app的VERSION_CODE
		mParams.put("gameVersion", Constant.VERSION_CODE);
		// deviceId 设备id 根据设备特征生产id用于标识设备的唯一性
		mParams.put("deviceId",QGSdkUtils.getDeviceID(mContext));
		Log.d("hermesgame","deviceId: "+QGSdkUtils.getDeviceID(mContext));
		// PLATFORM用于区分app运行的平台类型
		mParams.put("platform", Constant.PLATFORM);
		mParams.put("clientLang",QGSdkUtils.getLanguate());//

		mParams.put("oaid",(Constant.OAID==null||Constant.OAID.equals(""))?"0":Constant.OAID);

		mParams.put("andId",QGSdkUtils.getAndroidID(mContext));//

		if(Build.VERSION.SDK_INT < 29)
		{
			mParams.put("imei",QGSdkUtils.getImei(mContext).equals("") ? "0" :QGSdkUtils.getImei(mContext));
		}else{
			mParams.put("imei",(Constant.OAID==null||Constant.OAID.equals(""))?"0":Constant.OAID);
		}

		// hermesgame分配给cp的产品号，用于标识接入hermesgame的应用
		mParams.put("productCode", Constant.PRODUCT_ID);
		// hermesgame分配的CHANNEL_ID，用于标识接入hermesgame的应用发布的渠道
		mParams.put("channelCode", Constant.CHANNEL_ID);

		if(Constant.BYTEDANCE_ID!=null&&!Constant.BYTEDANCE_ID.equals("")){
			mParams.put("tt_cpscode", Constant.BYTEDANCE_ID);
		}

		// authToken 每次网络请求用于服务端认证，登录成功之前以当前系统时间加密获取
		// 登录成功之后返回authToken 用于后续网络请求的服务端认证
		String authToken;
		long time = System.currentTimeMillis();
		QGUserInfo userInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		// userInfo登录成功前为null，登录成功后不为null
		if (!mParams.containsKey("authToken")) {
			if (userInfo != null) {
				authToken = userInfo.getAuthtoken();
			} else {
				authToken = QGSdkUtils.getMD5Str(time + "");
			}
			mParams.put("authToken", authToken);
		}

		// 客户端网络请求时系统时间
		mParams.put("time", time);

	}

	/**
	 * 将mParams排序,加密，签名 拼接成data=value&sign=value的字符串
	 */
	public final String create() {

		if (addPublic) {
			addPublicParameter();
		}
		coverParameter(mParams);
		String encryptData = null;
		JSONObject jsonObject = new JSONObject();
		try {
			for (String key : mParams.keySet()) {

				jsonObject.put(key, mParams.get(key));
			}
			// 加密 Constant.signkey为加密秘钥
			if (encodeType == ENCODE_BASE64) {
				encryptData = Base64.encodeToString(jsonObject.toString().getBytes(), Base64.DEFAULT);
			} else {
				encryptData = QGSdkUtils.encryptAES(jsonObject.toString(), signKey);
				encryptData=encryptData.replace(" ","+");
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		// 签名 用于验证网络请求过程数据是否被更改
		String signData = QGSdkUtils.signData(mParams, signKey);
		return "data=" + encryptData + "&sign=" + signData;

	}

	public final String create(boolean addPublic, String signKey, int encodeType) {
		this.signKey = signKey;
		this.addPublic = addPublic;
		this.encodeType = encodeType;
		return create();
	}

	/**
	 * 添加网络请求特殊参数
	 * 
	 */
	public final QGParameter addParameter(String key, Object value) {

		mParams.put(key, value);
		return this;
	}

	public void coverParameter(HashMap<String, Object> params) {

	}
}
