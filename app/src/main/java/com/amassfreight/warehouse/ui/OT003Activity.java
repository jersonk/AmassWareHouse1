package com.amassfreight.warehouse.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DepotOT003;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.domain.OT003DetailData;
import com.amassfreight.domain.OT003SearchData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.widget.GalleryAct;
import com.amassfreight.widget.ScrollGridView;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;

public class OT003Activity extends BaseActivity {

	@SuppressWarnings("rawtypes")
	private ArrayAdapter listAdapter;  	  		
	private LayoutInflater listInflater;
	private ScrollGridView listOt003;   	      
	private EditText editCdOrderPublic;   		// 进仓编号	
	private EditText editCdLoader;   			// 同行编号  add by yxq 2014/10/27
	private TextView labelContainerStatusNm;    // 集装箱状态名
	private ImageAdapter imageAdpter_InsPhoto;  // 进仓照片
	private ImageAdapter imageAdpter_MtPhoto;   // 唛头照片
	private ImageAdapter imageAdpter_VasPhoto;  // 增值服务照片     add by yxq 2014/09/12
	private GridView gridView_InsPhoto;
	private GridView gridView_MtPhoto;
	private GridView gridView_VasPhoto;   // add by yxq 2014/09/12
	private String strDepotDtId = "";     // 扫描到的桩脚牌Id
	private String orderCd = null;
	private String type = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle(getString(R.string.title_activity_ot003));   // 库存信息查询
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ot003);
		listInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listOt003 = (ScrollGridView) findViewById(R.id.listOt003);
		editCdOrderPublic = (EditText) findViewById(R.id.editCdOrderPublic);
		labelContainerStatusNm = (TextView) findViewById(R.id.labelContainerStatusNm);
		editCdOrderPublic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode(editCdOrderPublic);
				ShowInputDialig("1");
			}
		});
		
		// add by yxq 2014/10/27 begin
		// 同行编号
		editCdLoader = (EditText) findViewById(R.id.editCdLoader);
		editCdLoader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode(editCdLoader);
				ShowInputDialig("2");
			}
		});
		// add by yxq 2014/10/27 end 
		
		setupActionBar();
		setAdapter();        // 适配器
		
		// 唛头照片
		gridView_InsPhoto = (GridView) findViewById(R.id.gridView_InsertPhoto);
		imageAdpter_InsPhoto = new ImageAdapter(this, 0);
		gridView_InsPhoto.setAdapter(imageAdpter_InsPhoto);
		// 进仓照片
		gridView_MtPhoto = (GridView) findViewById(R.id.gridView_MtPhoto);
		imageAdpter_MtPhoto = new ImageAdapter(this, 0);
		gridView_MtPhoto.setAdapter(imageAdpter_MtPhoto);
		// 增值服务照片  add by yxq 2014/09/12
		gridView_VasPhoto  = (GridView) findViewById(R.id.gridView_VasPhoto);
		imageAdpter_VasPhoto = new ImageAdapter(this, 0);
		gridView_VasPhoto.setAdapter(imageAdpter_VasPhoto);
		
		gridView_InsPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				ImageData imageData = (ImageData) data.getData();
				if (imageData.getData() != null) {
					GalleryAct.start(_thisActivity, imageData.getUrl()); // 放大
				}
			}
		});

		gridView_MtPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				ImageData imageData = (ImageData) data.getData(); 
				if (imageData.getData() != null) {  
					GalleryAct.start(_thisActivity, imageData.getUrl());  // 放大
				}
			}
		});
		
		// 增值服务照片  add by yxq 2014/09/12
		gridView_VasPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				ImageData imageData = (ImageData) data.getData(); 
				if (imageData.getData() != null) {  
					GalleryAct.start(_thisActivity, imageData.getUrl());  // 放大
				}
			}
		});
		
		// 单击
		listOt003.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int pos, long id) {
				try{
					OT003DetailData  ot003DetailData = (OT003DetailData) parent.getItemAtPosition(pos);
					Intent intent = new Intent();
					intent.setClass(_thisActivity, DN004Activity.class);
					Bundle detailBundle = new Bundle();
					detailBundle.putString("Type", "5"); //  查看标识
					detailBundle.putString("cdOrder", ot003DetailData.getCdOrder()); // 进仓编号个别
					detailBundle.putString("cdOrderPublic",ot003DetailData.getCdOrderPublic()); // 进仓编号共通
					detailBundle.putString("depotID", ot003DetailData.getDepotId()); // 进仓ID
					detailBundle.putString("noBatch", ot003DetailData.getBatchNo()); // 批次
					detailBundle.putString("thNo", ot003DetailData.getCoLoader()); // 同行编号
					detailBundle.putString("depotDtID",ot003DetailData.getDepotDtId());
					detailBundle.putString("noPilecard",ot003DetailData.getPilecardNo());
					
					intent.putExtras(detailBundle);
					startActivity(intent);
				}catch(Exception e){
					Utils.showAlertDialog(_thisActivity,"OT003Activity-查看桩脚牌:"+e.toString());
				}
			}
		});
		
	    Intent intent=getIntent(); 
	    orderCd = intent.getStringExtra("orderCd");
	    type = intent.getStringExtra("type");
	    if(orderCd != null && !"".equals(orderCd)&& "01".equals(type)){
	    	editCdOrderPublic.setEnabled(false);
	    	editCdLoader.setEnabled(false);
	    	editCdOrderPublic.setText(orderCd);
	    	OT003SearchData ot003SearchData = new OT003SearchData();
	    	ot003SearchData.setCdOrderPublic(orderCd);
	    	InitData(ot003SearchData);
	    }
	}
	
	// 显示
	@SuppressWarnings("rawtypes")
	public void setAdapter(){
		// 适配器
		listAdapter = new ArrayAdapter(getApplicationContext(), 0) {
		  public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout layout = new LinearLayout(getContext());
			OT003DetailData item = (OT003DetailData) getItem(position);
			try {
				// 设置背景颜色
				if(strDepotDtId.equals(item.getDepotDtId())){
					layout.setBackgroundResource(R.color.lightBlue);
				}else if (position % 2 == 0) {
					layout.setBackgroundResource(R.color.listview_back_odd);
				} else {
					layout.setBackgroundResource(R.color.listview_back_uneven);
				}
				listInflater.inflate(R.layout.activity_ot003_item_list, layout,true);
			    // 桩脚牌
				TextView t1 = (TextView) layout.findViewById(R.id.textPileCard);  
				t1.setText(item.getBatchNo() + "-" + item.getPilecardNo());		
				// 库区库位
				t1 = (TextView) layout.findViewById(R.id.textPosAndLocation);  
				t1.setText(item.getPos() + "-" + item.getLocation());
				// 件毛体								
				t1 = (TextView) layout.findViewById(R.id.textNWS);  
				t1.setText(item.getNum() + "/" + item.getKgs() + "/" + item.getCbm());	
				if(item.getLength() > 550 || item.getWidth() > 220 || item.getHeight() > 225){
					//超长超高超宽
					t1.setTextColor(Color.RED);
				}else{
					t1.setTextColor(Color.BLACK);
				}	
				// add by yxq 2014/10/22 begin
				// 创建日期
				t1 = (TextView) layout.findViewById(R.id.textCreateDate);  
				t1.setText(item.getCreateDate());
				//盘点人
				t1 = (TextView) layout.findViewById(R.id.textNmStocktaking);  
				t1.setText(item.getNmStocktaking());
				//盘点时间
				t1 = (TextView) layout.findViewById(R.id.textDtStocktaking);  
				t1.setText(item.getDtStocktaking());
	
				// add by yxq 2014/10/22 end
			} catch (Exception e) {
				e.printStackTrace();
			}
		   return layout;
		  }
		};
		listOt003.setAdapter(listAdapter);
	}
    // 检索
	private void InitData(OT003SearchData ot003SearchData) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("ot003SearchData", ot003SearchData);   
		NetworkHelper.getInstance().postJsonData(_thisActivity,"OT003_GetItemList", paraMap, 
			new TypeToken<DepotOT003>() {}.getType(),
			new AmassHttpResponseHandler<DepotOT003>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(DepotOT003 response) {
						super.OnSuccess(response);
						DepotOT003 ms = (DepotOT003) response;
						// add by yxq 2014/10/27 begin
						editCdOrderPublic.setText(ms.getStrCdOrderPublic());   		// 进仓编号	
						editCdLoader.setText(ms.getStrCdLoader());                  // 同行编号
						// add by yxq 2014/10/27 end
						labelContainerStatusNm.setText(ms.getContainerStatusNm());
						setStatusColor(ms.getContainerStatus());
						listAdapter.clear();
						listAdapter.addAll(ms.getOt003List());
						listAdapter.notifyDataSetChanged();
						
						GetPhotoInfo(ms.getOt003InsFileList(),imageAdpter_InsPhoto);
						GetPhotoInfo(ms.getOt003MtFileList(),imageAdpter_MtPhoto);
						// 增值服务照片 add by yxq 2014/09/12
						GetPhotoInfo(ms.getOt003VasFileList(),imageAdpter_VasPhoto);
					}
				}, true);
	}
	
	// 得到照片
	public void GetPhotoInfo(List<FileManageData> list,ImageAdapter imageAdpter) {
		imageAdpter.clear();
		// 循环加载照片
		for (FileManageData file : list) {
			ImageData data = new ImageData();
			data.setImageId(file.getFileId());
			data.setImageDesc(file.getFileName());
			MoreData d = new MoreData(data);
			// edit by yxq 2014/09/12 begin
			// 原代码 imageAdpter.add(d);
			imageAdpter.insert(d, 0);
			// edit by yxq 2014/09/12 end
		}
	}
	
	/*
	 * 弹出进仓编号 或 同行编号  输入框
	 */
	// searchType add by yxq 2014/10/27
	// searchType : "1" : 编号   "2": 同行编号
	@SuppressLint("DefaultLocale")
	public void ShowInputDialig(final String searchType) {
		String title = "";
		String digits = "";
		if("1".equals(searchType)){
			title = getString(R.string.OT003_Label_CdOrder);
			digits = "0123456789";
		}else{
			title = getString(R.string.OT003_Label_CdLoader);
			digits = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				title).setCancelable(false);
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.activity_ot003_view_input_dialog, null);
		final EditText editSearchData_dialog = (EditText) textEntryView
				.findViewById(R.id.editSearchData_dialog);
		editSearchData_dialog.setKeyListener(DigitsKeyListener.getInstance(digits));   
		builder.setView(textEntryView);
		final AlertDialog dialog = builder.show();// 显示对话框
		dialog.setCanceledOnTouchOutside(false); // 点击空白处，不关闭对话框
		// "查询"按钮单击事件
		Button btnSearch = (Button) textEntryView.findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 进仓编号
				String strSearchData = editSearchData_dialog.getText().toString().trim(); 
				OT003SearchData ot003SearchData = new OT003SearchData();
				if (strSearchData.length() > 0) {
					if("1".equals(searchType)){
						strSearchData = Utils.completeOrderId(strSearchData);
						ot003SearchData.setCdOrderPublic(strSearchData);
					}else{
						strSearchData = strSearchData.toUpperCase();
						ot003SearchData.setCoLoader(strSearchData);
					}
					strDepotDtId = "";
					InitData(ot003SearchData);  // 根据进仓编号查
					dialog.dismiss(); // 关闭对话框
				}else{
					if("1".equals(searchType)){
					 // 请输入进仓编号
					 Utils.showAlertDialog(_thisActivity,getString(R.string.OT003_Info_001));
					}else{
					 // 请输入同行编号
					 Utils.showAlertDialog(_thisActivity,getString(R.string.OT003_Info_004));
					}
				}
			}
		});
		// "取消"按钮单击事件
		Button btnCancel = (Button) textEntryView.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss(); // 关闭对话框
			}
		});
	}
	
	/*
	 * 禁止显示软键盘
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
		if(!"01".equals(type)){
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.common_scan, menu);			
		}
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * 扫描事件
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan_action:
			// 扫描
			Intent intent = new Intent(this, CaptureActivity.class);
			intent.putExtra("SCAN_MODE", "SCAN_MODE");
			startActivityForResult(intent, 1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * 扫描后返回事件
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		Bundle bundle = data.getExtras();
		switch (requestCode) {
		case 1:
			//扫描桩脚牌时
			final BarCode02 barCode02 = new BarCode02();
			boolean result02 = barCode02.paserBarCode(data.getStringExtra("SCAN_RESULT"));
			if(result02){
				Map<String,Object> requestData = new HashMap<String,Object>();
				strDepotDtId =  barCode02.getDepotDtId();
				requestData.put("strDepotDtId",strDepotDtId);   // 桩脚牌Id
				// 扫描桩脚牌
				NetworkHelper.getInstance().postJsonData(_thisActivity, "OT003_ScanDepot", 
						requestData,DepotOT003.class ,
						new AmassHttpResponseHandler<DepotOT003>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(DepotOT003 response) {
						super.OnSuccess(response);
						DepotOT003 ms = (DepotOT003) response;
						String strCdOrderPublic = ms.getStrCdOrderPublic();
						String strCoLoader = ms.getStrCdLoader();
						// 扫描到的桩脚牌不存在
						if(strCdOrderPublic == null && strCoLoader == null){				    	
							Utils.showAlertDialog(_thisActivity,getString(R.string.OT001_Info_002));
						}else{
							strDepotDtId = ms.getDepotDtId();
							editCdOrderPublic.setText(strCdOrderPublic);
							editCdLoader.setText(strCoLoader);
							labelContainerStatusNm.setText(ms.getContainerStatusNm());
							setStatusColor(ms.getContainerStatus());
							listAdapter.clear();
							listAdapter.addAll(ms.getOt003List());
							listAdapter.notifyDataSetChanged();
							GetPhotoInfo(ms.getOt003InsFileList(),imageAdpter_InsPhoto);
							GetPhotoInfo(ms.getOt003MtFileList(),imageAdpter_MtPhoto);
							// 增值服务照片 add by yxq 2014/09/12
							GetPhotoInfo(ms.getOt003VasFileList(),imageAdpter_VasPhoto);
						}
					}
				}, true);
			  }else{
				//扫描到错误的桩脚牌
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.OT001_Info_003);
			  }
			break;
		}
	}
	
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
			labelContainerStatusNm.setTextColor(0xFF00AE55);   // 绿色
		}else{
			labelContainerStatusNm.setTextColor(Color.BLACK);
		}
	}
}
