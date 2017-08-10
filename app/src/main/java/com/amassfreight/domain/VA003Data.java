package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class VA003Data implements Serializable {
	private long attachmentCount;						 // 附件个数
    private String taskType;  						 	 // 是否是自己的任务
    private String hasCancelCompleteAth; 				 // 是否有"取消完成"权限
	private List<VA003FileList> va003FileList;	     	 // 照片信息
	private List<VA003PileCard> va003PileCardList_Orig;	 // 原桩脚牌信息
	private List<VA003PileCard> va003PileCardList_New;	 // 新桩脚牌信息
	private String flgService;							 // 状态
	private boolean flgCancel;							 // 是否取消完成过
	private int newNoBatch;								 // 新增的桩脚牌Id
	private Boolean flgCheck;							 // 是否核销标志
	private Boolean flgLoader;							 // 是否有装卸工参与
	public boolean isFlgCancel() {
		return flgCancel;
	}
	public void setFlgCancel(boolean flgCancel) {
		this.flgCancel = flgCancel;
	}
	public long getAttachmentCount() {
		return attachmentCount;
	}
	public void setAttachmentCount(long attachmentCount) {
		this.attachmentCount = attachmentCount;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getHasCancelCompleteAth() {
		return hasCancelCompleteAth;
	}
	public void setHasCancelCompleteAth(String hasCancelCompleteAth) {
		this.hasCancelCompleteAth = hasCancelCompleteAth;
	}
	public List<VA003FileList> getVa003FileList() {
		return va003FileList;
	}
	public void setVa003FileList(List<VA003FileList> va003FileList) {
		this.va003FileList = va003FileList;
	}
	public List<VA003PileCard> getVa003PileCardList_Orig() {
		return va003PileCardList_Orig;
	}
	public void setVa003PileCardList_Orig(List<VA003PileCard> va003PileCardList_Orig) {
		this.va003PileCardList_Orig = va003PileCardList_Orig;
	}
	public List<VA003PileCard> getVa003PileCardList_New() {
		return va003PileCardList_New;
	}
	public void setVa003PileCardList_New(List<VA003PileCard> va003PileCardList_New) {
		this.va003PileCardList_New = va003PileCardList_New;
	}
	public String getFlgService() {
		return flgService;
	}
	public void setFlgService(String flgService) {
		this.flgService = flgService;
	}
	public int getNewNoBatch() {
		return newNoBatch;
	}
	public void setNewNoBatch(int newNoBatch) {
		this.newNoBatch = newNoBatch;
	}
	public Boolean getFlgCheck() {
		return flgCheck;
	}
	public void setFlgCheck(Boolean flgCheck) {
		this.flgCheck = flgCheck;
	}
	public Boolean getFlgLoader() {
		return flgLoader;
	}
	public void setFlgLoader(boolean flgLoader) {
		this.flgLoader = flgLoader;
	}
}
