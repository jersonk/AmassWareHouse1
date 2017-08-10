package com.amassfreight.warehouse.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DN006ResponseData;
import com.amassfreight.domain.SelectDict;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;

public class DN006Activity extends BaseActivity {

	private Spinner spReservoir; // 库区
	private EditText txtStorage; // 库位
	private ListView listView;
	private Button btnSave;
	private Button btnClear;
	private DN006Adapter adapter;
	private LayoutInflater mInflater;
	private List<DN006ResponseData> depotDataList;
	private String depotDtID = ""; // 扫描后得到的货物明细ID
	private String strLocation = ""; // 参数：库位
	private String strPos = ""; // 参数：库区

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dn006);
		setupActionBar();
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 初始化数据源
		depotDataList = new ArrayList<DN006ResponseData>();
		// 库区
		spReservoir = (Spinner) findViewById(R.id.sp_reservoir);
		// 库位
		txtStorage = (EditText) findViewById(R.id.edit_Storage);
		// 列表控件
		listView = (ListView) findViewById(R.id.result_listView);
		// 保存按钮
		btnSave = (Button) findViewById(R.id.btn_Save);
		// 隐藏保存按钮
		btnSave.setVisibility(View.GONE);

		btnClear = (Button) findViewById(R.id.btn_Clear);

		btnClear.setVisibility(View.GONE);

		// 绑定库区
		InitStoreList();
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
	 * 绑定库区下拉框
	 */
	private void InitStoreList() {
		Type a = new TypeToken<List<SelectDict>>() {
		}.getType();

		NetworkHelper.getInstance().postData(this, "GetDepotNoListByUser",
				null, a, new AmassHttpResponseHandler<List<SelectDict>>() {
					@Override
					protected void OnSuccess(List<SelectDict> response) {
						// TODO Auto-generated method stub
						super.OnSuccess(response);
						List<SelectDict> listData = response;
						ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
								_thisActivity,
								android.R.layout.simple_spinner_item, listData);
						selectList
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spReservoir.setAdapter(selectList);
					}

					@Override
					protected void onFailure(int statusCode, Header[] headers,
							String responseBody, Throwable e) {
						super.onFailure(statusCode, headers, responseBody, e);
					}
				}, true);
	}

	/*
	 * 保存按钮事件
	 */
	public void Btn_Save_OnClick(View view) {
		// 数据源非空判断
		if (depotDataList.size() == 0) {
			return;
		}

		// 获取库区
		SelectDict spReservoirItem = (SelectDict) spReservoir.getSelectedItem();
		strPos = spReservoirItem.getId();

		// 库区非空判断
		if (strPos.isEmpty()) {
			Utils.showAlertDialog(this,
					getString(R.string.DN006_LOCATION_INPUT));
			spReservoir.requestFocus();
			return;
		}

		// 库位非空判断
		if (txtStorage.getText().toString().trim().isEmpty()) {
			Utils.showAlertDialog(this, getString(R.string.DN006_POS_INPUT));
			txtStorage.requestFocus();
			return;
		}

		// 库位补0
		if (txtStorage.getText().toString().trim().length() == 1) {
			txtStorage.setText("0" + txtStorage.getText().toString().trim());
		}

		strLocation = txtStorage.getText().toString().trim();

		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(
						getString(R.string.DN006_CRFIM_MSG) + strPos + "-"
								+ strLocation)
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// 保存
								DoSave();
							}
						})
				.setNegativeButton(getString(R.string.button_no), null).show();
	}

	/*
	 * 执行保存
	 */
	private void DoSave() {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("strLocation", strLocation);
		p1.put("strPos", strPos);
		p1.put("listDepot", depotDataList);

		NetworkHelper.getInstance().postJsonData(this, "DN006_DoSave", p1,
				new TypeToken<List<DN006ResponseData>>() {
				}.getType(),
				new AmassHttpResponseHandler<List<DN006ResponseData>>() {

					@Override
					protected void OnSuccess(List<DN006ResponseData> response) {
						// TODO Auto-generated method stub
						super.OnSuccess(response);
						// 获取数据
						List<DN006ResponseData> depotList = (List<DN006ResponseData>) response;
						// 判断是否有数据
						if (depotList != null && depotList.size() > 0) {
							DisplayMetrics dm = new DisplayMetrics();
							getWindowManager().getDefaultDisplay().getMetrics(
									dm);

							adapter = new DN006Adapter(_thisActivity,
									depotList, dm.widthPixels, btnSave,
									btnClear);
							// adapter.setData(depotDataList);
							listView.setAdapter(adapter);

							Utils.showAlertDialog(_thisActivity,
									getString(R.string.msg_save_success));
						}
					}
				}, true);
	}

	/*
	 * 获取listview数据
	 */
	private void GetBarcode(final List<DN006ResponseData> depotDataList) {

		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("deportID", depotDtID);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN006_GetDepotByDepotID", p1,
				new TypeToken<DN006ResponseData>() {
				}.getType(), new AmassHttpResponseHandler<DN006ResponseData>() {

					@Override
					protected void OnSuccess(DN006ResponseData response) {
						super.OnSuccess(response);
						// 获取数据
						DN006ResponseData depot = (DN006ResponseData) response;
						// 判断是否有数据
						if (depot == null) {
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.DN006_RESPONSE_EMPTY));
							return;
						} else {
							DN006ResponseData data = depot;
							depotDataList.add(data);
						}

						DisplayMetrics dm = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dm);

						adapter = new DN006Adapter(_thisActivity,
								depotDataList, dm.widthPixels, btnSave,
								btnClear);
						// adapter.setData(depotDataList);
						listView.setAdapter(adapter);

						if (depotDataList.size() > 0) {
							btnSave.setVisibility(View.VISIBLE);
							btnClear.setVisibility(View.VISIBLE);
						}
					}
				}, true);
	}

	/*
	 * 创建右上角按钮
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_scan, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * 扫描事件
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan_action:
			// 扫描
			Intent intent = new Intent(this, CaptureActivity.class);
			intent.putExtra("SCAN_MODE", "SCAN_MODE");
			startActivityForResult(intent, 2);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * 扫描后返回事件
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {

		case 2:
			if (resultCode == RESULT_OK) {
				// 扫描成功
				String barCode = data.getStringExtra("SCAN_RESULT");

				if (barCode.isEmpty()) {
					return;
				}

				final BarCode02 barCode02 = new BarCode02();
				boolean result02 = barCode02.paserBarCode(barCode);

				if (!result02) {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN006_001_MSG));
					return;
				}

				depotDtID = barCode02.getDepotDtId();

				boolean flag = true;
				depotDataList = new ArrayList<DN006ResponseData>();
				// 判断数据源中是否有数据
				if (adapter != null && adapter.getCount() > 0) {
					for (int i = 0; i < adapter.getCount(); i++) {
						DN006ResponseData depot = (DN006ResponseData) adapter
								.getItem(i);
						// 判断数据是否存在 不存在时添加数据
						if (depot.getDepotDtID().equals(depotDtID)) {
							flag = false;
						}
						depotDataList.add(depot);
					}

					// 数据源中不存在时向数据源中添加数据
					if (flag) {
						GetBarcode(depotDataList);
					}
				} else {
					GetBarcode(depotDataList);
				}
			}
			break;
		}
	}

	/*
	 * 清空按钮事件
	 */
	public void Btn_Clear_OnClick(View view) {
		if (depotDataList.size() == 0) {
			return;
		}
		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(getString(R.string.DN006_CLEAR_MSG))
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// 初始化数据源
								depotDataList = new ArrayList<DN006ResponseData>();

								DisplayMetrics dm = new DisplayMetrics();
								getWindowManager().getDefaultDisplay()
										.getMetrics(dm);

								// 清空适配器
								adapter = new DN006Adapter(_thisActivity,
										depotDataList, dm.widthPixels, btnSave,
										btnClear);
								// adapter.setData(depotDataList);
								listView.setAdapter(adapter);

								// 清空库区、库位
								txtStorage.setText("");
								spReservoir.setSelection(0);

								// 隐藏按钮
								btnSave.setVisibility(View.GONE);
								btnClear.setVisibility(View.GONE);

							}
						})
				.setNegativeButton(getString(R.string.button_no), null).show();
	}
}
