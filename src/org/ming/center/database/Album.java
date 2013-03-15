package org.ming.center.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Album implements Parcelable
{
	public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>()
	{
		public Album createFromParcel(Parcel paramAnonymousParcel)
		{
			return new Album(paramAnonymousParcel);
		}

		public Album[] newArray(int paramAnonymousInt)
		{
			return new Album[paramAnonymousInt];
		}
	};
	public int mExternalId = -1;
	public int mInternalId = -1;
	public String mName = null;
	public int mNumOfSong = 0;

	public Album()
	{}

	private Album(Parcel paramParcel)
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