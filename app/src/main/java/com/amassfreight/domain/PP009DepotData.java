package com.amassfreight.domain;

import javax.security.auth.PrivateCredentialPermission;

public class PP009DepotData {
	public PP009DepotData() {
	}

	private String cdBusinessPublic; // 进仓编号
	private String cdBusiness; // 进仓编号
	private String noBatch; // 桩脚牌号
	private int originalNum; // 原件数
	private int stockNum; // 可装件数
	
	private int realCounts; // 实装件数
	private String flgPicking; // 拣货状态
	private String flgPacking; // 装箱状态
	private String depotId;// 货物明细ID
	private String matchId;
	private String posAndLocation;
	private String packingRemark; //装箱备注
	
	public String getPosAndLocation() {
		return posAndLocation;
	}

	public void setPosAndLocation(String posAndLocation) {
		this.posAndLocation = posAndLocation;
	}

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public String getDepotId() {
		return depotId;
	}

	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}

	public String getCdBusinessPublic() {
		return cdBusinessPublic;
	}

	public void setCdBusinessPublic(String cdBusinessPublic) {
		this.cdBusinessPublic = cdBusinessPublic;
	}

	public String getCdBusiness() {
		return cdBusiness;
	}

	public void setCdBusiness(String cdBusiness) {
		this.cdBusiness = cdBusiness;
	}

	public String getNoBatch() {
		return noBatch;
	}

	public void setNoBatch(String noBatch) {
		this.noBatch = noBatch;
	}

	public int getOriginalNum() {
		return originalNum;
	}

	public void setOriginalNum(int originalNum) {
		this.originalNum = originalNum;
	}

	public int getStockNum() {
		return stockNum;
	}

	public void setStockNum(int stockNum) {
		this.stockNum = stockNum;
	}

	public int getRealCounts() {
		return realCounts;
	}

	public void setRealCounts(int realCounts) {
		this.realCounts = realCounts;
	}

	public String getFlgPicking() {
		return flgPicking;
	}

	public void setFlgPicking(String flgPicking) {
		this.flgPicking = flgPicking;
	}

	public String getFlgPacking() {
		return flgPacking;
	}

	public void setFlgPacking(String flgPacking) {
		this.flgPacking = flgPacking;
	}

	public String getPackingRemark() {
		return packingRemark;
	}

	public void setPackingRemark(String packingRemark) {
		this.packingRemark = packingRemark;
	}

}
