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

public class FocusedHorizontalListView extends HorizontalListView implements FocusedBasePositionManager.PositionInterface {
	public static final String TAG = "FocusedHorizontalListView";
	public static final int SCROLLING_DURATION = 900;
	private static final int SCROLLING_DELAY = 10;
	private static final int SCROLL_LEFT = 0;
	private static final int SCROLL_RIGHT = 1;
	private long KEY_INTERVEL = 20L;
	private long mKeyTime = 0L;
	private AbsHorizontalListView.OnScrollListener mOuterScrollListener;
	private AdapterView.OnItemClickListener mOnItemClickListener = null;
	private FocusedBasePositionManager.FocusItemSelectedListener mOnItemSelectedListener = null;
	private FocusedHListViewPositionManager mPositionManager;
	private int mFocusViewId = -1;
	private int mCurrentPosition = -1;
	private int mLastPosition = -1;
	private Object lock = new Object();
	private boolean isScrolling = false;
	private boolean mNeedScroll = false;
	int mScrollDistance = 0;
	int mScrollDuration = 900;
	private int mScrollDirection = 0;
	private int mLastScrollDirection = 0;
	private boolean mNeedDrawChild = false;
	private AbsHorizontalListView.OnScrollListener mOnScrollListener = new AbsHorizontalListView.OnScrollListener() {
		public void onScrollStateChanged(AbsHorizontalListView paramAnonymousAbsHorizontalListView, int paramAnonymousInt) {
			if (FocusedHorizontalListView.this.mOuterScrollListener != null)
				FocusedHorizontalListView.this.mOuterScrollListener.onScrollStateChanged(paramAnonymousAbsHorizontalListView, paramAnonymousInt);
			Log.i("FocusedHorizontalListView", "onScrollStateChanged scrolling");
			switch (paramAnonymousInt) {
			case 1:
			case 2:
				FocusedHorizontalListView.this.setScrolling(true);
				break;
			case 0:
				Log.i("FocusedHorizontalListView", "onScrollStateChanged idle mNeedScroll = " + FocusedHorizontalListView.this.mNeedScroll);
				Log.d("lingdang", "mCurrentPosition=" + FocusedHorizontalListView.this.mCurrentPosition);
				if (FocusedHorizontalListView.this.mNeedScroll)
					FocusedHorizontalListView.this.setSelection(FocusedHorizontalListView.this.mCurrentPosition);
				FocusedHorizontalListView.this.setScrolling(false);
				break;
			}
		}

		public void onScroll(AbsHorizontalListView paramAnonymousAbsHorizontalListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
			if (FocusedHorizontalListView.this.mOuterScrollListener != null)
				FocusedHorizontalListView.this.mOuterScrollListener.onScroll(paramAnonymousAbsHorizontalListView, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
		}
	};
	private static final int DRAW_FOCUS = 1;
	Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			switch (paramAnonymousMessage.what) {
			case 1:
				Log.i("FocusedHorizontalListView", "Handler handleMessage");
				if (FocusedHorizontalListView.this.getSelectedView() != null)
					FocusedHorizontalListView.this.performItemSelect(FocusedHorizontalListView.this.getSelectedView(), FocusedHorizontalListView.this.mCurrentPosition, true);
				else
					sendEmptyMessageDelayed(1, 10L);
				FocusedHorizontalListView.this.mPositionManager.setTransAnimation(false);
				FocusedHorizontalListView.this.mPositionManager.setContrantNotDraw(false);
				FocusedHorizontalListView.this.mPositionManager.setScaleCurrentView(true);
				FocusedHorizontalListView.this.mPositionManager.setScaleLastView(true);
				FocusedHorizontalListView.this.mPositionManager.setNeedDraw(true);
				FocusedHorizontalListView.this.mPositionManager.setSelectedView(FocusedHorizontalListView.this.getSelectedView());
				if (!FocusedHorizontalListView.this.isScrolling())
					FocusedHorizontalListView.this.invalidate();
				break;
			}
		}
	};
	private onKeyDownListener onKeyDownListener;
	int mScrollX = 0;

	public FocusedHorizontalListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext);
	}

	public FocusedHorizontalListView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	public FocusedHorizontalListView(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener) {
		this.mOnItemClickListener = paramOnItemClickListener;
	}

	public void setOnItemSelectedListener(FocusedBasePositionManager.FocusItemSelectedListener paramFocusItemSelectedListener) {
		this.mOnItemSelectedListener = paramFocusItemSelectedListener;
	}

	private void init(Context paramContext) {
		setChildrenDrawingOrderEnabled(true);
		this.mPositionManager = new FocusedHListViewPositionManager(paramContext, this);
		super.setOnScrollListener(this.mOnScrollListener);
	}

	public void setScrollDuration(int paramInt) {
		this.mScrollDuration = paramInt;
	}

	public void setOnScrollListener(AbsHorizontalListView.OnScrollListener paramOnScrollListener) {
		this.mOuterScrollListener = paramOnScrollListener;
	}

	private void performItemSelect(View paramView, int paramInt, boolean paramBoolean) {
		Log.d("FocusedHorizontalListView", new StringBuilder().append("performItemSelect position = ").append(paramInt).append(", isSelected = ").append(paramBoolean).toString());
		if (this.mOnItemSelectedListener != null) {
			this.mOnItemSelectedListener.onItemSelected(paramView, paramInt, paramBoolean, this);
			if (paramView.isFocusable())
				if (paramBoolean)
					paramView.requestFocus();
				else
					paramView.clearFocus();
		}
	}

	private void performItemClick() {
		View localView = getSelectedView();
		if ((localView != null) && (this.mOnItemClickListener != null))
			this.mOnItemClickListener.onItemClick(this, localView, this.mCurrentPosition, 0L);
	}

	public void dispatchDraw(Canvas paramCanvas) {
		Log.i("FocusedHorizontalListView", new StringBuilder().append("dispatchDraw child count = ").append(getChildCount()).toString());
		super.dispatchDraw(paramCanvas);
		if ((this.mPositionManager.getSelectedView() == null) && (getSelectedView() != null) && (hasFocus())) {
			this.mPositionManager.setSelectedView(getSelectedView());
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		}
		this.mPositionManager.drawFrame(paramCanvas);
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

	public void setSelection(int paramInt) {
		super.setSelection(paramInt);
		Log.i("FocusedHorizontalListView", new StringBuilder().append("setSelection = position:").append(paramInt).toString());
		this.mLastPosition = this.mCurrentPosition;
		this.mCurrentPosition = paramInt;
	}

	public int getSelectedItemPosition() {
		return this.mCurrentPosition;
	}

	public View getSelectedView() {
		int i = this.mCurrentPosition;
		int j = i - getFirstVisiblePosition();
		View localView = getChildAt(j);
		return localView;
	}

	protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
		Log.i("FocusedHorizontalListView",
				new StringBuilder().append("onFocusChanged,gainFocus:").append(paramBoolean).append(", mCurrentPosition = ").append(this.mCurrentPosition)
						.append(", child count = ").append(getChildCount()).toString());
		super.onFocusChanged(paramBoolean, paramInt, paramRect);
		synchronized (this) {
			this.mKeyTime = System.currentTimeMillis();
		}
		this.mPositionManager.setFocus(paramBoolean);
		if (!paramBoolean) {
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
			Log.i("FocusedHorizontalListView", new StringBuilder().append("onFocusChanged mCurrentPosition = ").append(this.mCurrentPosition).toString());
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
			performItemSelect(getSelectedView(), this.mCurrentPosition, paramBoolean);
		} else {
			Log.i("FocusedHorizontalListView", "============getSelectedView  is  null================");
		}
		invalidate();
		this.mPositionManager.setNeedDraw(true);
		this.mPositionManager.setState(1);
	}

	public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
		if ((getSelectedView() != null) && (getSelectedView().onKeyUp(paramInt, paramKeyEvent)))
			return true;
		if ((paramInt == 23) || (paramInt == 66)) {
			performItemClick();
			return true;
		}
		if ((paramInt == 20) || (paramInt == 20))
			return true;
		Log.i("FocusedHorizontalListView", new StringBuilder().append("onKeyUp super:").append(paramInt).toString());
		return super.onKeyUp(paramInt, paramKeyEvent);
	}

	public void setOnKeyDownListener(onKeyDownListener paramonKeyDownListener) {
		this.onKeyDownListener = paramonKeyDownListener;
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if ((this.onKeyDownListener != null) && (this.onKeyDownListener.onKeyDown(paramInt, paramKeyEvent)))
			return true;
		if ((paramInt != 22) && (paramInt != 21))
			return super.onKeyDown(paramInt, paramKeyEvent);
		synchronized (this) {
			if ((System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL) || (this.mPositionManager.getState() == 1)) {
				Log.d("FocusedHorizontalListView", new StringBuilder().append("onKeyDown KeyInterval:").append(System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL)
						.append(", getState():").append(this.mPositionManager.getState() == 1 ? "drawing" : "idle").append(", isScrolling: = ").toString());
				return true;
			}
			this.mKeyTime = System.currentTimeMillis();
		}
		if ((getSelectedView() != null) && (getSelectedView().onKeyDown(paramInt, paramKeyEvent)))
			return true;
		switch (paramInt) {
		case 21:
			if (!arrowScroll(17)) {
				Log.d("FocusedHorizontalListView", "onKeyDown left super.onkeydown");
				return super.onKeyDown(paramInt, paramKeyEvent);
			}
			return true;
		case 22:
			if (!arrowScroll(66)) {
				Log.d("FocusedHorizontalListView", "onKeyDown right super.onkeydown");
				return super.onKeyDown(paramInt, paramKeyEvent);
			}
			return true;
		}
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	public boolean arrowScroll(int paramInt) {
		if ((this.mScrollDistance <= 0) && (getChildCount() > 0))
			this.mScrollDistance = getChildAt(0).getWidth();
		View localView1 = getSelectedView();
		int i = this.mCurrentPosition;
		boolean bool1 = true;
		int j = 0;
		int k = getPaddingLeft();
		int m = getWidth() - getPaddingRight();
		View localView2;
		switch (paramInt) {
		case 17:
			if (this.mCurrentPosition > 0) {
				this.mCurrentPosition -= 1;
				localView2 = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				if (localView2 != null) {
					int n = localView2.getLeft();
					if (n < k)
						j = n - k;
				} else {
					j = -this.mScrollDistance;
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
				localView2 = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				if (localView2 != null) {
					int n = localView2.getRight();
					if (n > m)
						j = n - m;
				} else {
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
		}
		Log.i("FocusedHorizontalListView", new StringBuilder().append("arrowScroll scrollBy = ").append(j).append(" mCurrentPosition = ").append(this.mCurrentPosition).toString());
		playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
		if (i != this.mCurrentPosition)
			this.mLastPosition = i;
		if (localView1 != null)
			performItemSelect(localView1, i, false);
		if ((getSelectedView() != null) && (getSelectedView() != localView1) && (i != this.mCurrentPosition))
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		this.mPositionManager.setSelectedView(getSelectedView());
		this.mPositionManager.setTransAnimation(bool1);
		this.mPositionManager.setState(1);
		if (j != 0) {
			this.mPositionManager.setContrantNotDraw(true);
			this.mNeedScroll = true;
			arrowSmoothScroll(j);
			this.mHandler.sendEmptyMessageDelayed(1, 10L);
		} else {
			setSelection(this.mCurrentPosition);
			this.mPositionManager.setNeedDraw(true);
			this.mPositionManager.setContrantNotDraw(false);
			this.mPositionManager.setScaleCurrentView(true);
			this.mPositionManager.setScaleLastView(true);
			invalidate();
		}
		return true;
	}

	void arrowSmoothScroll(int paramInt) {
		boolean bool = isScrolling();
		endFling();
		int i = getActualX();
		Log.e("FocusedHorizontalListView",
				new StringBuilder().append("arrowSmoothScroll currX = ").append(i).append(", mCurrentPosition = ").append(this.mCurrentPosition).append(", isScrolling = ")
						.append(bool).append(", scrollBy = ").append(paramInt).toString());
		if (this.mScrollX < 0)
			i -= 2147483647;
		Log.e("FocusedHorizontalListView", new StringBuilder().append("arrowSmoothScroll currX = ").append(i).append(", mScrollX = ").append(this.mScrollX).append(", scrollBy = ")
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
		Log.e("FocusedHorizontalListView", new StringBuilder().append("arrowSmoothScroll mScrollX = ").append(this.mScrollX).toString());
		smoothScrollBy(this.mScrollX, this.mScrollDuration);
	}

	void checkSelection() {
		View localView;
		for (int i = this.mCurrentPosition; i <= getLastVisiblePosition(); i++) {
			localView = getChildAt(i - getFirstVisiblePosition());
			if (null != localView) {
				Log.d("FocusedHorizontalListView",
						new StringBuilder().append("arrowSmoothScroll child.getTop() = ").append(localView.getTop()).append(", child.getBottom() = ").append(localView.getBottom())
								.append(", this.getTop() = ").append(getTop()).append(", this.getBottom() = ").append(getBottom()).append(", pos = ").append(i).toString());
				if ((localView.getLeft() >= getLeft()) && (localView.getRight() <= getRight())) {
					Log.d("FocusedHorizontalListView", new StringBuilder().append("arrowSmoothScroll child.getTop() = ").append(localView.getTop())
							.append(", child.getBottom() = ").append(localView.getBottom()).append(", pos = ").append(i).toString());
					super.setSelection(i);
					return;
				}
			}
		}
		for (int i = this.mCurrentPosition; i >= getFirstVisiblePosition(); i--) {
			localView = getChildAt(i - getFirstVisiblePosition());
			if (null != localView) {
				Log.d("FocusedHorizontalListView",
						new StringBuilder().append("arrowSmoothScroll child.getTop() = ").append(localView.getTop()).append(", child.getBottom() = ").append(localView.getBottom())
								.append(", this.getTop() = ").append(getTop()).append(", this.getBottom() = ").append(getBottom()).append(", pos = ").append(i).toString());
				if ((localView.getLeft() >= getLeft()) && (localView.getRight() <= getRight())) {
					Log.d("FocusedHorizontalListView", new StringBuilder().append("arrowSmoothScroll child.getTop() = ").append(localView.getTop())
							.append(", child.getBottom() = ").append(localView.getBottom()).append(", pos = ").append(i).toString());
					super.setSelection(i);
					return;
				}
			}
		}
	}

	void endFling() {
		smoothScrollBy(0, this.mScrollDuration);
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

	public void setManualPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		this.mPositionManager.setManualPadding(paramInt1, paramInt2, paramInt3, paramInt4);
	}

	public void setFrameRate(int paramInt) {
		this.mPositionManager.setFrameRate(paramInt);
	}

	public void setFocusResId(int paramInt) {
		this.mPositionManager.setFocusResId(paramInt);
	}

	public void setFocusShadowResId(int paramInt) {
		this.mPositionManager.setFocusShadowResId(paramInt);
	}

	public void setItemScaleValue(float paramFloat1, float paramFloat2) {
		this.mPositionManager.setItemScaleValue(paramFloat1, paramFloat2);
	}

	public void setFocusMode(int paramInt) {
		this.mPositionManager.setFocusMode(paramInt);
	}

	public void setFocusViewId(int paramInt) {
		this.mFocusViewId = paramInt;
	}

	class FocusedHListViewPositionManager extends FocusedBasePositionManager {
		public FocusedHListViewPositionManager(Context paramView, View arg3) {
			super(paramView, arg3);
		}

		public Rect getDstRectBeforeScale(boolean paramBoolean) {
			Log.i("FocusedBasePositionManager", "getDstRectBeforeScale===========getSelectedItemPosition======" + FocusedHorizontalListView.this.getSelectedItemPosition());
			View localView1 = getSelectedView();
			if (null == localView1)
				return null;
			View localView2 = localView1.findViewById(FocusedHorizontalListView.this.mFocusViewId);
			if (localView2 == null)
				localView2 = localView1;
			Rect localRect1 = new Rect();
			Rect localRect2 = new Rect();
			Rect localRect3 = new Rect();
			if (!localView2.getGlobalVisibleRect(localRect1))
				return null;
			FocusedHorizontalListView.this.getGlobalVisibleRect(localRect2);
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
			if (FocusedHorizontalListView.this.isScrolling())
				return;
			FocusedHorizontalListView.this.drawChild(paramCanvas, getSelectedView(), FocusedHorizontalListView.this.getDrawingTime());
		}

		public Rect getDstRectAfterScale(boolean paramBoolean) {
			View localView1 = getSelectedView();
			if (null == localView1)
				return null;
			View localView2 = localView1.findViewById(FocusedHorizontalListView.this.mFocusViewId);
			if (localView2 == null)
				localView2 = localView1;
			Rect localRect1 = new Rect();
			Rect localRect2 = new Rect();
			Rect localRect3 = new Rect();
			if (!localView2.getGlobalVisibleRect(localRect1))
				return null;
			localView1.getGlobalVisibleRect(localRect3);
			FocusedHorizontalListView.this.getGlobalVisibleRect(localRect2);
			if (FocusedHorizontalListView.this.isScrolling()) {
				int i;
				if (FocusedHorizontalListView.this.mScrollDirection == 1) {
					i = FocusedHorizontalListView.this.getRight() - FocusedHorizontalListView.this.getPaddingRight() - localRect1.left;
					if (localRect1.left + localView2.getWidth() * getItemScaleXValue() >= FocusedHorizontalListView.this.getRight()
							- FocusedHorizontalListView.this.getPaddingRight())
						localRect1.right = (FocusedHorizontalListView.this.getRight() - FocusedHorizontalListView.this.getPaddingRight());
				}
				if (FocusedHorizontalListView.this.mScrollDirection == 0) {
					i = localRect1.right - FocusedHorizontalListView.this.getPaddingLeft();
					if (localRect1.right - localView2.getWidth() * getItemScaleXValue() <= FocusedHorizontalListView.this.getPaddingLeft())
						localRect1.left = FocusedHorizontalListView.this.getPaddingLeft();
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
 * com.yunos.tv.app.widget.FocusedHorizontalListView JD-Core Version: 0.6.2
 */