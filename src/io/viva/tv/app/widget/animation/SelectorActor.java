package io.viva.tv.app.widget.animation;

public class SelectorActor implements Actor {
	PosInfo mPosInfo;
	TransInfo mSelectorTrans;

	public SelectorActor(TransInfo selectorTrans, PosInfo pos) {
		this.mSelectorTrans = selectorTrans;
		this.mPosInfo = pos;
	}

	public void update(float fraction) {
		TransInfo transInfo = this.mPosInfo.evalute(fraction);

		this.mSelectorTrans.clone(transInfo);
	}
}
