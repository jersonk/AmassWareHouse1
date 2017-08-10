package com.amassfreight.warehouse.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.MoreData;
import com.amassfreight.domain.PP001ResponseData;
import com.amassfreight.domain.PP001SearchData;
import com.amassfreight.domain.PageBaseData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.amassfreight.warehouse.ui.dialogs.PP001SearchDetailDialog;

import com.google.gson.reflect.TypeToken;

public class PP001Activity extends BaseActivity implements OnDialogOkListener {

	private LayoutInflater mInflater;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter pp001Adapter;
	private ListView listDetail;
	private PP001SearchData pp001SearchData;
	private int nPage;
	private List<MoreData> dataList;
	private Boolean bolIsDefault;

	/** 初始化事件 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pp001_list);
		nPage = 0;
		// 设置标题栏是左边是否有返回事件
		setupActionBar();
		bolIsDefault = true;

		pp001SearchData = new PP001SearchData();

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		listDetail = (ListView) findViewById(R.id.listDetail);

		dataList = new ArrayList<MoreData>();

		// 适配器
		pp001Adapter = new ArrayAdapter(getApplicationContext(), 0) {
			public View getView(int position, View convertView, ViewGroup parent) {
				MoreData data = (MoreData) getItem(position);
				LinearLayout layout = new LinearLayout(getContext());
				if (data.getDataType() == MoreData.DATA_TYPE) {
					PP001ResponseData pp001Data = (PP001ResponseData) data
							.getData();

					try {
						mInflater.inflate(R.layout.activity_pp001_list_item,
								layout, true);
						if (position % 2 == 0) {
							layout.setBackgroundResource(R.color.listview_back_odd);
						} else {
							layout.setBackgroundResource(R.color.listview_back_uneven);
						}
						TextView tvNoBox = (TextView) layout
								.findViewById(R.id.tvNoBox);
						tvNoBox.setText(pp001Data.getNoBox());// 集箱号
						tvNoBox.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
						tvNoBox.setTextColor(Color.BLUE);
						//不在场的箱子设置成红色
						if(pp001Data.getFlgPresent() == 0){
							tvNoBox.setTextColor(Color.RED);
						}
						TextView tvContainType = (TextView) layout
								.findViewById(R.id.tvContainType); // 装箱类型
						if ("1".equals(pp001Data.getContaType())) {

							tvContainType
									.setText(getString(R.string.CONTA_TYPE_X));
						} else if ("2".equals(pp001Data.getContaType())) {
							tvContainType
									.setText(getString(R.string.CONTA_TYPE_F));
						} else if ("6".equals(pp001Data.getContaType())) {
							tvContainType
									.setText(getString(R.string.CONTA_TYPE_S));
						}
						TextView tvStsPacking = (TextView) layout
								.findViewById(R.id.tvStsPacking); // 装箱状态

						if("0".equals(pp001Data.getStatusNow())){
							tvStsPacking.setText(R.string.status_now_0);
							Resources resource = (Resources) getBaseContext()
									.getResources();
							ColorStateList csl = (ColorStateList) resource
									.getColorStateList(R.color.orange);
							tvStsPacking.setTextColor(csl);
						}else{
						    tvStsPacking.setText(pp001Data.getStsPacking());
						    // 未开始
							if (getString(R.string.status_packing_undo).equals(
									pp001Data.getStsPacking())) {
								tvStsPacking.setTextColor(Color.RED);
							} else if (getString(
									R.string.status_packing_picking_doing).equals(
									pp001Data.getStsPacking())) {

								// 通过资源文件写在String.xml中
								Resources resource = (Resources) getBaseContext()
										.getResources();
								ColorStateList csl = (ColorStateList) resource
										.getColorStateList(R.color.orange);
								// 拣货中
								tvStsPacking.setTextColor(csl);
							} else if (getString(R.string.status_packing_doing)
									.equals(pp001Data.getStsPacking())) {
								// 装箱中
								tvStsPacking.setTextColor(Color.BLUE);
							} else {
								// 已装箱
								tvStsPacking.setTextColor(Color.GRAY);
							}
						}

						TextView tvDeadLine = (TextView) layout
								.findViewById(R.id.tvDeadLine); // 最晚装箱时间

						tvDeadLine.setText(pp001Data.getDtPackingDeadLine());

						TextView tvPackingName = (TextView) layout
								.findViewById(R.id.tvPackingName); // 装箱员

						TextView tvETD = (TextView) layout
								.findViewById(R.id.tvEtd); // ETD

						tvETD.setText(pp001Data.getDtETD());

						TextView tvLine = (TextView) layout
								.findViewById(R.id.tvLine); // 航线

						tvLine.setText(pp001Data.getAirLine());

						tvPackingName.setText(pp001Data.getNmPacking());

						
						@SuppressWarnings("unused")
						final String strContaType = pp001Data.getContaType();
						@SuppressWarnings("unused")
						final String strNoBox = pp001Data.getNoBox();

					} catch (Exception e) {
						try {
							throw e;
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}

					return layout;
				} else {
					mInflater.inflate(R.layout.list_more_item, layout, true);
					if (!data.isGetDataing()) {
						data.setGetDataing(true);
						// GetMoreData
						InitData(dataList, pp001SearchData);
					} else {

					}
					return layout;
				}
			}
		};

		listDetail.setAdapter(pp001Adapter);
		InitData(dataList, pp001SearchData);

		listDetail.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MoreData data = (MoreData) listDetail.getItemAtPosition(arg2);
				PP001ResponseData mData = (PP001ResponseData) data.getData();
				String strContaType = mData.getContaType();
				Intent intent = new Intent();
				// 整箱
				if ("2".equals(strContaType)) {
					intent.setClass(PP001Activity.this, PP010Activity_FCL.class);
					intent.putExtra("noBox", mData.getNoBox());
				} else if ("6".equals(strContaType)) {
					// 自拼箱
					intent.setClass(PP001Activity.this, PP010Activity_SCL.class);
					intent.putExtra("noBox", mData.getNoBox());
				} else if ("1".equals(strContaType)) {
					// 拼箱
					intent.setClass(PP001Activity.this, PP006Activity.class);
					intent.putExtra("boxNo", mData.getNoBox());
				}

				intent.putExtra("IsEdit", false);

				startActivity(intent);

			}

		});
	}

	/* 数据赋值 */
	public void InitData(final List<MoreData> dataList,
			PP001SearchData pp001Search) {

		Map<String, Object> pp001Map = new HashMap<String, Object>();
		pp001Map.put("pp001Search", pp001Search);// 查询条件
		pp001Map.put("nPage", String.valueOf(nPage));
		pp001Map.put("isDefault", bolIsDefault);
		if (nPage == 0) {
			NetworkHelper
					.getInstance()
					.postJsonData(
							_thisActivity,
							"PP001_GetItemData",
							pp001Map,
							new TypeToken<PageBaseData<PP001ResponseData>>() {
							}.getType(),
							new AmassHttpResponseHandler<PageBaseData<PP001ResponseData>>() {
								@SuppressWarnings("unchecked")
								@Override
								protected void OnSuccess(
										PageBaseData<PP001ResponseData> response) {
									super.OnSuccess(response);
									PageBaseData<PP001ResponseData> ms = (PageBaseData<PP001ResponseData>) response;
									pp001Adapter.clear();

									List<MoreData> allDatas = new ArrayList<MoreData>();
									for (int n = 0; n < ms.getDataList().size(); n++) {

										dataList.add(new MoreData(ms
												.getDataList().get(n)));
									}
									allDatas.addAll(dataList);
									if (allDatas.size() < ms.getTotalCount()) {
										allDatas.add(new MoreData());
									}
									nPage++;
									pp001Adapter.addAll(allDatas);
									pp001Adapter.notifyDataSetChanged();

									if (!bolIsDefault
											&& ms.getDataList().size() == 0) {
										Utils.showAlertDialogIntMsg(
												_thisActivity,
												R.string.PP001_DIALOG_001);
									}

								}
							}, true);
		} else {

			NetworkHelper
					.getInstance()
					.postJsonData(
							_thisActivity,
							"PP001_GetItemData",
							pp001Map,
							new TypeToken<PageBaseData<PP001ResponseData>>() {
							}.getType(),
							new AmassHttpResponseHandler<PageBaseData<PP001ResponseData>>() {
								@SuppressWarnings("unchecked")
								@Override
								protected void OnSuccess(
										PageBaseData<PP001ResponseData> response) {
									super.OnSuccess(response);
									PageBaseData<PP001ResponseData> ms = (PageBaseData<PP001ResponseData>) response;
									pp001Adapter.clear();

									List<MoreData> allDatas = new ArrayList<MoreData>();
									for (int n = 0; n < ms.getDataList().size(); n++) {

										dataList.add(new MoreData(ms
												.getDataList().get(n)));
									}
									allDatas.addAll(dataList);
									if (allDatas.size() < ms.getTotalCount()) {
										allDatas.add(new MoreData());
									}
									nPage++;
									pp001Adapter.addAll(allDatas);
									pp001Adapter.notifyDataSetChanged();

									if (!bolIsDefault
											&& ms.getDataList().size() == 0) {
										Utils.showAlertDialogIntMsg(
												_thisActivity,
												R.string.PP001_DIALOG_001);
									}

								}
							}, false);
		}
	}

	@SuppressWarnings("unused")
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pp001, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.search_detail_action:
			PP001SearchDetailDialog dlg = new PP001SearchDetailDialog(1,
					pp001SearchData, this);
			dlg.createDialog(this).show();

			return true;

		case R.id.refresh:
			dataList.clear();
			nPage = 0;
			InitData(dataList, pp001SearchData);
			listDetail.setSelection(0);
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {
		pp001SearchData = (PP001SearchData) data.getExtras().get(
				OnDialogOkListener.OK);
		dataList.clear();
		nPage = 0;
		bolIsDefault = false;
		InitData(dataList, pp001SearchData);
	}

}
