package org.ming.ui.view;

import org.ming.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBarView extends RelativeLayout
{
	private ImageView mBtnRight;
	private Context mContext;
	private Activity mCurrentActivity;
	private ImageView mImgBtnLeft;
	private ImageView mImgBtnRight;
	private LayoutInflater mLayoutInflater;
	private View.OnClickListener mLeftOnclick = null;
	private View.OnClickListener mRightOnclick = null;
	private TextView mTxtTitle;
	private View.OnClickListener rigetbtn_onclick = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			// Intent localIntent = new Intent(TitleBarView.this.mContext,
			// MusicOnlineMusicSearchActivity.class);
			// TitleBarView.this.mContext.startActivity(localIntent);
		}
	};

	public TitleBarView(Context paramContext)
	{
		super(paramContext);
		this.mContext = paramContext;
		initialize();
	}

	public TitleBarView(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		this.mContext = paramContext;
		initialize();
	}

	public void initialize()
	{
		this.mLayoutInflater = LayoutInflater.from(this.mContext);
		removeAllViews();
		RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(
				-1, -2);
		LinearLayout localLinearLayout = (LinearLayout) this.mLayoutInflater
				.inflate(R.layout.title_bar_view, null);
		addView(localLinearLayout, localLayoutParams);
		this.mImgBtnLeft = ((ImageView) localLinearLayout
				.findViewById(R.id.btn_left));
		this.mTxtTitle = ((TextView) localLinearLayout
				.findViewById(R.id.title_text));
		this.mImgBtnRight = ((ImageView) localLinearLayout
				.findViewById(R.id.btn_right));
		this.mBtnRight = ((ImageView) localLinearLayout
				.findViewById(R.id.btn_rightbutton));
	}

	public void setButtons(int paramInt)
	{
		switch (paramInt)
		{
		default:
			if (this.mLeftOnclick != null)
				this.mImgBtnLeft.setOnClickListener(this.mLeftOnclick);
			break;
		case 0:
			this.mImgBtnRight.setVisibility(View.INVISIBLE);
			break;
		case 1:
			if (this.mRightOnclick != null)
			{
				this.mImgBtnRight.setOnClickListener(this.mRightOnclick);
				break;
			}
			this.mImgBtnRight.setOnClickListener(this.rigetbtn_onclick);
			break;
		case 2:
			this.mBtnRight.setVisibility(0);
			this.mImgBtnRight.setVisibility(8);
			break;
		}

		this.mImgBtnLeft.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				TitleBarView.this.mCurrentActivity.finish();
			}
		});
	}

	public void setCurrentActivity(Activity paramActivity)
	{
		this.mCurrentActivity = paramActivity;
	}

	public void setLeftBtnImage(int paramInt)
	{
		this.mImgBtnLeft.setImageResource(paramInt);
	}

	public void setLeftBtnOnlickListner(
			View.OnClickListener paramOnClickListener)
	{
		this.mLeftOnclick = paramOnClickListener;
		this.mImgBtnLeft.setOnClickListener(paramOnClickListener);
	}

	public void setRightBtnImage(int paramInt)
	{
		this.mBtnRight.setBackgroundResource(paramInt);
	}

	public void setRightBtnOnlickListner(
			View.OnClickListener paramOnClickListener)
	{
		this.mRightOnclick = paramOnClickListener;
	}

	public void setTitle(int paramInt)
	{
		this.mTxtTitle.setText(paramInt);
	}

	public void setTitle(String paramString)
	{
		this.mTxtTitle.setText(paramString);
	}

	public void setrightBtnListner(View.OnClickListener paramOnClickListener)
	{
		this.mBtnRight.setOnClickListener(paramOnClickListener);
	}
}