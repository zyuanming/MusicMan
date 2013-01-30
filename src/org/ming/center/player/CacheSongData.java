package org.ming.center.player;

import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpTask;
import org.ming.center.ui.UIEventListener;
import org.ming.util.MyLogger;
import org.ming.util.Util;

import android.os.Message;
import android.util.Log;

public class CacheSongData implements PlayerEventListener, UIEventListener,
		MMHttpEventListener
{
	private static final MyLogger logger = MyLogger
			.getLogger("MusicPlayerActivity");
	private static CacheSongData sInstance = null;
	private CacheSongDataSource cacheSongDataSource = null;
	private MobileMusicApplication mApp = null;
	private boolean mBCurrPlayingCacheComplate = false;
	private MMHttpTask mCurrentTask = null;
	private HttpController mHttpController = null;
	private int mICacheDBLength = 0;
	private int mICurrPlayIndex = -1;
	private long mIFileAllLength = 0L;
	private int mINextPlayIndex = -1;
	private int mRepeatMode = 0;
	private Song mSongCache = null;
	private String mStrCacheFilePath = "";
	private String mStrCacheXml = "";
	private boolean mbPlayed = false;

	private CacheSongData()
	{
		this.mApp.getController().addPlayerEventListener(1021, this);
		this.mApp.getController().addPlayerEventListener(1020, this);
		this.mApp.getController().addPlayerEventListener(1022, this);
		this.mApp.getController().addPlayerEventListener(1023, this);
		this.mApp.getController().addPlayerEventListener(1024, this);
		this.mApp.getController().addUIEventListener(4017, this);
		this.mApp.getController().addUIEventListener(4014, this);
		this.mApp.getController().addUIEventListener(4015, this);
		this.mApp.getController().addUIEventListener(4016, this);
		Controller.getInstance(this.mApp).addHttpEventListener(3003, this);
		this.mRepeatMode = this.mApp.getController().getDBController()
				.getRepeatMode();
		this.mHttpController = this.mApp.getController().getHttpController();
	}

	private void askSongInfo(Song paramSong)
	{
		// CacheSongDataSource.stopCacheDB();
		// if (paramSong.mGroupCode == "<unknown>")
		// paramSong.mGroupCode = "null";
		// if (paramSong.mContentId == "<unknown>")
		// paramSong.mContentId = "null";
		// if (NetUtil.isNetStateWap())
		// ;
		// for (int i = 1002;; i = 5005)
		// {
		// MMHttpRequest localMMHttpRequest = MMHttpRequestBuilder
		// .buildRequest(i);
		// localMMHttpRequest.addUrlParams("contentid", paramSong.mContentId);
		// localMMHttpRequest.addUrlParams("groupcode", paramSong.mGroupCode);
		// Log.v("cache db", "sendRequest" + paramSong.mContentId
		// + paramSong.mTrack);
		// this.mCurrentTask = this.mHttpController
		// .sendRequest(localMMHttpRequest);
		// return;
		// }
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
		case 1002:
		case 5005:
		}
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
		case 3003:
		}
		if ((this.mCurrentTask != null)
				&& (localMMHttpTask.getTransId() == this.mCurrentTask
						.getTransId()))
		{
			onHttpResponse(localMMHttpTask);
			logger.v("get url complete");
		}
	}

	public void handlePlayerEvent(Message paramMessage)
	{
		// switch (paramMessage.what)
		// {
		// default:
		// case 1021:
		// case 1020:
		// case 1022:
		// case 1023:
		// case 1024:
		// }
		// this.mBCurrPlayingCacheComplate = false;
		// this.mbPlayed = true;
		// if (this.mApp.getController().getPlayerController()
		// .isPlayRecommendSong())
		// {
		// this.mICurrPlayIndex = this.mApp.getController()
		// .getPlayerController().getNowPlayingItemPosition();
		// this.mINextPlayIndex = this.mICurrPlayIndex;
		// this.mINextPlayIndex = (1 + this.mINextPlayIndex);
		// if (this.mINextPlayIndex >= -1
		// + this.mApp.getController().getPlayerController()
		// .getRecommendPlayList().size())
		// this.mINextPlayIndex = 0;
		// } else
		// {
		// this.mICurrPlayIndex = this.mApp.getController()
		// .getPlayerController().getNowPlayingItemPosition();
		// this.mApp.getController().getPlayerController().setNextItem();
		// this.mINextPlayIndex = this.mApp.getController()
		// .getPlayerController().getNowPlayingNextItem();
		// if (!Util.isOnlineMusic((Song) paramMessage.obj))
		// {
		// this.mBCurrPlayingCacheComplate = true;
		// if ((this.mRepeatMode != 1)
		// && (Util.isOnlineMusic((Song) this.mApp.getController()
		// .getPlayerController().getNowPlayingList()
		// .get(this.mINextPlayIndex))))
		// {
		// cacheNextSong((Song) this.mApp.getController()
		// .getPlayerController().getNowPlayingList()
		// .get(this.mINextPlayIndex));
		// continue;
		// this.mBCurrPlayingCacheComplate = true;
		// if (this.mApp.getController().getPlayerController()
		// .isPlayRecommendSong())
		// {
		// if ((this.mINextPlayIndex > -1)
		// && (this.mINextPlayIndex < this.mApp
		// .getController().getPlayerController()
		// .getRecommendPlayList().size()))
		// cacheNextSong((Song) this.mApp.getController()
		// .getPlayerController()
		// .getRecommendPlayList()
		// .get(this.mINextPlayIndex));
		// } else if ((this.mRepeatMode != 1)
		// && (this.mINextPlayIndex > -1)
		// && (this.mINextPlayIndex < this.mApp
		// .getController().getPlayerController()
		// .getNowPlayingList().size())
		// && (this.mICurrPlayIndex > -1)
		// && (this.mICurrPlayIndex < this.mApp
		// .getController().getPlayerController()
		// .getNowPlayingList().size())
		// && (Util.isOnlineMusic((Song) this.mApp
		// .getController().getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex)))
		// && (!((Song) this.mApp.getController()
		// .getPlayerController().getNowPlayingList()
		// .get(this.mINextPlayIndex)).mContentId
		// .equals(((Song) this.mApp.getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mICurrPlayIndex)).mContentId)))
		// {
		// cacheNextSong((Song) this.mApp.getController()
		// .getPlayerController().getNowPlayingList()
		// .get(this.mINextPlayIndex));
		// continue;
		// this.mRepeatMode = this.mApp.getController()
		// .getDBController().getRepeatMode();
		// Log.v("cache", "play mode id:" + this.mRepeatMode);
		// if ((this.mbPlayed)
		// && (!this.mApp.getController()
		// .getPlayerController()
		// .isPlayRecommendSong()))
		// {
		// this.mApp.getController().getPlayerController()
		// .setNextItem();
		// this.mINextPlayIndex = this.mApp.getController()
		// .getPlayerController()
		// .getNowPlayingNextItem();
		// Log.v("cache", "next song index:"
		// + this.mINextPlayIndex);
		// this.mICurrPlayIndex = this.mApp.getController()
		// .getPlayerController()
		// .getNowPlayingItemPosition();
		// Log.v("cache", "current song index:"
		// + this.mICurrPlayIndex);
		// if ((this.mINextPlayIndex > -1)
		// && (this.mINextPlayIndex < this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList().size())
		// && (this.mBCurrPlayingCacheComplate)
		// && (this.mRepeatMode != 1)
		// && (Util.isOnlineMusic((Song) this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex))))
		// if (this.mSongCache != null)
		// {
		// Log.v("CacheDb", "pre item "
		// + this.mSongCache.mContentId);
		// Log.v("CacheDb",
		// "Next item "
		// + ((Song) this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex)).mContentId);
		// if (!((Song) this.mApp.getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex)).mContentId
		// .equals(this.mSongCache.mContentId))
		// cacheNextSong((Song) this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex));
		// } else if ((this.mRepeatMode != 1)
		// && (this.mBCurrPlayingCacheComplate))
		// {
		// cacheNextSong((Song) this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex));
		// continue;
		// if ((this.mbPlayed)
		// && (!this.mApp.getController()
		// .getPlayerController()
		// .isPlayRecommendSong()))
		// {
		// this.mApp.getController()
		// .getPlayerController()
		// .setNextItem();
		// this.mINextPlayIndex = this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingNextItem();
		// this.mICurrPlayIndex = this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingItemPosition();
		// if ((this.mINextPlayIndex > -1)
		// && (this.mINextPlayIndex < this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .size())
		// && (this.mBCurrPlayingCacheComplate)
		// && (this.mRepeatMode != 1)
		// && (Util.isOnlineMusic((Song) this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex))))
		// if (this.mSongCache != null)
		// {
		// Log.v("CacheDb",
		// "pre item "
		// + this.mSongCache.mContentId);
		// Log.v("CacheDb",
		// "Next item "
		// + ((Song) this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex)).mContentId);
		// if (!((Song) this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex)).mContentId
		// .equals(this.mSongCache.mContentId))
		// cacheNextSong((Song) this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex));
		// } else if ((this.mRepeatMode != 1)
		// && (this.mBCurrPlayingCacheComplate))
		// {
		// cacheNextSong((Song) this.mApp
		// .getController()
		// .getPlayerController()
		// .getNowPlayingList()
		// .get(this.mINextPlayIndex));
		// continue;
		// this.mSongCache = ((Song) paramMessage.obj);
		// }
		// }
		// }
		// }
		// }
		// }
		// }
		// }
	}

	public void handleUIEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
		case 4014:
		case 4015:
		case 4016:
		case 4017:
		}
		if (this.mApp.getController().getPlayerController()
				.isPlayRecommendSong())
			if ((this.mINextPlayIndex > -1)
					&& (this.mINextPlayIndex < this.mApp.getController()
							.getPlayerController().getRecommendPlayList()
							.size()))
				cacheNextSong((Song) this.mApp.getController()
						.getPlayerController().getRecommendPlayList()
						.get(this.mINextPlayIndex));
		Log.v("cache", "UI_EVENT_LOGIN_SUCCESSED");
		if ((this.mRepeatMode != 1)
				&& (this.mBCurrPlayingCacheComplate)
				&& (this.mINextPlayIndex > -1)
				&& (this.mINextPlayIndex < this.mApp.getController()
						.getPlayerController().getNowPlayingList().size())
				&& (Util.isOnlineMusic((Song) this.mApp.getController()
						.getPlayerController().getNowPlayingList()
						.get(this.mINextPlayIndex)))
				&& (!((Song) this.mApp.getController().getPlayerController()
						.getNowPlayingList().get(this.mINextPlayIndex)).mContentId
						.equals(((Song) this.mApp.getController()
								.getPlayerController().getNowPlayingList()
								.get(this.mICurrPlayIndex)).mContentId)))
			cacheNextSong((Song) this.mApp.getController()
					.getPlayerController().getNowPlayingList()
					.get(this.mINextPlayIndex));
		if (this.mApp.getController().getPlayerController()
				.isPlayRecommendSong())
		{
			if ((this.mINextPlayIndex > -1)
					&& (this.mINextPlayIndex < this.mApp.getController()
							.getPlayerController().getRecommendPlayList()
							.size()))
				cacheNextSong((Song) this.mApp.getController()
						.getPlayerController().getRecommendPlayList()
						.get(this.mINextPlayIndex));
		} else
		{
			Log.v("cache",
					"next song is dolby"
							+ ((Song) this.mApp.getController()
									.getPlayerController().getNowPlayingList()
									.get(this.mINextPlayIndex)).isDolby);
			if ((this.mRepeatMode != 1)
					&& (this.mBCurrPlayingCacheComplate)
					&& (this.mINextPlayIndex > -1)
					&& (this.mINextPlayIndex < this.mApp.getController()
							.getPlayerController().getNowPlayingList().size())
					&& (Util.isOnlineMusic((Song) this.mApp.getController()
							.getPlayerController().getNowPlayingList()
							.get(this.mINextPlayIndex)))
					&& (!((Song) this.mApp.getController()
							.getPlayerController().getNowPlayingList()
							.get(this.mINextPlayIndex)).mContentId
							.equals(((Song) this.mApp.getController()
									.getPlayerController().getNowPlayingList()
									.get(this.mICurrPlayIndex)).mContentId)))
				cacheNextSong((Song) this.mApp.getController()
						.getPlayerController().getNowPlayingList()
						.get(this.mINextPlayIndex));
		}
	}

	public Song makeOnlineSong(String paramString)
	{
		return null;
		// Song localSong;
		// if (paramString == null)
		// localSong = null;
		//
		// XMLParser localXMLParser = new XMLParser(paramString.getBytes());
		// if ((localXMLParser.getRoot() == null)
		// || (localXMLParser.getValueByTag("code") == null))
		// {
		// localSong = null;
		// } else if ((localXMLParser.getValueByTag("code") != null)
		// && (!localXMLParser.getValueByTag("code").equals("000000")))
		// {
		// localSong = null;
		// } else if ((localXMLParser.getValueByTag("durl1") == null)
		// || (localXMLParser.getValueByTag("contentid") == null))
		// {
		// localXMLParser.getValueByTag("des");
		// logger.e("PlaySelectedSong() : data from server is null");
		// localSong = null;
		// } else
		// {
		// localXMLParser.getValueByTag("contentid");
		// localSong = new Song();
		// localSong.mAlbum = localXMLParser.getValueByTag("album");
		// localSong.mAlbumId = -1;
		// localSong.mArtist = localXMLParser.getValueByTag("singer");
		// localSong.mContentId = localXMLParser.getValueByTag("contentid");
		// localSong.mDuration = -1;
		// localSong.mId = -1L;
		// localSong.mMusicType = MusicType.ONLINEMUSIC.ordinal();
		// localSong.mLyric = null;
		// localSong.mArtUrl = localXMLParser.getValueByTag("img");
		// localSong.mTrack = localXMLParser.getValueByTag("songname");
		// localSong.mUrl = localXMLParser.getValueByTag("durl1");
		// localSong.mUrl2 = localXMLParser.getValueByTag("durl2");
		// localSong.mUrl3 = localXMLParser.getValueByTag("durl3");
		// if ((!"".equals(localXMLParser.getValueByTag("filesize1")))
		// && (localXMLParser.getValueByTag("filesize1") != null))
		// localSong.mSize = Long.valueOf(
		// localXMLParser.getValueByTag("filesize1")).longValue();
		// if ((!"".equals(localXMLParser.getValueByTag("filesize2")))
		// && (localXMLParser.getValueByTag("filesize2") != null))
		// localSong.mSize2 = Long.valueOf(
		// localXMLParser.getValueByTag("filesize2")).longValue();
		// if ((!"".equals(localXMLParser.getValueByTag("filesize3")))
		// && (localXMLParser.getValueByTag("filesize3") != null))
		// localSong.mSize3 = Long.valueOf(
		// localXMLParser.getValueByTag("filesize3")).longValue();
		// localSong.isDolby = Util.isDolby(localSong);
		// localSong.mGroupCode = localXMLParser.getValueByTag("groupcode");
		// String str = localXMLParser.getValueByTag("point");
		// if ((str != null) && (!str.trim().equals("")))
		// localSong.mPoint = Integer.parseInt(str);
		// else
		// localSong.mPoint = 0;
		// }
		// return localSong;
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
