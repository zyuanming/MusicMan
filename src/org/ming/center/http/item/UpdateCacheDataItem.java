package org.ming.center.http.item;

public class UpdateCacheDataItem
{
	public String groupcode;
	public String publish_time;

	public String toString()
	{
		return "groupcode=" + this.groupcode + " | publish_time="
				+ this.publish_time;
	}
}
