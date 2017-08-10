package com.amassfreight.warehouse.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode04;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.domain.PP005StatusData;
import com.amassfreight.domain.PP006CntScanLock;
import com.amassfreight.domain.PP008FclData;
import com.amassfreight.domain.PP008ResponseData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.google.zxing.client.android.CaptureActivity;

public class PP010Activity extends BaseActivity {
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
	private TextView planner;
	private RelativeLayout relatBtnLayout;

	private String strNoBox;
	private Boolean bolIsFclOrScl; // True:整箱； False:自拼箱
	private ListView listPP010;
	private ListView listPP010File;
	private LayoutInflater mInflater;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter PP010Adapter;
	private FileDataAdapter pp010FileAdapter;

	private Button btnCompulsoryPacking;
	private Button btnCompletePacking;
	private Button btnCompleteCancelPacking;
	private LinearLayout center;
	private String strCdContainer;

	private LinearLayout linMain;

	private Boolean meunShow = false;
	private ImageButton imgBtn;
	private TextView tvOprName; // 计划备注
	private String strOprName;
	private LinearLayout imgLiner;

	private TextView tvTitleNoBox;// 集箱号
	private TextView packPhotoQuery;
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

		@SuppressWarnings("unused")
		Intent intent = getIntent();

		bolIsFclOrScl = isFclOrScl();
		bolIsEit = isEdit();

		// 根据页面的类型不同，设置显示的标题
		if (bolIsFclOrScl) {
			setTitle(getString(R.string.PP010_TITLE_NAME_FCL));
		} else {
			setTitle(getString(R.string.PP010_TITLE_NAME_SCL));
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp010_pack);

		// 设置标题栏是左边是否有返回事件
		setupActionBar();

		strNoBox = "";
		strCdContainer = "";
		// 控件初始化
		tvno_box = (TextView) findViewById(R.id.tvno_box); // 集箱号
		tvsts_packing = (TextView) findViewById(R.id.tvsts_packing);// 装箱状态
		tvdt_packing_date = (TextView) findViewById(R.id.tvdt_packing_date);// 最晚完成时间
		tvop_wh_remarks = (TextView) findViewById(R.id.tvop_wh_remarks);// 装箱规定
		planner = (TextView) findViewById(R.id.planner);// 计划员

		// 溢出滚动
		tvop_wh_remarks
				.setMovementMethod(ScrollingMovementMethod.getInstance());

		tvTotalCounts = (TextView) findViewById(R.id.tvTotalCounts);// 总Packages
		tvTotalWeigth = (TextView) findViewById(R.id.tvTotalWeigth);// 总KGS
		tvTotalVolume = (TextView) findViewById(R.id.tvTotalVolume);// 总CBM
		tvRealTotalCounts = (TextView) findViewById(R.id.tvRealTotalCounts);// 实装总件数

		btnCompulsoryPacking = (Button) findViewById(R.id.btnCompulsoryPacking); // 强制装箱完成
		btnCompletePacking = (Button) findViewById(R.id.btnCompletePacking); // 装箱完成
		btnCompleteCancelPacking = (Button) findViewById(R.id.btnCompleteCancelPacking); // 取消装箱完成
		center = (LinearLayout) findViewById(R.id.center);

		// 明细跳转按钮
		tvDetail = (TextView) findViewById(R.id.tvDetail);
		tvDetail.setVisibility(View.GONE);
		tvDetail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		tvDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PP010Activity.this,
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

		packPhotoQuery =(TextView) findViewById(R.id.packPhotoQuery);
		packPhotoQuery.setVisibility(View.GONE);
		packPhotoQuery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(); 
				intent.setClass(PP010Activity.this, PP016Activity.class); 
				intent.putExtra("boxNo", strNoBox);
				intent.putExtra("orderCd", "");
				intent.putExtra("orderCdPublic", "");
				startActivity(intent);
			}
		});
		
		// 获取合计布局，初始化隐藏
		tabLayout = (TableLayout) findViewById(R.id.tabLayout);
		tabLayout.setVisibility(View.GONE);

		relatBtnLayout = (RelativeLayout) findViewById(R.id.relatBtnLayout);
		relatBtnLayout.setVisibility(View.GONE);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		listPP010 = (ListView) findViewById(R.id.listPP010View);

		listPP010File = (ListView) findViewById(R.id.listFileList);

		tvOprName = (TextView) findViewById(R.id.tvOPrName);
		linMain = (LinearLayout) findViewById(R.id.linMain);
		imgBtn = (ImageButton) findViewById(R.id.imgBtn);

		imgLiner = (LinearLayout) findViewById(R.id.imgLiner);
		imgLiner.setVisibility(View.GONE);

		tvTitleNoBox = (TextView) findViewById(R.id.tvTitleNoBox);

		// 适配器
		PP010Adapter = new ArrayAdapter(getApplicationContext(), 0) {
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
					tvCoder.setText(data.getCdBusinessPublic()); // 进仓编号
					tvCoder.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
					if(data.getRealNum() == data.getDropCounts()){
					    tvCoder.setTextColor(Color.BLUE);
					}else{
						tvCoder.setTextColor(Color.rgb(255,127,0));
					}
					final int proPosition = position;
					// 进仓编号的监听事件
					tvCoder.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							PP008FclData data = (PP008FclData) getItem(proPosition);
							Intent intent = new Intent(PP010Activity.this,
									PP011Activity.class);
							Bundle bundle = new Bundle();
							bundle.putString("cdOrder", data.getCdBusiness());
							bundle.putBoolean("isFclOrScl", bolIsFclOrScl);
							bundle.putString("noBox", strNoBox);
							bundle.putString("depotDtId", "");
							bundle.putBoolean("IsEdit", bolIsEit);
							bundle.putBoolean("isPost", true);
							bundle.putBoolean("selectSetFont", false);
							bundle.putString("cdBooking", data.getCdBooking());
							bundle.putInt("realCounts", 0);
							String strStsPacking = tvsts_packing.getText()
									.toString();
							if (getString(R.string.status_packing_done).equals(
									strStsPacking)) {
								bundle.putBoolean("IsSacn", false);
							} else {
								bundle.putBoolean("IsSacn", true);
							}

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

					TextView t3 = (TextView) layout
							.findViewById(R.id.tvNumMark);
					t3.setText(data.getNumRemark());// 件数备注

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
						e1.printStackTrace();
					}
				}

				return layout;
			}
		};

		listPP010.setAdapter(PP010Adapter);

		listPP010.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				PP008FclData data = (PP008FclData) listPP010
						.getItemAtPosition(arg2);
				Intent intent = new Intent(PP010Activity.this,
						PP011Activity.class);
				Bundle bundle = new Bundle();
				bundle.putString("cdOrder", data.getCdBusiness());
				bundle.putBoolean("isFclOrScl", bolIsFclOrScl);
				bundle.putString("noBox", strNoBox);
				bundle.putString("depotDtId", "");
				bundle.putBoolean("isPost", true);
				bundle.putBoolean("IsEdit", bolIsEit);
				bundle.putBoolean("selectSetFont", false);
				bundle.putString("cdBooking", data.getCdBooking());
				bundle.putInt("realCounts", 0);
				String strStsPacking = tvsts_packing.getText().toString();
				if (getString(R.string.status_packing_done).equals(
						strStsPacking)) {
					bundle.putBoolean("IsSacn", false);
				} else {
					bundle.putBoolean("IsSacn", true);
				}
				intent.putExtras(bundle);
				startActivityForResult(intent, 2);

			}
		});

		// 集箱号监听事件
		tvno_box.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!strNoBox.isEmpty()) {
					Intent intent = new Intent(PP010Activity.this,
							PP003Activity.class);
					intent.putExtra("boxNo", strNoBox);
					startActivity(intent);

				}
			}
		});

		// 强制装箱完成
		btnCompulsoryPacking.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				showDoubleDialog(_thisActivity,
						getString(R.string.PP010_msg_packError_004), 0);
			}
		});

		// 装箱完成
		btnCompletePacking.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDoubleDialog(_thisActivity,
						getString(R.string.PP010_msg_packError_003), 2);
			}
		});

		// 装箱完成取消
		btnCompleteCancelPacking.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDoubleDialog(_thisActivity,
						getString(R.string.PP010_msg_packError_005), 1);
			}
		});

		if (bolIsEit) {
			strNoBox = "";
		} else {
			strNoBox = noBox();
			setInitData();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
				//@SuppressWarnings("unused")
				final BarCode04 barResult04 = new BarCode04();
				//final BarCode05 barResult05 = new BarCode05();
				boolean result04 = barResult04.paserBarCode(data
						.getStringExtra("SCAN_RESULT"));

				//boolean result05 = barResult05.paserBarCode(data
				//		.getStringExtra("SCAN_RESULT"));
				// 首次扫描：集箱号；其他这是桩脚牌
				/*
				 * if (strNoBox.isEmpty()) {
				 */
				// 判断是否是集箱号
				if (result04) {

					// 箱号
					strCdContainer = barResult04.getContainerCd();
					setInitData();
				} else {
					/*
					 * if (result05) { // 箱号 strNoBox = barResult05.getBoxNo();
					 * setInitData(); } else {
					 */
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.PP012_001_MSG));
					/* } */

				}
				/* } */

				/*
				 * else {
				 * 
				 * // 判断是否是集箱号 if (result04) {
				 * 
				 * // 箱号 strCdContainer = barResult04.getContainerCd();
				 * setInitData(); } else {
				 * 
				 * final BarCode02 barResult = new BarCode02(); boolean result02
				 * = barResult.paserBarCode(data
				 * .getStringExtra("SCAN_RESULT")); // 判断是否是货物明显ID if (result02)
				 * {  2013090500001370 final String strOrderId =
				 * barResult.getOrderCd(); final String strDepotId =
				 * barResult.getDepotDtId(); Map<String, Object> PP010Map = new
				 * HashMap<String, Object>(); PP010Map.put("strNoBox",
				 * strNoBox);// 集箱号 PP010Map.put("strDepotId", strDepotId);//
				 * 货物明显ID
				 * 
				 * NetworkHelper.getInstance().postJsonData( _thisActivity,
				 * "PP008_GetTDcDetailByDepot", PP010Map, Integer.class, new
				 * AmassHttpResponseHandler<Integer>() {
				 * 
				 * @Override protected void OnSuccess( Integer response) {
				 * super.OnSuccess(response); // 获取对应的数据 Integer numRes =
				 * (Integer) response;
				 * 
				 * if (numRes > 0) { Intent intent = new Intent(
				 * PP010Activity.this, PP011Activity.class); Bundle bundle = new
				 * Bundle(); bundle.putString("cdOrder", strOrderId);
				 * bundle.putString("depotDtId", strDepotId);
				 * bundle.putBoolean("isFclOrScl", bolIsFclOrScl);
				 * bundle.putString("noBox", strNoBox);
				 * bundle.putBoolean("isPost", true); bundle.putBoolean(
				 * "selectSetFont", false); bundle.putInt("realCounts", 0);
				 * 
				 * intent.putExtras(bundle); startActivityForResult(intent, 2);
				 * } else { Utils.showAlertDialog( _thisActivity,
				 * getString(R.string.PP008_DIALOG_SCAN)); }
				 * 
				 * } }, true); } else { Utils.showAlertDialog(_thisActivity,
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
				setInitData();
			}
			break;
		}
	}

	/*
	 * 页面赋值
	 */
	public void setInitData() {
		Map<String, Object> PP010Map = new HashMap<String, Object>();
		PP010Map.put("strCdContainer", strCdContainer);// 箱号
		PP010Map.put("bolIsFclOrScl", bolIsFclOrScl);// 是否是整箱或者自拼箱
		PP010Map.put("strNobox", strNoBox);// 集箱号
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"PP010_GetContainer", PP010Map, PP008ResponseData.class,
				new AmassHttpResponseHandler<PP008ResponseData>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(PP008ResponseData response) {
						super.OnSuccess(response);
						if (response != null) {
							// 获取对应的数据
							PP008ResponseData PP010Data = (PP008ResponseData) response;
							if (!PP010Data.isFlgLockContainer()) {
								Utils.showAlertDialog(_thisActivity,
										getString(R.string.PP005_msg_pickError_009));
							} else {

								// 合计显示
								tabLayout.setVisibility(View.VISIBLE);
								// 明细
								tvDetail.setVisibility(View.VISIBLE);
								packPhotoQuery.setVisibility(View.VISIBLE);
								packPhotoQuery.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
								// 控件赋值
								tvTitleNoBox.setText(PP010Data.getNoBox()); // 集箱号
								planner.setText(PP010Data.getPlanner());   //计划员
								tvno_box.setText(PP010Data.getNoBox()); // 集箱号
								strNoBox = PP010Data.getNoBox();
								tvno_box.getPaint().setFlags(
										Paint.UNDERLINE_TEXT_FLAG);// 下划线
								tvno_box.setTextColor(Color.BLUE);
								//不在场时标红
								if(PP010Data.getFlgPresent() == 0){
									tvno_box.setTextColor(Color.RED);
								}
								tvsts_packing.setText(PP010Data.getStspacking());// 装箱状态
								if (getString(
										R.string.status_packing_picking_doing)
										.equals(PP010Data.getStspacking())
										|| getString(
												R.string.status_packing_doing)
												.equals(PP010Data
														.getStspacking())) {
									tvsts_packing
											.setBackgroundColor(Color.BLUE);
									tvsts_packing.setTextColor(Color.WHITE);
								} else if (getString(
										R.string.status_packing_done).equals(
										PP010Data.getStspacking())) {
									tvsts_packing
											.setBackgroundColor(Color.GRAY);
									tvsts_packing.setTextColor(Color.BLACK);
								} else {
									tvsts_packing
											.setBackgroundColor(Color.TRANSPARENT);

									tvsts_packing.setTextColor(Color.BLACK);
								}
								tvdt_packing_date.setText(PP010Data
										.getDtPackingDeadline());// 最晚完成时间

								tvop_wh_remarks.setText(PP010Data
										.getOpWhRemark());// 装箱规定
								tvTotalCounts.setText(String.valueOf(PP010Data
										.getTotalCounts()));// 总Packages
								tvTotalWeigth.setText(String.valueOf(PP010Data
										.getTotalWeight()));// 总KGS
								tvTotalVolume.setText(String.valueOf(PP010Data
										.getTotalVolume()));// 总CBM
								tvRealTotalCounts.setText(String
										.valueOf(PP010Data.getTotalRealCounts()));// 实装总数量

								strOprName = PP010Data.getOprUserName();// 操作人

								imgLiner.setVisibility(View.VISIBLE);
								// 明细数据
								List<PP008FclData> ppList = (List<PP008FclData>) PP010Data
										.getPp008FclList();
								//create by sdhuang 2014-11-26
								int numRemarkCount = 0; //有件数备注的进仓编号个数
								for(int i=0;i<ppList.size();i++){
									if(ppList.get(i).getNumRemark()!=null && !"".equals(ppList.get(i).getNumRemark())){
										numRemarkCount++;
									}
								}

								relatBtnLayout.setVisibility(View.VISIBLE);
								if (getString(R.string.status_packing_doing)
										.equals(PP010Data.getStspacking())) {

									if (PP010Data.isBolShowComplete()) {
										showBtn(true, true,
												PP010Data.isBolRoleShow());
									} else {
										if (PP010Data.isBolRoleShow()) {

											showBtn(true, false,
													PP010Data.isBolRoleShow());
										} else if(bolIsFclOrScl && numRemarkCount > 0){  //暂时只开发整箱
											//有件数备注时可以强制装箱完成
											showBtn(true, false,
													PP010Data.isBolRoleShow());
										} else {
											showBtn(false, false,
													PP010Data.isBolRoleShow());
										}

									}

								} else if (getString(
										R.string.status_packing_done).equals(
										PP010Data.getStspacking())) {
									showBtn(false, true,
											PP010Data.isBolRoleCancelShow());
								} else {
									showBtn(false, false, false);
								}
								center.setVisibility(View.GONE);
								PP010Adapter.clear();
								if (ppList != null) {
									PP010Adapter.addAll(ppList);
									PP010Adapter.notifyDataSetChanged();
									center.setVisibility(View.VISIBLE);
								}
								List<FileManageData> fileList = (List<FileManageData>) PP010Data
										.getFileList();
								pp010FileAdapter = new FileDataAdapter(
										_thisActivity, fileList);
								listPP010File.setAdapter(pp010FileAdapter);
								if (!bolIsEit) {
									btnCompletePacking.setVisibility(View.GONE);
									btnCompulsoryPacking
											.setVisibility(View.GONE);
									btnCompleteCancelPacking
											.setVisibility(View.GONE);
								}
							}
						} else {
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.PP005_msg_pickError_009));
						}
					}

				}, true);
	}

	/*
	 * 设置完成按钮是否显示还是隐藏
	 */
	public void showBtn(Boolean bolIsPacking, Boolean bolCompleteOrCompulsory,
			Boolean bolRole) {
		if (bolIsPacking) {

			if (bolCompleteOrCompulsory) {

				btnCompulsoryPacking.setVisibility(View.GONE); // 强制装箱完成
				btnCompletePacking.setVisibility(View.VISIBLE);// 装箱完成
			} else {

				btnCompulsoryPacking.setVisibility(View.VISIBLE);// 强制装箱完成

				btnCompletePacking.setVisibility(View.GONE);// 装箱完成
			}

			btnCompleteCancelPacking.setVisibility(View.GONE); // 取消装箱完成
		} else {
			if (bolRole) {

				btnCompulsoryPacking.setVisibility(View.GONE); // 强制装箱完成
				btnCompletePacking.setVisibility(View.GONE);// 装箱完成
				btnCompleteCancelPacking.setVisibility(View.VISIBLE); // 取消装箱完成
			} else {

				btnCompulsoryPacking.setVisibility(View.GONE); // 强制装箱完成
				btnCompletePacking.setVisibility(View.GONE);// 装箱完成
				btnCompleteCancelPacking.setVisibility(View.GONE); // 取消装箱完成
			}

		}
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

	/**
	 * Dialog(YES/NO)
	 * 
	 */
	public void showDoubleDialog(final Context context, String str,
			final int dialogId) {
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
								actionAfterOk(context, dialogId, 0);
							}
						})
				.setCancelable(false)
				.setNegativeButton(R.string.button_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// DoubleDialog(yes/no)
								actionAfterOk(context, dialogId, 1);

							}
						}).show();
	}

	/**
	 * DoubleDialog(yes/no)
	 * 
	 * @param dialogId
	 *            “0”代表确定，“1”代表取消
	 */
	public void actionAfterOk(final Context context, int dialogId, int i) {

		// dialogId==0 系统MEN选中
		if (dialogId == 0) {
			// 标识
			if (i == 0) {
				Map<String, Object> pp010Map = new HashMap<String, Object>();
				pp010Map.put("noBox", strNoBox);// 集箱号
				NetworkHelper.getInstance().postJsonData(context,
						"PP006_CheckCntScanLock", pp010Map, PP006CntScanLock.class,
						new AmassHttpResponseHandler<PP006CntScanLock>() {
							@Override
							protected void OnSuccess(PP006CntScanLock response) {
								super.OnSuccess(response);
								PP006CntScanLock cntScanLock = (PP006CntScanLock) response;
								if (cntScanLock.isFlag()) {
									Utils.showAlertDialog(_thisActivity, cntScanLock.getRemark());
								} else {
									Map<String, Object> pp010Map = new HashMap<String, Object>();
									pp010Map.put("strNoBox", strNoBox);// 集箱号
									NetworkHelper.getInstance().postJsonData(context,
											"CheckPackPic", pp010Map, Boolean.class,
											new AmassHttpResponseHandler<Boolean>() {
												@Override
												protected void OnSuccess(Boolean response) {
													super.OnSuccess(response);
													Boolean bolCheck = (Boolean) response;
													if (!bolCheck) {
														Utils.showAlertDialogIntMsg(_thisActivity,
																R.string.PP006_error_NoPic);
													} else {
														Intent intent = new Intent(
																PP010Activity.this,
																PP015Activity.class);
														Bundle bundle = new Bundle();
														bundle.putBoolean("isFclOrScl",
																bolIsFclOrScl);
														bundle.putString("noBox", strNoBox);
														bundle.putString("oprUser", strOprName);
														intent.putExtras(bundle);
														startActivityForResult(intent, 2);
													}
												}
											}, true);
													}
												}
						}, true);
			}
		}
		if (dialogId == 2) {
			// 标识
			if (i == 0) {
				Map<String, Object> pp010Map = new HashMap<String, Object>();
				pp010Map.put("noBox", strNoBox);// 集箱号
				NetworkHelper.getInstance().postJsonData(context,
						"PP006_CheckCntScanLock", pp010Map, PP006CntScanLock.class,
						new AmassHttpResponseHandler<PP006CntScanLock>() {
							@Override
							protected void OnSuccess(PP006CntScanLock response) {
								super.OnSuccess(response);
								PP006CntScanLock cntScanLock = (PP006CntScanLock) response;
								if (cntScanLock.isFlag()) {
									Utils.showAlertDialog(_thisActivity, cntScanLock.getRemark());
								} else {
									Map<String, Object> pp010Map = new HashMap<String, Object>();
									pp010Map.put("strNoBox", strNoBox);// 集箱号
									NetworkHelper.getInstance().postJsonData(context,
											"CheckPackPic", pp010Map, Boolean.class,
											new AmassHttpResponseHandler<Boolean>() {
												@Override
												protected void OnSuccess(Boolean response) {
													super.OnSuccess(response);
													Boolean bolCheck = (Boolean) response;
													if (!bolCheck) {
														Utils.showAlertDialogIntMsg(_thisActivity,
																R.string.PP006_error_NoPic);
													} else {
														Map<String, Object> pp010Map = new HashMap<String, Object>();
														pp010Map.put("strNoBox", strNoBox);// 集箱号
														pp010Map.put("bolIsFclOrScl", bolIsFclOrScl);
														NetworkHelper
																.getInstance()
																.postJsonData(
																		context,
																		"UpdateComplete",
																		pp010Map,
																		PP005StatusData.class,
																		new AmassHttpResponseHandler<PP005StatusData>() {
																			@Override
																			protected void OnSuccess(
																					PP005StatusData response) {
																				super.OnSuccess(response);
																				setInitData();
																			}
																		}, true);
													}
												}
											}, true);
								}
							}
						}, true);

			}
		}
		if (dialogId == 1) {
			// 标识
			if (i == 0) {
				Map<String, Object> pp010Map = new HashMap<String, Object>();
				pp010Map.put("strNoBox", strNoBox);// 集箱号
				pp010Map.put("bolIsFclOrScl", bolIsFclOrScl);
				NetworkHelper.getInstance().postJsonData(context,
						"UpdateCancel", pp010Map, PP005StatusData.class,
						new AmassHttpResponseHandler<PP005StatusData>() {
							@Override
							protected void OnSuccess(PP005StatusData response) {
								super.OnSuccess(response);
								// 获取对应的数据
								PP005StatusData statusData = (PP005StatusData) response;
								if (!statusData.getUpdatePickStatus()) {
									if ("006".equals(statusData.getErrorCd())) {
										Utils.showAlertDialogIntMsg(
												_thisActivity,
												R.string.PP010_msg_packError_006);
									} else {
										Utils.showAlertDialogIntMsg(
												_thisActivity,
												R.string.PP005_msg_pickError_unknow);
									}

								}
								setInitData();
							}
						}, true);
			}
		}
	}
}
