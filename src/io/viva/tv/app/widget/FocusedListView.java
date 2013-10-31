package io.viva.tv.app.widget;

import io.viva.tv.lib.LOG;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class FocusedListView extends ListView implements FocusedBasePositionManager.PositionInterface {
	private static final String TAG = "FocusedListView";
	private static final boolean DEBUG = true;
	private static final int SCROLLING_DURATION = 150;
	private static final int SCROLLING_DELAY = 50;
	private long KEY_INTERVEL = 20L;
	private long mKeyTime = 0L;
	private AbsListView.OnScrollListener mOuterScrollListener;
	private AdapterView.OnItemClickListener mOnItemClickListener = null;
	private FocusedBasePositionManager.FocusItemSelectedListener mOnItemSelectedListener = null;
	private FocusedListViewPositionManager mPositionManager;
	private int mFocusViewId = -1;
	private int mCurrentPosition = -1;
	private int mLastPosition = -1;
	private Object lock = new Object();
	private boolean isScrolling = false;
	private boolean mNeedScroll = false;
	private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
		public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt) {
			if (FocusedListView.this.mOuterScrollListener != null)
				FocusedListView.this.mOuterScrollListener.onScrollStateChanged(paramAnonymousAbsListView, paramAnonymousInt);
			LOG.i("FocusedListView", true, "onScrollStateChanged scrolling");
			switch (paramAnonymousInt) {
			case 1:
			case 2:
				FocusedListView.this.setScrolling(true);
				break;
			case 0:
				LOG.i("FocusedListView", true, "onScrollStateChanged idle mNeedScroll = " + FocusedListView.this.mNeedScroll);
				LOG.d("lingdang", true, "mCurrentPosition=" + FocusedListView.this.mCurrentPosition);
				if (FocusedListView.this.mNeedScroll)
					FocusedListView.this.setSelection(FocusedListView.this.mCurrentPosition);
				FocusedListView.this.setScrolling(false);
				break;
			}
		}

		public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
			if (FocusedListView.this.mOuterScrollListener != null)
				FocusedListView.this.mOuterScrollListener.onScroll(paramAnonymousAbsListView, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
		}
	};
	private static final int DRAW_FOCUS = 1;
	Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			switch (paramAnonymousMessage.what) {
			case 1:
				LOG.i("FocusedListView", true, "Handler handleMessage");
				if (FocusedListView.this.getSelectedView() != null) {
					LOG.d("lingdang", true, "***11111111***");
					FocusedListView.this.performItemSelect(FocusedListView.this.getSelectedView(), FocusedListView.this.mCurrentPosition, true);
				}
				FocusedListView.this.mPositionManager.setSelectedView(FocusedListView.this.getSelectedView());
				FocusedListView.this.mPositionManager.setTransAnimation(false);
				FocusedListView.this.mPositionManager.setNeedDraw(true);
				FocusedListView.this.mPositionManager.setContrantNotDraw(false);
				FocusedListView.this.mPositionManager.setScaleCurrentView(true);
				FocusedListView.this.mPositionManager.setScaleLastView(true);
				FocusedListView.this.mPositionManager.setState(1);
				if (!FocusedListView.this.isScrolling()) {
					LOG.d("lingdang", true, "***222222***");
					FocusedListView.this.invalidate();
				}
				break;
			}
		}
	};
	public static int FOCUS_ITEM_REMEMBER_LAST = 0;
	public static int FOCUS_ITEM_AUTO_SEARCH = 1;
	private int focusPositionMode = FOCUS_ITEM_AUTO_SEARCH;
	boolean isKeyDown = false;
	private onKeyDownListener onKeyDownListener;
	int mScrollDistance = 0;

	public FocusedListView(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public FocusedListView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	public FocusedListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
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
		this.mFocusViewId = paramInt;
	}

	public void setOnScrollListener(AbsListView.OnScrollListener paramOnScrollListener) {
		this.mOuterScrollListener = paramOnScrollListener;
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener) {
		this.mOnItemClickListener = paramOnItemClickListener;
	}

	public void setOnItemSelectedListener(FocusedBasePositionManager.FocusItemSelectedListener paramFocusItemSelectedListener) {
		this.mOnItemSelectedListener = paramFocusItemSelectedListener;
	}

	private void init(Context paramContext) {
		setChildrenDrawingOrderEnabled(true);
		this.mPositionManager = new FocusedListViewPositionManager(paramContext, this);
		super.setOnScrollListener(this.mOnScrollListener);
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

	private void performItemSelect(View paramView, int paramInt, boolean paramBoolean) {
		if (this.mOnItemSelectedListener != null) {
			LOG.d("lingdang", true, new StringBuilder().append("**position=").append(paramInt).append(",isSelected=").append(paramBoolean).toString());
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
		LOG.i("FocusedListView", true, new StringBuilder().append("dispatchDraw child count = ").append(getChildCount()).toString());
		super.dispatchDraw(paramCanvas);
		if ((this.mPositionManager.getSelectedView() == null) && (getSelectedView() != null) && (hasFocus())) {
			this.mPositionManager.setSelectedView(getSelectedView());
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		}
		this.mPositionManager.drawFrame(paramCanvas);
	}

	public void setSelection(int paramInt) {
		LOG.i("ccdd", true, new StringBuilder().append("mCurrentPositionchange:setSelection:").append(paramInt).toString());
		this.mLastPosition = this.mCurrentPosition;
		this.mCurrentPosition = paramInt;
		int i = 0;
		View localView = getSelectedView();
		if (localView != null) {
			i = localView.getTop() - getPaddingTop();
			super.setSelectionFromTop(paramInt, i);
		} else {
			super.setSelectionFromTop(paramInt, getPaddingTop());
		}
		LOG.i("ccdd",
				true,
				new StringBuilder().append("setSelection:").append(paramInt).append(",top:").append(i).append(",getSelectedView().getTop():")
						.append(getSelectedView() != null ? new StringBuilder().append(getSelectedView().getTop()).append("").toString() : "null").append(",this.getPaddingTop():")
						.append(getPaddingTop()).append(",getCount():").append(getCount()).toString());
	}

	public int getSelectedItemPosition() {
		return this.mCurrentPosition;
	}

	public View getSelectedView() {
		int i = this.mCurrentPosition;
		int j = i - getFirstVisiblePosition();
		View localView = getChildAt(j);
		LOG.i("FocusedListView", true,
				new StringBuilder().append("getSelectedView mcurrentPosition:").append(this.mCurrentPosition).append(",firstVisiblePosition:").append(getFirstVisiblePosition())
						.toString());
		return localView;
	}

	public void setFocusPositionMode(int paramInt) {
		this.focusPositionMode = paramInt;
	}

	public int getFocusPositionMode() {
		return this.focusPositionMode;
	}

	protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
		LOG.i("ccdd",
				true,
				new StringBuilder().append("onFocusChanged,gainFocus:").append(paramBoolean).append(", mCurrentPosition = ").append(this.mCurrentPosition)
						.append(", child count = ").append(getChildCount()).append(",focusMode:").append(this.focusPositionMode).toString());
		if (this.focusPositionMode == FOCUS_ITEM_AUTO_SEARCH) {
			LOG.i("FocusedListView", true, "onfocus autosearch");
			super.onFocusChanged(paramBoolean, paramInt, paramRect);
		}
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
			LOG.i("FocusedListView", true,
					new StringBuilder().append("onfocus setSelection:").append((this.mCurrentPosition > -1) && (this.mCurrentPosition < getCount()) ? this.mCurrentPosition : 0)
							.toString());
			this.mCurrentPosition = ((this.mCurrentPosition > -1) && (this.mCurrentPosition < getCount()) ? this.mCurrentPosition : 0);
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
		}
		invalidate();
		this.mPositionManager.setNeedDraw(true);
		this.mPositionManager.setState(1);
	}

	public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
		if ((getSelectedView() != null) && (getSelectedView().onKeyUp(paramInt, paramKeyEvent))) {
			this.isKeyDown = false;
			return true;
		}
		if ((paramInt == 23) || (paramInt == 66)) {
			LOG.i("333", true, new StringBuilder().append("onKeyUp isKeyDown:").append(this.isKeyDown).toString());
			if (this.isKeyDown)
				performItemClick();
			this.isKeyDown = false;
			return true;
		}
		if ((paramInt == 20) || (paramInt == 20)) {
			this.isKeyDown = false;
			return true;
		}
		this.isKeyDown = false;
		LOG.i("FocusedListView", true, new StringBuilder().append("onKeyUp super:").append(paramInt).toString());
		return super.onKeyUp(paramInt, paramKeyEvent);
	}

	public void setOnKeyDownListener(onKeyDownListener paramonKeyDownListener) {
		this.onKeyDownListener = paramonKeyDownListener;
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if (paramKeyEvent.getRepeatCount() == 0) {
			LOG.i("333", true, new StringBuilder().append("onKeyDown event.getRepeatCount():").append(paramKeyEvent.getRepeatCount()).toString());
			this.isKeyDown = true;
		}
		if ((this.onKeyDownListener != null) && (this.onKeyDownListener.onKeyDown(paramInt, paramKeyEvent))) {
			LOG.d("lingdang", true, "1111111111111111");
			return true;
		}
		if ((paramInt != 20) && (paramInt != 19)) {
			LOG.d("lingdang", true, "222222222222222");
			return super.onKeyDown(paramInt, paramKeyEvent);
		}
		LOG.i("FocusedListView", true, new StringBuilder().append("onKeyDown keyCode = ").append(paramInt).append(", child count = ").append(getChildCount()).toString());
		synchronized (this) {
			if ((System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL) || (this.mPositionManager.getState() == 1) || (isScrolling())) {
				LOG.d("lingdang", true, new StringBuilder().append("KeyInterval:").append(System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL).append(", getState():")
						.append(this.mPositionManager.getState() == 1 ? "drawing" : "idle").append(", isScrolling: = ").append(isScrolling()).toString());
				LOG.d("lingdang", true, "333333333333333");
				return true;
			}
			this.mKeyTime = System.currentTimeMillis();
		}
		if ((getSelectedView() != null) && (getSelectedView().onKeyDown(paramInt, paramKeyEvent))) {
			LOG.d("lingdang", true, "44444444444444444444444");
			return true;
		}
		switch (paramInt) {
		case 19:
			LOG.d("lingdang", true, "5555555555555555");
			if (!arrowScroll(33)) {
				LOG.d("lingdang", true, "5555555111111111111155555555");
				LOG.i("FocusedListView", true, "onKeyDown super focus_up");
				return super.onKeyDown(paramInt, paramKeyEvent);
			}
			return true;
		case 20:
			LOG.d("lingdang", true, "6666666666666666666666666");
			if (!arrowScroll(130)) {
				LOG.d("lingdang", true, "6666666666611111111111166666666666");
				LOG.i("FocusedListView", true, "onKeyDown super focus_down");
				return super.onKeyDown(paramInt, paramKeyEvent);
			}
			return true;
		}
		LOG.i("FocusedListView", true, "onKeyDown super");
		LOG.d("lingdang", true, "77777777777");
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	public boolean arrowScroll(int paramInt) {
		LOG.d("lingdang", true, "+++++111111111111++++");
		if (this.mScrollDistance <= 0) {
			LOG.d("lingdang", true, "+++++22222222222++++");
			if (getChildCount() > 0)
				this.mScrollDistance = getChildAt(0).getHeight();
		}
		LOG.i("FocusedListView", true, new StringBuilder().append("scrollBy:mCurrentPosition before ").append(this.mCurrentPosition).toString());
		View localView1 = getSelectedView();
		int i = this.mCurrentPosition;
		boolean bool1 = true;
		int j = 0;
		int k = getListPaddingTop();
		int m = getHeight() - getListPaddingBottom();
		View localView2;
		switch (paramInt) {
		case 33:
			LOG.d("lingdang", true, "+++++3333333333++++");
			if (this.mCurrentPosition > 0) {
				this.mCurrentPosition -= 1;
				localView2 = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				if (localView2 != null) {
					int n = localView2.getTop();
					if (n < k) {
						j = n - k;
						if (this.mCurrentPosition != 0)
							j -= getDividerHeight();
					}
				} else {
					j = -this.mScrollDistance;
					if (this.mCurrentPosition != 0)
						j -= getDividerHeight();
				}
			} else {
				return false;
			}
			break;
		case 130:
			LOG.d("lingdang", true, "+++++444444444++++");
			LOG.i("FocusedListView", true, new StringBuilder().append("mCurrentPosition:").append(this.mCurrentPosition).append(",getCount:").append(getCount()).toString());
			if (this.mCurrentPosition < getCount() - 1) {
				LOG.d("lingdang", true, "+++++5555555555++++");
				this.mCurrentPosition += 1;
				localView2 = getChildAt(this.mCurrentPosition - getFirstVisiblePosition());
				if (localView2 != null) {
					LOG.d("lingdang", true, "+++++6666666666++++");
					int n = localView2.getBottom();
					if (n > m) {
						LOG.d("lingdang", true, "+++++7777777777++++");
						j = n - m;
						if (this.mCurrentPosition != getCount() - 1)
							j += getDividerHeight();
					}
				} else {
					LOG.d("lingdang", true, new StringBuilder().append("+++++888888888++++mScrollDistance=").append(this.mScrollDistance).append("scrollbBy=").append(j).toString());
					j = this.mScrollDistance;
					if (this.mCurrentPosition != getCount() - 1)
						j += getDividerHeight();
				}
			} else {
				LOG.d("lingdang", true, "+++++9999999999++++");
				return false;
			}
			break;
		}
		LOG.i("FocusedListView", true, new StringBuilder().append("scrollBy:").append(j).append(" mCurrentPosition after ").append(this.mCurrentPosition).toString());
		playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
		if (i != this.mCurrentPosition) {
			LOG.d("lingdang", true, "+++++-----1------++++");
			this.mLastPosition = i;
		}
		if (localView1 != null) {
			LOG.d("lingdang", true, "+++++-----2------++++");
			performItemSelect(localView1, i, false);
		}
		if ((getSelectedView() != null) && (getSelectedView() != localView1) && (i != this.mCurrentPosition)) {
			LOG.d("lingdang", true, "+++++-----3------++++");
			performItemSelect(getSelectedView(), this.mCurrentPosition, true);
		}
		this.mPositionManager.setSelectedView(getSelectedView());
		this.mPositionManager.setTransAnimation(bool1);
		if (j != 0) {
			LOG.d("lingdang", true, new StringBuilder().append("+++++-----4------++++scrollBy=").append(j).toString());
			this.mPositionManager.setContrantNotDraw(true);
			this.mNeedScroll = true;
			smoothScrollBy(j, 150);
			this.mHandler.sendEmptyMessageDelayed(1, 50L);
		} else {
			setSelection(this.mCurrentPosition);
			LOG.d("lingdang", true, "+++++-----5------++++");
			this.mPositionManager.setNeedDraw(true);
			this.mPositionManager.setContrantNotDraw(false);
			this.mPositionManager.setState(1);
			this.mPositionManager.setScaleCurrentView(true);
			this.mPositionManager.setScaleLastView(true);
			invalidate();
		}
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

	class FocusedListViewPositionManager extends FocusedBasePositionManager {
		public FocusedListViewPositionManager(Context paramView, View arg3) {
			super(paramView, arg3);
		}

		public Rect getDstRectBeforeScale(boolean paramBoolean) {
			View localView1 = getSelectedView();
			if (null == localView1)
				return null;
			View localView2 = localView1.findViewById(FocusedListView.this.mFocusViewId);
			if (localView2 == null)
				localView2 = localView1;
			Rect localRect1 = new Rect();
			Rect localRect2 = new Rect();
			Rect localRect3 = new Rect();
			localView2.getGlobalVisibleRect(localRect1);
			FocusedListView.this.getGlobalVisibleRect(localRect2);
			localView1.getGlobalVisibleRect(localRect3);
			LOG.i("FocusedBasePositionManager", true, "mLastFocusRect imgView:" + localRect1 + "(" + localRect1.width() + "," + localRect1.height() + ")");
			LOG.i("FocusedBasePositionManager", true, "mLastFocusRect listViewRect:" + localRect2 + "(" + localRect2.width() + "," + localRect2.height() + ")");
			LOG.i("FocusedBasePositionManager", true, "mLastFocusRect selectViewRect:" + localRect3 + "(" + localRect3.width() + "," + localRect3.height() + ")");
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
			FocusedListView.this.drawChild(paramCanvas, getSelectedView(), FocusedListView.this.getDrawingTime());
		}

		public Rect getDstRectAfterScale(boolean paramBoolean) {
			View localView1 = getSelectedView();
			if (null == localView1)
				return null;
			View localView2 = localView1.findViewById(FocusedListView.this.mFocusViewId);
			if (localView2 == null)
				localView2 = localView1;
			Rect localRect1 = new Rect();
			Rect localRect2 = new Rect();
			Rect localRect3 = new Rect();
			localView2.getGlobalVisibleRect(localRect1);
			localView1.getGlobalVisibleRect(localRect3);
			FocusedListView.this.getGlobalVisibleRect(localRect2);
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
 * com.yunos.tv.app.widget.FocusedListView JD-Core Version: 0.6.2
 */