package org.ming.ui.activity.online;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.business.CMCCMusicBusiness;
import org.ming.center.business.MusicBusinessDefine_Net;
import org.ming.center.business.MusicBusinessDefine_WAP;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.http.item.AdListItem;
import org.ming.center.http.item.ContentItem;
import org.ming.center.http.item.MusicListColumnItem;
import org.ming.center.http.item.SongItem;
import org.ming.center.http.item.SongListItem;
import org.ming.center.http.item.TabItem;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.ListButtonClickListener;
import org.ming.center.ui.UIEventListener;
import org.ming.dispatcher.DispatcherEventEnum;
import org.ming.ui.activity.MusicPlayerActivity;
import org.ming.ui.adapter.MobileMusicAlbumListItemAdapter;
import org.ming.ui.adapter.MobileMusicColumnListItemAdapter;
import org.ming.ui.adapter.MobileMusicContentListItemAdapter;
import org.ming.ui.adapter.MobileMusicRecommendListItemAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.view.AdView;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;
import org.ming.util.XMLParser;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class OnlineMusicActivity extends Activity implements
		MMHttpEventListener, SystemEventListener, UIEventListener,
		PlayerEventListener
{
	private static final MyLogger logger = MyLogger
			.getLogger("OnlineMusicActivity");
	public static ListButtonClickListener mListButtonClickListener;
	private boolean LOAD_MORE = true;
	AdView adView;
	private boolean headerMove = false;
	private List<AdListItem> mADContentList = new ArrayList();
	private MobileMusicAlbumListItemAdapter mAlbumAdapter = null;
	private GridView mAlbumGridView;
	private AdapterView.OnItemClickListener mAlbumItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int paramAnonymousInt,
				long paramAnonymousLong)
		{
			logger.v("onListItemClick() ---> Enter " + paramAnonymousInt);
			MusicListColumnItem localMusicListColumnItem = (MusicListColumnItem) mAlbumItemData
					.get(paramAnonymousInt);
			if ((localMusicListColumnItem.category_type.equals(""))
					&& (localMusicListColumnItem.img.equals(""))
					&& (localMusicListColumnItem.title.equals(""))
					&& (localMusicListColumnItem.url.equals("")))
			{
				mInital = false;
				mAlbumAdapter.setProgressVisiable(true);
				mAlbumAdapter.notifyDataSetChanged();
				mTargetPageNo = (1 + mCurrentPageNo);
				if ((mTargetPageNo > 1) && (mTargetPageNo <= mPageTotalCount))
					requestAlbumGpoup(((TabItem) tabInfoList
							.get(mButtonPosition)).url);
			} else
			{
				Intent localIntent = new Intent(OnlineMusicActivity.this,
						MusicOnlineMusicAlbumDetailActivity.class);
				localIntent.putExtra("title", localMusicListColumnItem.title);
				localIntent.putExtra("album_song_url",
						localMusicListColumnItem.url);
				startActivity(localIntent);
			}

			logger.v("onListItemClick() ---> Exit");
		}
	};
	private ArrayList<MusicListColumnItem> mAlbumItemData = new ArrayList();
	private int mButtonPosition;
	private Dialog mCheckOlderVersionDialog = null;
	private ListView mColumnContentListView;
	private ArrayList<MusicListColumnItem> mColumnItemData = new ArrayList();
	private Controller mController;
	private int mCurShortClickSelectedItem = -1;
	private Dialog mCurrentDialog = null;
	private int mCurrentPageNo = 1;
	private MMHttpTask mCurrentTask;
	private ImageView mEmpty = null;
	private View mHeadView;
	private HttpController mHttpController;
	private ArrayList<ContentItem> mInfoItemData = new ArrayList();
	private boolean mInital = false;
	private boolean mIsInital = false;
	private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> adapterview, View view, int i,
				long l)
		{
			logger.v("onListItemClick() ---> Enter");
			if ((mButtonPosition < tabInfoList.size())
					|| (mButtonPosition >= 0))
			{
				switch (Integer.parseInt(((TabItem) tabInfoList
						.get(mButtonPosition)).category_type))
				{
				case 3:
				case 4:
				default:
					int k1 = i - mColumnContentListView.getHeaderViewsCount();
					MusicListColumnItem musiclistcolumnitem1 = (MusicListColumnItem) mColumnItemData
							.get(k1);
					Intent intent1 = new Intent(OnlineMusicActivity.this,
							MusicOnlineMusicColumnDetailActivity.class);
					intent1.putExtra("title", musiclistcolumnitem1.title);
					intent1.putExtra("COLUMITEM", musiclistcolumnitem1);
					startActivity(intent1);
					break;
				case 0:
					int k = i - mRecommendContentListView.getHeaderViewsCount();
					Song song = mPlayerController.getCurrentPlayingItem();
					if (mRecommendSongItemData.size() <= k)
						k = -1 + mRecommendSongItemData.size();
					if (k > 0)
					{
						int i1 = mRecommendSongItemData.size();
						if (k < i1)
						{
							SongListItem songlistitem = (SongListItem) mRecommendSongItemData
									.get(k);
							if (song != null
									&& song.mMusicType != MusicType.LOCALMUSIC
											.ordinal()
									&& song.mMusicType != MusicType.RADIO
											.ordinal()
									&& song.mContentId == songlistitem.contentid)
							{
								mPlayerController.pause();
							} else
							{
								long l1 = mPlayerController
										.addCurrentTrack2OnlineMusicTable(songlistitem);
								if (l1 != -1L)
									mPlayerController
											.addCurrentTrack2RecentPlaylist(
													songlistitem, l1);
								Song song1 = Util.makeSong(songlistitem);
								song1.mId = l1;
								int j1 = mPlayerController
										.add2NowPlayingList(song1);
								mPlayerController.open(j1);
							}
							mMobileMusicRecommendListItemAdapter
									.notifyDataSetChanged();
							logger.v("onListItemClick() ---> out");
						}
					}
					break;
				case 1:
				case 5:
					int j = i - mColumnContentListView.getHeaderViewsCount();
					if (j < mColumnItemData.size() && j > 0)
					{
						MusicListColumnItem musiclistcolumnitem = (MusicListColumnItem) mColumnItemData
								.get(j);
						if (!musiclistcolumnitem.category_type
								.equals(CMCCMusicBusiness.CATEGORY_TYPE_0)
								&& !musiclistcolumnitem.category_type
										.equals(CMCCMusicBusiness.CATEGORY_TYPE_1))
						{
							if (musiclistcolumnitem.category_type
									.equals(CMCCMusicBusiness.CATEGORY_TYPE_2))
							{
								if (mPlayerController.isPlaying())
									mPlayerController.stop();
								if (j == 0 && gotoLogin())
								{
									return;
								} else
								{
									requestRadioSongList(j);
								}
							}
						} else
						{
							Intent intent = new Intent(
									OnlineMusicActivity.this,
									org.ming.ui.activity.online.MusicOnlineMusicColumnDetailActivity.class);
							intent.putExtra("title", musiclistcolumnitem.title);
							intent.putExtra("COLUMITEM", musiclistcolumnitem);
							startActivity(intent);
							logger.v("onListItemClick() ---> out");
						}
					}
					break;
				case 2:
					if (mCurShortClickSelectedItem != (int) l)
					{
						logger.v("onItemClick() ---> Enter");
						int g;
						MMHttpRequest mmhttprequest;
						if (NetUtil.isNetStateWap())
							g = 1007;
						else
							g = 5010;
						mmhttprequest = MMHttpRequestBuilder.buildRequest(g);
						mmhttprequest
								.addUrlParams("groupcode",
										((ContentItem) mInfoItemData
												.get((int) l)).groupcode);
						mmhttprequest
								.addUrlParams("contentid",
										((ContentItem) mInfoItemData
												.get((int) l)).contentid);
						mCurrentTask = mHttpController
								.sendRequest(mmhttprequest);
						mCurShortClickSelectedItem = (int) l;
						mCurrentDialog = DialogUtil.show1BtnProgressDialog(
								OnlineMusicActivity.this, R.string.loading,
								R.string.cancel, new View.OnClickListener()
								{
									public void onClick(View view)
									{
										cancleDialog();
									}
								});
					}
					break;
				}
			}
		}
	};
	private View.OnClickListener mLoadMoreOnClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			logger.v("mLoadMoreOnClickListener() ---> Exit");
			mReqMoreProgress.setVisibility(0);
			mInital = false;
			mTargetPageNo = (1 + mCurrentPageNo);
			String s;
			if ((mTargetPageNo > 1) && (mTargetPageNo <= mPageTotalCount))
			{
				s = ((TabItem) tabInfoList.get(mButtonPosition)).url;
				switch (Integer.parseInt(((TabItem) tabInfoList
						.get(mButtonPosition)).category_type))
				{
				case 3:
				case 4:
				default:
					requestColumnGpoup(s);
					break;
				case 0:
					requestRecommendContentListData(s);
					break;
				case 1:
				case 5:
					requestColumnGpoup(s);
					break;
				case 2:
					requestInfoGroup(s);
					break;
				}
			}
		}
	};
	private View mLoadMoreView;
	private MobileMusicColumnListItemAdapter mMobileMusicColumnListItemAdapter = null;
	private MobileMusicContentListItemAdapter mMobileMusicContentListItemAdapter = null;
	private MobileMusicRecommendListItemAdapter mMobileMusicRecommendListItemAdapter = null;
	private int mPageTotalCount = -1;
	private PlayerController mPlayerController = null;
	private boolean mPlayerLoveRadio = false;
	private ListView mRecommendContentListView;
	private List<SongListItem> mRecommendSongItemData = new ArrayList();
	private ProgressBar mReqMoreProgress;
	private LinearLayout mRetryLayout;
	AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener()
	{
		int firstItem = -1;
		int totalItem = 0;
		int visibleItem = 0;

		public void onScroll(AbsListView paramAnonymousAbsListView, int i,
				int j, int k)
		{
			if (LOAD_MORE)
			{
				this.firstItem = i;
				this.visibleItem = j;
				this.totalItem = k;
			}
		}

		public void onScrollStateChanged(AbsListView abslistview, int i)
		{
			if ((LOAD_MORE) && (i == 0)
					&& (this.firstItem + this.visibleItem == this.totalItem))
			{
				if (abslistview.getId() != R.id.album_list_temp)
				{
					if (abslistview.getId() == R.id.column_list_temp
							|| abslistview.getId() == R.id.recommned_list_temp)
					{
						mLoadMoreOnClickListener.onClick(mLoadMoreView);
						LOAD_MORE = false;
					}
					if (abslistview.getId() == R.id.recommned_list_temp)
						if (firstItem > 0 && adView.getStatus() != 1)
							adView.pause();
						else if (firstItem == 0 && i == 0
								&& adView.getStatus() == 1)
							adView.resume();
				} else
				{
					mInital = false;
					if (mAlbumAdapter != null)
					{
						mAlbumAdapter.setProgressVisiable(true);
						mAlbumAdapter.notifyDataSetChanged();
						mTargetPageNo = 1 + mCurrentPageNo;
						if (mTargetPageNo > 1
								&& mTargetPageNo <= mPageTotalCount)
							requestAlbumGpoup(((TabItem) tabInfoList
									.get(mButtonPosition)).url);
						LOAD_MORE = false;
					}
				}
			} else
			{
				if (abslistview.getId() != R.id.album_list_temp)
				{
					if (abslistview.getId() == R.id.column_list_temp
							|| abslistview.getId() == R.id.recommned_list_temp)
					{
						mLoadMoreOnClickListener.onClick(mLoadMoreView);
						LOAD_MORE = false;
					}
					if (abslistview.getId() == R.id.recommned_list_temp)
						if (firstItem > 0 && adView.getStatus() != 1)
							adView.pause();
						else if (firstItem == 0 && i == 0
								&& adView.getStatus() == 1)
							adView.resume();
				} else
				{
					mInital = false;
					if (mAlbumAdapter != null)
					{
						mAlbumAdapter.setProgressVisiable(true);
						mAlbumAdapter.notifyDataSetChanged();
						mTargetPageNo = 1 + mCurrentPageNo;
						if (mTargetPageNo > 1
								&& mTargetPageNo <= mPageTotalCount)
							requestAlbumGpoup(((TabItem) tabInfoList
									.get(mButtonPosition)).url);
						LOAD_MORE = false;
					}
				}
			}
		}
	};
	private Button mSearchBtn;
	private int mTargetPageNo = 1;
	private List<TabItem> tabInfoList;

	private void adViewClick(int paramInt)
	{
		int i = Integer.parseInt(((AdListItem) this.mADContentList
				.get(paramInt)).content_type);
		Intent intent = null;
		switch (i)
		{
		default:
			if (intent != null)
			{
				intent.putExtra(
						"mobi.mobiemusic.ui.activity.online.onlinemusicsubscribeactivity.entry.url",
						((AdListItem) mADContentList.get(i)).url);
				startActivity(intent);
			}
			return;
		case 1:
			char c;
			MMHttpRequest mmhttprequest;
			String s;
			String s1;
			String s2;
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
			mCurrentDialog = DialogUtil.showIndeterminateProgressDialog(
					getParent(), R.string.loading_radio_list_activity);
			s = ((AdListItem) mADContentList.get(i)).url;
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
			intent = null;
			break;
		case 2:
			// intent = new Intent(this,
			// MusicOnlineRecommendInfoDetailActivity.class);
			break;
		case 3:
			intent = new Intent(this, MusicPlayerActivity.class);
			intent.putExtra(
					"mobi.mobiemusic.ui.activity.online.onlinemusicsubscribeactivity.from.recommed",
					3);
			break;
		case 4:
			// intent = new Intent(this,
			// MusicOnlineRecommendContentActivity.class);
			// intent.putExtra("title", ((AdListItem)
			// mADContentList.get(i)).title);
			break;
		case 5:
			// intent = new Intent(this,
			// MusicOnlineRecommendInforListActivity.class);
			break;
		case 6:
			// intent = new Intent(this,
			// MusicOnlineRecommendColumnActivity.class);
			break;
		case 7:
			intent = new Intent("android.intent.action.VIEW",
					Uri.parse(((AdListItem) mADContentList.get(i)).url));
			break;
		}
	}

	private void cancleDialog()
	{
		if (this.mCurrentTask != null)
			this.mCurrentTask.setIsCancled(true);
		if (this.mCurrentDialog != null)
		{
			this.mCurrentDialog.dismiss();
			this.mCurShortClickSelectedItem = -1;
			this.mCurrentDialog = null;
			if (this.mCurrentTask != null)
				this.mCurrentTask = null;
		}
	}

	private void checkOlderMusicVersion()
	{
		PackageManager packagemanager = getPackageManager();
		try
		{
			if (packagemanager
					.getPackageInfo("mobi.redcloud.mobilemusic", 8192) != null)
				mCheckOlderVersionDialog = DialogUtil
						.show2BtnDialogWithCheckBoxIconTitleMsg(
								getParent(),
								getText(R.string.title_information_common),
								getText(R.string.text_check_older_version_text),
								new android.view.View.OnClickListener()
								{
									public void onClick(View view)
									{
										mCheckOlderVersionDialog.dismiss();
										mCheckOlderVersionDialog = null;
										Intent intent = new Intent(
												"android.intent.action.DELETE",
												Uri.fromParts(
														"package",
														"mobi.redcloud.mobilemusic",
														null));
										startActivity(intent);
									}
								}, new android.view.View.OnClickListener()
								{
									public void onClick(View view)
									{
										mCheckOlderVersionDialog.dismiss();
										mCheckOlderVersionDialog = null;
									}
								});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void createBtListView()
	{
		final ArrayList localArrayList = new ArrayList();
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.tabhost_layout);
		for (int i = 0;; i++)
		{
			if (i >= this.tabInfoList.size())
			{
				((View) localArrayList.get(0))
						.setBackgroundResource(R.drawable.main_column_navigation_lable_hl);
				((Button) localArrayList.get(0)).requestFocus();
				((Button) localArrayList.get(0)).setTextColor(-1);
				return;
			}
			Button localButton = new Button(this);
			localButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.main_column_navigation_lable_selector));
			FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
					(int) getResources().getDimension(
							R.dimen.music_online_tab_width), -2);
			localLayoutParams.gravity = 17;
			localButton.setLayoutParams(localLayoutParams);
			localButton.setGravity(17);
			localButton.setText(((TabItem) this.tabInfoList.get(i)).title);
			localButton.setTextColor(-3808769);
			localButton.setTag(Integer.valueOf(i));
			if (((TabItem) this.tabInfoList.get(i)).category_type.equals("3"))
				localButton.setVisibility(8);
			localArrayList.add(localButton);
			localButton.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View view)
				{
					String str;
					for (int j = 0; j < localArrayList.size(); j++)
					{
						((View) localArrayList.get(j))
								.setBackgroundDrawable(getResources()
										.getDrawable(
												R.drawable.main_column_navigation_lable_nor));
						((Button) localArrayList.get(j))
								.setTextColor(0xffc5e1ff);
					}
					((View) localArrayList.get(((Integer) view.getTag())
							.intValue()))
							.setBackgroundResource(R.drawable.main_column_navigation_lable_hl);
					((View) localArrayList.get(((Integer) view.getTag())
							.intValue())).setSelected(true);
					((Button) localArrayList.get(((Integer) view.getTag())
							.intValue())).setTextColor(-1);
					if (mButtonPosition != ((Integer) view.getTag()).intValue())
					{
						System.gc();
						mEmpty.setVisibility(0);
						mInital = false;
						mPageTotalCount = -1;
						mCurrentPageNo = 1;
						mTargetPageNo = 1;
						CancelPreviousReq();
						mRecommendSongItemData.clear();
						mAlbumItemData.clear();
						mColumnItemData.clear();
						mInfoItemData.clear();
						if (mMobileMusicRecommendListItemAdapter != null)
							mMobileMusicRecommendListItemAdapter
									.releaseAdapterResource();
						mMobileMusicRecommendListItemAdapter = null;
						if (mMobileMusicColumnListItemAdapter != null)
							mMobileMusicColumnListItemAdapter
									.releaseAdapterResource();
						mMobileMusicColumnListItemAdapter = null;
						if (mAlbumAdapter != null)
							mAlbumAdapter.releaseAdapterResource();
						mAlbumAdapter = null;
						if (mMobileMusicContentListItemAdapter != null)
							mMobileMusicContentListItemAdapter
									.releaseAdapterResource();
						mMobileMusicContentListItemAdapter = null;
						mButtonPosition = ((Integer) view.getTag()).intValue();
						str = ((TabItem) tabInfoList.get(mButtonPosition)).url;
						switch (Integer.parseInt(((TabItem) tabInfoList
								.get(mButtonPosition)).category_type))
						{
						case 3:
						default:
							mRecommendContentListView.setVisibility(8);
							mColumnContentListView.setVisibility(0);
							mAlbumGridView.setVisibility(8);
							requestColumnGpoup(str);
						case 0:
							mRecommendContentListView.setVisibility(0);
							mColumnContentListView.setVisibility(8);
							mAlbumGridView.setVisibility(8);
							requestRecommendContentListData(str);
							return;
						case 1:
						case 5:
							mRecommendContentListView.setVisibility(8);
							mColumnContentListView.setVisibility(0);
							mAlbumGridView.setVisibility(8);
							requestColumnGpoup(str);
							return;
						case 2:
							mRecommendContentListView.setVisibility(8);
							mColumnContentListView.setVisibility(0);
							mAlbumGridView.setVisibility(8);
							requestInfoGroup(str);
							return;
						case 4:
							mRecommendContentListView.setVisibility(8);
							mColumnContentListView.setVisibility(8);
							mAlbumGridView.setVisibility(0);
							requestAlbumGpoup(str);
							return;
						}
					}
				}
			});
			localLinearLayout.addView(localButton);
		}
	}

	private void exitApplication()
	{
		logger.v("exitApplication() ---> Enter");
		this.mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(this,
				getText(R.string.quit_app_dialog_title),
				getText(R.string.quit_app_dialog_message),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						mCurrentDialog.dismiss();
						mCurrentDialog = null;
						Util.exitMobileMusicApp(false);
					}
				}, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						mCurrentDialog.dismiss();
						mCurrentDialog = null;
					}
				});
		this.mCurrentDialog.setCancelable(false);
		logger.v("exitApplication() ---> Exit");
	}

	private boolean gotoLogin()
	{
		boolean flag = true;
		logger.v("gotoLogin() ---> Enter");
		if (GlobalSettingParameter.useraccount == null)
		{
			Uiutil.login(this, 0);
			mPlayerLoveRadio = flag;
		} else
		{
			logger.v("gotoLogin() ---> Exit");
			flag = false;
		}
		return flag;
	}

	private void onHttpResponse(MMHttpTask mmhttptask)
	{
		logger.v("onHttpResponse() ---> Enter");
		int i = mmhttptask.getRequest().getReqType();
		byte[] arrayOfByte = mmhttptask.getResponseBody();
		XMLParser xmlparser = new XMLParser(arrayOfByte);
		if ((xmlparser.getRoot() == null)
				|| (xmlparser.getValueByTag("code") == null))
		{
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
			mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(getParent(),
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
			if (!xmlparser.getValueByTag("code").equals("000000"))
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
			mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(getParent(),
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

		switch (i)
		{
		case 1002:
		case 5005:
		default:
		{
			logger.v("onHttpResponse() ---> Exit");
		}
			break;
		case 1000:
		case 5004:
		{
			logger.v("init service");
			GlobalSettingParameter.initServerParam(arrayOfByte);
			this.tabInfoList = xmlparser.getListByTagsAndAttributeID("list",
					"category", "0", "item", TabItem.class);
			if (this.tabInfoList == null)
			{
				mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
						getParent(),
						getText(R.string.fail_to_parse_xml_common),
						getText(R.string.server_data_empty_common),
						new View.OnClickListener()
						{
							public void onClick(View paramAnonymousView)
							{
								mCurrentDialog.dismiss();
							}
						});
			}

			String s = null;
			int k = tabInfoList.size();
			for (int j = 0; j < k; j++)
			{
				if (((TabItem) tabInfoList.get(j)).category_type.equals("0"))
					s = ((TabItem) tabInfoList.get(j)).url;
				createBtListView();
				if (s == null)
				{
					mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
							getParent(),
							getText(R.string.title_information_common),
							getText(R.string.fail_to_parse_xml_common),
							new View.OnClickListener()
							{
								public void onClick(View view)
								{
									mCurrentDialog.dismiss();
								}
							});
				}
			}
			refreshADContentUI(xmlparser);
			requestRecommendContentListData(s);
			mIsInital = true;
		}
			break;
		case 1006:
		case 5009:
		{
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			if (((TabItem) tabInfoList.get(mButtonPosition)).category_type
					.equals("2"))
				refreshInformationContentUI(xmlparser);
			else
				refreshRecommendContentUI(xmlparser);
			mInital = true;
			LOAD_MORE = true;
		}
			break;
		case 1003:
		case 1004:
		case 5006:
		case 5007:
		{
			if (((TabItem) tabInfoList.get(mButtonPosition)).category_type
					.equals("4"))
				refreshAlbumContentUI(xmlparser);
			else
				refreshColumnContentUI(xmlparser);
			mInital = true;
			LOAD_MORE = true;
		}
			break;
		case 1007:
		case 5010:
		{
			refreshNewsInfoUI(mmhttptask);
		}
			break;
		case 1005:
		case 5008:
		{
			onRadioSongsResponse(mmhttptask);
		}
			break;
		}
	}

	private void onRadioSongsResponse(MMHttpTask paramMMHttpTask)
	{
		logger.v("onInitColumnResponse() ---> Enter");
		XMLParser localXMLParser = new XMLParser(
				paramMMHttpTask.getResponseBody());
		List localList = localXMLParser.getListByTag("music", SongItem.class);
		if (localList == null)
		{
			Toast.makeText(this, R.string.radio_no_music, 0).show();
		} else
		{
			String str = localXMLParser.getValueByTag("groupcode");
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
					}
					logger.v("onInitColumnResponse() ---> Exit");
					break;
				}
				SongItem localSongItem = (SongItem) localList.get(j);
				Song localSong = new Song();
				localSong.mAlbum = "<unknown>";
				localSong.mAlbumId = -1;
				localSong.mArtist = localSongItem.singer;
				localSong.mDuration = -1;
				localSong.mId = -1L;
				localSong.mMusicType = MusicType.RADIO.ordinal();
				localSong.mLyric = null;
				localSong.mTrack = localSongItem.songname;
				localSong.mUrl = localSongItem.durl1;
				localSong.mUrl2 = localSongItem.durl2;
				localSong.mLyric = localSongItem.lrc;
				localSong.mArtUrl = localSongItem.img;
				localSong.mContentId = localSongItem.contentid;
				localSong.mGroupCode = str;
				localSong.like = Integer.parseInt(localSongItem.like);
				localArrayList.add(localSong);
			}
		}
	}

	private void onSendHttpRequestFail(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestFail() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
				getParent(), getText(R.string.title_information_common),
				getText(R.string.getfail_data_error_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						OnlineMusicActivity.this.mCurrentDialog.dismiss();
					}
				});
		logger.v("onSendHttpRequestFail() ---> Exit");
	}

	private void onSendHttpRequestTimeOut(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestTimeOut() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
				getParent(), getText(R.string.title_information_common),
				getText(R.string.connect_timeout_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						OnlineMusicActivity.this.mCurrentDialog.dismiss();
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	private void refreshADContentUI(XMLParser paramXMLParser)
	{
		logger.v("init ADImage");
		this.mADContentList = paramXMLParser.getListByTagsAndAttributeID(
				"list", "category", "1", "item", AdListItem.class);
		if (this.mADContentList == null)
		{
			this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
					getParent(), getText(R.string.getfail_data_error_common),
					getText(R.string.server_data_empty_common),
					new View.OnClickListener()
					{
						public void onClick(View paramAnonymousView)
						{
							mCurrentDialog.dismiss();
						}
					});
		} else
		{
			adView.setTotalPage(this.mADContentList.size());
			if ((this.mADContentList != null)
					&& (this.mADContentList.size() > 0))
			{
				for (int i = 0; i < mADContentList.size(); i++)
				{
					adView.addBmpByKey(
							((AdListItem) this.mADContentList.get(i)).img,
							((AdListItem) this.mADContentList.get(i)).groupcode);
				}
			} else
			{
				adView.checkImage();
				adView.resume();
			}
		}
	}

	private void refreshAlbumContentUI(XMLParser xmlparser)
	{
		if (mAlbumAdapter != null)
		{
			mAlbumAdapter.setProgressVisiable(false);
			mAlbumAdapter.notifyDataSetChanged();
		}
		if (mPageTotalCount == -1)
			mPageTotalCount = Integer.parseInt(xmlparser
					.getValueByTag("pagecount"));
		mCurrentPageNo = mTargetPageNo;
		ArrayList arraylist = (ArrayList) xmlparser.getListByTagAndAttribute(
				"item", MusicListColumnItem.class);
		if (arraylist == null)
		{
			mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(getParent(),
					getText(R.string.fail_to_parse_xml_common),
					getText(R.string.server_data_empty_common),
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
		} else
		{
			if (mAlbumItemData != null && mAlbumItemData.size() > 0)
				mAlbumItemData.remove(-1 + mAlbumItemData.size());
			mAlbumItemData.addAll(arraylist);
			if (mCurrentPageNo != mPageTotalCount)
			{
				MusicListColumnItem musiclistcolumnitem = new MusicListColumnItem();
				musiclistcolumnitem.category_type = "";
				musiclistcolumnitem.img = "";
				musiclistcolumnitem.title = "";
				musiclistcolumnitem.url = "";
				mAlbumItemData.add(musiclistcolumnitem);
			}
			if (mAlbumItemData != null && !mAlbumItemData.isEmpty())
				mEmpty.setVisibility(View.GONE);
			if (mAlbumAdapter == null)
			{
				mAlbumAdapter = new MobileMusicAlbumListItemAdapter(this,
						mAlbumItemData);
				mAlbumGridView.setAdapter(mAlbumAdapter);
			}
			mAlbumAdapter.notifyDataSetChanged();
		}
	}

	private void refreshColumnContentUI(XMLParser paramXMLParser)
	{
		List list;
		if (paramXMLParser.getValueByTag("pagecount") != null)
		{
			if (this.mPageTotalCount == -1)
				this.mPageTotalCount = Integer.parseInt(paramXMLParser
						.getValueByTag("pagecount"));
			this.mCurrentPageNo = this.mTargetPageNo;
			this.mReqMoreProgress.setVisibility(View.GONE);
			list = paramXMLParser.getListByTagAndAttribute("item",
					MusicListColumnItem.class);
			if (list == null)
			{
				this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
						getParent(),
						getText(R.string.fail_to_parse_xml_common),
						getText(R.string.server_data_empty_common),
						new View.OnClickListener()
						{
							public void onClick(View paramAnonymousView)
							{
								mCurrentDialog.dismiss();
							}
						});
			} else
			{
				mColumnItemData.addAll(list);
				int i = mColumnContentListView.getFirstVisiblePosition();
				if (mCurrentPageNo != mPageTotalCount
						&& mColumnContentListView.getFooterViewsCount() == 0)
					mColumnContentListView.addFooterView(mLoadMoreView);
				if (mColumnItemData != null && !mColumnItemData.isEmpty())
					mEmpty.setVisibility(View.GONE);
				if (mMobileMusicColumnListItemAdapter == null)
				{
					mMobileMusicColumnListItemAdapter = new MobileMusicColumnListItemAdapter(
							this, mColumnItemData);
					mColumnContentListView
							.setAdapter(mMobileMusicColumnListItemAdapter);
				}
				if (mTargetPageNo == mPageTotalCount
						&& mColumnContentListView.getFooterViewsCount() > 0)
					mColumnContentListView.removeFooterView(mLoadMoreView);
				mMobileMusicColumnListItemAdapter.notifyDataSetChanged();
				if (i > 0)
					mColumnContentListView.setSelectionFromTop(i + 1, 0);
			}
		} else
		{
			mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(getParent(),
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
	}

	private void refreshInformationContentUI(XMLParser paramXMLParser)
	{
		if (mPageTotalCount == -1)
			mPageTotalCount = Integer.parseInt(paramXMLParser
					.getValueByTag("pagecount"));
		mCurrentPageNo = mTargetPageNo;
		if ((mCurrentPageNo != mPageTotalCount)
				&& (mColumnContentListView.getFooterViewsCount() == 0))
			mColumnContentListView.addFooterView(mLoadMoreView);
		mReqMoreProgress.setVisibility(8);
		List localList = paramXMLParser.getListByTagAndAttribute("item",
				ContentItem.class);
		String str = paramXMLParser.getValueByTag("groupcode");
		if (localList == null)
		{
			this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
					getParent(), getText(R.string.fail_to_parse_xml_common),
					getText(R.string.server_data_empty_common),
					new View.OnClickListener()
					{
						public void onClick(View paramAnonymousView)
						{
							mCurrentDialog.dismiss();
						}
					});
		} else
		{
			Iterator localIterator = localList.iterator();
			while (true)
			{
				if (!localIterator.hasNext())
				{
					this.mInfoItemData.addAll(localList);
					int i = this.mColumnContentListView
							.getFirstVisiblePosition();
					if ((this.mCurrentPageNo != this.mPageTotalCount)
							&& (this.mColumnContentListView
									.getFooterViewsCount() == 0))
						this.mColumnContentListView
								.addFooterView(this.mLoadMoreView);
					if ((this.mInfoItemData != null)
							&& (!this.mInfoItemData.isEmpty()))
						this.mEmpty.setVisibility(8);
					this.mMobileMusicContentListItemAdapter = new MobileMusicContentListItemAdapter(
							this, this.mInfoItemData);
					if ((this.mTargetPageNo == this.mPageTotalCount)
							&& (this.mColumnContentListView
									.getFooterViewsCount() > 0))
						this.mColumnContentListView
								.removeFooterView(this.mLoadMoreView);
					this.mColumnContentListView
							.setAdapter(this.mMobileMusicContentListItemAdapter);
					this.mMobileMusicContentListItemAdapter
							.notifyDataSetChanged();
					if (i > 0)
						this.mColumnContentListView.setSelectionFromTop(i + 1,
								0);
					break;
				}
				ContentItem localContentItem = (ContentItem) localIterator
						.next();
				localContentItem.groupcode = str;
				localContentItem.current_page = this.mCurrentPageNo;
			}
		}
	}

	private void refreshNewsInfoUI(MMHttpTask paramMMHttpTask)
	{
		String str = new String(paramMMHttpTask.getResponseBody());
		Intent localIntent = new Intent(this,
				MusicOnlineInformationDetailActivity.class);
		localIntent
				.putExtra("cmccwm.mobilemusic.mmhttpdefines.xmlRawData", str);
		localIntent.putParcelableArrayListExtra(
				"cmccwm.mobilemusic.onlinemusicinformationactivity.allnews",
				this.mInfoItemData);
		localIntent.putExtra(
				"cmccwm.mobilemusic.onlinemusicinformationactivity.currentpos",
				this.mCurShortClickSelectedItem);
		localIntent
				.putExtra(
						"cmccwm.mobilemusic.onlinemusicinformationactivity.pagetotalcount",
						this.mPageTotalCount);
		localIntent
				.putExtra(
						"cmccwm.mobilemusic.onlinemusicinformationactivity.currentpage",
						this.mCurrentPageNo);
		localIntent.putExtra("url",
				((TabItem) this.tabInfoList.get(this.mButtonPosition)).url);
		startActivity(localIntent);
	}

	private void refreshRecommendContentUI(XMLParser xmlparser)
	{
		logger.v("refreshRecommendContentUI() ---> Enter");
		List list = xmlparser.getListByTagAndAttribute("item",
				SongListItem.class);
		String s = xmlparser.getValueByTag("groupcode");
		Iterator iterator = list.iterator();
		do
		{
			if (!iterator.hasNext())
			{
				if (mPageTotalCount == -1)
					mPageTotalCount = Integer.parseInt(xmlparser
							.getValueByTag("pagecount"));
				mCurrentPageNo = mTargetPageNo;
				if (mCurrentPageNo != mPageTotalCount
						&& mRecommendContentListView.getFooterViewsCount() == 0)
					mRecommendContentListView.addFooterView(mLoadMoreView);
				mRecommendSongItemData.addAll(list);
				if (mRecommendSongItemData != null
						&& !mRecommendSongItemData.isEmpty())
					mEmpty.setVisibility(View.GONE);
				if (mMobileMusicRecommendListItemAdapter == null
						&& mRecommendSongItemData != null)
				{
					mMobileMusicRecommendListItemAdapter = new MobileMusicRecommendListItemAdapter(
							this, mRecommendSongItemData);
					mRecommendContentListView
							.setAdapter(mMobileMusicRecommendListItemAdapter);
				}
				if (mTargetPageNo == mPageTotalCount
						&& mRecommendContentListView.getFooterViewsCount() > 0)
					mRecommendContentListView.removeFooterView(mLoadMoreView);
				if (mListButtonClickListener != null)
					mListButtonClickListener
							.setListData(mRecommendSongItemData);
				else
					mListButtonClickListener = new ListButtonClickListener(
							this, mRecommendSongItemData);
				mMobileMusicRecommendListItemAdapter
						.setBtnOnClickListener(mListButtonClickListener);
				mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
				if (!MobileMusicApplication.getShowMusicSelectedToast())
				{
					MobileMusicApplication.setShowMusicSelectedToast(true);
					if (NetUtil.isNetStateWLAN())
						Toast.makeText(this,
								getText(R.string.music_select_high), 1).show();
					else if (NetUtil.isConnection())
						Toast.makeText(this,
								getText(R.string.music_select_low), 1).show();
				}
				if (GlobalSettingParameter.isCheckOlderMusicVersion)
				{
					checkOlderMusicVersion();
					GlobalSettingParameter.isCheckOlderMusicVersion = false;
				}
				logger.v("refreshRecommendContentUI() ---> Exit");
				return;
			}
			((SongListItem) iterator.next()).groupcode = s;
		} while (true);
	}

	private void requestAlbumGpoup(String s)
	{
		logger.v("requestAlbumGpoup() ---> Enter");
		if (mCurrentTask == null && !mInital)
		{
			CancelPreviousReq();
			if (mPageTotalCount == -1)
				showInitingDialog();
			char c;
			MMHttpRequest mmhttprequest;
			String s1;
			String s2;
			if (NetUtil.isNetStateWap())
				c = '\u03EB';
			else
				c = '\u138E';
			mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
			mmhttprequest.addUrlParams("itemcount",
					GlobalSettingParameter.SERVER_INIT_PARAM_ITEM_COUNT);
			mmhttprequest.addUrlParams("pageno", String.valueOf(mTargetPageNo));
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
			mCurrentTask = mHttpController.sendRequest(mmhttprequest, true);
		}
		logger.v("requestAlbumGpoup() ---> Exit");
	}

	private void requestColumnGpoup(String s)
	{
		logger.v("requestGpoup() ---> Enter");
		if (mCurrentTask == null && !mInital)
		{
			CancelPreviousReq();
			if (mPageTotalCount == -1)
				showInitingDialog();
			char c;
			MMHttpRequest mmhttprequest;
			String s1;
			String s2;
			if (NetUtil.isNetStateWap())
				c = '\u03EB';
			else
				c = '\u138E';
			mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
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
			mmhttprequest.addUrlParams("pageno", String.valueOf(mTargetPageNo));
			mCurrentTask = mHttpController.sendRequest(mmhttprequest, true);
		}
		logger.v("requestGpoup() ---> Exit");
	}

	private void requestInfoGroup(String s)
	{
		if (mCurrentTask == null && !mInital)
		{
			CancelPreviousReq();
			if (mPageTotalCount == -1)
				showInitingDialog();
			char c;
			MMHttpRequest mmhttprequest;
			String s1;
			String s2;
			if (NetUtil.isNetStateWap())
				c = '\u03EE';
			else
				c = '\u1391';
			mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
			s1 = s.substring(s.indexOf("rdp2"));
			if (NetUtil.isNetStateWap())
				s2 = (new StringBuilder(
						String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_IP)))
						.append(s1).toString();
			else
				s2 = (new StringBuilder(
						String.valueOf(MusicBusinessDefine_Net.NET_HOST_IP)))
						.append(s1).toString();
			mmhttprequest.addUrlParams("pageno", String.valueOf(mTargetPageNo));
			mmhttprequest.addUrlParams("itemcount",
					GlobalSettingParameter.SERVER_INIT_PARAM_ITEM_COUNT);
			mmhttprequest.setURL(s2);
			mCurrentTask = mHttpController.sendRequest(mmhttprequest, true);
		}
	}

	private void requestRadioSongList(int i)
	{
		logger.v("requestRadioSongList() ---> Enter");
		CancelPreviousReq();
		char c;
		MMHttpRequest mmhttprequest;
		String s;
		String s1;
		String s2;
		if (NetUtil.isNetStateWap())
			c = '\u03ED';
		else
			c = '\u1390';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		s = ((MusicListColumnItem) mColumnItemData.get(i)).url;
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
		mmhttprequest.addUrlParams("itemcount",
				GlobalSettingParameter.SERVER_INIT_PARAM_ITEM_COUNT);
		mmhttprequest.addUrlParams("pageno", "1");
		mCurrentTask = mHttpController.sendRequest(mmhttprequest);
		if (mCurrentDialog != null)
		{
			mCurrentDialog.dismiss();
			mCurrentDialog = null;
		}
		mCurrentDialog = DialogUtil.show1BtnProgressDialog(this,
				R.string.loading_radio_list_activity, R.string.cancel,
				new android.view.View.OnClickListener()
				{
					public void onClick(View view)
					{
						cancleDialog();
					}
				});
		logger.v("requestRadioSongList() ---> Exit");
	}

	private void requestRecommendContentListData(String s)
	{
		logger.v("requestRecommendContentListData(String) ---> Enter");
		CancelPreviousReq();
		if (mPageTotalCount == -1)
			showInitingDialog();
		char c;
		MMHttpRequest mmhttprequest;
		String s1;
		String s2;
		if (NetUtil.isNetStateWap())
			c = '\u03EE';
		else
			c = '\u1391';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
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
		mmhttprequest.addUrlParams("pageno", String.valueOf(mTargetPageNo));
		mmhttprequest.addUrlParams("itemcount",
				GlobalSettingParameter.SERVER_INIT_PARAM_ITEM_COUNT);
		mCurrentTask = mHttpController.sendRequest(mmhttprequest, true);
		logger.v("requestRecommendContentListData(String) ---> Exit");
	}

	/**
	 * 请求服务
	 */
	private void requestService()
	{
		logger.v("requestService() ---> Enter");
		CancelPreviousReq();
		if (mPageTotalCount == -1)
			showInitingDialog();
		char c;
		MMHttpRequest mmhttprequest;
		if (NetUtil.isNetStateWap())
			c = '\u03E8';
		else
			c = '\u138C';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		mCurrentTask = mHttpController.sendRequest(mmhttprequest, true);
		logger.v("requestService() ---> Exit");
	}

	/**
	 * 强制软件更新对话框，要不就关闭应用
	 */
	private void showEnforceUpdateDialog()
	{
		logger.v("showEnforceUpdateDialog() ---> Enter");
		this.mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(this,
				getText(R.string.update_optional_title_entry_list_activity),
				GlobalSettingParameter.SERVER_INIT_PARAM_UPDATE_INFO,
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						// mCurrentDialog.dismiss();
						// Intent localIntent = new Intent(
						// OnlineMusicActivity.this,
						// MobileMusicUpdateActivity.class);
						// localIntent.putExtra("come_form", true);
						// startActivity(localIntent);
					}
				}, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						Util.exitMobileMusicApp(false);
					}
				});
		logger.v("showEnforceUpdateDialog() ---> Exit");
	}

	private void showInitingDialog()
	{
		logger.v("showInitingDialog() ---> Enter");
		if (this.mCurrentDialog != null)
		{
			this.mCurrentDialog.dismiss();
			this.mCurrentDialog = null;
		}
		if (this.mColumnContentListView.getFooterViewsCount() > 0)
			this.mColumnContentListView.removeFooterView(this.mLoadMoreView);
		this.mRetryLayout.setVisibility(View.GONE);
		if (!this.mEmpty.isShown())
			this.mEmpty.setVisibility(View.VISIBLE);
		this.mCurrentDialog = DialogUtil.show1BtnProgressDialog(this,
				R.string.loading, R.string.cancel, new View.OnClickListener()
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
							mEmpty.setVisibility(8);
							((Button) mRetryLayout
									.findViewById(R.id.refresh_temp_btn))
									.setOnClickListener(new View.OnClickListener()
									{
										public void onClick(
												View paramAnonymous2View)
										{
											mRetryLayout.setVisibility(8);
											mEmpty.setVisibility(0);
											mPageTotalCount = -1;
											mCurrentPageNo = 1;
											mTargetPageNo = 1;
											if ((tabInfoList == null)
													|| (tabInfoList.isEmpty()))
											{
												requestService();
											} else
											{
												String str = ((TabItem) tabInfoList
														.get(mButtonPosition)).url;
												switch (Integer
														.parseInt(((TabItem) tabInfoList
																.get(mButtonPosition)).category_type))
												{
												case 3: // '\003'
												case 4: // '\004'
												default:
													requestColumnGpoup(str);
													break;
												case 0: // '\0'
													requestRecommendContentListData(str);
													break;
												case 1: // '\001'
												case 5: // '\005'
													requestColumnGpoup(str);
													break;
												case 2: // '\002'
													requestInfoGroup(str);
													break;
												}
											}
										}
									});
						}
					}
				});
		logger.v("showInitingDialog() ---> Exit");
	}

	/**
	 * 显示更新对话框，普通的
	 */
	private void showUpdateDialog()
	{
		logger.v("showUpdateDialog() ---> Enter");
		this.mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(this,
				getText(R.string.update_optional_title_entry_list_activity),
				GlobalSettingParameter.SERVER_INIT_PARAM_UPDATE_INFO,
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						// mCurrentDialog.dismiss();
						// Intent localIntent = new Intent(
						// OnlineMusicActivity.this,
						// MusicUpdateActivity.class);
						// localIntent.putExtra("come_form", true);
						// startActivity(localIntent);
					}
				}, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						mCurrentDialog.dismiss();
					}
				});
		logger.v("showUpdateDialog() ---> Exit");
	}

	/**
	 * 取消上一次的请求
	 */
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
		boolean flag = true;
		switch (motionevent.getAction())
		{
		case 1:
		default:
		{
			flag = super.dispatchTouchEvent(motionevent);
		}
			break;
		case 0:
		{
			if (mListButtonClickListener == null
					|| !mListButtonClickListener.closePopupWindow())
			{
				flag = super.dispatchTouchEvent(motionevent);
			}
		}
			break;
		case 2:
		{
			if (!headerMove)
			{
				flag = super.dispatchTouchEvent(motionevent);
			} else
			{
				adView.onTouchEvent(motionevent);
			}
		}
			break;
		}
		return flag;
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
		} else
		{
			if ((this.mRetryLayout != null)
					&& (this.mRetryLayout.getVisibility() != View.GONE))
				this.mRetryLayout.setVisibility(View.GONE);
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
			if (this.mReqMoreProgress != null)
				this.mReqMoreProgress.setVisibility(View.GONE);
			if (this.mCurrentTask != null)
				this.mCurrentTask = null;
			int i = localMMHttpTask.getRequest().getReqType();
			switch (paramMessage.what)
			{
			case DispatcherEventEnum.HTTP_EVENT_TASK_CANCELED:
			default:
				if (this.mCurrentDialog != null)
				{
					this.mCurrentDialog.dismiss();
					this.mCurrentDialog = null;
				}
				break;
			case DispatcherEventEnum.HTTP_EVENT_TASK_COMPLETE:
			{
				Activity activity;
				if (getParent() == null)
				{
					activity = this;
				} else
				{
					activity = getParent();
				}
				Uiutil.ifSwitchToWapDialog((Context) activity);
				this.LOAD_MORE = true;
				if (((i == 1000) || (i == 5004) || (i == 1036) || (i == 5039))
						&& ((paramMessage.what == DispatcherEventEnum.HTTP_EVENT_TASK_FAIL) || (paramMessage.what == DispatcherEventEnum.HTTP_EVENT_TASK_TIMEOUT)))
				{
					this.mRetryLayout.setVisibility(0);
					this.mEmpty.setVisibility(8);
					((Button) this.mRetryLayout
							.findViewById(R.id.refresh_temp_btn))
							.setOnClickListener(new View.OnClickListener()
							{
								public void onClick(View paramAnonymousView)
								{
									mRetryLayout.setVisibility(8);
									requestService();
								}
							});
				}
				mCurShortClickSelectedItem = -1;
				logger.v("handleMMHttpEvent() ---> Exit");
			}
				break;
			case DispatcherEventEnum.HTTP_EVENT_TASK_FAIL:
				onSendHttpRequestFail(localMMHttpTask);
				if (((i == 1000) || (i == 5004) || (i == 1036) || (i == 5039))
						&& ((paramMessage.what == 3004) || (paramMessage.what == 3006)))
				{
					this.mRetryLayout.setVisibility(0);
					this.mEmpty.setVisibility(8);
					((Button) this.mRetryLayout
							.findViewById(R.id.refresh_temp_btn))
							.setOnClickListener(new View.OnClickListener()
							{
								public void onClick(View paramAnonymousView)
								{
									mRetryLayout.setVisibility(8);
									requestService();
								}
							});
				}
				this.mCurShortClickSelectedItem = -1;
				logger.v("handleMMHttpEvent() ---> Exit");
			case DispatcherEventEnum.HTTP_EVENT_TASK_TIMEOUT:
			{
				onSendHttpRequestTimeOut(localMMHttpTask);
				if (((i == 1000) || (i == 5004) || (i == 1036) || (i == 5039))
						&& ((paramMessage.what == 3004) || (paramMessage.what == 3006)))
				{
					this.mRetryLayout.setVisibility(0);
					this.mEmpty.setVisibility(8);
					((Button) this.mRetryLayout
							.findViewById(R.id.refresh_temp_btn))
							.setOnClickListener(new View.OnClickListener()
							{
								public void onClick(View paramAnonymousView)
								{
									mRetryLayout.setVisibility(8);
									requestService();
								}
							});
				}
				mCurShortClickSelectedItem = -1;
				logger.v("handleMMHttpEvent() ---> Exit");
			}
				break;
			case DispatcherEventEnum.HTTP_EVENT_WAP_CLOSED:
			case DispatcherEventEnum.WLAN_EVENT_WLAN_CLOSE:
				break;
			}
		}
	}

	public void handlePlayerEvent(Message paramMessage)
	{
		logger.v("handlePlayerEvent() ---> Enter " + paramMessage.what);
		switch (paramMessage.what)
		{
		default:
			logger.v("handlePlayerEvent() ---> Exit");
			return;
		case DispatcherEventEnum.PLAYER_EVENT_TRACK_ENDED:
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_START:
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_PAUSE:
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_STOP:
			if (mMobileMusicRecommendListItemAdapter != null)
				mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
			break;
		case DispatcherEventEnum.PLAYER_EVENT_WAP_CLOSED:
			Uiutil.ifSwitchToWapDialog(getParent());
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
			logger.v("handleSystemEvent() ---> Exit");
			finish();
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
		case DispatcherEventEnum.UI_EVENT_UPDATECACHE:
		{
			if (tabInfoList != null && tabInfoList.size() > 0)
				tabInfoList.clear();
			requestService();
		}
			break;
		case DispatcherEventEnum.UI_EVENT_PLAY_NEWSONG:
		{
			if (mMobileMusicRecommendListItemAdapter != null)
				mMobileMusicRecommendListItemAdapter.notifyDataSetChanged();
		}
			break;
		case DispatcherEventEnum.UI_EVENT_NEED_UPDATE: // 软件更新
		{
			if (GlobalSettingParameter.SERVER_INIT_PARAM_NEED_UPDATE
					.equals("1"))
				showUpdateDialog();
			else if (GlobalSettingParameter.SERVER_INIT_PARAM_NEED_UPDATE
					.equals("2"))
				showEnforceUpdateDialog();
		}
			break;
		}
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_online_music_temp_layout);
		mController = Controller
				.getInstance((MobileMusicApplication) getApplication());
		mHttpController = mController.getHttpController();
		mPlayerController = mController.getPlayerController();
		mEmpty = ((ImageView) findViewById(R.id.empty_temp));
		mSearchBtn = ((Button) findViewById(R.id.search_button));
		mSearchBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				// Intent localIntent = new Intent(this,
				// MusicOnlineMusicSearchActivity.class);
				// startActivity(localIntent);
			}
		});
		mHeadView = getLayoutInflater().inflate(
				R.layout.list_head_view_for_recommend_layout, null);
		adView = ((AdView) mHeadView.findViewById(R.id.adView));
		adView.setOnTouchListener(new View.OnTouchListener()
		{
			long downTime = 0L;
			float downX;
			float downY;

			public boolean onTouch(View paramAnonymousView,
					MotionEvent paramAnonymousMotionEvent)
			{
				if (paramAnonymousMotionEvent.getAction() == 0)
				{
					headerMove = true;
					if ((downTime == 0L)
							|| (paramAnonymousMotionEvent.getEventTime()
									- downTime > 500L))
					{
						downX = paramAnonymousMotionEvent.getX();
						downY = paramAnonymousMotionEvent.getY();
						downTime = paramAnonymousMotionEvent.getDownTime();
					}
				} else
				{
					if ((paramAnonymousMotionEvent.getAction() == 1)
							|| (paramAnonymousMotionEvent.getAction() == 3))
					{
						headerMove = false;
						if ((paramAnonymousMotionEvent.getAction() == 1)
								&& (Math.abs(paramAnonymousMotionEvent.getX()
										- this.downX) < 10.0F)
								&& (Math.abs(paramAnonymousMotionEvent.getY()
										- this.downY) < 10.0F)
								&& (paramAnonymousMotionEvent.getEventTime()
										- this.downTime < 300L))
							adViewClick(adView.getCurrentIndex());
					}
				}
				adView.onTouchEvent(paramAnonymousMotionEvent);
				return true;
			}
		});
		mRecommendContentListView = ((ListView) findViewById(R.id.recommned_list_temp));
		mRecommendContentListView.addHeaderView(mHeadView);
		mRecommendContentListView
				.setOnItemClickListener(mListItemClickListener);
		mRecommendContentListView.setOnScrollListener(mScrollListener);
		mColumnContentListView = ((ListView) findViewById(R.id.column_list_temp));
		mColumnContentListView.setOnItemClickListener(mListItemClickListener);
		mColumnContentListView.setOnScrollListener(mScrollListener);
		mAlbumGridView = ((GridView) findViewById(R.id.album_list_temp));
		mAlbumGridView.setOnItemClickListener(mAlbumItemClickListener);
		mAlbumGridView.setOnScrollListener(mScrollListener);
		mRetryLayout = ((LinearLayout) findViewById(R.id.refresh_temp_layout));
		mController.addHttpEventListener(
				DispatcherEventEnum.HTTP_EVENT_TASK_COMPLETE, this);
		mController.addHttpEventListener(
				DispatcherEventEnum.HTTP_EVENT_TASK_CANCELED, this);
		mController.addHttpEventListener(
				DispatcherEventEnum.HTTP_EVENT_TASK_FAIL, this);
		mController.addHttpEventListener(
				DispatcherEventEnum.HTTP_EVENT_TASK_TIMEOUT, this);
		mController.addHttpEventListener(
				DispatcherEventEnum.HTTP_EVENT_WAP_CLOSED, this);
		mController.addHttpEventListener(
				DispatcherEventEnum.WLAN_EVENT_WLAN_CLOSE, this);
		mLoadMoreView = getLayoutInflater().inflate(
				R.layout.load_more_list_footer_view, null);
		mReqMoreProgress = ((ProgressBar) mLoadMoreView
				.findViewById(R.id.progressbar));
		mLoadMoreView.setOnClickListener(mLoadMoreOnClickListener);
		mController.addSystemEventListener(
				DispatcherEventEnum.SYTEM_EVENT_FINISH_ALL_ACTIVITIES, this);
		mController.addUIEventListener(
				DispatcherEventEnum.UI_EVENT_UPDATECACHE, this);
		mController.addUIEventListener(
				DispatcherEventEnum.UI_EVENT_PLAY_NEWSONG, this);
		mController.addUIEventListener(
				DispatcherEventEnum.UI_EVENT_NEED_UPDATE, this);
		mController.addPlayerEventListener(
				DispatcherEventEnum.PLAYER_EVENT_TRACK_ENDED, this);
		mController.addPlayerEventListener(
				DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_START, this);
		mController.addPlayerEventListener(
				DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_STOP, this);
		mController.addPlayerEventListener(
				DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_PAUSE, this);
		mController.addPlayerEventListener(
				DispatcherEventEnum.PLAYER_EVENT_WAP_CLOSED, this);
	}

	public boolean onCreateOptionsMenu(Menu paramMenu)
	{
		logger.v("onCreateOptionsMenu() ---> Enter");
		getMenuInflater().inflate(R.menu.online_music_main_ui_option_menu,
				paramMenu);
		logger.v("onCreateOptionsMenu() ---> Exit");
		return super.onCreateOptionsMenu(paramMenu);
	}

	protected void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		super.onDestroy();
		this.mController.removeHttpEventListener(3003, this);
		this.mController.removeHttpEventListener(3005, this);
		this.mController.removeHttpEventListener(3004, this);
		this.mController.removeHttpEventListener(3006, this);
		this.mController.removeHttpEventListener(3007, this);
		this.mController.removeHttpEventListener(3008, this);
		this.mController.removeSystemEventListener(22, this);
		this.mController.removeUIEventListener(4013, this);
		this.mController.removeUIEventListener(4008, this);
		this.mController.removeUIEventListener(4022, this);
		this.mController.removePlayerEventListener(1002, this);
		this.mController.removePlayerEventListener(1010, this);
		this.mController.removePlayerEventListener(1012, this);
		this.mController.removePlayerEventListener(1011, this);
		this.mController.removePlayerEventListener(1014, this);
		if (adView != null)
		{
			adView.pause();
			adView.releaseResource();
		}
		logger.v("onDestroy() ---> Exit");
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem)
	{
		boolean bool = true;
		logger.v("onOptionsItemSelected() ---> Enter");
		switch (paramMenuItem.getItemId())
		{
		default:
			logger.v("onOptionsItemSelected() ---> Exit");
			bool = super.onOptionsItemSelected(paramMenuItem);
		case R.id.menu_item_set:
			// startActivity(new Intent(this, MobileMusicUpdateActivity.class));
			break;
		case R.id.menu_item_time_close:
			// startActivity(new Intent(this, MobileMusicMoreActivity.class));
			break;
		case R.id.menu_item_update:
			// startActivity(new Intent(this,
			// MobileMusicFeedBackActivity.class));
			break;
		case R.id.menu_item_feed_back:
			// startActivity(new Intent(this, TimingClosureActivity.class));
			break;
		case R.id.menu_item_exit:
			exitApplication();
			break;
		}
		return bool;
	}

	protected void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		MobclickAgent.onPause(this);
		if (mListButtonClickListener != null)
			mListButtonClickListener.closePopupWindow();
		if (adView != null)
			adView.pause();
		CancelPreviousReq();
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ----> Enter");
		super.onResume();
		MobclickAgent.onResume(this);
		if ((this.mCurrentTask == null) && (!this.mIsInital))
		{
			logger.v("CurrentTask == null && mIsInital == false");
			requestService();
		} else
		{
			if (mPlayerLoveRadio
					&& (GlobalSettingParameter.useraccount != null))
				requestRadioSongList(0);
		}
		if (adView != null)
			adView.resume();
		mPlayerLoveRadio = false;
		if (mListButtonClickListener != null)
			mListButtonClickListener.doUnCompleteTask();
		logger.v("onResume() ----> Exit");
	}
}