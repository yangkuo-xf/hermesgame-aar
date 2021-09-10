package com.hermesgamesdk.entity;

import java.util.List;

public class QGUserInfo {
	// 实名认证标志，默认值是-1，表示没有实名认证的功能，用户中心需要隐藏实名认证栏（游戏不开实名认证，或注册的时候，服务端都不会返回这个字段，我们就会使用默认值）
	// 即，注册之后，用户中心不会有实名认证选项  0 实名了 或者没开 1开了 但不强制   2.开了 强制

	private int checkRealName = -1;
	private String authToken;
	private Userdata userData;
	private ExtInfo extInfo;
	private boolean isAdult;



	private int guestRealName=1;//表示游客是否需要实名认证，1为需要，0不需要



	private int ckPlayTime=0;
	private int uAge=-1;
	private int id;
	private String message;

	private double sdkCoinNum=0;

	public int getCkPlayTime() {
		return ckPlayTime;
	}

	public void setCkPlayTime(int ckPlayTime) {
		this.ckPlayTime = ckPlayTime;
	}

	public double getSdkCoinNum() {
		return sdkCoinNum;
	}

	public void setSdkCoinNum(double sdkCoinNum) {
		this.sdkCoinNum = sdkCoinNum;
	}


	public int getGuestRealName() {
		return guestRealName;
	}

	public void setGuestRealName(int guestRealName) {
		this.guestRealName = guestRealName;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isAdult() {
		return isAdult;
	}

	public void setAdult(boolean isAdult) {
		this.isAdult = isAdult;
	}

	public int getuAge() {
		return uAge;
	}

	public void setuAge(int uAge) {
		this.uAge = uAge;
	}

	public void setAuthtoken(String authtoken) {
		this.authToken = authtoken;
	}

	public ExtInfo getExtInfo() {
		return extInfo;
	}

	public void setExtInfo(ExtInfo extInfo) {
		this.extInfo = extInfo;
	}

	public String getAuthtoken() {
		return authToken;
	}

	public void setUserdata(Userdata userdata) {
		this.userData = userdata;
	}

	public Userdata getUserdata() {
		return userData;
	}

	public void setCheckrealname(int checkrealname) {
		this.checkRealName = checkrealname;
	}

	public int getCheckrealname() {
		return checkRealName;
	}
	public static class ExtInfo{
		private int oauthType;
		private String oauthId="0";


		private String access_token="0";

		public int getOauthType() {
			return oauthType;
		}

		public void setOauthType(int oauthType) {
			this.oauthType = oauthType;
		}

		public String getOauthId() {
			return oauthId;
		}

		public void setOauthId(String oauthId) {
			this.oauthId = oauthId;
		}
		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

	}
	public static class Erro{
		private int id;
		private String message;
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	}
	public static class BindUsers{


		private String 	authToken ;
		private String uid;
		private String username;
		private String 	level ;
		private String serverName;
		private String roleName="";

		public String getAuthToken() {
			return authToken;
		}

		public void setAuthToken(String authToken) {
			this.authToken = authToken;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}


		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String getServerName() {
			return serverName;
		}

		public void setServerName(String serverName) {
			this.serverName = serverName;
		}

		public String getRoleName() {
			return roleName;
		}

		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}



	}


	public static class Userdata {

		private String uid;
		private String username;
		private int isGuest;
		private String token;

		public String getSiId() {
			return siId;
		}

		public void setSiId(String siId) {
			this.siId = siId;
		}

		private String siId="0";

		private List<BindUsers> bindUsers;

		private String mobile;


		private int isMbUser;

		private int needActive;
		private String activeTips;
		public int getIsMbUser() {
			return isMbUser;
		}
		public List<BindUsers> getBindUsers() {
			return bindUsers;
		}

		public void setBindUsers(List<BindUsers> bindUsers) {
			this.bindUsers = bindUsers;
		}
		public void setIsMbUser(int isMbUser) {
			this.isMbUser = isMbUser;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getActiveTips() {
			return activeTips;
		}

		public void setActiveTips(String activeTips) {
			this.activeTips = activeTips;
		}

		public int getNeedActive() {
			return needActive;
		}

		public void setNeedActive(int needActive) {
			this.needActive = needActive;
		}

		private String upwd;

		public int getIsGuest() {
			return isGuest;
		}

		public void setIsGuest(int isGuest) {
			this.isGuest = isGuest;
		}

		public String getUpwd() {
			return upwd;
		}

		public void setUpwd(String upwd) {
			this.upwd = upwd;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getUid() {
			return uid;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getUsername() {
			return username;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getToken() {
			return token;
		}

	}

}
