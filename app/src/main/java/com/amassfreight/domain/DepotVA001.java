package com.amassfreight.domain;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amassfreight.utils.Utils;

public class DepotVA001 implements Serializable {

	public DepotVA001(){}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5727707879970408208L;

	public String getCdOrderPublic() {
		return cdOrderPublic;
	}

	public void setCdOrderPublic(String cdOrderPublic) {
		this.cdOrderPublic = cdOrderPublic;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	 private String cdOrder ;
	 private String remark ;
	 private String serviceType;
	 private String flgService;
	 private String planNm;
	 private String depotNo;
	 private Date dtApply;
	 // add by yxq 2014/06/18 begin
	 private Date dtEnd; 		   // 最晚时间
	 private Date dtAct;  		   // 实际时间
	 public Boolean flgEmergency;  // 是否加急
	// add by yxq 2014/06/18 end
	 private String cdOrderPublic;
	 private String serviceId;
	 private Integer flgApproval;
     public String getCdOrder() {
		return cdOrder;
	}

	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getFlgService() {
		return flgService;
	}

	public void setFlgService(String flgService) {
		this.flgService = flgService;
	}

	public String getDepotNo() {
		return depotNo;
	}

	public void setDepotNo(String depotNo) {
		this.depotNo = depotNo;
	}

	public Date getDtApply() {
		return dtApply;
	}

	public void setDtApply(Date dtApply) {
		this.dtApply = dtApply;
	}

	public Date getDtEnd() {
		return dtEnd;
	}

	public void setDtEnd(Date dtEnd) {
		this.dtEnd = dtEnd;
	}

	public Date getDtAct() {
		return dtAct;
	}

	public void setDtAct(Date dtAct) {
		this.dtAct = dtAct;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	public String getPlanNm() {
		return planNm;
	}

	public void setPlanNm(String planNm) {
		this.planNm = planNm;
	}
	public Integer getFlgApproval() {
		return flgApproval;
	}
	public void setFlgApproval(Integer flgApproval) {
		this.flgApproval = flgApproval;
	}
	 public Boolean getFlgEmergency() {
		return flgEmergency;
	}
	public void setFlgEmergency(Boolean flgEmergency) {
		this.flgEmergency = flgEmergency;
	}
	private int version ;
}
