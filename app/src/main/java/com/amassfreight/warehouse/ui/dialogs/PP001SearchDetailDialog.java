package com.amassfreight.warehouse.ui.dialogs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.PP001SearchData;
import com.amassfreight.domain.PP002ResponseData;
import com.amassfreight.domain.SelectDict;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;

@SuppressLint("SimpleDateFormat")
public class PP001SearchDetailDialog implements OnDialogOkListener {

	private int requestCode;
	private OnDialogOkListener listener;
	private PP001SearchData moreData;

	private View view;
	private Spinner spinner_Lime;
	private Spinner spinner_stsPacking;
	private Spinner spinner_contaType;
	private Spinner spinner_sort;
	private EditText editNoBox;
	private EditText editPackingName;
	private EditText editFromDate;
	private EditText editToDate;
	private EditText editETDFromDate;
	private EditText editETDToDate;

	private PP001SearchDetailDialog _thisDialog;
	SimpleDateFormat timeStart = new SimpleDateFormat("yyyy-MM-dd 00:00");
	SimpleDateFormat timeEnd = new SimpleDateFormat("yyyy-MM-dd 23:59");
	SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd");
	Date curDate = new Date(System.currentTimeMillis());

	public PP001SearchDetailDialog(int requestCode,
			PP001SearchData pp001SearchData, OnDialogOkListener listener) {
		this.requestCode = requestCode;
		this.listener = listener;
		this.moreData = pp001SearchData;

		_thisDialog = this;
	}

	public Dialog createDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(context,
						android.R.style.Theme_Holo_Light));

		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.activity_pp001_search_detail, null);
		builder.setView(view);

		spinner_Lime = (Spinner) view.findViewById(R.id.spinner_Lime); // 航线
		spinner_stsPacking = (Spinner) view
				.findViewById(R.id.spinner_stsPacking);// 状态
		spinner_contaType = (Spinner) view.findViewById(R.id.spinner_contaType);// 装箱类型
		editNoBox = (EditText) view.findViewById(R.id.editNoBox);// 集箱号
		editPackingName = (EditText) view.findViewById(R.id.editPackingName);// 装箱员
		editFromDate = (EditText) view.findViewById(R.id.editFromDate);// 最晚时间开始
		editToDate = (EditText) view.findViewById(R.id.editToDate);// 最晚时间结束
		spinner_sort = (Spinner) view.findViewById(R.id.spinner_sort);// 排序

		editETDFromDate = (EditText) view.findViewById(R.id.editETDFromDate);// ETD
		editETDToDate = (EditText) view.findViewById(R.id.editETDToDate);// ETD

		if (moreData.getDeadLineStart() == null
				&& moreData.getDeadLineEnd() == null) {
			editFromDate.setText(timeStart.format(curDate));
			editToDate.setText(timeEnd.format(curDate));
		} else {
			if (moreData.getDeadLineStart() != null) {
				editFromDate.setText(time.format(moreData.getDeadLineStart()));
			}
			if (moreData.getDeadLineEnd() != null) {
				editToDate.setText(time.format(moreData.getDeadLineEnd()));
			}

		}

		editNoBox.setText(moreData.getNoBox());// 集箱号
		editPackingName.setText(moreData.getPackingName());// 装箱员
		if (moreData.getEtdStart() != null) {
			editETDFromDate.setText(dateTime.format(moreData.getEtdStart()));// ETD
		}
		if (moreData.getEtdEnd() != null) {
			editETDToDate.setText(dateTime.format(moreData.getEtdEnd()));// ETD
		}

		editFromDate.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Date d = null;
					try {
						d = time.parse(editFromDate.getText().toString());

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						d = Utils.getCurrentDate(true);
					}
					UtilsDialog.DatePickDialog(
							view.getContext(),
							1,
							_thisDialog,
							d,
							view.getContext().getString(
									R.string.PP001_DEADLINE_START));

				}
				return true;
			}
		});

		editFromDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean bFocus) {
				if (bFocus) {

					Date d = null;
					try {
						d = time.parse(editFromDate.getText().toString());

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						d = Utils.getCurrentDate(true);
					}
					UtilsDialog.DatePickDialog(
							view.getContext(),
							1,
							_thisDialog,
							d,
							view.getContext().getString(
									R.string.PP001_DEADLINE_START));
				}
			}
		});

		editToDate.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Date d = null;
					try {
						d = time.parse(editToDate.getText().toString());

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						d = Utils.getCurrentDate(true);
					}
					UtilsDialog.DatePickDialog(
							view.getContext(),
							2,
							_thisDialog,
							d,
							view.getContext().getString(
									R.string.PP001_DEADLINE_END));
				}
				return true;
			}
		});

		editToDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean bFocus) {
				if (bFocus) {
					Date d = null;
					try {
						d = time.parse(editToDate.getText().toString());

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						d = Utils.getCurrentDate(true);
					}
					UtilsDialog.DatePickDialog(
							view.getContext(),
							2,
							_thisDialog,
							d,
							view.getContext().getString(
									R.string.PP001_DEADLINE_END));
				}
			}
		});

		editETDFromDate.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Date d = null;
					try {
						d = dateTime
								.parse(editETDFromDate.getText().toString());

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						d = Utils.getCurrentDate(true);
					}
					UtilsDateDialog.DatePickDialog(
							view.getContext(),
							3,
							_thisDialog,
							d,
							view.getContext().getString(
									R.string.PP001_ETD_START));
				}
				return true;
			}
		});

		editETDFromDate
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View arg0, boolean bFocus) {
						if (bFocus) {
							Date d = null;
							try {
								d = dateTime.parse(editETDFromDate.getText()
										.toString());

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								d = Utils.getCurrentDate(true);
							}
							UtilsDateDialog.DatePickDialog(
									view.getContext(),
									3,
									_thisDialog,
									d,
									view.getContext().getString(
											R.string.PP001_ETD_START));
						}
					}
				});

		editETDToDate.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Date d = null;
					try {
						d = dateTime.parse(editETDToDate.getText().toString());

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						d = Utils.getCurrentDate(true);
					}
					UtilsDateDialog.DatePickDialog(view.getContext(), 4,
							_thisDialog, d,
							view.getContext().getString(R.string.PP001_ETD_END));
				}
				return true;
			}
		});

		editETDToDate
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View arg0, boolean bFocus) {
						if (bFocus) {
							Date d = null;
							try {
								d = dateTime.parse(editETDToDate.getText()
										.toString());

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								d = Utils.getCurrentDate(true);
							}
							UtilsDateDialog.DatePickDialog(view.getContext(),
									4, _thisDialog, d, view.getContext()
											.getString(R.string.PP001_ETD_END));
						}
					}
				});

		builder.setPositiveButton("查询", new DialogInterface.OnClickListener() {

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

		InitContaType(context);
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false); // 点击空白处，不关闭对话框
		return dialog;
	}

	/* 装箱状态和航线 */
	private void InitStsPackingList(final Context context) {

		Map<String, Object> pp001SearchMap = new HashMap<String, Object>();
		pp001SearchMap.put("cdType", "STS_PACKING");// 装箱状态
		NetworkHelper.getInstance().postJsonData(
				spinner_stsPacking.getContext(), "Sys_GetMLineList",
				pp001SearchMap, PP002ResponseData.class,
				new AmassHttpResponseHandler<PP002ResponseData>() {
					@Override
					protected void OnSuccess(PP002ResponseData response) {

						super.OnSuccess(response);
						PP002ResponseData pp002Data = response;
						// 装箱状态
						List<SelectDict> listData = pp002Data.getStsPacking();
						List<SelectDict> list = new ArrayList<SelectDict>();
						SelectDict item = new SelectDict();
						item.setId("");
						item.setName("请选择");
						list.add(item);
						list.addAll(listData);
						ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
								context, android.R.layout.simple_spinner_item,
								list);
						selectList
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinner_stsPacking.setAdapter(selectList);
						if (moreData.getStsPacking() != null) {
							SelectDict tmp = new SelectDict();
							tmp.setId(moreData.getStsPacking());
							spinner_stsPacking.setSelection(list.indexOf(tmp));
						}
						// 航线
						List<SelectDict> listLineData = pp002Data.getmLine();
						List<SelectDict> listLine = new ArrayList<SelectDict>();
						SelectDict itemLine = new SelectDict();
						itemLine.setId("");
						itemLine.setName("请选择");
						listLine.add(item);
						listLine.addAll(listLineData);
						ArrayAdapter<SelectDict> selectListLine = new ArrayAdapter<SelectDict>(
								context, android.R.layout.simple_spinner_item,
								listLine);
						selectListLine
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinner_Lime.setAdapter(selectListLine);
						if (moreData.getCdBooking() != null) {
							SelectDict tmp = new SelectDict();
							tmp.setId(moreData.getCdBooking());
							spinner_Lime.setSelection(listLine.indexOf(tmp));
						}

					}

				}, true);
	}

	/* 装箱类型 */
	private void InitContaType(final Context context) {
		List<SelectDict> list = new ArrayList<SelectDict>();
		SelectDict item = new SelectDict();
		item.setId("");
		item.setName("请选择");
		list.add(item);

		SelectDict item1 = new SelectDict();
		item1.setId("1");
		item1.setName(context.getString(R.string.CONTA_TYPE_X));
		list.add(item1);

		SelectDict item2 = new SelectDict();
		item2.setId("2");
		item2.setName(context.getString(R.string.CONTA_TYPE_F));
		list.add(item2);

		SelectDict item6 = new SelectDict();
		item6.setId("6");
		item6.setName(context.getString(R.string.CONTA_TYPE_S));
		list.add(item6);

		ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
				context, android.R.layout.simple_spinner_item, list);
		selectList
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_contaType.setAdapter(selectList);
		if (moreData.getContaType() != null) {
			SelectDict tmp = new SelectDict();
			tmp.setId(moreData.getContaType());
			spinner_contaType.setSelection(list.indexOf(tmp));
		}
		InitSort(context);

		InitStsPackingList(context);
	}

	/* 排序 */
	private void InitSort(final Context context) {
		List<SelectDict> list = new ArrayList<SelectDict>();
		SelectDict item = new SelectDict();
		item.setId("{vo.dtPackingDeadLine}");
		item.setName(context.getString(R.string.PP002_SORT_DEADLINEDATE));
		list.add(item);

		SelectDict item1 = new SelectDict();
		item1.setId("{vo.noBox}");
		item1.setName(context.getString(R.string.PP002_SORT_NOBOX));
		list.add(item1);

		SelectDict item2 = new SelectDict();
		item2.setId("{vo.cdAirLines}");
		item2.setName(context.getString(R.string.PP002_SORT_AIRLINE));
		list.add(item2);

		SelectDict item3 = new SelectDict();
		item3.setId("{vo.stsPacking}");
		item3.setName(context.getString(R.string.PP002_SORT_STSPACKING));
		list.add(item3);

		SelectDict item4 = new SelectDict();
		item4.setId("{vo.contaType}");
		item4.setName(context.getString(R.string.PP002_SORT_CONTATYPE));
		list.add(item4);

		ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
				context, android.R.layout.simple_spinner_item, list);
		selectList
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_sort.setAdapter(selectList);
		if (moreData.getSort() != null) {
			SelectDict tmp = new SelectDict();
			tmp.setId(moreData.getSort());
			spinner_sort.setSelection(list.indexOf(tmp));
		} else {
			spinner_sort.setSelection(0, true);
		}

	}

	private void setDialogResult() {

		if (listener != null) {

			moreData.setNoBox(editNoBox.getText().toString());
			moreData.setPackingName(editPackingName.getText().toString());

			moreData.setCdBooking(((SelectDict) spinner_Lime.getSelectedItem())
					.getId());
			moreData.setStsPacking(((SelectDict) spinner_stsPacking
					.getSelectedItem()).getId());
			moreData.setContaType(((SelectDict) spinner_contaType
					.getSelectedItem()).getId());
			moreData.setSort(((SelectDict) spinner_sort.getSelectedItem())
					.getId());

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
			try {
				if (!TextUtils.isEmpty(editFromDate.getText())) {
					moreData.setDeadLineStart(df.parse(editFromDate.getText()
							.toString()));
				} else {
					moreData.setDeadLineStart(null);
				}
				if (!TextUtils.isEmpty(editToDate.getText())) {
					moreData.setDeadLineEnd(df.parse(editToDate.getText()
							.toString()));
				} else {
					moreData.setDeadLineEnd(null);
				}

				if (!TextUtils.isEmpty(editETDFromDate.getText())) {
					moreData.setEtdStart(dfDate.parse(editETDFromDate.getText()
							.toString()));
				} else {
					moreData.setEtdStart(null);
				}

				if (!TextUtils.isEmpty(editETDToDate.getText())) {
					moreData.setEtdEnd(dfDate.parse(editETDToDate.getText()
							.toString()));
				} else {
					moreData.setEtdEnd(null);
				}

			} catch (ParseException e1) {

				e1.printStackTrace();
			}

			Intent intent = new Intent();
			intent.putExtra(OnDialogOkListener.OK, moreData);
			listener.onDialogResult(requestCode, Activity.RESULT_OK, intent);
		}
	}

	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;

		Date dateValue = (Date) data
				.getSerializableExtra(OnDialogOkListener.OK);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
		switch (requestCode) {
		case 1: // From date
			if (dateValue == null) {
				editFromDate.setText(null);
			} else {

				if (!TextUtils.isEmpty(editToDate.getText())) {
					try {
						Date deadLineEnd = df.parse(editToDate.getText()
								.toString());
						if (dateValue.after(deadLineEnd)) {
							Utils.showAlertDialog(editFromDate.getContext(),
									"开始时间不能比结束时间晚");
							break;
						}
					} catch (ParseException e1) {

						e1.printStackTrace();
					}
				}
				SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				editFromDate.setText(time.format(dateValue));
			}
			break;

		case 2: // To Date

			if (dateValue == null) {
				editToDate.setText(null);
			} else {

				if (!TextUtils.isEmpty(editFromDate.getText())) {
					try {
						Date deadLineStart = df.parse(editFromDate.getText()
								.toString());
						if (dateValue.before(deadLineStart)) {
							Utils.showAlertDialog(editFromDate.getContext(),
									"开始时间不能比结束时间晚");
							break;
						}
					} catch (ParseException e1) {

						e1.printStackTrace();
					}
				}
				SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				editToDate.setText(time.format(dateValue));
			}
			break;

		case 3: // From date
			if (dateValue == null) {
				editETDFromDate.setText(null);
			} else {

				if (!TextUtils.isEmpty(editToDate.getText())) {
					try {
						Date etdEnd = dfDate.parse(editETDToDate.getText()
								.toString());
						if (dateValue.after(etdEnd)) {
							Utils.showAlertDialog(editETDFromDate.getContext(),
									"开始时间不能比结束时间晚");
							break;
						}
					} catch (ParseException e1) {

						e1.printStackTrace();
					}
				}
				SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
				editETDFromDate.setText(time.format(dateValue));
			}
			break;

		case 4: // To Date

			if (dateValue == null) {
				editETDToDate.setText(null);
			} else {

				if (!TextUtils.isEmpty(editETDFromDate.getText())) {
					try {
						Date etdStart = dfDate.parse(editETDFromDate.getText()
								.toString());
						if (dateValue.before(etdStart)) {
							Utils.showAlertDialog(editETDFromDate.getContext(),
									"开始时间不能比结束时间晚");
							break;
						}
					} catch (ParseException e1) {

						e1.printStackTrace();
					}
				}
				SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
				editETDToDate.setText(time.format(dateValue));
			}
			break;
		default:
			break;
		}
	}
}