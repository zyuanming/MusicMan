package org.ming.ui.activity.online;

import org.ming.R;
import org.ming.util.MyLogger;

import android.app.Activity;
import android.os.Bundle;

public class OnlineMusicActivity extends Activity
{
	private static final MyLogger logger = MyLogger
			.getLogger("MusicOnlineMusicTempActivity");

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setContentView(R.layout.activity_online_music_temp_layout);
	}
}
