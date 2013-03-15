package org.ming.ui.activity.online;

import java.util.ArrayList;

import org.ming.R;
import org.ming.center.Controller;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.download.DownLoadService2;
import org.ming.center.http.HttpController;
import org.ming.center.http.MMHttpTask;
import org.ming.ui.util.DialogUtil;
import org.ming.ui.util.Uiutil;
import org.ming.ui.view.TitleBarView;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.cm.omp.fuction.FullSongManagerInterface;
import com.cm.omp.fuction.RingbackManagerInterface;
import com.cm.omp.fuction.VibrateRingManagerInterface;
import com.cm.omp.fuction.data.BizInfo;
import com.cm.omp.fuction.data.CodeMessageObject;

public class MusicOnlineSetRingToneActivity extends Activity
{
	public static final String RETCODE_NOT_RING_USER = "301001";
	public static final String RETCODE_SUCCESS = "000000";
	private static final MyLogger logger = MyLogger
			.getLogger("MusicOnlineSetRingToneActivity");
	private Button mBtnDone = null;
	private int mBusinessType;
	private String bizCode; // 业务代码,只是单个
	private String bizType; // 业务类型
	private String bizDescription; // 业务描述
	private String bizOriginalPrice; // 原始价格
	private String bizSalePrice; // 销售价格
	private String singerName;
	private String songName;
	private String ringSongPrice; // 彩铃价格
	private String crbtValidity; // 彩铃有效期
	private View.OnClickListener mClickListerner = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			MusicOnlineSetRingToneActivity.logger
					.v("mClickListerner ---> Enter");
			if (!NetUtil.isConnection())
			{
				Uiutil.ifSwitchToWapDialog(MusicOnlineSetRingToneActivity.this,
						true);
				return;
			}
			switch (mBusinessType)
			{
			default:
				finish();

				break;
			case 1: // 振铃管理
				// 获取振铃下载的URL，成功后在Handler中新建线程下载
				mCurrentDialog = DialogUtil.showIndeterminateProgressDialog(
						MusicOnlineSetRingToneActivity.this, R.string.loading);
				getTheToneDownloadURL();
				break;
			case 2: // 彩铃管理
				// 订购彩铃
				mCurrentDialog = DialogUtil.showIndeterminateProgressDialog(
						MusicOnlineSetRingToneActivity.this, R.string.loading);
				buyTheRing();
				break;
			case 3: // 下载歌曲
				// 获取全曲下载的URL,成功后在Handler中新建线程下载
				new T3().start();
				break;
			}
		}
	};
	private String mMusicId;
	private Controller mController;
	private Dialog mCurrentDialog = null;
	private MMHttpTask mCurrentTask;
	private HttpController mHttpController;
	private TextView mInformationTxt;
	private DialogInterface.OnCancelListener mOnCancelListner = new DialogInterface.OnCancelListener()
	{
		public void onCancel(DialogInterface paramAnonymousDialogInterface)
		{
			MusicOnlineSetRingToneActivity.logger
					.v("mOnCancelListner ---> Enter");
			MusicOnlineSetRingToneActivity.this.CancelPreviousReq();
			MusicOnlineSetRingToneActivity.this.finish();
			MusicOnlineSetRingToneActivity.logger
					.v("mOnCancelListner ---> Enter");
		}
	};
	private TextView mPromptTxt;
	private TextView mSongName;
	private TitleBarView mTitleBar;
	private Dialog mToneDialog = null;
	private CheckBox setRing;

	// private DownloadService downLoadService;

	private void showApplayForColorRingDialog()
	{
		logger.v("showOrderColorRingDialog() ---> Enter");
		mToneDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(this,
				getText(R.string.title_order_tone_atdcontroller),
				GlobalSettingParameter.LOGIN_PARAM_FOR_COLOR_TONE_INFO,
				new android.view.View.OnClickListener()
				{
					public void onClick(View view)
					{
						if (mToneDialog != null)
							mToneDialog.dismiss();
						mCurrentDialog = DialogUtil
								.showIndeterminateProgressDialog(
										MusicOnlineSetRingToneActivity.this,
										R.string.loading);
						mCurrentDialog.setCancelable(false);
					}

				}, new android.view.View.OnClickListener()
				{
					public void onClick(View view)
					{
						if (mToneDialog != null)
							mToneDialog.dismiss();
					}

				});
		logger.v("showOrderColorRingDialog() ---> Exit");
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
		logger.v("CancelPreviousReq() ---> Enter");
	}

	// private ServiceConnection serviceConnection = new ServiceConnection()
	// {
	// // 连接服务失败后，该方法被调用
	// @Override
	// public void onServiceDisconnected(ComponentName name)
	// {
	// downLoadService = null;
	// Toast.makeText(MusicOnlineSetRingToneActivity.this,
	// "Service Failed.", Toast.LENGTH_SHORT).show();
	// }
	//
	// // 成功连接服务后，该方法被调用。在该方法中可以获得downLoadService对象
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service)
	// {
	// // 获得downLoadService对象
	// downLoadService = ((DownloadService.DownLoadServiceBinder) service)
	// .getService();
	// Toast.makeText(MusicOnlineSetRingToneActivity.this,
	// "Service Connected.", Toast.LENGTH_SHORT).show();
	// }
	// };

	private UIHandler mUIHandler = new UIHandler();

	private class UIHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			switch (msg.what)
			{
			case 0: // 出错
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				Toast.makeText(MusicOnlineSetRingToneActivity.this, "没有数据",
						Toast.LENGTH_SHORT).show();
				break;
			case 1: // 订购彩铃成功
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				Toast.makeText(MusicOnlineSetRingToneActivity.this, "订购彩铃成功",
						Toast.LENGTH_SHORT);

				// 判断是否设置当前铃声为默认铃声
				if (setRing != null && setRing.isChecked())
				{
					setDefaultRing();
				}
				break;
			case 2: // 非彩铃用户
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				mCurrentDialog = DialogUtil.show2BtnDialogWithIconTitleMsg(
						MusicOnlineSetRingToneActivity.this,
						getText(R.string.title_information_common),
						"你还没有开通彩铃功能，是否现在开通", new View.OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								// 开通彩铃用户
								openRingFeature();
							}
						}, new View.OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								if (mCurrentDialog != null)
								{
									mCurrentDialog.dismiss();
									mCurrentDialog = null;
								}
							}
						});
				break;
			case 3:
				// 全曲资费显示
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				if (bizDescription != null && bizSalePrice != null
						&& songName != null && singerName != null)
				{
					mPromptTxt.setText("资费说明：" + bizDescription + "("
							+ bizSalePrice + "元" + "/首" + ")");
					mSongName.setText("歌曲名称：" + songName);
					mInformationTxt.setText("歌手名称：" + singerName);
					Toast.makeText(MusicOnlineSetRingToneActivity.this, "操作成功",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 4:
				// 全曲下载
				Toast.makeText(MusicOnlineSetRingToneActivity.this, "现在开始下载",
						Toast.LENGTH_SHORT).show();
				String downloadUrl = (String) msg.obj;
				Intent serviceIntent = new Intent(
						MusicOnlineSetRingToneActivity.this,
						DownLoadService2.class);
				serviceIntent.putExtra("downloadUrl", downloadUrl);
				serviceIntent.putExtra("songName", songName);
				serviceIntent.putExtra("singerName", singerName);
				// bindService(serviceIntent, serviceConnection,
				// Context.BIND_AUTO_CREATE);
				startService(serviceIntent);
				break;
			case 5: // 开通彩铃功能成功
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				Toast.makeText(MusicOnlineSetRingToneActivity.this, "开通彩铃成功",
						Toast.LENGTH_SHORT).show();
				break;
			case 6: // 振铃资费显示
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				if (bizDescription != null && bizSalePrice != null
						&& songName != null && singerName != null)
				{
					double price = Double.valueOf(bizSalePrice);
					double price1;
					String price2;
					if (price > 100)
					{
						price1 = (double) (price / 100.00);
						price2 = String.valueOf(price1);
					} else
					{
						price2 = bizSalePrice;
					}
					mPromptTxt.setText("资费说明：" + bizDescription + "(" + price2
							+ " 元" + "/首" + ")");
					mSongName.setText("歌曲名称：" + songName);
					mInformationTxt.setText("歌手名称：" + singerName);
					Toast.makeText(MusicOnlineSetRingToneActivity.this,
							"获取振铃资费成功啦", Toast.LENGTH_SHORT).show();
				}
				break;
			case 7: // 振铃下载
				if (mCurrentDialog != null)
				{
					mCurrentDialog.dismiss();
					mCurrentDialog = null;
				}
				Toast.makeText(MusicOnlineSetRingToneActivity.this, "现在开始下载",
						Toast.LENGTH_SHORT).show();
				String downloadUrl1 = (String) msg.obj;
				Intent serviceIntent1 = new Intent(
						MusicOnlineSetRingToneActivity.this,
						DownLoadService2.class);
				serviceIntent1.putExtra("downloadUrl", downloadUrl1);
				serviceIntent1.putExtra("songName", songName);
				serviceIntent1.putExtra("singerName", singerName);
				startService(serviceIntent1);
				break;
			default:
				break;
			}
		}
	}

	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		logger.v("onCreate() ---> Enter");
		requestWindowFeature(1);
		setContentView(R.layout.activity_online_setring);
		mSongName = (TextView) findViewById(R.id.songname_txt);
		mInformationTxt = (TextView) findViewById(R.id.information_txt);
		mPromptTxt = (TextView) findViewById(R.id.prompt_txt);
		mBtnDone = (Button) findViewById(R.id.btn_done);
		mBtnDone.setOnClickListener(mClickListerner);
		mTitleBar = (TitleBarView) findViewById(R.id.title_view);
		mTitleBar.setCurrentActivity(this);
		mTitleBar.setButtons(0);
		mController = Controller
				.getInstance((MobileMusicApplication) getApplication());
		mHttpController = mController.getHttpController();
		Intent intent = getIntent();
		mMusicId = intent
				.getStringExtra("mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.musicid");
		singerName = intent
				.getStringExtra("mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.singername");
		songName = intent
				.getStringExtra("mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.songname");
		ringSongPrice = intent
				.getStringExtra("mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.ringsongprice");
		crbtValidity = intent
				.getStringExtra("mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.crbtValidity");
		mBusinessType = intent.getIntExtra(
				"mobi.redcloud.mobilemusic.MusicOnlineRecommendFriend.type", 0);
		logger.v("onCreate() ---> Exit");
	}

	protected void onPause()
	{
		logger.v("onPause() ---> Enter");
		super.onPause();
		CancelPreviousReq();
		logger.v("onPause() ---> Exit");
	}

	protected void onResume()
	{
		logger.v("onResume() ---> Enter");
		super.onResume();
		logger.v("onResume() ---> exit");
	}

	protected void onStop()
	{
		logger.v("onStop() ---> Enter");
		super.onStop();
		logger.v("onStop() ---> Exit");
	}

	@Override
	protected void onStart()
	{
		logger.v("onStart() ---> Enter");
		super.onStart();
		switch (this.mBusinessType)
		{
		case 1: // 振铃管理
			mTitleBar.setTitle(R.string.orderwindow_settone);

			// 查询，显示振铃管理策略
			this.mCurrentDialog = DialogUtil.show1BtnProgressDialog(this,
					R.string.loading, R.string.cancel,
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
			getTheToneInfos();
			break;
		case 2: // 来电铃声
			findViewById(R.id.set_ring_layout).setVisibility(View.VISIBLE);
			setRing = (CheckBox) findViewById(R.id.set_as_ring);
			mTitleBar.setTitle(R.string.orderwindow_setring);
			// 显示彩铃订购策略
			if (mCurrentDialog != null)
			{
				mCurrentDialog.dismiss();
				mCurrentDialog = null;
			}
			String price2;
			if (ringSongPrice != null)
			{
				double price = Double.valueOf(ringSongPrice);
				double price1;

				if (price > 10)
				{
					price1 = (double) (price / 100.00);
					price2 = String.valueOf(price1);
				} else
				{
					price2 = ringSongPrice;
				}
			} else
			{
				price2 = null;
			}
			mPromptTxt.setText("彩铃价格：" + price2 + "元" + " (彩铃失效时间："
					+ crbtValidity + ")");
			mSongName.setText("歌曲名称：" + songName);
			mInformationTxt.setText("歌手名称：" + singerName);
			break;
		case 3: // 全曲下载
			mTitleBar.setTitle(R.string.orderwindow_download);

			// 查询，显示全曲下载策略
			this.mCurrentDialog = DialogUtil.show1BtnProgressDialog(this,
					R.string.loading, R.string.cancel,
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
			getTheDownloadInfos();
			break;
		default:
			finish();
			logger.v("onStart() ---> Exit");
			return;
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// unbindService(serviceConnection);
	}

	private void getTheDownloadInfos()
	{
		new T2().start();
	}

	private void getTheRingInfos()
	{
		new T5().start();
	}

	private void buyTheRing()
	{
		new T4().start();
	}

	private void setDefaultRing()
	{

	}

	private void getTheToneInfos()
	{
		new T7().start();
	}

	private void openRingFeature()
	{
		new T1().start();
	}

	private void getTheToneDownloadURL()
	{
		new T8().start();
	}

	// 开通彩铃功能的Thread
	class T1 extends Thread
	{
		@Override
		public void run()
		{
			Looper.prepare();
			super.run();
			// 调用SDK接口开通彩铃功能
			CodeMessageObject cmo0 = RingbackManagerInterface
					.openRingbackByMonth(MusicOnlineSetRingToneActivity.this);
			if (cmo0 != null)
			{
				// 判断返回结果
				if ("000000".equals(cmo0.getCode()))
				{
					// 成功
					mUIHandler.obtainMessage(5).sendToTarget();
					return;
				}
			}
			mUIHandler.obtainMessage(0).sendToTarget();
			Looper.loop();
		}
	}

	// 查询全曲下载策略的Thread
	class T2 extends Thread
	{
		@Override
		public void run()
		{
			Looper.prepare();
			super.run();
			// 调用SDK接口查询全曲下载策略
			CodeMessageObject cmo2 = FullSongManagerInterface
					.queryFullSongDownloadWay(
							MusicOnlineSetRingToneActivity.this, mMusicId);
			if (cmo2 != null)
			{
				// 判断返回结果
				if ("000000".equals(cmo2.getCode()))
				{
					// 成功
					@SuppressWarnings("unchecked")
					ArrayList<BizInfo> list1 = (ArrayList<BizInfo>) cmo2
							.getObject();
					if (list1 != null)
					{
						bizCode = list1.get(list1.size() - 1).getBizCode();
						bizType = list1.get(list1.size() - 1).getBizType();
						bizDescription = list1.get(list1.size() - 1)
								.getDescription();
						bizOriginalPrice = list1.get(list1.size() - 1)
								.getOriginalPrice();
						bizSalePrice = list1.get(list1.size() - 1)
								.getSalePrice();
						mUIHandler.obtainMessage(3).sendToTarget();
						return;
					}
				}
			}
			mUIHandler.obtainMessage(0).sendToTarget();
			Looper.loop();
		}
	}

	// 获取全曲下载地址的Thread
	class T3 extends Thread
	{
		@Override
		public void run()
		{
			Looper.prepare();
			super.run();
			Log.d("ming", mMusicId);
			Log.d("ming", bizCode);
			Log.d("ming", bizType);
			// 调用SDK接口获取全曲下载地址
			CodeMessageObject cmo3 = FullSongManagerInterface
					.getFullSongDownloadUrl(
							MusicOnlineSetRingToneActivity.this, mMusicId,
							bizCode, bizType);
			if (cmo3 != null)
			{
				// 判断返回结果
				if ("000000".equals(cmo3.getCode()))
				{
					// 成功
					String downloadUrl = (String) cmo3.getObject();
					Message m = new Message();
					m.what = 4;
					m.obj = downloadUrl;
					mUIHandler.sendMessage(m);
					return;
				}
			}
			mUIHandler.obtainMessage(0).sendToTarget();
			Looper.loop();
		}
	}

	// 订购单首彩铃
	class T4 extends Thread
	{
		@Override
		public void run()
		{
			Looper.prepare();
			super.run();
			// 调用SDK接口订购彩铃
			CodeMessageObject cmo4 = RingbackManagerInterface.buyRingback(
					MusicOnlineSetRingToneActivity.this, mMusicId);
			if (cmo4 != null)
			{
				// 判断返回结果
				if ("000000".equals(cmo4.getCode()))
				{
					mUIHandler.obtainMessage(1).sendToTarget(); // 订购彩铃成功
				} else if ("301001".equals(cmo4.getCode())) // 非彩铃用户
				{
					mUIHandler.obtainMessage(2).sendToTarget();
				}
			}
			mUIHandler.obtainMessage(0).sendToTarget();
			Looper.loop();
		}
	}

	// 获取彩铃试听地址、彩铃分发价格、彩铃失效期的Thread
	class T5 extends Thread
	{
		@Override
		public void run()
		{
			Looper.prepare();
			super.run();
			// 调用SDK接口获取彩铃试听地址
			Log.d("MMMMMM", "begin");
			CodeMessageObject cmo1 = RingbackManagerInterface
					.getRingbackMusicTestUrl(
							MusicOnlineSetRingToneActivity.this, mMusicId);
			Log.d("MMMMMM", "Hello");
			if (cmo1 != null)
			{
				// 判断返回结果
				if ("000000".equals(cmo1.getCode()))
				{
					// 成功
					Log.d("MMMMMM", "成功");
					String[] url1 = (String[]) cmo1.getObject();
					Message m = new Message();
					m.what = 6;
					bizDescription = url1[2];
					bizSalePrice = url1[1];
					mUIHandler.sendMessage(m);
					return;
				}
			}
			mUIHandler.obtainMessage(0).sendToTarget();
			Looper.loop();
		}
	}

	// 设置默认铃音的Thread
	// 还没有完成×××××××××××××××
	class T6 extends Thread
	{
		@Override
		public void run()
		{
			Looper.prepare();
			super.run();
			// 调用SDK接口设置默认铃音
			CodeMessageObject cmo4 = RingbackManagerInterface.setDefaultRing(
					MusicOnlineSetRingToneActivity.this, null);
			if (cmo4 != null)
			{
				// 判断返回结果
				if ("000000".equals(cmo4.getCode()))
				{
					// 成功
					mUIHandler.obtainMessage(10).sendToTarget();
				}
			}
			mUIHandler.obtainMessage(0).sendToTarget();
			Looper.loop();
		}
	}

	// 查询用户支持的振铃下载策略的Thread
	class T7 extends Thread
	{
		@Override
		public void run()
		{
			Looper.prepare();
			super.run();
			// 调用SDK接口查询用户支持的振铃下载策略
			CodeMessageObject cmo1 = VibrateRingManagerInterface
					.queryVibrateRingWay(MusicOnlineSetRingToneActivity.this,
							mMusicId);
			if (cmo1 != null)
			{
				// 判断返回结果
				if ("000000".equals(cmo1.getCode()))
				{
					// 成功
					@SuppressWarnings("unchecked")
					ArrayList<BizInfo> list1 = (ArrayList<BizInfo>) cmo1
							.getObject();
					if (list1 != null)
					{
						bizCode = list1.get(list1.size() - 1).getBizCode();
						bizType = list1.get(list1.size() - 1).getBizType();
						bizDescription = list1.get(list1.size() - 1)
								.getDescription();
						bizOriginalPrice = list1.get(list1.size() - 1)
								.getOriginalPrice();
						bizSalePrice = list1.get(list1.size() - 1)
								.getSalePrice();
						mUIHandler.obtainMessage(6).sendToTarget();
						return;
					}
				}
			}
			mUIHandler.obtainMessage(0).sendToTarget();
			Looper.loop();
		}
	}

	// 获取振铃下载地址的Thread
	class T8 extends Thread
	{
		@Override
		public void run()
		{
			Looper.prepare();
			super.run();
			// 调用SDK接口获取振铃下载地址
			CodeMessageObject cmo2 = VibrateRingManagerInterface
					.queryVibrateRingDownloadUrl(
							MusicOnlineSetRingToneActivity.this, mMusicId,
							bizCode, bizType);
			if (cmo2 != null)
			{
				// 判断返回结果
				if ("000000".equals(cmo2.getCode()))
				{
					// 成功
					String downloadUrl = (String) cmo2.getObject();
					Message m = new Message();
					m.what = 7;
					m.obj = downloadUrl;
					mUIHandler.sendMessage(m);
				}
			}
			mUIHandler.obtainMessage(0).sendToTarget();
			Looper.loop();
		}
	}
}