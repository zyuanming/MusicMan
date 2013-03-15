package org.ming.ui.activity;

import java.util.ArrayList;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.download.UrlImageDownloader;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpTask;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerControllerImpl;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.UIEventListener;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.dispatcher.DispatcherEventEnum;
import org.ming.ui.activity.online.MusicOnlinePlaylistActivity;
import org.ming.ui.view.LyricsView;
import org.ming.ui.view.PlayerAlbumInfoView;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayerActivity extends Activity implements
		PlayerEventListener, MMHttpEventListener,
		ViewPager.OnPageChangeListener, SystemEventListener, UIEventListener
{
	public static final int PLAY_NEXT_SONG = 0;
	public static final int PLAY_PRE_SONG = 1;
	private static final MyLogger logger = MyLogger
			.getLogger("MusicPlayerActivity");
	private final int MENU_ITEM_EXIT = 1;
	private final int MENU_ITEM_SCAN_MUSIC = 0;
	private final int MENU_ITEM_SET = 2;
	private final int MENU_ITEM_TIME_CLOSE = 3;
	private android.view.View.OnClickListener addplaylistlistener;
	private Context context;
	private ImageView mAlbumImage;
	private TextView mArtistView;
	private ImageButton mBtnHidePlayeActivity;
	private ImageButton mBtnRecentPlayList;
	private BussinessType mBussinessTYpe;
	private long mClicktime;
	private int mComeForm;
	private Controller mController;
	private Dialog mCurrentDialog;
	String mCurrentLyric;
	private Song mCurrentSong;
	private MMHttpTask mCurrentTask;
	private List mCurrentTasks;
	private TextView mCurrentTimeView;
	private DBController mDBController;
	private ImageView mDoblyImageForSong;
	private ImageView mErrorDialogBtn;
	private ImageButton mGarbage;
	private HttpController mHttpController;
	private UrlImageDownloader mImageDownloader;
	private boolean mIsFromPushService;
	private boolean mIsInLyric;
	private boolean mIsNextMusic;
	private List mListViews;
	private ProgressBar mLoading;
	private ImageButton mLoveSong;
	private LyricsView mLyricsView;
	private ImageButton mMarketBtn;
	private final Handler mMsgHandler = new MsgHandler();
	private android.content.DialogInterface.OnCancelListener mOnCancelListner;
	private android.view.View.OnClickListener mPlayControlListener;
	private ImageView mPlayNextButton;
	private ImageView mPlayPauseButton;
	private ImageView mPlayPreButton;
	private PlayerAlbumInfoView mPlayerAlbumInfoView;
	private PlayerController mPlayerController;
	private Toast mPlayerOrder;
	private android.view.View.OnClickListener mPlayerOrderListener;
	private String mPoint;
	private PopupWindow mPopupWindow;
	private SeekBar mProgressBar;
	private ImageButton mRatingBtn;
	private Dialog mReportErroeDialog;
	private android.widget.SeekBar.OnSeekBarChangeListener mSeekListener;
	private Button mShareBtn;
	private ImageButton mSongsRandom;
	private TextView mTotalTimeView;
	private Runnable mTrackEndResetAction;
	private Runnable mTrackEndUpdateAction;
	private ImageView mViewDot;
	private Integer mViewDotImages[];
	private ViewPager mViewPager;
	public android.view.View.OnClickListener onMarketBarClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setContentView(R.layout.activity_music_player_layout);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
		this.context = this;
		Integer[] arrayOfInteger = new Integer[3];
		arrayOfInteger[0] = Integer
				.valueOf(R.drawable.dot_1_for_player_album_info);
		arrayOfInteger[1] = Integer.valueOf(R.drawable.dot_2_for_player_album);
		arrayOfInteger[2] = Integer.valueOf(R.drawable.dot_3_for_player_lrc);
		mViewDotImages = arrayOfInteger;
		this.mController = ((MobileMusicApplication) getApplication())
				.getController();
		this.mPlayerController = this.mController.getPlayerController();
		mDBController = mController.getDBController();
		mPlayerAlbumInfoView = new PlayerAlbumInfoView(this);
		InitViewPager();
		mViewDot = (ImageView) findViewById(R.id.view_dot);
		mViewDot.setBackgroundResource(mViewDotImages[UIGlobalSettingParameter.music_player_pager_index]
				.intValue());
		mArtistView = (TextView) findViewById(R.id.artistName);
		mCurrentTimeView = (TextView) findViewById(R.id.currenttime);
		mTotalTimeView = (TextView) findViewById(R.id.totaltime);
		mLoading = (ProgressBar) findViewById(R.id.music_download_progressBar);
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
		mErrorDialogBtn = (ImageView) ((View) mListViews.get(1))
				.findViewById(R.id.show_error_dialog);
		this.mBtnHidePlayeActivity = ((ImageButton) findViewById(R.id.btn_close));
		mImageDownloader = new UrlImageDownloader(this);
		mSongsRandom = (ImageButton) findViewById(R.id.function_button_random);
		// mSongsRandom.setOnClickListener(mPlayerOrderListener);
		this.mBtnHidePlayeActivity.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent localIntent = new Intent(MusicPlayerActivity.this,
						MobileMusicMainActivity.class);
				localIntent.putExtra("startFromNotification", true);
				startActivity(localIntent);
				overridePendingTransition(R.anim.player_finish_in,
						R.anim.player_finish_out);
			}
		});
		// 打开正在播放列表
		this.mBtnRecentPlayList = ((ImageButton) findViewById(R.id.btn_recentplaylist));
		this.mBtnRecentPlayList.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				Intent localIntent = new Intent(MusicPlayerActivity.this,
						MusicOnlinePlaylistActivity.class);
				localIntent.putExtra("startFromButtonRecentPlayList", true);
				startActivity(localIntent);
			}
		});
		this.mProgressBar = ((SeekBar) findViewById(R.id.progress)); // 16908301------102000D
		this.mProgressBar.setMax(1000);
		this.mProgressBar.setEnabled(false);
		if (mProgressBar instanceof SeekBar)
			mProgressBar.setOnSeekBarChangeListener(mSeekListener);
		this.mPlayPauseButton = ((ImageView) findViewById(R.id.statusbar_play_and_pause_button));
		setPlayPauseButtonImag(mPlayerController.isPlaying());
		this.mPlayPauseButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (mPlayerController.isInteruptByCall())
				{
					Toast.makeText(context, R.string.user_calling,
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (mPlayerController.isInitialized())
				{
					if (mPlayerController.isPlaying())
					{
						mPlayerController.pause();
						setPlayPauseButtonImag(mPlayerController.isPlaying());
					} else
					{
						// 如果当前播放的音乐是在线音乐或者是广播
						if (((mPlayerController.getCurrentPlayingItem().mMusicType == MusicType.ONLINEMUSIC
								.ordinal()) || (mPlayerController
								.getCurrentPlayingItem().mMusicType == MusicType.RADIO
								.ordinal()))
								&& (NetUtil.isConnection()))
						{
							mPlayerController.start();
							setPlayPauseButtonImag(mPlayerController
									.isPlaying());
						} else if (mPlayerController.getCurrentPlayingItem().mMusicType == MusicType.LOCALMUSIC
								.ordinal())
						{
							mPlayerController.start();
							setPlayPauseButtonImag(mPlayerController
									.isPlaying());
						} else
						{
							Toast.makeText(context,
									R.string.wlan_disconnect_title_util, 1)
									.show();
							mProgressBar.setProgress(0);
							mProgressBar.setSecondaryProgress(0);
							if (mPlayerController.isPlayRecommendSong())
								mPlayerController
										.openRecommendSong(mPlayerController
												.getNowPlayingItemPosition());
							else
								mPlayerController.open(mPlayerController
										.getNowPlayingItemPosition());
						}
					}
				}
			}
		});
		this.mPlayNextButton = ((ImageView) findViewById(R.id.statusbar_next_button));
		this.mPlayNextButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				logger.d("click to play next song");
				Message localMessage = mMsgHandler
						.obtainMessage(MsgHandler.MSG_CLICK_NEXT_SONG);
				mMsgHandler.removeMessages(MsgHandler.MSG_CLICK_NEXT_SONG);
				mMsgHandler.sendMessageDelayed(localMessage, 0L);
			}
		});
		this.mPlayPreButton = ((ImageView) findViewById(R.id.statusbar_prev_button));
		this.mPlayPreButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				logger.d("click to play previous song");
				Message localMessage = mMsgHandler
						.obtainMessage(MsgHandler.MSG_CLICK_PRE_SONG);
				mMsgHandler.removeMessages(MsgHandler.MSG_CLICK_PRE_SONG);
				mMsgHandler.sendMessageDelayed(localMessage, 0L);

			}
		});

		this.mMarketBtn = (ImageButton) findViewById(R.id.function_button_market);
		this.mMarketBtn
				.setOnClickListener(new android.view.View.OnClickListener()
				{

					public void onClick(View view)
					{
						View view1 = getLayoutInflater().inflate(
								R.layout.player_order_window, null);
						((Button) view1.findViewById(R.id.orderwindow_settone))
								.setOnClickListener(onMarketBarClickListener);
						((Button) view1.findViewById(R.id.orderwindow_setring))
								.setOnClickListener(onMarketBarClickListener);
						((Button) view1.findViewById(R.id.orderwindow_download))
								.setOnClickListener(onMarketBarClickListener);
						Button button = (Button) view1
								.findViewById(R.id.orderwindow_sendsong);
						button.setOnClickListener(onMarketBarClickListener);
						if (GlobalSettingParameter.useraccount != null
								&& GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER != null
								&& Integer
										.parseInt(GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER) == 3)
						{
							button.setClickable(false);
							button.setTextColor(0xff888888);
						}
						((Button) view1
								.findViewById(R.id.orderwindow_recommand))
								.setOnClickListener(onMarketBarClickListener);
						mPopupWindow = new PopupWindow(view1, -2, -2);
						int ai[] = new int[2];
						view.getLocationOnScreen(ai);
						int j = (int) context.getResources().getDimension(
								R.dimen.playerXoffset);
						int k = (int) context.getResources().getDimension(
								R.dimen.playerYoffset);
						mPopupWindow.showAtLocation(view, 48, ai[0] - j, ai[1]
								- k);
					}
				});
		mLoveSong = (ImageButton) findViewById(R.id.function_button_love);
		mGarbage = (ImageButton) findViewById(0x7f050066);
		mGarbage.setOnClickListener(new android.view.View.OnClickListener()
		{
			public void onClick(View view)
			{
				// setMusicGarbage();
			}
		});

		mRatingBtn = (ImageButton) findViewById(0x7f050067);
		mRatingBtn.setOnClickListener(new android.view.View.OnClickListener()
		{
			public void onClick(View view)
			{

				// popupWindowForRating();
			}

		});

		mShareBtn = (Button) ((View) mListViews.get(1))
				.findViewById(R.id.weibo_share);
		mShareBtn.setOnClickListener(new android.view.View.OnClickListener()
		{

			public void onClick(View view)
			{
				// if (mPlayerController.getCurrentPlayingItem() != null)
				// {
				// String s = mPlayerController.getCurrentPlayingItem().mArtist;
				// String s1 = mPlayerController.getCurrentPlayingItem().mTrack;
				// Intent intent = new Intent("android.intent.action.SEND");
				// intent.setFlags(0x10000000);
				// intent.putExtra(
				// "android.intent.extra.TEXT",
				// (new StringBuilder("我正在听")).append(s).append("的")
				// .append(s1)
				// .append(",下载音乐随身听一起来听听http://wm.12530.com")
				// .toString());
				// intent.setType("image/*");
				// intent.setType("text/*");
				// startActivity(Intent.createChooser(intent, "分享"));
				// } else
				// {
				// mShareBtn.setVisibility(4);
				// }
			}
		});

		mLyricsView = (LyricsView) ((View) mListViews.get(2))
				.findViewById(R.id.lyric_view);
		mLyricsView.setNoLyricsHint(getText(
				R.string.no_lyric_now_playback_activity).toString());
		mDoblyImageForSong = (ImageView) ((View) mListViews.get(1))
				.findViewById(R.id.dobly_image_for_player_activity);
		mAlbumImage = (ImageView) ((View) mListViews.get(1))
				.findViewById(R.id.ablum_img);
	}

	protected void onResume()
	{
		logger.v("MusicPlayer onResume() ---> Enter");
		super.onResume();
		if (mPlayerController != null)
			setPlayPauseButtonImag(mPlayerController.isPlaying());
		if (mPlayerController.getIsLoadingData())
		{
			mLoading.setVisibility(0);
			mPlayPauseButton.setVisibility(8);
		} else
		{
			mLoading.setVisibility(8);
			mPlayPauseButton.setVisibility(0);
		}
		queueNextRefresh(refreshNow());
		showPlayer();
		if (UIGlobalSettingParameter.music_player_pager_index == 2
				&& mCurrentLyric != null)
			mIsInLyric = true;
		else
			mIsInLyric = false;
		logger.v("MusicPlayer onResume() ---> Exit");
	}

	private void InitViewPager()
	{
		mViewPager = (ViewPager) findViewById(R.id.music_player_middle_slide_Pager);
		mListViews = new ArrayList();
		LayoutInflater layoutinflater = getLayoutInflater();
		mListViews.add(mPlayerAlbumInfoView);
		mListViews.add(layoutinflater.inflate(
				R.layout.music_player_album_view_layout, null));
		mListViews.add(layoutinflater.inflate(
				R.layout.music_player_full_lrc_view_layout, null));
		mViewPager.setAdapter(new MyPagerAdapter(mListViews));
		mViewPager
				.setCurrentItem(UIGlobalSettingParameter.music_player_pager_index);
		mViewPager.getFocusedChild();
		mViewPager.setOnPageChangeListener(this);
	}

	public class MyPagerAdapter extends PagerAdapter
	{

		public List mListViews;

		public void destroyItem(View view, int i, Object obj)
		{
			((ViewPager) view).removeView((View) mListViews.get(i));
		}

		public void finishUpdate(View view)
		{}

		public int getCount()
		{
			return mListViews.size();
		}

		public Object instantiateItem(View view, int i)
		{
			((ViewPager) view).addView((View) mListViews.get(i), 0);
			return mListViews.get(i);
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

		public void restoreState(Parcelable parcelable, ClassLoader classloader)
		{}

		public Parcelable saveState()
		{
			return null;
		}

		public void startUpdate(View view)
		{}

		public MyPagerAdapter(List list)
		{
			mListViews = list;
		}
	}

	private void updateLoadingButton()
	{
		if (mPlayerController.getIsLoadingData())
		{
			mLoading.setVisibility(0);
			mPlayPauseButton.setVisibility(8);
		} else
		{
			mLoading.setVisibility(8);
			mPlayPauseButton.setVisibility(0);
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

	private void showPlayer()
	{
		Song song;
		updateLoadingButton();
		int j;
		long l;
		long al[];
		boolean flag;
		long l1;
		song = mPlayerController.getCurrentPlayingItem();
		if (song != null)
		{
			MusicType musictype;
			refreshNow();
			mCurrentTimeView.setVisibility(0);
			mTotalTimeView.setVisibility(0);
			mErrorDialogBtn.setVisibility(0);
			mPlayPauseButton.setEnabled(true);
			mPlayNextButton.setEnabled(true);
			musictype = MusicType.values()[song.mMusicType];
			switch (musictype.ordinal())
			{
			case 1:
				this.mRatingBtn.setImageResource(R.drawable.marked_1);
				break;
			case 2:
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
								mLoveSong
										.setImageResource(R.drawable.player_activity_function_button_love_hl);
							} else
							{
								mLoveSong.setTag(Long.valueOf(0L));
								mLoveSong
										.setImageResource(R.drawable.player_activity_function_button_love_nor);
							}
						} else
						{
							for (int i1 = 0; i1 < al.length; i1++)
							{

								l1 = al[i1];
								if (mDBController.getPlaylistByID(l1, 0) == null)
								{
									flag = false;
									mLoveSong.setTag(Long.valueOf(l1));
								}
							}
							if (flag)
							{
								mLoveSong
										.setImageResource(R.drawable.player_activity_function_button_love_hl);
							} else
							{
								mLoveSong.setTag(Long.valueOf(0L));
								mLoveSong
										.setImageResource(R.drawable.player_activity_function_button_love_nor);
							}
							mLoveSong
									.setImageResource(R.drawable.player_activity_function_button_love_nor);
							if (song.isDolby)
								mDoblyImageForSong.setVisibility(0);
							else
								mDoblyImageForSong.setVisibility(4);
							if (mCurrentSong == null
									|| !mCurrentSong.equals(song))
							{
								mPlayerAlbumInfoView.clearSingerInfo();
								mPlayerAlbumInfoView.clearAlbumInfo();
								mPlayerAlbumInfoView
										.requestalbuminfo(song.mContentId);
								mPlayerAlbumInfoView
										.requestsingerinfo(song.mContentId);
								mCurrentSong = song;
							}
							mShareBtn.setVisibility(0);
							mPlayPreButton.setEnabled(false);
							mMarketBtn.setEnabled(true);
							mLoveSong.setEnabled(true);
							mSongsRandom.setEnabled(false);
							mGarbage.setEnabled(true);
							mRatingBtn.setEnabled(true);
							mBtnRecentPlayList.setVisibility(4);
							if (song.like == 1)
								mLoveSong
										.setImageResource(R.drawable.player_activity_function_button_love_hl);
							else
								mLoveSong
										.setImageResource(R.drawable.player_activity_function_button_love_nor);
						}
					}
				}
				break;
			case 3:
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
			default:
				break;
			}

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
						mRatingBtn.setImageResource(R.drawable.marked_1);
						break;

					case 2: // '\002'
						mRatingBtn.setImageResource(R.drawable.marked_2);
						break;

					case 3: // '\003'
						mRatingBtn.setImageResource(R.drawable.marked_3);
						break;

					case 4: // '\004'
						mRatingBtn.setImageResource(R.drawable.marked_4);
						break;

					case 5: // '\005'
						mRatingBtn.setImageResource(R.drawable.marked_5);
						break;
					}
				else
					mRatingBtn
							.setImageResource(R.drawable.player_activity_function_button_share_slt);
			} else
			{
				mRatingBtn
						.setImageResource(R.drawable.player_activity_function_button_share_slt);
			}
		} else
		{
			mMarketBtn.setEnabled(false);
			mLoveSong.setEnabled(false);
			mLoveSong
					.setImageResource(R.drawable.player_activity_function_button_love_disable);
			mGarbage.setEnabled(false);
			mProgressBar.setProgress(0);
			mProgressBar.setSecondaryProgress(0);
			mProgressBar.setEnabled(false);
			mPlayPauseButton.setEnabled(false);
			mPlayNextButton.setEnabled(false);
			mPlayPreButton.setEnabled(false);
			mRatingBtn.setEnabled(false);
			mRatingBtn
					.setImageResource(R.drawable.player_activity_function_button_share_slt);
			mShareBtn.setVisibility(4);
			mErrorDialogBtn.setVisibility(4);
			mLyricsView.setLyrics(null);
			mLyricsView.setNoLyricsHint(getText(
					R.string.no_lyric_now_playback_activity).toString());
			mCurrentTimeView.setVisibility(4);
			mTotalTimeView.setVisibility(4);
			mArtistView.setText(" ");
			mPlayerAlbumInfoView.clearSingerInfo();
			mPlayerAlbumInfoView.clearAlbumInfo();
			mPlayerAlbumInfoView.showNothingImage(false);
			mImageDownloader.download(null,
					R.drawable.image_default_ablum_for_play_view, mAlbumImage,
					null);
			mDoblyImageForSong.setVisibility(4);
			int j1 = mPlayerController.getShuffleMode();
			int k1 = mPlayerController.getRepeatMode();
			if (j1 == 1 && k1 == 2)
				mSongsRandom
						.setImageResource(R.drawable.player_activity_function_button_random_disable);
			else if (j1 == 0 && k1 == 1)
				mSongsRandom
						.setImageResource(R.drawable.player_activity_function_button_repeat_disable);
			else if (j1 == 0 && k1 == 2)
				mSongsRandom
						.setImageResource(R.drawable.player_activity_function_button_sequence_disable);
			else
				mSongsRandom
						.setImageResource(R.drawable.player_activity_function_button_random_disable);
			mSongsRandom.setEnabled(false);
		}
	}

	@Override
	protected void onDestroy()
	{
		logger.v("MusicPlayer onDestroy() ---> Enter");
		mController.removePlayerEventListener(1004, this);
		mController.removePlayerEventListener(1003, this);
		mController.removePlayerEventListener(1002, this);
		mController.removePlayerEventListener(1008, this);
		mController.removePlayerEventListener(1009, this);
		mController.removePlayerEventListener(1010, this);
		mController.removePlayerEventListener(1012, this);
		mController.removePlayerEventListener(1014, this);
		mController.removePlayerEventListener(1005, this);
		mController.removePlayerEventListener(1011, this);
		mController.removePlayerEventListener(1018, this);
		mController.removePlayerEventListener(1020, this);
		mController.removeHttpEventListener(3003, this);
		mController.removeHttpEventListener(3005, this);
		mController.removeHttpEventListener(3004, this);
		mController.removeHttpEventListener(3006, this);
		mController.removeHttpEventListener(3007, this);
		mController.removeHttpEventListener(3008, this);
		mController.removeSystemEventListener(4, this);
		mController.removeSystemEventListener(21, this);
		mController.removeSystemEventListener(20, this);
		mController.removeSystemEventListener(22, this);
		mController.removeUIEventListener(4008, this);
		mController.removeUIEventListener(4009, this);
		mController.removeUIEventListener(4010, this);
		mMsgHandler.removeMessages(2);
		mPlayerAlbumInfoView.removeListner();
		mViewPager.removeAllViews();
		mViewPager.destroyDrawingCache();
		mListViews.clear();
		if (mPlayerAlbumInfoView != null)
		{
			mPlayerAlbumInfoView.destroyDrawingCache();
			mPlayerAlbumInfoView = null;
		}
		mCurrentTasks = null;
		mTrackEndResetAction = null;
		mTrackEndUpdateAction = null;
		if (mImageDownloader != null)
			mImageDownloader.clearCache();
		mImageDownloader = null;
		super.onDestroy();
		logger.v("MusicPlayer onDestroy() ---> Exit");
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

	@Override
	public void handleUIEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSystemEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int i)
	{
		if (i == 2)
			mIsInLyric = true;
		else
			mIsInLyric = false;
		mViewDot.setBackgroundResource(mViewDotImages[i].intValue());
		UIGlobalSettingParameter.music_player_pager_index = i;
	}

	@Override
	public void handleMMHttpEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

	private void stopRefresh()
	{
		logger.v("stopRefresh() ---> Enter");
		this.mMsgHandler.removeMessages(0);
		this.mProgressBar.setProgress(0);
		this.mProgressBar.setSecondaryProgress(0);
		this.mProgressBar.setEnabled(false);
		logger.v("stopRefresh() ---> Exit");
	}

	private class MsgHandler extends Handler
	{
		public static final int MSG_CLICK_NEXT_SONG = 1;
		public static final int MSG_CLICK_PRE_SONG = 2;
		public static final int MSG_TYPE_REFRESH_UI = 0;

		private MsgHandler()
		{}

		public void handleMessage(Message paramMessage)
		{
			switch (paramMessage.what)
			{
			default:
			case MSG_TYPE_REFRESH_UI:
			{
				long l = refreshNow();
				queueNextRefresh(l);
			}
				break;
			case MSG_CLICK_NEXT_SONG:
			{
				mPlayerController.next();
			}
				break;
			case MSG_CLICK_PRE_SONG:
			{
				mPlayerController.prev();
			}
			}
		}
	}

	private long refreshNow()
	{
		long l = 500L;
		logger.v("refreshNow() ---> Enter");
		Song song = mPlayerController.getCurrentPlayingItem();
		if (song != null)
		{
			int i = song.mDuration;
			if (!mPlayerController.isInitialized())
			{
				mProgressBar.setProgress(0);
				if (Util.isOnlineMusic(song))
					mProgressBar.setSecondaryProgress(10 * mPlayerController
							.getProgressDownloadPercent());
				else
					mProgressBar.setSecondaryProgress(0);
			} else
			{
				int j = mPlayerController.getPosition();
				l = 1000 - j % 1000;
				if (j >= 0 && i > 0)
				{
					if (!mPlayerController.isPlaying())
						l = 500L;
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
					mProgressBar.setProgress(0);
					mProgressBar.setSecondaryProgress(0);
				}
				logger.v("refreshNow() ---> Exit");
			}
		}
		return l;
	}

	private void queueNextRefresh(long paramLong)
	{
		logger.v("queueNextRefresh() ---> Enter");
		Message localMessage = this.mMsgHandler.obtainMessage(0);
		this.mMsgHandler.removeMessages(0);
		this.mMsgHandler.sendMessageDelayed(localMessage, paramLong);
		logger.v("queueNextRefresh() ---> Exit");
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 0, 0, "").setIcon(R.drawable.menu_item_localscan_selector);
		menu.add(2, 2, 0, "").setIcon(R.drawable.menu_item_set_selector);
		menu.add(3, 3, 0, "").setIcon(R.drawable.menu_item_time_close_selector);
		menu.add(1, 1, 0, "").setIcon(R.drawable.menu_item_exit_selector);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void handlePlayerEvent(Message paramMessage)
	{
		logger.v("handlePlayerEvent() ---> Enter");
		switch (paramMessage.what)
		{
		case DispatcherEventEnum.PLAYER_EVENT_TRACK_ENDED:
			if (mPlayerController instanceof PlayerControllerImpl)
				stopRefresh();
			break;
		default:
		case DispatcherEventEnum.PLAYER_EVENT_NOTFOUND_MUSIC:
		case DispatcherEventEnum.PLAYER_EVENT_BUFFER_UPDATED:
		case DispatcherEventEnum.PLAYER_EVENT_META_CHANGED:
		case DispatcherEventEnum.PLAYER_EVENT_PREPARE_START:
		case DispatcherEventEnum.PLAYER_EVENT_NO_RIGHTS_LISTEN_ONLINE_LISTEN:
		case DispatcherEventEnum.PLAYER_EVENT_RETRY_PLAY:
		case DispatcherEventEnum.PLAYER_EVENT_RETRY_DOWNLOAD:
		case DispatcherEventEnum.PLAYER_EVENT_NO_LOGIN_LISTEN:
			logger.v("handlePlayerEvent() ---> Exit");
			break;
		case DispatcherEventEnum.PLAYER_EVENT_PREPARED_ENDED:
			Song song = mPlayerController.getCurrentPlayingItem();
			if (song != null)
			{
				if (song.mDuration <= 0)
				{
					if (mPlayerController.isInitialized()
							&& mPlayerController.getDuration() > 0)
						song.mDuration = mPlayerController.getDuration();
					else
						song.mDuration = 0;
				}
			}
			break;
		case DispatcherEventEnum.PLAYER_EVENT_ERROR_OCCURED:
		case DispatcherEventEnum.PLAYER_EVENT_NETWORK_ERROR:
		case DispatcherEventEnum.PLAYER_EVENT_WAP_CLOSED:
			mPlayPauseButton.setVisibility(0);
			if (mPlayerController instanceof PlayerControllerImpl)
				stopRefresh();
			break;
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_START:
			mPlayPauseButton.setVisibility(View.VISIBLE);
			queueNextRefresh(refreshNow());
			break;
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_PAUSE: // 暂停
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_STOP: // 停止
			mProgressBar.setProgress(0);
			mProgressBar.setSecondaryProgress(0);
			break;
		}
		logger.v("handlePlayerEvent() ---> Exit");
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

}
