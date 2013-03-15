package org.ming.center.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Song implements Parcelable
{
	public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>()
	{
		public Song createFromParcel(Parcel paramAnonymousParcel)
		{
			return new Song(paramAnonymousParcel);
		}

		public Song[] newArray(int paramAnonymousInt)
		{
			return new Song[paramAnonymousInt];
		}
	};
	public boolean isDolby = false;
	public int like = 0;
	public int limit1 = 0;
	public int limit2 = 0;
	public int limit3 = 0;
	public String mAlbum = "<unknown>";
	public int mAlbumId = -1;
	public String mArtUrl = null;
	public String mArtist = "<unknown>";
	public String mContentId = "<unknown>";
	public int mDuration = 0;
	public String mGroupCode = "<unknown>";
	public long mId = -1L;
	public String mLyric = null;
	public int mMusicType = 0;
	public int mPoint = 0;
	public long mSize = 0L;
	public long mSize2 = 0L;
	public long mSize3 = 0L;
	public String mTrack = "<unknown>";
	public String mUrl = "<unknown>";
	public String mUrl2 = "<unknown>";
	public String mUrl3 = "<unknown>";
	public String ringSongPrice = "0";
	public String crbtValidity = "";

	public Song()
	{}

	private Song(Parcel paramParcel)
	{
		boolean[] arrayOfBoolean = new boolean[1];
		this.mId = paramParcel.readLong();
		this.mUrl = paramParcel.readString();
		this.mUrl2 = paramParcel.readString();
		this.mUrl3 = paramParcel.readString();
		this.mTrack = paramParcel.readString();
		this.mArtist = paramParcel.readString();
		this.mAlbum = paramParcel.readString();
		this.mGroupCode = paramParcel.readString();
		this.mSize = paramParcel.readLong();
		this.mSize2 = paramParcel.readLong();
		this.mSize3 = paramParcel.readLong();
		this.mContentId = paramParcel.readString();
		this.mAlbumId = paramParcel.readInt();
		this.mMusicType = paramParcel.readInt();
		this.mDuration = paramParcel.readInt();
		this.mLyric = paramParcel.readString();
		this.mArtUrl = paramParcel.readString();
		this.limit1 = paramParcel.readInt();
		this.mPoint = paramParcel.readInt();
		this.limit2 = paramParcel.readInt();
		this.limit3 = paramParcel.readInt();
		this.crbtValidity = paramParcel.readString();
		this.ringSongPrice = paramParcel.readString();
		paramParcel.readBooleanArray(arrayOfBoolean);
		this.isDolby = arrayOfBoolean[0];
	}

	public int describeContents()
	{
		return 0;
	}

	public boolean equals(Object paramObject)
	{
		boolean bool;
		if (paramObject == null)
			bool = false;
		if ((this.mMusicType == MusicType.ONLINEMUSIC.ordinal())
				&& (((Song) paramObject).mMusicType == MusicType.ONLINEMUSIC
						.ordinal()))
		{
			if ((this.mContentId
					.equalsIgnoreCase(((Song) paramObject).mContentId))
					&& (this.mGroupCode
							.equalsIgnoreCase(((Song) paramObject).mGroupCode)))
				bool = true;
			else
				bool = false;
		} else if ((this.mMusicType == MusicType.LOCALMUSIC.ordinal())
				&& (((Song) paramObject).mMusicType == MusicType.LOCALMUSIC
						.ordinal()))
		{
			if (this.mId == ((Song) paramObject).mId)
				bool = true;
			else
				bool = false;
		} else if ((this.mMusicType == MusicType.RADIO.ordinal())
				&& (((Song) paramObject).mMusicType == MusicType.RADIO
						.ordinal()))
		{
			if ((this.mContentId
					.equalsIgnoreCase(((Song) paramObject).mContentId))
					&& (this.mGroupCode
							.equalsIgnoreCase(((Song) paramObject).mGroupCode)))
				bool = true;
			else
				bool = false;
		} else
		{
			bool = false;
		}

		return bool;
	}

	public void writeToParcel(Parcel paramParcel, int paramInt)
	{
		boolean[] arrayOfBoolean = new boolean[1];
		paramParcel.writeLong(this.mId);
		paramParcel.writeString(this.mUrl);
		paramParcel.writeString(this.mUrl2);
		paramParcel.writeString(this.mUrl3);
		paramParcel.writeString(this.mTrack);
		paramParcel.writeString(this.mArtist);
		paramParcel.writeString(this.mAlbum);
		paramParcel.writeString(this.mGroupCode);
		paramParcel.writeLong(this.mSize);
		paramParcel.writeLong(this.mSize2);
		paramParcel.writeLong(this.mSize3);
		paramParcel.writeString(this.mContentId);
		paramParcel.writeInt(this.mAlbumId);
		paramParcel.writeInt(this.mMusicType);
		paramParcel.writeInt(this.mDuration);
		paramParcel.writeString(this.mLyric);
		paramParcel.writeString(this.mArtUrl);
		paramParcel.writeInt(this.limit1);
		paramParcel.writeInt(this.mPoint);
		paramParcel.writeInt(this.limit2);
		paramParcel.writeInt(this.limit3);
		paramParcel.writeString(this.crbtValidity);
		paramParcel.writeString(this.ringSongPrice);
		arrayOfBoolean[0] = this.isDolby;
		paramParcel.writeBooleanArray(arrayOfBoolean);
	}
}