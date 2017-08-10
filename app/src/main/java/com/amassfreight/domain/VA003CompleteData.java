package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class VA003CompleteData implements Serializable {
	
	public VA003CompleteData() {
	}

	private String cdOrder;
	private String serviceId;
	private List<DN004TDepotDt> listDN004TDepotDt;     // 新增的货物明细表
	private List<DN004TLoad> listDN004TLoad;     	   // 新增的货物装卸表
	private List<DN004TDepotDt> listDN004TDepotDtUpd;  // 修改的货物明细表
	private List<DN004TLoad> listDN004TLoadUpd;        // 修改的货物装卸表
	private List<String> listPileCardNew_Upd;		   // 修改的货物装卸表
	private List<String> listPileCardNew_Del;		   // 删除的新增桩脚牌
	// add by yxq 2014/09/22 begin
	private String saveType;						   // 保存类型
	// add by yxq 2014/09/22 end
	// add by yxq 2014/10/16 begin
    public List<Integer> MapNoMulDepotKey;			   // 分票号集合的Key
    public List<String> MapNoMulDepotValue;			   // 分票号集合的Value
	// add by yxq 2014/10/16 end
	
	/* 注销  by yxq 2014/09/22
	private List<String> listPileCard_Orig_ScanedUpload;  // 扫描的桩脚牌(分货用)
	*/
    private boolean flgCheck;  // 核销标志  add by sdhuang 2014-12-16
    
    private boolean flgLoader; // 装卸工标记
    
	public String getCdOrder() {
		return cdOrder;
	}
	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public List<DN004TDepotDt> getListDN004TDepotDt() {
		return listDN004TDepotDt;
	}
	public void setListDN004TDepotDt(List<DN004TDepotDt> listDN004TDepotDt) {
		this.listDN004TDepotDt = listDN004TDepotDt;
	}
	public List<DN004TLoad> getListDN004TLoad() {
		return listDN004TLoad;
	}
	public void setListDN004TLoad(List<DN004TLoad> listDN004TLoad) {
		this.listDN004TLoad = listDN004TLoad;
	}
	public List<DN004TDepotDt> getListDN004TDepotDtUpd() {
		return listDN004TDepotDtUpd;
	}
	public void setListDN004TDepotDtUpd(List<DN004TDepotDt> listDN004TDepotDtUpd) {
		this.listDN004TDepotDtUpd = listDN004TDepotDtUpd;
	}
	public List<DN004TLoad> getListDN004TLoadUpd() {
		return listDN004TLoadUpd;
	}
	public void setListDN004TLoadUpd(List<DN004TLoad> listDN004TLoadUpd) {
		this.listDN004TLoadUpd = listDN004TLoadUpd;
	}
	public List<String> getListPileCardNew_Del() {
		return listPileCardNew_Del;
	}
	public void setListPileCardNew_Del(List<String> listPileCardNew_Del) {
		this.listPileCardNew_Del = listPileCardNew_Del;
	}
	public List<String> getListPileCardNew_Upd() {
		return listPileCardNew_Upd;
	}
	public void setListPileCardNew_Upd(List<String> listPileCardNew_Upd) {
		this.listPileCardNew_Upd = listPileCardNew_Upd;
	}
	/* 注销 by yxq 2014/09/22
	public List<String> getListPileCard_Orig_ScanedUpload() {
		return listPileCard_Orig_ScanedUpload;
	}
	public void setListPileCard_Orig_ScanedUpload(
			List<String> listPileCard_Orig_ScanedUpload) {
		this.listPileCard_Orig_ScanedUpload = listPileCard_Orig_ScanedUpload;
	}*/
	public String getSaveType() {
		return saveType;
	}
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	public List<Integer> getMapNoMulDepotKey() {
		return MapNoMulDepotKey;
	}
	public void setMapNoMulDepotKey(List<Integer> mapNoMulDepotKey) {
		MapNoMulDepotKey = mapNoMulDepotKey;
	}
	public List<String> getMapNoMulDepotValue() {
		return MapNoMulDepotValue;
	}
	public void setMapNoMulDepotValue(List<String> mapNoMulDepotValue) {
		MapNoMulDepotValue = mapNoMulDepotValue;
	}
	public boolean isFlgCheck() {
		return flgCheck;
	}
	public void setFlgCheck(boolean flgCheck) {
		this.flgCheck = flgCheck;
	}
	public boolean isFlgLoader() {
		return flgLoader;
	}
	public void setFlgLoader(boolean flgLoader) {
		this.flgLoader = flgLoader;
	}

}
