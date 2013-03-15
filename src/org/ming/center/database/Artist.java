package org.ming.center.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Artist implements Parcelable
{
	public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>()
	{
		public Artist createFromParcel(Parcel paramAnonymousParcel)
		{
			return new Artist(paramAnonymousParcel);
		}

		public Artist[] newArray(int paramAnonymousInt)
		{
			return new Artist[paramAnonymousInt];
		}
	};
	public int mExternalId = -1;
	public int mInternalId = -1;
	public String mName = null;
	public int mNumOfAlbum = 0;
	public int mNumOfSong = 0;

	public Artist()
	{}

	private Artist(Parcel paramParcel)
	{
		this.mExternalId = paramParcel.readInt();
		this.mInternalId = paramParcel.readInt();
		this.mName = paramParcel.readString();
		this.mNumOfSong = paramParcel.readInt();
		this.mNumOfAlbum = paramParcel.readInt();
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
		paramParcel.writeInt(this.mNumOfAlbum);
	}
}