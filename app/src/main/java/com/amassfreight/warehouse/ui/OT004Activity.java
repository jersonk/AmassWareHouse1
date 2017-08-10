package com.amassfreight.warehouse.ui;

import java.io.Serializable;
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
import android.text.method.DigitsKeyListener;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DepotOT003;
import com.amassfreight.domain.OT001DetailData;
import com.amassfreight.domain.OT003DetailData;
import com.amassfreight.domain.OT003SearchData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.OT004Activity.ListAdapter.ViewHolder;
import com.google.gson.reflect.TypeToken;

@SuppressLint("UseSparseArrays")
public class OT004Activity extends BaseActivity {

	private ListAdapter listAdapter;  	  		
	private LayoutInflater listInflater;
	private ListView listOt003;   	      
	private EditText editCdOrderPublic;   		// 进仓编号	
	private EditText editCdLoader;   			// 同行编号  add by yxq 2014/10/27
	private TextView btnInventory;              // 盘点
	private TextView labelContainerStatusNm;    // 集装箱状态名
	//private String strDepotDtId = "";     // 扫描到的桩脚牌Id
	private String posCd;		// 已选择的库区Id
	private String location;    // 已输入的库位
	private List<OT003DetailData> mData = new ArrayList<OT003DetailData>();
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
    private Boolean isShowCheckBox = false;                      // 是否显示多选checkBox
    private int itemIndex;                                       // 当前listView第一个可见的item的索引
    private int itemTopPosition;                                 // 当前listView第一个可见的item的偏移量
    private int checkNum = 0;                                    // 选中的文件个数
    private Boolean selectAllFlag = true;                        // 全选标志
    private ActionMode mActionMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle(getString(R.string.title_activity_ot004));   // 查询盘点
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ot004);
		listInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		Intent intent=getIntent(); 
		posCd = intent.getStringExtra("posCd");
		location = intent.getStringExtra("location");
		listOt003 = (ListView) findViewById(R.id.listOt003);
		editCdOrderPublic = (EditText) findViewById(R.id.editCdOrderPublic);
		labelContainerStatusNm = (TextView) findViewById(R.id.labelContainerStatusNm);
		btnInventory = (TextView) findViewById(R.id.tools_inventory);
		btnInventory.setVisibility(View.GONE);
		btnInventory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doInventory(getSelectedDepotList());
			}
		});
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
		
		listAdapter = new ListAdapter(mData,false);
		listOt003.setAdapter(listAdapter);		
		listOt003.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if(!isShowCheckBox){
						// 长按时显示操作菜单，重写listView,显示多选checkBox
						rebuildListView(true);     
				        // Start the CAB using the ActionMode.Callback defined above  
				        mActionMode = startActionMode(mCallback); 
				        listOt003.setSelected(true);
				        ViewHolder holder = (ViewHolder) arg1.getTag();  
						// 改变CheckBox的状态  
				        if(mData.get(arg2).getStockDiffNum() == 0){
						    holder.mCheckBox.toggle();  
						 // 调整选定条目  
							if (holder.mCheckBox.isChecked()) {  
								checkNum++;  
							} else {  
								checkNum--;  
							}					        
						} 
				        // 未勾选时，设置按钮不可用
						if(checkNum == 0){
							btnInventory.setEnabled(false);
		                    btnInventory.setTextColor(getResources().getColor(R.color.tools_white));
						}else{
							btnInventory.setEnabled(true);
		                    btnInventory.setTextColor(getResources().getColor(R.color.tools_black));
						}
				        mActionMode.setTitle(String.format(getResources()
				        		.getString(R.string.label_title_selectNum),checkNum));
						// 将CheckBox的选中状况记录下来  
						mSelectMap.put(arg2, holder.mCheckBox.isChecked()); 
						listAdapter.notifyDataSetChanged();
				        return true;
					}
					return false;
				}            	
         });
		// 单击
		listOt003.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int pos, long id) {
				try{
					if(isShowCheckBox){
						// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化的步骤  
						ViewHolder holder = (ViewHolder) view.getTag();  
						// 改变CheckBox的状态  
						if(mData.get(pos).getStockDiffNum() == 0){
						    holder.mCheckBox.toggle(); 
						    // 调整选定条目  						
							if (holder.mCheckBox.isChecked()) {  
								checkNum++;  
							} else {  
								checkNum--;  
							}  														
							// 设置默认为全选,当全部checked时设置为全不选
							if(checkNum == getUnInventoryNum()){
								mActionMode.getMenu().getItem(0).setTitle(R.string.label_button_unSelectAll);
				                selectAllFlag = false;
							}else{
								mActionMode.getMenu().getItem(0).setTitle(R.string.label_button_selectAll);
								selectAllFlag = true;
							}
						}
						// 未勾选时，设置按钮不可用
						if(checkNum == 0){
							btnInventory.setEnabled(false);
		                    btnInventory.setTextColor(getResources().getColor(R.color.tools_white));
						}else{
							btnInventory.setEnabled(true);
		                    btnInventory.setTextColor(getResources().getColor(R.color.tools_black));
						}
						// 显示选中个数
						mActionMode.setTitle(String.format(getResources()
				        		.getString(R.string.label_title_selectNum),checkNum));
						// 将CheckBox的选中状况记录下来  
						mSelectMap.put(pos, holder.mCheckBox.isChecked()); 
						listAdapter.notifyDataSetChanged();
						
					}else{
						if(mData.get(pos).getStockDiffNum() > 0){
							Utils.showAlertDialog(_thisActivity,getString(R.string.OT004_msg_003));
						}else{
							List<OT003DetailData> list = new ArrayList<OT003DetailData>();
							list.add(mData.get(pos));
							doInventory(list);
						}
					}
				}catch(Exception e){
					Utils.showAlertDialog(_thisActivity,"OT003Activity-查看桩脚牌:"+e.toString());
				}
			}
		});
		
		listOt003.setOnScrollListener(new OnScrollListener() {                 
            //滚动状态改变时调用    
            public void onScrollStateChanged(AbsListView view, int scrollState) {   
                // 不滚动时保存当前滚动到的位置  
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {    
                	// 保存当前第一个可见的item的索引和偏移量
                	itemIndex = listOt003.getFirstVisiblePosition();
                	View v = listOt003.getChildAt(0);
                	itemTopPosition = (v == null) ? 0 : v.getTop();    
                }   
            }   
          
            //滚动时调用  
            @Override  
            public void onScroll(AbsListView view, int firstVisibleItem, 
            		int visibleItemCount, int totalItemCount) {   
            }   
        });
	}
	
    // 检索
	private void InitData(OT003SearchData ot003SearchData) {
		Map<String, Object> paraMap = new HashMap<String, Object>();		
		paraMap.put("ot003SearchData", ot003SearchData);   
		paraMap.put("posCd", posCd);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"OT004_GetDepotListWithoutInventory", paraMap, 
			new TypeToken<DepotOT003>() {}.getType(),
			new AmassHttpResponseHandler<DepotOT003>() {
					@Override
					protected void OnSuccess(DepotOT003 response) {
						super.OnSuccess(response);
						DepotOT003 ms = (DepotOT003) response;
						editCdOrderPublic.setText(ms.getStrCdOrderPublic());   		// 进仓编号	
						editCdLoader.setText(ms.getStrCdLoader());                  // 同行编号
						labelContainerStatusNm.setText(ms.getContainerStatusNm());
						setStatusColor(ms.getContainerStatus());
						mData = ms.getOt003List();
						listAdapter.mData = ms.getOt003List();
						listAdapter.notifyDataSetChanged();						
					}
				}, true);
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
					//strDepotDtId = "";
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
	 
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		if(!"01".equals(type)){
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.common_scan, menu);			
		}
		return super.onCreateOptionsMenu(menu);
	}

	
	 * 扫描事件
	 
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

	
	 * 扫描后返回事件
	 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
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
							mData = ms.getOt003List();
							listAdapter.mData = ms.getOt003List();
							listAdapter.notifyDataSetChanged();
						}
					}
				}, true);
			  }else{
				//扫描到错误的桩脚牌
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.OT001_Info_003);
			  }
			break;
		}
	}*/
	
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
	
	public class ListAdapter extends BaseAdapter {		
		private List<OT003DetailData> mData;
		private Boolean isShowCheckBox;
		public ListAdapter(List<OT003DetailData> mData,Boolean isShowCheckBox){  
			this.mData = mData;
			this.isShowCheckBox = isShowCheckBox;
		}  
		public int getCount() {  
			return mData.size();
		}  

		public Object getItem(int arg0) {  
			return mData.get(arg0);
		}  

		public long getItemId(int arg0) {   
			return arg0;  
		}  

		public View getView(final int position, View convertView, ViewGroup parent) {  

			ViewHolder holder;  
			if (convertView == null) {  
				holder=new ViewHolder();   
				convertView = listInflater.inflate(R.layout.activity_ot004_item_list, null);  
				holder.layout = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
				holder.pileCard = (TextView)convertView.findViewById(R.id.textPileCard);
				holder.posAndLocation = (TextView)convertView.findViewById(R.id.textPosAndLocation);
				holder.num = (TextView)convertView.findViewById(R.id.textNWS);
				holder.date = (TextView)convertView.findViewById(R.id.textCreateDate);
				holder.mCheckBox = (CheckBox)convertView.findViewById(R.id.checkbox);				
				convertView.setTag(holder);

			}else {                       
				holder = (ViewHolder)convertView.getTag();  
			}
			// 设置背景颜色
			holder.mCheckBox.setEnabled(true);
			holder.mCheckBox.setClickable(false);;
			if(mData.get(position).getStockDiffNum() > 0){
				holder.layout.setBackgroundResource(R.color.lightBlue);
				holder.mCheckBox.setEnabled(false);
			}else if (position % 2 == 0) {
				holder.layout.setBackgroundResource(R.color.listview_back_odd);
			} else {
				holder.layout.setBackgroundResource(R.color.listview_back_uneven);
			}
			holder.pileCard.setText(mData.get(position).getBatchNo()
					+"_"+mData.get(position).getPilecardNo());
			holder.posAndLocation.setText(mData.get(position).getPos()
					+"_"+mData.get(position).getLocation()); 
			holder.num.setText(mData.get(position).getNum()
					+ "/" + mData.get(position).getKgs() + "/" + mData.get(position).getCbm());	
			if(mData.get(position).getLength() > 550 ||
					mData.get(position).getWidth() > 220 ||
					mData.get(position).getHeight() > 225){
				//超长超高超宽
				holder.num.setTextColor(Color.RED);
			}else{
				holder.num.setTextColor(Color.BLACK);
			}	
			holder.date.setText(mData.get(position).getCreateDate()); 
			if(!isShowCheckBox){
                holder.mCheckBox.setVisibility(View.GONE);
			}else{
				holder.mCheckBox.setVisibility(View.VISIBLE);
			}
			holder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);

			return convertView;  
		}  

		public class ViewHolder{  
			public LinearLayout layout;
			public TextView pileCard;
			public TextView posAndLocation;
			public TextView num;
			public TextView date;
			public CheckBox mCheckBox;
		} 
	}
	
	public void rebuildListView(Boolean showCheckBox){		
		if(showCheckBox){
			btnInventory.setVisibility(View.VISIBLE);
		}else{
			btnInventory.setVisibility(View.GONE);
		}
		if(isShowCheckBox != showCheckBox){
			listAdapter = new ListAdapter(mData,showCheckBox);
			listOt003.setAdapter(listAdapter);
			// 恢复listView上次查看的位置
			listOt003.setSelectionFromTop(itemIndex, itemTopPosition);
		}
		checkNum = 0;
		isShowCheckBox = showCheckBox;
		mSelectMap.clear();
	}
	
	/**
	 * 顶部全选菜单栏处理
	 */
	private ActionMode.Callback mCallback = new ActionMode.Callback() {  
		  
        @Override  
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {  
            return false;  
        }  
  
        @Override  
        public void onDestroyActionMode(ActionMode mode) { 
        	rebuildListView(false);
        }  
  
        @Override  
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {  
            MenuInflater inflater = mode.getMenuInflater();  
            inflater.inflate(R.menu.select, menu);            
            return true;  
        }  
  
        @Override  
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {  
            boolean ret = false;  
            if (item.getItemId() == R.id.action_selectAll) {  
            	// 遍历list的长度，全选或全不选
                for (int i = 0; i < mData.size(); i++) {   
                	if(mData.get(i).getStockDiffNum() == 0){
                	    mSelectMap.put(i, selectAllFlag); 
                	}else{
                		mSelectMap.put(i, false); 
                	}
                }
                if(selectAllFlag){
                	item.setTitle(R.string.label_button_unSelectAll);
                    checkNum = getUnInventoryNum();                     
                    selectAllFlag = false;
                }else{
                	item.setTitle(R.string.label_button_selectAll);
                	checkNum = 0;                	
                	selectAllFlag = true;
                }
                if(checkNum == 0){
                	btnInventory.setEnabled(false);							
    				btnInventory.setTextColor(getResources().getColor(R.color.tools_white));
                }else{
                	btnInventory.setEnabled(true);
                    btnInventory.setTextColor(getResources().getColor(R.color.tools_black));
                }
                listAdapter.notifyDataSetChanged();
                mode.setTitle(String.format(getResources()
						.getString(R.string.label_title_selectNum),checkNum));
                //mode.finish();  
                ret = true;  
            }  
            return ret;  
        }  
    };
    
    
    /*
     * 获取选中的列表
     */
    private List<OT003DetailData> getSelectedDepotList(){
    	List<OT003DetailData> list = new ArrayList<OT003DetailData>();
		for(int i=0;i<mData.size();i++){
			if(mSelectMap.containsKey(i) && mSelectMap.get(i)){
				list.add(mData.get(i));
			}
		}
		return list;
    }
    
    /*
     * 未做过盘点的桩脚牌数量
     */
    private int getUnInventoryNum(){
    	int count = 0;          //未做过盘点的桩脚牌个数
		int size = mData.size();
		for(int n=0;n<size;n++){
			if(mData.get(n).getStockDiffNum() == 0){
				count++;
			}
		}
		return count;
    }
    
    /*
     * 对桩脚牌进行盘点
     */
    private void doInventory(final List<OT003DetailData> list){
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light));
		builder.setIcon(R.drawable.ic_launcher);
		builder.setCancelable(false);
		builder.setMessage(R.string.OT004_msg_001);
		builder.setTitle(R.string.app_name);
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Map<String,Object> requestData = new HashMap<String,Object>();
				requestData.put("posCd", posCd);
				requestData.put("location", location);
				requestData.put("depotList", list);
				NetworkHelper.getInstance().postJsonData(OT004Activity.this, 
						"OT004_doInventory", requestData,new TypeToken<List<OT001DetailData>>(){}.getType(), 
						new AmassHttpResponseHandler<List<OT001DetailData>>() {
					@Override
					protected void OnSuccess(List<OT001DetailData> response) {
						super.OnSuccess(response);
						List<OT001DetailData> data = response;
						if(data.size() == 0){
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.OT004_msg_002);
							return;
						}else{
							Bundle bundle = new Bundle();  
			                bundle.putSerializable("inventoryList", (Serializable) data);  
			                Intent intent=getIntent(); 
			                intent.putExtras(bundle); 
			                OT004Activity.this.setResult(RESULT_OK, intent);  
			                OT004Activity.this.finish();  
						}
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
    }
}
