package com.hermesgamesdk.utils;

import android.content.Context;
import android.util.Log;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;


/**
 * Created by zheng on 2019/8/22.
 */

public class MiitHelper implements IIdentifierListener {

	private AppIdsUpdater _listener;
	private IdSupplier mIdSupplier;

	public MiitHelper(AppIdsUpdater callback) {
		_listener = callback;
	}

	public void getDeviceIds(Context cxt) {
		long timeb = System.currentTimeMillis();
		int nres = CallFromReflect(cxt);
		// int nres=DirectCall(cxt);
		long timee = System.currentTimeMillis();
		long offset = timee - timeb;
		if (nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT) {// 不支持的设备

		} else if (nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE) {// 加载配置文件出错

		} else if (nres == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT) {// 不支持的设备厂商

		} else if (nres == ErrorCode.INIT_ERROR_RESULT_DELAY) {// 获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程

		} else if (nres == ErrorCode.INIT_HELPER_CALL_ERROR) {// 反射调用出错

		}
		Log.d("hermesgame", "OAID getDeviceIds ErrorCode: " + String.valueOf(nres));

	}

	/*
	 * 通过反射调用，解决android 9以后的类加载升级，导至找不到so中的方法
	 */
	private int CallFromReflect(Context cxt) {
		Log.d("hermesgame", "OAID getDeviceIds CallFromReflect");
		return MdidSdkHelper.InitSdk(cxt, true, this);
	}

	@Override
	public void OnSupport(boolean isSupport, IdSupplier _supplier) {
		if (_supplier == null) {
			return;
		}
		mIdSupplier = _supplier;
		String oaid = _supplier.getOAID();
		String vaid = _supplier.getVAID();
		String aaid = _supplier.getAAID();

		if (_listener != null) {
			_listener.OnIdsAvalid(oaid, vaid, aaid);
		}
	}

	public interface AppIdsUpdater {
		void OnIdsAvalid(String oaid, String vaid, String aaid);
	}

	public void shutDownMSA() {
		// mIdSupplier.shutDown();
	}

}
