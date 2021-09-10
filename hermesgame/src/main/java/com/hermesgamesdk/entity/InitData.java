package com.hermesgamesdk.entity;

/**
 * Copyright 2017 aTool.org
 */

import java.util.List;

public class InitData {

	private Productconfig productConfig;
	private Version version;
	private AppAuthInfo	appAuthInfo;//盒子授权信息
	private int realNameNode;//实名节点
	private String opRdPack="0";

	public String getOpRdPack() {
		return opRdPack;
	}

	public void setOpRdPack(String opRdPack) {
		this.opRdPack = opRdPack;
	}


	public int getUseEWallet() {
		return useEWallet;
	}

	public void setUseEWallet(int useEWallet) {
		this.useEWallet = useEWallet;
	}

	private int useEWallet;

	public int getRealNameNode() {
		return realNameNode;
	}

	public void setRealNameNode(int realNameNode) {
		this.realNameNode = realNameNode;
	}

	private List<Paytypes> payTypes;

	private String forbidEmt="0";
	public String getForbidEmt() {
		return forbidEmt;
	}

	public void setForbidEmt(String forbidEmt) {
		this.forbidEmt = forbidEmt;
	}

	public void setProductconfig(Productconfig productconfig) {
		this.productConfig = productconfig;
	}

	public Productconfig getProductconfig() {
		return productConfig;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public Version getVersion() {
		return version;
	}

	public void setPaytypes(List<Paytypes> paytypes) {
		this.payTypes = paytypes;
	}

	public List<Paytypes> getPaytypes() {
		return payTypes;
	}
	public AppAuthInfo getAppAuthInfo() {
		return appAuthInfo;
	}

	public void setAppAuthInfo(AppAuthInfo appAuthInfo) {
		this.appAuthInfo = appAuthInfo;
	}

	public static class Paytypes {

		private String payName;

		private int payTypeId;

		private Rebate rebate;//折扣

		public Rebate getRebate() {
			return rebate;
		}

		public void setRebate(Rebate rebate) {
			this.rebate = rebate;
		}

		public void setPayname(String payname) {
			this.payName = payname;
		}

		public String getPayname() {
			return payName;
		}

		public void setPaytypeid(int paytypeid) {
			this.payTypeId = paytypeid;
		}

		public int getPaytypeid() {
			return payTypeId;
		}

		public static class Rebate {

			private String rate;

			private String rateval;

			public String getRateval() {
				return rateval;
			}

			public void setRateval(String rateval) {
				this.rateval = rateval;
			}

			private List<RateConfig> rateConfig;

			public String getRate() {
				return rate;
			}

			public void setRate(String rate) {
				this.rate = rate;
			}

			public List<RateConfig> getRateConfig() {
				return rateConfig;
			}

			public void setRateConfig(List<RateConfig> rateConfig) {
				this.rateConfig = rateConfig;
			}

			public static class RateConfig {
				private String minval;
				private String maxval;
				private String rate;
				private String rateval;

				public String getRateval() {
					return rateval;
				}

				public void setRateval(String rateval) {
					this.rateval = rateval;
				}

				public String getMinval() {
					return minval;
				}

				public void setMinval(String minval) {
					this.minval = minval;
				}

				public String getMaxval() {
					return maxval;
				}

				public void setMaxval(String maxval) {
					this.maxval = maxval;
				}

				public String getRate() {
					return rate;
				}

				public void setRate(String rate) {
					this.rate = rate;
				}
			}
		}
	}

	public static class Version {
		private String versionNo;
		private String updateTips;
		private String versionName;
		private String updateTime;
		private String isMust; //是否强制更新
		private String versionUrl;

		public void setVersionNo(String versionno) {
			this.versionNo = versionno;
		}

		public String getVersionNo() {
			return versionNo;
		}

		public void setUpdatetips(String updatetips) {
			this.updateTips = updatetips;
		}

		public String getUpdatetips() {
			return updateTips;
		}

		public void setVersionname(String versionname) {
			this.versionName = versionname;
		}

		public String getVersionname() {
			return versionName;
		}

		public void setUpdatetime(String updatetime) {
			this.updateTime = updatetime;
		}

		public String getUpdatetime() {
			return updateTime;
		}

		public void setIsmust(String ismust) {
			this.isMust = ismust;
		}

		public String getIsmust() {
			return isMust;
		}

		public void setVersionurl(String versionurl) {
			this.versionUrl = versionurl;
		}

		public String getVersionurl() {
			return versionUrl;
		}

	}

	public static class Productconfig {
		private String isShowFloat;
		private String logo;//浮标icon
		private String useSms;
		private String title;

		private String switchWxAppPlug="0";//是否切换到微信支付插件


		private String rpScrollSwitch="1";//是否展示红包滚动 不等于1 就展示



		private String rpDialogSwitch="1";//红包弹窗开关字段：rpDialogSwitch，有此字段并且等于1时才关闭红包弹窗，其余都默认显示弹窗


		public String getSwitchWxAppPlug() {
			return switchWxAppPlug;
		}

		public String getRpDialogSwitch() {
			return rpDialogSwitch;
		}

		public void setRpDialogSwitch(String rpDialogSwitch) {
			this.rpDialogSwitch = rpDialogSwitch;
		}
		public void setSwitchWxAppPlug(String switchWxAppPlug) {
			this.switchWxAppPlug = switchWxAppPlug;
		}

		public String getRpScrollSwitch() {
			return rpScrollSwitch;
		}

		public void setRpScrollSwitch(String rpScrollSwitch) {
			this.rpScrollSwitch = rpScrollSwitch;
		}


		public String getUseAppAuth() {
			return useAppAuth;
		}

		public void setUseAppAuth(String useAppAuth) {
			this.useAppAuth = useAppAuth;
		}

		private String useAppAuth;


		private FcmTips fcmTips;

		private String floatLogo;

		private String ucentUrl;


		private String useServiceCenter;

		private String autoOpenAgreement;

		private String skinStyle;

		private String serviceInfo;

		private String gift;

		private String useBBS;

		private String mainLoginType;  //主要登录方式


		private String useCpLogin;  //是否开启cp的登录方式
		public String getFloatLogo() {
			return floatLogo;
		}

		public void setFloatLogo(String floatLogo) {
			this.floatLogo = floatLogo;
		}

		public String getUseCpLogin() {
			return useCpLogin;
		}

		public void setUseCpLogin(String useCpLogin) {
			this.useCpLogin = useCpLogin;
		}

		public String getUcentUrl() {
			return ucentUrl;
		}

		public void setUcentUrl(String ucentUrl) {
			this.ucentUrl = ucentUrl;
		}


		public String getMainLoginType() {
			return mainLoginType;
		}

		public void setMainLoginType(String mainLoginType) {
			this.mainLoginType = mainLoginType;
		}

		public String getAutoOpenAgreement() {
			return autoOpenAgreement;
		}

		public void setAutoOpenAgreement(String autoOpenAgreement) {
			this.autoOpenAgreement = autoOpenAgreement;
		}


		public String getIsShowFloat() {
			return isShowFloat;
		}

		public void setIsShowFloat(String isShowFloat) {
			this.isShowFloat = isShowFloat;
		}

		public String getUseBBS() {
			return useBBS;
		}

		public void setUseBBS(String useBBS) {
			this.useBBS = useBBS;
		}

		public String getGift() {
			return gift;
		}

		public void setGift(String gift) {
			this.gift = gift;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

		public String getLogo() {
			return logo;
		}

		public void setUsesms(String useSms) {
			this.useSms = useSms;
		}

		public String getUsesms() {
			return useSms;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setUseservicecenter(String useservicecenter) {
			this.useServiceCenter = useservicecenter;
		}

		public String getUseservicecenter() {
			return useServiceCenter;
		}

		public void setSkinstyle(String skinstyle) {
			this.skinStyle = skinstyle;
		}

		public String getSkinstyle() {
			return skinStyle;
		}

		public void setServiceinfo(String serviceinfo) {
			this.serviceInfo = serviceinfo;
		}

		public String getServiceinfo() {
			return serviceInfo;
		}
		public FcmTips getFcmTips() {
			return fcmTips;
		}

		public void setFcmTips(FcmTips fcmTips) {
			this.fcmTips = fcmTips;
		}

		public static class FcmTips{
			private String guestTimeTip;
			private String minorTimeTip;
			private String guestLoginTip;
			private String minorLoginTip;
			public String getGuestLoginTip() {
				return guestLoginTip;
			}

			public void setGuestLoginTip(String guestLoginTip) {
				this.guestLoginTip = guestLoginTip;
			}

			public String getMinorLoginTip() {
				return minorLoginTip;
			}

			public void setMinorLoginTip(String minorLoginTip) {
				this.minorLoginTip = minorLoginTip;
			}


			public String getGuestTimeTip() {
				return guestTimeTip;
			}

			public void setGuestTimeTip(String guestTimeTip) {
				this.guestTimeTip = guestTimeTip;
			}

			public String getMinorTimeTip() {
				return minorTimeTip;
			}

			public void setMinorTimeTip(String minorTimeTip) {
				this.minorTimeTip = minorTimeTip;
			}
		}


	}

	public static class AppAuthInfo{

		public String getAppLogo() {
			return appLogo;
		}

		public void setAppLogo(String appLogo) {
			this.appLogo = appLogo;
		}

		public String getAppPackage() {
			return appPackage;
		}

		public void setAppPackage(String appPackage) {
			this.appPackage = appPackage;
		}

		public String getTheme() {
			return theme;
		}

		public void setTheme(String theme) {
			this.theme = theme;
		}
		public String getDefaultAvatar() {
			return defaultAvatar;
		}
		public void setDefaultAvatar(String defaultAvatar) {
			this.defaultAvatar = defaultAvatar;
		}
		private String appLogo;
		private String appPackage;
		private String theme;
		private String defaultAvatar;
	}

}