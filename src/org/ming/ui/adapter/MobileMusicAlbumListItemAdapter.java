package org.ming.ui.adapter;

import java.util.List;

import org.ming.R;
import org.ming.center.download.UrlImageDownloader;
import org.ming.center.http.item.MusicListColumnItem;
import org.ming.util.MyLogger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MobileMusicAlbumListItemAdapter extends BaseAdapter
{
	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicAlbumListItemAdapter");
	private List<MusicListColumnItem> mAlbumListItemData;
	private UrlImageDownloader mImageDownloader;
	private LayoutInflater mInflater;
	private boolean mIsVisiable = false;

	public MobileMusicAlbumListItemAdapter(Context paramContext,
			List<MusicListColumnItem> paramList)
	{
		this.mInflater = LayoutInflater.from(paramContext);
		this.mAlbumListItemData = paramList;
		this.mImageDownloader = new UrlImageDownloader(paramContext);
	}

	public int getCount()
	{
		return this.mAlbumListItemData.size();
	}

	public Object getItem(int paramInt)
	{
		return this.mAlbumListItemData.get(paramInt);
	}

	public long getItemId(int paramInt)
	{
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
	{
		logger.v("getView() ---> Enter");
		View localView = null;
		if (paramInt >= 0)
		{
			int i = this.mAlbumListItemData.size();
			localView = null;
			if (i >= 0)
			{
				int j = this.mAlbumListItemData.size();
				localView = null;
				if (paramInt < j)
				{
					MusicListColumnItem localMusicListColumnItem = (MusicListColumnItem) this.mAlbumListItemData
							.get(paramInt);
					ViewHolder localViewHolder;
					if (paramView == null)
					{
						localViewHolder = new ViewHolder();
						paramView = this.mInflater.inflate(
								R.layout.list_cell_album_list_item, null);
						localViewHolder.column_bg = ((LinearLayout) paramView
								.findViewById(R.id.bg_album));
						localViewHolder.column_progress = ((ProgressBar) paramView
								.findViewById(R.id.album_progressbar));
						localViewHolder.column_icon = ((ImageView) paramView
								.findViewById(R.id.album));
						localViewHolder.column_name = ((TextView) paramView
								.findViewById(R.id.album_name));
						paramView.setTag(localViewHolder);
					} else
					{
						localViewHolder = (ViewHolder) paramView.getTag();
					}
					if (!localMusicListColumnItem.category_type.equals("")
							|| (!localMusicListColumnItem.img.equals(""))
							|| (!localMusicListColumnItem.title.equals(""))
							|| (!localMusicListColumnItem.url.equals("")))
					{
						localViewHolder.column_bg
								.setBackgroundResource(R.drawable.bg_album_list_add_data);
						localViewHolder.column_icon.setVisibility(8);
						localViewHolder.column_name.setVisibility(8);
						if (this.mIsVisiable)
						{
							localViewHolder.column_progress.setVisibility(0);
						} else
						{
							localViewHolder.column_progress.setVisibility(8);
						}
					}
					localViewHolder.column_bg
							.setBackgroundResource(R.drawable.bg_album_list_selector);
					localViewHolder.column_icon.setVisibility(0);
					localViewHolder.column_name.setVisibility(0);
					localViewHolder.column_progress.setVisibility(8);
					String str1 = localMusicListColumnItem.url;
					int k = str1.indexOf("groupcode=");
					String str2 = null;
					String str3;
					int m;
					if (k != -1)
					{
						str3 = str1.substring(k);
						m = str3.indexOf("&");
						if (m == -1)
						{
							str2 = str3.substring(10);
						} else
						{
							str2 = str3.substring(10, m);
						}
						this.mImageDownloader.download(
								localMusicListColumnItem.img,
								R.drawable.image_default_ablum_for_play_view,
								localViewHolder.column_icon, str2);
						localViewHolder.column_name
								.setText(localMusicListColumnItem.title);
					}
				}
			}
		}
		logger.v("getView() ---> Exit");
		localView = paramView;
		return localView;
	}

	public void releaseAdapterResource()
	{
		this.mInflater = null;
		if (this.mAlbumListItemData != null)
			this.mAlbumListItemData.clear();
		if (this.mImageDownloader != null)
			this.mImageDownloader.clearCache();
		this.mImageDownloader = null;
	}

	public void setProgressVisiable(boolean paramBoolean)
	{
		this.mIsVisiable = paramBoolean;
	}

	public final class ViewHolder
	{
		public LinearLayout column_bg;
		public ImageView column_icon;
		public TextView column_name;
		public ProgressBar column_progress;

		public ViewHolder()
		{}
	}
}