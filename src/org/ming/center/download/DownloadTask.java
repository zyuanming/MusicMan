package org.ming.center.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ming.center.MobileMusicApplication;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;

import android.telephony.TelephonyManager;

public class DownloadTask implements Runnable
{
	public static final long FILE_LENGTH_UNKNOWN = -1000L;
	public static final int PACKAGE_SIZE = 1572864;
	public static final int REFRESH_INTERVAL_NOT_INFLUCE_CLICK = 500;
	public static final String TAG_RANGE = "RANGE";
	private static final MyLogger logger = MyLogger.getLogger("DownloadTask");
	private long mDownloadSize;
	private int mErrCode = -1;
	private boolean mIsCanceled = false;
	private DownloadItem mItem;
	private DLTaskListener mListener;
	private RandomAccessFile mRandFile;
	private int mTransId = -1;
	private boolean mWaitOrNot = false;

	public DownloadTask(DownloadItem paramDownloadItem)
	{
		this.mItem = paramDownloadItem;
		this.mDownloadSize = 0L;
		this.mTransId = -1;
	}

	private DownloadItem getUpdateFile() throws Exception
	{
		return null;
		// long l;
		// File file;
		// DownloadItem downloaditem;
		// logger.v("getUpdateFile() ---> Enter");
		// if(mItem.getFileSize() != -1000L)
		// {
		// l = mItem.getFileSize();
		// if(mListener != null)
		// mListener.taskStarted(this);
		// String s = mItem.getFilePath();
		// String s1 = s.substring(0, 1 + s.lastIndexOf("/"));
		// logger.d((new StringBuilder("folder is : ")).append(s1).toString());
		// file = new File((new
		// StringBuilder(String.valueOf(s))).append(".part").toString());
		// if(!file.exists())
		// file.createNewFile();
		// long l1 = file.length();
		// mItem.setDownloadSize(l1);
		// mDownloadSize = mItem.getDownloadSize();
		// long l2 = Util.getFreeSpace(s1);
		// if(l2 < l - mDownloadSize)
		// {
		// logger.e((new
		// StringBuilder("getFile(), no space available, free space is: ")).append(l2).append(" Bytes").toString());
		// mErrCode = -2;
		// throw new Exception();
		// }
		// if(!mIsCanceled && l > mDownloadSize)
		// {
		// InputStream inputstream;
		// long l3 = 0x180000L;
		// if(0x180000L + mDownloadSize > l)
		// l3 = l - mDownloadSize;
		// inputstream = getPackage(mDownloadSize, l3);
		// if(inputstream != null)
		// {
		// if(mIsCanceled)
		// inputstream.close();
		// }
		// }
		// else
		// {
		// if(mDownloadSize < l)
		// {
		// if(mIsCanceled)
		// {
		// logger.v("getUpdateFile() ---> Exit");
		// downloaditem = mItem;
		// byte abyte0[];
		// long l4;
		// abyte0 = new byte[0x10000];
		// l4 = (new Date()).getTime();
		// }
		// else
		// {
		// logger.e((new
		// StringBuilder("Download size: ")).append(mDownloadSize).append(" / File size: ").append(l).toString());
		// throw new SocketException();
		// return downloaditem;
		// }
		// }
		// else
		// {
		// file.renameTo(new File(mItem.getFilePath()));
		// mErrCode = 0;
		// if(mListener != null)
		// mListener.taskCompleted(this);
		// }
		// }
		// }
		// else
		// {
		// DefaultHttpClient defaulthttpclient;
		// logger.e("Try to get file length");
		// defaulthttpclient = Util.createNetworkClient(true);
		// if(defaulthttpclient != null)
		// {
		// HttpGet httpget = new HttpGet(mItem.getUrl());
		// HttpResponse httpresponse = defaulthttpclient.execute(httpget);
		// int j = httpresponse.getStatusLine().getStatusCode();
		// logger.e((new StringBuilder("HTTP retCode: ")).append(j).toString());
		// if(j <= 0)
		// throw new IOException();
		// if(j != 200)
		// throw new FileNotFoundException();
		// long l6 = httpresponse.getEntity().getContentLength();
		// String s;
		// String s1;
		// long l1;
		// long l2;
		// if(l6 > 0L)
		// {
		// logger.d((new
		// StringBuilder("Get the file length is: ")).append(l6).toString());
		// mItem.setFileSize(l6);
		// } else
		// {
		// logger.e("Can not get the file length!");
		// }
		// }
		// else
		// {
		// if(mListener != null && NetUtil.isNetStateWap())
		// mListener.taskCmWapClosed(this);
		// downloaditem = mItem;
		// }
		// }
		//
		// int i = inputstream.read(abyte0);
		// if(i != -1 && !mIsCanceled)
		// {
		// mRandFile = new RandomAccessFile(file, "rw");
		// mRandFile.seek(mDownloadSize);
		// mRandFile.write(abyte0, 0, i);
		// mRandFile.close();
		// mDownloadSize = mDownloadSize + (long)i;
		// mItem.setDownloadSize(mDownloadSize);
		// logger.v((new
		// StringBuilder("Read and Write ")).append(i).append(" bytes").toString());
		// long l5 = (new Date()).getTime();
		// if(mListener != null && l5 - l4 > 500L)
		// {
		// l4 = (new Date()).getTime();
		// mListener.taskProgress(this, mDownloadSize, l);
		// }
		// goto _L12
		// }
		// else
		// {
		// inputstream.close();
		// }
	}

	public void cancelTask()
	{
		if (this.mListener != null)
			this.mListener.taskCanceled(this);
		this.mIsCanceled = true;
	}

	public DownloadItem getDownloadItem()
	{
		return this.mItem;
	}

	public HttpEntity getDownloadResponse(long l, long l1, boolean flag)
			throws Exception
	{
		return null;
	}

	public int getErrCode()
	{
		return this.mErrCode;
	}

	public DownloadItem getMediaFile() throws Exception
	{
		return null;
	}

	public InputStream getPackage(long l, long l1) throws Exception
	{
		logger.v("getPackage() ---> Enter");
		DefaultHttpClient defaulthttpclient = Util.createNetworkClient(true);
		InputStream inputstream;
		if (defaulthttpclient == null)
		{
			if (mListener != null && NetUtil.isNetStateWap())
				mListener.taskCmWapClosed(this);
			inputstream = null;
		} else
		{
			HttpGet httpget = new HttpGet(mItem.getUrl());
			long l2 = l + l1;
			httpget.setHeader("RANGE", (new StringBuilder("bytes=")).append(l)
					.append("-").append(l2).toString());
			HttpResponse httpresponse = defaulthttpclient.execute(httpget);
			int i = httpresponse.getStatusLine().getStatusCode();
			logger.e((new StringBuilder("HTTP retCode: ")).append(i).toString());
			if (i <= 0)
				throw new IOException();
			if (i != 200 && i != 206)
				throw new FileNotFoundException();
			inputstream = httpresponse.getEntity().getContent();
			logger.v("getPackage() ---> Exit");
		}
		return inputstream;
	}

	public int getTransId()
	{
		return this.mTransId;
	}

	public boolean getWaitOrNot()
	{
		return this.mWaitOrNot;
	}

	public boolean isCanceled()
	{
		return this.mIsCanceled;
	}

	public void run()
	{
		try
		{
			if ((this.mItem.getContentType() == -300)
					|| (this.mItem.getContentType() == -400))
				getUpdateFile();
			else
				getMediaFile();
		} catch (Exception localException)
		{
			if (((localException instanceof ConnectTimeoutException))
					|| ((localException instanceof SocketTimeoutException))
					|| ((localException instanceof SocketException)))
			{
				logger.e("DownloadTask fail: ", localException);
				this.mErrCode = -3;
			}
			if (this.mListener != null)
				this.mListener.taskFailed(this, localException);
		}
	}

	public void setDownloadItem(DownloadItem paramDownloadItem)
	{
		this.mItem = paramDownloadItem;
	}

	public void setDownloadListener(DLTaskListener paramDLTaskListener)
	{
		this.mListener = paramDLTaskListener;
	}

	public void setErrCode(int paramInt)
	{
		this.mErrCode = paramInt;
	}

	public void setTransId(int paramInt)
	{
		this.mTransId = paramInt;
	}

	public void setWaitOrNot(boolean paramBoolean)
	{
		this.mWaitOrNot = paramBoolean;
	}
}