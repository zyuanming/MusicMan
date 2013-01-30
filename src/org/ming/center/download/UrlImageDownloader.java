package org.ming.center.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import org.ming.util.MyLogger;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class UrlImageDownloader extends AbstractImageDownloader
{
	private static final int BYTE_ARRAY_BUFFER_INCREMENTAL_SIZE = 1048;
	private static final String HTTP_CACHE_FILE_NAME = "image_downloader_http_cache";
	private static final long HTTP_CACHE_SIZE = 5242880L;
	public static String TAG = "UrlImageDownloader";
	private static final MyLogger logger = MyLogger
			.getLogger("AbstractImageDownloader");

	public UrlImageDownloader(Context paramContext)
	{
		super(paramContext);
		disableConnectionReuseIfNecessary();
		if (paramContext != null)
			enableHttpResponseCache(paramContext);
	}

	private void disableConnectionReuseIfNecessary()
	{
		logger.v("disableConnectionReuseIfNecessary() ---> Enter");
		System.setProperty("http.keepAlive", "false");
		logger.v("disableConnectionReuseIfNecessary() ---> Exit");
	}

	private void enableHttpResponseCache(Context context)
	{
		logger.v("enableHttpResponseCache() ---> Enter");
		File file = context.getCacheDir();
		if (file == null)
		{
			Log.w(TAG, "cache directory could not be found");
		} else
		{
			try
			{
				File file1 = new File(file, "image_downloader_http_cache");
				Class class1 = Class
						.forName("android.net.http.HttpResponseCache");
				Class aclass[] = new Class[2];
				aclass[0] = File.class;
				aclass[1] = Long.TYPE;
				Method method = class1.getMethod("install", aclass);
				Object aobj[] = new Object[2];
				aobj[0] = file1;
				aobj[1] = Long.valueOf(0x500000L);
				method.invoke(null, aobj);
			} catch (Exception exception)
			{
				Log.v(TAG, "HttpResponseCache is not available");
			}
			logger.v("enableHttpResponseCache() ---> Exit");
		}
	}

	public class FlushedInputStream extends BufferedInputStream
	{
		public FlushedInputStream(InputStream is)
		{
			super(is);
		}

		public long skip(long paramLong) throws IOException
		{
			UrlImageDownloader.logger.v("skip() ---> Enter");
			long l2 = 0;
			for (long l1 = 0L; l1 < paramLong; l1 += l2)
			{
				if (read() < 0)
				{
					l2 = this.in.skip(paramLong - l1);
				}
				if (l2 == 0L)
				{
					l2 = 1L;
				}
			}

			UrlImageDownloader.logger.v("skip() ---> Exit");
			return l2;
		}
	}

	protected Bitmap download(String s, WeakReference<ImageView> weakreference)
	{
		logger.v("UrlImageDownloaderdownload() ---> Enter");
		logger.d((new StringBuilder(
				"UrlImageDownloaderdownload(), the key is: ")).append(s)
				.toString());
		Bitmap bitmap = null;
		// if(s != null && !s.equals(""))
		// {
		// URL url = new URL(s);
		// HttpURLConnection httpurlconnection =
		// (HttpURLConnection)url.openConnection();
		// FlushedInputStream flushedinputstream;
		// int i;
		// bitmap = null;
		// flushedinputstream = null;
		// i = 0;
		// int j;
		// FlushedInputStream flushedinputstream1;
		// j = httpurlconnection.getContentLength();
		// flushedinputstream1 = new
		// FlushedInputStream(httpurlconnection.getInputStream());
		// ByteArrayBuffer bytearraybuffer;
		// byte abyte0[];
		// bytearraybuffer = new ByteArrayBuffer(1048);
		// abyte0 = new byte[1048];
		// }
		// else
		// {
		// bitmap = null;
		// }
		//
		// _L9:
		// boolean flag;
		// flag = isCancelled(weakreference);
		// bitmap = null;
		// if(!flag)
		// {
		// k = flushedinputstream1.read(abyte0);
		// bitmap = null;
		// if(k != -1)
		// {
		// i += k;
		// bitmap = null;
		// if(j <= 0)
		// {
		// if(i <= 0)
		// break MISSING_BLOCK_LABEL_259;
		// bitmap = null;
		// if(i != j)
		// break MISSING_BLOCK_LABEL_259;
		// }
		// publishProgress((i * 100) / j, weakreference);
		// bitmap = null;
		// if(bytearraybuffer != null)
		// {
		// bitmap = null;
		// if(abyte0 != null)
		// bytearraybuffer.append(abyte0, 0, k);
		// }
		// goto _L9
		// flushedinputstream = flushedinputstream1;
		// }
		// }
		// else
		// {
		// boolean flag1 = isCancelled(weakreference);
		// if(!flag1)
		// {
		// bitmap = BitmapFactory.decodeByteArray(bytearraybuffer.toByteArray(),
		// 0, bytearraybuffer.length());
		// FileUtils.saveBitmapToCache(bitmap, "/sdcard/redcloud/",
		// FileUtils.getCacheFileName(s));
		// if(httpurlconnection != null)
		// httpurlconnection.disconnect();
		// if(flushedinputstream1 != null)
		// try
		// {
		// flushedinputstream1.close();
		// }
		// catch(IOException ioexception5)
		// {
		// logger.e("download(), close() IOException: ", ioexception5);
		// }
		// break ;
		// if(httpurlconnection != null)
		// httpurlconnection.disconnect();
		// if(flushedinputstream1 != null)
		// try
		// {
		// flushedinputstream1.close();
		// }
		// catch(IOException ioexception4)
		// {
		// logger.e("download(), close() IOException: ", ioexception4);
		// }
		// bitmap = null;
		// return bitmap;
		// }
		// else
		// {
		// if(httpurlconnection != null)
		// httpurlconnection.disconnect();
		// int k;
		// if(flushedinputstream1 != null)
		// try
		// {
		// flushedinputstream1.close();
		// }
		// catch(IOException ioexception6)
		// {
		// logger.e("download(), close() IOException: ", ioexception6);
		// }
		// bitmap = null;
		// return bitmap;
		// logger.e("UrlImageDownloaderdownload(), MalformedURLException: ",
		// malformedurlexception);
		// bitmap = null;
		// return bitmap;
		// logger.e("UrlImageDownloaderdownload(), openConnection() IOException: ",
		// ioexception);
		// bitmap = null;
		// return bitmap;
		// }
		// }
		//
		// _L11:
		// if(httpurlconnection != null)
		// httpurlconnection.disconnect();
		// if(flushedinputstream != null)
		// try
		// {
		// flushedinputstream.close();
		// }
		// catch(IOException ioexception2)
		// {
		// logger.e("download(), close() IOException: ", ioexception2);
		// }
		// flushedinputstream = flushedinputstream1;
		// _L10:
		// bitmap = null;
		// flushedinputstream = null;
		// logger.e("UrlImageDownloaderdownload(), getInputStream() IOException: ",
		// ioexception1);
		// if(httpurlconnection != null)
		// httpurlconnection.disconnect();
		// if(flushedinputstream != null)
		// try
		// {
		// flushedinputstream.close();
		// }
		// catch(IOException ioexception3)
		// {
		// logger.e("download(), close() IOException: ", ioexception3);
		// }
		// logger.v("UrlImageDownloaderdownload() ---> Exit");
		return bitmap;
	}
}