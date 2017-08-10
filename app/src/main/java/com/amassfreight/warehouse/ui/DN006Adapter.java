package com.amassfreight.warehouse.ui;

import java.util.List;

import com.amassfreight.domain.DN006ResponseData;
import com.amassfreight.warehouse.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class DN006Adapter extends ArrayAdapter implements View.OnClickListener {

	private LayoutInflater inflater;

	/**
	 * 屏幕宽度,由于我们用的是HorizontalScrollView,所以按钮选项应该在屏幕外
	 */
	private int mScreentWidth;
	private Button btnSave;
	private Button btnClear;

	public DN006Adapter(Context context, List<DN006ResponseData> datas,
			int screenWidth, Button btnSave, Button btnClear) {
		super(context, 0, datas);
		inflater = LayoutInflater.from(context);
		mScreentWidth = screenWidth;
		this.btnSave = btnSave;
		this.btnClear = btnClear;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		DN006ResponseData model = (DN006ResponseData) getItem(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.dn006_list_view_item, null);

			holder.hSView = (HorizontalScrollView) convertView
					.findViewById(R.id.hsv);
			holder.action = convertView.findViewById(R.id.ll_action);

			holder.txtInStore = (TextView) convertView
					.findViewById(R.id.txtInStore);
			holder.txtTH = (TextView) convertView.findViewById(R.id.txtTH);
			holder.txtNo = (TextView) convertView.findViewById(R.id.txtNo);
			holder.txtFootNo = (TextView) convertView
					.findViewById(R.id.txtFootNo);
			holder.txtOldStore = (TextView) convertView
					.findViewById(R.id.txtOldStore);
			holder.txtStore = (TextView) convertView
					.findViewById(R.id.txtStore);

			holder.btnDel = (Button) convertView.findViewById(R.id.btn_Del);
			holder.btnDel.setTag(position);

			// 设置内容view的大小为屏幕宽度,这样按钮就正好被挤出屏幕外
			holder.content = convertView.findViewById(R.id.ll_content);
			LayoutParams lp = holder.content.getLayoutParams();
			lp.width = mScreentWidth;

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 设置监听事件
		convertView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:

					// 获得ViewHolder
					ViewHolder viewHolder = (ViewHolder) v.getTag();

					// 获得HorizontalScrollView滑动的水平方向值.
					int scrollX = viewHolder.hSView.getScrollX();

					// 获得操作区域的长度
					int actionW = viewHolder.action.getWidth();

					// 注意使用smoothScrollTo,这样效果看起来比较圆滑,不生硬
					// 如果水平方向的移动值<操作区域的长度的一半,就复原
					if (scrollX < actionW / 2) {
						viewHolder.hSView.smoothScrollTo(0, 0);
					} else// 否则的话显示操作区域
					{
						viewHolder.hSView.smoothScrollTo(actionW, 0);
					}
					return true;
				}
				return false;
			}
		});

		// 这里防止删除一条item后,ListView处于操作状态,直接还原
		if (holder.hSView.getScrollX() != 0) {
			holder.hSView.scrollTo(0, 0);
		}

		holder.txtInStore.setText(model.getDepotID());
		holder.txtTH.setText(model.getCoLoader());
		holder.txtNo.setText(String.valueOf(model.getNoBatch()));
		holder.txtFootNo.setText(model.getNoPilecard());
		holder.txtOldStore.setText(model.getDefaultPos());
		holder.txtStore.setText(model.getPosLocation());

		// 设置监听事件
		holder.btnDel.setOnClickListener(this);

		int[] colors = { R.color.listview_back_odd,
				R.color.listview_back_uneven };
		convertView.setBackgroundResource(colors[position % 2]);

		return convertView;
	}

	public final class ViewHolder {
		public TextView txtInStore;
		public TextView txtTH;
		public TextView txtNo;
		public TextView txtFootNo;
		public TextView txtOldStore;
		public TextView txtStore;

		public HorizontalScrollView hSView;
		public View action;
		public View content;
		public TextView btnDel;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		this.remove(this.getItem(position));
		notifyDataSetChanged();
		if (this.getCount() == 0) {
			btnSave.setVisibility(View.GONE);
			btnClear.setVisibility(View.GONE);
		}
	}
}
