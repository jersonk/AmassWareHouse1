package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DN005DetailData;
import com.amassfreight.domain.DepotDN005;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.widget.GalleryAct;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class DN005Activity extends BaseActivity {
	private String depotID = ""; // 进仓ID
	private String noBatch = ""; // 批次
	private String cdOrder = ""; // 进仓编号
	private String thNo = ""; // 同行编号
	private String noPilecard = ""; // 桩脚牌ID
	private String depotDtID = ""; // 货物明细ID

	private TextView txtDepotNo; // 进仓编号
	private TextView txtThNo; // 同行编号
	private TextView txtNoBatch; // 批次
	private TextView txtNoPilecard; // 桩脚牌ID

	private EditText txtSlight; // 轻微破损
	private EditText txtDamage;	// 一般破损
	private EditText txtSerious; // 严重破损
	private EditText txtLogNo; // 原木无章
	private EditText txtWarp; // 变形
	private EditText txtPolluteSlight; // 轻微污染
	private EditText txtPolluteSerious; // 严重污染
	private EditText txtDampSlight; // 轻微受潮
	private EditText txtDampSerious; // 严重受潮
	private EditText txtOilStained; // 油污
	private EditText txtNumBark; // 树皮
	private CheckBox chkLogYesFlg; // 原木有章
	private CheckBox chkSimplePackageFlg; // 包装简易
	//private CheckBox chkMarksDifFlg; // 唛头不符
	private CheckBox chkBark; // 树皮
	private CheckBox chkOther; // 其他异常
	//private CheckBox chkNoMarks;//无唛头
	private EditText txtRemark; // 备注
	private Button btnSave;

	private GridView gv;
	private File cameraTempFile;
	private ImageAdapter imageAdpter;
	// private List<String> listImageId_Del; // 删除的照片
	private boolean bolFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dn005);
		setupActionBar();

		// 初期加载
		initializeView();
	}

	/*
	 * 初期加载
	 */
	private void initializeView() {
		// 删除文件夹内容
		File dir = new File(Environment.getExternalStorageDirectory(),
				"amass/pics/dn005");
		Utils.deleteFileAndPath(dir);

		/* 获取Intent中的Bundle对象 */
		Bundle bundle = this.getIntent().getExtras();
		/* 获取Bundle中的数据，注意类型和key */
		cdOrder = bundle.getString("coOrder");
		thNo = bundle.getString("thNo");
		noBatch = bundle.getString("noBatch");
		depotDtID = bundle.getString("depotDtID");
		noPilecard = bundle.getString("noPilecard");
		depotID = bundle.getString("depotID");
		bolFlag = bundle.getBoolean("bolFlag");

		txtDepotNo = (TextView) findViewById(R.id.txtInStore); // 进仓编号
		txtDepotNo.setText(cdOrder);
		txtThNo = (TextView) findViewById(R.id.txtTH); // 同行编号
		txtThNo.setText(thNo);
		txtNoBatch = (TextView) findViewById(R.id.txtPC); // 批次
		txtNoBatch.setText(noBatch);
		txtNoPilecard = (TextView) findViewById(R.id.txtNoPilecard); // 桩脚牌ID
		txtNoPilecard.setText(noPilecard);

		txtSlight = (EditText) findViewById(R.id.edit_Slight); // 轻微破损
		txtSlight.setSelectAllOnFocus(true);
		
		txtDamage = (EditText) findViewById(R.id.edit_Damage); // 一般破损
		txtDamage.setSelectAllOnFocus(true);
		
		txtSerious = (EditText) findViewById(R.id.edit_Serious); // 严重破损
		txtSerious.setSelectAllOnFocus(true);
		txtLogNo = (EditText) findViewById(R.id.edit_LogNo); // 原木无章
		txtLogNo.setSelectAllOnFocus(true);
		txtWarp = (EditText) findViewById(R.id.edit_Warp); // 变形
		txtWarp.setSelectAllOnFocus(true);
		txtDampSlight = (EditText) findViewById(R.id.edit_DampSlight); // 轻微受潮
		txtDampSlight.setSelectAllOnFocus(true);
		txtDampSerious = (EditText) findViewById(R.id.edit_DampSerious); // 严重受潮
		txtDampSerious.setSelectAllOnFocus(true);
		txtOilStained = (EditText) findViewById(R.id.edit_OilStained); // 油污
		txtOilStained.setSelectAllOnFocus(true);
		txtPolluteSlight = (EditText) findViewById(R.id.edit_PolluteSlight); // 一般污染
		txtPolluteSlight.setSelectAllOnFocus(true);
		txtPolluteSerious = (EditText) findViewById(R.id.edit_PolluteSerious); // 严重污染
		txtPolluteSerious.setSelectAllOnFocus(true);
		chkLogYesFlg = (CheckBox) findViewById(R.id.chk_LogYesFlg); // 原木有章
		chkSimplePackageFlg = (CheckBox) findViewById(R.id.chk_SimplePackageFlg); // 包装简易
		//chkMarksDifFlg = (CheckBox) findViewById(R.id.chk_MarksDifFlg); // 唛头不符
		//chkNoMarks = (CheckBox) findViewById(R.id.chk_noMarks); //无唛头
		chkBark = (CheckBox) findViewById(R.id.chk_flgBark); // 树皮
		txtNumBark = (EditText) findViewById(R.id.edit_Bark); // 树皮
		chkOther = (CheckBox) findViewById(R.id.chk_others); // 其他
		// txtNumBark.setEnabled(false);
		txtNumBark.setSelectAllOnFocus(true);
		txtRemark = (EditText) findViewById(R.id.txtRemark); // 备注
		btnSave = (Button) findViewById(R.id.btn_SAVE);
		btnSave.setEnabled(false);
		// chkBark.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton arg0, boolean isChecked)
		// {
		// 
		// if (isChecked) {
		// txtNumBark.setEnabled(true);
		// } else {
		// txtNumBark.setEnabled(false);
		// txtNumBark.setText("");
		// }
		// }
		// });
		gv = (GridView) findViewById(R.id.grid_ErrorPicture);
		imageAdpter = new ImageAdapter(this, 0);
		//imageAdpter.add(new MoreData());
		gv.setAdapter(imageAdpter);
		// listImageId_Del = new ArrayList<String>();

		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE) { // 添加
					if (CheckCanUseAction() && bolFlag) {
						Intent intent = new Intent();
						// 指定开启系统相机的Action
						intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						File dir = new File(Environment
								.getExternalStorageDirectory(),
								"amass/pics/dn005");
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
		
		/*// 唛头不符
		chkMarksDifFlg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
					chkNoMarks.setChecked(!((CheckBox) v).isChecked());
				}
			}
		});
		//无唛头
		chkNoMarks.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
					chkMarksDifFlg.setChecked(!((CheckBox) v).isChecked());
				}
			}
		});
*/
		// 获取页面数据
		GetEditData();
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
	 * 根据货物明细ID货物数据
	 */
	private void GetEditData() {
		try {
			Map<String, Object> p1 = new HashMap<String, Object>();
			p1.put("depotDtID", depotDtID);
			p1.put("cdOrder", cdOrder);
			p1.put("depotID", depotID);

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"DN005_GetEditDataByDepotDtID", p1,
					new TypeToken<DepotDN005>() {
					}.getType(), new AmassHttpResponseHandler<DepotDN005>() {

						@Override
						protected void OnSuccess(DepotDN005 response) {
							super.OnSuccess(response);
							btnSave.setEnabled(bolFlag);
							// 获取数据
							DepotDN005 depot = (DepotDN005) response;

							if (depot != null) {
								txtSlight.setText(String.valueOf(depot
										.getNumGoodsDamageSlight()));
								txtDamage.setText(String.valueOf(depot
										.getNumGoodsDamage()));//一般破损
								txtSerious.setText(String.valueOf(depot
										.getNumGoodsDamageSerious())); // 严重破损
								txtLogNo.setText(String.valueOf(depot
										.getNumLogNo())); // 原木无章
								txtWarp.setText(String.valueOf(depot
										.getNumWarp())); // 变形
								txtPolluteSlight.setText(String.valueOf(depot
										.getNumPolluteSlight())); // 轻微污染
								txtPolluteSerious.setText(String.valueOf(depot
										.getNumPolluteSerious())); // 严重污染
								txtDampSlight.setText(String.valueOf(depot
										.getNumDampSlight())); // 轻微受潮
								txtDampSerious.setText(String.valueOf(depot
										.getNumDampSerious())); // 严重受潮
								txtOilStained.setText(String.valueOf(depot
										.getNumOilStained())); // 油污
								chkLogYesFlg.setChecked(depot.isFlgLogYes()); // 原木有章
								chkSimplePackageFlg.setChecked(depot
										.isFlgSimplePackage()); // 包装简易
								//chkMarksDifFlg.setChecked(depot.isFlgMarksDif()); // 唛头不符
								//chkNoMarks.setChecked(depot.isFlgMarksExist());   //无唛头
								txtRemark.setText(depot.getGoodsDamageRemark()); // 备注

								chkBark.setChecked(depot.isFlgBark()); // 树皮
								chkOther.setChecked(depot.isFlgDamageOthers());//其他异常
								txtNumBark.setText(String.valueOf(depot
										.getNumBark())); // 树皮
							}

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
								/*for(int i = depot.getFileList().size()-1;i >= 0;i-- ){
									ImageData data = new ImageData();
									data.setImageId(depot.getFileList().get(i).getFileId());
									data.setImageDesc(depot.getFileList().get(i).getFileName());
									MoreData d = new MoreData(data);
									imageAdpter.add(d);									
								}*/
		    					imageAdpter.notifyDataSetChanged();
							}
						}
					}, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 验证是否可以使用拍照功能
	 */
	private boolean CheckCanUseAction() {
		if (txtSlight.getText().length() > 0
				|| txtDamage.getText().length() > 0
				|| txtPolluteSlight.getText().length() > 0
				|| txtDampSlight.getText().length() > 0
				|| txtDampSerious.getText().length() > 0
				|| txtSerious.getText().length() > 0
				|| txtLogNo.getText().length() > 0
				|| txtWarp.getText().length() > 0
				|| txtPolluteSerious.getText().length() > 0
				|| txtOilStained.getText().length() > 0
				|| txtNumBark.getText().length() > 0
				|| chkLogYesFlg.isChecked() 
				|| chkSimplePackageFlg.isChecked()
				|| chkBark.isChecked()
				|| chkOther.isChecked()
				|| txtRemark.getText().length() > 0
				//|| chkMarksDifFlg.isChecked() 
			    //|| chkNoMarks.isChecked()
			    ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
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
		}
	}

	/*
	 * 保存按钮事件
	 */
	public void Btn_Save_OnClick(View view) {
		if (!DoCheck()) {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN005_001_MSG));
			return;
		}
		UpdateErrorData();
	}

	/*
	 * 入力验证
	 */
	private boolean DoCheck() {
		if ((txtSlight.getText().length() > 0 && Integer.parseInt(txtSlight
				.getText().toString().trim()) > 0)
				|| (txtDamage.getText().length() > 0 && Integer
						.parseInt(txtDamage.getText().toString().trim()) > 0)
				|| (txtPolluteSlight.getText().length() > 0 && Integer
						.parseInt(txtPolluteSlight.getText().toString().trim()) > 0)
				|| (txtDampSlight.getText().length() > 0 && Integer
						.parseInt(txtDampSlight.getText().toString().trim()) > 0)
				|| (txtDampSerious.getText().length() > 0 && Integer
						.parseInt(txtDampSerious.getText().toString().trim()) > 0)
				|| (txtSerious.getText().length() > 0 && Integer
						.parseInt(txtSerious.getText().toString().trim()) > 0)
				|| (txtLogNo.getText().length() > 0 && Integer
						.parseInt(txtLogNo.getText().toString().trim()) > 0)
				|| (txtWarp.getText().length() > 0 && Integer.parseInt(txtWarp
						.getText().toString().trim()) > 0)
				|| (txtPolluteSerious.getText().length() > 0 && Integer.parseInt(txtPolluteSerious
						.getText().toString().trim()) > 0)
				|| (txtOilStained.getText().length() > 0 && Integer
						.parseInt(txtOilStained.getText().toString().trim()) > 0)
				|| (txtNumBark.getText().length() > 0 && Integer
						.parseInt(txtNumBark.getText().toString().trim()) > 0)
				|| chkLogYesFlg.isChecked()
				|| chkSimplePackageFlg.isChecked()
				|| chkBark.isChecked()
				|| chkOther.isChecked()
				|| txtRemark.getText().length() > 0
				//|| chkMarksDifFlg.isChecked() 
				//|| chkNoMarks.isChecked()
				) {
			if (imageAdpter.getCount() > 1) {
				return true;
			} else {
				return false;
			}
		}
		else{
			return false;	
		}		
	}

	/*
	 * 更新异常信息
	 */
	private void UpdateErrorData() {
		try {
			// 参数赋值
			DN005DetailData depotDt = SetDepotData();
			depotDt.setDepotDtID(depotDtID); // 货物明细ID

			Map<String, Object> p1 = new HashMap<String, Object>();
			p1.put("depotDt", depotDt);

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"DN005_UpdateErrorData", p1, new TypeToken<DepotDN005>() {
					}.getType(), new AmassHttpResponseHandler<DepotDN005>() {

						@Override
						protected void OnSuccess(DepotDN005 response) {
							super.OnSuccess(response);
							boolean bolflag = false;
							for (int nIndex = 0; nIndex < imageAdpter
									.getCount(); nIndex++) {
								MoreData data = imageAdpter.getItem(nIndex);
								if (data.getDataType() == MoreData.DATA_TYPE) {
									ImageData imageData = (ImageData) data
											.getData();
									if (imageData.getImageId() == null) {
										bolflag = true;
									}
								}
							}

							// if (listImageId_Del.size() > 0) {
							// bolflag = true;
							// }

							if (bolflag) {
								SavePicture();
							} else {
								Utils.showAlertDialog(_thisActivity,
										getString(R.string.msg_save_success));
							}
						}
					}, true);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 参数赋值
	 */
	private DN005DetailData SetDepotData() {
		DN005DetailData depotDt = new DN005DetailData();
		depotDt.setFlgGoodsDamageSlight(ReturnFlg(txtSlight)); // 货物轻微破损标志
		depotDt.setNumGoodsDamageSlight(ReturnNum(txtSlight)); // 货物轻微破损件数
		
		depotDt.setFlgGoodsDamage(ReturnFlg(txtDamage)); // 货物一般破损标志
		depotDt.setNumGoodsDamage(ReturnNum(txtDamage)); // 货物一般破损件数
		
		depotDt.setFlgPolluteSlight(ReturnFlg(txtPolluteSlight)); // 货物轻微污染标志
		depotDt.setNumPolluteSlight(ReturnNum(txtPolluteSlight)); // 货物轻微污染件数
		depotDt.setFlgGoodsDamageSerious(ReturnFlg(txtSerious)); // 货物严重破损标志
		depotDt.setNumGoodsDamageSerious(ReturnNum(txtSerious)); // 货物严重破损件数
		depotDt.setFlgLogNo(ReturnFlg(txtLogNo)); // 原木无章标志
		depotDt.setNumLogNo(ReturnNum(txtLogNo)); // 原木无章件数
		depotDt.setFlgWarp(ReturnFlg(txtWarp)); // 变形标志
		depotDt.setNumWarp(ReturnNum(txtWarp)); // 变形件数
		depotDt.setFlgPolluteSerious(ReturnFlg(txtPolluteSerious)); // 严重污染标志
		depotDt.setNumPolluteSerious(ReturnNum(txtPolluteSerious)); // 严重污染件数
		depotDt.setFlgDampSlight(ReturnFlg(txtDampSlight)); // 轻微受潮标志
		depotDt.setNumDampSlight(ReturnNum(txtDampSlight)); // 轻微受潮件数
		depotDt.setFlgDampSerious(ReturnFlg(txtDampSerious)); // 严重受潮标志
		depotDt.setNumDampSerious(ReturnNum(txtDampSerious)); // 严重受潮件数
		depotDt.setFlgOilStained(ReturnFlg(txtOilStained)); // 油污标志
		depotDt.setNumOilStained(ReturnNum(txtOilStained)); // 油污件数
		depotDt.setFlgLogYes(chkLogYesFlg.isChecked()); // 原木有章标志
		depotDt.setFlgSimplePackage(chkSimplePackageFlg.isChecked()); // 包装简易标志
		//depotDt.setFlgMarksDif(chkMarksDifFlg.isChecked()); // 唛头不符标志
		//depotDt.setFlgMarksExist(chkNoMarks.isChecked());  // 无唛头标志
		depotDt.setGoodsDamageRemark(txtRemark.getText().toString().trim()); // 货物破损备注
		depotDt.setFlgBark(chkBark.isChecked()); // 树皮
		depotDt.setFlgDamageOthers(chkOther.isChecked());
		depotDt.setNumBark(ReturnNum(txtNumBark)); // 树皮
		return depotDt;
	}

	/*
	 * 判断标识
	 */
	private boolean ReturnFlg(EditText edit) {
		if (edit.getText().toString().trim().length() > 0
				&& Integer.parseInt(edit.getText().toString().trim()) > 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 返回件数
	 */
	private int ReturnNum(EditText edit) {
		if (edit.getText().toString().trim().length() > 0) {
			return Integer.parseInt(edit.getText().toString().trim());
		} else {
			return 0;
		}
	}

	/*
	 * 保存照片
	 */
	private void SavePicture() {
		try {
			RequestParams params = new RequestParams();
			params.put("cdOrder", cdOrder);
			params.put("depotId", depotID);
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

			params.put("imageId_Del", imageId_Del);
			NetworkHelper.getInstance().postFilesData(this,
					"DN005_UploadFiles", "AA", params,
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
							GetEditData();
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
}
