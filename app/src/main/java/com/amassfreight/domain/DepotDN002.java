package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class DepotDN002 implements Serializable {

	public DepotDN002() {
	}

	private List<DN002ListData> dn002List;
	private List<FileManageData> dn002InFileList;
	private List<FileManageData> dn002MtFileList;
	private List<FileManageData> dn002KiFileList;
	// add by yxq 2014/08/21 begin
	private String MTMatchFlg;  // 唛头相符标识
	// add by yxq 2014/08/21 end
	// add by yxq 2014/08/26 begin
	private String noCarLice;       // 车牌号
	private String typeCar;         // 车辆类型
	private String noMultipleOrder; // 分票号
	private String noCarNum;        // 同车编号
	// add by yxq 2014/08/26 end
	private String noMultipleDepot; // 进仓分票号  add by yxq 2014/09/03
	private String reaMeaFlg;       // 复量标识符  add by yxq 2014/09/17
	private String noMultipleColo; // 同行分号
	
	private Boolean flgHydraulic;	//是否使用的液压车
	
	private Integer numHydraulic;		//使用液压车的件数
	
	private Boolean flgCdOrderDif;		//进仓编号不符
	
	private Boolean flgKarachiInvoice;	//KARACHI港货物是否有箱单和发票
	
	private Boolean flgBlackDrum;		//是否是黑色塑料桶包装
	
	private Boolean flgDrumHandle;		//是否用了夹桶器
	
	public List<DN002ListData> getDn002List() {
		return dn002List;
	}

	public void setDn002List(List<DN002ListData> dn002List) {
		this.dn002List = dn002List;
	}

	public List<FileManageData> getDn002InFileList() {
		return dn002InFileList;
	}

	public void setDn002InFileList(List<FileManageData> dn002InFileList) {
		this.dn002InFileList = dn002InFileList;
	}

	public List<FileManageData> getDn002MtFileList() {
		return dn002MtFileList;
	}

	public void setDn002MtFileList(List<FileManageData> dn002MtFileList) {
		this.dn002MtFileList = dn002MtFileList;
	}

	public String getMTMatchFlg() {
		return MTMatchFlg;
	}

	public void setMTMatchFlg(String mTMatchFlg) {
		MTMatchFlg = mTMatchFlg;
	}

	public String getNoCarLice() {
		return noCarLice;
	}

	public void setNoCarLice(String noCarLice) {
		this.noCarLice = noCarLice;
	}

	public String getTypeCar() {
		return typeCar;
	}

	public void setTypeCar(String typeCar) {
		this.typeCar = typeCar;
	}

	public String getNoMultipleOrder() {
		return noMultipleOrder;
	}

	public void setNoMultipleOrder(String noMultipleOrder) {
		this.noMultipleOrder = noMultipleOrder;
	}

	public String getNoCarNum() {
		return noCarNum;
	}

	public void setNoCarNum(String noCarNum) {
		this.noCarNum = noCarNum;
	}

	public String getNoMultipleDepot() {
		return noMultipleDepot;
	}

	public void setNoMultipleDepot(String noMultipleDepot) {
		this.noMultipleDepot = noMultipleDepot;
	}

	public String getReaMeaFlg() {
		return reaMeaFlg;
	}

	public void setReaMeaFlg(String reaMeaFlg) {
		this.reaMeaFlg = reaMeaFlg;
	}

	public String getNoMultipleColo() {
		return noMultipleColo;
	}

	public void setNoMultipleColo(String noMultipleColo) {
		this.noMultipleColo = noMultipleColo;
	}

	public Boolean getFlgHydraulic() {
		return flgHydraulic;
	}

	public void setFlgHydraulic(Boolean flgHydraulic) {
		this.flgHydraulic = flgHydraulic;
	}

	public Integer getNumHydraulic() {
		return numHydraulic;
	}

	public void setNumHydraulic(int numHydraulic) {
		this.numHydraulic = numHydraulic;
	}

	public Boolean getFlgCdOrderDif() {
		return flgCdOrderDif;
	}

	public void setFlgCdOrderDif(Boolean flgCdOrderDif) {
		this.flgCdOrderDif = flgCdOrderDif;
	}

	public Boolean getFlgKarachiInvoice() {
		return flgKarachiInvoice;
	}

	public void setFlgKarachiInvoice(Boolean flgKarachiInvoice) {
		this.flgKarachiInvoice = flgKarachiInvoice;
	}

	public List<FileManageData> getDn002KiFileList() {
		return dn002KiFileList;
	}

	public void setDn002KiFileList(List<FileManageData> dn002KiFileList) {
		this.dn002KiFileList = dn002KiFileList;
	}

	public Boolean getFlgBlackDrum() {
		return flgBlackDrum;
	}

	public void setFlgBlackDrum(Boolean flgBlackDrum) {
		this.flgBlackDrum = flgBlackDrum;
	}

	public Boolean getFlgDrumHandle() {
		return flgDrumHandle;
	}

	public void setFlgDrumHandle(Boolean flgDrumHandle) {
		this.flgDrumHandle = flgDrumHandle;
	}

}
