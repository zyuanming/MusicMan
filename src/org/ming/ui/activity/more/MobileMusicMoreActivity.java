package org.ming.ui.activity.more;

import java.io.File;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.Playlist;
import org.ming.center.download.DLControllerImpl;
import org.ming.center.player.PlayerController;
import org.ming.center.weibo.AccessInfo;
import org.ming.center.weibo.AccessInfoHelper;
import org.ming.center.weibo.InfoHelper;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MobileMusicMoreActivity extends Activity
{
	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicMoreActivity");
	private int CMD_CANCEL_ALARMMANAGER = 1;
	private int CMD_START_ALARMMANAGER = 3;
	private SharedPreferences allowPushPreferences;
	private Context context;
	private View.OnClickListener dialogListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			if (mDialogBool.isShowing())
				mDialogBool.dismiss();
		}
	};
	private Button mAbout;
	private AccessInfoHelper mAccessDBHelper;
	private AccessInfo mAccessInfo = null;
	private Button mClearCache;
	private View.OnClickListener mClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			MobileMusicMoreActivity.logger.v("onClick() ---> Enter");
			File localFile = new File("/sdcard/redcloud/");
			File[] arrayOfFile;
			switch (paramAnonymousView.getId())
			{
			case R.id.more_login_music_cancellation_textview:
			case R.id.more_member_notification_push:
			case R.id.more_member_notification_is_push:
			default:
				logger.v("onClick() ---> Exit");
				return;
			case R.id.more_timing_member_close: // 定时关闭
				Intent intent5 = new Intent(MobileMusicMoreActivity.this,
						TimingClosureActivity.class);
				startActivity(intent5);
				break;
			case R.id.more_login_music_cancellation: // 登录注销
				// if (GlobalSettingParameter.useraccount == null)
				// {
				// Uiutil.login(MobileMusicMoreActivity.this, 7);
				// } else
				// {
				// Intent intent4 = new Intent(MobileMusicMoreActivity.this,
				// MobileMusicLoginCancellation.class);
				// startActivity(intent4);
				// }
				break;
			case R.id.more_member_clear_cache: // 清空缓存
				MobileMusicMoreActivity.this.mCurrentDialog = DialogUtil
						.show2BtnDialogWithIconTitleMsg(
								MobileMusicMoreActivity.this,
								MobileMusicMoreActivity.this
										.getText(R.string.user_clear_cache),
								MobileMusicMoreActivity.this
										.getText(R.string.more_clear_cache_content),
								new View.OnClickListener()
								{
									public void onClick(View paramAnonymous2View)
									{
										if (GlobalSettingParameter.useraccount != null)
										{
											DLControllerImpl
													.getInstance(
															(MobileMusicApplication) MobileMusicMoreActivity.this
																	.getApplication())
													.cancelDownloadRemainNotification();
											DLControllerImpl
													.getInstance(
															(MobileMusicApplication) MobileMusicMoreActivity.this
																	.getApplication())
													.cancelDownloadNotification();
											GlobalSettingParameter.useraccount = null;
											GlobalSettingParameter.loginMobileNum = null;
											GlobalSettingParameter.loginRadomNum = null;
											GlobalSettingParameter.show_dobly_toast = false;
											MobileMusicMoreActivity.this
													.clearPreferences();
											AccessInfoHelper localAccessInfoHelper = new AccessInfoHelper(
													MobileMusicMoreActivity.this);
											AccessInfo localAccessInfo = InfoHelper
													.getAccessInfo(MobileMusicMoreActivity.this);
											if (localAccessInfo != null)
											{
												localAccessInfoHelper.open();
												localAccessInfoHelper
														.delete(localAccessInfo);
												localAccessInfoHelper.close();
											}
										}
										Playlist localPlaylist = MobileMusicMoreActivity.this.mDBController
												.getPlaylistByName(
														"cmccwm.mobilemusic.database.default.mix.playlist.recent.play",
														2);
										MobileMusicMoreActivity.this.mDBController
												.deleteAllSongsFromMixPlaylist(
														localPlaylist.mExternalId,
														2);
										MobileMusicMoreActivity.this.mPlayerController
												.clearNowPlayingList();
										MobileMusicMoreActivity.this.mDBController
												.removeAllCacheData();
										File localFile = new File(
												"/sdcard/redcloud/");
										File[] arrayOfFile;
										if (localFile.isDirectory())
										{
											arrayOfFile = localFile.listFiles();
											for (int i = 0;; i++)
											{
												if (i >= arrayOfFile.length)
												{
													if (MobileMusicMoreActivity.this.mCurrentDialog != null)
													{
														MobileMusicMoreActivity.this.mCurrentDialog
																.dismiss();
														MobileMusicMoreActivity.this.mCurrentDialog = null;
													}
													return;
												}
												arrayOfFile[i].delete();
											}
										}
									}
								}, new View.OnClickListener()
								{
									public void onClick(View paramAnonymous2View)
									{
										if (MobileMusicMoreActivity.this.mCurrentDialog != null)
										{
											MobileMusicMoreActivity.this.mCurrentDialog
													.dismiss();
											MobileMusicMoreActivity.this.mCurrentDialog = null;
										}
									}
								});
				break;
			case R.id.more_member_help: // 帮助
				if (localFile.isDirectory())
				{
					arrayOfFile = localFile.listFiles();
					for (int i = 0; i < arrayOfFile.length; i++)
					{
						arrayOfFile[i].delete();
					}
					if (mCurrentDialog != null)
					{
						mCurrentDialog.dismiss();
						mCurrentDialog = null;
					}
				}
				return;
			case R.id.more_member_opinion: // 意见反馈
				Intent intent2 = new Intent(MobileMusicMoreActivity.this,
						MobileMusicFeedBackActivity.class);
				startActivity(intent2);
				break;
			case R.id.more_member_flow: // 免流量说明
				mDialogBool = DialogUtil.show1BtnDialogWithTitleMsg(
						MobileMusicMoreActivity.this,
						getResources().getText(R.string.user_member_flow),
						getResources().getText(R.string.user_flow_),
						dialogListener);
				break;
			case R.id.more_member_update_version: // 检查新版本
				// Intent intent1 = new Intent(MobileMusicMoreActivity.this,
				// MobileMusicUpdateActivity.class);
				// startActivity(intent1);
				break;
			case R.id.app_about: // 关于
				Intent intent = new Intent(MobileMusicMoreActivity.this,
						MobileMusicAboutActivity.class);
				startActivity(intent);
				break;
			}
		}
	};
	private Controller mController;
	private Dialog mCurrentDialog;
	private DBController mDBController;
	private Dialog mDialogBool;
	private EndSessionTask mEndSessionTask;
	private CheckBox mIsPushCheckBox;
	private Button mMembeFlow;
	private Button mMembeHelp;
	private Button mMemberOpinion;
	private Button mMsicLoginCancellation;
	private TextView mMsicLoginCancellationTextView;
	private RelativeLayout mNotificationPushLayout;
	private PlayerController mPlayerController;
	private Button mTimingClose;
	private TitleBarView mTitleBar;
	private Button mUpdateVersion;

	private void goNewweiboAutoActivity()
	{
		// Intent localIntent = new Intent(this.context,
		// NewWeiboAuthActivity.class);
		// localIntent.putExtra("WeiboAuthResult", false);
		// startActivity(localIntent);
	}

	public void clearPreferences()
	{
		getSharedPreferences("LOGIN-PREF", 0).edit().clear().commit();
	}

	protected void onCreate(Bundle bundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(bundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_more_list_layout);
		context = this;
		mController = ((MobileMusicApplication) getApplication())
				.getController();
		mDBController = mController.getDBController();
		mPlayerController = mController.getPlayerController();
		mTitleBar = (TitleBarView) findViewById(R.id.more_title_view);
		mTitleBar.setCurrentActivity(this);
		mTitleBar.setTitle(R.string.set);
		mTitleBar.setButtons(0);
		mAccessDBHelper = new AccessInfoHelper(this);
		mTimingClose = (Button) findViewById(R.id.more_timing_member_close);
		mMsicLoginCancellation = (Button) findViewById(R.id.more_login_music_cancellation);
		mMsicLoginCancellationTextView = (TextView) findViewById(R.id.more_login_music_cancellation_textview);
		mClearCache = (Button) findViewById(R.id.more_member_clear_cache);
		mMembeHelp = (Button) findViewById(R.id.more_member_help);
		mMemberOpinion = (Button) findViewById(R.id.more_member_opinion);
		mMembeFlow = (Button) findViewById(R.id.more_member_flow);
		mUpdateVersion = (Button) findViewById(R.id.more_member_update_version);
		mAbout = (Button) findViewById(R.id.app_about);
		mNotificationPushLayout = (RelativeLayout) findViewById(R.id.more_member_notification_push);
		mIsPushCheckBox = (CheckBox) findViewById(R.id.more_member_notification_is_push);
		mAbout.setOnClickListener(mClickListener);
		mTimingClose.setOnClickListener(mClickListener);
		mMsicLoginCancellation.setOnClickListener(mClickListener);
		mClearCache.setOnClickListener(mClickListener);
		mMembeHelp.setOnClickListener(mClickListener);
		mMemberOpinion.setOnClickListener(mClickListener);
		mMembeFlow.setOnClickListener(mClickListener);
		mUpdateVersion.setOnClickListener(mClickListener);
		if (NetUtil.isNetStateWap())
		{
			mMsicLoginCancellation.setVisibility(8);
			mMsicLoginCancellationTextView.setVisibility(8);
		} else
		{
			mMsicLoginCancellation.setVisibility(0);
			mMsicLoginCancellationTextView.setVisibility(0);
		}
		logger.v("onCreate() ---> Exit");
	}

	protected void onNewIntent(Intent paramIntent)
	{
		super.onNewIntent(paramIntent);
	}

	protected void onResume()
	{
		super.onResume();
		// mAccessInfo = InfoHelper.getAccessInfo(MobileMusicMoreActivity.this);
		mIsPushCheckBox.setClickable(false);
		allowPushPreferences = getSharedPreferences("allowpush", 0);
		if (allowPushPreferences.getBoolean("isallowpush", true))
			mIsPushCheckBox.setChecked(true);
		else
			mIsPushCheckBox.setChecked(false);
		mNotificationPushLayout
				.setOnClickListener(new android.view.View.OnClickListener()
				{
					public void onClick(View view)
					{
						if (mIsPushCheckBox.isChecked())
						{
							mIsPushCheckBox.setChecked(false);
							allowPushPreferences.edit()
									.putBoolean("isallowpush", false).commit();
							Intent intent1 = new Intent();
							intent1.setAction("cmccwm.mobilemusic.ui.util.NotificationService");
							intent1.putExtra("isCancelAlarm",
									CMD_CANCEL_ALARMMANAGER);
							sendBroadcast(intent1);
						} else
						{
							mIsPushCheckBox.setChecked(true);
							allowPushPreferences.edit()
									.putBoolean("isallowpush", true).commit();
							Intent intent = new Intent();
							intent.setAction("cmccwm.mobilemusic.ui.util.NotificationService");
							intent.putExtra("isCancelAlarm",
									CMD_START_ALARMMANAGER);
							sendBroadcast(intent);
						}
					}
				});
	}

	public void startOAuth()
	{
		logger.v("startOAuth() ---> Enter");
		if (this.mAccessInfo != null)
		{
			mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(this,
					getText(R.string.operator_notice),
					getText(R.string.weibo_remove_auth),
					new android.view.View.OnClickListener()
					{
						public void onClick(View view)
						{
							mEndSessionTask = new EndSessionTask();
							if (mEndSessionTask.getStatus() != android.os.AsyncTask.Status.RUNNING)
								mEndSessionTask.execute(new Void[0]);
							if (mCurrentDialog != null)
							{
								mCurrentDialog.dismiss();
								mCurrentDialog = null;
							}
						}
					}, new android.view.View.OnClickListener()
					{
						public void onClick(View view)
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
			goNewweiboAutoActivity();
		}
		logger.v("startOAuth() ---> Exit");
	}

	class EndSessionTask extends AsyncTask<Void, Void, Boolean>
	{
		Dialog mWeiboDialog;

		EndSessionTask()
		{}

		private boolean delWeiboToken()
		{
			mAccessDBHelper.open();
			boolean flag = mAccessDBHelper.deleteAll();
			mAccessDBHelper.close();
			return flag;
		}

		protected Boolean doInBackground(Void[] paramArrayOfVoid)
		{
			return Boolean.valueOf(delWeiboToken());
		}

		protected void onPostExecute(Boolean paramBoolean)
		{
			super.onPostExecute(paramBoolean);
		}

		protected void onPreExecute()
		{
			super.onPreExecute();
		}
	}
}