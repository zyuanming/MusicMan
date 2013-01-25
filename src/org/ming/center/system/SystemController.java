package org.ming.center.system;

import android.content.Context;

public abstract interface SystemController
{
	public abstract boolean checkWapStatus();

	public abstract void scanDirAsync(String paramString);

	public abstract void scanFileAsync(String paramString);

	public abstract void sendLongSMS(Context paramContext, String paramString1,
			String paramString2);

	public abstract void sendSMS(Context paramContext, String paramString1,
			String paramString2);
}
