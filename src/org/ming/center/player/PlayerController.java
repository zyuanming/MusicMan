package org.ming.center.player;

public abstract interface PlayerController
{
	public static final String CMCCWM_MOBILEMUSIC_ACTION_PLAYER_START = "cmccwm.mobilemusic.action.PLAYER_START";

	public abstract void cancelPlaybackStatusBar();

	public abstract void clearNowPlayingList();

	public abstract boolean get51CHStatus();

	public abstract int getDuration();

	public abstract int getEQMode();

	public abstract boolean getIsLoadingData();

	public abstract int getNowPlayingItemPosition();

	public abstract int getNowPlayingNextItem();

	public abstract int getPosition();

	public abstract int getProgressDownloadPercent();

	public abstract int getRepeatMode();

	public abstract int getShuffleMode();

	public abstract int getTransId();

	public abstract boolean isFileOnExternalStorage();

	public abstract boolean isInitialized();

	public abstract boolean isInteruptByCall();

	public abstract boolean isPause();

	public abstract boolean isPlayEnd();

	public abstract boolean isPlayRecommendSong();

	public abstract boolean isPlaying();

	public abstract void loadAllLocalTracks2NowPlayingList();

	public abstract void next();

	public abstract boolean open(int paramInt);

	public abstract boolean openRecommendSong(int paramInt);

	public abstract void pause();

	public abstract void playOnlineSong(String paramString);

	public abstract void prev();

	public abstract boolean renewOnlinePlay(int paramInt);

	public abstract void seek(long paramLong);

	public abstract void set51CHStatus(boolean paramBoolean);

	public abstract void setEQMode(int paramInt);

	public abstract boolean setNextItem();

	public abstract void setNowPlayingItemPosition(int paramInt);

	public abstract int setRepeatMode(int paramInt);

	public abstract int setShuffleMode(int paramInt);

	public abstract void setTransId(int paramInt);

	public abstract void start();

	public abstract void stop();
}