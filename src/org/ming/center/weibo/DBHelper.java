package org.ming.center.weibo;

import org.ming.center.GlobalSettingParameter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	public static final String ACCESSLIB_TABLE = "accessinfo";
	private static final String CREATE_ACCESSINFO_LIB = "CREATE TABLE accessinfo (_id integer primary key autoincrement,USERID text,ACCESS_TOKEN text,ACCESS_SECRET text,ACCESS_EXPIRESIN text,ACCESS_REFRESHTOKEN text,ACCESS_OVERDUETIME text)";
	public static final String DATABASE_NAME = GlobalSettingParameter.WEIBO_DB_NAME;
	public static final int DATABASE_VERSION = 3;

	public DBHelper(Context paramContext)
	{
		super(paramContext, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase paramSQLiteDatabase)
	{
		paramSQLiteDatabase.execSQL(CREATE_ACCESSINFO_LIB);
	}

	public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1,
			int paramInt2)
	{
		paramSQLiteDatabase.execSQL("drop table  accessinfo");
		paramSQLiteDatabase.execSQL(CREATE_ACCESSINFO_LIB);
	}
}