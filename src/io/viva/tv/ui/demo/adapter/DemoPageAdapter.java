package io.viva.tv.ui.demo.adapter;

import io.viva.tv.app.widget.FocusedRelativeLayout;
import io.viva.tv.app.widget.PagerAdapter;
import io.viva.tv.app.widget.ReflectImageView;
import io.viva.tv.app.widget.ViewPager;
import io.viva.tv.ui.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class DemoPageAdapter extends PagerAdapter {
	private Context mContext;
	public ArrayList<ViewGroup> mPageViews; 
	public List<Map<String, Object>> mList;
	private ViewPager mHomeViewPager;
	public FocusedRelativeLayout layout;
	private float mScaleValue = 1.1f;
	private List<String> fistListName = null;
	private List<String> lastListName = null;

	public DemoPageAdapter(Context ctxt, ViewPager homeViewPager) {
		this.mContext = ctxt;
		mList = new ArrayList<Map<String, Object>>();
		this.mPageViews = new ArrayList<ViewGroup>();
		this.mHomeViewPager = homeViewPager;
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
		ViewGroup viewGroup = mPageViews.get(arg1);
		 fistListName = new ArrayList<String>();
		 lastListName = new ArrayList<String>();
		 for (int i = 0; i < mList.size(); i++) {
			 Map<String, Object> info = mList.get(i);
				if (i < 7) {
					fistListName.add(info.get("text").toString());
				} else {
					lastListName.add(info.get("text").toString());
				}
			}
		 
		if (arg1 == 0) {
			layout = (FocusedRelativeLayout) mPageViews.get(arg1).findViewById(R.id.demo_page1_layout);		
			layout.setItemScaleValue(mScaleValue, mScaleValue);
			layout.setFocusResId(R.drawable.tui_bg_focus);
			layout.setFocusShadowResId(R.drawable.tui_grid_focus);
			layout.setOnClickListener(listener);

			ReflectImageView demoImage0 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image1);
			demoImage0.setImageResource(R.drawable.image2, 80);
			demoImage0.setText(fistListName.get(0));


			ReflectImageView demoImage1 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image2);
			demoImage1.setTag(1);
			demoImage1.setText(fistListName.get(1));
			demoImage1.setImageResource(R.drawable.image2);


			ReflectImageView demoImage2 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image3);
			demoImage2.setText(fistListName.get(2));
			demoImage2.setImageResource(R.drawable.image2, 80);

			ReflectImageView demoImage3 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image4);
			demoImage3.setText(fistListName.get(3));
			demoImage3.setImageResource(R.drawable.image2);


			ReflectImageView demoImage4 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image5);
			demoImage4.setText(fistListName.get(4));
			demoImage4.setImageResource(R.drawable.image2, 80);

			ReflectImageView demoImage5 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image6);
			demoImage5.setText(fistListName.get(5));
			demoImage5.setImageResource(R.drawable.image2);
			
			ReflectImageView demoImage6 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image7);
			demoImage6.setText(fistListName.get(6));
			demoImage6.setImageResource(R.drawable.image2, 80);		
			
		} else {
			layout = (FocusedRelativeLayout) mPageViews.get(arg1).findViewById(R.id.demo_page2_layout);			
			layout.setItemScaleValue(mScaleValue, mScaleValue);
			layout.setFocusResId(R.drawable.tui_grid_focus);
			layout.setFocusShadowResId(R.drawable.tui_grid_focus);
			layout.setOnClickListener(listener);
			
			ReflectImageView demoImage0 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image1);
			demoImage0.setImageResource(R.drawable.image2);
			demoImage0.setText(lastListName.get(0));

			ReflectImageView demoImage1 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image2);
			demoImage1.setText(lastListName.get(1));
			demoImage1.setImageResource(R.drawable.image2, 80);

			ReflectImageView demoImage2 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image3);
			demoImage2.setText(lastListName.get(2));
			demoImage2.setImageResource(R.drawable.image2);

			ReflectImageView demoImage3 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image4);
			demoImage3.setText(lastListName.get(3));
			demoImage3.setImageResource(R.drawable.image2, 80);

			ReflectImageView demoImage4 = (ReflectImageView) viewGroup.findViewById(R.id.demo_image5);
			demoImage4.setText(lastListName.get(4));
			demoImage4.setImageResource(R.drawable.image2, 80);
		}

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

	public OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FocusedRelativeLayout layout = (FocusedRelativeLayout)v;			
			Log.i("aabb", layout.mIndex+"======onClick=========="+v);
		}
	};
}
