package org.ming.center.player;

import java.util.ArrayList;
import java.util.List;

import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.MusicType;
import org.ming.center.database.Playlist;
import org.ming.center.database.Song;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpTask;
import org.ming.center.http.item.SongListItem;
import org.ming.center.system.SystemControllerImpl;
import org.ming.center.system.SystemEventListener;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Message;
import android.util.Log;

public class PlayerControllerImpl implements PlayerController,
		PlayerEventListener, SystemEventListener, MMHttpEventListener
{
	public static final int MAX_PLAYER_ERROR_COUNT = 5;
	public static final int MEDIA_ERROR_DATA_SOURCE = -102;
	public static final int MEDIA_ERROR_LICENSE_REQUIRED = 300;
	public static final int MEDIA_ERROR_PLAYER = -101;
	public static final int MEDIA_ERROR_PLAYER_IO = -100;
	private static final MyLogger logger = MyLogger
			.getLogger("PlayerControllerImpl");
	public static boolean mIsCmwapToWlan = false;
	public static boolean mIsplayEnd;
	private static PlayerControllerImpl sInstance = null;
	public final String HTTP_PREFIX = "http";
	boolean ISOPENLOCALMUSIC = false;
	public final String WAP_HOST = "218.200.160.30";
	private boolean m51CHStatus = false;
	private MobileMusicApplication mApp = null;
	private Dialog mCurrentDialog;
	private DBController mDBController = null;
	private Dispatcher mDispatcher;
	private int mEQMode = 1;
	private boolean mIsLoadingData = false;
	private boolean mIsRadio = false;
	private List<Song> mNowPlayingList;
	private List<Song> mRecommendPlayList = null;
	private List<Song> mBackUpList;
	private List<Song> mFullPlayList;
	private int mPayingNextItem = 0;
	private int mPlayerErrCount = 0;
	private int mPlayingItemPosition = 0;
	private int mRadioPageNo = 1;
	private int mRepeatMode = 0;
	private int mShuffleMode = 0;
	private int mTransId = -1;
	private long time_lastPress;
	private MusicPlayerWrapper wrapper;
	private MMHttpTask mCurrentTask;
	private HttpController mHttpController;
	static
	{
		mIsplayEnd = false;
	}

	private PlayerControllerImpl(
			MobileMusicApplication paramMobileMusicApplication)
	{
		logger.v("PlayerControllerImpl() ---> Enter");
		this.mApp = paramMobileMusicApplication;
		this.mDBController = paramMobileMusicApplication.getController()
				.getDBController();
		// this.mHttpController = paramMobileMusicApplication.getController()
		// .getHttpController();
		this.mDispatcher = this.mApp.getEventDispatcher();
		this.mFullPlayList = new ArrayList();
		// asyncLoadFullList();
		if (this.wrapper == null)
			this.wrapper = new MusicPlayerWrapper(this.mApp);
		this.mNowPlayingList = new ArrayList();
		this.mBackUpList = new ArrayList();
		Playlist localPlaylist = this.mDBController.getPlaylistByName(
				"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
				2);
		List localList = this.mDBController
				.getSongsFromMixPlaylist(localPlaylist.mExternalId);
		if (localList != null)
		{
			this.mNowPlayingList.addAll(localList);
			this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(1023));
			this.mBackUpList.addAll(localList);
		}
		this.mApp.getController().addHttpEventListener(3009, this);
		this.mApp.getController().addPlayerEventListener(1002, this);
		this.mApp.getController().addPlayerEventListener(1004, this);
		this.mApp.getController().addPlayerEventListener(1005, this);
		this.mApp.getController().addPlayerEventListener(1003, this);
		this.mApp.getController().addPlayerEventListener(1009, this);
		this.mApp.getController().addPlayerEventListener(1010, this);
		this.mApp.getController().addPlayerEventListener(1012, this);
		this.mApp.getController().addPlayerEventListener(1011, this);
		this.mApp.getController().addPlayerEventListener(1015, this);
		this.mApp.getController().addPlayerEventListener(1018, this);
		this.mApp.getController().addPlayerEventListener(1019, this);
		this.mApp.getController().addPlayerEventListener(1016, this);
		this.mApp.getController().addPlayerEventListener(1006, this);
		this.mApp.getController().addHttpEventListener(3007, this);
		this.mApp.getController().addHttpEventListener(3008, this);
		this.mApp.getController().addHttpEventListener(3003, this);
		this.mApp.getController().addHttpEventListener(3003, this);
		this.mApp.getController().addHttpEventListener(3005, this);
		this.mApp.getController().addHttpEventListener(3004, this);
		this.mApp.getController().addHttpEventListener(3006, this);
		this.mApp.getController().addSystemEventListener(8, this);
		this.mApp.getController().addSystemEventListener(9, this);
		this.mApp.getController().addSystemEventListener(6, this);
		this.mApp.getController().addSystemEventListener(7, this);
		this.mApp.getController().addSystemEventListener(4, this);
		this.mApp.getController().addSystemEventListener(5, this);
		this.mApp.getController().addSystemEventListener(22, this);
		this.mApp.getController().addSystemEventListener(23, this);
		this.mShuffleMode = this.mDBController.getShuffleMode();
		this.mRepeatMode = this.mDBController.getRepeatMode();
		this.mEQMode = this.mDBController.getEQMode();
		this.m51CHStatus = this.mDBController.get51CHStatus();
		logger.v("PlayerControllerImpl() ---> Exit");
	}

	public static PlayerControllerImpl getInstance(
			MobileMusicApplication paramMobileMusicApplication)
	{
		logger.v("getInstance() ---> Enter");
		if (sInstance == null)
			sInstance = new PlayerControllerImpl(paramMobileMusicApplication);
		logger.v("getInstance() ---> Exit");
		return sInstance;
	}

	@Override
	public void handleMMHttpEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSystemEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePlayerEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelPlaybackStatusBar()
	{
		logger.v("cancelPlaybackStatusBar() ---> Enter");
		((NotificationManager) this.mApp.getSystemService("notification"))
				.cancel(1);
		logger.v("cancelPlaybackStatusBar() ---> Exit");
	}

	@Override
	public void clearNowPlayingList()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean get51CHStatus()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDuration()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEQMode()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getIsLoadingData()
	{
		boolean bool = false;
		try
		{
			bool = this.mIsLoadingData;
			return bool;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return bool;
	}

	@Override
	public int getNowPlayingItemPosition()
	{
		int i = -1;
		try
		{
			i = this.mPlayingItemPosition;
			return i;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}

	@Override
	public int getNowPlayingNextItem()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPosition()
	{
		return this.wrapper.getCurrentPosition();
	}

	@Override
	public int getProgressDownloadPercent()
	{
		return (int) (100.0F * this.wrapper.getPercent());
	}

	@Override
	public int getRepeatMode()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getShuffleMode()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTransId()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFileOnExternalStorage()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInitialized()
	{
		boolean bool = this.wrapper.isInitialized();
		return bool;
	}

	@Override
	public boolean isInteruptByCall()
	{
		logger.d("isInteruptByCall ----> enter");
		try
		{
			boolean bool = this.wrapper.isInteruptByCall();
			logger.d("return ---->" + bool);
			logger.d("isInteruptByCall ----> exit");
			return bool;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isPause()
	{
		return this.wrapper.isPaused();
	}

	@Override
	public boolean isPlayEnd()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPlayRecommendSong()
	{
		boolean flag;
		if (mRecommendPlayList != null)
			flag = true;
		else
			flag = false;
		return flag;
	}

	@Override
	public boolean isPlaying()
	{
		return this.wrapper.isPlaying();
	}

	@Override
	public void loadAllLocalTracks2NowPlayingList()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void next()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean open(int paramInt)
	{
		boolean bool1 = false;
		try
		{
			logger.v("open(position) ---> Enter");
			this.ISOPENLOCALMUSIC = true;
			if (paramInt == -1)
				return bool1;
			if (Util.checkWapStatus())
			{
				boolean bool2 = doHandOpen(paramInt);
				bool1 = bool2;
				return bool1;
			}
			if ((this.mNowPlayingList == null)
					|| (this.mNowPlayingList.get(paramInt) == null)
					|| (((Song) this.mNowPlayingList.get(paramInt)).mUrl == null)
					|| ("<unknown>".equals(((Song) this.mNowPlayingList
							.get(paramInt)).mUrl))
					|| ("".equals(((Song) this.mNowPlayingList.get(paramInt)).mUrl)))
			{
				logger.v("------------------------");
				bool1 = doHandOpen(paramInt);
			}
			if (((Song) this.mNowPlayingList.get(paramInt)).mUrl
					.contains("218.200.160.30"))
			{
				if (this.mRecommendPlayList != null)
				{
					this.mRecommendPlayList.clear();
					this.mRecommendPlayList = null;
					this.mPlayingItemPosition = 0;
				}
				this.wrapper.stopInternal();
				mIsCmwapToWlan = true;
				if ((CacheSongData.getInstance().getCacheSong() != null)
						&& (CacheSongData.getInstance().getCacheSong().mContentId
								.equals(((Song) this.mNowPlayingList
										.get(paramInt)).mContentId)))
				{
					playOnlineSong(CacheSongData.getInstance().getCacheXml());
					Log.v("cache", "begin playing cache db");
				} else
				{
					bool1 = doHandOpen(paramInt);
				}
			}
			bool1 = doHandOpen(paramInt);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return bool1;
	}

	private boolean doHandOpen(int i)
	{
		logger.v("doHandlOpen ----> enter");
		boolean flag = false;
		if (mNowPlayingList != null && !mNowPlayingList.isEmpty())
		{
			if (i < 0 || i > -1 + mNowPlayingList.size())
			{
				logger.e("open(position), position is invalid");
			} else
			{
				if (mRecommendPlayList != null)
				{
					mRecommendPlayList.clear();
					mRecommendPlayList = null;
					mPlayingItemPosition = 0;
				}
				setIsLoadingData(true);
				mDispatcher.sendMessage(mDispatcher.obtainMessage(4008));
				Song song = (Song) mNowPlayingList.get(i);
				flag = false;
				if (song != null)
				{
					mDispatcher.sendMessage(mDispatcher.obtainMessage(4010));
					// if (mCurrentTask != null) {
					// mHttpController.cancelTask(mCurrentTask);
					// mCurrentTask = null;
					// }
					if (song.mUrl == null
							|| song.mUrl.equalsIgnoreCase("<unknown>"))
					{
						mPlayingItemPosition = i;
						wrapper.stop();
						if (CacheSongData.getInstance().getCacheSong() != null
								&& CacheSongData.getInstance().getCacheSong().mContentId
										.equals(song.mContentId))
						{
							if (NetUtil.netState == 3
									&& !SystemControllerImpl.getInstance(mApp)
											.checkWapStatus()
									|| !NetUtil.isConnection())
								mApp.getEventDispatcher().sendMessage(
										mApp.getEventDispatcher()
												.obtainMessage(1014));
							else
								playOnlineSong(CacheSongData.getInstance()
										.getCacheXml());
							logger.v("cache----->begin playing cache db");
						} else
						{
							askSongInfo(song);
						}
					} else
					{
						wrapper.start((Song) mNowPlayingList.get(i));
						mPlayingItemPosition = i;
						logger.v("open(position) ---> Exit");
					}
					flag = true;
				}
			}
		} else
		{
			logger.e("open(position), now playing list is empty");
		}
		return flag;
	}

	private void askSongInfo(Song paramSong)
	{
		// if (paramSong.mGroupCode == "<unknown>")
		// paramSong.mGroupCode = "null";
		// if (paramSong.mContentId == "<unknown>")
		// paramSong.mContentId = "null";
		// if (NetUtil.isNetStateWap())
		// ;
		// for (int i = 1002;; i = 5005) {
		// MMHttpRequest localMMHttpRequest = MMHttpRequestBuilder
		// .buildRequest(i);
		// localMMHttpRequest.addUrlParams("contentid", paramSong.mContentId);
		// localMMHttpRequest.addUrlParams("groupcode", paramSong.mGroupCode);
		// this.mCurrentTask = this.mHttpController
		// .sendRequest(localMMHttpRequest);
		// return;
		// }
	}

	@Override
	public boolean openRecommendSong(int i)
	{
		boolean flag = false;
		logger.v("open(position) ---> Enter");
		if (mRecommendPlayList != null && !mRecommendPlayList.isEmpty())
		{
			if ((i < 0) || (i > -1 + this.mRecommendPlayList.size()))
			{
				logger.e("open(position), position is invalid");
				flag = false;
			}
			logger.e("open(position), position is invalid");
			Song song = (Song) mRecommendPlayList.get(i);
			flag = false;
			if (song != null)
			{
				add2NowPlayingList(song, true);
				setIsLoadingData(true);
				mDispatcher.sendMessage(mDispatcher.obtainMessage(4008));
				mDispatcher.sendMessage(mDispatcher.obtainMessage(4010));
				if (mCurrentTask != null)
				{
					mHttpController.cancelTask(mCurrentTask);
					mCurrentTask = null;
				}
				if (song.mUrl == null
						|| song.mUrl.equalsIgnoreCase("<unknown>"))
				{
					mPlayingItemPosition = i;
					wrapper.stop();
					if (CacheSongData.getInstance().getCacheSong() != null
							&& CacheSongData.getInstance().getCacheSong().mContentId
									.equals(song.mContentId))
					{
						playOnlineSong(CacheSongData.getInstance()
								.getCacheXml());
						Log.v("cache", "begin playing cache db");
					} else
					{
						askSongInfo(song);
					}
				} else
				{
					wrapper.start((Song) mRecommendPlayList.get(i));
					mPlayingItemPosition = i;
					logger.v("open(position) ---> Exit");
				}
				flag = true;
			}
		} else
		{
			logger.e("open(position), now playing list is empty");
		}
		return flag;
	}

	@Override
	public void pause()
	{
		logger.v("pause() ---> Enter");
		this.wrapper.pause();
		logger.v("pause() ---> Exit");
	}

	@Override
	public void playOnlineSong(String paramString)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void prev()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean renewOnlinePlay(int paramInt)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void seek(long paramLong)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void set51CHStatus(boolean paramBoolean)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setEQMode(int paramInt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setNextItem()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNowPlayingItemPosition(int paramInt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int setRepeatMode(int paramInt)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setShuffleMode(int paramInt)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTransId(int paramInt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void start()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void stop()
	{
		logger.v("stop() ---> Enter");
		setIsLoadingData(false);
		this.wrapper.stop();
		logger.v("stop() ---> Exit");
	}

	@Override
	public int add2NowPlayingList(Song paramSong)
	{
		logger.d("add2NowPlayingList() ----> enter");
		try
		{
			int i = add2NowPlayingList(paramSong, false);
			logger.d("i ----> " + i);
			logger.d("add2NowPlayingList() ----> exit");
			return i;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int add2NowPlayingList(Song song, boolean flag)
	{
		logger.v("add2NowPlayingList() ---> Enter");
		int i = -1;
		if (song != null)
		{
			int j;
			if (mIsRadio)
			{
				mNowPlayingList.clear();
				mBackUpList.clear();
				Playlist playlist = mDBController
						.getPlaylistByName(
								"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
								2);
				List list = mDBController
						.getSongsFromMixPlaylist(playlist.mExternalId);
				if (list != null)
				{
					mNowPlayingList.addAll(list);
					mBackUpList.addAll(list);
				}
				mPlayingItemPosition = 0;
				mIsRadio = false;
			}
			if (mRecommendPlayList != null && mRecommendPlayList.size() > 0)
				flag = true;
			j = mNowPlayingList.indexOf(song);
			if (!flag)
			{
				if (i != j)
				{
					mNowPlayingList.remove(song);
					mBackUpList.remove(song);
				} else if (i == j)
				{
					logger.d("mNowPlayingList do not has the song , now add the song");
					mNowPlayingList.add(song);
					mBackUpList.add(song);
				}
				if (j > mPlayingItemPosition)
				{
					mNowPlayingList.add(1 + mPlayingItemPosition, song);
					mBackUpList.add(1 + mPlayingItemPosition, song);
					if (mNowPlayingList.size() == 0
							|| mPlayingItemPosition == -1
									+ mNowPlayingList.size())
					{
						mNowPlayingList.add(song);
						mBackUpList.add(song);
					} else
					{
						int k = 1 + mPlayingItemPosition;
						int l = 1 + mPlayingItemPosition;
						if (mNowPlayingList.size() < 1 + mPlayingItemPosition)
							k = mNowPlayingList.size();
						mNowPlayingList.add(k, song);
						if (mBackUpList.size() < 1 + mPlayingItemPosition)
							l = mBackUpList.size();
						mBackUpList.add(l, song);
					}

				} else
				{
					int i1 = 1 + mPlayingItemPosition;
					int j1 = 1 + mPlayingItemPosition;
					if (mNowPlayingList.size() < 1 + mPlayingItemPosition)
						i1 = mNowPlayingList.size();
					mNowPlayingList.add(i1, song);
					if (mBackUpList.size() < 1 + mPlayingItemPosition)
						j1 = mBackUpList.size();
					mBackUpList.add(j1, song);
					mPlayingItemPosition = -1 + mPlayingItemPosition;
					if (flag)
						mDispatcher
								.sendMessage(mDispatcher.obtainMessage(1023));
					i = mNowPlayingList.indexOf(song);
					logger.d("i ----> " + i);
					logger.v("add2NowPlayingList() ---> Exit");
					return i;
				}
			} else
			{
				mDispatcher.sendMessage(mDispatcher.obtainMessage(1023));
				i = mNowPlayingList.indexOf(song);
				logger.d("i ----> " + i);
				logger.v("add2NowPlayingList() ---> Exit");
				return i;
			}
		}
		return i;
	}

	@Override
	public int add2NowPlayingList(List<Song> paramList)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addRecommendSongList(List<Song> paramList)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int checkSongInNowPlayingList(Song paramSong)
	{
		logger.v("checkSongInNowPlayingList() ---> Enter");
		int ii = 0;
		if (paramSong == null)
		{
			ii = -1;
		} else
		{
			for (int i = 0; i < this.mNowPlayingList.size(); i++)
			{
				if (paramSong.equals((Song) this.mNowPlayingList.get(i)))
				{
					logger.d("Got the position is " + i);
					return i;
				}
			}
		}
		logger.v("checkSongInNowPlayingList() ---> Exit");
		return ii;
	}

	@Override
	public void delDownloadSong(Song paramSong)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void delOnlineSong(Song paramSong)
	{
		Song localSong;
		int i;
		int j;
		try
		{
			logger.v("delOnlineSong() ---> Enter");
			localSong = getCurrentPlayingItem();
			if (this.mNowPlayingList.size() > 0)
			{
				i = this.mNowPlayingList.indexOf(paramSong);
				if ((paramSong != null) && (i != -1))
				{
					this.mNowPlayingList.remove(paramSong);
					this.mBackUpList.remove(paramSong);
					this.mDispatcher.sendMessage(this.mDispatcher
							.obtainMessage(1023));
				}
			} else
			{
				return;
			}
			j = 0;
			if (localSong != null)
			{
				if (paramSong.mMusicType != MusicType.LOCALMUSIC.ordinal())
				{
					int i4 = localSong.mMusicType;
					int i5 = MusicType.LOCALMUSIC.ordinal();
					j = 0;
					if (i4 == i5)
					{
						boolean bool3 = localSong.mUrl.equals(paramSong.mUrl);
						j = 0;
						if (bool3)
						{
							j = 1;
							stop();
						}
					}
				} else if (localSong.mMusicType == MusicType.ONLINEMUSIC
						.ordinal())
				{
					j = 0;
					if (localSong.mMusicType == localSong.mMusicType)
					{
						j = 0;
						if (localSong.mContentId.equals(paramSong.mContentId))
						{
							j = 0;
							if (localSong.mGroupCode
									.equals(paramSong.mGroupCode))
							{
								j = 1;
								stop();
								if ((this.mPlayingItemPosition == i)
										&& ((this.mNowPlayingList.size() == 0) || (this.mPlayingItemPosition >= this.mNowPlayingList
												.size())))
								{
									this.mPlayingItemPosition = 0;
									this.mDispatcher
											.sendMessage(this.mDispatcher
													.obtainMessage(4008));
								} else if (this.mPlayingItemPosition <= i)
								{
									int m = 0;
									if (this.mPlayingItemPosition > 0)
									{
										m = -1 + this.mPlayingItemPosition;
									}
									this.mPlayingItemPosition = m;
									setIsLoadingData(false);
								}
							}
						}
					}
				}
			}
			if (j != 0)
				open(this.mPlayingItemPosition);
			logger.v("delOnlineSong() ---> Exit");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setIsLoadingData(boolean paramBoolean)
	{
		this.mIsLoadingData = paramBoolean;
	}

	@Override
	public void delRadioSong(Song paramSong)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Song getCurrentPlayingItem()
	{
		Song localSong = null;
		try
		{
			List localList = this.mRecommendPlayList;
			localSong = null;
			if (localList != null)
			{
				int i = this.mRecommendPlayList.size();
				localSong = null;
				if (i > 0)
				{
					localSong = (Song) this.mRecommendPlayList
							.get(this.mPlayingItemPosition);
					if (localSong != null)
						return localSong;
				}
			}
			if ((this.mNowPlayingList != null)
					&& (!this.mNowPlayingList.isEmpty()))
				localSong = (Song) this.mNowPlayingList
						.get(this.mPlayingItemPosition);
			return localSong;
		} catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
		{
			localIndexOutOfBoundsException.printStackTrace();
		}
		return localSong;
	}

	@Override
	public List<Song> getNowPlayingList()
	{
		List<Song> localList = new ArrayList<Song>();
		if (this.mNowPlayingList != null)
		{
			localList = this.mNowPlayingList;
		}
		return localList;
	}

	@Override
	public List<Song> getRecommendPlayList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Song makeOnlineSong(String paramString, Context paramContext)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void playOnlineSong(Song paramSong)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setNowPlayingList(List<Song> paramList, boolean paramBoolean)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public long addCurrentTrack2OnlineMusicTable(SongListItem paramSongListItem)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addCurrentTrack2RecentPlaylist(SongListItem paramSongListItem,
			long paramLong)
	{
		// TODO Auto-generated method stub

	}
}
