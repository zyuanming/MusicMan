package org.ming.center.weibo;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccessInfoHelper
{
	private Context context;
	private DBHelper dbHelper;
	private SQLiteDatabase newsDB;

	public AccessInfoHelper(Context paramContext)
	{
		this.context = paramContext;
	}

	public void close()
	{
		if (this.dbHelper != null)
			this.dbHelper.close();
	}

	public long create(AccessInfo paramAccessInfo)
	{
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("USERID", paramAccessInfo.getUserID());
		localContentValues
				.put("ACCESS_TOKEN", paramAccessInfo.getAccessToken());
		localContentValues.put("ACCESS_SECRET",
				paramAccessInfo.getAccessSecret());
		localContentValues.put("ACCESS_EXPIRESIN",
				paramAccessInfo.getExpiresIn());
		localContentValues.put("ACCESS_REFRESHTOKEN",
				paramAccessInfo.getRefreshToken());
		localContentValues.put("ACCESS_OVERDUETIME",
				paramAccessInfo.getOverdueTime());
		return this.newsDB.insert("accessinfo", null, localContentValues);
	}

	public boolean delete(AccessInfo accessinfo)
	{
		boolean flag = true;
		if (accessinfo != null)
		{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("ACCESS_TOKEN", accessinfo.getAccessToken());
			contentvalues.put("ACCESS_EXPIRESIN", accessinfo.getExpiresIn());
			contentvalues.put("ACCESS_SECRET", accessinfo.getAccessSecret());
			SQLiteDatabase sqlitedatabase = newsDB;
			String as[] = new String[3];
			as[0] = accessinfo.getAccessToken();
			as[1] = accessinfo.getExpiresIn();
			as[2] = accessinfo.getAccessSecret();
			if (sqlitedatabase
					.delete("accessinfo",
							"ACCESS_TOKEN= ?   and ACCESS_EXPIRESIN= ?  and  ACCESS_SECRET= ? ",
							as) <= 0)
				flag = false;
		} else
		{
			flag = false;
		}
		return flag;
	}

	public boolean deleteAll()
	{
		boolean flag;
		if (newsDB.delete("accessinfo", null, null) > 0)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public AccessInfo getAccessInfo(String s)
	{
		String s1 = (new StringBuilder("USERID=")).append(s).toString();
		Cursor cursor = newsDB.query("accessinfo", AccessInfoColumn.PROJECTION,
				s1, null, null, null, null);
		AccessInfo accessinfo = null;
		if (cursor != null)
		{
			int i = cursor.getCount();
			accessinfo = null;
			if (i > 0)
			{
				cursor.moveToFirst();
				accessinfo = new AccessInfo();
				accessinfo.setUserID(cursor.getString(1));
				accessinfo.setAccessToken(cursor.getString(2));
				accessinfo.setAccessSecret(cursor.getString(3));
				accessinfo.setExpiresIn(cursor.getString(4));
				accessinfo.setRefreshToken(cursor.getString(5));
				accessinfo.setOverdueTime(cursor.getString(6));
			}
		}
		return accessinfo;
	}

	public ArrayList getAccessInfos()
	{
		ArrayList arraylist;
		Cursor cursor;
		arraylist = new ArrayList();
		cursor = newsDB.query("accessinfo", AccessInfoColumn.PROJECTION, null,
				null, null, null, null);
		if (cursor.getCount() <= 0)
		{
			cursor.close();
			return arraylist;
		} else
		{
			cursor.moveToFirst();
			while (true)
			{
				if (!cursor.isAfterLast())
				{
					AccessInfo accessinfo = new AccessInfo();
					accessinfo.setUserID(cursor.getString(1));
					accessinfo.setAccessToken(cursor.getString(2));
					accessinfo.setAccessSecret(cursor.getString(3));
					accessinfo.setExpiresIn(cursor.getString(4));
					accessinfo.setRefreshToken(cursor.getString(5));
					accessinfo.setOverdueTime(cursor.getString(6));
					arraylist.add(accessinfo);
					cursor.moveToNext();
				} else
				{
					cursor.close();
					return arraylist;
				}
			}
		}
	}

	public AccessInfoHelper open()
	{
		this.dbHelper = new DBHelper(this.context);
		this.newsDB = this.dbHelper.getWritableDatabase();
		return this;
	}

	public boolean update(AccessInfo accessinfo)
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("USERID", accessinfo.getUserID());
		contentvalues.put("ACCESS_TOKEN", accessinfo.getAccessToken());
		contentvalues.put("ACCESS_SECRET", accessinfo.getAccessSecret());
		contentvalues.put("ACCESS_EXPIRESIN", accessinfo.getExpiresIn());
		contentvalues.put("ACCESS_REFRESHTOKEN", accessinfo.getRefreshToken());
		contentvalues.put("ACCESS_OVERDUETIME", accessinfo.getOverdueTime());
		String s = (new StringBuilder("USERID="))
				.append(accessinfo.getUserID()).toString();
		boolean flag;
		if (newsDB.update("accessinfo", contentvalues, s, null) > 0)
			flag = true;
		else
			flag = false;
		return flag;
	}
}