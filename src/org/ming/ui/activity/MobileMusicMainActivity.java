package org.ming.ui.activity;

import org.ming.R;
import org.ming.ui.activity.local.LocalMusicActivity;
import org.ming.ui.activity.mymigu.MyMiGuActivity;
import org.ming.ui.activity.online.OnlineMusicActivity;
import org.ming.ui.util.DialogUtil;
import org.ming.util.MyLogger;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;

public class MobileMusicMainActivity extends TabActivity
{
	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicMainActivity");
	private Dialog ShortCutDialog;
	AlarmManager alarmManager;
	private LayoutInflater mInflater;
	private boolean mIsFromLocalScan = false;
	private int mLastCurrentTab;
	private TabHost.OnTabChangeListener mOnTabChangeListener = new TabHost.OnTabChangeListener()
	{
		public void onTabChanged(String paramAnonymousString)
		{
			MobileMusicMainActivity.logger.v("onTabChanged() ---> Enter");
			MobileMusicMainActivity.this.requestRoot = true;
			if (paramAnonymousString.equalsIgnoreCase("TAB_MIGU"))
			{
				MobileMusicMainActivity.logger.v("onTabChanged() ---> Exit");
				MobileMusicMainActivity.this.mLastCurrentTab = MobileMusicMainActivity.this.mTabHost
						.getCurrentTab();
				Intent localIntent2 = MobileMusicMainActivity.this.getIntent();
				localIntent2.putExtra("TABINDEX",
						MobileMusicMainActivity.this.mTabHost.getCurrentTab());
				MobileMusicMainActivity.this.setIntent(localIntent2);
			} else if ((paramAnonymousString.equalsIgnoreCase("TAB_LOCAL")))
			{
				MobileMusicMainActivity.this.mLastCurrentTab = MobileMusicMainActivity.this.mTabHost
						.getCurrentTab();
				Intent localIntent1 = MobileMusicMainActivity.this.getIntent();
				localIntent1.putExtra("TABINDEX",
						MobileMusicMainActivity.this.mTabHost.getCurrentTab());
				MobileMusicMainActivity.this.setIntent(localIntent1);
			}

		}
	};
	private TabHost mTabHost;
	PendingIntent pIntent = null;
	boolean requestRoot = true;
	private SharedPreferences shortCutSharedPreferences;

	private void addShortcut()
	{
		Intent localIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 设置名字
		localIntent.putExtra("android.intent.extra.shortcut.NAME",
				getString(R.string.app_name));
		// 不允许重建
		localIntent.putExtra("duplicate", false);
		// 使一个Activity与当前的桌面快捷方式绑定
		ComponentName localComponentName = new ComponentName("org.ming",
				"org.ming.ui.activity.PreSplashActivity");
		localIntent.putExtra("android.intent.extra.shortcut.INTENT",
				new Intent("android.intent.action.MAIN")
						.setComponent(localComponentName));
		localIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE",
				Intent.ShortcutIconResource.fromContext(this, R.drawable.icon));
		// 发送广播
		sendBroadcast(localIntent);
	}

	private void createLaunchShortcut()
	{
		if (!hasShortCut(this))
		{
			addShortcut();
		}
	}

	private TabHost.TabSpec createTabSpec(String paramString, int paramInt,
			Intent paramIntent)
	{
		logger.v("createTabSpec() ---> Enter");
		TabHost.TabSpec localTabSpec = this.mTabHost.newTabSpec(paramString);
		localTabSpec.setIndicator(this.mInflater.inflate(paramInt, null));
		localTabSpec.setContent(paramIntent);
		logger.v("createTabSpec() ---> Exit");
		return localTabSpec;
	}

	private static int getSystemVersion()
	{
		return Build.VERSION.SDK_INT;
	}

	private void initTab()
	{
		logger.v("initTab() ---> Enter");
		this.mInflater = LayoutInflater.from(this);
		this.mTabHost = ((TabHost) findViewById(android.R.id.tabhost));
		this.mTabHost.setup(getLocalActivityManager());
		Intent localIntent1 = new Intent(this, OnlineMusicActivity.class);
		this.mTabHost.addTab(createTabSpec("TAB_ONLINE",
				R.layout.tab_online_music_layout, localIntent1));
		Intent localIntent2 = new Intent(this, LocalMusicActivity.class);
		this.mTabHost.addTab(createTabSpec("TAB_LOCAL",
				R.layout.tab_local_music_layout, localIntent2));
		Intent localIntent3 = new Intent(this, MyMiGuActivity.class);
		this.mTabHost.addTab(createTabSpec("TAB_MIGU",
				R.layout.tab_my_migu_layout, localIntent3));
		this.mTabHost.setCurrentTab(0);

		this.mTabHost.getTabWidget().setOnClickListener(
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						Toast.makeText(
								MobileMusicMainActivity.this
										.getApplicationContext(), "click",
								Toast.LENGTH_SHORT).show();
					}
				});

		this.mTabHost.setOnTabChangedListener(this.mOnTabChangeListener);
		logger.v("initTab() ---> Exit");
	}

	private void refreshUI()
	{

	}

	private void shortcutDialogDismiss()
	{
		if (this.ShortCutDialog != null)
		{
			this.ShortCutDialog.dismiss();
			this.ShortCutDialog = null;
		}
		this.shortCutSharedPreferences.edit()
				.putBoolean("appcation_first_start", false).commit();
	}

	private void showDilaogForShortCutInLaunch()
	{
		logger.v("showDialogForShortCutInLaunch()------>enter");
		this.shortCutSharedPreferences = getSharedPreferences("shortcut",
				MODE_WORLD_WRITEABLE);
		if (this.shortCutSharedPreferences.getBoolean("appcation_first_start",
				true))
		{
			logger.v("appcation_first_start");
			if (!hasShortCut(MobileMusicMainActivity.this))
				this.ShortCutDialog = DialogUtil
						.show2BtnDialogWithIconTitleMsg(
								this,
								getResources().getText(
										R.string.title_information_common),
								getResources().getText(
										R.string.create_shoutcat_inlaunch),
								new View.OnClickListener()
								{
									public void onClick(View paramAnonymousView)
									{
										MobileMusicMainActivity.this
												.shortcutDialogDismiss();
										MobileMusicMainActivity.this
												.createLaunchShortcut();
									}
								}, new View.OnClickListener()
								{
									public void onClick(View paramAnonymousView)
									{
										MobileMusicMainActivity.this
												.shortcutDialogDismiss();
									}
								});
		}
	}

	public boolean hasShortCut(Context paramContext)
	{
		logger.v("hasShortCut()------->call");
		String str;
		boolean isInstallShortcut;
		if (getSystemVersion() < 8)
		{
			str = "content://com.android.launcher.settings/favorites?notify=true";
		} else
		{
			str = "content://com.android.launcher2.settings/favorites";
		}
		ContentResolver localContentResolver = paramContext
				.getContentResolver();
		Uri localUri = Uri.parse(str);
		String[] arrayOfString = new String[1];
		arrayOfString[0] = paramContext.getString(R.string.app_name);
		logger.v("getTheCursor------->before");
		Cursor localCursor = localContentResolver.query(localUri, null,
				"title=?", arrayOfString, null);

		if (localCursor != null && localCursor.moveToFirst())
		{
			isInstallShortcut = true;
			localCursor.close();
		} else
		{
			isInstallShortcut = false;
			logger.v("localCursor--->null");
		}
		return isInstallShortcut;
	}

	public void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity_layout);

		// 初始化主界面的各个TAG页
		initTab();
		showDilaogForShortCutInLaunch();

		logger.v("onCreate() ---> Exit");
	}

	@Override
	public void onCreateContextMenu(ContextMenu paramContextMenu,
			View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
	{
		logger.v("onCreateContextMenu() ---> Enter");
		super.onCreateContextMenu(paramContextMenu, paramView,
				paramContextMenuInfo);
		logger.v("onCreateContextMenu() ---> Exit");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent paramIntent)
	{
		logger.v("onNewIntent() ---> Enter");
		Bundle localBundle = paramIntent.getExtras();
		this.mIsFromLocalScan = localBundle
				.getBoolean("isFromLocalScan", false);
		if (this.mIsFromLocalScan)
		{
			this.mTabHost.setCurrentTab(1);
			this.mIsFromLocalScan = false;
		}
		super.onNewIntent(paramIntent);
		logger.v("onNewIntent() ---> Exit");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		this.requestRoot = true;
		refreshUI();
		super.onResume();
		logger.v("onResume() ---> Exit");
	}

}