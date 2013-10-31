package io.viva.tv.app.widget;

import java.util.LinkedList;
import java.util.List;

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
import android.view.animation.DecelerateInterpolator;

public class FocusedHorizontalGridView extends HorizontalGridView implements FocusedBasePositionManager.PositionInterface {
	private static final String TAG = "FocusedHorizontalGridView";
	private static final int SCROLLING_DURATION = 1300;
	private static final int SCROLLING_DELAY = 10;
	private static final int SCROLL_DURATION = 100;
	private static final int SCROLL_LEFT = 0;
	private static final int SCROLL_RIGHT = 1;
	public static final int HORIZONTAL_SINGEL = 1;
	public static final int HORIZONTAL_FULL = 2;
	private long KEY_INTERVEL = 150L;
	private long mKeyTime = 0L;
	private int mCurrentPosition = -1;
	private int mLastPosition = -1;
	private AbsHorizontalListView.OnScrollListener mOuterScrollListener;
	private boolean isScrolling = false;
	private Object lock = new Object();
	private int mStartX;
	private boolean mNeedScroll = false;
	private boolean mOutsieScroll = false;
	private FocusedGridPositionManager mPositionManager;
	private AdapterView.OnItemClickListener mOnItemClickListener = null;
	private FocusedBasePositionManager.FocusItemSelectedListener mOnItemSelectedListener = null;
	private int mFocusViewId = -1;
	private boolean mIsFocusInit = false;
	private boolean mInit = false;
	private int mHorizontalMode = 2;
	private int mScrollDirection = 1;
	private int mLastScrollDirection = 1;
	private int mScrollDuration = 1300;
	private FocusDrawListener mFocusDrawListener = null;
	private int mPaddingLeft = -1;
	private AbsHorizontalListView.OnScrollListener mOnScrollListener = new AbsHorizontalListView.OnScrollListener() {
		public void onScrollStateChanged(AbsHorizontalListView paramAnonymousAbsHorizontalListView, int paramAnonymousInt) {
			if (FocusedHorizontalGridView.this.mOuterScrollListener != null)
				FocusedHorizontalGridView.this.mOuterScrollListener.onScrollStateChanged(paramAnonymousAbsHorizontalListView, paramAnonymousInt);
			Log.d("FocusedHorizontalGridView", "onScrollStateChanged scrolling");
			switch (paramAnonymousInt) {
			case 1:
			case 2:
				FocusedHorizontalGridView.this.setScrolling(true);
				break;
			case 0:
				Log.d("FocusedHorizontalGridView", "onScrollStateChanged idle mNeedScroll = " + FocusedHorizontalGridView.this.mNeedScroll + ", mSpinnerPadding.left = "
						+ FocusedHorizontalGridView.this.mSpinnerPadding.left);
				if (FocusedHorizontalGridView.this.mNeedScroll) {
					FocusedHorizontalGridView.this.mNeedScroll = false;
					if ((FocusedHorizontalGridView.this.mHorizontalMode == 2) && (FocusedHorizontalGridView.this.mScrollList.size() <= 0)) {
						FocusedHorizontalGridView.this.mBlockLayoutRequests = true;
						FocusedHorizontalGridView.this.setPadding(FocusedHorizontalGridView.this.mPaddingLeft, FocusedHorizontalGridView.this.mSpinnerPadding.top,
								FocusedHorizontalGridView.this.mSpinnerPadding.right, FocusedHorizontalGridView.this.mSpinnerPadding.bottom);
						FocusedHorizontalGridView.this.mBlockLayoutRequests = false;
						FocusedHorizontalGridView.this.mPaddingLeft = -1;
					}
				}
				FocusedHorizontalGridView.this.setScrolling(false);
				break;
			}
		}

		public void onScroll(AbsHorizontalListView paramAnonymousAbsHorizontalListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
			if (FocusedHorizontalGridView.this.mOuterScrollListener != null)
				FocusedHorizontalGridView.this.mOuterScrollListener.onScroll(paramAnonymousAbsHorizontalListView, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
		}
	};
	public static int FOCUS_ITEM_REMEMBER_LAST = 0;
	public static int FOCUS_ITEM_AUTO_SEARCH = 1;
	private int focusPositionMode = FOCUS_ITEM_REMEMBER_LAST;
	boolean isKeyDown = false;
	int mScrollDistance = 0;
	List<Integer> mScrollList = new LinkedList();
	int mScrollX = 0;
	private static final int DRAW_FOCUS = 1;
	Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			switch (paramAnonymousMessage.what) {
			case 1:
				Log.d("FocusedHorizontalGridView", "Handler handleMessage");
				if (FocusedHorizontalGridView.this.getSelectedView() != null) {
					FocusedHorizontalGridView.this.performItemSelect(FocusedHorizontalGridView.this.getSelectedView(), FocusedHorizontalGridView.this.mCurrentPosition, true);
				} else {
					sendEmptyMessageDelayed(1, 10L);
					return;
				}
				FocusedHorizontalGridView.this.mPositionManager.setContrantNotDraw(false);
				FocusedHorizontalGridView.this.mPositionManager.setScaleCurrentView(true);
				FocusedHorizontalGridView.this.mPositionManager.setTransAnimation(false);
				FocusedHorizontalGridView.this.mPositionManager.setScaleLastView(true);
				FocusedHorizontalGridView.this.mPositionManager.setNeedDraw(true);
				FocusedHorizontalGridView.this.mPositionManager.setSelectedView(FocusedHorizontalGridView.this.getSelectedView());
				if (!FocusedHorizontalGridView.this.isScrolling())
					FocusedHorizontalGridView.this.invalidate();
				break;
			}
		}
	};
	private onKeyDownListener onKeyDownListener;

	public void setFocusDrawListener(FocusDrawListener paramFocusDrawListener) {
		this.mFocusDrawListener = paramFocusDrawListener;
	}

	public void setScrollDuration(int paramInt) {
		this.mScrollDuration = paramInt;
	}

	public void setHorizontalMode(int paramInt) {
		this.mHorizontalMode = paramInt;
	}

	public void setOutsideSroll(boolean paramBoolean) {
		Log.d("FocusedHorizontalGridView", new StringBuilder().append("setOutsideSroll scroll = ").append(paramBoolean).toString());
		this.mOutsieScroll = paramBoolean;
	}

	public void setFocusViewId(int paramInt) {
		this.mFocusViewId = paramInt;
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener) {
		this.mOnItemClickListener = paramOnItemClickListener;
	}

	public void setOnItemSelectedListener(FocusedBasePositionManager.FocusItemSelectedListener paramFocusItemSelectedListener) {
		this.mOnItemSelectedListener = paramFocusItemSelectedListener;
	}

	public void setScale(boolean paramBoolean) {
		this.mPositionManager.setScale(paramBoolean);
	}

	public void setFrameRate(int paramInt) {
		this.mPositionManager.setFrameRate(paramInt);
	}

	public void setFrameRate(int paramInt1, int paramInt2) {
		this.mPositionManager.setFrameRate(paramInt1, paramInt2);
	}

	public void getFocusedRect(Rect paramRect) {
		super.getFocusedRect(paramRect);
		if (this.mPaddingLeft < 0) {
			paramRect.left += getPaddingLeft();
			paramRect.right += getPaddingLeft();
		} else {
			paramRect.left += this.mPaddingLeft;
			paramRect.right += this.mPaddingLeft;
		}
	}

	public FocusedHorizontalGridView(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public FocusedHorizontalGridView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	public FocusedHorizontalGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext);
	}

	private void setScrolling(boolean paramBoolean) {
		synchronized (this.lock) {
			this.isScrolling = paramBoolean;
		}
	}

	private boolean isScrolling() {
		synchronized (this.lock) {
			return this.isScrolling;
		}
	}

	private void initLeftPosition() {
		if (!this.mInit) {
			this.mInit = true;
			int[] arrayOfInt = new int[2];
			getLocationOnScreen(arrayOfInt);
			this.mStartX = (arrayOfInt[0] + getPaddingLeft());
			Log.d("FocusedHorizontalGridView", new StringBuilder().append("initLeftPosition mStartX = ").append(this.mStartX).toString());
		}
	}

	public void init(Context paramContext) {
		setChildrenDrawingOrderEnabled(true);
		this.mPositionManager = new FocusedGridPositionManager(paramContext, this);
		setFlingInterpolator(new DecelerateInterpolator());
		super.setOnScrollListener(this.mOnScrollListener);
	}

	protected int getChildDrawingOrder(int paramInt1, int paramInt2) {
		int i = getSelectedItemPosition() - getFirstVisiblePosition();
		if (i < 0)
			return paramInt2;
		if (paramInt2 < i)
			return paramInt2;
		if (paramInt2 >= i)
			return paramInt1 - 1 - paramInt2 + i;
		return paramInt2;
	}

	public void dispatchDraw(Canvas paramCanvas) {
		Log.i("FocusedHorizontalGridView", new StringBuilder().append("dispatchDraw child count = ").append(getChildCount()).append(", mOutsieScroll = ")
				.append(this.mOutsieScroll).toString());
		long l = System.currentTimeMillis();
		super.dispatchDraw(paramCanvas);
		if (this.mFocusDrawListener != null) {
			paramCanvas.save();
			this.mFocusDrawListener.beforFocusDraw(paramCanvas);
			paramCanvas.restore();
		}
		if ((this.mPositionManager.getSelectedView() == null) && (getSelectedView() != null) && (hasFocus())) {
			this.mPositionManager.setSelectedView(getSelectedView());
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		}
		if (getSelectedView() != null) {
			this.mPositionManager.drawFrame(paramCanvas);
			if (this.mOutsieScroll)
				invalidate();
			if (hasFocus())
				focusInit();
		}
		Log.w("FocusedHorizontalGridView", new StringBuilder().append("dispatchDraw time cost = ").append(System.currentTimeMillis() - l).toString());
	}

	public void subSelectPosition() {
		arrowScroll(17);
	}

	public void setSelection(int paramInt) {
		Log.d("FocusedHorizontalGridView", "setSelection");
		this.mLastPosition = this.mCurrentPosition;
		this.mCurrentPosition = paramInt;
		this.mPositionManager.setSelectedView(getSelectedView());
		this.mPositionManager.setLastSelectedView(null);
		this.mPositionManager.setScaleLastView(false);
		this.mPositionManager.setScaleCurrentView(true);
		performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		this.mPositionManager.setNeedDraw(true);
		this.mPositionManager.setState(1);
	}

	public void setOnScrollListener(AbsHorizontalListView.OnScrollListener paramOnScrollListener) {
		this.mOuterScrollListener = paramOnScrollListener;
	}

	public void setFocusPositionMode(int paramInt) {
		this.focusPositionMode = paramInt;
	}

	public int getFocusPositionMode() {
		return this.focusPositionMode;
	}

	protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
		Log.d("FocusedHorizontalGridView",
				new StringBuilder().append("onFocusChanged,gainFocus:").append(paramBoolean).append(", mCurrentPosition = ").append(this.mCurrentPosition)
						.append(", child count = ").append(getChildCount()).toString());
		if (this.focusPositionMode == FOCUS_ITEM_AUTO_SEARCH) {
			Log.i("FocusedHorizontalGridView", "focusaaa,super.onFocusChanged1");
			super.onFocusChanged(paramBoolean, paramInt, paramRect);
		}
		synchronized (this) {
			this.mKeyTime = System.currentTimeMillis();
		}
		if (paramBoolean != this.mPositionManager.hasFocus())
			this.mIsFocusInit = false;
		this.mPositionManager.setFocus(paramBoolean);
		focusInit();
		initLeftPosition();
	}

	private void focusInit() {
		if (this.mIsFocusInit)
			return;
		Log.d("FocusedHorizontalGridView", new StringBuilder().append("focusInit mCurrentPosition = ").append(this.mCurrentPosition).append(", getSelectedItemPosition() = ")
				.append(getSelectedItemPosition()).toString());
		if (this.mCurrentPosition < 0)
			this.mCurrentPosition = getSelectedItemPosition();
		if (this.mCurrentPosition < 0)
			this.mCurrentPosition = 0;
		if (!hasFocus()) {
			this.mPositionManager.drawFrame(null);
			this.mPositionManager.setSelectedView(null);
			this.mLastPosition = this.mCurrentPosition;
			this.mPositionManager.setFocusDrawableVisible(false, true);
			this.mPositionManager.setFocusDrawableShadowVisible(false, true);
			this.mPositionManager.setTransAnimation(false);
			this.mPositionManager.setScaleCurrentView(false);
			this.mPositionManager.setScaleLastView(true);
		} else {
			if (this.focusPositionMode == FOCUS_ITEM_AUTO_SEARCH) {
				Log.i("FocusedHorizontalGridView", "focusaaa,super.onFocusChanged2");
				this.mCurrentPosition = super.getSelectedItemPosition();
			} else {
				Log.i("FocusedHorizontalGridView",
						new StringBuilder().append("onfocus setSelection:")
								.append((this.mCurrentPosition > -1) && (this.mCurrentPosition < getCount()) ? this.mCurrentPosition : 0).toString());
				setSelection((this.mCurrentPosition > -1) && (this.mCurrentPosition < getCount()) ? this.mCurrentPosition : 0);
			}
			this.mPositionManager.setLastSelectedView(null);
			this.mPositionManager.setScaleLastView(false);
			this.mPositionManager.setScaleCurrentView(true);
		}
		if (getSelectedView() != null) {
			this.mPositionManager.setSelectedView(getSelectedView());
			performItemSelect(getSelectedView(), this.mCurrentPosition, hasFocus());
			if (this.mCurrentPosition >= 0)
				this.mIsFocusInit = true;
		}
		this.mPositionManager.setNeedDraw(true);
		this.mPositionManager.setState(1);
		if (!isScrolling())
			invalidate();
	}

	public void setItemScaleValue(float paramFloat1, float paramFloat2) {
		this.mPositionManager.setItemScaleValue(paramFloat1, paramFloat2);
	}

	public int getSelectedItemPosition() {
		return this.mCurrentPosition;
	}

	public int getLastSelectedItemPosition() {
		return this.mLastPosition;
	}

	public View getSelectedView() {
		if (getChildCount() <= 0)
			return null;
		int i = this.mCurrentPosition;
		if ((i < getFirstVisiblePosition()) || (i > getLastVisiblePosition())) {
			Log.w("FocusedHorizontalGridView",
					new StringBuilder().append("getSelectedView mCurrentPosition = ").append(this.mCurrentPosition).append(", getFirstVisiblePosition() = ")
							.append(getFirstVisiblePosition()).append(", getLastVisiblePosition() = ").append(getLastVisiblePosition()).toString());
			return null;
		}
		int j = i - getFirstVisiblePosition();
		View localView = getChildAt(j);
		Log.d("FocusedHorizontalGridView",
				new StringBuilder().append("getSelectedView getSelectedView: mCurrentPosition = ").append(this.mCurrentPosition).append(", indexOfView = ").append(j)
						.append(", child count = ").append(getChildCount()).append(", getFirstVisiblePosition() = ").append(getFirstVisiblePosition())
						.append(", getLastVisiblePosition() = ").append(getLastVisiblePosition()).toString());
		return localView;
	}

	private void performItemSelect(View paramView, int paramInt, boolean paramBoolean) {
		if (this.mOnItemSelectedListener != null)
			this.mOnItemSelectedListener.onItemSelected(paramView, paramInt, paramBoolean, this);
	}

	private void performItemClick() {
		View localView = getSelectedView();
		if ((localView != null) && (this.mOnItemClickListener != null))
			this.mOnItemClickListener.onItemClick(this, localView, this.mCurrentPosition, 0L);
	}

	public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
		if ((getSelectedView() != null) && (getSelectedView().onKeyUp(paramInt, paramKeyEvent))) {
			this.isKeyDown = false;
			return true;
		}
		if ((paramInt == 23) || (paramInt == 66)) {
			if (this.isKeyDown)
				performItemClick();
			this.isKeyDown = false;
			return true;
		}
		this.isKeyDown = false;
		return super.onKeyUp(paramInt, paramKeyEvent);
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if (paramKeyEvent.getRepeatCount() == 0)
			this.isKeyDown = true;
		if ((this.onKeyDownListener != null) && (this.onKeyDownListener.onKeyDown(paramInt, paramKeyEvent)))
			return true;
		if ((paramInt != 23) && (paramInt != 66) && (paramInt != 21) && (paramInt != 22) && (paramInt != 20) && (paramInt != 19))
			return super.onKeyDown(paramInt, paramKeyEvent);
		Log.d("FocusedHorizontalGridView",
				new StringBuilder().append("onKeyDown keyCode = ").append(paramInt).append(", child count = ").append(getChildCount()).append(", mCurrentPosition = ")
						.append(this.mCurrentPosition).toString());
		synchronized (this) {
			if (this.mPositionManager.getState() == 1) {
				Log.d("FocusedHorizontalGridView",
						new StringBuilder().append("onKeyDown KyeInterval = ").append(System.currentTimeMillis() - this.mKeyTime).append(", getState() = ")
								.append(this.mPositionManager.getState()).append(", isScrolling() = ").append(isScrolling()).toString());
				return true;
			}
			if ((isScrolling())
					&& ((this.mCurrentPosition < getFirstVisiblePosition()) || (this.mCurrentPosition > getLastVisiblePosition()) || (System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL)))
				return true;
			this.mKeyTime = System.currentTimeMillis();
		}
		if (getChildCount() <= 0)
			return true;
		if ((getSelectedView() != null) && (getSelectedView().onKeyDown(paramInt, paramKeyEvent)))
			return true;
		if ((this.mHorizontalMode == 2) && (isScrolling()))
			return true;
		switch (paramInt) {
		case 19:
			if (!arrowScroll(33))
				return super.onKeyDown(paramInt, paramKeyEvent);
			return true;
		case 20:
			if (!arrowScroll(130))
				return super.onKeyDown(paramInt, paramKeyEvent);
			return true;
		case 21:
			if (!arrowScroll(17))
				return super.onKeyDown(paramInt, paramKeyEvent);
			return true;
		case 22:
			if (!arrowScroll(66))
				return super.onKeyDown(paramInt, paramKeyEvent);
			return true;
		}
		Log.d("FocusedHorizontalGridView", "onKeyDown super");
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	public boolean arrowScroll(int paramInt) {
		if ((this.mScrollDistance <= 0) && (getCount() > 0)) {
			this.mScrollDistance = (getChildAt(0).getWidth() + this.mSpacing + this.mHorizontalSpacing);
			Log.i("FocusedHorizontalGridView", new StringBuilder().append("scrollBy: mScrollDistance ").append(this.mScrollDistance).toString());
		}
		Log.i("FocusedHorizontalGridView", new StringBuilder().append("scrollBy:mCurrentPosition before ").append(this.mCurrentPosition).toString());
		View localView1 = getSelectedView();
		int i = this.mCurrentPosition;
		boolean bool1 = true;
		int j = 0;
		int k = getNumLines();
		int m = getPaddingLeft();
		if ((this.mHorizontalMode == 2) && (this.mPaddingLeft > 0))
			m += this.mPaddingLeft;
		int n = getWidth() - getPaddingRight();
		int i1 = this.mCurrentPosition % k;
		View localView2;
		switch (paramInt) {
		case 33:
			if ((i1 > 0) && (this.mCurrentPosition > 0))
				this.mCurrentPosition -= 1;
			else
				return false;
			break;
		case 130:
			if ((this.mCurrentPosition < getCount() - 1) && ((i1 + 1) % k > 0))
				this.mCurrentPosition += 1;
			else
				return false;
			break;
		case 17:
			if (this.mCurrentPosition - k >= 0) {
				this.mCurrentPosition -= k;
				localView2 = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				if (localView2 != null) {
					int i2 = localView2.getLeft();
					if ((this.mCurrentPosition >= getNumLines()) && (this.mHorizontalMode != 2)) {
						i2 -= this.mSpacing;
						i2 -= this.mHorizontalSpacing;
					}
					if (i2 < m)
						j = i2 - m;
				} else {
					j = -this.mScrollDistance;
					bool1 = false;
				}
				if (j != 0) {
					this.mLastScrollDirection = this.mScrollDirection;
					this.mScrollDirection = 0;
				}
			} else {
				return false;
			}
			break;
		case 66:
			if (this.mCurrentPosition + k <= getCount() - 1) {
				this.mCurrentPosition += k;
				localView2 = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				if (localView2 != null) {
					int i2 = localView2.getRight();
					if (this.mCurrentPosition < getAdapter().getCount() - getNumLines()) {
						i2 += this.mSpacing;
						i2 += this.mHorizontalSpacing;
					}
					if (i2 > n)
						j = i2 - n;
				} else {
					j = this.mScrollDistance;
					bool1 = false;
				}
				if (j != 0) {
					this.mLastScrollDirection = this.mScrollDirection;
					this.mScrollDirection = 1;
				}
			} else {
				return false;
			}
			break;
		}
		Log.i("FocusedHorizontalGridView", new StringBuilder().append("arrowScroll: mCurrentPosition = ").append(this.mCurrentPosition).toString());
		playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
		if (i != this.mCurrentPosition)
			this.mLastPosition = i;
		boolean bool2 = true;
		Log.d("FocusedHorizontalGridView",
				new StringBuilder().append("arrowScroll this.mLastPosition = ").append(this.mLastPosition).append(", this.mCurrentPosition = ").append(this.mCurrentPosition)
						.append(", lastPosition = ").append(i).toString());
		if (localView1 != null)
			performItemSelect(localView1, i, false);
		if ((getSelectedView() != null) && (getSelectedView() != localView1) && (i != this.mCurrentPosition))
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		setSelectedPositionInt(this.mCurrentPosition);
		this.mPositionManager.setTransAnimation(bool1);
		this.mPositionManager.setState(1);
		if (j != 0) {
			Log.i("FocusedHorizontalGridView", new StringBuilder().append("scrollBy: scrollBy = ").append(j).toString());
			this.mPositionManager.setContrantNotDraw(true);
			arrowSmoothScroll(j);
			this.mHandler.sendEmptyMessageDelayed(1, 10L);
		} else {
			this.mPositionManager.setSelectedView(getSelectedView());
			this.mPositionManager.setNeedDraw(true);
			this.mPositionManager.setContrantNotDraw(false);
			this.mPositionManager.setScaleCurrentView(bool2);
			this.mPositionManager.setScaleLastView(true);
			invalidate();
		}
		return true;
	}

	void arrowSmoothScroll(int paramInt) {
		if (this.mHorizontalMode == 1)
			arrowSmoothScrollSingle(paramInt);
		else if (this.mHorizontalMode == 2)
			arrowSmoothScrollFull(paramInt);
		this.mNeedScroll = true;
	}

	void arrowSmoothScrollSingle(int paramInt) {
		boolean bool = isScrolling();
		endFling();
		int i = getCurrentX();
		Log.e("FocusedHorizontalGridView",
				new StringBuilder().append("arrowSmoothScroll currY = ").append(i).append(", mCurrentPosition = ").append(this.mCurrentPosition).append(", isScrolling = ")
						.append(bool).toString());
		if (this.mScrollX < 0)
			i -= 2147483647;
		Log.e("FocusedHorizontalGridView", new StringBuilder().append("arrowSmoothScroll currY = ").append(i).append(", mScrollY = ").append(this.mScrollX).append(", scrollBy = ")
				.append(paramInt).toString());
		this.mScrollX -= i;
		if (this.mScrollDirection != this.mLastScrollDirection) {
			this.mScrollX = 0;
			i = 0;
		}
		if ((this.mScrollX != 0) && (bool) && (this.mScrollDirection == this.mLastScrollDirection)) {
			if (paramInt > 0)
				paramInt = this.mScrollDistance;
			else
				paramInt = -this.mScrollDistance;
			this.mNeedScroll = false;
		} else {
			this.mNeedScroll = true;
		}
		checkSelection();
		this.mScrollX += paramInt;
		Log.e("FocusedHorizontalGridView", new StringBuilder().append("arrowSmoothScroll mScrollY = ").append(this.mScrollX).toString());
		smoothScrollBy(this.mScrollX, this.mScrollDuration);
	}

	void arrowSmoothScrollFull(int paramInt) {
		int i = 0;
		View localView = getSelectedView();
		int j;
		if (this.mScrollDirection == 1) {
			if (this.mPaddingLeft < 0) {
				this.mPaddingLeft = getPaddingLeft();
				this.mBlockLayoutRequests = true;
				setPadding(0, getPaddingTop(), getPaddingRight(), getPaddingBottom());
				this.mBlockLayoutRequests = false;
			}
			if (localView != null) {
				j = localView.getLeft();
				paramInt = j - this.mStartX;
			} else {
				paramInt = getRight() + this.mScrollDistance;
			}
			i = paramInt * 100 / 300;
			this.mScrollList.add(Integer.valueOf(paramInt));
		} else if (this.mScrollDirection == 0) {
			j = this.mScrollList.size();
			paramInt = ((Integer) this.mScrollList.get(j - 1)).intValue();
			this.mScrollList.remove(j - 1);
			i = paramInt * 100 / 300;
			paramInt = -paramInt;
		}
		smoothScrollBy(paramInt, i);
	}

	void checkSelection() {
		int i = this.mCurrentPosition;
		View localView;
		while (i <= getLastVisiblePosition()) {
			localView = getChildAt(i - getFirstVisiblePosition());
			if (null != localView) {
				Log.d("FocusedHorizontalGridView",
						new StringBuilder().append("arrowSmoothScroll child.getTop() = ").append(localView.getTop()).append(", child.getBottom() = ").append(localView.getBottom())
								.append(", this.getTop() = ").append(getTop()).append(", this.getBottom() = ").append(getBottom()).append(", pos = ").append(i).toString());
				if ((localView.getTop() > getTop()) && (localView.getBottom() < getBottom())) {
					Log.d("FocusedHorizontalGridView", new StringBuilder().append("arrowSmoothScroll child.getTop() = ").append(localView.getTop())
							.append(", child.getBottom() = ").append(localView.getBottom()).append(", pos = ").append(i).toString());
					super.setSelection(i);
					return;
				}
			}
			i += getNumLines();
		}
		i = this.mCurrentPosition;
		while (i >= getFirstVisiblePosition()) {
			localView = getChildAt(i - getFirstVisiblePosition());
			if (null != localView) {
				Log.d("FocusedHorizontalGridView",
						new StringBuilder().append("arrowSmoothScroll child.getTop() = ").append(localView.getTop()).append(", child.getBottom() = ").append(localView.getBottom())
								.append(", this.getTop() = ").append(getTop()).append(", this.getBottom() = ").append(getBottom()).append(", pos = ").append(i).toString());
				if ((localView.getTop() > getTop()) && (localView.getBottom() < getBottom())) {
					Log.d("FocusedHorizontalGridView", new StringBuilder().append("arrowSmoothScroll child.getTop() = ").append(localView.getTop())
							.append(", child.getBottom() = ").append(localView.getBottom()).append(", pos = ").append(i).toString());
					super.setSelection(i);
					return;
				}
			}
			i -= getNumLines();
		}
	}

	void endFling() {
		smoothScrollBy(0, 0);
	}

	int getCurrentX() {
		return getActualX();
	}

	public void setOnKeyDownListener(onKeyDownListener paramonKeyDownListener) {
		this.onKeyDownListener = paramonKeyDownListener;
	}

	public void setManualPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		this.mPositionManager.setManualPadding(paramInt1, paramInt2, paramInt3, paramInt4);
	}

	public void setFocusResId(int paramInt) {
		this.mPositionManager.setFocusResId(paramInt);
	}

	public void setFocusShadowResId(int paramInt) {
		this.mPositionManager.setFocusShadowResId(paramInt);
	}

	public void setFocusMode(int paramInt) {
		this.mPositionManager.setFocusMode(paramInt);
	}

	private boolean checkFocusPosition() {
		Rect localRect = this.mPositionManager.getDstRectAfterScale(true);
		Log.i("FocusedHorizontalGridView",
				new StringBuilder().append("checkFocusPosition:").append(this.mPositionManager.getCurrentRect()).append(",").append(hasFocus()).append(",").append(localRect)
						.append(",").append(isShown()).toString());
		if ((null == this.mPositionManager.getCurrentRect()) || (!hasFocus()) || (null == localRect) || (!isShown()) || (this.mPositionManager.getContrantNotDraw()))
			return false;
		return (Math.abs(localRect.left - this.mPositionManager.getCurrentRect().left) > 5) || (Math.abs(localRect.right - this.mPositionManager.getCurrentRect().right) > 5)
				|| (Math.abs(localRect.top - this.mPositionManager.getCurrentRect().top) > 5) || (Math.abs(localRect.bottom - this.mPositionManager.getCurrentRect().bottom) > 5);
	}

	public static abstract interface ScrollerListener {
		public abstract void horizontalSmoothScrollBy(int paramInt1, int paramInt2);

		public abstract int getCurrX(boolean paramBoolean);

		public abstract int getFinalX(boolean paramBoolean);

		public abstract boolean isFinished();
	}

	public static abstract interface FocusDrawListener {
		public abstract void beforFocusDraw(Canvas paramCanvas);

		public abstract void drawChild(Canvas paramCanvas);
	}

	class FocusedGridPositionManager extends FocusedBasePositionManager {
		public FocusedGridPositionManager(Context paramView, View arg3) {
			super(paramView, arg3);
		}

		public Rect getDstRectBeforeScale(boolean paramBoolean) {
			View localView1 = getSelectedView();
			if (null == localView1)
				return null;
			View localView2 = localView1.findViewById(FocusedHorizontalGridView.this.mFocusViewId);
			if (localView2 == null)
				localView2 = localView1;
			Rect localRect1 = new Rect();
			Rect localRect2 = new Rect();
			Rect localRect3 = new Rect();
			if (!localView2.getGlobalVisibleRect(localRect1))
				return null;
			FocusedHorizontalGridView.this.getGlobalVisibleRect(localRect2);
			localView1.getGlobalVisibleRect(localRect3);
			if (paramBoolean) {
				int i = (localRect3.top + localRect3.bottom) / 2;
				int j = (localRect3.left + localRect3.right) / 2;
				int k = (int) (localRect1.width() / getItemScaleXValue());
				int m = (int) (localRect1.height() / getItemScaleYValue());
				localRect1.left = ((int) (j - localRect1.width() / getItemScaleXValue() / 2.0F));
				localRect1.right = (localRect1.left + k);
				localRect1.top = ((int) (i - (i - localRect1.top) / getItemScaleYValue()));
				localRect1.bottom = (localRect1.top + m);
			}
			int i = localRect1.right - localRect1.left;
			int j = localRect1.bottom - localRect1.top;
			localRect1.left = ((int) (localRect1.left + (1.0D - getItemScaleXValue()) * i / 2.0D));
			localRect1.top = ((int) (localRect1.top + (1.0D - getItemScaleYValue()) * ((localRect3.top + localRect3.bottom) / 2 - localRect1.top)));
			localRect1.right = ((int) (localRect1.left + i * getItemScaleXValue()));
			localRect1.bottom = ((int) (localRect1.top + j * getItemScaleYValue()));
			localRect1.left -= localRect2.left;
			localRect1.right -= localRect2.left;
			localRect1.top -= localRect2.top;
			localRect1.bottom -= localRect2.top;
			localRect1.top -= getSelectedPaddingTop();
			localRect1.left -= getSelectedPaddingLeft();
			localRect1.right += getSelectedPaddingRight();
			localRect1.bottom += getSelectedPaddingBottom();
			return localRect1;
		}

		public void drawChild(Canvas paramCanvas) {
			Log.i("FocusedBasePositionManager", "drawChild");
			if (FocusedHorizontalGridView.this.isScrolling())
				return;
			FocusedHorizontalGridView.this.drawChild(paramCanvas, getSelectedView(), FocusedHorizontalGridView.this.getDrawingTime());
		}

		public Rect getDstRectAfterScale(boolean paramBoolean) {
			View localView1 = getSelectedView();
			if (null == localView1)
				return null;
			View localView2 = localView1.findViewById(FocusedHorizontalGridView.this.mFocusViewId);
			if (localView2 == null)
				localView2 = localView1;
			Rect localRect1 = new Rect();
			Rect localRect2 = new Rect();
			Rect localRect3 = new Rect();
			if (!localView2.getGlobalVisibleRect(localRect1))
				return null;
			localView1.getGlobalVisibleRect(localRect3);
			FocusedHorizontalGridView.this.getGlobalVisibleRect(localRect2);
			if (FocusedHorizontalGridView.this.isScrolling()) {
				int i;
				if (FocusedHorizontalGridView.this.mScrollDirection == 1) {
					i = FocusedHorizontalGridView.this.getRight() - FocusedHorizontalGridView.this.getPaddingRight() - localRect1.left;
					if (localRect1.left + localView2.getWidth() * getItemScaleXValue() >= FocusedHorizontalGridView.this.getRight()
							- FocusedHorizontalGridView.this.getPaddingRight())
						localRect1.right = (FocusedHorizontalGridView.this.getRight() - FocusedHorizontalGridView.this.getPaddingRight());
				}
				if (FocusedHorizontalGridView.this.mScrollDirection == 0) {
					i = localRect1.right - FocusedHorizontalGridView.this.getPaddingLeft();
					if (localRect1.right - localView2.getWidth() * getItemScaleXValue() <= FocusedHorizontalGridView.this.getPaddingLeft())
						localRect1.left = FocusedHorizontalGridView.this.getPaddingLeft();
				}
			}
			localRect1.left -= localRect2.left;
			localRect1.right -= localRect2.left;
			localRect1.top -= localRect2.top;
			localRect1.bottom -= localRect2.top;
			if ((paramBoolean) && (isLastFrame())) {
				localRect1.top -= getSelectedShadowPaddingTop();
				localRect1.left -= getSelectedShadowPaddingLeft();
				localRect1.right += getSelectedShadowPaddingRight();
				localRect1.bottom += getSelectedShadowPaddingBottom();
			} else {
				localRect1.top -= getSelectedPaddingTop();
				localRect1.left -= getSelectedPaddingLeft();
				localRect1.right += getSelectedPaddingRight();
				localRect1.bottom += getSelectedPaddingBottom();
			}
			return localRect1;
		}
	}

	public static abstract interface onKeyDownListener {
		public abstract boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent);
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.app.widget.FocusedHorizontalGridView JD-Core Version: 0.6.2
 */