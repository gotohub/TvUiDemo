package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

public abstract class FocusedBasePositionManager {
	public static final String TAG = "FocusedBasePositionManager";
	private static final int DEFAULT_FRAME_RATE = 6;
	private static final int DEFAULT_FRAME = 1;
	public static final int FOCUS_SYNC_DRAW = 0;
	public static final int FOCUS_ASYNC_DRAW = 1;
	public static final int FOCUS_STATIC_DRAW = 2;
	public static final int STATE_IDLE = 0;
	public static final int STATE_DRAWING = 1;
	public static final int SCALED_FIXED_COEF = 1;
	public static final int SCALED_FIXED_X = 2;
	public static final int SCALED_FIXED_Y = 3;
	private int mCurrentFrame = 1;
	private int mFrameRate = 6;
	private int mFocusFrameRate = 2;
	private int mScaleFrameRate = 2;
	private float mScaleXValue = 1.0F;
	private float mScaleYValue = 1.0F;

	private int mScaledMode = 2;
	private int mFixedScaledX = 30;
	private int mFixedScaledY = 30;

	private int mState = 0;
	private boolean mNeedDraw = false;
	private int mode = 1;
	private Rect mSelectedPaddingRect = new Rect();
	private Rect mManualSelectedPaddingRect = new Rect();
	protected Drawable mMySelectedDrawable = null;
	private Drawable mMySelectedDrawableShadow;
	private Rect mMySelectedPaddingRectShadow;
	private boolean mIsFirstFrame = true;
	private boolean mConstrantNotDraw = false;
	private boolean mIsLastFrame = false;
	private View mSelectedView;
	private View mLastSelectedView;
	private View mContainerView;
	private boolean mHasFocus = false;
	private boolean mTransAnimation = false;
	private Context mContext;
	private Rect mLastFocusRect;
	private Rect mFocusRect;
	private Rect mCurrentRect;
	private boolean mScaleCurrentView = true;
	private boolean mScaleLastView = true;

	public FocusedBasePositionManager(Context context, View container) {
		this.mContext = context;
		this.mContainerView = container;
	}

	public void drawFrame(Canvas canvas) {
		Log.w("FocusedBasePositionManager", "drawFrame: mCurrentFrame = " + this.mCurrentFrame + ", needDraw = " + this.mNeedDraw + ", mode = " + this.mode + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", mFocusFrameRate = "
				+ this.mFocusFrameRate + ", mScaleFrameRate" + this.mScaleFrameRate);
		if (this.mode == 0)
			drawSyncFrame(canvas);
		else if (1 == this.mode)
			drawAsyncFrame(canvas);
		else if (2 == this.mode)
			drawStaticFrame(canvas);
	}

	public void setScaleMode(int mode) {
		this.mScaledMode = mode;
	}

	public void setScaleCurrentView(boolean isScale) {
		this.mScaleCurrentView = isScale;
	}

	public void setScaleLastView(boolean isScale) {
		this.mScaleLastView = isScale;
	}

	public boolean isLastFrame() {
		return this.mIsLastFrame;
	}

	public void setContrantNotDraw(boolean notDraw) {
		this.mConstrantNotDraw = notDraw;
	}

	public void setFocusDrawableVisible(boolean visible, boolean restart) {
		this.mMySelectedDrawable.setVisible(visible, restart);
	}

	public void setFocusDrawableShadowVisible(boolean visible, boolean restart) {
		this.mMySelectedDrawableShadow.setVisible(visible, restart);
	}

	public void setLastSelectedView(View v) {
		this.mLastSelectedView = v;
	}

	public void setTransAnimation(boolean transAnimation) {
		this.mTransAnimation = transAnimation;
	}

	public void setNeedDraw(boolean needDraw) {
		this.mNeedDraw = needDraw;
	}

	public void setFocusResId(int focusResId) {
		this.mMySelectedDrawable = this.mContext.getResources().getDrawable(focusResId);
		this.mSelectedPaddingRect = new Rect();
		this.mMySelectedDrawable.getPadding(this.mSelectedPaddingRect);
	}

	public void setFocusShadowResId(int focusResId) {
		this.mMySelectedDrawableShadow = this.mContext.getResources().getDrawable(focusResId);
		this.mMySelectedPaddingRectShadow = new Rect();
		this.mMySelectedDrawableShadow.getPadding(this.mMySelectedPaddingRectShadow);
	}

	public void setItemScaleValue(float scaleXValue, float scaleYValue) {
		this.mScaleXValue = scaleXValue;
		this.mScaleYValue = scaleYValue;
	}

	public void setItemScaleFixedX(int x) {
		this.mFixedScaledX = x;
	}

	public void setItemScaleFixedY(int y) {
		this.mFixedScaledY = y;
	}

	public float getItemScaleXValue() {
		return this.mScaleXValue;
	}

	public float getItemScaleYValue() {
		return this.mScaleYValue;
	}

	public Rect getCurrentRect() {
		return this.mCurrentRect;
	}

	public void setState(int s) {
		synchronized (this) {
			this.mState = s;
		}
	}

	public int getState() {
		synchronized (this) {
			return this.mState;
		}
	}

	public void setManualPadding(int left, int top, int right, int bottom) {
		this.mManualSelectedPaddingRect.left = left;
		this.mManualSelectedPaddingRect.right = right;
		this.mManualSelectedPaddingRect.top = top;
		this.mManualSelectedPaddingRect.bottom = bottom;
	}

	public int getManualPaddingLeft() {
		return this.mManualSelectedPaddingRect.left;
	}

	public int getManualPaddingRight() {
		return this.mManualSelectedPaddingRect.right;
	}

	public int getManualPaddingTop() {
		return this.mManualSelectedPaddingRect.top;
	}

	public int getManualPaddingBottom() {
		return this.mManualSelectedPaddingRect.bottom;
	}

	public int getSelectedPaddingLeft() {
		return this.mSelectedPaddingRect.left;
	}

	public int getSelectedPaddingRight() {
		return this.mSelectedPaddingRect.right;
	}

	public int getSelectedPaddingTop() {
		return this.mSelectedPaddingRect.top;
	}

	public int getSelectedPaddingBottom() {
		return this.mSelectedPaddingRect.bottom;
	}

	public int getSelectedShadowPaddingLeft() {
		return this.mMySelectedPaddingRectShadow.left;
	}

	public int getSelectedShadowPaddingRight() {
		return this.mMySelectedPaddingRectShadow.right;
	}

	public int getSelectedShadowPaddingTop() {
		return this.mMySelectedPaddingRectShadow.top;
	}

	public int getSelectedShadowPaddingBottom() {
		return this.mMySelectedPaddingRectShadow.bottom;
	}

	public void setFocus(boolean isFocus) {
		this.mHasFocus = isFocus;
	}

	public boolean hasFocus() {
		return this.mHasFocus;
	}

	public void setFocusMode(int mode) {
		this.mode = mode;
	}

	public void setFrameRate(int rate) {
		this.mFrameRate = rate;
		if (rate % 2 == 0) {
			this.mScaleFrameRate = (rate / 2);
			this.mFocusFrameRate = (rate / 2);
		} else {
			this.mScaleFrameRate = (rate / 2);
			this.mFocusFrameRate = (rate / 2 + 1);
		}
	}

	public void setSelectedView(View v) {
		Log.d("FocusedBasePositionManager", "setSelectedView v = " + v);
		this.mSelectedView = v;
	}

	public View getSelectedView() {
		return this.mSelectedView;
	}

	private void drawSyncFrame(Canvas canvas) {
		Log.d("FocusedBasePositionManager", "drawSyncFrame");
		if (getSelectedView() != null) {
			if ((this.mCurrentFrame < this.mFrameRate) && (this.mNeedDraw)) {
				if (this.mIsFirstFrame)
					drawFirstFrame(canvas, true, true);
				else
					drawOtherFrame(canvas, true, true);
			} else if (this.mCurrentFrame == this.mFrameRate) {
				drawLastFrame(canvas, true, true);
			} else if (hasFocus()) {
				drawFocus(canvas, false);
				this.mLastFocusRect = getDstRectBeforeScale(true);
			}
		} else {
			Log.w("FocusedBasePositionManager", "drawSyncFrame select view is null");
		}
	}

	private void drawAsyncFrame(Canvas canvas) {
		Log.d("FocusedBasePositionManager", "drawAsyncFrame");
		if (getSelectedView() != null) {
			boolean isScale = this.mCurrentFrame > this.mFocusFrameRate;
			if ((this.mCurrentFrame < this.mFrameRate) && (this.mNeedDraw)) {
				if (this.mIsFirstFrame)
					drawFirstFrame(canvas, isScale, !isScale);
				else
					drawOtherFrame(canvas, isScale, !isScale);
			} else if (this.mCurrentFrame == this.mFrameRate)
				drawLastFrame(canvas, isScale, !isScale);
			else if (!this.mConstrantNotDraw)
				if (hasFocus()) {
					drawFocus(canvas, false);
					this.mLastFocusRect = getDstRectBeforeScale(true);
				}
		} else {
			Log.w("FocusedBasePositionManager", "drawAsyncFrame select view is null");
		}
	}

	private void drawStaticFrame(Canvas canvas) {
		Log.d("FocusedBasePositionManager", "drawStaticFrame");
		if (getSelectedView() != null) {
			if ((this.mCurrentFrame < this.mFrameRate) && (this.mNeedDraw)) {
				if (this.mIsFirstFrame)
					drawFirstFrame(canvas, true, false);
				else
					drawOtherFrame(canvas, true, false);
			} else if (this.mCurrentFrame == this.mFrameRate)
				drawLastFrame(canvas, true, false);
			else if (!this.mConstrantNotDraw) {
				if (hasFocus()) {
					drawFocus(canvas, false);
					this.mLastFocusRect = getDstRectBeforeScale(true);
				}
			}
		} else
			Log.w("FocusedBasePositionManager", "drawStaticFrame select view is null");
	}

	private void drawFirstFrame(Canvas canvas, boolean isScale, boolean isDynamic) {
		boolean dynamic = isDynamic;
		float scaleXValue = this.mScaleXValue;
		float scaleYValue = this.mScaleXValue;
		if (1 == this.mode) {
			dynamic = false;
			scaleXValue = 1.0F;
			scaleYValue = 1.0F;
		}
		this.mIsLastFrame = false;
		if (dynamic)
			this.mFocusRect = getDstRectBeforeScale(!dynamic);
		else {
			this.mFocusRect = getDstRectAfterScale(false);
		}

		this.mCurrentRect = this.mFocusRect;
		Log.d("FocusedBasePositionManager", "drawFirstFrame: mFocusRect = " + this.mFocusRect + ", this = " + this);

		drawScale(isScale);

		if (hasFocus()) {
			drawFocus(canvas, isDynamic);
		}
		this.mIsFirstFrame = false;
		this.mCurrentFrame += 1;

		this.mContainerView.invalidate();
	}

	private void drawOtherFrame(Canvas canvas, boolean isScale, boolean isDynamic) {
		Log.i("FocusedBasePositionManager", "drawOtherFrame, this = " + this);

		this.mIsLastFrame = false;
		drawScale(isScale);

		if (hasFocus()) {
			drawFocus(canvas, isDynamic);
		}
		this.mCurrentFrame += 1;
		this.mContainerView.invalidate();
	}

	private void drawLastFrame(Canvas canvas, boolean isScale, boolean isDynamic) {
		Log.d("FocusedBasePositionManager", "drawLastFrame, this = " + this);
		this.mIsLastFrame = true;

		drawScale(isScale);

		if (hasFocus()) {
			drawFocus(canvas, isDynamic);
		}

		this.mCurrentFrame = 1;

		this.mScaleLastView = this.mScaleCurrentView;
		this.mLastSelectedView = getSelectedView();
		this.mNeedDraw = false;
		this.mIsFirstFrame = true;
		this.mLastFocusRect = getDstRectBeforeScale(true);

		setState(0);
	}

	private void scaleSelectedView() {
		View selectedView = getSelectedView();
		if (selectedView != null) {
			float diffX = this.mScaleXValue - 1.0F;
			float diffY = this.mScaleYValue - 1.0F;

			int frameRate = this.mFrameRate;
			int currentFrame = this.mCurrentFrame;

			if (1 == this.mode) {
				frameRate = this.mScaleFrameRate;
				currentFrame -= this.mFocusFrameRate;
				if (currentFrame <= 0) {
					return;
				}
			}
			float dstScaleX = 1.0F + diffX * currentFrame / frameRate;
			float dstScaleY = 1.0F + diffY * currentFrame / frameRate;
			Log.i("FocusedBasePositionManager", "scaleSelectedView: dstScaleX = " + dstScaleX + ", dstScaleY = " + dstScaleY + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", Selected View = " + selectedView + ", this = " + this);

			selectedView.setScaleX(dstScaleX);
			selectedView.setScaleY(dstScaleY);
		}
	}

	private void scaleLastSelectedView() {
		if (this.mLastSelectedView != null) {
			float diffX = this.mScaleXValue - 1.0F;
			float diffY = this.mScaleYValue - 1.0F;

			int frameRate = this.mFrameRate;
			int currentFrame = this.mCurrentFrame;

			if (1 == this.mode) {
				frameRate = this.mScaleFrameRate;
				if (currentFrame > frameRate) {
					return;
				}
			}

			currentFrame = frameRate - currentFrame;
			float dstScaleX = 1.0F + diffX * currentFrame / frameRate;
			float dstScaleY = 1.0F + diffY * currentFrame / frameRate;
			Log.i("FocusedBasePositionManager", "scaleLastSelectedView: dstScaleX = " + dstScaleX + ", dstScaleY = " + dstScaleY + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", mLastSelectedView = " + this.mLastSelectedView + ", this = "
					+ this + ", mCurrentFrame = " + this.mCurrentFrame);

			this.mLastSelectedView.setScaleX(dstScaleX);
			this.mLastSelectedView.setScaleY(dstScaleY);
		}
	}

	private void drawScale(boolean isScale) {
		Log.i("FocusedBasePositionManager", "drawScale: mCurrentFrame = " + this.mCurrentFrame + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", this = " + this + ", mScaleCurrentView = " + this.mScaleCurrentView + ", mScaleLastView = "
				+ this.mScaleLastView);

		if ((hasFocus()) && (isScale) && (this.mScaleCurrentView)) {
			scaleSelectedView();
		}

		if (this.mScaleLastView)
			scaleLastSelectedView();
	}

	private void drawFocus(Canvas canvas, boolean isDynamic) {
		Log.i("FocusedBasePositionManager", "drawFocus: mCurrentFrame = " + this.mCurrentFrame + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", this = " + this);
		if (this.mConstrantNotDraw) {
			return;
		}

		if ((isDynamic) && (this.mTransAnimation) && (this.mLastFocusRect != null) && (getState() != 0) && (!isLastFrame()))
			drawDynamicFocus(canvas);
		else
			drawStaticFocus(canvas);
	}

	private void drawStaticFocus(Canvas canvas) {
		float diffX = this.mScaleXValue - 1.0F;
		float diffY = this.mScaleYValue - 1.0F;

		int frameRate = this.mFrameRate;
		int currentFrame = this.mCurrentFrame;

		float dstScaleX = 1.0F + diffX * currentFrame / frameRate;
		float dstScaleY = 1.0F + diffY * currentFrame / frameRate;
		Log.i("FocusedBasePositionManager", "drawStaticFocus: mCurrentFrame = " + this.mCurrentFrame + ", dstScaleX = " + dstScaleX + ", dstScaleY = " + dstScaleY + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", this = " + this);

		Rect dstRect = getDstRectAfterScale(true);
		if (dstRect == null) {
			return;
		}

		this.mFocusRect = dstRect;
		this.mCurrentRect = dstRect;

		if (isLastFrame()) {
			this.mMySelectedDrawableShadow.setBounds(dstRect);
			this.mMySelectedDrawableShadow.draw(canvas);
			this.mMySelectedDrawableShadow.setVisible(true, true);
		} else {
			this.mMySelectedDrawable.setBounds(dstRect);
			this.mMySelectedDrawable.draw(canvas);
			this.mMySelectedDrawable.setVisible(true, true);
		}

		if ((this.mSelectedView != null) && (canvas != null) && ((this.mState == 0) || (isLastFrame())))
			drawChild(canvas);
	}

	private void drawDynamicFocus(Canvas canvas) {
		Rect dstRect = new Rect();

		int frameRate = this.mFrameRate;
		if (1 == this.mode) {
			frameRate = this.mFocusFrameRate;
		}

		int diffLeft = this.mFocusRect.left - this.mLastFocusRect.left;
		int diffRight = this.mFocusRect.right - this.mLastFocusRect.right;
		int diffTop = this.mFocusRect.top - this.mLastFocusRect.top;
		int diffBottom = this.mFocusRect.bottom - this.mLastFocusRect.bottom;

		dstRect.left = (this.mLastFocusRect.left + diffLeft * this.mCurrentFrame / frameRate);
		dstRect.right = (this.mLastFocusRect.right + diffRight * this.mCurrentFrame / frameRate);
		dstRect.top = (this.mLastFocusRect.top + diffTop * this.mCurrentFrame / frameRate);
		dstRect.bottom = (this.mLastFocusRect.bottom + diffBottom * this.mCurrentFrame / frameRate);
		Log.i("FocusedBasePositionManager", "drawDynamicFocus: mCurrentFrame = " + this.mCurrentFrame + ", dstRect.left = " + dstRect.left + ", dstRect.right = " + dstRect.right + ", dstRect.top = " + dstRect.top + ", dstRect.bottom = " + dstRect.bottom + ", this = " + this);

		this.mCurrentRect = dstRect;

		this.mMySelectedDrawable.setBounds(dstRect);
		this.mMySelectedDrawable.draw(canvas);
		this.mMySelectedDrawable.setVisible(true, true);

		if ((this.mSelectedView != null) && (canvas != null) && ((this.mState == 0) || (isLastFrame())))
			drawChild(canvas);
	}

	public void computeScaleXY() {
		if ((2 == this.mScaledMode) || (3 == this.mScaledMode)) {
			View v = getSelectedView();
			int[] location = new int[2];
			v.getLocationOnScreen(location);
			int width = v.getWidth();
			int height = v.getHeight();
			if (2 == this.mScaledMode) {
				this.mScaleXValue = ((width + this.mFixedScaledX) / width);
				this.mScaleYValue = this.mScaleXValue;
			} else if (3 == this.mScaledMode) {
				this.mScaleXValue = ((height + this.mFixedScaledY) / height);
				this.mScaleYValue = this.mScaleXValue;
			}

			Log.d("FocusedBasePositionManager", "computeScaleXY mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", mFixedScaledX = " + this.mFixedScaledX + ", mFixedScaledY = " + this.mFixedScaledY + ", height = " + height + ", width = " + width);
		}
	}

	public abstract Rect getDstRectBeforeScale(boolean paramBoolean);

	public abstract Rect getDstRectAfterScale(boolean paramBoolean);

	public abstract void drawChild(Canvas paramCanvas);

	public static abstract interface PositionInterface {
		public abstract void setManualPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

		public abstract void setFrameRate(int paramInt);

		public abstract void setFocusResId(int paramInt);

		public abstract void setFocusShadowResId(int paramInt);

		public abstract void setItemScaleValue(float paramFloat1, float paramFloat2);

		public abstract void setFocusMode(int paramInt);

		public abstract void setFocusViewId(int paramInt);
	}
}