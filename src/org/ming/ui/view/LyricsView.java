package org.ming.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ming.center.lyric.Lyric;
import org.ming.util.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class LyricsView extends View
{

	public static final int LAUNCHED_BY_DEFAULT = 0;
	public static final int LAUNCHED_BY_WIDGET_3 = 1;
	private static final int LINE_SPACING = 2;
	private static final int LYRICS_VIEW_WIDTH = 300;
	private static final int SCROLL_DURATION = 500;
	private static final int SCROLL_INTERVAL = 50;
	private int mAnimationDistance;
	private long mAnimationStartTime;
	private int mBreakedLineCounts[];
	private Map mBreakedLines;
	private int mCurrentLine;
	private List mDestLines;
	private int mFocusLineColor;
	private int mInitializeType;
	private int mLineCount;
	private int mLineHeight;
	private Lyric mLyric;
	private boolean mNeedUpdateMetrics;
	private String mNoLyricsHint;
	private int mOtherLineColor;
	private TextPaint mPaint;
	private boolean mScrolling;
	private int mVerticalMargin;
	private int mVisibleLineCount;

	public LyricsView(Context context)
	{
		this(context, ((AttributeSet) (null)));
		init();
	}

	public LyricsView(Context context, int i)
	{
		this(context, ((AttributeSet) (null)));
		mInitializeType = i;
		init();
	}

	public LyricsView(Context context, AttributeSet attributeset)
	{
		this(context, attributeset, 0);
	}

	public LyricsView(Context context, AttributeSet attributeset, int i)
	{
		super(context, attributeset, i);
		mInitializeType = 0;
		init();
	}

	private void breakLyricsLine(int i)
	{
		int j = getWidth() - getPaddingLeft() - getPaddingRight();
		String s = mLyric.getLineByIndex(i);
		if (j == 0)
			j = 300;
		String as[] = Util.breakLine(s, mPaint, j);
		mBreakedLineCounts[i] = as.length;
		mBreakedLines.put(Integer.valueOf(i), as);
	}

	private void calculateMetrics()
	{
		int i = getHeight() - getPaddingBottom() - getPaddingTop();
		android.graphics.Paint.FontMetrics fontmetrics = mPaint
				.getFontMetrics();
		mLineHeight = (int) (fontmetrics.descent - fontmetrics.ascent);
		mVisibleLineCount = (i + 2) / (2 + mLineHeight);
		mVerticalMargin = ((i + 2) % (2 + mLineHeight)) / 2;
		mNeedUpdateMetrics = false;
	}

	private int drawLine(Canvas canvas, int i, int j, int k)
	{
		int l = getWidth() - getPaddingLeft() - getPaddingRight();
		String as[] = (String[]) mBreakedLines.get(Integer.valueOf(i));
		int i1 = as.length;
		int j1 = 0;
		do
		{
			if (j1 >= i1)
				return j;
			String s = as[j1];
			canvas.drawText(s, (l - (int) mPaint.measureText(s)) / 2, j - k,
					mPaint);
			j += 2 + mLineHeight;
			j1++;
		} while (true);
	}

	private void init()
	{
		mBreakedLines = new HashMap();
		mDestLines = new ArrayList();
		mNeedUpdateMetrics = true;
		mFocusLineColor = getResources().getColor(0x7f060001);
		mOtherLineColor = getResources().getColor(0x7f060002);
		mPaint = new TextPaint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(getResources().getDimension(0x7f080000));
	}

	public int getFocusLineColor()
	{
		return mFocusLineColor;
	}

	public int getNextTime()
	{
		int i;
		if (mLyric == null)
			i = -1;
		else
			i = mLyric.getNextTime();
		return i;
	}

	public String getNoLyricsHint()
	{
		return mNoLyricsHint;
	}

	public int getOtherLineColor()
	{
		return mOtherLineColor;
	}

	public boolean hasLyrics()
	{
		boolean flag;
		if (mLyric != null)
			flag = true;
		else
			flag = false;
		return flag;
	}

	protected void onDraw(Canvas canvas)
	{
		// int i;
		// int j;
		// android.graphics.Paint.FontMetrics fontmetrics;
		// super.onDraw(canvas);
		// i = getWidth();
		// j = getHeight();
		// canvas.save();
		// canvas.clipRect(getPaddingLeft(), getPaddingTop(), i -
		// getPaddingRight(), j - getPaddingBottom());
		// if (mInitializeType == 1)
		// canvas.scale(-1F, 1.0F, getWidth() / 2, getHeight() / 2);
		// fontmetrics = mPaint.getFontMetrics();
		// if (mNeedUpdateMetrics)
		// calculateMetrics();
		// if (mLyric != null) goto _L2; else goto _L1
		// _L1:
		// if (TextUtils.isEmpty(mNoLyricsHint)) goto _L4; else goto _L3
		// _L3:
		// String as[];
		// int j4;
		// int k4;
		// int l4;
		// mPaint.setColor(mOtherLineColor);
		// mPaint.setAlpha(255);
		// as = Util.breakLine(mNoLyricsHint, mPaint, i - getPaddingLeft() -
		// getPaddingRight());
		// j4 = (int)((float)((j - mLineHeight * as.length - 2 * (-1 +
		// as.length)) / 2) - fontmetrics.ascent);
		// k4 = as.length;
		// l4 = 0;
		// _L6:
		// if (l4 < k4) goto _L5; else goto _L4
		// _L4:
		// return;
		// _L5:
		// String s = as[l4];
		// canvas.drawText(s, Math.max(0, (i - (int)mPaint.measureText(s)) / 2),
		// j4, mPaint);
		// j4 += 2 + mLineHeight;
		// l4++;
		// goto _L6
		// _L2:
		// int k = j - getPaddingBottom();
		// if (!mScrolling) goto _L8; else goto _L7
		// _L7:
		// int i2;
		// int j2;
		// int k2;
		// int l2;
		// int i3;
		// long l1 = System.currentTimeMillis();
		// i2 = Math.min((mAnimationDistance * (int)(l1 - mAnimationStartTime))
		// / 500, mAnimationDistance);
		// j2 = (i2 * 255) / mAnimationDistance;
		// k2 = (mVerticalMargin - (int)fontmetrics.ascent) + getPaddingTop();
		// l2 = mCurrentLine;
		// i3 = mVisibleLineCount / 2;
		// _L15:
		// if (i3 > 0) goto _L10; else goto _L9
		// _L9:
		// int j3;
		// if (i3 < 0)
		// k2 -= (2 + mLineHeight) * Math.abs(i3);
		// j3 = l2;
		// _L16:
		// if (j3 < mLineCount && k2 < k) goto _L12; else goto _L11
		// _L11:
		// if (i2 >= mAnimationDistance) goto _L14; else goto _L13
		// _L13:
		// postInvalidateDelayed(50L);
		// _L21:
		// canvas.restore();
		// goto _L4
		// _L10:
		// if (--l2 >= 0)
		// i3 -= mBreakedLineCounts[l2];
		// else
		// i3--;
		// goto _L15
		// _L12:
		// if (j3 < 0)
		// {
		// k2 += 2 + mLineHeight;
		// } else
		// {
		// if (j3 == mCurrentLine)
		// mPaint.setColor(mFocusLineColor);
		// else
		// mPaint.setColor(mOtherLineColor);
		// if (j3 == mCurrentLine)
		// mPaint.setAlpha(255 - j2);
		// else
		// mPaint.setAlpha(255);
		// if (mBreakedLineCounts[j3] == 0)
		// breakLyricsLine(j3);
		// k2 = drawLine(canvas, j3, k2, i2);
		// }
		// j3++;
		// goto _L16
		// _L14:
		// List list = mDestLines;
		// list;
		// JVM INSTR monitorenter ;
		// mCurrentLine = ((Integer)mDestLines.remove(0)).intValue();
		// _L23:
		// if (!mDestLines.isEmpty()) goto _L18; else goto _L17
		// _L17:
		// int l;
		// int i1;
		// int j1;
		// int k1;
		// Exception exception;
		// boolean flag;
		// int k3;
		// int l3;
		// int i4;
		// if (mDestLines.isEmpty())
		// flag = false;
		// else
		// flag = true;
		// if (!flag)
		// break MISSING_BLOCK_LABEL_780;
		// k3 = ((Integer)mDestLines.get(0)).intValue();
		// l3 = 0;
		// i4 = mCurrentLine;
		// _L24:
		// if (i4 < k3) goto _L20; else goto _L19
		// _L19:
		// mAnimationDistance = l3 * (2 + mLineHeight);
		// mScrolling = true;
		// mAnimationStartTime = System.currentTimeMillis();
		// _L25:
		// invalidate();
		// goto _L21
		// exception;
		// throw exception;
		// _L18:
		// if (((Integer)mDestLines.get(0)).intValue() > mCurrentLine) goto
		// _L17; else goto _L22
		// _L22:
		// mDestLines.remove(0);
		// goto _L23
		// _L20:
		// if (mBreakedLineCounts[i4] == 0)
		// breakLyricsLine(i4);
		// l3 += mBreakedLineCounts[i4];
		// i4++;
		// goto _L24
		// mScrolling = false;
		// goto _L25
		// _L8:
		// l = (mVerticalMargin - (int)fontmetrics.ascent) + getPaddingTop();
		// i1 = mCurrentLine;
		// j1 = mVisibleLineCount / 2;
		// _L26:
		// label0:
		// {
		// if (j1 > 0)
		// break label0;
		// if (j1 < 0)
		// l -= (2 + mLineHeight) * Math.abs(j1);
		// k1 = i1;
		// while (k1 < mLineCount && l < k)
		// {
		// if (k1 < 0)
		// {
		// l += 2 + mLineHeight;
		// } else
		// {
		// if (k1 == mCurrentLine)
		// mPaint.setColor(mFocusLineColor);
		// else
		// mPaint.setColor(mOtherLineColor);
		// if (mBreakedLineCounts[k1] == 0)
		// breakLyricsLine(k1);
		// l = drawLine(canvas, k1, l, 0);
		// }
		// k1++;
		// }
		// }
		// goto _L21
		// if (--i1 >= 0)
		// j1 -= mBreakedLineCounts[i1];
		// else
		// j1--;
		// goto _L26
	}

	public void reset()
	{
		synchronized (this.mDestLines)
		{
			this.mDestLines.clear();
			this.mScrolling = false;
			this.mCurrentLine = 0;
			invalidate();
			return;
		}
	}

	public void seekByTime(int i, boolean flag)
	{
		if (mLyric != null)
		{
			mLyric.seekByTime(i);
			setCurrentLine(mLyric.getCurrentIndex(), flag);
		}
	}

	public void setCurrentLine(int paramInt, boolean paramBoolean)
	{
		if ((paramInt > this.mCurrentLine) && (paramBoolean))
			while (true)
			{
				synchronized (this.mDestLines)
				{
					this.mDestLines.add(Integer.valueOf(paramInt));
					int i;
					int j;
					if (!this.mScrolling)
					{
						i = 0;
						j = this.mCurrentLine;
						if (j >= paramInt)
						{
							this.mAnimationDistance = (i * (2 + this.mLineHeight));
							this.mScrolling = true;
							this.mAnimationStartTime = System
									.currentTimeMillis();
						}
					} else
					{
						invalidate();
						return;
					}
					if (this.mBreakedLineCounts[j] == 0)
						breakLyricsLine(j);
					i += this.mBreakedLineCounts[j];
					j++;
				}
				this.mCurrentLine = paramInt;
			}
	}

	public void setFocusLineColor(int i)
	{
		mFocusLineColor = i;
	}

	public void setLyrics(String s)
	{
		if (s == null || s.equals("暂无歌词"))
		{
			mLyric = null;
			mLineCount = 0;
			mBreakedLineCounts = null;
		} else
		{
			mLyric = new Lyric(Util.getUTF8Bytes(s), "utf-8");
			mLineCount = mLyric.getLineCount();
			mBreakedLineCounts = new int[mLineCount];
		}
		mCurrentLine = 0;
		mBreakedLines.clear();
		invalidate();
	}

	public void setNoLyricsHint(String s)
	{
		mNoLyricsHint = s;
	}

	public void setOtherLineColor(int i)
	{
		mOtherLineColor = i;
	}

	public void setTextSize(float f)
	{
		mPaint.setTextSize(f);
		calculateMetrics();
		invalidate();
	}
}
