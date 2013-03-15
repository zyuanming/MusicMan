package org.ming.ui.adapter;

import java.util.List;

import org.ming.R;
import org.ming.center.download.UrlImageDownloader;
import org.ming.center.http.item.ContentItem;
import org.ming.util.MyLogger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MobileMusicContentListItemAdapter extends BaseAdapter
{
	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicContentListItemAdapter");
	private List<ContentItem> mContentItemData;
	private UrlImageDownloader mImageDownloader;
	private LayoutInflater mInflater;

	public MobileMusicContentListItemAdapter(Context paramContext,
			List<ContentItem> paramList)
	{
		this.mInflater = LayoutInflater.from(paramContext);
		this.mContentItemData = paramList;
		this.mImageDownloader = new UrlImageDownloader(paramContext);
	}

	public int getCount()
	{
		return this.mContentItemData.size();
	}

	public Object getItem(int paramInt)
	{
		return this.mContentItemData.get(paramInt);
	}

	public long getItemId(int paramInt)
	{
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
	{
		logger.v("getView() ---> Enter");
		ViewHolder localViewHolder;
		if (paramView == null)
		{
			localViewHolder = new ViewHolder();
			paramView = this.mInflater.inflate(
					R.layout.list_cell_information_list, null);
			localViewHolder.content_icon = ((ImageView) paramView
					.findViewById(R.id.content_icon));
			localViewHolder.content_title = ((TextView) paramView
					.findViewById(R.id.content_title));
			localViewHolder.content_publish_time = ((TextView) paramView
					.findViewById(R.id.content_publish_time));
			paramView.setTag(localViewHolder);
		} else
		{
			localViewHolder = (ViewHolder) paramView.getTag();
		}
		this.mImageDownloader.download(
				((ContentItem) this.mContentItemData.get(paramInt)).img,
				R.drawable.image_default_ablum_for_play_view,
				localViewHolder.content_icon,
				((ContentItem) this.mContentItemData.get(paramInt)).groupcode);
		localViewHolder.content_title
				.setText(((ContentItem) this.mContentItemData.get(paramInt)).title);
		localViewHolder.content_publish_time
				.setText(((ContentItem) this.mContentItemData.get(paramInt)).publish_time);
		logger.v("getView() ---> Exit");
		return paramView;
	}

	public void releaseAdapterResource()
	{
		this.mInflater = null;
		if (this.mImageDownloader != null)
		{
			this.mImageDownloader.clearCache();
			this.mImageDownloader = null;
		}
		if (this.mContentItemData != null)
			this.mContentItemData.clear();
	}

	public final class ViewHolder
	{
		public ImageView content_icon;
		public TextView content_publish_time;
		public TextView content_title;

		public ViewHolder()
		{}
	}
}