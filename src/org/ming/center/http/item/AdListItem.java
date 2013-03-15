package org.ming.center.http.item;

public class AdListItem
{
	public String content_type;
	public String contentid;
	public String groupcode;
	public String img;
	public String title;
	public String url;

	public String toString()
	{
		return "AdListItem [content_type=" + this.content_type + ", contentid="
				+ this.contentid + ", groupcode=" + this.groupcode + ", img="
				+ this.img + ", title=" + this.title + ", url=" + this.url
				+ "]";
	}
}