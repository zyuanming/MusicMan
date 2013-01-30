package org.ming.center;

import org.ming.center.player.PlayerController;
import org.ming.util.MyLogger;
import org.ming.util.NetStatusReceiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class MobileMusicService extends Service
{
	public static final String CMDPAUSE = "pause";
	public static final String CMDSTOP = "stop";
	public static final String KEY_CMD = "command";
	public static final String SERVICECMD = "com.android.music.musicservicecommand";
	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicService");
	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context paramAnonymousContext,
				Intent paramAnonymousIntent)
		{
			String str = paramAnonymousIntent.getStringExtra("command");
			PlayerController localPlayerController;
			if (("stop".equals(str)) || ("pause".equals(str)))
			{
				MobileMusicService.logger.v("BroadcastReceiver, cmd: " + str);
				localPlayerController = ((MobileMusicApplication) MobileMusicService.this
						.getApplication()).getController()
						.getPlayerController();
				if (!"pause".equals(str))
					localPlayerController.pause();
				if (!"stop".equals(str))
					localPlayerController.stop();
			}
		}
	};
	private NetStatusReceiver netStatusReceiver;

	public static void startService(Context paramContext)
	{
		logger.v("startService() ---> Enter");
		paramContext.startService(new Intent(paramContext,
				MobileMusicService.class));
		logger.v("startService() ---> Exit");
	}

	public static void stopService(Context paramContext)
	{
		logger.v("startService() ---> Enter");
		paramContext.stopService(new Intent(paramContext,
				MobileMusicService.class));
		logger.v("startService() ---> Exit");
	}

	public IBinder onBind(Intent paramIntent)
	{
		return null;
	}

	public void onCreate()
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate();

		IntentFilter localIntentFilter1 = new IntentFilter();
		localIntentFilter1.addAction(SERVICECMD);
		registerReceiver(this.mIntentReceiver, localIntentFilter1);

		this.netStatusReceiver = new NetStatusReceiver();

		IntentFilter localIntentFilter2 = new IntentFilter();
		localIntentFilter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(this.netStatusReceiver, localIntentFilter2);
		logger.v("onCreate() ---> Exit");
	}

	public void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		unregisterReceiver(this.mIntentReceiver);
		unregisterReceiver(this.netStatusReceiver);
		super.onDestroy();
		logger.v("onDestroy() ---> Exit");
	}
}