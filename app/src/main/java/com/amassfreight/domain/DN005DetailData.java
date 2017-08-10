package com.amassfreight.domain;

import java.io.Serializable;

public class DN005DetailData implements Serializable {
	public DN005DetailData() {
	}

	private String depotDtID; // 货物明细ID
	private String depotID; // 进仓ID
	private int noBatch; // 批次
	private boolean flgGoodsDamageSlight; // 货物轻微破损标志
	private int numGoodsDamageSlight; // 货物轻微破损件数
	private boolean flgGoodsDamage; // 货物一般破损标志
	private int numGoodsDamage; // 货物一般破损件数
	private boolean flgGoodsDamageSerious; // 货物严重破损标志
	private int numGoodsDamageSerious; // 货物严重破损件数
	private boolean flgLogNo; // 原木无章标志
	private int numLogNo; // 原木无章件数
	private boolean flgWarp; // 变形标志
	private int numWarp; // 变形件数
	private boolean flgDamp; // 一般受潮标志
	private int numDamp; // 一般受潮件数
	private boolean flgDampSlight; // 轻微受潮标志
	private int numDampSlight; // 轻微受潮件数
	private boolean flgDampSerious; // 严重受潮标志
	private int numDampSerious; // 严重受潮件数
	private boolean flgOilStained; // 油污标志
	private int numOilStained; // 油污件数
	private boolean flgLogYes; // 原木有章标志
	private boolean flgSimplePackage; // 包装简易标志
	private boolean flgMarksDif; // 唛头不符标志
	private boolean flgMarksExist; // 无唛头标志
	private String goodsDamageRemark; // 货物破损备注
	private boolean flgBark; // 树皮
	private int numBark; // 树皮
	private boolean flgDamageOthers; //其他异常信息
	private boolean flgPolluteSlight; //轻微污染
	private int numPolluteSlight;		//轻微污染件数
	private boolean flgPolluteSerious; //严重污染
	private int numPolluteSerious;		//严重污染件数	

	public String getDepotDtID() {
		return depotDtID;
	}

	public void setDepotDtID(String depotDtID) {
		this.depotDtID = depotDtID;
	}

	public String getDepotID() {
		return depotID;
	}

	public void setDepotID(String depotID) {
		this.depotID = depotID;
	}

	public int getNoBatch() {
		return noBatch;
	}

	public void setNoBatch(int noBatch) {
		this.noBatch = noBatch;
	}

	public boolean isFlgGoodsDamageSlight() {
		return flgGoodsDamageSlight;
	}

	public void setFlgGoodsDamageSlight(boolean flgGoodsDamageSlight) {
		this.flgGoodsDamageSlight = flgGoodsDamageSlight;
	}

	public int getNumGoodsDamageSlight() {
		return numGoodsDamageSlight;
	}

	public void setNumGoodsDamageSlight(int numGoodsDamageSlight) {
		this.numGoodsDamageSlight = numGoodsDamageSlight;
	}

	public boolean isFlgGoodsDamage() {
		return flgGoodsDamage;
	}

	public void setFlgGoodsDamage(boolean flgGoodsDamage) {
		this.flgGoodsDamage = flgGoodsDamage;
	}

	public int getNumGoodsDamage() {
		return numGoodsDamage;
	}

	public void setNumGoodsDamage(int numGoodsDamage) {
		this.numGoodsDamage = numGoodsDamage;
	}

	public boolean isFlgGoodsDamageSerious() {
		return flgGoodsDamageSerious;
	}

	public void setFlgGoodsDamageSerious(boolean flgGoodsDamageSerious) {
		this.flgGoodsDamageSerious = flgGoodsDamageSerious;
	}

	public int getNumGoodsDamageSerious() {
		return numGoodsDamageSerious;
	}

	public void setNumGoodsDamageSerious(int numGoodsDamageSerious) {
		this.numGoodsDamageSerious = numGoodsDamageSerious;
	}

	public boolean isFlgLogNo() {
		return flgLogNo;
	}

	public void setFlgLogNo(boolean flgLogNo) {
		this.flgLogNo = flgLogNo;
	}

	public int getNumLogNo() {
		return numLogNo;
	}

	public void setNumLogNo(int numLogNo) {
		this.numLogNo = numLogNo;
	}

	public boolean isFlgWarp() {
		return flgWarp;
	}

	public void setFlgWarp(boolean flgWarp) {
		this.flgWarp = flgWarp;
	}

	public int getNumWarp() {
		return numWarp;
	}

	public void setNumWarp(int numWarp) {
		this.numWarp = numWarp;
	}

	public boolean isFlgDamp() {
		return flgDamp;
	}

	public void setFlgDamp(boolean flgDamp) {
		this.flgDamp = flgDamp;
	}

	public int getNumDamp() {
		return numDamp;
	}

	public void setNumDamp(int numDamp) {
		this.numDamp = numDamp;
	}

	public boolean isFlgDampSlight() {
		return flgDampSlight;
	}

	public void setFlgDampSlight(boolean flgDampSlight) {
		this.flgDampSlight = flgDampSlight;
	}

	public int getNumDampSlight() {
		return numDampSlight;
	}

	public void setNumDampSlight(int numDampSlight) {
		this.numDampSlight = numDampSlight;
	}

	public boolean isFlgDampSerious() {
		return flgDampSerious;
	}

	public void setFlgDampSerious(boolean flgDampSerious) {
		this.flgDampSerious = flgDampSerious;
	}

	public int getNumDampSerious() {
		return numDampSerious;
	}

	public void setNumDampSerious(int numDampSerious) {
		this.numDampSerious = numDampSerious;
	}

	public boolean isFlgOilStained() {
		return flgOilStained;
	}

	public void setFlgOilStained(boolean flgOilStained) {
		this.flgOilStained = flgOilStained;
	}

	public int getNumOilStained() {
		return numOilStained;
	}

	public void setNumOilStained(int numOilStained) {
		this.numOilStained = numOilStained;
	}

	public boolean isFlgLogYes() {
		return flgLogYes;
	}

	public void setFlgLogYes(boolean flgLogYes) {
		this.flgLogYes = flgLogYes;
	}

	public boolean isFlgSimplePackage() {
		return flgSimplePackage;
	}

	public void setFlgSimplePackage(boolean flgSimplePackage) {
		this.flgSimplePackage = flgSimplePackage;
	}

	public boolean isFlgMarksDif() {
		return flgMarksDif;
	}

	public void setFlgMarksDif(boolean flgMarksDif) {
		this.flgMarksDif = flgMarksDif;
	}

	public String getGoodsDamageRemark() {
		return goodsDamageRemark;
	}

	public void setGoodsDamageRemark(String goodsDamageRemark) {
		this.goodsDamageRemark = goodsDamageRemark;
	}

	public boolean isFlgBark() {
		return flgBark;
	}

	public void setFlgBark(boolean flgBark) {
		this.flgBark = flgBark;
	}

	public int getNumBark() {
		return numBark;
	}

	public void setNumBark(int numBark) {
		this.numBark = numBark;
	}

	public boolean isFlgMarksExist() {
		return flgMarksExist;
	}

	public void setFlgMarksExist(boolean flgMarksExist) {
		this.flgMarksExist = flgMarksExist;
	}

	public boolean isFlgDamageOthers() {
		return flgDamageOthers;
	}

	public void setFlgDamageOthers(boolean flgDamageOthers) {
		this.flgDamageOthers = flgDamageOthers;
	}

	public boolean isFlgPolluteSlight() {
		return flgPolluteSlight;
	}

	public void setFlgPolluteSlight(boolean flgPolluteSlight) {
		this.flgPolluteSlight = flgPolluteSlight;
	}

	public int getNumPolluteSlight() {
		return numPolluteSlight;
	}

	public void setNumPolluteSlight(int numPolluteSlight) {
		this.numPolluteSlight = numPolluteSlight;
	}

	public boolean isFlgPolluteSerious() {
		return flgPolluteSerious;
	}

	public void setFlgPolluteSerious(boolean flgPolluteSerious) {
		this.flgPolluteSerious = flgPolluteSerious;
	}

	public int getNumPolluteSerious() {
		return numPolluteSerious;
	}

	public void setNumPolluteSerious(int numPolluteSerious) {
		this.numPolluteSerious = numPolluteSerious;
	}
}
