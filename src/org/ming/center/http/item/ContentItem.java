package org.ming.center.http.item;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ContentItem implements Parcelable
{
	public static final Parcelable.Creator<ContentItem> CREATOR = new Parcelable.Creator<ContentItem>()
	{
		public ContentItem createFromParcel(Parcel paramAnonymousParcel)
		{
			return new ContentItem(paramAnonymousParcel);
		}

		public ContentItem[] newArray(int paramAnonymousInt)
		{
			return new ContentItem[paramAnonymousInt];
		}
	};
	public String content_type;
	public String contentid;
	public int current_page;
	public String groupcode;
	public String img;
	public String mark;
	public String publish_time;
	public String summary;
	public String title;

	public ContentItem()
	{}

	private ContentItem(Parcel paramParcel)
	{
		this.title = paramParcel.readString();
		this.contentid = paramParcel.readString();
		this.groupcode = paramParcel.readString();
		this.img = paramParcel.readString();
		this.content_type = paramParcel.readString();
		this.mark = paramParcel.readString();
		this.summary = paramParcel.readString();
		this.publish_time = paramParcel.readString();
		this.current_page = paramParcel.readInt();
	}

	public int describeContents()
	{
		return 0;
	}

	public String toString()
	{
		return "ContentItem [content_type=" + this.content_type
				+ ", contentid=" + this.contentid + ", img=" + this.img
				+ ", mark=" + this.mark + ", publish_time=" + this.publish_time
				+ ", summary=" + this.summary + ", title=" + this.title
				+ ", current_page = " + this.current_page + "]";
	}

	public void writeToParcel(Parcel paramParcel, int paramInt)
	{
		paramParcel.writeString(this.title);
		paramParcel.writeString(this.contentid);
		paramParcel.writeString(this.groupcode);
		paramParcel.writeString(this.img);
		paramParcel.writeString(this.content_type);
		paramParcel.writeString(this.mark);
		paramParcel.writeString(this.summary);
		paramParcel.writeString(this.publish_time);
		paramParcel.writeInt(this.current_page);
	}
}