package org.ming.ui.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.ming.R;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.http.item.MusicListColumnItem;
import org.ming.center.http.item.NotificationItem;
import org.ming.ui.activity.MusicPlayerActivity;
import org.ming.ui.activity.online.MusicOnlineMusicAlbumDetailActivity;
import org.ming.ui.activity.online.MusicOnlineMusicColumnDetailActivity;
import org.ming.ui.activity.online.MusicOnlineRecommendInfoDetailActivity;
import org.ming.util.NetUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class NotificationService extends Service
{
	private int CMD_STOP_SERVICE = 2;
	private int CONNECTION_TIMEOUT_INT = 3000;
	private int SO_TIMEOUT_INT = 3000;
	private CommandReceiver cmdReceiver;
	Handler handler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{
			if (paramAnonymousMessage.obj != null)
			{
				pushDataList = ((List) paramAnonymousMessage.obj);
				mNotificationManager = ((NotificationManager) NotificationService.this
						.getSystemService("notification"));
				mNotification = new Notification();

				for (int i = 0; i < pushDataList.size();)
				{
					switch (Integer.parseInt(((NotificationItem) pushDataList
							.get(i)).type))
					{
					default:
						break;
					case 1:
						mIntent = new Intent(NotificationService.this,
								MusicOnlineRecommendInfoDetailActivity.class);
						mIntent.putExtra(
								"mobi.mobiemusic.ui.activity.online.onlinemusicsubscribeactivity.entry.url",
								((NotificationItem) NotificationService.this.pushDataList
										.get(i)).url);
						mIntent.putExtra("fromPushService", true);
						mIntent.setFlags(69206016);
						mNotification.icon = R.drawable.icon;
						mNotification.tickerText = ((NotificationItem) NotificationService.this.pushDataList
								.get(i)).msg;
						mNotification.defaults = 1;
						mNotification.flags = 16;
						mPendingIntent = PendingIntent.getActivity(
								NotificationService.this, 199, mIntent, 0);
						mNotification.setLatestEventInfo(
								NotificationService.this,
								((NotificationItem) pushDataList.get(i)).title,
								((NotificationItem) pushDataList.get(i)).msg,
								mPendingIntent);
						mNotificationManager.notify(199, mNotification);
						i++;
						break;
					case 2:
						mIntent = new Intent(NotificationService.this,
								MusicPlayerActivity.class);
						Bundle localBundle = new Bundle();
						localBundle.putInt("pushSongFlag", 2);
						localBundle.putString("pushSongUrl",
								((NotificationItem) pushDataList.get(i)).url);
						localBundle.putBoolean("fromPushService", true);
						mIntent.putExtras(localBundle);
						mIntent.setFlags(69206016);
						mNotification.icon = R.drawable.icon;
						mNotification.tickerText = ((NotificationItem) pushDataList
								.get(i)).msg;
						mNotification.defaults = 1;
						mNotification.flags = 16;
						mPendingIntent = PendingIntent.getActivity(
								NotificationService.this, 299, mIntent, 0);
						mNotification.setLatestEventInfo(
								NotificationService.this,
								((NotificationItem) pushDataList.get(i)).title,
								((NotificationItem) pushDataList.get(i)).msg,
								mPendingIntent);
						mNotificationManager.notify(299, mNotification);
						i++;
						break;
					case 3:
						mIntent = new Intent(NotificationService.this,
								MusicOnlineMusicColumnDetailActivity.class);
						MusicListColumnItem localMusicListColumnItem = new MusicListColumnItem();
						localMusicListColumnItem.title = ((NotificationItem) NotificationService.this.pushDataList
								.get(i)).title;
						localMusicListColumnItem.category_type = "0";
						localMusicListColumnItem.url = ((NotificationItem) NotificationService.this.pushDataList
								.get(i)).url;
						mIntent.putExtra("title",
								localMusicListColumnItem.title);
						mIntent.putExtra("COLUMITEM", localMusicListColumnItem);
						mIntent.putExtra("fromPushService", true);
						mIntent.setFlags(69206016);
						mNotification.icon = R.drawable.icon;
						mNotification.tickerText = ((NotificationItem) pushDataList
								.get(i)).msg;
						mNotification.defaults = 1;
						mNotification.flags = 16;
						mPendingIntent = PendingIntent.getActivity(
								NotificationService.this, 399, mIntent, 0);
						mNotification.setLatestEventInfo(
								NotificationService.this,
								((NotificationItem) pushDataList.get(i)).title,
								((NotificationItem) pushDataList.get(i)).msg,
								mPendingIntent);
						mNotificationManager.notify(399, mNotification);
						i++;
						break;
					case 4:
						mIntent = new Intent(NotificationService.this,
								MusicOnlineMusicAlbumDetailActivity.class);
						mIntent.putExtra("title",
								((NotificationItem) pushDataList.get(i)).title);
						mIntent.putExtra("album_song_url",
								((NotificationItem) pushDataList.get(i)).url);
						mIntent.putExtra("fromPushService", true);
						mIntent.setFlags(69206016);
						mNotification.icon = R.drawable.icon;
						mNotification.tickerText = ((NotificationItem) pushDataList
								.get(i)).msg;
						mNotification.defaults = 1;
						mNotification.flags = 16;
						mPendingIntent = PendingIntent.getActivity(
								NotificationService.this, 499, mIntent, 0);
						mNotification.setLatestEventInfo(
								NotificationService.this,
								((NotificationItem) pushDataList.get(i)).title,
								((NotificationItem) pushDataList.get(i)).msg,
								mPendingIntent);
						mNotificationManager.notify(499, mNotification);
						i++;
						break;
					}
				}
			}
		}
	};
	Intent mIntent;
	Notification mNotification;
	NotificationManager mNotificationManager;
	PendingIntent mPendingIntent;
	private SharedPreferences preferences;
	private List<NotificationItem> pushDataList;
	private boolean pushFlag;

	private void getPushNotification()
	{
		(new Thread()
		{
			public void run()
			{
				do
				{
					if (!pushFlag)
						return;
					try
					{
						Thread.sleep(20000L);
					} catch (Exception exception)
					{
						exception.printStackTrace();
					}
					getPushData();
				} while (true);
			}
		}).start();
	}

	public List<NotificationItem> checkNewPush(List<NotificationItem> paramList)
	{
		ArrayList localArrayList = new ArrayList();
		this.preferences = getSharedPreferences("PushNotificationId", 0);
		int i = this.preferences.getInt("pushId", 0);
		if (paramList != null)
		{
			for (int j = 0; j < paramList.size(); j++)
			{
				int k = Integer
						.parseInt(((NotificationItem) paramList.get(j)).id);
				if (i < k)
				{
					localArrayList.add((NotificationItem) paramList.get(j));
					this.preferences.edit().putInt("pushId", k).commit();
				}
			}
		}
		return localArrayList;
	}

	public void getPushData()
	{
		if (!NetUtil.isNetStateWLAN() && !NetUtil.isNetStateNet())
		{
			if (NetUtil.isNetStateWap())
			{
				pushFlag = false;
				stopSelf();
			} else
			{
				pushFlag = false;
				stopSelf();
			}
		} else
		{
			String s = (new StringBuilder(
					"https://218.200.227.224/rdp2/v5.3/pushnotice.do?ua="))
					.append(GlobalSettingParameter.LOCAL_PARAM_USER_AGENT)
					.append("&version=")
					.append(GlobalSettingParameter.LOCAL_PARAM_VERSION)
					.toString();
			if (s != null && !"".equals(s))
			{
				BasicHttpParams basichttpparams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(basichttpparams,
						CONNECTION_TIMEOUT_INT);
				HttpConnectionParams.setSoTimeout(basichttpparams,
						SO_TIMEOUT_INT);
				DefaultHttpClient defaulthttpclient = new DefaultHttpClient(
						basichttpparams);
				HttpGet httpget = new HttpGet(s);
				try
				{
					pushFlag = false;
					HttpResponse httpresponse = defaulthttpclient
							.execute(httpget);
					stopSelf();
					if (httpresponse.getStatusLine().getStatusCode() == 200)
					{
						PushXmlParser pushxmlparser = new PushXmlParser(
								EntityUtils.toString(httpresponse.getEntity(),
										"UTF-8").getBytes("UTF-8"));
						if ("000000"
								.equals(pushxmlparser.getValueByTag("code")))
						{
							Message message = new Message();
							message.obj = checkNewPush(pushxmlparser
									.getPushDataList());
							handler.sendMessage(message);
						}
					}
				} catch (ClientProtocolException clientprotocolexception)
				{
					clientprotocolexception.printStackTrace();
				} catch (IOException ioexception)
				{
					ioexception.printStackTrace();
				}
			} else
			{
				pushFlag = false;
				stopSelf();
			}
		}
	}

	public IBinder onBind(Intent paramIntent)
	{
		return null;
	}

	public void onCreate()
	{
		this.pushFlag = true;
	}

	public void onDestroy()
	{
		unregisterReceiver(this.cmdReceiver);
		super.onDestroy();
	}

	public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
	{
		this.cmdReceiver = new CommandReceiver();
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter
				.addAction("cmccwm.mobilemusic.ui.util.NotificationService");
		registerReceiver(this.cmdReceiver, localIntentFilter);
		getPushNotification();
		return super.onStartCommand(paramIntent, paramInt1, paramInt2);
	}

	private class CommandReceiver extends BroadcastReceiver
	{
		private CommandReceiver()
		{}

		public void onReceive(Context paramContext, Intent paramIntent)
		{
			if (paramIntent.getIntExtra("commendsign", 0) == NotificationService.this.CMD_STOP_SERVICE)
			{
				NotificationService.this.pushFlag = false;
				NotificationService.this.stopSelf();
			}
		}
	}

	private class PushXmlParser
	{
		private Element root;

		public PushXmlParser(byte[] arg2)
		{
			if (arg2 != null)
			{
				ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(
						arg2);
				DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory
						.newInstance();
				try
				{
					this.root = localDocumentBuilderFactory
							.newDocumentBuilder()
							.parse(localByteArrayInputStream)
							.getDocumentElement();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		public List<NotificationItem> getPushDataList()
		{
			Object obj;
			if (root != null)
			{
				NodeList nodelist;
				obj = new ArrayList();
				nodelist = root.getElementsByTagName("list");
				if (nodelist == null || nodelist.getLength() == 0)
					obj = null;
				else
				{
					for (int i = 0; i < nodelist.getLength(); i++)
					{
						NodeList nodelist1;
						nodelist1 = ((Element) nodelist.item(i))
								.getElementsByTagName("item");
						for (int j = 0; j < nodelist1.getLength(); j++)
						{
							Node node = nodelist1.item(j);
							NotificationItem notificationitem = new NotificationItem();
							notificationitem.setId(node.getAttributes()
									.getNamedItem("id").getNodeValue());
							notificationitem.setTitle(node.getAttributes()
									.getNamedItem("title").getNodeValue());
							notificationitem.setMsg(node.getAttributes()
									.getNamedItem("msg").getNodeValue());
							notificationitem.setPushtime(node.getAttributes()
									.getNamedItem("push_time").getNodeValue());
							notificationitem.setType(node.getAttributes()
									.getNamedItem("type").getNodeValue());
							notificationitem.setUrl(node.getAttributes()
									.getNamedItem("url").getNodeValue());
							((List) (obj)).add(notificationitem);
						}
					}
				}
			} else
			{
				obj = null;
			}
			return ((List) (obj));
		}

		public String getValueByTag(String paramString)
		{
			Element localElement = this.root;
			String str = null;
			if (localElement != null)
			{
				NodeList localNodeList1 = this.root
						.getElementsByTagName(paramString);
				if (localNodeList1 != null)
				{
					int i = localNodeList1.getLength();
					if (i != 0)
					{
						str = "";
						NodeList localNodeList2 = localNodeList1.item(0)
								.getChildNodes();
						for (int j = 0; j < localNodeList2.getLength(); j++)
							if (localNodeList2.item(j) != null)
							{
								Node localNode = localNodeList2.item(j);
								if (localNode != null)
									str = str + localNode.getNodeValue();
							}
					}
				}
			}
			return str;
		}
	}
}