package org.ming.center.player;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.client.methods.HttpGet;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.Song;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;
import org.ming.util.Util;

import android.util.Log;

public class DataSourceHandler extends Thread
{
	// 观察者模式，数据变化时自动调用
	private class HandlerObservable extends Observable
	{

		private void change(long l, long l1)
		{
			bytesDownloaded = l1;
			DataSourceHandler datasourcehandler = DataSourceHandler.this;
			float f;
			if (l > 0L)
				f = (float) l1 / (float) l;
			else
				f = 0.0F;
			datasourcehandler.percent = f;
			setChanged();
			notifyObservers(DataSourceHandler.this);
			clearChanged();
		}
	}

	// 待解决
	public static DataSourceHandler startHandle(Song song1,
			MusicPlayerWrapper musicplayerwrapper,
			MobileMusicApplication mobilemusicapplication)
	{
		logger.v("startHandler ----> enter");
		handlersLock.lock();
		DataSourceHandler datasourcehandler = null;
		DataSourceHandler datasourcehandler1;
		logger.v("handlers.size() ---- > " + handlers.size());
		for (int i = 0; i < handlers.size(); i++)
		{
			logger.v("handlers  ---------- " + i);
			((DataSourceHandler) handlers.get(i)).stopDownload();
		}
		handlers.clear();
		if (song1 != null)
		{
			datasourcehandler1 = new DataSourceHandler(song1,
					mobilemusicapplication);
			if (musicplayerwrapper != null)
			{
				datasourcehandler1.registerObserver(musicplayerwrapper, 0);
				handlers.add(datasourcehandler1);
				datasourcehandler1.start(); // 新线程开始执行
				datasourcehandler = datasourcehandler1;
			}
			handlersLock.unlock();
			return datasourcehandler;
		}
		handlersLock.unlock();
		logger.v("startHandler ----> exit");
		return datasourcehandler;
	}

	public enum State
	{
		CONNECT("CONNECT", 0), SAVE("SAVE", 1), ERROR("ERROR", 2), SUCCESS(
				"SUCCESS", 3), CANCEL("CANCEL", 4);
		private String name;
		private int id;

		State(String name, int id)
		{
			this.name = name;
			this.id = id;
		}

	}

	private DataSourceHandler(Song song1,
			MobileMusicApplication mobilemusicapplication)
	{
		ISONLINE_DOLBY = false;
		stopped = false;
		lock = new Object();
		observed = new HandlerObservable();
		methodHttp = new AtomicReference();
		song = song1;
		state = State.CONNECT;
		app = mobilemusicapplication;
		mDispatcher = mobilemusicapplication.getEventDispatcher();
	}

	public static void close()
	{
		// startHandle(null, null, null);
	}

	private int doTask()
	{
		int i;
		if (!Util.isOnlineMusic(song))
		{
			mFile = new File(song.mUrl);
			boolean flag = stopped;
			i = 0;
			if (!flag)
			{
				observed.change(song.mSize, song.mSize);
				state = State.SUCCESS;
				i = 0;
			}
		} else
		{
			mFile = null;
			CacheSongData.getInstance().stopCacheDB();
			int j = fillCacheDb();
			i = downonlinemusic(song, j);
		}
		return i;
	}

	private int downonlinemusic(Song song1, int i)
	{
		return 0;
		// String s;
		// byte byte0;
		// int k;
		// long l;
		// RandomAccessFile randomaccessfile;
		// Log.d("DataSourceHandler", "Connecting...");
		// if(Util.isDolby(song1))
		// {
		// if(GlobalSettingParameter.useraccount != null &&
		// (GlobalSettingParameter.SERVER_INIT_PARAM_ONLINE_MUSIC_ORDER_STATUS.equals("1")
		// || GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER != null &&
		// GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER.equals(String.valueOf(3))
		// || GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER != null &&
		// GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER.equals(String.valueOf(2))))
		// {
		// song1.isDolby = true;
		// ISONLINE_DOLBY = true;
		// } else
		// {
		// song1.isDolby = false;
		// }
		// } else
		// {
		// song1.isDolby = false;
		// }
		// percent = 0.0F;
		// if(i == 0)
		// observed.change(0L, 0L);
		// if(song1.isDolby)
		// s = song1.mUrl3;
		// else
		// if(NetUtil.isNetStateWLAN() && song1.mUrl2 != null &&
		// !song1.mUrl2.equals("<unknown>") && !song1.mUrl2.equals(""))
		// s = song1.mUrl2;
		// else
		// s = song1.mUrl;
		// if(NetUtil.isNetStateWap() &&
		// GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER.equals(String.valueOf(3)))
		// {
		// int j2 = s.indexOf("/", 10);
		// if(j2 != -1)
		// {
		// String s5 = s.substring(j2 + 1);
		// s = (new
		// StringBuilder(String.valueOf(MusicBusinessDefine_Net.NET_MUSICHOST_NAME))).append(s5).toString();
		// }
		// } else
		// if(NetUtil.isNetStateWap())
		// {
		// int i2 = s.indexOf("/", 10);
		// if(i2 != -1)
		// {
		// String s4 = s.substring(i2 + 1);
		// s = (new
		// StringBuilder(String.valueOf(MusicBusinessDefine_WAP.WAP_MUSICHOST_NAME))).append(s4).toString();
		// }
		// } else
		// {
		// int j = s.indexOf("/", 10);
		// if(j != -1)
		// {
		// String s1 = s.substring(j + 1);
		// s = (new
		// StringBuilder(String.valueOf(MusicBusinessDefine_Net.NET_MUSICHOST_NAME))).append(s1).toString();
		// }
		// }
		// byte0 = -1;
		// k = 0;
		// l = 0L;
		// randomaccessfile = null;
		// _L9:
		// org.apache.http.impl.client.DefaultHttpClient defaulthttpclient;
		// if(k >= 3 || byte0 != -1)
		// {
		// randomaccessfile;
		// } else
		// {
		// k++;
		// HttpGet httpget = new HttpGet(s);
		// if(!NetUtil.isNetStateWap())
		// httpget.addHeader("cookie",
		// ConfigSettingParameter.LOCAL_PARAM_COOKIE_VALUE);
		// if(i > 0)
		// {
		// String s3 = (new
		// StringBuilder("bytes=")).append(i).append("-").toString();
		// logger.e((new
		// StringBuilder("dolbymobile3 --> instream skip :")).append(s3).toString());
		// httpget.setHeader("RANGE", s3);
		// }
		// methodHttp.set(httpget);
		// defaulthttpclient = Util.createNetworkClient(true);
		// if(defaulthttpclient == null)
		// {
		// app.getEventDispatcher().sendMessage(app.getEventDispatcher().obtainMessage(1014));
		// state = State.ERROR;
		// Log.v("cache downlinemusic end", "Lin236");
		// byte0 = -1;
		// randomaccessfile;
		// } else
		// {
		// label0:
		// {
		// if(!stopped)
		// break label0;
		// Log.v("cache downlinemusic end", "Lin245");
		// randomaccessfile;
		// byte0 = 0;
		// }
		// }
		// }
		// _L3:
		// return byte0;
		// InputStream inputstream;
		// int i1;
		// inputstream = null;
		// i1 = 0;
		// HttpResponse httpresponse;
		// Thread.sleep(300L);
		// httpresponse =
		// defaulthttpclient.execute((HttpUriRequest)methodHttp.get());
		// inputstream = null;
		// if(httpresponse != null) goto _L2; else goto _L1
		// _L1:
		// Log.v("cache downlinemusic end", "Lin272");
		// _L6:
		// Exception exception;
		// RandomAccessFile randomaccessfile1;
		// Exception exception1;
		// boolean flag;
		// StatusLine statusline;
		// int j1;
		// HttpEntity httpentity;
		// int k1;
		// byte abyte0[];
		// int l1;
		// long l2;
		// String s2;
		// boolean flag1;
		// if(false)
		// try
		// {
		// null.close();
		// }
		// catch(IOException ioexception11) { }
		// if(randomaccessfile != null)
		// try
		// {
		// randomaccessfile.close();
		// }
		// catch(IOException ioexception10) { }
		// methodHttp.set(null);
		// Log.d("DataSourceHandler", "Exiting!");
		// observed.change(l + (long)i, i + 0);
		// randomaccessfile;
		// byte0 = 0;
		// goto _L3
		// _L2:
		// flag = stopped;
		// inputstream = null;
		// if(!flag) goto _L5; else goto _L4
		// _L4:
		// Log.v("cache downlinemusic end", "Lin276");
		// goto _L6
		// exception1;
		// randomaccessfile1 = randomaccessfile;
		// _L13:
		// exception1.printStackTrace();
		// Util.shutDownHttpClient();
		// if(stopped) goto _L8; else goto _L7
		// _L7:
		// if(k == 3)
		// state = State.ERROR;
		// byte0 = -1;
		// IOException ioexception2;
		// IOException ioexception3;
		// IOException ioexception8;
		// IOException ioexception9;
		// if(inputstream != null)
		// try
		// {
		// inputstream.close();
		// }
		// catch(IOException ioexception5) { }
		// if(randomaccessfile1 != null)
		// try
		// {
		// randomaccessfile1.close();
		// }
		// catch(IOException ioexception4) { }
		// methodHttp.set(null);
		// Log.d("DataSourceHandler", "Exiting!");
		// observed.change(l + (long)i, i + i1);
		// randomaccessfile = randomaccessfile1;
		// goto _L9
		// _L5:
		// statusline = httpresponse.getStatusLine();
		// j1 = statusline.getStatusCode();
		// inputstream = null;
		// if(j1 == 200)
		// break MISSING_BLOCK_LABEL_981;
		// Log.d("DataSourceHandler", (new
		// StringBuilder("GET not OK: ")).append(statusline).toString());
		// inputstream = null;
		// if(j1 != 404)
		// break MISSING_BLOCK_LABEL_981;
		// s2 = lastDownUrl;
		// inputstream = null;
		// if(s2 != null)
		// {
		// flag1 = lastDownUrl.equals(s);
		// inputstream = null;
		// if(flag1)
		// break MISSING_BLOCK_LABEL_981;
		// }
		// lastDownUrl = s;
		// if(false)
		// try
		// {
		// null.close();
		// }
		// // Misplaced declaration of an exception variable
		// catch(IOException ioexception9) { }
		// if(randomaccessfile != null)
		// try
		// {
		// randomaccessfile.close();
		// }
		// // Misplaced declaration of an exception variable
		// catch(IOException ioexception8) { }
		// methodHttp.set(null);
		// Log.d("DataSourceHandler", "Exiting!");
		// observed.change(l + (long)i, i + 0);
		// byte0 = -3;
		// randomaccessfile;
		// goto _L3
		// state = State.SAVE;
		// httpentity = httpresponse.getEntity();
		// if(httpentity == null)
		// break MISSING_BLOCK_LABEL_1419;
		// k1 = l != 0L;
		// inputstream = null;
		// if(k1 == 0)
		// l = httpentity.getContentLength();
		// inputstream = httpentity.getContent();
		// if(mFile == null)
		// mFile = initCacheFileForHandler(l);
		// randomaccessfile1 = new RandomAccessFile(mFile, "rws");
		// if(i <= 0)
		// break MISSING_BLOCK_LABEL_1082;
		// l2 = i;
		// randomaccessfile1.seek(l2);
		// abyte0 = new byte[2048];
		// _L12:
		// l1 = inputstream.read(abyte0);
		// if(l1 >= 0) goto _L11; else goto _L10
		// _L10:
		// (byte[])null;
		// state = State.SUCCESS;
		// byte0 = 0;
		// _L16:
		// IOException ioexception;
		// IOException ioexception1;
		// if(inputstream != null)
		// try
		// {
		// inputstream.close();
		// }
		// catch(IOException ioexception7) { }
		// if(randomaccessfile1 != null)
		// try
		// {
		// randomaccessfile1.close();
		// }
		// catch(IOException ioexception6) { }
		// methodHttp.set(null);
		// Log.d("DataSourceHandler", "Exiting!");
		// observed.change(l + (long)i, i + i1);
		// randomaccessfile = randomaccessfile1;
		// goto _L9
		// _L11:
		// i1 += l1;
		// randomaccessfile1.write(abyte0, 0, l1);
		// observed.change(l + (long)i, i + i1);
		// goto _L12
		// exception1;
		// goto _L13
		// _L8:
		// Log.v("cache downlinemusic end", "Lin327");
		// if(inputstream != null)
		// try
		// {
		// inputstream.close();
		// }
		// // Misplaced declaration of an exception variable
		// catch(IOException ioexception3) { }
		// if(randomaccessfile1 != null)
		// try
		// {
		// randomaccessfile1.close();
		// }
		// // Misplaced declaration of an exception variable
		// catch(IOException ioexception2) { }
		// methodHttp.set(null);
		// Log.d("DataSourceHandler", "Exiting!");
		// observed.change(l + (long)i, i + i1);
		// byte0 = 0;
		// goto _L3
		// exception;
		// randomaccessfile1 = randomaccessfile;
		// _L15:
		// if(inputstream != null)
		// try
		// {
		// inputstream.close();
		// }
		// // Misplaced declaration of an exception variable
		// catch(IOException ioexception1) { }
		// if(randomaccessfile1 != null)
		// try
		// {
		// randomaccessfile1.close();
		// }
		// // Misplaced declaration of an exception variable
		// catch(IOException ioexception) { }
		// methodHttp.set(null);
		// Log.d("DataSourceHandler", "Exiting!");
		// observed.change(l + (long)i, i + i1);
		// throw exception;
		// exception;
		// if(true) goto _L15; else goto _L14
		// _L14:
		// randomaccessfile1 = randomaccessfile;
		// i1 = 0;
		// inputstream = null;
		// goto _L16
	}

	private int fillCacheDb()
	{
		return 0;
		// int i;
		// int j;
		// byte abyte0[];
		// (byte[])null;
		// Song song1 = CacheSongData.getInstance().getCacheSong();
		// i = 0;
		// if(song1 == null)
		// break MISSING_BLOCK_LABEL_265;
		// boolean flag =
		// song.mContentId.equals(CacheSongData.getInstance().getCacheSong().mContentId);
		// i = 0;
		// if(!flag)
		// break MISSING_BLOCK_LABEL_265;
		// observed.change(0L, 0L);
		// Log.v("cache", "stop caching");
		// j = CacheSongData.getInstance().getCacheDBLength();
		// Log.v("cache", (new
		// StringBuilder("cache data length")).append(j).toString());
		// abyte0 = new byte[j];
		// RandomAccessFile randomaccessfile = new RandomAccessFile(new
		// File(CacheSongData.getInstance().getCacheFilePath()), "rws");
		// long l;
		// randomaccessfile.read(abyte0, 0, j);
		// if(j < (int)randomaccessfile.length())
		// break MISSING_BLOCK_LABEL_163;
		// l = randomaccessfile.length();
		// j = (int)l;
		// RandomAccessFile randomaccessfile1;
		// if(mFile != null || CacheSongData.getInstance().getCacheSong() ==
		// null ||
		// !song.mContentId.equals(CacheSongData.getInstance().getCacheSong().mContentId)
		// || abyte0 == null)
		// break MISSING_BLOCK_LABEL_262;
		// mFile =
		// initCacheFileForHandler(CacheSongData.getInstance().getFileAllLength());
		// randomaccessfile1 = new RandomAccessFile(mFile, "rws");
		// randomaccessfile1.write(abyte0, 0, j);
		// observed.change(CacheSongData.getInstance().getFileAllLength(), j);
		// i = j;
		// _L1:
		// return i;
		// IOException ioexception;
		// ioexception;
		// _L3:
		// ioexception.printStackTrace();
		// i = 0;
		// goto _L1
		// Exception exception;
		// exception;
		// _L2:
		// i = 0;
		// goto _L1
		// Exception exception1;
		// exception1;
		// goto _L2
		// ioexception;
		// goto _L3
	}

	private void fillFile(long l, File file) throws Exception
	{
		// if(l <= 0L) goto _L2; else goto _L1
		// _L1:
		// byte abyte0[];
		// RandomAccessFile randomaccessfile;
		// abyte0 = new byte[2048];
		// randomaccessfile = null;
		// RandomAccessFile randomaccessfile1 = new RandomAccessFile(file,
		// "rw");
		// long l1 = l;
		// _L6:
		// if(l1 > 0L) goto _L4; else goto _L3
		// _L3:
		// if(randomaccessfile1 == null) goto _L2; else goto _L5
		// _L5:
		// randomaccessfile1.close();
		// _L2:
		// return;
		// _L4:
		// long l2;
		// int i;
		// if(l1 > 2048L)
		// l2 = 2048L;
		// else
		// l2 = l1;
		// i = (int)l2;
		// randomaccessfile1.write(abyte0, 0, i);
		// l1 -= 2048L;
		// goto _L6
		// Exception exception;
		// exception;
		// _L8:
		// throw exception;
		// Exception exception1;
		// exception1;
		// _L7:
		// if(randomaccessfile != null)
		// try
		// {
		// randomaccessfile.close();
		// }
		// catch(IOException ioexception) { }
		// throw exception1;
		// IOException ioexception1;
		// ioexception1;
		// goto _L2
		// exception1;
		// randomaccessfile = randomaccessfile1;
		// goto _L7
		// exception;
		// randomaccessfile = randomaccessfile1;
		// goto _L8
	}

	private File initCacheFileForHandler(long l) throws Exception
	{
		return null;
		// if(!Util.isOnlineMusic(song)) goto _L2; else goto _L1
		// _L1:
		// String s;
		// StatFs statfs;
		// s = app.getApplicationInfo().dataDir;
		// statfs = new StatFs(s);
		// statfs.restat(s);
		// if(l >= (long)statfs.getAvailableBlocks() *
		// (long)statfs.getBlockSize()) goto _L4; else goto _L3
		// _L3:
		// String s1 = (new
		// StringBuilder(String.valueOf(s))).append("/tmp").toString();
		// _L6:
		// if(s1 == null)
		// throw new
		// FileNotFoundException("can not create cache file, no enough space ");
		// break; /* Loop/switch isn't completed */
		// _L4:
		// boolean flag =
		// "mounted".equals(Environment.getExternalStorageState());
		// s1 = null;
		// if(flag)
		// {
		// String s2 =
		// Environment.getExternalStorageDirectory().getAbsolutePath();
		// StatFs statfs1 = new StatFs(s2);
		// statfs1.restat(s2);
		// int i = l != (long)statfs1.getAvailableBlocks() *
		// (long)statfs1.getBlockSize();
		// s1 = null;
		// if(i < 0)
		// s1 = (new
		// StringBuilder(String.valueOf(s2))).append("/12530").toString();
		// }
		// if(true) goto _L6; else goto _L5
		// _L5:
		// File file;
		// File file1 = new File(s1);
		// if(!file1.exists())
		// file1.mkdirs();
		// file = new File((new
		// StringBuilder(String.valueOf(s1))).append("/onlinefile.mp4").toString());
		// file.deleteOnExit();
		// if(file.exists())
		// file.delete();
		// file.createNewFile();
		// if(!file.exists())
		// throw new FileNotFoundException((new
		// StringBuilder("can not find ")).append(file.getAbsolutePath()).toString());
		// fillFile(l, file);
		// _L8:
		// return file;
		// _L2:
		// file = new File(song.mUrl);
		// if(true) goto _L8; else goto _L7
		// _L7:
		// }
		//
		// public static DataSourceHandler startHandle(Song song1,
		// MusicPlayerWrapper musicplayerwrapper, MobileMusicApplication
		// mobilemusicapplication)
		// {
		// int i;
		// handlersLock.lock();
		// i = 0;
		// _L1:
		// DataSourceHandler datasourcehandler;
		// DataSourceHandler datasourcehandler1;
		// if(i < handlers.size())
		// break MISSING_BLOCK_LABEL_80;
		// handlers.clear();
		// datasourcehandler = null;
		// if(song1 == null)
		// break MISSING_BLOCK_LABEL_71;
		// datasourcehandler1 = new DataSourceHandler(song1,
		// mobilemusicapplication);
		// if(musicplayerwrapper == null)
		// break MISSING_BLOCK_LABEL_53;
		// datasourcehandler1.registerObserver(musicplayerwrapper, 0);
		// handlers.add(datasourcehandler1);
		// datasourcehandler1.start();
		// datasourcehandler = datasourcehandler1;
		// handlersLock.unlock();
		// return datasourcehandler;
		// ((DataSourceHandler)handlers.get(i)).stopDownload();
		// i++;
		// goto _L1
		// Exception exception;
		// exception;
		// _L3:
		// handlersLock.unlock();
		// throw exception;
		// exception;
		// if(true) goto _L3; else goto _L2
		// _L2:
	}

	private void stopDownload()
	{
		if (!stopped && isAlive())
		{
			Log.d("DataSourceHandler", "Stopping download...");
			stopped = true;
			state = State.CANCEL;
			observed.deleteObservers();
			interrupt();
			synchronized (lock)
			{
				HttpGet httpget = (HttpGet) methodHttp.get();
				if (httpget != null)
					httpget.abort();
			}
			Log.d("DataSourceHandler", "Download stopped.");
		}
	}

	public String getAbsoluteFilePath()
	{
		String s;
		if (mFile != null)
			s = mFile.getAbsolutePath();
		else
			s = null;
		return s;
	}

	public long getBytes()
	{
		return bytesDownloaded;
	}

	public Observable getObervable()
	{
		return observed;
	}

	public float getPercent()
	{
		return percent;
	}

	public boolean isDownloadSuccess()
	{
		boolean flag;
		if (state == State.SUCCESS)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public boolean isEnd()
	{
		boolean flag1;
		StringBuilder stringbuilder = new StringBuilder("cache isend:");
		boolean flag;
		State state1;
		State state2;
		State state3;
		State state4;
		if (isAlive())
			flag = false;
		else
			flag = true;
		Log.v("cache isend", stringbuilder.append(flag).toString());

		if (!isAlive() || isInterrupted() || stopped || percent >= 1.0F)
		{
			flag1 = true;
		} else
		{
			state1 = state;
			state2 = State.CONNECT;
			flag1 = false;
			if (state1 == state2)
			{
				return flag1;
			} else
			{
				state3 = state;
				state4 = State.SAVE;
				flag1 = false;
				if (state3 != state4)
				{
					flag1 = true;
				} else
				{
					return flag1;
				}
			}
		}
		return flag1;
	}

	public boolean isError()
	{
		boolean flag;
		if (state == State.ERROR)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public boolean isISONLINE_DOLBY()
	{
		return ISONLINE_DOLBY;
	}

	public void registerObserver(MusicPlayerWrapper musicplayerwrapper, int i)
	{
		if (musicplayerwrapper != null)
			musicplayerwrapper.addPlayObserver(this, i);
	}

	public void run()
	{
		int i;
		i = doTask();
		if (i == -1)
			logger.w("dolbymobile3 --> Try downloading duby file faile");
		if (i != -1)
		{
			if (i == -3)
			{
				mDispatcher.sendMessage(mDispatcher.obtainMessage(1006, 0, 0,
						null));
				Log.d("DataSourceHandler", "Not find the music file...");
			}
		} else
		{
			mDispatcher.sendMessage(mDispatcher.obtainMessage(1005, 1, -100,
					null));
			logger.e("dolbymobile3 --> Download duby file complete faile");
		}

	}

	public void setISONLINE_DOLBY(boolean flag)
	{
		ISONLINE_DOLBY = flag;
	}

	private static ArrayList handlers = new ArrayList();
	private static ReentrantLock handlersLock = new ReentrantLock(); // ??????????????????????????????
	private static String lastDownUrl = "<unknown>";
	private static final MyLogger logger = MyLogger
			.getLogger("DataSourceHandler");
	boolean ISONLINE_DOLBY;
	public final String TAG = "DataSourceHandler";
	private final String TAG_RANGE = "RANGE";
	private MobileMusicApplication app;
	private volatile long bytesDownloaded;
	private Object lock;
	private Dispatcher mDispatcher;
	private File mFile;
	private AtomicReference methodHttp;
	private HandlerObservable observed;
	private volatile float percent;
	private Song song;
	private volatile State state;
	protected volatile boolean stopped;

}
