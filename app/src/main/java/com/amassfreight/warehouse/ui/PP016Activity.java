package com.amassfreight.warehouse.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.ImageAdapter;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.warehouse.R;
import com.amassfreight.widget.GalleryAct;
import com.google.gson.reflect.TypeToken;

public class PP016Activity extends BaseActivity {

	private TextView content_boxNo;    // 集箱号
	private TextView content_orderCd;    // 进仓编号
	private TextView label_orderCd;     
	private ImageAdapter imageAdpter_PackPhoto;  // 装箱照片
	private GridView gridView_PackPhoto;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle(getString(R.string.PP007_pack_photo_title));   // 装箱照片一览
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pp016);
		content_boxNo = (TextView) findViewById(R.id.content_boxNo);
		content_orderCd = (TextView) findViewById(R.id.content_orderCd);
		label_orderCd = (TextView) findViewById(R.id.label_orderCd);
		setupActionBar();
		
		// 唛头照片
		gridView_PackPhoto = (GridView) findViewById(R.id.gridView_PackPhoto);
		imageAdpter_PackPhoto = new ImageAdapter(this, 0);
		gridView_PackPhoto.setAdapter(imageAdpter_PackPhoto);
		
		gridView_PackPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,long id) {
				MoreData data = (MoreData) parent.getItemAtPosition(pos);
				ImageData imageData = (ImageData) data.getData();
				if (imageData.getData() != null) {
					GalleryAct.start(_thisActivity, imageData.getUrl()); // 放大
				}
			}
		});
						
	    Intent intent=getIntent(); 
	    String boxNo = intent.getStringExtra("boxNo");
	    String orderCd = intent.getStringExtra("orderCd");
	    String orderCdPublic = intent.getStringExtra("orderCdPublic");
	    if(boxNo != null && !"".equals(boxNo)){
	    	InitData(boxNo,orderCd,orderCdPublic);
	    	content_boxNo.setText(boxNo);	    	
	    }
	}
	

    // 检索
	private void InitData(String boxNo,String orderCd,String orderCdPublic) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("noBox", boxNo);
		if(orderCd != null && !"".equals(orderCd)){
			paraMap.put("cdOrder", orderCd);
			content_orderCd.setText(orderCdPublic);
		}else{
			paraMap.put("cdOrder", "");
			content_orderCd.setVisibility(View.GONE);
			label_orderCd.setVisibility(View.GONE);
		}
        if(orderCdPublic != null && !"".equals(orderCdPublic)){
        	paraMap.put("cdOrderPublic", orderCdPublic);
		}else{
			paraMap.put("cdOrderPublic", "");
		}
		NetworkHelper.getInstance().postJsonData(_thisActivity,"getPackPhotoList", paraMap, 
				new TypeToken<List<FileManageData>>() {}.getType(),
			new AmassHttpResponseHandler<List<FileManageData>>() {
					@Override
					protected void OnSuccess(List<FileManageData> response) {
						super.OnSuccess(response);
						List<FileManageData> list = (List<FileManageData>) response;
						// 循环加载照片
						for (FileManageData file : list) {
							ImageData data = new ImageData();
							data.setImageId(file.getFileId());
							data.setImageDesc(file.getFileName());
							MoreData d = new MoreData(data);
							//imageAdpter_PackPhoto.insert(d, 0);
							imageAdpter_PackPhoto.add(d);
						}
					}
				}, true);
	}
}
