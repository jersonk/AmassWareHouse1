package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DN003ListData;
import com.amassfreight.domain.DepotDN003;
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

public class DN003Activity extends BaseActivity implements
		CompoundButton.OnCheckedChangeListener {

	private String depotID = ""; // 进仓ID
	private String cdOrderPublic = ""; // 进仓编号 共通
	private String cdOrder = ""; // 进仓编号 个别
	private String errorNo = "";// 选中行的异常报告ID
	private TextView txtDepotNo; // 进仓编号
	private TextView txtThNo; // 同行编号

	private CheckBox chkFlgGuarLost; // 保函缺失标志
	private CheckBox chkFlgGoodsDamage; // 严重破损标志
	private CheckBox chkFlgDamp; // 受潮标志
	private CheckBox chkFlgOilStained; // 漏油/油污标志
	private CheckBox chkFlgOdors; // 有异味标记
	private CheckBox chkFlgBark; // 有树皮标记
	private CheckBox chkFlgDanger; // 危险品/化学品/易燃品标记
	private CheckBox chkFlgUltrasize; // 超长/超宽/超高标记
	private CheckBox chkFlgOther; // 其他异常标记
	private EditText editDescOther; // 其他异常描述
	private CheckBox chkFlgLogNo;	//原木无章
	private CheckBox chkFlgMarksPortDif; //唛头目的港不符
	private CheckBox chkFlgCdOrderDif;	//进仓编号不符
	private Button btnSave;

	private ListView listView;
	private ArrayAdapter adapter;
	private LayoutInflater mInflater;

	private GridView gv;
	private File cameraTempFile;
	private ImageAdapter imageAdpter;
	// private List<String> listImageId_Del; // 删除的照片

	private View selView = null; // 选择行
	private int selRow;
	private int rowColor = Color.TRANSPARENT; // 选择行颜色
	private boolean flag; // 更新、新增标识
	private boolean enFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dn003);
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
				"amass/pics/dn003");
		Utils.deleteFileAndPath(dir);

		enFlag = true;

		/* 获取Intent中的Bundle对象 */
		Bundle bundle = this.getIntent().getExtras();

		/* 获取Bundle中的数据，注意类型和key */
		depotID = bundle.getString("depotID");
		cdOrderPublic = bundle.getString("cdOrderPublic");
		cdOrder = bundle.getString("cdOrder");
		String tnNo = bundle.getString("thNo");

		txtDepotNo = (TextView) findViewById(R.id.txtInStore); // 进仓编号
		txtDepotNo.setText(cdOrderPublic);
		txtThNo = (TextView) findViewById(R.id.txtTH); // 同行编号
		txtThNo.setText(tnNo);

		chkFlgGuarLost = (CheckBox) findViewById(R.id.chk_Error1); // 保函缺失标志
		chkFlgGoodsDamage = (CheckBox) findViewById(R.id.chk_Error2); // 严重破损标志
		chkFlgDamp = (CheckBox) findViewById(R.id.chk_Error3); // 受潮标志
		chkFlgOilStained = (CheckBox) findViewById(R.id.chk_Error4); // 漏油/油污标志
		chkFlgOdors = (CheckBox) findViewById(R.id.chk_Error5); // 有异味标记
		chkFlgBark = (CheckBox) findViewById(R.id.chk_Error6); // 有树皮标记
		chkFlgDanger = (CheckBox) findViewById(R.id.chk_Error7); // 危险品/化学品/易燃品标记
		chkFlgUltrasize = (CheckBox) findViewById(R.id.chk_Error8); // 超长/超宽/超高标记
		chkFlgOther = (CheckBox) findViewById(R.id.chk_Error9); // 其他异常标记
		editDescOther = (EditText) findViewById(R.id.txtRemark); // 其他异常描述
		chkFlgLogNo = (CheckBox) findViewById(R.id.chk_Error10); // 原木无章标记
		chkFlgMarksPortDif = (CheckBox) findViewById(R.id.chk_Error11); // 唛头目的港不符
		chkFlgCdOrderDif = (CheckBox) findViewById(R.id.chk_Error12); // 进仓编号不符
		editDescOther.setEnabled(false);

		btnSave = (Button) findViewById(R.id.btn_SAVE);
		btnSave.setEnabled(false);

		gv = (GridView) findViewById(R.id.gridView_image);
		imageAdpter = new ImageAdapter(this, 0);
		imageAdpter.add(new MoreData());
		gv.setAdapter(imageAdpter);
		// listImageId_Del = new ArrayList<String>();

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 列表控件
		listView = (ListView) findViewById(R.id.result_listView);

		GetListView();

		// 监听checkbox改变事件
		chkFlgOther.setOnCheckedChangeListener(this);

		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				if (data.getDataType() == MoreData.MORE_TYPE) { // 添加
					// 拍照 判断是否选择异常信息
					if (CheckSelect() && enFlag) {
						Intent intent = new Intent();
						// 指定开启系统相机的Action
						intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						File dir = new File(Environment
								.getExternalStorageDirectory(),
								"amass/pics/dn003");
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
	 * 异常信息选择其他时的改变事件
	 */
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			editDescOther.setEnabled(true);
		} else {
			editDescOther.setEnabled(false);
			editDescOther.setText("");
		}
	}

	private void GetListView() {
		adapter = new ArrayAdapter(getApplicationContext(), 0) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout layout = new LinearLayout(getContext());
				DN003ListData item = (DN003ListData) getItem(position);
				mInflater.inflate(R.layout.dn003_list_view_item, layout, true);

				TextView t1 = (TextView) layout.findViewById(R.id.lblErrorNo);
				t1.setText(item.getCdException()); //
				t1 = (TextView) layout.findViewById(R.id.txtTallyman);
				t1.setText(item.getNmTallyman()); //
				t1 = (TextView) layout.findViewById(R.id.txtInserTime);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if (item.getDtInsert() != null) {
					t1.setText(sdf.format((item.getDtInsert()))); //
				}
				t1 = (TextView) layout.findViewById(R.id.lblStatus);
				t1.setText(item.getFlgStatus()); //
				if ("待收货".equals(item.getFlgStatus())) {
					t1.setTextColor(Color.RED);
				}
				if ("可收货".equals(item.getFlgStatus())) {
					t1.setTextColor(Color.BLUE);
				}
				if ("司机拉回".equals(item.getFlgStatus())) {
					t1.setTextColor(Color.GREEN);
				}
				t1 = (TextView) layout.findViewById(R.id.txtPlanner);
				t1.setText(item.getNmApproval()); //
				t1 = (TextView) layout.findViewById(R.id.txtReverTime);
				if (item.getDtApproval() != null) {
					t1.setText(sdf.format(item.getDtApproval())); //
				}

				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// 获得选中项
						DN003ListData map = (DN003ListData) listView
								.getItemAtPosition(arg2);

						// 未选中行颜色(奇偶行交替)
						final int[] rowColors = {
								android.graphics.Color.parseColor("#30FFFFFF"),
								android.graphics.Color.parseColor("#30CCCC99") };

						// 选中行颜色
						int selColor = android.graphics.Color
								.parseColor("#CFE5EF");

						// 设置选中行背景色
						if (selView == null) {
							arg1.setBackgroundColor(selColor);
							flag = true;
						} else {
							if (selView == arg1) {
								if (rowColor == 2) {
									arg1.setBackgroundColor(selColor);
									rowColor = 1; // 设置为选中
									flag = true;
								} else {
									arg1.setBackgroundColor(rowColors[arg2 % 2]);
									rowColor = 2; // 设置为未选中
									flag = false;
								}
							} else {
								selView.setBackgroundColor(rowColors[selRow % 2]);
								arg1.setBackgroundColor(selColor);
								flag = true;
							}
						}
						selView = arg1; // 当前选中行
						selRow = arg2; // 记录当前选中行号

						if (flag) {
							ShowSelectRowData(map);
						} else {
							ClearContral();
							setContralEnable(true);
							enFlag = true;
						}
					}
				});

				int[] colors = { R.color.listview_back_odd,
						R.color.listview_back_uneven };
				layout.setBackgroundResource(colors[position % 2]);

				return layout;
			}
		};

		listView.setAdapter(adapter);

		GetErrorList();
	}

	/*
	 * 选择行赋值
	 */
	private void ShowSelectRowData(DN003ListData map) {
		errorNo = map.getCdException(); // 异常报告ID
		chkFlgGuarLost.setChecked(map.isFlgGuarLost()); // 保函缺失标志
		chkFlgGoodsDamage.setChecked(map.isFlgGoodsDamage()); // 严重破损标志
		chkFlgDamp.setChecked(map.isFlgDamp()); // 受潮标志
		chkFlgOilStained.setChecked(map.isFlgOilStained()); // 漏油/油污标志
		chkFlgOdors.setChecked(map.isFlgOdors()); // 有异味标记
		chkFlgBark.setChecked(map.isFlgBark()); // 有树皮标记
		chkFlgDanger.setChecked(map.isFlgDanger()); // 危险品/化学品/易燃品标记
		chkFlgUltrasize.setChecked(map.isFlgUltrasize()); // 超长/超宽/超高标记
		chkFlgOther.setChecked(map.isFlgOther()); // 其他异常标记
		editDescOther.setText(map.getDescOther()); // 其他异常描述
		chkFlgLogNo.setChecked(map.isFlgLogNo());	//原木无章
		chkFlgMarksPortDif.setChecked(map.isFlgMarksPortDif());//唛头目的港不符
		chkFlgCdOrderDif.setChecked(map.isFlgCdOrderDif());//进仓编号不符港

		if (map.getBolStatus() > 0) {
			setContralEnable(false);
			enFlag = false;
		} else {
			setContralEnable(true);
			enFlag = true;
		}
	}

	/*
	 * 设置控件有效化、无效化
	 */
	private void setContralEnable(boolean enableFlag) {
		chkFlgGuarLost.setEnabled(enableFlag); // 保函缺失标志
		chkFlgGoodsDamage.setEnabled(enableFlag); // 严重破损标志
		chkFlgDamp.setEnabled(enableFlag); // 受潮标志
		chkFlgOilStained.setEnabled(enableFlag); // 漏油/油污标志
		chkFlgOdors.setEnabled(enableFlag); // 有异味标记
		chkFlgBark.setEnabled(enableFlag); // 有树皮标记
		chkFlgDanger.setEnabled(enableFlag); // 危险品/化学品/易燃品标记
		chkFlgUltrasize.setEnabled(enableFlag); // 超长/超宽/超高标记
		chkFlgOther.setEnabled(enableFlag); // 其他异常标记
		chkFlgLogNo.setEnabled(enableFlag);	//原木无章
		chkFlgMarksPortDif.setEnabled(enableFlag);//唛头目的港不符
		chkFlgCdOrderDif.setEnabled(enableFlag);//进仓编号不符
		editDescOther.setEnabled(chkFlgOther.isChecked()); // 其他异常描述
		btnSave.setEnabled(enableFlag);
	}

	/*
	 * 清空控件
	 */
	private void ClearContral() {
		errorNo = ""; // 异常报告ID
		chkFlgGuarLost.setChecked(false); // 保函缺失标志
		chkFlgGoodsDamage.setChecked(false); // 严重破损标志
		chkFlgDamp.setChecked(false); // 受潮标志
		chkFlgOilStained.setChecked(false); // 漏油/油污标志
		chkFlgOdors.setChecked(false); // 有异味标记
		chkFlgBark.setChecked(false); // 有树皮标记
		chkFlgDanger.setChecked(false); // 危险品/化学品/易燃品标记
		chkFlgUltrasize.setChecked(false); // 超长/超宽/超高标记
		chkFlgOther.setChecked(false); // 其他异常标记
		chkFlgLogNo.setChecked(false);	//原木无章
		chkFlgMarksPortDif.setChecked(false);//唛头目的港不符
		chkFlgCdOrderDif.setChecked(false);//进仓编号不符
		editDescOther.setEnabled(chkFlgOther.isChecked());
		editDescOther.setText(""); // 其他异常描述
	}

	/*
	 * 获取异常报告
	 */
	private void GetErrorList() {
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
						adapter.clear();
						btnSave.setEnabled(true);
						// 判断是否有数据
						if (depot == null) {
							return;
						} else {
							// 明细数据
							List<DN003ListData> list = depot.getDn003List();

							// 判断是否有数据
							if (list != null || list.size() > 0) {
								adapter.addAll(list);
								adapter.notifyDataSetChanged();
							}

							if (depot.getDn003FileList() != null
									&& depot.getDn003FileList().size() > 0) {
								// 循环加载进仓照片
								for (FileManageData file : depot
										.getDn003FileList()) {
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
	 * 判断选中项
	 */
	private boolean CheckSelect() {
		if (chkFlgBark.isChecked() || chkFlgDamp.isChecked()
				|| chkFlgDanger.isChecked() || chkFlgGoodsDamage.isChecked()
				|| chkFlgGuarLost.isChecked() || chkFlgOdors.isChecked()
				|| chkFlgOilStained.isChecked() || chkFlgOther.isChecked()
				|| chkFlgUltrasize.isChecked() || chkFlgLogNo.isChecked()
				|| chkFlgMarksPortDif.isChecked() || chkFlgCdOrderDif.isChecked()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 保存按钮事件
	 */
	public void Btn_Save_OnClick(View view) {
		if (CheckSelect()) {
			// 其他勾选时，描述信息必输
			if (chkFlgOther.isChecked()
					&& editDescOther.getText().toString().trim().length() == 0) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN003_004_MSG));

				editDescOther.setFocusable(true);
				editDescOther.setFocusableInTouchMode(true);
				editDescOther.requestFocus();
				return;
			}

			if (imageAdpter.getCount() < 2) {
				Utils.showAlertDialog(_thisActivity,
						getString(R.string.DN003_001_MSG));
				return;
			}

			if (flag) {
				Map<String, Object> p1 = new HashMap<String, Object>();
				p1.put("depotID", depotID); // 进仓ID
				p1.put("errorNo", errorNo); // 异常报告ID

				NetworkHelper.getInstance().postJsonData(_thisActivity,
						"DN003_CheckErrorStatus", p1,
						new TypeToken<DN003ListData>() {
						}.getType(),
						new AmassHttpResponseHandler<DN003ListData>() {

							@Override
							protected void OnSuccess(DN003ListData response) {
								super.OnSuccess(response);
								DN003ListData dn = (DN003ListData) response;
								if (dn != null) {
									if (dn.getBolStatus() == 0) {
										// 执行更新
										UpdataErrorData();
									} else {
										Utils.showAlertDialog(
												_thisActivity,
												getString(R.string.DN003_003_MSG));
										return;
									}
								}
							}
						}, true);
			} else {

				int numAddImage = 0;
				int nCount = imageAdpter.getCount();
				for (int nIndex = 0; nIndex < nCount; nIndex++) {
					MoreData data = imageAdpter.getItem(nIndex);
					if (data.getDataType() == MoreData.DATA_TYPE) {
						ImageData imageData = (ImageData) data.getData();
						if (imageData.getImageId() == null) {
							try {
								numAddImage += 1;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

				if (numAddImage == 0) {
					Utils.showAlertDialog(_thisActivity,
							getString(R.string.DN003_005_MSG));
					return;
				}
				// 执行新增
				SaveErrorData();
			}
		} else {
			Utils.showAlertDialog(_thisActivity,
					getString(R.string.DN003_002_MSG));
			return;
		}
	}

	/*
	 * 更新数据
	 */
	private void UpdataErrorData() {
		DN003ListData depot = new DN003ListData();
		depot.setDepotId(depotID); // 进仓ID
		depot.setCdException(errorNo); // 异常报告ID
		depot.setFlgGuarLost(chkFlgGuarLost.isChecked()); // 保函缺失标志
		depot.setFlgGoodsDamage(chkFlgGoodsDamage.isChecked()); // 严重破损标志
		depot.setFlgDamp(chkFlgDamp.isChecked()); // 受潮标志
		depot.setFlgOilStained(chkFlgOilStained.isChecked()); // 漏油/油污标志
		depot.setFlgOdors(chkFlgOdors.isChecked()); // 有异味标记
		depot.setFlgBark(chkFlgBark.isChecked()); // 有树皮标记
		depot.setFlgDanger(chkFlgDanger.isChecked()); // 危险品/化学品/易燃品标记
		depot.setFlgUltrasize(chkFlgUltrasize.isChecked()); // 超长/超宽/超高标记
		depot.setFlgOther(chkFlgOther.isChecked()); // 其他异常标记
		depot.setDescOther(editDescOther.getText().toString()); // 其他异常描述
		depot.setFlgLogNo(chkFlgLogNo.isChecked());	//原木无章
		depot.setFlgMarksPortDif(chkFlgMarksPortDif.isChecked());//唛头目的港不符
		depot.setFlgCdOrderDif(chkFlgCdOrderDif.isChecked());//进仓编号不符

		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depot", depot);
		p1.put("cdOrder", cdOrder);
		p1.put("cdOrderPublic", cdOrderPublic);
		p1.put("coLoader", txtThNo.getText().toString().trim());

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN003_UpdataErrorData", p1, new TypeToken<DepotDN003>() {
				}.getType(), new AmassHttpResponseHandler<DepotDN003>() {

					@Override
					protected void OnSuccess(DepotDN003 response) {
						super.OnSuccess(response);
						// 保存照片
						SavePicture();

						flag = false;
					}
				}, true);
	}

	/*
	 * 新增数据
	 */
	private void SaveErrorData() {

		DN003ListData depot = new DN003ListData();
		depot.setDepotId(depotID); // 进仓ID
		depot.setFlgGuarLost(chkFlgGuarLost.isChecked()); // 保函缺失标志
		depot.setFlgGoodsDamage(chkFlgGoodsDamage.isChecked()); // 严重破损标志
		depot.setFlgDamp(chkFlgDamp.isChecked()); // 受潮标志
		depot.setFlgOilStained(chkFlgOilStained.isChecked()); // 漏油/油污标志
		depot.setFlgOdors(chkFlgOdors.isChecked()); // 有异味标记
		depot.setFlgBark(chkFlgBark.isChecked()); // 有树皮标记
		depot.setFlgDanger(chkFlgDanger.isChecked()); // 危险品/化学品/易燃品标记
		depot.setFlgUltrasize(chkFlgUltrasize.isChecked()); // 超长/超宽/超高标记
		depot.setFlgOther(chkFlgOther.isChecked()); // 其他异常标记
		depot.setDescOther(editDescOther.getText().toString()); // 其他异常描述
		depot.setFlgLogNo(chkFlgLogNo.isChecked());	//原木无章
		depot.setFlgMarksPortDif(chkFlgMarksPortDif.isChecked());//唛头目的港不符
		depot.setFlgCdOrderDif(chkFlgCdOrderDif.isChecked());//进仓编号不符

		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("depot", depot);
		p1.put("cdOrder", cdOrder);
		p1.put("cdOrderPublic", cdOrderPublic);
		p1.put("coLoader", txtThNo.getText().toString().trim());

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"DN003_SaveErrorData", p1, new TypeToken<DepotDN003>() {
				}.getType(), new AmassHttpResponseHandler<DepotDN003>() {

					@Override
					protected void OnSuccess(DepotDN003 response) {
						super.OnSuccess(response);
						// 保存照片
						SavePicture();
					}
				}, true);
	}

	/*
	 * 保存照片
	 */
	private void SavePicture() {
		try {
			RequestParams params = new RequestParams();
			params.put("cdOrder", cdOrderPublic);
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
					"DN003_UploadFiles", "ErrorPicture", params,
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

							ClearContral();
							// 清空照片
							imageAdpter.clear();
							imageAdpter.add(new MoreData());
							GetErrorList();
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
