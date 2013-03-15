package org.ming.ui.activity.more;

import org.ming.R;
import org.ming.center.ConfigSettingParameter;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.system.SystemEventListener;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.TextView;

public class MobileMusicAboutActivity extends Activity implements
		SystemEventListener
{
	public static final MyLogger logger = MyLogger
			.getLogger("MobileMusicAboutActivity");
	private Controller mController;
	private TextView mTextVersion;
	private TextView textAbout;
	private TitleBarView titleBar;

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
		setContentView(R.layout.activity_more_about_layout);
		mController = Controller
				.getInstance((MobileMusicApplication) getApplication());
		titleBar = (TitleBarView) findViewById(R.id.mobile_about);
		titleBar.setCurrentActivity(this);
		titleBar.setButtons(0);
		titleBar.setTitle(R.string.app_about);
		mTextVersion = (TextView) findViewById(R.id.mobile_text_version);
		mTextVersion
				.setText((new StringBuilder())
						.append(getText(R.string.app_version))
						.append(GlobalSettingParameter.LOCAL_PARAM_VERSION)
						.append(".")
						.append(GlobalSettingParameter.LOCAL_SVN_NUMBER)
						.append("\n").append(getText(R.string.app_build_id))
						.append(ConfigSettingParameter.LOCAL_PARAM_BUILD_ID)
						.toString());
		SpannableString spannablestring = new SpannableString(getResources()
				.getText(R.string.app_about_info));
		spannablestring.setSpan(new URLSpan("http://m.10086.cn"), 4, 14, 33);
		textAbout = (TextView) findViewById(R.id.mobile_text_about);
		textAbout.setText(spannablestring);
		textAbout.setClickable(true);
		textAbout.setMovementMethod(LinkMovementMethod.getInstance());
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
		this.mController.removeSystemEventListener(22, this);
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		super.onResume();
		this.mController.addSystemEventListener(22, this);
	}
}