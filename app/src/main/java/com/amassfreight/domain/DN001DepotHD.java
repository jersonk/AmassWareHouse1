package com.amassfreight.domain;

import java.io.Serializable;

public class DN001DepotHD implements Serializable {

	public DN001DepotHD() {
	}

	private String cdOrder;
	private String cdOrderPublic;
	private String coLoader;

	public String getCdOrder() {
		return cdOrder;
	}

	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
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
