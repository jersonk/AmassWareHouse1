package com.amassfreight.domain;

import com.loopj.android.http.RequestParams;

public class PP005RequestData implements BaseRequestData{

	@Override
	public void setRequestData(RequestParams params) {
		params.put("orderCd", orderCd);
		
	}

	private String orderCd;

	public String getOrderCd() {
		return orderCd;
	}

	public void setOrderCd(String orderCd) {
		this.orderCd = orderCd;
	}


}
