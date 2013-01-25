package org.ming.dispatcher;

import org.ming.util.MyLogger;

import android.os.Handler;
import android.os.Message;

public class Dispatcher extends Handler
{
	private static final MyLogger logger = MyLogger.getLogger("Dispatcher");
	private static Dispatcher sInstance = null;
	private DispatcherEventListener mListener = null;

	private Dispatcher(DispatcherEventListener paramDispatcherEventListener)
	{
		this.mListener = paramDispatcherEventListener;
	}

	public static Dispatcher getInstance(
			DispatcherEventListener paramDispatcherEventListener)
	{
		logger.v("getInstance()");
		if (sInstance == null)
			sInstance = new Dispatcher(paramDispatcherEventListener);
		return sInstance;
	}

	public void handleMessage(Message paramMessage)
	{
		if (this.mListener != null)
			this.mListener.handleMessage(paramMessage);
	}

	public void setListener(DispatcherEventListener paramDispatcherEventListener)
	{
		this.mListener = paramDispatcherEventListener;
	}
}
