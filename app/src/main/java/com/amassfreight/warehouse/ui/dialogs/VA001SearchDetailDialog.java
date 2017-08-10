package com.amassfreight.warehouse.ui.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.amassfreight.warehouse.R;
import com.amassfreight.utils.Utils;

import com.amassfreight.domain.SelectDict;
import com.amassfreight.utils.SessionHelper;

public class VA001SearchDetailDialog implements OnDialogOkListener {

	private int requestCode;
	private OnDialogOkListener listener;
	private VA001SearchMoreData moreData;
	
	private View view;
	private Spinner spinner_serviceType;
	private Spinner spinner_flgService;
	private EditText editDateFrom;
	private EditText editDateTo;
	private EditText editOrderPublic;
	private VA001SearchDetailDialog _thisDialog;

	public VA001SearchDetailDialog(int requestCode,
			VA001SearchMoreData moreData, OnDialogOkListener listener) {
		this.requestCode = requestCode;
		this.listener = listener;
		this.moreData = moreData.clone();
		_thisDialog = this;
	}

	@SuppressLint("SimpleDateFormat")
	public Dialog createDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(context,android.R.style.Theme_Holo_Light));

		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.activity_va001_search_detail, null);
		builder.setView(view);
		
		editOrderPublic = (EditText)view.findViewById(R.id.editCdPubOrder);
		editOrderPublic.setSelectAllOnFocus(true);  // 全选
		editOrderPublic.setText(moreData.getCdOrderPublic());
		
		editOrderPublic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View view, boolean bFocus) {
				if(bFocus == false){
					editOrderPublic.setText(Utils.completeOrderId(editOrderPublic.getText().toString()));
				}
			}
		});
		
		spinner_serviceType = (Spinner) view.findViewById(R.id.spinner_serviceType);
		spinner_flgService = (Spinner) view.findViewById(R.id.spinner_complete);
		editDateFrom = (EditText) view.findViewById(R.id.editFromDate);
		editDateTo = (EditText) view.findViewById(R.id.editToDate);

		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat timeStart = new SimpleDateFormat("yyyy-MM-dd 00:00");
		SimpleDateFormat timeEnd = new SimpleDateFormat("yyyy-MM-dd 23:59");
		Date curDate = new Date(System.currentTimeMillis());
		if (moreData.getDtFrom() != null){
			editDateFrom.setText(time.format(moreData.getDtFrom()));
		}else{
			editDateFrom.setText(timeStart.format(curDate));
		}
		if (moreData.getDtEnd() != null){
			editDateTo.setText(time.format(moreData.getDtEnd()));
		}else{
			editDateTo.setText(timeEnd.format(curDate));
		}

		editDateFrom.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Date d = null;
					SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					try {
						d = time.parse(editDateFrom.getText().toString());
					} catch (ParseException e) {
						d = Utils.getCurrentDate(true);
					}

						UtilsDialog.DatePickDialog(view.getContext(), 1,
								_thisDialog, d,view.getContext()
								.getString(R.string.VA001_lastTime_from));
				}
				return true;
			}
		});
		
		editDateFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean bFocus) {
				if(bFocus){
					Date d = null;
					SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					try {
						d = time.parse(editDateFrom.getText().toString());
					} catch (ParseException e) {
						d = Utils.getCurrentDate(true);
					}

						UtilsDialog.DatePickDialog(view.getContext(), 1,
								_thisDialog, d,view.getContext()
								.getString(R.string.VA001_lastTime_from));

				}
			}
		});

		editDateTo.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Date d = null;
					SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					try {
						d = time.parse(editDateTo.getText().toString());
					} catch (ParseException e) {
						d = Utils.getCurrentDate(false);
					}

						UtilsDialog.DatePickDialog(view.getContext(), 2,
								_thisDialog, d,view.getContext()
								.getString(R.string.VA001_lastTime_to));

				}
				return true;
			}
		});

		editDateTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean bFocus) {
				if(bFocus){
					Date d = null;
					SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					try {
						d = time.parse(editDateTo.getText().toString());
					} catch (ParseException e) {
						d = Utils.getCurrentDate(false);
					}

						UtilsDialog.DatePickDialog(view.getContext(), 2,
								_thisDialog, d,view.getContext()
								.getString(R.string.VA001_lastTime_to));
				}
			}
		});
		List<SelectDict> list = new ArrayList<SelectDict>();
		SelectDict item = new SelectDict();
		item.setId("ALL");
		item.setName(context.getString(R.string.VA001_Dialog_All));
		list.add(item);
		list.addAll(SessionHelper.getInstance().getServiceTypeList());
		ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
				context, android.R.layout.simple_spinner_item, list);
		 selectList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_serviceType.setAdapter(selectList);
		if(moreData.getServiceType() != null){
			SelectDict tmp = new SelectDict();
			tmp.setId(moreData.getServiceType());
			spinner_serviceType.setSelection(list.indexOf(tmp));
		}
		list = new ArrayList<SelectDict>();
		list.clear();
		list.add(item);
		list.addAll(SessionHelper.getInstance().getServiceFlgList());
		selectList = new ArrayAdapter<SelectDict>(context,
				android.R.layout.simple_spinner_item, list);
		selectList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_flgService.setAdapter(selectList);
		if(moreData.getFlgService() != null){
			SelectDict tmp = new SelectDict();
			tmp.setId(moreData.getFlgService());
			spinner_flgService.setSelection(list.indexOf(tmp));
		}
		builder.setPositiveButton(context.getString(R.string.VA001_Dialog_Find),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dailog, int id) {
				editOrderPublic.clearFocus();
				setDialogResult();
				dailog.dismiss();
			}

		});
		builder.setNegativeButton(context.getString(R.string.VA001_Dialog_Cancel)
				, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				
				if (view.findFocus() != null && imm.isActive()) {
					imm.hideSoftInputFromWindow(
							view.findFocus().getWindowToken(), 0);
				}
				return true;
			}
		});
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);  // 点击空白处，不关闭对话框
		return dialog;
	}

	@SuppressLint("SimpleDateFormat")
	private void setDialogResult() {

		if (listener != null) {
			moreData.setCdOrderPublic(Utils.completeOrderId(editOrderPublic.getText().toString()));
			moreData.setServiceType(((SelectDict)spinner_serviceType.getSelectedItem()).getId());
			moreData.setFlgService(((SelectDict)spinner_flgService.getSelectedItem()).getId());
			SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try{
				if (!TextUtils.isEmpty(editDateFrom.getText())) {
					moreData.setDtFrom(time.parse(editDateFrom.getText().toString()));
				} else {
					moreData.setDtFrom(null);
				}
				if (!TextUtils.isEmpty(editDateTo.getText())) {
					moreData.setDtEnd(time.parse(editDateTo.getText().toString()));
				} else {
					moreData.setDtEnd(null);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			Intent intent = new Intent();
			intent.putExtra(OnDialogOkListener.OK, moreData);
			listener.onDialogResult(requestCode, Activity.RESULT_OK, intent);
		}
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;

		Date dateValue = (Date) data
				.getSerializableExtra(OnDialogOkListener.OK);
		switch (requestCode) {
		case 1: // From date
			if (dateValue == null) {
				editDateFrom.setText(null);
			} else {
				if (moreData.getDtEnd() != null) {
					if (dateValue.after(moreData.getDtEnd())) {
						Utils.showAlertDialog(editDateFrom.getContext(),
						    editDateFrom.getContext().getString(R.string.VA001_Dialog_MSG_001));  // 开始时间不能比结束时间晚
						break;
					}
				}
				SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				editDateFrom.setText(time.format(dateValue));
			}
			break;

		case 2: // To Date

			if (dateValue == null) {
				editDateTo.setText(null);
			} else {
				if (moreData.getDtFrom() != null) {
					if (dateValue.before(moreData.getDtFrom())) {
						Utils.showAlertDialog(editDateFrom.getContext(),
						    editDateFrom.getContext().getString(R.string.VA001_Dialog_MSG_001));  // 开始时间不能比结束时间晚
						break;
					}
				}
				SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				editDateTo.setText(time.format(dateValue));
			}
			break;
		default:
			break;
		}
	}
}