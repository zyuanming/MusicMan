package org.ming.center.http.item;

public class NotificationItem
{
	public String id;
	public String msg;
	public String pushtime;
	public String title;
	public String type;
	public String url;

	public String getId()
	{
		return this.id;
	}

	public String getMsg()
	{
		return this.msg;
	}

	public String getPushtime()
	{
		return this.pushtime;
	}

	public String getTitle()
	{
		return this.title;
	}

	public String getType()
	{
		return this.type;
	}

	public String getUrl()
	{
		return this.url;
	}

	public void setId(String paramString)
	{
		this.id = paramString;
	}

	public void setMsg(String paramString)
	{
		this.msg = paramString;
	}

	public void setPushtime(String paramString)
	{
		this.pushtime = paramString;
	}

	public void setTitle(String paramString)
	{
		this.title = paramString;
	}

	public void setType(String paramString)
	{
		this.type = paramString;
	}

	public void setUrl(String paramString)
	{
		this.url = paramString;
	}

	public String toString()
	{
		return "NotificationItem [id=" + this.id + ", title=" + this.title
				+ ", msg=" + this.msg + ", pushtime=" + this.pushtime
				+ ", type=" + this.type + ", url=" + this.url + "]";
	}
}