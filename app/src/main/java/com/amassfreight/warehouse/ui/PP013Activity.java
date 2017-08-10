package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode04;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DepotPP013;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.domain.PP013Data;
import com.amassfreight.domain.SelectDict;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.widget.GalleryAct;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class PP013Activity extends BaseActivity {

	private String cdContainer = ""; // 箱号
	private TextView txtBoxNo; // 集箱号
	private TextView txtContainer;// 箱号
	private TextView txtBoxModel;// 箱型箱量
	private TextView txtBoxStatus;//集装箱状态
	private CheckBox chkBoxOkFlg; // 验箱正常标志
	private CheckBox chkBoxPollutionFlg; // 验箱污染标志
	private CheckBox chkBoxDamagedFlg; // 验箱破损标志
	private CheckBox chkBoxDeformFlg; // 验箱变形标志
	private EditText txtRemark; // 验箱备注
	private EditText txtQtVgmKgsLeather;	// 箱皮重
	
	// add by yxq 2014/10/24 begin
	private Spinner spPlace;   			  // 位置    
	private List<SelectDict> placeList;   // 位置列表 
	// add by yxq 2014/10/24 end

	private GridView gv;
	private File cameraTempFile;
	private ImageAdapter imageAdpter;
	// private List<String> listImageId_Del; // 删除的照片

	private Button btn_SAVE;
	private boolean actionFlag = false;
    // add by yxq 2014/10/28 begin
	private String mailSendFlg = "0";  // 邮件是否发过 0:未发过  1：已发
	// add by yxq 2014/10/28 end
	private PP013Data data = new PP013Data();
	
	private Boolean flgSavePic = false;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp013);
		setupActionBar();

		initializeView();
	}

	/*
	 * 初期加载
	 */
	private void initializeView() {
		// 删除文件夹
		File dir = new File(Environment.getExternalStorageDirectory(),
				"amass/pics/pp013");
		Utils.deleteFileAndPath(dir);

		txtBoxNo = (TextView) findViewById(R.id.txtBoxNo); // 集箱号
		txtContainer = (TextView) findViewById(R.id.txtContainer); // 箱号
		txtBoxModel = (TextView) findViewById(R.id.txtBoxModel); // 箱型箱量
		txtBoxStatus = (TextView)findViewById(R.id.txtBoxStatus); // 集装箱状态
		chkBoxOkFlg = (CheckBox) findViewById(R.id.chk_BoxOkFlg); // 验箱正常标志
		chkBoxOkFlg.setEnabled(false);
		chkBoxPollutionFlg = (CheckBox) findViewById(R.id.chk_BoxPollutionFlg); // 验箱污染标志
		chkBoxPollutionFlg.setEnabled(false);
		chkBoxDamagedFlg = (CheckBox) findViewById(R.id.chk_BoxDamagedFlg); // 验箱破损标志
		chkBoxDamagedFlg.setEnabled(false);
		chkBoxDeformFlg = (CheckBox) findViewById(R.id.chk_BoxDeformFlg); // 验箱变形标志
		chkBoxDeformFlg.setEnabled(false);
		txtRemark = (EditText) findViewById(R.id.txtRemark); // 验箱备注
		txtQtVgmKgsLeather = (EditText) findViewById(R.id.txtQtVgmKgsLeather); 	 // 箱皮重
		txtRemark.setEnabled(false);
		txtQtVgmKgsLeather.setEnabled(false);
		txtQtVgmKgsLeather.setSelectAllOnFocus(true);
		
		// add by yxq 2014/10/24 begin
		spPlace = (Spinner)findViewById(R.id.sp_Place);      // 位置
		spPlace.setEnabled(false);
		GetSelectList("CONTAINER_PLACE");
		// add by yxq 2014/10/24 end

		btn_SAVE = (Button) findViewById(R.id.btn_SAVE); // 保存按钮
		btn_SAVE.setEnabled(false);

		gv = (GridView) findViewById(R.id.gridView_image);
		imageAdpter = new ImageAdapter(this, 0);
		imageAdpter.add(new MoreData());
		gv.setAdapter(imageAdpter);
		// listImageId_Del = new ArrayList<String>();

		// 监听checkbox改变事件
		chkBoxOkFlg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chkBoxPollutionFlg.setChecked(false);
					chkBoxDamagedFlg.setChecked(false);
					chkBoxDeformFlg.setChecked(false);
				}
			}
		});

		chkBoxPollutionFlg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chkBoxOkFlg.setChecked(false);
				}
			}
		});

		chkBoxDamagedFlg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chkBoxOkFlg.setChecked(false);
				}
			}
		});

		chkBoxDeformFlg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chkBoxOkFlg.setChecked(false);
				}
			}
		});

		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE) { // 添加
					if (actionFlag) {
						Intent intent = new Intent();
						// 指定开启系统相机的Action
						intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						File dir = new File(Environment
								.getExternalStorageDirectory(),
								"amass/pics/pp013");
						// dir.mkdirs();
						cameraTempFile = new File(dir, UUID.randomUUID()
								+ ".jpg");
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(cameraTempFile));
						startActivityForResult(intent, 1);
					}
				} else { // 放大
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getData() != null) {
						GalleryAct.start(_thisActivity, imageData.getUrl());
					}
				}
			}
		});

		gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int pos, long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.DATA_TYPE) {
					final int imagePos = pos;
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getImageId() != null) {
						return true;
					}
					DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								MoreData data = (MoreData) imageAdpter
										.getItem(imagePos);

								imageAdpter.remove(data);
								// if (imageData.getImageId() != null) {
								// listImageId_Del.add(imageData.getImageId());
								// }
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

	/*
	 * 创建右上角按钮
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_scan, menu);
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
			startActivityForResult(intent, 2);

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
		switch (requestCode) {
		case 1:
			String path = cameraTempFile.getAbsolutePath();
			Utils.compressImage(path); // 压缩图片
			String displyPath = path.startsWith("/") ? "file://" + path : path;
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
			break;
		case 2:
			// 扫描成功
			String barCode = data.getStringExtra("SCAN_RESULT");

			if (barCode.isEmpty()) {
				return;
			}

			final BarCode04 barCode04 = new BarCode04();
			boolean result04 = barCode04.paserBarCode(barCode);

			if (!result04) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.PP013_001_MSG));
				DoClear();
				return;
			}

			cdContainer = barCode04.getContainerCd(); // 箱号
            mailSendFlg = "0"; // add by yxq 2014/10/29
			// 根据扫描的箱号获取数据
			GetDataByCdContainer();

			break;
		}
	}

	/*
	 * 根据扫描的箱号获取数据
	 */
	private void GetDataByCdContainer() {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("cdContainer", cdContainer);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"PP013_GetContainerData", p1, new TypeToken<DepotPP013>() {
				}.getType(), new AmassHttpResponseHandler<DepotPP013>() {

					@Override
					protected void OnSuccess(DepotPP013 response) {
						super.OnSuccess(response);
						// 获取数据
						DepotPP013 depot = (DepotPP013) response;
						// 判断是否有数据
						if (depot == null) {
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.PP013_002_MSG));
							DoClear();
							return;
						} else {
							btn_SAVE.setEnabled(true);
							chkBoxOkFlg.setEnabled(true);
							chkBoxPollutionFlg.setEnabled(true);
							chkBoxDamagedFlg.setEnabled(true);
							chkBoxDeformFlg.setEnabled(true);
							txtRemark.setEnabled(true);
							txtQtVgmKgsLeather.setEnabled(true);
							actionFlag = true;
							// add by yxq 2014/10/24 begin
							spPlace.setEnabled(true);
							setSpinnerItemSelectedByValue(spPlace,depot.getPlace());
							// add by yxq 2014/10/24 end
							txtBoxNo.setText(depot.getNoBox()); // 集箱号
							txtContainer.setText(depot.getCdContainer()); // 箱号
							txtBoxModel.setText(depot.getNmBox()); // 箱型箱量
							txtBoxStatus.setText(depot.getNmStatusNow()); // 箱型箱量
							if(!depot.isFlgCheckPollution() && !depot.isFlgCheckDamaged()
									&& !depot.isFlgCheckDeform()){
							    //chkBoxOkFlg.setChecked(depot.isFlgCheckOk()); // 验箱正常标志
								//默认选中正常
								chkBoxOkFlg.setChecked(true);
							}
							chkBoxPollutionFlg.setChecked(depot
									.isFlgCheckPollution()); // 验箱污染标志
							chkBoxDamagedFlg.setChecked(depot
									.isFlgCheckDamaged()); // 验箱破损标志
							chkBoxDeformFlg.setChecked(depot.isFlgCheckDeform()); // 验箱变形标志
							txtRemark.setText(depot.getCheckRemarks()); // 铅封备注
							txtQtVgmKgsLeather.setText(depot.getQtVgmKgsLeather().toString());
							imageAdpter.clear();
							imageAdpter.add(new MoreData());

							if (depot.getFileList() != null
									&& depot.getFileList().size() > 0) {
								for (FileManageData file : depot.getFileList()) {
									ImageData data = new ImageData();
									data.setImageId(file.getFileId());
									data.setImageDesc(file.getFileName());
									MoreData d = new MoreData(data);
									//imageAdpter.insert(d,imageAdpter.getCount() - 1);
									imageAdpter.insert(d,1);
								}
							}
						}
					}
				}, true);
	}

	/*
	 * 页面清空
	 */
	private void DoClear() {
		btn_SAVE.setEnabled(false);
		chkBoxOkFlg.setEnabled(false);
		chkBoxOkFlg.setChecked(false);
		chkBoxPollutionFlg.setEnabled(false);
		chkBoxPollutionFlg.setChecked(false);
		chkBoxDamagedFlg.setEnabled(false);
		chkBoxDamagedFlg.setChecked(false);
		chkBoxDeformFlg.setEnabled(false);
		chkBoxDeformFlg.setChecked(false);
		txtRemark.setEnabled(false);
		txtQtVgmKgsLeather.setEnabled(false);
		txtRemark.setText("");
		txtQtVgmKgsLeather.setText("0.0");
		actionFlag = false;

		txtBoxNo.setText("");
		txtContainer.setText("");
		txtBoxModel.setText("");
		txtBoxStatus.setText("");
		
		// add by yxq 2014/10/24 begin
		spPlace.setSelection(0);
		spPlace.setEnabled(false);
		// add by yxq 2014/10/24 end

		imageAdpter.clear();
		imageAdpter.add(new MoreData());
	}

	
	public void Btn_SavePic_OnClick(View view){
		btn_SAVE.setEnabled(true);
		//txtRemark.setEnabled(true);
		actionFlag = true;
		imageAdpter.clear();
		imageAdpter.add(new MoreData());
		flgSavePic = true;
	}
	
	/*
	 * 保存按钮事件
	 */
	public void Btn_Save_OnClick(View view) {
		if (!flgSavePic){
			try {
				if (chkBoxOkFlg.isChecked() || chkBoxPollutionFlg.isChecked()
						|| chkBoxDamagedFlg.isChecked()
						|| chkBoxDeformFlg.isChecked()) {
					if (imageAdpter.getCount() < 2) {
						Utils.showAlertDialog(_thisActivity,
								getString(R.string.PP013_003_MSG));
						return;
					}
				}else{
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.PP013_004_MSG));
					return;
				}
				String qtVgmKgsLeather = txtQtVgmKgsLeather.getText().toString().trim();
				if (qtVgmKgsLeather.equals("") 
						|| Double.parseDouble(qtVgmKgsLeather) < 2000
						|| Double.parseDouble(qtVgmKgsLeather) > 5000){
					txtQtVgmKgsLeather.requestFocus();
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.PP013_005_MSG));
					return;
				}
				//PP013Data data = new PP013Data();
				data.setNoBox(txtBoxNo.getText().toString());
				data.setCheckRemarks(txtRemark.getText().toString().trim());
				data.setFlgCheckOk(chkBoxOkFlg.isChecked());
				data.setFlgCheckPollution(chkBoxPollutionFlg.isChecked());
				data.setFlgCheckDamaged(chkBoxDamagedFlg.isChecked());
				data.setFlgCheckDeform(chkBoxDeformFlg.isChecked());
				data.setQtVgmKgsLeather(Double.parseDouble(qtVgmKgsLeather));
				// add by yxq 2014/10/24 begin
				data.setPlace(((SelectDict)spPlace.getSelectedItem()).getId());
				// add by yxq 2014/10/24 end
				/*Map<String, Object> p1 = new HashMap<String, Object>();
				p1.put("pp013Data", data);
				p1.put("mailSendFlg", mailSendFlg); // 邮件是否发过标识， add by yxq 2014/10/28
				NetworkHelper.getInstance().postJsonData(_thisActivity,
						"PP013_UpdateContainerData", p1,String.class, 
						new AmassHttpResponseHandler<String>() {
							@Override
							protected void OnSuccess(String response) {
								super.OnSuccess(response);
								mailSendFlg = response;  // add by yxq 2014/10/28  
								SavePicture();
							}
						}, true);*/
				SavePicture();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			SavePicture();
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.msg_save_success));
		}
	}

	private void SaveData(){
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("pp013Data", data);
		p1.put("mailSendFlg", mailSendFlg); // 邮件是否发过标识， add by yxq 2014/10/28
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"PP013_UpdateContainerData", p1,String.class, 
				new AmassHttpResponseHandler<String>() {
					@Override
					protected void OnSuccess(String response) {
						super.OnSuccess(response);
						mailSendFlg = response;  
						Utils.showAlertDialog(_thisActivity,
								getString(R.string.msg_save_success));
						GetDataByCdContainer();
					}
				}, true);
	}
	
	/*
	 * 保存照片
	 */
	private void SavePicture() {
		try {
			RequestParams params = new RequestParams();
			if (flgSavePic){
				params.put("noBox", "X99999999");
				params.put("cdContainer", txtContainer.getText()); 
			}
			else{
				params.put("noBox", txtBoxNo.getText());
				// add by yxq 2014/10/21 begin
				params.put("cdContainer", txtContainer.getText());   // 箱号				
			}
			
			// add by yxq 2014/10/21 end
			//照片名序列，用于排序照片
			String imgOrderList = "";
			int nCount = imageAdpter.getCount();
			/*for (int nIndex = 0; nIndex < nCount; nIndex++) {
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
			}*/
			for (int nIndex = nCount -1 ; nIndex >=0; nIndex--) {
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
			String imageId_Del = "";
			// for (int i = 0; i < listImageId_Del.size(); i++) {
			// imageId_Del = imageId_Del + listImageId_Del.get(i) + ",";
			// }
			//
			params.put("imageId_Del", imageId_Del);
			NetworkHelper.getInstance().postFilesData(this,
					"PP013_UploadFiles", "BB", params,
					new TypeToken<HashMap<String, String>>() {
					}.getType(),
					new AmassHttpResponseHandler<HashMap<String, String>>() {

						@Override
						protected void OnSuccess(
								HashMap<String, String> response) {
							// listImageId_Del.clear();
							int nCount = imageAdpter.getCount();
							for (int nIndex = 0; nIndex < nCount; nIndex++) {
								MoreData d = imageAdpter.getItem(nIndex);
								if (d.getDataType() == MoreData.DATA_TYPE) {
									ImageData image = (ImageData) d.getData();
									if (image.getImageId() == null) {
										String key = image.getFileUploadId();
										if (response.containsKey(key)) {
											String imageId = response.get(key);
											image.setImageId(imageId);
										}
									}
								}
							}
							/*Utils.showAlertDialog(_thisActivity,
									getString(R.string.msg_save_success));
							GetDataByCdContainer();*/	
							if (!flgSavePic){
								SaveData();
							}
						}

					}, true);
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	// add by yxq 2014/10/24
	// 绑定下拉框
	private void InitDropDownList( Spinner sp,List<SelectDict> listData) {
		if (listData != null && listData.size() > 0) {
			// 添加空行
			SelectDict seDict = new SelectDict();
			seDict.setId("");
			seDict.setName("");
			listData.add(0, seDict);

			ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>(
					_thisActivity, android.R.layout.simple_spinner_item,listData);
			selectList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(selectList);
		}
	}
	
	/*
	 * 根据值, 设置spinner默认选中项
	 */
	private void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
		try {
			SpinnerAdapter apsAdapter = spinner.getAdapter(); // 得到SpinnerAdapter对象
			int flg = 0;
			if (apsAdapter != null) {
				int k = apsAdapter.getCount();
				for (int i = 0; i < k; i++) {
					SelectDict se = (SelectDict) apsAdapter.getItem(i);
					if ((se.getId()).equals(value)) {
						spinner.setSelection(i, true);// 默认选中项
						flg = 1;
						break;
					}
				}
				if(flg == 0){
					spinner.setSelection(0, true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 得到列表  add by yxq 2014/10/24
	private void GetSelectList(final String cdType) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("cdType", cdType);
		NetworkHelper.getInstance().postJsonData(_thisActivity,"Sys_GetDictList", p, 
				new TypeToken<List<SelectDict>>() {}.getType(),
				new AmassHttpResponseHandler<List<SelectDict>>() {
					@Override
					protected void OnSuccess(List<SelectDict> response) {
						super.OnSuccess(response);
						if(cdType.equals("CONTAINER_PLACE")){
							placeList = response;
							// add by yxq 2014/10/24 begin
							InitDropDownList(spPlace,placeList);    
							// add by yxq 2014/10/24 end
						}
					}
		}, false);
	}
}
