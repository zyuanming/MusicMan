package org.ming.ui.view;

import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.MusicType;
import org.ming.center.database.Playlist;
import org.ming.center.database.Song;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.ui.UIEventListener;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.ui.adapter.LocalSongCursorAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.util.MyLogger;
import org.ming.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class LocalSongListViewByCursor extends LinearLayout implements
		PlayerEventListener, UIEventListener
{
	private static final int POPWINDOW_END = 1;
	private static final MyLogger logger = MyLogger.getLogger("SongListView");
	LocalSongCursorAdapter adapter;
	private Cursor cursor;
	private Button mBtnPlayAll = null;
	private Context mContext;
	private Controller mController;
	private Dialog mCurrentDialog = null;
	private DBController mDBController = null;
	private LayoutInflater mLayoutInflater;
	private ImageView mNothingView;
	private PlayerController mPlayerController = null;
	// private PlayerStatusBar mPlayerStatusBar = null;
	private PopupWindow mPopupWindow = null;
	private List<Song> mSongListData = null;
	private AdapterView.OnItemClickListener mSongListItemOnItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int paramAnonymousInt,
				long paramAnonymousLong)
		{
			if (LocalSongListViewByCursor.this.mPlayerController
					.isInteruptByCall())
				Toast.makeText(LocalSongListViewByCursor.this.getContext(),
						R.string.user_calling, 1).show();
			Cursor localCursor = LocalSongListViewByCursor.this.cursor;
			Song localSong = null;
			if (localCursor != null)
			{
				int j = LocalSongListViewByCursor.this.cursor.getCount();
				localSong = null;
				if (j > paramAnonymousInt)
				{
					LocalSongListViewByCursor.this.cursor
							.moveToPosition(paramAnonymousInt);
					localSong = LocalSongListViewByCursor.this.mDBController
							.getSongById(LocalSongListViewByCursor.this.cursor
									.getLong(LocalSongListViewByCursor.this.cursor
											.getColumnIndexOrThrow("_id")));
				}
			}
			if (localSong != null)
			{
				int i = LocalSongListViewByCursor.this.mPlayerController
						.add2NowPlayingList(localSong);
				Playlist localPlaylist = LocalSongListViewByCursor.this.mDBController
						.getPlaylistByName(
								"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
								2);
				if (!LocalSongListViewByCursor.this.mDBController
						.isSongInMixPlaylist(localPlaylist.mExternalId,
								localSong.mId, false))
				{
					if (LocalSongListViewByCursor.this.mDBController
							.countSongNumInPlaylist(localPlaylist.mExternalId,
									2) >= 20)
					{
						long l2 = LocalSongListViewByCursor.this.mDBController
								.getFirstSongInPlaylist(
										localPlaylist.mExternalId, 2);
						if (l2 != -1L)
							LocalSongListViewByCursor.this.mDBController
									.deleteSongsFromMixPlaylist(
											localPlaylist.mExternalId,
											new long[] { l2 }, 2);
					}
					DBController localDBController = LocalSongListViewByCursor.this.mDBController;
					long l1 = localPlaylist.mExternalId;
					long[] arrayOfLong = new long[1];
					arrayOfLong[0] = localSong.mId;
					localDBController.addSongs2MixPlaylist(l1, arrayOfLong,
							false);
				}
				mPlayerController.open(i);
				adapter.notifyDataSetChanged();
			}
		}
	};
	private ListView mSongListView = null;
	private final Handler mSplashHandler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{
			LocalSongListViewByCursor.logger.v("handleMessage() ---> Enter : "
					+ paramAnonymousMessage.what);
			switch (paramAnonymousMessage.what)
			{
			default:
				LocalSongListViewByCursor.logger.v("handleMessage() ---> Exit");
			case 1:
			}
			if (LocalSongListViewByCursor.this.mPopupWindow != null)
			{
				LocalSongListViewByCursor.this.mPopupWindow.dismiss();
				LocalSongListViewByCursor.this.mPopupWindow = null;
			}
		}
	};

	public LocalSongListViewByCursor(Context paramContext)
	{
		super(paramContext);
		this.mContext = paramContext;
		inital();
	}

	public LocalSongListViewByCursor(Context paramContext,
			AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		this.mContext = paramContext;
		inital();
	}

	private int playAll()
	{
		// int i = -1;
		// long l = this.mDBController.getPlaylistByName(
		// "cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
		// 2).mExternalId;
		// int j;
		// if ((this.cursor == null) || (this.cursor.getCount() == 0))
		// {
		// j = i;
		// return j;
		// }
		// if (cursor != null && cursor.getCount() != 0)
		// {
		// this.cursor.moveToPosition(-1);
		// long[] arrayOfLong = new long[this.cursor.getCount()];
		// if (!this.cursor.moveToNext())
		// {
		// if ((l != -1L)
		// && (this.mDBController.addSongs2MixPlaylist(l,
		// arrayOfLong, false)))
		// i = 0;
		// this.cursor.moveToFirst();
		// Song localSong2 = getSong(this.cursor);
		// this.mPlayerController.open(this.mPlayerController
		// .checkSongInNowPlayingList(localSong2));
		// j = i;
		// break;
		// }
		// Song localSong1 = getSong(this.cursor);
		// this.mPlayerController.add2NowPlayingList(localSong1, true);
		// if (this.cursor.getPosition() < arrayOfLong.length)
		// arrayOfLong[this.cursor.getPosition()] = localSong1.mId;
		// }
		return 0;
	}

	public void addEventListner()
	{
		// this.mPlayerStatusBar.registEventListener();
		this.mController.addPlayerEventListener(1002, this);
		this.mController.addPlayerEventListener(1010, this);
		this.mController.addPlayerEventListener(1012, this);
		this.mController.addPlayerEventListener(1011, this);
		this.mController.addPlayerEventListener(1014, this);
		this.mController.addUIEventListener(4008, this);
	}

	public void closeCursor()
	{
		if ((this.cursor != null) && (!this.cursor.isClosed()))
			this.cursor.close();
	}

	public void deleteSongFromList(Song paramSong)
	{
		this.cursor.requery();
	}

	public Song getSong(Cursor paramCursor)
	{
		Song localSong = new Song();
		localSong.mAlbum = paramCursor.getString(paramCursor
				.getColumnIndexOrThrow("album"));
		localSong.mArtist = paramCursor.getString(paramCursor
				.getColumnIndexOrThrow("artist"));
		localSong.mUrl = paramCursor.getString(paramCursor
				.getColumnIndexOrThrow("_data"));
		localSong.mContentId = this.mDBController
				.queryContentId(localSong.mUrl);
		localSong.mMusicType = MusicType.LOCALMUSIC;
		localSong.mLyric = null;
		try
		{
			localSong.mAlbumId = paramCursor.getInt(paramCursor
					.getColumnIndexOrThrow("album_id"));
			localSong.mDuration = paramCursor.getInt(paramCursor
					.getColumnIndexOrThrow("duration"));
			localSong.mId = paramCursor.getInt(paramCursor
					.getColumnIndexOrThrow("_id"));
			localSong.mTrack = paramCursor.getString(paramCursor
					.getColumnIndexOrThrow("title"));
			int i = paramCursor.getColumnIndexOrThrow("_size");
			if ((i < paramCursor.getColumnCount()) && (i >= 0))
				localSong.mSize = paramCursor.getLong(paramCursor
						.getColumnIndexOrThrow("_size"));
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return localSong;
	}

	public Song getSongbyPosition(int paramInt)
	{
		if ((this.cursor != null) && (this.cursor.getCount() > paramInt))
			this.cursor.moveToPosition(paramInt);
		for (Song localSong = getSong(this.cursor);; localSong = null)
			return localSong;
	}

	public void handlePlayerEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
		case 1014:
		case 1002:
		case 1010:
		case 1011:
		case 1012:
		}
		Object obj;
		if (((Activity) mContext).getParent() == null)
			obj = mContext;
		else
			obj = (Activity) mContext;
		Uiutil.ifSwitchToWapDialog(((Context) (obj)));
		notifyDataChange();
	}

	public void handleUIEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
		case 4008:
		}
		notifyDataChange();
	}

	public void inital()
	{
		mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		mPlayerController = mController.getPlayerController();
		mDBController = mController.getDBController();
		mLayoutInflater = LayoutInflater.from(mContext);
		removeAllViews();
		LayoutParams layoutparams = new LayoutParams(-1, -1);
		LinearLayout linearlayout = (LinearLayout) mLayoutInflater.inflate(
				R.layout.local_song_list_view, null);
		// mPlayerStatusBar = (PlayerStatusBar) linearlayout
		// .findViewById(R.id.playerStatusBar);
		mNothingView = (ImageView) findViewById(R.id.nothing);
		addView(linearlayout, layoutparams);
		mBtnPlayAll = (Button) findViewById(R.id.btn_all_play);
		mBtnPlayAll.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				if (mPlayerController.isInteruptByCall())
				{
					Toast.makeText(getContext(), R.string.user_calling, 1)
							.show();
				} else
				{
					if (mCurrentDialog != null)
					{
						mCurrentDialog.dismiss();
						mCurrentDialog = null;
					}
					mBtnPlayAll.setClickable(false);
					mCurrentDialog = DialogUtil
							.showIndeterminateProgressDialog(mContext,
									R.string.local_music_playallsong);
					(new PlayAllTask()).execute(new String[0]);
				}
			}
		});
	}

	public void notifyDataChange()
	{
		if (this.adapter != null)
			this.adapter.notifyDataSetChanged();
	}

	public void reQueryCursor()
	{
		this.cursor.requery();
	}

	public void removeEventListner()
	{
		// this.mPlayerStatusBar.unRegistEventListener();
		this.mController.removePlayerEventListener(1002, this);
		this.mController.removePlayerEventListener(1010, this);
		this.mController.removePlayerEventListener(1012, this);
		this.mController.removePlayerEventListener(1011, this);
		this.mController.removePlayerEventListener(1014, this);
		this.mController.removeUIEventListener(4008, this);
	}

	public void renameSongFromList(Song paramSong, String paramString)
	{
		paramSong.mTrack = paramString;
	}

	public void setButtonDisable(boolean paramBoolean)
	{
		if (paramBoolean)
			this.mBtnPlayAll.setEnabled(true);
		this.mBtnPlayAll.setEnabled(false);
	}

	public void setCursor(Cursor paramCursor)
	{
		closeCursor();
		if ((paramCursor != null) && (paramCursor.getCount() > 0))
		{
			this.mNothingView = ((ImageView) findViewById(R.id.nothing));
			this.mNothingView.setVisibility(View.GONE);
			this.mSongListView = ((ListView) findViewById(R.id.songlistview));
			this.adapter = new LocalSongCursorAdapter(getContext(), paramCursor);
			this.mSongListView.setAdapter(this.adapter);
			this.adapter.setBtnOnClickListener(new ListButtonListener());
			this.mSongListView.setFadingEdgeLength(0);
			this.mSongListView
					.setOnItemClickListener(this.mSongListItemOnItemClickListener);
			this.cursor = paramCursor;
		}
		this.mNothingView = ((ImageView) findViewById(R.id.nothing));
		this.mNothingView.setVisibility(View.VISIBLE);
	}

	public void setOnCreateContextMenuListener(
			View.OnCreateContextMenuListener paramOnCreateContextMenuListener)
	{
		if (this.mSongListView != null)
			this.mSongListView
					.setOnCreateContextMenuListener(paramOnCreateContextMenuListener);
	}

	public class ListButtonListener implements View.OnClickListener
	{
		public ListButtonListener()
		{}

		public void onClick(View paramView)
		{
			LocalSongListViewByCursor.logger.v("ListButtonListener----");
			paramView.getId();
			int i = Integer.parseInt(paramView.getTag().toString());
			Cursor localCursor = LocalSongListViewByCursor.this.cursor;
			Song localSong1 = null;
			if (localCursor != null)
			{
				int j = LocalSongListViewByCursor.this.cursor.getCount();
				localSong1 = null;
				if (j > i)
				{
					LocalSongListViewByCursor.this.cursor.moveToPosition(i);
					localSong1 = LocalSongListViewByCursor.this.mDBController
							.getSongById(LocalSongListViewByCursor.this.cursor
									.getLong(LocalSongListViewByCursor.this.cursor
											.getColumnIndexOrThrow("_id")));
				}
			}
			if (localSong1 == null)
				;
			Song localSong2 = LocalSongListViewByCursor.this.mPlayerController
					.getCurrentPlayingItem();
			if ((localSong2 == null) || (!Util.isRadioMusic(localSong2)))
				LocalSongListViewByCursor.this.mPlayerController
						.add2NowPlayingList(localSong1, true);
			Playlist localPlaylist = LocalSongListViewByCursor.this.mDBController
					.getPlaylistByName(
							"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
							2);
			if (!LocalSongListViewByCursor.this.mDBController
					.isSongInMixPlaylist(localPlaylist.mExternalId,
							localSong1.mId, false))
			{
				if (LocalSongListViewByCursor.this.mDBController
						.countSongNumInPlaylist(localPlaylist.mExternalId, 2) >= 20)
				{
					long l2 = LocalSongListViewByCursor.this.mDBController
							.getFirstSongInPlaylist(localPlaylist.mExternalId,
									2);
					if (l2 != -1L)
						mDBController
								.deleteSongsFromMixPlaylist(
										localPlaylist.mExternalId,
										new long[] { l2 }, 2);
				}
				DBController localDBController = LocalSongListViewByCursor.this.mDBController;
				long l1 = localPlaylist.mExternalId;
				long[] arrayOfLong = new long[1];
				arrayOfLong[0] = localSong1.mId;
				localDBController.addSongs2MixPlaylist(l1, arrayOfLong, false);
				if ((mPlayerController.getNowPlayingList().size() == 1)
						&& (!Util.isRadioMusic(localSong2)))
					LocalSongListViewByCursor.this.mPlayerController.open(0);
			}
			if (mPopupWindow != null)
			{
				mPopupWindow.dismiss();
				mPopupWindow = null;
			}
			mPopupWindow = Uiutil.popupWarningIcon(
					LocalSongListViewByCursor.this.mContext, paramView,
					R.drawable.add_playlist_warn_icon);
			mSplashHandler.sendEmptyMessageDelayed(1,
					UIGlobalSettingParameter.add_music_animation_delaytime);
		}
	}

	class PlayAllTask extends AsyncTask<String, Void, Integer>
	{
		PlayAllTask()
		{}

		public Integer doInBackground(String[] paramArrayOfString)
		{
			return Integer.valueOf(LocalSongListViewByCursor.this.playAll());
		}

		public void onPostExecute(Integer paramInteger)
		{
			LocalSongListViewByCursor.this.mBtnPlayAll.setClickable(true);
			if (LocalSongListViewByCursor.this.mCurrentDialog != null)
			{
				LocalSongListViewByCursor.this.mCurrentDialog.dismiss();
				LocalSongListViewByCursor.this.mCurrentDialog = null;
			}
			super.onPostExecute(paramInteger);
		}

		public void onPreExecute()
		{
			super.onPreExecute();
		}
	}
}