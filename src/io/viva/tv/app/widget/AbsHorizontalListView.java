package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.view.ContextMenu;
import android.view.FocusFinder;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;
import android.widget.OverScroller;

public abstract class AbsHorizontalListView extends AbsSpinner implements GestureDetector.OnGestureListener {
	private static final String TAG = "AbsHorizontalListView";
	private static final boolean DEBUG = true;
	static final int TOUCH_MODE_REST = -1;
	static final int TOUCH_MODE_FLING = 4;
	private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;
	protected int mSpacing = 0;
	private int mAnimationDuration = 200;
	private float mUnselectedAlpha;
	protected int mLeftMost;
	protected int mRightMost;
	int mGravity;
	int mGravityHeightAnchor;
	private GestureDetector mGestureDetector;
	private int mDownTouchPosition;
	private View mDownTouchView;
	FlingRunnable mFlingRunnable = new FlingRunnable();
	private int mLastScrollState = 0;
	int mTouchMode = -1;
	private OnScrollListener mOnScrollListener;
	int mSelectedLeft;
	boolean gainFocus;
	Drawable mSelector;
	int mSelectorBorderWidth;
	int mSelectorBorderHeight;
	boolean unhandleFullVisible;
	private Runnable mDisableSuppressSelectionChangedRunnable = new Runnable() {
		public void run() {
			AbsHorizontalListView.this.mSuppressSelectionChanged = false;
			AbsHorizontalListView.this.selectionChanged();
		}
	};
	protected boolean mShouldStopFling;
	private View mSelectedChild;
	private boolean mShouldCallbackDuringFling = true;
	private boolean mShouldCallbackOnUnselectedItemClick = true;
	private boolean mSuppressSelectionChanged;
	protected boolean mReceivedInvokeKeyDown;
	private AdapterView.AdapterContextMenuInfo mContextMenuInfo;
	private boolean mIsFirstScroll;
	protected boolean mStackFromRight = false;
	private final ArrowScrollFocusResult mArrowScrollFocusResult = new ArrowScrollFocusResult();
	private final Rect mTempRect = new Rect();
	boolean mItemsCanFocus = true;
	boolean mDrawSelectorOnTop = false;
	Rect mExactlyUserSelectedRect;
	Runnable mPositionScrollAfterLayout;
	PositionScroller mPositionScroller;
	private boolean mAreAllItemsSelectable = true;
	private int mLastTouchMode = -1;
	private static final int TOUCH_MODE_UNKNOWN = -1;
	private static final int TOUCH_MODE_ON = 0;
	private static final int TOUCH_MODE_OFF = 1;
	int mSpecificLeft;
	private int mFirstPositionDistanceGuess;
	private int mLastPositionDistanceGuess;
	int mMotionPosition;
	int mMotionViewOriginalLeft;
	int mMotionViewNewLeft;
	private static final float MAX_SCROLL_FACTOR = 0.33F;
	static final int MIN_SCROLL_PREVIEW_PIXELS = 2;
	Interpolator mInterpolator = null;

	public AbsHorizontalListView(Context paramContext) {
		super(paramContext);
	}

	public AbsHorizontalListView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public AbsHorizontalListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		this.mGestureDetector = new GestureDetector(paramContext, this);
		this.mGestureDetector.setIsLongpressEnabled(true);
		this.needMeasureSelectedView = false;
	}

	public void setSelector(Drawable paramDrawable) {
		this.mSelector = paramDrawable;
	}

	public void setOnScrollListener(OnScrollListener paramOnScrollListener) {
		this.mOnScrollListener = paramOnScrollListener;
	}

	public void setSelectorBorderWidth(int paramInt) {
		this.mSelectorBorderWidth = paramInt;
	}

	public void setSelectorBorderHeight(int paramInt) {
		this.mSelectorBorderHeight = paramInt;
	}

	public void setCallbackDuringFling(boolean paramBoolean) {
		this.mShouldCallbackDuringFling = paramBoolean;
	}

	public void setCallbackOnUnselectedItemClick(boolean paramBoolean) {
		this.mShouldCallbackOnUnselectedItemClick = paramBoolean;
	}

	public void setAnimationDuration(int paramInt) {
		this.mAnimationDuration = paramInt;
	}

	public void setSpacing(int paramInt) {
		this.mSpacing = paramInt;
	}

	public void setUnselectedAlpha(float paramFloat) {
		this.mUnselectedAlpha = paramFloat;
	}

	protected int computeHorizontalScrollExtent() {
		return 1;
	}

	protected int computeHorizontalScrollOffset() {
		return this.mSelectedPosition;
	}

	protected int computeHorizontalScrollRange() {
		return this.mItemCount;
	}

	protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) {
		return paramLayoutParams instanceof AbsSpinner.LayoutParams;
	}

	protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) {
		return new AbsSpinner.LayoutParams(paramLayoutParams);
	}

	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) {
		return new AbsSpinner.LayoutParams(getContext(), paramAttributeSet);
	}

	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new AbsSpinner.LayoutParams(-2, -2, 0);
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
		this.mInLayout = true;
		layout(0, false);
		this.mInLayout = false;
	}

	int getChildHeight(View paramView) {
		return paramView.getMeasuredHeight();
	}

	void trackMotionScroll(int paramInt1, int paramInt2) {
		if (getChildCount() == 0)
			return;
		boolean bool = paramInt1 < 0;
		int i = paramInt1;
		if (i != paramInt1) {
			this.mFlingRunnable.endFling(false);
			onFinishedMovement();
		}
		offsetChildrenLeftAndRight(i);
		detachOffScreenChildren(bool);
		fillGap(bool);
		onScrollChanged(0, 0, 0, 0);
		invalidate();
	}

	abstract void fillGap(boolean paramBoolean);

	abstract int lookForSelectablePositionOnScreen(int paramInt);

	private int getCenterOfGallery() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	int getHeaderViewsCount() {
		return 0;
	}

	int getFooterViewsCount() {
		return 0;
	}

	int getLimitedMotionScrollAmount(boolean paramBoolean, int paramInt) {
		int i = paramBoolean != this.mStackFromRight ? this.mItemCount - 1 : 0;
		View localView = getChildAt(i - this.mFirstPosition);
		if (localView == null)
			return paramInt;
		int j = getEdgeOfView(localView, paramBoolean);
		int k = getEdgeOfGallery(paramBoolean);
		if (paramBoolean) {
			if (j <= k)
				return 0;
		} else if (j >= k)
			return 0;
		int m = k - j;
		return paramBoolean ? Math.max(m, paramInt) : Math.min(m, paramInt);
	}

	protected void offsetChildrenLeftAndRight(int paramInt) {
		int i = getChildCount();
		for (int j = i - 1; j >= 0; j--)
			getChildAt(j).offsetLeftAndRight(paramInt);
	}

	private static int getEdgeOfView(View paramView, boolean paramBoolean) {
		int i = paramBoolean ? paramView.getRight() : paramView.getLeft();
		return i;
	}

	private int getEdgeOfGallery(boolean paramBoolean) {
		int i = paramBoolean ? getWidth() : getPaddingLeft();
		return i;
	}

	private void detachOffScreenChildren(boolean paramBoolean) {
		int i = getChildCount();
		int j = this.mFirstPosition;
		int k = 0;
		int m = 0;
		int n;
		int i1;
		int i2;
		View localView;
		if (paramBoolean) {
			n = getPaddingLeft();
			for (i1 = 0; i1 < i; i1++) {
				i2 = this.mStackFromRight ? i - 1 - i1 : i1;
				localView = getChildAt(i2);
				if (localView.getRight() >= n)
					break;
				k = i2;
				m++;
				this.mRecycler.addScrapView(j + i2, localView);
			}
			if (!this.mStackFromRight)
				k = 0;
		} else {
			n = getWidth() - getPaddingRight();
			for (i1 = i - 1; i1 >= 0; i1--) {
				i2 = this.mStackFromRight ? i - 1 - i1 : i1;
				localView = getChildAt(i2);
				if (localView.getLeft() <= n)
					break;
				k = i2;
				m++;
				this.mRecycler.addScrapView(j + i2, localView);
			}
			if (this.mStackFromRight)
				k = 0;
		}
		detachViewsFromParent(k, m);
		if (paramBoolean != this.mStackFromRight)
			this.mFirstPosition += m;
	}

	private void scrollIntoSlots() {
	}

	void onFinishedMovement() {
		if (this.mSuppressSelectionChanged) {
			this.mSuppressSelectionChanged = false;
			super.selectionChanged();
		}
		invalidate();
	}

	void selectionChanged() {
		if (!this.mSuppressSelectionChanged)
			super.selectionChanged();
	}

	public void getFocusedRect(Rect paramRect) {
		View localView = getSelectedView();
		if ((localView != null) && (localView.getParent() == this)) {
			localView.getFocusedRect(paramRect);
			offsetDescendantRectToMyCoords(localView, paramRect);
		} else {
			super.getFocusedRect(paramRect);
		}
	}

	public int getActualX() {
		if (this.mFlingRunnable != null)
			return this.mFlingRunnable.getActualX();
		return 0;
	}

	int reconcileSelectedPosition() {
		int i = this.mSelectedPosition;
		if (i < 0)
			i = this.mResurrectToPosition;
		i = Math.max(0, i);
		i = Math.min(i, this.mItemCount - 1);
		return i;
	}

	boolean resurrectSelection() {
		int i = getChildCount();
		if (i <= 0)
			return false;
		int j = 0;
		int m = this.mSpinnerPadding.left;
		int n = getRight() - getTop() - this.mSpinnerPadding.right;
		int i1 = this.mFirstPosition;
		int i2 = this.mResurrectToPosition;
		boolean bool = true;
		int k = -1;
		if ((i2 >= i1) && (i2 < i1 + i)) {
			k = i2;
			View localView1 = getChildAt(k - this.mFirstPosition);
			j = localView1.getLeft();
			int i4 = localView1.getRight();
			if (j < m)
				j = m + getHorizontalFadingEdgeLength();
			else if (i4 > n)
				j = n - localView1.getMeasuredWidth() - getHorizontalFadingEdgeLength();
		} else {
			int i3;
			if (i2 < i1) {
				k = i1;
				for (i3 = 0; i3 < i; i3++) {
					View localView2 = getChildAt(i3);
					int i6 = localView2.getLeft();
					if (i3 == 0) {
						j = i6;
						if ((i1 > 0) || (i6 < m))
							m += getHorizontalFadingEdgeLength();
					}
					if (i6 >= m) {
						k = i1 + i3;
						j = i6;
						break;
					}
				}
			} else {
				i3 = this.mItemCount;
				bool = false;
				k = i1 + i - 1;
				for (int i5 = i - 1; i5 >= 0; i5--) {
					View localView3 = getChildAt(i5);
					int i7 = localView3.getLeft();
					int i8 = localView3.getRight();
					if (i5 == i - 1) {
						j = i7;
						if ((i1 + i < i3) || (i8 > n))
							n -= getHorizontalFadingEdgeLength();
					}
					if (i8 <= n) {
						k = i1 + i5;
						j = i7;
						break;
					}
				}
			}
		}
		this.mResurrectToPosition = -1;
		removeCallbacks(this.mFlingRunnable);
		if (this.mPositionScroller != null)
			this.mPositionScroller.stop();
		this.mSpecificLeft = j;
		k = lookForSelectablePosition(k, bool);
		if ((k >= i1) && (k <= getLastVisiblePosition())) {
			this.mLayoutMode = 4;
			updateSelectorState();
			setSelectionInt(k);
		}
		return k >= 0;
	}

	public void smoothScrollToPosition(int paramInt) {
		if (this.mPositionScroller == null)
			this.mPositionScroller = new PositionScroller();
		this.mPositionScroller.start(paramInt);
	}

	void setSelectionInt(int paramInt) {
		setNextSelectedPositionInt(paramInt);
		int i = 0;
		int j = this.mSelectedPosition;
		if (j >= 0)
			if (paramInt == j - 1)
				i = 1;
			else if (paramInt == j + 1)
				i = 1;
		if (this.mPositionScroller != null)
			this.mPositionScroller.stop();
		layout(0, false);
		if (i != 0)
			awakenScrollBars();
	}

	void updateSelectorState() {
		if (this.mSelector != null)
			if (shouldShowSelector())
				this.mSelector.setState(getDrawableState());
			else
				this.mSelector.setState(StateSet.NOTHING);
	}

	protected void adjustViewsRightOrLeft() {
		int i = getChildCount();
		if (i > 0) {
			View localView;
			int j;
			if (!this.mStackFromRight) {
				localView = getChildAt(0);
				j = localView.getLeft() - this.mSpinnerPadding.left;
				if (this.mFirstPosition != 0)
					j -= this.mDividerWidth;
				if (j < 0)
					j = 0;
			} else {
				localView = getChildAt(i - 1);
				j = localView.getRight() - (getWidth() - this.mSpinnerPadding.right);
				if (this.mFirstPosition + i < this.mItemCount)
					j += this.mDividerWidth;
				if (j > 0)
					j = 0;
			}
			if (j != 0)
				offsetChildrenLeftAndRight(-j);
		}
	}

	protected void drawSelector(Canvas paramCanvas) {
		if ((this.mSelector != null) && (this.mSelectorRect != null) && (!this.mSelectorRect.isEmpty())) {
			Rect localRect = new Rect(this.mExactlyUserSelectedRect != null ? this.mExactlyUserSelectedRect : this.mSelectorRect);
			this.mSelector.setBounds(localRect);
			this.mSelector.draw(paramCanvas);
		}
	}

	protected void resetItemLayout(View paramView) {
		this.mItemWidth = paramView.getWidth();
		this.mItemHeight = paramView.getHeight();
	}

	protected int getMaxWidth(View paramView, int paramInt) {
		return paramInt;
	}

	int getGravityHeightAnchor(View paramView) {
		int i = 0;
		switch (this.mGravity) {
		case 16:
			int j = paramView.getMeasuredHeight();
			i = this.mGravityHeightAnchor - (j >> 1);
		}
		return i;
	}

	View obtainView(int paramInt, boolean[] paramArrayOfBoolean) {
		paramArrayOfBoolean[0] = false;
		View localView1 = this.mRecycler.getScrapView(paramInt);
		View localView2;
		if (localView1 != null) {
			localView2 = this.mAdapter.getView(paramInt, localView1, this);
			if (localView2 != localView1)
				this.mRecycler.addScrapView(paramInt, localView1);
			else
				paramArrayOfBoolean[0] = true;
		} else {
			localView2 = this.mAdapter.getView(paramInt, null, this);
		}
		if (this.mAdapterHasStableIds) {
			ViewGroup.LayoutParams localLayoutParams = localView2.getLayoutParams();
			AbsSpinner.LayoutParams localLayoutParams1;
			if (localLayoutParams == null)
				localLayoutParams1 = (AbsSpinner.LayoutParams) generateDefaultLayoutParams();
			else if (!checkLayoutParams(localLayoutParams))
				localLayoutParams1 = (AbsSpinner.LayoutParams) generateLayoutParams(localLayoutParams);
			else
				localLayoutParams1 = (AbsSpinner.LayoutParams) localLayoutParams;
			localLayoutParams1.itemId = this.mAdapter.getItemId(paramInt);
			localView2.setLayoutParams(localLayoutParams1);
		}
		return localView2;
	}

	private int calculateTop(View paramView, boolean paramBoolean) {
		int i = paramBoolean ? getMeasuredHeight() : getHeight();
		int j = paramBoolean ? paramView.getMeasuredHeight() : paramView.getHeight();
		int k = 0;
		switch (this.mGravity) {
		case 48:
			k = this.mSpinnerPadding.top;
			break;
		case 16:
			int m = i - this.mSpinnerPadding.bottom - this.mSpinnerPadding.top - j;
			k = this.mSpinnerPadding.top + m / 2;
			break;
		case 80:
			k = i - this.mSpinnerPadding.bottom - j;
		}
		return k;
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		return super.onTouchEvent(paramMotionEvent);
	}

	public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
		if (this.mDownTouchPosition >= 0) {
			boolean bool = this.mDownTouchPosition > this.mSelectedPosition;
			scrollToChild(this.mDownTouchPosition - this.mFirstPosition, bool);
			if ((this.mShouldCallbackOnUnselectedItemClick) || (this.mDownTouchPosition == this.mSelectedPosition))
				performItemClick(this.mDownTouchView, this.mDownTouchPosition, this.mAdapter.getItemId(this.mDownTouchPosition));
			invalidate();
			return true;
		}
		return false;
	}

	public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		if (!this.mShouldCallbackDuringFling) {
			removeCallbacks(this.mDisableSuppressSelectionChangedRunnable);
			if (!this.mSuppressSelectionChanged)
				this.mSuppressSelectionChanged = true;
		}
		this.mFlingRunnable.startUsingVelocity((int) -paramFloat1);
		return true;
	}

	public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		getParent().requestDisallowInterceptTouchEvent(true);
		if (!this.mShouldCallbackDuringFling) {
			if (this.mIsFirstScroll) {
				if (!this.mSuppressSelectionChanged)
					this.mSuppressSelectionChanged = true;
				postDelayed(this.mDisableSuppressSelectionChangedRunnable, 250L);
			}
		} else if (this.mSuppressSelectionChanged)
			this.mSuppressSelectionChanged = false;
		trackMotionScroll(-1 * (int) paramFloat1, -1 * (int) paramFloat1);
		this.mIsFirstScroll = false;
		return true;
	}

	public boolean onDown(MotionEvent paramMotionEvent) {
		this.mFlingRunnable.stop(false);
		this.mDownTouchPosition = pointToPosition((int) paramMotionEvent.getX(), (int) paramMotionEvent.getY());
		if (this.mDownTouchPosition >= 0) {
			this.mDownTouchView = getChildAt(this.mDownTouchPosition - this.mFirstPosition);
			this.mDownTouchView.setPressed(true);
		}
		this.mIsFirstScroll = true;
		return true;
	}

	void onUp() {
		if (this.mFlingRunnable.mScroller.isFinished())
			scrollIntoSlots();
		dispatchUnpress();
	}

	void onCancel() {
		onUp();
	}

	public void onLongPress(MotionEvent paramMotionEvent) {
		if (this.mDownTouchPosition < 0)
			return;
		performHapticFeedback(0);
		long l = getItemIdAtPosition(this.mDownTouchPosition);
		dispatchLongPress(this.mDownTouchView, this.mDownTouchPosition, l);
	}

	public void onShowPress(MotionEvent paramMotionEvent) {
	}

	private void dispatchPress(View paramView) {
		if (paramView != null)
			paramView.setPressed(true);
		setPressed(true);
	}

	private void dispatchUnpress() {
		for (int i = getChildCount() - 1; i >= 0; i--)
			getChildAt(i).setPressed(false);
		setPressed(false);
	}

	public void dispatchSetSelected(boolean paramBoolean) {
	}

	protected void dispatchSetPressed(boolean paramBoolean) {
		if (this.mSelectedChild != null)
			this.mSelectedChild.setPressed(paramBoolean);
	}

	protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
		return this.mContextMenuInfo;
	}

	public boolean showContextMenuForChild(View paramView) {
		int i = getPositionForView(paramView);
		if (i < 0)
			return false;
		long l = this.mAdapter.getItemId(i);
		return dispatchLongPress(paramView, i, l);
	}

	public boolean showContextMenu() {
		if ((isPressed()) && (this.mSelectedPosition >= 0)) {
			int i = this.mSelectedPosition - this.mFirstPosition;
			View localView = getChildAt(i);
			return dispatchLongPress(localView, this.mSelectedPosition, this.mSelectedRowId);
		}
		return false;
	}

	private boolean dispatchLongPress(View paramView, int paramInt, long paramLong) {
		boolean bool = false;
		if (this.mOnItemLongClickListener != null)
			bool = this.mOnItemLongClickListener.onItemLongClick(this, this.mDownTouchView, this.mDownTouchPosition, paramLong);
		if (!bool) {
			this.mContextMenuInfo = new AdapterView.AdapterContextMenuInfo(paramView, paramInt, paramLong);
			bool = super.showContextMenuForChild(this);
		}
		if (bool)
			performHapticFeedback(0);
		return bool;
	}

	public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
		return paramKeyEvent.dispatch(this, null, null);
	}

	public void setSelectionFromTop(int paramInt1, int paramInt2) {
		if (this.mAdapter == null)
			return;
		if (!isInTouchMode()) {
			paramInt1 = lookForSelectablePosition(paramInt1, true);
			if (paramInt1 >= 0)
				setNextSelectedPositionInt(paramInt1);
		} else {
			this.mResurrectToPosition = paramInt1;
		}
		if (paramInt1 >= 0) {
			this.mLayoutMode = 4;
			this.mSpecificTop = (this.mSpinnerPadding.left + paramInt2);
			if (this.mNeedSync) {
				this.mSyncPosition = paramInt1;
				this.mSyncRowId = this.mAdapter.getItemId(paramInt1);
			}
			if (this.mPositionScroller != null)
				this.mPositionScroller.stop();
			requestLayout();
		}
	}

	int lookForSelectablePosition(int paramInt, boolean paramBoolean) {
		if ((this.mAdapter instanceof ListAdapter)) {
			ListAdapter localListAdapter = (ListAdapter) this.mAdapter;
			if ((localListAdapter == null) || (isInTouchMode()))
				return -1;
			int i = localListAdapter.getCount();
			if (!this.mAreAllItemsSelectable) {
				if (paramBoolean)
					for (paramInt = Math.max(0, paramInt); (paramInt < i) && (!localListAdapter.isEnabled(paramInt)); paramInt++)
						;
				for (paramInt = Math.min(paramInt, i - 1); (paramInt >= 0) && (!localListAdapter.isEnabled(paramInt)); paramInt--)
					;
				if ((paramInt < 0) || (paramInt >= i))
					return -1;
				return paramInt;
			}
			if ((paramInt < 0) || (paramInt >= i))
				return -1;
			return paramInt;
		}
		return paramInt;
	}

	public int getMaxScrollAmount() {
		return (int) (0.33F * (getRight() - getLeft()));
	}

	void checkSelectionChanged() {
		if ((this.mSelectedPosition != this.mOldSelectedPosition) || (this.mSelectedRowId != this.mOldSelectedRowId)) {
			onSelectionChanged(this.mOldSelectedPosition, this.mSelectedPosition, this.mOldSelectedRowId, this.mSelectedRowId);
			selectionChanged();
			if (this.mSelectedPosition != -1) {
				this.mOldSelectedPosition = this.mSelectedPosition;
				this.mOldSelectedRowId = this.mSelectedRowId;
			}
		}
	}

	void onSelectionChanged(int paramInt1, int paramInt2, long paramLong1, long paramLong2) {
	}

	public void setDrawSelectorOnTop(boolean paramBoolean) {
		this.mDrawSelectorOnTop = paramBoolean;
	}

	public void setExactlyUserSelectedRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		this.mExactlyUserSelectedRect = new Rect(paramInt1, paramInt2, paramInt3, paramInt4);
	}

	public void clearExactlyUserSelectedRect() {
		this.mExactlyUserSelectedRect = null;
	}

	void handleNewSelectionChange(View paramView, int paramInt1, int paramInt2, boolean paramBoolean) {
		if (paramInt2 == -1)
			throw new IllegalArgumentException("newSelectedPosition needs to be valid");
		int k = 0;
		int m = this.mSelectedPosition - this.mFirstPosition;
		int n = paramInt2 - this.mFirstPosition;
		int i;
		int j;
		View localView1;
		View localView2;
		if (paramInt1 == 17) {
			i = n;
			j = m;
			localView1 = getChildAt(i);
			localView2 = paramView;
			k = 1;
		} else {
			i = m;
			j = n;
			localView1 = paramView;
			localView2 = getChildAt(j);
		}
		int i1 = getChildCount();
		if (localView1 != null) {
			localView1.setSelected((!paramBoolean) && (k != 0));
			measureAndAdjustForward(localView1, i, i1);
		}
		if (localView2 != null) {
			localView2.setSelected((!paramBoolean) && (k == 0));
			measureAndAdjustForward(localView2, j, i1);
		}
	}

	void measureAndAdjustForward(View paramView, int paramInt1, int paramInt2) {
		int i = paramView.getWidth();
		measureItem(paramView);
		if (paramView.getMeasuredWidth() != i) {
			relayoutMeasuredItem(paramView);
			int j = paramView.getMeasuredWidth() - i;
			for (int k = paramInt1 + 1; k < paramInt2; k++)
				getChildAt(k).offsetLeftAndRight(j);
		}
	}

	private void measureItem(View paramView) {
		ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
		if (localLayoutParams == null)
			localLayoutParams = new ViewGroup.LayoutParams(-2, -1);
		int i = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, localLayoutParams.height);
		int j = localLayoutParams.width;
		int k;
		if (j > 0)
			k = View.MeasureSpec.makeMeasureSpec(j, 1073741824);
		else
			k = View.MeasureSpec.makeMeasureSpec(0, 0);
		paramView.measure(k, i);
	}

	private void relayoutMeasuredItem(View paramView) {
		int i = paramView.getMeasuredWidth();
		int j = paramView.getMeasuredHeight();
		int k = this.mSpinnerPadding.left;
		int m = k + i;
		int n = paramView.getTop();
		int i1 = n + j;
		paramView.layout(k, n, m, i1);
	}

	protected int getListRight() {
		int i = (getGroupFlags() & 0x22) == 34 ? 1 : 0;
		return i != 0 ? getWidth() - this.mSpinnerPadding.right : getUnClipToPaddingRightEdge();
	}

	int getUnClipToPaddingRightEdge() {
		return getWidth() - this.mSpinnerPadding.right;
	}

	protected int getListLeft() {
		int i = (getGroupFlags() & 0x22) == 34 ? 1 : 0;
		return i != 0 ? this.mSpinnerPadding.left : getUnClipToPaddingLeftEdge();
	}

	int getUnClipToPaddingLeftEdge() {
		return this.mSpinnerPadding.left;
	}

	int distanceToView(View paramView) {
		int i = 0;
		paramView.getDrawingRect(this.mTempRect);
		offsetDescendantRectToMyCoords(paramView, this.mTempRect);
		int j = getBottom() - getTop() - this.mSpinnerPadding.bottom;
		if (this.mTempRect.bottom < this.mSpinnerPadding.top)
			i = this.mSpinnerPadding.top - this.mTempRect.bottom;
		else if (this.mTempRect.top > j)
			i = this.mTempRect.top - j;
		return i;
	}

	ArrowScrollFocusResult arrowScrollFocused(int paramInt) {
		View localView1 = getSelectedView();
		View localView2;
		int i;
		int j;
		int k;
		if ((localView1 != null) && (localView1.hasFocus())) {
			View localView3 = localView1.findFocus();
			localView2 = FocusFinder.getInstance().findNextFocus(this, localView3, paramInt);
		} else {
			if (paramInt == 66) {
				i = this.mFirstPosition > 0 ? 1 : 0;
				j = this.mSpinnerPadding.left + (i != 0 ? getArrowScrollPreviewLength() : 0);
				k = (localView1 != null) && (localView1.getLeft() > j) ? localView1.getLeft() : j;
				this.mTempRect.set(k, 0, k, 0);
			} else {
				i = this.mFirstPosition + getChildCount() - 1 < this.mItemCount ? 1 : 0;
				j = getHeight() - this.mSpinnerPadding.right - (i != 0 ? getArrowScrollPreviewLength() : 0);
				k = (localView1 != null) && (localView1.getRight() < j) ? localView1.getBottom() : j;
				this.mTempRect.set(k, 0, k, k);
			}
			localView2 = FocusFinder.getInstance().findNextFocusFromRect(this, this.mTempRect, paramInt);
		}
		if (localView2 != null) {
			i = positionOfNewFocus(localView2);
			if ((this.mSelectedPosition != -1) && (i != this.mSelectedPosition)) {
				j = lookForSelectablePositionOnScreen(paramInt);
				if ((j != -1) && (((paramInt == 66) && (j < i)) || ((paramInt == 17) && (j > i))))
					return null;
			}
			j = amountToScrollToNewFocus(paramInt, localView2, i);
			k = getMaxScrollAmount();
			if (j < k) {
				localView2.requestFocus(paramInt);
				this.mArrowScrollFocusResult.populate(i, j);
				return this.mArrowScrollFocusResult;
			}
			if (distanceToView(localView2) < k) {
				localView2.requestFocus(paramInt);
				this.mArrowScrollFocusResult.populate(i, k);
				return this.mArrowScrollFocusResult;
			}
		}
		return null;
	}

	private int amountToScrollToNewFocus(int paramInt1, View paramView, int paramInt2) {
		int i = 0;
		paramView.getDrawingRect(this.mTempRect);
		offsetDescendantRectToMyCoords(paramView, this.mTempRect);
		if (paramInt1 == 17) {
			if (this.mTempRect.left < this.mSpinnerPadding.left) {
				i = this.mSpinnerPadding.left - this.mTempRect.left;
				if (paramInt2 > 0)
					i += getArrowScrollPreviewLength();
			}
		} else {
			int j = getWidth() - this.mSpinnerPadding.right;
			if (this.mTempRect.right > j) {
				i = this.mTempRect.right - j;
				if (paramInt2 < this.mItemCount - 1)
					i += getArrowScrollPreviewLength();
			}
		}
		return i;
	}

	public void setStackFromRight(boolean paramBoolean) {
	}

	public boolean isStackFromRight() {
		return this.mStackFromRight;
	}

	private int positionOfNewFocus(View paramView) {
		int i = getChildCount();
		for (int j = 0; j < i; j++) {
			View localView = getChildAt(j);
			if (isViewAncestorOf(paramView, localView))
				return this.mFirstPosition + j;
		}
		throw new IllegalArgumentException("newFocus is not a child of any of the children of the list!");
	}

	boolean isViewAncestorOf(View paramView1, View paramView2) {
		if (paramView1 == paramView2)
			return true;
		ViewParent localViewParent = paramView1.getParent();
		return ((localViewParent instanceof ViewGroup)) && (isViewAncestorOf((View) localViewParent, paramView2));
	}

	int getArrowScrollPreviewLength() {
		return Math.max(2, getHorizontalFadingEdgeLength());
	}

	public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
		if ((this.mAdapter == null) || (!this.mIsAttached))
			return false;
		switch (paramInt) {
		case 23:
		case 66:
			if ((this.mReceivedInvokeKeyDown) && (this.mItemCount > 0)) {
				dispatchPress(this.mSelectedChild);
				postDelayed(new Runnable() {
					public void run() {
						AbsHorizontalListView.this.dispatchUnpress();
					}
				}, ViewConfiguration.getPressedStateDuration());
				int i = this.mSelectedPosition - this.mFirstPosition;
				if (this.mSelectedPosition != -1)
					performItemClick(getChildAt(i), this.mSelectedPosition, this.mAdapter.getItemId(this.mSelectedPosition));
			}
			this.mReceivedInvokeKeyDown = false;
			return true;
		}
		return super.onKeyUp(paramInt, paramKeyEvent);
	}

	private boolean scrollToChild(int paramInt, boolean paramBoolean) {
		View localView = getChildAt(paramInt);
		int i = 0;
		int j = getEdgeOfGallery(paramBoolean);
		int k = paramInt + this.mFirstPosition;
		int m = 0;
		if (localView == null)
			return false;
		if (localView != null) {
			int n = getEdgeOfView(localView, paramBoolean);
			if (paramBoolean)
				i = n > j ? -(n - j + this.mSpacing) : 0;
			else
				i = n < j ? j - n + this.mSpacing : 0;
			makeNextPosition(k);
			if (m != 0)
				this.mFirstPosition = (paramBoolean ? this.mFirstPosition : k);
			if (Math.abs(i) > 0)
				this.mFlingRunnable.startUsingDistance(i);
			return true;
		}
		return false;
	}

	private void makeNextPosition(int paramInt) {
		setSelectedPositionInt(paramInt);
		setNextSelectedPositionInt(paramInt);
		checkSelectionChanged();
	}

	void setSelectedPositionInt(int paramInt) {
		super.setSelectedPositionInt(paramInt);
		setNextSelectedPositionInt(paramInt);
	}

	private void updateSelectedItemMetadata(int paramInt) {
		View localView1 = this.mSelectedChild;
		int i = paramInt - this.mFirstPosition > 0 ? paramInt - this.mFirstPosition : 0;
		View localView2 = this.mSelectedChild = getChildAt(i);
		if (localView2 == null)
			return;
		localView2.setSelected(true);
		localView2.setFocusable(true);
		if (hasFocus())
			localView2.requestFocus();
		if ((localView1 != null) && (localView1 != localView2)) {
			localView1.setSelected(false);
			localView1.setFocusable(false);
		}
	}

	public void setGravity(int paramInt) {
		if (this.mGravity != paramInt) {
			this.mGravity = paramInt;
			requestLayout();
		}
	}

	protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
		super.onFocusChanged(paramBoolean, paramInt, paramRect);
		this.gainFocus = paramBoolean;
		handlerFocusChanged(paramBoolean);
	}

	protected void handlerFocusChanged(boolean paramBoolean) {
		if (paramBoolean) {
			if (this.mSelectedPosition == -1)
				this.mSelectedPosition = 0;
			positionSelector(this.mSelectedPosition, getSelectedView());
		}
	}

	public void smoothScrollBy(int paramInt1, int paramInt2) {
		smoothScrollBy(paramInt1, paramInt2, false);
	}

	void smoothScrollBy(int paramInt1, int paramInt2, boolean paramBoolean) {
		if (this.mFlingRunnable == null)
			this.mFlingRunnable = new FlingRunnable();
		int i = this.mFirstPosition;
		int j = getChildCount();
		int k = i + j;
		int m = this.mSpinnerPadding.left;
		int n = getWidth() - this.mSpinnerPadding.right;
		if ((paramInt1 == 0) || (this.mItemCount == 0) || (j == 0) || ((i == 0) && (getChildAt(0).getLeft() == m) && (paramInt1 < 0))
				|| ((k == this.mItemCount) && (getChildAt(j - 1).getRight() == n) && (paramInt1 > 0))) {
			this.mFlingRunnable.endFling(false);
			if (this.mPositionScroller != null)
				this.mPositionScroller.stop();
		} else {
			reportScrollStateChange(2);
			this.mFlingRunnable.startScroll(paramInt1, paramInt2);
		}
	}

	void reportScrollStateChange(int paramInt) {
		if ((paramInt != this.mLastScrollState) && (this.mOnScrollListener != null)) {
			this.mLastScrollState = paramInt;
			this.mOnScrollListener.onScrollStateChanged(this, paramInt);
		}
	}

	public void postOnAnimation(Runnable paramRunnable) {
		post(paramRunnable);
	}

	protected OverScroller getOverScrollerFromFlingRunnable() {
		if (this.mFlingRunnable != null)
			return this.mFlingRunnable.mScroller;
		return null;
	}

	public void setFlingInterpolator(Interpolator paramInterpolator) {
		this.mInterpolator = paramInterpolator;
	}

	public static abstract interface OnScrollListener {
		public static final int SCROLL_STATE_IDLE = 0;
		public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
		public static final int SCROLL_STATE_FLING = 2;

		public abstract void onScrollStateChanged(AbsHorizontalListView paramAbsHorizontalListView, int paramInt);

		public abstract void onScroll(AbsHorizontalListView paramAbsHorizontalListView, int paramInt1, int paramInt2, int paramInt3);
	}

	class FlingRunnable implements Runnable {
		OverScroller mScroller = new OverScroller(AbsHorizontalListView.this.getContext(), AbsHorizontalListView.this.mInterpolator);
		private int mLastFlingX;
		private int mActualX;

		public FlingRunnable() {
		}

		public int getActualX() {
			return this.mActualX;
		}

		private void startCommon() {
			AbsHorizontalListView.this.removeCallbacks(this);
		}

		public void startUsingVelocity(int paramInt) {
			if (paramInt == 0)
				return;
			startCommon();
			int i = paramInt < 0 ? 2147483647 : 0;
			this.mLastFlingX = i;
			this.mScroller.fling(i, 0, paramInt, 0, 0, 2147483647, 0, 2147483647);
			AbsHorizontalListView.this.post(this);
		}

		public void startScroll(int paramInt1, int paramInt2) {
			if (paramInt1 == 0)
				return;
			startCommon();
			int i = paramInt1 < 0 ? 2147483647 : 0;
			this.mLastFlingX = i;
			this.mScroller.startScroll(i, 0, paramInt1, 0, paramInt2);
			AbsHorizontalListView.this.mTouchMode = 4;
			AbsHorizontalListView.this.post(this);
		}

		public void startUsingDistance(int paramInt) {
			if (paramInt == 0)
				return;
			startCommon();
			this.mLastFlingX = 0;
			this.mScroller.startScroll(0, 0, -paramInt, 0, AbsHorizontalListView.this.mAnimationDuration);
			AbsHorizontalListView.this.post(this);
		}

		public void stop(boolean paramBoolean) {
			AbsHorizontalListView.this.removeCallbacks(this);
			endFling(paramBoolean);
		}

		public void endFling(boolean paramBoolean) {
			AbsHorizontalListView.this.mTouchMode = -1;
			AbsHorizontalListView.this.removeCallbacks(this);
			this.mScroller.abortAnimation();
			Log.w(TAG, "endFling");
			AbsHorizontalListView.this.reportScrollStateChange(0);
			if (paramBoolean)
				AbsHorizontalListView.this.scrollIntoSlots();
		}

		public void run() {
			switch (AbsHorizontalListView.this.mTouchMode) {
			default:
				endFling(true);
				return;
			case 4:
			}
			if (AbsHorizontalListView.this.mItemCount == 0) {
				endFling(true);
				return;
			}
			AbsHorizontalListView.this.mShouldStopFling = false;
			OverScroller localOverScroller = this.mScroller;
			boolean bool = localOverScroller.computeScrollOffset();
			int i = localOverScroller.getCurrX();
			Log.d(TAG, "x = " + i + ", isFinished = " + this.mScroller.isFinished());
			this.mActualX = i;
			int j = this.mLastFlingX - i;
			if (j > 0) {
				AbsHorizontalListView.this.mDownTouchPosition = (AbsHorizontalListView.this.mStackFromRight ? AbsHorizontalListView.this.mFirstPosition
						+ AbsHorizontalListView.this.getChildCount() - 1 : AbsHorizontalListView.this.mFirstPosition);
				j = Math.min(AbsHorizontalListView.this.getWidth() - AbsHorizontalListView.this.getPaddingLeft() - AbsHorizontalListView.this.getPaddingRight() - 1, j);
			} else {
				int k = AbsHorizontalListView.this.getChildCount() - 1;
				AbsHorizontalListView.this.mDownTouchPosition = (AbsHorizontalListView.this.mStackFromRight ? AbsHorizontalListView.this.mFirstPosition
						: AbsHorizontalListView.this.mFirstPosition + AbsHorizontalListView.this.getChildCount() - 1);
				j = Math.max(-(AbsHorizontalListView.this.getWidth() - AbsHorizontalListView.this.getPaddingRight() - AbsHorizontalListView.this.getPaddingLeft() - 1), j);
			}
			AbsHorizontalListView.this.trackMotionScroll(j, j);
			if ((bool) && (!AbsHorizontalListView.this.mShouldStopFling)) {
				this.mLastFlingX = i;
				AbsHorizontalListView.this.post(this);
			} else {
				endFling(true);
				if (AbsHorizontalListView.this.getSelectedView() != null) {
					AbsHorizontalListView.this.positionSelector(AbsHorizontalListView.this.mSelectedPosition, AbsHorizontalListView.this.getSelectedView());
					AbsHorizontalListView.this.mSelectedLeft = AbsHorizontalListView.this.getSelectedView().getLeft();
				}
			}
		}
	}

	class PositionScroller implements Runnable {
		private static final int SCROLL_DURATION = 200;
		private static final int MOVE_RIGHT_POS = 1;
		private static final int MOVE_LEFT_POS = 2;
		private static final int MOVE_RIGHT_BOUND = 3;
		private static final int MOVE_LEFT_BOUND = 4;
		private static final int MOVE_OFFSET = 5;
		private int mMode;
		private int mTargetPos;
		private int mBoundPos;
		private int mLastSeenPos;
		private int mScrollDuration;
		private final int mExtraScroll = ViewConfiguration.get(AbsHorizontalListView.this.getContext()).getScaledFadingEdgeLength();
		private int mOffsetFromLeft;

		PositionScroller() {
		}

		void start(final int paramInt) {
			stop();
			if (AbsHorizontalListView.this.mDataChanged) {
				AbsHorizontalListView.this.mPositionScrollAfterLayout = new Runnable() {
					public void run() {
						AbsHorizontalListView.PositionScroller.this.start(paramInt);
					}
				};
				return;
			}
			int i = AbsHorizontalListView.this.getChildCount();
			if (i == 0)
				return;
			int j = AbsHorizontalListView.this.mFirstPosition;
			int k = j + i - 1;
			int n = Math.max(0, Math.min(AbsHorizontalListView.this.getCount() - 1, paramInt));
			int m;
			if (n < j) {
				m = j - n + 1;
				this.mMode = 2;
			} else if (n > k) {
				m = n - k + 1;
				this.mMode = 1;
			} else {
				scrollToVisible(n, -1, 200);
				return;
			}
			if (m > 0)
				this.mScrollDuration = (200 / m);
			else
				this.mScrollDuration = 200;
			this.mTargetPos = n;
			this.mBoundPos = -1;
			this.mLastSeenPos = -1;
			AbsHorizontalListView.this.postOnAnimation(this);
		}

		void start(final int paramInt1, final int paramInt2) {
			stop();
			if (paramInt2 == -1) {
				start(paramInt1);
				return;
			}
			if (AbsHorizontalListView.this.mDataChanged) {
				AbsHorizontalListView.this.mPositionScrollAfterLayout = new Runnable() {
					public void run() {
						AbsHorizontalListView.PositionScroller.this.start(paramInt1, paramInt2);
					}
				};
				return;
			}
			int i = AbsHorizontalListView.this.getChildCount();
			if (i == 0)
				return;
			int j = AbsHorizontalListView.this.mFirstPosition;
			int k = j + i - 1;
			int n = Math.max(0, Math.min(AbsHorizontalListView.this.getCount() - 1, paramInt1));
			int i1;
			int i2;
			int i3;
			int m;
			if (n < j) {
				i1 = k - paramInt2;
				if (i1 < 1)
					return;
				i2 = j - n + 1;
				i3 = i1 - 1;
				if (i3 < i2) {
					m = i3;
					this.mMode = 4;
				} else {
					m = i2;
					this.mMode = 2;
				}
			} else if (n > k) {
				i1 = paramInt2 - j;
				if (i1 < 1)
					return;
				i2 = n - k + 1;
				i3 = i1 - 1;
				if (i3 < i2) {
					m = i3;
					this.mMode = 3;
				} else {
					m = i2;
					this.mMode = 1;
				}
			} else {
				scrollToVisible(n, paramInt2, 200);
				return;
			}
			if (m > 0)
				this.mScrollDuration = (200 / m);
			else
				this.mScrollDuration = 200;
			this.mTargetPos = n;
			this.mBoundPos = paramInt2;
			this.mLastSeenPos = -1;
			AbsHorizontalListView.this.postOnAnimation(this);
		}

		void startWithOffset(int paramInt1, int paramInt2) {
			startWithOffset(paramInt1, paramInt2, 200);
		}

		void startWithOffset(final int paramInt1, final int paramInt2, final int paramInt3) {
			stop();
			if (AbsHorizontalListView.this.mDataChanged) {
				AbsHorizontalListView.this.mPositionScrollAfterLayout = new Runnable() {
					public void run() {
						AbsHorizontalListView.PositionScroller.this.startWithOffset(paramInt1, paramInt2, paramInt3);
					}
				};
				return;
			}
			final int i = AbsHorizontalListView.this.getChildCount();
			if (i == 0) {
				return;
			}
			this.mTargetPos = Math.max(0, Math.min(AbsHorizontalListView.this.getCount() - 1, paramInt1));
			this.mOffsetFromLeft = paramInt2 + AbsHorizontalListView.this.getPaddingTop();
			this.mBoundPos = -1;
			this.mLastSeenPos = -1;
			this.mMode = 5;
			int j = AbsHorizontalListView.this.mFirstPosition;
			int k = j + i - 1;
			int m;
			if (this.mTargetPos < j) {
				m = j - this.mTargetPos;
			} else if (this.mTargetPos > k) {
				m = this.mTargetPos - k;
			} else {
				int n = AbsHorizontalListView.this.getChildAt(this.mTargetPos - j).getLeft();
				AbsHorizontalListView.this.smoothScrollBy(n - paramInt2, paramInt3, true);
				return;
			}
			float f = m / i;
			this.mScrollDuration = (f < 1.0F ? paramInt3 : (int) (paramInt3 / f));
			this.mLastSeenPos = -1;
			AbsHorizontalListView.this.postOnAnimation(this);
		}

		void scrollToVisible(int paramInt1, int paramInt2, int paramInt3) {
			int i = AbsHorizontalListView.this.mFirstPosition;
			int j = AbsHorizontalListView.this.getChildCount();
			int k = i + j - 1;
			int m = AbsHorizontalListView.this.mSpinnerPadding.left;
			int n = AbsHorizontalListView.this.getWidth() - AbsHorizontalListView.this.mSpinnerPadding.right;
			if ((paramInt2 < i) || (paramInt2 > k))
				paramInt2 = -1;
			View localView1 = AbsHorizontalListView.this.getChildAt(paramInt1 - i);
			int i1 = localView1.getLeft();
			int i2 = localView1.getRight();
			int i3 = 0;
			if (i2 > n)
				i3 = i2 - n;
			if (i1 < m)
				i3 = i1 - m;
			if (i3 == 0)
				return;
			if (paramInt2 >= 0) {
				View localView2 = AbsHorizontalListView.this.getChildAt(paramInt2 - i);
				int i4 = localView2.getLeft();
				int i5 = localView2.getRight();
				int i6 = Math.abs(i3);
				if ((i3 < 0) && (i5 + i6 > n))
					i3 = Math.max(0, i5 - n);
				else if ((i3 > 0) && (i4 - i6 < m))
					i3 = Math.min(0, i4 - m);
			}
			AbsHorizontalListView.this.smoothScrollBy(i3, paramInt3);
		}

		void stop() {
			AbsHorizontalListView.this.removeCallbacks(this);
		}

		public void run() {
			int i = AbsHorizontalListView.this.getWidth();
			int j = AbsHorizontalListView.this.mFirstPosition;
			int n;
			int i5;
			int i6;
			int i7;
			int i8;
			int i1;
			int m;
			int i4;
			int i9;
			switch (this.mMode) {
			case 1:
				int k = AbsHorizontalListView.this.getChildCount() - 1;
				n = j + k;
				if (k < 0)
					return;
				if (n == this.mLastSeenPos) {
					AbsHorizontalListView.this.postOnAnimation(this);
					return;
				}
				View localView2 = AbsHorizontalListView.this.getChildAt(k);
				int i3 = localView2.getWidth();
				i5 = localView2.getLeft();
				i6 = i - i5;
				i7 = n < AbsHorizontalListView.this.mItemCount - 1 ? Math.max(AbsHorizontalListView.this.mSpinnerPadding.bottom, this.mExtraScroll)
						: AbsHorizontalListView.this.mSpinnerPadding.bottom;
				i8 = i3 - i6 + i7;
				AbsHorizontalListView.this.smoothScrollBy(-i8, this.mScrollDuration, true);
				this.mLastSeenPos = n;
				if (n < this.mTargetPos)
					AbsHorizontalListView.this.postOnAnimation(this);
				break;
			case 3:
				n = AbsHorizontalListView.this.getChildCount();
				if ((j == this.mBoundPos) || (n <= 1) || (j + n >= AbsHorizontalListView.this.mItemCount))
					return;
				i1 = j + 1;
				if (i1 == this.mLastSeenPos) {
					AbsHorizontalListView.this.postOnAnimation(this);
					return;
				}
				View localView4 = AbsHorizontalListView.this.getChildAt(1);
				i5 = localView4.getWidth();
				i6 = localView4.getLeft();
				i7 = Math.max(AbsHorizontalListView.this.mSpinnerPadding.right, this.mExtraScroll);
				if (i1 < this.mBoundPos) {
					AbsHorizontalListView.this.smoothScrollBy(Math.max(0, i5 + i6 - i7), this.mScrollDuration, true);
					this.mLastSeenPos = i1;
					AbsHorizontalListView.this.postOnAnimation(this);
				} else if (i6 > i7) {
					AbsHorizontalListView.this.smoothScrollBy(i6 - i7, this.mScrollDuration, true);
				}
				break;
			case 2:
				if (j == this.mLastSeenPos) {
					AbsHorizontalListView.this.postOnAnimation(this);
					return;
				}
				View localView1 = AbsHorizontalListView.this.getChildAt(0);
				if (localView1 == null)
					return;
				n = localView1.getLeft();
				i1 = j > 0 ? Math.max(this.mExtraScroll, AbsHorizontalListView.this.mSpinnerPadding.left) : AbsHorizontalListView.this.mSpinnerPadding.left;
				AbsHorizontalListView.this.smoothScrollBy(n - i1, this.mScrollDuration, true);
				this.mLastSeenPos = j;
				if (j > this.mTargetPos)
					AbsHorizontalListView.this.postOnAnimation(this);
				break;
			case 4:
				m = AbsHorizontalListView.this.getChildCount() - 2;
				if (m < 0)
					return;
				n = j + m;
				if (n == this.mLastSeenPos) {
					AbsHorizontalListView.this.postOnAnimation(this);
					return;
				}
				View localView3 = AbsHorizontalListView.this.getChildAt(m);
				i4 = localView3.getWidth();
				i5 = localView3.getLeft();
				i6 = i - i5;
				i7 = Math.max(AbsHorizontalListView.this.mSpinnerPadding.left, this.mExtraScroll);
				this.mLastSeenPos = n;
				if (n > this.mBoundPos) {
					AbsHorizontalListView.this.smoothScrollBy(-(i6 - i7), this.mScrollDuration, true);
					AbsHorizontalListView.this.postOnAnimation(this);
				} else {
					i8 = i - i7;
					i9 = i5 + i4;
					if (i8 > i9)
						AbsHorizontalListView.this.smoothScrollBy(-(i8 - i9), this.mScrollDuration, true);
				}
				break;
			case 5:
				if (this.mLastSeenPos == j) {
					AbsHorizontalListView.this.postOnAnimation(this);
					return;
				}
				this.mLastSeenPos = j;
				m = AbsHorizontalListView.this.getChildCount();
				n = this.mTargetPos;
				int i2 = j + m - 1;
				i4 = 0;
				if (n < j)
					i4 = j - n + 1;
				else if (n > i2)
					i4 = n - i2;
				float f1 = i4 / m;
				float f2 = Math.min(Math.abs(f1), 1.0F);
				if (n < j) {
					i7 = (int) (-AbsHorizontalListView.this.getWidth() * f2);
					i8 = (int) (this.mScrollDuration * f2);
					AbsHorizontalListView.this.smoothScrollBy(i7, i8, true);
					AbsHorizontalListView.this.postOnAnimation(this);
				} else if (n > i2) {
					i7 = (int) (AbsHorizontalListView.this.getWidth() * f2);
					i8 = (int) (this.mScrollDuration * f2);
					AbsHorizontalListView.this.smoothScrollBy(i7, i8, true);
					AbsHorizontalListView.this.postOnAnimation(this);
				} else {
					i7 = AbsHorizontalListView.this.getChildAt(n - j).getLeft();
					i8 = i7 - this.mOffsetFromLeft;
					i9 = (int) (this.mScrollDuration * (Math.abs(i8) / AbsHorizontalListView.this.getWidth()));
					AbsHorizontalListView.this.smoothScrollBy(i8, i9, true);
				}
				break;
			}
		}
	}

	static class ArrowScrollFocusResult {
		private int mSelectedPosition;
		private int mAmountToScroll;

		void populate(int paramInt1, int paramInt2) {
			this.mSelectedPosition = paramInt1;
			this.mAmountToScroll = paramInt2;
		}

		public int getSelectedPosition() {
			return this.mSelectedPosition;
		}

		public int getAmountToScroll() {
			return this.mAmountToScroll;
		}
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.app.widget.AbsHorizontalListView JD-Core Version: 0.6.2
 */