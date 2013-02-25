package org.ming.ui.activity;

import org.ming.R;
import org.ming.center.ConfigSettingParameter;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.MobileMusicService;
import org.ming.center.database.DBController;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.player.CacheSongData;
import org.ming.center.ui.AsyncToastDialogController;
import org.ming.center.ui.UIGlobalSettingParameter;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity implements MMHttpEventListener
{
	private static final MyLogger logger = MyLogger.getLogger("SplashActivity");
	private final int SPLASH_END = 0;
	private final int SPLASH_TIME = 800;
	private Controller mController;
	private Dialog mCurrentDialog = null;
	private MMHttpTask mCurrentTask;
	private DBController mDBController;
	private Dispatcher mDispatcher;
	private HttpController mHttpController;
	private SharedPreferences mNotWlanCheckBoxSharedPreferences;
	private int mTabIndex = 0;
	private CheckBox notWlanCheckBox;
	private final Handler mSplashHandler = new Handler()
	{
		// 处理匿名信息
		public void handleMessage(Message paramAnonymousMessage)
		{
			SplashActivity.logger.v("handleMessage() ---> Enter : "
					+ paramAnonymousMessage.what);
			switch (paramAnonymousMessage.what)
			{
			case 0:
			{
				SplashActivity.this.turnToMainActivity();
				SplashActivity.this.finish();
			}
			default:
				SplashActivity.logger.v("handleMessage() ---> Exit");
			}
		}
	};

	private void enterMonbileMusicMainActivty()
	{
		Message localMessage = new Message();
		localMessage.what = 0;
		this.mSplashHandler.sendMessageDelayed(localMessage, 800L);
	}

	private void turnToMainActivity()
	{
		logger.v("turnToMainActivity() ---> Enter");
		if (Util.isNeedForUserLead(this))
		{
			logger.v("startActivity------>UserLeadActivity");
			startActivity(new Intent(this, UserLeadActivity.class));
			Util.setNoNeedUserLead(this);
		} else
		{
			Intent localIntent = new Intent(getBaseContext(),
					MobileMusicMainActivity.class);
			localIntent.putExtra("TABINDEX", this.mTabIndex);
			startActivity(localIntent);
		}
		logger.v("turnToMainActivity() ---> Exit");
	}

	@Override
	public void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		this.mNotWlanCheckBoxSharedPreferences = getSharedPreferences(
				"LOGIN-PREF", 0);
		this.mController = Controller
				.getInstance((MobileMusicApplication) getApplication());
		this.mDBController = this.mController.getDBController();
		this.mHttpController = this.mController.getHttpController();
		this.mDispatcher = ((MobileMusicApplication) getApplication())
				.getEventDispatcher();

		ConfigSettingParameter.CONSTANT_CHANNEL_VALUE = this.mDBController
				.getChannelId();
		ConfigSettingParameter.CONSTANT_SUBCHANNEL_VALUE = this.mDBController
				.getSubChannelId();
		UIGlobalSettingParameter.usermore_download_auto_recovery = this.mController
				.getDBController().getDownLoad_AutoRecover();

		// 创建Service了，所有任务都是在这里开始执行。
		MobileMusicService.startService(getApplicationContext());

		AsyncToastDialogController.getInstance(MobileMusicApplication
				.getInstance());

		CacheSongData.getInstance();

		requestWindowFeature(1);
		setContentView(R.layout.splash);

		NetUtil.netState = NetUtil.getNetWorkState(this);
		if (!NetUtil.isConnection())
		{
			Toast.makeText(this, R.string.no_network_login, 1).show();
			mTabIndex = 0;
			NetUtil.netState = 8;
			enterMonbileMusicMainActivty();
		} else
		{
			final boolean autoLogin;
			final String userName;
			final String password;
			SharedPreferences sharedpreferences = getSharedPreferences(
					"LOGIN-PREF", 0);
			autoLogin = sharedpreferences.getBoolean(
					"loginactivity.pref.auto.login", false);
			userName = sharedpreferences.getString(
					"loginactivity.pref.username", null);
			password = sharedpreferences.getString("loginactivity.pref.pswd",
					null);
			if (NetUtil.netState != 6
					|| "1".equals(mNotWlanCheckBoxSharedPreferences.getString(
							"netCheck", null)))
			{
				mTabIndex = 0;
				int i;
				MMHttpRequest mmhttprequest;
				if (NetUtil.isNetStateWap())
					i = 1008; // 1008
				else
					i = 5011; // 5011
				mmhttprequest = MMHttpRequestBuilder.buildRequest(i);
				mCurrentTask = mHttpController.sendRequest(mmhttprequest);
				if (!NetUtil.isNetStateWap())
				{
					if (autoLogin && userName != null && password != null)
						mDispatcher
								.sendMessage(mDispatcher.obtainMessage(4020));
				} else
				{
					mDispatcher.sendMessage(mDispatcher.obtainMessage(4019));
				}
			} else
			{
				// 在移动网络下显示收费提示框
				mCurrentDialog = createDialogAboutNet(
						getText(R.string.title_information_common),
						getText(R.string.networker_change_is_other_net),
						new View.OnClickListener()
						{

							public void onClick(View view)
							{
								mCurrentDialog.dismiss();
								mTabIndex = 0;
								int i;
								MMHttpRequest mmhttprequest1;
								if (NetUtil.isNetStateWap())
									i = 1008; // 1008
								else
									i = 5011; // 5011
								mmhttprequest1 = MMHttpRequestBuilder
										.buildRequest(i);
								mCurrentTask = mHttpController
										.sendRequest(mmhttprequest1);
								if (autoLogin && userName != null
										&& password != null)
									mDispatcher.sendMessage(mDispatcher
											.obtainMessage(4020));
								if (notWlanCheckBox.isChecked())
									mNotWlanCheckBoxSharedPreferences.edit()
											.putString("netCheck", "1")
											.commit();
								else
									mNotWlanCheckBoxSharedPreferences.edit()
											.putString("netCheck", null)
											.commit();
								enterMonbileMusicMainActivty();
							}
						}, new View.OnClickListener()
						{

							public void onClick(View view)
							{
								if (mCurrentDialog != null)
								{
									mCurrentDialog.dismiss();
									mCurrentDialog = null;
								}
								if (notWlanCheckBox.isChecked())
									mNotWlanCheckBoxSharedPreferences.edit()
											.putString("netCheck", "1")
											.commit();
								else
									mNotWlanCheckBoxSharedPreferences.edit()
											.putString("netCheck", null)
											.commit();
								Util.exitMobileMusicApp(false);
							}

						});
			}
		}
		enterMonbileMusicMainActivty();
		logger.v("onCreate() ---> Exit");
	}

	@Override
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
		if (paramInt == 4)
		{
			// 在当前Activity的消息队列中删除消息号码为0的没有处理的消息
			this.mSplashHandler.removeMessages(0);
			Util.exitMobileMusicApp(false);
		}
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	@Override
	protected void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		logger.v("onPause() ---> Exit");
	}

	@Override
	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		logger.v("onResume() ---> Exit");
	}

	@Override
	public void handleMMHttpEvent(Message message)
	{
		MMHttpTask mmhttptask;
		logger.v("handleMMHttpEvent() ---> Enter");
		mmhttptask = (MMHttpTask) message.obj;
		if (mmhttptask != null && mCurrentTask != null
				&& mmhttptask.getTransId() == mCurrentTask.getTransId())
		{
			mCurrentTask = null;
			switch (message.what)
			{
			case 3003:
			case 3004:
			case 3005:
			case 3006:
			default:
				logger.v("handleMMHttpEvent() ---> Exit");
				break;
			}
		}
		logger.v("Thus http message is not for this activity");
	}

	/**
	 * 创建一个显示当前网络状况的框
	 * 
	 * @param paramCharSequence1
	 * @param paramCharSequence2
	 * @param paramOnClickListener1
	 * @param paramOnClickListener2
	 * @return
	 */
	private Dialog createDialogAboutNet(CharSequence charsequence,
			CharSequence charsequence1, View.OnClickListener onclicklistener,
			View.OnClickListener onclicklistener1)
	{
		Dialog dialog = new Dialog(this, R.style.CustomDialogTheme);
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_tile_text_two_button_checkbox_network, null);
		this.notWlanCheckBox = ((CheckBox) view.findViewById(R.id.not_warn));
		TextView textview = (TextView) view.findViewById(R.id.nw_title);
		TextView textview1 = (TextView) view.findViewById(R.id.msg);
		Button button = (Button) view.findViewById(R.id.button1);
		Button button1 = (Button) view.findViewById(R.id.button2);
		if (charsequence != null)
			textview.setText(charsequence);
		else
			textview.setVisibility(8);
		if (charsequence1 != null)
			textview1.setText(charsequence1);
		else
			textview.setVisibility(8);
		button.setOnClickListener(onclicklistener);
		button1.setOnClickListener(onclicklistener1);
		dialog.setContentView(view);
		dialog.setCancelable(false);
		dialog.show();
		return dialog;
	}
}