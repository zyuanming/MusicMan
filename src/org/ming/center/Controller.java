package org.ming.center;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ming.center.database.DBController;
import org.ming.center.database.DBControllerImpl;
import org.ming.center.download.DLController;
import org.ming.center.download.DLControllerImpl;
import org.ming.center.download.DLEventListener;
import org.ming.center.http.HttpController;
import org.ming.center.http.HttpControllerImpl;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.player.PlayerController;
import org.ming.center.player.PlayerControllerImpl;
import org.ming.center.player.PlayerEventListener;
import org.ming.center.system.SystemController;
import org.ming.center.system.SystemControllerImpl;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.UIEventListener;
import org.ming.dispatcher.DispatcherEventEnum;
import org.ming.dispatcher.DispatcherEventListener;
import org.ming.util.MyLogger;

import android.os.Message;

/**
 * 处理各种消息： 1、数据库消息 2、网络消息 3、UI消息 4、系统消息 5、播放器消息
 * 
 * @author lkh
 * 
 */
public class Controller implements DispatcherEventListener
{

	private static MyLogger logger = MyLogger.getLogger("Controller");
	private static Controller sInstance = null;
	private MobileMusicApplication mApp = null;
	private DBController mDbController = null;
	private DLController mDlController = null;
	private PlayerController mPlayerController = null;
	private HttpController mHttpController = null;
	private SystemController mSystemController = null;
	private Map<Integer, List<DLEventListener>> mDLEventListeners = null;
	private Map<Integer, List<MMHttpEventListener>> mHttpEventListeners = null;
	private Map<Integer, List<PlayerEventListener>> mPlayerEventListeners = null;
	private Map<Integer, List<SystemEventListener>> mSystemEventListeners = null;
	private Map<Integer, List<UIEventListener>> mUIEventListeners = null;

	private Controller(MobileMusicApplication paramMobileMusicApplication)
	{
		this.mApp = paramMobileMusicApplication;
	}

	public static Controller getInstance(
			MobileMusicApplication mobileMusicApplication)
	{
		logger.v("getInstance()");
		if (sInstance == null)
			sInstance = new Controller(mobileMusicApplication);
		return sInstance;
	}

	public HttpController getHttpController()
	{
		return this.mHttpController;
	}

	public DBController getDBController()
	{
		return this.mDbController;
	}

	public DLController getDLController()
	{
		return this.mDlController;
	}

	public PlayerController getPlayerController()
	{
		return this.mPlayerController;
	}

	public SystemController getSystemController()
	{
		return this.mSystemController;
	}

	private void handleDLEvent(Message paramMessage)
	{
		logger.v("handleDLEvent() ---> Enter");
		List<DLEventListener> localList = (List<DLEventListener>) this.mDLEventListeners
				.get(Integer.valueOf(paramMessage.what));
		if (localList == null)
		{
			logger.v("handlePlayerEvent() ---> Exit");
			return;
		} else
		{
			for (Iterator<DLEventListener> localIterator = localList.iterator(); localIterator
					.hasNext();)
			{
				((DLEventListener) localIterator.next())
						.handleDLEvent(paramMessage);
			}
		}
	}

	private void handleHttpEvent(Message paramMessage)
	{
		logger.v("handleHttpEvent() ---> Enter");
		List<MMHttpEventListener> localList = (List<MMHttpEventListener>) this.mHttpEventListeners
				.get(Integer.valueOf(paramMessage.what));
		if (localList == null)
		{
			logger.v("handlePlayerEvent() ---> Exit");
		} else
		{
			for (Iterator<MMHttpEventListener> localIterator = localList
					.iterator(); localIterator.hasNext();)
			{
				((MMHttpEventListener) localIterator.next())
						.handleMMHttpEvent(paramMessage);
			}
		}
	}

	private void handlePlayerEvent(Message message)
	{
		List list;
		logger.v("handlePlayerEvent() ---> Enter");
		list = (List) mPlayerEventListeners.get(Integer.valueOf(message.what));
		if (list == null)
		{
			logger.v("handlePlayerEvent() ---> Exit");
		} else
		{
			for (Iterator iterator = list.iterator(); iterator.hasNext();)
			{
				((PlayerEventListener) iterator.next())
						.handlePlayerEvent(message);
			}
		}
	}

	private void handleSystemEvent(Message paramMessage)
	{
		logger.v("handleSystemEvent() ---> Enter");
		List<SystemEventListener> localList = (List<SystemEventListener>) this.mSystemEventListeners
				.get(Integer.valueOf(paramMessage.what));
		if (localList == null)
		{
			logger.v("handlePlayerEvent() ---> Exit");
		} else
		{
			for (Iterator<SystemEventListener> localIterator = localList
					.iterator(); localIterator.hasNext();)
			{
				((SystemEventListener) localIterator.next())
						.handleSystemEvent(paramMessage);
			}
		}
	}

	private void handleUIEvent(Message paramMessage)
	{
		logger.v("handleUIEvent() ---> Enter");
		List<UIEventListener> localList = (List<UIEventListener>) this.mUIEventListeners
				.get(Integer.valueOf(paramMessage.what));
		if (localList == null)
		{
			logger.v("handlePlayerEvent() ---> Exit");
		} else
		{
			for (Iterator<UIEventListener> localIterator = localList.iterator(); localIterator
					.hasNext();)
			{
				((UIEventListener) localIterator.next())
						.handleUIEvent(paramMessage);
			}
		}
	}

	public void addDLEventListener(int paramInt,
			DLEventListener paramDLEventListener)
	{
		synchronized (this.mDLEventListeners)
		{
			List<DLEventListener> localObject2 = (List<DLEventListener>) this.mDLEventListeners
					.get(Integer.valueOf(paramInt));
			if (localObject2 == null)
			{
				localObject2 = new ArrayList<DLEventListener>();
				this.mDLEventListeners.put(Integer.valueOf(paramInt),
						localObject2);
			}
			if (!localObject2.contains(paramDLEventListener))
				localObject2.add(paramDLEventListener);
			return;
		}
	}

	public void addHttpEventListener(int paramInt,
			MMHttpEventListener paramMMHttpEventListener)
	{
		synchronized (this.mHttpEventListeners)
		{
			List<MMHttpEventListener> localObject2 = (List<MMHttpEventListener>) this.mHttpEventListeners
					.get(Integer.valueOf(paramInt));
			if (localObject2 == null)
			{
				localObject2 = new ArrayList<MMHttpEventListener>();
				this.mHttpEventListeners.put(Integer.valueOf(paramInt),
						localObject2);
			}
			if (!localObject2.contains(paramMMHttpEventListener))
				localObject2.add(paramMMHttpEventListener);
			return;
		}
	}

	public void addPlayerEventListener(int paramInt,
			PlayerEventListener paramPlayerEventListener)
	{
		synchronized (this.mPlayerEventListeners)
		{
			List<PlayerEventListener> localObject2 = (List<PlayerEventListener>) this.mPlayerEventListeners
					.get(Integer.valueOf(paramInt));
			if (localObject2 == null)
			{
				localObject2 = new ArrayList<PlayerEventListener>();
				this.mPlayerEventListeners.put(Integer.valueOf(paramInt),
						localObject2);
			}
			if (!localObject2.contains(paramPlayerEventListener))
				localObject2.add(paramPlayerEventListener);
			return;
		}
	}

	public void addSystemEventListener(int paramInt,
			SystemEventListener paramSystemEventListener)
	{
		synchronized (this.mSystemEventListeners)
		{
			List<SystemEventListener> localObject2 = (List<SystemEventListener>) this.mSystemEventListeners
					.get(Integer.valueOf(paramInt));
			if (localObject2 == null)
			{
				localObject2 = new ArrayList<SystemEventListener>();
				this.mSystemEventListeners.put(Integer.valueOf(paramInt),
						localObject2);
			}
			if (!localObject2.contains(paramSystemEventListener))
				localObject2.add(paramSystemEventListener);
			return;
		}
	}

	public void addUIEventListener(int paramInt,
			UIEventListener paramUIEventListener)
	{
		synchronized (this.mUIEventListeners)
		{
			List<UIEventListener> localObject2 = (List<UIEventListener>) this.mUIEventListeners
					.get(Integer.valueOf(paramInt));
			if (localObject2 == null)
			{
				localObject2 = new ArrayList<UIEventListener>();
				this.mUIEventListeners.put(Integer.valueOf(paramInt),
						localObject2);
			}
			if (!localObject2.contains(paramUIEventListener))
				localObject2.add(paramUIEventListener);
			return;
		}
	}

	public void handleMessage(Message paramMessage)
	{
		logger.v("handleMessage() ---> Enter");
		logger.v("Receive msg: "
				+ DispatcherEventEnum.getString(paramMessage.what));
		if (DispatcherEventEnum.isSystemEvent(paramMessage))
			handleSystemEvent(paramMessage);

		else if (DispatcherEventEnum.isPlayerEvent(paramMessage))
			handlePlayerEvent(paramMessage);
		else if (DispatcherEventEnum.isHttpEvent(paramMessage))
			handleHttpEvent(paramMessage);
		else if (DispatcherEventEnum.isDLEvent(paramMessage))
			handleDLEvent(paramMessage);
		else if (DispatcherEventEnum.isUIEvent(paramMessage))
			handleUIEvent(paramMessage);
		logger.v("handleMessage() ---> Exit");
	}

	protected void initController()
	{
		logger.v("initController() ---> Enter");
		this.mSystemEventListeners = new HashMap();
		this.mSystemController = SystemControllerImpl.getInstance(this.mApp);
		this.mDbController = DBControllerImpl.getInstance(this.mApp);
		this.mHttpEventListeners = new HashMap();
		this.mHttpController = HttpControllerImpl.getInstance(this.mApp);
		this.mPlayerEventListeners = new HashMap();
		this.mPlayerController = PlayerControllerImpl.getInstance(this.mApp);
		this.mDLEventListeners = new HashMap();
		this.mDlController = DLControllerImpl.getInstance(this.mApp);
		this.mUIEventListeners = new HashMap();
		logger.v("initController() ---> Exit");
	}

	public void removeDLEventListener(int paramInt,
			DLEventListener paramDLEventListener)
	{
		synchronized (this.mDLEventListeners)
		{
			List localList = (List) this.mDLEventListeners.get(Integer
					.valueOf(paramInt));
			if ((localList != null)
					&& (localList.contains(paramDLEventListener)))
				localList.remove(paramDLEventListener);
			if (localList.isEmpty())
				this.mDLEventListeners.remove(Integer.valueOf(paramInt));
			return;
		}
	}

	public void removeHttpEventListener(int paramInt,
			MMHttpEventListener paramMMHttpEventListener)
	{
		synchronized (this.mHttpEventListeners)
		{
			List localList = (List) this.mHttpEventListeners.get(Integer
					.valueOf(paramInt));
			if ((localList != null)
					&& (localList.contains(paramMMHttpEventListener)))
				localList.remove(paramMMHttpEventListener);
			if (localList.isEmpty())
				this.mHttpEventListeners.remove(Integer.valueOf(paramInt));
			return;
		}
	}

	public void removePlayerEventListener(int paramInt,
			PlayerEventListener paramPlayerEventListener)
	{
		synchronized (this.mPlayerEventListeners)
		{
			List localList = (List) this.mPlayerEventListeners.get(Integer
					.valueOf(paramInt));
			if ((localList != null)
					&& (localList.contains(paramPlayerEventListener)))
				localList.remove(paramPlayerEventListener);
			if (localList.isEmpty())
				this.mPlayerEventListeners.remove(Integer.valueOf(paramInt));
			return;
		}
	}

	public void removeSystemEventListener(int paramInt,
			SystemEventListener paramSystemEventListener)
	{
		synchronized (this.mSystemEventListeners)
		{
			List localList = (List) this.mSystemEventListeners.get(Integer
					.valueOf(paramInt));
			if ((localList != null)
					&& (localList.contains(paramSystemEventListener)))
				localList.remove(paramSystemEventListener);
			if (localList.isEmpty())
				this.mSystemEventListeners.remove(Integer.valueOf(paramInt));
			return;
		}
	}

	public void removeUIEventListener(int paramInt,
			UIEventListener paramUIEventListener)
	{
		synchronized (this.mUIEventListeners)
		{
			List localList = (List) this.mUIEventListeners.get(Integer
					.valueOf(paramInt));
			if ((localList != null)
					&& (localList.contains(paramUIEventListener)))
				localList.remove(paramUIEventListener);
			if (localList.isEmpty())
				this.mUIEventListeners.remove(Integer.valueOf(paramInt));
			return;
		}
	}
}