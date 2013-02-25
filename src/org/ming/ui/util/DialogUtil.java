package org.ming.ui.util;

import org.ming.R;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.DBController;
import org.ming.util.MyLogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogUtil
{

	public static DialogInterface.OnClickListener gotoLocalMusicViewListener;
	public static final MyLogger logger = MyLogger.getLogger("DialogUtil");
	private static Context mContext;

	public static DialogInterface.OnClickListener defaultDismissListener = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface paramAnonymousDialogInterface,
				int paramAnonymousInt)
		{
			paramAnonymousDialogInterface.dismiss();
		}
	};

	public static void setDialogUtilContext(Context paramContext)
	{
		logger.v("setDialogUtilContext() ---> Enter");
		mContext = paramContext;
		logger.v("setDialogUtilContext() ---> Exit");
	}

	public static Dialog show1BtnDialogWithTitleMsg(Context paramContext,
			CharSequence paramCharSequence1, CharSequence paramCharSequence2,
			View.OnClickListener paramOnClickListener)
	{
		logger.v("show1BtnDialogWithTitleMsg() ---> Enter");
		int i = ((Activity) paramContext).getTaskId();
		Dialog localDialog = null;
		View localView;
		TextView localTextView1;
		TextView localTextView2;
		Button localButton;
		if (i != -1)
		{
			localDialog = new Dialog(paramContext, R.style.CustomDialogTheme);
			localView = LayoutInflater.from(paramContext).inflate(
					R.layout.dialog_title_text_one_button, null);
			localTextView1 = (TextView) localView.findViewById(R.id.title);
			localTextView2 = (TextView) localView.findViewById(R.id.msg);
			localButton = (Button) localView.findViewById(R.id.button1);
			if (paramCharSequence1 != null)
			{
				localTextView1.setText(paramCharSequence1);
				localTextView1.setVisibility(View.VISIBLE);
			} else
			{
				localTextView1.setVisibility(View.GONE);
			}
			if (paramCharSequence2 != null)
			{
				localTextView2.setText(paramCharSequence2);
				localTextView2.setVisibility(View.VISIBLE);
			} else
			{
				localTextView2.setVisibility(View.GONE);
			}
			if (paramOnClickListener != null)
			{
				localButton.setOnClickListener(paramOnClickListener);
				localButton.setVisibility(View.VISIBLE);
			} else
			{
				localButton.setVisibility(View.GONE);
			}
			localDialog.setContentView(localView);
			localDialog.show();
			logger.v("show1BtnDialogWithTitleMsg() ---> Exit");
		}

		return localDialog;
	}

	public static Dialog show1BtnProgressDialog(Context paramContext,
			int paramInt1, int paramInt2,
			View.OnClickListener paramOnClickListener)
	{
		logger.v("show1BtnProgressDialog() ---> Enter");
		Dialog localDialog = new Dialog(paramContext, R.style.CustomDialogTheme);
		View localView = LayoutInflater.from(paramContext).inflate(
				R.layout.dialog_progress_text_one_button, null);
		Button localButton = (Button) localView.findViewById(R.id.button);
		TextView localTextView = (TextView) localView
				.findViewById(android.R.id.text1); // 1020014
		localButton.setText(paramInt2);
		localButton.setOnClickListener(paramOnClickListener);
		localTextView.setText(paramInt1);
		localDialog.setContentView(localView);
		localDialog.show();
		logger.v("show1BtnProgressDialog() ---> Exit");
		return localDialog;
	}

	public static Dialog show2BtnDialogWithCheckBoxIconTitleMsg(
			Context context, CharSequence charsequence,
			CharSequence charsequence1,
			android.view.View.OnClickListener onclicklistener,
			android.view.View.OnClickListener onclicklistener1)
	{
		logger.v("show2BtnDialogWithIconTitleMsg() ---> Enter");
		Dialog dialog = new Dialog(context, R.style.CustomDialogTheme);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_tile_text_two_button_checkbox, null);
		TextView textview = (TextView) view.findViewById(R.id.nw_title);
		TextView textview1 = (TextView) view.findViewById(R.id.msg);
		Button button = (Button) view.findViewById(R.id.button1);
		Button button1 = (Button) view.findViewById(R.id.button2);
		final CheckBox checkbox = (CheckBox) view.findViewById(R.id.not_warn);
		checkbox.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View view1)
			{
				DBController dbcontroller = MobileMusicApplication
						.getInstance().getController().getDBController();
				boolean flag;
				if (checkbox.isChecked())
					flag = false;
				else
					flag = true;
				dbcontroller.setCheckOlderVersion(flag);
			}

		});
		if (charsequence != null)
			textview.setText(charsequence);
		else
			textview.setVisibility(8);
		if (charsequence1 != null)
			textview1.setText(charsequence1);
		else
			textview.setVisibility(8);
		button.setOnClickListener(onclicklistener);
		button1.setOnClickListener(onclicklistener1);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
		logger.v("show2BtnDialogWithIconTitleMsg() ---> Exit");
		return dialog;
	}

	public static Dialog show2BtnDialogWithIconTitleMsg(Context paramContext,
			CharSequence paramCharSequence1, CharSequence paramCharSequence2,
			View.OnClickListener paramOnClickListener1,
			View.OnClickListener paramOnClickListener2)
	{
		logger.v("show2BtnDialogWithIconTitleMsg() ---> Enter");
		Dialog localDialog = new Dialog(paramContext, R.style.CustomDialogTheme);
		View localView = LayoutInflater.from(paramContext).inflate(
				R.layout.dialog_title_text_two_button, null);
		TextView localTextView1 = (TextView) localView
				.findViewById(R.id.nw_title);
		TextView localTextView2 = (TextView) localView.findViewById(R.id.msg);
		Button localButton1 = (Button) localView.findViewById(R.id.button1);
		Button localButton2 = (Button) localView.findViewById(R.id.button2);
		if (paramCharSequence1 != null)
		{
			localTextView1.setText(paramCharSequence1);
			localTextView1.setVisibility(View.VISIBLE);
		} else
		{
			localTextView1.setVisibility(View.GONE);
		}
		if (paramCharSequence2 != null)
		{
			localTextView2.setText(paramCharSequence2);
		}
		if (paramOnClickListener1 != null)
		{
			localButton1.setOnClickListener(paramOnClickListener1);
		}
		if (paramOnClickListener2 != null)
		{
			localButton2.setOnClickListener(paramOnClickListener2);
		}
		localDialog.setContentView(localView);
		localDialog.setCancelable(true);
		localDialog.show();
		logger.v("show2BtnDialogWithIconTitleMsg() ---> Exit");

		return localDialog;
	}

	public static Dialog show2BtnDialogWithIconTitleView(Context paramContext,
			CharSequence paramCharSequence1, CharSequence paramCharSequence2,
			View paramView, View.OnClickListener paramOnClickListener1,
			View.OnClickListener paramOnClickListener2)
	{
		logger.v("show2BtnDialogWithIconTitleView() ---> Enter");
		Dialog localDialog = new Dialog(paramContext, R.style.CustomDialogTheme);
		View localView = LayoutInflater.from(paramContext).inflate(
				R.layout.dialog_title_text_view_two_button, null);
		ImageView localImageView = (ImageView) localView
				.findViewById(R.id.line);
		TextView localTextView1 = (TextView) localView
				.findViewById(R.id.nw_title);
		TextView localTextView2 = (TextView) localView.findViewById(R.id.msg);
		LinearLayout localLinearLayout = (LinearLayout) localView
				.findViewById(R.id.add_view);
		Button localButton1 = (Button) localView.findViewById(R.id.button1);
		Button localButton2 = (Button) localView.findViewById(R.id.button2);
		if (paramCharSequence1 != null)
		{
			localTextView1.setText(paramCharSequence1);
			localTextView1.setVisibility(View.VISIBLE);
		} else
		{
			localTextView1.setVisibility(View.GONE);
		}
		if (paramCharSequence2 != null)
		{
			localTextView2.setVisibility(View.VISIBLE);
			localTextView2.setText(paramCharSequence2);
		} else
		{
			localTextView2.setVisibility(View.GONE);
		}
		if (paramOnClickListener1 != null)
		{
			localButton1.setOnClickListener(paramOnClickListener1);
		}
		if (paramOnClickListener2 != null)
		{
			localButton2.setOnClickListener(paramOnClickListener2);
		}
		if (paramView != null)
		{
			localLinearLayout.addView(paramView);
		}
		localDialog.setContentView(localView);
		localDialog.show();
		logger.v("show2BtnDialogWithIconTitleView() ---> Exit");
		return localDialog;
	}

	public static Object[] show2BtnDialogWithMoreMicrobolView(
			Context paramContext, View paramView,
			View.OnClickListener paramOnClickListener)
	{
		Dialog localDialog = new Dialog(paramContext, 2131296261);
		View localView = LayoutInflater.from(paramContext).inflate(2130903097,
				null);
		Button localButton = (Button) localView.findViewById(2131034343);
		localDialog.setContentView(localView);
		Object[] arrayOfObject = { localDialog, localView };
		localButton.setOnClickListener(paramOnClickListener);
		localDialog.show();
		return arrayOfObject;
	}

	public static Dialog show2SeftDefineBtnDialogWithIconTitleMsg(
			Context paramContext, int paramInt,
			CharSequence paramCharSequence1, CharSequence paramCharSequence2,
			CharSequence paramCharSequence3, CharSequence paramCharSequence4,
			DialogInterface.OnClickListener paramOnClickListener1,
			DialogInterface.OnClickListener paramOnClickListener2)
	{
		logger.v("show2SeftDefineBtnDialogWithIconTitleMsg() ---> Enter");
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext)
				.setPositiveButton(paramCharSequence3, paramOnClickListener1)
				.setNegativeButton(paramCharSequence4, paramOnClickListener2);
		if (paramInt != 0)
			localBuilder.setIcon(paramInt);
		if (paramCharSequence1 != null)
			localBuilder.setTitle(paramCharSequence1);
		if (paramCharSequence2 != null)
			localBuilder.setMessage(paramCharSequence2);
		AlertDialog localAlertDialog = localBuilder.show();
		localAlertDialog.setCancelable(true);
		logger.v("show2SeftDefineBtnDialogWithIconTitleMsg() ---> Exit");
		return localAlertDialog;
	}

	public static Dialog showIndeterminateProgressDialog(Context paramContext,
			int paramInt)
	{
		logger.v("showIndeterminateProgressDialog() ---> Enter");
		Dialog localDialog = new Dialog(paramContext, R.style.CustomDialogTheme);
		View localView = ((LayoutInflater) paramContext
				.getSystemService("layout_inflater")).inflate(
				R.layout.dialog_progress_circular, null);
		TextView localTextView = (TextView) localView
				.findViewById(android.R.id.text1); // 1020014
		if (paramInt != 0)
			localTextView.setText(paramInt);
		localDialog.setContentView(localView);
		localDialog.setCancelable(false);
		localDialog.show();
		logger.v("showIndeterminateProgressDialog() ---> Exit");
		return localDialog;
	}
}