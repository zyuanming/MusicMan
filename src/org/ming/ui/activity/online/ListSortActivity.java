package org.ming.ui.activity.online;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.ming.R;
import org.ming.ui.view.MyLetterListView;
import org.ming.ui.view.MyLetterListView.OnTouchingLetterChangedListener;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListSortActivity extends Activity
{
	private String TAG = "ListSortActivity";
	private BaseAdapter adapter;
	private ListView personList;
	private TextView overlay;
	private MyLetterListView letterListView;
	private AsyncQueryHandler asyncQuery;
	private static final String NAME = "name", NUMBER = "number",
			SORT_KEY = "sort_key";
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private Handler handler;
	private OverlayThread overlayThread;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.v(TAG, "onCreate");
		personList = (ListView) findViewById(R.id.list_view);
		letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());

		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Uri uri = Uri.parse("content://com.android.contacts/data/phones");
		String[] projection = { "_id", "display_name", "data1", "sort_key" };
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");

	}

	// 异步查询联系人
	private class MyAsyncQueryHandler extends AsyncQueryHandler
	{

		public MyAsyncQueryHandler(ContentResolver cr)
		{
			super(cr);

		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor)
		{
			if (cursor != null && cursor.getCount() > 0)
			{
				List<ContentValues> list = new ArrayList<ContentValues>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++)
				{
					ContentValues cv = new ContentValues();
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					System.out.println(sortKey);
					if (number.startsWith("+86"))
					{
						cv.put(NAME, name);
						cv.put(NUMBER, number.substring(3)); // 去掉+86
						cv.put(SORT_KEY, sortKey);
					} else
					{
						cv.put(NAME, name);
						cv.put(NUMBER, number);
						cv.put(SORT_KEY, sortKey);
					}
					list.add(cv);
				}
				if (list.size() > 0)
				{
					setAdapter(list);
				}
			}
		}

	}

	private void setAdapter(List<ContentValues> list)
	{
		adapter = new ListAdapter(this, list);
		personList.setAdapter(adapter);

	}

	private class ListAdapter extends BaseAdapter
	{
		private LayoutInflater inflater;
		private List<ContentValues> list;

		public ListAdapter(Context context, List<ContentValues> list)
		{
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++)
			{
				// 当前汉语拼音首字母
				String currentStr = getAlpha(list.get(i).getAsString(SORT_KEY));
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
						.getAsString(SORT_KEY)) : " ";
				if (!previewStr.equals(currentStr))
				{
					String name = getAlpha(list.get(i).getAsString(SORT_KEY));
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public Object getItem(int position)
		{
			return list.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if (convertView == null)
			{
				convertView = inflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.number = (TextView) convertView
						.findViewById(R.id.number);
				convertView.setTag(holder);
			} else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			ContentValues cv = list.get(position);
			holder.name.setText(cv.getAsString(NAME));
			holder.number.setText(cv.getAsString(NUMBER));
			String currentStr = getAlpha(list.get(position).getAsString(
					SORT_KEY));// 当前字母
			String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
					position - 1).getAsString(SORT_KEY)) : " ";
			if (!previewStr.equals(currentStr))
			{
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
				// holder.number.setVisibility(View.GONE);
			} else
			{
				holder.alpha.setVisibility(View.GONE);
				// holder.number.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		private class ViewHolder
		{
			TextView alpha;
			TextView name;
			TextView number;
		}

	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay()
	{
		Log.v(TAG, "onCreate,initOverlay");
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	// 这个地方一定要加上，不然退出会报错的

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.removeView(overlay);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener
	{

		@Override
		public void onTouchingLetterChanged(final String s)
		{
			if (alphaIndexer.get(s) != null)
			{
				int position = alphaIndexer.get(s);
				personList.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1500);
			}
		}

	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable
	{

		@Override
		public void run()
		{
			overlay.setVisibility(View.GONE);
		}

	}

	// 获得汉语拼音首字母
	private String getAlpha(String str)
	{
		if (str == null)
		{
			return "#";
		}

		if (str.trim().length() == 0)
		{
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches())
		{
			return (c + "").toUpperCase();
		} else
		{
			return "#";
		}
	}

}
