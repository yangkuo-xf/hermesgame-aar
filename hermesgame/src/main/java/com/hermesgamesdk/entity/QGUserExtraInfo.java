package com.hermesgamesdk.entity;

import android.text.TextUtils;

/**
 * 用户中心数据
 */
public class QGUserExtraInfo {

	private String nickName;
	private String phone;
	private int isBindPhone;
	private int sexType;

	public static final int GENDER_UNDEFINE = 0;
	public static final int GENDER_MALE = 1;
	public static final int GENDER_FEMALE = 2;

	public static final int IS_BIND_PHONE = 1;
	public static final int IS_UNBIND_PHONE = 0;

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getSexType() {
		return sexType;
	}

	public void setSexType(int sexType) {
		this.sexType = sexType;
	}

	public int getIsBindPhone() {
		return isBindPhone;
	}

	public void setIsBindPhone(int isBindPhone) {
		this.isBindPhone = isBindPhone;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		// 需要将中间4位设置* 例如135****7890
		if (TextUtils.isEmpty(phone) || phone.length() < 11) {
			this.phone = phone;
			return;
		}
		this.phone = phone.subSequence(0, 3) + "****" + phone.subSequence(7, 11);
	}

	@Override
	public String toString() {
		return "QGUserExtraInfo{" + "nickName='" + nickName + '\'' + ", sexType=" + sexType + ", isBindPhone="
				+ isBindPhone + '}';
	}
}
