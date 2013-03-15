package org.ming.center.http.item;

public class ToneboxItem
{
	public String attachtypeid;
	public String boxid;
	public String des;
	public String groupcode;
	public String topic;

	public String toString()
	{
		return "topic=" + this.topic + " | boxid=" + this.boxid
				+ " | attachtypeid=" + this.attachtypeid + " | groupcode="
				+ this.groupcode + "des=" + this.des;
	}
}