package org.ming.center.http.item;

import org.ming.center.database.MusicType;

import android.os.Parcel;
import android.os.Parcelable;

public class SongListItem implements Parcelable {
	public static final Parcelable.Creator<SongListItem> CREATOR = new Parcelable.Creator<SongListItem>() {
		public SongListItem createFromParcel(Parcel paramAnonymousParcel) {
			return new SongListItem(paramAnonymousParcel);
		}

		public SongListItem[] newArray(int paramAnonymousInt) {
			return new SongListItem[paramAnonymousInt];
		}
	};
	public String content_type;
	public String contentid;
	public String filesize1;
	public String filesize2;
	public String filesize3;
	public String groupcode;
	public String img;
	public String isdolby = "<unknown>";
	public int mMusicType = MusicType.ONLINEMUSIC;
	public String point;
	public String singer;
	public String title;
	public String url;
	public String url1;
	public String url2;
	public String url3;

	public SongListItem() {
	}

	private SongListItem(Parcel paramParcel) {
		this.title = paramParcel.readString();
		this.contentid = paramParcel.readString();
		this.groupcode = paramParcel.readString();
		this.img = paramParcel.readString();
		this.url = paramParcel.readString();
		this.content_type = paramParcel.readString();
		this.singer = paramParcel.readString();
		this.mMusicType = paramParcel.readInt();
		this.point = paramParcel.readString();
		this.isdolby = paramParcel.readString();
		this.url1 = paramParcel.readString();
		this.filesize1 = paramParcel.readString();
		this.url2 = paramParcel.readString();
		this.filesize2 = paramParcel.readString();
		this.url3 = paramParcel.readString();
		this.filesize3 = paramParcel.readString();
	}

	public int describeContents() {
		return 0;
	}

	public String toString() {
		return "SongListItem [content_type=" + this.content_type
				+ ", contentid=" + this.contentid + ", img=" + this.img
				+ ", mark=" + this.point + ", title=" + this.title + "]";
	}

	public void writeToParcel(Parcel paramParcel, int paramInt) {
		paramParcel.writeString(this.title);
		paramParcel.writeString(this.contentid);
		paramParcel.writeString(this.groupcode);
		paramParcel.writeString(this.img);
		paramParcel.writeString(this.url);
		paramParcel.writeString(this.content_type);
		paramParcel.writeString(this.singer);
		paramParcel.writeInt(this.mMusicType);
		paramParcel.writeString(this.point);
		paramParcel.writeString(this.isdolby);
		paramParcel.writeString(this.url1);
		paramParcel.writeString(this.filesize1);
		paramParcel.writeString(this.url2);
		paramParcel.writeString(this.filesize2);
		paramParcel.writeString(this.url3);
		paramParcel.writeString(this.filesize3);
	}
}