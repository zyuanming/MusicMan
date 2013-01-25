package org.ming.center.download;

public abstract interface DLController
{

	public abstract void cancelAllTask();

	public abstract void cancelDownloadNotification();

	public abstract void cancelDownloadRemainNotification();

	public abstract void initDownloadListFromDB();

	public abstract void removeDownloadItemFromLocal(String paramString);

	public abstract void startAllTask();

}