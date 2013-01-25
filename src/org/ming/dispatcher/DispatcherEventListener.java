package org.ming.dispatcher;

import android.os.Message;

public abstract interface DispatcherEventListener
{
	public abstract void handleMessage(Message paramMessage);
}