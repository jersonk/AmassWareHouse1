package com.amassfreight.warehouse.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.DepotPP010;
import com.amassfreight.domain.MoreData;
import com.amassfreight.warehouse.R;

import com.google.gson.reflect.TypeToken;

public class PP010Activity_Depot extends BaseActivity {

	private LayoutInflater mInflater;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter pp001Adapter;
	private ListView listDetail;
	private List<MoreData> dataList;

	private String strNoBox;
	private String strType;

	/** 初始化事件 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		// 判断页面传值是否为空
		if (bundle != null) {
			strNoBox = bundle.get("noBox").toString();
			strType = bundle.get("boxType").toString();
		}
		setContentView(R.layout.activity_pp010_depot_list);
		// 设置标题栏是左边是否有返回事件
		setupActionBar();

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		listDetail = (ListView) findViewById(R.id.listDetail);

		dataList = new ArrayList<MoreData>();

		// 适配器
		pp001Adapter = new ArrayAdapter(getApplicationContext(), 0) {
			public View getView(int position, View convertView, ViewGroup parent) {
				DepotPP010 pp010Data = (DepotPP010) getItem(position);
				LinearLayout layout = new LinearLayout(getContext());
				try {
					mInflater.inflate(R.layout.activity_pp010_depot_list_item,
							layout, true);
					if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}
					TextView tvCommon = (TextView) layout
							.findViewById(R.id.tvCdOrder);
					tvCommon.setText(pp010Data.getCdOrder()); // 进仓编号

					tvCommon = (TextView) layout.findViewById(R.id.tvNoBatch);
					tvCommon.setText(String.valueOf(pp010Data.getNoBatch())); // 批次

					tvCommon = (TextView) layout.findViewById(R.id.tvNoLen);
					DecimalFormat df = new DecimalFormat("#");

					String strLWH = df.format(pp010Data.getNoLen()).toString()
							+ "*"
							+ df.format(pp010Data.getNoWidth()).toString()
							+ "*"
							+ df.format(pp010Data.getNoHeight()).toString();

					tvCommon.setText(strLWH); // 长
					if (pp010Data.getBolLenhColor()
							|| pp010Data.getBolWidthColor()
							|| pp010Data.getBolHeighthColor()) {
						tvCommon.setBackgroundResource(R.color.red);// setBackgroundResource
						// R.color.red setTextColor
					}

					tvCommon = (TextView) layout.findViewById(R.id.tvStockNum);
					tvCommon.setText(String.valueOf(pp010Data.getStockNum())); // 库存件数

					tvCommon = (TextView) layout.findViewById(R.id.tvPackAge);
					tvCommon.setText(pp010Data.getDepotPackage()); // 包装单位

					if (pp010Data.getBolLenhColor()
							|| pp010Data.getBolWidthColor()
							|| pp010Data.getBolHeighthColor()) {
						layout.setBackgroundResource(R.color.lightBlue);
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

		listDetail.setAdapter(pp001Adapter);
		InitData(dataList);

	}

	/* 数据赋值 */
	public void InitData(final List<MoreData> dataList) {

		Map<String, Object> pp001Map = new HashMap<String, Object>();
		pp001Map.put("strNoBox", strNoBox);// 查询条件
		pp001Map.put("strBoxType", strType);
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"PP010_GetTDepotDtData", pp001Map,
				new TypeToken<List<DepotPP010>>() {
				}.getType(), new AmassHttpResponseHandler<List<DepotPP010>>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(List<DepotPP010> response) {
						super.OnSuccess(response);
						List<DepotPP010> ms = (List<DepotPP010>) response;
						pp001Adapter.clear();
						pp001Adapter.addAll(ms);
						pp001Adapter.notifyDataSetChanged();
					}
				}, true);
	}

	/*
	 * 返回事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
