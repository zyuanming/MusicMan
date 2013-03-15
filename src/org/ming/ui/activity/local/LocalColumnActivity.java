package org.ming.ui.activity.local;

import java.util.ArrayList;
import java.util.Iterator;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.Song;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.ui.adapter.LocalColumnListCursorAdapter;
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
import android.widget.TextView;

public class LocalColumnActivity extends ListActivity implements
		SystemEventListener
{
	public static final int TYPE_FOLDER = 2;
	public static final int TYPE_FOLDER_ENTER = 4;
	public static final int TYPE_SINGER = 1;
	public static final int TYPE_SINGER_ENTER = 3;
	public static final MyLogger logger = MyLogger
			.getLogger("LocalColumnActivity");
	LocalColumnListCursorAdapter adapter = null;
	public static int currentItemId = 0;
	int currentItemSingerId;
	String currentItemTitle;
	Intent intent = null;
	private Controller mController = null;
	private Dialog mCurrentDialog = null;
	private DBController mDBController = null;
	private PlayerStatusBar mPlayerStatusBar = null;
	private int[] mSingerIds = null;
	ArrayList<Song> mSong;
	private int[] mSubTile = null;
	private String[] mTile = null;
	private TitleBarView mTitleBar;
	private int mType = 0;
	private AdapterView.OnItemClickListener itemclicklistener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView adapterview, View view, int i,
				long l)
		{
			logger.v("itemclicklistener--onItemClick() ---> Enter");
			currentItemId = i;
			if (mCurrentDialog != null)
				mCurrentDialog.dismiss();
			switch (mType)
			{
			default:
				logger.v("itemclicklistener--onItemClick() ---> Exit");
				return;
			case 1: // 歌手浏览
				intent = new Intent(LocalColumnActivity.this,
						LocalSongListActivity.class);
				currentItemTitle = adapter.getCurrentItemTitleByPostion(i);
				currentItemSingerId = adapter.getCurrentItemIdByPostion(i);
				if (currentItemTitle == null
						|| currentItemTitle.equals("<unknown>"))
					currentItemTitle = (new StringBuffer(
							getText(R.string.unknown_artist_name_db_controller)))
							.toString();
				intent.putExtra("title", currentItemTitle);
				intent.putExtra("type", 2);
				intent.putExtra("songid", currentItemSingerId);
				startActivity(intent);
				break;
			case 2: // 目录浏览
				intent = new Intent(LocalColumnActivity.this,
						LocalSongListActivity.class);
				intent.putExtra("title", mTile[i]);
				intent.putExtra("type", 3);
				intent.putExtra("folderpath", mTile[i]);
				startActivity(intent);
				break;
			}
		}
	};

	private void refreshUI()
	{
		logger.v("refreshUI() ---> Enter");
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

	public void handleSystemEvent(Message paramMessage)
	{
		logger.v("handleSystemEvent() ---> Enter");
		switch (paramMessage.what)
		{
		default:
			logger.v("handleSystemEvent() ---> Exit");
			return;
		case 4:
			finish();
			break;
		case 5:
			refreshUI();
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
		setContentView(R.layout.activity_local_column_layout);
		mPlayerStatusBar = (PlayerStatusBar) findViewById(R.id.playerStatusBar);
		mTitleBar = (TitleBarView) findViewById(R.id.title_view);
		mTitleBar.setCurrentActivity(this);
		mTitleBar.setButtons(0);
		mController = ((MobileMusicApplication) getApplication())
				.getController();
		mDBController = mController.getDBController();
		mController.addSystemEventListener(4, this);
		mController.addSystemEventListener(22, this);
		Intent intent1 = getIntent();
		if (intent1.getStringExtra("title") != null)
			mTitleBar.setTitle(intent1.getStringExtra("title"));
		mType = intent1.getIntExtra("TYPE", 0);
		AsyncTaskGetDateFromDb asynctaskgetdatefromdb = new AsyncTaskGetDateFromDb();
		Integer ainteger[] = new Integer[1];
		ainteger[0] = Integer.valueOf(mType);
		asynctaskgetdatefromdb.execute(ainteger);
		logger.v("onCreate() ---> Exit");
	}

	protected void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		this.mController.removeSystemEventListener(4, this);
		this.mController.removeSystemEventListener(22, this);
		super.onDestroy();
		logger.v("onDestroy() ---> Exit");
	}

	protected void onPause()
	{
		logger.v("onPause() ---> Enter");
		this.mPlayerStatusBar.unRegistEventListener();
		this.mController.removeSystemEventListener(5, this);
		super.onPause();
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		this.mPlayerStatusBar.registEventListener();
		refreshUI();
		this.mController.addSystemEventListener(5, this);
		super.onResume();
		logger.v("onResume() ---> Exit");
	}

	private class AsyncTaskGetDateFromDb extends
			AsyncTask<Integer, Void, Cursor>
	{
		boolean START_ACTIVITY = false;

		private AsyncTaskGetDateFromDb()
		{}

		protected Cursor doInBackground(Integer[] paramArrayOfInteger)
		{
			logger.v("doInBackground() ----> Enter");
			int i = paramArrayOfInteger[0].intValue();
			Cursor cursor = null;
			switch (i)
			{
			default:
				return cursor;
			case 1: // 歌手浏览
				logger.v("1");
				cursor = mDBController
						.getArtistsByCursor(UIGlobalSettingParameter.localmusic_folder_names);
				if (cursor == null || cursor.getCount() <= 0)
				{
					logger.v("getArtists is null, the cursor == null");
					return cursor;
				} else
				{
					int i2 = cursor.getColumnIndex("_id");
					int j2 = 0;
					mSubTile = new int[cursor.getCount()];
					while (cursor.moveToNext())
					{
						mSubTile[j2] = mDBController
								.getAllSongsCountByFolderAndSinger(
										UIGlobalSettingParameter.localmusic_folder_names,
										cursor.getInt(i2),
										UIGlobalSettingParameter.localmusic_scan_smallfile);
						j2++;
					}
					cursor.moveToPosition(-1);
					return cursor;
				}
			case 2: // 目录浏览
				ArrayList arraylist = new ArrayList();
				String as[];
				mTile = UIGlobalSettingParameter.localmusic_folder_names;
				as = mTile;
				cursor = null;
				if (as == null)
				{
					return cursor;
				} else
				{
					int j = mTile.length;
					if (j <= 0)
					{
						return cursor;
					} else
					{
						int k = 0;
						String as1[] = mTile;
						mSubTile = new int[mTile.length];
						for (int i1 = 0; i1 < as1.length; i1++)
						{
							String s = as1[i1];
							int j1 = mDBController
									.getAllSongsCountByFolder(
											new String[] { s },
											UIGlobalSettingParameter.localmusic_scan_smallfile);
							if (j1 != 0)
							{
								arraylist.add(s);
								mSubTile[k] = j1;
								k++;
							}
						}
						int k1 = 0;
						Iterator iterator = arraylist.iterator();
						mTile = new String[arraylist.size()];
						boolean flag;
						while (iterator.hasNext())
						{
							String s1 = (String) iterator.next();
							String as2[] = mTile;
							int l1 = k1 + 1;
							as2[k1] = s1;
							k1 = l1;
						}
						return cursor;
					}
				}
			}
		}

		protected void onPostExecute(Cursor paramCursor)
		{
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			setAdapterAfterAsyncTask(paramCursor);
		}
	}

	private class MobileMusicLocalColumnListAdapter extends BaseAdapter
	{
		private Context mContext;
		private LayoutInflater mInflater;

		public MobileMusicLocalColumnListAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
			this.mContext = context;
		}

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
			String s;
			LocalColumnActivity localcolumnactivity;
			Object aobj[];
			String s1;
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
			s = mTile[i];
			if (s == null || s.equals("<unknown>"))
				viewholder.column_tile.setText(mContext
						.getText(R.string.unknown_artist_name_db_controller));
			else
				viewholder.column_tile.setText(s);
			localcolumnactivity = LocalColumnActivity.this;
			aobj = new Object[1];
			aobj[0] = Integer.valueOf(mSubTile[i]);
			s1 = localcolumnactivity.getString(R.string.local_music_subtile,
					aobj);
			if (s1 == null || s1.equals("<unknown>"))
				viewholder.column_subtile.setText(mContext
						.getText(R.string.unknown_artist_name_db_controller));
			else
				viewholder.column_subtile.setText(s1);
			return view;
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