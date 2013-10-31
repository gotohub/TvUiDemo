package io.viva.tv.app.widget.animation;

public class TransInfoKey {
	public TransInfo mTransInfo;
	public float mKey;

	public TransInfoKey(float X, float Y, float ScaleX, float ScaleY, float Alpha, float key) {
		this.mKey = key;
		this.mTransInfo = new TransInfo(X, Y, ScaleX, ScaleY, Alpha);
	}

	public TransInfoKey clone(TransInfoKey t) {
		TransInfoKey key = new TransInfoKey(t.mTransInfo.x, t.mTransInfo.y, t.mTransInfo.scaleX, t.mTransInfo.scaleY, t.mTransInfo.alpha, t.mKey);

		return key;
	}
}
