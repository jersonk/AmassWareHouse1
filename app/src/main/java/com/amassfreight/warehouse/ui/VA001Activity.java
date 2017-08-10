package com.amassfreight.warehouse.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DepotVA001;
import com.amassfreight.domain.MoreData;
import com.amassfreight.domain.PageBaseData;
import com.amassfreight.utils.SessionHelper;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.amassfreight.warehouse.ui.dialogs.VA001SearchDetailDialog;
import com.amassfreight.warehouse.ui.dialogs.VA001SearchMoreData;
import com.google.gson.reflect.TypeToken;

public class VA001Activity extends BaseActivity implements OnDialogOkListener {
	private ListView listView;
	private LayoutInflater mInflater;
	private ArrayAdapter p;
	private int nPage;
	private VA001SearchMoreData moreData;
	private List<MoreData> depotDataList;
	private int flashStatus = 0;                // 闪烁的状态
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nPage = 0;
		moreData = new VA001SearchMoreData();
		 
		setContentView(R.layout.activity_va001);
		setupActionBar();

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listView = (ListView) findViewById(R.id.result_listView);
		depotDataList = new ArrayList<MoreData>();

		p = new ArrayAdapter(getApplicationContext(), 0){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				MoreData data = (MoreData) getItem(position);
				LinearLayout layout = new LinearLayout(getContext());
				SimpleDateFormat time = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());
				if (data.getDataType() == MoreData.DATA_TYPE) {
					DepotVA001 item = (DepotVA001) data.getData();
					boolean flgEmergency = item.flgEmergency;
					// 设置背景颜色
					// edit by yxq 2014/09/15 begin
					// 原代码 if(flgEmergency){
					if(flgEmergency && !"2".equals(item.getFlgService())){
					// edit by yxq 2014/09/15 end
						if(flashStatus == 0){
							layout.setBackgroundResource(R.color.red);
						}else{
							layout.setBackgroundResource(R.color.yellow);
						}
					}else if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}
					mInflater.inflate(R.layout.va001_list_view_item, layout,true);
					TextView t1 = (TextView) layout.findViewById(R.id.textRemark);
					t1.setText(item.getRemark());
					t1 = (TextView) layout.findViewById(R.id.textCdPubOrder);
					t1.setText(item.getCdOrderPublic());
					t1 = (TextView) layout.findViewById(R.id.textServiceType);
					t1.setText(SessionHelper.getInstance().getServiceType(item.getServiceType()));
					// 审核时间
					if (item.getDtApply() != null) {
						t1 = (TextView) layout.findViewById(R.id.textApplyTime);
						t1.setText(time.format(item.getDtApply()));
					}
					// 最晚时间
					if (item.getDtEnd() != null) {
						t1 = (TextView) layout.findViewById(R.id.textEndTime);
						t1.setText(time.format(item.getDtEnd()));
					}
					// 实际时间
					if (item.getDtAct() != null) {
						t1 = (TextView) layout.findViewById(R.id.textActTime);
						t1.setText(time.format(item.getDtAct()));
					}
					t1 = (TextView) layout.findViewById(R.id.textPlanNm);
					if (item.getPlanNm() == null) { // 客服
						t1.setText("");    
					} else {
						t1.setText(item.getPlanNm());
					}
					// 状态
					t1 = (TextView) layout.findViewById(R.id.textFlgService);
					t1.setText(SessionHelper.getInstance().getServiceFlg(item.getFlgService()));
					if("0".equals(item.getFlgService())){          // 未安排
						if(flgEmergency && flashStatus == 0){
							t1.setTextColor(getResources().getColorStateList(R.color.yellow));
						}else{
							t1.setTextColor(getResources().getColorStateList(R.color.red));
						}
					}else if("1".equals(item.getFlgService())){    // 作业中
						if(flgEmergency && flashStatus == 0){
							t1.setTextColor(getResources().getColorStateList(R.color.yellow));
						}else{
							t1.setTextColor(getResources().getColorStateList(R.color.red));
						}						
					}else if ("2".equals(item.getFlgService())) {  // 已完成
						t1.setTextColor(getResources().getColorStateList(R.color.blue));
					}
					t1 = (TextView) layout.findViewById(R.id.textDepotNo);
					t1.setText(item.getDepotNo());
					return layout;
				} else {
					mInflater.inflate(R.layout.list_more_item, layout, true);
					if (!data.isGetDataing()) {
						data.setGetDataing(true);
						// GetMoreData
						GetScData(depotDataList, moreData);
					} else {

					}
					return layout;
				}
			}

		};
		listView.setAdapter(p);

		GetScData(depotDataList, moreData);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long seq) {
				MoreData data = (MoreData) parent.getItemAtPosition(position);
				EnterVa003(data);   // 进入详细画面
			}
		});
		
		// 长按接受任务
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent,View view, int pos, long id) {
			if(pos >= depotDataList.size()){   // 长按"数据加载中"
				return true;
			}
			// 得到任务状态
			MoreData data = (MoreData) depotDataList.get(pos);
			DepotVA001 depotVA001 = (DepotVA001)data.getData();
			if("2".equals(depotVA001.getFlgService())){    // 该任务已经完成
				Utils.showAlertDialog(_thisActivity,getString(R.string.VA001_MSG_001));
				return true;
			}
			DoWithTask(depotVA001.getServiceId(),pos);    
			return true;
		}
	 });
		
		TimerTask task = new TimerTask(){   
	        public void run() {   
	            runOnUiThread(new Runnable(){   
	            public void run() {
	            	flashStatus = 1 - flashStatus;
	            	p.notifyDataSetChanged();
	             }
	            });   
	        }   
	    };
	    Timer timer = new Timer();
	    timer.schedule(task,1000,1000);  //参数分别是delay（多长时间后执行），duration（执行间隔）
	}
	
	// 进入详细画面
	private void EnterVa003(MoreData data){
		if (data.getDataType() == MoreData.DATA_TYPE) {
			DepotVA001 item = (DepotVA001) data.getData();
			SessionHelper.getInstance().setObject("VA003", item);
			Intent intent = new Intent(_thisActivity,VA003Activity.class);
			startActivity(intent);
		}
	}

	private void GetScData(final List<MoreData> depotDataList,
			VA001SearchMoreData data) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("vo", data);
		p1.put("nPage", String.valueOf(nPage));
		boolean showDialog = (nPage == 0);  // 是否显示"网络连接中"弹出信息
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"VA001_GetDepotPageList",p1
				, new TypeToken<PageBaseData<DepotVA001>>(){}.getType()
				, new AmassHttpResponseHandler<PageBaseData<DepotVA001>>() {
					@Override
					protected void OnSuccess(PageBaseData<DepotVA001> response) {
						// TODO Auto-generated method stub
						super.OnSuccess(response);
						PageBaseData<DepotVA001> ms = (PageBaseData<DepotVA001>) response;
						p.clear();
						List<MoreData> allDatas = new ArrayList<MoreData>();
						for (int n = 0; n < ms.getDataList().size(); n++) {
							depotDataList.add(new MoreData(ms.getDataList().get(n)));
						}
						allDatas.addAll(depotDataList);
						if (allDatas.size() < ms.getTotalCount()) {
							allDatas.add(new MoreData());
						}
						nPage++;
						p.addAll(allDatas);
						p.notifyDataSetChanged();
					}
				}, showDialog);
	}

	
	@Override
	protected void onResume() {
		p.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.va001, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search_detail_action:        // “检索”
//			moreData = new VA001SearchMoreData();   // 每次检索条件都清空
			VA001SearchDetailDialog dlg = new VA001SearchDetailDialog(1,moreData, this);
			dlg.createDialog(this).show();
			return true;
		case R.id.refresh:
			depotDataList.clear();
			nPage = 0;
			GetScData(depotDataList, moreData);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
		moreData = (VA001SearchMoreData) data.getExtras().get(OnDialogOkListener.OK);
		depotDataList.clear();
		nPage = 0;
		GetScData(depotDataList, moreData);
	}
	
	// “处理任务”
	public void DoWithTask(final String strServiceId,final int pos){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("strServiceId", strServiceId);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA001_GetTaskStatus",paraMap
			, String.class , new AmassHttpResponseHandler<String>() {
				@Override
				protected void OnSuccess(String response) {
					super.OnSuccess(response);
					String taskStatus =  response.toString();   //任务状态
					ShowTaskDialog(taskStatus,strServiceId,pos);
				}
			}, true);
	}
	// “任务”弹出框
	public void ShowTaskDialog(final String taskStatus,final String strServiceId,final int pos){
		// edit by yxq 2014/09/23 begin
		/* 原代码
		if("otherAndNotBegin".equals(taskStatus)||
				"otherAndBegin".equals(taskStatus)){    // 该任务已经被他人接受。
	    */	
		if("otherAndNotBeginAndNo".equals(taskStatus)||
				"otherAndBeginAndNo".equals(taskStatus)){    // 该任务已经被他人接受,且没有权限
		// edit by yxq 2014/09/23 end
			Utils.showAlertDialog(_thisActivity,getString(R.string.VA001_MSG_002));
			return;
		}
		DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog,int which) {
				String strFlg = "";
				if("selfAndBegin".equals(taskStatus))  // 任务已经开始
				{
					if(which == 0){
						strFlg = "relieve";		       // 解除任务
					}
				}else if("selfAndNotBegin".equals(taskStatus)){   // 任务未开始
					if(which == 0){
						strFlg = "take";		       // 开始作业
					}else if(which == 1){
						strFlg = "relieve";		       // 解除任务
					}
				}else if("noBody".equals(taskStatus)){ // 未分配的任务   
					if(which == 0){
						strFlg = "take";  			   // 开始作业
					}
				}
				// add by yxq 2014/09/23 begin
				if("otherAndNotBeginAndYes".equals(taskStatus)||
						"otherAndBeginAndYes".equals(taskStatus)){
					if(which == 0){
						strFlg = "forcedAccept";        // 强制接受任务
					}
				}
				// add by yxq 2014/09/23 end
			    TakeOrRelieveTask(strServiceId,strFlg,pos);
			}
	};
	String info1 = getString(R.string.VA001_Relieve_Task);   // 解除任务
	String info2 = getString(R.string.VA001_Accept_Task);    // 开始作业
	String[] info = null;
	if("selfAndBegin".equals(taskStatus)){  			  // 任务已经开始
		info = new String[] { info1, getString(R.string.VA001_Return)};
	}else if("selfAndNotBegin".equals(taskStatus)){       // 任务未开始
		info = new String[] { info2,info1,getString(R.string.VA001_Return)};
	}else if("noBody".equals(taskStatus)){                // 未分配的任务
		info = new String[] { info2, getString(R.string.VA001_Return)};
	}
	// add by yxq 2014/09/23 begin
	else if("otherAndNotBeginAndYes".equals(taskStatus)||
			"otherAndBeginAndYes".equals(taskStatus)){
		info = new  String[] { getString(R.string.VA001_Force_Accept_Task)};
	}
	// add by yxq 2014/09/23 end
	// "接受任务","返回"
	new AlertDialog.Builder(_thisActivity)
					.setItems(info,lis).show();
	}
	
	// "接受"或"解除"任务
	// strServiceId:任务Id
	// strFlg:接受或解除标识
	public void TakeOrRelieveTask(String strServiceId,final String strFlg,final int pos){
		if("".equals(strFlg)){
			return;
		}
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("strServiceId", strServiceId);
		paraMap.put("strFlg", strFlg);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA001_TakeOrRelieveTask",paraMap
			, new TypeToken<String>() {}.getType(),new AmassHttpResponseHandler<String>() {
				@Override
				protected void OnSuccess(String response) {
					try{
						super.OnSuccess(response);
						String planNm = (String)response;
						if("hasTake".equals(planNm)){
							Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_006));
							return;
						}
						MoreData data = (MoreData) p.getItem(pos);
						DepotVA001 depotVA001 = (DepotVA001) data.getData();
						depotVA001.setPlanNm(planNm);        // 仓库客服名字
						// edit by yxq 2014/09/23 begin
						// 原代码 if("take".equals(strFlg)){    		 // 成功接受
						if("take".equals(strFlg) || "forcedAccept".equals(strFlg)){ // 成功接受
						// edit by yxq 2014/09/23 end
							depotVA001.setFlgService("1");
							EnterVa003(data);   			 // 进入详细画面
						//	Utils.showAlertDialog(_thisActivity,getString(R.string.VA001_Accept_OK));
						}else if("relieve".equals(strFlg)){  // 成功解除
							depotVA001.setFlgService("0");
							Utils.showAlertDialog(_thisActivity,getString(R.string.VA001_Relieve_OK));
						}
						p.notifyDataSetChanged();
					}catch(Exception e){
						Utils.showAlertDialog(_thisActivity, e.toString());
					}
				}
			}, true);
	}
}
