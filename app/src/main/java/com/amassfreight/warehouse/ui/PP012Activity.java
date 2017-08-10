package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode04;
import com.amassfreight.base.bean.BarCode05;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DepotPP012;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.widget.GalleryAct;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class PP012Activity extends BaseActivity {

	private String noBox = ""; // 集箱号
	private TextView txtBoxNo; // 集箱号
	private TextView txtContainer;// 箱号
	private TextView txtBoxModel;// 箱型箱量
	private TextView txtCloseNo;// 封号
	private EditText txtRemark; // 铅封备注
	private EditText txtTotalWeight;//集箱总重量

	private GridView gv;
	private File cameraTempFile;
	private ImageAdapter imageAdpter;
	// private List<String> listImageId_Del; // 删除的照片

	private Button btn_SAVE;
	private boolean actionFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp012);
		setupActionBar();

		initializeView();
	}

	/*
	 * 初期加载
	 */
	private void initializeView() {
		// 删除文件夹内容
		File dir = new File(Environment.getExternalStorageDirectory(),
				"amass/pics/pp012");
		Utils.deleteFileAndPath(dir);

		txtBoxNo = (TextView) findViewById(R.id.txtBoxNo); // 集箱号
		txtContainer = (TextView) findViewById(R.id.txtContainer); // 箱号
		txtBoxModel = (TextView) findViewById(R.id.txtBoxModel); // 箱型箱量
		txtCloseNo = (TextView) findViewById(R.id.txtCloseNo); // 封号
		txtRemark = (EditText) findViewById(R.id.txtRemark); // 铅封备注
		txtTotalWeight = (EditText) findViewById(R.id.txtTotalWeight); // 铅封备注
		txtTotalWeight.setSelectAllOnFocus(true);
		txtRemark.setEnabled(false);
		txtTotalWeight.setEnabled(false);

		btn_SAVE = (Button) findViewById(R.id.btn_SAVE); // 保存按钮
		btn_SAVE.setEnabled(false);

		gv = (GridView) findViewById(R.id.gridView_image);
		imageAdpter = new ImageAdapter(this, 0);
		imageAdpter.add(new MoreData());
		gv.setAdapter(imageAdpter);
		// listImageId_Del = new ArrayList<String>();

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
								"amass/pics/pp012");
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
			final BarCode05 barCode05 = new BarCode05();
			boolean result04 = barCode04.paserBarCode(barCode);
			boolean result05 = barCode05.paserBarCode(barCode);

			if (!result05 && !result04) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.PP012_001_MSG));
				DoClear();
				return;
			}

			if (result04) {
				noBox = barCode04.getContainerCd(); // 箱号
			}
			if (result05) {
				noBox = barCode05.getBoxNo(); // 集箱号
			}

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
		p1.put("noBox", noBox);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"PP012_GetContainerData", p1, new TypeToken<DepotPP012>() {
				}.getType(), new AmassHttpResponseHandler<DepotPP012>() {

					@Override
					protected void OnSuccess(DepotPP012 response) {
						super.OnSuccess(response);
						// 获取数据
						DepotPP012 depot = (DepotPP012) response;
						// 判断是否有数据
						if (depot == null) {
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.PP012_002_MSG));
							DoClear();
							return;
						} else {
							btn_SAVE.setEnabled(true);
							actionFlag = true;
							txtRemark.setEnabled(true);
							txtTotalWeight.setEnabled(true);

							txtBoxNo.setText(depot.getNoBox()); // 集箱号
							txtContainer.setText(depot.getCdContainer()); // 箱号
							txtBoxModel.setText(depot.getNmBox()); // 箱型箱量
							txtCloseNo.setText(depot.getCdBoxTitle()); // 封号
							txtRemark.setText(depot.getSealRemarks()); // 铅封备注
							txtTotalWeight.setText(String.valueOf(depot.getTotalWeight()));//总重量

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
									imageAdpter.insert(d, 1);
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
		txtBoxNo.setText("");
		txtContainer.setText("");
		txtBoxModel.setText("");
		txtCloseNo.setText("");
		txtRemark.setText("");
		btn_SAVE.setEnabled(false);
		actionFlag = false;
		txtRemark.setEnabled(false);
		txtTotalWeight.setEnabled(false);
		imageAdpter.clear();
		imageAdpter.add(new MoreData());
	}

	/*
	 * 保存按钮事件
	 */
	public void Btn_Save_OnClick(View view) {
		if (txtTotalWeight.getText().toString().trim().equals("") ||
			Double.parseDouble(txtTotalWeight.getText().toString().trim()) < 2 ||
			Double.parseDouble(txtTotalWeight.getText().toString().trim()) > 40){
			txtTotalWeight.requestFocus();
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.PP012_TOTAL_WEIGHT_EMPTY));
			return;
			
		}
		try {
			Map<String, Object> p1 = new HashMap<String, Object>();
			p1.put("noBox", txtBoxNo.getText().toString());
			p1.put("strRemark", txtRemark.getText().toString().trim());
			p1.put("totalWeight", txtTotalWeight.getText().toString().trim());

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"PP012_UpdateContainerData", p1,
					new TypeToken<DepotPP012>() {
					}.getType(), new AmassHttpResponseHandler<DepotPP012>() {

						@Override
						protected void OnSuccess(DepotPP012 response) {
							super.OnSuccess(response);
							SavePicture();
						}
					}, true);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 保存照片
	 */
	private void SavePicture() {
		try {
			RequestParams params = new RequestParams();
			params.put("noBox", txtBoxNo.getText());
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
					"PP012_UploadFiles", "AA", params,
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
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.msg_save_success));
							GetDataByCdContainer();
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
}
