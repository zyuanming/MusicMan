package org.ming.center.download;

import java.util.Date;

public class DownloadItem implements Comparable
{
	public static final int COLUMN_INDEX_ARTIST = 16;
	public static final int COLUMN_INDEX_CONTENT_ID = 14;
	public static final int COLUMN_INDEX_CONTENT_TYPE = 13;
	public static final int COLUMN_INDEX_DOWNLOAD_SIZE = 9;
	public static final int COLUMN_INDEX_FILE_NAME = 6;
	public static final int COLUMN_INDEX_FILE_PATH = 5;
	public static final int COLUMN_INDEX_FILE_SIZE = 8;
	public static final int COLUMN_INDEX_GROUP_CODE = 15;
	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_NETWORKTYPE = 17;
	public static final int COLUMN_INDEX_PROXY_HOST = 11;
	public static final int COLUMN_INDEX_PROXY_PORT = 12;
	public static final int COLUMN_INDEX_SHOW_NAME = 7;
	public static final int COLUMN_INDEX_SIZE_FROM_START = 10;
	public static final int COLUMN_INDEX_STATUS = 1;
	public static final int COLUMN_INDEX_TIMESTARTDL = 4;
	public static final int COLUMN_INDEX_TIMESTEP = 3;
	public static final int COLUMN_INDEX_URL = 2;
	private String mArtist = null;
	private int mContentType;
	private String mContentid;
	private long mDownloadSize;
	private String mFileName;
	private String mFilePath;
	private long mFileSize;
	private String mGroupCode;
	private boolean mIsBatchItem = false;
	private long mItemId;
	private int mNetworkType = 3;
	private String mProxyHost = null;
	private int mProxyPort = -1;
	private String mShowName;
	private long mSizeFromStart;
	private int mStatus;
	private long mStatusMdfTime;
	private long mTimeFromStart;
	private String mUrl;

	public DownloadItem(long paramLong1, int paramInt1, String paramString1,
			long paramLong2, long paramLong3, String paramString2,
			String paramString3, String paramString4, long paramLong4,
			long paramLong5, long paramLong6, String paramString5,
			int paramInt2, int paramInt3, String paramString6,
			String paramString7, String paramString8, int paramInt4)
	{
		this.mItemId = paramLong1;
		this.mStatus = paramInt1;
		this.mUrl = paramString1;
		this.mStatusMdfTime = paramLong2;
		this.mTimeFromStart = paramLong3;
		this.mFilePath = paramString2;
		if (this.mFilePath != null)
			this.mFilePath.replace('/', '-');
		this.mFileName = paramString3;
		if (this.mFileName != null)
			this.mFileName.replace('/', '-');
		this.mShowName = paramString4;
		this.mFileSize = paramLong4;
		this.mDownloadSize = paramLong5;
		this.mSizeFromStart = paramLong6;
		this.mProxyHost = paramString5;
		this.mProxyPort = paramInt2;
		this.mContentType = paramInt3;
		this.mContentid = paramString6;
		this.mGroupCode = paramString7;
		this.mArtist = paramString8;
		this.mNetworkType = paramInt4;
	}

	public int compareTo(Object paramObject)
	{
		int i = -1;
		if (paramObject == null)
		{
			return i;
		} else
		{
			long l = ((DownloadItem) paramObject).mStatusMdfTime;
			if (getStatus() == COLUMN_INDEX_TIMESTARTDL)
			{
				if (this.mStatusMdfTime < l)
					i = 1;
			} else
			{
				i = 0;
			}
			return i;
		}
	}

	public String getArtist()
	{
		return this.mArtist;
	}

	public String getContentId()
	{
		return this.mContentid;
	}

	public int getContentType()
	{
		return this.mContentType;
	}

	public long getDownloadSize()
	{
		return this.mDownloadSize;
	}

	public String getFileName()
	{
		return this.mFileName;
	}

	public String getFilePath()
	{
		return this.mFilePath;
	}

	public long getFileSize()
	{
		return this.mFileSize;
	}

	public String getGroupCode()
	{
		return this.mGroupCode;
	}

	public long getItemId()
	{
		return this.mItemId;
	}

	public int getNetworkType()
	{
		return this.mNetworkType;
	}

	public String getProxyHost()
	{
		return this.mProxyHost;
	}

	public int getProxyPort()
	{
		return this.mProxyPort;
	}

	public String getShowName()
	{
		return this.mShowName;
	}

	public long getSizeFromStart()
	{
		return this.mSizeFromStart;
	}

	public int getStatus()
	{
		return this.mStatus;
	}

	public long getTimeStartDL()
	{
		return this.mTimeFromStart;
	}

	public long getTimeStep()
	{
		return this.mStatusMdfTime;
	}

	public String getUrl()
	{
		return this.mUrl;
	}

	public boolean isBatchItem()
	{
		return this.mIsBatchItem;
	}

	public void setArtist(String paramString)
	{
		this.mArtist = paramString;
	}

	public void setDownloadSize(long paramLong)
	{
		this.mDownloadSize = paramLong;
	}

	public void setFileName(String paramString)
	{
		this.mFileName = paramString;
	}

	public void setFilePath(String paramString)
	{
		this.mFilePath = paramString;
	}

	public void setFileSize(long paramLong)
	{
		this.mFileSize = paramLong;
	}

	public void setIsBatchItem(boolean paramBoolean)
	{
		this.mIsBatchItem = paramBoolean;
	}

	public void setItemId(long paramLong)
	{
		this.mItemId = paramLong;
	}

	public void setShowName(String paramString)
	{
		this.mShowName = paramString;
	}

	public void setSizeFromStart(long paramLong)
	{
		this.mSizeFromStart = paramLong;
	}

	public void setStatus(int paramInt)
	{
		this.mStatus = paramInt;
		this.mStatusMdfTime = new Date().getTime();
	}

	public void setTimeStartDL(long paramLong)
	{
		this.mTimeFromStart = paramLong;
	}

	public String toString()
	{
		return this.mShowName;
	}
}
