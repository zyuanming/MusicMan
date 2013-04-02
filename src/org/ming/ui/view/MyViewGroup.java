package org.ming.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Scroller;
import org.ming.util.MyLogger;

import java.util.ArrayList;
import java.util.Iterator;

public class MyViewGroup extends ViewGroup
{

	private MyLogger logger = MyLogger.getLogger("MyViewGroup");

	private Scroller scroller;

	private int currentScreenIndex;

	private GestureDetector gestureDetector;

	private ArrayList<ScrollToScreenCallback> scrollToScreenCallbackList = new ArrayList<ScrollToScreenCallback>();

	public void addScrollToScreenCallback(
			ScrollToScreenCallback scrollToScreenCallback)
	{
		logger.v("call----->addscrollToScreenCallback----enter");
		this.scrollToScreenCallbackList.add(scrollToScreenCallback);
		logger.v("call----->addscrollToScreenCallback----out");
	}

	// 设置一个标志位，防止底层的onTouch事件重复处理UP事件
	private boolean fling;

	public MyViewGroup(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	public MyViewGroup(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public MyViewGroup(Context context)
	{
		super(context);
		initView(context);
	}

	private void initView(final Context context)
	{
		this.scroller = new Scroller(context);

		this.gestureDetector = new GestureDetector(new OnGestureListener()
		{

			@Override
			public boolean onSingleTapUp(MotionEvent e)
			{
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e)
			{
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY)
			{
				if ((distanceX > 0 && currentScreenIndex < getChildCount() - 1)// 防止移动过最后一页
						|| (distanceX < 0 && getScrollX() > 0))
				{// 防止向第一页之前移动
					scrollBy((int) distanceX, 0);
				}
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e)
			{
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY)
			{
				logger.v("min velocity >>>"
						+ ViewConfiguration.get(context)
								.getScaledMinimumFlingVelocity()
						+ " current velocity>>" + velocityX);
				if (Math.abs(velocityX) > ViewConfiguration.get(context)
						.getScaledMinimumFlingVelocity())
				{// 判断是否达到最小轻松速度，取绝对值的
					if (velocityX > 0 && currentScreenIndex > 0)
					{
						logger.v(">>>>fling to right");
						fling = true;
						scrollToScreen(currentScreenIndex - 1);
					} else if (velocityX < 0
							&& currentScreenIndex < getChildCount() - 1)
					{
						logger.v(">>>>fling to left");
						fling = true;
						scrollToScreen(currentScreenIndex + 1);
					}
				}

				return true;
			}

			@Override
			public boolean onDown(MotionEvent e)
			{
				return false;
			}
		});
	}

	// 初始化时会用到这个函数
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		logger.v(">>left: " + left + " top: " + top + " right: " + right
				+ " bottom:" + bottom);

		/**
		 * 设置布局，将子视图顺序横屏排列
		 */
		for (int i = 0; i < getChildCount(); i++)
		{
			logger.v("------>width" + getWidth());
			logger.v("------>height" + getHeight());
			View child = getChildAt(i);
			child.setVisibility(View.VISIBLE);
			child.measure(right - left, bottom - top);
			child.layout(0 + i * getWidth(), 0, getWidth() + i * getWidth(),
					getHeight());
		}
	}

	@Override
	public void computeScroll()
	{
		// 动画没有结束
		if (scroller.computeScrollOffset())
		{
			logger.v("----->computeScroll" + "动画没有结束");
			scrollTo(scroller.getCurrX(), 0);
			// 更新主线程的UI
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		gestureDetector.onTouchEvent(event);

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			logger.v("----->Touch_ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			logger.v("----->Touch_ACTION_MOVE");
			break;
		case MotionEvent.ACTION_UP:
			logger.v("----->Touch_ACTION_UP");
			if (!fling)
			{
				logger.v("----->Touch_ACTION_UP------>fling--" + fling);
				snapToDestination();
			}
			fling = false;
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 切换到指定屏
	 * 
	 * @param whichScreen
	 */
	public void scrollToScreen(int whichScreen)
	{
		logger.v("scrollToScreen------>" + whichScreen);
		if (getFocusedChild() != null && whichScreen != currentScreenIndex
				&& getFocusedChild() == getChildAt(currentScreenIndex))
		{
			getFocusedChild().clearFocus();
			logger.v("clearFocus()");
		}

		final int delta = whichScreen * getWidth() - getScrollX();
		scroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		// 更新UI线程
		invalidate();

		currentScreenIndex = whichScreen;
		if (scrollToScreenCallbackList != null)
		{
			for (Iterator<ScrollToScreenCallback> iterator = scrollToScreenCallbackList
					.iterator(); iterator.hasNext();)
			{
				iterator.next().callback(currentScreenIndex);
			}
		}
	}

	/**
	 * 切换到指定的屏幕
	 */
	private void snapToDestination()
	{
		logger.v("snapToDestination----" + "getScrollX()------>" + getScrollX());
		scrollToScreen((getScrollX() + (getWidth() / 2)) / getWidth());
	}

	public interface ScrollToScreenCallback
	{
		public void callback(int currentIndex);
	}
}