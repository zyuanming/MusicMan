package org.ming.ui.activity.more;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpEventListener;
import org.ming.center.http.MMHttpRequest;
import org.ming.center.http.MMHttpRequestBuilder;
import org.ming.center.http.MMHttpTask;
import org.ming.center.system.SystemEventListener;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.XMLParser;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MobileMusicFeedBackActivity extends Activity implements
		MMHttpEventListener, SystemEventListener
{
	public static final MyLogger logger = MyLogger
			.getLogger("MobileMusicFeedBackActivity");
	private View.OnClickListener clickListener = new android.view.View.OnClickListener()
	{
		public void onClick(View view)
		{
			String s = mEditSms.getText().toString().trim();
			if (s == null || "".equals(s))
				Toast.makeText(MobileMusicFeedBackActivity.this,
						R.string.feedback_send, 0).show();
			else if (s.length() > count)
			{
				Toast.makeText(MobileMusicFeedBackActivity.this,
						R.string.feedback_send_failed, 0).show();
			} else
			{
				String s1 = mUserPhoneNum.getText().toString();
				if (s1.indexOf(" ") != -1)
				{
					Toast.makeText(MobileMusicFeedBackActivity.this,
							R.string.wrong_feedback_msg, 0).show();
				} else
				{
					char c;
					MMHttpRequest mmhttprequest;
					if (NetUtil.isNetStateWap())
						c = '\u041E';
					else
						c = '\u13C1';
					mmhttprequest = MMHttpRequestBuilder.buildRequest(c);
					mmhttprequest.addUrlParams("contactinfo", s1);
					mmhttprequest.setReqBodyString(s);
					mCurrentTask = mHttpController.sendRequest(mmhttprequest);
					mCurrentDialog = DialogUtil.show1BtnProgressDialog(
							MobileMusicFeedBackActivity.this,
							R.string.mobile_music_sms_clent, R.string.cancel,
							dailogListener);
				}
			}
		}

	};
	private int count = 200;
	private View.OnClickListener dailogListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
		}
	};
	private TextView mCharCount;
	private Controller mController;
	private Dialog mCurrentDialog = null;
	private MMHttpTask mCurrentTask;
	private EditText mEditSms;
	private HttpController mHttpController;
	private TextWatcher mTextWatcher = new TextWatcher()
	{
		public void afterTextChanged(Editable editable)
		{
			if (count - editable.toString().trim().length() >= 0)
			{
				TextView textview1 = mCharCount;
				String s1 = (String) getText(R.string.left_num_count);
				Object aobj1[] = new Object[1];
				aobj1[0] = Integer.valueOf(count
						- editable.toString().trim().length());
				textview1.setText(String.format(s1, aobj1));
			} else
			{
				TextView textview = mCharCount;
				StringBuilder stringbuilder = new StringBuilder(
						"<font color=#d71345>");
				String s = (String) getText(R.string.left_num_count);
				Object aobj[] = new Object[1];
				aobj[0] = String.valueOf(count
						- editable.toString().trim().length());
				textview.setText(Html.fromHtml(stringbuilder
						.append(String.format(s, aobj)).append("</font> ")
						.toString()));
			}
		}

		public void beforeTextChanged(CharSequence charsequence, int i, int j,
				int k)
		{}

		public void onTextChanged(CharSequence charsequence, int i, int j, int k)
		{}

	};
	private EditText mUserPhoneNum;
	private TitleBarView titleBar;

	private void onHttpResponse(MMHttpTask paramMMHttpTask)
	{
		int i = paramMMHttpTask.getRequest().getReqType();
		byte[] arrayOfByte = paramMMHttpTask.getResponseBody();
		switch (i)
		{
		default:
			return;
		case 1054:
		case 5057:
			XMLParser xmlparser = new XMLParser(arrayOfByte);
			if (xmlparser.getRoot() == null)
				mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(
						getParent(),
						getText(R.string.title_information_common),
						getText(R.string.fail_to_parse_xml_common),
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
			else if ("000000".equals(xmlparser.getValueByTag("code")))
			{
				Toast.makeText(this, R.string.feedback_ok, 0).show();
				finish();
			} else
			{
				Toast.makeText(this, xmlparser.getValueByTag("info"), 0).show();
			}
			break;
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
						if (mCurrentDialog != null)
						{
							mCurrentDialog.dismiss();
							mCurrentDialog = null;
						}
					}
				});
		logger.v("onSendHttpRequestFail() ---> Enter");
	}

	private void onSendHttpRequestTimeOut(MMHttpTask paramMMHttpTask)
	{
		logger.v("onSendHttpRequestTimeOut() ---> Enter");
		this.mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
				getText(R.string.title_information_common),
				getText(R.string.connect_timeout_common),
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						if (mCurrentDialog != null)
						{
							mCurrentDialog.dismiss();
							mCurrentDialog = null;
						}
					}
				});
		logger.v("onSendHttpRequestTimeOut() ---> Exit");
	}

	public void handleMMHttpEvent(Message message)
	{
		MMHttpTask mmhttptask = (MMHttpTask) message.obj;
		if (mmhttptask != null && mCurrentTask != null
				&& mmhttptask.getTransId() == mCurrentTask.getTransId())
		{
			mCurrentTask = null;
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			switch (message.what)
			{
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
			case 3008:
				Uiutil.ifSwitchToWapDialog(this);
				break;
			default:
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
			logger.v("handleSystemEvent() ---> Exit");
			return;
		case 22:
			finish();
			break;
		}
	}

	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_more_feed_back_layout);
		mController = Controller
				.getInstance((MobileMusicApplication) getApplication());
		mHttpController = mController.getHttpController();
		mCharCount = (TextView) findViewById(R.id.char_num_count);
		titleBar = (TitleBarView) findViewById(R.id.mobile_feed_back);
		titleBar.setRightBtnImage(R.drawable.btn_titlebar_send_selector);
		titleBar.setCurrentActivity(this);
		titleBar.setTitle(R.string.feedback);
		titleBar.setButtons(2);
		mEditSms = (EditText) findViewById(R.id.feed_back_username_edittext);
		mEditSms.addTextChangedListener(mTextWatcher);
		mUserPhoneNum = (EditText) findViewById(R.id.feed_back_user_phone_num_edittext);
		if (GlobalSettingParameter.useraccount != null)
		{
			mUserPhoneNum.setText(GlobalSettingParameter.useraccount.mMDN);
			mUserPhoneNum.setEnabled(false);
		} else
		{
			mUserPhoneNum.setEnabled(true);
			mUserPhoneNum.setKeyListener(DialerKeyListener.getInstance());
		}
		titleBar.setrightBtnListner(clickListener);
	}

	public void onDestroy()
	{
		logger.v("onDestroy() ---> Enter");
		super.onDestroy();
		logger.v("onDestroy() ---> Exit");
	}

	public void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
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
		super.onResume();
		this.mController.addHttpEventListener(3003, this);
		this.mController.addHttpEventListener(3005, this);
		this.mController.addHttpEventListener(3004, this);
		this.mController.addHttpEventListener(3006, this);
		this.mController.addHttpEventListener(3007, this);
		this.mController.addHttpEventListener(3008, this);
		this.mController.addSystemEventListener(22, this);
	}
}