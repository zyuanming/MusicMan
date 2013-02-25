package org.ming.center.system;

import android.os.Message;

public abstract interface SystemEventListener
{
	public abstract void handleSystemEvent(Message paramMessage);
}