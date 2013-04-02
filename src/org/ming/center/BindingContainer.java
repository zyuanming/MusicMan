package org.ming.center;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ming.center.download.DownloadTask;
import org.ming.center.http.MMHttpTask;
import org.ming.util.MyLogger;

public class BindingContainer
{
	private static final MyLogger logger = MyLogger
			.getLogger("BindingContainer");
	private static BindingContainer sInstance = null;
	private List mDownloadTaskList;
	private List mHttpTaskList;
	private HashMap mServerContentMap;

	private BindingContainer()
	{
		mHttpTaskList = null;
		mServerContentMap = null;
		mDownloadTaskList = null;
		mHttpTaskList = new ArrayList();
		mServerContentMap = new HashMap();
		mDownloadTaskList = new ArrayList();
	}

	public static BindingContainer getInstance()
	{
		if (sInstance == null)
			sInstance = new BindingContainer();
		return sInstance;
	}

	public void addDownloadTask(DownloadTask paramDownloadTask)
	{
		synchronized (this.mDownloadTaskList)
		{
			if (!this.mDownloadTaskList.contains(paramDownloadTask))
				this.mDownloadTaskList.add(paramDownloadTask);
			return;
		}
	}

	public void addMMHttpTask(MMHttpTask paramMMHttpTask)
	{
		synchronized (this.mHttpTaskList)
		{
			logger.v("addMMHttpTask() ---> Enter");
			if (!this.mHttpTaskList.contains(paramMMHttpTask))
				this.mHttpTaskList.add(paramMMHttpTask);
			logger.v("addMMHttpTask() ---> Exit");
			return;
		}
	}

	public void addXMLContent(String paramString1, String paramString2)
	{
		synchronized (this.mServerContentMap)
		{
			logger.v("addXMLContent() ---> Enter");
			this.mServerContentMap.put(paramString1, paramString2);
			logger.v("addXMLContent() ---> Exit");
			return;
		}
	}

	public void clearDownloadTaskTable()
	{
		synchronized (this.mDownloadTaskList)
		{
			this.mDownloadTaskList.clear();
			return;
		}
	}

	public void clearMMHttpTaskList()
	{
		synchronized (this.mHttpTaskList)
		{
			logger.v("clearMMHttpTaskList() ---> Enter");
			this.mHttpTaskList.clear();
			logger.v("clearMMHttpTaskList() ---> Exit");
			return;
		}
	}

	public void clearXMLContentMap()
	{
		synchronized (this.mServerContentMap)
		{
			logger.v("clearXMLContentMap() ---> Enter");
			this.mServerContentMap.clear();
			logger.v("clearXMLContentMap() ---> Exit");
			return;
		}
	}

	public DownloadTask getDownloadTask(String paramString)
	{
		DownloadTask localDownloadTask;
		synchronized (this.mDownloadTaskList)
		{
			Iterator localIterator = this.mDownloadTaskList.iterator();
			do
			{
				if (!localIterator.hasNext())
				{
					localDownloadTask = null;
					break;
				}
				localDownloadTask = (DownloadTask) localIterator.next();
			} while (!localDownloadTask.getDownloadItem().getUrl()
					.equalsIgnoreCase(paramString));
		}
		return localDownloadTask;
	}

	public String getXMLContent(String paramString)
	{
		synchronized (this.mServerContentMap)
		{
			logger.v("getXMLContent()");
			String str = (String) this.mServerContentMap.get(paramString);
			return str;
		}
	}

	public boolean hasDownloadTask(DownloadTask paramDownloadTask)
	{
		synchronized (this.mDownloadTaskList)
		{
			boolean bool = this.mDownloadTaskList.contains(paramDownloadTask);
			return bool;
		}
	}

	public boolean hasDownloadTask(String paramString)
	{
		boolean bool;
		synchronized (this.mDownloadTaskList)
		{
			Iterator localIterator = this.mDownloadTaskList.iterator();
			do
			{
				if (!localIterator.hasNext())
				{
					bool = false;
					break;
				}
			} while (!((DownloadTask) localIterator.next()).getDownloadItem()
					.getUrl().equalsIgnoreCase(paramString));
			bool = true;
		}
		return bool;
	}

	public boolean hasMMHttpTask(MMHttpTask paramMMHttpTask)
	{
		synchronized (this.mHttpTaskList)
		{
			logger.v("hasMMHttpTask()");
			boolean bool = this.mHttpTaskList.contains(paramMMHttpTask);
			return bool;
		}
	}

	public boolean hasXMLContent(String paramString)
	{
		synchronized (this.mServerContentMap)
		{
			logger.v("hasXMLContent()");
			boolean bool = this.mServerContentMap.containsKey(paramString);
			return bool;
		}
	}

	public boolean isDownloadTaskListEmpty()
	{
		synchronized (this.mDownloadTaskList)
		{
			boolean bool = this.mDownloadTaskList.isEmpty();
			return bool;
		}
	}

	public void removeDownloadTask(DownloadTask paramDownloadTask)
	{
		synchronized (this.mDownloadTaskList)
		{
			this.mDownloadTaskList.remove(paramDownloadTask);
			return;
		}
	}

	public void removeMMHttpTask(MMHttpTask paramMMHttpTask)
	{
		synchronized (this.mHttpTaskList)
		{
			logger.v("removeMMHttpTask() ---> Enter");
			if (this.mHttpTaskList.contains(paramMMHttpTask))
				this.mHttpTaskList.remove(paramMMHttpTask);
			logger.v("removeMMHttpTask() ---> Exit");
			return;
		}
	}

	public void removeXMLContent(String paramString)
	{
		synchronized (this.mServerContentMap)
		{
			logger.v("removeXMLContent() ---> Enter");
			this.mServerContentMap.remove(paramString);
			logger.v("removeXMLContent() ---> Exit");
			return;
		}
	}
}