package org.ming.center.player;

import java.io.File;
import java.io.FileNotFoundException;
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

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class CacheSongDataSource extends Thread
{
	static final String FILE_PATH = "file";
	static final String FILE_PERCENT = "percent";
	static final String FILE_SIZE = "size";
	public static final String TAG = "CacheSongDataSource";
	public static final String TAG_RANGE = "RANGE";
	private static ArrayList<CacheSongDataSource> handlers = new ArrayList();
	private static ReentrantLock handlersLock;
	private static final MyLogger logger = MyLogger
			.getLogger("CacheSongDataSource");
	static long tempbytes = 0L;
	boolean ISONLINE_DOLBY = false;
	private MobileMusicApplication app;
	private Object lock = new Object();
	private Dispatcher mDispatcher;
	private File mFile;
	private AtomicReference<HttpGet> methodHttp = new AtomicReference();
	private HandlerObservable observed = new HandlerObservable();
	private volatile float percent;
	private Song song;
	private volatile State state;
	protected volatile boolean stopped = false;

	static
	{
		handlersLock = new ReentrantLock();
	}

	private CacheSongDataSource(Song paramSong,
			MobileMusicApplication paramMobileMusicApplication)
	{
		this.song = paramSong;
		this.state = State.CONNECT;
		this.app = paramMobileMusicApplication;
		this.mDispatcher = paramMobileMusicApplication.getEventDispatcher();
	}

	public static void close()
	{
		startHandle(null, null, null);
	}

	private void doTask()
	{
		downCacheMusic(this.song);
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

	private int downCacheMusic(Song song1)
	{
		return 0;
		// String s;
		// byte byte0;
		// int i;
		// long l;
		// long l1;
		// RandomAccessFile randomaccessfile;
		// byte byte1;
		// Log.d("CacheSongDataSource", "Connecting...");
		// android.os.Message message;
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
		// observed.change(0L, 0L);
		// if(song1.isDolby)
		// s = song1.mUrl3;
		// else
		// if(NetUtil.isNetStateWLAN() && song1.mUrl2 != null &&
		// !song1.mUrl2.equals("<unknown>") && !song1.mUrl2.equals(""))
		// s = song1.mUrl2;
		// else
		// s = song1.mUrl;
		// Log.v("cache", (new
		// StringBuilder("ceche is bolby:")).append(song1.isDolby).toString());
		// byte0 = -1;
		// i = 0;
		// l = 0L;
		// l1 = 0L;
		// mFile = null;
		// message = mDispatcher.obtainMessage(1024, song1);
		// mDispatcher.sendMessage(message);
		// randomaccessfile = null;
		// _L6:
		// org.apache.http.impl.client.DefaultHttpClient defaulthttpclient;
		// if(i >= 3 || byte0 != -1)
		// {
		// randomaccessfile;
		// byte1 = byte0;
		// } else
		// {
		// i++;
		// HttpGet httpget = new HttpGet(s);
		// if(!NetUtil.isNetStateWap())
		// httpget.addHeader("cookie",
		// ConfigSettingParameter.LOCAL_PARAM_COOKIE_VALUE);
		// methodHttp.set(httpget);
		// defaulthttpclient = Util.createNetworkClient(true);
		// if(defaulthttpclient == null)
		// {
		// app.getEventDispatcher().sendMessage(app.getEventDispatcher().obtainMessage(1014));
		// state = State.ERROR;
		// byte1 = -1;
		// randomaccessfile;
		// } else
		// {
		// label0:
		// {
		// if(!stopped)
		// break label0;
		// randomaccessfile;
		// byte1 = 0;
		// }
		// }
		// }
		// _L1:
		// return byte1;
		// InputStream inputstream = null;
		// HttpResponse httpresponse;
		// boolean flag;
		// Thread.sleep(300L);
		// httpresponse =
		// defaulthttpclient.execute((HttpUriRequest)methodHttp.get());
		// flag = stopped;
		// Exception exception;
		// RandomAccessFile randomaccessfile1;
		// Exception exception1;
		// label1:
		// {
		// if(!flag)
		// break label1;
		// StatusLine statusline;
		// int j;
		// HttpEntity httpentity;
		// byte abyte0[];
		// int k;
		// if(false)
		// try
		// {
		// null.close();
		// }
		// catch(IOException ioexception9) { }
		// if(randomaccessfile != null)
		// try
		// {
		// randomaccessfile.close();
		// }
		// catch(IOException ioexception8) { }
		// methodHttp.set(null);
		// Log.d("CacheSongDataSource", "Exiting!");
		// observed.change(l1, l);
		// randomaccessfile;
		// byte1 = 0;
		// }
		// goto _L1
		// statusline = httpresponse.getStatusLine();
		// j = statusline.getStatusCode();
		// inputstream = null;
		// if(j != 200)
		// Log.d("CacheSongDataSource", (new
		// StringBuilder("GET not OK: ")).append(statusline).toString());
		// state = State.SAVE;
		// httpentity = httpresponse.getEntity();
		// inputstream = null;
		// if(httpentity == null)
		// break MISSING_BLOCK_LABEL_1027;
		// inputstream = httpentity.getContent();
		// if(mFile != null) goto _L3; else goto _L2
		// _L2:
		// mFile = initCacheFileForHandler(l1);
		// randomaccessfile1 = new RandomAccessFile(mFile, "rws");
		// _L11:
		// if(l1 != 0L)
		// break MISSING_BLOCK_LABEL_599;
		// l1 = httpentity.getContentLength();
		// CacheSongData.getInstance().setFileAllLength(l1);
		// abyte0 = new byte[2048];
		// _L7:
		// k = inputstream.read(abyte0);
		// if(k >= 0) goto _L5; else goto _L4
		// _L4:
		// state = State.SUCCESS;
		// byte0 = 0;
		// stopCacheDB();
		// _L12:
		// IOException ioexception;
		// IOException ioexception1;
		// IOException ioexception2;
		// IOException ioexception3;
		// IOException ioexception4;
		// IOException ioexception5;
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
		// Log.d("CacheSongDataSource", "Exiting!");
		// observed.change(l1, l);
		// randomaccessfile = randomaccessfile1;
		// goto _L6
		// _L5:
		// l += k;
		// randomaccessfile1.write(abyte0, 0, k);
		// Log.v("cache data length", String.valueOf(l));
		// CacheSongData.getInstance().setCacheDBLength(l);
		// observed.change(l1, l);
		// if(l < 0x32000L) goto _L7; else goto _L4
		// exception1;
		// randomaccessfile1 = randomaccessfile;
		// _L10:
		// exception1.printStackTrace();
		// Util.shutDownHttpClient();
		// if(stopped)
		// break MISSING_BLOCK_LABEL_849;
		// if(i == 3)
		// state = State.ERROR;
		// byte0 = -1;
		// if(inputstream != null)
		// try
		// {
		// inputstream.close();
		// }
		// // Misplaced declaration of an exception variable
		// catch(IOException ioexception5) { }
		// if(randomaccessfile1 != null)
		// try
		// {
		// randomaccessfile1.close();
		// }
		// // Misplaced declaration of an exception variable
		// catch(IOException ioexception4) { }
		// methodHttp.set(null);
		// Log.d("CacheSongDataSource", "Exiting!");
		// observed.change(l1, l);
		// randomaccessfile = randomaccessfile1;
		// goto _L6
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
		// Log.d("CacheSongDataSource", "Exiting!");
		// observed.change(l1, l);
		// byte1 = 0;
		// goto _L1
		// exception;
		// randomaccessfile1 = randomaccessfile;
		// _L9:
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
		// Log.d("CacheSongDataSource", "Exiting!");
		// observed.change(l1, l);
		// throw exception;
		// exception;
		// if(true) goto _L9; else goto _L8
		// _L8:
		// exception1;
		// goto _L10
		// _L3:
		// randomaccessfile1 = randomaccessfile;
		// goto _L11
		// randomaccessfile1 = randomaccessfile;
		// inputstream = null;
		// goto _L12
		// }
		//
		// private void fillFile(long l, File file)
		// throws Exception
		// {
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

	private File initCacheFileForHandler(long paramLong) throws Exception
	{
		return null;
		// File localFile1;
		// if (Util.isOnlineMusic(this.song))
		// {
		// String str1 = this.app.getApplicationInfo().dataDir;
		// StatFs localStatFs1 = new StatFs(str1);
		// localStatFs1.restat(str1);
		// String str2;
		// if (paramLong < localStatFs1.getAvailableBlocks()
		// * localStatFs1.getBlockSize())
		// str2 = str1 + "/cache";
		// while (str2 == null)
		// {
		// throw new FileNotFoundException(
		// "can not create cache file, no enough space ");
		// boolean bool1 = "mounted".equals(Environment
		// .getExternalStorageState());
		// str2 = null;
		// if (bool1)
		// {
		// String str3 = Environment.getExternalStorageDirectory()
		// .getAbsolutePath();
		// StatFs localStatFs2 = new StatFs(str3);
		// localStatFs2.restat(str3);
		// boolean bool2 = paramLong < localStatFs2
		// .getAvailableBlocks() * localStatFs2.getBlockSize();
		// str2 = null;
		// if (bool2)
		// str2 = str3 + "/12530/cache";
		// }
		// }
		// File localFile2 = new File(str2);
		// if (!localFile2.exists())
		// localFile2.mkdirs();
		// CacheSongData.getInstance().setCacheFilePath(
		// str2 + "/cachefile.mp4");
		// localFile1 = new File(str2 + "/cachefile.mp4");
		// localFile1.deleteOnExit();
		// if (localFile1.exists())
		// localFile1.delete();
		// localFile1.createNewFile();
		// if (!localFile1.exists())
		// throw new FileNotFoundException("can not find "
		// + localFile1.getAbsolutePath());
		// fillFile(paramLong, localFile1);
		// }
		// localFile1 = new File(this.song.mUrl);
		// return localFile1;

	}

	public static CacheSongDataSource startHandle(Song song1,
			CacheSongData cachesongdata,
			MobileMusicApplication mobilemusicapplication)
	{
		CacheSongDataSource cachesongdatasource = null;
		CacheSongDataSource cachesongdatasource1;
		handlersLock.lock();
		for (int i = 0; i < handlers.size(); i++)
		{
			handlers.clear();
			if (song1 != null)
			{
				cachesongdatasource1 = new CacheSongDataSource(song1,
						mobilemusicapplication);
				handlers.add(cachesongdatasource1);
				cachesongdatasource1.start();
				cachesongdatasource = cachesongdatasource1;
			}
			((CacheSongDataSource) handlers.get(i)).stopDownload();
		}
		handlersLock.unlock();
		return cachesongdatasource;
	}

	public static void stopCacheDB()
	{
		handlersLock.lock();
		int i = 0;
		try
		{
			while (true)
			{
				if (i >= handlers.size())
				{
					handlers.clear();
					return;
				}
				((CacheSongDataSource) handlers.get(i)).stopDownload();
				i++;
			}
		} finally
		{
			handlersLock.unlock();
		}
	}

	private void stopDownload()
	{
		if (this.stopped)
		{
			if (isAlive())
			{
				Log.d("CacheSongDataSource", "Stopping download...");
				this.stopped = true;
				this.state = State.CANCEL;
				this.observed.deleteObservers();
				interrupt();
				synchronized (this.lock)
				{
					HttpGet localHttpGet = (HttpGet) this.methodHttp.get();
					if (localHttpGet != null)
						localHttpGet.abort();
					Log.d("CacheSongDataSource", "Download stopped.");
				}
			}
		}
	}

	public String getAbsoluteFilePath()
	{
		String str = null;
		if (this.mFile != null)
			str = this.mFile.getAbsolutePath();
		return str;
	}

	public Observable getObervable()
	{
		return this.observed;
	}

	public float getPercent()
	{
		return this.percent;
	}

	public boolean isDownloadSuccess()
	{
		if (this.state == State.SUCCESS)
			;
		for (boolean bool = true;; bool = false)
			return bool;
	}

	public boolean isEnd()
	{
		if ((isAlive())
				&& (!isInterrupted())
				&& (!this.stopped)
				&& (this.percent < 1.0F)
				&& ((this.state == State.CONNECT) || (this.state == State.SAVE)))
			;
		for (boolean bool = false;; bool = true)
			return bool;
	}

	public boolean isError()
	{
		if (this.state == State.ERROR)
			;
		for (boolean bool = true;; bool = false)
			return bool;
	}

	public boolean isISONLINE_DOLBY()
	{
		return this.ISONLINE_DOLBY;
	}

	public void run()
	{
		doTask();
	}

	public void setISONLINE_DOLBY(boolean paramBoolean)
	{
		this.ISONLINE_DOLBY = paramBoolean;
	}

	private class HandlerObservable extends Observable
	{
		private HandlerObservable()
		{}

		private void change(long paramLong1, long paramLong2)
		{
			CacheSongData.getInstance().setCacheDBLength(paramLong2);
			Log.v("cache data length", String.valueOf(paramLong2));
		}
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
}