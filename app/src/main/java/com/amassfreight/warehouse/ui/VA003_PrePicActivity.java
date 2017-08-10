package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DN001DepotHD;
import com.amassfreight.domain.DepotVA001;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.amassfreight.widget.GalleryAct;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class VA003_PrePicActivity extends BaseActivity implements OnDialogOkListener {
	
	private EditText editOrder;  //转移到进仓编号
	private EditText editCoLoader; //转移到同行编号 
	private TextView orderOrgin; //当前进仓编号
	private Button btn_savePoto; // 保存照片
	private GridView gvInStore;
	private ImageAdapter imageAdpter;
	private String cdOrder; // 原进仓编号
	private String cdOrderPublic;
	private String cdOrderPublicTransfer = "";
	private String coLoaderTransfer = "";
	private String depotIdTransfer= "";
	private String noBatch ="";
	private File cameraTempFile;
	private static final int REQUEST_CODE_IMAGE = 1;        // 拍照
	private boolean hasPhotoFlag;	 	  		  // 是否有照片标记
	private DepotVA001 itemVA001;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_va003_prepic);
		setupActionBar();
		
		Bundle bundle = this.getIntent().getExtras();
		cdOrder = bundle.getString("cdOrder");
		cdOrderPublic = bundle.getString("cdOrderPublic");
		itemVA001 = (DepotVA001) bundle.getSerializable("itemVA001");
		
		editOrder = (EditText) findViewById(R.id.edit_cdOrder);        // 转移到进仓编号
		editCoLoader = (EditText) findViewById(R.id.edit_coLoader);    // 转移到同行编号
		orderOrgin = (TextView) findViewById(R.id.txt_cdOrder_orgin);  // 当前进仓编号
		btn_savePoto = (Button) findViewById(R.id.btn_savePoto);
		btn_savePoto.setVisibility(View.GONE);
		
		if(cdOrderPublic != null){
		    orderOrgin.setText(cdOrderPublic);
		}		
		gvInStore = (GridView) findViewById(R.id.grid_Insert);
		
		gvInStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE) {   // 添加
					Intent intent = new Intent();
					// 指定开启系统相机的Action
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					File dir = new File(Environment.getExternalStorageDirectory(),
							"amass/pics/va003/transfer");
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
		gvInStore.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int pos, long id) {
						MoreData data = (MoreData) parent.getItemAtPosition(pos);
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
		
		editOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode((EditText) view);

				ShowInputDialig();
			}
		});
		
		editCoLoader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 禁用软键盘
				hideSoftInputMode((EditText) view);
				ShowCdLoaderInputDialig();
			}
		});
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
			}
			break;
	    }
	}
	
	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {

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
	 * 弹出进仓编号输入框
	 */
	public void ShowInputDialig() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.DN001_INSERT_STORE).setCancelable(false);
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.dn001_view_input_dialog, null);
		final EditText editTexCdOrder = (EditText) textEntryView
				.findViewById(R.id.editTextCdOrder);
		builder.setView(textEntryView);
		final AlertDialog dialog = builder.show();// 显示对话框
		dialog.setCanceledOnTouchOutside(false); // 点击空白处，不关闭对话框
		// "保存"按钮单击事件
		Button btnSave = (Button) textEntryView.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String strCdOrder = editTexCdOrder.getText().toString().trim(); // 进仓编号
				if (strCdOrder.length() > 0) {
					getDataByInput(Utils.completeOrderId(strCdOrder));
					dialog.dismiss(); // 关闭对话框
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
	 * 弹出同行编号输入框
	 */
	public void ShowCdLoaderInputDialig() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.DN001_THNO).setCancelable(false);
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.dn001_view_loader_input_dialog, null);
		final EditText editTexCdLoader = (EditText) textEntryView.findViewById(R.id.editTextCdLoader);
		builder.setView(textEntryView);
		final AlertDialog dialog = builder.show();// 显示对话框
		dialog.setCanceledOnTouchOutside(false); // 点击空白处，不关闭对话框
		// "保存"按钮单击事件
		Button btnSearch = (Button) textEntryView.findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String strCdLoader = editTexCdLoader.getText().toString().trim(); // 同行编号
				if (strCdLoader.length() > 0) {
					getDataByCdLoaderInput(strCdLoader);
					dialog.dismiss(); // 关闭对话框
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
	 * 根据输入的进仓编号获取数据
	 */
	private void getDataByInput(String strCdOrder) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("cdOrderPublic", strCdOrder);
		cdOrderPublicTransfer = strCdOrder;
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN001_GetDepotIDByCdOrderPublic", p1, new TypeToken<String>() {
				}.getType(), new AmassHttpResponseHandler<String>() {

					@Override
					protected void OnSuccess(String response) {
						super.OnSuccess(response);
						// 获取数据
						String strDepotID = (String) response;

						if (strDepotID == null) {
							cdOrderPublicTransfer = "";
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.DN001_005_MSG));
							return;
						} else {
							depotIdTransfer = strDepotID;
							editOrder.setText(cdOrderPublicTransfer);
							editCoLoader.setText("");
							btn_savePoto.setVisibility(View.VISIBLE);
							imageAdpter = new ImageAdapter(VA003_PrePicActivity.this, 0);
                    		imageAdpter.add(new MoreData());
                    		gvInStore.setAdapter(imageAdpter);
                    		GetDataByDepotID();
						}
					}
				}, true);
	}
	
	/*
	 * 根据输入的同行编号获取数据
	 */
	private void getDataByCdLoaderInput(String strCdLoader) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("cdLoader", strCdLoader);
		coLoaderTransfer = strCdLoader;
		NetworkHelper.getInstance().postJsonData(_thisActivity,
			"DN001_GetDepotIDByCdLoader", p1, new TypeToken<String>() {
			}.getType(), new AmassHttpResponseHandler<String>() {
				@Override
				protected void OnSuccess(String response) {
					super.OnSuccess(response);
					// 获取数据
					String strDepotID = (String) response;
					if (strDepotID == null) {
						coLoaderTransfer = "";
						Utils.showAlertDialog(_thisActivity,getString(R.string.DN001_006_MSG));
						return;
					} else {
						depotIdTransfer = strDepotID;
						editOrder.setText("");
						editCoLoader.setText(coLoaderTransfer);
						btn_savePoto.setVisibility(View.VISIBLE);
						imageAdpter = new ImageAdapter(VA003_PrePicActivity.this, 0);
                		imageAdpter.add(new MoreData());
                		gvInStore.setAdapter(imageAdpter);
                		GetDataByDepotID();
					}
				}
			}, true);
	}
	
	/*
	 * 照片保存按钮事件
	 */
	public void Btn_SavePhoto_OnClick(View view) {
		if(hasPhotoFlag){
			RequestParams params = new RequestParams();
			params.put("cdOrder", cdOrderPublicTransfer);
			params.put("depotId", depotIdTransfer);
			params.put("noBatch", noBatch);
			//照片名序列，用于排序照片
			String imgOrderList = "";

			for (int nIndex = imageAdpter.getCount() -1 ; nIndex >=0; nIndex--) {
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

			params.put("imageId_Del", imageId_Del);
			NetworkHelper.getInstance().postFilesData(this, "DN002_UploadFiles",
					"JC", params, new TypeToken<HashMap<String, String>>() {
			}.getType(),
			new AmassHttpResponseHandler<HashMap<String, String>>() {

				@Override
				protected void OnSuccess(HashMap<String, String> response) {
					// listImageId_Del.clear();
					for (int nIndex = 0; nIndex < imageAdpter.getCount(); nIndex++) {
						MoreData d = imageAdpter.getItem(nIndex);
						if (d.getDataType() == MoreData.DATA_TYPE) {
							ImageData image = (ImageData) d.getData();
							if (image.getImageId() == null) {
								String key = image.getFileUploadId();
								if (response.containsKey(key)) {
									String imageId = response.get(key);
									//image.setImageId(imageId);
									Utils.setImageInfo(image, imageId);
								}
							}
						}
					}
					imageAdpter.notifyDataSetChanged();
					Utils.showAlertDialog(_thisActivity,getString(R.string.msg_save_success));

				}
			}, true);
		}else{
			Utils.showAlertDialog(_thisActivity,getString(R.string.DN002_error_NoOrderPic));
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
	
/*	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			SessionHelper.getInstance().setObject("VA003", itemVA001);
			Intent intent = new Intent(_thisActivity,VA003Activity.class);
			startActivity(intent);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}		*/
	
	private void GetDataByDepotID() {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotID", depotIdTransfer);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN001_GetDataByDepotID", p1, new TypeToken<DN001DepotHD>() {
				}.getType(), new AmassHttpResponseHandler<DN001DepotHD>() {

					@Override
					protected void OnSuccess(DN001DepotHD response) {
						super.OnSuccess(response);
						// 获取数据
						DN001DepotHD depot = (DN001DepotHD) response;

						if (depot == null) {
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.DN001_002_MSG));
							return;
						} else {
							// 进仓编号存在的场合
							if (depot.getCdOrder() != null
									&& depot.getCdOrder().length() != 0) {
								// 进仓编号
								editOrder.setText(depot.getCdOrderPublic());// 进仓编号
								editCoLoader.setText(depot.getCoLoader()); // 同行编号
								cdOrderPublicTransfer = depot.getCdOrderPublic();
								coLoaderTransfer = depot.getCoLoader();
							} else if (depot.getCdOrder().length() == 0
									&& depot.getCoLoader().length() != 0) {
								// 进仓编号不存在，同行编号存在的场合
								editCoLoader.setText(depot.getCoLoader()); // 同行编号
								coLoaderTransfer = depot.getCoLoader();
							} else {
								return;
							}
						}

					}
				}, true);
	}

}
