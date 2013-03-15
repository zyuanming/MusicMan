package org.ming.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.ming.center.MobileMusicApplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

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
		int i = 8;
		if (netState != 3
				&& netState != 5
				&& (netState == 1 || netState == 2 || netState == 6 || netState == 7))
			i = 1;
		return i;
	}

	public static int getNetWorkState(Context paramContext)
	{
		int i = 3;
		WifiManager localWifiManager = (WifiManager) MobileMusicApplication
				.getInstance().getSystemService(Context.WIFI_SERVICE);
		NetworkInfo localNetworkInfo;
		NetworkInfo.State localState;
		// 如果Wifi不可用
		if (!localWifiManager.isWifiEnabled())
		{
			localNetworkInfo = ((ConnectivityManager) paramContext
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			localState = localNetworkInfo.getState();
			if ((localNetworkInfo != null)
					&& (localNetworkInfo.getExtraInfo() != null))
			{
				if (localNetworkInfo.getExtraInfo().equalsIgnoreCase("cmwap"))
				{
					if (NetworkInfo.State.CONNECTED != localState)
					{
						i = 5;
					}
				} else if (localNetworkInfo.getExtraInfo().equalsIgnoreCase(
						"cmnet"))
				{
					i = (NetworkInfo.State.CONNECTED == localState) ? 6 : 7;
				}
			} else
			{
				i = 5;
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
		if ((netState == 3) || (netState == 1) || (netState == 6))
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

	public static String encryptDES(String paramString1, String paramString2)
	{
		try
		{
			SecureRandom localSecureRandom = new SecureRandom();
			DESKeySpec localDESKeySpec = new DESKeySpec(paramString2.getBytes());
			SecretKey localSecretKey = SecretKeyFactory.getInstance("DES")
					.generateSecret(localDESKeySpec);
			Cipher localCipher = Cipher.getInstance("DES");
			localCipher.init(1, localSecretKey, localSecureRandom);
			String str2 = new String(EncodeBase64.encodeToChar(
					localCipher.doFinal(paramString1.getBytes()), false))
					+ ":p1";
			return str2;
		} catch (Exception localException)
		{
			localException.printStackTrace();
			String str1 = null;
			return str1;
		}
	}
}
