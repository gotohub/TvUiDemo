package io.viva.tv.app.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class NumberIndicator extends View {
	private static final int TEXT_COLOR = -1;
	private static final int MIN_VALUE = 0;
	private static final String BELOW_MIN_VALUE = "-";
	private static final int MAX_VALUE = 999;
	private int mMaxValue = 99;
	private final Drawable mSmallBackground;
	private final Drawable mLargeBackground;
	private int mNumber;
	private String mText;
	private boolean mShowing = false;
	private Paint mPaint;

	public NumberIndicator(Context context) {
		this(context, null);
	}

	public NumberIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources resources = context.getResources();
		this.mSmallBackground = resources.getDrawable(2114191550);
		this.mLargeBackground = resources.getDrawable(2114191550);

		this.mPaint = new Paint();
		this.mPaint.setAntiAlias(true);
		this.mPaint.setColor(-1);
		float fontSize = resources.getDimension(2114125859);
		this.mPaint.setTextSize(fontSize);
		setEnlarged(false);
	}

	public int getNumber() {
		return this.mNumber;
	}

	public void setMaxNum(int max) {
		this.mMaxValue = max;
	}

	public void setNumber(int number) {
		if (number < 0) {
			setText("-");
			if (isEnlarged()) {
				setEnlarged(false);
			}
		} else if (number > this.mMaxValue) {
			setText(this.mMaxValue + "+");
			if (!isEnlarged())
				setEnlarged(true);
		} else {
			setText(String.valueOf(number));
			if (isEnlarged()) {
				setEnlarged(false);
			}
		}

		invalidate();

		this.mNumber = number;
	}

	public void setScale(float scale) {
		Animation animation = new ScaleAnimation(scale, scale, scale, scale, 1, 0.0F, 1, 1.0F);
		animation.setFillAfter(true);
		super.setAnimation(animation);
	}

	public synchronized void show() {
		if (!this.mShowing) {
			ScaleAnimation showAnimation = new ScaleAnimation(0.0F, 1.0F, 0.0F, 1.0F, 1, 0.0F, 1, 1.0F);
			showAnimation.setDuration(200L);
			showAnimation.setFillAfter(true);
			super.startAnimation(showAnimation);
			this.mShowing = true;
		}
	}

	public synchronized void hide() {
		if (this.mShowing) {
			ScaleAnimation hideAnimation = new ScaleAnimation(1.0F, 0.0F, 1.0F, 0.0F, 1, 0.0F, 1, 1.0F);
			hideAnimation.setDuration(200L);
			hideAnimation.setFillAfter(true);
			super.startAnimation(hideAnimation);
			this.mShowing = false;
		}
	}

	public synchronized void draw(Canvas canvas) {
		if ((super.getVisibility() == 0) && ((this.mShowing) || (super.getAnimation() != null)))
			super.draw(canvas);
	}

	protected synchronized void onDraw(Canvas canvas) {
		if (this.mText != null) {
			int width = super.getWidth();
			int height = super.getHeight();

			Rect bounds = new Rect();
			this.mPaint.getTextBounds(this.mText, 0, this.mText.length(), bounds);

			canvas.drawText(this.mText, (width - bounds.left - bounds.right) / 2, (height - bounds.top - bounds.bottom) / 2, this.mPaint);
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable background = super.getBackground();
		int width = background.getIntrinsicWidth();
		int height = background.getIntrinsicHeight();
		super.onMeasure(View.MeasureSpec.makeMeasureSpec(1073741824, width), View.MeasureSpec.makeMeasureSpec(1073741824, height));
	}

	private boolean isEnlarged() {
		return this.mNumber > 999;
	}

	private void setEnlarged(boolean enlarged) {
		Drawable background = enlarged ? this.mLargeBackground : this.mSmallBackground;
		super.setBackgroundDrawable(background);
		super.requestLayout();
	}

	private void setText(String text) {
		if (text != this.mText) {
			this.mText = text;
			if (this.mShowing) {
				ScaleAnimation setNumAnimation = new ScaleAnimation(1.0F, 1.1F, 1.0F, 1.1F, 1, 0.0F, 1, 1.0F);
				setNumAnimation.setDuration(100L);
				setNumAnimation.setFillAfter(true);
				setNumAnimation.setAnimationListener(new Animation.AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}

					public void onAnimationRepeat(Animation animation) {
					}

					public void onAnimationEnd(Animation animation) {
						ScaleAnimation BackScale = new ScaleAnimation(1.1F, 1.0F, 1.1F, 1.0F, 1, 0.0F, 1, 1.0F);
						BackScale.setDuration(100L);
						BackScale.setFillAfter(true);
						NumberIndicator.this.setAnimation(BackScale);
					}
				});
				super.setAnimation(setNumAnimation);
			}
		}
	}
}
