package io.viva.tv.app.widget;

import android.content.DialogInterface;

public abstract class TvOnDialogClickListener implements DialogInterface.OnClickListener {
	boolean isPalyBtnClick = false;

	public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		synchronized (this) {
			if (this.isPalyBtnClick)
				return;
			this.isPalyBtnClick = true;
		}
		onClicked(paramDialogInterface, paramInt);
		synchronized (this) {
			this.isPalyBtnClick = false;
		}
	}

	public abstract void onClicked(DialogInterface paramDialogInterface, int paramInt);
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.app.widget.TvOnDialogClickListener JD-Core Version: 0.6.2
 */