package io.viva.tv.app.widget.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.Interpolator;

public class MultiAnimator implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
	float currentValue;
	ValueAnimator mValueAnimator;
	PosInfo mPosInfo;
	TransInfo mCurTransInfo;
	static String TAG = "MultiAnimator";
	private static final boolean DEBUG_MODE = false;
	Interpolator mInterpolator;
	Animator.AnimatorListener mListener;
	ValueAnimator.AnimatorUpdateListener mUpdateListener;

	public MultiAnimator() {
		this(null);
	}

	public MultiAnimator(PosInfo posInfo) {
		this.mPosInfo = posInfo;
		this.mValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
		this.mValueAnimator.addUpdateListener(this);
		this.mValueAnimator.addListener(this);
	}

	public void setInterpolator(Interpolator interpolator) {
		this.mInterpolator = interpolator;
	}

	public void addUpdateListener(ValueAnimator.AnimatorUpdateListener listener) {
		this.mUpdateListener = listener;
	}

	public void addListener(Animator.AnimatorListener listener) {
		this.mListener = listener;
	}

	public void onAnimationUpdate(ValueAnimator animation) {
		this.currentValue = ((Float) animation.getAnimatedValue()).floatValue();

		float value = this.currentValue;

		if (this.mInterpolator != null) {
			value = this.mInterpolator.getInterpolation(this.currentValue);
		}

		this.mCurTransInfo = this.mPosInfo.evalute(this.currentValue);

		if (this.mUpdateListener != null)
			this.mUpdateListener.onAnimationUpdate(animation);
	}

	public float getCurrentFraction() {
		return this.currentValue;
	}

	public TransInfo getCurTrans() {
		return this.mCurTransInfo;
	}

	public TransInfo getStartTrans() {
		return this.mPosInfo.getStartTrans();
	}

	public void setDuration(int duration) {
		this.mValueAnimator.setDuration(duration);
	}

	public void start() {
		if (this.mPosInfo != null)
			this.mValueAnimator.start();
	}

	public void setPosInfo(PosInfo posInfo) {
		this.mPosInfo = posInfo;
	}

	public void onAnimationStart(Animator animation) {
		if (this.mListener != null)
			this.mListener.onAnimationStart(animation);
	}

	public void onAnimationEnd(Animator animation) {
		if (this.mListener != null)
			this.mListener.onAnimationEnd(animation);
	}

	public void onAnimationCancel(Animator animation) {
		if (this.mListener != null)
			this.mListener.onAnimationCancel(animation);
	}

	public void onAnimationRepeat(Animator animation) {
		if (this.mListener != null)
			this.mListener.onAnimationRepeat(animation);
	}
}
