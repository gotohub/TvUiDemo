package io.viva.tv.ui.demo.adapter;

import io.viva.tv.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import io.viva.tv.app.widget.adapter.PagerAdapter;
import io.viva.tv.app.widget.FocusedGridView;
import io.viva.tv.app.widget.ViewPager;
import io.viva.tv.ui.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class DemoGridPageAdapter extends PagerAdapter {
	private Context mContext;
	public ArrayList<ViewGroup> mPageViews;
	public List<Map<String, Object>> mList;
	private int counts_per_page = 8;
	public DemoGridAdapter mAppAdapter;

	public DemoGridPageAdapter(Context ctxt) {
		this.mContext = ctxt;
		mList = new ArrayList<Map<String, Object>>();
		this.mPageViews = new ArrayList<ViewGroup>();
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		if (arg1 < mPageViews.size()) {
			((ViewPager) arg0).removeView(mPageViews.get(arg1));
		}
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		return mPageViews.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		int startIndex = arg1 * counts_per_page;
		int endIndex = startIndex + counts_per_page;

		ViewGroup viewGroup = mPageViews.get(arg1);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while ((startIndex < mList.size()) && (startIndex < endIndex)) {
			list.add(mList.get(startIndex));
			startIndex++;
		}

		FocusedGridView mGridView = (FocusedGridView) viewGroup.findViewById(R.id.demo_grid);
		mAppAdapter = new DemoGridAdapter(mContext, list);
		mGridView.setAdapter(mAppAdapter);		
		mGridView.setOnItemClickListener(listener);
		mGridView.setOnItemSelectedListener(mOnItemSelectedListener);
		((ViewPager) arg0).addView(viewGroup);

		return viewGroup;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

	private View mCurrentView;

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		mCurrentView = (View) object;
	}

	public View getPrimaryItem() {
		return mCurrentView;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private FocusItemSelectedListener mOnItemSelectedListener = new FocusItemSelectedListener() {

		@Override
		public void onItemSelected(View paramView1, int paramInt, boolean paramBoolean, View paramView2) {
			Log.d("aabb", "====Selected==position=========" + paramInt);
		}
	};
	public OnItemClickListener listener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d("aabb", "====onItemClick====position=====" + position);
		}
	};

}
