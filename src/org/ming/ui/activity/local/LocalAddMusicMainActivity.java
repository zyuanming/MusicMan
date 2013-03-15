package org.ming.ui.activity.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.Song;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.UIEventListener;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.dispatcher.Dispatcher;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.view.TitleBarView;
import org.ming.ui.widget.PlayerStatusBar;
import org.ming.util.MyLogger;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.ToggleButton;

public class LocalAddMusicMainActivity extends ListActivity implements
		UIEventListener, SystemEventListener
{
	private static final int LIST_ITEM_ID_ALL_SONG = 0;
	private static final int LIST_ITEM_ID_BROWSE_BY_CATALOG = 2;
	private static final int LIST_ITEM_ID_BROWSE_BY_SINGER = 1;
	private static final int LIST_ITEM_ID_DWONLOAD_MUSIC = 3;
	private static final int LIST_ITEM_ID_Dobly_MUSIC = 4;
	private static final MyLogger logger = MyLogger
			.getLogger("LocalAddMusicMainActivity");
	Intent intent = null;
	long itemId;
	private SimpleAdapter mAdapter = null;
	private View.OnClickListener mAddAllMusicOnClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			logger.v("mAddAllMusicOnClickListener--onclick() ---> Enter");
			ToggleButton localToggleButton = mToggleAddAll;
			if (mToggleAddAll.isChecked())
				localToggleButton.setChecked(false);
			logger.v("mAddAllMusicOnClickListener--onclick() ---> Exit");
		}
	};
	private int mAllSongsNumber = 0;
	private ImageView mBtnComplete = null;
	private View.OnClickListener mCompleteOnClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			logger.v("mCompleteOnClickListener--onclick() ---> Enter");
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			new ArrayList();
			ArrayList arraylist;
			if (UIGlobalSettingParameter.localmusic_folder_names == null)
			{
				DBController localDBController = mDBController;
				String[] arrayOfString = new String[1];
				arrayOfString[0] = GlobalSettingParameter.LOCAL_PARAM_MUSIC_STORE_SD_DIR;
				arraylist = (ArrayList) localDBController.getSongsByFolder(
						arrayOfString,
						UIGlobalSettingParameter.localmusic_scan_smallfile);
			} else
			{
				arraylist = (ArrayList) mDBController.getSongsByFolder(
						UIGlobalSettingParameter.localmusic_folder_names,
						UIGlobalSettingParameter.localmusic_scan_smallfile);
			}

			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			if (!mToggleAddAll.isChecked() || arraylist == null
					|| arraylist.size() == 0)
			{
				mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
						LocalAddMusicMainActivity.this,
						LocalAddMusicMainActivity.this
								.getText(R.string.title_information_common),
						LocalAddMusicMainActivity.this
								.getText(R.string.local_addmusic_nomusic),
						new View.OnClickListener()
						{
							public void onClick(View paramAnonymous2View)
							{
								if (mCurrentDialog != null)
									mCurrentDialog.dismiss();
							}
						});
			} else
			{
				mCurrentDialog = DialogUtil.showIndeterminateProgressDialog(
						LocalAddMusicMainActivity.this,
						R.string.local_addmusic_subtile);
				new LocalAddMusicMainActivity.AddAllTask()
						.execute(new String[0]);
			}
			logger.v("mCompleteOnClickListener--onclick() ---> Exit");
		}
	};
	private Controller mController = null;
	private Dialog mCurrentDialog = null;
	private DBController mDBController = null;
	private int mDoblySongNumber = 0;
	private int mDownloadFileNumber = 0;
	private int mFolderNumber = 0;
	private View mHeadView = null;
	private PlayerStatusBar mPlayerStatusBar = null;
	private int mPlaylistId = 0;
	private long[] mSelectedSongids;
	private int mSingerNumber = 0;
	ArrayList<Song> mSong;
	private TitleBarView mTitleBar;
	private ToggleButton mToggleAddAll = null;

	private void addRow(List<Map<String, Object>> paramList,
			String paramString1, String paramString2, int paramInt)
	{
		logger.v("addRow() ---> Enter");
		HashMap localHashMap = new HashMap();
		localHashMap.put("title", paramString1);
		localHashMap.put("num", paramString2);
		localHashMap.put("icon", Integer.valueOf(paramInt));
		paramList.add(localHashMap);
		logger.v("addRow() ---> Exit");
	}

	private void completeTask()
	{
		ArrayList arraylist;
		logger.v("completeTask() ---> Enter");
		new ArrayList();
		if (UIGlobalSettingParameter.localmusic_folder_names == null)
		{
			DBController dbcontroller = mDBController;
			String as[] = new String[1];
			as[0] = GlobalSettingParameter.LOCAL_PARAM_MUSIC_STORE_SD_DIR;
			arraylist = (ArrayList) dbcontroller.getSongsByFolder(as,
					UIGlobalSettingParameter.localmusic_scan_smallfile);
		} else
		{
			arraylist = (ArrayList) mDBController.getSongsByFolder(
					UIGlobalSettingParameter.localmusic_folder_names,
					UIGlobalSettingParameter.localmusic_scan_smallfile);
		}
		if (!mToggleAddAll.isChecked())
		{
			mDBController.getPlaylistByID(mPlaylistId, 1);
			ArrayList arraylist1 = (ArrayList) mDBController
					.getSongsFromPlaylist(mPlaylistId, 1);
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			Dispatcher dispatcher;
			Song song;
			if (arraylist1 == null || arraylist1.size() == 0)
			{
				Intent intent1 = new Intent(this,
						org.ming.ui.activity.MobileMusicMainActivity.class);
				intent1.putExtra("TABINDEX", 1);
				startActivity(intent1);
			} else
			{
				Dispatcher dispatcher1 = MobileMusicApplication.getInstance()
						.getEventDispatcher();
				dispatcher1.sendMessage(dispatcher1.obtainMessage(4012));
			}
			dispatcher = MobileMusicApplication.getInstance()
					.getEventDispatcher();
			dispatcher.sendMessage(dispatcher.obtainMessage(4012));
			logger.v("mAddAllMusicOnClickListener() ---> Exit");
			logger.v("completeTask() ---> Exit");

		} else
		{
			if (arraylist != null && arraylist.size() != 0)
			{
				long al[];
				int i = 0;
				int j;
				al = new long[arraylist.size()];
				for (Iterator iterator = arraylist.iterator(); iterator
						.hasNext();)
				{
					Song song = (Song) iterator.next();
					al[i] = song.mId;
					i++;
				}
				if (al.length > 0)
					mDBController.addSongs2Playlist(mPlaylistId, al, 1);
			}
		}
	}

	private void refreshUI()
	{
		logger.v("refreshUI() ---> Enter");
		ArrayList localArrayList = new ArrayList(4);
		String[] arrayOfString = { "title", "num", "icon" };
		int[] arrayOfInt = { R.id.text1, R.id.text2, R.id.listicon1 };
		String str1 = getText(R.string.local_music_all_song).toString();
		Object[] arrayOfObject1 = new Object[1];
		arrayOfObject1[0] = Integer.valueOf(this.mAllSongsNumber);
		addRow(localArrayList, str1,
				getString(R.string.local_music_all_song_num, arrayOfObject1)
						.toString(), R.drawable.list_cell_button_arrow_selector);
		String str2 = getText(R.string.local_music_browse_by_singer).toString();
		Object[] arrayOfObject2 = new Object[1];
		arrayOfObject2[0] = Integer.valueOf(this.mSingerNumber);
		addRow(localArrayList,
				str2,
				getString(R.string.local_music_browse_by_singer_num,
						arrayOfObject2).toString(),
				R.drawable.list_cell_button_arrow_selector);
		String str3 = getText(R.string.local_music_browse_by_catalog)
				.toString();
		Object[] arrayOfObject3 = new Object[1];
		arrayOfObject3[0] = Integer.valueOf(this.mFolderNumber);
		addRow(localArrayList,
				str3,
				getString(R.string.local_music_browse_by_catalog_num,
						arrayOfObject3).toString(),
				R.drawable.list_cell_button_arrow_selector);
		String str4 = getText(R.string.local_music_download_music).toString();
		Object[] arrayOfObject4 = new Object[1];
		arrayOfObject4[0] = Integer.valueOf(this.mDownloadFileNumber);
		addRow(localArrayList, str4,
				getString(R.string.local_music_all_song_num, arrayOfObject4)
						.toString(), R.drawable.list_cell_button_arrow_selector);
		String str5 = getText(R.string.dobly_song_number).toString();
		Object[] arrayOfObject5 = new Object[1];
		arrayOfObject5[0] = Integer.valueOf(this.mDoblySongNumber);
		addRow(localArrayList, str5,
				getString(R.string.local_music_all_song_num, arrayOfObject5)
						.toString(), R.drawable.list_cell_button_arrow_selector);
		this.mAdapter = new SimpleAdapter(this, localArrayList,
				R.layout.cmcc_list_2, arrayOfString, arrayOfInt);
		setListAdapter(this.mAdapter);
		logger.v("refreshUI() ---> Exit");
	}

	public void handleSystemEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			return;
		case 4:
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
			finish();
			break;
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
		case 4012:
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
			finish();
		}

	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_local_addmusic_column_layout);
		this.mController = ((MobileMusicApplication) getApplication())
				.getController();
		this.mDBController = this.mController.getDBController();
		this.mTitleBar = ((TitleBarView) findViewById(R.id.title_view));
		this.mTitleBar.setCurrentActivity(this);
		this.mTitleBar.setTitle(R.string.local_addmusic_subtile);
		this.mBtnComplete = ((ImageView) this.mTitleBar
				.findViewById(R.id.btn_rightbutton));
		this.mBtnComplete.setOnClickListener(this.mCompleteOnClickListener);
		this.mPlayerStatusBar = ((PlayerStatusBar) findViewById(R.id.playerStatusBar));
		this.mTitleBar.setButtons(2);
		this.mTitleBar
				.setRightBtnImage(R.drawable.btn_titlebar_complete_selector);
		if (UIGlobalSettingParameter.localmusic_folder_names == null)
		{
			String str = this.mDBController.getLocalFolder();
			if (str != null)
				UIGlobalSettingParameter.localmusic_folder_names = str
						.split(";");
		}
		this.mHeadView = getLayoutInflater().inflate(
				R.layout.local_addmusic_head_view, null);
		this.mHeadView.setOnClickListener(this.mAddAllMusicOnClickListener);
		this.mToggleAddAll = ((ToggleButton) this.mHeadView
				.findViewById(R.id.btn_addmusic_add));
		getListView().addHeaderView(this.mHeadView);
		this.mController.addSystemEventListener(4, this);
		this.mController.addUIEventListener(4012, this);
		this.mController.addSystemEventListener(22, this);
		if (UIGlobalSettingParameter.localmusic_folder_names != null)
		{
			this.mAllSongsNumber = this.mDBController.getAllSongsCountByFolder(
					UIGlobalSettingParameter.localmusic_folder_names,
					UIGlobalSettingParameter.localmusic_scan_smallfile);
			this.mSingerNumber = this.mDBController.getArtistCountByFolder(
					UIGlobalSettingParameter.localmusic_folder_names,
					UIGlobalSettingParameter.localmusic_scan_smallfile);
			this.mFolderNumber = UIGlobalSettingParameter.localmusic_folder_names.length;
		}
		DBController localDBController1 = this.mDBController;
		String[] arrayOfString1 = new String[1];
		arrayOfString1[0] = GlobalSettingParameter.LOCAL_PARAM_MUSIC_STORE_SD_DIR;
		this.mDownloadFileNumber = localDBController1.getAllSongsCountByFolder(
				arrayOfString1,
				UIGlobalSettingParameter.localmusic_scan_smallfile);
		DBController localDBController2 = this.mDBController;
		String[] arrayOfString2 = new String[1];
		arrayOfString2[0] = GlobalSettingParameter.LOCAL_PARAM_DOBLY_MUSIC_STORE_SD_DIR;
		this.mDoblySongNumber = localDBController2.getAllSongsCountByFolder(
				arrayOfString2,
				UIGlobalSettingParameter.localmusic_scan_smallfile);
		Intent localIntent = getIntent();
		this.mPlaylistId = localIntent.getIntExtra("playlistid", 0);
		this.mSelectedSongids = localIntent.getLongArrayExtra("Selectedmusic");
		if ((this.mSelectedSongids != null)
				&& (this.mSelectedSongids.length == this.mAllSongsNumber))
			this.mToggleAddAll.setChecked(true);
		refreshUI();
		logger.v("onCreate() ---> Exit");
	}

	protected void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		if (this.mCurrentDialog != null)
		{
			this.mCurrentDialog.dismiss();
			this.mCurrentDialog = null;
		}
		this.mController.removeUIEventListener(4012, this);
		this.mController.removeSystemEventListener(4, this);
		this.mController.removeSystemEventListener(22, this);
		super.onDestroy();
		logger.v("onDestroy() ---> Exit");
	}

	protected void onListItemClick(ListView paramListView, View paramView,
			int paramInt, long paramLong)
	{
		logger.v("onListItemClick() ---> Enter");
		super.onListItemClick(paramListView, paramView, paramInt, paramLong);
		switch ((int) paramLong)
		{
		default:
		case LIST_ITEM_ID_ALL_SONG:
		{
			this.intent = new Intent(this, LocalAddMusicSongListActivity.class);
			this.intent.putExtra("title",
					getText(R.string.local_music_all_song).toString());
			this.intent.putExtra("playlistid", this.mPlaylistId);
			this.intent.putExtra("Selectedmusic", this.mSelectedSongids);
			this.intent.putExtra("type", 0);
			startActivity(this.intent);
		}
			break;
		case LIST_ITEM_ID_BROWSE_BY_SINGER:
		{
			this.intent = new Intent(this, LocalAddMusicColumnActivity.class);
			this.intent.putExtra("title",
					getText(R.string.local_music_browse_by_singer).toString());
			this.intent.putExtra("TYPE", 1);
			this.intent.putExtra("playlistid", this.mPlaylistId);
			this.intent.putExtra("Selectedmusic", this.mSelectedSongids);
			startActivity(this.intent);
		}
			break;
		case LIST_ITEM_ID_BROWSE_BY_CATALOG:
		{
			this.intent = new Intent(this, LocalAddMusicColumnActivity.class);
			this.intent.putExtra("title",
					getText(R.string.local_music_browse_by_catalog).toString());
			this.intent.putExtra("TYPE", 2);
			this.intent.putExtra("playlistid", this.mPlaylistId);
			this.intent.putExtra("Selectedmusic", this.mSelectedSongids);
			startActivity(this.intent);
		}
			break;
		case LIST_ITEM_ID_DWONLOAD_MUSIC:
		{
			this.intent = new Intent(this, LocalAddMusicSongListActivity.class);
			this.intent.putExtra("type", 4);
			this.intent.putExtra("folderpath",
					GlobalSettingParameter.LOCAL_PARAM_MUSIC_STORE_SD_DIR);
			this.intent.putExtra("title",
					getText(R.string.local_music_download_music).toString());
			this.intent.putExtra("playlistid", this.mPlaylistId);
			this.intent.putExtra("Selectedmusic", this.mSelectedSongids);
			startActivity(this.intent);
		}
			break;
		case LIST_ITEM_ID_Dobly_MUSIC:
		{
			this.intent = new Intent(this, LocalAddMusicSongListActivity.class);
			this.intent.putExtra("type", 4);
			this.intent
					.putExtra(
							"folderpath",
							GlobalSettingParameter.LOCAL_PARAM_DOBLY_MUSIC_STORE_SD_DIR);
			this.intent.putExtra("title",
					getText(R.string.local_music_download_music).toString());
			this.intent.putExtra("playlistid", this.mPlaylistId);
			this.intent.putExtra("Selectedmusic", this.mSelectedSongids);
			startActivity(this.intent);
		}
			break;
		}
		logger.v("onListItemClick() ---> Exit");

	}

	protected void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		this.mPlayerStatusBar.unRegistEventListener();
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		this.mPlayerStatusBar.registEventListener();
		logger.v("onResume() ---> Exit");
	}

	class AddAllTask extends AsyncTask<String, Void, Void>
	{
		protected Void doInBackground(String[] paramArrayOfString)
		{
			LocalAddMusicMainActivity.this.completeTask();
			return null;
		}

		public void onPostExecute(Void paramVoid)
		{
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			super.onPostExecute(null);
		}

		public void onPreExecute()
		{
			super.onPreExecute();
		}
	}

	private class AsyncTaskGetDateFromDb extends
			AsyncTask<Integer, Void, List<Song>>
	{
		private AsyncTaskGetDateFromDb()
		{}

		protected List<Song> doInBackground(Integer[] paramArrayOfInteger)
		{
			switch (paramArrayOfInteger[0].intValue())
			{
			case 1:
			case 2:
			default:
				return null;
			case 0:
				mSong = ((ArrayList<Song>) mDBController.getSongsByFolder(
						UIGlobalSettingParameter.localmusic_folder_names,
						UIGlobalSettingParameter.localmusic_scan_smallfile));
				return mSong;
			case 3:
				DBController dbcontroller1 = mDBController;
				String as1[] = new String[1];
				as1[0] = GlobalSettingParameter.LOCAL_PARAM_MUSIC_STORE_SD_DIR;
				mSong = (ArrayList<Song>) dbcontroller1.getSongsByFolder(as1,
						UIGlobalSettingParameter.localmusic_scan_smallfile);
				return mSong;
			case 4:
				DBController dbcontroller = mDBController;
				String as[] = new String[1];
				as[0] = GlobalSettingParameter.LOCAL_PARAM_DOBLY_MUSIC_STORE_SD_DIR;
				mSong = (ArrayList<Song>) dbcontroller.getSongsByFolder(as,
						UIGlobalSettingParameter.localmusic_scan_smallfile);
				return mSong;
			}
		}

		protected void onPostExecute(List<Song> paramList)
		{
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			intent.putParcelableArrayListExtra("SONGLIST", mSong);
			startActivity(intent);
			super.onPostExecute(mSong);
		}
	}
}