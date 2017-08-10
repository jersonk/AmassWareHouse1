package com.amassfreight.domain;

import java.util.List;

import com.amassfreight.warehouse.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PP004Adapter extends BaseAdapter{
	private List<OrderListResponseData> list;
	private LayoutInflater inflater;
	
	public PP004Adapter(Context context,List<OrderListResponseData> list) {
		// TODO Auto-generated constructor stub
		this.inflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	public void setList(List<OrderListResponseData> list) {
		this.list = list;
	}

	public LayoutInflater getInflater() {
		return inflater;
	}

	public void setInflater(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	public List<OrderListResponseData> getList() {
		return list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		OrderListResponseData g = list.get(arg0);
		

		View v = inflater.inflate(R.layout.activity_pp004_listview_item, arg2, false);
		
		TextView orderCdPublic = (TextView) v.findViewById(R.id.tv_jc_no);		
		TextView workStatus = (TextView) v.findViewById(R.id.tv_dp_status);
		TextView realNum = (TextView) v.findViewById(R.id.tv_ss_count);
		TextView packingRequire = (TextView) v.findViewById(R.id.zx_requirement);
		TextView numRemark = (TextView) v.findViewById(R.id.js_comment);
		TextView orderCd = (TextView) v.findViewById(R.id.order_cd); 		
		
		if (arg0 % 2 == 0) {
			v.setBackgroundResource(R.color.listview_back_odd);
		} else {
			v.setBackgroundResource(R.color.listview_back_uneven);
		}
		orderCdPublic.setText(g.getOrderCdPublic());
		orderCdPublic.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		if(g.getRealNum() == g.getPreconfigureNum()){
		    orderCdPublic.setTextColor(Color.BLUE);
		}else{
			orderCdPublic.setTextColor(Color.rgb(255,127,0));
		}
		if(g.getWorkStatus() == 2){
			workStatus.setText(R.string.status_work_done);
			workStatus.setBackgroundResource(R.color.gay);
		}
		else if(g.getWorkStatus() == 1){
			workStatus.setText(R.string.status_work_doing);
			workStatus.setBackgroundResource(R.color.yellow);
		}
		else{
			workStatus.setText(R.string.status_work_undo);
			workStatus.setBackgroundResource(0);
		}
		realNum.setText(String.valueOf(g.getRealNum()));
		//装箱要求不为空时，显示“有”
		if(g.getPackingRequire()!= null && !g.getPackingRequire().trim().equals("")){
		    packingRequire.setText(R.string.PP004_packingRequire_exist);
		    //packingRequire.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		    packingRequire.setTextColor(Color.RED);
		}
		if(g.getNumRemark()!=null && !"".equals(g.getNumRemark())){
		    numRemark.setText(g.getNumRemark().trim());
		}
		orderCd.setText(g.getOrderCd());
		return v;
	}

}
