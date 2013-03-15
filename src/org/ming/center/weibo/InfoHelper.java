package org.ming.center.weibo;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

public class InfoHelper
{
	public static final String SDCARD = "/sdcard";
	public static final String SDCARD_MNT = "/mnt/sdcard";

	public static boolean checkNetWork(Context paramContext)
	{
		NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
				.getSystemService("connectivity")).getActiveNetworkInfo();
		boolean bool = false;
		if (localNetworkInfo != null)
			bool = true;
		return bool;
	}

	public static String getAbsolutePathFromNoStandardUri(Uri uri)
	{
		String s;
		String s1;
		String s2;
		String s3 = null;
		s = Uri.decode(uri.toString());
		s1 = (new StringBuilder("file:///sdcard")).append(File.separator)
				.toString();
		s2 = (new StringBuilder("file:///mnt/sdcard")).append(File.separator)
				.toString();
		if (!s.startsWith(s1))
		{
			boolean flag = s.startsWith(s2);
			if (flag)
				s3 = (new StringBuilder(String.valueOf(Environment
						.getExternalStorageDirectory().getPath())))
						.append(File.separator)
						.append(s.substring(s2.length())).toString();
		} else
		{
			s3 = (new StringBuilder(String.valueOf(Environment
					.getExternalStorageDirectory().getPath())))
					.append(File.separator).append(s.substring(s1.length()))
					.toString();
		}
		return s3;
	}

	public static AccessInfo getAccessInfo(Context context)
	{
		AccessInfoHelper accessinfohelper = new AccessInfoHelper(context);
		accessinfohelper.open();
		AccessInfo accessinfo = null;
		try
		{
			ArrayList arraylist = accessinfohelper.getAccessInfos();
			accessinfohelper.close();
			if (arraylist != null && arraylist.size() != 0)
				accessinfo = (AccessInfo) arraylist.get(-1 + arraylist.size());
			return accessinfo;
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			accessinfohelper.close();
		}
		return accessinfo;
	}

	public static String getCamerPath()
	{
		return Environment.getExternalStorageDirectory() + File.separator
				+ "FounderNews" + File.separator;
	}

	public static String getFileName()
	{
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS")
				.format(new Timestamp(System.currentTimeMillis()));
	}

	public static Bitmap getScaleBitmap(Context paramContext, String paramString)
	{
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inSampleSize = 4;
		return BitmapFactory.decodeFile(paramString, localOptions);
	}
}