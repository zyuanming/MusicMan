package org.ming.center.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Genre implements Parcelable
{
	public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>()
	{
		public Genre createFromParcel(Parcel paramAnonymousParcel)
		{
			return new Genre(paramAnonymousParcel);
		}

		public Genre[] newArray(int paramAnonymousInt)
		{
			return new Genre[paramAnonymousInt];
		}
	};
	public int mExternalId = -1;
	public int mInternalId = -1;
	public String mName = null;
	public int mNumOfSong = 0;

	public Genre()
	{}

	private Genre(Parcel paramParcel)
	{
		this.mExternalId = paramParcel.readInt();
		this.mInternalId = paramParcel.readInt();
		this.mName = paramParcel.readString();
		this.mNumOfSong = paramParcel.readInt();
	}

	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel(Parcel paramParcel, int paramInt)
	{
		paramParcel.writeInt(this.mExternalId);
		paramParcel.writeInt(this.mInternalId);
		paramParcel.writeString(this.mName);
		paramParcel.writeInt(this.mNumOfSong);
	}
}