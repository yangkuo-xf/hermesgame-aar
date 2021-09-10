package com.hermesgamesdk.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.Manifest;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.hermesgamesdk.constant.Constant;

public class FileUtils {

	private static final String TAG = "hermesgame Fu ";
	private volatile static FileUtils sInstance;
	private static final String CHARSET = "UTF-8";
	private static Context mContext=null;

	private FileUtils() {
	}

	public static FileUtils getInstance(Context context) {

		if (sInstance == null) {
			sInstance = new FileUtils();
			mContext=context;
		}

		return sInstance;
	}

	/**
	 * 获取指定路径的缓存文件
	 * 
	 * @param dirPath
	 * @param fileName
	 * @return
	 */
	public File getFile(String dirPath, String fileName) {
		try {
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File logFile = new File(dir, fileName);
			if (!logFile.exists()) {
				logFile.createNewFile();
			}

			return logFile;
		} catch (Exception ex) {
			Log.e(TAG,ex.toString());
			return null;
		}
	}

	/**
	 * 保存特定前缀的标志值至内部存储
	 * 
	 * @param dirPath
	 * @param filename
	 * @param flagPrefix
	 * @param falgValue
	 */
	public void saveFlag2Internal(String dirPath, String filename,
			String flagPrefix, String falgValue) {
		try {
			if (!checkPermission(mContext)){
				return ;
			}
			String flag = flagPrefix + ":" + falgValue;

			cleanFileContent(dirPath, filename);

			File flagFile = getFile(dirPath, filename);

			FileOutputStream fos = new FileOutputStream(flagFile, true);

			// 保存的是加密过的文本内容

			fos.write(QGSdkUtils.encryptAES(flag, Constant.signkey).getBytes(CHARSET));

			fos.close();
		} catch (Exception ex) {
			Log.e(TAG,ex.toString());
		}
	}

	/**
	 * 获取特定前缀的标志值
	 * 
	 * @param dirPath
	 * @param filename
	 * @param flagPrefix
	 * @return
	 */
	public String getFlagFromInternal(String dirPath, String filename,
			String flagPrefix) {
		try {
			if (!checkPermission(mContext)){
				return "";
			}
			String flag = "";
			String cacheContent = getFileContent(
					dirPath, filename);

			if (TextUtils.isEmpty(cacheContent)) {
				Log.d(TAG, "gffi return empty");
				return flag;
			}

			if (cacheContent.startsWith(flagPrefix)) {
				String flagValue = cacheContent.substring(
						cacheContent.indexOf(":") + 1, cacheContent.length());
				return flagValue;
			}

			return flag;
		} catch (Exception ex) {
			Log.e(TAG,ex.toString());
			return "";
		}
	}

	/**
	 * 读取用户缓存文件内容　
	 * 
	 * @param dirPath
	 * @param fileName
	 * @return
	 */
	public String getFileContent(String dirPath, String fileName) {
		String cacheContent = "";
		String encryptContent = "";
		File file = getFile(dirPath, fileName);
		if (file != null) {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					// 分行读取
					while ((line = buffreader.readLine()) != null) {
						encryptContent += line + "\n";
					}
					instream.close();
					buffreader.close();

					if (TextUtils.isEmpty(encryptContent)) {
						return "";
					}
					
					// 保存在缓存文件的内容是加密过的，因此返回的时候，需要先解密
					cacheContent = QGSdkUtils.decryptAES(encryptContent,
							Constant.signkey);
				}
			} catch (Exception ex) {
				Log.e(TAG,"getFileContent: "+ex.toString());
				return "";
			}
		}
		return cacheContent;
	}


	/**
	 * 清空指定路径的缓存文件
	 * 
	 * @param dirPath
	 * @param fileName
	 */
	public void cleanFileContent(String dirPath, String fileName) {
		try {
			if (TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(fileName)) {
				return;
			}

			File file = new File(new File(dirPath), fileName);
			if (file.exists() && !file.isDirectory()) {
				FileWriter fstreamWrite = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fstreamWrite);
				out.write("");
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 检查权限
	 */
	public boolean checkPermission(Context context){
		int checkWritePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(checkWritePermission==0){
			return true;
		}else
		{
			return false;
		}
	}

}
