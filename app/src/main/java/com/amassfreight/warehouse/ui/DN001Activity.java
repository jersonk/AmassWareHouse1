package com.amassfreight.warehouse.ui;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode01;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.CommonResult;
import com.amassfreight.domain.DN001DepotHD;
import com.amassfreight.domain.DN001ListData;
import com.amassfreight.domain.DepotDN001;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.amassfreight.warehouse.ui.dialogs.SelectPrintDialog;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;

/**
 * 理货信息确认页
 * @author U11001548
 *
 */
public class DN001Activity extends BaseActivity implements OnDialogOkListener {

	private String barCode = "";
	private String cdOrder = ""; // 进仓编号 个别
	private String depotID = ""; // 扫描后得到的进仓ID
	private boolean bolFlgInStore = false; // 进仓标志
	private EditText editOrder; // 进仓编号
	private EditText editThNo; // 同行编号
	private TextView txtLineType; // 航线
	private TextView txtBusinessType; // 业务类型
	//private TextView txtOrderType; // 订单类型
	private TextView txtStatus; // 装箱状态
	private TextView txtCustomerLevel; // 客户级别
	private TextView txtSpecial; // 特殊保障客户
	private TextView guarExist;  // 有无保函
	private TextView txtFlgCommChemical;

	private TextView txtJS; // 件数
	private TextView txtZL; // 重量
	private TextView txtTJ; // 体积
	private TextView txtGK; // 港口
	private TextView txtBZ; // 包装
	private TextView txtJHY; // 计划员
	private TextView txtMT;// 唛头
	private TextView txtRemark; // 进仓备注
	private TextView lblFlgInStore; // 进仓标志

	private ListView listView;
	private TextView txtHJ;
	private TextView txtSUMJS; // 合计：件数
	private TextView txtSUMZL; // 合计：重量
	private TextView txtSUMTJ; // 合计：体积
	private LinearLayout horHJ;

	private Button btn_Error; // 异常报告
	private Button btn_Add; // 新增批次
	private Button btn_ShowMSDS; //一般化工品操作要求

	private ArrayAdapter adapter;
	private LayoutInflater mInflater;
	private List<DN001ListData> depotDataList;

	private int sumJS;
	private double sumZL;
	private double sumTJ;

	private int selFlag = 0;

	private String[] selItem = null;
	private boolean bolRole = false;

	private LinearLayout linMain;
	private Boolean meunShow = false;
	private ImageButton imgBtn;
	private TextView tvOprName;
	private boolean btnFlag = false;
	
	private String jjn;						//应急处理
	private String fqn;						//消除措施
	private String cczyn;					//安全操作的注意事项
	private String ccczn;					//安全储存的条件
		
	//需要显示公司简称的特殊客户列表
	private String[] specialCustomers = {"AMI002495","AMI002494","AMI026690"  //密尔克卫
			,"AMI002377","AMI034780","AMI042076","AMI002378","AMI024659","AMI025611","AMI042077" //上海、苏州宏广
			,"AMI001171","AMI001170","AMI025332","AMI027289","AMI030414" //兰森
			,"AMI015963","AMI027576","AMI029639" //江苏海航
			,"AMI012827" //库尔兹压烫科技
			,"AMI004939" //上海申克机械
			,"AMI011567"
			,"AMI006297"
			};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dn001);
		setupActionBar();
		initView();
	}

	private void initView() {
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		depotDataList = new ArrayList<DN001ListData>();

		editOrder = (EditText) findViewById(R.id.edit_Storage); // 进仓编号
		editThNo = (EditText) findViewById(R.id.edit_TH); // 同行编号
		txtLineType = (TextView) findViewById(R.id.lblLineType); // 航线
		txtBusinessType = (TextView) findViewById(R.id.lblBusinessType); // 业务类型
		//txtOrderType = (TextView) findViewById(R.id.lblOrderType); // 订单类型
		guarExist = (TextView) findViewById(R.id.guarExist); // 订单类型
		
		txtStatus = (TextView) findViewById(R.id.lblStatus); // 装箱状态
		txtCustomerLevel = (TextView) findViewById(R.id.lblCustomerLevel); // 客户级别
		txtSpecial = (TextView) findViewById(R.id.lblSpecial); // 特殊保障客户
		txtFlgCommChemical = (TextView) findViewById(R.id.lblFlgCommChemical); // 一般化工品

		tvOprName = (TextView) findViewById(R.id.tvOPrName);
		linMain = (LinearLayout) findViewById(R.id.linMain);
		imgBtn = (ImageButton) findViewById(R.id.imgBtn);

		txtJS = (TextView) findViewById(R.id.txtJS); // 件数
		txtZL = (TextView) findViewById(R.id.txtZL); // 重量
		txtTJ = (TextView) findViewById(R.id.txtTJ); // 体积
		txtGK = (TextView) findViewById(R.id.txtGK); // 港口
		txtBZ = (TextView) findViewById(R.id.txtBZ); // 包装
		txtJHY = (TextView) findViewById(R.id.txtJHY); // 计划员
		txtMT = (TextView) findViewById(R.id.txtMT); // 唛头
		txtRemark = (TextView) findViewById(R.id.txtRemark); // 进仓备注
		lblFlgInStore = (TextView) findViewById(R.id.lblFlgInStore); // 进仓标志

		txtHJ = (TextView) findViewById(R.id.lblHJ); // 合计
		txtSUMJS = (TextView) findViewById(R.id.lblSUMJS); // 合计：件数
		txtSUMZL = (TextView) findViewById(R.id.lblSUMZL); // 合计：重量
		txtSUMTJ = (TextView) findViewById(R.id.lblSUMTJ); // 合计：体积
		horHJ = (LinearLayout) findViewById(R.id.hor_HJ);
		horHJ.setVisibility(View.GONE);

		// 列表控件
		listView = (ListView) findViewById(R.id.result_listView);

		btn_Error = (Button) findViewById(R.id.btn_Error);
		btn_Error.setEnabled(btnFlag);
		btn_Add = (Button) findViewById(R.id.btn_Add);
		btn_Add.setEnabled(bolFlgInStore);
		
		btn_ShowMSDS = (Button) findViewById(R.id.btn_ShowMSDS);
		btn_ShowMSDS.setVisibility(View.GONE);

		// 控件无效化
		// editOrder.setEnabled(false);
		// 注销 by yxq 2014/10/09 editThNo.setEnabled(false);

		editOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				hideSoftInputMode((EditText) view);
				ShowInputDialig();
			}
		});
		// add by yxq 2014/10/09 begin
		editThNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode((EditText) view);
				ShowCdLoaderInputDialig();
			}
		});
	}

	
	//############################# 弹出输入框 获取相应的数据 ###################################
	
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
		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String strCdOrder = editTexCdOrder.getText().toString().trim(); // 进仓编号
				if (strCdOrder.length() > 0) {
					barCode = "";
					getDataByInputBinaryCode(Utils.completeOrderId(strCdOrder));
					dialog.dismiss(); // 关闭对话框
				}
			}
		});

		// "取消"按钮单击事件
		Button btnCancel = (Button) textEntryView.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss(); // 关闭对话框
			}
		});
	}

	/*
	 * 根据输入的进仓编号获取理货信息
	 */
	private void getDataByInputBinaryCode(String strCdOrder) {
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
							depotID = strDepotID;//进仓ID

							// 根据扫描到的进仓ID获取数据
							GetDataByDepotID();
						}
					}
				}, true);
	}
	
	
	

	// add by yxq 2014/10/09 begin
	/*
	 * 弹出同行编号输入框
	 */
	public void ShowCdLoaderInputDialig() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.DN001_THNO).setCancelable(false);
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.dn001_view_loader_input_dialog, null);
		final EditText editTexCdLoader = (EditText) textEntryView.findViewById(R.id.editTextCdLoader);
		builder.setView(textEntryView);
		final AlertDialog dialog = builder.show();// 显示对话框
		dialog.setCanceledOnTouchOutside(false); // 点击空白处，不关闭对话框
		// "保存"按钮单击事件
		Button btnSearch = (Button) textEntryView.findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String strCdLoader = editTexCdLoader.getText().toString().trim(); // 进仓编号
				if (strCdLoader.length() > 0) {
					barCode = "";
					getDataByCdLoaderInput(strCdLoader);
					dialog.dismiss(); // 关闭对话框
				}
			}
		});

		Button btnCancel = (Button) textEntryView.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss(); // 关闭对话框
			}
		});
	}
	/*
	 * 根据输入的同行编号获取理货信息
	 */
	private void getDataByCdLoaderInput(String strCdLoader) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("cdLoader", strCdLoader);
		NetworkHelper.getInstance().postJsonData(_thisActivity,
			"DN001_GetDepotIDByCdLoader", p1, new TypeToken<String>() {
			}.getType(), new AmassHttpResponseHandler<String>() {
				@Override
				protected void OnSuccess(String response) {
					super.OnSuccess(response);
					// 获取数据
					String strDepotID = (String) response;
					if (strDepotID == null) {
						Utils.showAlertDialog(_thisActivity,getString(R.string.DN001_006_MSG));
						DoClear();
						return;
					} else {
						depotID = strDepotID;
						cdOrder = "";   //add by sdhuang 2014/11/11
						GetDataByDepotID();    // 根据扫描到的进仓ID获取数据
					}
				}
			}, true);
	}
	
	
	//############################################ input dialog end ###############################################################
	
	//############################################ menu item click response(now no print option)###############################################################

	
	/*
	 * 隐藏软键盘
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
		//根据actionbar获取相应的 菜单栏加载器，添加相应的条目
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_scanandprint, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * 菜单条目点击事件
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan_action:
			//扫描
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
	 * 扫描后返回响应
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		Bundle bundle = data.getExtras();
		switch (requestCode) {

		case 2: //理货界面进入扫进仓通知书上的码
			// 扫描成功
			barCode = data.getStringExtra("SCAN_RESULT");
			GetDataByBarCode(barCode);
			break;
		case 3:
			btnFlag = (boolean) bundle.getBoolean("btnFlag");
			bolFlgInStore = (boolean) bundle.getBoolean("bolFlgInStore");
			if (barCode != null && barCode.length() > 0) {
				GetDataByBarCode(barCode);
			} else if (depotID != null && depotID.length() > 0) {
				GetDataByDepotID();
			}
			break;
		case 4:
			btnFlag = (boolean) bundle.getBoolean("btnFlag");
			bolFlgInStore = (boolean) bundle.getBoolean("bolFlgInStore");
			if (barCode != null && barCode.length() > 0) {
				GetDataByBarCode(barCode);
			} else if (depotID != null && depotID.length() > 0) {
				GetDataByDepotID();
			}
			break;
		}
	}

	/*
	 * 根据扫描的条码获取数据
	 * 注意：扫描出来的二维码是由 “01，depotId，orderCd，coLoader”组成的
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
	 * 根据进仓ID获取数据 DN001DepotHD 的数据
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

						if (depot == null) { //扫描到的进仓编号或同行编号不存在
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.DN001_002_MSG));
							DoClear();
							return;
						} else {
							// 进仓编号存在的场合
							if (depot.getCdOrder() != null
									&& depot.getCdOrder().length() != 0) {
								selFlag = 1;
								// 进仓编号
								GetDepotByCdOrderAndDepotID(depot.getCdOrder(),
										false);
							} else if (depot.getCdOrder().length() == 0
									&& depot.getCoLoader().length() != 0) {
								// 进仓编号不存在，同行编号存在的场合
								selFlag = 2;
								DoClear();     // add by yxq 2014/09/02
								editOrder.setText(depot.getCdOrderPublic());// 进仓编号
								editThNo.setText(depot.getCoLoader()); // 同行编号
								GetDepotByDepotID(false);
							} else {
								return;
							}
						}
						btnFlag = true;
						btn_Error.setEnabled(true);
						// 获取数据
						GetListView();
					}
				}, true);
	}
	
	//############################################ menu item click response(scan code end)###############################################################
	
	

	/*
	 * 清空页面数据
	 */
	private void DoClear() {
		barCode = "";
		editOrder.setText("");// 进仓编号
		editThNo.setText(""); // 同行编号
		txtLineType.setText(""); // 航线
		txtBusinessType.setText(""); // 业务类型
		//txtOrderType.setText(""); // 订单类型
		guarExist.setText("");  //有无保函
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
		txtFlgCommChemical.setText("");//一般化工品
		horHJ.setVisibility(View.GONE);

		btn_Error.setEnabled(false);
		btn_Add.setEnabled(false);
		lblFlgInStore.setText("");

		if (adapter != null && adapter.getCount() > 0) {
			adapter.clear();
		}
	}

	/*
	 * 进仓编号存在的场合
	 * DepotDN001
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
							
							// edit by yxq 2014/08/29 begin
						    // 原代码 lblFlgInStore.setText(depot.getFlgInStore());
							lblFlgInStore.setText(depot.getContainerStatusNm());
							setStatusColor(depot.getContainerStatus());   // 设置字体颜色
							// edit by yxq 2014/08/29 end
							bolFlgInStore = depot.isBolflgInStore();
							btn_Add.setEnabled(bolFlgInStore);

							// 控件赋值
							ShowData(depot);

							depotDataList = depot.getDn001List();// list

							//计算合计信息
							if (depotDataList != null
									&& depotDataList.size() > 0) {

								horHJ.setVisibility(View.VISIBLE);

								sumJS = 0;
								sumZL = 0;
								sumTJ = 0;

								for (DN001ListData info : depotDataList) {
									sumJS += info.getSumNum();
									sumZL += info.getSumKgs();
									sumTJ += info.getSumCbm();
								}

								txtHJ.setVisibility(View.VISIBLE);
								txtSUMJS.setVisibility(View.VISIBLE);
								txtSUMZL.setVisibility(View.VISIBLE);
								txtSUMTJ.setVisibility(View.VISIBLE);

								txtSUMJS.setText(String.valueOf(sumJS)); // 合计：件数
								txtSUMZL.setText(ChangeDouble(sumZL, 3, 3)); // 合计：重量
								txtSUMTJ.setText(ChangeDouble(sumTJ, 4, 4)); // 合计：体积

								adapter.addAll(depotDataList);
								adapter.notifyDataSetChanged();
								// add by yxq 2014/10/10 begin
								txtJS.setFocusable(true);
								txtJS.setFocusableInTouchMode(true);
								txtJS.requestFocus();
								// add by yxq 2014/10/10 end
							} else {
								horHJ.setVisibility(View.GONE);
							}
						}
					}
				}, true);
	}

	/*
	 * 进仓编号不存在，同行编号存在的场合
	 * DepotDN001
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

							// edit by yxq 2014/08/29 begin
						    // 原代码 lblFlgInStore.setText(depot.getFlgInStore());
							lblFlgInStore.setText(depot.getContainerStatusNm());
							setStatusColor(depot.getContainerStatus());   // 设置字体颜色
							// edit by yxq 2014/08/29 end
							bolFlgInStore = depot.isBolflgInStore();
							btn_Add.setEnabled(bolFlgInStore);

							bolRole = depot.isBolRole(); // 权限

							if (depot.getDn001List() != null
									&& depot.getDn001List().size() > 0) {

								horHJ.setVisibility(View.VISIBLE);

								sumJS = 0;
								sumZL = 0;
								sumTJ = 0;

								for (DN001ListData info : depot.getDn001List()) {
									sumJS += info.getSumNum();
									sumZL += info.getSumKgs();
									sumTJ += info.getSumCbm();
								}

								txtHJ.setVisibility(View.VISIBLE);
								txtSUMJS.setVisibility(View.VISIBLE);
								txtSUMZL.setVisibility(View.VISIBLE);
								txtSUMTJ.setVisibility(View.VISIBLE);

								txtSUMJS.setText(String.valueOf(sumJS)); // 合计：件数
								txtSUMZL.setText(ChangeDouble(sumZL, 3, 3)); // 合计：重量
								txtSUMTJ.setText(ChangeDouble(sumTJ, 4, 4)); // 合计：体积

								adapter.addAll(depot.getDn001List());
								adapter.notifyDataSetChanged();
								// add by yxq 2014/10/10 begin
								txtJS.setFocusable(true);
								txtJS.setFocusableInTouchMode(true);
								txtJS.requestFocus();
								// add by yxq 2014/10/10 end
							} else {
								horHJ.setVisibility(View.GONE);
							}
						}
					}
				}, true);
	}
	
	/*
	 * 页面数据显示
	 */
	private void ShowData(DepotDN001 data) {
		bolRole = data.isBolRole(); // 权限
		cdOrder = data.getCdOrder(); // 进仓编号 共通
		editOrder.setText(data.getCdOrderPublic());// 进仓编号
		editThNo.setText(data.getCoLoader()); // 同行编号
		txtLineType.setText(data.getNmAirlines()); // 航线
		txtBusinessType.setText(data.getOrderType()); // 业务类型
		//txtOrderType.setText(data.getCdOrderType()); // 订单类型
		if(!data.isFlgGuarYes()){
		    guarExist.setText(R.string.DN001_GUAR_NG);
		    guarExist.setTextColor(Color.RED);
		}else{
			guarExist.setText(R.string.DN001_GUAR_OK);
			guarExist.setTextColor(Color.BLUE);
		}
		txtStatus.setText(data.getStsPacking()); // 装箱状态
		txtCustomerLevel.setText(data.getRank()); // 客户级别
		//一般化工品
		if (data.getFlgCommChemical() == 0){
			txtFlgCommChemical.setText("");
			btn_ShowMSDS.setVisibility(View.GONE);
		}
		else{
			txtFlgCommChemical.setText(R.string.DN001_FlgCommChemical);
			txtFlgCommChemical.setTextColor(Color.RED);
			btn_ShowMSDS.setVisibility(View.VISIBLE);
			//是否显示MSDS
			if (data.isFlgShowMSDS()) {
				this.jjn = data.getJjn();
				this.fqn = data.getFqn();
				this.cczyn = data.getCczyn();
				this.ccczn = data.getCcczn();
			}
			else {
				this.jjn = "";
				this.fqn = "";
				this.cczyn = getString(R.string.DN001_CommMSDS);
				this.ccczn = "";
			}
		}
		
		
		
		// edit by yxq 2014/09/01 begin -- modify by sdhuang 20140923
		// 原代码 txtSpecial.setText(data.getSpecial()); // 特殊保障客户
		txtSpecial.setText(""); 
		for(int n = 0;n < specialCustomers.length;n++){
			if(data.getCdCust().equals(specialCustomers[n])){
		        txtSpecial.setText(data.getNmCustAbbr());     // 客户简称
		        break;
			}
		}
		// edit by yxq 2014/09/01 end
		txtJS.setText(data.getQtNum()); // 件数
		txtZL.setText(data.getQtKgs()); // 重量
		txtTJ.setText(data.getQtCbm()); // 体积
		txtGK.setText(data.getNmWpod()); // 港口
		txtBZ.setText(data.getmPackage()); // 包装
		txtJHY.setText(data.getNmPlanner()); // 计划员
		txtMT.setText(data.getMarks()); // 唛头
		txtRemark.setText(data.getRemark()); // 进仓备注
		//如果是KARACHI，设置为红色
		if (data.getNmWpod().trim().equalsIgnoreCase("KARACHI")){
			txtGK.setTextColor(Color.RED);
		}
	}
	
	/**
	 * change double to string
	 * @param d
	 * @param scale
	 * @param dLen
	 * @return
	 */
	private String ChangeDouble(double d, int scale, int dLen) {
		BigDecimal b = new BigDecimal(d);
		double f = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(dLen);
		return nf.format(f);
	}

	/*
	 * 列举理货批次的列表信息
	 */
	private void GetListView() {
		adapter = new ArrayAdapter(getApplicationContext(), 0) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final LinearLayout layout = new LinearLayout(getContext());
				DN001ListData item = (DN001ListData) getItem(position);
				mInflater.inflate(R.layout.dn001_list_view_item, layout, true);

				TextView t1 = (TextView) layout.findViewById(R.id.lblPCContent);
				t1.setText(item.getNoBatch()); //
				t1 = (TextView) layout.findViewById(R.id.lblUserContent);
				t1.setText(item.getNmWorker());
				t1 = (TextView) layout.findViewById(R.id.lblJSContent);
				t1.setText(String.valueOf(item.getSumNum())); //
				t1 = (TextView) layout.findViewById(R.id.lblZLContent);
				t1.setText(ChangeDouble(item.getSumKgs(), 3, 3)); //
				t1 = (TextView) layout.findViewById(R.id.lblTJContent);
				t1.setText(ChangeDouble(item.getSumCbm(), 4, 4)); //
				t1 = (TextView) layout.findViewById(R.id.lblNMStatus);
				if (item.getStatus()) {
					t1.setText(getString(R.string.DN001_TALLYEND_BTN)); //
				} else {
					t1.setText(getString(R.string.DN001_TALLYING)); //
				}

				int[] colors = { R.color.listview_back_odd,
						R.color.listview_back_uneven };
				layout.setBackgroundResource(colors[position % 2]);

				try {
					listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int pos, long id) {
							final DN001ListData data = (DN001ListData) parent
									.getItemAtPosition(pos);
							// 状态为理货中
							if (!data.getStatus()) {
								// 理货员为登录人
								if (data.isBolIsTally() || bolRole) {
									// 0:理货完成 1：打印本批次桩脚牌
									selItem = new String[] {
											getString(R.string.DN001_TALLYEND_BTN),
											getString(R.string.DN001_PRINT_NOBATCH) };
								} else {
									// 0:打印本批次桩脚牌
									selItem = new String[] { getString(R.string.DN001_PRINT_NOBATCH) };
								}
							} else {
								// 有重新理货权限
								if (bolRole) {
									// 0:打印进仓确认书 1:重新理货 2:打印本批次桩脚牌
									selItem = new String[] {
											getString(R.string.DN001_PRINT_BTN),
											getString(R.string.DN001_REVTALLYEND_BTN),
											getString(R.string.DN001_PRINT_NOBATCH) };
								} else { // 没有重新理货权限
									// 理货员为登录人时可以重新理货
									if (data.isBolIsTally()) {
										// 0:打印进仓确认书 1:重新理货 2:打印本批次桩脚牌
										selItem = new String[] {
												getString(R.string.DN001_PRINT_BTN),
												getString(R.string.DN001_REVTALLYEND_BTN),
												getString(R.string.DN001_PRINT_NOBATCH) };
									} else {
										// 理货员不是登录人时不能重新理货 0:打印进仓确认书 1:打印本批次桩脚牌
										selItem = new String[] {
												getString(R.string.DN001_PRINT_BTN),
												getString(R.string.DN001_PRINT_NOBATCH) };
									}
								}
							}

							DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0: // 理货完成,打印进仓确认书
										if (selItem[0]
												.equals(getString(R.string.DN001_PRINT_NOBATCH))) {
											// 打印本批次桩脚牌
											PrintPileCardReportByNoBatch(data
													.getNoBatch());
										} else {
											TallyEndOrPrint(data.getStatus(),
													data.getNoBatch());
										}
										break;
									case 1:
										if (selItem[1]
												.equals(getString(R.string.DN001_REVTALLYEND_BTN))) {
											// 重新理货
											TallyAgain(data.getNoBatch());
										} else {
											// 打印本批次桩脚牌
											PrintPileCardReportByNoBatch(data
													.getNoBatch());
										}
										break;
									case 2: // 打印本批次桩脚牌
										PrintPileCardReportByNoBatch(data
												.getNoBatch());
										break;
									default:
										break;
									}
								}
							};

							new AlertDialog.Builder(_thisActivity).setItems(
									selItem, lis).show();
							return true;
						}
					});

					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// 获得选中项的HashMap对象
							DN001ListData map = (DN001ListData) listView
									.getItemAtPosition(arg2);

							Intent intent = new Intent(_thisActivity,
									DN002Activity.class);
							Bundle bundle = new Bundle();
							bundle.putBoolean("btnFlag", btnFlag);
							bundle.putString("AddType", "1"); // 编辑批次
							bundle.putBoolean("bolFlgInStore", bolFlgInStore);
							bundle.putString("cdOrder", cdOrder);
							bundle.putString("cdOrderPublic", editOrder
									.getText().toString());
							bundle.putString("depotID", depotID);
							bundle.putString("thNo", editThNo.getText()
									.toString());
							bundle.putString("noBatch", map.getNoBatch());
							bundle.putBoolean("bolRole", bolRole);
							bundle.putBoolean("bolIsTally", map.isBolIsTally());
							// add by yxq 2014/08/21 begin
							bundle.putString("txtMT",txtMT.getText().toString()); // 唛头
							// add by yxq 2014/08/21 end
							bundle.putString("txtGK", txtGK.getText().toString()); // 港口
							intent.putExtras(bundle);
							startActivityForResult(intent, 4);
						}
					});
				} catch (Exception e) {
					try {
						throw e;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				return layout;
			}
		};

		listView.setAdapter(adapter);
	}

	/*
	 * 批量打印桩脚牌
	 */
	private void PrintPileCardReportByNoBatch(String noBatch) {
		try {
			
			Map<String, Object> p1 = new HashMap<String, Object>();
			p1.put("depotId", depotID);
			p1.put("noBatch", noBatch);
			p1.put("cdOrder", cdOrder);
			p1.put("cdOrderPublic", editOrder.getText().toString());
			p1.put("coLoader", editThNo.getText().toString().trim());

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"DN001_PrintPileCardReportByNoBatch", p1,
					new TypeToken<DepotDN001>() {
					}.getType(), new AmassHttpResponseHandler<DepotDN001>() {

						@Override
						protected void OnSuccess(DepotDN001 response) {
							super.OnSuccess(response);
							//打印成功
							Utils.showAlertDialog(
									_thisActivity,
									getString(R.string.msg_common_print_success));
						}

						@Override
						protected void onFailure(int statusCode, Header[] headers,
								String responseBody, Throwable e) {
						}
					}, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	/*
	 * 异常报告按钮事件
	 */
	public void Btn_Error_OnClick(View view) {
		Intent intent = new Intent(_thisActivity, DN003Activity.class);
		Bundle bundle = new Bundle();
		bundle.putString("cdOrderPublic", editOrder.getText().toString());
		bundle.putString("cdOrder", cdOrder);
		bundle.putString("depotID", depotID);
		bundle.putString("thNo", editThNo.getText().toString());
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	/*
	 * 一般化工品操作要求
	 */
	public void Btn_ShowMSDS_OnClick(View view){
		Intent intent = new Intent(_thisActivity, DN008Activity.class);
		Bundle bundle = new Bundle();
		bundle.putString("jjn", jjn);
		bundle.putString("fqn", fqn);
		bundle.putString("cczyn", cczyn);
		bundle.putString("ccczn", ccczn);
		intent.putExtras(bundle);
		startActivity(intent);	
	}

	/*
	 * 新增批次按钮事件
	 */
	public void Btn_Add_OnClick(View view) {
		GetMaxNoBatch();
	}

	/*
	 * 获取最大批次
	 */
	private void GetMaxNoBatch() {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotID", depotID);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN001_GetMaxNoBatchBydepotID", p1,
				new TypeToken<DN001ListData>() {
				}.getType(), new AmassHttpResponseHandler<DN001ListData>() {

					@Override
					protected void OnSuccess(DN001ListData response) {
						super.OnSuccess(response);
						// 获取数据 List<DN001ListData> depotDataList
						DN001ListData depot = (DN001ListData) response;
						// 判断是否有数据
						if (depot != null) {
							Intent intent = new Intent(_thisActivity,
									DN002Activity.class);
							Bundle bundle = new Bundle();
							bundle.putBoolean("btnFlag", btnFlag);
							bundle.putString("AddType", "0"); // 新增批次
							bundle.putBoolean("bolFlgInStore", bolFlgInStore);
							bundle.putString("cdOrder", cdOrder);
							bundle.putString("cdOrderPublic", editOrder
									.getText().toString());
							bundle.putString("depotID", depotID);
							bundle.putString("thNo", editThNo.getText()
									.toString());
							bundle.putString("noBatch", depot.getNoBatch()); // 该进仓编号对应的最大批次+1
							bundle.putBoolean("bolRole", true);
							bundle.putBoolean("bolIsTally", true);
							// add by yxq 2014/08/21 begin
							bundle.putString("txtMT",txtMT.getText().toString()); // 唛头
							// add by yxq 2014/08/21 end
							bundle.putString("txtGK", txtGK.getText().toString()); // 港口
							intent.putExtras(bundle);
							startActivityForResult(intent, 3);
						}
					}
				}, true);
	}

	/*
	 * 理货完成按钮事件
	 */
	public void TallyEndOrPrint(boolean flag, final String strNoBatch) {
		// 理货完成
		if (!flag) {
			// 弹出确认框
			new AlertDialog.Builder(new ContextThemeWrapper(this,
					android.R.style.Theme_Holo_Light))
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.app_name)
					.setCancelable(false)
					.setMessage(getString(R.string.DN001_003_MSG))
					.setPositiveButton(getString(R.string.button_ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// 更新理货状态
									DoUpData(true, strNoBatch);
								}
							})
					.setNegativeButton(getString(R.string.button_no), null)
					.show();
		} else {
			// 打印进仓确认书
			DoPrint(strNoBatch);
		}
	}

	/*
	 * 重新理货dialog
	 */
	private void TallyAgain(final String strNoBatch) {
		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(getString(R.string.DN001_004_MSG))
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// 更新理货状态
								DoUpData(false, strNoBatch);
							}
						})
				.setNegativeButton(getString(R.string.button_no), null).show();
	}

	/*
	 * 更新理货状态
	 */
	private void DoUpData(boolean flag, String strNoBatch) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotId", depotID);
		p1.put("noBatch", strNoBatch);//批次号
		p1.put("status", flag);

		NetworkHelper.getInstance().postJsonData(this, "DN001_DoUpdate", p1,
				CommonResult.class, new AmassHttpResponseHandler<CommonResult>() {

					@Override
					protected void OnSuccess(CommonResult response) {
						super.OnSuccess(response);
						CommonResult result = (CommonResult) response;
						if(result.getFlag()){
							adapter.clear();
							if (selFlag == 1) {
								GetDepotByCdOrderAndDepotID(cdOrder, true);
							} else if (selFlag == 2) {
								GetDepotByDepotID(true);
							}
						}else{
							if("001".equals(result.getErrorCd())){
		    					Utils.showAlertDialogIntMsg(_thisActivity,R.string.DN001_007_MSG);
		    				}else if("002".equals(result.getErrorCd())){
		    					Utils.showAlertDialogIntMsg(_thisActivity,R.string.DN001_008_MSG);
		    				}else {
		    					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
		    				}
						}
					}
				}, true);
	}

	/*
	 * 打印进仓确认书按钮事件
	 */
	private void DoPrint(String noBatch) {
		try {
			Map<String, Object> p1 = new HashMap<String, Object>();
			p1.put("depotID", depotID);
			p1.put("noBatch", noBatch);

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"DN001_PrintGoodsConfirmation", p1,
					new TypeToken<DepotDN001>() {
					}.getType(), new AmassHttpResponseHandler<DepotDN001>() {

						@Override
						protected void OnSuccess(DepotDN001 response) {
							super.OnSuccess(response);
							Utils.showAlertDialog(
									_thisActivity,
									getString(R.string.msg_common_print_success));
						}
					}, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 展开或者收起 */
	public void Btn_collapse_expand_OnClick(View view) {

		View toolbar = findViewById(R.id.linMain);
		if (meunShow) {
			((LinearLayout.LayoutParams) toolbar.getLayoutParams()).topMargin = -linMain
					.getHeight();
			toolbar.setVisibility(View.GONE);
			
			imgBtn.setImageResource(R.drawable.collapse);
			meunShow = false;
			tvOprName.setText(getString(R.string.status_up));

		} else {
			((LinearLayout.LayoutParams) toolbar.getLayoutParams()).topMargin = 0;
			toolbar.setVisibility(View.GONE);
			
			// 这里是TranslateAnimation动画
			imgBtn.setImageResource(R.drawable.expand);
			meunShow = true;
			tvOprName.setText(getString(R.string.status_down));
		}

		// Creating the expand animation for the item
		ExpandAnimation expandAni = new ExpandAnimation(toolbar, 500);
		// Start the animation on the toolbar
		toolbar.startAnimation(expandAni);
	}
     
	// add by yxq 2014/08/29 begin
	// 设置集装箱状态的颜色
	public void setStatusColor(String status){
		double douStatus = 0;
		if(status!=null){
			try{
				douStatus = Double.parseDouble(status.trim());
			}catch(Exception e){
				douStatus = 0;
			}
		}
		if(douStatus > 8 || douStatus == 7.7||douStatus == 7.4 || douStatus == 8){
			lblFlgInStore.setTextColor(0xFF00AE55);   // 绿色
		}else{
			lblFlgInStore.setTextColor(Color.BLACK);
		}
	}
	
	
	// 无用代码
	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {

	}
}
