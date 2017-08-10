package com.amassfreight.warehouse.ui;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.CommonResult;
import com.amassfreight.domain.PP014DetailData;
import com.amassfreight.domain.UnPackingList;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;

@SuppressLint("SimpleDateFormat")
public class PP014Activity extends BaseActivity {
	private static final String METHOD_URL = "PP014getDetail";

	private PP014Adapter adapter;
	private ListView unPackingList;
	private TextView boxNoText;
	private TextView deadlineTime;
	private TextView pickingRemark;	
	private String boxNo;
	private String orderCd;
	private String boxNoByOrder = null;
	private List<UnPackingList> datalist;  
	private String depotDtIdScan = null; //扫描到货物明细ID
	private String unPackingNoScan = null;   //进行掏箱的掏箱ID
	private String containerCd = null;   //箱号
	private Button saveUnPackingResult;
	private Boolean IsEdit;
	private LinearLayout linMain;
	private Boolean meunShow = false;
	private ImageButton imgBtn;
	private TextView tvOprName; 
	private TextView tvTitleNoBox;// 集箱号
	private LinearLayout imgLiner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pp014);
		setupActionBar();
		unPackingList = (ListView) findViewById(R.id.unPackingList);
		boxNoText = (TextView) findViewById(R.id.tv_jxno);
		deadlineTime = (TextView) findViewById(R.id.tv_zxdeadline);
		pickingRemark = (TextView) findViewById(R.id.pickingRequire);
		saveUnPackingResult = (Button) findViewById(R.id.saveUnPackingResult);
		saveUnPackingResult.setVisibility(View.GONE);
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
		containerCd = intent.getStringExtra("containerCd");
		IsEdit = intent.getBooleanExtra("IsEdit", true);
		if(boxNo != null && !boxNo.equals("")){
			Map<String,Object> requestData = new HashMap<String,Object>();
			requestData.put("boxNo", boxNo);			
			Type type = new TypeToken<PP014DetailData>(){}.getType();
			NetworkHelper.getInstance().postJsonData(this, METHOD_URL, requestData,type , httpHandler, false);
		}

		// 集箱号监听事件
		boxNoText.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (boxNo != null && !boxNo.isEmpty()) {
					Intent intent = new Intent(PP014Activity.this,
							PP003Activity.class);
					intent.putExtra("boxNo", boxNo);
					startActivity(intent);
				}
			}
		});
	}

	// AmassHttpResponseHandler
	private AmassHttpResponseHandler<PP014DetailData> httpHandler = new AmassHttpResponseHandler<PP014DetailData>() {
		protected void OnSuccess(PP014DetailData response) {
			super.OnSuccess(response);
			try{
				PP014DetailData responseBox = (PP014DetailData) response;
				if(responseBox != null && responseBox.getBoxNo()!=null){
					//if(responseBox.getContainerLockFlg()){
						tvTitleNoBox.setText(responseBox.getBoxNo()); // 集箱号
						imgLiner.setVisibility(View.VISIBLE);
						boxNoText.setText(responseBox.getBoxNo());
						boxNoText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
						boxNoText.setTextColor(Color.BLUE);
						boxNo = responseBox.getBoxNo();
						if(responseBox.getPackingDeadline() != null){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
							deadlineTime.setText(sdf.format(responseBox.getPackingDeadline()));
						}
						pickingRemark.setText(responseBox.getPackingRemarks());
						datalist = responseBox.getUnPackingList();
						if(datalist.size() > 0){
							adapter = new PP014Adapter(PP014Activity.this);
							unPackingList.setAdapter(adapter);
							adapter.notifyDataSetChanged(); 
							//unPackingList.setOnItemClickListener(new ItemClickListener());
						}
						
					//}else{
					//	Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_001);
					//}
				}else {
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_001);
				}
			}
			catch (Exception ex){
				ex.printStackTrace();
			}

		};

	};
	
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
				//扫描桩脚牌时
				//final BarCodeResult result = Utils.analysisBarCode(data.getStringExtra("SCAN_RESULT"),"02");						
				final BarCode02 barCode02 = new BarCode02();
				boolean result02 = barCode02.paserBarCode(data.getStringExtra("SCAN_RESULT"));
				if(result02){
					//判断桩脚牌是否本次操作的对象，并改变画面状态
					int count = 0;//正常货物计数
					int flg = 0;
					for(int i = 0 ; i < datalist.size() ; i++){
						if(barCode02.getDepotDtId().equals(datalist.get(i).getDepotDtId())||
								barCode02.getDepotDtId().equals(datalist.get(i).getDepotDtIdOrigin())
								){
							count++;
							depotDtIdScan = datalist.get(i).getDepotDtId();
							flg = datalist.get(i).getUnPackingFlg();
							if(flg == 0){
								//进行掏箱，状态变为已掏箱
								unPackingNoScan = datalist.get(i).getUnPackingId();
								datalist.get(i).setUnPackingFlg(1);
								break;
							}
						}
					}
					if(count > 0){
						if(flg == 0){
							//画面刷新状态变化
							adapter.notifyDataSetChanged();
							saveUnPackingResult.setVisibility(View.VISIBLE);
						}
						else{
							//已经掏箱
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP014_error_unPacked);
						}
					}else{
					    //不是本次操作的桩脚牌
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_001);
					}
				}else{
					//错误的桩脚牌条码
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_002);
				}
			} else {
				//扫描失败提示
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_scanError);
			}
			break;
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//按下的如果是BACK，同时没有重复
	         Intent intent=new Intent();   
	         intent.setClass(PP014Activity.this, PP006Activity.class);   
		     intent.putExtra("boxNo", boxNo);
		     intent.putExtra("containerCd", containerCd);
		     intent.putExtra("IsEdit", IsEdit);
	         startActivity(intent);   
	         PP014Activity.this.finish();   
		}
		return super.onKeyDown(keyCode, event);
	}	
	
	//保存按钮
	public void SaveUnPackingResult_OnClick(View arg0) {		
		Map<String,Object> requestData = new HashMap<String,Object>();
		requestData.put("boxNo", boxNo);	
		requestData.put("unPackingList", datalist);
		NetworkHelper.getInstance().postJsonData(PP014Activity.this, 
				"PP014saveResult", requestData,CommonResult.class , saveHandler, true);
	}

	//保存掏箱处理handler
	private AmassHttpResponseHandler<CommonResult> saveHandler = new AmassHttpResponseHandler<CommonResult>() {
		protected void OnSuccess(CommonResult response) {
			super.OnSuccess(response);
			CommonResult result = (CommonResult) response;
			if(result != null && result.getFlag()){
				//处理成功
				saveUnPackingResult.setVisibility(View.GONE);
				//Utils.showAlertDialogIntMsg(_thisActivity,R.string.msg_common_success);
				showAlertDialogRefresh(_thisActivity,R.string.msg_common_success);
			}else{
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP014_error_saveFailed);
			}
			
		}
	};
	
	//ViewHolder静态类  
    static class ViewHolder  
    {  
    	public TextView orderCdPublic; 
    	public TextView unPackingFlg; 
        public TextView pileCardCd;
        public TextView paidinNum;
        public TextView numRemark;
        public TextView unPacking_date_text;
        
    }  
      
    @SuppressLint("ResourceAsColor")
	public class PP014Adapter extends BaseAdapter  
    {      
        private LayoutInflater mInflater = null;  
        private PP014Adapter(Context context)  
        {  
            //根据context上下文加载布局，这里的是Activity本身，即this  
            this.mInflater = LayoutInflater.from(context);  
            
        }  
  
        @Override  
        public int getCount() {  
            //How many items are in the data set represented by this Adapter.  
            //在此适配器中所代表的数据集中的条目数  
            return datalist.size();  
        }  
  
        @Override  
        public Object getItem(int position) {  
            // Get the data item associated with the specified position in the data set.  
            //获取数据集中与指定索引对应的数据项  
            return position;  
        }  
  
        @Override  
        public long getItemId(int position) {  
            //Get the row id associated with the specified position in the list.  
            //获取在列表中与指定索引对应的行id  
            return position;  
        }  
          
        //Get a View that displays the data at the specified position in the data set.  
        //获取一个在数据集中指定索引的视图来显示数据  
        @SuppressLint("ResourceAsColor")
		@Override  
        public View getView(int position, View convertView, ViewGroup parent) {  
            ViewHolder holder = null;  
            //如果缓存convertView为空，则需要创建View  
            if(convertView == null)  
            {  
                holder = new ViewHolder();  
                //根据自定义的Item布局加载布局  
                convertView = mInflater.inflate(R.layout.activity_pp014_listview_item, null);   
                holder.pileCardCd = (TextView) convertView.findViewById(R.id.pileCardCd_text);
                holder.orderCdPublic = (TextView) convertView.findViewById(R.id.orderCd_public_text);
                holder.unPackingFlg = (TextView) convertView.findViewById(R.id.unPacking_flg_text);
                holder.paidinNum = (TextView) convertView.findViewById(R.id.paidinNum_text);
                holder.numRemark = (TextView) convertView.findViewById(R.id.num_remark_text);
                holder.unPacking_date_text = (TextView) convertView.findViewById(R.id.unPacking_date_text);

                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag  
                convertView.setTag(holder);  
            }else  
            {  
                holder = (ViewHolder)convertView.getTag();  
            }  
            if (position % 2 == 0) {
            	convertView.setBackgroundResource(R.color.listview_back_odd);
			} else {
				convertView.setBackgroundResource(R.color.listview_back_uneven);
			}
            holder.pileCardCd.setText((String)datalist.get(position).getPileCardCd());  
            holder.orderCdPublic.setText((String)datalist.get(position).getOrderCdPublic());
            holder.paidinNum.setText(String.valueOf(datalist.get(position).getPaidinNum()));
            if(datalist.get(position).getOrderCdPublic() != null){
                holder.numRemark.setText((String)datalist.get(position).getNumRemark());
            }
            if(datalist.get(position).getUnPackingDate() != null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				holder.unPacking_date_text.setText(sdf.format(datalist.get(position).getUnPackingDate()));
			}
            //设置掏箱状态背景色
            if(datalist.get(position).getUnPackingFlg() != 1){
            	holder.unPackingFlg.setText(R.string.PP014_unPacking_flg_undo);
            	holder.unPackingFlg.setBackgroundResource(0);
            }else{
            	holder.unPackingFlg.setText(R.string.PP014_unPacking_flg_done);
            	holder.unPackingFlg.setBackgroundResource(R.color.gay);
            }
            
            //设置正在操作项的颜色
            //if(datalist.get(position).getDepotDtId().equals(depotDtIdScan)){
            if(datalist.get(position).getUnPackingId().equals(unPackingNoScan)){
            	convertView.setBackgroundResource(R.color.lightBlue);
            }
            /*else{
            	convertView.setBackgroundResource(0);
            }*/
            return convertView;  
        }  
          
    }  

	public void showAlertDialogRefresh(Context context, int alertMsg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(context,
						android.R.style.Theme_Holo_Light));
		builder.setIcon(R.drawable.ic_launcher);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_name);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Map<String,Object> requestData = new HashMap<String,Object>();
				requestData.put("boxNo", boxNo);			
				Type type = new TypeToken<PP014DetailData>(){}.getType();
				NetworkHelper.getInstance().postJsonData(PP014Activity.this, METHOD_URL, requestData,type , httpHandler, false);
			}
		});
		builder.show();
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
	
	public PP014Adapter getAdapter() {
		return adapter;
	}

	public void setAdapter(PP014Adapter adapter) {
		this.adapter = adapter;
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

	public AmassHttpResponseHandler<PP014DetailData> getHttpHandler() {
		return httpHandler;
	}

	public void setHttpHandler(AmassHttpResponseHandler<PP014DetailData> httpHandler) {
		this.httpHandler = httpHandler;
	}

	public String getBoxNoByOrder() {
		return boxNoByOrder;
	}

	public void setBoxNoByOrder(String boxNoByOrder) {
		this.boxNoByOrder = boxNoByOrder;
	}

	public String getDepotDtIdScan() {
		return depotDtIdScan;
	}

	public void setDepotDtIdScan(String depotDtIdScan) {
		this.depotDtIdScan = depotDtIdScan;
	}
	
	public AmassHttpResponseHandler<CommonResult> getSaveHandler() {
		return saveHandler;
	}

	public void setSaveHandler(AmassHttpResponseHandler<CommonResult> saveHandler) {
		this.saveHandler = saveHandler;
	}

	public String getContainerCd() {
		return containerCd;
	}

	public void setContainerCd(String containerCd) {
		this.containerCd = containerCd;
	}

}
