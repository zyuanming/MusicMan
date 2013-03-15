package org.ming.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * 实现定时关闭应用的功能
 * 
 * @author lkh
 * 
 */
public class AlarmReceiver extends BroadcastReceiver
{
	public static final MyLogger logger = MyLogger.getLogger("AlarmReceiver");

	public void onReceive(Context paramContext, Intent paramIntent)
	{
		logger.v("onReceive() ---> Enter");
		SharedPreferences localSharedPreferences = paramContext
				.getSharedPreferences("closetimesharedpre", 0);
		String str = localSharedPreferences.getString("CLOSETIME", null);
		if ((str != null) && (!"".equals(str.trim())))
		{
			localSharedPreferences.edit().putString("CLOSETIME", null).commit();
			Util.exitMobileMusicApp(false);
		}
		logger.v("onReceive() ---> Exit");
	}
}
