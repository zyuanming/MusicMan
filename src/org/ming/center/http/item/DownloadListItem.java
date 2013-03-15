package org.ming.center.http.item;

public class DownloadListItem
{
	public String batch;
	public String contentid;
	public String filename;
	public String groupcode;
	public String link;
	public String singer;
	public String size;
	public String topic;

	public String toString()
	{
		return "topic=" + this.topic + "|contentid=" + this.contentid
				+ "|size=" + this.size + "|link=" + this.link + "|groupcode="
				+ this.groupcode + "|filename=" + this.filename + "|batch="
				+ this.batch + "|singer=" + this.singer;
	}
}