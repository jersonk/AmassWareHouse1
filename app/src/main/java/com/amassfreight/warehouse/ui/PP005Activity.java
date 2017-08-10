package com.amassfreight.warehouse.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.BoxPP004;
import com.amassfreight.domain.DepotVas;
import com.amassfreight.domain.PP005ResponseData;
import com.amassfreight.domain.PP005StatusData;
import com.amassfreight.domain.PileCardResponseData;
import com.amassfreight.utils.SessionHelper;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.google.zxing.client.android.CaptureActivity;

@SuppressLint("ShowToast")
public class PP005Activity extends BaseActivity{
	
	private String orderCdPrivate;      //进仓编号Private
	private String boxNo;               //集箱号
	//private TextView orderCd;           //进仓编号
	private TextView shutoutFlag;       //是否退关
	private TextView preconfigureNum;   //预配件数
	private TextView paidinNum;         //实收件数
	private TextView preconfigureCube;  //预配立方
	private TextView paidinCube;        //实收立方
	private TextView numRemark;         //件数备注
	private TextView preconfigureWeight;//预配重量
	private TextView planRemark;        //计划备注
	private TextView packingRequire;    //装箱要求
	private ListView pileCardList;
	private List<PileCardResponseData> datalist;  
	private List<DepotVas> depotVasList; //增值一览
	private String boxNoByOrder = null;  //根据拣货时扫描到的进仓编号获得的集箱号
	private String orderCdScan = null;   //拣货时扫描到进仓编号
	private String depotDtIdScan = null; //拣货时扫描到货物明细ID
	private MyAdapter adp;
	private Boolean IsEdit;
	
	private LinearLayout linMain;
	private Boolean meunShow = false;
	private ImageButton imgBtn;
	private TextView tvOprName; 
	private TextView tvTitleCdOrder; // 进仓编号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp005_lclpick);
		setupActionBar();
		//orderCd = (TextView) findViewById(R.id.content_order_cd);
		shutoutFlag = (TextView) findViewById(R.id.content_shutout_flag);
		preconfigureNum = (TextView) findViewById(R.id.content_preconfigure_num);
		paidinNum = (TextView) findViewById(R.id.content_paidin_num);
		preconfigureCube = (TextView) findViewById(R.id.content_preconfigure_cube);
		paidinCube = (TextView) findViewById(R.id.content_paidin_cube);
		numRemark = (TextView) findViewById(R.id.content_num_remark);
		preconfigureWeight = (TextView) findViewById(R.id.content_preconfigure_weight);
		planRemark = (TextView) findViewById(R.id.content_plan_remark);
		packingRequire = (TextView) findViewById(R.id.content_packing_require);
		pileCardList = (ListView) this.findViewById(R.id.listView);  
		
		tvOprName = (TextView) findViewById(R.id.tvOPrName);
		linMain = (LinearLayout) findViewById(R.id.linMain);
		imgBtn = (ImageButton) findViewById(R.id.imgBtn);
		tvTitleCdOrder= (TextView) findViewById(R.id.tvTitleCdOrder);
		
		Intent intent=getIntent(); 
		orderCdPrivate = intent.getStringExtra("orderCd");
        boxNo = intent.getStringExtra("boxNo");
        depotDtIdScan = intent.getStringExtra("depotDtId");
        IsEdit = intent.getBooleanExtra("IsEdit", true);
//		String depotNum = intent.getStringExtra("depotNum");
//		String coloader = intent.getStringExtra("coloader");

        //画面初始化数据
        this.getData();
/*        //判断是否是点击链接跳转过来的
        if(depotDtIdScan!=null){
            this.picking(depotDtIdScan);
        }*/
	}  
	
	//画面初始化数据
	public void getData(){
		//根据桩脚牌号查其所在进仓编号下的所有桩脚牌
		Map<String,Object> requestData = new HashMap<String,Object>();
		requestData.put("orderCd", orderCdPrivate);

		NetworkHelper.getInstance().postJsonData(this, 
				"PP005getDetail", requestData, PP005ResponseData.class, httpHandler,true);
	}

	//返回
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//按下的如果是BACK，同时没有重复
	         Intent intent=new Intent();   
	         intent.setClass(PP005Activity.this, PP004Activity.class);   
		     intent.putExtra("boxNo", boxNo);
		     intent.putExtra("IsEdit", IsEdit);
	         startActivity(intent);   
	         PP005Activity.this.finish();   
			 //return true;
		}
		return super.onKeyDown(keyCode, event);
	}	

	//ViewHolder静态类  
    static class ViewHolder  
    {  
        //public ImageView img;  
        public TextView pileCardCd;
        public TextView number;
        public TextView pickStatus;
        public TextView packStatus;
        public TextView posAndLoc;
        
    }  
      
    @SuppressLint("ResourceAsColor")
	public class MyAdapter extends BaseAdapter  
    {      
        private LayoutInflater mInflater = null;  
        private MyAdapter(Context context)  
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
            return datalist.get(position);  
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
                convertView = mInflater.inflate(R.layout.activity_pp005_lclpick_item, null);  
                //holder.img = (ImageView)convertView.findViewById(R.id.img);  
                holder.pileCardCd = (TextView) convertView.findViewById(R.id.pileCardCd);
                holder.number = (TextView) convertView.findViewById(R.id.number);
                holder.pickStatus = (TextView) convertView.findViewById(R.id.pickStatus);
                holder.packStatus = (TextView) convertView.findViewById(R.id.packStatus);
                holder.posAndLoc = (TextView) convertView.findViewById(R.id.posAndLoc);

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
            holder.number.setText((String)datalist.get(position).getNumber());
            holder.posAndLoc.setText((String)datalist.get(position).getPosAndLoc());
            //设置拣货状态背景色
            if(datalist.get(position).getPickStatus().equals("1")){
            	holder.pickStatus.setText(R.string.status_pick_done);
            	holder.pickStatus.setBackgroundResource(R.color.yellow);
            }else{
            	holder.pickStatus.setText(R.string.status_pick_undo);
            	holder.pickStatus.setBackgroundResource(0);
            }
            
            if(datalist.get(position).getPackStatus().equals("1")){
            	holder.packStatus.setText(R.string.status_pack_done); 
            	holder.packStatus.setBackgroundResource(R.color.gay);
            	
/*            }else if(datalist.get(position).getPackStatus().equals("2"))
            {
            	holder.packStatus.setText("装箱中"); 
            	holder.packStatus.setBackgroundResource(R.color.lightBlue);*/
            }else{
            	holder.packStatus.setText(R.string.status_pack_undo); 
            	holder.packStatus.setBackgroundResource(0);
            }
            //设置正在操作项的颜色
            if(datalist.get(position).getDepotDtId().equals(depotDtIdScan)){
            	/*holder.pileCardCd.setTextColor(Color.RED);
            	holder.number.setTextColor(Color.RED);*/
            	convertView.setBackgroundResource(R.color.lightBlue);
            }
            /*else{
            	convertView.setBackgroundResource(0);
            }*/
            return convertView;  
        }  
          
    }  
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		//扫描进入该画面时显示，点击链接进入该画面时隐藏	
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
				//final BarCodeResult result = Utils.analysisBarCode(data.getStringExtra("SCAN_RESULT"),"02");
				final BarCode02 barCode02 = new BarCode02();
				boolean result02 = barCode02.paserBarCode(data.getStringExtra("SCAN_RESULT"));
				//判断扫描的是否是桩脚牌
				if(result02){
					//判断是否是该集装箱下的桩脚牌
					Map<String,Object> orderRequest = new HashMap<String,Object>();
					orderRequest.put("orderCd", barCode02.getOrderCd());
					orderRequest.put("depotDtId", barCode02.getDepotDtId());
					orderCdScan = barCode02.getOrderCd();
					depotDtIdScan = barCode02.getDepotDtId();
					//根据orderCd获取集箱号
					NetworkHelper.getInstance().postJsonData(PP005Activity.this, "PP004getBoxbyOrderCd", orderRequest,BoxPP004.class ,
							new AmassHttpResponseHandler<BoxPP004>() {
						@Override
						protected void OnSuccess(BoxPP004 response) {
							super.OnSuccess(response);
							final BoxPP004 responseBox = (BoxPP004) response;
							if(responseBox != null && responseBox.getBoxNo()!=null){
								boxNoByOrder = responseBox.getBoxNo();
							}else{
								boxNoByOrder = null;
							}
							//判断是否是该集装箱的货物							
							if(boxNo.equals(boxNoByOrder)){
								//判断是否要进行进仓编号切换
								if(orderCdPrivate.equals(orderCdScan)){
									//拣货操作
									picking(barCode02.getDepotDtId());
								}
								else{
									//弹出确认进仓编号切换dialog
									AlertDialog.Builder builder = new AlertDialog.Builder(
											new ContextThemeWrapper(_thisActivity,
													android.R.style.Theme_Holo_Light));
									builder.setIcon(R.drawable.ic_launcher);
									builder.setCancelable(false);
									builder.setMessage(R.string.PP005_warning_changeOrderCd);
									builder.setTitle(R.string.app_name);
									builder.setPositiveButton(R.string.button_ok, new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
											/*Map<String,Object> requestData = new HashMap<String,Object>();
											requestData.put("orderCd", orderCdScan);
											NetworkHelper.getInstance().postJsonData(PP005Activity.this,
													"PP005getDetail", requestData,PP005ResponseData.class, httpHandler, false);*/
											//判断是否有掏箱记录未完成
											if(!responseBox.getUnPackingFlg()){
												orderCdPrivate = orderCdScan;
												getData();
											}else{
												Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP004_error_unPackingRemain);
											}
											//picking(depotDtIdScan);
										}
									});
									builder.setNegativeButton(R.string.button_no, new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});
									builder.create().show();
								}
							}else{
								//非目标集装箱的桩脚牌
								Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_001);
							}
						}

					}, false);
				}else{
					//桩脚牌错误
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_002);
				}

			} else {
				//扫描失败提示
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_scanError);
			}
			break;
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
		builder.setPositiveButton(R.string.button_ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				refresh();
			}
		});
		builder.show();
	}
	
	//刷新
    private void refresh() {  
    	PP005Activity.this.finish();  
    	Intent intent = new Intent(PP005Activity.this,PP005Activity.class);  
    	intent.putExtra("orderCd", orderCdPrivate);
    	intent.putExtra("boxNo", boxNo);
    	intent.putExtra("depotDtId", depotDtIdScan);
    	intent.putExtra("IsEdit", IsEdit);
    	startActivity(intent);  
	}  
	 
    //画面初始化handler
    private AmassHttpResponseHandler<PP005ResponseData> httpHandler = new AmassHttpResponseHandler<PP005ResponseData>() {
    	@Override
		protected void OnSuccess(PP005ResponseData response) {
			// TODO Auto-generated method stub
			super.OnSuccess(response);
			PP005ResponseData postData = (PP005ResponseData) response;
			if(postData!=null && postData.getOrderCdPrivate()!=null)
			{
				tvTitleCdOrder.setText(postData.getOrderCd());  // 进仓编号
				//orderCd.setText(postData.getOrderCd());
				orderCdPrivate = postData.getOrderCdPrivate();
				if("1".equals(postData.getShutoutFlag())){
					shutoutFlag.setText(R.string.PP005_shutoutFlag_ok);
				}else{
					shutoutFlag.setText(R.string.PP005_shutoutFlag_ng);
				}
				preconfigureNum.setText(postData.getPreconfigureNum());
				paidinNum.setText(postData.getPaidinNum());
				if(postData.getPreconfigureCube() != null){
				    preconfigureCube.setText(postData.getPreconfigureCube().toString());
				}
				if(postData.getPaidinCube() != null){
				    paidinCube.setText(postData.getPaidinCube().toString());
			    }
				numRemark.setText(postData.getNumRemark());
				preconfigureWeight.setText(postData.getPreconfigureWeight());
				planRemark.setText(postData.getPlanRemark());				
				if(postData.getPackingRequire() != null && !"".equals(postData.getPackingRequire())){
				    packingRequire.setText(postData.getPackingRequire().trim());
				}
				depotVasList = postData.getDepotVasList();

				if(postData.getPileCardList() != null 
						&& !postData.getPileCardList().equals(""))
				{
					//获取桩脚牌listview  
					datalist = postData.getPileCardList();

					//格式化list;
					adp = new MyAdapter(PP005Activity.this);
					pileCardList.setAdapter(adp);
					final String[] selItem = new String[] { getString(R.string.button_detail) };
					//常按桩脚牌跳转
					pileCardList.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0, View view,
								int pos, long arg3) {

							final PileCardResponseData data = (PileCardResponseData) arg0
									.getItemAtPosition(pos);

							DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch (which) {
									case 0: // 查看
										Intent detailIntent = new Intent(_thisActivity,
												DN004Activity.class);
										String strPos = null; // 库区
										String strLocation = null;// 库位
										if (data.getPosAndLoc() != null
												&& data.getPosAndLoc().split("-").length > 0) {
											strPos = data.getPosAndLoc().split("-")[0];
											if (data.getPosAndLoc().split("-").length > 1) {
												strLocation = data.getPosAndLoc()
														.split("-")[1];
											}
										}
										Bundle detailBundle = new Bundle();
										detailBundle.putString("Type", "8"); // 更新标识
										detailBundle.putString("cdOrderPublic",
												tvTitleCdOrder.getText().toString()); // 进仓编号共通
										detailBundle.putString("noBatch", data.getBatchNo());// 批次
										detailBundle.putString("depotDtId",
												data.getDepotDtId());// 货物明细ID
										detailBundle.putString("noPilecard",data.getPileCardNo());// 桩脚牌
										detailBundle.putString("pos", strPos);// 库区
										detailBundle.putString("location", strLocation);// 库位
										detailIntent.putExtras(detailBundle);
										startActivity(detailIntent);
										break;

									default:
										break;
									}
								}
							};

							new AlertDialog.Builder(_thisActivity).setItems(selItem, lis)
									.show();
							return true;
						}
					});
				}
		        //判断是否是点击链接跳转过来的
		        if(depotDtIdScan!=null){
		            picking(depotDtIdScan);
		        }
			}
			else {
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_error_wrongOrderCd);
			}
			//SessionHelper.getInstance().setUser(user);
			SessionHelper.getInstance().initDictList(_thisActivity);

		}

    };
    
    //拣货/拣货取消操作
    public void picking(String depotDtId){
    	//批号-桩脚牌号
    	//String pileCardCd = "";
    	//桩脚牌号
    	String pileCardNo = null;
    	if(datalist != null){
    		for(int i = 0;i < datalist.size();i++){
    			if(datalist.get(i).getDepotDtId().equals(depotDtIdScan)){
    				//pileCardCd = datalist.get(i).getPileCardCd();
    				pileCardNo = datalist.get(i).getPileCardNo();
    			}
    		}
		}
    	//判断桩脚牌号是否为空
    	if(pileCardNo != null && !"".equals(pileCardNo)){ 		
    		/*int count = 0;
    		if(depotVasList != null){
    			for(int i = 0 ; i < depotVasList.size() ; i++){
    				if(!"2".equals(depotVasList.get(i).getFlgService())){
    					count++;
    				}
    			}
    		}
    		//判断是否有增值服务未完成
    		if(count == 0){*/
    			//拣货操作
    			Map<String,Object> requestData = new HashMap<String,Object>();
    			requestData.put("orderCd", orderCdPrivate);
    			requestData.put("pileCard", depotDtId);
    			NetworkHelper.getInstance().postJsonData(PP005Activity.this, "PP005picking", requestData, PP005StatusData.class,
    					new AmassHttpResponseHandler<PP005StatusData>() {
    				@Override
    				protected void OnSuccess(PP005StatusData response) {
    					// TODO Auto-generated method stub
    					super.OnSuccess(response);
    					PP005StatusData statusData = (PP005StatusData) response;
    					//拣货成功
    					if(statusData.getUpdatePickStatus()){	
    						for(int i = 0;i < datalist.size();i++){
    							if(datalist.get(i).getDepotDtId().equals(depotDtIdScan)){
    								datalist.get(i).setPickStatus("1");
    							}
    						}
    						adp.notifyDataSetChanged();
    						//showAlertDialogRefresh(_thisActivity,R.string.PP005_msg_pick_ok);
    						//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pick_ok);
    					}
    					else{
    						String pileCard = "";
    						for(int i = 0;i < datalist.size();i++){
    							if(datalist.get(i).getDepotDtId().equals(depotDtIdScan)){
    								pileCard = datalist.get(i).getPileCardCd();
    							}
    						}

    						if("001".equals(statusData.getErrorCd())){
    							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_001);
    						}
    						else if("005".equals(statusData.getErrorCd())){
    							Utils.showAlertDialog(_thisActivity,
    									String.format(getResources().getString(R.string.PP005_msg_pickError_005)
    											,pileCard));
    							//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_005);
    						}
    						else if("006".equals(statusData.getErrorCd())){
    							Utils.showAlertDialog(_thisActivity,
    									String.format(getResources().getString(R.string.PP005_msg_pickError_006)
    											,pileCard));
    							//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_006);
    						}
    						else if("007".equals(statusData.getErrorCd())){
    							Utils.showAlertDialog(_thisActivity,
    									String.format(getResources().getString(R.string.PP005_msg_pickError_007)
    											,pileCard));
    							//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_007);
    						}
    						else if("008".equals(statusData.getErrorCd())){
    							Utils.showAlertDialog(_thisActivity,
    									String.format(getResources().getString(R.string.PP005_msg_pickError_008)
    											,pileCard));
    							//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_008);
    						}    		    				
    						else if("009".equals(statusData.getErrorCd())){
    							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_VasError);
    						}
    						else if("010".equals(statusData.getErrorCd())){
    							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_ShutOutError);
    						}
    						else if("003".equals(statusData.getErrorCd())){
    							this.PP005Dialog(statusData.getErrorCd(),orderCdPrivate,pileCard,depotDtIdScan);
    						}
    						else if("004".equals(statusData.getErrorCd())){
    							this.PP005Dialog(statusData.getErrorCd(),orderCdPrivate,pileCard,depotDtIdScan);
    						}
    						else {
    							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
    						}
    					}
    					//SessionHelper.getInstance().setUser(user);
    					SessionHelper.getInstance().initDictList(_thisActivity);
    				}

    				public void PP005Dialog( String errorCd , String orderCd, String pileCard,String depotDtId) {
    					AlertDialog.Builder builder = new AlertDialog.Builder(
    							new ContextThemeWrapper(_thisActivity,
    									android.R.style.Theme_Holo_Light));
    					builder.setIcon(R.drawable.ic_launcher);
    					builder.setCancelable(false);
    					final String errorCd_fin = errorCd;
    					final String orderCd_fin = orderCd ;
    					final String pileCard_fin = depotDtId;
    					if("003".equals(errorCd)){
    						builder.setMessage(String.format(getResources().getString(R.string.PP005_msg_pickError_003)
    								,pileCard));
    						//builder.setMessage(R.string.PP005_msg_pickError_003);			
    					}else if("004".equals(errorCd)){
    						builder.setMessage(String.format(getResources().getString(R.string.PP005_msg_pickError_004)
    								,pileCard));
    						//builder.setMessage(R.string.PP005_msg_pickError_004);	
    					}
    					builder.setTitle(R.string.app_name);
    					builder.setPositiveButton(R.string.button_ok, new OnClickListener() {
    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							dialog.dismiss();
    							//_thisActivity.finish();
    							//拣货操作
    							Map<String,Object> requestData = new HashMap<String,Object>();
    							requestData.put("errorCd", errorCd_fin);
    							requestData.put("orderCd", orderCd_fin);
    							requestData.put("pileCard", pileCard_fin);
    							NetworkHelper.getInstance().postJsonData(PP005Activity.this, 
    									"PP005pickingCancel", requestData, PP005StatusData.class,
    									new AmassHttpResponseHandler<PP005StatusData>() {
    								@Override
    								protected void OnSuccess(PP005StatusData response) {
    									super.OnSuccess(response);
    									PP005StatusData statusData = (PP005StatusData) response;
    									//拣货取消成功
    									if(statusData.getUpdatePickStatus()){
    										for(int i = 0;i < datalist.size();i++){
    											if(datalist.get(i).getDepotDtId().equals(depotDtIdScan)){
    												datalist.get(i).setPickStatus("0");
    												datalist.get(i).setPackStatus("0");
    											}
    										}
    										adp.notifyDataSetChanged();
    										//getData();
    										//showAlertDialogRefresh(_thisActivity,R.string.PP005_msg_pickCancel_ok);
    										//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickCancel_ok);
    									}
    									else{
    										Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickCancel_ng);

    									}			
    									//SessionHelper.getInstance().setUser(user);
    									SessionHelper.getInstance().initDictList(_thisActivity);
    								}
    							}, false);
    						}
    					});
    					builder.setNegativeButton(R.string.button_no, new OnClickListener() {
    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							dialog.dismiss();
    						}
    					});
    					builder.create().show();
    				}

    			}, true);
    		/*}
    		else{
    			Utils.showAlertDialog(_thisActivity,
    					String.format(getResources().getString(R.string.PP005_msg_VasError)
    							,pileCardCd));
    		}*/
    	}else{
    		Utils.showAlertDialogIntMsg(_thisActivity,R.string.COMMON_PICKING_PILECARD);
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
    
	public String getOrderCdPrivate() {
		return orderCdPrivate;
	}

	public void setOrderCdPrivate(String orderCdPrivate) {
		this.orderCdPrivate = orderCdPrivate;
	}

	public AmassHttpResponseHandler<PP005ResponseData> getHttpHandler() {
		return httpHandler;
	}

	public void setHttpHandler(
			AmassHttpResponseHandler<PP005ResponseData> httpHandler) {
		this.httpHandler = httpHandler;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	
	public String getBoxNoByOrder() {
		return boxNoByOrder;
	}

	public void setBoxNoByOrder(String boxNoByOrder) {
		this.boxNoByOrder = boxNoByOrder;
	}


	public String getOrderCdScan() {
		return orderCdScan;
	}


	public void setOrderCdScan(String orderCdScan) {
		this.orderCdScan = orderCdScan;
	}


	public String getDepotDtIdScan() {
		return depotDtIdScan;
	}


	public void setDepotDtIdScan(String depotDtIdScan) {
		this.depotDtIdScan = depotDtIdScan;
	}

	public MyAdapter getAdp() {
		return adp;
	}

	public void setAdp(MyAdapter adp) {
		this.adp = adp;
	}
	
	public List<PileCardResponseData> getDatalist() {
		return datalist;
	}

	public void setDatalist(List<PileCardResponseData> datalist) {
		this.datalist = datalist;
	}

}
