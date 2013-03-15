package org.ming.ui.adapter;

import java.util.List;

import org.ming.R;
import org.ming.center.download.UrlImageDownloader;
import org.ming.center.http.item.MusicListColumnItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MobileMusicColumnListItemAdapter extends BaseAdapter
{
	private List<MusicListColumnItem> mColumnListItemData;
	private UrlImageDownloader mImageDownloader;
	private LayoutInflater mInflater;

	public MobileMusicColumnListItemAdapter(Context paramContext,
			List<MusicListColumnItem> paramList)
	{
		this.mInflater = LayoutInflater.from(paramContext);
		this.mColumnListItemData = paramList;
		this.mImageDownloader = new UrlImageDownloader(paramContext);
	}

	public int getCount()
	{
		return this.mColumnListItemData.size();
	}

	public Object getItem(int paramInt)
	{
		return this.mColumnListItemData.get(paramInt);
	}

	public long getItemId(int paramInt)
	{
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
	{
		ViewHolder localViewHolder;
		if (paramView == null)
		{
			localViewHolder = new ViewHolder();
			paramView = this.mInflater.inflate(R.layout.list_cell_column_list,
					null);
			localViewHolder.column_icon = ((ImageView) paramView
					.findViewById(R.id.column_icon));
			localViewHolder.column_name = ((TextView) paramView
					.findViewById(R.id.column_name));
			paramView.setTag(localViewHolder);
		} else
		{
			localViewHolder = (ViewHolder) paramView.getTag();
		}
		this.mImageDownloader
				.download(((MusicListColumnItem) this.mColumnListItemData
						.get(paramInt)).img,
						R.drawable.image_default_ablum_for_list,
						localViewHolder.column_icon, null);
		localViewHolder.column_name
				.setText(((MusicListColumnItem) this.mColumnListItemData
						.get(paramInt)).title);
		return paramView;
	}

	public void releaseAdapterResource()
	{
		this.mInflater = null;
		if (this.mColumnListItemData != null)
		{
			this.mColumnListItemData.clear();
			this.mImageDownloader = null;
		}
		if (this.mImageDownloader != null)
			this.mImageDownloader.clearCache();
	}

	public final class ViewHolder
	{
		public ImageView column_icon;
		public TextView column_name;

		public ViewHolder()
		{}
	}
}
