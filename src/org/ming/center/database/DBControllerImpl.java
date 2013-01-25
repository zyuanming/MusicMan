package org.ming.center.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ming.center.ConfigSettingParameter;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.util.MyLogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

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
			sqlitedatabase
					.execSQL("create table T_APPINFO( CHANNELID text not null,SUBCHANNELID text not null)");
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
	public long addCacheData(String paramString1, String paramString2,
			String paramString3, String paramString4)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long addContentId(String paramString1, String paramString2)
	{
		// TODO Auto-generated method stub
		return 0;
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
	public boolean addSongs2MixPlaylist(long paramLong,
			long[] paramArrayOfLong, boolean paramBoolean)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addSongs2Playlist(long paramLong, long[] paramArrayOfLong,
			int paramInt)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void closeDB()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int countSongNumInPlaylist(long paramLong, int paramInt)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long createPlaylist(String paramString, int paramInt)
	{
		// TODO Auto-generated method stub
		return 0;
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
	public void deleteDBDlItemByPath(String paramString)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deletePlaylist(long paramLong, int paramInt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSongFromDB(long paramLong)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSongFromPlaylist(long paramLong1, long paramLong2,
			int paramInt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteSongsFromMixPlaylist(long paramLong,
			long[] paramArrayOfLong, int paramInt)
	{
		// TODO Auto-generated method stub
		return false;
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
	public int getAllSongsCountByFolder(String[] paramArrayOfString,
			boolean paramBoolean)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAllSongsCountByFolderAndSinger(String[] paramArrayOfString,
			int paramInt, boolean paramBoolean)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getArtistCountByFolder(String[] paramArrayOfString,
			boolean paramBoolean)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cursor getArtistsByCursor(String[] paramArrayOfString)
	{
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public Cursor getCursorFromPlaylist(long paramLong, int paramInt)
	{
		// TODO Auto-generated method stub
		return null;
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
	public String getDisplayedArtistName(String paramString)
	{
		// TODO Auto-generated method stub
		return null;
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
	public long getFirstSongInPlaylist(long paramLong, int paramInt)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLocalFolder()
	{
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSongIdByContentId(String paramString1, String paramString2)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSongIdByPath(String paramString)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cursor getSongsCursorByFolder(String[] paramArrayOfString,
			boolean paramBoolean)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor getSongsCursorByFolderAndSinger(String[] paramArrayOfString,
			int paramInt, boolean paramBoolean)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor getSongsFromAlbum(Uri paramUri, long paramLong,
			String[] paramArrayOfString)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor getSongsFromArtist(Uri paramUri, long paramLong,
			String[] paramArrayOfString)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor getSongsFromGenre(String paramString, long paramLong,
			String[] paramArrayOfString)
	{
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCacheDataExist(String paramString)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefaultLocalPlaylist(String paramString)
	{
		// TODO Auto-generated method stub
		return false;
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
	public boolean isSongInMixPlaylist(long paramLong1, long paramLong2,
			boolean paramBoolean)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSongInPlaylist(long paramLong1, long paramLong2,
			int paramInt)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long[] isSongInPlaylist(long paramLong, int paramInt)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor query(Uri paramUri, String[] paramArrayOfString1,
			String paramString1, String[] paramArrayOfString2,
			String paramString2)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String queryCacheData(String paramString)
	{
		// TODO Auto-generated method stub
		return null;
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
	public void renameLocalSong(long paramLong, String paramString)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void renamePlaylist(long paramLong, int paramInt, String paramString)
	{
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void setRepeatMode(int paramInt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setScanSmallSongFile(Boolean paramBoolean)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setShuffleMode(int paramInt)
	{
		// TODO Auto-generated method stub

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
		String s;
		arraylist = new ArrayList();
		arraylist.clear();
		s = null;
		ArrayList temp;
		UserAccount useraccount;
		useraccount = GlobalSettingParameter.useraccount;
		temp = null;
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
		String s1;
		Playlist playlist = null;
		logger.v("queryPlaylist(name) ---> Enter");
		s1 = s.replaceAll("'", "''");
		if (i != 1
				&& !s1.equals("cmccwm.mobilemusic.database.default.online.playlist.recent.play")
				&& !s1.equals("cmccwm.mobilemusic.database.default.mix.playlist.recent.play"))
		{
			UserAccount useraccount;
			useraccount = GlobalSettingParameter.useraccount;
			String s2;
			if (useraccount == null)
			{
				s2 = (new StringBuilder("name='")).append(s1).append("'")
						.toString();

			} else
			{
				s2 = (new StringBuilder("name='")).append(s1)
						.append("' and user_id ='").append(useraccount.mId)
						.append("'").toString();
			}
			Cursor cursor;
			cursor = mDb.query(getPlaylistDB(i), null, s2, null, null, null,
					null);
			if (cursor == null)
				return playlist;
			if (!cursor.moveToFirst())
			{
				cursor.close();
				logger.v("queryPlaylist(name) ---> Exit");
				playlist = null;
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
			return playlist;
		}
		return playlist;
	}
}