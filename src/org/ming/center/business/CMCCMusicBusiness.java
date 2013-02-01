package org.ming.center.business;

import org.ming.center.GlobalSettingParameter;
import org.ming.center.database.UserAccount;

public class CMCCMusicBusiness
{
	public static final int BUSINESS_TYPE_DOWNLOAD = 3;
	public static final int BUSINESS_TYPE_RECOMMEND = 5;
	public static final int BUSINESS_TYPE_RING = 1;
	public static final int BUSINESS_TYPE_SENDMUSIC = 4;
	public static final int BUSINESS_TYPE_VIBERATE = 2;
	public static String CATEGORY_TYPE_0 = "0";
	public static String CATEGORY_TYPE_1 = "1";
	public static String CATEGORY_TYPE_2 = "2";
	public static String CATEGORY_TYPE_3 = "3";
	public static final String INFO_TYPE_CONSTITUTION = "constitution";
	public static final String INFO_TYPE_HELP = "Help";
	public static final String INFO_TYPE_PRICE = "Price";
	public static final String INFO_TYPE_QUERY = "Query";
	public static final String INFO_TYPE_RIGHT = "Right";
	public static final String INFO_TYPE_RINGING = "ringing";
	public static final int LOGIN_TYPE_CANCELLOGIN = 7;
	public static final int LOGIN_TYPE_THIRTYMUSIC = 6;
	public static final String TAG_ADITEM = "aditem";
	public static final String TAG_ADITEMS = "aditems";
	public static final String TAG_ALBUM = "album";
	public static final String TAG_ATTACH_ID = "attachid";
	public static final String TAG_ATTACH_TYPE_ID = "attachtypeid";
	public static final String TAG_BATCH = "batch";
	public static final String TAG_BITRATE = "biterate";
	public static final String TAG_BITRATE1 = "bitrate";
	public static final String TAG_BOXID = "boxid";
	public static final String TAG_BUFFER_SIZE = "buffersize";
	public static final String TAG_CACHE_XML = "rdp2";
	public static final String TAG_CANCEL_DES = "canceldes";
	public static final String TAG_CANCEL_INFO = "cancelinfo";
	public static final String TAG_CATALOG = "catalog";
	public static final String TAG_CATALOG_NAME = "catalogname";
	public static final String TAG_CATALOG_TYPE = "catalogtype";
	public static final String TAG_CHANNEL = "channel";
	public static final String TAG_CODE = "code";
	public static final String TAG_COLUMN = "column";
	public static final String TAG_COLUMNS = "columns";
	public static final String TAG_CONTACTINFO = "contactinfo";
	public static final String TAG_CONTENT_ID = "contentid";
	public static final String TAG_CONTROL = "control";
	public static final String TAG_COOKIE_NAME = "cookie";
	public static final String TAG_COPYRIGHT = "copyright";
	public static final String TAG_CRBTCANCEL = "crbtcancel";
	public static final String TAG_CRBTINFO = "crbtinfo";
	public static final String TAG_CRBTUSER = "crbtuser";
	public static final String TAG_D1_PROMPT = "d1prompt";
	public static final String TAG_D1_SIZE = "d1size";
	public static final String TAG_D1_URL = "d1url";
	public static final String TAG_D2_PROMPT = "d2prompt";
	public static final String TAG_D2_SIZE = "d2size";
	public static final String TAG_D2_URL = "d2url";
	public static final String TAG_DCOST = "dcost";
	public static final String TAG_DES = "des";
	public static final String TAG_DESCRIPTION = "description";
	public static final String TAG_DETAIL = "detail";
	public static final String TAG_DEVICE_CONTROL = "devicecontrol";
	public static final String TAG_DISCOUNT = "discount";
	public static final String TAG_DURL = "durl";
	public static final String TAG_DURL1 = "durl1";
	public static final String TAG_DURL2 = "durl2";
	public static final String TAG_DURL3 = "durl3";
	public static final String TAG_FILENAME = "filename";
	public static final String TAG_FILESIZE = "filesize";
	public static final String TAG_FILESIZE1 = "filesize1";
	public static final String TAG_FILESIZE2 = "filesize2";
	public static final String TAG_FILESIZE3 = "filesize3";
	public static final String TAG_FLAG = "flag";
	public static final String TAG_FORE_URL = "foreurl";
	public static final String TAG_GROUPNAME = "groupname";
	public static final String TAG_GROUP_CODE = "groupcode";
	public static final String TAG_GROUP_TOPIC = "grouptopic";
	public static final String TAG_HEADER_MDN = "x-up-calling-line-id";
	public static final String TAG_IMEI = "imei";
	public static final String TAG_IMG = "img";
	public static final String TAG_IMSI = "imsi";
	public static final String TAG_INFO = "info";
	public static final String TAG_ISDULBY = "1";
	public static final String TAG_IS_USER = "isuser";
	public static final String TAG_ITEM = "item";
	public static final String TAG_ITEM_COUNT = "itemcount";
	public static final String TAG_LINK = "link";
	public static final String TAG_MDN = "mdn";
	public static final String TAG_MEMBER = "member";
	public static final String TAG_MEMBER_INFO = "memberinfo";
	public static final String TAG_MEMO = "memo";
	public static final String TAG_MESSAGE_CODE = "messagecode";
	public static final String TAG_MESSAGE_INFO = "messageinfo";
	public static final String TAG_MIGU = "migu";
	public static final String TAG_MOBILENO = "mobileno";
	public static final String TAG_MODE = "mode";
	public static final String TAG_MSG = "msg";
	public static final String TAG_MUSIC = "music";
	public static final String TAG_NAME = "name";
	public static final String TAG_NEED_UPDATE = "needupdate";
	public static final String TAG_NTEPL = "netpl";
	public static final String TAG_ORDER = "order";
	public static final String TAG_ORDERINFO = "orderinfo";
	public static final String TAG_ORDER_INFO = "orderinfo";
	public static final String TAG_PACKAGE_SIZE = "packagesize";
	public static final String TAG_PAGE_COUNT = "pagecount";
	public static final String TAG_PAGE_NO = "pageno";
	public static final String TAG_PDES = "pdes";
	public static final String TAG_PID = "pid";
	public static final String TAG_PL = "pl";
	public static final String TAG_POLICY = "policy";
	public static final String TAG_PORT = "port";
	public static final String TAG_PROMPT = "prompt";
	public static final String TAG_PSIZE = "psize";
	public static final String TAG_PTYPE = "ptype";
	public static final String TAG_PUBLISH_TIME = "publish_time";
	public static final String TAG_PURL = "purl";
	public static final String TAG_QQINFO = "qqinfo";
	public static final String TAG_QQORDER = "qqorder";
	public static final String TAG_RANDOMSESSIONKEY = "randomsessionkey";
	public static final String TAG_RAND_KEY = "randkey";
	public static final String TAG_RESULT = "result";
	public static final String TAG_RETCODE = "retcode";
	public static final String TAG_RETURNCODE = "returncode";
	public static final String TAG_RETURNDES = "returndes";
	public static final String TAG_RIGHTS_INFO = "rightsinfo";
	public static final String TAG_ROREQ = "roreq";
	public static final String TAG_SCROLL_INFO = "scrollinfo";
	public static final String TAG_SEARCH_INFO = "searchinfo";
	public static final String TAG_SEARCH_TYPE = "searchtype";
	public static final String TAG_SEND = "send";
	public static final String TAG_SEND_INFO = "sendinfo";
	public static final String TAG_SERVICE_ID = "serviceid";
	public static final String TAG_SINGER = "singer";
	public static final String TAG_SIZE = "size";
	public static final String TAG_SKIN = "skin";
	public static final String TAG_SONG_NAME = "songname";
	public static final String TAG_SUBCHANNEL = "subchannel";
	public static final String TAG_TIME_STEP = "timestep";
	public static final String TAG_TITLE = "title";
	public static final String TAG_TONE = "tone";
	public static final String TAG_TONEID = "toneid";
	public static final String TAG_TOPIC = "topic";
	public static final String TAG_TYPE = "type";
	public static final String TAG_UA = "ua";
	public static final String TAG_UPDATE_INFO = "updateinfo";
	public static final String TAG_UPDATE_URL = "updateurl";
	public static final String TAG_UPDATE_VERSION = "updateversion";
	public static final String TAG_URL = "url";
	public static final String TAG_VERSION = "version";
	public static final String TAG_XML_MUSIC_INFO = "cmccwm.mobilemusic.mmhttpdefines.musicinfo";
	public static final String TAG_XML_RAW_DATA = "cmccwm.mobilemusic.mmhttpdefines.xmlRawData";
	public static final String TAG_XML_RECOMMEND_RAW_DATA = "cmccwm.mobilemusic.mmhttpdefines.xmlRecommendRawData";

	public static boolean IsLogIn()
	{
		if (GlobalSettingParameter.useraccount == null)
			;
		for (boolean bool = true;; bool = false)
			return bool;
	}

	public static UserAccount getAccount()
	{
		return GlobalSettingParameter.useraccount;
	}

	public static void setAccount(UserAccount paramUserAccount)
	{
		GlobalSettingParameter.useraccount = paramUserAccount;
	}
}