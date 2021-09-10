package com.hermesgamesdk.utils;

import static android.content.Context.CONNECTIVITY_SERVICE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.hermesgamesdk.QGManager;
import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.view.MaqueeLayout;

public class QGSdkUtils {
	private static Toast toast;
	static boolean isShowedRedpack=false;
	private static String cacheDirPath = Environment.getExternalStorageDirectory() + File.separator + "hermesgame";
	private static String deviceIdFlag = "qgDvid";
	private static String deviceIdFileName = "qg.dvid.txt";

	public static void showToast(Context context, String message) {
		if (toast == null) {
			toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}
		toast.show();
	}
	public static String getDeviceID(Context context) {
		String deviceid="";
		try {
			//判断读写权限
			if (QGSdkUtils.checkWritePermission(context)){

				deviceid=FileUtils.getInstance(context).getFlagFromInternal(Constant.DEVICEID_PATH,Constant.DEVICEID_FILENAME,Constant.DEVICEID_PREFIX);
				if (!deviceid.equals("")){
					QGSdkUtils.saveString(context,Constant.DEVICEID_KEY,deviceid);
					return deviceid;
				}else {
					deviceid=QGSdkUtils.getString(context,Constant.DEVICEID_KEY);
					//内存 缓存都为空
					if(deviceid.equals("")){
						return deviceUniqueId(context);
					}else{
						FileUtils.getInstance(context).saveFlag2Internal(Constant.DEVICEID_PATH,Constant.DEVICEID_FILENAME,Constant.DEVICEID_PREFIX,deviceid);
						return deviceid;
					}
				}
			}
			//没文件权限 读取缓存  如果缓存没有 就 计算值
			else{

				deviceid=QGSdkUtils.getString(context,Constant.DEVICEID_KEY);
				if(deviceid.equals("")){

					return deviceUniqueId(context);
				}else{
					return deviceid;
				}
			}
		}catch (Exception e){
			deviceid=getMD5Str(UUID.randomUUID().toString().replace("-", ""));
			QGSdkUtils.saveString(context,Constant.DEVICEID_KEY,deviceid);
			return deviceid;
		}

	}

	/**
	 * create a device unique Id
	 * 
	 * @param context
	 * @return
	 * @author
	 */
	public static String deviceUniqueId(Context context) {
		if (QGSdkUtils.checkPhonePermission(context)){

			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}
			String m_szDevIDShort = "" + Build.BOARD + Build.BRAND + Build.CPU_ABI + Build.DEVICE + Build.DISPLAY
					+ Build.HOST + Build.ID

					+ Build.MANUFACTURER + Build.MODEL.length() + Build.PRODUCT;

			TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			String deviceId = tm.getDeviceId();


			String simSerialNumber = tm.getSimSerialNumber();

			String subscriberId = tm.getSubscriberId();

			String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

			String serialnum = null;
			try {
				Class<?> c = Class.forName("android.os.SystemProperties");
				Method get = c.getMethod("get", String.class, String.class);
				serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
			} catch (Exception ignored) {

			}

			String uniqueId = m_szDevIDShort + (TextUtils.isEmpty(deviceId) ? "null" : deviceId)
					+ (TextUtils.isEmpty(simSerialNumber) ? "null" : simSerialNumber)
					+ (TextUtils.isEmpty(subscriberId) ? "null" : subscriberId)
					+ (TextUtils.isEmpty(androidId) ? "null" : androidId)
					+ (TextUtils.isEmpty(serialnum) ? "null" : serialnum);
			md.update(uniqueId.getBytes(), 0, uniqueId.length());
			// get md5 bytes
			byte p_md5Data[] = md.digest();
			// create a hex string
			String m_szUniqueID = new String();
			for (int i = 0; i < p_md5Data.length; i++) {
				int b = (0xFF & p_md5Data[i]);
				// if it is a single digit, make sure it have 0 in front (proper
				// padding)
				if (b <= 0xF)
					m_szUniqueID += "0";
				// add number to string
				m_szUniqueID += Integer.toHexString(b);
			} // hex string to uppercase
			m_szUniqueID = m_szUniqueID.toUpperCase();
			if (QGSdkUtils.checkWritePermission(context)){
				FileUtils.getInstance(context).saveFlag2Internal(Constant.DEVICEID_PATH,Constant.DEVICEID_FILENAME,Constant.DEVICEID_PREFIX,m_szUniqueID);
			}
			QGSdkUtils.saveString(context,Constant.DEVICEID_KEY,m_szUniqueID);
			return m_szUniqueID;
		}else{

			String deviceid="";
			if (Constant.OAID!=null&&!Constant.OAID.equals("")&&!Constant.OAID.equals("0")){
				deviceid=	Constant.OAID;
			}else{
				deviceid=getMD5Str(UUID.randomUUID().toString().replace("-", ""));
			}
			if (QGSdkUtils.checkWritePermission(context)){
				FileUtils.getInstance(context).saveFlag2Internal(Constant.DEVICEID_PATH,Constant.DEVICEID_FILENAME,Constant.DEVICEID_PREFIX,deviceid);
			}
			QGSdkUtils.saveString(context,Constant.DEVICEID_KEY,deviceid);
			return deviceid;
		}

	}

	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				md5StrBuff.append("0").append(Integer.toHexString(0xff & byteArray[i]));
			} else {
				md5StrBuff.append(Integer.toHexString(0xff & byteArray[i]));
			}

		}

		return md5StrBuff.toString();
	}

	public static <T> boolean isEmpty(List<T> list) {
		if (list == null || list.size() == 0) {
			return true;
		}
		return false;
	}

	public static String encryptAES(String encryptString, String encryptKey) throws Exception {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(encryptKey.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(encryptString.getBytes());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		// flag 不用URL_SAFE 不把 "/"转为"_"才能与服务器互通
		return Base64.encodeToString(crypted, Base64.DEFAULT);
	}

	public static String decryptAES(String decryptString, String decryptKey) throws Exception {
		byte[] output = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(decryptKey.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(Base64.decode(decryptString, Base64.DEFAULT));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return new String(output);
	}

	public static String signData(HashMap<String, Object> dataMap) {
		return signData(dataMap, Constant.signkey);

	}

	public static String signData(HashMap<String, Object> dataMap, String signKey) {
		if (dataMap == null || dataMap.size() == 0) {
			return null;
		}

		TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
		treeMap.putAll(dataMap);

		StringBuffer buffer = new StringBuffer();
		Set<String> keySet = treeMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			buffer.append(key);
			buffer.append("=");
			buffer.append(treeMap.get(key));
			buffer.append("&");
		}
		buffer.append(signKey);

		String md5Str = getMD5Str(buffer.toString());
		return md5Str;
	}

	public static int[] getScreenCfg(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		int[] displayCfg = new int[3];
		displayCfg[0] = metrics.widthPixels;
		displayCfg[1] = metrics.heightPixels;
		displayCfg[2] = metrics.densityDpi;

		return displayCfg;
	}
	public static String getAndroidID(Context context){
		String androidid="0";

		try {
			androidid= Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		}catch (Exception e){
			androidid="0";
		}
		return androidid;
	}

	public static String getImei(Context context) {
		try {
			if (Build.VERSION.SDK_INT >= 29){
				return (Constant.OAID==null||Constant.OAID.equals(""))?getMD5Str(UUID.randomUUID().toString().replace("-", "")):Constant.OAID;
			}
			else{
				int checkReadPhonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
				if (checkReadPhonePermission!=0) {
					return (Constant.OAID==null||Constant.OAID.equals(""))?getMD5Str(UUID.randomUUID().toString().replace("-", "")):Constant.OAID;
				}else {
					// 获取imei
					TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
					if (tm == null) {
						return getMD5Str(UUID.randomUUID().toString().replace("-", ""));
					}
					String imei = tm.getDeviceId();
					if (TextUtils.isEmpty(imei)) {
						return getMD5Str(UUID.randomUUID().toString().replace("-", ""));
					}
					return imei;
				}
			}

		} catch (Exception e) {
			Log.e("hermesgame", "getImei erro: " + e.toString());
			return getMD5Str(UUID.randomUUID().toString().replace("-", ""));
		}

	}

	public static String getCountryCode(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm == null) {
			return "''";
		}
		return tm.getSimCountryIso();
	}

	public static String blueToothAddress = null;

	public static String getBluetoothAddress(Context context) {
		if (blueToothAddress == null) {

			BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
			if (ba == null || !ba.isEnabled()) {
				return "";
			}
			String address = ba.getAddress();
			if (TextUtils.isEmpty(address)) {
				blueToothAddress = "";
			} else {
				blueToothAddress = ba.getAddress();
			}
		}

		return blueToothAddress;

	}

	public static void setHideVirtualKey(Window window) {
		int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		if (Build.VERSION.SDK_INT >= 19) {
			uiOptions |= 0x00001000;
		} else {
			uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
		}
		window.getDecorView().setSystemUiVisibility(uiOptions);
	}


	public static String getWifiAddress(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wm != null) {
			WifiInfo info = wm.getConnectionInfo();
			String macAddress = info.getMacAddress();
			if (TextUtils.isEmpty(macAddress)) {
				return "";
			} else {
				return info.getMacAddress();
			}
		}
		return "";
	}

	public static String getLanguate() {
		return Locale.getDefault().getLanguage();
	}

	public static String getImsi(Context context) {
		if (!QGSdkUtils.checkPhonePermission(context)){
			return getMD5Str(UUID.randomUUID().toString().replace("-", ""));
		}
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm == null) {
			return "0";
		}
		String imsi = tm.getSubscriberId();
		if (TextUtils.isEmpty(imsi)) {
			return "0";
		}
		return tm.getSubscriberId();
	}

	/**
	 * get network type 0-unkown 1-wifi 2-yidong 3-liantong 4-dianxin
	 * 
	 * @return
	 */
	public static int getNetworkType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null)
			return 0;
		int subType = info.getSubtype();
		if (subType == ConnectivityManager.TYPE_WIFI)
			return 1;
		else {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String simOperator = tm.getSimOperator();
			if ("46002".equals(simOperator) || "46000".equals(simOperator)) {
				return 2;
			} else if ("46001".equals(simOperator)) {
				return 3;
			} else if ("46003".equals(simOperator)) {
				return 4;
			}
		}
		return 0;
	}

	/**
	 * 动态获取资源
	 * 
	 * @param context
	 * @param resFullName
	 *            例如获取资源R.layout.activity_main，传"R.layout.activity_main"
	 * @return
	 */
	public static int getResId(Context context, String resFullName) {
		if (context==null){
			Log.e("hermesgame","context is null!!!!");
		}
		String[] parameters = resFullName.split("\\.");
		String resType = parameters[1];
		String resName = parameters[2];
		return context.getResources().getIdentifier(resName, resType, context.getPackageName());
	}
	// 将px值转换为sp值
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 账号密码存取
	 */
	private static final String NAME = "hermesgame";

	public static void saveString(Context context, String key, String value) {
		SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static String getString(Context context, String key) {
		String value="";
		SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		try {
			value=	preferences.getString(key, "");
		}catch (Exception e){

		}
		return value;
	}

	public static int getAndroidAnim(Context context, String string) {
		try {
			return context.getResources().getIdentifier(string, "anim", "android");
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 将图片转换为Base64
	public static String imgToBase64(String imgPath, int rate) {
		Bitmap bitmap = getSmallBitmap(imgPath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, rate, baos);
		byte[] bytes = baos.toByteArray();
		String result = Base64.encodeToString(bytes, Base64.NO_WRAP);// 该模式可以剔除转换出来后的换行
		return result;
	}

	private static Bitmap getSmallBitmap(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = calculateInSampleSize(opts, 480, 800);
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, opts);
	}

	private static int calculateInSampleSize(BitmapFactory.Options opts, int reqWidth, int reqHeight) {
		int height = opts.outHeight;
		int width = opts.outWidth;
		int sampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			while ((halfHeight / sampleSize) > reqHeight && (halfWidth / sampleSize) > reqWidth) {
				sampleSize *= 2;
			}
		}
		return sampleSize;
	}

	/**
	 * 获取assets的指定目录中的所有文件及子目录名数组
	 * 
	 * @param assetsFolderPath
	 *            - 目录的相对路径（目录），例如："why"
	 * @注意 子目录中必须有文件，否则不会将子目录名称写入数组中
	 * @return [img,listdata.txt,test.html]
	 * */
	public static String[] getFileNamesArray(Context mContext, String assetsFolderPath) {
		String fileNames[] = null;
		try {
			fileNames = mContext.getResources().getAssets().list(assetsFolderPath);// 获取assets目录下的所有文件及子目录名
			AssetManager assetManager=mContext.getResources().getAssets();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileNames;
	}

	/**
	 * 检测网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		return (isWiFiActive(context) || isNetAvailable(context));
	}

	/**
	 * 判断移动网络是否已经连接
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isNetAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断wifi是否已经连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWiFiActive(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
		if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取进程名
	 * 
	 * @param context
	 * @return
	 */

	public static String getProcessName(Context context) {
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
			if (runningApps == null) {
				return null;
			}
			for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
				if (proInfo.pid == android.os.Process.myPid()) {
					if (proInfo.processName != null) {
						return proInfo.processName;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {// 全角空格为12288，半角空格为32
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)// 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static Boolean isUrl(String url) {
		Pattern pattern = Pattern
				.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
		if (pattern.matcher(url).matches()) {
			return true;
		}
		return false;
	}



	/**
	 * 利用正则表达式判断字符串是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false;
		}
		return true;
	}


	public static int dp2px(Context context, float dipValue) {
		try {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dipValue * scale + 0.5f);
		} catch (Exception e) {
			return (int) dipValue;
		}
	}

	public static int px2dp(Context context, float px) {
		try {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (px / scale + 0.5f);
		} catch (Exception e) {
			return (int) px;
		}
	}

	public static int getPhoneWidthPixels(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics var2 = new DisplayMetrics();
		if (wm != null) {
			wm.getDefaultDisplay().getMetrics(var2);
		}

		return var2.widthPixels;
	}

	public static int getPhoneHeightPixels(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics var2 = new DisplayMetrics();
		if (wm != null) {
			wm.getDefaultDisplay().getMetrics(var2);
		}

		return var2.heightPixels;
	}
	public static boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 返回手机号码
	 */
	public static String[] telFirst="134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
	public static String getTel() {
		int index=getNum(0,telFirst.length-1);
		String first=telFirst[index];
		String second=String.valueOf(getNum(1,888)+10000).substring(1);
		String third=String.valueOf(getNum(1,9100)+10000).substring(1);
		return first+second+third;
	}
	public static int getNum(int start,int end) {
		return (int)(Math.random()*(end-start+1)+start);
	}
	public static void showMarQ(final Activity activity){

		if (isShowedRedpack){
			return;
		}
		final FrameLayout bannerView = new FrameLayout(activity);// 创建帧布局对象layout
		RelativeLayout.LayoutParams frameLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

		bannerView.setLayoutParams(frameLayout);

		FrameLayout.LayoutParams layoutParams =new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);//设置帧布局的高宽属性
		layoutParams.gravity= Gravity.CENTER_HORIZONTAL;
		activity.addContentView(bannerView,layoutParams);
		List<String> messages = new ArrayList<>();
		for (int i=0;i<15;i++){
			String tel=QGSdkUtils.getTel().substring(0,3)+"****"+QGSdkUtils.getTel().substring(6,10);
			int amount=QGSdkUtils.getNum(1,10);
			String messa=tel+" 领取了 "+amount+"元 的红包";
			//字体大小颜色不同
			messages.add(messa);
		}
		final MaqueeLayout maqueeLayout;
		maqueeLayout=new MaqueeLayout(activity, messages, bannerView);
		isShowedRedpack=true;
		maqueeLayout.setListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bannerView.removeAllViews();
				isShowedRedpack=false;
			}
		});

	}
	public static void showRedPack(final Activity activity){
		final FrameLayout bannerView = new FrameLayout(activity);// 创建帧布局对象layout
		RelativeLayout.LayoutParams frameLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

		bannerView.setLayoutParams(frameLayout);
		final ImageView imag1=new ImageView(activity);
		//imag1.setLayoutParams(new FrameLayout.LayoutParams(550,750));
		if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
				||activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT){
			imag1.setLayoutParams(new FrameLayout.LayoutParams((int)(QGSdkUtils.getPhoneWidthPixels(activity)*0.35),(int)(QGSdkUtils.getPhoneHeightPixels(activity)*0.35)));
		}else if (activity.getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				||activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE){
			imag1.setLayoutParams(new FrameLayout.LayoutParams((int)(QGSdkUtils.getPhoneWidthPixels(activity)*0.31),(int)(QGSdkUtils.getPhoneHeightPixels(activity)*0.77)));
		}

		imag1.setBackgroundResource(QGSdkUtils.getResId(activity,"R.drawable.qg_redpack_thumb"));
		final FrameLayout.LayoutParams layoutParams =new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);//设置帧布局的高宽属性

		layoutParams.gravity= Gravity.CENTER;
		Timer timer = new Timer();
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						bannerView.addView(imag1);
						activity.addContentView(bannerView,layoutParams);
					}
				});

			}
		};

		timer.schedule(task,4*60*1000);

		imag1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QGManager.showUserCenter(activity,"clickToRedpk");
				bannerView.setVisibility(View.GONE);
			}
		});
	}
	public static String stampToDate(String s) {
		if(s.length()==10){
			s=s+"000";
		}
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		//如果它本来就是long类型的,则不用写这一步
		long lt = new Long(s);
//        Date date = new Date(lt * 1000);
		Date date = new Date(lt );
		res = simpleDateFormat.format(date);
		return res;
	}
	public static String createRandom( int length){
		String retStr = "";
		String strTable ="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@_.";
		int len = strTable.length();
		boolean bDone = true;
		do {
			retStr = "";
			int count = 0;
			for (int i = 0; i < length; i++) {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <= c) && (c <= '9')) {
					count++;
				}
				retStr += strTable.charAt(intR);
			}
			if (count >= 2) {
				bDone = false;
			}
		} while (bDone);

		return retStr;
	}
	public static boolean isTapTap(Context context){
		boolean TapTapInstalled = false;
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.taptap", 0);
			if (null != packageInfo) {
				TapTapInstalled = true;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return TapTapInstalled;
		}
		return TapTapInstalled;
	}
	//是否是模拟器
	public static boolean getIsEumlator(Activity mActivity) {
		int ret=0;
		try {
			//传感器数量  一般模拟器<=12
			SensorManager	sensorManager = (SensorManager)mActivity. getSystemService(Context.SENSOR_SERVICE);
			int sensorCount=sensorManager.getSensorList(Sensor.TYPE_ALL).size();

			String url = "tel:" + "123456";
			Intent intent = new Intent();
			intent.setData(Uri.parse(url));
			intent.setAction(Intent.ACTION_DIAL);
			boolean canCallPhone = intent.resolveActivity(mActivity.getPackageManager()) != null;
			//不能打电话
			if (!canCallPhone){
				ret=1;
			}
			//夜神商店
			if (checkAppInstalled(mActivity,"com.bignox.app.store.hd")){
				ret=2;
			}
			//雷电
			if (checkAppInstalled(mActivity,"com.android.flysilkworm")){
				ret=3;
			}
			//逍遥
			if (checkAppInstalled(mActivity,"com.microvirt.market")&&checkAppInstalled(mActivity,"com.microvirt.memuime")
					&&checkAppInstalled(mActivity,"com.microvirt.tools")
					&&checkAppInstalled(mActivity,"com.microvirt.guide")){
				ret=4;  //
			}
			//mumu
			if (checkAppInstalled(mActivity,"com.mumu.audio")&&checkAppInstalled(mActivity,"com.netease.mumu.cloner")
					&&checkAppInstalled(mActivity,"com.mumu.launcher")){
				ret=5;
			}
			//腾旭手游助手
			if (checkAppInstalled(mActivity,"com.tencent.tinput")){
				if (sensorCount<=11){
					ret=6;
				}

			}

			//天天模拟器
			if (checkAppInstalled(mActivity,"com.tiantian.ime")){
				if (sensorCount<=11){
					ret=7;
				}
			}
			//蓝叠 既安装了谷歌浏览器 又安装了 谷歌游戏app
			if (checkAppInstalled(mActivity,"com.android.chrome")&&checkAppInstalled(mActivity,"com.google.android.play.games")){
				if (sensorCount<=11){
					ret=8;
				}
			}

			Log.d("hermesgame","getIsEumlator: "+ret) ;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return ret!=0;
	}

	public static boolean checkAppInstalled(Context context,String pkgName) {
		if (pkgName== null || pkgName.isEmpty()) {
			return false;
		}
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			packageInfo = null;
		}
		if(packageInfo == null) {
			return false;
		} else {
			return true;//true为安装了，false为未安装
		}
	}
	public static void installApk(Context context,String apkPath) {
		Log.d("hermesgame","install new apk  apkPath: "+apkPath);

			File apkfile = new File(apkPath);
			if (!apkfile.exists()) {
				return;
			}

			if (context!=null){
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if (Build.VERSION.SDK_INT >= 24) {
					intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

					Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".qgfileprovider", apkfile);
					intent.setDataAndType(uri, "application/vnd.android.package-archive");
					Log.d("hermesgame","install new apk  uri: "+uri.toString());
				} else {
					Log.d("hermesgame","install new apk 2 uri: "+Uri.fromFile(apkfile).toString());
					intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
				}
				context.startActivity(intent);
			}else{
				Log.d("hermesgame","install new apk Fail: ");
			}
	}
	public static boolean checkWritePermission(Context context){
		int result=ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE);
		return result==0?true:false;
	}
	public static boolean checkPhonePermission(Context context){
		int result=ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE);
		return result==0?true:false;
	}
}
