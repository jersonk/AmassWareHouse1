package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.VA006Attachment;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.google.gson.reflect.TypeToken;

public class VA006Activity extends BaseActivity {

	@SuppressWarnings("rawtypes")
	private ArrayAdapter attaAdapter;  	  // 附件适配器
	private LayoutInflater attaInflater;
	private ListView listVa006;   	      // 附件
	private String strCdOrderPublic; 	  // 业务编号共通
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	protected BaseActivity _activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		strCdOrderPublic = (String) intent.getSerializableExtra("cdOrderPublic");
		setTitle(getString(R.string.title_activity_va006));    // "附件列表"
		super.onCreate(savedInstanceState);
		_activity = this;
		setContentView(R.layout.activity_va006);
		// Show the Up button in the action bar.
		attaInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listVa006 = (ListView) findViewById(R.id.listVa006);

		setupActionBar();

		listVa006.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,long id){
				// 获得选中项的HashMap对象
				final VA006Attachment map = (VA006Attachment) listVa006.getItemAtPosition(pos);
				final String dirName = Environment.getExternalStorageDirectory() 
						+ "/amass/Download";
				final File dir = new File(dirName);
				if (!dir.exists()) {
					dir.mkdir();
				}
				/* 取得扩展名 */
				String end = map.getNmFile().substring(map.getNmFile().lastIndexOf("."),
								map.getNmFile().length()).toLowerCase();
				final String fileNewName = map.getIdFile() + end;
				final File f = new File(dir, fileNewName);
				if (f.exists()) {
					Utils.openFile(_activity, dirName + "/" + fileNewName);
				} else {
					Map<String, Object> p = new HashMap<String, Object>();
					p.put("strFileId", map.getIdFile());
					NetworkHelper.getInstance().postJsonReturnBinary(_activity, "VA006_GetFileData", 
						p,new AmassHttpResponseHandler() {
							@Override
							protected void OnSuccess(byte[] response) {
								// TODO Auto-generated method stub
								super.OnSuccess(response);
								try {
									FileOutputStream fs = new FileOutputStream(f);
									fs.write(response);
									fs.close();
									Utils.openFile(_activity, dirName + "/" + fileNewName);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, false);
				}
			}
		});
		setAttachment();  //显示附件 
	}
	
	// 画面上设置附件
	@SuppressWarnings("rawtypes")
	public void setAttachment(){
		// 适配器
		attaAdapter = new ArrayAdapter(getApplicationContext(), 0) {
			public View getView(int position, View convertView, ViewGroup parent) {
				RelativeLayout layout = new RelativeLayout(getContext());
				VA006Attachment data = (VA006Attachment) getItem(position);
				try {
					// 设置背景颜色
					if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}
					attaInflater.inflate(R.layout.va006_list_view_item, layout,true);
					VA006Attachment item = (VA006Attachment) data;
					// 文件名
					TextView  t1 = (TextView) layout.findViewById(R.id.txtFileName);  
					t1.setText(item.getNmFile());
					// 上传者
					t1 = (TextView) layout.findViewById(R.id.txtUploadUser);
					t1.setText(item.getCdUpdateUserName());
					// 上传日期
					t1 = (TextView) layout.findViewById(R.id.txtUploadDate);
					t1.setText(df.format(item.getDtUpdateDate()));
					// 文件ID
					t1 = (TextView) layout.findViewById(R.id.txtFileId);
					t1.setText(item.getIdFile());
					t1.setVisibility(View.GONE);
					
				} catch (Exception e) {
					try {
						throw e;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				return layout;
			}
		};
		listVa006.setAdapter(attaAdapter);
		getAttachmentList();
	}
	// 得到附件列表
	public void getAttachmentList(){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("cdOrderPublic", strCdOrderPublic);    // 增值服务ID
		NetworkHelper.getInstance().postJsonData(_activity,"VA006_GetAttachmentList", paraMap, 
			new TypeToken<List<VA006Attachment>>() {}.getType(),
			new AmassHttpResponseHandler<List<VA006Attachment>>() {
					@SuppressWarnings("unchecked")
					@Override
					protected void OnSuccess(List<VA006Attachment> response) {
						super.OnSuccess(response);
						List<VA006Attachment> ms = (List<VA006Attachment>) response;
						attaAdapter.clear();
						attaAdapter.addAll(ms);
						attaAdapter.notifyDataSetChanged();
					}
				}, true);
	}
}
