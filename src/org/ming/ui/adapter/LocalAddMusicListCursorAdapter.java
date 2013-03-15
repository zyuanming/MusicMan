package org.ming.ui.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.player.PlayerController;
import org.ming.util.MyLogger;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalAddMusicListCursorAdapter extends CursorAdapter
{
	private static final MyLogger logger = MyLogger
			.getLogger("LocalAddMusicListCursorAdapter");
	private Cursor currentCursor;
	private View.OnClickListener mBtnClicker;
	private Controller mController;
	private DBController mDBController;
	private LayoutInflater mInflater;
	private List<Long> mOrigilSelectIdList = null;
	private PlayerController mPlayerController = null;
	private List<Long> mSelectIdList = new ArrayList();
	private CheckBox recommendAdd;
	private ImageView recommendState;
	private int songArtistIndex;
	private int songIdIndex;
	private int songTitleIndex;

	public LocalAddMusicListCursorAdapter(Context paramContext,
			Cursor paramCursor)
	{
		super(paramContext, paramCursor);
		initAdapter(paramContext, paramCursor);
	}

	public LocalAddMusicListCursorAdapter(Context paramContext,
			Cursor paramCursor, int paramInt)
	{
		super(paramContext, paramCursor, paramInt);
		initAdapter(paramContext, paramCursor);
	}

	public LocalAddMusicListCursorAdapter(Context paramContext,
			Cursor paramCursor, boolean paramBoolean)
	{
		super(paramContext, paramCursor, paramBoolean);
		initAdapter(paramContext, paramCursor);
	}

	private void initAdapter(Context paramContext, Cursor paramCursor)
	{
		this.mInflater = LayoutInflater.from(paramContext);
		this.mContext = paramContext;
		this.mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		this.mPlayerController = this.mController.getPlayerController();
		if ((paramCursor != null) && (paramCursor.getCount() > 0))
		{
			this.songTitleIndex = paramCursor.getColumnIndexOrThrow("title");
			this.songArtistIndex = paramCursor.getColumnIndexOrThrow("artist");
			this.songIdIndex = paramCursor.getColumnIndexOrThrow("_id");
		}
		this.currentCursor = paramCursor;
		this.mDBController = this.mController.getDBController();
	}

	public void OnClickListener(View.OnClickListener paramOnClickListener)
	{
		logger.v("OnClickListener() ---> Enter");
		this.mBtnClicker = paramOnClickListener;
		logger.v("OnClickListener() ---> Exit");
	}

	public void bindView(View view, Context context, Cursor cursor)
	{
		((TextView) view.findViewById(R.id.song_name)).setText(cursor
				.getString(songTitleIndex));
		((TextView) view.findViewById(R.id.songer_name)).setText(cursor
				.getString(songArtistIndex));
		recommendState = (ImageView) view.findViewById(R.id.play_state);
		recommendAdd = (CheckBox) view.findViewById(R.id.btn_addmusic_add);
		Song song = mPlayerController.getCurrentPlayingItem();
		Song song1 = getSong(cursor);
		if (mSelectIdList.contains(Long.valueOf(song1.mId)))
			recommendAdd.setChecked(true);
		else
			recommendAdd.setChecked(false);
		if (song != null)
		{
			if (song1.mMusicType == MusicType.LOCALMUSIC.ordinal()
					&& song.mMusicType == MusicType.LOCALMUSIC.ordinal())
				if (song1.mUrl.equalsIgnoreCase(song.mUrl))
					recommendState.setVisibility(0);
				else
					recommendState.setVisibility(8);
		} else
		{
			recommendState.setVisibility(8);
		}
		recommendAdd.setOnClickListener(mBtnClicker);
		recommendAdd.setTag(Integer.valueOf(cursor.getPosition()));
	}

	public long[] getDeSelectedMusic()
	{
		long[] arrayOfLong = null;
		if (mOrigilSelectIdList != null)
		{
			logger.v("getSelectedMusic() ---> Enter");
			arrayOfLong = new long[this.mOrigilSelectIdList.size()];
			int i = 0;
			Iterator localIterator = this.mOrigilSelectIdList.iterator();
			while (true)
			{
				if (!localIterator.hasNext())
				{
					logger.v("getSelectedMusic() ---> Exit");
					break;
				}
				long l = ((Long) localIterator.next()).longValue();
				if (!this.mSelectIdList.contains(Long.valueOf(l)))
				{
					int j = i + 1;
					arrayOfLong[i] = l;
					i = j;
				}
			}
		}
		return arrayOfLong;
	}

	public long[] getSelectedMusic()
	{
		logger.v("getSelectedMusic() ---> Enter");
		long[] arrayOfLong = new long[this.mSelectIdList.size()];
		int i = 0;
		Iterator localIterator = this.mSelectIdList.iterator();
		while (true)
		{
			if (!localIterator.hasNext())
			{
				logger.v("getSelectedMusic() ---> Exit");
				return arrayOfLong;
			}
			arrayOfLong[i] = ((Long) localIterator.next()).longValue();
			i++;
		}
	}

	public Song getSong(Cursor paramCursor)
	{
		Song localSong = new Song();
		localSong.mAlbum = paramCursor.getString(paramCursor
				.getColumnIndexOrThrow("album"));
		localSong.mAlbumId = paramCursor.getInt(paramCursor
				.getColumnIndexOrThrow("album_id"));
		localSong.mArtist = paramCursor.getString(paramCursor
				.getColumnIndexOrThrow("artist"));
		localSong.mUrl = paramCursor.getString(paramCursor
				.getColumnIndexOrThrow("_data"));
		localSong.mContentId = this.mDBController
				.queryContentId(localSong.mUrl);
		localSong.mDuration = paramCursor.getInt(paramCursor
				.getColumnIndexOrThrow("duration"));
		localSong.mId = paramCursor.getInt(paramCursor
				.getColumnIndexOrThrow("_id"));
		localSong.mMusicType = MusicType.LOCALMUSIC.ordinal();
		localSong.mLyric = null;
		localSong.mTrack = paramCursor.getString(paramCursor
				.getColumnIndexOrThrow("title"));
		localSong.mSize = paramCursor.getLong(paramCursor
				.getColumnIndexOrThrow("_size"));
		return localSong;
	}

	public boolean isAllChecked()
	{
		boolean flag;
		if (mSelectIdList.size() == currentCursor.getCount())
			flag = true;
		else
			flag = false;
		return flag;
	}

	public View newView(Context context, Cursor cursor, ViewGroup viewgroup)
	{
		return mInflater.inflate(R.layout.list_cell_local_addmusic, null);
	}

	public void setAllChecked(boolean flag)
	{
		logger.v("setAllChecked() ---> Enter");
		if (flag && currentCursor != null)
		{
			currentCursor.moveToPosition(-1);
			while (currentCursor.moveToNext())
			{
				long l = currentCursor.getLong(songIdIndex);
				mSelectIdList.add(Long.valueOf(l));
			}
			mSelectIdList.clear();
			logger.v("setAllChecked() ---> Exit");
		}
	}

	public void setChecked(int i)
	{
		logger.v("setChecked() ---> Enter");
		long l = -1L;
		if (currentCursor != null && currentCursor.getCount() > i)
		{
			currentCursor.moveToPosition(i);
			l = currentCursor.getLong(songIdIndex);
		}
		if (l != -1L)
		{
			if (mSelectIdList.contains(Long.valueOf(l)))
				mSelectIdList.remove(Long.valueOf(l));
			else
				mSelectIdList.add(Long.valueOf(l));
			logger.v("setChecked() ---> Exit");
		}
	}

	public void setSelectedMusic(List<Long> paramList)
	{
		logger.v("setSelectedMusic() ---> Enter");
		if (paramList != null)
		{
			this.mSelectIdList.addAll(paramList);
			this.mOrigilSelectIdList = paramList;
		}
		logger.v("setSelectedMusic() ---> Exit");
	}
}