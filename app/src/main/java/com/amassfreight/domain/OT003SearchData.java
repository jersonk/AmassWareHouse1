package com.amassfreight.domain;

import java.io.Serializable;

// 显示的列表信息
public class OT003SearchData implements Serializable {
	private String depotId;		    // 进仓Id
	private String cdOrderPublic;	// 进仓编号共通
	private String coLoader;   		// 同行编号
	private String depotDtId;
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
	public String getDepotId() {
		return depotId;
	}
	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}
	public String getDepotDtId() {
		return depotDtId;
	}
	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}

}
