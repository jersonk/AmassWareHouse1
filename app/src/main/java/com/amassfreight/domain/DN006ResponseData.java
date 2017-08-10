package com.amassfreight.domain;

import java.io.Serializable;

public class DN006ResponseData implements Serializable {

	public DN006ResponseData() {
	}

	private String depotDtID;
	private String cdOrder;
	private String coLoader;
	private int noBatch;
	private String noPilecard;
	private String defaultPos;
	private String posLocation;

	public String getDepotDtID() {
		return depotDtID;
	}

	public void setDepotDtID(String depotDtID) {
		this.depotDtID = depotDtID;
	}

	public String getDepotID() {
		return cdOrder;
	}

	public void setDepotID(String cdOrder) {
		this.cdOrder = cdOrder;
	}

	public String getCoLoader() {
		return coLoader;
	}

	public void setCoLoader(String coLoader) {
		this.coLoader = coLoader;
	}

	public int getNoBatch() {
		return noBatch;
	}

	public void setNoBatch(int noBatch) {
		this.noBatch = noBatch;
	}

	public String getNoPilecard() {
		return noPilecard;
	}

	public void setNoPilecard(String noPilecard) {
		this.noPilecard = noPilecard;
	}

	public String getDefaultPos() {
		return defaultPos;
	}

	public void setDefaultPos(String defaultPos) {
		this.defaultPos = defaultPos;
	}

	public String getCdOrder() {
		return cdOrder;
	}

	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
	}

	public String getPosLocation() {
		return posLocation;
	}

	public void setPosLocation(String posLocation) {
		this.posLocation = posLocation;
	}
}
