package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Transformation;
import android.widget.Scroller;

public class Gallery extends AbsSpinner implements GestureDetector.OnGestureListener {
	private static final String TAG = "Gallery";
	private static final boolean DEBUG = false;
	private static final boolean localLOGV = false;
	private static final boolean localLOGD = false;
	private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;
	int mSpacing = -20;
	private int mAnimationDuration = 400;
	private float mUnselectedAlpha;
	private int mLeftMost;
	private int mRightMost;
	private int mGravity;
	private GestureDetector mGestureDetector;
	private int mDownTouchPosition;
	private View mDownTouchView;
	boolean gainFocus;
	private FlingRunnable mFlingRunnable = new FlingRunnable();
	private AutoScrollRunnable mAutoScrollRunnable = new AutoScrollRunnable();
	private Runnable mDisableSuppressSelectionChangedRunnable = new Runnable() {
		public void run() {
			Gallery.this.mSuppressSelectionChanged = false;
			Gallery.this.selectionChanged();
		}
	};
	private boolean mShouldStopFling;
	private View mSelectedChild;
	private boolean mShouldCallbackDuringFling = true;
	private boolean mShouldCallbackOnUnselectedItemClick = true;
	private boolean mSuppressSelectionChanged;
	private boolean mReceivedInvokeKeyDown;
	private AdapterView.AdapterContextMenuInfo mContextMenuInfo;
	private boolean mIsFirstScroll;
	private boolean mIsRtl = false;
	private boolean mAutoScroll;
	private boolean mAutoPaused;
	private int mAutoScrollDelay = 5000;
	private OnScrollingListener mOnScrollingListener;
	private boolean isScrolling;
	protected int scrollPosition = -1;

	public Gallery(Context paramContext) {
		super(paramContext, null);
	}

	public Gallery(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public Gallery(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		this.mGestureDetector = new GestureDetector(paramContext, this);
		this.mGestureDetector.setIsLongpressEnabled(true);
		setGravity(16);
		setUnselectedAlpha(0.85F);
		int i = getGroupFlags();
		i |= 1024;
		i |= 2048;
		setGroupFlags(i);
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

	protected boolean getChildStaticTransformation(View paramView, Transformation paramTransformation) {
		paramTransformation.clear();
		paramTransformation.setAlpha(paramView == this.mSelectedChild ? 1.0F : this.mUnselectedAlpha);
		return true;
	}

	public void setIsRtl(boolean paramBoolean) {
		this.mIsRtl = paramBoolean;
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
		return new AbsSpinner.LayoutParams(-2, -2);
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
		this.mInLayout = true;
		layout(0, false);
		this.mInLayout = false;
	}

	protected void OnScrolling(boolean paramBoolean) {
	}

	int getChildHeight(View paramView) {
		return paramView.getMeasuredHeight();
	}

	void trackMotionScroll(int paramInt) {
		if (getChildCount() == 0)
			return;
		boolean bool = paramInt < 0;
		int i = getLimitedMotionScrollAmount(bool, paramInt);
		if (i != paramInt) {
			this.mFlingRunnable.endFling(false);
			onFinishedMovement();
		}
		offsetChildrenLeftAndRight(i);
		detachOffScreenChildren(bool);
		if (bool)
			fillToGalleryRight();
		else
			fillToGalleryLeft();
		setSelectionToCenterChild();
		onScrollChanged(0, 0, 0, 0);
		invalidate();
	}

	int getLimitedMotionScrollAmount(boolean paramBoolean, int paramInt) {
		int i = paramBoolean != this.mIsRtl ? this.mItemCount - 1 : 0;
		View localView = getChildAt(i - this.mFirstPosition);
		if (localView == null)
			return paramInt;
		int j = getCenterOfView(localView);
		int k = getCenterOfGallery();
		if (paramBoolean) {
			if (j <= k)
				return 0;
		} else if (j >= k)
			return 0;
		int m = k - j;
		return paramBoolean ? Math.max(m, paramInt) : Math.min(m, paramInt);
	}

	private void offsetChildrenLeftAndRight(int paramInt) {
		for (int i = getChildCount() - 1; i >= 0; i--) {
			getChildAt(i).offsetLeftAndRight(paramInt);
			getChildAt(i).invalidate();
		}
	}

	private int getCenterOfGallery() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	private static int getCenterOfView(View paramView) {
		return paramView.getLeft() + paramView.getWidth() / 2;
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
				i2 = this.mIsRtl ? i - 1 - i1 : i1;
				localView = getChildAt(i2);
				if (localView.getRight() >= n)
					break;
				k = i2;
				m++;
				this.mRecycler.addScrapView(j + i2, localView);
			}
			if (!this.mIsRtl)
				k = 0;
		} else {
			n = getWidth() - getPaddingRight();
			for (i1 = i - 1; i1 >= 0; i1--) {
				i2 = this.mIsRtl ? i - 1 - i1 : i1;
				localView = getChildAt(i2);
				if (localView.getLeft() <= n)
					break;
				k = i2;
				m++;
				this.mRecycler.addScrapView(j + i2, localView);
			}
			if (this.mIsRtl)
				k = 0;
		}
		detachViewsFromParent(k, m);
		if (paramBoolean != this.mIsRtl)
			this.mFirstPosition += m;
	}

	protected void scrollIntoSlots() {
		if ((getChildCount() == 0) || (this.mSelectedChild == null))
			return;
		int i = getCenterOfView(this.mSelectedChild);
		int j = getCenterOfGallery();
		int k = j - i;
		if (isInScrollPosition()) {
			View localView = getChildAt(this.scrollPosition - this.mFirstPosition);
			if (localView != null) {
				int m = getCenterOfView(localView);
				k = j - m;
			}
		} else {
			this.scrollPosition = -1;
		}
		if (k != 0) {
			this.mFlingRunnable.startUsingDistance(k);
		} else {
			onFinishedMovement();
			if (this.gainFocus)
				positionSelector(getSelectedItemPosition(), getSelectedView());
		}
	}

	private boolean isInScrollPosition() {
		return (this.scrollPosition != -1) && (this.scrollPosition != this.mSelectedPosition);
	}

	private void onFinishedMovement() {
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

	private void setSelectionToCenterChild() {
		View localView1 = this.mSelectedChild;
		if (this.mSelectedChild == null)
			return;
		int i = getCenterOfGallery();
		if ((localView1.getLeft() <= i) && (localView1.getRight() >= i))
			return;
		int j = 2147483647;
		int k = 0;
		for (int m = getChildCount() - 1; m >= 0; m--) {
			View localView2 = getChildAt(m);
			if ((localView2.getLeft() <= i) && (localView2.getRight() >= i)) {
				k = m;
				break;
			}
			int n = Math.min(Math.abs(localView2.getLeft() - i), Math.abs(localView2.getRight() - i));
			if (n < j) {
				j = n;
				k = m;
			}
		}
		int m = this.mFirstPosition + k;
		if (m != this.mSelectedPosition) {
			setSelectedPositionInt(m);
			setNextSelectedPositionInt(m);
			checkSelectionChanged();
		}
	}

	void layout(int paramInt, boolean paramBoolean) {
		int i = this.mSpinnerPadding.left;
		int j = getRight() - getLeft() - this.mSpinnerPadding.left - this.mSpinnerPadding.right;
		if (this.mDataChanged)
			handleDataChanged();
		if (this.mItemCount == 0) {
			resetList();
			return;
		}
		if (this.mNextSelectedPosition >= 0)
			setSelectedPositionInt(this.mNextSelectedPosition);
		recycleAllViews();
		detachAllViewsFromParent();
		this.mRightMost = 0;
		this.mLeftMost = 0;
		this.mFirstPosition = this.mSelectedPosition;
		View localView = makeAndAddView(this.mSelectedPosition, 0, 0, true);
		resetItemLayout(localView);
		int k = i + j / 2 - localView.getWidth() / 2;
		localView.offsetLeftAndRight(k);
		fillToGalleryRight();
		fillToGalleryLeft();
		invalidate();
		checkSelectionChanged();
		this.mDataChanged = false;
		this.mNeedSync = false;
		setNextSelectedPositionInt(this.mSelectedPosition);
		updateSelectedItemMetadata();
	}

	private void fillToGalleryLeft() {
		if (this.mIsRtl)
			fillToGalleryLeftRtl();
		else
			fillToGalleryLeftLtr();
	}

	private void resetItemLayout(View paramView) {
		if (paramView != null) {
			this.mItemWidth = paramView.getWidth();
			this.mItemHeight = paramView.getHeight();
		}
	}

	private void fillToGalleryLeftRtl() {
		int i = this.mSpacing;
		int j = getPaddingLeft();
		int k = getChildCount();
		int m = this.mItemCount;
		View localView = getChildAt(k - 1);
		int n;
		int i1;
		if (localView != null) {
			n = this.mFirstPosition + k;
			i1 = localView.getLeft() - i;
		} else {
			this.mFirstPosition = (n = this.mItemCount - 1);
			i1 = getRight() - getLeft() - getPaddingRight();
			this.mShouldStopFling = true;
		}
		while ((i1 > j) && (n < this.mItemCount)) {
			localView = makeAndAddView(n, n - this.mSelectedPosition, i1, false);
			i1 = localView.getLeft() - i;
			n++;
		}
	}

	private void fillToGalleryLeftLtr() {
		int i = this.mSpacing;
		int j = getPaddingLeft();
		View localView = getChildAt(0);
		int k;
		int m;
		if (localView != null) {
			k = this.mFirstPosition - 1;
			m = localView.getLeft() - i;
		} else {
			int n = this.scrollPosition != -1 ? 1 : 0;
			if (n != 0) {
				k = getCurPosInScrollPositionMode(false);
				this.scrollPosition = -1;
			} else {
				k = 0;
			}
			m = getRight() - getLeft() - getPaddingRight();
			this.mShouldStopFling = true;
		}
		while ((m > j) && (k >= 0)) {
			localView = makeAndAddView(k, k - this.mSelectedPosition, m, false);
			this.mFirstPosition = k;
			m = localView.getLeft() - i;
			k--;
		}
	}

	private void fillToGalleryRight() {
		if (this.mIsRtl)
			fillToGalleryRightRtl();
		else
			fillToGalleryRightLtr();
	}

	private void fillToGalleryRightRtl() {
		int i = this.mSpacing;
		int j = getRight() - getLeft() - getPaddingLeft();
		View localView = getChildAt(0);
		int k;
		int m;
		if (localView != null) {
			k = this.mFirstPosition - 1;
			m = localView.getRight() + i;
		} else {
			k = 0;
			m = getPaddingLeft();
			this.mShouldStopFling = true;
		}
		while ((m < j) && (k >= 0)) {
			localView = makeAndAddView(k, k - this.mSelectedPosition, m, true);
			this.mFirstPosition = k;
			m = localView.getRight() + i;
			k--;
		}
	}

	private void fillToGalleryRightLtr() {
		int i = this.mSpacing;
		int j = getRight() - getLeft() - getPaddingRight();
		int k = getChildCount();
		int m = this.mItemCount;
		View localView = getChildAt(k - 1);
		int n;
		int i1;
		if (localView != null) {
			n = this.mFirstPosition + k;
			i1 = localView.getRight() + i;
		} else {
			int i2 = this.scrollPosition != -1 ? 1 : 0;
			if (i2 != 0) {
				this.mFirstPosition = (n = getCurPosInScrollPositionMode(true));
				this.scrollPosition = -1;
			} else {
				this.mFirstPosition = (n = this.mItemCount - 1);
			}
			i1 = getPaddingLeft();
			this.mShouldStopFling = true;
		}
		while ((i1 < j) && (n < m)) {
			localView = makeAndAddView(n, n - this.mSelectedPosition, i1, true);
			i1 = localView.getRight() + i;
			n++;
		}
	}

	private int getCurPosInScrollPositionMode(boolean paramBoolean) {
		int i = getItemWidth() + this.mSpacing;
		int j = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2;
		int k = (int) Math.ceil(1.0F * j / i);
		int m = -1;
		if (paramBoolean) {
			m = this.scrollPosition - k + 1;
			m = m <= 0 ? 0 : m;
		} else {
			m = this.scrollPosition + k - 1;
			m = m >= this.mItemCount ? this.mItemCount : m;
		}
		return m;
	}

	private View makeAndAddView(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
		if (!this.mDataChanged) {
			View localView = this.mRecycler.getScrapView(paramInt1);
			if (localView != null) {
				int i = localView.getLeft();
				this.mRightMost = Math.max(this.mRightMost, i + localView.getMeasuredWidth());
				this.mLeftMost = Math.min(this.mLeftMost, i);
				setUpChild(localView, paramInt2, paramInt3, paramBoolean);
				return localView;
			}
		}
		View localView = obtainView(paramInt1, this.mIsScrap);
		setUpChild(localView, paramInt2, paramInt3, paramBoolean);
		return localView;
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

	void checkSelectionChanged() {
		if ((this.mSelectedPosition != this.mOldSelectedPosition) || (this.mSelectedRowId != this.mOldSelectedRowId)) {
			onSelectionChanged(this.mOldSelectedPosition, this.mSelectedPosition, this.mOldSelectedRowId, this.mSelectedRowId);
			selectionChanged();
			if (this.mSelectedPosition != -1) {
				this.mOldSelectedPosition = this.mSelectedPosition;
				this.mOldSelectedRowId = this.mSelectedRowId;
			}
		} else if ((this.mSelectedPosition == this.mOldSelectedPosition) && (this.mSelectedPosition == 0) && (this.gainFocus)) {
			if (this.mSelectorRect.isEmpty())
				positionSelector(0, getSelectedView());
			setScalableView(0, getSelectedView());
		}
	}

	void onSelectionChanged(int paramInt1, int paramInt2, long paramLong1, long paramLong2) {
		if ((paramInt2 != -1) && (this.gainFocus)) {
			if (this.mSelectorRect.isEmpty())
				positionSelector(paramInt2, getSelectedView());
			setScalableView(paramInt2, getSelectedView());
		}
		postInvalidate();
	}

	private void setUpChild(View paramView, int paramInt1, int paramInt2, boolean paramBoolean) {
		AbsSpinner.LayoutParams localLayoutParams = (AbsSpinner.LayoutParams) paramView.getLayoutParams();
		if (localLayoutParams == null)
			localLayoutParams = (AbsSpinner.LayoutParams) generateDefaultLayoutParams();
		addViewInLayout(paramView, paramBoolean != this.mIsRtl ? -1 : 0, localLayoutParams);
		paramView.setSelected(paramInt1 == 0);
		int i = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, localLayoutParams.height);
		int j = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, localLayoutParams.width);
		paramView.measure(j, i);
		int n = calculateTop(paramView, true);
		int i1 = n + paramView.getMeasuredHeight();
		int i2 = paramView.getMeasuredWidth();
		int k;
		int m;
		if (paramBoolean) {
			k = paramInt2;
			m = k + i2;
		} else {
			k = paramInt2 - i2;
			m = paramInt2;
		}
		paramView.layout(k, n, m, i1);
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
		boolean bool = this.mGestureDetector.onTouchEvent(paramMotionEvent);
		int i = paramMotionEvent.getAction();
		if (i == 1)
			onUp();
		else if (i == 3)
			onCancel();
		return bool;
	}

	public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
		if (this.mDownTouchPosition >= 0) {
			scrollToChild(this.mDownTouchPosition - this.mFirstPosition);
			if ((this.mShouldCallbackOnUnselectedItemClick) || (this.mDownTouchPosition == this.mSelectedPosition))
				performItemClick(this.mDownTouchView, this.mDownTouchPosition, this.mAdapter.getItemId(this.mDownTouchPosition));
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
		trackMotionScroll(-1 * (int) paramFloat1);
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

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		switch (paramInt) {
		case 21:
			if (movePrevious()) {
				playSoundEffect(1);
				return true;
			}
			break;
		case 22:
			if (moveNext()) {
				playSoundEffect(3);
				return true;
			}
			break;
		case 23:
		case 66:
			this.mReceivedInvokeKeyDown = true;
		}
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
		switch (paramInt) {
		case 23:
		case 66:
			if ((this.mReceivedInvokeKeyDown) && (this.mItemCount > 0)) {
				dispatchPress(this.mSelectedChild);
				postDelayed(new Runnable() {
					public void run() {
						Gallery.this.dispatchUnpress();
					}
				}, ViewConfiguration.getPressedStateDuration());
				int i = this.mSelectedPosition - this.mFirstPosition;
				performItemClick(getChildAt(i), this.mSelectedPosition, this.mAdapter.getItemId(this.mSelectedPosition));
			}
			this.mReceivedInvokeKeyDown = false;
			return true;
		}
		return super.onKeyUp(paramInt, paramKeyEvent);
	}

	boolean movePrevious() {
		return scrollToChild(this.mSelectedPosition - 1);
	}

	boolean moveNext() {
		return scrollToChild(this.mSelectedPosition + 1);
	}

	public boolean scrollToChild(int paramInt) {
		if ((paramInt < 0) || (paramInt > getAdapter().getCount() - 1))
			return false;
		smoothScrollToPosition(paramInt);
		return true;
	}

	private void smoothScrollToPosition(int paramInt) {
		this.scrollPosition = paramInt;
		int i = 0;
		int j = getRelSelectedPosition();
		int k = j - paramInt;
		i = k * (getItemWidth() + this.mSpacing);
		int m = getWidth();
		this.mFlingRunnable.startUsingDistance(i);
	}

	public void smoothScrollBy(int paramInt1, int paramInt2) {
		this.mFlingRunnable.startScroll(-paramInt1, paramInt2);
	}

	public void endFling() {
		this.mFlingRunnable.endFling(false);
	}

	public int getActualX() {
		return this.mFlingRunnable.getActualX();
	}

	public void startAutoScroll() {
		this.mAutoScroll = true;
		this.mAutoPaused = false;
		this.mAutoScrollRunnable.stop();
		this.mAutoScrollRunnable.run();
	}

	public void stopAutoScroll() {
		this.mAutoScroll = false;
		this.mAutoScrollRunnable.stop();
	}

	public void setAutoScrollDelay(int paramInt) {
		this.mAutoScrollDelay = paramInt;
	}

	int getRelSelectedPosition() {
		int i = -1;
		if ((!this.gainFocus) && (this.mSelectedPosition == -1))
			i = this.mOldSelectedPosition;
		else
			i = this.mSelectedPosition;
		return i;
	}

	void setSelectedPositionInt(int paramInt) {
		super.setSelectedPositionInt(paramInt);
		updateSelectedItemMetadata();
	}

	private void updateSelectedItemMetadata() {
		View localView1 = this.mSelectedChild;
		View localView2 = this.mSelectedChild = getChildAt(this.mSelectedPosition - this.mFirstPosition);
		if (localView2 == null)
			return;
		localView2.setSelected(true);
		localView2.setFocusable(false);
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

	private int getDrawingOrder(int paramInt1, int paramInt2, int paramInt3) {
		int i = 0;
		int j = 0;
		int k = paramInt1 - 1;
		i = paramInt2;
		j = paramInt2;
		i = paramInt2;
		j = paramInt2;
		if (paramInt2 < 0)
			return paramInt1 - 1 - paramInt3;
		if (paramInt2 > paramInt1 - 1)
			return paramInt3;
		if (k == paramInt3)
			return paramInt2;
		for (int m = 0; m <= paramInt1; m++) {
			i--;
			if (i >= 0) {
				k--;
				if (k == paramInt3)
					return i;
			}
			j++;
			if (j <= paramInt1 - 1) {
				k--;
				if (k == paramInt3)
					return j;
			}
		}
		return paramInt3;
	}

	protected int getChildDrawingOrder(int paramInt1, int paramInt2) {
		int i = 0;
		return getDrawingOrder(paramInt1, getRelSelectedPosition() - this.mFirstPosition, paramInt2);
	}

	protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
		super.onFocusChanged(paramBoolean, paramInt, paramRect);
		this.gainFocus = paramBoolean;
		if (paramBoolean) {
			this.mSelectedPosition = (this.mOldSelectedPosition == -1 ? 0 : this.mOldSelectedPosition);
			positionSelector(this.mSelectedPosition, getSelectedView());
			setScalableView(this.mSelectedPosition, getSelectedView());
		} else {
			this.mOldSelectedPosition = this.mSelectedPosition;
			this.mSelectedPosition = -1;
			this.mSelectorRect.setEmpty();
			clearScalableView();
		}
		checkSelectionChanged();
	}

	public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent) {
		super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
		paramAccessibilityEvent.setClassName(Gallery.class.getName());
	}

	public boolean isCoverFlowScrolling() {
		return this.isScrolling;
	}

	public void setOnScrollingListener(OnScrollingListener paramOnScrollingListener) {
		this.mOnScrollingListener = paramOnScrollingListener;
	}

	public static abstract interface OnScrollingListener {
		public abstract boolean OnScroll(boolean paramBoolean);
	}

	private class FlingRunnable implements Runnable {
		private Scroller mScroller = new Scroller(Gallery.this.getContext());
		private int mLastFlingX;
		private int mActualX = 0;

		public FlingRunnable() {
		}

		public int getActualX() {
			return this.mActualX;
		}

		private void startCommon() {
			Gallery.this.removeCallbacks(this);
			if (Gallery.this.mAutoScroll)
				Gallery.this.mAutoPaused = true;
			if (!Gallery.this.isScrolling) {
				Gallery.this.isScrolling = true;
				if (Gallery.this.mOnScrollingListener != null)
					Gallery.this.mOnScrollingListener.OnScroll(true);
				Gallery.this.OnScrolling(Gallery.this.isScrolling);
			}
		}

		public void startUsingVelocity(int paramInt) {
			if (paramInt == 0)
				return;
			startCommon();
			int i = paramInt < 0 ? 2147483647 : 0;
			this.mLastFlingX = i;
			this.mScroller.fling(i, 0, paramInt, 0, 0, 2147483647, 0, 2147483647);
			Gallery.this.post(this);
		}

		public void startScroll(int paramInt1, int paramInt2) {
			if (paramInt1 == 0)
				return;
			startCommon();
			this.mLastFlingX = 0;
			this.mScroller.startScroll(0, 0, -paramInt1, 0, paramInt2);
			Gallery.this.post(this);
		}

		public void startUsingDistance(int paramInt) {
			if (paramInt == 0)
				return;
			startCommon();
			this.mLastFlingX = 0;
			this.mScroller.startScroll(0, 0, -paramInt, 0, Gallery.this.mAnimationDuration);
			Gallery.this.post(this);
		}

		public void stop(boolean paramBoolean) {
			Gallery.this.removeCallbacks(this);
			endFling(paramBoolean);
		}

		private void endFling(boolean paramBoolean) {
			this.mScroller.forceFinished(true);
			if (paramBoolean)
				Gallery.this.scrollIntoSlots();
			if (Gallery.this.mAutoScroll)
				Gallery.this.mAutoPaused = false;
			if (Gallery.this.isScrolling) {
				Gallery.this.isScrolling = false;
				if (Gallery.this.mOnScrollingListener != null)
					Gallery.this.mOnScrollingListener.OnScroll(false);
				Gallery.this.OnScrolling(Gallery.this.isScrolling);
			}
		}

		public void run() {
			if (Gallery.this.mItemCount == 0) {
				endFling(true);
				return;
			}
			Gallery.this.mShouldStopFling = false;
			Scroller localScroller = this.mScroller;
			boolean bool = localScroller.computeScrollOffset();
			int i = localScroller.getCurrX();
			this.mActualX = i;
			int j = this.mLastFlingX - i;
			int k;
			if (j > 0) {
				Gallery.this.mDownTouchPosition = (Gallery.this.mIsRtl ? Gallery.this.mFirstPosition + Gallery.this.getChildCount() - 1 : Gallery.this.mFirstPosition);
				j = Math.min(Gallery.this.getWidth() - Gallery.this.getPaddingLeft() - Gallery.this.getPaddingRight() - 1, j);
			} else {
				k = Gallery.this.getChildCount() - 1;
				Gallery.this.mDownTouchPosition = (Gallery.this.mIsRtl ? Gallery.this.mFirstPosition : Gallery.this.mFirstPosition + Gallery.this.getChildCount() - 1);
				j = Math.max(-(Gallery.this.getWidth() - Gallery.this.getPaddingRight() - Gallery.this.getPaddingLeft() - 1), j);
			}
			Gallery.this.trackMotionScroll(j);
			if ((bool) && (!Gallery.this.mShouldStopFling)) {
				this.mLastFlingX = i;
				k = Gallery.this.getRelSelectedPosition();
				if ((Gallery.this.scrollPosition != -1) && (Gallery.this.scrollPosition != k)) {
					int m = Gallery.this.getWidth() - Gallery.this.getPaddingRight() - Gallery.this.getPaddingLeft() - 1;
					int n = k - Gallery.this.scrollPosition;
					int i1 = Gallery.this.getItemWidth() + Gallery.this.mSpacing;
					int i2 = n * i1;
					int i3 = Math.abs(i2);
					if (i3 > m)
						Gallery.this.smoothScrollToPosition(Gallery.this.scrollPosition);
					else
						Gallery.this.post(this);
				} else {
					Gallery.this.post(this);
				}
			} else {
				endFling(true);
			}
		}
	}

	private class AutoScrollRunnable implements Runnable {
		private AutoScrollRunnable() {
		}

		public void run() {
			if (Gallery.this.mAutoScroll) {
				if (!Gallery.this.mAutoPaused) {
					int i = Gallery.this.getRelSelectedPosition();
					int j = -1;
					if (i == Gallery.this.getAdapter().getCount() - 1)
						j = 0;
					else
						j = i + 1;
					Gallery.this.scrollToChild(j);
				}
				Gallery.this.postDelayed(this, Gallery.this.mAutoScrollDelay);
			}
		}

		public void stop() {
			Gallery.this.removeCallbacks(this);
		}
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.app.widget.Gallery JD-Core Version: 0.6.2
 */