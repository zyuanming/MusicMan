package org.ming.ui.activity.online;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.business.MusicBusinessDefine_Net;
import org.ming.center.business.MusicBusinessDefine_WAP;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.http.item.SongListItem;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.ListButtonClickListener;
import org.ming.center.ui.UIEventListener;
import org.ming.ui.activity.MobileMusicMainActivity;
import org.ming.ui.adapter.MobileMusicAlbumDetailListItemAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.view.TitleBarView;
import org.ming.ui.widget.PlayerStatusBar;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;
import org.ming.util.XMLParser;

import com.umeng.analytics.MobclickAgent;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MusicOnlineMusicAlbumDetailActivity extends ListActivity implements
		MMHttpEventListener, PlayerEventListener, UIEventListener,
		SystemEventListener
{
	private static final MyLogger logger = MyLogger
			.getLogger("MusicOnlineMusicAlbumDetailActivity");
	private boolean DownloadLoginFlag;
	private boolean LOAD_MORE = true;
	private List<SongListItem> mAlbumSongListData = new ArrayList();
	private Button mBtnBatchDownload = null;
	private Button mBtnPlayAll = null;
	private Controller mController;
	private Dialog mCurrentDialog;
	private int mCurrentPageNo = 1;
	private MMHttpTask mCurrentTask;
	private HttpController mHttpController;
	private boolean mIsFromPushService = false;
	private boolean mIsInital = false;
	private ListButtonClickListener mListButtonClickListener = null;
	private View.OnClickListener mLoadMoreOnClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			logger.v("mLoadMoreOnClickListener() ---> Enter");
			mReqMoreProgress.setVisibility(0);
			mTargetPageNo = 1 + mCurrentPageNo;
			if (mTargetPageNo > 1 && mTargetPageNo <= mPageTotalCount)
				requestAlbumDetail();
			else
				getListView().removeFooterView(mLoadMoreView);
			MusicOnlineMusicAlbumDetailActivity.logger
					.v("mLoadMoreOnClickListener() ---> Exit");
		}
	};
	private View mLoadMoreView;
	private MobileMusicAlbumDetailListItemAdapter mMobileMusicAlbumDetailListItemAdapter;
	private ImageView mNothingView;
	private int mPageTotalCount = -1;
	private PlayerController mPlayerController = null;
	private PlayerStatusBar mPlayerStatusBar = null;
	private ProgressBar mReqMoreProgress;
	private String mRequestUrl;
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
			if (LOAD_MORE)
			{
				this.firstItem = paramAnonymousInt1;
				this.visibleItem = paramAnonymousInt2;
				this.totalItem = paramAnonymousInt3;
			}
		}

		public void onScrollStateChanged(AbsListView paramAnonymousAbsListView,
				int paramAnonymousInt)
		{
			if ((MusicOnlineMusicAlbumDetailActivity.this.LOAD_MORE)
					&& (paramAnonymousInt == 0)
					&& (this.firstItem + this.visibleItem == this.totalItem)
					&& (mLoadMoreView.getVisibility() == 0))
			{
				mLoadMoreOnClickListener.onClick(mLoadMoreView);
				LOAD_MORE = false;
			}
		}
	};
	private int mTargetPageNo = 1;
	private TitleBarView mTitleBar;

	private void doUnCompleteTask()
	{
		// logger.v("doUnCompleteTask() ---> Enter");
		// if (GlobalSettingParameter.useraccount != null)
		// {
		// if (DownloadLoginFlag)
		// {
		// DownloadLoginFlag = false;
		// Intent intent = new Intent(this,
		// MusicOnlineBatchDownloadActivity.class);
		// intent.putParcelableArrayListExtra("DOWNLOADDATA",
		// (ArrayList) mAlbumSongListData);
		// startActivity(intent);
		// }
		// logger.v("doUnCompleteTask() ---> Exit");
		// }
	}

	private boolean gotoLogin()
	{
		boolean flag = true;
		logger.v("gotoLogin() ---> Enter");
		if (GlobalSettingParameter.useraccount == null)
		{
			DownloadLoginFlag = flag;
			Uiutil.login(this, 0);
		} else
		{
			logger.v("gotoLogin() ---> Exit");
			flag = false;
		}
		return flag;
	}

	private void onHttpRequestCanceled(MMHttpTask paramMMHttpTask)
	{}

	private void onHttpResponse(MMHttpTask paramMMHttpTask)
	{
		logger.v("onHttpResponse() ---> Enter");
		int i = paramMMHttpTask.getRequest().getReqType();
		XMLParser xmlparser = new XMLParser(paramMMHttpTask.getResponseBody());
		if ((xmlparser.getRoot() == null)
				|| (xmlparser.getValueByTag("code") == null))
		{
			if (this.mReqMoreProgress != null)
				this.mReqMoreProgress.setVisibility(8);
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
			this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
					getText(R.string.title_information_common),
					getText(R.string.fail_to_parse_xml_common),
					new View.OnClickListener()
					{
						public void onClick(View paramAnonymousView)
						{
							if (mCurrentDialog != null)
							{
								mCurrentDialog.dismiss();
								mCurrentDialog = null;
							}
						}
					});
		} else
		{
			if (!xmlparser.getValueByTag("code").equals("000000"))
			{
				if (mReqMoreProgress != null)
					mReqMoreProgress.setVisibility(8);
				mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
						getText(R.string.title_information_common),
						xmlparser.getValueByTag("info"),
						new android.view.View.OnClickListener()
						{
							public void onClick(View view)
							{
								if (mCurrentDialog != null)
								{
									mCurrentDialog.dismiss();
									mCurrentDialog = null;
								}
							}
						});
			}
		}

		switch (i)
		{
		default:
			logger.v("onHttpResponse() ---> Exit");
			break;
		case 1017:
		case 5021:
			List list;
			if (mPageTotalCount == -1)
				mPageTotalCount = Integer.parseInt(xmlparser
						.getValueByTag("pagecount"));
			mCurrentPageNo = mTargetPageNo;
			if (mCurrentPageNo != mPageTotalCount
					&& getListView().getFooterViewsCount() == 0)
				getListView().addFooterView(mLoadMoreView);
			mReqMoreProgress.setVisibility(8);
			list = xmlparser.getListByTagAndAttribute("item",
					SongListItem.class);
			if (list != null)
			{
				String s;
				Iterator iterator;
				s = xmlparser.getValueByTag("groupcode");
				iterator = list.iterator();
			} else
			{
				mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
						getText(R.string.fail_to_parse_xml_common),
						getText(R.string.server_data_empty_common),
						new View.OnClickListener()
						{
							public void onClick(View view)
							{
								if (mCurrentDialog != null)
								{
									mCurrentDialog.dismiss();
									mCurrentDialog = null;
								}
							}
						});
			}
			break;
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
						if (mCurrentDialog != null)
						{
							mCurrentDialog.dismiss();
							mCurrentDialog = null;
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
						mCurrentDialog.dismiss();
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	private int playAll()
	{
		logger.v("playAll() ---> Enter");
		ArrayList localArrayList = new ArrayList();
		int i = this.mAlbumSongListData.size();
		if (mAlbumSongListData != null && i > 0)
		{
			int j = 0;
			while (true)
			{
				if (j >= i)
				{
					this.mPlayerController.add2NowPlayingList(localArrayList);
					this.mPlayerController
							.open(this.mPlayerController.checkSongInNowPlayingList(Util
									.makeSong((SongListItem) this.mAlbumSongListData
											.get(0))));
					logger.v("playAll() ---> Exit");
					return i;
				}
				try
				{
					SongListItem localSongListItem = (SongListItem) this.mAlbumSongListData
							.get(j);
					if (localSongListItem != null)
						localArrayList.add(Util.makeSong(localSongListItem));
					long l = this.mPlayerController
							.addCurrentTrack2OnlineMusicTable(localSongListItem);
					if (l != -1L)
						this.mPlayerController.addCurrentTrack2RecentPlaylist(
								localSongListItem, l);
					j++;
				} catch (Exception localException)
				{
					localException.printStackTrace();
				}
			}
		} else
		{
			return -1;
		}
	}

	private void refreshUI(List list)
	{
		logger.v("refreshUI(List<SongListItem>) ---> Enter");
		if (mPageTotalCount == 1)
			getListView().removeFooterView(mLoadMoreView);
		mNothingView.setVisibility(8);
		mAlbumSongListData.addAll(list);
		int i = getListView().getFirstVisiblePosition();
		mMobileMusicAlbumDetailListItemAdapter = new MobileMusicAlbumDetailListItemAdapter(
				this, mAlbumSongListData);
		if (mListButtonClickListener != null)
			mListButtonClickListener.setListData(mAlbumSongListData);
		else
			mListButtonClickListener = new ListButtonClickListener(this,
					mAlbumSongListData);
		mMobileMusicAlbumDetailListItemAdapter
				.setBtnOnClickListener(mListButtonClickListener);
		mMobileMusicAlbumDetailListItemAdapter.setCacheImgData(true);
		getListView().setAdapter(mMobileMusicAlbumDetailListItemAdapter);
		getListView().setOnScrollListener(mScrollListener);
		mMobileMusicAlbumDetailListItemAdapter.notifyDataSetChanged();
		if (MobileMusicApplication.getShowMusicSelectedToast())
		{
			if (i > 0)
				getListView().setSelectionFromTop(i + 1, 0);
			logger.v("refreshUI(List<SongListItem>) ---> Exit");
			return;
		} else
		{
			MobileMusicApplication.setShowMusicSelectedToast(true);
			if (!NetUtil.isNetStateWLAN())
			{
				if (NetUtil.isConnection())
					Toast.makeText(this, getText(R.string.music_select_low), 1)
							.show();
			} else
			{
				Toast.makeText(this, getText(R.string.music_select_high), 1)
						.show();
			}
		}
	}

	private void requestAlbumDetail()
	{
		logger.v("requestAlbumDetail() ---> Enter");
		if (mRequestUrl != null && !"".equals(mRequestUrl.trim()))
		{
			CancelPreviousReq();
			if (mPageTotalCount == -1)
				showInitingDialog(R.string.loading);
			char c;
			MMHttpRequest mmhttprequest;
			int i;
			if (NetUtil.isNetStateWap())
				c = '\u03F9';
			else
				c = '\u139D';
			mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
			mmhttprequest.addUrlParams("itemcount",
					GlobalSettingParameter.SERVER_INIT_PARAM_ITEM_COUNT);
			mmhttprequest.addUrlParams("pageno", String.valueOf(mTargetPageNo));
			i = mRequestUrl.indexOf("rdp2");
			if (i >= 0 && i < mRequestUrl.length())
				mRequestUrl = mRequestUrl.substring(i);
			if (NetUtil.isNetStateWap())
				mRequestUrl = (new StringBuilder(
						String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_IP)))
						.append(mRequestUrl).toString();
			else
				mRequestUrl = (new StringBuilder(
						String.valueOf(MusicBusinessDefine_Net.NET_HOST_IP)))
						.append(mRequestUrl).toString();
			mmhttprequest.setURL(mRequestUrl);
			mCurrentTask = mHttpController.sendRequest(mmhttprequest);
			logger.v("requestAlbumDetail() ---> Exit");
		}
	}

	private void showInitingDialog(int paramInt)
	{
		logger.v("showInitingDialog() ---> Enter");
		if (this.mCurrentDialog != null)
		{
			this.mCurrentDialog.dismiss();
			this.mCurrentDialog = null;
		}
		this.mRetryLayout.setVisibility(8);
		this.mNothingView.setVisibility(0);
		this.mCurrentDialog = DialogUtil.show1BtnProgressDialog(this, paramInt,
				R.string.cancel, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						mCurrentTask = new MMHttpTask(null);
						mCurrentTask.setIsCancled(true);
						if (mCurrentDialog != null)
						{
							mCurrentDialog.dismiss();
							mCurrentDialog = null;
							mCurrentTask = null;
							mRetryLayout.setVisibility(0);
							mNothingView.setVisibility(8);
							((Button) mRetryLayout
									.findViewById(R.id.refresh_btn))
									.setOnClickListener(new View.OnClickListener()
									{
										public void onClick(
												View paramAnonymous2View)
										{
											mRetryLayout.setVisibility(8);
											requestAlbumDetail();
										}
									});
						}
					}
				});
		logger.v("showInitingDialog() ---> Exit");
	}

	public void CancelPreviousReq()
	{
		logger.v("CancelPreviousReq() ---> Enter");
		if (this.mCurrentTask != null)
		{
			this.mHttpController.cancelTask(this.mCurrentTask);
			this.mCurrentTask = null;
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
		}
		logger.v("CancelPreviousReq() ---> Exit");
	}

	public boolean dispatchTouchEvent(MotionEvent motionevent)
	{
		boolean flag = false;
		switch (motionevent.getAction())
		{
		default:
			flag = super.dispatchTouchEvent(motionevent);
			break;
		case 0:
			if (mListButtonClickListener != null
					&& mListButtonClickListener.closePopupWindow())
				flag = true;
			break;
		}
		return flag;
	}

	public void handleMMHttpEvent(Message message)
	{
		logger.v("handleMMHttpEvent() ---> Enter");
		MMHttpTask mmhttptask = (MMHttpTask) message.obj;
		if ((mmhttptask == null) || (this.mCurrentTask == null)
				|| (mmhttptask.getTransId() != this.mCurrentTask.getTransId()))
		{
			logger.v("Thus http message is not for this activity");
			return;
		} else
		{
			if ((this.mRetryLayout != null)
					&& (this.mRetryLayout.getVisibility() != 8))
				this.mRetryLayout.setVisibility(8);
			if (this.mCurrentTask != null)
				this.mCurrentTask = null;
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
			int i = mmhttptask.getRequest().getReqType();
			switch (message.what)
			{
			default:
				Uiutil.ifSwitchToWapDialog(this);
				LOAD_MORE = true;
				break;
			case 3003:
			case 3005:
				onHttpRequestCanceled(mmhttptask);
				break;
			case 3004:
				onSendHttpRequestFail(mmhttptask);
				break;
			case 3006:
				onSendHttpRequestTimeOut(mmhttptask);
				break;
			case 3007:
			case 3008:
				onHttpResponse(mmhttptask);
				break;
			}
			if ((i == 1017 || i == 5021)
					&& (message.what == 3004 || message.what == 3006))
			{
				mRetryLayout.setVisibility(0);
				((Button) mRetryLayout.findViewById(R.id.refresh_btn))
						.setOnClickListener(new View.OnClickListener()
						{
							public void onClick(View view)
							{
								mRetryLayout.setVisibility(8);
								requestAlbumDetail();
							}
						});
			}
			logger.v("handleMMHttpEvent() ---> Exit");
		}
	}

	public void handlePlayerEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			break;
		case 1014:
			Uiutil.ifSwitchToWapDialog(this);
			break;
		case 1002:
		case 1010:
		case 1011:
		case 1012:
			notifyDataChange();
			break;
		}
	}

	public void handleSystemEvent(Message paramMessage)
	{
		logger.v("handleSystemEvent() ---> Enter");
		switch (paramMessage.what)
		{
		default:
			logger.v("handleSystemEvent() ---> Exit");
			return;
		case 22:
			finish();
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

	public void notifyDataChange()
	{
		if (this.mMobileMusicAlbumDetailListItemAdapter != null)
			this.mMobileMusicAlbumDetailListItemAdapter.notifyDataSetChanged();
	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_online_music_album_detail_layout);
		this.mController = Controller
				.getInstance((MobileMusicApplication) getApplication());
		this.mHttpController = this.mController.getHttpController();
		this.mPlayerController = this.mController.getPlayerController();
		this.mRetryLayout = ((LinearLayout) findViewById(R.id.refresh_layout));
		this.mNothingView = ((ImageView) findViewById(R.id.album_empty));
		this.mTitleBar = ((TitleBarView) findViewById(R.id.title_view));
		this.mPlayerStatusBar = ((PlayerStatusBar) findViewById(R.id.playerStatusBar));
		Intent localIntent = getIntent();
		if (localIntent.getExtras() != null)
		{
			this.mTitleBar.setTitle(localIntent.getStringExtra("title"));
			this.mIsFromPushService = localIntent.getBooleanExtra(
					"fromPushService", false);
		}
		this.mRequestUrl = localIntent.getStringExtra("album_song_url");
		this.mTitleBar.setCurrentActivity(this);
		this.mTitleBar.setButtons(1);
		this.mBtnPlayAll = ((Button) findViewById(R.id.btn_all_play));
		this.mBtnPlayAll.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				if (mAlbumSongListData != null
						&& mAlbumSongListData.size() != 0)
				{
					mCurrentDialog = DialogUtil
							.showIndeterminateProgressDialog(
									MusicOnlineMusicAlbumDetailActivity.this,
									R.string.local_music_playallsong);
					(new PlayAllTask()).execute(new String[0]);
				}
			}
		});
		this.mBtnBatchDownload = ((Button) findViewById(R.id.btn_batch_download));
		this.mBtnBatchDownload.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				// if (mAlbumSongListData != null
				// && mAlbumSongListData.size() != 0 && !gotoLogin())
				// {
				// Intent intent1 = new Intent(
				// MusicOnlineMusicAlbumDetailActivity.this,
				// MusicOnlineBatchDownloadActivity.class);
				// intent1.putParcelableArrayListExtra("DOWNLOADDATA",
				// (ArrayList) mAlbumSongListData);
				// startActivity(intent1);
				// }
			}
		});
		this.mLoadMoreView = getLayoutInflater().inflate(
				R.layout.load_more_list_footer_view, null);
		this.mReqMoreProgress = ((ProgressBar) this.mLoadMoreView
				.findViewById(R.id.progressbar));
		this.mLoadMoreView.setOnClickListener(this.mLoadMoreOnClickListener);
		this.mController.addSystemEventListener(22, this);
		logger.v("onCreate() ---> Exit");
	}

	protected void onDestroy()
	{
		this.mController.removeSystemEventListener(22, this);
		this.mLoadMoreView = null;
		if (this.mAlbumSongListData != null)
			this.mAlbumSongListData.clear();
		this.mAlbumSongListData = null;
		if (this.mMobileMusicAlbumDetailListItemAdapter != null)
		{
			this.mMobileMusicAlbumDetailListItemAdapter
					.releaseAdapterResource();
			this.mMobileMusicAlbumDetailListItemAdapter = null;
		}
		this.mRetryLayout = null;
		this.mPlayerStatusBar = null;
		super.onDestroy();
	}

	public boolean onKeyDown(int i, KeyEvent keyevent)
	{
		boolean flag = true;
		if (i == 4)
		{
			if ((this.mListButtonClickListener != null)
					&& (this.mListButtonClickListener.closePopupWindow()))
				return flag;
			else
			{
				if (mIsFromPushService)
				{
					Intent intent = new Intent(this,
							MobileMusicMainActivity.class);
					intent.putExtra("startFromNotification", flag);
					startActivity(intent);
					finish();
				} else
				{
					finish();
				}
			}
		}
		flag = super.onKeyDown(i, keyevent);
		return flag;
	}

	protected void onListItemClick(ListView listview, View view, int i, long l)
	{
		logger.v("onListItemClick() ---> Enter");
		Song song = this.mPlayerController.getCurrentPlayingItem();
		if (i < mAlbumSongListData.size() && i >= 0)
		{
			SongListItem localSongListItem = (SongListItem) this.mAlbumSongListData
					.get(i);
			if ((song != null)
					&& (song.mMusicType != MusicType.LOCALMUSIC.ordinal())
					&& (song.mMusicType != MusicType.RADIO.ordinal())
					&& (song.mContentId == localSongListItem.contentid))
				this.mPlayerController.pause();
			long l1 = this.mPlayerController
					.addCurrentTrack2OnlineMusicTable(localSongListItem);
			if (l1 != -1L)
				this.mPlayerController.addCurrentTrack2RecentPlaylist(
						localSongListItem, l1);
			Song localSong2 = Util.makeSong(localSongListItem);
			localSong2.mId = l1;
			int j = this.mPlayerController.add2NowPlayingList(localSong2);
			this.mPlayerController.open(j);
			this.mMobileMusicAlbumDetailListItemAdapter.notifyDataSetChanged();
			logger.v("onListItemClick() ---> Exit");
		}
	}

	protected void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		MobclickAgent.onPause(this);
		CancelPreviousReq();
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
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		MobclickAgent.onResume(this);
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
		doUnCompleteTask();
		if ((this.mCurrentTask == null) && (!this.mIsInital))
			requestAlbumDetail();
		this.mTitleBar.setLeftBtnOnlickListner(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (mIsFromPushService)
				{
					Intent intent = new Intent(
							MusicOnlineMusicAlbumDetailActivity.this,
							MobileMusicMainActivity.class);
					intent.putExtra("startFromNotification", true);
					startActivity(intent);
					finish();
				} else
				{
					finish();
				}
			}
		});
		notifyDataChange();
		if (this.mListButtonClickListener != null)
			this.mListButtonClickListener.doUnCompleteTask();
		logger.v("onResume() ---> Exit");
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
			MusicOnlineMusicAlbumDetailActivity.logger
					.v("onPostExecute(Integer) ---> Enter");
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			if (mMobileMusicAlbumDetailListItemAdapter != null)
				mMobileMusicAlbumDetailListItemAdapter.notifyDataSetChanged();
			super.onPostExecute(paramInteger);
			MusicOnlineMusicAlbumDetailActivity.logger
					.v("onPostExecute(Integer) ---> Exit");
		}

		public void onPreExecute()
		{
			super.onPreExecute();
		}
	}
}