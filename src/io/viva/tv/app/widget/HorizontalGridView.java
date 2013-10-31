package io.viva.tv.app.widget;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.SpinnerAdapter;

public class HorizontalGridView extends AbsHorizontalListView {
	int mDividerWidth;
	Drawable mDivider;
	int mNumLines;
	int mTotalHeight = 0;
	List<Integer> mHeightList = new LinkedList();
	protected int mHorizontalSpacing = 0;
	protected int mVerticalSpacing = 0;
	static final int NO_POSITION = -1;
	private static final String TAG = "HorizontalListView";

	public HorizontalGridView(Context paramContext) {
		this(paramContext, null);
	}

	public HorizontalGridView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public HorizontalGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		setSpacing(0);
	}

	public void setNumLines(int paramInt) {
		this.mNumLines = paramInt;
	}

	public int getNumLines() {
		return this.mNumLines;
	}

	public void setHorizontalSpacing(int paramInt) {
		this.mHorizontalSpacing = paramInt;
	}

	public void setVerticalSpacing(int paramInt) {
		this.mVerticalSpacing = paramInt;
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
		this.mItemCount = (this.mAdapter == null ? 0 : this.mAdapter.getCount());
		int m = this.mItemCount;
		if (m > 0) {
			Object localObject;
			if (j == 0) {
				localObject = obtainView(0, this.mIsScrap);
				AbsSpinner.LayoutParams localLayoutParams = (AbsSpinner.LayoutParams) ((View) localObject).getLayoutParams();
				if (localLayoutParams == null) {
					localLayoutParams = new AbsSpinner.LayoutParams(-1, -2, 0);
					((View) localObject).setLayoutParams(localLayoutParams);
				}
				localLayoutParams.viewType = this.mAdapter.getItemViewType(0);
				localLayoutParams.forceAdd = true;
				int i2 = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, localLayoutParams.height);
				int i3 = localLayoutParams.width;
				int i4;
				if (i3 > 0)
					i4 = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
				else
					i4 = View.MeasureSpec.makeMeasureSpec(0, 0);
				((View) localObject).measure(i4, i2);
				int i5 = ((View) localObject).getMeasuredWidth();
				int i6 = ((View) localObject).getMeasuredHeight();
				setMeasuredDimension(this.mWidthMeasureSpec, i6 * this.mNumLines + this.mSpinnerPadding.top + this.mSpinnerPadding.bottom);
				if (this.mRecycler.shouldRecycleViewType(localLayoutParams.viewType))
					this.mRecycler.addScrapView(-1, (View) localObject);
			} else {
				localObject = getLayoutParams();
				int i1 = ((ViewGroup.LayoutParams) localObject).width;
				k = ((ViewGroup.LayoutParams) localObject).height;
				setMeasuredDimension(this.mWidthMeasureSpec, k);
			}
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
		int i = paramInt - this.mSelectedPosition;
		if (Math.abs(i) >= this.mNumLines) {
			if (i >= this.mNumLines)
				arrowScrollImpl(66);
			else if (i <= -this.mNumLines)
				arrowScrollImpl(17);
			setNextSelectedPositionInt(paramInt);
		} else {
			super.setSelection(paramInt);
		}
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
		int m;
		int n;
		View localView;
		int i1;
		int i2;
		int i3;
		if (paramInt1 == 66) {
			m = k - 1;
			if (paramInt2 != -1)
				m = paramInt2 - this.mFirstPosition;
			n = this.mFirstPosition + m;
			localView = getChildAt(m);
			i1 = i;
			if (n < this.mItemCount - 1)
				i1 -= getArrowScrollPreviewLength();
			if (!this.unhandleFullVisible) {
				if (localView.getRight() <= i1)
					return 0;
				if ((paramInt2 != -1) && (i1 - localView.getLeft() >= getMaxScrollAmount()))
					return 0;
			}
			i2 = localView.getRight() - i1;
			if (this.mFirstPosition + k == this.mItemCount) {
				i3 = getChildAt(k - 1).getRight() - i1;
				i2 = Math.min(i2, i3);
			}
			return Math.min(i2, getMaxScrollAmount());
		}
		if (paramInt1 == 17) {
			m = 0;
			if (paramInt2 != -1)
				m = paramInt2 - this.mFirstPosition;
			n = this.mFirstPosition + m;
			localView = getChildAt(m);
			i1 = j;
			if (n > 0)
				i1 += getArrowScrollPreviewLength();
			if (!this.unhandleFullVisible) {
				if (localView.getLeft() >= i1)
					return 0;
				if ((paramInt2 != -1) && (localView.getLeft() - i1 >= getMaxScrollAmount()))
					return 0;
			}
			i2 = i1 - localView.getLeft();
			if (this.mFirstPosition == 0) {
				i3 = i1 - getChildAt(0).getLeft();
				i2 = Math.min(i2, i3);
			}
			return Math.min(i2, getMaxScrollAmount());
		}
		return 0;
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
		this.mTotalHeight = 0;
		this.mHeightList.clear();
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
		int j = paramView.getRight() + this.mDividerWidth + this.mSpacing + this.mHorizontalSpacing;
		View localView = null;
		int k = 0;
		for (int m = 0; (m < this.mNumLines) && (i < this.mItemCount); m++) {
			if (localView != null)
				k += localView.getHeight();
			localView = obtainView(i, this.mIsScrap);
			setupChild(localView, i, j, false, this.mSpinnerPadding.top + k + this.mVerticalSpacing * m + this.mSpacing * m, false, this.mIsScrap[0]);
			i++;
		}
		return localView;
	}

	View addViewBackward(View paramView, int paramInt) {
		int i = paramInt + 1;
		int j = paramView.getLeft() - this.mDividerWidth - this.mSpacing - this.mHorizontalSpacing;
		View localView = null;
		int k = 0;
		for (int m = 0; (m < this.mNumLines) && (i < this.mItemCount); m++) {
			if (localView != null)
				k += localView.getHeight();
			localView = obtainView(i, this.mIsScrap);
			setupChild(localView, i, j, true, this.mSpinnerPadding.left + k + this.mVerticalSpacing * m + this.mSpacing * m, false, this.mIsScrap[0]);
			i++;
		}
		return localView;
	}

	View addViewFoward(View paramView, int paramInt1, int paramInt2) {
		int i = paramInt1;
		View localView = paramView;
		for (int j = 0; (j < paramInt2) && (i < this.mItemCount); j++) {
			localView = addViewFoward(localView, i);
			i += this.mNumLines;
		}
		return localView;
	}

	View addViewBackward(View paramView, int paramInt1, int paramInt2) {
		int i = paramInt1;
		View localView = paramView;
		for (int j = 0; (j < paramInt2) && (i >= 0); j++) {
			localView = addViewBackward(localView, i);
			i -= this.mNumLines;
		}
		return localView;
	}

	void scrollListItemsBy(int paramInt) {
		this.mFlingRunnable.endFling(false);
		this.mFlingRunnable.startScroll(paramInt, 300);
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
			localView2 = fillRight(paramInt1 - 1, localView1.getLeft() - i - this.mSpacing - this.mHorizontalSpacing);
			adjustViewsRightOrLeft();
			localView3 = fillLeft(paramInt1 + 1, localView1.getRight() + i + this.mSpacing + this.mHorizontalSpacing);
			j = getChildCount();
			if (j > 0)
				correctTooRight(j);
		} else {
			localView3 = fillLeft(paramInt1 + 1, localView1.getRight() + i + this.mSpacing + this.mHorizontalSpacing);
			adjustViewsRightOrLeft();
			localView2 = fillRight(paramInt1 - 1, localView1.getLeft() - i - this.mSpacing - this.mHorizontalSpacing);
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
				localView = this.mAdapter.getView(paramInt1, localView, this);
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
			i1 = localView.getLeft() - i - this.mHorizontalSpacing;
		} else {
			this.mFirstPosition = (n = this.mItemCount - 1);
			i1 = getRight() - getLeft() - getPaddingRight();
			this.mShouldStopFling = true;
		}
		while ((i1 > j) && (n < this.mItemCount)) {
			i1 = localView.getLeft() - i - this.mHorizontalSpacing;
			int i2 = 0;
			for (int i3 = this.mNumLines - 1; (i3 >= 0) && (n + i3 < this.mItemCount); i3--) {
				i2 += ((Integer) this.mHeightList.get(i3)).intValue();
				int i4 = this.mTotalHeight - i2;
				localView = makeAndAddView(n + i3, i1, false, this.mSpinnerPadding.top + i4 + this.mVerticalSpacing * i3 + i * i3, this.mSelectedPosition == n);
			}
			n -= this.mNumLines;
		}
	}

	private void fillToGalleryLeftLtr() {
		int i = this.mSpacing;
		int j = getPaddingLeft();
		View localView = getChildAt(0);
		int k;
		int m;
		if (localView != null) {
			k = this.mFirstPosition - this.mNumLines;
			m = localView.getLeft() - i - this.mHorizontalSpacing;
		} else {
			k = 0;
			m = getRight() - getLeft() - getPaddingRight();
			this.mShouldStopFling = true;
		}
		while ((m > j) && (k >= 0)) {
			this.mFirstPosition = k;
			m = localView.getLeft() - i - this.mHorizontalSpacing;
			int n = 0;
			for (int i1 = this.mNumLines - 1; (i1 >= 0) && (k + i1 < this.mItemCount); i1--) {
				n += ((Integer) this.mHeightList.get(i1)).intValue();
				int i2 = this.mTotalHeight - n;
				localView = makeAndAddView(k + i1, m, false, this.mSpinnerPadding.top + i2 + this.mVerticalSpacing * i1 + i * i1, this.mSelectedPosition == k);
			}
			k -= this.mNumLines;
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
		View localView1 = getChildAt(0);
		int k;
		int m;
		if (localView1 != null) {
			k = this.mFirstPosition - this.mNumLines;
			m = localView1.getRight() + i + this.mHorizontalSpacing;
		} else {
			k = 0;
			m = getPaddingLeft();
			this.mShouldStopFling = true;
		}
		while ((m < j) && (k >= 0)) {
			this.mFirstPosition = k;
			m = localView1.getRight() + i + this.mHorizontalSpacing;
			int n = 0;
			View localView2 = null;
			for (int i1 = 0; (i1 < this.mNumLines) && (k + i1 <= this.mItemCount); i1++) {
				if (localView2 != null)
					n += localView2.getHeight();
				localView1 = makeAndAddView(k + i1, m, true, this.mSpinnerPadding.top + n + this.mVerticalSpacing * i1 + i * i1, this.mSelectedPosition == k);
				localView2 = localView1;
			}
			k -= this.mNumLines;
		}
	}

	private void fillToGalleryRightLtr() {
		int i = this.mSpacing;
		int j = getRight() - getLeft() - getPaddingRight();
		int k = getChildCount();
		int m = this.mItemCount;
		View localView1 = getChildAt(k - 1);
		int n;
		int i1;
		if (localView1 != null) {
			n = this.mFirstPosition + k;
			i1 = localView1.getRight() + i + this.mHorizontalSpacing;
		} else {
			this.mFirstPosition = (n = this.mItemCount - 1);
			i1 = getPaddingLeft();
			this.mShouldStopFling = true;
		}
		while ((i1 < j) && (n < m)) {
			i1 = localView1.getRight() + i + this.mHorizontalSpacing;
			int i2 = 0;
			View localView2 = null;
			for (int i3 = 0; (i3 < this.mNumLines) && (n + i3 < m); i3++) {
				if (localView2 != null)
					i2 += localView2.getHeight();
				localView1 = makeAndAddView(n + i3, i1, true, this.mSpinnerPadding.top + i2 + this.mVerticalSpacing * i3 + i * i3, this.mSelectedPosition == n);
				localView2 = localView1;
			}
			n += this.mNumLines;
		}
	}

	private View fillLeft(int paramInt1, int paramInt2) {
		View localObject = null;
		int i = 0;
		View localView = null;
		for (int j = 0; (paramInt2 > i) && (paramInt1 >= 0); j = 0) {
			for (int k = 0; (k < this.mNumLines) && (paramInt1 > 0); k++) {
				boolean bool = paramInt1 == this.mSelectedPosition;
				if (localView != null)
					j += localView.getHeight();
				localView = makeAndAddView(paramInt1, paramInt2, false, this.mSpinnerPadding.top + j + this.mVerticalSpacing * k + this.mSpacing * k, bool);
				if (k > this.mHeightList.size() - 1) {
					this.mTotalHeight += localView.getHeight();
					this.mHeightList.add(Integer.valueOf(localView.getHeight()));
				}
				if (bool)
					localObject = localView;
				paramInt1--;
			}
			paramInt2 = localView.getLeft() - this.mDividerWidth;
			paramInt2 -= this.mHorizontalSpacing;
			paramInt2 -= this.mSpacing;
			localView = null;
		}
		this.mFirstPosition = (paramInt1 + this.mNumLines);
		return localObject;
	}

	private View fillRight(int paramInt1, int paramInt2) {
		View localObject = null;
		int i = getRight() - getLeft();
		View localView = null;
		for (int j = 0; (paramInt2 < i) && (paramInt1 < this.mItemCount); j = 0) {
			for (int k = 0; (k < this.mNumLines) && (paramInt1 < this.mItemCount); k++) {
				boolean bool = paramInt1 == this.mSelectedPosition;
				if (localView != null)
					j += localView.getHeight();
				localView = makeAndAddView(paramInt1, paramInt2, true, this.mSpinnerPadding.top + j + this.mVerticalSpacing * k + this.mSpacing * k, bool);
				if (k > this.mHeightList.size() - 1) {
					this.mTotalHeight += localView.getHeight();
					this.mHeightList.add(Integer.valueOf(localView.getHeight()));
				}
				if (localView != null)
					i = getMaxWidth(localView, i);
				if ((bool) || ((this.mSelectedPosition == -1) && (paramInt1 == 0))) {
					localObject = localView;
					if (localObject != null)
						resetItemLayout(localObject);
				}
				paramInt1++;
			}
			paramInt2 = localView.getRight() + this.mDividerWidth;
			paramInt2 += this.mHorizontalSpacing;
			paramInt2 += this.mSpacing;
			localView = null;
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

	int lookForSelectablePositionOnScreen(int paramInt) {
		int i = this.mFirstPosition;
		int j;
		int k;
		SpinnerAdapter localSpinnerAdapter;
		int m;
		if (paramInt == 66) {
			j = this.mSelectedPosition != -1 ? this.mSelectedPosition + this.mNumLines : i;
			if (j >= this.mAdapter.getCount())
				return -1;
			if (j < i)
				j = i;
			k = getLastVisiblePosition();
			localSpinnerAdapter = getAdapter();
			for (m = j; m <= k; m++)
				if (getChildAt(m - i).getVisibility() == 0)
					return m;
		} else if (paramInt == 17) {
			j = i + getChildCount() - 1;
			k = this.mSelectedPosition != -1 ? this.mSelectedPosition - this.mNumLines : i + getChildCount() - 1;
			if ((k < 0) || (k < 0))
				return -1;
			if (k > j)
				k = j;
			localSpinnerAdapter = getAdapter();
			for (m = k; m >= i; m--)
				if (getChildAt(m - i).getVisibility() == 0)
					return m;
		} else if (paramInt == 33) {
			if (this.mSelectedPosition % this.mNumLines == 0)
				return -1;
			j = i + getChildCount() - 1;
			k = this.mSelectedPosition != -1 ? this.mSelectedPosition - 1 : i + getChildCount() - 1;
			if ((k < 0) || (k < 0))
				return -1;
			if (k > j)
				k = j;
			localSpinnerAdapter = getAdapter();
			for (m = k; m >= i; m--)
				if (getChildAt(m - i).getVisibility() == 0)
					return m;
		} else if (paramInt == 130) {
			if (this.mSelectedPosition % this.mNumLines == this.mNumLines - 1)
				return -1;
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
		}
		return -1;
	}

	boolean arrowScrollImpl(int paramInt) {
		if (getChildCount() <= 0)
			return false;
		View localView1 = getSelectedView();
		int i = this.mSelectedPosition;
		int j = lookForSelectablePositionOnScreen(paramInt);
		if (j == -1)
			return false;
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
			scrollListItemsBy(paramInt == 17 ? -k : k);
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

	boolean moveLeft() {
		return (this.mItemCount > 0) && (this.mSelectedPosition - this.mNumLines >= 0) && (arrowScrollImpl(17));
	}

	boolean moveRight() {
		return (this.mItemCount > 0) && (this.mSelectedPosition + this.mNumLines < this.mItemCount - 1) && (arrowScrollImpl(66));
	}

	boolean moveUp() {
		return (this.mItemCount > 0) && (this.mSelectedPosition > 0) && (arrowScrollImpl(33));
	}

	boolean moveDown() {
		return (this.mItemCount > 0) && (this.mSelectedPosition + 1 < this.mItemCount - 1) && (arrowScrollImpl(130));
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if ((this.mAdapter == null) || (!this.mIsAttached))
			return false;
		switch (paramInt) {
		case 21:
			if (moveLeft()) {
				playSoundEffect(1);
				return true;
			}
			break;
		case 22:
			if (moveRight()) {
				playSoundEffect(3);
				return true;
			}
			break;
		case 19:
			if (moveUp()) {
				playSoundEffect(2);
				return true;
			}
			break;
		case 20:
			if (moveDown()) {
				playSoundEffect(4);
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