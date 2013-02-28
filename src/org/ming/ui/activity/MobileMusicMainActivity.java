package org.ming.ui.activity;

import java.util.List;

import org.ming.R;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.AsyncToastDialogController;
import org.ming.ui.activity.local.LocalMusicActivity;
import org.ming.ui.activity.mymigu.MyMiGuActivity;
import org.ming.ui.activity.online.MingOnlineMusic;
import org.ming.ui.activity.online.OnlineMusicActivity;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.NotificationService;
import org.ming.ui.util.Uiutil;
import org.ming.ui.widget.PlayerStatusBar;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

public class MobileMusicMainActivity extends TabActivity implements
		SystemEventListener
{
	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicMainActivity");
	private int CMD_CANCEL_ALARMMANAGER;
	private int CMD_START_ALARMMANAGER;
	private int CMD_STOP_SERVICE;
	private Dialog ShortCutDialog;
	AlarmManager alarmManager;
	private SharedPreferences allowPushPreferences;
	private ClosePushReceiver cpReceiver;
	private Handler handler;
	private Dialog mCurrentDialog;
	private LayoutInflater mInflater;
	private boolean mIsFromLocalScan;
	private int mLastCurrentTab;
	private PlayerStatusBar mPlayerStatusBar;
	private boolean mStartFromNotification;
	private TabHost mTabHost;
	private boolean mTurnMiGu;
	private Intent newIntent;
	PendingIntent pIntent;
	boolean requestRoot;
	private SharedPreferences shortCutSharedPreferences;
	private TabHost.OnTabChangeListener mOnTabChangeListener;

	public MobileMusicMainActivity()
	{
		mTurnMiGu = false;
		mIsFromLocalScan = false;
		mStartFromNotification = false;
		mPlayerStatusBar = null;
		requestRoot = true;
		mCurrentDialog = null;
		CMD_CANCEL_ALARMMANAGER = 1;
		CMD_STOP_SERVICE = 2;
		CMD_START_ALARMMANAGER = 3;
		pIntent = null;
		mOnTabChangeListener = new TabHost.OnTabChangeListener()
		{

			public void onTabChanged(String s)
			{
				logger.v("onTabChanged() ---> Enter");
				requestRoot = true;
				if (s.equalsIgnoreCase("TAB_MIGU"))
				{
//					if (OnlineMusicActivity.mListButtonClickListener != null)
//						OnlineMusicActivity.mListButtonClickListener
//								.closePopupWindow();
					if (GlobalSettingParameter.useraccount == null)
					{
						mTurnMiGu = true;
						Uiutil.login(MobileMusicMainActivity.this, 0);
						mTabHost.setCurrentTab(mLastCurrentTab);
					} else
					{
						mLastCurrentTab = mTabHost.getCurrentTab();
						Intent intent1 = getIntent();
						intent1.putExtra("TABINDEX", mTabHost.getCurrentTab());
						setIntent(intent1);
					}
				} else
				{
					// if (s.equalsIgnoreCase("TAB_LOCAL")
					// && OnlineMusicActivity.mListButtonClickListener != null)
					// OnlineMusicActivity.mListButtonClickListener
					// .closePopupWindow();
					mLastCurrentTab = mTabHost.getCurrentTab();
					Intent intent = getIntent();
					intent.putExtra("TABINDEX", mTabHost.getCurrentTab());
					setIntent(intent);
				}
				logger.v("onTabChanged() ---> Exit");
			}
		};
	}

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

	public void handleSystemEvent(Message paramMessage)
	{
		// int i = Util.setRingTone(this, ((Long)
		// paramMessage.obj).longValue());
		// this.handler.sendEmptyMessage(i);
	}

	/**
	 * 初始化主界面三个主标签
	 */
	private void initTab()
	{
		logger.v("initTab() ---> Enter");
		this.mInflater = LayoutInflater.from(this);
		this.mTabHost = ((TabHost) findViewById(android.R.id.tabhost));
		this.mTabHost.setup(getLocalActivityManager());
		// Intent localIntent1 = new Intent(this, OnlineMusicActivity.class);
		Intent localIntent1 = new Intent(this, MingOnlineMusic.class);
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
		ImageView localImageView = (ImageView) this.mTabHost.getTabWidget()
				.getChildAt(2).findViewById(R.id.tab_image);
		if (GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER != null)
			switch (Integer
					.parseInt(GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER))
			{
			default:
			case 0:
				localImageView.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.tab_my_migu_selector));
				break;
			case 1:
				localImageView
						.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.tab_my_migu_selector_normaluser));
				break;
			case 2:
				localImageView.setBackgroundDrawable(getResources()
						.getDrawable(
								R.drawable.tab_my_migu_selector_hight_member));
				break;
			case 3:
				localImageView
						.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.tab_my_migu_selector_special_member));
				break;
			}
	}

	// 退出应用提示
	private void exitApplication()
	{
		logger.v("exitApplication() ---> Enter");
		this.mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(this,
				getText(R.string.quit_app_dialog_title),
				getText(R.string.quit_app_dialog_message),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MobileMusicMainActivity.this.mCurrentDialog != null)
						{
							MobileMusicMainActivity.this.mCurrentDialog
									.dismiss();
							MobileMusicMainActivity.this.mCurrentDialog = null;
						}
						Intent localIntent = new Intent();
						localIntent
								.setAction("cmccwm.mobilemusic.ui.util.NotificationService");
						localIntent.putExtra("commendsign",
								MobileMusicMainActivity.this.CMD_STOP_SERVICE);
						MobileMusicMainActivity.this.sendBroadcast(localIntent);
						Util.exitMobileMusicApp(false);
					}
				}, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MobileMusicMainActivity.this.mCurrentDialog != null)
						{
							MobileMusicMainActivity.this.mCurrentDialog
									.dismiss();
							MobileMusicMainActivity.this.mCurrentDialog = null;
						}
					}
				});
		this.mCurrentDialog.setCancelable(false);
		logger.v("exitApplication() ---> Exit");
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

	/**
	 * 显示建立桌面快捷方式对话框
	 */
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

	/**
	 * 判断是否已经有桌面快捷方式了
	 * 
	 * @param paramContext
	 * @return
	 */
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

		this.mPlayerStatusBar = ((PlayerStatusBar) findViewById(R.id.mainplayerStatusBar));

		// 处理来电铃声消息
		this.handler = new Handler()
		{
			public void handleMessage(Message paramAnonymousMessage)
			{
				switch (paramAnonymousMessage.what)
				{
				default:
					Toast.makeText(MobileMusicApplication.getInstance(),
							R.string.set_vibrate_failed, 1).show();
				case 0:
					Toast.makeText(MobileMusicApplication.getInstance(),
							R.string.set_vibrate_successed, 1).show();
				case -1:
					Toast.makeText(MobileMusicApplication.getInstance(),
							R.string.set_vibrate_failed_in_some_mode, 1).show();
				}
				super.handleMessage(paramAnonymousMessage);
			}
		};

		// 初始化主界面的各个TAG页
		initTab();
		this.newIntent = getIntent();
		Bundle localBundle = this.newIntent.getExtras();
		if (localBundle != null)
		{
			this.mTurnMiGu = localBundle.getBoolean("hasLogin", false);
			this.mStartFromNotification = localBundle.getBoolean(
					"startFromNotification", false);
		}

		alarmManager = (AlarmManager) getSystemService("alarm");

		// 如果网络连接上了，而且没有通知启动标志，就开始轮询
		if ((NetUtil.isConnection()) && (!this.mStartFromNotification))
			startPollService();
		this.cpReceiver = new ClosePushReceiver();

		// 启动通知服务
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter
				.addAction("cmccwm.mobilemusic.ui.util.NotificationService");
		registerReceiver(this.cpReceiver, localIntentFilter);

		if (GlobalSettingParameter.IsMonthCheck
				&& getSharedPreferences(
						"cmccwm.mobilemusic.MusicOnlineMusicActivity", 0)
						.getString("currentVersion", "0").compareTo(
								GlobalSettingParameter.LOCAL_PARAM_VERSION) < 0)
		{
			AsyncToastDialogController.writeUpdateTime(this,
					System.currentTimeMillis());
		}
		showDilaogForShortCutInLaunch();
		logger.v("onCreate() ---> Exit");
	}

	public boolean dispatchKeyEvent(KeyEvent keyevent)
	{
		boolean flag = true;
		if (keyevent.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			// if (keyevent.getAction() == KeyEvent.ACTION_DOWN
			// && (OnlineMusicActivity.mListButtonClickListener == null ||
			// !OnlineMusicActivity.mListButtonClickListener
			// .closePopupWindow()))
			if(keyevent.getAction() == KeyEvent.ACTION_DOWN)
			{
				List list = ((ActivityManager) getSystemService("activity"))
						.getRunningTasks(2);
				if (list.size() > 1)
				{
					android.app.ActivityManager.RunningTaskInfo runningtaskinfo = (android.app.ActivityManager.RunningTaskInfo) list
							.get(1);
					Intent intent1 = new Intent();
					intent1.setComponent(runningtaskinfo.topActivity);
					startActivity(intent1);
				} else
				{
					Intent intent = new Intent("android.intent.action.MAIN");
					intent.setFlags(0x10000000);
					intent.addCategory("android.intent.category.HOME");
					startActivity(intent);
				}
			}
		} else
		{
			if (requestRoot)
			{
				mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab())
						.getRootView().requestFocus();
				requestRoot = false;
			}
			flag = super.dispatchKeyEvent(keyevent);
		}
		return flag;
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
		this.mTurnMiGu = localBundle.getBoolean("hasLogin", false);
		if ((this.mTurnMiGu) && (GlobalSettingParameter.useraccount != null))
		{
			this.mTabHost.setCurrentTab(2);
			this.mTurnMiGu = false;
		}
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
	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		this.requestRoot = true;
		if ((this.mTurnMiGu) && (GlobalSettingParameter.useraccount != null))
		{
			this.mTabHost.setCurrentTab(2);
			this.mTurnMiGu = false;
		}
		this.mPlayerStatusBar.registEventListener();
		if (this.newIntent != null)
		{
			int i = this.newIntent.getIntExtra("TABINDEX", 0);
			this.mTabHost.setCurrentTab(i);
			if ((i == 2) && (GlobalSettingParameter.useraccount == null))
				this.mTabHost.setCurrentTab(0);
		}
		refreshUI();
		super.onResume();
		logger.v("onResume() ---> Exit");
	}

	protected void onPause()
	{
		super.onPause();
		this.mPlayerStatusBar.unRegistEventListener();
	}

	/**
	 * 关闭的推送通知接收者，定时关闭应用
	 * 
	 * @author lkh
	 * 
	 */
	private class ClosePushReceiver extends BroadcastReceiver
	{
		private ClosePushReceiver()
		{}

		public void onReceive(Context paramContext, Intent paramIntent)
		{
			int i = paramIntent.getIntExtra("isCancelAlarm", 0);
			if (i == CMD_START_ALARMMANAGER)
				if (NetUtil.isConnection())
					startPollService();
			if ((i == CMD_CANCEL_ALARMMANAGER) && (pIntent != null))
			{
				alarmManager.cancel(pIntent);
				pIntent = null;
			}
		}
	}

	private void startPollService()
	{
		this.allowPushPreferences = getSharedPreferences("allowpush", 0);
		if (this.allowPushPreferences.getBoolean("isallowpush", true))
		{
			long l = System.currentTimeMillis();
			this.pIntent = PendingIntent.getService(this, 0, new Intent(this,
					NotificationService.class), 10000000);
			this.alarmManager.setRepeating(0, l, 1800000L, this.pIntent);
			this.allowPushPreferences.edit().putBoolean("isallowpush", true)
					.commit();
		}
	}
}