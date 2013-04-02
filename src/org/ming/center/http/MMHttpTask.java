package org.ming.center.http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ming.center.cachedata.CacheDataManager;
import org.ming.util.MyLogger;
import org.ming.util.Util;

import android.util.Log;

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
	 * 
	 * 
	 * @param s
	 * @return
	 */
	private int doNetWorkTask(String s)
	{
		logger.v("doNetWorkTask() ---> Enter");
		int byte0 = -1;
		int i = 0;
		HttpGet httpget;
		HttpPost httppost;
		Object obj;
		Map map;
		Iterator iterator;
		if (!mIsUseCache)
		{
			DefaultHttpClient defaulthttpclient = Util
					.createNetworkClient(false);
			if (defaulthttpclient == null)
			{
				if (mListener != null)
					if (s.startsWith("https"))
						mListener.taskWlanClosed(this);
					else
						mListener.taskCmWapClosed(this);
				byte0 = 0;
			}
			// 初始化请求体：obj
			if (!mReq.isPostMethod())
			{
				httpget = new HttpGet(s);
				obj = httpget;
			} else
			{
				httppost = new HttpPost(s);
				obj = httppost;
			}
			// 构造请求体：obj
			if (!mReq.isPostMethod())
			{
				map = mReq.getHeaders();
				if (map != null && !map.isEmpty())
				{
					iterator = map.entrySet().iterator();
					while (iterator.hasNext())
					{
						Entry entry = (Entry) iterator.next();
						((HttpUriRequest) (obj)).addHeader(
								(String) entry.getKey(),
								(String) entry.getValue());
					}
				}
			} else
			{
				String s7 = mReq.getReqBodyString();
				byte abyte0[] = mReq.getReqBody();
				if (s7 == null)
				{
					if (abyte0 != null)
					{
						ByteArrayEntity bytearrayentity = new ByteArrayEntity(
								abyte0);
						bytearrayentity.setContentType(mReq.getContentType());
						((HttpPost) obj).setEntity(bytearrayentity);
					}
					map = mReq.getHeaders();
					if (map != null && !map.isEmpty())
					{
						iterator = map.entrySet().iterator();
						while (iterator.hasNext())
						{
							Entry entry = (Entry) iterator.next();
							((HttpUriRequest) (obj)).addHeader(
									(String) entry.getKey(),
									(String) entry.getValue());
						}
					}
				} else
				{
					try
					{
						StringEntity stringentity = new StringEntity(s7,
								"UTF-8");
						stringentity.setContentType(mReq.getContentType());
						stringentity.setContentEncoding(mReq
								.getContentEncoding());
						((HttpPost) obj).setEntity(stringentity);
					} catch (UnsupportedEncodingException unsupportedencodingexception)
					{
						logger.e("doTask(), Exception: ",
								unsupportedencodingexception);
						byte0 = -1;
					}
				}
			}
			// 开始网络请求
			try
			{
				HttpResponse httpresponse = defaulthttpclient
						.execute(((HttpUriRequest) (obj)));
				i = httpresponse.getStatusLine().getStatusCode();
				logger.e((new StringBuilder("HTTP retCode: ")).append(i)
						.toString());
				InputStream inputstream = httpresponse.getEntity().getContent();
				mResponseBody = Util.InputStreamToByte(inputstream);
				Log.d("mingming", (new String(mResponseBody)).toString());
				if (inputstream != null)
					inputstream.close();
			} catch (Exception e)
			{
				logger.e("doTask() Exception: ", e);
				if ((e instanceof ConnectTimeoutException)
						|| (e instanceof SocketTimeoutException))
					mIsTimeOut = true;
				byte0 = -1;
			}
			if (i != 200)
			{
				// 网络请求出错
				logger.e((new StringBuilder("HTTPS taskFailed: ")).append(s)
						.toString());
				byte0 = -1;
			} else
			// 网络请求成功
			{
				// 获得返回的数据，构造指定的返回数据byte0
				if (!mIsUseCache)
				{
					if (mListener != null && !mIsCancled)
						mListener.taskCompleted(this);
					logger.i((new StringBuilder("doTask() get response: \n"))
							.append(new String(mResponseBody)).toString());
					logger.v("doTask() ---> Exit");
					byte0 = 0;
				} else
				{
					String s1 = new String(mResponseBody);
					String s2;
					int j = s.indexOf("rdp2");
					if (j == -1)
					{
						byte0 = -1;
					}
					s2 = s.substring(j);
					if (s1 == null || s1.length() <= 0)
					{
						if (mListener != null && !mIsCancled)
							mListener.taskCompleted(this);
						logger.i((new StringBuilder("doTask() get response: \n"))
								.append(new String(mResponseBody)).toString());
						logger.v("doTask() ---> Exit");
						byte0 = 0;
					} else
					{
						int k;
						int l;
						k = 6 + s1.indexOf("<code>");
						l = s1.indexOf("</code>");
						if (k <= 0 || k >= s1.length() || l <= 0
								|| l >= s1.length()
								|| !s1.substring(k, l).equals("000000"))
						{
							if (mListener != null && !mIsCancled)
								mListener.taskCompleted(this);
							logger.i((new StringBuilder(
									"doTask() get response: \n")).append(
									new String(mResponseBody)).toString());
							logger.v("doTask() ---> Exit");
							byte0 = 0;
						} else
						{
							int i1 = s.indexOf("groupcode=");
							String s3 = null;
							if (i1 != -1)
							{
								String s5 = s.substring(i1);
								int j1 = s5.indexOf("&");
								if (j1 == -1)
								{
									String s6 = s5.substring(10);
									s3 = s6;
								} else
								{
									s3 = s5.substring(10, j1);
								}

							}
							if (s3 == null && s1.contains("<groupcode>"))
								s3 = s1.substring(
										11 + s1.indexOf("<groupcode>"),
										s1.indexOf("</groupcode>"));
							if (s3 != null)
							{
								String s4 = s1.substring(
										14 + s1.indexOf("<publish_time>"),
										s1.indexOf("</publish_time>"));
								mCacheDataManager.saveCacheData(s2, s3, s4, s1);
							}
						}
					}
				}
			}
		} else
		{
			// 使用缓存的数据，不需要网络获取了，（在没有网络的情况下）
			int k1 = s.indexOf("rdp2");
			if (k1 != -1)
			{
				String s8 = s.substring(k1);
				if (mCacheDataManager.isInCacheData(s8))
				{
					byte abyte1[] = mCacheDataManager.getCacheData(s8)
							.getBytes();
					if (abyte1 != null && abyte1.length > 0)
					{
						mResponseBody = abyte1;
						mListener.taskCompleted(this);
						byte0 = 0;
					}
				}
			}
		}
		logger.v("doNetWorkTask() ---> Exit");
		return byte0;
	}

	private int doTask()
	{
		int i = -1;
		logger.v("doTask() ---> Enter");
		if (mReq != null)
		{
			String s = mReq.getUrlWithParams();
			Log.d("mingming", s);
			if (s != null)
			{
				logger.d((new StringBuilder("doTask(), url with params is: "))
						.append(s).toString());
				i = doNetWorkTask(s);
			}
		}
		logger.v("doTask() ----> Exit");
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
		int j = -1;
		do
		{
			if (j != -1 || i <= 0 || mIsTimeOut)
			{
				if (j == -1)
					if (mIsTimeOut)
						mListener.taskTimeOut(this);
					else
						mListener.taskFailed(this);
				if (mListener != null)
					mListener.taskEnd(this);
				logger.v("run() ---> Exit");
				return;
			}
			j = doTask();
			try
			{
				Thread.sleep(6000L);
			} catch (InterruptedException interruptedexception)
			{
				interruptedexception.printStackTrace();
			}
			i--;
		} while (true);
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
