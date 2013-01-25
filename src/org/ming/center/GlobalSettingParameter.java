// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package org.ming.center;

import java.util.List;

import org.ming.center.database.UserAccount;

import android.os.Environment;

public class GlobalSettingParameter
{

	public static final String CACH_PATH = "/sdcard/redcloud/";
	public static boolean DOBLY_LOCAL_MUSIC = false;
	public static final String INTIAL_URL = "inital.xml";
	public static boolean IsBBK_Y3T = false;
	public static boolean IsMonthCheck = false;
	public static String LOCAL_PARAM_DEFAULT_SKIN_STYLE_NAME = "DefaultSkinStyle";
	public static final String LOCAL_PARAM_DOBLY_MUSIC_STORE_SD_DIR = (new StringBuilder())
			.append(Environment.getExternalStorageDirectory())
			.append("/12530/dolby/").toString();
	public static final String LOCAL_PARAM_FLAT = "Android";
	public static final String LOCAL_PARAM_MODE = "chinamobile";
	public static final String LOCAL_PARAM_MUSIC_STORE_SD_DIR = (new StringBuilder())
			.append(Environment.getExternalStorageDirectory())
			.append("/12530/song/").toString();
	public static final String LOCAL_PARAM_MV_STORE_SD_DIR = (new StringBuilder())
			.append(Environment.getExternalStorageDirectory())
			.append("/12530/mv/").toString();
	public static final String LOCAL_PARAM_RINGTONE_STORE_SD_DIR = (new StringBuilder())
			.append(Environment.getExternalStorageDirectory())
			.append("/12530/ringtone/").toString();
	public static String LOCAL_PARAM_SKIN_CONFIG_FILE_NAME = "skin.xml";
	public static final String LOCAL_PARAM_SKIN_STORE_SD_DIR = (new StringBuilder())
			.append(Environment.getExternalStorageDirectory())
			.append("/12530/skin/").toString();
	public static String LOCAL_PARAM_SVN_VERSION = "r10629";
	public static final String LOCAL_PARAM_UPDATE_STORE_SD_DIR = (new StringBuilder())
			.append(Environment.getExternalStorageDirectory())
			.append("/12530/update/").toString();
	public static String LOCAL_PARAM_USER_AGENT = "MobileMusic_DefaultUA";
	public static String LOCAL_PARAM_VERSION = "4.00001";
	public static String LOCAL_SVN_NUMBER = "";
	public static String LOGIN_PARAM_FOR_COLOR_TONE = null;
	public static String LOGIN_PARAM_FOR_COLOR_TONE_CANCEL = null;
	public static String LOGIN_PARAM_FOR_COLOR_TONE_INFO = null;
	public static final int MAX_NUMBER_OF_SONG_IN_DEFAULT_PLAYLIST = 20;
	public static final int NOTIFICATION_DOWNLOAD_REMAINING_STATUS_ID = 3;
	public static final int NOTIFICATION_DOWNLOAD_STATUS_ID = 2;
	public static final int NOTIFICATION_PLAYBACK_STATUS_ID = 1;
	public static final int ONLINE_SONG_NO_RIGHT_LISTEN_TIME = 30000;
	public static final int RESERVE_SPACE = 0x200000;
	public static int SCREEN_HEIGHT = 0;
	public static int SCREEN_WIDTH = 0;
	public static String SERVER_INIT_IS_SICHUAN_MEMBER = null;
	public static String SERVER_INIT_PARAM_BITERATE = null;
	public static String SERVER_INIT_PARAM_ITEM_COUNT = null;
	public static String SERVER_INIT_PARAM_MDN = null;
	public static String SERVER_INIT_PARAM_MEMBER = null;
	public static String SERVER_INIT_PARAM_NEED_UPDATE = null;
	public static String SERVER_INIT_PARAM_ONLINE_MUSIC_ORDER_STATUS = null;
	public static String SERVER_INIT_PARAM_QQORDER = null;
	public static String SERVER_INIT_PARAM_SEARCH_INFO = null;
	public static String SERVER_INIT_PARAM_UPDATE_INFO = null;
	public static String SERVER_INIT_PARAM_UPDATE_URL = null;
	public static String SERVER_INIT_PARAM_UPDATE_VERSION = null;
	public static String SERVER_INIT_RANDOMSESSIONKEY = null;
	public static String SKIN_ZIP_FILE_SUFFIX_NAME = ".zip";
	public static String UPDATE_TAG_IMEI_INFO = null;
	public static String UPDATE_TAG_IMSI_INFO = null;
	public static final boolean USE_MOCK = true;
	public static String WEIBO_DB_NAME = "";
	public static boolean isAppSetWlan = false;
	public static boolean isCheckOlderMusicVersion = true;
	public static boolean isLogining = false;
	public static boolean isWlanLogined = false;
	public static String loginMobileNum = null;
	public static String loginRadomNum = null;
	public static List memberInfoItemList = null;
	public static List orderInfoItemList = null;
	public static List qqInfoItemList = null;
	public static boolean show_dobly_toast = false;
	public static String xmlRawDataForLogin = null;
	public static UserAccount useraccount = null;

	public GlobalSettingParameter()
	{
	}

	public static boolean initLoginParam(byte abyte0[])
	{
		return false;
	}

	public static boolean initServerParam(byte abyte0[])
	{
		return false;
	}

	static
	{
		SCREEN_WIDTH = -1;
		SCREEN_HEIGHT = -1;
		IsBBK_Y3T = false;
		IsMonthCheck = false;
		DOBLY_LOCAL_MUSIC = false;
	}
}
