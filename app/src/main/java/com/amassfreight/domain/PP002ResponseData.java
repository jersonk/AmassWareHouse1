package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class PP002ResponseData implements Serializable {

	public PP002ResponseData() {
	}

	private List<SelectDict> stsPacking;

	private List<SelectDict> mLine;

	public List<SelectDict> getStsPacking() {
		return stsPacking;
	}

	public void setStsPacking(List<SelectDict> stsPacking) {
		this.stsPacking = stsPacking;
	}

	public List<SelectDict> getmLine() {
		return mLine;
	}

	public void setmLine(List<SelectDict> mLine) {
		this.mLine = mLine;
	}
}
