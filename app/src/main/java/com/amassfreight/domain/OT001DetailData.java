package com.amassfreight.domain;

import java.io.Serializable;
import java.util.Date;

// 显示的列表信息
public class OT001DetailData implements Serializable {
	private String batchNo;    // 批次
	private String pilecardNo; // 桩脚牌Id
	private String numInData;  // 数据库中的数量
	private String numInFact;  // 实际的数量
	private String status;     // 状态
	private String cdOrderPublic;    // 进仓编号
	private String coLoader;   // 同行编号
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getPilecardNo() {
		return pilecardNo;
	}
	public void setPilecardNo(String pilecardNo) {
		this.pilecardNo = pilecardNo;
	}
	public String getNumInData() {
		return numInData;
	}
	public void setNumInData(String numInData) {
		this.numInData = numInData;
	}
	public String getNumInFact() {
		return numInFact;
	}
	public void setNumInFact(String numInFact) {
		this.numInFact = numInFact;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCdOrderPublic() {
		return cdOrderPublic;
	}
	public void setCdOrderPublic(String cdOrderPublic) {
		this.cdOrderPublic = cdOrderPublic;
	}
	public String getCoLoader() {
		return coLoader;
	}
	public void setCoLoader(String coLoader) {
		this.coLoader = coLoader;
	}
}
