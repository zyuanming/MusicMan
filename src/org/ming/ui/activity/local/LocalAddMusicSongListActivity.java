package org.ming.ui.activity.local;

import java.util.ArrayList;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.UIEventListener;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.dispatcher.Dispatcher;
import org.ming.ui.activity.MobileMusicMainActivity;
import org.ming.ui.adapter.LocalAddMusicListCursorAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class LocalAddMusicSongListActivity extends ListActivity implements
		UIEventListener, SystemEventListener
{
	class AddAllTask extends AsyncTask<String, Void, Integer>
	{
		@Override
		protected Integer doInBackground(String as[])
		{
			long al[] = adapter.getSelectedMusic();
			long al1[] = adapter.getDeSelectedMusic();
			if (al != null && al.length > 0)
				mDBController.addSongs2Playlist(mPlaylistId, al, 1);
			if (al1 != null && al1.length > 0)
				mDBController.deleteSongsFromPlaylist(mPlaylistId, al1, 1);
			Cursor cursor = mDBController.getCursorFromPlaylist(mPlaylistId, 1);
			int i = 0;
			if (cursor != null)
			{
				i = cursor.getCount();
				cursor.close();
			}
			return Integer.valueOf(i);
		}

		public void onPostExecute(Integer integer)
		{
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			completeTask(integer.intValue());
		}

		public void onPreExecute()
		{
			super.onPreExecute();
		}

	}

	class asyncGetCursor extends AsyncTask<Integer, Void, Cursor>
	{
		int type;

		protected Cursor doInBackground(Integer ainteger[])
		{
			int i;
			Cursor cursor;
			type = ainteger[0].intValue();
			i = type;
			cursor = null;
			switch (i)
			{
			case 1:
			case 2:
			default:
				break;
			case 0:
				cursor = mDBController.getSongsCursorByFolder(
						UIGlobalSettingParameter.localmusic_folder_names,
						UIGlobalSettingParameter.localmusic_scan_smallfile);
				break;
			case 3:
				cursor = mDBController.getSongsCursorByFolderAndSinger(
						UIGlobalSettingParameter.localmusic_folder_names,
						getIntent().getIntExtra("songid", 0),
						UIGlobalSettingParameter.localmusic_scan_smallfile);
				break;
			case 4:
				DBController dbcontroller = mDBController;
				String as[] = new String[1];
				as[0] = getIntent().getStringExtra("folderpath");
				boolean flag;
				if (mTitle == getText(R.string.local_music_download_music)
						.toString())
					flag = true;
				else
					flag = UIGlobalSettingParameter.localmusic_scan_smallfile;
				cursor = dbcontroller.getSongsCursorByFolder(as, flag);
				break;
			case 5:
				cursor = mDBController.getCursorFromPlaylist(getIntent()
						.getIntExtra("playlistid", 0), 1);
				break;
			}
			if (cursor != null)
			{
				long al[] = getIntent().getLongArrayExtra("Selectedmusic");
				if (al == null || al.length <= 0 || cursor == null
						|| cursor.getCount() <= 0)
				{
					return cursor;
				} else
				{
					int j = cursor.getColumnIndexOrThrow("_id");
					while (cursor.moveToNext())
					{
						int k = cursor.getInt(j);
						for (int i1 = 0; i1 < al.length; i1++)
						{
							long l1 = al[i1];
							if ((long) k == l1)
								mCurrentselectedidlist.add(Long.valueOf(l1));
						}
					}
					cursor.moveToPosition(-1);
					if (mCurrentselectedidlist.size() == cursor.getCount())
						mToggleAddAll.setChecked(true);
				}
			}
			return cursor;
		}

		protected void onPostExecute(Cursor cursor)
		{
			switch (this.type)
			{
			case 2:
			default:
				return;
			case 0:
				queryHandlerQueryComplete(cursor);
				break;
			case 1:
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				break;
			case 3:
				queryHandlerQueryComplete(cursor);
				break;
			case 4:
				queryHandlerQueryComplete(cursor);
				break;
			}
		}

	}

	public static final int CURSOR_ALL_SONG = 0;
	private static final int CURSOR_REQUERY_AFTER_DELETE = 1;
	private static final int CURSOR_REQUERY_RENAME = 2;
	public static final int CURSOR_SONG_BY_FOLDERPATH = 4;
	public static final int CURSOR_SONG_BY_PLAYLIST = 5;
	public static final int CURSOR_SONG_BY_SINGERID = 3;
	public static final String FOLDERPATH = "folderpath";
	public static final String LAUNCHTYPE = "type";
	public static final String PLAYLISTID = "playlistid";
	public static final String SONGID = "songid";
	public static final MyLogger logger = MyLogger
			.getLogger("LocalAddMusicSongListActivity");
	private LocalAddMusicListCursorAdapter adapter;
	private android.view.View.OnClickListener mAddAllMusicOnClickListener;
	private android.view.View.OnClickListener mAddSingleMusicOnClickListener;
	private ImageView mBtnComplete;
	private android.view.View.OnClickListener mCompleteOnClickListener;
	private Controller mController;
	private Dialog mCurrentDialog;
	private List mCurrentselectedidlist;
	private DBController mDBController;
	private View mHeadView;
	private int mLaunchType;
	private int mPlaylistId;
	private String mTitle;
	private TitleBarView mTitleBar;
	private ToggleButton mToggleAddAll;

	public LocalAddMusicSongListActivity()
	{
		mController = null;
		mDBController = null;
		mToggleAddAll = null;
		mHeadView = null;
		mBtnComplete = null;
		mPlaylistId = 0;
		mCurrentselectedidlist = new ArrayList();
		mCurrentDialog = null;
		mLaunchType = 0;
		mTitle = "";
		mAddSingleMusicOnClickListener = new android.view.View.OnClickListener()
		{

			public void onClick(View view)
			{
				int i = ((Integer) view.getTag()).intValue();
				adapter.setChecked(i);
				adapter.notifyDataSetChanged();
				mToggleAddAll.setChecked(adapter.isAllChecked());
				LocalAddMusicSongListActivity.logger
						.v("mAddAllMusicOnClickListener() ---> Exit");
			}
		};
		mAddAllMusicOnClickListener = new android.view.View.OnClickListener()
		{

			public void onClick(View view)
			{
				ToggleButton togglebutton = mToggleAddAll;
				boolean flag;
				if (mToggleAddAll.isChecked())
					flag = false;
				else
					flag = true;
				togglebutton.setChecked(flag);
				if (mToggleAddAll.isChecked())
					adapter.setAllChecked(true);
				else
					adapter.setAllChecked(false);
				adapter.notifyDataSetChanged();
				logger.v("mAddAllMusicOnClickListener() ---> Exit");
			}
		};
		mCompleteOnClickListener = new android.view.View.OnClickListener()
		{

			public void onClick(View view)
			{
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				long al[] = adapter.getSelectedMusic();
				long al1[] = adapter.getDeSelectedMusic();
				if ((al == null || al.length == 0)
						&& (al1 == null || al1.length == 0))
				{
					mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
							LocalAddMusicSongListActivity.this,
							getText(R.string.title_information_common),
							getText(R.string.local_addmusic_nomusic),
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
					mCurrentDialog = DialogUtil
							.showIndeterminateProgressDialog(
									LocalAddMusicSongListActivity.this,
									R.string.local_addmusic_subtile);
					(new AddAllTask()).execute(new String[0]);
				}
			}

		};
	}

	private void completeTask(int i)
	{
		if (i == 0)
		{
			Intent intent = new Intent(this, MobileMusicMainActivity.class);
			intent.putExtra("TABINDEX", 1);
			startActivity(intent);
		} else
		{
			Dispatcher dispatcher = MobileMusicApplication.getInstance()
					.getEventDispatcher();
			dispatcher.sendMessage(dispatcher.obtainMessage(4012));
		}
		logger.v("mAddAllMusicOnClickListener() ---> Exit");
	}

	private void queryHandlerQueryComplete(Cursor cursor)
	{
		logger.v("queryHandlerQueryComplete() ---> Enter");
		adapter = new LocalAddMusicListCursorAdapter(getApplicationContext(),
				cursor);
		getListView().setAdapter(adapter);
		adapter.setSelectedMusic(mCurrentselectedidlist);
		adapter.OnClickListener(mAddSingleMusicOnClickListener);
		adapter.notifyDataSetChanged();
		getListView().setFadingEdgeLength(0);
		logger.v("queryHandlerQueryComplete() ---> Exit");
	}

	private void refreshUI()
	{
		logger.v("refreshUI() ---> Enter");
		Intent intent = getIntent();
		mPlaylistId = intent.getIntExtra("playlistid", 0);
		mLaunchType = intent.getIntExtra("type", 0);
		mTitle = intent.getStringExtra("title");
		if (mTitle != null)
			mTitleBar.setTitle(mTitle);
		asyncGetCursor asyncgetcursor = new asyncGetCursor();
		Integer ainteger[] = new Integer[1];
		ainteger[0] = Integer.valueOf(mLaunchType);
		asyncgetcursor.execute(ainteger);
		logger.v("refreshUI() ---> Exit");
	}

	public void handleSystemEvent(Message message)
	{
		switch (message.what)
		{
		default:
			return;
		case 4:
			finish();
			break;
		case 22:
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
		case 4012:
			finish();
			break;
		}
	}

	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_local_addmusic_layout);
		mTitleBar = (TitleBarView) findViewById(R.id.title_view);
		mTitleBar.setCurrentActivity(this);
		mTitleBar.setButtons(2);
		mTitleBar.setRightBtnImage(R.drawable.btn_titlebar_complete_selector);
		mBtnComplete = (ImageView) mTitleBar.findViewById(R.id.btn_rightbutton);
		mBtnComplete.setOnClickListener(mCompleteOnClickListener);
		mHeadView = getLayoutInflater().inflate(
				R.layout.local_addmusic_head_view, null);
		mHeadView.setOnClickListener(mAddAllMusicOnClickListener);
		mToggleAddAll = (ToggleButton) mHeadView
				.findViewById(R.id.btn_addmusic_add);
		mToggleAddAll
				.setOnClickListener(new android.view.View.OnClickListener()
				{
					public void onClick(View view)
					{
						if (mToggleAddAll.isChecked())
							adapter.setAllChecked(true);
						else
							adapter.setAllChecked(false);
						adapter.notifyDataSetChanged();
					}
				});
		getListView().addHeaderView(mHeadView);
		mController = ((MobileMusicApplication) getApplication())
				.getController();
		mDBController = mController.getDBController();
		mController.addSystemEventListener(4, this);
		mController.addUIEventListener(4012, this);
		mController.addSystemEventListener(22, this);
	}

	protected void onDestroy()
	{
		mController.removeUIEventListener(4012, this);
		mController.removeSystemEventListener(4, this);
		mController.removeSystemEventListener(22, this);
		super.onDestroy();
	}

	protected void onResume()
	{
		refreshUI();
		super.onResume();
	}
}
