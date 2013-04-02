package org.ming.center.http;

import org.ming.center.BindingContainer;
import org.ming.center.MobileMusicApplication;
import org.ming.dispatcher.Dispatcher;
import org.ming.dispatcher.DispatcherEventEnum;
import org.ming.util.MyLogger;

import android.net.ConnectivityManager;
import android.os.Message;

public class HttpControllerImpl implements HttpController, MMHttpTaskListener
{
	private static final int ENABLE_CMWAP_TIMEOUT = 10000;
	private static MyLogger logger = MyLogger.getLogger("HttpControllerImpl");
	private static HttpControllerImpl sInstance = null;
	private MobileMusicApplication mApp = null;
	private ConnectivityManager mConnMgr = null;
	private Dispatcher mDispatcher = null;

	public HttpControllerImpl(MobileMusicApplication paramMobileMusicApplication)
	{
		this.mApp = paramMobileMusicApplication;
		this.mDispatcher = paramMobileMusicApplication.getEventDispatcher();
		this.mConnMgr = ((ConnectivityManager) paramMobileMusicApplication
				.getSystemService("connectivity"));
	}

	public static HttpControllerImpl getInstance(
			MobileMusicApplication paramMobileMusicApplication)
	{
		if (sInstance == null)
			sInstance = new HttpControllerImpl(paramMobileMusicApplication);
		return sInstance;
	}

	public void cancelTask(MMHttpTask paramMMHttpTask)
	{
		logger.v("cancelTask() ---> Enter ");
		if (paramMMHttpTask == null)
			paramMMHttpTask.setIsCancled(true);
		BindingContainer.getInstance().removeMMHttpTask(paramMMHttpTask);
		logger.v("cancelTask() ---> Exit ");
	}

	public MMHttpTask sendRequest(MMHttpRequest paramMMHttpRequest)
	{
		logger.v("sendRequest() ---> Enter");
		logger.d("sendRequest(), req type is: "
				+ paramMMHttpRequest.getReqType());
		MMHttpTask localMMHttpTask = new MMHttpTask(paramMMHttpRequest, this);
		localMMHttpTask.setTransId(MobileMusicApplication.getTransId());
		BindingContainer.getInstance().addMMHttpTask(localMMHttpTask);
		new Thread(localMMHttpTask).start();
		logger.v("sendRequest() ---> Exit");
		return localMMHttpTask;
	}

	public MMHttpTask sendRequest(MMHttpRequest paramMMHttpRequest,
			boolean paramBoolean)
	{
		logger.v("sendRequest() ---> Enter");
		logger.d("sendRequest(), req type is: "
				+ paramMMHttpRequest.getReqType());
		MMHttpTask localMMHttpTask = new MMHttpTask(paramMMHttpRequest, this);
		localMMHttpTask.setCacheAble(paramBoolean);
		localMMHttpTask.setTransId(MobileMusicApplication.getTransId());
		BindingContainer.getInstance().addMMHttpTask(localMMHttpTask);
		new Thread(localMMHttpTask).start();
		logger.v("sendRequest() ---> Exit");
		return localMMHttpTask;
	}

	public void taskCanceled(MMHttpTask paramMMHttpTask)
	{
		logger.v("taskCancelled() ---> Enter");
		Message localMessage = this.mDispatcher.obtainMessage(
				DispatcherEventEnum.HTTP_EVENT_TASK_CANCELED, paramMMHttpTask);
		this.mDispatcher.sendMessage(localMessage);
		logger.v("taskCancelled() ---> Exit");
	}

	public void taskCmWapClosed(MMHttpTask paramMMHttpTask)
	{
		logger.v("taskWapClosed() ---> Enter");
		if (BindingContainer.getInstance().hasMMHttpTask(paramMMHttpTask))
		{
			Message localMessage = this.mDispatcher.obtainMessage(
					DispatcherEventEnum.HTTP_EVENT_WAP_CLOSED, paramMMHttpTask);
			this.mDispatcher.sendMessage(localMessage);
			BindingContainer.getInstance().removeMMHttpTask(paramMMHttpTask);
		}
		logger.v("taskWapClosed() ---> Exit");
	}

	public void taskCompleted(MMHttpTask paramMMHttpTask)
	{
		logger.v("taskCompleted() ---> Enter");
		if (BindingContainer.getInstance().hasMMHttpTask(paramMMHttpTask))
		{
			Message localMessage = this.mDispatcher.obtainMessage(
					DispatcherEventEnum.HTTP_EVENT_TASK_COMPLETE,
					paramMMHttpTask);
			this.mDispatcher.sendMessage(localMessage);
			BindingContainer.getInstance().removeMMHttpTask(paramMMHttpTask);
		}
		logger.v("taskCompleted() ---> Exit");
	}

	public void taskEnd(MMHttpTask paramMMHttpTask)
	{
		logger.v("taskEnd() ---> Enter");
		logger.v("taskEnd() ---> Exit");
	}

	public void taskFailed(MMHttpTask paramMMHttpTask)
	{
		logger.v("taskFailed() ---> Enter");
		if (BindingContainer.getInstance().hasMMHttpTask(paramMMHttpTask))
		{
			Message localMessage = this.mDispatcher.obtainMessage(
					DispatcherEventEnum.HTTP_EVENT_TASK_FAIL, paramMMHttpTask);
			this.mDispatcher.sendMessage(localMessage);
			BindingContainer.getInstance().removeMMHttpTask(paramMMHttpTask);
		}
		logger.v("taskFailed() ---> Exit");
	}

	public void taskStarted(MMHttpTask paramMMHttpTask)
	{
		logger.v("taskStarted() ---> Enter");
		if (BindingContainer.getInstance().hasMMHttpTask(paramMMHttpTask))
		{
			Message localMessage = this.mDispatcher.obtainMessage(
					DispatcherEventEnum.HTTP_EVENT_TASK_START, paramMMHttpTask);
			this.mDispatcher.sendMessage(localMessage);
		}
		logger.v("taskStarted() ---> Exit");
	}

	public void taskTimeOut(MMHttpTask paramMMHttpTask)
	{
		logger.v("taskTimeOut() ---> Enter");
		if (BindingContainer.getInstance().hasMMHttpTask(paramMMHttpTask))
		{
			Message localMessage = this.mDispatcher.obtainMessage(
					DispatcherEventEnum.HTTP_EVENT_TASK_TIMEOUT,
					paramMMHttpTask);
			this.mDispatcher.sendMessage(localMessage);
			BindingContainer.getInstance().removeMMHttpTask(paramMMHttpTask);
		}
		logger.v("taskTimeOut() ---> Exit");
	}

	public void taskWlanClosed(MMHttpTask paramMMHttpTask)
	{
		logger.v("taskWapClosed() ---> Enter");
		if (BindingContainer.getInstance().hasMMHttpTask(paramMMHttpTask))
		{
			Message localMessage = this.mDispatcher.obtainMessage(
					DispatcherEventEnum.WLAN_EVENT_WLAN_CLOSE, paramMMHttpTask);
			this.mDispatcher.sendMessage(localMessage);
			BindingContainer.getInstance().removeMMHttpTask(paramMMHttpTask);
		}
		logger.v("taskWapClosed() ---> Exit");
	}
}