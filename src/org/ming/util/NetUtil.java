package org.ming.util;

import org.ming.center.MobileMusicApplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetUtil
{
	public static final int NET_STATE_WLAN = 1;
	public static final int NET_STATE_WLAN_UNAVAILABLE = 2;
	public static final int NET_STATE_CMWAP = 3;
	public static final int NET_STATE_CMWAP_UNAVAILABLE = 5;
	public static final int NET_STATE_NET = 6;
	public static final int NET_STATE_NET_UNAVAILABLE = 7;
	public static final int NET_STATE_UNAVAILABLE = 8;
	public static final int NETWORK_DOWNLOAD_RETRY_TIMES = 6;
	public static int netState = -1;

	public static int getDownLoadNetType()
	{
		int i = 3;
		if ((netState == i) || (netState == 5))
			;
		while (true)
		{
			if ((netState == 1) || (netState == 2) || (netState == 6)
					|| (netState == 7))
				i = 1;
		}
	}

	public static int getNetWorkState(Context paramContext)
	{
		int i = 3;
		WifiManager localWifiManager = (WifiManager) MobileMusicApplication
				.getInstance().getSystemService("wifi");
		NetworkInfo localNetworkInfo;
		NetworkInfo.State localState;
		// 如果Wifi不可用
		if (!localWifiManager.isWifiEnabled())
		{
			localNetworkInfo = ((ConnectivityManager) paramContext
					.getSystemService("connectivity")).getNetworkInfo(0);
			localState = localNetworkInfo.getState();
			if ((localNetworkInfo != null)
					&& (localNetworkInfo.getExtraInfo() != null))
			{
				if (localNetworkInfo.getExtraInfo().equalsIgnoreCase("cmwap"))
				{
					i = (NetworkInfo.State.CONNECTED != localState) ? 5 : 3;
				} else if (localNetworkInfo.getExtraInfo().equalsIgnoreCase(
						"cmnet"))
				{
					i = (NetworkInfo.State.CONNECTED == localState) ? 6 : 7;
				}
			}
		} else
		{
			WifiInfo localWifiInfo = localWifiManager.getConnectionInfo();
			if (localWifiInfo != null)
			{
				i = (localWifiInfo.getIpAddress() != 0) ? 1 : 2;
			}
		}
		return i;
	}

	public static boolean isConnection()
	{
		int i = 1;
		if ((netState == 3) || (netState == i) || (netState == 6))
		{
			return true;
		}
		return false;
	}

	public static boolean isNetStateNet()
	{
		if ((netState == 6) || (netState == 7))
		{
			return true;
		}
		return false;
	}

	public static boolean isNetStateWLAN()
	{
		int i = 1;
		if (netState == i)
		{
			return true;
		}
		return false;
	}

	public static boolean isNetStateWap()
	{
		if ((netState == 3) || (netState == 5))
		{
			return true;
		}
		return false;
	}
}
