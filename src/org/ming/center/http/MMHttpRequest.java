package org.ming.center.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MMHttpRequest
{
	private String mContentEncoding = "utf-8";
	private String mContentType = "binary/octet-stream";
	private Map<String, String> mHeaders = new HashMap();
	private boolean mIsPostMethod;
	private String mProxyHost = null;
	private int mProxyPort = -1;
	private byte[] mReqBody;
	private String mReqBodyString;
	private int mReqType;
	private String mUrl;
	private Map<String, String> mUrlParams = new HashMap();

	public void addHeader(String paramString1, String paramString2)
	{
		this.mHeaders.put(paramString1, paramString2);
	}

	public void addUrlParams(String paramString1, String paramString2)
	{
		this.mUrlParams.put(paramString1, paramString2);
	}

	public String getContentEncoding()
	{
		return this.mContentEncoding;
	}

	public String getContentType()
	{
		return this.mContentType;
	}

	public Map<String, String> getHeaders()
	{
		return this.mHeaders;
	}

	public String getProxyHost()
	{
		return this.mProxyHost;
	}

	public int getProxyPort()
	{
		return this.mProxyPort;
	}

	public byte[] getReqBody()
	{
		return this.mReqBody;
	}

	public String getReqBodyString()
	{
		return this.mReqBodyString;
	}

	public int getReqType()
	{
		return this.mReqType;
	}

	public String getURL()
	{
		return this.mUrl;
	}

	public Map<String, String> getUrlParams()
	{
		return this.mUrlParams;
	}

	public String getUrlWithParams()
	{
		String s3 = null;
		String s = getURL();
		if (s != null)
		{
			boolean flag;
			StringBuilder stringbuilder;
			if (s.indexOf('?') == -1)
				flag = true;
			else
				flag = false;
			stringbuilder = new StringBuilder(s);

			for (Iterator iterator = mUrlParams.keySet().iterator(); iterator
					.hasNext();)
			{
				String s1 = (String) iterator.next();
				String s2 = (String) mUrlParams.get(s1);
				if (flag)
					stringbuilder.append('?');
				else if (stringbuilder.charAt(-1 + stringbuilder.length()) != '?')
					stringbuilder.append('&');
				stringbuilder.append(s1).append('=').append(s2);
				s = stringbuilder.toString();
				flag = false;
			}
			s3 = s;
		}
		return s3;
	}

	public String getValueOfUrlParams(String paramString)
	{
		return (String) this.mUrlParams.get(paramString);
	}

	public boolean isPostMethod()
	{
		return this.mIsPostMethod;
	}

	public void setContentEncoding(String paramString)
	{
		this.mContentEncoding = paramString;
	}

	public void setContentType(String paramString)
	{
		this.mContentType = paramString;
	}

	public void setPostMethod(boolean paramBoolean)
	{
		this.mIsPostMethod = paramBoolean;
	}

	public void setProxyHost(String paramString)
	{
		this.mProxyHost = paramString;
	}

	public void setProxyPort(int paramInt)
	{
		this.mProxyPort = paramInt;
	}

	public void setReqBody(byte[] paramArrayOfByte)
	{
		this.mReqBody = paramArrayOfByte;
	}

	public void setReqBodyString(String paramString)
	{
		this.mReqBodyString = paramString;
	}

	public void setReqType(int paramInt)
	{
		this.mReqType = paramInt;
	}

	public void setURL(String paramString)
	{
		this.mUrl = paramString;
	}
}