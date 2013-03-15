package org.ming.ui.adapter;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.player.PlayerController;
import org.ming.util.MyLogger;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocalColumnListCursorAdapter extends CursorAdapter
{

	private static final MyLogger logger = MyLogger
			.getLogger("LocalColumnListCursorAdapter");
	int artistIndex;
	Cursor currentCusor;
	private android.view.View.OnClickListener mBtnClicker;
	private Context mContext;
	private Controller mController;
	private DBController mDBController;
	private int mIconrid;
	private LayoutInflater mInflater;
	private PlayerController mPlayerController;
	int mSubTile[];
	String subtileString;
	TextView subtitle;
	TextView title;
	String titleString;

	public LocalColumnListCursorAdapter(Context context, Cursor cursor)
	{
		super(context, cursor);
		mDBController = null;
		mPlayerController = null;
		mIconrid = 0;
		artistIndex = 0;
		initAdapter(context, cursor);
	}

	public LocalColumnListCursorAdapter(Context context, Cursor cursor, int i)
	{
		super(context, cursor, i);
		mDBController = null;
		mPlayerController = null;
		mIconrid = 0;
		artistIndex = 0;
		initAdapter(context, cursor);
	}

	public LocalColumnListCursorAdapter(Context context, Cursor cursor,
			boolean flag)
	{
		super(context, cursor, flag);
		mDBController = null;
		mPlayerController = null;
		mIconrid = 0;
		artistIndex = 0;
		initAdapter(context, cursor);
	}

	private void initAdapter(Context context, Cursor cursor)
	{
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mController = Controller.getInstance(MobileMusicApplication
				.getInstance());
		mPlayerController = mController.getPlayerController();
		if (cursor != null && cursor.getCount() > 0)
			artistIndex = cursor.getColumnIndexOrThrow("artist");
		mSubTile = new int[0];
		mDBController = mController.getDBController();
		currentCusor = cursor;
	}

	public void bindView(View view, Context context, Cursor cursor)
	{
		title = (TextView) view.findViewById(R.id.column_title);
		subtitle = (TextView) view.findViewById(R.id.column_subtitle);
		titleString = cursor.getString(artistIndex);
		if (titleString == null || titleString.equals("<unknown>"))
			title.setText(mContext
					.getText(R.string.unknown_artist_name_db_controller));
		else
			title.setText(titleString);
		if (mSubTile.length > cursor.getPosition())
		{
			Object aobj[] = new Object[1];
			aobj[0] = Integer.valueOf(mSubTile[cursor.getPosition()]);
			subtileString = context.getString(R.string.local_music_subtile,
					aobj);
		}
		if (subtileString == null || subtileString.equals("<unknown>"))
			subtitle.setText(mContext
					.getText(R.string.unknown_artist_name_db_controller));
		else
			subtitle.setText(subtileString);
	}

	public int getCurrentItemIdByPostion(int i)
	{
		int j;
		if (currentCusor != null && currentCusor.getCount() > i)
		{
			currentCusor.moveToPosition(i);
			j = currentCusor.getInt(currentCusor.getColumnIndexOrThrow("_id"));
		} else
		{
			j = -1;
		}
		return j;
	}

	public String getCurrentItemTitleByPostion(int i)
	{
		String s;
		if (currentCusor != null && currentCusor.getCount() > i)
		{
			currentCusor.moveToPosition(i);
			s = currentCusor.getString(artistIndex);
		} else
		{
			s = null;
		}
		return s;
	}

	public View newView(Context context, Cursor cursor, ViewGroup viewgroup)
	{
		return mInflater.inflate(R.layout.list_cell_localmusic_column_list,
				null);
	}

	public void setArtistSongCountList(int ai[])
	{
		mSubTile = ai;
	}

}
