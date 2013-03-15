package org.ming.center.download;

public abstract interface DLTaskListener
{
	public abstract void taskCanceled(DownloadTask paramDownloadTask);

	public abstract void taskCmWapClosed(DownloadTask paramDownloadTask);

	public abstract void taskCompleted(DownloadTask paramDownloadTask);

	public abstract void taskFailed(DownloadTask paramDownloadTask,
			Throwable paramThrowable);

	public abstract void taskProgress(DownloadTask paramDownloadTask,
			long paramLong1, long paramLong2);

	public abstract void taskStarted(DownloadTask paramDownloadTask);
}