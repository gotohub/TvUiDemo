package io.viva.tv.app.widget;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class FocusedRelativeLayout extends RelativeLayout implements FocusedBasePositionManager.PositionInterface {
	public static final String TAG = "FocusedRelativeLayout";
	public static final int HORIZONTAL_SINGEL = 1;
	public static final int HORIZONTAL_FULL = 2;
	private static final int SCROLL_DURATION = 100;
	private long KEY_INTERVEL = 20L;
	private long mKeyTime = 0L;

	public int mIndex = -1;

	private boolean mOutsieScroll = false;
	private boolean mInit = false;
	private HotScroller mScroller;
	private int mScreenWidth;
	private int mViewRight = 20;
	private int mViewLeft = 0;
	private int mStartX;
	private long mScrollTime = 0L;
	private int mHorizontalMode = -1;
	FocusedLayoutPositionManager mPositionManager;
	private Map<View, NodeInfo> mNodeMap = new HashMap<View, NodeInfo>();

	public void setManualPadding(int left, int top, int right, int bottom) {
		this.mPositionManager.setManualPadding(left, top, right, bottom);
	}

	public void setFrameRate(int rate) {
		this.mPositionManager.setFrameRate(rate);
	}

	public void setScaleMode(int mode) {
		this.mPositionManager.setScaleMode(mode);
	}

	public void setFocusResId(int focusResId) {
		this.mPositionManager.setFocusResId(focusResId);
	}

	public void setFocusShadowResId(int focusResId) {
		this.mPositionManager.setFocusShadowResId(focusResId);
	}

	public void setItemScaleValue(float scaleXValue, float scaleYValue) {
		this.mPositionManager.setItemScaleValue(scaleXValue, scaleYValue);
	}

	public void setItemScaleFixedX(int x) {
		this.mPositionManager.setItemScaleFixedX(x);
	}

	public void setItemScaleFixedY(int y) {
		this.mPositionManager.setItemScaleFixedY(y);
	}

	public void setFocusMode(int mode) {
		this.mPositionManager.setFocusMode(mode);
	}

	public void setFocusViewId(int id) {
	}

	public void setHorizontalMode(int mode) {
		this.mHorizontalMode = mode;
	}

	private void setInit(boolean isInit) {
		synchronized (this) {
			this.mInit = isInit;
		}
	}

	private boolean isInit() {
		synchronized (this) {
			return this.mInit;
		}
	}

	public void setViewRight(int right) {
		this.mViewRight = right;
	}

	public void setViewLeft(int right) {
		this.mViewLeft = right;
	}

	public void setOutsideSroll(boolean scroll) {
		Log.d("FocusedRelativeLayout", "setOutsideSroll scroll = " + scroll + ", this = " + this);
		this.mScrollTime = System.currentTimeMillis();
		this.mOutsieScroll = scroll;
	}

	public FocusedRelativeLayout(Context contxt) {
		super(contxt);
		setChildrenDrawingOrderEnabled(true);
		this.mScroller = new HotScroller(contxt, new DecelerateInterpolator());
		this.mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;

		this.mPositionManager = new FocusedLayoutPositionManager(contxt, this);
	}

	public FocusedRelativeLayout(Context contxt, AttributeSet attrs) {
		super(contxt, attrs);
		setChildrenDrawingOrderEnabled(true);
		this.mScroller = new HotScroller(contxt, new DecelerateInterpolator());
		this.mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;

		this.mPositionManager = new FocusedLayoutPositionManager(contxt, this);
	}

	public FocusedRelativeLayout(Context contxt, AttributeSet attrs, int defStyle) {
		super(contxt, attrs, defStyle);
		setChildrenDrawingOrderEnabled(true);
		this.mScroller = new HotScroller(contxt, new DecelerateInterpolator());
		this.mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;

		this.mPositionManager = new FocusedLayoutPositionManager(contxt, this);
	}

	protected int getChildDrawingOrder(int childCount, int i) {
		int selectedIndex = this.mIndex;
		if (selectedIndex < 0) {
			return i;
		}

		if (i < selectedIndex)
			return i;
		if (i >= selectedIndex) {
			return childCount - 1 - i + selectedIndex;
		}
		return i;
	}

	private synchronized void init() {
		if ((hasFocus()) && (!this.mOutsieScroll) && (!isInit())) {
			int[] location = new int[2];
			int minLeft = 65536;
			for (int index = 0; index < getChildCount(); index++) {
				View child = getChildAt(index);
				if (!this.mNodeMap.containsKey(child)) {
					NodeInfo info = new NodeInfo();
					info.index = index;
					this.mNodeMap.put(child, info);
				}

				child.getLocationOnScreen(location);
				if (location[0] < minLeft) {
					minLeft = location[0];
				}
			}

			this.mStartX = minLeft;
			Log.d("FocusedRelativeLayout", "init mStartX = " + this.mStartX);
			setInit(true);
		}
	}

	public void release() {
		this.mNodeMap.clear();
	}

	public void dispatchDraw(Canvas canvas) {
		Log.i("FocusedRelativeLayout", "dispatchDraw");
		super.dispatchDraw(canvas);
		if (getVisibility() == VISIBLE)
			this.mPositionManager.drawFrame(canvas);
	}

	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		Log.d("FocusedRelativeLayout", "onFocusChanged this = " + this + ", mScreenWidth = " + this.mScreenWidth + ", mIndex = " + this.mIndex + ", gainFocus = " + gainFocus + ", child count = " + getChildCount());
		synchronized (this) {
			this.mKeyTime = System.currentTimeMillis();
		}

		this.mPositionManager.setFocus(gainFocus);

		this.mPositionManager.setTransAnimation(false);
		this.mPositionManager.setNeedDraw(true);
		this.mPositionManager.setState(FocusedLayoutPositionManager.STATE_DRAWING);
		if (!gainFocus) {
			this.mPositionManager.drawFrame(null);
			this.mPositionManager.setFocusDrawableVisible(false, true);
		} else {
			if (-1 == this.mIndex) {
				this.mIndex = 0;
				this.mPositionManager.setSelectedView(getSelectedView());
			}

			View v = getSelectedView();
			if ((v instanceof ScalePostionInterface)) {
				ScalePostionInterface face = (ScalePostionInterface) v;
				this.mPositionManager.setScaleCurrentView(face.getIfScale());
			}

			this.mPositionManager.setLastSelectedView(null);
			invalidate();
		}
	}

	public void getFocusedRect(Rect r) {
		View item = getSelectedView();
		if (item != null) {
			item.getFocusedRect(r);
			offsetDescendantRectToMyCoords(item, r);
			Log.d("FocusedRelativeLayout", "getFocusedRect r = " + r);
			return;
		}
		super.getFocusedRect(r);
	}

	public View getSelectedView() {
		int indexOfView = this.mIndex;
		View selectedView = getChildAt(indexOfView);
		return selectedView;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((23 == keyCode) && (getSelectedView() != null)) {
			getSelectedView().performClick();
		}

		return super.onKeyUp(keyCode, event);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		synchronized (this) {
			if ((System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL) || (this.mPositionManager.getState() == 1) || (System.currentTimeMillis() - this.mScrollTime < 100L) || (!this.mScroller.isFinished())) {
				Log.d("FocusedRelativeLayout", "onKeyDown mAnimationTime = " + this.mKeyTime + " -- current time = " + System.currentTimeMillis());
				return true;
			}
			this.mKeyTime = System.currentTimeMillis();
		}

		if (!isInit()) {
			init();
			return true;
		}

		View lastSelectedView = getSelectedView();
		NodeInfo nodeInfo = (NodeInfo) this.mNodeMap.get(lastSelectedView);
		View v = null;
		int direction;
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (nodeInfo.fromLeft != null)
				v = nodeInfo.fromLeft;
			else {
				v = lastSelectedView.focusSearch(17);
			}
			direction = 17;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (nodeInfo.fromRight != null)
				v = nodeInfo.fromRight;
			else {
				v = lastSelectedView.focusSearch(66);
			}
			direction = 66;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (nodeInfo.fromDown != null)
				v = nodeInfo.fromDown;
			else {
				v = lastSelectedView.focusSearch(130);
			}
			direction = 130;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if (nodeInfo.fromUp != null)
				v = nodeInfo.fromUp;
			else {
				v = lastSelectedView.focusSearch(33);
			}
			direction = 33;
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		Log.d("FocusedRelativeLayout", "onKeyDown v = " + v);
		if ((v != null) && (this.mNodeMap.containsKey(v))) {
			NodeInfo info = (NodeInfo) this.mNodeMap.get(v);
			View selectedView = getSelectedView();
			if (selectedView != null) {
				selectedView.setSelected(false);
				View.OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
				if (listener != null) {
					listener.onFocusChange(selectedView, false);
				}
			}

			this.mIndex = info.index;

			selectedView = getSelectedView();
			if (selectedView != null) {
				selectedView.setSelected(true);
				View.OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
				if (listener != null) {
					listener.onFocusChange(selectedView, true);
				}
			}

			switch (keyCode) {
			case 21:
				info.fromRight = lastSelectedView;
				break;
			case 22:
				info.fromLeft = lastSelectedView;
				break;
			case 20:
				info.fromUp = lastSelectedView;
				break;
			case 19:
				info.fromDown = lastSelectedView;
			}

			boolean isScale = true;

			if ((selectedView instanceof ScalePostionInterface)) {
				ScalePostionInterface inter = (ScalePostionInterface) selectedView;
				isScale = inter.getIfScale();
			}

			this.mPositionManager.setSelectedView(getSelectedView());
			this.mPositionManager.computeScaleXY();
			this.mPositionManager.setScaleCurrentView(isScale);
			horizontalScroll();
			this.mPositionManager.setTransAnimation(true);
			this.mPositionManager.setNeedDraw(true);
			this.mPositionManager.setState(1);
			invalidate();
		} else {
			Log.w("FocusedRelativeLayout", "onKeyDown select view is null");
			playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
			return super.onKeyDown(keyCode, event);
		}

		playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
		return true;
	}

	private void horizontalScroll() {
		if (1 == this.mHorizontalMode)
			scrollSingel();
		else if (2 == this.mHorizontalMode)
			scrollFull();
	}

	void scrollFull() {
		int[] location = new int[2];
		getSelectedView().getLocationOnScreen(location);
		int left = location[0];
		int right = location[0] + getSelectedView().getWidth();
		Log.d("FocusedRelativeLayout", "scrollFull left = " + left + ", right = " + right + ", scaleX = " + this.mPositionManager.getItemScaleXValue());
		int imgW = getSelectedView().getWidth();
		left = (int) (left + (1.0D - this.mPositionManager.getItemScaleXValue()) * imgW / 2.0D);
		right = (int) (left + imgW * this.mPositionManager.getItemScaleXValue());

		Log.d("FocusedRelativeLayout", "scrollFull scaled left = " + left + ", scaled right = " + right);
		getLocationOnScreen(location);
		if ((right - this.mScreenWidth > 3) && (!this.mOutsieScroll)) {
			int dx = left - this.mStartX - this.mViewLeft;
			Log.d("FocusedRelativeLayout", "scrollFull to right dx = " + dx + ", mStartX = " + this.mStartX + ", mScreenWidth = " + this.mScreenWidth + ", left = " + left);
			if (dx + this.mScroller.getFinalX() > location[0] + getWidth()) {
				dx = location[0] + getWidth() - this.mScroller.getFinalX();
			}
			int duration = dx * 100 / 300;
			smoothScrollBy(dx, duration);
			return;
		}

		Log.d("FocusedRelativeLayout", "scroll conrtainer left = " + this.mStartX);
		if ((this.mStartX - left > 3) && (!this.mOutsieScroll)) {
			int dx = right - this.mScreenWidth;
			Log.d("FocusedRelativeLayout", "scrollFull to left dx = " + dx + ", mStartX = " + this.mStartX + ", currX = " + this.mScroller.getCurrX() + ", mScreenWidth = " + this.mScreenWidth + ", left = " + left);
			if (this.mScroller.getCurrX() < Math.abs(dx)) {
				dx = -this.mScroller.getCurrX();
			}
			int duration = -dx * 100 / 300;
			smoothScrollBy(dx, duration);
		}
	}

	void scrollSingel() {
		int[] location = new int[2];
		getSelectedView().getLocationOnScreen(location);
		int left = location[0];
		int right = location[0] + getSelectedView().getWidth();
		int imgW = getSelectedView().getWidth();
		left = (int) (left + (1.0D - this.mPositionManager.getItemScaleXValue()) * imgW / 2.0D);
		right = (int) (left + imgW * this.mPositionManager.getItemScaleXValue());

		Log.d("FocusedRelativeLayout", "scroll left = " + location[0] + ", right = " + right);
		if ((right >= this.mScreenWidth) && (!this.mOutsieScroll)) {
			int dx = right - this.mScreenWidth + this.mViewRight;

			smoothScrollBy(dx, 100);
			return;
		}
		getLocationOnScreen(location);
		Log.d("FocusedRelativeLayout", "scroll conrtainer left = " + this.mStartX);
		if ((left < this.mStartX) && (!this.mOutsieScroll)) {
			int dx = left - this.mStartX;
			if (this.mScroller.getCurrX() > Math.abs(dx))
				smoothScrollBy(dx, 100);
			else
				smoothScrollBy(-this.mScroller.getCurrX(), 100);
		}
	}

	private boolean containView(View v) {
		Rect containerRect = new Rect();
		Rect viewRect = new Rect();

		getGlobalVisibleRect(containerRect);
		v.getGlobalVisibleRect(viewRect);
		if ((containerRect.left <= viewRect.left) && (containerRect.right >= viewRect.right) && (containerRect.top <= viewRect.top) && (containerRect.bottom >= viewRect.bottom)) {
			return true;
		}

		return false;
	}

	public void smoothScrollTo(int fx, int duration) {
		int dx = fx - this.mScroller.getFinalX();
		smoothScrollBy(dx, duration);
	}

	public void smoothScrollBy(int dx, int duration) {
		Log.w("FocusedRelativeLayout", "smoothScrollBy dx = " + dx);
		this.mScroller.startScroll(this.mScroller.getFinalX(), this.mScroller.getFinalY(), dx, this.mScroller.getFinalY(), duration);
		invalidate();
	}

	private boolean checkFocusPosition() {
		if ((this.mPositionManager.getCurrentRect() == null) || (!hasFocus())) {
			return false;
		}

		Rect dstRect = this.mPositionManager.getDstRectAfterScale(true);
		Log.d("FocusedRelativeLayout", "checkFocusPosition this.mPositionManager.getCurrentRect() = " + this.mPositionManager.getCurrentRect() + ", this.mPositionManager.getDstRectAfterScale(true) = " + this.mPositionManager.getDstRectAfterScale(true));
		if ((Math.abs(dstRect.left - this.mPositionManager.getCurrentRect().left) > 5) || (Math.abs(dstRect.right - this.mPositionManager.getCurrentRect().right) > 5) || (Math.abs(dstRect.top - this.mPositionManager.getCurrentRect().top) > 5)
				|| (Math.abs(dstRect.bottom - this.mPositionManager.getCurrentRect().bottom) > 5)) {
			return true;
		}

		return false;
	}

	public void computeScroll() {
		if (this.mScroller.computeScrollOffset()) {
			scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
			Log.d("FocusedRelativeLayout", "computeScroll mScroller.getCurrX() = " + this.mScroller.getCurrX());
		}

		super.computeScroll();
	}

	class FocusedLayoutPositionManager extends FocusedBasePositionManager {
		public FocusedLayoutPositionManager(Context context, View container) {
			super(context, container);
		}

		public Rect getDstRectBeforeScale(boolean isBeforeScale) {
			View selectedView = getSelectedView();
			if (selectedView == null) {
				return null;
			}
			Rect imgRect = new Rect();
			Rect gridViewRect = new Rect();

			if ((selectedView instanceof FocusedRelativeLayout.ScalePostionInterface)) {
				FocusedRelativeLayout.ScalePostionInterface face = (FocusedRelativeLayout.ScalePostionInterface) selectedView;
				if (face.getIfScale())
					imgRect = face.getScaledRect(getItemScaleXValue(), getItemScaleYValue(), true);
				else
					imgRect = face.getScaledRect(getItemScaleXValue(), getItemScaleYValue(), false);
			} else {
				selectedView.getGlobalVisibleRect(imgRect);
				int imgW = imgRect.right - imgRect.left;
				int imgH = imgRect.bottom - imgRect.top;
				if (!isBeforeScale) {
					imgRect.left = ((int) (imgRect.left + (1.0D - getItemScaleXValue()) * imgW / 2.0D));
					imgRect.top = ((int) (imgRect.top + (1.0D - getItemScaleYValue()) * imgH / 2.0D));
					imgRect.right = ((int) (imgRect.left + imgW * getItemScaleXValue()));
					imgRect.bottom = ((int) (imgRect.top + imgH * getItemScaleYValue()));
				}
			}
			Log.d("FocusedBasePositionManager", "getImageRect imgRect = " + imgRect);

			FocusedRelativeLayout.this.getGlobalVisibleRect(gridViewRect);

			imgRect.left -= gridViewRect.left;
			imgRect.right -= gridViewRect.left;
			imgRect.top -= gridViewRect.top;
			imgRect.bottom -= gridViewRect.top;

			imgRect.left += FocusedRelativeLayout.this.mScroller.getCurrX();
			imgRect.right += FocusedRelativeLayout.this.mScroller.getCurrX();

			imgRect.top -= getSelectedPaddingTop();
			imgRect.left -= getSelectedPaddingLeft();
			imgRect.right += getSelectedPaddingRight();
			imgRect.bottom += getSelectedPaddingBottom();

			imgRect.left += getManualPaddingLeft();
			imgRect.right += getManualPaddingRight();
			imgRect.top += getManualPaddingTop();
			imgRect.bottom += getManualPaddingBottom();

			return imgRect;
		}

		public Rect getDstRectAfterScale(boolean isShadow) {
			View selectedView = getSelectedView();
			if (selectedView == null) {
				return null;
			}
			Rect imgRect = new Rect();
			Rect gridViewRect = new Rect();

			if ((selectedView instanceof FocusedRelativeLayout.ScalePostionInterface)) {
				FocusedRelativeLayout.ScalePostionInterface face = (FocusedRelativeLayout.ScalePostionInterface) selectedView;
				imgRect = face.getScaledRect(getItemScaleXValue(), getItemScaleYValue(), false);
			} else {
				selectedView.getGlobalVisibleRect(imgRect);
			}
			Log.d("FocusedBasePositionManager", "getImageRect imgRect = " + imgRect);

			FocusedRelativeLayout.this.getGlobalVisibleRect(gridViewRect);

			imgRect.left -= gridViewRect.left;
			imgRect.right -= gridViewRect.left;
			imgRect.top -= gridViewRect.top;
			imgRect.bottom -= gridViewRect.top;

			imgRect.left += FocusedRelativeLayout.this.mScroller.getCurrX();
			imgRect.right += FocusedRelativeLayout.this.mScroller.getCurrX();

			if ((isShadow) && (isLastFrame())) {
				imgRect.top -= getSelectedShadowPaddingTop();
				imgRect.left -= getSelectedShadowPaddingLeft();
				imgRect.right += getSelectedShadowPaddingRight();
				imgRect.bottom += getSelectedShadowPaddingBottom();
			} else {
				imgRect.top -= getSelectedPaddingTop();
				imgRect.left -= getSelectedPaddingLeft();
				imgRect.right += getSelectedPaddingRight();
				imgRect.bottom += getSelectedPaddingBottom();
			}

			imgRect.left += getManualPaddingLeft();
			imgRect.right += getManualPaddingRight();
			imgRect.top += getManualPaddingTop();
			imgRect.bottom += getManualPaddingBottom();

			return imgRect;
		}

		public void drawChild(Canvas canvas) {
		}
	}

	class HotScroller extends Scroller {
		public HotScroller(Context context, Interpolator interpolator, boolean flywheel) {
			super(context, interpolator, flywheel);
		}

		public HotScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		public HotScroller(Context context) {
			super(context, new AccelerateDecelerateInterpolator());
		}

		public boolean computeScrollOffset() {
			boolean isFinished = isFinished();
			boolean needInvalidate = FocusedRelativeLayout.this.checkFocusPosition();
			Log.d("FocusedRelativeLayout", "computeScrollOffset isFinished = " + isFinished + ", mOutsieScroll = " + FocusedRelativeLayout.this.mOutsieScroll + ", needInvalidate = " + needInvalidate + ", this = " + this);
			if ((FocusedRelativeLayout.this.mOutsieScroll) || (!isFinished) || (needInvalidate)) {
				FocusedRelativeLayout.this.invalidate();
			}
			FocusedRelativeLayout.this.init();
			return super.computeScrollOffset();
		}
	}

	class NodeInfo {
		public int index;
		public View fromLeft;
		public View fromRight;
		public View fromUp;
		public View fromDown;

		NodeInfo() {
		}
	}

	public static abstract interface ScalePostionInterface {
		public abstract Rect getScaledRect(float paramFloat1, float paramFloat2, boolean paramBoolean);

		public abstract boolean getIfScale();
	}
}