package io.viva.tv.app.widget;

import android.view.View;
import android.widget.AdapterView;

public abstract class TvOnItemClickListener implements AdapterView.OnItemClickListener {
	boolean isPalyBtnClick = false;

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		synchronized (this) {
			if (this.isPalyBtnClick) {
				return;
			}
			this.isPalyBtnClick = true;
		}

		onItemClicked(parent, view, position, id);

		synchronized (this) {
			this.isPalyBtnClick = false;
		}
	}

	public abstract void onItemClicked(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong);
}