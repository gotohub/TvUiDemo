package io.viva.tv.app.widget;

import io.viva.tv.app.widget.animation.Actor;
import io.viva.tv.app.widget.animation.PosInfo;
import io.viva.tv.app.widget.animation.Scene;
import io.viva.tv.app.widget.animation.TransInfo;
import io.viva.tv.app.widget.animation.VerticalPosInfo;
import io.viva.tv.app.widget.animation.ViewActor;
import io.viva.tv.ui.demo.R;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import android.widget.SpinnerAdapter;

class PokerFlow extends AbsSpinner implements GestureDetector.OnGestureListener {
	private static final String TAG = "PokerFlow";
	private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;
	private int mSpacing = 0;

	private int mAnimationDuration = 400;
	private int mGravity;
	private GestureDetector mGestureDetector;
	private int mDownTouchPosition;
	private View mDownTouchView;
	private FlingRunnable mFlingRunnable = new FlingRunnable();

	private int mPokerFlowZoomAnimationStatus = 0;
	public static final int POKER_FLOW_UNSELECTED_STATUS = 0;
	public static final int POKER_FLOW_ZOOM_OUT_START = 1;
	public static final int POKER_FLOW_ZOOM_OUT_END = 2;
	public static final int POKER_FLOW_ZOOM_IN_START = 3;
	public static final int POKER_FLOW_ZOOM_IN_END = 4;
	int mPokerFlowMoveAnimationStatus = 0;
	public static final int POKER_FLOW_MOVE_START = 1;
	public static final int POKER_FLOW_MOVE_END = 2;
	Rect mViewRealRect = new Rect();

	private Runnable mDisableSuppressSelectionChangedRunnable = new Runnable() {
		public void run() {
			PokerFlow.this.mSuppressSelectionChanged = false;
			PokerFlow.this.selectionChanged();
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
	private boolean mIsRtl = true;
	private int mTopMost;
	private int mBottomMost;
	private View delayedOffsetChild;
	private boolean isCommonStatus;
	int mGravityHeightAnchor;
	private int needDetachPos = -1;
	private static final int MAX_SELECTION_ITEM = 1;
	private static final int MIN_ITEM_COUNT = 2;
	private boolean mIsDrawShadow = true;
	private Bitmap mShadowNextBitmap;
	private Bitmap mShadowPreBitmap;
	private NinePatch mShadowNextNinePatch;
	private NinePatch mShadowPreNinePatch;
	private float mShadowRatio = 0.2F;

	private Paint mShadowPaint = new Paint();
	private static final int mShadowColorStart = -536870912;
	private static final int mShadowColorEnd = 0;
	private int mMoveStatus = -1;
	private static final int MOVE_INVALID = -1;
	private static final int MOVE_PREVIOUS = 0;
	private static final int MOVE_NEXT = 1;
	private static final boolean DEBUG_UI_LOG = false;
	int mduration;
	ZoomAnimator mZoomAnimator = new ZoomAnimator();

	ArrayList<Actor> mCopyHideActorList = new ArrayList();

	private PokerFlowAnimator mPokerFlowAnimator = new PokerFlowAnimator();

	public PokerFlow(Context context) {
		this(context, null);
	}

	public PokerFlow(Context context, AttributeSet attrs) {
		this(context, attrs, 16842864);
	}

	public PokerFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.mGestureDetector = new GestureDetector(context, this);
		this.mGestureDetector.setIsLongpressEnabled(true);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Gallery, defStyle, 0);

		int index = a.getInt(0, -1);

		if (index >= 0) {
			setGravity(index);
		}

		int animationDuration = a.getInt(1, -1);

		if (animationDuration > 0) {
			setAnimationDuration(animationDuration);
		}

		int spacing = a.getDimensionPixelOffset(2, 0);

		setSpacing(spacing);

		float unselectedAlpha = a.getFloat(3, 0.5F);

		setUnselectedAlpha(unselectedAlpha);

		a.recycle();

		setChildrenDrawingOrderEnabled(true);
		setStaticTransformationsEnabled(true);

		setFocusable(false);
		this.mduration = 500;
	}

	public void setCallbackDuringFling(boolean shouldCallback) {
		this.mShouldCallbackDuringFling = shouldCallback;
	}

	public void setCallbackOnUnselectedItemClick(boolean shouldCallback) {
		this.mShouldCallbackOnUnselectedItemClick = shouldCallback;
	}

	public void setAnimationDuration(int animationDurationMillis) {
		this.mAnimationDuration = animationDurationMillis;
	}

	public void setSpacing(int spacing) {
		this.mSpacing = spacing;
	}

	public void setUnselectedAlpha(float unselectedAlpha) {
	}

	private void setDelayedOffsetChild() {
		int realSelectindex = this.mSelectedPosition - this.mFirstPosition;

		if (this.mMoveStatus == 0) {
			this.delayedOffsetChild = getChildAt(realSelectindex - 1);
		} else if (this.mMoveStatus == 1) {
			this.delayedOffsetChild = getChildAt(realSelectindex + 1);
		}
	}

	public float getInOutCircleInterpolator(float input) {
		return (float) ((Math.sin(input * 3.141592653589793D - 1.570796326794897D) + 1.0D) * 0.5D);
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

	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof AbsSpinner.LayoutParams;
	}

	protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new AbsSpinner.LayoutParams(p);
	}

	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new AbsSpinner.LayoutParams(getContext(), attrs);
	}

	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new AbsSpinner.LayoutParams(-2, -2);
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		this.mInLayout = true;
		layout(0, false);
		this.mInLayout = false;
	}

	void trackMotionScroll(int deltaY) {
		if (getChildCount() == 0) {
			return;
		}

		boolean toTop = deltaY < 0;

		int limitedDeltaY = getLimitedMotionScrollAmount(toTop, deltaY);
		if (limitedDeltaY != deltaY) {
			this.mFlingRunnable.endFling(false);
			onFinishedMovement();
		}

		offsetChildrenTopAndBottom(limitedDeltaY);

		detachOffScreenChildren(toTop);

		if (toTop) {
			fillToPokerFlowBottomTtb();
		} else {
			fillToPokerFlowTopTtb();
		}

		this.mRecycler.clear();

		setSelectionToCenterChild();

		onScrollChanged(0, 0, 0, 0);

		invalidate();
	}

	int getLimitedMotionScrollAmount(boolean motionToTop, int deltaY) {
		int extremeItemPosition = motionToTop != this.mIsRtl ? this.mItemCount - 1 : 0;
		View extremeChild = getChildAt(extremeItemPosition - this.mFirstPosition);

		if (extremeChild == null) {
			return deltaY;
		}

		int extremeChildCenter = getCenterOfView(extremeChild);
		int pokerFlowCenter = getCenterOfPokerFlow();

		if (motionToTop) {
			if (extremeChildCenter <= pokerFlowCenter) {
				return 0;
			}
		} else if (extremeChildCenter >= pokerFlowCenter) {
			return 0;
		}

		int centerDifference = pokerFlowCenter - extremeChildCenter;

		return motionToTop ? Math.max(centerDifference, deltaY) : Math.min(centerDifference, deltaY);
	}

	public void offsetChildrenTopAndBottom(int offset) {
		for (int i = getChildCount() - 1; i >= 0; i--)
			getChildAt(i).offsetTopAndBottom(offset);
	}

	private int getCenterOfPokerFlow() {
		return (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();
	}

	private int getCenterOfPokerFlowX() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingTop();
	}

	private int getCenterOfPokerFlowY() {
		return (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();
	}

	private static int getCenterOfView(View view) {
		return view.getTop() + view.getHeight() / 2;
	}

	private static int getCenterOfViewX(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	private static int getCenterOfViewY(View view) {
		return view.getTop() + view.getHeight() / 2;
	}

	private void detachOffScreenChildren(boolean toTop) {
		int numChildren = getChildCount();
		int firstPosition = this.mFirstPosition;
		int start = 0;
		int count = 0;

		if (toTop) {
			int pokerFlowTop = getPaddingTop();
			for (int i = 0; i < numChildren; i++) {
				int n = this.mIsRtl ? numChildren - 1 - i : i;
				View child = getChildAt(n);
				if (child.getBottom() >= pokerFlowTop) {
					break;
				}
				start = n;
				count++;
				this.mRecycler.addScrapView(firstPosition + n, child);
			}

			if (!this.mIsRtl)
				start = 0;
		} else {
			int pokerFlowBottom = getHeight() - getPaddingBottom();
			for (int i = numChildren - 1; i >= 0; i--) {
				int n = this.mIsRtl ? numChildren - 1 - i : i;
				View child = getChildAt(n);
				if (child.getTop() <= pokerFlowBottom) {
					break;
				}
				start = n;
				count++;
				this.mRecycler.addScrapView(firstPosition + n, child);
			}

			if (this.mIsRtl) {
				start = 0;
			}
		}
		detachViewsFromParent(start, count);

		if (toTop != this.mIsRtl)
			this.mFirstPosition += count;
	}

	private void scrollIntoSlots() {
		if ((getChildCount() == 0) || (this.mSelectedChild == null)) {
			return;
		}
		int selectedCenter = getCenterOfView(this.mSelectedChild);
		int targetCenter = getCenterOfPokerFlow();

		int scrollAmount = targetCenter - selectedCenter;
		if (scrollAmount != 0)
			this.mFlingRunnable.startUsingDistance(scrollAmount);
		else
			onFinishedMovement();
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

	void checkSelectionChanged() {
		if ((this.mSelectedPosition != this.mOldSelectedPosition) || (this.mSelectedRowId != this.mOldSelectedRowId)) {
			onSelectionChanged(this.mOldSelectedPosition, this.mSelectedPosition, this.mOldSelectedRowId, this.mSelectedRowId);

			selectionChanged();
			this.mOldSelectedPosition = this.mSelectedPosition;
			this.mOldSelectedRowId = this.mSelectedRowId;
		}
	}

	void onSelectionChanged(int oldSelectedPosition, int newSelectedPosition, long oldSelectedRowId, long newSelectedRowId) {
		View newSelected = null;
		View oldSelected = null;
		if (oldSelectedPosition != -1) {
			oldSelected = getChildAt(oldSelectedPosition - this.mFirstPosition);
		}
		if (newSelectedPosition != -1) {
			newSelected = getChildAt(newSelectedPosition - this.mFirstPosition);
		}

		if ((newSelected != null) && (oldSelected != newSelected) && (oldSelected != null)) {
			startChangeAnimation(oldSelectedPosition, newSelectedPosition);
		}
	}

	private void setSelectionToCenterChild() {
		View selView = this.mSelectedChild;
		if (this.mSelectedChild == null) {
			return;
		}
		int pokerFlowCenter = getCenterOfPokerFlow();

		if ((selView.getTop() <= pokerFlowCenter) && (selView.getBottom() >= pokerFlowCenter)) {
			return;
		}

		int closestEdgeDistance = 2147483647;
		int newSelectedChildIndex = 0;
		for (int i = getChildCount() - 1; i >= 0; i--) {
			View child = getChildAt(i);

			if ((child.getTop() <= pokerFlowCenter) && (child.getBottom() >= pokerFlowCenter)) {
				newSelectedChildIndex = i;
				break;
			}

			int childClosestEdgeDistance = Math.min(Math.abs(child.getTop() - pokerFlowCenter), Math.abs(child.getBottom() - pokerFlowCenter));

			if (childClosestEdgeDistance < closestEdgeDistance) {
				closestEdgeDistance = childClosestEdgeDistance;
				newSelectedChildIndex = i;
			}
		}

		int newPos = this.mFirstPosition + newSelectedChildIndex;

		if (newPos != this.mSelectedPosition) {
			setSelectedPositionInt(newPos);
			setNextSelectedPositionInt(newPos);
			checkSelectionChanged();
		}
	}

	public int getPokerFlowZoomAnimationStatus() {
		return this.mPokerFlowZoomAnimationStatus;
	}

	int lookForSelectablePositionOnScreen(int direction) {
		int firstPosition = this.mFirstPosition;
		if (direction == 130) {
			int startPos = this.mSelectedPosition != -1 ? this.mSelectedPosition + 1 : firstPosition;

			if (startPos >= this.mAdapter.getCount()) {
				return -1;
			}
			if (startPos < firstPosition) {
				startPos = firstPosition;
			}

			int lastVisiblePos = getLastVisiblePosition();
			SpinnerAdapter adapter = getAdapter();
			for (int pos = startPos; pos <= lastVisiblePos; pos++)
				if (getChildAt(pos - firstPosition).getVisibility() == 0) {
					return pos;
				}
		} else if (direction == 33) {
			int last = firstPosition + getChildCount() - 1;
			int startPos = this.mSelectedPosition != -1 ? this.mSelectedPosition - 1 : firstPosition + getChildCount() - 1;

			if ((startPos < 0) || (startPos >= this.mAdapter.getCount())) {
				return -1;
			}
			if (startPos > last) {
				startPos = last;
			}

			SpinnerAdapter adapter = getAdapter();
			for (int pos = startPos; pos >= firstPosition; pos--) {
				if (getChildAt(pos - firstPosition).getVisibility() == 0) {
					return pos;
				}
			}
		}
		return -1;
	}

	boolean arrowScrollImpl(int direction) {
		if (getChildCount() <= 0) {
			return false;
		}
		View selectedView = getSelectedView();
		int selectedPos = this.mSelectedPosition;

		int nextSelectedPosition = lookForSelectablePositionOnScreen(direction);

		boolean needToRedraw = false;
		if (nextSelectedPosition != -1) {
			this.isCommonStatus = checkNeedScroll(direction, selectedPos, nextSelectedPosition);

			setSelectedPositionInt(nextSelectedPosition);
			setNextSelectedPositionInt(nextSelectedPosition);
			selectedView = getSelectedView();
			selectedPos = nextSelectedPosition;
			needToRedraw = true;
			checkSelectionChanged();
		}

		if ((nextSelectedPosition == -1) && (selectedView != null) && (!isViewAncestorOf(selectedView, this))) {
			selectedView = null;
			hideSelector();
		}

		if (needToRedraw) {
			if (!awakenScrollBars()) {
				postInvalidate();
			}
			return true;
		}

		return false;
	}

	boolean isViewAncestorOf(View child, View parent) {
		if (child == parent) {
			return true;
		}

		ViewParent theParent = child.getParent();
		return ((theParent instanceof ViewGroup)) && (isViewAncestorOf((View) theParent, parent));
	}

	private boolean checkNeedScroll(int direction, int selectedPos, int nextSelectedPosition) {
		int totalCount = this.mAdapter.getCount();
		int count = getChildCount();
		this.needDetachPos = -1;
		if (direction == 130) {
			int lastVisiblePos = getLastVisiblePosition();

			if ((nextSelectedPosition > 0) && (nextSelectedPosition < totalCount - 1)) {
				View last = getChildAt(count - 1);
				if (lastVisiblePos < totalCount - 1) {
					addViewBackward(last, lastVisiblePos);
				}

				if (nextSelectedPosition - this.mFirstPosition > 1) {
					this.needDetachPos = 0;
					return true;
				}
			} else if ((nextSelectedPosition == totalCount - 1) && (totalCount >= 3)) {
				this.needDetachPos = 0;
			} else if (nextSelectedPosition != -1)
				;
		} else if (direction == 33) {
			if (nextSelectedPosition > 0) {
				View first = getChildAt(0);

				if (this.mFirstPosition > 0) {
					addViewForward(first, this.mFirstPosition);
					this.mFirstPosition -= 1;
				}

				if (count > 2) {
					this.needDetachPos = (getChildCount() - 1);
					return true;
				}
			} else if ((nextSelectedPosition == 0) && (totalCount >= 3)) {
				this.needDetachPos = (getChildCount() - 1);
			} else if (nextSelectedPosition != -1)
				;
		}

		return false;
	}

	View addViewForward(View theView, int position) {
		int abovePosition = position - 1;
		View view = obtainView(abovePosition, this.mIsScrap);
		int edgeOfNewChild = theView.getLeft() - this.mDividerWidth;
		setupChild(view, abovePosition, edgeOfNewChild, false, this.mSpinnerPadding.left, false, this.mIsScrap[0]);

		return view;
	}

	View addViewBackward(View theView, int position) {
		int belowPosition = position + 1;
		View view = obtainView(belowPosition, this.mIsScrap);
		int edgeOfNewChild = theView.getBottom() + this.mDividerWidth;
		setupChild(view, belowPosition, edgeOfNewChild, true, this.mSpinnerPadding.left, false, this.mIsScrap[0]);

		return view;
	}

	View obtainView(int position, boolean[] isScrap) {
		isScrap[0] = false;

		View scrapView = this.mRecycler.getScrapView(position);
		View child;
		if (scrapView != null) {
			child = this.mAdapter.getView(position, scrapView, this);

			if (child != scrapView) {
				this.mRecycler.addScrapView(position, scrapView);
			} else {
				isScrap[0] = true;
			}
		} else {
			child = this.mAdapter.getView(position, null, this);
		}

		if (this.mAdapterHasStableIds) {
			ViewGroup.LayoutParams vlp = child.getLayoutParams();
			AbsSpinner.LayoutParams lp;
			if (vlp == null) {
				lp = (AbsSpinner.LayoutParams) generateDefaultLayoutParams();
			} else {
				if (!checkLayoutParams(vlp))
					lp = (AbsSpinner.LayoutParams) generateLayoutParams(vlp);
				else
					lp = (AbsSpinner.LayoutParams) vlp;
			}
			lp.itemId = this.mAdapter.getItemId(position);
			child.setLayoutParams(lp);
		}

		return child;
	}

	private void setupChild(View child, int position, int y, boolean flowBottom, int childrenLeft, boolean selected, boolean recycled) {
		boolean isSelected = (selected) && (shouldShowSelector());
		boolean updateChildSelected = isSelected != child.isSelected();

		boolean needToMeasure = (!recycled) || (updateChildSelected) || (child.isLayoutRequested());

		AbsSpinner.LayoutParams p = (AbsSpinner.LayoutParams) child.getLayoutParams();

		if (p == null) {
			p = (AbsSpinner.LayoutParams) generateDefaultLayoutParams();
		}
		p.viewType = this.mAdapter.getItemViewType(position);

		if ((recycled) && (!p.forceAdd)) {
			attachViewToParent(child, flowBottom ? -1 : 0, p);
		} else {
			p.forceAdd = false;

			addViewInLayout(child, flowBottom ? -1 : 0, p, true);
		}

		if (updateChildSelected) {
			child.setSelected(isSelected);
		}

		if (needToMeasure) {
			int childHeightSpec = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, p.height);

			int lpWidth = p.width;
			int childWidthSpec;
			if (lpWidth > 0) {
				childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpWidth, 1073741824);
			} else {
				childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
			}

			child.measure(childWidthSpec, childHeightSpec);
		} else {
			cleanupLayoutState(child);
		}

		int w = child.getMeasuredWidth();
		int h = child.getMeasuredHeight();
		int childTop = flowBottom ? y : y - h;

		if (needToMeasure) {
			int childRight = childrenLeft + w;
			int childBottom = childTop + h;
			child.layout(childrenLeft, childTop, childRight, childBottom);
		} else {
			child.offsetLeftAndRight(childrenLeft - child.getLeft());
			child.offsetTopAndBottom(childTop - child.getTop());
		}

		if ((recycled) && (((AbsSpinner.LayoutParams) child.getLayoutParams()).scrappedFromPosition != position)) {
			child.jumpDrawablesToCurrentState();
		}
	}

	void layout(int delta, boolean animate) {
		if (!canMove()) {
			return;
		}
		this.mIsRtl = false;

		int childrenTop = this.mSpinnerPadding.top;
		int childrenHeight = getBottom() - getTop() - this.mSpinnerPadding.top - this.mSpinnerPadding.bottom;

		if (this.mDataChanged) {
			handleDataChanged();
		}

		if (this.mItemCount == 0) {
			resetList();
			return;
		}

		if (this.mNextSelectedPosition >= 0) {
			setSelectedPositionInt(this.mNextSelectedPosition);
		}

		recycleAllViews();

		detachAllViewsFromParent();

		this.mTopMost = 0;
		this.mBottomMost = 0;

		this.mFirstPosition = this.mSelectedPosition;
		View sel = makeAndAddView(this.mSelectedPosition, 0, 0, true);

		int selectedOffset = childrenTop + childrenHeight / 2 - sel.getHeight() / 2;

		sel.offsetTopAndBottom(selectedOffset);
		if (sel != null) {
			resetItemLayout(sel);
		}
		fillToPokerFlowBottomTtb();
		fillToPokerFlowTopTtb();

		this.mRecycler.clear();

		invalidate();
		checkSelectionChanged();

		this.mDataChanged = false;
		this.mNeedSync = false;
		setNextSelectedPositionInt(this.mSelectedPosition);

		updateSelectedItemMetadata();
	}

	private void resetItemLayout(View selectedView) {
		this.mItemWidth = selectedView.getWidth();
		this.mItemHeight = selectedView.getHeight();
	}

	void printPokerFlowItemPos() {
		int childCount = getChildCount();
		int yv;
		for (int i = childCount - 1; i >= 0; i--) {
			View v = getChildAt(i);

			int xv = getCenterOfViewX(v);
			yv = getCenterOfViewY(v);
		}
	}

	void initialPokerFlowItemInCenter() {
		int childCount = getChildCount();

		float destX = getCenterOfPokerFlowX();
		float destY = getCenterOfPokerFlowY();
		int yv;
		for (int i = childCount - 1; i >= 0; i--) {
			View v = getChildAt(i);

			float originX = getCenterOfViewX(v);
			float originY = getCenterOfViewY(v);

			TransInfo trans = VerticalPosInfo.getInitialPosInfo(originX, originY, destX, destY, 1, v.getWidth(), 3, 1);

			bindTransToView(trans, v);

			int xv = getCenterOfViewX(v);
			yv = getCenterOfViewY(v);
		}

		setPokerFlowZoomAnimationStatus(4);
	}

	void initPokerFlowItemPos(boolean isZoomed) {
		int childCount = getChildCount();

		float destX = getCenterOfPokerFlowX();
		float destY = getCenterOfPokerFlowY();

		if (!canMove()) {
			return;
		}

		if (!isZoomed) {
			int yv;
			for (int i = childCount - 1; i >= 0; i--) {
				View v = getChildAt(i);

				float originX = getCenterOfViewX(v);
				float originY = getCenterOfViewY(v);

				TransInfo trans = VerticalPosInfo.getInitialPosInfo(originX, originY, destX, destY, this.mSelectedPosition - this.mFirstPosition, v.getWidth(), childCount,
						this.mSelectedPosition - this.mFirstPosition);

				bindTransToView(trans, v);

				int xv = getCenterOfViewX(v);
				yv = getCenterOfViewY(v);
			}

			setPokerFlowZoomAnimationStatus(4);
		} else {
			int yv;
			for (int i = childCount - 1; i >= 0; i--) {
				View v = getChildAt(i);

				float originX = getCenterOfViewX(v);
				float originY = getCenterOfViewY(v);

				TransInfo trans = VerticalPosInfo.getInitialPosInfo(originX, originY, destX, destY, i, v.getWidth(), childCount, this.mSelectedPosition - this.mFirstPosition);

				bindTransToView(trans, v);

				int xv = getCenterOfViewX(v);
				yv = getCenterOfViewY(v);
			}

			setPokerFlowZoomAnimationStatus(2);
		}
	}

	private void bindTransToView(TransInfo Info, View v) {
		if (v != null) {
			v.setScaleX(Info.scaleX);
			v.setScaleY(Info.scaleY);
			v.setTranslationX(Info.x);
			v.setTranslationY(Info.y);
			v.setAlpha(1.0F);
		}
	}

	private void fillToPokerFlowTopTtb() {
		int itemSpacing = this.mSpacing;
		int pokerFlowTop = getPaddingTop();

		View prevIterationView = getChildAt(0);
		int curBottomEdge;
		int curPosition;
		if (prevIterationView != null) {
			curPosition = this.mFirstPosition - 1;
			curBottomEdge = prevIterationView.getTop() - itemSpacing;
		} else {
			curPosition = 0;
			curBottomEdge = getBottom() - getTop() - getPaddingBottom();
			this.mShouldStopFling = true;
		}

		while ((curBottomEdge > pokerFlowTop) && (curPosition >= 0)) {
			prevIterationView = makeAndAddView(curPosition, curPosition - this.mSelectedPosition, curBottomEdge, false);

			this.mFirstPosition = curPosition;

			curBottomEdge = prevIterationView.getTop() - itemSpacing;
			curPosition--;
		}
	}

	private void fillToPokerFlowBottomTtb() {
		int itemSpacing = this.mSpacing;
		int pokerFlowBottom = getBottom() - getTop() - getPaddingBottom();
		int numChildren = getChildCount();
		int numItems = this.mItemCount;

		View prevIterationView = getChildAt(numChildren - 1);
		int curTopEdge;
		int curPosition;
		if (prevIterationView != null) {
			curPosition = this.mFirstPosition + numChildren;
			curTopEdge = prevIterationView.getBottom() + itemSpacing;
		} else {
			this.mFirstPosition = (curPosition = this.mItemCount - 1);
			curTopEdge = getPaddingTop();
			this.mShouldStopFling = true;
		}

		while ((curTopEdge < pokerFlowBottom) && (curPosition < numItems)) {
			prevIterationView = makeAndAddView(curPosition, curPosition - this.mSelectedPosition, curTopEdge, true);

			curTopEdge = prevIterationView.getBottom() + itemSpacing;
			curPosition++;
		}
	}

	private View makeAndAddView(int position, int offset, int y, boolean fromTop) {
		if (!this.mDataChanged) {
			View child = this.mRecycler.getScrapView(position);
			if (child != null) {
				int childTop = child.getTop();

				this.mBottomMost = Math.max(this.mBottomMost, childTop + child.getMeasuredHeight());

				this.mTopMost = Math.min(this.mTopMost, childTop);

				setUpChild(child, offset, y, fromTop);

				return child;
			}

		}

		View child = this.mAdapter.getView(position, null, this);

		setUpChild(child, offset, y, fromTop);

		return child;
	}

	private void setUpChild(View child, int offset, int y, boolean fromTop) {
		AbsSpinner.LayoutParams lp = (AbsSpinner.LayoutParams) child.getLayoutParams();

		if (lp == null) {
			lp = (AbsSpinner.LayoutParams) generateDefaultLayoutParams();
		}

		addViewInLayout(child, fromTop != this.mIsRtl ? -1 : 0, lp);

		child.setSelected(offset == 0);

		int childHeightSpec = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, lp.height);

		int childWidthSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, lp.width);

		child.measure(childWidthSpec, childHeightSpec);

		int childLeft = calculateLeft(child, true);
		int childRight = childLeft + child.getMeasuredWidth();

		int height = child.getMeasuredHeight();
		int childBottom;
		int childTop;
		if (fromTop) {
			childTop = y;
			childBottom = childTop + height;
		} else {
			childTop = y - height;
			childBottom = y;
		}

		child.layout(childLeft, childTop, childRight, childBottom);
	}

	private int calculateLeft(View child, boolean duringLayout) {
		int myWidth = duringLayout ? getMeasuredWidth() : getWidth();
		int childWidth = duringLayout ? child.getMeasuredWidth() : child.getWidth();

		int childLeft = 0;

		switch (this.mGravity) {
		case 3:
			childLeft = this.mSpinnerPadding.left;
			break;
		case 1:
			int availableSpace = myWidth - this.mSpinnerPadding.right - this.mSpinnerPadding.left - childWidth;

			childLeft = this.mSpinnerPadding.left + availableSpace / 2;
			break;
		case 5:
			childLeft = myWidth - this.mSpinnerPadding.right - childWidth;
		case 2:
		case 4:
		}
		return childLeft;
	}

	public boolean onTouchEvent(MotionEvent event) {
		boolean retValue = this.mGestureDetector.onTouchEvent(event);

		int action = event.getAction();
		if (action == 1) {
			onUp();
		} else if (action == 3) {
			onCancel();
		}

		return retValue;
	}

	public boolean onSingleTapUp(MotionEvent e) {
		if (this.mDownTouchPosition >= 0) {
			if ((this.mShouldCallbackOnUnselectedItemClick) || (this.mDownTouchPosition == this.mSelectedPosition)) {
				performItemClick(this.mDownTouchView, this.mDownTouchPosition, this.mAdapter.getItemId(this.mDownTouchPosition));
			}

			return true;
		}

		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return true;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return true;
	}

	public boolean onDown(MotionEvent e) {
		this.mFlingRunnable.stop(false);

		this.mDownTouchPosition = getDownViewPosition((int) e.getX(), (int) e.getY());
		int delta = this.mDownTouchPosition - this.mSelectedPosition;
		if (delta != 0) {
			if (delta == -1) {
				movePrevious();
			} else if (delta == 1) {
				moveNext();
			}
		}

		if (this.mDownTouchPosition >= 0) {
			this.mDownTouchView = getChildAt(this.mDownTouchPosition - this.mFirstPosition);
			this.mDownTouchView.setPressed(true);
		}

		this.mIsFirstScroll = true;

		return true;
	}

	private int getDownViewPosition(int x, int y) {
		int selPos = this.mSelectedPosition - this.mFirstPosition;

		this.mDownTouchPosition = -1;
		this.mDownTouchView = null;

		View sel = getChildAt(selPos);
		View pre = getChildAt(selPos - 1);
		View next = getChildAt(selPos + 1);
		if (isInViewRect(sel, x, y))
			this.mDownTouchPosition = this.mSelectedPosition;
		else if (isInViewRect(pre, x, y))
			this.mDownTouchPosition = (this.mSelectedPosition - 1);
		else if (isInViewRect(next, x, y)) {
			this.mDownTouchPosition = (this.mSelectedPosition + 1);
		}
		return this.mDownTouchPosition;
	}

	boolean isInViewRect(View v, int x, int y) {
		boolean isInView = false;
		if (v != null) {
			Rect rect = getRectOfView(v);
			isInView = rect.contains(x, y);

			this.mDownTouchView = v;
		}
		return isInView;
	}

	void onUp() {
		if (this.mDownTouchPosition != -1)
			;
		dispatchUnpress();
	}

	void onCancel() {
		onUp();
	}

	public void onLongPress(MotionEvent e) {
		if (this.mDownTouchPosition < 0) {
			return;
		}

		performHapticFeedback(0);
		long id = getItemIdAtPosition(this.mDownTouchPosition);
		dispatchLongPress(this.mDownTouchView, this.mDownTouchPosition, id);
	}

	public void onShowPress(MotionEvent e) {
	}

	private void dispatchPress(View child) {
		if (child != null) {
			child.setPressed(true);
		}

		setPressed(true);
	}

	private void dispatchUnpress() {
		for (int i = getChildCount() - 1; i >= 0; i--) {
			getChildAt(i).setPressed(false);
		}

		setPressed(false);
	}

	public void dispatchSetSelected(boolean selected) {
	}

	protected void dispatchSetPressed(boolean pressed) {
		if (this.mSelectedChild != null)
			this.mSelectedChild.setPressed(pressed);
	}

	protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
		return this.mContextMenuInfo;
	}

	public boolean showContextMenuForChild(View originalView) {
		int longPressPosition = getPositionForView(originalView);
		if (longPressPosition < 0) {
			return false;
		}

		long longPressId = this.mAdapter.getItemId(longPressPosition);
		return dispatchLongPress(originalView, longPressPosition, longPressId);
	}

	public boolean showContextMenu() {
		if ((isPressed()) && (this.mSelectedPosition >= 0)) {
			int index = this.mSelectedPosition - this.mFirstPosition;
			View v = getChildAt(index);
			return dispatchLongPress(v, this.mSelectedPosition, this.mSelectedRowId);
		}

		return false;
	}

	private boolean dispatchLongPress(View view, int position, long id) {
		boolean handled = false;

		if (this.mOnItemLongClickListener != null) {
			handled = this.mOnItemLongClickListener.onItemLongClick(this, this.mDownTouchView, this.mDownTouchPosition, id);
		}

		if (!handled) {
			this.mContextMenuInfo = new AdapterView.AdapterContextMenuInfo(view, position, id);
			handled = super.showContextMenuForChild(this);
		}

		if (handled) {
			performHapticFeedback(0);
		}

		return handled;
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		return event.dispatch(this, null, null);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 19:
			if (movePrevious()) {
				playSoundEffect(2);
				return true;
			}

			break;
		case 20:
			if (moveNext()) {
				playSoundEffect(4);
				return true;
			}

			break;
		case 23:
		case 66:
			this.mReceivedInvokeKeyDown = true;
		}

		return super.onKeyDown(keyCode, event);
	}

	void doZoomOutAnimation() {
		this.mZoomAnimator.startZoomAnimation(false);
	}

	void doZoomInAnimation() {
		if (this.mPokerFlowAnimator.mScene.isRunning()) {
			this.mPokerFlowAnimator.cancel();
		}

		this.mZoomAnimator.startZoomAnimation(true);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 23:
		case 66:
			if ((this.mReceivedInvokeKeyDown) && (this.mItemCount > 0)) {
				dispatchPress(this.mSelectedChild);
				postDelayed(new Runnable() {
					public void run() {
						PokerFlow.this.dispatchUnpress();
					}
				}, ViewConfiguration.getPressedStateDuration());

				int selectedIndex = this.mSelectedPosition - this.mFirstPosition;
				performItemClick(getChildAt(selectedIndex), this.mSelectedPosition, this.mAdapter.getItemId(this.mSelectedPosition));
			}

			this.mReceivedInvokeKeyDown = false;

			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	boolean movePrevious() {
		if ((this.mItemCount > 0) && (this.mSelectedPosition > 0)) {
			if (canMove()) {
				arrowScrollImpl(33);
			}
			return true;
		}
		ViewParent parent = getParent();
		if (((parent instanceof PokerGroupView)) && (((PokerGroupView) parent).mOnLastItemMoveListener != null)) {
			return ((PokerGroupView) parent).mOnLastItemMoveListener.OnLastItemMoveListener();
		}
		return false;
	}

	boolean moveNext() {
		if ((this.mItemCount > 0) && (this.mSelectedPosition < this.mItemCount - 1)) {
			if (canMove()) {
				arrowScrollImpl(130);
			}
			return true;
		}
		ViewParent parent = getParent();
		if (((parent instanceof PokerGroupView)) && (((PokerGroupView) parent).mOnLastItemMoveListener != null)) {
			return ((PokerGroupView) parent).mOnLastItemMoveListener.OnLastItemMoveListener();
		}
		return false;
	}

	private boolean scrollToChild(int childPosition) {
		View child = getChildAt(childPosition);

		if (child != null) {
			int distance = getCenterOfPokerFlow() - getCenterOfView(child);
			this.mFlingRunnable.startUsingDistance(distance);
			return true;
		}

		return false;
	}

	void setSelectedPositionInt(int position) {
		super.setSelectedPositionInt(position);

		updateSelectedItemMetadata();
	}

	private void updateSelectedItemMetadata() {
		View oldSelectedChild = this.mSelectedChild;

		View child = this.mSelectedChild = getChildAt(this.mSelectedPosition - this.mFirstPosition);

		if (child == null) {
			return;
		}

		if ((oldSelectedChild != null) && (oldSelectedChild != child)) {
			oldSelectedChild.setSelected(false);

			oldSelectedChild.setFocusable(false);
		}
	}

	public void setGravity(int gravity) {
		if (this.mGravity != gravity) {
			this.mGravity = gravity;
			requestLayout();
		}
	}

	public void getFocusedRect(Rect r) {
		View view = getSelectedView();
		if ((view != null) && (view.getParent() == this)) {
			view.getFocusedRect(r);
			offsetDescendantRectToMyCoords(view, r);
		} else {
			super.getFocusedRect(r);
		}
	}

	private int getRelSelectedPosition() {
		int selectPos = -1;

		if (this.mSelectedPosition == -1)
			;
		selectPos = this.mSelectedPosition;

		return selectPos;
	}

	private int getPreSelectedPosition() {
		return this.mPokerFlowAnimator.mOldSelectedPosition;
	}

	protected int getChildDrawingOrder(int childCount, int i) {
		int drawOrderIndex = 0;

		if ((this.mPokerFlowMoveAnimationStatus == 0) || (this.mPokerFlowMoveAnimationStatus == 2)) {
			drawOrderIndex = getDrawingOrder(childCount, getRelSelectedPosition() - this.mFirstPosition, i);
		} else if (this.mPokerFlowMoveAnimationStatus == 1) {
			float value = this.mPokerFlowAnimator.mScene.getCurrentValue();

			if (value > 0.5F) {
				drawOrderIndex = getDrawingOrder(childCount, getRelSelectedPosition() - this.mFirstPosition, i);
			} else
				drawOrderIndex = getDrawingOrder(childCount, getPreSelectedPosition() - this.mFirstPosition, i);
		} else {
			drawOrderIndex = getDrawingOrder(childCount, getRelSelectedPosition() - this.mFirstPosition, i);
		}

		return drawOrderIndex;
	}

	private int getDrawingOrder(int childCount, int selectedIndex, int i) {
		int subPoint = 0;
		int addPoint = 0;
		int result = childCount - 1;

		subPoint = selectedIndex;
		addPoint = selectedIndex;

		subPoint = selectedIndex;
		addPoint = selectedIndex;

		if (selectedIndex < 0) {
			return childCount - 1 - i;
		}

		if (selectedIndex > childCount - 1) {
			return i;
		}

		if (result == i) {
			return selectedIndex;
		}
		for (int v = 0; v <= childCount; v++) {
			subPoint--;
			if (subPoint >= 0) {
				result--;
				if (result == i) {
					return subPoint;
				}
			}
			addPoint++;
			if (addPoint <= childCount - 1) {
				result--;
				if (result == i) {
					return addPoint;
				}
			}
		}
		return i;
	}

	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		if ((gainFocus) && (this.mSelectedChild != null)) {
			this.mSelectedChild.setSelected(true);

			doZoomOutAnimation();
		}
	}

	void setPokerFlowMoveAnimationStatus(int aniStatus) {
		this.mPokerFlowMoveAnimationStatus = aniStatus;
	}

	void setPokerFlowZoomAnimationStatus(int aniStatus) {
		this.mPokerFlowZoomAnimationStatus = aniStatus;
	}

	boolean canMove() {
		if (((this.mPokerFlowMoveAnimationStatus == 2) || (this.mPokerFlowMoveAnimationStatus == 0))
				&& ((this.mPokerFlowZoomAnimationStatus == 0) || (this.mPokerFlowZoomAnimationStatus == 2) || (this.mPokerFlowZoomAnimationStatus == 4))) {
			return true;
		}

		return false;
	}

	private int projectNewtoOldIndex(int moveDirection, int newIndex) {
		if (moveDirection == 1) {
			return newIndex;
		}
		return newIndex - 1;
	}

	private void startChangeAnimation(int oldSelectedPosition, int newSelectedPosition) {
		this.mPokerFlowAnimator.startAnimation(oldSelectedPosition, newSelectedPosition);
	}

	private void detachViewafterAnimation() {
		int count = getChildCount();

		if ((this.needDetachPos != -1) && (count > 2)) {
			detachViewFromParent(this.needDetachPos);
			if (this.needDetachPos == 0) {
				this.mFirstPosition += 1;
			}
			this.needDetachPos = -1;
		}
	}

	void copyList(ArrayList<Actor> actorList) {
		this.mCopyHideActorList.clear();

		if (actorList != null) {
			int count = actorList.size();
			for (int i = 0; i < count; i++)
				this.mCopyHideActorList.add(actorList.get(i));
		}
	}

	void hideZoomInView(boolean isHide) {
		if (this.mCopyHideActorList != null) {
			int count = this.mCopyHideActorList.size();
			for (int i = 0; i < count; i++) {
				Actor actor = (Actor) this.mCopyHideActorList.get(i);
				if ((actor instanceof ViewActor)) {
					ViewActor vActor = (ViewActor) actor;

					View v = vActor.getView();

					if (isHide)
						v.setVisibility(4);
					else
						v.setVisibility(0);
				}
			}
		}
	}

	void hideCoveredView(ArrayList<Actor> actorList) {
		copyList(actorList);
		hideZoomInView(true);
	}

	void ShowHideCover() {
		hideZoomInView(false);
	}

	void updateParentReflectBitmap() {
	}

	public void printChildPos() {
		View v = null;
		int count = getChildCount();
		int pos;
		for (int i = 0; i < count; i++) {
			v = getChildAt(i);
			if (v != null)
				pos = getCenterOfView(v);
		}
	}

	public void setDrawShadowImage(boolean isDraw) {
		this.mIsDrawShadow = isDraw;
	}

	int getShadowWidth() {
		return (int) (getItemWidth() * this.mShadowRatio);
	}

	int getShadowHeight() {
		return (int) (getItemHeight() * this.mShadowRatio);
	}

	private Rect getShadowDrawUpDownRect(boolean isLeft, Rect rect, View view) {
		Rect drawRect = new Rect(rect);
		int shadowHeight = getShadowHeight();
		if (isLeft) {
			drawRect.bottom = (drawRect.top + 1);
			drawRect.top = (drawRect.bottom - (int) (shadowHeight * view.getScaleY()));
		} else {
			drawRect.top = (drawRect.bottom - 1);
			drawRect.bottom = (drawRect.top + (int) (shadowHeight * view.getScaleY()));
		}

		return drawRect;
	}

	private Rect getShadowDrawLeftRightRect(boolean isLeft, Rect rect, View view) {
		Rect drawRect = new Rect(rect);
		int shadowWidht = getShadowWidth();
		if (isLeft) {
			drawRect.right = (drawRect.left + 1);
			drawRect.left -= (int) (shadowWidht * view.getScaleX());
		} else {
			drawRect.left = (drawRect.right - 1);
			drawRect.right += (int) (shadowWidht * view.getScaleX());
		}

		return drawRect;
	}

	Rect getShadowPreClipRect(int index) {
		int leftIndex = index - 1;

		View vLeft = getChildAt(leftIndex);

		return getRectOfView(vLeft);
	}

	Rect getShadowNextClipRect(int index) {
		int leftIndex = index + 1;

		View vLeft = getChildAt(leftIndex);

		return getRectOfView(vLeft);
	}

	public void setImageShadowWidthRadio(float radio) {
		this.mShadowRatio = radio;
	}

	private void drawChildShadowAniRunning(Canvas canvas, View child, int indexChild, Rect rect) {
		int selIndex = getRelSelectedPosition();

		int OldSelIndex = selIndex - 1;

		boolean isPre = true;
		int alpha = 255;

		if (checkMoveToPre())
			OldSelIndex = selIndex + 1;
		else {
			OldSelIndex = selIndex - 1;
		}

		float value = getCurAnimatorValue();

		if (indexChild == selIndex) {
			alpha = (int) (255.0F * value);

			if (checkMoveToPre()) {
				renderChildShadowPre(canvas, child, indexChild, 255, rect);
				renderChildShadowNext(canvas, child, indexChild, alpha, rect);
			} else {
				renderChildShadowPre(canvas, child, indexChild, 255, rect);
				renderChildShadowNext(canvas, child, indexChild, 255, rect);
			}
		} else if (indexChild == OldSelIndex) {
			alpha = (int) (255.0F * (1.0F - value));

			if (checkMoveToPre()) {
				renderChildShadowPre(canvas, child, indexChild, alpha, rect);
				renderChildShadowNext(canvas, child, indexChild, 255, rect);
			} else {
				renderChildShadowPre(canvas, child, indexChild, 255, rect);
				renderChildShadowNext(canvas, child, indexChild, alpha, rect);
			}
		} else {
			if (selIndex < indexChild)
				isPre = false;
			else {
				isPre = true;
			}
			if (isPre)
				renderChildShadowPre(canvas, child, indexChild, 255, rect);
			else
				renderChildShadowNext(canvas, child, indexChild, 255, rect);
		}
	}

	private void renderChildShadowPre(Canvas canvas, View child, int index, int alpha, Rect rect) {
		Rect rectDrawPre = getShadowDrawUpDownRect(true, rect, child);

		Rect rectClipPre = getShadowPreClipRect(index - this.mFirstPosition);

		canvas.save();

		if (rectClipPre != null) {
			canvas.clipRect(rectClipPre);
			this.mShadowPaint.reset();
			this.mShadowPaint.setAlpha(alpha);

			LinearGradient shader = new LinearGradient(rectDrawPre.left, rectDrawPre.top, rectDrawPre.left, rectDrawPre.bottom, 0, -536870912, Shader.TileMode.CLAMP);

			this.mShadowPaint.setShader(shader);

			canvas.drawRect(rectDrawPre, this.mShadowPaint);
		}
		canvas.restore();
	}

	private void renderChildShadowNext(Canvas canvas, View child, int index, int alpha, Rect rect) {
		Rect rectDrawNext = getShadowDrawUpDownRect(false, rect, child);

		Rect rectClipNext = getShadowNextClipRect(index - this.mFirstPosition);

		canvas.save();

		if (rectClipNext != null) {
			canvas.clipRect(rectClipNext);
			this.mShadowPaint.reset();
			this.mShadowPaint.setAlpha(alpha);

			LinearGradient shader = new LinearGradient(rectDrawNext.left, rectDrawNext.top, rectDrawNext.left, rectDrawNext.bottom, -536870912, 0, Shader.TileMode.CLAMP);

			this.mShadowPaint.setShader(shader);
			canvas.drawRect(rectDrawNext, this.mShadowPaint);
		}
		canvas.restore();
	}

	private void offsetRect(Rect rect, int offset) {
		rect.set(rect.left, rect.top + offset, rect.right, rect.bottom + offset);
	}

	private void renderChildShadowNext_offset(Canvas canvas, View child, int alpha, int offset) {
		int index = getPositionForView(child);

		Rect rect = getRectOfView(child);

		Rect rectDrawNext = getShadowDrawUpDownRect(true, rect, child);

		Rect rectClipNext = getShadowNextClipRect(index - this.mFirstPosition);

		canvas.save();

		if (rectClipNext != null) {
			this.mShadowPaint.reset();
			this.mShadowPaint.setAlpha(255);

			offsetRect(rectDrawNext, offset);

			canvas.drawRect(rectDrawNext, this.mShadowPaint);

			LinearGradient shader = new LinearGradient(rectDrawNext.left, rectDrawNext.top, rectDrawNext.left, rectDrawNext.bottom, 0, -536870912, Shader.TileMode.CLAMP);

			this.mShadowPaint.setShader(shader);
			canvas.drawRect(rectDrawNext, this.mShadowPaint);
		}
		canvas.restore();
	}

	private void drawChildShadowAniNotRunning(Canvas canvas, View child, int index, Rect rect) {
		boolean isPre = true;
		int selIndex = getRelSelectedPosition();

		if (selIndex < index)
			isPre = false;
		else {
			isPre = true;
		}

		if (selIndex == index) {
			renderChildShadowPre(canvas, child, index, 255, rect);
			renderChildShadowNext(canvas, child, index, 255, rect);
		} else if (isPre) {
			renderChildShadowPre(canvas, child, index, 255, rect);
		} else {
			renderChildShadowNext(canvas, child, index, 255, rect);
		}
	}

	private boolean isAnimatorRunning() {
		return this.mPokerFlowAnimator.mScene.isRunning();
	}

	private boolean checkMoveToPre() {
		return this.mPokerFlowAnimator.mMoveDirection == 0;
	}

	private float getCurAnimatorValue() {
		return this.mPokerFlowAnimator.mScene.getCurrentValue();
	}

	private void drawChildShadow(Canvas canvas, View child, int index, Rect rect) {
		if (!isAnimatorRunning())
			drawChildShadowAniNotRunning(canvas, child, index, rect);
		else
			drawChildShadowAniRunning(canvas, child, index, rect);
	}

	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		if (this.mPokerFlowAnimator.mScene.isRunning())
			;
		boolean handler = super.drawChild(canvas, child, drawingTime);
		if (this.mIsDrawShadow) {
			int index = getPositionForView(child);
			getRectOfView(child, this.mViewRealRect);
			drawChildShadow(canvas, child, index, this.mViewRealRect);
		}
		return handler;
	}

	private Rect getRectOfView(int indexofView) {
		View v = getChildAt(indexofView);
		return getRectOfView(v);
	}

	private Rect getRectOfView(View v) {
		if (v != null) {
			Rect rect = new Rect();

			int l = v.getLeft();
			int r = v.getRight();

			int t = v.getTop();
			int b = v.getBottom();

			int xCenter = (l + r) / 2;
			int yCenter = (t + b) / 2;

			int width = r - l;
			int height = b - t;

			float xNewCenter = xCenter + v.getTranslationX();
			float yNewCenter = yCenter + v.getTranslationY();

			float newWidth = width * v.getScaleX();
			float newHeight = height * v.getScaleX();

			rect.left = ((int) (xNewCenter - newWidth / 2.0F));
			rect.right = ((int) (xNewCenter + newWidth / 2.0F));

			rect.top = ((int) (yNewCenter - newHeight / 2.0F));
			rect.bottom = ((int) (yNewCenter + newHeight / 2.0F));
			return rect;
		}

		return null;
	}

	private void getRectOfView(View v, Rect rect) {
		if (v == null) {
			return;
		}

		int l = v.getLeft();
		int r = v.getRight();

		int t = v.getTop();
		int b = v.getBottom();

		int xCenter = (l + r) / 2;
		int yCenter = (t + b) / 2;

		int width = r - l;
		int height = b - t;

		float xNewCenter = xCenter + v.getTranslationX();
		float yNewCenter = yCenter + v.getTranslationY();

		float newWidth = width * v.getScaleX();
		float newHeight = height * v.getScaleX();

		rect.left = ((int) (xNewCenter - newWidth / 2.0F));
		rect.right = ((int) (xNewCenter + newWidth / 2.0F));

		rect.top = ((int) (yNewCenter - newHeight / 2.0F));
		rect.bottom = ((int) (yNewCenter + newHeight / 2.0F));
	}

	class PokerFlowAnimator {
		int mMoveDirection;
		int mOldSelectedPosition;
		Scene mScene = new Scene();

		PokerFlowAnimator() {
		}

		private void startAnimation(int oldSelectedPosition, int newSelectedPosition) {
			if ((this.mScene == null) || (!this.mScene.isRunning())) {
				PokerFlow.this.setPokerFlowMoveAnimationStatus(1);

				this.mScene.initial();

				addActorToScene(oldSelectedPosition, newSelectedPosition);

				playScene();
			} else if (!this.mScene.isRunning())
				;
		}

		private void notifyParentSelector() {
			ViewParent viewParent = PokerFlow.this.getParent();

			if (viewParent != null) {
				PokerGroupView pokerGroupView = (PokerGroupView) viewParent;

				float value = 1.0F;

				if (this.mScene != null) {
					value = this.mScene.getCurrentValue();

					pokerGroupView.changeSelectorAlpha(value, PokerFlow.this);
				}
			}
		}

		private void playScene() {
			this.mScene.setDuration(PokerFlow.this.mduration);

			this.mScene.setInterpolator(AnimationUtils.loadInterpolator(PokerFlow.this.getContext(), 2114322434));

			this.mScene.addUpdateListener(new Scene.SceneUpdateListener() {
				public void OnSceneUpdate(ValueAnimator animation) {
					PokerFlow.this.postInvalidate();

					PokerFlow.PokerFlowAnimator.this.notifyParentSelector();
				}
			});
			this.mScene.addEndListener(new Scene.SceneEndListener() {
				public void OnSceneEnd(Animator animation) {
					PokerFlow.this.detachViewafterAnimation();

					PokerFlow.this.setPokerFlowMoveAnimationStatus(2);
				}
			});
			this.mScene.addStartListener(new Scene.SceneStartListener() {
				public void OnSceneStart(Animator animation) {
				}
			});
			this.mScene.addCancelListener(new Scene.SceneCancelListener() {
				public void onSceneCancel(Animator animation) {
				}
			});
			this.mScene.start();
		}

		public void cancel() {
			this.mScene.cancel();
		}

		private void addActorToScene(int oldSelectedPosition, int newSelectedPosition) {
			if (oldSelectedPosition <= newSelectedPosition)
				this.mMoveDirection = 1;
			else {
				this.mMoveDirection = 0;
			}

			this.mOldSelectedPosition = oldSelectedPosition;

			int viewSelectIndex = oldSelectedPosition;

			float destX = PokerFlow.this.getCenterOfPokerFlowX();
			float destY = PokerFlow.this.getCenterOfPokerFlowY();

			boolean isStable = PokerFlow.this.isCommonStatus;

			io.viva.tv.app.widget.animation.PosInfo pos = null;

			int count = PokerFlow.this.getChildCount();

			for (int i = 0; i < count; i++) {
				View v = PokerFlow.this.getChildAt(i);

				if (v != null) {
					float originX = PokerFlow.getCenterOfViewX(v);
					float originY = PokerFlow.getCenterOfViewY(v);

					if (isStable)
						pos = VerticalPosInfo.getAnimStablePos(originX, originY, destX, destY, this.mMoveDirection, i, v.getWidth());
					else {
						pos = VerticalPosInfo.getAnimUnstablePos(originX, originY, destX, destY, this.mMoveDirection, viewSelectIndex - PokerFlow.this.mFirstPosition, i,
								v.getWidth());
					}

					if (pos != null)
						this.mScene.addActor(new ViewActor(v, pos));
				}
			}
		}
	}

	class ZoomAnimator {
		Scene mZoomScene = new Scene();
		int selectItemIndexZoomOut;

		ZoomAnimator() {
		}

		private void playZoomScene() {
			this.mZoomScene.setDuration(PokerFlow.this.mduration);

			this.mZoomScene.addUpdateListener(new Scene.SceneUpdateListener() {
				public void OnSceneUpdate(ValueAnimator animation) {
					PokerFlow.this.postInvalidate();
				}
			});
			this.mZoomScene.addEndListener(new Scene.SceneEndListener() {
				public void OnSceneEnd(Animator animation) {
					if (PokerFlow.this.mPokerFlowZoomAnimationStatus == 1) {
						PokerFlow.this.setPokerFlowZoomAnimationStatus(2);
					}

					if (PokerFlow.this.mPokerFlowZoomAnimationStatus == 3) {
						PokerFlow.this.setPokerFlowZoomAnimationStatus(4);

						PokerFlow.this.hideCoveredView(PokerFlow.ZoomAnimator.this.mZoomScene.getActorList());
					}
				}
			});
			this.mZoomScene.start();
		}

		private void startZoomAnimation(boolean isZoomIn) {
			if (!isZoomIn) {
				this.selectItemIndexZoomOut = PokerFlow.this.getSelectedItemPosition();
			}

			if ((!isZoomIn) || (!isZoomIn)) {
				PokerFlow.this.ShowHideCover();
			}

			if ((this.mZoomScene == null) || (!this.mZoomScene.isRunning())) {
				if (isZoomIn)
					PokerFlow.this.setPokerFlowZoomAnimationStatus(3);
				else {
					PokerFlow.this.setPokerFlowZoomAnimationStatus(1);
				}

				this.mZoomScene.initial();

				if (isZoomIn)
					addZoomInActorToScene();
				else {
					addZoomOutActorToScene();
				}

				playZoomScene();
			} else if (!this.mZoomScene.isRunning())
				;
		}

		private void addZoomInActorToScene() {
			PokerFlow.this.setPokerFlowZoomAnimationStatus(3);

			float destX = PokerFlow.this.getCenterOfPokerFlowX();
			float destY = PokerFlow.this.getCenterOfPokerFlowY();

			boolean isStable = PokerFlow.this.isCommonStatus;
			PosInfo pos = null;
			int count = PokerFlow.this.getChildCount();

			for (int i = 0; i < count; i++) {
				View v = PokerFlow.this.getChildAt(i);

				if (v != null) {
					float originX = PokerFlow.getCenterOfViewX(v);
					float originY = PokerFlow.getCenterOfViewY(v);

					if (count == 3) {
						if (PokerFlow.this.mSelectedPosition - PokerFlow.this.mFirstPosition != i) {
							pos = VerticalPosInfo.getAnimZoomInPos(originX, originY, destX, destY, i, v.getWidth(), true, count, PokerFlow.this.mSelectedPosition
									- PokerFlow.this.mFirstPosition);

							if (pos != null)
								this.mZoomScene.addActor(new ViewActor(v, pos));
						}
					} else if ((count == 2) && (i != PokerFlow.this.mSelectedPosition - PokerFlow.this.mFirstPosition)) {
						pos = VerticalPosInfo.getAnimZoomInPos(originX, originY, destX, destY, i, v.getWidth(), true, count, PokerFlow.this.mSelectedPosition
								- PokerFlow.this.mFirstPosition);

						if (pos != null)
							this.mZoomScene.addActor(new ViewActor(v, pos));
					}
				}
			}
		}

		private void addZoomOutActorToScene() {
			PokerFlow.this.setPokerFlowZoomAnimationStatus(1);

			float destX = PokerFlow.this.getCenterOfPokerFlowX();
			float destY = PokerFlow.this.getCenterOfPokerFlowY();

			boolean isStable = PokerFlow.this.isCommonStatus;

			PosInfo pos = null;
			int count = PokerFlow.this.getChildCount();

			for (int i = 0; i < count; i++) {
				View v = PokerFlow.this.getChildAt(i);

				if (v != null) {
					float originX = PokerFlow.getCenterOfViewX(v);
					float originY = PokerFlow.getCenterOfViewY(v);

					if (count == 3) {
						if (PokerFlow.this.mSelectedPosition - PokerFlow.this.mFirstPosition != i) {
							pos = VerticalPosInfo.getAnimZoomInPos(originX, originY, destX, destY, i, v.getWidth(), false, count, PokerFlow.this.mSelectedPosition
									- PokerFlow.this.mFirstPosition);

							if (pos != null) {
								this.mZoomScene.addActor(new ViewActor(v, pos));
							}
						}

					} else if ((count == 2) && (i != PokerFlow.this.mSelectedPosition - PokerFlow.this.mFirstPosition)) {
						pos = VerticalPosInfo.getAnimZoomInPos(originX, originY, destX, destY, i, v.getWidth(), false, count, PokerFlow.this.mSelectedPosition
								- PokerFlow.this.mFirstPosition);

						if (pos != null)
							this.mZoomScene.addActor(new ViewActor(v, pos));
					}
				}
			}
		}
	}

	private class FlingRunnable implements Runnable {
		private Scroller mScroller;
		private int mLastFlingY;

		public FlingRunnable() {
			this.mScroller = new Scroller(PokerFlow.this.getContext(), new PokerFlow.InOutCircleInterpolator());
		}

		private void startCommon() {
			PokerFlow.this.removeCallbacks(this);
		}

		public void startUsingVelocity(int initialVelocity) {
			if (initialVelocity == 0)
				return;
			startCommon();
			int initialY = initialVelocity < 0 ? 2147483647 : 0;
			this.mLastFlingY = initialY;
			this.mScroller.fling(0, initialY, 0, initialVelocity, 0, 2147483647, 0, 2147483647);

			PokerFlow.this.post(this);
		}

		public void startUsingDistance(int distance) {
			if (distance == 0) {
				return;
			}

			startCommon();
			PokerFlow.this.setDelayedOffsetChild();

			this.mLastFlingY = 0;
			this.mScroller.startScroll(0, 0, 0, -distance, PokerFlow.this.mAnimationDuration);
			PokerFlow.this.post(this);
		}

		public void stop(boolean scrollIntoSlots) {
			PokerFlow.this.removeCallbacks(this);
			endFling(scrollIntoSlots);
		}

		private void endFling(boolean scrollIntoSlots) {
			this.mScroller.forceFinished(true);
			PokerFlow.this.delayedOffsetChild = null;
			if (scrollIntoSlots)
				PokerFlow.this.scrollIntoSlots();
		}

		public void run() {
			if (PokerFlow.this.mItemCount == 0) {
				endFling(true);
				return;
			}

			PokerFlow.this.mShouldStopFling = false;

			Scroller scroller = this.mScroller;
			boolean more = scroller.computeScrollOffset();
			int y = scroller.getCurrY();

			int delta = this.mLastFlingY - y;

			if (delta > 0) {
				PokerFlow.this.mDownTouchPosition = (PokerFlow.this.mIsRtl ? PokerFlow.this.mFirstPosition + PokerFlow.this.getChildCount() - 1 : PokerFlow.this.mFirstPosition);

				delta = Math.min(PokerFlow.this.getHeight() - PokerFlow.this.getPaddingTop() - PokerFlow.this.getPaddingBottom() - 1, delta);
			} else {
				int offsetToLast = PokerFlow.this.getChildCount() - 1;
				PokerFlow.this.mDownTouchPosition = (PokerFlow.this.mIsRtl ? PokerFlow.this.mFirstPosition : PokerFlow.this.mFirstPosition + offsetToLast);

				delta = Math.max(-(PokerFlow.this.getHeight() - PokerFlow.this.getPaddingBottom() - PokerFlow.this.getPaddingTop() - 1), delta);
			}

			PokerFlow.this.trackMotionScroll(delta);

			if ((more) && (!PokerFlow.this.mShouldStopFling)) {
				this.mLastFlingY = y;
				PokerFlow.this.post(this);
			} else {
				endFling(true);
			}
		}
	}

	class InOutCircleInterpolator extends DecelerateInterpolator {
		InOutCircleInterpolator() {
		}

		public float getInterpolation(float input) {
			return (float) ((Math.sin(input * 3.141592653589793D - 1.570796326794897D) + 1.0D) * 0.5D);
		}
	}

	class CircleOutInterpolator extends DecelerateInterpolator {
		CircleOutInterpolator() {
		}

		public float getInterpolation(float input) {
			return (float) Math.abs(Math.sqrt(1.0D - Math.pow(input - 1.0F, 2.0D)));
		}
	}
}
