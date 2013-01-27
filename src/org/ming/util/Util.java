package org.ming.util;

import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBControllerImpl;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.download.DLControllerImpl;
import org.ming.center.player.PlayerControllerImpl;
import org.ming.dispatcher.Dispatcher;
import org.ming.ui.activity.SplashActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Process;

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

	public static boolean isOnlineMusic(Song song)
	{
		boolean flag;
		if (song.mMusicType == MusicType.ONLINEMUSIC
				|| song.mMusicType == MusicType.RADIO)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static void exitMobileMusicApp(boolean flag)
	{
		// Dispatcher dispatcher = MobileMusicApplication.getInstance()
		// .getEventDispatcher();
		// dispatcher.sendMessage(dispatcher.obtainMessage(22));
		// final MobileMusicApplication app =
		// MobileMusicApplication.getInstance();
		// if (PlayerControllerImpl.getInstance(app).isPlaying())
		// PlayerControllerImpl.getInstance(app).stop();
		// PlayerControllerImpl.getInstance(app).cancelPlaybackStatusBar();
		// DLControllerImpl.getInstance(app).cancelDownloadRemainNotification();
		// DLControllerImpl.getInstance(app).cancelDownloadNotification();
		// shutDownHttpClient();
		// MobileMusicService.stopService(app);
		// if (useForClear)
		// MobileMusicApplication.getInstance().setIsInitService(false);
		// (new Handler()).postDelayed(new Runnable()
		// {
		//
		// public void run()
		// {
		// if (!useForClear)
		// {
		// Process.killProcess(Process.myPid());
		// DBControllerImpl.getInstance(app).closeDB();
		// }
		// }
		//
		// private final MobileMusicApplication val$app;
		// private final boolean val$useForClear;
		//
		// {
		// useForClear = flag;
		// app = mobilemusicapplication;
		// super();
		// }
		// }, 600L);
	}

	public static boolean getDefaultSettings()
	{
		return true;
	}

	public static boolean isRadioMusic(Song song)
	{
		boolean flag;
		if (song != null && song.mMusicType == MusicType.RADIO)
			flag = true;
		else
			flag = false;
		return flag;
	}
}
