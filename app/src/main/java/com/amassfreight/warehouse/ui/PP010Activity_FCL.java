package com.amassfreight.warehouse.ui;

import android.os.Bundle;

public class PP010Activity_FCL extends PP010Activity {

	@Override
	protected Boolean isFclOrScl() {
		return true;
	}

	@Override
	protected Boolean isEdit() {
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("IsEdit")) {
			return extras.getBoolean("IsEdit");
		} else {
			return true;
		}
	}

	@Override
	protected String noBox() {
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("noBox")) {
			return extras.getString("noBox");
		} else {
			return "";
		}

	}
}
