package com.amassfreight.domain;

import java.util.Date;
import java.util.List;

public class RC001Deliver {
	private String deliv_id; // 放货编号
	private Date dt_forward;// 放货日期
	private String forward_user;//
	private String no_car_lice;
	private String tel_no;
	private String type_deliv;
	private String flg_deliv;
	private String num;     //放货件数
	private String flag;    //司机自分货标志
	
	// 分批次的数据
	private List<RC001Deliver_batch> rl;
	//增值服务一览
	private List<DepotVas> depotVasList;

	private String forward_remark;

	public RC001Deliver() {
		// TODO Auto-generated constructor stub
	}

	public String getDeliv_id() {
		return deliv_id;
	}

	public void setDeliv_id(String deliv_id) {
		this.deliv_id = deliv_id;
	}

	public Date getDt_forward() {
		return dt_forward;
	}

	public void setDt_forward(Date dt_forward) {
		this.dt_forward = dt_forward;
	}

	public String getForward_user() {
		return forward_user;
	}

	public void setForward_user(String forward_user) {
		this.forward_user = forward_user;
	}

	public String getNo_car_lice() {
		return no_car_lice;
	}

	public void setNo_car_lice(String no_car_lice) {
		this.no_car_lice = no_car_lice;
	}

	public String getTel_no() {
		return tel_no;
	}

	public void setTel_no(String tel_no) {
		this.tel_no = tel_no;
	}

	public String getType_deliv() {
		return type_deliv;
	}

	public void setType_deliv(String type_deliv) {
		this.type_deliv = type_deliv;
	}

	public String getForward_remark() {
		return forward_remark;
	}

	public void setForward_remark(String forward_remark) {
		this.forward_remark = forward_remark;
	}

	public List<RC001Deliver_batch> getRl() {
		return rl;
	}

	public void setRl(List<RC001Deliver_batch> rl) {
		this.rl = rl;
	}

	public List<DepotVas> getDepotVasList() {
		return depotVasList;
	}

	public void setDepotVasList(List<DepotVas> depotVasList) {
		this.depotVasList = depotVasList;
	}

	public String getFlg_deliv() {
		return flg_deliv;
	}

	public void setFlg_deliv(String flg_deliv) {
		this.flg_deliv = flg_deliv;
	}
	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
