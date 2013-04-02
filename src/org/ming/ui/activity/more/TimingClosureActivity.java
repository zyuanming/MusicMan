package org.ming.ui.activity.more;

import java.util.Calendar;

import org.ming.R;
import org.ming.center.MobileMusicApplication;
import org.ming.ui.view.TitleBarView;
import org.ming.util.AlarmReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class TimingClosureActivity extends Activity
{
	private static final String CLOSETIME = "CLOSETIME";
	private static final String SPNAME = "closetimesharedpre";
	private String closeTime = null;
	private Intent intent;
	private SharedPreferences mCloseTime;
	private View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		private void closeAlarm()
		{
			((AlarmManager) TimingClosureActivity.this
					.getSystemService("alarm"))
					.cancel(TimingClosureActivity.this.p_intent);
		}

		private void closes(int paramAnonymousInt)
		{
			Calendar localCalendar = Calendar.getInstance();
			localCalendar.setTimeInMillis(System.currentTimeMillis());
			localCalendar.add(13, paramAnonymousInt);
			((AlarmManager) TimingClosureActivity.this
					.getSystemService("alarm")).set(0,
					localCalendar.getTimeInMillis(),
					TimingClosureActivity.this.p_intent);
		}

		private void setCb_Cloure(View paramAnonymousView)
		{
			switch (paramAnonymousView.getId())
			{
			case R.id.time_text_ten_minutes:
			case R.id.time_text_twenty_minutes:
			case R.id.time_text_thirty_minutes:
			case R.id.time_text_forty_minutes:
			case R.id.time_text_fifty_minutes:
			case R.id.time_text_noe_hours:
			default:
			case R.id.time_cancel:
				closeAlarm();
				mTimeCancel.setChecked(true);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(false);
				closeTime = mTimeTextCancel.getText().toString();
				break;
			case R.id.time_ten_minutes:
				closeAlarm();
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(true);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(false);
				closeTime = mTimeTextTenMinutes.getText().toString();
				break;
			case R.id.time_twenty_minutes:
				closeAlarm();
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(true);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(false);
				closeTime = mTimeTextTwentyMinutes.getText().toString();
				break;
			case R.id.time_thirty_minutes:
				closeAlarm();
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(true);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(false);
				closeTime = mTimeTextThirtyMinutes.getText().toString();
				break;
			case R.id.time_forty_minutes:
				closeAlarm();
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(true);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(false);
				closeTime = mTimeTextFortyMinutes.getText().toString();
				break;
			case R.id.time_fifty_minutes:
				closeAlarm();
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(true);
				mTimeNoeHours.setChecked(false);
				closeTime = mTimeTextFiftyMinutes.getText().toString();
				break;
			case R.id.time_noe_hours:
				closeAlarm();
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(true);
				closeTime = mTimeTextNoeHours.getText().toString();
				break;
			}
			mCloseTime.edit().putString(CLOSETIME, closeTime).commit();
			return;
		}

		public void onClick(View view)
		{
			setCb_Cloure(view);
			if (closeTime != null && !"".equals(closeTime.trim()))
				if (getResources().getString(R.string.timing_ten_minutes_close)
						.equals(closeTime.trim()))
					closes(600);
				else if (getResources().getString(
						R.string.timing_twenty_minutes_close).equals(
						closeTime.trim()))
					closes(1200);
				else if (getResources().getString(
						R.string.timing_thirty_minutes_close).equals(
						closeTime.trim()))
					closes(1800);
				else if (getResources().getString(
						R.string.timing_forty_minutes_close).equals(
						closeTime.trim()))
					closes(2400);
				else if (getResources().getString(
						R.string.timing_fifty_minutes_close).equals(
						closeTime.trim()))
					closes(3000);
				else if (getResources().getString(
						R.string.timing_one_hours_close).equals(
						closeTime.trim()))
					closes(3600);
				else
					closeAlarm();
		}
	};
	private CheckBox mTimeCancel;
	private CheckBox mTimeFiftyMinutes;
	private CheckBox mTimeFortyMinutes;
	private CheckBox mTimeNoeHours;
	private CheckBox mTimeTenMinutes;
	private TextView mTimeTextCancel;
	private TextView mTimeTextFiftyMinutes;
	private TextView mTimeTextFortyMinutes;
	private TextView mTimeTextNoeHours;
	private TextView mTimeTextTenMinutes;
	private TextView mTimeTextThirtyMinutes;
	private TextView mTimeTextTwentyMinutes;
	private CheckBox mTimeThirtyMinutes;
	private CheckBox mTimeTwentyMinutes;
	private PendingIntent p_intent;
	private TitleBarView titleBar;

	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_more_timing_closure_layout);
		intent = new Intent(MobileMusicApplication.getInstance(),
				AlarmReceiver.class);
		p_intent = PendingIntent.getBroadcast(
				MobileMusicApplication.getInstance(), 0, intent, 0);
		titleBar = (TitleBarView) findViewById(R.id.title_view_timing);
		titleBar.setCurrentActivity(this);
		titleBar.setTitle(R.string.timingclosure);
		titleBar.setButtons(0);
		mTimeCancel = (CheckBox) findViewById(R.id.time_cancel);
		mTimeTenMinutes = (CheckBox) findViewById(R.id.time_ten_minutes);
		mTimeTwentyMinutes = (CheckBox) findViewById(R.id.time_twenty_minutes);
		mTimeThirtyMinutes = (CheckBox) findViewById(R.id.time_thirty_minutes);
		mTimeFortyMinutes = (CheckBox) findViewById(R.id.time_forty_minutes);
		mTimeFiftyMinutes = (CheckBox) findViewById(R.id.time_fifty_minutes);
		mTimeNoeHours = (CheckBox) findViewById(R.id.time_noe_hours);
		mTimeCancel.setOnClickListener(mOnClickListener);
		mTimeTenMinutes.setOnClickListener(mOnClickListener);
		mTimeTwentyMinutes.setOnClickListener(mOnClickListener);
		mTimeThirtyMinutes.setOnClickListener(mOnClickListener);
		mTimeFortyMinutes.setOnClickListener(mOnClickListener);
		mTimeFiftyMinutes.setOnClickListener(mOnClickListener);
		mTimeNoeHours.setOnClickListener(mOnClickListener);
		mTimeTextCancel = (TextView) findViewById(R.id.time_text_cancel);
		mTimeTextTenMinutes = (TextView) findViewById(R.id.time_text_ten_minutes);
		mTimeTextTwentyMinutes = (TextView) findViewById(R.id.time_text_twenty_minutes);
		mTimeTextThirtyMinutes = (TextView) findViewById(R.id.time_text_thirty_minutes);
		mTimeTextFortyMinutes = (TextView) findViewById(R.id.time_text_forty_minutes);
		mTimeTextFiftyMinutes = (TextView) findViewById(R.id.time_text_fifty_minutes);
		mTimeTextNoeHours = (TextView) findViewById(R.id.time_text_noe_hours);
		mCloseTime = getSharedPreferences("closetimesharedpre", 0);
		closeTime = mCloseTime.getString("CLOSETIME", mTimeTextCancel.getText()
				.toString());
		if (!closeTime.equals(mTimeTextCancel.getText().toString()))
		{
			if (closeTime.equals(mTimeTextTenMinutes.getText().toString()))
			{
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(true);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(false);
			} else if (closeTime.equals(mTimeTextTwentyMinutes.getText()
					.toString()))
			{
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(true);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(false);
			} else if (closeTime.equals(mTimeTextThirtyMinutes.getText()
					.toString()))
			{
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(true);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(false);
			} else if (closeTime.equals(mTimeTextFortyMinutes.getText()
					.toString()))
			{
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(true);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(false);
			} else if (closeTime.equals(mTimeTextFiftyMinutes.getText()
					.toString()))
			{
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(true);
				mTimeNoeHours.setChecked(false);
			} else if (closeTime.equals(mTimeTextNoeHours.getText().toString()))
			{
				mTimeCancel.setChecked(false);
				mTimeTenMinutes.setChecked(false);
				mTimeTwentyMinutes.setChecked(false);
				mTimeThirtyMinutes.setChecked(false);
				mTimeFortyMinutes.setChecked(false);
				mTimeFiftyMinutes.setChecked(false);
				mTimeNoeHours.setChecked(true);
			}
		} else
		{
			this.mTimeCancel.setChecked(true);
			this.mTimeTenMinutes.setChecked(false);
			this.mTimeTwentyMinutes.setChecked(false);
			this.mTimeThirtyMinutes.setChecked(false);
			this.mTimeFortyMinutes.setChecked(false);
			this.mTimeFiftyMinutes.setChecked(false);
			this.mTimeNoeHours.setChecked(false);
		}
	}
}