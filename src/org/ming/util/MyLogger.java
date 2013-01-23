package org.ming.util;

import java.util.Hashtable;

import android.util.Log;

public class MyLogger
{
	private static final String LOG_TAG = "[MobileMusic40]";
	private static Hashtable<String, MyLogger> sLoggerTable = new Hashtable<String, MyLogger>();
	private String mClassName;

	private MyLogger(String paramString)
	{
		this.mClassName = paramString;
	}

	public static MyLogger getLogger(String paramString)
	{
		MyLogger localMyLogger = (MyLogger) sLoggerTable.get(paramString);
		if (localMyLogger == null)
		{
			localMyLogger = new MyLogger(paramString);
			sLoggerTable.put(paramString, localMyLogger);
		}
		return localMyLogger;
	}

	public void d(String paramString)
	{
		Log.d(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}"
				+ "[" + this.mClassName + ":] " + paramString);
	}

	public void e(String paramString)
	{

		Log.e(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}"
				+ "[" + this.mClassName + ":] " + paramString);
	}

	public void e(String paramString, Throwable paramThrowable)
	{

		Log.e(LOG_TAG,
				"{Thread:" + Thread.currentThread().getName() + "}" + "["
						+ this.mClassName + ":] " + paramString + "\n"
						+ Log.getStackTraceString(paramThrowable));
	}

	public void i(String paramString)
	{

		Log.i(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}"
				+ "[" + this.mClassName + ":] " + paramString);
	}

	public void i(String paramString, Throwable paramThrowable)
	{

		Log.i("[MobileMusic40]", "{Thread:" + Thread.currentThread().getName()
				+ "}" + "[" + this.mClassName + ":] " + paramString + "\n"
				+ Log.getStackTraceString(paramThrowable));

	}

	public void v(String paramString)
	{

		Log.v(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}"
				+ "[" + this.mClassName + ":] " + paramString);
	}

	public void w(String paramString)
	{

		Log.w(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}"
				+ "[" + this.mClassName + ":] " + paramString);
	}

	public void w(String paramString, Throwable paramThrowable)
	{

		Log.w(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}"
				+ "[" + this.mClassName + ":] " + paramString + "\n");

	}
}