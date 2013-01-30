package org.ming.ui.adapter;

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
import android.widget.ImageView;
import android.widget.TextView;

public class LocalSongCursorAdapter extends CursorAdapter
{
	private static final MyLogger logger = MyLogger
			.getLogger("LocalSongCursorAdapter");
	private android.view.View.OnClickListener mBtnClicker;
	private Context mContext;
	private Controller mController;
	private DBController mDBController;
	private int mIconrid;
	private LayoutInflater mInflater;
	private PlayerController mPlayerController;
	int songArtistIndex;
	int songTitleIndex;

	public final class ViewHolder
	{

		public ImageView btn_recommend_add;
		public ImageView btn_recommend_state;
		public TextView song_name;
		public TextView songer_name;
		final LocalSongCursorAdapter localSongCursorAdapter;

		public ViewHolder()
		{
			localSongCursorAdapter = LocalSongCursorAdapter.this;
		}
	}

	public LocalSongCursorAdapter(Context context, Cursor cursor)
	{
		super(context, cursor);
		mDBController = null;
		mPlayerController = null;
		mIconrid = 0;
		songTitleIndex = 0;
		songArtistIndex = 0;
		initAdapter(context, cursor);
	}

	public LocalSongCursorAdapter(Context context, Cursor cursor, int i)
	{
		super(context, cursor, i);
		mDBController = null;
		mPlayerController = null;
		mIconrid = 0;
		songTitleIndex = 0;
		songArtistIndex = 0;
		initAdapter(context, cursor);
	}

	public LocalSongCursorAdapter(Context context, Cursor cursor, boolean flag)
	{
		super(context, cursor, flag);
		mDBController = null;
		mPlayerController = null;
		mIconrid = 0;
		songTitleIndex = 0;
		songArtistIndex = 0;
		initAdapter(context, cursor);
	}

	private void initAdapter(Context context, Cursor cursor)
	{
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		mPlayerController = mController.getPlayerController();
		if (cursor != null && cursor.getCount() > 0)
		{
			songTitleIndex = cursor.getColumnIndexOrThrow("title");
			songArtistIndex = cursor.getColumnIndexOrThrow("artist");
		}
		mDBController = mController.getDBController();
	}

	public void bindView(View view, Context context, Cursor cursor)
	{
		((TextView) view.findViewById(R.id.song_name)).setText(cursor
				.getString(songTitleIndex));
		((TextView) view.findViewById(R.id.songer_name)).setText(cursor
				.getString(songArtistIndex));
		Song song = getSong(cursor);
		Song song1 = mPlayerController.getCurrentPlayingItem();
		if (song1 != null && song.mMusicType == MusicType.LOCALMUSIC.ordinal()
				&& song1.mMusicType == MusicType.LOCALMUSIC.ordinal())
		{
			if (song.mUrl.equalsIgnoreCase(song1.mUrl))
				((ImageView) view.findViewById(R.id.play_state))
						.setVisibility(View.VISIBLE);
			else
				((ImageView) view.findViewById(R.id.play_state))
						.setVisibility(View.GONE);
		} else
		{
			((ImageView) view.findViewById(R.id.play_state))
					.setVisibility(View.GONE);
		}
		((ImageView) view.findViewById(R.id.btn_recommend_add))
				.setOnClickListener(mBtnClicker);
		((ImageView) view.findViewById(R.id.btn_recommend_add)).setTag(Integer
				.valueOf(cursor.getPosition()));
	}

	public Song getSong(Cursor cursor)
	{
		Song song = new Song();
		song.mAlbum = cursor.getString(cursor.getColumnIndexOrThrow("album"));
		song.mAlbumId = cursor.getInt(cursor.getColumnIndexOrThrow("album_id"));
		song.mArtist = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
		song.mUrl = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
		song.mContentId = mDBController.queryContentId(song.mUrl);
		song.mDuration = cursor
				.getInt(cursor.getColumnIndexOrThrow("duration"));
		song.mId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
		song.mMusicType = MusicType.LOCALMUSIC.ordinal();
		song.mLyric = null;
		song.mTrack = cursor.getString(cursor.getColumnIndexOrThrow("title"));
		song.mSize = cursor.getLong(cursor.getColumnIndexOrThrow("_size"));
		return song;
	}

	public View newView(Context context, Cursor cursor, ViewGroup viewgroup)
	{
		return mInflater.inflate(R.layout.list_cell_localmusic, null);
	}

	public void setBtnOnClickListener(
			android.view.View.OnClickListener onclicklistener)
	{
		mBtnClicker = onclicklistener;
	}

}
