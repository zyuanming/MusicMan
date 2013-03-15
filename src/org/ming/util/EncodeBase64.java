package org.ming.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class EncodeBase64
{
	private static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
			.toCharArray();
	private static final int[] IA = new int[256];

	static
	{
		Arrays.fill(IA, -1);
		int j = CA.length;
		for (int i = 0; i < j; i++)
		{
			IA[CA[i]] = i;
		}
		IA[61] = 0;
	}

	public static final String encode(String s)
	{
		String s1;
		try
		{
			s1 = new String(encodeToChar(s.getBytes("UTF-8"), false));
		} catch (UnsupportedEncodingException unsupportedencodingexception)
		{
			unsupportedencodingexception.printStackTrace();
			s1 = null;
		}
		return s1;
	}

	public static final char[] encodeToChar(byte[] abyte0, boolean flag)
	{
		return null;
		// int i;
		// char ac[];
		// if(abyte0 != null)
		// i = abyte0.length;
		// else
		// i = 0;
		// if(i != 0)
		// {
		// int i1;
		// int j1;
		// int k1;
		// int l1;
		// int j = 3 * (i / 3);
		// int k = 1 + (i - 1) / 3 << 2;
		// int l;
		// int k4;
		// int l4;
		// int j5;
		// int k5;
		// if(flag)
		// l = (k - 1) / 76 << 1;
		// else
		// l = 0;
		// i1 = k + l;
		// ac = new char[i1];
		// j1 = 0;
		// k1 = 0;
		// l1 = 0;
		//
		// if(l1 < j)
		// break label0;
		// k4 = i - j;
		// if(k4 > 0)
		// {
		// l4 = (0xff & abyte0[j]) << 10;
		// int i2;
		// int j2;
		// int k2;
		// int l2;
		// int j3;
		// int k3;
		// int l3;
		// int i4;
		// int j4;
		// int i5;
		// char c;
		// if(k4 == 2)
		// i5 = (0xff & abyte0[i - 1]) << 2;
		// else
		// i5 = 0;
		// j5 = l4 | i5;
		// ac[i1 - 4] = CA[j5 >> 12];
		// ac[i1 - 3] = CA[0x3f & j5 >>> 6];
		// k5 = i1 - 2;
		// if(k4 == 2)
		// c = CA[j5 & 0x3f];
		// else
		// c = '=';
		// ac[k5] = c;
		// ac[i1 - 1] = '=';
		// }
		//
		//
		//
		// i2 = l1 + 1;
		// j2 = (0xff & abyte0[l1]) << 16;
		// k2 = i2 + 1;
		// l2 = j2 | (0xff & abyte0[i2]) << 8;
		// int i3 = k2 + 1;
		// j3 = l2 | 0xff & abyte0[k2];
		// k3 = k1 + 1;
		// ac[k1] = CA[0x3f & j3 >>> 18];
		// l3 = k3 + 1;
		// ac[k3] = CA[0x3f & j3 >>> 12];
		// i4 = l3 + 1;
		// ac[l3] = CA[0x3f & j3 >>> 6];
		// k1 = i4 + 1;
		// ac[i4] = CA[j3 & 0x3f];
		// if(flag && ++j1 == 19 && k1 < i1 - 2)
		// {
		// j4 = k1 + 1;
		// ac[k1] = '\r';
		// k1 = j4 + 1;
		// ac[j4] = '\n';
		// l1 = i3;
		// j1 = 0;
		// } else
		// {
		// l1 = i3;
		// }
		// }
		// else
		// {
		// ac = new char[0];
		// }
		//
		// return ac;
	}
}