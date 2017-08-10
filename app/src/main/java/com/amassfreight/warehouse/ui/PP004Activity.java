package com.amassfreight.warehouse.ui;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.bean.BarCode05;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.BoxPP004;
import com.amassfreight.domain.OrderListResponseData;
import com.amassfreight.domain.PP004Adapter;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;

@SuppressLint("SimpleDateFormat")
public class PP004Activity extends BaseActivity {
	private static final String METHOD_URL = "PP004getDetail";

	private PP004Adapter adapter;
	private ListView bListView;
	private TextView boxNoText;
	private TextView workStatus;
	private TextView deadlineTime;
	private TextView pickingRemark;
	private String boxNo;
	private String orderCd;
	private String boxNoByOrder = null;
	private Boolean IsEdit;
	private List<OrderListResponseData> list = new ArrayList<OrderListResponseData>();
	
	private LinearLayout linMain;
	private Boolean meunShow = false;
	private ImageButton imgBtn;
	private TextView tvOprName; 
	private TextView tvTitleNoBox;// 集箱号
	private LinearLayout imgLiner;
	private TextView tvDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pp004);
		setupActionBar();
		bListView = (ListView) findViewById(R.id.lv_zjp);
		boxNoText = (TextView) findViewById(R.id.tv_jxno);
		deadlineTime = (TextView) findViewById(R.id.tv_zxdeadline);
		workStatus = (TextView) findViewById(R.id.tv_zxstatus);
		pickingRemark = (TextView) findViewById(R.id.pickingRequire);
		
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

		Intent intent=getIntent(); 
		boxNo = intent.getStringExtra("boxNo");
		IsEdit = intent.getBooleanExtra("IsEdit", true);
		if(boxNo != null && !boxNo.equals("")){
//			boxNo = "X13001206";
			adapter = new PP004Adapter(getApplicationContext(), list);
			bListView.setAdapter(adapter);
			Map<String,Object> requestData = new HashMap<String,Object>();
			requestData.put("boxNo", boxNo);			
			Type type = new TypeToken<BoxPP004>(){}.getType();
			NetworkHelper.getInstance().postJsonData(this, METHOD_URL, requestData,type , httpHandler, true);
		}

		// 明细跳转按钮
		tvDetail = (TextView) findViewById(R.id.tvDetail);
		tvDetail.setVisibility(View.GONE);
		tvDetail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PP004Activity.this,
						PP010Activity_Depot.class);
				Bundle bundle = new Bundle();
				bundle.putString("noBox", boxNo);
				bundle.putString("boxType", "1");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		// 集箱号监听事件
		boxNoText.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (boxNo != null && !boxNo.isEmpty()) {
					Intent intent = new Intent(PP004Activity.this,
							PP003Activity.class);
					intent.putExtra("boxNo", boxNo);
					startActivity(intent);
				}
			}
		});
	}

	// AmassHttpResponseHandler
	private AmassHttpResponseHandler<BoxPP004> httpHandler = new AmassHttpResponseHandler<BoxPP004>() {
		protected void OnSuccess(BoxPP004 response) {
			super.OnSuccess(response);
			try{
				BoxPP004 responseBox = (BoxPP004) response;
				if(responseBox != null && responseBox.getBoxNo()!=null){	
					tvTitleNoBox.setText(responseBox.getBoxNo()); // 集箱号
					imgLiner.setVisibility(View.VISIBLE);
					boxNoText.setText(responseBox.getBoxNo());
					boxNoText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
					boxNoText.setTextColor(Color.BLUE);
					boxNo = responseBox.getBoxNo();
					tvDetail.setVisibility(View.VISIBLE);
					tvDetail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
					if(responseBox.getPackingDeadline() != null){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						deadlineTime.setText(sdf.format(responseBox.getPackingDeadline()));
					}
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
						workStatus.setText(R.string.status_work_undo);
						workStatus.setBackgroundResource(0);
					}
					pickingRemark.setText(responseBox.getPackingRemarks());
					list = responseBox.getOrderList();
					if(list.size() > 0){
						adapter = new PP004Adapter(getApplicationContext(), list);
						adapter.setList(list);
						adapter.notifyDataSetChanged(); 
					}
					bListView.setAdapter(adapter);
					bListView.setOnItemClickListener(new ItemClickListener());
				}else{
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_001);
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
							intent.setClass(PP004Activity.this, PP005Activity.class); 
							intent.putExtra("boxNo", boxNo);
							intent.putExtra("orderCd", orderCd);
							intent.putExtra("IsEdit", IsEdit);
							startActivity(intent);
							finish(); // 结束当前活动
						}else if("1".equals(unPackingFlg)){
							//有掏箱记录未完成
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP004_error_unPackingRemain);
						}else{
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
						}
					}else{
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
					}
				}
			}, true);			
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
				//final BarCodeResult result = Utils.analysisBarCode(data.getStringExtra("SCAN_RESULT"),"05");
				final BarCode05 barCode05 = new BarCode05();
				boolean result05 = barCode05.paserBarCode(data.getStringExtra("SCAN_RESULT"));
				//初始化时扫描集装号
				if(boxNo == null){					
					if(result05){
						Map<String,Object> requestData = new HashMap<String,Object>();
						requestData.put("boxNo", barCode05.getBoxNo());			
						Type type = new TypeToken<BoxPP004>(){}.getType();
						NetworkHelper.getInstance().postJsonData(this, METHOD_URL, requestData,type , httpHandler, true);
					}else{
						//扫描到错误的集装箱号
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP004_error_wrongBoxNo);
					}			
				}else {
					//集装箱切换时
					if(result05){
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
									NetworkHelper.getInstance().postJsonData(PP004Activity.this, METHOD_URL, requestData,type , httpHandler, true);
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
							Map<String,Object> requestData = new HashMap<String,Object>();
							requestData.put("boxNo", boxNo);			
							Type type = new TypeToken<BoxPP004>(){}.getType();
							NetworkHelper.getInstance().postJsonData(this, METHOD_URL, requestData,type , httpHandler, true);
						}
					}else{
						//扫描桩脚牌时
						//final BarCodeResult result1 = Utils.analysisBarCode(data.getStringExtra("SCAN_RESULT"),"02");						
						final BarCode02 barCode02 = new BarCode02();
						boolean result02 = barCode02.paserBarCode(data.getStringExtra("SCAN_RESULT"));
						if(result02){
							Map<String,Object> requestData = new HashMap<String,Object>();
							requestData.put("orderCd", barCode02.getOrderCd());
							requestData.put("depotDtId", barCode02.getDepotDtId());
							//根据orderCd获取集箱号
							NetworkHelper.getInstance().postJsonData(PP004Activity.this, "PP004getBoxbyOrderCd", requestData,BoxPP004.class ,
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
									//判断是否是该集装箱的货物							
									if(boxNo.equals(boxNoByOrder)){
										//判断是否有掏箱记录未完成
										if(!responseBox.getUnPackingFlg()){
											//跳转到拣货明细画面
											Intent intent = new Intent(); // 建立Intent
											intent.setClass(PP004Activity.this, PP005Activity.class); // 设置活动
											intent.putExtra("boxNo", boxNo);
											intent.putExtra("depotDtId", barCode02.getDepotDtId());
											intent.putExtra("orderCd", barCode02.getOrderCd());
											intent.putExtra("coloader", barCode02.getCoLoader());
											intent.putExtra("depotNum", barCode02.getDepotNum());
											intent.putExtra("IsEdit", IsEdit);
											startActivity(intent);
											finish(); // 结束当前活动
										}else{
											Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP004_error_unPackingRemain);
										}
									}else{				    	
										Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_001);
									}
								}
							}, true);
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

	public AmassHttpResponseHandler<BoxPP004> getHttpHandler() {
		return httpHandler;
	}

	public void setHttpHandler(AmassHttpResponseHandler<BoxPP004> httpHandler) {
		this.httpHandler = httpHandler;
	}

	public String getBoxNoByOrder() {
		return boxNoByOrder;
	}

	public void setBoxNoByOrder(String boxNoByOrder) {
		this.boxNoByOrder = boxNoByOrder;
	}
}
