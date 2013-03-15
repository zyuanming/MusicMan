package org.ming.center.http.item;

public class OrderInfoItem
{
	public String orderinfo;
	public String type;

	public String toString()
	{
		return "type=" + this.type + " | orderinfo=" + this.orderinfo;
	}
}