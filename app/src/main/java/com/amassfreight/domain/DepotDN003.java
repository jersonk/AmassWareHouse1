package com.amassfreight.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DepotDN003 implements Serializable {

	public DepotDN003() {
	}

	private List<DN003ListData> dn003List;
	private List<FileManageData> dn003FileList;

	public List<DN003ListData> getDn003List() {
		return dn003List;
	}

	public void setDn003List(List<DN003ListData> dn003List) {
		this.dn003List = dn003List;
	}

	public List<FileManageData> getDn003FileList() {
		return dn003FileList;
	}

	public void setDn003InFileList(List<FileManageData> dn003FileList) {
		this.dn003FileList = dn003FileList;
	}
}
