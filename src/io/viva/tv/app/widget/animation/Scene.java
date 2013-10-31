package io.viva.tv.app.widget.animation;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.Interpolator;

public class Scene implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
	ArrayList<Actor> mList = new ArrayList<Actor>();
	ValueAnimator mValueAnimator;
	static String TAG = "MultiAnimator";
	Interpolator mInterpolator;
	SceneUpdateListener mUpdateListener;
	SceneStartListener mStartListener;
	SceneEndListener mEndListener;
	SceneCancelListener mCancelListener;
	boolean mIsRunning;
	float mCurrentValue;

	public Scene() {
		initial();
	}

	public void initial() {
		this.mCurrentValue = 0.0F;
		this.mValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
		this.mValueAnimator.addUpdateListener(this);
		this.mValueAnimator.addListener(this);
		this.mList.clear();
		this.mIsRunning = false;
	}

	public void setInterpolator(Interpolator interpolator) {
		this.mInterpolator = interpolator;
	}

	public void addUpdateListener(SceneUpdateListener listener) {
		this.mUpdateListener = listener;
	}

	public void addStartListener(SceneStartListener listener) {
		this.mStartListener = listener;
	}

	public void addEndListener(SceneEndListener listener) {
		this.mEndListener = listener;
	}

	public void addCancelListener(SceneCancelListener listener) {
		this.mCancelListener = listener;
	}

	public void onAnimationUpdate(ValueAnimator animation) {
		this.mCurrentValue = ((Float) animation.getAnimatedValue()).floatValue();
		float value = this.mCurrentValue;
		if (this.mInterpolator != null) {
			value = this.mInterpolator.getInterpolation(this.mCurrentValue);
		}

		if (this.mUpdateListener != null) {
			this.mUpdateListener.OnSceneUpdate(animation);
		}

		update(value);
	}

	public long getCurrentPlayTime() {
		return this.mValueAnimator.getCurrentPlayTime();
	}

	public float getCurrentValue() {
		return this.mCurrentValue;
	}

	public void setDuration(int duration) {
		this.mValueAnimator.setDuration(duration);
	}

	public void start() {
		this.mValueAnimator.start();
	}

	public void cancel() {
		this.mValueAnimator.cancel();
	}

	public void onAnimationStart(Animator animation) {
		this.mIsRunning = true;

		if (this.mStartListener != null)
			this.mStartListener.OnSceneStart(animation);
	}

	public void onAnimationEnd(Animator animation) {
		if (this.mEndListener != null) {
			this.mEndListener.OnSceneEnd(animation);
		}

		this.mIsRunning = false;

		this.mCurrentValue = 1.0F;

		update(this.mCurrentValue);
	}

	public void onAnimationCancel(Animator animation) {
		if (this.mCancelListener != null)
			this.mCancelListener.onSceneCancel(animation);
	}

	public void onAnimationRepeat(Animator animation) {
	}

	public boolean isRunning() {
		return this.mIsRunning;
	}

	public void addActor(Actor actor) {
		this.mList.add(actor);
	}

	void update(float fraction) {
		for (int i = 0; i < this.mList.size(); i++) {
			Actor actor = (Actor) this.mList.get(i);
			actor.update(fraction);
		}
	}

	public ArrayList<Actor> getActorList() {
		return this.mList;
	}

	public static abstract interface SceneCancelListener {
		public abstract void onSceneCancel(Animator paramAnimator);
	}

	public static abstract interface SceneEndListener {
		public abstract void OnSceneEnd(Animator paramAnimator);
	}

	public static abstract interface SceneStartListener {
		public abstract void OnSceneStart(Animator paramAnimator);
	}

	public static abstract interface SceneUpdateListener {
		public abstract void OnSceneUpdate(ValueAnimator paramValueAnimator);
	}
}
