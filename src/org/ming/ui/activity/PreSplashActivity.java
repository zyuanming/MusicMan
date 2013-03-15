package org.ming.ui.activity;

import java.util.List;

import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.system.SystemEventListener;
import org.ming.util.MyLogger;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;

public class PreSplashActivity extends Activity implements SystemEventListener
{
	static boolean islog = true;
	private static final MyLogger logger = MyLogger
			.getLogger("PreSplashActivity");
	private Controller mController = null;

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		this.mController = ((MobileMusicApplication) getApplication())
				.getController();
		this.mController.addSystemEventListener(22, this);
		if (islog)
		{
			startActivity(new Intent(this, SplashActivity.class));
		}
		finish();
		logger.v("onCreate() ---> Exit");
	}

	protected void onDestroy()
	{
		this.mController.removeSystemEventListener(22, this);
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
		super.onResume();
		MobclickAgent.onResume(this);
		List<ActivityManager.RunningTaskInfo> localList = ((ActivityManager) getSystemService("activity"))
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
	}

	@Override
	public void handleSystemEvent(Message paramMessage)
	{
		logger.v("handleSystemEvent() ---> Enter");
		switch (paramMessage.what)
		{
		default:
		case 22:
		}
		logger.v("handleSystemEvent() ---> Exit");
		finish();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
}