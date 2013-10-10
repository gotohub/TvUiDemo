package io.viva.tv.ui.demo.adapter;

import io.viva.tv.ui.demo.R;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DemoGridAdapter extends BaseAdapter {
	public List<Map<String, Object>> mList;
	private Context mContext;

	public DemoGridAdapter(Context context, List<Map<String, Object>> mList) {
		mContext = context;
		this.mList = mList;
	}

	public int getCount() {
		return mList.size();
	}

	public Map<String, Object> getItem(int position) {
		return mList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {		
		Map<String, Object> info = mList.get(position);
		ImageView image = null;
		TextView name = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.demo_grid_item, null);
			image = (ImageView) convertView.findViewById(R.id.demo_icon);
			name = (TextView) convertView.findViewById(R.id.demo_txt);
		} else {
			image = (ImageView) convertView.findViewById(R.id.demo_icon);
			name = (TextView) convertView.findViewById(R.id.demo_txt);
		}	
		image.setImageResource(R.drawable.image2);
		name.setText(info.get("text").toString());

		return convertView;
	}

}
