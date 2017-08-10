package com.amassfreight.domain;

import java.io.Serializable;

public class PP006CntScanLock implements Serializable {

	public PP006CntScanLock(){}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean flag;
	public String remark;
}
