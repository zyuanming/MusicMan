package org.ming.center.download;

import java.util.ArrayList;

public interface DLController
{

	public abstract long addDownloadItem(DownloadItem downloaditem);

	public abstract void cancelAllTask();

	public abstract void cancelDownloadNotification();

	public abstract void cancelDownloadRemainNotification();

	public abstract void cancelSingleTask(DownloadTask downloadtask);

	public abstract void downloadFirstBuyDrmRights(DownloadItem downloaditem);

	public abstract ArrayList getAllNoneCompleteItems();

	public abstract DownloadItem getDownloadItemByFileName(String s, int i);

	public abstract ArrayList getDownloadItemByStatus(int i);

	public abstract ArrayList getDownloadItemByType(int i);

	public abstract DownloadItem getFirstDownloadItemByStatus(int i);

	public abstract void initDownloadListFromDB();

	public abstract boolean isTheFileExist(DownloadItem downloaditem);

	public abstract DownloadItem isTheFileInDownloadList(
			DownloadItem downloaditem);

	public abstract DownloadTask queryAliveTask(String s);

	public abstract void removeCacheSong(DownloadItem downloaditem);

	public abstract void removeCacheSongs(ArrayList arraylist);

	public abstract void removeCompletedFile(DownloadItem downloaditem);

	public abstract void removeCompletedFiles(ArrayList arraylist);

	public abstract void removeDownloadItem(DownloadItem downloaditem);

	public abstract void removeDownloadItemFromLocal(String s);

	public abstract void removeDownloadItems(ArrayList arraylist);

	public abstract void startAllTask();

	public abstract boolean submitMediaDlTask(DownloadTask downloadtask);

	public abstract boolean submitUpdateDlTask(DownloadTask downloadtask);

	public abstract void updateDownloadItem(DownloadItem downloaditem);
}