package org.ming.ui.activity.local;

import java.util.ArrayList;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.Song;
import org.ming.ui.view.LocalSongListView;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

public class LocalMusicSearchActivity extends Activity
{
	public static final MyLogger logger = MyLogger
			.getLogger("LocalMusicSearchActivity");
	private Controller mController;
	private DBController mDBController;
	private ImageView mNoDataView;
	private LocalSongListView mLocalSongListView;
	private Button mSearchBtn;
	private EditText mSearchContent;
	private ArrayList<Song> mSong;
	private TitleBarView mTitleView;
	private View.OnClickListener onSearchListener = new View.OnClickListener()
	{
		public void onClick(View view)
		{
			LocalMusicSearchActivity.logger
					.v("onSearchListener---onClick() ---> Enter");
			String s = mSearchContent.getText().toString().trim();
			if (s == null || s.equals(""))
			{
				Toast.makeText(LocalMusicSearchActivity.this,
						R.string.please_input_search_content_search_activity, 0)
						.show();
			} else
			{
				mSong = (ArrayList) mDBController.getSongByKey(s);
				if (mSong != null && mSong.size() > 0)
				{
					mNoDataView.setVisibility(8);
					mLocalSongListView.setVisibility(0);
					mLocalSongListView.addSongList(mSong);
				} else
				{
					mLocalSongListView.setVisibility(8);
					mNoDataView.setVisibility(0);
					mNoDataView
							.setImageResource(R.drawable.image_local_nothing);
				}
				hideInputMethod();
				LocalMusicSearchActivity.logger
						.v("onSearchListener---onClick() ---> Exit");
			}
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
		this.mLocalSongListView = (LocalSongListView) findViewById(0x7f050013);
		this.mSearchBtn = ((Button) findViewById(R.id.local_music_btn_search));
		this.mSearchBtn.setOnClickListener(this.onSearchListener);
		this.mTitleView.setCurrentActivity(this);
		this.mTitleView.setButtons(0);
		this.mTitleView.setTitle(R.string.local_music_search_by_key);
		logger.v("onCreate() ---> Exit");
	}

	protected void onPause()
	{
		super.onPause();
		mLocalSongListView.removeEventListner();
		MobclickAgent.onPause(this);
	}

	protected void onResume()
	{
		super.onResume();
		mLocalSongListView.addEventListner();
		MobclickAgent.onResume(this);
	}
}