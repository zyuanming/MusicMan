package org.ming.center.player;

import java.util.List;

import org.ming.center.database.Song;
import org.ming.center.http.item.SongListItem;

import android.content.Context;

public abstract interface PlayerController
{
	public static final String CMCCWM_MOBILEMUSIC_ACTION_PLAYER_START = "cmccwm.mobilemusic.action.PLAYER_START";

	public abstract int add2NowPlayingList(Song paramSong);

	public abstract int add2NowPlayingList(Song paramSong, boolean paramBoolean);

	public abstract int add2NowPlayingList(List<Song> paramList);

	public abstract long addCurrentTrack2OnlineMusicTable(
			SongListItem paramSongListItem);

	public abstract void addCurrentTrack2RecentPlaylist(
			SongListItem paramSongListItem, long paramLong);

	public abstract void addRecommendSongList(List<Song> paramList);

	public abstract void cancelPlaybackStatusBar();

	public abstract int checkSongInNowPlayingList(Song paramSong);

	public abstract void clearNowPlayingList();

	public abstract void delDownloadSong(Song paramSong);

	public abstract void delOnlineSong(Song paramSong);

	public abstract void delRadioSong(Song paramSong);

	public abstract boolean get51CHStatus();

	public abstract Song getCurrentPlayingItem();

	public abstract int getDuration();

	public abstract int getEQMode();

	public abstract boolean getIsLoadingData();

	public abstract int getNowPlayingItemPosition();

	public abstract List<Song> getNowPlayingList();

	public abstract int getNowPlayingNextItem();

	public abstract int getPosition();

	public abstract int getProgressDownloadPercent();

	public abstract List<Song> getRecommendPlayList();

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

	public abstract Song makeOnlineSong(String paramString, Context paramContext);

	public abstract void next();

	public abstract boolean open(int paramInt);

	// 自己加上的自定义方法
	public abstract boolean open2(int paramInt);

	public abstract boolean openRecommendSong(int paramInt);

	public abstract void pause();

	public abstract void playOnlineSong(Song paramSong);

	public abstract void playOnlineSong(String paramString);

	public abstract void prev();

	public abstract boolean renewOnlinePlay(int paramInt);

	public abstract void seek(long paramLong);

	public abstract void set51CHStatus(boolean paramBoolean);

	public abstract void setEQMode(int paramInt);

	public abstract boolean setNextItem();

	public abstract void setNowPlayingItemPosition(int paramInt);

	public abstract void setNowPlayingList(List<Song> paramList,
			boolean paramBoolean);

	public abstract int setRepeatMode(int paramInt);

	public abstract int setShuffleMode(int paramInt);

	public abstract void setTransId(int paramInt);

	public abstract void start();

	public abstract void stop();
}