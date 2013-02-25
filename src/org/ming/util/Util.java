package org.ming.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.MobileMusicService;
import org.ming.center.business.MusicBusinessDefine_WAP;
import org.ming.center.database.DBControllerImpl;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.download.DLControllerImpl;
import org.ming.center.http.HttpConst;
import org.ming.center.http.item.SongListItem;
import org.ming.center.player.PlayerControllerImpl;
import org.ming.center.system.SystemControllerImpl;
import org.ming.dispatcher.Dispatcher;
import org.ming.ui.activity.SplashActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Process;
import android.os.StatFs;

public class Util
{
	// private static DolbyUtils mDolbyUtils = null;
	private static DefaultHttpClient mHttpsClient = null;
	private static DefaultHttpClient mHttpClient = null;
	private static final MyLogger logger = MyLogger.getLogger("Util");

	private static SharedPreferences preferences;

	public static Song makeSong(SongListItem songlistitem)
	{
		Song song = new Song();
		song.mTrack = songlistitem.title;
		song.mContentId = songlistitem.contentid;
		song.mGroupCode = songlistitem.groupcode;
		song.mArtist = songlistitem.singer;
		song.mMusicType = songlistitem.mMusicType;
		song.mArtUrl = songlistitem.img;
		song.mPoint = Integer.parseInt(songlistitem.point);
		song.isDolby = songlistitem.isdolby.equals("1");
		if (songlistitem.url1 != null && !songlistitem.url1.equals("")
				&& !songlistitem.url1.equals("<unknown>"))
		{
			song.mUrl = songlistitem.url1;
			song.mSize = Long.valueOf(songlistitem.filesize1).longValue();
		}
		if (songlistitem.url2 != null && !songlistitem.url2.equals("")
				&& !songlistitem.url2.equals("<unknown>"))
		{
			song.mUrl2 = songlistitem.url2;
			song.mSize2 = Long.valueOf(songlistitem.filesize2).longValue();
		}
		if (songlistitem.url3 != null && !songlistitem.url3.equals("")
				&& !songlistitem.url3.equals("<unknown>"))
		{
			song.mUrl3 = songlistitem.url3;
			song.mSize3 = Long.valueOf(songlistitem.filesize3).longValue();
		}
		if (song.mMusicType == MusicType.LOCALMUSIC.ordinal())
			song.mUrl = songlistitem.url;
		return song;
	}

	public static boolean isNeedForUserLead(Context context)
	{
		if (context.getClass() == SplashActivity.class)
		{
			logger.v("this is splashActivity");
			preferences = context.getSharedPreferences("isFirstInApplication",
					Context.MODE_WORLD_WRITEABLE);
			return preferences.getBoolean("isFirstInApplication", true);
		}
		return false;
	}

	public static String getRandKey(String s, String s1, String s2)
	{
		return getMD5((new StringBuilder(String.valueOf(s))).append(s1)
				.append(s2).append("c079d16a-bca3-4151-8cb3-c4ea521c086b")
				.toString().getBytes());
	}

	public static String getRandKey(String s, String s1)
	{
		return getMD5((new StringBuilder(String.valueOf(s))).append(s1)
				.append("c079d16a-bca3-4151-8cb3-c4ea521c086b").toString()
				.getBytes());
	}

	public static String getMD5(byte abyte0[])
	{
		String s1;
		String s = null;
		try
		{
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			messagedigest.update(abyte0);
			s1 = byteArrayToHexString(messagedigest.digest());
			s = s1;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return s;
	}

	public static boolean isDolby(Song song)
	{
		boolean flag;
		if (song != null && song.mUrl3 != null
				&& !song.mUrl3.equals("<unknown>") && !song.mUrl3.equals(""))
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static void setNoNeedUserLead(Context context)
	{
		if (context.getClass() == SplashActivity.class)
		{
			Editor editor = preferences.edit();
			editor.putBoolean("isFirstInApplication", false);
			editor.commit();
		}
	}

	public static boolean isOnlineMusic(Song song)
	{
		boolean flag;
		if (song.mMusicType == MusicType.ONLINEMUSIC.ordinal()
				|| song.mMusicType == MusicType.RADIO.ordinal())
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static void exitMobileMusicApp(final boolean useForClear)
	{
		Dispatcher dispatcher = MobileMusicApplication.getInstance()
				.getEventDispatcher();
		dispatcher.sendMessage(dispatcher.obtainMessage(22));
		final MobileMusicApplication app = MobileMusicApplication.getInstance();
		if (PlayerControllerImpl.getInstance(app).isPlaying())
			PlayerControllerImpl.getInstance(app).stop();
		PlayerControllerImpl.getInstance(app).cancelPlaybackStatusBar();
		DLControllerImpl.getInstance(app).cancelDownloadRemainNotification();
		DLControllerImpl.getInstance(app).cancelDownloadNotification();
		shutDownHttpClient();
		MobileMusicService.stopService(app);
		if (useForClear)
			MobileMusicApplication.getInstance().setIsInitService(false);
		(new Handler()).postDelayed(new Runnable()
		{
			public void run()
			{
				if (!useForClear)
				{
					Process.killProcess(Process.myPid());
					DBControllerImpl.getInstance(app).closeDB();
				}
			}
		}, 600L);
	}

	public static void shutDownHttpClient()
	{
		logger.v("shutDownHttpClient() ---> Enter");
		if (mHttpsClient != null)
		{
			mHttpsClient.getConnectionManager().shutdown();
			mHttpsClient = null;
		}
		if (mHttpClient != null)
		{
			mHttpClient.getConnectionManager().shutdown();
			mHttpClient = null;
		}
		logger.v((new StringBuilder(
				"shutDownHttpClient() ---> Exit, mHttpsClient = ")).append(
				mHttpsClient).toString());
	}

	public static boolean getDefaultSettings()
	{
		return true;
	}

	public static boolean isRadioMusic(Song song)
	{
		boolean flag;
		if (song != null && song.mMusicType == MusicType.RADIO.ordinal())
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static boolean checkWapStatus()
	{
		logger.v("checkWapStatus ----> enter");
		ArrayList arraylist;
		ConnectivityManager connectivitymanager;
		boolean flag = false;
		MobileMusicApplication mobilemusicapplication = MobileMusicApplication
				.getInstance();
		arraylist = new ArrayList();
		connectivitymanager = (ConnectivityManager) mobilemusicapplication
				.getSystemService("connectivity");
		if (connectivitymanager == null)
		{
			logger.v("connectivitymanager == null");
			logger.v("return ----> " + flag);
			return flag;
		} else
		{
			NetworkInfo anetworkinfo[];
			anetworkinfo = connectivitymanager.getAllNetworkInfo();
			if (anetworkinfo == null)
			{
				logger.v("anetworkinfo == null");
				logger.v("return ----> " + flag);
				return flag;
			} else
			{
				for (int i = 0; i < anetworkinfo.length; i++)
				{
					if (anetworkinfo[i].getState() == android.net.NetworkInfo.State.CONNECTED)
						arraylist.add(anetworkinfo[i].getTypeName());
				}
				int j;
				j = arraylist.size();
				if (j <= 0)
				{
					logger.v("arraylist.size() <= 0");
					logger.v("return ----> " + flag);
					return flag;
				} else
				{
					boolean flag1;
					flag1 = arraylist.contains("WIFI");
					if (!flag1)
					{
						boolean flag2;
						flag2 = arraylist.contains("mobile");
						if (!flag2)
						{
							logger.v("arraylist do not contains 'mobile'");
							logger.v("return ----> " + flag);
							return flag;
						} else
						{
							for (int k = 0; k < anetworkinfo.length; k++)
							{
								if (anetworkinfo[k].getState() != android.net.NetworkInfo.State.CONNECTED)
								{
									logger.v("not connected to network");
									logger.v("return ----> " + flag);
									return flag;
								} else
								{
									String s = anetworkinfo[k].getExtraInfo();
									if (s == null || !s.equals("cmwap"))
									{
										logger.v("networkinfo not cmwap");
										logger.v("return -----> " + flag);
										return flag;
									}
									flag = true;
								}
							}
						}
					} else
					{
						logger.v("return ----> true");
						return true;
					}
				}
			}
		}
		return flag;
	}

	public static String encodeByMD5(String s)
	{
		String s1 = null;
		if (s != null)
		{
			try
			{
				String s2 = byteArrayToHexString(
						MessageDigest.getInstance("MD5").digest(s.getBytes()))
						.toUpperCase();
				s1 = s2;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return s1;

	}

	public static String byteArrayToHexString(byte abyte0[])
	{
		StringBuffer stringbuffer = new StringBuffer(2 * abyte0.length);
		int i = 0;
		do
		{
			if (i >= abyte0.length)
				return stringbuffer.toString();
			int j = abyte0[i];
			if (j < 0)
				j += 256;
			String s = Integer.toHexString(j);
			if (s.length() % 2 == 1)
				s = (new StringBuilder("0")).append(s).toString();
			stringbuffer.append(s);
			i++;
		} while (true);
	}

	public static String[] breakLine(String s, Paint paint, int i)
	{
		String as[] = {};
		if (s != null && i >= 0)
		{
			ArrayList arraylist = new ArrayList();
			int j = 0;
			int k = s.length();
			int l = 1;
			int i1 = paint.breakText(s, 0, k, true, i, null);
			do
			{
				if (j + i1 > k)
				{
					arraylist.add(s.substring(j, j + i1));
					as = (String[]) arraylist.toArray(new String[l]);
					return as;
				}
				l++;
				int j1 = s.lastIndexOf(' ', j + i1);
				if (j1 > j)
				{
					arraylist.add(s.substring(j, j1));
					j = j1 + 1;
				} else
				{
					arraylist.add(s.substring(j, j + i1));
					j += i1;
				}
				i1 = paint.breakText(s, j, k, true, i, null);
			} while (true);
		}
		return as;
	}

	public static byte[] getUTF8Bytes(String s)
	{
		byte abyte0[] = new byte[0];
		if (s != null)
		{
			try
			{
				byte abyte2[] = s.getBytes("UTF-8");
				abyte0 = abyte2;
				ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
				DataOutputStream dataoutputstream = new DataOutputStream(
						bytearrayoutputstream);
				dataoutputstream.writeUTF(s);
				byte abyte1[] = bytearrayoutputstream.toByteArray();
				bytearrayoutputstream.close();
				dataoutputstream.close();
				abyte0 = new byte[-2 + abyte1.length];
				System.arraycopy(abyte1, 2, abyte0, 0, abyte0.length);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return abyte0;
	}

	public static String makeTimeString(long l)
	{
		StringBuffer stringbuffer = new StringBuffer();
		long l1 = l / 60000L;
		Object obj;
		long l2;
		Object obj1;
		if (l1 < 10L)
			obj = (new StringBuilder("0")).append(l1).toString();
		else
			obj = Long.valueOf(l1);
		stringbuffer.append(obj);
		stringbuffer.append(":");
		l2 = (l / 1000L) % 60L;
		if (l2 < 10L)
			obj1 = (new StringBuilder("0")).append(l2).toString();
		else
			obj1 = Long.valueOf(l2);
		stringbuffer.append(obj1);
		return stringbuffer.toString();
	}

	public static boolean LocalSongIsDolby(Song song)
	{
		return false;
		// if (mDolbyUtils == null)
		// mDolbyUtils = new DolbyUtils();
		// boolean flag;
		// if (mDolbyUtils.startParse(song.mUrl) != 0
		// || !"China Mobile".equalsIgnoreCase(mDolbyUtils
		// .getDistributor()))
		// flag = false;
		// else
		// flag = true;
		// return flag;
	}

	public static Matcher getUrlMatcher(String s)
	{
		return Pattern.compile("http://[a-zA-Z0-9./\\s]+").matcher(s);
	}

	public static DefaultHttpClient createNetworkClient(boolean flag)
	{
		DefaultHttpClient defaulthttpclient1;
		if (NetUtil.netState == 8)
			defaulthttpclient1 = null;
		else if (!NetUtil.isNetStateWap() && !flag)
		{
			logger.v("createNetworkClient() ---> !WlanUtils.isNetStateWap() && !isHttpReqIfWlan");
			if (NetUtil.netState != 1 && NetUtil.netState != 6)
			{
				logger.d("Wlan has been closed.");
				defaulthttpclient1 = null;
			}
		} else
		{
			MobileMusicApplication mobilemusicapplication = MobileMusicApplication
					.getInstance();
			if (NetUtil.netState != 3
					&& NetUtil.netState != 5
					&& !SystemControllerImpl
							.getInstance(mobilemusicapplication)
							.checkWapStatus())
			{
				logger.d("WAP has been closed.");
				defaulthttpclient1 = null;
			}
		}

		if (mHttpsClient == null)
			mHttpsClient = createHttpsClient();
		DefaultHttpClient defaulthttpclient = mHttpsClient;
		if (NetUtil.netState == 3)
		{
			HttpHost httphost = new HttpHost(
					MusicBusinessDefine_WAP.CMCC_WAP_PROXY_HOST,
					MusicBusinessDefine_WAP.CMCC_WAP_PROXY_PORT);
			if (defaulthttpclient == null)
				defaulthttpclient = createHttpsClient();
			defaulthttpclient.getParams().setParameter(
					"http.route.default-proxy", httphost);
		}
		logger.v("createNetworkClient() ---> Exit");
		defaulthttpclient1 = defaulthttpclient;
		if (mHttpClient == null)
			mHttpClient = createHttpClient();
		defaulthttpclient = mHttpClient;
		return defaulthttpclient1;
	}

	private static DefaultHttpClient createHttpClient()
	{
		DefaultHttpClient defaulthttpclient = new DefaultHttpClient();
		DefaultHttpClient defaulthttpclient1;
		try
		{
			SchemeRegistry schemeregistry = new SchemeRegistry();
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(null, null);
			CustomSSLSocketFactory customsslsocketfactory = new CustomSSLSocketFactory(
					keystore);
			customsslsocketfactory
					.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schemeregistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schemeregistry.register(new Scheme("https", customsslsocketfactory,
					443));
			BasicHttpParams basichttpparams = new BasicHttpParams();
			HttpProtocolParams
					.setVersion(basichttpparams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(basichttpparams, "UTF-8");
			HttpProtocolParams.setUseExpectContinue(basichttpparams, false);
			HttpConnectionParams.setConnectionTimeout(basichttpparams,
					HttpConst.NETWORK_TIME_OUT);
			HttpConnectionParams.setSoTimeout(basichttpparams,
					HttpConst.NETWORK_TIME_OUT);
			defaulthttpclient1 = new DefaultHttpClient(
					new ThreadSafeClientConnManager(basichttpparams,
							schemeregistry), basichttpparams);
			defaulthttpclient1
					.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(
							3, false));
			defaulthttpclient1.getCredentialsProvider().setCredentials(
					new AuthScope(null, -1),
					new UsernamePasswordCredentials("admin", "admin"));
			defaulthttpclient = defaulthttpclient1;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return defaulthttpclient;

	}

	private static DefaultHttpClient createHttpsClient()
	{
		DefaultHttpClient defaulthttpclient = new DefaultHttpClient();
		DefaultHttpClient defaulthttpclient1;
		try
		{
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(null, null);
			CustomSSLSocketFactory customsslsocketfactory = new CustomSSLSocketFactory(
					keystore);
			customsslsocketfactory
					.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			BasicHttpParams basichttpparams = new BasicHttpParams();
			HttpProtocolParams
					.setVersion(basichttpparams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(basichttpparams, "utf-8");
			HttpProtocolParams.setUseExpectContinue(basichttpparams, false);
			HttpConnectionParams.setConnectionTimeout(basichttpparams,
					HttpConst.NETWORK_TIME_OUT);
			HttpConnectionParams.setSoTimeout(basichttpparams,
					HttpConst.NETWORK_TIME_OUT);
			SchemeRegistry schemeregistry = new SchemeRegistry();
			schemeregistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schemeregistry.register(new Scheme("https", customsslsocketfactory,
					443));
			defaulthttpclient1 = new DefaultHttpClient(
					new ThreadSafeClientConnManager(basichttpparams,
							schemeregistry), basichttpparams);
			defaulthttpclient1.getCredentialsProvider().setCredentials(
					new AuthScope(null, -1),
					new UsernamePasswordCredentials("admin", "admin"));
			defaulthttpclient = defaulthttpclient1;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return defaulthttpclient;
	}

	private static class CustomSSLSocketFactory extends SSLSocketFactory
	{

		public Socket createSocket() throws IOException
		{
			return sslContext.getSocketFactory().createSocket();
		}

		public Socket createSocket(Socket socket, String s, int i, boolean flag)
				throws IOException, UnknownHostException
		{
			return sslContext.getSocketFactory().createSocket(socket, s, i,
					flag);
		}

		private SSLContext sslContext;

		public CustomSSLSocketFactory(KeyStore keystore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException
		{
			super(keystore);
			sslContext = SSLContext.getInstance("TLS");
			X509TrustManager x509trustmanager = new Cls();
			sslContext
					.init(null, new TrustManager[] { x509trustmanager }, null);
		}

		class Cls implements X509TrustManager
		{
			public X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException
			{
				// TODO Auto-generated method stub
			}
		}
	}

	public static final long getFreeSpace(String s)
	{
		long l = 0L;
		if (s != null)
		{
			long l1;
			int i;
			StatFs statfs = new StatFs(s);
			l1 = statfs.getBlockSize();
			i = statfs.getAvailableBlocks();
			l = l1 * (long) i - 0x200000L;
		}
		return l;
	}

	public static final String getRingtoneStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_RINGTONE_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_RINGTONE_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	public static final String getMVStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_MV_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_MV_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	public static final String getUpdateStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_UPDATE_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_UPDATE_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	public static final String getSkinStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_SKIN_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_SKIN_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	public static final String getDoblySongStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_DOBLY_MUSIC_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_DOBLY_MUSIC_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	public static final String getSongStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_MUSIC_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_MUSIC_STORE_SD_DIR;
		else
			s = null;
		return s;
	}
}
