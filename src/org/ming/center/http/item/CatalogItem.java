package org.ming.center.http.item;

public class CatalogItem
{
	public String batchsub;
	public String catalogtype;
	public String ids;
	public String title;
	public String url;

	public String toString()
	{
		return "title=" + this.title + " | url=" + this.url + " | catalogtype="
				+ this.catalogtype + "batchsub=" + this.batchsub + "ids="
				+ this.ids;
	}
}