package com.amassfreight.warehouse.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.method.ScrollingMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.domain.PP005StatusData;
import com.amassfreight.domain.PP009DepotData;
import com.amassfreight.domain.PP009ResponseData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.google.zxing.client.android.CaptureActivity;

public class PP009Activity extends BaseActivity {

	private Boolean bolIsEit = true; // 判断哪个页面跳转：一览画面 false；拣货 true；
	private Boolean bolIsFclOrScl; // True:整箱； False:自拼箱
	private String strNoBox; // 集箱号
	private String strCdOrdr; // 进仓编号

	private TextView tvCdOrder; // 进仓编号
	private TextView tvFlgQuit; // 是否退关
	private TextView tvQtNum; // 原件数
	private TextView tvWeight; // kgs
	private TextView tvCounts; // 本箱件数
	private TextView tvValume; // CMB
	private TextView tvCdBooking; // 关单号
	private TextView tvPlanRemarks; // 计划备注

	private ListView listPP009;
	private ListView listPP009File;
	private LayoutInflater mInflater;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter pp009Adapter;
	private FileDataAdapter pp009FileAdapter;
	private List<PP009DepotData> ppList;
	private String strDepotId;
	private String strFlgPicking;
	private Boolean bolIsPost;
	private String strNoBotch;
	private String strCdBooking;
	private LinearLayout linMain;

	private Boolean meunShow = false;
	private ImageButton imgBtn;
	private TextView tvOprName; // 计划备注
	private TextView tvTitleCdOrder; // 进仓编号

	/** 初始化事件 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Bundle bundle = this.getIntent().getExtras();

		strFlgPicking = "";
		strNoBotch = "";
		bolIsPost = false;
		strCdBooking = null;
		// 判断页面传值是否为空
		if (bundle != null) {
			// 根据不同传值类型
			bolIsEit = (Boolean) bundle.get("IsEdit"); // 判断扫描功能是否使用
			bolIsFclOrScl = (Boolean) bundle.get("isFclOrScl");// 判断是否为整箱或者自拼箱
			strNoBox = bundle.get("noBox").toString();// 集箱号
			strCdOrdr = bundle.get("cdOrder").toString(); // 进仓编号
			strDepotId = bundle.get("depotDtId").toString();
			bolIsPost = (Boolean) bundle.get("isPost");// 判断是否为整箱或者自拼箱
			strCdBooking = bundle.get("cdBooking").toString();// 关单号
		} else {
			bolIsEit = false;
		}
		// 根据页面的类型不同，设置显示的标题
		if (bolIsFclOrScl) {
			setTitle(getString(R.string.PP009_TITLE_NAME_FCL));
		} else {
			setTitle(getString(R.string.PP009_TITLE_NAME_SCL));
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp009_pick_detail);
		// 设置标题栏是左边是否有返回事件
		setupActionBar();

		// 实例化
		tvCdOrder = (TextView) findViewById(R.id.tvCdOrder);
		tvFlgQuit = (TextView) findViewById(R.id.tvFlgQuit);
		tvQtNum = (TextView) findViewById(R.id.tvQtNum);
		tvWeight = (TextView) findViewById(R.id.tvWeight);
		tvCounts = (TextView) findViewById(R.id.tvCounts);
		tvValume = (TextView) findViewById(R.id.tvValume);
		tvPlanRemarks = (TextView) findViewById(R.id.tvPlanRemarks);
		// 溢出滚动
		tvPlanRemarks.setMovementMethod(ScrollingMovementMethod.getInstance());
		tvCdBooking = (TextView) findViewById(R.id.tvCdBooking);

		tvOprName = (TextView) findViewById(R.id.tvOPrName);
		linMain = (LinearLayout) findViewById(R.id.linMain);
		imgBtn = (ImageButton) findViewById(R.id.imgBtn);

		tvTitleCdOrder = (TextView) findViewById(R.id.tvTitleCdOrder);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		listPP009 = (ListView) findViewById(R.id.listPP009View);

		listPP009File = (ListView) findViewById(R.id.fileList);

		// 适配器
		pp009Adapter = new ArrayAdapter(getApplicationContext(), 0) {
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout layout = new LinearLayout(getContext());
				final PP009DepotData data = (PP009DepotData) getItem(position);

				try {
					mInflater.inflate(R.layout.activity_pp009_pick_detail_item,
							layout, true);

					if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}

					TextView tvNoBatch = (TextView) layout
							.findViewById(R.id.tvNoBatch);
					tvNoBatch.setText(data.getNoBatch());// 桩脚牌
					TextView tvQt = (TextView) layout.findViewById(R.id.tvQt);
					tvQt.setText(String.valueOf(data.getOriginalNum()));// 原件数
					TextView tvQz = (TextView) layout.findViewById(R.id.tvQz);
					tvQz.setText(String.valueOf(data.getStockNum()));// 可装件数
					TextView tvReal = (TextView) layout
							.findViewById(R.id.tvReal);
					tvReal.setText(String.valueOf(data.getRealCounts()));// 实际件数
					TextView tvFlgPicking = (TextView) layout
							.findViewById(R.id.tvFlgPicking);
					tvFlgPicking.setText(data.getFlgPicking());// 拣货状态
					TextView tvFlgPacking = (TextView) layout
							.findViewById(R.id.tvFlgPacking);

					tvFlgPacking.setText(data.getFlgPacking());// 装箱状态

					TextView tvPos = (TextView) layout
							.findViewById(R.id.tvPosAndLocation);

					tvPos.setText(data.getPosAndLocation());// 库区库位

					// 判断是否选中
					if (!strDepotId.isEmpty()
							&& strDepotId.equals(data.getDepotId())) {
						layout.setBackgroundResource(R.color.lightBlue);

						strNoBotch = data.getNoBatch();
						if (!strFlgPicking.isEmpty()) {
							data.setFlgPicking(strFlgPicking);// 拣货状态
						}
					}

					if (getString(R.string.status_pick_done).equals(
							data.getFlgPicking())) {
						tvFlgPicking.setBackgroundColor(Color.YELLOW);
					}

					if (getString(R.string.status_packing_doing).equals(
							data.getFlgPacking())) {
						tvFlgPacking.setBackgroundColor(Color.BLUE);
						tvFlgPacking.setTextColor(Color.WHITE);
					} else if (getString(R.string.status_packing_done).equals(
							data.getFlgPacking())) {
						tvFlgPacking.setBackgroundColor(Color.GRAY);
						tvFlgPacking.setTextColor(Color.BLACK);
					} else {
						tvFlgPacking.setTextColor(Color.BLACK);
						tvFlgPacking.setBackgroundColor(Color.TRANSPARENT);
					}

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

		listPP009.setAdapter(pp009Adapter);

		final String[] selItem = new String[] { getString(R.string.button_detail) };

		listPP009.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int pos, long arg3) {

				final PP009DepotData data = (PP009DepotData) arg0
						.getItemAtPosition(pos);

				DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: // 查看
							Intent detailIntent = new Intent(_thisActivity,
									DN004Activity.class);
							String strNoBatch = null; // 批次
							String strnoPileCard = null; // 桩脚牌
							if (data.getNoBatch() != null
									&& data.getNoBatch().split("-").length > 0) {
								strNoBatch = data.getNoBatch().split("-")[0];
								if (data.getNoBatch().split("-").length > 1) {
									strnoPileCard = data.getNoBatch()
											.split("-")[1];
								}
							}

							String strPos = null; // 库区
							String strLocation = null;// 库位
							if (data.getPosAndLocation() != null
									&& data.getPosAndLocation().split("-").length > 0) {
								strPos = data.getPosAndLocation().split("-")[0];
								if (data.getPosAndLocation().split("-").length > 1) {
									strLocation = data.getPosAndLocation()
											.split("-")[1];
								}
							}
							Bundle detailBundle = new Bundle();
							detailBundle.putString("Type", "8"); // 更新标识
							detailBundle.putString("cdOrderPublic",
									data.getCdBusinessPublic()); // 进仓编号共通
							detailBundle.putString("noBatch", strNoBatch);// 批次
							detailBundle.putString("depotDtId",
									data.getDepotId());// 货物明细ID
							detailBundle.putString("noPilecard", strnoPileCard);// 桩脚牌
							detailBundle.putString("pos", strPos);// 库区
							detailBundle.putString("location", strLocation);// 库位
							detailIntent.putExtras(detailBundle);
							startActivity(detailIntent);
							break;

						default:
							break;
						}
					}
				};

				new AlertDialog.Builder(_thisActivity).setItems(selItem, lis)
						.show();
				return true;
			}
		});

		InitData();
		// 页面跳转事件
		if (!strDepotId.isEmpty() && bolIsPost) {
			OpScanResult(strCdOrdr, strDepotId);
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
			tvOprName.setText("收起");

		} else {
			((LinearLayout.LayoutParams) toolbar.getLayoutParams()).topMargin = 0;
			toolbar.setVisibility(View.GONE);
			// 这里是TranslateAnimation动画

			imgBtn.setImageResource(R.drawable.expand);
			meunShow = true;
			tvOprName.setText("展开");
		}

		// Creating the expand animation for the item
		ExpandAnimation expandAni = new ExpandAnimation(toolbar, 500);
		// Start the animation on the toolbar
		toolbar.startAnimation(expandAni);
	}

	/* 页面赋值 */
	public void InitData() {
		Map<String, Object> pp009Map = new HashMap<String, Object>();
		pp009Map.put("IsFclOrScl", bolIsFclOrScl);// 是否是整箱或者自拼箱
		pp009Map.put("strNoBox", strNoBox);// 集箱号
		pp009Map.put("strcdBusiness", strCdOrdr);// 进仓编号
		pp009Map.put("strCdBooking", strCdBooking);// 关单号
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"PP009_GetData", pp009Map, PP009ResponseData.class,
				new AmassHttpResponseHandler<PP009ResponseData>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(PP009ResponseData response) {
						super.OnSuccess(response);
						if (response != null) {

							// 获取对应的数据
							PP009ResponseData pp009Response = (PP009ResponseData) response;
							tvCdOrder.setText(pp009Response
									.getCdBusinessPublic()); // 进仓编号

							tvTitleCdOrder.setText(pp009Response
									.getCdBusinessPublic()); // 进仓编号

							// 是否退关
							if (pp009Response.getFlgQuit() != null
									&& pp009Response.getFlgQuit()) {
								tvFlgQuit.setText("是");
							} else {
								tvFlgQuit.setText("否");
							}
							tvQtNum.setText(String.valueOf(pp009Response
									.getQtNum())); // 原件数
							tvWeight.setText(String.valueOf(pp009Response
									.getWeight())); // 重量
							tvCounts.setText(String.valueOf(pp009Response
									.getCounts())); // 件数
							tvValume.setText(String.valueOf(pp009Response
									.getVolume())); // 体积
							tvPlanRemarks.setText(pp009Response.getRemark()); // 备注

							tvCdBooking.setText(pp009Response.getCdBooking());
							strCdBooking = pp009Response.getCdBooking();
							// 明细数据
							ppList = (List<PP009DepotData>) pp009Response
									.getPp009DepList();
							pp009Adapter.clear();
							if (ppList != null) {
								pp009Adapter.addAll(ppList);
								pp009Adapter.notifyDataSetChanged();
							}
							List<FileManageData> fileList = (List<FileManageData>) pp009Response
									.getFileList();

							pp009FileAdapter = new FileDataAdapter(
									_thisActivity, fileList);
							listPP009File.setAdapter(pp009FileAdapter);
						}
					}
				}, true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 从主菜单的【整箱拣货】或【自拼箱拣货】点击进来的情况下，本按钮可用
		// 从装箱一览迁移过来的情况下，本按钮不可用
		if (bolIsEit) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.common_scan, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan_action:
			Intent intent = new Intent(this, CaptureActivity.class);
			intent.putExtra("SCAN_MODE", "SCAN_MODE");
			startActivityForResult(intent, 1);
			return true;
			/*
			 * case android.R.id.home: setResult(Activity.RESULT_OK);
			 * this.finish(); return true;
			 */
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {

		case 1:
			if (resultCode == RESULT_OK) {
				@SuppressWarnings("unused")
				String barCode = data.getStringExtra("SCAN_RESULT");
				final BarCode02 barResult = new BarCode02();
				boolean result02 = barResult.paserBarCode(data
						.getStringExtra("SCAN_RESULT"));

				// 判断是否是桩脚牌
				if (result02) {
					OpScanResult(barResult.getOrderCd(),
							barResult.getDepotDtId());
				} else {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.PP008_DIALOG_DEOPT));
				}

			}
			break;
		}

	}

	/*
	 * 执行拣货状态变更事件
	 */
	public void OpScanResult(final String cdOrder, final String strDepot) {

		Map<String, Object> pp009Map = new HashMap<String, Object>();
		pp009Map.put("isFclOrScl", bolIsFclOrScl);// 是否为整箱还是自拼箱
		pp009Map.put("strNoBox", strNoBox);// 集箱号
		pp009Map.put("strcdBusiness", cdOrder);// 业务编号
		pp009Map.put("strDepotDtId", strDepot);// 货物明显ID

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"PP009_GetTDcDetailAndUpdateData", pp009Map,
				PP005StatusData.class,
				new AmassHttpResponseHandler<PP005StatusData>() {
					@Override
					protected void OnSuccess(PP005StatusData response) {
						super.OnSuccess(response);
						// 获取对应的数据
						PP005StatusData statusData = (PP005StatusData) response;
						for (int i = 0; i < ppList.size(); i++) {
							if (ppList.get(i).getDepotId().equals(strDepot)) {
								strNoBotch = ppList.get(i).getNoBatch();
							}
						}
						if (statusData.getUpdatePickStatus()) {
							if (cdOrder.equals(strCdOrdr)) {
								strDepotId = strDepot;
								strFlgPicking = getString(R.string.status_pick_done);
								pp009Adapter.notifyDataSetChanged();
							} else {
								/*
								 * strDepotId = strDepot; strFlgPicking = "";
								 * strCdOrdr = cdOrder; InitData();
								 */
								Utils.showAlertDialog(
										_thisActivity,
										getString(R.string.PP009_msg_SCAN_CDORDER));
							}
						} else {
							if ("001".equals(statusData.getErrorCd())) {
								Utils.showAlertDialogIntMsg(_thisActivity,
										R.string.PP005_msg_pickError_001);
							} else if ("005".equals(statusData.getErrorCd())) {
								Utils.showAlertDialog(
										_thisActivity,
										String.format(
												getResources()
														.getString(
																R.string.PP005_msg_pickError_005),
												strNoBotch));

							} else if ("010".equals(statusData.getErrorCd())) {
								Utils.showAlertDialog(_thisActivity,
										getString(R.string.PP005_msg_VasError));

							} else if ("006".equals(statusData.getErrorCd())) {
								Utils.showAlertDialog(
										_thisActivity,
										String.format(
												getResources()
														.getString(
																R.string.PP005_msg_pickError_006),
												strNoBotch));
							} else if ("007".equals(statusData.getErrorCd())) {
								Utils.showAlertDialog(
										_thisActivity,
										String.format(
												getResources()
														.getString(
																R.string.PP005_msg_pickError_007),
												strNoBotch));
							} else if ("003".equals(statusData.getErrorCd())) {
								showDoubleDialog(
										_thisActivity,
										String.format(
												getResources()
														.getString(
																R.string.PP005_msg_pickError_003),
												strNoBotch), 0, cdOrder,
										strDepot);
							} else if ("004".equals(statusData.getErrorCd())) {
								showDoubleDialog(
										_thisActivity,
										String.format(
												getResources()
														.getString(
																R.string.PP005_msg_pickError_004),
												strNoBotch), 0, cdOrder,
										strDepot);

							} else if ("011".equals(statusData.getErrorCd())) {
								Utils.showAlertDialog(
										_thisActivity,
										getString(R.string.COMMON_PICKING_PILECARD));
							}

							else {
								Utils.showAlertDialogIntMsg(_thisActivity,
										R.string.PP005_msg_pickError_unknow);
							}
						}

					}
				}, true);
	}

	public void showDoubleDialog(final Context context, String str,
			final int dialogId, final String strCdOrder, final String strDepot) {
		new AlertDialog.Builder(new ContextThemeWrapper(context,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(str)
				.setPositiveButton(R.string.button_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// DoubleDialog(yes/no)
								actionAfterOk(context, dialogId, 0, strCdOrder,
										strDepot);
							}
						})
				.setCancelable(false)
				.setNegativeButton(R.string.button_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// DoubleDialog(yes/no)
								actionAfterOk(context, dialogId, 1, strCdOrder,
										strDepot);

							}
						}).show();
	}

	/**
	 * DoubleDialog(yes/no)
	 * 
	 * @param dialogId
	 *            “0”代表确定，“1”代表取消
	 */
	public void actionAfterOk(Context context, int dialogId, int i,
			final String cdOrdr, final String strDepot) {

		// dialogId==0 系统MEN选中
		if (dialogId == 0) {
			// 标识
			if (i == 0) {
				Map<String, Object> pp009Map = new HashMap<String, Object>();
				pp009Map.put("IsFclOrScl", bolIsFclOrScl);// 是否为整箱还是自拼箱
				pp009Map.put("strNoBox", strNoBox);// 集箱号
				pp009Map.put("strcdBusiness", cdOrdr);// 业务编号
				pp009Map.put("strDepotDtId", strDepot);// 货物明显ID
				pp009Map.put("strCdBooking", strCdBooking);// 关单号
				NetworkHelper.getInstance().postJsonData(context,
						"PP009_CancelData", pp009Map, PP005StatusData.class,
						new AmassHttpResponseHandler<PP005StatusData>() {
							@Override
							protected void OnSuccess(PP005StatusData response) {
								super.OnSuccess(response);
								// 获取对应的数据
								PP005StatusData statusData = (PP005StatusData) response;

								if (statusData.getUpdatePickStatus()) {
									if (cdOrdr.equals(strCdOrdr)) {
										strDepotId = strDepot;
										strFlgPicking = getString(R.string.status_pick_undo);
										pp009Adapter.notifyDataSetChanged();
									} else {
										strDepotId = strDepot;
										strFlgPicking = "";
										strCdOrdr = cdOrdr;
										InitData();
									}
								} else {
									if ("005".equals(statusData.getErrorCd())) {
										Utils.showAlertDialog(
												_thisActivity,
												String.format(
														getResources()
																.getString(
																		R.string.PP005_msg_pickError_005),
														strNoBotch));

									} else if ("008".equals(statusData
											.getErrorCd())) {
										Utils.showAlertDialog(
												_thisActivity,
												strNoBotch
														+ getString(R.string.PP009_msg_pickError_008));
									} else if ("009".equals(statusData
											.getErrorCd())) {
										Utils.showAlertDialog(
												_thisActivity,
												strNoBotch
														+ getString(R.string.PP009_msg_pickError_009));
									}
								}
							}
						}, true);
			}
		}
	}

	// 刷新
	@SuppressWarnings("unused")
	private void refresh(Bundle bundle) {
		finish();
		Intent intent = new Intent(PP009Activity.this, PP009Activity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/*
	 * 返回事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 按下的如果是BACK，同时没有重复
			setResult(Activity.RESULT_OK);
			this.finish();

		}
		return super.onKeyDown(keyCode, event);
	}

}
