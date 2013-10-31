package io.viva.tv.lib;

public class TransInfo {
	public float x;
	public float y;
	public float scaleX;
	public float scaleY;
	public float alpha;

	public TransInfo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5) {
		this.x = paramFloat1;
		this.y = paramFloat2;
		this.scaleX = paramFloat3;
		this.scaleY = paramFloat4;
		this.alpha = paramFloat5;
	}

	public TransInfo() {
		this(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	public void clone(TransInfo paramTransInfo) {
		this.x = paramTransInfo.x;
		this.y = paramTransInfo.y;
		this.scaleX = paramTransInfo.scaleX;
		this.scaleY = paramTransInfo.scaleY;
		this.alpha = paramTransInfo.alpha;
	}
}