package org.ming.center.player;

import java.util.List;

import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.Song;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.system.SystemEventListener;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;

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
	private int mPayingNextItem = 0;
	private int mPlayerErrCount = 0;
	private int mPlayingItemPosition = 0;
	private int mRadioPageNo = 1;
	private int mRepeatMode = 0;
	private int mShuffleMode = 0;
	private int mTransId = -1;
	private long time_lastPress;

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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int add2NowPlayingList(Song paramSong, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return 0;
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
}
