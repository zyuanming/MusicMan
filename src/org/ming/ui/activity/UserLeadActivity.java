package org.ming.ui.activity;

import org.ming.R;
import org.ming.ui.view.MyViewGroup;
import org.ming.ui.view.PageControlView;
import org.ming.util.MyLogger;
import org.ming.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

public class UserLeadActivity extends Activity
{
	private static final MyLogger logger = MyLogger
			.getLogger("UserLeadActivity");
	private MyViewGroup viewGroup;
	private PageControlView pageControl;

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);

		logger.v("super.onCreate() ---> Enter");
		setContentView(R.layout.user_lead);

		logger.v("setContentView(R.layout.user_lead) ---> Enter");
		viewGroup = (MyViewGroup) findViewById(R.id.gallery_lead);

		ImageView imageView = new ImageView(this);
		imageView.setImageDrawable(getResources().getDrawable(
				R.drawable.bg_lead_1));
		viewGroup.addView(imageView);

		imageView = new ImageView(this);
		imageView.setImageDrawable(getResources().getDrawable(
				R.drawable.bg_lead_2));
		viewGroup.addView(imageView);

		imageView = new ImageView(this);
		imageView.setImageDrawable(getResources().getDrawable(
				R.drawable.bg_lead_3));
		viewGroup.addView(imageView);

		pageControl = (PageControlView) findViewById(R.id.lead_dot);
		pageControl.setCount(viewGroup.getChildCount());
		pageControl.generatePageControl(0);
		viewGroup.setScrollToScreenCallback(pageControl);
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
		logger.v("onKeyDown() ---> Enter");
		if (paramInt == 4)
		{
			finish();
			Util.exitMobileMusicApp(false);
		}
		logger.v("onKeyDown() ---> Exit");
		return super.onKeyDown(paramInt, paramKeyEvent);
	}
}
