package io.viva.tv.app.widget;

import io.viva.tv.lib.LOG;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;

public class FocusedCoverFlow extends CoverFlow implements FocusedBasePositionManager.PositionInterface {
	private static final String TAG = "FocusedGridView";
	private static final boolean DEBUG = true;
	private static final int SCROLL_LEFT = 0;
	private static final int SCROLL_RIGHT = 1;
	protected int mCurrentPosition = -1;
	private int mLastPosition = -1;
	private FocusedCoverFlowPositionManager mPositionManager;
	private AdapterView.OnItemClickListener mOnItemClickListener = null;
	private FocusedBasePositionManager.FocusItemSelectedListener mOnItemSelectedListener = null;
	private boolean mIsFocusInit = false;
	private long mKeyTime = 0L;
	private int mDistance = -1;
	private int mScrollDirection = 0;
	private int mLastScrollDirection = 0;
	boolean isKeyDown = false;
	int mScrollX = 0;

	public FocusedCoverFlow(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext);
	}

	public FocusedCoverFlow(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	public FocusedCoverFlow(Context paramContext) {
		super(paramContext);
		init(paramContext);
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
	}

	public void setOnItemSelectedListener(FocusedBasePositionManager.FocusItemSelectedListener paramFocusItemSelectedListener) {
		this.mOnItemSelectedListener = paramFocusItemSelectedListener;
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
		LOG.d("FocusedGridView", true, "dispatchDraw child count = " + getChildCount() + ", first position = " + getFirstVisiblePosition() + ", last posititon = "
				+ getLastVisiblePosition());
		super.dispatchDraw(paramCanvas);
		if ((this.mPositionManager.getSelectedView() == null) && (getSelectedView() != null) && (hasFocus())) {
			this.mPositionManager.setSelectedView(getSelectedView());
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		}
		if (getSelectedView() != null)
			this.mPositionManager.drawFrame(paramCanvas);
	}

	protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
		LOG.i("FocusedGridView", true, "onFocusChanged,gainFocus:" + paramBoolean + ", mCurrentPosition = " + this.mCurrentPosition + ", child count = " + getChildCount());
		super.onFocusChanged(paramBoolean, paramInt, paramRect);
		synchronized (this) {
			this.mKeyTime = System.currentTimeMillis();
		}
		if (paramBoolean != this.mPositionManager.hasFocus())
			this.mIsFocusInit = false;
		this.mPositionManager.setFocus(paramBoolean);
		focusInit();
	}

	void init(Context paramContext) {
		Log.i("FocusedGridView", "init mCurrentPosition11:" + this.mCurrentPosition);
		setChildrenDrawingOrderEnabled(true);
		this.mPositionManager = new FocusedCoverFlowPositionManager(paramContext, this);
		Log.i("FocusedGridView", "init mCurrentPosition12:" + this.mCurrentPosition);
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
			this.mPositionManager.setScaleLastView(true);
		} else {
			this.mCurrentPosition = super.getSelectedItemPosition();
			Log.i("FocusedGridView", "mCurrentPosition4:" + this.mCurrentPosition);
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
		invalidate();
	}

	public int getSelectedItemPosition() {
		return this.mCurrentPosition;
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
		if (paramKeyEvent.getRepeatCount() == 0)
			this.isKeyDown = true;
		if (this.mDistance < 0)
			this.mDistance = (getSelectedView().getWidth() + this.mSpacing);
		if ((paramInt != 22) && (paramInt != 21))
			return super.onKeyDown(paramInt, paramKeyEvent);
		synchronized (this) {
			if (this.mPositionManager.getState() == 1)
				return true;
			this.mKeyTime = System.currentTimeMillis();
		}
		if ((getSelectedView() != null) && (getSelectedView().onKeyDown(paramInt, paramKeyEvent)))
			return true;
		switch (paramInt) {
		case 21:
			if (!arrowScroll(17)) {
				Log.d("FocusedGridView", "onKeyDown left super.onkeydown");
				return super.onKeyDown(paramInt, paramKeyEvent);
			}
			return true;
		case 22:
			if (!arrowScroll(66)) {
				Log.d("FocusedGridView", "onKeyDown right super.onkeydown");
				return super.onKeyDown(paramInt, paramKeyEvent);
			}
			return true;
		}
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	public boolean arrowScroll(int paramInt) {
		View localView = getSelectedView();
		int i = this.mCurrentPosition;
		int j = 0;
		boolean bool1 = false;
		boolean bool2 = true;
		int k = 1;
		int m;
		int n;
		switch (paramInt) {
		case 17:
			if (this.mCurrentPosition > 0) {
				this.mCurrentPosition -= 1;
				this.mCurrentSelectedPosition = this.mCurrentPosition;
				m = this.mSelectedPosition - getFirstVisiblePosition();
				n = m - getMidItemCount() / 2;
				int i1 = getFirstVisiblePosition() + n;
				if (this.mCurrentPosition < i1) {
					j = 1;
					bool1 = true;
					this.mLastScrollDirection = this.mScrollDirection;
					this.mScrollDirection = 0;
				}
			} else {
				if (this.mCurrentPosition == this.mSelectedPosition)
					return false;
				this.mCurrentSelectedPosition = 0;
				k = 0;
				j = 1;
				bool1 = true;
				this.mLastScrollDirection = this.mScrollDirection;
				this.mScrollDirection = 0;
			}
			break;
		case 66:
			if (this.mCurrentPosition < getCount() - 1) {
				this.mCurrentPosition += 1;
				this.mCurrentSelectedPosition = this.mCurrentPosition;
				m = this.mSelectedPosition;
				n = m + getMidItemCount() / 2;
				if (this.mCurrentPosition > n) {
					j = 1;
					bool1 = false;
					this.mLastScrollDirection = this.mScrollDirection;
					this.mScrollDirection = 1;
				}
			} else {
				if (this.mCurrentPosition == this.mSelectedPosition)
					return false;
				k = 0;
				j = 1;
				bool1 = false;
				this.mLastScrollDirection = this.mScrollDirection;
				this.mScrollDirection = 1;
			}
			break;
		}
		playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
		if (i != this.mCurrentPosition)
			this.mLastPosition = i;
		if (localView != null)
			performItemSelect(localView, i, false);
		if ((getSelectedView() != null) && (getSelectedView() != localView) && (i != this.mCurrentPosition))
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		if (j != 0) {
			arrowScroll(bool1);
			bool2 = false;
		}
		if (k != 0) {
			this.mPositionManager.setSelectedView(getSelectedView());
			this.mPositionManager.setTransAnimation(bool2);
			this.mPositionManager.setState(1);
			this.mPositionManager.setNeedDraw(true);
			this.mPositionManager.setContrantNotDraw(false);
			this.mPositionManager.setScaleCurrentView(true);
			this.mPositionManager.setScaleLastView(true);
			invalidate();
		}
		return true;
	}

	void arrowScroll(boolean paramBoolean) {
		endFling();
		int i = this.mDistance;
		int j = getActualX();
		this.mScrollX -= j;
		if (this.mScrollDirection != this.mLastScrollDirection) {
			this.mScrollX = 0;
			j = 0;
		}
		if (paramBoolean)
			i = -i;
		this.mScrollX += i;
		if (paramBoolean)
			movePrevious(this.mScrollX);
		else
			moveNext(this.mScrollX);
	}

	boolean movePrevious(int paramInt) {
		this.scrollPosition = (this.mSelectedPosition - 1);
		smoothScrollBy(paramInt, 1000);
		return true;
	}

	boolean moveNext(int paramInt) {
		this.scrollPosition = (this.mSelectedPosition + 1);
		smoothScrollBy(paramInt, 1000);
		return true;
	}

	protected void scrollIntoSlots() {
	}

	class FocusedCoverFlowPositionManager extends FocusedBasePositionManager {
		public FocusedCoverFlowPositionManager(Context paramView, View arg3) {
			super(paramView, arg3);
		}

		public Rect getDstRectBeforeScale(boolean paramBoolean) {
			Log.i("FocusedBasePositionManager", "getDstRectBeforeScale===========getSelectedItemPosition======" + FocusedCoverFlow.this.getSelectedItemPosition());
			View localView = getSelectedView();
			if (null == localView)
				return null;
			Rect localRect1 = new Rect();
			Rect localRect2 = new Rect();
			if (!localView.getGlobalVisibleRect(localRect1))
				return null;
			FocusedCoverFlow.this.getGlobalVisibleRect(localRect2);
			if (paramBoolean) {
				int i = (localRect1.top + localRect1.bottom) / 2;
				int j = (localRect1.left + localRect1.right) / 2;
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
			localRect1.top = ((int) (localRect1.top + (1.0D - getItemScaleYValue()) * ((localRect1.top + localRect1.bottom) / 2 - localRect1.top)));
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
			FocusedCoverFlow.this.drawChild(paramCanvas, getSelectedView(), FocusedCoverFlow.this.getDrawingTime());
		}

		public Rect getDstRectAfterScale(boolean paramBoolean) {
			View localView = getSelectedView();
			if (null == localView)
				return null;
			Rect localRect1 = new Rect();
			Rect localRect2 = new Rect();
			if (!localView.getGlobalVisibleRect(localRect1))
				return null;
			FocusedCoverFlow.this.getGlobalVisibleRect(localRect2);
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
}