package org.ming.ui.adapter;

import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.http.item.SongListItem;
import org.ming.center.player.PlayerController;
import org.ming.util.MyLogger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MobileMusicAlbumDetailListItemAdapter extends BaseAdapter
{
	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicAlbumDetailListItemAdapter");
	private android.view.View.OnClickListener mBtnClicker;
	private Controller mController;
	private int mIconrid;
	private LayoutInflater mInflater;
	private boolean mIsRatingVisable;
	private PlayerController mPlayerController;
	private List mRecommendSongListItemData;

	public MobileMusicAlbumDetailListItemAdapter(Context context, List list)
	{
		mPlayerController = null;
		mIconrid = 0;
		mIsRatingVisable = true;
		mInflater = LayoutInflater.from(context);
		mRecommendSongListItemData = list;
		mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		mPlayerController = mController.getPlayerController();
	}

	private int setMarkLevel(String paramString)
	{
		logger.v("setMarkLevel() ---> Enter");
		int i;
		if ((!this.mIsRatingVisable) || (paramString == null)
				|| (paramString.trim().equals("")))
		{
			i = 0;
		} else
		{
			int j;
			switch (Integer.parseInt(paramString))
			{
			default:
			case 1:
				j = R.drawable.mark_1;
				break;
			case 2:
				j = R.drawable.mark_2;
				break;
			case 3:
				j = R.drawable.mark_3;
				break;
			case 4:
				j = R.drawable.mark_4;
				break;
			case 5:
				j = R.drawable.mark_5;
				break;
			}
			logger.v("setMarkLevel() ---> Exit");
			i = j;
		}
		return i;
	}

	public int getCount()
	{
		return this.mRecommendSongListItemData.size();
	}

	public Object getItem(int paramInt)
	{
		return this.mRecommendSongListItemData.get(paramInt);
	}

	public long getItemId(int paramInt)
	{
		return (long) paramInt;
	}

	public View getView(int i, View view, ViewGroup viewgroup)
	{
		logger.v("getView() ---> Enter");
		if (view == null)
		{
			ViewHolder viewholder = new ViewHolder();
			view = mInflater
					.inflate(R.layout.list_cell_album_detail_list, null);
			viewholder.mAblumNo = (Button) view.findViewById(0x7f0500f9);
			viewholder.mImageDoblyForListCell = (ImageView) view
					.findViewById(0x7f0500fa);
			viewholder.mSongName = (TextView) view.findViewById(0x7f050003);
			viewholder.mSongerName = (TextView) view.findViewById(0x7f0500fd);
			viewholder.mSongRatingbar = (ImageView) view
					.findViewById(0x7f0500fe);
			viewholder.mBtnRecommendState = (ImageView) view
					.findViewById(0x7f0500fb);
			viewholder.mBtnRecommendAdd = (ImageView) view
					.findViewById(0x7f050100);
			view.setTag(viewholder);
		}
		ViewHolder viewholder1 = (ViewHolder) view.getTag();
		int j = i + 1;
		viewholder1.mAblumNo
				.setText((new StringBuilder()).append(j).toString());
		viewholder1.mSongName
				.setText(((SongListItem) mRecommendSongListItemData.get(i)).title);
		viewholder1.mSongerName
				.setText(((SongListItem) mRecommendSongListItemData.get(i)).singer);
		Song song;
		SongListItem songlistitem;
		if (((SongListItem) mRecommendSongListItemData.get(i)).isdolby
				.equals("1"))
			viewholder1.mImageDoblyForListCell.setVisibility(0);
		else
			viewholder1.mImageDoblyForListCell.setVisibility(4);
		if (((SongListItem) mRecommendSongListItemData.get(i)).mMusicType == MusicType.ONLINEMUSIC
				.ordinal())
		{
			int k = setMarkLevel(((SongListItem) mRecommendSongListItemData
					.get(i)).point);
			if (k != 0)
			{
				viewholder1.mSongRatingbar.setVisibility(0);
				viewholder1.mSongRatingbar.setBackgroundResource(k);
			} else
			{
				viewholder1.mSongRatingbar.setVisibility(8);
			}
		} else
		{
			viewholder1.mSongRatingbar.setBackgroundResource(0x7f0200e5);
		}
		if (mIconrid != 0)
			viewholder1.mBtnRecommendAdd.setImageResource(mIconrid);
		else if (mIconrid == -1)
			viewholder1.mBtnRecommendAdd.setVisibility(8);
		song = mPlayerController.getCurrentPlayingItem();
		songlistitem = (SongListItem) mRecommendSongListItemData.get(i);
		if (song != null)
		{
			if (songlistitem.mMusicType == MusicType.LOCALMUSIC.ordinal()
					&& song.mMusicType == MusicType.LOCALMUSIC.ordinal()
					&& songlistitem.url.equalsIgnoreCase(song.mUrl))
				viewholder1.mBtnRecommendState.setVisibility(0);
			else if (songlistitem.mMusicType == MusicType.ONLINEMUSIC.ordinal()
					&& song.mMusicType == MusicType.ONLINEMUSIC.ordinal()
					&& songlistitem.contentid.equalsIgnoreCase(song.mContentId)
					&& songlistitem.groupcode.equalsIgnoreCase(song.mGroupCode))
				viewholder1.mBtnRecommendState.setVisibility(0);
			else
				viewholder1.mBtnRecommendState.setVisibility(8);
		} else
		{
			viewholder1.mBtnRecommendState.setVisibility(8);
		}
		viewholder1.mBtnRecommendAdd.setOnClickListener(mBtnClicker);
		viewholder1.mBtnRecommendAdd.setTag(Integer.valueOf(i));
		logger.v("getView() ---> Exit");
		return view;
	}

	public void releaseAdapterResource()
	{
		this.mInflater = null;
		if (this.mRecommendSongListItemData != null)
		{
			this.mRecommendSongListItemData.clear();
			this.mRecommendSongListItemData = null;
		}
		this.mController = null;
		this.mPlayerController = null;
	}

	public void setBtnOnClickListener(View.OnClickListener paramOnClickListener)
	{
		this.mBtnClicker = paramOnClickListener;
	}

	public void setCacheImgData(boolean paramBoolean)
	{}

	public void setIcon(int paramInt)
	{
		this.mIconrid = paramInt;
	}

	public void setRatingVisaible(boolean paramBoolean)
	{
		this.mIsRatingVisable = paramBoolean;
	}

	public final class ViewHolder
	{
		public Button mAblumNo;
		public ImageView mBtnRecommendAdd;
		public ImageView mBtnRecommendState;
		public ImageView mImageDoblyForListCell;
		public TextView mSongName;
		public ImageView mSongRatingbar;
		public TextView mSongerName;

		public ViewHolder()
		{}
	}
}