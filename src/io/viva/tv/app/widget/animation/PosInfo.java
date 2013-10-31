package io.viva.tv.app.widget.animation;

import java.util.ArrayList;

public class PosInfo {
	public static final int MOVE_INVALID = -1;
	public static final int MOVE_LEFT = 0;
	public static final int MOVE_RIGHT = 1;
	ArrayList<TransInfoKey> mList;

	public PosInfo() {
		this.mList = new ArrayList();
	}

	public PosInfo(float X, float Y, float ScaleX, float ScaleY, float Alpha, float key) {
		this.mList = new ArrayList();
		this.mList.add(new TransInfoKey(X, Y, ScaleX, Y, Alpha, 0.0F));
	}

	public void addKeyFrame(TransInfoKey keyFrame) {
		if (this.mList == null) {
			this.mList = new ArrayList();
		}

		this.mList.add(keyFrame);
	}

	public PosInfo(float startX, float endX, float startY, float endY, float startScaleX, float endScaleX, float startScaleY, float endScaleY, float startAlpha, float endAlpha) {
		this(
				new TransInfoKey[] { new TransInfoKey(startX, startY, startScaleX, startScaleY, startAlpha, 0.0F),
						new TransInfoKey(endX, endY, endScaleX, endScaleY, endAlpha, 1.0F) });
	}

	public PosInfo(TransInfoKey[] key) {
		this.mList = new ArrayList();
		int len = key.length;

		TransInfoKey[] keyArr = (TransInfoKey[]) key.clone();
		for (int i = 0; i < len; i++)
			this.mList.add(keyArr[i]);
	}

	public TransInfo evalute(float fraction) {
		if (fraction <= 0.0F) {
			TransInfoKey a = (TransInfoKey) this.mList.get(0);
			if (a != null) {
				return a.mTransInfo;
			}

			return null;
		}
		if (fraction >= 1.0F) {
			int count = this.mList.size();
			if (count >= 1) {
				TransInfoKey a = (TransInfoKey) this.mList.get(count - 1);
				if (a != null) {
					return a.mTransInfo;
				}
				return null;
			}
		} else {
			int count = this.mList.size();

			if (count == 2) {
				return evalute(fraction, (TransInfoKey) this.mList.get(0), (TransInfoKey) this.mList.get(1));
			}

			float value = fraction;
			TransInfoKey startTransKey = (TransInfoKey) this.mList.get(0);
			float startKey = startTransKey.mKey;

			for (int i = 1; i < count; i++) {
				TransInfoKey endTransKey = (TransInfoKey) this.mList.get(i);
				float endKey = endTransKey.mKey;

				if (value < endKey) {
					return evalute((value - startKey) / (endKey - startKey), startTransKey, endTransKey);
				}

				startTransKey = endTransKey;
				startKey = endKey;
			}

		}

		return null;
	}

	private TransInfo evalute(float fraction, TransInfoKey startKey, TransInfoKey endKey) {
		TransInfo start = startKey.mTransInfo;
		TransInfo end = endKey.mTransInfo;

		float curX = start.x + fraction * (end.x - start.x);
		float curY = start.y + fraction * (end.y - start.y);

		float curScaleX = start.scaleX + fraction * (end.scaleX - start.scaleX);
		float curScaleY = start.scaleY + fraction * (end.scaleY - start.scaleY);

		float curAlpha = start.alpha + fraction * (end.alpha - start.alpha);

		return new TransInfo(curX, curY, curScaleX, curScaleY, curAlpha);
	}

	public TransInfo getStartTrans() {
		if (this.mList != null) {
			TransInfoKey keyFrameStart = (TransInfoKey) this.mList.get(0);
			if (keyFrameStart != null) {
				return keyFrameStart.mTransInfo;
			}
		}
		return null;
	}

	public TransInfo getEndTrans() {
		if (this.mList != null) {
			int count = this.mList.size();
			TransInfoKey keyFrameEnd = (TransInfoKey) this.mList.get(count - 1);
			if (keyFrameEnd != null) {
				return keyFrameEnd.mTransInfo;
			}
		}
		return null;
	}
}
