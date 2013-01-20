package org.ming.ui;

import org.ming.logic.MainService;
import org.ming.logic.Task;
import org.ming.mp3_online_01.R;
import org.ming.util.NetUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

public class LogoActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 去掉Activity标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉任务条
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.logo);

		AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
		aa.setDuration(3000);
		this.findViewById(R.id.ImageView01).startAnimation(aa);
		aa.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
				// 检查网络
				if (NetUtil.checkNet(LogoActivity.this))
				{
					if (!MainService.isrun)
					{
						MainService.isrun = true;
						Intent intent = new Intent(LogoActivity.this, MainService.class);
						LogoActivity.this.startService(intent);
					}

					//Task loginTask = new Task(Task.TASK_USER_LOGIN, null);
					//MainService.addNewTask(loginTask);

					Log.d("netcheck", "...........success");
				} else
				{
					Log.d("netcheck", "...........error");
					Toast.makeText(LogoActivity.this, "没有网络连接", Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				Intent intent = new Intent(LogoActivity.this,
						HomeActivity.class);
				LogoActivity.this.startActivity(intent);
				finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub

			}

		});

	}
}
