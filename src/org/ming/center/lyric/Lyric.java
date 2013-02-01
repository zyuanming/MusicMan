package org.ming.center.lyric;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

import org.ming.util.Util;

public class Lyric
{
	private class TimeTag
	{

		public String toString()
		{
			return (new StringBuilder("[")).append(time).append(":")
					.append(index).append("]").toString();
		}

		private int index;
		private int time;

		private TimeTag(int i, int j)
		{
			time = i;
			index = j;
		}

		TimeTag(int i, int j, TimeTag timetag)
		{
			this(i, j);
		}
	}

	public Lyric(byte abyte0[])
	{
		this(abyte0, null);
	}

	public Lyric(byte abyte0[], String s)
	{
		mCharset = "UTF-8";
		mCharset = s;
		mTags = new Vector();
		mLyricsMap = new Hashtable();
		parse(abyte0);
	}

	private TimeTag getTimeTag(byte abyte0[], int i, int j, int k, int l)
	{
		return null;
		// int i1;
		// TimeTag timetag;
		// i1 = Util.indexOf(abyte0, (byte)93, i, j);
		// timetag = null;
		// if(i1 == -1) goto _L2; else goto _L1
		// _L1:
		// timetag = null;
		// if(i1 < 6) goto _L2; else goto _L3
		// _L3:
		// timetag = null;
		// if(i1 <= 10) goto _L4; else goto _L2
		// _L2:
		// return timetag;
		// _L4:
		// int k1;
		// int l1;
		// int i2;
		// int j1 = Util.indexOf(abyte0, (byte)58, i, i1);
		// timetag = null;
		// if(j1 != 3)
		// continue; /* Loop/switch isn't completed */
		// boolean flag = Character.isDigit(abyte0[i + 1]);
		// timetag = null;
		// if(!flag)
		// continue; /* Loop/switch isn't completed */
		// boolean flag1 = Character.isDigit(abyte0[i + 2]);
		// timetag = null;
		// if(!flag1)
		// continue; /* Loop/switch isn't completed */
		// k1 = 60000 * (10 * (-48 + abyte0[i + 1]) + (-48 + abyte0[i + 2]));
		// l1 = Util.indexOf(abyte0, (byte)46, i, i1);
		// if(l1 != -1)
		// break; /* Loop/switch isn't completed */
		// timetag = null;
		// if(i1 != 6)
		// continue; /* Loop/switch isn't completed */
		// boolean flag2 = Character.isDigit(abyte0[i + 4]);
		// timetag = null;
		// if(!flag2)
		// continue; /* Loop/switch isn't completed */
		// boolean flag3 = Character.isDigit(abyte0[i + 5]);
		// timetag = null;
		// if(!flag3)
		// continue; /* Loop/switch isn't completed */
		// i2 = k1 + 1000 * (10 * (-48 + abyte0[i + 4]) + (-48 + abyte0[i +
		// 5]));
		// _L9:
		// timetag = new TimeTag(i2 - l, k, null);
		// if(true) goto _L2; else goto _L5
		// _L5:
		// timetag = null;
		// if(l1 != 6) goto _L2; else goto _L6
		// _L6:
		// boolean flag4;
		// flag4 = Character.isDigit(abyte0[i + 4]);
		// timetag = null;
		// if(!flag4) goto _L2; else goto _L7
		// _L7:
		// boolean flag5;
		// flag5 = Character.isDigit(abyte0[i + 5]);
		// timetag = null;
		// if(!flag5) goto _L2; else goto _L8
		// _L8:
		// i2 = k1 + 1000 * (10 * (-48 + abyte0[i + 4]) + (-48 + abyte0[i + 5]))
		// + Util.getInt(Util.getUTF8String(abyte0, l1 + 1, -1 + (i1 - l1)));
		// goto _L9
	}

	private void insertTimeTag(TimeTag timetag)
	{
		int i = 0;
		int j = 0;
		int k = mTags.size();
		do
		{
			if (k <= i)
			{
				mTags.insertElementAt(timetag, j);
				return;
			}
			j = i + k >> 1;
			if (timetag.time > ((TimeTag) mTags.elementAt(j)).time)
				i = ++j;
			else
				k = j;
		} while (true);
	}

	public static void main(String args[]) throws Exception
	{
		File file = new File("e:/media/mp3/xmn.lrc");
		if (file.exists())
		{
			InputStreamReader inputstreamreader;
			StringBuffer stringbuffer;
			inputstreamreader = new InputStreamReader(
					new FileInputStream(file), "UTF-8");
			stringbuffer = new StringBuffer();
			int i = inputstreamreader.read();
			if (i != -1)
			{
				stringbuffer.append((char) i);
			} else
			{
				System.out.println(stringbuffer.toString());
				Lyric lyric = new Lyric(stringbuffer.toString().getBytes(
						"UTF-8"));
				lyric.seekByTime(40960);
				System.out.println(lyric.getCurrentLine());
			}
		}
	}

	private void parse(byte abyte0[])
	{
		// int i;
		// int j;
		// int k;
		// int l;
		// int i1;
		// boolean flag;
		// if(mCharset == null)
		// mCharset = Util.detectEncoding(abyte0);
		// i = abyte0.length;
		// j = 0;
		// k = 0;
		// l = 0;
		// i1 = 0;
		// flag = false;
		// _L3:
		// do
		// {
		// label0:
		// {
		// if(j >= i)
		// return;
		// if(abyte0[j] == 91 || flag)
		// break label0;
		// j++;
		// k++;
		// }
		// } while(true);
		// if(abyte0[j] != 91 || flag) goto _L2; else goto _L1
		// _L1:
		// label1:
		// {
		// TimeTag timetag = getTimeTag(abyte0, j, i, l, i1);
		// if(timetag == null)
		// break label1;
		// flag = true;
		// while(timetag != null)
		// {
		// insertTimeTag(timetag);
		// int l1 = Util.indexOf(abyte0, (byte)93, j, i);
		// int i2;
		// if(l1 == -1)
		// i2 = 1;
		// else
		// i2 = l1 + 1;
		// j += i2;
		// k = j;
		// timetag = getTimeTag(abyte0, j, i, l, i1);
		// }
		// }
		// goto _L3
		// int k1;
		// if(abyte0[j + 1] != 111 || abyte0[j + 2] != 102 || abyte0[j + 3] !=
		// 102 || abyte0[j + 4] != 115 || abyte0[j + 5] != 101 || abyte0[j + 6]
		// != 116 || abyte0[j + 7] != 58)
		// break MISSING_BLOCK_LABEL_289;
		// k1 = j += 8;
		// _L4:
		// if(j < i && abyte0[j] != 91)
		// {
		// label2:
		// {
		// if(abyte0[j] != 93)
		// break label2;
		// i1 = Util.getInt(Util.getUTF8String(abyte0, k1, j - k1).trim());
		// }
		// }
		// k = j;
		// goto _L3
		// j++;
		// goto _L4
		// j++;
		// k++;
		// goto _L3
		// _L2:
		// if(!flag) goto _L3; else goto _L5
		// _L8:
		// int j1 = j - k;
		// if(mCharset != null) goto _L7; else goto _L6
		// _L6:
		// String s1 = "UTF-8";
		// _L9:
		// String s = new String(abyte0, k, j1, s1);
		// _L10:
		// mLyricsMap.put(Integer.valueOf(l), s);
		// l++;
		// k = ++j;
		// flag = false;
		// goto _L3
		// _L5:
		// while(j < i && abyte0[j] != 13 && abyte0[j] != 10)
		// j++;
		// goto _L8
		// _L7:
		// s1 = mCharset;
		// goto _L9
		// UnsupportedEncodingException unsupportedencodingexception;
		// unsupportedencodingexception;
		// s = (new
		// StringBuilder("UnsupportedEncodingException:")).append(unsupportedencodingexception.getMessage()).toString();
		// goto _L10
	}

	public int getCurrentIndex()
	{
		return mCurrentIndex;
	}

	public String getCurrentLine()
	{
		return getLineByIndex(mCurrentIndex);
	}

	public String getLineByIndex(int i)
	{
		String s;
		if (i < 0 || i >= mTags.size())
			s = null;
		else
			s = (String) mLyricsMap.get(Integer.valueOf(((TimeTag) mTags
					.elementAt(i)).index));
		return s;
	}

	public int getLineCount()
	{
		return mTags.size();
	}

	public String getNextLine()
	{
		return getLineByIndex(1 + mCurrentIndex);
	}

	public int getNextTime()
	{
		int i;
		if (mCurrentIndex < -1 + mTags.size())
			i = ((TimeTag) mTags.elementAt(1 + mCurrentIndex)).time;
		else
			i = -1;
		return i;
	}

	public String getPrevious()
	{
		return getLineByIndex(-1 + mCurrentIndex);
	}

	public int getRemainLine()
	{
		return mTags.size() - mCurrentIndex;
	}

	public void seekByTime(int i)
	{
		// if(mTags != null && mTags.size() >= 1) goto _L2; else goto _L1
		// _L1:
		// return;
		// _L2:
		// int j;
		// int k;
		// int l;
		// if(i <= 0)
		// {
		// mCurrentIndex = 0;
		// continue; /* Loop/switch isn't completed */
		// }
		// if(i >= ((TimeTag)mTags.elementAt(mCurrentIndex)).time && 1 +
		// mCurrentIndex < mTags.size() && i < ((TimeTag)mTags.elementAt(1 +
		// mCurrentIndex)).time)
		// continue; /* Loop/switch isn't completed */
		// if(2 + mCurrentIndex < mTags.size() && i >=
		// ((TimeTag)mTags.elementAt(1 + mCurrentIndex)).time && i <
		// ((TimeTag)mTags.elementAt(2 + mCurrentIndex)).time)
		// {
		// mCurrentIndex = 1 + mCurrentIndex;
		// continue; /* Loop/switch isn't completed */
		// }
		// if(2 + mCurrentIndex == mTags.size() && i >=
		// ((TimeTag)mTags.elementAt(1 + mCurrentIndex)).time)
		// {
		// mCurrentIndex = 1 + mCurrentIndex;
		// continue; /* Loop/switch isn't completed */
		// }
		// if(mCurrentIndex >= -1 + mTags.size() && i >=
		// ((TimeTag)mTags.elementAt(mCurrentIndex)).time)
		// continue; /* Loop/switch isn't completed */
		// j = 0;
		// k = 0;
		// l = -1 + mTags.size();
		// _L4:
		// label0:
		// {
		// if(l > j)
		// break label0;
		// if(i < ((TimeTag)mTags.elementAt(k)).time && k > 0 && k >
		// mCurrentIndex)
		// k--;
		// mCurrentIndex = k;
		// }
		// if(true) goto _L1; else goto _L3
		// _L3:
		// k = j + l >> 1;
		// if(i > ((TimeTag)mTags.elementAt(k)).time)
		// j = ++k;
		// else
		// l = k;
		// goto _L4
		// if(true) goto _L1; else goto _L5
		// _L5:
	}

	public String toString()
	{
		return (new StringBuilder(String.valueOf(mTags.toString())))
				.append("\n").append(mLyricsMap.toString()).toString();
	}

	private String mCharset;
	private int mCurrentIndex;
	private Hashtable mLyricsMap;
	private Vector mTags;
}
