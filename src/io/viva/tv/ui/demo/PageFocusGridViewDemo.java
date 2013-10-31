package io.viva.tv.ui.demo;

import io.viva.tv.app.widget.FocusedGridView;
import io.viva.tv.app.widget.ViewPager;
import io.viva.tv.app.widget.ViewPager.OnPageChangeListener;
import io.viva.tv.ui.demo.adapter.DemoGridPageAdapter;
import io.viva.tv.ui.demo.view.ViewScroller;
import io.viva.tv.ui.demo.view.ViewScroller.ScrollListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageFocusGridViewDemo extends Activity implements ScrollListener {

	private String TAG = "uidemo";
	private ViewPager mViewPager;
	private ImageView[] mImageViews;
	private ViewScroller mScroller;
	private ViewGroup mGroup;
	private DemoGridPageAdapter mAdapter;
	private int mIndex = 0;
	private int mLastIndex = 0;

	private String[] mAppName = { "电影", "淘宝团购", "电视回看", "虾米音乐", "游戏", "水电煤", "设置", "淘宝", "旺信", "U盘", "天猫", "我的收藏" };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_viewpage);

		mGroup = (ViewGroup) findViewById(R.id.viewGroup);
		mViewPager = (ViewPager) findViewById(R.id.guidePages);
		mAdapter = new DemoGridPageAdapter(PageFocusGridViewDemo.this);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mAppName.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", mAppName[i]);
			list.add(map);
		}

		mViewPager.setAdapter(mAdapter);
		mAdapter.mList = list;
		setAppList(list);
		mViewPager.setOnPageChangeListener(new GuidePageChangeListener());
		try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			mScroller = new ViewScroller(mViewPager.getContext(), new AccelerateInterpolator());
			mScroller.setmDuration(500);
			mScroller.setScrollListener(this);
			mField.set(mViewPager, mScroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAppList(List<Map<String, Object>> list_date) {
		mGroup.removeAllViews();

		mAdapter.mPageViews = new ArrayList<ViewGroup>();
		int PageCount = (int) Math.ceil(list_date.size() / 8.0f);
		Log.d(TAG, "======PageCount===" + 2);
		for (int i = 0; i < PageCount; i++) {
			ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.demo_gridview, null);
			FocusedGridView gridView = (FocusedGridView) viewGroup.findViewById(R.id.demo_grid);
			gridView.setFocusResId(R.drawable.tui_bg_focus);
			gridView.setFocusShadowResId(R.drawable.tui_grid_focus);
			gridView.setFocusViewId(R.id.demo_icon);
			gridView.setFrameRate(6);
			gridView.setSelection(0);
			gridView.setItemScaleValue(1.1f, 1.1f);
			mAdapter.mPageViews.add(viewGroup);
		}
		mImageViews = new ImageView[mAdapter.mPageViews.size()];

		for (int i = 0; i < mAdapter.mPageViews.size(); i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(20, 20));
			imageView.setPadding(20, 0, 20, 0);
			imageView.setTag(i);
			mImageViews[i] = imageView;
			mGroup.addView(mImageViews[i]);
		}

	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			Log.d(TAG, "==========onPageSelected===" + arg0);
			mLastIndex = mIndex;
			mIndex = arg0;
			startScroll();

			for (int i = 0; i < mImageViews.length; i++) {
				mImageViews[arg0].setBackgroundResource(R.drawable.page_indicator_focused);
				if (arg0 != i) {
					mImageViews[i].setBackgroundResource(R.drawable.page_indicator);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void startScroll() {
		Log.w(TAG, "=============startScroll=== mIndex = " + mIndex);
		FocusedGridView gridview = (FocusedGridView) ((LinearLayout) mAdapter.mPageViews.get(mIndex)).getChildAt(0);
		FocusedGridView LastGridview = (FocusedGridView)((LinearLayout) mAdapter.mPageViews.get(mLastIndex)).getChildAt(0);
		

	}

	@Override
	public void scrolling() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endScroll() {
		Log.w(TAG, "=============endScroll=== mIndex = " + mIndex);
		FocusedGridView gridview = (FocusedGridView) ((LinearLayout) mAdapter.mPageViews.get(mIndex)).getChildAt(0);
		FocusedGridView LastGridview = (FocusedGridView) ((LinearLayout) mAdapter.mPageViews.get(mLastIndex)).getChildAt(0);

	}

}
