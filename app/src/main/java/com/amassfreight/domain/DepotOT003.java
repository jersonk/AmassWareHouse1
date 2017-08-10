package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class DepotOT003 implements Serializable {

	public DepotOT003() {
	}
	
	private String strCdOrderPublic;
	private String strCdLoader;      // 同行编号 add by yxq 2014/10/27
	private List<OT003DetailData> ot003List;
	private List<FileManageData> ot003InsFileList;
	private List<FileManageData> ot003MtFileList;
	// add by yxq 2014/09/12 begin
	private List<FileManageData> ot003VasFileList;   // 增值服务照片
	// add by yxq 2014/09/12 end
	// add by yxq 2014/08/29 begin
	private String containerStatus;   // 集装箱的当前状态
	private String containerStatusNm; // 集装箱的当前状态名
	// add by yxq 2014/08/29 end
	private String depotDtId;
	
	public List<OT003DetailData> getOt003List() {
		return ot003List;
	}
	public void setOt003List(List<OT003DetailData> ot003List) {
		this.ot003List = ot003List;
	}
	public List<FileManageData> getOt003InsFileList() {
		return ot003InsFileList;
	}
	public void setOt003InsFileList(List<FileManageData> ot003InsFileList) {
		this.ot003InsFileList = ot003InsFileList;
	}
	public List<FileManageData> getOt003MtFileList() {
		return ot003MtFileList;
	}
	public void setOt003MtFileList(List<FileManageData> ot003MtFileList) {
		this.ot003MtFileList = ot003MtFileList;
	}
	public String getStrCdOrderPublic() {
		return strCdOrderPublic;
	}
	public void setStrCdOrderPublic(String strCdOrderPublic) {
		this.strCdOrderPublic = strCdOrderPublic;
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
	public List<FileManageData> getOt003VasFileList() {
		return ot003VasFileList;
	}
	public void setOt003VasFileList(List<FileManageData> ot003VasFileList) {
		this.ot003VasFileList = ot003VasFileList;
	}
	public String getStrCdLoader() {
		return strCdLoader;
	}
	public void setStrCdLoader(String strCdLoader) {
		this.strCdLoader = strCdLoader;
	}
	public String getDepotDtId() {
		return depotDtId;
	}
	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}

}
