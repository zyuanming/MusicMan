package org.ming.ui.activity.online;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.download.UrlImageDownloader;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.http.item.ContentItem;
import org.ming.center.http.item.SongListItem;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.ListButtonClickListener;
import org.ming.center.ui.UIEventListener;
import org.ming.dispatcher.DispatcherEventEnum;
import org.ming.ui.adapter.MobileMusicRecommendListItemAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.view.TitleBarView;
import org.ming.ui.widget.PlayerStatusBar;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;
import org.ming.util.XMLParser;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MusicOnlineInformationDetailActivity extends Activity implements
		MMHttpEventListener, SystemEventListener, PlayerEventListener,
		UIEventListener
{
	class mClickSpan extends ClickableSpan implements
			android.view.View.OnClickListener
	{
		String tag;

		public void onClick(View view)
		{
			if (!Uiutil.isNetChange())
			{
				// Intent intent = new Intent(
				// MusicOnlineInformationDetailActivity.this,
				// MusicOnlineInformationDetailByUrlActivity.class);
				// intent.putExtra(
				// MusicOnlineInformationDetailByUrlActivity.INTENT_DATA_URL,
				// tag);
				// intent.putExtra(
				// MusicOnlineInformationDetailByUrlActivity.INTENT_DATA_TITLE,
				// mInfoTitle.getText());
				// startActivity(intent);
			} else
			{
				Uiutil.ifSwitchToWapDialog(MusicOnlineInformationDetailActivity.this);
			}
		}

		public mClickSpan(String s)
		{
			tag = s;
		}
	}

	private static final MyLogger logger = MyLogger
			.getLogger("MusicOnlineInformationDetailActivity");
	private android.view.View.OnClickListener mChangeItemListener;
	private Controller mController;
	private Dialog mCurrentDialog;
	private int mCurrentPageNo;
	private int mCurrentPostion;
	private MMHttpTask mCurrentTask;
	private View mHeadView;
	private HttpController mHttpController;
	private UrlImageDownloader mImageDownloader;
	private TextView mInfoDetail;
	private ImageView mInfoPic;
	private String mInfoPicUrl;
	private TextView mInfoTime;
	private TextView mInfoTitle;
	private String mKeyBitmap;
	private ListButtonClickListener mListButtonClickListener;
	private List mNewsList;
	private ImageView mNextItem;
	private android.widget.AdapterView.OnItemClickListener mOnItemClickListener;
	private PlayerController mPlayerController;
	private PlayerStatusBar mPlayerStatusBar;
	private ImageView mPreviousItem;
	private LinearLayout mRetryLayout;
	private String mSelfContent;
	private String mSelfUrl;
	private List mSongItemDataOfInfo;
	private MobileMusicRecommendListItemAdapter mSongListItemOfInfoAdapter;
	private ListView mSongListViewOfInformation;
	private TitleBarView mTitleBar;

	public MusicOnlineInformationDetailActivity()
	{
		mController = null;
		mHttpController = null;
		mCurrentTask = null;
		mSelfContent = null;
		mCurrentDialog = null;
		mSongItemDataOfInfo = new ArrayList();
		mPlayerController = null;
		mPlayerStatusBar = null;
		mSelfUrl = null;
		mNewsList = null;
		mCurrentPageNo = 1;
		mCurrentPostion = 0;
		mListButtonClickListener = null;
		mOnItemClickListener = new android.widget.AdapterView.OnItemClickListener()
		{

			public void onItemClick(AdapterView adapterview, View view, int i,
					long l)
			{
				MusicOnlineInformationDetailActivity.logger
						.v("onItemClick() ---> Enter");
				if (NetUtil.isConnection())
				{
					SongListItem songlistitem;
					int j = i
							- mSongListViewOfInformation.getHeaderViewsCount();
					if (j > 0 && j < mSongItemDataOfInfo.size())
					{
						Song song = mPlayerController.getCurrentPlayingItem();
						songlistitem = (SongListItem) mSongItemDataOfInfo
								.get(j);
						if ((song != null)
								&& (song.mMusicType != MusicType.LOCALMUSIC
										.ordinal())
								&& (song.mMusicType != MusicType.RADIO
										.ordinal())
								&& (song.mContentId == songlistitem.contentid))
							mPlayerController.pause();
						long l1 = mPlayerController
								.addCurrentTrack2OnlineMusicTable(songlistitem);
						if (l1 != -1L)
							mPlayerController.addCurrentTrack2RecentPlaylist(
									songlistitem, l1);
						Song song1 = Util.makeSong(songlistitem);
						song1.mId = l1;
						int k = mPlayerController.add2NowPlayingList(song1);
						mPlayerController.open(k);
						logger.v("onItemClick() ---> Exit");
					}
				} else
				{
					Uiutil.ifSwitchToWapDialog(MusicOnlineInformationDetailActivity.this);
				}
			}
		};
		mChangeItemListener = new android.view.View.OnClickListener()
		{

			public void onClick(View view)
			{
				logger.v("onClick() ---> Enter");
				switch (view.getId())
				{
				case R.id.info_pic:
				default:
					MusicOnlineInformationDetailActivity.logger
							.v("onClick() ---> Eixt");
					return;
				case R.id.next_item:
					mNextItem.setClickable(false);
					MusicOnlineInformationDetailActivity musiconlineinformationdetailactivity1 = MusicOnlineInformationDetailActivity.this;
					musiconlineinformationdetailactivity1.mCurrentPostion = 1 + musiconlineinformationdetailactivity1.mCurrentPostion;
					requestNewsInfo();
					if (mCurrentDialog != null)
					{
						mCurrentDialog.dismiss();
						mCurrentDialog = null;
					}
					mCurrentDialog = DialogUtil.show1BtnProgressDialog(
							MusicOnlineInformationDetailActivity.this,
							R.string.loading, R.string.cancel,
							new android.view.View.OnClickListener()
							{

								public void onClick(View view)
								{
									mCurrentTask.setIsCancled(true);
									if (mCurrentDialog != null)
									{
										mCurrentDialog.dismiss();
										mCurrentDialog = null;
										mCurrentTask = null;
										mNextItem.setClickable(true);
										MusicOnlineInformationDetailActivity musiconlineinformationdetailactivity = MusicOnlineInformationDetailActivity.this;
										musiconlineinformationdetailactivity.mCurrentPostion = -1
												+ musiconlineinformationdetailactivity.mCurrentPostion;
									}
								}
							});
					break;
				case R.id.previous_item:
					mPreviousItem.setClickable(false);
					MusicOnlineInformationDetailActivity musiconlineinformationdetailactivity = MusicOnlineInformationDetailActivity.this;
					musiconlineinformationdetailactivity.mCurrentPostion = -1
							+ musiconlineinformationdetailactivity.mCurrentPostion;
					requestNewsInfo();
					if (mCurrentDialog != null)
					{
						mCurrentDialog.dismiss();
						mCurrentDialog = null;
					}
					mCurrentDialog = DialogUtil.show1BtnProgressDialog(
							MusicOnlineInformationDetailActivity.this,
							R.string.loading, R.string.cancel,
							new android.view.View.OnClickListener()
							{

								public void onClick(View view)
								{
									mCurrentTask.setIsCancled(true);
									if (mCurrentDialog != null)
									{
										mCurrentDialog.dismiss();
										mCurrentDialog = null;
										mCurrentTask = null;
										mPreviousItem.setClickable(true);
										MusicOnlineInformationDetailActivity musiconlineinformationdetailactivity = MusicOnlineInformationDetailActivity.this;
										musiconlineinformationdetailactivity.mCurrentPostion = 1 + musiconlineinformationdetailactivity.mCurrentPostion;
									}
								}
							});
					break;
				}
			}
		};
	}

	private void onGetNewsInfoResponse(MMHttpTask mmhttptask)
	{
		logger.v("onGetNewsInfoResponse() ---> Enter");
		if (mCurrentDialog != null)
		{
			mCurrentDialog.dismiss();
			mCurrentDialog = null;
		}
		byte abyte0[] = mmhttptask.getResponseBody();
		if ((new XMLParser(abyte0)).getValueByTag("detail") != null)
		{
			mSelfContent = new String(abyte0);
			refreshUI();
		} else
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
		}
		logger.v("onGetNewsInfoResponse() ---> Enter");
	}

	private void onHttpResponse(MMHttpTask mmhttptask)
	{
		logger.v("onHttpResponse() ---> Enter");
		int i = mmhttptask.getRequest().getReqType();
		logger.i("Response type is: " + i);
		XMLParser xmlparser = new XMLParser(mmhttptask.getResponseBody());
		if ((xmlparser.getRoot() == null)
				|| (xmlparser.getValueByTag("code") == null))
		{
			mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
					getText(R.string.title_information_common),
					getText(R.string.fail_to_parse_xml_common),
					new View.OnClickListener()
					{

						public void onClick(View view)
						{
							mCurrentDialog.dismiss();
						}

					});
		} else
		{
			if (xmlparser.getValueByTag("code") != null
					&& xmlparser.getValueByTag("code").equals("000000"))
			{
				mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
						getText(R.string.title_information_common),
						xmlparser.getValueByTag("info"),
						new View.OnClickListener()
						{

							public void onClick(View view)
							{
								mCurrentDialog.dismiss();
							}
						});
			}
		}
		if (this.mCurrentDialog != null)
		{
			this.mCurrentDialog.dismiss();
			this.mCurrentDialog = null;
		}
		switch (i)
		{
		default:
			logger.v("onHttpResponse() ---> Exit");
			break;
		case 1007:
		case 5010:
			onGetNewsInfoResponse(mmhttptask);
			break;
		}
	}

	private void onSendHttpRequestFail(MMHttpTask mmhttptask)
	{
		logger.v("onSendHttpRequestFail() ---> Enter");
		mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(R.string.title_information_common),
				getText(R.string.getfail_data_error_common),
				new android.view.View.OnClickListener()
				{
					public void onClick(View view)
					{
						mCurrentDialog.dismiss();
					}
				});
		logger.v("onSendHttpRequestFail() ---> Exit");
	}

	private void onSendHttpRequestTimeOut(MMHttpTask mmhttptask)
	{
		logger.v("onSendHttpRequestTimeOut() ---> Enter");
		mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(R.string.title_information_common),
				getText(R.string.connect_timeout_common),
				new android.view.View.OnClickListener()
				{
					public void onClick(View view)
					{
						mCurrentDialog.dismiss();
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	private void refreshUI()
	{
		logger.v("refreshUI() ---> Enter");
		XMLParser xmlparser;
		String s;
		SpannableString spannablestring;
		int i;
		Matcher matcher;
		if (mNewsList.size() == 1)
		{
			mPreviousItem.setVisibility(4);
			mNextItem.setVisibility(4);
		} else
		{
			if (mCurrentPostion == 0)
			{
				mPreviousItem.setVisibility(4);
				mNextItem.setVisibility(0);
			} else if (mCurrentPostion == -1 + mNewsList.size())
			{
				mNextItem.setVisibility(4);
				mPreviousItem.setVisibility(0);
			} else
			{
				mPreviousItem.setVisibility(0);
				mNextItem.setVisibility(0);
			}
			mPreviousItem.setClickable(true);
			mNextItem.setClickable(true);
		}
		xmlparser = new XMLParser(mSelfContent.getBytes());
		mInfoTitle.setText(xmlparser.getValueByTag("title"));
		mInfoTime.setText(xmlparser.getValueByTag("publish_time"));
		s = xmlparser.getValueByTagWithKeyWord("detail");
		spannablestring = new SpannableString(s);
		i = s.indexOf("music.10086.cn");
		if (i > 0)
			spannablestring.setSpan(new mClickSpan("http://music.10086.cn"), i,
					i + 14, 33);
		matcher = Util.getUrlMatcher(s);
		do
		{
			if (!matcher.find())
			{
				mInfoDetail.setText(spannablestring);
				mInfoDetail.setMovementMethod(LinkMovementMethod.getInstance());
				mInfoPicUrl = xmlparser.getValueByTag("img");
				if (mInfoPicUrl != null)
				{
					mKeyBitmap = xmlparser.getValueByTag("img");
					mImageDownloader.download(mKeyBitmap,
							R.drawable.default_info_image, mInfoPic,
							xmlparser.getValueByTag("groupcode"));
					mInfoPic.setOnClickListener(new android.view.View.OnClickListener()
					{
						public void onClick(View view)
						{
							// Intent intent = new Intent(
							// MusicOnlineInformationDetailActivity.this,
							// HDImageActivity.class);
							// intent.putExtra("image_url", mInfoPicUrl);
							// startActivity(intent);
						}
					});
				}
				mSongItemDataOfInfo = xmlparser.getListByTagAndAttribute(
						"item", SongListItem.class);
				if (mSongItemDataOfInfo != null)
				{
					mSongListItemOfInfoAdapter = new MobileMusicRecommendListItemAdapter(
							this, mSongItemDataOfInfo);
					mSongListViewOfInformation
							.setAdapter(mSongListItemOfInfoAdapter);
					mSongListViewOfInformation
							.setOnItemClickListener(mOnItemClickListener);
					if (mListButtonClickListener != null)
						mListButtonClickListener
								.setListData(mSongItemDataOfInfo);
					else
					{
						mListButtonClickListener = new ListButtonClickListener(
								this, mSongItemDataOfInfo);
						mSongListItemOfInfoAdapter
								.setBtnOnClickListener(mListButtonClickListener);
					}
					mSongListItemOfInfoAdapter.notifyDataSetChanged();
				}
				logger.v("refreshUI() ---> Enter");
				return;
			}
			spannablestring.setSpan(new mClickSpan(matcher.group()),
					matcher.start(), matcher.end(), 33);
		} while (true);
	}

	private void requestNewsInfo()
	{
		logger.v("requestNewsInfo() ---> Enter");
		CancelPreviousReq();
		char c;
		MMHttpRequest mmhttprequest;
		if (NetUtil.isNetStateWap())
			c = '\u03EF';
		else
			c = '\u1392';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		mmhttprequest.addUrlParams("groupcode",
				((ContentItem) mNewsList.get(mCurrentPostion)).groupcode);
		mmhttprequest.addUrlParams("contentid",
				((ContentItem) mNewsList.get(mCurrentPostion)).contentid);
		mCurrentTask = mHttpController.sendRequest(mmhttprequest);
		logger.v("requestNewsInfo() ---> Exit");
	}

	public void CancelPreviousReq()
	{
		logger.v("CancelPreviousReq() ---> Enter");
		if (mCurrentTask != null)
		{
			mHttpController.cancelTask(mCurrentTask);
			mCurrentTask = null;
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
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
		}
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
		case DispatcherEventEnum.HTTP_EVENT_TASK_CANCELED:
		default:
			break;
		case DispatcherEventEnum.HTTP_EVENT_TASK_COMPLETE:
			onHttpResponse(mmhttptask);
			break;
		case DispatcherEventEnum.HTTP_EVENT_TASK_FAIL:
			onSendHttpRequestFail(mmhttptask);
			break;
		case DispatcherEventEnum.HTTP_EVENT_TASK_TIMEOUT:
			onSendHttpRequestTimeOut(mmhttptask);
			break;
		case DispatcherEventEnum.HTTP_EVENT_WAP_CLOSED:
		case DispatcherEventEnum.WLAN_EVENT_WLAN_CLOSE:
			Uiutil.ifSwitchToWapDialog(this);
			break;
		}
		if ((i == 1007 || i == 5010)
				&& (message.what == 3004 || message.what == 3006))
		{
			mRetryLayout.setVisibility(0);
			mSongListViewOfInformation.setVisibility(8);
			((Button) mRetryLayout.findViewById(R.id.refresh_btn))
					.setOnClickListener(new android.view.View.OnClickListener()
					{

						public void onClick(View view)
						{
							mRetryLayout.setVisibility(8);
							mSongListViewOfInformation.setVisibility(0);
							requestNewsInfo();
						}
					});
		}
		logger.v("handleMMHttpEvent() ---> Exit");
	}

	public void handlePlayerEvent(Message message)
	{
		switch (message.what)
		{
		default:
			return;
		case DispatcherEventEnum.PLAYER_EVENT_WAP_CLOSED:
			Uiutil.ifSwitchToWapDialog(this);
			break;
		case DispatcherEventEnum.PLAYER_EVENT_TRACK_ENDED:
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_START:
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_PAUSE:
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_STOP:
			if (mSongListItemOfInfoAdapter != null)
				mSongListItemOfInfoAdapter.notifyDataSetChanged();
			break;
		}
	}

	public void handleSystemEvent(Message message)
	{
		logger.v("handleSystemEvent() ---> Enter");
		switch (message.what)
		{
		default:
			logger.v("handleSystemEvent() ---> Exit");
			return;
		case DispatcherEventEnum.SYTEM_EVENT_FINISH_ALL_ACTIVITIES:
			finish();
			break;
		}
	}

	public void handleUIEvent(Message message)
	{

		switch (message.what)
		{
		default:
			return;
		case DispatcherEventEnum.UI_EVENT_PLAY_NEWSONG:
			if (mSongListItemOfInfoAdapter != null)
				mSongListItemOfInfoAdapter.notifyDataSetChanged();
			break;
		}
	}

	protected void onCreate(Bundle bundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(bundle);
		requestWindowFeature(1);
		mController = ((MobileMusicApplication) getApplication())
				.getController();
		mPlayerController = mController.getPlayerController();
		mHttpController = mController.getHttpController();
		mImageDownloader = new UrlImageDownloader(this);
		setContentView(R.layout.activity_online_music_information_detail_layout);
		mHeadView = getLayoutInflater().inflate(
				R.layout.list_head_view_of_info, null);
		mPlayerStatusBar = (PlayerStatusBar) findViewById(R.id.playerStatusBar);
		mTitleBar = (TitleBarView) findViewById(R.id.title_view);
		mTitleBar.setCurrentActivity(this);
		mTitleBar.setTitle(R.string.title_infomation_activity);
		mTitleBar.setButtons(0);
		Bundle bundle1;
		if (bundle != null)
			bundle1 = bundle;
		else
			bundle1 = getIntent().getExtras();
		mSelfUrl = bundle1.getString("url");
		mSelfContent = bundle1
				.getString("cmccwm.mobilemusic.mmhttpdefines.xmlRawData");
		mCurrentPostion = bundle1
				.getInt("cmccwm.mobilemusic.onlinemusicinformationactivity.currentpos");
		mNewsList = bundle1
				.getParcelableArrayList("cmccwm.mobilemusic.onlinemusicinformationactivity.allnews");
		mCurrentPageNo = ((ContentItem) mNewsList.get(mCurrentPostion)).current_page;
		mInfoTitle = (TextView) mHeadView.findViewById(R.id.info_title);
		mInfoTime = (TextView) mHeadView.findViewById(R.id.info_time);
		mPreviousItem = (ImageView) mHeadView.findViewById(R.id.previous_item);
		mNextItem = (ImageView) mHeadView.findViewById(R.id.next_item);
		mInfoPic = (ImageView) mHeadView.findViewById(R.id.info_pic);
		mInfoDetail = (TextView) mHeadView.findViewById(R.id.head_info_detail);
		mSongListViewOfInformation = (ListView) findViewById(R.id.info_song_list);
		mSongListViewOfInformation.addHeaderView(mHeadView);
		mPreviousItem.setOnClickListener(mChangeItemListener);
		mNextItem.setOnClickListener(mChangeItemListener);
		mRetryLayout = (LinearLayout) findViewById(R.id.refresh_layout);
		mController.addSystemEventListener(22, this);
		refreshUI();
		logger.v("onCreate() ---> Exit");
	}

	public void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		super.onDestroy();
		mPlayerStatusBar = null;
		mController.removeSystemEventListener(22, this);
		mInfoTitle = null;
		mInfoTime = null;
		mInfoDetail = null;
		mPreviousItem = null;
		mNextItem = null;
		mInfoPic = null;
		mTitleBar = null;
		mSongListViewOfInformation = null;
		if (mSongItemDataOfInfo != null)
		{
			mSongItemDataOfInfo.clear();
			mSongItemDataOfInfo = null;
		}
		mHeadView = null;
		mRetryLayout = null;
		if (mImageDownloader != null)
			mImageDownloader.clearCache();
		mImageDownloader = null;
		if (mSongListItemOfInfoAdapter != null)
		{
			mSongListItemOfInfoAdapter.releaseAdapterResource();
			mSongListItemOfInfoAdapter = null;
		}
		mNewsList = null;
		logger.v("onDestroy() ---> Exit");
	}

	public boolean onKeyDown(int i, KeyEvent keyevent)
	{
		boolean flag = false;
		if (i != 4)
		{
			flag = super.onKeyDown(i, keyevent);
		} else
		{
			if (mListButtonClickListener == null
					|| !mListButtonClickListener.closePopupWindow())
			{
				finish();
				overridePendingTransition(R.anim.player_finish_in,
						R.anim.player_finish_out);
			} else
			{
				flag = true;
			}
		}
		return flag;
	}

	public void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		MobclickAgent.onPause(this);
		CancelPreviousReq();
		mPlayerStatusBar.unRegistEventListener();
		mController.removePlayerEventListener(1002, this);
		mController.removePlayerEventListener(1010, this);
		mController.removePlayerEventListener(1012, this);
		mController.removePlayerEventListener(1011, this);
		mController.removePlayerEventListener(1014, this);
		mController.removeUIEventListener(4008, this);
		mController.removeHttpEventListener(3003, this);
		mController.removeHttpEventListener(3004, this);
		mController.removeHttpEventListener(3006, this);
		mController.removeHttpEventListener(3007, this);
		mController.removeHttpEventListener(3008, this);
		logger.v("onPause() ---> Exit");
	}

	public void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		MobclickAgent.onResume(this);
		mPlayerStatusBar.registEventListener();
		mController.addPlayerEventListener(1002, this);
		mController.addPlayerEventListener(1010, this);
		mController.addPlayerEventListener(1012, this);
		mController.addPlayerEventListener(1011, this);
		mController.addPlayerEventListener(1014, this);
		mController.addUIEventListener(4008, this);
		mController.addHttpEventListener(3003, this);
		mController.addHttpEventListener(3004, this);
		mController.addHttpEventListener(3006, this);
		mController.addHttpEventListener(3007, this);
		mController.addHttpEventListener(3008, this);
		if (mSongListItemOfInfoAdapter != null)
			mSongListItemOfInfoAdapter.notifyDataSetChanged();
		if (mListButtonClickListener != null)
			mListButtonClickListener.doUnCompleteTask();
		mPreviousItem.setClickable(true);
		mNextItem.setClickable(true);
		logger.v("onResume() ---> Exit");
	}

	protected void onSaveInstanceState(Bundle bundle)
	{
		logger.v("onSaveInstanceState() ---> Enter");
		super.onSaveInstanceState(bundle);
		bundle.putCharSequence("cmccwm.mobilemusic.mmhttpdefines.xmlRawData",
				mSelfContent);
		bundle.putCharSequence("url", mSelfUrl);
		bundle.putParcelableArrayList(
				"cmccwm.mobilemusic.onlinemusicinformationactivity.allnews",
				(ArrayList) mNewsList);
		bundle.putInt(
				"cmccwm.mobilemusic.onlinemusicinformationactivity.currentpos",
				mCurrentPostion);
		bundle.putInt("cmccwm.mobilemusic.anylistactivity.currentpage",
				mCurrentPageNo);
		logger.v("onSaveInstanceState() ---> Exit");
	}

}
