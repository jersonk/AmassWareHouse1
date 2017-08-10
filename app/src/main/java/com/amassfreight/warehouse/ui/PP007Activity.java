package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.BoxPP004;
import com.amassfreight.domain.CommonResult;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.domain.PP005ResponseData;
import com.amassfreight.domain.PileCardResponseData;
import com.amassfreight.utils.SessionHelper;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.widget.GalleryAct;
import com.google.zxing.client.android.CaptureActivity;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class PP007Activity extends BaseActivity{
	
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
	private TextView realPackNum;       //实装件数
	private ListView pileCardList;
	private List<PileCardResponseData> datalist;  
	private Button orderPacked;         //单票装箱完成
	private Button orderPackedCancel;         //取消单票装箱完成
	private String boxNoByOrder = null;  //根据装箱时扫描到的进仓编号获得的集箱号
	private String orderCdScan = null;   //装箱时扫描到进仓编号
	private String depotDtIdScan = null; //装箱时扫描到货物明细ID
	private String depotDtIdEditing = null; //正在操作的货物明细ID
	private String containerCd = null;   //箱号
	private String orderCd;      //进仓编号public
	private Adapter adp;
	private Boolean IsEdit;
	private TextView stockQuery;
	private TextView packPhotoQuery;
	private GridView gv;
	private GridView gv1;
	private ImageAdapter imageAdpter;
	private ImageAdapter imageAdpter1;
	private File cameraTempFile;
	private List<ImageData> listImage;
	private List<ImageData> saveImageList; //需要保存的图片list
	private List<ImageData> deleteImageList; //需要删除的图片list
	private List<ImageData> listImage1;
	private List<ImageData> saveImageList1; //需要保存的图片list
	private List<ImageData> deleteImageList1; //需要删除的图片list
	
	private static final int REQUEST_CODE_PHOTO = 2; // 拍照
	private static final int REQUEST_CODE_SCAN = 1; // 拍照
	private static final int REQUEST_CODE_PHOTO1 = 21; // 拍照
	
	private Button savePic;
	private String packStatus;  //箱子装箱状态
	
	private LinearLayout linMain;
	private Boolean meunShow = false;
	private ImageButton imgBtn;
	private TextView tvOprName; 
	private TextView tvTitleCdOrder; // 进仓编号
	private boolean isShowDialog = true;
	private int realPackingNum = 0;  //实际配置的装箱件数
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp007_lclpack);
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
		realPackNum= (TextView) findViewById(R.id.realPackNum);
		pileCardList = (ListView) this.findViewById(R.id.listView);  
		orderPacked = (Button) findViewById(R.id.orderPacked);
		orderPackedCancel = (Button) findViewById(R.id.orderPackedCancel);
		
		tvOprName = (TextView) findViewById(R.id.tvOPrName);
		linMain = (LinearLayout) findViewById(R.id.linMain);
		imgBtn = (ImageButton) findViewById(R.id.imgBtn);
		tvTitleCdOrder= (TextView) findViewById(R.id.tvTitleCdOrder);
		stockQuery = (TextView) findViewById(R.id.stockQuery);
		packPhotoQuery = (TextView) findViewById(R.id.packPhotoQuery);		
		
		gv = (GridView) findViewById(R.id.gv_PP007);
		gv1 = (GridView) findViewById(R.id.gv_PP007_1);
		
		savePic = (Button) findViewById(R.id.savePic);
		File dir = new File(Environment.getExternalStorageDirectory(),
				"amass/pics/PP007");
		Utils.deleteFileAndPath(dir);
				
		listImage = new ArrayList<ImageData>();
		saveImageList = new ArrayList<ImageData>();	
		imageAdpter = new ImageAdapter(this, 0);
		deleteImageList = new ArrayList<ImageData>();	
		imageAdpter.setShowAddDesc(false);
		imageAdpter.add(new MoreData());
		gv.setAdapter(imageAdpter);
		
		listImage1 = new ArrayList<ImageData>();
		saveImageList1 = new ArrayList<ImageData>();	
		imageAdpter1 = new ImageAdapter(this, 0);
		deleteImageList1 = new ArrayList<ImageData>();	
		imageAdpter1.setShowAddDesc(false);
		imageAdpter1.add(new MoreData());
		gv1.setAdapter(imageAdpter1);
		
		//设置按钮不可见
		orderPacked.setVisibility(View.GONE);
		orderPackedCancel.setVisibility(View.GONE);
		savePic.setVisibility(View.GONE);
		
		Intent intent=getIntent(); 
		orderCdPrivate = intent.getStringExtra("orderCd");
        boxNo = intent.getStringExtra("boxNo");
        depotDtIdScan = intent.getStringExtra("depotDtId");
        containerCd = intent.getStringExtra("containerCd");
        packStatus = intent.getStringExtra("packStatus");
        IsEdit = intent.getBooleanExtra("IsEdit", true);
        if(!IsEdit){
        	gv.setVisibility(View.GONE);
        	gv1.setVisibility(View.GONE);
        }
//		String depotNum = intent.getStringExtra("depotNum");
//		String coloader = intent.getStringExtra("coloader");

        //画面初始化数据
        this.getData();
        /*if(depotDtIdScan!=null){
        	this.packing(depotDtIdScan);
        }*/
        
        // 库存查询
        stockQuery = (TextView) findViewById(R.id.stockQuery);
        stockQuery.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        stockQuery.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		Intent intent = new Intent(); 
        		intent.setClass(PP007Activity.this, OT003Activity.class); 
        		intent.putExtra("orderCd", orderCd);
        		intent.putExtra("type", "01");
        		startActivity(intent);
        	}
        });
        //装箱照片
        packPhotoQuery =(TextView) findViewById(R.id.packPhotoQuery);
        packPhotoQuery.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        packPhotoQuery.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		Intent intent = new Intent(); 
        		intent.setClass(PP007Activity.this, PP016Activity.class); 
        		intent.putExtra("boxNo", boxNo);
        		intent.putExtra("orderCd", orderCdPrivate);
        		intent.putExtra("orderCdPublic", orderCd);
        		startActivity(intent);
        	}
        });
        
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				// 查看放大图
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE) { // 添加
					Intent intent = new Intent();
					// 指定开启系统相机的Action
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					File dir = new File(Environment.getExternalStorageDirectory(),
							"amass/pics/PP007");
					//dir.mkdirs();
					cameraTempFile = new File(dir, UUID.randomUUID() + ".jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(cameraTempFile));
					startActivityForResult(intent, REQUEST_CODE_PHOTO);
				} else { // 放大
					ImageData imageData = (ImageData) data.getData();
					if(imageData.getData() != null){
						GalleryAct.start(_thisActivity, imageData.getUrl());
					}
				}
			}
		});
        
        gv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				// 查看放大图
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE) { // 添加
					Intent intent = new Intent();
					// 指定开启系统相机的Action
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					File dir = new File(Environment.getExternalStorageDirectory(),
							"amass/pics/PP007");
					//dir.mkdirs();
					cameraTempFile = new File(dir, UUID.randomUUID() + ".jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(cameraTempFile));
					startActivityForResult(intent, REQUEST_CODE_PHOTO1);
				} else { // 放大
					ImageData imageData = (ImageData) data.getData();
					if(imageData.getData() != null){
						GalleryAct.start(_thisActivity, imageData.getUrl());
					}
				}
			}
		});

		gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int pos, long id) {
				final int imagePos = pos;
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				//ImageData imageData = (ImageData) data.getData();
				if (data.getDataType() == MoreData.DATA_TYPE
						&& saveImageList.contains((ImageData)data.getData())) {
					
					DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							if (which == 0) {
								MoreData data = (MoreData) imageAdpter
										.getItem(imagePos);
								listImage.remove(data);
								ImageData imageData = (ImageData) data.getData();
								deleteImageList.add(imageData);
								saveImageList.remove(imageData);
								imageAdpter.remove(data);
								imageAdpter.notifyDataSetChanged();
								if(saveImageList == null || saveImageList.size() <= 0){
									savePic.setVisibility(View.GONE);
								}
								/*if (1==imageAdpter.getCount()) {
									savePic.setVisibility(View.GONE);
								}*/
							}
						}
					};
					new AlertDialog.Builder(_thisActivity).setItems(
							new String[] { getString(R.string.button_delete),
									getString(R.string.button_no) }, lis)
							.show();
				}
				return true;
			}
		});
		
		gv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int pos, long id) {
				final int imagePos = pos;
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				//ImageData imageData = (ImageData) data.getData();
				if (data.getDataType() == MoreData.DATA_TYPE
						&& saveImageList1.contains((ImageData)data.getData())) {
					
					DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							if (which == 0) {
								MoreData data = (MoreData) imageAdpter1
										.getItem(imagePos);
								listImage1.remove(data);
								ImageData imageData = (ImageData) data.getData();
								deleteImageList1.add(imageData);
								saveImageList1.remove(imageData);
								imageAdpter1.remove(data);
								imageAdpter1.notifyDataSetChanged();
								if(saveImageList1 == null || saveImageList1.size() <= 0){
									savePic.setVisibility(View.GONE);
								}
								/*if (1==imageAdpter.getCount()) {
									savePic.setVisibility(View.GONE);
								}*/
							}
						}
					};
					new AlertDialog.Builder(_thisActivity).setItems(
							new String[] { getString(R.string.button_delete),
									getString(R.string.button_no) }, lis)
							.show();
				}
				return true;
			}
		});
		
	}  

	//画面初始化数据
	public void getData(){
		//根据桩脚牌号查其所在进仓编号下的所有桩脚牌
		Map<String,Object> requestData = new HashMap<String,Object>();
		requestData.put("boxNo", boxNo);
		requestData.put("orderCd", orderCdPrivate);
		NetworkHelper.getInstance().postJsonData(this,
				"PP005getDetail", requestData, PP005ResponseData.class, httpHandler,isShowDialog);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//按下的如果是BACK，同时没有重复
	         Intent intent=new Intent();   
	         intent.setClass(PP007Activity.this, PP006Activity.class);   
		     intent.putExtra("boxNo", boxNo);
		     intent.putExtra("containerCd", containerCd);
		     intent.putExtra("IsEdit", IsEdit);
	         startActivity(intent);   
	         PP007Activity.this.finish();   
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
	public class Adapter extends BaseAdapter  
    {      
        private LayoutInflater mInflater = null;  
        private Adapter(Context context)  
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
            //设置装箱状态背景色
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
            if(datalist.get(position).getDepotDtId().equals(depotDtIdEditing)){
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
			startActivityForResult(intent, REQUEST_CODE_SCAN);
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

		case REQUEST_CODE_SCAN:
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
					orderRequest.put("boxNo", boxNo);
					orderCdScan = barCode02.getOrderCd();
					depotDtIdScan = barCode02.getDepotDtId();
					//根据orderCd获取集箱号
					//modify by sdhuang 20150119
					//NetworkHelper.getInstance().postJsonData(PP007Activity.this, "PP004getBoxbyOrderCd", orderRequest,BoxPP004.class ,
					//		new AmassHttpResponseHandler<BoxPP004>() {
					NetworkHelper.getInstance().postJsonData(PP007Activity.this, "PP004getBoxbyOrderCdNew", orderRequest,BoxPP004.class ,
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
								orderCdScan = responseBox.getOrderCd();
								depotDtIdScan = responseBox.getDepotDtId();
								//判断是否要进行进仓编号切换
								if(orderCdPrivate.equals(orderCdScan)){
									//装箱操作
									//packing(barCode02.getDepotDtId());
									packing(depotDtIdScan);
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
									builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
											/*Map<String,Object> requestData = new HashMap<String,Object>();
											requestData.put("orderCd", orderCdScan);
											NetworkHelper.getInstance().postJsonData(PP007Activity.this, 
													"PP005getDetail", requestData,PP005ResponseData.class, httpHandler, false);*/
											//判断是否有掏箱记录未完成
											if(!responseBox.getUnPackingFlg()){
												orderCdPrivate = orderCdScan;
												listImage.clear();
												saveImageList.clear();
												imageAdpter = new ImageAdapter(PP007Activity.this, 0);
												imageAdpter.add(new MoreData());
												imageAdpter.notifyDataSetChanged();
												gv.setAdapter(imageAdpter);											
												listImage1.clear();
												saveImageList1.clear();
												imageAdpter1 = new ImageAdapter(PP007Activity.this, 0);
												imageAdpter1.add(new MoreData());
												imageAdpter1.notifyDataSetChanged();
												gv1.setAdapter(imageAdpter1);											
												savePic.setVisibility(View.GONE);
												getData();
											}else{
												Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP004_error_unPackingRemain);
											}
											//packing(depotDtIdScan);											
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
							}else{
								if (responseBox.getLockScanRemark() == ""){
									//非目标集装箱的桩脚牌
									Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_001);
								}
								else{
									Utils.showAlertDialog(_thisActivity, responseBox.getLockScanRemark());
								}
							}
						}

					}, isShowDialog);
				}else{
					//桩脚牌错误
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_002);
				}

			} else {
				//扫描失败提示
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_scanError);
			}
			break;
		case REQUEST_CODE_PHOTO: // 拍照
			if (resultCode == Activity.RESULT_OK) {
				String path = cameraTempFile.getAbsolutePath();
				Utils.compressImage(path);
				String displyPath = path.startsWith("/") ? "file://" + path : path;
				ImageSize size = new ImageSize(100, 100);
				Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(
						displyPath, size);
				ImageData imageData = new ImageData();
				imageData.setData(thumbnail);
				imageData.setPath(path);
				imageData.setUrl(displyPath);
				listImage.add(imageData);
				saveImageList.add(imageData);
				MoreData item = new MoreData(imageData);
				//imageAdpter.insert(item, imageAdpter.getCount() - 1);
				imageAdpter.insert(item, 1);
				imageAdpter.notifyDataSetChanged();
				savePic.setVisibility(View.VISIBLE);
			}
			break;
		case REQUEST_CODE_PHOTO1:
			if (resultCode == Activity.RESULT_OK) {
				String path = cameraTempFile.getAbsolutePath();
				Utils.compressImage(path);
				String displyPath = path.startsWith("/") ? "file://" + path : path;
				ImageSize size = new ImageSize(100, 100);
				Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(
						displyPath, size);
				ImageData imageData = new ImageData();
				imageData.setData(thumbnail);
				imageData.setPath(path);
				imageData.setUrl(displyPath);
				listImage1.add(imageData);
				saveImageList1.add(imageData);
				MoreData item = new MoreData(imageData);
				//imageAdpter.insert(item, imageAdpter.getCount() - 1);
				imageAdpter1.insert(item, 1);
				imageAdpter1.notifyDataSetChanged();
				savePic.setVisibility(View.VISIBLE);
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
	
/*	//刷新
    private void refresh() {  
    	PP007Activity.this.finish();  
    	Intent intent = new Intent(PP007Activity.this,PP007Activity.class);  
    	intent.putExtra("orderCd", orderCdPrivate);
    	intent.putExtra("boxNo", boxNo);
    	intent.putExtra("depotDtId", depotDtIdScan);
    	intent.putExtra("IsEdit", IsEdit);
    	startActivity(intent);  
	}  */
	 
    //画面初始化handler
    private AmassHttpResponseHandler<PP005ResponseData> httpHandler = new AmassHttpResponseHandler<PP005ResponseData>() {
    	@Override
		protected void OnSuccess(PP005ResponseData response) {
			super.OnSuccess(response);
			PP005ResponseData postData = (PP005ResponseData) response;
			if(postData!=null && postData.getOrderCdPrivate()!=null)
			{
				tvTitleCdOrder.setText(postData.getOrderCd());  // 进仓编号
				//orderCd.setText(postData.getOrderCd());
				orderCdPrivate = postData.getOrderCdPrivate();
				orderCd = postData.getOrderCd();
				preconfigureNum.setText(postData.getPreconfigureNum());
				paidinNum.setText(postData.getPaidinNum());
				realPackingNum = postData.getRealPackingNum();
				realPackNum.setText(String.valueOf(postData.getRealPackingNum()));
				if(postData.getPreconfigureCube() != null){
				    preconfigureCube.setText(postData.getPreconfigureCube().toString());
				}
				if(postData.getPaidinCube() != null){
					paidinCube.setText(postData.getPaidinCube().toString());
				}	
				if(postData.getNumRemark()!=null && !"".equals(postData.getNumRemark())){
					numRemark.setText(postData.getNumRemark().trim());
				}				
				preconfigureWeight.setText(postData.getPreconfigureWeight());
				planRemark.setText(postData.getPlanRemark());
				if(postData.getPackingRequire() != null && !"".equals(postData.getPackingRequire())){
				    packingRequire.setText(postData.getPackingRequire().trim());
				}
		
				orderPacked.setVisibility(View.GONE);
				orderPackedCancel.setVisibility(View.GONE);
				//获取桩脚牌listview  
				if(postData.getPileCardList() != null 
						&& !postData.getPileCardList().equals(""))
				{
					datalist = postData.getPileCardList();
					//格式化list;
					adp = new Adapter(PP007Activity.this);
					pileCardList.setAdapter(adp);
					final String[] selItem;
					if(IsEdit){
					    selItem = new String[] { getString(R.string.button_detail), 
							getString(R.string.PP007_input_packing_remark)};
					}else{
						selItem = new String[] { getString(R.string.button_detail)};
					}
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

									case 1: // 输入装箱备注
										AlertDialog.Builder builder = new AlertDialog.Builder(PP007Activity.this).setTitle(
												R.string.PP007_lb_packing_remark).setCancelable(false);
										LayoutInflater factory = LayoutInflater.from(PP007Activity.this);
										final View textEntryView = factory.inflate(
												R.layout.pp007_packing_remark_dialog, null);
										final EditText packingRemark = (EditText) textEntryView
												.findViewById(R.id.packing_remark);
										packingRemark.setText(data.getPackingRemark());
										builder.setView(textEntryView);
										final AlertDialog dl = builder.show();// 显示对话框
										dl.setCanceledOnTouchOutside(false); // 点击空白处，不关闭对话框
										// "保存"按钮单击事件
										Button btnSave = (Button) textEntryView.findViewById(R.id.btn_save);
										btnSave.setOnClickListener(new View.OnClickListener() {
											public void onClick(View v) {
												String remark = packingRemark.getText().toString().trim(); // 装箱备注
												dl.dismiss(); // 关闭对话框
												Map<String,Object> requestData = new HashMap<String,Object>();
												requestData.put("depotDtId", data.getDepotDtId());
												requestData.put("packingRemark", remark);								
												NetworkHelper.getInstance().postJsonData(PP007Activity.this, 
														"PP007updatePackingRemark", requestData,CommonResult.class , PackingRemarkHandler, isShowDialog);

											}
										});

										// "取消"按钮单击事件
										Button btnCancel = (Button) textEntryView.findViewById(R.id.btn_cancel);
										btnCancel.setOnClickListener(new View.OnClickListener() {
											public void onClick(View v) {
												dl.dismiss(); // 关闭对话框
											}
										});
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
					
					int count = 0;
					for(int i = 0;i<datalist.size();i++){
						if(datalist.get(i).getPackStatus().equals("1")){
							count++;
						}
					}
					if(IsEdit){
						//判断所有桩脚牌是否都已装箱
						if(!"3".equals(packStatus) && count == datalist.size()){
							//if(!postData.getPreconfigureNum().equals(postData.getPaidinNum())){
							//设置单票完成按钮可见
							// modify by sdhuang 20150122
							//if(postData.getAuth()){
							    orderPacked.setVisibility(View.VISIBLE);
							//}
						}
//						if(!"3".equals(packStatus) && count == postData.getRealPackingNum()){
//							orderPacked.setVisibility(View.VISIBLE);
//						}
					}
				}
				
				if(IsEdit){
					if(!"3".equals(packStatus) && postData.getWorkStatus().equals("2")){
						orderPacked.setVisibility(View.GONE);
						if(postData.getAuth()){
							//设置取消单票完成按钮可见
							orderPackedCancel.setVisibility(View.VISIBLE);							
						}
					}
				}
				
				if("1".equals(postData.getShutoutFlag())){
					shutoutFlag.setText(R.string.PP005_shutoutFlag_ok);
					gv.setVisibility(View.GONE);
					orderPacked.setVisibility(View.GONE);
					orderPackedCancel.setVisibility(View.GONE);
					savePic.setVisibility(View.GONE);
				}else{
					shutoutFlag.setText(R.string.PP005_shutoutFlag_ng);
				}
				if(depotDtIdScan!=null){
		        	packing(depotDtIdScan);
		        }
				
			}
			else {
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_error_wrongOrderCd);
			}
			//SessionHelper.getInstance().setUser(user);
			SessionHelper.getInstance().initDictList(_thisActivity);

		}

    };
    
    //装箱、装箱取消操作
    public void packing(String depotDtId){
    	//装箱操作
    	Map<String,Object> requestData = new HashMap<String,Object>();
    	requestData.put("boxNo", boxNo);
    	requestData.put("orderCd", orderCdPrivate);
    	requestData.put("pileCard", depotDtId);
    	NetworkHelper.getInstance().postJsonData(PP007Activity.this, "PP007packing", requestData, CommonResult.class,
    			new AmassHttpResponseHandler<CommonResult>() {
    		@Override
    		protected void OnSuccess(CommonResult response) {
    			super.OnSuccess(response);
    			CommonResult statusData = (CommonResult) response;
    			//装箱成功
    			if(statusData.getFlag()){	
    				depotDtIdEditing = depotDtIdScan;
    				depotDtIdScan = null;
    				getData();
    				/*for(int i = 0;i < datalist.size();i++){
						if(datalist.get(i).getDepotDtId().equals(depotDtIdScan)){
							datalist.get(i).setPackStatus("1");
						}
					}
					adp.notifyDataSetChanged();*/
   				
    				//showAlertDialogRefresh(_thisActivity,R.string.PP007_msg_pack_ok);
        			//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP007_msg_pack_ok);
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
    							String.format(getResources().getString(R.string.PP007_msg_pickError_006)
    									,pileCard));
    					//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP007_msg_pickError_006);
    				}
    				else if("007".equals(statusData.getErrorCd())){
    					Utils.showAlertDialog(_thisActivity,
    							String.format(getResources().getString(R.string.PP007_msg_pickError_007)
    									,pileCard));
    					//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP007_msg_pickError_007);
    				}
    				else if("009".equals(statusData.getErrorCd())){
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_VasError);
					}
    				else if("010".equals(statusData.getErrorCd())){
						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_ShutOutError);
					}
    				else if("003".equals(statusData.getErrorCd())){
    					this.PP007Dialog(statusData.getErrorCd(),orderCdPrivate,pileCard,depotDtIdScan);
    				}
    				else if("004".equals(statusData.getErrorCd())){
    					Utils.showAlertDialog(_thisActivity,
    							String.format(getResources().getString(R.string.PP005_msg_pickError_008)
    									,pileCard));
    					//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_008);
    				}
    				else {
    					if (statusData.getRemark().trim() == ""){
    						Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
    					}
    					else{
    						Utils.showAlertDialog(_thisActivity, statusData.getRemark());
    					}
    				}
    			}
    			//SessionHelper.getInstance().setUser(user);
    			//SessionHelper.getInstance().initDictList(_thisActivity);
    		}

    		public void PP007Dialog( String errorCd , String orderCd, String pileCard,String depotDtId) {
    			AlertDialog.Builder builder = new AlertDialog.Builder(
    					new ContextThemeWrapper(_thisActivity,
    							android.R.style.Theme_Holo_Light));
    			builder.setIcon(R.drawable.ic_launcher);
    			builder.setCancelable(false);
    			final String errorCd_fin = errorCd;
    			final String orderCd_fin = orderCd ;
    			final String pileCard_fin = depotDtId;
    			if("003".equals(errorCd)){
    				builder.setMessage(String.format(getResources().getString(R.string.PP007_msg_pickError_003)
							,pileCard));
    				//builder.setMessage(R.string.PP007_msg_pickError_003);			
    			}
    			builder.setTitle(R.string.app_name);
    			builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					dialog.dismiss();
    					//_thisActivity.finish();
    					//装箱操作
    					Map<String,Object> requestData = new HashMap<String,Object>();
    					requestData.put("errorCd", errorCd_fin);
    					requestData.put("orderCd", orderCd_fin);
    					requestData.put("pileCard", pileCard_fin);
    					NetworkHelper.getInstance().postJsonData(PP007Activity.this, "PP007packingCancel", requestData, CommonResult.class,
    							new AmassHttpResponseHandler<CommonResult>() {
    						@Override
    						protected void OnSuccess(CommonResult response) {
    							super.OnSuccess(response);
    							CommonResult statusData = (CommonResult) response;
    							//装箱取消成功
    							if(statusData.getFlag()){
    								depotDtIdEditing = depotDtIdScan;
    								depotDtIdScan = null;
    								getData();
    								/*for(int i = 0;i < datalist.size();i++){
    									if(datalist.get(i).getDepotDtId().equals(depotDtIdScan)){
    										datalist.get(i).setPackStatus("0");
    										datalist.get(i).setPickStatus("0");
    									}
    								}*/
    								adp.notifyDataSetChanged();   								
    								//showAlertDialogRefresh(_thisActivity,R.string.PP007_msg_packCancel_ok);
    								//Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP007_msg_packCancel_ok);
    							}
    							else{
    								Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP007_msg_packCancel_ng);

    							}			
    							//SessionHelper.getInstance().setUser(user);
    							//SessionHelper.getInstance().initDictList(_thisActivity);
    						}
    					}, isShowDialog);
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

    	}, isShowDialog);
    }
    
	//单票装箱完成按钮
	public void orderPacked_OnClick(View arg0) {	
		String  Num1 = ((preconfigureNum.getText() == null || "".equals(preconfigureNum.getText()))
				? "0" : preconfigureNum.getText().toString());
		String  Num2 = ((paidinNum.getText() == null || "".equals(paidinNum.getText()))
				? "0" : paidinNum.getText().toString());
		//判断预配件数与实际件数是否相等
		if(Num1.equals(Num2)){
			orderPackedConfirm();
		}else{
			if(numRemark.getText()!= null && !numRemark.getText().equals(""))
			{
				orderPackedConfirm();
			}
			else{
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP007_msg_pickError_008);
			}
		}
		//orderPackedConfirm();  
	}
	
	public void orderPackedConfirm(){
		//弹出确认dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light));
		builder.setIcon(R.drawable.ic_launcher);
		builder.setCancelable(false);
		builder.setMessage(R.string.PP007_msg_pickError_009);
		builder.setTitle(R.string.app_name);
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Map<String,Object> requestData = new HashMap<String,Object>();
				requestData.put("orderCd", orderCdPrivate);	
				requestData.put("flg", "1");
				NetworkHelper.getInstance().postJsonData(PP007Activity.this, 
						"PP007updateOrderPackStatus", requestData,CommonResult.class , allPackedHandler, isShowDialog);
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
	
	//取消单票装箱完成按钮
	public void orderPackedCancel_OnClick(View arg0) {			
		//弹出确认dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light));
		builder.setIcon(R.drawable.ic_launcher);
		builder.setCancelable(false);
		builder.setMessage(R.string.PP007_msg_pickError_010);
		builder.setTitle(R.string.app_name);
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Map<String,Object> requestData = new HashMap<String,Object>();
				requestData.put("orderCd", orderCdPrivate);	
				requestData.put("flg", "0");
				NetworkHelper.getInstance().postJsonData(PP007Activity.this, 
						"PP007updateOrderPackStatus", requestData,CommonResult.class , allPackedHandler, isShowDialog);
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
					depotDtIdScan = null;
					showAlertDialogRefresh(_thisActivity,R.string.msg_common_success);
				//处理失败
				}else{
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
				}
			}else{
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
			}
			
		}
	};
	
	//保存装箱备注处理handler
	private AmassHttpResponseHandler<CommonResult> PackingRemarkHandler = new AmassHttpResponseHandler<CommonResult>() {
		protected void OnSuccess(CommonResult response) {
			super.OnSuccess(response);
			CommonResult result = (CommonResult) response;
			if(result != null){
				//处理成功
				if(result.getFlag()){			
					showAlertDialogRefresh(_thisActivity,R.string.msg_save_success);
				//处理失败
				}else{
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
				}
			}else{
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
			}
			
		}
	};
	
	/*
	 * 保存照片
	 */
	public void savePic_OnClick(View view) {
		try {
			RequestParams params = new RequestParams();
			params.put("noBox", boxNo);
			params.put("cdOrder", orderCdPrivate);
			
			RequestParams params1 = new RequestParams();
			params1.put("noBox", boxNo);
			params1.put("cdOrder", orderCdPrivate);
			
			/*int nCount = imageAdpter.getCount();
			for (int nIndex = 0; nIndex < nCount; nIndex++) {
				MoreData data = imageAdpter.getItem(nIndex);
				if (data.getDataType() == MoreData.DATA_TYPE) {
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getImageId() == null) {
						try {
							File f = new File(imageData.getPath());

							imageData.setFileUploadId(f.getName());
							params.put(imageData.getFileUploadId(), f);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}*/
 			if(saveImageList.size() > 0){
				for (ImageData imageData : saveImageList) {
					try {						
						File f = new File(imageData.getPath());
						imageData.setFileUploadId(f.getName());
						params.put(imageData.getFileUploadId(), f);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			
			NetworkHelper.getInstance().postFilesData(this,
					"PP011_UploadFiles", "PP011", params, String.class,
					new AmassHttpResponseHandler<String>() {
						@Override
						protected void OnSuccess(String response) {
							//imageAdpter.clear();
							//imageAdpter.add(new MoreData());
							saveImageList.clear();
							deleteImageList.clear();
							imageAdpter.notifyDataSetChanged();
							savePic.setVisibility(View.GONE);
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.msg_save_success));
						}

					}, isShowDialog);
			
			//保存其他装箱文件
			if(saveImageList1.size() > 0){
				for (ImageData imageData : saveImageList1) {
					try {						
						File f = new File(imageData.getPath());
						imageData.setFileUploadId(f.getName());
						params1.put(imageData.getFileUploadId(), f);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			
			NetworkHelper.getInstance().postFilesData(this,
					"PP011_UploadOtherFiles", "PP011", params1, String.class,
					new AmassHttpResponseHandler<String>() {
						@Override
						protected void OnSuccess(String response) {
							//imageAdpter.clear();
							//imageAdpter.add(new MoreData());
							saveImageList1.clear();
							deleteImageList1.clear();
							imageAdpter1.notifyDataSetChanged();
							savePic.setVisibility(View.GONE);
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.msg_save_success));
						}

					}, isShowDialog);
			
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
/*	//库存查询
	public void stockQuery_OnClick(View view) {
		Intent intent = new Intent(); 
		intent.setClass(PP007Activity.this, OT003Activity.class); 
		intent.putExtra("orderCd", orderCd);
		intent.putExtra("type", "01");
		startActivity(intent);
	}
	
	//装箱照片查询
	public void photoListQuery_OnClick(View view) {
		Intent intent = new Intent(); 
		intent.setClass(PP007Activity.this, PP016Activity.class); 
		intent.putExtra("boxNo", boxNo);
		intent.putExtra("orderCd", orderCdPrivate);
		intent.putExtra("orderCdPublic", orderCd);
		startActivity(intent);
	}*/
	
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

	public Adapter getAdp() {
		return adp;
	}

	public void setAdp(Adapter adp) {
		this.adp = adp;
	}
	
	public List<PileCardResponseData> getDatalist() {
		return datalist;
	}

	public void setDatalist(List<PileCardResponseData> datalist) {
		this.datalist = datalist;
	}

	public AmassHttpResponseHandler<CommonResult> getAllPackedHandler() {
		return allPackedHandler;
	}

	public void setAllPackedHandler(
			AmassHttpResponseHandler<CommonResult> allPackedHandler) {
		this.allPackedHandler = allPackedHandler;
	}

	public String getContainerCd() {
		return containerCd;
	}

	public void setContainerCd(String containerCd) {
		this.containerCd = containerCd;
	}

}
