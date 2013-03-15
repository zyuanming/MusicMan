package org.ming.center.weibo;

import android.provider.BaseColumns;

public class AccessInfoColumn implements BaseColumns
{
	public static final String ACCESS_EXPIRESIN = "ACCESS_EXPIRESIN";
	public static final int ACCESS_EXPIRESIN_COLUMN = 4;
	public static final String ACCESS_OVERDUETIME = "ACCESS_OVERDUETIME";
	public static final int ACCESS_OVERDUETIME_COLUMN = 6;
	public static final String ACCESS_REFRESHTOKEN = "ACCESS_REFRESHTOKEN";
	public static final int ACCESS_REFRESHTOKEN_COLUMN = 5;
	public static final String ACCESS_SECRET = "ACCESS_SECRET";
	public static final int ACCESS_SECRET_COLUMN = 3;
	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	public static final int ACCESS_TOKEN_COLUMN = 2;
	public static final String[] PROJECTION = { "_id", "USERID",
			"ACCESS_TOKEN", "ACCESS_SECRET", "ACCESS_EXPIRESIN",
			"ACCESS_REFRESHTOKEN", "ACCESS_OVERDUETIME" };
	public static final String USERID = "USERID";
	public static final int USERID_COLUMN = 1;
	// public static final int _ID_ACCESS;
}
