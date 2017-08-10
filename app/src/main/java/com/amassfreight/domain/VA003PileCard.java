package com.amassfreight.domain;

import java.io.Serializable;

public class VA003PileCard implements Serializable {
	
	public VA003PileCard() {
	}

	private String serviceId;  	    // 增值服务ID
	private String depotDtId;     	// 货物明细ID	
	private int noBatch;     	    // 批号	
	private String noPileCard;      // 桩脚牌ID
	private String pos;				// 库区
	private String location;	    // 库位
	private boolean flgPileCardPicking; // 桩脚牌拣货标记
	private String depotNum;           // 进仓件数
	private String kgs;				// 重量
	private String cbm;				// 体积
	// add by yxq 2014/09/24 begin
	private String depotId;			// 进仓Id
	private String coLoader;		// 同行编号
	// add by yxq 2014/09/24 end
	// add by yxq 2014/10/16 begin
	private String noMultipleDepot; // 进仓分票号
	// add by yxq 2014/10/16 end
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getDepotDtId() {
		return depotDtId;
	}
	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}
	public int getNoBatch() {
		return noBatch;
	}
	public void setNoBatch(int noBatch) {
		this.noBatch = noBatch;
	}
	public String getNoPileCard() {
		return noPileCard;
	}
	public void setNoPileCard(String noPileCard) {
		this.noPileCard = noPileCard;
	}
	public boolean isFlgPileCardPicking() {
		return flgPileCardPicking;
	}
	public void setFlgPileCardPicking(boolean flgPileCardPicking) {
		this.flgPileCardPicking = flgPileCardPicking;
	}
	public String getDepotNum() {
		return depotNum;
	}
	public void setDepotNum(String depotNum) {
		this.depotNum = depotNum;
	}
	public String getKgs() {
		return kgs;
	}
	public void setKgs(String kgs) {
		this.kgs = kgs;
	}
	public String getCbm() {
		return cbm;
	}
	public void setCbm(String cbm) {
		this.cbm = cbm;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDepotId() {
		return depotId;
	}
	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}
	public String getCoLoader() {
		return coLoader;
	}
	public void setCoLoader(String coLoader) {
		this.coLoader = coLoader;
	}
	public String getNoMultipleDepot() {
		return noMultipleDepot;
	}
	public void setNoMultipleDepot(String noMultipleDepot) {
		this.noMultipleDepot = noMultipleDepot;
	}

}
