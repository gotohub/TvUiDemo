package io.viva.tv.app.widget;

import android.view.View;
import android.widget.AdapterView;

public abstract class TvOnItemClickListener implements AdapterView.OnItemClickListener {
	boolean isPalyBtnClick = false;

	public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
		synchronized (this) {
			if (this.isPalyBtnClick)
				return;
			this.isPalyBtnClick = true;
		}
		onItemClicked(paramAdapterView, paramView, paramInt, paramLong);
		synchronized (this) {
			this.isPalyBtnClick = false;
		}
	}

	public abstract void onItemClicked(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong);
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.app.widget.TvOnItemClickListener JD-Core Version: 0.6.2
 */