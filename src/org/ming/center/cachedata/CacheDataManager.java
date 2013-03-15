package org.ming.center.cachedata;

import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;

public class CacheDataManager
{
	private static CacheDataManager sInstance = null;
	private MobileMusicApplication mApp = null;
	private DBController mDBController = null;

	public static CacheDataManager getInstance()
	{
		if (sInstance == null)
			sInstance = new CacheDataManager();
		return sInstance;
	}

	public String getCacheData(String paramString)
	{
		return this.mDBController.queryCacheData(paramString);
	}

	public boolean isInCacheData(String paramString)
	{
		return this.mDBController.isCacheDataExist(paramString);
	}

	public long saveCacheData(String paramString1, String paramString2,
			String paramString3, String paramString4)
	{
		long l = 0;
		try
		{
			l = this.mDBController.addCacheData(paramString1, paramString2,
					paramString3, paramString4);
			return l;
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return l;
	}
}
