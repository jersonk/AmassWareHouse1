package com.amassfreight.domain;

public class PP008FclData {
	public PP008FclData() {
	}

	private String cdBusinessPublic; // 进仓编号
	private String cdBooking; // 关单号
	private int dropCounts; // 预配件数
	private String fclRemark; // 装箱要求
	private int realCounts; // 实装件数
	private String goodsEng; // 品名
	private String mark; // 唛头
	private String cdBusiness; // 进仓编号
	private int realNum;  //库存件数
	private String numRemark; //件数备注

	public String getCdBusiness() {
		return cdBusiness;
	}

	public void setCdBusiness(String cdBusiness) {
		this.cdBusiness = cdBusiness;
	}

	public int getRealCounts() {
		return realCounts;
	}

	public void setRealCounts(int realCounts) {
		this.realCounts = realCounts;
	}

	public String getGoodsEng() {
		return goodsEng;
	}

	public void setGoodsEng(String goodsEng) {
		this.goodsEng = goodsEng;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getCdBusinessPublic() {
		return cdBusinessPublic;
	}

	public void setCdBusinessPublic(String cdBusinessPublic) {
		this.cdBusinessPublic = cdBusinessPublic;
	}

	public String getCdBooking() {
		return cdBooking;
	}

	public void setCdBooking(String cdBooking) {
		this.cdBooking = cdBooking;
	}

	public int getDropCounts() {
		return dropCounts;
	}

	public void setDropCounts(int dropCounts) {
		this.dropCounts = dropCounts;
	}

	public String getFclRemark() {
		return fclRemark;
	}

	public void setFclRemark(String fclRemark) {
		this.fclRemark = fclRemark;
	}

	public int getRealNum() {
		return realNum;
	}

	public void setRealNum(int realNum) {
		this.realNum = realNum;
	}

	public String getNumRemark() {
		return numRemark;
	}

	public void setNumRemark(String numRemark) {
		this.numRemark = numRemark;
	}
}
