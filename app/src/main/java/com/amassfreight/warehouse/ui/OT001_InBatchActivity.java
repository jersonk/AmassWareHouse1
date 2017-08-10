package com.amassfreight.warehouse.ui;

import java.lang.reflect.Type;
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
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.OT001DetailData;
import com.amassfreight.domain.OT001ScanData;
import com.amassfreight.domain.SelectDict;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.amassfreight.warehouse.ui.dialogs.SelectPrintDialog;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;

public class OT001_InBatchActivity extends BaseActivity implements OnDialogOkListener {
	
	private LayoutInflater depotInflater;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter depotAdapter;
	private Spinner spPos;    			// 库区
	private EditText editLocation;  	// 库位
	private ListView listDepot;		    // 已扫描的货物
	private Button btnStart;			// "开始"按钮
	private Button btnClear;			// "清空"按钮
	private MenuItem btnScan;		    // "扫描"按钮
	private String seletedPosCd;		// 已选择的库区Id
	private String seletedPosName;		// 已选择的库区Name
	private String inputedLocation;		// 已输入的库位
	private OT001ScanData scanData;     // 扫描的信息
	
	private Map<String, SelectDict> _scanStatusMap; // 扫描状态
	
	private static final int REQUEST_CODE_SCAN = 1;        // 扫描
	private int pageStatus = 0;    // 0 ： “开始”按钮显示      1：“清空”按钮显示
	
	/** 初始化事件 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ot001_inbatch);
		setupActionBar();
		depotInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		spPos = (Spinner)findViewById(R.id.sp_pos);            		// 库区
		editLocation = (EditText)findViewById(R.id.editLocation);   // 库位
		editLocation.setSelectAllOnFocus(true);     // 全选
		listDepot = (ListView)findViewById(R.id.listDepot);		    // 已更新的货物
		btnStart = (Button)findViewById(R.id.btnStart); 			// "开始"按钮
		btnClear = (Button)findViewById(R.id.btnClear); 			// "清空"按钮
		btnClear.setVisibility(View.GONE);
		SetSpPos();           // 设置库区
		SetScanStatusList();  // 扫描状态列表
		scanData = new OT001ScanData();
		SetDepotAdapter();    // 设置已扫描的明细
	}
		
	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// add by yxq 2014/10/21 begin 
		// 选择打印机后
		if(requestCode == 1 && resultCode == Activity.RESULT_OK && pageStatus == 0){
			SetSpPos();           // 设置库区
		}
		// add by yxq 2014/10/21 end
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_scanandprint, menu);
		btnScan =  menu.findItem(R.id.scan_action);           // "扫描"按钮
		btnScan.setVisible(false);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan_action:   // 扫描
			Intent intent = new Intent(this, CaptureActivity.class);
			intent.putExtra("SCAN_MODE", "SCAN_MODE");
			startActivityForResult(intent, REQUEST_CODE_SCAN);
			return true;
		case R.id.print_action:  // 选择打印机
			seletedPosCd = ((SelectDict)spPos.getSelectedItem()).getId();
			SelectPrintDialog dlg = new SelectPrintDialog(1, this);
			dlg.createDialog(this).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	// 设置库区下拉框
	public void SetSpPos(){
		Type a = new TypeToken<List<SelectDict>>() {}.getType();
		NetworkHelper.getInstance().postData(this, "OT001_GetPosList", null, a,
			new AmassHttpResponseHandler<List<SelectDict>>() {
				@Override
				protected void OnSuccess(List<SelectDict> response) {
					// TODO Auto-generated method stub
					super.OnSuccess(response);
					List<SelectDict> listData = new ArrayList<SelectDict>();
					SelectDict item = new SelectDict();
					item.setId("");
					item.setName(getString(R.string.OT001_Select_Pos));   // "请选择库区" 
					listData.add(item);
					listData.addAll(response);
					ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
							_thisActivity,android.R.layout.simple_spinner_item, listData);
					selectList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spPos.setAdapter(selectList);
				}
			}, true);
	}
	// 扫描状态列表
	public void SetScanStatusList(){
		if(_scanStatusMap == null){
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("cdType", "STOCKTAKING_RESULT");
			NetworkHelper.getInstance().postJsonData(this, "Sys_GetDictList", p,
					new TypeToken<List<SelectDict>>(){}.getType(), new AmassHttpResponseHandler<List<SelectDict>>(){
				@Override
				protected void OnSuccess(List<SelectDict> response) {
					super.OnSuccess(response);
					List<SelectDict> _serviceFlgList = response;
					_scanStatusMap = new HashMap<String, SelectDict>();
					Iterator<SelectDict> it = _serviceFlgList.iterator();
					while(it.hasNext()){
						SelectDict item = it.next();
						_scanStatusMap.put(item.getId(), item);
					}
				}
			}, false);
		}
	}
	// 得到某一扫描状态
	public String GetScanStatus(String scanStatus){
		SelectDict dict = _scanStatusMap.get(scanStatus);
		if(dict != null){
			return dict.getName();
		}
		return null;
	}
	// "尚未更新列表"按钮
	public void Btn_NotScannedList(View view){
		Intent intent = new Intent(_thisActivity,OT002Activity.class);
		SelectDict depotItem = (SelectDict)spPos.getSelectedItem();
		seletedPosCd = depotItem.getId();
		intent.putExtra("posCd", seletedPosCd);
		intent.putExtra("posNm", depotItem.getName());
		startActivity(intent);
	}
	// “开始”按钮
	public void Btn_Start(View view){
		SelectDict depotItem = (SelectDict)spPos.getSelectedItem();
		seletedPosCd = depotItem.getId();
		seletedPosName = depotItem.getName();
		inputedLocation = editLocation.getText().toString();
		if(seletedPosCd.isEmpty()){
			Utils.showAlertDialog(_thisActivity,getString(R.string.OT001_Select_Pos));     // "请选择库区" 
			return;
		}
		if(inputedLocation.isEmpty()){
			Utils.showAlertDialog(_thisActivity,getString(R.string.OT001_Input_Location)); // "请输入库位"
			return;
		}
		if(inputedLocation.length() == 1){
			inputedLocation = "0" + inputedLocation;
			editLocation.setText(inputedLocation);
		}
		// 库位更新对象\n\t%s库区\n\t%s库位
		String info = String.format(getString(R.string.OT001_001_MSG),seletedPosName,inputedLocation);
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
		.setIcon(R.drawable.ic_launcher)
		.setTitle(R.string.app_name)
		.setMessage(info)   
		.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {         // 确定按钮
					@Override
					public void onClick(DialogInterface dialog,int whichButton) {
						btnStart.setVisibility(View.GONE);		// "开始"按钮隐藏
						btnClear.setVisibility(View.VISIBLE);	// "清空"按钮显示
						btnScan.setVisible(true);				// "扫描"按钮显示
						spPos.setEnabled(false);
						editLocation.setEnabled(false);
						pageStatus = 1;
					}
				})
		.setCancelable(false)
		.setNegativeButton(R.string.button_no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int whichButton) {
					}
				}).show();					
	}
	// “清空”按钮
	public void Btn_Clear(View view){
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
		.setIcon(R.drawable.ic_launcher)
		.setTitle(R.string.app_name)
		.setMessage(getString(R.string.OT001_002_MSG))    // 是否确定完成
		.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {   // 确定按钮
					@Override
					public void onClick(DialogInterface dialog,int whichButton) {
						btnStart.setVisibility(View.VISIBLE);	// "开始"按钮显示
						btnClear.setVisibility(View.GONE);		// "清空"按钮隐藏
						btnScan.setVisible(false);				// "扫描"按钮隐藏
						spPos.setEnabled(true);
						editLocation.setEnabled(true);
						editLocation.clearFocus();
						depotAdapter.clear();
						pageStatus = 0;
					}
				})
		.setCancelable(false)
		.setNegativeButton(R.string.button_no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int whichButton) {
					}
				}).show();					
	}
	// “扫描”按钮
	public void Btn_Scan(View view){
		Intent intent = new Intent(this, CaptureActivity.class);
		intent.putExtra("SCAN_MODE", "SCAN_MODE");
		startActivityForResult(intent, REQUEST_CODE_SCAN);		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (resultCode != RESULT_OK) {
			return;
		}
	    switch(requestCode){
		case REQUEST_CODE_SCAN:     // 扫描桩脚牌
			//扫描桩脚牌时
			final BarCode02 barCode02 = new BarCode02();
			boolean result02 = barCode02.paserBarCode(data.getStringExtra("SCAN_RESULT"));
			if(result02){
				try{
					Integer.parseInt(barCode02.getDepotNum()); // 得到的桩脚牌的数量不为数字
					scanData.setDepotNum(barCode02.getDepotNum());    // 货物件数   
				}catch(Exception ex){
					scanData.setDepotNum("0");    // 错误的数字时，数量置为0
				}
				Map<String,Object> requestData = new HashMap<String,Object>();
				scanData.setPosCd(seletedPosCd);    			  // 选择的库区Id
				scanData.setLocation(inputedLocation);			  // 输入的库位
				scanData.setDepotDtId(barCode02.getDepotDtId());  // 货物明细Id
				scanData.setCdOrder("");
				requestData.put("scanData",scanData);
				// 根据orderCd获取集箱号
				NetworkHelper.getInstance().postJsonData(_thisActivity, "OT001_ScanDepotInBatch", 
						requestData,new TypeToken<List<OT001DetailData>>(){}.getType() ,
						new AmassHttpResponseHandler<List<OT001DetailData>>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(List<OT001DetailData> response) {
						super.OnSuccess(response);
						List<OT001DetailData> data = response;
						if(data == null || data.size() <= 0){
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.OT001_003_MSG);
							return;
						}
						for(int i=0;i<data.size();i++){
						    depotAdapter.add(data.get(i));
						}
						depotAdapter.notifyDataSetChanged();
					}
				}, true);
			  }else{
				//扫描到错误的桩脚牌
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.OT001_004_MSG);
			  }
			  break;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void SetDepotAdapter(){
		// 适配器
		depotAdapter = new ArrayAdapter(getApplicationContext(), 0) {
			public View getView(int position, View convertView, ViewGroup parent) {
				TableLayout layout = new TableLayout(getContext());
				OT001DetailData data = (OT001DetailData) getItem(position);
				try {
					// 设置背景颜色
					if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}
					depotInflater.inflate(R.layout.activity_ot001_inbatch_scanned_list, layout,true);
					OT001DetailData item = (OT001DetailData) data;
					// 进仓编号
					TextView  t1 = (TextView) layout.findViewById(R.id.textViewCdOrder);
					t1.setText(item.getCdOrderPublic());
					// 同行编号
					t1 = (TextView) layout.findViewById(R.id.textViewCoLoader);
					t1.setText(item.getCoLoader());
					// 桩脚牌 
					t1 = (TextView) layout.findViewById(R.id.textViewPileCard);  
					t1.setText(item.getBatchNo()+"-"+item.getPilecardNo());
			     	// 件数 
					t1 = (TextView) layout.findViewById(R.id.textViewNum);
					t1.setText(item.getNumInData());
			     	/*// 实际
					t1 = (TextView) layout.findViewById(R.id.textViewFact);
					t1.setText(item.getNumInFact());
				    // 状态 
					t1 = (TextView) layout.findViewById(R.id.textViewStatus);
					int color = Color.RED;
					if("0".equals(item.getStatus())){
						color =  0xFF00AE55;   // 绿色
					}
					t1.setText(GetScanStatus(item.getStatus()));	
					t1.setTextColor(color);*/
				} catch (Exception e) {
					Utils.showAlertDialog(_thisActivity, "OT001Activity-SetDepotAdapter"+e.toString());
					e.printStackTrace();
				}
					return layout;
				}
			};
			listDepot.setAdapter(depotAdapter);
	}

	
}
