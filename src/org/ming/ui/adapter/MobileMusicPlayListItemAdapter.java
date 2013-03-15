package org.ming.ui.adapter;

import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.download.UrlImageDownloader;
import org.ming.center.http.item.SongListItem;
import org.ming.center.player.PlayerController;
import org.ming.util.MyLogger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MobileMusicPlayListItemAdapter extends BaseAdapter
{
	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicPlayListItemAdapter");
	private View.OnClickListener mBtnClicker;
	private Context mContext;
	private Controller mController;
	private UrlImageDownloader mImageDownloader;
	private LayoutInflater mInflater;
	private boolean mIsRatingVisable = true;
	private List<SongListItem> mPlayListItemData;
	private PlayerController mPlayerController = null;

	public MobileMusicPlayListItemAdapter(Context paramContext, List paramList)
	{
		this.mInflater = LayoutInflater.from(paramContext);
		this.mPlayListItemData = paramList;
		this.mContext = paramContext;
		this.mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		this.mPlayerController = this.mController.getPlayerController();
		this.mImageDownloader = new UrlImageDownloader(paramContext);
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
			default:
				j = R.drawable.mark_0;
				break;
			}
			i = j;
		}
		logger.v("setMarkLevel() ---> Exit");
		return i;
	}

	public int getCount()
	{
		if (mPlayListItemData != null)
		{
			return this.mPlayListItemData.size();
		} else
		{
			return 0;
		}
	}

	public Object getItem(int paramInt)
	{
		return this.mPlayListItemData.get(paramInt);
	}

	public long getItemId(int paramInt)
	{
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
	{
		logger.v("getView() ---> Enter");
		if (paramView == null)
		{
			ViewHolder localViewHolder1 = new ViewHolder();
			paramView = this.mInflater.inflate(R.layout.list_cell_play_list,
					null);
			localViewHolder1.mAblumImage = ((ImageView) paramView
					.findViewById(R.id.ablum_img));
			localViewHolder1.mImageDoblyForListCell = ((ImageView) paramView
					.findViewById(R.id.dobly_img_for_list_cell));
			localViewHolder1.mSongName = ((TextView) paramView
					.findViewById(R.id.song_name));
			localViewHolder1.mSongerName = ((TextView) paramView
					.findViewById(R.id.songer_name));
			localViewHolder1.mSongRatingbar = ((ImageView) paramView
					.findViewById(R.id.song_ratingbar));
			localViewHolder1.mBtnRecommendState = ((ImageView) paramView
					.findViewById(R.id.play_state));
			localViewHolder1.mBtnRecommendAdd = ((ImageView) paramView
					.findViewById(R.id.btn_recommend_add));
			paramView.setTag(localViewHolder1);
		}
		ViewHolder localViewHolder2 = (ViewHolder) paramView.getTag();
		SongListItem localSongListItem1 = (SongListItem) this.mPlayListItemData
				.get(paramInt);
		this.mImageDownloader.download(localSongListItem1.img,
				R.drawable.image_default_ablum_for_play_view,
				localViewHolder2.mAblumImage, localSongListItem1.groupcode);
		localViewHolder2.mSongName.setText(localSongListItem1.title);
		String str = localSongListItem1.singer;
		Song localSong;
		SongListItem localSongListItem2;
		if ((str == null) || (str.equals("<unknown>")))
		{
			localViewHolder2.mSongerName.setText(this.mContext
					.getText(R.string.unknown_artist_name_db_controller));
		} else
		{
			localViewHolder2.mSongerName.setText(str);
		}
		if (!localSongListItem1.isdolby.equals("1"))
		{
			localViewHolder2.mImageDoblyForListCell.setVisibility(0);
		} else
		{
			localViewHolder2.mImageDoblyForListCell.setVisibility(4);
		}
		if (localSongListItem1.mMusicType == MusicType.ONLINEMUSIC.ordinal())
		{
			int i = setMarkLevel(localSongListItem1.point);
			if (i != 0)
			{
				localViewHolder2.mSongRatingbar.setVisibility(0);
				localViewHolder2.mSongRatingbar.setBackgroundResource(i);
			} else
			{
				localViewHolder2.mSongRatingbar.setVisibility(8);
			}
		} else
		{
			localViewHolder2.mSongRatingbar.setVisibility(0);
			localViewHolder2.mSongRatingbar
					.setBackgroundResource(R.drawable.local_music_icon);
		}
		localSong = this.mPlayerController.getCurrentPlayingItem();
		localSongListItem2 = (SongListItem) this.mPlayListItemData
				.get(paramInt);
		if (localSong != null)
		{
			if ((localSongListItem2.mMusicType != MusicType.LOCALMUSIC
					.ordinal())
					|| (localSong.mMusicType != MusicType.LOCALMUSIC.ordinal())
					|| (!localSongListItem2.url
							.equalsIgnoreCase(localSong.mUrl)))
			{
				if ((localSongListItem2.mMusicType == MusicType.ONLINEMUSIC
						.ordinal())
						&& (localSong.mMusicType == MusicType.ONLINEMUSIC
								.ordinal())
						&& (localSongListItem2.contentid
								.equalsIgnoreCase(localSong.mContentId))
						&& (localSongListItem2.groupcode
								.equalsIgnoreCase(localSong.mGroupCode)))
				{
					localViewHolder2.mBtnRecommendState.setVisibility(0);
				} else
				{
					localViewHolder2.mBtnRecommendState.setVisibility(8);
				}
			} else
			{
				localViewHolder2.mBtnRecommendState.setVisibility(0);
			}
		} else
		{
			localViewHolder2.mBtnRecommendState.setVisibility(8);
		}
		localViewHolder2.mBtnRecommendAdd.setOnClickListener(this.mBtnClicker);
		localViewHolder2.mBtnRecommendAdd.setTag(Integer.valueOf(paramInt));
		logger.v("getView() ---> Exit");
		return paramView;
	}

	public void setBtnOnClickListener(View.OnClickListener paramOnClickListener)
	{
		this.mBtnClicker = paramOnClickListener;
	}

	public void setRatingVisaible(boolean paramBoolean)
	{
		this.mIsRatingVisable = paramBoolean;
	}

	public final class ViewHolder
	{
		public ImageView mAblumImage;
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

/*
 * Location: D:\guangge\windows64\反编译工具\classes-dex2jar.jar Qualified Name:
 * cmccwm.mobilemusic.ui.adapter.MobileMusicPlayListItemAdapter JD-Core Version:
 * 0.6.2
 */
// R.style.launch