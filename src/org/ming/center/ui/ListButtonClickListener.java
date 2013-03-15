package org.ming.center.ui;

import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.database.UserAccount;
import org.ming.center.http.item.SongListItem;
import org.ming.center.player.PlayerController;
import org.ming.ui.activity.MusicPlayerActivity;
import org.ming.ui.util.Uiutil;
import org.ming.util.SongOnlineManager;
import org.ming.util.Util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cm.omp.fuction.data.CodeMessageObject;

public class ListButtonClickListener implements View.OnClickListener
{
	private MusicPlayerActivity.BussinessType mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
	private Context mContext;
	private Controller mController;
	private SongListItem mCurrentSong = null;
	private DBController mDBController = null;
	private LayoutInflater mInflater;
	private PlayerController mPlayerController = null;
	public PopupWindow mPopupWindow = null;
	private List<SongListItem> mSongListItem;
	private boolean mToastFlag = false;
	SongOnlineManager songOnlineManager;
	private Boolean isGetUrlSuccess = false;
	public View.OnClickListener onMarketBarClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			marketBarclick(paramAnonymousView);
		}
	};

	public ListButtonClickListener()
	{
		mPopupWindow = null;
		mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
		mCurrentSong = null;
		mPlayerController = null;
		mDBController = null;
		mToastFlag = false;
		songOnlineManager = SongOnlineManager.getInstance();
	}

	public ListButtonClickListener(Context context, List list)
	{
		mPopupWindow = null;
		mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
		mCurrentSong = null;
		mPlayerController = null;
		mDBController = null;
		mToastFlag = false;
		mSongListItem = list;
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		mPlayerController = mController.getPlayerController();
		mDBController = mController.getDBController();
		songOnlineManager = SongOnlineManager.getInstance();
	}

	private boolean gotoLogin(
			MusicPlayerActivity.BussinessType paramBussinessType)
	{
		UserAccount localUserAccount = GlobalSettingParameter.useraccount;
		boolean bool = false;
		if (localUserAccount == null)
		{
			this.mBussinessTYpe = paramBussinessType;
			Uiutil.login(this.mContext, 0);
			bool = true;
		}
		return bool;
	}

	private void marketBarclick(View paramView)
	{
		switch (paramView.getId())
		{
		default:
			closePopupWindow();
			return;
		case R.id.orderwindow_addsongtoplaylist:
			startGetSongUrlThread();
			closePopupWindow();
			break;
		case R.id.orderwindow_settone:
			// 设置振铃
			Uiutil.setTone(mContext, mCurrentSong.musicid,
					mCurrentSong.singerName, mCurrentSong.songName);
			closePopupWindow();
			break;
		case R.id.orderwindow_setring:
			// 来电铃声
			Log.d("yuanming", "mCurrentSong.price:" + mCurrentSong.price);
			Log.d("yuanming", "mCurrentSong.crbtValidity:"
					+ mCurrentSong.crbtValidity);
			Uiutil.setViberate(mContext, mCurrentSong.musicid,
					mCurrentSong.singerName, mCurrentSong.songName,
					mCurrentSong.price, mCurrentSong.crbtValidity);
			closePopupWindow();
			break;
		case R.id.orderwindow_download:
			// 下载歌曲
			Uiutil.downloadMusic(mContext, mCurrentSong.musicid,
					mCurrentSong.singerName, mCurrentSong.songName);
			closePopupWindow();
			break;
		case R.id.orderwindow_sendsong:
			// 赠送铃声
			mToastFlag = true;
			Uiutil.sendMusic(mContext, mCurrentSong.musicid,
					mCurrentSong.singerName, mCurrentSong.songName,
					mCurrentSong.price);
			closePopupWindow();
			break;
		case R.id.orderwindow_recommand:
			// 免费推荐
			Uiutil.recommondMusic(mContext, mCurrentSong.contentid,
					mCurrentSong.groupcode);
			closePopupWindow();
			break;
		}
	}

	public boolean closePopupWindow()
	{
		boolean flag;
		if (mPopupWindow != null && mPopupWindow.isShowing())
		{
			mPopupWindow.dismiss();
			mPopupWindow = null;
			flag = true;
		} else
		{
			flag = false;
		}
		return flag;
	}

	public void doUnCompleteTask()
	{
		if (GlobalSettingParameter.useraccount == null)
		{
			this.mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
			return;
		}
		switch (this.mBussinessTYpe.ordinal())
		{
		case 8:
		case 9:
		default:
			Uiutil.downloadMusic(mContext, mCurrentSong.musicid,
					mCurrentSong.singerName, mCurrentSong.songName);
			mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
			break;
		case 4:
			Uiutil.setTone(mContext, mCurrentSong.musicid,
					mCurrentSong.singerName, mCurrentSong.songName);
			mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
			break;
		case 5:
			Uiutil.setViberate(mContext, mCurrentSong.musicid,
					mCurrentSong.singerName, mCurrentSong.songName,
					mCurrentSong.price, mCurrentSong.crbtValidity);
			mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
			break;
		case 6:
			Uiutil.recommondMusic(mContext, mCurrentSong.contentid,
					mCurrentSong.groupcode);
			mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
			break;
		case 7:
			if (GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER == null)
			{
				mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
			} else
			{
				if (Integer
						.parseInt(GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER) == 3)
					if (!mToastFlag)
					{
						mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
					} else
					{
						mToastFlag = false;
						Toast.makeText(
								mContext,
								mContext.getText(R.string.text_my_speical_member_not_send_music),
								1).show();
					}
			}
		case 3:
			Uiutil.sendMusic(mContext, mCurrentSong.musicid,
					mCurrentSong.singerName, mCurrentSong.songName,
					mCurrentSong.price);
			mBussinessTYpe = MusicPlayerActivity.BussinessType.NONE;
		}
	}

	public void onClick(View view)
	{
		if ((this.mPopupWindow != null) && (this.mPopupWindow.isShowing()))
		{
			this.mPopupWindow.dismiss();
			this.mPopupWindow = null;
			return;
		} else
		{
			if ((view.getTag() instanceof Integer))
			{
				int i = Integer.parseInt(view.getTag().toString());
				this.mCurrentSong = ((SongListItem) this.mSongListItem.get(i));
			}
			if ((view.getTag() instanceof SongListItem))
			{
				mCurrentSong = (SongListItem) view.getTag();
			}
			View view1 = mInflater.inflate(
					R.layout.playlist_online_music_player_order_window, null);
			((TextView) view1.findViewById(R.id.windowTitle))
					.setText(mCurrentSong.title);
			((Button) view1.findViewById(R.id.orderwindow_settone))
					.setOnClickListener(onMarketBarClickListener);
			((Button) view1.findViewById(R.id.orderwindow_addsongtoplaylist))
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
					&& GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER
							.equals(String.valueOf(3)))
			{
				button.setTextColor(0xff888888);
				button.setClickable(false);
			}
			// ((Button) view1.findViewById(R.id.orderwindow_recommand))
			// .setOnClickListener(onMarketBarClickListener);
			((Button) view1.findViewById(R.id.orderwindow_delete))
					.setVisibility(View.GONE);
			mPopupWindow = new PopupWindow(view1, -2, -2);
			mPopupWindow.showAtLocation(view, 17, 0, 0);
			doUnCompleteTask();
		}
	}

	public void setListData(List<SongListItem> paramList)
	{
		this.mSongListItem = paramList;
	}

	/**
	 * 获取歌曲在线试听地址
	 */
	private void getSongUrl()
	{
		try
		{
			CodeMessageObject cmo = songOnlineManager.getOnLineListenerSongUrl(
					mContext, mCurrentSong.musicid);
			if ("000000".equals(cmo.getCode()))
			{
				String url = (String) cmo.getObject();
				mCurrentSong.url = url;
				isGetUrlSuccess = true;
				mHandler.sendEmptyMessage(GET_SONG_URL_SUCCESS);
			} else
			{
				Log.d("song", "cmo.getCode() " + cmo.getCode());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void startGetSongUrlThread()
	{
		new Thread()
		{
			public void run()
			{
				getSongUrl();
			}
		}.start();
	}

	private MingHandler mHandler = new MingHandler();;

	private static final int GET_SONG_URL_SUCCESS = 1;

	private class MingHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			switch (msg.what)
			{
			case GET_SONG_URL_SUCCESS:
				// 添加到播放列表
				if (mCurrentSong != null)
				{
					// 如果当前正在播放音乐
					Song song = mPlayerController.getCurrentPlayingItem();
					if (song != null)
					{

						if (mCurrentSong.mMusicType == MusicType.ONLINEMUSIC
								.ordinal())
						{
							mPlayerController.add2NowPlayingList(Util
									.makeSong2(mCurrentSong));
							long l2 = mPlayerController
									.addCurrentTrack2OnlineMusicTable(mCurrentSong);
							if (l2 != -1L)
								mPlayerController
										.addCurrentTrack2RecentPlaylist(
												mCurrentSong, l2);
						} else if (mCurrentSong.mMusicType == MusicType.LOCALMUSIC
								.ordinal())
						{
							Song song2 = Util.makeSong(mCurrentSong);
							song2.mSize = 1L;
							mPlayerController.add2NowPlayingList(song2);
						}
					} else
					// 如果当前没有音乐播放
					{
						if (mCurrentSong.mMusicType == MusicType.ONLINEMUSIC
								.ordinal())
						{
							int j1 = mPlayerController.add2NowPlayingList(Util
									.makeSong2(mCurrentSong));
							long l2 = mPlayerController
									.addCurrentTrack2OnlineMusicTable(mCurrentSong);
							if (l2 != -1L)
								mPlayerController
										.addCurrentTrack2RecentPlaylist(
												mCurrentSong, l2);
							mPlayerController.open2(j1);
						} else if (mCurrentSong.mMusicType == MusicType.LOCALMUSIC
								.ordinal())
						{
							Song song1 = Util.makeSong2(mCurrentSong);
							song1.mSize = 1L;
							int j = mPlayerController.add2NowPlayingList(song1);
							mPlayerController.open(j);
						}
					}
				}
				Toast.makeText(mContext, "歌曲已添加到当前播放列表", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				return;
			}
		}
	}
}