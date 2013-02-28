package org.ming.dispatcher;

import java.lang.reflect.Field;

import android.os.Message;

public class DispatcherEventEnum
{
	public static final int DL_EVENT_BEGIN = 2001;
	public static final int DL_EVENT_DL_LIST_CHANGED = 2002;
	public static final int DL_EVENT_DL_TASK_CANCELED = 2004;
	public static final int DL_EVENT_DL_TASK_COMPLETE = 2007;
	public static final int DL_EVENT_DL_TASK_FAILED = 2006;
	public static final int DL_EVENT_DL_TASK_PROGRESS = 2005;
	public static final int DL_EVENT_DL_TASK_REMAINING = 2008;
	public static final int DL_EVENT_DL_TASK_START = 2003;
	public static final int DL_EVENT_END = 3000;
	public static final int DL_EVENT_GET_DRM_RIGHTS_FAIL = 2010;
	public static final int DL_EVENT_GET_DRM_RIGHTS_SUCCESS = 2009;
	public static final int DL_EVENT_REPORT_MV_DOWNLOAD_FAIL = 2014;
	public static final int DL_EVENT_REPORT_MV_DOWNLOAD_SUCCESS = 2013;
	public static final int DL_EVENT_REPORT_VIBRATE_TONE_DOWNLOAD_FAIL = 2012;
	public static final int DL_EVENT_REPORT_VIBRATE_TONE_DOWNLOAD_SUCCESS = 2011;
	public static final int DL_EVENT_WAP_CLOSED = 2021;
	public static final int HTTP_EVENT_BEGIN = 3001;
	public static final int HTTP_EVENT_END = 4000;
	public static final int HTTP_EVENT_NETWORK_CHANGED = 3009;
	public static final int HTTP_EVENT_TASK_CANCELED = 3005;
	public static final int HTTP_EVENT_TASK_COMPLETE = 3003;
	public static final int HTTP_EVENT_TASK_FAIL = 3004;
	public static final int HTTP_EVENT_TASK_START = 3002;
	public static final int HTTP_EVENT_TASK_TIMEOUT = 3006;
	public static final int HTTP_EVENT_WAP_CLOSED = 3007;
	public static final int PLAYER_EVENT_BEGIN = 1001;
	public static final int PLAYER_EVENT_BUFFER_UPDATED = 1007;
	public static final int PLAYER_EVENT_CACHE_FINISHED = 1020;
	public static final int PLAYER_EVENT_CACHE_SONG = 1024;
	public static final int PLAYER_EVENT_CACHE_STRAT_PLAYING = 1021;
	public static final int PLAYER_EVENT_DUBI_NOTCMCCMUSIC = 1019;
	public static final int PLAYER_EVENT_END = 2000;
	public static final int PLAYER_EVENT_ERROR_OCCURED = 1004;
	public static final int PLAYER_EVENT_META_CHANGED = 1008;
	public static final int PLAYER_EVENT_NETWORK_ERROR = 1005;
	public static final int PLAYER_EVENT_NOTFOUND_MUSIC = 1006;
	public static final int PLAYER_EVENT_NO_LOGIN_LISTEN = 1017;
	public static final int PLAYER_EVENT_NO_RIGHTS_LISTEN_ONLINE_LISTEN = 1013;
	public static final int PLAYER_EVENT_PLAYBACK_PAUSE = 1011;
	public static final int PLAYER_EVENT_PLAYBACK_START = 1010;
	public static final int PLAYER_EVENT_PLAYBACK_STOP = 1012;
	public static final int PLAYER_EVENT_PREPARED_ENDED = 1003;
	public static final int PLAYER_EVENT_PREPARE_START = 1009;
	public static final int PLAYER_EVENT_REPEAT_MODE = 1022;
	public static final int PLAYER_EVENT_RETRY_DOWNLOAD = 1016;
	public static final int PLAYER_EVENT_RETRY_PLAY = 1015;
	public static final int PLAYER_EVENT_SONGLIST_CHANGE = 1023;
	public static final int PLAYER_EVENT_SPACENOTFILL_ERROR = 1018;
	public static final int PLAYER_EVENT_TRACK_ENDED = 1002;
	public static final int PLAYER_EVENT_WAP_CLOSED = 1014;
	public static final int SYSTEM_EVENT_BEGIN = 0;
	public static final int SYSTEM_EVENT_DATA_CONNECTION_CLOSED = 12;
	public static final int SYSTEM_EVENT_DATA_CONNECTION_OPENED = 11;
	public static final int SYSTEM_EVENT_DATA_CONNECTION_STATUS_CHANGED = 13;
	public static final int SYSTEM_EVENT_END = 1000;
	public static final int SYSTEM_EVENT_MEDIA_BUTTON_NEXT = 8;
	public static final int SYSTEM_EVENT_MEDIA_BUTTON_PLAY_PAUSE = 6;
	public static final int SYSTEM_EVENT_MEDIA_BUTTON_PREV = 9;
	public static final int SYSTEM_EVENT_MEDIA_BUTTON_STOP = 7;
	public static final int SYSTEM_EVENT_MEDIA_EJECT = 4;
	public static final int SYSTEM_EVENT_MEDIA_MOUNTED = 5;
	public static final int SYSTEM_EVENT_MEDIA_SCANNER_FINISHED = 15;
	public static final int SYSTEM_EVENT_MEDIA_SCANNER_STARTED = 14;
	public static final int SYSTEM_EVENT_NEED_FINISH_QUICKLOGIN = 25;
	public static final int SYSTEM_EVENT_PHONE_POWER_DOWN = 10;
	public static final int SYSTEM_EVENT_PHONE_STATUS_IDLE = 3;
	public static final int SYSTEM_EVENT_PHONE_STATUS_OFFHOOK = 2;
	public static final int SYSTEM_EVENT_PHONE_STATUS_RING = 1;
	public static final int SYTEM_EVENT_FINISH_ALL_ACTIVITIES = 22;
	public static final int SYTEM_EVENT_HEADSET_PLUG_IN = 20;
	public static final int SYTEM_EVENT_HEADSET_PLUG_OUT = 21;
	public static final int SYTEM_EVENT_PLAY_PAUSE = 23;
	public static final int SYTEM_EVENT_SET_RING = 24;
	public static final int SYTEM_EVENT_SMS_DELIVERED_FAILED = 19;
	public static final int SYTEM_EVENT_SMS_DELIVERED_SUCCESS = 18;
	public static final int SYTEM_EVENT_SMS_SENT_FAILED = 17;
	public static final int SYTEM_EVENT_SMS_SENT_SUCCESS = 16;
	public static final int UI_EVENT_BEGIN = 4001;
	public static final int UI_EVENT_DOWNSONGINF_START = 4010;
	public static final int UI_EVENT_DOWNSONGINF_STOP = 4011;
	public static final int UI_EVENT_END = 5000;
	public static final int UI_EVENT_FIND_HOME_PAGE = 4003;
	public static final int UI_EVENT_FIND_LAST_PAGE = 4004;
	public static final int UI_EVENT_FIND_NEXT_PAGE = 4005;
	public static final int UI_EVENT_FIND_PRE_PAGE = 4006;
	public static final int UI_EVENT_HASDUBIRIGHT = 4015;
	public static final int UI_EVENT_LOCAL_ADDMUSIC_FINISH = 4012;
	public static final int UI_EVENT_LOGIN_CANCAL = 4021;
	public static final int UI_EVENT_LOGIN_FAILED = 4018;
	public static final int UI_EVENT_LOGIN_HTTP = 4019;
	public static final int UI_EVENT_LOGIN_HTTPS = 4020;
	public static final int UI_EVENT_LOGIN_SUCCESSED = 4014;
	public static final int UI_EVENT_LOGOUT = 4017;
	public static final int UI_EVENT_LOSEDUBIRIGHT = 4016;
	public static final int UI_EVENT_MEMBER_STATUS_CHANGE = 4007;
	public static final int UI_EVENT_NEED_UPDATE = 4022;
	public static final int UI_EVENT_PLAY_ERROR = 4009;
	public static final int UI_EVENT_PLAY_NEWSONG = 4008;
	public static final int UI_EVENT_SWITCH_TO_LOCAL_VIEW = 4002;
	public static final int UI_EVENT_UPDATECACHE = 4013;
	public static final int UPDATE_EVENT_CANCELED = 2016;
	public static final int UPDATE_EVENT_COMPLETE = 2019;
	public static final int UPDATE_EVENT_FAILED = 2018;
	public static final int UPDATE_EVENT_PROGRESS = 2017;
	public static final int UPDATE_EVENT_START = 2015;
	public static final int UPDATE_EVENT_WAP_CLOSED = 2020;
	public static final int WLAN_EVENT_WLAN_CLOSE = 3008;

	public static final int UI_INITIAL_SUCCESS = 4400;
	public static final int UI_INITIAL_FAIL = 4401;
	public static final int Http_UpdateData_Begin = 3300;
	public static final int Http_UpdateData_Fail = 3301;
	public static final int Http_UpdateData_Sucess = 3302;

	public static String getString(int paramInt)
	{
		Field[] arrayOfField = DispatcherEventEnum.class.getDeclaredFields();
		int i = arrayOfField.length;
		String result = "false";
		if (i > 0)
		{
			for (int j = 0; j < i; j++)
			{
				Field field = arrayOfField[j];
				try
				{
					if (field.getType() == Integer.TYPE
							&& field.getInt(null) == paramInt)
					{
						result = field.getName();
					}
				} catch (IllegalAccessException localIllegalAccessException)
				{
					localIllegalAccessException.printStackTrace();
				}
			}
		}
		return result;
	}

	public static boolean isDLEvent(Message paramMessage)
	{
		if ((paramMessage.what > 2001) && (paramMessage.what < 3000))
		{
			return true;
		}
		return false;
	}

	public static boolean isHttpEvent(Message paramMessage)
	{
		if ((paramMessage.what > 3001) && (paramMessage.what < 4000))
		{
			return true;
		}
		return false;
	}

	public static boolean isPlayerEvent(Message paramMessage)
	{
		if ((paramMessage.what > 1001) && (paramMessage.what < 2000))
		{
			return true;
		}
		return false;
	}

	public static boolean isSystemEvent(Message paramMessage)
	{
		if ((paramMessage.what > 0) && (paramMessage.what < 1000))
		{
			return true;
		}
		return false;
	}

	public static boolean isUIEvent(Message paramMessage)
	{
		if ((paramMessage.what > 4001) && (paramMessage.what < 5000))
		{
			return true;
		}
		return false;
	}
}