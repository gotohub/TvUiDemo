package io.viva.tv.app.widget.utils;

import io.viva.tv.app.widget.FocusedGridView;
import io.viva.tv.lib.LOG;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.OverScroller;

public class FlingManager implements Runnable {
	private static final String TAG = "FlingManager";
	private static final boolean DEBUG = true;
	private final OverScroller mScroller;
	private final FocusedGridView mGridView;
	private int mLastFlingY;
	int mActualY;
	FlingCallback mFlingCallback;
	private static final int FLYWHEEL_TIMEOUT = 40;

	public int getActualY() {
		synchronized (this) {
			return this.mActualY;
		}
	}

	public FlingManager(FocusedGridView paramFocusedGridView, FlingCallback paramFlingCallback) {
		this.mGridView = paramFocusedGridView;
		this.mFlingCallback = paramFlingCallback;
		this.mScroller = new OverScroller(this.mGridView.getContext());
	}

	public void startScroll(int paramInt1, int paramInt2) {
		LOG.i(TAG, true, "FlingRunnable startScroll distance = " + paramInt1 + ", duration");
		int i = paramInt1 < 0 ? 2147483647 : 0;
		this.mLastFlingY = i;
		this.mActualY = i;
		this.mScroller.startScroll(0, i, 0, paramInt1, paramInt2);
		setTouchMode(4);
		this.mGridView.post(this);
	}

	public void endFling() {
		LOG.i(TAG, true, "FlingRunnable endFling");
		setTouchMode(-1);
		this.mGridView.removeCallbacks(this);
		focusedReportScrollStateChange(0);
		focusedClearScrollingCache();
		this.mScroller.abortAnimation();
		finishFlingStrictSpan();
	}

	public void run() {
		int i = getTouchMode();
		switch (i) {
		default:
			endFling();
			return;
		case 4:
		}
		if (getDataChanged())
			this.mFlingCallback.flingLayoutChildren();
		if ((this.mGridView.getAdapter().getCount() == 0) || (this.mGridView.getChildCount() == 0)) {
			endFling();
			return;
		}
		OverScroller localOverScroller = this.mScroller;
		boolean bool = localOverScroller.computeScrollOffset();
		int j = localOverScroller.getCurrY();
		synchronized (this) {
			this.mActualY = j;
		}
		int k = this.mLastFlingY - j;
		if (k > 0) {
			setMotionPosition(this.mGridView.getFirstVisiblePosition());
			View localView1 = this.mGridView.getChildAt(0);
			setMotionViewOriginalTop(localView1.getTop());
			k = Math.min(this.mGridView.getHeight() - this.mGridView.getPaddingBottom() - this.mGridView.getPaddingTop() - 1, k);
		} else {
			int m = this.mGridView.getChildCount() - 1;
			setMotionPosition(this.mGridView.getFirstVisiblePosition() + m);
			View localView3 = this.mGridView.getChildAt(m);
			setMotionViewOriginalTop(localView3.getTop());
			k = Math.max(-(this.mGridView.getHeight() - this.mGridView.getPaddingBottom() - this.mGridView.getPaddingTop() - 1), k);
		}
		View localView2 = this.mGridView.getChildAt(getMotionPosition() - this.mGridView.getFirstVisiblePosition());
		int n = 0;
		if (localView2 != null)
			n = localView2.getTop();
		int i1 = (trackMotionScroll(k, k)) && (k != 0) ? 1 : 0;
		if (i1 != 0) {
			Log.w(TAG, "FlingManager.run at end");
			endFling();
		} else if ((bool) && (i1 == 0)) {
			this.mGridView.invalidate();
			this.mLastFlingY = j;
			this.mGridView.post(this);
		} else {
			endFling();
		}
	}

	boolean trackMotionScroll(int paramInt1, int paramInt2) {
		LOG.i(TAG, true, "FlingRunnable trackMotionScroll");
		int i = this.mGridView.getChildCount();
		if (i == 0)
			return true;
		View localView1 = null;
		for (int j = 0; j < this.mGridView.getChildCount(); j++) {
			localView1 = this.mGridView.getChildAt(j);
			if ((localView1.getScaleX() == 1.0F) || (localView1.getScaleY() == 1.0F))
				break;
		}
		if (localView1 == null)
			return true;
		int j = localView1.getTop();
		int k = this.mGridView.getChildAt(i - 1).getBottom();
		Rect localRect = getListPadding();
		int m = 0;
		int n = 0;
		if ((getGroupFlags() & this.mFlingCallback.getClipToPaddingMask()) == this.mFlingCallback.getClipToPaddingMask()) {
			m = localRect.top;
			n = localRect.bottom;
		}
		int i1 = m - j;
		int i2 = this.mGridView.getHeight() - n;
		int i3 = k - i2;
		int i4 = this.mGridView.getHeight() - this.mGridView.getPaddingBottom() - this.mGridView.getPaddingTop();
		if (paramInt1 < 0)
			paramInt1 = Math.max(-(i4 - 1), paramInt1);
		else
			paramInt1 = Math.min(i4 - 1, paramInt1);
		if (paramInt2 < 0)
			paramInt2 = Math.max(-(i4 - 1), paramInt2);
		else
			paramInt2 = Math.min(i4 - 1, paramInt2);
		int i5 = this.mGridView.getFirstVisiblePosition();
		if (i5 == 0)
			setFirstPositionDistanceGuess(j - localRect.top);
		else
			setFirstPositionDistanceGuess(paramInt2);
		if (i5 + i == this.mGridView.getAdapter().getCount())
			setFirstPositionDistanceGuess(k + localRect.bottom);
		else
			setFirstPositionDistanceGuess(paramInt2);
		boolean bool1 = (i5 == 0) && (j >= localRect.top) && (paramInt2 >= 0);
		boolean bool2 = (i5 + i == this.mGridView.getAdapter().getCount()) && (k <= this.mGridView.getHeight() - localRect.bottom) && (paramInt2 <= 0);
		if ((bool1) || (bool2)) {
			Log.w(TAG, "trackMotionScroll cannotScrollDown = " + bool1 + ", cannotScrollUp = " + bool2);
			return paramInt2 != 0;
		}
		boolean bool3 = paramInt2 < 0;
		boolean bool4 = this.mGridView.isInTouchMode();
		if (bool4)
			focusedHideSelector();
		int i6 = this.mGridView.getAdapter().getCount() - 0;
		int i7 = 0;
		int i8 = 0;
		int i10;
		View localView2;
		int i11, i9;
		if (bool3) {
			i9 = -paramInt2;
			if ((getGroupFlags() & this.mFlingCallback.getClipToPaddingMask()) == this.mFlingCallback.getClipToPaddingMask())
				i9 += localRect.top;
			for (i10 = 0; i10 < i; i10++) {
				localView2 = this.mGridView.getChildAt(i10);
				if (localView2.getBottom() >= i9)
					break;
				i8++;
				i11 = i5 + i10;
				if ((i11 >= 0) && (i11 < i6))
					focusedAddScrapView(localView2, i11);
			}
		} else {
			i9 = this.mGridView.getHeight() - paramInt2;
			if ((getGroupFlags() & this.mFlingCallback.getClipToPaddingMask()) == this.mFlingCallback.getClipToPaddingMask())
				i9 -= localRect.bottom;
			for (i10 = i - 1; i10 >= 0; i10--) {
				localView2 = this.mGridView.getChildAt(i10);
				if (localView2.getTop() <= i9)
					break;
				i7 = i10;
				i8++;
				i11 = i5 + i10;
				if ((i11 >= 0) && (i11 < i6))
					focusedAddScrapView(localView2, i11);
			}
		}
		setMotionViewNewTop(getMotionViewOriginalTop() + paramInt1);
		setBlockLayoutRequests(true);
		if (i8 > 0)
			this.mFlingCallback.flingDetachViewsFromParent(i7, i8);
		focusedOffsetChildrenTopAndBottom(paramInt2);
		if (bool3)
			setFirstPosition(this.mGridView.getFirstVisiblePosition() + i8);
		this.mGridView.invalidate();
		i9 = Math.abs(paramInt2);
		if ((i1 < i9) || (i3 < i9))
			focusedFillGap(bool3);
		if ((!bool4) && (getSelectedPosition() != -1)) {
			i10 = getSelectedPosition() - this.mGridView.getFirstVisiblePosition();
			if ((i10 >= 0) && (i10 < this.mGridView.getChildCount()))
				focusedPositionSelector(getSelectedPosition(), this.mGridView.getChildAt(i10));
		} else if (getSelectorPosition() != -1) {
			i10 = getSelectorPosition() - this.mGridView.getFirstVisiblePosition();
			if ((i10 >= 0) && (i10 < this.mGridView.getChildCount()))
				focusedPositionSelector(-1, this.mGridView.getChildAt(i10));
		} else {
			setSelectorRectEmpty();
		}
		setBlockLayoutRequests(false);
		focusedInvokeOnItemScrollListener();
		this.mFlingCallback.flingAwakenScrollBars();
		return false;
	}

	Rect getListPadding() {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mListPadding");
			localField.setAccessible(true);
			return (Rect) localField.get(this.mGridView);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
		return null;
	}

	int getGroupFlags() {
		try {
			Class localClass = Class.forName("android.view.ViewGroup");
			Field localField = localClass.getDeclaredField("mGroupFlags");
			localField.setAccessible(true);
			return localField.getInt(this.mGridView);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
		return 0;
	}

	void focusedHideSelector() {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Method localMethod = localClass.getDeclaredMethod("hideSelector", new Class[0]);
			localMethod.setAccessible(true);
			localMethod.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void focusedAddScrapView(View paramView, int paramInt) {
		try {
			Class localClass1 = Class.forName("android.widget.AbsListView");
			Field localField = localClass1.getDeclaredField("mRecycler");
			localField.setAccessible(true);
			Object localObject = localField.get(this.mGridView);
			Class localClass2 = Class.forName("android.widget.AbsListView$RecycleBin");
			Method localMethod = localClass2.getDeclaredMethod("addScrapView", new Class[] { View.class, Integer.TYPE });
			localMethod.setAccessible(true);
			localMethod.invoke(localObject, new Object[] { paramView, Integer.valueOf(paramInt) });
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		}
	}

	void setFirstPositionDistanceGuess(int paramInt) {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mFirstPositionDistanceGuess");
			localField.setAccessible(true);
			localField.setInt(this.mGridView, paramInt);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	int getMotionViewOriginalTop() {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mMotionViewOriginalTop");
			localField.setAccessible(true);
			return localField.getInt(this.mGridView);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
		return 0;
	}

	void setMotionViewOriginalTop(int paramInt) {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mMotionViewOriginalTop");
			localField.setAccessible(true);
			localField.setInt(this.mGridView, paramInt);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void setMotionViewNewTop(int paramInt) {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mMotionViewNewTop");
			localField.setAccessible(true);
			localField.setInt(this.mGridView, paramInt);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void setBlockLayoutRequests(boolean paramBoolean) {
		try {
			Class localClass = Class.forName("android.widget.AdapterView");
			Field localField = localClass.getDeclaredField("mBlockLayoutRequests");
			localField.setAccessible(true);
			localField.setBoolean(this.mGridView, paramBoolean);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void focusedOffsetChildrenTopAndBottom(int paramInt) {
		try {
			Class localClass = Class.forName("android.view.ViewGroup");
			Method localMethod = localClass.getDeclaredMethod("offsetChildrenTopAndBottom", new Class[] { Integer.TYPE });
			localMethod.setAccessible(true);
			localMethod.invoke(this.mGridView, new Object[] { Integer.valueOf(paramInt) });
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void setFirstPosition(int paramInt) {
		try {
			Class localClass = Class.forName("android.widget.AdapterView");
			Field localField = localClass.getDeclaredField("mFirstPosition");
			localField.setAccessible(true);
			localField.setInt(this.mGridView, paramInt);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void focusedFillGap(boolean paramBoolean) {
		try {
			Class localClass = Class.forName("android.widget.GridView");
			Method localMethod = localClass.getDeclaredMethod("fillGap", new Class[] { Boolean.TYPE });
			localMethod.setAccessible(true);
			localMethod.invoke(this.mGridView, new Object[] { Boolean.valueOf(paramBoolean) });
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	int getSelectedPosition() {
		try {
			Class localClass = Class.forName("android.widget.AdapterView");
			Field localField = localClass.getDeclaredField("mSelectedPosition");
			localField.setAccessible(true);
			localField.getInt(this.mGridView);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
		return 0;
	}

	int getSelectorPosition() {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mSelectorPosition");
			localField.setAccessible(true);
			localField.getInt(this.mGridView);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
		return 0;
	}

	void focusedPositionSelector(int paramInt, View paramView) {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Method localMethod = localClass.getDeclaredMethod("positionSelector", new Class[] { Integer.TYPE, View.class });
			localMethod.setAccessible(true);
			localMethod.invoke(this.mGridView, new Object[] { Integer.valueOf(paramInt), paramView });
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void setSelectorRectEmpty() {
		try {
			Class localClass1 = Class.forName("android.widget.AbsListView");
			Field localField = localClass1.getDeclaredField("mSelectorRect");
			localField.setAccessible(true);
			Class localClass2 = Class.forName("android.graphics.Rect");
			Method localMethod = localClass2.getDeclaredMethod("setEmpty", new Class[0]);
			localMethod.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		}
	}

	void focusedInvokeOnItemScrollListener() {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Method localMethod = localClass.getDeclaredMethod("invokeOnItemScrollListener", new Class[0]);
			localMethod.setAccessible(true);
			localMethod.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void setTouchMode(int paramInt) {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mTouchMode");
			localField.setAccessible(true);
			Object localObject = localField.get(this.mGridView);
			localField.setInt(this.mGridView, paramInt);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	int getTouchMode() {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mTouchMode");
			localField.setAccessible(true);
			return localField.getInt(this.mGridView);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
		return 0;
	}

	boolean getDataChanged() {
		try {
			Class localClass = Class.forName("android.widget.AdapterView");
			Field localField = localClass.getDeclaredField("mDataChanged");
			localField.setAccessible(true);
			return localField.getBoolean(this.mGridView);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
		return false;
	}

	int getMotionPosition() {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mMotionPosition");
			localField.setAccessible(true);
			return localField.getInt(this.mGridView);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
		return 0;
	}

	void setMotionPosition(int paramInt) {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mMotionPosition");
			localField.setAccessible(true);
			localField.setInt(this.mGridView, paramInt);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	int getOverflingDistance() {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Field localField = localClass.getDeclaredField("mOverflingDistance");
			localField.setAccessible(true);
			return localField.getInt(this.mGridView);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
		return 0;
	}

	void edgeReached(int paramInt) {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Method localMethod = localClass.getDeclaredMethod("edgeReached", new Class[0]);
			localMethod.setAccessible(true);
			localMethod.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	public void focusedReportScrollStateChange(int paramInt) {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Method localMethod = localClass.getDeclaredMethod("reportScrollStateChange", new Class[] { Integer.TYPE });
			localMethod.setAccessible(true);
			localMethod.invoke(this.mGridView, new Object[] { Integer.valueOf(paramInt) });
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			int i = 2;
			localInvocationTargetException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void focusedClearScrollingCache() {
		try {
			Class localClass = Class.forName("android.widget.AbsListView");
			Method localMethod = localClass.getDeclaredMethod("clearScrollingCache", new Class[0]);
			localMethod.setAccessible(true);
			localMethod.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void finishFlingStrictSpan() {
		try {
			Class localClass1 = Class.forName("android.widget.AbsListView");
			Field localField = localClass1.getDeclaredField("mFlingStrictSpan");
			localField.setAccessible(true);
			Object localObject = localField.get(this.mGridView);
			if (null == localObject)
				return;
			Class localClass2 = Class.forName("android.os.StrictMode$Span");
			Method localMethod = localClass2.getDeclaredMethod("finish", new Class[0]);
			localMethod.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		} catch (NoSuchFieldException localNoSuchFieldException) {
			localNoSuchFieldException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	void focusedSetNextSelectedPositionInt(int paramInt) {
		try {
			Class localClass = Class.forName("android.widget.AdapterView");
			Method localMethod = localClass.getDeclaredMethod("setNextSelectedPositionInt", new Class[] { Integer.TYPE });
			localMethod.setAccessible(true);
			localMethod.invoke(this.mGridView, new Object[] { Integer.valueOf(paramInt) });
		} catch (SecurityException localSecurityException) {
			localSecurityException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		} catch (NoSuchMethodException localNoSuchMethodException) {
			localNoSuchMethodException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
		} catch (ClassNotFoundException localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
	}

	public static interface FlingCallback {
		public abstract void flingLayoutChildren();

		public abstract int getScrollY();

		public abstract int getClipToPaddingMask();

		public abstract boolean flingOverScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8,
				boolean paramBoolean);

		public abstract void flingDetachViewsFromParent(int paramInt1, int paramInt2);

		public abstract boolean flingAwakenScrollBars();
	}
}