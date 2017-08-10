package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.CommonResult;
import com.amassfreight.domain.DN002ListData;
import com.amassfreight.domain.DepotDN002;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.domain.SelectDict;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.widget.GalleryAct;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

@SuppressLint("SimpleDateFormat")
public class DN002Activity extends BaseActivity {

	private String depotID = ""; // 扫描后得到的进仓ID
	private String noBatch = ""; // 批次
	private String cdOrder = ""; // 进仓编号 共通
	private String cdOrderPublic = ""; // 进仓编号 个别
	private String AddType = ""; // 新增、编辑批次标识
	private boolean bolFlgInStore; // 进仓标志
	private TextView txtDepotNo; // 进仓编号
	private TextView txtThNo; // 同行编号
	private TextView txtNoBatch; // 批次

	private int sumJS;
	private double sumZL;
	private double sumTJ;

	private LinearLayout layTitle;
	private LinearLayout layHJ;
	private View lineTitle;
	private TextView txtSUMJS; // 合计：件数
	private TextView txtSUMZL; // 合计：重量
	private TextView txtSUMTJ; // 合计：体积

	private Button btnAdd;
	private Button btnSavePhoto;
	private Button btnPortInput;
	// add by yxq 2014/08/21 begin
	private CheckBox chkMatch; 	  // 唛头相符
	private CheckBox chkNotMatch; // 唛头不符
	private CheckBox chkKarachiHasInvoice; //有箱单、发票
	private CheckBox chkKarachiHasNotInvoice; //无箱单、发票
	private CheckBox chkBlackDrum;			//黑色塑料桶
	private CheckBox chkDrumHandle;			//黑色塑料桶
	private CheckBox chkNoMarks;  // 无唛头
	private CheckBox chkPortMarks;// 唛头港口不符
	private boolean mailSendFlg;  // 邮件是否发过
	private TextView textMTDesc;  // 唛头
	private String nmWpod;			//港口
	// add by yxq 2014/08/21 end
	// add by yxq 2014/09/17 begin
	private String strReaMeaFlg;  // 复查标识 
	// add by yxq 2014/09/17 end
	private CheckBox chkHydraulic;	//是否使用了液压车
	private CheckBox chkCdOrderDif;	//进仓编号不符
	
	private EditText editHydraulic;	//使用液压车的件数
	
	private ListView listView;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter adapter;
	private LayoutInflater mInflater;

	private GridView gvInStore;
	private GridView gvMt;
	private GridView gvKarachi;

	private File cameraTempFile;
	private ImageAdapter imageAdpter1;
	private ImageAdapter imageAdpter2;
	private ImageAdapter imageAdpter3;
	
	// private List<String> listImageId_Del1; // 删除的照片
	// private List<String> listImageId_Del2; // 删除的照片

	private String CarNo; // 车牌号
	private String CarType; // 车辆类型
	private String NoMultipleOrder; // 分票号
	private String NoCarNum; // 同车编号
	private String NoMultipleDepot; // 进仓分票号
	private String NoMultipleColo;  // 同行编号分号

	private String[] selItem = null;
	private boolean enFlag = true;
	private boolean btnFlag;
	private boolean bolFlag;
	private DN002ListData dataList;
	private boolean bolRole = false;
	private boolean bolIsTally=false;
	private boolean alertFlg = false; //是否操作成功后弹出确认框
	//private String strCdWareHouse = ""; //打印机编号
	private List<DN002ListData> list;
	private List<SelectDict> portList;
	private String realPortInput = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dn002);
		setupActionBar();
		//获取库区信息
		//GetCdWareHouse();
		// 初期加载
		initializeView();
	}

	/*
	 * 初期加载
	 */
	private void initializeView() {
		CarNo = "";
		CarType = "";
		NoMultipleOrder = "";
		NoCarNum = "";
		NoMultipleDepot = "";   // add yxq 2014/09/03
		NoMultipleColo = "";
		bolFlag = true;

		// 删除文件夹内容
		File dir = new File(Environment.getExternalStorageDirectory(),
				"amass/pics/dn002");
		Utils.deleteFileAndPath(dir);

		/* 获取Intent中的Bundle对象 */
		Bundle bundle = this.getIntent().getExtras();

		/* 获取Bundle中的数据，注意类型和key */
		btnFlag = bundle.getBoolean("btnFlag");
		bolFlgInStore = bundle.getBoolean("bolFlgInStore");
		AddType = bundle.getString("AddType");
		cdOrder = bundle.getString("cdOrder");
		cdOrderPublic = bundle.getString("cdOrderPublic");
		depotID = bundle.getString("depotID");
		String tnNo = bundle.getString("thNo");
		noBatch = bundle.getString("noBatch");
		String MTDesc = bundle.getString("txtMT");  // 唛头 add by yxq 2014/08/21
		nmWpod = bundle.getString("txtGK");	//港口
		
		// ZXX MODIFY
		bolRole = bundle.getBoolean("bolRole");
		bolIsTally = bundle.getBoolean("bolIsTally");
		if ((bolIsTally || bolRole) && bolFlgInStore) {
			bolFlgInStore=true;
		}
		else {
			bolFlgInStore=false;
		}
		
		txtDepotNo = (TextView) findViewById(R.id.txtInStore); //
		txtDepotNo.setText(cdOrderPublic);
		txtThNo = (TextView) findViewById(R.id.txtTH); //
		txtThNo.setText(tnNo);
		txtNoBatch = (TextView) findViewById(R.id.txtPC); //
		txtNoBatch.setText(noBatch);

		layHJ = (LinearLayout) findViewById(R.id.layHJ);
		layHJ.setVisibility(View.GONE);
		layTitle = (LinearLayout) findViewById(R.id.layTitle);
		layTitle.setVisibility(View.GONE);
		lineTitle = (View) findViewById(R.id.lineTitle);
		lineTitle.setVisibility(View.GONE);

		txtSUMJS = (TextView) findViewById(R.id.lblSUMJS); // 合计：件数
		txtSUMZL = (TextView) findViewById(R.id.lblSUMZL); // 合计：重量
		txtSUMTJ = (TextView) findViewById(R.id.lblSUMTJ); // 合计：体积

		btnAdd = (Button) findViewById(R.id.btn_Add);
		btnAdd.setEnabled(false);
		btnSavePhoto = (Button) findViewById(R.id.btn_SAVEPHOTO);
		btnSavePhoto.setEnabled(false);
		btnPortInput = (Button) findViewById(R.id.btn_port_input);
		btnPortInput.setVisibility(View.GONE);
		
		// add by yxq 2014/08/21 begin
		chkMatch = (CheckBox) findViewById(R.id.chkMatch);
		chkNotMatch = (CheckBox) findViewById(R.id.chkNotMatch);
		chkNoMarks = (CheckBox) findViewById(R.id.chkNoMarks);
		chkPortMarks = (CheckBox) findViewById(R.id.chkPortMarks);
		chkHydraulic = (CheckBox) findViewById(R.id.chkHydraulic);
		editHydraulic = (EditText) findViewById(R.id.editHydraulic);
		chkCdOrderDif = (CheckBox) findViewById(R.id.chkCdOrderDif);
		//箱单发票
		chkKarachiHasInvoice = (CheckBox) findViewById(R.id.chkKarachiHasInvoice);
		chkKarachiHasNotInvoice = (CheckBox) findViewById(R.id.chkKarachiHasNotInvoice);
		
		//黑色塑料桶
		chkBlackDrum = (CheckBox) findViewById(R.id.chkBlackDrum);
		//夹桶器
		chkDrumHandle = (CheckBox) findViewById(R.id.chkDrumHandle);
		
		
		mailSendFlg = false;    // 未发过邮件
		textMTDesc = (TextView) findViewById(R.id.textMTDesc);
		textMTDesc.setText(MTDesc);
		// add by yxq 2014/08/21 end
		
		gvInStore = (GridView) findViewById(R.id.grid_Insert);
		imageAdpter1 = new ImageAdapter(this, 0);
		imageAdpter1.add(new MoreData());
		gvInStore.setAdapter(imageAdpter1);
		// listImageId_Del1 = new ArrayList<String>();

		gvMt = (GridView) findViewById(R.id.grid_MT);
		imageAdpter2 = new ImageAdapter(this, 0);
		imageAdpter2.add(new MoreData());
		gvMt.setAdapter(imageAdpter2);
		// listImageId_Del2 = new ArrayList<String>();
		gvKarachi = (GridView) findViewById(R.id.grid_Karachi);
		imageAdpter3 = new ImageAdapter(this, 0);
		imageAdpter3.add(new MoreData());
		gvKarachi.setAdapter(imageAdpter3);		
		
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 列表控件
		listView = (ListView) findViewById(R.id.result_listView);

		GetListView();

		gvInStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE) { // 添加
					StartAction(1);
				} else { // 放大
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getData() != null) {
						GalleryAct.start(_thisActivity, imageData.getUrl());
					}
				}
			}
		});

		gvMt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE) { // 添加
					StartAction(2);
				} else { // 放大
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getData() != null) {
						GalleryAct.start(_thisActivity, imageData.getUrl());
					}
				}
			}
		});
		
		gvKarachi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE) { // 添加
					StartAction(7);
				} else { // 放大
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getData() != null) {
						GalleryAct.start(_thisActivity, imageData.getUrl());
					}
				}
			}
		});
		
		gvInStore
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int pos, long id) {
						/* 注销 by yxq 2014/08/26
						 if (!enFlag) {
							return true;
						}*/
						MoreData data = (MoreData) parent
								.getItemAtPosition(pos);
						if (data.getDataType() == MoreData.DATA_TYPE) {
							final int imagePos = pos;
							ImageData imageData = (ImageData) data.getData();
							if (imageData.getImageId() != null) {
								return true;
							}
							DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										MoreData data = (MoreData) imageAdpter1
												.getItem(imagePos);
										imageAdpter1.remove(data);
										// if (imageData.getImageId() != null) {
										// listImageId_Del1.add(imageData
										// .getImageId());
										// }
										imageAdpter1.notifyDataSetChanged();
									}
								}
							};

							new AlertDialog.Builder(_thisActivity).setItems(
									new String[] {
											getString(R.string.button_delete),
											getString(R.string.button_no) },
									lis).show();
						}
						return true;
					}
				});

		gvMt.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int pos, long id) {
				/* 注销 by yxq 2014/08/26 
				if (!enFlag) {
					return true;
				}*/
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
								MoreData data = (MoreData) imageAdpter2
										.getItem(imagePos);
								imageAdpter2.remove(data);
								// if (imageData.getImageId() != null) {
								// listImageId_Del2.add(imageData.getImageId());
								// }
								imageAdpter2.notifyDataSetChanged();
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

		gvKarachi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
								MoreData data = (MoreData) imageAdpter3
										.getItem(imagePos);
								imageAdpter3.remove(data);
								imageAdpter3.notifyDataSetChanged();
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

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int pos, long id) {
				final DN002ListData data = (DN002ListData) parent
						.getItemAtPosition(pos);
				dataList = data;
				// add by yxq 2014/09/24 begin
				strReaMeaFlg = "no";   // 复量功能放到增值服务画面
				// add by yxq 2014/09/24 end
				if (enFlag && bolFlgInStore) {
					// add by yxq 2014/09/17 begin
					if ("no".equals(strReaMeaFlg)){
				    // add by yxq 2014/09/17 end
						selItem = new String[] { getString(R.string.button_detail),
								getString(R.string.DN002_EDIT_BTN),
								getString(R.string.DN002_COPY_BTN),
								getString(R.string.DN002_DELETE_BTN),
								getString(R.string.DN002_ERROR_BTN) };
					// add by yxq 2014/09/16 begin
					}else{
						selItem = new String[] { getString(R.string.button_detail),
								getString(R.string.DN002_EDIT_BTN),
								getString(R.string.DN002_COPY_BTN),
								getString(R.string.DN002_DELETE_BTN),
								getString(R.string.DN002_ERROR_BTN),
								getString(R.string.DN002_REP_MEA_BTN)};
					}
					// add by yxq 2014/09/16 end
				} else {
					// add by yxq 2014/09/17 begin
					if ("no".equals(strReaMeaFlg)){
				    // add by yxq 2014/09/17 end
						selItem = new String[] { getString(R.string.button_detail),
								getString(R.string.DN002_ERROR_BTN) };
					// add by yxq 2014/09/16 begin
					}else{
						selItem = new String[] { getString(R.string.button_detail),
								getString(R.string.DN002_ERROR_BTN) ,
								getString(R.string.DN002_REP_MEA_BTN)};
					}
					// add by yxq 2014/09/16 end
				}
				if (!bolRole && !bolIsTally) {
					// add by yxq 2014/09/17 begin
					if ("no".equals(strReaMeaFlg)){
						selItem = new String[] { getString(R.string.button_detail)};
					}else{
					// add by yxq 2014/09/17 end
						selItem = new String[] { getString(R.string.button_detail),
								getString(R.string.DN002_REP_MEA_BTN)};
					}
				}

				DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: // 查看
							Intent detailIntent = new Intent(_thisActivity,
									DN004Activity.class);
							Bundle detailBundle = new Bundle();
							detailBundle.putString("Type", "5"); // 更新标识
							detailBundle.putBoolean("bolFlag", enFlag);
							detailBundle.putString("cdOrder", cdOrder); // 进仓编号个别
							detailBundle.putString("cdOrderPublic",
									cdOrderPublic); // 进仓编号共通
							detailBundle.putString("depotID", depotID); // 进仓ID
							detailBundle.putString("noBatch", noBatch); // 批次
							detailBundle.putString("thNo", txtThNo.getText()
									.toString()); // 同行编号
							detailBundle.putString("depotDtID",
									data.getDepotDtID());
							detailBundle.putString("noPilecard",
									data.getNoPilecard());
							detailIntent.putExtras(detailBundle);
							startActivity(detailIntent);
							break;
						case 1: // 编辑
							if (selItem[1]
									.equals(getString(R.string.DN002_EDIT_BTN))) {
								CheckDepotDtDxist(data, "编辑", 1);
							} 
							// add by yxq 2014/09/16 begin
							else if(getString(R.string.DN002_REP_MEA_BTN).equals(selItem[1])){  // 复量
								RepeatedMeasure(data);   // 跳转到复量 页面
								break;
							}
							// add by yxq 2014/09/16 end
							else {
								// 页面跳转
								Intent errotItent = new Intent(_thisActivity,
										DN005Activity.class);
								Bundle errorBundle = new Bundle();
								errorBundle.putBoolean("bolFlag", false);
								errorBundle.putString("noPilecard",
										data.getNoPilecard()); // 选择项桩脚牌ID
								errorBundle.putString("depotDtID",
										data.getDepotDtID()); // 货物明细ID
								errorBundle.putString("depotID", depotID);
								errorBundle.putString("coOrder", cdOrderPublic); // 进仓编号
								errorBundle.putString("noBatch", noBatch); // 批次
								errorBundle.putString("thNo", txtThNo.getText()
										.toString()); // 同行编号
								errotItent.putExtras(errorBundle);
								startActivity(errotItent);
							}
							break;
						case 2: // 复制
							// add by yxq 2014/09/16 begin
							if(getString(R.string.DN002_REP_MEA_BTN).equals(selItem[2])){  // 复量
								RepeatedMeasure(data);   // 跳转到复量 页面
								break;
							}
							// add by yxq 2014/09/16 end
							// zxx modify
							if (!bolFlag) {
								showDoubleDialog(_thisActivity,
										getString(R.string.DN002_002_MSG), 1);
							} else {
								actionAfterOk(1, 0);
							}

							break;
						case 3: // 删除桩脚牌
							CheckDepotDtDxist(data, "删除", 2);
							break;
						case 4: // 异常信息
							// 页面跳转
							Intent errotItent = new Intent(_thisActivity,
									DN005Activity.class);
							Bundle errorBundle = new Bundle();
							errorBundle.putBoolean("bolFlag", true);
							errorBundle.putString("noPilecard",
									data.getNoPilecard()); // 选择项桩脚牌ID
							errorBundle.putString("depotDtID",
									data.getDepotDtID()); // 货物明细ID
							errorBundle.putString("depotID", depotID);
							errorBundle.putString("coOrder", cdOrderPublic); // 进仓编号
							errorBundle.putString("noBatch", noBatch); // 批次
							errorBundle.putString("thNo", txtThNo.getText()
									.toString()); // 同行编号
							errotItent.putExtras(errorBundle);
							startActivity(errotItent);
							break;
						case 5: // 复量
							RepeatedMeasure(data);   // 跳转到复量 页面
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

		// 点击查看桩脚牌
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				DN002ListData data = (DN002ListData) listView
						.getItemAtPosition(arg2);
				Intent detailIntent = new Intent(_thisActivity,
						DN004Activity.class);
				Bundle detailBundle = new Bundle();
				detailBundle.putString("Type", "5"); // 更新标识
				detailBundle.putBoolean("bolFlag", enFlag);
				detailBundle.putString("cdOrder", cdOrder); // 进仓编号个别
				detailBundle.putString("cdOrderPublic", cdOrderPublic); // 进仓编号共通
				detailBundle.putString("depotID", depotID); // 进仓ID
				detailBundle.putString("noBatch", noBatch); // 批次
				detailBundle.putString("thNo", txtThNo.getText().toString()); // 同行编号
				detailBundle.putString("depotDtID", data.getDepotDtID());
				detailBundle.putString("noPilecard", data.getNoPilecard());
				detailIntent.putExtras(detailBundle);
				startActivity(detailIntent);
			}
		});

		// add by yxq 2014/08/21 begin
		// 唛头相符
		chkMatch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
			        chkNotMatch.setChecked(!((CheckBox) v).isChecked());
			        chkNoMarks.setChecked(!((CheckBox) v).isChecked());
			        //chkPortMarks.setChecked(!((CheckBox) v).isChecked());
			        //btnPortInput.setVisibility(View.GONE);
				}
			}
		});
		// 唛头不符
		chkNotMatch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
				    chkMatch.setChecked(!((CheckBox) v).isChecked());
				    chkNoMarks.setChecked(!((CheckBox) v).isChecked());
				    //chkPortMarks.setChecked(!((CheckBox) v).isChecked());
				}
				/*else{
					if(chkPortMarks.isChecked()){
					    chkPortMarks.setChecked(false);
					    btnPortInput.setVisibility(View.GONE);
					}
				}*/
			}
		});
		// 有箱单、发票 
		chkKarachiHasInvoice.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
					chkKarachiHasNotInvoice.setChecked(!((CheckBox) v).isChecked());
				}
			}
		});
		// 无箱单、发票
		chkKarachiHasNotInvoice.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
					chkKarachiHasInvoice.setChecked(!((CheckBox) v).isChecked());
				}
			}
		});
		//无唛头
		chkNoMarks.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
				    chkMatch.setChecked(!((CheckBox) v).isChecked());
				    chkNotMatch.setChecked(!((CheckBox) v).isChecked());
				    chkPortMarks.setChecked(!((CheckBox) v).isChecked());
				    btnPortInput.setVisibility(View.GONE);
				}
			}
		});
		// add by yxq 2014/08/21 end
		chkPortMarks.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()){
					/*chkMatch.setChecked(!((CheckBox) v).isChecked());
					chkNotMatch.setChecked(((CheckBox) v).isChecked());*/
				    chkNoMarks.setChecked(!((CheckBox) v).isChecked());
				    btnPortInput.setVisibility(View.VISIBLE);
				}else{
					btnPortInput.setVisibility(View.GONE);
				}
			}
		});
		
		GetErrorListIsExist();
	}

	/*
	 * 判断桩脚牌是否可以删除
	 */
	private void CheckDepotDtDxist(final DN002ListData data, String strType,
			final int type) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotDtID", data.getDepotDtID());
		p1.put("strType", strType);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN002_CheckDepotDtDxist", p1, new TypeToken<String>() {
				}.getType(), new AmassHttpResponseHandler<String>() {

					@Override
					protected void OnSuccess(String response) {
						super.OnSuccess(response);
						if (response.length() > 0) {
							Utils.showAlertDialog(_thisActivity, response);
							return;
						} else {
							if (type == 1) {
								Intent editIntent = new Intent(_thisActivity,
										DN004Activity.class);
								Bundle editBundle = new Bundle();
								editBundle.putString("Type", "1"); // 更新标识
								editBundle.putString("cdOrder", cdOrder); // 进仓编号个别
								editBundle.putString("cdOrderPublic",
										cdOrderPublic); // 进仓编号共通
								editBundle.putString("depotID", depotID); // 进仓ID
								editBundle.putString("noBatch", noBatch); // 批次
								editBundle.putString("thNo", txtThNo.getText()
										.toString()); // 同行编号
								editBundle.putString("depotDtID",
										data.getDepotDtID());
								editBundle.putString("noPilecard",
										data.getNoPilecard());
								editBundle.putString("CarNo", CarNo); // 车牌号
								editBundle.putString("CarType", CarType); // 车辆类型
								editBundle.putString("NoMultipleOrder",
										NoMultipleOrder); // 分票号
								editBundle.putString("NoCarNum", NoCarNum); // 同车编号
								editBundle.putString("NoMultipleDepot",
										   NoMultipleDepot); //进仓分票号   // add yxq 2014/09/03
								editBundle.putString("NoMultipleColo",
										NoMultipleColo);
								editIntent.putExtras(editBundle);
								startActivityForResult(editIntent, 5);
							} else if (type == 2) {
								DeleteNoPileCard(data.getDepotDtID());
							}
						}
					}
				}, false);
	}

	/*
	 * 删除桩脚牌
	 */
	private void DeleteNoPileCard(final String depotDtID) {
		// 弹出确认框
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(getString(R.string.DN002_001_MSG))
				.setPositiveButton(getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Map<String, Object> p1 = new HashMap<String, Object>();
								p1.put("depotDtID", depotDtID);
								p1.put("depotID", depotID);
								p1.put("cdOrder", cdOrderPublic);

								NetworkHelper
										.getInstance()
										.postJsonData(
												_thisActivity,
												"DN002_DeleteNoPileCard",
												p1,
												new TypeToken<DN002ListData>() {
												}.getType(),
												new AmassHttpResponseHandler<DN002ListData>() {

													@Override
													protected void OnSuccess(
															DN002ListData response) {
														super.OnSuccess(response);
														Utils.showAlertDialog(
																_thisActivity,
																getString(R.string.msg_delete_success));
														sumJS = 0;
														sumZL = 0;
														sumTJ = 0;
														// imageAdpter1.clear();
														// imageAdpter1
														// .add(new MoreData());
														// imageAdpter2.clear();
														// imageAdpter2
														// .add(new MoreData());
														GetDepotData();
													}
												}, true);
							}
						})
				.setNegativeButton(getString(R.string.button_no), null).show();
	}

	@SuppressWarnings("rawtypes")
	private void GetListView() {
		adapter = new ArrayAdapter(getApplicationContext(), 0) {

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				LinearLayout layout = new LinearLayout(getContext());
				DN002ListData item = (DN002ListData) getItem(position);
				mInflater.inflate(R.layout.dn002_list_view_item, layout, true);

				TextView t1 = (TextView) layout
						.findViewById(R.id.txtNoPileCard);
				t1.setText(item.getNoPilecard()); //
				t1 = (TextView) layout.findViewById(R.id.txtJS);
				t1.setText(String.valueOf(item.getDepotNum())); //
				t1 = (TextView) layout.findViewById(R.id.txtZL);
				t1.setText(ChangeDouble(item.getKgs(), 3, 3)); //
				t1 = (TextView) layout.findViewById(R.id.txtTJ);
				t1.setText(ChangeDouble(item.getCbm(), 4, 4)); //
				t1 = (TextView) layout.findViewById(R.id.txt_depotDate);
				if(item.getDepotDate() != null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					t1.setText(sdf.format(item.getDepotDate()));
				}

				int[] colors = { R.color.listview_back_odd,
						R.color.listview_back_uneven };
				layout.setBackgroundResource(colors[position % 2]);

				return layout;
			}
		};

		listView.setAdapter(adapter);

		GetDepotData();
	}

	private String ChangeDouble(double d, int scale, int dLen) {
		BigDecimal b = new BigDecimal(d);
		double f = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(dLen);
		return nf.format(f);
	}

	/*
	 * 获取明细数据
	 */
	private void GetDepotData() {
		try {
			Map<String, Object> p1 = new HashMap<String, Object>();
			p1.put("depotID", depotID);
			p1.put("noBatch", noBatch);
			p1.put("cdOrder", cdOrderPublic);

			NetworkHelper.getInstance().postJsonData(_thisActivity,
					"DN002_GetDepotDetailAndPhotoData", p1,
					new TypeToken<DepotDN002>() {
					}.getType(), new AmassHttpResponseHandler<DepotDN002>() {

						@SuppressWarnings("unchecked")
						@Override
						protected void OnSuccess(DepotDN002 response) {
							super.OnSuccess(response);
							btnAdd.setEnabled(bolFlgInStore);
							// edit by yxq 2014/08/26 begin
							// 原代码 btnSavePhoto.setEnabled(bolFlgInStore);
							btnSavePhoto.setEnabled(true);
							// edit by yxq 2014/08/26 end
							// 获取数据
							DepotDN002 depot = (DepotDN002) response;
							adapter.clear();
							// 判断是否有数据
							if (depot != null) {
								boolean btnFlag = false;
								// 明细数据
								list = depot.getDn002List();

								if (list != null && list.size() > 0) {
									DN002ListData dn = list.get(0);
									CarNo = dn.getNoCarLice(); // 车牌号
									CarType = dn.getTypeCar(); // 车辆类型
									NoMultipleOrder = dn.getNoMultipleOrder(); // 分票号
									NoCarNum = dn.getNoCarNum(); // 同车编号
									// add yxq 2014/09/03 begin
									NoMultipleDepot = dn.getNoMultipleDepot();  //进仓分票号  
									// add yxq 2014/09/03 end
									NoMultipleColo = dn.getNoMultipleColo();
									sumJS = 0;
									sumZL = 0;
									sumTJ = 0;

									for (DN002ListData info : list) {
										sumJS += info.getDepotNum();
										sumZL += info.getKgs();
										sumTJ += info.getCbm();
										if (info.isStatus()) {
											btnFlag = true;
										}
									}

									if (btnFlag || !bolFlgInStore) {
										btnAdd.setEnabled(false);
										// edit by yxq 2014/08/26 begin
										// btnSavePhoto.setEnabled(false);
										btnSavePhoto.setEnabled(true);
										// edit by yxq 2014/08/26 end
										enFlag = false;
									}

									layHJ.setVisibility(View.VISIBLE);
									layTitle.setVisibility(View.VISIBLE);
									lineTitle.setVisibility(View.VISIBLE);
									txtSUMJS.setText(String.valueOf(sumJS)); // 合计：件数
									txtSUMZL.setText(ChangeDouble(sumZL, 3, 3)); // 合计：重量
									txtSUMTJ.setText(ChangeDouble(sumTJ, 4, 4)); // 合计：体积

									adapter.addAll(list);
									adapter.notifyDataSetChanged();
								} else {
									layHJ.setVisibility(View.GONE);
									layTitle.setVisibility(View.GONE);
									lineTitle.setVisibility(View.GONE);
									
									// add by yxq 2014/08/26 begin
									CarNo = depot.getNoCarLice(); 				  // 车牌号
									CarType = depot.getTypeCar(); 				  // 车辆类型
									NoMultipleOrder = depot.getNoMultipleOrder(); // 分票号
									NoCarNum = depot.getNoCarNum();               // 同车编号
									// add by yxq 2014/08/26 end
									// add yxq 2014/09/03 begin
									NoMultipleDepot = depot.getNoMultipleDepot();  //进仓分票号  
									// add yxq 2014/09/03 end
									NoMultipleColo = depot.getNoMultipleColo();
								}

								imageAdpter1.clear();
								imageAdpter1.add(new MoreData());
								imageAdpter2.clear();
								imageAdpter2.add(new MoreData());
								imageAdpter3.clear();
								imageAdpter3.add(new MoreData());

								if (depot.getDn002InFileList() != null
										&& depot.getDn002InFileList().size() > 0) {
									// 循环加载进仓照片
									for (FileManageData file : depot
											.getDn002InFileList()) {
										ImageData data = new ImageData();
										data.setImageId(file.getFileId());
										data.setImageDesc(file.getFileName());
										MoreData d = new MoreData(data);
										//imageAdpter1.insert(d,imageAdpter1.getCount() - 1);
										imageAdpter1.insert(d,1);
									}
								}

								if (depot.getDn002MtFileList() != null
										&& depot.getDn002MtFileList().size() > 0) {
									// 循环加载唛头照片
									for (FileManageData fielMt : depot
											.getDn002MtFileList()) {
										ImageData data = new ImageData();
										data.setImageId(fielMt.getFileId());
										data.setImageDesc(fielMt.getFileName());
										MoreData d = new MoreData(data);
										//imageAdpter2.insert(d,imageAdpter2.getCount() - 1);
										imageAdpter2.insert(d,1);
									}
								}
								if (depot.getDn002KiFileList() != null
										&& depot.getDn002KiFileList().size() > 0) {
									// 循环加载箱单发票照片
									for (FileManageData fielKi : depot
											.getDn002KiFileList()) {
										ImageData data = new ImageData();
										data.setImageId(fielKi.getFileId());
										data.setImageDesc(fielKi.getFileName());
										MoreData d = new MoreData(data);
										imageAdpter3.insert(d,1);
									}
								}
							}
							
							// add by yxq 2014/08/21 begin
							// 唛头
							if("MarkOkPortOk".equals(depot.getMTMatchFlg())){        // 唛头相符,港口相符
								chkMatch.setChecked(true);
								chkNotMatch.setChecked(false);
								chkNoMarks.setChecked(false);
								chkPortMarks.setChecked(false);
							}else if("MarkOkPortNg".equals(depot.getMTMatchFlg())){  // 唛头相符,港口不符
								chkMatch.setChecked(true);
								chkNotMatch.setChecked(false);
								chkNoMarks.setChecked(false);
								chkPortMarks.setChecked(true);
								btnPortInput.setVisibility(View.VISIBLE);
								for(int i=0;i<depot.getDn002List().size();i++){
									if(depot.getDn002List().get(i).getMarksRealPort()!=null 
											&& !"".equals(depot.getDn002List().get(i).getMarksRealPort())){
										realPortInput = depot.getDn002List().get(i).getMarksRealPort();
										break;
									}
								}	
							}else if("MarkNgPortNg".equals(depot.getMTMatchFlg())){ // 唛头、港口不符
								chkMatch.setChecked(false);
								chkNotMatch.setChecked(true);
								chkNoMarks.setChecked(false);	
								chkPortMarks.setChecked(true);
								btnPortInput.setVisibility(View.VISIBLE);
								for(int i=0;i<depot.getDn002List().size();i++){
									if(depot.getDn002List().get(i).getMarksRealPort()!=null 
											&& !"".equals(depot.getDn002List().get(i).getMarksRealPort())){
										realPortInput = depot.getDn002List().get(i).getMarksRealPort();
										break;
									}
								}								
							}else if("MarkNgPortOk".equals(depot.getMTMatchFlg())){ // 不符
								chkMatch.setChecked(false);
								chkNotMatch.setChecked(true);
								chkNoMarks.setChecked(false);
								chkPortMarks.setChecked(false);
							}else if("NoMark".equals(depot.getMTMatchFlg())){ // 无唛头
								chkMatch.setChecked(false);
								chkNotMatch.setChecked(false);
								chkNoMarks.setChecked(true);
								chkPortMarks.setChecked(false);
							}else if("NonePortOk".equals(depot.getMTMatchFlg())){											  // 没有桩脚牌
								chkMatch.setChecked(false);
								chkNotMatch.setChecked(false);   
								chkNoMarks.setChecked(false);
								chkPortMarks.setChecked(false);
							}else if("NonePortNg".equals(depot.getMTMatchFlg())){											  // 没有桩脚牌
								chkMatch.setChecked(false);
								chkNotMatch.setChecked(false);   
								chkNoMarks.setChecked(false);
								chkPortMarks.setChecked(true);
								btnPortInput.setVisibility(View.VISIBLE);
								for(int i=0;i<depot.getDn002List().size();i++){
									if(depot.getDn002List().get(i).getMarksRealPort()!=null 
											&& !"".equals(depot.getDn002List().get(i).getMarksRealPort())){
										realPortInput = depot.getDn002List().get(i).getMarksRealPort();
										break;
									}
								}	
							}
							// add by yxq 2014/08/21 end
							// add by yxq 2014/09/17 begin
							strReaMeaFlg = depot.getReaMeaFlg();
							// add by yxq 2014/09/17 end
							
							//液压车
							if (depot.getFlgHydraulic() == null){
								chkHydraulic.setChecked(false);
							}
							else {
								chkHydraulic.setChecked(depot.getFlgHydraulic());
							}

							Integer nHydraulic = depot.getNumHydraulic();
							if (nHydraulic == null) {
								editHydraulic.setText("0");
							}
							else{
								editHydraulic.setText(nHydraulic.toString().trim());
							}
							
							//进仓编号不符
							if (depot.getFlgCdOrderDif() == null){
								chkCdOrderDif.setChecked(false);
							}
							else {
								chkCdOrderDif.setChecked(depot.getFlgCdOrderDif());
							}
							
							//KARACHI港货物是否有箱单和发票
							if (depot.getFlgKarachiInvoice() == null){
								chkKarachiHasInvoice.setChecked(false);
								chkKarachiHasNotInvoice.setChecked(false);
							}
							else{
								chkKarachiHasInvoice.setChecked(depot.getFlgKarachiInvoice());
								chkKarachiHasNotInvoice.setChecked(!depot.getFlgKarachiInvoice());
							}
							
							//是否是黑色塑料桶包装
							if (depot.getFlgBlackDrum() == null){
								chkBlackDrum.setChecked(false);
							}
							else{
								chkBlackDrum.setChecked(depot.getFlgBlackDrum());
							}
							
							//是否用了夹桶器
							if (depot.getFlgDrumHandle() == null){
								chkDrumHandle.setChecked(false);
							}
							else {
								chkDrumHandle.setChecked(depot.getFlgDrumHandle());
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

	/*
	 * 新增按钮事件
	 */
	public void Btn_Add_OnClick(View view) {
		// zxx modify
		if (!bolFlag) {
			showDoubleDialog(_thisActivity, getString(R.string.DN002_002_MSG),
					0);
		} else {
			actionAfterOk(0, 0);
		}
	}

	/*
	 * 开启相机拍照功能
	 */
	private void StartAction(int type) {
		// 注销 by yxq 2014/08/26 if (enFlag) {
			Intent intent = new Intent();
			// 指定开启系统相机的Action
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			File dir = new File(Environment.getExternalStorageDirectory(),
					"amass/pics/dn002");
			// dir.mkdirs();
			cameraTempFile = new File(dir, UUID.randomUUID() + ".jpg");
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(cameraTempFile));

			startActivityForResult(intent, type);
		//}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			ImageSize size = new ImageSize(100, 100);
			switch (requestCode) {
			// 进仓拍照
			case 1:
				String path = cameraTempFile.getAbsolutePath();
				Utils.compressImage(path); // 压缩图片
				String displyPath = path.startsWith("/") ? "file://" + path
						: path;
				Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(
						displyPath, size);
				ImageData imageData = new ImageData();
				imageData.setData(thumbnail);
				imageData.setPath(path);
				imageData.setImageDesc("新拍\n照片");
				imageData.setUrl(displyPath);

				MoreData item = new MoreData(imageData);

				//imageAdpter1.insert(item, imageAdpter1.getCount() - 1);
				imageAdpter1.insert(item, 1);
				imageAdpter1.notifyDataSetChanged();
				break;
			// 唛头拍照
			case 2:
				String path2 = cameraTempFile.getAbsolutePath();
				Utils.compressImage(path2); // 压缩图片
				String displyPath2 = path2.startsWith("/") ? "file://" + path2
						: path2;
				Bitmap thumbnail2 = ImageLoader.getInstance().loadImageSync(
						displyPath2, size);
				ImageData imageData2 = new ImageData();
				imageData2.setData(thumbnail2);
				imageData2.setPath(path2);
				imageData2.setImageDesc("新拍\n照片");
				imageData2.setUrl(displyPath2);

				MoreData item2 = new MoreData(imageData2);
				//imageAdpter2.insert(item2, imageAdpter2.getCount() - 1);
				imageAdpter2.insert(item2, 1);
				imageAdpter2.notifyDataSetChanged();
				break;
			// 新增返回
			case 4:
				initializeView();
				// imageAdpter1 = new ImageAdapter(this, 0);
				// imageAdpter1.add(new MoreData());
				// imageAdpter2 = new ImageAdapter(this, 0);
				// imageAdpter2.add(new MoreData());
				// btnSavePhoto.setEnabled(false);
				// btnAdd.setEnabled(false);
				// GetDepotData();
				AddType = "1";
				break;
			// 编辑返回
			case 5:
				initializeView();
				// imageAdpter1 = new ImageAdapter(this, 0);
				// imageAdpter1.add(new MoreData());
				// imageAdpter2 = new ImageAdapter(this, 0);
				// imageAdpter2.add(new MoreData());
				// btnSavePhoto.setEnabled(false);
				// btnAdd.setEnabled(false);
				// GetDepotData();
				AddType = "1";
				break;
			// 复制返回
			case 6:
				initializeView();
				// imageAdpter1 = new ImageAdapter(this, 0);
				// imageAdpter1.add(new MoreData());
				// imageAdpter2 = new ImageAdapter(this, 0);
				// imageAdpter2.add(new MoreData());
				// btnSavePhoto.setEnabled(false);
				// btnAdd.setEnabled(false);
				// GetDepotData();
				AddType = "1";
				break;
			// 装箱单发票照片
			case 7:
				String path3 = cameraTempFile.getAbsolutePath();
				Utils.compressImage(path3); // 压缩图片
				String displyPath3 = path3.startsWith("/") ? "file://" + path3
						: path3;
				Bitmap thumbnail3 = ImageLoader.getInstance().loadImageSync(
						displyPath3, size);
				ImageData imageData3 = new ImageData();
				imageData3.setData(thumbnail3);
				imageData3.setPath(path3);
				imageData3.setImageDesc("新拍\n照片");
				imageData3.setUrl(displyPath3);

				MoreData item3 = new MoreData(imageData3);
				imageAdpter3.insert(item3, 1);
				imageAdpter3.notifyDataSetChanged();
				break;
			}
		}
	}

	/*
	 * 照片保存按钮事件
	 */
	public void Btn_SavePhoto_OnClick(View view) {
		try {
			boolean bolFlag1 = false;
			boolean bolFlag2 = false;
			boolean bolFlag3 = false;
			for (int nIndex = 0; nIndex < imageAdpter1.getCount(); nIndex++) {
				MoreData data = imageAdpter1.getItem(nIndex);
				if (data.getDataType() == MoreData.DATA_TYPE) {
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getImageId() == null) {
						bolFlag1 = true;
					}
				}
			}
			// if (listImageId_Del1.size() > 0) {
			// bolFlag1 = true;
			// }

			for (int nIndex = 0; nIndex < imageAdpter2.getCount(); nIndex++) {
				MoreData data = imageAdpter2.getItem(nIndex);
				if (data.getDataType() == MoreData.DATA_TYPE) {
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getImageId() == null) {
						bolFlag2 = true;
					}
				}
			}
			//检查是否有未保存的照片
			for (int nIndex = 0; nIndex < imageAdpter3.getCount(); nIndex++) {
				MoreData data = imageAdpter3.getItem(nIndex);
				if (data.getDataType() == MoreData.DATA_TYPE) {
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getImageId() == null) {
						bolFlag3 = true;
					}
				}
			}
			// if (listImageId_Del2.size() > 0) {
			// bolFlag2 = true;
			// }

/*			// add by yxq 2014/08/21 begin
			// 是否拍了唛头照片
			if(bolFlag2){
				// 检查唛头相符是否勾选
				if(!chkMatch.isChecked()&&!chkNotMatch.isChecked()){
					// 请选择唛头是否相符
					Utils.showAlertDialog(_thisActivity,getString(R.string.DN002_003_MSG));
					return;
				}
				UpdateMarksDif();  // 更新唛头相符标识
			}
			// add by yxq 2014/08/21 end
*/			
			
			if (chkHydraulic.isChecked() 
					&& ("".equals(editHydraulic.getText().toString().trim()) || "0".equals(editHydraulic.getText().toString().trim()))){
				Utils.showAlertDialog(_thisActivity,getString(R.string.DN002_004_MSG));
				return;	
			}
			if (!chkHydraulic.isChecked()){
				editHydraulic.setText("0");
			}
			if(list.size() > 0){
				
				//如果港口是KARACHI，必须要选择是否有箱单和发票
				if (nmWpod.equalsIgnoreCase("KARACHI") && !chkKarachiHasInvoice.isChecked() && !chkKarachiHasNotInvoice.isChecked()){
					Utils.showAlertDialog(_thisActivity,getString(R.string.DN002_error_KarachiInvoicePic));
					return;					
				}
				
				//如果勾选了“有箱单、发票”，必须要拍照
				if (chkKarachiHasInvoice.isChecked() && imageAdpter3.getCount() == 1){
					Utils.showAlertDialog(_thisActivity,getString(R.string.DN002_error_NoKarachiInvoicePic));
					return;
				}
				
				if(imageAdpter1 != null && imageAdpter1.getCount() > 1)
				{
					if(!chkMatch.isChecked()&&!chkNotMatch.isChecked()
							&&!chkNoMarks.isChecked()&& !chkPortMarks.isChecked()){
						// 请选择唛头是否相符
						//if("AMI020133".equals(strCdWareHouse)){         // 一号库必须唛头照片，二号库暂时只拍进仓照片
						Utils.showAlertDialog(_thisActivity,getString(R.string.DN002_003_MSG));
						return;
						//}
					//}else if(chkNoMarks.isChecked()){
					//	UpdateMarksDif();  // 更新唛头相符标识
					}else if(chkMatch.isChecked()||chkNotMatch.isChecked()||chkPortMarks.isChecked()||chkNoMarks.isChecked()){
						//if("AMI020133".equals(strCdWareHouse)){         // 一号库
						if(imageAdpter2 != null && imageAdpter2.getCount() > 1){
							UpdateMarksDif();  // 更新唛头相符标识
						}else{
							Utils.showAlertDialog(_thisActivity,getString(R.string.DN002_error_NoMarksPic));
							return;
						}
						//}else{
						//	UpdateMarksDif();  // 更新唛头相符标识
						//}
					}
					
					//保存箱单发票照片
					if (bolFlag3){
						SavePicture3("KI", imageAdpter3);
					}
					if (bolFlag1 && !bolFlag2) {
						SavePicture1("JC", imageAdpter1);
					} else if (!bolFlag1 && bolFlag2) {
						SavePicture1("MT", imageAdpter2);
					} else if (bolFlag1 && bolFlag2) {
						SavePicture2("JC", imageAdpter1);
					}

				}else{
					Utils.showAlertDialog(_thisActivity,getString(R.string.DN002_error_NoOrderPic));
					return;
				}
			}
			else{
				Utils.showAlertDialog(_thisActivity,getString(R.string.DN002_error_NoPileCard));
			}
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * 保存图片
	 */
	private void SavePicture1(String typeName, final ImageAdapter imageAdpter) {
		RequestParams params = new RequestParams();
		params.put("cdOrder", cdOrderPublic);
		params.put("coLoader", txtThNo.getText().toString().trim());
		params.put("depotId", depotID);
		params.put("noBatch", noBatch);
		//照片名序列，用于排序照片
		String imgOrderList = "";

		/*for (int nIndex = 0; nIndex < imageAdpter.getCount(); nIndex++) {
			MoreData data = imageAdpter.getItem(nIndex);
			if (data.getDataType() == MoreData.DATA_TYPE) {
				ImageData imageData = (ImageData) data.getData();
				if (imageData.getImageId() == null) {
					try {
						File f = new File(imageData.getPath());
						imageData.setFileUploadId(f.getName());
						imgOrderList = imgOrderList + f.getName()+",";
						params.put(imageData.getFileUploadId(), f);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}*/
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
		// for (int i = 0; i < listImageId_Del.size(); i++) {
		// imageId_Del = imageId_Del + listImageId_Del.get(i) + ",";
		// }

		params.put("imageId_Del", imageId_Del);
		NetworkHelper.getInstance().postFilesData(this, "DN002_UploadFiles",
				typeName, params, new TypeToken<HashMap<String, String>>() {
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
										image.setImageId(imageId);
									}
								}
							}
						}
						if(!alertFlg){
						    Utils.showAlertDialog(_thisActivity,getString(R.string.msg_save_success));
						}else{
							alertFlg = false;
						}
						GetDepotData();
					}
				}, true);
	}

	/*
	 * 保存图片
	 */
	private void SavePicture2(String typeName, final ImageAdapter imageAdpter) {
		RequestParams params = new RequestParams();
		params.put("cdOrder", cdOrderPublic);
		params.put("depotId", depotID);
		params.put("noBatch", noBatch);
		//照片名序列，用于排序照片
		String imgOrderList = "";
		
		/*for (int nIndex = 0; nIndex < imageAdpter.getCount(); nIndex++) {
			MoreData data = imageAdpter.getItem(nIndex);
			if (data.getDataType() == MoreData.DATA_TYPE) {
				ImageData imageData = (ImageData) data.getData();
				if (imageData.getImageId() == null) {
					try {
						File f = new File(imageData.getPath());
						imageData.setFileUploadId(f.getName());
						imgOrderList = imgOrderList + f.getName()+",";
						params.put(imageData.getFileUploadId(), f);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}*/
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
		// for (int i = 0; i < listImageId_Del.size(); i++) {
		// imageId_Del = imageId_Del + listImageId_Del.get(i) + ",";
		// }

		params.put("imageId_Del", imageId_Del);
		NetworkHelper.getInstance().postFilesData(this, "DN002_UploadFiles",
				typeName, params, new TypeToken<HashMap<String, String>>() {
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
										image.setImageId(imageId);
									}
								}
							}
						}
						SavePicture1("MT", imageAdpter2);
					}
				}, true);
	}
	
	private void SavePicture3(String typeName, final ImageAdapter imageAdpter) {
		RequestParams params = new RequestParams();
		params.put("cdOrder", cdOrderPublic);
		params.put("depotId", depotID);
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
				typeName, params, new TypeToken<HashMap<String, String>>() {
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
										image.setImageId(imageId);
									}
								}
							}
						}
					}
				}, true);
	}


	/*
	 * 返回事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			Intent retIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putBoolean("btnFlag", btnFlag);
			bundle.putBoolean("bolFlgInStore", bolFlgInStore);
			retIntent.putExtras(bundle);
			// 按下的如果是BACK，同时没有重复
			setResult(Activity.RESULT_OK, retIntent);
			this.finish();

		}
		return super.onKeyDown(keyCode, event);
	}

/*	
	 //获取异常报告 zxx modify 
	 
	private void GetErrorListIsExist() {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("deportID", depotID);
		p1.put("cdOrder", cdOrderPublic);

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN003_GetErrorListByDepotID", p1, new TypeToken<DepotDN003>() {
				}.getType(), new AmassHttpResponseHandler<DepotDN003>() {

					@Override
					protected void OnSuccess(DepotDN003 response) {
						super.OnSuccess(response);
						// 获取数据
						DepotDN003 depot = (DepotDN003) response;
						// 判断是否有数据
						if (depot == null || depot.getDn003List() == null
								|| depot.getDn003List().size() == 0) {

							bolFlag = true;
						} else {
							List<DN003ListData> dn003ListDatas = depot
									.getDn003List();
							int numFlgStatus = 0;
							for (DN003ListData dn003ListData : dn003ListDatas) {
								if ("待收货".equals(dn003ListData.getFlgStatus())) {
									numFlgStatus += 1;
								}
							}
							if (numFlgStatus == 0) {
								bolFlag = true;
							} else {
								bolFlag = false;
							}
						}
					}
				}, false);
	}*/
	
	private void GetErrorListIsExist() {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depotId", depotID);
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN003_hasErrorList", p1, CommonResult.class,
				new AmassHttpResponseHandler<CommonResult>() {
			@Override
			protected void OnSuccess(CommonResult response) {
				super.OnSuccess(response);
				CommonResult result = (CommonResult) response;
				if (result.getFlag()) {
					bolFlag = true;
				} else {
					bolFlag = false;
				}
			}
		},false);
	}

	public void showDoubleDialog(final Context context, String str,
			final int dialogId) {
		new AlertDialog.Builder(new ContextThemeWrapper(context,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setMessage(str)
				.setPositiveButton(R.string.button_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// DoubleDialog(yes/no)
								actionAfterOk(dialogId, 0);
							}
						})
				.setCancelable(false)
				.setNegativeButton(R.string.button_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// DoubleDialog(yes/no)
								actionAfterOk(dialogId, 1);

							}
						}).show();

	}

	/**
	 * DoubleDialog(yes/no)
	 * 
	 * @param dialogId
	 *            “0”代表确定，“1”代表取消
	 */
	public void actionAfterOk(int dialogId, int i) {

		// dialogId==0 系统MEN选中
		if (dialogId == 0) {
			// 标识
			if (i == 0) {
				// 页面跳转 复制
				Intent intent = new Intent(_thisActivity, DN004Activity.class);
				Bundle bundle = new Bundle();
				bundle.putString("AddType", AddType);
				bundle.putString("Type", "0"); // 新增标识
				bundle.putString("cdOrder", cdOrder); // 进仓编号个别
				bundle.putString("cdOrderPublic", cdOrderPublic); // 进仓编号共通
				bundle.putString("depotID", depotID); // 进仓ID
				bundle.putString("noBatch", noBatch); // 批次
				bundle.putString("thNo", txtThNo.getText().toString()); // 同行编号
				bundle.putString("CarNo", CarNo); // 车牌号
				bundle.putString("CarType", CarType); // 车辆类型
				bundle.putString("NoMultipleOrder", NoMultipleOrder); // 分票号
				bundle.putString("NoCarNum", NoCarNum); // 同车编号
				// add yxq 2014/09/03 begin
				bundle.putString("NoMultipleDepot", NoMultipleDepot);//进仓分票号  
				// add yxq 2014/09/03 end
				bundle.putString("NoMultipleColo", NoMultipleColo);
				intent.putExtras(bundle);
				startActivityForResult(intent, 4);
			}
		} else if (dialogId == 1) {
			// 新增
			if (i == 0) {
				Intent copyIntent = new Intent(_thisActivity,
						DN004Activity.class);
				Bundle copyBundle = new Bundle();
				copyBundle.putString("AddType", AddType);
				copyBundle.putString("Type", "2"); // 更新标识
				copyBundle.putString("cdOrder", cdOrder); // 进仓编号个别
				copyBundle.putString("cdOrderPublic", cdOrderPublic); // 进仓编号共通
				copyBundle.putString("depotID", depotID); // 进仓ID
				copyBundle.putString("noBatch", noBatch); // 批次
				copyBundle.putString("thNo", txtThNo.getText().toString()); // 同行编号
				copyBundle.putString("depotDtID", dataList.getDepotDtID());
				copyBundle.putString("noPilecard", dataList.getNoPilecard());
				copyBundle.putString("CarNo", CarNo); // 车牌号
				copyBundle.putString("CarType", CarType); // 车辆类型
				copyBundle.putString("NoMultipleOrder", NoMultipleOrder); // 分票号
				copyBundle.putString("NoCarNum", NoCarNum); // 同车编号
				// add yxq 2014/09/03 begin
				copyBundle.putString("NoMultipleDepot", NoMultipleDepot);//进仓分票号  
				// add yxq 2014/09/03 end
				copyBundle.putString("NoMultipleColo", NoMultipleColo);
				copyIntent.putExtras(copyBundle);
				startActivityForResult(copyIntent, 6);
			}
		}
	}
	
	// 更新唛头相符标识  add by yxq 2014/08/21
	public void UpdateMarksDif(){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		Integer nHydraulic = null;
		if (editHydraulic.getText().toString().trim() != ""){
			nHydraulic = Integer.parseInt(editHydraulic.getText().toString().trim());
		}
		paraMap.put("depotId", depotID);
		paraMap.put("noBatch", noBatch);
		paraMap.put("marksDif", chkNotMatch.isChecked());
		paraMap.put("marksExist", chkNoMarks.isChecked());
		paraMap.put("marksPortDif", chkPortMarks.isChecked());
		paraMap.put("mailSendFlg",mailSendFlg);
		paraMap.put("realPortInput",realPortInput);
		paraMap.put("flgHydraulic", chkHydraulic.isChecked());
		paraMap.put("numHydraulic", nHydraulic);
		paraMap.put("flgCdOrderDif", chkCdOrderDif.isChecked());
		paraMap.put("flgKarachiInvoice", chkKarachiHasInvoice.isChecked());
		paraMap.put("flgBlackDrum", chkBlackDrum.isChecked());
		paraMap.put("flgDrumHandle", chkDrumHandle.isChecked());
		
		NetworkHelper.getInstance().postJsonData(_thisActivity,"DN002_UpdateMarksDifAndHydraulicAndMail",
				paraMap,CommonResult.class,new AmassHttpResponseHandler<CommonResult>() {
					@Override
					protected void OnSuccess(CommonResult response) {
						if(response.getFlag()){
						    mailSendFlg = true;   // 发过邮件了
						}
						alertFlg = true;   //设置保存照片时不弹确认信息
						Utils.showAlertDialog(_thisActivity,
								getString(R.string.msg_save_success));
					}
				}, true);
	}
	// 跳转到复量画面
	private void RepeatedMeasure(DN002ListData data) {
		Intent detailIntent = new Intent(_thisActivity,DN004Activity.class);
		Bundle detailBundle = new Bundle();
		detailBundle.putString("Type", "12"); 					// 复量标识
		detailBundle.putBoolean("bolFlag", enFlag);
		detailBundle.putString("cdOrder", cdOrder); 			// 进仓编号个别
		detailBundle.putString("cdOrderPublic",cdOrderPublic);  // 进仓编号共通
		detailBundle.putString("depotID", depotID);           	// 进仓ID
		detailBundle.putString("noBatch", noBatch); 			// 批次
		detailBundle.putString("thNo", txtThNo.getText().toString()); // 同行编号
		detailBundle.putString("depotDtID",data.getDepotDtID());
		detailBundle.putString("noPilecard",data.getNoPilecard());
		detailIntent.putExtras(detailBundle);
		startActivity(detailIntent);
	}
	
	/*
	 * 弹出实际港口输入框
	 */
	public void ShowInputDialig() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light));  
		builder.setTitle(R.string.DN002_PORT_INPUT).setCancelable(false);
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.dialog_port_input, null);
		final AutoCompleteTextView editTexCdOrder = (AutoCompleteTextView) textEntryView
				.findViewById(R.id.autocomplete_port);
		if(realPortInput!=null && !realPortInput.isEmpty()){
			editTexCdOrder.setText(realPortInput);
		}
		String[] portNames = new String[portList.size()];
		for(int i=0;i<portList.size();i++){
			portNames[i] = portList.get(i).getName();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.item_port_list ,portNames);  
		editTexCdOrder.setAdapter(adapter);  
		builder.setView(textEntryView);		
		builder.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						realPortInput = editTexCdOrder.getText().toString();
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
	
	/*
	 * 输入港口按钮事件
	 */
	public void Btn_PortInput_OnClick(View view) {
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("name", "all");
		NetworkHelper.getInstance().postJsonData(
				_thisActivity,"getPortList",p1,
				new TypeToken<List<SelectDict>>(){}.getType(),
				new AmassHttpResponseHandler<List<SelectDict>>() {
					@Override
					protected void OnSuccess(
							List<SelectDict> response) {
						super.OnSuccess(response);
						List<SelectDict> List = (List<SelectDict>) response;
						if (List != null){
							portList = List;
							ShowInputDialig();
						}
					}
				}, true);
	}
/*	private void GetCdWareHouse(){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA003_GetCdWareHouse",
			paraMap,String.class, new AmassHttpResponseHandler<String>() {
				@Override
				protected void OnSuccess(String response) {
					super.OnSuccess(response);
					strCdWareHouse = response;
				}
			}, false);
	}*/
}
