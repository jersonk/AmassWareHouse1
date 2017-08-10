package com.amassfreight.warehouse.ui.dialogs;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.LogonFormData;
import com.amassfreight.domain.LogonUser;
import com.amassfreight.domain.SelectDict;
import com.amassfreight.utils.SessionHelper;
import com.amassfreight.warehouse.R;
import com.google.gson.reflect.TypeToken;

@SuppressLint("SimpleDateFormat")
public class SelectPrintDialog implements OnDialogOkListener {

	private int requestCode;
	private OnDialogOkListener listener;

	private View view;
	private Spinner depotList;

	private SelectPrintDialog _thisDialog;

	public SelectPrintDialog(int requestCode, OnDialogOkListener listener) {
		this.requestCode = requestCode;
		this.listener = listener;
		_thisDialog = this;
	}

	public Dialog createDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(context,
						android.R.style.Theme_Holo_Light));

		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.activity_select_print, null);
		builder.setView(view);
		depotList = (Spinner) view.findViewById(R.id.depotno_list);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dailog, int id) {
				setDialogResult();
				dailog.dismiss();
			}

		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		view.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) view.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				if (view.findFocus() != null && imm.isActive()) {
					imm.hideSoftInputFromWindow(view.findFocus()
							.getWindowToken(), 0);
				}
				return true;
			}
		});

		InitDepotList(context);
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false); // 点击空白处，不关闭对话框
		return dialog;
	}

	private void InitDepotList(final Context context) {

		Type a = new TypeToken<List<SelectDict>>() {
		}.getType();
		// List<SelectDict> b = new ArrayList<SelectDict>();

		NetworkHelper.getInstance().postData(depotList.getContext(),
				"GetDepotNoList", null, a,
				new AmassHttpResponseHandler<List<SelectDict>>() {
					@Override
					protected void OnSuccess(List<SelectDict> response) {
						// TODO Auto-generated method stub
						super.OnSuccess(response);
						List<SelectDict> listData = response;
						ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
								context, android.R.layout.simple_spinner_item,
								listData);
						selectList
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						depotList.setAdapter(selectList);
						if (SessionHelper.getInstance().getAuthenUser() != null) {
							SelectDict curPos = new SelectDict();
							curPos.setId(SessionHelper.getInstance()
									.getAuthenUser().getDepotNo());

							depotList.setSelection(selectList
									.getPosition(curPos));
						}

					}

				}, true);
	}

	private void setDialogResult() {

		if (listener != null) {
			LogonFormData u = new LogonFormData();
			u = SessionHelper.getInstance().getAuthenUser();
			SelectDict depotNoItem = (SelectDict) depotList.getSelectedItem();
			u.setDepotNo(depotNoItem.getId());
			SessionHelper.getInstance().setAuthenUser(u);
			saveLoginInfo(u);

			Map<String, Object> printMap = new HashMap<String, Object>();
			printMap.put("depotNo", depotNoItem.getId());// 打印机选择

			NetworkHelper.getInstance().postJsonData(depotList.getContext(),
					"DN004_UpdateDepotNo", printMap, LogonUser.class,
					new AmassHttpResponseHandler<LogonUser>() {

						@Override
						protected void OnSuccess(LogonUser response) {
							// TODO Auto-generated method stub
							super.OnSuccess(response);

							Intent intent = new Intent();
							listener.onDialogResult(requestCode,
									Activity.RESULT_OK, intent);
						}

						@Override
						protected void onFailure(int statusCode,
								Header[] headers, String responseBody,
								Throwable e) {
							// TODO Auto-generated method stub
							super.onFailure(statusCode, headers, responseBody,
									e);

						}

					}, true);

		}
	}

	private void saveLoginInfo(LogonFormData u) {
		SharedPreferences sharedPref = depotList.getContext()
				.getSharedPreferences("com.amassfreight.loginUser",
						Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("depotNo", u.getDepotNo());
		editor.commit();
	}

	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}

}