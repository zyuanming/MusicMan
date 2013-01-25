package org.ming.center.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Playlist implements Parcelable
{
	public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>()
	{
		public Playlist createFromParcel(Parcel paramAnonymousParcel)
		{
			return new Playlist(paramAnonymousParcel);
		}

		public Playlist[] newArray(int paramAnonymousInt)
		{
			return new Playlist[paramAnonymousInt];
		}
	};
	public String mData = null;
	public long mDateAdded = 0L;
	public long mDateModified = 0L;
	public int mExternalId = -1;
	public String mName = null;
	public int mNumOfSong = 0;

	public Playlist()
	{
	}

	private Playlist(Parcel paramParcel)
	{
		this.mExternalId = paramParcel.readInt();
		this.mName = paramParcel.readString();
		this.mData = paramParcel.readString();
		this.mDateAdded = paramParcel.readLong();
		this.mDateModified = paramParcel.readLong();
		this.mNumOfSong = paramParcel.readInt();
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel paramParcel, int paramInt)
	{
		paramParcel.writeInt(this.mExternalId);
		paramParcel.writeString(this.mName);
		paramParcel.writeString(this.mData);
		paramParcel.writeLong(this.mDateAdded);
		paramParcel.writeLong(this.mDateModified);
		paramParcel.writeInt(this.mNumOfSong);
	}
}