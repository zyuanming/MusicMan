package org.ming.center.http.item;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicListColumnItem implements Parcelable
{
	public static final Parcelable.Creator<MusicListColumnItem> CREATOR = new Parcelable.Creator<MusicListColumnItem>()
	{
		public MusicListColumnItem createFromParcel(Parcel paramAnonymousParcel)
		{
			return new MusicListColumnItem(paramAnonymousParcel);
		}

		public MusicListColumnItem[] newArray(int paramAnonymousInt)
		{
			return new MusicListColumnItem[paramAnonymousInt];
		}
	};
	public String category_type;
	public String img;
	public String title;
	public String url;

	public MusicListColumnItem()
	{}

	private MusicListColumnItem(Parcel paramParcel)
	{
		this.img = paramParcel.readString();
		this.category_type = paramParcel.readString();
		this.title = paramParcel.readString();
		this.url = paramParcel.readString();
	}

	public int describeContents()
	{
		return 0;
	}

	public String toString()
	{
		return "MusicListColumnItem [catalog_type=" + this.category_type
				+ ", img=" + this.img + ", title=" + this.title + ", url="
				+ this.url + "]";
	}

	public void writeToParcel(Parcel paramParcel, int paramInt)
	{
		paramParcel.writeString(this.img);
		paramParcel.writeString(this.category_type);
		paramParcel.writeString(this.title);
		paramParcel.writeString(this.url);
	}
}