package org.ming.util;

import org.ming.ui.activity.SplashActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Util
{
	private static final MyLogger logger = MyLogger.getLogger("Util");

	private static SharedPreferences preferences;

	public static boolean isNeedForUserLead(Context context)
	{
		if (context.getClass() == SplashActivity.class)
		{
			logger.v("this is splashActivity");
			preferences = context.getSharedPreferences("isFirstInApplication",
					Context.MODE_WORLD_WRITEABLE);
			return preferences.getBoolean("isFirstInApplication", true);
		}
		return false;
	}

	public static void setNoNeedUserLead(Context context)
	{
		if (context.getClass() == SplashActivity.class)
		{
			Editor editor = preferences.edit();
			editor.putBoolean("isFirstInApplication", false);
			editor.commit();
		}
	}

	public static void exitMobileMusicApp(boolean flag)
	{

	}

	public static boolean getDefaultSettings()
	{
		return true;
	}
}
