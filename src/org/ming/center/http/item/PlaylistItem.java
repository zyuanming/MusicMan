package org.ming.center.http.item;

public class PlaylistItem
{
	public String batch;
	public String contentid;
	public String groupcode;
	public String link;
	public String size;
	public String topic;

	public String toString()
	{
		return "groupcode=" + this.groupcode + "|topic=" + this.topic
				+ "|contentid=" + this.contentid + "|size=" + this.size
				+ "|link=" + this.link + "|batch=" + this.batch;
	}
}