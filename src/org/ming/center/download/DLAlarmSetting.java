package org.ming.center.download;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DLAlarmSetting
{
	public static final String PREFS_END_TIME = "endTime";
	public static final String PREFS_NAME = "downloadSetting";
	public static final String PREFS_START_TIME = "startTime";
	public static final String PREFS_TIMING_FlAG = "isTimgingDownload";
	private static DLAlarmSetting m_instance = null;
	private Context m_ctx;
	private String m_endTime;
	private boolean m_isTimingDownload;
	private String m_startTime;

	private DLAlarmSetting(Context paramContext)
	{
		this.m_ctx = paramContext;
		this.m_startTime = "00:00";
		this.m_endTime = "00:00";
		initFromPref();
	}

	public static DLAlarmSetting getInstance(Context context)
	{
		DLAlarmSetting dlalarmsetting;
		if (context == null)
		{
			dlalarmsetting = null;
		} else
		{
			if (m_instance == null)
				m_instance = new DLAlarmSetting(context);
			dlalarmsetting = m_instance;
		}
		return dlalarmsetting;
	}

	private void initFromPref()
	{
		SharedPreferences localSharedPreferences = this.m_ctx
				.getSharedPreferences("downloadSetting", 0);
		this.m_isTimingDownload = localSharedPreferences.getBoolean(
				"isTimgingDownload", false);
		this.m_startTime = localSharedPreferences.getString("startTime",
				"00:00");
		this.m_endTime = localSharedPreferences.getString("endTime", "00:00");
	}

	public String getEndTime()
	{
		return this.m_endTime;
	}

	public String getStartTime()
	{
		return this.m_startTime;
	}

	public boolean isTimingDownload()
	{
		return this.m_isTimingDownload;
	}

	public void setTime(String paramString1, String paramString2)
	{
		this.m_startTime = paramString1;
		this.m_endTime = paramString2;
		sync2Pref();
	}

	public void setTimingDownload(boolean paramBoolean)
	{
		this.m_isTimingDownload = paramBoolean;
		sync2Pref();
	}

	public void sync2Pref()
	{
		SharedPreferences.Editor localEditor = this.m_ctx.getSharedPreferences(
				"downloadSetting", 0).edit();
		localEditor.putBoolean("isTimgingDownload", this.m_isTimingDownload);
		localEditor.putString("startTime", this.m_startTime);
		localEditor.putString("endTime", this.m_endTime);
		localEditor.commit();
	}
}