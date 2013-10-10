package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

public class FocusedListView extends ListView implements FocusedBasePositionManager.PositionInterface {
	private static final String TAG = "FocusedListView";
	private static final int SCROLLING_DURATION = 150;
	private static final int SCROLLING_DELAY = 50;
	private long KEY_INTERVEL = 20L;
	private long mKeyTime = 0L;
	private AbsListView.OnScrollListener mOuterScrollListener;
	private AdapterView.OnItemClickListener mOnItemClickListener = null;
	private FocusItemSelectedListener mOnItemSelectedListener = null;
	private FocusedListViewPositionManager mPositionManager;
	private int mFocusViewId = -1;
	private int mCurrentPosition = -1;
	private int mLastPosition = -1;

	private Object lock = new Object();
	private boolean mOutsieScroll = false;
	private boolean isScrolling = false;
	private boolean mNeedScroll = false;

	private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (FocusedListView.this.mOuterScrollListener != null) {
				FocusedListView.this.mOuterScrollListener.onScrollStateChanged(view, scrollState);
			}
			Log.i("FocusedListView", "onScrollStateChanged scrolling");
			switch (scrollState) {
			case 1:
			case 2:
				FocusedListView.this.setScrolling(true);
				break;
			case 0:
				Log.i("FocusedListView", "onScrollStateChanged idle mNeedScroll = " + FocusedListView.this.mNeedScroll);
				Log.d("lingdang", "mCurrentPosition=" + FocusedListView.this.mCurrentPosition);
				if (FocusedListView.this.mNeedScroll) {
					FocusedListView.this.setSelection(FocusedListView.this.mCurrentPosition);
				}
				FocusedListView.this.setScrolling(false);
				break;
			}
		}

		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (FocusedListView.this.mOuterScrollListener != null)
				FocusedListView.this.mOuterScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	};
	private static final int DRAW_FOCUS = 1;
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Log.i("FocusedListView", "Handler handleMessage");
				if (FocusedListView.this.getSelectedView() != null) {
					Log.d("lingdang", "***11111111***");
					FocusedListView.this.performItemSelect(FocusedListView.this.getSelectedView(), FocusedListView.this.mCurrentPosition, true);
				}

				FocusedListView.this.mPositionManager.setSelectedView(FocusedListView.this.getSelectedView());
				FocusedListView.this.mPositionManager.setTransAnimation(false);
				FocusedListView.this.mPositionManager.setNeedDraw(true);
				FocusedListView.this.mPositionManager.setContrantNotDraw(false);
				FocusedListView.this.mPositionManager.setScaleCurrentView(true);
				FocusedListView.this.mPositionManager.setScaleLastView(true);
				FocusedListView.this.mPositionManager.setState(1);
				if (!FocusedListView.this.isScrolling()) {
					Log.d("lingdang", "***222222***");
					FocusedListView.this.invalidate();
				}
				break;
			}
		}
	};

	private boolean isSetSelectionAfterLayoutChildren = false;
	private onKeyDownListener onKeyDownListener;
	int mScrollDistance = 0;

	public FocusedListView(Context contxt) {
		super(contxt);
		init(contxt);
	}

	public FocusedListView(Context contxt, AttributeSet attrs) {
		super(contxt, attrs);
		init(contxt);
	}

	public FocusedListView(Context contxt, AttributeSet attrs, int defStyle) {
		super(contxt, attrs, defStyle);
		init(contxt);
	}

	public void setManualPadding(int left, int top, int right, int bottom) {
		this.mPositionManager.setManualPadding(left, top, right, bottom);
	}

	public void setFrameRate(int rate) {
		this.mPositionManager.setFrameRate(rate);
	}

	public void setFocusResId(int focusResId) {
		this.mPositionManager.setFocusResId(focusResId);
	}

	public void setFocusShadowResId(int focusResId) {
		this.mPositionManager.setFocusShadowResId(focusResId);
	}

	public void setItemScaleValue(float scaleXValue, float scaleYValue) {
		this.mPositionManager.setItemScaleValue(scaleXValue, scaleYValue);
	}

	public void setFocusMode(int mode) {
		this.mPositionManager.setFocusMode(mode);
	}

	public void setFocusViewId(int id) {
		this.mFocusViewId = id;
	}

	public void setOnScrollListener(AbsListView.OnScrollListener l) {
		this.mOuterScrollListener = l;
	}

	public void setOutsideSroll(boolean scroll) {
		Log.i("FocusedListView", "setOutsideSroll scroll = " + scroll);
		this.mOutsieScroll = scroll;
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	public void setOnItemSelectedListener(FocusItemSelectedListener listener) {
		this.mOnItemSelectedListener = listener;
	}

	private void init(Context context) {
		setChildrenDrawingOrderEnabled(true);
		this.mPositionManager = new FocusedListViewPositionManager(context, this);
		super.setOnScrollListener(this.mOnScrollListener);
	}

	private void setScrolling(boolean scrolling) {
		synchronized (this.lock) {
			this.isScrolling = scrolling;
		}
	}

	private boolean isScrolling() {
		synchronized (this.lock) {
			return this.isScrolling;
		}
	}

	private void performItemSelect(View v, int position, boolean isSelected) {
		if (this.mOnItemSelectedListener != null) {
			Log.d("lingdang", "**position=" + position + ",isSelected=" + isSelected);
			this.mOnItemSelectedListener.onItemSelected(v, position, isSelected, this);
			if (v.isFocusable())
				if (isSelected)
					v.requestFocus();
				else
					v.clearFocus();
		}
	}

	private void performItemClick() {
		View v = getSelectedView();
		if ((v != null) && (this.mOnItemClickListener != null))
			this.mOnItemClickListener.onItemClick(this, v, this.mCurrentPosition, 0L);
	}

	public void dispatchDraw(Canvas canvas) {
		Log.i("FocusedListView", "dispatchDraw child count = " + getChildCount() + ", mOutsieScroll = " + this.mOutsieScroll);
		super.dispatchDraw(canvas);
		if ((this.mPositionManager.getSelectedView() == null) && (getSelectedView() != null) && (hasFocus())) {
			this.mPositionManager.setSelectedView(getSelectedView());
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		}
		this.mPositionManager.drawFrame(canvas);
		if (this.mOutsieScroll)
			invalidate();
	}

	protected void layoutChildren() {
		Log.i("ccdd", "layoutChildren()");
		super.layoutChildren();
		if (this.isSetSelectionAfterLayoutChildren)
			setSelection(this.mCurrentPosition);
	}

	public void setSelection(int position) {
		Log.i("ccdd", "mCurrentPositionchange:setSelection:" + position);
		this.mLastPosition = this.mCurrentPosition;
		this.mCurrentPosition = position;
		int top = 0;

		View selectedView = getSelectedView();
		if (selectedView != null) {
			top = selectedView.getTop() - getPaddingTop();
			super.setSelectionFromTop(position, top);
			this.isSetSelectionAfterLayoutChildren = false;
		} else {
			this.isSetSelectionAfterLayoutChildren = true;
		}
		Log.i("ccdd", "setSelection:" + position + ",top:" + top + ",getSelectedView().getTop():" + (getSelectedView() != null ? getSelectedView().getTop() : "null") + ",this.getPaddingTop():" + getPaddingTop() + ",getCount():" + getCount());
	}

	public int getSelectedItemPosition() {
		return this.mCurrentPosition;
	}

	public View getSelectedView() {
		int pos = this.mCurrentPosition;
		int indexOfView = pos - getFirstVisiblePosition();
		View selectedView = getChildAt(indexOfView);

		Log.i("FocusedListView", "getSelectedView mcurrentPosition:" + this.mCurrentPosition + ",firstVisiblePosition:" + getFirstVisiblePosition());

		return selectedView;
	}

	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		Log.i("ccdd", "onFocusChanged,gainFocus:" + gainFocus + ", mCurrentPosition = " + this.mCurrentPosition + ", child count = " + getChildCount());
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		synchronized (this) {
			this.mKeyTime = System.currentTimeMillis();
		}

		this.mPositionManager.setFocus(gainFocus);
		if (!gainFocus) {
			this.mPositionManager.drawFrame(null);
			this.mPositionManager.setSelectedView(null);
			this.mLastPosition = this.mCurrentPosition;
			this.mCurrentPosition = getSelectedItemPosition();
			this.mPositionManager.setFocusDrawableVisible(false, true);
			this.mPositionManager.setFocusDrawableShadowVisible(false, true);
			this.mPositionManager.setTransAnimation(false);
			this.mPositionManager.setScaleCurrentView(false);
			this.mPositionManager.setScaleLastView(true);
		} else {
			this.mCurrentPosition = super.getSelectedItemPosition();
			this.mCurrentPosition = ((this.mCurrentPosition > -1) && (this.mCurrentPosition < getCount()) ? this.mCurrentPosition : 0);
			setSelection(this.mCurrentPosition);
			this.mPositionManager.setLastSelectedView(null);
			this.mPositionManager.setScaleLastView(false);
			this.mPositionManager.setScaleCurrentView(true);

			this.mPositionManager.setFocusDrawableVisible(true, true);
			this.mPositionManager.setFocusDrawableShadowVisible(true, true);
			this.mPositionManager.setTransAnimation(false);
		}

		if (getSelectedView() != null) {
			this.mPositionManager.setSelectedView(getSelectedView());
			performItemSelect(getSelectedView(), this.mCurrentPosition, gainFocus);
		}
		invalidate();

		this.mPositionManager.setNeedDraw(true);
		this.mPositionManager.setState(1);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((getSelectedView() != null) && (getSelectedView().onKeyUp(keyCode, event))) {
			return true;
		}

		if ((keyCode == 23) || (keyCode == 66)) {
			performItemClick();
			return true;
		}

		if ((keyCode == 20) || (keyCode == 20)) {
			return true;
		}

		Log.i("FocusedListView", "onKeyUp super:" + keyCode);
		return super.onKeyUp(keyCode, event);
	}

	public void setOnKeyDownListener(onKeyDownListener l) {
		this.onKeyDownListener = l;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((this.onKeyDownListener != null) && (this.onKeyDownListener.onKeyDown(keyCode, event))) {
			Log.d("lingdang", "1111111111111111");
			return true;
		}
		if ((keyCode != 20) && (keyCode != 19)) {
			Log.d("lingdang", "222222222222222");
			return super.onKeyDown(keyCode, event);
		}

		Log.i("FocusedListView", "onKeyDown keyCode = " + keyCode + ", child count = " + getChildCount());
		synchronized (this) {
			if ((System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL) || (this.mPositionManager.getState() == 1) || (isScrolling())) {
				Log.d("lingdang", "KeyInterval:" + (System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL) + ", getState():" + (this.mPositionManager.getState() == 1 ? "drawing" : "idle") + ", isScrolling: = " + isScrolling());
				Log.d("lingdang", "333333333333333");
				return true;
			}
			this.mKeyTime = System.currentTimeMillis();
		}

		if ((getSelectedView() != null) && (getSelectedView().onKeyDown(keyCode, event))) {
			Log.d("lingdang", "44444444444444444444444");
			return true;
		}

		switch (keyCode) {
		case 19:
			Log.d("lingdang", "5555555555555555");
			if (!arrowScroll(33)) {
				Log.d("lingdang", "5555555111111111111155555555");
				Log.i("FocusedListView", "onKeyDown super focus_up");
				return super.onKeyDown(keyCode, event);
			}
			return true;
		case 20:
			Log.d("lingdang", "6666666666666666666666666");
			if (!arrowScroll(130)) {
				Log.d("lingdang", "6666666666611111111111166666666666");
				Log.i("FocusedListView", "onKeyDown super focus_down");
				return super.onKeyDown(keyCode, event);
			}
			return true;
		}

		Log.i("FocusedListView", "onKeyDown super");
		Log.d("lingdang", "77777777777");
		return super.onKeyDown(keyCode, event);
	}

	public boolean arrowScroll(int direction) {
		Log.d("lingdang", "+++++111111111111++++");
		if (this.mScrollDistance <= 0) {
			Log.d("lingdang", "+++++22222222222++++");
			if (getChildCount() > 0) {
				this.mScrollDistance = getChildAt(0).getHeight();
			}
		}
		Log.i("FocusedListView", "scrollBy:mCurrentPosition before " + this.mCurrentPosition);
		View lastSelectedView = getSelectedView();

		int lastPosition = this.mCurrentPosition;

		boolean isNeedTrans = true;
		int scrollBy = 0;
		int paddedTop = getListPaddingTop();
		int paddedBottom = getHeight() - getListPaddingBottom();
		switch (direction) {
		case 33:
			Log.d("lingdang", "+++++3333333333++++");
			if (this.mCurrentPosition > 0) {
				this.mCurrentPosition -= 1;
				View mCurrentView = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());

				if (mCurrentView != null) {
					int targetTop = mCurrentView.getTop();
					if (targetTop < paddedTop)
						scrollBy = targetTop - paddedTop;
				} else {
					scrollBy = -this.mScrollDistance;
				}
			} else {
				return false;
			}
			break;
		case 130:
			Log.d("lingdang", "+++++444444444++++");

			Log.i("FocusedListView", "mCurrentPosition:" + this.mCurrentPosition + ",getCount:" + getCount());
			if (this.mCurrentPosition < getCount() - 1) {
				Log.d("lingdang", "+++++5555555555++++");
				this.mCurrentPosition += 1;
				View mCurrentView = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				if (mCurrentView != null) {
					Log.d("lingdang", "+++++6666666666++++");
					int targetBottom = mCurrentView.getBottom();
					if (targetBottom > paddedBottom) {
						Log.d("lingdang", "+++++7777777777++++");
						scrollBy = targetBottom - paddedBottom;
					}
				} else {
					Log.d("lingdang", "+++++888888888++++mScrollDistance=" + this.mScrollDistance + "scrollbBy=" + scrollBy);
					scrollBy = this.mScrollDistance;
				}
			} else {
				Log.d("lingdang", "+++++9999999999++++");
				return false;
			}
			break;
		}
		Log.i("FocusedListView", "scrollBy:" + scrollBy + " mCurrentPosition after " + this.mCurrentPosition);

		playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
		if (lastPosition != this.mCurrentPosition) {
			Log.d("lingdang", "+++++-----1------++++");
			this.mLastPosition = lastPosition;
		}

		if (lastSelectedView != null) {
			Log.d("lingdang", "+++++-----2------++++");
			performItemSelect(lastSelectedView, lastPosition, false);
		}

		if ((getSelectedView() != null) && (getSelectedView() != lastSelectedView) && (lastPosition != this.mCurrentPosition)) {
			Log.d("lingdang", "+++++-----3------++++");
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		}

		boolean isScaleLastView = true;
		boolean isScaleCurrentView = true;
		this.mPositionManager.setSelectedView(getSelectedView());
		this.mPositionManager.setTransAnimation(isNeedTrans);

		if (scrollBy != 0) {
			Log.d("lingdang", "+++++-----4------++++scrollBy=" + scrollBy);
			this.mPositionManager.setContrantNotDraw(true);
			this.mNeedScroll = true;
			smoothScrollBy(scrollBy, 150);
			this.mHandler.sendEmptyMessageDelayed(1, 50L);
		} else {
			setSelection(this.mCurrentPosition);
			Log.d("lingdang", "+++++-----5------++++");
			this.mPositionManager.setNeedDraw(true);
			this.mPositionManager.setContrantNotDraw(false);
			this.mPositionManager.setState(1);
			this.mPositionManager.setScaleCurrentView(isScaleCurrentView);
			this.mPositionManager.setScaleLastView(isScaleLastView);
			invalidate();
		}

		return true;
	}

	protected int getChildDrawingOrder(int childCount, int i) {
		int selectedIndex = getSelectedItemPosition() - getFirstVisiblePosition();
		if (selectedIndex < 0) {
			return i;
		}

		if (i < selectedIndex)
			return i;
		if (i >= selectedIndex) {
			return childCount - 1 - i + selectedIndex;
		}
		return i;
	}

	public static abstract interface FocusItemSelectedListener {
		public abstract void onItemSelected(View paramView, int paramInt, boolean paramBoolean, AdapterView paramAdapterView);
	}

	class FocusedListViewPositionManager extends FocusedBasePositionManager {
		public FocusedListViewPositionManager(Context context, View container) {
			super(context, container);
		}

		public Rect getDstRectBeforeScale(boolean isBeforeScale) {
			View selectedView = getSelectedView();
			if (selectedView == null) {
				return null;
			}

			View focusedView = selectedView.findViewById(FocusedListView.this.mFocusViewId);
			if (focusedView == null) {
				focusedView = selectedView;
			}
			Rect focusedRect = new Rect();
			Rect listViewRect = new Rect();
			Rect selectViewRect = new Rect();

			focusedView.getGlobalVisibleRect(focusedRect);
			FocusedListView.this.getGlobalVisibleRect(listViewRect);
			selectedView.getGlobalVisibleRect(selectViewRect);
			Log.i("FocusedBasePositionManager", "mLastFocusRect imgView:" + focusedRect + "(" + focusedRect.width() + "," + focusedRect.height() + ")");
			Log.i("FocusedBasePositionManager", "mLastFocusRect listViewRect:" + listViewRect + "(" + listViewRect.width() + "," + listViewRect.height() + ")");
			Log.i("FocusedBasePositionManager", "mLastFocusRect selectViewRect:" + selectViewRect + "(" + selectViewRect.width() + "," + selectViewRect.height() + ")");

			if (isBeforeScale) {
				int hCenter = (selectViewRect.top + selectViewRect.bottom) / 2;
				int wCenter = (selectViewRect.left + selectViewRect.right) / 2;
				int imgUnscaleWidth = (int) (focusedRect.width() / getItemScaleXValue());
				int imgUnscaleHeight = (int) (focusedRect.height() / getItemScaleYValue());

				focusedRect.left = ((int) (wCenter - focusedRect.width() / getItemScaleXValue() / 2.0F));
				focusedRect.right = (focusedRect.left + imgUnscaleWidth);
				focusedRect.top = ((int) (hCenter - (hCenter - focusedRect.top) / getItemScaleYValue()));
				focusedRect.bottom = (focusedRect.top + imgUnscaleHeight);
			}

			int imgW = focusedRect.right - focusedRect.left;
			int imgH = focusedRect.bottom - focusedRect.top;
			focusedRect.left = ((int) (focusedRect.left + (1.0D - getItemScaleXValue()) * imgW / 2.0D));
			focusedRect.top = ((int) (focusedRect.top + (1.0D - getItemScaleYValue()) * ((selectViewRect.top + selectViewRect.bottom) / 2 - focusedRect.top)));
			focusedRect.right = ((int) (focusedRect.left + imgW * getItemScaleXValue()));
			focusedRect.bottom = ((int) (focusedRect.top + imgH * getItemScaleYValue()));

			focusedRect.left -= listViewRect.left;
			focusedRect.right -= listViewRect.left;
			focusedRect.top -= listViewRect.top;
			focusedRect.bottom -= listViewRect.top;

			focusedRect.top -= getSelectedPaddingTop();
			focusedRect.left -= getSelectedPaddingLeft();
			focusedRect.right += getSelectedPaddingRight();
			focusedRect.bottom += getSelectedPaddingBottom();
			return focusedRect;
		}

		public void drawChild(Canvas canvas) {
			Log.i("FocusedBasePositionManager", "drawChild");
			FocusedListView.this.drawChild(canvas, getSelectedView(), FocusedListView.this.getDrawingTime());
		}

		public Rect getDstRectAfterScale(boolean isShadow) {
			View selectedView = getSelectedView();
			if (selectedView == null) {
				return null;
			}

			View focusedView = selectedView.findViewById(FocusedListView.this.mFocusViewId);
			if (focusedView == null) {
				focusedView = selectedView;
			}
			Rect focusedRect = new Rect();
			Rect listViewRect = new Rect();
			Rect selectViewRect = new Rect();

			focusedView.getGlobalVisibleRect(focusedRect);
			selectedView.getGlobalVisibleRect(selectViewRect);
			FocusedListView.this.getGlobalVisibleRect(listViewRect);

			focusedRect.left -= listViewRect.left;
			focusedRect.right -= listViewRect.left;
			focusedRect.top -= listViewRect.top;
			focusedRect.bottom -= listViewRect.top;

			if ((isShadow) && (isLastFrame())) {
				focusedRect.top -= getSelectedShadowPaddingTop();
				focusedRect.left -= getSelectedShadowPaddingLeft();
				focusedRect.right += getSelectedShadowPaddingRight();
				focusedRect.bottom += getSelectedShadowPaddingBottom();
			} else {
				focusedRect.top -= getSelectedPaddingTop();
				focusedRect.left -= getSelectedPaddingLeft();
				focusedRect.right += getSelectedPaddingRight();
				focusedRect.bottom += getSelectedPaddingBottom();
			}

			return focusedRect;
		}
	}

	public static abstract interface onKeyDownListener {
		public abstract boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent);
	}
}