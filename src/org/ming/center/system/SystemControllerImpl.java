package org.ming.center.system;

import java.io.File;
import java.util.ArrayList;

import org.ming.center.MobileMusicApplication;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class SystemControllerImpl implements SystemController
{
	public static final String ACTION_SMS_SENT = "oms.action.SMS_SENT";
	public static final String CMWAP_APN = "cmwap";
	public static final String EXTERNAL_VOLUME = "external";
	public static final String INTERNAL_VOLUME = "internal";
	public static final String PHONE_ACTION_POWER_DOWN = "oms.action.POWERDOWN";
	private static final MyLogger logger = MyLogger
			.getLogger("SystemControllerImpl");
	private static SystemControllerImpl sInstance = null;
	private MobileMusicApplication mApp = null;
	private ConnectivityManager mConnMgr = null;
	private Dispatcher mDispatcher = null;
	private PhoneListener mPhoneStatusListener = null;
	private BroadcastReceiver mSystemIntentReceiver = null;
	private TelephonyManager mTelephonyManager = null;
	private SmsManager smsManager = null;

	private SystemControllerImpl(
			MobileMusicApplication paramMobileMusicApplication)
	{
		logger.v("SystemControllerImpl() ---> Enter");
		this.mApp = paramMobileMusicApplication;
		this.mDispatcher = this.mApp.getEventDispatcher();
		this.mPhoneStatusListener = new PhoneListener();
		this.mTelephonyManager = ((TelephonyManager) this.mApp
				.getSystemService("phone"));
		this.mTelephonyManager.listen(this.mPhoneStatusListener, 32);
		this.mConnMgr = ((ConnectivityManager) this.mApp
				.getSystemService("connectivity"));
		registerSystemIntentListener();
		logger.v("SystemControllerImpl() ---> Exit");
	}

	public static SystemControllerImpl getInstance(
			MobileMusicApplication paramMobileMusicApplication)
	{
		if (sInstance == null)
			sInstance = new SystemControllerImpl(paramMobileMusicApplication);
		return sInstance;
	}

	private void registerSystemIntentListener()
	{
		logger.v("registerSystemEventListener() ---> Enter");
		if (this.mSystemIntentReceiver == null)
		{
			this.mSystemIntentReceiver = new BroadcastReceiver()
			{
				public void onReceive(Context paramAnonymousContext,
						Intent paramAnonymousIntent)
				{
					String str = paramAnonymousIntent.getAction();
					SystemControllerImpl.logger.i("onReceive(), Got Intent: "
							+ str);
					if (str.equalsIgnoreCase("oms.action.POWERDOWN"))
						SystemControllerImpl.this.mDispatcher
								.sendMessage(SystemControllerImpl.this.mDispatcher
										.obtainMessage(10));
					while (true)
					{

						if ((str.equalsIgnoreCase("android.intent.action.MEDIA_EJECT"))
								|| (str.equalsIgnoreCase("android.intent.action.MEDIA_KILL_ALL"))
								|| (str.equalsIgnoreCase("android.intent.action.ACTION_SHUTDOWN")))
						{
							SystemControllerImpl.this.mDispatcher
									.sendMessage(SystemControllerImpl.this.mDispatcher
											.obtainMessage(4));
						} else if (str
								.equalsIgnoreCase("android.intent.action.MEDIA_MOUNTED"))
						{
							SystemControllerImpl.this.mDispatcher
									.sendMessage(SystemControllerImpl.this.mDispatcher
											.obtainMessage(5));
						} else if (str
								.equalsIgnoreCase("android.intent.action.MEDIA_SCANNER_STARTED"))
						{
							SystemControllerImpl.this.mDispatcher
									.sendMessage(SystemControllerImpl.this.mDispatcher
											.obtainMessage(14));
						} else if (str
								.equalsIgnoreCase("android.intent.action.MEDIA_SCANNER_FINISHED"))
						{
							SystemControllerImpl.this.mDispatcher
									.sendMessage(SystemControllerImpl.this.mDispatcher
											.obtainMessage(15));
						} else if (str.equalsIgnoreCase("oms.action.SMS_SENT"))
						{
							switch (getResultCode())
							{
							default:
								SystemControllerImpl.this.mDispatcher
										.sendMessage(SystemControllerImpl.this.mDispatcher
												.obtainMessage(17));
								break;
							case -1:
								SystemControllerImpl.this.mDispatcher
										.sendMessage(SystemControllerImpl.this.mDispatcher
												.obtainMessage(16));
								break;
							}
						} else if (str
								.equalsIgnoreCase("android.intent.action.HEADSET_PLUG"))
						{
							int i = paramAnonymousIntent
									.getIntExtra("state", 0);
							SystemControllerImpl.logger
									.d("HeadSet stat : " + i);
							if (i == 0)
								SystemControllerImpl.this.mDispatcher
										.sendMessage(SystemControllerImpl.this.mDispatcher
												.obtainMessage(21));
							else if ((2 == i) || (1 == i))
								SystemControllerImpl.this.mDispatcher
										.sendMessage(SystemControllerImpl.this.mDispatcher
												.obtainMessage(20));
							else
								SystemControllerImpl.logger
										.e("Unknown head set plug state !");
							return;
						}
					}
				}
			};
			IntentFilter localIntentFilter1 = new IntentFilter();
			localIntentFilter1.addAction("oms.action.POWERDOWN");
			localIntentFilter1.addAction("oms.action.SMS_SENT");
			localIntentFilter1.addAction("android.intent.action.HEADSET_PLUG");
			localIntentFilter1
					.addAction("android.intent.action.ACTION_SHUTDOWN");
			this.mApp.registerReceiver(this.mSystemIntentReceiver,
					localIntentFilter1);
			IntentFilter localIntentFilter2 = new IntentFilter();
			localIntentFilter2
					.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
			localIntentFilter2
					.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
			localIntentFilter2.addAction("android.intent.action.MEDIA_EJECT");
			localIntentFilter2.addAction("android.intent.action.MEDIA_MOUNTED");
			localIntentFilter2
					.addAction("android.intent.action.MEDIA_KILL_ALL");
			localIntentFilter2.addDataScheme("file");
			this.mApp.registerReceiver(this.mSystemIntentReceiver,
					localIntentFilter2);
		}
		logger.v("registerSystemEventListener() ---> Exit");
	}

	public boolean checkWapStatus()
	{
		boolean flag = true;
		NetworkInfo anetworkinfo[];
		boolean flag1;
		boolean flag2;
		if (mConnMgr != null)
		{
			anetworkinfo = mConnMgr.getAllNetworkInfo();
			flag1 = false;
			flag2 = false;
			if (anetworkinfo != null)
			{
				for (int i = 0; i < anetworkinfo.length; i++)
				{
					if (anetworkinfo[i].getState() == android.net.NetworkInfo.State.CONNECTED)
					{
						String s = anetworkinfo[i].getExtraInfo();
						if (s != null && s.equals("cmwap"))
							flag2 = true;
						else if (anetworkinfo[i].getType() == 1)
							flag1 = true;
					}
					flag = false;
					return flag;
				}
			}
		}
		return flag;
	}

	public void scanDirAsync(String paramString)
	{
		Intent localIntent = new Intent("android.intent.action.MEDIA_MOUNTED");
		localIntent.setData(Uri.fromFile(new File(paramString)));
		this.mApp.sendBroadcast(localIntent);
	}

	public void scanFileAsync(String paramString)
	{
		Intent localIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		localIntent.setData(Uri.fromFile(new File(paramString)));
		this.mApp.sendBroadcast(localIntent);
	}

	public void sendLongSMS(Context paramContext, String paramString1,
			String paramString2)
	{
		PendingIntent localPendingIntent1 = PendingIntent.getBroadcast(
				paramContext, 0, new Intent("oms.action.SMS_SENT"), 0);
		PendingIntent localPendingIntent2 = PendingIntent.getBroadcast(
				paramContext, 0, new Intent(), 0);
		ArrayList localArrayList1 = new ArrayList();
		localArrayList1.add(localPendingIntent1);
		ArrayList localArrayList2 = new ArrayList();
		localArrayList1.add(localPendingIntent2);
		this.smsManager = SmsManager.getDefault();
		ArrayList localArrayList3 = this.smsManager.divideMessage(paramString2);
		this.smsManager.sendMultipartTextMessage(paramString1, null,
				localArrayList3, localArrayList1, localArrayList2);
	}

	public void sendSMS(Context paramContext, String paramString1,
			String paramString2)
	{
		PendingIntent localPendingIntent1 = PendingIntent.getBroadcast(
				paramContext, 0, new Intent("oms.action.SMS_SENT"), 0);
		PendingIntent localPendingIntent2 = PendingIntent.getBroadcast(
				paramContext, 0, new Intent(), 0);
		this.smsManager = SmsManager.getDefault();
		this.smsManager.sendTextMessage(paramString1, null, paramString2,
				localPendingIntent1, localPendingIntent2);
	}

	private class PhoneListener extends PhoneStateListener
	{
		private PhoneListener()
		{}

		public void onCallStateChanged(int paramInt, String paramString)
		{
			switch (paramInt)
			{
			default:
			case 1:
				mDispatcher.sendMessage(mDispatcher.obtainMessage(1));
				break;
			case 2:
				mDispatcher.sendMessage(mDispatcher.obtainMessage(2));
				break;
			case 0:
				mDispatcher.sendMessage(mDispatcher.obtainMessage(3));
				break;
			}
		}
	}
}