package org.ming.ui.activity.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.ui.activity.MobileMusicMainActivity;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class LocalScanMusicActivity extends ListActivity implements
		View.OnClickListener, SystemEventListener {
	public static final MyLogger logger = MyLogger
			.getLogger("LocalScanMusicActivity");
	private List<String> mAllFolders = new ArrayList();
	private Button mButton_OK;
	private Button mCancel;
	private Controller mController = null;
	private Dialog mCurrentDialog = null;
	private DBController mDBController = null;
	private boolean mIsFromMusicPlayPage = false;
	private boolean mIsSelectAll = false;
	private MobileMusicScanMusicListItemAdapter mMobileMusicScanMusicListItemAdapter;
	private Button mSelectAll;
	private CheckBox mSelectCheckBoxForSmallSong = null;
	private List<String> mSelectFolders = new ArrayList();
	private TitleBarView mTitleBar;

	private void refreshUI(File paramFile) {
		logger.v("refreshUI() ---> Enter");
		mAllFolders.clear();

		for (Iterator iterator1 = mDBController.getSongFolder().iterator(); iterator1
				.hasNext();) {
			// logger.v(iterator1.next().toString());
			String str1 = (String) iterator1.next();

			mAllFolders.add(str1);

		}
		if (mAllFolders != null) {
			for (Iterator iterator2 = mAllFolders.iterator(); iterator2
					.hasNext();) {
				String str2 = (String) iterator2.next();
				this.mSelectFolders.add(str2);
			}

		}
		if (UIGlobalSettingParameter.localmusic_folder_names != null) {
			for (String str3 : UIGlobalSettingParameter.localmusic_folder_names)
				mSelectFolders.add(str3);
		}
		if (mSelectFolders.size() == mAllFolders.size()) {
			mSelectAll.setVisibility(View.GONE);
			mCancel.setVisibility(View.VISIBLE);
		} else {
			mSelectAll.setVisibility(View.VISIBLE);
			mCancel.setVisibility(View.GONE);
		}
		mMobileMusicScanMusicListItemAdapter = new MobileMusicScanMusicListItemAdapter(
				this, mAllFolders);
		setListAdapter(mMobileMusicScanMusicListItemAdapter);
		logger.v("refreshUI() ---> Exit");
	}

	public void handleSystemEvent(Message paramMessage) {
		logger.v("handleSystemEvent() ---> Enter");
		switch (paramMessage.what) {
		default:
		case 4:
			finish();
		}
		logger.v("handleSystemEvent() ---> Exit");

	}

	// 开始扫描指定目录里的音乐文件
	public void onClick(View paramView) {
		logger.v("onClick() ---> Enter");
		switch (paramView.getId()) {
		default:
			break;
		case R.id.local_scan_music_ok_button: {
			logger.d("click the ok button to scan the target folder to find music");
			if (this.mSelectFolders.size() > 0) {
				String[] arrayOfString1 = new String[this.mSelectFolders.size()];
				String[] arrayOfString2 = (String[]) this.mSelectFolders
						.toArray(arrayOfString1);
				String str1 = new String();
				int i = arrayOfString2.length;
				for (int j = 0; j < i; j++) {
					String str2 = arrayOfString2[j];
					str1 = (new StringBuilder(
							String.valueOf((new StringBuilder(String
									.valueOf(str1))).append(str2).toString())))
							.append(";").toString();

				}
				UIGlobalSettingParameter.localmusic_folder_names = arrayOfString2;
				UIGlobalSettingParameter.localmusic_scan_warningdlg = false;
				boolean bool = mSelectCheckBoxForSmallSong.isChecked();
				if (bool != UIGlobalSettingParameter.localmusic_scan_smallfile) {
					UIGlobalSettingParameter.localmusic_scan_smallfile = bool;
					mDBController.setScanSmallSongFile(Boolean.valueOf(bool));
				}
				mDBController.setLocalFolder(str1);
				if (mIsFromMusicPlayPage) {
					Intent localIntent = new Intent(this,
							MobileMusicMainActivity.class);
					localIntent.putExtra("isFromLocalScan", true);
					startActivity(localIntent);
					finish();
				} else {
					finish();
				}
			} else {
				mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
						getText(R.string.title_information_common),
						getText(R.string.local_music_select_folder_warning),
						new View.OnClickListener() {
							public void onClick(View paramAnonymousView) {
								if (mCurrentDialog != null) {
									mCurrentDialog.dismiss();
									mCurrentDialog = null;
								}
							}
						});
			}
		}
		}
		logger.v("onClick() ---> Exit");
	}

	protected void onCreate(Bundle paramBundle) {
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_local_scan_music_layout);
		this.mController = ((MobileMusicApplication) getApplication())
				.getController();
		this.mDBController = this.mController.getDBController();
		this.mTitleBar = ((TitleBarView) findViewById(R.id.title_view));
		this.mSelectAll = ((Button) findViewById(R.id.local_scan_all_music_checkall));
		this.mSelectAll.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				mIsSelectAll = true;
				mSelectFolders.clear();
				mSelectFolders.addAll(mAllFolders);
				mMobileMusicScanMusicListItemAdapter.notifyDataSetChanged();
				mSelectAll.setVisibility(View.GONE);
				mCancel.setVisibility(View.VISIBLE);
			}
		});
		this.mCancel = ((Button) findViewById(R.id.local_scan_music_cancel));
		this.mCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				mIsSelectAll = false;
				mSelectFolders.clear();
				mMobileMusicScanMusicListItemAdapter.notifyDataSetChanged();
				mSelectAll.setVisibility(View.VISIBLE);
				mCancel.setVisibility(View.GONE);
			}
		});
		mSelectCheckBoxForSmallSong = ((CheckBox) findViewById(R.id.local_scan_music_checkall));
		mTitleBar.setCurrentActivity(this);
		mTitleBar.setTitle(R.string.local_music_scan_activity_tile);
		mTitleBar.setButtons(0);
		mButton_OK = ((Button) findViewById(R.id.local_scan_music_ok_button));
		mButton_OK.setOnClickListener(this);
		mController.addSystemEventListener(4, this);

		// 判断是否是从播放音乐界面跳转到这个Activity
		Bundle localBundle = getIntent().getExtras();
		if (localBundle != null) {
			mIsFromMusicPlayPage = localBundle.getBoolean(
					"isFromMusicPlayPage", false);
		}
		logger.v("onCreate() ---> Enter");
	}

	protected void onDestroy() {
		logger.v("onDestroy() ---> Enter");
		this.mController.removeSystemEventListener(4, this);
		super.onDestroy();
		logger.v("onDestroy() ---> Exit");
	}

	protected void onListItemClick(ListView paramListView, View paramView,
			int paramInt, long paramLong) {
		logger.v("onListItemClick() ---> Enter");
		super.onListItemClick(paramListView, paramView, paramInt, paramLong);
	}

	protected void onPause() {
		logger.v("onPause() ---> Enter");
		super.onPause();
		logger.v("onPause() ---> Exit");
	}

	protected void onResume() {
		logger.v("onResume() ---> Enter");
		super.onResume();
		refreshUI(null);
		logger.v("onResume() ---> Exit");
	}

	private class MobileMusicScanMusicListItemAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<String> mList;
		final LocalScanMusicActivity localScanMusicActivity;

		public MobileMusicScanMusicListItemAdapter(
				LocalScanMusicActivity localscanmusicactivity1, List list) {
			super();
			localScanMusicActivity = LocalScanMusicActivity.this;

			mInflater = LayoutInflater.from(localscanmusicactivity1);
			mList = list;
		}

		public int getCount() {
			return this.mList.size();
		}

		public Object getItem(int paramInt) {
			return this.mList.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return (long) paramInt;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			ViewHolder localViewHolder;
			if (paramView == null) {
				localViewHolder = new ViewHolder();
				paramView = mInflater.inflate(
						R.layout.local_scan_music_list_cell, null);
				localViewHolder.mScanDirectory = ((TextView) paramView
						.findViewById(R.id.scan_directory));
				localViewHolder.mCheckBox = ((CheckBox) paramView
						.findViewById(android.R.id.checkbox));
				paramView.setTag(localViewHolder);

			} else {
				localViewHolder = (ViewHolder) paramView.getTag();
			}

			localViewHolder.mScanDirectory.setText((CharSequence) this.mList
					.get(paramInt));
			if ((!LocalScanMusicActivity.this.mIsSelectAll)
					&& (!LocalScanMusicActivity.this.mSelectFolders
							.contains(this.mList.get(paramInt)))) {
				localViewHolder.mCheckBox.setChecked(false);
			} else {
				localViewHolder.mCheckBox.setChecked(true);
			}

			localViewHolder.mCheckBox.setTag(mList.get(paramInt));
			localViewHolder.mCheckBox
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View paramAnonymousView) {
							String s = (String) paramAnonymousView.getTag();
							if (mSelectFolders.contains(s)) {
								mSelectFolders.remove(s);
								mIsSelectAll = false;
							} else {
								mSelectFolders.add(s);
								if (mSelectFolders.size() == mList.size())
									mIsSelectAll = true;
							}
							if (mList.size() == mSelectFolders.size()) {
								mSelectAll.setVisibility(8);
								mCancel.setVisibility(0);
							} else {
								mSelectAll.setVisibility(0);
								mCancel.setVisibility(8);
							}
						}
					});
			return paramView;
		}

		public final class ViewHolder {
			public CheckBox mCheckBox;
			public TextView mScanDirectory;

			public ViewHolder() {
			}
		}
	}
}