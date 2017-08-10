package com.amassfreight.warehouse.ui;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.bean.BarCode04;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.BoxPP004;
import com.amassfreight.domain.CommonResult;
import com.amassfreight.domain.OrderListResponseData;
import com.amassfreight.domain.PP004Adapter;
import com.amassfreight.domain.PP006DetailData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;

@SuppressLint("SimpleDateFormat")
public class PP006Activity extends BaseActivity {

	private static final String METHOD_URL = "PP006getDetail";
	private static final String METHOD_URL1 = "PP004getDetail";
	private boolean isShowDialog = true;

	private PP004Adapter adapter;
	private ListView bListView;
	private TextView boxNoText;
	private TextView workStatus;
	private TextView deadlineTime;
	private TextView pickingRemark;
	private TextView planner;
	private Button unPacking;
	private Button allPacked;
	private Button allPackedCompel;
	private Button CancelAllPacked;
	private String boxNo;
	private Boolean flgCntScanLock;
	private String containerCd;
	private String orderCd;
	private String packStatus;  //箱子装箱状态
	private String boxNoByOrder = null;
	private List<OrderListResponseData> list = new ArrayList<OrderListResponseData>();
	private Boolean IsEdit;
	
	private LinearLayout linMain;
	private Boolean meunShow = false;
	private ImageButton imgBtn;
	private TextView tvOprName; 
	private TextView tvTitleNoBox;// 集箱号
	private LinearLayout imgLiner;
	private TextView tvDetail;
	private TextView packPhotoQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pp006);
		setupActionBar();
		bListView = (ListView) findViewById(R.id.lv_zjp);
		boxNoText = (TextView) findViewById(R.id.tv_jxno);
		deadlineTime = (TextView) findViewById(R.id.tv_zxdeadline);
		workStatus = (TextView) findViewById(R.id.tv_zxstatus);
		pickingRemark = (TextView) findViewById(R.id.pickingRequire);
		planner = (TextView) findViewById(R.id.planner);
		unPacking = (Button) findViewById(R.id.unPacking);
		allPacked = (Button) findViewById(R.id.allPacked);
		allPackedCompel= (Button) findViewById(R.id.allPackedCompel);
		CancelAllPacked = (Button) findViewById(R.id.CancelAllPacked);
		tvOprName = (TextView) findViewById(R.id.tvOPrName);
		linMain = (LinearLayout) findViewById(R.id.linMain);
		imgBtn = (ImageButton) findViewById(R.id.imgBtn);
		tvTitleNoBox = (TextView) findViewById(R.id.tvTitleNoBox);
		imgLiner = (LinearLayout) findViewById(R.id.imgLiner);
		imgLiner.setVisibility(View.GONE);
		boxNoText.setClickable(true);
        boxNoText.setFocusable(true);
		boxNoText.setFocusableInTouchMode(true);
		boxNoText.requestFocus();
		//设置按钮不可见
		unPacking.setVisibility(View.GONE);
		allPacked.setVisibility(View.GONE);
		CancelAllPacked.setVisibility(View.GONE);
        allPackedCompel.setVisibility(View.GONE);	

		Intent intent=getIntent(); 
		boxNo = intent.getStringExtra("boxNo");
		containerCd = intent.getStringExtra("containerCd");
		IsEdit = intent.getBooleanExtra("IsEdit", true);
		if(boxNo != null && !boxNo.equals("")){
			getData();
		}
		
		// 明细跳转按钮
		tvDetail = (TextView) findViewById(R.id.tvDetail);
		tvDetail.setVisibility(View.GONE);
		tvDetail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PP006Activity.this,
						PP010Activity_Depot.class);
				Bundle bundle = new Bundle();
				bundle.putString("noBox", boxNo);
			    bundle.putString("boxType", "1");
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
				intent.setClass(PP006Activity.this, PP016Activity.class); 
				intent.putExtra("boxNo", boxNo);
				intent.putExtra("orderCd", "");
				intent.putExtra("orderCdPublic", "");
				startActivity(intent);
			}
		});
		
		// 集箱号监听事件
		boxNoText.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (boxNo != null && !boxNo.isEmpty()) {
					Intent intent = new Intent(PP006Activity.this,
							PP003Activity.class);
					intent.putExtra("boxNo", boxNo);
					startActivity(intent);
				}
			}
		});
	}

	//刷新画面数据
	public void getData(){
		if(containerCd != null && !containerCd.equals("")){
			Map<String,Object> requestData = new HashMap<String,Object>();
			requestData.put("containerCd", containerCd);			
			Type type = new TypeToken<PP006DetailData>(){}.getType();
			NetworkHelper.getInstance().postJsonData(this, METHOD_URL, requestData,type , httpHandler, true);
		}else{
			Map<String,Object> requestData = new HashMap<String,Object>();
			requestData.put("boxNo", boxNo);			
			Type type = new TypeToken<BoxPP004>(){}.getType();
			NetworkHelper.getInstance().postJsonData(this, METHOD_URL1, requestData,type , httpHandler1, true);
		}
	}
	
	//画面初始化handler
	private AmassHttpResponseHandler<PP006DetailData> httpHandler = new AmassHttpResponseHandler<PP006DetailData>() {
		protected void OnSuccess(PP006DetailData response) {
			super.OnSuccess(response);
			try{
				PP006DetailData responseBox = (PP006DetailData) response;
				if(responseBox != null && responseBox.getBoxNo()!=null){
					tvTitleNoBox.setText(responseBox.getBoxNo()); // 集箱号
					imgLiner.setVisibility(View.VISIBLE);
					boxNoText.setText(responseBox.getBoxNo());
					boxNoText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
					boxNoText.setTextColor(Color.BLUE);
					boxNo = responseBox.getBoxNo();
					containerCd = responseBox.getContainerCd();					
					tvDetail.setVisibility(View.VISIBLE);
					tvDetail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
					packPhotoQuery.setVisibility(View.VISIBLE);
					packPhotoQuery.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
					if(responseBox.getFlgPresent() == 0){
						boxNoText.setTextColor(Color.RED);
					}
					if(responseBox.getPackingDeadline() != null){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						deadlineTime.setText(sdf.format(responseBox.getPackingDeadline()));
					}
					if("0".equals(responseBox.getStatusNow())){
						workStatus.setText(R.string.status_now_0);
						workStatus.setBackgroundResource(R.color.orangle);
					}else{
						if(responseBox.getPackingStatus() == 3){
							workStatus.setText(R.string.status_pack_done);
							workStatus.setBackgroundResource(R.color.gay);
						} 
						else if(responseBox.getPackingStatus() == 2){
							workStatus.setText(R.string.status_pack_doing);
							workStatus.setBackgroundResource(R.color.lightBlue);
						}
						else if(responseBox.getPackingStatus() == 1){
							workStatus.setText(R.string.status_pick_doing);
							workStatus.setBackgroundResource(R.color.lightBlue);
						}
						else{
							workStatus.setText(R.string.status_pick_undo);
							workStatus.setBackgroundResource(0);
						}
					}
					if(responseBox.getPackingStatus() != null){
						packStatus = responseBox.getPackingStatus().toString();
					}else{
						packStatus = "0";
					}
					planner.setText(responseBox.getPlanner());
					pickingRemark.setText(responseBox.getPackingRemarks());
					list = responseBox.getOrderList();						
					unPacking.setVisibility(View.GONE);
					allPacked.setVisibility(View.GONE);
					CancelAllPacked.setVisibility(View.GONE);
					allPackedCompel.setVisibility(View.GONE);
					if(IsEdit){
						//设置掏箱按钮是否可见
						if(responseBox.getUnPackingFlg()){
							unPacking.setVisibility(View.VISIBLE);
						}else{
							//原判断逻辑，以后可能会启用，保留勿删除
							/*if(responseBox.getAllPackedFlg()){
								//设置装箱完成按钮是否可见
								if(responseBox.getPackingStatus() == 2){
									allPacked.setVisibility(View.VISIBLE);
									//设置取消装箱完成按钮是否可见
								}else if(responseBox.getPackingStatus() == 3){
									if(responseBox.getCancelPackedFlg()){
										CancelAllPacked.setVisibility(View.VISIBLE);
									}
								}
							}*/

							if(responseBox.getPackingStatus() == 2){
								/*//判断是否有单票在作业中未完成的
								if(checkOrderFinish() && checkDoneOrder()){
									allPacked.setVisibility(View.VISIBLE);
								}*/
								//判断是否所有单票都已作业完成
								if(checkOrderAllDone()){
									allPacked.setVisibility(View.VISIBLE);	
								}
								//判断是否有权限显示强制装箱按钮
								if(responseBox.getPackCompelFlg()){
									allPackedCompel.setVisibility(View.VISIBLE);	
								}
							}
							if(responseBox.getPackingStatus() == 3){
								if(responseBox.getCancelPackedFlg()){
									CancelAllPacked.setVisibility(View.VISIBLE);
								}
							}
						}
					}

					if(list.size() > 0){
						adapter = new PP004Adapter(getApplicationContext(), list);
						adapter.setList(list);
						adapter.notifyDataSetChanged(); 
					}
					bListView.setAdapter(adapter);
					bListView.setOnItemClickListener(new ItemClickListener());
				}else{
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_009);
				}

			}
			catch (Exception ex){
				ex.printStackTrace();
			}

		};
	};
	
	private AmassHttpResponseHandler<BoxPP004> httpHandler1 = new AmassHttpResponseHandler<BoxPP004>() {
		protected void OnSuccess(BoxPP004 response) {
			super.OnSuccess(response);
			try{
				BoxPP004 responseBox = (BoxPP004) response;
				if(responseBox != null && responseBox.getBoxNo()!=null){
					tvTitleNoBox.setText(responseBox.getBoxNo()); // 集箱号
					imgLiner.setVisibility(View.VISIBLE);
					boxNoText.setFocusableInTouchMode(true);
					boxNoText.setText(responseBox.getBoxNo());
					boxNoText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
					boxNoText.setTextColor(Color.BLUE);
					boxNo = responseBox.getBoxNo();
					containerCd = responseBox.getContainerCd();
					tvDetail.setVisibility(View.VISIBLE);
					tvDetail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
					packPhotoQuery.setVisibility(View.VISIBLE);
					packPhotoQuery.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
					//不在场时
					if(responseBox.getFlgPresent() == 0){
						boxNoText.setTextColor(Color.RED);
					}
					if(responseBox.getPackingDeadline() != null){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						deadlineTime.setText(sdf.format(responseBox.getPackingDeadline()));
					}
					if("0".equals(responseBox.getStatusNow())){
						workStatus.setText(R.string.status_now_0);
						workStatus.setBackgroundResource(R.color.orangle);
					}else{
						if(responseBox.getPackingStatus() == 3){
							workStatus.setText(R.string.status_pack_done);
							workStatus.setBackgroundResource(R.color.gay);
						} 
						else if(responseBox.getPackingStatus() == 2){
							workStatus.setText(R.string.status_pack_doing);
							workStatus.setBackgroundResource(R.color.lightBlue);
						}
						else if(responseBox.getPackingStatus() == 1){
							workStatus.setText(R.string.status_pick_doing);
							workStatus.setBackgroundResource(R.color.lightBlue);
						}
						else{
							workStatus.setText(R.string.status_pick_undo);
							workStatus.setBackgroundResource(0);
						}
					}
					if(responseBox.getPackingStatus() != null){
						packStatus = responseBox.getPackingStatus().toString();
					}else{
						packStatus = "0";
					}
					planner.setText(responseBox.getPlanner());
					pickingRemark.setText(responseBox.getPackingRemarks());
					list = responseBox.getOrderList();

					unPacking.setVisibility(View.GONE);
					allPacked.setVisibility(View.GONE);
					CancelAllPacked.setVisibility(View.GONE);
					if(IsEdit){
						//设置掏箱按钮是否可见
						if(responseBox.getUnPackingFlg()){
							unPacking.setVisibility(View.VISIBLE);
						}else{
							if(responseBox.getPackingStatus() == 2){
								/*//判断是否有单票在作业中未完成的
								if(checkOrderFinish() && checkDoneOrder()){
									allPacked.setVisibility(View.VISIBLE);
								}*/
								//判断是否所有单票都已作业完成
								if(checkOrderAllDone()){
									allPacked.setVisibility(View.VISIBLE);	
								}
								//判断是否有权限显示强制装箱按钮
								if(responseBox.getPackCompelFlg()){
									allPackedCompel.setVisibility(View.VISIBLE);	
								}
							}							
						}
						if(responseBox.getPackingStatus() == 3){
							if(responseBox.getCancelPackedFlg()){
								CancelAllPacked.setVisibility(View.VISIBLE);
							}
						}
					}
					if(list.size() > 0){
						adapter = new PP004Adapter(getApplicationContext(), list);
						adapter.setList(list);
						adapter.notifyDataSetChanged(); 
					}
					bListView.setAdapter(adapter);
					bListView.setOnItemClickListener(new ItemClickListener());

				}else {
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_009);
				}
			}
			catch (Exception ex){
				ex.printStackTrace();
			}

		};

	};
	
	//获取listview item点击事件      
	private final class ItemClickListener implements OnItemClickListener{  
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  
			TextView tv = (TextView) view.findViewById(R.id.order_cd);
			final String orderCd = tv.getText().toString();
			Map<String,Object> requestData = new HashMap<String,Object>();
			requestData.put("orderCd", orderCd);		 
			NetworkHelper.getInstance().postJsonData(_thisActivity, "PP004getUnPackingStatusByOrder", 
					requestData,String.class ,
					new AmassHttpResponseHandler<String>() {
				@Override
				protected void OnSuccess(String response) {
					super.OnSuccess(response);
					String unPackingFlg = (String) response;	
					if(unPackingFlg != null && !"".equals(unPackingFlg)){						
						if("0".equals(unPackingFlg)){
							//跳转到拣货明细画面
							Intent intent = new Intent(); 
							intent.setClass(PP006Activity.this, PP007Activity.class); 
							intent.putExtra("boxNo", boxNo);
							intent.putExtra("containerCd", containerCd);
							intent.putExtra("orderCd", orderCd);
							intent.putExtra("packStatus", packStatus);
							intent.putExtra("IsEdit", IsEdit);
							startActivity(intent);
							finish(); // 结束当前活动
						}else if("1".equals(unPackingFlg)){
							//有掏箱记录未完成
							//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP004_error_unPackingRemain);
							showUnpackDialog();
						}else{
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
						}
					}else{
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
					}
				}
			}, isShowDialog);	
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		if(IsEdit){
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
			//this.finish();
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
				//boxNo = data.getStringExtra("SCAN_RESULT");
				//final BarCodeResult result = Utils.analysisBarCode(data.getStringExtra("SCAN_RESULT"),"04");
				final BarCode04 barCode04 = new BarCode04();
				boolean result04 = barCode04.paserBarCode(data.getStringExtra("SCAN_RESULT"));
				/*final BarCode05 barCode05 = new BarCode05();
				boolean result05 = barCode05.paserBarCode(data.getStringExtra("SCAN_RESULT"));*/
				//初始化时
				if(boxNo == null){	
					//扫描箱号
					if(result04){
						Map<String,Object> requestData = new HashMap<String,Object>();
						requestData.put("containerCd", barCode04.getContainerCd());			
						Type type = new TypeToken<PP006DetailData>(){}.getType();
						NetworkHelper.getInstance().postJsonData(this, METHOD_URL, requestData,type , httpHandler, true);
					//扫描集箱号
					}
					/*else if(result05){
						Map<String,Object> requestData = new HashMap<String,Object>();
						requestData.put("boxNo", barCode05.getBoxNo());			
						Type type = new TypeToken<BoxPP004>(){}.getType();
						NetworkHelper.getInstance().postJsonData(this, METHOD_URL1, requestData,type , httpHandler1, true);
					}*/
					else{
						//扫描到错误的集装箱号
						//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP012_001_MSG);
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP013_001_MSG);
					}			
				}else {
					//集装箱切换时
					//扫描到箱号
					if(result04){
						if(!barCode04.getContainerCd().equals(containerCd)){
							//弹出确认集装箱切换dialog
							AlertDialog.Builder builder = new AlertDialog.Builder(
									new ContextThemeWrapper(_thisActivity,
											android.R.style.Theme_Holo_Light));
							builder.setMessage(R.string.PP004_warning_changeBox);
							builder.setIcon(R.drawable.ic_launcher);
							builder.setCancelable(false);
							builder.setTitle(R.string.app_name);
							builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									Map<String,Object> requestData = new HashMap<String,Object>();
									requestData.put("containerCd", barCode04.getContainerCd());			
									Type type = new TypeToken<PP006DetailData>(){}.getType();
									NetworkHelper.getInstance().postJsonData(PP006Activity.this, METHOD_URL, requestData,type , httpHandler, isShowDialog);
								}
							});
							builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							builder.create().show();
						}else{
							getData();
						}
					}
					////扫描到集箱号
					/*else if(result05){
						if(!barCode05.getBoxNo().equals(boxNo)){
							//弹出确认集装箱切换dialog
							AlertDialog.Builder builder = new AlertDialog.Builder(
									new ContextThemeWrapper(_thisActivity,
											android.R.style.Theme_Holo_Light));
							builder.setIcon(R.drawable.ic_launcher);
							builder.setCancelable(false);
							builder.setMessage(R.string.PP004_warning_changeBox);
							builder.setTitle(R.string.app_name);
							builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									Map<String,Object> requestData = new HashMap<String,Object>();
									requestData.put("boxNo", barCode05.getBoxNo());			
									Type type = new TypeToken<BoxPP004>(){}.getType();
									NetworkHelper.getInstance().postJsonData(PP006Activity.this, METHOD_URL1, requestData,type , httpHandler1, isShowDialog);
								}
							});
							builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							builder.create().show();
						}						
					}*/
					else{
						//扫描桩脚牌时
						//final BarCodeResult result1 = Utils.analysisBarCode(data.getStringExtra("SCAN_RESULT"),"02");						
						final BarCode02 barCode02 = new BarCode02();
						boolean result02 = barCode02.paserBarCode(data.getStringExtra("SCAN_RESULT"));
						if(result02){
							Map<String,Object> requestData = new HashMap<String,Object>();
							requestData.put("orderCd", barCode02.getOrderCd());
							requestData.put("depotDtId", barCode02.getDepotDtId());
							requestData.put("boxNo", boxNo);
							//根据orderCd获取集箱号
							//NetworkHelper.getInstance().postJsonData(PP006Activity.this, "PP004getBoxbyOrderCd", requestData,BoxPP004.class ,
							//		new AmassHttpResponseHandler<BoxPP004>() {
							NetworkHelper.getInstance().postJsonData(PP006Activity.this, "PP004getBoxbyOrderCdNew", requestData,BoxPP004.class ,
									new AmassHttpResponseHandler<BoxPP004>() {
								@Override
								protected void OnSuccess(BoxPP004 response) {
									super.OnSuccess(response);
									BoxPP004 responseBox = (BoxPP004) response;
									if(responseBox != null && responseBox.getBoxNo()!=null){
										boxNoByOrder = responseBox.getBoxNo();
									}else{
										boxNoByOrder = null;
									}
									//判断是否已经加了装箱扫描锁
									if (responseBox.getFlgLockScan() == false){
									
										//判断是否是该集装箱的货物							
										if(boxNo.equals(boxNoByOrder)){
											//判断是否有掏箱记录未完成
											if(!responseBox.getUnPackingFlg()){
											//跳转到装箱明细画面
												Intent intent = new Intent(); // 建立Intent
												intent.setClass(PP006Activity.this, PP007Activity.class); // 设置活动
												intent.putExtra("boxNo", boxNo);
												//intent.putExtra("depotDtId", barCode02.getDepotDtId());
												//intent.putExtra("orderCd", barCode02.getOrderCd());
												intent.putExtra("depotDtId", responseBox.getDepotDtId());
												intent.putExtra("orderCd", responseBox.getOrderCd());
												intent.putExtra("coloader", barCode02.getCoLoader());
												intent.putExtra("depotNum", barCode02.getDepotNum());
												intent.putExtra("containerCd", containerCd);
												intent.putExtra("packStatus", packStatus);
												intent.putExtra("IsEdit", IsEdit);
												startActivity(intent);
												finish(); // 结束当前活动
											}else{
												//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP004_error_unPackingRemain);
												showUnpackDialog();
											}
										}else{		
											if (responseBox.getLockScanRemark() == ""){
												Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_001);
											}
											else{
												Utils.showAlertDialog(_thisActivity,responseBox.getLockScanRemark());
											}
												
										}
									}
									else {
										Utils.showAlertDialog(_thisActivity,responseBox.getLockScanRemark());
									}
								}

							}, isShowDialog);
						}else{
							//扫描到错误的桩脚牌
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_002);
						}
					}					
				}
			} else {
				//失败提示
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_scanError);
			}
			break;
		}
	}
	
	//点击掏箱按钮
	public void unPacking_OnClick(View arg0) {		
		if(!"3".equals(packStatus)){
			Intent intent = new Intent(); // 建立Intent
			intent.setClass(PP006Activity.this, PP014Activity.class); // 设置活动
			intent.putExtra("boxNo", boxNo);
			intent.putExtra("containerCd", containerCd);
			intent.putExtra("IsEdit", IsEdit);
			startActivity(intent);
			finish(); // 结束当前活动
		}else{
			Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP006_error_unPacking);
		}
	}
	
	//装箱完成按钮
	public void allPacked_OnClick(View arg0) {	
		//弹出确认dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light));
		//判断是否有状态为未开始的单票记录
		if(checkUndoOrder()){
		    builder.setMessage(R.string.PP006_warning_allPacked);
		}else{
			builder.setMessage(R.string.PP006_warning_remainUnPackedOrder);
		}
		builder.setIcon(R.drawable.ic_launcher);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_name);
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Map<String,Object> requestData = new HashMap<String,Object>();
				requestData.put("boxNo", boxNo);	
				requestData.put("flg", "1");
				NetworkHelper.getInstance().postJsonData(PP006Activity.this, 
						"PP006updateAllpackedStatus", requestData,CommonResult.class , allPackedHandler, isShowDialog);
			}
		});
		builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	//强制装箱完成按钮
	//create by sdhuang 2014/11/12
	public void allPacked_Compel_OnClick(View arg0) {	
		//弹出确认dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light));

		//判断是否有状态为未开始的单票记录
		if(checkUndoOrder()){
		    builder.setMessage(R.string.PP006_warning_allPacked_compel);
		}else{
			builder.setMessage(R.string.PP006_warning_remainUnPackedOrder_compel);
		}
		builder.setIcon(R.drawable.ic_launcher);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_name);
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Map<String,Object> requestData = new HashMap<String,Object>();
				requestData.put("boxNo", boxNo);	
				requestData.put("flg", "3");
				NetworkHelper.getInstance().postJsonData(PP006Activity.this, 
						"PP006updateAllpackedStatus", requestData,CommonResult.class , allPackedHandler, isShowDialog);
			}
		});
		builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	//取消装箱完成按钮
	public void CancelAllPacked_OnClick(View arg0) {		
		//弹出确认dialog
		//AlertDialog.Builder builder = new Builder(_thisActivity);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light));
		builder.setIcon(R.drawable.ic_launcher);
		builder.setCancelable(false);
		builder.setMessage(R.string.PP006_warning_CancelAllPacked);
		builder.setTitle(R.string.app_name);
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Map<String,Object> requestData = new HashMap<String,Object>();
				requestData.put("boxNo", boxNo);	
				requestData.put("flg", "0");
				NetworkHelper.getInstance().postJsonData(PP006Activity.this, 
						"PP006updateAllpackedStatus", requestData,CommonResult.class , allPackedHandler, isShowDialog);
			}
		});
		builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	//装箱完成处理handler
	private AmassHttpResponseHandler<CommonResult> allPackedHandler = new AmassHttpResponseHandler<CommonResult>() {
		protected void OnSuccess(CommonResult response) {
			super.OnSuccess(response);
			CommonResult result = (CommonResult) response;
			if(result != null){
				//处理成功
				if(result.getFlag()){
					showAlertDialogRefresh(_thisActivity,R.string.msg_common_success);
				//处理失败
				}else{
					if("004".equals(result.getErrorCd())){
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP006_error_CancelAllPacked);
					}else if("005".equals(result.getErrorCd())){
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP006_error_allPacked);
					}else if("006".equals(result.getErrorCd())){
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP006_error_NoPic);
					}else if("007".equals(result.getErrorCd())){//做过配柜调整且尚未完成掏箱
						Intent intent = new Intent(); // 建立Intent
						intent.setClass(PP006Activity.this, PP017Activity.class); // 设置活动
						intent.putExtra("boxNo", boxNo);
						startActivity(intent);
					}else if("008".equals(result.getErrorCd())){
						Utils.showAlertDialog(_thisActivity, result.getRemark());
					}else{
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
					}
				}
			}else{
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
			}
			
		}
	};
	
	public void showAlertDialogRefresh(Context context, int alertMsg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light));
		builder.setIcon(R.drawable.ic_launcher);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_name);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//refresh();
				getData();
			}
		});
		builder.show();
	}
	
	//判断是否有单票在作业中未完成的记录,有则返回false
	public Boolean checkOrderFinish(){
		int count = 0;
		if(list != null){
			for(int i=0 ; i < list.size() ; i++){
				if(1 == list.get(i).getWorkStatus()){
					count++;
				}
			}
		}
		if(count > 0){
			return false;
		}else{
			return true;
		}
	}
	
	//判断是否有状态为未开始的单票记录,有则返回false
	public Boolean checkUndoOrder(){
		int count = 0;
		if(list != null){
			for(int i=0 ; i < list.size() ; i++){
				if(0 == list.get(i).getWorkStatus()){
					count++;
				}
			}
		}
		if(count > 0){
			return false;
		}else{
			return true;
		}
	}
	
/*
	public Boolean CheckCntScanLock(){
		flgCntScanLock = false;
		Map<String,Object> requestData = new HashMap<String,Object>();
		requestData.put("noBox", boxNo);	
		NetworkHelper.getInstance().postJsonData(_thisActivity, "PP006_CheckCntScanLock", 
				requestData,String.class ,
				new AmassHttpResponseHandler<String>() {
			@Override
			protected void OnSuccess(String response) {
				super.OnSuccess(response);
				String checkResult = (String) response;	
				if(checkResult != null && !"".equals(checkResult)){						
					if("false".equals(checkResult)){
						flgCntScanLock = false;
					}
					else if("true".equals(checkResult)){
						flgCntScanLock = true; 
					}
					else{
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
					}
				}else{
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
				}
			}
		}, isShowDialog);
		return flgCntScanLock;
	}
*/
	
	//判断单票完成的数量,有则返回true
	public Boolean checkDoneOrder(){
		int count = 0;
		if(list != null){
			for(int i=0 ; i < list.size() ; i++){
				if(2 == list.get(i).getWorkStatus()){
					count++;
				}
			}
		}
		if(count > 0){
			return true;
		}else{
			return false;
		}
	}
	
	//判断是否所有单票都已作业完成
	public Boolean checkOrderAllDone(){
		int count = 0;
		if(list != null){
			for(int i=0 ; i < list.size() ; i++){
				if(2 == list.get(i).getWorkStatus()){
					count++;
				}
			}
		}
		if(count > 0 && count == list.size()){
			return true;
		}else{
			return false;
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
	
	public void showUnpackDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light));
		builder.setMessage(R.string.PP004_error_unPackingRemain);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_name);
		builder.setPositiveButton(R.string.msg_detail_search, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(); // 建立Intent
				intent.setClass(PP006Activity.this, PP017Activity.class); // 设置活动
				intent.putExtra("boxNo", boxNo);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
/*	//装箱照片查询
	public void photoListQuery_OnClick(View view) {
		Intent intent = new Intent(); 
		intent.setClass(PP006Activity.this, PP016Activity.class); 
		intent.putExtra("boxNo", boxNo);
		intent.putExtra("orderCd", "");
		intent.putExtra("orderCdPublic", "");
		startActivity(intent);
	}*/

/*	//刷新
    private void refresh() {  
    	PP006Activity.this.finish();  
    	Intent intent = new Intent(PP006Activity.this,PP006Activity.class);  
    	intent.putExtra("boxNo", boxNo);
    	intent.putExtra("containerCd", containerCd);
    	startActivity(intent);  
	}  */
	
	public boolean isShowDialog() {
		return isShowDialog;
	}

	public void setShowDialog(boolean isShowDialog) {
		this.isShowDialog = isShowDialog;
	}

	public PP004Adapter getAdapter() {
		return adapter;
	}

	public void setAdapter(PP004Adapter adapter) {
		this.adapter = adapter;
	}

	public ListView getbListView() {
		return bListView;
	}

	public void setbListView(ListView bListView) {
		this.bListView = bListView;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public String getOrderCd() {
		return orderCd;
	}

	public void setOrderCd(String orderCd) {
		this.orderCd = orderCd;
	}

	public List<OrderListResponseData> getList() {
		return list;
	}

	public void setList(List<OrderListResponseData> list) {
		this.list = list;
	}

	public AmassHttpResponseHandler<PP006DetailData> getHttpHandler() {
		return httpHandler;
	}

	public void setHttpHandler(AmassHttpResponseHandler<PP006DetailData> httpHandler) {
		this.httpHandler = httpHandler;
	}

	public String getBoxNoByOrder() {
		return boxNoByOrder;
	}

	public void setBoxNoByOrder(String boxNoByOrder) {
		this.boxNoByOrder = boxNoByOrder;
	}

	public String getContainerCd() {
		return containerCd;
	}

	public void setContainerCd(String containerCd) {
		this.containerCd = containerCd;
	}
	
	public AmassHttpResponseHandler<CommonResult> getAllPackedHandler() {
		return allPackedHandler;
	}

	public void setAllPackedHandler(
			AmassHttpResponseHandler<CommonResult> allPackedHandler) {
		this.allPackedHandler = allPackedHandler;
	}

}
