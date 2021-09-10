package com.hermesgamesdk.utils;

import java.io.UnsupportedEncodingException;

import android.util.Base64;

public class Base64Utils {
	/**
	 * 加密
	 * 
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
		String strBase64 = null;
		try {
			strBase64 = new String(Base64.encode(str.getBytes("utf-8"), Base64.DEFAULT));
		} catch (UnsupportedEncodingException e) {
		}
		return strBase64;
	}

	/**
	 * 解密
	 * 
	 * @param str
	 * @return
	 */
	public static String decode(String str) {
		String strBase64 = null;
		try {
			strBase64 = new String(Base64.decode(str.getBytes("utf-8"), Base64.DEFAULT));
		} catch (UnsupportedEncodingException e) {
		}
		return strBase64;
	}
}
