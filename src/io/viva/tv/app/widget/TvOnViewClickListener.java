package io.viva.tv.app.widget;

import android.view.View;

public abstract class TvOnViewClickListener implements View.OnClickListener {
	boolean isPalyBtnClick = false;

	public void onClick(View v) {
		synchronized (this) {
			if (this.isPalyBtnClick) {
				return;
			}
			this.isPalyBtnClick = true;
		}

		onClicked(v);

		synchronized (this) {
			this.isPalyBtnClick = false;
		}
	}

	public abstract void onClicked(View paramView);
}