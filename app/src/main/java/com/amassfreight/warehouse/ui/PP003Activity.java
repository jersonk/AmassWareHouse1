package com.amassfreight.warehouse.ui;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.PP003DetailData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;

@SuppressLint("ShowToast")
public class PP003Activity extends BaseActivity{
	
	private String boxNo;             //传递的集箱号
	private TextView vesselNm;        //船名航次
	private TextView fdestNm;         //卸货港
	private TextView boxNo_text;      //集箱号
	private TextView boxNm;           //箱型
	private TextView mblCd;           //M.B/L NO
	private TextView containerCd;     //箱封号
	//private TextView deadLine;        //装箱最晚完成时间
	private TextView etd;             //ETD
	private TextView operator;        //操作
	private TextView portArea;        //港区


	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp003);
		setupActionBar();
		vesselNm = (TextView) findViewById(R.id.vesselNm);
		fdestNm = (TextView) findViewById(R.id.fdestNm);
		boxNo_text = (TextView) findViewById(R.id.boxNo_text);
		boxNm = (TextView) findViewById(R.id.boxNm);
		mblCd = (TextView) findViewById(R.id.mblCd);
		containerCd = (TextView) findViewById(R.id.containerCd);
		//deadLine = (TextView) findViewById(R.id.deadLine);
		etd = (TextView) findViewById(R.id.etd);
		operator = (TextView) findViewById(R.id.operator);
		portArea = (TextView) findViewById(R.id.portArea);
		
		Intent intent=getIntent(); 
        boxNo = intent.getStringExtra("boxNo");
        Map<String,Object> requestData = new HashMap<String,Object>();
		requestData.put("boxNo", boxNo);
		//获取配柜信息
		NetworkHelper.getInstance().postJsonData(PP003Activity.this, "PP003getDetail", requestData,PP003DetailData.class ,
				new AmassHttpResponseHandler<PP003DetailData>() {
			@Override
			protected void OnSuccess(PP003DetailData response) {
				super.OnSuccess(response);
				PP003DetailData detailData = (PP003DetailData) response;
				if(detailData != null ){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					if(detailData.getVesselNm()!=null){
						vesselNm.setText(detailData.getVesselNm());
					}
					if(detailData.getFdestNm()!=null){
						fdestNm.setText(detailData.getFdestNm());
					}
					if(detailData.getBoxNo()!=null){
						boxNo_text.setText(detailData.getBoxNo());
					}
					if(detailData.getBoxNm()!=null){
						boxNm.setText(detailData.getBoxNm());
					}
					if(detailData.getMblCd()!=null){
						mblCd.setText(detailData.getMblCd());
					}
					if(detailData.getContainerCd()!=null){
						containerCd.setText(detailData.getContainerCd());
					}
					if(detailData.getOperatorNm()!=null){
						operator.setText(detailData.getOperatorNm());
					}
					if(detailData.getPortarea()!=null){
						portArea.setText(detailData.getPortarea());
					}
					/*if(detailData.getPackingDeadLine()!=null){
						deadLine.setText(sdf.format(detailData.getPackingDeadLine()));
					}*/
					if(detailData.getEtd()!=null){
						etd.setText(sdf.format(detailData.getEtd()));
					}
				}else{
					Utils.showAlertDialogIntMsg(_thisActivity,R.string.PP005_msg_pickError_unknow);
				}
			}
		}, false);
	}  
	
	//返回
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//按下的如果是BACK，同时没有重复
	         PP003Activity.this.finish();   
			 //return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}	
    
}
