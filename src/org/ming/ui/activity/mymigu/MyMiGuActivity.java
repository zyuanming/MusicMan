package org.ming.ui.activity.mymigu;

import org.ming.R;
import org.ming.util.MyLogger;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MyMiGuActivity extends TabActivity
{
	private static final MyLogger logger = MyLogger
			.getLogger("MusicOnlineMusicTempActivity");
	private Dialog mCurrentDialog = null;
	private LayoutInflater mInflater;
	private TabHost mTabHost;
	private int[] mTabTitleID = { R.string.title_my_collection_activity,
			R.string.title_business_order_activity,
			R.string.title_music_management_activity };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_migu_layout);
		initTab();
	}

	private void initTab()
	{
		this.mInflater = LayoutInflater.from(this);
		this.mTabHost = ((TabHost) findViewById(android.R.id.tabhost));
		this.mTabHost.setup(getLocalActivityManager());
		this.mTabHost.setCurrentTab(0);
		this.mTabHost.getTabWidget().setOnClickListener(
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						Toast.makeText(
								MyMiGuActivity.this.getApplicationContext(),
								"click", Toast.LENGTH_SHORT).show();
					}
				});
		for (int i = 0; i < this.mTabTitleID.length; i++)
		{
			// Intent localIntent;
			// if (this.mTabTitleID[i] == R.string.title_my_collection_activity)
			// {
			// localIntent = new Intent(this, MyMiGuMyCollectActivity.class);
			// } else if (this.mTabTitleID[i] ==
			// R.string.title_music_management_activity)
			// {
			// localIntent = new Intent(this,
			// MyMiGuMusicManagementActivity.class);
			// } else
			// {
			// localIntent = new Intent(this,
			// MyMiGuBusinessOrderActivity.class);
			// }
			// TabHost.TabSpec localTabSpec =
			// this.mTabHost.newTabSpec("TAB_TITLE"
			// + i);
			// View localView = this.mInflater.inflate(
			// R.layout.my_migu_navigation_lable, null);
			// localTabSpec.setIndicator(localView);
			// localTabSpec.setContent(localIntent);
			// this.mTabHost.addTab(localTabSpec);
			// ((TextView) localView.findViewById(2131034455))
			// .setText(this.mTabTitleID[i]);
		}

	}
}
