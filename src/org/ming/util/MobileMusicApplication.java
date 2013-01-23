// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package org.ming.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Application;
import android.os.Process;
import android.telephony.TelephonyManager;

// Referenced classes of package com.redclound.lib:
//			Controller, GlobalSettingParameter

public class MobileMusicApplication extends Application
{

	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicApplication");
	private static AtomicBoolean mIsInLoginActivity = new AtomicBoolean();
	private static boolean mShowMusicSelectedToast = false;
	private static MobileMusicApplication sInstance = null;
	private boolean mIsInitService;
	private int mLastMemberShip;

	public MobileMusicApplication()
	{
		mIsInitService = false;
		mLastMemberShip = -1;
		sInstance = this;
		mIsInLoginActivity.set(false);
	}

	public static MobileMusicApplication getInstance()
	{
		if (sInstance == null)
			sInstance = new MobileMusicApplication();
		return sInstance;
	}

	public static boolean getIsInLogin()
	{
		return mIsInLoginActivity.get();
	}

	public static boolean getShowMusicSelectedToast()
	{
		return mShowMusicSelectedToast;
	}

	public static void setIsInLogin(boolean flag)
	{
		mIsInLoginActivity.set(flag);
	}

	public static void setShowMusicSelectedToast(boolean flag)
	{
		mShowMusicSelectedToast = flag;
	}

	public int getLastMemberShip()
	{
		return mLastMemberShip;
	}

	public boolean isInitService()
	{
		return mIsInitService;
	}

	public void onCreate()
	{
		super.onCreate();
	}

	public void onLowMemory()
	{
		super.onLowMemory();
	}

	public void setIsInitService(boolean flag)
	{
		mIsInitService = flag;
	}

	public void setLastMemberShip(int i)
	{
		mLastMemberShip = i;
	}

}
