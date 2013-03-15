package org.ming.center.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ming.center.ConfigSettingParameter;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.download.DownloadItem;
import org.ming.center.http.item.SongListItem;
import org.ming.util.MyLogger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Pair;

public class DBControllerImpl implements DBController
{
	private static final String CACHEDATA_COLUMNS[] = { "CONTENT_KEY",
			"GROUP_CODE", "TIME_STAMP", "DATA" };
	public static final String CHECK_OLDERVERSION = "cmccwm.mobilemusic.database.checkolderversion";
	private static final String CONTENTID_COLUMNS[] = { "_id", "filepath",
			"contentid" };
	private static final String CREATE_APPINFO = "create table T_APPINFO( CHANNELID text not null,SUBCHANNELID text not null)";
	private static final String CREATE_CACHEDATA = "create table T_CACHEDATA( CONTENT_KEY text primary key, GROUP_CODE text not null, TIME_STAMP text, DATA text not null)";
	private static final String CREATE_CONTENTID_MAP = "create table cotentidmap( _id integer primary key autoincrement, filepath text not null, contentid text not null)";
	private static final String CREATE_DOWNLOAD = "create table downloadlist( _id integer primary key autoincrement, status integer not null, url text not null, timestep integer not null, timestartdl integer not null, filepath text not null, filename text not null, showname text not null, filesize integer not null, downloadsize interger not null, sizefromstart interger not null, proxyHost text not null, proxyPort integer not null, contentType interger not null, contentid text not null, groupcode text not null, networktype integer not null, user_id integer not null, artist text not null)";
	private static final String CREATE_LOCAL_MUSIC_PLAYLIST_MAP = "create table local_music_playlist_map ( _id integer primary key autoincrement, audio_id INTEGER NOT NULL, playlist_id INTEGER NOT NULL)";
	private static final String CREATE_LOCAL_MUSIC_PLAYLIST_TABLE = "create table local_music_playlist ( _id integer primary key autoincrement, name text not null, _data text not null, date_added long not null, date_modified long not null)";
	private static final String CREATE_MUSIC_PLAYLIST_MAP = "create table music_playlist_map ( _id integer primary key autoincrement, audio_id INTEGER NOT NULL, playlist_id INTEGER NOT NULL,ISONLINEMUSIC INTEGER NOT NULL)";
	private static final String CREATE_MUSIC_PLAYLIST_TABLE = "create table music_playlist ( _id integer primary key autoincrement, name text not null, _data text not null, date_added long not null, date_modified long not null)";
	private static final String CREATE_ONLINE_MUSIC_PLAYLIST_MAP = "create table onine_music_playlist_map ( _id integer primary key autoincrement, audio_id INTEGER NOT NULL, playlist_id INTEGER NOT NULL)";
	private static final String CREATE_ONLINE_MUSIC_PLAYLIST_TABLE = "create table online_music_playlist ( _id integer primary key autoincrement, name text not null, _data text not null, date_added long not null, date_modified long not null, user_id integer not null)";
	private static final String CREATE_ONLINE_MUSIC_TABLE = "create table online_music_audio_info ( _id integer primary key autoincrement, _data text, album text, album_id integer , artist text not null, date_added long not null, title text not null, duration long, _size long, contentid text not null, groupcode text , point integer, img text, user_id integer not null, url1 text ,url2 text,url3 text, filesize2 text, filesize3 text,isdolby integer not null)";
	private static final String CREATE_RADIOGARBAGE = "create table radiogarbagelist( _id integer primary key autoincrement, user_id integer not null, contentid integer not null, groupconde integer not null)";
	private static final String CREATE_RATETABLE = "create table ratelist( _id integer primary key autoincrement, user_id integer not null, contentid integer not null, point integer not null)";
	private static final String CREATE_USER = "create table user( user_id integer primary key autoincrement, mdn integer not null, username text, password text, login_time date)";
	public static final int DATABASE_VERSION = 7;
	public static final String DB_NAME = "mobile_music";
	private static final String DOWNLOAD_COLUMNS[] = { "_id", "status", "url",
			"timestep", "timestartdl", "filepath", "filename", "showname",
			"filesize", "downloadsize", "sizefromstart", "proxyHost",
			"proxyPort", "contentType", "contentid", "groupcode", "artist",
			"networktype", "user_id" };
	private static final String DROP_APPINFO = "drop TABLE T_APPINFO";
	private static final String DROP_CACHEDATA = "drop TABLE T_CACHEDATA";
	private static final String DROP_CONTENTID_MAP = "drop TABLE cotentidmap";
	private static final String DROP_DOWNLOAD = "drop TABLE downloadlist";
	private static final String DROP_LOCAL_MUSIC_PLAYLIST_MAP = "drop TABLE local_music_playlist_map";
	private static final String DROP_LOCAL_MUSIC_PLAYLIST_TABLE = "drop TABLE local_music_playlist";
	private static final String DROP_MUSIC_PLAYLIST_MAP = "drop TABLE music_playlist_map";
	private static final String DROP_MUSIC_PLAYLIST_TABLE = "drop TABLE music_playlist";
	private static final String DROP_ONLINE_MUSIC_PLAYLIST_MAP = "drop TABLE onine_music_playlist_map";
	private static final String DROP_ONLINE_MUSIC_PLAYLIST_TABLE = "drop TABLE online_music_playlist";
	private static final String DROP_ONLINE_MUSIC_TABLE = "drop TABLE online_music_audio_info";
	private static final String DROP_RADIOGARBAGE = "drop TABLE radiogarbagelist";
	private static final String DROP_RATETABLE = "drop TABLE ratelist";
	private static final String DROP_USER = "drop TABLE user";
	public static final int FLAG_OPERATE_LOCAL = 1;
	public static final int FLAG_OPERATE_MIX = 2;
	public static final int FLAG_OPERATE_ONLINE = 0;
	public static final int LOCAL_MUSIC_SCAN_MIN_TIME = 10000;
	private static final String ONLINE_MUSIC_COLUMNS_CONTENT_ID = "contentid";
	private static final String ONLINE_MUSIC_COLUMNS_GROUP_CODE = "groupcode";
	public static final String PREFS_51CH_STATUS = "cmccwm.mobilemusic.database.51chstatus";
	public static final String PREFS_DOWNLOAD_AUTO_RECOVER = "cmccwm.mobilemusic.database.download_auto_recovery";
	public static final String PREFS_EQ_MODE = "cmccwm.mobilemusic.database.eqmode";
	public static final String PREFS_LOCALMUISC_FOLDERNAME = "cmccwm.mobilemusic.database.foldername";
	public static final String PREFS_LOCALMUISC_SCANSMALLFILE = "cmccwm.mobilemusic.database.scansmallfile";
	public static final String PREFS_NAME = "cmccwm.mobilemusic.database.peference";
	public static final String PREFS_REPEAT_MODE = "cmccwm.mobilemusic.database.repeatmode";
	public static final String PREFS_SHUFFLE_MODE = "cmccwm.mobilemusic.database.shufflemode";
	public static final String PREFS_SKIN_STYLE_NAME = "cmccwm.mobilemusic.database.skinstylename";
	public static final String PREFS_TENSILE_SHOWS = "cmccwm.mobilemusic.database.usermore_tensile_shows";
	private static final String RADIOGARBAGE_COLUMNS[] = { "user_id",
			"contentid", "groupconde" };
	private static final String RATING_COLUMNS[] = { "user_id", "contentid",
			"point" };
	private static final String TABLE_APPINFO = "T_APPINFO";
	private static final String TABLE_CACHEDATA = "T_CACHEDATA";
	private static final String TABLE_CONTENTID_MAP = "cotentidmap";
	private static final String TABLE_DOWNLOAD = "downloadlist";
	private static final String TABLE_LOCAL_MUSIC_PLAYLIST = "local_music_playlist";
	private static final String TABLE_LOCAL_MUSIC_PLAYLIST_MAP = "local_music_playlist_map";
	private static final String TABLE_MUSIC_PLAYLIST = "music_playlist";
	private static final String TABLE_MUSIC_PLAYLIST_MAP = "music_playlist_map";
	private static final String TABLE_ONLINE_MUSIC = "online_music_audio_info";
	private static final String TABLE_ONLINE_MUSIC_PLAYLIST = "online_music_playlist";
	private static final String TABLE_ONLINE_MUSIC_PLAYLIST_MAP = "onine_music_playlist_map";
	private static final String TABLE_RADIOGARBAGE = "radiogarbagelist";
	private static final String TABLE_RATE = "ratelist";
	private static final String TABLE_USER = "user";
	private static final String USER_COLUMNS[] = { "user_id", "mdn",
			"username", "password", "login_time" };
	private static final MyLogger logger = MyLogger
			.getLogger("DBControllerImpl");
	private static DBControllerImpl sInstance = null;
	private MobileMusicApplication mApp;
	private SQLiteDatabase mDb;
	private DatabaseHelper mOpenHelper;

	private DBControllerImpl(MobileMusicApplication mobilemusicapplication)
	{
		mApp = null;
		mDb = null;
		mApp = mobilemusicapplication;
		mOpenHelper = new DatabaseHelper(mobilemusicapplication);
		mDb = mOpenHelper.getWritableDatabase();
	}

	public static DBControllerImpl getInstance(
			MobileMusicApplication mobilemusicapplication)
	{
		if (sInstance == null)
			sInstance = new DBControllerImpl(mobilemusicapplication);
		return sInstance;
	}

	private class DatabaseHelper extends SQLiteOpenHelper
	{

		final DBControllerImpl dbControllerImpl;

		DatabaseHelper(Context context)
		{
			super(context, "mobile_music", null, 7);
			dbControllerImpl = DBControllerImpl.this;
		}

		public void onCreate(SQLiteDatabase sqlitedatabase)
		{
			DBControllerImpl.logger.v("DatabaseHelper.onCreate() ---> Enter");
			// 创建必要的表
			sqlitedatabase
					.execSQL("create table downloadlist( _id integer primary key autoincrement, status integer not null, url text not null, timestep integer not null, timestartdl integer not null, filepath text not null, filename text not null, showname text not null, filesize integer not null, downloadsize interger not null, sizefromstart interger not null, proxyHost text not null, proxyPort integer not null, contentType interger not null, contentid text not null, groupcode text not null, networktype integer not null, user_id integer not null, artist text not null)");
			sqlitedatabase
					.execSQL("create table cotentidmap( _id integer primary key autoincrement, filepath text not null, contentid text not null)");
			sqlitedatabase
					.execSQL("create table online_music_audio_info ( _id integer primary key autoincrement, _data text, album text, album_id integer , artist text not null, date_added long not null, title text not null, duration long, _size long, contentid text not null, groupcode text , point integer, img text, user_id integer not null, url1 text ,url2 text,url3 text, filesize2 text, filesize3 text,isdolby integer not null)");
			sqlitedatabase
					.execSQL("create table online_music_playlist ( _id integer primary key autoincrement, name text not null, _data text not null, date_added long not null, date_modified long not null, user_id integer not null)");
			sqlitedatabase
					.execSQL("create table onine_music_playlist_map ( _id integer primary key autoincrement, audio_id INTEGER NOT NULL, playlist_id INTEGER NOT NULL)");
			sqlitedatabase
					.execSQL("create table local_music_playlist ( _id integer primary key autoincrement, name text not null, _data text not null, date_added long not null, date_modified long not null)");
			sqlitedatabase
					.execSQL("create table local_music_playlist_map ( _id integer primary key autoincrement, audio_id INTEGER NOT NULL, playlist_id INTEGER NOT NULL)");
			sqlitedatabase
					.execSQL("create table music_playlist ( _id integer primary key autoincrement, name text not null, _data text not null, date_added long not null, date_modified long not null)");
			sqlitedatabase
					.execSQL("create table music_playlist_map ( _id integer primary key autoincrement, audio_id INTEGER NOT NULL, playlist_id INTEGER NOT NULL,ISONLINEMUSIC INTEGER NOT NULL)");
			sqlitedatabase
					.execSQL("create table T_CACHEDATA( CONTENT_KEY text primary key, GROUP_CODE text not null, TIME_STAMP text, DATA text not null)");
			sqlitedatabase
					.execSQL("create table user( user_id integer primary key autoincrement, mdn integer not null, username text, password text, login_time date)");
			sqlitedatabase
					.execSQL("create table ratelist( _id integer primary key autoincrement, user_id integer not null, contentid integer not null, point integer not null)");
			sqlitedatabase
					.execSQL("create table radiogarbagelist( _id integer primary key autoincrement, user_id integer not null, contentid integer not null, groupconde integer not null)");
			sqlitedatabase.execSQL(CREATE_APPINFO);
			mDb = sqlitedatabase;
			writeChannelId(ConfigSettingParameter.CONSTANT_CHANNEL_VALUE,
					ConfigSettingParameter.CONSTANT_SUBCHANNEL_VALUE);
			createPlaylist(
					"cmccwm.mobilemusic.database.default.local.playlist.recent.download",
					1);
			createPlaylist(
					"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
					2);
			DBControllerImpl.logger.v("DatabaseHelper.onCreate() ---> Exit");
		}

		public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j)
		{
			DBControllerImpl.logger.v("DatabaseHelper.onUpgrade() ---> Enter");
			if (i < 7)
			{
				try
				{
					sqlitedatabase.execSQL("drop TABLE downloadlist");
					sqlitedatabase.execSQL("drop TABLE cotentidmap");
					sqlitedatabase
							.execSQL("drop TABLE online_music_audio_info");
					sqlitedatabase.execSQL("drop TABLE online_music_playlist");
					sqlitedatabase
							.execSQL("drop TABLE onine_music_playlist_map");
					sqlitedatabase.execSQL("drop TABLE local_music_playlist");
					sqlitedatabase
							.execSQL("drop TABLE local_music_playlist_map");
				} catch (SQLException sqlexception)
				{
					DBControllerImpl.logger.e("Fail to remove table!!");
				}
				try
				{
					sqlitedatabase.execSQL("drop TABLE music_playlist");
					sqlitedatabase.execSQL("drop TABLE music_playlist_map");
					sqlitedatabase.execSQL("drop TABLE T_CACHEDATA");
					sqlitedatabase.execSQL("drop TABLE user");
					sqlitedatabase.execSQL("drop TABLE ratelist");
					sqlitedatabase.execSQL("drop TABLE radiogarbagelist");
					sqlitedatabase.execSQL("drop TABLE T_APPINFO");
				} catch (SQLException sqlexception1)
				{
					DBControllerImpl.logger.e("Fail to remove table!!");
				}
				sqlitedatabase
						.execSQL("create table downloadlist( _id integer primary key autoincrement, status integer not null, url text not null, timestep integer not null, timestartdl integer not null, filepath text not null, filename text not null, showname text not null, filesize integer not null, downloadsize interger not null, sizefromstart interger not null, proxyHost text not null, proxyPort integer not null, contentType interger not null, contentid text not null, groupcode text not null, networktype integer not null, user_id integer not null, artist text not null)");
				sqlitedatabase
						.execSQL("create table cotentidmap( _id integer primary key autoincrement, filepath text not null, contentid text not null)");
				sqlitedatabase
						.execSQL("create table online_music_audio_info ( _id integer primary key autoincrement, _data text, album text, album_id integer , artist text not null, date_added long not null, title text not null, duration long, _size long, contentid text not null, groupcode text , point integer, img text, user_id integer not null, url1 text ,url2 text,url3 text, filesize2 text, filesize3 text,isdolby integer not null)");
				sqlitedatabase
						.execSQL("create table online_music_playlist ( _id integer primary key autoincrement, name text not null, _data text not null, date_added long not null, date_modified long not null, user_id integer not null)");
				sqlitedatabase
						.execSQL("create table onine_music_playlist_map ( _id integer primary key autoincrement, audio_id INTEGER NOT NULL, playlist_id INTEGER NOT NULL)");
				sqlitedatabase
						.execSQL("create table local_music_playlist ( _id integer primary key autoincrement, name text not null, _data text not null, date_added long not null, date_modified long not null)");
				sqlitedatabase
						.execSQL("create table local_music_playlist_map ( _id integer primary key autoincrement, audio_id INTEGER NOT NULL, playlist_id INTEGER NOT NULL)");
				sqlitedatabase
						.execSQL("create table music_playlist ( _id integer primary key autoincrement, name text not null, _data text not null, date_added long not null, date_modified long not null)");
				sqlitedatabase
						.execSQL("create table music_playlist_map ( _id integer primary key autoincrement, audio_id INTEGER NOT NULL, playlist_id INTEGER NOT NULL,ISONLINEMUSIC INTEGER NOT NULL)");
				sqlitedatabase
						.execSQL("create table T_CACHEDATA( CONTENT_KEY text primary key, GROUP_CODE text not null, TIME_STAMP text, DATA text not null)");
				sqlitedatabase
						.execSQL("create table user( user_id integer primary key autoincrement, mdn integer not null, username text, password text, login_time date)");
				sqlitedatabase
						.execSQL("create table ratelist( _id integer primary key autoincrement, user_id integer not null, contentid integer not null, point integer not null)");
				sqlitedatabase
						.execSQL("create table radiogarbagelist( _id integer primary key autoincrement, user_id integer not null, contentid integer not null, groupconde integer not null)");
				if (i < 7)
				{
					sqlitedatabase.execSQL("drop TABLE T_APPINFO");
					sqlitedatabase
							.execSQL("create table T_APPINFO( CHANNELID text not null,SUBCHANNELID text not null)");
					mDb = sqlitedatabase;
					writeChannelId(
							ConfigSettingParameter.CONSTANT_CHANNEL_VALUE,
							ConfigSettingParameter.CONSTANT_SUBCHANNEL_VALUE);
				} else
				{
					mDb = sqlitedatabase;
				}
				createPlaylist(
						"cmccwm.mobilemusic.database.default.local.playlist.recent.download",
						1);
				createPlaylist(
						"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
						2);
			}
			DBControllerImpl.logger.v("DatabaseHelper.onUpgrade() ---> Exit");
		}
	}

	public long writeChannelId(String s, String s1)
	{
		logger.v("writeChannelId() ---> Enter");
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("CHANNELID", s);
		contentvalues.put("SUBCHANNELID", s1);
		logger.v("writeChannelId() ---> Exit");
		return mDb.insert("T_APPINFO", null, contentvalues);
	}

	@Override
	public long addCacheData(String s, String s1, String s2, String s3)
	{
		if (s == null)
			throw new NullPointerException("null downloadItem");
		String s4 = s.replaceAll("'", "'");
		long l;
		if (isCacheDataExist(s4))
		{
			l = updateCacheData(s4, s2, s3);
		} else
		{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(CACHEDATA_COLUMNS[0], s4);
			if (s1 == null || "".equals(s1.trim()))
				s1 = "-1";
			contentvalues.put(CACHEDATA_COLUMNS[1], s1);
			if (s2 != null)
				contentvalues.put(CACHEDATA_COLUMNS[2], s2);
			contentvalues.put(CACHEDATA_COLUMNS[3], s3);
			l = mDb.insert("T_CACHEDATA", null, contentvalues);
		}
		return l;
	}

	@Override
	public long addContentId(String s, String s1)
	{
		if (s == null)
			throw new NullPointerException("null downloadItem");
		String s2 = s.replaceAll("'", "''");
		long l;
		if (isContentIdExist(s2))
		{
			l = updateContentId(s2, s1);
		} else
		{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(CONTENTID_COLUMNS[1], s2);
			contentvalues.put(CONTENTID_COLUMNS[2], s1);
			l = mDb.insert("cotentidmap", null, contentvalues);
		}
		return l;
	}

	private boolean isContentIdExist(String s)
	{
		boolean flag = true;
		String s1 = s.replaceAll("'", "''");
		Cursor cursor = mDb.query("cotentidmap", null, (new StringBuilder(
				String.valueOf(CONTENTID_COLUMNS[1]))).append("='").append(s1)
				.append("'").toString(), null, null, null, null);
		if (cursor.getCount() == 0)
			flag = false;
		cursor.close();
		return flag;
	}

	@Override
	public long addMusicRadioGarbage(String paramString1, String paramString2)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long addMusicRate(String paramString, int paramInt)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean addSongs2MixPlaylist(long l, long[] al, boolean flag)
	{
		boolean flag1;
		flag1 = false;
		logger.v("addSongs2MixPlaylist() ---> Enter");
		if (l >= 0L && al != null && al.length != 0)
		{
			deleteSongsFromMixPlaylist(l, al, 2);

			for (int i = 0; i < al.length; i++)
			{
				ContentValues contentvalues = new ContentValues();
				contentvalues.put("playlist_id", Long.valueOf(l));
				contentvalues.put("audio_id", Long.valueOf(al[i]));
				int j;
				if (flag)
					j = ((flag1) ? 1 : 0);
				else
					j = 0;
				contentvalues.put("ISONLINEMUSIC", Integer.valueOf(j));
				mDb.insert(getPlaylistMapDB(2), null, contentvalues);
				logger.d((new StringBuilder("Insert sid: ")).append(al[i])
						.append(" to playlist: ").append(l).toString());
			}
			flag1 = true;
		} else
		{
			logger.d("illegal arguments from addSongsToPlaylist");
		}
		logger.v("return " + flag1);
		logger.v("addSongs2MixPlaylist() ---> Exit");
		return flag1;
	}

	@Override
	public boolean addSongs2Playlist(long l, long[] al, int i)
	{
		boolean flag = false;
		logger.v("addSongs2Playlist() ---> Enter");
		if (l >= 0L && al != null && al.length != 0)
		{
			for (int j = 0; j < al.length; j++)
			{
				if (!isSongInPlaylist(l, al[j], i))
				{
					ContentValues contentvalues = new ContentValues();
					contentvalues.put("playlist_id", Long.valueOf(l));
					contentvalues.put("audio_id", Long.valueOf(al[j]));
					mDb.insert(getPlaylistMapDB(i), null, contentvalues);
					logger.d((new StringBuilder("Insert sid: ")).append(al[j])
							.append(" to playlist: ").append(l).toString());
				}
			}
			flag = true;

		} else
		{
			logger.d("illegal arguments from addSongsToPlaylist");
		}
		logger.v("addSongs2Playlist() ---> Exit");
		logger.v("return 0 " + flag);
		return flag;
	}

	@Override
	public void closeDB()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int countSongNumInPlaylist(long l, int i)
	{
		logger.v("countSongNumInPlaylist() ---> Enter");
		Cursor cursor = mDb.query(getPlaylistMapDB(i),
				new String[] { "count(*)" },
				(new StringBuilder("playlist_id=")).append(l).toString(), null,
				null, null, null);
		int j = 0;
		if (cursor != null)
		{
			int k = cursor.getCount();
			if (k > 0)
			{
				logger.v("cursor.getCount() ----> " + k);
				cursor.moveToFirst();
				j = cursor.getInt(cursor.getColumnIndexOrThrow("count(*)"));
				cursor.close();
			}
		}
		logger.v("return ----> " + j);
		logger.v("countSongNumInPlaylist() ---> Exit");
		return j;
	}

	@Override
	public long createPlaylist(String paramString, int paramInt)
	{
		logger.v("createPlaylist() ---> Enter");
		long l;
		if (getPlaylistByName(paramString, paramInt) == null)
		{
			ContentValues contentvalues;
			contentvalues = new ContentValues();
			contentvalues.put("name", paramString);
			contentvalues.put("_data", "");
			contentvalues.put("date_added",
					Long.valueOf(System.currentTimeMillis()));
			contentvalues.put("date_modified",
					Long.valueOf(System.currentTimeMillis()));
			if (paramString
					.equals("cmccwm.mobilemusic.database.default.online.playlist.recent.play"))
			{
				contentvalues.put("user_id", Integer.valueOf(-1));
			}
			if (paramInt == 1 || paramInt == 2)
			{
				logger.v("createOnlinePlaylist() ---> Exit");
				l = mDb.insert(getPlaylistDB(paramInt), null, contentvalues);
				logger.v(String.valueOf(l) + "---------------------------");
			} else
			{
				contentvalues.put("user_id",
						Long.valueOf(GlobalSettingParameter.useraccount.mId));
				logger.v("createOnlinePlaylist() ---> Exit");
				l = mDb.insert(getPlaylistDB(paramInt), null, contentvalues);
			}
		} else
		{
			logger.e("Create duplicate default playlist!!");
			l = -1L;
		}
		return l;
	}

	@Override
	public void delAllLcSongsFromPlaylist(long paramLong)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteAllSongsFromMixPlaylist(long paramLong, int paramInt)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int deleteCacheDataByGroupCode(String paramString)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteDBDlItemById(long paramLong)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteDBDlItemByPath(String s)
	{
		mDb.delete("downloadlist",
				(new StringBuilder(String.valueOf(DOWNLOAD_COLUMNS[5])))
						.append("=").append(s).toString(), null);
	}

	@Override
	public void deletePlaylist(long l, int i)
	{
		logger.v("deletePlaylist() ---> Enter");
		mDb.delete(getPlaylistMapDB(i), (new StringBuilder("playlist_id="))
				.append(l).toString(), null);
		mDb.delete(getPlaylistDB(i), (new StringBuilder("_id=")).append(l)
				.toString(), null);
		logger.v("deletePlaylist() ---> Exit");
	}

	@Override
	public void deleteSongFromDB(long l)
	{
		ContentResolver contentresolver = mApp.getContentResolver();
		if (contentresolver != null)
			contentresolver
					.delete(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							(new StringBuilder("_id=")).append(l).toString(),
							null);
	}

	@Override
	public void deleteSongFromPlaylist(long paramLong1, long paramLong2,
			int paramInt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteSongsFromMixPlaylist(long l, long[] al, int i)
	{
		logger.v("deleteSongsFromPlaylist() ---> Enter");
		boolean flag = false;
		if (l >= 0L && al != null && al.length != 0)
		{
			for (int j = 0; j < al.length; j++)
			{
				mDb.delete(
						getPlaylistMapDB(i),
						(new StringBuilder("playlist_id=")).append(l)
								.append(" AND ").append("audio_id").append("=")
								.append(al[j]).toString(), null);
				logger.v("deleteSongsFromPlaylist() ---> Exit");
				flag = true;
			}
		} else
		{
			logger.d("illegal arguments from deleteSongsToPlaylist");
		}
		return flag;

	}

	@Override
	public boolean deleteSongsFromPlaylist(long paramLong,
			long[] paramArrayOfLong, int paramInt)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int deleteUpdatePackage(String paramString)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean get51CHStatus()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor getAllSongs(Uri paramUri, String[] paramArrayOfString)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getAllSongsCountByFolder(String[] as, boolean flag)
	{
		String s = "";
		int i = as.length;
		int k = 0;
		for (int j = 0; j < i; j++)
		{
			String s1 = editTheFolder(as[j]);
			s = (new StringBuilder(String.valueOf(s))).append("_data like '")
					.append(s1).append("%' and ").append("_data")
					.append(" not like '").append(s1).append("/%/%' or ")
					.toString();
		}
		if (s.length() > 0)
		{
			String s3 = (new StringBuilder(String.valueOf(s.substring(0,
					s.lastIndexOf("or"))))).append(")").toString();
			s = (new StringBuilder(String.valueOf("("))).append(s3).toString();
		}
		if (flag)
			s = (new StringBuilder(String.valueOf(s))).append(
					" and duration > 10000").toString();
		String s2 = (new StringBuilder(String.valueOf(s))).append(
				" and is_music = 1").toString();
		Cursor cursor = query(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null, s2, null, null);
		if (cursor != null)
		{
			k = cursor.getCount();
			cursor.close();
		}
		logger.v("getAllSongsCountByFolder() ---> Exit");
		return k;
	}

	public String editTheFolder(String s)
	{
		if (s.contains("'"))
			s = s.replace("'", "''");
		return s;
	}

	@Override
	public int getAllSongsCountByFolderAndSinger(String as[], int i,
			boolean flag)
	{
		String s = "";
		int j = as.length;
		int k = 0;
		do
		{
			if (k >= j)
			{
				if (s.length() > 0)
				{
					String s3 = (new StringBuilder(String.valueOf(s.substring(
							0, s.lastIndexOf("or"))))).append(")").toString();
					s = (new StringBuilder(String.valueOf("("))).append(s3)
							.toString();
				}
				if (i > 0)
					s = (new StringBuilder(String.valueOf(s)))
							.append(" and artist_id = ").append(i).toString();
				if (flag)
					s = (new StringBuilder(String.valueOf(s))).append(
							" and duration > 10000").toString();
				String s2 = (new StringBuilder(String.valueOf(s))).append(
						" and is_music = 1").toString();
				Cursor cursor = query(
						android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						null, s2, null, null);
				int l = cursor.getCount();
				cursor.close();
				logger.v("getAllSongs(Projection) ---> Exit");
				return l;
			}
			String s1 = editTheFolder(as[k]);
			s = (new StringBuilder(String.valueOf(s))).append("_data like '")
					.append(s1).append("%' and ").append("_data")
					.append(" not like '").append(s1).append("/%/%' or ")
					.toString();
			k++;
		} while (true);
	}

	@Override
	public int getArtistCountByFolder(String[] as, boolean flag)
	{
		logger.v("getArtistCountByFolder() ---> Enter");
		String s = "";
		HashSet hashset;
		int i = as.length;

		hashset = new HashSet();
		for (int j = 0; j < i; j++)
		{
			String s1 = editTheFolder(as[j]);
			s = (new StringBuilder(String.valueOf(s))).append("_data like '")
					.append(s1).append("%' and ").append("_data")
					.append(" not like '").append(s1).append("/%/%' or ")
					.toString();
		}
		if (s.length() > 0)
		{
			String s3 = (new StringBuilder(String.valueOf(s.substring(0,
					s.lastIndexOf("or"))))).append(")").toString();
			s = (new StringBuilder(String.valueOf("("))).append(s3).toString();
		}
		if (flag)
		{
			s = (new StringBuilder(String.valueOf(s))).append(
					" and duration > 10000").toString();
		}
		String s2 = (new StringBuilder(String.valueOf(s))).append(
				" and is_music = 1").toString();
		Cursor cursor = query(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { "artist_id" }, s2, null, "title_key");

		if (cursor == null || cursor.getCount() <= 0)
		{
			if (cursor != null && !cursor.isClosed())
				cursor.close();
		} else
		{
			logger.d((new StringBuilder("There are "))
					.append(cursor.getCount()).append(" songs in external DB!")
					.toString());
			cursor.moveToFirst();
		}
		while (cursor.moveToNext())
		{
			int k = cursor.getInt(cursor.getColumnIndexOrThrow("artist_id"));
			if (!hashset.contains(Integer.valueOf(k)))
				hashset.add(Integer.valueOf(k));
		}
		logger.v("getArtistCountByFolder() ---> Exit");
		return hashset.size();

	}

	@Override
	public Cursor getArtistsByCursor(String as[])
	{
		logger.v("getArtistsByCursor() ---> Enter");
		Cursor cursor = null;
		if (as != null && as.length != 0)
		{
			String s = "";
			HashSet hashset = new HashSet();
			for (int j = 0; j < as.length; j++)
			{
				String s1 = editTheFolder(as[j]);
				s = (new StringBuilder(String.valueOf(s)))
						.append("_data like '").append(s1).append("%' and ")
						.append("_data").append(" not like '").append(s1)
						.append("/%/%' or ").toString();
			}
			Cursor cursor1 = null;
			String s2 = "";
			if (s.length() > 0)
			{
				s = s.substring(0, s.lastIndexOf("or"));
				cursor1 = query(
						android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						new String[] { "artist_id" }, s, null, "title_key");
			}
			if (cursor1 == null || cursor1.getCount() <= 0)
			{
				if (cursor1 != null && !cursor1.isClosed())
					cursor1.close();
				logger.v("getArtistsByCursor(Projection) ---> Exit");
				return cursor;
			} else
			{
				logger.d((new StringBuilder("There are "))
						.append(cursor1.getCount())
						.append(" songs in external DB!").toString());
				cursor1.moveToFirst();
				do
				{
					int k = cursor1.getInt(cursor1
							.getColumnIndexOrThrow("artist_id"));
					logger.d("the artist_id is " + k);
					if (!hashset.contains(Integer.valueOf(k)))
					{
						s2 = (new StringBuilder(String.valueOf(s2)))
								.append("_id='").append(k).append("' or ")
								.toString();
						hashset.add(Integer.valueOf(k));
					}
				} while (cursor1.moveToNext());
				if (cursor1.isAfterLast())
					s2 = s2.substring(0, s2.lastIndexOf("or"));
				cursor = query(
						android.provider.MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
						null, s2, null, "artist_key");
				logger.v("getArtistsByCursor() ---> Exit");
			}
		}
		return cursor;
	}

	@Override
	public String getChannelId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getCheckOlderVersion()
	{
		// TODO Auto-generated method stub
		return false;
	}

	private String getPlaylistMapDB(int i)
	{
		String s;
		if (i == FLAG_OPERATE_ONLINE)
			s = "onine_music_playlist_map";
		else if (i == FLAG_OPERATE_LOCAL)
			s = "local_music_playlist_map";
		else
			s = "music_playlist_map";
		return s;
	}

	@Override
	public Cursor getCursorFromPlaylist(long l, int i)
	{
		logger.v("getCursorFromPlaylist() ---> Enter");
		Cursor cursor1;
		Cursor tempCursor = null;
		String s = "";
		boolean flag = true;
		cursor1 = mDb.query(getPlaylistMapDB(i), null, (new StringBuilder(
				"playlist_id=")).append(l).toString(), null, null, null, null);
		if (cursor1 != null && cursor1.getCount() > 0)
		{
			while (cursor1.moveToNext())
			{

				if (flag)
				{
					s = (new StringBuilder(String.valueOf(s)))
							.append("_id=")
							.append(cursor1.getInt(cursor1
									.getColumnIndexOrThrow("audio_id")))
							.toString();
					flag = false;
				} else
				{
					s = (new StringBuilder(String.valueOf(s)))
							.append(" OR _id=")
							.append(cursor1.getInt(cursor1
									.getColumnIndexOrThrow("audio_id")))
							.toString();
				}
			}
			logger.d((new StringBuilder("WHERE is: ")).append(s).toString());
			Cursor cursor2;
			if (i == 0)
			{
				cursor2 = mDb.query("online_music_audio_info", null, s, null,
						null, null, null);
			} else
			{
				String as[] = { "_id", "album", "album_id", "artist", "title",
						"_data", "duration", "_size" };
				cursor2 = query(
						android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						as, s, null, null);
			}
			tempCursor = cursor2;
			cursor1.close();
		} else
		{
			if (cursor1 != null)
			{
				cursor1.close();
			}
			logger.d("Do not find any music record in music map.");
		}

		return tempCursor;
	}

	@Override
	public List<String> getDataByGroupcode(String paramString)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayedAlbumName(String paramString)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayedArtistName(String s)
	{
		if (s == null || s.equalsIgnoreCase("<unknown>"))
			s = null;
		return s;
	}

	@Override
	public boolean getDownLoad_AutoRecover()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getEQMode()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getFirstSongInPlaylist(long l, int i)
	{
		logger.v("getFirstSongInPlaylist() ---> Enter");
		int j = -1;
		Cursor cursor = mDb.query(getPlaylistMapDB(i), null,
				(new StringBuilder("playlist_id=")).append(l).toString(), null,
				null, null, "_id asc");
		if (cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			j = cursor.getInt(cursor.getColumnIndexOrThrow("audio_id"));
			cursor.close();
		}
		logger.v("getFirstSongInPlaylist() ---> Exit");
		return (long) j;
	}

	@Override
	public String getLocalFolder()
	{
		return mApp.getSharedPreferences(
				"cmccwm.mobilemusic.database.peference", 0).getString(
				"cmccwm.mobilemusic.database.foldername", null);
	}

	@Override
	public int getMusicRate(String paramString)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRepeatMode()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getScanSmallSongFile()
	{
		return mApp.getSharedPreferences(
				"cmccwm.mobilemusic.database.peference", Context.MODE_PRIVATE)
				.getBoolean("cmccwm.mobilemusic.database.scansmallfile", false);
	}

	@Override
	public int getShuffleMode()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSkinStyleName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getSongFolder()
	{
		logger.v("getSongFolder()  ----> enter");
		HashSet hashset;
		Cursor cursor;
		hashset = new HashSet();
		cursor = query(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { "_data" }, "is_music = 1", null, null);

		if (cursor != null)
		{
			logger.v("cursor.getColumnCount() ---->" + cursor.getColumnCount());
			while (cursor.moveToNext())
			{
				String s;
				int i;
				if ((s = cursor
						.getString(cursor.getColumnIndexOrThrow("_data")))
						.length() > 0
						&& (i = s.lastIndexOf("/")) > 0)
				{
					hashset.add(s.substring(0, i));
				}
			}
			cursor.close();
		}
		return hashset;
	}

	@Override
	public long getSongIdByContentId(String paramString1, String paramString2)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSongIdByPath(String s)
	{
		long l = -1L;
		Cursor cursor = query(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { "_id" }, (new StringBuilder("_data = '"))
						.append(s).append("'").toString(), null, null);
		if (cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			l = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
			cursor.close();
		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		return l;
	}

	@Override
	public Cursor getSongsCursorByFolder(String[] as, boolean flag)
	{
		logger.v("getSongsCursorByFolder(Projection) ---> Enter");
		Cursor cursor = null;
		if (as != null)
		{
			int i;
			i = as.length;
			if (i != 0)
			{
				String s = "";
				int j = as.length;

				for (int k = 0; k < j; k++)
				{
					String s1 = editTheFolder(as[k]);
					s = (new StringBuilder(String.valueOf(s)))
							.append("_data like '").append(s1)
							.append("%' and ").append("_data")
							.append(" not like '").append(s1)
							.append("/%/%' or ").toString();
				}
				if (s.length() > 0)
				{
					String s3 = (new StringBuilder(String.valueOf(s.substring(
							0, s.lastIndexOf("or"))))).append(")").toString();
					s = (new StringBuilder(String.valueOf("("))).append(s3)
							.toString();
				}
				if (flag)
					s = (new StringBuilder(String.valueOf(s))).append(
							" and duration > 10000").toString();
				String s2 = (new StringBuilder(String.valueOf(s))).append(
						" and is_music = 1").toString();
				cursor = query(
						android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						null, s2, null, "title_key");
			}
		}
		logger.v("getSongsCursorByFolder(Projection) ---> exit");
		return cursor;
	}

	@Override
	public Cursor getSongsCursorByFolderAndSinger(String as[], int i,
			boolean flag)
	{
		logger.v("getSongsCursorByFolderAndSinger(Projection) ---> Enter");
		(new ArrayList()).clear();
		String s = "";
		int j = as.length;
		int k = 0;
		do
		{
			if (k >= j)
			{
				if (s.length() > 0)
				{
					String s3 = (new StringBuilder(String.valueOf(s.substring(
							0, s.lastIndexOf("or"))))).append(")").toString();
					s = (new StringBuilder(String.valueOf("("))).append(s3)
							.toString();
				}
				if (i > 0)
					s = (new StringBuilder(String.valueOf(s)))
							.append(" and artist_id = ").append(i).toString();
				if (flag)
					s = (new StringBuilder(String.valueOf(s))).append(
							" and duration > 10000").toString();
				String s2 = (new StringBuilder(String.valueOf(s))).append(
						" and is_music = 1").toString();
				Cursor cursor = query(
						android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						null, s2, null, "title_key");
				if (cursor != null && cursor.getCount() > 0)
					logger.d((new StringBuilder("There are "))
							.append(cursor.getCount())
							.append(" songs in external DB!").toString());
				logger.v("getSongsCursorByFolderAndSinger(Projection) ---> Exit");
				return cursor;
			}
			String s1 = editTheFolder(as[k]);
			s = (new StringBuilder(String.valueOf(s))).append("_data like '")
					.append(s1).append("%' and ").append("_data")
					.append(" not like '").append(s1).append("/%/%' or ")
					.toString();
			k++;
		} while (true);
	}

	@Override
	public Cursor getSongsFromAlbum(Uri uri, long l, String as[])
	{
		return query(uri, as, (new StringBuilder("album_id=")).append(l)
				.toString(), null, "title");
	}

	@Override
	public Cursor getSongsFromArtist(Uri uri, long l, String as[])
	{
		return query(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				as, (new StringBuilder("artist_id=")).append(l).toString(),
				null, "title");
	}

	@Override
	public Cursor getSongsFromGenre(String s, long l, String as[])
	{
		return query(
				android.provider.MediaStore.Audio.Genres.Members.getContentUri(
						s, l), as, null, null, null);
	}

	@Override
	public long[] getSongsIdFromFilePath(String paramString)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSubChannelId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getTensileShows()
	{
		return mApp.getSharedPreferences(
				"cmccwm.mobilemusic.database.peference", 0).getBoolean(
				"cmccwm.mobilemusic.database.usermore_tensile_shows", false);
	}

	@Override
	public boolean isCacheDataExist(String s)
	{
		boolean flag = true;
		String s1 = s.replaceAll("'", "'");
		Cursor cursor = mDb.query("T_CACHEDATA", null, (new StringBuilder(
				String.valueOf(CACHEDATA_COLUMNS[0]))).append("='").append(s1)
				.append("'").toString(), null, null, null, null);
		if (cursor.getCount() == 0)
			flag = false;
		cursor.close();
		return flag;
	}

	@Override
	public boolean isDefaultLocalPlaylist(String s)
	{
		boolean flag;
		if (s.equalsIgnoreCase("cmccwm.mobilemusic.database.default.local.playlist.recent.download")
				|| s.equalsIgnoreCase("cmccwm.mobilemusic.database.default.local.playlist.recent.play")
				|| s.equalsIgnoreCase("cmccwm.mobilemusic.database.default.local.playlist.favorite"))
			flag = true;
		else
			flag = false;
		return flag;
	}

	@Override
	public boolean isInMusicRadioGarbage(String paramString1,
			String paramString2)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtectedLocalPlaylist(String paramString)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtectedOnlinePlaylist(String paramString)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRingTone(String paramString)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSongInMixPlaylist(long l, long l1, boolean flag)
	{
		logger.v("isSongInMixPlaylist() ---> Enter");
		boolean flag1 = true;
		SQLiteDatabase sqlitedatabase = mDb;
		String s = getPlaylistMapDB(2);
		StringBuilder stringbuilder = (new StringBuilder("audio_id="))
				.append(l1).append(" AND ").append("playlist_id").append("=")
				.append(l).append(" AND ISONLINEMUSIC = ");
		int i;
		Cursor cursor;
		if (flag)
			i = 1;
		else
			i = 0;
		cursor = sqlitedatabase.query(s, null, stringbuilder.append(i)
				.toString(), null, null, null, null);
		if (cursor.getCount() == 0)
			flag1 = false;
		cursor.close();
		logger.d("return ---->" + flag1);
		logger.v("isSongInMixPlaylist() ---> Exit");
		return flag1;
	}

	@Override
	public boolean isSongInPlaylist(long l, long l1, int i)
	{
		logger.v("isSongInPlaylist() ---> Enter");
		boolean flag = true;
		Cursor cursor = mDb
				.query(getPlaylistMapDB(i),
						null,
						(new StringBuilder("audio_id=")).append(l1)
								.append(" AND ").append("playlist_id")
								.append("=").append(l).toString(), null, null,
						null, null);
		if (cursor.getCount() == 0)
			flag = false;
		cursor.close();
		logger.v("isSongInPlaylist() ---> Exit");
		return flag;
	}

	@Override
	public long[] isSongInPlaylist(long l, int i)
	{
		long al[];
		Cursor cursor;
		logger.v("isSongInPlaylist() ---> Enter");
		al = (long[]) null;
		cursor = mDb.query(getPlaylistMapDB(i), null, (new StringBuilder(
				"audio_id=")).append(l).toString(), null, null, null, null);
		if (cursor.getCount() != 0)
		{
			int j;
			int k;
			al = new long[cursor.getCount()];
			j = 0;
			k = cursor.getColumnIndexOrThrow("playlist_id");
			cursor.moveToFirst();
			if (!cursor.isAfterLast())
			{
				int i1 = j + 1;
				al[j] = cursor.getLong(k);
				cursor.moveToNext();
				j = i1;
			}
		}
		cursor.close();
		logger.v("isSongInPlaylist() ---> Exit");
		return al;
	}

	@Override
	public Cursor query(Uri uri, String[] as1, String s1, String[] as2,
			String s2)
	{
		ContentResolver contentresolver = mApp.getContentResolver();
		Cursor cursor;
		if (contentresolver == null)
			cursor = null;
		else
			cursor = contentresolver.query(uri, as1, s1, as2, s2);
		return cursor;
	}

	@Override
	public String queryCacheData(String s)
	{
		String s1 = null;
		if (s == null)
			throw new NullPointerException("null downloadItem");
		String s2 = s.replaceAll("'", "''");
		Cursor cursor = mDb.query("T_CACHEDATA", CACHEDATA_COLUMNS,
				(new StringBuilder(String.valueOf(CACHEDATA_COLUMNS[0])))
						.append("='").append(s2).append("'").toString(), null,
				null, null, null);
		if (cursor.getCount() == 0)
		{
			cursor.close();
		} else
		{
			cursor.moveToNext();
			String s3 = cursor.getString(cursor
					.getColumnIndexOrThrow(CACHEDATA_COLUMNS[3]));
			cursor.close();
			s1 = s3;
		}
		return s1;
	}

	@Override
	public String queryContentId(String paramString)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor queryDBDownloadList(Integer paramInteger)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String queryDateByGroupCode(String paramString)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int querySongIdByContentId(String paramString)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cursor querySongs(Uri paramUri, String paramString,
			String[] paramArrayOfString)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int removeAllCacheData()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeCacheData(String paramString)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public long renameDownloadMusic(String paramString1, String paramString2)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void renameLocalSong(long l, String s)
	{
		ContentResolver contentresolver = mApp.getContentResolver();
		if (contentresolver != null)
		{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("title", s);
			contentresolver
					.update(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							contentvalues, (new StringBuilder("_id="))
									.append(l).toString(), null);
		}
	}

	@Override
	public void renamePlaylist(long l, int i, String s)
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("name", s);
		mDb.update(getPlaylistDB(i), contentvalues, (new StringBuilder("_id="))
				.append(l).toString(), null);
	}

	@Override
	public void set51CHStatus(boolean paramBoolean)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setCheckOlderVersion(boolean paramBoolean)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setDownLoad_AutoRecover(Boolean paramBoolean)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setEQMode(int paramInt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocalFolder(String paramString)
	{
		android.content.SharedPreferences.Editor editor = mApp
				.getSharedPreferences("cmccwm.mobilemusic.database.peference",
						0).edit();
		editor.putString("cmccwm.mobilemusic.database.foldername", paramString);
		editor.commit();
	}

	@Override
	public void setRepeatMode(int i)
	{
		android.content.SharedPreferences.Editor editor = mApp
				.getSharedPreferences("cmccwm.mobilemusic.database.peference",
						0).edit();
		editor.putInt("cmccwm.mobilemusic.database.repeatmode", i);
		editor.commit();
	}

	@Override
	public void setScanSmallSongFile(Boolean paramBoolean)
	{
		android.content.SharedPreferences.Editor editor = mApp
				.getSharedPreferences("cmccwm.mobilemusic.database.peference",
						0).edit();
		editor.putBoolean("cmccwm.mobilemusic.database.scansmallfile",
				paramBoolean.booleanValue());
		editor.commit();
	}

	@Override
	public void setShuffleMode(int i)
	{
		android.content.SharedPreferences.Editor editor = mApp
				.getSharedPreferences("cmccwm.mobilemusic.database.peference",
						0).edit();
		editor.putInt("cmccwm.mobilemusic.database.shufflemode", i);
		editor.commit();
	}

	@Override
	public void setSkinStyleName(String paramString)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setTensileShows(Boolean paramBoolean)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int updateCacheData(String paramString1, String paramString2,
			String paramString3)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateContentId(String paramString1, String paramString2)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public List getAllPlaylists(int i)
	{
		logger.v("getPlaylists() ---> Enter");
		ArrayList arraylist;
		String s = null;
		arraylist = new ArrayList();
		arraylist.clear();
		ArrayList temp = null;
		UserAccount useraccount = GlobalSettingParameter.useraccount;
		if (useraccount != null)
		{
			s = (new StringBuilder("user_id ='")).append(useraccount.mId)
					.append("'").toString();
		}
		if (1 == i)
		{
			Cursor cursor = mDb.query(getPlaylistDB(i), null, s, null, null,
					null, "date_added asc");
			if (cursor != null && cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				while (cursor.moveToNext())
				{
					Playlist playlist = new Playlist();
					playlist.mExternalId = cursor.getInt(cursor
							.getColumnIndexOrThrow("_id"));
					playlist.mName = cursor.getString(cursor
							.getColumnIndexOrThrow("name"));
					playlist.mData = cursor.getString(cursor
							.getColumnIndexOrThrow("_data"));
					playlist.mDateModified = cursor.getLong(cursor
							.getColumnIndexOrThrow("date_modified"));
					playlist.mDateAdded = cursor.getLong(cursor
							.getColumnIndexOrThrow("date_added"));
					playlist.mNumOfSong = countSongNumInPlaylist(
							playlist.mExternalId, i);
					logger.d((new StringBuilder("Find playlist: ")).append(
							playlist.mName).toString());
					arraylist.add(playlist);
				}
				cursor.close();
				logger.v("getPlaylists() ---> Exit");
				temp = arraylist;
			}
		}
		return temp;
	}

	private String getPlaylistDB(int i)
	{
		String s;
		if (i == 0)
			s = "online_music_playlist";
		else if (i == 1)
			s = "local_music_playlist";
		else
			s = "music_playlist";
		return s;
	}

	@Override
	public Playlist getPlaylistByName(String s, int i)
	{
		logger.d("getPlaylistByName() ---> Enter");
		String s1;
		Playlist playlist = null;
		s1 = s.replaceAll("'", "''");
		String s2;
		if (i != 1
				&& !s1.equals("cmccwm.mobilemusic.database.default.online.playlist.recent.play")
				&& !s1.equals("cmccwm.mobilemusic.database.default.mix.playlist.recent.play"))
		{
			UserAccount useraccount;
			useraccount = GlobalSettingParameter.useraccount;
			if (useraccount == null)
			{
				return playlist;
			} else
			{
				s2 = (new StringBuilder("name='")).append(s1)
						.append("' and user_id ='").append(useraccount.mId)
						.append("'").toString();
			}
		} else
		{
			s2 = (new StringBuilder("name='")).append(s1).append("'")
					.toString();
		}
		Cursor cursor;
		cursor = mDb.query(getPlaylistDB(i), null, s2, null, null, null, null);
		if (cursor == null)
			return playlist;
		if (!cursor.moveToFirst())
		{
			cursor.close();
			playlist = null;
			return playlist;
		} else
		{
			Playlist playlist1 = new Playlist();
			playlist1.mExternalId = cursor.getInt(cursor
					.getColumnIndexOrThrow("_id"));
			playlist1.mName = cursor.getString(cursor
					.getColumnIndexOrThrow("name"));
			playlist1.mData = cursor.getString(cursor
					.getColumnIndexOrThrow("_data"));
			playlist1.mDateAdded = cursor.getLong(cursor
					.getColumnIndexOrThrow("date_added"));
			playlist1.mDateModified = cursor.getLong(cursor
					.getColumnIndexOrThrow("date_modified"));
			cursor.close();
			playlist = playlist1;
		}
		logger.d("getPlaylistByName() ---> exit");
		return playlist;
	}

	@Override
	public List<Song> getSongsFromPlaylist(long l, int i)
	{
		logger.v("getSongsFromPlaylist() ----> Enter");
		Cursor cursor = getCursorFromPlaylist(l, i);
		List list;
		if (cursor == null)
		{
			logger.d("Do not find any music");
			list = null;
		} else
		{
			list = getSongsFromCursor(cursor, i);
			cursor.close();
			logger.v("getSongsFromPlaylist() ---> Exit");
		}
		return list;
	}

	@Override
	public List getSongsFromCursor(Cursor cursor, int i)
	{
		ArrayList arraylist;
		arraylist = new ArrayList();
		arraylist.clear();
		if (cursor == null || cursor.getCount() <= 0)
		{
			return arraylist;
		} else
		{
			cursor.moveToFirst();
			while (!cursor.isAfterLast())
			{
				Song song;
				song = new Song();
				song.mAlbum = cursor.getString(cursor
						.getColumnIndexOrThrow("album"));
				song.mAlbumId = cursor.getInt(cursor
						.getColumnIndexOrThrow("album_id"));
				song.mArtist = cursor.getString(cursor
						.getColumnIndexOrThrow("artist"));
				song.mDuration = cursor.getInt(cursor
						.getColumnIndexOrThrow("duration"));
				if (i == 0)
				{
					song.mContentId = cursor.getString(cursor
							.getColumnIndexOrThrow("contentid"));
					song.mGroupCode = cursor.getString(cursor
							.getColumnIndexOrThrow("groupcode"));
					song.mMusicType = MusicType.ONLINEMUSIC.ordinal();
					song.mUrl = cursor.getString(cursor
							.getColumnIndexOrThrow("_data"));
					song.mUrl2 = cursor.getString(cursor
							.getColumnIndexOrThrow("url2"));
					song.mUrl3 = cursor.getString(cursor
							.getColumnIndexOrThrow("url3"));
					song.mSize2 = cursor.getLong(cursor
							.getColumnIndexOrThrow("filesize2"));
					song.mSize3 = cursor.getLong(cursor
							.getColumnIndexOrThrow("filesize3"));
					song.mPoint = cursor.getInt(cursor
							.getColumnIndexOrThrow("point"));
					song.mArtUrl = cursor.getString(cursor
							.getColumnIndexOrThrow("img"));

					boolean flag;
					if (cursor.getInt(cursor.getColumnIndexOrThrow("isdolby")) == 1)
						flag = true;
					else
						flag = false;
					song.isDolby = flag;
				} else
				{
					song.mUrl = cursor.getString(cursor
							.getColumnIndexOrThrow("_data"));
					song.mContentId = queryContentId(song.mUrl);
					song.mMusicType = MusicType.LOCALMUSIC.ordinal();
					if (song.mUrl.toLowerCase().endsWith(".mp4"))
						song.isDolby = true;
				}
				song.mId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
				song.mLyric = null;
				song.mTrack = cursor.getString(cursor
						.getColumnIndexOrThrow("title"));
				song.mSize = cursor.getLong(cursor
						.getColumnIndexOrThrow("_size"));
				arraylist.add(song);
				cursor.moveToNext();
			}
		}
		return arraylist;
	}

	@Override
	public Song getSongById(long l)
	{
		String s = (new StringBuilder("_id=")).append(l).toString();
		Cursor cursor = query(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null, s, null, null);
		Song song;
		if (cursor != null && cursor.getCount() > 0)
		{
			song = new Song();
			cursor.moveToFirst();
			song.mAlbum = cursor.getString(cursor
					.getColumnIndexOrThrow("album"));
			song.mAlbumId = cursor.getInt(cursor
					.getColumnIndexOrThrow("album_id"));
			song.mArtist = cursor.getString(cursor
					.getColumnIndexOrThrow("artist"));
			song.mUrl = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
			song.mContentId = queryContentId(song.mUrl);
			song.mDuration = cursor.getInt(cursor
					.getColumnIndexOrThrow("duration"));
			song.mId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
			song.mMusicType = MusicType.LOCALMUSIC.ordinal();
			song.mLyric = null;
			song.mTrack = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			song.mSize = cursor.getLong(cursor.getColumnIndexOrThrow("_size"));
			if (song.mUrl.toLowerCase().endsWith(".mp4"))
				song.isDolby = true;
			cursor.close();
		} else
		{
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			song = null;
		}
		return song;
	}

	@Override
	public List<Song> getSongsFromMixPlaylist(long l)
	{
		ArrayList arraylist;
		Cursor cursor;
		ArrayList arraylist1;
		arraylist = new ArrayList();
		cursor = mDb.query(getPlaylistMapDB(2), null, (new StringBuilder(
				"playlist_id=")).append(l).toString(), null, null, null, null);
		arraylist1 = new ArrayList();
		if (cursor != null && cursor.getCount() > 0)
		{
			String s = "";
			ArrayList arraylist2 = new ArrayList();
			int i = 0;
			String s1 = new String();
			cursor.moveToFirst();
			while (cursor.moveToNext())
			{
				boolean flag;
				int j;
				Pair pair;
				if (cursor
						.getInt(cursor.getColumnIndexOrThrow("ISONLINEMUSIC")) > 0)
					flag = true;
				else
					flag = false;
				j = cursor.getInt(cursor.getColumnIndexOrThrow("audio_id"));
				if (flag)
				{
					s = (new StringBuilder(String.valueOf(s))).append("_id=")
							.append(j).append("  or ").toString();
				} else
				{
					s1 = (new StringBuilder(String.valueOf(s1))).append("_id=")
							.append(j).append("  or ").toString();
					if (++i >= 500)
					{
						int k = s1.lastIndexOf("or");
						arraylist2.add(s1.substring(0, k));
						s1 = new String();
						i = 0;
					}
				}
				Cursor cursor1;
				String as[];

				if (s1 != null && s1.length() > 0)
				{
					int l1 = s1.lastIndexOf("or");
					if (l1 != -1 && l1 < s1.length())
						s1 = s1.substring(0, l1);
					arraylist2.add(s1);
				}
				if (s.lastIndexOf("or") != -1)
					s = s.substring(0, s.lastIndexOf("or"));
				cursor1 = mDb.query("online_music_audio_info", null, s, null,
						null, null, null);
				as = (new String[] { "_id", "album", "album_id", "artist",
						"title", "_data", "duration", "_size" });

				ArrayList arraylist3 = new ArrayList();
				for (int i1 = 0; i1 < arraylist2.size(); i1++)
				{
					arraylist3
							.add(query(
									android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
									as, (String) arraylist2.get(i1), null, null));
				}
				Map map = getSongsMapFromCursor(cursor1, 0);
				HashMap hashmap = new HashMap();
				for (int j1 = 0; j1 < arraylist3.size(); j1++)
				{
					hashmap.putAll(getSongsMapFromCursor(
							(Cursor) arraylist3.get(j1), 1));
				}

				for (Iterator iterator = arraylist1.iterator(); iterator
						.hasNext();)
				{
					Pair pair1 = (Pair) iterator.next();
					if (((Boolean) pair1.second).booleanValue())
					{
						Song song1 = (Song) map.get(Long
								.valueOf(((Integer) pair1.first).longValue()));
						if (song1 != null)
							arraylist.add(song1);
					} else
					{
						Song song = (Song) hashmap.get(Long
								.valueOf(((Integer) pair1.first).longValue()));
						if (song != null)
							arraylist.add(song);
					}
				}
				int k1 = 0;
				if (arraylist3.get(k1) != null)
					((Cursor) arraylist3.get(k1)).close();
				k1++;
				if (k1 > arraylist3.size())
				{
					arraylist3.clear();
					return arraylist;
				} else if (0 > arraylist3.size())
					arraylist3.clear();

				if (cursor1 != null)
					cursor1.close();

				pair = new Pair(Integer.valueOf(j), Boolean.valueOf(flag));
				arraylist1.add(pair);
			}
		} else
		{
			logger.d("Do not find any mix music record in mix playlist map.");
			if (cursor != null)
				cursor.close();
			arraylist = null;
		}

		return arraylist;
	}

	private Map getSongsMapFromCursor(Cursor cursor, int i)
	{
		HashMap hashmap = new HashMap();
		if (cursor == null || cursor.getCount() <= 0)
		{
			return hashmap;
		} else
		{
			cursor.moveToFirst();
			if (!cursor.isAfterLast())
			{
				while (cursor.moveToNext())
				{
					Song song;
					song = new Song();
					song.mAlbum = cursor.getString(cursor
							.getColumnIndexOrThrow("album"));
					song.mAlbumId = cursor.getInt(cursor
							.getColumnIndexOrThrow("album_id"));
					song.mArtist = cursor.getString(cursor
							.getColumnIndexOrThrow("artist"));
					song.mUrl = cursor.getString(cursor
							.getColumnIndexOrThrow("_data"));
					song.mDuration = cursor.getInt(cursor
							.getColumnIndexOrThrow("duration"));
					song.mContentId = cursor.getString(cursor
							.getColumnIndexOrThrow("contentid"));
					song.mGroupCode = cursor.getString(cursor
							.getColumnIndexOrThrow("groupcode"));
					if (i == 0)
					{
						song.mMusicType = MusicType.ONLINEMUSIC.ordinal();
					} else if (i == 1)
					{
						song.mMusicType = MusicType.LOCALMUSIC.ordinal();
					}

					song.mPoint = cursor.getInt(cursor
							.getColumnIndexOrThrow("point"));
					song.mArtUrl = cursor.getString(cursor
							.getColumnIndexOrThrow("img"));
					song.mUrl2 = cursor.getString(cursor
							.getColumnIndexOrThrow("url2"));
					song.mUrl3 = cursor.getString(cursor
							.getColumnIndexOrThrow("url3"));
					song.mSize2 = cursor.getLong(cursor
							.getColumnIndexOrThrow("filesize2"));
					song.mSize3 = cursor.getLong(cursor
							.getColumnIndexOrThrow("filesize3"));
					song.mId = cursor.getInt(cursor
							.getColumnIndexOrThrow("_id"));
					song.mLyric = null;
					song.mTrack = cursor.getString(cursor
							.getColumnIndexOrThrow("title"));
					song.mSize = cursor.getLong(cursor
							.getColumnIndexOrThrow("_size"));

					if (cursor.getInt(cursor.getColumnIndexOrThrow("isdolby")) == 1)
						song.isDolby = true;
					else
						song.isDolby = false;
					if (song.mUrl.toLowerCase().endsWith(".mp4"))
						song.isDolby = true;
					hashmap.put(Long.valueOf(song.mId), song);
				}
			}
			return hashmap;
		}
		// song.mContentId = queryContentId(song.mUrl);
	}

	@Override
	public List<Song> getSongsByFolder(String[] as, boolean flag)
	{
		logger.v("getAllSongs(Projection) ---> Enter");
		ArrayList<Song> al;
		if (as != null && as.length != 0)
		{
			String s;
			int i;
			al = new ArrayList<Song>();
			((List<Song>) (al)).clear();
			s = "";
			i = as.length;
			for (int j = 0; j < i; j++)
			{
				String s1 = editTheFolder(as[j]);
				s = (new StringBuilder(String.valueOf(s)))
						.append("_data like '").append(s1).append("%' and ")
						.append("_data").append(" not like '").append(s1)
						.append("/%/%' or ").toString();
			}
			Cursor cursor;
			if (s.length() > 0)
			{
				String s3 = (new StringBuilder(String.valueOf(s.substring(0,
						s.lastIndexOf("or"))))).append(")").toString();
				s = (new StringBuilder(String.valueOf("("))).append(s3)
						.toString();
			}
			if (flag)
				s = (new StringBuilder(String.valueOf(s))).append(
						" and duration > 10000").toString();
			String s2 = (new StringBuilder(String.valueOf(s))).append(
					" and is_music = 1").toString();
			cursor = query(
					android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					null, s2, null, "title_key");
			if (cursor == null || cursor.getCount() <= 0)
			{
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				logger.v("getAllSongs(Projection) ---> Exit");
				return ((List<Song>) (al));
			} else
			{
				logger.d((new StringBuilder("There are "))
						.append(cursor.getCount())
						.append(" songs in external DB!").toString());
				cursor.moveToFirst();
				while (cursor.moveToNext())
				{
					Song song = new Song();
					song.mAlbum = cursor.getString(cursor
							.getColumnIndexOrThrow("album"));
					song.mAlbumId = cursor.getInt(cursor
							.getColumnIndexOrThrow("album_id"));
					song.mArtist = cursor.getString(cursor
							.getColumnIndexOrThrow("artist"));
					song.mUrl = cursor.getString(cursor
							.getColumnIndexOrThrow("_data"));
					song.mContentId = queryContentId(song.mUrl);
					song.mDuration = cursor.getInt(cursor
							.getColumnIndexOrThrow("duration"));
					song.mId = cursor.getInt(cursor
							.getColumnIndexOrThrow("_id"));
					song.mMusicType = MusicType.LOCALMUSIC.ordinal();
					song.mLyric = null;
					song.mTrack = cursor.getString(cursor
							.getColumnIndexOrThrow("title"));
					song.mSize = cursor.getLong(cursor
							.getColumnIndexOrThrow("_size"));

					if (song.mUrl.toLowerCase().endsWith(".mp4"))
						song.isDolby = true;
					((List<Song>) (al)).add(song);
				}
				cursor.close();
			}

		} else
		{
			al = null;
		}
		return al;
	}

	@Override
	public Playlist getPlaylistByID(long l, int i)
	{
		logger.v("queryPlaylist(name) ---> Enter");
		Playlist playlist;
		UserAccount useraccount = GlobalSettingParameter.useraccount;
		String s;
		if (useraccount != null && i == 0)
		{

			s = (new StringBuilder("_id='")).append(l)
					.append("' and user_id ='").append(useraccount.mId)
					.append("'").toString();
		} else
		{
			s = (new StringBuilder("_id='")).append(l).append("'").toString();

		}
		Cursor cursor = mDb.query(getPlaylistDB(i), null, s, null, null, null,
				null);
		if (cursor != null && cursor.moveToFirst())
		{
			Playlist playlist1 = new Playlist();
			playlist1.mExternalId = cursor.getInt(cursor
					.getColumnIndexOrThrow("_id"));
			playlist1.mName = cursor.getString(cursor
					.getColumnIndexOrThrow("name"));
			playlist1.mData = cursor.getString(cursor
					.getColumnIndexOrThrow("_data"));
			playlist1.mDateAdded = cursor.getLong(cursor
					.getColumnIndexOrThrow("date_added"));
			playlist1.mDateModified = cursor.getLong(cursor
					.getColumnIndexOrThrow("date_modified"));
			cursor.close();
			playlist = playlist1;
		} else
		{
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			logger.v("queryPlaylist(name) ---> Exit");
			playlist = null;
		}
		return playlist;
	}

	@Override
	public long addOnlineMusicItem(Song song)
	{
		logger.v("addOnlineMusicItem() ---> Enter");
		long l;
		if (isOnlineMusicInDB(song))
		{
			logger.v((new StringBuilder(
					"addOnlineMusicItem() ---> item already exsits, Just Exit. Item id is: "))
					.append(song.mId).toString());
			l = song.mId;
		} else
		{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("_data", song.mUrl);
			contentvalues.put("album", song.mAlbum);
			contentvalues.put("album_id", Integer.valueOf(song.mAlbumId));
			contentvalues.put("artist", song.mArtist);
			contentvalues.put("date_added",
					Long.valueOf(System.currentTimeMillis()));
			contentvalues.put("title", song.mTrack);
			contentvalues.put("duration", Integer.valueOf(song.mDuration));
			contentvalues.put("_size", Long.valueOf(song.mSize));
			contentvalues.put("contentid", song.mContentId);
			contentvalues.put("groupcode", song.mGroupCode);
			contentvalues.put("url2", song.mUrl2);
			contentvalues.put("url3", song.mUrl3);
			contentvalues.put("filesize2", Long.valueOf(song.mSize2));
			contentvalues.put("filesize3", Long.valueOf(song.mSize3));
			contentvalues.put("groupcode", song.mGroupCode);
			String s;
			if (GlobalSettingParameter.useraccount != null)
				contentvalues.put("user_id",
						Long.valueOf(GlobalSettingParameter.useraccount.mId));
			else
				contentvalues.put("user_id", Integer.valueOf(-1));
			contentvalues.put("point", Integer.valueOf(song.mPoint));
			contentvalues.put("img", song.mArtUrl);
			if (song.isDolby)
				s = "1";
			else
				s = "0";
			contentvalues.put("isdolby", s);
			song.mId = mDb.insert("online_music_audio_info", null,
					contentvalues);
			logger.d((new StringBuilder("new item id is: ")).append(song.mId)
					.toString());
			logger.v("addOnlineMusicItem() ---> Exit");
			l = song.mId;
		}
		return l;
	}

	private boolean isOnlineMusicInDB(Song song)
	{
		logger.v("isOnlineMusicInDB() ---> Enter");
		boolean flag = true;
		Cursor cursor = mDb.query("online_music_audio_info", null,
				(new StringBuilder("contentid='")).append(song.mContentId)
						.append("'").toString(), null, null, null, null);
		if (cursor.getCount() == 0)
		{
			flag = false;
		} else
		{
			cursor.moveToFirst();
			song.mId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
		}
		cursor.close();
		logger.v("isOnlineMusicInDB() ---> Exit");
		return flag;
	}

	@Override
	public List<Song> getAllSongs(String[] as)
	{
		logger.v("getAllSongs(Projection) ---> Enter");
		ArrayList<Song> arraylist = new ArrayList<Song>();
		Cursor cursor;
		arraylist.clear();
		cursor = getAllSongs(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null);
		if (cursor == null || cursor.getCount() <= 0)
		{
			if (cursor != null && !cursor.isClosed())
				cursor.close();
		} else
		{
			logger.d((new StringBuilder("There are "))
					.append(cursor.getCount()).append(" songs in external DB!")
					.toString());
			while (cursor.moveToNext())
			{
				Song song = new Song();
				song.mAlbum = cursor.getString(cursor
						.getColumnIndexOrThrow("album"));
				song.mAlbumId = cursor.getInt(cursor
						.getColumnIndexOrThrow("album_id"));
				song.mArtist = cursor.getString(cursor
						.getColumnIndexOrThrow("artist"));
				song.mUrl = cursor.getString(cursor
						.getColumnIndexOrThrow("_data"));
				song.mContentId = queryContentId(song.mUrl);
				song.mDuration = cursor.getInt(cursor
						.getColumnIndexOrThrow("duration"));
				song.mId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
				song.mMusicType = MusicType.LOCALMUSIC.ordinal();
				song.mLyric = null;
				song.mTrack = cursor.getString(cursor
						.getColumnIndexOrThrow("title"));
				song.mSize = cursor.getLong(cursor
						.getColumnIndexOrThrow("_size"));
				if (song.mUrl.toLowerCase().endsWith(".mp4"))
					song.isDolby = true;
				arraylist.add(song);
			}
			cursor.close();
		}
		logger.v("getAllSongs(Projection) ---> Exit");
		return arraylist;
	}

	@Override
	public Song getSongByPath(String s)
	{
		String s1 = s.replaceAll("'", "''");
		String s2 = (new StringBuilder("_data='")).append(s1).append("'")
				.toString();
		Cursor cursor = query(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null, s2, null, null);
		Song song;
		if (cursor != null && cursor.getCount() > 0)
		{
			song = new Song();
			cursor.moveToFirst();
			song.mAlbum = cursor.getString(cursor
					.getColumnIndexOrThrow("album"));
			song.mAlbumId = cursor.getInt(cursor
					.getColumnIndexOrThrow("album_id"));
			song.mArtist = cursor.getString(cursor
					.getColumnIndexOrThrow("artist"));
			song.mUrl = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
			song.mContentId = queryContentId(song.mUrl);
			song.mDuration = cursor.getInt(cursor
					.getColumnIndexOrThrow("duration"));
			song.mId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
			song.mMusicType = MusicType.LOCALMUSIC.ordinal();
			song.mLyric = null;
			song.mTrack = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			song.mSize = cursor.getLong(cursor.getColumnIndexOrThrow("_size"));
			if (song.mUrl.toLowerCase().endsWith(".mp4"))
				song.isDolby = true;
			cursor.close();
		} else
		{
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			song = null;
		}
		return song;
	}

	@Override
	public int updateDBDownloadItem(DownloadItem downloaditem)
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(DOWNLOAD_COLUMNS[1],
				Integer.valueOf(downloaditem.getStatus()));
		contentvalues.put(DOWNLOAD_COLUMNS[2], downloaditem.getUrl());
		contentvalues.put(DOWNLOAD_COLUMNS[3],
				Long.valueOf(downloaditem.getTimeStep()));
		contentvalues.put(DOWNLOAD_COLUMNS[4],
				Long.valueOf(downloaditem.getTimeStartDL()));
		contentvalues.put(DOWNLOAD_COLUMNS[5], downloaditem.getFilePath());
		contentvalues.put(DOWNLOAD_COLUMNS[6], downloaditem.getFileName());
		contentvalues.put(DOWNLOAD_COLUMNS[7], downloaditem.getShowName());
		if (downloaditem.getContentType() == -300)
			contentvalues.put(DOWNLOAD_COLUMNS[8],
					Long.valueOf(downloaditem.getFileSize()));
		contentvalues.put(DOWNLOAD_COLUMNS[9],
				Long.valueOf(downloaditem.getDownloadSize()));
		contentvalues.put(DOWNLOAD_COLUMNS[10],
				Long.valueOf(downloaditem.getSizeFromStart()));
		contentvalues.put(DOWNLOAD_COLUMNS[16], downloaditem.getArtist());
		return mDb.update("downloadlist", contentvalues,
				(new StringBuilder(String.valueOf(DOWNLOAD_COLUMNS[0])))
						.append("=").append(downloaditem.getItemId())
						.toString(), null);
	}

	@Override
	public long updateOnlineMusicItem(Song song)
	{
		logger.v("updateOnlineMusicItem() ---> Enter");
		long l;
		if (!isOnlineMusicInDB(song))
		{
			logger.v((new StringBuilder(
					"updateOnlineMusicItem() ---> item already exsits, Just Exit. Item id is: "))
					.append(song.mId).toString());
			l = -1L;
		} else
		{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("_data", song.mUrl);
			contentvalues.put("album", song.mAlbum);
			contentvalues.put("album_id", Integer.valueOf(song.mAlbumId));
			contentvalues.put("artist", song.mArtist);
			contentvalues.put("title", song.mTrack);
			contentvalues.put("duration", Integer.valueOf(song.mDuration));
			contentvalues.put("_size", Long.valueOf(song.mSize));
			song.mId = mDb.update("online_music_audio_info", contentvalues,
					(new StringBuilder("_id='")).append(song.mId).append("'")
							.toString(), null);
			logger.d((new StringBuilder("new item id is: ")).append(song.mId)
					.toString());
			logger.v("updateOnlineMusicItem() ---> Exit");
			l = song.mId;
		}
		return l;
	}

	@Override
	public long addOnlineMusicItem(SongListItem songlistitem)
	{
		logger.v("addOnlineMusicItem() ---> Enter");
		long l = isOnlineMusicInDB(songlistitem.contentid,
				songlistitem.groupcode);
		long l2;
		if (l != -1L)
		{
			l2 = l;
		} else
		{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("artist", songlistitem.singer);
			contentvalues.put("date_added",
					Long.valueOf(System.currentTimeMillis()));
			contentvalues.put("title", songlistitem.title);
			contentvalues.put("contentid", songlistitem.contentid);
			contentvalues.put("groupcode", songlistitem.groupcode);
			contentvalues.put("_data", songlistitem.url);
			contentvalues.put("url2", songlistitem.url2);
			contentvalues.put("url3", songlistitem.url3);
			contentvalues.put("filesize2", songlistitem.filesize2);
			contentvalues.put("filesize3", songlistitem.filesize3);
			String s;
			long l1;
			if (GlobalSettingParameter.useraccount != null)
				contentvalues.put("user_id",
						Long.valueOf(GlobalSettingParameter.useraccount.mId));
			else
				contentvalues.put("user_id", Integer.valueOf(-1));
			contentvalues.put("point", songlistitem.point);
			contentvalues.put("img", songlistitem.img);
			if (songlistitem.isdolby != null
					&& songlistitem.isdolby.equals("1"))
				s = "1";
			else
				s = "0";
			contentvalues.put("isdolby", s);
			l1 = mDb.insert("online_music_audio_info", null, contentvalues);
			logger.d((new StringBuilder("new item id is: ")).append(l1)
					.toString());
			logger.v("addOnlineMusicItem() ---> Exit");
			l2 = l1;
		}
		return l2;
	}

	private int isOnlineMusicInDB(String s, String s1)
	{
		logger.v("isOnlineMusicInDB() ---> Enter");
		int i = -1;
		Cursor cursor = mDb.query("online_music_audio_info", null,
				(new StringBuilder("contentid='")).append(s).append("' and ")
						.append("groupcode").append("='").append(s1)
						.append("'").toString(), null, null, null, null);
		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			i = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
		}
		cursor.close();
		logger.v("isOnlineMusicInDB() ---> Exit");
		return i;
	}

	@Override
	public List<Song> getSongByKey(String s)
	{
		Object obj = null;
		if (s != null && !s.equals(""))
		{
			Cursor cursor;
			obj = new ArrayList();
			String s1 = editTheFolder(s);
			String s2 = (new StringBuilder(String.valueOf((new StringBuilder(
					String.valueOf(""))).append("(artist like '%").append(s1)
					.append("%' or ").append("title").append(" like '%")
					.append(s1).append("%')").toString()))).append(
					" and is_music = 1").toString();
			cursor = query(
					android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					null, s2, null, "title_key");
			if (cursor != null && cursor.getCount() > 0)
			{
				logger.d((new StringBuilder("There are "))
						.append(cursor.getCount())
						.append(" songs in external DB!").toString());
				cursor.moveToFirst();
				do
				{
					Song song = new Song();
					song.mAlbum = cursor.getString(cursor
							.getColumnIndexOrThrow("album"));
					song.mAlbumId = cursor.getInt(cursor
							.getColumnIndexOrThrow("album_id"));
					song.mArtist = cursor.getString(cursor
							.getColumnIndexOrThrow("artist"));
					song.mUrl = cursor.getString(cursor
							.getColumnIndexOrThrow("_data"));
					song.mContentId = queryContentId(song.mUrl);
					song.mDuration = cursor.getInt(cursor
							.getColumnIndexOrThrow("duration"));
					song.mId = cursor.getInt(cursor
							.getColumnIndexOrThrow("_id"));
					song.mMusicType = MusicType.LOCALMUSIC.ordinal();
					song.mLyric = null;
					song.mTrack = cursor.getString(cursor
							.getColumnIndexOrThrow("title"));
					song.mSize = cursor.getLong(cursor
							.getColumnIndexOrThrow("_size"));
					if (song.mUrl.toLowerCase().endsWith(".mp4"))
						song.isDolby = true;
					((List) (obj)).add(song);
				} while (cursor.moveToNext());
				if (cursor.isAfterLast())
					cursor.close();
			}
		}
		return ((List) (obj));
	}
}