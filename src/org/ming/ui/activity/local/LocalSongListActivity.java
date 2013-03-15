package org.ming.ui.activity.local;

import java.io.File;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.Song;
import org.ming.center.player.PlayerController;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.UIEventListener;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.view.LocalSongListViewByCursor;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LocalSongListActivity extends Activity implements UIEventListener,
		SystemEventListener
{
	public static final int CURSOR_ALL_SONG = 0;
	private static final int CURSOR_REQUERY_AFTER_DELETE = 1;
	public static final int CURSOR_SONG_BY_FOLDERPATH = 3;
	public static final int CURSOR_SONG_BY_PLAYLIST = 4;
	public static final int CURSOR_SONG_BY_SINGERID = 2;
	public static final String FOLDERPATH = "folderpath";
	public static final String LAUNCHTYPE = "type";
	public static final String PLAYLISTID = "playlistid";
	public static final String SONGID = "songid";
	public static final MyLogger logger = MyLogger
			.getLogger("LocalSongListActivity");
	private final int CONTEXT_MENU_DELETE_LOCAL_MUSIC = 0;
	private final int CONTEXT_MENU_REMOVE_LOCAL_MUSIC = 2;
	private final int CONTEXT_MENU_RENAME_LOCAL_MUSIC = 1;
	private Controller mController = null;
	private Dialog mCurrentDialog;
	private DBController mDBController = null;
	private int mLaunchType = 0;
	private LocalSongListViewByCursor mLocalSongListView;
	private PlayerController mPlayerController = null;
	private int mPlaylistId = 0;
	private Song mSong;
	private TitleBarView mTitleBar;
	private long[] songids;
	private View.OnClickListener mCreatePlayListOnClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			logger.v("mCreatePlayListOnClickListener----onclick() ---> in");
			Intent localIntent = new Intent(LocalSongListActivity.this,
					LocalAddMusicMainActivity.class);
			localIntent.putExtra("Selectedmusic", songids);
			localIntent.putExtra("playlistid", mPlaylistId);
			startActivity(localIntent);
			logger.v("mCreatePlayListOnClickListener----onclick() ---> Exit");
		}
	};

	private void queryHandlerQueryComplete(Cursor paramCursor)
	{
		logger.v("queryHandlerQueryComplete() ---> Enter");
		this.mLocalSongListView.setCursor(paramCursor);
		// 设置歌曲长按弹出---删除---重命名的菜单
		this.mLocalSongListView
				.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
				{
					public void onCreateContextMenu(ContextMenu contextMenu,
							View paramAnonymousView,
							ContextMenu.ContextMenuInfo contextMenuInfo)
					{
						AdapterView.AdapterContextMenuInfo localAdapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
						mSong = mLocalSongListView
								.getSongbyPosition(localAdapterContextMenuInfo.position);
						contextMenu.setHeaderTitle(mSong.mTrack);
						if (mPlaylistId > 0)
						{
							contextMenu.add(0, 2, 0,
									R.string.local_music_remove);
						} else
						{
							contextMenu.add(0, 0, 0, R.string.delete_common);
							contextMenu.add(1, 1, 0,
									R.string.local_music_rename);
						}
					}
				});
		logger.v("queryHandlerQueryComplete() ---> Exit");
	}

	private void refreshUI()
	{
		logger.v("refreshUI() ---> Enter");
		Intent localIntent = getIntent();
		this.mLaunchType = getIntent().getIntExtra("type", 0);
		asyncGetCursor localasyncGetCursor = new asyncGetCursor();
		Integer[] arrayOfInteger = new Integer[1];
		arrayOfInteger[0] = Integer.valueOf(this.mLaunchType);

		// 开始异步任务
		localasyncGetCursor.execute(arrayOfInteger);

		this.mPlaylistId = localIntent.getIntExtra(PLAYLISTID, 0);
		if (localIntent.getStringExtra("title") != null)
			this.mTitleBar.setTitle(localIntent.getStringExtra("title"));
		if (this.mPlaylistId > 0)
		{
			this.mTitleBar.setButtons(2);
			ImageView localImageView = (ImageView) this.mTitleBar
					.findViewById(R.id.btn_rightbutton);
			this.mTitleBar
					.setRightBtnImage(R.drawable.btn_titlebar_add_music_selector);
			localImageView
					.setOnClickListener(this.mCreatePlayListOnClickListener);
		} else
		{
			this.mTitleBar.setButtons(0);
		}
		logger.v("refreshUI() ---> Exit");

	}

	// 重命名歌曲
	private void show2BtnDialogWithEditTextView()
	{
		logger.v("show2BtnDialogWithIconTitleView() ---> Enter");
		final Dialog localDialog = new Dialog(this, R.style.CustomDialogTheme);
		View localView = LayoutInflater.from(this).inflate(
				R.layout.dialog_title_edittext_two_button, null);
		final EditText localEditText = (EditText) localView
				.findViewById(R.id.new_name);
		Button localButton1 = (Button) localView.findViewById(R.id.button1);
		Button localButton2 = (Button) localView.findViewById(R.id.button2);
		localButton1.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				new File(mSong.mUrl).renameTo(new File(""));
				String str = localEditText.getText().toString();
				if ((str == null) || ("".equals(str.trim())))
				{
					Toast.makeText(LocalSongListActivity.this,
							getText(R.string.local_music_rename_not_empty), 1)
							.show();
				} else
				{
					mLocalSongListView.renameSongFromList(mSong, str);
					mDBController.renameLocalSong(mSong.mId, str);
					if (mDBController.getSongIdByPath(mSong.mUrl) != -1L)
						mDBController.renameDownloadMusic(mSong.mUrl, str);
				}
				localDialog.dismiss();
			}
		});
		localButton2.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				localDialog.dismiss();
			}
		});
		localDialog.setContentView(localView);
		localDialog.show();
		logger.v("show2BtnDialogWithIconTitleView() ---> Exit");
	}

	public void handleSystemEvent(Message paramMessage)
	{
		logger.v("handleSystemEvent() ---> Enter");
		switch (paramMessage.what)
		{
		case 4:
			finish();
			break;
		case 5:
			refreshUI();
			break;
		case 22:
			finish();
		default:
		}
		logger.v("handleSystemEvent() ---> Exit");
	}

	public void handleUIEvent(Message paramMessage)
	{
		logger.v("handleUIEvent() ---> Enter");
		logger.v("handleUIEvent() ---> Exit");
	}

	// 处理--->重命名--->删除本地歌曲
	public boolean onContextItemSelected(MenuItem paramMenuItem)
	{

		switch (paramMenuItem.getItemId())
		{

		case CONTEXT_MENU_DELETE_LOCAL_MUSIC:
		{
			if ((this.mPlayerController.getCurrentPlayingItem() == null)
					|| (this.mPlayerController.getCurrentPlayingItem().mId != this.mSong.mId))
			{
				this.mCurrentDialog = DialogUtil
						.show2BtnDialogWithIconTitleMsg(this,
								getText(R.string.local_music_delete),
								getText(R.string.local_music_delete_content),
								new View.OnClickListener()
								{
									public void onClick(View paramAnonymousView)
									{
										new File(mSong.mUrl).delete();
										mDBController
												.deleteSongFromDB(mSong.mId);
										mLocalSongListView
												.deleteSongFromList(mSong);
										if (mDBController
												.getSongIdByPath(mSong.mUrl) != -1L)
											mDBController
													.deleteDBDlItemByPath(mSong.mUrl);
										if (mCurrentDialog != null)
										{
											mCurrentDialog.dismiss();
											mCurrentDialog = null;
										}
										mPlayerController.delOnlineSong(mSong);
										refreshUI();
									}
								}, new View.OnClickListener()
								{
									public void onClick(View paramAnonymousView)
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
				// 歌曲正在播放，不能进行此操作
				Toast.makeText(this, R.string.local_music_undo, 1).show();
			}
		}
			break;
		case CONTEXT_MENU_RENAME_LOCAL_MUSIC:
		{
			if ((this.mPlayerController.getCurrentPlayingItem() == null)
					|| (this.mPlayerController.getCurrentPlayingItem().mId != this.mSong.mId))
			{
				show2BtnDialogWithEditTextView();
			} else
			{
				Toast.makeText(this, R.string.local_music_undo, 1).show();
				if ((this.mPlayerController.getCurrentPlayingItem() == null)
						|| (this.mPlayerController.getCurrentPlayingItem().mId != this.mSong.mId))
				{
					this.mDBController.deleteSongFromPlaylist(this.mPlaylistId,
							this.mSong.mId, 1);
					refreshUI();
				} else
				{
					Toast.makeText(this, R.string.local_music_undo, 1).show();
				}
			}
		}
			break;
		case CONTEXT_MENU_REMOVE_LOCAL_MUSIC:
			break;
		}

		return super.onContextItemSelected(paramMenuItem);
	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_local_songlist_layout);
		mTitleBar = ((TitleBarView) findViewById(R.id.title_view));
		mTitleBar.setCurrentActivity(this);
		mTitleBar.setLeftBtnOnlickListner(null);
		mLocalSongListView = ((LocalSongListViewByCursor) findViewById(R.id.playerlist_songlistview));
		mController = ((MobileMusicApplication) getApplication())
				.getController();
		mDBController = mController.getDBController();
		mPlayerController = mController.getPlayerController();
		mController.addUIEventListener(4012, this);
		mController.addSystemEventListener(22, this);
		mController.addSystemEventListener(4, this);
		logger.v("onCreate() ---> Exit");
	}

	protected void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		this.mController.removeUIEventListener(4012, this);
		this.mController.removeSystemEventListener(22, this);
		this.mController.removeSystemEventListener(4, this);
		super.onDestroy();
		logger.v("onDestroy() ---> Exit");
	}

	protected void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		MobclickAgent.onPause(this);
		this.mLocalSongListView.removeEventListner();
		this.mController.removeSystemEventListener(5, this);
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		MobclickAgent.onResume(this);
		this.mLocalSongListView.addEventListner();
		refreshUI();
		this.mController.addSystemEventListener(5, this);
		this.mLocalSongListView
				.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
				{
					public void onCreateContextMenu(ContextMenu contextMenu,
							View paramAnonymousView,
							ContextMenu.ContextMenuInfo contextMenuInfo)
					{
						AdapterView.AdapterContextMenuInfo localAdapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
						mSong = mLocalSongListView
								.getSongbyPosition(localAdapterContextMenuInfo.position);
						contextMenu.setHeaderTitle(mSong.mTrack);
						if (mPlaylistId > 0)
							contextMenu.add(0, 2, 0,
									R.string.local_music_remove);
						contextMenu.add(0, 0, 0, R.string.delete_common);
						contextMenu.add(1, 1, 0, R.string.local_music_rename);
					}
				});
		logger.v("onResume() ---> Exit");
	}

	class asyncGetCursor extends AsyncTask<Integer, Void, Cursor>
	{
		int type = 0;

		asyncGetCursor()
		{}

		protected Cursor doInBackground(Integer[] ai)
		{
			logger.d("asyncGetCursor.doInBackground() ----> enter");
			asyncGetCursor.this.type = ai[0].intValue();
			int i = asyncGetCursor.this.type;
			logger.d("asyncGetCursor.type --->" + i);
			Cursor localCursor = null;
			localCursor = mDBController.getSongsCursorByFolder(
					UIGlobalSettingParameter.localmusic_folder_names,
					UIGlobalSettingParameter.localmusic_scan_smallfile);
			switch (i)
			{
			default:
			case CURSOR_ALL_SONG:
				// 全部歌曲
				songids = new long[localCursor.getCount()];

				int j = 0;
				int k = localCursor.getColumnIndex("_id");
				if (!localCursor.moveToNext())
				{
					localCursor.moveToPosition(-1);
				}
				long[] arrayOfLong1 = songids;
				int m = j + 1;
				arrayOfLong1[j] = localCursor.getInt(k);
				j = m;
				break;
			case CURSOR_REQUERY_AFTER_DELETE:
				songids = new long[localCursor.getCount()];
				int i6 = 0;
				int i7 = localCursor.getColumnIndex("_id");
				if (!localCursor.moveToNext())
				{
					localCursor.moveToPosition(-1);
				}
				long[] arrayOfLong4 = songids;
				int i8 = i6 + 1;
				arrayOfLong4[i6] = localCursor.getInt(i7);
				i6 = i8;
				new File(mSong.mUrl).delete();
				mDBController.deleteSongFromDB(mSong.mId);
				boolean bool = mDBController.getSongIdByPath(mSong.mUrl) < -1L;
				localCursor = null;
				if (bool)
				{
					mDBController.deleteDBDlItemByPath(mSong.mUrl);
					localCursor = null;
				}
				break;
			case CURSOR_SONG_BY_SINGERID:
				// 按歌手浏览
				localCursor = mDBController.getSongsCursorByFolderAndSinger(
						UIGlobalSettingParameter.localmusic_folder_names,
						getIntent().getIntExtra("songid", 0),
						UIGlobalSettingParameter.localmusic_scan_smallfile);
				break;
			case CURSOR_SONG_BY_FOLDERPATH:
				// 按目录浏览
				songids = new long[localCursor.getCount()];

				int i3 = 0;
				int i4 = localCursor.getColumnIndex("_id");
				if (!localCursor.moveToNext())
				{
					localCursor.moveToPosition(-1);
				}
				long[] arrayOfLong3 = songids;
				int i5 = i3 + 1;
				arrayOfLong3[i3] = localCursor.getInt(i4);
				i3 = i5;
				String[] arrayOfString = new String[1];
				arrayOfString[0] = getIntent().getStringExtra("folderpath");
				localCursor = mDBController.getSongsCursorByFolder(
						arrayOfString,
						UIGlobalSettingParameter.localmusic_scan_smallfile);
				break;
			case CURSOR_SONG_BY_PLAYLIST:
				// 根据播放列表查看
				songids = new long[localCursor.getCount()];

				int n = 0;
				int i1 = localCursor.getColumnIndex("_id");
				if (!localCursor.moveToNext())
				{
					localCursor.moveToPosition(-1);
				}
				long[] arrayOfLong2 = songids;
				int i2 = n + 1;
				arrayOfLong2[n] = localCursor.getInt(i1);
				n = i2;
				localCursor = mDBController.getCursorFromPlaylist(getIntent()
						.getIntExtra("playlistid", 0), 1);
			}
			logger.d("asyncGetCursor.doInBackground() ----> exit");
			return localCursor;
		}

		@Override
		protected void onPostExecute(Cursor paramCursor)
		{
			queryHandlerQueryComplete(paramCursor);
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
		}
	}
}