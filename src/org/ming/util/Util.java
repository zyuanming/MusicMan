package org.ming.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
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
import org.ming.center.download.DownloadItem;
import org.ming.center.http.HttpConst;
import org.ming.center.http.item.SongListItem;
import org.ming.center.player.PlayerControllerImpl;
import org.ming.center.system.SystemControllerImpl;
import org.ming.dispatcher.Dispatcher;
import org.ming.ui.activity.SplashActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.StatFs;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;

public class Util
{
	private static final String HEADSET_PATH = "/sys/class/accessory/headset/online";
	private static final String PREFS_NAME = "MobileMusic4";
	public static final String SONG_LYRIC_SUFFIX = "lrc";
	public static final int TIME_KILL_PROCESS_AFTER_FINISH_ACTIVITIES = 600;
	private static final MyLogger logger = MyLogger.getLogger("Util");
	public static Dialog mApnDialog;
	// private static DolbyUtils mDolbyUtils = null;
	private static DefaultHttpClient mHttpClient = null;
	private static DefaultHttpClient mHttpsClient = null;
	public static boolean mIsNewestVersion = true;
	public static Dialog mWlanCloseDialog;
	private static final Uri sArtworkUri = Uri
			.parse("content://media/external/audio/albumart");
	private static final android.graphics.BitmapFactory.Options sBitmapOptions = new android.graphics.BitmapFactory.Options();
	public static String sCharset;

	private static SharedPreferences preferences;

	/**
	 * 该函数返回整形 -1：代表下载文件出错 0：代表下载文件成功 1：代表文件已经存在
	 */
	public static int downFile(String urlStr, String path, String fileName)
	{
		InputStream inputStream = null;
		try
		{
			if (FileUtils.isFileExits(path, fileName))
			{
				Log.d("ming", "File is Exits");
				return 0;
			} else
			{
				inputStream = getInputStreamFromUrl(urlStr);
				File resultFile = FileUtils.write2SDFromInput(path, fileName,
						inputStream);
				if (resultFile == null)
				{
					return -1;
				} else
				{
					Log.d("ming", "Download Success");
					return 1;
				}
			}
		} catch (Exception e)
		{
			Log.d("ming", "Exception -- " + e);
			e.printStackTrace();
			return -1;
		} finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据URL得到输入流
	 * 
	 * @param urlStr
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream getInputStreamFromUrl(String urlStr)
			throws MalformedURLException, IOException
	{
		URL url = new URL(urlStr);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		InputStream inputStream = urlConn.getInputStream();
		return inputStream;
	}

	public ProgressDialog showProgressDialog(Context context, String s)
	{
		ProgressDialog progressdialog = new ProgressDialog(context);
		progressdialog.setMessage(s);
		progressdialog.setIndeterminate(true);
		progressdialog.setCancelable(true);
		progressdialog.show();
		return progressdialog;
	}

	public void stopProgressDialog(ProgressDialog progressdialog)
	{
		if (progressdialog != null)
			progressdialog.dismiss();
	}

	public static String verify(String s)
	{
		StringBuffer stringbuffer = new StringBuffer();
		char ac[] = s.toCharArray();
		int i = 0;
		do
		{
			if (i >= ac.length)
			{
				return stringbuffer.toString();
			}
			if (ac[i] == '[' && i - 1 >= 0 && ac[i - 1] != ']')
			{
				if (ac[i - 1] != '\n' && ac[i - 1] != '\r')
					stringbuffer.append('\n');
				stringbuffer.append(ac[i]);
			} else
			{
				stringbuffer.append(ac[i]);
			}
			i++;
		} while (true);
	}

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
		Log.d("Util", "song.mTrack = " + song.mTrack);
		Log.d("Util", "song.mContentId = " + song.mContentId);
		Log.d("Util", "song.mGroupCode" + song.mGroupCode);
		Log.d("Util", "song.mArtist" + song.mArtist);
		Log.d("Util", "song.mMusicType" + song.mMusicType);
		Log.d("Util", "song.mArtUrl" + song.mArtist);
		Log.d("Util", "song.mPoint" + song.mPoint);
		Log.d("Util", "song.isDolby" + song.isDolby);
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

	public static Song makeSong2(SongListItem songlistitem)
	{
		Song song = new Song();
		song.mUrl = songlistitem.url;
		Log.d("song", "song.mUrl" + songlistitem.url);
		song.crbtValidity = songlistitem.crbtValidity;
		song.ringSongPrice = songlistitem.price;
		song.mTrack = songlistitem.songName;
		song.mContentId = songlistitem.songName;
		song.mGroupCode = songlistitem.musicid;
		song.mArtist = songlistitem.singerName;
		song.mMusicType = songlistitem.mMusicType;
		song.mArtUrl = songlistitem.img;
		song.mPoint = Integer.parseInt(songlistitem.point);
		song.isDolby = songlistitem.isdolby.equals("1");
		Log.d("Util", "song.mTrack = " + song.mTrack);
		Log.d("Util", "song.mContentId = " + song.mContentId);
		Log.d("Util", "song.mGroupCode" + song.mGroupCode);
		Log.d("Util", "song.mArtist" + song.mArtist);
		Log.d("Util", "song.mMusicType" + song.mMusicType);
		Log.d("Util", "song.mArtUrl" + song.mArtist);
		Log.d("Util", "song.mPoint" + song.mPoint);
		Log.d("Util", "song.isDolby" + song.isDolby);
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

	public static SongListItem makeSongListItem(Song song)
	{
		SongListItem songlistitem;
		songlistitem = new SongListItem();
		songlistitem.title = song.mTrack;
		songlistitem.contentid = song.mContentId;
		songlistitem.groupcode = song.mGroupCode;
		songlistitem.singer = song.mArtist;
		songlistitem.point = Integer.toString(song.mPoint);
		songlistitem.img = song.mArtUrl;
		songlistitem.mMusicType = song.mMusicType;
		String s;
		Log.d("Utils", "song.mTrack = " + song.mTrack);
		Log.d("Utils", "song.mContentId = " + song.mContentId);
		Log.d("Utils", "song.mGroupCode" + song.mGroupCode);
		Log.d("Utils", "song.mArtist" + song.mArtist);
		Log.d("Utils", "song.mMusicType" + song.mMusicType);
		Log.d("Utils", "song.mArtUrl" + song.mArtist);
		Log.d("Utils", "song.mPoint" + song.mPoint);
		Log.d("Utils", "song.isDolby" + song.isDolby);
		if (song.isDolby)
			s = "1";
		else
			s = "0";
		songlistitem.isdolby = s;
		if (songlistitem.mMusicType != MusicType.LOCALMUSIC.ordinal())
		{
			if (isDolby(song))
				songlistitem.isdolby = "1";
		} else
		{
			songlistitem.url = song.mUrl;
		}
		return songlistitem;
	}

	public static final int setRingTone(Context context, long l)
	{
		int i;
		logger.v("setRingTone() ---> Enter");
		byte byte0 = -1;
		i = ((AudioManager) context.getSystemService("audio")).getRingerMode();
		if (i != 0 && 1 != i)
		{
			ContentResolver contentresolver;
			Uri uri;
			Cursor cursor;
			contentresolver = context.getContentResolver();
			uri = ContentUris
					.withAppendedId(
							android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							l);
			String as[] = { "_id", "_data", "title" };
			String s = (new StringBuilder("_id=")).append(l).toString();
			cursor = query(
					context,
					android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					as, s, null, null);
			if (cursor != null)
			{
				boolean flag;
				cursor.moveToFirst();
				String s1 = cursor.getString(cursor
						.getColumnIndexOrThrow("_data"));
				logger.v((new StringBuilder("Ringtone path is: ")).append(s1)
						.toString());
				flag = android.provider.Settings.System.putString(
						contentresolver, "ringtone", uri.toString());
				if (flag)
				{
					if (cursor != null)
						cursor.close();
					byte0 = 0;
				} else
				{
					if (cursor != null)
						cursor.close();
					byte0 = -2;
				}
				// logger.e("Set ringtone fail!", exception1);
				// Toast.makeText(context,
				// (new StringBuilder()).append(exception1).toString(), 0)
				// .show();
			}
			ContentValues contentvalues = new ContentValues(2);
			contentvalues.put("is_ringtone", "1");
			contentvalues.put("is_alarm", "1");
			contentvalues.put("is_notification", "1");
			contentresolver.update(uri, contentvalues, null, null);
			logger.v("setRingTone() ---> Exit");
			// logger.e((new
			// StringBuilder("couldn't set ringtone flag for id "))
			// .append(l).toString());
		}

		logger.e("In slient mode or vibrate mode, can not set ringtone.");
		return byte0;
	}

	public static boolean isWiredHeadsetOn()
	{
		boolean flag = false;
		try
		{
			FileReader filereader = new FileReader(HEADSET_PATH);
			int i = filereader.read();
			int j = i - 48;
			logger.e((new StringBuilder("Flag = ")).append(j).toString());

			if (j > 0)
				flag = true;
		} catch (IOException ioexception)
		{
			logger.e((new StringBuilder("Read  Error! ")).append(ioexception)
					.toString());
		}
		return flag;
	}

	public static Song makeDownloadItemToSong(DownloadItem downloaditem)
	{
		Song song = new Song();
		song.mId = downloaditem.getItemId();
		song.mTrack = downloaditem.getShowName();
		song.mContentId = downloaditem.getContentId();
		song.mGroupCode = downloaditem.getGroupCode();
		song.mArtist = downloaditem.getArtist();
		song.mMusicType = MusicType.LOCALMUSIC.ordinal();
		song.mUrl = downloaditem.getFilePath();
		song.mSize = downloaditem.getFileSize();
		return song;
	}

	public static String makeLyricString(Context context, String s)
	{
		TextView textview = new TextView(context);
		textview.setText(s);
		TextPaint textpaint = textview.getPaint();
		if (textpaint
				.breakText(textview.getText().toString(), true, 240F, null) < s
				.length())
			s = (new StringBuilder(
					String.valueOf(s.substring(0, textpaint.breakText(textview
							.getText().toString(), true, 240F, null)))))
					.append("...").toString();
		return s;
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

	public static boolean isMediaScannerScanning(Context context)
	{
		Cursor cursor = query(context, MediaStore.getMediaScannerUri(),
				new String[] { "volume" }, null, null, null);
		boolean flag = false;
		if (cursor != null)
		{
			int i = cursor.getCount();
			flag = false;
			if (i == 1)
			{
				cursor.moveToFirst();
				flag = "external".equals(cursor.getString(0));
			}
			cursor.close();
		}
		return flag;
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

	public static String getPerentString(double d, double d1)
	{
		double d2 = d / d1;
		NumberFormat numberformat = NumberFormat.getPercentInstance();
		numberformat.setMinimumFractionDigits(1);
		return numberformat.format(d2);
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

	public static String parseUrlTagValue(String s, String s1)
	{
		String s2;
		if (s.contains(s1))
		{
			String s3 = s.substring(s.indexOf(s1));
			if (s3.contains("&"))
				s2 = s3.substring(1 + s1.length(), s3.indexOf("&"));
			else
				s2 = s3.substring(1 + s1.length());
		} else
		{
			s2 = null;
		}
		return s2;
	}

	public static int indexOf(byte abyte0[], byte byte0, int i, int j)
	{
		int k;
		int i1 = -1;
		k = Math.min(i + j, abyte0.length);
		for (int l = i; l < k; l++)
		{
			if (abyte0[l] == byte0)
				i1 = l - i;
		}
		return i1;
	}

	/**
	 * 判断是否是中国移动的手机号码
	 * 
	 * @param s
	 *            手机号码
	 * @return
	 */
	public static boolean isChinaMobileMobileNumber(String s)
	{
		boolean flag = Pattern
				.compile(
						"^13[4-9]{1}[0-9]{8}|^147[0-9]{8}|^15[012789]{1}[0-9]{8}|^18[2378]{1}[0-9]{8}|^(\\+86|86)13[4-9]{1}[0-9]{8}|^(\\+86|86)147[0-9]{8}|^(\\+86|86)15[012789]{1}[0-9]{8}|^(\\+86|86)18[2378]{1}[0-9]{8}")
				.matcher(s).matches();
		boolean flag1 = false;
		if (flag)
			flag1 = true;
		return flag1;
	}

	/**
	 * 判断是否是中国移动的SIM卡
	 * 
	 * @return
	 */
	public static boolean isChinaMobileSIMCard()
	{
		boolean flag = false;
		String s = ((TelephonyManager) MobileMusicApplication.getInstance()
				.getSystemService("phone")).getSubscriberId();
		if (s != null)
		{
			if (!s.startsWith("46000") && !s.startsWith("46002"))
			{
				boolean flag1 = s.startsWith("46007");
				if (flag1)
					flag = true;
			}
		}
		return flag;
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

	public static int px2dip(Context context, float f)
	{
		return (int) (0.5F + f
				/ context.getResources().getDisplayMetrics().density);
	}

	public static Cursor query(Context context, Uri uri, String as[], String s,
			String as1[], String s1)
	{
		Cursor cursor = null;
		Cursor cursor1;
		ContentResolver contentresolver = context.getContentResolver();
		if (contentresolver == null)
		{
			cursor = null;
		} else
		{
			cursor1 = contentresolver.query(uri, as, s, as1, s1);
			cursor = cursor1;
		}
		return cursor;
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

	/**
	 * 退出应用
	 * 
	 * @param useForClear
	 */
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

	public static void reSetHttpClient()
	{
		mHttpClient = null;
		mHttpsClient = null;
	}

	/**
	 * 关闭Http的连接
	 */
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

	/**
	 * 判断本地音乐是否是杜比音乐，调用C++代码
	 * 
	 * @param song
	 * @return
	 */
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

	/**
	 * 正则表达式，判断是否是正确的网络地址
	 * 
	 * @param s
	 * @return
	 */
	public static Matcher getUrlMatcher(String s)
	{
		return Pattern.compile("http://[a-zA-Z0-9./\\s]+").matcher(s);
	}

	/**
	 * 创建HTTP的网络连接
	 * 
	 * @param flag
	 * @return
	 */
	public static DefaultHttpClient createNetworkClient(boolean flag)
	{
		DefaultHttpClient defaulthttpclient;
		if (NetUtil.netState == 8)
			defaulthttpclient = null;
		else if (!NetUtil.isNetStateWap() && !flag)
		{
			logger.v("createNetworkClient() ---> !WlanUtils.isNetStateWap() && !isHttpReqIfWlan");
			if (NetUtil.netState != 1 && NetUtil.netState != 6)
			{
				logger.d("Wlan has been closed.");
				defaulthttpclient = null;
				return defaulthttpclient;
			} else
			{
				if (mHttpsClient == null)
					mHttpsClient = createHttpsClient();
				defaulthttpclient = mHttpsClient;
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
				defaulthttpclient = null;
				return defaulthttpclient;
			} else
			{
				if (mHttpClient == null)
					mHttpClient = createHttpClient();
				defaulthttpclient = mHttpClient;
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
			}
		}
		logger.v("createNetworkClient() ---> Exit");
		return defaulthttpclient;
	}

	public static Bitmap createBitmap(byte abyte0[])
	{
		return BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length);
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

		private SSLContext sslContext = SSLContext.getInstance("TLS");

		public CustomSSLSocketFactory(KeyStore keystore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException
		{
			super(keystore);
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

	/**
	 * 从网络地址中得到位图图片数据
	 * 
	 * @param s
	 *            网络地址
	 * @return
	 */
	public static Bitmap getImageBitmap(String s)
	{
		Bitmap bitmap = null;
		Bitmap bitmap1 = null;
		DefaultHttpClient defaulthttpclient;
		defaulthttpclient = createNetworkClient(true);
		bitmap = null;
		if (defaulthttpclient != null)
		{
			HttpResponse httpresponse;
			int i;
			try
			{
				httpresponse = defaulthttpclient.execute(new HttpGet(s));
				i = httpresponse.getStatusLine().getStatusCode();
				logger.e((new StringBuilder("HTTP retCode: ")).append(i)
						.toString());
				bitmap = null;
				bitmap1 = null;
				if (i == 200)
				{
					InputStream inputstream = httpresponse.getEntity()
							.getContent();
					BufferedInputStream bufferedinputstream = new BufferedInputStream(
							inputstream);
					bitmap = BitmapFactory.decodeStream(bufferedinputstream);
					bufferedinputstream.close();
					inputstream.close();
					bitmap1 = bitmap;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return bitmap1;
	}

	public static int getInt(String s)
	{
		return getInt(10, s);
	}

	public static int getInt(String s, int i)
	{
		return getInt(10, s, i);
	}

	public static int getInt(int i, String s, int j)
	{
		if (s != null)
		{
			long l = Long.parseLong(s.trim(), i);
			j = (int) l;
		}
		return j;
	}

	public static int getInt(int i, String s)
	{
		return getInt(i, s, 0);
	}

	public static String getLyricFromLocalFile(String s) throws Exception
	{
		boolean flag;
		String s1 = null;
		logger.v("getLyricFromLocalFile() ---> Enter ");
		// flag = s.startsWith(Environment.getExternalStorageDirectory()
		// .getAbsolutePath());
		// if (flag)
		// {
		// int i = s.lastIndexOf(".");
		// File file = new File((new StringBuilder(String.valueOf(s.substring(
		// 0, i + 1)))).append("lrc").toString());
		// if (!file.exists())
		// file = new File((new StringBuilder(String.valueOf(s.substring(
		// 0, i + 1)))).append("txt").toString());
		// logger.v((new StringBuilder("lrc file: ")).append(file.getPath())
		// .toString());
		// if (file.exists())
		// {
		// FileInputStream fileinputstream = new FileInputStream(file);
		// ByteArrayOutputStream bytearrayoutputstream = new
		// ByteArrayOutputStream();
		// byte abyte0[] = new byte[1024];
		// do
		// {
		// int j = fileinputstream.read(abyte0);
		// if (j == -1)
		// {
		// byte abyte1[] = bytearrayoutputstream.toByteArray();
		// int k = (new BytesEncodingDetector())
		// .detectEncoding(abyte1);
		// fileinputstream.close();
		// s1 = new String(abyte1,
		// BytesEncodingDetector.javaname[k]);
		// return s1;
		// }
		// bytearrayoutputstream.write(abyte0, 0, j);
		// } while (true);
		// }
		// }
		// logger.v("getLyricFromLocalFile() ---> Exit");
		return s1;
	}

	public static String getLyricFromNetwork(String s, String s1)
			throws Exception
	{

		return null;
		// logger.e((new StringBuilder("getLyricFromNetwork() ---> Enter "))
		// .append(s).toString());
		// String s4 = null;
		// if (s != null)
		// {
		// String s2 = s.substring(1 + s.lastIndexOf("/"), s.lastIndexOf("."));
		// Environment.getExternalStorageDirectory().getAbsolutePath();
		// String s3 = null;
		// String s8;
		// boolean flag3 = s.startsWith(Environment
		// .getExternalStorageDirectory().getAbsolutePath());
		// if (flag3)
		// {
		// s8 = (new StringBuilder(String.valueOf(s.substring(0,
		// 1 + s.lastIndexOf("."))))).append("lrc").toString();
		// s3 = s8;
		// }
		// if (s4 != null)
		// {
		// logger.v("nw---------------3");
		// if (s.startsWith(Environment.getExternalStorageDirectory()
		// .getAbsolutePath()))
		// {
		// long l = getFreeSpace(s);
		// BufferedInputStream bufferedinputstream = null;
		// ByteArrayOutputStream bytearrayoutputstream = new
		// ByteArrayOutputStream();
		// byte abyte1[] = new byte[512];
		// int k = 0;
		// int i1;
		// byte abyte2[] = bytearrayoutputstream.toByteArray();
		// MyLogger mylogger = logger;
		// StringBuilder stringbuilder = new StringBuilder(
		// "response: ");
		// String s7 = new String(abyte2);
		// XMLParser xmlparser = new XMLParser(abyte2);
		// mylogger.i(stringbuilder.append(s7).toString());
		// boolean flag1 = xmlparser.getValueByTag("code").equals(
		// "000000");
		// if (flag1)
		// s4 = verify(xmlparser.getValueByTag("lrc"));
		// if (l - 0x200000L < (long) s4.length())
		// {
		// logger.e((new StringBuilder(
		// "getFile(), no space available, free space is: "))
		// .append(l).append(" Bytes").toString());
		// } else
		// {
		// File file = new File(s3);
		// logger.i((new StringBuilder("lrc file: ")).append(
		// file.getPath()).toString());
		// if (!file.exists())
		// file.createNewFile();
		// RandomAccessFile randomaccessfile = new RandomAccessFile(
		// file, "rw");
		// randomaccessfile.seek(0L);
		// byte abyte0[] = s4.getBytes();
		// randomaccessfile.write(abyte0, 0, abyte0.length);
		// randomaccessfile.close();
		// }
		// DefaultHttpClient defaulthttpclient1 = createNetworkClient(false);
		// if (defaulthttpclient1 == null)
		// {
		// s4 = null;
		// s3 = null;
		// }
		// boolean flag;
		// flag = NetUtil.isNetStateWap();
		// String s5;
		// String s6;
		// if (!flag)
		// {
		// s5 = MusicBusinessDefine_Net.NET_HOST_NAME;
		// } else
		// {
		// s5 = MusicBusinessDefine_WAP.CMWAP_HOST_NAME;
		// }
		// if (s1 == null)
		// {
		// s6 = (new StringBuilder(String.valueOf(s5)))
		// .append("rdp2/v5.3/lrcinfo.do?")
		// .append("songname=")
		// .append(EncodeBase64.encode(s2))
		// .append("&version=")
		// .append(GlobalSettingParameter.LOCAL_PARAM_VERSION)
		// .toString();
		// } else
		// {
		// boolean flag2;
		// flag2 = s1.equals("<unknown>");
		// if (!flag2)
		// {
		// s6 = (new StringBuilder(String.valueOf(s5)))
		// .append("lrcinfo.do?")
		// .append("songname=")
		// .append(EncodeBase64.encode(s2))
		// .append("&contentid=")
		// .append(s1)
		// .append("&ua=")
		// .append("MobileMusic_DefaultUA")
		// .append("&version=")
		// .append(GlobalSettingParameter.LOCAL_PARAM_VERSION)
		// .toString();
		// } else
		// {
		// s6 = (new StringBuilder(String.valueOf(s5)))
		// .append("rdp2/v5.3/lrcinfo.do?")
		// .append("songname=")
		// .append(EncodeBase64.encode(s2))
		// .append("&version=")
		// .append(GlobalSettingParameter.LOCAL_PARAM_VERSION)
		// .toString();
		// }
		// }
		// HttpResponse httpresponse1;
		// int j;
		// logger.i((new StringBuilder("search url: ")).append(s6)
		// .toString());
		// HttpGet httpget1 = new HttpGet(s6);
		// httpresponse1 = defaulthttpclient1.execute(httpget1);
		//
		// j = httpresponse1.getStatusLine().getStatusCode();
		// logger.e((new StringBuilder("HTTP retCode: ")).append(j)
		// .toString());
		// if (j == 200)
		// {
		// logger.v("nw---------------2");
		// bufferedinputstream = new BufferedInputStream(
		// httpresponse1.getEntity().getContent());
		// bytearrayoutputstream = new ByteArrayOutputStream();
		// abyte1 = new byte[512];
		// k = 0;
		// } else
		// {
		// s4 = null;
		// logger.v("nw---------------1");
		// }
		// while ((i1 = bufferedinputstream.read(abyte1)) != -1)
		// {
		// bytearrayoutputstream.write(abyte1, 0, i1);
		// k += i1;
		// }
		// } else if (s.startsWith("http://"))
		// try
		// {
		// DefaultHttpClient defaulthttpclient = createNetworkClient(true);
		// if (defaulthttpclient == null)
		// {
		// s4 = null;
		// } else
		// {
		// HttpGet httpget = new HttpGet(s);
		// httpget.setHeader("RANGE", "bytes=0-3072");
		// HttpResponse httpresponse = defaulthttpclient
		// .execute(httpget);
		// int i = httpresponse.getStatusLine()
		// .getStatusCode();
		// logger.e((new StringBuilder("HTTP retCode: "))
		// .append(i).toString());
		// if (i != 200)
		// {
		// s4 = null;
		// } else
		// {
		// InputStream inputstream = httpresponse
		// .getEntity().getContent();
		// DataInputStream datainputstream = new DataInputStream(
		// inputstream);
		// s4 = PlayerID3V2Parser.getInstance()
		// .parseLyric(datainputstream);
		// inputstream.close();
		// datainputstream.close();
		// }
		// }
		// } catch (IOException ioexception1)
		// {
		// logger.e(ioexception1.toString());
		// s4 = null;
		// }
		// }
		// }
		//
		// logger.v("nw---------------2");
		// return s4;
	}

	/**
	 * 获得SD卡指定文件对象的剩余容量
	 * 
	 * @param s
	 * @return
	 */
	public static final long getFreeSpace(String s)
	{
		long l = 0L;
		if (s != null)
		{
			long l1;
			int i;
			StatFs statfs = new StatFs(s);
			l1 = statfs.getBlockSize(); // 获得存储块大小
			i = statfs.getAvailableBlocks(); // 获得可用存储块数量
			l = l1 * (long) i - 0x200000L;
		}
		return l;
	}

	/**
	 * 获取铃声存储的路径，如果容量不足，返回null;
	 * 
	 * @param l
	 * @return
	 */
	public static final String getRingtoneStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_RINGTONE_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_RINGTONE_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	/**
	 * 获取MV存储的路径，如果容量不足，返回null;
	 * 
	 * @param l
	 * @return
	 */
	public static final String getMVStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_MV_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_MV_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	/**
	 * 获取升级存储的路径，如果容量不足，返回null;
	 * 
	 * @param l
	 * @return
	 */
	public static final String getUpdateStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_UPDATE_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_UPDATE_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	/**
	 * 字节数组转换为UTF-8的字符串
	 * 
	 * @param abyte0
	 *            待转换的字节数组
	 * @param i
	 * @param j
	 * @return
	 */
	public static String getUTF8String(byte abyte0[])
	{
		String s;
		if (abyte0 == null)
			s = "";
		else
			s = getUTF8String(abyte0, 0, abyte0.length);
		return s;
	}

	/**
	 * 字节数组的某一范围数据转换为UTF-8的字符串
	 * 
	 * @param abyte0
	 *            待转换的字节数组
	 * @param i
	 * @param j
	 * @return
	 */
	public static String getUTF8String(byte abyte0[], int i, int j)
	{
		String s;
		if (abyte0 == null)
			s = "";
		else
		{
			try
			{
				s = new String(abyte0, i, j, "UTF-8");
			} catch (UnsupportedEncodingException unsupportedencodingexception)
			{
				s = "";
			}
		}
		return s;
	}

	/**
	 * 获取皮肤存储的路径，如果容量不足，返回null;
	 * 
	 * @param l
	 * @return
	 */
	public static final String getSkinStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_SKIN_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_SKIN_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	/**
	 * 获取杜比音乐存储的路径，如果容量不足，返回null;
	 * 
	 * @param l
	 * @return
	 */
	public static final String getDoblySongStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_DOBLY_MUSIC_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_DOBLY_MUSIC_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	/**
	 * 获取音乐存储的路径，如果容量不足，返回null;
	 * 
	 * @param l
	 * @return
	 */
	public static final String getSongStoreDir(long l)
	{
		String s;
		if (getFreeSpace(GlobalSettingParameter.LOCAL_PARAM_MUSIC_STORE_SD_DIR) > l)
			s = GlobalSettingParameter.LOCAL_PARAM_MUSIC_STORE_SD_DIR;
		else
			s = null;
		return s;
	}

	private static Bitmap getArtworkFromFile(Context context, long l, long l1)
	{
		Bitmap bitmap;
		bitmap = null;
		if (l1 < 0L && l < 0L)
			throw new IllegalArgumentException(
					"Must specify an album or a song id");
		if (l1 >= 0L)
		{
			Uri uri1 = Uri.parse((new StringBuilder(
					"content://media/external/audio/media/")).append(l)
					.append("/albumart").toString());
			try
			{
				ParcelFileDescriptor parcelfiledescriptor1 = context
						.getContentResolver().openFileDescriptor(uri1, "r");
				bitmap = null;
				if (parcelfiledescriptor1 != null)
				{
					bitmap = BitmapFactory
							.decodeFileDescriptor(parcelfiledescriptor1
									.getFileDescriptor());
					parcelfiledescriptor1.close();
				}
				Uri uri = ContentUris.withAppendedId(sArtworkUri, l1);
				ParcelFileDescriptor parcelfiledescriptor = context
						.getContentResolver().openFileDescriptor(uri, "r");
				bitmap = null;
				if (parcelfiledescriptor != null)
				{
					bitmap = BitmapFactory
							.decodeFileDescriptor(parcelfiledescriptor
									.getFileDescriptor());
					parcelfiledescriptor.close();
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	private static Bitmap getDefaultArtwork(Context context)
	{
		return null;
	}

	/**
	 * DIP ---> PX 单位转换
	 * 
	 * @param context
	 * @param f
	 *            待转换的数据
	 * @return
	 */
	public static int dip2px(Context context, float f)
	{
		return (int) (0.5F + f
				* context.getResources().getDisplayMetrics().density);
	}

	/**
	 * 检查是否手机已插入外部存储，SD卡
	 * 
	 * @return
	 */
	public static boolean checkExternalStorage()
	{
		logger.v("checkExternalStorage() ---> Enter");
		String s = Environment.getExternalStorageState();
		boolean flag;
		if (s.equals("removed") || s.equals("unmounted"))
			flag = false;
		else
			flag = true;
		return flag;
	}

	/**
	 * 输入流到字节的转换
	 * 
	 * @param inputstream
	 * @return
	 * @throws IOException
	 */
	public static byte[] InputStreamToByte(InputStream inputstream)
			throws IOException
	{
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		do
		{
			int i = inputstream.read();
			if (i == -1)
			{
				byte abyte0[] = bytearrayoutputstream.toByteArray();
				bytearrayoutputstream.close();
				return abyte0;
			}
			bytearrayoutputstream.write(i);
		} while (true);
	}
}
