package org.ming.center.http;

public abstract interface MMHttpTaskListener
{
	public abstract void taskCanceled(MMHttpTask paramMMHttpTask);

	public abstract void taskCmWapClosed(MMHttpTask paramMMHttpTask);

	public abstract void taskCompleted(MMHttpTask paramMMHttpTask);

	public abstract void taskEnd(MMHttpTask paramMMHttpTask);

	public abstract void taskFailed(MMHttpTask paramMMHttpTask);

	public abstract void taskStarted(MMHttpTask paramMMHttpTask);

	public abstract void taskTimeOut(MMHttpTask paramMMHttpTask);

	public abstract void taskWlanClosed(MMHttpTask paramMMHttpTask);
}