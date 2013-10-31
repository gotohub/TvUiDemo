package io.viva.tv.app.widget.animation;

public class TransInfo {
	public float x;
	public float y;
	public float scaleX;
	public float scaleY;
	public float alpha;

	public TransInfo(float X, float Y, float ScaleX, float ScaleY, float Alpha) {
		this.x = X;
		this.y = Y;
		this.scaleX = ScaleX;
		this.scaleY = ScaleY;
		this.alpha = Alpha;
	}

	public TransInfo() {
		this(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	public void clone(TransInfo transInfo) {
		this.x = transInfo.x;
		this.y = transInfo.y;

		this.scaleX = transInfo.scaleX;
		this.scaleY = transInfo.scaleY;

		this.alpha = transInfo.alpha;
	}
}
