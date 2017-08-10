package com.amassfreight.warehouse.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.OT002DetailData;
import com.amassfreight.warehouse.R;
import com.google.gson.reflect.TypeToken;

public class OT002Activity extends BaseActivity {

	@SuppressWarnings("rawtypes")
	private ArrayAdapter listAdapter;  	  // 附件适配器
	private LayoutInflater listInflater;
	private ListView listOt002;   	      // 尚未更新的
	private String strPosCd; 			  // 库区编号
	private String strPosNm;			  // 库区名字
	private TextView labelPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		strPosCd = (String) intent.getSerializableExtra("posCd");
		strPosNm = (String) intent.getSerializableExtra("posNm");
		setTitle(getString(R.string.OT002_Title_NotUpdate));   // 尚未更新
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ot002);
		listInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listOt002 = (ListView) findViewById(R.id.listOt002);
		labelPos = (TextView) findViewById(R.id.labelPos);
		labelPos.setText(String.format(getResources().getString(R.string.OT002_Label_Pos)
				,strPosNm));
		setupActionBar();
		// 显示未更新库位列表
		setAdapter();        // 适配器
		setNotScannedList(); // 数据
	}
	
	// 显示未更新库位列表
	@SuppressWarnings("rawtypes")
	public void setAdapter(){
		// 适配器
		listAdapter = new ArrayAdapter(getApplicationContext(), 0) {
			public View getView(int position, View convertView, ViewGroup parent) {
				RelativeLayout layout = new RelativeLayout(getContext());
				OT002DetailData data = (OT002DetailData) getItem(position);
				try {
					// 设置背景颜色
					if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}
					listInflater.inflate(R.layout.activity_ot002_not_scanned_list, layout,true);
					OT002DetailData item = (OT002DetailData) data;	
				    // 进仓编号
					TextView  t1 = (TextView) layout.findViewById(R.id.textCdOrder);  
					t1.setText(item.getCdOrder());
				    // 桩脚牌
					t1 = (TextView) layout.findViewById(R.id.textViewPileCard);  
					t1.setText(item.getBatchNo() + "-" + item.getPilecardNo());
				    // 库位 
			     	t1 = (TextView) layout.findViewById(R.id.textViewLocation);  
					t1.setText(item.getLocation());
				    // 同行编号
					t1 = (TextView) layout.findViewById(R.id.textCoLoader);  
					t1.setText(item.getCoLoader());
			     	// 件数 
			     	t1 = (TextView) layout.findViewById(R.id.textViewNum);  
					t1.setText(item.getNum());
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
		listOt002.setAdapter(listAdapter);
	}
	// 得到附件列表
	public void setNotScannedList(){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		if(strPosCd == null){
			strPosCd = "";
		}
		paraMap.put("posCd", strPosCd);    // 增值服务ID
		NetworkHelper.getInstance().postJsonData(_thisActivity,"OT002_GetNotScannedList", paraMap, 
			new TypeToken<List<OT002DetailData>>() {}.getType(),
			new AmassHttpResponseHandler<List<OT002DetailData>>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(List<OT002DetailData> response) {
						super.OnSuccess(response);
						List<OT002DetailData> ms = (List<OT002DetailData>) response;
						listAdapter.clear();
						listAdapter.addAll(ms);
						listAdapter.notifyDataSetChanged();
					}
				}, true);
	}

}
