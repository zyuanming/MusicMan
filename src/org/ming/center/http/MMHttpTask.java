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

	private int doNetWorkTask(String s)
	{
		return 0;
		// logger.v("doNetWorkTask() ---> Enter");
		// if(!mIsUseCache) goto _L2; else goto _L1
		// {
		// DefaultHttpClient defaulthttpclient;
		// defaulthttpclient = Util.createNetworkClient(false);
		// if(defaulthttpclient == null)
		// {
		// if(mListener != null)
		// if(s.startsWith("https"))
		// mListener.taskWlanClosed(this);
		// else
		// mListener.taskCmWapClosed(this);
		// byte0 = 0;
		// continue; /* Loop/switch isn't completed */
		// }
		// if(!mReq.isPostMethod()) goto _L6; else goto _L5
		// {
		// httpget = new HttpGet(s);
		// obj = httpget;
		// if(!mReq.isPostMethod()) goto _L8; else goto _L7
		// {
		// map = mReq.getHeaders();
		// if(map == null || map.isEmpty()) goto _L12; else goto _L11
		// {
		// HttpResponse httpresponse =
		// defaulthttpclient.execute(((HttpUriRequest) (obj)));
		// int i = httpresponse.getStatusLine().getStatusCode();
		// logger.e((new StringBuilder("HTTP retCode: ")).append(i).toString());
		// InputStream inputstream = httpresponse.getEntity().getContent();
		// mResponseBody = Util.InputStreamToByte(inputstream);
		// if(inputstream != null)
		// inputstream.close();
		// if(i == 200)
		// break MISSING_BLOCK_LABEL_550;
		// logger.e((new
		// StringBuilder("HTTPS taskFailed: ")).append(s).toString());
		// byte0 = -1;
		// continue; /* Loop/switch isn't completed */
		// }
		// else
		// {
		// iterator = map.entrySet().iterator();
		// }
		// }
		// else
		// {
		// String s7;
		// byte abyte0[];
		// s7 = mReq.getReqBodyString();
		// abyte0 = mReq.getReqBody();
		// if(s7 == null) goto _L10; else goto _L9
		// {
		//
		// }
		// }
		// exception;
		// exception.printStackTrace();
		// obj = null;
		// if(!mReq.isPostMethod()) goto _L8; else goto _L7
		// {
		// map = mReq.getHeaders();
		// if(map == null || map.isEmpty()) goto _L12; else goto _L11
		// {
		// HttpResponse httpresponse =
		// defaulthttpclient.execute(((HttpUriRequest) (obj)));
		// int i = httpresponse.getStatusLine().getStatusCode();
		// logger.e((new StringBuilder("HTTP retCode: ")).append(i).toString());
		// InputStream inputstream = httpresponse.getEntity().getContent();
		// mResponseBody = Util.InputStreamToByte(inputstream);
		// if(inputstream != null)
		// inputstream.close();
		// if(i == 200)
		// break MISSING_BLOCK_LABEL_550;
		// logger.e((new
		// StringBuilder("HTTPS taskFailed: ")).append(s).toString());
		// byte0 = -1;
		// continue; /* Loop/switch isn't completed */
		// }
		// else
		// {
		// iterator = map.entrySet().iterator();
		// }
		// }
		// else
		// {
		// String s7;
		// byte abyte0[];
		// s7 = mReq.getReqBodyString();
		// abyte0 = mReq.getReqBody();
		// if(s7 == null) goto _L10; else goto _L9
		// {
		// if(abyte0 == null) goto _L8; else goto _L14
		// {
		// map = mReq.getHeaders();
		// if(map == null || map.isEmpty()) goto _L12; else goto _L11
		// {
		// HttpResponse httpresponse =
		// defaulthttpclient.execute(((HttpUriRequest) (obj)));
		// int i = httpresponse.getStatusLine().getStatusCode();
		// logger.e((new StringBuilder("HTTP retCode: ")).append(i).toString());
		// InputStream inputstream = httpresponse.getEntity().getContent();
		// mResponseBody = Util.InputStreamToByte(inputstream);
		// if(inputstream != null)
		// inputstream.close();
		// if(i == 200)
		// break MISSING_BLOCK_LABEL_550;
		// logger.e((new
		// StringBuilder("HTTPS taskFailed: ")).append(s).toString());
		// byte0 = -1;
		// continue; /* Loop/switch isn't completed */
		// }
		// else
		// {
		// iterator = map.entrySet().iterator();
		// }
		// }
		// else
		// {
		// ByteArrayEntity bytearrayentity = new ByteArrayEntity(abyte0);
		// bytearrayentity.setContentType(mReq.getContentType());
		// ((HttpPost)obj).setEntity(bytearrayentity);
		// map = mReq.getHeaders();
		// if(map == null || map.isEmpty()) goto _L12; else goto _L11
		// {
		// HttpResponse httpresponse =
		// defaulthttpclient.execute(((HttpUriRequest) (obj)));
		// int i = httpresponse.getStatusLine().getStatusCode();
		// logger.e((new StringBuilder("HTTP retCode: ")).append(i).toString());
		// InputStream inputstream = httpresponse.getEntity().getContent();
		// mResponseBody = Util.InputStreamToByte(inputstream);
		// if(inputstream != null)
		// inputstream.close();
		// if(i == 200)
		// break MISSING_BLOCK_LABEL_550;
		// logger.e((new
		// StringBuilder("HTTPS taskFailed: ")).append(s).toString());
		// byte0 = -1;
		// continue; /* Loop/switch isn't completed */
		// }
		// else
		// {
		// iterator = map.entrySet().iterator();
		// }
		// java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
		// ((HttpUriRequest) (obj)).addHeader((String)entry.getKey(),
		// (String)entry.getValue());
		// if(iterator.hasNext())
		// break MISSING_BLOCK_LABEL_508;
		// if(!mIsUseCache) goto _L17; else goto _L16
		// {
		// if(mListener != null && !mIsCancled)
		// mListener.taskCompleted(this);
		// logger.i((new StringBuilder("doTask() get response: \n")).append(new
		// String(mResponseBody)).toString());
		// logger.v("doTask() ---> Exit");
		// byte0 = 0;
		// continue; /* Loop/switch isn't completed */
		// }
		// else
		// {
		// String s1;
		// String s2;
		// s1 = new String(mResponseBody);
		// int j = s.indexOf("rdp2");
		// if(j == -1)
		// {
		// byte0 = -1;
		// continue; /* Loop/switch isn't completed */
		// }
		// s2 = s.substring(j);
		// if(s1 == null || s1.length() <= 0) goto _L17; else goto _L18
		// {
		// if(mListener != null && !mIsCancled)
		// mListener.taskCompleted(this);
		// logger.i((new StringBuilder("doTask() get response: \n")).append(new
		// String(mResponseBody)).toString());
		// logger.v("doTask() ---> Exit");
		// byte0 = 0;
		// continue; /* Loop/switch isn't completed */
		// }
		// else
		// {
		// int k;
		// int l;
		// k = 6 + s1.indexOf("<code>");
		// l = s1.indexOf("</code>");
		// if(k <= 0 || k >= s1.length() || l <= 0 || l >= s1.length() ||
		// !s1.substring(k, l).equals("000000")) goto _L17; else goto _L19
		// {
		//
		// }
		// else
		// {
		// int i1;
		// String s3;
		// i1 = s.indexOf("groupcode=");
		// s3 = null;
		// if(i1 == -1) goto _L21; else goto _L20
		// {
		// if(s3 == null && s1.contains("<groupcode>"))
		// s3 = s1.substring(11 + s1.indexOf("<groupcode>"),
		// s1.indexOf("</groupcode>"));
		// if(s3 != null)
		// {
		// String s4 = s1.substring(14 + s1.indexOf("<publish_time>"),
		// s1.indexOf("</publish_time>"));
		// mCacheDataManager.saveCacheData(s2, s3, s4, s1);
		// }
		// }
		// else
		// {
		// String s5;
		// int j1;
		// s5 = s.substring(i1);
		// j1 = s5.indexOf("&");
		// if(j1 == -1) goto _L23; else goto _L22
		// {
		// String s6 = s5.substring(10);
		// s3 = s6;
		// if(s3 == null && s1.contains("<groupcode>"))
		// s3 = s1.substring(11 + s1.indexOf("<groupcode>"),
		// s1.indexOf("</groupcode>"));
		// if(s3 != null)
		// {
		// String s4 = s1.substring(14 + s1.indexOf("<publish_time>"),
		// s1.indexOf("</publish_time>"));
		// mCacheDataManager.saveCacheData(s2, s3, s4, s1);
		// }
		// Exception exception1;
		// exception1;
		// logger.e("doTask() Exception: ", exception1);
		// if((exception1 instanceof ConnectTimeoutException) || (exception1
		// instanceof SocketTimeoutException))
		// mIsTimeOut = true;
		// byte0 = -1;
		// if(true) goto _L25; else goto _L24
		// {
		//
		// }
		// }
		// else
		// {
		// s3 = s5.substring(10, j1);
		// }
		// }
		// }
		// }
		// }
		// }
		// else
		// {
		// Exception exception;
		// Map map;
		// Iterator iterator;
		// HttpGet httpget;
		// try
		// {
		// StringEntity stringentity = new StringEntity(s7, "UTF-8");
		// stringentity.setContentType(mReq.getContentType());
		// stringentity.setContentEncoding(mReq.getContentEncoding());
		// ((HttpPost)obj).setEntity(stringentity);
		// }
		// catch(UnsupportedEncodingException unsupportedencodingexception)
		// {
		// logger.e("doTask(), Exception: ", unsupportedencodingexception);
		// byte0 = -1;
		// continue; /* Loop/switch isn't completed */
		// }
		// }
		// }
		// }
		// else
		// {
		// int k1 = s.indexOf("rdp2");
		// if(k1 != -1) goto _L4; else goto _L3
		// {
		// String s8 = s.substring(k1);
		// if(mCacheDataManager.isInCacheData(s8))
		// {
		// byte abyte1[] = mCacheDataManager.getCacheData(s8).getBytes();
		// if(abyte1 != null && abyte1.length > 0)
		// {
		// mResponseBody = abyte1;
		// mListener.taskCompleted(this);
		// byte0 = 0;
		// continue; /* Loop/switch isn't completed */
		// }
		// }
		// }
		// else
		// {
		// byte byte0 = -1;
		// }
		// }
		// }
		// else
		// {
		// HttpPost httppost = new HttpPost(s);
		// Object obj = httppost;
		//
		// }
		// return byte0;
		// }
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
