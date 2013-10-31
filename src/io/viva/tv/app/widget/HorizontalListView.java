package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.SpinnerAdapter;

public class HorizontalListView extends AbsHorizontalListView {
	int mDividerWidth;
	Drawable mDivider;
	static final int NO_POSITION = -1;
	private static final String TAG = "HorizontalListView";

	public HorizontalListView(Context paramContext) {
		this(paramContext, null);
	}

	public HorizontalListView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public HorizontalListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		setSpacing(0);
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		super.onMeasure(paramInt1, paramInt2);
		int i = View.MeasureSpec.getMode(paramInt1);
		int j = View.MeasureSpec.getMode(paramInt2);
		if (i == -2147483648) {
			int k = View.MeasureSpec.getSize(paramInt1);
			int m = getMeasuredHeight();
			int n = measureWidthOfChildren(paramInt2, 0, -1, k, -1);
			k = n > k ? k : n;
			setMeasuredDimension(k, m);
		}
		int k = getMeasuredHeight();
		switch (this.mGravity) {
		case 16:
			this.mGravityHeightAnchor = (k >> 1);
		}
	}

	final int measureWidthOfChildren(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
		SpinnerAdapter localSpinnerAdapter = this.mAdapter;
		if (localSpinnerAdapter == null)
			return this.mSpinnerPadding.left + this.mSpinnerPadding.right;
		int i = this.mSpinnerPadding.left + this.mSpinnerPadding.right;
		int j = (this.mDividerWidth > 0) && (this.mDivider != null) ? this.mDividerWidth : 0;
		int k = 0;
		paramInt3 = paramInt3 == -1 ? localSpinnerAdapter.getCount() - 1 : paramInt3;
		AbsSpinner.RecycleBin localRecycleBin = this.mRecycler;
		boolean bool = recycleOnMeasure();
		boolean[] arrayOfBoolean = this.mIsScrap;
		for (int m = paramInt2; m <= paramInt3; m++) {
			View localView = obtainView(m, arrayOfBoolean);
			measureScrapChild(localView, m, paramInt1);
			if (m > 0)
				i += j;
			int n = ((AbsSpinner.LayoutParams) localView.getLayoutParams()).viewType;
			if ((bool) && (localRecycleBin.shouldRecycleViewType(n)))
				localRecycleBin.addScrapView(-1, localView);
			i += localView.getMeasuredWidth();
			if (i >= paramInt4)
				return (paramInt5 >= 0) && (m > paramInt5) && (k > 0) && (i != paramInt4) ? k : i;
			if ((paramInt5 >= 0) && (m >= paramInt5))
				k = i;
		}
		return i;
	}

	public void setSelection(int paramInt) {
		super.setSelection(paramInt);
		requestLayout();
	}

	int lookForSelectablePositionOnScreen(int paramInt) {
		int i = this.mFirstPosition;
		int j;
		int k;
		SpinnerAdapter localSpinnerAdapter;
		int m;
		if (paramInt == 66) {
			j = this.mSelectedPosition != -1 ? this.mSelectedPosition + 1 : i;
			if (j >= this.mAdapter.getCount())
				return -1;
			if (j < i)
				j = i;
			k = getLastVisiblePosition();
			localSpinnerAdapter = getAdapter();
			for (m = j; m <= k; m++)
				if (getChildAt(m - i).getVisibility() == 0)
					return m;
		} else {
			j = i + getChildCount() - 1;
			k = this.mSelectedPosition != -1 ? this.mSelectedPosition - 1 : i + getChildCount() - 1;
			if ((k < 0) || (k >= this.mAdapter.getCount()))
				return -1;
			if (k > j)
				k = j;
			localSpinnerAdapter = getAdapter();
			for (m = k; m >= i; m--)
				if (getChildAt(m - i).getVisibility() == 0)
					return m;
		}
		return -1;
	}

	void fillGap(boolean paramBoolean) {
		if (paramBoolean)
			fillToGalleryRight();
		else
			fillToGalleryLeft();
	}

	int amountToScroll(int paramInt1, int paramInt2) {
		int i = getListRight();
		int j = getListLeft();
		int k = getChildCount();
		int i3;
		if (paramInt1 == 66) {
			int m = k - 1;
			if (paramInt2 != -1)
				m = paramInt2 - this.mFirstPosition;
			int n = this.mFirstPosition + m;
			View localView = getChildAt(m);
			int i1 = i;
			if (n < this.mItemCount - 1)
				i1 -= getArrowScrollPreviewLength();
			if (!this.unhandleFullVisible) {
				if (localView.getRight() <= i1)
					return 0;
				if ((paramInt2 != -1) && (i1 - localView.getLeft() >= getMaxScrollAmount()))
					return 0;
			}
			int i2 = localView.getRight() - i1;
			if (this.mFirstPosition + k == this.mItemCount) {
				i3 = getChildAt(k - 1).getRight() - i1;
				i2 = Math.min(i2, i3);
			}
			return Math.min(i2, getMaxScrollAmount());
		}
		int m = 0;
		if (paramInt2 != -1)
			m = paramInt2 - this.mFirstPosition;
		int n = this.mFirstPosition + m;
		View localView = getChildAt(m);
		int i1 = j;
		if (n > 0)
			i1 += getArrowScrollPreviewLength();
		if (!this.unhandleFullVisible) {
			if (localView.getLeft() >= i1)
				return 0;
			if ((paramInt2 != -1) && (localView.getLeft() - i1 >= getMaxScrollAmount()))
				return 0;
		}
		int i2 = i1 - localView.getLeft();
		if (this.mFirstPosition == 0) {
			i3 = i1 - getChildAt(0).getLeft();
			i2 = Math.min(i2, i3);
		}
		return Math.min(i2, getMaxScrollAmount());
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
		int k = getChildCount();
		int m = 0;
		View localView1 = null;
		View localView2 = null;
		m = this.mSelectedPosition - this.mFirstPosition;
		if ((m >= 0) && (m < k))
			localView1 = getChildAt(m);
		localView2 = getChildAt(0);
		if (this.mNextSelectedPosition >= 0)
			paramInt = this.mNextSelectedPosition - this.mSelectedPosition;
		if (this.mNextSelectedPosition >= 0)
			setSelectedPositionInt(this.mNextSelectedPosition);
		View localView3 = null;
		Object localObject = null;
		View localView4 = getFocusedChild();
		if (localView4 != null) {
			if (!this.mDataChanged) {
				localObject = localView4;
				localView3 = findFocus();
				if (localView3 != null)
					localView3.onStartTemporaryDetach();
			}
			requestFocus();
		}
		recycleAllViews();
		detachAllViewsFromParent();
		this.mRightMost = 0;
		this.mLeftMost = 0;
		View localView5 = null;
		int n;
		switch (this.mLayoutMode) {
		case 4:
			localView5 = fillSpecific(reconcileSelectedPosition(), this.mSpecificTop);
			break;
		default:
			if (!this.mStackFromRight) {
				n = localView2 != null ? localView2.getLeft() : i;
				localView5 = fillFromLeft(n);
			}
			break;
		}
		if ((localView5 != null) && (this.gainFocus)) {
			if ((this.mItemsCanFocus) && (hasFocus()) && (!localView5.hasFocus())) {
				n = ((localView5 == localObject) && (localView3 != null) && (localView3.requestFocus())) || (localView5.requestFocus()) ? 1 : 0;
				if (n == 0) {
					View localView6 = getFocusedChild();
					if (localView6 != null)
						localView6.clearFocus();
					positionSelector(-1, localView5);
				} else {
					localView5.setSelected(false);
					this.mSelectorRect.setEmpty();
				}
			} else {
				positionSelector(-1, localView5);
			}
			this.mSelectedLeft = localView5.getLeft();
		} else {
			this.mSelectorRect.setEmpty();
		}
		if ((localView3 != null) && (localView3.getWindowToken() != null))
			localView3.onFinishTemporaryDetach();
		invalidate();
		checkSelectionChanged();
		this.mDataChanged = false;
		if (this.mPositionScrollAfterLayout != null) {
			post(this.mPositionScrollAfterLayout);
			this.mPositionScrollAfterLayout = null;
		}
		this.mNeedSync = false;
		setNextSelectedPositionInt(this.mSelectedPosition);
	}

	View addViewFoward(View paramView, int paramInt) {
		int i = paramInt - 1;
		View localView = obtainView(i, this.mIsScrap);
		int j = paramView.getLeft() - this.mDividerWidth;
		setupChild(localView, i, j, false, this.mSpinnerPadding.left, false, this.mIsScrap[0]);
		return localView;
	}

	View addViewBackward(View paramView, int paramInt) {
		int i = paramInt + 1;
		View localView = obtainView(i, this.mIsScrap);
		int j = paramView.getRight() + this.mDividerWidth;
		setupChild(localView, i, j, true, this.mSpinnerPadding.left, false, this.mIsScrap[0]);
		return localView;
	}

	View addViewFoward(View paramView, int paramInt1, int paramInt2) {
		int i = paramInt1;
		View localView = paramView;
		for (int j = 0; j < paramInt2; j++) {
			localView = addViewFoward(localView, i);
			i--;
		}
		return localView;
	}

	View addViewBackward(View paramView, int paramInt1, int paramInt2) {
		int i = paramInt1;
		View localView = paramView;
		for (int j = 0; j < paramInt2; j++) {
			localView = addViewBackward(localView, i);
			i++;
		}
		return localView;
	}

	void scrollListItemsBy(int paramInt) {
		this.mFlingRunnable.startUsingDistance(paramInt);
		int i = getWidth() - this.mSpinnerPadding.right;
		int j = this.mSpinnerPadding.left;
		AbsSpinner.RecycleBin localRecycleBin = this.mRecycler;
		View localView3;
		AbsSpinner.LayoutParams localLayoutParams;
		if (paramInt < 0) {
			int k = getChildCount();
			View localView2 = getChildAt(k - 1);
			while (localView2.getRight() < i) {
				int n = this.mFirstPosition + k - 1;
				if (n >= this.mItemCount - 1)
					break;
				localView2 = addViewBackward(localView2, n);
				k++;
			}
			if (localView2.getRight() < i)
				offsetChildrenLeftAndRight(i - localView2.getRight());
			localView3 = getChildAt(0);
			while (localView3.getRight() < j) {
				localLayoutParams = (AbsSpinner.LayoutParams) localView3.getLayoutParams();
				if (localRecycleBin.shouldRecycleViewType(localLayoutParams.viewType)) {
					detachViewFromParent(localView3);
					localRecycleBin.addScrapView(this.mFirstPosition, localView3);
				} else {
					removeViewInLayout(localView3);
				}
				localView3 = getChildAt(0);
				this.mFirstPosition += 1;
			}
		} else {
			View localView1 = getChildAt(0);
			while ((localView1.getLeft() > j) && (this.mFirstPosition > 0)) {
				localView1 = addViewFoward(localView1, this.mFirstPosition);
				this.mFirstPosition -= 1;
			}
			if (localView1.getLeft() > j)
				offsetChildrenLeftAndRight(j - localView1.getLeft());
			int m = getChildCount() - 1;
			for (localView3 = getChildAt(m); localView3.getLeft() > i; localView3 = getChildAt(--m)) {
				localLayoutParams = (AbsSpinner.LayoutParams) localView3.getLayoutParams();
				if (localRecycleBin.shouldRecycleViewType(localLayoutParams.viewType)) {
					detachViewFromParent(localView3);
					localRecycleBin.addScrapView(this.mFirstPosition + m, localView3);
				} else {
					removeViewInLayout(localView3);
				}
			}
		}
	}

	private View fillFromLeft(int paramInt) {
		this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
		this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
		if (this.mFirstPosition < 0)
			this.mFirstPosition = 0;
		return fillRight(this.mFirstPosition, paramInt);
	}

	private void correctTooLeft(int paramInt) {
		if ((this.mFirstPosition == 0) && (paramInt > 0)) {
			View localView1 = getChildAt(0);
			int i = localView1.getLeft();
			int j = this.mSpinnerPadding.left;
			int k = getRight() - getLeft() - this.mSpinnerPadding.right;
			int m = i - j;
			View localView2 = getChildAt(paramInt - 1);
			int n = localView2.getBottom();
			int i1 = this.mFirstPosition + paramInt - 1;
			if (m > 0)
				if ((i1 < this.mItemCount - 1) || (n > k)) {
					if (i1 == this.mItemCount - 1)
						m = Math.min(m, n - k);
					offsetChildrenLeftAndRight(-m);
					if (i1 < this.mItemCount - 1) {
						fillRight(i1 + 1, localView2.getBottom() + this.mDividerWidth);
						adjustViewsRightOrLeft();
					}
				} else if (i1 == this.mItemCount - 1) {
					adjustViewsRightOrLeft();
				}
		}
	}

	private View fillSpecific(int paramInt1, int paramInt2) {
		boolean bool = paramInt1 == this.mSelectedPosition;
		View localView1 = makeAndAddView(paramInt1, paramInt2, true, this.mSpinnerPadding.top, bool);
		this.mFirstPosition = paramInt1;
		int i = this.mDividerWidth;
		View localView2;
		View localView3;
		int j;
		if (!this.mStackFromRight) {
			localView2 = fillRight(paramInt1 - 1, localView1.getLeft() - i);
			adjustViewsRightOrLeft();
			localView3 = fillLeft(paramInt1 + 1, localView1.getRight() + i);
			j = getChildCount();
			if (j > 0)
				correctTooRight(j);
		} else {
			localView3 = fillLeft(paramInt1 + 1, localView1.getRight() + i);
			adjustViewsRightOrLeft();
			localView2 = fillRight(paramInt1 - 1, localView1.getLeft() - i);
			j = getChildCount();
			if (j <= 0)
				;
		}
		if (bool)
			return localView1;
		if (localView2 != null)
			return localView2;
		return localView3;
	}

	private void correctTooRight(int paramInt) {
		int i = this.mFirstPosition + paramInt - 1;
		if ((i == this.mItemCount - 1) && (paramInt > 0)) {
			View localView1 = getChildAt(paramInt - 1);
			int j = localView1.getRight();
			int k = getRight() - getLeft() - this.mSpinnerPadding.right;
			int m = k - j;
			View localView2 = getChildAt(0);
			int n = localView2.getLeft();
			if ((m > 0) && ((this.mFirstPosition > 0) || (n < this.mSpinnerPadding.left))) {
				if (this.mFirstPosition == 0)
					m = Math.min(m, this.mSpinnerPadding.left - n);
				offsetChildrenLeftAndRight(m);
				if (this.mFirstPosition > 0) {
					fillLeft(this.mFirstPosition - 1, localView2.getTop() - this.mDividerWidth);
					adjustViewsRightOrLeft();
				}
			}
		}
	}

	private View makeAndAddView(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2) {
		if (!this.mDataChanged) {
			View localView = this.mRecycler.getScrapView(paramInt1);
			if (localView != null) {
				setupChild(localView, paramInt1, paramInt2, paramBoolean1, paramInt3, paramBoolean2, true);
				return localView;
			}
		}
		View localView = obtainView(paramInt1, this.mIsScrap);
		setupChild(localView, paramInt1, paramInt2, paramBoolean1, paramInt3, paramBoolean2, this.mIsScrap[0]);
		return localView;
	}

	private void setupChild(View paramView, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, boolean paramBoolean3) {
		boolean bool = (paramBoolean2) && (shouldShowSelector());
		int i = bool != paramView.isSelected() ? 1 : 0;
		int j = (!paramBoolean3) || (i != 0) || (paramView.isLayoutRequested()) ? 1 : 0;
		AbsSpinner.LayoutParams localLayoutParams = (AbsSpinner.LayoutParams) paramView.getLayoutParams();
		if (localLayoutParams == null)
			localLayoutParams = (AbsSpinner.LayoutParams) generateDefaultLayoutParams();
		localLayoutParams.viewType = this.mAdapter.getItemViewType(paramInt1);
		if ((paramBoolean3) && (!localLayoutParams.forceAdd)) {
			attachViewToParent(paramView, paramBoolean1 ? -1 : 0, localLayoutParams);
		} else {
			localLayoutParams.forceAdd = false;
			addViewInLayout(paramView, paramBoolean1 ? -1 : 0, localLayoutParams, true);
		}
		if (i != 0)
			paramView.setSelected(bool);
		if (j != 0) {
			int k = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, localLayoutParams.height);
			int m = localLayoutParams.width;
			int n;
			if (m > 0)
				n = View.MeasureSpec.makeMeasureSpec(m, 1073741824);
			else
				n = View.MeasureSpec.makeMeasureSpec(0, 0);
			paramView.measure(n, k);
		} else {
			cleanupLayoutState(paramView);
		}
		int k = paramView.getMeasuredWidth();
		int m = paramView.getMeasuredHeight();
		int n = paramBoolean1 ? paramInt2 : paramInt2 - k;
		paramInt3 += getGravityHeightAnchor(paramView);
		if (j != 0) {
			int i1 = n + k;
			int i2 = paramInt3 + m;
			paramView.layout(n, paramInt3, i1, i2);
		} else {
			paramView.offsetLeftAndRight(n - paramView.getLeft());
			paramView.offsetTopAndBottom(paramInt3 - paramView.getTop());
		}
		if ((paramBoolean3) && (((AbsSpinner.LayoutParams) paramView.getLayoutParams()).scrappedFromPosition != paramInt1))
			paramView.jumpDrawablesToCurrentState();
	}

	private void fillToGalleryLeft() {
		if (this.mStackFromRight)
			fillToGalleryLeftRtl();
		else
			fillToGalleryLeftLtr();
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
			localView = makeAndAddView(n, i1, false, this.mSpinnerPadding.top, this.mSelectedPosition == n);
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
			k = 0;
			m = getRight() - getLeft() - getPaddingRight();
			this.mShouldStopFling = true;
		}
		while ((m > j) && (k >= 0)) {
			localView = makeAndAddView(k, m, false, this.mSpinnerPadding.top, this.mSelectedPosition == k);
			this.mFirstPosition = k;
			m = localView.getLeft() - i;
			k--;
		}
	}

	private void fillToGalleryRight() {
		if (this.mStackFromRight)
			fillToGalleryRightRtl();
		else
			fillToGalleryRightLtr();
	}

	private void fillToGalleryRightRtl() {
		int i = this.mSpacing;
		int j = getRight() - getLeft() - getPaddingRight();
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
			localView = makeAndAddView(k, m, true, this.mSpinnerPadding.top, this.mSelectedPosition == k);
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
			this.mFirstPosition = (n = this.mItemCount - 1);
			i1 = getPaddingLeft();
			this.mShouldStopFling = true;
		}
		while ((i1 < j) && (n < m)) {
			localView = makeAndAddView(n, i1, true, this.mSpinnerPadding.top, this.mSelectedPosition == n);
			i1 = localView.getRight() + i;
			n++;
		}
	}

	private View fillLeft(int paramInt1, int paramInt2) {
		View localObject = null;
		int i = 0;
		while ((paramInt2 > i) && (paramInt1 >= 0)) {
			boolean bool = paramInt1 == this.mSelectedPosition;
			View localView = makeAndAddView(paramInt1, paramInt2, false, this.mSpinnerPadding.top, bool);
			paramInt2 = localView.getLeft() - this.mDividerWidth;
			if (bool)
				localObject = localView;
			paramInt1--;
		}
		this.mFirstPosition = (paramInt1 + 1);
		return localObject;
	}

	private View fillRight(int paramInt1, int paramInt2) {
		View localObject = null;
		int i = getRight() - getLeft();
		while ((paramInt2 < i) && (paramInt1 < this.mItemCount)) {
			boolean bool = paramInt1 == this.mSelectedPosition;
			View localView = makeAndAddView(paramInt1, paramInt2, true, this.mSpinnerPadding.top, bool);
			if (localView != null)
				i = getMaxWidth(localView, i);
			paramInt2 = localView.getRight() + this.mDividerWidth;
			if ((bool) || ((this.mSelectedPosition == -1) && (paramInt1 == 0))) {
				localObject = localView;
				if (localObject != null)
					resetItemLayout(localObject);
			}
			paramInt1++;
		}
		return localObject;
	}

	@ViewDebug.ExportedProperty(category = "list")
	protected boolean recycleOnMeasure() {
		return true;
	}

	protected void measureScrapChild(View paramView, int paramInt1, int paramInt2) {
		AbsSpinner.LayoutParams localLayoutParams = (AbsSpinner.LayoutParams) paramView.getLayoutParams();
		if (localLayoutParams == null) {
			localLayoutParams = new AbsSpinner.LayoutParams(-2, -1);
			paramView.setLayoutParams(localLayoutParams);
		}
		localLayoutParams.viewType = this.mAdapter.getItemViewType(paramInt1);
		int i = ViewGroup.getChildMeasureSpec(paramInt2, this.mSpinnerPadding.top + this.mSpinnerPadding.right, localLayoutParams.height);
		int j = localLayoutParams.width;
		int k;
		if (j > 0)
			k = View.MeasureSpec.makeMeasureSpec(j, 1073741824);
		else
			k = View.MeasureSpec.makeMeasureSpec(0, 0);
		paramView.measure(k, i);
	}

	protected boolean getChildStaticTransformation(View paramView, Transformation paramTransformation) {
		return super.getChildStaticTransformation(paramView, paramTransformation);
	}

	@Deprecated
	public void setItemsCanFocus(boolean paramBoolean) {
	}

	boolean arrowScrollImpl(int paramInt) {
		if (getChildCount() <= 0)
			return false;
		View localView1 = getSelectedView();
		int i = this.mSelectedPosition;
		int j = lookForSelectablePositionOnScreen(paramInt);
		int k = amountToScroll(paramInt, j);
		ArrowScrollFocusResult localObject = this.mItemsCanFocus ? arrowScrollFocused(paramInt) : null;
		if (localObject != null) {
			j = localObject.getSelectedPosition();
			k = localObject.getAmountToScroll();
		}
		int m = localObject != null ? 1 : 0;
		View localView2;
		if (j != -1) {
			handleNewSelectionChange(localView1, paramInt, j, localObject != null);
			setSelectedPositionInt(j);
			setNextSelectedPositionInt(j);
			localView1 = getSelectedView();
			i = j;
			if ((this.mItemsCanFocus) && (localObject == null)) {
				localView2 = getFocusedChild();
				if (localView2 != null)
					localView2.clearFocus();
			}
			m = 1;
			checkSelectionChanged();
		}
		if (k > 0) {
			scrollListItemsBy(paramInt == 17 ? k : -k);
			m = 1;
		}
		if ((this.mItemsCanFocus) && (localObject == null) && (localView1 != null) && (localView1.hasFocus())) {
			localView2 = localView1.findFocus();
			if ((!isViewAncestorOf(localView2, this)) || (distanceToView(localView2) > 0))
				localView2.clearFocus();
		}
		if ((j == -1) && (localView1 != null) && (!isViewAncestorOf(localView1, this))) {
			localView1 = null;
			hideSelector();
		}
		if (m != 0) {
			if ((localView1 != null) && (k <= 0)) {
				positionSelector(i, localView1);
				this.mSelectedLeft = localView1.getLeft();
			}
			if (!awakenScrollBars())
				invalidate();
			return true;
		}
		return false;
	}

	boolean movePrevious() {
		if ((this.mItemCount > 0) && (this.mSelectedPosition > 0)) {
			arrowScrollImpl(17);
			return true;
		}
		return false;
	}

	boolean moveNext() {
		if ((this.mItemCount > 0) && (this.mSelectedPosition < this.mItemCount - 1)) {
			arrowScrollImpl(66);
			return true;
		}
		return false;
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if ((this.mAdapter == null) || (!this.mIsAttached))
			return false;
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
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.app.widget.HorizontalListView JD-Core Version: 0.6.2
 */