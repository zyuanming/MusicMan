package org.ming.ui.activity.online;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.http.item.MusicListColumnItem;
import org.ming.center.system.SystemEventListener;
import org.ming.ui.activity.MobileMusicMainActivity;
import org.ming.ui.view.BaseViewInterface;
import org.ming.ui.view.ColumnListView;
import org.ming.ui.view.SongListView;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

/**
 * 显示榜单对应的歌曲信息列表，只是显示数据，数据的获取不在这里
 * 
 * @author lkh
 * 
 */
public class MusicOnlineMusicColumnDetailActivity extends Activity implements
		SystemEventListener
{
	private static final MyLogger logger = MyLogger
			.getLogger("MusicOnlineMusicColumnDetailActivity");
	private int deleteType = 0;
	private Controller mController;
	private boolean mIsFromPushService = false;
	private TitleBarView mTitleBar;
	private ViewFlipper mViewFlipper;
	private BaseViewInterface mViewInterface;
	private String title;

	public boolean dispatchTouchEvent(MotionEvent motionevent)
	{
		boolean flag = super.dispatchTouchEvent(motionevent);
		switch (motionevent.getAction())
		{
		default:
			break;
		case 0:
			if ((SongListView.mListButtonClickListener != null)
					&& (SongListView.mListButtonClickListener
							.closePopupWindow()))
				flag = true;
			break;
		}
		return flag;
	}

	public void handleSystemEvent(Message paramMessage)
	{
		logger.v("handleSystemEvent() ---> Enter");
		switch (paramMessage.what)
		{
		default:
			logger.v("handleSystemEvent() ---> Exit");
			break;
		case 22:
			finish();
			break;
		}
	}

	protected void onCreate(Bundle bundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(bundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_online_music_column_detail_layout);
		mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		mViewFlipper = (ViewFlipper) findViewById(R.id.song_viewflipper);
		mTitleBar = (TitleBarView) findViewById(R.id.title_view);
		Intent intent = getIntent();
		if (intent.getExtras() != null)
		{
			mTitleBar.setTitle(intent.getStringExtra("title"));
			title = intent.getStringExtra("title");
			mIsFromPushService = intent.getBooleanExtra("fromPushService",
					false);
		}
		MusicListColumnItem musiclistcolumnitem = (MusicListColumnItem) intent
				.getParcelableExtra("COLUMITEM");
		logger.v("MusicListColumnItem.category_type ----"
				+ musiclistcolumnitem.category_type);
		switch (Integer.parseInt(musiclistcolumnitem.category_type))
		{
		case 1:
			deleteType = 0;
			SongListView songlistview = new SongListView(this);
			songlistview.setURL(musiclistcolumnitem.url);
			songlistview.setName(musiclistcolumnitem.title);
			mViewInterface = songlistview;
			mViewFlipper.addView(songlistview);
			break;
		case 2:
			break;
		default:
			break;
		}
		logger.v("onCreate() ---> Exit");
	}

	protected void onDestroy()
	{
		logger.v("onCreate() ---> Enter");
		this.mController.removeSystemEventListener(22, this);
		int i;
		if (this.mViewFlipper != null)
		{
			this.mViewFlipper.destroyDrawingCache();
			i = 0;
			while (true)
			{
				i++;
				if (this.deleteType == 1)
				{
					ColumnListView localColumnListView = (ColumnListView) this.mViewFlipper
							.getChildAt(i);
					if (localColumnListView != null)
					{
						localColumnListView.releaseResource();
						localColumnListView.destroyDrawingCache();
					}
				}
				if (i >= this.mViewFlipper.getChildCount())
				{
					this.mViewFlipper.removeAllViews();
					logger.v("onCreate() ---> Exit");
					super.onDestroy();
					return;
				} else
				{
					if (deleteType == 0)
					{
						SongListView songlistview = (SongListView) mViewFlipper
								.getChildAt(i);
						if (songlistview != null)
						{
							songlistview.releaseResource();
							songlistview.destroyDrawingCache();
						}
					}
				}
			}
		} else
		{
			this.mViewFlipper = null;
			this.mViewInterface = null;
			this.mTitleBar = null;
			super.onDestroy();
			logger.v("onCreate() ---> Exit");
			return;
		}
	}

	public boolean onKeyDown(int i, KeyEvent keyevent)
	{
		logger.v("onKeyDown() ----> Enter");
		boolean flag = true;
		if (i == 4)
		{
			if ((SongListView.mListButtonClickListener != null)
					&& (SongListView.mListButtonClickListener
							.closePopupWindow()))
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
		logger.v("flag ---- " + flag);
		logger.v("onKeyDown() ----> Exit");
		return flag;
	}

	protected void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		MobclickAgent.onPause(this);
		this.mViewInterface.removeListner();
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		MobclickAgent.onResume(this);
		this.mViewInterface.addListner();

		((SongListView) mViewInterface).getDataFromURL();
		// 返回按钮
		mTitleBar.setLeftBtnOnlickListner(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				if (mIsFromPushService)
				{
					Intent intent = new Intent(
							MusicOnlineMusicColumnDetailActivity.this,
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
		if (SongListView.mListButtonClickListener != null)
			SongListView.mListButtonClickListener.doUnCompleteTask();
		logger.v("onResume() ---> Exit");
		return;
	}
}