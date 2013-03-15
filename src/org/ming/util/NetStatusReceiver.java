package org.ming.util;

import org.ming.center.BindingContainer;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.Song;
import org.ming.dispatcher.Dispatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetStatusReceiver extends BroadcastReceiver
{
	public static final String RETCODE_SUCCESS = "000000";
	private static final MyLogger logger = MyLogger
			.getLogger("NetStatusReceiver");
	private Controller mController = null;
	private Dispatcher mDispatcher = null;

	public void onReceive(Context paramContext, Intent paramIntent)
	{
		logger.v("onReceive() ---> Enter : " + NetUtil.netState);
		MobileMusicApplication localMobileMusicApplication = MobileMusicApplication
				.getInstance();
		this.mController = localMobileMusicApplication.getController();
		this.mDispatcher = localMobileMusicApplication.getEventDispatcher();
		int i = NetUtil.getNetWorkState(localMobileMusicApplication);
		Song localSong = this.mController.getPlayerController()
				.getCurrentPlayingItem();
		logger.v("onReceive() netState  = : " + i);
		// 如果CNWEB | CNNET | WLAN | NetworkState不可用
		if ((NetUtil.netState == 5) || (NetUtil.netState == 7)
				|| (NetUtil.netState == 2) || (NetUtil.netState == 8))
		{
			if (((NetUtil.netState == 8) || (NetUtil.netState == 5))
					&& (i == 3))
			{
				NetUtil.netState = 3;
			} else if (((NetUtil.netState == 8) || (NetUtil.netState == 7))
					&& (i == 6))
			{
				NetUtil.netState = 6;
			} else if (((NetUtil.netState == 8) || (NetUtil.netState == 2))
					&& (i == 1))
			{
				NetUtil.netState = 1;
			}
		} else if ((NetUtil.netState == 1) && (i != 1)) // 如果当前WLAN标志可用，可是收到的状态却不可用
		{
			logger.v("onReceive() : disconnect from wlan");
			if ((i != 2)
					&& (((this.mController.getPlayerController()
							.getCurrentPlayingItem() != null) && (Util
							.isOnlineMusic(localSong))) || (!BindingContainer
							.getInstance().isDownloadTaskListEmpty())))
				this.mDispatcher.sendMessage(localMobileMusicApplication
						.getEventDispatcher().obtainMessage(3009));
			NetUtil.netState = 2;
		} else if ((NetUtil.netState == 6) && (i != 6)) // 如果当前CNNET标志可用，但是收到的状态却不可用
		{
			logger.v("onReceive() : disconnect from net");
			if ((i != 7)
					&& (((this.mController.getPlayerController()
							.getCurrentPlayingItem() != null) && (Util
							.isOnlineMusic(localSong))) || (!BindingContainer
							.getInstance().isDownloadTaskListEmpty())))
				this.mDispatcher.sendMessage(localMobileMusicApplication
						.getEventDispatcher().obtainMessage(3009));
			NetUtil.netState = 7;
		} else if ((NetUtil.netState == 3) && (i != 3)) // 如果当前CNWEB标志可用，但是收到的状态却不可用
		{
			logger.v("onReceive() : disconnect from cmwep");
			if ((i != 5)
					&& (((this.mController.getPlayerController()
							.getCurrentPlayingItem() != null) && (Util
							.isOnlineMusic(localSong))) || (!BindingContainer
							.getInstance().isDownloadTaskListEmpty())))
				this.mDispatcher.sendMessage(localMobileMusicApplication
						.getEventDispatcher().obtainMessage(3009));
			NetUtil.netState = 5;
		}
	}
}
