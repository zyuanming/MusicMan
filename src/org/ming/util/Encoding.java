package org.ming.util;

class Encoding
{

	public Encoding()
	{
		javaname = new String[TOTALTYPES];
		nicename = new String[TOTALTYPES];
		htmlname = new String[TOTALTYPES];
		javaname[GB2312] = "GB2312";
		javaname[GBK] = "GBK";
		javaname[GB18030] = "GB18030";
		javaname[HZ] = "ASCII";
		javaname[ISO2022CN_GB] = "ISO2022CN_GB";
		javaname[BIG5] = "BIG5";
		javaname[CNS11643] = "EUC-TW";
		javaname[ISO2022CN_CNS] = "ISO2022CN_CNS";
		javaname[ISO2022CN] = "ISO2022CN";
		javaname[UTF8] = "UTF8";
		javaname[UTF8T] = "UTF8";
		javaname[UTF8S] = "UTF8";
		javaname[UNICODE] = "Unicode";
		javaname[UNICODET] = "Unicode";
		javaname[UNICODES] = "Unicode";
		javaname[EUC_KR] = "EUC_KR";
		javaname[CP949] = "MS949";
		javaname[ISO2022KR] = "ISO2022KR";
		javaname[JOHAB] = "Johab";
		javaname[SJIS] = "SJIS";
		javaname[EUC_JP] = "EUC_JP";
		javaname[ISO2022JP] = "ISO2022JP";
		javaname[ASCII] = "ASCII";
		javaname[OTHER] = "ISO8859_1";
		htmlname[GB2312] = "GB2312";
		htmlname[GBK] = "GBK";
		htmlname[GB18030] = "GB18030";
		htmlname[HZ] = "HZ-GB-2312";
		htmlname[ISO2022CN_GB] = "ISO-2022-CN-EXT";
		htmlname[BIG5] = "BIG5";
		htmlname[CNS11643] = "EUC-TW";
		htmlname[ISO2022CN_CNS] = "ISO-2022-CN-EXT";
		htmlname[ISO2022CN] = "ISO-2022-CN";
		htmlname[UTF8] = "UTF-8";
		htmlname[UTF8T] = "UTF-8";
		htmlname[UTF8S] = "UTF-8";
		htmlname[UNICODE] = "UTF-16";
		htmlname[UNICODET] = "UTF-16";
		htmlname[UNICODES] = "UTF-16";
		htmlname[EUC_KR] = "EUC-KR";
		htmlname[CP949] = "x-windows-949";
		htmlname[ISO2022KR] = "ISO-2022-KR";
		htmlname[JOHAB] = "x-Johab";
		htmlname[SJIS] = "Shift_JIS";
		htmlname[EUC_JP] = "EUC-JP";
		htmlname[ISO2022JP] = "ISO-2022-JP";
		htmlname[ASCII] = "ASCII";
		htmlname[OTHER] = "ISO8859-1";
		nicename[GB2312] = "GB-2312";
		nicename[GBK] = "GBK";
		nicename[GB18030] = "GB18030";
		nicename[HZ] = "HZ";
		nicename[ISO2022CN_GB] = "ISO2022CN-GB";
		nicename[BIG5] = "Big5";
		nicename[CNS11643] = "CNS11643";
		nicename[ISO2022CN_CNS] = "ISO2022CN-CNS";
		nicename[ISO2022CN] = "ISO2022 CN";
		nicename[UTF8] = "UTF-8";
		nicename[UTF8T] = "UTF-8 (Trad)";
		nicename[UTF8S] = "UTF-8 (Simp)";
		nicename[UNICODE] = "Unicode";
		nicename[UNICODET] = "Unicode (Trad)";
		nicename[UNICODES] = "Unicode (Simp)";
		nicename[EUC_KR] = "EUC-KR";
		nicename[CP949] = "CP949";
		nicename[ISO2022KR] = "ISO 2022 KR";
		nicename[JOHAB] = "Johab";
		nicename[SJIS] = "Shift-JIS";
		nicename[EUC_JP] = "EUC-JP";
		nicename[ISO2022JP] = "ISO 2022 JP";
		nicename[ASCII] = "ASCII";
		nicename[OTHER] = "OTHER";
	}

	public static int ASCII = 0;
	public static int BIG5 = 0;
	public static int CNS11643 = 0;
	public static int CP949 = 0;
	public static int EUC_JP = 0;
	public static int EUC_KR = 0;
	public static int GB18030 = 0;
	public static int GB2312 = 0;
	public static int GBK = 0;
	public static int HZ = 0;
	public static int ISO2022CN = 0;
	public static int ISO2022CN_CNS = 0;
	public static int ISO2022CN_GB = 0;
	public static int ISO2022JP = 0;
	public static int ISO2022KR = 0;
	public static int JOHAB = 0;
	public static int OTHER = 0;
	public static final int SIMP = 0;
	public static int SJIS = 0;
	public static int TOTALTYPES = 0;
	public static final int TRAD = 1;
	public static int UNICODE = 9;
	public static int UNICODES = 11;
	public static int UNICODET = 10;
	public static int UTF8 = 6;
	public static int UTF8S = 8;
	public static int UTF8T = 7;
	public static String htmlname[];
	public static String javaname[];
	public static String nicename[];

	static
	{
		GB2312 = 0;
		GBK = 1;
		GB18030 = 2;
		HZ = 3;
		BIG5 = 4;
		CNS11643 = 5;
		ISO2022CN = 12;
		ISO2022CN_CNS = 13;
		ISO2022CN_GB = 14;
		EUC_KR = 15;
		CP949 = 16;
		ISO2022KR = 17;
		JOHAB = 18;
		SJIS = 19;
		EUC_JP = 20;
		ISO2022JP = 21;
		ASCII = 22;
		OTHER = 23;
		TOTALTYPES = 24;
	}
}
