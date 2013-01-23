package org.ming.ui.util;

import org.ming.R;
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
			if (paramCharSequence1 == null)
				localTextView1.setText(paramCharSequence1);
			if (paramCharSequence2 == null)
				localTextView2.setText(paramCharSequence2);
			if (paramOnClickListener == null)
				localButton.setOnClickListener(paramOnClickListener);

			localDialog.setContentView(localView);
			localDialog.show();
			logger.v("show1BtnDialogWithTitleMsg() ---> Exit");
			localTextView1.setVisibility(8);
			localTextView2.setVisibility(8);
			localButton.setVisibility(8);
		}

		return localDialog;
	}

	public static Dialog show1BtnProgressDialog(Context paramContext,
			int paramInt1, int paramInt2,
			View.OnClickListener paramOnClickListener)
	{
		logger.v("show1BtnProgressDialog() ---> Enter");
		Dialog localDialog = new Dialog(paramContext, 2131296261);
		View localView = LayoutInflater.from(paramContext).inflate(2130903101,
				null);
		Button localButton = (Button) localView.findViewById(2131034349);
		TextView localTextView = (TextView) localView.findViewById(16908308);
		localButton.setText(paramInt2);
		localButton.setOnClickListener(paramOnClickListener);
		localTextView.setText(paramInt1);
		localDialog.setContentView(localView);
		localDialog.show();
		logger.v("show1BtnProgressDialog() ---> Exit");
		return localDialog;
	}

	public static Dialog show2BtnDialogWithCheckBoxIconTitleMsg(
			Context paramContext, CharSequence paramCharSequence1,
			CharSequence paramCharSequence2,
			View.OnClickListener paramOnClickListener1,
			View.OnClickListener paramOnClickListener2)
	{
		logger.v("show2BtnDialogWithIconTitleMsg() ---> Enter");
		Dialog localDialog = new Dialog(paramContext, 2131296261);
		View localView = LayoutInflater.from(paramContext).inflate(2130903103,
				null);
		TextView localTextView1 = (TextView) localView.findViewById(2131034354);
		TextView localTextView2 = (TextView) localView.findViewById(2131034194);
		Button localButton1 = (Button) localView.findViewById(2131034340);
		Button localButton2 = (Button) localView.findViewById(2131034338);
		CheckBox localCheckBox = (CheckBox) localView.findViewById(2131034355);
		localCheckBox.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{

			}
		});
		if (paramCharSequence1 != null)
		{
			localTextView1.setText(paramCharSequence1);
			if (paramCharSequence2 == null)
				localTextView2.setText(paramCharSequence2);
		}
		localButton1.setOnClickListener(paramOnClickListener1);
		localButton2.setOnClickListener(paramOnClickListener2);
		localDialog.setContentView(localView);
		localDialog.setCancelable(true);
		localDialog.show();
		logger.v("show2BtnDialogWithIconTitleMsg() ---> Exit");
		localTextView1.setVisibility(8);
		return localDialog;
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
		}
		if (paramCharSequence2 != null)
		{
			localTextView2.setText(paramCharSequence2);
		}
		localButton1.setOnClickListener(paramOnClickListener1);
		localButton2.setOnClickListener(paramOnClickListener2);
		localDialog.setContentView(localView);
		localDialog.setCancelable(true);
		localDialog.show();
		logger.v("show2BtnDialogWithIconTitleMsg() ---> Exit");
		localTextView1.setVisibility(8);
		return localDialog;
	}

	public static Dialog show2BtnDialogWithIconTitleView(Context paramContext,
			CharSequence paramCharSequence1, CharSequence paramCharSequence2,
			View paramView, View.OnClickListener paramOnClickListener1,
			View.OnClickListener paramOnClickListener2)
	{
		logger.v("show2BtnDialogWithIconTitleView() ---> Enter");
		Dialog localDialog = new Dialog(paramContext, 2131296261);
		View localView = LayoutInflater.from(paramContext).inflate(2130903108,
				null);
		ImageView localImageView = (ImageView) localView
				.findViewById(2131034357);
		TextView localTextView1 = (TextView) localView.findViewById(2131034354);
		TextView localTextView2 = (TextView) localView.findViewById(2131034194);
		LinearLayout localLinearLayout = (LinearLayout) localView
				.findViewById(2131034358);
		Button localButton1 = (Button) localView.findViewById(2131034340);
		Button localButton2 = (Button) localView.findViewById(2131034338);
		if (paramCharSequence1 != null)
		{
			localTextView1.setText(paramCharSequence1);
			if (paramCharSequence2 == null)
				localTextView2.setText(paramCharSequence2);
			if (paramView == null)
				localLinearLayout.addView(paramView);
			if (paramOnClickListener1 == null)
				localButton1.setOnClickListener(paramOnClickListener1);
			if (paramOnClickListener2 == null)
				localButton2.setOnClickListener(paramOnClickListener2);
		}
		localDialog.setContentView(localView);
		localDialog.show();
		logger.v("show2BtnDialogWithIconTitleView() ---> Exit");
		localImageView.setVisibility(8);
		localTextView1.setVisibility(8);
		localTextView2.setVisibility(8);
		localLinearLayout.setVisibility(8);
		localButton1.setVisibility(8);
		localButton2.setVisibility(8);
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
		Dialog localDialog = new Dialog(paramContext, 2131296261);
		View localView = ((LayoutInflater) paramContext
				.getSystemService("layout_inflater")).inflate(2130903099, null);
		TextView localTextView = (TextView) localView.findViewById(16908308);
		if (paramInt != 0)
			localTextView.setText(paramInt);
		localDialog.setContentView(localView);
		localDialog.setCancelable(false);
		localDialog.show();
		logger.v("showIndeterminateProgressDialog() ---> Exit");
		return localDialog;
	}
}