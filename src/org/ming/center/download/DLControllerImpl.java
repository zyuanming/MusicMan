package org.ming.center.download;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ming.center.BindingContainer;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.Playlist;
import org.ming.center.database.Song;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpTask;
import org.ming.center.system.SystemController;
import org.ming.center.system.SystemEventListener;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;

import android.app.NotificationManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Message;

public class DLControllerImpl implements DLController, DLEventListener,
		MMHttpEventListener, SystemEventListener, DLTaskListener,
		MediaScannerConnection.MediaScannerConnectionClient
{
	private static final String REQUEST_BODY_FOR_MP3 = "Need not body for Mp3";
	public static final String SONG_CACHE_SUFFIX = ".part";
	private static final MyLogger logger = MyLogger
			.getLogger("DLControllerImpl");
	private static DLControllerImpl sInstance = null;
	private boolean cancleAllTask;
	private MobileMusicApplication mApp;
	private MMHttpTask mCurrentTask;
	private DBController mDBController;
	private Dispatcher mDispatcher;
	private DownloadItem mDownloadItem;
	private List mDownloadList;
	private HttpController mHttpController;
	String mItemFilePath;
	private String mRingtonePath;
	MediaScannerConnection mScanner;
	private SystemController mSystemController;

	private DLControllerImpl(MobileMusicApplication mobilemusicapplication)
	{
		mApp = null;
		mDispatcher = null;
		mSystemController = null;
		mDBController = null;
		mHttpController = null;
		mDownloadList = null;
		mDownloadItem = null;
		mRingtonePath = null;
		mCurrentTask = null;
		cancleAllTask = false;
		logger.v("DLControllerImpl() ---> Enter");
		mApp = mobilemusicapplication;
		mDispatcher = mobilemusicapplication.getEventDispatcher();
		mDBController = mobilemusicapplication.getController()
				.getDBController();
		mHttpController = mobilemusicapplication.getController()
				.getHttpController();
		mSystemController = mobilemusicapplication.getController()
				.getSystemController();
		mDownloadList = new ArrayList();
		mApp.getController().addHttpEventListener(3009, this);
		mApp.getController().addSystemEventListener(4, this);
		mApp.getController().addSystemEventListener(10, this);
		mApp.getController().addSystemEventListener(22, this);
		mApp.getController().addDLEventListener(2002, this);
		mApp.getController().addDLEventListener(2004, this);
		mApp.getController().addDLEventListener(2007, this);
		mApp.getController().addDLEventListener(2006, this);
		mApp.getController().addDLEventListener(2005, this);
		mApp.getController().addDLEventListener(2003, this);
		mApp.getController().addHttpEventListener(3003, this);
		mApp.getController().addHttpEventListener(3005, this);
		mApp.getController().addHttpEventListener(3004, this);
		mApp.getController().addDLEventListener(2015, this);
		mApp.getController().addDLEventListener(2017, this);
		mApp.getController().addDLEventListener(2019, this);
		mApp.getController().addDLEventListener(2018, this);
		mApp.getController().addDLEventListener(2020, this);
		mApp.getController().addDLEventListener(2016, this);
		logger.v("DLControllerImpl() ---> Exit");
	}

	public static DLControllerImpl getInstance(
			MobileMusicApplication paramMobileMusicApplication)
	{
		if (sInstance == null)
			sInstance = new DLControllerImpl(paramMobileMusicApplication);
		return sInstance;
	}

	@Override
	public void cancelAllTask()
	{
		ArrayList arraylist;
		logger.v("cancelAllTask() ---> Enter");
		cancleAllTask = true;
		arraylist = getAllNoneCompleteItems();
		if (!arraylist.isEmpty())
		{
			Iterator iterator = arraylist.iterator();
			do
			{
				if (!iterator.hasNext())
				{
					mDispatcher.sendMessage(mDispatcher.obtainMessage(2002));
					cancleAllTask = false;
					logger.v("cancelAllTask() ---> Exit");
					return;
				}
				DownloadItem downloaditem = (DownloadItem) iterator.next();
				if (downloaditem.getStatus() == 1)
				{
					DownloadTask downloadtask = BindingContainer.getInstance()
							.getDownloadTask(downloaditem.getUrl());
					if (downloadtask == null)
						downloaditem.setStatus(3);
					else
						cancelSingleTask(downloadtask);
				} else if (downloaditem.getStatus() == 2)
					downloaditem.setStatus(3);
				mDBController.updateDBDownloadItem(downloaditem);
			} while (true);
		}
	}

	@Override
	public void cancelDownloadNotification()
	{
		((NotificationManager) mApp.getSystemService("notification")).cancel(2);
	}

	@Override
	public void cancelDownloadRemainNotification()
	{
		((NotificationManager) mApp.getSystemService("notification")).cancel(3);
	}

	@Override
	public void initDownloadListFromDB()
	{
		Cursor cursor = mDBController.queryDBDownloadList(null);
		logger.v("initDownloadListFromDB() ---> Enter");
		mDownloadList.clear();
		if (cursor.getCount() != 0)
		{
			logger.d((new StringBuilder("Item count: ")).append(
					String.valueOf(cursor.getCount())).toString());
			cursor.moveToFirst();
			do
			{
				if (cursor.isAfterLast())
				{
					cursor.close();
					logger.v("initDownloadListFromDB() ---> Exit");
					return;
				}
				if (cursor.getLong(13) != -300L && cursor.getLong(13) != -400L)
				{
					DownloadItem downloaditem = new DownloadItem(
							cursor.getLong(0), cursor.getInt(1),
							cursor.getString(2), cursor.getLong(3),
							cursor.getLong(4), cursor.getString(5),
							cursor.getString(6), cursor.getString(7),
							cursor.getLong(8), cursor.getLong(10),
							cursor.getLong(9), cursor.getString(11),
							cursor.getInt(12), cursor.getInt(13),
							cursor.getString(14), cursor.getString(15),
							cursor.getString(16), cursor.getInt(17));
					logger.d("/n");
					logger.d((new StringBuilder("Item id: ")).append(
							String.valueOf(downloaditem.getItemId()))
							.toString());
					logger.d((new StringBuilder("Item showName: ")).append(
							downloaditem.getShowName()).toString());
					logger.d((new StringBuilder("Item url: ")).append(
							downloaditem.getUrl()).toString());
					logger.d("/n");
					mDownloadList.add(downloaditem);
					if (downloaditem.getStatus() == 1
							|| downloaditem.getStatus() == 2)
						downloaditem.setStatus(3);
				}
				cursor.moveToNext();
			} while (true);
		}
		cursor.close();
	}

	@Override
	public void removeDownloadItemFromLocal(String paramString)
	{
		synchronized (this.mDownloadList)
		{
			logger.v("removeDownloadItemFromLocal() ---> Enter");

			if (paramString != null)
			{
				ArrayList localArrayList = getDownloadItemByStatus(4);
				for (int i = 0; i < localArrayList.size(); i++)
				{
					DownloadItem localDownloadItem = (DownloadItem) localArrayList
							.get(i);
					if (localDownloadItem.getFilePath().equals(paramString))
						this.mDownloadList.remove(localDownloadItem);
				}
				logger.d("remove song path: " + paramString);
			} else
			{
				logger.v("removeDownloadItemFromLocal() ---> Exit");
			}
		}
	}

	@Override
	public void startAllTask()
	{
		ArrayList arraylist;
		logger.v("startAllTask() ---> Enter");
		arraylist = getAllNoneCompleteItems();
		if (!arraylist.isEmpty())
		{
			Iterator iterator = arraylist.iterator();
			while (true)
			{
				if (!iterator.hasNext())
				{
					this.mDispatcher.sendMessage(this.mDispatcher
							.obtainMessage(2002));
					if (BindingContainer.getInstance()
							.isDownloadTaskListEmpty())
					{
						DownloadTask localDownloadTask = new DownloadTask(
								(DownloadItem) arraylist.get(0));
						if (!submitMediaDlTask(localDownloadTask))
							this.mDispatcher.sendMessage(this.mDispatcher
									.obtainMessage(2006, localDownloadTask));
					}
					logger.v("startAllTask() ---> Exit");
					break;
				}
				DownloadItem localDownloadItem = (DownloadItem) iterator.next();
				if (localDownloadItem.getStatus() != 1)
					localDownloadItem.setStatus(2);
			}
		}
	}

	@Override
	public void onMediaScannerConnected()
	{
		logger.v("onMediaScannerConnected() ---> Enter");
		try
		{
			this.mScanner.scanFile(this.mItemFilePath, "audio/*");
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

	@Override
	public void onScanCompleted(String path, Uri uri)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSystemEvent(Message paramMessage)
	{
		logger.v("handleSystemEvent() ---> Enter");
		switch (paramMessage.what)
		{
		default:
			logger.v("handleSystemEvent() ---> Exit");
			return;
		case 4:
		case 10:
		case 22:
			cancelAllTask();
			break;
		}
	}

	@Override
	public void handleMMHttpEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDLEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public long addDownloadItem(DownloadItem paramDownloadItem)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void cancelSingleTask(DownloadTask paramDownloadTask)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadFirstBuyDrmRights(DownloadItem paramDownloadItem)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<DownloadItem> getAllNoneCompleteItems()
	{
		ArrayList localArrayList = new ArrayList();
		Iterator localIterator = this.mDownloadList.iterator();
		while (localIterator.hasNext())
		{
			DownloadItem localDownloadItem = (DownloadItem) localIterator
					.next();
			if ((localDownloadItem.getStatus() != 4)
					&& (NetUtil.getDownLoadNetType() == localDownloadItem
							.getNetworkType()))
				localArrayList.add(localDownloadItem);
		}
		return localArrayList;
	}

	@Override
	public DownloadItem getDownloadItemByFileName(String paramString,
			int paramInt)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<DownloadItem> getDownloadItemByStatus(int paramInt)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<DownloadItem> getDownloadItemByType(int paramInt)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DownloadItem getFirstDownloadItemByStatus(int paramInt)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTheFileExist(DownloadItem paramDownloadItem)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DownloadItem isTheFileInDownloadList(DownloadItem paramDownloadItem)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DownloadTask queryAliveTask(String paramString)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeCacheSong(DownloadItem paramDownloadItem)
	{
		synchronized (this.mDownloadList)
		{
			logger.v("removeCacheSong() ---> Enter");
			if (paramDownloadItem != null)
			{
				File localFile = new File(paramDownloadItem.getFilePath()
						+ ".part");
				if (localFile.exists())
					localFile.delete();
			}
			logger.v("removeCacheSong() ---> Exit");
			return;
		}
	}

	@Override
	public void removeCompletedFile(DownloadItem downloaditem)
	{
		synchronized (this.mDownloadList)
		{
			logger.v("removeCompletedFile() ---> Enter");
			if (downloaditem == null)
			{
				logger.v("removeCompletedFile() ---> Exit");
				return;
			} else
			{
				String s = downloaditem.getFilePath();
				List list1 = mDBController.getAllPlaylists(1);
				Song song = mDBController.getSongByPath(s);
				if (song == null)
				{
					File file = new File(s);
					if (file.exists())
						file.delete();
					if (downloaditem.getContentType() != -100
							&& downloaditem.getContentType() != -200)
					{
						File file1 = new File((new StringBuilder(
								String.valueOf(s.substring(0,
										1 + s.lastIndexOf(".")))))
								.append("lrc").toString());
						if (file1.exists())
							file1.delete();
					}
				} else
				{
					for (int i = 0; i < list1.size(); i++)
					{
						DBController dbcontroller = mDBController;
						long l = ((Playlist) list1.get(i)).mExternalId;
						long al[] = new long[1];
						al[0] = song.mId;
						dbcontroller.deleteSongsFromPlaylist(l, al, 1);
					}
					mDBController.deleteSongFromDB(song.mId);
				}
			}
		}
	}

	@Override
	public void removeDownloadItem(DownloadItem downloaditem)
	{
		synchronized (this.mDownloadList)
		{
			logger.v("removeDownloadItem() ---> Enter");
			if (downloaditem != null)
			{
				mDownloadList.remove(downloaditem);
				mDBController.deleteDBDlItemById(downloaditem.getItemId());
				logger.d((new StringBuilder("remove item id: ")).append(
						String.valueOf(downloaditem.getItemId())).toString());
				mDispatcher.sendMessage(mDispatcher.obtainMessage(2002));
				mSystemController
						.scanDirAsync(GlobalSettingParameter.LOCAL_PARAM_MUSIC_STORE_SD_DIR);
				mSystemController
						.scanDirAsync(GlobalSettingParameter.LOCAL_PARAM_DOBLY_MUSIC_STORE_SD_DIR);
			}
			logger.v("removeDownloadItem() ---> Exit");
		}
	}

	@Override
	public boolean submitMediaDlTask(DownloadTask downloadtask)
	{
		boolean flag = false;
		logger.v("submitMediaDlTask() ---> Enter");
		DownloadItem downloaditem = downloadtask.getDownloadItem();
		String s = downloaditem.getFileName();
		boolean flag1 = isTheFileExist(downloaditem);
		if (flag1)
		{
			downloadtask.setErrCode(-5);
			logger.v("This file has alrealdy exist in sd card or phone");
		} else
		{
			DownloadItem downloaditem1 = isTheFileInDownloadList(downloaditem);
			if (downloaditem1 == null || downloaditem1 != null
					&& !downloaditem1.getFilePath().contains("/"))
			{
				String s1 = getTheProperStoreDir(downloadtask);
				if (s1 == null)
				{
					downloadtask.setErrCode(-2);
					logger.v("space is not enough() ---> Exit");
					flag = false;
				} else
				{
					logger.d((new StringBuilder(
							"submitTask(), the download path is: ")).append(s1)
							.append(s).toString());
					downloaditem.setFilePath((new StringBuilder(String
							.valueOf(s1))).append(s).toString());
					submitTask(downloadtask);
					logger.v("submitMediaDlTask() ---> Exit");
					flag = true;
				}
			} else
			{
				if (downloaditem1.getStatus() == 4)
				{
					downloaditem1.setStatus(downloaditem.getStatus());
					downloaditem1.setDownloadSize(downloaditem
							.getDownloadSize());
					downloaditem1.setSizeFromStart(downloaditem
							.getSizeFromStart());
					downloaditem1.setTimeStartDL(downloaditem.getTimeStartDL());
					downloaditem1.setFileSize(downloaditem.getFileSize());
					updateDownloadItem(downloaditem1);
				}
				downloadtask.setDownloadItem(downloaditem1);
				if (downloaditem1.getStatus() != 1 || !flag1)
					submitTask(downloadtask);
				downloadtask.setErrCode(-6);
				logger.d("submitTask(), download item has in download list, just submit it.");
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public boolean submitUpdateDlTask(DownloadTask downloadtask)
	{
		boolean flag = false;
		logger.v("submitUpdateDlTask() ---> Enter");
		DownloadItem downloaditem = downloadtask.getDownloadItem();
		String s = downloaditem.getFileName();
		DownloadItem downloaditem1;
		File afile[];
		switch (downloaditem.getContentType())
		{
		default:
			break;
		case -400:
			if (isTheFileExist(downloaditem))
			{
				downloadtask.setErrCode(-5);
				logger.v("This file has alrealdy exist in sd card or phone");
				flag = false;
			}
		case -300:
			downloaditem1 = isTheFileInDownloadList(downloaditem);
			if (downloaditem1 != null)
			{
				if ((new File(
						(new StringBuilder(
								String.valueOf(GlobalSettingParameter.LOCAL_PARAM_UPDATE_STORE_SD_DIR)))
								.append(downloaditem1.getFileName())
								.append(".part").toString())).exists())
				{
					downloadtask.setErrCode(-6);
					downloadtask.setDownloadItem(downloaditem1);
				}
			} else
			{
				afile = (new File(
						GlobalSettingParameter.LOCAL_PARAM_UPDATE_STORE_SD_DIR))
						.listFiles();
				if (afile != null)
				{
					int i = 0;
					while (i < afile.length)
					{
						afile[i].delete();
						i++;
					}
				}
			}
			break;
		}

		if (!isTheFileExist(downloaditem))
		{
			String s1 = getTheProperStoreDir(downloadtask);
			if (s1 == null)
			{
				downloadtask.setErrCode(-2);
				logger.v("space is not enough() ---> Exit");
			} else
			{
				logger.d((new StringBuilder(
						"submitTask(), the download path is: ")).append(s1)
						.append(s).toString());
				downloaditem
						.setFilePath((new StringBuilder(String.valueOf(s1)))
								.append(s).toString());
				submitTask(downloadtask);
				logger.v("submitUpdateDlTask() ---> Exit");
				flag = true;
			}
		} else
		{
			downloadtask.setErrCode(-5);
			logger.v("This file has alrealdy exist in sd card or phone");
			flag = false;
		}
		return flag;
	}

	@Override
	public void updateDownloadItem(DownloadItem downloaditem)
	{
		logger.v("updateDownloadItem() ---> Enter");
		mDBController.updateDBDownloadItem(downloaditem);
		mDispatcher.sendMessage(mDispatcher.obtainMessage(2002));
		logger.v("updateDownloadItem() ---> Exit");
	}

	@Override
	public void removeCacheSongs(ArrayList arraylist)
	{
		synchronized (this.mDownloadList)
		{
			List list = mDownloadList;
			logger.v("removeCacheSongs() ---> Enter");
			if (arraylist.isEmpty())
			{
				logger.v("removeCacheSongs() ---> Exit");
				return;
			} else
			{
				Iterator iterator = arraylist.iterator();
				while (iterator.hasNext())
					removeCacheSong((DownloadItem) iterator.next());
			}
		}
	}

	@Override
	public void removeDownloadItems(ArrayList arraylist)
	{
		synchronized (this.mDownloadList)
		{
			logger.v("removeDownloadItems() ---> Enter");
			Iterator localIterator;
			if (!arraylist.isEmpty())
			{
				localIterator = arraylist.iterator();
				while (localIterator.hasNext())
				{
					DownloadItem localDownloadItem = (DownloadItem) localIterator
							.next();
					this.mDownloadList.remove(localDownloadItem);
					this.mDBController.deleteDBDlItemById(localDownloadItem
							.getItemId());
				}
				this.mDispatcher.sendMessage(this.mDispatcher
						.obtainMessage(2002));
			} else
			{
				logger.v("removeDownloadItems() ---> Exit");
				return;
			}
		}
	}

	public void taskStarted(DownloadTask downloadtask)
	{
		logger.v("taskStarted() ---> Enter");
		if (BindingContainer.getInstance().hasDownloadTask(downloadtask))
		{
			DownloadItem downloaditem = downloadtask.getDownloadItem();
			if (downloaditem.getContentType() == -300
					|| downloaditem.getContentType() == -400)
				mDispatcher.sendMessage(mDispatcher.obtainMessage(2015,
						downloadtask));
			else
			{
				mDispatcher.sendMessage(mDispatcher.obtainMessage(2003,
						downloadtask));
			}
		}
		logger.v("taskStarted() ---> Exit");
	}

	public void taskProgress(DownloadTask paramDownloadTask, long paramLong1,
			long paramLong2)
	{
		logger.v("taskProgress() ---> Enter");
		if (BindingContainer.getInstance().hasDownloadTask(paramDownloadTask))
		{
			DownloadItem localDownloadItem = paramDownloadTask
					.getDownloadItem();
			if ((localDownloadItem.getContentType() == -300)
					|| (localDownloadItem.getContentType() == -400))
				this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(
						2017, paramDownloadTask));
			else
			{
				this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(
						2005, (int) paramLong1, (int) paramLong2,
						paramDownloadTask));
			}
		}
		logger.v("taskProgress() ---> Exit");
	}

	@Override
	public void taskCanceled(DownloadTask downloadtask)
	{
		logger.v("taskCanceled() ---> Enter");
		if (BindingContainer.getInstance().hasDownloadTask(downloadtask))
		{
			DownloadItem downloaditem = downloadtask.getDownloadItem();
			if (downloaditem.getContentType() == -300
					|| downloaditem.getContentType() == -400)
				mDispatcher.sendMessage(mDispatcher.obtainMessage(2016,
						downloadtask));
			else
				mDispatcher.sendMessage(mDispatcher.obtainMessage(2004,
						downloadtask));
			BindingContainer.getInstance().removeDownloadTask(downloadtask);
		}
		logger.v("taskCanceled() ---> Exit");
	}

	@Override
	public void taskCmWapClosed(DownloadTask paramDownloadTask)
	{
		logger.v("taskCmWapClosed() ---> Enter");
		if (BindingContainer.getInstance().hasDownloadTask(paramDownloadTask))
		{
			BindingContainer.getInstance()
					.removeDownloadTask(paramDownloadTask);
			DownloadItem localDownloadItem = paramDownloadTask
					.getDownloadItem();
			if ((localDownloadItem.getContentType() == -300)
					|| (localDownloadItem.getContentType() == -400))
			{
				this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(
						2018, paramDownloadTask));
				this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(
						2020, paramDownloadTask));
			} else
			{
				this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(
						2006, paramDownloadTask));
				this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(
						2021, paramDownloadTask));
			}
		}
		logger.v("taskCmWapClosed() ---> Exit");

	}

	@Override
	public void taskCompleted(DownloadTask paramDownloadTask)
	{
		logger.v("taskCompleted() ---> Enter");
		if (BindingContainer.getInstance().hasDownloadTask(paramDownloadTask))
		{
			DownloadItem localDownloadItem = paramDownloadTask
					.getDownloadItem();
			if ((localDownloadItem.getContentType() == -300)
					|| (localDownloadItem.getContentType() == -400))
			{
				this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(
						2019, paramDownloadTask));
			} else
			{
				this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(
						2007, paramDownloadTask));
			}
			BindingContainer.getInstance()
					.removeDownloadTask(paramDownloadTask);
		}
		logger.v("taskCompleted() ---> Exit");
	}

	@Override
	public void taskFailed(DownloadTask paramDownloadTask,
			Throwable paramThrowable)
	{
		logger.v("taskFailed() ---> Enter");
		if (BindingContainer.getInstance().hasDownloadTask(paramDownloadTask))
		{
			DownloadItem localDownloadItem = paramDownloadTask
					.getDownloadItem();
			if ((localDownloadItem.getContentType() == -300)
					|| (localDownloadItem.getContentType() == -400))
				this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(
						2018, paramDownloadTask));
			else
			{
				this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(
						2006, paramDownloadTask));
			}
			BindingContainer.getInstance()
					.removeDownloadTask(paramDownloadTask);
		}
		logger.v("taskFailed() ---> Exit");
	}

	@Override
	public void removeCompletedFiles(ArrayList arraylist)
	{
		synchronized (this.mDownloadList)
		{
			logger.v("removeCompletedFiles() ---> Enter");
			Iterator localIterator;
			if (!arraylist.isEmpty())
			{
				localIterator = arraylist.iterator();
				while (localIterator.hasNext())
					removeCompletedFile((DownloadItem) localIterator.next());
			} else
			{
				logger.v("removeCompletedFiles() ---> Exit");
				return;
			}

		}
	}

	private String getTheProperStoreDir(DownloadTask downloadtask)
	{
		logger.v("getTheProperStoreDir() ---> Enter");
		DownloadItem downloaditem = downloadtask.getDownloadItem();
		long l = downloaditem.getFileSize();
		int i = downloaditem.getContentType();
		String s;
		if (i == -100)
			s = Util.getRingtoneStoreDir(l);
		else if (i == -200)
			s = Util.getMVStoreDir(l);
		else if (i == -300)
			s = Util.getUpdateStoreDir(l);
		else if (i == -400)
			s = Util.getSkinStoreDir(l);
		else if (i == -500)
			s = Util.getDoblySongStoreDir(l);
		else
			s = Util.getSongStoreDir(l);
		logger.v("getTheProperStoreDir() ---> Exit");
		return s;
	}

	public void submitTask(DownloadTask downloadtask)
	{
		logger.v("submitTask() ---> Enter");
		if (downloadtask != null)
		{
			downloadtask.setTransId(MobileMusicApplication.getTransId());
			downloadtask.setDownloadListener(this);
			BindingContainer.getInstance().addDownloadTask(downloadtask);
			(new Thread(downloadtask)).start();
			logger.v("submitTask() ---> Exit");
		}
	}
}
