package org.ming.center.download;

import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.system.SystemController;
import org.ming.center.system.SystemEventListener;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Message;

public class DLControllerImpl implements DLController, DLEventListener,
		MMHttpEventListener, SystemEventListener,
		MediaScannerConnection.MediaScannerConnectionClient
{
	private static final String REQUEST_BODY_FOR_MP3 = "Need not body for Mp3";
	public static final String SONG_CACHE_SUFFIX = ".part";
	private static final MyLogger logger = MyLogger
			.getLogger("DLControllerImpl");
	private static DLControllerImpl sInstance = null;
	private boolean cancleAllTask = false;
	private MobileMusicApplication mApp = null;
	private DBController mDBController = null;
	private Dispatcher mDispatcher = null;
	String mItemFilePath;
	private String mRingtonePath = null;
	MediaScannerConnection mScanner;
	private SystemController mSystemController = null;

	private DLControllerImpl(MobileMusicApplication paramMobileMusicApplication)
	{
		logger.v("DLControllerImpl() ---> Enter");
		this.mApp = paramMobileMusicApplication;
		this.mDispatcher = paramMobileMusicApplication.getEventDispatcher();
		this.mDBController = paramMobileMusicApplication.getController()
				.getDBController();

		this.mSystemController = paramMobileMusicApplication.getController()
				.getSystemController();
		this.mApp.getController().addHttpEventListener(3009, this);
		this.mApp.getController().addSystemEventListener(4, this);
		this.mApp.getController().addSystemEventListener(10, this);
		this.mApp.getController().addSystemEventListener(22, this);
		this.mApp.getController().addDLEventListener(2002, this);
		this.mApp.getController().addDLEventListener(2004, this);
		this.mApp.getController().addDLEventListener(2007, this);
		this.mApp.getController().addDLEventListener(2006, this);
		this.mApp.getController().addDLEventListener(2005, this);
		this.mApp.getController().addDLEventListener(2003, this);
		this.mApp.getController().addHttpEventListener(3003, this);
		this.mApp.getController().addHttpEventListener(3005, this);
		this.mApp.getController().addHttpEventListener(3004, this);
		this.mApp.getController().addDLEventListener(2015, this);
		this.mApp.getController().addDLEventListener(2017, this);
		this.mApp.getController().addDLEventListener(2019, this);
		this.mApp.getController().addDLEventListener(2018, this);
		this.mApp.getController().addDLEventListener(2020, this);
		this.mApp.getController().addDLEventListener(2016, this);
		logger.v("DLControllerImpl() ---> Exit");
	}

	public static DLControllerImpl getInstance(
			MobileMusicApplication paramMobileMusicApplication)
	{
		if (sInstance == null)
			sInstance = new DLControllerImpl(paramMobileMusicApplication);
		return sInstance;
	}

	@Override
	public void cancelAllTask()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelDownloadNotification()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelDownloadRemainNotification()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void initDownloadListFromDB()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDownloadItemFromLocal(String paramString)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void startAllTask()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onMediaScannerConnected()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onScanCompleted(String path, Uri uri)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSystemEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMMHttpEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDLEvent(Message paramMessage)
	{
		// TODO Auto-generated method stub

	}

}
