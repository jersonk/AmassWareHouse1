package com.amassfreight.warehouse.ui;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.os.IBinder;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.ContextThemeWrapper;
import android.view.KeyEvent;

import android.view.MotionEvent;
import android.view.View;

import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;

import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;

import com.amassfreight.domain.DepotDN004;
import com.amassfreight.domain.PP005StatusData;

import com.amassfreight.domain.SelectDict;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;

import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;

public class PP011Activity_Spilt extends BaseActivity implements
		OnDialogOkListener {

	private String depotID = ""; // 扫描后得到的进仓ID

	private String depotDtID = ""; // 货物明细ID
	private String loadID1 = ""; // 理货员
	private String loadID2 = ""; // 装卸工
	private String AddType = ""; // 编辑，新增批次标识

	private TextView txtDepotNo; // 进仓编号
	private TextView txtNoBatch; // 批次
	private TextView txtNoPilecard; // 桩脚牌ID

	private Spinner spMeterModel; // 测量模式
	private EditText editLong; // 长
	private EditText editWide; // 宽
	private EditText editHigh; // 高
	private EditText editNumber; // 件数
	private EditText editArgueNumber; // 计费件数
	private EditText editWeight; // 重量
	private EditText editVolume; // 体积
	private EditText editDock; // 装卸工
	private Spinner spPackUnit; // 包装单位
	private EditText editPackType; // 包装类型
	private EditText editPos; // 库区
	private EditText editStore; // 库位
	private EditText txtRemark; // 备注

	private Button btnSave;

	private boolean saveFlag = false; // 保存完成标识
	private boolean updateFlag = false; // 是否需要更新车辆信息标识

	private List<SelectDict> posList;
	private String selPos;

	private List<SelectDict> packTypeList;
	private Map<String, String> packTypeMap;    // add by yxq 2014/10/31
	private String selPackType;

	private String strDepotDtId;

	private Integer numPage = 0;
	private DepotDN004 depotDtOld;
	private List<DepotDN004> depotDtNewList;
	private Integer reNumTotal = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp011_spilt);
		setupActionBar();

		depotDtOld = new DepotDN004();
		depotDtNewList = new ArrayList<DepotDN004>();

		// 初期加载
		initializeView();
	}

	/*
	 * 初期加载
	 */
	private void initializeView() {

		txtDepotNo = (TextView) findViewById(R.id.txtInStore); // 进仓编号
		editPos = (EditText) findViewById(R.id.edit_Pos); // 库区
		editStore = (EditText) findViewById(R.id.edit_Store); // 库位

		/* 获取Intent中的Bundle对象 */
		Bundle bundle = this.getIntent().getExtras();

		strDepotDtId = bundle.get("depotDtId").toString();
		selPos = bundle.get("pos").toString();
		editStore.setText(bundle.get("location").toString());
		txtDepotNo.setText(bundle.get("cdOrderPublic").toString());
		reNumTotal = (Integer) bundle.getInt("reNum");

		if (selPos != null && selPos.length() > 0
				&& editPos.getText().toString().trim().isEmpty()) {
			editPos.setText(selPos);
		}

		txtNoBatch = (TextView) findViewById(R.id.txtPC); // 批次
		txtNoPilecard = (TextView) findViewById(R.id.txtNoPilecard); // 桩脚牌ID

		spMeterModel = (Spinner) findViewById(R.id.sp_MeterModel); // 测量模式
		editLong = (EditText) findViewById(R.id.edit_Long); // 长
		editLong.setSelectAllOnFocus(true);
		editWide = (EditText) findViewById(R.id.edit_Wide); // 宽
		editWide.setSelectAllOnFocus(true);
		editHigh = (EditText) findViewById(R.id.edit_High); // 高
		editHigh.setSelectAllOnFocus(true);
		editNumber = (EditText) findViewById(R.id.edit_Number); // 件数
		editNumber.setSelectAllOnFocus(true);
		editArgueNumber = (EditText) findViewById(R.id.edit_ArgueNumber); // 计费件数
		editArgueNumber.setSelectAllOnFocus(true);
		editWeight = (EditText) findViewById(R.id.edit_Weight); // 重量
		editWeight.setSelectAllOnFocus(true);
		editVolume = (EditText) findViewById(R.id.edit_Volume); // 体积
		editVolume.setSelectAllOnFocus(true);
		editDock = (EditText) findViewById(R.id.edit_Dock); // 装卸工
		editDock.setSelectAllOnFocus(true);
		spPackUnit = (Spinner) findViewById(R.id.sp_PackUnit); // 包装单位
		editPackType = (EditText) findViewById(R.id.edit_PackType);// 包装类型

		editStore.setSelectAllOnFocus(true);
		txtRemark = (EditText) findViewById(R.id.txtRemark); // 备注

		btnSave = (Button) findViewById(R.id.btn_Save); // 保存按钮

		// 监听事件 计算体积
		DoFocusChangeListener(editLong);
		DoFocusChangeListener(editWide);
		DoFocusChangeListener(editHigh);
		DoFocusChangeListener(editArgueNumber);

		// 绑定数据
		ShowDataByDepotDtID();

		// 限制EditText输入整数、小数位数
		CheckDigits(editLong, 7, 2); // 长
		CheckDigits(editWide, 7, 2); // 宽
		CheckDigits(editHigh, 7, 2); // 高
		CheckDigits(editNumber, 7, 0); // 件数
		CheckDigits(editArgueNumber, 7, 0); // 计费件数
		CheckDigits(editWeight, 15, 3); // 重量
		CheckDigits(editVolume, 15, 4); // 体积

		// 件数焦点离开时赋值计费件数
		editNumber.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View arg0, boolean arg1) {
				// 赋值
				editArgueNumber.setText(editNumber.getText().toString().trim());
				ArgueNumber();
			}
		});

		// 库区
		editPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode((EditText) view);

				final String[] posId = new String[posList.size()];
				final String[] posName = new String[posList.size()];
				for (int j = 0; j < posList.size(); j++) {
					posId[j] = ((SelectDict) posList.get(j)).getId();
					posName[j] = ((SelectDict) posList.get(j)).getName();
				}
				int i = -1; // 初期选中项
				// 循环获取选择项
				if (selPos != null && selPos.length() > 0) {
					for (i = 0; i < posList.size(); i++) {
						if (((SelectDict) posList.get(i)).getId()
								.equals(selPos)) {
							break;
						}
					}
				}

				new AlertDialog.Builder(new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light))
						.setTitle(R.string.DN004_POS_SELECT)
						.setSingleChoiceItems(posName, i,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										editPos.setText(posName[which]);
										selPos = posId[which];
										dialog.dismiss();
									}
								}).show();
			}
		});

		// 包装类型
		editPackType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode((EditText) view);

				// 显示包装类型
				ShowPackTypeEditText();
			}
		});

	}

	/*
	 * 查看时设置控件无效化
	 */
	private void SetEnableForDetail() {
		ArgueNumber();
		if (numPage == 0) {

			spMeterModel.setEnabled(false); // 测量模式

			editNumber.setEnabled(false); // 件数
			editArgueNumber.setEnabled(false); // 计费件数

			// edit by yxq 2014/10/08 begin
			// 原代码 editDock.setEnabled(false); // 装卸工
			editDock.setFocusableInTouchMode(false); // 装卸工
			editDock.setTextColor(Color.GRAY);   //设置只读时的文字颜色
			// edit by yxq 2014/10/08 end
			spPackUnit.setEnabled(false); // 包装单位
			editPackType.setEnabled(false); // 包装类型
			editPos.setEnabled(false); // 库区

			editStore.setEnabled(false); // 库位
			txtRemark.setEnabled(false); // 备注
		} else {
			spMeterModel.setEnabled(true); // 测量模式

			editNumber.setEnabled(true); // 件数
			editArgueNumber.setEnabled(true); // 计费件数
			editLong.setText("");// 长
			editWide.setText("");// 宽
			editHigh.setText("");// 高
			editNumber.setText(String.valueOf(reNumTotal)); // 件数
			editArgueNumber.setText(String.valueOf(reNumTotal)); // 计费件数
			editWeight.setText(""); // 重量
			editVolume.setText(""); // 体积
			Integer numPileCard = ReturnInt(txtNoPilecard.getText().toString())
					+ numPage;

			NumberFormat formatter = NumberFormat.getNumberInstance();
			formatter.setMinimumIntegerDigits(2);
			formatter.setGroupingUsed(false);
			String s = formatter.format(numPileCard);

			txtNoPilecard.setText(s);
			// edit by yxq 2014/10/08 begin
			// 原代码 editDock.setEnabled(true); 		// 装卸工
			editDock.setText("999");
			editDock.setFocusableInTouchMode(false); // 装卸工
			editDock.setTextColor(Color.GRAY);   //设置只读时的文字颜色
			// edit by yxq 2014/10/08 end
			spPackUnit.setEnabled(true); // 包装单位
			editPackType.setEnabled(true); // 包装类型
			editPos.setEnabled(true); // 库区
			editStore.setEnabled(true); // 库位
			txtRemark.setEnabled(true); // 备注
		}
	}

	/*
	 * 显示包装类型
	 */
	private void ShowPackTypeEditText() {
		final String[] packTypeId = new String[packTypeList.size()];
		final String[] packTypeName = new String[packTypeList.size()];
		boolean[] checksItem = null;

		final List medicSelecID = new ArrayList(); // 选中的包装类型

		if (selPackType != null && selPackType.length() > 0) {
			if (selPackType.contains(",")) {
				for (String str : selPackType.split(",")) {
					medicSelecID.add(str);
				}
			} else {
				medicSelecID.add(selPackType);
			}
		}

		if (medicSelecID != null && medicSelecID.size() > 0) {
			checksItem = new boolean[packTypeList.size()];
		}

		for (int j = 0; j < packTypeList.size(); j++) {
			packTypeId[j] = ((SelectDict) packTypeList.get(j)).getId();
			packTypeName[j] = ((SelectDict) packTypeList.get(j)).getName();
		}

		for (int h = 0; h < packTypeList.size(); h++) {
			if (medicSelecID != null && medicSelecID.size() > 0) {
				boolean chkFlag = false;
				for (Object sel : medicSelecID) {
					if (packTypeId[h].toString().equals(sel.toString())) {
						chkFlag = true;
						break;
					}
				}
				checksItem[h] = chkFlag;
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light))
				.setTitle(R.string.DN004_PACKTYPE_SELECT).setCancelable(false);

		builder.setMultiChoiceItems(packTypeName, checksItem,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						if (isChecked) {
							medicSelecID.add(packTypeId[which]);
						} else {
							for (int i = 0; i < medicSelecID.size(); i++) {
								if (medicSelecID.get(i).toString()
										.equals(packTypeId[which])) {
									medicSelecID.remove(i);
								}
							}
						}
					}
				});

		// 取消
		builder.setNegativeButton(getString(R.string.button_no), null);

		// 确定
		builder.setPositiveButton(getString(R.string.button_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String str = "";
						String strID = "";
						// 排序
						// final List selTemp = new ArrayList(); // 选中的包装类型
						// for (int i = 0; i < packTypeList.size(); i++) {
						// for (Object sel : medicSelecID) {
						// if (((SelectDict) packTypeList.get(i)).getId()
						// .equals(sel)) {
						// selTemp.add(sel);
						// }
						// }
						// }
						for (Object sel : medicSelecID) {
							// edit by yxq 2014/10/31 begin
							/* 原代码 
							str += packTypeName[Integer.valueOf(sel.toString()) - 1]
									+ ",";*/
							str += packTypeMap.get(sel.toString()) + ",";
							// edit by yxq 2014/10/31 end
							strID += sel + ",";
						}
						if (str.length() > 0) {
							str = str.substring(0, str.length() - 1);
							strID = strID.substring(0, strID.length() - 1);
						}

						editPackType.setText(str);
						selPackType = strID;
					}
				}).show();
	}

	/** 点击空白隐藏软键盘 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();

			if (isShouldHideInput(v, ev)) {
				hideSoftInput(v.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/*
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/*
	 * 多种隐藏软件盘方法的其中一种
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/*
	 * 限制EditText输入整数、小数位数
	 */
	private void CheckDigits(final EditText edit, final int zlen, final int xlen) {
		edit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// s:变化后的所有字符
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// s:变化前的所有字符； start:字符开始的位置； count:变化前的总字节数；after:变化后的字节数
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				try {
					// S：变化后的所有字符；start：字符起始的位置；before: 变化之前的总字节数；count:变化后的字节数
					// 判断第一位是否为小数点
					if (start == 0 && s.toString().equals(".")) {
						edit.setText("");
					} else if (s.toString().contains(".")) {
						if (getOccur(s.toString(), ".") > 1) {
							edit.setText(s.toString().substring(0, start));
						}

						// 判断小数位数
						if (start - xlen > 1) {
							if (s.toString()
									.substring(start - xlen - 1, start - xlen)
									.equals(".")) {
								edit.setText(s.toString().substring(0, start));
							}
						}
					} else {
						// 没有小数点时判断位数
						if (s.toString().length() > zlen) {
							edit.setText(s.toString().substring(0, zlen));
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * 获取某个字符在字符串中的个数
	 */
	private int getOccur(String src, String find) {
		int o = 0;
		int index = -1;
		while ((index = src.indexOf(find, index)) > -1) {
			++index;
			++o;
		}
		return o;
	}

	/*
	 * 计算体积
	 */
	private void DoFocusChangeListener(EditText et) {
		et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean flag) {
				try {
					if (!flag) {
						ArgueNumber();
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * 体积
	 */
	private void ArgueNumber() {
		String strLong = "0";
		String strWide = "0";
		String strHigh = "0";
		String strNum = "0";
		if (editLong.getText().toString().trim().length() > 0) {
			strLong = editLong.getText().toString().trim();
		}
		if (editWide.getText().toString().trim().length() > 0) {
			strWide = editWide.getText().toString().trim();
		}
		if (editHigh.getText().toString().trim().length() > 0) {
			strHigh = editHigh.getText().toString().trim();
		}
		if (editArgueNumber.getText().toString().trim().length() > 0) {
			strNum = editArgueNumber.getText().toString().trim();
		}

		double flong = Double.parseDouble(strLong) / 100;
		double fwide = Double.parseDouble(strWide) / 100;
		double fhigh = Double.parseDouble(strHigh) / 100;
		double fnum = Double.parseDouble(strNum);

		double f = flong * fwide * fhigh * fnum;
		editVolume.setText(ChangeDouble(f, 4, 4).replace(",", ""));
	}

	/*
	 * 小数转换
	 */
	private String ChangeDouble(double d, int scale, int dLen) {
		BigDecimal b = new BigDecimal(d);
		double f = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(dLen);
		return nf.format(f);
	}

	/*
	 * 编辑时加载页面数据
	 */
	private void ShowDataByDepotDtID() {
		try {

			Map<String, Object> p1 = new HashMap<String, Object>();
			p1.put("depotDtId", strDepotDtId);

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"PP011_GetDepot", p1, DepotDN004.class,
					new AmassHttpResponseHandler<DepotDN004>() {

						@Override
						protected void OnSuccess(DepotDN004 response) {
							super.OnSuccess(response);
							DepotDN004 depot = response;
							if (depot != null) {

								// 绑定测量模式下拉框
								InitDropDownList(depot.getMeterModelList(),
										spMeterModel);

								// 绑定包装单位下拉框
								InitDropDownList(depot.getPackUnitList(),
										spPackUnit);

								// 绑定库区下拉框
								posList = depot.getPosList();

								// 包装类型
								packTypeList = depot.getPackTypeList();
								
								// add by yxq 2014/10/31 begin
								packTypeMap = new HashMap<String, String>();
								Iterator<SelectDict> it = packTypeList.iterator();
								while (it.hasNext()) {
									SelectDict item = it.next();
									packTypeMap.put(item.getId(), item.getName());
								}
								// add by yxq 2014/10/31 end

								setSpinnerItemSelectedByValue(spMeterModel,
										depot.getModeMeasure()); // 测量模式
								setSpinnerItemSelectedByValue(spPackUnit,
										depot.getPacking());// 包装
								editDock.setText(depot.getWorkerID()); // 装卸工

								selPackType = depot.getPackType();

								txtNoBatch.setText(String.valueOf(depot
										.getNoBatch())); // 批次
								txtNoPilecard.setText(depot.getNoPilecard()); // 桩脚牌ID
								editLong.setText(String.valueOf(depot
										.getNoLen()));// 长
								editWide.setText(String.valueOf(depot
										.getNoWidth()));// 宽
								editHigh.setText(String.valueOf(depot
										.getNoHeight()));// 高
								editNumber.setText(String.valueOf(depot
										.getDepotNum() - reNumTotal));// 件数
								editArgueNumber.setText(String.valueOf(depot
										.getBillingNum() - reNumTotal));// 计费件数
								editWeight.setText(ChangeDouble(depot.getKgs(),
										3, 3).replace(",", ""));// 重量
								editVolume.setText(ChangeDouble(depot.getCbm(),
										4, 4).replace(",", ""));// 体积
								txtRemark.setText(depot.getDepotRemark());// 进仓备注

								loadID1 = depot.getLoadID1();// 理货员
								loadID2 = depot.getLoadID2(); // 装卸ID
								depotID = depot.getDepotID();
								ShowPackType();

								SetEnableForDetail();
							}
						}
					}, true);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 显示包装类型
	 */
	private void ShowPackType() {
		// 包装类型赋值
		if (selPackType != null && selPackType.length() > 0
				&& editPackType.getText().toString().trim().isEmpty()) {
			String selContent = "";

			if (selPackType.contains(",")) {
				for (int i = 0; i < selPackType.split(",").length; i++) {
					for (int k = 0; k < packTypeList.size(); k++) {
						if (((SelectDict) packTypeList.get(k)).getId().equals(
								selPackType.split(",")[i])) {
							selContent += ((SelectDict) packTypeList.get(k))
									.getName() + ",";
							break;
						}
					}
				}
			} else {
				for (int k = 0; k < packTypeList.size(); k++) {
					if (((SelectDict) packTypeList.get(k)).getId().equals(
							selPackType)) {
						selContent = ((SelectDict) packTypeList.get(k))
								.getName();
						break;
					}
				}
			}
			if (selContent.substring(selContent.length() - 1,
					selContent.length()).equals(",")) {
				selContent = selContent.substring(0, selContent.length() - 1);
			}
			editPackType.setText(selContent);
		}
	}

	/*
	 * 绑定下拉框
	 */
	private void InitDropDownList(List<SelectDict> listData, Spinner sp) {
		if (listData != null && listData.size() > 0) {
			// 添加空行
			SelectDict seDict = new SelectDict();
			seDict.setId("");
			seDict.setName("");
			listData.add(0, seDict);

			ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
					_thisActivity, android.R.layout.simple_spinner_item,
					listData);
			selectList
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(selectList);
		}
	}

	/*
	 * 根据值, 设置spinner默认选中项
	 */
	private void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
		try {
			SpinnerAdapter apsAdapter = spinner.getAdapter(); // 得到SpinnerAdapter对象
			if (apsAdapter != null) {
				int k = apsAdapter.getCount();
				for (int i = 0; i < k; i++) {
					SelectDict se = (SelectDict) apsAdapter.getItem(i);
					if (value.equals(se.getId())) {
						spinner.setSelection(i, true);// 默认选中项
						break;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 禁止显示软键盘
	 */
	private void hideSoftInputMode(EditText editText) {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(editText.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/*
	 * 保存按钮事件
	 */
	public void Btn_Save_OnClick(View view) {
		if (editLong.hasFocus()) {
			editLong.clearFocus();
		}

		if (editWide.hasFocus()) {
			editWide.clearFocus();
		}

		if (editHigh.hasFocus()) {
			editHigh.clearFocus();
		}

		if (editArgueNumber.hasFocus()) {
			editArgueNumber.clearFocus();
		}

		// 入力验证
		if (!DoCheck()) {
			return;
		}

		String msg = getString(R.string.DN004_004_MSG);
		boolean flag = false;
		if (ReturnFloat(editLong.getText().toString().trim()) > 1190) {
			msg += getString(R.string.DN004_007_MSG);
			flag = true;
		}
		if (ReturnFloat(editWide.getText().toString().trim()) > 230) {
			if (flag) {
				msg += getString(R.string.DN004_023_MSG);
			}
			msg += getString(R.string.DN004_008_MSG);
			flag = true;
		}
		if (ReturnFloat(editHigh.getText().toString().trim()) > 250) {
			if (flag) {
				msg += getString(R.string.DN004_023_MSG);
			}
			msg += getString(R.string.DN004_022_MSG);
			flag = true;
		}

		if (flag) {
			ShowDialogSave(msg + getString(R.string.DN004_009_MSG));
		} else {
			SaveData();
		}
	}

	/*
	 * 入力验证
	 */
	private boolean DoCheck() {
		try {

			// 测量模式
			SelectDict meterModel = (SelectDict) spMeterModel.getSelectedItem();
			if (meterModel.getId().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_013_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			// 长
			if (editLong.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_014_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			if (ReturnFloat(editLong.getText().toString().trim()) == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_014_MSG)
								+ getString(R.string.DN004_025_MSG));
				return false;
			}

			// 宽
			if (editWide.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_015_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			if (ReturnFloat(editWide.getText().toString().trim()) == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_015_MSG)
								+ getString(R.string.DN004_025_MSG));
				return false;
			}

			// 高
			if (editHigh.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_016_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			if (ReturnFloat(editHigh.getText().toString()) == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_016_MSG)
								+ getString(R.string.DN004_025_MSG));
				return false;
			}

			// 件数
			if (editNumber.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_017_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			if (ReturnInt(editNumber.getText().toString().trim()) == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_017_MSG)
								+ getString(R.string.DN004_025_MSG));
				return false;
			}

			// 计费件数
			if (editArgueNumber.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_018_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			if (ReturnInt(editArgueNumber.getText().toString()) == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_018_MSG)
								+ getString(R.string.DN004_025_MSG));
				return false;
			}

			// 体积
			if (editVolume.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_019_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			if (ReturnFloat(editVolume.getText().toString().trim()) == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_019_MSG)
								+ getString(R.string.DN004_025_MSG));
				return false;
			}

			// 包装单位
			SelectDict spPack = (SelectDict) spPackUnit.getSelectedItem();
			if (spPack.getId().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_020_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			// 装卸工
			if (editDock.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_021_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
		    // edit by yxq 2014/10/08 begin
			// 原代码 } else if (editDock.getText().toString().trim().length() != 3) {
			} else if (editDock.getText().toString().trim().length() % 3 != 0) {
			// edit by yxq 2014/10/08 begin
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_021_MSG)
								+ getString(R.string.DN004_030_MSG));
				return false;
			}

			// 库区
			if (editPos.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_026_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			// 库位
			if (editStore.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_027_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/*
	 * 显示超长、超宽，超高确认框
	 */
	private void ShowDialogSave(String msg) {
		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(msg)
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								SaveData();
							}
						})
				.setNegativeButton(getString(R.string.button_no), null).show();
	}

	/*
	 * 保存数据
	 */
	private void SaveData() {
		// 新增
		if (numPage == 0) {
			// 更新
			DoUpdate();
		} else {
			DoSave();
		}
	}

	/*
	 * 新增
	 */
	public void DoSave() {
		try {
			if (ReturnInt(editNumber.getText().toString().trim()) > reNumTotal) {
				Utils.showAlertDialogIntMsg(_thisActivity,
						R.string.PP011_MSG_012);
				return;
			}
			Integer totalReNum = reNumTotal
					- ReturnInt(editNumber.getText().toString().trim());
			if (totalReNum > 0) {
				ShowDialogStopOrContinue(String.format(getResources()
						.getString(R.string.PP011_MSG_013), totalReNum), 1);
			} else {
				ShowDialogStopOrContinue(getString(R.string.PP011_MSG_016), 3);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 更新
	 */
	private void DoUpdate() {

		ShowDialogStopOrContinue(String.format(
				getResources().getString(R.string.PP011_MSG_014), reNumTotal),
				0);
	}

	/*
	 * 返回事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 按下的如果是BACK，同时没有重复
			ShowDialogStopOrContinue(getString(R.string.PP011_MSG_015), 2);
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * 文本框验证
	 */
	private int ReturnInt(String str) {
		if (str.trim().length() > 0) {
			return Integer.parseInt(str.trim());
		} else {
			return 0;
		}
	}

	/*
	 * 文本框验证
	 */
	private Double ReturnFloat(String str) {
		if (str.trim().length() > 0) {
			return Double.parseDouble(str.trim());
		} else {
			return Double.parseDouble("0");
		}
	}

	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}

	/*
	 * 保存处理
	 */
	private void ShowDialogStopOrContinue(String msg, final Integer flag) {
		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(msg)
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (flag == 0) {

									depotDtOld.setDepotDtID(strDepotDtId); // 货物明细ID
									depotDtOld.setNoLen(ReturnFloat(editLong
											.getText().toString().trim()));// 长
									depotDtOld.setNoWidth(ReturnFloat(editWide
											.getText().toString().trim()));// 宽
									depotDtOld.setNoHeight(ReturnFloat(editHigh
											.getText().toString().trim()));// 高
									depotDtOld.setDepotNum(ReturnInt(editNumber
											.getText().toString().trim()));// 进仓件数
									depotDtOld
											.setBillingNum(ReturnInt(editArgueNumber
													.getText().toString()
													.trim()));// 计费件数
									depotDtOld.setKgs(ReturnFloat(editWeight
											.getText().toString().trim()));// 重量
									depotDtOld.setCbm(ReturnFloat(editVolume
											.getText().toString().trim()));// 体积
									numPage = +1;
									SetEnableForDetail();
								} else if (flag == 1) {
									DepotDN004 depotDtNew = new DepotDN004();
									depotDtNew.setDepotID(depotID); // 进仓ID
									depotDtNew.setNoBatch(Integer
											.parseInt(txtNoBatch.getText()
													.toString())); // 批次
									depotDtNew.setNoLen(ReturnFloat(editLong
											.getText().toString().trim()));// 长
									depotDtNew.setNoWidth(ReturnFloat(editWide
											.getText().toString().trim()));// 宽
									depotDtNew.setNoHeight(ReturnFloat(editHigh
											.getText().toString().trim()));// 高
									depotDtNew.setDepotNum(ReturnInt(editNumber
											.getText().toString().trim()));// 进仓件数
									depotDtNew
											.setBillingNum(ReturnInt(editArgueNumber
													.getText().toString()
													.trim()));// 计费件数
									SelectDict spPack = (SelectDict) spPackUnit
											.getSelectedItem();
									depotDtNew.setPacking(spPack.getId());// 包装
									depotDtNew.setPackType(selPackType);// 包装类型
									depotDtNew.setKgs(ReturnFloat(editWeight
											.getText().toString().trim()));// 重量
									depotDtNew.setCbm(ReturnFloat(editVolume
											.getText().toString().trim()));// 体积
									depotDtNew.setDepotRemark(txtRemark
											.getText().toString().trim());// 进仓备注

									SelectDict meterModel = (SelectDict) spMeterModel
											.getSelectedItem();
									depotDtNew.setModeMeasure(meterModel
											.getId());// 测量模式

									depotDtNew.setPos(editPos.getText()
											.toString());// 库区
									depotDtNew.setLocation(editStore.getText()
											.toString());// 库位

									depotDtNew.setWorkerID(editDock.getText()
											.toString().trim());
									reNumTotal = reNumTotal
											- ReturnInt(editNumber.getText()
													.toString().trim());
									depotDtNewList.add(depotDtNew);
									numPage = +1;
									SetEnableForDetail();
								} else if (flag == 2) {
									// 注销该页面
									setResult(Activity.RESULT_CANCELED);
									_thisActivity.finish();
								} else if (flag == 3) {

									DepotDN004 depotDtNew = new DepotDN004();
									depotDtNew.setDepotID(depotID); // 进仓ID
									depotDtNew.setNoBatch(Integer
											.parseInt(txtNoBatch.getText()
													.toString())); // 批次
									depotDtNew.setNoLen(ReturnFloat(editLong
											.getText().toString().trim()));// 长
									depotDtNew.setNoWidth(ReturnFloat(editWide
											.getText().toString().trim()));// 宽
									depotDtNew.setNoHeight(ReturnFloat(editHigh
											.getText().toString().trim()));// 高
									depotDtNew.setDepotNum(ReturnInt(editNumber
											.getText().toString().trim()));// 进仓件数
									depotDtNew
											.setBillingNum(ReturnInt(editArgueNumber
													.getText().toString()
													.trim()));// 计费件数
									SelectDict spPack = (SelectDict) spPackUnit
											.getSelectedItem();
									depotDtNew.setPacking(spPack.getId());// 包装
									depotDtNew.setPackType(selPackType);// 包装类型
									depotDtNew.setKgs(ReturnFloat(editWeight
											.getText().toString().trim()));// 重量
									depotDtNew.setCbm(ReturnFloat(editVolume
											.getText().toString().trim()));// 体积
									depotDtNew.setDepotRemark(txtRemark
											.getText().toString().trim());// 进仓备注

									SelectDict meterModel = (SelectDict) spMeterModel
											.getSelectedItem();
									depotDtNew.setModeMeasure(meterModel
											.getId());// 测量模式
									depotDtNew.setPos(editPos.getText()
											.toString());// 库区
									depotDtNew.setLocation(editStore.getText()
											.toString());// 库位

									depotDtNew.setWorkerID(editDock.getText()
											.toString().trim());
									reNumTotal = reNumTotal
											- ReturnInt(editNumber.getText()
													.toString().trim());

									depotDtNewList.add(depotDtNew);

									Map<String, Object> pp011Map = new HashMap<String, Object>();

									pp011Map.put("depotDt", depotDtOld);
									pp011Map.put("depotDtList", depotDtNewList);

									NetworkHelper
											.getInstance()
											.postJsonData(
													_thisActivity,
													"PP011_SaveData",
													pp011Map,
													PP005StatusData.class,
													new AmassHttpResponseHandler<PP005StatusData>() {

														@Override
														protected void OnSuccess(
																PP005StatusData response) {
															super.OnSuccess(response);
															setResult(Activity.RESULT_OK);
															_thisActivity
																	.finish();
														}

													}, true);
								}

							}
						})
				.setNegativeButton(getString(R.string.button_no), null).show();
	}

}