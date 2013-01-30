package org.ming.util;

import java.security.MessageDigest;
import java.util.ArrayList;

import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.ui.activity.SplashActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
		if (song.mMusicType == MusicType.ONLINEMUSIC.ordinal()
				|| song.mMusicType == MusicType.RADIO.ordinal())
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
		if (song != null && song.mMusicType == MusicType.RADIO.ordinal())
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static boolean checkWapStatus()
	{
		logger.v("checkWapStatus ----> enter");
		ArrayList arraylist;
		ConnectivityManager connectivitymanager;
		boolean flag = false;
		MobileMusicApplication mobilemusicapplication = MobileMusicApplication
				.getInstance();
		arraylist = new ArrayList();
		connectivitymanager = (ConnectivityManager) mobilemusicapplication
				.getSystemService("connectivity");
		if (connectivitymanager == null)
		{
			logger.v("connectivitymanager == null");
			logger.v("return ----> " + flag);
			return flag;
		} else
		{
			NetworkInfo anetworkinfo[];
			anetworkinfo = connectivitymanager.getAllNetworkInfo();
			if (anetworkinfo == null)
			{
				logger.v("anetworkinfo == null");
				logger.v("return ----> " + flag);
				return flag;
			} else
			{
				for (int i = 0; i < anetworkinfo.length; i++)
				{
					if (anetworkinfo[i].getState() == android.net.NetworkInfo.State.CONNECTED)
						arraylist.add(anetworkinfo[i].getTypeName());
				}
				int j;
				j = arraylist.size();
				if (j <= 0)
				{
					logger.v("arraylist.size() <= 0");
					logger.v("return ----> " + flag);
					return flag;
				} else
				{
					boolean flag1;
					flag1 = arraylist.contains("WIFI");
					if (!flag1)
					{
						boolean flag2;
						flag2 = arraylist.contains("mobile");
						if (!flag2)
						{
							logger.v("arraylist do not contains 'mobile'");
							logger.v("return ----> " + flag);
							return flag;
						} else
						{
							for (int k = 0; k < anetworkinfo.length; k++)
							{
								if (anetworkinfo[k].getState() != android.net.NetworkInfo.State.CONNECTED)
								{
									logger.v("not connected to network");
									logger.v("return ----> " + flag);
									return flag;
								} else
								{
									String s = anetworkinfo[k].getExtraInfo();
									if (s == null || !s.equals("cmwap"))
									{
										logger.v("networkinfo not cmwap");
										logger.v("return -----> " + flag);
										return flag;
									}
									flag = true;
								}
							}
						}
					} else
					{
						logger.v("return ----> true");
						return true;
					}
				}
			}
		}
		return flag;
	}

	public static String encodeByMD5(String s)
	{
		String s1 = null;
		if (s != null)
		{
			try
			{
				String s2 = byteArrayToHexString(
						MessageDigest.getInstance("MD5").digest(s.getBytes()))
						.toUpperCase();
				s1 = s2;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return s1;

	}

	public static String byteArrayToHexString(byte abyte0[])
	{
		StringBuffer stringbuffer = new StringBuffer(2 * abyte0.length);
		int i = 0;
		do
		{
			if (i >= abyte0.length)
				return stringbuffer.toString();
			int j = abyte0[i];
			if (j < 0)
				j += 256;
			String s = Integer.toHexString(j);
			if (s.length() % 2 == 1)
				s = (new StringBuilder("0")).append(s).toString();
			stringbuffer.append(s);
			i++;
		} while (true);
	}
}
