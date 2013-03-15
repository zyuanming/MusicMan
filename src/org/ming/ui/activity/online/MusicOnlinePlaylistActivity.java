package org.ming.ui.activity.online;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.MusicType;
import org.ming.center.database.Playlist;
import org.ming.center.database.Song;
import org.ming.center.database.UserAccount;
import org.ming.center.http.item.SongListItem;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.system.SystemEventListener;
import org.ming.ui.activity.MobileMusicMainActivity;
import org.ming.ui.activity.MusicPlayerActivity;
import org.ming.ui.adapter.MobileMusicPlayListItemAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;
import org.ming.util.Util;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MusicOnlinePlaylistActivity extends ListActivity implements
		PlayerEventListener, SystemEventListener
{
	public static final MyLogger logger = MyLogger
			.getLogger("MusicOnlinePlaylistActivity");
	private MusicPlayerActivity.BussinessType mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
	private Button mClearAllSong = null;
	private Controller mController = null;
	private Context context;
	private List<Song> mCurrentContentList = null;
	private Dialog mCurrentDialog;
	private Song mCurrentSong;
	private DBController mDBController = null;
	private MobileMusicPlayListItemAdapter mMobileMusicPlayListItemAdapter = null;
	private View.OnClickListener mOnclickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			MusicOnlinePlaylistActivity.logger.v("mOnclickListener ---> Enter");
			switch (paramAnonymousView.getId())
			{

			case R.id.clear_allsong:
				ClearAllSong();
			default:
				logger.v("mOnclickListener ---> Exit");
			}
		}
	};
	private PlayerController mPlayerController = null;
	private PopupWindow mPopupWindow = null;
	private List<SongListItem> mSongItemList;
	private AdapterView.OnItemClickListener mSongListItemOnItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int paramAnonymousInt,
				long paramAnonymousLong)
		{
			SongListItem localSongListItem = (SongListItem) mSongItemList
					.get(paramAnonymousInt);
			Song localSong = mPlayerController.getCurrentPlayingItem();
			if (localSong != null)
			{
				if ((localSong.mMusicType == MusicType.ONLINEMUSIC.ordinal())
						&& (localSong.mContentId == localSongListItem.contentid))
				{
					onPlayPauseButtonClick();
				} else
				{
					if ((localSong.mMusicType == MusicType.LOCALMUSIC.ordinal())
							&& (localSong.mUrl
									.equalsIgnoreCase(localSongListItem.url)))
					{
						onPlayPauseButtonClick();
					} else
					{
						if (localSongListItem.mMusicType == MusicType.ONLINEMUSIC
								.ordinal())
						{
							mPlayerController.open(paramAnonymousInt);
						} else
						{
							if (localSongListItem.mMusicType == MusicType.LOCALMUSIC
									.ordinal())
							{
								mPlayerController.open(paramAnonymousInt);
							}
						}
					}
				}
			} else
			{
				if (localSongListItem.mMusicType == MusicType.ONLINEMUSIC
						.ordinal())
				{
					mPlayerController.open(paramAnonymousInt);
				} else
				{
					if (localSongListItem.mMusicType == MusicType.LOCALMUSIC
							.ordinal())
					{
						mPlayerController.open(paramAnonymousInt);
					}
				}
			}
		}
	};
	private TitleBarView mTitleBar;
	private boolean mTosatFlag = false;
	public View.OnClickListener onMarketBarClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			marketBarclick(paramAnonymousView);
		}
	};

	private void ClearAllSong()
	{
		logger.v("ClearAllSong() ---> Enter");
		showAskDeleteAllItemDialog();
		logger.v("ClearAllSong() ---> Exit");
	}

	private void DeleteItemInPlaylist(int paramInt)
	{
		logger.v("DeleteItemInDefaulPlaylist() ---> Enter");
		Song localSong = (Song) this.mCurrentContentList.get(paramInt);
		if (Util.isOnlineMusic(localSong))
		{
			Long localLong = Long.valueOf(this.mDBController
					.getSongIdByContentId(localSong.mContentId,
							localSong.mGroupCode));
			Playlist localPlaylist2 = this.mDBController
					.getPlaylistByName(
							"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
							2);
			DBController localDBController2 = this.mDBController;
			long l2 = localPlaylist2.mExternalId;
			long[] arrayOfLong2 = new long[1];
			arrayOfLong2[0] = localLong.longValue();
			localDBController2.deleteSongsFromMixPlaylist(l2, arrayOfLong2, 2);
		}
		while (true)
		{
			this.mPlayerController.delOnlineSong(localSong);
			refreshUI(false);
			logger.v("DeleteItemInDefaulPlaylist() ---> Enter");
			Playlist localPlaylist1 = this.mDBController
					.getPlaylistByName(
							"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
							2);
			DBController localDBController1 = this.mDBController;
			long l1 = localPlaylist1.mExternalId;
			long[] arrayOfLong1 = new long[1];
			arrayOfLong1[0] = localSong.mId;
			localDBController1.deleteSongsFromMixPlaylist(l1, arrayOfLong1, 2);
		}
	}

	private void doUnCompleteTask()
	{
		if (GlobalSettingParameter.useraccount != null)
		{
			switch (this.mBussinessTYpe.ordinal())
			{
			case 9:
			case 10:
			default:
				break;
			case 5:
				Uiutil.setTone(this, mCurrentSong.mGroupCode,
						mCurrentSong.mArtist, mCurrentSong.mContentId);
				break;
			case 6:
				Uiutil.setViberate(this, mCurrentSong.mGroupCode,
						mCurrentSong.mArtist, mCurrentSong.mContentId,
						mCurrentSong.ringSongPrice, mCurrentSong.crbtValidity);
				break;
			case 7:
				Uiutil.recommondMusic(this, mCurrentSong.mContentId,
						mCurrentSong.mGroupCode);
				break;
			case 8:
				if (GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER == null)
				{
					mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
					return;
				} else
				{
					if (Integer
							.parseInt(GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER) == 3)
					{
						if (mTosatFlag)
						{
							mTosatFlag = false;
							Toast.makeText(this, getText(0x7f0701f6), 1).show();
						}
					}
				}
				Song localSong = this.mPlayerController.getCurrentPlayingItem();
				Uiutil.sendMusic(this, mCurrentSong.mGroupCode,
						mCurrentSong.mArtist, mCurrentSong.mContentId,
						mCurrentSong.ringSongPrice);
			case 4:
				Uiutil.downloadMusic(this, mCurrentSong.mGroupCode,
						mCurrentSong.mArtist, mCurrentSong.mContentId);
				break;
			}
			this.mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
		}
	}

	private boolean gotoLogin()
	{
		UserAccount localUserAccount = GlobalSettingParameter.useraccount;
		boolean bool = false;
		if (localUserAccount == null)
		{
			Uiutil.login(this, 0);
			bool = true;
		}
		return bool;
	}

	private boolean gotoLogin(
			MusicPlayerActivity.BussinessType paramBussinessType)
	{
		UserAccount localUserAccount = GlobalSettingParameter.useraccount;
		boolean bool = false;
		if (localUserAccount == null)
		{
			this.mBussinessTYpe = paramBussinessType;
			Uiutil.login(this, 0);
			bool = true;
		}
		return bool;
	}

	private void marketBarclick(View paramView)
	{
		switch (paramView.getId())
		{

		case R.id.orderwindow_settone:
			Uiutil.setTone(this, mCurrentSong.mGroupCode, mCurrentSong.mArtist,
					mCurrentSong.mContentId);
			break;
		case R.id.orderwindow_setring:
			Uiutil.setViberate(this, mCurrentSong.mGroupCode,
					mCurrentSong.mArtist, mCurrentSong.mContentId,
					mCurrentSong.ringSongPrice, mCurrentSong.crbtValidity);
			break;
		case R.id.orderwindow_download:
			Uiutil.downloadMusic(this, mCurrentSong.mGroupCode,
					mCurrentSong.mArtist, mCurrentSong.mContentId);
			break;
		case R.id.orderwindow_sendsong:
			Uiutil.sendMusic(this, mCurrentSong.mGroupCode,
					mCurrentSong.mArtist, mCurrentSong.mContentId,
					mCurrentSong.ringSongPrice);
			break;
		case R.id.orderwindow_recommand:
			Uiutil.recommondMusic(this, this.mCurrentSong.mContentId,
					this.mCurrentSong.mGroupCode);
		case R.id.popupwindow_setring_li:
		case R.id.popupwindow_download_li:
		case R.id.popupwindow_sendsong_li:
		case R.id.popupwindow_recommand_li:
		default:
			if (this.mPopupWindow != null)
			{
				this.mPopupWindow.dismiss();
				this.mPopupWindow = null;
				break;
			}
		}
	}

	private void onPlayPauseButtonClick()
	{
		logger.v("onPlayPauseButtonClick() ---> Enter");
		if (this.mPlayerController.isInteruptByCall())
		{
			Toast.makeText(this, R.string.user_calling, 0).show();
		} else
		{
			if (this.mPlayerController.isInitialized())
			{
				if (this.mPlayerController.isPlaying())
				{
					this.mPlayerController.pause();
				} else
				{
					this.mPlayerController.start();
				}
			} else
			{
				this.mPlayerController.open(this.mPlayerController
						.getNowPlayingItemPosition());
			}
			logger.v("onPlayPauseButtonClick() ---> Exit");
		}
	}

	private void playAllSong()
	{
		if (this.mSongItemList.size() > 0)
			this.mPlayerController.open(0);
	}

	private void refreshUI(boolean paramBoolean)
	{
		logger.v("refreshUI() ---> Enter");
		setTitle(getText(R.string.playlist_recent_play_common));
		this.mCurrentContentList = this.mPlayerController.getNowPlayingList();
		if ((this.mCurrentContentList != null)
				&& (this.mCurrentContentList.size() > 0))
		{
			this.mSongItemList = new ArrayList();
			Iterator localIterator;
			localIterator = this.mCurrentContentList.iterator();
			while (localIterator.hasNext())
			{
				Song localSong2 = (Song) localIterator.next();
				if (localSong2 != null)
				{
					this.mSongItemList.add(Util.makeSongListItem(localSong2));
				}
			}
		} else
		{
			this.mClearAllSong.setEnabled(false);// 如果列表没有歌曲时，删除按钮不可用
		}
		int i = getListView().getFirstVisiblePosition();
		mMobileMusicPlayListItemAdapter = new MobileMusicPlayListItemAdapter(
				this, mSongItemList);
		mMobileMusicPlayListItemAdapter.setRatingVisaible(false);
		mMobileMusicPlayListItemAdapter
				.setBtnOnClickListener(new ListButtonListener());
		getListView().setAdapter(this.mMobileMusicPlayListItemAdapter);
		mMobileMusicPlayListItemAdapter.notifyDataSetChanged();
		if (i > 0)
		{
			getListView().setSelectionFromTop(i + 1, 0);
		}
		Song localSong1 = this.mPlayerController.getCurrentPlayingItem();
		if ((localSong1 != null) && (this.mPlayerController.isPlaying()))
		{
			MusicType localMusicType = MusicType.values()[localSong1.mMusicType];
			if ((localSong1.isDolby)
					&& (GlobalSettingParameter.show_dobly_toast)
					&& (!"LOCALMUSIC".equals(localMusicType.toString())))
			{
				Toast.makeText(this, R.string.you_can_enjoy_dobly_music, 0)
						.show();
				GlobalSettingParameter.show_dobly_toast = false;
			}
		}
		this.mClearAllSong.setEnabled(true);
		logger.v("refreshUI() ---> Exit");
	}

	private void showAskDeleteAllItemDialog()
	{
		logger.v("showAskDeleteItemDialog() ---> Enter");
		if (this.mCurrentDialog != null)
		{
			this.mCurrentDialog.dismiss();
			this.mCurrentDialog = null;
		}
		this.mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(this,
				getText(R.string.title_confirm_delete_playlist_activity),
				getText(R.string.confirm_delete_allitem_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MusicOnlinePlaylistActivity.this.mCurrentDialog != null)
						{
							MusicOnlinePlaylistActivity.this.mCurrentDialog
									.dismiss();
							MusicOnlinePlaylistActivity.this.mCurrentDialog = null;
						}
						Playlist localPlaylist = MusicOnlinePlaylistActivity.this.mDBController
								.getPlaylistByName(
										"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
										2);
						MusicOnlinePlaylistActivity.this.mDBController
								.deleteAllSongsFromMixPlaylist(
										localPlaylist.mExternalId, 2);
						MusicOnlinePlaylistActivity.this.mPlayerController
								.clearNowPlayingList();
						MusicOnlinePlaylistActivity.this.refreshUI(false);
					}
				}, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MusicOnlinePlaylistActivity.this.mCurrentDialog != null)
						{
							MusicOnlinePlaylistActivity.this.mCurrentDialog
									.dismiss();
							MusicOnlinePlaylistActivity.this.mCurrentDialog = null;
						}
					}
				});
		logger.v("showAskDeleteItemDialog() ---> Exit");
	}

	private void showAskDeleteItemDialog(final int paramInt)
	{
		logger.v("showAskDeleteItemDialog() ---> Enter");
		this.mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(this,
				getText(R.string.title_confirm_delete_playlist_activity),
				getText(R.string.confirm_delete_item_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MusicOnlinePlaylistActivity.this.mCurrentDialog != null)
						{
							MusicOnlinePlaylistActivity.this.mCurrentDialog
									.dismiss();
							MusicOnlinePlaylistActivity.this.mCurrentDialog = null;
						}
						MusicOnlinePlaylistActivity.this
								.DeleteItemInPlaylist(paramInt);
					}
				}, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MusicOnlinePlaylistActivity.this.mCurrentDialog != null)
						{
							MusicOnlinePlaylistActivity.this.mCurrentDialog
									.dismiss();
							MusicOnlinePlaylistActivity.this.mCurrentDialog = null;
						}
					}
				});
		logger.v("showAskDeleteItemDialog() ---> Exit");
	}

	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
	{
		boolean bool;
		switch (paramMotionEvent.getAction())
		{
		case 0:
			if (this.mPopupWindow != null)
			{
				this.mPopupWindow.dismiss();
				this.mPopupWindow = null;
				bool = true;
				break;
			}
		default:
			bool = super.dispatchTouchEvent(paramMotionEvent);
		}
		return bool;
	}

	public void handlePlayerEvent(Message paramMessage)
	{
		logger.v("handlePlayerEvent() ---> Enter");
		switch (paramMessage.what)
		{
		case 1002:
		case 1010:
		case 1011:
			// case
			// 1012:this.mMobileMusicPlayListItemAdapter.notifyDataSetChanged();break;
		case 1014:
			Uiutil.ifSwitchToWapDialog(this);
		default:
		}
		logger.v("handlePlayerEvent() ---> Exit");
	}

	public void handleSystemEvent(Message paramMessage)
	{
		logger.v("handleSystemEvent() ---> Enter");
		switch (paramMessage.what)
		{
		case 22:
			finish();
		default:
		}
		logger.v("handleSystemEvent() ---> Exit");
	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_play_list_layout);
		mController = ((MobileMusicApplication) getApplication())
				.getController();
		mDBController = mController.getDBController();
		mPlayerController = mController.getPlayerController();
		mController.addSystemEventListener(22, this);
		getListView().setOnItemClickListener(mSongListItemOnItemClickListener);
		this.mTitleBar = ((TitleBarView) findViewById(R.id.title_view));
		this.mTitleBar.setCurrentActivity(this);
		this.mTitleBar
				.setTitle(R.string.now_playing_list_name_now_playing_list_activity);
		this.mTitleBar.setButtons(2);
		this.mClearAllSong = ((Button) findViewById(R.id.clear_allsong));
		this.mClearAllSong.setOnClickListener(this.mOnclickListener);
		this.mController.addPlayerEventListener(1002, this);
		this.mController.addPlayerEventListener(1010, this);
		this.mController.addPlayerEventListener(1012, this);
		this.mController.addPlayerEventListener(1011, this);
		this.mController.addPlayerEventListener(1014, this);
		refreshUI(false);
		logger.v("onCreate() ---> Exit");
	}

	public void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		super.onDestroy();
		this.mController.removePlayerEventListener(1002, this);
		this.mController.removePlayerEventListener(1010, this);
		this.mController.removePlayerEventListener(1012, this);
		this.mController.removePlayerEventListener(1011, this);
		this.mController.removePlayerEventListener(1014, this);
		this.mController.removeSystemEventListener(22, this);
		logger.v("onDestroy() ---> Exit");
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
		logger.v("onKeyDown() ---> Enter");
		boolean bool = false;
		if (paramInt == 4)
		{
			if (this.mPopupWindow != null)
			{
				this.mPopupWindow.dismiss();
				this.mPopupWindow = null;
				bool = true;
			} else
			{
				finish();
				overridePendingTransition(R.anim.player_finish_in,
						R.anim.player_finish_out);
			}
		} else
		{
			logger.v("onKeyDown() ---> Exit");
			bool = super.onKeyDown(paramInt, paramKeyEvent);
		}
		return bool;
	}

	public void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		logger.v("onPause() ---> Exit");
	}

	public void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		this.mTitleBar
				.setRightBtnImage(R.drawable.btn_paly_to_collect_selector);
		this.mTitleBar.setrightBtnListner(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (GlobalSettingParameter.useraccount == null)
				{
					Uiutil.login(MusicOnlinePlaylistActivity.this, 0);
				} else
				{
					Intent localIntent = new Intent(
							MusicOnlinePlaylistActivity.this,
							MobileMusicMainActivity.class);
					Bundle localBundle = new Bundle();
					localBundle.putBoolean("hasLogin", true);
					localIntent.putExtras(localBundle);
					MusicOnlinePlaylistActivity.this.startActivity(localIntent);
				}

			}
		});
		refreshUI(true);
		doUnCompleteTask();
		logger.v("onResume() ---> Exit");
	}

	public class ListButtonListener implements View.OnClickListener
	{
		public ListButtonListener()
		{}

		public void onClick(View paramView)
		{
			MusicOnlinePlaylistActivity.logger
					.v("ListButtonListener ---> Enter");
			final int i = Integer.parseInt(paramView.getTag().toString());
			mCurrentSong = ((Song) mCurrentContentList.get(i));
			if (mCurrentSong != null)
			{
				MusicType localMusicType = MusicType.values()[mCurrentSong.mMusicType];
				switch (localMusicType.ordinal())
				{
				case 1:
					View localView1 = getLayoutInflater().inflate(
							R.layout.playlist_online_music_player_order_window,
							null);
					((TextView) localView1.findViewById(R.id.windowTitle))
							.setText(MusicOnlinePlaylistActivity.this.mCurrentSong.mTrack);
					((Button) localView1.findViewById(R.id.orderwindow_settone))
							.setOnClickListener(MusicOnlinePlaylistActivity.this.onMarketBarClickListener);
					((Button) localView1.findViewById(R.id.orderwindow_setring))
							.setOnClickListener(MusicOnlinePlaylistActivity.this.onMarketBarClickListener);
					((Button) localView1
							.findViewById(R.id.orderwindow_download))
							.setOnClickListener(MusicOnlinePlaylistActivity.this.onMarketBarClickListener);
					Button localButton = (Button) localView1
							.findViewById(R.id.orderwindow_sendsong);
					localButton
							.setOnClickListener(MusicOnlinePlaylistActivity.this.onMarketBarClickListener);
					if ((GlobalSettingParameter.useraccount != null)
							&& (GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER != null)
							&& (GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER
									.equals(String.valueOf(3))))
					{
						localButton.setTextColor(-7829368);
						localButton.setClickable(false);
					}
					((Button) localView1
							.findViewById(R.id.orderwindow_addsongtoplaylist))
							.setVisibility(8);
					((Button) localView1
							.findViewById(R.id.orderwindow_recommand))
							.setOnClickListener(MusicOnlinePlaylistActivity.this.onMarketBarClickListener);
					((Button) localView1.findViewById(R.id.orderwindow_delete))
							.setOnClickListener(new View.OnClickListener()
							{
								public void onClick(View paramAnonymousView)
								{
									if (MusicOnlinePlaylistActivity.this.mPopupWindow != null)
									{
										MusicOnlinePlaylistActivity.this.mPopupWindow
												.dismiss();
										MusicOnlinePlaylistActivity.this.mPopupWindow = null;
									}
									MusicOnlinePlaylistActivity.this
											.showAskDeleteItemDialog(i);
								}
							});
					if ((MusicOnlinePlaylistActivity.this.mPopupWindow != null)
							&& (MusicOnlinePlaylistActivity.this.mPopupWindow
									.isShowing()))
					{
						MusicOnlinePlaylistActivity.this.mPopupWindow.dismiss();
						MusicOnlinePlaylistActivity.this.mPopupWindow = null;
					}
					MusicOnlinePlaylistActivity.this.mPopupWindow = new PopupWindow(
							localView1, -2, -2);
					MusicOnlinePlaylistActivity.this.mPopupWindow
							.showAtLocation(paramView, 17, 0, 0);
					if ((MusicOnlinePlaylistActivity.this.mPopupWindow != null)
							&& (MusicOnlinePlaylistActivity.this.mPopupWindow
									.isShowing()))
						MusicOnlinePlaylistActivity.this.mPopupWindow.dismiss();
					MusicOnlinePlaylistActivity.this.mPopupWindow = new PopupWindow(
							localView1, -2, -2);
					MusicOnlinePlaylistActivity.this.mPopupWindow
							.showAtLocation(paramView, 17, 0, 0);
					break;
				case 2:
					View localView2 = MusicOnlinePlaylistActivity.this
							.getLayoutInflater()
							.inflate(
									R.layout.playlist_local_music_player_order_window,
									null);
					((TextView) localView2.findViewById(R.id.windowTitle))
							.setText(MusicOnlinePlaylistActivity.this.mCurrentSong.mTrack);
					((Button) localView2.findViewById(R.id.orderwindow_delete))
							.setOnClickListener(new View.OnClickListener()
							{
								public void onClick(View paramAnonymousView)
								{
									if (mPopupWindow != null)
									{
										mPopupWindow.dismiss();
										mPopupWindow = null;
									}
									showAskDeleteItemDialog(i);
								}
							});
					if ((mPopupWindow != null) && (mPopupWindow.isShowing()))
					{
						mPopupWindow.dismiss();
						mPopupWindow = null;
					}
					mPopupWindow = new PopupWindow(localView2, -2, -2);
					mPopupWindow.showAtLocation(paramView, 17, 0, 0);
					break;
				default:
					break;
				}
			}
			logger.v("ListButtonListener ---> Exit");
		}
	}
}

/*
 * Location: D:\guangge\windows64\反编译工具\classes-dex2jar.jar Qualified Name:
 * cmccwm.mobilemusic.ui.activity.online.MusicOnlinePlaylistActivity JD-Core
 * Version: 0.6.2
 */
// R.style.launch