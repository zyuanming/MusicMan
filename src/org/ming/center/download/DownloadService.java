package org.ming.center.download;

import org.ming.R;
import org.ming.ui.activity.local.LocalMusicActivity;
import org.ming.util.Util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DownloadService extends Service
{
	private static final int NOTIFICATION_ID = 0x12;
	private Notification notification = null;
	private NotificationManager manager = null;
	private int _progress = 0;
	private boolean isStop = false;
	private Binder serviceBinder = new DownLoadServiceBinder();

	@Override
	public IBinder onBind(Intent intent)
	{
		return serviceBinder;
	}

	public class DownLoadServiceBinder extends Binder
	{
		public DownloadService getService()
		{
			return DownloadService.this;
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		notification = new Notification(R.drawable.icon, "歌曲正在下载",
				System.currentTimeMillis());

		notification.contentView = new RemoteViews(getApplication()
				.getPackageName(), R.layout.notify_content);
		notification.contentView.setProgressBar(R.id.progressBar1, 100, 0,
				false);
		notification.contentView.setTextViewText(R.id.textView1, "进度"
				+ _progress + "%");

		notification.contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, LocalMusicActivity.class), 0);

		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String downloadUrl = intent.getStringExtra("downloadUrl");
		String songName = intent.getStringExtra("songName");

		DownloadThread downloadThread = new DownloadThread(downloadUrl,
				songName);
		// 启动新线程
		Thread thread = new Thread(downloadThread);
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	class DownloadThread implements Runnable
	{
		private String downloadUrl;
		private String songName;

		public DownloadThread(String downloadUrl, String songName)
		{
			this.downloadUrl = downloadUrl;
			this.songName = songName;
		}

		@Override
		public void run()
		{
			// 将文件下载下来，并存储到SDCard当中
			StringBuilder sb = new StringBuilder();
			sb.append(this.songName).append(".mp3");
			int mp3Result = Util.downFile(this.downloadUrl, "MusicManMp3",
					sb.toString());

			Log.d("ming", String.valueOf(mp3Result));
			String resultMessage = null;
			if (mp3Result == -1)
			{
				resultMessage = "下载失败";
			} else if (mp3Result == 0)
			{
				resultMessage = "文件已经存在，不需要重复下载";
			} else if (mp3Result == 1)
			{
				resultMessage = "文件下载成功";
			}

			// // 使用Notification提示客户下载结果
			// NotificationManager nm = (NotificationManager)
			// getSystemService(Context.NOTIFICATION_SERVICE);
			// Notification n = new Notification(R.drawable.icon, resultMessage,
			// System.currentTimeMillis());
			// n.flags = Notification.FLAG_AUTO_CANCEL;
			// nm.notify(1000, n);
		}
	}

	public Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			notification.contentView.setProgressBar(R.id.progressBar1, 100,
					msg.arg1, false);
			notification.contentView.setTextViewText(R.id.textView1, "进度"
					+ msg.arg1 + "%");
			manager.notify(NOTIFICATION_ID, notification);

			if (msg.arg1 == 100)
			{
				_progress = 0;
				manager.cancel(NOTIFICATION_ID);
				isStop = false;
			}
		}
	};
}
