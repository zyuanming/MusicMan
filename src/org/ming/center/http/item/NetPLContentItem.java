package org.ming.center.http.item;

public class NetPLContentItem
{
	public String contentid;
	public String groupcode;
	public String name;

	public String toString()
	{
		return "name=" + this.name + "|contentid=" + this.contentid
				+ "|groupcode=" + this.groupcode;
	}
}