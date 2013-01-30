package org.ming.center;

public class ConfigSettingParameter
{

	public ConfigSettingParameter()
	{}

	public static String CMCC_WAP_PROXY_HOST = "10.0.0.172";
	public static int CMCC_WAP_PROXY_PORT = 0;
	public static String CONSTANT_CHANNEL_VALUE = "0140009";
	public static String CONSTANT_SUBCHANNEL_VALUE = "0140009";
	public static final String IsBBK_Y3T = "isBBK_y3t";
	public static final String IsMonthCheck = "monthCheck";
	public static String LOCAL_PARAM_BUILD_ID = null;
	public static final String LOCAL_PARAM_CONFIG_FILE_PATH = "mobilemusic_config.xml";
	public static final String LOCAL_PARAM_CONFIG_TAG_BUILD_ID = "BuildID";
	public static final String LOCAL_PARAM_CONFIG_TAG_ENABLE_HOST_LOG = "EnableHostLog";
	public static final String LOCAL_PARAM_CONFIG_TAG_ENABLE_LOG = "EnableLog";
	public static final String LOCAL_PARAM_CONFIG_TAG_UA = "MobileMusic_DefaultUA";
	public static final String LOCAL_PARAM_CONSTANT_CHANNEL_VALUE = "constant_channel_value";
	public static final String LOCAL_PARAM_CONSTANT_SUBCHANNEL_VALUE = "constant_subchannel_value";
	public static String LOCAL_PARAM_COOKIE_VALUE = null;
	public static boolean LOCAL_PARAM_DEVICE_IS_MOTOROLA_MT870_CMCC = false;
	public static boolean LOCAL_PARAM_IS_ENABLE_HOST_LOG = false;
	public static boolean LOCAL_PARAM_IS_ENABLE_LOG = false;
	public static boolean LOCAL_PARAM_IS_SUPPORT_WLAN_VALUE = false;
	public static final String NETWORK_TIMEOUT = "timeout";
	public static String SVN_PARAMETER = "Svn";
	public static final boolean USE_CACH = false;
	public static final String WEIBO_DB_NAME = "weibo_db_name";

	static
	{
		LOCAL_PARAM_IS_SUPPORT_WLAN_VALUE = false;
		LOCAL_PARAM_IS_ENABLE_LOG = true;
		LOCAL_PARAM_IS_ENABLE_HOST_LOG = false;
		LOCAL_PARAM_DEVICE_IS_MOTOROLA_MT870_CMCC = false;
		CMCC_WAP_PROXY_PORT = 80;
	}
}
