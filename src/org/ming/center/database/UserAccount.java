package org.ming.center.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class UserAccount implements Parcelable
{
	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_LOGINTIME = 4;
	public static final int COLUMN_INDEX_MDN = 1;
	public static final int COLUMN_INDEX_Name = 2;
	public static final int COLUMN_INDEX_PASSWORD = 3;
	public static final Parcelable.Creator<UserAccount> CREATOR = new Parcelable.Creator<UserAccount>()
	{
		public UserAccount createFromParcel(Parcel paramAnonymousParcel)
		{
			return new UserAccount(paramAnonymousParcel);
		}

		public UserAccount[] newArray(int paramAnonymousInt)
		{
			return new UserAccount[paramAnonymousInt];
		}
	};
	public long mId;
	public String mMDN = null;
	public String mName = null;
	public String mPassword = null;

	public UserAccount()
	{
	}

	private UserAccount(Parcel paramParcel)
	{
		this.mPassword = paramParcel.readString();
		this.mMDN = paramParcel.readString();
		this.mName = paramParcel.readString();
		this.mId = paramParcel.readInt();
	}

	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel(Parcel paramParcel, int paramInt)
	{
		paramParcel.writeString(this.mMDN);
		paramParcel.writeString(this.mPassword);
		paramParcel.writeString(this.mName);
		paramParcel.writeLong(this.mId);
	}
}
