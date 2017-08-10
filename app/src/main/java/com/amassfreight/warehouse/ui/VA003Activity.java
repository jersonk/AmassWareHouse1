package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DN004TDepotDt;
import com.amassfreight.domain.DN004TLoad;
import com.amassfreight.domain.DepotVA001;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.domain.VA003CompleteData;
import com.amassfreight.domain.VA003Data;
import com.amassfreight.domain.VA003FileList;
import com.amassfreight.domain.VA003PileCard;
import com.amassfreight.utils.SessionHelper;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.amassfreight.warehouse.ui.dialogs.SelectPrintDialog;
import com.amassfreight.widget.GalleryAct;
import com.amassfreight.widget.ScrollListView;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

@SuppressWarnings("rawtypes")
public class VA003Activity extends BaseActivity implements OnDialogOkListener {

	private DepotVA001 itemVA001;
	private TextView textPublicOrder;
	private TextView textServiceRemark;
	private TextView textStatus; 		// 状态
	private TextView preTransferPic;    //预存作业照片
	private GridView imageGrid;       	
	private ImageAdapter imageAdpter;
	private File cameraTempFile;
//	private List<String> listImageId_Del;          	   // 删除的照片
	private List<DN004TDepotDt> listDN004TDepotDt;     // 新增的货物明细表
	private List<DN004TLoad> listDN004TLoad;     	   // 新增的货物装卸表
	private List<DN004TDepotDt> listDN004TDepotDtUpd;  // 修改的货物明细表
	private List<DN004TLoad> listDN004TLoadUpd;        // 修改的货物装卸表
	private List<String> listPileCardNew_New;   	   // 这次新建的货物的桩脚牌Id
	private List<String> listPileCardNew_Upd;		   // 修改的已经保存在数据库中的新增桩脚牌
	private List<String> listPileCardNew_Del;		   // 删除的新增桩脚牌
	
	private View linePilecardOrig_Down;         // 原增桩脚牌列表底部黑线
	private TextView textViewObject;            // 操作对象
	private LinearLayout layoutPilecardNew;     // 新增桩脚牌的标题
	private View linePilecardNew_Up;            // 新增桩脚牌列表头部黑线
	private View linePilecardNew_Down;          // 新增桩脚牌列表底部黑线
	
	// 原桩脚牌   
	private ScrollListView listPileCard_Orig;     
	private ArrayAdapter origPileCardAdapter;     
	private LayoutInflater origPileCardInflater;  
	
	private List<String> listPileCard_Orig_ScanedBefore;		   // 以前扫描的原桩脚牌
	/* 注销  by yxq 2014/10/30 
	private List<Map<String,String>> listPileCard_Orig_ScanedNow;  // 这次扫描的原桩脚牌
	private List<String> listPileCard_Orig_ScanedUpload;           // 上传用的原桩脚牌      
	*/
	
	// 新桩脚牌  
	private ScrollListView listPileCard_New;
	private ArrayAdapter newPileCardAdapter;
	private LayoutInflater newPileCardInflater;
	
	private TextView linkViewAttachment; // "查看附件"按钮
	private MenuItem btnScan;            // "扫描"按钮
	private Button btnVasList;           // "耗材列表"按钮
	private Button btnAddPileCard;       // "添加桩脚牌"按钮
	private Button btnComplete;			 // "完成"按钮
	private Button btnCancelComplete;    // "取消完成"按钮
	private Button btnPrintPileCard_Batch; // "打印桩脚牌"按钮
	private Button btnUploadPhoto;		 // "上传照片"按钮
	private Button btnUpdateStatus;      // "更改状态"按钮
	private Button btnTempSave;			 // "保存"按钮     add by yxq 2014/09/22
	private CheckBox chkMatch; 	  // 核销
	private CheckBox chkNotMatch; // 不核销
	private CheckBox chkFlgLoader; //装卸工
	private CheckBox chkFlgService; //客服
	
	private boolean showAddPileCardBySerTypeFlg;  // 根据服务类型判断是否一定要新加桩脚牌
	private boolean scannedFlag = true;     	  // 扫描标记，记录是否可以进行下一步操作
	private boolean hasPhotoFlag;	 	  		  // 是否有照片标记
	
	private static final int REQUEST_CODE_IMAGE = 1;        // 拍照
	private static final int REQUEST_CODE_SCAN = 2;         // 扫描
	private static final int REQUEST_CODE_ADDPILECARD = 3;  // 新增桩脚牌
	private static final int REQUEST_CODE_UPDPILECARD = 4;  // 修改桩脚牌
	// add by yxq 2014/10/15 begin
	private static final int REQUEST_CODE_ADDBATCH = 5;     // 新增批次
	// add by yxq 2014/10/15 end
	
	private String cdOrder;     	// 进仓号
	private String serviceId;   	// 服务Id
	private String cdOrderPublic;   // 业务编号共通
	
	private String hasCancelCompleteAth;  // “取消完成”权限
	private String taskType;			  // 任务所属
	private long attachmentCount;		  // 附件个数
	private String flgService;			  // 状态
	private boolean flgCancel;			  // 是否取消过
	private int newPileCardPos;           // 修改的新增桩脚牌的位置
	private String newPileCardNo;         // 修改的新增桩脚牌的桩脚牌
	// add by yxq 2014/09/17 begin
	private String strCdWareHouse;		  // 选择的打印机所属仓库
	// add by yxq 2014/09/17 end
	// add by yxq 2014/09/17 begin
	private int newNoBatch;               // 新增的批次
	private int maxNoBatch = 0;           // 目前最大的批次
	// add by yxq 2014/09/17 end
	// add by yxq 2014/10/16 begin
	private Map<Integer,String> MapNoMulDepot;// 分票号
	private String[] selItem = null;
	// add by yxq 2014/10/16 end
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		itemVA001 = (DepotVA001) SessionHelper.getInstance().getObject("VA003");
		setTitle(SessionHelper.getInstance().getServiceType(
				itemVA001.getServiceType()));
		super.onCreate(savedInstanceState);

		File dir = new File(Environment.getExternalStorageDirectory(),
				"amass/pics/va003");
		Utils.deleteFileAndPath(dir);
		cdOrder = itemVA001.getCdOrder();
		serviceId = itemVA001.getServiceId();
		cdOrderPublic = itemVA001.getCdOrderPublic();

		setContentView(R.layout.activity_va003);
		setupActionBar();
		textPublicOrder = (TextView) findViewById(R.id.textCdPublicOrder);
		textServiceRemark = (TextView) findViewById(R.id.textServiceRemark);
		textStatus = (TextView) findViewById(R.id.textStatus);
		preTransferPic = (TextView) findViewById(R.id.lbl_preTransferPic);		
		preTransferPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(_thisActivity, VA003_PrePicActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("cdOrder", cdOrder);
				bundle.putString("cdOrderPublic", cdOrderPublic);
				bundle.putSerializable("itemVA001",itemVA001);
				//SessionHelper.getInstance().setObject(itemVA001);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		origPileCardInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		newPileCardInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listPileCard_Orig = (ScrollListView)findViewById(R.id.listPileCard_Orig);
		listPileCard_New = (ScrollListView)findViewById(R.id.listPileCard_New);
		
		listPileCard_Orig_ScanedBefore =  new ArrayList<String>();
		/* 注销 by yxq 2014/10/30
		listPileCard_Orig_ScanedNow = new ArrayList<Map<String,String>>();
		listPileCard_Orig_ScanedUpload = new ArrayList<String>();*/
		
		linkViewAttachment = (TextView) findViewById(R.id.linkViewAttachment);  // "查看附件"按钮
		linkViewAttachment.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);      // 下划线
		btnAddPileCard = (Button) findViewById(R.id.btnAddPileCard);        	// "添加桩脚牌"按钮
		btnComplete = (Button) findViewById(R.id.btnComplete);		 	    	// "完成"按钮
		btnCancelComplete = (Button) findViewById(R.id.btnCancelComplete);		// "取消完成"按钮
		btnPrintPileCard_Batch = (Button) findViewById(R.id.btnPrintPileCard_Batch);	// "打印桩脚牌"按钮
		btnVasList = (Button) findViewById(R.id.btnVasList);	            	// "添加耗材"按钮
		btnUploadPhoto = (Button) findViewById(R.id.btnUploadPhoto);			// "上传照片"按钮
		btnUpdateStatus = (Button) findViewById(R.id.btnUpdateStatus);			// "更改状态"按钮
		// add by yxq 2014/09/22 begin
		btnTempSave = (Button) findViewById(R.id.btnTempSave);					// "保存"按钮
		// add by yxq 2014/09/22 end
		if("1".equals(itemVA001.getServiceType()) ||
				"7".equals(itemVA001.getServiceType())){   // "打托" 或 "分货"
			showAddPileCardBySerTypeFlg = true;      
		}else{
			showAddPileCardBySerTypeFlg = false;
		}
		chkMatch = (CheckBox) findViewById(R.id.chkMatch);
		chkNotMatch = (CheckBox) findViewById(R.id.chkNotMatch);		
		// 唛核销
		chkMatch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
					chkNotMatch.setChecked(!((CheckBox) v).isChecked());
				}
			}
		});
		// 不核销
		chkNotMatch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
					chkMatch.setChecked(!((CheckBox) v).isChecked());
				}
			}
		});
		//装卸工
		chkFlgLoader = (CheckBox) findViewById(R.id.chkFlgLoader);
		chkFlgService = (CheckBox) findViewById(R.id.chkFlgService);
		chkFlgLoader.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
					chkFlgService.setChecked(!((CheckBox) v).isChecked());
				}
			}
		});
		// 客服
		chkFlgService.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
					chkFlgLoader.setChecked(!((CheckBox) v).isChecked());
				}
			}
		});
		
		// 照片
		imageGrid = (GridView) findViewById(R.id.gridView_image);     
		imageAdpter = new ImageAdapter(this, 0);
		imageGrid.setAdapter(imageAdpter);
		SetOrigPileCardAdapter();   // 原桩脚牌适配器
		SetNewPileCardAdapter();    // 新增桩脚牌适配器
		layoutPilecardNew = (LinearLayout) findViewById(R.id.layoutPilecardNew); 
		linePilecardOrig_Down = (View) findViewById(R.id.linePilecardOrig_Down); 
		textViewObject = (TextView) findViewById(R.id.textObject);
		linePilecardNew_Up = (View) findViewById(R.id.linePilecardNew_Up); 
		linePilecardNew_Down = (View) findViewById(R.id.linePilecardNew_Down); 
		GetInitData();		
		
//		listImageId_Del = new ArrayList<String>();
		listDN004TDepotDt = new ArrayList<DN004TDepotDt>();
		listDN004TLoad = new ArrayList<DN004TLoad>();
		listDN004TDepotDtUpd = new ArrayList<DN004TDepotDt>();  // 修改的货物明细表
		listDN004TLoadUpd = new ArrayList<DN004TLoad>();        // 修改的货物装卸表
		listPileCardNew_New = new ArrayList<String>();   		// 这次新建的货物的桩脚牌Id
		listPileCardNew_Upd = new ArrayList<String>();		    // 修改的已经保存在数据库中的新增桩脚牌
		listPileCardNew_Del = new ArrayList<String>();

		textPublicOrder.setText(itemVA001.getCdOrderPublic());
		textServiceRemark.setText(itemVA001.getRemark());
		
		imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE) {   // 添加
					Intent intent = new Intent();
					// 指定开启系统相机的Action
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					File dir = new File(Environment.getExternalStorageDirectory(),
							"amass/pics/va003");
					dir.mkdirs();
					cameraTempFile = new File(dir, UUID.randomUUID() + ".jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(cameraTempFile));
					startActivityForResult(intent, REQUEST_CODE_IMAGE);
				} else {      // 放大
					ImageData imageData = (ImageData) data.getData();
					if(imageData.getData() != null){
						GalleryAct.start(_thisActivity, imageData.getUrl());
					}
				}
			}
		});
		// 长按删除
		imageGrid
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int pos, long id) {
						MoreData data = (MoreData) parent.getItemAtPosition(pos);
						// 完成了，或未全部扫描，或不是(自己的任务或者   仓库主管且任务已经分配）
						/*if("2".equals(flgService)||!scannedFlag || 
								!("selfAndBegin".equals(taskType)|| 
										("yes".equals(hasCancelCompleteAth) && "otherAndBegin".equals(taskType)))){  
						   return true;
						}*/
						if (data.getDataType() == MoreData.DATA_TYPE) {
							final int imagePos = pos;
							final ImageData imageData = (ImageData) data.getData();
							// 已经上传的照片不能删除
							if(imageData.getImageId() != null){
								return true;
							}
							DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,int which) {
									if (which == 0) {
										MoreData data = (MoreData) imageAdpter.getItem(imagePos);
										imageAdpter.remove(data);
										/*if (imageData.getImageId() != null) {
											listImageId_Del.add(imageData.getImageId());
										}*/
										imageAdpter.notifyDataSetChanged();
										hasPhotoFlag = HasNewPhoto();    // 这次是否新拍了照片
										setBtnEnabled();     			 // 设置按钮显示
									}
								}
							};
							// "删除","取消"
							new AlertDialog.Builder(_thisActivity)
									.setItems(new String[] { getString(R.string.VA003_Delete), 
											getString(R.string.VA003_Cancel)},lis).show();
					}
						return true;
				}
			});
		// "原桩脚牌"单击
		listPileCard_Orig.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				ViewOrigPileCard(arg2);  // 查看原桩脚牌
			}
		});
		// "新增桩脚牌"单击
		listPileCard_New.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				// 获得选中项的HashMap对象
				ViewNewPileCard(arg2);
			}
		});
				
		// "新增桩脚牌"长按
		listPileCard_New.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,View view, final int pos, long id) {
				// 完成之后，可以查看和打印桩脚牌
				if("2".equals(flgService)){
				   ViewAndPrint(pos);
				   return true;
				}
				// 未全部扫描，或不是(自己的任务或者   仓库主管且任务已经分配）
				if(// 注销 by yxq 2014/09/22   !scannedFlag || 
						!("selfAndBegin".equals(taskType) 
				   // 注销 by yxq 2014/09/22  ||("yes".equals(hasCancelCompleteAth) && "otherAndBegin".equals(taskType))
				)){  
					selItem = new String[] { getString(R.string.VA003_View)};
				}else{
					if("7".equals(itemVA001.getServiceType())){
						selItem = new String[] { getString(R.string.VA003_View),
								getString(R.string.VA003_Create_This),
								getString(R.string.VA003_Create_Next),
								getString(R.string.VA003_Copy_This),
								getString(R.string.VA003_Copy_Next),
								getString(R.string.VA003_Update), 
								getString(R.string.VA003_Delete),
								getString(R.string.VA003_transfer),
								getString(R.string.VA003_transfer_batch)};
					}else{
						selItem = new String[] { getString(R.string.VA003_View),
								getString(R.string.VA003_Create_This),
								getString(R.string.VA003_Create_Next),
								getString(R.string.VA003_Copy_This),
								getString(R.string.VA003_Copy_Next),
								getString(R.string.VA003_Update), 
								getString(R.string.VA003_Delete)};
					}
				}
				DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {
						if(which == 0){   // 查看
							ViewNewPileCard(pos);
						} else if(getString(R.string.VA003_Create_This).equals(selItem[which])){ // 新建（本次桩脚牌）
							VA003PileCard newPileCard = (VA003PileCard) newPileCardAdapter.getItem(pos);
							AddPileCardCommon(newPileCard.getNoBatch(),REQUEST_CODE_ADDPILECARD);
						}else if(getString(R.string.VA003_Create_Next).equals(selItem[which])){  // 新建（新增批次）
							AddPileCardCommon(newNoBatch,REQUEST_CODE_ADDBATCH);
						} else if (getString(R.string.VA003_Update).equals(selItem[which])
								|| getString(R.string.VA003_Copy_This).equals(selItem[which])
								|| getString(R.string.VA003_Copy_Next).equals(selItem[which])) {  // 修改 或  复制
							newPileCardPos = pos;
							VA003PileCard newPileCard = (VA003PileCard) newPileCardAdapter.getItem(newPileCardPos);
							newPileCardNo = newPileCard.getNoPileCard();
							try{
								Intent intent = new Intent();
								intent.setClass(_thisActivity, DN004Activity.class);
								Bundle bundle = new Bundle();
								bundle.putString("cdOrderPublic",cdOrderPublic);
								bundle.putString("noPilecard",newPileCardNo);
								bundle.putString("pos",newPileCard.getPos());
								bundle.putString("location",newPileCard.getLocation());
								if(newPileCard.getDepotDtId() == null){  // 这次新增的
								   // edit by yxq 2014/10/15 begin
								   // 原代码 int index = listPileCardNew_New.indexOf(newPileCardNo);
									int index = listPileCardNew_New.indexOf(
															newPileCard.getNoBatch()+":"+newPileCardNo);
								   // edit by yxq 2014/10/15 end
								   DN004TDepotDt tDepotDt = listDN004TDepotDt.get(index);
								   DN004TLoad tload = listDN004TLoad.get(index);
								   bundle.putSerializable("DN004TDepotDt",tDepotDt);
								   bundle.putSerializable("DN004TLoad",tload);
								}else{  // 如果已经更新过了
								   if(listPileCardNew_Upd.contains(newPileCard.getDepotDtId())){
									   int index = listPileCardNew_Upd.indexOf(newPileCard.getDepotDtId());
									   DN004TDepotDt tDepotDt = listDN004TDepotDtUpd.get(index);
									   DN004TLoad tload = listDN004TLoadUpd.get(index);
									   bundle.putSerializable("DN004TDepotDt",tDepotDt);
									   bundle.putSerializable("DN004TLoad",tload);
								   }else{
									   bundle.putString("depotDtId",newPileCard.getDepotDtId());
								   }
								}
								int requestType;
								if(getString(R.string.VA003_Copy_This).equals(selItem[which])){ // 复制(本批次)
									bundle.putString("Type", "10"); // 复制标识
									bundle.putString("noBatch",newPileCard.getNoBatch()+"");
									bundle.putString("NoMultipleDepot",MapNoMulDepot.get(newPileCard.getNoBatch())); 
									requestType = REQUEST_CODE_ADDPILECARD;
								}else if(getString(R.string.VA003_Copy_Next).equals(selItem[which])){ // 复制（新增批次）
									bundle.putString("Type", "10"); // 复制标识
									bundle.putString("noBatch",newNoBatch+"");
									bundle.putString("NoMultipleDepot",MapNoMulDepot.get(newNoBatch)); 
									requestType = REQUEST_CODE_ADDBATCH;
								}else{           // 修改
									bundle.putString("Type", "6"); // 修改标识
									bundle.putString("noBatch",newPileCard.getNoBatch()+"");
									bundle.putString("NoMultipleDepot",MapNoMulDepot.get(newPileCard.getNoBatch())); 
									requestType = REQUEST_CODE_UPDPILECARD;
								}
								intent.putExtras(bundle);
								startActivityForResult(intent, requestType);
							}catch(Exception e){
								Utils.showAlertDialog(_thisActivity,"VA003Activity-修改桩脚牌:"+e.toString());
							}
						}else if (getString(R.string.VA003_Delete).equals(selItem[which])) {  // 删除
							 DeleteNewPileCard(pos);
						}else if (getString(R.string.VA003_transfer).equals(selItem[which])) {  //库存转移
							TransferNewPileCard(pos);
						}else if (getString(R.string.VA003_transfer_batch).equals(selItem[which])) {  //库存转移(批次)
							TransferNewPileCardBatch(pos);
						}
					}
				};
				// "查看"，"复制","更新"，"删除"
			    new AlertDialog.Builder(_thisActivity).setItems(selItem,lis).show();
				return true;
			}
		});
		
		// add by yxq 2014/09/24 begin
		// "原桩脚牌"长按
		listPileCard_Orig.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,View view, final int pos, long id) {
				if("8".equals(itemVA001.getServiceType())){      // 复量
					RepeatedMeasure_Dialog(pos);
				}
				// add by yxq 2014/10/20 begin 
				//modify by sdhuang 2014-12-01
				/*else if("4".equals(itemVA001.getServiceType())){ // 更改包装
					ChangePack_Dialog(pos);
				}*/
				// add by yxq 2014/10/20 end
				// add by yxq 2014/10/30 begin
				else if("10".equals(itemVA001.getServiceType()) || 
						"11".equals(itemVA001.getServiceType())){ // 更改件数 或 称重
					origPileCard_Dialog_Common(pos);
				}
				// add by yxq 2014/10/30 end
				return true;
			}
		});
	  // add by yxq 2014/09/24
	  // add by yxq 2014/10/15 begin
	  // "新增桩脚牌"按钮长按时，可以新增批次
	  btnAddPileCard.setOnLongClickListener(new View.OnLongClickListener() {
	    	 public boolean onLongClick(View v) {
	    		 new AlertDialog.Builder(new ContextThemeWrapper(_thisActivity,
	    				 android.R.style.Theme_Holo_Light))
	    			.setIcon(R.drawable.ic_launcher)
	    			.setTitle(R.string.app_name)
	    			.setMessage(R.string.VA003_inf_026)    // 是否新增批次？
	    			.setPositiveButton(R.string.button_ok,
	    					new DialogInterface.OnClickListener() {   // 确定按钮
	    						@Override
	    						public void onClick(DialogInterface dialog,int whichButton) {
	    							try{
	    								Intent intent = new Intent();
	    								intent.setClass(_thisActivity, DN004Activity.class);
	    								Bundle bundle = new Bundle();
	    								bundle.putString("Type", "3"); // 新增标识
	    								bundle.putString("cdOrderPublic",cdOrderPublic);
	    								bundle.putString("noBatch",String.valueOf(newNoBatch));
	    								intent.putExtras(bundle);
	    								startActivityForResult(intent,REQUEST_CODE_ADDBATCH);
	    							}catch(Exception e){
	    								Utils.showAlertDialog(_thisActivity,
	    										"VA003Activity-Btn_AddPileCard_OnLongClick"+e.toString());
	    							}
	    						}
	    			})		
	    			.setCancelable(false)
	    			.setNegativeButton(R.string.button_no,
	    					new DialogInterface.OnClickListener() {
	    						@Override
	    						public void onClick(DialogInterface dialog,int whichButton) {
	    						}
	    			}).show();
				return true;
	         }
	     });  
	  // add by yxq 2014/10/15 end
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (resultCode != RESULT_OK) {
			return;
		}
	    switch(requestCode){
		case REQUEST_CODE_IMAGE:    // 拍照
			if (resultCode == Activity.RESULT_OK) {
				String path = cameraTempFile.getAbsolutePath();
				Utils.compressImage(path);
				String displyPath = path.startsWith("/") ? "file://" + path
						: path;
				ImageSize size = new ImageSize(100, 100);
				Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(
						displyPath, size);
				ImageData imageData = new ImageData();
				imageData.setData(thumbnail);
				imageData.setPath(path);
				imageData.setImageDesc("新拍\n照片");
				imageData.setUrl(displyPath);
				MoreData item = new MoreData(imageData);
				//imageAdpter.insert(item, imageAdpter.getCount() - 1);
				imageAdpter.insert(item, 1);
				imageAdpter.notifyDataSetChanged();
				hasPhotoFlag = true;
				setBtnEnabled();     // 设置按钮显示
			}
			break;
		case REQUEST_CODE_SCAN:     // 扫描桩脚牌
			//扫描桩脚牌时
			final BarCode02 barCode02 = new BarCode02();
			boolean result02 = barCode02.paserBarCode(data.getStringExtra("SCAN_RESULT"));
			if(result02){
				String strScanDepotDtId =  barCode02.getDepotDtId();   // 扫描到的桩脚牌
				// add by yxq 2014/11/05 begin
				Map<String,Object> requestData = new HashMap<String,Object>();
				// 桩脚牌Id
				requestData.put("strDepotDtId",strScanDepotDtId);
				//根据orderCd获取集箱号
				NetworkHelper.getInstance().postJsonData(_thisActivity, "VA003_GetRealDepotId", 
						requestData,String.class ,
						new AmassHttpResponseHandler<String>() {
					@Override
					protected void OnSuccess(String response) {
						super.OnSuccess(response);
						String strDepotDtId = (String) response;	    // 最新的桩脚牌				
				// add by yxq 2014/11/05 end
						// 已经核销的桩脚牌
						if(listPileCard_Orig_ScanedBefore.contains(strDepotDtId)){
							// 扫描到的桩脚牌之前已经核销
							Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_014));
							return;
						}
						// 本次扫描的桩脚牌
						/* 注销 by yxq 2014/10/30 
						for(int i = 0; i < listPileCard_Orig_ScanedNow.size();i++){
							Map map = listPileCard_Orig_ScanedNow.get(i);
							if(map.containsKey(strDepotDtId)){
					    */
						for(int j = 0;j < origPileCardAdapter.getCount();j++){
							VA003PileCard origPileCard = (VA003PileCard)origPileCardAdapter.getItem(j);
							if(strDepotDtId.equals(origPileCard.getDepotDtId())){
								// edit by yxq 2014/10/30 begin
								// 原代码 CancleScan(origPileCard,map,i);  // 取消扫描
								if(origPileCard.isFlgPileCardPicking()){
									CancelScan(origPileCard);  // 取消扫描
								//  edit by yxq 2014/10/30 begin
									return;
								}
							}
						//}
						//	}
						}
						scan(strDepotDtId);   // 扫描桩脚牌
					}
				}, false);
			  }else{
				//扫描到错误的桩脚牌
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.VA003_msg_002);
			  }
			  break;
		case REQUEST_CODE_ADDPILECARD:  // 新增桩脚牌
			addPileCardCommon(data,REQUEST_CODE_ADDPILECARD);
			break;
		case REQUEST_CODE_UPDPILECARD:  // 修改桩脚牌
			if(data==null){   // 未修改
				return;
			}
			/* 获取Intent中的Bundle对象 */
			Bundle bundle1 = data.getExtras();
			/* 获取Bundle中的数据，注意类型和key */
			DN004TDepotDt depotDt1 = (DN004TDepotDt) bundle1.getSerializable("DN004TDepotDt");
			DN004TLoad tload1 = (DN004TLoad) bundle1.getSerializable("DN004TLoad");
			VA003PileCard newPileCard1 = (VA003PileCard) newPileCardAdapter.getItem(newPileCardPos);
			newPileCard1.setKgs(String.valueOf(depotDt1.getKgs()));  	       // 重量
			newPileCard1.setCbm(String.format("%.4f",depotDt1.getCbm()));  	   // 体积
			newPileCard1.setDepotNum(String.valueOf(depotDt1.getDepotNum()));  // 件数
			newPileCard1.setPos(depotDt1.getPos());                            // 库区
			newPileCard1.setLocation(depotDt1.getLocation());                  // 库位
			if(newPileCard1.getDepotDtId() != null){  // 新增桩脚牌在数据库中已经存在
				if(listPileCardNew_Upd.contains(newPileCard1.getDepotDtId())){ // 已经修改过了
					int tempPos = listPileCardNew_Upd.indexOf(newPileCard1.getDepotDtId());
					listDN004TDepotDtUpd.set(tempPos, depotDt1);
					listDN004TLoadUpd.set(tempPos,tload1);
				}else{
					listPileCardNew_Upd.add(newPileCard1.getDepotDtId());
					listDN004TDepotDtUpd.add(depotDt1);
					listDN004TLoadUpd.add(tload1);
				}
			}else{    // 不存在
				// add by yxq 2014/10/15 begin
				// 原代码 int index = listPileCardNew_New.indexOf(newPileCardNo);
				int index = listPileCardNew_New.indexOf(depotDt1.getNoBatch() +":"+ newPileCardNo);
				// add by yxq 2014/10/15 end
				listDN004TDepotDt.set(index, depotDt1);
				listDN004TLoad.set(index, tload1);;
			}
			newPileCardAdapter.notifyDataSetChanged();
	        // add by yxq 2014/10/16 begin
	     	MapNoMulDepot.put(depotDt1.getNoBatch(),depotDt1.getNoMultipleDepot());  // 分票号
	     	// add by yxq 2014/10/16 end
			break;
		// add by yxq 2014/10/15 begin
	    case REQUEST_CODE_ADDBATCH:
	    	addPileCardCommon(data,REQUEST_CODE_ADDBATCH);
	    	break;
	    // add by yxq 2014/10/15 end
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addPileCardCommon( Intent data,int type){
		if(data==null){   // 未添加
			return;
		}
		// add by yxq 2014/10/15 begin
		if(type == REQUEST_CODE_ADDBATCH || maxNoBatch == newNoBatch){   // 新增批次 或 刚刚新增时
			maxNoBatch = newNoBatch;
			newNoBatch = newNoBatch + 1;
		}
		// add by yxq 2014/10/15 end
		/* 获取Intent中的Bundle对象 */
		Bundle bundle = data.getExtras();
		/* 获取Bundle中的数据，注意类型和key */
		DN004TDepotDt depotDt = (DN004TDepotDt) bundle.getSerializable("DN004TDepotDt");
		DN004TLoad tload = (DN004TLoad) bundle.getSerializable("DN004TLoad");
		// add by yxq 2014/09/24 begin
		String strCopy = (String) bundle.getSerializable("Copies");
        int intCopy = 1;
        if (strCopy!=null && !"".equals(strCopy) && !"0".equals(strCopy))
        {
        	intCopy = Integer.parseInt(strCopy);
        }
        for(int i = 0 ; i < intCopy ; i++){
		// add by yxq 2014/09/24 end
			listDN004TDepotDt.add(depotDt);
			listDN004TLoad.add(tload);
			// 新增桩脚牌显示
			int intNoPileCard = 1;
			int intAddPos = 0;   // 插入的位置   add by yxq 2014/10/15
			if(newPileCardAdapter.getCount() > 0){  // 如果已经存在
				// 取最后一个新增桩脚牌
				// edit by yxq 2014/10/15 begin
				/* 原代码
				VA003PileCard newPileCard = (VA003PileCard) newPileCardAdapter
						.getItem(newPileCardAdapter.getCount()-1);
				intNoPileCard = Integer.parseInt(newPileCard.getNoPileCard()) + 1;*/
				VA003PileCard newPileCard;
				for(int j = newPileCardAdapter.getCount()-1;j>-1;j--){
					newPileCard = (VA003PileCard) newPileCardAdapter.getItem(j);
					if(depotDt.getNoBatch() > newPileCard.getNoBatch()){
						intAddPos = j + 1;
						break;
					}else if (depotDt.getNoBatch() == newPileCard.getNoBatch())
					{
						intNoPileCard = Integer.parseInt(newPileCard.getNoPileCard()) + 1;
						intAddPos = j + 1;
						break;
					}
				}
				// edit by yxq 2014/10/15 end
				
			}
			String strNoPileCard = intNoPileCard + "";
			if(intNoPileCard < 10){
				strNoPileCard = "0" + strNoPileCard;
			}
			// edit by yxq 2014/10/15 begin
			// 原代码 listPileCardNew_New.add(strNoPileCard);
			listPileCardNew_New.add(depotDt.getNoBatch()+":"+strNoPileCard);
			// edit by yxq 2014/10/15 end
			VA003PileCard newPileCard = new VA003PileCard();
			// edit by yxq 2014/10/15 begin
			// 原代码 newPileCard.setNoBatch(newNoBatch);		// 批次
			newPileCard.setNoBatch(depotDt.getNoBatch());					 // 批次
			// edit by yxq 2014/10/15 end
			newPileCard.setNoPileCard(strNoPileCard);			             // 桩脚牌Id
			newPileCard.setKgs(String.valueOf(depotDt.getKgs()));  			 // 重量
			newPileCard.setCbm(String.format("%.4f",depotDt.getCbm()));  	 // 体积
			newPileCard.setDepotNum(String.valueOf(depotDt.getDepotNum()));  // 件数
			newPileCard.setPos(depotDt.getPos());                            // 库区
			newPileCard.setLocation(depotDt.getLocation());                  // 库位
			// edit by yxq 2014/10/15 begin
			// 原代码 newPileCardAdapter.add(newPileCard);
			newPileCardAdapter.insert(newPileCard, intAddPos);
			// edit by yxq 2014/10/15 end
        }
        // add by yxq 2014/10/16 begin
     	MapNoMulDepot.put(depotDt.getNoBatch(),depotDt.getNoMultipleDepot());  // 分票号
     	// add by yxq 2014/10/16 end
		newPileCardAdapter.notifyDataSetChanged();
		// 新增桩脚牌表头及黑线
		layoutPilecardNew.setVisibility(View.VISIBLE);
		linePilecardNew_Up.setVisibility(View.VISIBLE);
		linePilecardNew_Down.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_scanandprint, menu);
		btnScan =  menu.findItem(R.id.scan_action); // "扫描"按钮
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
		case R.id.print_action:
			SelectPrintDialog dlg = new SelectPrintDialog(1,this);
			dlg.createDialog(this).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	// 耗材列表
	public void Btn_VasList_OnClick(View view){
		Intent intent = new Intent(this, VA004Activity.class);
		intent.putExtra("strCdOrder", cdOrder);
		intent.putExtra("strServiceId", serviceId);
		intent.putExtra("strFlgDefault", itemVA001.getServiceType());
		intent.putExtra("materialNum", getMaterialNum());
		intent.putExtra("materialCbm",getMaterialCbm());
		startActivity(intent);
	}
	
	// “查看附件”按钮
	public void Btn_View_Attachment(View view) {
		Intent intent = new Intent(_thisActivity,VA006Activity.class);
		intent.putExtra("cdOrderPublic", cdOrderPublic);
		startActivity(intent);
	}
	
	// 画面初始化时
	public void GetInitData(){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("strServiceId", serviceId);
		paraMap.put("strCdOrderPublic", cdOrderPublic);   
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_GetInitData",paraMap
				, new TypeToken<VA003Data>(){}.getType()
				, new AmassHttpResponseHandler<VA003Data>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(VA003Data response) {
						super.OnSuccess(response);
						VA003Data data = (VA003Data) response;
						// 取消完成权限
						hasCancelCompleteAth =  data.getHasCancelCompleteAth();
						// 任务所属
						taskType = data.getTaskType();
						// 附件个数
					    attachmentCount = data.getAttachmentCount();
					    // 状态
					    flgService = data.getFlgService();
					    textStatus.setText(SessionHelper.getInstance().getServiceFlg(flgService));
					    // 是否取消过
					    flgCancel = data.isFlgCancel();
						// 是否新拍了照片
						hasPhotoFlag = false;
						// 核销标志 add by sdhuang 2014-12-16
						if(data.getFlgCheck()!=null){
							if(data.getFlgCheck()){
								chkMatch.setChecked(true);
								chkNotMatch.setChecked(false);
							}else{
								chkMatch.setChecked(false);
								chkNotMatch.setChecked(true);
							}
						}else{
							chkMatch.setChecked(false);
							chkNotMatch.setChecked(false);
						}
						//装卸工
						//chkFlgLoader.setChecked(data.isFlgLoader());
						if(data.getFlgLoader() != null){
							if(data.getFlgLoader()){
								chkFlgLoader.setChecked(true);
								chkFlgService.setChecked(false);
							}else{
								chkFlgLoader.setChecked(false);
								chkFlgService.setChecked(true);
							}
						}else{
							chkFlgLoader.setChecked(false);
							chkFlgService.setChecked(false);
						}
					    // 原桩脚牌
						List list = data.getVa003PileCardList_Orig();
						origPileCardAdapter.clear();
						if(null!=list&&list.size()!=0){
							origPileCardAdapter.addAll(list);
							linePilecardOrig_Down.setVisibility(View.VISIBLE);
							textViewObject.setVisibility(View.VISIBLE);
							// 分货
							// 注销  by yxq 2014/10/30 if("7".equals(itemVA001.getServiceType())){
							for(int i = 0;i<list.size();i++){
								VA003PileCard temp = (VA003PileCard)list.get(i);
								if(temp.isFlgPileCardPicking() && 
										"0".equals(temp.getDepotNum())){  // 已经核销后
									listPileCard_Orig_ScanedBefore.add(temp.getDepotDtId());
								}
							}
							//}
						}else{
							linePilecardOrig_Down.setVisibility(View.GONE);
							textViewObject.setVisibility(View.GONE);
						}
						origPileCardAdapter.notifyDataSetChanged();
				        // 新增的桩脚牌
						newPileCardAdapter.clear();
						list = data.getVa003PileCardList_New();
						if(null!=list&&list.size()!=0){
							newPileCardAdapter.addAll(list);
							// add by yxq 2014/10/16 begin
							maxNoBatch = ((VA003PileCard)list.get(list.size()-1)).getNoBatch();
							// add by yxq 2014/10/16 end
							layoutPilecardNew.setVisibility(View.VISIBLE);
							linePilecardNew_Up.setVisibility(View.VISIBLE);
							linePilecardNew_Down.setVisibility(View.VISIBLE);
						}else{
							layoutPilecardNew.setVisibility(View.GONE);
							linePilecardNew_Up.setVisibility(View.GONE);
							linePilecardNew_Down.setVisibility(View.GONE);
						}
						newPileCardAdapter.notifyDataSetChanged();
						GetMapNoMulDepot(list);  // 得到分票号  add by yxq 2014/10/16
						// 照片
						GetPhotoInfo(data.getVa003FileList());
						setScannedFlag(data.getVa003PileCardList_Orig()); // 判断是否所有的原桩脚牌是否都扫描过了
						// edit by yxq 2014/09/17 begin
						// 原代码 setBtnEnabled();  // 设置各按钮的可用不可用状态
						GetCdWareHouse();
						// edit by yxq 2014/09/17 end
						// add by yxq 2014/09/17 begin
						newNoBatch = data.getNewNoBatch();
						if(maxNoBatch == 0 ){
							maxNoBatch = newNoBatch;
						}
						// add by yxq 2014/09/17 end
					}
				}, true);
	}
	
	// 原桩脚牌适配器
	public void SetOrigPileCardAdapter(){
		// 适配器
		origPileCardAdapter = new ArrayAdapter(getApplicationContext(), 0) {
			public View getView(int position, View convertView, ViewGroup parent) {
				TableLayout layout = new TableLayout(getContext());
				VA003PileCard data = (VA003PileCard) getItem(position);
				try {
					// 设置背景颜色
					if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}
					origPileCardInflater.inflate(R.layout.va003_list_view_pilecard_orig, layout,true);
					VA003PileCard item = (VA003PileCard) data;
					// 批号
					TextView  t1 = (TextView) layout.findViewById(R.id.txtBatchNo);  
					t1.setText(getString(R.string.VA003_BatchNo)+item.getNoBatch());
					// 桩脚牌Id
					t1 = (TextView) layout.findViewById(R.id.txtPileCardNo);
					t1.setText(getString(R.string.VA003_PilecardId)+item.getNoPileCard());
					// 库区库位
					t1 = (TextView) layout.findViewById(R.id.txtPos);
					t1.setText(getString(R.string.VA003_Pos) + item.getPos()+"-"+item.getLocation());
					t1.setTextColor(Color.BLUE);    // 蓝色
					// 件毛体
					t1 = (TextView) layout.findViewById(R.id.txtNWS);
					t1.setText(getString(R.string.VA003_NWS)+item.getDepotNum()+"/"
					            +item.getKgs()+"/"+item.getCbm());
					// 扫描状态
					t1 = (TextView) layout.findViewById(R.id.txtScanStatus);
					if(item.isFlgPileCardPicking()){    // 已扫描
						t1.setText(getString(R.string.VA003_Scanned));
						t1.setTextColor(0xFF00AE55);    // 绿色
					}else{								// 未扫描
						t1.setText(getString(R.string.VA003_NotScanned));
						t1.setTextColor(Color.BLUE);    // 蓝色
					}
				} catch (Exception e) {
					Utils.showAlertDialog(_thisActivity, "VA003Activity-initPileCard_Orig"+e.toString());
					try {
						throw e;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
					return layout;
				}
			};
			listPileCard_Orig.setAdapter(origPileCardAdapter);
	}
	
	// 新增的桩脚牌适配器
	public void SetNewPileCardAdapter(){
		// 适配器
		newPileCardAdapter = new ArrayAdapter(getApplicationContext(), 0) {
			public View getView(int position, View convertView, ViewGroup parent) {
				RelativeLayout layout = new RelativeLayout(getContext());
				VA003PileCard data = (VA003PileCard) getItem(position);
				try {
					// 设置背景颜色
					if (position % 2 == 0) {   
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}
					newPileCardInflater.inflate(R.layout.va003_list_view_pilecard_new, layout,true);
					VA003PileCard item = (VA003PileCard) data;
					// 批号
					TextView  t1 = (TextView) layout.findViewById(R.id.txtBatchNo);  
					t1.setText(item.getNoBatch()+"");
					// 桩脚牌Id
					t1 = (TextView) layout.findViewById(R.id.txtPileCardNo);
					t1.setText(item.getNoPileCard());
					// 库区库位
					t1 = (TextView) layout.findViewById(R.id.txtPos);
					t1.setText(item.getPos()+"-"+item.getLocation());
					// 件毛体
					t1 = (TextView) layout.findViewById(R.id.txtNWS);
					t1.setText(item.getDepotNum() + "/" +item.getKgs()+"/"+item.getCbm());
				} catch (Exception e) {
					Utils.showAlertDialog(_thisActivity, "VA003Activity-initPileCard_New"+e.toString());
					try {
						throw e;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
					return layout;
				}
			};
			listPileCard_New.setAdapter(newPileCardAdapter);
	}

	// 得到照片
	public void GetPhotoInfo(List<VA003FileList> list) {
		// 循环加载照片
		for (VA003FileList file : list) {
			ImageData data = new ImageData();
			data.setImageId(file.getFileId());
			data.setImageDesc(file.getFileName());
			MoreData d = new MoreData(data);
			//imageAdpter.add(d);
			//modify by sdhuang 20140910
			imageAdpter.insert(d, 0);
		}
	}
	
	// 得到新增桩脚牌列表
	@SuppressWarnings("unchecked")
	public void GetNewPileCardData(){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("strServiceId", serviceId);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_GetNewPileCardData",paraMap
				, new TypeToken<List<VA003PileCard>>(){}.getType()
				, new AmassHttpResponseHandler<List<VA003PileCard>>() {
					@Override
					protected void OnSuccess(List<VA003PileCard> response) {
						super.OnSuccess(response);
						List<VA003PileCard> list = (List<VA003PileCard>) response;
						newPileCardAdapter.clear();
						newPileCardAdapter.addAll(list);
						newPileCardAdapter.notifyDataSetChanged();
						GetMapNoMulDepot(list);  // 得到分票号  add by yxq 2014/10/16
					}
				}, true);
	}
	
	// 判断原桩脚牌的扫描情况，是否可以进行下一步操作 (根据传入的原桩脚牌List)
	public void setScannedFlag(List<VA003PileCard> list){
		if(null!=list){   // 是否有原桩脚牌
			// edit by yxq 2014/10/16 begin
			// 原代码 if("7".equals(itemVA001.getServiceType())){  // 分货
			if(!"1".equals(itemVA001.getServiceType())){        // 除打托外
			// edit by yxq 2014/10/16 end
				for(int i = 0;i<list.size();i++){		        // 原桩脚牌	
					if(list.get(i).isFlgPileCardPicking()){     // 存在已扫描的桩脚牌
						scannedFlag = true;                  
						return;
					}
					scannedFlag = false;
				}
			}else{
				for(int i = 0;i<list.size();i++){		        // 原桩脚牌	
					if(!list.get(i).isFlgPileCardPicking()){    // 存在未扫描的桩脚牌
						scannedFlag = false;
						return;
			    }
					scannedFlag = true;
		     }
			}
		}else{
			scannedFlag = true;
		}
	}
	// 判断原桩脚牌的扫描情况，是否可以进行下一步操作 (根据画面的适配器)
	/* 注销 by yxq 2014/10/16
	public void setScannedFlag_1(){
		for(int i = 0; i < origPileCardAdapter.getCount(); i++){
			VA003PileCard origPileCard = (VA003PileCard) origPileCardAdapter.getItem(i);
			if("7".equals(itemVA001.getServiceType())){      // 分货
				if(origPileCard.isFlgPileCardPicking()){     // 存在已扫描的桩脚牌
					scannedFlag = true;
					return;
				}
			}else{
				if(origPileCard.isFlgPileCardPicking()){    // 存在未扫描的桩脚牌
					scannedFlag = false;
					return;
				}
		     }
		}
		if("7".equals(itemVA001.getServiceType())){      // 分货
			scannedFlag = false;
		}else{
			scannedFlag = true;
		}
	}*/
	
	// 设置各按钮的可用不可用
	public void setBtnEnabled(){
		btnUploadPhoto.setVisibility(View.GONE);        // "上传照片"按钮
		if(attachmentCount == 0){   // 附件个数
			linkViewAttachment.setVisibility(View.GONE);    // "查看附件"按钮隐藏
		}else{
			linkViewAttachment.setVisibility(View.VISIBLE); // "查看附件"按钮显示
		}
		// 是否已经完成
		if("2".equals(flgService)){
			btnScan.setVisible(false);      				// "扫描"按钮
			btnAddPileCard.setVisibility(View.GONE);   		// "添加桩脚牌"按钮
			btnComplete.setVisibility(View.GONE);			// "完成"按钮
			btnCancelComplete.setVisibility(View.VISIBLE);	// "取消完成"按钮
			if("yes".equals(hasCancelCompleteAth)){
				btnCancelComplete.setEnabled(true); 		// "取消完成"按钮
			}else{
				btnCancelComplete.setEnabled(false); 		// "取消完成"按钮
			}
			setBtnPhotoVisible(false);      				// 拍照的"+"隐藏
			// 自己的任务 或者 （仓库主管且任务已经分派）
			// edit by yxq 2014/09/26
			/* 原代码
			if("selfAndBegin".equals(taskType)  
			     ||("yes".equals(hasCancelCompleteAth) && "otherAndBeginAndYes".equals(taskType))
			 ){
				btnVasList.setVisibility(View.VISIBLE);      // "耗材列表"按钮
			}*/
			btnVasList.setVisibility(View.GONE);             // "耗材列表"按钮
			// edit by yxq 2014/09/26 end
			// 根据服务类型判断是否显示
			// edit by yxq 2014/10/29 begin
			/* 原代码
			if(showAddPileCardBySerTypeFlg){
			*/
			if(newPileCardAdapter.getCount()>0){
			// edit by yxq 2014/10/29 end
				btnPrintPileCard_Batch.setVisibility(View.VISIBLE);  // "打印桩脚牌"按钮
			}else{
				btnPrintPileCard_Batch.setVisibility(View.GONE);     // "打印桩脚牌"按钮
			}
			btnUpdateStatus.setVisibility(View.GONE);		// "更改状态"按钮
			btnTempSave.setVisibility(View.GONE);			// "保存"按钮   add by yxq 2014/09/22
			chkMatch.setEnabled(false);
			chkNotMatch.setEnabled(false);
			chkFlgLoader.setEnabled(false);
			chkFlgService.setEnabled(false);
			preTransferPic.setVisibility(View.GONE);
			return;
		}
		btnPrintPileCard_Batch.setVisibility(View.GONE);     // "打印桩脚牌"按钮
		//  注销 by yxq 2014/09/26  btnVasList.setVisibility(View.GONE);   // "耗材列表"按钮
		btnUpdateStatus.setVisibility(View.GONE);		 // "更改状态"按钮
		
		// 自己的任务 或者 （仓库主管且任务已经分派）
		if("selfAndBegin".equals(taskType) 
		// 注销 by yxq 2014/09/23	|| ("yes".equals(hasCancelCompleteAth) && "otherAndBegin".equals(taskType))
		){
			/* 注销  by yxq 2014/09/22
			if (scannedFlag) {		// 是否可以进行扫描后的下一步操作
			*/
				setBtnPhotoVisible(true); 						// 拍照的"+"添加
			    // edit by yxq 2014/09/22 begin
			    /* 原代码 
			    if("7".equals(itemVA001.getServiceType())){     // 分货
					btnScan.setVisible(true);			        // "扫描"按钮
				}else{
					btnScan.setVisible(false);			        // "扫描"按钮
				}*/
				btnScan.setVisible(true);			            // "扫描"按钮
				btnTempSave.setVisibility(View.VISIBLE);	    // "保存"按钮
				// edit by yxq 2014/09/22 end
				// add by yxq 2014/09/26 begin
				btnVasList.setVisibility(View.VISIBLE);         // "耗材列表"
				
				// add by yxq 2014/09/26 end
				
			    // edit yxq 2014/10/29 begin
				/* 原代码
				if (!showAddPileCardBySerTypeFlg) {         	// 根据服务类型判断是否显示
					btnAddPileCard.setVisibility(View.GONE);	// "添加桩脚牌"按钮
				} else {
					btnAddPileCard.setVisibility(View.VISIBLE); // "添加桩脚牌"按钮
				}*/
				// edit by yxq 2014/09/26 end
				// 所有服务都可以新增桩脚牌
				btnAddPileCard.setVisibility(View.VISIBLE); // "添加桩脚牌"按钮

				/* 注销 by yxq 2014/09/22
				// （这次新拍了照片  或者  “取消完成过”） 且 照片不能没有
				if((hasPhotoFlag||flgCancel) && 
						imageAdpter.getCount() > 1){  	
				 // edit by yxq 2014/08/27 
				*/
				btnComplete.setVisibility(View.VISIBLE);     // "完成"按钮
				
				chkFlgLoader.setEnabled(true);					//装卸工标记按钮
				chkFlgService.setEnabled(true);
				
				/* 注销 by yxq 2014/09/22
				}else{
				   btnComplete.setVisibility(View.GONE);
				}*/
				btnCancelComplete.setVisibility(View.GONE); 	// "取消完成"按钮
		    /* 注销 by yxq 2014/09/22
			} else {
				btnScan.setVisible(true);			 			// "扫描"按钮
				btnAddPileCard.setVisibility(View.GONE); 		// "添加桩脚牌"按钮
				btnComplete.setVisibility(View.GONE); 			// "完成"按钮
				btnCancelComplete.setVisibility(View.GONE); 	// "取消完成"按钮
				setBtnPhotoVisible(false); // 拍照的"+"隐藏
			}*/
				if("1".equals(itemVA001.getServiceType())            //打托
						|| "4".equals(itemVA001.getServiceType())    //更改包装
						|| "7".equals(itemVA001.getServiceType())){  //分货
					chkMatch.setChecked(true);
					chkNotMatch.setChecked(false);
					chkMatch.setEnabled(false);
					chkNotMatch.setEnabled(false);
				}else{
					chkMatch.setEnabled(true);
					chkNotMatch.setEnabled(true);
				}
		}else{ 
			// add by yxq 2014/09/26 begin
			btnVasList.setVisibility(View.GONE);   // "耗材列表"按钮  
			// add by yxq 2014/09/26 end
			btnScan.setVisible(false);			        // "扫描"按钮
			btnAddPileCard.setVisibility(View.GONE);    // "添加桩脚牌"按钮
			btnComplete.setVisibility(View.GONE);       // "完成"按钮
			btnCancelComplete.setVisibility(View.GONE); // "取消完成"按钮
			setBtnPhotoVisible(false); // 拍照的"+"隐藏
			chkMatch.setEnabled(false);
			chkNotMatch.setEnabled(false);
			chkFlgLoader.setEnabled(false);
			chkFlgService.setEnabled(false);
			
		}
		// modify by sdhuang 2014-12-15
		if("7".equals(itemVA001.getServiceType()) && "selfAndBegin".equals(taskType)){
			preTransferPic.setVisibility(View.VISIBLE);
			preTransferPic.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
			preTransferPic.setTextColor(Color.BLUE);			
		}else{
			preTransferPic.setVisibility(View.GONE);
		}
	}
	
	// 设置拍照按钮可见性
	public void setBtnPhotoVisible(boolean visible){
		if(visible){  // 添加"+"
			if(imageAdpter.getCount()>0){  // 有照片
				//MoreData moreData = imageAdpter.getItem(imageAdpter.getCount()-1);
				MoreData moreData = imageAdpter.getItem(0);
				if(moreData.getDataType() == MoreData.MORE_TYPE){  // 最后一个不是"+"号
					return;
				}
			}
			// 照片中的"+"号
			MoreData itemAdd = new MoreData();
			//imageAdpter.insert(itemAdd, imageAdpter.getCount());
			imageAdpter.insert(itemAdd, 0);
			imageAdpter.notifyDataSetChanged();
		}else{   // 去掉"+"号
			if(imageAdpter.getCount()>0){
				//MoreData moreData = imageAdpter.getItem(imageAdpter.getCount()-1);
				MoreData moreData = imageAdpter.getItem(0);
				if(moreData.getDataType() == MoreData.MORE_TYPE){
					imageAdpter.remove(moreData);
					imageAdpter.notifyDataSetChanged();
				}
			}
		}
	}

	// 保存照片
	// flg:"1": 点击"完成" 或 "保存"按钮
	//     "2": 点击"上传照片"时
	public void SavePhoto(final String flg) {
		try {
			RequestParams params = new RequestParams();
			params.put("cdOrderPublic", cdOrderPublic);
			//照片名序列，用于排序照片
			String imgOrderList = "";
			
			int nCount = imageAdpter.getCount();
			for (int nIndex = 0; nIndex < nCount; nIndex++) {
				MoreData data = imageAdpter.getItem(nIndex);
				if (data.getDataType() == MoreData.DATA_TYPE) {
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getImageId() == null) {
						try {
							File f = new File(imageData.getPath());
							imgOrderList = imgOrderList + f.getName()+",";
							imageData.setFileUploadId(f.getName());
							params.put(imageData.getFileUploadId(), f);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}
			params.put("imgOrderList", imgOrderList);
			/*String imageId_Del = "";
			for(int i = 0;i < listImageId_Del.size(); i++){
				imageId_Del = imageId_Del + listImageId_Del.get(i)+ ",";
			}
			params.put("imageId_Del", imageId_Del);*/
			NetworkHelper.getInstance().postFilesData(this,
					"VA003_UploadFiles", "ZZ", params, new TypeToken<HashMap<String, String>>() {
					}.getType(),
					new AmassHttpResponseHandler<HashMap<String, String>>() {

						@Override
						protected void OnSuccess(
								HashMap<String, String> response) {
							//listImageId_Del.clear();
							int nCount = imageAdpter.getCount();
							for (int nIndex = 0; nIndex < nCount; nIndex++) {
								MoreData d = imageAdpter.getItem(nIndex);
								if (d.getDataType() == MoreData.DATA_TYPE) {
									ImageData image = (ImageData) d.getData();
									if (image.getImageId() == null) {
										String key = image.getFileUploadId();
										if (response.containsKey(key)) {
											String imageId = response.get(key);
											Utils.setImageInfo(image, imageId);
										}
									}
								}
							}
							imageAdpter.notifyDataSetChanged();
							// 点击“上传照片”
							if("2".equals(flg)){  // 上传成功
								Utils.showAlertDialog(_thisActivity, getString(R.string.VA003_inf_013));
							}
						}
					}, true);
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	// 这次是否真增了照片
	public boolean HasNewPhoto() {
		int nCount = imageAdpter.getCount();
		for (int nIndex = 0; nIndex < nCount; nIndex++) {
			MoreData data = imageAdpter.getItem(nIndex);
			if (data.getDataType() == MoreData.DATA_TYPE) {
				ImageData imageData = (ImageData) data.getData();
				if (imageData.getImageId() == null) {
					return true;
				}
			}
		}
		return false;
	}
	
	// “新增桩脚牌”按钮
	public void Btn_AddPileCard_OnClick(View view){
		AddPileCardCommon(maxNoBatch,REQUEST_CODE_ADDPILECARD);
	}
	
	// “打印桩脚牌”按钮
	public void Btn_PrintPilecardBatch_OnClick(View view){
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
		.setIcon(R.drawable.ic_launcher)
		.setTitle(R.string.app_name)
		.setMessage(R.string.VA003_inf_010)    // 是否确定所有新增的桩脚牌？
		.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {   // 确定按钮
					@Override
					public void onClick(DialogInterface dialog,int whichButton) {
							DoPrint_Batch();   // 批量打印新增桩脚牌
							// 事隔60秒后才能再次打印
							new CommonTimerButtonActivity(btnPrintPileCard_Batch);
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
	
	// "完成"按钮
	public void Btn_Complete_OnClick(View view){
		// add by yxq 2014/09/22 begin
		// 是否桩脚牌已经全部扫描完（分货除外）
		if(!scannedFlag){
			// add by yxq 2014/10/16 begin
			// 原代码 if("7".equals(itemVA001.getServiceType())){
			if(!"1".equals(itemVA001.getServiceType())){   // 除打托
			// add by yxq 2014/10/16 end
				// 至少扫描一个原桩脚牌！
				Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_019)); 
			}else{  
				// 必须扫描所有的原桩脚牌！
				Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_018)); 
			}
			return;
		}
		if(imageAdpter.getCount() < 2){
			// 必须拍照片！
			Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_020)); 
			return;
		}
		// add by yxq 2014/09/22 end
		if(showAddPileCardBySerTypeFlg){  // 显示新增桩脚牌按钮的
			if(newPileCardAdapter.getCount()==0){  // 没有新增的桩脚牌
				// 必须新增至少一个桩脚牌!
				Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_007)); 
				return;
			}
		}
		// 必须要选择装卸工或者客服
		if (!chkFlgLoader.isChecked() && !chkFlgService.isChecked()){
			Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_027)); 
			return;			
		}
		String message = "";
		/* 注销 by yxq 2014/10/30
		// 分货 且 本次扫描了桩脚牌
		if("7".equals(itemVA001.getServiceType()) && !listPileCard_Orig_ScanedNow.isEmpty()){ 
			message = getString(R.string.VA003_inf_016);
			int rowNo = 1;
			for(int i = 0;i < origPileCardAdapter.getCount();i++){
				VA003PileCard origPileCard = (VA003PileCard)origPileCardAdapter.getItem(i);
				for(int j = 0;j < listPileCard_Orig_ScanedNow.size();j++){
				   Map map = listPileCard_Orig_ScanedNow.get(j);
				   if(map.containsKey(origPileCard.getDepotDtId())){
					   message += String.format(getString(R.string.VA003_inf_017), 
							   rowNo, origPileCard.getNoBatch(),origPileCard.getNoPileCard(),
							   String.valueOf(map.get(origPileCard.getDepotDtId()))+"/"
							   +String.valueOf(origPileCard.getKgs())+"/"
							   +String.valueOf(origPileCard.getCbm()));
					   rowNo++;
				   }
				}
			}
		}else{
		*/
			message = getString(R.string.VA003_inf_001);
		//}
	    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
		.setIcon(R.drawable.ic_launcher)
		.setTitle(R.string.app_name)
		.setMessage(message)    // 是否确定完成？
		.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {   // 确定按钮
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						SavePhoto("1");   	// 保存照片
						VA003_Complete();   // 新增桩脚牌
					}
				})
		.setCancelable(false)
		.setNegativeButton(R.string.button_no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
					}
				});	
	    // add by yxq 2014/10/29 begin
	    if("7".equals(itemVA001.getServiceType()) ||
		   "8".equals(itemVA001.getServiceType())||
		   "10".equals(itemVA001.getServiceType()) ||
		   "11".equals(itemVA001.getServiceType())||
		   "5".equals(itemVA001.getServiceType())){  // 分货、复量、更改件数、称重 、修补包装不需要检查耗材数量
	    	builder.show();
	    }else{
	    // add by yxq 2014/10/29 end
			// add by yxq 2014/09/28 begin
		    // 得到 耗材列表的个数（1. 打托时，返回单位为“托”的数量   2.  -1代表没有耗材）
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("strServiceId", serviceId);
			NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_GetVasExpNum",
				paraMap,Double.class, new AmassHttpResponseHandler<Double>() {
					@Override
					protected void OnSuccess(Double response) {
						super.OnSuccess(response);
						Double num = (Double) response;
						if(num == -1){   // 没有耗材列表
							// 请新增耗材
							Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_024)); 
							return;
						}
						/* 注销 by yxq 2014/10/17
						// 打托时，判断添加耗材中的托数是否与新增的桩脚牌个数一致
						if("1".equals(itemVA001.getServiceType()) && num != newPileCardAdapter.getCount()){
							// 耗材中的托盘数和新增的桩脚牌的个数不一致，请确认
							Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_025)); 
							return;
						}*/
						// 打托时，耗材列表的托数
						builder.show();
					}
				}, true);
		// add by yxq 2014/09/28 end
	    }
	}
	
	// "取消完成"按钮
	public void Btn_CancelComplete_OnClick(View view){
		// 得到耗材的审核状态
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("strServiceId", serviceId);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_GetApprStatus",paraMap
			, Integer.class, new AmassHttpResponseHandler<Integer>() {
				@Override
				protected void OnSuccess(Integer response) {
					super.OnSuccess(response);
					CancelCompleteDialog(response);  
				}
			}, true);
	}
	
	// 上传照片
	public void Btn_UploadPhoto_OnClick(View view){
		if(!HasNewPhoto()){  // 没有新增的照片
			// 请新增照片
			Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_011)); 
			return;
		}
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
		.setIcon(R.drawable.ic_launcher)
		.setTitle(R.string.app_name)
		.setMessage(getString(R.string.VA003_inf_012))    // 是否确定上传照片？
		.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {   // 确定按钮
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						SavePhoto("2");   		// 保存照片
					}
				})
		.setCancelable(false)
		.setNegativeButton(R.string.button_no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
					}
				}).show();
	}
	
	// 更改增值服务的状态为"已完成"
	public void Btn_UpdateStatus_OnClick(View view){
		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(getString(R.string.VA003_inf_001))    // 是否确定更改状态？
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								UpdateStatus();
							}
						})
				.setNegativeButton(R.string.button_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}
	
	
	// “取消完成”确认框
	private void CancelCompleteDialog(Integer apprStatus){
		String message = "";
		if(apprStatus==0){    		// 尚未提交审批
			message = getString(R.string.VA003_inf_003);
		}else if(apprStatus==1){    // 提交审批
			message = getString(R.string.VA003_inf_008);   // 耗材已经提交审核，是否继续取消完成！
		}else{     // “仓库审核”或仓库审核
			// 耗材已经审核，不能取消完成！
			Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_009)); 
			return;
		}
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
		.setIcon(R.drawable.ic_launcher)
		.setTitle(R.string.app_name)
		.setMessage(message)    // 是否确定取消完成？
		.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {   // 确定按钮
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						VA003_CancelComplete();
					}
				})
		.setCancelable(false)
		.setNegativeButton(R.string.button_no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
					}
				}).show();
	}
	
	// 完成按钮
	public void VA003_Complete() {
		SaveDataCommon("complete");
	}
	
	// "取消完成"
	public void VA003_CancelComplete() {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("strServiceId", serviceId);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_CancelComplete",paraMap
			, null, new AmassHttpResponseHandler<String>() {
				@Override
				protected void OnSuccess(String response) {
					super.OnSuccess(response);
					flgService = "1";       // 未完成
					textStatus.setText(SessionHelper.getInstance().getServiceFlg(flgService)); // "作业中"
					itemVA001.setFlgService(flgService);
					itemVA001.setDtAct(null);
					flgCancel = true;       // 是否取消完成过
					setBtnEnabled();        // 设置按钮的可用性
					Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_inf_004)); // 取消成功！
				}
			}, true);
	}
	// 删除新增的桩脚牌
	private void DeleteNewPileCard(final int pos) {
		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(getString(R.string.VA003_inf_005))
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
							@SuppressWarnings("unchecked")
							public void onClick(DialogInterface dialog,int which) {
								VA003PileCard data = (VA003PileCard) newPileCardAdapter.getItem(pos);
								if(data.getDepotDtId()!=null){  // 数据库中有
									listPileCardNew_Del.add(data.getDepotDtId()); // 放入删除的List中
								}
								// 这次新增的
								// edit by yxq 2014/10/15 begin
								/* 原代码 if(listPileCardNew_New.contains(data.getNoPileCard())){
									int index = listPileCardNew_New.indexOf(data.getNoPileCard());*/
								if(listPileCardNew_New.contains(data.getNoBatch()+":"+data.getNoPileCard())){
									// 删除
									int index = listPileCardNew_New.indexOf(data.getNoBatch()+":"
																			+data.getNoPileCard());
								// edit by yxq 2014/10/15 end
									listPileCardNew_New.remove(index);
									listDN004TDepotDt.remove(index);
									listDN004TLoad.remove(index);
								}else if(listPileCardNew_Upd.contains(data.getNoPileCard())){
									// 数据库中有,这次修改的
									int index = listPileCardNew_Upd.indexOf(data.getNoPileCard());
									listPileCardNew_Upd.remove(index);
									listDN004TDepotDtUpd.remove(index);
									listDN004TLoadUpd.remove(index);									
								}
								newPileCardAdapter.remove(data);
								newPileCardAdapter.notifyDataSetChanged();
							}
						})
				.setNegativeButton(getString(R.string.button_no), null).show();
	}
	
	// 转移桩脚牌
	private void TransferNewPileCard(int pos) {
		//VA003PileCard data = (VA003PileCard) newPileCardAdapter.getItem(pos);
		//if(data.getDepotDtId()!=null){  // 数据库中有
		int count = 0;
		VA003PileCard data = (VA003PileCard) newPileCardAdapter.getItem(pos);
		for(int i=0;i < newPileCardAdapter.getCount();i++){
		    VA003PileCard pc = (VA003PileCard) newPileCardAdapter.getItem(i);
		    if(pc.getDepotDtId() == null){
		    	count++;
		    }
		}
		if(count == 0){  // 数据库中有
			Intent intent = new Intent();
			intent.setClass(_thisActivity, VA003_TransferActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("cdOrder", cdOrder);
			bundle.putString("cdOrderPublic", cdOrderPublic);
			bundle.putString("depotDtId", data.getDepotDtId());
			bundle.putString("noBatch", "");
			bundle.putSerializable("itemVA001",itemVA001);
			//SessionHelper.getInstance().setObject(itemVA001);
			intent.putExtras(bundle);
			startActivity(intent);
			this.finish();
		}else{
			//Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_warning_unsave));
			Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_warning_unsave_all));
		}
	}
	
	// 转移桩脚牌(批次)
	private void TransferNewPileCardBatch(int pos) {
		int count = 0;
		VA003PileCard data = (VA003PileCard) newPileCardAdapter.getItem(pos);
		for(int i=0;i < newPileCardAdapter.getCount();i++){
		    VA003PileCard pc = (VA003PileCard) newPileCardAdapter.getItem(i);
		    if(pc.getDepotDtId() == null){
		    	count++;
		    }
		}
		if(count == 0){  // 数据库中有
			Intent intent = new Intent();
			intent.setClass(_thisActivity, VA003_TransferActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("cdOrder", cdOrder);
			bundle.putString("cdOrderPublic", cdOrderPublic);
			bundle.putString("depotDtId", data.getDepotDtId());
			bundle.putString("noBatch", String.valueOf(data.getNoBatch()));
			bundle.putSerializable("itemVA001",itemVA001);
			//SessionHelper.getInstance().setObject(itemVA001);
			intent.putExtras(bundle);
			startActivity(intent);
			this.finish();
		}else{
			Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_warning_unsave_all));
		}
	}

	// 查看和打印桩脚牌
	private void ViewAndPrint(final int pos){
		selItem = new String[] { getString(R.string.VA003_View), 
								 getString(R.string.VA003_Print),
								 getString(R.string.VA003_Print_Confirm_This),
								 getString(R.string.VA003_Print_Confirm_All)};
		DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				VA003PileCard newPileCard = (VA003PileCard) newPileCardAdapter.getItem(pos);
				if (which == 0) {   // 查看
					ViewNewPileCard(pos);
				}else if (which == 1) {
					DoPrint(newPileCard.getDepotDtId());  // 打印桩脚牌
				}
				// add by yxq 2014/10/16 begin
				else if(which == 2){  // 打印本批次的进仓确认书
					DoPrint_Confirm(newPileCard.getDepotId(),newPileCard.getNoBatch()+"");
				}else if(which == 3){ // 打印所有批次的进仓确认书
					DoPrint_Confirm(newPileCard.getDepotId(),null);
				}
				// add by yxq 2014/10/16 end
			}
		};
		// "查看","打印桩脚牌"
		new AlertDialog.Builder(_thisActivity).setItems(selItem,lis).show();
	}
	
	// 查看新桩脚牌
	private void ViewNewPileCard(int pos){
		VA003PileCard newPileCard = (VA003PileCard) newPileCardAdapter.getItem(pos);
		try{
			Intent intent = new Intent();
			intent.setClass(_thisActivity, DN004Activity.class);
			Bundle bundle = new Bundle();
			bundle.putString("Type", "8"); // 查看标识
			bundle.putString("cdOrderPublic",cdOrderPublic);
			bundle.putString("noPilecard",newPileCard.getNoPileCard());
			bundle.putString("noBatch",String.valueOf(newPileCard.getNoBatch()));
			bundle.putString("pos",newPileCard.getPos());
			bundle.putString("location",newPileCard.getLocation());
			// add by yxq 2014/10/16 begin
			bundle.putString("NoMultipleDepot",MapNoMulDepot.get(newPileCard.getNoBatch()));
			// add by yxq 2014/10/16 end
			if(newPileCard.getDepotDtId() == null){  // 是这次新增的
			   // edit by yxq 2014/10/15 begin
			   //原代码 int index = listPileCardNew_New.indexOf(newPileCard.getNoPileCard());
			   int index = listPileCardNew_New.indexOf(
					   newPileCard.getNoBatch()+":"+newPileCard.getNoPileCard());
			   // edit by yxq 2014/10/15 end
			   DN004TDepotDt tDepotDt = listDN004TDepotDt.get(index);
			   DN004TLoad tload = listDN004TLoad.get(index);
			   bundle.putSerializable("DN004TDepotDt",tDepotDt);
			   bundle.putSerializable("DN004TLoad",tload);
			}else{  // 如果已经更新过了
			   if(listPileCardNew_Upd.contains(newPileCard.getDepotDtId())){
				   int index = listPileCardNew_Upd.indexOf(newPileCard.getDepotDtId());
				   DN004TDepotDt tDepotDt = listDN004TDepotDtUpd.get(index);
				   DN004TLoad tload = listDN004TLoadUpd.get(index);
				   bundle.putSerializable("DN004TDepotDt",tDepotDt);
				   bundle.putSerializable("DN004TLoad",tload);
			   }else{
				   bundle.putString("depotDtId",newPileCard.getDepotDtId());
			   }
			}
			intent.putExtras(bundle);
			startActivity(intent);
		}catch(Exception e){
			Utils.showAlertDialog(_thisActivity,"VA003Activity-查看新桩脚牌:"+e.toString());
		}
	}
	
	// 查看原桩脚牌
	private void ViewOrigPileCard(int pos){
		VA003PileCard origPileCard = (VA003PileCard) origPileCardAdapter.getItem(pos);
		try{
			Intent intent = new Intent();
			intent.setClass(_thisActivity, DN004Activity.class);
			Bundle bundle = new Bundle();
			bundle.putString("Type", "8"); // 查看标识
			bundle.putString("cdOrderPublic",cdOrderPublic);
			bundle.putString("noPilecard",origPileCard.getNoPileCard());
			bundle.putString("noBatch",String.valueOf(origPileCard.getNoBatch()));
			bundle.putString("pos",origPileCard.getPos());
			bundle.putString("location",origPileCard.getLocation());
			bundle.putString("depotDtId",origPileCard.getDepotDtId());
			// add by yxq 2014/10/16 begin
			bundle.putString("NoMultipleDepot",origPileCard.getNoMultipleDepot());
			// add by yxq 2014/10/16 end
			intent.putExtras(bundle);
			startActivity(intent);
		}catch(Exception e){
			Utils.showAlertDialog(_thisActivity,"VA003Activity-查看原桩脚牌:"+e.toString());
		}
	}
	
	// 打印
	private void DoPrint(String depotDtID) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotDtId", depotDtID);
		NetworkHelper.getInstance().postJsonData(_thisActivity,
			"VA003_PrintPileCardReport", p1,null, 
			new AmassHttpResponseHandler() {
				@SuppressWarnings("unchecked")
				@Override
				protected void OnSuccess(Object response) {
					super.OnSuccess(response);
					Utils.showAlertDialog(_thisActivity,getString(R.string.msg_common_print_success));
				}
			}, true);
	}
	
	// 打印进仓确认书
	// noBatch 为null时，打印所有批次的进仓确认书
	private void DoPrint_Confirm(String depotID,String noBatch) {
		List<String> noBatchList = new ArrayList<String>();
		if(noBatch==null){
			for(int i = 0 ; i< newPileCardAdapter.getCount();i++){
				VA003PileCard newPileCard = (VA003PileCard) newPileCardAdapter.getItem(i);
				if(!noBatchList.contains(newPileCard.getNoBatch()+"")){
					noBatchList.add(newPileCard.getNoBatch()+"");
				}
			}
		}else{
			noBatchList.add(noBatch);
		}
		// 得到耗材的审核状态
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("depotID", depotID);
		paraMap.put("noBatchList", noBatchList);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_PrintGoodsConfirmation",
			paraMap,null, new AmassHttpResponseHandler() {
				@SuppressWarnings("unchecked")
				@Override
				protected void OnSuccess(Object response) {
					super.OnSuccess(response);
					Utils.showAlertDialog(_thisActivity,getString(R.string.msg_common_print_success));
				}
			}, true);
	}
	
	private void DoPrint_Batch() {
		// 得到耗材的审核状态
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("strServiceId", serviceId);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_PrintPileCardReport_Batch",
			paraMap,null, new AmassHttpResponseHandler() {
				@SuppressWarnings("unchecked")
				@Override
				protected void OnSuccess(Object response) {
					super.OnSuccess(response);
					Utils.showAlertDialog(_thisActivity,getString(R.string.msg_common_print_success));
				}
			}, true);
	}
	
	// 扫描桩脚牌
	private void scan(final String strDepotDtId){
		Map<String,Object> requestData = new HashMap<String,Object>();
		// 桩脚牌Id
		requestData.put("strDepotDtId",strDepotDtId);
		// 服务Id
		requestData.put("strServiceId",serviceId);
		// 类型：扫描
		requestData.put("type","scan");
		//根据orderCd获取集箱号
		NetworkHelper.getInstance().postJsonData(_thisActivity, "VA003_ScanDepot", 
				requestData,String.class ,
				new AmassHttpResponseHandler<String>() {
			@Override
			protected void OnSuccess(String response) {
				super.OnSuccess(response);
				String scanStatus = (String) response;					
				if("outOfRange".equals(scanStatus)){	// 扫描的桩脚牌 不在 原桩脚牌中			    	
					Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_msg_001));
				}
				/* 注销  by yxq 2014/10/30 begin
				// add by yxq 2014/09/23 begin
				else if("alreadyScanned".equals(scanStatus)){ // 该桩脚牌已扫描！
					Utils.showAleertDialog(_thisActivity,getString(R.string.VA003_inf_021));
				}
				// add by yxq 2014/09/23 end
				 */
				else{
					List<VA003PileCard> list = new ArrayList<VA003PileCard>();
					for(int i = 0; i < origPileCardAdapter.getCount(); i++){
						VA003PileCard origPileCard = (VA003PileCard) origPileCardAdapter.getItem(i);
						// 找到扫描到的桩脚牌
						if(scanStatus.equals(origPileCard.getDepotDtId())){
							// 原桩脚牌
							origPileCard.setFlgPileCardPicking(true);  	      // 已扫描
							/* 注销 by yxq 2014/09/22
							if("1".equals(itemVA001.getServiceType())){       // "打托" 
								origPileCard.setDepotNum("0");			   	  // 件数为0
							}else if("7".equals(itemVA001.getServiceType())){ // 分货
								Map<String,String> map = new HashMap<String,String>();
								map.put(origPileCard.getDepotDtId(), origPileCard.getDepotNum());
								listPileCard_Orig_ScanedNow.add(map);         // 本次扫描的原桩脚牌
								origPileCard.setDepotNum("0");			   	  // 件数为0
							}*/
							origPileCardAdapter.notifyDataSetChanged();
						}
						list.add(origPileCard);
					}
					setScannedFlag(list);      // 判断是否可以进行扫描过后的
					setBtnEnabled();  		   // 设置各按钮的可用不可用状态
				}
			}
		}, true);
	}
	
	// 取消扫描
	// edit by yxq 2014/10/30 begin
	/* 原代码 
	private void CancelScan(final VA003PileCard origPileCard,final Map map,final int removePos){
	*/
	private void CancelScan(final VA003PileCard origPileCard){
    // edit by yxq 2014/10/30 end
		String message = String.format(getString(R.string.VA003_inf_015)
									,origPileCard.getNoBatch(),origPileCard.getNoPileCard());
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
		.setIcon(R.drawable.ic_launcher)
		.setTitle(R.string.app_name)
		.setMessage(message)    // 是否确定取消以下桩脚牌的核销？
		.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {   // 确定按钮
					@Override
					public void onClick(DialogInterface dialog,int whichButton) {
						// 注销 by yxq 2014/10/30 origPileCard.setFlgPileCardPicking(false);  	      // 未扫描
						// edit by yxq 2014/10/30
						// 原代码 origPileCard.setDepotNum((String)map.get(origPileCard.getDepotDtId()));// 件数  
						Map<String,Object> requestData = new HashMap<String,Object>();
						// 桩脚牌Id
						requestData.put("strDepotDtId",origPileCard.getDepotDtId());
						// 服务Id
						requestData.put("strServiceId",serviceId);
						// 类型：取消扫描
						requestData.put("type","cancel");
						// 取消扫描
						NetworkHelper.getInstance().postJsonData(_thisActivity, "VA003_ScanDepot", 
								requestData,String.class ,new AmassHttpResponseHandler<String>() {
							@Override
							protected void OnSuccess(String response) {
								super.OnSuccess(response);
								String scanStatus = (String) response;					
								if("outOfRange".equals(scanStatus)){	// 扫描的桩脚牌 不在 原桩脚牌中			    	
									Utils.showAlertDialog(_thisActivity,getString(R.string.VA003_msg_001));
								}else{
									List<VA003PileCard> list = new ArrayList<VA003PileCard>();
									for(int i = 0; i < origPileCardAdapter.getCount(); i++){
										VA003PileCard origPileCard = (VA003PileCard) origPileCardAdapter.getItem(i);
										// 找到扫描到的桩脚牌
										if(scanStatus.equals(origPileCard.getDepotDtId())){
											// 原桩脚牌
											origPileCard.setFlgPileCardPicking(false);  	      // 已扫描
											origPileCardAdapter.notifyDataSetChanged();
										}
										list.add(origPileCard);
									}
									setScannedFlag(list);      // 判断是否可以进行扫描过后的
									setBtnEnabled();  		   // 设置各按钮的可用不可用状态
								}
							}
						}, true);
						// 注销 by yxq 2014/10/30 listPileCard_Orig_ScanedNow.remove(removePos);
					}
				})
		.setCancelable(false)
		.setNegativeButton(R.string.button_no,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int whichButton) {}
				}).show();
	}
	
	// 更改状态
	private void UpdateStatus(){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("strServiceId", serviceId);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_UpdateStatus",
			paraMap,null, new AmassHttpResponseHandler() {
				@SuppressWarnings("unchecked")
				@Override
				protected void OnSuccess(Object response) {
					super.OnSuccess(response);
					flgService = "2";     // 已经完成
					itemVA001.setFlgService(flgService);
					itemVA001.setDtAct(SessionHelper.getInstance().getCurSysTime());
					setBtnEnabled();
					textStatus.setText(SessionHelper.getInstance().getServiceFlg(flgService)); // "已完成"
				}
			}, true);
	}
	
	// add by yxq 2014/09/22 begin
	// "保存"按钮
	public void Btn_TempSave_OnClick(View view){
		new AlertDialog.Builder(new ContextThemeWrapper(this,android.R.style.Theme_Holo_Light))
			.setIcon(R.drawable.ic_launcher)
			.setTitle(R.string.app_name)
			.setMessage(getString(R.string.VA003_inf_023))    // 是否确定保存？
			.setPositiveButton(R.string.button_ok,
			new DialogInterface.OnClickListener() {   // 确定按钮
				@Override
				public void onClick(DialogInterface dialog,int whichButton) {
					SavePhoto("1");   	// 保存照片
					SaveDataCommon("tempSave");   // 保存数据
				}
			})
			.setCancelable(false)
			.setNegativeButton(R.string.button_no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
						int whichButton) {
				}
			}).show();					
	}
	@SuppressWarnings("unchecked")
	private void SaveDataCommon(final String saveFlg){
		if(chkMatch.isChecked() || chkNotMatch.isChecked()){
			Map<String, Object> paraMap = new HashMap<String, Object>();
			VA003CompleteData va003CompleteData = new VA003CompleteData();
			va003CompleteData.setCdOrder(cdOrder);
			va003CompleteData.setServiceId(serviceId);
			if(chkMatch.isChecked()){
				va003CompleteData.setFlgCheck(true);
			}else{
				va003CompleteData.setFlgCheck(false);
			}
			va003CompleteData.setListDN004TDepotDt(listDN004TDepotDt);
			va003CompleteData.setListDN004TLoad(listDN004TLoad);
			va003CompleteData.setListDN004TDepotDtUpd(listDN004TDepotDtUpd);
			va003CompleteData.setListDN004TLoadUpd(listDN004TLoadUpd);
			va003CompleteData.setListPileCardNew_Del(listPileCardNew_Del);
			va003CompleteData.setListPileCardNew_Upd(listPileCardNew_Upd);
			va003CompleteData.setSaveType(saveFlg);   // 保存的类型  add by yxq 2014/09/22
			// add by yxq 2014/10/16 begin
			List listKey = new ArrayList();         
			List listValue = new ArrayList();        
			Iterator it = MapNoMulDepot.keySet().iterator();    
			while (it.hasNext()) {           
				String key = it.next().toString();             
				listKey.add(key);             
				listValue.add(MapNoMulDepot.get(Integer.valueOf(key)));
			}  
			va003CompleteData.setMapNoMulDepotKey(listKey);      // 分票号集合
			va003CompleteData.setMapNoMulDepotValue(listValue);  // 分票号集合
			va003CompleteData.setFlgLoader(chkFlgLoader.isChecked());
			// add by yxq 2014/10/16 end

			/* 注销 by yxq 2014/09/22 begin
		for(int i = 0;i < listPileCard_Orig_ScanedNow.size();i++){
			listPileCard_Orig_ScanedUpload.add(
					listPileCard_Orig_ScanedNow.get(i).entrySet()
					  .iterator().next().getKey());
		}
		va003CompleteData.setListPileCard_Orig_ScanedUpload(listPileCard_Orig_ScanedUpload);
			 */

			paraMap.put("va003CompleteData",va003CompleteData);
			NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_Complete",paraMap,String.class,
					new AmassHttpResponseHandler<String>() {
				@Override
				protected void OnSuccess(String response) {
					// add by yxq 2014/09/23 begin
					if("complete".equals(saveFlg)){
						// add by yxq 2014/09/23 end
						flgService = "2";     // 已经完成
						itemVA001.setFlgService(flgService);
						itemVA001.setDtAct(SessionHelper.getInstance().getCurSysTime());
						textStatus.setText(SessionHelper.getInstance().getServiceFlg(flgService)); // "已完成"
					}
					setBtnEnabled();
					listDN004TDepotDt = new ArrayList<DN004TDepotDt>();      // 清空新增的货物明细表
					listDN004TLoad = new ArrayList<DN004TLoad>();			 // 清空新增的货物装卸表
					listDN004TDepotDtUpd = new ArrayList<DN004TDepotDt>();   // 清空修改的货物明细表 
					listDN004TLoadUpd = new ArrayList<DN004TLoad>();   		 // 清空修改的货物装卸表 
					listPileCardNew_New = new ArrayList<String>();   		 // 清空这次新加的桩脚牌
					listPileCardNew_Upd = new ArrayList<String>();   		 // 清空这次修改的桩脚牌
					listPileCardNew_Del = new ArrayList<String>();			 // 清空删除的桩脚牌
					GetNewPileCardData();
					/* 注销 by yxq 2014/09/22
					if("7".equals(itemVA001.getServiceType())){  // 分货
						listPileCard_Orig_ScanedBefore.addAll(listPileCard_Orig_ScanedUpload);
						listPileCard_Orig_ScanedNow.clear();
						listPileCard_Orig_ScanedUpload.clear();
					}*/
					if("complete".equals(saveFlg)){
						listPileCard_Orig_ScanedBefore.clear(); // add by yxq 2014/10/31
						for(int i = 0; i < origPileCardAdapter.getCount(); i++){
							VA003PileCard origPileCard = (VA003PileCard) origPileCardAdapter.getItem(i);
							// 找到扫描到的桩脚牌
							if(origPileCard.isFlgPileCardPicking()){								
								/*if("1".equals(itemVA001.getServiceType()) ||
										"4".equals(itemVA001.getServiceType()) ||
										"7".equals(itemVA001.getServiceType())){*/
								if(chkMatch.isChecked()){
									// 原桩脚牌
									origPileCard.setDepotNum("0");		 // 件数为0
								}
								// add by yxq 2014/10/31 begin
								listPileCard_Orig_ScanedBefore.add(origPileCard.getDepotDtId());
								// add by yxq 2014/10/31 end
							}
						}
						origPileCardAdapter.notifyDataSetChanged();
						Utils.showAlertDialog(_thisActivity, getString(R.string.VA003_inf_002)); // 任务已完成！
						// add by yxq 2014/10/27 begin
						if(response != null && !"".equals(response)){         // 邮件发送时，出错信息
							Utils.showAlertDialog(_thisActivity, response); 
						}
						// add by yxq 2014/10/27 end
					}else{
						Utils.showAlertDialog(_thisActivity, getString(R.string.VA003_inf_022)); // 保存成功！
					}
				}
			}, true);
		}else{
			Utils.showAlertDialog(_thisActivity, getString(R.string.VA003_error_nocheck)); 
		}
	}
	// add by yxq 2014/09/22 end
	
	// add by yxq 2014/09/17
	private void GetCdWareHouse(){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_GetCdWareHouse",
			paraMap,String.class, new AmassHttpResponseHandler<String>() {
				@Override
				protected void OnSuccess(String response) {
					super.OnSuccess(response);
					strCdWareHouse = response;
					setBtnEnabled();
				}
			}, false);
	}
	
	// add by yxq 2014/09/24 begin
	private void RepeatedMeasure_Dialog(final int pos){
		// 完成之后 或  增值服务不是复量  或 不是自己的任务
		if("2".equals(flgService) || !"8".equals(itemVA001.getServiceType()) ||
				!("selfAndBegin".equals(taskType))){
		   return;
		}
		String[] message = new String[]{getString(R.string.VA003_ReaMea)}; // 复量
		DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				if(which == 0){   // 复量
					RepeatedMeasure((VA003PileCard) origPileCardAdapter.getItem(pos));
				}
			}
		};
		// "复量"
	    new AlertDialog.Builder(_thisActivity).setItems(message,lis).show();
	}
	// 跳转到复量画面
	private void RepeatedMeasure(VA003PileCard data) {
		SessionHelper.getInstance().setObject("VA003_ReaMea", data);
		Intent detailIntent = new Intent(_thisActivity,DN004Activity.class);
		Bundle detailBundle = new Bundle();
		detailBundle.putString("Type", "12"); 					               // 复量标识
		detailBundle.putBoolean("bolFlag", false);
		detailBundle.putString("cdOrder", cdOrder); 			               // 进仓编号个别
		detailBundle.putString("cdOrderPublic",cdOrderPublic);                 // 进仓编号共通
		detailBundle.putString("depotID", data.getDepotId());           	   // 进仓ID
		detailBundle.putString("noBatch", String.valueOf(data.getNoBatch()));  // 批次
		detailBundle.putString("thNo", data.getCoLoader());                    // 同行编号
		detailBundle.putString("depotDtID",data.getDepotDtId());
		detailBundle.putString("noPilecard",data.getNoPileCard());
		detailBundle.putString("serviceId",data.getServiceId());    // add by yxq 2014/09/29
		detailIntent.putExtras(detailBundle);
		startActivity(detailIntent);
	}
	// add by yxq 2014/09/24 end
	
	// add by yxq 2014/10/20 begin
	@SuppressWarnings("unused")
	private void ChangePack_Dialog(final int pos){
		// 完成之后 或  增值服务不是复量  或 不是自己的任务
		if("2".equals(flgService) || !"4".equals(itemVA001.getServiceType()) ||
				!("selfAndBegin".equals(taskType))){
		   return;
		}
		String[] message = new String[]{getString(R.string.VA003_ChangePack)}; // 修改包装
		DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				if(which == 0){   // 修改包装
					ChangePack((VA003PileCard) origPileCardAdapter.getItem(pos));
				}
			}
		};
		// "修改包装"
	    new AlertDialog.Builder(_thisActivity).setItems(message,lis).show();
	}
	// 跳转到更改包装画面
	private void ChangePack(VA003PileCard data) {
		SessionHelper.getInstance().setObject("VA003_ChangePack", data);
		Intent detailIntent = new Intent(_thisActivity,DN004Activity.class);
		Bundle detailBundle = new Bundle();
		detailBundle.putString("Type", "13"); 					               // 更改包装标识
		detailBundle.putBoolean("bolFlag", false);
		detailBundle.putString("cdOrder", cdOrder); 			               // 进仓编号个别
		detailBundle.putString("cdOrderPublic",cdOrderPublic);                 // 进仓编号共通
		detailBundle.putString("depotID", data.getDepotId());           	   // 进仓ID
		detailBundle.putString("noBatch", String.valueOf(data.getNoBatch()));  // 批次
		detailBundle.putString("thNo", data.getCoLoader());                    // 同行编号
		detailBundle.putString("depotDtID",data.getDepotDtId());
		detailBundle.putString("noPilecard",data.getNoPileCard());
		detailBundle.putString("serviceId",data.getServiceId());    // add by yxq 2014/09/29
		detailIntent.putExtras(detailBundle);
		startActivity(detailIntent);
	}
	// add by yxq 2014/10/20 end
	
	// add by yxq 2014/10/20 begin
	// 更改件数、称重
	private void origPileCard_Dialog_Common(final int pos){
		// 完成之后 或  增值服务不是复量  或 不是自己的任务
		if("2".equals(flgService) || !("selfAndBegin".equals(taskType))){
		   return;
		}
		String[] message;
		if("10".equals(itemVA001.getServiceType())){
			message = new String[]{getString(R.string.VA003_ChangeNum)}; // 更改件数
		}else if("11".equals(itemVA001.getServiceType())){
			message = new String[]{getString(R.string.VA003_Weigh)};     // 称重
		}else{
			return;
		}
		DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				if(which == 0){   // 点击
				    String Type = "";
					if("10".equals(itemVA001.getServiceType())){
						Type = "14"; // 更改件数
					}else if("11".equals(itemVA001.getServiceType())){
						Type = "15"; // 称重
					}
					ChangPileCardCommon((VA003PileCard) origPileCardAdapter.getItem(pos),Type);
				}
			}
		};
		// "更改件数" 或  称重
	    new AlertDialog.Builder(_thisActivity).setItems(message,lis).show();
	}
	// 跳转到修改桩脚牌的画面
	private void ChangPileCardCommon(VA003PileCard data,String Type) {
		SessionHelper.getInstance().setObject("VA003_ChangPileCardCommon", data);
		Intent detailIntent = new Intent(_thisActivity,DN004Activity.class);
		Bundle detailBundle = new Bundle();
		detailBundle.putString("Type", Type); 					               // 更改包装标识
		detailBundle.putBoolean("bolFlag", false);
		detailBundle.putString("cdOrder", cdOrder); 			               // 进仓编号个别
		detailBundle.putString("cdOrderPublic",cdOrderPublic);                 // 进仓编号共通
		detailBundle.putString("depotID", data.getDepotId());           	   // 进仓ID
		detailBundle.putString("noBatch", String.valueOf(data.getNoBatch()));  // 批次
		detailBundle.putString("thNo", data.getCoLoader());                    // 同行编号
		detailBundle.putString("depotDtID",data.getDepotDtId());
		detailBundle.putString("noPilecard",data.getNoPileCard());
		detailBundle.putString("serviceId",data.getServiceId());
		detailIntent.putExtras(detailBundle);
		startActivity(detailIntent);
	}
	// add by yxq 2014/10/20 end
	
	// add by yxq 2014/10/16 begin
	@SuppressLint("UseSparseArrays")
	public void GetMapNoMulDepot(List<VA003PileCard> list){
		MapNoMulDepot = new HashMap<Integer,String>(); 
		if(list == null || list.size() == 0){
			return;
		}
		int beforeNoBatch = -1;
		for(int i = 0;i<list.size();i++){
			VA003PileCard temp = (VA003PileCard)(list.get(i));
			if(beforeNoBatch != temp.getNoBatch()){  // 不一样的批次
				MapNoMulDepot.put(temp.getNoBatch(),temp.getNoMultipleDepot());
				beforeNoBatch = temp.getNoBatch();
			}
		}
	}
	
	public void AddPileCardCommon(int noBatch,int type){
		try{
			Intent intent = new Intent();
			intent.setClass(_thisActivity, DN004Activity.class);
			Bundle bundle = new Bundle();
			bundle.putString("Type", "3"); // 新增标识
			bundle.putString("cdOrderPublic",cdOrderPublic);
			bundle.putString("noBatch",String.valueOf(noBatch));
			bundle.putString("NoMultipleDepot",MapNoMulDepot.get(noBatch)); 
			intent.putExtras(bundle);
			startActivityForResult(intent,type);
		}catch(Exception e){
			Utils.showAlertDialog(_thisActivity,"VA003Activity-AddPileCardCommon"+e.toString());
		}
	}
	// add by yxq 2014/10/16 end
	
	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
		GetCdWareHouse();   // add by yxq 2014/09/17 begin
	}
	
	// add by yxq 2014/10/20 begin
	@Override
	protected void onResume() {
		origPileCardAdapter.notifyDataSetChanged();
		super.onResume();
	}
	
	/**
	 * 计算默认耗材数量
	 * @return
	 */
	private int getMaterialNum(){
		int num = 0;
		int count = 0;
		if(chkMatch.isChecked()){
			count = newPileCardAdapter.getCount();
			for(int i=0;i<count;i++){
				VA003PileCard newPileCard = (VA003PileCard) newPileCardAdapter.getItem(i);
				num = num + Integer.parseInt(newPileCard.getDepotNum());
			}
		}else{
			count = origPileCardAdapter.getCount();
			for(int i=0;i<count;i++){
				VA003PileCard newPileCard = (VA003PileCard) origPileCardAdapter.getItem(i);
				if(newPileCard.isFlgPileCardPicking()){
				    num = num + Integer.parseInt(newPileCard.getDepotNum());
				}
			}
		}
		return num;
	}
	
	/**
	 * 计算默认耗材体积
	 * @return
	 */
	private double getMaterialCbm(){
		double num = 0;
		int count = 0;
		if(chkMatch.isChecked()){
			count = newPileCardAdapter.getCount();
			for(int i=0;i<count;i++){
				VA003PileCard newPileCard = (VA003PileCard) newPileCardAdapter.getItem(i);
				num = num + Double.parseDouble(newPileCard.getCbm());
			}
		}else{
			count = origPileCardAdapter.getCount();
			for(int i=0;i<count;i++){
				VA003PileCard newPileCard = (VA003PileCard) origPileCardAdapter.getItem(i);
				if(newPileCard.isFlgPileCardPicking()){
				    num = num + Double.parseDouble(newPileCard.getCbm());
				}
			}
		}
		return num;
	}
}
