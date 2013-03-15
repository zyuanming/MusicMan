package org.ming.ui.activity.online;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.center.database.UserAccount;
import org.ming.center.download.DLController;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.ui.UIEventListener;
import org.ming.dispatcher.Dispatcher;
import org.ming.ui.util.DialogUtil;
import org.ming.util.MyLogger;
import org.ming.util.XMLParser;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MusicOnlineWapLoginActivity extends Activity implements
		MMHttpEventListener, UIEventListener
{
	public static final String RETCODE_RESET_FAIL = "000001";
	public static final String RETCODE_SUCCESS = "000000";
	public static final String RETCODE_UESER_UREGISTERED = "000002";
	private static final MyLogger logger = MyLogger
			.getLogger("MusicOnlineWapLoginActivity");
	private Button mCancel;
	private Controller mController;
	private Dialog mCurrentDialog;
	private MMHttpTask mCurrentTask;
	private DBController mDBController = null;
	private DLController mDLController = null;
	private Dispatcher mDispatcher;
	private int mFromType = 0;
	private HttpController mHttpController;
	private TextView mMsg;

	private void onHttpResponse(MMHttpTask paramMMHttpTask)
	{
		logger.v("onHttpResponse() ---> Enter");
		int i = paramMMHttpTask.getRequest().getReqType();
		byte[] abyte0 = paramMMHttpTask.getResponseBody();
		switch (i)
		{
		default:
			break;
		case 1001:
			// XMLParser xmlparser = new XMLParser(abyte0);
			// if (xmlparser.getRoot() == null
			// || xmlparser.getValueByTag("code") == null)
			// {
			// mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
			// getText(0x7f070041), getText(0x7f070040),
			// new android.view.View.OnClickListener()
			// {
			// public void onClick(View view)
			// {
			// finish();
			// mCurrentDialog.dismiss();
			// }
			// });
			// }
			// String s = xmlparser.getValueByTag("code");
			// String s1 = xmlparser.getValueByTag("info");
			// if (s1 == null)
			// {
			// if (s.equals("000000"))
			// {
			// GlobalSettingParameter.initLoginParam(abyte0);
			// if (GlobalSettingParameter.SERVER_INIT_PARAM_MDN != null)
			// {
			// GlobalSettingParameter.useraccount = mDBController
			// .getInDBByMDN(GlobalSettingParameter.SERVER_INIT_PARAM_MDN);
			// if (GlobalSettingParameter.useraccount == null)
			// {
			// UserAccount useraccount = new UserAccount();
			// useraccount.mMDN = GlobalSettingParameter.SERVER_INIT_PARAM_MDN;
			// useraccount.mId = mDBController
			// .addUserAccount(useraccount);
			// if (useraccount.mId != 0L)
			// GlobalSettingParameter.useraccount = useraccount;
			// if (mDBController
			// .getPlaylistByName(
			// "cmccwm.mobilemusic.database.default.online.playlist.favorite",
			// 0) == null)
			// mDBController
			// .createPlaylist(
			// "cmccwm.mobilemusic.database.default.online.playlist.favorite",
			// 0);
			// }
			// if (mFromType == 1)
			// mDispatcher.sendMessageDelayed(
			// mDispatcher.obtainMessage(1013), 0L);
			// if (mFromType == 6)
			// {
			// MobileMusicApplication.setIsInLogin(false);
			// mDispatcher.sendMessageDelayed(
			// mDispatcher.obtainMessage(1013), 0L);
			// }
			// mDLController.initDownloadListFromDB();
			// mDispatcher.sendMessageDelayed(
			// mDispatcher.obtainMessage(2008), 0L);
			// finish();
			// }
			// GlobalSettingParameter.loginMobileNum = xmlparser
			// .getValueByTag("mdn");
			// GlobalSettingParameter.loginRadomNum = xmlparser
			// .getValueByTag("randomsessionkey");
			// logger.i((new StringBuilder("Response phone number: "))
			// .append(GlobalSettingParameter.loginMobileNum)
			// .append("radom number: ")
			// .append(GlobalSettingParameter.loginRadomNum)
			// .toString());
			// if (GlobalSettingParameter.loginMobileNum == null
			// || GlobalSettingParameter.loginRadomNum == null)
			// {
			// mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
			// this, getText(0x7f070041), getText(0x7f070040),
			// new android.view.View.OnClickListener()
			// {
			// public void onClick(View view)
			// {
			// finish();
			// }
			// });
			// } else
			// {
			// Toast.makeText(this, s1, 0).show();
			// GlobalSettingParameter.isWlanLogined = true;
			// }
			// } else
			// {
			// if (s.equals("000002"))
			// {
			// logger.i("register");
			// Toast.makeText(this, s1, 1).show();
			// Intent intent = new Intent(getApplicationContext(),
			// RegisterActvity.class);
			// intent.putExtra(
			// "cmccwm.mobilemusic.ui.activity.registeractivity.spec",
			// "register");
			// startActivity(intent);
			// } else
			// {
			// mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
			// this, getText(0x7f070041), s1,
			// new android.view.View.OnClickListener()
			// {
			//
			// public void onClick(View view)
			// {
			// finish();
			// }
			// });
			// }
			// }
			// }
			// mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
			// getText(0x7f070041), getText(0x7f070047),
			// new android.view.View.OnClickListener()
			// {
			// public void onClick(View view)
			// {
			// finish();
			// }
			// });
			break;
		}
	}

	private void onSendHttpRequestClose()
	{
		logger.v("onSendHttpRequestClose() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(2131165249), getText(2131165252),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (MusicOnlineWapLoginActivity.this.mCurrentDialog != null)
						{
							MusicOnlineWapLoginActivity.this.mCurrentDialog
									.dismiss();
							MusicOnlineWapLoginActivity.this.mCurrentDialog = null;
						}
					}
				});
		logger.v("onSendHttpRequestClose() ---> Exit");
	}

	private void onSendHttpRequestFail(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestTimeOut() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(2131165249), getText(2131165254),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						MusicOnlineWapLoginActivity.this.finish();
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	private void onSendHttpRequestTimeOut(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestTimeOut() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(2131165249), getText(2131165254),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						MusicOnlineWapLoginActivity.this.finish();
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	public void CancelPreviousReq()
	{
		logger.v("CancelPreviousReq() ---> Enter");
		this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(4021));
		if (GlobalSettingParameter.isLogining)
		{
			this.mController.removeUIEventListener(4014, this);
			this.mController.removeUIEventListener(4018, this);
		}
		GlobalSettingParameter.isLogining = false;
		if (this.mCurrentTask != null)
		{
			this.mHttpController.cancelTask(this.mCurrentTask);
			this.mCurrentTask = null;
			if ((this.mCurrentDialog != null)
					&& (this.mCurrentDialog.isShowing()))
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
		}
		logger.v("CancelPreviousReq() ---> Exit");
	}

	public void handleMMHttpEvent(Message message)
	{
		logger.v("handleMMHttpEvent() ---> Enter");
		if (!GlobalSettingParameter.isLogining)
		{
			MMHttpTask mmhttptask;
			mmhttptask = (MMHttpTask) message.obj;
			if (mmhttptask == null || mCurrentTask == null
					|| mmhttptask.getTransId() != mCurrentTask.getTransId())
			{
				logger.v("Thus http message is not for this activity");
			}
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			switch (message.what)
			{
			case 3005:
			default:
				logger.v("handleMMHttpEvent() ---> Exit");
				break;
			case 3003:
				onHttpResponse(mmhttptask);
				break;
			case 3004:
				onSendHttpRequestFail(mmhttptask);
				break;
			case 3006:
				onSendHttpRequestTimeOut(mmhttptask);
				break;
			case 3007:
				onSendHttpRequestClose();
				break;
			}
		}
	}

	public void handleUIEvent(Message paramMessage)
	{
		switch (paramMessage.what)
		{
		default:
			return;
		case 4014:
			if (this.mFromType == 1)
				this.mDispatcher.sendMessageDelayed(
						this.mDispatcher.obtainMessage(1013), 0L);
			if (this.mFromType == 6)
			{
				MobileMusicApplication.setIsInLogin(false);
				this.mDispatcher.sendMessageDelayed(
						this.mDispatcher.obtainMessage(1013), 0L);
			}
			this.mDispatcher.sendMessageDelayed(
					this.mDispatcher.obtainMessage(2008), 0L);
			finish();
			break;
		}
	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_wap_login);
		this.mMsg = ((TextView) findViewById(2131034314));
		this.mCancel = ((Button) findViewById(2131034315));
		this.mFromType = getIntent().getIntExtra("FROMTYPE", 0);
		this.mController = Controller
				.getInstance((MobileMusicApplication) getApplication());
		this.mHttpController = this.mController.getHttpController();
		this.mDispatcher = ((MobileMusicApplication) getApplication())
				.getEventDispatcher();
		this.mDBController = this.mController.getDBController();
		this.mDLController = this.mController.getDLController();
		logger.v("onCreate() ---> Exit");
	}

	public void onPause()
	{
		logger.v("onDestroy() ---> Enter");
		super.onPause();
		MobclickAgent.onPause(this);
		CancelPreviousReq();
		this.mController.removeHttpEventListener(3003, this);
		this.mController.removeHttpEventListener(3005, this);
		this.mController.removeHttpEventListener(3004, this);
		this.mController.removeHttpEventListener(3006, this);
		this.mController.removeHttpEventListener(3007, this);
		this.mController.removeHttpEventListener(3008, this);
		if (GlobalSettingParameter.isLogining)
		{
			this.mController.removeUIEventListener(4014, this);
			this.mController.removeUIEventListener(4018, this);
		}
		logger.v("onPause() ---> Exit");
	}

	public void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		MobclickAgent.onResume(this);
		this.mController.addUIEventListener(4014, this);
		this.mController.addUIEventListener(4018, this);
		this.mController.addHttpEventListener(3003, this);
		this.mController.addHttpEventListener(3005, this);
		this.mController.addHttpEventListener(3004, this);
		this.mController.addHttpEventListener(3006, this);
		this.mController.addHttpEventListener(3007, this);
		this.mController.addHttpEventListener(3008, this);
		this.mMsg.setText(2131165407);
		if (!GlobalSettingParameter.isLogining)
		{
			MMHttpRequest localMMHttpRequest = MMHttpRequestBuilder
					.buildRequest(1001);
			localMMHttpRequest.addUrlParams("migu", "1");
			this.mCurrentTask = this.mHttpController
					.sendRequest(localMMHttpRequest);
		}
		this.mCancel.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				MusicOnlineWapLoginActivity.this.CancelPreviousReq();
				MusicOnlineWapLoginActivity.this.finish();
			}
		});
		logger.v("onResume() ---> Exit");
	}
}