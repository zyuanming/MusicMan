package org.ming.center.download;

import android.os.Message;

public abstract interface DLEventListener
{
	public abstract void handleDLEvent(Message paramMessage);
}
