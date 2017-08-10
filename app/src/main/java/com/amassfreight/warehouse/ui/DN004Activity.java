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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DN004TDepotDt;
import com.amassfreight.domain.DN004TLoad;
import com.amassfreight.domain.DepotDN004;
import com.amassfreight.domain.SelectDict;
import com.amassfreight.domain.VA003PileCard;
import com.amassfreight.utils.SessionHelper;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.amassfreight.warehouse.ui.dialogs.SelectPrintDialog;
import com.google.gson.reflect.TypeToken;

public class DN004Activity extends BaseActivity implements OnDialogOkListener {

	private String barCode = "";
	private String depotID = ""; // 扫描后得到的进仓ID
	private String noBatch = ""; // 批次
	private String cdOrder = ""; // 进仓编号个别
	private String cdOrderPublic = ""; // 进仓编号共通
	private String thNo = ""; // 同行编号
	private String noPilecard = ""; // 桩脚牌ID
	private String depotDtID = ""; // 货物明细ID
	private String type = ""; // 新增、更新标识
	private String loadID1 = ""; // 理货员
	private String loadID2 = ""; // 搬运工
	private String loadID3 = ""; // 铲车工
	private String AddType = ""; // 编辑，新增批次标识

	private TextView txtDepotNo; // 进仓编号
	private TextView txtThNo; // 同行编号
	private TextView txtNoBatch; // 批次
	private TextView txtNoPilecard; // 桩脚牌ID

	private LinearLayout carContent;
	private LinearLayout linLayNoMulDepot;  // 进仓编号分号
	private EditText editCarNo; // 车牌号
	private EditText editCarArea; // 车牌简称
	private Spinner spCarType; // 车辆类型
	/* 注销  by yxq 2014/09/03
	private Spinner spTicket1; // 分票号1
	// edit by yxq 2014/08/26 begin
	// 原代码 private Spinner spTicket2; // 分票号2
	private TextView textScanedNum;   // 已扫描n票
	// edit by yxq 2014/08/26 end
	private EditText editSameCarNo; // 同车编号
	*/
	// add by yxq 2014/09/03 begin
	private EditText editNoMultipleDepot;  // 进仓分票号
	// add by yxq 2014/09/03 end
	private EditText editNoMultipleColo;   //同行编号分号
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
	private LinearLayout layPos;
	private EditText editPos; // 库区
	private EditText editStore; // 库位
	private EditText spTruck; // 铲车工
	private EditText editCopies; // 桩脚牌个数
	private EditText editCopies_1; // 桩脚牌个数(增值服务用)  add by yxq 2014/09/24
	private LinearLayout layCopies;
	private LinearLayout layCopies_1; // （增值服务用） add by yxq 2014/09/24
	private EditText txtRemark; // 备注
	// add by yxq 2014/09/24 begin
	private LinearLayout layReaMea;
	private CheckBox chkReaMeaOK;   // 复量正确
	private CheckBox chkReaMeaSize; // 复量尺寸
	// add by yxq 2014/09/24 end

	private Button btnSave;
	private Button btnPrint;
	private Button btnReturnOk;
	private Button btnError;
	private Button btnReaMea;          // 保存（复量） add by yxq 2014/09/16
	private Button btnChangePack;  	   // 保存（更改包装） add by yxq 2014/09/16
	private Button btnVa003SaveCommon; // 保存（更改件数、称重共通）add by yxq 2014/10/30

	private String CarNo; // 车牌号
	private String CarType; // 车辆类型
	private String NoMultipleOrder; // 分票号
	private String NoCarNum; // 同车编号
	private String NoMultipleDepot; // 进仓分票号  add by yxq 2014/09/03
	private String NoMultipleColo;  // 同行编号

	private boolean saveFlag = false; // 保存完成标识
	private boolean updateFlag = false; // 是否需要更新车辆信息标识
	private DN004TDepotDt va003DepotDt;
	private DN004TLoad va003TLoad;
	private String selTick;
	private final String[] carArea = new String[] { "沪", "苏", "浙", "皖", "赣",
			"川", "鄂", "桂", "甘", "贵", "黑", "京", "津", "冀", "晋", "吉", "辽", "鲁",
			"蒙", "闽", "宁", "青", "琼", "陕", "湘", "新", "渝", "豫", "云", "粤", "藏 " };
	private String selCarArea; // 选中的地区简称

	private List<SelectDict> posList;
	private List<SelectDict> truckList;
	private String selPos;
	private String selTruckId;

	private List<SelectDict> packTypeList;
	// add by yxq 2014/10/13 begin
	private Map<String, String> packTypeMap;
	// add by yxq 2014/10/13 end
	private String selPackType;
	private String selPackType_sec;//单板多尺寸
	private List<DepotDN004> reDepotList;

	private boolean bolFlag = true;

	private EditText ed_OprLength; // 临时长度
	private EditText ed_OprWidth; // 临时宽度
	private EditText ed_OprHeight; // 临时高度
	private EditText ed_OprNum; // 临时件数
	private EditText ed_PackType; // 临时包装类型
	private Button btnOprSave; // 保存按钮
	private Boolean bolOpr = true;
	private Boolean bolFirstlOpr = true;
	private LinearLayout linOpr;
	private ListView listOprLWH;
	private ArrayAdapter<Object> oprAdapter;
	private LayoutInflater mInflater;

	private double numOprLength; // 临时长度
	private double numOprWidth; // 临时宽度
	private double numOprHeight; // 临时高度
	private int numOprNum; // 临时件数
	private double numVolume; // 临时体积
	private List<DepotDN004> dn004List;
	private int numSelect = 0;
	private String strModeRemark = "";

	private String strServiceId = "";  // add by yxq 2014/09/29
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		type = (String) intent.getStringExtra("Type");

		if (type.equals("5") || type.equals("8")) {
			// 理货信息查看
			setTitle(getString(R.string.DN004_DETAIL_title));
			bolFirstlOpr = false;
		} else if (type.equals("0") || type.equals("2") || type.equals("3")
				|| type.equals("10")) {
			// 理货信息录入
			setTitle(getString(R.string.DN004_INSERT_title));

		} else if (type.equals("1") || type.equals("6")) {
			// 理货信息编辑
			setTitle(getString(R.string.DN004_EDIT_title));
			bolFirstlOpr = false;
		}
		// add by yxq 2014/09/16 begin
		else if (type.equals("12")) {
			setTitle(getString(R.string.DN004_REP_MEA_title));  // 理货信息复量
			bolFirstlOpr = false;
		}
		// add by yxq 2014/09/16 end
		// add by yxq 2014/10/20 begin
		else if (type.equals("13")) {
			setTitle(getString(R.string.DN004_CHANGE_PACK_title));  // 理货信息更改包装
			bolFirstlOpr = false;
		}
		// add by yxq 2014/10/20 end
		// add by yxq 2014/10/30 begin
		else if (type.equals("14")) {
			setTitle(getString(R.string.DN004_CHANGE_NUM_title));   // 理货信息更改件数
			bolFirstlOpr = false;
		}
		else if (type.equals("15")) {
			setTitle(getString(R.string.DN004_CHANGE_WEIGH_title)); // 理货信息称重
			bolFirstlOpr = false;
		}
		// add by yxq 2014/10/30 end

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dn004);
		setupActionBar();

		// 初期加载
		initializeView();

		init();
	}

	/*
	 * 初期加载
	 */
	private void initializeView() {
		selTick = "";
		selCarArea = "";
		reDepotList = new ArrayList<DepotDN004>();

		/* 获取Intent中的Bundle对象 */
		Bundle bundle = this.getIntent().getExtras();
		/* 获取Bundle中的数据，注意类型和key */
		// type = bundle.getString("Type");
		cdOrder = bundle.getString("cdOrder");
		cdOrderPublic = bundle.getString("cdOrderPublic");
		depotID = bundle.getString("depotID");
		thNo = bundle.getString("thNo");
		noBatch = bundle.getString("noBatch");

		txtDepotNo = (TextView) findViewById(R.id.txtInStore); // 进仓编号
		txtDepotNo.setText(cdOrderPublic);
		txtThNo = (TextView) findViewById(R.id.txtTH); // 同行编号
		txtThNo.setText(thNo);
		txtNoBatch = (TextView) findViewById(R.id.txtPC); // 批次
		txtNoBatch.setText(noBatch);
		txtNoPilecard = (TextView) findViewById(R.id.txtNoPilecard); // 桩脚牌ID

		layCopies = (LinearLayout) findViewById(R.id.layCopies);
		layCopies_1 =  (LinearLayout) findViewById(R.id.layCopies_1);   // add by yxq 2014/09/24
		editCopies = (EditText) findViewById(R.id.edit_Copies); // 桩脚牌个数
		editCopies.setSelectAllOnFocus(true);
		
		// add by yxq 2014/09/24 begin
		editCopies_1 = (EditText) findViewById(R.id.edit_Copies_1); // 桩脚牌个数
		editCopies_1.setSelectAllOnFocus(true);
		// add by yxq 2014/09/24 end
		
		// 0：DN002新增；1：DN002编辑；2：DN002复制；3：VA003新增；5：DN002查看；
		// 6：VA003编辑（不查数据库）；7：VA003编辑（查数据库）；8：VA003查看（查数据库）；9：VA003查看（不查数据库）；10：VA003复制
		// 11:VA003复制（查数据库）；12：VA003复量；13：VA003更改包装；14：VA003更改件数；15：称重
		if (type.equals("0") || type.equals("1") || type.equals("2")) {
			CarNo = bundle.getString("CarNo");
			CarType = bundle.getString("CarType");
			NoMultipleOrder = bundle.getString("NoMultipleOrder"); // 分票号
			NoCarNum = bundle.getString("NoCarNum"); // 同车编号
			NoMultipleDepot = bundle.getString("NoMultipleDepot"); // 进仓分票号  add by yxq 2014/09/03
			NoMultipleColo = bundle.getString("NoMultipleColo"); 
			if (type.equals("0") || type.equals("2")) {
				AddType = bundle.getString("AddType");
			}
		}

		// 编辑
		// edit by yxq 2014/09/16 begin
		// 原代码  if (type.equals("1") || type.equals("2") || type.equals("5")) {
		if (type.equals("1") || type.equals("2") || type.equals("5") || type.equals("12")
			// add by yxq 2014/10/20 begin
			|| type.equals("13")
			// add by yxq 2014/10/20 end
			// add by yxq 2014/10/30 begin
			|| type.equals("14") || type.equals("15")
			// add by yxq 2014/10/30 end
			){
		// edit by yxq 2014/09/16 end
			noPilecard = bundle.getString("noPilecard");
			depotDtID = bundle.getString("depotDtID");

			// edit by yxq 2014/09/16 begin
			// 原代码  if (type.equals("1") || type.equals("5")) {
			if (type.equals("1") || type.equals("5")  || type.equals("12")
			  // add by yxq 2014/10/20 begin
			  || type.equals("13")
			  // add by yxq 2014/10/20 end
			  // add by yxq 2014/10/30 begin
			  || type.equals("14") || type.equals("15")
			  // add by yxq 2014/10/30 end
			  ){
			// edit by yxq 2014/09/16 end
				txtNoPilecard.setText(noPilecard);
				// layCopies.setVisibility(View.GONE); // 桩脚牌个数
				editCopies.setEnabled(false);
				// edit by yxq 2014/09/16 begin
				// 原代码  if (type.equals("5")) {
				if (type.equals("5") || type.equals("12")
				  // add by yxq 2014/10/20 begin
				  || type.equals("13")
				  // add by yxq 2014/10/20 end
				  // add by yxq 2014/10/30 begin
				  || type.equals("14") || type.equals("15")
				  // add by yxq 2014/10/30 end
			     ) {
				// edit by yxq 2014/09/16 end
					bolFlag = bundle.getBoolean("bolFlag");
				}
			}
			// add by yxq 2014/09/29 begin
			if(type.equals("12")
			  // add by yxq 2014/10/20 begin
			  || type.equals("13")
			  // add by yxq 2014/10/20 end
			  // add by yxq 2014/10/30 begin
			  || type.equals("14") || type.equals("15")
			  // add by yxq 2014/10/30 end
			  ){
				strServiceId = bundle.getString("serviceId");
			}
			// add by yxq 2014/09/29 end
		}

		carContent = (LinearLayout) findViewById(R.id.carContent);
		// 进仓编号分号  add by yxq 2014/09/03
		linLayNoMulDepot =  (LinearLayout) findViewById(R.id.linLayNoMulDepot);  
		editCarNo = (EditText) findViewById(R.id.edit_CarNo); // 车牌号
		editCarNo.setSelectAllOnFocus(true);
		editCarArea = (EditText) findViewById(R.id.edit_CarArea); // 车牌简称
		spCarType = (Spinner) findViewById(R.id.sp_CarType); // 车辆类型
		/* 注销  by yxq 2014/09/03
		spTicket1 = (Spinner) findViewById(R.id.sp_Ticket1); // 分票号1
		// edit by yxq 2014/08/26 begin		
		// 原代码 spTicket2 = (Spinner) findViewById(R.id.sp_Ticket2); // 分票号2
		textScanedNum = (TextView) findViewById(R.id.textScanedNum); // 已扫描n票
		// edit by yxq 2014/08/26 end
		editSameCarNo = (EditText) findViewById(R.id.edit_SameCarNo); // 同车编号
		*/
		// add by yxq 2014/09/03 begin
		editNoMultipleDepot = (EditText) findViewById(R.id.edit_NoMultipleDepot); // 进仓分票号
		// add by yxq 2014/09/03 end
		editNoMultipleColo = (EditText) findViewById(R.id.edit_NoMultipleColo); // 同行分号
		if(cdOrder == null ||"".equals(cdOrder)){
			if(cdOrderPublic == null||"".equals(cdOrderPublic)){
			    editNoMultipleDepot.setEnabled(false);
			}
		}
		if(thNo == null ||"".equals(thNo)){
			editNoMultipleColo.setEnabled(false);
		}
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
		layPos = (LinearLayout) findViewById(R.id.layPos);
		// layPos.setVisibility(View.GONE);
		editPos = (EditText) findViewById(R.id.edit_Pos); // 库区
		editStore = (EditText) findViewById(R.id.edit_Store); // 库位
		editStore.setSelectAllOnFocus(true);
		spTruck = (EditText) findViewById(R.id.sp_Truck); // 铲车工
		txtRemark = (EditText) findViewById(R.id.txtRemark); // 备注
		// add by yxq 2014/09/24 begin
		layReaMea = (LinearLayout) findViewById(R.id.layReaMea);     // 复量
		chkReaMeaOK = (CheckBox) findViewById(R.id.chkReaMeaOK);     // 复量正确
		chkReaMeaSize = (CheckBox) findViewById(R.id.chkReaMeaSize); // 复量尺寸
		// add by yxq 2014/09/24 end
		// add by yxq 2014/11/04 begin
		chkReaMeaOK.setEnabled(false);
		chkReaMeaSize.setEnabled(false);
		// add by yxq 2014/11/04 end

		btnSave = (Button) findViewById(R.id.btn_Save); // 保存按钮
		btnSave.setEnabled(false);
		// add by yxq 2014/09/16 begin
		btnReaMea = (Button) findViewById(R.id.btn_Rea_Mea); // 保存按钮（复量）
		// add by yxq 2014/09/16 end
		// add by yxq 2014/10/20 begin
		btnChangePack = (Button) findViewById(R.id.btn_Change_Pack); // 保存按钮（修改包装）
		// add by yxq 2014/10/20 end
		// add by yxq 2014/10/30 begin
		// 保存按钮（更改件数、称重共通）
		btnVa003SaveCommon =  (Button) findViewById(R.id.btn_VA003_Save_Common); 
		// add by yxq 2014/10/30 end
		btnPrint = (Button) findViewById(R.id.btn_Print); // 打印桩脚牌按钮
		btnPrint.setEnabled(false);
	    //补打桩脚牌
		btnPrint.setOnLongClickListener(new View.OnLongClickListener() {			
			@Override
			public boolean onLongClick(View arg0) {
				//弹出确认dialog
				AlertDialog.Builder builder = new AlertDialog.Builder(
						new ContextThemeWrapper(_thisActivity,
								android.R.style.Theme_Holo_Light));
				builder.setIcon(R.drawable.ic_launcher);
				builder.setCancelable(false);
				builder.setMessage(R.string.msg_reprint_pilecard);
				builder.setTitle(R.string.app_name);
				builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Map<String, Object> p1 = new HashMap<String, Object>();
						p1.put("depotDtId", depotDtID);
						p1.put("cdOrder", cdOrder);
						p1.put("cdOrderPublic", cdOrderPublic);
						p1.put("coLoader", thNo);
						p1.put("reDepotList", reDepotList);

						NetworkHelper.getInstance().postJsonData(_thisActivity,
								"DN004_ReprintPileCardReport", p1,
								new TypeToken<DN004TDepotDt>() {
								}.getType(), new AmassHttpResponseHandler<DN004TDepotDt>() {

									@Override
									protected void OnSuccess(DN004TDepotDt response) {
										super.OnSuccess(response);
										Utils.showAlertDialog(_thisActivity,
												getString(R.string.msg_common_print_success));
									}
								}, true);
					}
				});
				builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				return false;
			}
		});
		btnReturnOk = (Button) findViewById(R.id.btn_Return); // 确定返回前画面
		btnReturnOk.setVisibility(View.GONE);
		btnError = (Button) findViewById(R.id.btn_Error); // 异常信息按钮
		btnError.setEnabled(false);

		// 从VA003跳转 3:新增 6:编辑
		if (type.equals("3") || type.equals("6") || type.equals("10")) {
			// layPos.setVisibility(View.VISIBLE);
			btnSave.setVisibility(View.GONE);
			btnPrint.setVisibility(View.GONE);
			btnError.setVisibility(View.GONE);
			carContent.setVisibility(View.GONE);
			// 进仓编号分号  add by yxq 2014/09/03
			// edit by yxq 2014/10/16 begin
			// 原代码  linLayNoMulDepot.setVisibility(View.GONE);
			editNoMultipleDepot.setText(bundle.getString("NoMultipleDepot"));
			editNoMultipleColo.setText(bundle.getString("NoMultipleColo"));
		    // edit by yxq 2014/10/16 end
			txtDepotNo.setText(bundle.getString("cdOrderPublic")); // 进仓编号
			txtNoBatch.setText(bundle.getString("noBatch")); // 批次

			layCopies.setVisibility(View.GONE);      // 桩脚牌个数

			// add by yxq 2014/09/24 begin
			if(type.equals("3") || type.equals("10")){   // 新增 或 复制
				layCopies_1.setVisibility(View.VISIBLE);   	
			}
			// add by yxq 2014/09/24 end

			if (type.equals("6") || type.equals("10")) {
				txtNoPilecard.setText(bundle.getString("noPilecard")); // 桩脚牌
				selPos = bundle.getString("pos");
				editStore.setText(bundle.getString("location"));

				String va003DepotDtID = bundle.getString("depotDtId");
				if (va003DepotDtID != null && !va003DepotDtID.equals("")) {
					depotDtID = va003DepotDtID;
					if (type.equals("6")) {
						type = "7";
					} else {
						type = "11";
					}
				}
			}
		}

		// 查看
		if (type.equals("5")) {
			btnSave.setVisibility(View.GONE);
		}
		
		// edit by yxq 2014/09/16 begin
		if (type.equals("12")) {  // 复量
			btnSave.setVisibility(View.GONE);
			btnReaMea.setVisibility(View.VISIBLE);
			// add by yxq 2014/09/24 begin
			btnPrint.setVisibility(View.GONE);
			btnError.setVisibility(View.GONE);
			layReaMea.setVisibility(View.VISIBLE);
			// add by yxq 2014/09/24 end
		}
		// edit by yxq 2014/09/16 end
		// add by yxq 2014/10/20 begin
		if (type.equals("13")) {  // 更改包装
			btnSave.setVisibility(View.GONE);
			btnChangePack.setVisibility(View.VISIBLE);
			btnPrint.setVisibility(View.GONE);
			btnError.setVisibility(View.GONE);
			layReaMea.setVisibility(View.GONE);
		}
		// add by yxq 2014/10/20 end

		 // add by yxq 2014/10/30 begin
		 if(type.equals("14") || type.equals("15")){
			btnSave.setVisibility(View.GONE);
			btnPrint.setVisibility(View.GONE);
			btnError.setVisibility(View.GONE);
			layReaMea.setVisibility(View.GONE);
			btnVa003SaveCommon.setVisibility(View.VISIBLE);
		 }
		 // add by yxq 2014/10/30 end
		// VA003查看
		if (type.equals("8")) {
			// layPos.setVisibility(View.VISIBLE);
			btnSave.setVisibility(View.GONE);
			btnPrint.setVisibility(View.GONE);
			btnError.setVisibility(View.GONE);
			carContent.setVisibility(View.GONE);
			// 进仓编号分号  add by yxq 2014/09/03
			// edit by yxq 2014/10/16 begin
			// 原代码 linLayNoMulDepot.setVisibility(View.GONE);
			editNoMultipleDepot.setText(bundle.getString("NoMultipleDepot"));
		    // edit by yxq 2014/10/16 end
			editNoMultipleColo.setText(bundle.getString("NoMultipleColo"));
			layCopies.setVisibility(View.GONE); // 桩脚牌个数

			txtDepotNo.setText(bundle.getString("cdOrderPublic")); // 进仓编号
			txtNoBatch.setText(bundle.getString("noBatch")); // 批次
			txtNoPilecard.setText(bundle.getString("noPilecard")); // 桩脚牌

			selPos = bundle.getString("pos");
			editStore.setText(bundle.getString("location"));

			String va003DepotDtID = bundle.getString("depotDtId");
			if (va003DepotDtID != null && !va003DepotDtID.equals("")) {
				depotDtID = va003DepotDtID;
			} else {
				type = "9";
			}
		}

		// 绑定分票号下拉框
		// 注销 by yxq 2014/09/03 InitTicketList();

		// 省份简称
		editCarArea.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode((EditText) view);

				int i = -1; // 初期选中项
				// 循环获取选择项
				if (selCarArea != null && selCarArea.length() > 0) {
					for (i = 0; i < carArea.length; i++) {
						if (carArea[i].equals(selCarArea)) {
							break;
						}
					}
				}

				new AlertDialog.Builder(new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light))
						.setTitle(getString(R.string.DN004_CAR_AREA))
						.setSingleChoiceItems(carArea, i,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										editCarArea.setText(carArea[which]);
										selCarArea = carArea[which];
										dialog.dismiss();
									}
								}).show();
			}
		});

		// 监听事件 计算体积
		// add by yxq 2014/10/30 begin
		if(!type.equals("14")){
		// add by yxq 2014/10/30 end
			DoFocusChangeListener(editLong);
			DoFocusChangeListener(editWide);
			DoFocusChangeListener(editHigh);
			DoFocusChangeListener(editArgueNumber);
		}

		// 绑定数据
		ShowDataByDepotDtID();

		// VA003编辑的情况页面赋值
		if (type.equals("6") || type.equals("9") || type.equals("10")) {
			va003DepotDt = (DN004TDepotDt) bundle
					.getSerializable("DN004TDepotDt");
			editLong.setText(String.valueOf(va003DepotDt.getNoLen()));// 长
			editWide.setText(String.valueOf(va003DepotDt.getNoWidth()));// 宽
			editHigh.setText(String.valueOf(va003DepotDt.getNoHeight()));// 高
			editNumber.setText(String.valueOf(va003DepotDt.getDepotNum()));// 件数
			editArgueNumber
					.setText(String.valueOf(va003DepotDt.getBillingNum()));// 计费件数
			editWeight.setText(String.valueOf(va003DepotDt.getKgs()));// 重量
			editVolume.setText(String.valueOf(va003DepotDt.getCbm()));// 体积
			txtRemark.setText(va003DepotDt.getDepotRemark());// 进仓备注

			va003TLoad = (DN004TLoad) bundle.getSerializable("DN004TLoad");
			editDock.setText(va003TLoad.getWorkerID()); // 搬运工
			selPackType = va003DepotDt.getPackType();
		}

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
			    // add by yxq 2014/10/30 begin
				if("14".equals(type)){
					return;
				}
				// add by yxq 2014/10/30 end
				// 赋值
				//拉板测量时默认设置计费件数为1
				//modify by sdhuang 2014-12-08
				SelectDict selectItem  = (SelectDict) spMeterModel.getSelectedItem();
				if("1".equals(selectItem.getId())){
				//if(spMeterModel.getSelectedItemId()== 1){
					editArgueNumber.setText("1");
				}else{
				    editArgueNumber.setText(editNumber.getText().toString().trim());
				}
				ArgueNumber();
			}
		});

		/* 注销  by yxq 2014/09/03
		// 同车编号点击事件
		editSameCarNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode((EditText) view);

				if (editSameCarNo.getText().length() > 0) {

					if (type.equals("5") || type.equals("8")
							|| type.equals("9")) {
						return;
					}

					// 显示同车编号
					ShowEditText();
				}
			}
		});
		*/

		//铲车工列表
		spTruck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				initTruck(view);			
			}
		});
		
		// 库区
		editPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				initPosition(view);				
			}
		});

		// 包装类型
		editPackType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode((EditText) view);

				if (type.equals("5") || type.equals("8") || type.equals("9")) {
					return;
				}
				// add by yxq 2014/09/16 begin
				if (type.equals("12")) {
					return;
				}
				// add by yxq 2014/09/16 end
				// add by yxq 2014/10/20 begin
				if (type.equals("13")) {
					return;
				}
				// add by yxq 2014/10/20 end
				// add by yxq 2014/10/30 begin
				if (type.equals("14")||type.equals("15")) {
					return;
				}
				// add by yxq 2014/10/30 end

				// 显示包装类型
				ShowPackTypeEditText();
			}
		});

		SetEnableForDetail();
		
		// add by yxq 2014/09/24 begin
		// 复量正确
		chkReaMeaOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				chkReaMeaSize.setChecked(!chkReaMeaOK.isChecked());
			}
		});
		// 复量尺寸
		chkReaMeaSize.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				chkReaMeaOK.setChecked(!chkReaMeaSize.isChecked());
			}
		});
		// add by yxq 2014/09/24 end
	}

	/*
	 * 查看时设置控件无效化
	 */
	private void SetEnableForDetail() {
		// edit by yxq 2014/09/16 begin
		// 原代码 if (type.equals("5") || type.equals("8") || type.equals("9")) {
		if (type.equals("5") || type.equals("8") || type.equals("9") || type.equals("12")
			 // add by yxq 2014/10/20 begin
			 || type.equals("13")
			 // add by yxq 2014/10/20 end
			 // add by yxq 2014/10/30 begin
			 || type.equals("14")|| type.equals("15")
			 // add by yxq 2014/10/30 end
		) {
		// edit by yxq 2014/09/16 end	
			editCarNo.setEnabled(false); // 车牌号
			editCarArea.setEnabled(false); // 车牌简称
			spCarType.setEnabled(false); // 车辆类型
			/* 注销  by yxq 2014/09/03
			spTicket1.setEnabled(false); // 分票号1
			// 注销 by yxq 2014/08/26 spTicket2.setEnabled(false); // 分票号2
			editSameCarNo.setEnabled(false); // 同车编号
			*/
			// add by yxq 2014/09/03 begin
			editNoMultipleDepot.setEnabled(false);
			// add by yxq 2014/09/03 end 
			// add by yxq 2014/11/07 begin
			if(!type.equals("12")){
			// add by yxq 2014/11/07 end
				spMeterModel.setEnabled(false); // 测量模式
			}
			// add by yanxiaoqi 2014/09/16 begin
			if(!type.equals("12")
			  // add by yxq 2014/10/20 begin
			   && !type.equals("13")
			  // add by yxq 2014/10/20 end
			  ){
			// add by yanxiaoqi 2014/09/16 end
				editLong.setEnabled(false); // 长
				editWide.setEnabled(false); // 宽
				editHigh.setEnabled(false); // 高
				editNumber.setEnabled(false); // 件数
				editArgueNumber.setEnabled(false); // 计费件数
			}
			editWeight.setEnabled(false); // 重量
			editVolume.setEnabled(false); // 体积
			// add by yxq 2014/10/30 begin
			if(type.equals("14")){
				editNumber.setEnabled(true); // 件数
			}else if(type.equals("15")){
				editWeight.setEnabled(true); // 重量
			}
			// add by yxq 2014/10/30 end
			// edit by yxq 2014/10/08 begin
			// 解决在只读情况下，输入的字符很多时，内容可以左右滚动
			//editDock.setEnabled(false); // 装卸工
			editDock.setFocusableInTouchMode(false); // 装卸工
			editDock.setTextColor(Color.GRAY);   //设置只读时的文字颜色
			// edit by yxq 2014/10/08 end
			spPackUnit.setEnabled(false); // 包装单位
			editPackType.setEnabled(false); // 包装类型
			editPos.setEnabled(false); // 库区
			editStore.setEnabled(false); // 库位
			txtRemark.setEnabled(false); // 备注
			// layCopies.setVisibility(View.GONE); // 桩脚牌个数
			spTruck.setEnabled(false); // 铲车工
			editCopies.setEnabled(false); // 桩脚牌个数
		}
	}

	/*
	 * 显示包装类型
	 */
	private void ShowPackTypeEditText() {
		final String[] packTypeId = new String[packTypeList.size()];
		final String[] packTypeName = new String[packTypeList.size()];
		boolean[] checksItem = null;

		final List<String> medicSelecID = new ArrayList<String>(); // 选中的包装类型

		if (selPackType != null && selPackType.length() > 0) {
			if (selPackType.contains(",")) {
				for (String str : selPackType.split(",")) {
					medicSelecID.add(str.trim());
				}
			} else {
				medicSelecID.add(selPackType.trim());
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
							// edit by yxq 2014/10/13 begin
							/*str += packTypeName[Integer.valueOf(sel.toString()) - 1]
									+ ",";*/
							str += packTypeMap.get(sel.toString()) + ",";
							// edit by yxq 2014/10/13 end
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
					if (!flag && bolOpr) {
						ArgueNumber();
					}
				} catch (NumberFormatException e) {
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
			p1.put("depotDtId", depotDtID);
			p1.put("type", type);

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"DN004_GetDepotDataByDepotDtID", p1,
					new TypeToken<DepotDN004>() {
					}.getType(), new AmassHttpResponseHandler<DepotDN004>() {

						@Override
						protected void OnSuccess(DepotDN004 response) {
							super.OnSuccess(response);
							btnPrint.setEnabled(true);
							// add by yxq 2014/09/16 begin
							// 原代码 if (type.equals("1") || type.equals("5")) {
							if (type.equals("1") || type.equals("5") || type.equals("12")
								// add by yxq 2014/10/20 begin
								|| type.equals("13")
							    // add by yxq 2014/10/20 end
								// add by yxq 2014/10/30 begin
								|| type.equals("14") ||type.equals("15")
							    // add by yxq 2014/10/30 end
							) {
							// add by yxq 2014/09/16 end
								btnError.setEnabled(true);
							}
							btnSave.setEnabled(true);
							if (type.equals("3") || type.equals("6")
									|| type.equals("7") || type.equals("10")
									|| type.equals("11")) {
								btnReturnOk.setVisibility(View.VISIBLE);
							}

							// 获取数据
							DepotDN004 depot = (DepotDN004) response;

							// 绑定车辆类型下拉框
							InitDropDownList(depot.getCarTypeList(), spCarType);

							// 绑定测量模式下拉框
							InitDropDownList(depot.getMeterModelList(),
									spMeterModel);

							// 绑定包装单位下拉框
							InitDropDownList(depot.getPackUnitList(),
									spPackUnit);

							// 绑定铲车工下拉框
							//InitDropDownList(depot.getTruckList(), spTruck);
							truckList = depot.getTruckList();

							// 绑定库区下拉框
							posList = depot.getPosList();

							// 包装类型
							packTypeList = depot.getPackTypeList();
							// add by yxq 2014/10/13 begin
							packTypeMap = new HashMap<String, String>();
							Iterator<SelectDict> it = packTypeList.iterator();
							while (it.hasNext()) {
								SelectDict item = it.next();
								packTypeMap.put(item.getId(), item.getName());
							}
							// add by yxq 2014/10/13 end

							if (type.equals("0")) {
								editCarNo.setText(CarNo); // 车牌号

								if (CarNo != null && CarNo.length() > 1) {
									editCarArea.setText(CarNo.substring(0, 1)); // 车牌简称
									selCarArea = CarNo.substring(0, 1);
									editCarNo.setText(CarNo.substring(1,
											CarNo.length())); // 车牌号
								}

								setSpinnerItemSelectedByValue(spCarType,
										CarType);// 车辆类型
								/* 注销  by yxq 2014/09/03
								if (NoMultipleOrder != null
										&& !NoMultipleOrder.equals("")) {
									if (NoMultipleOrder.contains("-")) {
										setSpinnerItemSelectedByValue(
												spTicket1,
												NoMultipleOrder.split("-")[0]); // 分票号1
										selTick = NoMultipleOrder.split("-")[1]; // 分票号2
									} else {
										setSpinnerItemSelectedByValue(
												spTicket1, NoMultipleOrder); // 分票号1
										selTick = "";
									}
								}
								
								editSameCarNo.setText(NoCarNum); // 同车编号
								// add by yxq 2014/08/26 begin
								textScanedNum.setText(String.format(getString(
									      R.string.DN004_029_MSG),GetSameCarNo()));
								// add by yxq 2014/08/26 end
								*/
								// add by yxq 2014/09/03 begin
								editNoMultipleDepot.setText(NoMultipleDepot); // 进仓分票号
								// add by yxq 2014/09/03 end
								editNoMultipleColo.setText(NoMultipleColo);
								// start zxx modify
								Map<String, Object> p1 = new HashMap<String, Object>();
								p1.put("depotID", depotID);
								p1.put("noBatch", noBatch);

								NetworkHelper
										.getInstance()
										.postJsonData(
												_thisActivity,
												"DN004_GetDepotList",
												p1,
												new TypeToken<List<DepotDN004>>() {
												}.getType(),
												new AmassHttpResponseHandler<List<DepotDN004>>() {

													@Override
													protected void OnSuccess(
															List<DepotDN004> response) {
														super.OnSuccess(response);
														List<DepotDN004> depotList = response;
														if (depotList != null
																&& depotList
																		.size() > 0) {

															DepotDN004 depot004 = depotList
																	.get(0);
															setSpinnerItemSelectedByValue(
																	spMeterModel,
																	depot004.getModeMeasure()); // 测量模式
															setSpinnerItemSelectedByValue(
																	spPackUnit,
																	depot004.getPacking());// 包装
															editDock.setText(depot004
																	.getWorkerID()); // 装卸工

															//setSpinnerItemSelectedByValue(spTruck,depot004.getTruckID());// 铲车工

															spTruck.setText(depot004.getTruckName());
															selTruckId = depot004.getTruckID();
															selPos = depot004.getPos();
															editPos.setText(depot004
																	.getPos());
															editStore
																	.setText(depot004
																			.getLocation());
														}
													}
												}, true);
								// end zxx modify

							}

							if (type.equals("1") || type.equals("2")
									|| type.equals("5") || type.equals("7")
									|| type.equals("8") || type.equals("11")
									// add by yxq 2014/09/16 begin
									|| type.equals("12")   
									// add by yxq 2014/09/16 end
									// add by yxq 2014/10/20 begin
									|| type.equals("13") 
									// add by yxq 2014/10/20 end
									// add by yxq 2014/10/30 begin
									|| type.equals("14") ||type.equals("15")
									// add by yxq 2014/10/30 end
									) {
								if (depot.getNoCarLice() != null
										&& depot.getNoCarLice().length() > 1) {
									editCarArea.setText(depot.getNoCarLice()
											.substring(0, 1)); // 车牌简称
									selCarArea = depot.getNoCarLice()
											.substring(0, 1);
									editCarNo.setText(depot.getNoCarLice()
											.substring(
													1,
													depot.getNoCarLice()
															.length())); // 车牌号
								}
								/* 注销 by yxq 2014/09/03
								editSameCarNo.setText(depot.getNoCarNum());// 同车编号
								// add by yxq 2014/08/24 begin
								textScanedNum.setText(String.format(getString(
										      R.string.DN004_029_MSG),GetSameCarNo()));
								// add by yxq 2014/08/24 end
								*/
								// add by yxq 2014/09/03 begin
								// add by yxq 2014/09/03 begin
								if(!(type.equals("7")||type.equals("8")||type.equals("11"))){
								// add by yxq 2014/09/03 end
									editNoMultipleDepot.setText(depot.getNoMultipleDepot()); // 进仓分票号
									editNoMultipleColo.setText(depot.getNoMultipleColo());
								}
								// add by yxq 2014/09/03 end
								editLong.setText(String.valueOf(depot
										.getNoLen()));// 长
								editWide.setText(String.valueOf(depot
										.getNoWidth()));// 宽
								editHigh.setText(String.valueOf(depot
										.getNoHeight()));// 高
								editNumber.setText(String.valueOf(depot
										.getDepotNum()));// 件数
								editArgueNumber.setText(String.valueOf(depot
										.getBillingNum()));// 计费件数
								editWeight.setText(ChangeDouble(depot.getKgs(),
										3, 3).replace(",", ""));// 重量
								editVolume.setText(ChangeDouble(depot.getCbm(),
										4, 4).replace(",", ""));// 体积
								txtRemark.setText(depot.getDepotRemark());// 进仓备注

								editDock.setText(depot.getWorkerID()); // 装卸工
								loadID1 = depot.getLoadID1();// 理货员
								loadID2 = depot.getLoadID2(); // 装卸ID
								loadID3 = depot.getTruckLoadID(); // 铲车工

								if (type.equals("1") || type.equals("2")
										|| type.equals("5")
										// add by yxq 2014/09/16 begin
										|| type.equals("12")   
										// add by yxq 2014/09/16 end
										// add by yxq 2014/10/20 begin
										|| type.equals("13")   
										// add by yxq 2014/10/20 end
										// add by yxq 2014/10/30 begin
										|| type.equals("14") ||type.equals("15")
										// add by yxq 2014/10/30 end
									) {
									//setSpinnerItemSelectedByValue(spTruck,depot.getTruckID());// 铲车工
									spTruck.setText(depot.getTruckName());
									selTruckId = depot.getTruckID();
									editPos.setText(depot.getPos());
									editStore.setText(depot.getLocation());
									selPos = depot.getPos();
								}

								/* 注销  by yxq 2014/09/03
								// 分票号
								String strnoMultipleOrder = depot
										.getNoMultipleOrder();
								if (strnoMultipleOrder != null) {
									if (strnoMultipleOrder.contains("-")) {
										setSpinnerItemSelectedByValue(
												spTicket1,
												strnoMultipleOrder.split("-")[0]);
										selTick = strnoMultipleOrder.split("-")[1];
									} else {
										setSpinnerItemSelectedByValue(
												spTicket1, strnoMultipleOrder);
										selTick = "";
									}
								}*/
								setSpinnerItemSelectedByValue(spCarType,
										depot.getTypeCar());// 车辆类型
								setSpinnerItemSelectedByValue(spMeterModel,
										depot.getModeMeasure()); // 测量模式
								setSpinnerItemSelectedByValue(spPackUnit,
										depot.getPacking());// 包装
							}

							if (type.equals("6") || type.equals("9")
									|| type.equals("10")) {
								setSpinnerItemSelectedByValue(spMeterModel,
										String.valueOf(va003DepotDt
												.getModeMeasure())); // 测量模式
								setSpinnerItemSelectedByValue(spPackUnit,
										va003DepotDt.getPacking());// 包装
							}

							// 库区赋值
							// if (type.equals("3") || type.equals("6")
							// || type.equals("7") || type.equals("8")
							// || type.equals("9") || type.equals("10")
							// || type.equals("11")) {
							if (selPos != null
									&& selPos.length() > 0
									&& editPos.getText().toString().trim()
											.isEmpty()) {
								editPos.setText(selPos);
							}
							if(selTruckId != null 
									&& selTruckId.length() > 0
									&& spTruck.getText().toString().trim()
											.isEmpty()){
								spTruck.setText(depot.getTruckName());
							}
							// }
							if (selPackType == null
									|| selPackType.length() == 0) {
								selPackType = depot.getPackType();
							}
							
							// add by yxq 2014/09/24 begin
							if(type.equals("12")){
								if("0".equals(depot.getTypeReaMea())){        // 复量正确
									chkReaMeaOK.setChecked(true);
									chkReaMeaSize.setChecked(false);
								}else if("1".equals(depot.getTypeReaMea())){  // 复量尺寸
									chkReaMeaOK.setChecked(false);
									chkReaMeaSize.setChecked(true);
								}else{
									chkReaMeaOK.setChecked(false);
									// edit by yxq 2014/11/04 begin
									// 原代码 chkReaMeaSize.setChecked(true);
									chkReaMeaSize.setChecked(false);
									// edit by yxq 2014/11/04 end
								}
							}
							// add by yxq 2014/09/24 end
							

							ShowPackType();
						}
					}, true);
		} catch (Exception e) {
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
								selPackType.split(",")[i].trim())) {
							selContent += ((SelectDict) packTypeList.get(k))
									.getName() + ",";
							break;
						}
					}
				}
			} else {
				for (int k = 0; k < packTypeList.size(); k++) {
					if (((SelectDict) packTypeList.get(k)).getId().equals(
							selPackType.trim())) {
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
			e.printStackTrace();
		}
	}

	
	/*
	 * 同车编号提示框可清空
	 */
	/* 注销 by yxq 2014/09/03 
	private void ShowEditText() {
		final String[] arySameCarNO = editSameCarNo.getText().toString().trim()
				.split(",");

		// 删除的同车编号
		final List medicSelec = new ArrayList();

		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light))
				.setTitle(R.string.DN004_SAMECAR_TITLE).setCancelable(false);

		builder.setMultiChoiceItems(arySameCarNO, null,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						if (isChecked) {
							medicSelec.add(which);
						} else if (medicSelec.contains(which)) {
							medicSelec.remove(Integer.valueOf(which));
						}
					}
				});

		// 取消
		builder.setNegativeButton(getString(R.string.button_no), null);

		// 删除
		builder.setPositiveButton(getString(R.string.button_delete),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String str = "";
						for (int i = 0; i < arySameCarNO.length; i++) {
							boolean flag = true;
							for (Object sel : medicSelec) {
								if (i == Integer.valueOf(sel.toString())) {
									flag = false;
									break;
								}
							}
							if (flag) {
								str += arySameCarNO[i] + ",";
							}
						}

						if (str.length() > 0) {
							str = str.substring(0, str.length() - 1);
						}

						editSameCarNo.setText(str);
						// add by yxq 2014/08/24 begin
						textScanedNum.setText(String.format(getString(
								      R.string.DN004_029_MSG),GetSameCarNo()));
						// add by yxq 2014/08/24 end
					}
				}).show();
	}
	 */

	/*
	 * 禁止显示软键盘
	 */
	private void hideSoftInputMode(EditText editText) {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(editText.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/*
	 * 绑定分票号下拉框
	 */
	/*
	private void InitTicketList() {
		try {
			String ticket[] = new String[9];
			for (int i = 0; i < 9; i++) {
				ticket[i] = String.valueOf(i + 1);
			}

			List<SelectDict> listData = new ArrayList<SelectDict>();
			for (String str : ticket) {
				SelectDict se = new SelectDict();
				se.setId(str);
				se.setName(str);
				listData.add(se);
			}

			ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
					_thisActivity, android.R.layout.simple_spinner_item,
					listData);
			selectList
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spTicket1.setAdapter(selectList);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		spTicket1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					if (position == 0) {
						editSameCarNo.setText("");
						// add by yxq 2014/08/26 begin
						textScanedNum.setText(String.format(getString(
							      R.string.DN004_029_MSG),GetSameCarNo()));
						// add by yxq 2014/08/26 end
					}
					注销 by yxq 2014/08/26 begin
					String t[] = new String[position + 1];
					for (int i = 0; i < position + 1; i++) {
						t[i] = String.valueOf(i + 1);
					}

					List<SelectDict> listData = new ArrayList<SelectDict>();
					for (int i = 0; i < t.length; i++) {
						SelectDict se = new SelectDict();
						se.setId(t[i]);
						se.setName(t[i]);
						listData.add(se);
					}

					ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
							_thisActivity,
							android.R.layout.simple_spinner_item, listData);
					selectList
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spTicket2.setAdapter(selectList);

					// 分票号2初期赋值
					if (!selTick.equals("")) {
						setSpinnerItemSelectedByValue(spTicket2, selTick);
					}
					注销 by yxq 2014/08/26 end
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
    */

	/*
	 * 创建右上角按钮
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// 除查看以外的显示拍照按钮
		/*	注销 by yxq 2014/09/03	
		if (!type.equals("5") && !type.equals("8") && !type.equals("9")) {
			// Inflate the menu items for use in the action bar
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.common_scanandprint, menu);
		} else {*/
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.selprintcommon, menu);
       //}
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * 扫描事件
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/* 注销 by yxq 2014/09/03
		case R.id.scan_action:
			SelectDict spTicketItem = (SelectDict) spTicket1.getSelectedItem();
			if (spTicketItem.getId().equals("1")
					|| String.valueOf(GetSameCarNo() + 1).equals(
							spTicketItem.getId())) {
				return false;
			}

			// 扫描
			Intent intent = new Intent(this, CaptureActivity.class);
			intent.putExtra("SCAN_MODE", "SCAN_MODE");
			startActivityForResult(intent, 2);
			return true;
		 */	
		case R.id.print_action:
			SelectPrintDialog dlg = new SelectPrintDialog(1, this);
			dlg.createDialog(this).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * 返回VA003时的确认提示
	 */
	private boolean ReurnCheckData(String msg) {
		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setMessage(msg)
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								setResult(Activity.RESULT_OK, null);
								_thisActivity.finish();
							}
						})
				.setNegativeButton(getString(R.string.button_no), null).show();
		return false;
	}

	/*
	 * 获取返回VA003数据
	 */
	private Intent ReturnIntent() {
		DN004TDepotDt depotDt = new DN004TDepotDt();
		// add by yxq 2014/10/15 begin
		depotDt.setNoBatch(Integer.parseInt(noBatch));
		// add by yxq 2014/10/15 end
		depotDt.setNoLen(ReturnFloat(editLong.getText().toString().trim()));// 长
		depotDt.setNoWidth(ReturnFloat(editWide.getText().toString().trim()));// 宽
		depotDt.setNoHeight(ReturnFloat(editHigh.getText().toString().trim()));// 高
		depotDt.setDepotNum(ReturnInt(editNumber.getText().toString().trim()));// 进仓件数
		depotDt.setBillingNum(ReturnInt(editArgueNumber.getText().toString().trim()));// 计费件数
		SelectDict spPack = (SelectDict) spPackUnit.getSelectedItem();
		depotDt.setPacking(spPack.getId());// 包装
		depotDt.setPackType(selPackType); // 包装类型
		depotDt.setKgs(ReturnFloat(editWeight.getText().toString().trim()));// 重量
		depotDt.setCbm(ReturnFloat(editVolume.getText().toString().trim()));// 体积
		depotDt.setDepotRemark(txtRemark.getText().toString().trim());// 进仓备注
		SelectDict meterModel = (SelectDict) spMeterModel.getSelectedItem();
		depotDt.setModeMeasure(ReturnInt(meterModel.getId()));// 测量模式
		depotDt.setPos(selPos); // 库区
		String location = editStore.getText().toString().trim();
		if (location.length() == 1) {
			depotDt.setLocation("0" + editStore.getText().toString().trim()); // 库位
		} else {
			depotDt.setLocation(editStore.getText().toString().trim()); // 库位
		}
		
		// add by yxq 2014/10/16 begin
		depotDt.setNoMultipleDepot(editNoMultipleDepot.getText().toString());
		// add by yxq 2014/10/16 end
		
		// add by yxq 2014/11/10 begin
		// 测量模式备注
		if (oprAdapter.getCount() > 0) {
			strModeRemark = "";
			for (int i = 0; i < oprAdapter.getCount(); i++) {
				DepotDN004 dn004Data = (DepotDN004) oprAdapter.getItem(i);
				strModeRemark += doubleTrans(dn004Data.getNoLen()) + "*"
						+ doubleTrans(dn004Data.getNoWidth()) + "*"
						+ doubleTrans(dn004Data.getNoHeight()) + "*"
						+ dn004Data.getDepotNum() +"/"
						+ dn004Data.getPackType() + ";";
			}
		}
		if (strModeRemark.length() > 0) {
			strModeRemark = strModeRemark.substring(0,
					strModeRemark.length() - 1);
			strModeRemark = strModeRemark + "。  ";
		}
		depotDt.setModeRemark(strModeRemark);
		// add by yxq 2014/11/10 end
        depotDt.setNoMultipleColo(editNoMultipleColo.getText().toString());
		DN004TLoad tLoad = new DN004TLoad();
		tLoad.setWorkerID(editDock.getText().toString().trim());// 装卸工编号

		Intent retIntent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("DN004TDepotDt", depotDt);
		bundle.putSerializable("DN004TLoad", tLoad);
		// add by yxq 2014/09/24 begin
		bundle.putSerializable("Copies", editCopies_1.getText().toString().trim());
		// add by yxq 2014/09/24 end
		retIntent.putExtras(bundle);
		return retIntent;
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
		/* 注销 by yxq 2014/09/03
		case 2:
			// 扫描成功
			barCode = data.getStringExtra("SCAN_RESULT");

			if (barCode.isEmpty()) {
				return;
			}

			final BarCode01 barCode01 = new BarCode01();
			boolean result01 = barCode01.paserBarCode(data
					.getStringExtra("SCAN_RESULT"));

			if (!result01) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN001_001_MSG));
				return;
			}

			// 验证并显示同车编号
			CheckSameCarNo(barCode01.getOrderCd());
			break;
			*/
		}
	}

	/*
	 * 验证并显示同车编号
	 */
	 /* 注销 by yxq 2014/09/09
	private void CheckSameCarNo(String OrderCd) {
		try {
			Map<String, Object> p1 = new HashMap<String, Object>();
			p1.put("cdOrder", OrderCd);

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"DN004_CheckCdOrder", p1, new TypeToken<DepotDN001>() {
					}.getType(), new AmassHttpResponseHandler<DepotDN001>() {

						@Override
						protected void OnSuccess(DepotDN001 response) {
							super.OnSuccess(response);
							DepotDN001 depot = (DepotDN001) response;

							if (depot != null) {
								// 扫描到的进仓编号与页面进仓编号相同
								if (depot.getCdOrderPublic().equals(
										cdOrderPublic)) {
									return;
								}

								// 页面同车编号
								String sameCarNoString = editSameCarNo
										.getText().toString();

								if (sameCarNoString.length() == 0) {
									editSameCarNo.setText(depot
											.getCdOrderPublic());
								} else {
									if (sameCarNoString.indexOf(depot
											.getCdOrderPublic()) > -1) {
										return;
									} else {
										editSameCarNo.setText(sameCarNoString
												+ ","
												+ depot.getCdOrderPublic());
									}
								}
								// add by yxq 2014/08/24 begin
								textScanedNum.setText(String.format(getString(
										      R.string.DN004_029_MSG),GetSameCarNo()));
								// add by yxq 2014/08/24 end
							} else {
								Utils.showAlertDialog(_thisActivity,
										getString(R.string.DN004_005_MSG));
								return;
							}
						}
					}, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
   */
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
		if (ReturnFloat(editWide.getText().toString().trim()) > 228) {
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
		if (type.equals("0") || type.equals("2")) {
			if (saveFlag) {
				// 更新
				DoUpdate();
			} else {
				DoSave();
			}
		} else if (type.equals("1")) {
			// 更新
			DoUpdate();
		} 
		// add by yxq 2014/09/17 begin
		else if (type.equals("12")) {
			DoUpdate_ReaMea();    // 保存复量的数据
		}
		// add by yxq 2014/09/17 end
		// add by yxq 2014/10/20 begin
		else if (type.equals("13")) {
			DoUpdate_ChangePack();    // 保存更改包装的数据
		}
		// add by yxq 2014/10/20 end
		else if (type.equals("14")||type.equals("15")){
			DoUpdate_Va003SaveCommon();   // 保存更改件数或称重的数据
		}
	}

	/*
	 * 入力验证
	 */
	private boolean DoCheck() {
		try {
			// add by yxq 2014/10/30 begin
			// 更改件数
			// 件数
			if(type.equals("14")){   // 更改件数
				// 更改件数
				if (editNumber.getText().toString().trim().length() == 0) {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN004_017_MSG)
									+ getString(R.string.DN004_001_MSG));
					return false;
				}
				return true;
			}else if(type.equals("15")) // 称重
			{
				// 重量
				if (editWeight.getText().toString().trim().length() == 0) {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN004_032_MSG)
									+ getString(R.string.DN004_001_MSG));
					return false;
				}
				return true;
			}
			
			// add by yxq 2014/10/30 end
			if (!type.equals("3") && !type.equals("6") && !type.equals("7")
					&& !type.equals("10") && !type.equals("11")
					// add by yxq 2014/09/16 begin
					&& !type.equals("12")
					// add by yxq 2014/09/16 end
					// add by yxq 2014/10/20 end
					&& !type.equals("13")   
					// add by yxq 2014/10/20 end
				) {
				// 车牌简称
				if (editCarArea.getText().toString().trim().length() == 0) {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN004_010_MSG)
									+ getString(R.string.DN004_001_MSG));
					return false;
				}

				// 车牌号
				if (editCarNo.getText().toString().trim().length() == 0) {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN004_011_MSG)
									+ getString(R.string.DN004_001_MSG));
					return false;
				}

				// 车辆类型
				SelectDict carType = (SelectDict) spCarType.getSelectedItem();
				if (carType.getId().length() == 0) {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN004_012_MSG)
									+ getString(R.string.DN004_001_MSG));
					return false;
				}

				/* 注销 by yxq 2014?09/03
				// 判断是否需要扫描同车编号
				SelectDict tick1 = (SelectDict) spTicket1.getSelectedItem();
				if (!tick1.getId().equals("1")
						&& editSameCarNo.getText().toString().trim().length() == 0) {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN004_002_MSG));
					return false;
				}

				// 判断扫描的同车编号的个数
				if (Integer.parseInt(tick1.getId()) > 1
						&& GetSameCarNo() != Integer.parseInt(tick1.getId()) - 1) {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN004_006_MSG));
					return false;
				}
				*/
			}

			// add by yxq 2014/09/16 begin
			if(!type.equals("12")
			  // add by yxq 2014/10/20 begin
			  && !type.equals("13")
			  // add by yxq 2014/10/20 end
			  ){
			// add by yxq 2014/09/16 end
			   // 测量模式
			   SelectDict meterModel = (SelectDict) spMeterModel.getSelectedItem();
				if (meterModel.getId().length() == 0) {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN004_013_MSG)
									+ getString(R.string.DN004_001_MSG));
					return false;
				}
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
			
			// add by yxq 2014/10/20 begin
			if(type.equals("13")||type.equals("12")){   // 如果是更改包装或者复量，下面的均不需检查
				return true;
			}
			// add by yxq 2014/10/20 end

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

			//包装类型
			if (editPackType.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_PACK_TYPE_SEC)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}
			// 装卸工
			if (editDock.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_021_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			// add by yxq 2014/09/10 begin
			// edit by yxq 2014/10/08 begin
			// 原代码 if (editDock.getText().toString().trim().length() != 3) {
			if (editDock.getText().toString().trim().length() % 3 != 0) {	
			// 搬运工必须是三的倍数
				Utils.showAlertDialog(_thisActivity,getString(R.string.DN004_021_MSG)
											         + getString(R.string.DN004_030_MSG));
				return false;
			}
			// add by yxq 2014/09/10 end
			
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

			// 铲车工
			if (type.equals("0") || type.equals("1") || type.equals("2")) {
				// 测量模式
				//SelectDict truck = (SelectDict) spTruck.getSelectedItem();
				//if (truck.getId().length() == 0) {
				if(spTruck.getText().toString().trim().length() == 0){
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN004_028_MSG)
									+ getString(R.string.DN004_001_MSG));
					return false;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/*
	 * 获取同车编号个数
	 */
	/* 注销  by yxq 2014/09/03
	private int GetSameCarNo() {
		String sameCarNo = editSameCarNo.getText().toString().trim();
		if (sameCarNo.length() > 0) {
			if (sameCarNo.contains(",")) {
				return sameCarNo.split(",").length;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}
	*/

	/*
	 * 新增
	 */
	public void DoSave() {
		try {
			DN004TDepotDt depotDt = new DN004TDepotDt();
			depotDt.setDepotID(depotID); // 进仓ID
			depotDt.setNoBatch(Integer.parseInt(noBatch)); // 批次
			depotDt.setNoLen(ReturnFloat(editLong.getText().toString().trim()));// 长
			depotDt.setNoWidth(ReturnFloat(editWide.getText().toString().trim()));// 宽
			depotDt.setNoHeight(ReturnFloat(editHigh.getText().toString()
					.trim()));// 高
			depotDt.setDepotNum(ReturnInt(editNumber.getText().toString()
					.trim()));// 进仓件数
			depotDt.setBillingNum(ReturnInt(editArgueNumber.getText()
					.toString().trim()));// 计费件数
			SelectDict spPack = (SelectDict) spPackUnit.getSelectedItem();
			depotDt.setPacking(spPack.getId());// 包装
			depotDt.setPackType(selPackType);// 包装类型
			depotDt.setKgs(ReturnFloat(editWeight.getText().toString().trim()));// 重量
			depotDt.setCbm(ReturnFloat(editVolume.getText().toString().trim()));// 体积
			/* 注销 by yxq 2014/09/03
			depotDt.setNoCarNum(editSameCarNo.getText().toString().trim());// 同车编号
			*/
			// add by yxq 2014/09/03 begin
			depotDt.setNoMultipleDepot(editNoMultipleDepot.getText().toString()); // 进仓分票号
			// add by yxq 2014/09/03 end
			depotDt.setNoMultipleColo(editNoMultipleColo.getText().toString());
			depotDt.setNoCarLice(editCarArea.getText().toString().trim()
					+ editCarNo.getText().toString().toUpperCase());// 车牌号
			depotDt.setDepotRemark(txtRemark.getText().toString().trim());// 进仓备注
			/* 注销 by yxq 2014/09/03
			SelectDict tick1 = (SelectDict) spTicket1.getSelectedItem();
			// edit by yxq 2014/08/26 begin
			 原代码 begin
			SelectDict tick2 = (SelectDict) spTicket2.getSelectedItem();
			depotDt.setNoMultipleOrder(tick1.getId() + "-" + tick2.getId());// 分票号
			 原代码 end
			depotDt.setNoMultipleOrder(tick1.getId());// 总票号
			// edit by yxq 2014/08/26 end
			*/
			
			SelectDict carType = (SelectDict) spCarType.getSelectedItem();
			depotDt.setTypeCar(ReturnInt(carType.getId()));// 车辆类型
			SelectDict meterModel = (SelectDict) spMeterModel.getSelectedItem();
			depotDt.setModeMeasure(ReturnInt(meterModel.getId()));// 测量模式
			depotDt.setPos(selPos); // 库区
			String location = editStore.getText().toString().trim();
			if (location.length() == 1) {
				depotDt.setLocation("0" + editStore.getText().toString().trim()); // 库位
			} else {
				depotDt.setLocation(editStore.getText().toString().trim()); // 库位
			}
			if (oprAdapter.getCount() > 0) {
				strModeRemark = "";
				//strModeRemark += "长/宽/高/件数/包装类型：";
				for (int i = 0; i < oprAdapter.getCount(); i++) {
					DepotDN004 dn004Data = (DepotDN004) oprAdapter.getItem(i);
					strModeRemark += doubleTrans(dn004Data.getNoLen()) + "*"
							+ doubleTrans(dn004Data.getNoWidth()) + "*"
							+ doubleTrans(dn004Data.getNoHeight()) + "*"
							+ dn004Data.getDepotNum() +"/"
							+ dn004Data.getPackType() + ";";
				}
			}
			if (strModeRemark.length() > 0) {
				strModeRemark = strModeRemark.substring(0,
						strModeRemark.length() - 1);
				strModeRemark = strModeRemark + "。  ";
			}
			depotDt.setModeRemark(strModeRemark);

			DN004TLoad tLoad = new DN004TLoad();
			tLoad.setDepotID(depotID);// 进仓ID
			tLoad.setNoBatch(ReturnInt(noBatch));// 批次
			tLoad.setWorkerID(editDock.getText().toString().trim());// 员工编号			

            tLoad.setTruckID(selTruckId); // 铲车工ID
			tLoad.setTruckNM(spTruck.getText().toString()); // 铲车工姓名
			//SelectDict truck = (SelectDict) spTruck.getSelectedItem();
			//tLoad.setTruckID(truck.getId()); // 铲车工ID
			//tLoad.setTruckNM(truck.getName()); // 铲车工姓名

			// edit by yxq 2014/08/26 begin
			/* 原代码
			CheckIsUpdateCarMessage(carType.getId(), tick1.getId(),tick2.getId());
			*/
			// edit by yxq 2014/09/03 begin
			// 原代码 CheckIsUpdateCarMessage(carType.getId(), tick1.getId());
			CheckIsUpdateCarMessage(carType.getId());
			// edit by yxq 2014/09/03 end 
			// edit by yxq 2014/08/26 end
			

			btnSave.setVisibility(View.GONE);
			Map<String, Object> p1 = new HashMap<String, Object>();
			p1.put("depotDt", depotDt);
			p1.put("tLoad", tLoad);
			p1.put("updateFlag", updateFlag);
			p1.put("AddType", AddType);
			p1.put("cdOrder", cdOrderPublic);
			p1.put("Copies", editCopies.getText().toString().trim());

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"DN004_SaveData", p1, new TypeToken<List<DepotDN004>>() {
					}.getType(),
					new AmassHttpResponseHandler<List<DepotDN004>>() {

						@Override
						protected void OnSuccess(List<DepotDN004> response) {
							super.OnSuccess(response);
							List<DepotDN004> dn = (List<DepotDN004>) response;
							strModeRemark = "";
							if (dn != null && dn.size() > 0) {
								reDepotList = dn;
								depotDtID = dn.get(0).getDepotDtID();
								txtNoPilecard
										.setText(dn.get(0).getNoPilecard());
								loadID1 = dn.get(0).getLoadID1(); // 理货员
								loadID2 = dn.get(0).getLoadID2(); // 搬运工
								loadID3 = dn.get(0).getTruckLoadID(); // 铲车工
								btnSave.setVisibility(View.VISIBLE);
								saveFlag = true;
								// layCopies.setVisibility(View.GONE);
								editCopies.setEnabled(false);
								btnError.setEnabled(true);
								Utils.showAlertDialog(_thisActivity,
										getString(R.string.msg_save_success));
							}
						}
					}, true);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 更新
	 */
	private void DoUpdate() {
		try {
			DepotDN004 depotDt = new DepotDN004();
			depotDt.setDepotDtID(depotDtID); // 货物明细ID
			depotDt.setDepotID(depotID); // 进仓ID
			depotDt.setNoBatch(Integer.parseInt(noBatch)); // 批次
			depotDt.setNoLen(ReturnFloat(editLong.getText().toString().trim()));// 长
			depotDt.setNoWidth(ReturnFloat(editWide.getText().toString().trim()));// 宽
			depotDt.setNoHeight(ReturnFloat(editHigh.getText().toString()
					.trim()));// 高
			depotDt.setDepotNum(ReturnInt(editNumber.getText().toString()
					.trim()));// 进仓件数
			depotDt.setBillingNum(ReturnInt(editArgueNumber.getText()
					.toString().trim()));// 计费件数
			SelectDict spPack = (SelectDict) spPackUnit.getSelectedItem();
			depotDt.setPacking(spPack.getId());// 包装
			depotDt.setPackType(selPackType);// 包装类型
			depotDt.setKgs(ReturnFloat(editWeight.getText().toString().trim()));// 重量
			depotDt.setCbm(ReturnFloat(editVolume.getText().toString().trim()));// 体积
			/* 注销  by  yxq 2014/09/03 
			depotDt.setNoCarNum(editSameCarNo.getText().toString().trim());// 同车编号
			*/
			// add by yxq 2014/09/03 begin
			depotDt.setNoMultipleDepot(editNoMultipleDepot.getText().toString()); // 进仓分票号
			// add by yxq 2014/09/03 end
			depotDt.setNoMultipleColo(editNoMultipleColo.getText().toString());
			depotDt.setNoCarLice(editCarArea.getText().toString().trim()
					+ editCarNo.getText().toString().toUpperCase());// 车牌号
			depotDt.setDepotRemark(txtRemark.getText().toString().trim());// 进仓备注
			/* 注销 by yxq 2014/09/03
			SelectDict tick1 = (SelectDict) spTicket1.getSelectedItem();
			// edit  by yxq 2014/08/28 begin
			 原代码  begin
			SelectDict tick2 = (SelectDict) spTicket2.getSelectedItem();
			depotDt.setNoMultipleOrder(tick1.getId() + "-" + tick2.getId());// 分票号
			 原代码  end			
			depotDt.setNoMultipleOrder(tick1.getId());// 总票数
			// edit  by yxq 2014/08/28 end
			 */
			SelectDict carType = (SelectDict) spCarType.getSelectedItem();
			depotDt.setTypeCar(carType.getId());// 车辆类型
			SelectDict meterModel = (SelectDict) spMeterModel.getSelectedItem();
			depotDt.setModeMeasure(meterModel.getId());// 测量模式

			depotDt.setPos(selPos); // 库区
			String location = editStore.getText().toString().trim();
			if (location.length() == 1) {
				depotDt.setLocation("0" + editStore.getText().toString().trim()); // 库位
			} else {
				depotDt.setLocation(editStore.getText().toString().trim()); // 库位
			}

			if (oprAdapter.getCount() > 0) {
				strModeRemark = "";
				//strModeRemark += "长/宽/高/件数/包装类型：";
				for (int i = 0; i < oprAdapter.getCount(); i++) {
					DepotDN004 dn004Data = (DepotDN004) oprAdapter.getItem(i);
					strModeRemark += doubleTrans(dn004Data.getNoLen()) + "*"
							+ doubleTrans(dn004Data.getNoWidth()) + "*"
							+ doubleTrans(dn004Data.getNoHeight()) + "*"
							+ dn004Data.getDepotNum() +"/"
							+ dn004Data.getPackType() + ";";
				}
			}
			if (strModeRemark.length() > 0) {
				strModeRemark = strModeRemark.substring(0,
						strModeRemark.length() - 1);
				strModeRemark = strModeRemark + "。  ";
			}
			depotDt.setModeRemark(strModeRemark);

			depotDt.setLoadID1(loadID1);
			depotDt.setLoadID2(loadID2);
			depotDt.setTruckLoadID(loadID3);

			DN004TLoad tLoad = new DN004TLoad();
			tLoad.setDepotID(depotID);// 进仓ID
			tLoad.setNoBatch(ReturnInt(noBatch));// 批次
			tLoad.setWorkerID(editDock.getText().toString().trim());// 员工编号
			/*SelectDict truck = (SelectDict) spTruck.getSelectedItem();
			tLoad.setTruckID(truck.getId()); // 铲车工ID
			tLoad.setTruckNM(truck.getName()); // 铲车工姓名
			*/			
            tLoad.setTruckID(selTruckId); // 铲车工ID
			tLoad.setTruckNM(spTruck.getText().toString()); // 铲车工姓名
			// edit by yxq 2014/08/26 begin
			/* 原代码
			CheckIsUpdateCarMessage(carType.getId(), tick1.getId(),
					tick2.getId());
			*/
			// edit by yxq 2014/09/03 begin
			// 原代码  CheckIsUpdateCarMessage(carType.getId(), tick1.getId());
			CheckIsUpdateCarMessage(carType.getId());
			// edit by yxq 2014/09/03 end
			// edit by yxq 2014/08/26 end

			Map<String, Object> p1 = new HashMap<String, Object>();
			p1.put("depotDt", depotDt);
			p1.put("tLoad", tLoad);
			p1.put("updateFlag", updateFlag);

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"DN004_UpdateData", p1, new TypeToken<DN004TDepotDt>() {
					}.getType(), new AmassHttpResponseHandler<DN004TDepotDt>() {

						@Override
						protected void OnSuccess(DN004TDepotDt response) {
							super.OnSuccess(response);
							strModeRemark = "";
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.msg_save_success));
							saveFlag = true;
						}
					}, true);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 更新 (复量)
	 */
	// add by yxq 2014/09/17
	private void DoUpdate_ReaMea(){
		DepotDN004 depotDt = new DepotDN004();
		depotDt.setDepotDtID(depotDtID); // 货物明细ID
		depotDt.setNoLen(ReturnFloat(editLong.getText().toString().trim()));   // 长
		depotDt.setNoWidth(ReturnFloat(editWide.getText().toString().trim())); // 宽
		depotDt.setNoHeight(ReturnFloat(editHigh.getText().toString().trim()));// 高
		depotDt.setDepotNum(ReturnInt(editNumber.getText().toString().trim()));// 进仓件数
		depotDt.setBillingNum(ReturnInt(editArgueNumber.getText().toString().trim()));// 计费件数
		// add by yxq 2014/09/24 begin
		depotDt.setTypeReaMea(chkReaMeaOK.isChecked()?"0":"1");                // 复量尺寸
		// add by yxq 2014/09/24 end
		// add by yxq 2014/10/28 begin
		depotDt.setCbm(ReturnFloat(editVolume.getText().toString().trim()));   // 体积
		// add by yxq 2014/10/28 end
		// add by yxq 2014/11/10 begin
		// 测量模式
		SelectDict meterModel = (SelectDict) spMeterModel.getSelectedItem();
		depotDt.setModeMeasure(meterModel.getId());
		// 测量模式备注
		if (oprAdapter.getCount() > 0) {
			strModeRemark = "";
			for (int i = 0; i < oprAdapter.getCount(); i++) {
				DepotDN004 dn004Data = (DepotDN004) oprAdapter.getItem(i);
				strModeRemark += doubleTrans(dn004Data.getNoLen()) + "*"
						+ doubleTrans(dn004Data.getNoWidth()) + "*"
						+ doubleTrans(dn004Data.getNoHeight()) + "*"
						+ dn004Data.getDepotNum() +"/"
						+ dn004Data.getPackType() + ";";
			}
		}
		if (strModeRemark.length() > 0) {
			strModeRemark = strModeRemark.substring(0,
					strModeRemark.length() - 1);
			strModeRemark = strModeRemark + "。  ";
		}
		depotDt.setModeRemark(strModeRemark);
		// add by yxq 2014/11/10 end
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("depotDt", depotDt);
		p.put("strServiceId", strServiceId);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"DN004_UpdateData_ReaMea", p, 
			String.class,new AmassHttpResponseHandler<String>() {
				@Override
				protected void OnSuccess(String response) {
					super.OnSuccess(response);
					// add by yxq 2014/10/28 begin
					// va003画面的显示
					VA003PileCard itemVA003 = (VA003PileCard) SessionHelper.getInstance()
												.getObject("VA003_ReaMea");
					itemVA003.setCbm(editVolume.getText().toString().trim());
					itemVA003.setDepotNum(editNumber.getText().toString().trim());
					// add by yxq 2014/10/28 end
					// add by yxq 2014/10/28 begin
					if("0".equals(response)){           // 复量正确
						chkReaMeaOK.setChecked(true);
						chkReaMeaSize.setChecked(false);
					}else if("1".equals(response)){     // 复量尺寸
						chkReaMeaOK.setChecked(false);
						chkReaMeaSize.setChecked(true);
					}else{
						chkReaMeaOK.setChecked(false);
						chkReaMeaSize.setChecked(false);
					}
					// add by yxq 2014/10/28 end
					Utils.showAlertDialog(_thisActivity,getString(R.string.msg_save_success));
				}
			}, true);
	}

	/*
	 * 更新 (更改包装)
	 */
	// add by yxq 2014/10/20
	private void DoUpdate_ChangePack(){
		DepotDN004 depotDt = new DepotDN004();
		depotDt.setDepotDtID(depotDtID); // 货物明细ID
		depotDt.setNoLen(ReturnFloat(editLong.getText().toString().trim()));   // 长
		depotDt.setNoWidth(ReturnFloat(editWide.getText().toString().trim())); // 宽
		depotDt.setNoHeight(ReturnFloat(editHigh.getText().toString().trim()));// 高
		depotDt.setDepotNum(ReturnInt(editNumber.getText().toString().trim()));// 进仓件数
		depotDt.setBillingNum(ReturnInt(editArgueNumber.getText().toString().trim()));// 计费件数
		// add by yxq 2014/10/31 begin
		depotDt.setCbm(ReturnFloat(editVolume.getText().toString().trim()));   // 体积
		// add by yxq 2014/10/31 end
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("depotDt", depotDt);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"DN004_DoUpdate_ChangePack", p, 
			new TypeToken<DN004TDepotDt>() {}.getType(), 
			new AmassHttpResponseHandler<DN004TDepotDt>() {
				@Override
				protected void OnSuccess(DN004TDepotDt response) {
					super.OnSuccess(response);
					// va003画面的显示
					VA003PileCard itemVA003 = (VA003PileCard) SessionHelper.getInstance()
												.getObject("VA003_ChangePack");
					itemVA003.setDepotNum(editNumber.getText().toString().trim());
					itemVA003.setCbm(editVolume.getText().toString().trim());  // add by yxq 2014/10/31
					Utils.showAlertDialog(_thisActivity,getString(R.string.msg_save_success));
				}
			}, true);
	}
	
	// add by yxq 2014/10/30 begin
	private void DoUpdate_Va003SaveCommon(){
		DepotDN004 depotDt = new DepotDN004();
		depotDt.setDepotDtID(depotDtID); // 货物明细ID
		if(type.equals("14")){
		  depotDt.setDepotNum(ReturnInt(editNumber.getText().toString().trim()));// 进仓件数
		}else if(type.equals("15")){
		  depotDt.setKgs(ReturnFloat(editWeight.getText().toString().trim()));   // 重量
		}
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("depotDt", depotDt);
		p.put("type", type);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"DN004_DoUpdate_VA003SaveCommon",
			p, new TypeToken<DN004TDepotDt>() {}.getType(), 
			new AmassHttpResponseHandler<DN004TDepotDt>() {
				@Override
				protected void OnSuccess(DN004TDepotDt response) {
					super.OnSuccess(response);
					// va003画面的显示
					VA003PileCard itemVA003 = (VA003PileCard) SessionHelper.getInstance()
												.getObject("VA003_ChangPileCardCommon");
					if(type.equals("14")){
					  itemVA003.setDepotNum(editNumber.getText().toString().trim());
					}else if(type.equals("15")){
					  itemVA003.setKgs(editWeight.getText().toString().trim());
					}
					Utils.showAlertDialog(_thisActivity,getString(R.string.msg_save_success));
				}
			}, true);
	}
	// add by yxq 2014/10/30 end
	
	/*
	 * 判断车辆信息是否修改
	 */
	// edit by yxq 2014/08/28 begin
	/* 原代码
	private void CheckIsUpdateCarMessage(String typeID, String tick1,String tick2) {
	*/
	// edit yxq 2014/09/03 begin
	/* 原代码
	  private void CheckIsUpdateCarMessage(String typeID, String tick1) {
	 */
	private void CheckIsUpdateCarMessage(String typeID) {
	// edit by yxq 2014/08/28 end
		if (CarNo != null && CarType != null && !CarNo.equals("")
				&& !CarType.equals("")) {
			if (!(editCarArea.getText().toString().trim() + editCarNo.getText()
					.toString().trim()).equals(CarNo)
					|| !typeID.equals(CarType)
				/* 注销 by yxq 2014/09/03
				// edit by yxq 2014/08/28 begin
				   原代码 begin
					|| !(tick1 + "-" + tick2).equals(NoMultipleOrder)
				   原代码 end
					|| !(tick1).equals(NoMultipleOrder)
				// edit by yxq 2014/08/28 end	
					|| !editSameCarNo.getText().toString().trim()
							.equals(NoCarNum)*/ 
				){
				
				updateFlag = true;
			}
		} else {
			updateFlag = true;
		}
		
		// add by yxq 2014/09/03 begin
		if(NoMultipleDepot!=null && !NoMultipleDepot.equals("")){
			if(!NoMultipleDepot.equals(editNoMultipleDepot.getText().toString())){
				updateFlag = true;
			}
		}else{
			updateFlag = true;
		}
		// add by yxq 2014/09/03 end
		if(NoMultipleColo!=null && !NoMultipleColo.equals("")){
			if(!NoMultipleColo.equals(editNoMultipleColo.getText().toString())){
				updateFlag = true;
			}
		}else{
			updateFlag = true;
		}
	}

	/*
	 * 桩脚牌打印按钮事件
	 */
	public void Btn_Print_OnClick(View view) {
		try {
			if (type.equals("0") || type.equals("2")) {
				if (saveFlag) {
					DoPrint();
				} else {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN004_003_MSG));
				}
			} else {
				DoPrint();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 打印
	 */
	private void DoPrint() {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotDtId", depotDtID);
		p1.put("cdOrder", cdOrder);
		p1.put("cdOrderPublic", cdOrderPublic);
		p1.put("coLoader", thNo);
		p1.put("reDepotList", reDepotList);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN004_PrintPileCardReport", p1,
				new TypeToken<DN004TDepotDt>() {
				}.getType(), new AmassHttpResponseHandler<DN004TDepotDt>() {

					@Override
					protected void OnSuccess(DN004TDepotDt response) {
						super.OnSuccess(response);
						Utils.showAlertDialog(_thisActivity,
								getString(R.string.msg_common_print_success));
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
			if (type.equals("3") || type.equals("10") || type.equals("11")) {
				ReurnCheckData(getString(R.string.DN004_RETURN_MSG1));
			} else if (type.equals("6") || type.equals("7")) {
				ReurnCheckData(getString(R.string.DN004_RETURN_MSG2));
			} else {
				setResult(Activity.RESULT_OK);
				this.finish();
			}
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

	/*
	 * 确定返回前画面
	 */
	public void Btn_ReturnOk_OnClick(View view) {
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
			// 弹出确认框
			new AlertDialog.Builder(new ContextThemeWrapper(this,
					android.R.style.Theme_Holo_Light))
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.app_name)
					.setCancelable(false)
					.setMessage(msg + getString(R.string.DN004_024_MSG))
					.setPositiveButton(getString(R.string.button_ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									setResult(Activity.RESULT_OK,
											ReturnIntent());
									_thisActivity.finish();
								}
							})
					.setNegativeButton(getString(R.string.button_no), null)
					.show();

		} else {
			setResult(Activity.RESULT_OK, ReturnIntent());
			this.finish();
		}
	}

	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
	}

	/*
	 * 异常信息按钮
	 */
	public void Btn_Error_OnClick(View view) {
		// 页面跳转
		Intent errotItent = new Intent(_thisActivity, DN005Activity.class);
		Bundle errorBundle = new Bundle();
		errorBundle.putBoolean("bolFlag", bolFlag);
		errorBundle.putString("noPilecard", txtNoPilecard.getText().toString()); // 选择项桩脚牌ID
		errorBundle.putString("depotDtID", depotDtID); // 货物明细ID
		errorBundle.putString("depotID", depotID);
		errorBundle.putString("coOrder", cdOrderPublic); // 进仓编号
		errorBundle.putString("noBatch", noBatch); // 批次
		errorBundle.putString("thNo", txtThNo.getText().toString()); // 同行编号
		errotItent.putExtras(errorBundle);
		startActivity(errotItent);
	}

	/* 新增按钮初始化 */ 
	public void init() {
		ed_OprLength = (EditText) findViewById(R.id.ed_OprLength);// 长
		ed_OprWidth = (EditText) findViewById(R.id.ed_OprWidth);// 宽
		ed_OprHeight = (EditText) findViewById(R.id.ed_OprHeight);// 高
		ed_OprNum = (EditText) findViewById(R.id.ed_OprNum);// 件数
		ed_PackType = (EditText) findViewById(R.id.ed_PackType);// 包装类型
		
		// 包装类型
		ed_PackType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode((EditText) view);
				// 显示包装类型
				ShowPackTypeEditText_sec();
			}
		});
		
		btnOprSave = (Button) findViewById(R.id.btnOprSave);// 保存操作
		linOpr = (LinearLayout) findViewById(R.id.linOpr);// 计算布局

		linOpr.setVisibility(View.GONE);
		dn004List = new ArrayList<DepotDN004>();

		spMeterModel.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//modify by sdhuang 2014-12-08
				SelectDict item = (SelectDict) arg0.getItemAtPosition(arg2);
				if (bolFirstlOpr) {					
					if("3".equals(item.getId())){					
					//if (arg2 == 3) {
						setEnable(false);
					} else if (numSelect == 3) {
						setEnable(true);
					}
				} else {
					bolFirstlOpr = true;
				}
				if(item.getId()!=null && !"".equals(item.getId())){
				    numSelect = Integer.parseInt(item.getId());
				}else{
					numSelect = 0;
				}
				//numSelect = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listOprLWH = (ListView) findViewById(R.id.LWHlist);

		// 适配器
		oprAdapter = new ArrayAdapter<Object>(getApplicationContext(), 0) {
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				LinearLayout layout = new LinearLayout(getContext());
				final DepotDN004 data = (DepotDN004) getItem(position);

				try {
					mInflater.inflate(R.layout.activity_dn004_oprlwh_item,
							layout, true);

					if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}
					// 长
					TextView tView = (TextView) layout
							.findViewById(R.id.tv_Length);
					tView.setText(String.valueOf(data.getNoLen()));

					// 宽
					tView = (TextView) layout.findViewById(R.id.tv_Width);
					tView.setText(String.valueOf(data.getNoWidth()));

					// 高
					tView = (TextView) layout.findViewById(R.id.tv_Height);
					tView.setText(String.valueOf(data.getNoHeight()));

					// 件数
					tView = (TextView) layout.findViewById(R.id.tv_Num);
					tView.setText(String.valueOf(data.getDepotNum()));

					// 体积
					tView = (TextView) layout.findViewById(R.id.tv_Volume);
					tView.setText(String.valueOf(data.getCbm()));
					
					//包装类型
					tView = (TextView) layout.findViewById(R.id.tv_packType);
					tView.setText(String.valueOf(data.getPackType()));


					ImageButton imgBtn = (ImageButton) layout
							.findViewById(R.id.imgBtn);
					imgBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							DepotDN004 dn004Data = (DepotDN004) oprAdapter
									.getItem(position);
							oprAdapter.remove(dn004Data);
							dn004List.remove((DepotDN004) dn004Data);
							oprAdapter.notifyDataSetChanged();
							numVolume = numVolume - data.getCbm();
							numVolume= Double.valueOf(ChangeDouble(numVolume, 4, 4).replace(",", ""));
							numOprNum = numOprNum - data.getDepotNum();
							setLWHvalue();
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

		listOprLWH.setAdapter(oprAdapter);

	}

	/* 设置长宽高件数体积是否有效 */
	public void setEnable(Boolean bolEnable) {
		if (bolEnable) {
			linOpr.setVisibility(View.GONE);
			oprAdapter.clear();
			dn004List.clear();
		} else {
			oprAdapter.clear();
			dn004List.clear();
			linOpr.setVisibility(View.VISIBLE);
		}
		strModeRemark = "";
		numOprLength = 0; // 临时长度
		numOprWidth = 0; // 临时宽度
		numOprHeight = 0; // 临时高度
		numOprNum = 0; // 临时件数
		numVolume = 0; // 临时体积

		bolOpr = bolEnable;
		editLong.setEnabled(bolEnable); // 长
		editWide.setEnabled(bolEnable); // 宽
		editHigh.setEnabled(bolEnable); // 高
		editNumber.setEnabled(bolEnable); // 件数
		editArgueNumber.setEnabled(bolEnable); // 计费件数
		editVolume.setEnabled(bolEnable); // 体积

		editLong.setText(""); // 长
		editWide.setText("");// 宽
		editHigh.setText("");// 高
		editNumber.setText(""); // 件数
		editArgueNumber.setText(""); // 计费件数
		editVolume.setText(""); // 体积
	}

	/*
	 * 入力验证
	 */
	private boolean DoOprCheck() {
		try {

			// 长
			if (ed_OprLength.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_014_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			if (ReturnFloat(ed_OprLength.getText().toString().trim()) == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_014_MSG)
								+ getString(R.string.DN004_025_MSG));
				return false;
			}

			// 宽
			if (ed_OprWidth.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_015_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			if (ReturnFloat(ed_OprWidth.getText().toString().trim()) == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_015_MSG)
								+ getString(R.string.DN004_025_MSG));
				return false;
			}

			// 高
			if (ed_OprHeight.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_016_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			if (ReturnFloat(ed_OprHeight.getText().toString()) == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_016_MSG)
								+ getString(R.string.DN004_025_MSG));
				return false;
			}

			// 件数
			if (ed_OprNum.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_017_MSG)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

			if (ReturnInt(ed_OprNum.getText().toString().trim()) == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_017_MSG)
								+ getString(R.string.DN004_025_MSG));
				return false;
			}
			
			//包装类型
			if (ed_PackType.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN004_PACK_TYPE_SEC)
								+ getString(R.string.DN004_001_MSG));
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/*
	 * 保存按钮事件
	 */
	public void Btn_OprSave_OnClick(View view) {

		// 入力验证
		if (!DoOprCheck()) {
			return;
		}
		DepotDN004 depotDt = new DepotDN004();

		double flong = 0;
		double fwide = 0;
		double fhigh = 0;
		int fnum = 0;

		flong = ReturnFloat(ed_OprLength.getText().toString().trim());

		fwide = ReturnFloat(ed_OprWidth.getText().toString().trim());

		fhigh = ReturnFloat(ed_OprHeight.getText().toString().trim());

		fnum = ReturnInt(ed_OprNum.getText().toString().trim());

		double f = (flong / 100) * (fwide / 100) * (fhigh / 100) * fnum;
		depotDt.setNoLen(flong); // 长
		depotDt.setNoWidth(fwide); // 宽
		depotDt.setNoHeight(fhigh); // 高
		depotDt.setDepotNum(fnum); // 进仓件数
		depotDt.setCbm(Double.valueOf(ChangeDouble(f, 4, 4).replace(",", "")));// 体积
		depotDt.setPackType(ed_PackType.getText().toString()); //包装类型
		dn004List.add(depotDt);

		numVolume += Double.valueOf(ChangeDouble(f, 4, 4).replace(",", ""));
		numOprNum += fnum;
		if (dn004List != null) {
			oprAdapter.clear();
			oprAdapter.addAll(dn004List);
			oprAdapter.notifyDataSetChanged();
		}

		ed_OprLength.setText("");
		ed_OprWidth.setText("");
		ed_OprHeight.setText("");
		ed_OprNum.setText("");
		ed_PackType.setText("");
		selPackType_sec = "";
		setLWHvalue();
	}

	/* 设置长宽高的值 */
	public void setLWHvalue() {
		if (oprAdapter.getCount() > 0) {
			DepotDN004 dn004Data = (DepotDN004) oprAdapter.getItem(0);
			numOprLength = dn004Data.getNoLen();
			numOprWidth = dn004Data.getNoWidth();
			numOprHeight = dn004Data.getNoHeight();
			editLong.setText(String.valueOf(numOprLength)); // 长
			editWide.setText(String.valueOf(numOprWidth));// 宽
			editHigh.setText(String.valueOf(numOprHeight));// 高
			editNumber.setText(String.valueOf(numOprNum)); // 件数
			editArgueNumber.setText(String.valueOf(numOprNum)); // 计费件数
			numVolume = Double.valueOf(ChangeDouble(numVolume, 4, 4).replace(",", ""));
			editVolume.setText(String.valueOf(numVolume)); // 体积
		} else {
			numOprLength = 0;
			numOprWidth = 0;
			numOprHeight = 0;
			numOprNum = 0;
			numVolume = 0;
			editLong.setText(""); // 长
			editWide.setText("");// 宽
			editHigh.setText("");// 高
			editNumber.setText(""); // 件数
			editArgueNumber.setText(""); // 计费件数
			editVolume.setText(""); // 体积
		}

	}
	
	// add by yxq 2014/09/16 begin
	/*
	 * 保存按钮事件(复量)
	 */
	public void Btn_ReaMea_SAVE_OnClick(View view) {
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
			SaveData();    // 保存长宽高
		}
	}
	// add by yxq 2014/09/16 end
	
	// add by yxq 2014/10/20 begin
	/*
	 * 保存按钮事件(更改包装)
	 */
	public void Btn_ChangePack_SAVE_OnClick(View view) {
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
			SaveData();    // 保存长宽高
		}
	}
	// add by yxq 2014/10/20 end
	
	// add by yxq 2014/10/30 begin 
	// 更改件数和称重
	public void Btn_VA003_Save_Common_OnClick(View view) {
		// 入力验证
		if (!DoCheck()) {
			return;
		}
		SaveData();    // 保存
	}
	// add by yxq 2014/10/30 end
	
	//更新库区，装卸工列表
	public void initTruck(final View view){
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotDtId", depotDtID);
		p1.put("type", type);
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN004_GetDepotDataByDepotDtID", p1,
				new TypeToken<DepotDN004>() {
		}.getType(), new AmassHttpResponseHandler<DepotDN004>() {

			@Override
			protected void OnSuccess(DepotDN004 response) {
				super.OnSuccess(response);
				// 获取数据
				DepotDN004 depot = (DepotDN004) response;

				// 绑定铲车工下拉框
				truckList = depot.getTruckList();

				// 绑定库区下拉框
				posList = depot.getPosList();
				
				if (type.equals("5") || type.equals("8") || type.equals("9")) {
					return;
				}
				// add by yxq 2014/09/16 begin
				if (type.equals("12")) {
					return;
				}
				// add by yxq 2014/09/16 end
				// add by yxq 2014/10/20 begin
				if (type.equals("13")) {
					return;
				}
				// add by yxq 2014/10/20 end
				// add by yxq 2014/10/30 begin
				if (type.equals("14")||type.equals("15")) {
					return;
				}
				// add by yxq 2014/10/30 end
				// 禁用软键盘
				hideSoftInputMode((EditText) view);

				final String[] truckId = new String[truckList.size()];
				final String[] truckName = new String[truckList.size()];
				for (int j = 0; j < truckList.size(); j++) {
					truckId[j] = ((SelectDict) truckList.get(j)).getId();
					truckName[j] = ((SelectDict) truckList.get(j)).getName();
				}
				int i = -1; // 初期选中项
				// 循环获取选择项
				if (selTruckId != null && selTruckId.length() > 0) {
					for (i = 0; i < truckList.size(); i++) {
						if (((SelectDict) truckList.get(i)).getId()
								.equals(selTruckId)) {
							break;
						}
					}
				}

				new AlertDialog.Builder(new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light))
						.setTitle(R.string.DN004_TRUCK_SELECT)
						.setSingleChoiceItems(truckName, i,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										spTruck.setText(truckName[which]);
										selTruckId = truckId[which];
										dialog.dismiss();
									}
								}).show();
			}
		}, false);
	}
	
	//更新库区，装卸工列表
	public void initPosition(final View view){
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotDtId", depotDtID);
		p1.put("type", type);
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN004_GetDepotDataByDepotDtID", p1,
				new TypeToken<DepotDN004>() {
		}.getType(), new AmassHttpResponseHandler<DepotDN004>() {

			@Override
			protected void OnSuccess(DepotDN004 response) {
				super.OnSuccess(response);
				// 获取数据
				DepotDN004 depot = (DepotDN004) response;

				// 绑定铲车工下拉框
				truckList = depot.getTruckList();

				// 绑定库区下拉框
				posList = depot.getPosList();
				if (type.equals("5") || type.equals("8") || type.equals("9")) {
					return;
				}
				// add by yxq 2014/09/16 begin
				if (type.equals("12")) {
					return;
				}
				// add by yxq 2014/09/16 end
				// add by yxq 2014/10/20 begin
				if (type.equals("13")) {
					return;
				}
				// add by yxq 2014/10/20 end
				// add by yxq 2014/10/30 begin
				if (type.equals("14")||type.equals("15")) {
					return;
				}
				// add by yxq 2014/10/30 end
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
		}, false);
	}
	
	//去除double类型的整数值后面的小数点及多余的0，如2.0->2
	public String doubleTrans(double d){
		if(Math.round(d)-d==0){
			return String.valueOf((long) d);
		}
		return String.valueOf(d);
	}	
	
	/*
	 * 显示包装类型，单板多尺寸
	 */
	private void ShowPackTypeEditText_sec() {
		final String[] packTypeId = new String[packTypeList.size()];
		final String[] packTypeName = new String[packTypeList.size()];
		boolean[] checksItem = null;

		final List<String> medicSelecID = new ArrayList<String>(); // 选中的包装类型

		if (selPackType_sec != null && selPackType_sec.length() > 0) {
			if (selPackType_sec.contains(",")) {
				for (String str : selPackType_sec.split(",")) {
					medicSelecID.add(str.trim());
				}
			} else {
				medicSelecID.add(selPackType_sec.trim());
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
							// edit by yxq 2014/10/13 begin
							/*str += packTypeName[Integer.valueOf(sel.toString()) - 1]
									+ ",";*/
							str += packTypeMap.get(sel.toString()) + ",";
							// edit by yxq 2014/10/13 end
							strID += sel + ",";
						}
						if (str.length() > 0) {
							str = str.substring(0, str.length() - 1);
							strID = strID.substring(0, strID.length() - 1);
						}

						ed_PackType.setText(str);
						selPackType_sec = strID;
					}
				}).show();
	}
	
}
