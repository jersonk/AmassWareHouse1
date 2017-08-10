package com.amassfreight.warehouse.ui;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.UnPackingList;
import com.amassfreight.warehouse.R;
import com.google.gson.reflect.TypeToken;

@SuppressLint("SimpleDateFormat")
public class PP017Activity extends BaseActivity {
	private static final String METHOD_URL = "getUnpackingListByOrder";

	private PP017Adapter adapter;
	private ListView unPackingList;
	private TextView boxNoText;
	private String boxNo;
	private List<UnPackingList> datalist;  
	private Button back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pp017);
		setupActionBar();
		unPackingList = (ListView) findViewById(R.id.unPackingList);
		boxNoText = (TextView) findViewById(R.id.boxNo);
		back = (Button) findViewById(R.id.back);
		
		Intent intent=getIntent(); 
		boxNo = intent.getStringExtra("boxNo");
		if(boxNo != null && !boxNo.equals("")){
			boxNoText.setText(boxNo);
			Map<String,Object> requestData = new HashMap<String,Object>();
			requestData.put("boxNo", boxNo);			
			Type type = new TypeToken<List<UnPackingList>>(){}.getType();
			NetworkHelper.getInstance().postJsonData(this, METHOD_URL, requestData,type , httpHandler, true);
		}

	}

	// AmassHttpResponseHandler
	private AmassHttpResponseHandler<List<UnPackingList>> httpHandler = new AmassHttpResponseHandler<List<UnPackingList>>() {
		protected void OnSuccess(List<UnPackingList> response) {
			super.OnSuccess(response);
			try{
				List<UnPackingList> responseList= (List<UnPackingList>) response;
				if(responseList != null && responseList.size() >0){
					datalist = responseList;
					adapter = new PP017Adapter(PP017Activity.this);
					unPackingList.setAdapter(adapter);
					adapter.notifyDataSetChanged(); 
				}
			}
			catch (Exception ex){
				ex.printStackTrace();
			}

		};

	};
		
	
	//保存按钮
	public void back_OnClick(View arg0) {		
        this.finish();
	}

	
	//ViewHolder静态类  
    static class ViewHolder  
    {  
    	public TextView orderCdPublic; 
    	public TextView unPackingFlg; 
        public TextView pileCardCd;
        public TextView paidinNum;
        public TextView boxNo_text;
        
    }  
      
    @SuppressLint("ResourceAsColor")
	public class PP017Adapter extends BaseAdapter  
    {      
        private LayoutInflater mInflater = null;  
        private PP017Adapter(Context context)  
        {  
            //根据context上下文加载布局，这里的是Activity本身，即this  
            this.mInflater = LayoutInflater.from(context);  
            
        }  
  
        @Override  
        public int getCount() {  
            //How many items are in the data set represented by this Adapter.  
            //在此适配器中所代表的数据集中的条目数  
            return datalist.size();  
        }  
  
        @Override  
        public Object getItem(int position) {  
            // Get the data item associated with the specified position in the data set.  
            //获取数据集中与指定索引对应的数据项  
            return position;  
        }  
  
        @Override  
        public long getItemId(int position) {  
            //Get the row id associated with the specified position in the list.  
            //获取在列表中与指定索引对应的行id  
            return position;  
        }  
          
        //Get a View that displays the data at the specified position in the data set.  
        //获取一个在数据集中指定索引的视图来显示数据  
        @SuppressLint("ResourceAsColor")
		@Override  
        public View getView(int position, View convertView, ViewGroup parent) {  
            ViewHolder holder = null;  
            //如果缓存convertView为空，则需要创建View  
            if(convertView == null)  
            {  
                holder = new ViewHolder();  
                //根据自定义的Item布局加载布局  
                convertView = mInflater.inflate(R.layout.activity_pp017_listview_item, null);   
                holder.pileCardCd = (TextView) convertView.findViewById(R.id.pileCardCd_text);
                holder.orderCdPublic = (TextView) convertView.findViewById(R.id.orderCd_public_text);
                holder.unPackingFlg = (TextView) convertView.findViewById(R.id.unPacking_flg_text);
                holder.paidinNum = (TextView) convertView.findViewById(R.id.paidinNum_text);
                holder.boxNo_text = (TextView) convertView.findViewById(R.id.boxNo_text);

                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag  
                convertView.setTag(holder);  
            }else  
            {  
                holder = (ViewHolder)convertView.getTag();  
            }  
            if (position % 2 == 0) {
            	convertView.setBackgroundResource(R.color.listview_back_odd);
			} else {
				convertView.setBackgroundResource(R.color.listview_back_uneven);
			}
            holder.pileCardCd.setText((String)datalist.get(position).getPileCardCd());  
            holder.orderCdPublic.setText((String)datalist.get(position).getOrderCdPublic());
            holder.paidinNum.setText(String.valueOf(datalist.get(position).getPaidinNum()));
            holder.boxNo_text.setText((String)datalist.get(position).getBoxNo());
            
            //设置掏箱状态背景色
            if(datalist.get(position).getUnPackingFlg() != 1){
            	holder.unPackingFlg.setText(R.string.PP014_unPacking_flg_undo);
            	holder.unPackingFlg.setBackgroundResource(0);
            }else{
            	holder.unPackingFlg.setText(R.string.PP014_unPacking_flg_done);
            	holder.unPackingFlg.setBackgroundResource(R.color.gay);
            }
            
            return convertView;  
        }  
          
    }  
	
	public PP017Adapter getAdapter() {
		return adapter;
	}

	public void setAdapter(PP017Adapter adapter) {
		this.adapter = adapter;
	}

	public AmassHttpResponseHandler<List<UnPackingList>> getHttpHandler() {
		return httpHandler;
	}

	public void setHttpHandler(AmassHttpResponseHandler<List<UnPackingList>> httpHandler) {
		this.httpHandler = httpHandler;
	}

}
