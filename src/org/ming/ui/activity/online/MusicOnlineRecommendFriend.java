package org.ming.ui.activity.online;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.MobileMusicApplication;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpTask;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.view.TitleBarView;
import org.ming.ui.widget.PlayerStatusBar;
import org.ming.util.MyLogger;
import org.ming.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cm.omp.fuction.RingbackManagerInterface;
import com.cm.omp.fuction.data.CodeMessageObject;

public class MusicOnlineRecommendFriend extends Activity
{
	private static final MyLogger logger = MyLogger
			.getLogger("MusicOnlineRecommendFriend");
	private Button mBtnViewContact = null;
	private int mBusinessType;
	private Button mButtonSend = null;
	private View.OnClickListener mClickListerner = new View.OnClickListener()
	{
		public void onClick(View view)
		{
			logger.v("mClickListerner ---> Enter");

			if (view.equals(mBtnViewContact))
			{
				if (pullPhoneNum == null || "".equals(pullPhoneNum))
				{
					Intent intent = new Intent(MusicOnlineRecommendFriend.this,
							ContactsAllActivity.class);
					intent.putStringArrayListExtra("CONTACTNAME",
							(ArrayList) namelist);
					intent.putStringArrayListExtra("CONTACTNUMBER",
							(ArrayList) numberlist);
					startActivityForResult(intent, 0);
				} else
				{
					// 推荐歌曲
					if (mBusinessType != 4)
					{
						String as[];
						Pattern pattern;
						as = pullPhoneNum.split("\\|");
						pattern = Pattern
								.compile("^[0-9]{11}$|^(\\+86|86)[0-9]{11}$");
						for (int j = 0; j < as.length; j++)
						{
							String s = as[j].replace(" ", "");
							if (!pattern.matcher(s).matches())
							{
								Toast.makeText(
										MusicOnlineRecommendFriend.this,
										getString(
												R.string.recommend_invalid_number,
												new Object[] { s }), 0).show();
							}
						}
						Intent intent = new Intent(
								MusicOnlineRecommendFriend.this,
								ContactsAllActivity.class);
						intent.putStringArrayListExtra("CONTACTNAME",
								(ArrayList) namelist);
						intent.putStringArrayListExtra("CONTACTNUMBER",
								(ArrayList) numberlist);
						startActivityForResult(intent, 0);
					} else
					// 赠送歌曲
					{
						String as1[];
						as1 = pullPhoneNum.split("\\|");
						for (int l = 0; l < as1.length; l++)
						{
							String s1 = as1[l].replace(" ", "");
							if (!Util.isChinaMobileMobileNumber(s1))
							{
								if (s1.length() > 10)
								{
									String s2 = s1.substring(0, 3);
									String s3 = s1.substring(-5 + s1.length(),
											s1.length());
									s1 = (new StringBuilder(String.valueOf(s2)))
											.append("...").append(s3)
											.toString();
								}
								Toast.makeText(
										MusicOnlineRecommendFriend.this,
										getString(
												R.string.recommend_invalid_mobile_number,
												new Object[] { s1 }), 0).show();
								return;
							}
						}
						Intent intent = new Intent(
								MusicOnlineRecommendFriend.this,
								ContactsAllActivity.class);
						intent.putStringArrayListExtra("CONTACTNAME",
								(ArrayList) namelist);
						intent.putStringArrayListExtra("CONTACTNUMBER",
								(ArrayList) numberlist);
						startActivityForResult(intent, 0);
						logger.v("mClickListerner ---> Exit");
					}
				}
			} else if (view.equals(mButtonSend)) // 确认赠送，开始赠送
			{
				onSendRecommendInfo();
				logger.v("mClickListerner ---> Exit");
			}
		}
	};

	private void onSendRecommendInfo()
	{
		logger.v("onSendRecommendInfo() ---> Enter");
		String s = mNumberEdit.getText().toString().trim();
		if (s == null || s.equals(""))
		{
			CharSequence charsequence = getText(R.string.title_information_common);
			CharSequence charsequence1 = getText(R.string.please_input_phone_number_recommend_activity);
			android.view.View.OnClickListener onclicklistener = new android.view.View.OnClickListener()
			{
				public void onClick(View view)
				{
					if (mCurrentDialog != null)
					{
						mCurrentDialog.dismiss();
						mCurrentDialog = null;
					}
				}
			};
			mCurrentDialog = DialogUtil.show1BtnDialogWithTitleMsg(this,
					charsequence, charsequence1, onclicklistener);
			mNumberEdit.requestFocus();
		} else
		{
			pullPhoneNum = pullPhoneNum.replace(" ", "");
			if (pullPhoneNum == null && pullPhoneNum.indexOf(" ") != -1)
			{
				Toast.makeText(this,
						R.string.error_format_phone_number_recommend_activity,
						0).show();
				return;
			} else
			{
				String as1[];
				as1 = pullPhoneNum.split("\\|");
				String s8;
				String s9;
				String s10;
				for (int i1 = 0; i1 < as1.length; i1++)
				{
					s8 = as1[i1].replace(" ", "");
					if (!Util.isChinaMobileMobileNumber(s8))
					{
						if (s8.length() > 10)
						{
							s9 = s8.substring(0, 3);
							s10 = s8.substring(-5 + s8.length(), s8.length());
							s8 = (new StringBuilder(String.valueOf(s9)))
									.append("...").append(s10).toString();
						}
						Toast.makeText(
								this,
								getString(
										R.string.recommend_invalid_mobile_number,
										new Object[] { s8 }), 0).show();
						return;
					}
				}
				new Thread(new T7()).start();
			}
		}
	}

	private String musicid;
	private String singerName;
	private String songName;
	private String ringSongPrice;
	private Controller mController;
	private TextView mCopyright;
	private Dialog mCurrentDialog = null;
	private MMHttpTask mCurrentTask;
	private TextView mFriendNameTitle;
	private TextView mFriendNameView;
	private TextView mHintTxt;
	private HttpController mHttpController;
	private boolean mIsInital = false;
	private TextView mNothingView;
	private EditText mNumberEdit;
	private DialogInterface.OnCancelListener mOnCancelListner = new DialogInterface.OnCancelListener()
	{
		public void onCancel(DialogInterface paramAnonymousDialogInterface)
		{
			MusicOnlineRecommendFriend.logger.v("mOnCancelListner ---> Enter");
			MusicOnlineRecommendFriend.logger.v("mOnCancelListner ---> Exit");
		}
	};
	private PlayerStatusBar mPlayerStatusBar = null;
	private TextView mPromptTxt;
	private LinearLayout mRetryLayout;
	private TextView mSinger;
	private TextView mSongName;
	private TextWatcher mTextWatcher = new TextWatcher()
	{
		public void afterTextChanged(Editable editable)
		{
			String as[];
			MusicOnlineRecommendFriend.logger.v("mTextWatcher ---> Enter");
			as = editable.toString().split(",");
			showPhoneNumName = "";
			pullPhoneNum = "";
			namelist.clear();
			numberlist.clear();
			for (int i = 0; i < as.length; i++)
			{
				if (as[i].contains("<") && as[i].contains(">"))
				{
					numberlist.add(as[i].substring(1 + as[i].lastIndexOf("<"),
							as[i].lastIndexOf(">")));
					namelist.add(as[i].substring(0, as[i].lastIndexOf("<")));
				} else if (!as[i].equals(""))
				{
					numberlist.add(as[i]);
					namelist.add(as[i]);
				}
			}
			for (int j = 0; j < numberlist.size(); j++)
			{
				pullPhoneNum = (new StringBuilder(String.valueOf(pullPhoneNum)))
						.append((String) numberlist.get(j)).append("|")
						.toString();
				showPhoneNumName = (new StringBuilder(
						String.valueOf(showPhoneNumName)))
						.append((String) namelist.get(j)).append(",")
						.toString();
			}
			if (showPhoneNumName.length() > 1)
			{
				showPhoneNumName = showPhoneNumName.substring(0, -1
						+ showPhoneNumName.length());
				mFriendNameView.setText(showPhoneNumName);
			}
			logger.v("mTextWatcher ---> Exit");
		}

		public void beforeTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3)
		{}

		public void onTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3)
		{}
	};
	private TitleBarView mTitleBar;
	List<String> namelist = new ArrayList();
	List<String> numberlist = new ArrayList();
	private String pullPhoneNum;
	private String showPhoneNum = "";
	private String showPhoneNumName = "";

	protected void onActivityResult(int paramInt1, int paramInt2,
			Intent paramIntent)
	{
		logger.v("onActivityResult() ---> Enter");
		if (paramIntent != null)
		{
			ArrayList localArrayList1 = paramIntent
					.getStringArrayListExtra("CONTACTNAME");
			ArrayList localArrayList2 = paramIntent
					.getStringArrayListExtra("CONTACTNUMBER");
			if ((localArrayList2 == null) || (localArrayList2.size() == 0))
			{
				this.mNumberEdit.setText("");
				this.mFriendNameView.setText("");
				return;
			} else
			{
				this.namelist.clear();
				this.namelist.addAll(localArrayList1);
				this.numberlist.clear();
				this.numberlist.addAll(localArrayList2);
				this.showPhoneNum = "";
				this.showPhoneNumName = "";
				this.pullPhoneNum = "";
				for (int i = 0; i < namelist.size(); i++)
				{
					this.showPhoneNum = (this.showPhoneNum
							+ (String) this.namelist.get(i) + "<"
							+ (String) this.numberlist.get(i) + ">,");
				}
				Editable localEditable = this.mNumberEdit.getText();
				if ((localEditable instanceof Spannable))
					Selection.setSelection((Spannable) localEditable,
							localEditable.length());
				this.mNumberEdit.setText(this.showPhoneNum);
				this.mNumberEdit.setSelection(this.showPhoneNum.length());
				this.mFriendNameView.setText(this.showPhoneNumName);
				super.onActivityResult(paramInt1, paramInt2, paramIntent);
				logger.v("onActivityResult() ---> Exit");
			}

		}
	}

	protected void onCreate(Bundle paramBundle)
	{
		logger.v("onCreate() ---> Enter");
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_online_recommend_friend);
		this.mHintTxt = ((TextView) findViewById(R.id.hint_txt));
		this.mPromptTxt = ((TextView) findViewById(R.id.prompt_txt));
		this.mCopyright = ((TextView) findViewById(R.id.copyright_txt));
		this.mSongName = ((TextView) findViewById(R.id.songname_txt));
		mSinger = (TextView) findViewById(R.id.singer_txt);
		mBtnViewContact = (Button) findViewById(R.id.btn_contacts);
		mBtnViewContact.setOnClickListener(mClickListerner);
		mNumberEdit = (EditText) findViewById(R.id.phone_num_edit_text);
		mNumberEdit.setKeyListener(DialerKeyListener.getInstance());
		mNumberEdit.addTextChangedListener(mTextWatcher);
		mNothingView = (TextView) findViewById(R.id.nothing);
		mFriendNameTitle = (TextView) findViewById(R.id.friend_name_title);
		mFriendNameView = (TextView) findViewById(R.id.friend_name_view);
		mPlayerStatusBar = (PlayerStatusBar) findViewById(R.id.playerStatusBar);
		mPlayerStatusBar.setVisibility(View.GONE);
		mButtonSend = (Button) findViewById(R.id.btn_done);
		mButtonSend.setVisibility(View.GONE);
		// 歌曲赠送
		this.mButtonSend.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				onSendRecommendInfo();
			}
		});
		this.mTitleBar = ((TitleBarView) findViewById(R.id.title_view));
		this.mTitleBar.setCurrentActivity(this);
		this.mTitleBar.setButtons(0);
		this.mController = Controller
				.getInstance((MobileMusicApplication) getApplication());
		this.mRetryLayout = ((LinearLayout) findViewById(R.id.refresh_layout));
		Intent localIntent = getIntent();
		this.mBusinessType = localIntent.getIntExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.type", 0);
		this.musicid = localIntent
				.getStringExtra("mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.musicid");
		this.singerName = localIntent
				.getStringExtra("mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.singerName");
		this.songName = localIntent
				.getStringExtra("mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.songName");
		this.ringSongPrice = localIntent
				.getStringExtra("mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.ringsongprice");
		requestSongInfo();
		logger.v("onCreate() ---> Exit");
	}

	protected void onDestroy()
	{
		super.onDestroy();
	}

	protected void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		this.mPlayerStatusBar.unRegistEventListener();
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		this.mPlayerStatusBar.registEventListener();
		logger.v("onResume() ---> Exit");
	}

	private void requestSongInfo()
	{
		mTitleBar.setTitle(R.string.activity_sendmusic_title); // 赠送歌曲给好友
		mButtonSend.setText(R.string.button_present_song); // 赠送给好友
		mFriendNameTitle.setText(R.string.gif_friend_textview); // 赠送歌曲给：
		mSongName.setText(getString(R.string.recommend_song_name,
				new Object[] { songName }));
		mSinger.setText(getString(R.string.recommend_singer,
				new Object[] { singerName }));
		mPromptTxt.setText(getString(R.string.recommend_price,
				new Object[] { ringSongPrice }));
		mHintTxt.setText(getString(R.string.hint_send_friend_song));
		mPlayerStatusBar.setVisibility(View.VISIBLE);
		mCopyright.setVisibility(View.GONE);
		mNothingView.setVisibility(View.GONE);
		mButtonSend.setVisibility(View.VISIBLE);
	}

	// 赠送单首彩铃的Thread
	class T7 extends Thread
	{
		@Override
		public void run()
		{
			Looper.prepare();
			super.run();
			// 调用SDK接口赠送单首彩铃
			if (numberlist != null && numberlist.size() > 0)
			{
				for (int i = 0; i < numberlist.size(); i++)
				{
					CodeMessageObject cmo6 = RingbackManagerInterface
							.presentRingback(MusicOnlineRecommendFriend.this,
									musicid, numberlist.get(i));
					if (cmo6 != null)
					{
						// 判断返回结果
						if ("000000".equals(cmo6.getCode()))
						{
							// 成功
							mUIHandler.obtainMessage(0).sendToTarget();
						} else
						{
							Message m = new Message();
							m.what = 1;
							m.obj = cmo6;
							mUIHandler.sendMessage(m);
						}
					} else
					{
						mUIHandler.obtainMessage(2).sendToTarget();
					}
				}
			}
			Looper.loop();
		}
	}

	private UIHandler mUIHandler = new UIHandler();

	private class UIHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			switch (msg.what)
			{
			case 0:
				Toast.makeText(MusicOnlineRecommendFriend.this, "赠送彩铃成功", 0)
						.show();
				break;
			case 1:
				Toast.makeText(MusicOnlineRecommendFriend.this, "操作失败", 0)
						.show();
				break;
			case 2:
				Toast.makeText(MusicOnlineRecommendFriend.this, "请设置正确参数", 0)
						.show();
				break;
			default:
				break;
			}
		}
	}
}
