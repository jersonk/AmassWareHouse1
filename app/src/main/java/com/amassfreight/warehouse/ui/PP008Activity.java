package com.amassfreight.warehouse.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode05;

import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.FileManageData;

import com.amassfreight.domain.PP008FclData;
import com.amassfreight.domain.PP008ResponseData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.google.zxing.client.android.CaptureActivity;

public class PP008Activity extends BaseActivity {

	private Boolean bolIsEit = true; // 判断哪个页面跳转：一览画面 false；拣货 true；

	private TableLayout tabLayout;
	private TextView tvno_box;
	private TextView tvsts_packing;
	private TextView tvdt_packing_date;
	private TextView tvop_wh_remarks;
	private TextView tvTotalCounts;
	private TextView tvTotalWeigth;
	private TextView tvTotalVolume;
	private TextView tvRealTotalCounts;

	private String strNoBox;
	private Boolean bolIsFclOrScl; // True:整箱； False:自拼箱
	private ListView listPP008;
	private ListView listPP008File;
	private LayoutInflater mInflater;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter pp008Adapter;
	private FileDataAdapter pp008FileAdapter;

	private LinearLayout linMain;

	private Boolean meunShow = false;
	private ImageButton imgBtn;
	private TextView tvOprName; // 计划备注
	private LinearLayout imgLiner;

	private TextView tvTitleNoBox;// 集箱号
	private TextView tvDetail;
	protected Boolean isFclOrScl() {
		return true;
	}

	protected Boolean isEdit() {
		return false;
	}

	protected String noBox() {
		return "";
	}

	/** 初始化事件 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// 根据不同传值类型
		bolIsFclOrScl = isFclOrScl();
		bolIsEit = isEdit();

		// 根据页面的类型不同，设置显示的标题
		if (bolIsFclOrScl) {
			setTitle(getString(R.string.PP008_TITLE_NAME_FCL));
		} else {
			setTitle(getString(R.string.PP008_TITLE_NAME_SCL));
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp008_fclpick);

		// 设置标题栏是左边是否有返回事件
		setupActionBar();

		// 控件初始化
		tvno_box = (TextView) findViewById(R.id.tvno_box); // 集箱号
		tvsts_packing = (TextView) findViewById(R.id.tvsts_packing);// 装箱状态
		tvdt_packing_date = (TextView) findViewById(R.id.tvdt_packing_date);// 最晚完成时间
		tvop_wh_remarks = (TextView) findViewById(R.id.tvop_wh_remarks);// 装箱规定
		// 溢出滚动
		tvop_wh_remarks
				.setMovementMethod(ScrollingMovementMethod.getInstance());

		tvTotalCounts = (TextView) findViewById(R.id.tvTotalCounts);// 总Packages
		tvTotalWeigth = (TextView) findViewById(R.id.tvTotalWeigth);// 总KGS
		tvTotalVolume = (TextView) findViewById(R.id.tvTotalVolume);// 总CBM
		tvRealTotalCounts = (TextView) findViewById(R.id.tvRealTotalCounts);// 实装总件数

		// 获取合计布局，初始化隐藏
		tabLayout = (TableLayout) findViewById(R.id.tabLayout);
		tabLayout.setVisibility(View.GONE);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		listPP008 = (ListView) findViewById(R.id.listPP008View);

		listPP008File = (ListView) findViewById(R.id.listFileList);

		tvOprName = (TextView) findViewById(R.id.tvOPrName);
		linMain = (LinearLayout) findViewById(R.id.linMain);
		imgBtn = (ImageButton) findViewById(R.id.imgBtn);

		imgLiner = (LinearLayout) findViewById(R.id.imgLiner);
		imgLiner.setVisibility(View.GONE);

		tvTitleNoBox = (TextView) findViewById(R.id.tvTitleNoBox);
		// 明细跳转按钮
				tvDetail = (TextView) findViewById(R.id.tvDetail);
				tvDetail.setVisibility(View.GONE);
				tvDetail.getPaint().setFlags(
						Paint.UNDERLINE_TEXT_FLAG);// 下划线
				tvDetail.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(PP008Activity.this,
								PP010Activity_Depot.class);
						Bundle bundle = new Bundle();
						bundle.putString("noBox", strNoBox);
						if (bolIsFclOrScl) {
							bundle.putString("boxType", "2");
						} else {
							bundle.putString("boxType", "3");
						}
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
		// 适配器
		pp008Adapter = new ArrayAdapter(getApplicationContext(), 0) {
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout layout = new LinearLayout(getContext());
				PP008FclData data = (PP008FclData) getItem(position);
				try {
					mInflater.inflate(R.layout.activity_pp008_fckpick_item,
							layout, true);

					if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}

					TextView tvCoder = (TextView) layout
							.findViewById(R.id.tvcoder);
					tvCoder.setText(data.getCdBusinessPublic());// 进仓编号
					tvCoder.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
					tvCoder.setTextColor(Color.BLUE);
					final int proPosition = position;
					// 进仓编号的监听事件
					tvCoder.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							PP008FclData data = (PP008FclData) getItem(proPosition);
							Intent intent = new Intent(PP008Activity.this,
									PP009Activity.class);
							Bundle bundle = new Bundle();
							bundle.putString("cdOrder", data.getCdBusiness());
							bundle.putBoolean("isFclOrScl", bolIsFclOrScl);
							bundle.putString("noBox", strNoBox);
							bundle.putBoolean("IsEdit", bolIsEit);
							bundle.putString("depotDtId", "");
							bundle.putString("cdBooking", data.getCdBooking());
							bundle.putBoolean("isPost", true);
							intent.putExtras(bundle);
							startActivityForResult(intent, 2);

						}
					});
					TextView t1 = (TextView) layout
							.findViewById(R.id.tvdropCounts);
					t1.setText(String.valueOf(data.getDropCounts()));// 预配件数

					t1 = (TextView) layout.findViewById(R.id.tvcdbooking);
					t1.setText(data.getCdBooking());// 关单号

					TextView t2 = (TextView) layout
							.findViewById(R.id.tvRCounts);
					t2.setText(String.valueOf(data.getRealCounts()));// 实装件数

					if (bolIsFclOrScl) {

						// 品名，唛头
						LinearLayout lineScLayout = (LinearLayout) layout
								.findViewById(R.id.lineSclRemark);
						lineScLayout.setVisibility(View.GONE);

						// 整箱要求
						lineScLayout = (LinearLayout) layout
								.findViewById(R.id.lineFclRemark);
						lineScLayout.setVisibility(View.VISIBLE);

						t1 = (TextView) layout.findViewById(R.id.tvremark);
						t1.setText(data.getFclRemark());// 装箱要求

					}

					else {

						// 品名，唛头
						LinearLayout lineScLayout = (LinearLayout) layout
								.findViewById(R.id.lineSclRemark);
						lineScLayout.setVisibility(View.VISIBLE);

						// 整箱要求
						lineScLayout = (LinearLayout) layout
								.findViewById(R.id.lineFclRemark);
						lineScLayout.setVisibility(View.GONE);

						t1 = (TextView) layout.findViewById(R.id.tvGoodsEng);
						t1.setText(data.getGoodsEng());// 品名

						t1 = (TextView) layout.findViewById(R.id.tvMark);
						t1.setText(data.getMark());// 唛头

					}
				} catch (Exception e) {
					try {
						throw e;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				return layout;
			}
		};

		listPP008.setAdapter(pp008Adapter);

		// 集箱号监听事件
		tvno_box.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!strNoBox.isEmpty()) {
					Intent intent = new Intent(PP008Activity.this,
							PP003Activity.class);
					intent.putExtra("boxNo", strNoBox);
					startActivity(intent);

				}
			}
		});

		if (bolIsEit) {
			strNoBox = "";
		} else {
			strNoBox = noBox();
			InitData();
		}

		//
		listPP008.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				PP008FclData data = (PP008FclData) listPP008
						.getItemAtPosition(arg2);
				Intent intent = new Intent(PP008Activity.this,
						PP009Activity.class);
				Bundle bundle = new Bundle();
				bundle.putString("cdOrder", data.getCdBusiness());
				bundle.putBoolean("isFclOrScl", bolIsFclOrScl);
				bundle.putString("noBox", strNoBox);
				bundle.putBoolean("IsEdit", bolIsEit);
				bundle.putString("depotDtId", "");
				bundle.putString("cdBooking", data.getCdBooking());
				bundle.putBoolean("isPost", true);
				intent.putExtras(bundle);
				startActivityForResult(intent, 2);

			}
		});
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
				final BarCode05 barResult05 = new BarCode05();
				boolean result05 = barResult05.paserBarCode(data
						.getStringExtra("SCAN_RESULT"));

				// 首次扫描：集箱号；其他这是桩脚牌
				/* if (strNoBox.isEmpty()) { */
				// 判断是否是集箱号
				if (result05) {

					strNoBox = barResult05.getBoxNo();

					InitData();
				} else {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.PP008_DIALOG_NOBOX));
				}
				/* } */

				/*
				 * else { // 判断是否是集箱号 if (result05) {
				 * 
				 * // TODO F13000068 strNoBox = barResult05.getBoxNo();
				 * 
				 * InitData(); } else { final BarCode02 barResult = new
				 * BarCode02(); boolean result02 = barResult.paserBarCode(data
				 * .getStringExtra("SCAN_RESULT")); // 判断是否是货物明显ID if (result02)
				 * { final String strOrderId = barResult.getOrderCd(); final
				 * String strDepotId = barResult.getDepotDtId(); Map<String,
				 * Object> pp008Map = new HashMap<String, Object>();
				 * pp008Map.put("strNoBox", strNoBox);// 集箱号
				 * pp008Map.put("strDepotId", strDepotId);// 货物明显ID
				 * 
				 * NetworkHelper.getInstance().postJsonData( _thisActivity,
				 * "PP008_GetTDcDetailByDepot", pp008Map, Integer.class, new
				 * AmassHttpResponseHandler<Integer>() {
				 * 
				 * @Override protected void OnSuccess( Integer response) {
				 * super.OnSuccess(response); // 获取对应的数据 Integer numRes =
				 * (Integer) response;
				 * 
				 * if (numRes > 0) { Intent intent = new Intent(
				 * PP008Activity.this, PP009Activity.class); Bundle bundle = new
				 * Bundle(); bundle.putString("cdOrder", strOrderId);
				 * bundle.putString("depotDtId", strDepotId);
				 * bundle.putBoolean("isFclOrScl", bolIsFclOrScl);
				 * bundle.putString("noBox", strNoBox);
				 * bundle.putBoolean("IsEdit", bolIsEit);
				 * bundle.putBoolean("isPost", true); intent.putExtras(bundle);
				 * startActivityForResult(intent, 2); } else {
				 * Utils.showAlertDialog( _thisActivity,
				 * getString(R.string.PP008_DIALOG_SCAN)); } } }, true); } else
				 * { Utils.showAlertDialog(_thisActivity,
				 * getString(R.string.PP008_DIALOG_DEOPT)); } } }
				 */

			} else {
				// 失败提示
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.PP005_msg_scanError));
			}
			break;
		case 2:
			if (resultCode == RESULT_OK) {
				InitData();
			}
			break;
		}
	}

	public void InitData() {
		Map<String, Object> pp008Map = new HashMap<String, Object>();
		pp008Map.put("strNoBox", strNoBox);// 集箱号
		pp008Map.put("bolIsFclOrScl", bolIsFclOrScl);// 是否是整箱或者自拼箱
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"PP008_GetContainer", pp008Map, PP008ResponseData.class,
				new AmassHttpResponseHandler<PP008ResponseData>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(PP008ResponseData response) {
						super.OnSuccess(response);
						if (response != null) {
							// 获取对应的数据
							PP008ResponseData pp008Data = (PP008ResponseData) response;
							if (!pp008Data.isFlgLockContainer()) {
								Utils.showAlertDialog(_thisActivity,
										getString(R.string.PP008_DIALOG_SCAN));
							} else {
								// 合计显示
								tabLayout.setVisibility(View.VISIBLE);

								// 明细
								tvDetail.setVisibility(View.VISIBLE);
								// 控件赋值

								tvTitleNoBox.setText(pp008Data.getNoBox()); // 集箱号

								tvno_box.setText(pp008Data.getNoBox()); // 集箱号

								tvno_box.getPaint().setFlags(
										Paint.UNDERLINE_TEXT_FLAG);// 下划线
								tvno_box.setTextColor(Color.BLUE);
								tvsts_packing.setText(pp008Data.getStspacking());// 装箱状态
								if (getString(
										R.string.status_packing_picking_doing)
										.equals(pp008Data.getStspacking())
										|| getString(
												R.string.status_packing_doing)
												.equals(pp008Data
														.getStspacking())) {
									tvsts_packing
											.setBackgroundColor(Color.BLUE);
									tvsts_packing.setTextColor(Color.WHITE);
								} else if (getString(
										R.string.status_packing_done).equals(
										pp008Data.getStspacking())) {
									tvsts_packing
											.setBackgroundColor(Color.GRAY);
									tvsts_packing.setTextColor(Color.BLACK);
								} else {
									tvsts_packing
											.setBackgroundColor(Color.TRANSPARENT);

									tvsts_packing.setTextColor(Color.BLACK);
								}
								tvdt_packing_date.setText(pp008Data
										.getDtPackingDeadline());// 最晚完成时间
								tvop_wh_remarks.setText(pp008Data
										.getOpWhRemark());// 装箱规定
								tvTotalCounts.setText(String.valueOf(pp008Data
										.getTotalCounts()));// 总Packages
								tvTotalWeigth.setText(String.valueOf(pp008Data
										.getTotalWeight()));// 总KGS
								tvTotalVolume.setText(String.valueOf(pp008Data
										.getTotalVolume()));// 总CBM
								tvRealTotalCounts.setText(String
										.valueOf(pp008Data.getTotalRealCounts()));// 实装总数量

								imgLiner.setVisibility(View.VISIBLE);

								// 明细数据
								List<PP008FclData> ppList = (List<PP008FclData>) pp008Data
										.getPp008FclList();
								pp008Adapter.clear();
								if (ppList != null) {
									pp008Adapter.addAll(ppList);
									pp008Adapter.notifyDataSetChanged();
								}
								List<FileManageData> fileList = (List<FileManageData>) pp008Data
										.getFileList();

								pp008FileAdapter = new FileDataAdapter(
										_thisActivity, fileList);
								listPP008File.setAdapter(pp008FileAdapter);

							}
						} else {
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.PP008_DIALOG_SCAN));
						}
					}

				}, true);
	}

	/*
	 * 返回事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 按下的如果是BACK，同时没有重复

			this.finish();

		}
		return super.onKeyDown(keyCode, event);
	}

}
