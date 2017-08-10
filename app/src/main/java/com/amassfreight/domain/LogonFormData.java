package com.amassfreight.domain;

import java.io.Serializable;

import com.loopj.android.http.RequestParams;

public class LogonFormData implements BaseRequestData, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5463708598207652299L;

	public void setRequestData(RequestParams params) {
			params.put("userId", userId);
			params.put("password", password);
			params.put("depotNo", depotNo);
	}
	
	private String userId;
	private String password;
	private String depotNo;
	
	public String getDepotNo() {
		return depotNo;
	}
	public void setDepotNo(String depotNo) {
		this.depotNo = depotNo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
