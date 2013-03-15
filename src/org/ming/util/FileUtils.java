package org.ming.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	private static String SDCardRoot = Environment
			.getExternalStorageDirectory().getAbsolutePath() + File.separator;

	public static void deleteFile(String paramString)
	{
		logger.v("deleteFile(String filePath) Access");
		new File(paramString).delete();
	}

	public static String existThenRenameFile(String s)
	{
		File file;
		String s1;
		String s3;
		String s4;
		int k;
		file = new File(s);
		s1 = file.getName();
		int i = s1.lastIndexOf(".");
		if (-1 != i)
		{
			s3 = s1.substring(0, i);
			s4 = s1.substring(i + 1);
			k = 0;
			while (file.exists())
			{
				StringBuilder stringbuilder1 = (new StringBuilder(
						String.valueOf(s3))).append("(");
				k++;
				s1 = stringbuilder1.append(k).append(")").append(".")
						.append(s4).toString();
				file = new File(file.getParent(), s1);
			}
		} else
		{
			String s2 = s1;
			int j = 0;
			while (file.exists())
			{
				StringBuilder stringbuilder = (new StringBuilder(
						String.valueOf(s2))).append("(");
				j++;
				s1 = stringbuilder.append(j).append(")").toString();
				file = new File(file.getParent(), s1);
			}
		}
		return (new StringBuilder(String.valueOf(file.getParent())))
				.append(File.separator).append(s1).toString();
	}

	/**
	 * 以特定的压缩比例得到缓存目录中的位图数据
	 * 
	 * @param paramString
	 *            文件名称
	 * @param paramInt1
	 *            宽压缩比
	 * @param paramInt2
	 *            高压缩比
	 * @return
	 */
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
		String s = MobileMusicApplication.getInstance().getApplicationInfo().dataDir;
		(new StatFs(s)).restat(s);
		return (new StringBuilder(String.valueOf(s))).append("/mobilemusic4")
				.toString();
	}

	public static String getCacheFileName(String paramString)
	{
		return Util.encodeByMD5(paramString);
	}

	/**
	 * 得到默认缓存目录下的指定文件的数据
	 * 
	 * @param s
	 *            文件名称
	 * @return
	 */
	public static byte[] getFileData(String s)
	{
		byte abyte0[] = null;
		byte abyte1[] = null;
		try
		{
			InputStream inputstream = MobileMusicApplication
					.getInstance()
					.getAssets()
					.open((new StringBuilder("cache-data/")).append(s)
							.toString());
			abyte0 = new byte[inputstream.available()];
			inputstream.read(abyte0);
			inputstream.close();
			abyte1 = abyte0;
			abyte1 = abyte0;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return abyte1;

	}

	/**
	 * 获得FileUtils这个帮助类，单例模式
	 * 
	 * @return
	 */
	public static FileUtils getInstance()
	{
		logger.v("FileUtils getInstance() Access");
		if (fileUtil == null)
			fileUtil = new FileUtils();
		return fileUtil;
	}

	public static File getNewFile(File file, String s)
	{
		logger.v("FileUtils getNewFile(File path, String name) Access");
		File file1 = new File(file, s);
		if (file1.exists())
		{
			int i = s.length();
			char c = s.charAt(-1 + s.length());
			if (c >= '0' && c <= '9')
			{
				int j = 1 + (c - 48);
				file1 = getNewFile(
						file,
						(new StringBuilder(
								String.valueOf(s.substring(0, i - 1)))).append(
								j).toString());
			} else
			{
				file1 = getNewFile(file, (new StringBuilder(String.valueOf(s)))
						.append('1').toString());
			}
		}
		return file1;
	}

	public static File getNewFile(String paramString1, String paramString2)
	{
		logger.v("FileUtils getNewFile(String path, String name) Access");
		return getNewFile(new File(paramString1), paramString2);
	}

	/**
	 * 图片压缩
	 * 
	 * @param bitmap
	 *            原始位图文件
	 * @param i
	 *            宽压缩比例
	 * @param j
	 *            高压缩比例
	 * @return
	 */
	public static Bitmap imageScale(Bitmap bitmap, int i, int j)
	{
		int k = bitmap.getWidth();
		int l = bitmap.getHeight();
		float f = (float) i / (float) k;
		float f1 = (float) j / (float) l;
		Matrix matrix = new Matrix();
		matrix.postScale(f, f1);
		return Bitmap.createBitmap(bitmap, 0, 0, k, l, matrix, true);
	}

	/**
	 * 检查是否手机已插入SD卡
	 * 
	 * @return
	 */
	public static boolean isHasSDCard()
	{
		boolean flag;
		if ("mounted".equals(Environment.getExternalStorageState()))
			flag = true;
		else
			flag = false;
		return flag;
	}

	/**
	 * 把原始的位图文件压缩后写到默认的缓存目录中
	 * 
	 * @param bitmap
	 *            原始位图
	 * @param s
	 *            文件名称
	 */
	public static void saveBitmapToCache(Bitmap bitmap, String s)
	{
		File file1;
		String s1 = MobileMusicApplication.getInstance().getApplicationInfo().dataDir;
		(new StatFs(s1)).restat(s1);
		String s2 = (new StringBuilder(String.valueOf(s1))).append(
				"/mobilemusic4").toString();
		if (s2 != null)
		{
			File file = new File(s2);
			if (!file.exists())
				file.mkdirs();
			file1 = new File((new StringBuilder(String.valueOf(s2)))
					.append("/").append(s).toString());
			file1.deleteOnExit();
			if (file1.exists())
				file1.delete();
			try
			{
				file1.createNewFile();
				file1.exists();
				FileOutputStream fileoutputstream = new FileOutputStream(file1);
				bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,
						100, fileoutputstream);
				fileoutputstream.flush();
				fileoutputstream.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 把原始的位图文件压缩后写到指定的缓存目录中
	 * 
	 * @param bitmap
	 *            原始位图文件
	 * @param s
	 *            第一级目录名称
	 * @param s1
	 *            缓存文件名称
	 */
	public static void saveBitmapToCache(Bitmap bitmap, String s, String s1)
	{
		if (s != null)
		{
			File file = new File(s);
			if (!file.exists())
				file.mkdirs();
			File file1 = new File((new StringBuilder(String.valueOf(s)))
					.append("/").append(s1).toString());
			file1.deleteOnExit();
			if (file1.exists())
				file1.delete();
			try
			{
				file1.createNewFile();
				file1.exists();
				FileOutputStream fileoutputstream = new FileOutputStream(file1);
				if (bitmap != null)
				{
					bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,
							50, fileoutputstream);
					fileoutputstream.flush();
					fileoutputstream.close();
				}
			} catch (IOException ioexception)
			{
				ioexception.printStackTrace();
			}
		}
	}

	/**
	 * 创建目录
	 * 
	 * @param paramString
	 *            目录名称
	 * @return
	 */
	public File createDir(String paramString)
	{
		logger.v(getClass().getName() + "createDir(String dir) Access");
		File localFile = new File(getSDCardRoot() + paramString
				+ File.separator);
		if (!localFile.exists())
			localFile.mkdir();
		return localFile;
	}

	/**
	 * 在SD卡中创建一级目录的文件
	 * 
	 * @param s
	 *            第一级目录名称
	 * @param s1
	 *            文件名称
	 * @return
	 * @throws IOException
	 */
	public File createFileInSdcard(String s, String s1) throws IOException
	{
		logger.v((new StringBuilder(String.valueOf(getClass().getName())))
				.append("createFileInSdcard(String dir, String fileName) Access")
				.toString());
		File file = new File(
				(new StringBuilder(String.valueOf(getSDCardRoot()))).append(s)
						.append(File.separator).append(s1).toString());
		if (file.createNewFile())
			Log.i("flag", "make success");
		else
			Log.i("flag", "the file is exits");
		return file;
	}

	/**
	 * 删除文件
	 * 
	 * @param s1
	 *            第一级目录名称
	 * @param s2
	 *            文件名称
	 */
	public void deleteFile(String s1, String s2)
	{
		logger.v(getClass().getName()
				+ "deleteFile(String fileName, String dir) Access");
		new File(getSDCardRoot() + s2 + File.separator + s1).delete();
	}

	/**
	 * 获得文件的路径
	 * 
	 * @param s1
	 *            第一级目录名称
	 * @param s2
	 *            文件名称
	 * @return
	 */
	public String getFilePath(String s1, String s2)
	{
		logger.v(getClass().getName()
				+ "getFilePath(String dir, String fielName) Access");
		File localFile = new File(getSDCardRoot() + s1 + File.separator + s2);
		boolean bool = localFile.exists();
		String str = null;
		if (bool)
			str = localFile.getPath();
		return str;
	}

	/**
	 * 取得SD卡根目录的绝对路径
	 * 
	 * @return
	 */
	public static String getSDCardRoot()
	{
		logger.v("getSDCardRoot() Access");
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
	}

	/**
	 * 文件是否存在SD卡中
	 * 
	 * @param s1
	 *            第一级目录名称
	 * @param s2
	 *            文件名称
	 * @return
	 * @throws IOException
	 */
	public static boolean isFileExits(String s1, String s2) throws IOException
	{
		logger.v("isFileExits(String dir, String fileName) Access");
		return new File(getSDCardRoot() + s1 + File.separator + s2).exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public static File write2SDFromInput(String path, String fileName,
			InputStream input)
	{

		File file = null;
		OutputStream output = null;
		try
		{
			creatSDDir(path);
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int temp;
			while ((temp = input.read(buffer)) != -1)
			{
				output.write(buffer, 0, temp);
			}
			output.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				output.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public static File createFileInSDCard(String fileName, String dir)
			throws IOException
	{
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		System.out.println("file---->" + file);
		boolean flag = file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public static File creatSDDir(String dir)
	{
		Log.d("ming", "creatSDDir");
		Log.d("ming", dir);
		File dirFile = new File(SDCardRoot + dir + File.separator);
		Boolean flag = dirFile.mkdirs();
		Log.d("ming", String.valueOf(flag));
		return dirFile;
	}

	/**
	 * 把输入流数据写入SD卡中
	 * 
	 * @param s
	 *            目录名称
	 * @param s1
	 * @param inputstream
	 *            输入流
	 * @return
	 * @throws IOException
	 */
	public File writeInputStreamToSDCard(String s, String s1,
			InputStream inputstream) throws IOException
	{
		File file;
		FileOutputStream fileoutputstream;
		byte abyte0[];
		logger.v((new StringBuilder(String.valueOf(getClass().getName())))
				.append("writeInputStreamToSDCard(String dir, String fileName, InputStream inputstream) Access")
				.toString());
		createDir(s);
		file = createFileInSdcard(s, s1);
		fileoutputstream = new FileOutputStream(file);
		abyte0 = new byte[4096];
		int i;
		while ((i = inputstream.read(abyte0)) != -1)
		{
			fileoutputstream.write(abyte0, 0, i);
		}
		fileoutputstream.flush();
		try
		{
			fileoutputstream.close();
		} catch (Exception exception2)
		{
			logger.e(exception2.toString());
		}
		return file;
	}
}