package org.ming.ui;

import org.ming.logic.PlayerEventListener;
import org.ming.logic.UIEventListener;
import org.ming.util.UrlImageDownloader;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class PlayerStatusBar extends RelativeLayout implements PlayerEventListener, UIEventListener
{

	private UrlImageDownloader mImageDownloader;
	
	public PlayerStatusBar(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		Log.d("PlayerStatusBar", ".....enter");
	}

	@Override
	public void handleUIEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handlerPlayerEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub
		
	}

}
