package org.ming.center.http.item;

public class TabItem
{
	public String category_type;
	public String title;
	public String url;

	public String toString()
	{
		return "title=" + this.title + "|category_type=" + this.category_type
				+ "|url=" + this.url;
	}
}