package org.ming.ui.view;

import java.util.Iterator;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.Playlist;
import org.ming.center.database.Song;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.ui.UIEventListener;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.ui.adapter.LocalSongListAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.widget.PlayerStatusBar;
import org.ming.util.MyLogger;
import org.ming.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

public class LocalSongListView extends LinearLayout implements
		PlayerEventListener, UIEventListener
{
	private static final int POPWINDOW_END = 1;
	private static final MyLogger logger = MyLogger.getLogger("SongListView");
	private Button mBtnPlayAll = null;
	private Context mContext;
	private Controller mController;
	private Dialog mCurrentDialog = null;
	private DBController mDBController = null;
	private LayoutInflater mLayoutInflater;
	LocalSongListAdapter mMobileMusicRecommendListItemAdapter;
	private ImageView mNothingView;
	private PlayerController mPlayerController = null;
	private PlayerStatusBar mPlayerStatusBar = null;
	private PopupWindow mPopupWindow = null;
	private List<Song> mSongListData = null;
	private AdapterView.OnItemClickListener mSongListItemOnItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView adapterview, View view, int i,
				long l)
		{
			if (mPlayerController.isInteruptByCall())
			{
				Toast.makeText(getContext(), R.string.user_calling, 0).show();
			} else
			{
				Song song = (Song) mSongListData.get(i);
				int j = mPlayerController.add2NowPlayingList(song);
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
						long l2 = mDBController.getFirstSongInPlaylist(
								playlist.mExternalId, 2);
						if (l2 != -1L)
							mDBController.deleteSongsFromMixPlaylist(
									playlist.mExternalId, new long[] { l2 }, 2);
					}
					DBController dbcontroller = mDBController;
					long l1 = playlist.mExternalId;
					long al[] = new long[1];
					al[0] = song.mId;
					dbcontroller.addSongs2MixPlaylist(l1, al, false);
				}
				mPlayerController.open(j);
				mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
			}
		}
	};
	private ListView mSongListView = null;
	private final Handler mSplashHandler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{
			LocalSongListView.logger.v("handleMessage() ---> Enter : "
					+ paramAnonymousMessage.what);
			switch (paramAnonymousMessage.what)
			{
			default:
				LocalSongListView.logger.v("handleMessage() ---> Exit");
				return;
			case 1:
				if (mPopupWindow != null)
				{
					mPopupWindow.dismiss();
					mPopupWindow = null;
				}
				break;
			}
		}
	};

	public LocalSongListView(Context paramContext)
	{
		super(paramContext);
		this.mContext = paramContext;
		inital();
	}

	public LocalSongListView(Context paramContext,
			AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		this.mContext = paramContext;
		inital();
	}

	private int playAll()
	{
		int i = -1;
		long l = this.mDBController.getPlaylistByName(
				"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
				2).mExternalId;
		int j;
		if ((this.mSongListData == null) || (this.mSongListData.size() == 0))
		{
			j = i;
			return j;
		} else
		{
			long[] arrayOfLong = new long[this.mSongListData.size()];
			int k = 0;
			Iterator localIterator = this.mSongListData.iterator();
			while (true)
			{
				if (!localIterator.hasNext())
				{
					if ((l != -1L)
							&& (this.mDBController.addSongs2MixPlaylist(l,
									arrayOfLong, false)))
						i = 0;
					this.mPlayerController
							.open(this.mPlayerController
									.checkSongInNowPlayingList((Song) this.mSongListData
											.get(0)));
					j = i;
					return j;
				}
				Song localSong = (Song) localIterator.next();
				this.mPlayerController.add2NowPlayingList(localSong, true);
				int m = k + 1;
				arrayOfLong[k] = localSong.mId;
				k = m;
			}
		}
	}

	public void addEventListner()
	{
		this.mPlayerStatusBar.registEventListener();
		this.mController.addPlayerEventListener(1002, this);
		this.mController.addPlayerEventListener(1010, this);
		this.mController.addPlayerEventListener(1012, this);
		this.mController.addPlayerEventListener(1011, this);
		this.mController.addPlayerEventListener(1014, this);
		this.mController.addUIEventListener(4008, this);
	}

	public void addSongList(List<Song> paramList)
	{
		if ((paramList != null) && (paramList.size() > 0))
		{
			this.mNothingView = ((ImageView) findViewById(2131034276));
			this.mNothingView.setVisibility(8);
		}
		this.mSongListView = ((ListView) findViewById(2131034423));
		this.mSongListData = paramList;
		this.mMobileMusicRecommendListItemAdapter = new LocalSongListAdapter(
				this.mContext, paramList);
		this.mSongListView
				.setAdapter(this.mMobileMusicRecommendListItemAdapter);
		this.mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
		this.mSongListView.setFadingEdgeLength(0);
		this.mSongListView
				.setOnItemClickListener(this.mSongListItemOnItemClickListener);
		this.mMobileMusicRecommendListItemAdapter
				.setBtnOnClickListener(new ListButtonListener());
	}

	public void deleteSongFromList(Song paramSong)
	{
		this.mSongListData.remove(paramSong);
		if (this.mMobileMusicRecommendListItemAdapter != null)
			this.mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
	}

	public Song getSongbyPosition(int paramInt)
	{
		return (Song) this.mSongListData.get(paramInt);
	}

	public void handlePlayerEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			return;
		case 1014:
			Object obj;
			if (((Activity) mContext).getParent() == null)
				obj = mContext;
			else
				obj = (Activity) mContext;
			Uiutil.ifSwitchToWapDialog(((Context) (obj)));
			break;
		case 1002:
		case 1010:
		case 1011:
		case 1012:
			notifyDataChange();
			break;
		}
	}

	public void handleUIEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			return;
		case 4008:
			notifyDataChange();
			break;
		}
	}

	public void inital()
	{
		this.mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		this.mPlayerController = this.mController.getPlayerController();
		this.mDBController = this.mController.getDBController();
		this.mLayoutInflater = LayoutInflater.from(this.mContext);
		removeAllViews();
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
				-1, -1);
		LinearLayout localLinearLayout = (LinearLayout) this.mLayoutInflater
				.inflate(R.layout.local_song_list_view, null);
		this.mPlayerStatusBar = ((PlayerStatusBar) localLinearLayout
				.findViewById(R.id.playerStatusBar));
		this.mNothingView = ((ImageView) findViewById(R.id.nothing));
		addView(localLinearLayout, localLayoutParams);
		this.mBtnPlayAll = ((Button) findViewById(R.id.btn_all_play));
		this.mBtnPlayAll.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				if (mPlayerController.isInteruptByCall())
				{
					Toast.makeText(getContext(), R.string.user_calling, 0)
							.show();
				} else
				{
					if (mCurrentDialog != null)
					{
						mCurrentDialog.dismiss();
						mCurrentDialog = null;
					}
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
		if (this.mMobileMusicRecommendListItemAdapter != null)
			this.mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
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
		if (this.mMobileMusicRecommendListItemAdapter != null)
			this.mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
	}

	public void setButtonDisable(boolean flag)
	{
		if (flag)
			mBtnPlayAll.setEnabled(true);
		else
			mBtnPlayAll.setEnabled(false);
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

		public void onClick(View view)
		{
			LocalSongListView.logger.v("ListButtonListener----");
			view.getId();
			int i = Integer.parseInt(view.getTag().toString());
			Song song = (Song) mSongListData.get(i);
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
				if (mDBController.countSongNumInPlaylist(playlist.mExternalId,
						2) >= 20)
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