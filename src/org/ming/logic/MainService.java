package org.ming.logic;

import java.util.ArrayList;

import org.ming.mp3_online_01.R;
import org.ming.util.NetUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class MainService extends Service implements Runnable
{
	public static boolean isrun = false;
	public static MainService mainService; // 自身全局静态变量
	public NetUtil netReceiver = new NetUtil();
	public static ArrayList<IMusicActivity> allActivity = new ArrayList<IMusicActivity>();

	// 保存所有任务对象
	private static ArrayList<Task> allTask = new ArrayList<Task>();

	public MainService()
	{
		mainService = this;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		mainService = this;
		Thread t = new Thread(this);
		t.start();

		// 添加网络状态变化的广播接收器
		this.registerReceiver(netReceiver, new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE"));
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static void addNewTask(Task ts)
	{
		allTask.add(ts);
	}

	public void doTask(Task task)
	{
		Message message = handler.obtainMessage();

		try
		{
			switch (task.getTaskID())
			{
			}
		} catch (Exception e)
		{

		}

	}

	public Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{

		}
	};

	// 提示用户网络异常
	public static void alertNetError(final Activity context)
	{
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		// 设定标题
		ab.setTitle(context.getResources().getString(R.string.net_err_title));
		// 设定内容
		ab.setMessage(context.getResources().getString(R.string.net_err_info));
		// 设定推出按钮
		ab.setNegativeButton(context.getResources()
				.getString(R.string.exit_app), new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				context.startActivityForResult(new Intent(
						android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
			}
		});

		ab.create().show();
	}
	
	// 退出应用程序
	public static void exitApp(Activity context)
	{
		for (int i = 0; i < allActivity.size(); i++)
		{
			allActivity.clear();
			// 退出Service
			context.stopService(new Intent("org.ming.logic.MainService"));
			// 关闭子线程
			MainService.isrun = false;
			// 关闭广播接口
			MainService.mainService
					.unregisterReceiver(MainService.mainService.netReceiver);
		}
	}
}
