package org.ming.center.http.item;

public class SearchItem
{
	public String attachtypeid;
	public String contentid;
	public String control;
	public String des;
	public String groupcode;
	public String ishot;
	public String topic;

	public String toString()
	{
		return "topic=" + this.topic + " | attachtypeid=" + this.attachtypeid
				+ " | groupcode=" + this.groupcode + "contentid="
				+ this.contentid + "des=" + this.des + "control="
				+ this.control + "ishot=" + this.ishot;
	}
}