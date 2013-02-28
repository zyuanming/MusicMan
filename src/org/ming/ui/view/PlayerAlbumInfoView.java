package org.ming.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.Song;
import org.ming.center.download.UrlImageDownloader;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.player.PlayerController;
import org.ming.util.NetUtil;
import org.ming.util.XMLParser;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

public class PlayerAlbumInfoView extends LinearLayout implements
		MMHttpEventListener, BaseViewInterface
{
	private String mAlbumImgUrl;
	private String mAlbumInfo = null;
	private String mAlbumName = null;
	private String mAlbumSumary = null;
	private LinearLayout mBgInfoViewAlbum;
	private LinearLayout mBgInfoViewSinger;
	private CompoundButton.OnCheckedChangeListener mBtnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
	{
		public void onCheckedChanged(
				CompoundButton paramAnonymousCompoundButton,
				boolean paramAnonymousBoolean)
		{
			if (paramAnonymousBoolean)
				switch (paramAnonymousCompoundButton.getId())
				{
				default:
				case R.id.info_view_btn_ablum_info:
				{
					PlayerAlbumInfoView.this.showAlbumInfo();
				}
					break;
				case R.id.info_view_btn_artist_info:
				{
					PlayerAlbumInfoView.this.showSingerInfo();
				}
					break;
				}
		}
	};
	private Context mContext;
	private Controller mController;
	private MMHttpTask mCurrentAlbumTask = null;
	private Dialog mCurrentDialog = null;
	private MMHttpTask mCurrentSingerTask = null;
	private List<MMHttpTask> mCurrentTasks = new ArrayList();
	private HttpController mHttpController = null;
	private UrlImageDownloader mImageDownloader;
	private ProgressBar mInfoLoadingBar = null;
	private ImageView mInfoViewAlbumImg;
	private RadioButton mInfoViewBtnAblumInfo;
	private RadioButton mInfoViewBtnArtistInfo;
	private TextView mInfoViewContent;
	private ImageView mInfoViewSingerImg;
	private TextView mInfoViewSongerInfo;
	private TextView mInfoViewSummaryInfo;
	private PlayerController mPlayerController = null;
	private String mSingerImgUrl;
	private String mSingerInfo = null;
	private String mSingerName = null;
	private String mSingerSumary = null;
	private LinearLayout nothingView;

	public PlayerAlbumInfoView(Context paramContext)
	{
		super(paramContext);
		this.mContext = paramContext;
		initialize();
	}

	private void onFailResponse(MMHttpTask paramMMHttpTask)
	{
		switch (paramMMHttpTask.getRequest().getReqType())
		{
		default:
		case 1018:
		case 5022:
		{
			mInfoLoadingBar.setVisibility(8);
		}
			break;
		case 1017:
		case 5021:
		{
			mInfoLoadingBar.setVisibility(8);
		}
			break;
		}
	}

	private void onHttpResponse(MMHttpTask paramMMHttpTask)
	{
		int i = paramMMHttpTask.getRequest().getReqType();
		XMLParser localXMLParser = new XMLParser(
				paramMMHttpTask.getResponseBody());
		if ((localXMLParser.getRoot() == null)
				|| (localXMLParser.getValueByTag("code") == null))
			onFailResponse(paramMMHttpTask);
		else if ((localXMLParser.getValueByTag("code") != null)
				&& (!localXMLParser.getValueByTag("code").equals("000000")))
		{
			switch (i)
			{
			default:
				break;
			case 1017:
			case 5021:
				this.mInfoLoadingBar.setVisibility(8);
				onInitAlbumInfoResponse(paramMMHttpTask);
				break;
			case 1018:
			case 5022:
				this.mInfoLoadingBar.setVisibility(8);
				onInitSongerInfoResponse(paramMMHttpTask);
			}
		} else
		{
			onFailResponse(paramMMHttpTask);
		}
	}

	private void onInitAlbumInfoResponse(MMHttpTask mmhttptask)
	{
		if (mCurrentDialog != null)
		{
			mCurrentDialog.dismiss();
			mCurrentDialog = null;
		}
		XMLParser xmlparser = new XMLParser(mmhttptask.getResponseBody());
		mAlbumInfo = xmlparser.getValueByTag("detail");
		mAlbumName = xmlparser.getValueByTag("title");
		mAlbumSumary = xmlparser.getValueByTag("summary");
		mAlbumImgUrl = xmlparser.getValueByTag("img");
		Song song = mPlayerController.getCurrentPlayingItem();
		if (mAlbumImgUrl != null && song != null)
		{
			mImageDownloader.download(mAlbumImgUrl, 0x7f0200d1,
					mInfoViewAlbumImg, song.mGroupCode);
			mInfoViewAlbumImg
					.setOnClickListener(new android.view.View.OnClickListener()
					{

						public void onClick(View view)
						{
							// Intent intent = new Intent(mContext,
							// HDImageActivity.class);
							// intent.putExtra("image_url", mAlbumImgUrl);
							// intent.addFlags(0x10000000);
							// mContext.startActivity(intent);
						}
					});
		} else
		{
			mImageDownloader
					.download(null, 0x7f0200d1, mInfoViewAlbumImg, null);
		}
		mInfoLoadingBar.setVisibility(8);
		if (mInfoViewBtnAblumInfo.isChecked())
			showAlbumInfo();
	}

	private void onInitSongerInfoResponse(MMHttpTask mmhttptask)
	{
		if (mCurrentDialog != null)
		{
			mCurrentDialog.dismiss();
			mCurrentDialog = null;
		}
		XMLParser xmlparser = new XMLParser(mmhttptask.getResponseBody());
		mSingerInfo = xmlparser.getValueByTag("detail");
		mSingerName = xmlparser.getValueByTag("title");
		mSingerSumary = xmlparser.getValueByTag("summary");
		mSingerImgUrl = xmlparser.getValueByTag("img");
		Song song = mPlayerController.getCurrentPlayingItem();
		if (mSingerImgUrl != null && song != null)
		{
			mImageDownloader.download(mSingerImgUrl, 0x7f0200d1,
					mInfoViewSingerImg, song.mGroupCode);
			mInfoViewSingerImg
					.setOnClickListener(new android.view.View.OnClickListener()
					{
						public void onClick(View view)
						{
							// Intent intent = new Intent(mContext,
							// HDImageActivity.class);
							// intent.putExtra("image_url", mSingerImgUrl);
							// intent.addFlags(0x10000000);
							// mContext.startActivity(intent);
						}
					});
		} else
		{
			mImageDownloader.download(null, 0x7f0200d1, mInfoViewSingerImg,
					null);
		}
		mInfoLoadingBar.setVisibility(8);
		if (mInfoViewBtnArtistInfo.isChecked())
			showSingerInfo();
	}

	private void onSendHttpRequestTimeOut(MMHttpTask paramMMHttpTask)
	{
		this.mInfoLoadingBar.setVisibility(8);
	}

	private void showAlbumInfo()
	{
		this.mInfoViewContent.setText(this.mAlbumInfo);
		this.mInfoViewSongerInfo.setText(this.mAlbumName);
		this.mInfoViewSummaryInfo.setText(this.mAlbumSumary);
		this.mBgInfoViewAlbum.setVisibility(0);
		this.mBgInfoViewSinger.setVisibility(8);
	}

	private void showSingerInfo()
	{
		this.mInfoViewContent.setText(this.mSingerInfo);
		this.mInfoViewSongerInfo.setText(this.mSingerName);
		this.mInfoViewSummaryInfo.setText(this.mSingerSumary);
		this.mBgInfoViewAlbum.setVisibility(8);
		this.mBgInfoViewSinger.setVisibility(0);
	}

	public void addListner()
	{
		this.mController.addHttpEventListener(3003, this);
		this.mController.addHttpEventListener(3005, this);
		this.mController.addHttpEventListener(3004, this);
		this.mController.addHttpEventListener(3006, this);
		this.mController.addHttpEventListener(3007, this);
		this.mController.addHttpEventListener(3008, this);
	}

	public void clearAlbumInfo()
	{
		mInfoViewContent.setText("");
		mInfoViewSongerInfo.setText("");
		mInfoViewSummaryInfo.setText("");
		mImageDownloader.download(null, 0x7f0200d1, mInfoViewAlbumImg, null);
		mAlbumInfo = "";
		mAlbumName = "";
		mAlbumSumary = "";
	}

	public void clearSingerInfo()
	{
		mInfoViewContent.setText("");
		mInfoViewSongerInfo.setText("");
		mInfoViewSummaryInfo.setText("");
		mImageDownloader.download(null, 0x7f0200d1, mInfoViewSingerImg, null);
		mSingerInfo = "";
		mSingerName = "";
		mSingerSumary = "";
	}

	public void getDataFromURL(int paramInt)
	{}

	public void handleMMHttpEvent(Message message)
	{
		MMHttpTask mmhttptask = (MMHttpTask) message.obj;
		if (mmhttptask != null && mCurrentTasks.size() != 0)
		{
			boolean flag;
			int i = mCurrentAlbumTask.getTransId();
			int j = mmhttptask.getTransId();
			flag = false;
			if (i == j)
				flag = true;
			if (mCurrentSingerTask.getTransId() == mmhttptask.getTransId())
				flag = true;
			if (flag)
			{
				mCurrentTasks.remove(mmhttptask);
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

				case 3005:
					onFailResponse(mmhttptask);
					break;

				case 3004:
					onFailResponse(mmhttptask);
					break;

				case 3006:
					onSendHttpRequestTimeOut(mmhttptask);
					break;

				case 3007:
					onFailResponse(mmhttptask);
					break;

				case 3008:
					onFailResponse(mmhttptask);
					break;
				}
			}
		}
	}

	public void initialize()
	{
		this.mImageDownloader = new UrlImageDownloader(this.mContext);
		this.mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		this.mPlayerController = this.mController.getPlayerController();
		this.mHttpController = this.mController.getHttpController();
		LayoutInflater.from(this.mContext).inflate(2130903141, this);
		this.mInfoViewBtnAblumInfo = ((RadioButton) findViewById(2131034430));
		this.mInfoViewBtnAblumInfo
				.setOnCheckedChangeListener(this.mBtnCheckedChangeListener);
		this.mInfoViewBtnArtistInfo = ((RadioButton) findViewById(2131034431));
		this.mInfoViewBtnArtistInfo
				.setOnCheckedChangeListener(this.mBtnCheckedChangeListener);
		this.mInfoLoadingBar = ((ProgressBar) findViewById(2131034442));
		this.mBgInfoViewAlbum = ((LinearLayout) findViewById(2131034433));
		this.mInfoViewAlbumImg = ((ImageView) findViewById(2131034434));
		this.mInfoViewAlbumImg.setBackgroundResource(2130837713);
		this.mBgInfoViewSinger = ((LinearLayout) findViewById(2131034435));
		this.mInfoViewSingerImg = ((ImageView) findViewById(2131034436));
		this.mInfoViewSingerImg.setBackgroundResource(2130837713);
		this.mInfoViewSongerInfo = ((TextView) findViewById(2131034438));
		this.mInfoViewSummaryInfo = ((TextView) findViewById(2131034439));
		this.mInfoViewContent = ((TextView) findViewById(2131034441));
	}

	public void removeListner()
	{
		if (this.mCurrentDialog != null)
		{
			this.mCurrentDialog.dismiss();
			this.mCurrentDialog = null;
		}
		this.mController.removeHttpEventListener(3003, this);
		this.mController.removeHttpEventListener(3005, this);
		this.mController.removeHttpEventListener(3004, this);
		this.mController.removeHttpEventListener(3006, this);
		this.mController.removeHttpEventListener(3007, this);
		this.mController.removeHttpEventListener(3008, this);
	}

	public void requestalbuminfo(String s)
	{
		char c;
		MMHttpRequest mmhttprequest;
		if (NetUtil.isNetStateWap())
			c = '\u03F9';
		else
			c = '\u139D';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		mmhttprequest.addUrlParams("contentid", s);
		mCurrentAlbumTask = mHttpController.sendRequest(mmhttprequest);
		mCurrentTasks.add(mCurrentAlbumTask);
		mInfoLoadingBar.setVisibility(0);
	}

	public void requestsingerinfo(String s)
	{
		char c;
		MMHttpRequest mmhttprequest;
		if (NetUtil.isNetStateWap())
			c = '\u03FA';
		else
			c = '\u139E';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		mmhttprequest.addUrlParams("contentid", s);
		mCurrentSingerTask = mHttpController.sendRequest(mmhttprequest);
		mCurrentTasks.add(mCurrentSingerTask);
		mInfoLoadingBar.setVisibility(0);
	}

	public void setURL(String paramString)
	{}

	public void showNothingImage(boolean flag)
	{
		nothingView = (LinearLayout) findViewById(0x7f05013a);
		if (flag)
			nothingView.setVisibility(0);
		else
			nothingView.setVisibility(8);
	}

	@Override
	public void getDataFromURL()
	{
		// TODO Auto-generated method stub

	}
}