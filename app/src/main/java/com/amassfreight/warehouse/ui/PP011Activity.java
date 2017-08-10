package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileNotFoundException;
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
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.bean.BarCode02;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.CommonResult;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.domain.PP005StatusData;
import com.amassfreight.domain.PP009DepotData;
import com.amassfreight.domain.PP009ResponseData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.widget.GalleryAct;
import com.google.zxing.client.android.CaptureActivity;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class PP011Activity extends BaseActivity {
	private Boolean bolIsEit = true; // 判断哪个页面跳转：一览画面 false；拣货 true；
	private Boolean bolIsFclOrScl; // True:整箱； False:自拼箱
	private String strNoBox; // 集箱号
	private String strCdOrdr; // 进仓编号
	private String cdOrderPublic;
	private TextView stockQuery;
	private TextView packPhotoQuery;
	private TextView tvCdOrder; // 进仓编号
	private TextView tvFlgQuit; // 是否退关
	private TextView tvQtNum; // 原件数
	private TextView tvWeight; // kgs
	private TextView tvCounts; // 本箱件数
	private TextView tvValume; // CMB
	private ListView listFileList; // 文件一览
	private TextView tvPlanRemarks; // 计划备注
	private Button btnSavePackNum;

	private ListView listPP009;
	private LayoutInflater mInflater;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter pp011Adapter;
	private FileDataAdapter pp011FileAdapter;
	private List<PP009DepotData> ppList;
	private String strDepotId;
	private Boolean bolIsPost;
	private Boolean bolSetFontRed;
	private int numRealCounts;
	private PP009DepotData pp011ReturnData;
	private String strCdBooking;
	private TextView tvCdBooking; // 关单号
	private Boolean bolScan;

	private GridView gv;
	private GridView gv1;
	private ImageAdapter imageAdpter;
	private ImageAdapter imageAdpter1;
	private boolean actionFlag = false;
	private File cameraTempFile;
	private Button btn_pic_save;
	private List<ImageData> saveImageList; // 需要保存的图片list
	private List<ImageData> saveImageList1; // 需要保存的图片list

	private LinearLayout linMain;

	private Boolean meunShow = false;
	private ImageButton imgBtn;
	private TextView tvOprName; // 计划备注
	private ScrollView scoListView;

	private TextView tvTitleCdOrder; // 进仓编号
	private Boolean bolIsPrint = false;

	/** 初始化事件 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Bundle bundle = this.getIntent().getExtras();

		bolIsPost = false;
		bolSetFontRed = false;
		numRealCounts = 0;
		pp011ReturnData = null;
		strCdBooking = null;
		bolScan = false;
		// 判断页面传值是否为空
		if (bundle != null) {
			// 根据不同传值类型
			bolIsEit = (Boolean) bundle.get("IsEdit"); // 判断扫描功能是否使用
			bolIsFclOrScl = (Boolean) bundle.get("isFclOrScl");// 判断是否为整箱或者自拼箱
			strNoBox = bundle.get("noBox").toString();// 集箱号
			strCdOrdr = bundle.get("cdOrder").toString(); // 进仓编号
			strDepotId = bundle.get("depotDtId").toString();
			bolIsPost = (Boolean) bundle.get("isPost");// 判断是否为整箱或者自拼箱
			bolSetFontRed = (Boolean) bundle.get("selectSetFont"); // 字体设置
			numRealCounts = (Integer) bundle.get("realCounts"); // 输入数量
			strCdBooking = bundle.get("cdBooking").toString();// 关单号
			bolScan = (Boolean) bundle.get("IsSacn"); // 扫描功能
		} else {
			bolIsEit = true;
		}
		// 根据页面的类型不同，设置显示的标题
		if (bolIsFclOrScl) {
			setTitle(getString(R.string.PP011_TITLE_NAME_FCL));
		} else {
			setTitle(getString(R.string.PP011_TITLE_NAME_SCL));
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp011_pack_detail);
		// 设置标题栏是左边是否有返回事件
		setupActionBar();

		// 实例化
		tvCdOrder = (TextView) findViewById(R.id.tvCdOrder);
		tvFlgQuit = (TextView) findViewById(R.id.tvFlgQuit);
		tvQtNum = (TextView) findViewById(R.id.tvQtNum);
		tvWeight = (TextView) findViewById(R.id.tvWeight);
		tvCounts = (TextView) findViewById(R.id.tvCounts);
		tvValume = (TextView) findViewById(R.id.tvValume);
		tvPlanRemarks = (TextView) findViewById(R.id.tvPlanRemarks);
		// 溢出滚动
		tvPlanRemarks.setMovementMethod(ScrollingMovementMethod.getInstance());
		tvCdBooking = (TextView) findViewById(R.id.tvCdBooking);

		tvOprName = (TextView) findViewById(R.id.tvOPrName);
		linMain = (LinearLayout) findViewById(R.id.linMain);
		imgBtn = (ImageButton) findViewById(R.id.imgBtn);
		stockQuery = (TextView) findViewById(R.id.stockQuery);
		packPhotoQuery = (TextView) findViewById(R.id.packPhotoQuery);	
		tvTitleCdOrder = (TextView) findViewById(R.id.tvTitleCdOrder);

		scoListView = (ScrollView) findViewById(R.id.scoList);
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		listPP009 = (ListView) findViewById(R.id.listPP009View);
		listFileList = (ListView) findViewById(R.id.fileList);

		// 库存查询
        stockQuery = (TextView) findViewById(R.id.stockQuery);
        stockQuery.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        stockQuery.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		Intent intent = new Intent(); 
        		intent.setClass(PP011Activity.this, OT003Activity.class); 
        		intent.putExtra("orderCd", cdOrderPublic);
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
        		intent.setClass(PP011Activity.this, PP016Activity.class); 
        		intent.putExtra("boxNo", strNoBox);
        		intent.putExtra("orderCd", strNoBox);
        		intent.putExtra("orderCdPublic", cdOrderPublic);
        		startActivity(intent);
        	}
        });
		
		btnSavePackNum = (Button) findViewById(R.id.btnSavePackNum);
		btnSavePackNum.setVisibility(View.GONE);
		btnSavePackNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final PP009DepotData pp011Data = pp011ReturnData;

				if (pp011Data == null || pp011Data.getStockNum() < pp011Data.getRealCounts()) {
					Utils.showAlertDialogIntMsg(_thisActivity,
							R.string.PP011_msg_packError_003);
					return;
				}
				Map<String, Object> pp011ap = new HashMap<String, Object>();
				pp011ap.put("pp009Data", pp011ReturnData);
				pp011ap.put("strNoBox", strNoBox);
				pp011ap.put("bolFclOrScl", bolIsFclOrScl);
				pp011ap.put("strCdBooking", strCdBooking);
				NetworkHelper.getInstance().postJsonData(PP011Activity.this,
						"PP011_UpdateMatch", pp011ap, PP005StatusData.class,
						new AmassHttpResponseHandler<PP005StatusData>() {
							protected void OnSuccess(PP005StatusData response) {

								super.OnSuccess(response);
								// 获取对应的数据
								PP005StatusData statusData = (PP005StatusData) response;
								if (statusData.getUpdatePickStatus()) {
									strDepotId = "";
									pp011ReturnData = null;
									bolSetFontRed = false;
									numRealCounts = 0;
									btnSavePackNum.setVisibility(View.GONE);
									InitData();
									Utils.showAlertDialogIntMsg(_thisActivity,
											R.string.PP011_msg_packOk_004);

								} else {
									if ("011".equals(statusData.getErrorCd())) {
										strDepotId = pp011ReturnData
												.getDepotId();
										bolSetFontRed = true;
										numRealCounts = pp011Data
												.getRealCounts();

										InitData();
										Utils.showAlertDialogIntMsg(
												_thisActivity,
												R.string.PP011_msg_packError_011);
									}

									else if ("003".equals(statusData
											.getErrorCd())) {
										strDepotId = pp011ReturnData
												.getDepotId();
										bolSetFontRed = true;
										numRealCounts = pp011Data
												.getRealCounts();

										InitData();

										Utils.showAlertDialogIntMsg(
												_thisActivity,
												R.string.PP011_msg_packError_003);
									}
								}
							}
						}, true);
			}
		});

		// 适配器
		pp011Adapter = new ArrayAdapter(getApplicationContext(), 0) {
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				LinearLayout layout = new LinearLayout(getContext());
				final PP009DepotData data = (PP009DepotData) getItem(position);
				try {
					mInflater.inflate(R.layout.activity_pp011_pack_detail_item,
							layout, true);

					if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}

					TextView tvNoBatch = (TextView) layout
							.findViewById(R.id.tvNoBatch);
					tvNoBatch.setText(data.getNoBatch());// 桩脚牌
					TextView tvQt = (TextView) layout.findViewById(R.id.tvQt);
					tvQt.setText(String.valueOf(data.getOriginalNum()));// 原件数
					TextView tvQz = (TextView) layout.findViewById(R.id.tvQz);
					tvQz.setText(String.valueOf(data.getStockNum()));// 可装件数
					final EditText tvReal = (EditText) layout
							.findViewById(R.id.tvReal);

					tvReal.setText(String.valueOf(data.getRealCounts()));// 实际件数

					tvReal.setEnabled(false);

					TextView tvPos = (TextView) layout
							.findViewById(R.id.tvPosAndLocation);

					tvPos.setText(data.getPosAndLocation());// 库区库位

					final Integer realNum = data.getStockNum();

					tvReal.setOnEditorActionListener(new OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView tvView,
								int actionId, KeyEvent event) {

							if (actionId == EditorInfo.IME_ACTION_DONE) {
								if (tvView.getText().toString().isEmpty()
										|| realNum < Integer.parseInt(tvView
												.getText().toString())) {
									Utils.showAlertDialogIntMsg(_thisActivity,
											R.string.PP011_msg_packError_003);
								}
							}
							return false;
						}
					});

					tvReal.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence arg0, int arg1,
								int arg2, int arg3) {

							if (arg0.length() == 0) {
								tvReal.setText("0");
							} else {
								Integer numReal = Integer.parseInt(arg0
										.toString());
								data.setRealCounts(numReal);

								if (arg0.length() != numReal.toString()
										.length()) {
									tvReal.setText(numReal.toString());
								}
							}

						}

						@Override
						public void beforeTextChanged(CharSequence arg0,
								int arg1, int arg2, int arg3) {
						}

						@Override
						public void afterTextChanged(Editable arg0) {

						}
					});

					TextView tvFlgPicking = (TextView) layout
							.findViewById(R.id.tvFlgPicking);
					tvFlgPicking.setText(data.getFlgPicking());// 拣货状态
					TextView tvFlgPacking = (TextView) layout
							.findViewById(R.id.tvFlgPacking);

					tvFlgPacking.setText(data.getFlgPacking());// 装箱状态

					// 判断是否选中
					if (!strDepotId.isEmpty()
							&& strDepotId.equals(data.getDepotId())) {

						pp011ReturnData = data;
						if (data.getStockNum() == 0
								&& data.getRealCounts() == 0) {
							tvReal.setEnabled(false);
						} else {
							// 获取光标
							tvReal.setFocusableInTouchMode(true);
							tvReal.setClickable(true);

							tvReal.setSelectAllOnFocus(true);
							tvReal.setEnabled(true);

						}

						layout.setBackgroundResource(R.color.lightBlue);

						if (numRealCounts != 0) {
							tvReal.setText(String.valueOf(numRealCounts));// 实际件数
						}

						// 如果数量输入错误则设置颜色
						if (bolSetFontRed) {
							tvReal.setBackgroundColor(Color.RED);
						}
						if (actionFlag) {

						}

						if (getString(R.string.status_pack_undo).equals(
								data.getFlgPacking())
								&& data.getStockNum() == 1) {

							Map<String, Object> pp011ap = new HashMap<String, Object>();
							pp011ap.put("pp009Data", pp011ReturnData);
							pp011ap.put("strNoBox", strNoBox);
							pp011ap.put("bolFclOrScl", bolIsFclOrScl);
							pp011ap.put("strCdBooking", strCdBooking);
							NetworkHelper
									.getInstance()
									.postJsonData(
											PP011Activity.this,
											"PP011_UpdateMatch",
											pp011ap,
											PP005StatusData.class,
											new AmassHttpResponseHandler<PP005StatusData>() {
												protected void OnSuccess(
														PP005StatusData response) {

													super.OnSuccess(response);
													// 获取对应的数据
													PP005StatusData statusData = (PP005StatusData) response;
													if (statusData
															.getUpdatePickStatus()) {
														strDepotId = "";
														pp011ReturnData = null;
														bolSetFontRed = false;
														numRealCounts = 0;
														btnSavePackNum
																.setVisibility(View.GONE);
														InitData();

													}
												}
											}, true);
						}
					}

					if (getString(R.string.status_pick_done).equals(
							data.getFlgPicking())) {
						tvFlgPicking.setBackgroundColor(Color.YELLOW);
					}

					if (getString(R.string.status_packing_doing).equals(
							data.getFlgPacking())) {
						tvFlgPacking.setBackgroundColor(Color.BLUE);
						tvFlgPacking.setTextColor(Color.WHITE);
					} else if (getString(R.string.status_packing_done).equals(
							data.getFlgPacking())) {
						tvFlgPacking.setBackgroundColor(Color.GRAY);
						tvFlgPacking.setTextColor(Color.BLACK);
					} else {
						tvFlgPacking.setTextColor(Color.BLACK);
						tvFlgPacking.setBackgroundColor(Color.TRANSPARENT);
					}
				} catch (Exception e) {
					try {
						throw e;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				return layout;
			}
		};

		listPP009.setAdapter(pp011Adapter);

		listPP009.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int pos, long arg3) {

				final PP009DepotData data = (PP009DepotData) arg0
						.getItemAtPosition(pos);
				final String[] selItem;
				if (!bolIsEit) {
					selItem = new String[] { getString(R.string.button_detail)};
				}else if(getString(R.string.status_packing_doing).equals(
						data.getFlgPacking())
						&& bolIsEit) {
					selItem = new String[] { getString(R.string.button_detail),
							getString(R.string.PP007_input_packing_remark),
							getString(R.string.PP011_SPILT_NAME) };
				}else {
					selItem = new String[] { getString(R.string.button_detail),
							getString(R.string.PP007_input_packing_remark)};
				}

				DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: // 查看
							Intent detailIntent = new Intent(_thisActivity,
									DN004Activity.class);
							String strNoBatch = null; // 批次
							String strnoPileCard = null; // 桩脚牌
							if (data.getNoBatch() != null
									&& data.getNoBatch().split("-").length > 0) {
								strNoBatch = data.getNoBatch().split("-")[0];
								if (data.getNoBatch().split("-").length > 1) {
									strnoPileCard = data.getNoBatch()
											.split("-")[1];
								}
							}

							String strPos = null; // 库区
							String strLocation = null;// 库位
							if (data.getPosAndLocation() != null
									&& data.getPosAndLocation().split("-").length > 0) {
								strPos = data.getPosAndLocation().split("-")[0];
								if (data.getPosAndLocation().split("-").length > 1) {
									strLocation = data.getPosAndLocation()
											.split("-")[1];
								}
							}
							Bundle detailBundle = new Bundle();
							detailBundle.putString("Type", "8"); // 更新标识
							detailBundle.putString("cdOrderPublic",
									data.getCdBusinessPublic()); // 进仓编号共通
							detailBundle.putString("noBatch", strNoBatch);// 批次
							detailBundle.putString("depotDtId",
									data.getDepotId());// 货物明细ID
							detailBundle.putString("noPilecard", strnoPileCard);// 桩脚牌
							detailBundle.putString("pos", strPos);// 库区
							detailBundle.putString("location", strLocation);// 库位
							detailIntent.putExtras(detailBundle);
							startActivity(detailIntent);
							break;
						case 1: // 输入装箱备注
							AlertDialog.Builder builder = new AlertDialog.Builder(PP011Activity.this).setTitle(
									R.string.PP007_lb_packing_remark).setCancelable(false);
							LayoutInflater factory = LayoutInflater.from(PP011Activity.this);
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
									requestData.put("depotDtId", data.getDepotId());
									requestData.put("packingRemark", remark);								
									NetworkHelper.getInstance().postJsonData(PP011Activity.this, 
											"PP007updatePackingRemark", requestData,CommonResult.class , PackingRemarkHandler, true);

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
						case 2: // 拆分货物

							String strSpiltPos = null; // 库区
							String strSpiltLocation = null;// 库位
							if (data.getPosAndLocation() != null
									&& data.getPosAndLocation().split("-").length > 0) {
								strSpiltPos = data.getPosAndLocation().split(
										"-")[0];
								if (data.getPosAndLocation().split("-").length > 1) {
									strSpiltLocation = data.getPosAndLocation()
											.split("-")[1];
								}
							}

							Intent modifyIntent = new Intent(_thisActivity,
									PP011Activity_Spilt.class);

							Bundle modifyBundle = new Bundle();
							modifyBundle.putString("depotDtId",
									data.getDepotId());// 货物明细ID
							modifyBundle.putString("pos", strSpiltPos);// 库区
							modifyBundle
									.putString("location", strSpiltLocation);// 库位
							modifyBundle.putString("cdOrderPublic", tvCdOrder
									.getText().toString());// 进仓编号
							modifyBundle.putInt("reNum", data.getStockNum()
									- data.getRealCounts());// 剩余数量
							modifyIntent.putExtras(modifyBundle);
							startActivityForResult(modifyIntent, 3);
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

		gv = (GridView) findViewById(R.id.gridView_image);
		gv1 = (GridView) findViewById(R.id.gridView_image1);
		
		imageAdpter = new ImageAdapter(this, 0);
		saveImageList = new ArrayList<ImageData>();
		imageAdpter.add(new MoreData());
		imageAdpter.setShowAddDesc(false);
		gv.setAdapter(imageAdpter);
		
		imageAdpter1 = new ImageAdapter(this, 0);
		saveImageList1 = new ArrayList<ImageData>();
		imageAdpter1.add(new MoreData());
		imageAdpter1.setShowAddDesc(false);
		gv1.setAdapter(imageAdpter1);
		
		actionFlag = true;
		if(!bolIsEit){
        	gv.setVisibility(View.GONE);
        	gv1.setVisibility(View.GONE);
        }
		btn_pic_save = (Button) findViewById(R.id.btn_pic_save);
		btn_pic_save.setVisibility(View.GONE);
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE && bolIsEit) { // 添加
					if (actionFlag) {
						Intent intent = new Intent();
						// 指定开启系统相机的Action
						intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						File dir = new File(Environment
								.getExternalStorageDirectory(),
								"amass/pics/pp011");

						cameraTempFile = new File(dir, UUID.randomUUID()
								+ ".jpg");
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(cameraTempFile));
						startActivityForResult(intent, 2);
					}
				} else { // 放大
					ImageData imageData = (ImageData) data.getData();
					if (imageData.getData() != null) {
						GalleryAct.start(_thisActivity, imageData.getUrl());
					}

				}
			}
		});
		gv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE && bolIsEit) { // 添加
					if (actionFlag) {
						Intent intent = new Intent();
						// 指定开启系统相机的Action
						intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						File dir = new File(Environment
								.getExternalStorageDirectory(),
								"amass/pics/pp011");

						cameraTempFile = new File(dir, UUID.randomUUID()
								+ ".jpg");
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(cameraTempFile));
						startActivityForResult(intent, 4);
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
				if (data.getDataType() == MoreData.DATA_TYPE
						&& saveImageList.contains((ImageData) data.getData())) {
					final int imagePos = pos;
					DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								MoreData data = (MoreData) imageAdpter
										.getItem(imagePos);
								imageAdpter.remove(data);
								saveImageList.remove((ImageData) data.getData());
								imageAdpter.notifyDataSetChanged();
								if (saveImageList == null
										|| saveImageList.size() <= 0) {
									btn_pic_save.setVisibility(View.GONE);
								}
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
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.DATA_TYPE
						&& saveImageList1.contains((ImageData) data.getData())) {
					final int imagePos = pos;
					DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								MoreData data = (MoreData) imageAdpter1
										.getItem(imagePos);
								imageAdpter1.remove(data);
								saveImageList1.remove((ImageData) data.getData());
								imageAdpter1.notifyDataSetChanged();
								if (saveImageList1 == null
										|| saveImageList1.size() <= 0) {
									btn_pic_save.setVisibility(View.GONE);
								}
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
		InitData();
		// 页面跳转事件
		if (!strDepotId.isEmpty() && bolIsPost) {
			OpScanResult(strCdOrdr, strDepotId);
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
		scoListView.fullScroll(ScrollView.FOCUS_UP);
		// Creating the expand animation for the item
		ExpandAnimation expandAni = new ExpandAnimation(toolbar, 500);
		// Start the animation on the toolbar
		toolbar.startAnimation(expandAni);
	}

	/* 页面赋值 */
	public void InitData() {

		// 删除文件夹内容
		File dir = new File(Environment.getExternalStorageDirectory(),
				"amass/pics/pp011");
		Utils.deleteFileAndPath(dir);

		Map<String, Object> pp009Map = new HashMap<String, Object>();
		pp009Map.put("IsFclOrScl", bolIsFclOrScl);// 是否是整箱或者自拼箱
		pp009Map.put("strNoBox", strNoBox);// 集箱号
		pp009Map.put("strcdBusiness", strCdOrdr);// 进仓编号
		pp009Map.put("strCdBooking", strCdBooking); // 关单号
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"PP009_GetData", pp009Map, PP009ResponseData.class,
				new AmassHttpResponseHandler<PP009ResponseData>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(PP009ResponseData response) {
						super.OnSuccess(response);
						if (response != null) {

							// 获取对应的数据
							PP009ResponseData pp009Response = (PP009ResponseData) response;
							tvCdOrder.setText(pp009Response
									.getCdBusinessPublic()); // 进仓编号

							tvTitleCdOrder.setText(pp009Response
									.getCdBusinessPublic()); // 进仓编号
							cdOrderPublic = pp009Response
									.getCdBusinessPublic();
							// 是否退关
							if (pp009Response.getFlgQuit() != null
									&& pp009Response.getFlgQuit()) {
								tvFlgQuit.setText("是");
							} else {
								tvFlgQuit.setText("否");
							}
							tvQtNum.setText(String.valueOf(pp009Response
									.getQtNum())); // 原件数
							tvWeight.setText(String.valueOf(pp009Response
									.getWeight())); // 重量
							tvCounts.setText(String.valueOf(pp009Response
									.getCounts())); // 件数
							tvValume.setText(String.valueOf(pp009Response
									.getVolume())); // 体积
							tvPlanRemarks.setText(pp009Response.getRemark()); // 备注
							tvCdBooking.setText(pp009Response.getCdBooking());
							strCdBooking = pp009Response.getCdBooking();
							// 明细数据
							ppList = (List<PP009DepotData>) pp009Response
									.getPp009DepList();
							pp011Adapter.clear();
							if (ppList != null) {
								pp011Adapter.addAll(ppList);
								pp011Adapter.notifyDataSetChanged();
							}
							List<FileManageData> fileList = (List<FileManageData>) pp009Response
									.getFileList();
							pp011FileAdapter = new FileDataAdapter(
									_thisActivity, fileList);
							listFileList.setAdapter(pp011FileAdapter);

							if (bolIsPrint) {
								bolIsPrint = false;
								Utils.showAlertDialogIntMsg(_thisActivity,
										R.string.msg_common_print_success);

							}

						}
					}
				}, true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 从主菜单的【整箱拣货】或【自拼箱拣货】点击进来的情况下，本按钮可用
		// 从装箱一览迁移过来的情况下，本按钮不可用
		if (bolScan && bolIsEit) {
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
			return true;
			/*
			 * case android.R.id.home: setResult(Activity.RESULT_OK);
			 * this.finish(); return true;
			 */
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
				@SuppressWarnings("unused")
				String barCode = data.getStringExtra("SCAN_RESULT");
				// 判断是否是桩脚牌
				final BarCode02 barResult = new BarCode02();
				boolean result02 = barResult.paserBarCode(data
						.getStringExtra("SCAN_RESULT"));
				// 判断是否是桩脚牌
				if (result02) {
					OpScanResult(barResult.getOrderCd(),
							barResult.getDepotDtId());
				} else {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.PP008_DIALOG_DEOPT));
				}

			}
			break;

		case 2:
			if (resultCode == RESULT_OK) {
				String path = cameraTempFile.getAbsolutePath();
				Utils.compressImage(path); // 压缩图片
				String displyPath = path.startsWith("/") ? "file://" + path
						: path;
				ImageSize size = new ImageSize(100, 100);
				Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(
						displyPath, size);
				ImageData imageData = new ImageData();
				imageData.setData(thumbnail);
				imageData.setPath(path);
				imageData.setUrl(displyPath);

				MoreData item = new MoreData(imageData);
				//imageAdpter.insert(item, imageAdpter.getCount() - 1);
				imageAdpter.insert(item,1);
				saveImageList.add((ImageData) item.getData());
				imageAdpter.notifyDataSetChanged();
				imageAdpter.setShowAddDesc(false);
				btn_pic_save.setVisibility(View.VISIBLE);
			}
			break;
		case 3:
			if (resultCode == RESULT_OK) {

				InitData();

			} else if (resultCode == RESULT_CANCELED) {
				bolIsPrint = false;
				InitData();
			}
			break;
		case 4:
			if (resultCode == RESULT_OK) {
				String path = cameraTempFile.getAbsolutePath();
				Utils.compressImage(path); // 压缩图片
				String displyPath = path.startsWith("/") ? "file://" + path
						: path;
				ImageSize size = new ImageSize(100, 100);
				Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(
						displyPath, size);
				ImageData imageData = new ImageData();
				imageData.setData(thumbnail);
				imageData.setPath(path);
				imageData.setUrl(displyPath);

				MoreData item = new MoreData(imageData);
				//imageAdpter.insert(item, imageAdpter.getCount() - 1);
				imageAdpter1.insert(item,1);
				saveImageList1.add((ImageData) item.getData());
				imageAdpter1.notifyDataSetChanged();
				imageAdpter1.setShowAddDesc(false);
				btn_pic_save.setVisibility(View.VISIBLE);
			}
			break;
		}

	}

	/*
	 * 执行拣货状态变更事件
	 */
	public void OpScanResult(final String cdOrder, final String strDepot) {

		Map<String, Object> pp011Map = new HashMap<String, Object>();

		pp011Map.put("isFclOrScl", bolIsFclOrScl);// 整箱自拼箱
		pp011Map.put("strNoBox", strNoBox);// 集箱号
		pp011Map.put("strcdBusiness", strCdOrdr);// 进仓编号
		pp011Map.put("strDepotDtId", strDepot);// 货物明显ID

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"PP011_GetTDcDetailAndUpdateData", pp011Map,
				PP005StatusData.class,
				new AmassHttpResponseHandler<PP005StatusData>() {

					@Override
					protected void OnSuccess(PP005StatusData response) {
						super.OnSuccess(response);
						// 获取对应的数据
						PP005StatusData statusData = (PP005StatusData) response;
						if (statusData.getUpdatePickStatus()) {
							//if (cdOrder.equals(strCdOrdr)) {
							if (statusData.getOrderCd().equals(strCdOrdr)) {
								boolean bolShowSave = true;
								// InitData();
								//strDepotId = strDepot;
								strDepotId = statusData.getDepotDtId();
								pp011Adapter.notifyDataSetChanged();
								int numPostion = 0;
								for (int i = 0; i < ppList.size(); i++) {
									if (strDepot.equals(ppList.get(i)
											.getDepotId())) {
										numPostion = i;
										if (ppList.get(i).getStockNum() == 0
												&& ppList.get(i)
														.getRealCounts() == 0) {
											bolShowSave = false;
										}
									}
								}

								int totalHeight = 0;

								for (int i = 0; i < numPostion; i++) {
									// listAdapter.getCount()返回数据项的数目

									View listItem = pp011Adapter.getView(i,
											null, listPP009);

									listItem.measure(0, 0); // 计算子项View 的宽高

									totalHeight += listItem.getMeasuredHeight();
									// 统计所有子项的总高度

								}

								if (numPostion > 0) {
									totalHeight += (listPP009
											.getDividerHeight() * (numPostion - 1));
								}

								final int numScoreY = totalHeight;

								scoListView.postDelayed(new Runnable() {

									@Override
									public void run() {
										if (scoListView.getScrollY() != numScoreY) {
											scoListView.scrollTo(0, numScoreY);
										}

									}
								}, 60);

								if (bolShowSave) {
									btnSavePackNum.setVisibility(View.VISIBLE);
								} else {
									btnSavePackNum.setVisibility(View.GONE);
								}

							} else {
								/*
								 * strDepotId = strDepot; strCdOrdr = cdOrder;
								 * InitData();
								 */
								Utils.showAlertDialog(
										_thisActivity,
										getString(R.string.PP011_msg_SCAN_CDORDER));

							}

						} else {
							if ("000".equals(statusData.getErrorCd())) {
								Utils.showAlertDialog(
										_thisActivity,
										getString(R.string.PP011_msg_packError_000));
							} else if("001".equals(statusData.getErrorCd())) {
								
								
								if (statusData.getLockScanRemark() == ""){
									Utils.showAlertDialog(_thisActivity,
											getString(R.string.PP011_msg_SCAN));
								}
								else{
									Utils.showAlertDialog(_thisActivity,statusData.getLockScanRemark());
								}

							}else if("009".equals(statusData.getErrorCd())){
								Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_VasError);						
							}
							btnSavePackNum.setVisibility(View.GONE);
						}

					}
				}, true);

	}

	/*
	 * 返回事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 删除文件夹内容
		File dir = new File(Environment.getExternalStorageDirectory(),
				"amass/pics/pp011");
		Utils.deleteFileAndPath(dir);

		// 后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 按下的如果是BACK，同时没有重复
			setResult(Activity.RESULT_OK);
			this.finish();

		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * 保存照片
	 */
	public void Btn_Save_OnClick(View view) {
		try {
			RequestParams params = new RequestParams();
			params.put("noBox", strNoBox);
			params.put("cdOrder", strCdOrdr);
			RequestParams params1 = new RequestParams();
			params1.put("noBox", strNoBox);
			params1.put("cdOrder", strCdOrdr);
			
			if (saveImageList.size() > 0) {
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
							saveImageList.clear();
							imageAdpter.notifyDataSetChanged();
							btn_pic_save.setVisibility(View.GONE);
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.msg_save_success));
						}

					}, true);
			
			//其他装箱文件
			if (saveImageList1.size() > 0) {
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
							saveImageList1.clear();
							imageAdpter1.notifyDataSetChanged();
							btn_pic_save.setVisibility(View.GONE);
							Utils.showAlertDialog(_thisActivity,
									getString(R.string.msg_save_success));  
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

	//保存装箱备注处理handler
	private AmassHttpResponseHandler<CommonResult> PackingRemarkHandler = new AmassHttpResponseHandler<CommonResult>() {
		protected void OnSuccess(CommonResult response) {
			super.OnSuccess(response);
			CommonResult result = (CommonResult) response;
			if(result != null){
				//处理成功
				if(result.getFlag()){		
					AlertDialog.Builder builder = new AlertDialog.Builder(
							new ContextThemeWrapper(PP011Activity.this,
									android.R.style.Theme_Holo_Light));
					builder.setIcon(R.drawable.ic_launcher);
					builder.setCancelable(false);
					builder.setTitle(R.string.app_name);
					builder.setMessage(R.string.msg_save_success);
					builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							InitData();
						}
					});
					builder.show();
				//处理失败
				}else{
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
				}
			}else{
				Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
			}
			
		}
	};
	/*public void stockQuery_OnClick(View view) {
		Intent intent = new Intent(); 
		intent.setClass(PP011Activity.this, OT003Activity.class); 
		intent.putExtra("orderCd", cdOrderPublic);
		intent.putExtra("type", "01");
		startActivity(intent);
	}*/
}
