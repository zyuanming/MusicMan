package org.ming.ui.activity.online;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.ming.center.Controller;
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
import org.ming.ui.adapter.MobileMusicRecommendListItemAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.view.RemoteImageView;
import org.ming.ui.view.TitleBarView;
import org.ming.ui.widget.PlayerStatusBar;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;
import org.ming.util.XMLParser;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

public class MusicOnlineRecommendInfoDetailActivity extends Activity implements
		MMHttpEventListener, SystemEventListener, PlayerEventListener,
		UIEventListener
{
	private static final MyLogger logger = MyLogger
			.getLogger("MusicOnlineRecommendInfoDetailActivity");
	private Controller mController = null;
	private Dialog mCurrentDialog = null;
	private MMHttpTask mCurrentTask = null;
	private View mHeadView;
	private HttpController mHttpController = null;
	private TextView mInfoDetail;
	private RemoteImageView mInfoPic;
	private String mInfoPicUrl;
	private TextView mInfoTitle;
	private boolean mIsFromPushService = false;
	private boolean mIsInital = false;
	private ListButtonClickListener mListButtonClickListener = null;
	private ImageView mNextItem;
	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int i, long l)
		{
			MusicOnlineRecommendInfoDetailActivity.logger
					.v("mOnItemClickListener ---> Enter");
			Song localSong1 = mPlayerController.getCurrentPlayingItem();
			int j = i - mSongListViewOfInformation.getHeaderViewsCount();
			if (j >= 0 && j < mSongItemDataOfInfo.size())
			{
				SongListItem localSongListItem = (SongListItem) mSongItemDataOfInfo
						.get(i);
				if ((localSong1 != null)
						&& (localSong1.mMusicType != MusicType.LOCALMUSIC
								.ordinal())
						&& (localSong1.mMusicType != MusicType.RADIO.ordinal())
						&& (localSong1.mContentId == localSongListItem.contentid))
				{
					mPlayerController.pause();
					long l1 = mPlayerController
							.addCurrentTrack2OnlineMusicTable(localSongListItem);
					if (l1 != -1L)
						mPlayerController.addCurrentTrack2RecentPlaylist(
								localSongListItem, l);
					Song localSong2 = Util.makeSong(localSongListItem);
					localSong2.mId = l1;
					int k = mPlayerController.add2NowPlayingList(localSong2);
					mPlayerController.open(k);
					logger.v("mOnItemClickListener ---> Exit");
				}
			}

		}
	};
	private PlayerController mPlayerController = null;
	private PlayerStatusBar mPlayerStatusBar = null;
	private ImageView mPreviousItem;
	private LinearLayout mRetryLayout;
	private String mSelfContent = null;
	private List<SongListItem> mSongItemDataOfInfo = new ArrayList();
	private MobileMusicRecommendListItemAdapter mSongListItemOfInfoAdapter;
	private ListView mSongListViewOfInformation;
	private TitleBarView mTitleBar;
	private String mURL = null;

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
			mIsInital = true;
		} else
		{
			mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
					getText(0x7f070041), getText(0x7f070040),
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

	private void onHttpResponse(MMHttpTask paramMMHttpTask)
	{
		logger.v("onHttpResponse() ---> Enter");
		int i = paramMMHttpTask.getRequest().getReqType();
		logger.i("Response type is: " + i);
		XMLParser localXMLParser = new XMLParser(
				paramMMHttpTask.getResponseBody());
		if ((localXMLParser.getRoot() == null)
				|| (localXMLParser.getValueByTag("code") == null))
		{
			this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
					getText(2131165249), getText(2131165248),
					new View.OnClickListener()
					{
						public void onClick(View paramAnonymousView)
						{
							MusicOnlineRecommendInfoDetailActivity.this.mCurrentDialog
									.dismiss();
						}
					});
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
		} else
		{
			if ((localXMLParser.getValueByTag("code") != null)
					&& (localXMLParser.getValueByTag("code").equals("000000")))
			{
				this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
						this, getText(2131165249),
						localXMLParser.getValueByTag("info"),
						new View.OnClickListener()
						{
							public void onClick(View paramAnonymousView)
							{
								MusicOnlineRecommendInfoDetailActivity.this.mCurrentDialog
										.dismiss();
							}
						});
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
					onGetNewsInfoResponse(paramMMHttpTask);
					break;
				}
			}
		}
	}

	private void onSendHttpRequestClose()
	{
		logger.v("onSendHttpRequestClose() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(2131165249), getText(2131165252),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MusicOnlineRecommendInfoDetailActivity.this.mCurrentDialog != null)
						{
							MusicOnlineRecommendInfoDetailActivity.this.mCurrentDialog
									.dismiss();
							MusicOnlineRecommendInfoDetailActivity.this.mCurrentDialog = null;
						}
					}
				});
		logger.v("onSendHttpRequestClose() ---> Exit");
	}

	private void onSendHttpRequestFail(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestFail() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(2131165249), getText(2131165269),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						MusicOnlineRecommendInfoDetailActivity.this.mCurrentDialog
								.dismiss();
					}
				});
		logger.v("onSendHttpRequestFail() ---> Exit");
	}

	private void onSendHttpRequestTimeOut(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestTimeOut() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(2131165249), getText(2131165254),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						MusicOnlineRecommendInfoDetailActivity.this.mCurrentDialog
								.dismiss();
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	private void refreshUI()
	{
		logger.v("refreshUI() ---> Enter");
		XMLParser xmlparser = new XMLParser(mSelfContent.getBytes());
		mInfoTitle.setText(xmlparser.getValueByTag("title"));
		String s = xmlparser.getValueByTagWithKeyWord("detail");
		SpannableString spannablestring = new SpannableString(s);
		int i = s.indexOf("music.10086.cn");
		if (i > 0)
			spannablestring.setSpan(new mClickSpan("http://music.10086.cn"), i,
					i + 14, 33);
		Matcher matcher = Util.getUrlMatcher(s);
		do
		{
			if (!matcher.find())
			{
				mInfoDetail.setText(spannablestring);
				mInfoDetail.setMovementMethod(LinkMovementMethod.getInstance());
				mInfoPicUrl = xmlparser.getValueByTag("img");
				if (mInfoPicUrl != null)
				{
					mInfoPic.setImageUrl(xmlparser.getValueByTag("img"));
					mInfoPic.setOnClickListener(new android.view.View.OnClickListener()
					{
						public void onClick(View view)
						{
							// Intent intent = new Intent(
							// MusicOnlineRecommendInfoDetailActivity.this,
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
					if (mListButtonClickListener != null)
						mListButtonClickListener
								.setListData(mSongItemDataOfInfo);
					else
						mListButtonClickListener = new ListButtonClickListener(
								this, mSongItemDataOfInfo);
					mSongListItemOfInfoAdapter
							.setBtnOnClickListener(mListButtonClickListener);
					mSongListViewOfInformation
							.setAdapter(mSongListItemOfInfoAdapter);
					mSongListViewOfInformation
							.setOnItemClickListener(mOnItemClickListener);
					mSongListItemOfInfoAdapter.notifyDataSetChanged();
				}
				logger.v("refreshUI() ---> Exit");
				return;
			}
			spannablestring.setSpan(new mClickSpan(matcher.group()),
					matcher.start(), matcher.end(), 33);
		} while (true);
	}

	private void startReqRecommendInfo()
	{
		char c;
		MMHttpRequest mmhttprequest;
		String s;
		String s1;
		String s2;
		if (NetUtil.isNetStateWap())
			c = '\u03EF';
		else
			c = '\u1392';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		s = mURL;
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
		mCurrentTask = mHttpController.sendRequest(mmhttprequest);
		if (mCurrentDialog != null)
		{
			mCurrentDialog.dismiss();
			mCurrentDialog = null;
		}
		mCurrentDialog = DialogUtil.show1BtnProgressDialog(this, 0x7f07006b,
				0x7f070024, new android.view.View.OnClickListener()
				{
					public void onClick(View view)
					{
						if (mCurrentTask != null)
							mCurrentTask.setIsCancled(true);
						if (mCurrentDialog != null)
						{
							mCurrentDialog.dismiss();
							mCurrentDialog = null;
							mCurrentTask = null;
						}
						mRetryLayout.setVisibility(0);
						((Button) mRetryLayout.findViewById(0x7f050023))
								.setOnClickListener(new android.view.View.OnClickListener()
								{

									public void onClick(View view)
									{
										mRetryLayout.setVisibility(8);
										startReqRecommendInfo();
									}
								});
					}

				});
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

	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
	{
		boolean bool = true;
		switch (paramMotionEvent.getAction())
		{
		default:
			bool = super.dispatchTouchEvent(paramMotionEvent);
		case 0:
			if ((this.mListButtonClickListener != null)
					&& (!this.mListButtonClickListener.closePopupWindow()))
				bool = true;
		}
		return bool;

	}

	public void handleMMHttpEvent(Message paramMessage)
	{
		logger.v("handleMMHttpEvent() ---> Enter");
		MMHttpTask localMMHttpTask = (MMHttpTask) paramMessage.obj;
		if ((localMMHttpTask == null)
				|| (this.mCurrentTask == null)
				|| (localMMHttpTask.getTransId() != this.mCurrentTask
						.getTransId()))
		{
			logger.v("Thus http message is not for this activity");
			return;
		} else
		{
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
			switch (paramMessage.what)
			{
			case 3005:
			default:
				logger.v("handleMMHttpEvent() ---> Exit");
				break;
			case 3003:
				onHttpResponse(localMMHttpTask);
				break;
			case 3004:
				onSendHttpRequestFail(localMMHttpTask);
				break;
			case 3006:
				onSendHttpRequestTimeOut(localMMHttpTask);
				break;
			case 3007:
			case 3008:
				Uiutil.ifSwitchToWapDialog(this);
				break;
			}
		}
	}

	public void handlePlayerEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			return;
		case 1014:
			Uiutil.ifSwitchToWapDialog(this);
			break;
		case 1002:
		case 1010:
		case 1011:
		case 1012:
			if (this.mSongListItemOfInfoAdapter != null)
				this.mSongListItemOfInfoAdapter.notifyDataSetChanged();
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
			Util.exitMobileMusicApp(false);
			finish();
			break;
		}
	}

	public void handleUIEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			break;
		case 4008:
			if (this.mSongListItemOfInfoAdapter != null)
				this.mSongListItemOfInfoAdapter.notifyDataSetChanged();
			break;
		}
	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		this.mController = ((MobileMusicApplication) getApplication())
				.getController();
		this.mPlayerController = this.mController.getPlayerController();
		this.mHttpController = this.mController.getHttpController();
		setContentView(2130903078);
		this.mHeadView = getLayoutInflater().inflate(2130903132, null);
		this.mRetryLayout = ((LinearLayout) findViewById(2131034146));
		this.mPlayerStatusBar = ((PlayerStatusBar) findViewById(2131034123));
		this.mTitleBar = ((TitleBarView) findViewById(2131034122));
		this.mTitleBar.setCurrentActivity(this);
		this.mTitleBar.setTitle(2131165445);
		this.mTitleBar.setButtons(0);
		Bundle localBundle = getIntent().getExtras();
		if (localBundle != null)
		{
			this.mURL = localBundle
					.getString("mobi.mobiemusic.ui.activity.online.onlinemusicsubscribeactivity.entry.url");
			this.mIsFromPushService = localBundle.getBoolean("fromPushService");
		}
		this.mPreviousItem = ((ImageView) this.mHeadView
				.findViewById(2131034415));
		this.mPreviousItem.setVisibility(4);
		this.mNextItem = ((ImageView) this.mHeadView.findViewById(2131034417));
		this.mNextItem.setVisibility(4);
		this.mInfoTitle = ((TextView) this.mHeadView.findViewById(2131034413));
		this.mInfoPic = ((RemoteImageView) this.mHeadView
				.findViewById(2131034416));
		this.mInfoDetail = ((TextView) this.mHeadView.findViewById(2131034418));
		this.mSongListViewOfInformation = ((ListView) findViewById(2131034271));
		this.mSongListViewOfInformation.addHeaderView(this.mHeadView);
		this.mController.addSystemEventListener(22, this);
		logger.v("onCreate() ---> Exit");
	}

	public void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		this.mController.removeSystemEventListener(22, this);
		this.mSongListViewOfInformation = null;
		if (this.mSongItemDataOfInfo != null)
		{
			this.mSongItemDataOfInfo.clear();
			this.mSongItemDataOfInfo = null;
		}
		this.mPlayerController = null;
		this.mHeadView = null;
		this.mRetryLayout = null;
		this.mPlayerStatusBar = null;
		if (this.mSongListItemOfInfoAdapter != null)
		{
			this.mSongListItemOfInfoAdapter.releaseAdapterResource();
			this.mSongListItemOfInfoAdapter = null;
		}
		super.onDestroy();
		logger.v("onDestroy() ---> Exit");
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
		boolean bool = true;
		if (paramInt == 4)
		{
			if ((this.mListButtonClickListener != null)
					&& (this.mListButtonClickListener.closePopupWindow()))
				return bool;
			else
			{
				if (mIsFromPushService)
				{
					Intent intent = new Intent(this,
							MobileMusicMainActivity.class);
					intent.putExtra("startFromNotification", bool);
					startActivity(intent);
					finish();
				} else
				{
					finish();
				}
			}
		}
		bool = super.onKeyDown(paramInt, paramKeyEvent);
		return bool;
	}

	public void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		MobclickAgent.onPause(this);
		setProgressBarIndeterminateVisibility(false);
		CancelPreviousReq();
		this.mPlayerStatusBar.unRegistEventListener();
		this.mController.removePlayerEventListener(1002, this);
		this.mController.removePlayerEventListener(1010, this);
		this.mController.removePlayerEventListener(1012, this);
		this.mController.removePlayerEventListener(1011, this);
		this.mController.removePlayerEventListener(1014, this);
		this.mController.removeUIEventListener(4008, this);
		this.mController.removeHttpEventListener(3003, this);
		this.mController.removeHttpEventListener(3004, this);
		this.mController.removeHttpEventListener(3006, this);
		this.mController.removeHttpEventListener(3007, this);
		this.mController.removeHttpEventListener(3008, this);
		logger.v("onPause() ---> Exit");
	}

	public void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		MobclickAgent.onResume(this);
		this.mPlayerStatusBar.registEventListener();
		if (this.mSongListItemOfInfoAdapter != null)
			this.mSongListItemOfInfoAdapter.notifyDataSetChanged();
		this.mController.addPlayerEventListener(1002, this);
		this.mController.addPlayerEventListener(1010, this);
		this.mController.addPlayerEventListener(1012, this);
		this.mController.addPlayerEventListener(1011, this);
		this.mController.addPlayerEventListener(1014, this);
		this.mController.addUIEventListener(4008, this);
		this.mController.addHttpEventListener(3003, this);
		this.mController.addHttpEventListener(3004, this);
		this.mController.addHttpEventListener(3006, this);
		this.mController.addHttpEventListener(3007, this);
		this.mController.addHttpEventListener(3008, this);
		if ((this.mCurrentTask == null) && (!this.mIsInital))
			startReqRecommendInfo();
		this.mTitleBar.setLeftBtnOnlickListner(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (mIsFromPushService)
				{
					Intent intent = new Intent(
							MusicOnlineRecommendInfoDetailActivity.this,
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
		if (this.mListButtonClickListener != null)
			this.mListButtonClickListener.doUnCompleteTask();
		logger.v("onResume() ---> Exit");
	}

	class mClickSpan extends ClickableSpan implements View.OnClickListener
	{
		String tag;

		public mClickSpan(String arg2)
		{
			this.tag = arg2;
		}

		public void onClick(View paramView)
		{
			// if (!Uiutil.isNetChange())
			// {
			// if (!Uiutil.isNetChange())
			// {
			// Intent intent = new Intent(
			// MusicOnlineRecommendInfoDetailActivity.this,
			// MusicOnlineInformationDetailByUrlActivity.class);
			// intent.putExtra(
			// MusicOnlineInformationDetailByUrlActivity.INTENT_DATA_URL,
			// tag);
			// intent.putExtra(
			// MusicOnlineInformationDetailByUrlActivity.INTENT_DATA_TITLE,
			// mInfoTitle.getText());
			// startActivity(intent);
			// } else
			// {
			// Uiutil.ifSwitchToWapDialog(MusicOnlineRecommendInfoDetailActivity.this);
			// }
			// }
		}
	}
}