package com.hermesgamesdk.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.util.Log;

public class HolidayUtils {
	public static HashMap<String, String> holidays = new HashMap<String, String>();
	public static HashMap<String, String> holidays_old = new HashMap<String, String>();
	public static HolidayUtils instance;

	public HolidayUtils() {
		holidays.put("01", "01");// 元旦
		holidays.put("05", "01");// 五一
		holidays.put("10", "n");
	/*	holidays.put("10", "01");
		holidays.put("10", "02");
		holidays.put("10", "03");*/// 国庆 单独判断
		holidays_old.put("1", "n");
	/*	holidays_old.put("1", "1");// 春节单独判断
		holidays_old.put("1", "2");
		holidays_old.put("1", "3");*/
		holidays_old.put("5", "5");// 端午
		holidays_old.put("8", "15");// 中秋
	}

	public static HolidayUtils getInstance() {
		if (instance == null)
			instance = new HolidayUtils();
		return instance;
	}

	public boolean isHolidays(String timestamp) {
		// 转化header里面的时间 10位时间戳
		SimpleDateFormat dateformat = new SimpleDateFormat("MM-dd");
		String dateStr = dateformat.format(Long.valueOf(timestamp) * 1000);
		String m = dateStr.split("-")[0];
		String d = dateStr.split("-")[1];
		Log.d("hermesgame", "HolidayUtils  timestamp: " + dateStr + "   m:" + m + "    d:" + d);
		// 公立节假日
		if (holidays.containsKey(m)) {
			if (holidays.get(m).equals(d)) {
				return true;
			}else if (m.equals("10")){
				if(d.equals("01")||d.equals("02")||d.equals("03")){
					return true;
				}else{
					return checkLunar(timestamp);
				}
			}
			else {
				// 判断阴历
				return checkLunar(timestamp);
			}
		} else {
			// 判断阴历
			Log.d("hermesgame", "holidays.not  containsKey ");
			return checkLunar(timestamp);
		}
	}

	public boolean checkLunar(String time) {
		try {
			SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
			Calendar today = Calendar.getInstance();
			String oldDateStr = chineseDateFormat.format(Long.valueOf(time) * 1000);
			today.setTime(chineseDateFormat.parse(oldDateStr));
			Lunar lunar = new Lunar(today);
			Log.d("hermesgame", "HolidayUtils oldYear    month" + lunar.month + "   day" + lunar.day);
			if (holidays_old.containsKey("" + lunar.month)) {
				if (holidays_old.get("" + lunar.month).equals("" + lunar.day)) {
					return true;
				}else if(lunar.month==1){
					if (lunar.day==1||lunar.day==2||lunar.day==3){
						return true;
					}else{
						return false;
					}
				}
				else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("hermesgame", "oldYear Exception");
			return false;
		}
	}
}
