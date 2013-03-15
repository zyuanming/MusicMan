package org.ming.ui.activity.online;

import java.util.ArrayList;
import java.util.List;

import org.ming.R;
import org.ming.center.MobileMusicApplication;
import org.ming.util.MyLogger;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsAllActivity extends AbstractContactsListActivity implements
		View.OnClickListener
{
	private class ContactsListAdapter extends CursorAdapter
	{
		public ContactsListAdapter(Context context, Cursor cursor)
		{
			super(context, cursor);
		}

		public void bindView(View view, Context context, Cursor cursor)
		{
			logger.v("bindView(View, Context, Cursor) ---> Enter");
			String s = cursor.getString(3);
			((TextView) view.findViewById(R.id.contact_name)).setText(s);
			CheckBox checkbox = (CheckBox) view
					.findViewById(android.R.id.checkbox); // 0x1020001
			// checkbox.setOnCheckedChangeListener(null);
			if (isMulit)
				checkbox.setChecked(true);
			final String number = cursor.getString(1);
			final String name = s;
			int i = cursor.getInt(2);
			((TextView) view.findViewById(R.id.address)).setText(number);
			((TextView) view.findViewById(R.id.type))
					.setText(ContactsAllActivity.getDisplayNameForPhoneType(
							ContactsAllActivity.this, i));
			if (cacheCheckNums.contains(number))
				checkbox.setChecked(true);
			else
				checkbox.setChecked(false);
			checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked)
				{

					mReady = false;
					if (isChecked)
					{
						cacheCheckNums.add(number);
						cacheCheckNames.add(name);
					} else
					{
						cacheCheckNames.remove(cacheCheckNums.indexOf(number));
						cacheCheckNums.remove(number);
					}
					notifyDataSetChanged();
					if (cacheNames.size() == cacheCheckNames.size()
							&& cacheNums.size() == cacheCheckNums.size())
					{
						mCancelBtn.setVisibility(0);
						mSelectAllBtn.setVisibility(8);
					} else
					{
						mCancelBtn.setVisibility(8);
						mSelectAllBtn.setVisibility(0);
					}
				}
			});
			logger.v("bindView(View, Context, Cursor) ---> Exit");
		}

		public View newView(Context context, Cursor cursor, ViewGroup viewgroup)
		{
			return mInflater.inflate(R.layout.list_cell_contact_item,
					viewgroup, false);
		}
	}

	private class ContactsListQueryHandler extends AsyncQueryHandler
	{

		public ContactsListQueryHandler(ContentResolver cr)
		{
			super(cr);
		}

		/**
		 * 查询完毕后自动调用
		 */
		protected void onQueryComplete(int i, Object obj, Cursor cursor)
		{
			logger.v("onQueryComplete(int, Object, Cursor) ---> Enter");
			switch (i)
			{
			case 0:
				fillData(cursor);
				if (mContactsListAdapter != null)
					mContactsListAdapter.changeCursor(cursor);
				setProgressBarIndeterminateVisibility(false);
				break;
			case 1:
				while (cursor.moveToNext())
					;
				setProgressBarIndeterminateVisibility(false);
				break;
			default:
				logger.v("onQueryComplete(int, Object, Cursor) ---> Exit");
				return;
			}
		}
	}

	protected static List allContactNumes = new ArrayList();
	protected static List cacheCheckNames = new ArrayList();
	protected static List cacheCheckNums = new ArrayList();
	private static final MyLogger logger = MyLogger
			.getLogger("ContactsAllActivity");
	protected static List notInContactNums = new ArrayList();
	protected List cacheNames = new ArrayList();
	protected List cacheNums = new ArrayList();
	private boolean isMulit = false;
	private ContactsListAdapter mContactsListAdapter;
	private ContactsListQueryHandler mContactsListQueryHandler;

	private void fillData(Cursor cursor)
	{
		logger.v("fillData(Cursor) ---> Enter");
		String s;
		String s1;
		String s2;
		if (cursor == null)
		{
			mReady = true;
			if (cacheCheckNums.size() <= 0)
			{
				logger.v("fillData(Cursor) ---> Exit");
				return;
			} else
			{
				for (int i = 0; i < cacheCheckNums.size(); i++)
				{
					s = (String) cacheCheckNums.get(i);
					if (!cacheNums.contains(s))
					{
						cacheCheckNames.remove(cacheCheckNums.indexOf(s));
						cacheCheckNums.remove(s);
						notInContactNums.add(s);
						i = 0;
					}
				}
				if (cacheNames.size() == cacheCheckNames.size()
						&& cacheNums.size() == cacheCheckNums.size())
				{
					mCancelBtn.setVisibility(0);
					mSelectAllBtn.setVisibility(8);
				} else
				{
					mCancelBtn.setVisibility(8);
					mSelectAllBtn.setVisibility(0);
				}
			}
		} else
		{
			allContactNumes.clear();
			while (cursor.moveToNext())
			{
				allContactNumes.add(cursor.getString(3));
				s1 = cursor.getString(1);
				if (!cacheNums.contains(s1))
				{
					cacheNums.add(s1);
					s2 = cursor.getString(3);
					cursor.getString(cursor.getColumnIndex("_id"));
					cacheNames.add(s2);
					MobileMusicApplication.getInstance()
							.getmAllNumAndNameCache().put(s1, s2);
				}
			}
		}
	}

	public List getCache()
	{
		return allContactNumes;
	}

	public void initDatas(Cursor cursor)
	{
	}

	public void initViews()
	{
		logger.v("initViews() ---> Enter");
		mContactsListQueryHandler = new ContactsListQueryHandler(
				getContentResolver());
		mContactsListQueryHandler.startQuery(0, null,
				android.provider.ContactsContract.Data.CONTENT_URI,
				MPROJECTIONSTRINGS,
				"mimetype='vnd.android.cursor.item/phone_v2'", null,
				"display_name COLLATE LOCALIZED ASC ");
		mContactsListAdapter = new ContactsListAdapter(this, null);
		setListAdapter(mContactsListAdapter);
		logger.v("initViews() ---> Exit");
	}

	public void onClick(View view)
	{
		logger.v("onClick(View) ---> Enter");
		int i = view.getId();
		switch (i)
		{
		case R.id.btn_select_contacts_cancel: // 60
			mCancelBtn.setVisibility(View.GONE);
			mSelectAllBtn.setVisibility(View.VISIBLE);
			cacheCheckNums.clear();
			cacheCheckNames.clear();
			mContactsListAdapter.notifyDataSetChanged();
			break;
		case R.id.btn_select_contacts_checkall:
			mCancelBtn.setVisibility(View.VISIBLE);
			mSelectAllBtn.setVisibility(View.GONE);
			cacheCheckNums.clear();
			cacheCheckNums.addAll(cacheNums);
			cacheCheckNames.clear();
			cacheCheckNames.addAll(cacheNames);
			mContactsListAdapter.notifyDataSetChanged();
			break;
		case R.id.btn_select_contacts_ok:
			Intent intent;
			intent = new Intent();
			if (cacheCheckNums.size() + notInContactNums.size() > 240
					|| cacheCheckNames.size() + notInContactNums.size() > 240)
				Toast.makeText(this,
						getText(R.string.phone_number_recommend_more_240), 0)
						.show();
			cacheCheckNames.addAll(notInContactNums);
			cacheCheckNums.addAll(notInContactNums);
			intent.putStringArrayListExtra("CONTACTNAME",
					(ArrayList) cacheCheckNames);
			intent.putStringArrayListExtra("CONTACTNUMBER",
					(ArrayList) cacheCheckNums);
			setResult(0, intent);
			finish();
			break;
		default:
			logger.v("onClick(View) ---> Exit");
			return;
		}
	}

	public void onCreate(Bundle bundle)
	{
		logger.v("onCreate(Bundle) ---> Enter");
		Intent intent = getIntent();
		cacheCheckNames = intent.getStringArrayListExtra("CONTACTNAME");
		cacheCheckNums = intent.getStringArrayListExtra("CONTACTNUMBER");
		super.onCreate(bundle);
		logger.v("onCreate(Bundle) ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		if (cacheNames != null && cacheNames.size() > 0)
			mReady = true;
		initViews();
		mContactsListAdapter.notifyDataSetChanged();
		logger.v("onResume() ---> Exit");
	}

	protected void onStop()
	{
		logger.v("onStop() ---> Enter");
		notInContactNums.clear();
		super.onStop();
		logger.v("onStop() ---> Exit");
	}
}
