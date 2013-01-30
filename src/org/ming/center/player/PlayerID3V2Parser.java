package org.ming.center.player;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.ming.util.MyLogger;

public class PlayerID3V2Parser
{

	public PlayerID3V2Parser()
	{}

	public static PlayerID3V2Parser getInstance()
	{
		if (instance == null)
			instance = new PlayerID3V2Parser();
		return instance;
	}

	private int parseFrameContentDescrip(int i) throws IOException
	{
		StringBuffer stringbuffer = new StringBuffer();
		char c = '\0';
		logger.d((new StringBuilder("--ID3parse:encodingByte:")).append(i)
				.toString());
		int j;
		if (i == 1)
		{
			int k = in.readUnsignedShort();
			logger.d((new StringBuilder("--ID3parse:bom:")).append(
					Integer.toHexString(k)).toString());
			j = 0 + 2;
			do
			{
				if (k == 65279)
				{
					c = (char) ((0xff & in.read()) << 8 | 0xff & in.read());
					j += 2;
				} else if (k == 65534)
				{
					c = (char) (0xff & in.read() | (0xff & in.read()) << 8);
					j += 2;
				}
				if (c != 0)
					stringbuffer.append(c);
			} while (c != 0);
		} else
		{
			j = 0;
			char c1;
			if (i == 0)
				do
				{
					c1 = (char) (0xff & in.read());
					j++;
					if (c1 != 0)
						stringbuffer.append(c1);
				} while (c1 != 0);
		}
		frameContentDescrip = stringbuffer.toString();
		logger.d((new StringBuilder("--ID3parse:desp:")).append(
				frameContentDescrip).toString());
		return j;
	}

	private void parseFrameTextContent(int i, int j) throws IOException,
			UnsupportedEncodingException
	{
		if (i <= 0)
		{
			logger.d((new StringBuilder("--ID3parse:frameTextContent:"))
					.append(frameTextContent).toString());
			return;
		} else
		{
			if (j != 1)
			{
				if (j == 0)
				{
					byte abyte2[] = new byte[i];
					in.read(abyte2);
					frameTextContent = new String(abyte2, "GBK");
				} else if (j == 2)
				{
					byte abyte1[] = new byte[i];
					in.read(abyte1);
					frameTextContent = parseUnicodeBE(abyte1);
				} else if (j == 3)
				{
					byte abyte0[] = new byte[i];
					in.read(abyte0);
					frameTextContent = new String(abyte0, "UTF-8");
				}
			} else
			{
				int k;
				byte abyte3[];
				k = in.readUnsignedShort();
				abyte3 = new byte[i - 2];
				in.read(abyte3);
				if (k != 65279)
				{
					if (k == 65534)
						frameTextContent = parseUnicodeLE(abyte3);
				} else
				{
					frameTextContent = parseUnicodeBE(abyte3);
				}
			}
		}
	}

	private static String parseISO8859(byte abyte0[])
	{
		char ac[] = new char[abyte0.length];
		int i = 0;
		do
		{
			if (i >= abyte0.length)
				return new String(ac);
			ac[i] = (char) abyte0[i];
			i++;
		} while (true);
	}

	private void parseTextFrame(boolean flag, boolean flag1)
			throws IOException, UnsupportedEncodingException
	{
		int i = frameLen;
		int j = in.read();
		int k = i - 1;
		if (flag)
		{
			in.skip(3L);
			k -= 3;
		}
		if (flag1)
			k -= parseFrameContentDescrip(j);
		parseFrameTextContent(k, j);
	}

	private static String parseUnicodeBE(byte abyte0[])
	{
		char ac[] = new char[abyte0.length / 2];
		int i = 0;
		do
		{
			if (i >= ac.length)
				return new String(ac);
			ac[i] = (char) (0xff & abyte0[1 + i * 2] | (0xff & abyte0[i * 2]) << 8);
			i++;
		} while (true);
	}

	private static String parseUnicodeLE(byte abyte0[])
	{
		char ac[] = new char[abyte0.length / 2];
		int i = 0;
		do
		{
			if (i >= ac.length)
				return new String(ac);
			ac[i] = (char) (0xff & abyte0[i * 2] | (0xff & abyte0[1 + i * 2]) << 8);
			i++;
		} while (true);
	}

	public byte[] getFrameBuf()
	{
		return frameBuf;
	}

	public String getFrameContentDescrip()
	{
		return frameContentDescrip;
	}

	public int getFrameFlag()
	{
		return frameFlag;
	}

	public int getFrameID()
	{
		return frameID;
	}

	public String getFrameTextContent()
	{
		return frameTextContent;
	}

	public boolean hasMoreFrames()
	{
		boolean flag;
		if (headerSize > 10)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public boolean isV23()
	{
		return isV23;
	}

	public void nextFrame()
	{
		this.frameID = 0;
		this.frameLen = 0;
		this.frameFlag = 0;
		this.frameBuf = null;
		this.frameContentDescrip = null;
		this.frameTextContent = null;
		if (hasMoreFrames())
			while (true)
			{
				try
				{
					this.frameID = this.in.readInt();
					this.frameLen = ((0xFF & this.in.read()) << 24
							| (0xFF & this.in.read()) << 16
							| (0xFF & this.in.read()) << 8 | 0xFF & this.in
							.read());
					this.frameFlag = ((0xFF & this.in.read()) << 8 | 0xFF & this.in
							.read());
					logger.d("--ID3parse:frameID:"
							+ Integer.toHexString(this.frameID));
					logger.d("--ID3parse:frameLen:" + this.frameLen);
					logger.d("--ID3parse:frameFlag:"
							+ Integer.toBinaryString(this.frameFlag));
					if (this.frameLen < 0)
					{
						logger.d("--ID3parse:frameLenError:" + this.frameLen
								+ "headerSize" + this.headerSize);
						this.frameLen = (-10 + this.headerSize);
						this.in.skip(this.frameLen);
						this.headerSize -= 10 + this.frameLen;
						break;
					}
					if (this.frameID == 1415075928)
					{
						parseTextFrame(false, true);
						continue;
					}
					if ((0xFF000000 & this.frameID) == 1409286144)
					{
						parseTextFrame(false, false);
					} else if (this.frameID == 1431522388)
					{
						parseTextFrame(true, true);
					} else if (this.frameID == 1129270605)
					{
						parseTextFrame(true, true);
					} else
					{
						this.frameBuf = new byte[this.frameLen];
						this.in.read(this.frameBuf);
					}
				} catch (IOException localIOException)
				{
					localIOException.printStackTrace();
				}

			}
	}

	public int parseDuration(DataInputStream paramDataInputStream)
	{
		System.gc();
		setInput(paramDataInputStream);
		int j;
		String str;
		do
		{
			if (!hasMoreFrames())
			{
				j = -1;
				return j;
			}
			nextFrame();
			str = getFrameTextContent();
		} while (getFrameID() != 1414284622);
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0;; i++)
		{
			if (i >= str.length())
			{
				j = Integer.parseInt(localStringBuffer.toString());
				return j;
			}
			char c = str.charAt(i);
			if ((c >= '0') && (c <= '9'))
				localStringBuffer.append(c);
		}
	}

	public String parseLyric(DataInputStream datainputstream)
	{
		System.gc();
		String s = null;
		setInput(datainputstream);
		if (hasMoreFrames())
		{
			nextFrame();
			s = getFrameTextContent();
			if (getFrameID() != 0x55534c54)
				return s;
		}
		return s;
	}

	public String parseTitle(DataInputStream datainputstream)
	{
		System.gc();
		String s = null;
		setInput(datainputstream);
		if (hasMoreFrames())
		{
			nextFrame();
			s = getFrameTextContent();
			if (getFrameID() != 0x54495432)
				return s;
		}
		return s;
	}

	public boolean setInput(DataInputStream datainputstream)
	{
		return false;
		// boolean flag;
		// flag = false;
		// in = datainputstream;
		// headerSize = 0;
		// int i;
		// i = in.readInt();
		// logger.d((new
		// StringBuilder("ID3parse:lablel:")).append(Integer.toHexString(i)).toString());
		// if(i == 0x49443304 || i == 0x49443303)
		// {
		// if(i == 0x49443303)
		// flag1 = true;
		// else
		// flag1 = false;
		// boolean flag1;
		// boolean flag2;
		// isV23 = flag1;
		// logger.d((new
		// StringBuilder("ID3parse:isV3:")).append(isV23).toString());
		// in.skip(1L);
		// if((0x40 & in.readByte()) != 64)
		// break MISSING_BLOCK_LABEL_240;
		// flag2 = true;
		// }
		// else
		// {
		// flag = false;
		// return flag;
		// }
		//
		// headerSize = in.read() << 21 | in.read() << 14 | in.read() << 7 |
		// in.read();
		// if(!flag2)
		// {
		// flag = true;
		// return flag;
		// flag2 = false;
		// headerSize = in.read() << 21 | in.read() << 14 | in.read() << 7 |
		// in.read();
		// }
		// else
		// {
		// int j;
		// int k;
		// j = in.readInt();
		// k = 0;
		// if(k < j)
		// {
		// in.read();
		// k++;
		// continue; /* Loop/switch isn't completed */
		// }
		// else
		// {
		// flag = true;
		// goto _L3
		// flag2 = false;
		// goto _L9
		// }
		// }
	}

	public static final int COMM = 0x434f4d4d;
	private static final int ID3V24 = 0x49443304;
	private static final int ID3V33 = 0x49443303;
	public static final int POSS = 0x504f5353;
	public static final int TALB = 0x54414c42;
	public static final int TCOM = 0x54434f4d;
	public static final int TCON = 0x54434f4e;
	public static final int TDRC = 0x54445243;
	public static final int TDTG = 0x54445447;
	public static final int TIT2 = 0x54495432;
	public static final int TIT3 = 0x54495433;
	public static final int TLEN = 0x544c454e;
	public static final int TPE1 = 0x54504531;
	public static final int TRCK = 0x5452434b;
	public static final int TXXX = 0x54585858;
	public static final int USLT = 0x55534c54;
	private static PlayerID3V2Parser instance;
	private static final MyLogger logger = MyLogger
			.getLogger("PlayerControllerImpl");
	private byte frameBuf[];
	private String frameContentDescrip;
	private int frameFlag;
	private int frameID;
	private int frameLen;
	private String frameTextContent;
	private int headerSize;
	private DataInputStream in;
	private boolean isV23;

}
