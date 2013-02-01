package org.ming.center.business;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicBusinessDefine_Net
{
	public static String DES_ENCRYPTION_KEY = "jbXGHTytBAxFqleSbJ";
	public static final int HTTPS_REQ_ADDMUSICTOSERVER = 5053;
	public static final int HTTPS_REQ_CANCEL_COLORRING_CODE = 5064;
	public static final int HTTPS_REQ_END_CODE = 5063;
	public static final int HTTPS_REQ_FEEDBACK = 5057;
	public static final int HTTPS_REQ_GARBAGEMUSICTOSERVER = 5054;
	public static final int HTTPS_REQ_QUICKLOGIN_AUTH_CODE = 5066;
	public static final int HTTPS_REQ_QUICKLOGIN_CODE = 5067;
	public static final int HTTPS_REQ_RECOMMEND_SONGINFO = 5058;
	public static final int HTTPS_REQ_REPORT_ERROR_CODE = 5065;
	public static final int HTTPS_REQ_SONG_MARK = 5052;
	public static final int HTTPS_REQ_START_CODE = 5000;
	public static final int HTTPS_REQ_TYPE_ALBUMINFO = 5021;
	public static final int HTTPS_REQ_TYPE_APK_UPDATE = 5012;
	public static final int HTTPS_REQ_TYPE_CANCELSUB = 5029;
	public static final int HTTPS_REQ_TYPE_COMMENT = 5023;
	public static final int HTTPS_REQ_TYPE_CONTENT = 5039;
	public static final int HTTPS_REQ_TYPE_DOWNLOADINFO = 5038;
	public static final int HTTPS_REQ_TYPE_DOWNLOADLIST = 5018;
	public static final int HTTPS_REQ_TYPE_DOWNLOAD_SHARE_LIST = 5034;
	public static final int HTTPS_REQ_TYPE_GET_AUTH_CODE = 5000;
	public static final int HTTPS_REQ_TYPE_GET_CONTENT_LIST = 5009;
	public static final int HTTPS_REQ_TYPE_GET_NET_PLAYLIST = 5032;
	public static final int HTTPS_REQ_TYPE_GET_RADIO = 5007;
	public static final int HTTPS_REQ_TYPE_GET_SHARE_URL = 5013;
	public static final int HTTPS_REQ_TYPE_GET_SONG_LIST = 5008;
	public static final int HTTPS_REQ_TYPE_GROUP = 5006;
	public static final int HTTPS_REQ_TYPE_INFORMATION = 5016;
	public static final int HTTPS_REQ_TYPE_INIT_SERVICE = 5004;
	public static final int HTTPS_REQ_TYPE_LOGIN = 5003;
	public static final int HTTPS_REQ_TYPE_MEMBER = 5026;
	public static final int HTTPS_REQ_TYPE_MEMBERINFO = 5025;
	public static final int HTTPS_REQ_TYPE_MUSICINFO = 5005;
	public static final int HTTPS_REQ_TYPE_MUSICMONTHAUTH = 5027;
	public static final int HTTPS_REQ_TYPE_NEWSINFO = 5010;
	public static final int HTTPS_REQ_TYPE_PERSONALLIST = 5020;
	public static final int HTTPS_REQ_TYPE_PLAYLIST = 5017;
	public static final int HTTPS_REQ_TYPE_QUERYMONTH = 5030;
	public static final int HTTPS_REQ_TYPE_QUERY_MONTH = 5035;
	public static final int HTTPS_REQ_TYPE_RADIOSONGLIST = 5031;
	public static final int HTTPS_REQ_TYPE_RADIO_INFO = 5062;
	public static final int HTTPS_REQ_TYPE_REGISTER = 5001;
	public static final int HTTPS_REQ_TYPE_RESET = 5002;
	public static final int HTTPS_REQ_TYPE_SEARCHCONTENT = 5015;
	public static final int HTTPS_REQ_TYPE_SENDLOG = 5024;
	public static final int HTTPS_REQ_TYPE_SETCOLORTONE = 5037;
	public static final int HTTPS_REQ_TYPE_SINGERINFO = 5022;
	public static final int HTTPS_REQ_TYPE_SONGMARK = 5014;
	public static final int HTTPS_REQ_TYPE_SUBSCRIPTION = 5028;
	public static final int HTTPS_REQ_TYPE_TONEINFO = 5036;
	public static final int HTTPS_REQ_TYPE_UPDATECHECK = 5011;
	public static final int HTTPS_REQ_TYPE_UPLOAD_SHARE_LIST = 5033;
	public static final int HTTPS_REQ_TYPE_WAPPUSH = 5019;
	public static String NET_HOST_IP = "https://218.200.227.224/";
	public static String NET_HOST_NAME = "https://218.200.227.224/rdp2/v5.3/";
	public static String NET_MUSICHOST_NAME = "http://218.200.227.225:8080/";
	public static final String TAG_CODE = "code";
	public static final String TAG_COOKIE_NAME = "cookie";
	public static final String TAG_HEADER_RADOM = "randomsessionkey";
	public static final String TAG_MDN = "mdn";
	public static final String TAG_MIGU = "migu";
	public static final String TAG_PASSWORD = "password";
	public static final String TAG_USERNAME = "username";
	public static final String WLAN_CMCC = "CMCC";
	public static final String WLAN_CMCC_EDU = "CMCC-EDU";
	public static final String WLAN_LOGIN_PREF = "LOGIN-PREF";
	public static final String WLAN_LOGIN_PREF_AUTO_LOGIN = "loginactivity.pref.auto.login";
	public static final String WLAN_LOGIN_PREF_PSWD = "loginactivity.pref.pswd";
	public static final String WLAN_LOGIN_PREF_REMEMBER_PSWD = "loginactivity.pref.remember.pswd";
	public static final String WLAN_LOGIN_PREF_USERNAME = "loginactivity.pref.username";
	public static final int WLAN_LOGIN_PSWD_MAX_LENGN = 12;
	public static final int WLAN_LOGIN_PSWD_MIN_LENGN = 6;
	public static final int WLAN_LOGIN_USERNAME_MAX_LENGN = 20;
	public static final int WLAN_LOGIN_USERNAME_MIN_LENGN = 3;
	public static final int WLAN_REQ_TYPE_APPLY_MEMBERSHIP = 5045;
	public static final int WLAN_REQ_TYPE_CANCEL_ONLINE_MUSIC = 5041;
	public static final int WLAN_REQ_TYPE_CANCEL_WHOLE_SONG = 5042;
	public static final int WLAN_REQ_TYPE_DELETE_TONE = 5047;
	public static final int WLAN_REQ_TYPE_GET_DRM_FIRST_BUY_RIGHTS = 5059;
	public static final int WLAN_REQ_TYPE_GET_HELP_INFO = 5044;
	public static final int WLAN_REQ_TYPE_GET_MULTI_PLAYLIST = 5049;
	public static final int WLAN_REQ_TYPE_GET_SONGINFO = 5050;
	public static final int WLAN_REQ_TYPE_GET_TONE_LIST = 5048;
	public static final int WLAN_REQ_TYPE_MODIFY_DEFAULT_TONE = 5046;
	public static final int WLAN_REQ_TYPE_ORDER_COLOR_TONE_SERVICE = 5051;
	public static final int WLAN_REQ_TYPE_ORDER_ONLINE_MUSIC = 5040;
	public static final int WLAN_REQ_TYPE_ORDER_WHOLE_SONG = 5043;
	public static final int WLAN_REQ_TYPE_PRESENT_SONG = 5056;
	public static final int WLAN_REQ_TYPE_QUERY_MONTH = 5061;
	public static final int WLAN_REQ_TYPE_RECOMMEND_SONG = 5055;
	public static final int WLAN_REQ_TYPE_REPORT_RINGTONE_DOWNLOAD = 5060;

	public static boolean checkPhoneNum(String paramString)
	{
		if (!Pattern.compile("^[0-9]{11}$").matcher(paramString).find())
			;
		for (boolean bool = false;; bool = true)
			return bool;
	}
}