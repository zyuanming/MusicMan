package org.ming.center.http;

public abstract interface HttpController
{
	public abstract void cancelTask(MMHttpTask paramMMHttpTask);

	public abstract MMHttpTask sendRequest(MMHttpRequest paramMMHttpRequest);

	public abstract MMHttpTask sendRequest(MMHttpRequest paramMMHttpRequest,
			boolean paramBoolean);
}