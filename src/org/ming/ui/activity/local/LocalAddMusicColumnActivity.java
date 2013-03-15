package org.ming.ui.activity.local;

import java.util.ArrayList;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.UIEventListener;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.dispatcher.Dispatcher;
import org.ming.ui.activity.MobileMusicMainActivity;
import org.ming.ui.adapter.LocalColumnListCursorAdapter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.view.TitleBarView;
import org.ming.ui.widget.PlayerStatusBar;
import org.ming.util.MyLogger;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LocalAddMusicColumnActivity extends ListActivity implements
		UIEventListener, SystemEventListener
{
	class AddAllTask extends AsyncTask<String, Void, Integer>
	{
		protected Integer doInBackground(String as[])
		{
			Cursor cursor = mDBController.getSongsCursorByFolder(
					UIGlobalSettingParameter.localmusic_folder_names,
					UIGlobalSettingParameter.localmusic_scan_smallfile);
			if (!mToggleAddAll.isChecked() || cursor == null)
			{
				int i = 0;
				if (cursor != null)
				{
					i = cursor.getCount();
					cursor.close();
				}
				return Integer.valueOf(i);
			} else
			{
				long al[] = new long[cursor.getCount()];
				int j = 0;
				int k = cursor.getColumnIndexOrThrow("_id");
				if (cursor.moveToNext())
				{
					int l = j + 1;
					al[j] = cursor.getInt(k);
					j = l;
				} else
				{
					if (al.length > 0)
						mDBController.addSongs2Playlist(mPlaylistId, al, 1);
				}
				return j;
			}
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

	public static final int TYPE_FOLDER = 2;
	public static final int TYPE_FOLDER_ENTER = 4;
	public static final int TYPE_SINGER = 1;
	public static final int TYPE_SINGER_ENTER = 3;
	public static final MyLogger logger = MyLogger
			.getLogger("LocalAddMusicColumnActivity");
	LocalColumnListCursorAdapter adapter;
	int currentItemId;
	int currentItemSingerId;
	String currentItemTitle;
	Intent intent;
	private android.widget.AdapterView.OnItemClickListener itemclicklistener;
	private android.view.View.OnClickListener mAddAllMusicOnClickListener;
	private ImageView mBtnComplete;
	private android.view.View.OnClickListener mCompleteOnClickListener;
	private Controller mController;
	private Dialog mCurrentDialog;
	private DBController mDBController;
	private View mHeadView;
	private PlayerStatusBar mPlayerStatusBar;
	private int mPlaylistId;
	private long mSelectedSongids[];
	private int mSingerIds[];
	ArrayList mSong;
	private int mSubTile[];
	private String mTile[];
	private TitleBarView mTitleBar;
	private ToggleButton mToggleAddAll;
	private int mType;

	public LocalAddMusicColumnActivity()
	{
		mController = null;
		mDBController = null;
		mSingerIds = null;
		mTile = null;
		mSubTile = null;
		mPlaylistId = 0;
		mType = 0;
		mHeadView = null;
		mToggleAddAll = null;
		mBtnComplete = null;
		mCurrentDialog = null;
		mPlayerStatusBar = null;
		intent = null;
		currentItemId = 0;
		adapter = null;
		itemclicklistener = new android.widget.AdapterView.OnItemClickListener()
		{

			public void onItemClick(AdapterView adapterview, View view, int i,
					long l)
			{
				LocalAddMusicColumnActivity.logger
						.v("itemclicklistener() --onItemClick---> Enter");
				currentItemId = i - 1;
				if (currentItemId >= 0)
				{
					if (mCurrentDialog != null)
						mCurrentDialog.dismiss();
					switch (mType)
					{
					default:
						break;

					case 1: // '\001'
						currentItemTitle = adapter
								.getCurrentItemTitleByPostion(currentItemId);
						currentItemSingerId = adapter
								.getCurrentItemIdByPostion(currentItemId);
						if ((currentItemTitle == null)
								|| (currentItemTitle.equals("<unknown>")))
							currentItemTitle = (String) getText(R.string.unknown_artist_name_db_controller);
						intent = new Intent(LocalAddMusicColumnActivity.this,
								LocalAddMusicSongListActivity.class);
						intent.putExtra("type", 3);
						intent.putExtra("title", currentItemTitle);
						intent.putExtra("playlistid", mPlaylistId);
						intent.putExtra("Selectedmusic", mSelectedSongids);
						currentItemSingerId = adapter
								.getCurrentItemIdByPostion(currentItemId);
						intent.putExtra("songid", currentItemSingerId);
						startActivity(intent);
						break;

					case 2: // '\002'
						intent = new Intent(LocalAddMusicColumnActivity.this,
								LocalAddMusicSongListActivity.class);
						intent.putExtra("type", 4);
						intent.putExtra("folderpath", mTile[currentItemId]);
						intent.putExtra("title", mTile[currentItemId]);
						intent.putExtra("playlistid", mPlaylistId);
						intent.putExtra("Selectedmusic", mSelectedSongids);
						startActivity(LocalAddMusicColumnActivity.this.intent);
						break;
					}
				}
			}
		};
		mAddAllMusicOnClickListener = new android.view.View.OnClickListener()
		{

			public void onClick(View view)
			{
				logger.v("mAddAllMusicOnClickListener() ---> Enter");
				ToggleButton togglebutton = mToggleAddAll;
				boolean flag;
				if (mToggleAddAll.isChecked())
					flag = false;
				else
					flag = true;
				togglebutton.setChecked(flag);
				logger.v("mAddAllMusicOnClickListener() ---> Exit");
			}

		};
		mCompleteOnClickListener = new android.view.View.OnClickListener()
		{

			public void onClick(View view)
			{
				logger.v("mCompleteOnClickListener()--onClick ---> Enter");
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				if (!mToggleAddAll.isChecked())
				{
					mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
							LocalAddMusicColumnActivity.this,
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
									LocalAddMusicColumnActivity.this,
									R.string.local_addmusic_subtile);
					(new AddAllTask()).execute(new String[0]);
				}
				logger.v("mCompleteOnClickListener()--onClick ---> Exit");
			}

		};
	}

	private void completeTask(int i)
	{
		if (i == 0)
		{
			Intent intent1 = new Intent(this, MobileMusicMainActivity.class);
			intent1.putExtra("TABINDEX", 1);
			startActivity(intent1);
		}
		Dispatcher dispatcher = MobileMusicApplication.getInstance()
				.getEventDispatcher();
		dispatcher.sendMessage(dispatcher.obtainMessage(4012));
		logger.v("mAddAllMusicOnClickListener() ---> Exit");
	}

	private void refreshUI()
	{
		logger.v("refreshUI() ---> Enter");
		Intent intent1 = getIntent();
		if (intent1.getStringExtra("title") != null)
			mTitleBar.setTitle(intent1.getStringExtra("title"));
		mPlaylistId = intent1.getIntExtra("playlistid", 0);
		mSelectedSongids = intent1.getLongArrayExtra("Selectedmusic");
		mType = intent1.getIntExtra("TYPE", 0);
		AsyncTaskGetDateFromDb asynctaskgetdatefromdb = new AsyncTaskGetDateFromDb();
		Integer ainteger[] = new Integer[1];
		ainteger[0] = Integer.valueOf(mType);
		asynctaskgetdatefromdb.execute(ainteger);
		logger.v("refreshUI() ---> Exit");
	}

	private void setAdapterAfterAsyncTask(Cursor cursor)
	{
		if (mType == 2)
		{
			getListView().setAdapter(
					new MobileMusicLocalColumnListAdapter(this));
		} else
		{
			adapter = new LocalColumnListCursorAdapter(this, cursor);
			adapter.setArtistSongCountList(mSubTile);
			getListView().setAdapter(adapter);
		}
		getListView().setOnItemClickListener(itemclicklistener);
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
		logger.v("onCreate() ---> Enter");
		super.onCreate(bundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_local_column_layout);
		mTitleBar = (TitleBarView) findViewById(R.id.title_view);
		mHeadView = getLayoutInflater().inflate(
				R.layout.local_addmusic_head_view, null);
		mHeadView.setOnClickListener(mAddAllMusicOnClickListener);
		mToggleAddAll = (ToggleButton) mHeadView
				.findViewById(R.id.btn_addmusic_add);
		getListView().addHeaderView(mHeadView);
		mTitleBar.setCurrentActivity(this);
		mBtnComplete = (ImageView) mTitleBar.findViewById(R.id.btn_rightbutton);
		mBtnComplete.setOnClickListener(mCompleteOnClickListener);
		mTitleBar.setButtons(2);
		mTitleBar.setRightBtnImage(R.drawable.btn_titlebar_complete_selector);
		mPlayerStatusBar = (PlayerStatusBar) findViewById(R.id.playerStatusBar);
		mController = ((MobileMusicApplication) getApplication())
				.getController();
		mDBController = mController.getDBController();
		mController.addUIEventListener(4012, this);
		mController.addSystemEventListener(22, this);
		refreshUI();
		logger.v("onCreate() ---> Exit");
	}

	protected void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		mPlayerStatusBar = null;
		mController.removeUIEventListener(4012, this);
		mController.removeSystemEventListener(22, this);
		logger.v("onDestroy() ---> Exit");
		super.onDestroy();
	}

	protected void onPause()
	{
		logger.v("onPause() ---> Enter");
		mPlayerStatusBar.unRegistEventListener();
		super.onPause();
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		mPlayerStatusBar.registEventListener();
		logger.v("onResume() ---> Exit");
		super.onResume();
	}

	private class AsyncTaskGetDateFromDb extends
			AsyncTask<Integer, Void, Cursor>
	{

		protected Cursor doInBackground(Integer ainteger[])
		{
			int i = ainteger[0].intValue();
			Cursor cursor = null;
			switch (i)
			{
			default:
				return cursor;
			case 1:
				cursor = mDBController
						.getArtistsByCursor(UIGlobalSettingParameter.localmusic_folder_names);
				if (cursor != null && cursor.getCount() > 0)
				{
					mSubTile = new int[cursor.getCount()];
					int j1 = cursor.getColumnIndex("_id");
					int k1 = 0;
					do
					{
						if (!cursor.moveToNext())
						{
							cursor.moveToPosition(-1);
							return cursor;
						}
						mSubTile[k1] = mDBController
								.getAllSongsCountByFolderAndSinger(
										UIGlobalSettingParameter.localmusic_folder_names,
										cursor.getInt(j1),
										UIGlobalSettingParameter.localmusic_scan_smallfile);
						k1++;
					} while (true);
				}
				break;
			case 2:
				mTile = UIGlobalSettingParameter.localmusic_folder_names;
				String as[] = mTile;
				if (mTile != null && mTile.length > 0)
				{
					mSubTile = new int[mTile.length];
					int k = 0;
					String as1[] = mTile;
					int l = as1.length;
					int i1 = 0;
					do
					{
						if (i1 >= l)
							return cursor;
						String s = as1[i1];
						mSubTile[k] = mDBController
								.getAllSongsCountByFolder(
										new String[] { s },
										UIGlobalSettingParameter.localmusic_scan_smallfile);
						k++;
						i1++;
					} while (true);
				}
				break;
			}
			return cursor;
		}

		protected void onPostExecute(Cursor cursor)
		{
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			setAdapterAfterAsyncTask(cursor);
		}
	}

	private class MobileMusicLocalColumnListAdapter extends BaseAdapter
	{

		private LayoutInflater mInflater;

		public int getCount()
		{
			int i;
			if (mTile == null)
				i = 0;
			else
				i = mTile.length;
			return i;
		}

		public Object getItem(int i)
		{
			return mTile[i];
		}

		public long getItemId(int i)
		{
			return (long) i;
		}

		public View getView(int i, View view, ViewGroup viewgroup)
		{
			ViewHolder viewholder;
			TextView textview;
			LocalAddMusicColumnActivity localaddmusiccolumnactivity;
			Object aobj[];
			if (view == null)
			{
				viewholder = new ViewHolder();
				view = mInflater.inflate(
						R.layout.list_cell_localmusic_column_list, null);
				viewholder.column_tile = (TextView) view
						.findViewById(R.id.column_title);
				viewholder.column_subtile = (TextView) view
						.findViewById(R.id.column_subtitle);
				view.setTag(viewholder);
			} else
			{
				viewholder = (ViewHolder) view.getTag();
			}
			viewholder.column_tile.setText(mTile[i]);
			textview = viewholder.column_subtile;
			localaddmusiccolumnactivity = LocalAddMusicColumnActivity.this;
			aobj = new Object[1];
			aobj[0] = Integer.valueOf(mSubTile[i]);
			textview.setText(localaddmusiccolumnactivity.getString(
					R.string.local_music_subtile, aobj));
			return view;
		}

		public MobileMusicLocalColumnListAdapter(Context context)
		{
			mInflater = LayoutInflater.from(context);
		}

		public final class ViewHolder
		{
			public TextView column_subtile;
			public TextView column_tile;

			public ViewHolder()
			{}
		}
	}
}
