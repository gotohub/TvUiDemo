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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Scroller;

public class FocusedGridView extends GridView implements FocusedBasePositionManager.PositionInterface {
	private static final String TAG = "FocusedGridView";
	private static final int SCROLLING_DURATION = 500;
	private static final int SCROLLING_DELAY = 50;
	private static final int SCROLL_DURATION = 100;
	public static final int HORIZONTAL_SINGEL = 1;
	public static final int HORIZONTAL_FULL = 2;
	public static final int HORIZONTAL_OUTSIDE_FULL = 3;
	public static final int HORIZONTAL_OUTSIDE_SINGEL = 4;
	private long KEY_INTERVEL = 20L;
	private long mKeyTime = 0L;

	private int mCurrentPosition = -1;
	private int mLastPosition = -1;
	private AbsListView.OnScrollListener mOuterScrollListener;
	private boolean isScrolling = false;
	private Object lock = new Object();
	private int mStartX;
	private boolean mNeedScroll = false;
	private boolean mOutsieScroll = false;
	private FocusedGridPositionManager mPositionManager;
	private AdapterView.OnItemClickListener mOnItemClickListener = null;
	private FocusItemSelectedListener mOnItemSelectedListener = null;
	private int mFocusViewId = -1;
	private int mHeaderPosition = -1;
	private boolean mHeaderSelected = false;
	private boolean mIsFocusInit = false;
	private int mLastOtherPosition = -1;
	private FocusedScroller mScroller;
	private int mScreenWidth;
	private boolean mInit = false;
	private int mHorizontalMode = -1;
	private int mViewLeft = 0;
	private int mViewRight = 20;
	private ScrollerListener mScrollerListener;
	private boolean mAutoChangeLine = true;

	private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (FocusedGridView.this.mOuterScrollListener != null) {
				FocusedGridView.this.mOuterScrollListener.onScrollStateChanged(view, scrollState);
			}
			Log.d("FocusedGridView", "onScrollStateChanged scrolling");
			switch (scrollState) {
			case 1:
			case 2:
				FocusedGridView.this.setScrolling(true);
				break;
			case 0:
				Log.d("FocusedGridView", "onScrollStateChanged idle mNeedScroll = " + FocusedGridView.this.mNeedScroll);
				if (FocusedGridView.this.mNeedScroll) {
					FocusedGridView.this.setSelection(FocusedGridView.this.mCurrentPosition);
				}
				FocusedGridView.this.setScrolling(false);
				break;
			}
		}

		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (FocusedGridView.this.mOuterScrollListener != null)
				FocusedGridView.this.mOuterScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	};

	public static int FOCUS_ITEM_REMEMBER_LAST = 0;

	public static int FOCUS_ITEM_AUTO_SEARCH = 1;
	private int focusPositionMode = FOCUS_ITEM_REMEMBER_LAST;

	int mScrollDistance = 0;
	int mScrollHeaderDiscance = 0;
	private static final int DRAW_FOCUS = 1;
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Log.d("FocusedGridView", "Handler handleMessage");
				if (FocusedGridView.this.getSelectedView() != null) {
					FocusedGridView.this.performItemSelect(FocusedGridView.this.getSelectedView(), FocusedGridView.this.mCurrentPosition, true);
				}

				FocusedGridView.this.mPositionManager.setSelectedView(FocusedGridView.this.getSelectedView());
				FocusedGridView.this.mPositionManager.setTransAnimation(false);
				FocusedGridView.this.mPositionManager.setNeedDraw(true);
				if (FocusedGridView.this.checkHeaderPosition()) {
					if (!FocusedGridView.this.checkFromHeaderPosition()) {
						FocusedGridView.this.mPositionManager.setContrantNotDraw(true);
						FocusedGridView.this.mPositionManager.setScaleCurrentView(false);
					}
				} else {
					FocusedGridView.this.mPositionManager.setContrantNotDraw(false);
					FocusedGridView.this.mPositionManager.setScaleCurrentView(true);
				}
				FocusedGridView.this.mPositionManager.setScaleLastView(true);
				FocusedGridView.this.mPositionManager.setState(1);
				if (!FocusedGridView.this.isScrolling()) {
					FocusedGridView.this.invalidate();
				}
				break;
			}
		}
	};
	private onKeyDownListener onKeyDownListener;

	public void setAutoChangeLine(boolean isChange) {
		this.mAutoChangeLine = isChange;
	}

	public void setScrollerListener(ScrollerListener l) {
		this.mScrollerListener = l;
	}

	public void setHorizontalMode(int mode) {
		this.mHorizontalMode = mode;
	}

	public void setHeaderPosition(int position) {
		this.mHeaderPosition = position;
	}

	private boolean hasHeader() {
		return this.mHeaderPosition >= 0;
	}

	private boolean checkHeaderPosition() {
		return (hasHeader()) && (this.mCurrentPosition < getNumColumns());
	}

	public boolean checkHeaderPosition(int position) {
		return (hasHeader()) && (position < getNumColumns());
	}

	public boolean checkFromHeaderPosition() {
		return (hasHeader()) && (this.mLastPosition < getNumColumns());
	}

	public void setOutsideSroll(boolean scroll) {
		Log.d("FocusedGridView", "setOutsideSroll scroll = " + scroll);
		this.mOutsieScroll = scroll;
	}

	public void setFocusViewId(int id) {
		this.mFocusViewId = id;
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	public void setOnItemSelectedListener(FocusItemSelectedListener listener) {
		this.mOnItemSelectedListener = listener;
	}

	public void setViewRight(int right) {
		this.mViewRight = right;
	}

	public void setViewLeft(int left) {
		this.mViewLeft = left;
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

	public FocusedGridView(Context contxt) {
		super(contxt);
		init(contxt);
	}

	public FocusedGridView(Context contxt, AttributeSet attrs) {
		super(contxt, attrs);
		init(contxt);
	}

	public FocusedGridView(Context contxt, AttributeSet attrs, int defStyle) {
		super(contxt, attrs, defStyle);
		init(contxt);
	}

	private void initLeftPosition() {
		if (!this.mInit) {
			this.mInit = true;
			int[] location = new int[2];
			getLocationOnScreen(location);
			this.mStartX = (location[0] + getPaddingLeft());
			Log.d("FocusedGridView", "initLeftPosition mStartX = " + this.mStartX);
		}
	}

	private void init(Context context) {
		setChildrenDrawingOrderEnabled(true);
		this.mPositionManager = new FocusedGridPositionManager(context, this);
		this.mScroller = new FocusedScroller(context);
		this.mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
		super.setOnScrollListener(this.mOnScrollListener);
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

	public void setFrameRate(int rate) {
		this.mPositionManager.setFrameRate(rate);
	}

	public void dispatchDraw(Canvas canvas) {
		Log.i("FocusedGridView", "dispatchDraw child count = " + getChildCount() + ", mOutsieScroll = " + this.mOutsieScroll);
		super.dispatchDraw(canvas);
		if ((this.mPositionManager.getSelectedView() == null) && (getSelectedView() != null) && (hasFocus())) {
			this.mPositionManager.setSelectedView(getSelectedView());
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		}
		this.mPositionManager.drawFrame(canvas);
		if (this.mOutsieScroll) {
			invalidate();
		}

		if (hasFocus())
			focusInit();
	}

	public void subSelectPosition() {
		arrowScroll(17);
	}

	public void setNumColumns(int numColumns) {
		Log.i("FocusedGridView", "setNumColumns: origin" + getNumColumns() + ",setNumColumns:" + numColumns);
		int originNumColumns = getNumColumns();
		if (originNumColumns != numColumns) {
			int current = getSelectedItemPosition();
			if (current != -1) {
				int row = current / originNumColumns;
				current += row * (numColumns - originNumColumns);
				if (current != getSelectedItemPosition()) {
					setSelection(current);
				}
			}
		}
		super.setNumColumns(numColumns);
	}
	
	public void setSelection(int position) {
		Log.d("FocusedGridView", "setSelection");
		this.mLastPosition = this.mCurrentPosition;
		this.mCurrentPosition = position;
		super.setSelection(position);
	}

	public void setOnScrollListener(AbsListView.OnScrollListener l) {
		this.mOuterScrollListener = l;
	}

	public void setFocusPositionMode(int mode) {
		this.focusPositionMode = mode;
	}

	public int getFocusPositionMode() {
		return this.focusPositionMode;
	}

	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		Log.d("FocusedGridView", "onFocusChanged,gainFocus:" + gainFocus + ", mCurrentPosition = " + this.mCurrentPosition + ", child count = " + getChildCount());
		if (this.focusPositionMode == FOCUS_ITEM_AUTO_SEARCH) {
			Log.i("FocusedGridView", "focusaaa,super.onFocusChanged1");
			super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		}
		synchronized (this) {
			this.mKeyTime = System.currentTimeMillis();
		}

		if (gainFocus != this.mPositionManager.hasFocus()) {
			this.mIsFocusInit = false;
		}
		this.mPositionManager.setFocus(gainFocus);

		focusInit();
		initLeftPosition();
	}

	private void focusInit() {
		if (this.mIsFocusInit) {
			return;
		}
		Log.d("FocusedGridView", "focusInit mCurrentPosition = " + this.mCurrentPosition + ", getSelectedItemPosition() = " + getSelectedItemPosition());
		if (this.mCurrentPosition < 0) {
			this.mCurrentPosition = getSelectedItemPosition();
		}

		if (this.mCurrentPosition < 0) {
			this.mCurrentPosition = 0;
		}

		if (!hasFocus()) {
			this.mPositionManager.drawFrame(null);
			this.mPositionManager.setSelectedView(null);
			this.mLastPosition = this.mCurrentPosition;
			this.mPositionManager.setFocusDrawableVisible(false, true);
			this.mPositionManager.setFocusDrawableShadowVisible(false, true);
			this.mPositionManager.setTransAnimation(false);
			this.mPositionManager.setScaleCurrentView(false);
			if (checkHeaderPosition()) {
				this.mPositionManager.setContrantNotDraw(true);
				this.mPositionManager.setScaleLastView(false);
			} else {
				this.mPositionManager.setScaleLastView(true);
			}
		} else {
			if (this.focusPositionMode == FOCUS_ITEM_AUTO_SEARCH) {
				Log.i("FocusedGridView", "focusaaa,super.onFocusChanged2");
				this.mCurrentPosition = super.getSelectedItemPosition();
			} else {
				Log.i("FocusedGridView", "onfocus setSelection:" + ((this.mCurrentPosition > -1) && (this.mCurrentPosition < getCount()) ? this.mCurrentPosition : 0));
				setSelection((this.mCurrentPosition > -1) && (this.mCurrentPosition < getCount()) ? this.mCurrentPosition : 0);
			}

			this.mPositionManager.setLastSelectedView(null);
			this.mPositionManager.setScaleLastView(false);
			if (checkHeaderPosition()) {
				this.mPositionManager.setContrantNotDraw(true);
				this.mPositionManager.setScaleCurrentView(false);
			} else {
				this.mPositionManager.setScaleCurrentView(true);
			}
		}

		if (getSelectedView() != null) {
			if (checkHeaderPosition()) {
				this.mPositionManager.setSelectedView(getSelectedView());
				performItemSelect(getSelectedView(), this.mHeaderPosition, hasFocus());
			} else {
				this.mPositionManager.setSelectedView(getSelectedView());
				performItemSelect(getSelectedView(), this.mCurrentPosition, hasFocus());
			}

			if (this.mCurrentPosition >= 0) {
				this.mIsFocusInit = true;
			}
		}

		invalidate();

		this.mPositionManager.setNeedDraw(true);
		this.mPositionManager.setState(1);
	}

	public void setItemScaleValue(float scaleXValue, float scaleYValue) {
		this.mPositionManager.setItemScaleValue(scaleXValue, scaleYValue);
	}

	public int getSelectedItemPosition() {
		return this.mCurrentPosition;
	}

	public int getLastSelectedItemPosition() {
		return this.mLastPosition;
	}

	public View getSelectedView() {
		if (getChildCount() <= 0) {
			return null;
		}

		int pos = this.mCurrentPosition;
		if (checkHeaderPosition()) {
			pos = this.mHeaderPosition;
		}

		int indexOfView = pos - getFirstVisiblePosition();
		View selectedView = getChildAt(indexOfView);

		Log.i("FocusedGridView", "getSelectedView getSelectedView: indexOfView = " + indexOfView + ", child count = " + getChildCount());
		return selectedView;
	}

	private void performItemSelect(View v, int position, boolean isSelected) {
		if (this.mOnItemSelectedListener != null)
			this.mOnItemSelectedListener.onItemSelected(v, position, isSelected, this);
	}

	private void performItemClick() {
		View v = getSelectedView();
		if ((v != null) && (this.mOnItemClickListener != null))
			this.mOnItemClickListener.onItemClick(this, v, this.mCurrentPosition, 0L);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((getSelectedView() != null) && (getSelectedView().onKeyUp(keyCode, event))) {
			return true;
		}

		if ((keyCode == 23) || (keyCode == 66)) {
			performItemClick();
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((this.onKeyDownListener != null) && (this.onKeyDownListener.onKeyDown(keyCode, event))) {
			return true;
		}
		if ((keyCode != 23) && (keyCode != 66) && (keyCode != 21) && (keyCode != 22) && (keyCode != 20) && (keyCode != 19)) {
			return super.onKeyDown(keyCode, event);
		}
		Log.d("FocusedGridView", "onKeyDown keyCode = " + keyCode + ", child count = " + getChildCount() + ", mCurrentPosition = " + this.mCurrentPosition);
		synchronized (this) {
			if ((System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL) || (this.mPositionManager.getState() == 1) || (isScrolling())) {
				Log.d("FocusedGridView", "KyeInterval:" + (System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL) + "getState():" + this.mPositionManager.getState() + ", isScrolling() = " + isScrolling());
				return true;
			}
			this.mKeyTime = System.currentTimeMillis();
		}

		if (getChildCount() <= 0) {
			return true;
		}

		if (((2 == this.mHorizontalMode) || (1 == this.mHorizontalMode)) && (!this.mScroller.isFinished()))
			return true;
		if (((3 == this.mHorizontalMode) || (4 == this.mHorizontalMode)) && (this.mScrollerListener != null) && (!this.mScrollerListener.isFinished())) {
			return true;
		}

		if (!this.mAutoChangeLine) {
			if ((keyCode == 22) && ((this.mCurrentPosition + 1) % getNumColumns() == 0))
				return false;
			if ((keyCode == 21) && (this.mCurrentPosition != 0) && (this.mCurrentPosition % getNumColumns() == 0)) {
				return false;
			}

		}

		if ((getSelectedView() != null) && (getSelectedView().onKeyDown(keyCode, event))) {
			return true;
		}

		if ((hasHeader())
				&& ((((this.mCurrentPosition + 1) / getNumColumns() == 1) && ((this.mCurrentPosition + 1) % getNumColumns() == 0) && (22 == keyCode)) || ((this.mCurrentPosition / getNumColumns() == 1) && (this.mCurrentPosition % getNumColumns() == 0) && (21 == keyCode)))) {
			return true;
		}

		switch (keyCode) {
		case 19:
			if (!arrowScroll(33)) {
				return super.onKeyDown(keyCode, event);
			}
			return true;
		case 20:
			if (!arrowScroll(130)) {
				return super.onKeyDown(keyCode, event);
			}
			return true;
		case 21:
			if (!arrowScroll(17)) {
				return super.onKeyDown(keyCode, event);
			}
			return true;
		case 22:
			if (!arrowScroll(66)) {
				return super.onKeyDown(keyCode, event);
			}
			return true;
		}

		Log.d("FocusedGridView", "onKeyDown super");
		return super.onKeyDown(keyCode, event);
	}

	private boolean arrowScroll(int direction) {
		if (this.mScrollDistance <= 0) {
			if (hasHeader()) {
				if (getChildAt(getNumColumns()) != null) {
					this.mScrollDistance = getChildAt(getNumColumns()).getHeight();
				}
				this.mScrollHeaderDiscance = getChildAt(this.mHeaderPosition).getHeight();
				Log.i("FocusedGridView", "scrollBy: mScrollHeaderDiscance " + this.mScrollHeaderDiscance);
			} else if (getCount() > 0) {
				this.mScrollDistance = getChildAt(0).getHeight();
				Log.i("FocusedGridView", "scrollBy: mScrollDistance " + this.mScrollDistance);
			}
		}

		Log.i("FocusedGridView", "scrollBy:mCurrentPosition before " + this.mCurrentPosition);
		View lastSelectedView = getSelectedView();

		int lastPosition = this.mCurrentPosition;

		boolean isNeedTrans = true;
		int scrollBy = 0;
		int columns = getNumColumns();
		int paddedTop = getListPaddingTop();
		int paddedBottom = getHeight() - getListPaddingBottom();
		boolean isHeaderViewVisible = this.mHeaderPosition >= getFirstVisiblePosition();
		switch (direction) {
		case 33:
			if (this.mCurrentPosition >= columns) {
				this.mCurrentPosition -= columns;
				View mCurrentView;
				if (checkHeaderPosition()) {
					mCurrentView = getChildAt(this.mHeaderPosition - getFirstVisiblePosition());
					this.mCurrentPosition = this.mHeaderPosition;
				} else {
					mCurrentView = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				}

				if (mCurrentView != null) {
					int targetTop = mCurrentView.getTop();
					if (targetTop < paddedTop)
						scrollBy = targetTop - paddedTop;
				} else {
					scrollBy = checkHeaderPosition() ? -this.mScrollHeaderDiscance : -this.mScrollDistance;
				}
			} else {
				return false;
			}

			break;
		case 130:
			if (this.mCurrentPosition / columns < (getCount() - 1) / columns) {
				this.mCurrentPosition += columns;
				this.mCurrentPosition = (this.mCurrentPosition > getCount() - 1 ? getCount() - 1 : this.mCurrentPosition);
				View mCurrentView = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				if (mCurrentView != null) {
					int targetBottom = mCurrentView.getBottom();
					if (targetBottom > paddedBottom)
						scrollBy = targetBottom - paddedBottom;
				} else {
					scrollBy = isHeaderViewVisible ? this.mScrollHeaderDiscance : this.mScrollDistance;
				}
			} else {
				return false;
			}
			break;
		case 17:
			if (this.mCurrentPosition > 0) {
				this.mCurrentPosition -= 1;
				if ((this.mCurrentPosition + 1) % columns == 0) {
					View mCurrentView;
					if (checkHeaderPosition()) {
						mCurrentView = getChildAt(this.mHeaderPosition - getFirstVisiblePosition());
						this.mCurrentPosition = this.mHeaderPosition;
					} else {
						mCurrentView = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
					}
					if (mCurrentView != null) {
						int targetTop = mCurrentView.getTop();
						if (targetTop < paddedTop) {
							scrollBy = targetTop - paddedTop;
						}
						isNeedTrans = false;
					} else {
						scrollBy = checkHeaderPosition() ? -this.mScrollHeaderDiscance : -this.mScrollDistance;
					}
				}
			} else {
				return false;
			}
			break;
		case 66:
			if (this.mCurrentPosition < getCount() - 1) {
				this.mCurrentPosition += 1;
				if (this.mCurrentPosition % columns == 0) {
					View mCurrentView = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
					if (mCurrentView != null) {
						int targetBottom = mCurrentView.getBottom();
						if (targetBottom > paddedBottom) {
							scrollBy = targetBottom - paddedBottom;
						}
						isNeedTrans = false;
					} else {
						scrollBy = isHeaderViewVisible ? this.mScrollHeaderDiscance : this.mScrollDistance;
					}
				}
			} else {
				return false;
			}
			break;
		}
		Log.i("FocusedGridView", "arrowScroll: mCurrentPosition = " + this.mCurrentPosition);

		playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));

		if (lastPosition != this.mCurrentPosition) {
			this.mLastPosition = lastPosition;
		}
		if (checkHeaderPosition()) {
			if (scrollBy != 0) {
				this.mPositionManager.setContrantNotDraw(true);
				this.mNeedScroll = true;
				smoothScrollBy(scrollBy, 500);
				this.mHandler.sendEmptyMessageDelayed(1, 50L);
				return true;
			}
			this.mPositionManager.setContrantNotDraw(true);
			this.mPositionManager.setTransAnimation(false);
			if (checkFromHeaderPosition()) {
				Log.d("FocusedGridView", "arrowScroll focus form header");
				this.mPositionManager.setScaleCurrentView(false);
				this.mPositionManager.setScaleLastView(false);
				this.mPositionManager.setNeedDraw(false);
			} else {
				Log.d("FocusedGridView", "arrowScroll focus form other");
				this.mLastOtherPosition = lastPosition;
				this.mPositionManager.setScaleCurrentView(false);
				this.mPositionManager.setScaleLastView(true);
				this.mPositionManager.setNeedDraw(true);
				invalidate();
			}
			Log.d("FocusedGridView", "arrowScroll header mCurrentPosition = " + this.mCurrentPosition + ", mHeaderPosition = " + this.mHeaderPosition + ", mHeaderSelected = " + this.mHeaderSelected);
			if (!this.mHeaderSelected) {
				this.mHeaderSelected = true;
				if (lastSelectedView != null) {
					performItemSelect(lastSelectedView, lastPosition, false);
				}

				if ((getSelectedView() != null) && (getSelectedView() != lastSelectedView) && (lastPosition != this.mCurrentPosition)) {
					performItemSelect(getSelectedView(), this.mHeaderPosition, true);
				}
			}

			return true;
		}

		boolean isScaleLastView = true;
		boolean isScaleCurrentView = true;
		Log.d("FocusedGridView", "arrowScroll this.mLastPosition = " + this.mLastPosition + ", this.mCurrentPosition = " + this.mCurrentPosition + ", lastPosition = " + lastPosition);
		if (checkFromHeaderPosition()) {
			isNeedTrans = false;
			isScaleLastView = false;
			if ((this.mLastOtherPosition >= 0) && (lastPosition == this.mHeaderPosition)) {
				this.mCurrentPosition = this.mLastOtherPosition;
			}
		}

		this.mHeaderSelected = false;
		if (lastSelectedView != null) {
			performItemSelect(lastSelectedView, lastPosition, false);
		}

		if ((getSelectedView() != null) && (getSelectedView() != lastSelectedView) && (lastPosition != this.mCurrentPosition)) {
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		}

		this.mPositionManager.setSelectedView(getSelectedView());

		if (checkHeaderPosition()) {
			isNeedTrans = false;
			isScaleCurrentView = false;
		}
		this.mPositionManager.setTransAnimation(isNeedTrans);

		horizontalScroll();
		if (scrollBy != 0) {
			Log.i("FocusedGridView", "scrollBy: scrollBy = " + scrollBy);
			this.mPositionManager.setContrantNotDraw(true);
			this.mNeedScroll = true;
			smoothScrollBy(scrollBy, 500);
			this.mHandler.sendEmptyMessageDelayed(1, 50L);
		} else {
			super.setSelection(this.mCurrentPosition);
			this.mPositionManager.setNeedDraw(true);
			this.mPositionManager.setContrantNotDraw(false);
			this.mPositionManager.setState(1);
			this.mPositionManager.setScaleCurrentView(isScaleCurrentView);
			this.mPositionManager.setScaleLastView(isScaleLastView);
			invalidate();
		}

		return true;
	}

	private void horizontalScroll() {
		if (1 == this.mHorizontalMode)
			horizontalScrollSingel();
		else if (2 == this.mHorizontalMode)
			horizontalScrollFull();
		else if ((3 == this.mHorizontalMode) && (this.mScrollerListener != null))
			horizontalOutsideScrollFull();
		else if ((4 == this.mHorizontalMode) && (this.mScrollerListener != null))
			horizontalOutsideScrollSingel();
	}

	void horizontalScrollFull() {
		int[] location = new int[2];
		getSelectedView().getLocationOnScreen(location);
		int left = location[0];
		int right = location[0] + getSelectedView().getWidth();
		int imgW = getSelectedView().getWidth();
		left = (int) (left + (1.0D - this.mPositionManager.getItemScaleXValue()) * imgW / 2.0D);
		right = (int) (left + imgW * this.mPositionManager.getItemScaleXValue());

		getLocationOnScreen(location);
		if ((right >= this.mScreenWidth) && (!this.mOutsieScroll)) {
			int dx = left - this.mStartX - this.mViewLeft;
			Log.d("FocusedGridView", "scrollFull to right dx = " + dx + ", mStartX = " + this.mStartX + ", mScreenWidth = " + this.mScreenWidth + ", left = " + left);
			if (dx + this.mScroller.getFinalX() > location[0] + getWidth()) {
				dx = location[0] + getWidth() - this.mScroller.getFinalX();
			}
			int duration = dx * 100 / 300;

			horizontalSmoothScrollBy(dx, duration);
			return;
		}

		Log.d("FocusedGridView", "scroll conrtainer left = " + this.mStartX);
		if ((left < this.mStartX) && (!this.mOutsieScroll)) {
			int dx = right - this.mScreenWidth;
			if (this.mScroller.getCurrX() < Math.abs(dx)) {
				dx = -this.mScroller.getCurrX();
			}
			int duration = -dx * 100 / 300;

			this.mScrollerListener.horizontalSmoothScrollBy(dx, duration);
		}
	}

	void horizontalOutsideScrollFull() {
		int[] location = new int[2];
		getSelectedView().getLocationOnScreen(location);
		int left = location[0];
		int right = location[0] + getSelectedView().getWidth();
		int imgW = getSelectedView().getWidth();
		left = (int) (left + (1.0D - this.mPositionManager.getItemScaleXValue()) * imgW / 2.0D);
		right = (int) (left + imgW * this.mPositionManager.getItemScaleXValue());

		getLocationOnScreen(location);
		if ((right >= this.mScreenWidth) && (!this.mOutsieScroll)) {
			int dx = left - this.mStartX - this.mViewLeft;
			Log.d("FocusedGridView", "scrollFull to right dx = " + dx + ", mStartX = " + this.mStartX + ", mScreenWidth = " + this.mScreenWidth + ", left = " + left);
			if (dx + this.mScrollerListener.getFinalX(true) > location[0] + getWidth()) {
				dx = location[0] + getWidth() - this.mScrollerListener.getFinalX(true);
			}
			int duration = dx * 100 / 300;

			this.mScrollerListener.horizontalSmoothScrollBy(dx, duration);

			return;
		}

		Log.d("FocusedGridView", "scroll conrtainer left = " + this.mStartX);
		if ((left < this.mStartX) && (!this.mOutsieScroll)) {
			int dx = right - this.mScreenWidth;
			if (this.mScrollerListener.getCurrX(true) < Math.abs(dx)) {
				dx = -this.mScrollerListener.getCurrX(true);
			}
			int duration = -dx * 100 / 300;

			this.mScrollerListener.horizontalSmoothScrollBy(dx, duration);
		}
	}

	void horizontalScrollSingel() {
		int[] location = new int[2];
		getSelectedView().getLocationOnScreen(location);
		int left = location[0];
		int right = location[0] + getSelectedView().getWidth();
		int imgW = getSelectedView().getWidth();
		left = (int) (left + (1.0D - this.mPositionManager.getItemScaleXValue()) * imgW / 2.0D);
		right = (int) (left + imgW * this.mPositionManager.getItemScaleXValue());

		Log.d("FocusedGridView", "scroll left = " + location[0] + ", right = " + right);
		if ((right >= this.mScreenWidth) && (!this.mOutsieScroll)) {
			int dx = right - this.mScreenWidth + this.mViewRight;

			int duration = -dx * 100 / 300;

			horizontalSmoothScrollBy(dx, duration);

			return;
		}
		getLocationOnScreen(location);
		Log.d("FocusedGridView", "scroll conrtainer left = " + this.mStartX);
		if ((left < this.mStartX) && (!this.mOutsieScroll)) {
			int dx = left - this.mStartX;
			if (this.mScroller.getCurrX() > Math.abs(dx)) {
				dx = -this.mScroller.getCurrX();
			}

			int duration = -dx * 100 / 300;

			horizontalSmoothScrollBy(dx, duration);
		}
	}

	void horizontalOutsideScrollSingel() {
		int[] location = new int[2];
		getSelectedView().getLocationOnScreen(location);
		int left = location[0];
		int right = location[0] + getSelectedView().getWidth();
		int imgW = getSelectedView().getWidth();
		left = (int) (left + (1.0D - this.mPositionManager.getItemScaleXValue()) * imgW / 2.0D);
		right = (int) (left + imgW * this.mPositionManager.getItemScaleXValue());

		Log.d("FocusedGridView", "scroll left = " + location[0] + ", right = " + right);
		if ((right >= this.mScreenWidth) && (!this.mOutsieScroll)) {
			int dx = right - this.mScreenWidth + this.mViewRight;

			int duration = -dx * 100 / 300;

			this.mScrollerListener.horizontalSmoothScrollBy(dx, duration);

			return;
		}
		getLocationOnScreen(location);
		Log.d("FocusedGridView", "scroll conrtainer left = " + this.mStartX);
		if ((left < this.mStartX) && (!this.mOutsieScroll)) {
			int dx = left - this.mStartX;
			if (this.mScrollerListener.getCurrX(true) > Math.abs(dx)) {
				dx = -this.mScrollerListener.getCurrX(true);
			}

			int duration = -dx * 100 / 300;

			this.mScrollerListener.horizontalSmoothScrollBy(dx, duration);
		}
	}

	public void horizontalSmoothScrollTo(int fx, int duration) {
		int dx = fx - this.mScroller.getFinalX();
		smoothScrollBy(dx, duration);
	}

	public void horizontalSmoothScrollBy(int dx, int duration) {
		Log.w("FocusedGridView", "smoothScrollBy dx = " + dx);
		this.mScroller.startScroll(this.mScroller.getFinalX(), this.mScroller.getFinalY(), dx, this.mScroller.getFinalY(), duration);
		invalidate();
	}

	public void computeScroll() {
		if (this.mScroller.computeScrollOffset()) {
			scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
			Log.d("FocusedGridView", "computeScroll mScroller.getCurrX() = " + this.mScroller.getCurrX());
		}

		super.computeScroll();
	}

	public void setOnKeyDownListener(onKeyDownListener l) {
		this.onKeyDownListener = l;
	}

	public void setManualPadding(int left, int top, int right, int bottom) {
		this.mPositionManager.setManualPadding(left, top, right, bottom);
	}

	public void setFocusResId(int focusResId) {
		this.mPositionManager.setFocusResId(focusResId);
	}

	public void setFocusShadowResId(int focusResId) {
		this.mPositionManager.setFocusShadowResId(focusResId);
	}

	public void setFocusMode(int mode) {
		this.mPositionManager.setFocusMode(mode);
	}

	private boolean checkFocusPosition() {
		Rect dstRect = this.mPositionManager.getDstRectAfterScale(true);
		Log.i("FocusedGridView", "checkFocusPosition:" + this.mPositionManager.getCurrentRect() + "," + hasFocus() + "," + dstRect + "," + isShown());
		if ((this.mPositionManager.getCurrentRect() == null) || (!hasFocus()) || (dstRect == null) || (!isShown())) {
			return false;
		}

		if ((Math.abs(dstRect.left - this.mPositionManager.getCurrentRect().left) > 5) || (Math.abs(dstRect.right - this.mPositionManager.getCurrentRect().right) > 5) || (Math.abs(dstRect.top - this.mPositionManager.getCurrentRect().top) > 5)
				|| (Math.abs(dstRect.bottom - this.mPositionManager.getCurrentRect().bottom) > 5)) {
			return true;
		}

		return false;
	}

	public static abstract interface FocusItemSelectedListener {
		public abstract void onItemSelected(View paramView, int paramInt, boolean paramBoolean, AdapterView paramAdapterView);
	}

	class FocusedGridPositionManager extends FocusedBasePositionManager {
		public FocusedGridPositionManager(Context context, View container) {
			super(context, container);
		}

		public Rect getDstRectBeforeScale(boolean isBeforeScale) {
			View selectedView = getSelectedView();
			if (selectedView == null) {
				return null;
			}

			View imgView = selectedView.findViewById(FocusedGridView.this.mFocusViewId);
			Rect imgRect = new Rect();
			Rect gridViewRect = new Rect();
			Rect selectViewRect = new Rect();

			imgView.getGlobalVisibleRect(imgRect);
			FocusedGridView.this.getGlobalVisibleRect(gridViewRect);
			selectedView.getGlobalVisibleRect(selectViewRect);
			Log.i("FocusedBasePositionManager", "mLastFocusRect imgView:" + imgRect + "(" + imgRect.width() + "," + imgRect.height() + ")");
			Log.i("FocusedBasePositionManager", "mLastFocusRect gridViewRect:" + gridViewRect + "(" + gridViewRect.width() + "," + gridViewRect.height() + ")");
			Log.i("FocusedBasePositionManager", "mLastFocusRect selectViewRect:" + selectViewRect + "(" + selectViewRect.width() + "," + selectViewRect.height() + ")");

			if (isBeforeScale) {
				int hCenter = (selectViewRect.top + selectViewRect.bottom) / 2;
				int wCenter = (selectViewRect.left + selectViewRect.right) / 2;
				int imgUnscaleWidth = (int) (imgRect.width() / getItemScaleXValue());
				int imgUnscaleHeight = (int) (imgRect.height() / getItemScaleYValue());

				imgRect.left = ((int) (wCenter - imgRect.width() / getItemScaleXValue() / 2.0F));
				imgRect.right = (imgRect.left + imgUnscaleWidth);
				imgRect.top = ((int) (hCenter - (hCenter - imgRect.top) / getItemScaleYValue()));
				imgRect.bottom = (imgRect.top + imgUnscaleHeight);
			}

			int imgW = imgRect.right - imgRect.left;
			int imgH = imgRect.bottom - imgRect.top;
			imgRect.left = ((int) (imgRect.left + (1.0D - getItemScaleXValue()) * imgW / 2.0D));
			imgRect.top = ((int) (imgRect.top + (1.0D - getItemScaleYValue()) * ((selectViewRect.top + selectViewRect.bottom) / 2 - imgRect.top)));
			imgRect.right = ((int) (imgRect.left + imgW * getItemScaleXValue()));
			imgRect.bottom = ((int) (imgRect.top + imgH * getItemScaleYValue()));

			if ((2 == FocusedGridView.this.mHorizontalMode) || (1 == FocusedGridView.this.mHorizontalMode)) {
				imgRect.left += FocusedGridView.this.mScroller.getCurrX();
				imgRect.right += FocusedGridView.this.mScroller.getCurrX();
			} else if (((3 == FocusedGridView.this.mHorizontalMode) || (4 == FocusedGridView.this.mHorizontalMode)) && (FocusedGridView.this.mScrollerListener != null)) {
				imgRect.left += FocusedGridView.this.mScrollerListener.getCurrX(false);
				imgRect.right += FocusedGridView.this.mScrollerListener.getCurrX(false);
			}

			imgRect.left -= gridViewRect.left;
			imgRect.right -= gridViewRect.left;
			imgRect.top -= gridViewRect.top;
			imgRect.bottom -= gridViewRect.top;

			imgRect.top -= getSelectedPaddingTop();
			imgRect.left -= getSelectedPaddingLeft();
			imgRect.right += getSelectedPaddingRight();
			imgRect.bottom += getSelectedPaddingBottom();
			return imgRect;
		}

		public void drawChild(Canvas canvas) {
			Log.d("FocusedBasePositionManager", "drawChild");
			FocusedGridView.this.drawChild(canvas, getSelectedView(), FocusedGridView.this.getDrawingTime());
		}

		public Rect getDstRectAfterScale(boolean isShadow) {
			View selectedView = getSelectedView();
			if (selectedView == null) {
				return null;
			}

			View imgView = selectedView.findViewById(FocusedGridView.this.mFocusViewId);
			Rect imgRect = new Rect();
			Rect gridViewRect = new Rect();
			Rect selectViewRect = new Rect();

			imgView.getGlobalVisibleRect(imgRect);
			selectedView.getGlobalVisibleRect(selectViewRect);
			FocusedGridView.this.getGlobalVisibleRect(gridViewRect);

			imgRect.left -= gridViewRect.left;
			imgRect.right -= gridViewRect.left;
			imgRect.top -= gridViewRect.top;
			imgRect.bottom -= gridViewRect.top;

			if ((2 == FocusedGridView.this.mHorizontalMode) || (1 == FocusedGridView.this.mHorizontalMode)) {
				imgRect.left += FocusedGridView.this.mScroller.getCurrX();
				imgRect.right += FocusedGridView.this.mScroller.getCurrX();
			} else if (((3 == FocusedGridView.this.mHorizontalMode) || (4 == FocusedGridView.this.mHorizontalMode)) && (FocusedGridView.this.mScrollerListener != null)) {
				imgRect.left += FocusedGridView.this.mScrollerListener.getCurrX(false);
				imgRect.right += FocusedGridView.this.mScrollerListener.getCurrX(false);
			}

			if ((isShadow) && (isLastFrame())) {
				imgRect.top -= getSelectedShadowPaddingTop();
				imgRect.left -= getSelectedShadowPaddingLeft();
				imgRect.right += getSelectedShadowPaddingRight();
				imgRect.bottom += getSelectedShadowPaddingBottom();
			} else {
				imgRect.top -= getSelectedPaddingTop();
				imgRect.left -= getSelectedPaddingLeft();
				imgRect.right += getSelectedPaddingRight();
				imgRect.bottom += getSelectedPaddingBottom();
			}

			return imgRect;
		}
	}

	class FocusedScroller extends Scroller {
		public FocusedScroller(Context context, Interpolator interpolator, boolean flywheel) {
			super(context, interpolator, flywheel);
		}

		public FocusedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		public FocusedScroller(Context context) {
			super(context, new AccelerateDecelerateInterpolator());
		}

		public boolean computeScrollOffset() {
			boolean isFinished = isFinished();
			boolean needInvalidate = FocusedGridView.this.checkFocusPosition();
			if ((FocusedGridView.this.mOutsieScroll) || (!isFinished) || (needInvalidate)) {
				FocusedGridView.this.invalidate();
			}

			boolean hr = super.computeScrollOffset();
			Log.d("FocusedGridView", "FocusedScroller computeScrollOffset isFinished = " + isFinished + ", mOutsieScroll = " + FocusedGridView.this.mOutsieScroll + ", hr = " + hr);
			return hr;
		}
	}

	public static abstract interface ScrollerListener {
		public abstract void horizontalSmoothScrollBy(int paramInt1, int paramInt2);

		public abstract int getCurrX(boolean paramBoolean);

		public abstract int getFinalX(boolean paramBoolean);

		public abstract boolean isFinished();
	}

	public static abstract interface onKeyDownListener {
		public abstract boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent);
	}
}