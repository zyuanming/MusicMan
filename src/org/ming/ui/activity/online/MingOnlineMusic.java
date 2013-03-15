package org.ming.ui.activity.online;

import java.util.ArrayList;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.item.MusicListColumnItem;
import org.ming.center.http.item.TabItem;
import org.ming.center.player.PlayerController;
import org.ming.center.ui.UIEventListener;
import org.ming.dispatcher.Dispatcher;
import org.ming.dispatcher.DispatcherEventEnum;
import org.ming.ui.activity.more.MobileMusicFeedBackActivity;
import org.ming.ui.activity.more.MobileMusicMoreActivity;
import org.ming.ui.activity.more.TimingClosureActivity;
import org.ming.ui.adapter.MobileMusicAlbumListItemAdapter;
import org.ming.ui.adapter.MobileMusicColumnListItemAdapter;
import org.ming.ui.adapter.MobileMusicContentListItemAdapter;
import org.ming.ui.adapter.MobileMusicRecommendListItemAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.SongOnlineManager;
import org.ming.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cm.omp.fuction.data.ChartInfo;

/**
 * 在线音乐的主界面，包括4个子界面，可以相互切换
 * 
 * @author lkh
 * 
 */
public class MingOnlineMusic extends Activity implements UIEventListener,
		MMHttpEventListener
{
	private static final int GET_INFO_SUCC = 0;// 获取信息成功
	private static final int GET_INFO_FAIL = 1;// 获取信息失败
	private static final int GET_MORE_INFO_FAIL = 2;// 获取信息失败
	private static final int GET_INFO_NETERROR = 3;// 网络连接失败
	private static final int SIM_IS_CAN = 4;// 判断sim卡是否可用
	private List<ChartInfo> mChartInfos = null;

	private static boolean isNetWorkConnected = false;
	private HttpController mHttpController;
	private Controller mController;
	private PlayerController mPlayerController;
	private static final MyLogger logger = MyLogger
			.getLogger("MingOnlineMusic");
	private Dialog mCurrentDialog = null;
	private SharedPreferences isInitialedPreferences;
	SongOnlineManager songOnlineManager;
	private boolean isInit;
	private int mButtonPosition = 0;
	private ImageView mEmpty = null;
	private int mPageTotalCount = -1;
	private int mCurrentPageNo = 1;
	private int mTargetPageNo = 1;
	private MobileMusicRecommendListItemAdapter mMobileMusicRecommendListItemAdapter = null;
	private MobileMusicColumnListItemAdapter mMobileMusicColumnListItemAdapter = null;
	private MobileMusicContentListItemAdapter mMobileMusicContentListItemAdapter = null;
	private MobileMusicAlbumListItemAdapter mAlbumAdapter;
	private ListView mRecommendContentListView;
	private ListView mColumnContentListView;
	private GridView mAlbumGridView;
	private List<TabItem> tabInfoList;
	private static final int RING_SONG_WEEK_BANG = SongOnlineManager.RING_SONG_WEEK_BANG;// 周热销榜
	private static final int RING_SONG_MOUTH_BANG = SongOnlineManager.RING_SONG_MOUTH_BANG;// 月热销榜
	private static final int RING_SONG_TOTAL_BNAG = SongOnlineManager.RING_SONG_TOTAL_BNAG;// 总热销榜
	private boolean isTotalOver = false;
	private boolean isTotalThreadStart = false;
	private ArrayList<ChartInfo> chartInfos = null;
	private Dispatcher mDispatcher;
	private LinearLayout mRetryLayout;
	private View mLoadMoreView;
	private ProgressBar mReqMoreProgress;
	private ArrayList<MusicListColumnItem> mColumnItemData = new ArrayList();

	private int curBangType = RING_SONG_WEEK_BANG;
	String appid = "";

	/**
	 * 列表的点击
	 */
	private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> adapterview, View view, int i,
				long l)
		{
			logger.v("onListItemClick() ---> Enter");
			if ((mButtonPosition < tabInfoList.size())
					|| (mButtonPosition >= 0))
			{
				logger.v("mButtonPosition ---- " + mButtonPosition);
				logger.v("i ---- " + i);
				switch (Integer.parseInt(((TabItem) tabInfoList
						.get(mButtonPosition)).category_type))
				{
				case 1:
					// 榜单的列表点击,获取榜单的歌曲信息
					int j = i - mColumnContentListView.getHeaderViewsCount();
					logger.v("j ---- " + j);
					if (j < mColumnItemData.size() && j >= 0)
					{
						MusicListColumnItem musiclistcolumnitem = (MusicListColumnItem) mColumnItemData
								.get(j);
						Intent intent = new Intent(
								MingOnlineMusic.this,
								org.ming.ui.activity.online.MusicOnlineMusicColumnDetailActivity.class);
						intent.putExtra("title", musiclistcolumnitem.title);
						intent.putExtra("COLUMITEM", musiclistcolumnitem);
						startActivity(intent);
						logger.v("onListItemClick() ---> out");
					}
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				default:
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ----> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		mController = Controller
				.getInstance((MobileMusicApplication) getApplication());
		setContentView(R.layout.activity_online_music_temp_layout);
		mController.addUIEventListener(DispatcherEventEnum.UI_INITIAL_SUCCESS,
				MingOnlineMusic.this);
		mController.addUIEventListener(DispatcherEventEnum.UI_INITIAL_FAIL,
				MingOnlineMusic.this);
		mController
				.addHttpEventListener(
						DispatcherEventEnum.Http_UpdateData_Begin,
						MingOnlineMusic.this);
		mHttpController = mController.getHttpController();
		mDispatcher = MobileMusicApplication.getInstance().getEventDispatcher();
		mPlayerController = mController.getPlayerController();
		songOnlineManager = SongOnlineManager.getInstance();
		mRetryLayout = ((LinearLayout) findViewById(R.id.refresh_temp_layout));
		mEmpty = ((ImageView) findViewById(R.id.empty_temp));
		mRecommendContentListView = ((ListView) findViewById(R.id.recommned_list_temp));
		// mRecommendContentListView.addHeaderView(mHeadView);
		// mRecommendContentListView
		// .setOnItemClickListener(mListItemClickListener);
		// mRecommendContentListView.setOnScrollListener(mScrollListener);

		// 榜单列表的点击
		mColumnContentListView = ((ListView) findViewById(R.id.column_list_temp));
		mColumnContentListView.setOnItemClickListener(mListItemClickListener);
		// mColumnContentListView.setOnScrollListener(mScrollListener);
		mAlbumGridView = ((GridView) findViewById(R.id.album_list_temp));
		// mAlbumGridView.setOnItemClickListener(mAlbumItemClickListener);
		// mAlbumGridView.setOnScrollListener(mScrollListener);

		mLoadMoreView = getLayoutInflater().inflate(
				R.layout.load_more_list_footer_view, null);
		mReqMoreProgress = ((ProgressBar) mLoadMoreView
				.findViewById(R.id.progressbar));
		mLoadMoreView.setOnClickListener(mLoadMoreOnClickListener);
		InitTabInfoList();
		createBtListView();
		this.isInitialedPreferences = getSharedPreferences("InitTheAppid",
				MODE_WORLD_WRITEABLE);
		this.isInit = isInitialedPreferences.getBoolean("InitTheAppid", false);
		logger.v("onCreate() ----> Exit");
	}

	@Override
	protected void onResume()
	{
		logger.v("onResume() ----> Enter");
		super.onResume();
		if (NetUtil.isConnection())
		{
			isNetWorkConnected = true;
		} else
		{
			isNetWorkConnected = false;
		}
		if (isNetWorkConnected)
		{
			refresh();
		} else
		{
			Toast.makeText(this, "网络连接有问题，请检查网络", Toast.LENGTH_SHORT).show();
		}
		logger.v("onResume() ----> Exit");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		super.onDestroy();
		mController.removeUIEventListener(DispatcherEventEnum.UI_INITIAL_FAIL,
				this);
		mController.removeUIEventListener(
				DispatcherEventEnum.UI_INITIAL_SUCCESS, this);
		mController.removeHttpEventListener(
				DispatcherEventEnum.Http_UpdateData_Begin, this);
	}

	@Override
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
			startActivity(new Intent(this, MobileMusicMoreActivity.class));
			break;
		case R.id.menu_item_time_close:
			startActivity(new Intent(this, TimingClosureActivity.class));
			break;
		case R.id.menu_item_update:
			// startActivity(new Intent(this, MobileMusicUpdateActivity.class));
			break;
		case R.id.menu_item_feed_back:

			startActivity(new Intent(this, MobileMusicFeedBackActivity.class));
			break;
		case R.id.menu_item_exit:
			exitApplication();
			break;
		}
		return bool;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu paramMenu)
	{
		logger.v("onCreateOptionsMenu() ---> Enter");
		getMenuInflater().inflate(R.menu.online_music_main_ui_option_menu,
				paramMenu);
		logger.v("onCreateOptionsMenu() ---> Exit");
		return super.onCreateOptionsMenu(paramMenu);
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

	/**
	 * 显示4个子标签，横排滑动的
	 */
	private void createBtListView()
	{
		final ArrayList<Button> localArrayList = new ArrayList<Button>();
		Button button;
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.tabhost_layout);
		int i;
		for (i = 0; i < tabInfoList.size(); i++)
		{
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
			localArrayList.add(localButton);
		}

		for (int j = 0; j < localArrayList.size(); j++)
		{
			button = (Button) localArrayList.get(j);
			button.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View paramAnonymousView)
				{
					logger.v("onClick() ----> Enter");
					logger.v("the Position of Click "
							+ ((Integer) paramAnonymousView.getTag())
									.intValue());
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
					((View) localArrayList.get(((Integer) paramAnonymousView
							.getTag()).intValue()))
							.setBackgroundResource(R.drawable.main_column_navigation_lable_hl);
					((View) localArrayList.get(((Integer) paramAnonymousView
							.getTag()).intValue())).setSelected(true);
					((Button) localArrayList.get(((Integer) paramAnonymousView
							.getTag()).intValue())).setTextColor(-1);
					if (mButtonPosition != ((Integer) paramAnonymousView
							.getTag()).intValue())
					{
						System.gc();
						mEmpty.setVisibility(View.VISIBLE);
						mPageTotalCount = -1;
						mCurrentPageNo = 1;
						mTargetPageNo = 1;
						if (mMobileMusicRecommendListItemAdapter != null)
						{
							mMobileMusicRecommendListItemAdapter
									.releaseAdapterResource();
							mMobileMusicRecommendListItemAdapter = null;
						}
						if (mMobileMusicColumnListItemAdapter != null)
						{
							mMobileMusicColumnListItemAdapter
									.releaseAdapterResource();
							mMobileMusicColumnListItemAdapter = null;
						}
						if (mAlbumAdapter != null)
						{
							mAlbumAdapter.releaseAdapterResource();
							mAlbumAdapter = null;
						}
						if (mMobileMusicContentListItemAdapter != null)
						{
							mMobileMusicContentListItemAdapter
									.releaseAdapterResource();
							mMobileMusicContentListItemAdapter = null;
						}
						mButtonPosition = ((Integer) paramAnonymousView
								.getTag()).intValue();
						str = ((TabItem) tabInfoList.get(mButtonPosition)).url;
						switch (Integer.parseInt(((TabItem) tabInfoList
								.get(mButtonPosition)).category_type))
						{
						default:
							return;
						case 1:
							mRecommendContentListView.setVisibility(View.GONE);
							mColumnContentListView.setVisibility(View.VISIBLE);
							mAlbumGridView.setVisibility(View.GONE);
							refreshColumnContentUI();
							return;
						case 2:
						case 3:
							mRecommendContentListView.setVisibility(View.GONE);
							mColumnContentListView.setVisibility(View.VISIBLE);
							mAlbumGridView.setVisibility(View.GONE);
							// requestColumnGpoup(str);
							return;
						case 4:
							mRecommendContentListView.setVisibility(View.GONE);
							mColumnContentListView.setVisibility(View.VISIBLE);
							mAlbumGridView.setVisibility(View.GONE);
							// requestColumnGpoup(str);
							return;
						}
					}
				}
			});
			localLinearLayout.addView(button);
		}
		((View) localArrayList.get(0))
				.setBackgroundResource(R.drawable.main_column_navigation_lable_hl);
		((Button) localArrayList.get(0)).requestFocus();
		((Button) localArrayList.get(0)).setTextColor(-1);
	}

	/**
	 * 新建线程初始化用户，并获取榜单信息
	 */
	private void startThread()
	{
		logger.v("startThread() ----> Enter");
		new Thread()
		{
			public void run()
			{
				isTotalThreadStart = true;
				try
				{
					// 没有初始化
					if (!isInit)
					{
						if (songOnlineManager.isCanUseSim(MingOnlineMusic.this))
						{
							logger.v("initCmOmp()");
							isInit = songOnlineManager
									.initCmOmp(MingOnlineMusic.this);
							if (!isInit)
							{
								mDispatcher
										.sendMessage(mDispatcher
												.obtainMessage(DispatcherEventEnum.UI_INITIAL_FAIL));
								return;
							} else
							{
								mDispatcher
										.sendMessage(mDispatcher
												.obtainMessage(DispatcherEventEnum.UI_INITIAL_SUCCESS));
								mDispatcher
										.sendMessage(mDispatcher
												.obtainMessage(DispatcherEventEnum.Http_UpdateData_Begin));
								return;
							}
						} else
						{
							mHandler.sendEmptyMessage(SIM_IS_CAN);
							return;
						}
					} else
					{
						logger.v("isInit == true");
						ArrayList<ChartInfo> chartInfos = getTempData();
						// 获取榜单信息不成功
						if (chartInfos.size() <= 0)
						{
							mHandler.sendEmptyMessage(GET_INFO_FAIL);
							mDispatcher
									.sendMessage(mDispatcher
											.obtainMessage(DispatcherEventEnum.Http_UpdateData_Fail));
						}
						// 获取榜单信息成功
						if (chartInfos != null && chartInfos.size() != 0)
						{
							updateState(chartInfos);
							addMusicInfo(chartInfos);
							mHandler.sendEmptyMessage(GET_INFO_SUCC);
							// mDispatcher
							// .sendMessage(mDispatcher
							// .obtainMessage(DispatcherEventEnum.Http_UpdateData_Sucess));
						} else
						{
							mHandler.sendEmptyMessage(GET_INFO_FAIL);
							mDispatcher
									.sendMessage(mDispatcher
											.obtainMessage(DispatcherEventEnum.Http_UpdateData_Fail));
						}
					}
				} catch (Exception e)
				{
					// pad上,initCmOmp失败会抛出NumberFormatException
					e.printStackTrace();
					// 相应处理
				} finally
				{
					resetThreadState();
				}
			}
		}.start();
	}

	@Override
	public void handleMMHttpEvent(Message msg)
	{
		switch (msg.what)
		{
		case DispatcherEventEnum.Http_UpdateData_Begin:
			loadData();
			break;
		case DispatcherEventEnum.Http_UpdateData_Fail:
			mHandler.sendEmptyMessage(GET_INFO_FAIL);
			break;
		case DispatcherEventEnum.Http_UpdateData_Sucess:
			refresh();
		default:
			break;
		}
	}

	@Override
	public void handleUIEvent(Message msg)
	{
		switch (msg.what)
		{
		case DispatcherEventEnum.UI_INITIAL_SUCCESS:
			Toast.makeText(MingOnlineMusic.this, "初始化  成功", Toast.LENGTH_SHORT)
					.show();
			this.isInitialedPreferences.edit().putBoolean("InitTheAppid", true)
					.commit();
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			break;
		case DispatcherEventEnum.UI_INITIAL_FAIL:
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			Toast.makeText(MingOnlineMusic.this, "初始化  失败", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			break;
		}
	}

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case GET_INFO_SUCC:
				Toast.makeText(MingOnlineMusic.this, "加载数据成功",
						Toast.LENGTH_SHORT).show();
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				refresh();
				break;
			case GET_INFO_FAIL:
				Toast.makeText(MingOnlineMusic.this, "加载数据失败",
						Toast.LENGTH_SHORT).show();
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				break;
			case GET_MORE_INFO_FAIL:
				// totalPage--;
				break;
			case GET_INFO_NETERROR:
				Toast.makeText(MingOnlineMusic.this, "网络连接失败，请检查您的网络",
						Toast.LENGTH_SHORT).show();
				break;
			case SIM_IS_CAN:
				Toast.makeText(MingOnlineMusic.this, "您用的不是移动的sim卡或sim卡不存在",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	/**
	 * 获取榜单信息
	 * 
	 * @return
	 */
	private ArrayList<ChartInfo> getTempData()
	{
		logger.v("getTempData() ----> Enter");
		ArrayList<ChartInfo> chartInfo = null;
		if (isInit)
		{
			chartInfo = songOnlineManager.getChartInfo(this, 1,
					SongOnlineManager.SONG_PAGE_SIZE);
		}
		logger.v("getTempData() ----> Exit");
		return chartInfo;
	}

	/**
	 * 获取数据时,更新是否已经获取全部数据
	 */
	private void updateState(List<ChartInfo> tempInfos)
	{
		if (tempInfos.size() < SongOnlineManager.SONG_PAGE_SIZE)
		{
			isTotalOver = true;
		}
	}

	/**
	 * 线程跑完,重置线程的状态
	 * 
	 * @param bangType
	 */
	private void resetThreadState()
	{
		isTotalThreadStart = false;
	}

	/**
	 * 给相应的list中添加数据
	 * 
	 * @param tempInfos
	 */
	private void addMusicInfo(List<ChartInfo> tempInfos)
	{
		logger.v("addMusicInfo() ----> Enter");
		mChartInfos = new ArrayList<ChartInfo>();
		mChartInfos = tempInfos;
		logger.v("addMusicInfo() ----> Exit");
	}

	/**
	 * 加载数据
	 */
	private void loadData()
	{
		if (isTotalThreadStart)
			return;
		startThread();
	}

	private void InitTabInfoList()
	{
		TabItem tabItem1 = new TabItem();
		TabItem tabItem2 = new TabItem();
		TabItem tabItem3 = new TabItem();
		TabItem tabItem4 = new TabItem();
		tabItem1.category_type = "1";
		tabItem1.title = "榜单";
		tabItem2.category_type = "2";
		tabItem2.title = "专辑";
		tabItem3.category_type = "3";
		tabItem3.title = "歌手";
		tabItem4.category_type = "4";
		tabItem4.title = "标签";
		this.tabInfoList = new ArrayList<TabItem>();
		this.tabInfoList.add(tabItem1);
		this.tabInfoList.add(tabItem2);
		this.tabInfoList.add(tabItem3);
		this.tabInfoList.add(tabItem4);
	}

	/**
	 * 显示榜单信息
	 */
	private void refreshColumnContentUI()
	{
		List list = new ArrayList();
		if (this.mPageTotalCount == -1)
			this.mPageTotalCount = SongOnlineManager.SONG_PAGE_SIZE;
		this.mCurrentPageNo = this.mTargetPageNo;
		this.mReqMoreProgress.setVisibility(View.GONE);

		MusicListColumnItem musicListColumnItem;
		if (mChartInfos != null && mChartInfos.size() > 0)
		{
			for (int i = 0; i < mChartInfos.size(); i++)
			{
				musicListColumnItem = new MusicListColumnItem();
				musicListColumnItem.title = mChartInfos.get(i).getChartName();
				musicListColumnItem.url = mChartInfos.get(i).getChartCode();
				musicListColumnItem.category_type = "1";
				list.add(musicListColumnItem);
			}
			if (mColumnItemData != null && mColumnItemData.size() > 0)
			{
				mColumnItemData.clear();
			}
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
		} else
		{
			if (NetUtil.isConnection())
			{
				isNetWorkConnected = true;
			} else
			{
				isNetWorkConnected = false;
			}
			if (songOnlineManager.isCanUseSim(this))
			{
				logger.v("Can use Sim Card");
				if (isNetWorkConnected)
				{
					logger.v("NetWork is Connected");
					loadData();
					mCurrentDialog = DialogUtil.show1BtnProgressDialog(
							MingOnlineMusic.this, R.string.loading,
							R.string.cancel, new View.OnClickListener()
							{
								public void onClick(View view)
								{
									if (mCurrentDialog != null)
									{
										mCurrentDialog.dismiss();
										mCurrentDialog = null;
										mRetryLayout
												.setVisibility(View.VISIBLE);
										mEmpty.setVisibility(View.GONE);
										((Button) mRetryLayout
												.findViewById(R.id.refresh_temp_btn))
												.setOnClickListener(new View.OnClickListener()
												{
													public void onClick(
															View paramAnonymous2View)
													{
														mRetryLayout
																.setVisibility(8);
														mEmpty.setVisibility(0);
														mPageTotalCount = -1;
														mCurrentPageNo = 1;
														mTargetPageNo = 1;
														if ((tabInfoList == null)
																|| (tabInfoList
																		.isEmpty()))
														{
															loadData();
														} else
														{
															String str = ((TabItem) tabInfoList
																	.get(mButtonPosition)).url;
															switch (Integer
																	.parseInt(((TabItem) tabInfoList
																			.get(mButtonPosition)).category_type))
															{
															case 1:
																loadData();
																break;
															case 2:
																break;
															case 3:
																break;
															case 4:
																break;
															default:
																break;
															}
														}
													}
												});
									}
								}
							});
				}
			} else
			{
				mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
						"警告", "本应用只适用移动SIM卡，你可以使用本地功能",
						new View.OnClickListener()
						{
							@Override
							public void onClick(View v)
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
	}

	/**
	 * 单击加载更多内容
	 */
	private View.OnClickListener mLoadMoreOnClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			logger.v("mLoadMoreOnClickListener() ---> Exit");
			mReqMoreProgress.setVisibility(0);
			mTargetPageNo = (1 + mCurrentPageNo);
			String s;
			if ((mTargetPageNo > 1) && (mTargetPageNo <= mPageTotalCount))
			{
				s = ((TabItem) tabInfoList.get(mButtonPosition)).url;
				switch (Integer.parseInt(((TabItem) tabInfoList
						.get(mButtonPosition)).category_type))
				{
				case 1:
				case 2:
				case 3:
					// requestRecommendContentListData(s);
					break;
				case 4:
					break;
				default:
					break;
				}
			}
		}
	};

	/**
	 * 更新界面，数据
	 */
	private void refresh()
	{
		switch (Integer.valueOf(tabInfoList.get(mButtonPosition).category_type))
		{
		case 1:
			mRecommendContentListView.setVisibility(View.GONE);
			mColumnContentListView.setVisibility(View.VISIBLE);
			mAlbumGridView.setVisibility(View.GONE);
			mRetryLayout.setVisibility(View.GONE);
			refreshColumnContentUI();
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		default:
			break;
		}
	}
}
