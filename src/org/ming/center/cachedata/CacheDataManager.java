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

	private CacheDataManager()
	{
		mApp = MobileMusicApplication.getInstance();
		mDBController = mApp.getController().getDBController();
	}

	public String getCacheData(String paramString)
	{
		return this.mDBController.queryCacheData(paramString);
	}

	public boolean isInCacheData(String paramString)
	{
		return this.mDBController.isCacheDataExist(paramString);
	}

	/**
	 * 保存缓存数据在数据库中
	 * 
	 * @param content_key
	 * @param group_code
	 * @param time_stamp
	 * @param data
	 * @return
	 */
	public long saveCacheData(String content_key, String group_code,
			String time_stamp, String data)
	{
		long l = 0;
		try
		{
			l = this.mDBController.addCacheData(content_key, group_code,
					time_stamp, data);
			return l;
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return l;
	}
}
