package io.viva.tv.app.widget;

import io.viva.tv.app.widget.adapter.AbsCoverFlowAdapter;
import io.viva.tv.app.widget.adapter.ReflectingImageAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Adapter;

public class AbsCoverFlow extends Gallery {
	private static final String TAG = "CoverFlow";
	private static final int DEFAULT_IMAGE_WIDTH = 200;
	private static final int DEFAULT_IMAGE_HEIGHT = 200;
	private static final float DEFAULT_IMAGE_REFLECTION_RATIO = 0.3F;
	private static final int DEFAULT_REFLECTION_GAP = 4;
	private static final int DEFAULT_SPACING = 0;
	private static final int DEFULT_SELECTOR_PADDING_LEFT = 10;
	private static final int DEFULT_SELECTOR_PADDING_TOP = 10;
	private static final int DEFULT_SELECTOR_PADDING_RIGHT = 10;
	private static final int DEFULT_SELECTOR_PADDING_BOTTOM = 10;
	private static final int MAX_SHOW_COVER_FLOW_TEXT_COUNT = 2;
	private static final int DEFAULT_MAX_COVERFLOW_TEXT_LINE = 2;
	private static final boolean DBG = false;
	private static final int DEFAULT_MAX_COVERFLOW_TEXT_CACHE_COUNT = 100;
	private static final int TEXT_SHADOW_COLOR = -16737793;
	private final Camera mCamera = new Camera();
	int mMaxRotationAngle = 60;
	private int mMaxZoom = -120;
	int mCoveflowCenter;
	private int imageHeight;
	private int imageWidth;
	private int reflectionGap;
	private boolean withReflection;
	private float imageReflectionRatio;
	private Drawable mSelector;
	private Drawable mDividerDrawable;
	private int mDividerHeight;
	private int mCoverFlowSelectedTextColor;
	private int mCoverFlowTextColor;
	private boolean forceFocus;
	int mSelectorBorderWidth;
	int mSelectorBorderHeight;
	protected int mCurrentSelectedPosition = -1;
	LruCache<Integer, String[]> mCoverFlowBreakTextCache = new LruCache<Integer, String[]>(DEFAULT_MAX_COVERFLOW_TEXT_CACHE_COUNT);
	TextPaint mTextPaint;
	boolean mDrawSelectorOnTop = false;
	Rect mExactlyUserSelectedRect;
	private int mCoverFlowTextSpacing;
	private float mCoverFlowTextSize;
	private int mCoverFlowTextMaxLine;
	private int mCoverFlowTextLineHeight;
	private int mCoverflowBaselineHeight;
	MyHandler mHandler = new MyHandler();
	private boolean mIsUseMarquee = false;
	private Rect mTextClipRect = new Rect();
	private volatile long mStarMarqueetTime = 0L;
	int mTextOffsetMarquee = 0;
	int mTextSpeedMarquee = 2;
	TruncateAt mEllipsize;
	private boolean mIsDrawShadow = false;
	private float mShadowRatio = DEFAULT_IMAGE_REFLECTION_RATIO;
	private Paint mShadowPaint = new Paint();
	private Bitmap mShadowNextBitmap;
	private Bitmap mShadowPreBitmap;
	private NinePatch mShadowNextNinePatch;
	private NinePatch mShadowPreNinePatch;
	private int mShadowTopPadding = 16;
	private int mShadowBottomPadding = 25;
	private static final int mShadowColorStart = -536870912;
	private static final int mShadowColorEnd = 0;

	private void setUseMarquee(boolean paramBoolean) {
		this.mIsUseMarquee = paramBoolean;
	}

	private void startMarqueeTickTime() {
		Message localMessage = Message.obtain(this.mHandler, 1);
		removeMarqueeTickTime();
		this.mHandler.sendMessageDelayed(localMessage, 2000L);
	}

	private void removeMarqueeTickTime() {
		this.mHandler.removeMessages(1);
	}

	public float getImageHeight() {
		return this.imageHeight;
	}

	public void setImageHeight(int paramInt) {
		this.imageHeight = paramInt;
	}

	public float getImageWidth() {
		return this.imageWidth;
	}

	public void setImageWidth(int paramInt) {
		this.imageWidth = paramInt;
	}

	public float getReflectionGap() {
		return this.reflectionGap;
	}

	public void setReflectionGap(int paramInt) {
		this.reflectionGap = paramInt;
	}

	public boolean isWithReflection() {
		return this.withReflection;
	}

	public void setWithReflection(boolean paramBoolean) {
		this.withReflection = paramBoolean;
	}

	public void setImageReflectionRatio(float paramFloat) {
		this.imageReflectionRatio = paramFloat;
	}

	public float getImageReflectionRatio() {
		return this.imageReflectionRatio;
	}

	public AbsCoverFlow(Context paramContext) {
		super(paramContext);
		setStaticTransformationsEnabled(true);
		this.mTextOffsetMarquee = 0;
	}

	public AbsCoverFlow(Context paramContext, AttributeSet paramAttributeSet) {
		this(paramContext, paramAttributeSet, 16842864);
	}

	public AbsCoverFlow(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		parseAttributes(paramContext, paramAttributeSet);
		setStaticTransformationsEnabled(true);
		this.mTextOffsetMarquee = 0;
	}

	public Drawable getSelector() {
		return this.mSelector;
	}

	public int getMaxRotationAngle() {
		return this.mMaxRotationAngle;
	}

	public void setAdapter(Adapter paramAdapter) {
		if ((paramAdapter instanceof AbsCoverFlowAdapter)) {
			AbsCoverFlowAdapter localAbsCoverFlowAdapter = (AbsCoverFlowAdapter) paramAdapter;
			localAbsCoverFlowAdapter.setWidth(this.imageWidth);
			localAbsCoverFlowAdapter.setHeight(this.imageHeight);
			if (this.withReflection) {
				ReflectingImageAdapter localReflectingImageAdapter = new ReflectingImageAdapter(localAbsCoverFlowAdapter);
				localReflectingImageAdapter.setReflectionGap(this.reflectionGap);
				localReflectingImageAdapter.setWidthRatio(this.imageReflectionRatio);
				localReflectingImageAdapter.setWidth(this.imageWidth);
				localReflectingImageAdapter.setHeight(this.imageHeight * (1.0F + this.imageReflectionRatio));
				super.setAdapter(localReflectingImageAdapter);
			} else {
				super.setAdapter(localAbsCoverFlowAdapter);
			}
		} else {
			super.setAdapter(paramAdapter);
		}
	}

	public void setMaxRotationAngle(int paramInt) {
		this.mMaxRotationAngle = paramInt;
	}

	public int getMaxZoom() {
		return this.mMaxZoom;
	}

	public void setMaxZoom(int paramInt) {
		this.mMaxZoom = paramInt;
	}

	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	static int getCenterOfView(View paramView) {
		return paramView.getLeft() + paramView.getWidth() / 2;
	}

	public void reset() {
		this.mCoverFlowBreakTextCache.evictAll();
	}

	protected boolean getChildStaticTransformation(View paramView, Transformation paramTransformation) {
		int i = getCenterOfView(paramView);
		int j = paramView.getWidth();
		int k = 0;
		paramTransformation.clear();
		paramTransformation.setTransformationType(Transformation.TYPE_MATRIX);
		if (i == this.mCoveflowCenter) {
			transformImageBitmap(paramView, paramTransformation, 0);
		} else {
			k = (int) ((this.mCoveflowCenter - i) / j * this.mMaxRotationAngle / 2.0F);
			if (Math.abs(k) > this.mMaxRotationAngle)
				k = k < 0 ? -this.mMaxRotationAngle : this.mMaxRotationAngle;
			transformImageBitmap(paramView, paramTransformation, k);
		}
		return true;
	}

	protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong) {
		int i = getRelSelectedPosition();
		int j = getPositionForView(paramView);
		if ((i == j) && (this.mIsDrawShadow))
			drawShadow(paramCanvas, paramView, paramLong);
		boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
		if (this.mEllipsize == null) {
			drawCoverFlowText(paramCanvas, paramView, paramLong, j);
		} else if (isCoverFlowScrolling()) {
			this.mTextOffsetMarquee = 0;
			drawCoverFlowTextInTruncateMode(paramCanvas, paramView, paramLong, j, true);
		} else {
			drawCoverFlowTextInTruncateMode(paramCanvas, paramView, paramLong, j, false);
		}
		return bool;
	}

	private void drawShadow(Canvas paramCanvas, View paramView, long paramLong) {
		int i = getSelectedItemPosition();
		Rect localRect = new Rect();
		localRect = getRectOfView(paramView);
		if (paramView != null) {
			paramCanvas.save();
			renderChildShadowPre(paramCanvas, paramView, i, 255, localRect);
			paramCanvas.restore();
			renderChildShadowNext(paramCanvas, paramView, i, 255, localRect);
		}
	}

	public void setEllipsize(TruncateAt paramTruncateAt) {
		this.mEllipsize = paramTruncateAt;
	}

	private void drawCoverFlowTextInTruncateMode(Canvas paramCanvas, View paramView, long paramLong, int paramInt, boolean paramBoolean) {
		if ((!needDrawCoverFlowText(paramInt)) || (this.mAdapter == null) || (this.mAdapter.getItem(paramInt) == null))
			return;
		String str = "";
		Object localObject = this.mAdapter.getItem(paramInt);
		try {
			str = (String) localObject;
		} catch (Exception localException) {
		}
		if (str.isEmpty())
			return;
		initialDrawTextEnv(paramInt);
		Rect localRect = getCoverflowTextRect(paramView, str);
		int i = paramView.getWidth();
		int j = this.mCoverFlowTextSpacing;
		String[] arrayOfString = getTextInTruncateMode(str, paramInt);
		drawTextInTruncateMode(paramCanvas, arrayOfString[0], localRect, i, paramInt, paramBoolean);
	}

	private void drawCoverFlowText(Canvas paramCanvas, View paramView, long paramLong, int paramInt) {
		if ((!needDrawCoverFlowText(paramInt)) || (this.mAdapter == null) || (this.mAdapter.getItem(paramInt) == null))
			return;
		String str = "";
		Object localObject = this.mAdapter.getItem(paramInt);
		try {
			str = (String) localObject;
		} catch (Exception localException) {
		}
		if (str.isEmpty())
			return;
		initialDrawTextEnv(paramInt);
		Rect localRect = getCoverflowTextRect(paramView, str);
		int i = paramView.getWidth();
		int j = this.mCoverFlowTextSpacing;
		String[] arrayOfString = getText(str, i, paramInt);
		drawText(paramCanvas, arrayOfString, localRect, i);
	}

	private void initialDrawTextEnv(int paramInt) {
		if (this.mTextPaint == null) {
			this.mTextPaint = new TextPaint();
			this.mTextPaint.setTextSize(this.mCoverFlowTextSize);
			Paint.FontMetricsInt localFontMetricsInt = new Paint.FontMetricsInt();
			this.mTextPaint.getFontMetricsInt(localFontMetricsInt);
			this.mCoverFlowTextLineHeight = (localFontMetricsInt.bottom - localFontMetricsInt.top);
			this.mCoverflowBaselineHeight = localFontMetricsInt.top;
		}
		this.mTextPaint.setTextAlign(Paint.Align.CENTER);
		int i = 0;
		if (this.mCurrentSelectedPosition >= 0)
			i = this.mCurrentSelectedPosition == paramInt ? 1 : 0;
		else
			i = paramInt == getRelSelectedPosition() ? 1 : 0;
		if ((i != 0) && ((this.gainFocus) || (this.forceFocus))) {
			this.mTextPaint.setColor(this.mCoverFlowSelectedTextColor);
			this.mTextPaint.setShadowLayer(20.0F, 0.0F, 0.0F, -16737793);
		} else {
			this.mTextPaint.setColor(this.mCoverFlowTextColor);
			this.mTextPaint.setShadowLayer(0.0F, 0.0F, 0.0F, getResources().getColor(17170445));
		}
	}

	private String[] getText(String paramString, int paramInt1, int paramInt2) {
		String[] arrayOfString = (String[]) this.mCoverFlowBreakTextCache.get(Integer.valueOf(paramInt2));
		if (arrayOfString == null) {
			arrayOfString = getBreakStringArray(paramString, paramInt1);
			this.mCoverFlowBreakTextCache.put(Integer.valueOf(paramInt2), arrayOfString);
		}
		return arrayOfString;
	}

	private String[] getTextInTruncateMode(String paramString, int paramInt) {
		String[] arrayOfString = (String[]) this.mCoverFlowBreakTextCache.get(Integer.valueOf(paramInt));
		if (arrayOfString == null) {
			arrayOfString = new String[1];
			arrayOfString[0] = "";
			int tmp38_37 = 0;
			String[] tmp38_36 = arrayOfString;
			tmp38_36[tmp38_37] = (tmp38_36[tmp38_37] + paramString);
			this.mCoverFlowBreakTextCache.put(Integer.valueOf(paramInt), arrayOfString);
		}
		return arrayOfString;
	}

	public void setForceGainFocus(boolean paramBoolean) {
		this.forceFocus = paramBoolean;
		this.mSelectedPosition = getSelectedItemPosition();
		Log.d("CoverFlow", "setForceGainFocus forceFocus = " + paramBoolean + ", mSelectedPosition = " + this.mSelectedPosition);
		if (paramBoolean)
			positionSelector(this.mSelectedPosition, getSelectedView());
		else
			clearSelectorRect();
		invalidate();
	}

	private void clearSelectorRect() {
		this.mSelectorRect.setEmpty();
	}

	private Rect getCoverflowTextRect(View paramView, String paramString) {
		Rect localRect = new Rect();
		localRect.left = paramView.getLeft();
		localRect.top = paramView.getTop();
		localRect.right = paramView.getRight();
		localRect.bottom = paramView.getBottom();
		return localRect;
	}

	public void setCoverFlowTextColor(int paramInt) {
		this.mCoverFlowTextColor = paramInt;
	}

	public void setCoverFlowSelectedTextColor(int paramInt) {
		this.mCoverFlowSelectedTextColor = paramInt;
	}

	public void setCoverFlowTextSpacing(int paramInt) {
		this.mCoverFlowTextSpacing = paramInt;
	}

	public void setCoverFlowTextSize(float paramFloat) {
		this.mCoverFlowTextSize = paramFloat;
	}

	public void setCoverFlowTextMaxLine(int paramInt) {
		this.mCoverFlowTextMaxLine = paramInt;
	}

	private void drawText(Canvas paramCanvas, String[] paramArrayOfString, Rect paramRect, int paramInt) {
		int i = paramRect.left;
		int j = paramRect.bottom + this.mCoverFlowTextSpacing;
		int k = this.mCoverFlowTextLineHeight;
		int m = paramArrayOfString.length;
		for (int n = 0; n < m; n++) {
			String str = paramArrayOfString[n];
			if ((str != null) && (!str.isEmpty()))
				paramCanvas.drawText(str, i + paramInt / 2, j - this.mCoverflowBaselineHeight + 0.5F + k * n, this.mTextPaint);
		}
	}

	private long now() {
		return SystemClock.uptimeMillis();
	}

	private void startMarquee() {
		if ((this.mEllipsize != null) && (this.mEllipsize == TruncateAt.MARQUEE)) {
			this.mStarMarqueetTime = now();
			this.mTextOffsetMarquee = 0;
			this.mIsUseMarquee = false;
			startMarqueeTickTime();
		}
	}

	private void endMarquee() {
		if ((this.mEllipsize != null) && (this.mEllipsize == TruncateAt.MARQUEE)) {
			this.mIsUseMarquee = false;
			removeMarqueeTickTime();
		}
	}

	protected void OnScrolling(boolean paramBoolean) {
		if (paramBoolean)
			endMarquee();
		else
			startMarquee();
		super.OnScrolling(paramBoolean);
	}

	protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
		if (paramBoolean)
			startMarquee();
		else
			endMarquee();
		super.onFocusChanged(paramBoolean, paramInt, paramRect);
	}

	private void drawTextInTruncateMode(Canvas paramCanvas, String paramString, Rect paramRect, int paramInt1, int paramInt2, boolean paramBoolean) {
		int i = paramRect.left;
		int j = paramRect.bottom + this.mCoverFlowTextSpacing;
		int k = this.mCoverFlowTextLineHeight;
		paramCanvas.save();
		this.mTextClipRect.left = paramRect.left;
		this.mTextClipRect.right = paramRect.right;
		this.mTextClipRect.top = paramRect.bottom;
		this.mTextClipRect.bottom = (this.mTextClipRect.top + this.mCoverFlowTextSpacing + this.mCoverFlowTextLineHeight);
		paramCanvas.clipRect(this.mTextClipRect);
		int m = paramInt1 / 2;
		if ((paramString != null) && (!paramString.isEmpty())) {
			int i1 = (int) StaticLayout.getDesiredWidth(paramString, 0, paramString.length(), this.mTextPaint);
			if (this.mEllipsize != null) {
				int n;
				if (this.mEllipsize == TruncateAt.START) {
					n = i;
					this.mTextPaint.setTextAlign(Paint.Align.LEFT);
					paramCanvas.drawText(paramString, n, j - this.mCoverflowBaselineHeight + 0.5F, this.mTextPaint);
				} else if (this.mEllipsize == TruncateAt.END) {
					n = i + paramInt1;
					this.mTextPaint.setTextAlign(Paint.Align.RIGHT);
					paramCanvas.drawText(paramString, n, j - this.mCoverflowBaselineHeight + 0.5F, this.mTextPaint);
				} else if (this.mEllipsize == TruncateAt.MIDDLE) {
					n = i + paramInt1 / 2;
					this.mTextPaint.setTextAlign(Paint.Align.CENTER);
					paramCanvas.drawText(paramString, n, j - this.mCoverflowBaselineHeight + 0.5F, this.mTextPaint);
				} else if (i1 < paramInt1) {
					this.mTextPaint.setTextAlign(Paint.Align.CENTER);
					n = i + paramInt1 / 2;
					paramCanvas.drawText(paramString, n, j - this.mCoverflowBaselineHeight + 0.5F, this.mTextPaint);
				} else {
					this.mTextPaint.setTextAlign(Paint.Align.LEFT);
					n = i;
					if (paramBoolean)
						paramCanvas.drawText(paramString, n, j - this.mCoverflowBaselineHeight + 0.5F, this.mTextPaint);
					else if ((paramInt2 == getRelSelectedPosition()) && ((this.gainFocus) || (this.forceFocus))) {
						if (this.mIsUseMarquee) {
							if (now() - this.mStarMarqueetTime > 2500L) {
								this.mTextOffsetMarquee -= this.mTextSpeedMarquee;
								paramCanvas.drawText(paramString, this.mTextOffsetMarquee + n, j - this.mCoverflowBaselineHeight + 0.5F, this.mTextPaint);
								if (this.mTextOffsetMarquee < -(i1 - (paramInt1 - m)))
									paramCanvas.drawText(paramString, this.mTextOffsetMarquee + i1 + m + n, j - this.mCoverflowBaselineHeight + 0.5F, this.mTextPaint);
								if (this.mTextOffsetMarquee < -(i1 + m))
									this.mTextOffsetMarquee = 0;
							} else {
								paramCanvas.drawText(paramString, n, j - this.mCoverflowBaselineHeight + 0.5F, this.mTextPaint);
							}
							postInvalidateDelayed(80L, this.mTextClipRect.left, this.mTextClipRect.top, this.mTextClipRect.right, this.mTextClipRect.bottom);
						} else {
							paramCanvas.drawText(paramString, n, j - this.mCoverflowBaselineHeight + 0.5F, this.mTextPaint);
						}
					} else
						paramCanvas.drawText(paramString, n, j - this.mCoverflowBaselineHeight + 0.5F, this.mTextPaint);
				}
			}
		}
		paramCanvas.restore();
	}

	private String[] getBreakStringArray(String paramString, int paramInt) {
		int i = this.mCoverFlowTextMaxLine;
		String[] arrayOfString = new String[i];
		int j = paramString.indexOf("\n");
		if (j > 0)
			paramString.replaceAll("\n", "");
		int k = 0;
		int m = 0;
		int n = paramString.length();
		int i1 = (int) StaticLayout.getDesiredWidth(paramString, k, n, this.mTextPaint);
		int i2 = i1 / paramInt;
		if (i2 == 0) {
			arrayOfString[0] = paramString;
		} else {
			int i3 = n / i2;
			for (int i4 = 0; i4 < i; i4++) {
				int i5 = (int) StaticLayout.getDesiredWidth(paramString, k, n, this.mTextPaint);
				if (i5 <= paramInt) {
					m = n;
					arrayOfString[i4] = paramString.substring(k, m);
					break;
				}
				m = (i4 + 1) * i3 >= n ? n : (i4 + 1) * i3;
				int i6 = getLineEndIndex(paramString, k, m, paramInt);
				if ((i4 == i - 1) && (i6 < n))
					arrayOfString[i4] = (paramString.substring(k, i6 - 2) + "...");
				else
					arrayOfString[i4] = paramString.substring(k, i6);
				k = i6;
			}
		}
		return arrayOfString;
	}

	private int getLineEndIndex(String paramString, int paramInt1, int paramInt2, int paramInt3) {
		int i = paramString.length();
		int j = (int) StaticLayout.getDesiredWidth(paramString, paramInt1, paramInt2, this.mTextPaint);
		if (j > paramInt3)
			while (j >= paramInt3) {
				paramInt2--;
				j = (int) StaticLayout.getDesiredWidth(paramString, paramInt1, paramInt2, this.mTextPaint);
			}
		while (j < paramInt3) {
			if (paramInt2 < i - 1)
				paramInt2++;
			j = (int) StaticLayout.getDesiredWidth(paramString, paramInt1, paramInt2, this.mTextPaint);
		}
		return paramInt2;
	}

	private void drawScaleableView(Canvas paramCanvas, View paramView, long paramLong) {
	}

	private boolean needDrawCoverFlowText(int paramInt) {
		int i = getRelSelectedPosition();
		int j = Math.abs(paramInt - i);
		return j <= 2;
	}

	protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		this.mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
	}

	public void setSelector(Drawable paramDrawable) {
		this.mSelector = paramDrawable;
	}

	public void setDividerDrawable(Drawable paramDrawable) {
		this.mDividerDrawable = paramDrawable;
		if (this.mDividerDrawable != null)
			this.mDividerHeight = this.mDividerDrawable.getIntrinsicHeight();
		else
			this.mDividerHeight = 0;
	}

	public void setSelectorBorderWidth(int paramInt) {
		this.mSelectorBorderWidth = paramInt;
	}

	public void setSelectorBorderHeight(int paramInt) {
		this.mSelectorBorderHeight = paramInt;
	}

	protected void dispatchDraw(Canvas paramCanvas) {
		drawDivider(paramCanvas);
		if (!this.mDrawSelectorOnTop)
			drawSelector(paramCanvas);
		super.dispatchDraw(paramCanvas);
		if (this.mDrawSelectorOnTop)
			drawSelector(paramCanvas);
	}

	void drawDivider(Canvas paramCanvas) {
		if (this.mDividerDrawable != null) {
			Rect localRect = new Rect();
			localRect.left = getPaddingLeft();
			localRect.right = (getRight() - getLeft() - getPaddingRight());
			localRect.top = (getBottom() - getTop() - getPaddingBottom());
			localRect.bottom = (localRect.top + this.mDividerHeight);
			drawDivider(paramCanvas, localRect);
		}
	}

	private void drawDivider(Canvas paramCanvas, Rect paramRect) {
		Drawable localDrawable = this.mDividerDrawable;
		localDrawable.setBounds(paramRect);
		localDrawable.draw(paramCanvas);
	}

	public void setDrawSelectorOnTop(boolean paramBoolean) {
		this.mDrawSelectorOnTop = paramBoolean;
	}

	/** @deprecated */
	public void setExactlyUserSelectedRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		this.mExactlyUserSelectedRect = new Rect(paramInt1, paramInt2, paramInt3, paramInt4);
	}

	/** @deprecated */
	public void clearExactlyUserSelectedRect() {
		this.mExactlyUserSelectedRect = null;
	}

	public void getFocusedRect(Rect paramRect) {
		View localView = getSelectedView();
		if ((localView != null) && (localView.getParent() == this)) {
			localView.getFocusedRect(paramRect);
			offsetDescendantRectToMyCoords(localView, paramRect);
		} else {
			this.mSelectorRect.setEmpty();
		}
	}

	protected void drawSelector(Canvas paramCanvas) {
		if (((this.mSelectedPosition != -1) || (this.forceFocus)) && (!this.mSelectorRect.isEmpty()) && (this.mSelector != null)) {
			this.mSelector.setBounds(this.mSelectorRect);
			int i = Math.abs(getCenterOfView(getSelectedView()) - getCenterOfCoverflow());
			float f1 = 1.0F * i / (getSelectedView().getWidth() + this.mSpacing);
			float f2 = SelectorAlphaChange(f1);
			this.mSelector.setAlpha((int) (f2 * 255.0F));
			this.mSelector.draw(paramCanvas);
		}
	}

	private float SelectorAlphaInterpolate(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7) {
		float f = 0.0F;
		if (paramFloat7 < 0.0F)
			f = paramFloat1;
		if (paramFloat7 > 1.0F)
			f = paramFloat2;
		if ((paramFloat7 < paramFloat4) && (paramFloat7 >= 0.0D))
			f = paramFloat1 + (paramFloat7 - 0.0F) * (paramFloat3 - paramFloat1) / (paramFloat4 - 0.0F);
		if ((paramFloat7 < paramFloat6) && (paramFloat7 >= paramFloat4))
			f = paramFloat3 + (paramFloat7 - paramFloat4) * (paramFloat5 - paramFloat3) / (paramFloat6 - paramFloat4);
		if (paramFloat7 > paramFloat6)
			f = paramFloat5 + (paramFloat7 - paramFloat6) * (paramFloat2 - paramFloat5) / (1.0F - paramFloat6);
		return f;
	}

	private float SelectorAlphaChange(float paramFloat) {
		float f1 = DEFAULT_IMAGE_REFLECTION_RATIO * 2;
		float f2 = DEFAULT_IMAGE_REFLECTION_RATIO;
		float f3 = DEFAULT_IMAGE_REFLECTION_RATIO;
		float f4 = DEFAULT_IMAGE_REFLECTION_RATIO * 2;
		return SelectorAlphaInterpolate(1.0F, 1.0F, f1, f2, f3, f4, paramFloat);
	}

	void transformImageBitmap(View paramView, Transformation paramTransformation, int paramInt) {
		int i = paramView.getMeasuredHeight();
		int j = paramView.getMeasuredWidth();
		int k = Math.abs(paramInt);
		this.mCamera.save();
		Matrix localMatrix = paramTransformation.getMatrix();
		this.mCamera.translate(0.0F, 0.0F, 120.0F);
		if (k < this.mMaxRotationAngle) {
			float f = this.mMaxZoom + k * 2;
			this.mCamera.translate(0.0F, 0.0F, f);
		}
		this.mCamera.rotateY(paramInt);
		this.mCamera.getMatrix(localMatrix);
		localMatrix.preTranslate(-(j / 2.0F), -(i / 2.0F));
		localMatrix.postTranslate(j / 2.0F, i / 2.0F);
		this.mCamera.restore();
	}

	private void parseAttributes(Context paramContext, AttributeSet paramAttributeSet) {
		setSpacing(0);
		setSelectorPadding(DEFULT_SELECTOR_PADDING_LEFT, DEFULT_SELECTOR_PADDING_TOP, DEFULT_SELECTOR_PADDING_RIGHT, DEFULT_SELECTOR_PADDING_BOTTOM);
		setDrawSelectorOnTop(true);
		setClipChildren(false);
		setClipToPadding(false);
		setCoverFlowTextColor(-6710887);
		setCoverFlowSelectedTextColor(-1);
		setCoverFlowTextSize(24.0F);
		setCoverFlowTextMaxLine(2);
	}

	public void setShadowTopPadding(int paramInt) {
		this.mShadowTopPadding = paramInt;
	}

	public void setShadowBottomPadding(int paramInt) {
		this.mShadowBottomPadding = paramInt;
	}

	public void setDrawShadowImage(boolean paramBoolean) {
		this.mIsDrawShadow = paramBoolean;
	}

	int getShadowWidth(View paramView) {
		return (int) (paramView.getWidth() * this.mShadowRatio);
	}

	int getShadowHeight(View paramView) {
		return (int) (paramView.getHeight() * this.mShadowRatio);
	}

	private Rect getShadowDrawUpDownRect(boolean paramBoolean, Rect paramRect, View paramView) {
		Rect localRect = new Rect(paramRect);
		int i = getShadowHeight(paramView);
		if (paramBoolean) {
			localRect.bottom = (localRect.top + 1);
			localRect.top = (localRect.bottom - (int) (i * paramView.getScaleY()));
		} else {
			localRect.top = (localRect.bottom - 1);
			localRect.bottom += (int) (i * paramView.getScaleY());
		}
		return localRect;
	}

	private Rect getShadowDrawLeftRightRect(boolean paramBoolean, Rect paramRect, View paramView) {
		Rect localRect = new Rect(paramRect);
		int i = getShadowWidth(paramView);
		if (paramBoolean) {
			localRect.right = (localRect.left + 1);
			localRect.left -= (int) (i * paramView.getScaleX());
		} else {
			localRect.left = (localRect.right - 1);
			localRect.right += (int) (i * paramView.getScaleX());
		}
		return localRect;
	}

	private Rect getShadowDrawLeftRect(Rect paramRect, View paramView) {
		Rect localRect = new Rect(paramRect);
		int i = getShadowWidth(paramView);
		localRect.right = (localRect.left + 1);
		localRect.left -= (int) (i * paramView.getScaleX());
		return localRect;
	}

	private Rect getShadowDrawRightRect(Rect paramRect, View paramView) {
		Rect localRect = new Rect(paramRect);
		int i = getShadowWidth(paramView);
		localRect.left = (localRect.right - 1);
		localRect.right += (int) (i * paramView.getScaleX());
		return localRect;
	}

	private Bitmap loadShadowBitmap(int paramInt) {
		Resources localResources = getResources();
		Bitmap localBitmap = BitmapFactory.decodeResource(localResources, paramInt);
		return localBitmap;
	}

	Rect getShadowPreClipRect(int paramInt) {
		int i = paramInt - 1;
		View localView = getChildAt(i);
		if (localView != null)
			return getRectOfView(localView);
		return null;
	}

	Rect getShadowNextClipRect(int paramInt) {
		int i = paramInt + 1;
		View localView = getChildAt(i);
		if (localView != null)
			return getRectOfView(localView);
		return null;
	}

	Path getShadowPreClipPath(int paramInt) {
		int i = paramInt - 1;
		View localView = getChildAt(i);
		if (localView != null)
			return getPathOfView(localView);
		return null;
	}

	Path getPathOfView(View paramView) {
		int i = paramView.getLeft();
		int j = paramView.getRight();
		int k = paramView.getTop();
		int m = paramView.getBottom();
		float f1 = paramView.getPivotX();
		float f2 = paramView.getPivotY();
		Path localPath = new Path();
		localPath.addRect(0.0F - f1, 0.0F - f2 + this.mShadowTopPadding, j - i - f1, m - k - f2 - this.mShadowBottomPadding, Path.Direction.CCW);
		Transformation localTransformation = new Transformation();
		getChildStaticTransformation(paramView, localTransformation);
		Matrix localMatrix1 = localTransformation.getMatrix();
		Matrix localMatrix2 = new Matrix(localMatrix1);
		localMatrix2.postTranslate(f1 + i, f2 + k);
		localPath.transform(localMatrix2);
		return localPath;
	}

	Path getShadowNextClipPath(int paramInt) {
		int i = paramInt + 1;
		View localView = getChildAt(i);
		if (localView != null)
			return getPathOfView(localView);
		return null;
	}

	public void setImageShadowWidthRadio(float paramFloat) {
		this.mShadowRatio = paramFloat;
	}

	private void renderChildShadowPre(Canvas paramCanvas, View paramView, int paramInt1, int paramInt2, Rect paramRect) {
		Rect localRect = getShadowDrawLeftRect(paramRect, paramView);
		Path localPath = getShadowPreClipPath(paramInt1 - this.mFirstPosition);
		if (localPath != null) {
			paramCanvas.save();
			this.mShadowPaint.setAlpha(paramInt2);
			this.mShadowPaint.setColor(-1);
			LinearGradient localLinearGradient = new LinearGradient(localRect.left, localRect.top, localRect.right, localRect.top, 0, -536870912, Shader.TileMode.CLAMP);
			this.mShadowPaint.setShader(localLinearGradient);
			this.mShadowPaint.setAntiAlias(true);
			paramCanvas.drawPath(localPath, this.mShadowPaint);
			paramCanvas.restore();
		}
	}

	private void renderChildShadowNext(Canvas paramCanvas, View paramView, int paramInt1, int paramInt2, Rect paramRect) {
		Rect localRect = getShadowDrawRightRect(paramRect, paramView);
		Path localPath = getShadowNextClipPath(paramInt1 - this.mFirstPosition);
		if (localPath != null) {
			paramCanvas.save();
			this.mShadowPaint.setAlpha(paramInt2);
			this.mShadowPaint.setAlpha(paramInt2);
			this.mShadowPaint.setColor(-1);
			LinearGradient localLinearGradient = new LinearGradient(localRect.left, localRect.top, localRect.right, localRect.top, -536870912, 0, Shader.TileMode.CLAMP);
			this.mShadowPaint.setShader(localLinearGradient);
			this.mShadowPaint.setAntiAlias(true);
			paramCanvas.drawPath(localPath, this.mShadowPaint);
			paramCanvas.restore();
		}
	}

	private Rect getRectOfView(View paramView) {
		if (paramView != null) {
			Rect localRect = new Rect();
			int i = paramView.getLeft();
			int j = paramView.getRight();
			int k = paramView.getTop();
			int m = paramView.getBottom();
			int n = (i + j) / 2;
			int i1 = (k + m) / 2;
			int i2 = j - i;
			int i3 = m - k;
			float f1 = n + paramView.getTranslationX();
			float f2 = i1 + paramView.getTranslationY();
			float f3 = i2 * paramView.getScaleX();
			float f4 = i3 * paramView.getScaleX();
			localRect.left = ((int) (f1 - f3 / 2.0F));
			localRect.right = ((int) (f1 + f3 / 2.0F));
			localRect.top = ((int) (f2 - f4 / 2.0F));
			localRect.bottom = ((int) (f2 + f4 / 2.0F));
			return localRect;
		}
		return null;
	}

	public static enum TruncateAt {
		START, MIDDLE, END, MARQUEE, END_SMALL;
	}

	class MyHandler extends Handler {
		static final int MSG_MARQUEE_START = 1;
		static final int MSG_MARQUEE_END = 2;

		public MyHandler() {
		}

		public MyHandler(Looper arg2) {
			super();
		}

		public void handleMessage(Message paramMessage) {
			if (paramMessage.what == 1) {
				AbsCoverFlow.this.setUseMarquee(true);
				AbsCoverFlow.this.postInvalidate();
			} else if (paramMessage.what != 2) {
				super.handleMessage(paramMessage);
			}
		}
	}
}