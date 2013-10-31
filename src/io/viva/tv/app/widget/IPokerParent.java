package io.viva.tv.app.widget;

import io.viva.tv.app.widget.adapter.PokerItemAdapter;
import android.widget.SpinnerAdapter;

public abstract interface IPokerParent {
	public abstract void addPokerFlow(SpinnerAdapter paramSpinnerAdapter);

	public abstract void addPokerItem(PokerItemAdapter paramPokerItemAdapter);
}
