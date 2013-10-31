package io.viva.tv.app.widget.animation;

import android.animation.Animator;
import android.graphics.Rect;
import android.util.Log;

public class SeletorAnimator {
	private Scene mViewScene = new Scene();

	private TransInfo mSelectorTrans = new TransInfo();

	int mduration = 200;
	static final int DEFAULT_DURATION = 200;
	private int mSelectStartWidth;
	private int mSelectStartHeight;
	static String TAG = "SeletorAnimator";

	private void addActorToScene(Rect CurrentSelectRect, Rect NextSelectRect) {
		Rect rectStart = CurrentSelectRect;
		Rect rectEnd = NextSelectRect;

		int xStart = rectStart.left + rectStart.right >> 1;
		int yStart = rectStart.top + rectStart.bottom >> 1;

		int xEnd = rectEnd.left + rectEnd.right >> 1;
		int yEnd = rectEnd.top + rectEnd.bottom >> 1;

		Rect selectorCurRect = new Rect(rectStart);
		this.mSelectStartWidth = (selectorCurRect.right - selectorCurRect.left);
		this.mSelectStartHeight = (selectorCurRect.bottom - selectorCurRect.top);

		int endWidth = NextSelectRect.right - NextSelectRect.left;
		int endHeight = NextSelectRect.bottom - NextSelectRect.top;

		TransInfoKey transKeyStart = new TransInfoKey(xStart, yStart, 1.0F, 1.0F, 1.0F, 0.0F);

		float endScaleX = endWidth * 1.0F / this.mSelectStartWidth;
		float endScaleY = endHeight * 1.0F / this.mSelectStartHeight;

		TransInfoKey transKeyEnd = new TransInfoKey(xEnd, yEnd, endScaleX, endScaleY, 1.0F, 1.0F);

		PosInfo pos = new PosInfo(new TransInfoKey[] { transKeyStart, transKeyEnd });

		this.mViewScene.addActor(new SelectorActor(this.mSelectorTrans, pos));
	}

	private void playScene(Scene.SceneUpdateListener listener) {
		this.mViewScene.setDuration(this.mduration);

		this.mViewScene.addUpdateListener(listener);

		this.mViewScene.addEndListener(new Scene.SceneEndListener() {
			public void OnSceneEnd(Animator animation) {
				Log.d("TEST", "-----------onAnimationEnd---------");
			}
		});
		this.mViewScene.start();
	}

	public void getCurSelectRect(Rect r) {
		r.left = ((int) this.mSelectorTrans.x - ((int) (this.mSelectStartWidth * this.mSelectorTrans.scaleX) >> 1));
		r.right = ((int) this.mSelectorTrans.x + ((int) (this.mSelectStartWidth * this.mSelectorTrans.scaleX) >> 1));
		r.top = ((int) this.mSelectorTrans.y - ((int) (this.mSelectStartHeight * this.mSelectorTrans.scaleY) >> 1));
		r.bottom = ((int) this.mSelectorTrans.y + ((int) (this.mSelectStartHeight * this.mSelectorTrans.scaleY) >> 1));

		Log.v(TAG, " l = " + r.left + " r = " + r.right + " t = " + r.top + " b = " + r.bottom);
	}

	public void startSelectorShiftAni(Rect CurrentSelectRect, Rect NextSelectRect, Scene.SceneUpdateListener listener) {
		if ((this.mViewScene == null) || (!this.mViewScene.isRunning())) {
			this.mViewScene.initial();

			addActorToScene(CurrentSelectRect, NextSelectRect);

			playScene(listener);
		}
	}

	public boolean isRunning() {
		return this.mViewScene.isRunning();
	}
}
