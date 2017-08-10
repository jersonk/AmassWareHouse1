package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class DepotDN001 implements Serializable {

	public DepotDN001() {
	}

	private String cdOrder;
	private String cdOrderPublic;
	private String coLoader;
	private String nmAirlines;
	private String orderType;
	public String cdOrderType;
	private String stsPacking;
	private String rank;
	private String special;
	public String qtNum;
	private String qtKgs;
	private String qtCbm;
	private String nmWpod;
	private String mPackage;
	private String nmPlanner;
	private String marks;
	private String remark;
	private boolean bolRole;
	private boolean bolflgInStore;
	private String flgInStore;
	// add by yxq 2014/08/29 begin
	private String containerStatus;   // 集装箱的当前状态
	private String containerStatusNm; // 集装箱的当前状态名
	// add by yxq 2014/08/29 end
    // add by yxq 2014/09/01 begin
	private String nmCustAbbr;        // 客户简称
    // add by yxq 2014/09/01 end
	private boolean flgGuarYes;      // 有无保函
	private String cdCust;            // 客户编号
	private int flgCommChemical;	//一般化工品标记
	private boolean flgShowMSDS;	//是否要显示MSDS
	private String jjn;						//应急处理
	private String fqn;						//消除措施
	private String cczyn;					//安全操作的注意事项
	private String ccczn;					//安全储存的条件

	private List<DN001ListData> dn001List;

	public String getCdOrderPublic() {
		return cdOrderPublic;
	}

	public void setCdOrderPublic(String cdOrderPublic) {
		this.cdOrderPublic = cdOrderPublic;
	}

	public String getCdOrder() {
		return cdOrder;
	}

	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
	}

	public String getCoLoader() {
		return coLoader;
	}

	public void setCoLoader(String coLoader) {
		this.coLoader = coLoader;
	}

	public String getNmAirlines() {
		return nmAirlines;
	}

	public void setNmAirlines(String nmAirlines) {
		this.nmAirlines = nmAirlines;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getCdOrderType() {
		return cdOrderType;
	}

	public void setCdOrderType(String cdOrderType) {
		this.cdOrderType = cdOrderType;
	}

	public String getStsPacking() {
		return stsPacking;
	}

	public void setStsPacking(String stsPacking) {
		this.stsPacking = stsPacking;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getSpecial() {
		return special;
	}

	public void setSpecial(String special) {
		this.special = special;
	}

	public String getQtNum() {
		return qtNum;
	}

	public void setQtNum(String qtNum) {
		this.qtNum = qtNum;
	}

	public String getQtKgs() {
		return qtKgs;
	}

	public void setQtKgs(String qtKgs) {
		this.qtKgs = qtKgs;
	}

	public String getQtCbm() {
		return qtCbm;
	}

	public void setQtCbm(String qtCbm) {
		this.qtCbm = qtCbm;
	}

	public String getNmWpod() {
		return nmWpod;
	}

	public void setNmWpod(String nmWpod) {
		this.nmWpod = nmWpod;
	}

	public String getmPackage() {
		return mPackage;
	}

	public void setmPackage(String mPackage) {
		this.mPackage = mPackage;
	}

	public String getNmPlanner() {
		return nmPlanner;
	}

	public void setNmPlanner(String nmPlanner) {
		this.nmPlanner = nmPlanner;
	}

	public String getMarks() {
		return marks;
	}

	public void setMarks(String marks) {
		this.marks = marks;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isBolRole() {
		return bolRole;
	}

	public void setBolRole(boolean bolRole) {
		this.bolRole = bolRole;
	}

	public boolean isBolflgInStore() {
		return bolflgInStore;
	}

	public void setBolflgInStore(boolean bolflgInStore) {
		this.bolflgInStore = bolflgInStore;
	}

	public String getFlgInStore() {
		return flgInStore;
	}

	public void setFlgInStore(String flgInStore) {
		this.flgInStore = flgInStore;
	}

	public List<DN001ListData> getDn001List() {
		return dn001List;
	}

	public void setDn001List(List<DN001ListData> dn001List) {
		this.dn001List = dn001List;
	}

	public String getContainerStatus() {
		return containerStatus;
	}

	public void setContainerStatus(String containerStatus) {
		this.containerStatus = containerStatus;
	}

	public String getContainerStatusNm() {
		return containerStatusNm;
	}

	public void setContainerStatusNm(String containerStatusNm) {
		this.containerStatusNm = containerStatusNm;
	}

	public String getNmCustAbbr() {
		return nmCustAbbr;
	}

	public void setNmCustAbbr(String nmCustAbbr) {
		this.nmCustAbbr = nmCustAbbr;
	}

	public String getCdCust() {
		return cdCust;
	}

	public void setCdCust(String cdCust) {
		this.cdCust = cdCust;
	}

	public boolean isFlgGuarYes() {
		return flgGuarYes;
	}

	public void setFlgGuarYes(boolean flgGuarYes) {
		this.flgGuarYes = flgGuarYes;
	}

	public int getFlgCommChemical() {
		return flgCommChemical;
	}

	public void setFlgCommChemical(int flgCommChemical) {
		this.flgCommChemical = flgCommChemical;
	}

	public String getJjn() {
		return jjn;
	}

	public void setJjn(String jjn) {
		this.jjn = jjn;
	}

	public String getFqn() {
		return fqn;
	}

	public void setFqn(String fqn) {
		this.fqn = fqn;
	}

	public String getCczyn() {
		return cczyn;
	}

	public void setCczyn(String cczyn) {
		this.cczyn = cczyn;
	}

	public String getCcczn() {
		return ccczn;
	}

	public void setCcczn(String ccczn) {
		this.ccczn = ccczn;
	}

	public boolean isFlgShowMSDS() {
		return flgShowMSDS;
	}

	public void setFlgShowMSDS(boolean flgShowMSDS) {
		this.flgShowMSDS = flgShowMSDS;
	}

}
