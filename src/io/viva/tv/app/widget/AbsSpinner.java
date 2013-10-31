package io.viva.tv.app.widget;

import java.lang.reflect.Method;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.SpinnerAdapter;

public abstract class AbsSpinner extends AdapterView<SpinnerAdapter> {
	private static final String TAG = "AbsSpinner";
	private static final boolean DEBUG = true;
	SpinnerAdapter mAdapter;
	int mHeightMeasureSpec;
	int mWidthMeasureSpec;
	int mItemWidth;
	int mItemHeight;
	boolean mBlockLayoutRequests;
	int mSelectionLeftPadding = 0;
	int mSelectionTopPadding = 0;
	int mSelectionRightPadding = 0;
	int mSelectionBottomPadding = 0;
	final Rect mSpinnerPadding = new Rect();
	final RecycleBin mRecycler = new RecycleBin();
	private DataSetObserver mDataSetObserver;
	private Rect mTouchFrame;
	boolean mAdapterHasStableIds;
	final boolean[] mIsScrap = new boolean[1];
	Drawable mDivider;
	int mDividerWidth;
	int mSelectedLeft = 0;
	int mSelectorPosition;
	Adapter mScalableAdapter;
	View mScalableView;
	int mScalableViewSpacing;
	final RecycleBin mScalableRecycler = new RecycleBin();
	Rect mSelectorRect = new Rect();
	boolean mIsAttached;
	int mResurrectToPosition = 0;
	int mLayoutMode = 0;
	static final int LAYOUT_NORMAL = 0;
	static final int LAYOUT_FORCE_TOP = 1;
	static final int LAYOUT_SET_SELECTION = 2;
	static final int LAYOUT_FORCE_BOTTOM = 3;
	static final int LAYOUT_SPECIFIC = 4;
	static final int LAYOUT_SYNC = 5;
	protected boolean needMeasureSelectedView = true;

	boolean shouldShowSelector() {
		return ((hasFocus()) && (!isInTouchMode())) || (touchModeDrawsInPressedState());
	}

	boolean touchModeDrawsInPressedState() {
		return false;
	}

	public AbsSpinner(Context paramContext) {
		super(paramContext);
		initAbsSpinner();
	}

	public AbsSpinner(Context paramContext, AttributeSet paramAttributeSet) {
		this(paramContext, paramAttributeSet, 0);
	}

	public AbsSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		initAbsSpinner();
	}

	private void initAbsSpinner() {
		setFocusable(true);
		setWillNotDraw(false);
	}

	public void setAdapter(Adapter paramAdapter) {
		if (null != this.mAdapter) {
			this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
			resetList();
		}
		this.mAdapter = ((SpinnerAdapter) paramAdapter);
		this.mOldSelectedPosition = -1;
		this.mOldSelectedRowId = -9223372036854775808L;
		this.mRecycler.clear();
		if (this.mAdapter != null) {
			this.mOldItemCount = this.mItemCount;
			this.mAdapterHasStableIds = this.mAdapter.hasStableIds();
			this.mItemCount = this.mAdapter.getCount();
			checkFocus();
			this.mDataSetObserver = new AdapterView.AdapterDataSetObserver();
			this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
			this.mRecycler.setViewTypeCount(this.mAdapter.getViewTypeCount());
			int i = initPosition();
			setSelectedPositionInt(i);
			setNextSelectedPositionInt(i);
			if (this.mItemCount == 0)
				checkSelectionChanged();
		} else {
			checkFocus();
			resetList();
			checkSelectionChanged();
		}
		requestLayout();
	}

	int initPosition() {
		return this.mItemCount > 0 ? 0 : -1;
	}

	void positionSelector(int paramInt, View paramView) {
		if (paramInt != -1) {
			this.mSelectorPosition = paramInt;
			setSelectedPositionInt(paramInt);
		}
		getFocusedRect(this.mSelectorRect);
		if ((paramView instanceof AbsListView.SelectionBoundsAdjuster)) {
			((AbsListView.SelectionBoundsAdjuster) paramView).adjustListItemSelectionBounds(this.mSelectorRect);
		}
		positionSelector(this.mSelectorRect.left, this.mSelectorRect.top, this.mSelectorRect.right, this.mSelectorRect.bottom);
	}

	private void positionSelector(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		if (!this.mSelectorRect.isEmpty())
			this.mSelectorRect.set(paramInt1 - this.mSelectionLeftPadding, paramInt2 - this.mSelectionTopPadding, paramInt3 + this.mSelectionRightPadding, paramInt4
					+ this.mSelectionBottomPadding);
	}

	public void setSelectorPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		this.mSelectionLeftPadding = paramInt1;
		this.mSelectionTopPadding = paramInt2;
		this.mSelectionRightPadding = paramInt3;
		this.mSelectionBottomPadding = paramInt4;
	}

	private void setupScalableView(View paramView1, View paramView2) {
		LayoutParams localLayoutParams = (LayoutParams) paramView2.getLayoutParams();
		if (localLayoutParams == null)
			localLayoutParams = (LayoutParams) generateDefaultLayoutParams();
		int i = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, localLayoutParams.height);
		int j = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, localLayoutParams.width);
		paramView1.measure(j, i);
		int k = paramView1.getMeasuredHeight();
		int m = paramView2.getLeft();
		int n = paramView2.getRight();
		int i1 = paramView2.getBottom() + this.mScalableViewSpacing;
		int i2 = i1 + k;
		paramView1.layout(m, i1, n, i2);
	}

	void setScalableView(int paramInt, View paramView) {
		if (this.mScalableAdapter != null) {
			View localView = this.mScalableAdapter.getView(paramInt, null, this);
			Log.d("AbsSpinner", " getScalableView position = " + paramInt + " child = " + getChildAt(paramInt));
			if ((localView != null) && (paramView != null)) {
				setupScalableView(localView, paramView);
				this.mScalableView = localView;
			} else {
				this.mScalableView = null;
			}
		}
	}

	int getItemWidth() {
		return this.mItemWidth;
	}

	int getItemHeight() {
		return this.mItemHeight;
	}

	void clearScalableView() {
		this.mScalableView = null;
	}

	public void setScalableAdapter(Adapter paramAdapter) {
		this.mScalableAdapter = paramAdapter;
		this.mScalableRecycler.clear();
	}

	public void setScalableViewSpacing(int paramInt) {
		this.mScalableViewSpacing = paramInt;
	}

	void resetList() {
		this.mDataChanged = false;
		this.mNeedSync = false;
		removeAllViewsInLayout();
		this.mOldSelectedPosition = -1;
		this.mOldSelectedRowId = -9223372036854775808L;
		this.mOldItemCount = this.mItemCount;
		this.mItemCount = 0;
		setSelectedPositionInt(-1);
		setNextSelectedPositionInt(-1);
		invalidate();
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		int i = View.MeasureSpec.getMode(paramInt1);
		this.mSpinnerPadding.left = getPaddingLeft();
		this.mSpinnerPadding.top = getPaddingTop();
		this.mSpinnerPadding.right = getPaddingRight();
		this.mSpinnerPadding.bottom = getPaddingBottom();
		if (this.mDataChanged)
			handleDataChanged();
		int m = 0;
		int n = 0;
		int i1 = 1;
		if (this.needMeasureSelectedView) {
			int i2 = getSelectedItemPosition();
			if ((i2 >= 0) && (this.mAdapter != null) && (i2 < this.mAdapter.getCount())) {
				View localView = this.mRecycler.getScrapView(i2);
				if (localView == null) {
					localView = this.mAdapter.getView(i2, null, this);
					if (localView != null)
						this.mRecycler.addScrapView(i2, localView);
				}
				if (localView != null) {
					if (localView.getLayoutParams() == null) {
						this.mBlockLayoutRequests = true;
						localView.setLayoutParams(generateDefaultLayoutParams());
						this.mBlockLayoutRequests = false;
					}
					measureChild(localView, paramInt1, paramInt2);
					m = getChildHeight(localView) + this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
					n = getChildWidth(localView) + this.mSpinnerPadding.left + this.mSpinnerPadding.right;
					i1 = 0;
				}
			}
		}
		if (i1 != 0) {
			m = this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
			if (i == 0)
				n = this.mSpinnerPadding.left + this.mSpinnerPadding.right;
		}
		m = Math.max(m, getSuggestedMinimumHeight());
		n = Math.max(n, getSuggestedMinimumWidth());
		int k = resolveSizeAndState(m, paramInt2, 0);
		int j = resolveSizeAndState(n, paramInt1, 0);
		setMeasuredDimension(j, k);
		this.mHeightMeasureSpec = paramInt2;
		this.mWidthMeasureSpec = paramInt1;
	}

	int getChildHeight(View paramView) {
		return paramView.getMeasuredHeight();
	}

	int getChildWidth(View paramView) {
		return paramView.getMeasuredWidth();
	}

	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new ViewGroup.LayoutParams(-1, -2);
	}

	void recycleAllViews() {
		int i = getChildCount();
		RecycleBin localRecycleBin = this.mRecycler;
		int j = this.mFirstPosition;
		for (int k = 0; k < i; k++) {
			View localView = getChildAt(k);
			int m = j + k;
			localRecycleBin.addScrapView(m, localView);
		}
	}

	public void setSelection(int paramInt, boolean paramBoolean) {
		boolean bool = (paramBoolean) && (this.mFirstPosition <= paramInt) && (paramInt <= this.mFirstPosition + getChildCount() - 1);
		setSelectionInt(paramInt, bool);
	}

	public void setSelection(int paramInt) {
		setNextSelectedPositionInt(paramInt);
		requestLayout();
		invalidate();
	}

	void setSelectionInt(int paramInt, boolean paramBoolean) {
		if (paramInt != this.mOldSelectedPosition) {
			this.mBlockLayoutRequests = true;
			int i = paramInt - this.mSelectedPosition;
			setNextSelectedPositionInt(paramInt);
			layout(i, paramBoolean);
			this.mBlockLayoutRequests = false;
		}
	}

	abstract void layout(int paramInt, boolean paramBoolean);

	public View getSelectedView() {
		if ((this.mItemCount > 0) && (this.mSelectedPosition >= 0))
			return getChildAt(this.mSelectedPosition - this.mFirstPosition);
		return null;
	}

	public void requestLayout() {
		if (!this.mBlockLayoutRequests)
			super.requestLayout();
	}

	public SpinnerAdapter getAdapter() {
		return this.mAdapter;
	}

	public int getCount() {
		return this.mItemCount;
	}

	void hideSelector() {
		if (this.mSelectedPosition != -1) {
			if (this.mLayoutMode != 4)
				this.mResurrectToPosition = this.mSelectedPosition;
			if ((this.mNextSelectedPosition >= 0) && (this.mNextSelectedPosition != this.mSelectedPosition))
				this.mResurrectToPosition = this.mNextSelectedPosition;
			setSelectedPositionInt(-1);
			setNextSelectedPositionInt(-1);
			this.mSelectedLeft = 0;
		}
	}

	public int pointToPosition(int paramInt1, int paramInt2) {
		Rect localRect = this.mTouchFrame;
		if (localRect == null) {
			this.mTouchFrame = new Rect();
			localRect = this.mTouchFrame;
		}
		int i = getChildCount();
		for (int j = i - 1; j >= 0; j--) {
			View localView = getChildAt(j);
			if (localView.getVisibility() == 0) {
				localView.getHitRect(localRect);
				if (localRect.contains(paramInt1, paramInt2))
					return this.mFirstPosition + j;
			}
		}
		return -1;
	}

	public Parcelable onSaveInstanceState() {
		Parcelable localParcelable = super.onSaveInstanceState();
		SavedState localSavedState = new SavedState(localParcelable);
		localSavedState.selectedId = getSelectedItemId();
		if (localSavedState.selectedId >= 0L)
			localSavedState.position = getSelectedItemPosition();
		else
			localSavedState.position = -1;
		return localSavedState;
	}

	public void onRestoreInstanceState(Parcelable paramParcelable) {
		SavedState localSavedState = (SavedState) paramParcelable;
		super.onRestoreInstanceState(localSavedState.getSuperState());
		if (localSavedState.selectedId >= 0L) {
			this.mDataChanged = true;
			this.mNeedSync = true;
			this.mSyncRowId = localSavedState.selectedId;
			this.mSyncPosition = localSavedState.position;
			this.mSyncMode = 0;
			requestLayout();
		}
	}

	static View retrieveFromScrap(SparseArray<View> paramSparseArray, int paramInt) {
		int i = paramSparseArray.size();
		if (i > 0) {
			for (int j = 0; j < i; j++) {
				int k = paramSparseArray.keyAt(j);
				View localView2 = (View) paramSparseArray.get(k);
				if (((LayoutParams) localView2.getLayoutParams()).scrappedFromPosition == paramInt) {
					paramSparseArray.remove(k);
					return localView2;
				}
			}
			View localView1 = (View) paramSparseArray.get(paramInt);
			paramSparseArray.remove(paramInt);
			return localView1;
		}
		return null;
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		this.mIsAttached = true;
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		this.mRecycler.clear();
		this.mIsAttached = false;
	}

	class RecycleBin {
		private SparseArray<View> mCurrentScrap;
		private SparseArray<View>[] mScrapViews;
		private SparseArray<View> mTransientStateViews;
		private int mViewTypeCount;
		Method reflectViewHasTransientStateMethod;

		public RecycleBin() {
			initReflectMethod();
		}

		public boolean shouldRecycleViewType(int paramInt) {
			return paramInt >= 0;
		}

		View getTransientStateView(int paramInt) {
			if (this.mTransientStateViews == null)
				return null;
			int i = this.mTransientStateViews.indexOfKey(paramInt);
			if (i < 0)
				return null;
			View localView = (View) this.mTransientStateViews.valueAt(i);
			this.mTransientStateViews.remove(paramInt);
			return localView;
		}

		public void addScrapView(int paramInt, View paramView) {
			AbsSpinner.LayoutParams localLayoutParams = (AbsSpinner.LayoutParams) paramView.getLayoutParams();
			if (localLayoutParams == null)
				return;
			localLayoutParams.scrappedFromPosition = paramInt;
			int i = localLayoutParams.viewType;
			if (this.mViewTypeCount == 1)
				this.mCurrentScrap.put(paramInt, paramView);
			else
				this.mScrapViews[i].put(paramInt, paramView);
		}

		private void initReflectMethod() {
			if (Build.VERSION.SDK_INT >= 16)
				try {
					this.reflectViewHasTransientStateMethod = View.class.getDeclaredMethod("hasTransientState", new Class[0]);
					this.reflectViewHasTransientStateMethod.setAccessible(true);
				} catch (NoSuchMethodException localNoSuchMethodException) {
					localNoSuchMethodException.printStackTrace();
				}
		}

		public boolean reflectViewHasTransientState(View paramView) {
			if ((Build.VERSION.SDK_INT >= 16) && (this.reflectViewHasTransientStateMethod != null))
				try {
					Boolean localBoolean = (Boolean) this.reflectViewHasTransientStateMethod.invoke(paramView, new Object[0]);
					return localBoolean.booleanValue();
				} catch (Exception localException) {
					localException.printStackTrace();
				}
			return false;
		}

		public void setViewTypeCount(int paramInt) {
			if (paramInt < 1)
				throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
			SparseArray[] arrayOfSparseArray = new SparseArray[paramInt];
			for (int i = 0; i < paramInt; i++)
				arrayOfSparseArray[i] = new SparseArray();
			this.mViewTypeCount = paramInt;
			this.mCurrentScrap = arrayOfSparseArray[0];
			this.mScrapViews = arrayOfSparseArray;
		}

		View getScrapView(int paramInt) {
			View localView = null;
			if (this.mViewTypeCount == 1)
				return AbsSpinner.retrieveFromScrap(this.mCurrentScrap, paramInt);
			int i = AbsSpinner.this.mAdapter.getItemViewType(paramInt);
			if ((i >= 0) && (i < this.mScrapViews.length))
				return AbsSpinner.retrieveFromScrap(this.mScrapViews[i], paramInt);
			return localView;
		}

		void clearTransientStateViews() {
			if (this.mTransientStateViews != null)
				this.mTransientStateViews.clear();
		}

		void clear() {
			int i;
			int j;
			if (this.mViewTypeCount == 1) {
				SparseArray<View> localObject = this.mCurrentScrap;
				i = localObject.size();
				for (j = 0; j < i; j++) {
					View localView1 = localObject.valueAt(j);
					if (localView1 != null)
						AbsSpinner.this.removeDetachedView(localView1, true);
				}
				localObject.clear();
			} else {
				SparseArray<View>[] localObject = this.mScrapViews;
				i = 0;
				for (j = 0; j < this.mViewTypeCount; j++) {
					SparseArray<View> localView1 = localObject[j];
					i = localView1.size();
					for (int k = 0; k < i; k++) {
						View localView2 = (View) localView1.valueAt(k);
						if (localView2 != null)
							AbsSpinner.this.removeDetachedView(localView2, true);
					}
					localView1.clear();
				}
			}
			clearTransientStateViews();
		}
	}

	public static class LayoutParams extends ViewGroup.LayoutParams {
		int viewType;
		int scrappedFromPosition;
		long itemId = -1L;

		@ViewDebug.ExportedProperty(category = "list")
		boolean forceAdd;

		public LayoutParams(Context context, AttributeSet paramAttributeSet) {
			super(context, paramAttributeSet);
		}

		public LayoutParams(int paramInt1, int paramInt2) {
			super(paramInt1, paramInt2);
		}

		public LayoutParams(int paramInt1, int paramInt2, int paramInt3) {
			super(paramInt1, paramInt2);
			this.viewType = paramInt3;
		}

		public LayoutParams(ViewGroup.LayoutParams paramLayoutParams) {
			super(paramLayoutParams);
		}
	}

	static class SavedState extends View.BaseSavedState {
		long selectedId;
		int position;
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator() {
			public AbsSpinner.SavedState createFromParcel(Parcel paramAnonymousParcel) {
				return new AbsSpinner.SavedState(paramAnonymousParcel);
			}

			public AbsSpinner.SavedState[] newArray(int paramAnonymousInt) {
				return new AbsSpinner.SavedState[paramAnonymousInt];
			}
		};

		SavedState(Parcelable paramParcelable) {
			super(paramParcelable);
		}

		private SavedState(Parcel paramParcel) {
			super(paramParcel);
			this.selectedId = paramParcel.readLong();
			this.position = paramParcel.readInt();
		}

		public void writeToParcel(Parcel paramParcel, int paramInt) {
			super.writeToParcel(paramParcel, paramInt);
			paramParcel.writeLong(this.selectedId);
			paramParcel.writeInt(this.position);
		}

		public String toString() {
			return "AbsSpinner.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " selectedId=" + this.selectedId + " position=" + this.position + "}";
		}
	}
}