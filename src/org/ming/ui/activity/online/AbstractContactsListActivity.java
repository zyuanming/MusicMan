package org.ming.ui.activity.online;

import java.util.List;

import org.ming.R;
import org.ming.ui.view.TitleBarView;

import android.app.ListActivity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public abstract class AbstractContactsListActivity extends ListActivity
		implements android.widget.AbsListView.OnScrollListener,
		android.view.View.OnClickListener
{
	class Contact
	{
		public boolean isCheck;
		public String name;
		public String number;
		public int type;
	}

	private class ContactsListGroupQueryHandler extends AsyncQueryHandler
	{

		protected void onQueryComplete(int i, Object obj, Cursor cursor)
		{
			switch (i)
			{
			case 0:
				initDatas(cursor);
				mContactsListGroupQueryHandler = null;
				break;
			default:
				return;
			}
		}

		public ContactsListGroupQueryHandler(ContentResolver contentresolver)
		{
			super(contentresolver);
		}
	}

	private final class RemoveWindow implements Runnable
	{
		public void run()
		{
			removeWindow();
		}
	}

	protected static final int COLUMN_DISPLAY_NAME = 3;
	protected static final int COLUMN_ID = 0;
	protected static final int COLUMN_NUMBER = 1;
	protected static final int COLUMN_TYPE = 2;
	protected static final int CONTACTS_GROUP_TOKEN = 2;
	protected static final int CONTACTS_QUERY_TOKEN = 0;
	protected static final int CONTACTS_SELECT_ALL_TOKEN = 1;
	protected static final String MCALLPROJECTIONSTRINGS[] = { "_id", "number",
			"numbertype", "name" };
	private static final int MENU_CANCEL_MODE = 2;
	private static final int MENU_MULTI_MODE = 1;
	protected static final String MGROUPCOUNTSTRINGS[] = { "_id", "summ_count",
			"title", "system_id", "_id" };
	protected static final String MGROUPSTRINGS[] = { "_id", "title",
			"system_id", "sourceid", "summ_count" };
	protected static final String MPROJECTIONSTRINGS[] = { "_id", "data1",
			"data2", "display_name", "starred" };
	protected boolean isClearAll;
	protected boolean isScrool;
	protected boolean isSelectAll;
	protected Button mCancelBtn;
	private ContactsListGroupQueryHandler mContactsListGroupQueryHandler;
	protected TextView mDialogText;
	Handler mHandler;
	protected LayoutInflater mInflater;
	protected Button mOkBtn;
	protected String mPrevLetter;
	protected boolean mReady;
	protected RemoveWindow mRemoveWindow;
	protected Button mSelectAllBtn;
	protected CheckBox mSelectAllCheckBox;
	protected boolean mShowing;
	private TitleBarView mTitleBar;
	protected WindowManager mWindowManager;

	public static String getDisplayNameForPhoneType(Context context, int i)
	{
		String s = "";
		switch (i)
		{
		case 0:
		case 7:
			s = context.getString(R.string.type_other);
			break;
		case 1:
			s = context.getString(R.string.type_home);
			break;
		case 2:
			s = context.getString(R.string.type_mobile);
			break;
		case 3:
		case 17:
			s = context.getString(R.string.type_work);
			break;
		case 4:
		case 5:
			s = context.getString(R.string.type_fax);
			break;
		case 6:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		default:
			return s;
		}
		return s;
	}

	public abstract List getCache();

	public abstract void initDatas(Cursor cursor);

	public abstract void initViews();

	public void onClick(View view)
	{
		int i = view.getId();
		switch (i)
		{
		case R.id.btn_select_contacts_cancel:
		case R.id.btn_select_contacts_checkall:
		default:
			return;
		case R.id.btn_select_contacts_ok:
			finish();
			break;

		}
	}

	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(5);
		requestWindowFeature(1);
		mRemoveWindow = new RemoveWindow();
		mHandler = new Handler();
		mPrevLetter = "";
		isScrool = false;
		isSelectAll = false;
		setContentView(R.layout.activity_online_music_abstract_contacts_list_layout);
		mInflater = LayoutInflater.from(this);
		setProgressBarIndeterminateVisibility(true);
		getListView().setOnScrollListener(this);
		mWindowManager = (WindowManager) getSystemService("window");
		mDialogText = (TextView) ((LayoutInflater) getSystemService("layout_inflater"))
				.inflate(R.layout.list_position, null);
		mDialogText.setVisibility(4);
		mHandler.post(new Runnable()
		{
			public void run()
			{
				LayoutParams layoutparams = new LayoutParams(-2, -2, 2, 24, -3);
				mWindowManager.addView(mDialogText, layoutparams);
			}
		});
		mOkBtn = (Button) findViewById(R.id.btn_select_contacts_ok);
		mCancelBtn = (Button) findViewById(R.id.btn_select_contacts_cancel);
		mSelectAllBtn = (Button) findViewById(R.id.btn_select_contacts_checkall);
		mTitleBar = (TitleBarView) findViewById(R.id.title_view);
		mTitleBar.setCurrentActivity(this);
		mTitleBar.setButtons(0);
		mTitleBar.setTitle(R.string.activity_contact_title);
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		mSelectAllBtn.setOnClickListener(this);
		initViews();
	}

	protected void onDestroy()
	{
		super.onDestroy();
		mReady = false;
		isScrool = false;
	}

	public boolean onKeyDown(int i, KeyEvent keyevent)
	{
		switch (i)
		{
		case 4: // '\004'
		default:
			return super.onKeyDown(i, keyevent);
		}
	}

	protected void onPause()
	{
		super.onPause();
		removeWindow();
		mReady = false;
		isScrool = false;
	}

	public void onScroll(AbsListView abslistview, int i, int j, int k)
	{
		// int = -1 + (i + j);
		if (mReady && isScrool)
		{
			int l;
			int i1;
			String s;
			String s1;
			if (j == 0)
				l = 5;
			else
				l = j / 2;
			i1 = i + l;
			if (getCache().size() <= i1)
				i1 = -1 + getCache().size();
			s = (String) getCache().get(i1);
			if (s == null && "".equals(s))
				s1 = mPrevLetter;
			else
				s1 = s.substring(0, 1);
			if (!mShowing && s1 != mPrevLetter)
			{
				mShowing = true;
				mDialogText.setVisibility(0);
			}
			mDialogText.setText(s1);
			mHandler.removeCallbacks(mRemoveWindow);
			mHandler.postDelayed(mRemoveWindow, 1000L);
			mPrevLetter = s1;
		}
	}

	public void onScrollStateChanged(AbsListView abslistview, int i)
	{
		isScrool = true;
	}

	protected Cursor queryPhoneNumbers(long l)
	{
		Uri uri = Uri.withAppendedPath(ContentUris.withAppendedId(
				android.provider.ContactsContract.Contacts.CONTENT_URI, l),
				"data");
		Cursor cursor = getContentResolver().query(uri,
				new String[] { "_id", "data1", "is_super_primary" },
				"mimetype=?",
				new String[] { "vnd.android.cursor.item/phone_v2" }, null);
		if (cursor == null || !cursor.moveToFirst())
			cursor = null;
		return cursor;
	}

	public void removeWindow()
	{
		if (mShowing)
		{
			mShowing = false;
			mDialogText.setVisibility(4);
		}
	}

	public void startQuery()
	{
		if (mContactsListGroupQueryHandler != null)
		{
			mContactsListGroupQueryHandler.cancelOperation(0);
			mContactsListGroupQueryHandler = null;
		}
		mContactsListGroupQueryHandler = new ContactsListGroupQueryHandler(
				getContentResolver());
		mContactsListGroupQueryHandler.startQuery(0, null,
				android.provider.ContactsContract.Groups.CONTENT_SUMMARY_URI,
				MGROUPSTRINGS, null, null, null);
	}

}
