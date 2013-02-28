package org.ming.center.player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.MusicType;
import org.ming.center.database.Playlist;
import org.ming.center.database.Song;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.http.item.SongListItem;
import org.ming.center.system.SystemControllerImpl;
import org.ming.center.system.SystemEventListener;
import org.ming.dispatcher.Dispatcher;
import org.ming.dispatcher.DispatcherEventEnum;
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
	private MMHttpTask mGetRadioSongTask;
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
		logger.v("handleSystemEvent() ---> Enter");
		logger.v("message: " + paramMessage);
		switch (paramMessage.what)
		{
		default:
		{
			logger.v("handleSystemEvent() ---> Exit");
			return;
		}
		case DispatcherEventEnum.SYSTEM_EVENT_MEDIA_BUTTON_NEXT:
		{
			if (System.currentTimeMillis() - time_lastPress > 500L)
			{
				time_lastPress = System.currentTimeMillis();
				next();
			}
		}
			break;
		case DispatcherEventEnum.SYSTEM_EVENT_MEDIA_BUTTON_PREV:
		{
			if (System.currentTimeMillis() - time_lastPress > 500L)
			{
				time_lastPress = System.currentTimeMillis();
				prev();
			}
		}
			break;
		case DispatcherEventEnum.SYSTEM_EVENT_MEDIA_BUTTON_PLAY_PAUSE:
		{
			if (isPlaying())
				pause();
			else if (isPause())
				start();
			else
				open(mPlayingItemPosition);
		}
			break;
		case DispatcherEventEnum.SYSTEM_EVENT_MEDIA_BUTTON_STOP:
		{
			stop();
		}
			break;
		case DispatcherEventEnum.SYSTEM_EVENT_MEDIA_EJECT:
		{
			if (getCurrentPlayingItem() != null
					&& wrapper.isFileOnExternalStorage())
			{
				stop();
				wrapper.close();
			}
			mFullPlayList.clear();
		}
			break;
		case DispatcherEventEnum.SYSTEM_EVENT_MEDIA_MOUNTED:
		{
			asyncLoadFullList();
		}
			break;
		case DispatcherEventEnum.SYTEM_EVENT_FINISH_ALL_ACTIVITIES:
		{
			stop();
		}
			break;
		case DispatcherEventEnum.SYTEM_EVENT_PLAY_PAUSE:
		{
			if (isPlaying())
				pause();
		}
			break;
		}
	}

	private void asyncLoadFullList()
	{
		mApp.getEventDispatcher().post(new Runnable()
		{
			public void run()
			{
				mFullPlayList = mDBController.getAllSongs(null);
			}
		});
	}

	@Override
	public void handlePlayerEvent(Message message)
	{
		logger.v("handlePlayerEvent() ---> Enter");
		switch (message.what)
		{
		case DispatcherEventEnum.PLAYER_EVENT_BUFFER_UPDATED:
		case DispatcherEventEnum.PLAYER_EVENT_META_CHANGED:
		case DispatcherEventEnum.PLAYER_EVENT_PREPARE_START:
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_PAUSE:
		case DispatcherEventEnum.PLAYER_EVENT_NO_RIGHTS_LISTEN_ONLINE_LISTEN:
		case DispatcherEventEnum.PLAYER_EVENT_WAP_CLOSED:
		case DispatcherEventEnum.PLAYER_EVENT_NO_LOGIN_LISTEN:
		default:
		{
			logger.v("handlePlayerEvent() ---> Exit");
			return;
		}
		case DispatcherEventEnum.PLAYER_EVENT_PREPARED_ENDED:
		{
			onPlayerPrepareEnded();
		}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_TRACK_ENDED:
		{
			cancelPlaybackStatusBar();
			onTrackEnded();
		}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_DUBI_NOTCMCCMUSIC:
		{
			((Song) mNowPlayingList.get(mPlayingItemPosition)).isDolby = false;
			open(mPlayingItemPosition);
		}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_SPACENOTFILL_ERROR:
		{
			setIsLoadingData(false);
			stop();
		}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_ERROR_OCCURED:
		case DispatcherEventEnum.PLAYER_EVENT_NETWORK_ERROR:
		{
			setIsLoadingData(false);
			onErrorOccuered(message);
		}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_START:
		{
			setIsLoadingData(false);
		}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_STOP:
		{
			cancelPlaybackStatusBar();
		}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_RETRY_PLAY:
		{
			open(mPlayingItemPosition);
		}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_RETRY_DOWNLOAD:
		{
			if (wrapper != null)
				wrapper.retryDowmload();
		}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_NOTFOUND_MUSIC:
		{
			Song song = getCurrentPlayingItem();
			song.mUrl = "<unknown>";
			askSongInfo(song);
		}
			break;
		}
	}

	private void onPlayerPrepareEnded()
	{
		logger.v("onPlayerPrepareEnded() ---> Enter");
		this.mPlayerErrCount = 0;
		logger.v("onPlayerPrepareEnded() ---> Exit");
	}

	private void onTrackEnded()
	{
		logger.v("onTrackEnded() ---> Enter");
		if (mIsRadio)
		{
			mPlayingItemPosition = mPayingNextItem;
			open(mPlayingItemPosition);
			if (mNowPlayingList.size() - mPlayingItemPosition < 5)
				requestRadioSongInfo();
		} else
		{
			if (mApp.getController().getDBController().getRepeatMode() != 1)
				mPlayingItemPosition = mPayingNextItem;
			open(mPlayingItemPosition);
			logger.v("onTrackEnded() ---> Exit");
		}
	}

	private void onErrorOccuered(Message message)
	{
		logger.v("onErrorOccuered() ---> Enter");
		logger.e((new StringBuilder("player report error: ")).append(
				message.arg1).toString());
		cancelPlaybackStatusBar();
		int i = mPlayerErrCount;
		mPlayerErrCount = i + 1;
		if (i < 5 && NetUtil.isConnection())
			next();
		logger.v("onErrorOccuered() ---> Exit");
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

	// 还有bug没有清理--------------》 等待清理
	@Override
	public void next()
	{
		try
		{
			logger.v("next() ---> Enter");
			if (this.mRecommendPlayList != null)
			{
				int j = this.mRecommendPlayList.size();
				if ((j == 0) || (this.mPlayingItemPosition < 0))
				{
					this.mRecommendPlayList = null;
					return;
				} else
				{
					this.mPlayingItemPosition = (1 + this.mPlayingItemPosition);
					if (this.mPlayingItemPosition > j - 1)
						this.mPlayingItemPosition = 0;
					openRecommendSong(this.mPlayingItemPosition);
					return;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (this.mIsRadio)
		{
			int i = this.mNowPlayingList.size();
			if (i > 0)
			{
				this.mPlayingItemPosition = (1 + this.mPlayingItemPosition);
				if (this.mPlayingItemPosition > i - 1)
					this.mPlayingItemPosition = 0;
				open(this.mPlayingItemPosition);
			}
			if ((i - this.mPlayingItemPosition < 5)
					&& (this.mGetRadioSongTask == null))
				requestRadioSongInfo();
		} else
		{
			this.mPlayingItemPosition = this.mPayingNextItem;
			open(this.mPlayingItemPosition);
		}
		logger.v("next() ---> Exit");
	}

	/**
	 * 开始播放音乐
	 */
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
		logger.v("open(position) ---> Exit");
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
				mDispatcher
						.sendMessage(mDispatcher
								.obtainMessage(DispatcherEventEnum.UI_EVENT_PLAY_NEWSONG));
				Song song = (Song) mNowPlayingList.get(i);
				flag = false;
				if (song != null)
				{
					mDispatcher
							.sendMessage(mDispatcher
									.obtainMessage(DispatcherEventEnum.UI_EVENT_DOWNSONGINF_START));
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
							if (NetUtil.netState == 3
									&& !SystemControllerImpl.getInstance(mApp)
											.checkWapStatus()
									|| !NetUtil.isConnection())
								mApp.getEventDispatcher()
										.sendMessage(
												mApp.getEventDispatcher()
														.obtainMessage(
																DispatcherEventEnum.PLAYER_EVENT_WAP_CLOSED));
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
		// for (int i = 1002;; i = 5005)
		// {
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
		logger.v("openRecommendSong(i) ---> Enter");
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
					logger.v("openRecommendSong(i) ---> Exit");
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

	/**
	 * 播放在线音乐
	 */
	@Override
	public void playOnlineSong(String s)
	{
		Song song = makeOnlineSong(s, MobileMusicApplication.getInstance());
		if (song != null)
		{
			if (mIsCmwapToWlan)
			{
				queryMonth(song);
				int i = mNowPlayingList.indexOf(song);
				if (-1 != i)
				{
					mNowPlayingList.remove(i);
					mNowPlayingList.add(i, song);
					open(i);
				}
				mIsCmwapToWlan = false;
			} else if (-1 != mNowPlayingList.indexOf(song))
			{
				queryMonth(song);
				playOnlineSong(song);
			}
		} else
		{
			setNextItem();
		}

	}

	private void queryMonth(Song song)
	{
		logger.v("nw---->queryMonth");
		char c;
		MMHttpRequest mmhttprequest;
		String s;
		long l;
		String s1;
		String s2;
		if (NetUtil.isNetStateWap())
			c = '\u0421';
		else
			c = '\u13C5';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		s = GlobalSettingParameter.SERVER_INIT_PARAM_MDN;
		l = System.currentTimeMillis();
		s1 = (new SimpleDateFormat("yyyyMMddhhmmss")).format(Long.valueOf(l));
		s2 = Util.getRandKey(s, s1, song.mContentId);
		mmhttprequest.addHeader("mdn",
				GlobalSettingParameter.SERVER_INIT_PARAM_MDN);
		mmhttprequest.addHeader("mode", "chinamobile");
		mmhttprequest.addHeader("timestep", s1);
		mmhttprequest.addHeader("randkey", s2);
		mmhttprequest.addUrlParams("ua",
				GlobalSettingParameter.LOCAL_PARAM_USER_AGENT);
		mmhttprequest.addUrlParams("contentid", song.mContentId);
		mmhttprequest.addUrlParams("groupcode", song.mGroupCode);
		if (Util.isDolby(song))
			mmhttprequest.addUrlParams("size", String.valueOf(song.mSize3));
		else if (NetUtil.isNetStateWLAN())
			mmhttprequest.addUrlParams("size", String.valueOf(song.mSize2));
		else
			mmhttprequest.addUrlParams("size", String.valueOf(song.mSize));
		mCurrentTask = mHttpController.sendRequest(mmhttprequest);
	}

	public void playOnlineSong(Song song)
	{
		Song song1 = getCurrentPlayingItem();
		if (song1 == null
				|| song1.mUrl != null
				&& (song1.mUrl == null || !song1.mUrl
						.equalsIgnoreCase("<unknown>")))
		{
			if (song1 != null && song1.mUrl != null && song.mUrl != null
					&& !song1.mUrl.equalsIgnoreCase(song.mUrl))
			{
				int j = add2NowPlayingList(song);
				if (isPlayRecommendSong())
					openRecommendSong(j);
				else
					open(j);
				addCurrentTrack2OnlineMusicTable();
				addCurrentTrack2RecentPlaylist();
			} else if (song1 == null)
			{
				int i = add2NowPlayingList(song);
				if (isPlayRecommendSong())
					openRecommendSong(i);
				else
					open(i);
				addCurrentTrack2OnlineMusicTable();
				addCurrentTrack2RecentPlaylist();
			}
		} else
		{
			song1.limit1 = song.limit1;
			song1.limit2 = song.limit2;
			song1.mAlbum = song.mAlbum;
			song1.mAlbumId = song.mAlbumId;
			song1.mArtist = song.mArtist;
			song1.mArtUrl = song.mArtUrl;
			song1.mContentId = song.mContentId;
			song1.mDuration = song.mDuration;
			song1.mGroupCode = song.mGroupCode;
			song1.mLyric = song.mLyric;
			song1.mPoint = song.mPoint;
			song1.mSize = song.mSize;
			song1.mSize2 = song.mSize2;
			song1.mTrack = song.mTrack;
			song1.mUrl = song.mUrl;
			song1.mUrl2 = song.mUrl2;
			song1.mUrl3 = song.mUrl3;
			song1.mMusicType = MusicType.ONLINEMUSIC.ordinal();
			song1.isDolby = song.isDolby;
			updateCurrentTrack2OnlineMusicTable();
			if (isPlayRecommendSong())
				openRecommendSong(mPlayingItemPosition);
			else
				open(mPlayingItemPosition);
		}
	}

	private void updateCurrentTrack2OnlineMusicTable()
	{
		Song localSong;
		long l;
		logger.v("updateCurrentTrack2OnlineMusicTable() ---> Enter");
		localSong = getCurrentPlayingItem();
		if (localSong != null)
		{
			boolean bool = Util.isOnlineMusic(localSong);
			if (bool)
			{
				l = this.mDBController.updateOnlineMusicItem(localSong);
				if (l == -1L)
				{
					logger.d("Fail to add song to DB.");
				} else
				{
					localSong.mId = l;
					logger.d("================================");
					logger.d("Item id: " + localSong.mId);
					logger.d("Item name: " + localSong.mTrack);
					logger.d("Item artist: " + localSong.mArtist);
					logger.d("================================");
					logger.v("updateCurrentTrack2OnlineMusicTable() ---> Exit");
				}
			}
		}
	}

	private void addCurrentTrack2RecentPlaylist()
	{
		logger.v("addCurrentTrack2RecentPlaylist() ---> Enter");
		Song localSong = getCurrentPlayingItem();
		if (localSong == null)
			logger.e("Current playing item is null !!");
		else
		{
			Playlist localPlaylist = this.mDBController
					.getPlaylistByName(
							"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
							2);
			if (!this.mDBController.isSongInMixPlaylist(
					localPlaylist.mExternalId, localSong.mId,
					Util.isOnlineMusic(localSong)))
			{
				if (this.mDBController.countSongNumInPlaylist(
						localPlaylist.mExternalId, 2) >= 20)
				{
					long l2 = this.mDBController.getFirstSongInPlaylist(
							localPlaylist.mExternalId, 2);
					if (l2 != -1L)
						this.mDBController
								.deleteSongsFromMixPlaylist(
										localPlaylist.mExternalId,
										new long[] { l2 }, 2);
				}
				DBController localDBController = this.mDBController;
				long l1 = localPlaylist.mExternalId;
				long[] arrayOfLong = new long[1];
				arrayOfLong[0] = localSong.mId;
				localDBController.addSongs2MixPlaylist(l1, arrayOfLong,
						Util.isOnlineMusic(localSong));
			}
			logger.v("addCurrentTrack2RecentPlaylist() ---> Exit");
		}
	}

	private void addCurrentTrack2OnlineMusicTable()
	{
		Song localSong;
		long l;
		logger.v("addCurrentTrack2OnlineMusicTable() ---> Enter");
		localSong = getCurrentPlayingItem();
		if ((localSong == null) || (!Util.isOnlineMusic(localSong)))
		{
			logger.e("Should NOT record local music!!!");
		} else
		{
			l = this.mDBController.addOnlineMusicItem(localSong);
			if (l == -1L)
			{
				logger.d("Fail to add song to DB.");
			} else
			{
				localSong.mId = l;
				logger.d("================================");
				logger.d("Item id: " + localSong.mId);
				logger.d("Item name: " + localSong.mTrack);
				logger.d("Item artist: " + localSong.mArtist);
				logger.d("================================");
				logger.v("addCurrentTrack2OnlineMusicTable() ---> Exit");
			}
		}
	}

	@Override
	public void prev()
	{
		try
		{
			logger.v("prev() ---> Enter");
			if (mRecommendPlayList != null)
			{
				if ((mPlayingItemPosition < 0)
						|| (mPlayingItemPosition > -1
								+ mRecommendPlayList.size()))
					logger.e("next(), error position");
				else if (mPlayingItemPosition == 0)
					mPlayingItemPosition = (-1 + mRecommendPlayList.size());
				else
					mPlayingItemPosition = -1 + mPlayingItemPosition;
				openRecommendSong(mPlayingItemPosition);
			} else
			{
				if ((this.mPlayingItemPosition < 0)
						|| (this.mPlayingItemPosition > -1
								+ this.mNowPlayingList.size()))
					logger.e("next(), error position");
				else if (mPlayingItemPosition == 0)
					mPlayingItemPosition = -1 + mNowPlayingList.size();
				else
					mPlayingItemPosition = -1 + mPlayingItemPosition;
				open(mPlayingItemPosition);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		logger.v("prev() ---> Exit");
	}

	@Override
	public boolean renewOnlinePlay(int paramInt)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void seek(long l)
	{
		logger.v("seek() ---> Enter");
		wrapper.seekTo((int) l);
		logger.v("seek() ---> Enter");
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
		boolean flag = false;
		mPayingNextItem = mPlayingItemPosition;
		if (!mIsRadio)
		{
			if (mShuffleMode == 0)
			{
				if (sequentPlayRepeatAll())
				{
					flag = true;
				} else
				{
					flag = false;
				}
			}
			if (shufflePlay())
			{
				flag = true;
			} else
			{
				flag = false;
			}
		} else
		{
			sequentPlayRepeatAll();
			flag = true;
		}
		return false;
	}

	private boolean shufflePlay()
	{
		logger.v("shufflePlay() ---> Enter");
		boolean flag = false;
		switch (this.mRepeatMode)
		{
		default:
			return flag;
		case 0:
		{
			flag = shufflePlayRepeatNone();
		}
			break;
		case 1:
		{
			flag = playCurrent();
		}
			break;
		case 2:
		{
			flag = shufflePlayRepeateAll();
		}
			break;
		}
		return flag;
	}

	private boolean shufflePlayRepeatNone()
	{
		logger.v("shufflePlayRepeatNone() ---> Enter");
		int i = this.mPayingNextItem;
		int j = this.mNowPlayingList.size();
		boolean bool = false;
		if (i < j)
		{
			int k = this.mPayingNextItem;
			if (k >= 0)
			{
				Song localSong = (Song) this.mNowPlayingList
						.get(this.mPayingNextItem);
				this.mBackUpList.remove(localSong);
				logger.d("BackupList length: " + this.mBackUpList.size());
				int m = this.mBackUpList.size();
				if (m != 0)
				{
					int n = (new Random().nextInt() >>> 1) % m;
					this.mPayingNextItem = this.mNowPlayingList
							.indexOf(this.mBackUpList.get(n));
					logger.i("shufflePlayRepeatNone(), selected NO."
							+ this.mPayingNextItem + " track to play!");
					logger.v("shufflePlayRepeatNone() ---> Exit");
					bool = true;
				}
			}
		}
		return bool;
	}

	private boolean shufflePlayRepeateAll()
	{
		int i = 1;
		boolean flag = true;
		logger.v("shufflePlayRepeateAll() ---> Enter");
		int j = this.mNowPlayingList.size();
		int k = this.mPayingNextItem;
		Random localRandom;
		if (j > i)
		{
			localRandom = new Random();
			while (k == this.mPayingNextItem)
			{
				k = (localRandom.nextInt() >>> 1) % j;
			}
		} else
		{
			if ((k < 0) || (k >= this.mBackUpList.size())
					|| (this.mNowPlayingList.size() <= 0))
			{
				flag = false;
			} else
			{
				this.mPayingNextItem = this.mNowPlayingList
						.indexOf(this.mBackUpList.get(k));
				logger.i("shufflePlayRepeatNone(), selected NO."
						+ this.mPayingNextItem + " track to play!");
			}
		}
		logger.v("shufflePlayRepeateAll() ---> Exit");
		return flag;
	}

	private boolean playCurrent()
	{
		logger.v("playCurrent() ---> Enter");
		logger.v("playCurrent() ---> Exit");
		return true;
	}

	private boolean sequentPlayRepeatAll()
	{
		boolean bool = false;
		try
		{
			logger.v("sequentPlayRepeatAll() ---> Enter");
			int i = this.mNowPlayingList.size();

			if (i != 0)
			{
				int j = this.mPayingNextItem;
				bool = false;
				if (j >= 0)
				{
					this.mPayingNextItem = (1 + this.mPayingNextItem);
					if (this.mPayingNextItem > i - 1)
						this.mPayingNextItem = 0;
					logger.v("sequentPlayRepeatAll() ---> Exit");
					bool = true;
				}
			}
			return bool;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return bool;
	}

	@Override
	public void setNowPlayingItemPosition(int paramInt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int setRepeatMode(int paramInt)
	{
		logger.v("setRepeatMode() ---> Enter");
		if (paramInt == 0)
		{
			this.mRepeatMode = 0;
		} else
		{
			if (paramInt == 2)
			{
				this.mRepeatMode = 2;
				this.mBackUpList.clear();
				this.mBackUpList.addAll(this.mNowPlayingList);
			}
			if (paramInt == 1)
			{
				this.mRepeatMode = 1;
			} else
			{
				this.mRepeatMode = 0;
			}
		}
		try
		{
			this.mDBController.setRepeatMode(this.mRepeatMode);
			if (this.mRepeatMode == 1)
			{
				logger.i("setRepeatMode(), repeat mode is repeat_current, so change shuffle mode to close");
				this.mShuffleMode = 0;
				this.mDBController.setShuffleMode(this.mShuffleMode);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			logger.e("setRepeatMode(), Error: unknown shuffle mode");
		}
		this.mDispatcher.sendMessage(this.mDispatcher
				.obtainMessage(DispatcherEventEnum.PLAYER_EVENT_REPEAT_MODE));
		logger.v("setRepeatMode() ---> Exit");
		int i = this.mRepeatMode;
		return i;
	}

	@Override
	public int setShuffleMode(int paramInt)
	{
		logger.v("setShuffleMode() ---> Enter");
		if (paramInt == 0)
		{
			this.mShuffleMode = 0;
			if (this.mRepeatMode == 1)
			{
				logger.i("setShuffleMode(), repeat mode is repeat_current, so change shuffle mode to close");
				this.mShuffleMode = 0;
			}
			this.mDBController.setShuffleMode(this.mShuffleMode);

		} else if (paramInt == 1)
		{
			this.mShuffleMode = 1;
		}
		logger.v("setShuffleMode() ---> Exit");
		int i = this.mShuffleMode;
		return i;
	}

	@Override
	public void setTransId(int paramInt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void start()
	{
		logger.v("start() ---> Enter");
		this.wrapper.resumeOrRestart();
		logger.v("start() ---> Exit");
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
		logger.d("add2NowPlayingList(song) ----> enter");
		try
		{
			int i = add2NowPlayingList(paramSong, false);
			logger.d("i ----> " + i);
			logger.d("add2NowPlayingList(song) ----> exit");
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
		logger.d("add2NowPlayingList(song, flag) ---> Enter");
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
			logger.d("the song in the position of NowPlayingList is " + j);
			if (!flag)
			{
				if (i != j) // 如果当前播放列表中有这首歌
				{
					mNowPlayingList.remove(song);
					mBackUpList.remove(song);
					logger.d("mPlayingItemPosition ----> "
							+ mPlayingItemPosition);
					if (j > mPlayingItemPosition)
					{
						mNowPlayingList.add(1 + mPlayingItemPosition, song);
						mBackUpList.add(1 + mPlayingItemPosition, song);
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

					}
				} else if (i == j)
				{
					logger.d("mNowPlayingList do not has the song , now add the song");
					mNowPlayingList.add(song);
					mBackUpList.add(song);
				}
			} else
			{
				if (i == j) // 当前的播放列表没有这首歌
				{
					this.mNowPlayingList.add(song);
					this.mBackUpList.add(song);
					mDispatcher
							.sendMessage(mDispatcher
									.obtainMessage(DispatcherEventEnum.PLAYER_EVENT_SONGLIST_CHANGE));
				}
				// else
				// // 当前的播放列表中存在这首歌
				// {
				// if ((this.mNowPlayingList.size() == 0)
				// || (this.mPlayingItemPosition == -1
				// + this.mNowPlayingList.size()))
				// {
				// this.mNowPlayingList.add(song);
				// this.mBackUpList.add(song);
				// } else
				// {
				// int k = 1 + this.mPlayingItemPosition;
				// int m = 1 + this.mPlayingItemPosition;
				// if (this.mNowPlayingList.size() < 1 +
				// this.mPlayingItemPosition)
				// k = this.mNowPlayingList.size();
				// this.mNowPlayingList.add(k, song);
				// if (this.mBackUpList.size() < 1 + this.mPlayingItemPosition)
				// m = this.mBackUpList.size();
				// this.mBackUpList.add(m, song);
				// }
				// }
			}
		}
		i = mNowPlayingList.indexOf(song);
		logger.d("i ----> " + i);
		logger.v("add2NowPlayingList(song, flag) ---> Exit");
		return i;
	}

	@Override
	public int add2NowPlayingList(List<Song> list)
	{
		logger.v("add2NowPlayingList(list) ---> Enter");
		int i = 0;
		if (list != null)
		{
			int j = list.size();
			if (j != 0)
			{
				int l;
				int k = mNowPlayingList.size();
				Song song = null;
				if (k > 0)
					song = (Song) mNowPlayingList.get(mPlayingItemPosition);
				if (mIsRadio)
				{
					mNowPlayingList.clear();
					mBackUpList.clear();
					Playlist playlist = mDBController
							.getPlaylistByName(
									"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
									2);
					List list1 = mDBController
							.getSongsFromMixPlaylist(playlist.mExternalId);
					if (list1 != null)
					{
						mNowPlayingList.addAll(list1);
						mBackUpList.addAll(list1);
					}
					mIsRadio = false;
				}
				mNowPlayingList.removeAll(list);
				mBackUpList.removeAll(list);
				mNowPlayingList.addAll(list);
				mBackUpList.addAll(list);
				if (song != null)
					mPlayingItemPosition = mNowPlayingList.indexOf(song);
				mDispatcher.sendMessage(mDispatcher.obtainMessage(1023));
				l = mNowPlayingList.indexOf(list.get(0));
				i = l;
			}
		} else
		{
			i = -1;
		}
		logger.v("return ----- " + i);
		logger.v("add2NowPlayingList(list) ---> Exit");
		return i;
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
		while (true)
		{
			int i = 0;
			if (paramSong == null)
			{
				i = -1;
				logger.v("the song is null");
				logger.v("checkSongInNowPlayingList() ---> Exit");
				return i;
			}
			if (i >= this.mNowPlayingList.size())
			{
				logger.v("no song in NowPlayingList !!!");
				logger.v("checkSongInNowPlayingList() ---> Exit");
				i = -1;
				return i;
			}
			if (paramSong.equals((Song) this.mNowPlayingList.get(i)))
			{
				logger.d("Got the position is " + i);
				logger.v("checkSongInNowPlayingList() ---> Exit");
				return i;
			}
			i++;
		}
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
		List<Song> localList = this.mRecommendPlayList;
		return localList;
	}

	@Override
	public Song makeOnlineSong(String paramString, Context paramContext)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void requestRadioSongInfo()
	{
		if (mNowPlayingList.size() != 0)
		{
			char c;
			MMHttpRequest mmhttprequest;
			Song song;
			if (NetUtil.isNetStateWap())
				c = '\u03ED';
			else
				c = '\u1390';
			mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
			song = (Song) mNowPlayingList.get(0);
			mmhttprequest.addUrlParams("itemcount",
					GlobalSettingParameter.SERVER_INIT_PARAM_ITEM_COUNT);
			mmhttprequest.addUrlParams("groupcode", song.mGroupCode);
			mmhttprequest.addUrlParams("pageno", String.valueOf(mRadioPageNo));
			mGetRadioSongTask = mHttpController.sendRequest(mmhttprequest);
		}
	}

	@Override
	public void setNowPlayingList(List<Song> paramList, boolean flag)
	{
		logger.v("setNowPlayingList() ---> Enter");
		if (flag)
		{
			this.mRadioPageNo = 1;
			this.mRadioPageNo = (1 + this.mRadioPageNo);
			if (this.mGetRadioSongTask != null)
			{
				this.mHttpController.cancelTask(this.mGetRadioSongTask);
				this.mGetRadioSongTask = null;
			}
			this.mNowPlayingList.clear();
			this.mBackUpList.clear();
			this.mNowPlayingList.addAll(paramList);
			this.mBackUpList.addAll(paramList);
			requestRadioSongInfo();
		} else
		{
			if (this.mIsRadio)
			{
				this.mNowPlayingList.clear();
				this.mBackUpList.clear();
				Playlist localPlaylist = this.mDBController
						.getPlaylistByName(
								"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
								2);
				List localList = this.mDBController
						.getSongsFromMixPlaylist(localPlaylist.mExternalId);
				if (localList != null)
				{
					this.mNowPlayingList.addAll(localList);
					this.mBackUpList.addAll(localList);
				}
				this.mPlayingItemPosition = this.mNowPlayingList.size();
				this.mNowPlayingList.addAll(paramList);
				this.mBackUpList.addAll(paramList);
			}
		}
		mIsRadio = flag;
		mDispatcher
				.sendMessage(mDispatcher
						.obtainMessage(DispatcherEventEnum.PLAYER_EVENT_SONGLIST_CHANGE));
		logger.v("setNowPlayingList() ---> Exit");
	}

	@Override
	public long addCurrentTrack2OnlineMusicTable(SongListItem songlistitem)
	{
		long l;
		logger.v("addCurrentTrack2OnlineMusicTable() ---> Enter");
		if (songlistitem != null)
		{
			l = mDBController.addOnlineMusicItem(songlistitem);
			if (l == -1L)
				logger.d("Fail to add song to DB.");
			logger.v("addCurrentTrack2OnlineMusicTable() ---> Exit");
		} else
		{
			logger.e("Should NOT record local music!!!");
			l = -1L;
		}
		return l;
	}

	@Override
	public void addCurrentTrack2RecentPlaylist(SongListItem songlistitem, long l)
	{
		boolean flag;
		logger.v("addCurrentTrack2RecentPlaylist() ---> Enter");
		if (songlistitem != null)
		{
			Playlist playlist;
			DBController dbcontroller;
			long l1;
			playlist = mDBController
					.getPlaylistByName(
							"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
							2);
			dbcontroller = mDBController;
			l1 = playlist.mExternalId;
			if (songlistitem.mMusicType != MusicType.ONLINEMUSIC.ordinal())
			{
				flag = false;
				if (dbcontroller.isSongInMixPlaylist(l1, l, flag))
				{
					logger.v("addCurrentTrack2RecentPlaylist() ---> Exit");
				} else
				{
					DBController dbcontroller1;
					long l2 = playlist.mExternalId;
					long al[] = (new long[] { l });
					boolean flag1 = false;
					if (mDBController.countSongNumInPlaylist(
							playlist.mExternalId, 2) >= 20)
					{
						long l3 = mDBController.getFirstSongInPlaylist(
								playlist.mExternalId, 2);
						if (l3 != -1L)
							mDBController.deleteSongsFromMixPlaylist(
									playlist.mExternalId, new long[] { l3 }, 2);
					}
					dbcontroller1 = mDBController;
					if (songlistitem.mMusicType == MusicType.ONLINEMUSIC
							.ordinal())
					{
						flag1 = true;
					}
					dbcontroller1.addSongs2MixPlaylist(l2, al, flag1);
				}
			} else
			{
				flag = true;
			}
		} else
		{
			logger.e("Current playing item is null !!");
		}

	}
}
