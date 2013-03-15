package org.ming.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ming.R;
import org.ming.center.cachedata.CacheDataManager;
import org.ming.center.download.UrlImageDownloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class AdView extends View implements GestureDetector.OnGestureListener
{
	public static final int CHECK_DRAW_BY_PC = 9;
	public static final int DRAW_AUTO_MOVE = 5;
	public static final int DRAW_AUTO_MOVE_NEXT = 6;
	public static final int DRAW_AUTO_MOVE_PREVIOUS = 7;
	public static final int DRAW_BY_PC = 8;
	public static final int DRAW_FINGER_MOVE = 1;
	public static final int DRAW_FINGER_MOVE_NEXT = 2;
	public static final int DRAW_FINGER_MOVE_PREVIOUS = 3;
	public static final int DRAW_MOVE_RESTORE = 4;
	public static final int STATUS_DRAW = 0;
	public static final int STATUS_PAUSE = 1;
	boolean ALLOW_DRAW = true;
	boolean ALLOW_LISTEN = true;
	boolean INIT = false;
	int[] allMsg = { 1, 4, 5, 8, 9 };
	Map<Integer, Boolean> bmpDownLoadMap = new HashMap();
	List<Bitmap> bmpList = new ArrayList();
	List<String> bmpPath = new ArrayList();
	int currentIndex = 0;
	GestureDetector detector;
	float distanceX = 0.0F;
	List<Bitmap> dotBmpList = new ArrayList(2);
	int draw_type = 0;
	Handler handler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{
			AdView.this.removeMessage(AdView.this.allMsg);
			switch (paramAnonymousMessage.what)
			{
			case 2:
				return;
			case 3:
				return;
			case 6:
				return;
			case 7:
				return;
			default:
				return;
			case 1:
				AdView.this.draw_type = 1;
				AdView localAdView = AdView.this;
				localAdView.distanceX += ((Float) paramAnonymousMessage.obj)
						.floatValue();
				if (AdView.this.distanceX > 0.0F)
					AdView.this.move_type = 3;
				else
					AdView.this.move_type = 2;
				AdView.this.invalidate();
				break;
			case 4:
				ALLOW_DRAW = false;
				AdView.this.draw_type = 4;
				AdView.this.invalidate();
				break;
			case 5:
				AdView.this.draw_type = 5;
				AdView.this.invalidate();
				break;
			case 8:
				AdView.this.draw_type = 8;
				AdView.this.handler.removeMessages(8);
				AdView.this.invalidate();
				break;
			case 9:
				if (AdView.this.bmpDownLoadMap.size() < AdView.this.mTotalPage)
					AdView.this.checkImage();
				AdView.this.draw_type = 0;
				AdView.this.handler.removeMessages(9);
				if ((AdView.this.ALLOW_DRAW)
						&& (AdView.this.touchEventType == 0))
				{
					AdView.this.stepByPc = 0;
					AdView.this.handler.removeMessages(8);
					AdView.this.handler.sendEmptyMessageDelayed(8, 3000L);
				} else
				{
					AdView.this.handler.sendEmptyMessageDelayed(9, 3000L);
				}
			}
		}
	};
	int height;
	int imgHeight;
	Map imgviewMap;
	private UrlImageDownloader mImageDownloader;
	int mTotalPage;
	int move_type;
	Paint paint;
	public int status;
	int stepByPc;
	int touchEventType;
	int width;

	public AdView(Context context)
	{
		super(context);
		status = 0;
		imgHeight = 300;
		bmpList = new ArrayList();
		bmpPath = new ArrayList();
		bmpDownLoadMap = new HashMap();
		imgviewMap = new HashMap();
		draw_type = 0;
		move_type = 0;
		INIT = false;
		touchEventType = 0;
		ALLOW_DRAW = true;
		ALLOW_LISTEN = true;
		distanceX = 0.0F;
		stepByPc = 0;
		currentIndex = 0;
		paint = new Paint(3);
		dotBmpList = new ArrayList(2);
		init();
	}

	public AdView(Context context, AttributeSet attributeset)
	{
		super(context, attributeset);
		status = 0;
		imgHeight = 300;
		bmpList = new ArrayList();
		bmpPath = new ArrayList();
		bmpDownLoadMap = new HashMap();
		imgviewMap = new HashMap();
		draw_type = 0;
		move_type = 0;
		INIT = false;
		touchEventType = 0;
		ALLOW_DRAW = true;
		ALLOW_LISTEN = true;
		distanceX = 0.0F;
		stepByPc = 0;
		currentIndex = 0;
		paint = new Paint(3);
		dotBmpList = new ArrayList(2);
		init();
	}

	public AdView(Context context, AttributeSet attributeset, int i)
	{
		super(context, attributeset, i);
		status = 0;
		imgHeight = 300;
		bmpList = new ArrayList();
		bmpPath = new ArrayList();
		bmpDownLoadMap = new HashMap();
		imgviewMap = new HashMap();
		draw_type = 0;
		move_type = 0;
		INIT = false;
		touchEventType = 0;
		ALLOW_DRAW = true;
		ALLOW_LISTEN = true;
		distanceX = 0.0F;
		stepByPc = 0;
		currentIndex = 0;
		paint = new Paint(3);
		dotBmpList = new ArrayList(2);
		init();
	}

	private void drawDot(Canvas canvas)
	{
		Rect rect = new Rect();
		getDrawingRect(rect);
		int i = (int) getContext().getResources().getDimension(0x7f08000a);
		int j = (int) getContext().getResources().getDimension(0x7f08000b);
		int k = (rect.width() - (i * mTotalPage + 15 * (-1 + mTotalPage))) / 2;
		int l = rect.height() - j - rect.height() / 20;
		int i1 = 0;
		do
		{
			if (i1 >= mTotalPage)
				return;
			Bitmap bitmap;
			Rect rect1;
			if (i1 == currentIndex)
				bitmap = BitmapFactory.decodeResource(getResources(),
						0x7f0200c2);
			else
				bitmap = BitmapFactory.decodeResource(getResources(),
						0x7f0200c3);
			rect1 = new Rect();
			rect1.left = k;
			rect1.top = l;
			rect1.right = k + i;
			rect1.bottom = l + j;
			canvas.drawBitmap(bitmap, null, rect1, paint);
			k += i + 15;
			i1++;
		} while (true);
	}

	public void addBmpByKey(String paramString1, String paramString2)
	{
		if (!this.imgviewMap.containsKey(paramString1))
		{
			ImageView localImageView = new ImageView(getContext());
			this.mImageDownloader.download(paramString1, R.drawable.ad_banner,
					localImageView, paramString2);
			this.imgviewMap.put(paramString1, localImageView);
		}
		this.bmpPath.add(paramString1);
	}

	public void checkImage()
	{
		int i = 0;
		do
		{
			if (i >= bmpPath.size())
				return;
			String s = (String) bmpPath.get(i);
			int j = s.indexOf("//");
			CacheDataManager cachedatamanager = CacheDataManager.getInstance();
			if (j != -1)
			{
				String s1 = s.substring(j + 2);
				int k = s1.lastIndexOf("/");
				if (k != -1 && k < s1.length()
						&& bmpDownLoadMap.get(Integer.valueOf(i)) == null)
				{
					String s2 = cachedatamanager.getCacheData(s1.substring(k));
					Rect rect = new Rect();
					getDrawingRect(rect);
					int l = rect.width();
					int i1 = rect.height();
					Matrix matrix = new Matrix();
					try
					{
						Bitmap bitmap = BitmapFactory.decodeFile(s2);
						if (bitmap != null)
						{
							matrix.setScale(
									(float) l / (float) bitmap.getWidth(),
									(float) i1 / (float) bitmap.getHeight());
							Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0,
									bitmap.getWidth(), bitmap.getHeight(),
									matrix, false);
							if (bmpList.size() > 0 && i < bmpList.size())
								bmpList.remove(i);
							bmpList.add(i, bitmap1);
							bmpDownLoadMap.put(Integer.valueOf(i),
									Boolean.valueOf(true));
							imgviewMap.remove(s);
						}
					} catch (Exception exception)
					{
						exception.printStackTrace();
					}
				}
			}
			i++;
		} while (true);
	}

	public void drawImg(Canvas paramCanvas, Bitmap paramBitmap, int paramInt1,
			int paramInt2, int paramInt3, int paramInt4)
	{
		Rect localRect1 = new Rect();
		Rect localRect2 = new Rect();
		localRect1.left = paramInt1;
		localRect1.top = 0;
		localRect1.right = paramInt2;
		localRect1.bottom = this.imgHeight;
		localRect2.left = paramInt3;
		localRect2.top = 0;
		localRect2.right = paramInt4;
		localRect2.bottom = this.imgHeight;
		paramCanvas.drawBitmap(paramBitmap, localRect1, localRect2, null);
	}

	public int getCurrentIndex()
	{
		return this.currentIndex;
	}

	public int getStatus()
	{
		return this.status;
	}

	void init()
	{
		this.mImageDownloader = new UrlImageDownloader(getContext());
		this.detector = new GestureDetector(this);
	}

	void initAdItem(int i)
	{
		Rect rect = new Rect();
		getDrawingRect(rect);
		int j = rect.width();
		int k = rect.height();
		Matrix matrix = new Matrix();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ad_banner);
		matrix.setScale((float) j / (float) bitmap.getWidth(), (float) k
				/ (float) bitmap.getHeight());
		Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, false);
		int l = 0;
		do
		{
			if (l >= mTotalPage)
			{
				Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),
						R.drawable.dot_selected);
				dotBmpList.add(bitmap2);
				Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(),
						R.drawable.dot_unselected);
				dotBmpList.add(bitmap3);
				return;
			}
			bmpList.add(bitmap1);
			l++;
		} while (true);
	}

	public boolean onDown(MotionEvent paramMotionEvent)
	{
		return false;
	}

	protected void onDraw(Canvas paramCanvas)
	{
		if (this.draw_type == 1)
		{
			if (this.move_type == 3)
			{
				int i5 = -1 + this.currentIndex;
				if (i5 < 0)
					i5 = -1 + this.bmpList.size();
				drawImg(paramCanvas,
						(Bitmap) this.bmpList.get(this.currentIndex), 0,
						this.width - (int) this.distanceX,
						(int) this.distanceX, this.width);
				drawImg(paramCanvas, (Bitmap) this.bmpList.get(i5), this.width
						- (int) this.distanceX, this.width, 0,
						(int) this.distanceX);
			} else
			{
				int i3 = 1 + this.currentIndex;
				if (i3 >= this.bmpList.size())
					i3 = 0;
				int i4 = -(int) this.distanceX;
				drawImg(paramCanvas,
						(Bitmap) this.bmpList.get(this.currentIndex), i4,
						this.width, 0, this.width - i4);
				drawImg(paramCanvas, (Bitmap) this.bmpList.get(i3), 0, i4,
						this.width - i4, this.width);
			}
		} else
		{
			if (this.draw_type == 5)
			{
				if (Math.abs(this.distanceX) < -30 + this.width)
				{
					if (this.move_type == 3)
					{
						this.distanceX = (30.0F + this.distanceX);
						int i2 = 1 + this.currentIndex;
						if (i2 >= this.bmpList.size())
							i2 = 0;
						drawImg(paramCanvas, (Bitmap) this.bmpList.get(i2), 0,
								this.width - (int) this.distanceX,
								(int) this.distanceX, this.width);
						drawImg(paramCanvas,
								(Bitmap) this.bmpList.get(this.currentIndex),
								this.width - (int) this.distanceX, this.width,
								0, (int) this.distanceX);
					} else
					{
						this.distanceX -= 30.0F;
						int n = (int) -this.distanceX;
						int i1 = -1 + this.currentIndex;
						if (i1 < 0)
							i1 = -1 + this.bmpList.size();
						drawImg(paramCanvas, (Bitmap) this.bmpList.get(i1), n,
								this.width, 0, this.width - n);
						drawImg(paramCanvas,
								(Bitmap) this.bmpList.get(this.currentIndex),
								0, n, this.width - n, this.width);
					}
					this.handler.sendEmptyMessageDelayed(5, 10L);
				} else
				{
					this.distanceX = 0.0F;
					drawImg(paramCanvas,
							(Bitmap) this.bmpList.get(this.currentIndex), 0,
							this.width, 0, this.width);
					this.draw_type = 0;
					this.ALLOW_DRAW = true;
					if (this.status == 0)
						this.handler.sendEmptyMessageDelayed(9, 3000L);
				}
			} else
			{
				if (this.draw_type == 4)
				{
					if ((this.move_type == 3) && (this.distanceX > 0.0F))
					{
						int m = -1 + this.currentIndex;
						if (m < 0)
							m = -1 + this.bmpList.size();
						drawImg(paramCanvas,
								(Bitmap) this.bmpList.get(this.currentIndex),
								0, this.width - (int) this.distanceX,
								(int) this.distanceX, this.width);
						drawImg(paramCanvas, (Bitmap) this.bmpList.get(m),
								this.width - (int) this.distanceX, this.width,
								0, (int) this.distanceX);
						this.distanceX -= 30.0F;
					} else
					{
						this.distanceX = (30.0F + this.distanceX);
						int j = 1 + this.currentIndex;
						if (j >= this.bmpList.size())
							j = 0;
						int k = -(int) this.distanceX;
						drawImg(paramCanvas,
								(Bitmap) this.bmpList.get(this.currentIndex),
								k, this.width, 0, this.width - k);
						drawImg(paramCanvas, (Bitmap) this.bmpList.get(j), 0,
								k, this.width - k, this.width);
					}
					if (Math.abs(this.distanceX) > 30.0F)
					{
						if (Math.abs(this.distanceX) > this.width)
						{
							this.distanceX = 0.0F;
							this.stepByPc = 0;
							this.ALLOW_DRAW = true;
							drawImg(paramCanvas,
									(Bitmap) this.bmpList
											.get(this.currentIndex), 0,
									this.width, 0, this.width);
						} else
						{
							this.handler.sendEmptyMessageDelayed(4, 10L);
						}
					} else
					{
						this.distanceX = 0.0F;
						this.stepByPc = 0;
						drawImg(paramCanvas,
								(Bitmap) this.bmpList.get(this.currentIndex),
								0, this.width, 0, this.width);
						this.ALLOW_DRAW = true;
						if (this.status == 0)
							this.handler.sendEmptyMessageDelayed(8, 3000L);
					}
				} else
				{
					if (this.draw_type == 8)
					{
						int i = 1 + this.currentIndex;
						if (i >= this.bmpList.size())
							i = 0;
						if (-1 + this.currentIndex < 0)
							// -1 + this.bmpList.size();
							i = -1 + this.bmpList.size();
						if (this.stepByPc < -120 + this.width)
						{
							this.stepByPc = (120 + this.stepByPc);
							drawImg(paramCanvas,
									(Bitmap) this.bmpList
											.get(this.currentIndex),
									this.stepByPc, this.width, 0, this.width
											- this.stepByPc);
							drawImg(paramCanvas, (Bitmap) this.bmpList.get(i),
									0, this.stepByPc, this.width
											- this.stepByPc, this.width);
							this.handler.sendEmptyMessageDelayed(8, 30L);
						} else
						{
							this.currentIndex = i;
							this.stepByPc = 0;
							drawImg(paramCanvas,
									(Bitmap) this.bmpList
											.get(this.currentIndex), 0,
									this.width, 0, this.width);
							this.handler.sendEmptyMessage(9);
						}
						this.distanceX = this.stepByPc;
					} else
					{
						if (this.bmpList.size() > this.currentIndex)
							drawImg(paramCanvas,
									(Bitmap) this.bmpList
											.get(this.currentIndex), 0,
									this.width, 0, this.width);
					}
				}
			}
		}
		drawDot(paramCanvas);
		super.onDraw(paramCanvas);
	}

	public boolean onFling(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
	{
		return false;
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4)
	{
		this.width = getWidth();
		this.height = getHeight();
		if (!this.INIT)
		{
			initAdItem(this.mTotalPage);
			this.INIT = true;
		}
		super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
	}

	public void onLongPress(MotionEvent paramMotionEvent)
	{}

	public boolean onScroll(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
	{
		if ((this.ALLOW_LISTEN) && (this.ALLOW_DRAW))
			this.handler.sendMessage(this.handler.obtainMessage(1,
					Float.valueOf(-paramFloat1)));
		return false;
	}

	public void onShowPress(MotionEvent paramMotionEvent)
	{}

	public boolean onSingleTapUp(MotionEvent paramMotionEvent)
	{
		return false;
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent)
	{
		if (paramMotionEvent.getAction() == 0)
		{
			this.ALLOW_LISTEN = true;
			this.handler.removeMessages(8);
			if (this.ALLOW_DRAW)
			{
				this.move_type = 3;
				this.handler.sendEmptyMessage(4);
			}
			this.touchEventType = 1;
		}
		boolean bool1;
		if ((paramMotionEvent.getAction() == 1)
				|| (paramMotionEvent.getAction() == 3))
		{
			if (this.draw_type == 1)
			{
				this.draw_type = 4;
				removeMessage(this.allMsg);
				if (Math.abs(this.distanceX) > this.width / 4)
				{
					if (this.move_type == 3)
					{
						int j = -1 + this.currentIndex;
						if (j < 0)
							j = -1 + this.bmpList.size();
						this.currentIndex = j;
					} else
					{
						if (this.move_type != 2)
						{
							int i = 1 + this.currentIndex;
							if (i >= this.bmpList.size())
								i = 0;
							this.currentIndex = i;
						}
					}
					this.ALLOW_LISTEN = false;
					this.ALLOW_DRAW = false;
					this.handler.sendEmptyMessage(5);
				} else
				{
					this.handler.sendEmptyMessage(4);
				}
			} else
			{
				this.handler.sendEmptyMessageDelayed(9, 3000L);
			}
			this.touchEventType = 0;
			bool1 = true;
		} else
		{
			boolean bool2 = this.ALLOW_LISTEN;
			bool1 = false;
			if (bool2)
			{
				boolean bool3 = this.ALLOW_DRAW;
				bool1 = false;
				if (bool3)
					bool1 = this.detector.onTouchEvent(paramMotionEvent);
			}
		}
		return bool1;
	}

	public void pause()
	{
		this.status = 1;
		this.draw_type = 0;
		removeMessage(this.allMsg);
	}

	public void releaseResource()
	{
		this.bmpList.clear();
		this.handler.removeMessages(8);
		this.dotBmpList.clear();
		this.imgviewMap.clear();
	}

	public void removeMessage(int ai[])
	{
		int i = ai.length;
		int j = 0;
		do
		{
			if (j >= i)
				return;
			int k = ai[j];
			handler.removeMessages(k);
			j++;
		} while (true);
	}

	public void resume()
	{
		this.status = 0;
		this.draw_type = 0;
		invalidate();
		this.ALLOW_DRAW = true;
		this.handler.sendEmptyMessageDelayed(9, 1000L);
	}

	public void setCurrentIndex(int paramInt)
	{
		this.currentIndex = paramInt;
	}

	public void setStatus(int paramInt)
	{
		this.status = paramInt;
	}

	public void setTotalPage(int paramInt)
	{
		this.mTotalPage = paramInt;
	}
}