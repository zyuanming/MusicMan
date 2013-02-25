package org.ming.center.player;

import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.ui.UIEventListener;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;
import org.ming.util.XMLParser;

import android.os.Message;
import android.util.Log;

public class CacheSongData implements PlayerEventListener, UIEventListener,
		MMHttpEventListener
{
	private static final MyLogger logger = MyLogger
			.getLogger("MusicPlayerActivity");
	private static CacheSongData sInstance = null;
	private CacheSongDataSource cacheSongDataSource;
	private MobileMusicApplication mApp;
	private boolean mBCurrPlayingCacheComplate;
	private MMHttpTask mCurrentTask;
	private HttpController mHttpController;
	private int mICacheDBLength;
	private int mICurrPlayIndex;
	private long mIFileAllLength;
	private int mINextPlayIndex;
	private int mRepeatMode;
	private Song mSongCache;
	private String mStrCacheFilePath;
	private String mStrCacheXml;
	private boolean mbPlayed;

	private CacheSongData()
	{
		mApp = null;
		mCurrentTask = null;
		mHttpController = null;
		mbPlayed = false;
		cacheSongDataSource = null;
		mRepeatMode = 0;
		mICurrPlayIndex = -1;
		mINextPlayIndex = -1;
		mICacheDBLength = 0;
		mIFileAllLength = 0L;
		mBCurrPlayingCacheComplate = false;
		mStrCacheFilePath = "";
		mSongCache = null;
		mStrCacheXml = "";
		mApp = MobileMusicApplication.getInstance();
		mApp.getController().addPlayerEventListener(1021, this);
		mApp.getController().addPlayerEventListener(1020, this);
		mApp.getController().addPlayerEventListener(1022, this);
		mApp.getController().addPlayerEventListener(1023, this);
		mApp.getController().addPlayerEventListener(1024, this);
		mApp.getController().addUIEventListener(4017, this);
		mApp.getController().addUIEventListener(4014, this);
		mApp.getController().addUIEventListener(4015, this);
		mApp.getController().addUIEventListener(4016, this);
		Controller.getInstance(mApp).addHttpEventListener(3003, this);
		mRepeatMode = mApp.getController().getDBController().getRepeatMode();
		mHttpController = mApp.getController().getHttpController();
	}

	private void askSongInfo(Song song)
	{
		CacheSongDataSource.stopCacheDB();
		if (song.mGroupCode == "<unknown>")
			song.mGroupCode = "null";
		if (song.mContentId == "<unknown>")
			song.mContentId = "null";
		char c;
		MMHttpRequest mmhttprequest;
		if (NetUtil.isNetStateWap())
			c = '\u03EA';
		else
			c = '\u138D';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		mmhttprequest.addUrlParams("contentid", song.mContentId);
		mmhttprequest.addUrlParams("groupcode", song.mGroupCode);
		Log.v("cache db",
				(new StringBuilder("sendRequest")).append(song.mContentId)
						.append(song.mTrack).toString());
		mCurrentTask = mHttpController.sendRequest(mmhttprequest);
	}

	private void cacheNextSong(Song paramSong)
	{
		if ((paramSong != null) && (paramSong.mUrl != null)
				&& (!paramSong.mUrl.equalsIgnoreCase("<unknown>")))
		{
			this.mSongCache = paramSong;
			this.mStrCacheXml = null;
			this.cacheSongDataSource = CacheSongDataSource.startHandle(
					paramSong, this, this.mApp);
		}
		askSongInfo(paramSong);
	}

	public static CacheSongData getInstance()
	{
		if (sInstance == null)
			sInstance = new CacheSongData();
		return sInstance;
	}

	private void onHttpResponse(MMHttpTask paramMMHttpTask)
	{
		int i = paramMMHttpTask.getRequest().getReqType();
		byte[] arrayOfByte = paramMMHttpTask.getResponseBody();
		switch (i)
		{
		default:
			break;
		case 1002:
		case 5005:
			String str = new String(arrayOfByte);
			Song localSong = makeOnlineSong(str);
			if (localSong != null)
			{
				this.mSongCache = makeOnlineSong(this.mStrCacheXml);
				this.mStrCacheXml = str;
				this.cacheSongDataSource = CacheSongDataSource.startHandle(
						localSong, this, this.mApp);
			}
			logger.v("nw---->" + str);
			break;
		}

	}

	public int GetNextSongPosition()
	{
		return this.mINextPlayIndex;
	}

	public int getCacheDBLength()
	{
		return this.mICacheDBLength;
	}

	public String getCacheFilePath()
	{
		return this.mStrCacheFilePath;
	}

	public Song getCacheSong()
	{
		return this.mSongCache;
	}

	public String getCacheXml()
	{
		return this.mStrCacheXml;
	}

	public long getFileAllLength()
	{
		return this.mIFileAllLength;
	}

	public void handleMMHttpEvent(Message paramMessage)
	{
		MMHttpTask localMMHttpTask = (MMHttpTask) paramMessage.obj;
		switch (paramMessage.what)
		{
		default:
			break;
		case 3003:
			if ((this.mCurrentTask != null)
					&& (localMMHttpTask.getTransId() == this.mCurrentTask
							.getTransId()))
			{
				onHttpResponse(localMMHttpTask);
				logger.v("get url complete");
			}
			break;
		}

	}

	public void handlePlayerEvent(Message message)
	{
		switch (message.what)
		{
		default:
		case 1021:
			mBCurrPlayingCacheComplate = false;
			mbPlayed = true;
			if (mApp.getController().getPlayerController()
					.isPlayRecommendSong())
			{
				mICurrPlayIndex = mApp.getController().getPlayerController()
						.getNowPlayingItemPosition();
				mINextPlayIndex = mICurrPlayIndex;
				mINextPlayIndex = 1 + mINextPlayIndex;
				if (mINextPlayIndex >= -1
						+ mApp.getController().getPlayerController()
								.getRecommendPlayList().size())
					mINextPlayIndex = 0;
			} else
			{
				mICurrPlayIndex = mApp.getController().getPlayerController()
						.getNowPlayingItemPosition();
				mApp.getController().getPlayerController().setNextItem();
				mINextPlayIndex = mApp.getController().getPlayerController()
						.getNowPlayingNextItem();
				if (!Util.isOnlineMusic((Song) message.obj))
				{
					mBCurrPlayingCacheComplate = true;
					if (mRepeatMode != 1
							&& Util.isOnlineMusic((Song) mApp.getController()
									.getPlayerController().getNowPlayingList()
									.get(mINextPlayIndex)))
						cacheNextSong((Song) mApp.getController()
								.getPlayerController().getNowPlayingList()
								.get(mINextPlayIndex));
				}
			}
			break;
		case 1020:
			mBCurrPlayingCacheComplate = true;
			if (mApp.getController().getPlayerController()
					.isPlayRecommendSong())
			{
				if (mINextPlayIndex > -1
						&& mINextPlayIndex < mApp.getController()
								.getPlayerController().getRecommendPlayList()
								.size())
					cacheNextSong((Song) mApp.getController()
							.getPlayerController().getRecommendPlayList()
							.get(mINextPlayIndex));
			} else if (mRepeatMode != 1
					&& mINextPlayIndex > -1
					&& mINextPlayIndex < mApp.getController()
							.getPlayerController().getNowPlayingList().size()
					&& mICurrPlayIndex > -1
					&& mICurrPlayIndex < mApp.getController()
							.getPlayerController().getNowPlayingList().size()
					&& Util.isOnlineMusic((Song) mApp.getController()
							.getPlayerController().getNowPlayingList()
							.get(mINextPlayIndex))
					&& !((Song) mApp.getController().getPlayerController()
							.getNowPlayingList().get(mINextPlayIndex)).mContentId
							.equals(((Song) mApp.getController()
									.getPlayerController().getNowPlayingList()
									.get(mICurrPlayIndex)).mContentId))
				cacheNextSong((Song) mApp.getController().getPlayerController()
						.getNowPlayingList().get(mINextPlayIndex));
			break;
		case 1022:
			mRepeatMode = mApp.getController().getDBController()
					.getRepeatMode();
			Log.v("cache",
					(new StringBuilder("play mode id:")).append(mRepeatMode)
							.toString());
			if (mbPlayed
					&& !mApp.getController().getPlayerController()
							.isPlayRecommendSong())
			{
				mApp.getController().getPlayerController().setNextItem();
				mINextPlayIndex = mApp.getController().getPlayerController()
						.getNowPlayingNextItem();
				Log.v("cache",
						(new StringBuilder("next song index:")).append(
								mINextPlayIndex).toString());
				mICurrPlayIndex = mApp.getController().getPlayerController()
						.getNowPlayingItemPosition();
				Log.v("cache", (new StringBuilder("current song index:"))
						.append(mICurrPlayIndex).toString());
				if (mINextPlayIndex > -1
						&& mINextPlayIndex < mApp.getController()
								.getPlayerController().getNowPlayingList()
								.size()
						&& mBCurrPlayingCacheComplate
						&& mRepeatMode != 1
						&& Util.isOnlineMusic((Song) mApp.getController()
								.getPlayerController().getNowPlayingList()
								.get(mINextPlayIndex)))
					if (mSongCache != null)
					{
						Log.v("CacheDb", (new StringBuilder("pre item "))
								.append(mSongCache.mContentId).toString());
						Log.v("CacheDb",
								(new StringBuilder("Next item "))
										.append(((Song) mApp.getController()
												.getPlayerController()
												.getNowPlayingList()
												.get(mINextPlayIndex)).mContentId)
										.toString());
						if (!((Song) mApp.getController().getPlayerController()
								.getNowPlayingList().get(mINextPlayIndex)).mContentId
								.equals(mSongCache.mContentId))
							cacheNextSong((Song) mApp.getController()
									.getPlayerController().getNowPlayingList()
									.get(mINextPlayIndex));
					} else if (mRepeatMode != 1 && mBCurrPlayingCacheComplate)
						cacheNextSong((Song) mApp.getController()
								.getPlayerController().getNowPlayingList()
								.get(mINextPlayIndex));
			}
			break;
		case 1023:
			if (mbPlayed
					&& !mApp.getController().getPlayerController()
							.isPlayRecommendSong())
			{
				mApp.getController().getPlayerController().setNextItem();
				mINextPlayIndex = mApp.getController().getPlayerController()
						.getNowPlayingNextItem();
				mICurrPlayIndex = mApp.getController().getPlayerController()
						.getNowPlayingItemPosition();
				if (mINextPlayIndex > -1
						&& mINextPlayIndex < mApp.getController()
								.getPlayerController().getNowPlayingList()
								.size()
						&& mBCurrPlayingCacheComplate
						&& mRepeatMode != 1
						&& Util.isOnlineMusic((Song) mApp.getController()
								.getPlayerController().getNowPlayingList()
								.get(mINextPlayIndex)))
					if (mSongCache != null)
					{
						Log.v("CacheDb", (new StringBuilder("pre item "))
								.append(mSongCache.mContentId).toString());
						Log.v("CacheDb",
								(new StringBuilder("Next item "))
										.append(((Song) mApp.getController()
												.getPlayerController()
												.getNowPlayingList()
												.get(mINextPlayIndex)).mContentId)
										.toString());
						if (!((Song) mApp.getController().getPlayerController()
								.getNowPlayingList().get(mINextPlayIndex)).mContentId
								.equals(mSongCache.mContentId))
							cacheNextSong((Song) mApp.getController()
									.getPlayerController().getNowPlayingList()
									.get(mINextPlayIndex));
					} else if (mRepeatMode != 1 && mBCurrPlayingCacheComplate)
						cacheNextSong((Song) mApp.getController()
								.getPlayerController().getNowPlayingList()
								.get(mINextPlayIndex));
			}
			break;
		case 1024:
			mSongCache = (Song) message.obj;
			break;
		}
	}

	public void handleUIEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			return;
		case 4014:
		case 4015:
			if (mApp.getController().getPlayerController()
					.isPlayRecommendSong())
			{
				if (mINextPlayIndex > -1
						&& mINextPlayIndex < mApp.getController()
								.getPlayerController().getRecommendPlayList()
								.size())
					cacheNextSong((Song) mApp.getController()
							.getPlayerController().getRecommendPlayList()
							.get(mINextPlayIndex));
			} else if (mRepeatMode != 1
					&& mBCurrPlayingCacheComplate
					&& mINextPlayIndex > -1
					&& mINextPlayIndex < mApp.getController()
							.getPlayerController().getNowPlayingList().size()
					&& Util.isOnlineMusic((Song) mApp.getController()
							.getPlayerController().getNowPlayingList()
							.get(mINextPlayIndex))
					&& !((Song) mApp.getController().getPlayerController()
							.getNowPlayingList().get(mINextPlayIndex)).mContentId
							.equals(((Song) mApp.getController()
									.getPlayerController().getNowPlayingList()
									.get(mICurrPlayIndex)).mContentId))
				cacheNextSong((Song) mApp.getController().getPlayerController()
						.getNowPlayingList().get(mINextPlayIndex));
			Log.v("cache", "UI_EVENT_LOGIN_SUCCESSED");
			break;
		case 4016:
		case 4017:
			if (mApp.getController().getPlayerController()
					.isPlayRecommendSong())
			{
				if (mINextPlayIndex > -1
						&& mINextPlayIndex < mApp.getController()
								.getPlayerController().getRecommendPlayList()
								.size())
					cacheNextSong((Song) mApp.getController()
							.getPlayerController().getRecommendPlayList()
							.get(mINextPlayIndex));
			} else
			{
				Log.v("cache",
						(new StringBuilder("next song is dolby")).append(
								((Song) mApp.getController()
										.getPlayerController()
										.getNowPlayingList()
										.get(mINextPlayIndex)).isDolby)
								.toString());
				if (mRepeatMode != 1
						&& mBCurrPlayingCacheComplate
						&& mINextPlayIndex > -1
						&& mINextPlayIndex < mApp.getController()
								.getPlayerController().getNowPlayingList()
								.size()
						&& Util.isOnlineMusic((Song) mApp.getController()
								.getPlayerController().getNowPlayingList()
								.get(mINextPlayIndex))
						&& !((Song) mApp.getController().getPlayerController()
								.getNowPlayingList().get(mINextPlayIndex)).mContentId
								.equals(((Song) mApp.getController()
										.getPlayerController()
										.getNowPlayingList()
										.get(mICurrPlayIndex)).mContentId))
					cacheNextSong((Song) mApp.getController()
							.getPlayerController().getNowPlayingList()
							.get(mINextPlayIndex));
			}
			break;
		}
	}

	public Song makeOnlineSong(String s)
	{
		Song song;
		if (s == null)
		{
			song = null;
		} else
		{
			XMLParser xmlparser = new XMLParser(s.getBytes());
			if (xmlparser.getRoot() == null
					|| xmlparser.getValueByTag("code") == null)
				song = null;
			else if (xmlparser.getValueByTag("code") != null
					&& !xmlparser.getValueByTag("code").equals("000000"))
				song = null;
			else if (xmlparser.getValueByTag("durl1") == null
					|| xmlparser.getValueByTag("contentid") == null)
			{
				xmlparser.getValueByTag("des");
				logger.e("PlaySelectedSong() : data from server is null");
				song = null;
			} else
			{
				xmlparser.getValueByTag("contentid");
				song = new Song();
				song.mAlbum = xmlparser.getValueByTag("album");
				song.mAlbumId = -1;
				song.mArtist = xmlparser.getValueByTag("singer");
				song.mContentId = xmlparser.getValueByTag("contentid");
				song.mDuration = -1;
				song.mId = -1L;
				song.mMusicType = MusicType.ONLINEMUSIC.ordinal();
				song.mLyric = null;
				song.mArtUrl = xmlparser.getValueByTag("img");
				song.mTrack = xmlparser.getValueByTag("songname");
				song.mUrl = xmlparser.getValueByTag("durl1");
				song.mUrl2 = xmlparser.getValueByTag("durl2");
				song.mUrl3 = xmlparser.getValueByTag("durl3");
				if (!"".equals(xmlparser.getValueByTag("filesize1"))
						&& xmlparser.getValueByTag("filesize1") != null)
					song.mSize = Long.valueOf(
							xmlparser.getValueByTag("filesize1")).longValue();
				if (!"".equals(xmlparser.getValueByTag("filesize2"))
						&& xmlparser.getValueByTag("filesize2") != null)
					song.mSize2 = Long.valueOf(
							xmlparser.getValueByTag("filesize2")).longValue();
				if (!"".equals(xmlparser.getValueByTag("filesize3"))
						&& xmlparser.getValueByTag("filesize3") != null)
					song.mSize3 = Long.valueOf(
							xmlparser.getValueByTag("filesize3")).longValue();
				song.isDolby = Util.isDolby(song);
				song.mGroupCode = xmlparser.getValueByTag("groupcode");
				String s1 = xmlparser.getValueByTag("point");
				if (s1 != null && !s1.trim().equals(""))
					song.mPoint = Integer.parseInt(s1);
				else
					song.mPoint = 0;
			}
		}
		return song;
	}

	public void setCacheDBLength(long paramLong)
	{
		this.mICacheDBLength = ((int) paramLong);
	}

	public void setCacheFilePath(String paramString)
	{
		this.mStrCacheFilePath = paramString;
	}

	public void setCacheSong(Song paramSong)
	{
		this.mSongCache = paramSong;
	}

	public void setFileAllLength(long paramLong)
	{
		this.mIFileAllLength = paramLong;
	}

	public void stopCacheDB()
	{
		CacheSongDataSource.stopCacheDB();
	}
}
