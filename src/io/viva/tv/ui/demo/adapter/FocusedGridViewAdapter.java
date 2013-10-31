package io.viva.tv.ui.demo.adapter;

import io.viva.tv.app.widget.MediaCoverFlow;
import io.viva.tv.ui.demo.R;
import io.viva.tv.ui.demo.view.MyLinearLayout;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class FocusedGridViewAdapter extends BaseAdapter {

	public static final int GRID_COUNT = 100;
	public static final int HEADERVIEW_ITEM_COUNT = 25;
	private LayoutInflater mInflater;
	int imageWidth;
	int imageHeight;
	private int mNumColumns = 6;
	MyLinearLayout mHeadView;
	MediaCoverFlow mCoverFlow;
	Context mContext;
	private SpinnerAdapter coverImageAdapter;
	private boolean hasCoverFlow = false;
	private int mScreenWidth;

	public boolean hasCoverFlow() {
		return hasCoverFlow;
	}

	public void setHasCoverFlow(boolean hasCoverFlow) {
		this.hasCoverFlow = hasCoverFlow;
	}

	public FocusedGridViewAdapter(Context context, float itemWidthDip, float itemHeightDip) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		WindowManager mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = mWm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		mScreenWidth = dm.widthPixels;
	}

	@Override
	public int getCount() {
		return this.hasCoverFlow() ? GRID_COUNT + mNumColumns : GRID_COUNT;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private void setupCoverFlow(MediaCoverFlow coverFlow) {
		coverImageAdapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ImageView imageView;
				if (convertView == null) {
					imageView = new ImageView(mContext);
					imageView.setLayoutParams(new MediaCoverFlow.LayoutParams(160, 200));
					imageView.setFocusable(false);
					imageView.setScaleType(ScaleType.FIT_XY);
					imageView.setImageResource(R.drawable.tv_film_cover_middle_normal);
				} else {
					imageView = (ImageView) convertView;
				}
				// 开启硬件加速时，需要加入以下code，才能使抗锯齿生效
				/*
				 * Matrix localMatrix = new Matrix(); Bitmap bitmap =
				 * drawable.getBitmap();
				 * 
				 * int boundWidth = imageView.getWidth();//getWidth() -
				 * getPaddingLeft() - getPaddingRight(); int boundHeight =
				 * imageView.getHeight();//getHeight() - getPaddingTop() -
				 * getPaddingBottom();
				 * 
				 * float sx = 1.0f*boundWidth/bitmap.getWidth(); float sy =
				 * 1.0f*boundHeight/bitmap.getHeight();
				 * 
				 * localMatrix.setScale(sx, sy);
				 * 
				 * Paint paint = drawable.getPaint(); BitmapShader shader = new
				 * BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
				 * shader.setLocalMatrix(localMatrix); paint.setShader(shader);
				 */
				return imageView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				// 如果要绘制标题文字，需要在该接口中返回对应的String.
				return "item-" + position;
			}

			@Override
			public int getCount() {
				return HEADERVIEW_ITEM_COUNT;
			}
		};

		mCoverFlow.setGravity(Gravity.CENTER_VERTICAL);
		// 设置切换时的动画时间
		mCoverFlow.setAnimationDuration(500);
		// 设置item之间的间隔，水平方向的
		mCoverFlow.setSpacing(40);
		// 设置标题文字和item之间的间隔
		mCoverFlow.setCoverFlowTextSpacing(15);
		// 设置选择框的上下左右padding，可以为负值
		mCoverFlow.setSelectorPadding(10, 10, 10, 10);
		mCoverFlow.setAdapter(coverImageAdapter);
		mCoverFlow.setSelection(5);
		mCoverFlow.setDrawShadowImage(false);
	}
	
	/**
	 * 这里定义三种类型的view: 第一行第一列，第一行其他列，其他。
	 */
	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return 0;
		} else if (position < mNumColumns) {
			return 1;
		} else {
			return 2;
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("aabb", "getView -- position = " + position + "," + hasCoverFlow());

		if (hasCoverFlow()) {
			if (position == 0) {
				if (mHeadView == null) {
					mHeadView = (MyLinearLayout) mInflater.inflate(R.layout.headerview_coverflow, null);
					mHeadView.setFocusable(false);
					mHeadView.setLayoutParams(new AbsListView.LayoutParams(mScreenWidth - 60, 270));
					mCoverFlow = (MediaCoverFlow) mHeadView.findViewById(R.id.grid_item);
					mCoverFlow.setFocusable(false);
					mCoverFlow.setSelected(true);
					setupCoverFlow(mCoverFlow);
				}
				return mHeadView;
			}
			if (position < mNumColumns) {
				if (convertView == null) {
					convertView = new MyLinearLayout(mContext);
					convertView.setLayoutParams(new AbsListView.LayoutParams(0, 270));
					convertView.setFocusable(false);
				}
				return convertView;
			}
		}
		
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.item_gridview, null);
		}
		TextView title = (TextView) convertView.findViewById(R.id.text_yingshi_grid);
		title.setText("griditem-" + position);
		
		return convertView;
	}

	public void setHeaderViewFocus(boolean isFocus) {
		if (mCoverFlow != null) {
			mCoverFlow.setForceGainFocus(isFocus);
		}
	}

}
