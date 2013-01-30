package org.ming.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.ming.center.MobileMusicApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class FileUtils
{
	private static FileUtils fileUtil = null;
	private static final MyLogger logger = MyLogger.getLogger("FileUtils");

	public static void deleteFile(String paramString)
	{
		logger.v("deleteFile(String filePath) Access");
		new File(paramString).delete();
	}

	public static String existThenRenameFile(String paramString)
	{
		// File localFile = new File(paramString);
		// String str1 = localFile.getName();
		// int i = str1.lastIndexOf(".");
		// String str3;
		// String str4;
		// int k;
		// if (-1 != i)
		// {
		// str3 = str1.substring(0, i);
		// str4 = str1.substring(i + 1);
		// k = 0;
		// if (localFile.exists())
		// ;
		// }
		//
		// StringBuilder localStringBuilder2 = new StringBuilder(
		// String.valueOf(str3)).append("(");
		// k++;
		// str1 = k + ")" + "." + str4;
		// localFile = new File(localFile.getParent(), str1);
		// break;
		// String str2 = str1;
		// int j = 0;
		// while (localFile.exists())
		// {
		// StringBuilder localStringBuilder1 = new StringBuilder(
		// String.valueOf(str2)).append("(");
		// j++;
		// str1 = j + ")";
		// localFile = new File(localFile.getParent(), str1);
		// }
		// return localFile.getParent() + File.separator + str1;
		return null;
	}

	public static Bitmap getBitmapFromCache(String paramString, int paramInt1,
			int paramInt2)
	{
		Bitmap bitmap = null;
		try
		{
			Bitmap localBitmap1 = BitmapFactory.decodeFile(getCacheDir() + "/"
					+ paramString);
			bitmap = localBitmap1;
			if ((paramInt1 != -1) && (paramInt2 != -1))
			{
				Bitmap localBitmap2 = imageScale(localBitmap1, paramInt1,
						paramInt2);
				bitmap = localBitmap2;
			}

		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return bitmap;
	}

	public static String getCacheDir()
	{
		String str = MobileMusicApplication.getInstance().getApplicationInfo().dataDir;
		new StatFs(str).restat(str);
		return str + "/mobilemusic4";
	}

	public static String getCacheFileName(String paramString)
	{
		return Util.encodeByMD5(paramString);
	}

	public static byte[] getFileData(String paramString)
	{
		byte[] arrayOfByte1 = (byte[]) null;
		// try
		// {
		// InputStream localInputStream = MobileMusicApplication.getInstance()
		// .getAssets().open("cache-data/" + paramString);
		// arrayOfByte1 = new byte[localInputStream.available()];
		// localInputStream.read(arrayOfByte1);
		// localInputStream.close();
		// arrayOfByte2 = arrayOfByte1;
		// return arrayOfByte2;
		// } catch (Exception localException)
		// {
		// while (true)
		// {
		// localException.printStackTrace();
		// byte[] arrayOfByte2 = arrayOfByte1;
		// }
		// }
		return arrayOfByte1;
	}

	public static FileUtils getInstance()
	{
		logger.v("FileUtils getInstance() Access");
		if (fileUtil == null)
			fileUtil = new FileUtils();
		return fileUtil;
	}

	public static File getNewFile(File paramFile, String paramString)
	{
		logger.v("FileUtils getNewFile(File path, String name) Access");
		File localFile = new File(paramFile, paramString);
		// int i;
		// int k;
		// if (localFile.exists())
		// {
		// i = paramString.length();
		// int j = paramString.charAt(-1 + paramString.length());
		// if ((j < 48) || (j > 57))
		// break label98;
		// k = 1 + (j - 48);
		// }
		// label98: for (localFile = getNewFile(paramFile,
		// paramString.substring(0, i - 1) + k);; localFile = getNewFile(
		// paramFile, paramString + '1'))
		return localFile;
	}

	public static File getNewFile(String paramString1, String paramString2)
	{
		logger.v("FileUtils getNewFile(String path, String name) Access");
		return getNewFile(new File(paramString1), paramString2);
	}

	public static Bitmap imageScale(Bitmap paramBitmap, int paramInt1,
			int paramInt2)
	{
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		float f1 = paramInt1 / i;
		float f2 = paramInt2 / j;
		Matrix localMatrix = new Matrix();
		localMatrix.postScale(f1, f2);
		return Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);
	}

	public static boolean isHasSDCard()
	{
		if ("mounted".equals(Environment.getExternalStorageState()))
			;
		for (boolean bool = true;; bool = false)
			return bool;
	}

	public static void saveBitmapToCache(Bitmap paramBitmap, String paramString)
	{
		String str1 = MobileMusicApplication.getInstance().getApplicationInfo().dataDir;
		new StatFs(str1).restat(str1);
		String str2 = str1 + "/mobilemusic4";
		if (str2 == null)
			;
		File localFile1 = new File(str2);
		if (!localFile1.exists())
			localFile1.mkdirs();
		File localFile2 = new File(str2 + "/" + paramString);
		localFile2.deleteOnExit();
		if (localFile2.exists())
			localFile2.delete();
		try
		{
			localFile2.createNewFile();
			localFile2.exists();
			FileOutputStream localFileOutputStream = new FileOutputStream(
					localFile2);
			paramBitmap.compress(Bitmap.CompressFormat.PNG, 100,
					localFileOutputStream);
			localFileOutputStream.flush();
			localFileOutputStream.close();
			return;
		} catch (IOException localIOException)
		{
			while (true)
				localIOException.printStackTrace();
		}
	}

	public static void saveBitmapToCache(Bitmap paramBitmap,
			String paramString1, String paramString2)
	{
		// if (paramString1 == null)
		// ;
		// while (true)
		// {
		// return;
		// File localFile1 = new File(paramString1);
		// if (!localFile1.exists())
		// localFile1.mkdirs();
		// File localFile2 = new File(paramString1 + "/" + paramString2);
		// localFile2.deleteOnExit();
		// if (localFile2.exists())
		// localFile2.delete();
		// try
		// {
		// localFile2.createNewFile();
		// localFile2.exists();
		// FileOutputStream localFileOutputStream = new FileOutputStream(
		// localFile2);
		// if (paramBitmap != null)
		// {
		// paramBitmap.compress(Bitmap.CompressFormat.PNG, 50,
		// localFileOutputStream);
		// localFileOutputStream.flush();
		// localFileOutputStream.close();
		// }
		// } catch (IOException localIOException)
		// {
		// localIOException.printStackTrace();
		// }
		// }
	}

	public File createDir(String paramString)
	{
		logger.v(getClass().getName() + "createDir(String dir) Access");
		File localFile = new File(getSDCardRoot() + paramString
				+ File.separator);
		if (!localFile.exists())
			localFile.mkdir();
		return localFile;
	}

	public File createFileInSdcard(String paramString1, String paramString2)
			throws IOException
	{
		logger.v(getClass().getName()
				+ "createFileInSdcard(String dir, String fileName) Access");
		File localFile = new File(getSDCardRoot() + paramString1
				+ File.separator + paramString2);
		if (localFile.createNewFile())
			Log.i("flag", "make success");

		Log.i("flag", "the file is exits");
		return localFile;
	}

	public void deleteFile(String paramString1, String paramString2)
	{
		logger.v(getClass().getName()
				+ "deleteFile(String fileName, String dir) Access");
		new File(getSDCardRoot() + paramString2 + File.separator + paramString1)
				.delete();
	}

	public String getFilePath(String paramString1, String paramString2)
	{
		logger.v(getClass().getName()
				+ "getFilePath(String dir, String fielName) Access");
		File localFile = new File(getSDCardRoot() + paramString1
				+ File.separator + paramString2);
		boolean bool = localFile.exists();
		String str = null;
		if (bool)
			str = localFile.getPath();
		return str;
	}

	public String getSDCardRoot()
	{
		logger.v(getClass().getName() + "getSDCardRoot() Access");
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
	}

	public boolean isFileExits(String paramString1, String paramString2)
			throws IOException
	{
		logger.v(getClass().getName()
				+ "isFileExits(String dir, String fileName) Access");
		return new File(getSDCardRoot() + paramString1 + File.separator
				+ paramString2).exists();
	}

	public File writeInputStreamToSDCard(String s, String s1,
			InputStream inputstream) throws IOException
	{
		File file = null;
		// FileOutputStream fileoutputstream;
		// byte abyte0[];
		// logger.v((new
		// StringBuilder(String.valueOf(getClass().getName()))).append("writeInputStreamToSDCard(String dir, String fileName, InputStream inputstream) Access").toString());
		// createDir(s);
		// file = createFileInSdcard(s, s1);
		// fileoutputstream = new FileOutputStream(file);
		// abyte0 = new byte[4096];
		// _L2:
		// int i;
		// i = inputstream.read(abyte0);
		// if(i != -1)
		// break MISSING_BLOCK_LABEL_91;
		// fileoutputstream.flush();
		// Exception exception;
		// IOException ioexception;
		// try
		// {
		// fileoutputstream.close();
		// }
		// catch(Exception exception3)
		// {
		// logger.e(exception3.toString());
		// }
		// return file;
		// fileoutputstream.write(abyte0, 0, i);
		// continue; /* Loop/switch isn't completed */
		// ioexception;
		// ioexception.printStackTrace();
		// try
		// {
		// fileoutputstream.close();
		// }
		// catch(Exception exception2)
		// {
		// logger.e(exception2.toString());
		// }
		// if(false)
		// ;
		// else
		// break MISSING_BLOCK_LABEL_88;
		// exception;
		// try
		// {
		// fileoutputstream.close();
		// }
		// catch(Exception exception1)
		// {
		// logger.e(exception1.toString());
		// }
		// throw exception;
		// if(true) goto _L2; else goto _L1
		// _L1:
		return file;
	}
}