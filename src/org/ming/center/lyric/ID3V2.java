package org.ming.center.lyric;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ID3V2
{
	private InputStream is;
	private int tagSize = -1;
	private Map<String, byte[]> tags = new HashMap();

	public ID3V2(InputStream paramInputStream) throws IOException,
			ParseException
	{
		this.is = paramInputStream;
		if (this.is == null)
			throw new NullPointerException("InputStream of mp3 can not be null");
		byte[] arrayOfByte1 = new byte[10];
		this.is.read(arrayOfByte1);
		if ((arrayOfByte1[0] != 73) || (arrayOfByte1[1] != 68)
				|| (arrayOfByte1[2] != 51))
			throw new ParseException("not invalid mp3 ID3 tag");
		this.tagSize = ((0xFF & arrayOfByte1[9])
				+ ((0xFF & arrayOfByte1[8]) << 7)
				+ ((0xFF & arrayOfByte1[7]) << 14) + ((0xFF & arrayOfByte1[6]) << 21));
		int j;
		for (int i = 10;; i = 10 + (i + j))
		{
			if (i >= this.tagSize)
			{
				is.close();
				return;
			}
			byte abyte1[];
			abyte1 = new byte[10];
			is.read(abyte1);
			if (abyte1[0] == 0)
			{
				is.close();
				return;
			} else
			{
				String s;
				s = "" + (char) abyte1[0] + (char) abyte1[1] + (char) abyte1[2]
						+ (char) abyte1[3];
				j = byteArrayToLong(abyte1, 4, 4);
				if (j > tagSize - i)
				{
					is.close();
					return;
				} else
				{
					byte abyte2[] = new byte[j];
					is.read(abyte2);
					tags.put(s, abyte2);
				}
			}
		}
	}

	public ID3V2(byte[] paramArrayOfByte) throws IOException, ParseException
	{
		this(new ByteArrayInputStream(paramArrayOfByte));
	}

	public static void main(String[] paramArrayOfString)
	{
		try
		{
			String str = new ID3V2(new FileInputStream(new File(
					"e:\\qunxing.mp3"))).uslt();
			if (str != null)
			{
				System.out.println(str);
				Lyric localLyric = new Lyric(str.getBytes("UTF-8"));
				localLyric.seekByTime(13231);
				System.out.println(localLyric.getCurrentLine());
				System.out.println(localLyric.getNextLine());
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

	public int byteArrayToLong(byte[] paramArrayOfByte, int paramInt1,
			int paramInt2)
	{
		int i = 0;
		for (int j = paramInt1;; j++)
		{
			if (j >= paramInt1 + paramInt2)
				return i;
			i = (int) (i + ((0xFF & paramArrayOfByte[j]) << 8 * (-1 + (paramInt1 + (paramInt2 - j)))));
		}
	}

	public String uslt()
	{
		byte abyte0[];
		String s = null;
		abyte0 = (byte[]) tags.get("USLT");
		if (abyte0 != null)
		{
			Parser parser = new Parser(abyte0, true);
			String s1;
			try
			{
				new String(parser.parseBinary(3), "ISO8859_1");
				parser.parseText();
				s1 = parser.parseText();
				s = s1;
			} catch (Exception e)
			{
				s = null;
			}
		}
		return s;
	}

	class Parser
	{
		public static final byte ISO = 0;
		public static final byte UNICODE = 1;
		private byte encoding;
		private byte[] in;
		private int pos;
		private int start;
		private int stop;

		public Parser(byte abyte0[], boolean flag)
		{
			this(abyte0, flag, 0, -1 + abyte0.length);
		}

		public Parser(byte abyte0[], boolean flag, int i, int j)
		{
			super();
			in = abyte0;
			start = i;
			pos = i;
			stop = j;
			if (flag)
				parseEncoding();
			else
				encoding = 0;
		}

		public int getPosition()
		{
			return this.pos;
		}

		public byte[] parseBinary() throws ParseException
		{
			return parseBinary(1 + (this.stop - this.pos));
		}

		public byte[] parseBinary(int i) throws ParseException
		{
			byte abyte0[];
			try
			{
				abyte0 = new byte[i];
				System.arraycopy(in, pos, abyte0, 0, i);
				pos = i + pos;
			} catch (Exception exception)
			{
				throw new ParseException("Parse binary failed");
			}
			return abyte0;
		}

		public void parseEncoding()
		{
			this.encoding = this.in[this.pos];
			this.pos = (1 + this.pos);
		}

		public String parseText() throws ParseException
		{
			return parseText(this.encoding);
		}

		public String parseText(byte byte0) throws ParseException
		{
			return null;
		}

		public void setPosition(int paramInt)
		{
			this.pos = paramInt;
		}
	}
}