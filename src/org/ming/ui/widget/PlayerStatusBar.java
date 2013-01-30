package org.ming.ui.widget;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.download.UrlImageDownloader;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerControllerImpl;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.ui.UIEventListener;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

public class PlayerStatusBar extends RelativeLayout implements
		PlayerEventListener, UIEventListener
{
	private static boolean isLoading = false;
	private static final MyLogger logger = MyLogger
			.getLogger("PlayerStatusBar");
	private static final String songTag = "[12530.com]";
	private Context mContext;
	private Controller mController;
	private UrlImageDownloader mImageDownloader;
	private final Handler mMsgHandler = new MsgHandler();
	private ImageButton mNextPlayBtn = null;
	private ImageView mPlayActivityButton = null;
	private ImageButton mPlayPauseBtn = null;
	private PlayerController mPlayerController = null;
	private ImageButton mPrePlayBtn = null;
	private ProgressBar mProgressBar = null;
	private SeekBar mSeekBar;
	private Button mTitle = null;

	public PlayerStatusBar(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		logger.v("PlayerStatusBar() ---> Enter");
		this.mImageDownloader = new UrlImageDownloader(paramContext);
		this.mContext = paramContext;
		initialize(this.mContext);
		logger.v("PlayerStatusBar() ---> Exit");
	}

	private void changeSong()
	{
		logger.v("changeSong() ---> Enter");
		Song localSong = this.mPlayerController.getCurrentPlayingItem();
		if (localSong != null)
		{
			if (mPlayerController.isPause())
			{
				mPlayPauseBtn
						.setImageResource(R.drawable.musicplayer_statusbar_button_play_slt);
			} else if (!mPlayerController.isPlaying())
				mPlayPauseBtn
						.setImageResource(R.drawable.musicplayer_statusbar_button_pause_slt);
			setTitle(localSong.mTrack, localSong.mArtist);
			mImageDownloader.download(localSong.mArtUrl,
					R.drawable.musicplayer_statusbar_defimage,
					mPlayActivityButton, localSong.mGroupCode);
			mNextPlayBtn.setEnabled(true);
			mPlayPauseBtn.setEnabled(true);
			mPrePlayBtn.setEnabled(true);
			if (localSong.mMusicType == MusicType.RADIO.ordinal())
				mPrePlayBtn.setEnabled(false);
			else
				mPrePlayBtn.setEnabled(true);
			mSeekBar.setProgress(0);
			mSeekBar.setSecondaryProgress(0);
		} else
		{
			mTitle.setText(R.string.player_status_default_tile);
			mPlayPauseBtn
					.setImageResource(R.drawable.musicplayer_statusbar_button_play_slt);
			mSeekBar.setProgress(0);
			mSeekBar.setSecondaryProgress(0);
			isLoading = false;
			mProgressBar.setVisibility(View.INVISIBLE);
			mPlayPauseBtn.setVisibility(View.VISIBLE);
			mImageDownloader.download(null,
					R.drawable.musicplayer_statusbar_defimage,
					this.mPlayActivityButton, null);
		}
		logger.v("changeSong() ---> Exit");
	}

	private void queueNextRefresh(long paramLong)
	{
		logger.v("queueNextRefresh() ---> Enter");
		Message localMessage = this.mMsgHandler.obtainMessage(0);
		this.mMsgHandler.removeMessages(0);
		this.mMsgHandler.sendMessageDelayed(localMessage, paramLong);
		logger.v("queueNextRefresh() ---> Exit");
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
				mSeekBar.setProgress(0);
				if (Util.isOnlineMusic(song))
					mSeekBar.setSecondaryProgress(10 * mPlayerController
							.getProgressDownloadPercent());
				else
					mSeekBar.setSecondaryProgress(0);
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
						mSeekBar.setSecondaryProgress(10 * mPlayerController
								.getProgressDownloadPercent());
					else
						mSeekBar.setSecondaryProgress(1000);
					mSeekBar.setProgress((j * 1000) / i);
				} else
				{
					mSeekBar.setProgress(0);
					mSeekBar.setSecondaryProgress(0);
				}
				logger.v("refreshNow() ---> Exit");
			}
		}
		return l;
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

	public void handlePlayerEvent(Message paramMessage)
	{
		logger.v("handlePlayerEvent() ---> Enter");
		switch (paramMessage.what)
		{
		case 1002:
		case 1007:
		case 1008:
		case 1009:
		case 1010:
		case 1014:
		case 1017:
		case 1018:
		{
			logger.v("handlePlayerEvent() ---> Exit");
		}
			break;
		case 1003:
		{
			if (mPlayerController instanceof PlayerControllerImpl)
				stopRefresh();
			refreshPlayerStatusBar();
		}
			break;
		case 1004:
		{
			Song song = mPlayerController.getCurrentPlayingItem();
			if (song == null)
			{
				return;
			} else
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
		}
			break;
		default:
		case 1005:
		case 1006:
		case 1015:
		{
			isLoading = false;
			mProgressBar.setVisibility(4);
			mPlayPauseBtn.setVisibility(0);
			if (mPlayerController instanceof PlayerControllerImpl)
				stopRefresh();
			refreshPlayerStatusBar();
		}
			break;
		case 1011:
		{
			isLoading = false;
			mProgressBar.setVisibility(8);
			mPlayPauseBtn.setVisibility(0);
			refreshPlayerStatusBar();
			queueNextRefresh(refreshNow());
		}
			break;
		case 1012:
		case 1013:
		{
			refreshPlayerStatusBar();
		}
			break;
		}
		logger.v("handlePlayerEvent() ---> Exit");
	}

	public void handleUIEvent(Message paramMessage)
	{
		logger.v("handleUIEvent() ---> Enter");
		switch (paramMessage.what)
		{
		default:
		case 4008:
		{
			changeSong();
		}
			break;
		case 4010:
		{
			isLoading = true;
			this.mProgressBar.setVisibility(View.VISIBLE);
			this.mPlayPauseBtn.setVisibility(View.INVISIBLE);
		}
			break;
		case 4009:
		{
			isLoading = false;
			this.mProgressBar.setVisibility(View.INVISIBLE);
			this.mPlayPauseBtn.setVisibility(View.VISIBLE);
		}
			break;
		}
		logger.v("handleUIEvent() ---> Exit");
	}

	public void initialize(Context paramContext)
	{
		logger.v("initialize() ---> Enter");
		((LayoutInflater) paramContext.getSystemService("layout_inflater"))
				.inflate(R.layout.music_player_status_bar_layout, this);

		// 专辑图片按钮
		this.mPlayActivityButton = ((ImageView) findViewById(R.id.statusbar_list_button));
		this.mPlayActivityButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				// Intent localIntent = new
				// Intent(PlayerStatusBar.this.mContext,
				// MusicPlayerActivity.class);
				// localIntent.putExtra("PLAYERTYPE", "ONLINEMUSIC");
				// PlayerStatusBar.this.mContext.startActivity(localIntent);
			}
		});

		// 暂停
		this.mPlayPauseBtn = ((ImageButton) findViewById(R.id.statusbar_play_and_pause_button));
		this.mPlayPauseBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				logger.v("mPlayPauseBtn ---> onClick");
				if (mPlayerController.isInteruptByCall())
				{
					Toast.makeText(mContext, R.string.user_calling,
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (mPlayerController.isInitialized())
				{
					if (mPlayerController.isPlaying())
					{
						mPlayerController.pause();
						refreshPlayerStatusBar();
					}
				}
				if (((mPlayerController.getCurrentPlayingItem().mMusicType == MusicType.ONLINEMUSIC
						.ordinal()) || (mPlayerController
						.getCurrentPlayingItem().mMusicType == MusicType.RADIO
						.ordinal()))
						&& (NetUtil.isConnection()))
				{
					mPlayerController.start();
				} else if (mPlayerController.getCurrentPlayingItem().mMusicType == MusicType.LOCALMUSIC
						.ordinal())
				{
					mPlayerController.start();
				} else
				{
					Toast.makeText(mContext,
							R.string.wlan_disconnect_title_util, 1).show();
					mProgressBar.setProgress(0);
					mProgressBar.setSecondaryProgress(0);
					if (mPlayerController.isPlayRecommendSong())
						mPlayerController.openRecommendSong(mPlayerController
								.getNowPlayingItemPosition());
					else
						mPlayerController.open(mPlayerController
								.getNowPlayingItemPosition());
				}
			}
		});

		// 上一首
		this.mPrePlayBtn = ((ImageButton) findViewById(R.id.statusbar_prev_button));
		this.mPrePlayBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				Message localMessage = mMsgHandler.obtainMessage(2);
				mMsgHandler.removeMessages(2);
				mMsgHandler.sendMessageDelayed(localMessage, 0L);
			}
		});

		// 下一首
		this.mNextPlayBtn = ((ImageButton) findViewById(R.id.statusbar_next_button));
		this.mNextPlayBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				Message localMessage = mMsgHandler.obtainMessage(1);
				mMsgHandler.removeMessages(1);
				mMsgHandler.sendMessageDelayed(localMessage, 0L);
			}
		});

		// 进度条
		this.mProgressBar = ((ProgressBar) findViewById(R.id.statusbar_progressBar));
		this.mTitle = ((Button) findViewById(R.id.statusbar_title));
		this.mTitle.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				// Intent localIntent = new
				// Intent(PlayerStatusBar.this.mContext,
				// MusicPlayerActivity.class);
				// localIntent.putExtra("PLAYERTYPE", "ONLINEMUSIC");
				// PlayerStatusBar.this.mContext.startActivity(localIntent);
			}
		});
		setOnTouchListener(new View.OnTouchListener()
		{
			public boolean onTouch(View paramAnonymousView,
					MotionEvent paramAnonymousMotionEvent)
			{
				return true;
			}
		});
		this.mSeekBar = ((SeekBar) findViewById(R.id.statusbar_progress));
		this.mSeekBar.setMax(1000);
		this.mSeekBar.setEnabled(false);
		this.mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		this.mPlayerController = this.mController.getPlayerController();
		logger.v("initialize() ---> Exit");
	}

	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
	}

	public void refreshPlayerStatusBar()
	{
		logger.v("refreshPlayerStatusBar() ---> Enter");
		Song song = mPlayerController.getCurrentPlayingItem();
		if (song != null)
		{
			if (mPlayerController.isPlaying())
			{
				MusicType musictype = MusicType.values()[song.mMusicType];
				if (song.isDolby && GlobalSettingParameter.show_dobly_toast
						&& !"LOCALMUSIC".equals(musictype.toString()))
				{
					Toast.makeText(mContext,
							R.string.you_can_enjoy_dobly_music, 0).show();
					GlobalSettingParameter.show_dobly_toast = false;
				}
			}
			if (mPlayerController.isPlaying())
			{
				mMsgHandler.removeMessages(0);
				queueNextRefresh(refreshNow());
				mPlayPauseBtn
						.setImageResource(R.drawable.musicplayer_statusbar_button_pause_slt);
			} else
			{
				mMsgHandler.removeMessages(0);
				mPlayPauseBtn
						.setImageResource(R.drawable.musicplayer_statusbar_button_play_slt);
			}
			mImageDownloader.download(song.mArtUrl,
					R.drawable.musicplayer_statusbar_defimage,
					mPlayActivityButton, song.mGroupCode);
		} else
		{
			mImageDownloader.download(null,
					R.drawable.musicplayer_statusbar_defimage,
					mPlayActivityButton, null);
		}
		logger.v("refreshPlayerStatusBar() ---> Exit");
	}

	public void registEventListener()
	{
		logger.d("registEventListener ----> enter");
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
		this.mController.addUIEventListener(4008, this);
		this.mController.addUIEventListener(4009, this);
		this.mController.addUIEventListener(4010, this);
		mTitle.setText(R.string.player_status_default_tile);
		if (mPlayerController.getCurrentPlayingItem() == null)
		{
			isLoading = false;
			mTitle.setText(R.string.player_status_default_tile);
			mPlayPauseBtn
					.setImageResource(R.drawable.musicplayer_statusbar_button_play_slt);
			mSeekBar.setProgress(0);
			mSeekBar.setSecondaryProgress(0);
			mProgressBar.setVisibility(View.INVISIBLE);
			mPlayPauseBtn.setVisibility(View.VISIBLE);
			mImageDownloader.download(null,
					R.drawable.musicplayer_statusbar_defimage,
					mPlayActivityButton, null);
			if (mPlayerController.getNowPlayingList().size() == 0)
			{
				mPlayPauseBtn.setEnabled(false);
				mPrePlayBtn.setEnabled(false);
				mNextPlayBtn.setEnabled(false);
			}
		} else
		{
			isLoading = mPlayerController.getIsLoadingData();
			changeSong();
			refreshPlayerStatusBar();
			if (isLoading)
			{
				mProgressBar.setVisibility(View.VISIBLE);
				mPlayPauseBtn.setVisibility(View.INVISIBLE);
			} else
			{
				mProgressBar.setVisibility(View.INVISIBLE);
				mPlayPauseBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	public void setTitle(String paramString1, String paramString2)
	{
		logger.v("setTitle() ---> Enter");
		String str1 = null;
		if (paramString2 != null)
		{
			String str3 = this.mController.getDBController()
					.getDisplayedArtistName(paramString2);
			str1 = null;
			if (str3 != null)
				str1 = str3.trim();
		}
		if (str1 == null)
			str1 = this.mContext.getText(
					R.string.unknown_artist_name_db_controller).toString();
		if (str1.contains("[12530.com]"))
			str1 = str1.substring(0, str1.indexOf("[12530.com]"));
		String str2 = paramString1;
		if (str2.contains("[12530.com]"))
			str2 = str2.substring(0, str2.indexOf("[12530.com]"));
		this.mTitle.setText(str2 + "-" + str1);
		logger.v("setTitle() ---> Exit");
	}

	public void unRegistEventListener()
	{
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
		this.mController.removeUIEventListener(4008, this);
		this.mController.removeUIEventListener(4009, this);
		this.mController.removeUIEventListener(4010, this);
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
				long l = PlayerStatusBar.this.refreshNow();
				PlayerStatusBar.this.queueNextRefresh(l);
			}
				break;
			case MSG_CLICK_NEXT_SONG:
			{
				PlayerStatusBar.this.mPlayerController.next();
				PlayerStatusBar.this.refreshPlayerStatusBar();
			}
				break;
			case MSG_CLICK_PRE_SONG:
			{
				PlayerStatusBar.this.mPlayerController.prev();
				PlayerStatusBar.this.refreshPlayerStatusBar();
			}
			}
		}
	}
}