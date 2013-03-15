package org.ming.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class ImageUtils
{
	public static Bitmap addImageShadow(Bitmap paramBitmap)
	{
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		Matrix localMatrix = new Matrix();
		localMatrix.preScale(1.0F, -1.0F);
		Bitmap localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, 0, i, j,
				localMatrix, false);
		Bitmap localBitmap2 = Bitmap.createBitmap(i, j + j / 4,
				Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap2);
		localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, null);
		Paint localPaint1 = new Paint();
		localCanvas.drawRect(0.0F, j, i, j, localPaint1);
		localCanvas.drawBitmap(localBitmap1, 0.0F, j, null);
		Paint localPaint2 = new Paint();
		localPaint2.setShader(new LinearGradient(0.0F, paramBitmap.getHeight(),
				0.0F, localBitmap2.getHeight(), -1, 65535,
				Shader.TileMode.CLAMP));
		localPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		localCanvas.drawRect(0.0F, j, i, localBitmap2.getHeight(), localPaint2);
		return localBitmap2;
	}

	public static Bitmap addWhiteRim(Context paramContext, Bitmap paramBitmap)
	{
		Bitmap localBitmap1 = changeImage(paramContext, -1, paramBitmap, 341,
				428, false);
		Bitmap localBitmap2 = Bitmap.createBitmap(351, 438,
				Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap2);
		Paint localPaint = new Paint();
		RectF localRectF = new RectF(0.0F, 0.0F, 351.0F, 438.0F);
		localPaint.setColor(-1);
		localCanvas.drawRoundRect(localRectF, 5.0F, 5.0F, localPaint);
		localCanvas.drawBitmap(localBitmap1, 5.0F, 5.0F, localPaint);
		return localBitmap2;
	}

	public static Bitmap changeImage(Context context, int i, Bitmap bitmap,
			int j, int k, boolean flag)
	{
		Bitmap bitmap1;
		int l;
		int i1;
		float f;
		float f1;
		Matrix matrix;
		Bitmap bitmap2;
		if (bitmap == null)
			bitmap1 = BitmapFactory.decodeResource(context.getResources(), i);
		else
			bitmap1 = bitmap;
		l = bitmap1.getWidth();
		i1 = bitmap1.getHeight();
		f = (float) j / (float) l;
		f1 = (float) k / (float) i1;
		matrix = new Matrix();
		matrix.postScale(f, f1);
		if (flag)
			bitmap2 = addImageShadow(Bitmap.createBitmap(bitmap1, 0, 0, l, i1,
					matrix, true));
		else
			bitmap2 = Bitmap.createBitmap(bitmap1, 0, 0, l, i1, matrix, true);
		return bitmap2;
	}

	public static int computeInitialSampleSize(
			android.graphics.BitmapFactory.Options options, int i, int j)
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
			l = 64;
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

	public static int computeSampleSize(BitmapFactory.Options paramOptions,
			int paramInt1, int paramInt2)
	{
		int i = computeInitialSampleSize(paramOptions, paramInt1, paramInt2);
		int j;
		if (i <= 8)
		{
			j = 1;
			while (j < i)
			{
				j <<= 1;
			}
		} else
		{
			j = 8 * ((i + 7) / 8);
		}
		return j;
	}

	public static Bitmap createReflectionImageWithOrigin(Bitmap paramBitmap)
	{
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		Matrix localMatrix = new Matrix();
		localMatrix.preScale(1.0F, -1.0F);
		Bitmap localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, j / 2, i,
				j / 2, localMatrix, false);
		Bitmap localBitmap2 = Bitmap.createBitmap(i, j + j / 2,
				Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap2);
		localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, null);
		Paint localPaint1 = new Paint();
		localCanvas.drawRect(0.0F, j, i, j + 4, localPaint1);
		localCanvas.drawBitmap(localBitmap1, 0.0F, j + 4, null);
		Paint localPaint2 = new Paint();
		localPaint2.setShader(new LinearGradient(0.0F, paramBitmap.getHeight(),
				0.0F, 4 + localBitmap2.getHeight(), 1895825407, 16777215,
				Shader.TileMode.CLAMP));
		localPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		localCanvas.drawRect(0.0F, j, i, 4 + localBitmap2.getHeight(),
				localPaint2);
		return localBitmap2;
	}

	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		int i = drawable.getIntrinsicWidth();
		int j = drawable.getIntrinsicHeight();
		android.graphics.Bitmap.Config config;
		Bitmap bitmap;
		Canvas canvas;
		if (drawable.getOpacity() != -1)
			config = android.graphics.Bitmap.Config.ARGB_8888;
		else
			config = android.graphics.Bitmap.Config.RGB_565;
		bitmap = Bitmap.createBitmap(i, j, config);
		canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, i, j);
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap paramBitmap,
			float paramFloat)
	{
		Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(),
				paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap);
		Paint localPaint = new Paint();
		Rect localRect = new Rect(0, 0, paramBitmap.getWidth(),
				paramBitmap.getHeight());
		RectF localRectF = new RectF(localRect);
		localPaint.setAntiAlias(true);
		localCanvas.drawARGB(0, 0, 0, 0);
		localPaint.setColor(-12434878);
		localCanvas.drawRoundRect(localRectF, paramFloat, paramFloat,
				localPaint);
		localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		localCanvas.drawBitmap(paramBitmap, localRect, localRect, localPaint);
		return localBitmap;
	}

	public static Bitmap zoomBitmap(Bitmap paramBitmap, int paramInt1,
			int paramInt2)
	{
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		Matrix localMatrix = new Matrix();
		localMatrix.postScale(paramInt1 / i, paramInt2 / j);
		return Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);
	}
}