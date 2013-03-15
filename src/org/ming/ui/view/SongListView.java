package org.ming.ui.view;

/**
 * 各个榜单对应的歌曲列表信息，信息的显示和获取都是在这里
 */
import java.util.ArrayList;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpTask;
import org.ming.center.http.item.SongListItem;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.ui.ListButtonClickListener;
import org.ming.center.ui.UIEventListener;
import org.ming.dispatcher.Dispatcher;
import org.ming.dispatcher.DispatcherEventEnum;
import org.ming.ui.activity.online.SongMusicInfo;
import org.ming.ui.adapter.MobileMusicRecommendListItemAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.widget.PlayerStatusBar;
import org.ming.util.MyLogger;
import org.ming.util.SongOnlineManager;
import org.ming.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cm.omp.fuction.data.CodeMessageObject;

public class SongListView extends LinearLayout implements MMHttpEventListener,
		PlayerEventListener, BaseViewInterface, UIEventListener
{
	private static final MyLogger logger = MyLogger.getLogger("SongListView");
	public static ListButtonClickListener mListButtonClickListener;
	private boolean DownloadLoginFlag;
	private boolean LOAD_MORE;
	private Button mBtnBatchDownload;
	private Button mBtnPlayAll;
	private Context mContext;
	private Controller mController;
	private Dialog mCurrentDialog;
	private int mCurrentPageNo;
	private MMHttpTask mCurrentTask;
	private HttpController mHttpController;
	private int mIconrid;
	private boolean mInital;
	private Dispatcher mDispatcher;
	private boolean mIsRatingVisable;
	private LayoutInflater mLayoutInflater;
	SongOnlineManager songOnlineManager;
	private ArrayList<SongMusicInfo> songMusicInfos;
	private static int songNum;
	private static Thread thread;
	private List<SongListItem> mSongListData = new ArrayList<SongListItem>();

	// 加载更多
	private View.OnClickListener mLoadMoreOnClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			logger.v("mLoadMoreOnClickListener() ---> Exit");
			mReqMoreProgress.setVisibility(0);
			mTargetPageNo = (1 + mCurrentPageNo);
			if ((mTargetPageNo > 1) && (mTargetPageNo <= mPageTotalCount))
				// requestContentListData();
				logger.v("mLoadMoreOnClickListener() ---> Exit");
		}
	};
	private View mLoadMoreView;
	private MobileMusicRecommendListItemAdapter mMobileMusicRecommendListItemAdapter;
	private ImageView mNothingView;
	private LinearLayout mOperationButtonLayout;
	private int mPageTotalCount;
	private PlayerController mPlayerController;
	private PlayerStatusBar mPlayerStatusBar;
	private ProgressBar mReqMoreProgress;
	private LinearLayout mRetryLayout;
	AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener()
	{
		int firstItem = -1;
		int totalItem = 0;
		int visibleItem = 0;

		public void onScroll(AbsListView paramAnonymousAbsListView,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3)
		{
			if (SongListView.this.LOAD_MORE)
			{
				this.firstItem = paramAnonymousInt1;
				this.visibleItem = paramAnonymousInt2;
				this.totalItem = paramAnonymousInt3;
			}
		}

		public void onScrollStateChanged(AbsListView paramAnonymousAbsListView,
				int paramAnonymousInt)
		{
			if ((LOAD_MORE) && (paramAnonymousInt == 0)
					&& (this.firstItem + this.visibleItem == this.totalItem))
			{
				mLoadMoreOnClickListener.onClick(mLoadMoreView);
				LOAD_MORE = false;
			}
		}
	};

	/**
	 * 单击歌曲列表的中间内容时直接开始在线播放音乐
	 */
	private AdapterView.OnItemClickListener mSongListItemOnItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int i, long l)
		{
			logger.v("nw----<arg2>--->" + i + ":-->" + mSongListData.size());
			if (i < mSongListData.size() && i >= 0)
			{
				startGetSongUrlThread(i);
				songNum = i;
			}
			notifyDataChange();
		}
	};
	private ListView mSongListView = null;
	private int mTargetPageNo = 1;
	private String mURL = null;
	private String mName = null;

	public SongListView(Context context)
	{
		super(context);
		mSongListView = null;
		mPlayerController = null;
		mSongListData = new ArrayList();
		mURL = null;
		mBtnPlayAll = null;
		mBtnBatchDownload = null;
		mIconrid = 0;
		mIsRatingVisable = true;
		mCurrentPageNo = 1;
		mPageTotalCount = -1;
		mTargetPageNo = 1;
		mInital = false;
		LOAD_MORE = true;
		DownloadLoginFlag = false;
		mMobileMusicRecommendListItemAdapter = null;
		mContext = context;
		songOnlineManager = SongOnlineManager.getInstance();
		inital();
	}

	public SongListView(Context context, AttributeSet attributeset)
	{
		super(context, attributeset);
		mSongListView = null;
		mPlayerController = null;
		mSongListData = new ArrayList();
		mURL = null;
		mBtnPlayAll = null;
		mBtnBatchDownload = null;
		mIconrid = 0;
		mIsRatingVisable = true;
		mCurrentPageNo = 1;
		mPageTotalCount = -1;
		mTargetPageNo = 1;
		mInital = false;
		LOAD_MORE = true;
		DownloadLoginFlag = false;
		mMobileMusicRecommendListItemAdapter = null;
		mContext = context;
		inital();
	}

	private void doUnCompleteTask()
	{
		// if (GlobalSettingParameter.useraccount != null && DownloadLoginFlag)
		// {
		// DownloadLoginFlag = false;
		// Intent intent = new Intent(mContext,
		// MusicOnlineBatchDownloadActivity.class);
		// intent.putParcelableArrayListExtra("DOWNLOADDATA",
		// (ArrayList) mSongListData);
		// mContext.startActivity(intent);
		// }
	}

	private boolean gotoLogin()
	{
		boolean flag = true;
		if (GlobalSettingParameter.useraccount == null)
		{
			DownloadLoginFlag = flag;
			Uiutil.login(mContext, 0);
		} else
		{
			flag = false;
		}
		return flag;
	}

	private void onHttpResponse(MMHttpTask paramMMHttpTask)
	{
		int i = paramMMHttpTask.getRequest().getReqType();
		byte[] arrayOfByte = paramMMHttpTask.getResponseBody();
		switch (i)
		{
		default:
			return;
		case 1005:
		case 1011:
		case 5008:
		case 5015:
			break;
		}
	}

	private void onPlayPauseButtonClick()
	{
		logger.v("onPlayPauseButtonClick() ---> Enter");
		if (mPlayerController.isInteruptByCall())
		{
			Toast.makeText(mContext, R.string.user_calling, Toast.LENGTH_SHORT)
					.show();
		} else
		{
			if (mPlayerController.isInitialized())
			{
				if (mPlayerController.isPlaying())
					mPlayerController.pause();
				else
					mPlayerController.start();
			} else
			{
				mPlayerController.open(mPlayerController
						.getNowPlayingItemPosition());
			}
			logger.v("onPlayPauseButtonClick() ---> Exit");
		}
	}

	private void onSendHttpRequestFail(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestFail() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
				this.mContext,
				this.mContext.getText(R.string.title_information_common),
				this.mContext.getText(R.string.getfail_data_error_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (SongListView.this.mCurrentDialog != null)
						{
							SongListView.this.mCurrentDialog.dismiss();
							SongListView.this.mCurrentDialog = null;
						}
					}
				});
		logger.v("onSendHttpRequestFail() ---> Exit");
	}

	private void onSendHttpRequestTimeOut(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestTimeOut() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
				this.mContext,
				this.mContext.getText(R.string.title_information_common),
				this.mContext.getText(R.string.connect_timeout_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (SongListView.this.mCurrentDialog != null)
						{
							SongListView.this.mCurrentDialog.dismiss();
							SongListView.this.mCurrentDialog = null;
						}
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	private int playAll()
	{
		ArrayList localArrayList = new ArrayList();
		Object[] arrayOfObject;
		int i;
		if ((this.mSongListData != null) && (!this.mSongListData.isEmpty()))
		{
			arrayOfObject = this.mSongListData.toArray();
			i = arrayOfObject.length;
			for (int j = 0;; j++)
			{
				if (j >= i)
				{
					this.mPlayerController.add2NowPlayingList(localArrayList);
					this.mPlayerController.open(this.mPlayerController
							.checkSongInNowPlayingList(Util
									.makeSong((SongListItem) this.mSongListData
											.get(0))));
					return -1;
				}
				Object localObject = arrayOfObject[j];
				if (localObject != null)
					localArrayList.add(Util
							.makeSong((SongListItem) localObject));
				long l = this.mPlayerController
						.addCurrentTrack2OnlineMusicTable((SongListItem) localObject);
				if (l != -1L)
					this.mPlayerController.addCurrentTrack2RecentPlaylist(
							(SongListItem) localObject, l);
			}
		} else
		{
			return -1;
		}
	}

	public void addListner()
	{
		logger.v("addListner() ----> Enter");
		this.mPlayerStatusBar.registEventListener();
		this.mController.addHttpEventListener(3003, this);
		this.mController.addHttpEventListener(3005, this);
		this.mController.addHttpEventListener(3004, this);
		this.mController.addHttpEventListener(3006, this);
		this.mController.addHttpEventListener(3007, this);
		this.mController.addHttpEventListener(3008, this);
		this.mController.addPlayerEventListener(1002, this);
		this.mController.addPlayerEventListener(1010, this);
		this.mController.addPlayerEventListener(1012, this);
		this.mController.addPlayerEventListener(1011, this);
		this.mController.addPlayerEventListener(1014, this);
		this.mController.addUIEventListener(4008, this);
		this.mController.addHttpEventListener(
				DispatcherEventEnum.Http_UpdateData_Sucess, this);
		this.mController.addHttpEventListener(
				DispatcherEventEnum.Http_GetSongUrl_Sucess, this);
		logger.v("addListner() ----> Exit");
	}

	public void clearData()
	{
		this.mSongListData.clear();
	}

	/**
	 * 根据榜单的Url获取相应的榜单歌曲信息
	 */
	public void getDataFromURL()
	{
		logger.v("getDataFromURL() ----> Enter");
		if (!this.mInital)
		{
			this.mRetryLayout.setVisibility(View.GONE);
			this.mNothingView.setVisibility(View.VISIBLE);
			startGetSongListDataThread();
			this.mCurrentDialog = DialogUtil.show1BtnProgressDialog(
					this.mContext, R.string.loading, R.string.cancel,
					new View.OnClickListener()
					{
						public void onClick(View paramAnonymousView)
						{
							if (SongListView.this.mCurrentDialog != null)
							{
								SongListView.this.mCurrentDialog.dismiss();
								SongListView.this.mCurrentDialog = null;
								SongListView.this.mCurrentTask = null;
								SongListView.this.mNothingView
										.setVisibility(View.GONE);
								SongListView.this.mRetryLayout
										.setVisibility(View.VISIBLE);
								((Button) SongListView.this.mRetryLayout
										.findViewById(R.id.refresh_btn))
										.setOnClickListener(new View.OnClickListener()
										{
											public void onClick(
													View paramAnonymous2View)
											{
												mRetryLayout
														.setVisibility(View.GONE);
												mNothingView
														.setVisibility(View.VISIBLE);
												if (thread != null
														&& thread.isAlive())
												{
													thread.interrupt();
													thread = null;
												}
												getDataFromURL();
											}
										});
							}
						}
					});
		} else
		{
			this.mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
			logger.v("getDataFromURL() ----> Exit");
		}
	}

	@Override
	public void handleMMHttpEvent(Message message)
	{
		logger.v("handleMMHttpEvent ----> Enter");
		switch (message.what)
		{
		case DispatcherEventEnum.Http_UpdateData_Sucess:
			logger.v("handle DispatcherEventEnum.Http_UpdateData_Sucess");
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			mRetryLayout.setVisibility(View.GONE);
			mNothingView.setVisibility(View.GONE);
			refreshSongListData();
			Toast.makeText(mContext, "获取数据成功", Toast.LENGTH_SHORT).show();
			break;
		case DispatcherEventEnum.Http_GetSongUrl_Sucess:
			SongListItem songlistitem;
			songlistitem = (SongListItem) mSongListData.get(songNum);
			// 如果当前正在播放音乐
			Song song = mPlayerController.getCurrentPlayingItem();
			if (song != null)
			{
				if (song.mMusicType == MusicType.ONLINEMUSIC.ordinal()
						&& song.mContentId == songlistitem.contentid)
					onPlayPauseButtonClick();
				else if (song.mMusicType == MusicType.LOCALMUSIC.ordinal()
						&& song.mUrl.equalsIgnoreCase(songlistitem.url))
					onPlayPauseButtonClick();
				else if (songlistitem.mMusicType == MusicType.ONLINEMUSIC
						.ordinal())
				{
					int j1 = mPlayerController.add2NowPlayingList(Util
							.makeSong2(songlistitem));
					long l2 = mPlayerController
							.addCurrentTrack2OnlineMusicTable(songlistitem);
					if (l2 != -1L)
						mPlayerController.addCurrentTrack2RecentPlaylist(
								songlistitem, l2);
					mPlayerController.open2(j1);
				} else if (songlistitem.mMusicType == MusicType.LOCALMUSIC
						.ordinal())
				{
					Song song2 = Util.makeSong(songlistitem);
					song2.mSize = 1L;
					int i1 = mPlayerController.add2NowPlayingList(song2);
					mPlayerController.open2(i1);
				}
			} else
			// 如果当前没有音乐播放
			{
				if (songlistitem.mMusicType == MusicType.ONLINEMUSIC.ordinal())
				{
					// int k = mPlayerController.add2NowPlayingList(Util
					// .makeSong2(songlistitem));
					// long l1 = mPlayerController
					// .addCurrentTrack2OnlineMusicTable(songlistitem);
					// if (l1 != -1L)
					// mPlayerController.addCurrentTrack2RecentPlaylist(
					// songlistitem, l1);
					// mPlayerController.open2(k);
					// if (player != null && player.isPlaying())
					// {
					// player.stop();
					// player = null;
					// } else
					// {
					// player = new MediaPlayer();
					// }
					// try
					// {
					// Log.d("song---",
					// "song.url " + mSongListData.get(songNum).url);
					// player.setDataSource(mSongListData.get(songNum).url);
					// player.prepare();
					// player.start();
					// } catch (Exception e)
					// {
					// e.printStackTrace();
					// }

					int j1 = mPlayerController.add2NowPlayingList(Util
							.makeSong2(songlistitem));
					long l2 = mPlayerController
							.addCurrentTrack2OnlineMusicTable(songlistitem);
					if (l2 != -1L)
						mPlayerController.addCurrentTrack2RecentPlaylist(
								songlistitem, l2);
					mPlayerController.open2(j1);
				} else if (songlistitem.mMusicType == MusicType.LOCALMUSIC
						.ordinal())
				{
					Song song1 = Util.makeSong2(songlistitem);
					song1.mSize = 1L;
					int j = mPlayerController.add2NowPlayingList(song1);
					mPlayerController.open(j);
				}
			}
			break;
		default:
			break;
		}
		logger.v("handleMMHttpEvent ----> Exit");
	}

	public void handlePlayerEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			return;
		case 1014:
		case 1002:
		case 1010:
		case 1011:
			Object obj;
			if (((Activity) mContext).getParent() == null)
				obj = mContext;
			else
				obj = ((Activity) mContext).getParent();
			// Uiutil.ifSwitchToWapDialog(((Context) (obj)));
			break;
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

	/**
	 * 初始化基本的界面
	 */
	public void inital()
	{
		mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		mHttpController = this.mController.getHttpController();
		mPlayerController = this.mController.getPlayerController();
		mLayoutInflater = LayoutInflater.from(this.mContext);
		removeAllViews();
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
				-1, -2);
		RelativeLayout localRelativeLayout = (RelativeLayout) this.mLayoutInflater
				.inflate(R.layout.song_list_view, null);
		addView(localRelativeLayout, localLayoutParams);
		mDispatcher = MobileMusicApplication.getInstance().getEventDispatcher();
		mPlayerStatusBar = ((PlayerStatusBar) localRelativeLayout
				.findViewById(R.id.playerStatusBar));
		mNothingView = ((ImageView) findViewById(R.id.nothing));
		mRetryLayout = ((LinearLayout) findViewById(R.id.refresh_layout));
		// 全部播放
		mBtnPlayAll = ((Button) findViewById(R.id.btn_all_play));
		mBtnPlayAll.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				if (mSongListData.size() != 0)
				{
					mCurrentDialog = DialogUtil
							.showIndeterminateProgressDialog(mContext,
									R.string.local_music_playallsong);
					(new PlayAllTask()).execute(new String[0]);
				}
			}
		});
		// 批量下载
		this.mBtnBatchDownload = ((Button) findViewById(R.id.btn_batch_download));
		this.mBtnBatchDownload.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				// if (mSongListData != null && mSongListData.size() != 0
				// && !gotoLogin())
				// {
				// Intent intent = new Intent(mContext,
				// MusicOnlineBatchDownloadActivity.class);
				// intent.putParcelableArrayListExtra("DOWNLOADDATA",
				// (ArrayList) mSongListData);
				// mContext.startActivity(intent);
				// }
			}
		});
		this.mOperationButtonLayout = ((LinearLayout) findViewById(R.id.operation_button_layout));
		this.mSongListView = ((ListView) findViewById(R.id.songlistview));
		this.mLoadMoreView = this.mLayoutInflater.inflate(
				R.layout.load_more_list_footer_view, null);
		this.mLoadMoreView.setOnClickListener(this.mLoadMoreOnClickListener);
		this.mReqMoreProgress = ((ProgressBar) this.mLoadMoreView
				.findViewById(R.id.progressbar));
	}

	public void isShowOperationButtonLayout(boolean flag)
	{
		if (!flag)
			mOperationButtonLayout.setVisibility(8);
		else
			mOperationButtonLayout.setVisibility(0);
	}

	public void notifyDataChange()
	{
		if (this.mMobileMusicRecommendListItemAdapter != null)
			this.mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
	}

	public void releaseResource()
	{
		this.mLayoutInflater = null;
		this.mSongListView = null;
		if (this.mSongListData != null)
			this.mSongListData.clear();
		this.mSongListData = null;
		this.mOperationButtonLayout = null;
		this.mLoadMoreView = null;
		this.mReqMoreProgress = null;
		this.mNothingView = null;
		if (this.mPlayerStatusBar != null)
			this.mPlayerStatusBar.destroyDrawingCache();
		this.mPlayerStatusBar = null;
		if (this.mMobileMusicRecommendListItemAdapter != null)
			this.mMobileMusicRecommendListItemAdapter.releaseAdapterResource();
		this.mMobileMusicRecommendListItemAdapter = null;
		this.mScrollListener = null;
	}

	public void removeListner()
	{
		if (this.mCurrentTask != null)
		{
			this.mHttpController.cancelTask(this.mCurrentTask);
			this.mCurrentTask = null;
		}
		if (this.mCurrentDialog != null)
		{
			this.mCurrentDialog.dismiss();
			this.mCurrentDialog = null;
		}
		this.mPlayerStatusBar.unRegistEventListener();
		this.mController.removeHttpEventListener(3003, this);
		this.mController.removeHttpEventListener(3005, this);
		this.mController.removeHttpEventListener(3004, this);
		this.mController.removeHttpEventListener(3006, this);
		this.mController.removeHttpEventListener(3007, this);
		this.mController.removeHttpEventListener(3008, this);
		this.mController.removePlayerEventListener(1002, this);
		this.mController.removePlayerEventListener(1010, this);
		this.mController.removePlayerEventListener(1012, this);
		this.mController.removePlayerEventListener(1011, this);
		this.mController.removePlayerEventListener(1014, this);
		this.mController.removeUIEventListener(4008, this);
	}

	/**
	 * 底部两个按钮的设置---全部播放---批量下载
	 * 
	 * @param paramInt
	 */
	public void setButtonShow(int paramInt)
	{
		switch (paramInt)
		{
		default:
			return;
		case 0:
			mBtnBatchDownload.setVisibility(8);
			mBtnPlayAll.setVisibility(0);
			mBtnPlayAll
					.setBackgroundResource(R.drawable.btn_long_batch_download_selector);
			break;
		case 1:
			mBtnBatchDownload.setVisibility(0);
			mBtnBatchDownload
					.setBackgroundResource(R.drawable.btn_long_all_play_selector);
			mBtnPlayAll.setVisibility(8);
			break;
		}
	}

	public void setIconImage(int paramInt)
	{
		this.mIconrid = paramInt;
	}

	public void setListViewOnCreateContextMenuListener(
			View.OnCreateContextMenuListener paramOnCreateContextMenuListener)
	{
		this.mSongListView
				.setOnCreateContextMenuListener(paramOnCreateContextMenuListener);
	}

	public void setRatingVisaible(boolean paramBoolean)
	{
		this.mIsRatingVisable = paramBoolean;
	}

	public void setURL(String paramString)
	{
		this.mURL = paramString;
	}

	public void setName(String name)
	{
		this.mName = name;
	}

	public String getName(String name)
	{
		return this.mName;
	}

	public void setplayerStatusBarGone()
	{
		this.mPlayerStatusBar.setVisibility(8);
	}

	class PlayAllTask extends AsyncTask<String, Void, Integer>
	{
		PlayAllTask()
		{
		}

		public Integer doInBackground(String[] paramArrayOfString)
		{
			return Integer.valueOf(SongListView.this.playAll());
		}

		public void onPostExecute(Integer paramInteger)
		{
			if (SongListView.this.mCurrentDialog != null)
			{
				SongListView.this.mCurrentDialog.dismiss();
				SongListView.this.mCurrentDialog = null;
			}
			SongListView.this.notifyDataChange();
			super.onPostExecute(paramInteger);
		}

		public void onPreExecute()
		{
			super.onPreExecute();
		}
	}

	/**
	 * 获取榜单对应的歌曲列表信息
	 * 
	 * @param chartCode
	 * @return
	 */
	private ArrayList<SongMusicInfo> getSongListData(String chartCode)
	{
		logger.v("getSongListData() ----> Enter");
		logger.v("chartCode ============================== " + chartCode);
		ArrayList<SongMusicInfo> tempMusicInfos = songOnlineManager
				.getMusicInfoByCode(mContext, chartCode, 1,
						SongOnlineManager.SONG_PAGE_SIZE, this.mName);
		logger.v("getSongListData() ----> Exit");
		return tempMusicInfos;
	}

	private void refreshSongListData()
	{
		logger.v("refreshSongListData() ----> Enter");
		ArrayList<SongMusicInfo> tempSongMusicInfo = this.songMusicInfos;
		List<SongListItem> songListItems = new ArrayList<SongListItem>();
		for (int i = 0; i < tempSongMusicInfo.size(); i++)
		{
			SongMusicInfo song = tempSongMusicInfo.get(i);
			SongListItem songListItem = new SongListItem();
			songListItem.musicid = song.getMusicId();
			songListItem.price = song.getRingSongPrice();
			songListItem.count = song.getCount();
			songListItem.crbtValidity = song.getCrbtValidity();
			songListItem.singerName = song.getSingerName();
			songListItem.singerId = song.getSingerId();
			songListItem.songName = song.getSongName();
			songListItem.title = song.getSongName();
			songListItem.contentid = song.getMusicId();
			songListItem.singer = song.getSingerName();
			songListItem.isdolby = "0";
			songListItem.point = "0";
			// songListItem.url = song.getSongAuditionUrl();
			// Log.d("songsong", "songListItem.url" +
			// song.getSongAuditionUrl());
			songListItems.add(songListItem);
		}
		addSongList(songListItems);
		logger.v("refreshSongListData() ----> Exit");
	}

	public void addSongList(List<SongListItem> paramList)
	{
		logger.v("addSongList(List<SongListItem) ----> Exit");
		if ((paramList != null) && (paramList.size() > 0)
				&& (this.mNothingView != null))
			this.mNothingView.setVisibility(8);
		if (mSongListData != null && mSongListData.size() > 0)
		{
			clearData();
		}
		this.mSongListData.addAll(paramList);
		if ((this.mTargetPageNo == this.mPageTotalCount)
				&& (this.mSongListView.getFooterViewsCount() > 0))
			this.mSongListView.removeFooterView(this.mLoadMoreView);
		int i = this.mSongListView.getFirstVisiblePosition();
		if (this.mMobileMusicRecommendListItemAdapter == null)
		{
			this.mMobileMusicRecommendListItemAdapter = new MobileMusicRecommendListItemAdapter(
					this.mContext, this.mSongListData);
			this.mMobileMusicRecommendListItemAdapter.setIcon(this.mIconrid);

			// 这个是点击每首歌曲右边小按钮后弹出小的列表框
			mListButtonClickListener = new ListButtonClickListener(
					this.mContext, this.mSongListData);
			this.mMobileMusicRecommendListItemAdapter
					.setBtnOnClickListener(mListButtonClickListener);
			this.mSongListView
					.setAdapter(this.mMobileMusicRecommendListItemAdapter);
			this.mSongListView
					.setOnItemClickListener(this.mSongListItemOnItemClickListener);
			this.mSongListView.setOnScrollListener(this.mScrollListener);
			this.mSongListView.setFadingEdgeLength(0);
			this.mMobileMusicRecommendListItemAdapter
					.setRatingVisaible(this.mIsRatingVisable);
		}
		this.mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
		if (i > 0)
			this.mSongListView.setSelectionFromTop(i + 1, 0);
		logger.v("addSongList(List<SongListItem) ----> Exit");
	}

	@Override
	public void getDataFromURL(int url)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * 新建一个线程更新数据
	 */
	private void startGetSongListDataThread()
	{
		logger.v("startThread() ----> Enter");
		thread = new Thread()
		{
			public void run()
			{
				getTempData();
				if (songMusicInfos != null)
				{
					logger.v("getSongListData Success!!!!");
					mDispatcher
							.sendMessage(mDispatcher
									.obtainMessage(DispatcherEventEnum.Http_UpdateData_Sucess));
				}
			}
		};
		thread.start();
	}

	private void getTempData()
	{
		logger.v("getTempData() ----> Enter");
		if (songMusicInfos != null && songMusicInfos.size() > 0)
		{
			songMusicInfos = null;
		}
		songMusicInfos = getSongListData(this.mURL);
		logger.v("getTempData() ----> Exit");
	}

	/**
	 * 新建一个线程开始获取在线听歌URL
	 */
	private void startGetSongUrlThread(final int i)
	{
		logger.v("startGetSongUrlThread() ----> Enter");
		new Thread()
		{
			public void run()
			{
				getSongUrl(i);
				mDispatcher
						.sendMessage(mDispatcher
								.obtainMessage(DispatcherEventEnum.Http_GetSongUrl_Sucess));
			}
		}.start();
	}

	/**
	 * 获取歌曲在线试听地址
	 */
	private void getSongUrl(int i)
	{
		logger.v("getSongUrl() ----> Enter");
		try
		{
			if (songMusicInfos != null && mSongListData != null)
			{
				SongMusicInfo songMusicInfo = songMusicInfos.get(i);
				CodeMessageObject cmo = songOnlineManager
						.getOnLineListenerSongUrl(mContext,
								songMusicInfo.getMusicId());
				if ("000000".equals(cmo.getCode()))
				{
					String url = (String) cmo.getObject();
					mSongListData.get(i).url = url;
					songMusicInfos.get(i).setSongAuditionUrl(url);
				} else
				{
					Log.d("song", "cmo.getCode() " + cmo.getCode());
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			logger.e("Get the song Url not success....");
		}
	}
}