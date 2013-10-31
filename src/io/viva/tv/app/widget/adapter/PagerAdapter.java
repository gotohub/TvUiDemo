package io.viva.tv.app.widget.adapter;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

public abstract class PagerAdapter {
	private DataSetObservable mObservable = new DataSetObservable();
	public static final int POSITION_UNCHANGED = -1;
	public static final int POSITION_NONE = -2;

	public abstract int getCount();

	public void startUpdate(ViewGroup paramViewGroup) {
		startUpdate((View) paramViewGroup);
	}

	public Object instantiateItem(ViewGroup paramViewGroup, int paramInt) {
		return instantiateItem((View) paramViewGroup, paramInt);
	}

	public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject) {
		destroyItem((View) paramViewGroup, paramInt, paramObject);
	}

	public void setPrimaryItem(ViewGroup paramViewGroup, int paramInt, Object paramObject) {
		setPrimaryItem((View) paramViewGroup, paramInt, paramObject);
	}

	public void finishUpdate(ViewGroup paramViewGroup) {
		finishUpdate((View) paramViewGroup);
	}

	public void startUpdate(View paramView) {
	}

	public Object instantiateItem(View paramView, int paramInt) {
		throw new UnsupportedOperationException("Required method instantiateItem was not overridden");
	}

	public void destroyItem(View paramView, int paramInt, Object paramObject) {
		throw new UnsupportedOperationException("Required method destroyItem was not overridden");
	}

	public void setPrimaryItem(View paramView, int paramInt, Object paramObject) {
	}

	public void finishUpdate(View paramView) {
	}

	public abstract boolean isViewFromObject(View paramView, Object paramObject);

	public Parcelable saveState() {
		return null;
	}

	public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader) {
	}

	public int getItemPosition(Object paramObject) {
		return POSITION_UNCHANGED;
	}

	public void notifyDataSetChanged() {
		this.mObservable.notifyChanged();
	}

	public void registerDataSetObserver(DataSetObserver paramDataSetObserver) {
		this.mObservable.registerObserver(paramDataSetObserver);
	}

	public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver) {
		this.mObservable.unregisterObserver(paramDataSetObserver);
	}

	public CharSequence getPageTitle(int paramInt) {
		return null;
	}

	public float getPageWidth(int paramInt) {
		return 1.0F;
	}
}