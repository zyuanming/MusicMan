package org.ming.center.player;

import android.os.Message;

public abstract interface PlayerEventListener
{
	public abstract void handlePlayerEvent(Message paramMessage);
}