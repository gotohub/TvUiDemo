package io.viva.tv.app.widget;

import io.viva.tv.ui.demo.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageIndicatorsView extends LinearLayout {
	private Drawable mItemDrawableBase;
	private boolean mMultiSelectable;
	private int mSelectIndex = -1;

	private List<ImageView> mItems = new ArrayList<ImageView>();

	public PageIndicatorsView(Context context) {
		this(context, null);
	}

	public PageIndicatorsView(Context context, AttributeSet attrs) {
		this(new ContextThemeWrapper(context, 2114256926), attrs, 2114256926);
	}

	public PageIndicatorsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator, defStyle, 0);

		int selectIndex = a.getInteger(R.styleable.PageIndicator_selectIndex, 0);
		boolean multiSelectable = a.getBoolean(R.styleable.PageIndicator_multiSelectable, false);

		int itemCount = a.getInteger(R.styleable.PageIndicator_itemCount, 0);
		Drawable itemDrawable = a.getDrawable(R.styleable.PageIndicator_itemDrawable);
		a.recycle();

		setItemDrawable(itemDrawable);
		setMultiSelectable(multiSelectable);
		setItemCount(itemCount);
		setSelectIndex(selectIndex);
	}

	public void setItemDrawable(int resId) {
		Drawable drawable = getResources().getDrawable(resId);
		setItemDrawable(drawable);
	}

	public void setItemDrawable(Drawable drawable) {
		this.mItemDrawableBase = drawable;

		for (ImageView item : this.mItems)
			item.setImageDrawable(createItemDrawable(drawable));
	}

	public boolean isMultiSelectable() {
		return this.mMultiSelectable;
	}

	public void setMultiSelectable(boolean multiSelectable) {
		if (this.mMultiSelectable == multiSelectable) {
			return;
		}

		for (ImageView item : this.mItems) {
			item.setSelected(false);
		}
		this.mSelectIndex = -1;

		this.mMultiSelectable = multiSelectable;
	}

	public int getTotalPage() {
		return this.mItems.size();
	}

	public void setItemCount(int count) throws IllegalArgumentException {
		if (count < 0) {
			throw new IllegalArgumentException("count cannot be smaller than zero");
		}

		int oldCount = this.mItems.size();
		if (count > oldCount)
			for (int i = oldCount; i < count; i++) {
				ImageView item = createItem();

				this.mItems.add(item);
				addView(item);
			}
		else if (count < oldCount)
			for (int i = oldCount - 1; i >= count; i--) {
				ImageView view = (ImageView) this.mItems.get(i);
				this.mItems.remove(i);
				removeView(view);
			}
	}

	public int getSelectIndex() {
		return this.mSelectIndex;
	}

	public void setSelectIndex(int index) throws IllegalStateException {
		if (this.mMultiSelectable) {
			throw new IllegalStateException("cannot get select index under multi-select mode");
		}

		int count = this.mItems.size();
		if ((index < 0) || (index >= count)) {
			index = -1;
		}

		if (this.mSelectIndex >= 0) {
			((ImageView) this.mItems.get(this.mSelectIndex)).setSelected(false);
		}

		if (index >= 0) {
			((ImageView) this.mItems.get(index)).setSelected(true);
		}

		this.mSelectIndex = index;
	}

	public boolean isSelected(int index) {
		return ((ImageView) this.mItems.get(index)).isSelected();
	}

	public void setSelected(int index, boolean selected) throws IllegalStateException {
		if (!this.mMultiSelectable) {
			throw new IllegalStateException("cannot invoke setSelect(int, boolean) under multi-select mode, use setSelectIndex(int) instead");
		}

		((ImageView) this.mItems.get(index)).setSelected(selected);
	}

	public void clear() {
		for (int i = 0; i < this.mItems.size(); i++) {
			((ImageView) this.mItems.get(i)).setSelected(false);
		}
	}

	private ImageView createItem() {
		ImageView item = new ImageView(super.getContext());
		item.setImageDrawable(createItemDrawable(this.mItemDrawableBase));
		return item;
	}

	private Drawable createItemDrawable(Drawable base) {
		if (base == null) {
			return null;
		}

		StateListDrawable drawable = new StateListDrawable();

		base.setState(View.SELECTED_STATE_SET);
		drawable.addState(View.SELECTED_STATE_SET, base.getCurrent());

		base.setState(View.EMPTY_STATE_SET);
		drawable.addState(View.EMPTY_STATE_SET, base.getCurrent());

		return drawable;
	}
}
