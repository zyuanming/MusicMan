package org.ming.center.download;

public abstract interface DLConstants
{
	public static final int CONTENT_TYPE_DOBLY = -500;
	public static final int CONTENT_TYPE_MV = -200;
	public static final int CONTENT_TYPE_RINGTONE = -100;
	public static final int CONTENT_TYPE_SKIN = -400;
	public static final int CONTENT_TYPE_UPDATE_PACKAGE = -300;
	public static final int DOWNLOAD_CODE_DOWNLOAD_LIST_EXIST = -6;
	public static final int DOWNLOAD_CODE_SUCCESS = 0;
	public static final int DOWNLOAD_ERROR_CODE_CANCELED = -4;
	public static final int DOWNLOAD_ERROR_CODE_FILE_EXIST = -5;
	public static final int DOWNLOAD_ERROR_CODE_NETWORK_ERROR = -3;
	public static final int DOWNLOAD_ERROR_CODE_NO_SPACE = -2;
	public static final int DOWNLOAD_ERROR_CODE_UNKNOWN = -1;
	public static final int DOWNLOAD_STATUS_FINISHED = 4;
	public static final int DOWNLOAD_STATUS_PAUSED = 3;
	public static final int DOWNLOAD_STATUS_RUNNING = 1;
	public static final int DOWNLOAD_STATUS_WAITING = 2;
	public static final String TAG_LAUNCH_TYPE = "cmccwm.mobilemusic.download.launchType";
	public static final int TAG_LAUNCH_TYPE_NORMAL = 3;
	public static final int TAG_LAUNCH_TYPE_START_ALARM_DOWNLOAD = 1;
	public static final int TAG_LAUNCH_TYPE_STOP_ALARM_DOWNLOAD = 2;
}
