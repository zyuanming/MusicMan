package org.ming.ui.activity;

import java.text.SimpleDateFormat;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.http.HttpController;
import org.ming.center.http.HttpControllerImpl;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.system.SystemEventListener;
import org.ming.center.ui.AsyncToastDialogController;
import org.ming.ui.util.DialogUtil;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;
import org.ming.util.XMLParser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

public class DialogActivity extends Activity implements SystemEventListener,
		MMHttpEventListener
{
	public static final int ASYNC_EVENT_TYPE_APPLY_MEMBER_FAIL = 1015;
	public static final int ASYNC_EVENT_TYPE_CANCEL_ONLINE_MUSIC_FAIL = 1001;
	public static final int ASYNC_EVENT_TYPE_CANCEL_TONE_FAIL = 1006;
	public static final int ASYNC_EVENT_TYPE_CANCEL_WHOLE_SONG_FAIL = 1003;
	public static final int ASYNC_EVENT_TYPE_ONLINE_LISTEN_THIRTY_SECONDS_FOR_FREE_ONLINE_MUSIC = 1011;
	public static final int ASYNC_EVENT_TYPE_ONLINE_LISTEN_THIRTY_SECONDS_FOR_OPEN_HIGN_MEMBER = 1012;
	public static final int ASYNC_EVENT_TYPE_ONLINE_LISTEN_THIRTY_SECONDS_FOR_OPEN_SPECIAL_MEMBER = 1014;
	public static final int ASYNC_EVENT_TYPE_ORDER_ONLINE_MUSIC_FAIL = 1000;
	public static final int ASYNC_EVENT_TYPE_ORDER_TONE_BOX_FAIL = 1008;
	public static final int ASYNC_EVENT_TYPE_ORDER_TONE_FAIL = 1005;
	public static final int ASYNC_EVENT_TYPE_ORDER_WHOLE_SONG_FAIL = 1002;
	public static final int ASYNC_EVENT_TYPE_PLAY_SPACE_NOTFILL = 1017;
	public static final int ASYNC_EVENT_TYPE_PRESENT_SONG_FAIL = 1009;
	public static final int ASYNC_EVENT_TYPE_RECOMMEND_MOBLIEMUSIC_FAIL = 1004;
	public static final int ASYNC_EVENT_TYPE_RECOMMEND_SONG_FAIL = 1010;
	public static final int ASYNC_EVENT_TYPE_SET_DEFAULT_TONE_FAIL = 1007;
	public static final int ASYNC_EVENT_TYPE_SET_DEFAULT_TONE_SUCESS = 1013;
	public static final int ASYNC_EVENT_TYPE_USER_NEED_LOGIN = 1016;
	public static final String INTENT_KEY_EVENT_TYPE = "cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype";
	public static final String INTENT_KEY_ICON_RESID = "cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid";
	public static final String INTENT_KEY_MESSAGE_TEXT = "cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext";
	public static final String INTENT_KEY_OBJECT = "cmccwm.mobilemusic.ui.activity.DialogActivity.object";
	public static final String INTENT_KEY_SONG_ID = "cmccwm.mobilemusic.ui.activity.DialogActivity.songid";
	public static final String INTENT_KEY_TITLE_RESID = "cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid";
	private static final String MEMBERSHIP_SPECIAL = "tj";
	private static final String MEMBERSHIP_VIP = "gj";
	private static final MyLogger logger = MyLogger.getLogger("DialogActivity");
	private boolean isSpecial = false;
	private TextView mAlertTitleView = null;
	private MobileMusicApplication mApp = null;
	private Button mButton1 = null;
	private Button mButton2 = null;
	private Button mButton3 = null;
	private Controller mController = null;
	private Dialog mCurrentDialog = null;
	private MMHttpTask mCurrentTask = null;
	private int mEventType = 0;
	private HttpController mHttpController = null;
	private int mIconResId = 0;
	private ImageView mImageIconView = null;
	private View.OnClickListener mJustReturnListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			DialogActivity.this.finish();
		}
	};
	private View.OnClickListener mLoginListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			// if (NetUtil.isConnection())
			// {
			// if (NetUtil.isNetStateWap())
			// {
			// Intent intent = new Intent(DialogActivity.this,
			// MusicOnlineWapLoginActivity.class);
			// intent.putExtra("FROMTYPE", 6);
			// startActivity(intent);
			// } else
			// {
			// Intent intent1 = new Intent(DialogActivity.this,
			// LoginActivity.class);
			// intent1.putExtra("FROMTYPE", 6);
			// startActivity(intent1);
			// }
			// finish();
			// } else
			// {
			// Uiutil.ifSwitchToWapDialog(DialogActivity.this, true);
			// }
		}
	};
	private String mMessageText = null;
	private TextView mMessageView = null;
	private View.OnClickListener mOrderHighMemberMusicListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			DialogActivity.this.reqMemberButton("gj");
		}
	};
	private View.OnClickListener mOrderOnlineListenMusicListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			char c;
			if (NetUtil.isNetStateWap())
				c = '\u040D';
			else
				c = '\u13B0';
			SendSubscribeCommand(c);
			finish();
		}
	};
	private View.OnClickListener mOrderSpecialMemberMusicListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			reqMemberButton("tj");
		}
	};
	private int mTitleResId = 0;

	private void SendSubscribeCommand(int paramInt)
	{
		logger.v("SendSubscribeCommand() ---> Enter");
		long l = System.currentTimeMillis();
		String str1 = new SimpleDateFormat("yyyyMMddhhmmss").format(Long
				.valueOf(l));
		String str2 = Util.getRandKey(
				GlobalSettingParameter.SERVER_INIT_PARAM_MDN, str1);
		MMHttpRequest localMMHttpRequest = MMHttpRequestBuilder
				.buildRequest(paramInt);
		localMMHttpRequest.addHeader("mdn",
				GlobalSettingParameter.SERVER_INIT_PARAM_MDN);
		localMMHttpRequest.addHeader("mode", "chinamobile");
		localMMHttpRequest.addHeader("randkey", str2);
		localMMHttpRequest.addHeader("timestep", str1);
		HttpControllerImpl.getInstance(MobileMusicApplication.getInstance())
				.sendRequest(localMMHttpRequest);
		Toast.makeText(this,
				R.string.sent_order_online_listen_subscribe_activity, 1).show();
		logger.v("SendSubscribeCommand() ---> Exit");
	}

	private void onHttpResponse(MMHttpTask mmhttptask)
	{
		logger.v("onHttpResponse() ---> Enter");
		int i = mmhttptask.getRequest().getReqType();
		if (new XMLParser(mmhttptask.getResponseBody()).getRoot() == null)
		{
			this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
					getText(R.string.title_information_common),
					getText(R.string.fail_to_parse_xml_common),
					new View.OnClickListener()
					{
						public void onClick(View paramAnonymousView)
						{
							if (DialogActivity.this.mCurrentDialog != null)
							{
								DialogActivity.this.mCurrentDialog.dismiss();
								DialogActivity.this.mCurrentDialog = null;
							}
						}
					});
		} else
		{
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
			switch (i)
			{
			default:
				break;
			case 1042:
			case 5045:
				// onMemershipResponse(mmhttptask);
				break;
			}
		}
		logger.v("onHttpResponse() ---> Exit");
	}

	private void onMemershipResponse(MMHttpTask mmhttptask)
	{
		logger.v("onMemershipResponse() ---> Enter");
		XMLParser xmlparser = new XMLParser(mmhttptask.getResponseBody());
		if (xmlparser.getValueByTag("code") != null
				&& xmlparser.getValueByTag("code").equals("000000"))
		{
			if (isSpecial)
				GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER = "3";
			else
				GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER = "2";
			showReqOnlineMusicOrderDialog();
			logger.v("onMemershipResponse() ---> Enter");
		} else
		{
			mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
					getText(R.string.title_information_common),
					xmlparser.getValueByTag("info"),
					new android.view.View.OnClickListener()
					{

						public void onClick(View view)
						{
							if (mCurrentDialog != null)
							{
								mCurrentDialog.dismiss();
								mCurrentDialog = null;
							}
						}
					});
		}
	}

	private void onSendHttpRequestFail(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestFail() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(R.string.title_information_common),
				getText(R.string.getfail_data_error_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (DialogActivity.this.mCurrentDialog != null)
						{
							DialogActivity.this.mCurrentDialog.dismiss();
							DialogActivity.this.mCurrentDialog = null;
						}
					}
				});
		logger.v("onSendHttpRequestFail() ---> Enter");
	}

	private void onSendHttpRequestTimeOut(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestTimeOut() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(R.string.title_timeout_common),
				getText(R.string.connect_timeout_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (DialogActivity.this.mCurrentDialog != null)
						{
							DialogActivity.this.mCurrentDialog.dismiss();
							DialogActivity.this.mCurrentDialog = null;
						}
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	private void reqMemberButton(String s)
	{
		logger.v("reqHighMemberButton() ---> Enter");
		char c;
		MMHttpRequest mmhttprequest;
		String s1;
		long l;
		String s2;
		String s3;
		if (s.equals("tj"))
			isSpecial = true;
		else
			isSpecial = false;
		if (NetUtil.isNetStateWap())
			c = '\u0412';
		else
			c = '\u13B5';
		mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
		s1 = GlobalSettingParameter.SERVER_INIT_PARAM_MDN;
		l = System.currentTimeMillis();
		s2 = (new SimpleDateFormat("yyyyMMddhhmmss")).format(Long.valueOf(l));
		s3 = Util.getRandKey(s1, s2);
		mmhttprequest.addHeader("mdn",
				GlobalSettingParameter.SERVER_INIT_PARAM_MDN);
		mmhttprequest.addHeader("mode", "chinamobile");
		mmhttprequest.addHeader("timestep", s2);
		mmhttprequest.addHeader("randkey", s3);
		mmhttprequest.addUrlParams("type", s);
		mCurrentDialog = DialogUtil.showIndeterminateProgressDialog(this,
				R.string.loading_for_business);
		mCurrentTask = mHttpController.sendRequest(mmhttprequest);
		logger.v("reqHighMemberButton() ---> Exit");
	}

	private void setDialogStyle()
	{
		logger.v("setDialogStyle() ---> Enter");
		switch (this.mEventType)
		{
		default:
			break;
		case 1013:
		case 1000:
		case 1001:
		case 1002:
		case 1003:
		case 1004:
		case 1005:
		case 1006:
		case 1007:
		case 1008:
		case 1009:
		case 1010:
		case 1015:
			setOneBtnView(mIconResId, mTitleResId, mMessageText,
					mJustReturnListener);
			break;
		case 1017:
			setOneBtnView(mIconResId, mTitleResId, mMessageText,
					mJustReturnListener);
			break;
		case 1011:
			setTwoBtnView(mIconResId, mTitleResId, mMessageText,
					mOrderOnlineListenMusicListener, mJustReturnListener);
			break;
		case 1012:
			setTwoBtnView(mIconResId, mTitleResId, mMessageText,
					mOrderHighMemberMusicListener, mJustReturnListener);
			break;
		case 1014:
			setTwoBtnView(mIconResId, mTitleResId, mMessageText,
					mOrderSpecialMemberMusicListener, mJustReturnListener);
			break;
		case 1016:
			setTwoBtnView(mIconResId, mTitleResId, mMessageText,
					mLoginListener, mJustReturnListener);
			break;
		}
	}

	private void setOneBtnView(int i, int j, String s,
			android.view.View.OnClickListener onclicklistener)
	{
		logger.v("setOneBtnView() ---> Enter");
		mAlertTitleView.setText(j);
		mMessageView.setText(s);
		mImageIconView.setImageResource(i);
		if (onclicklistener != null)
			mButton1.setOnClickListener(onclicklistener);
		else
			mButton1.setOnClickListener(mJustReturnListener);
		mButton2.setVisibility(8);
		mButton3.setVisibility(8);
		logger.v("setOneBtnView() ---> Exit");
	}

	private void setTwoBtnView(int i, int j, String s,
			android.view.View.OnClickListener onclicklistener,
			android.view.View.OnClickListener onclicklistener1)
	{
		logger.v("setTwoBtnView() ---> Enter");
		mAlertTitleView.setText(j);
		mMessageView.setText(s);
		mImageIconView.setImageResource(i);
		if (onclicklistener != null)
			mButton1.setOnClickListener(onclicklistener);
		else
			mButton1.setOnClickListener(mJustReturnListener);
		if (onclicklistener1 != null)
			mButton2.setOnClickListener(onclicklistener1);
		else
			mButton2.setOnClickListener(mJustReturnListener);
		mButton3.setVisibility(8);
		logger.v("setTwoBtnView() ---> Exit");
	}

	private void showReqOnlineMusicOrderDialog()
	{
		logger.v("showReqOnlineMusicOrderDialog() ---> Enter");
		String s;
		if (isSpecial)
			s = getString(R.string.special_member_free_for_online_music_order_msg);
		else
			s = getString(R.string.hihg_member_free_for_online_music_order_msg);
		mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(this,
				getText(R.string.title_information_common), s,
				new android.view.View.OnClickListener()
				{

					public void onClick(View view)
					{
						char c;
						if (NetUtil.isNetStateWap())
							c = '\u040D';
						else
							c = '\u13B0';
						SendSubscribeCommand(c);
						finish();
						if (mCurrentDialog != null)
							mCurrentDialog.dismiss();
					}
				}, new android.view.View.OnClickListener()
				{

					public void onClick(View view)
					{
						if (mCurrentDialog != null)
							mCurrentDialog.dismiss();
						finish();
					}
				});
		logger.v("showReqOnlineMusicOrderDialog() ---> Enter");
	}

	public void CancelPreviousReq()
	{
		logger.v("CancelPreviousReq() ---> Enter");
		if (this.mCurrentTask != null)
		{
			this.mHttpController.cancelTask(this.mCurrentTask);
			this.mCurrentTask = null;
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
		}
		logger.v("CancelPreviousReq() ---> Exit");
	}

	public void handleMMHttpEvent(Message paramMessage)
	{
		logger.v("handleMMHttpEvent() ---> Enter");
		MMHttpTask mmhttptask = (MMHttpTask) paramMessage.obj;
		if (mmhttptask != null && mCurrentTask != null
				&& mmhttptask.getTransId() == mCurrentTask.getTransId())
		{
			if (this.mCurrentDialog != null)
			{
				this.mCurrentDialog.dismiss();
				this.mCurrentDialog = null;
			}
			this.mCurrentTask = null;
			switch (paramMessage.what)
			{
			case 3005:
			default:
				logger.v("handleMMHttpEvent() ---> exit");
				break;
			case 3003:
				// onHttpResponse(mmhttptask);
				break;
			case 3004:
				// onSendHttpRequestFail(mmhttptask);
				break;
			case 3006:
				// onSendHttpRequestTimeOut(mmhttptask);
				break;
			case 3007:
				// Uiutil.ifSwitchToWapDialog(this);
				break;
			case 3008:
				// Uiutil.ifSwitchToWapDialog(this);
				break;
			}
		}
	}

	public void handleSystemEvent(Message paramMessage)
	{
		logger.v("handleSystemEvent() ---> Enter");
		switch (paramMessage.what)
		{
		default:
			break;
		case 22:
			finish();
			break;
		}
		logger.v("handleSystemEvent() ---> Exit");
	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		this.mApp = ((MobileMusicApplication) getApplication());
		setContentView(R.layout.dialog_activity_layout);
		this.mController = ((MobileMusicApplication) getApplication())
				.getController();
		this.mHttpController = this.mController.getHttpController();
		this.mAlertTitleView = ((TextView) findViewById(R.id.alertTitle));
		this.mMessageView = ((TextView) findViewById(R.id.message));
		this.mImageIconView = ((ImageView) findViewById(R.id.icon));
		this.mButton1 = ((Button) findViewById(R.id.button1));
		this.mButton2 = ((Button) findViewById(R.id.button2));
		this.mButton3 = ((Button) findViewById(R.id.button3));
		Intent localIntent = getIntent();
		this.mEventType = localIntent.getIntExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.eventtype", 0);
		this.mIconResId = localIntent.getIntExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.iconresid", 0);
		this.mTitleResId = localIntent.getIntExtra(
				"cmccwm.mobilemusic.ui.activity.DialogActivity.titleresid", 0);
		this.mMessageText = localIntent
				.getStringExtra("cmccwm.mobilemusic.ui.activity.DialogActivity.messagetext");
		logger.v("onCreate() ---> Exit");
	}

	protected void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		super.onDestroy();
		logger.v("onDestroy() ---> Exit");
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
		if (paramInt == 4)
			AsyncToastDialogController.getInstance(this.mApp)
					.setPauseByThirdSeconds(false);
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	public void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		MobclickAgent.onPause(this);
		CancelPreviousReq();
		this.mController.removeHttpEventListener(3003, this);
		this.mController.removeHttpEventListener(3004, this);
		this.mController.removeHttpEventListener(3006, this);
		this.mController.removeHttpEventListener(3007, this);
		this.mController.removeHttpEventListener(3008, this);
		this.mController.removeSystemEventListener(22, this);
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		MobclickAgent.onResume(this);
		this.mController.addHttpEventListener(3003, this);
		this.mController.addHttpEventListener(3004, this);
		this.mController.addHttpEventListener(3006, this);
		this.mController.addHttpEventListener(3007, this);
		this.mController.addHttpEventListener(3008, this);
		this.mController.addSystemEventListener(22, this);
		setDialogStyle();
		logger.v("onResume() ---> Exit");
	}
}