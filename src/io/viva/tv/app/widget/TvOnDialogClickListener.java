package io.viva.tv.app.widget;

import android.content.DialogInterface;

public abstract class TvOnDialogClickListener implements DialogInterface.OnClickListener {
	boolean isPalyBtnClick = false;

	public void onClick(DialogInterface dialog, int which) {
		synchronized (this) {
			if (this.isPalyBtnClick) {
				return;
			}
			this.isPalyBtnClick = true;
		}

		onClicked(dialog, which);

		synchronized (this) {
			this.isPalyBtnClick = false;
		}
	}

	public abstract void onClicked(DialogInterface paramDialogInterface, int paramInt);
}