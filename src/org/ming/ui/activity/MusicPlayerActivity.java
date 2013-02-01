package org.ming.ui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.business.MusicBusinessDefine_Net;
import org.ming.center.business.MusicBusinessDefine_WAP;
import org.ming.center.database.DBController;
import org.ming.center.database.MusicType;
import org.ming.center.database.Playlist;
import org.ming.center.database.Song;
import org.ming.center.database.UserAccount;
import org.ming.center.download.UrlImageDownloader;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.http.item.SongItem;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerControllerImpl;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.UIEventListener;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.ui.activity.local.LocalScanMusicActivity;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.view.LyricsView;
import org.ming.ui.view.PlayerAlbumInfoView;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;
import org.ming.util.XMLParser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.TextKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayerActivity extends Activity implements
		PlayerEventListener, View.OnClickListener, MMHttpEventListener,
		ViewPager.OnPageChangeListener, SystemEventListener, UIEventListener
{
	public static final int PLAY_NEXT_SONG = 0;
	public static final int PLAY_PRE_SONG = 1;
	private static final MyLogger logger = MyLogger
			.getLogger("MusicPlayerActivity");
	private final int MENU_ITEM_EXIT;
	private final int MENU_ITEM_SCAN_MUSIC;
	private final int MENU_ITEM_SET;
	private final int MENU_ITEM_TIME_CLOSE;
	private View.OnClickListener addplaylistlistener;
	private Context context;
	private ImageView mAlbumImage;
	private TextView mArtistView;
	private ImageButton mBtnHidePlayeActivity;
	private ImageButton mBtnRecentPlayList;
	private BussinessType mBussinessTYpe = BussinessType.NONE;
	private long mClicktime;
	private int mComeForm;
	private Controller mController = null;
	private Dialog mCurrentDialog = null;
	String mCurrentLyric = null;
	private Song mCurrentSong = null;
	private MMHttpTask mCurrentTask;
	private List<MMHttpTask> mCurrentTasks = new ArrayList();
	private TextView mCurrentTimeView;
	private DBController mDBController = null;
	private ImageView mDoblyImageForSong;
	private ImageView mErrorDialogBtn;
	private ImageButton mGarbage;
	private HttpController mHttpController = null;
	private UrlImageDownloader mImageDownloader;
	private boolean mIsFromPushService = false;
	private boolean mIsInLyric = false;
	private boolean mIsNextMusic;
	private List<View> mListViews;
	private ProgressBar mLoading = null;
	private ImageButton mLoveSong;
	private LyricsView mLyricsView;
	private ImageButton mMarketBtn;
	private final Handler mMsgHandler = new MsgHandler();
	private DialogInterface.OnCancelListener mOnCancelListner;
	private View.OnClickListener mPlayControlListener;
	private ImageView mPlayNextButton;
	private ImageView mPlayPauseButton;
	private ImageView mPlayPreButton;
	private PlayerAlbumInfoView mPlayerAlbumInfoView;
	private PlayerController mPlayerController = null;
	private Toast mPlayerOrder;
	private View.OnClickListener mPlayerOrderListener;
	private String mPoint;
	private PopupWindow mPopupWindow = null;
	private SeekBar mProgressBar;
	private ImageButton mRatingBtn;
	private Dialog mReportErroeDialog = null;
	private SeekBar.OnSeekBarChangeListener mSeekListener;
	private Button mShareBtn;
	private ImageButton mSongsRandom;
	private TextView mTotalTimeView;
	private Runnable mTrackEndResetAction;
	private Runnable mTrackEndUpdateAction;
	private ImageView mViewDot;
	private Integer[] mViewDotImages;
	private ViewPager mViewPager;
	private View.OnClickListener marksonglistener;
	public View.OnClickListener onMarketBarClickListener;

	public MusicPlayerActivity()
	{
		Integer[] arrayOfInteger = new Integer[3];
		arrayOfInteger[0] = Integer
				.valueOf(R.drawable.dot_1_for_player_album_info);
		arrayOfInteger[1] = Integer.valueOf(R.drawable.dot_2_for_player_album);
		arrayOfInteger[2] = Integer.valueOf(R.drawable.dot_3_for_player_lrc);
		this.mViewDotImages = arrayOfInteger;
		this.MENU_ITEM_SCAN_MUSIC = 0;
		this.MENU_ITEM_EXIT = 1;
		this.MENU_ITEM_SET = 2;
		this.MENU_ITEM_TIME_CLOSE = 3;
		this.addplaylistlistener = new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				logger.v("addplaylistlistener-onClick ---> Enter");
				if (mPopupWindow != null)
				{
					mPopupWindow.dismiss();
					mPopupWindow = null;
				}
				String str = ((Button) paramAnonymousView).getText().toString();
				Toast.makeText(
						MusicPlayerActivity.this,
						MusicPlayerActivity.this
								.getString(
										R.string.playlist_add_successfully_playlist_activity,
										new Object[] { str }), 0).show();
				int i = ((Integer) paramAnonymousView.getTag()).intValue();
				addPlaylistSong(i);
				logger.v("addplaylistlistener-onClick ---> Exit");
			}
		};
		this.marksonglistener = new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (mPopupWindow != null)
				{
					mPopupWindow.dismiss();
					mPopupWindow = null;
				}
				int i = paramAnonymousView.getId();
				int j = 0;
				switch (i)
				{
				default:

					break;
				case R.id.mark1_view:
					j = 1;
					break;
				case R.id.mark2_view:
					j = 2;
					break;
				case R.id.mark3_view:
					j = 3;
					break;
				case R.id.mark4_view:
					j = 4;
					break;
				case R.id.mark5_view:
					j = 5;
					break;
				}
				mCurrentDialog = Uiutil.showWaitingDialog(
						MusicPlayerActivity.this, mOnCancelListner);
				char c;
				MMHttpRequest mmhttprequest;
				long l;
				String s;
				if (NetUtil.isNetStateWap())
					c = '\u0419';
				else
					c = '\u13BC';
				mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
				mmhttprequest.addUrlParams("contentid",
						mPlayerController.getCurrentPlayingItem().mContentId);
				mmhttprequest.addUrlParams("point", Integer.toString(j));
				l = System.currentTimeMillis();
				s = (new SimpleDateFormat("yyyyMMddhhmmss")).format(Long
						.valueOf(l));
				mmhttprequest.addHeader("randkey", Util.getRandKey(
						GlobalSettingParameter.SERVER_INIT_PARAM_MDN, s));
				mmhttprequest.addHeader("x-up-calling-line-id",
						GlobalSettingParameter.useraccount.mMDN);
				mCurrentTask = mHttpController.sendRequest(mmhttprequest);
			}
		};
		this.onMarketBarClickListener = new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				marketBarclick(paramAnonymousView);
			}
		};
		this.mPlayerOrder = null;
		this.mPlayerOrderListener = new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				int i = mPlayerController.getShuffleMode();
				int j = mPlayerController.getRepeatMode();
				if ((i == 1) && (j == 2))
				{
					mPlayerController.setShuffleMode(0);
					mPlayerController.setRepeatMode(1);
					mSongsRandom
							.setImageResource(R.drawable.player_activity_function_button_repeat_slt);
					if (mPlayerOrder != null)
						mPlayerOrder.cancel();
					mPlayerOrder = Toast.makeText(MusicPlayerActivity.this,
							R.string.repeat_one_player_controller, 0);
					mPlayerOrder.show();
				}
				if ((i == 0) && (j == 1))
				{
					mPlayerController.setShuffleMode(0);
					mPlayerController.setRepeatMode(2);
					mSongsRandom
							.setImageResource(R.drawable.player_activity_function_button_sequence_slt);
					if (mPlayerOrder != null)
						mPlayerOrder.cancel();
					mPlayerOrder = Toast.makeText(MusicPlayerActivity.this,
							R.string.repeat_all_player_controller, 0);
					mPlayerOrder.show();
				} else if ((i == 0) && (j == 2))
				{
					mPlayerController.setShuffleMode(1);
					mPlayerController.setRepeatMode(2);
					mSongsRandom
							.setImageResource(R.drawable.player_activity_function_button_random_slt);
					if (mPlayerOrder != null)
						mPlayerOrder.cancel();
					mPlayerOrder = Toast.makeText(MusicPlayerActivity.this,
							R.string.shuffle_on_player_controller, 0);
					mPlayerOrder.show();
				} else
				{
					mPlayerController.setShuffleMode(0);
					mPlayerController.setRepeatMode(2);
					mSongsRandom
							.setImageResource(R.drawable.player_activity_function_button_random_slt);
					if (mPlayerOrder != null)
						mPlayerOrder.cancel();
					mPlayerOrder = Toast.makeText(MusicPlayerActivity.this,
							R.string.shuffle_on_player_controller, 0);
					mPlayerOrder.show();
				}
			}
		};
		this.mClicktime = 0L;
		this.mPlayControlListener = new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				switch (paramAnonymousView.getId())
				{
				case R.id.music_download_progressBar:
				default:
				{
					return;
				}
				case R.id.statusbar_play_and_pause_button:
				{
					onPlayPauseButtonClick();
				}
					break;
				case R.id.statusbar_next_button:
				{
					mMsgHandler.removeMessages(4);
					mMsgHandler.obtainMessage(4).sendToTarget();
				}
					break;
				case R.id.statusbar_prev_button:
				{
					mMsgHandler.removeMessages(5);
					mMsgHandler.obtainMessage(5).sendToTarget();
				}
					break;
				}
			}
		};
		this.mSeekListener = new SeekBar.OnSeekBarChangeListener()
		{
			public void onProgressChanged(SeekBar paramAnonymousSeekBar,
					int paramAnonymousInt, boolean paramAnonymousBoolean)
			{}

			public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar)
			{}

			public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar)
			{
				Song localSong = MusicPlayerActivity.this.mPlayerController
						.getCurrentPlayingItem();
				int i = paramAnonymousSeekBar.getProgress();
				if (localSong != null)
				{
					if ((mProgressBar != null)
							&& (mProgressBar.getSecondaryProgress() > mProgressBar
									.getProgress()))
					{
						if (localSong.mDuration / 1000 <= 0)
						{
							mPlayerController.seek(i * localSong.mDuration
									/ 1000);
							mLyricsView.reset();
							mLyricsView.seekByTime(i * localSong.mDuration
									/ 1000, true);
						} else
						{
							mPlayerController.seek(i
									* (localSong.mDuration / 1000));
							mLyricsView.reset();
							mLyricsView.seekByTime(i
									* (localSong.mDuration / 1000), true);
						}
					} else if ((mProgressBar != null)
							&& (mProgressBar.getSecondaryProgress() == mProgressBar
									.getProgress())
							&& (1000 == mProgressBar.getProgress()))
					{
						mPlayerController.next();
						mMsgHandler.removeMessages(2);
					}
				}
				return;
			}
		};
		this.mIsNextMusic = false;
		this.mOnCancelListner = new DialogInterface.OnCancelListener()
		{
			public void onCancel(DialogInterface paramAnonymousDialogInterface)
			{
				CancelPreviousReq();
			}
		};
	}

	private void InitViewPager()
	{
		this.mViewPager = ((ViewPager) findViewById(R.id.music_player_middle_slide_Pager));
		this.mListViews = new ArrayList();
		LayoutInflater localLayoutInflater = getLayoutInflater();
		this.mListViews.add(mPlayerAlbumInfoView);
		this.mListViews.add(localLayoutInflater.inflate(
				R.layout.music_player_album_view_layout, null));
		this.mListViews.add(localLayoutInflater.inflate(
				R.layout.music_player_full_lrc_view_layout, null));
		this.mViewPager.setAdapter(new MyPagerAdapter(this.mListViews));
		this.mViewPager
				.setCurrentItem(UIGlobalSettingParameter.music_player_pager_index);
		this.mViewPager.getFocusedChild();
		this.mViewPager.setOnPageChangeListener(this);
	}

	private void addPlaylistSong(long l)
	{
		Song song;
		long l1;
		song = mPlayerController.getCurrentPlayingItem();
		l1 = mDBController.getSongIdByContentId(song.mContentId,
				song.mGroupCode);
		if (-1L != l1)
		{
			if (!mDBController.isSongInPlaylist(l, l1, 0))
			{
				if (mDBController.countSongNumInPlaylist(l, 0) >= 20)
				{
					long l2 = mDBController.getFirstSongInPlaylist(l, 0);
					if (l2 != -1L)
						mDBController.deleteSongsFromPlaylist(l,
								new long[] { l2 }, 0);
				}
				if (mDBController.addSongs2Playlist(l, new long[] { l1 }, 0))
				{
					mLoveSong.setTag(Long.valueOf(l));
					mLoveSong.setImageResource(0x7f020159);
				}
			}
		} else
		{
			l1 = mDBController.addOnlineMusicItem(song);
			if (-1L != l1)
			{
				if (!mDBController.isSongInPlaylist(l, l1, 0))
				{
					if (mDBController.countSongNumInPlaylist(l, 0) >= 20)
					{
						long l2 = mDBController.getFirstSongInPlaylist(l, 0);
						if (l2 != -1L)
							mDBController.deleteSongsFromPlaylist(l,
									new long[] { l2 }, 0);
					}
					if (mDBController
							.addSongs2Playlist(l, new long[] { l1 }, 0))
					{
						mLoveSong.setTag(Long.valueOf(l));
						mLoveSong.setImageResource(0x7f020159);
					}
				}
			}
		}
	}

	private long createPlayList(String s)
	{
		long l = 0L;
		logger.v("createPlayList(playlistName) ---> Enter");
		if (s == null || s.equals(""))
			Toast.makeText(this,
					R.string.invalid_playlist_name_playlist_activity, 0).show();
		else if (s.equals(getString(R.string.playlist_myfav_common))
				|| s.equals(getString(R.string.playlist_recent_play_common)))
			Toast.makeText(this,
					R.string.duplicate_playlist_edit_playlist_activity, 0)
					.show();
		else if (mDBController.getPlaylistByName(s, 0) != null)
		{
			Toast.makeText(this,
					R.string.duplicate_playlist_edit_playlist_activity, 0)
					.show();
		} else
		{
			l = mDBController.createPlaylist(s, 0);
			Toast.makeText(
					this,
					getString(
							R.string.playlist_create_successfully_playlist_activity,
							new Object[] { s }), 0).show();
			logger.v("createPlayList(playlistName) ---> Exit");
		}
		return l;
	}

	private void createPlayListDialog()
	{
		logger.v("createPlayListDialog() ---> Enter");
		View localView = getLayoutInflater().inflate(
				R.layout.activity_my_migu_music_create_playlist_layout, null);
		final EditText localEditText = (EditText) localView
				.findViewById(R.id.new_playlist);
		localEditText.setKeyListener(TextKeyListener.getInstance());
		localEditText.selectAll();
		localEditText
				.setBackgroundResource(R.drawable.login_edittext_input_selector);
		localEditText.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
		this.mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleView(this,
				getText(R.string.create_play_list_playlist_activity), null,
				localView, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						long l = MusicPlayerActivity.this
								.createPlayList(localEditText.getText()
										.toString());
						if (l != 0L)
						{
							MusicPlayerActivity.this.addPlaylistSong(l);
							MusicPlayerActivity.this.mCurrentDialog.dismiss();
						}
					}
				}, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						MusicPlayerActivity.this.mCurrentDialog.dismiss();
						MusicPlayerActivity.this.popupWindowForAddPlayList();
					}
				});
		logger.v("createPlayListDialog() ---> Exit");
	}

	private void doUnCompleteTask()
	{
		boolean flag = true;
		int i = 1;
		if (GlobalSettingParameter.useraccount == null)
		{
			this.mBussinessTYpe = BussinessType.NONE;
			return;
		}
		switch (this.mBussinessTYpe.ordinal())
		{
		case 3:
		case 10:
		default:
		{
			return;
		}
		case 1:
		{
			Song song5 = mPlayerController.getCurrentPlayingItem();
			if (MusicType.values()[song5.mMusicType] == MusicType.RADIO)
			{
				if (song5.like == i)
				{
					flag = false;
					i = 0;
				}
				setRadioMusicFavorite(flag, song5.mContentId);
			} else
			{
				popupWindowForAddPlayList();
			}
			mBussinessTYpe = BussinessType.NONE;
		}
			break;
		case 9:
		{
			setMusicGarbage();
			mBussinessTYpe = BussinessType.NONE;
		}
			break;
		case 5:
		{
			Song song4 = mPlayerController.getCurrentPlayingItem();
			Uiutil.setTone(this, song4.mContentId, song4.mGroupCode);
			mBussinessTYpe = BussinessType.NONE;
		}
			break;
		case 6:
		{
			Song song3 = mPlayerController.getCurrentPlayingItem();
			Uiutil.setViberate(this, song3.mContentId, song3.mGroupCode);
			mBussinessTYpe = BussinessType.NONE;
		}
			break;
		case 7:
		{
			Song song2 = mPlayerController.getCurrentPlayingItem();
			Uiutil.recommondMusic(this, song2.mContentId, song2.mGroupCode);
			mBussinessTYpe = BussinessType.NONE;
		}
			break;
		case 8:
		{
			if (GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER != null)
			{
				if (Integer
						.parseInt(GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER) == 3)
					Toast.makeText(
							context,
							getText(R.string.text_my_speical_member_not_send_music),
							1).show();
				mBussinessTYpe = BussinessType.NONE;
			}

		}
			break;
		case 4:
		{
			Song song = mPlayerController.getCurrentPlayingItem();
			Uiutil.downloadMusic(this, song.mContentId, song.mGroupCode);
		}
			break;
		case 2:
		{
			popupWindowForRating();
		}
			break;
		}
	}

	private void exitApplication()
	{
		this.mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(this,
				getText(R.string.quit_app_dialog_title),
				getText(R.string.quit_app_dialog_message),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MusicPlayerActivity.this.mCurrentDialog != null)
						{
							MusicPlayerActivity.this.mCurrentDialog.dismiss();
							MusicPlayerActivity.this.mCurrentDialog = null;
						}
						Util.exitMobileMusicApp(false);
					}
				}, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MusicPlayerActivity.this.mCurrentDialog != null)
						{
							MusicPlayerActivity.this.mCurrentDialog.dismiss();
							MusicPlayerActivity.this.mCurrentDialog = null;
						}
					}
				});
		this.mCurrentDialog.setCancelable(false);
	}

	private Runnable getTrackEndResetAction()
	{
		if (this.mTrackEndUpdateAction == null)
			this.mTrackEndUpdateAction = new Runnable()
			{
				public void run()
				{
					mCurrentTimeView.setVisibility(0);
					mCurrentTimeView
							.setText(MusicPlayerActivity.this.mTotalTimeView
									.getText());
					setPlayPauseButtonImag(false);
					mProgressBar.setProgress(0);
					mProgressBar.setSecondaryProgress(0);
					mProgressBar.setEnabled(false);
					setProgressBarIndeterminateVisibility(false);
					mLyricsView.reset();
					mLyricsView.seekByTime(0, true);
				}
			};
		return this.mTrackEndUpdateAction;
	}

	private Runnable getTrackEndUpdateAction()
	{
		if (this.mTrackEndResetAction != null)
			this.mTrackEndResetAction = new Runnable()
			{
				public void run()
				{
					mCurrentTimeView.setVisibility(0);
					mCurrentTimeView.setText(Util.makeTimeString(0L));
					mProgressBar
							.setProgress(MusicPlayerActivity.this.mProgressBar
									.getMax());
					mProgressBar
							.setSecondaryProgress(MusicPlayerActivity.this.mProgressBar
									.getMax());
				}
			};
		return this.mTrackEndResetAction;
	}

	private boolean gotoLogin(BussinessType paramBussinessType)
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
		case R.id.popupwindow_setring_li:
		case R.id.popupwindow_download_li:
		case R.id.popupwindow_sendsong_li:
		case R.id.popupwindow_recommand_li:
		default:
		{
			if (mPopupWindow != null)
			{
				mPopupWindow.dismiss();
				mPopupWindow = null;
			}
			return;
		}
		case R.id.orderwindow_settone:
		{
			if (!gotoLogin(BussinessType.TONE))
			{
				Song song4 = mPlayerController.getCurrentPlayingItem();
				Uiutil.setTone(this, song4.mContentId, song4.mGroupCode);
			}
		}
		case R.id.orderwindow_setring:
		{
			if (!gotoLogin(BussinessType.RING))
			{
				Song song3 = mPlayerController.getCurrentPlayingItem();
				Uiutil.setViberate(this, song3.mContentId, song3.mGroupCode);
			}
		}
			break;
		case R.id.orderwindow_download:
		{
			if (!gotoLogin(BussinessType.QUANQU))
			{
				Song song2 = mPlayerController.getCurrentPlayingItem();
				Uiutil.downloadMusic(this, song2.mContentId, song2.mGroupCode);
			}
		}
			break;
		case R.id.orderwindow_sendsong:
		{
			if (!gotoLogin(BussinessType.SENDSONG))
			{
				Song song1 = mPlayerController.getCurrentPlayingItem();
				Uiutil.sendMusic(this, song1.mContentId, song1.mGroupCode);
			}
		}
			break;
		case R.id.orderwindow_recommand:
		{
			if (!gotoLogin(BussinessType.RECOMMAND))
			{
				Song song = mPlayerController.getCurrentPlayingItem();
				Uiutil.recommondMusic(this, song.mContentId, song.mGroupCode);
			}
		}
			break;
		}
	}

	private void onHeadsetPlugInEvent()
	{
		logger.v("onHeadsetPlugInEvent() ---> Enter");
		logger.v("onHeadsetPlugInEvent() ---> Exit");
	}

	private void onHeadsetPlugOutEvent()
	{
		logger.v("onHeadsetPlugOutEvent() ---> Enter");
		logger.v("onHeadsetPlugOutEvent() ---> Exit");
	}

	private void onHttpResponse(MMHttpTask mmhttptask)
	{
		logger.v("onHttpResponse() ---> Enter");
		int i = mmhttptask.getRequest().getReqType();
		XMLParser xmlparser = new XMLParser(mmhttptask.getResponseBody());
		if ((xmlparser.getRoot() == null)
				|| (xmlparser.getValueByTag("code") == null))
		{
			mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
					getText(R.string.title_information_common),
					getText(R.string.fail_to_parse_xml_common),
					new android.view.View.OnClickListener()
					{
						public void onClick(View view)
						{
							mCurrentDialog.dismiss();
						}
					});
		} else
		{

			if (xmlparser.getValueByTag("code") != null
					&& !xmlparser.getValueByTag("code").equals("000000"))
			{
				mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
						getText(R.string.title_information_common),
						xmlparser.getValueByTag("info"),
						new android.view.View.OnClickListener()
						{
							public void onClick(View view)
							{
								mCurrentDialog.dismiss();
							}
						});
			}
		}
		switch (i)
		{
		case 5053:
		case 1050:
		{
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			Song song1 = mPlayerController.getCurrentPlayingItem();
			if (song1.like == 1)
			{
				mLoveSong
						.setImageResource(R.drawable.player_activity_function_button_love_nor);
				song1.like = 0;
				Toast.makeText(this, R.string.radio_song_remove_love, 0).show();
			} else
			{
				mLoveSong
						.setImageResource(R.drawable.player_activity_function_button_love_hl);
				song1.like = 1;
				Toast.makeText(this, R.string.radio_song_add_love, 0).show();
			}
		}
			break;
		case 1051:
		case 5054:
		{
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			Song song = mPlayerController.getCurrentPlayingItem();
			mPlayerController.delRadioSong(song);
			Toast.makeText(this, R.string.garbage_song_success, 0).show();
		}
			break;
		case 1005:
		case 5008:
		{
			onRadioSongsResponse(mmhttptask);
		}
			break;
		case 1049:
		case 5052:
		case 1002:
		case 5005:
		default:
		{
			return;
		}
		}
		logger.v("onHttpResponse() ---> Exit");
		if (this.mCurrentDialog != null)
		{
			this.mCurrentDialog.dismiss();
			this.mCurrentDialog = null;
		}
		Song localSong3 = this.mPlayerController.getCurrentPlayingItem();
		this.mPoint = ((String) mmhttptask.getRequest().getUrlParams()
				.get("point"));
		this.mDBController.addMusicRate(localSong3.mContentId,
				Integer.parseInt(this.mPoint));
		logger.v("nw-----fen---ed >" + this.mPoint + "content:-->"
				+ localSong3.mContentId);
		Toast.makeText(this, R.string.mark_song_success, 0).show();
		if (Integer.parseInt(this.mPoint) != -1)
		{
			switch (Integer.parseInt(this.mPoint))
			{
			default:
				break;
			case 1:
				this.mRatingBtn.setImageResource(R.drawable.marked_1);
				break;
			case 2:
				this.mRatingBtn.setImageResource(R.drawable.marked_2);
				break;
			case 3:
				this.mRatingBtn.setImageResource(R.drawable.marked_3);
				break;
			case 4:
				this.mRatingBtn.setImageResource(R.drawable.marked_4);
				break;
			case 5:
				this.mRatingBtn.setImageResource(R.drawable.marked_5);
				break;
			}
		} else
		{
			return;
		}
	}

	private void onMediaEjectEvent()
	{
		logger.v("onMediaEjectEvent() ---> Enter");
		if (this.mPlayerController.isFileOnExternalStorage())
			finish();
		logger.v("onMediaEjectEvent() ---> Exit");
	}

	private void onNetWorkError(Message paramMessage)
	{
		logger.v("onNetWorkError() ---> Enter");
		this.mMsgHandler.removeMessages(2);
		setPlayPauseButtonImag(false);
		this.mCurrentTimeView.setVisibility(0);
		this.mCurrentTimeView.setText(Util.makeTimeString(0L));
		this.mProgressBar.setProgress(0);
		setProgressBarIndeterminateVisibility(false);
		this.mLoading.setVisibility(8);
		this.mPlayPauseButton.setVisibility(0);
		logger.v("onNetWorkError() ---> Exit");
	}

	private void onNextButtonClick()
	{
		logger.v("onNextButtonClick() ---> Enter");
		if (mPlayerController.isInteruptByCall())
		{
			Toast.makeText(this, 0x7f07003c, 0).show();
		} else
		{
			mIsNextMusic = false;
			updateTrackInfo(true);
			queueNextRefresh(refreshNow());
			logger.v("onNextButtonClick() ---> Exit");
		}
	}

	private void onPlayListButtonClick()
	{
		logger.v("onPlayListButtonClick() ---> Enter");
		// startActivity(new Intent(this, MusicOnlinePlaylistActivity.class));
		logger.v("onPlayListButtonClick() ---> Exit");
	}

	private void onPlayPauseButtonClick()
	{
		logger.v("onPlayPauseButtonClick() ---> Enter");
		if (this.mPlayerController.isInteruptByCall())
		{
			Toast.makeText(this, R.string.user_calling, 0).show();
			return;
		}
		if (this.mPlayerController.isInitialized())
			if (this.mPlayerController.isPlaying())
			{
				this.mPlayerController.pause();
				setPlayPauseButtonImag(this.mPlayerController.isPlaying());
			}

		if (((this.mPlayerController.getCurrentPlayingItem().mMusicType == MusicType.ONLINEMUSIC
				.ordinal()) || (this.mPlayerController.getCurrentPlayingItem().mMusicType == MusicType.RADIO
				.ordinal()))
				&& (NetUtil.isConnection()))
		{
			this.mPlayerController.start();
		} else if (this.mPlayerController.getCurrentPlayingItem().mMusicType == MusicType.LOCALMUSIC
				.ordinal())
		{
			this.mPlayerController.start();
		}
		Toast.makeText(this, R.string.wlan_disconnect_title_util, 0).show();
		this.mProgressBar.setProgress(0);
		this.mProgressBar.setSecondaryProgress(0);
		if (this.mPlayerController.isPlayRecommendSong())
			this.mPlayerController.openRecommendSong(this.mPlayerController
					.getNowPlayingItemPosition());
		else
			this.mPlayerController.open(this.mPlayerController
					.getNowPlayingItemPosition());
		logger.v("onPlayPauseButtonClick() ---> Exit");
	}

	private void onPlaybackStart()
	{
		logger.v("onPlaybackStart() ---> Enter");
		this.mLoading.setVisibility(8);
		this.mPlayPauseButton.setVisibility(0);
		updateTrackInfo(false);
		setPlayPauseButtonImag(true);
		this.mProgressBar.setEnabled(true);
		setProgressBarIndeterminateVisibility(false);
		logger.v("onPlaybackStart() ---> Exit");
	}

	private void onPlayerError(Message paramMessage)
	{
		logger.v("onPlayerError() ---> Enter");
		this.mCurrentTimeView.setVisibility(0);
		this.mCurrentTimeView.setText(Util.makeTimeString(0L));
		this.mMsgHandler.removeMessages(2);
		setPlayPauseButtonImag(false);
		this.mProgressBar.setProgress(0);
		setProgressBarIndeterminateVisibility(false);
		this.mLyricsView.setLyrics(null);
		this.mLoading.setVisibility(8);
		this.mPlayPauseButton.setVisibility(0);
		logger.v("onPlayerError() ---> Exit");
	}

	private void onPlayerPrepareStart()
	{
		logger.v("onPlayerPrepareStart() ---> Enter");
		Song song = mPlayerController.getCurrentPlayingItem();
		if (song != null)
		{
			boolean flag;
			if (!(mPlayerController instanceof PlayerControllerImpl)
					&& !Util.isOnlineMusic(song))
				flag = false;
			else
				flag = true;
			setProgressBarIndeterminateVisibility(flag);
		}
		updateTrackInfo(true);
		mMsgHandler.removeMessages(2);
		queueNextRefresh(refreshNow());
		logger.v("onPlayerPrepareStart() ---> Exit");
	}

	private void onPrevButtonClick()
	{
		logger.v("onPrevButtonClick() ---> Enter");
		if (mPlayerController.isInteruptByCall())
		{
			Toast.makeText(this, 0x7f07003c, 0).show();
		} else
		{
			mIsNextMusic = false;
			updateTrackInfo(true);
			queueNextRefresh(refreshNow());
			logger.v("onPrevButtonClick() ---> Exit");
		}
	}

	private void onPushSongResponse(MMHttpTask mmhttptask)
	{
		XMLParser xmlparser = new XMLParser(mmhttptask.getResponseBody());
		ArrayList arraylist = new ArrayList();
		Song song = new Song();
		song.mAlbum = xmlparser.getValueByTag("album");
		song.mAlbumId = -1;
		song.mArtist = xmlparser.getValueByTag("singer");
		song.mContentId = xmlparser.getValueByTag("contentid");
		song.mDuration = -1;
		song.mId = -1L;
		song.mMusicType = MusicType.ONLINEMUSIC.ordinal();
		song.mLyric = null;
		song.mArtUrl = xmlparser.getValueByTag("img");
		song.mTrack = xmlparser.getValueByTag("songname");
		song.mUrl = xmlparser.getValueByTag("durl1");
		song.mUrl2 = xmlparser.getValueByTag("durl2");
		song.mUrl3 = xmlparser.getValueByTag("durl3");
		if (!"".equals(xmlparser.getValueByTag("filesize1"))
				&& xmlparser.getValueByTag("filesize1") != null)
			song.mSize = Long.valueOf(xmlparser.getValueByTag("filesize1"))
					.longValue();
		if (!"".equals(xmlparser.getValueByTag("filesize2"))
				&& xmlparser.getValueByTag("filesize2") != null)
			song.mSize2 = Long.valueOf(xmlparser.getValueByTag("filesize2"))
					.longValue();
		if (!"".equals(xmlparser.getValueByTag("filesize3"))
				&& xmlparser.getValueByTag("filesize3") != null)
			song.mSize3 = Long.valueOf(xmlparser.getValueByTag("filesize3"))
					.longValue();
		song.isDolby = Util.isDolby(song);
		song.mGroupCode = xmlparser.getValueByTag("groupcode");
		String s = xmlparser.getValueByTag("point");
		if (s != null && !s.trim().equals(""))
			song.mPoint = Integer.parseInt(s);
		else
			song.mPoint = 0;
		arraylist.add(song);
		if (!arraylist.isEmpty())
		{
			mPlayerController.add2NowPlayingList(arraylist);
			mPlayerController.open(-1
					+ mPlayerController.getNowPlayingList().size());
			showPlayer();
		}
	}

	private void onRadioSongsResponse(MMHttpTask paramMMHttpTask)
	{
		logger.v("onInitColumnResponse() ---> Enter");
		XMLParser localXMLParser = new XMLParser(
				paramMMHttpTask.getResponseBody());
		String str = localXMLParser.getValueByTag("groupcode");
		List localList = localXMLParser.getListByTag("music", SongItem.class);
		if (localList != null)
		{
			ArrayList localArrayList = new ArrayList();
			int i = localList.size();
			for (int j = 0;; j++)
			{
				if (j >= i)
				{
					if (!localArrayList.isEmpty())
					{
						this.mPlayerController.setNowPlayingList(
								localArrayList, true);
						this.mPlayerController.open(0);
						showPlayer();
					}
					logger.v("onInitColumnResponse() ---> Exit");
					break;
				}
				SongItem localSongItem = (SongItem) localList.get(j);
				Song localSong = new Song();
				localSong.mContentId = localSongItem.contentid;
				localSong.mAlbum = "<unknown>";
				localSong.mAlbumId = -1;
				localSong.mArtist = localSongItem.singer;
				localSong.mDuration = -1;
				localSong.mId = -1L;
				localSong.mMusicType = MusicType.RADIO.ordinal();
				localSong.mLyric = localSongItem.lrc;
				localSong.mTrack = localSongItem.songname;
				localSong.mUrl = localSongItem.durl1;
				localSong.mUrl2 = localSongItem.durl2;
				localSong.mGroupCode = str;
				localArrayList.add(localSong);
			}
		} else
		{
			return;
		}
	}

	private void onSendHttpRequestFail(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestFail() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(R.string.title_information_common),
				getText(R.string.getfail_data_error_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MusicPlayerActivity.this.mCurrentDialog != null)
						{
							MusicPlayerActivity.this.mCurrentDialog.dismiss();
							MusicPlayerActivity.this.mCurrentDialog = null;
						}
					}
				});
		logger.v("onSendHttpRequestFail() ---> Exit");
	}

	private void onSendHttpRequestTimeOut(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestTimeOut() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(R.string.title_information_common),
				getText(R.string.connect_timeout_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						MusicPlayerActivity.this.mCurrentDialog.dismiss();
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	private void onStopButtonClick()
	{
		logger.v("onStopButtonClick() ---> Enter");
		this.mPlayerController.stop();
		stopRefresh();
		logger.v("onStopButtonClick() ---> Exit");
	}

	private void onTrackEnded()
	{
		logger.v("onTrackEnded() ---> Enter");
		this.mMsgHandler.removeMessages(2);
		this.mMsgHandler.post(getTrackEndUpdateAction());
		this.mMsgHandler.postDelayed(getTrackEndResetAction(), 20L);
		logger.v("onTrackEnded() ---> Exit");
	}

	private void popupWindowForAddPlayList()
	{
		logger.v("popupWindowForAddPlayList() ---> Enter");
		long l;

		if ((Long) mLoveSong.getTag() == null)
			l = 0L;
		else
			l = ((Long) mLoveSong.getTag()).longValue();

		if (l == 0L)
		{
			LinearLayout linearlayout = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.player_add_play_list_window, null);
			List list = mDBController.getAllPlaylists(0);
			((Button) linearlayout
					.findViewById(R.id.orderwindow_createplaylist))
					.setOnClickListener(new android.view.View.OnClickListener()
					{
						public void onClick(View view1)
						{
							createPlayListDialog();
							if (mPopupWindow != null)
							{
								mPopupWindow.dismiss();
								mPopupWindow = null;
							}
						}
					});
			LinearLayout linearlayout1 = (LinearLayout) linearlayout
					.findViewById(R.id.playlist_li);
			int i = 0;
			do
			{
				if (i > list.size())
				{
					mPopupWindow = new PopupWindow(linearlayout, -2, -2);
					mPopupWindow.showAtLocation(mLoveSong, 16, 0, 0);
					return;
				}
				Playlist playlist = (Playlist) list.get(i);
				View view = getLayoutInflater().inflate(
						R.layout.player_add_play_list_item, null);
				Button button = (Button) view.findViewById(R.id.playlist_item);
				button.setTag(Integer.valueOf(playlist.mExternalId));
				if ("cmccwm.mobilemusic.database.default.online.playlist.favorite"
						.equals(playlist.mName))
					button.setText(getText(R.string.playlist_myfav_common)
							.toString());
				else
					button.setText(playlist.mName);
				button.setOnClickListener(addplaylistlistener);
				if (i + 1 == list.size())
					((ImageView) view.findViewById(R.id.playlist_line))
							.setVisibility(View.GONE);
				linearlayout1.addView(view);
				i++;
			} while (true);
		} else
		{
			removePlaylistSong(l);
		}
	}

	private void popupWindowForRating()
	{
		logger.v("popupWindowForRating() ---> Enter");
		View view = getLayoutInflater().inflate(R.layout.player_rating_window,
				null);
		this.mPopupWindow = new PopupWindow(view, -2, -2);
		((ImageButton) view.findViewById(R.id.mark1_view))
				.setOnClickListener(this.marksonglistener);
		((ImageButton) view.findViewById(R.id.mark2_view))
				.setOnClickListener(this.marksonglistener);
		((ImageButton) view.findViewById(R.id.mark3_view))
				.setOnClickListener(this.marksonglistener);
		((ImageButton) view.findViewById(R.id.mark4_view))
				.setOnClickListener(this.marksonglistener);
		((ImageButton) view.findViewById(R.id.mark5_view))
				.setOnClickListener(this.marksonglistener);
		ImageView imageview = (ImageView) view.findViewById(R.id.song_point);
		if ((this.mPoint != null) && (Integer.parseInt(this.mPoint) != -1))
			switch (Integer.parseInt(this.mPoint))
			{
			default:
				imageview.setImageResource(R.drawable.mark_0);
			case 1:
			{
				imageview.setImageResource(R.drawable.mark_1);
			}
				break;
			case 2:
			{
				imageview.setImageResource(R.drawable.mark_2);
			}
				break;
			case 3:
			{
				imageview.setImageResource(R.drawable.mark_3);
			}
				break;
			case 4:
			{
				imageview.setImageResource(R.drawable.mark_4);
			}
				break;
			case 5:
			{
				imageview.setImageResource(R.drawable.mark_5);
			}
				break;
			}
		else
		{
			int i = this.context.getResources().getDimensionPixelSize(
					R.dimen.ratingwindowY);
			this.mPopupWindow.showAtLocation(imageview, 80, 0, i);
			logger.v("popupWindowForRating() ---> Exit");
		}
	}

	private void queueNextRefresh(long paramLong)
	{
		Message localMessage = this.mMsgHandler.obtainMessage(2);
		this.mMsgHandler.removeMessages(2);
		this.mMsgHandler.sendMessageDelayed(localMessage, paramLong);
	}

	private long refreshNow()
	{
		long l = 500L;
		Song song = mPlayerController.getCurrentPlayingItem();
		if (song != null)
		{
			if (mPlayerController.isPlaying())
			{
				MusicType musictype = MusicType.values()[song.mMusicType];
				if (song.isDolby && GlobalSettingParameter.show_dobly_toast
						&& !"LOCALMUSIC".equals(musictype.toString()))
				{
					Toast.makeText(this, R.string.you_can_enjoy_dobly_music, 0)
							.show();
					GlobalSettingParameter.show_dobly_toast = false;
				}
			}
			int i = song.mDuration;
			if (!mPlayerController.isInitialized())
			{
				mProgressBar.setProgress(0);
				if (Util.isOnlineMusic(song))
				{
					if (10 * mPlayerController.getProgressDownloadPercent() == 0)
						mIsNextMusic = true;
					if (mIsNextMusic)
						mProgressBar
								.setSecondaryProgress(10 * mPlayerController
										.getProgressDownloadPercent());
					else
						mProgressBar.setSecondaryProgress(0);
				} else
				{
					mProgressBar.setSecondaryProgress(0);
				}
				mCurrentTimeView.setText(Util.makeTimeString(0L));
			} else
			{
				int j = mPlayerController.getPosition();
				l = 1000 - j % 1000;
				if (j >= 0 && i > 0)
				{
					mCurrentTimeView.setText(Util.makeTimeString(j));
					mTotalTimeView.setText(Util.makeTimeString(i));
					if (mIsInLyric && mCurrentLyric != null)
						mLyricsView.seekByTime(j, true);
					if (mPlayerController.isPlaying())
					{
						mCurrentTimeView.setVisibility(0);
					} else
					{
						if (j >= 0)
						{
							int k = mCurrentTimeView.getVisibility();
							TextView textview = mCurrentTimeView;
							int i1 = 0;
							if (k != 4)
								i1 = 4;
							textview.setVisibility(i1);
						}
						l = 500L;
					}
					if (Util.isOnlineMusic(song)
							&& mPlayerController.getProgressDownloadPercent() != -1)
						mProgressBar
								.setSecondaryProgress(10 * mPlayerController
										.getProgressDownloadPercent());
					else
						mProgressBar.setSecondaryProgress(1000);
					mProgressBar.setProgress((j * 1000) / i);
				} else
				{
					mCurrentTimeView
							.setText(getText(R.string.unknown_time_string_playback_activity));
					mTotalTimeView
							.setText(getText(R.string.unknown_time_string_playback_activity));
					mProgressBar.setProgress(0);
					mProgressBar.setSecondaryProgress(0);
					mLyricsView.setLyrics(null);
				}
			}
		}
		return l;
	}

	private void removePlaylistSong(long l)
	{
		Song song = mPlayerController.getCurrentPlayingItem();
		long l1 = mDBController.getSongIdByContentId(song.mContentId,
				song.mGroupCode);
		if (mDBController.isSongInPlaylist(l, l1, 0)
				&& l1 != -1L
				&& mDBController.deleteSongsFromPlaylist(l, new long[] { l1 },
						0))
		{
			mLoveSong.setTag(Long.valueOf(0L));
			mLoveSong
					.setImageResource(R.drawable.player_activity_function_button_love_nor);
		}
	}

	private void requestFirstRadioSongList(String s)
	{
		char c;
		MMHttpRequest mmhttprequest;
		String s1;
		String s2;
		MMHttpTask mmhttptask;
		if (NetUtil.isNetStateWap())
			c = '\u03ED';
		else
			c = '\u1390';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		if (mCurrentDialog != null)
		{
			mCurrentDialog.dismiss();
			mCurrentDialog = null;
		}
		mCurrentDialog = DialogUtil.showIndeterminateProgressDialog(this,
				R.string.loading_radio_list_activity);
		s1 = s.substring(s.indexOf("rdp2"));
		if (NetUtil.isNetStateWap())
			s2 = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_IP)))
					.append(s1).toString();
		else
			s2 = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_IP)))
					.append(s1).toString();
		mmhttprequest.setURL(s2);
		mmhttprequest.setReqType(c);
		mmhttprequest.addUrlParams("pageno", "1");
		mmhttprequest.addUrlParams("itemcount",
				GlobalSettingParameter.SERVER_INIT_PARAM_ITEM_COUNT);
		mmhttptask = mHttpController.sendRequest(mmhttprequest);
		mCurrentTasks.add(mmhttptask);
	}

	private void requestPushSong(String s)
	{
		char c;
		MMHttpRequest mmhttprequest;
		String s1;
		String s2;
		MMHttpTask mmhttptask;
		if (NetUtil.isNetStateWap())
			c = '\u03EA';
		else
			c = '\u138D';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		if (mCurrentDialog != null)
		{
			mCurrentDialog.dismiss();
			mCurrentDialog = null;
		}
		mCurrentDialog = DialogUtil.showIndeterminateProgressDialog(this,
				R.string.loading_radio_list_activity);
		s1 = s.substring(s.indexOf("rdp2"));
		if (NetUtil.isNetStateWap())
			s2 = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_IP)))
					.append(s1).toString();
		else
			s2 = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_IP)))
					.append(s1).toString();
		mmhttprequest.setURL(s2);
		mmhttprequest.setReqType(c);
		mmhttptask = mHttpController.sendRequest(mmhttprequest);
		mCurrentTasks.add(mmhttptask);
	}

	private void setMusicGarbage()
	{
		logger.v("setMusicGarbage() ---> Enter");
		if (mCurrentDialog != null)
		{
			mCurrentDialog.dismiss();
			mCurrentDialog = null;
		}
		mCurrentDialog = Uiutil.showWaitingDialog(this, mOnCancelListner);
		char c;
		MMHttpRequest mmhttprequest;
		if (NetUtil.isNetStateWap())
			c = '\u041B';
		else
			c = '\u13BE';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		mmhttprequest.addUrlParams("contentid",
				mPlayerController.getCurrentPlayingItem().mContentId);
		mmhttprequest.addUrlParams("op", "add");
		mmhttprequest.addUrlParams("type", "2");
		mmhttprequest.addUrlParams("x-up-calling-line-id",
				GlobalSettingParameter.useraccount.mMDN);
		mCurrentTask = mHttpController.sendRequest(mmhttprequest);
		logger.v("setMusicGarbage() ---> Exit");
	}

	private void setPlayPauseButtonImag(boolean flag)
	{
		logger.v("setPlayPauseButtonImag() ---> Enter");
		if (flag)
			mPlayPauseButton
					.setImageResource(R.drawable.player_activity_button_pause_slt);
		else
			mPlayPauseButton
					.setImageResource(R.drawable.player_activity_button_play_slt);
		logger.v("setPlayPauseButtonImag() ---> Exit");
	}

	private void setRadioMusicFavorite(boolean flag, String s)
	{
		logger.v("setRadioMusicFavorite() ---> Enter");
		if (mCurrentDialog != null)
		{
			mCurrentDialog.dismiss();
			mCurrentDialog = null;
		}
		mCurrentDialog = Uiutil.showWaitingDialog(this, mOnCancelListner);
		char c;
		MMHttpRequest mmhttprequest;
		String s1;
		if (NetUtil.isNetStateWap())
			c = '\u041A';
		else
			c = '\u13BD';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		mmhttprequest.addUrlParams("x-up-calling-line-id",
				GlobalSettingParameter.useraccount.mMDN);
		mmhttprequest.addUrlParams("contentid", s);
		if (flag)
			s1 = "add";
		else
			s1 = "del";
		mmhttprequest.addUrlParams("op", s1);
		mmhttprequest.addUrlParams("type", "1");
		if (flag)
			mLoveSong
					.setImageResource(R.drawable.player_activity_function_button_love_hl);
		else
			mLoveSong
					.setImageResource(R.drawable.player_activity_function_button_love_nor);
		mCurrentTask = mHttpController.sendRequest(mmhttprequest);
		logger.v("setRadioMusicFavorite() ---> Exit");
	}

	private void showPlayer()
	{
		updateLoadingButton();
		Song song = this.mPlayerController.getCurrentPlayingItem();
		if (song == null)
		{
			int i1;
			int i2;
			this.mMarketBtn.setEnabled(false);
			this.mLoveSong.setEnabled(false);
			this.mLoveSong
					.setImageResource(R.drawable.player_activity_function_button_love_disable);
			this.mGarbage.setEnabled(false);
			this.mProgressBar.setProgress(0);
			this.mProgressBar.setSecondaryProgress(0);
			this.mProgressBar.setEnabled(false);
			this.mPlayPauseButton.setEnabled(false);
			this.mPlayNextButton.setEnabled(false);
			this.mPlayPreButton.setEnabled(false);
			this.mRatingBtn.setEnabled(false);
			this.mRatingBtn
					.setImageResource(R.drawable.player_activity_function_button_share_slt);
			this.mShareBtn.setVisibility(4);
			this.mErrorDialogBtn.setVisibility(4);
			this.mLyricsView.setLyrics(null);
			this.mLyricsView.setNoLyricsHint(getText(
					R.string.no_lyric_now_playback_activity).toString());
			this.mCurrentTimeView.setVisibility(4);
			this.mTotalTimeView.setVisibility(4);
			this.mArtistView.setText(" ");
			this.mPlayerAlbumInfoView.clearSingerInfo();
			this.mPlayerAlbumInfoView.clearAlbumInfo();
			this.mPlayerAlbumInfoView.showNothingImage(false);
			this.mImageDownloader.download(null,
					R.drawable.image_default_ablum_for_play_view,
					this.mAlbumImage, null);
			this.mDoblyImageForSong.setVisibility(4);
			i1 = this.mPlayerController.getShuffleMode();
			i2 = this.mPlayerController.getRepeatMode();
			if ((i1 == 1) && (i2 == 2))
			{
				this.mSongsRandom
						.setImageResource(R.drawable.player_activity_function_button_random_disable);

			} else if ((i1 == 0) && (i2 == 1))
			{
				this.mSongsRandom
						.setImageResource(R.drawable.player_activity_function_button_repeat_disable);
			} else if ((i1 == 0) && (i2 == 2))
			{
				this.mSongsRandom
						.setImageResource(R.drawable.player_activity_function_button_sequence_disable);
			} else
			{
				this.mSongsRandom
						.setImageResource(R.drawable.player_activity_function_button_random_disable);
			}
			this.mSongsRandom.setEnabled(false);
		} else
		{

		}

		refreshNow();
		this.mCurrentTimeView.setVisibility(0);
		this.mTotalTimeView.setVisibility(0);
		this.mErrorDialogBtn.setVisibility(0);
		this.mPlayPauseButton.setEnabled(true);
		this.mPlayNextButton.setEnabled(true);
		MusicType musictype = MusicType.values()[song.mMusicType];
		switch (musictype.ordinal())
		{
		default:
		case 3:
		{
			mPlayPreButton.setEnabled(true);
			mMarketBtn.setEnabled(false);
			mLoveSong.setEnabled(false);
			mLoveSong
					.setImageResource(R.drawable.player_activity_function_button_love_disable);
			mSongsRandom.setEnabled(true);
			updatePlayRandomImg();
			mGarbage.setEnabled(false);
			mRatingBtn.setEnabled(false);
			mBtnRecentPlayList.setVisibility(0);
			mShareBtn.setVisibility(4);
			mErrorDialogBtn.setVisibility(4);
			mCurrentSong = null;
			mPlayerAlbumInfoView.showNothingImage(true);
			mPlayerAlbumInfoView.clearSingerInfo();
			mPlayerAlbumInfoView.clearAlbumInfo();
			if (musictype != MusicType.LOCALMUSIC)
			{
				int i = mDBController.getMusicRate(song.mContentId);
				logger.v((new StringBuilder("nw---->显示打分--->")).append(i)
						.append("content: --->").append(song.mContentId)
						.toString());
				int j;
				long l;
				long al[];
				boolean flag;
				int k;
				int i1;
				long l1;
				if (i != -1)
					switch (i)
					{
					case 1: // '\001'
						mRatingBtn.setImageResource(0x7f020100);
						break;

					case 2: // '\002'
						mRatingBtn.setImageResource(0x7f020101);
						break;

					case 3: // '\003'
						mRatingBtn.setImageResource(0x7f020102);
						break;

					case 4: // '\004'
						mRatingBtn.setImageResource(0x7f020103);
						break;

					case 5: // '\005'
						mRatingBtn.setImageResource(0x7f020104);
						break;
					}
				else
					mRatingBtn.setImageResource(0x7f02016f);
			} else
			{
				mRatingBtn.setImageResource(0x7f02016f);
			}
		}
			break;
		case 2:
		{
			int j;
			long l;
			long al[];
			boolean flag;
			int k;
			int i1;
			long l1;
			mPlayPreButton.setEnabled(true);
			mMarketBtn.setEnabled(true);
			mLoveSong.setEnabled(true);
			mSongsRandom.setEnabled(true);
			updatePlayRandomImg();
			mGarbage.setEnabled(false);
			mRatingBtn.setEnabled(true);
			mBtnRecentPlayList.setVisibility(0);
			mShareBtn.setVisibility(0);
			if (mCurrentSong == null || !mCurrentSong.equals(song))
			{
				mPlayerAlbumInfoView.clearSingerInfo();
				mPlayerAlbumInfoView.clearAlbumInfo();
				mPlayerAlbumInfoView.showNothingImage(false);
				mPlayerAlbumInfoView.requestalbuminfo(song.mContentId);
				mPlayerAlbumInfoView.requestsingerinfo(song.mContentId);
				mCurrentSong = song;
			}
			j = mDBController.getMusicRate(song.mContentId);
			if (j == -1)
				j = 0;
			mPoint = (new StringBuilder()).append(j).toString();
			if (!mLoveSong.isEnabled())
			{
				if (song.isDolby)
					mDoblyImageForSong.setVisibility(0);
				else
					mDoblyImageForSong.setVisibility(4);
				if (musictype != MusicType.LOCALMUSIC)
				{
					int i = mDBController.getMusicRate(song.mContentId);
					logger.v((new StringBuilder("nw---->显示打分--->")).append(i)
							.append("content: --->").append(song.mContentId)
							.toString());
					if (i != -1)
						switch (i)
						{
						case 1: // '\001'
							mRatingBtn.setImageResource(0x7f020100);
							break;

						case 2: // '\002'
							mRatingBtn.setImageResource(0x7f020101);
							break;

						case 3: // '\003'
							mRatingBtn.setImageResource(0x7f020102);
							break;

						case 4: // '\004'
							mRatingBtn.setImageResource(0x7f020103);
							break;

						case 5: // '\005'
							mRatingBtn.setImageResource(0x7f020104);
							break;
						}
					else
						mRatingBtn.setImageResource(0x7f02016f);
				} else
				{
					mRatingBtn.setImageResource(0x7f02016f);
				}
			} else
			{
				if (GlobalSettingParameter.useraccount != null)
				{
					l = mDBController.getSongIdByContentId(song.mContentId,
							song.mGroupCode);
					al = mDBController.isSongInPlaylist(l, 0);
					flag = false;
					if (al == null)
					{
						if (flag)
						{
							mLoveSong.setImageResource(0x7f020159);
						} else
						{
							mLoveSong.setTag(Long.valueOf(0L));
							mLoveSong.setImageResource(0x7f02015a);
						}
					} else
					{
						k = al.length;
						i1 = 0;
					}
				}
			}
		}
			break;
		case 1:
		{

		}
		}
	}

	private void stopRefresh()
	{
		setProgressBarIndeterminateVisibility(false);
		this.mMsgHandler.removeMessages(2);
		setPlayPauseButtonImag(false);
		this.mLyricsView.reset();
		this.mLyricsView.seekByTime(0, true);
		this.mCurrentTimeView.setVisibility(0);
		this.mCurrentTimeView.setText(Util.makeTimeString(0L));
		this.mProgressBar.setProgress(0);
		this.mProgressBar.setSecondaryProgress(0);
		this.mProgressBar.setEnabled(false);
	}

	private void updateLoadingButton()
	{
		if (mPlayerController.getIsLoadingData())
		{
			mLoading.setVisibility(View.VISIBLE);
			mPlayPauseButton.setVisibility(View.GONE);
		} else
		{
			mLoading.setVisibility(View.GONE);
			mPlayPauseButton.setVisibility(View.VISIBLE);
		}
	}

	private void updatePlayRandomImg()
	{
		int i = mPlayerController.getShuffleMode();
		int j = mPlayerController.getRepeatMode();
		if (i == 1 && j == 2)
			mSongsRandom
					.setImageResource(R.drawable.player_activity_function_button_random_slt);
		else if (i == 0 && j == 1)
			mSongsRandom
					.setImageResource(R.drawable.player_activity_function_button_repeat_slt);
		else if (i == 0 && j == 2)
			mSongsRandom
					.setImageResource(R.drawable.player_activity_function_button_sequence_slt);
		else
			mSongsRandom
					.setImageResource(R.drawable.player_activity_function_button_random_slt);
	}

	private void updateTrackInfo(boolean flag)
	{
		logger.v("updateTrackInfo() ---> Enter");
		Song song = mPlayerController.getCurrentPlayingItem();
		if (song != null)
		{
			String s;
			String s1;
			if (song.mTrack == null)
				s = "<unknown>";
			else
				s = song.mTrack;
			setTitle(s);
			s1 = mController.getDBController().getDisplayedArtistName(
					song.mArtist);
			if (s1 == null)
				s1 = getText(R.string.unknown_artist_name_db_controller)
						.toString();
			mArtistView
					.setText((new StringBuilder(String.valueOf(song.mTrack)))
							.append("-").append(s1).toString());
			mImageDownloader.download(song.mArtUrl,
					R.drawable.image_default_ablum_for_play_view, mAlbumImage,
					song.mGroupCode);
			if (song.isDolby)
				mDoblyImageForSong.setVisibility(View.VISIBLE);
			else
				mDoblyImageForSong.setVisibility(View.GONE);
			if (song.mDuration <= 0)
			{
				if (mPlayerController.isInitialized()
						&& mPlayerController.getDuration() > 0)
					song.mDuration = mPlayerController.getDuration();
				else
					song.mDuration = 0;
				mTotalTimeView.setText(Util.makeTimeString(song.mDuration));
			}
			if (!(mPlayerController instanceof PlayerControllerImpl))
			{
				boolean flag1;
				if (Util.isOnlineMusic(song)
						&& !mPlayerController.isInitialized())
					flag1 = true;
				else
					flag1 = false;
				setProgressBarIndeterminateVisibility(flag1);
			}
			if (flag)
				if (song.mLyric != null)
				{
					logger.d((new StringBuilder("lyric :")).append(song.mLyric)
							.toString());
					mCurrentLyric = song.mLyric;
					mLyricsView.setLyrics(mCurrentLyric);
				} else
				{
					mCurrentLyric = null;
					mLyricsView.setLyrics(mCurrentLyric);
					getLyric();
				}
			logger.v("updateTrackInfo() ---> Exit");
		}
	}

	private void updateTrackInfoWithoutMusicInfo()
	{
		logger.v("updateTrackInfo() ---> Enter");
		Song song = mPlayerController.getCurrentPlayingItem();
		if (song != null)
		{
			String s;
			String s1;
			if (song.mTrack == null)
				s = "<unknown>";
			else
				s = song.mTrack;
			setTitle(s);
			s1 = mController.getDBController().getDisplayedArtistName(
					song.mArtist);
			if (s1 == null)
				s1 = getText(R.string.unknown_artist_name_db_controller)
						.toString();
			mArtistView
					.setText((new StringBuilder(String.valueOf(song.mTrack)))
							.append("-").append(s1).toString());
			mImageDownloader.download(song.mArtUrl,
					R.drawable.image_default_ablum_for_play_view, mAlbumImage,
					song.mGroupCode);
			if (!(mPlayerController instanceof PlayerControllerImpl))
			{
				boolean flag;
				if (Util.isOnlineMusic(song)
						&& !mPlayerController.isInitialized())
					flag = true;
				else
					flag = false;
				setProgressBarIndeterminateVisibility(flag);
			}
			mLyricsView.setLyrics(null);
			mLyricsView.setNoLyricsHint(getText(
					R.string.search_lyric_now_playback_activity).toString());
			logger.v("updateTrackInfo() ---> Exit");
		}
	}

	public void CancelPreviousReq()
	{
		if (this.mCurrentTask != null)
		{
			this.mHttpController.cancelTask(this.mCurrentTask);
			this.mCurrentTask = null;
			this.mCurrentTasks.remove(this.mCurrentTask);
			setProgressBarIndeterminateVisibility(false);
		}
	}

	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
	{
		boolean bool = false;
		switch (paramMotionEvent.getAction())
		{
		default:
		{
			bool = super.dispatchTouchEvent(paramMotionEvent);
		}
			break;
		case 0:
		{
			if (this.mPopupWindow != null)
			{
				this.mPopupWindow.dismiss();
				this.mPopupWindow = null;
				bool = true;
			}
		}
			break;
		}
		return bool;
	}

	public void getLyric()
	{
		logger.i("getLyric() ---> Enter");
		// new Thread()
		// {
		// public void run()
		// {
		// MusicPlayerActivity.logger.i("getLyric() ---> thread start");
		// if (MusicPlayerActivity.this.mMsgHandler == null)
		// ;
		// Object localObject;
		// Song localSong;
		// do
		// {
		// return;
		// MusicPlayerActivity.this.mMsgHandler.obtainMessage(0)
		// .sendToTarget();
		// localObject = null;
		// localSong = MusicPlayerActivity.this.mPlayerController
		// .getCurrentPlayingItem();
		// } while (localSong == null);
		// if (Util.isOnlineMusic(localSong))
		// ;
		// while (true)
		// {
		// while (true)
		// {
		// try
		// {
		// String str3 = Util.getLyricFromNetwork(
		// localSong.mUrl, localSong.mContentId);
		// localObject = str3;
		// if (new Lyric(
		// Util.getUTF8Bytes((String) localObject),
		// "utf-8").getLineCount() <= 0)
		// break;
		// localSong.mLyric = ((String) localObject);
		// if (MusicPlayerActivity.this.mMsgHandler == null)
		// break;
		// MusicPlayerActivity.this.mMsgHandler.obtainMessage(
		// 1, localSong).sendToTarget();
		// MusicPlayerActivity.logger
		// .i("getLyric() ---> thread end");
		// } catch (Exception localException2)
		// {}
		// if (MusicPlayerActivity.this.mMsgHandler == null)
		// break;
		// MusicPlayerActivity.this.mMsgHandler.obtainMessage(1)
		// .sendToTarget();
		// MusicPlayerActivity.logger.e("Get Lyric error: ",
		// localException2);
		// localObject = null;
		// continue;
		// try
		// {
		// boolean bool = localSong.mUrl
		// .substring(0,
		// 1 + localSong.mUrl.lastIndexOf("/"))
		// .equals(GlobalSettingParameter.LOCAL_PARAM_RINGTONE_STORE_SD_DIR);
		// localObject = null;
		// if (!bool)
		// break;
		// Handler localHandler = MusicPlayerActivity.this.mMsgHandler;
		// localObject = null;
		// if (localHandler == null)
		// break;
		// MusicPlayerActivity.this.mMsgHandler.obtainMessage(
		// 1).sendToTarget();
		// } catch (Exception localException1)
		// {}
		// }
		// if (MusicPlayerActivity.this.mMsgHandler == null)
		// break;
		// MusicPlayerActivity.this.mMsgHandler.obtainMessage(1)
		// .sendToTarget();
		// MusicPlayerActivity.logger.e("Get Lyric error: ",
		// localException1);
		// continue;
		// localObject = Util.getLyricFromLocalFile(localSong.mUrl);
		// if ((localObject == null)
		// || (((String) localObject).length() == 0))
		// {
		// String str1 = MusicPlayerActivity.this.mDBController
		// .queryContentId(localSong.mUrl);
		// String str2 = Util.getLyricFromNetwork(localSong.mUrl,
		// str1);
		// localObject = str2;
		// continue;
		// if (MusicPlayerActivity.this.mMsgHandler == null)
		// break;
		// MusicPlayerActivity.this.mMsgHandler.obtainMessage(1)
		// .sendToTarget();
		// }
		// }
		// }
		// }.start();
		logger.i("getLyric() ---> Exit");
	}

	public void handleMMHttpEvent(Message message)
	{
		boolean flag;
		MMHttpTask mmhttptask = (MMHttpTask) message.obj;
		MMHttpTask mmhttptask1;
		if (mmhttptask != null
				&& (mCurrentTasks.size() != 0 || mCurrentTask != null))
		{
			if (mCurrentTask != null
					&& mCurrentTask.getTransId() == mmhttptask.getTransId())
			{
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				if (mmhttptask.getRequest().getReqType() != 5065
						&& mmhttptask.getRequest().getReqType() != 1061)
				{
					setProgressBarIndeterminateVisibility(false);
					switch (message.what)
					{
					case 3003:
						onHttpResponse(mmhttptask);
						break;

					case 3005:
						setProgressBarIndeterminateVisibility(false);
						break;

					case 3004:
						updateLoadingButton();
						onSendHttpRequestFail(mmhttptask);
						break;

					case 3006:
						updateLoadingButton();
						onSendHttpRequestTimeOut(mmhttptask);
						break;

					case 3007:
						updateLoadingButton();
						Uiutil.ifSwitchToWapDialog(this);
						break;

					case 3008:
						updateLoadingButton();
						Uiutil.ifSwitchToWapDialog(this);
						break;
					}
				}
			} else
			{
				Iterator iterator;
				flag = false;
				iterator = mCurrentTasks.iterator();
				while (iterator.hasNext())
				{
					mmhttptask1 = (MMHttpTask) iterator.next();
					if (mmhttptask.getTransId() == mmhttptask1.getTransId())
						flag = true;
				}
				if (flag)
				{
					mCurrentTasks.remove(mmhttptask);
				}
			}
		}
	}

	public void handlePlayerEvent(Message message)
	{
		logger.v("handlePlayerEvent() ---> Enter");
		switch (message.what)
		{
		case 1003:
		case 1006:
		case 1007:
		case 1008:
		case 1013:
		case 1015:
		case 1016:
		case 1017:
		case 1019:
		default:
		{
			return;
		}
		case 1009:
		{
			onPlayerPrepareStart();
		}
			break;
		case 1002:
		{
			mIsNextMusic = false;
			onTrackEnded();
		}
			break;
		case 1010:
		{
			onPlaybackStart();
		}
			break;
		case 1004:
		{
			mIsNextMusic = false;
			onPlayerError(message);
		}
			break;
		case 1005:
		{
			setProgressBarIndeterminateVisibility(false);
			onStopButtonClick();
			onNetWorkError(message);
		}
			break;
		case 1012:
		{
			if (mPlayerController instanceof PlayerControllerImpl)
				stopRefresh();
			else
				onStopButtonClick();
		}
			break;
		case 1011:
		{
			setPlayPauseButtonImag(mPlayerController.isPlaying());
		}
			break;
		case 1014:
		{
			updateLoadingButton();
			setProgressBarIndeterminateVisibility(false);
			Uiutil.ifSwitchToWapDialog(this);
		}
			break;
		case 1018:
		{
			mLoading.setVisibility(8);
			mPlayPauseButton.setVisibility(0);
			onStopButtonClick();
		}
			break;
		case 1020:
		{
			if (mPlayerController.isPause())
				mMsgHandler.removeMessages(2);
		}
			break;
		}
	}

	public void handleSystemEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
		{
			logger.v("handleSystemEvent() ---> Exit");
			return;
		}
		case 4:
		{
			onMediaEjectEvent();
		}
			break;
		case 21:
		{
			onHeadsetPlugOutEvent();
		}
			break;
		case 20:
		{
			onHeadsetPlugInEvent();
		}
			break;
		case 22:
		{
			finish();
		}
			break;
		}
	}

	public void handleUIEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
		{
			return;
		}
		case 4008:
		{
			showPlayer();
			updateTrackInfoWithoutMusicInfo();
		}
			break;
		case 4009:
		{
			this.mLoading.setVisibility(View.GONE);
			this.mPlayPauseButton.setVisibility(View.VISIBLE);
		}
			break;
		case 4010:
		{
			this.mLoading.setVisibility(View.VISIBLE);
			this.mPlayPauseButton.setVisibility(View.GONE);
		}
			break;
		}
	}

	public void onClick(View paramView)
	{
		if (paramView == this.mBtnHidePlayeActivity)
		{
			if (this.mIsFromPushService)
			{
				Intent localIntent = new Intent(this,
						MobileMusicMainActivity.class);
				localIntent.putExtra("startFromNotification", true);
				startActivity(localIntent);
				overridePendingTransition(R.anim.player_finish_in,
						R.anim.player_finish_out);
			} else
			{
				finish();
				overridePendingTransition(R.anim.player_finish_in,
						R.anim.player_finish_out);
			}
		} else
		{
			if (paramView == mBtnRecentPlayList)
				onPlayListButtonClick();
		}
	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("MusicPlayer onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_music_player_layout);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
		this.context = this;
		this.mController = ((MobileMusicApplication) getApplication())
				.getController();
		this.mPlayerController = this.mController.getPlayerController();
		this.mDBController = this.mController.getDBController();
		this.mHttpController = this.mController.getHttpController();
		this.mPlayerAlbumInfoView = new PlayerAlbumInfoView(this);
		InitViewPager();
		this.mViewDot = ((ImageView) findViewById(R.id.view_dot));
		this.mViewDot
				.setBackgroundResource(this.mViewDotImages[UIGlobalSettingParameter.music_player_pager_index]
						.intValue());
		this.mBtnRecentPlayList = ((ImageButton) findViewById(R.id.btn_recentplaylist));
		this.mBtnHidePlayeActivity = ((ImageButton) findViewById(R.id.btn_close));
		this.mBtnRecentPlayList.setOnClickListener(this);
		this.mBtnHidePlayeActivity.setOnClickListener(this);
		this.mArtistView = ((TextView) findViewById(R.id.artistName));
		this.mCurrentTimeView = ((TextView) findViewById(R.id.currenttime));
		this.mTotalTimeView = ((TextView) findViewById(R.id.totaltime));
		this.mLoading = ((ProgressBar) findViewById(R.id.music_download_progressBar));
		this.mProgressBar = ((SeekBar) findViewById(android.R.id.progress)); // 16908301------102000D
		this.mProgressBar.setMax(1000);
		if ((this.mProgressBar instanceof SeekBar))
			this.mProgressBar.setOnSeekBarChangeListener(this.mSeekListener);
		this.mPlayPauseButton = ((ImageView) findViewById(R.id.statusbar_play_and_pause_button));
		setPlayPauseButtonImag(this.mPlayerController.isPlaying());
		this.mPlayPauseButton.setOnClickListener(this.mPlayControlListener);
		this.mPlayNextButton = ((ImageView) findViewById(R.id.statusbar_next_button));
		this.mPlayNextButton.setOnClickListener(this.mPlayControlListener);
		this.mPlayPreButton = ((ImageView) findViewById(R.id.statusbar_prev_button));
		this.mPlayPreButton.setOnClickListener(this.mPlayControlListener);
		this.mLyricsView = ((LyricsView) ((View) this.mListViews.get(2))
				.findViewById(R.id.lyric_view));
		this.mLyricsView.setNoLyricsHint(getText(
				R.string.no_lyric_now_playback_activity).toString());
		this.mDoblyImageForSong = ((ImageView) ((View) this.mListViews.get(1))
				.findViewById(R.id.dobly_image_for_player_activity));
		this.mShareBtn = ((Button) ((View) this.mListViews.get(1))
				.findViewById(R.id.weibo_share));
		this.mErrorDialogBtn = ((ImageView) ((View) this.mListViews.get(1))
				.findViewById(R.id.show_error_dialog));
		this.mShareBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (MusicPlayerActivity.this.mPlayerController
						.getCurrentPlayingItem() != null)
				{
					String str1 = MusicPlayerActivity.this.mPlayerController
							.getCurrentPlayingItem().mArtist;
					String str2 = MusicPlayerActivity.this.mPlayerController
							.getCurrentPlayingItem().mTrack;
					Intent localIntent = new Intent(
							"android.intent.action.SEND");
					localIntent.setFlags(268435456); // 10000000
					localIntent.putExtra("android.intent.extra.TEXT", "我正在听"
							+ str1 + "的" + str2
							+ ",下载音乐随身听一起来听听http://wm.12530.com");
					localIntent.setType("image/*");
					localIntent.setType("text/*");
					MusicPlayerActivity.this.startActivity(Intent
							.createChooser(localIntent, "分享"));
				} else
				{
					MusicPlayerActivity.this.mShareBtn.setVisibility(4);
				}
			}
		});
		this.mErrorDialogBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (mPlayerController.getCurrentPlayingItem() != null)
				{
					mReportErroeDialog = new Dialog(context,
							R.style.CustomDialogTheme);
					View localView = LayoutInflater.from(context).inflate(
							R.layout.dialog_report_error_message, null);
					Button localButton1 = (Button) localView
							.findViewById(R.id.button1);
					Button localButton2 = (Button) localView
							.findViewById(R.id.button2);
					final CheckBox mSongCheckBox = (CheckBox) localView
							.findViewById(R.id.checkbox1);
					final CheckBox mLyricCheckBox = (CheckBox) localView
							.findViewById(R.id.checkbox2);
					final CheckBox mCoverCheckBox = (CheckBox) localView
							.findViewById(R.id.checkbox3);
					mReportErroeDialog.setContentView(localView);
					mReportErroeDialog.show();
					localButton1.setOnClickListener(new View.OnClickListener()
					{
						public void onClick(View paramAnonymous2View)
						{
							String str1;
							String str2;
							String str3;
							int i;
							if (!mSongCheckBox.isChecked())
							{
								str1 = "0";
							} else
							{
								str1 = "1";
							}
							if (!mLyricCheckBox.isChecked())
							{
								str2 = "0";
							} else
							{
								str2 = "1";
							}
							if (!mCoverCheckBox.isChecked())
							{
								str3 = "0";
							} else
							{
								str3 = "1";
							}
							if (mSongCheckBox.isChecked()
									|| mLyricCheckBox.isChecked()
									|| mCoverCheckBox.isChecked())
							{
								char c;
								MMHttpRequest mmhttprequest;
								if (NetUtil.isNetStateWap())
									c = '\u0425';
								else
									c = '\u13C9';
								mmhttprequest = MMHttpRequestBuilder
										.buildRequest(c);
								mmhttprequest
										.addUrlParams(
												"contentid",
												mPlayerController
														.getCurrentPlayingItem().mContentId);
								mmhttprequest.addUrlParams("songflag", str1);
								mmhttprequest.addUrlParams("singerflag", str2);
								mmhttprequest.addUrlParams("albumflag", str3);
								mmhttprequest
										.addUrlParams(
												"version",
												GlobalSettingParameter.LOCAL_PARAM_VERSION);
								mmhttprequest
										.addUrlParams(
												"ua",
												GlobalSettingParameter.LOCAL_PARAM_USER_AGENT);
								mCurrentTask = mHttpController
										.sendRequest(mmhttprequest);
								mReportErroeDialog.dismiss();
								mReportErroeDialog = null;
								Toast.makeText(context, "提交成功", 0).show();
							} else
							{
								Toast.makeText(context, "请勾选错误信息", 0).show();
							}
						}
					});

					localButton2.setOnClickListener(new View.OnClickListener()
					{
						public void onClick(View paramAnonymous2View)
						{
							MusicPlayerActivity.this.mReportErroeDialog
									.dismiss();
							MusicPlayerActivity.this.mReportErroeDialog = null;
						}
					});
				}
				MusicPlayerActivity.this.mErrorDialogBtn.setVisibility(4);
				return;
			}
		});
		this.mAlbumImage = ((ImageView) ((View) this.mListViews.get(1))
				.findViewById(R.id.ablum_img));
		this.mMarketBtn = ((ImageButton) findViewById(R.id.function_button_market));
		this.mMarketBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				View localView = getLayoutInflater().inflate(
						R.layout.player_order_window, null);
				((Button) localView.findViewById(R.id.orderwindow_settone))
						.setOnClickListener(onMarketBarClickListener);
				((Button) localView.findViewById(R.id.orderwindow_setring))
						.setOnClickListener(onMarketBarClickListener);
				((Button) localView.findViewById(R.id.orderwindow_download))
						.setOnClickListener(onMarketBarClickListener);
				Button localButton = (Button) localView
						.findViewById(R.id.orderwindow_sendsong);
				localButton.setOnClickListener(onMarketBarClickListener);
				if ((GlobalSettingParameter.useraccount != null)
						&& (GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER != null)
						&& (Integer
								.parseInt(GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER) == 3))
				{
					localButton.setClickable(false);
					localButton.setTextColor(0xff888888);
				}
				((Button) localView.findViewById(R.id.orderwindow_recommand))
						.setOnClickListener(onMarketBarClickListener);
				mPopupWindow = new PopupWindow(localView, -2, -2);
				int[] arrayOfInt = new int[2];
				paramAnonymousView.getLocationOnScreen(arrayOfInt);
				int i = (int) context.getResources().getDimension(
						R.dimen.playerXoffset);
				int j = (int) context.getResources().getDimension(
						R.dimen.playerYoffset);
				mPopupWindow.showAtLocation(paramAnonymousView, 48,
						arrayOfInt[0] - i, arrayOfInt[1] - j);
			}
		});
		this.mLoveSong = ((ImageButton) findViewById(R.id.function_button_love));
		this.mLoveSong.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (!NetUtil.isConnection())
					Uiutil.ifSwitchToWapDialog(MusicPlayerActivity.this);
				else
				{
					if (GlobalSettingParameter.useraccount == null)
					{
						mBussinessTYpe = MusicPlayerActivity.BussinessType.FAVORITESONG;
						Uiutil.login(MusicPlayerActivity.this, 0);
						if (mPopupWindow != null)
						{
							mPopupWindow.dismiss();
							mPopupWindow = null;
						}
					} else
					{
						Song song = mPlayerController.getCurrentPlayingItem();
						MusicType localMusicType = MusicType.values()[song.mMusicType];
						switch (localMusicType.ordinal())
						{
						case 1: // '\001'
							MusicPlayerActivity musicplayeractivity = MusicPlayerActivity.this;
							int j = song.like;
							boolean flag = false;
							if (j != 1)
								flag = true;
							musicplayeractivity.setRadioMusicFavorite(flag,
									song.mContentId);
							break;

						case 2: // '\002'
						case 3: // '\003'
							popupWindowForAddPlayList();
							break;
						}
					}
				}
			}
		});
		this.mSongsRandom = ((ImageButton) findViewById(R.id.function_button_random));
		this.mSongsRandom.setOnClickListener(this.mPlayerOrderListener);
		this.mGarbage = ((ImageButton) findViewById(R.id.function_button_garbage));
		this.mGarbage.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (GlobalSettingParameter.useraccount == null)
				{
					mBussinessTYpe = MusicPlayerActivity.BussinessType.GARBAGE;
					Uiutil.login(MusicPlayerActivity.this, 0);
					if (mPopupWindow != null)
					{
						mPopupWindow.dismiss();
						mPopupWindow = null;
					}
				} else
				{
					MusicPlayerActivity.this.setMusicGarbage();
				}
				return;
			}
		});
		this.mRatingBtn = ((ImageButton) findViewById(R.id.function_button_rating));
		this.mRatingBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (GlobalSettingParameter.useraccount == null)
				{
					mBussinessTYpe = MusicPlayerActivity.BussinessType.RATING;
					Uiutil.login(MusicPlayerActivity.this, 0);
					if (mPopupWindow != null)
					{
						mPopupWindow.dismiss();
						mPopupWindow = null;
					}
				} else
				{
					MusicPlayerActivity.this.popupWindowForRating();
				}
			}
		});
		this.mImageDownloader = new UrlImageDownloader(this);
		this.mController.addPlayerEventListener(1004, this);
		this.mController.addPlayerEventListener(1003, this);
		this.mController.addPlayerEventListener(1002, this);
		this.mController.addPlayerEventListener(1008, this);
		this.mController.addPlayerEventListener(1009, this);
		this.mController.addPlayerEventListener(1010, this);
		this.mController.addPlayerEventListener(1012, this);
		this.mController.addPlayerEventListener(1014, this);
		this.mController.addPlayerEventListener(1005, this);
		this.mController.addPlayerEventListener(1011, this);
		this.mController.addPlayerEventListener(1018, this);
		this.mController.addPlayerEventListener(1020, this);
		this.mController.addHttpEventListener(3003, this);
		this.mController.addHttpEventListener(3005, this);
		this.mController.addHttpEventListener(3004, this);
		this.mController.addHttpEventListener(3006, this);
		this.mController.addHttpEventListener(3007, this);
		this.mController.addHttpEventListener(3008, this);
		this.mController.addSystemEventListener(4, this);
		this.mController.addSystemEventListener(21, this);
		this.mController.addSystemEventListener(20, this);
		this.mController.addSystemEventListener(22, this);
		this.mController.addUIEventListener(4008, this);
		this.mController.addUIEventListener(4009, this);
		this.mController.addUIEventListener(4010, this);
		this.mPlayerAlbumInfoView.addListner();
		Bundle localBundle = getIntent().getExtras();
		if (localBundle != null)
		{
			this.mComeForm = localBundle
					.getInt("mobi.mobiemusic.ui.activity.online.onlinemusicsubscribeactivity.from.recommed");
			if (this.mComeForm == 3)
				requestFirstRadioSongList(localBundle
						.getString("mobi.mobiemusic.ui.activity.online.onlinemusicsubscribeactivity.entry.url"));
			int i = localBundle.getInt("pushSongFlag");
			this.mIsFromPushService = localBundle.getBoolean("fromPushService",
					false);
			if (i == 2)
				requestPushSong(localBundle.getString("pushSongUrl"));
		}
		logger.v("MusicPlayer onCreate() ---> Exit");
	}

	public boolean onCreateOptionsMenu(Menu paramMenu)
	{
		paramMenu.add(0, 0, 0, "").setIcon(
				R.drawable.menu_item_localscan_selector);
		paramMenu.add(2, 2, 0, "").setIcon(R.drawable.menu_item_set_selector);
		paramMenu.add(3, 3, 0, "").setIcon(
				R.drawable.menu_item_time_close_selector);
		paramMenu.add(1, 1, 0, "").setIcon(R.drawable.menu_item_exit_selector);
		return super.onCreateOptionsMenu(paramMenu);
	}

	protected void onDestroy()
	{
		logger.v("MusicPlayer onDestroy() ---> Enter");
		this.mController.removePlayerEventListener(1004, this);
		this.mController.removePlayerEventListener(1003, this);
		this.mController.removePlayerEventListener(1002, this);
		this.mController.removePlayerEventListener(1008, this);
		this.mController.removePlayerEventListener(1009, this);
		this.mController.removePlayerEventListener(1010, this);
		this.mController.removePlayerEventListener(1012, this);
		this.mController.removePlayerEventListener(1014, this);
		this.mController.removePlayerEventListener(1005, this);
		this.mController.removePlayerEventListener(1011, this);
		this.mController.removePlayerEventListener(1018, this);
		this.mController.removePlayerEventListener(1020, this);
		this.mController.removeHttpEventListener(3003, this);
		this.mController.removeHttpEventListener(3005, this);
		this.mController.removeHttpEventListener(3004, this);
		this.mController.removeHttpEventListener(3006, this);
		this.mController.removeHttpEventListener(3007, this);
		this.mController.removeHttpEventListener(3008, this);
		this.mController.removeSystemEventListener(4, this);
		this.mController.removeSystemEventListener(21, this);
		this.mController.removeSystemEventListener(20, this);
		this.mController.removeSystemEventListener(22, this);
		this.mController.removeUIEventListener(4008, this);
		this.mController.removeUIEventListener(4009, this);
		this.mController.removeUIEventListener(4010, this);
		this.mMsgHandler.removeMessages(2);
		this.mPlayerAlbumInfoView.removeListner();
		this.mViewPager.removeAllViews();
		this.mViewPager.destroyDrawingCache();
		this.mListViews.clear();
		this.mViewPager = null;
		this.mListViews = null;
		if (this.mPlayerAlbumInfoView != null)
		{
			this.mPlayerAlbumInfoView.destroyDrawingCache();
			this.mPlayerAlbumInfoView = null;
		}
		this.mCurrentTasks = null;
		this.mTrackEndResetAction = null;
		this.mTrackEndUpdateAction = null;
		if (this.mImageDownloader != null)
			this.mImageDownloader.clearCache();
		this.mImageDownloader = null;
		this.mViewDotImages = null;
		this.mPlayPauseButton = null;
		this.mPlayNextButton = null;
		this.mPlayPreButton = null;
		this.mBtnHidePlayeActivity = null;
		this.mBtnRecentPlayList = null;
		this.mProgressBar = null;
		this.mArtistView = null;
		this.mCurrentTimeView = null;
		this.mTotalTimeView = null;
		this.mAlbumImage = null;
		this.mShareBtn = null;
		this.mErrorDialogBtn = null;
		this.mDoblyImageForSong = null;
		this.mMarketBtn = null;
		this.mRatingBtn = null;
		this.mLoveSong = null;
		this.mSongsRandom = null;
		this.mGarbage = null;
		this.mPopupWindow = null;
		super.onDestroy();
		logger.v("MusicPlayer onDestroy() ---> Exit");
	}

	public boolean onKeyDown(int i, KeyEvent keyevent)
	{
		boolean bool = true;
		logger.v("onKeyDown() ---> Enter");
		if (i == 4)
		{
			if (this.mPopupWindow != null)
			{
				this.mPopupWindow.dismiss();
				this.mPopupWindow = null;
				return bool;
			} else
			{
				if (mIsFromPushService)
				{
					Intent intent = new Intent(this,
							MobileMusicMainActivity.class);
					intent.putExtra("startFromNotification", bool);
					startActivity(intent);
					overridePendingTransition(R.anim.player_finish_in,
							R.anim.player_finish_out);
				} else
				{
					finish();
					overridePendingTransition(R.anim.player_finish_in,
							R.anim.player_finish_out);
				}
			}
		} else
		{
			logger.v("onKeyDown() ---> Exit");
			bool = super.onKeyDown(i, keyevent);
		}
		return bool;
	}

	protected void onNewIntent(Intent paramIntent)
	{
		logger.v("onNewIntent() ---> Enter");
		Bundle localBundle = paramIntent.getExtras();
		if (localBundle != null)
		{
			int i = localBundle.getInt("pushSongFlag");
			this.mIsFromPushService = localBundle.getBoolean("fromPushService",
					false);
			if (i == 2)
				requestPushSong(localBundle.getString("pushSongUrl"));
		}
		super.onNewIntent(paramIntent);
		logger.v("onNewIntent() ---> Exit");
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem)
	{
		boolean bool = true;
		switch (paramMenuItem.getItemId())
		{
		default:
			bool = super.onOptionsItemSelected(paramMenuItem);
		case 0:
		{
			Intent localIntent = new Intent(this, LocalScanMusicActivity.class);
			localIntent.putExtra("isFromMusicPlayPage", bool);
			startActivity(localIntent);
			UIGlobalSettingParameter.SHOW_SCAN_CONSEQUENSE = bool;
		}
			break;
		case 1:
		{
			exitApplication();
		}
			break;
		case 2:
		{
			// startActivity(new Intent(this, MobileMusicMoreActivity.class));
		}
			break;
		case 3:
		{
			// startActivity(new Intent(this, TimingClosureActivity.class));
		}
			break;
		}
		return bool;
	}

	public void onPageScrollStateChanged(int paramInt)
	{}

	public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
	{}

	public void onPageSelected(int i)
	{
		if (i == 2)
			mIsInLyric = true;
		else
			mIsInLyric = false;
		mViewDot.setBackgroundResource(mViewDotImages[i].intValue());
		UIGlobalSettingParameter.music_player_pager_index = i;
	}

	protected void onPause()
	{
		logger.v("MusicPlayer onPause() ---> Enter");
		super.onPause();
		logger.v("MusicPlayer onPause() ---> Exit");
	}

	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuItem menuitem = menu.findItem(0);
		if ("mounted".equals(Environment.getExternalStorageState()))
			menuitem.setEnabled(true);
		else
			menuitem.setEnabled(false);
		return super.onPrepareOptionsMenu(menu);
	}

	public void onResume()
	{
		logger.v("MusicPlayer onResume() ---> Enter");
		super.onResume();
		doUnCompleteTask();
		if (mPlayerController != null)
			setPlayPauseButtonImag(mPlayerController.isPlaying());
		if (mPlayerController.getIsLoadingData())
		{
			mLoading.setVisibility(View.VISIBLE);
			mPlayPauseButton.setVisibility(View.GONE);
		} else
		{
			mLoading.setVisibility(View.GONE);
			mPlayPauseButton.setVisibility(View.VISIBLE);
		}
		updateTrackInfo(true);
		queueNextRefresh(refreshNow());
		showPlayer();
		if (UIGlobalSettingParameter.music_player_pager_index == 2
				&& mCurrentLyric != null)
			mIsInLyric = true;
		else
			mIsInLyric = false;
		logger.v("MusicPlayer onResume() ---> Exit");
	}

	private class AsyncTaskPlayer extends AsyncTask<Integer, Void, List<Song>>
	{
		private AsyncTaskPlayer()
		{}

		protected List<Song> doInBackground(Integer[] paramArrayOfInteger)
		{
			switch (paramArrayOfInteger[0].intValue())
			{
			default:
				return null;
			case 0:
			{
				if (mPlayerController.getNowPlayingList().size() != 1)
				{
					long l1 = System.currentTimeMillis();
					if (l1 - mClicktime >= 2000L)
					{
						mClicktime = l1;
						Iterator iterator1;
						if ((mCurrentTasks != null)
								&& (mCurrentTasks.size() > 0))
						{
							iterator1 = mCurrentTasks.iterator();
							while (iterator1.hasNext())
							{
								MMHttpTask mmhttptask1 = (MMHttpTask) iterator1
										.next();
								mHttpController.cancelTask(mmhttptask1);
							}
							mCurrentTasks.clear();
						} else
						{
							mPlayerController.next();
							return null;
						}
					}
				} else
				{
					mPlayerController.seek(0L);
					return null;
				}
			}
				break;
			case 1:
			{
				if (mPlayerController.getNowPlayingList().size() != 1)
				{
					long l = System.currentTimeMillis();
					if (l - mClicktime < 2000L)
					{
						return null;
					} else
					{
						mClicktime = l;
						if (mCurrentTasks == null || mCurrentTasks.size() <= 0)
						{
							mPlayerController.prev();
							return null;
						} else
						{
							Iterator iterator = mCurrentTasks.iterator();
							while (iterator.hasNext())
							{
								MMHttpTask mmhttptask = (MMHttpTask) iterator
										.next();
								mHttpController.cancelTask(mmhttptask);
							}
							mCurrentTasks.clear();
						}
					}
				} else
				{
					mPlayerController.seek(0L);
					return null;
				}
			}
				break;
			}
			return null;
		}

		protected void onCancelled()
		{
			super.onCancelled();
		}

		protected void onPostExecute(List<Song> paramList)
		{
			super.onPostExecute(paramList);
		}

		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		protected void onProgressUpdate(Void[] paramArrayOfVoid)
		{
			super.onProgressUpdate(paramArrayOfVoid);
		}
	}

	public enum BussinessType
	{
		FAVORITESONG("FAVORITESONG", 0), RATING("RATING", 1), GARBAGE(
				"GARBAGE", 2), QUANQU("QUANQU", 3), TONE("TONE", 4), RING(
				"RING", 5), RECOMMAND("RECOMMAND", 6), SENDSONG("SENDSONG", 7), RADIOGARBAGE(
				"RADIOGARBAGE", 8), NONE("NONE", 9);

		private String name;
		private int id;

		private BussinessType(String name, int id)
		{
			this.name = name;
			this.id = id;
		}
	}

	private class MsgHandler extends Handler
	{
		public static final int MSG_TYPE_GET_LYRIC_END = 1;
		public static final int MSG_TYPE_GET_LYRIC_START = 0;
		public static final int MSG_TYPE_LOVESONG = 3;
		public static final int MSG_TYPE_NEXTSONG = 4;
		public static final int MSG_TYPE_PRESONG = 5;
		public static final int MSG_TYPE_REFRESH_UI = 2;

		private MsgHandler()
		{}

		public void handleMessage(Message paramMessage)
		{
			Song localSong1 = mPlayerController.getCurrentPlayingItem();
			switch (paramMessage.what)
			{
			default:
				return;
			case MSG_TYPE_PRESONG:
			{
				AsyncTaskPlayer asynctaskplayer1 = new AsyncTaskPlayer();
				Integer ainteger1[] = new Integer[1];
				ainteger1[0] = Integer.valueOf(1);
				asynctaskplayer1.execute(ainteger1);
				onPrevButtonClick();
				showPlayer();

			}
				break;
			case MSG_TYPE_NEXTSONG:
			{
				AsyncTaskPlayer asynctaskplayer = new AsyncTaskPlayer();
				Integer ainteger[] = new Integer[1];
				ainteger[0] = Integer.valueOf(0);
				asynctaskplayer.execute(ainteger);
				onNextButtonClick();
				showPlayer();

			}
				break;
			case MSG_TYPE_LOVESONG:
			{
				char c;
				MMHttpRequest mmhttprequest;
				MMHttpTask mmhttptask;
				if (NetUtil.isNetStateWap())
					c = '\u03F8';
				else
					c = '\u139C';
				mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
				mmhttprequest.addUrlParams("contentid",
						mPlayerController.getCurrentPlayingItem().mContentId);
				mmhttprequest.addUrlParams("x-up-calling-line-id",
						GlobalSettingParameter.useraccount.mMDN);
				mmhttptask = mHttpController.sendRequest(mmhttprequest);
				mCurrentTasks.add(mmhttptask);
			}
				break;
			case MSG_TYPE_GET_LYRIC_START:
			{
				MusicPlayerActivity.logger.v("handle message: get lyric start");
				mLyricsView.setLyrics(null);
				mLyricsView
						.setNoLyricsHint(getText(
								R.string.search_lyric_now_playback_activity)
								.toString());
				mLyricsView.invalidate();
			}
				break;
			case MSG_TYPE_GET_LYRIC_END:
			{
				logger.v("handle message: get lyric end");
				Song localSong2 = (Song) paramMessage.obj;
				if ((localSong2 != null) && (localSong2.mUrl != null)
						&& (localSong1 != null) && (localSong1.mUrl != null)
						&& (localSong1.mUrl.equalsIgnoreCase(localSong2.mUrl)))
					MusicPlayerActivity.this.mCurrentLyric = localSong2.mLyric;
				MusicPlayerActivity.this.mLyricsView
						.setLyrics(MusicPlayerActivity.this.mCurrentLyric);
				MusicPlayerActivity.this.mLyricsView
						.setNoLyricsHint(MusicPlayerActivity.this.getText(
								R.string.no_lyric_now_playback_activity)
								.toString());
			}
				break;
			case MSG_TYPE_REFRESH_UI:
			{
				long l = MusicPlayerActivity.this.refreshNow();
				MusicPlayerActivity.this.queueNextRefresh(l);
			}
				break;
			}
		}
	}

	public class MyPagerAdapter extends PagerAdapter
	{
		public List<View> mListViews;

		public MyPagerAdapter(List list)
		{
			this.mListViews = list;
		}

		public void destroyItem(View paramView, int paramInt, Object paramObject)
		{
			((ViewPager) paramView).removeView((View) this.mListViews
					.get(paramInt));
		}

		public void finishUpdate(View paramView)
		{}

		public int getCount()
		{
			return this.mListViews.size();
		}

		public Object instantiateItem(View paramView, int paramInt)
		{
			((ViewPager) paramView).addView(
					(View) this.mListViews.get(paramInt), 0);
			return this.mListViews.get(paramInt);
		}

		public boolean isViewFromObject(View view, Object obj)
		{
			boolean flag;
			if (view == obj)
				flag = true;
			else
				flag = false;
			return flag;
		}

		public void restoreState(Parcelable paramParcelable,
				ClassLoader paramClassLoader)
		{}

		public Parcelable saveState()
		{
			return null;
		}

		public void startUpdate(View paramView)
		{}
	}
}