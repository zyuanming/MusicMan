package org.ming.center.weibo;

public class AccessInfo
{
	private String OverdueTime;
	private String accessSecret;
	private String accessToken;
	private String expiresIn;
	private String refreshToken;
	private String userID;

	public String getAccessSecret()
	{
		return this.accessSecret;
	}

	public String getAccessToken()
	{
		return this.accessToken;
	}

	public String getExpiresIn()
	{
		return this.expiresIn;
	}

	public String getOverdueTime()
	{
		return this.OverdueTime;
	}

	public String getRefreshToken()
	{
		return this.refreshToken;
	}

	public String getUserID()
	{
		return this.userID;
	}

	public void setAccessSecret(String paramString)
	{
		this.accessSecret = paramString;
	}

	public void setAccessToken(String paramString)
	{
		this.accessToken = paramString;
	}

	public void setExpiresIn(String paramString)
	{
		this.expiresIn = paramString;
	}

	public void setOverdueTime(String paramString)
	{
		this.OverdueTime = paramString;
	}

	public void setRefreshToken(String paramString)
	{
		this.refreshToken = paramString;
	}

	public void setUserID(String paramString)
	{
		this.userID = paramString;
	}
}