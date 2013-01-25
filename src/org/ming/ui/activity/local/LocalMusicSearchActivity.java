package org.ming.ui.activity.local;

import java.util.ArrayList;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.util.MyLogger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LocalMusicSearchActivity extends Activity
{
	public static final MyLogger logger = MyLogger
			.getLogger("LocalMusicSearchActivity");
	private Controller mController;
	private DBController mDBController;
	private LocalSongListView mLocalSongListView;
	private ImageView mNoDataView;
	private Button mSearchBtn;
	private EditText mSearchContent;
	private ArrayList<Song> mSong;
	private TitleBarView mTitleView;
	private View.OnClickListener onSearchListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			LocalMusicSearchActivity.logger
					.v("onSearchListener---onClick() ---> Enter");
			String str = LocalMusicSearchActivity.this.mSearchContent.getText()
					.toString().trim();
			if ((str == null) || (str.equals("")))
			{
				Toast.makeText(LocalMusicSearchActivity.this, 2131165462, 0)
						.show();
				return;
			}
			LocalMusicSearchActivity.this.mSong = ((ArrayList) LocalMusicSearchActivity.this.mDBController
					.getSongByKey(str));
			if ((LocalMusicSearchActivity.this.mSong != null)
					&& (LocalMusicSearchActivity.this.mSong.size() > 0))
			{
				LocalMusicSearchActivity.this.mNoDataView.setVisibility(8);
				LocalMusicSearchActivity.this.mLocalSongListView
						.setVisibility(0);
				LocalMusicSearchActivity.this.mLocalSongListView
						.addSongList(LocalMusicSearchActivity.this.mSong);
			}
			LocalMusicSearchActivity.this.hideInputMethod();
			LocalMusicSearchActivity.logger
					.v("onSearchListener---onClick() ---> Exit");
			LocalMusicSearchActivity.this.mLocalSongListView.setVisibility(8);
			LocalMusicSearchActivity.this.mNoDataView.setVisibility(0);
			LocalMusicSearchActivity.this.mNoDataView
					.setImageResource(R.drawable.image_local_nothing);
		}
	};

	protected void hideInputMethod()
	{
		logger.v("hideInputMethod ---> Enter");
		InputMethodManager localInputMethodManager = (InputMethodManager) getSystemService("input_method");
		if ((localInputMethodManager != null) && (getCurrentFocus() != null))
			localInputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), 0);
		logger.v("hideInputMethod ---> Exit");
	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_local_music_search_layout);
		this.mController = ((MobileMusicApplication) getApplication())
				.getController();
		this.mDBController = this.mController.getDBController();
		this.mNoDataView = ((ImageView) findViewById(R.id.local_music_search_no_data));
		this.mTitleView = ((TitleBarView) findViewById(R.id.local_music_search_title_view));
		this.mSearchContent = ((EditText) findViewById(R.id.local_music_search_edit_text));
		this.mLocalSongListView = ((LocalSongListView) findViewById(R.id.local_music_search_playerlist_songlistview));
		this.mSearchBtn = ((Button) findViewById(R.id.local_music_btn_search));
		this.mSearchBtn.setOnClickListener(this.onSearchListener);
		this.mTitleView.setCurrentActivity(this);
		this.mTitleView.setButtons(0);
		this.mTitleView.setTitle(R.string.local_music_search_by_key);
		logger.v("onCreate() ---> Exit");
	}

	protected void onPause()
	{
		this.mLocalSongListView.removeEventListner();
		super.onPause();
	}

	protected void onResume()
	{
		this.mLocalSongListView.addEventListner();
		super.onResume();
	}
}