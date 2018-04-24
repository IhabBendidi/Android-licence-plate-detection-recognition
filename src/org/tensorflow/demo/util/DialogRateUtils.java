package org.tensorflow.demo.util;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.tensorflow.demo.DialogRateActivity;
import org.tensorflow.demo.MenuActivity;
import org.tensorflow.demo.R;

public class DialogRateUtils {
    
	private DialogRateActivity mDialogRateActivity;
	private Dialog mDialog;

	private TextView mDialogText;
	private TextView mDialogOKButton;
	private TextView mDialogCancelButton;

	public DialogRateUtils(
			DialogRateActivity mDialogRateActivity) {
		this.mDialogRateActivity = mDialogRateActivity;
	}

	public void showDialog() {
		if (mDialog == null) {
			mDialog = new Dialog(mDialogRateActivity,
					R.style.CustomDialogTheme);
		}
		mDialog.setContentView(R.layout.dialog_rate);
		mDialog.setCancelable(true);
		mDialog.show();

		mDialogText = (TextView) mDialog
				.findViewById(R.id.dialog_universal_warning_text);
		mDialogOKButton = (TextView) mDialog
				.findViewById(R.id.dialog_universal_warning_ok);
		mDialogCancelButton = (TextView) mDialog
				.findViewById(R.id.dialog_universal_warning_cancel);

		initDialogButtons();
	}

	private void initDialogButtons() {

		mDialogOKButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				/*Intent intent = new Intent(DialogRateActivity.this,MenuActivity.class);
				startActivity(intent);
				finish();*/
				android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
				mDialog.dismiss();
			}
		});

		mDialogCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mDialog.dismiss();
			}
		});
	}

	public void dismissDialog() {
		mDialog.dismiss();
	}

}
