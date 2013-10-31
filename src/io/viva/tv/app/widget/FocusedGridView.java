package io.viva.tv.app.widget;

import io.viva.tv.app.widget.utils.FlingManager;
import io.viva.tv.lib.LOG;

import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Scroller;

public class FocusedGridView extends GridView implements FocusedBasePositionManager.PositionInterface, FlingManager.FlingCallback {
	private static final String TAG = "FocusedGridView";
	private static final boolean DEBUG = true;
	private static final int SCROLLING_DURATION = 1200;
	private static final int SCROLLING_DELAY = 10;
	private static final int SCROLL_UP = 0;
	private static final int SCROLL_DOWN = 1;
	private long KEY_INTERVEL = 150L;
	private long mKeyTime = 0L;
	protected int mCurrentPosition = -1;
	private int mLastPosition = -1;
	private AbsListView.OnScrollListener mOuterScrollListener;
	private boolean isScrolling = false;
	private Object lock = new Object();
	private int mStartX;
	private FocusedGridPositionManager mPositionManager;
	private AdapterView.OnItemClickListener mOnItemClickListener = null;
	private FocusedBasePositionManager.FocusItemSelectedListener mOnItemSelectedListener = null;
	private int mFocusViewId = -1;
	private int mHeaderPosition = -1;
	private boolean mHeaderSelected = false;
	private boolean mIsFocusInit = false;
	private int mLastOtherPosition = -1;
	private boolean mInit = false;
	private boolean mAutoChangeLine = true;
	private int mScrollDirection = 1;
	private int mLastScrollDirection = 1;
	private int mScrollDuration = 1200;
	private FocusDrawListener mFocusDrawListener = null;
	private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
		public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt) {
			if (FocusedGridView.this.mOuterScrollListener != null)
				FocusedGridView.this.mOuterScrollListener.onScrollStateChanged(paramAnonymousAbsListView, paramAnonymousInt);
			switch (paramAnonymousInt) {
			case 1:
			case 2:
				LOG.d("FocusedGridView", true, "onScrollStateChanged fling");
				FocusedGridView.this.setScrolling(true);
				break;
			case 0:
				LOG.d("FocusedGridView", true, "onScrollStateChanged idle");
				FocusedGridView.this.setScrolling(false);
				break;
			}
		}

		public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
			if (FocusedGridView.this.mOuterScrollListener != null)
				FocusedGridView.this.mOuterScrollListener.onScroll(paramAnonymousAbsListView, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
		}
	};
	public static int FOCUS_ITEM_REMEMBER_LAST = 0;
	public static int FOCUS_ITEM_AUTO_SEARCH = 1;
	private int focusPositionMode = FOCUS_ITEM_REMEMBER_LAST;
	boolean isKeyDown = false;
	int mScrollDistance = 0;
	int mScrollHeaderDiscance = 0;
	private Method methodSetNextSelectedPositionInt = null;
	int mScrollY = 0;
	private static final int DRAW_FOCUS = 1;
	Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			switch (paramAnonymousMessage.what) {
			case 1:
				if (FocusedGridView.this.getSelectedView() != null) {
					FocusedGridView.this.performItemSelect(FocusedGridView.this.getSelectedView(), FocusedGridView.this.mCurrentPosition, true);
				} else {
					Log.w("FocusedGridView", "Handler handleMessage selected view is null delay");
					sendEmptyMessageDelayed(1, 10L);
					return;
				}
				if (FocusedGridView.this.checkHeaderPosition()) {
					if (!FocusedGridView.this.checkFromHeaderPosition()) {
						FocusedGridView.this.mPositionManager.setContrantNotDraw(true);
						FocusedGridView.this.mPositionManager.setScaleCurrentView(false);
					}
				} else {
					FocusedGridView.this.mPositionManager.setContrantNotDraw(false);
					FocusedGridView.this.mPositionManager.setScaleCurrentView(true);
				}
				FocusedGridView.this.mPositionManager.setTransAnimation(false);
				FocusedGridView.this.mPositionManager.setScaleLastView(true);
				FocusedGridView.this.mPositionManager.setNeedDraw(true);
				FocusedGridView.this.mPositionManager.setSelectedView(FocusedGridView.this.getSelectedView());
				if (!FocusedGridView.this.isScrolling())
					FocusedGridView.this.invalidate();
				break;
			}
		}
	};
	private onKeyDownListener onKeyDownListener;
	FlingManager mFlingManager;

	public void setFocusDrawListener(FocusDrawListener paramFocusDrawListener) {
		this.mFocusDrawListener = paramFocusDrawListener;
	}

	public void setScrollDuration(int paramInt) {
		this.mScrollDuration = paramInt;
	}

	public void setAutoChangeLine(boolean paramBoolean) {
		this.mAutoChangeLine = paramBoolean;
	}

	public void setHeaderPosition(int paramInt) {
		this.mHeaderPosition = paramInt;
	}

	private boolean hasHeader() {
		return this.mHeaderPosition >= 0;
	}

	private boolean checkHeaderPosition() {
		return (hasHeader()) && (this.mCurrentPosition < getNumColumns());
	}

	public boolean checkHeaderPosition(int paramInt) {
		return (hasHeader()) && (paramInt < getNumColumns());
	}

	public boolean checkFromHeaderPosition() {
		return (hasHeader()) && (this.mLastPosition < getNumColumns());
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

	public void setFrameRate(int paramInt) {
		this.mPositionManager.setFrameRate(paramInt);
	}

	public void setScale(boolean paramBoolean) {
		this.mPositionManager.setScale(paramBoolean);
	}

	public FocusedGridView(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public FocusedGridView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	public FocusedGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext);
	}

	private void initLeftPosition() {
		if (!this.mInit) {
			this.mInit = true;
			int[] arrayOfInt = new int[2];
			getLocationOnScreen(arrayOfInt);
			this.mStartX = (arrayOfInt[0] + getPaddingLeft());
			LOG.d("FocusedGridView", true, "initLeftPosition mStartX = " + this.mStartX);
		}
	}

	public void init(Context paramContext) {
		Log.i("FocusedGridView", "init mCurrentPosition11:" + this.mCurrentPosition);
		setChildrenDrawingOrderEnabled(true);
		this.mPositionManager = new FocusedGridPositionManager(paramContext, this);
		super.setOnScrollListener(this.mOnScrollListener);
		Log.i("FocusedGridView", "init mCurrentPosition12:" + this.mCurrentPosition);
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		return true;
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

	public void dispatchDraw(Canvas paramCanvas) {
		LOG.d("FocusedGridView", true, "dispatchDraw child count = " + getChildCount() + ", first position = " + getFirstVisiblePosition() + ", last posititon = "
				+ getLastVisiblePosition());
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
			if (hasFocus())
				focusInit();
		}
	}

	public void getFocusedRect(Rect paramRect) {
		Log.i("FocusedGridView", "getFocusedRect mCurrentPosition" + this.mCurrentPosition + ",getFirstVisiblePosition:" + getFirstVisiblePosition() + ",getLastVisiblePosition:"
				+ getLastVisiblePosition());
		if ((this.mCurrentPosition < getFirstVisiblePosition()) || (this.mCurrentPosition > getLastVisiblePosition())) {
			this.mCurrentPosition = getFirstVisiblePosition();
			Log.i("FocusedGridView", "mCurrentPosition9:" + this.mCurrentPosition);
		}
		super.getFocusedRect(paramRect);
	}

	public void subSelectPosition() {
		this.mPositionManager.setLastSelectedView(null);
		arrowScroll(17);
	}

	public void setSelection(int paramInt) {
		LOG.i("aabbcc", true, "setSelection = " + paramInt + ", mCurrentPosition = " + this.mCurrentPosition);
		this.mLastPosition = this.mCurrentPosition;
		this.mCurrentPosition = paramInt;
		Log.i("aabbcc", "mCurrentPosition10:" + this.mCurrentPosition);
		this.mPositionManager.setTransAnimation(false);
		this.mPositionManager.setSelectedView(getSelectedView());
		this.mPositionManager.setLastSelectedView(null);
		this.mPositionManager.setScaleLastView(false);
		this.mPositionManager.setScaleCurrentView(true);
		performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		this.mPositionManager.setNeedDraw(true);
		this.mPositionManager.setState(1);
		requestLayout();
	}

	public void setOnScrollListener(AbsListView.OnScrollListener paramOnScrollListener) {
		this.mOuterScrollListener = paramOnScrollListener;
	}

	public void setFocusPositionMode(int paramInt) {
		LOG.i("FocusedGridView", true, "setFocusPositionMode mode = " + paramInt);
		this.focusPositionMode = paramInt;
	}

	public int getFocusPositionMode() {
		return this.focusPositionMode;
	}

	protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
		LOG.i("FocusedGridView", true, "onFocusChanged,gainFocus:" + paramBoolean + ", mCurrentPosition = " + this.mCurrentPosition + ", child count = " + getChildCount());
		if (this.focusPositionMode == FOCUS_ITEM_AUTO_SEARCH) {
			LOG.d("FocusedGridView", true, "super.onFocusChanged");
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
		LOG.i("FocusedGridView", true, "focusInit mCurrentPosition = " + this.mCurrentPosition + ", getSelectedItemPosition() = " + getSelectedItemPosition());
		if (this.mCurrentPosition < 0) {
			Log.i("FocusedGridView", "mCurrentPosition1:" + this.mCurrentPosition);
			this.mCurrentPosition = getSelectedItemPosition();
		}
		if (this.mCurrentPosition < 0) {
			Log.i("FocusedGridView", "mCurrentPosition2:" + this.mCurrentPosition);
			this.mCurrentPosition = 0;
		}
		if ((this.mCurrentPosition < getFirstVisiblePosition()) || (this.mCurrentPosition > getLastVisiblePosition())) {
			Log.i("FocusedGridView", "mCurrentPosition3:" + this.mCurrentPosition);
			this.mCurrentPosition = getFirstVisiblePosition();
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
				this.mCurrentPosition = super.getSelectedItemPosition();
				Log.i("FocusedGridView", "mCurrentPosition4:" + this.mCurrentPosition);
			} else {
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
			if (this.mCurrentPosition >= 0)
				this.mIsFocusInit = true;
		}
		this.mPositionManager.setNeedDraw(true);
		this.mPositionManager.setState(1);
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
			Log.w("FocusedGridView", "getSelectedView mCurrentPosition = " + this.mCurrentPosition + ", getFirstVisiblePosition() = " + getFirstVisiblePosition()
					+ ", getLastVisiblePosition() = " + getLastVisiblePosition());
			return null;
		}
		if (checkHeaderPosition())
			i = this.mHeaderPosition;
		int j = i - getFirstVisiblePosition();
		View localView = getChildAt(j);
		LOG.d("FocusedGridView", true, "getSelectedView getSelectedView: mCurrentPosition = " + this.mCurrentPosition + ", indexOfView = " + j + ", child count = "
				+ getChildCount() + ", getFirstVisiblePosition() = " + getFirstVisiblePosition() + ", getLastVisiblePosition() = " + getLastVisiblePosition());
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
		if (this.isKeyDown) {
			this.isKeyDown = false;
			return true;
		}
		return super.onKeyUp(paramInt, paramKeyEvent);
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if ((this.onKeyDownListener != null) && (this.onKeyDownListener.onKeyDown(paramInt, paramKeyEvent)))
			return true;
		if ((paramInt != 23) && (paramInt != 66) && (paramInt != 21) && (paramInt != 22) && (paramInt != 20) && (paramInt != 19))
			return super.onKeyDown(paramInt, paramKeyEvent);
		if (paramKeyEvent.getRepeatCount() == 0)
			this.isKeyDown = true;
		LOG.i("FocusedGridView", true, "onKeyDown keyCode = " + paramInt + ", child count = " + getChildCount() + ", mCurrentPosition = " + this.mCurrentPosition);
		synchronized (this) {
			if (this.mPositionManager.getState() == 1) {
				Log.w("FocusedGridView", "onKeyDown KyeInterval = " + (System.currentTimeMillis() - this.mKeyTime) + ", getState() = " + this.mPositionManager.getState()
						+ ", isScrolling() = " + isScrolling());
				return true;
			}
			if ((isScrolling())
					&& ((this.mCurrentPosition < getFirstVisiblePosition()) || (this.mCurrentPosition > getLastVisiblePosition()) || (System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL)))
				return true;
			this.mKeyTime = System.currentTimeMillis();
		}
		if (getChildCount() <= 0)
			return true;
		if (!this.mAutoChangeLine) {
			if ((paramInt == 22) && ((this.mCurrentPosition + 1) % getNumColumns() == 0))
				return false;
			if ((paramInt == 21) && (this.mCurrentPosition != 0) && (this.mCurrentPosition % getNumColumns() == 0))
				return false;
		}
		if ((getSelectedView() != null) && (getSelectedView().onKeyDown(paramInt, paramKeyEvent)))
			return true;
		if ((hasHeader())
				&& ((((this.mCurrentPosition + 1) / getNumColumns() == 1) && ((this.mCurrentPosition + 1) % getNumColumns() == 0) && (22 == paramInt)) || ((this.mCurrentPosition
						/ getNumColumns() == 1)
						&& (this.mCurrentPosition % getNumColumns() == 0) && (21 == paramInt))))
			return true;
		switch (paramInt) {
		case 19:
			if (!arrowScroll(33)) {
				Log.w("FocusedGridView", "arrowScroll up return false");
				return false;
			}
			return true;
		case 20:
			if (!arrowScroll(130)) {
				Log.w("FocusedGridView", "arrowScroll down return false");
				return false;
			}
			return true;
		case 21:
			if (!arrowScroll(17)) {
				Log.w("FocusedGridView", "arrowScroll left return false");
				return false;
			}
			return true;
		case 22:
			if (!arrowScroll(66)) {
				Log.w("FocusedGridView", "arrowScroll right return false");
				return false;
			}
			return true;
		}
		return true;
	}

	public boolean arrowScroll(int paramInt) {
		if (this.mScrollDistance <= 0)
			if (hasHeader()) {
				if (getChildAt(getNumColumns()) != null)
					this.mScrollDistance = getChildAt(getNumColumns()).getHeight();
				this.mScrollHeaderDiscance = getChildAt(this.mHeaderPosition).getHeight();
				LOG.d("FocusedGridView", true, "scrollBy: mScrollHeaderDiscance " + this.mScrollHeaderDiscance);
			} else if (getCount() > 0) {
				this.mScrollDistance = getChildAt(0).getHeight();
				LOG.d("FocusedGridView", true, "scrollBy: mScrollDistance " + this.mScrollDistance);
			}
		LOG.i("FocusedGridView", true, "scrollBy:mCurrentPosition before " + this.mCurrentPosition);
		View localView1 = getSelectedView();
		int i = this.mCurrentPosition;
		boolean bool1 = true;
		int j = 0;
		int k = getNumColumns();
		int m = getListPaddingTop();
		int n = getHeight() - getListPaddingBottom();
		View localView2;
		switch (paramInt) {
		case 33:
			if (this.mCurrentPosition >= k) {
				this.mCurrentPosition -= k;
				if (checkHeaderPosition()) {
					localView2 = getChildAt(this.mHeaderPosition - getFirstVisiblePosition());
					this.mCurrentPosition = this.mHeaderPosition;
					Log.i("FocusedGridView", "mCurrentPosition5:" + this.mCurrentPosition);
				} else {
					localView2 = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				}
				if (localView2 != null) {
					if (localView2.getTop() < m) {
						endFling();
						j = localView2.getTop() - m;
					}
				} else {
					endFling();
					j = checkHeaderPosition(this.mCurrentPosition) ? -this.mScrollHeaderDiscance : -this.mScrollDistance;
				}
				if (j != 0) {
					this.mLastScrollDirection = this.mScrollDirection;
					this.mScrollDirection = 0;
				}
			} else {
				return false;
			}
			break;
		case 130:
			if (this.mCurrentPosition / k < (getCount() - 1) / k) {
				this.mCurrentPosition += k;
				this.mCurrentPosition = (this.mCurrentPosition > getCount() - 1 ? getCount() - 1 : this.mCurrentPosition);
				Log.i("FocusedGridView", "mCurrentPosition6:" + this.mCurrentPosition);
				localView2 = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				if (localView2 != null) {
					if (localView2.getBottom() > n) {
						endFling();
						j = localView2.getBottom() - n;
					}
				} else {
					endFling();
					j = this.mScrollDistance;
				}
				if (j != 0) {
					this.mLastScrollDirection = this.mScrollDirection;
					this.mScrollDirection = 1;
				}
			} else {
				return false;
			}
			break;
		case 17:
			if (this.mCurrentPosition > 0) {
				this.mCurrentPosition -= 1;
				if ((this.mCurrentPosition + 1) % k == 0) {
					if (checkHeaderPosition()) {
						localView2 = getChildAt(this.mHeaderPosition - getFirstVisiblePosition());
						this.mCurrentPosition = this.mHeaderPosition;
						Log.i("FocusedGridView", "mCurrentPosition7:" + this.mCurrentPosition);
					} else {
						localView2 = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
					}
					if (localView2 != null) {
						if (localView2.getTop() < m) {
							endFling();
							j = localView2.getTop() - m;
						}
						bool1 = false;
					} else {
						endFling();
						j = checkHeaderPosition(this.mCurrentPosition) ? -this.mScrollHeaderDiscance : -this.mScrollDistance;
					}
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
			if (this.mCurrentPosition < getCount() - 1) {
				this.mCurrentPosition += 1;
				if (this.mCurrentPosition % k == 0) {
					localView2 = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
					if (localView2 != null) {
						if (localView2.getBottom() > n) {
							endFling();
							j = localView2.getBottom() - n;
						}
						bool1 = false;
					} else {
						endFling();
						j = this.mScrollDistance;
					}
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
		LOG.d("FocusedGridView", true, "arrowScroll: mCurrentPosition = " + this.mCurrentPosition);
		playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
		if (i != this.mCurrentPosition)
			this.mLastPosition = i;
		if (checkHeaderPosition(this.mCurrentPosition)) {
			if (j != 0) {
				this.mPositionManager.setContrantNotDraw(true);
				arrowSmoothScroll(j);
				this.mHandler.sendEmptyMessageDelayed(1, 10L);
				if (localView1 != null)
					performItemSelect(localView1, i, false);
				return true;
			}
			this.mPositionManager.setContrantNotDraw(true);
			this.mPositionManager.setTransAnimation(false);
			if (checkFromHeaderPosition()) {
				LOG.d("FocusedGridView", true, "arrowScroll focus form header");
				this.mPositionManager.setScaleCurrentView(false);
				this.mPositionManager.setScaleLastView(false);
				this.mPositionManager.setNeedDraw(false);
			} else {
				LOG.d("FocusedGridView", true, "arrowScroll focus form other");
				this.mLastOtherPosition = i;
				this.mPositionManager.setScaleCurrentView(false);
				this.mPositionManager.setScaleLastView(true);
				this.mPositionManager.setNeedDraw(true);
				invalidate();
			}
			LOG.d("FocusedGridView", true, "arrowScroll header mCurrentPosition = " + this.mCurrentPosition + ", mHeaderPosition = " + this.mHeaderPosition
					+ ", mHeaderSelected = " + this.mHeaderSelected);
			if (!this.mHeaderSelected) {
				this.mHeaderSelected = true;
				if (localView1 != null)
					performItemSelect(localView1, i, false);
				if ((getSelectedView() != null) && (getSelectedView() != localView1) && (i != this.mCurrentPosition))
					performItemSelect(getSelectedView(), this.mHeaderPosition, true);
			}
			return true;
		}
		boolean bool2 = true;
		boolean bool3 = true;
		LOG.d("FocusedGridView", true, "arrowScroll this.mLastPosition = " + this.mLastPosition + ", this.mCurrentPosition = " + this.mCurrentPosition + ", lastPosition = " + i);
		if (checkFromHeaderPosition()) {
			bool1 = false;
			bool2 = false;
			if ((this.mLastOtherPosition >= 0) && (i == this.mHeaderPosition)) {
				this.mCurrentPosition = this.mLastOtherPosition;
				Log.i("FocusedGridView", "mCurrentPosition8:" + this.mCurrentPosition);
			}
		}
		this.mHeaderSelected = false;
		if (localView1 != null)
			performItemSelect(localView1, i, false);
		if ((getSelectedView() != null) && (getSelectedView() != localView1) && (i != this.mCurrentPosition))
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		if (checkHeaderPosition()) {
			bool1 = false;
			bool3 = false;
		}
		this.mPositionManager.setTransAnimation(bool1);
		this.mPositionManager.setState(1);
		if (j != 0) {
			LOG.i("FocusedGridView", true, "scrollBy: scrollBy = " + j);
			this.mPositionManager.setContrantNotDraw(true);
			arrowSmoothScroll(j);
			this.mHandler.sendEmptyMessageDelayed(1, 10L);
		} else {
			this.mPositionManager.setSelectedView(getSelectedView());
			this.mPositionManager.setNeedDraw(true);
			this.mPositionManager.setContrantNotDraw(false);
			this.mPositionManager.setScaleCurrentView(bool3);
			this.mPositionManager.setScaleLastView(bool2);
			invalidate();
		}
		return true;
	}

	void arrowSmoothScroll(int paramInt) {
		boolean bool = isScrolling();
		endFling();
		int i = getCurrentY();
		LOG.d("FocusedGridView", true, "arrowSmoothScroll currY = " + i + ", mCurrentPosition = " + this.mCurrentPosition + ", isScrolling = " + bool);
		if (this.mScrollY < 0)
			i -= 2147483647;
		LOG.d("FocusedGridView", true, "arrowSmoothScroll currY = " + i + ", mScrollY = " + this.mScrollY + ", scrollBy = " + paramInt);
		this.mScrollY -= i;
		if (this.mScrollDirection != this.mLastScrollDirection) {
			this.mScrollY = 0;
			i = 0;
		}
		this.mScrollY += paramInt;
		LOG.d("FocusedGridView", true, "arrowSmoothScroll mScrollY = " + this.mScrollY);
		smoothScrollBy(this.mScrollY, this.mScrollDuration);
	}

	void setNextSelectedPositionInt(int paramInt) {
	}

	void endFling() {
		if (this.mFlingManager != null)
			this.mFlingManager.endFling();
	}

	int getCurrentY() {
		if (this.mFlingManager != null)
			return this.mFlingManager.getActualY();
		return 0;
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
		if ((null == this.mPositionManager.getCurrentRect()) || (!hasFocus()) || (null == localRect) || (!isShown()) || (this.mPositionManager.getContrantNotDraw()))
			return false;
		return (Math.abs(localRect.left - this.mPositionManager.getCurrentRect().left) > 5) || (Math.abs(localRect.right - this.mPositionManager.getCurrentRect().right) > 5)
				|| (Math.abs(localRect.top - this.mPositionManager.getCurrentRect().top) > 5) || (Math.abs(localRect.bottom - this.mPositionManager.getCurrentRect().bottom) > 5);
	}

	public void smoothScrollBy(int paramInt1, int paramInt2) {
		if (this.mFlingManager == null)
			this.mFlingManager = new FlingManager(this, this);
		int i = getFirstVisiblePosition();
		int j = getChildCount();
		int k = i + j - 1;
		int m = getPaddingTop();
		int n = getHeight() - getPaddingBottom();
		if ((paramInt1 == 0) || (getAdapter().getCount() == 0) || (j == 0) || ((i == 0) && (getChildAt(0).getTop() == m) && (paramInt1 < 0))
				|| ((k == getAdapter().getCount() - 1) && (getChildAt(j - 1).getBottom() == n) && (paramInt1 > 0))) {
			this.mFlingManager.endFling();
		} else {
			this.mFlingManager.focusedReportScrollStateChange(2);
			this.mFlingManager.startScroll(paramInt1, paramInt2);
		}
	}

	public void flingLayoutChildren() {
		layoutChildren();
	}

	public int getClipToPaddingMask() {
		return 34;
	}

	public boolean flingOverScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean) {
		return overScrollBy(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramBoolean);
	}

	public void flingDetachViewsFromParent(int paramInt1, int paramInt2) {
		detachViewsFromParent(paramInt1, paramInt2);
	}

	public boolean flingAwakenScrollBars() {
		return awakenScrollBars();
	}

	public static abstract interface ScrollerListener {
		public abstract void horizontalSmoothScrollBy(int paramInt1, int paramInt2);

		public abstract int getCurrX(boolean paramBoolean);

		public abstract int getFinalX(boolean paramBoolean);

		public abstract boolean isFinished();
	}

	class FocusedScroller extends Scroller {
		public FocusedScroller(Context paramInterpolator, Interpolator paramBoolean, boolean arg4) {
			super(paramInterpolator, paramBoolean, arg4);
		}

		public FocusedScroller(Context paramInterpolator, Interpolator arg3) {
			super(paramInterpolator, arg3);
		}

		public FocusedScroller(Context arg2) {
			super(arg2);
		}

		public boolean computeScrollOffset() {
			boolean bool1 = isFinished();
			boolean bool2 = FocusedGridView.this.checkFocusPosition();
			if ((!bool1) || (bool2))
				FocusedGridView.this.invalidate();
			boolean bool3 = super.computeScrollOffset();
			return bool3;
		}
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
			View localView2 = localView1.findViewById(FocusedGridView.this.mFocusViewId);
			Rect localRect1 = new Rect();
			Rect localRect2 = new Rect();
			Rect localRect3 = new Rect();
			localRect1.setEmpty();
			if (!localView2.getGlobalVisibleRect(localRect1))
				return null;
			FocusedGridView.this.getGlobalVisibleRect(localRect2);
			localView1.getGlobalVisibleRect(localRect3);
			LOG.d("FocusedBasePositionManager", true, "getDstRectBeforeScale imgView.top = " + localRect1.top + ", imgView.bottom = " + localRect1.bottom + ", gridiew.top = "
					+ FocusedGridView.this.getTop() + ", gridview.bottom = " + FocusedGridView.this.getBottom());
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
			LOG.i("FocusedBasePositionManager", true, "drawChild");
			FocusedGridView.this.drawChild(paramCanvas, getSelectedView(), FocusedGridView.this.getDrawingTime());
			if (FocusedGridView.this.mFocusDrawListener != null)
				FocusedGridView.this.mFocusDrawListener.drawChild(paramCanvas);
		}

		public Rect getDstRectAfterScale(boolean paramBoolean) {
			View localView1 = getSelectedView();
			if (null == localView1)
				return null;
			View localView2 = localView1.findViewById(FocusedGridView.this.mFocusViewId);
			Rect localRect1 = new Rect();
			Rect localRect2 = new Rect();
			Rect localRect3 = new Rect();
			if (!localView2.getGlobalVisibleRect(localRect1))
				return null;
			localView1.getGlobalVisibleRect(localRect3);
			FocusedGridView.this.getGlobalVisibleRect(localRect2);
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
 * com.yunos.tv.app.widget.FocusedGridView JD-Core Version: 0.6.2
 */