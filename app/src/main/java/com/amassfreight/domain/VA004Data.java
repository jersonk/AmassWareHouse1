package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class VA004Data implements Serializable {

	public VA004Data() {
	}
	
	private List<VasVA004> vasVa004List;   // 耗材列表
	private int flgAppoval; 			   // 审核标识
    // add by yxq 2014/09/23 begin
	private List<SelectDict> vasSumList;   // 计算方式1的数值 
    // add by yxq 2014/09/23 end	
	private Boolean flgSupervisor;   //主管权限标志 add by sdhuang 2014-12-29
	
	public List<VasVA004> getVasVa004List() {
		return vasVa004List;
	}
	public void setVasVa004List(List<VasVA004> vasVa004List) {
		this.vasVa004List = vasVa004List;
	}
	public int getFlgAppoval() {
		return flgAppoval;
	}
	public void setFlgAppoval(int flgAppoval) {
		this.flgAppoval = flgAppoval;
	}
	public List<SelectDict> getVasSumList() {
		return vasSumList;
	}
	public void setVasSumList(List<SelectDict> vasSumList) {
		this.vasSumList = vasSumList;
	}
	public Boolean getFlgSupervisor() {
		return flgSupervisor;
	}
	public void setFlgSupervisor(Boolean flgSupervisor) {
		this.flgSupervisor = flgSupervisor;
	}
}
