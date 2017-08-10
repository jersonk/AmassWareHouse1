package com.amassfreight.domain;

import java.util.Date;

public class TDelivDt {
	private String depot_id;
	private String cd_order;
	private String co_loader;
	private String depot_dt_id;
	private String no_batch;
	private int deliv_num;
	private String deliv_remark;
	private Date deliv_time;
	private String deliv_user;

	public TDelivDt() {
		// TODO Auto-generated constructor stub
	}

	public String getDepot_id() {
		return depot_id;
	}

	public void setDepot_id(String depot_id) {
		this.depot_id = depot_id;
	}

	public String getCd_order() {
		return cd_order;
	}

	public void setCd_order(String cd_order) {
		this.cd_order = cd_order;
	}

	public String getCo_loader() {
		return co_loader;
	}

	public void setCo_loader(String co_loader) {
		this.co_loader = co_loader;
	}

	public String getDepot_dt_id() {
		return depot_dt_id;
	}

	public void setDepot_dt_id(String depot_dt_id) {
		this.depot_dt_id = depot_dt_id;
	}

	public String getNo_batch() {
		return no_batch;
	}

	public void setNo_batch(String no_batch) {
		this.no_batch = no_batch;
	}

	public int getDeliv_num() {
		return deliv_num;
	}

	public void setDeliv_num(int deliv_num) {
		this.deliv_num = deliv_num;
	}

	public String getDeliv_remark() {
		return deliv_remark;
	}

	public void setDeliv_remark(String deliv_remark) {
		this.deliv_remark = deliv_remark;
	}

	public Date getDeliv_time() {
		return deliv_time;
	}

	public void setDeliv_time(Date deliv_time) {
		this.deliv_time = deliv_time;
	}

	public String getDeliv_user() {
		return deliv_user;
	}

	public void setDeliv_user(String deliv_user) {
		this.deliv_user = deliv_user;
	}

}
