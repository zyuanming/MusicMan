package org.ming.ui.util;

import java.util.WeakHashMap;

public class ImageCache extends WeakHashMap
{

	public ImageCache()
	{}

	public boolean isCached(String s)
	{
		boolean flag;
		if (containsKey(s) && get(s) != null)
			flag = true;
		else
			flag = false;
		return flag;
	}

	private static final long serialVersionUID = 1L;
}