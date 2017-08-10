package com.amassfreight.warehouse.ui.dialogs;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.amassfreight.warehouse.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class UtilsDialog {

	public static void DatePickDialog(Context context, final int requestCode,
			final OnDialogOkListener listener, Date initDate,String stritle) {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light));
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.date_time_dialog, null);

		final DatePicker datePicker = (DatePicker) view
				.findViewById(R.id.date_picker);
		final TimePicker timePicker = (android.widget.TimePicker) view
				.findViewById(R.id.time_picker);

		
		timePicker.setIs24HourView(true);
		if (initDate == null) {
			initDate = new Date();
		}
			Calendar cal = Calendar.getInstance(Locale.getDefault());
			cal.setTime(initDate);
			datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH), null);

			timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
		// }else{
		// Calendar cal = Calendar.getInstance(Locale.getDefault());
		// cal.setTime(new Date());
		// datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
		// cal.get(Calendar.DAY_OF_MONTH), null);
		//
		// timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		// timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
		// }
		builder.setTitle(stritle);
		
		builder.setView(view);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (listener != null) {
					datePicker.clearFocus();
					timePicker.clearFocus();
					Calendar cal = Calendar.getInstance(Locale.getDefault());
					cal.set(datePicker.getYear(), datePicker.getMonth(),
							datePicker.getDayOfMonth(),
							timePicker.getCurrentHour(),
							timePicker.getCurrentMinute());
					Date dateValue = cal.getTime();
					Intent intent = new Intent();
					intent.putExtra(OnDialogOkListener.OK, dateValue);
					listener.onDialogResult(requestCode, Activity.RESULT_OK,
							intent);
				}
				dialog.dismiss();
			}

		});
		builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}

		});
		builder.setNegativeButton("清除", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) {
					Intent intent = new Intent();
					listener.onDialogResult(requestCode, Activity.RESULT_OK,
							intent);
				}
				dialog.dismiss();
			}
		});
		Dialog dialog = builder.create();
		dialog.show();
	}
}
