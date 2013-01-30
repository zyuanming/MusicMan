package org.ming.center.download;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ming.center.cachedata.CacheDataManager;
import org.ming.util.FileUtils;
import org.ming.util.MyLogger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ImageView;

public abstract class AbstractImageDownloader
{
	private static final int DELAY_BEFORE_PURGE = 1000;
	private static final int HARD_CACHE_CAPACITY = 32;
	public static final String KEY_ELAPSED_TIME = "KEY_ELAPSED_TIME";
	public static final String KEY_PROGRESS = "KEY_PROGRESS";
	private static final int PUBLISH_PROGRESS_TIME_THRESHOLD_MILLI = 500;
	private static final MyLogger logger = MyLogger
			.getLogger("AbstractImageDownloader");
	private static CacheDataManager mCacheDataManger = CacheDataManager
			.getInstance();
	private static List<ImageDownloadTask> mRunningList = Collections
			.synchronizedList(new ArrayList<ImageDownloadTask>());
	private static List<ImageDownloadTask> mWaitingList = Collections
			.synchronizedList(new ArrayList<ImageDownloadTask>());
	private Context mContext;
	private Handler mHandler;
	private final Handler purgeHandler = new Handler();
	private final Runnable purger = new Runnable()
	{
		public void run()
		{
			AbstractImageDownloader.this.clearCache();
		}
	};
	private LinkedHashMap sHardBitmapCache = new LinkedHashMap(16, 0.75F, true)
	{
		protected boolean removeEldestEntry(Map.Entry entry)
		{
			boolean flag;
			if (size() > 32)
			{
				sSoftBitmapCache.put((String) entry.getKey(),
						new SoftReference((Bitmap) entry.getValue()));
				flag = true;
			} else
			{
				flag = false;
			}
			return flag;
		}
	};
	private ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			16);

	private AbstractImageDownloader()
	{}

	protected AbstractImageDownloader(Context paramContext)
	{
		this.mContext = paramContext;
		this.mHandler = new Handler(paramContext.getMainLooper());
	}

	private void addBitmapToCache(String paramString, Bitmap paramBitmap)
	{
		if (paramBitmap != null)
			synchronized (this.sHardBitmapCache)
			{
				this.sHardBitmapCache.put(paramString, paramBitmap);
			}
	}

	private static boolean cancelPotentialDownload(String paramString)
	{
		logger.e("cancelPotentialDownload ---> Enter the key: " + paramString);
		boolean flag = true;
		for (int i = 0; i < mRunningList.size(); i++)
		{
			if (!((ImageDownloadTask) mRunningList.get(i)).mKey
					.equals(paramString))
			{
				flag = false;
			}
		}
		return flag;
	}

	private static boolean cancelPotentialDownloadNoKey(String s,
			ImageView imageview)
	{
		boolean flag = true;
		ImageDownloadTask imagedownloadtask = getDownloadTask(imageview);
		if (imagedownloadtask != null)
			if (imagedownloadtask.mKey == null
					|| !imagedownloadtask.mKey.equals(s))
			{
				logger.e("cancelPotentialDownload ---> cancel");
				mRunningList.remove(imagedownloadtask);
				mWaitingList.remove(imagedownloadtask);
				imagedownloadtask.cancel(flag);
				imagedownloadtask.setProgressListener(null);
			} else
			{
				flag = false;
			}
		return flag;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int i, int j)
	{
		int k;
		int l;
		double d = options.outWidth;
		double d1 = options.outHeight;
		if (j == -1)
			k = 1;
		else
			k = (int) Math.ceil(Math.sqrt((d * d1) / (double) j));
		if (i == -1)
			l = 128;
		else
			l = (int) Math.min(Math.floor(d / (double) i),
					Math.floor(d1 / (double) i));
		if (l >= k)
		{
			if (j == -1 && i == -1)
				k = 1;
			else if (i != -1)
				k = l;
		}
		return k;
	}

	private static int computeSampleSize(BitmapFactory.Options options, int i,
			int j)
	{
		int k;
		int l = 1;
		k = computeInitialSampleSize(options, i, j);
		if (k < 8)
		{
			while (l < k)
			{
				l <<= 1;
			}
			l = 8 * ((k + 7) / 8);
			return l;
		}
		return l;
	}

	private void forceDownload(String s, ImageView imageView, Bitmap bitmap,
			ProgressListener progressListener, String s1)
	{
		if (s != null)
		{
			if (cancelPotentialDownloadNoKey(s, imageView))
			{
				ImageDownloadTask imagedownloadtask = new ImageDownloadTask(s,
						imageView, progressListener, s1);
				Object obj;
				if (bitmap == null)
					obj = new DownloadedDrawable(imagedownloadtask);
				else
					obj = new DefaultImageDrawable(imagedownloadtask, bitmap);
				imageView
						.setImageDrawable(((android.graphics.drawable.Drawable) (obj)));
				logger.e((new StringBuilder(
						"cancelPotentialDownload ---> Enter the mRunningList size: "))
						.append(mRunningList.size()).toString());
				if (mRunningList.size() < 20
						&& !mRunningList.contains(imagedownloadtask))
				{
					logger.e((new StringBuilder(
							"cancelPotentialDownload ---> Enter the key: "))
							.append(s).toString());
					if (cancelPotentialDownload(s))
					{
						mRunningList.add(imagedownloadtask);
						imagedownloadtask.execute(new Void[0]);
					}
				} else if (mWaitingList.size() < 20)
				{
					mWaitingList.add(imagedownloadtask);
				} else
				{
					mWaitingList.remove(19);
					mWaitingList.add(0, imagedownloadtask);
				}
			}
		} else
		{
			imageView.setImageDrawable(null);
		}
	}

	private Bitmap getBitmapFromCache(String s)
	{
		Bitmap bitmap1 = null;
		synchronized (this.sHardBitmapCache)
		{
			Bitmap bitmap = (Bitmap) this.sHardBitmapCache.get(s);
			if (bitmap != null)
			{
				this.sHardBitmapCache.remove(s);
				this.sHardBitmapCache.put(s, bitmap);
				bitmap1 = bitmap;
			} else
			{
				SoftReference localSoftReference = (SoftReference) this.sSoftBitmapCache
						.get(s);
				if (localSoftReference != null)
				{
					Bitmap bitmap2 = (Bitmap) localSoftReference.get();
					if (bitmap2 != null)
						bitmap1 = bitmap2;
				}
			}
		}
		this.sSoftBitmapCache.remove(s);
		return bitmap1;
	}

	private static ImageDownloadTask getDownloadTask(ImageView paramImageView)
	{

		ImageDownloadTask localImageDownloadTask = null;
		if (paramImageView != null)
		{
			Drawable localDrawable = paramImageView.getDrawable();
			if ((localDrawable instanceof DownloadedDrawable))
				localImageDownloadTask = ((DownloadedDrawable) localDrawable)
						.getDownloadTask();
		}
		return localImageDownloadTask;
	}

	private void resetPurgeTimer()
	{
		this.purgeHandler.removeCallbacks(this.purger);
		this.purgeHandler.postDelayed(this.purger, 1000L);
	}

	public void clearCache()
	{
		logger.v("clearCache() ---> Enter" + this.sHardBitmapCache.size());
		this.sHardBitmapCache.clear();
		this.sSoftBitmapCache.clear();
		logger.v("clearCache() ---> Exit" + this.sHardBitmapCache.size());
	}

	protected abstract Bitmap download(String paramString,
			WeakReference<ImageView> paramWeakReference);

	public void download(String s, int i, ImageView imageView, String s1)
	{
		try
		{
			Bitmap bitmap;
			Bitmap bitmap1;
			Drawable localDrawable = this.mContext.getResources()
					.getDrawable(i);
			bitmap = null;
			if (localDrawable != null)
			{
				imageView.setImageDrawable(localDrawable);
				bitmap1 = ((BitmapDrawable) localDrawable).getBitmap();
				bitmap = bitmap1;
				if (bitmap != null)
				{
					imageView.setImageBitmap(bitmap);
					download(s, imageView, bitmap, null, s1);
				} else
				{
					imageView.setImageBitmap(null);
				}
			} else
			{
				imageView.setImageDrawable(null);
			}

		} catch (OutOfMemoryError localOutOfMemoryError)
		{
			localOutOfMemoryError.printStackTrace();
		}
	}

	public void download(String s, ImageView paramImageView, Bitmap bitmap,
			ProgressListener progressListener, String s1)
	{
		resetPurgeTimer();
		Bitmap bitmap1 = null;
		if ((s == null) || (s.equals("")))
			bitmap1 = bitmap;
		String str1;
		int j;
		if ((bitmap1 == null) && (s != null) && (!s.equals("")))
		{
			int i = s.indexOf("//");
			if (i != -1)
			{
				str1 = s.substring(i + 2);
				j = str1.lastIndexOf("/");
				if (j != -1)
				{
					try
					{
						String str2 = str1.substring(j);
						String str3 = mCacheDataManger.getCacheData(str2);
						BitmapFactory.Options localOptions = new BitmapFactory.Options();
						localOptions.inSampleSize = computeSampleSize(
								localOptions, -1, 16384);
						localOptions.inJustDecodeBounds = false;
						Bitmap localBitmap = BitmapFactory.decodeFile(str3,
								localOptions);
						bitmap1 = localBitmap;
						if (bitmap1 == null)
						{
							forceDownload(s, paramImageView, bitmap,
									progressListener, s1);
						} else
						{
							bitmap1 = getBitmapFromCache(s);
							addBitmapToCache(s, (Bitmap) bitmap1);
							paramImageView.setImageBitmap((Bitmap) bitmap1);
							if (progressListener != null)
								progressListener.onProgressUpdated(100, 0L);
						}
					} catch (OutOfMemoryError localOutOfMemoryError)
					{
						localOutOfMemoryError.printStackTrace();
					}
				}
			}
		}
	}

	protected boolean isCancelled(WeakReference<ImageView> weakreference)
	{
		boolean flag;
		ImageView imageview;
		flag = true;
		imageview = (ImageView) weakreference.get();
		if (imageview != null)
		{
			ImageDownloadTask imagedownloadtask = getDownloadTask(imageview);
			if (imagedownloadtask != null)
				flag = imagedownloadtask.isCancelled();
		}
		return flag;
	}

	protected void publishProgress(int paramInt,
			WeakReference<ImageView> paramWeakReference)
	{
		ImageView localImageView = (ImageView) paramWeakReference.get();
		if (localImageView == null)
			;
		ImageDownloadTask localImageDownloadTask = getDownloadTask(localImageView);
		if (localImageDownloadTask != null)
			localImageDownloadTask.publishProgress(paramInt);
	}

	private static final class DefaultImageDrawable extends BitmapDrawable
	{
		private WeakReference<AbstractImageDownloader.ImageDownloadTask> downloadTaskReference = null;

		public DefaultImageDrawable(
				AbstractImageDownloader.ImageDownloadTask paramImageDownloadTask,
				Bitmap paramBitmap)
		{
			super();
			this.downloadTaskReference = new WeakReference(
					paramImageDownloadTask);
		}

		public AbstractImageDownloader.ImageDownloadTask getDownloadTask()
		{
			return (AbstractImageDownloader.ImageDownloadTask) this.downloadTaskReference
					.get();
		}
	}

	private static final class DownloadedDrawable extends ColorDrawable
	{
		private final WeakReference<AbstractImageDownloader.ImageDownloadTask> downloadTaskReference;

		public DownloadedDrawable(
				AbstractImageDownloader.ImageDownloadTask paramImageDownloadTask)
		{
			super();
			this.downloadTaskReference = new WeakReference(
					paramImageDownloadTask);
		}

		public AbstractImageDownloader.ImageDownloadTask getDownloadTask()
		{
			return (AbstractImageDownloader.ImageDownloadTask) this.downloadTaskReference
					.get();
		}
	}

	private final class ImageDownloadTask extends AsyncTask<Void, Void, Bitmap>
	{
		private String mGroupCode;
		private WeakReference<ImageView> mImageViewReference;
		String mKey;
		private long mLastUpdateTime;
		private AbstractImageDownloader.ProgressListener mProgressListener;
		private long mTimeBegin;

		public ImageDownloadTask(String s, ImageView paramProgressListener,
				AbstractImageDownloader.ProgressListener paramString1, String s1)
		{
			this.mKey = s;
			this.mImageViewReference = new WeakReference(paramProgressListener);
			this.mProgressListener = paramString1;
			this.mTimeBegin = SystemClock.elapsedRealtime();
			this.mGroupCode = s1;
		}

		private Bitmap downloadImage()
		{
			return AbstractImageDownloader.this.download(this.mKey,
					this.mImageViewReference);
		}

		protected Bitmap doInBackground(Void[] paramArrayOfVoid)
		{
			String str1 = FileUtils.getCacheFileName(this.mKey);
			int i = this.mKey.indexOf("//");
			if (i != -1)
			{
				String str2 = this.mKey.substring(i + 2);
				int j = str2.lastIndexOf("/");
				if (j != -1)
				{
					String str3 = str2.substring(j);
					AbstractImageDownloader.mCacheDataManger.saveCacheData(
							str3, this.mGroupCode, null, "/sdcard/redcloud/"
									+ str1);
				}
			}
			return downloadImage();
		}

		protected void onPostExecute(Bitmap bitmap)
		{
			AbstractImageDownloader.logger.e((new StringBuilder(
					"onPostExecute() ---> Enter bitmap")).append(bitmap)
					.toString());
			if (isCancelled())
			{
				AbstractImageDownloader.logger
						.e("onPostExecute() ---> Enter bitmap isCancelled");
				bitmap = null;
			}
			addBitmapToCache(mKey, bitmap);
			boolean flag;
			if (bitmap != null)
			{
				ImageView imageview = (ImageView) mImageViewReference.get();
				if (this == AbstractImageDownloader.getDownloadTask(imageview))
					imageview.setImageBitmap(bitmap);
			} else
			{
				logger.e((new StringBuilder("could not download bitmap: "))
						.append(mKey).toString());
			}
			flag = mRunningList.remove(this);
			logger.e((new StringBuilder("onPostExecute() remove task is: "))
					.append(flag).toString());
			if (AbstractImageDownloader.mWaitingList != null
					&& AbstractImageDownloader.mWaitingList.size() > 0
					&& AbstractImageDownloader.mRunningList.size() < 20)
			{
				ImageDownloadTask imagedownloadtask = (ImageDownloadTask) AbstractImageDownloader.mWaitingList
						.get(0);
				mRunningList.add(imagedownloadtask);
				imagedownloadtask.execute(new Void[0]);
				mWaitingList.remove(imagedownloadtask);
			}
			logger.e((new StringBuilder(
					"onPostExecute() the mRunningList size is")).append(
					AbstractImageDownloader.mRunningList.size()).toString());
			logger.e((new StringBuilder(
					"onPostExecute() the mWaitingList size is")).append(
					AbstractImageDownloader.mWaitingList.size()).toString());
			logger.e("onPostExecute() ---> Exit");
		}

		public void publishProgress(final int i)
		{
			if (mProgressListener != null
					&& (i >= 100 || SystemClock.elapsedRealtime()
							- mLastUpdateTime >= 500L))
			{
				this.mLastUpdateTime = SystemClock.elapsedRealtime();
				mHandler.post(new Runnable()
				{
					public void run()
					{
						if (mProgressListener != null)
						{
							long l = mLastUpdateTime - mTimeBegin;
							mProgressListener.onProgressUpdated(i, l);
						}
					}
				});
			}
		}

		public void setProgressListener(
				AbstractImageDownloader.ProgressListener paramProgressListener)
		{
			this.mProgressListener = paramProgressListener;
		}
	}

	public static interface ProgressListener
	{
		public abstract void onProgressUpdated(int paramInt, long paramLong);
	}
}
