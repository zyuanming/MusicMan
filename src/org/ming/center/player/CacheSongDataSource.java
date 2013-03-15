package org.ming.center.player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.ming.center.ConfigSettingParameter;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.Song;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
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
		if (l <= 0L)
		{
			return;
		} else
		{
			byte abyte0[];
			abyte0 = new byte[2048];
			RandomAccessFile randomaccessfile = new RandomAccessFile(file, "rw");
			long l1 = l;
			while (l1 > 0L)
			{
				long l2;
				int i;
				if (l1 > 2048L)
					l2 = 2048L;
				else
					l2 = l1;
				i = (int) l2;
				randomaccessfile.write(abyte0, 0, i);
				l1 -= 2048L;
			}
			if (randomaccessfile != null)
			{
				randomaccessfile.close();
			}
		}
	}

	/**
	 * 还没有完成的缓存音乐的方法
	 * 
	 * @param song1
	 * @return
	 */
	private int downCacheMusic(Song song1)
	{
		return 0;
	}

	private File initCacheFileForHandler(long l) throws Exception
	{
		File file;
		if (!Util.isOnlineMusic(song))
		{
			file = new File(song.mUrl);
		} else
		{
			String s = app.getApplicationInfo().dataDir;
			StatFs statfs = new StatFs(s);
			statfs.restat(s);
			String s1 = null;
			if (l >= (long) statfs.getAvailableBlocks()
					* (long) statfs.getBlockSize())
			{
				boolean flag = "mounted".equals(Environment
						.getExternalStorageState());
				if (flag)
				{
					String s2 = Environment.getExternalStorageDirectory()
							.getAbsolutePath();
					StatFs statfs1 = new StatFs(s2);
					statfs1.restat(s2);
					boolean flag1 = l < (long) statfs1.getAvailableBlocks()
							* (long) statfs1.getBlockSize();
					if (flag1)
						s1 = (new StringBuilder(String.valueOf(s2))).append(
								"/12530/cache").toString();
				}
			} else
			{
				s1 = (new StringBuilder(String.valueOf(s))).append("/cache")
						.toString();
			}
			if (s1 == null)
			{
				throw new FileNotFoundException(
						"can not create cache file, no enough space ");
			} else
			{
				File file1 = new File(s1);
				if (!file1.exists())
					file1.mkdirs();
				CacheSongData.getInstance().setCacheFilePath(
						(new StringBuilder(String.valueOf(s1))).append(
								"/cachefile.mp4").toString());
				file = new File((new StringBuilder(String.valueOf(s1))).append(
						"/cachefile.mp4").toString());
				file.deleteOnExit();
				if (file.exists())
					file.delete();
				file.createNewFile();
				if (!file.exists())
					throw new FileNotFoundException((new StringBuilder(
							"can not find ")).append(file.getAbsolutePath())
							.toString());
				fillFile(l, file);
			}
		}
		return file;
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
		} catch (Exception e)
		{
			e.printStackTrace();
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
		boolean flag;
		if (state == State.SUCCESS)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public boolean isEnd()
	{
		boolean flag;
		if (isAlive() && !isInterrupted() && !stopped && percent < 1.0F
				&& (state == State.CONNECT || state == State.SAVE))
			flag = false;
		else
			flag = true;
		return flag;
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