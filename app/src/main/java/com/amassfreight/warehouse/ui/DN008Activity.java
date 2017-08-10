package com.amassfreight.warehouse.ui;

import com.amassfreight.warehouse.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class DN008Activity extends Activity {

	private EditText editJJN; // 合计：体积
	private EditText editFQN; // 合计：体积
	private EditText editCCZYN; // 合计：体积
	private EditText editCCCZN; // 合计：体积
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dn008);
		
		// 初期加载
		initializeView();
	}
	
	private void initializeView() {
		editJJN = (EditText) findViewById(R.id.editJJN); // 应急处理
		editFQN = (EditText) findViewById(R.id.editFQN); // 消除措施
		editCCZYN = (EditText) findViewById(R.id.editCCZYN); // 安全操作的注意事项
		editCCCZN = (EditText) findViewById(R.id.editCCCZN); // 安全储存的条件
		
		editJJN.setEnabled(false);
		editJJN.setCursorVisible(false);
		editJJN.setFocusable(false);         
		editJJN.setFocusableInTouchMode(false);
		//editJJN.setTextColor(Color.RED);
		
		editFQN.setEnabled(false);
		editFQN.setCursorVisible(false);
		editFQN.setFocusable(false);         
		editFQN.setFocusableInTouchMode(false);
		//editFQN.setTextColor(Color.RED);
		
		editCCZYN.setEnabled(false);
		editCCZYN.setCursorVisible(false);
		editCCZYN.setFocusable(false);         
		editCCZYN.setFocusableInTouchMode(false);
		//editCCZYN.setTextColor(Color.RED);
		
		editCCCZN.setEnabled(false);
		editCCCZN.setCursorVisible(false);
		editCCCZN.setFocusable(false);         
		editCCCZN.setFocusableInTouchMode(false);
		//editCCCZN.setTextColor(Color.RED);
		
		/* 获取Intent中的Bundle对象 */
		Bundle bundle = this.getIntent().getExtras();
		
		editJJN.setText(bundle.getString("jjn"));
		editFQN.setText(bundle.getString("fqn")); 
		editCCZYN.setText(bundle.getString("cczyn")); 
		editCCCZN.setText(bundle.getString("ccczn")); 
	}
}
