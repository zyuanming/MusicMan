package org.ming.center;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;
import org.ming.util.Util;

import android.app.Application;
import android.os.Process;
import android.telephony.TelephonyManager;

public class MobileMusicApplication extends Application
{

	private static final MyLogger logger = MyLogger
			.getLogger("MobileMusicApplication");
	private static AtomicBoolean mIsInLoginActivity = new AtomicBoolean();
	private static Map mRatedContentid = new HashMap();
	private static boolean mShowMusicSelectedToast = false;
	private static MobileMusicApplication sInstance = null;
	private static int sTransId = 0;
	public HashMap mAllNumAndNameCache;
	private Controller mController;
	private Dispatcher mDispatcher;
	private boolean mIsInitService;
	private int mLastMemberShip;

	public MobileMusicApplication()
	{
		mDispatcher = null;
		mController = null;
		mIsInitService = false;
		mLastMemberShip = -1;
		mAllNumAndNameCache = new HashMap();
		sInstance = this;
		mIsInLoginActivity.set(false);
	}

	public static void addRatedMusic(Integer integer, Integer integer1)
	{
		mRatedContentid.put(integer, integer1);
	}

	public static void clearRated()
	{
		mRatedContentid.clear();
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

	public static int getRate(Integer integer)
	{
		return ((Integer) mRatedContentid.get(integer)).intValue();
	}

	public static boolean getShowMusicSelectedToast()
	{
		return mShowMusicSelectedToast;
	}

	public static int getTransId()
	{
		if (sTransId < 0x7fffffff)
			sTransId = 1 + sTransId;
		else
			sTransId = 0;
		return sTransId;
	}

	public static boolean isRated(Integer integer)
	{
		return mRatedContentid.containsKey(integer);
	}

	public static void setIsInLogin(boolean flag)
	{
		mIsInLoginActivity.set(flag);
	}

	public static void setShowMusicSelectedToast(boolean flag)
	{
		mShowMusicSelectedToast = flag;
	}

	public Controller getController()
	{
		return mController;
	}

	public Dispatcher getEventDispatcher()
	{
		return mDispatcher;
	}

	public int getLastMemberShip()
	{
		return mLastMemberShip;
	}

	public HashMap getmAllNumAndNameCache()
	{
		return mAllNumAndNameCache;
	}

	public boolean isInitService()
	{
		return mIsInitService;
	}

	public void onCreate()
	{
		super.onCreate();
		if (!Util.getDefaultSettings())
		{
			logger.e("The default setting is wrong!!!");
			Process.killProcess(Process.myPid());
		} else
		{
			mDispatcher = Dispatcher.getInstance(null);
			mController = Controller.getInstance(MobileMusicApplication.this);
			mController.initController();
			GlobalSettingParameter.isCheckOlderMusicVersion = mController
					.getDBController().getCheckOlderVersion();
			mDispatcher.setListener(mController);
			TelephonyManager telephonymanager = (TelephonyManager) getBaseContext()
					.getSystemService("phone");
			GlobalSettingParameter.UPDATE_TAG_IMEI_INFO = telephonymanager
					.getDeviceId();
			GlobalSettingParameter.UPDATE_TAG_IMSI_INFO = telephonymanager
					.getSubscriberId();
		}
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

	public void setmAllNumAndNameCache(HashMap hashmap)
	{
		mAllNumAndNameCache = hashmap;
	}

}
