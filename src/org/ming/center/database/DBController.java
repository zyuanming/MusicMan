package org.ming.center.database;

import java.util.List;
import java.util.Set;

import android.database.Cursor;
import android.net.Uri;

public abstract interface DBController {
	public abstract long addCacheData(String paramString1, String paramString2,
			String paramString3, String paramString4);

	public abstract long addContentId(String paramString1, String paramString2);

	public abstract long addMusicRadioGarbage(String paramString1,
			String paramString2);

	public abstract long addMusicRate(String paramString, int paramInt);

	public abstract boolean addSongs2MixPlaylist(long paramLong,
			long[] paramArrayOfLong, boolean paramBoolean);

	public abstract boolean addSongs2Playlist(long paramLong,
			long[] paramArrayOfLong, int paramInt);

	public abstract void closeDB();

	public abstract Playlist getPlaylistByName(String paramString, int paramInt);

	public abstract int countSongNumInPlaylist(long paramLong, int paramInt);

	public abstract long createPlaylist(String paramString, int paramInt);

	public abstract void delAllLcSongsFromPlaylist(long paramLong);

	public abstract boolean deleteAllSongsFromMixPlaylist(long paramLong,
			int paramInt);

	public abstract int deleteCacheDataByGroupCode(String paramString);

	public abstract void deleteDBDlItemById(long paramLong);

	public abstract void deleteDBDlItemByPath(String paramString);

	public abstract void deletePlaylist(long paramLong, int paramInt);

	public abstract void deleteSongFromDB(long paramLong);

	public abstract void deleteSongFromPlaylist(long paramLong1,
			long paramLong2, int paramInt);

	public abstract boolean deleteSongsFromMixPlaylist(long paramLong,
			long[] paramArrayOfLong, int paramInt);

	public abstract boolean deleteSongsFromPlaylist(long paramLong,
			long[] paramArrayOfLong, int paramInt);

	public abstract int deleteUpdatePackage(String paramString);

	public abstract boolean get51CHStatus();

	public abstract List<Song> getSongsFromCursor(Cursor paramCursor,
			int paramInt);

	public abstract Cursor getAllSongs(Uri paramUri, String[] paramArrayOfString);

	public abstract int getAllSongsCountByFolder(String[] paramArrayOfString,
			boolean paramBoolean);

	public abstract int getAllSongsCountByFolderAndSinger(
			String[] paramArrayOfString, int paramInt, boolean paramBoolean);

	public abstract int getArtistCountByFolder(String[] paramArrayOfString,
			boolean paramBoolean);

	public abstract Cursor getArtistsByCursor(String[] paramArrayOfString);

	public abstract String getChannelId();

	public abstract boolean getCheckOlderVersion();

	public abstract Cursor getCursorFromPlaylist(long paramLong, int paramInt);

	public abstract List<String> getDataByGroupcode(String paramString);

	public abstract String getDisplayedAlbumName(String paramString);

	public abstract String getDisplayedArtistName(String paramString);

	public abstract boolean getDownLoad_AutoRecover();

	public abstract int getEQMode();

	public abstract long getFirstSongInPlaylist(long paramLong, int paramInt);

	public abstract String getLocalFolder();

	public abstract int getMusicRate(String paramString);

	public abstract int getRepeatMode();

	public abstract boolean getScanSmallSongFile();

	public abstract int getShuffleMode();

	public abstract String getSkinStyleName();

	public abstract Set<String> getSongFolder();

	public abstract long getSongIdByContentId(String paramString1,
			String paramString2);

	public abstract long getSongIdByPath(String paramString);

	public abstract Cursor getSongsCursorByFolder(String[] paramArrayOfString,
			boolean paramBoolean);

	public abstract Cursor getSongsCursorByFolderAndSinger(
			String[] paramArrayOfString, int paramInt, boolean paramBoolean);

	public abstract Cursor getSongsFromAlbum(Uri paramUri, long paramLong,
			String[] paramArrayOfString);

	public abstract Cursor getSongsFromArtist(Uri paramUri, long paramLong,
			String[] paramArrayOfString);

	public abstract Cursor getSongsFromGenre(String paramString,
			long paramLong, String[] paramArrayOfString);

	public abstract long[] getSongsIdFromFilePath(String paramString);

	public abstract String getSubChannelId();

	public abstract List<Playlist> getAllPlaylists(int paramInt);

	public abstract boolean getTensileShows();

	public abstract boolean isCacheDataExist(String paramString);

	public abstract boolean isDefaultLocalPlaylist(String paramString);

	public abstract boolean isInMusicRadioGarbage(String paramString1,
			String paramString2);

	public abstract boolean isProtectedLocalPlaylist(String paramString);

	public abstract boolean isProtectedOnlinePlaylist(String paramString);

	public abstract boolean isRingTone(String paramString);

	public abstract boolean isSongInMixPlaylist(long paramLong1,
			long paramLong2, boolean paramBoolean);

	public abstract boolean isSongInPlaylist(long paramLong1, long paramLong2,
			int paramInt);

	public abstract long[] isSongInPlaylist(long paramLong, int paramInt);

	public abstract Cursor query(Uri paramUri, String[] paramArrayOfString1,
			String paramString1, String[] paramArrayOfString2,
			String paramString2);

	public abstract String queryCacheData(String paramString);

	public abstract String queryContentId(String paramString);

	public abstract Cursor queryDBDownloadList(Integer paramInteger);

	public abstract String queryDateByGroupCode(String paramString);

	public abstract int querySongIdByContentId(String paramString);

	public abstract Cursor querySongs(Uri paramUri, String paramString,
			String[] paramArrayOfString);

	public abstract int removeAllCacheData();

	public abstract void removeCacheData(String paramString);

	public abstract long renameDownloadMusic(String paramString1,
			String paramString2);

	public abstract void renameLocalSong(long paramLong, String paramString);

	public abstract void renamePlaylist(long paramLong, int paramInt,
			String paramString);

	public abstract void set51CHStatus(boolean paramBoolean);

	public abstract void setCheckOlderVersion(boolean paramBoolean);

	public abstract void setDownLoad_AutoRecover(Boolean paramBoolean);

	public abstract void setEQMode(int paramInt);

	public abstract List<Song> getSongsFromPlaylist(long paramLong, int paramInt);

	public abstract void setLocalFolder(String paramString);

	public abstract void setRepeatMode(int paramInt);

	public abstract void setScanSmallSongFile(Boolean paramBoolean);

	public abstract void setShuffleMode(int paramInt);

	public abstract void setSkinStyleName(String paramString);

	public abstract void setTensileShows(Boolean paramBoolean);

	public abstract int updateCacheData(String paramString1,
			String paramString2, String paramString3);

	public abstract int updateContentId(String paramString1, String paramString2);

}
