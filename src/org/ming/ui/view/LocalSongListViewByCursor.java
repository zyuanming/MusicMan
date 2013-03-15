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
import org.ming.dispatcher.DispatcherEventEnum;
import org.ming.ui.adapter.LocalSongCursorAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.widget.PlayerStatusBar;
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
	private static final MyLogger logger = MyLogger
			.getLogger("LocalSongListViewByCursor");
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
	private PlayerStatusBar mPlayerStatusBar = null;
	private PopupWindow mPopupWindow = null;
	private List<Song> mSongListData = null;
	private AdapterView.OnItemClickListener mSongListItemOnItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View v, int i, long l)
		{
			logger.d("ItemClick ----> enter");
			logger.d("paramAnonymousInt ---> " + i);
			logger.d("paramAnonymousLong ---> " + l);
			if (mPlayerController.isInteruptByCall())
			{
				Toast.makeText(getContext(), R.string.user_calling, 1).show();
			}
			Cursor localCursor = cursor;
			Song localSong = null;
			if (localCursor != null)
			{
				logger.v("localCursor != null");
				int k = cursor.getCount();
				localSong = null;
				if (k > i)
				{
					cursor.moveToPosition(i);
					localSong = mDBController.getSongById(cursor.getLong(cursor
							.getColumnIndexOrThrow("_id")));
				}
			}
			if (localSong != null)
			{
				logger.d("localSong ! = null");
				int j = mPlayerController.add2NowPlayingList(localSong);
				Playlist localPlaylist = mDBController
						.getPlaylistByName(
								"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
								2);
				if (!mDBController.isSongInMixPlaylist(
						localPlaylist.mExternalId, localSong.mId, false))
				{
					if (mDBController.countSongNumInPlaylist(
							localPlaylist.mExternalId, 2) >= 20)
					{
						long l2 = mDBController.getFirstSongInPlaylist(
								localPlaylist.mExternalId, 2);
						if (l2 != -1L)
							mDBController.deleteSongsFromMixPlaylist(
									localPlaylist.mExternalId,
									new long[] { l2 }, 2);
					}
					DBController localDBController = mDBController;
					long l1 = localPlaylist.mExternalId;
					long[] arrayOfLong = new long[1];
					arrayOfLong[0] = localSong.mId;
					localDBController.addSongs2MixPlaylist(l1, arrayOfLong,
							false);
				}
				mPlayerController.open(j);
				adapter.notifyDataSetChanged();
			}
			logger.d("ItemClick ----> exit");
		}
	};
	private ListView mSongListView = null;
	private Handler mSplashHandler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{
			logger.v("handleMessage() ---> Enter : "
					+ paramAnonymousMessage.what);
			switch (paramAnonymousMessage.what)
			{
			default:
				logger.v("handleMessage() ---> Exit");
			case 1:
			}
			if (mPopupWindow != null)
			{
				mPopupWindow.dismiss();
				mPopupWindow = null;
			}
		}
	};

	public LocalSongListViewByCursor(Context context)
	{
		super(context);
		mSongListView = null;
		mPlayerController = null;
		mDBController = null;
		mSongListData = null;
		mBtnPlayAll = null;
		mPopupWindow = null;
		mCurrentDialog = null;
		mPlayerStatusBar = null;
		mContext = context;
		inital();
	}

	public LocalSongListViewByCursor(Context context, AttributeSet attributeset)
	{
		super(context, attributeset);
		mSongListView = null;
		mPlayerController = null;
		mDBController = null;
		mSongListData = null;
		mBtnPlayAll = null;
		mPopupWindow = null;
		mCurrentDialog = null;
		mPlayerStatusBar = null;
		mContext = context;
		inital();
	}

	private int playAll()
	{
		logger.v("playAll() ----> enter");
		int i = -1;
		long l = this.mDBController.getPlaylistByName(
				"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
				2).mExternalId;
		int j;
		if ((this.cursor == null) || (this.cursor.getCount() == 0))
		{
			j = i;
			logger.v("cursor == null");
			logger.v("playAll() ----> exit");
			return j;
		} else
		{
			this.cursor.moveToPosition(-1);
			long[] arrayOfLong = new long[this.cursor.getCount()];
			while (true)
			{
				if (!this.cursor.moveToNext())
				{
					if ((l != -1L)
							&& (this.mDBController.addSongs2MixPlaylist(l,
									arrayOfLong, false)))
						i = 0;
					this.cursor.moveToFirst();
					Song localSong2 = getSong(this.cursor);
					this.mPlayerController.open(this.mPlayerController
							.checkSongInNowPlayingList(localSong2));
					j = i;
					logger.v("playAll() ----> exit");
					return j;
				}
				Song localSong1 = getSong(this.cursor);
				this.mPlayerController.add2NowPlayingList(localSong1, true);
				logger.v("add song to nowPlayingList ---->" + localSong1.mId);
				if (this.cursor.getPosition() < arrayOfLong.length)
					arrayOfLong[this.cursor.getPosition()] = localSong1.mId;
			}
		}
	}

	public void addEventListner()
	{
		logger.d("addEventlistener ----> enter");
		this.mPlayerStatusBar.registEventListener();
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
		localSong.mMusicType = MusicType.LOCALMUSIC.ordinal();
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

	public Song getSongbyPosition(int i)
	{
		Song song;
		if (cursor != null && cursor.getCount() > i)
		{
			cursor.moveToPosition(i);
			song = getSong(cursor);
		} else
		{
			song = null;
		}
		return song;
	}

	public void handlePlayerEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			return;
		case DispatcherEventEnum.PLAYER_EVENT_WAP_CLOSED:
		{
			Object obj;
			if (((Activity) mContext).getParent() == null)
				obj = mContext;
			else
				obj = (Activity) mContext;
			//Uiutil.ifSwitchToWapDialog(((Context) (obj)));
		}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_TRACK_ENDED:
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_START:
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_PAUSE:
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_STOP:
		{
			notifyDataChange();
		}
			break;
		}
	}

	public void handleUIEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			return;
		case DispatcherEventEnum.UI_EVENT_PLAY_NEWSONG:
		{
			notifyDataChange();
		}
			break;
		}
	}

	public void inital()
	{
		logger.d("inital ---> enter");
		mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		mPlayerController = mController.getPlayerController();
		mDBController = mController.getDBController();
		mLayoutInflater = LayoutInflater.from(mContext);
		removeAllViews();
		LayoutParams layoutparams = new LayoutParams(-1, -1);
		LinearLayout linearlayout = (LinearLayout) mLayoutInflater.inflate(
				R.layout.local_song_list_view, null);
		mPlayerStatusBar = (PlayerStatusBar) linearlayout
				.findViewById(R.id.playerStatusBar);
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
		this.mPlayerStatusBar.unRegistEventListener();
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

	public void setButtonDisable(boolean flag)
	{
		if (flag)
		{
			this.mBtnPlayAll.setEnabled(true);
		} else
		{
			this.mBtnPlayAll.setEnabled(false);
		}
	}

	public void setCursor(Cursor paramCursor)
	{
		logger.d("setCursor() ----> enter");
		closeCursor();
		if ((paramCursor != null) && (paramCursor.getCount() > 0))
		{
			mNothingView = ((ImageView) findViewById(R.id.nothing));
			mNothingView.setVisibility(View.GONE);
			mSongListView = ((ListView) findViewById(R.id.songlistview));
			adapter = new LocalSongCursorAdapter(getContext(), paramCursor);
			mSongListView.setAdapter(adapter);
			adapter.setBtnOnClickListener(new ListButtonListener());
			mSongListView.setFadingEdgeLength(0);
			mSongListView
					.setOnItemClickListener(mSongListItemOnItemClickListener);
			cursor = paramCursor;
		} else
		{
			mNothingView = ((ImageView) findViewById(R.id.nothing));
			mNothingView.setVisibility(View.VISIBLE);
			logger.d("setCursor() ----> exit");
		}
	}

	public void setOnCreateContextMenuListener(
			View.OnCreateContextMenuListener menuListener)
	{
		if (mSongListView != null)
			mSongListView.setOnCreateContextMenuListener(menuListener);
	}

	public class ListButtonListener implements View.OnClickListener
	{
		public ListButtonListener()
		{}

		public void onClick(View view)
		{
			logger.v("ListButtonListener.onClick() ----> enter");
			view.getId();
			int i = Integer.parseInt(view.getTag().toString());
			Cursor cursor1 = cursor;
			Song song = null;
			if (cursor1 != null)
			{
				int j = cursor.getCount();
				song = null;
				if (j > i)
				{
					cursor.moveToPosition(i);
					song = mDBController.getSongById(cursor.getLong(cursor
							.getColumnIndexOrThrow("_id")));
				}
			}
			if (song != null)
			{
				Song song1 = mPlayerController.getCurrentPlayingItem();
				if (song1 == null || !Util.isRadioMusic(song1))
					mPlayerController.add2NowPlayingList(song, true);
				Playlist playlist = mDBController
						.getPlaylistByName(
								"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
								2);
				if (!mDBController.isSongInMixPlaylist(playlist.mExternalId,
						song.mId, false))
				{
					if (mDBController.countSongNumInPlaylist(
							playlist.mExternalId, 2) >= 20)
					{
						long l1 = mDBController.getFirstSongInPlaylist(
								playlist.mExternalId, 2);
						if (l1 != -1L)
							mDBController.deleteSongsFromMixPlaylist(
									playlist.mExternalId, new long[] { l1 }, 2);
					}
					DBController dbcontroller = mDBController;
					long l = playlist.mExternalId;
					long al[] = new long[1];
					al[0] = song.mId;
					dbcontroller.addSongs2MixPlaylist(l, al, false);
					if (mPlayerController.getNowPlayingList().size() == 1
							&& !Util.isRadioMusic(song1))
						mPlayerController.open(0);
				}
				if (mPopupWindow != null)
				{
					mPopupWindow.dismiss();
					mPopupWindow = null;
				}
				mPopupWindow = Uiutil.popupWarningIcon(mContext, view,
						R.drawable.add_playlist_warn_icon);
				mSplashHandler.sendEmptyMessageDelayed(1,
						UIGlobalSettingParameter.add_music_animation_delaytime);
			}
		}
	}

	class PlayAllTask extends AsyncTask<String, Void, Integer>
	{
		PlayAllTask()
		{}

		public Integer doInBackground(String[] paramArrayOfString)
		{
			return Integer.valueOf(playAll());
		}

		public void onPostExecute(Integer paramInteger)
		{
			mBtnPlayAll.setClickable(true);
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			super.onPostExecute(paramInteger);
		}

		public void onPreExecute()
		{
			super.onPreExecute();
		}
	}
}