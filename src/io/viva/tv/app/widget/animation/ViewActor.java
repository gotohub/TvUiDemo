package io.viva.tv.app.widget.animation;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class ViewActor implements Actor {
	View mBindView;
	PosInfo mPosInfo;

	public ViewActor(View v, PosInfo pos) {
		this.mBindView = v;
		this.mPosInfo = pos;
	}

	public View getView() {
		return this.mBindView;
	}

	public void update(float fraction) {
		TransInfo transInfo = this.mPosInfo.evalute(fraction);

		if (transInfo != null) {
			this.mBindView.setScaleX(transInfo.scaleX);

			this.mBindView.setScaleY(transInfo.scaleY);

			this.mBindView.setTranslationX(transInfo.x);

			this.mBindView.setTranslationY(transInfo.y);
		} else {
			Log.v("PokerGroupView", "mBindView = " + this.mBindView);
		}
	}

	private void getRect(Rect r, TransInfo transInfo) {
		int xCenter = (r.left + r.right) / 2;
		int yCenter = (r.top + r.bottom) / 2;

		int width = r.right - r.left;
		int height = r.bottom - r.top;

		float xNewCenter = xCenter + transInfo.x;
		float yNewCenter = yCenter + transInfo.y;

		float newWidth = width * transInfo.scaleX;
		float newHeight = height * transInfo.scaleY;

		r.left = ((int) (xNewCenter - newWidth / 2.0F));
		r.right = ((int) (xNewCenter + newWidth / 2.0F));

		r.top = ((int) (yNewCenter - newHeight / 2.0F));
		r.bottom = ((int) (yNewCenter + newHeight / 2.0F));
	}
}
