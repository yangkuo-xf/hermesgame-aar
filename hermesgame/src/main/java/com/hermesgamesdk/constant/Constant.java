package com.hermesgamesdk.constant;

import java.io.File;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.hermesgamesdk.manager.InitManager;
import com.hermesgamesdk.utils.QGSdkUtils;

/**
 * Created by Administrator on 2017/9/5.
 */

public class Constant {

	// -------------------------url------------------------------------
	// TODO 网络轮询
	// 访问地址
	public static String HOST = "";
	// 访问地址2
	public static String HOST_RE = "";
	// -------------------------url------------------------------------
	// 初始化
	public static final String INIT = "/v1/system/init";

	// 检查支付方式
	public static final String CHECK_PAY_TYPE = "/v1/auth/jianchaLayLx";
	// 账号注册
	public static final String REGISTER = "/v1/user/registerUser";
	// 游客注册（根据设备号注册一个账号）
	public static final String REGISTER_TRY_PLAY = "/v1/user/registerUser";
	// 角色信息
	public static final String GAMEROLE = "/v1/auth/setGameRoleInfo";
	// 实名认证
	public static final String IDENTIFICATION = "/v1/auth/checkRealName";
	// 账号登录
	public static final String ACCOUNT_LOGIN = "/v1/user/loginByName";
	// 游客登录
	public static final String GUEST_LOGIN = "/v1/user/registerVisitor";

	//添加小号
	public static final String ADD_ACCOUNT = "/v1/auth/regSubUser";
	// 获取验证码
	public static final String GET_IDENTFING_CODE = "/v1/user/sendCode";
	// 手机登录
	public static final String PHOEN_LOGIN = "/v1/user/loginByPhone";
	// 手机绑定 参数 uid phone code
	public static final String PHONE_BIND = "/v1/auth/bindPhone";
	// 手机解绑 参数 uid code
	public static final String PHONE_UNBIND = "/v1/auth/unBindPhone";
	//防沉迷
	public static final String ADDICTION_PREVENTION= "/v1/auth/logout";
	// 找回密码
	public static final String FIND_PASSWORD = "/v1/user/findPassByPhone";
	// 用户协议
	public static final String AGREEMENT = "/v1/system/getAgreement";
	// 支付
	public static final String START_PAY = "/v1/auth/createOrder";
	//设置支付密码
	public static final String SET_PAY_PASS = "/v1/auth/setPytPass";
	public static final String MOBILE_BIND_USER = "/v1/system/ckAccount";

	// 查询平台币
	public static final String CHECK_SDK_COINS = "/v1/auth/walletInfo";
	// 查询订单结果
	public static final String REQUST_PAY_RESULT = "/v1/auth/queryOrd";
	// 第三方登录
	public static final String THIRD_LOGIN = "/v1/user/userLoginByOtherSdk";
	// 用户额外数据
	public static final String USER_EXTRA_INFO = "/v1/auth/getUserInfo";
	// 修改密码
	public static final String MODIFY_PASSWORD = "/v1/auth/changePassword";
	// token 自动登录
	public static final String AUTO_TOKEN = "/v1/user/autoLogin";
	// 游客账号绑定用户名
	public static final String BIND_USERNAME = "/v1/auth/bindUsername";
	// 礼包中心
	public static final String GIFT_CENTER = "/payH5/giftcenter";

	// 用户激活
	public static final String ACTIVE_PLAYER = "/v1/user/activePlayer";
	// 上传在线时间
	public static final String ONLINETIME = "/v1/auth/asyUonline";

	// 测试超时
	public static final String TEST_TIMEOUT = "/v1/system/cknet";

	public static final String CHECK_DEVICE_BIND ="/v1/system/ckDevAccount";

	// 公告
	public static final String NOTICE = "/v1/system/getNotice";
	// BBS
	public static final String BBS = "http://gamebbs.quicksdk.net";
	// SliderBar
	public static final String SLIDER = "http://qkgamesdk.quickapi.net/userCenter/play/"; // http://qkgamesdk.quickapi.net/userCenter/play
																							// http://10.0.18.4:1034/userCenter/play/

	// -----------------------------------openType 做第三方登录接口用--------------------------------------
	public static final String OPEN_TYPE_WX = "4";// 微信
	public static final String OPEN_TYPE_QQ = "5";// QQ
	public static final String OPEN_TYPE_WB = "6";// 微博
	public static final String OPEN_TYPE_CP = "17";// CP自定义方式
	public static final String OPEN_TYPE_ALYUN= "18";// 阿里云一键授权
	public static final String OPEN_TYPE_TAPTAP = "19";// TAPTAP授权
	public static final int CP_LOGIN_REQUEST = 1010;// CP自定义方式请求code
	public static final int CP_LOGIN_OK = 1;// CP自定义方式成功code
	// -----------------------------------loginType 返回给cp用--------------------------------------
	public static final String LOGIN_TYPE_AUTO = "0";// 自动登录
	public static final String LOGIN_TYPE_ACCOUNT = "1";// 账号登录(包含游客及静默登录)
	public static final String LOGIN_TYPE_VERIFYCODE = "2";// 手机验证码
	public static final String LOGIN_TYPE_QQ = "4";// qq授权
	public static final String LOGIN_TYPE_WEIXIN = "5";//微信授权
	public static final String LOGIN_TYPE_ALYUN = "6";// 阿里云一键授权
	public static final String LOGIN_TYPE_TAPTAP = "7";//taptap授权
	// --------------------------------请求短信验证码-----------------------------------------
	public static final int SMS_TYPE_LOGIN = 1;
	public static final int SMS_TYPE_BIND = 2;
	public static final int SMS_TYPE_UNBIND = 3;
	public static final int SMS_TYPE_FIND_PASSWORD = 4;
	// ----------------------------数据存储key----------------------------------------
	public static final String USERINFO_KEY = "userInfo";
	public static final String USEREXTRAINFO_KEY = "userExtraInfo";
	public static final String INIT_KEY = "initData";
	public static final String WALLET_KEY = "walletInfo";
	public static final String DEVICEID_KEY = "device_id";
	public static final String DEVICEID_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "hermesgame"
			+ File.separator;
	public static final String DEVICEID_FILENAME="device_id";
	public static final String DEVICEID_PREFIX ="device_id";

	// --------------------------账号类型-------------------------------------
	// 游客账号
	public static final int GUEST_ACCOUNT = 1;
	// 非游客账号
	public static final int NORMAL_ACCOUNT = 0;
	//

	public static String PRODUCT_ID;
	// 游戏版本号
	public static int VERSION_CODE;
	// 游戏版本名
	public static String VERSION_NAME;
	// 分包渠道ID
	public static String CHANNEL_ID;
	// 头条星图渠道ID
	public static String BYTEDANCE_ID;

	// quicksdk产品号，客服初始化需要用到
	public static String PRODUCT_CODE = "";
	// SDK版本号
	public static final int SDK_VERSION = 543;
	// SDK版本名
	public static final String SDK_VERSION_NAME = "V5.4.3";
	// 平台标识，1代表安卓
	public static final int PLATFORM = 1;

	// ------------------------------参数加密秘钥-------------------------

	public static final String signkey = "0b2a18e45d7df321";
	public static final String signkey_gift = "8e45320d7dfb2a11";

	// ------------------------------实名认证状态--------------------------

	// 不需要实名认证，即没有实名认证功能
	public static final int CRETIFICATION_NOT_NEED = -1;
	// 已经认证了
	public static final int CRETIFICATION_HAS_CERTIFIED = 0;
	// 需要实名认证，且实名认证界面可关闭
	public static final int CRETIFICATION_CAN_CLOSE = 1;
	// 需要实名认证，且实名认证界面不可关闭
	public static final int CRETIFICATION_CAN_NOT_CLOSE = 2;

	// ---------------------------------------------------------------

	// 登录时候弹出实名认证
	public static final int CRETIFICATION_DISPLAY_ON_LOGIN = 2;
	// 支付之前弹出实名认证
	public static final int CRETIFICATION_DISPLAY_ON_PAY = 3;

	// 只在悬浮窗显示实名认证
	public static final int CRETIFICATION_DISPLAY_ON_FLOATVIEW = 1;

	// ---------------公告弹出节点-----------------------------
	// 无公告配置
	public static final int NOTICE_NODE_NO_NOTICE = -1;
	// 不直接弹出，仅在悬浮窗显示
	public static final int NOTICE_NODE_FLOAT = 0;
	// 登录前弹出
	public static final int NOTICE_NODE_BEFORE_LOGIN = 1;
	// 登录后弹出
	public static final int NOTICE_NODE_AFTER_LOGIN = 2;
	// 将公告存储在map的key
	public static String noticeContent;
	public static String noticeTitle;
	// 公告显示节点
	public static int noticeShowNode = NOTICE_NODE_NO_NOTICE;

	// ----------------------------------------------------
	// 不支持客服
	public static final int NOT_SUPPORT_CUSTOM_SERVICE = 0;
	// 免费客服
	public static final int COSTOM_SERVICE_FREE = 1;
	// 支持在线客服，收费
	public static final int COSTOM_SERVICE_NOT_FREE = 2;
	// ---------------------------------------------------------------
	// SharePreferences Key
	public static final String SP_ACCOUNT = "account";
	public static final String SP_PASSWORD = "password";
	public static final String SP_ACCOUNT_INFO = "ACCOUNT_INFO";

	// 平台类型常量
	public static String OAID = "0";
	public static String VAID = "0";
	public static String AAID = "0";

	public static void reSetHost(Context context) {
		String hoststString = null;
		String[] hosts = InitManager.getInstance().getIPS(context);
		if (hosts != null) {
			hoststString = hosts[0];
		}
		if (hosts.length >= 2) {
			HOST_RE = hosts[1];
		}
		if (hoststString.isEmpty() || hoststString.equals("{{$qg_ip_config}}")) {
			HOST = "http://hermesgame.sdk.quicksdk.net";
			HOST_RE ="http://hermesgame.sdk.quicksdk.net";
		} else if (isUrl(hoststString)) {
			HOST = checkip(hoststString);
		} else {
			Log.d("hermesgame", "qg_ipconfig.txt erro：" + hoststString);
			QGSdkUtils.showToast(context, "HOST错误");
		}
		Log.d("hermesgame", "Host=" + HOST);
		Log.d("hermesgame", "HOST_RE=" + HOST_RE);
	}

	private static Boolean isUrl(String url) {
		Pattern pattern = Pattern
				.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
		if (pattern.matcher(url).matches()) {
			return true;
		}
		return false;
	}

	private static String checkip(String urlString) {
		if (urlString.endsWith("/")) {
			int endIndex = urlString.length() - 1;
			urlString = urlString.substring(0, endIndex);
		}
		return urlString;

	}

	public static void destory() {
		// 将公告存储在map的key
		noticeContent = null;
		noticeTitle = null;
		// 公告显示节点
		noticeShowNode = NOTICE_NODE_NO_NOTICE;
	}
}
