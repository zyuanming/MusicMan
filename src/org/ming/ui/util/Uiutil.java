package org.ming.ui.util;

import java.util.List;

import org.ming.R;
import org.ming.center.MobileMusicApplication;
import org.ming.center.system.SystemControllerImpl;
import org.ming.ui.activity.online.MusicOnlineRecommendFriend;
import org.ming.ui.activity.online.MusicOnlineSetRingToneActivity;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

public class Uiutil
{
	private static final MyLogger logger = MyLogger.getLogger("uiutil");
	public static Dialog mApnDialog;
	public static Dialog mLoadDataDialog;
	public static Dialog mWlanCloseDialog;

	public static void downloadMusic(Context context, String s, String s1,
			String s2)
	{
		logger.v("downloadMusic() ---> Enter");
		Intent intent = new Intent(context,
				MusicOnlineSetRingToneActivity.class);
		intent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.musicid",
				s);
		intent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.singername",
				s1);
		intent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.songname",
				s2);
		intent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.type", 3);
		context.startActivity(intent);
		logger.v("downloadMusic() ---> Exit");
	}

	public static void ifSwitchToWapDialog(Context paramContext)
	{
		logger.v("ifSwitchToWapDialog() ---> Enter");
		int i = NetUtil.getNetWorkState(paramContext);
		if (isTopActivy(paramContext))
		{
			if (((NetUtil.netState == 2) || (NetUtil.netState == 7) || (NetUtil.netState == 5))
					&& (i != 3) && (i != 6) && (i != 1))
			{
				if ((mWlanCloseDialog != null)
						&& (mWlanCloseDialog.isShowing()))
				{
					if ((mWlanCloseDialog.getOwnerActivity() != null)
							&& (!mWlanCloseDialog.getOwnerActivity()
									.isFinishing()))
					{
						mWlanCloseDialog.dismiss();
						mWlanCloseDialog = null;
					}
				} else if ((mWlanCloseDialog != null)
						&& !mWlanCloseDialog.isShowing())
				{
					mWlanCloseDialog = null;
				}
				if (mWlanCloseDialog == null)
					mWlanCloseDialog = DialogUtil
							.show1BtnDialogWithTitleMsg(
									paramContext,
									paramContext
											.getText(R.string.title_information_common),
									paramContext
											.getString(R.string.net_disconnect_util),
									new View.OnClickListener()
									{
										public void onClick(
												View paramAnonymousView)
										{
											if (Uiutil.mWlanCloseDialog != null)
											{
												Uiutil.mWlanCloseDialog
														.dismiss();
												Uiutil.mWlanCloseDialog = null;
											}
										}
									});
			}

			if ((NetUtil.netState == 2 || NetUtil.netState == 7 || NetUtil.netState == 5)
					&& (i == 3 || i == 6 || i == 1))
			{
				if (mWlanCloseDialog != null && mWlanCloseDialog.isShowing())
				{
					mWlanCloseDialog.dismiss();
					mWlanCloseDialog = null;
				}
				mWlanCloseDialog = DialogUtil
						.show2BtnDialogWithIconTitleMsg(
								paramContext,
								paramContext
										.getText(R.string.title_information_common),
								paramContext
										.getString(R.string.wlan_disconnect_cmwap_open_util),
								new android.view.View.OnClickListener()
								{

									public void onClick(View view)
									{
										Util.exitMobileMusicApp(false);
									}

								}, new android.view.View.OnClickListener()
								{

									public void onClick(View view)
									{
										if (Uiutil.mWlanCloseDialog != null)
										{
											Uiutil.mWlanCloseDialog.dismiss();
											Uiutil.mWlanCloseDialog = null;
										}
									}

								});
			} else if (NetUtil.netState == 8)
			{
				if (mWlanCloseDialog != null && mWlanCloseDialog.isShowing())
				{
					mWlanCloseDialog.dismiss();
					mWlanCloseDialog = null;
				}
				mWlanCloseDialog = DialogUtil
						.show1BtnDialogWithTitleMsg(
								paramContext,
								paramContext
										.getText(R.string.network_error_common),
								paramContext
										.getString(R.string.wlan_disconnect_title_util),
								new android.view.View.OnClickListener()
								{

									public void onClick(View view)
									{
										if (Uiutil.mWlanCloseDialog != null)
										{
											Uiutil.mWlanCloseDialog.dismiss();
											Uiutil.mWlanCloseDialog = null;
										}
									}

								});
			}
		}
		logger.v("ifSwitchToWapDialog Exit");
	}

	public static void ifSwitchToWapDialog(Context paramContext,
			boolean paramBoolean)
	{
		final Context context = paramContext;
		logger.v("ifSwitchToWapDialog() ---> Enter");
		if (paramContext != null)
		{
			int i = NetUtil.getNetWorkState(paramContext);
			if (((NetUtil.netState == 2) || (NetUtil.netState == 7) || (NetUtil.netState == 5))
					&& (i != 3) && (i != 6) && (i != 1))
			{
				if ((mWlanCloseDialog != null)
						&& (mWlanCloseDialog.isShowing())
						&& (mWlanCloseDialog.getOwnerActivity() != null)
						&& (!mWlanCloseDialog.getOwnerActivity().isFinishing()))
				{
					mWlanCloseDialog.dismiss();
					mWlanCloseDialog = null;
				}
				mWlanCloseDialog = DialogUtil
						.show1BtnDialogWithTitleMsg(
								paramContext,
								paramContext
										.getText(R.string.title_information_common),
								paramContext
										.getString(R.string.net_disconnect_util),
								new View.OnClickListener()
								{
									public void onClick(View paramAnonymousView)
									{
										if (Uiutil.mWlanCloseDialog != null)
										{
											Uiutil.mWlanCloseDialog.dismiss();
											Uiutil.mWlanCloseDialog = null;
											((Activity) context).finish();
										}
									}
								});
			}
			if (((NetUtil.netState == 2) || (NetUtil.netState == 7) || (NetUtil.netState == 5))
					&& ((i == 3) || (i == 6) || (i == 1)))
			{
				if ((mWlanCloseDialog != null)
						&& (mWlanCloseDialog.isShowing()))
				{
					mWlanCloseDialog.dismiss();
					mWlanCloseDialog = null;
				}
				mWlanCloseDialog = DialogUtil
						.show2BtnDialogWithIconTitleMsg(
								paramContext,
								paramContext
										.getText(R.string.title_information_common),
								paramContext
										.getString(R.string.wlan_disconnect_cmwap_open_util),
								new View.OnClickListener()
								{
									public void onClick(View paramAnonymousView)
									{
										Util.exitMobileMusicApp(false);
									}
								}, new View.OnClickListener()
								{
									public void onClick(View paramAnonymousView)
									{
										if (Uiutil.mWlanCloseDialog != null)
										{
											Uiutil.mWlanCloseDialog.dismiss();
											Uiutil.mWlanCloseDialog = null;
										}
										((Activity) context).finish();
									}
								});
			} else if (NetUtil.netState == 8)
			{
				if ((mWlanCloseDialog != null)
						&& (mWlanCloseDialog.isShowing()))
				{
					mWlanCloseDialog.dismiss();
					mWlanCloseDialog = null;
				}
				mWlanCloseDialog = DialogUtil
						.show1BtnDialogWithTitleMsg(
								paramContext,
								paramContext
										.getText(R.string.network_error_common),
								paramContext
										.getString(R.string.wlan_disconnect_title_util),
								new View.OnClickListener()
								{
									public void onClick(View paramAnonymousView)
									{
										if (Uiutil.mWlanCloseDialog != null)
										{
											Uiutil.mWlanCloseDialog.dismiss();
											Uiutil.mWlanCloseDialog = null;
										}
										((Activity) context).finish();
									}
								});
			}
			logger.v("ifSwitchToWapDialog Exit");
		}
	}

	public static boolean isNetChange()
	{
		boolean flag = true;
		if (NetUtil.netState != 8)
			if (!NetUtil.isNetStateWap())
			{
				logger.v("createNetworkClient() ---> !WlanUtils.isNetStateWap() && !isHttpReqIfWlan");
				if (NetUtil.netState != 1 && NetUtil.netState != 6)
					logger.d("Wlan has been closed.");
				else
					flag = false;
			} else
			{
				MobileMusicApplication mobilemusicapplication = MobileMusicApplication
						.getInstance();
				if ((NetUtil.netState == 3 || NetUtil.netState == 5)
						&& !SystemControllerImpl.getInstance(
								mobilemusicapplication).checkWapStatus())
					logger.d("WAP has been closed.");
				else
					flag = false;
			}
		return flag;
	}

	public static boolean isTopActivy(Context context)
	{
		List list = ((ActivityManager) context.getSystemService("activity"))
				.getRunningTasks(1);
		String s = null;
		if (list != null)
			s = ((android.app.ActivityManager.RunningTaskInfo) list.get(0)).topActivity
					.toString();
		boolean flag;
		if (s == null)
			flag = false;
		else
			flag = s.equals(((Activity) context).getComponentName().toString());
		return flag;
	}

	public static void login(Context context, int i)
	{
		logger.v("login() ---> Enter");
		// if (NetUtil.isConnection())
		// {
		// if (NetUtil.isNetStateWap())
		// {
		// Intent intent = new Intent(context,
		// org.ming.ui.activity.online.MusicOnlineWapLoginActivity);
		// intent.putExtra("FROMTYPE", i);
		// context.startActivity(intent);
		// } else
		// {
		// Intent intent1 = new Intent(context, cmccwm / mobilemusic / ui
		// / activity / LoginVerificationActivity);
		// intent1.putExtra("FROMTYPE", i);
		// context.startActivity(intent1);
		// }
		// } else
		// {
		// if (((Activity) context).getParent() != null)
		// context = ((Activity) context).getParent();
		// ifSwitchToWapDialog(context);
		// }
		logger.v("login() ---> Exit");
	}

	public static PopupWindow popupWarningIcon(Context paramContext,
			View paramView, int paramInt)
	{
		logger.v("PopupWindow() ---> Enter");
		PopupWindow localPopupWindow = new PopupWindow(LayoutInflater.from(
				paramContext).inflate(R.layout.window_add_playlist_warn, null),
				-2, -2);
		localPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		int[] arrayOfInt = new int[2];
		paramView.getLocationOnScreen(arrayOfInt);
		localPopupWindow.showAtLocation(paramView, 0, arrayOfInt[0],
				arrayOfInt[1]);
		localPopupWindow.update();
		logger.v("PopupWindow() ---> Exit");
		return localPopupWindow;
	}

	// public static void recommondMusic(Context paramContext,
	// String paramString1, String paramString2)
	// {
	// logger.v("recommondMusic() ---> Enter");
	// Intent localIntent = new Intent(paramContext,
	// MusicOnlineRecommendFriend.class);
	// localIntent
	// .putExtra(
	// "mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.cotentid",
	// paramString1);
	// localIntent
	// .putExtra(
	// "mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.groupcode",
	// paramString2);
	// localIntent.putExtra(
	// "mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.type", 5);
	// paramContext.startActivity(localIntent);
	// logger.v("recommondMusic() ---> Exit");
	// }

	// public static void sendMusic(Context paramContext, String paramString1,
	// String paramString2)
	// {
	// logger.v("sendMusic() ---> Enter");
	// Intent localIntent = new Intent(paramContext,
	// MusicOnlineRecommendFriend.class);
	// localIntent
	// .putExtra(
	// "mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.cotentid",
	// paramString1);
	// localIntent
	// .putExtra(
	// "mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.groupcode",
	// paramString2);
	// localIntent.putExtra(
	// "mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.type", 4);
	// paramContext.startActivity(localIntent);
	// logger.v("sendMusic() ---> Exit");
	// }

	/**
	 * 设置振铃
	 * 
	 * @param paramContext
	 * @param paramString1
	 * @param paramString2
	 */
	public static void setTone(Context paramContext, String s1, String s2,
			String s3)
	{
		logger.v("setTone() ---> Enter");
		Intent localIntent = new Intent(paramContext,
				MusicOnlineSetRingToneActivity.class);
		localIntent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.musicid",
				s1);
		localIntent
				.putExtra(
						"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.singername",
						s2);
		localIntent
				.putExtra(
						"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.songname",
						s3);
		localIntent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.type", 1);
		paramContext.startActivity(localIntent);
		logger.v("setTone() ---> Exit");
	}

	/**
	 * 设置来电铃声
	 * 
	 * @param paramContext
	 * @param paramString1
	 * @param paramString2
	 */
	public static void setViberate(Context context, String s, String s1,
			String s2, String s3, String s4)
	{
		logger.v("setViberate() ---> Enter");
		Intent intent = new Intent(context,
				MusicOnlineSetRingToneActivity.class);
		intent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.musicid",
				s);
		intent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.singername",
				s1);
		intent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.songname",
				s2);
		intent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.ringsongprice",
				s3);
		intent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.crbtValidity",
				s4);
		intent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.type", 2);
		context.startActivity(intent);
		logger.v("setViberate() ---> Exit");
	}

	public static Dialog showWaitingDialog(Context paramContext,
			DialogInterface.OnCancelListener paramOnCancelListener)
	{
		logger.v("showWaitingDialog() ---> Enter");
		mLoadDataDialog = DialogUtil.show1BtnProgressDialog(paramContext,
				2131165291, 2131165220, new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (Uiutil.mLoadDataDialog != null)
						{
							Uiutil.mLoadDataDialog.cancel();
							Uiutil.mLoadDataDialog = null;
						}
					}
				});
		mLoadDataDialog.setOnCancelListener(paramOnCancelListener);
		mLoadDataDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
		{
			public boolean onKey(DialogInterface paramAnonymousDialogInterface,
					int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
			{
				if (paramAnonymousInt == 82)
					;
				for (boolean bool = true;; bool = false)
					return bool;
			}
		});
		logger.v("showWaitingDialog() ---> Exit");
		return mLoadDataDialog;
	}

	public static void sendMusic(Context paramContext, String s1, String s2,
			String s3, String s4)
	{
		logger.v("sendMusic() ---> Enter");
		Intent localIntent = new Intent(paramContext,
				MusicOnlineRecommendFriend.class);
		localIntent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.musicid",
				s1);
		localIntent
				.putExtra(
						"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.singerName",
						s2);
		localIntent
				.putExtra(
						"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.songName",
						s3);
		localIntent
				.putExtra(
						"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.ringsongprice",
						s4);
		localIntent.putExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.type", 4);
		paramContext.startActivity(localIntent);
		logger.v("sendMusic() ---> Exit");
	}

	public static void recommondMusic(Context paramContext,
			String paramString1, String paramString2)
	{
		logger.v("recommondMusic() ---> Enter");
		// Intent localIntent = new Intent(paramContext,
		// MusicOnlineRecommendFriend.class);
		// localIntent
		// .putExtra(
		// "mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.cotentid",
		// paramString1);
		// localIntent
		// .putExtra(
		// "mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.groupcode",
		// paramString2);
		// localIntent.putExtra(
		// "mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.type", 5);
		// paramContext.startActivity(localIntent);
		logger.v("recommondMusic() ---> Exit");
	}
}