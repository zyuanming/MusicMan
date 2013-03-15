package org.ming.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.http.item.MusicListColumnItem;
import org.ming.center.player.PlayerEventListener;
import org.ming.ui.activity.online.MusicOnlineMusicAlbumDetailActivity;
import org.ming.ui.adapter.MobileMusicColumnListItemAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.widget.PlayerStatusBar;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.XMLParser;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.AttributeSet;
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

public class ColumnListView extends RelativeLayout implements
		MMHttpEventListener, PlayerEventListener, BaseViewInterface
{
	private static final MyLogger logger = MyLogger.getLogger("ColumnListView");
	private boolean LOAD_MORE = true;
	private List<MusicListColumnItem> mColumnListData = new ArrayList();
	private AdapterView.OnItemClickListener mColumnListItemOnItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView adapterview, View view, int i,
				long l)
		{
			if (i < mColumnListData.size() && i >= 0)
			{
				switch (Integer.parseInt(((MusicListColumnItem) mColumnListData
						.get(i)).category_type))
				{
				case 0: // '\0'
					Intent intent = new Intent(mContext,
							MusicOnlineMusicAlbumDetailActivity.class);
					intent.putExtra(
							"title",
							((MusicListColumnItem) mColumnListData.get(i)).title);
					intent.putExtra("album_song_url",
							((MusicListColumnItem) mColumnListData.get(i)).url);
					mContext.startActivity(intent);
					break;
				}
			}
		}
	};
	private ListView mColumnListView;
	private Context mContext;
	private Controller mController;
	private Dialog mCurrentDialog;
	private int mCurrentPageNo;
	private MMHttpTask mCurrentTask;
	private HttpController mHttpController;
	private boolean mInital;
	private LayoutInflater mLayoutInflater;
	/**
	 * 加载更多
	 */
	private View.OnClickListener mLoadMoreOnClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			logger.v("mLoadMoreOnClickListener() ---> Exit");
			mReqMoreProgress.setVisibility(0);
			mTargetPageNo = (1 + mCurrentPageNo);
			if ((mTargetPageNo > 1) && (mTargetPageNo <= mPageTotalCount))
				requestContentListData();
			logger.v("mLoadMoreOnClickListener() ---> Exit");
		}
	};
	private View mLoadMoreView;
	private MobileMusicColumnListItemAdapter mMobileMusicColumnListItemAdapter;
	private ImageView mNothingView;
	private int mPageTotalCount;
	private PlayerStatusBar mPlayerStatusBar;
	private ProgressBar mReqMoreProgress;
	private LinearLayout mRetryLayout;
	/**
	 * 滑动滚动条
	 */
	AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener()
	{
		int firstItem = -1;
		int totalItem = 0;
		int visibleItem = 0;

		public void onScroll(AbsListView paramAnonymousAbsListView,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3)
		{
			if (ColumnListView.this.LOAD_MORE)
			{
				this.firstItem = paramAnonymousInt1;
				this.visibleItem = paramAnonymousInt2;
				this.totalItem = paramAnonymousInt3;
			}
		}

		public void onScrollStateChanged(AbsListView paramAnonymousAbsListView,
				int paramAnonymousInt)
		{
			if ((ColumnListView.this.LOAD_MORE) && (paramAnonymousInt == 0)
					&& (this.firstItem + this.visibleItem == this.totalItem)
					&& (ColumnListView.this.mLoadMoreView.getVisibility() == 0))
			{
				ColumnListView.this.mLoadMoreOnClickListener
						.onClick(ColumnListView.this.mLoadMoreView);
				ColumnListView.this.LOAD_MORE = false;
			}
		}
	};
	private int mTargetPageNo = 1;
	private String mURL = null;

	public ColumnListView(Context context)
	{
		super(context);
		mColumnListView = null;
		mColumnListData = new ArrayList();
		mURL = null;
		mCurrentPageNo = 1;
		mPageTotalCount = -1;
		mTargetPageNo = 1;
		mInital = false;
		LOAD_MORE = true;
		mContext = context;
		inital();
	}

	public ColumnListView(Context context, AttributeSet attributeset)
	{
		super(context);
		mColumnListView = null;
		mColumnListData = new ArrayList();
		mURL = null;
		mCurrentPageNo = 1;
		mPageTotalCount = -1;
		mTargetPageNo = 1;
		mInital = false;
		LOAD_MORE = true;
		mContext = context;
		inital();
	}

	private void onHttpResponse(MMHttpTask paramMMHttpTask)
	{
		this.mNothingView.setVisibility(8);
		int i = paramMMHttpTask.getRequest().getReqType();
		byte[] arrayOfByte = paramMMHttpTask.getResponseBody();
		switch (i)
		{
		default:
		case 1005:
		case 1011:
		case 5008:
		case 5015:
			XMLParser xmlparser;
			xmlparser = new XMLParser(arrayOfByte);
			if ((xmlparser.getRoot() == null)
					|| (xmlparser.getValueByTag("code") == null))
			{
				if (this.mReqMoreProgress != null)
					this.mReqMoreProgress.setVisibility(8);
				this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
						this.mContext, this.mContext
								.getText(R.string.title_information_common),
						this.mContext
								.getText(R.string.fail_to_parse_xml_common),
						new View.OnClickListener()
						{
							public void onClick(View paramAnonymousView)
							{
								ColumnListView.this.mCurrentDialog.dismiss();
								if (ColumnListView.this.mReqMoreProgress != null)
									ColumnListView.this.mReqMoreProgress
											.setVisibility(4);
							}
						});
			} else
			{
				String s = xmlparser.getValueByTag("code");
				if (s != null && !s.equals("000000"))
				{
					if (mReqMoreProgress != null)
						mReqMoreProgress.setVisibility(8);
					if (mCurrentDialog != null)
					{
						mCurrentDialog.dismiss();
						mCurrentDialog = null;
					}
					mCurrentDialog = DialogUtil
							.show1BtnDialogWithTitleMsg(
									mContext,
									mContext.getText(R.string.title_information_common),
									xmlparser.getValueByTag("info"),
									new View.OnClickListener()
									{
										public void onClick(View view)
										{
											mCurrentDialog.dismiss();
											if (mReqMoreProgress != null)
												mReqMoreProgress
														.setVisibility(4);
										}
									});
				}
			}
			if (this.mPageTotalCount == -1)
				this.mPageTotalCount = Integer.parseInt(xmlparser
						.getValueByTag("pagecount"));
			this.mCurrentPageNo = this.mTargetPageNo;
			if (this.mCurrentPageNo == this.mPageTotalCount)
			{
				if ((this.mColumnListView.getFooterViewsCount() > 0)
						&& (this.mLoadMoreView != null))
					this.mColumnListView.removeFooterView(this.mLoadMoreView);
				else if ((this.mColumnListView.getFooterViewsCount() == 0)
						&& (this.mCurrentPageNo < this.mPageTotalCount))
					this.mColumnListView.addFooterView(this.mLoadMoreView);
			}
			this.mReqMoreProgress.setVisibility(8);
			List localList = xmlparser.getListByTagAndAttribute("item",
					MusicListColumnItem.class);
			if (localList != null)
			{
				addColumnList(localList);
				mInital = true;
				LOAD_MORE = true;
			}
			break;
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
						ColumnListView.this.mCurrentDialog.dismiss();
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
						ColumnListView.this.mCurrentDialog.dismiss();
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	private void requestContentListData()
	{
		logger.v("requestContentListData() ---> Enter");
		char c;
		MMHttpRequest mmhttprequest;
		if (NetUtil.isNetStateWap())
			c = '\u03ED';
		else
			c = '\u1390';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		mmhttprequest.setURL(mURL);
		mmhttprequest.addUrlParams("pageno", String.valueOf(mTargetPageNo));
		mmhttprequest.addUrlParams("itemcount",
				GlobalSettingParameter.SERVER_INIT_PARAM_ITEM_COUNT);
		mCurrentTask = mHttpController.sendRequest(mmhttprequest);
		logger.v("requestContentListData() ---> Exit");
	}

	public void addColumnList(List<MusicListColumnItem> paramList)
	{
		if ((paramList != null) && (paramList.size() > 0)
				&& (this.mNothingView != null))
			this.mNothingView.setVisibility(8);
		this.mColumnListData.addAll(paramList);
		int i = this.mColumnListView.getFirstVisiblePosition();
		if (this.mMobileMusicColumnListItemAdapter == null)
		{
			this.mMobileMusicColumnListItemAdapter = new MobileMusicColumnListItemAdapter(
					this.mContext, this.mColumnListData);
			this.mColumnListView
					.setAdapter(this.mMobileMusicColumnListItemAdapter);
		}
		this.mMobileMusicColumnListItemAdapter.notifyDataSetChanged();
		if (i > 0)
			this.mColumnListView.setSelectionFromTop(i + 1, 0);
		this.mColumnListView.setFadingEdgeLength(0);
		this.mColumnListView
				.setOnItemClickListener(this.mColumnListItemOnItemClickListener);
		this.mColumnListView.setOnScrollListener(this.mScrollListener);
	}

	public void addListner()
	{
		this.mPlayerStatusBar.registEventListener();
		this.mController.addHttpEventListener(3003, this);
		this.mController.addHttpEventListener(3005, this);
		this.mController.addHttpEventListener(3004, this);
		this.mController.addHttpEventListener(3006, this);
		this.mController.addHttpEventListener(3007, this);
		this.mController.addHttpEventListener(3008, this);
	}

	public void getDataFromURL(final int i)
	{
		this.mRetryLayout.setVisibility(8);
		if (!this.mInital)
		{
			MMHttpRequest localMMHttpRequest = MMHttpRequestBuilder
					.buildRequest(i);
			localMMHttpRequest.setURL(this.mURL);
			localMMHttpRequest.addUrlParams("itemcount",
					GlobalSettingParameter.SERVER_INIT_PARAM_ITEM_COUNT);
			localMMHttpRequest.addUrlParams("pageno",
					String.valueOf(this.mTargetPageNo));
			this.mCurrentTask = this.mHttpController
					.sendRequest(localMMHttpRequest);
			this.mCurrentDialog = DialogUtil.show1BtnProgressDialog(
					this.mContext, R.string.loading, R.string.cancel,
					new View.OnClickListener()
					{
						public void onClick(View paramAnonymousView)
						{
							ColumnListView.this.mCurrentTask.setIsCancled(true);
							if (ColumnListView.this.mCurrentDialog != null)
							{
								ColumnListView.this.mCurrentDialog.dismiss();
								ColumnListView.this.mCurrentDialog = null;
								ColumnListView.this.mCurrentTask = null;
								ColumnListView.this.mNothingView
										.setVisibility(8);
								ColumnListView.this.mRetryLayout
										.setVisibility(0);
								((Button) ColumnListView.this.mRetryLayout
										.findViewById(R.id.refresh_btn))
										.setOnClickListener(new View.OnClickListener()
										{
											public void onClick(
													View paramAnonymous2View)
											{
												ColumnListView.this
														.getDataFromURL(i);
											}
										});
							}
						}
					});
		}
	}

	public void handleMMHttpEvent(Message message)
	{
		MMHttpTask mmhttptask = (MMHttpTask) message.obj;
		if (mmhttptask != null && mCurrentTask != null
				&& mmhttptask.getTransId() == mCurrentTask.getTransId())
		{
			mCurrentTask = null;
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			switch (message.what)
			{
			case 3003:
				onHttpResponse(mmhttptask);
				break;

			case 3004:
				onSendHttpRequestFail(mmhttptask);
				break;

			case 3006:
				onSendHttpRequestTimeOut(mmhttptask);
				break;

			case 3007:
			case 3008:
				if (mReqMoreProgress != null)
					mReqMoreProgress.setVisibility(8);
				Uiutil.ifSwitchToWapDialog(mContext);
				LOAD_MORE = true;
				break;
			}
		}
	}

	public void handlePlayerEvent(Message paramMessage)
	{}

	/**
	 * 初始化
	 */
	public void inital()
	{
		this.mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		this.mHttpController = this.mController.getHttpController();
		this.mLayoutInflater = LayoutInflater.from(this.mContext);
		removeAllViews();
		RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(
				-1, -2);
		RelativeLayout localRelativeLayout = (RelativeLayout) this.mLayoutInflater
				.inflate(R.layout.column_list_view, null);
		addView(localRelativeLayout, localLayoutParams);
		this.mColumnListView = ((ListView) findViewById(R.id.columnlistview));
		this.mNothingView = ((ImageView) findViewById(R.id.nothing));
		this.mRetryLayout = ((LinearLayout) findViewById(R.id.refresh_layout));
		this.mPlayerStatusBar = ((PlayerStatusBar) localRelativeLayout
				.findViewById(R.id.player_status_bar));
		this.mLoadMoreView = this.mLayoutInflater.inflate(
				R.layout.load_more_list_footer_view, null);
		this.mLoadMoreView.setOnClickListener(this.mLoadMoreOnClickListener);
		this.mReqMoreProgress = ((ProgressBar) this.mLoadMoreView
				.findViewById(R.id.progressbar));
	}

	public void releaseResource()
	{
		this.mLayoutInflater = null;
		this.mColumnListView = null;
		if (this.mColumnListData != null)
		{
			this.mColumnListData.clear();
			this.mColumnListData = null;
		}
		if (this.mMobileMusicColumnListItemAdapter != null)
			this.mMobileMusicColumnListItemAdapter.releaseAdapterResource();
		this.mMobileMusicColumnListItemAdapter = null;
		if (this.mPlayerStatusBar != null)
			this.mPlayerStatusBar.destroyDrawingCache();
		this.mPlayerStatusBar = null;
		this.mLoadMoreView = null;
		this.mNothingView = null;
		this.mRetryLayout = null;
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
	}

	public void setURL(String paramString)
	{
		this.mURL = paramString;
	}

	@Override
	public void getDataFromURL()
	{
		// TODO Auto-generated method stub

	}
}