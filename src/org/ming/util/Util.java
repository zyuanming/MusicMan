package org.ming.util;

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

public class Util {
	private static final MyLogger logger = MyLogger.getLogger("Util");

	private static SharedPreferences preferences;

	public static boolean isNeedForUserLead(Context context) {
		if (context.getClass() == SplashActivity.class) {
			logger.v("this is splashActivity");
			preferences = context.getSharedPreferences("isFirstInApplication",
					Context.MODE_WORLD_WRITEABLE);
			return preferences.getBoolean("isFirstInApplication", true);
		}
		return false;
	}

	public static void setNoNeedUserLead(Context context) {
		if (context.getClass() == SplashActivity.class) {
			Editor editor = preferences.edit();
			editor.putBoolean("isFirstInApplication", false);
			editor.commit();
		}
	}

	public static boolean isOnlineMusic(Song song) {
		boolean flag;
		if (song.mMusicType == MusicType.ONLINEMUSIC
				|| song.mMusicType == MusicType.RADIO)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static void exitMobileMusicApp(boolean flag) {
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

	public static boolean getDefaultSettings() {
		return true;
	}

	public static boolean isRadioMusic(Song song) {
		boolean flag;
		if (song != null && song.mMusicType == MusicType.RADIO)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static boolean checkWapStatus() {
		ArrayList arraylist;
		ConnectivityManager connectivitymanager;
		boolean flag;
		MobileMusicApplication mobilemusicapplication = MobileMusicApplication
				.getInstance();
		arraylist = new ArrayList();
		connectivitymanager = (ConnectivityManager) mobilemusicapplication
				.getSystemService("connectivity");
		flag = false;
		if (connectivitymanager == null) {
			return flag;
		} else {
			NetworkInfo anetworkinfo[];
			anetworkinfo = connectivitymanager.getAllNetworkInfo();
			flag = false;
			if (anetworkinfo == null) {
				return flag;
			} else {
				for (int i = 0; i < anetworkinfo.length; i++) {
					if (anetworkinfo[i].getState() == android.net.NetworkInfo.State.CONNECTED)
						arraylist.add(anetworkinfo[i].getTypeName());
				}
				int j;
				j = arraylist.size();
				flag = false;
				if (j <= 0) {
					return flag;
				} else {
					boolean flag1;
					flag1 = arraylist.contains("WIFI");
					flag = false;
					if (!flag1) {
						boolean flag2;
						flag2 = arraylist.contains("mobile");
						flag = false;
						if (!flag2) {
							return flag;
						} else {
							for (int k = 0; k < anetworkinfo.length; k++) {
								if (anetworkinfo[k].getState() != android.net.NetworkInfo.State.CONNECTED) {
									return flag;
								} else {
									String s = anetworkinfo[k].getExtraInfo();
									if (s == null || !s.equals("cmwap")) {
										return flag;
									}
									flag = true;
								}
							}
						}
					} else {
						return flag;
					}
				}

			}
		}
		return flag;
	}
}
