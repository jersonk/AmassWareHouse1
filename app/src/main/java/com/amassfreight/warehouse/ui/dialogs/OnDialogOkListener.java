package com.amassfreight.warehouse.ui.dialogs;

import android.content.Intent;

public interface OnDialogOkListener {
	public final String OK = "OKDATA";
	void onDialogResult(int requestCode, int resultCode, Intent data);
}
