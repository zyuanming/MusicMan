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

public class MobileMusicRecommendListItemAdapter extends BaseAdapter
{
	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicRecommendListItemAdapter");
	private View.OnClickListener mBtnClicker;
	private Controller mController;
	private int mIconrid = 0;
	private UrlImageDownloader mImageDownloader;
	private LayoutInflater mInflater;
	private boolean mIsRatingVisable = true;
	private PlayerController mPlayerController = null;
	private List<SongListItem> mRecommendSongListItemData;

	public MobileMusicRecommendListItemAdapter(Context paramContext,
			List<SongListItem> paramList)
	{
		logger.e("new a MobileMusicRecommendListItemAdapter");
		this.mInflater = LayoutInflater.from(paramContext);
		this.mRecommendSongListItemData = paramList;
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
		return this.mRecommendSongListItemData.size();
	}

	public Object getItem(int paramInt)
	{
		return this.mRecommendSongListItemData.get(paramInt);
	}

	public long getItemId(int paramInt)
	{
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
	{
		logger.v("getView() ---> Enter");
		ViewHolder localViewHolder;
		String str;
		Song localSong;
		SongListItem localSongListItem2;
		if (paramView == null)
		{
			localViewHolder = new ViewHolder();
			paramView = this.mInflater.inflate(
					R.layout.list_cell_recommend_list, null);
			localViewHolder.mAblumImage = ((ImageView) paramView
					.findViewById(R.id.ablum_img));
			localViewHolder.mImageDoblyForListCell = ((ImageView) paramView
					.findViewById(R.id.dobly_img_for_list_cell));
			localViewHolder.mSongName = ((TextView) paramView
					.findViewById(R.id.song_name));
			localViewHolder.mSongerName = ((TextView) paramView
					.findViewById(R.id.songer_name));
			localViewHolder.mSongRatingbar = ((ImageView) paramView
					.findViewById(R.id.song_ratingbar));
			localViewHolder.mBtnRecommendState = ((ImageView) paramView
					.findViewById(R.id.play_state));
			localViewHolder.mBtnRecommendAdd = ((ImageView) paramView
					.findViewById(R.id.btn_recommend_add));
			paramView.setTag(localViewHolder);
		} else
		{
			localViewHolder = (ViewHolder) paramView.getTag();
		}
		SongListItem localSongListItem1 = (SongListItem) this.mRecommendSongListItemData
				.get(paramInt);
		this.mImageDownloader.download(localSongListItem1.img,
				R.drawable.image_default_ablum_for_play_view,
				localViewHolder.mAblumImage, null);
		localViewHolder.mSongName.setText(localSongListItem1.songName);
		str = localSongListItem1.singerName;
		if ((str != null) && (!str.equals("<unknown>")))
		{
			localViewHolder.mSongerName.setText(str);
		} else
		{
			localViewHolder.mSongerName
					.setText(R.string.unknown_artist_name_db_controller);
		}
		if (!localSongListItem1.isdolby.equals("1"))
		{
			localViewHolder.mImageDoblyForListCell.setVisibility(0);
		} else
		{
			localViewHolder.mImageDoblyForListCell.setVisibility(4);
		}
		if (localSongListItem1.mMusicType != MusicType.ONLINEMUSIC.ordinal())
		{
			int j = setMarkLevel(localSongListItem1.point);
			if (j != 0)
			{
				localViewHolder.mSongRatingbar.setVisibility(0);
				localViewHolder.mSongRatingbar.setBackgroundResource(j);
			} else
			{
				localViewHolder.mSongRatingbar.setVisibility(8);
			}
		} else
		{
			localViewHolder.mSongRatingbar.setVisibility(0);
			localViewHolder.mSongRatingbar
					.setBackgroundResource(R.drawable.local_music_icon);
		}
		if (this.mIconrid != 0)
		{
			localViewHolder.mBtnRecommendAdd.setImageResource(this.mIconrid);
		} else
		{
			if (this.mIconrid == -1)
			{
				localViewHolder.mBtnRecommendAdd.setVisibility(8);
			}
		}
		localSong = this.mPlayerController.getCurrentPlayingItem();
		int i = this.mRecommendSongListItemData.size();
		localSongListItem2 = null;
		if (paramInt <= i)
		{
			localSongListItem2 = (SongListItem) this.mRecommendSongListItemData
					.get(paramInt);
		}
		if ((localSong != null) && (localSongListItem2 != null))
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
					localViewHolder.mBtnRecommendState.setVisibility(0);
				} else
				{
					localViewHolder.mBtnRecommendState.setVisibility(8);
				}
			} else
			{
				localViewHolder.mBtnRecommendState.setVisibility(0);
			}
		} else
		{
			localViewHolder.mBtnRecommendState.setVisibility(8);
		}

		// 每首歌对应的小的弹出小列表
		localViewHolder.mBtnRecommendAdd.setOnClickListener(this.mBtnClicker);
		localViewHolder.mBtnRecommendAdd.setTag(Integer.valueOf(paramInt));
		logger.v("getView() ---> Exit");
		return paramView;
	}

	public void releaseAdapterResource()
	{
		this.mInflater = null;
		if (this.mRecommendSongListItemData != null)
			this.mRecommendSongListItemData.clear();
		this.mController = null;
		this.mPlayerController = null;
		if (this.mImageDownloader != null)
		{
			this.mImageDownloader.clearCache();
			this.mImageDownloader = null;
		}
	}

	public void setBtnOnClickListener(View.OnClickListener paramOnClickListener)
	{
		this.mBtnClicker = paramOnClickListener;
	}

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
