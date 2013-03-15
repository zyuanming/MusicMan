package org.ming.ui.view;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.RejectedExecutionException;

import org.ming.center.MobileMusicApplication;
import org.ming.center.cachedata.CacheDataManager;
import org.ming.ui.util.ImageCache;
import org.ming.util.FileUtils;
import org.ming.util.MyLogger;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ListView;

public class RemoteImageView extends ImageView
{
	class DownloadTask extends AsyncTask
	{
		private String mTaskUrl;

		private Bitmap decodeFile(InputStream inputstream)
		{
			Bitmap localBitmap1 = null;
			try
			{
				BitmapFactory.Options localOptions1 = new BitmapFactory.Options();
				localOptions1.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(inputstream, null, localOptions1);
				int i = 1;
				while (true)
				{
					if ((localOptions1.outWidth / i / 2 < 70)
							|| (localOptions1.outHeight / i / 2 < 70))
					{
						BitmapFactory.Options localOptions2 = new BitmapFactory.Options();
						localOptions2.inSampleSize = i;
						Bitmap localBitmap2 = BitmapFactory.decodeStream(
								inputstream, null, localOptions2);
						localBitmap1 = localBitmap2;
						return localBitmap1;
					}
					i *= 2;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return localBitmap1;
		}

		private String splitUrl(String s)
		{
			String s1;
			if (s == null || "".equals(s))
				s1 = "";
			else
				s1 = s.substring(1 + s.lastIndexOf("/"), s.lastIndexOf("."));
			return s1;
		}

		public String doInBackground(String as[])
		{
			String s = null;
			InputStream inputstream;
			mTaskUrl = as[0];
			inputstream = null;
			try
			{
				if (!mIsCacheData
						|| !RemoteImageView.mCacheDataManger
								.isInCacheData(mTaskUrl))
				{
					URL url = new URL(mTaskUrl);
					inputstream = url.openStream();
					mBitmap = BitmapFactory.decodeStream(inputstream);
					if (mBitmap == null)
					{
						RemoteImageView.logger.e("bmp-->null");
						if (inputstream != null)
							inputstream.close();
						if (inputstream == null)
						{
							s = mTaskUrl;
							return s;
						} else
						{
							if (inputstream != null)
							{
								inputstream.close();
								s = mTaskUrl;
								return s;
							}
						}
					} else
					{
						if (mIsCacheData)
						{
							String s1 = FileUtils.getCacheFileName(mTaskUrl);
							FileUtils.saveBitmapToCache(mBitmap,
									"/sdcard/redcloud/", s1);
							RemoteImageView.mCacheDataManger.saveCacheData(
									mTaskUrl, null, null, (new StringBuilder(
											"/sdcard/redcloud/")).append(s1)
											.toString());
						}
						MobileMusicApplication.getImageCache().put(mTaskUrl,
								mBitmap);
					}
				} else
				{
					Bitmap bitmap = BitmapFactory
							.decodeFile(RemoteImageView.mCacheDataManger
									.getCacheData(mTaskUrl));
					if (bitmap == null)
					{
						RemoteImageView.logger.e("bmp-->null");
					} else
					{
						mBitmap = bitmap;
						s = mTaskUrl;
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return s;
		}

		public void onPostExecute(String s)
		{
			super.onPostExecute(s);
			if (mTaskUrl == null || mTaskUrl.equals(mUrl))
			{
				if (mCurrentDialog != null)
				{
					mCurrentDialog.cancel();
					mCurrentDialog.dismiss();
				}
				if (mBitmap == null)
					setImageUrl(s);
				else if (mListView == null
						|| mPosition >= mListView.getFirstVisiblePosition()
						&& mPosition <= mListView.getLastVisiblePosition())
				{
					setImageBitmap(mBitmap);
					mCurrentlyGrabbedUrl = s;
				}
			}
		}

		public void onPreExecute()
		{
			loadDefaultImage();
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object aobj[])
		{
			return doInBackground((String[]) aobj);
		}
	}

	class DownloadTaskWithShadow extends AsyncTask
	{

		private String mTaskUrl;

		public String doInBackground(String as[])
		{
			String s = null;
			InputStream inputstream;
			mTaskUrl = as[0];
			inputstream = null;
			try
			{
				if (!mIsCacheData
						|| !RemoteImageView.mCacheDataManger
								.isInCacheData(mTaskUrl))
				{
					URL url = new URL(mTaskUrl);
					Bitmap bitmap;
					inputstream = url.openStream();
					bitmap = BitmapFactory.decodeStream(inputstream);
					if (bitmap == null)
					{
						RemoteImageView.logger.e("bmp-->null");
						if (inputstream != null)
							inputstream.close();
						if (inputstream == null)
						{
							s = mTaskUrl;
							return s;
						} else
						{
							if (inputstream == null)
								inputstream.close();
							return s;
						}
					} else
					{
						mBitmap = bitmap;
						if (mIsCacheData)
						{
							FileUtils fileutils = new FileUtils();
							String s1 = FileUtils.getCacheFileName(mTaskUrl);
							fileutils.writeInputStreamToSDCard(
									"/sdcard/redcloud/", s1, inputstream);
							RemoteImageView.mCacheDataManger.saveCacheData(
									mTaskUrl,
									null,
									null,
									(new StringBuilder(String
											.valueOf("/sdcard/redcloud/")))
											.append(s1).toString());
						}
					}
				} else
				{
					Bitmap bitmap1 = BitmapFactory
							.decodeFile(RemoteImageView.mCacheDataManger
									.getCacheData(mTaskUrl));
					if (bitmap1 == null)
					{
						RemoteImageView.logger.e("bmp-->null");
					} else
					{
						mBitmap = bitmap1;
						s = mTaskUrl;
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return s;
		}

		public void onPostExecute(String s)
		{
			super.onPostExecute(s);
			if (mTaskUrl.equals(mUrl))
			{
				if (mBitmap == null)
					setImageUrl(s);
				else if (mListView == null
						|| mPosition >= mListView.getFirstVisiblePosition()
						&& mPosition <= mListView.getLastVisiblePosition())
				{
					setImageBitmap(mBitmap);
					mCurrentlyGrabbedUrl = s;
				}
			}
		}

		public void onPreExecute()
		{
			loadDefaultImage();
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object aobj[])
		{
			return doInBackground((String[]) aobj);
		}
	}

	private static int MAX_FAILURES = 2;
	private static final MyLogger logger = MyLogger
			.getLogger("RemoteImageView");
	private static CacheDataManager mCacheDataManger = null;
	private Bitmap mBitmap;
	private Context mContext;
	private Dialog mCurrentDialog;
	private String mCurrentlyGrabbedUrl;
	private Integer mDefaultImage;
	private int mFailure;
	private boolean mIsCacheData;
	private ListView mListView;
	private int mMaxDimension;
	private int mPosition;
	private String mUrl;

	public RemoteImageView(Context context)
	{
		super(context);
		mBitmap = null;
		mCurrentDialog = null;
		mIsCacheData = false;
		init();
		mContext = context;
	}

	public RemoteImageView(Context context, AttributeSet attributeset)
	{
		super(context, attributeset);
		mBitmap = null;
		mCurrentDialog = null;
		mIsCacheData = false;
		init();
		mContext = context;
	}

	public RemoteImageView(Context context, AttributeSet attributeset, int i)
	{
		super(context, attributeset, i);
		mBitmap = null;
		mCurrentDialog = null;
		mIsCacheData = false;
		init();
		mContext = context;
	}

	public static Bitmap imageCrop(Bitmap bitmap)
	{
		int i = bitmap.getWidth();
		int j = bitmap.getHeight();
		if (i >= 150 && j >= 150)
		{
			int k;
			int l;
			int i1;
			if (i > j)
			{
				k = j;
				l = (i - j) / 2;
				i1 = 0;
			} else
			{
				k = i;
				l = 0;
				i1 = (j - i) / 2;
			}
			bitmap = Bitmap.createBitmap(bitmap, l, i1, k, k, null, false);
		}
		return bitmap;
	}

	private void init()
	{
		if (mCacheDataManger == null)
			mCacheDataManger = CacheDataManager.getInstance();
	}

	public void clearBitmap()
	{
		if (mBitmap != null)
		{
			setImageResource(0x7f0200cf);
			mBitmap.recycle();
			mBitmap = null;
		}
	}

	public String getURL()
	{
		return mUrl;
	}

	public void loadDefaultImage()
	{
		if (mDefaultImage != null)
			setImageResource(mDefaultImage.intValue());
	}

	public void setCurrentlyGrabbedUrl()
	{
		mCurrentlyGrabbedUrl = null;
		mUrl = null;
	}

	public void setDefaultImage(Integer integer)
	{
		mDefaultImage = integer;
	}

	public void setImagePicUrl(String s, Context context)
	{
		if ((this.mListView == null) && (this.mCurrentlyGrabbedUrl != null))
		{
			boolean bool = this.mCurrentlyGrabbedUrl.equals(s);
			if (bool)
				return;
		}
		if ((this.mUrl != null) && (this.mUrl.equals(s)))
		{
			this.mFailure = (1 + this.mFailure);
			if (this.mFailure > MAX_FAILURES)
				loadDefaultImage();
		}
		this.mUrl = s;
		this.mFailure = 0;
		ImageCache localImageCache = MobileMusicApplication.getImageCache();
		if ((localImageCache.isCached(s)) && (localImageCache.get(s) != null))
			setImageBitmap((Bitmap) localImageCache.get(s));
		else
		{
			try
			{
				new DownloadTask().execute(new String[] { s });
			} catch (RejectedExecutionException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setImageUrl(String s)
	{
		if ((this.mListView == null) && (this.mCurrentlyGrabbedUrl != null))
		{
			boolean bool = this.mCurrentlyGrabbedUrl.equals(s);
			if (bool)
				return;
		}
		if ((this.mUrl != null) && (this.mUrl.equals(s)))
		{
			this.mFailure = (1 + this.mFailure);
			if (this.mFailure > MAX_FAILURES)
				loadDefaultImage();
		}
		this.mUrl = s;
		this.mFailure = 0;
		ImageCache localImageCache = MobileMusicApplication.getImageCache();
		if ((localImageCache.isCached(s)) && (localImageCache.get(s) != null))
			setImageBitmap((Bitmap) localImageCache.get(s));
		else
		{
			try
			{
				new DownloadTask().execute(new String[] { s });
			} catch (RejectedExecutionException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setImageUrl(String s, int i, ListView listview)
	{
		mPosition = i;
		mListView = listview;
		setImageUrl(s);
	}

	public void setImageUrl(String s, boolean flag)
	{
		try
		{
			this.mIsCacheData = flag;
			setImageUrl(s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setImageUrlWithShadow(String s)
	{
		if (mListView != null || mCurrentlyGrabbedUrl == null
				|| !mCurrentlyGrabbedUrl.equals(s))
		{
			if (mUrl != null && mUrl.equals(s))
			{
				mFailure = 1 + mFailure;
				if (mFailure > MAX_FAILURES)
				{
					loadDefaultImage();
				}
			} else
			{
				mUrl = s;
				mFailure = 0;
				if (mBitmap != null)
					setImageBitmap(mBitmap);
				else
					try
					{
						(new DownloadTaskWithShadow())
								.execute(new String[] { s });
					} catch (RejectedExecutionException e)
					{
						e.printStackTrace();
					}
			}
		}
	}
}
