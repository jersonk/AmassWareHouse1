package com.amassfreight.warehouse.ui;

import java.io.Serializable;
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
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.bean.BarCode03;
import com.amassfreight.base.bean.BarCodeResult;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.CommonResult;
import com.amassfreight.domain.RC001Deliver;
import com.amassfreight.domain.RC001Deliver_batch;
import com.amassfreight.domain.RC001PileCard;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.amassfreight.warehouse.ui.dialogs.SelectPrintDialog;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;

public class RC001Activity extends BaseActivity implements OnDialogOkListener {
	private TextView tv_deliv_id;
	//private Button btn_print_jincang_confirm;
	private TextView tv_deliv_date;
	private TextView tv_tihuoren;
	private TextView tv_dr_no;
	private TextView tv_contact_tel;
	private TextView tv_deliv_type;
	private TextView tv_deliv_comment;
	//private ListView lv_tv_deliv_bat;
	private TextView tv_deliv_status_fin;
	private TextView tv_delivnm_already;
	private TextView tv_flag;              //司机自分货
	private TextView tv_total_num;         //总放货件数
	private ExpandableListView expandableListView;
	private Button btn_pilecard;
	private CheckBox chk_print_all;

	// 所有的批次
	private List<RC001Deliver_batch> deliver_batch_list = new ArrayList<RC001Deliver_batch>();
	// 该放货编号对应的所有的桩脚牌
	private List<RC001PileCard> total_pilecard = new ArrayList<RC001PileCard>();
	// 本次放货的桩脚牌 add by sdhuang 20150115
	private List<RC001PileCard> delived_pilecard = new ArrayList<RC001PileCard>();
	private Button btn_deliv_ok;
	private static final int REQUEST_CODE_SCAN= 11;	
	private String depot_dt_id;
	private String deliv_id;// 放货编号	
	private String flag;    // 司机自分货标志
	private int totalDevNum = 0;    //总放货件数
	private int remainDevNum = 0;   //剩余的可放件数
	private MyExpandableListViewAdapter expandableAdapter;	
	BarCodeResult barCodeResult = null;	
	private Boolean[][] checks; // 用于保存checkBox的选择状态
	private boolean savedFlag = false;  //放货完成按钮是否点击状态
	
	@SuppressWarnings("unchecked")
	@SuppressLint("CutPasteId")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rc001);
		setupActionBar();
		
		tv_deliv_id = (TextView) findViewById(R.id.tv_deliv_id);
		//btn_print_jincang_confirm = (Button) findViewById(R.id.btn_print_jincang_confirm);
		//btn_print_jincang_confirm.setEnabled(false);
		tv_deliv_date = (TextView) findViewById(R.id.tv_deliv_date);
		tv_tihuoren = (TextView) findViewById(R.id.tv_tihuoren);
		tv_dr_no = (TextView) findViewById(R.id.tv_dr_no);
		tv_contact_tel = (TextView) findViewById(R.id.tv_contact_tel);
		tv_deliv_type = (TextView) findViewById(R.id.tv_deliv_type);
		tv_deliv_comment = (TextView) findViewById(R.id.tv_deliv_comment);
		//lv_tv_deliv_bat = (ListView) findViewById(R.id.lv_tv_deliv_bat);
		expandableListView = (ExpandableListView) findViewById(R.id.lv_tv_deliv_bat);
		tv_deliv_status_fin = (TextView) findViewById(R.id.tv_deliv_status_fin);
		tv_delivnm_already = (TextView) findViewById(R.id.tv_delivnm_already);
		tv_flag = (TextView) findViewById(R.id.tv_flag);
		tv_total_num = (TextView) findViewById(R.id.tv_total_num);
		btn_deliv_ok = (Button) findViewById(R.id.btn_deliv_ok);
		btn_pilecard = (Button) findViewById(R.id.btn_pilecard);
		chk_print_all = (CheckBox) findViewById(R.id.chk_print_all);
		chk_print_all.setEnabled(false);
		btn_pilecard.setVisibility(View.GONE);
		//全选按钮事件
		chk_print_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				for(int i = 0;i < deliver_batch_list.size();i++){
					int num = 0;
					for(int j =0;j < deliver_batch_list.get(i).getPilecards().size();j++){
						if(deliver_batch_list.get(i).getPilecards().get(j).getFlg_deliv() == 1 
					        && deliver_batch_list.get(i).getPilecards().get(j).getCounts_deliv() != 0
					        && Integer.parseInt(deliver_batch_list.get(i).getPilecards()
					        		.get(j).getKucunCount()) != 0){
							num++;
							deliver_batch_list.get(i).getPilecards().get(j).setPrintChecked(chk_print_all.isChecked());
						}
					}
					if(num > 0){
					    deliver_batch_list.get(i).setChecked(chk_print_all.isChecked());
					}
				}
				expandableAdapter.notifyDataSetChanged();
			}
			
		});
		//打印桩脚牌
		btn_pilecard.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				// 重新取所有的桩脚牌list，刷新打印状态
				total_pilecard.clear();
				for (RC001Deliver_batch c : deliver_batch_list) {
					total_pilecard.addAll(c.getPilecards());
				}
				int num = 0;				
				for(int i = 0;i < total_pilecard.size();i++){
					if(total_pilecard.get(i).isPrintChecked()){
						num++;
					}
				}
				if(num > 0){				
					Map<String,Object> requestData = new HashMap<String,Object>(); 
					// modify by sdhuang 20150115 减小提交的数据list,解决大数据提交时报的500Error
					//requestData.put("total_pilecard", total_pilecard);	
					getDelivList();
					requestData.put("total_pilecard", delived_pilecard);
					NetworkHelper.getInstance().postJsonData(RC001Activity.this, 
							"RC001_Print_PileCards", requestData,CommonResult.class , printHandler, true);
				}else{
					Utils.showAlertDialogIntMsg(_thisActivity, R.string.msg_common_print_none);
				}
			}
		});
		/*btn_print_jincang_confirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				List<GoodsConfirmPrint> printList = new ArrayList<GoodsConfirmPrint>();
				for(int i = 0 ; i < deliver_batch_list.size() ; i++ ){
					if(checks[i]){
						GoodsConfirmPrint print = new GoodsConfirmPrint();
						print.setDepotID(deliver_batch_list.get(i).getDepot_id());
						print.setNoBatch(deliver_batch_list.get(i).getNo_batch());
						printList.add(print);
					}
				}
				if(printList != null && printList.size() > 0){
					Map<String, Object> p1 = new HashMap<String, Object>();
					p1.put("PrintList", printList);					
					NetworkHelper.getInstance().postJsonData(_thisActivity,
							"RC002_PrintGoodsConfirmationList", p1,CommonResult.class, 
							new AmassHttpResponseHandler<CommonResult>() {
						@Override
						protected void OnSuccess(CommonResult response) {
							super.OnSuccess(response);
							CommonResult result = (CommonResult) response;
							if(result != null && result.getFlag()){
								Utils.showAlertDialogIntMsg(_thisActivity, R.string.msg_common_print_success);
							}else{
								Utils.showAlertDialogIntMsg(_thisActivity,  R.string.msg_common_print_failed);
							}
						}
					}, true);
				}else{
					Utils.showAlertDialogIntMsg(_thisActivity, R.string.msg_common_print_none);
				}				
			}});*/
		btn_deliv_ok.setVisibility(View.GONE);
		//btn_deliv_ok.setOnClickListener(this);		
		btn_deliv_ok.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//确认放货完成
				AlertDialog.Builder builder = new AlertDialog.Builder(
						new ContextThemeWrapper(_thisActivity,
								android.R.style.Theme_Holo_Light));
				builder.setIcon(R.drawable.ic_launcher);
				builder.setCancelable(false);
				builder.setMessage(R.string.RC002_warning_delivConfirm);
				builder.setTitle(R.string.app_name);
				builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Map<String,Object> requestData = new HashMap<String,Object>(); 
						requestData.put("deliv_id", deliv_id);
						/*NetworkHelper.getInstance().postJsonData(RC001Activity.this, "RC001_deliv_ok", requestData, null, null, false);
						btn_deliv_ok.setVisibility(View.GONE);
						tv_deliv_status_fin.setText(R.string.RC001_delivStatus_done);
						tv_deliv_status_fin.setBackgroundResource(R.color.gay);*/
						
						// modify by sdhuang 20150115 减小提交的数据list,解决大数据提交时报的500Error
						//requestData.put("total_pilecard", total_pilecard);
						getDelivList();
						requestData.put("total_pilecard", delived_pilecard);
						NetworkHelper.getInstance().postJsonData(RC001Activity.this, 
								"RC001_deliv_finish", requestData,CommonResult.class , allDelivHandler, true);
					}
				});
				builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();				
			}});
		
		Intent intent=getIntent(); 
		deliv_id = intent.getStringExtra("deliv_id");
		deliver_batch_list = (List<RC001Deliver_batch>) intent.getSerializableExtra("deliver_batch_list");
		deliv_id_operation();
	}
	
/*	@Override
	protected void onRestart() {
		super.onRestart();
		deliv_id_operation();
	}
*/
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_scan, menu);
		return super.onCreateOptionsMenu(menu);
	}*/
	
	/*
	 * 创建右上角按钮
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_scanandprint, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan_action:
			Intent intent = new Intent(this, CaptureActivity.class);
			intent.putExtra("SCAN_MODE", "SCAN_MODE");
			startActivityForResult(intent, REQUEST_CODE_SCAN);
			//this.finish();
			return true;
		case R.id.print_action:
			SelectPrintDialog dlg = new SelectPrintDialog(1, this);
			dlg.createDialog(this).show();
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
		String barCode = data.getStringExtra("SCAN_RESULT");		
		
		switch (requestCode) {
		case REQUEST_CODE_SCAN:
			final BarCode03 code3 = new BarCode03();
			if(deliv_id == null){
				if(code3.paserBarCode(barCode)){
					Type responseType = new TypeToken<RC001Deliver>() {}.getType();
					Map<String, Object> requestData = new HashMap<String, Object>();
					requestData.put("deliv_id", code3.getDelivId());

					NetworkHelper.getInstance().postJsonData(this,"RC001_getDeliverInfo", 
							requestData, responseType,httpHandler1, true);
					//deliv_id = code3.getDelivId();
					//deliv_id_operation();
				}else{
					// 001扫描出来的不是放货编号(扫描到的信息不属于本次操作范围，请重试)
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC001_error1);
					return;
				}
			}else{
				//放货单切换
				if(code3.paserBarCode(barCode)){
					if(!code3.getDelivId().equals(deliv_id)){
						//弹出确认集装箱切换dialog
						AlertDialog.Builder builder = new AlertDialog.Builder(
								new ContextThemeWrapper(_thisActivity,
										android.R.style.Theme_Holo_Light));
						builder.setIcon(R.drawable.ic_launcher);
						builder.setCancelable(false);
						builder.setMessage(R.string.RC001_warning_changeDev);
						builder.setTitle(R.string.app_name);
						builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								deliver_batch_list.clear();
								Type responseType = new TypeToken<RC001Deliver>() {}.getType();
								Map<String, Object> requestData = new HashMap<String, Object>();
								requestData.put("deliv_id", code3.getDelivId());
								NetworkHelper.getInstance().postJsonData(RC001Activity.this,"RC001_getDeliverInfo", 
										requestData, responseType,httpHandler1, true);
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
						deliv_id_operation();
					}
				}else{
					final BarCode02 code2 = new BarCode02();
					if (code2.paserBarCode(barCode)) {
						depot_dt_id = code2.getDepotDtId();
						// 更新UI界面的数据,放货的批次的状态发生了改变
						pilecard_operation();
					}else{
						// 001扫描到的信息不属于本次操作范围，请重试
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC001_error1);
						return;
					}
					
				}
			}
		}
	}

	/**
	 * 根据放货编号查询放货的信息
	 * @param deliv_id
	 */
	@SuppressLint("SimpleDateFormat")
	public void deliv_id_operation() {
		if(deliv_id != null){
			Type responseType = new TypeToken<RC001Deliver>() {}.getType();
			Map<String, Object> requestData = new HashMap<String, Object>();
			requestData.put("deliv_id", deliv_id);

			NetworkHelper.getInstance().postJsonData(this,"RC001_getDeliverInfo", 
					requestData, responseType,httpHandler1, true);
		}
	}

	protected String getDeliv_type(String type_deliv) {
		//0：正常放货 1：三检放货 2：熏蒸放货
		switch (Integer.parseInt(type_deliv)) {
		case 0:
			return getResources().getString(R.string.RC001_delivType_0);
		case 1:
			return getResources().getString(R.string.RC001_delivType_1);
		case 2:
			return getResources().getString(R.string.RC001_delivType_2);
		default:
			break;
		}
		return null;
	}

	/**
	 * 扫描桩脚牌的二维码,得到的货物明细表的id
	 * 
	 * @param depot_dt_id
	 */
	public void pilecard_operation() {
		try {
	    	/*//判断是否有增值服务未完成
	    	int count = 0;
	    	if(depotVasList != null){
	    		for(int i = 0 ; i < depotVasList.size() ; i++){	    		
	    			if(!"2".equals(depotVasList.get(i).getFlgService())){
	    				count++;
	    			}
	    		}
	    	}
	    	if(count == 0){*/
	    		Map<String, Object> requestData = new HashMap<String, Object>();
	    		requestData.put("depot_dt_id", depot_dt_id); // 当前扫描得到的货物明细表
	    		requestData.put("deliv_id", deliv_id); // 当前扫描得到的放货编号

	    		Type responseType = new TypeToken<CommonResult>() {}.getType();
	    		NetworkHelper.getInstance().postJsonData(this,"RC001_check_depot_dt_id", requestData,
	    				responseType,httpHandler2, true);
	    	/*}else{
	    		Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC001_msg_VasError);
	    	}*/
	    	
		} catch (Exception e) {
			e.printStackTrace();
			Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC001_error3);
			return;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private AmassHttpResponseHandler<RC001Deliver> httpHandler1 = new AmassHttpResponseHandler<RC001Deliver>() {	
		@Override
		protected void OnSuccess(RC001Deliver response) {
			super.OnSuccess(response);

			if (response != null && response.getDeliv_id() != null) {
				// 拿到返回的数据
				deliv_id = response.getDeliv_id();
				tv_deliv_id.setText(response.getDeliv_id());
				if(response.getDt_forward() != null){
				    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
					tv_deliv_date.setText(sdf.format(response.getDt_forward()));
			    }
				tv_tihuoren.setText(response.getForward_user());
				tv_dr_no.setText(response.getNo_car_lice());
				tv_contact_tel.setText(response.getTel_no());
				tv_deliv_type.setText(response.getType_deliv());
				tv_total_num.setText(response.getNum());
				totalDevNum = Integer.parseInt(response.getNum());
				flag = response.getFlag();
				if("1".equals(response.getFlag())){
					tv_flag.setText(R.string.PP005_shutoutFlag_ok);
					tv_flag.setTextColor(Color.RED);
				}else{
					tv_flag.setText(R.string.PP005_shutoutFlag_ng);
				}
				
				if("1".equals(response.getFlg_deliv())){
					tv_deliv_status_fin.setText(R.string.RC001_delivStatus_done);
					tv_deliv_status_fin.setBackgroundResource(R.color.gay);
					savedFlag = true;
					chk_print_all.setEnabled(true);
					btn_pilecard.setVisibility(View.VISIBLE);
				}else{
					tv_deliv_status_fin.setText(R.string.RC001_delivAll_unFinish);
					tv_deliv_status_fin.setBackgroundResource(R.color.yellow);
					savedFlag = false;
					chk_print_all.setEnabled(false);
					btn_pilecard.setVisibility(View.GONE);
				}
				if(response.getType_deliv() != null){
				    tv_deliv_type.setText(getDeliv_type(response.getType_deliv()));
				}
				if(response.getForward_remark()!=null){
					tv_deliv_comment.setText(response.getForward_remark().trim());
				}else{
					tv_deliv_comment.setText(null);
				}
				//增值服务一览
				//depotVasList = response.getDepotVasList();
				// 根据里层的设置外层的数据
				if(deliver_batch_list == null || deliver_batch_list.size() <= 0){
					deliver_batch_list = response.getRl();
				}				
				// 拿到所有的桩脚牌
				total_pilecard.clear();
				for (RC001Deliver_batch c : deliver_batch_list) {
					total_pilecard.addAll(c.getPilecards());
				}
				//设置默认checkbox不选中
				checks = new Boolean[deliver_batch_list.size()][total_pilecard.size()];
				for(int i = 0; i < deliver_batch_list.size(); i++) {
					for(int j = 0; j < total_pilecard.size(); j++){
					    checks[i][j] = false;
					}
				}
				//计算已放货件数
				int count = 0;
				for (RC001PileCard card : total_pilecard) {
					if (1 == card.getFlg_deliv()) {
						count = count + card.getCounts_deliv();
					}
				}
				tv_delivnm_already.setText("("+String.valueOf(count)+")");
				remainDevNum = totalDevNum - count;
				if("1".equals(flag)){
					//判断司机自分货是否放货完成
					if(remainDevNum == 0 && !"1".equals(response.getFlg_deliv())){
						btn_deliv_ok.setVisibility(View.VISIBLE);
					}
				}else{
					//设置放货完成按钮可见
					if(total_pilecard.size() > 0 && countDevFinish() == total_pilecard.size()){
						if(!"1".equals(response.getFlg_deliv())){
							btn_deliv_ok.setVisibility(View.VISIBLE);
						}
					}
				}

				expandableAdapter = new MyExpandableListViewAdapter(_thisActivity);
				expandableListView.setAdapter(expandableAdapter);
				expandableAdapter.notifyDataSetChanged();
				expandableListView.setGroupIndicator(null);
				int groupCount = expandableAdapter.getGroupCount();
				for (int i=0; i<groupCount; i++) {
					expandableListView.expandGroup(i);
				}
				expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
					@Override 
					public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) { 
						return true;
					}
				});				
				expandableListView.setOnChildClickListener(new OnChildClickListener() {				     
				    @Override
				    public boolean onChildClick(ExpandableListView parent, View v,
				            int groupPosition, int childPosition, long id) {
				    	Intent detailIntent = new Intent(_thisActivity,
								DN004Activity.class);
						String strPos = null; // 库区
						String strLocation = null;// 库位
						if (deliver_batch_list.get(groupPosition).getPilecards()
								.get(childPosition).getPosAndLoc() != null
								&& deliver_batch_list.get(groupPosition).getPilecards()
								.get(childPosition).getPosAndLoc().split("-").length > 0) {
							strPos = deliver_batch_list.get(groupPosition).getPilecards()
									.get(childPosition).getPosAndLoc().split("-")[0];
							if (deliver_batch_list.get(groupPosition).getPilecards()
									.get(childPosition).getPosAndLoc().split("-").length > 1) {
								strLocation = deliver_batch_list.get(groupPosition).getPilecards()
										.get(childPosition).getPosAndLoc().split("-")[1];
							}
						}
						Bundle detailBundle = new Bundle();
						detailBundle.putString("Type", "8"); // 更新标识
						detailBundle.putString("cdOrderPublic",
								deliver_batch_list.get(groupPosition).getPilecards()
								.get(childPosition).getCd_order_public()); // 进仓编号共通
						detailBundle.putString("noBatch", deliver_batch_list.get(groupPosition)
								.getPilecards().get(childPosition).getNo_batch());// 批次
						detailBundle.putString("depotDtId",deliver_batch_list.get(groupPosition)
								.getPilecards().get(childPosition).getDepot_dt_id());// 货物明细ID
						detailBundle.putString("noPilecard",deliver_batch_list.get(groupPosition).
								getPilecards().get(childPosition).getNo_pilecard());// 桩脚牌
						detailBundle.putString("pos", strPos);// 库区
						detailBundle.putString("location", strLocation);// 库位
						detailIntent.putExtras(detailBundle);
						startActivity(detailIntent);
				        return true;
				    }
				});

				/*//这里是控制group展开的效果，只展开一个
				expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int groupPosition) {
						for (int i = 0; i < expandableAdapter.getGroupCount(); i++) {
							if (groupPosition != i) {
								expandableListView.collapseGroup(i);
							}
						}
					}
				});*/
			
			}else{
				// 002 扫描到的放货编号不存在，请联系计划员确认
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC001_error2);
			}
		}
	};
	
	private AmassHttpResponseHandler<CommonResult> httpHandler2 = new AmassHttpResponseHandler<CommonResult>() {
		@Override
		protected void OnSuccess(CommonResult response) {
			super.OnSuccess(response);
			CommonResult result = (CommonResult) response;
			if(result != null){
				if(result.getFlag()){
					depot_dt_id =  result.getDepotDtId();
					if(total_pilecard.size()>0){
						for (RC001PileCard p : total_pilecard) {
							if (depot_dt_id.equals(p.getDepot_dt_id())) {
								if(savedFlag) {
									Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC001_error7);
									return;
								}			

								// 不相等的场合，打开放货桩脚牌确认画面
								Intent intent = new Intent(RC001Activity.this,RC002Activity.class);
								intent.putExtra("pilecard", p);
								intent.putExtra("deliv_id",deliv_id);
								intent.putExtra("depot_dt_id", depot_dt_id);
								intent.putExtra("flag", flag);
								intent.putExtra("savedFlag", savedFlag);
								intent.putExtra("remainDevNum", remainDevNum);
								intent.putExtra("deliver_batch_list", (Serializable) deliver_batch_list);
								startActivity(intent);
								RC001Activity.this.finish();   
								return;
							} 
						}
					}
				}else{
					if("004".equals(result.getErrorCd())){
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC001_error3);
					}else if("003".equals(result.getErrorCd())){
						//Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC001_msg_VasError);
						//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_VasError);
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_VasDepotError);
					}
					else{
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
					}
				}
			}else{
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
			}
			
		}
	};
	
	//计算放货完成的桩脚牌件数
	public int countDevFinish(){
		int count = 0;
		for (RC001PileCard card : total_pilecard) {
			if (1 == card.getFlg_deliv()) {
				count++;
			}
		}
		return count;
	}

	//放货一览
	class MyExpandableListViewAdapter extends BaseExpandableListAdapter {
		private Context context;

		public MyExpandableListViewAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getGroupCount() {
			return deliver_batch_list.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return deliver_batch_list.get(groupPosition).getPilecards().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return deliver_batch_list.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return deliver_batch_list.get(groupPosition).getPilecards().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder groupHolder = null;
			final int pos = groupPosition;
			if (convertView == null) {
				getLayoutInflater();
				convertView = (View) LayoutInflater.from(context).inflate(
						R.layout.activity_rc001_listview_item, null);
				groupHolder = new GroupHolder();
				groupHolder.tv_deliv_pici = (TextView) convertView
						.findViewById(R.id.tv_deliv_pici);
				groupHolder.tv_jicang_id = (TextView) convertView
						.findViewById(R.id.tv_jicang_id);
				groupHolder.tv_kucun_count = (TextView) convertView
						.findViewById(R.id.tv_kucun_count);
				groupHolder.tv_fanghuo_status = (TextView) convertView
						.findViewById(R.id.tv_fanghuo_status);
				groupHolder.checkBox_group = (CheckBox) convertView
						.findViewById(R.id.chk_print_group);

				convertView.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) convertView.getTag();
			}
			//解决checkbox获取了group的焦点而不能展开的问题
			groupHolder.checkBox_group.setClickable(true);
			groupHolder.checkBox_group.setFocusable(false);
			RC001Deliver_batch rb = deliver_batch_list.get(groupPosition);
			groupHolder.tv_deliv_pici.setText(rb.getNo_batch());
			groupHolder.tv_jicang_id.setText(rb.getCd_order_public());
			groupHolder.tv_kucun_count.setText(rb.getKucunCount());			
			convertView.setBackgroundResource(R.color.grouplist_color);
				
			List<RC001PileCard> subpilecard = rb.getPilecards();			
			int count_flag_deliv1 = 0;
			int count_flag_deliv0 = 0;
			for (RC001PileCard rpc : subpilecard) {		
				if(rpc.getFlg_deliv() == 1) {
					count_flag_deliv1++;
				}				
				if(rpc.getFlg_deliv() == 0) {
					count_flag_deliv0++;
				}
			}
			
			if(count_flag_deliv1 == subpilecard.size()) {
				// 全部为1
				rb.setFumigationStatus(getResources().getString(R.string.RC001_delivStatus_done));
				groupHolder.tv_fanghuo_status.setBackgroundResource(R.color.gay);
			} else if(count_flag_deliv0 == subpilecard.size()) {
				// 全部为0
				rb.setFumigationStatus(getResources().getString(R.string.RC001_delivStatus_undo));
				groupHolder.tv_fanghuo_status.setBackgroundResource(0);
			} else {
				rb.setFumigationStatus(getResources().getString(R.string.RC001_delivStatus_doing));
				groupHolder.tv_fanghuo_status.setBackgroundResource(R.color.lightBlue);
			}
			groupHolder.tv_fanghuo_status.setText(rb.getFumigationStatus());
			
			//计算child中已放货且不是全部放货的个数	
			int groupCheck = 0;
			for(int n = 0; n < rb.getPilecards().size();n++){
				if(rb.getPilecards().get(n).getFlg_deliv() == 1 && 
						rb.getPilecards().get(n).getCounts_deliv() != 0 &&
						Integer.parseInt(rb.getPilecards().get(n).getKucunCount()) != 0){
						groupCheck++;
				}
			}
			if(!savedFlag || groupCheck == 0){
				groupHolder.checkBox_group.setEnabled(false);
			}else{
				groupHolder.checkBox_group.setEnabled(true);
			}
			groupHolder.checkBox_group.setOnClickListener(new CheckBox.OnClickListener(){

				@Override
				public void onClick(View v) {
					CheckBox checkBox = (CheckBox) v;
					RC001Deliver_batch rb = deliver_batch_list.get(pos);
					rb.setChecked(checkBox.isChecked());
					//将child中已放货且不是全部放货的打印flag设置为group的打印状态					
					for(int n = 0; n < rb.getPilecards().size();n++){
						if(rb.getPilecards().get(n).getFlg_deliv() == 1 && 
								rb.getPilecards().get(n).getCounts_deliv() != 0 &&
								Integer.parseInt(rb.getPilecards().get(n).getKucunCount()) != 0){
							rb.getPilecards().get(n).setPrintChecked(checkBox.isChecked());
						}
					}
					notifyDataSetChanged();
				}				
			});
			groupHolder.checkBox_group.setChecked(rb.isChecked());
			
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View subview, ViewGroup parent) {
			ItemHolder itemHolder = null;
			final int groupPos = groupPosition;
			final int childPos = childPosition;
			if (subview == null) {
				getLayoutInflater();
				subview = (View) LayoutInflater.from(context).inflate(
						R.layout.activity_rc001_listview_item_item, null);
				itemHolder = new ItemHolder();
				itemHolder.tv_zhuangjiapai_id = (TextView) subview.findViewById(R.id.tv_zhuangjiapai_id);
				itemHolder.tv_fanghuo_status = (TextView) subview.findViewById(R.id.tv_fanghuo_status);
				itemHolder.tv_kucun_count = (TextView) subview.findViewById(R.id.tv_kucun_count);
				itemHolder.tv_fanghuo_count = (TextView) subview.findViewById(R.id.tv_fanghuo_count);
				itemHolder.tv_posAndLoc = (TextView) subview.findViewById(R.id.tv_pos);
				itemHolder.checkBox = (CheckBox) subview.findViewById(R.id.chk_print);
				subview.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) subview.getTag();
			}
			itemHolder.checkBox.setClickable(true);
			itemHolder.checkBox.setFocusable(false);
			RC001PileCard cardNo = deliver_batch_list.get(groupPosition).getPilecards().get(childPosition);
			if("1".equals(String.valueOf(cardNo.getFlg_deliv()))){
				itemHolder.tv_fanghuo_status.setText(R.string.RC001_delivStatus_done);
				itemHolder.tv_fanghuo_status.setTextColor(Color.BLUE);
			}else{
				itemHolder.tv_fanghuo_status.setText(R.string.RC001_delivStatus_undo);
				itemHolder.tv_fanghuo_status.setTextColor(Color.BLACK);
			}
			itemHolder.tv_zhuangjiapai_id.setText(cardNo.getNo_pilecard());
			itemHolder.tv_kucun_count.setText(cardNo.getKucunCount());
			itemHolder.tv_fanghuo_count.setText(String.valueOf(cardNo.getCounts_deliv()));
			itemHolder.tv_posAndLoc.setText(cardNo.getPosAndLoc());
			if (childPosition % 2 == 0) {
				subview.setBackgroundResource(R.color.listview_back_odd);
			} else {
				subview.setBackgroundResource(R.color.listview_back_uneven);
			}
			
			if(savedFlag && cardNo.getFlg_deliv() == 1 
					&& cardNo.getCounts_deliv() != 0
					&& Integer.parseInt(cardNo.getKucunCount()) != 0){
				itemHolder.checkBox.setEnabled(true);
			}else{
				itemHolder.checkBox.setEnabled(false);
			}
			
			itemHolder.checkBox.setOnClickListener(new CheckBox.OnClickListener(){

				@Override
				public void onClick(View v) {
					CheckBox checkBox = (CheckBox) v;
					RC001Deliver_batch rb = deliver_batch_list.get(groupPos);
					rb.getPilecards().get(childPos).setPrintChecked(checkBox.isChecked());
					//是否有未勾的可打印项
					int count = 0;
					int count1 = 0;
					for(int n = 0; n < rb.getPilecards().size();n++){
						if(rb.getPilecards().get(n).getFlg_deliv() == 1 && 
								rb.getPilecards().get(n).getCounts_deliv() != 0 &&
								Integer.parseInt(rb.getPilecards().get(n).getKucunCount()) != 0 ){
							count1++;
							if(rb.getPilecards().get(n).isPrintChecked()){
							    count++;
							}
						}
					}
					if(count == 0){
						rb.setChecked(false);
					}else{
						if(count1 == count){
							rb.setChecked(true);
						}
					}
					notifyDataSetChanged();
				}				
			});
			itemHolder.checkBox.setChecked(cardNo.isPrintChecked());
			return subview;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
	
	private AmassHttpResponseHandler<CommonResult> allDelivHandler = new AmassHttpResponseHandler<CommonResult>() {
		protected void OnSuccess(CommonResult response) {
			super.OnSuccess(response);
			CommonResult result = (CommonResult) response;
			if(result != null){
				//处理成功
				if(result.getFlag()){
					savedFlag = true;
					btn_deliv_ok.setVisibility(View.GONE);
					chk_print_all.setEnabled(true);
					btn_pilecard.setVisibility(View.VISIBLE);
					tv_deliv_status_fin.setText(R.string.RC001_delivStatus_done);
					tv_deliv_status_fin.setBackgroundResource(R.color.gay);
					tv_delivnm_already.setText("("+String.valueOf(totalDevNum)+")");
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.msg_common_success);	
					deliver_batch_list.clear();
					deliv_id_operation();
				//处理失败
				}else{	
					if (result.getRemark().trim() != ""){
						Utils.showAlertDialog(_thisActivity, result.getRemark().trim());
					}
					else {
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);			
					}
				}
			}else{
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
			}
			
		}
	};
	
	private AmassHttpResponseHandler<CommonResult> printHandler = new AmassHttpResponseHandler<CommonResult>() {
		protected void OnSuccess(CommonResult response) {
			super.OnSuccess(response);
			CommonResult result = (CommonResult) response;
			if(result != null){
				//处理成功
				if(result.getFlag()){
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.msg_common_print_success);	
				//处理失败
				}else{					
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);					
				}
			}else{
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
			}
			
		}
	};
	
	//返回
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//判断是否放货完成
			if(!savedFlag){
				AlertDialog.Builder builder = new AlertDialog.Builder(
						new ContextThemeWrapper(_thisActivity,
								android.R.style.Theme_Holo_Light));
				builder.setIcon(R.drawable.ic_launcher);
				builder.setCancelable(false);
				builder.setMessage(R.string.RC002_backConfirm);
				builder.setTitle(R.string.app_name);
				builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
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
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}	
	
	// 获取实际放货的桩脚牌列表
	private void getDelivList(){
		delived_pilecard.clear();
		for(int i=0;i<total_pilecard.size();i++){
			if(total_pilecard.get(i).getFlg_deliv() ==1 
					&& total_pilecard.get(i).getCounts_deliv() > 0){
				delived_pilecard.add(total_pilecard.get(i));
			}
		}
	}
	
	class GroupHolder {
		public TextView tv_deliv_pici;
		public TextView tv_jicang_id;
		public TextView tv_kucun_count;
		public TextView tv_fanghuo_status;
		public CheckBox checkBox_group;
	}

	class ItemHolder {
		public TextView tv_zhuangjiapai_id;
		public TextView tv_fanghuo_status;
		public TextView tv_kucun_count;
		public TextView tv_fanghuo_count;
		public TextView tv_posAndLoc;
		public CheckBox checkBox;
	}
	
	public AmassHttpResponseHandler<CommonResult> getHttpHandler2() {
		return httpHandler2;
	}

	public void setHttpHandler2(AmassHttpResponseHandler<CommonResult> httpHandler2) {
		this.httpHandler2 = httpHandler2;
	}

	public AmassHttpResponseHandler<RC001Deliver> getHttpHandler1() {
		return httpHandler1;
	}

	public void setHttpHandler1(AmassHttpResponseHandler<RC001Deliver> httpHandler1) {
		this.httpHandler1 = httpHandler1;
	}

	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
	}

	public AmassHttpResponseHandler<CommonResult> getAllDelivHandler() {
		return allDelivHandler;
	}

	public void setAllDelivHandler(AmassHttpResponseHandler<CommonResult> allDelivHandler) {
		this.allDelivHandler = allDelivHandler;
	}

	public AmassHttpResponseHandler<CommonResult> getPrintHandler() {
		return printHandler;
	}

	public void setPrintHandler(AmassHttpResponseHandler<CommonResult> printHandler) {
		this.printHandler = printHandler;
	}

}
