package com.amassfreight.domain;

import java.util.List;
import java.util.Map;

public class PageBaseData <T> {//implements Serializable {//implements BaseResponseData{
	private int TotalCount;
	private int PageCount;
	private List<T> dataList;
	private Map<String, Boolean> auths;
	public PageBaseData(){}

	
	
	public int getTotalCount() {
		return TotalCount;
	}
	public void setTotalCount(int totalCount) {
		TotalCount = totalCount;
	}
	public int getPageCount() {
		return PageCount;
	}
	public void setPageCount(int pageCount) {
		PageCount = pageCount;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}



	public Map<String, Boolean> getAuths() {
		return auths;
	}



	public void setAuths(Map<String, Boolean> auths) {
		this.auths = auths;
	}
	
}
