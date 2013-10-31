package io.viva.tv.lib;

public class TransInfoKey {
	public TransInfo mTransInfo;
	public float mKey;

	public TransInfoKey(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) {
		this.mKey = paramFloat6;
		this.mTransInfo = new TransInfo(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
	}

	public TransInfoKey clone(TransInfoKey paramTransInfoKey) {
		TransInfoKey localTransInfoKey = new TransInfoKey(paramTransInfoKey.mTransInfo.x, paramTransInfoKey.mTransInfo.y, paramTransInfoKey.mTransInfo.scaleX,
				paramTransInfoKey.mTransInfo.scaleY, paramTransInfoKey.mTransInfo.alpha, paramTransInfoKey.mKey);
		return localTransInfoKey;
	}
}