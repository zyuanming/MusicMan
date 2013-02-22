// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package org.ming.center.http;

import org.ming.center.cachedata.CacheDataManager;
import org.ming.util.MyLogger;

public class MMHttpTask implements Runnable
{
	public static final int NETWORK_RETRY_TIMES = 3;
	private static final MyLogger logger = MyLogger.getLogger("MMHttpTask");
	private static CacheDataManager mCacheDataManager = null;
	private long mContentLength;
	private boolean mIsCancled;
	private boolean mIsTimeOut;
	private boolean mIsUseCache;
	private MMHttpTaskListener mListener;
	private MMHttpRequest mReq;
	private byte mResponseBody[];
	private int mTransId;

	public MMHttpTask(MMHttpRequest mmhttprequest,
			MMHttpTaskListener mmhttptasklistener)
	{
		mReq = null;
		mListener = null;
		mIsCancled = false;
		mIsTimeOut = false;
		mIsUseCache = false;
		mTransId = -1;
		mResponseBody = null;
		mContentLength = -1L;
		mReq = mmhttprequest;
		mListener = mmhttptasklistener;
		mTransId = -1;
		mCacheDataManager = CacheDataManager.getInstance();
	}

	public MMHttpTask(MMHttpTaskListener mmhttptasklistener)
	{
		mReq = null;
		mListener = null;
		mIsCancled = false;
		mIsTimeOut = false;
		mIsUseCache = false;
		mTransId = -1;
		mResponseBody = null;
		mContentLength = -1L;
		mListener = mmhttptasklistener;
	}

	/**
	 * 还没有完成的方法
	 * 
	 * @param s
	 * @return
	 */
	private int doNetWorkTask(String s)
	{
		logger.v("doNetWorkTask() ---> Enter");
		logger.d("nothing to do the networktask");
		logger.v("doNetWorkTask() ---> Exit");
		return 0;
	}

	private int doTask()
	{
		int i = -1;
		logger.v("doTask() ---> Enter");
		if (mReq != null)
		{
			String s = mReq.getUrlWithParams();
			if (s != null)
			{
				logger.d((new StringBuilder("doTask(), url with params is: "))
						.append(s).toString());
				i = doNetWorkTask(s);
			}
		}
		return i;
	}

	public long getContentLength()
	{
		return mContentLength;
	}

	public MMHttpTaskListener getListener()
	{
		return mListener;
	}

	public MMHttpRequest getRequest()
	{
		return mReq;
	}

	public byte[] getResponseBody()
	{
		return mResponseBody;
	}

	public int getTransId()
	{
		return mTransId;
	}

	public boolean isCancled()
	{
		return mIsCancled;
	}

	public void run()
	{
		logger.v("run() ---> Enter");
		if (mListener != null)
			mListener.taskStarted(this);
		int i = 3;
		for (int j = -1; j != -1 || i <= 0 || mIsTimeOut;)
		{
			if (j == -1)
				if (mIsTimeOut)
					mListener.taskTimeOut(this);
				else
					mListener.taskFailed(this);
			if (mListener != null)
				mListener.taskEnd(this);
			j = doTask();
			i--;
			logger.v("run() ---> Exit");
		}

	}

	public void setCacheAble(boolean flag)
	{
		mIsUseCache = flag;
	}

	public void setIsCancled(boolean flag)
	{
		mIsCancled = flag;
	}

	public void setListener(MMHttpTaskListener mmhttptasklistener)
	{
		mListener = mmhttptasklistener;
	}

	public void setRequest(MMHttpRequest mmhttprequest)
	{
		mReq = mmhttprequest;
	}

	public void setTransId(int i)
	{
		mTransId = i;
	}
}
