package org.ming.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.ming.R;
import org.ming.ui.view.MyViewGroup.ScrollToScreenCallback;
import org.ming.util.MyLogger;

public class PageControlView extends LinearLayout implements
		ScrollToScreenCallback
{

	private MyLogger logger = MyLogger.getLogger("PageControlView");

	private Context context;

	public PageControlView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.init(context);
	}

	public PageControlView(Context context)
	{
		super(context);
		this.init(context);
	}

	private void init(Context context)
	{
		this.context = context;
	}

	@Override
	public void callback(int currentIndex)
	{
		generatePageControl(currentIndex);
	}

	public void generatePageControl(int currentIndex)
	{
		this.removeAllViews();

		logger.v("generatePageControl");
		ImageView imageView = new ImageView(context);
		if (currentIndex == 0)
		{
			imageView.setImageResource(R.drawable.dot_1_for_player_activity);
		} else if (currentIndex == 1)
		{
			imageView.setImageResource(R.drawable.dot_2_for_player_activity);
		} else if (currentIndex == 2)
		{
			imageView.setImageResource(R.drawable.dot_3_for_player_activity);
		}
		this.addView(imageView);
	}
}