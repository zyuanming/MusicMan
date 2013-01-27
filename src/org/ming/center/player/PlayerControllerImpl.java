package org.ming.center.player;

import java.util.List;

import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.Playlist;
import org.ming.center.database.Song;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.item.SongListItem;
import org.ming.center.system.SystemControllerImpl;
import org.ming.center.system.SystemEventListener;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.util.Log;

public class PlayerControllerImpl implements PlayerController,
		PlayerEventListener, SystemEventListener, MMHttpEventListener {
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
	private int mPayingNextItem = 0;
	private int mPlayerErrCount = 0;
	private int mPlayingItemPosition = 0;
	private int mRadioPageNo = 1;
	private int mRepeatMode = 0;
	private int mShuffleMode = 0;
	private int mTransId = -1;
	private long time_lastPress;
	private MusicPlayerWrapper wrapper;
	static {
		mIsplayEnd = false;
	}

	private PlayerControllerImpl(
			MobileMusicApplication paramMobileMusicApplication) {
		logger.v("PlayerControllerImpl() ---> Enter");
		this.mApp = paramMobileMusicApplication;
		this.mDBController = paramMobileMusicApplication.getController()
				.getDBController();
		this.mDispatcher = this.mApp.getEventDispatcher();
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
			MobileMusicApplication paramMobileMusicApplication) {
		logger.v("getInstance() ---> Enter");
		if (sInstance == null)
			sInstance = new PlayerControllerImpl(paramMobileMusicApplication);
		logger.v("getInstance() ---> Exit");
		return sInstance;
	}

	@Override
	public void handleMMHttpEvent(Message paramMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSystemEvent(Message paramMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePlayerEvent(Message paramMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelPlaybackStatusBar() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearNowPlayingList() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean get51CHStatus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEQMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getIsLoadingData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNowPlayingItemPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNowPlayingNextItem() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProgressDownloadPercent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRepeatMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getShuffleMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTransId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFileOnExternalStorage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInteruptByCall() {
		try {
			boolean bool = this.wrapper.isInteruptByCall();
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isPause() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPlayEnd() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPlayRecommendSong() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loadAllLocalTracks2NowPlayingList() {
		// TODO Auto-generated method stub

	}

	@Override
	public void next() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean open(int paramInt) {
		try {
			logger.v("open(position) ---> Enter");
			boolean bool1 = doHandOpen(paramInt);
			this.ISOPENLOCALMUSIC = true;
			bool1 = false;
			if (paramInt == -1)
				return bool1;
			if (Util.checkWapStatus())
				break;
			if ((this.mNowPlayingList == null)
					|| (this.mNowPlayingList.get(paramInt) == null)
					|| (((Song) this.mNowPlayingList.get(paramInt)).mUrl == null)
					|| ("<unknown>".equals(((Song) this.mNowPlayingList
							.get(paramInt)).mUrl))
					|| ("".equals(((Song) this.mNowPlayingList.get(paramInt)).mUrl)))
				break;
			if (((Song) this.mNowPlayingList.get(paramInt)).mUrl
					.contains("218.200.160.30")) {
				if (this.mRecommendPlayList != null) {
					this.mRecommendPlayList.clear();
					this.mRecommendPlayList = null;
					this.mPlayingItemPosition = 0;
				}
				this.wrapper.stopInternal();
				mIsCmwapToWlan = true;
				if ((CacheSongData.getInstance().getCacheSong() != null)
						&& (CacheSongData.getInstance().getCacheSong().mContentId
								.equals(((Song) this.mNowPlayingList
										.get(paramInt)).mContentId))) {
					playOnlineSong(CacheSongData.getInstance().getCacheXml());
					Log.v("cache", "begin playing cache db");
				} else {
					doHandOpen(paramInt);
				}
			}
		} catch (Exception e) {
		}
	}

	private boolean doHandOpen(int i) {
		boolean flag = false;
		if (mNowPlayingList != null && !mNowPlayingList.isEmpty()) {
			if (i < 0 || i > -1 + mNowPlayingList.size()) {
				logger.e("open(position), position is invalid");
				flag = false;
			} else {
				if (mRecommendPlayList != null) {
					mRecommendPlayList.clear();
					mRecommendPlayList = null;
					mPlayingItemPosition = 0;
				}
				setIsLoadingData(true);
				mDispatcher.sendMessage(mDispatcher.obtainMessage(4008));
				Song song = (Song) mNowPlayingList.get(i);
				flag = false;
				if (song != null) {
					mDispatcher.sendMessage(mDispatcher.obtainMessage(4010));
					if (mCurrentTask != null) {
						mHttpController.cancelTask(mCurrentTask);
						mCurrentTask = null;
					}
					if (song.mUrl == null
							|| song.mUrl.equalsIgnoreCase("<unknown>")) {
						mPlayingItemPosition = i;
						wrapper.stop();
						if (CacheSongData.getInstance().getCacheSong() != null
								&& CacheSongData.getInstance().getCacheSong().mContentId
										.equals(song.mContentId)) {
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
							Log.v("cache", "begin playing cache db");
						} else {
							askSongInfo(song);
						}
					} else {
						wrapper.start((Song) mNowPlayingList.get(i));
						mPlayingItemPosition = i;
						logger.v("open(position) ---> Exit");
					}
					flag = true;
				}
			}
		} else {
			logger.e("open(position), now playing list is empty");
		}
		return flag;
	}

	private void askSongInfo(Song paramSong) {
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
	public boolean openRecommendSong(int paramInt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void playOnlineSong(String paramString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prev() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean renewOnlinePlay(int paramInt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void seek(long paramLong) {
		// TODO Auto-generated method stub

	}

	@Override
	public void set51CHStatus(boolean paramBoolean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEQMode(int paramInt) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setNextItem() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNowPlayingItemPosition(int paramInt) {
		// TODO Auto-generated method stub

	}

	@Override
	public int setRepeatMode(int paramInt) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setShuffleMode(int paramInt) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTransId(int paramInt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public int add2NowPlayingList(Song paramSong) {
		try {
			int i = add2NowPlayingList(paramSong, false);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int add2NowPlayingList(Song paramSong, boolean paramBoolean) {
		int i = -1;
		try {
			logger.v("add2NowPlayingList() ---> Enter");
			if (paramSong == null)
				return i;
			if (this.mIsRadio) {
				this.mNowPlayingList.clear();
				this.mBackUpList.clear();
				Playlist localPlaylist = this.mDBController
						.getPlaylistByName(
								"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
								2);
				List localList = this.mDBController
						.getSongsFromMixPlaylist(localPlaylist.mExternalId);
				if (localList != null) {
					this.mNowPlayingList.addAll(localList);
					this.mBackUpList.addAll(localList);
				}
				this.mPlayingItemPosition = 0;
				this.mIsRadio = false;
			}
			if ((this.mRecommendPlayList != null)
					&& (this.mRecommendPlayList.size() > 0))
				paramBoolean = true;
			int j = this.mNowPlayingList.indexOf(paramSong);
			if (paramBoolean) {
				if (i == j) {
					this.mNowPlayingList.add(paramSong);
					this.mBackUpList.add(paramSong);
				} else {
					this.mNowPlayingList.remove(paramSong);
					this.mBackUpList.remove(paramSong);
				}
				this.mDispatcher.sendMessage(this.mDispatcher
						.obtainMessage(1023));
			}
			if (j <= this.mPlayingItemPosition) {
				int n = 1 + this.mPlayingItemPosition;
				int i1 = 1 + this.mPlayingItemPosition;
				if (this.mNowPlayingList.size() < 1 + this.mPlayingItemPosition)
					n = this.mNowPlayingList.size();
				this.mNowPlayingList.add(n, paramSong);
				if (this.mBackUpList.size() < 1 + this.mPlayingItemPosition)
					i1 = this.mBackUpList.size();
				this.mBackUpList.add(i1, paramSong);
				this.mPlayingItemPosition = (-1 + this.mPlayingItemPosition);
			}
		} finally {
		}
		this.mNowPlayingList.add(1 + this.mPlayingItemPosition, paramSong);
		this.mBackUpList.add(1 + this.mPlayingItemPosition, paramSong);
		if ((this.mNowPlayingList.size() == 0)
				|| (this.mPlayingItemPosition == -1
						+ this.mNowPlayingList.size())) {
			this.mNowPlayingList.add(paramSong);
			this.mBackUpList.add(paramSong);
		} else {
			int k = 1 + this.mPlayingItemPosition;
			int m = 1 + this.mPlayingItemPosition;
			if (this.mNowPlayingList.size() < 1 + this.mPlayingItemPosition)
				k = this.mNowPlayingList.size();
			this.mNowPlayingList.add(k, paramSong);
			if (this.mBackUpList.size() < 1 + this.mPlayingItemPosition)
				m = this.mBackUpList.size();
			this.mBackUpList.add(m, paramSong);
		}
		logger.v("add2NowPlayingList() ---> Exit");
		i = this.mNowPlayingList.indexOf(paramSong);
		return i;
	}

	@Override
	public int add2NowPlayingList(List<Song> paramList) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addRecommendSongList(List<Song> paramList) {
		// TODO Auto-generated method stub

	}

	@Override
	public int checkSongInNowPlayingList(Song paramSong) {
		logger.v("checkSongInNowPlayingList() ---> Enter");
		int ii = 0;
		if (paramSong == null) {
			ii = -1;
		} else {
			for (int i = 0; i < this.mNowPlayingList.size(); i++) {
				if (paramSong.equals((Song) this.mNowPlayingList.get(i))) {
					logger.d("Got the position is " + i);
					return i;
				}
			}
		}
		logger.v("checkSongInNowPlayingList() ---> Exit");
		return ii;
	}

	@Override
	public void delDownloadSong(Song paramSong) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delOnlineSong(Song paramSong) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delRadioSong(Song paramSong) {
		// TODO Auto-generated method stub

	}

	@Override
	public Song getCurrentPlayingItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Song> getNowPlayingList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Song> getRecommendPlayList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Song makeOnlineSong(String paramString, Context paramContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void playOnlineSong(Song paramSong) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNowPlayingList(List<Song> paramList, boolean paramBoolean) {
		// TODO Auto-generated method stub

	}

	@Override
	public long addCurrentTrack2OnlineMusicTable(SongListItem paramSongListItem) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addCurrentTrack2RecentPlaylist(SongListItem paramSongListItem,
			long paramLong) {
		// TODO Auto-generated method stub

	}
}
