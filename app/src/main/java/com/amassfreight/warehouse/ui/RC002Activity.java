package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.domain.RC001Deliver_batch;
import com.amassfreight.domain.RC001PileCard;
import com.amassfreight.domain.RC002ListData;
import com.amassfreight.domain.SelectDict;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.amassfreight.warehouse.ui.dialogs.SelectPrintDialog;
import com.amassfreight.widget.GalleryAct;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class RC002Activity extends BaseActivity implements OnItemSelectedListener, OnDialogOkListener{ 
	private RC001PileCard pileCard;
	//private List<RC001PileCard> total_pilecard = new ArrayList<RC001PileCard>();
	private List<RC001Deliver_batch> deliver_batch_list = new ArrayList<RC001Deliver_batch>();
	private TextView tv_cd_order_public;
	private TextView tv_co_loader;
	private TextView tv_no_batch;
	private TextView tv_no_pilecard;
	private TextView tv_kucun_count;
	private TextView tv_counts_deliv;
	private TextView tv_deliv_stutas;
	private EditText et_deliv_remark;
	private EditText et_cargador_man;
	private EditText et_lenth;
	private EditText et_width;
	private EditText et_height;
	private EditText et_cbm;
	private EditText et_kgs;
	//private Spinner sp_deliv_man;
	private EditText sp_deliv_man;
	private GridView gv;
	private File cameraTempFile;
	private ImageAdapter imageAdpter;
	private List<ImageData> listImage;
	private List<ImageData> saveImageList; //需要保存的图片list
	//private List<ImageData> deleteImageList; //需要保存的图片list
	private TextView deliv_maxNum;     
	private TextView tv_deliv_maxNum;    //可放货件数
	private EditText edit_DelivNum;      //司机自分货时，实际放货件数。
	private List<SelectDict> men = new ArrayList<SelectDict>(); // 装卸工
	private String selTruckId;
	
	//private SelectDict depotMan = new SelectDict();
	private String deliv_id;
	//private Button btn_print_pilecard;
	private static final int REQUEST_CODE_IMAGE = 1; // 拍照
	private String flag;    // 司机自分货标志
	private boolean savedFlag;
	private int remainDevNum = 0;   //剩余的可放件数
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rc002);		
		setupActionBar();
		Intent intent=getIntent(); 
		flag = intent.getStringExtra("flag");
		remainDevNum = intent.getIntExtra("remainDevNum", 0);
		pileCard = (RC001PileCard) intent.getSerializableExtra("pilecard");
		deliver_batch_list = (List<RC001Deliver_batch>) intent.getSerializableExtra("deliver_batch_list");
		deliv_id = intent.getStringExtra("deliv_id");
		savedFlag = intent.getBooleanExtra("savedFlag", false);
		
		//btn_print_pilecard = (Button) findViewById(R.id.btn_print_pilecard);
		tv_cd_order_public = (TextView) findViewById(R.id.tv_cd_order_public);
		tv_co_loader = (TextView) findViewById(R.id.tv_co_loader);
		tv_no_batch = (TextView) findViewById(R.id.tv_no_batch);		
		tv_no_pilecard = (TextView) findViewById(R.id.tv_no_pilecard);
		tv_kucun_count = (TextView) findViewById(R.id.tv_kucun_count);
		tv_counts_deliv = (TextView) findViewById(R.id.tv_counts_deliv);
		tv_deliv_stutas = (TextView) findViewById(R.id.tv_deliv_stutas);
		deliv_maxNum = (TextView) findViewById(R.id.deliv_maxNum);
		tv_deliv_maxNum = (TextView) findViewById(R.id.tv_deliv_maxNum);
		
		gv = (GridView) findViewById(R.id.gv_rc002);
		
		File dir = new File(Environment.getExternalStorageDirectory(),
				"amass/pics/rc002");
		Utils.deleteFileAndPath(dir);
		//btn_print_pilecard.setEnabled(false);				
		listImage = new ArrayList<ImageData>();
		saveImageList = new ArrayList<ImageData>();	
		//deleteImageList = new ArrayList<ImageData>();	
		imageAdpter = new ImageAdapter(this, 0);
		gv.setAdapter(imageAdpter);		
		
		if(pileCard != null) {
			initNeck();
		}	
		
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
							"amass/pics/rc002");
					//dir.mkdirs();
					cameraTempFile = new File(dir, UUID.randomUUID() + ".jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(cameraTempFile));
					startActivityForResult(intent, REQUEST_CODE_IMAGE);
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
								/*ImageData imageData = (ImageData) data.getData();
								if(imageData.getImageId()!=null){
									deleteImageList.add(imageData);
								}*/
								ImageData imageData = (ImageData) data.getData();
								saveImageList.remove(imageData);
								imageAdpter.remove(data);
								imageAdpter.notifyDataSetChanged();
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
	
	private void initNeck() {
		// 设置数据
		tv_cd_order_public.setText(pileCard.getCd_order_public());
		tv_co_loader.setText(pileCard.getCo_loader());
		tv_no_batch.setText(pileCard.getNo_batch());
		tv_no_pilecard.setText(pileCard.getNo_pilecard());
		tv_kucun_count.setText(String.valueOf(pileCard.getKucunCount()));
		tv_counts_deliv.setText(pileCard.getCounts_deliv()+"");
		
		if(pileCard.getFlg_deliv() == 1){
			tv_deliv_stutas.setText(R.string.RC001_delivStatus_done);
			tv_deliv_stutas.setTextColor(Color.BLUE);
			//可放件数为库存件数与剩余可放件数中的最小值
			if((remainDevNum + pileCard.getCounts_deliv()) < Integer.parseInt(pileCard.getKucunCount())){
			    tv_deliv_maxNum.setText(String.valueOf(remainDevNum + pileCard.getCounts_deliv()));
			}else{
				tv_deliv_maxNum.setText(pileCard.getKucunCount());
			}
		}else{
		    tv_deliv_stutas.setText(R.string.RC001_delivStatus_undo);
		    //可放件数为库存件数与剩余可放件数中的最小值
			if(remainDevNum < Integer.parseInt(pileCard.getKucunCount())){
			    tv_deliv_maxNum.setText(String.valueOf(remainDevNum));
			}else{
				tv_deliv_maxNum.setText(pileCard.getKucunCount());
			}
		    
		}
		et_deliv_remark = (EditText) findViewById(R.id.et_deliv_remark);
		et_cargador_man = (EditText) findViewById(R.id.et_cargador_man);
		et_lenth = (EditText) findViewById(R.id.edit_Lenth);
		et_width = (EditText) findViewById(R.id.edit_Wide);
		et_height = (EditText) findViewById(R.id.edit_High);
		et_cbm = (EditText) findViewById(R.id.edit_Volume);
		et_kgs = (EditText) findViewById(R.id.edit_Weight);
		edit_DelivNum = (EditText) findViewById(R.id.edit_DelivNum);
		et_lenth.setSelectAllOnFocus(true);
		et_width.setSelectAllOnFocus(true);
		et_height.setSelectAllOnFocus(true);
		et_cbm.setSelectAllOnFocus(true);
		et_kgs.setSelectAllOnFocus(true);
		edit_DelivNum.setSelectAllOnFocus(true);
		
		et_lenth.setText(String.valueOf(pileCard.getLength()));
		et_width.setText(String.valueOf(pileCard.getWidth()));
		et_height.setText(String.valueOf(pileCard.getHeight()));
		et_cbm.setText(String.valueOf(pileCard.getCbm()));
		et_kgs.setText(String.valueOf(pileCard.getKgs()));
		edit_DelivNum.setText(String.valueOf(pileCard.getCounts_deliv()));
		
		et_cargador_man.requestFocus();
		//sp_deliv_man = (Spinner) findViewById(R.id.sp_deliv_man);
		//sp_deliv_man.setOnItemSelectedListener(this);
		sp_deliv_man = (EditText) findViewById(R.id.sp_deliv_man);
		sp_deliv_man.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				initTruck(view);			
			}
		});
		if(!"1".equals(flag)){
			//deliv_maxNum.setVisibility(View.GONE);
			//tv_deliv_maxNum.setVisibility(View.GONE);
			edit_DelivNum.setEnabled(false);
			//全放时，长、宽、高、体积、重量不可编辑
			if(pileCard.getKucunCount().equals(String.valueOf(pileCard.getCounts_deliv()))){
				et_lenth.setEnabled(false);
				et_width.setEnabled(false);
				et_height.setEnabled(false);
				et_cbm.setEnabled(false);
				et_kgs.setEnabled(false);
			}
		}
		// 监听事件 计算体积
		DoFocusChangeListener(et_lenth);
		DoFocusChangeListener(et_width);
		DoFocusChangeListener(et_height);
		DoFocusChangeListener(edit_DelivNum);
		
		// 限制EditText输入整数、小数位数
		CheckDigits(et_lenth, 7, 2); // 长
		CheckDigits(et_width, 7, 2); // 宽
		CheckDigits(et_height, 7, 2); // 高

		CheckDigits(et_kgs, 15, 3); // 重量
		CheckDigits(et_cbm, 15, 4); // 体积
		
		initSpinner();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			//isFirst = true;
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_CODE_IMAGE: // 拍照
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
				imageData.setImageDesc("新拍\n照片");
				imageData.setUrl(displyPath);
				listImage.add(imageData);
				saveImageList.add(imageData);

				MoreData item = new MoreData(imageData);
				//imageAdpter.insert(item, imageAdpter.getCount() - 1);
				imageAdpter.insert(item , 1);
				imageAdpter.notifyDataSetChanged();
			}
		}
	}
		
	//返回
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//刷新数据
			DataRefresh();
			//按下的如果是BACK，同时没有重复
	         Intent intent=new Intent();   
	         intent.setClass(RC002Activity.this, RC001Activity.class);   
		     intent.putExtra("deliv_id", deliv_id);
		     intent.putExtra("deliver_batch_list", (Serializable) deliver_batch_list);
	         startActivity(intent);  
	         //isFirst = true;
	         RC002Activity.this.finish();   	         
			 //return true;
		}
		return super.onKeyDown(keyCode, event);
	}	
	
	/**
	 * 初始化list数据和照片
	 */
	private void initSpinner() {
		if(pileCard.getCd_order_public()!=null){
		    Map<String, Object> paraMap = new HashMap<String, Object>();
		    paraMap.put("cdOrder", pileCard.getCd_order_public());
		    NetworkHelper.getInstance().postJsonData(_thisActivity,"getRC002ListData",paraMap, RC002ListData.class
		    		, new AmassHttpResponseHandler<RC002ListData>() {
		    			@Override
		    			protected void OnSuccess(RC002ListData response) {
		    				super.OnSuccess(response);
		    				RC002ListData listData = (RC002ListData) response;
		    				if(listData != null){		    					
		    					//装卸工下拉列表初始
		    					men = listData.getDepotMan();
		    					/*String[] strMen = new String[men.size()+1];
		    					strMen[0] = "";
		    					for(int i = 0 ; i < men.size(); i++) {
		    						strMen[i+1] = men.get(i).getName();
		    					}
		    					ArrayAdapter<String> depotManAdapter = new ArrayAdapter<String>(RC002Activity.this,android.R.layout.simple_spinner_item, strMen);
		    					depotManAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    					sp_deliv_man.setAdapter(depotManAdapter);*/
		    					//不为空时
		    					if(pileCard.getWorkerId() != null && pileCard.getCargadorId() != null){
		    						et_cargador_man.setText(pileCard.getWorkerId());
		    						//setSpinnerItemSelectedByValue(sp_deliv_man,pileCard.getCargadorId(),men);
		    						for(int n=0;n<men.size();n++){
		    							if(pileCard.getCargadorId().equals(men.get(n).getId())){
		    								selTruckId = pileCard.getCargadorId();
		    								sp_deliv_man.setText(men.get(n).getName());
		    							}
		    						}
	    						
		    					}else{
		    						//设置默认的装卸工和搬运工
		    						for(int n = 0; n < deliver_batch_list.size(); n++){
		    							for(int m = 0; m < deliver_batch_list.get(n).getPilecards().size(); m++){
		    								if(deliver_batch_list.get(n).getPilecards().get(m).getCargadorId() != null
		    										&& deliver_batch_list.get(n).getPilecards().get(m).getWorkerId() != null){
		    									//setSpinnerItemSelectedByValue(sp_deliv_man,
		    									//		deliver_batch_list.get(n).getPilecards().get(m).getCargadorId(),men);
		    									for(int i=0;i<men.size();i++){
		    		    							if(deliver_batch_list.get(n).getPilecards().get(m).getCargadorId()
		    		    									.equals(men.get(i).getId())){
		    		    								selTruckId = deliver_batch_list.get(n).getPilecards().get(m).getCargadorId();
		    		    								sp_deliv_man.setText(men.get(i).getName());
		    		    							}
		    		    						}
		    									et_cargador_man.setText(deliver_batch_list.get(n)
		    											.getPilecards().get(m).getWorkerId());
		    									break;
		    								}
		    							}
		    						}
		    					}

		    					imageAdpter.clear();
		    					imageAdpter.add(new MoreData());
		    					// 循环加载进仓照片
		    					for (FileManageData file : listData.getFileList()) {
		    						ImageData data = new ImageData();
		    						data.setImageId(file.getFileId());
		    						data.setImageDesc(file.getFileName());
		    						listImage.add(data);
		    						MoreData d = new MoreData(data);
		    						imageAdpter.insert(d,1);
		    					}
		    					imageAdpter.notifyDataSetChanged();
		    				}
		    			}
		    		}, true);
		}
	}
	
	/**
	 * 检测输入的情况
	 */
	public Boolean checkEmpty() {
		// 页面项目进行检查，XXX必须输入
		//if (depotMan.getId() == null || depotMan.getId().equals("")) {
		if(sp_deliv_man.getText().toString().trim().length() == 0){
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN004_028_MSG)
							+ getString(R.string.DN004_001_MSG));
			return false;
		}
		
		if(TextUtils.isEmpty(et_cargador_man.getText())) {
			Utils.showAlertDialog(this, getResources().getString(R.string.RC002_error_7));
			et_cargador_man.requestFocus();
			return false;
		}
		
		if(et_cargador_man.getText().toString().length() != 3) {
			Utils.showAlertDialog(this, getResources().getString(R.string.RC002_error_9));
			et_cargador_man.requestFocus();
			return false;
		}
		
		//放货件数为空
		if(TextUtils.isEmpty(edit_DelivNum.getText())) {
			Utils.showAlertDialog(this, getResources().getString(R.string.RC002_error_6));
			edit_DelivNum.requestFocus();
			return false;
		}	
		
		//放货件数大于可放件数
		if(Integer.parseInt(edit_DelivNum.getText().toString()) 
				> Integer.parseInt(tv_deliv_maxNum.getText().toString())) {
			Utils.showAlertDialog(this, getResources().getString(R.string.RC002_error_8));
			edit_DelivNum.requestFocus();
			return false;
		}	
		
		// 长
		if (et_lenth.getText().toString().trim().length() == 0) {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN004_014_MSG)
					+ getString(R.string.DN004_001_MSG));
			et_lenth.requestFocus();
			return false;
		}

		if (ReturnFloat(et_lenth.getText().toString().trim()) == 0) {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN004_014_MSG)
					+ getString(R.string.DN004_025_MSG));
			et_lenth.requestFocus();
			return false;
		}

		// 宽
		if (et_width.getText().toString().trim().length() == 0) {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN004_015_MSG)
					+ getString(R.string.DN004_001_MSG));
			et_width.requestFocus();
			return false;
		}

		if (ReturnFloat(et_width.getText().toString().trim()) == 0) {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN004_015_MSG)
					+ getString(R.string.DN004_025_MSG));
			et_width.requestFocus();
			return false;
		}

		// 高
		if (et_height.getText().toString().trim().length() == 0) {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN004_016_MSG)
					+ getString(R.string.DN004_001_MSG));
			et_height.requestFocus();
			return false;
		}

		if (ReturnFloat(et_height.getText().toString()) == 0) {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN004_016_MSG)
					+ getString(R.string.DN004_025_MSG));
			et_height.requestFocus();
			return false;
		}


		// 体积
		if (et_cbm.getText().toString().trim().length() == 0) {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN004_019_MSG)
					+ getString(R.string.DN004_001_MSG));
			et_cbm.requestFocus();
			return false;
		}

		if (ReturnFloat(et_cbm.getText().toString().trim()) == 0) {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN004_019_MSG)
					+ getString(R.string.DN004_025_MSG));
			et_cbm.requestFocus();
			return false;
		}
		
		if(imageAdpter.getCount() <= 1){			
			Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC002_error_noPic);
			return false;
		}		
						
		return true;
		
	}
	
	/*//保存操作
	public void do_save(){
		Map<String, Object> requestData = new HashMap<String, Object>();
		requestData.put("LoadWorkers", LoadWorkers);
		tDepotDt.setDepotDtId(pileCard.getDepot_dt_id());
		tDepotDt.setDelivNum(Integer.parseInt(edit_DelivNum.getText().toString()));
		tDepotDt.setMarks(et_deliv_remark.getText().toString());
		tDepotDt.setNoLen(ReturnFloat(et_lenth.getText().toString().trim()));
		tDepotDt.setNoWidth(ReturnFloat(et_width.getText().toString().trim()));
		tDepotDt.setNoHeight(ReturnFloat(et_height.getText().toString().trim()));
		tDepotDt.setCbm(ReturnFloat(et_cbm.getText().toString().trim()));
		tDepotDt.setKgs(ReturnFloat(et_kgs.getText().toString().trim()));
		requestData.put("tDepotDt", tDepotDt);
		requestData.put("worker_id", et_cargador_man.getText().toString()); // 搬运工编号
		requestData.put("cargador_id", depotMan.getId()); // 铲车工编号
		
		// 核销放货件数
		requestData.put("deliv_num", Integer.parseInt(edit_DelivNum.getText().toString())); // 放货件数			
		// 更新三检熏蒸
		requestData.put("deliv_id", deliv_id); // 放货编号		
		// 插入履历表
		//requestData.put("isFirst", isFirst);
		
		RC001TDelivDetail tDelivDetail = new RC001TDelivDetail();
		tDelivDetail.setDeliv_id(deliv_id);
		tDelivDetail.setDepotDtId(pileCard.getDepot_dt_id());
		tDelivDetail.setDepotId(pileCard.getDepot_id());
		tDelivDetail.setCdOrder(pileCard.getCd_order());
		tDelivDetail.setCdOrderPublic(pileCard.getCd_order_public());
		tDelivDetail.setColoader(pileCard.getCo_loader());
		tDelivDetail.setDepotDtId(pileCard.getDepot_dt_id());
		tDelivDetail.setNoBatch(Integer.parseInt(pileCard.getNo_batch()));
		tDelivDetail.setDelivnum(Integer.parseInt(edit_DelivNum.getText().toString()));
		tDelivDetail.setDtDeliv(new Date());
		tDelivDetail.setNmDelivman(SessionHelper.getInstance().getUser().getUserName());
		
		requestData.put("tDelivDt", tDelivDetail);			
		
		NetworkHelper.getInstance().postJsonData(this,"RC002_update_insert", requestData, RC002TLoadWorkers.class,
				new AmassHttpResponseHandler<RC002TLoadWorkers>() {
			@Override
			protected void OnSuccess(RC002TLoadWorkers response) {
				super.OnSuccess(response);
				RC002TLoadWorkers workers = (RC002TLoadWorkers) response;
				//保存成功
				if(workers != null){
					if(workers.getFlag()){
						//更新库存件数显示
						tv_kucun_count.setText(String.valueOf(Integer.parseInt(pileCard.getKucunCount()) 
								- Integer.parseInt(edit_DelivNum.getText().toString())));
						//更新放货件数显示
						tv_counts_deliv.setText(edit_DelivNum.getText().toString());
						//设置件数不可编辑
						//edit_DelivNum.setEnabled(false);
						//计算剩余可放件数
						remainDevNum = remainDevNum + pileCard.getCounts_deliv()
								- Integer.parseInt(edit_DelivNum.getText().toString());
						pileCard.setCounts_deliv(Integer.parseInt(edit_DelivNum.getText().toString()));
						pileCard.setFlg_deliv(1);
						tv_deliv_stutas.setText(R.string.RC001_delivStatus_done);
						tv_deliv_stutas.setTextColor(Color.BLUE);
						LoadWorkers = workers;					
						//isFirst = false;
						if(Integer.parseInt(pileCard.getKucunCount()) 
								!= Integer.parseInt(edit_DelivNum.getText().toString())){
						    btn_print_pilecard.setEnabled(true); 
						} 
						// 保存照片
						SavePicture();
					}else{
						if("001".equals(workers.getErrorCd())){
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC001_msg_delivErr2);
						}else if("005".equals(workers.getErrorCd())){
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.RC001_msg_delivErr3);
						}
						else{
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
						}
					}
				}
				else{
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP014_error_saveFailed);
				}			
			}
		}, false);
	}*/
	
	public void RC002Dialog(String warning){
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light));
		builder.setIcon(R.drawable.ic_launcher);
		builder.setCancelable(false);
		builder.setMessage(warning);
		builder.setTitle(R.string.app_name);
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//确认保存
				//do_save();
				SavePicture();
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
	
	/**
	 * 保存的点击事件
	 * @param view
	 */
	public void RC002_save(View view) {
		if (et_lenth.hasFocus()) {
			et_lenth.clearFocus();
		}
		else if (et_width.hasFocus()) {
			et_width.clearFocus();
		}
		else if (et_height.hasFocus()) {
			et_height.clearFocus();
		}		
		else if (edit_DelivNum.hasFocus()) {
			edit_DelivNum.clearFocus();
		}
		
		Boolean result= checkEmpty();
		//检查是否超长、超宽、超高
		if(result){
			String msg = getString(R.string.DN004_004_MSG);
			boolean flag = false;
			if (ReturnFloat(et_lenth.getText().toString().trim()) > 1190) {
				msg += getString(R.string.DN004_007_MSG);
				flag = true;
			}
			if (ReturnFloat(et_width.getText().toString().trim()) > 230) {
				if (flag) {
					msg += getString(R.string.DN004_023_MSG);
				}
				msg += getString(R.string.DN004_008_MSG);
				flag = true;
			}
			if (ReturnFloat(et_height.getText().toString().trim()) > 250) {
				if (flag) {
					msg += getString(R.string.DN004_023_MSG);
				}
				msg += getString(R.string.DN004_022_MSG);
				flag = true;
			}
			if (flag) {
				ShowDialogSave(msg + getString(R.string.DN004_009_MSG));
			} else {
				//do_save();
				SavePicture();
			}					
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> view, View v, int position,
			long arg3) {
		switch (view.getId()) {
		case R.id.sp_deliv_man:
			/*if(position > 0){
			    depotMan = men.get(position - 1);
			}else{
				depotMan.setId("");
				depotMan.setName("");
			}*/
			break;
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	/*
	 * 保存照片
	 */
	private void SavePicture() {
		try {
			tv_counts_deliv.setText(edit_DelivNum.getText().toString());
			//计算剩余可放件数
			remainDevNum = remainDevNum + pileCard.getCounts_deliv()
					- Integer.parseInt(edit_DelivNum.getText().toString());
			pileCard.setCounts_deliv(Integer.parseInt(edit_DelivNum.getText().toString()));			
			pileCard.setLength(ReturnFloat(et_lenth.getText().toString().trim()));
			pileCard.setWidth(ReturnFloat(et_width.getText().toString().trim()));
			pileCard.setHeight(ReturnFloat(et_height.getText().toString().trim()));
			pileCard.setCbm(ReturnFloat(et_cbm.getText().toString().trim()));
			pileCard.setKgs(ReturnFloat(et_kgs.getText().toString().trim()));
			pileCard.setMarks(et_deliv_remark.getText().toString());
			//pileCard.setCargadorId(depotMan.getId());
			pileCard.setCargadorId(selTruckId);
			pileCard.setWorkerId(et_cargador_man.getText().toString());
			if(Integer.parseInt(edit_DelivNum.getText().toString()) > 0){
				pileCard.setFlg_deliv(1);
				tv_deliv_stutas.setText(R.string.RC001_delivStatus_done);
				tv_deliv_stutas.setTextColor(Color.BLUE);
			}else{
				pileCard.setFlg_deliv(0);
				tv_deliv_stutas.setText(R.string.RC001_delivStatus_undo);
				tv_deliv_stutas.setTextColor(Color.BLACK);
			}
			
			int n = 0;
			int nCount = imageAdpter.getCount();
			for (int nIndex = 0; nIndex < nCount; nIndex++) {
				MoreData data = imageAdpter.getItem(nIndex);
				if (data.getDataType() == MoreData.DATA_TYPE) {
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getImageId() == null) {
						File f = new File(imageData.getPath());
						imageData.setFileUploadId(f.getName());
					}
				}
			}
			//if (saveImageList.size() > 0 || deleteImageList.size() > 0 ) {
				RequestParams params = new RequestParams();
				params.put("cdOrder", pileCard.getCd_order_public());
				//照片名序列，用于排序照片
				String imgOrderList = "";
				if(saveImageList.size() > 0){
					for (ImageData imageData : saveImageList) {
						try {
							File f = new File(imageData.getPath());
							params.put("ADD"+String.format("file%d.jpg", n), f);
							imgOrderList = imgOrderList + f.getName()+",";
							n++;
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
					/*}
				if(deleteImageList.size() > 0){
					for (ImageData imageData : deleteImageList) {
						try {
							params.put("DEL"+String.format("file%d.jpg", n), new File(
									imageData.getPath()));
							n++;
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}*/
				params.put("imgOrderList", imgOrderList);
				
				NetworkHelper.getInstance().postFilesData(this,
						"RC002_UploadFiles", "RC002Photo", params, new TypeToken<HashMap<String, String>>() {
						}.getType(),
						new AmassHttpResponseHandler<HashMap<String, String>>(){
					protected void OnSuccess(HashMap<String, String> response) {
						super.OnSuccess(response);
						HashMap<String, String> result = (HashMap<String, String>) response;
						if(result != null){
							//处理成功,清空需要保存的图片list
							saveImageList.clear();
							//deleteImageList.clear();
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
							//gv.setAdapter(imageAdpter);
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.msg_common_success);
						}else{
							Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP014_error_saveFailed);
						}
						
					}
				}, true);
			}else{
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.msg_common_success);
			}
			
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
/*	public void RC002_Print (View arg0) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotDtId", pileCard.getDepot_dt_id() == null ? "" : pileCard.getDepot_dt_id());
		p1.put("cdOrder", pileCard.getCd_order() == null ? "" : pileCard.getCd_order());
		p1.put("cdOrderPublic", pileCard.getCd_order_public() == null ? "" : pileCard.getCd_order_public());
		p1.put("coLoader", pileCard.getCo_loader() == null ? "" : pileCard.getCo_loader());

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN004_PrintPileCardReport", p1,
				new TypeToken<DN004TDepotDt>() {
		}.getType(), new AmassHttpResponseHandler<DN004TDepotDt>() {

			@Override
			protected void OnSuccess(DN004TDepotDt response) {
				super.OnSuccess(response);
				Utils.showAlertDialogIntMsg(_thisActivity, R.string.msg_common_print_success);
			}
		}, true);
	}*/
	
	/*
	 * 创建右上角按钮
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.selprintcommon, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {

	}
	
	/*
	 * 文本框验证
	 */
	private Double ReturnFloat(String str) {
		if (str.trim().length() > 0) {
			return Double.parseDouble(str.trim());
		} else {
			return Double.parseDouble("0");
		}
	}
	
	/** 点击空白隐藏软键盘 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();

			if (isShouldHideInput(v, ev)) {
				hideSoftInput(v.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/*
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/*
	 * 多种隐藏软件盘方法的其中一种
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/*
	 * 限制EditText输入整数、小数位数
	 */
	private void CheckDigits(final EditText edit, final int zlen, final int xlen) {
		edit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// s:变化后的所有字符
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// s:变化前的所有字符； start:字符开始的位置； count:变化前的总字节数；after:变化后的字节数
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				try {
					// S：变化后的所有字符；start：字符起始的位置；before: 变化之前的总字节数；count:变化后的字节数
					// 判断第一位是否为小数点
					if (start == 0 && s.toString().equals(".")) {
						edit.setText("");
					} else if (s.toString().contains(".")) {
						if (getOccur(s.toString(), ".") > 1) {
							edit.setText(s.toString().substring(0, start));
						}

						// 判断小数位数
						if (start - xlen > 1) {
							if (s.toString()
									.substring(start - xlen - 1, start - xlen)
									.equals(".")) {
								edit.setText(s.toString().substring(0, start));
							}
						}
					} else {
						// 没有小数点时判断位数
						if (s.toString().length() > zlen) {
							edit.setText(s.toString().substring(0, zlen));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * 获取某个字符在字符串中的个数
	 */
	private int getOccur(String src, String find) {
		int o = 0;
		int index = -1;
		while ((index = src.indexOf(find, index)) > -1) {
			++index;
			++o;
		}
		return o;
	}

	/*
	 * 计算体积
	 */
	private void DoFocusChangeListener(EditText et) {
		et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean flag) {
				try {
					if (!flag) {
						//ArgueNumber();
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * 体积
	 */
	private void ArgueNumber() {
		if (et_lenth.isEnabled() && et_width.isEnabled() && et_height.isEnabled()){
			String strLong = "0";
			String strWide = "0";
			String strHigh = "0";
			String strNum = "0";
	
			if (et_lenth.getText().toString().trim().length() > 0) {
				strLong = et_lenth.getText().toString().trim();
			}
			if (et_width.getText().toString().trim().length() > 0) {
				strWide = et_width.getText().toString().trim();
			}
			if (et_height.getText().toString().trim().length() > 0) {
				strHigh = et_height.getText().toString().trim();
			}
			/*if (tv_kucun_count.getText().toString().trim().length() > 0) {
				strNum = tv_kucun_count.getText().toString().trim();
			}*/		
			if (edit_DelivNum.getText().toString().trim().length() > 0) {
				//检查放货件数是否超过最大可放件数
				if(Integer.parseInt(edit_DelivNum.getText().toString().trim())
						<= Integer.parseInt(tv_deliv_maxNum.getText().toString().trim())){
					if(Integer.parseInt(pileCard.getKucunCount()) 
							> Integer.parseInt(edit_DelivNum.getText().toString().trim())){
						strNum = String.valueOf((Integer.parseInt(pileCard.getKucunCount())
								- Integer.parseInt(edit_DelivNum.getText().toString().trim())));
					}
					else{
						//全放时，体积不清零，保存原进仓体积
						strNum = String.valueOf(Integer.parseInt(pileCard.getKucunCount()));
					}
				}
				
				/*else{
					Utils.showAlertDialog(this, getResources().getString(R.string.RC002_error_8));
					return;
				}*/
				
			}
	
			
			double flong = Double.parseDouble(strLong) / 100;
			double fwide = Double.parseDouble(strWide) / 100;
			double fhigh = Double.parseDouble(strHigh) / 100;
			double fnum = Double.parseDouble(strNum);
	
			double f = flong * fwide * fhigh * fnum;
			
			et_cbm.setText(ChangeDouble(f, 4, 4).replace(",", ""));
		}
	}

	/*
	 * 小数转换
	 */
	private String ChangeDouble(double d, int scale, int dLen) {
		BigDecimal b = new BigDecimal(d);
		double f = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(dLen);
		return nf.format(f);
	}
	
	/*
	 * 显示超长、超宽，超高确认框
	 */
	private void ShowDialogSave(String msg) {
		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(msg)
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								//确认保存
								//do_save();
								SavePicture();
							}
						})
				.setNegativeButton(getString(R.string.button_no), null).show();
	}
	
	//更新放货单中此票的放货信息
	public void DataRefresh(){
		for(int i = 0 ; i < deliver_batch_list.size(); i++ ){
			if(pileCard.getCd_order_public().equals(deliver_batch_list.get(i).getCd_order_public())
					&& pileCard.getDepot_id().equals(deliver_batch_list.get(i).getDepot_id())
					&& pileCard.getNo_batch().equals(deliver_batch_list.get(i).getNo_batch())){
				for(int j = 0; j < deliver_batch_list.get(i).getPilecards().size(); j++ ){
					if(pileCard.getDepot_dt_id().equals(
							deliver_batch_list.get(i).getPilecards().get(j).getDepot_dt_id())){		
						deliver_batch_list.get(i).getPilecards().get(j).setCounts_deliv(pileCard.getCounts_deliv());//更新放货件数
						deliver_batch_list.get(i).getPilecards().get(j).setFlg_deliv(pileCard.getFlg_deliv());//更新放货状态
						deliver_batch_list.get(i).getPilecards().get(j).setCargadorId(pileCard.getCargadorId());//更新装卸工
						deliver_batch_list.get(i).getPilecards().get(j).setWorkerId(pileCard.getWorkerId());//更新搬运工
						deliver_batch_list.get(i).getPilecards().get(j).setLength(pileCard.getLength());
						deliver_batch_list.get(i).getPilecards().get(j).setWidth(pileCard.getWidth());
						deliver_batch_list.get(i).getPilecards().get(j).setHeight(pileCard.getHeight());
						deliver_batch_list.get(i).getPilecards().get(j).setCbm(pileCard.getCbm());
						deliver_batch_list.get(i).getPilecards().get(j).setKgs(pileCard.getKgs());
						deliver_batch_list.get(i).getPilecards().get(j).setMarks(pileCard.getMarks());
					}
				}
			}
		}
	}
	
	/**
	 * 根据值, 设置spinner默认选中:
	 * @param spinner
	 * @param value
	 */
	public static void setSpinnerItemSelectedByValue(Spinner spinner,String value,List<SelectDict> men){
		for(int i = 0;i < men.size(); i++){	    
			if(value.equals(men.get(i).getId())){
	            spinner.setSelection(i+1,true);// 默认选中项
	            break;
	        }
	    }
	} 
	
	//更新装卸工列表
	public void initTruck(final View view){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("cdOrder", pileCard.getCd_order_public());
		NetworkHelper.getInstance().postJsonData(_thisActivity,"getRC002ListData",paraMap, RC002ListData.class
				, new AmassHttpResponseHandler<RC002ListData>() {
			@Override
			protected void OnSuccess(RC002ListData response) {
				super.OnSuccess(response);
				RC002ListData listData = (RC002ListData) response;

				// 绑定铲车工下拉框
				men = listData.getDepotMan();

				final String[] truckId = new String[men.size()];
				final String[] truckName = new String[men.size()];
				for (int j = 0; j < men.size(); j++) {
					truckId[j] = ((SelectDict) men.get(j)).getId();
					truckName[j] = ((SelectDict) men.get(j)).getName();
				}
				int i = -1; // 初期选中项
				// 循环获取选择项
				if (selTruckId != null && selTruckId.length() > 0) {
					for (i = 0; i < men.size(); i++) {
						if (((SelectDict) men.get(i)).getId()
								.equals(selTruckId)) {
							break;
						}
					}
				}

				new AlertDialog.Builder(new ContextThemeWrapper(_thisActivity,
						android.R.style.Theme_Holo_Light))
				.setTitle(R.string.DN004_TRUCK_SELECT)
				.setSingleChoiceItems(truckName, i,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						sp_deliv_man.setText(truckName[which]);
						selTruckId = truckId[which];
						dialog.dismiss();
					}
				}).show();
			}
		}, false);
	}
}
