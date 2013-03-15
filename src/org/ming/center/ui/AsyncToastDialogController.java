package org.ming.center.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ming.R;
import org.ming.center.BindingContainer;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.business.MusicBusinessDefine_Net;
import org.ming.center.database.DBController;
import org.ming.center.database.Song;
import org.ming.center.download.DLController;
import org.ming.center.download.DLEventListener;
import org.ming.center.download.DownloadItem;
import org.ming.center.download.DownloadTask;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.http.item.SongListItem;
import org.ming.center.http.item.UpdateCacheDataItem;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerEventListener;
import org.ming.dispatcher.Dispatcher;
import org.ming.dispatcher.DispatcherEventEnum;
import org.ming.ui.activity.DialogActivity;
import org.ming.ui.activity.MusicPlayerActivity;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.XMLParser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AsyncToastDialogController implements MMHttpEventListener,
		PlayerEventListener, DLEventListener, UIEventListener
{
	public static final String ORDER_OR_CANCEL_SUCCESS = "000000";
	public static final String RETCODE_RESET_FAIL = "000001";
	public static final String RETCODE_SUCCESS = "000000";
	public static final String RETCODE_UESER_UREGISTERED = "000002";
	private static AsyncToastDialogController sInstance = null;
	private static final MyLogger sLogger = MyLogger
			.getLogger("AsyncToastDialogController");
	private boolean isPauseByThirdSeconds = false;
	private MobileMusicApplication mApp = null;
	private MMHttpTask mCurrentTask;
	private DBController mDBController = null;
	private DLController mDLController = null;
	private Dispatcher mDispatcher = null;
	private HttpController mHttpController = null;
	private PlayerController mPlayerController = null;

	private AsyncToastDialogController(
			MobileMusicApplication paramMobileMusicApplication)
	{
		sLogger.v("AsyncToastDialogController ---> Enter");
		this.mApp = paramMobileMusicApplication;
		this.mPlayerController = paramMobileMusicApplication.getController()
				.getPlayerController();
		this.mDLController = paramMobileMusicApplication.getController()
				.getDLController();
		this.mDBController = paramMobileMusicApplication.getController()
				.getDBController();
		this.mDispatcher = paramMobileMusicApplication.getEventDispatcher();
		this.mHttpController = paramMobileMusicApplication.getController()
				.getHttpController();
		registerAsyncEvent();
		sLogger.v("AsyncToastDialogController ---> Exit");
	}

	private void cancelDownloadNotification()
	{
		sLogger.v("cancelDownloadNotification() ---> Enter");
		((NotificationManager) this.mApp.getSystemService("notification"))
				.cancel(2);
		sLogger.v("cancelDownloadNotification() ---> Exit");
	}

	private void cancelDownloadRemainNotification()
	{
		sLogger.v("cancelDownloadRemainNotification() ---> Enter");
		((NotificationManager) this.mApp.getSystemService("notification"))
				.cancel(3);
		sLogger.v("cancelDownloadRemainNotification() ---> Exit");
	}

	private void cancelPlaybackStatusBar()
	{
		sLogger.v("cancelPlaybackStatusBar() ---> Enter");
		((NotificationManager) this.mApp.getSystemService("notification"))
				.cancel(1);
		sLogger.v("cancelPlaybackStatusBar() ---> Exit");
	}

	private void downloadMusicInfo(Message message)
	{
		sLogger.v("downloadMusicInfo() ---> Enter");
		switch (message.arg1)
		{
		default:
		case 1:
			Toast.makeText(mApp, R.string.connect_timeout_common, 0).show();
			break;
		case 2:
		case 3:
			Toast.makeText(mApp, R.string.getfail_data_error_common, 0).show();
			break;
		case 4:
			Song song = mPlayerController.getCurrentPlayingItem();
			if (song != null)
			{
				String s = (new StringBuilder(String.valueOf(song.mTrack)))
						.append(":").append(message.obj).toString();
				Toast.makeText(mApp, s, 0).show();
			}
			break;
		}
	}

	public static AsyncToastDialogController getInstance(
			MobileMusicApplication paramMobileMusicApplication)
	{
		if (sInstance == null)
			sInstance = new AsyncToastDialogController(
					paramMobileMusicApplication);
		return sInstance;
	}

	private void onCancelOnlineMusicFail(String s, boolean flag)
	{
		sLogger.v("onCancelOnlineMusicFail() ---> Enter");
		Intent intent = new Intent(mApp, DialogActivity.class);
		intent.setFlags(0x10000000);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype", 1001);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
				R.drawable.cmcc_dialog_question2);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
				R.string.title_cancel_online_music_atdcontroller);
		if (flag)
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					mApp.getString(R.string.connect_timeout_common));
		else
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					s);
		mApp.startActivity(intent);
		sLogger.v("onCancelOnlineMusicFail() ---> Exit");
	}

	private void onCancelWholeSongFail(String s, boolean flag)
	{
		sLogger.v("onCancelWholeSongFail() ---> Enter");
		Intent intent = new Intent(mApp, DialogActivity.class);
		intent.setFlags(0x10000000);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype", 1003);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
				R.drawable.cmcc_dialog_question2);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
				R.string.title_cancel_whole_song_atdcontroller);
		if (flag)
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					mApp.getString(R.string.connect_timeout_common));
		else
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					s);
		mApp.startActivity(intent);
		sLogger.v("onCancelWholeSongFail() ---> Exit");
	}

	private void onDeleteToneFail(String s, boolean flag)
	{
		sLogger.v("onDeleteToneFail() ---> Enter");
		Intent intent = new Intent(mApp, DialogActivity.class);
		intent.setFlags(0x10000000);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype", 1006);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
				R.drawable.cmcc_dialog_question2);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
				R.string.title_del_tone_personal_tone_activity);
		if (flag)
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					mApp.getString(R.string.connect_timeout_common));
		else
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					s);
		mApp.startActivity(intent);
		sLogger.v("onDeleteToneFail() ---> Exit");
	}

	private void onHttpResponse(MMHttpTask mmhttptask)
	{
		sLogger.v("onHttpResponse() ---> Enter");
		if (mmhttptask.getRequest() != null)
		{
			int i = mmhttptask.getRequest().getReqType();
			byte[] abyte0 = mmhttptask.getResponseBody();
			XMLParser xmlparser = new XMLParser(abyte0);
			switch (i)
			{
			default:
			case 1037:
			case 5040:
				String s14 = xmlparser.getValueByTag("code");
				if (s14 != null && s14.equalsIgnoreCase("000000"))
				{
					Toast.makeText(
							mApp,
							R.string.default_msg_order_online_music_ok_atdcontroller,
							1).show();
					GlobalSettingParameter.SERVER_INIT_PARAM_ONLINE_MUSIC_ORDER_STATUS = "1";
					GlobalSettingParameter.SERVER_INIT_PARAM_ONLINE_MUSIC_ORDER_STATUS = "1";
					if (isPauseByThirdSeconds())
					{
						if (mApp.getController().getPlayerController()
								.isPause())
							mApp.getController().getPlayerController().start();
						setPauseByThirdSeconds(false);
						// if (MyMiGuBusinessOrderActivity.mSelfIntace != null)
						// MyMiGuBusinessOrderActivity.mSelfIntace.refreshUI();
					}
				} else
				{
					String s15 = xmlparser.getValueByTag("info");
					if (s15 == null)
						s15 = mApp
								.getString(R.string.default_msg_order_online_music_fail_atdcontroller);
					onOrderOnlineMusicFail(s15, false);
				}
				break;
			case 1038:
			case 5041:
				String s12 = xmlparser.getValueByTag("code");
				if (s12 != null && s12.equalsIgnoreCase("000000"))
				{
					Toast.makeText(
							mApp,
							R.string.default_msg_cancel_online_music_ok_atdcontroller,
							1).show();
					GlobalSettingParameter.SERVER_INIT_PARAM_ONLINE_MUSIC_ORDER_STATUS = "0";
				} else
				{
					String s13 = xmlparser.getValueByTag("info");
					if (s13 == null)
						s13 = mApp
								.getString(R.string.default_msg_cancel_online_music_fail_atdcontroller);
					onCancelOnlineMusicFail(s13, false);
				}
				break;
			case 1040:
			case 5043:
				String s10 = xmlparser.getValueByTag("code");
				if (s10 != null)
					if (s10.equalsIgnoreCase("000000"))
					{
						Toast.makeText(
								mApp,
								R.string.default_msg_order_whole_song_ok_atdcontroller,
								1).show();
						if (mmhttptask.getRequest().getValueOfUrlParams("pid")
								.equals("3"))
							GlobalSettingParameter.SERVER_INIT_PARAM_QQORDER = "13";
						else
							GlobalSettingParameter.SERVER_INIT_PARAM_QQORDER = "14";
					} else
					{
						String s11 = xmlparser.getValueByTag("info");
						if (s11 == null)
							s11 = mApp
									.getString(R.string.default_msg_order_whole_song_fail_atdcontroller);
						onOrderWholeSongFail(s11, false);
					}
				break;
			case 1039:
			case 5042:
				String s8 = xmlparser.getValueByTag("code");
				if (s8 != null && s8.equalsIgnoreCase("000000"))
				{
					Toast.makeText(
							mApp,
							R.string.default_msg_cancel_whole_song_ok_atdcontroller,
							1).show();
					GlobalSettingParameter.SERVER_INIT_PARAM_QQORDER = "00";
				} else
				{
					String s9 = xmlparser.getValueByTag("info");
					if (s9 == null)
						s9 = mApp
								.getString(R.string.default_msg_cancel_whole_song_fail_atdcontroller);
					onCancelWholeSongFail(s9, false);
				}
				break;
			case 1044:
			case 5047:
				String s6 = xmlparser.getValueByTag("code");
				if (s6 != null && s6.equalsIgnoreCase("000000"))
				{
					Toast.makeText(
							mApp,
							R.string.default_msg_del_personal_tone_ok_atdcontroller,
							1).show();
				} else
				{
					String s7 = xmlparser.getValueByTag("info");
					if (s7 == null)
						s7 = mApp
								.getString(R.string.default_msg_del_personal_tone_fail_atdcontroller);
					onDeleteToneFail(s7, false);
				}
				break;
			case 1052:
			case 5056:
				if (xmlparser.getValueByTag("info") != null)
					Toast.makeText(
							mApp,
							mApp.getText(R.string.orderwindow_sendsong_success),
							0).show();
				else
					onPresentSongFail(
							mApp.getString(R.string.default_msg_present_song_fail_atdcontroller),
							false);
				break;
			case 1053:
			case 5055:
				if (xmlparser.getValueByTag("info") != null)
					Toast.makeText(
							mApp,
							mApp.getText(R.string.orderwindow_recommand_success),
							0).show();
				else
					onRecommendSongFail(
							mApp.getString(R.string.default_msg_recommend_song_fail_atdcontroller),
							false);
				break;
			case 1008:
			case 5011:
				List list = xmlparser.getListByTagsAndID("musiclist", "item",
						0, SongListItem.class);
				if (list == null)
				{
					List list1;
					boolean flag;
					int k;
					GlobalSettingParameter.SERVER_INIT_PARAM_UPDATE_INFO = xmlparser
							.getValueByTag("updateinfo");
					GlobalSettingParameter.SERVER_INIT_PARAM_UPDATE_URL = xmlparser
							.getValueByTag("updateurl");
					GlobalSettingParameter.SERVER_INIT_PARAM_NEED_UPDATE = xmlparser
							.getValueByTag("flag");
					GlobalSettingParameter.SERVER_INIT_PARAM_UPDATE_VERSION = xmlparser
							.getValueByTag("updateversion");
					int j;
					Song song;
					if (GlobalSettingParameter.IsMonthCheck)
					{
						if (monthUpdate(mApp.getApplicationContext())
								&& GlobalSettingParameter.SERVER_INIT_PARAM_NEED_UPDATE != null)
							mDispatcher.sendMessage(mDispatcher
									.obtainMessage(4022));
					} else if (GlobalSettingParameter.SERVER_INIT_PARAM_NEED_UPDATE != null)
						mDispatcher
								.sendMessage(mDispatcher.obtainMessage(4022));
					list1 = xmlparser.getListByTagAndAttribute("item",
							UpdateCacheDataItem.class);
					flag = false;
					if (list1 == null)
					{
						if (flag)
							mDispatcher.sendMessage(mDispatcher
									.obtainMessage(4013));
					} else
					{
						j = list1.size();
						flag = false;
						if (j == 0)
						{
							if (flag)
								mDispatcher.sendMessage(mDispatcher
										.obtainMessage(4013));
						} else
						{
							k = 0;
						}
					}
				} else
				{
					String s5;
					ArrayList arraylist;
					Iterator iterator;
					s5 = xmlparser.getAttributeByTag("musiclist", "groupcode");
					arraylist = new ArrayList();
					iterator = list.iterator();
				}
				break;
			case 5003:
				// GlobalSettingParameter.isLogining = false;
				// if (xmlparser.getRoot() == null ||
				// xmlparser.getValueByTag("code") == null)
				// {
				// mDispatcher.sendMessage(mDispatcher.obtainMessage(4018));
				// }
				// if (xmlparser.getValueByTag("code") != null &&
				// !xmlparser.getValueByTag("code").equals("000000"))
				// {
				// mDispatcher.sendMessage(mDispatcher.obtainMessage(4018));
				// }
				// String s1 = xmlparser.getValueByTag("code");
				// if (xmlparser.getValueByTag("info") != null)
				// {
				// if (s1.equals("000000"))
				// {
				// GlobalSettingParameter.initLoginParam(abyte0);
				// if (GlobalSettingParameter.SERVER_INIT_PARAM_MDN != null)
				// {
				// if (GlobalSettingParameter.useraccount == null)
				// {
				// UserAccount useraccount1 = new UserAccount();
				// useraccount1.mMDN =
				// GlobalSettingParameter.SERVER_INIT_PARAM_MDN;
				// useraccount1.mId =
				// mDBController.addUserAccount(useraccount1);
				// if (useraccount1.mId != 0L)
				// GlobalSettingParameter.useraccount = useraccount1;
				// if
				// (mDBController.getPlaylistByName("cmccwm.mobilemusic.database.default.online.playlist.favorite",
				// 0) == null)
				// mDBController.createPlaylist("cmccwm.mobilemusic.database.default.online.playlist.favorite",
				// 0);
				// }
				// mDLController.initDownloadListFromDB();
				// mDispatcher.sendMessageDelayed(mDispatcher.obtainMessage(2008),
				// 0L);
				// }
				// GlobalSettingParameter.loginMobileNum =
				// xmlparser.getValueByTag("mdn");
				// GlobalSettingParameter.loginRadomNum =
				// xmlparser.getValueByTag("randomsessionkey");
				// if (GlobalSettingParameter.loginMobileNum == null ||
				// GlobalSettingParameter.loginRadomNum == null)
				// {
				// mDispatcher.sendMessage(mDispatcher.obtainMessage(4018));
				// }
				// mDispatcher.sendMessage(mDispatcher.obtainMessage(4014));
				// GlobalSettingParameter.isWlanLogined = true;
				// } else
				// if (s1.equals("000002"))
				// mDispatcher.sendMessage(mDispatcher.obtainMessage(4018));
				// else
				// if (s1.equals("000001"))
				// mDispatcher.sendMessage(mDispatcher.obtainMessage(4018));
				// } else
				// {
				// mDispatcher.sendMessage(mDispatcher.obtainMessage(4018));
				// }
				// if (k < list1.size())
				// {
				// UpdateCacheDataItem updatecachedataitem =
				// (UpdateCacheDataItem)list1.get(k);
				// if (updatecachedataitem == null)
				// {
				// k++;
				// goto _L30
				// FileUtils.deleteFile((String)list2.get(l));
				// flag = true;
				// l++;
				// goto _L31
				// }
				// else
				// {
				// String s2;
				// String s3;
				// s2 = updatecachedataitem.groupcode;
				// s3 = updatecachedataitem.publish_time;
				// if (s2 == null || s3 == null)
				// {
				// Intent intent1 = new Intent(mApp,
				// cmccwm/mobilemusic/ui/activity/DialogActivity);
				// intent1.setFlags(0x10000000);
				// intent1.putExtra("cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype",
				// 1014);
				// intent1.putExtra("cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
				// 0x7f0200ad);
				// intent1.putExtra("cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
				// 0x7f070041);
				// intent1.putExtra("cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
				// s3);
				// mApp.startActivity(intent1);
				// }
				// else
				// {
				// i++;
				// String s4 = mDBController.queryDateByGroupCode(s2);
				// if (s4 == null || s4.equals("") || s4.equals(s3))
				// {
				// k++;
				// goto _L30
				// FileUtils.deleteFile((String)list2.get(l));
				// flag = true;
				// l++;
				// goto _L31
				// }
				// }
				// }
				// }
				// if (flag)
				// mDispatcher.sendMessage(mDispatcher.obtainMessage(4013));
				break;
			case 1001:
				// GlobalSettingParameter.isLogining = false;
				// if (xmlparser.getRoot() == null
				// || xmlparser.getValueByTag("code") == null)
				// {
				// mDispatcher.sendMessage(mDispatcher.obtainMessage(4018));
				// }
				// String s = xmlparser.getValueByTag("code");
				// if (xmlparser.getValueByTag("info") != null)
				// {
				// if (s.equals("000000"))
				// {
				// GlobalSettingParameter.initLoginParam(abyte0);
				// if (GlobalSettingParameter.SERVER_INIT_PARAM_MDN != null)
				// {
				// GlobalSettingParameter.useraccount = mDBController
				// .getInDBByMDN(GlobalSettingParameter.SERVER_INIT_PARAM_MDN);
				// if (GlobalSettingParameter.useraccount == null)
				// {
				// UserAccount useraccount = new UserAccount();
				// useraccount.mMDN =
				// GlobalSettingParameter.SERVER_INIT_PARAM_MDN;
				// useraccount.mId = mDBController
				// .addUserAccount(useraccount);
				// if (useraccount.mId != 0L)
				// GlobalSettingParameter.useraccount = useraccount;
				// if (mDBController
				// .getPlaylistByName(
				// "cmccwm.mobilemusic.database.default.online.playlist.favorite",
				// 0) == null)
				// mDBController
				// .createPlaylist(
				// "cmccwm.mobilemusic.database.default.online.playlist.favorite",
				// 0);
				// }
				// mDLController.initDownloadListFromDB();
				// mDispatcher.sendMessageDelayed(
				// mDispatcher.obtainMessage(2008), 0L);
				// }
				// GlobalSettingParameter.loginMobileNum = xmlparser
				// .getValueByTag("mdn");
				// GlobalSettingParameter.loginRadomNum = xmlparser
				// .getValueByTag("randomsessionkey");
				// if (GlobalSettingParameter.loginMobileNum == null
				// || GlobalSettingParameter.loginRadomNum == null)
				// {
				// mDispatcher.sendMessage(mDispatcher
				// .obtainMessage(4018));
				// }
				// mDispatcher
				// .sendMessage(mDispatcher.obtainMessage(4014));
				// GlobalSettingParameter.isWlanLogined = true;
				// } else
				// {
				// if (s.equals("000002"))
				// mDispatcher.sendMessage(mDispatcher
				// .obtainMessage(4018));
				// else
				// mDispatcher.sendMessage(mDispatcher
				// .obtainMessage(4018));
				// }
				// }
				break;
			}
		}
	}

	private void onOnlineListenMusicThirtySeconds()
	{

	}

	private void onOrderOnlineMusicFail(String s, boolean flag)
	{
		sLogger.v("onOrderOnlineMusicFail() ---> Enter");
		Intent intent = new Intent(mApp, DialogActivity.class);
		intent.setFlags(0x10000000);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype", 1000);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
				R.drawable.cmcc_dialog_question2);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
				R.string.title_order_online_music_atdcontroller);
		if (flag)
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					mApp.getString(R.string.connect_timeout_common));
		else
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					s);
		mApp.startActivity(intent);
		sLogger.v("onOrderOnlineMusicFail() ---> Exit");
	}

	private void onOrderWholeSongFail(String s, boolean flag)
	{
		sLogger.v("onOrderWholeSongFail() ---> Enter");
		Intent intent = new Intent(mApp, DialogActivity.class);
		intent.setFlags(0x10000000);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype", 1002);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
				R.drawable.cmcc_dialog_question2);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
				R.string.title_order_whole_song_atdcontroller);
		if (flag)
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					mApp.getString(R.string.connect_timeout_common));
		else
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					s);
		mApp.startActivity(intent);
		sLogger.v("onOrderWholeSongFail() ---> Exit");
	}

	private void onPresentSongFail(String s, boolean flag)
	{
		sLogger.v("onPresentSongFail() ---> Enter");
		Intent intent = new Intent(mApp, DialogActivity.class);
		intent.setFlags(0x10000000);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype", 1009);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
				R.drawable.cmcc_dialog_question2);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
				R.string.title_present_song_atdcontroller);
		if (flag)
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					mApp.getString(R.string.connect_timeout_common));
		else
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					s);
		mApp.startActivity(intent);
		sLogger.v("onPresentSongFail() ---> Exit");
	}

	private void onRecommendSongFail(String s, boolean flag)
	{
		sLogger.v("onRecommendSongFail() ---> Enter");
		Intent intent = new Intent(mApp, DialogActivity.class);
		intent.setFlags(0x10000000);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype", 1010);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
				R.drawable.cmcc_dialog_question2);
		intent.putExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
				R.string.title_recommend_song_atdcontroller);
		if (flag)
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					mApp.getString(R.string.connect_timeout_common));
		else
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					s);
		mApp.startActivity(intent);
		sLogger.v("onRecommendSongFail() ---> Exit");
	}

	private void onSendHttpRequestFail(MMHttpTask mmhttptask, boolean flag)
	{
		sLogger.v("onSendHttpRequestFail() ---> Enter");
		if (mmhttptask.getRequest() != null)
		{
			switch (mmhttptask.getRequest().getReqType())
			{
			default:
			case 1037:
				break;
			case 5040:
				sLogger.e("Order online music fail !!!");
				onOrderOnlineMusicFail(
						mApp.getString(R.string.default_msg_order_online_music_fail_atdcontroller),
						flag);
				break;
			case 1038:
			case 5041:
				sLogger.e("Cancel online music fail !!!");
				onCancelOnlineMusicFail(
						mApp.getString(R.string.default_msg_cancel_online_music_fail_atdcontroller),
						flag);
				break;
			case 1040:
			case 5043:
				sLogger.e("Order whole song fail !!!");
				onOrderWholeSongFail(
						mApp.getString(R.string.default_msg_order_whole_song_fail_atdcontroller),
						flag);
				break;
			case 1039:
			case 5042:
				sLogger.e("Cancel whole song fail !!!");
				onCancelWholeSongFail(
						mApp.getString(R.string.default_msg_cancel_whole_song_fail_atdcontroller),
						flag);
				break;
			case 1044:
			case 5047:
				sLogger.e("Delete tone fail !!!");
				onDeleteToneFail(
						mApp.getString(R.string.default_msg_del_personal_tone_fail_atdcontroller),
						flag);
				break;
			case 1052:
			case 5056:
				sLogger.e("Present song fail !!!");
				onPresentSongFail(
						mApp.getString(R.string.default_msg_present_song_fail_atdcontroller),
						flag);
				break;
			case 1053:
			case 5055:
				sLogger.e("Recommend song fail !!!");
				onRecommendSongFail(
						mApp.getString(R.string.default_msg_recommend_song_fail_atdcontroller),
						flag);
				break;
			case 5003:
				mDispatcher.sendMessage(mDispatcher.obtainMessage(4018));
				break;
			}
		}
		sLogger.v("onSendHttpRequestFail() ---> Exit");
	}

	private void registerAsyncEvent()
	{
		sLogger.v("registerAsyncEvent ---> Enter");
		this.mApp.getController().addHttpEventListener(3003, this);
		this.mApp.getController().addHttpEventListener(3004, this);
		this.mApp.getController().addHttpEventListener(3006, this);
		this.mApp.getController().addPlayerEventListener(1013, this);
		this.mApp.getController().addPlayerEventListener(1017, this);
		this.mApp.getController().addPlayerEventListener(1018, this);
		this.mApp.getController().addPlayerEventListener(1019, this);
		this.mApp.getController().addPlayerEventListener(1002, this);
		this.mApp.getController().addPlayerEventListener(1004, this);
		this.mApp.getController().addPlayerEventListener(1010, this);
		this.mApp.getController().addPlayerEventListener(1012, this);
		this.mApp.getController().addDLEventListener(2011, this);
		this.mApp.getController().addPlayerEventListener(1005, this);
		this.mApp.getController().addUIEventListener(4009, this);
		this.mApp.getController().addUIEventListener(4019, this);
		this.mApp.getController().addUIEventListener(4020, this);
		this.mApp.getController().addUIEventListener(4021, this);
		this.mApp.getController().addDLEventListener(2003, this);
		this.mApp.getController().addDLEventListener(2004, this);
		this.mApp.getController().addDLEventListener(2007, this);
		this.mApp.getController().addDLEventListener(2006, this);
		this.mApp.getController().addDLEventListener(2008, this);
		sLogger.v("registerAsyncEvent ---> Exit");
	}

	private void showDownloadNotification(DownloadItem downloaditem)
	{
		sLogger.v("showDownloadNotification() ---> Enter");
		// String s = downloaditem.getFileName();
		// Notification notification = new Notification(0x7f0200af, s,
		// System.currentTimeMillis());
		// notification.flags = 2 | notification.flags;
		// RemoteViews remoteviews;
		// Intent intent;
		// if (GlobalSettingParameter.IsBBK_Y3T)
		// remoteviews = new RemoteViews(mApp.getPackageName(), 0x7f030046);
		// else
		// remoteviews = new RemoteViews(mApp.getPackageName(), 0x7f030045);
		// remoteviews.setTextViewText(0x7f0500f7, s);
		// notification.contentView = remoteviews;
		// intent = new Intent(mApp, MyMiGuMusicManagementDetailActivity.class);
		// intent.putExtra("DOWNLOADTABID", 2);
		// notification.contentIntent = PendingIntent.getActivity(mApp, 0,
		// intent,
		// 0);
		// ((NotificationManager)
		// mApp.getSystemService("notification")).notify(2,
		// notification);
		sLogger.v("showDownloadNotification() ---> Exit");
	}

	private void showDownloadRemainNotification(int i)
	{
		sLogger.v("showDownloadRemainNotification() ---> Enter");
		// Resources resources = MobileMusicApplication.getInstance()
		// .getResources();
		// Object aobj[] = new Object[1];
		// aobj[0] = Integer.valueOf(i);
		// String s = resources.getString(0x7f07007f, aobj);
		// Notification notification = new Notification(0x7f0200b0, s,
		// System.currentTimeMillis());
		// notification.flags = 0x20 | notification.flags;
		// RemoteViews remoteviews;
		// Intent intent;
		// if (GlobalSettingParameter.IsBBK_Y3T)
		// remoteviews = new RemoteViews(mApp.getPackageName(), 0x7f030048);
		// else
		// remoteviews = new RemoteViews(mApp.getPackageName(), 0x7f030047);
		// remoteviews.setTextViewText(0x7f0500f8, s);
		// notification.contentView = remoteviews;
		// intent = new Intent(mApp, MyMiGuMusicManagementDetailActivity.class);
		// intent.putExtra("DOWNLOADTABID", 2);
		// notification.contentIntent = PendingIntent.getActivity(mApp, 0,
		// intent,
		// 0);
		// ((NotificationManager)
		// mApp.getSystemService("notification")).notify(3,
		// notification);
		sLogger.v("showDownloadRemainNotification() ---> Exit");
	}

	private void showPlaybackStatusBar()
	{
		sLogger.v("showPlaybackStatusBar() ---> Enter");
		Song song = mPlayerController.getCurrentPlayingItem();
		if (song != null)
		{
			Resources resources = MobileMusicApplication.getInstance()
					.getResources();
			Object aobj[] = new Object[1];
			aobj[0] = song.mTrack;
			Notification notification = new Notification(R.drawable.icon,
					resources.getString(R.string.player_notification_title,
							aobj), System.currentTimeMillis());
			notification.flags = 2 | notification.flags;
			RemoteViews remoteviews;
			String s;
			Intent intent;
			if (GlobalSettingParameter.IsBBK_Y3T)
				remoteviews = new RemoteViews(mApp.getPackageName(),
						R.layout.play_music_notification_bbk);
			else
				remoteviews = new RemoteViews(mApp.getPackageName(),
						R.layout.play_music_notification);
			remoteviews.setTextViewText(R.id.play_music_songname, song.mTrack);
			if (song.mArtist == null || song.mArtist.equals("<unknown>"))
				s = mApp.getString(R.string.unknown_artist_name_db_controller);
			else
				s = song.mArtist;
			remoteviews.setTextViewText(R.id.play_music_singer, s);
			notification.contentView = remoteviews;
			intent = new Intent(mApp, MusicPlayerActivity.class);
			intent.setFlags(0x4000000); // 0x4000000
			notification.contentIntent = PendingIntent.getActivity(mApp, 0,
					intent, 0x8000000);
			((NotificationManager) mApp.getSystemService("notification"))
					.notify(1, notification);
			sLogger.v("showPlaybackStatusBar() ---> Exit");
		}
	}

	public static void writeUpdateTime(Context paramContext, long paramLong)
	{
		SharedPreferences.Editor localEditor = paramContext
				.getSharedPreferences(
						"cmccwm.mobilemusic.ui.AsyncToastDialogController", 0)
				.edit();
		localEditor.putLong("music.update.oldtime", paramLong);
		localEditor.commit();
	}

	public long ReadUpdateTime(Context paramContext)
	{
		return paramContext.getSharedPreferences(
				"cmccwm.mobilemusic.ui.AsyncToastDialogController", 0).getLong(
				"music.update.oldtime", 0L);
	}

	public void handleDLEvent(Message message)
	{
		sLogger.v("handleDLEvent() ---> Enter");
		switch (message.what)
		{
		case 2005:
		case 2009:
		case 2010:
		default:
			sLogger.v("handleDLEvent() ---> Exit");
			return;
		case 2011:
			String s = (String) message.obj;
			Song song = null;
			if (s != null)
				song = mApp.getController().getDBController().getSongByPath(s);
			Intent intent = new Intent(mApp, DialogActivity.class);
			intent.setFlags(0x10000000);
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype",
					1013);
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
					R.drawable.cmcc_dialog_question2);
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
					R.string.title_set_ringtone);
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					mApp.getResources().getString(R.string.msg_set_ringtone));
			if (song != null)
				intent.putExtra(
						"cmccwm.mobilemusic.ui.activity.DialogActivity.songid",
						song.mId);
			else
				sLogger.e("song is null");
			mApp.startActivity(intent);
			break;
		case 2003:
			showDownloadNotification(((DownloadTask) message.obj)
					.getDownloadItem());
			break;
		case 2004:
		case 2006:
			if (BindingContainer.getInstance().isDownloadTaskListEmpty())
				cancelDownloadNotification();
			break;
		case 2007:
			if (BindingContainer.getInstance().isDownloadTaskListEmpty())
				cancelDownloadNotification();
			ArrayList arraylist1 = mDLController.getAllNoneCompleteItems();
			if (arraylist1 != null && arraylist1.size() != 0)
				showDownloadRemainNotification(arraylist1.size());
			else
				cancelDownloadRemainNotification();
			break;
		case 2008:
			ArrayList arraylist = mDLController.getAllNoneCompleteItems();
			if (arraylist != null && arraylist.size() != 0)
				showDownloadRemainNotification(arraylist.size());
			break;
		}
	}

	public void handleMMHttpEvent(Message paramMessage)
	{
		sLogger.v("handleMMHttpEvent() ---> Enter");
		MMHttpTask mmhttptask = (MMHttpTask) paramMessage.obj;
		switch (paramMessage.what)
		{
		case 3005:
		default:
			sLogger.v("handleMMHttpEvent() ---> Exit");
			return;
		case 3003:
			onHttpResponse(mmhttptask);
			break;
		case 3006:
			onSendHttpRequestFail(mmhttptask, true);
			break;
		case 3004:
			onSendHttpRequestFail(mmhttptask, false);
			break;
		}
	}

	public void handlePlayerEvent(Message paramMessage)
	{
		sLogger.v("handlePlayerEvent() ---> Enter");
		switch (paramMessage.what)
		{
		case 1003:
		case 1006:
		case 1007:
		case 1008:
		case 1009:
		case 1011:
		case 1014:
		case 1015:
		case 1016:
		default:
			sLogger.v("handlePlayerEvent() ---> Exit");
			return;
		case 1013:
			if (!MobileMusicApplication.getIsInLogin())
				onOnlineListenMusicThirtySeconds();
			break;
		case 1017:
			if (GlobalSettingParameter.useraccount == null)
			{
				setPauseByThirdSeconds(true);
				mApp.getController().getPlayerController().pause();
				if (!MobileMusicApplication.getIsInLogin())
				{
					Intent intent1 = new Intent(mApp, DialogActivity.class);
					intent1.setFlags(0x10000000);
					intent1.putExtra(
							"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype",
							1016);
					intent1.putExtra(
							"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
							R.drawable.cmcc_dialog_question2);
					intent1.putExtra(
							"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
							R.string.title_information_common);
					intent1.putExtra(
							"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
							mApp.getResources().getString(
									R.string.play_need_login));
					mApp.startActivity(intent1);
				}
			}
			break;
		case 1018:
			Intent intent = new Intent(mApp, DialogActivity.class);
			intent.setFlags(0x10000000);
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype",
					1017);
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid",
					R.drawable.cmcc_dialog_question2);
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid",
					R.string.play_failed);
			intent.putExtra(
					"cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext",
					mApp.getResources().getString(
							R.string.sdcard_spcacenotfill_message_common));
			mApp.startActivity(intent);
			break;
		case 1002:
			cancelPlaybackStatusBar();
			break;
		case DispatcherEventEnum.PLAYER_EVENT_ERROR_OCCURED:
			Toast.makeText(MobileMusicApplication.getInstance(),
					R.string.play_failed, 1).show();
			cancelPlaybackStatusBar();
			break;
		case 1005:
			Toast.makeText(MobileMusicApplication.getInstance(),
					R.string.getfail_data_error_common, 1).show();
			cancelPlaybackStatusBar();
			break;
		case DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_START:
			showPlaybackStatusBar();
			break;
		case 1012:
			cancelPlaybackStatusBar();
			break;
		}
	}

	public void handleUIEvent(Message message)
	{
		sLogger.v("handleUIEvent() ---> Enter");
		switch (message.what)
		{
		default:
		case 4009:
			downloadMusicInfo(message);
			List list = mPlayerController.getNowPlayingList();
			if (list != null && list.size() > 1)
				mPlayerController.next();
			break;
		case 4019:
			GlobalSettingParameter.isLogining = true;
			MMHttpRequest mmhttprequest1 = MMHttpRequestBuilder
					.buildRequest(1001);
			mmhttprequest1.addUrlParams("migu", "1");
			mCurrentTask = mHttpController.sendRequest(mmhttprequest1);
			break;
		case 4020:
			SharedPreferences sharedpreferences = mApp.getSharedPreferences(
					"LOGIN-PREF", 0);
			String s = sharedpreferences.getString(
					"loginactivity.pref.username", null);
			String s1 = sharedpreferences.getString("loginactivity.pref.pswd",
					null);
			GlobalSettingParameter.isLogining = true;
			MMHttpRequest mmhttprequest = MMHttpRequestBuilder
					.buildRequest(5003);
			mmhttprequest.addUrlParams("username", s);
			mmhttprequest.addUrlParams("password", NetUtil.encryptDES(s1,
					MusicBusinessDefine_Net.DES_ENCRYPTION_KEY));
			mmhttprequest.addUrlParams("migu", "1");
			mCurrentTask = mHttpController.sendRequest(mmhttprequest);
			break;
		case 4021:
			sLogger.e((new StringBuilder("The mCurrentTask is: ")).append(
					mCurrentTask).toString());
			if (mCurrentTask != null)
			{
				mHttpController.cancelTask(mCurrentTask);
				mCurrentTask = null;
			}
			break;
		}
	}

	public boolean isPauseByThirdSeconds()
	{
		return this.isPauseByThirdSeconds;
	}

	public boolean monthUpdate(Context paramContext)
	{
		long l1 = ReadUpdateTime(paramContext);
		long l2 = System.currentTimeMillis();
		boolean bool1 = l1 < l2;
		boolean bool2 = false;
		if (bool1)
		{
			boolean bool3 = (l2 - l1) / 86400000L < 30L;
			if (!bool3)
				bool2 = true;
		}
		return bool2;
	}

	public void setPauseByThirdSeconds(boolean paramBoolean)
	{
		this.isPauseByThirdSeconds = paramBoolean;
	}
}