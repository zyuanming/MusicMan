package org.ming.ui.adapter;

import java.util.List;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.player.PlayerController;
import org.ming.util.MyLogger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalSongListAdapter extends BaseAdapter
{
	private static final MyLogger logger = MyLogger
			.getLogger("LocalSongListAdapter");
	private View.OnClickListener mBtnClicker;
	private Context mContext;
	private Controller mController;
	private int mIconrid = 0;
	private LayoutInflater mInflater;
	private PlayerController mPlayerController = null;
	private List<Song> mRecommendSongListItemData;

	public LocalSongListAdapter(Context paramContext, List<Song> paramList)
	{
		this.mInflater = LayoutInflater.from(paramContext);
		this.mRecommendSongListItemData = paramList;
		this.mContext = paramContext;
		this.mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		this.mPlayerController = this.mController.getPlayerController();
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

	public View getView(int i, View view, ViewGroup viewgroup)
	{
		logger.v("getView() ---> Enter");
		ViewHolder viewholder;
		Song song;
		String s;
		Song song1;
		if (view == null)
		{
			viewholder = new ViewHolder();
			view = mInflater.inflate(R.layout.list_cell_localmusic, null);
			viewholder.song_name = (TextView) view.findViewById(R.id.song_name);
			viewholder.songer_name = (TextView) view
					.findViewById(R.id.songer_name);
			viewholder.btn_recommend_state = (ImageView) view
					.findViewById(R.id.play_state);
			viewholder.btn_recommend_add = (ImageView) view
					.findViewById(R.id.btn_recommend_add);
			view.setTag(viewholder);
		} else
		{
			viewholder = (ViewHolder) view.getTag();
		}
		song = (Song) mRecommendSongListItemData.get(i);
		viewholder.song_name.setText(song.mTrack);
		s = song.mArtist;
		if (s == null || s.equals("<unknown>"))
			viewholder.songer_name.setText(mContext
					.getText(R.string.unknown_artist_name_db_controller));
		else
			viewholder.songer_name.setText(song.mArtist);
		song1 = mPlayerController.getCurrentPlayingItem();
		if (song1 != null && song.mMusicType == MusicType.LOCALMUSIC.ordinal()
				&& song1.mMusicType == MusicType.LOCALMUSIC.ordinal())
		{
			if (song.mUrl.equalsIgnoreCase(song1.mUrl))
				viewholder.btn_recommend_state.setVisibility(0);
			else
				viewholder.btn_recommend_state.setVisibility(8);
		} else
		{
			viewholder.btn_recommend_state.setVisibility(8);
		}
		viewholder.btn_recommend_add.setOnClickListener(mBtnClicker);
		viewholder.btn_recommend_add.setTag(Integer.valueOf(i));
		logger.v("getView() ---> Exit");
		return view;
	}

	public void setBtnOnClickListener(View.OnClickListener paramOnClickListener)
	{
		this.mBtnClicker = paramOnClickListener;
	}

	public final class ViewHolder
	{
		public ImageView btn_recommend_add;
		public ImageView btn_recommend_state;
		public TextView song_name;
		public TextView songer_name;

		public ViewHolder()
		{}
	}
}