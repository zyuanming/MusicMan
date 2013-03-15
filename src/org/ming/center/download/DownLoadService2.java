package org.ming.center.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.ming.R;
import org.ming.ui.activity.MobileMusicMainActivity;
import org.ming.ui.activity.online.MusicOnlineSetRingToneActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

public class DownLoadService2 extends Service
{

	private static final int DOWNLOAD_SUCCESS = 0;
	private static final int DOWNLOAD_COMPLETE = 1;
	private static final int DOWNLOAD_FALL = 2;
	private static final int NOTIFICATION_ID = 0x12;
	private static File updateFile;
	private static String downloadDir = "MusicManMp3/";
	private static String sdPath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator;
	private Notification updateNotification;
	private NotificationManager updateNotificationMgr;
	private Intent updateIntent;
	private PendingIntent updatePendingIntent;

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		if (intent != null)
		{
			String downloadUrl = intent.getStringExtra("downloadUrl");
			String songName = intent.getStringExtra("songName");
			String singerName = intent.getStringExtra("singerName");
			File file = new File(sdPath + downloadDir);
			if (!file.exists())
			{
				file.mkdirs();
				Log.d("zhanyuanming", "mkmkmkkkkk");
			}
			if (sdPath != null && downloadUrl != null && songName != null
					&& singerName != null)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(songName).append(".mp3");
				updateFile = new File(sdPath + downloadDir + sb.toString());
				// 初始化通知管理器
				this.updateNotificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				this.updateNotification = new Notification();
				updateNotification.icon = R.drawable.icon;
				updateIntent = new Intent(this,
						MusicOnlineSetRingToneActivity.class);
				updatePendingIntent = PendingIntent.getActivity(this, 0,
						updateIntent, 0);
				// 通知自定义视图
				updateNotification.contentView = new RemoteViews(
						getPackageName(), R.layout.notify_content);
				updateNotification.contentView.setProgressBar(
						R.id.progressBar1, 100, 0, false);
				updateNotification.contentIntent = updatePendingIntent;// 这个pengdingIntent很重要，必须要设置
				updateNotification.contentView.setTextViewText(R.id.textView2,
						songName + " - " + singerName);
				// 发出通知
				updateNotificationMgr.notify(NOTIFICATION_ID,
						updateNotification);

				// 开启线程进行下载
				new Thread(new updateThread(downloadUrl, songName)).start();
			}
			super.onStart(intent, startId);
		}
	}

	// 3.开启一个线程来下载防止主线程堵塞。这里在servce写了一个内部类实现了Runnable
	class updateThread implements Runnable
	{
		Message msg = handler.obtainMessage();
		private String downloadUrl;
		private String songName;

		public updateThread(String downloadUrl, String songName)
		{
			this.downloadUrl = downloadUrl;
			this.songName = songName;
		}

		@Override
		public void run()
		{
			try
			{
				if (!updateFile.exists())
				{
					updateFile.createNewFile();
				}
				long downSize = downloadFile(downloadUrl, updateFile);
				if (downSize > 0)
				{
					// 下载成功！
					msg.what = DOWNLOAD_SUCCESS;
					handler.sendMessage(msg);
				}
			} catch (Exception ex)
			{
				ex.printStackTrace();// 下载失败
				msg.what = DOWNLOAD_FALL;
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param downloadUrl
	 *            下载路径
	 * @param saveFile
	 *            保存文件名
	 */
	public long downloadFile(String downloadUrl, File saveFile)
			throws Exception
	{
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;
		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try
		{
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0)
			{
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();// 总大小
			if (httpConnection.getResponseCode() == 404)
			{
				throw new Exception("conection net 404！");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile);
			byte[] buf = new byte[1024];
			int readSize = -1;

			while ((readSize = is.read(buf)) != -1)
			{
				fos.write(buf, 0, readSize);
				// 通知更新进度
				totalSize += readSize;
				int tmp = (int) (totalSize * 100 / updateTotalSize);
				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if (downloadCount == 0 || tmp - 10 > downloadCount)
				{
					downloadCount += 10;
					Message msg = handler.obtainMessage();
					msg.what = DOWNLOAD_COMPLETE;
					msg.arg1 = downloadCount;
					handler.sendMessage(msg);
				}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		} finally
		{
			if (httpConnection != null)
			{
				httpConnection.disconnect();
			}
			if (is != null)
			{
				is.close();
			}
			if (fos != null)
			{
				fos.close();
			}
		}
		return totalSize;
	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case DOWNLOAD_SUCCESS:

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setClass(DownLoadService2.this,
						MobileMusicMainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				Context mContext = getApplicationContext();
				updatePendingIntent = PendingIntent.getActivity(mContext, 0,
						intent, 0);

				updateNotification.defaults = Notification.DEFAULT_SOUND;// 设置铃声
				updateNotification.contentIntent = updatePendingIntent;
				// 更新通知视图值
				updateNotification.contentView.setTextViewText(R.id.textView1,
						"下载成功");
				updateNotification.contentView.setProgressBar(
						R.id.progressBar1, 100, 100, false);
				updateNotificationMgr.notify(NOTIFICATION_ID,
						updateNotification);
				stopService(updateIntent);// 停止service
				break;
			case DOWNLOAD_COMPLETE:// 下载中状态
				System.out.println(msg.arg1);
				updateNotification.contentView.setProgressBar(
						R.id.progressBar1, 100, msg.arg1, false);
				updateNotification.contentView.setTextViewText(R.id.textView1,
						"下载中" + msg.arg1 + "%");
				updateNotificationMgr.notify(NOTIFICATION_ID,
						updateNotification);
				break;
			case DOWNLOAD_FALL:// 失败状态
				// updateNotification.setLatestEventInfo(UpgradeService.this,
				// "下载失败", "", updatePendingIntent);]
				updateNotification.contentView.setTextViewText(R.id.textView1,
						"下载失败");
				updateNotificationMgr.notify(NOTIFICATION_ID,
						updateNotification);
				stopService(updateIntent);// 停止service
				break;
			default:
				stopService(updateIntent);
			}
		}
	};
}
