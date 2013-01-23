package org.ming.ui.activity;

import java.util.List;

import org.ming.util.MyLogger;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class PreSplashActivity extends Activity
{
	static boolean islog = true;
	private static final MyLogger logger = MyLogger.getLogger("PreSplashActivity");

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		if (islog)
		{
			super.onCreate(paramBundle);
			startActivity(new Intent(this, SplashActivity.class));
			finish();
		}
		logger.v("onCreate() ---> Exit");
	}

	protected void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * 返回True，以防止这个事件继续向下传播
	 */
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
		return true;
	}

	protected void onResume()
	{
		List localList = ((ActivityManager) getSystemService("activity"))
				.getRunningTasks(1);
		if ((localList.size() > 0) && (!islog))
		{
			ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo) localList
					.get(0);
			if ((localRunningTaskInfo != null)
					&& (localRunningTaskInfo.numActivities == 1)
					&& (localRunningTaskInfo.topActivity.getPackageName()
							.equals("org.ming")))
				startActivity(new Intent(this, SplashActivity.class));
		}
		islog = false;
		super.onResume();
	}
}