package com.amassfreight.warehouse.ui;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode01;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DN001DepotHD;
import com.amassfreight.domain.DepotDN001;
import com.amassfreight.domain.PP005StatusData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.amassfreight.warehouse.ui.dialogs.SelectPrintDialog;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;

public class DN001Activity_Single extends BaseActivity implements
		OnDialogOkListener {

	private String barCode = "";
	private String cdOrder = ""; // 进仓编号 个别
	private String depotID = ""; // 扫描后得到的进仓ID
	private boolean bolFlgInStore = false; // 进仓标志
	private EditText editOrder; // 进仓编号
	private EditText editThNo; // 同行编号
	//private EditText editNoBatch; //批次号
	private TextView txtLineType; // 航线
	private TextView txtBusinessType; // 业务类型
	private TextView txtOrderType; // 订单类型
	private TextView txtStatus; // 装箱状态
	private TextView txtCustomerLevel; // 客户级别
	private TextView txtSpecial; // 特殊保障客户

	private TextView txtJS; // 件数
	private TextView txtZL; // 重量
	private TextView txtTJ; // 体积
	private TextView txtGK; // 港口
	private TextView txtBZ; // 包装
	private TextView txtJHY; // 计划员
	private TextView txtMT;// 唛头
	private TextView txtRemark; // 进仓备注
	private TextView lblFlgInStore; // 进仓标志

	private Button btn_Single; // 异常报告

	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dn001_single);
		setupActionBar();

		// 初期加载
		initializeView();
	}

	/*
	 * 初期加载
	 */
	private void initializeView() {
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		editOrder = (EditText) findViewById(R.id.edit_Storage); // 进仓编号
		editThNo = (EditText) findViewById(R.id.edit_TH); // 同行编号
		//editNoBatch = (EditText) findViewById(R.id.edit_NoBatch); // 批次号
		txtLineType = (TextView) findViewById(R.id.lblLineType); // 航线
		txtBusinessType = (TextView) findViewById(R.id.lblBusinessType); // 业务类型
		txtOrderType = (TextView) findViewById(R.id.lblOrderType); // 订单类型
		txtStatus = (TextView) findViewById(R.id.lblStatus); // 装箱状态
		txtCustomerLevel = (TextView) findViewById(R.id.lblCustomerLevel); // 客户级别
		txtSpecial = (TextView) findViewById(R.id.lblSpecial); // 特殊保障客户

		txtJS = (TextView) findViewById(R.id.txtJS); // 件数
		txtZL = (TextView) findViewById(R.id.txtZL); // 重量
		txtTJ = (TextView) findViewById(R.id.txtTJ); // 体积
		txtGK = (TextView) findViewById(R.id.txtGK); // 港口
		txtBZ = (TextView) findViewById(R.id.txtBZ); // 包装
		txtJHY = (TextView) findViewById(R.id.txtJHY); // 计划员
		txtMT = (TextView) findViewById(R.id.txtMT); // 唛头
		txtRemark = (TextView) findViewById(R.id.txtRemark); // 进仓备注
		lblFlgInStore = (TextView) findViewById(R.id.lblFlgInStore); // 进仓标志

		btn_Single = (Button) findViewById(R.id.btn_Single);
		btn_Single.setVisibility(View.GONE);

		// 控件无效化
		// editOrder.setEnabled(false);
		editThNo.setEnabled(false);

		editOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode((EditText) view);

				ShowInputDialig();
			}
		});
	}

	/*
	 * 弹出进仓编号输入框
	 */
	public void ShowInputDialig() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.DN001_INSERT_STORE).setCancelable(false);
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.dn001_view_input_dialog, null);
		final EditText editTexCdOrder = (EditText) textEntryView
				.findViewById(R.id.editTextCdOrder);
		builder.setView(textEntryView);
		final AlertDialog dialog = builder.show();// 显示对话框
		dialog.setCanceledOnTouchOutside(false); // 点击空白处，不关闭对话框
		// "保存"按钮单击事件
		Button btnSave = (Button) textEntryView.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String strCdOrder = editTexCdOrder.getText().toString().trim(); // 进仓编号
				if (strCdOrder.length() > 0) {
					barCode = "";
					getDataByInput(Utils.completeOrderId(strCdOrder));
					dialog.dismiss(); // 关闭对话框
				}
			}
		});

		// "取消"按钮单击事件
		Button btnCancel = (Button) textEntryView.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss(); // 关闭对话框
			}
		});
	}

	/*
	 * 根据输入的进仓编号获取数据
	 */
	private void getDataByInput(String strCdOrder) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("cdOrderPublic", strCdOrder);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN001_GetDepotIDByCdOrderPublic", p1, new TypeToken<String>() {
				}.getType(), new AmassHttpResponseHandler<String>() {

					@Override
					protected void OnSuccess(String response) {
						super.OnSuccess(response);
						// 获取数据
						String strDepotID = (String) response;

						if (strDepotID == null) {
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.DN001_005_MSG));
							DoClear();
							return;
						} else {
							depotID = strDepotID;

							// 根据扫描到的进仓ID获取数据
							GetDataByDepotID();
						}
					}
				}, true);
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
	 * 创建右上角按钮
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_scanandprint, menu);
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
		case R.id.print_action:
			SelectPrintDialog dlg = new SelectPrintDialog(1, this);
			dlg.createDialog(this).show();
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
		Bundle bundle = data.getExtras();
		switch (requestCode) {

		case 2:
			// 扫描成功
			barCode = data.getStringExtra("SCAN_RESULT");
			GetDataByBarCode(barCode);
			break;
		}
	}

	/*
	 * 根据扫描的条码获取数据
	 */
	private void GetDataByBarCode(String barCode) {
		if (barCode.isEmpty()) {
			return;
		}

		final BarCode01 barCode01 = new BarCode01();
		boolean result01 = barCode01.paserBarCode(barCode);

		if (!result01) {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN001_001_MSG));
			DoClear();
			return;
		}

		depotID = barCode01.getDepotId();

		// 根据扫描到的进仓ID获取数据
		GetDataByDepotID();
	}

	/*
	 * 根据扫描到的进仓ID获取数据
	 */
	private void GetDataByDepotID() {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotID", depotID);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN001_GetDataByDepotID", p1, new TypeToken<DN001DepotHD>() {
				}.getType(), new AmassHttpResponseHandler<DN001DepotHD>() {

					@Override
					protected void OnSuccess(DN001DepotHD response) {
						super.OnSuccess(response);
						// 获取数据
						DN001DepotHD depot = (DN001DepotHD) response;

						if (depot == null) {
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.DN001_002_MSG));
							DoClear();
							return;
						} else {
							// 进仓编号存在的场合
							if (depot.getCdOrder() != null
									&& depot.getCdOrder().length() != 0) {
								// 进仓编号
								GetDepotByCdOrderAndDepotID(depot.getCdOrder(),
										false);
							} else if (depot.getCdOrder().length() == 0
									&& depot.getCoLoader().length() != 0) {
								// 进仓编号不存在，同行编号存在的场合
								editOrder.setText(depot.getCdOrderPublic());// 进仓编号
								editThNo.setText(depot.getCoLoader()); // 同行编号
								GetDepotByDepotID(false);
							} else {
								return;
							}
						}
					}
				}, true);
	}

	/*
	 * 页面清空
	 */
	private void DoClear() {
		barCode = "";
		editOrder.setText("");// 进仓编号
		editThNo.setText(""); // 同行编号
		txtLineType.setText(""); // 航线
		txtBusinessType.setText(""); // 业务类型
		txtOrderType.setText(""); // 订单类型
		txtStatus.setText(""); // 装箱状态
		txtCustomerLevel.setText(""); // 客户级别
		txtSpecial.setText(""); // 特殊保障客户
		txtJS.setText(""); // 件数
		txtZL.setText(""); // 重量
		txtTJ.setText(""); // 体积
		txtGK.setText(""); // 港口
		txtBZ.setText(""); // 包装
		txtJHY.setText(""); // 计划员
		txtMT.setText(""); // 唛头
		txtRemark.setText(""); // 进仓备注
		lblFlgInStore.setText("");
		btn_Single.setVisibility(View.GONE);
	}

	/*
	 * 进仓编号存在的场合
	 */
	private void GetDepotByCdOrderAndDepotID(String cdOrderPublic,
			final boolean flag) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("coOrder", cdOrderPublic);
		p1.put("depotID", depotID);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN001_GetDataByCode", p1, new TypeToken<DepotDN001>() {
				}.getType(), new AmassHttpResponseHandler<DepotDN001>() {

					@Override
					protected void OnSuccess(DepotDN001 response) {
						super.OnSuccess(response);
						// 获取数据
						DepotDN001 depot = (DepotDN001) response;
						// 判断是否有数据
						if (depot == null) {
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.DN001_002_MSG));
							return;
						} else {
							if (flag) {
								Utils.showAlertDialog(_thisActivity,
										getString(R.string.msg_common_success));
							}

							lblFlgInStore.setText(depot.getFlgInStore());
							bolFlgInStore = depot.isBolflgInStore();
							btn_Single.setVisibility(View.VISIBLE);
							// 控件赋值
							ShowData(depot);

						}
					}
				}, true);
	}

	private String ChangeDouble(double d, int scale, int dLen) {
		BigDecimal b = new BigDecimal(d);
		double f = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(dLen);
		return nf.format(f);
	}

	/*
	 * 进仓编号不存在，同行编号存在的场合
	 */
	private void GetDepotByDepotID(final boolean flag) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotID", depotID);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN001_GetDepotByCode", p1, new TypeToken<DepotDN001>() {
				}.getType(), new AmassHttpResponseHandler<DepotDN001>() {

					@Override
					protected void OnSuccess(DepotDN001 response) {
						super.OnSuccess(response);
						// 获取数据
						DepotDN001 depot = (DepotDN001) response;
						// 判断是否有数据
						if (depot == null) {
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.DN001_002_MSG));
							return;
						} else {
							if (flag) {
								Utils.showAlertDialog(_thisActivity,
										getString(R.string.msg_common_success));
							}
							btn_Single.setVisibility(View.VISIBLE);
							lblFlgInStore.setText(depot.getFlgInStore());
							bolFlgInStore = depot.isBolflgInStore();
						}
					}
				}, true);
	}

	/*
	 * 加载数据
	 */
	private void ShowData(DepotDN001 data) {

		cdOrder = data.getCdOrder(); // 进仓编号 共通
		editOrder.setText(data.getCdOrderPublic());// 进仓编号
		editThNo.setText(data.getCoLoader()); // 同行编号
		txtLineType.setText(data.getNmAirlines()); // 航线
		txtBusinessType.setText(data.getOrderType()); // 业务类型
		txtOrderType.setText(data.getCdOrderType()); // 订单类型
		txtStatus.setText(data.getStsPacking()); // 装箱状态
		txtCustomerLevel.setText(data.getRank()); // 客户级别
		txtSpecial.setText(data.getSpecial()); // 特殊保障客户
		txtJS.setText(data.getQtNum()); // 件数
		txtZL.setText(data.getQtKgs()); // 重量
		txtTJ.setText(data.getQtCbm()); // 体积
		txtGK.setText(data.getNmWpod()); // 港口
		txtBZ.setText(data.getmPackage()); // 包装
		txtJHY.setText(data.getNmPlanner()); // 计划员
		txtMT.setText(data.getMarks()); // 唛头
		txtRemark.setText(data.getRemark()); // 进仓备注
	}

	/*
	 * 换单时间
	 */
	public void Btn_Single_OnClick(View view) {

		/*
		if (editNoBatch.getText().toString().trim().equals("")){
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN001_010_MSG));
			return;
		}*/
		confirmSingleTime();
	}

	/*
	 * 换单完成时间
	 */
	public void confirmSingleTime() {

		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(getString(R.string.DN001_MSG_SINGLE_001))
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Map<String, Object> dn001Map = new HashMap<String, Object>();

								dn001Map.put("depotID", depotID);
								dn001Map.put("noBatch", "");
								NetworkHelper
										.getInstance()
										.postJsonData(
												_thisActivity,
												"InsertDepotTime",
												dn001Map,
												PP005StatusData.class,
												new AmassHttpResponseHandler<PP005StatusData>() {

													@Override
													protected void OnSuccess(
															PP005StatusData response) {
														super.OnSuccess(response);

														Utils.showAlertDialogIntMsg(
																_thisActivity,
																R.string.PP011_msg_packOk_004);

													}

												}, true);
							}
						})
				.setNegativeButton(getString(R.string.button_no), null).show();
	}

	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}

}
