package org.ming.ui.activity.online;

import org.ming.R;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MusicOnlineInformationDetailByUrlActivity extends Activity
{
	protected static String INTENT_DATA_TITLE = "title";
	protected static String INTENT_DATA_URL;
	private static final MyLogger logger = MyLogger
			.getLogger("MusicOnlineInformationDetailByUrlActivity");
	TitleBarView mTitleBar;
	private WebView mWebView;
	TextView urlLoadingProgress;
	ProgressBar urlLoadingProgressBar;

	static
	{
		INTENT_DATA_URL = "url";
	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_online_music_information_detail_webview);
		this.mTitleBar = ((TitleBarView) findViewById(R.id.title_view));
		this.mTitleBar.setCurrentActivity(this);
		this.mTitleBar.setTitle(R.string.title_infomation_activity);
		this.urlLoadingProgressBar = ((ProgressBar) findViewById(R.id.url_loading_progressbar));
		this.urlLoadingProgress = ((TextView) findViewById(R.id.url_loading_progress));
		this.mWebView = ((WebView) findViewById(R.id.detail_web));
		this.mTitleBar.setButtons(0);
		refreshUI();
		logger.v("onCreate() ---> Exit");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	void refreshUI()
	{
		WebSettings localWebSettings = this.mWebView.getSettings();
		localWebSettings.setJavaScriptEnabled(true);
		localWebSettings.setSaveFormData(false);
		localWebSettings.setSavePassword(false);
		localWebSettings.setSupportZoom(false);
		localWebSettings.setBuiltInZoomControls(true);
		localWebSettings.setCacheMode(2);
		this.mWebView.setWebChromeClient(new WebChromeClient()
		{
			public void onProgressChanged(WebView paramAnonymousWebView,
					int paramAnonymousInt)
			{
				if (MusicOnlineInformationDetailByUrlActivity.this.urlLoadingProgress != null)
					MusicOnlineInformationDetailByUrlActivity.this.urlLoadingProgress
							.setText(paramAnonymousInt + "%");
				if (paramAnonymousInt == 100)
				{
					MusicOnlineInformationDetailByUrlActivity.this.urlLoadingProgress
							.setVisibility(8);
					MusicOnlineInformationDetailByUrlActivity.this.urlLoadingProgressBar
							.setVisibility(8);
				}
				super.onProgressChanged(paramAnonymousWebView,
						paramAnonymousInt);
			}
		});
		if (getIntent() != null)
		{
			String str1 = getIntent().getStringExtra(INTENT_DATA_URL);
			String str2 = getIntent().getStringExtra(INTENT_DATA_TITLE);
			if (str1 != null)
			{
				if (this.urlLoadingProgress != null)
					this.urlLoadingProgress.setText("0%");
				this.mWebView.setWebViewClient(new WeiboWebViewClient(
						this.urlLoadingProgress));
				this.mWebView.loadUrl(str1);
			}
			if (str2 != null)
				this.mTitleBar.setTitle(str2);
		}
	}

	private class WeiboWebViewClient extends WebViewClient
	{
		TextView progress;

		public WeiboWebViewClient(TextView arg2)
		{
			Object localObject = null;
			this.progress = (TextView) localObject;
		}

		public void onPageFinished(WebView paramWebView, String paramString)
		{
			super.onPageFinished(paramWebView, paramString);
		}

		public void onPageStarted(WebView paramWebView, String paramString,
				Bitmap paramBitmap)
		{
			super.onPageStarted(paramWebView, paramString, paramBitmap);
		}

		public void onReceivedError(WebView paramWebView, int paramInt,
				String paramString1, String paramString2)
		{
			super.onReceivedError(paramWebView, paramInt, paramString1,
					paramString2);
		}

		public void onReceivedSslError(WebView paramWebView,
				SslErrorHandler paramSslErrorHandler, SslError paramSslError)
		{
			paramSslErrorHandler.proceed();
		}

		public boolean shouldOverrideUrlLoading(WebView paramWebView,
				String paramString)
		{
			paramWebView.loadUrl(paramString);
			if (this.progress != null)
			{
				this.progress.setText("0%");
				this.progress.setVisibility(0);
				MusicOnlineInformationDetailByUrlActivity.this.urlLoadingProgressBar
						.setVisibility(0);
			}
			return true;
		}
	}
}