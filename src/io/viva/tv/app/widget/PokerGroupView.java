package io.viva.tv.app.widget;

import io.viva.tv.app.widget.adapter.PokerItemAdapter;
import io.viva.tv.app.widget.adapter.ReflectedBitmapAdapter;
import io.viva.tv.app.widget.animation.HorizontalPosInfo;
import io.viva.tv.app.widget.animation.PosInfo;
import io.viva.tv.app.widget.animation.Scene;
import io.viva.tv.app.widget.animation.SelectorActor;
import io.viva.tv.app.widget.animation.TransInfo;
import io.viva.tv.app.widget.animation.ViewActor;
import io.viva.tv.app.widget.utils.TextUtils;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;

public class PokerGroupView extends HorizontalListView implements IPokerParent, IPokerTextDisplay {
	private static final int MAX_720P_WIDTH = 720;
	static final int MAX_SELECTION_ITEM = 3;
	private int mMaxItemCount;
	private int[] needDetachPosArray = { -1, -1, -1, -1 };

	private TransInfo mSelectorTrans = new TransInfo();
	private static final boolean DEBUG_UI_LOG = false;
	private static final String TAG = "PokerGroupView";
	PokerListConnector mConnector;
	Context mContext;
	private boolean mGainFocus;
	private boolean mDisableTextDisplay;
	ArrayList<String> mPokerGroupTextDisplayList;
	SparseArray<ArrayList<String>> mPokerFlowTextDisplayList;
	OnLastItemMoveInPokerFlowListener mOnLastItemMoveListener;
	int textSpaceFromDown = 48;
	int textSpaceFromUp = -20;
	Paint mTextPaint;
	int[] mTextWidths;
	Rect mViewRealRect = new Rect();
	private static final int MAX_COUNT = 20;
	private boolean mIsDrawReflectBitmap = true;

	Paint mReflectingPaint = new Paint();
	Rect mReflectingSrcRect = new Rect();
	Rect mReflectingDstRect = new Rect();

	private boolean mIsDrawShadow = true;

	private float mShadowRatio = 0.45F;

	private Paint mShadowPaint = new Paint();
	private Bitmap mShadowNextBitmap;
	private Bitmap mShadowPreBitmap;
	private NinePatch mShadowNextNinePatch;
	private NinePatch mShadowPreNinePatch;
	private Rect mShadowDrawRect = new Rect();
	private Rect mShadowClipRect = new Rect();
	ReflectedBitmapAdapter mBitmapAdapter;
	ReflectedBitmapObserver mBitmapObserver;
	Drawable mSingleMaskShade;
	static final int NOTIFY_LAYOUT = 0;
	boolean needDelayLayout;
	AdapterDataSetObserver mDataSetObserver;
	Handler mDelayNotifyHandler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.arg1;
			switch (what) {
			case 0:
				PokerGroupView.this.requestLayout();
				PokerGroupView.this.needDelayLayout = false;
				break;
			}
		}
	};

	private float mDestX = 0.0F;
	private float mDestY = 0.0F;

	private float mSelectorOriginX = 0.0F;
	private float mSelectorOriginY = 0.0F;

	Scene mViewScene = new Scene();
	int mSceneOldSelectedPosition = HorizontalPosInfo.ANI_SELECT_NEGATIVE_POSITION;
	int mSceneNewSelectedPosition = HorizontalPosInfo.ANI_SELECT_NEGATIVE_POSITION;

	int mAniMoveDirection = -1;
	int mduration;
	static final int DEFAULT_DURATION = 150;
	boolean handleKeyDownByPokerFlow = false;
	ActionBar mActionBar;
	View mDownTouchView;
	int mDownTouchPosition = -1;

	SparseArray<Integer> mUnCoverChildDrawingOrderList = new SparseArray();
	private static final int UNCOVER_CHILD_COUNT = 4;

	public PokerGroupView(Context context) {
		this(context, null);
	}

	public PokerGroupView(Context context, AttributeSet attrs) {
		this(context, attrs, 16842864);
	}

	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		this.mGainFocus = gainFocus;

		if ((this.mOldSelectedPosition == 0) && (this.mSelectedPosition == 0) && (gainFocus)) {
			startChangeAnimation(-1, 0);
		} else if ((this.mOldSelectedPosition == 0) && (!gainFocus)) {
			startAnimationNotUseCheck(0, -1);
		}

		if (gainFocus) {
			View sel = getSelectedView();
			doZoomOut(sel);
		}
	}

	private boolean isAnimating() {
		return (this.mViewScene != null) && (this.mViewScene.isRunning());
	}

	protected void handlerFocusChanged(boolean gainFocus) {
		if (gainFocus)
			positionSelector(this.mSelectedPosition, getSelectedView());
		else {
			this.mSelectorRect.setEmpty();
		}

		checkSelectionChanged();
	}

	public PokerGroupView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setSpacing(0);
		this.mContext = context;
		this.mGravity = 16;
		WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		if (width > MAX_720P_WIDTH)
			this.mMaxItemCount = 6;
		else if (width <= MAX_720P_WIDTH) {
			this.mMaxItemCount = 5;
		}
		setChildrenDrawingOrderEnabled(true);
		setStaticTransformationsEnabled(true);
		// this.mSingleMaskShade = R.drawable.tui_bg_focus;
		this.mduration = 500;
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void getFocusedRect(Rect rect) {
		View view = getSelectedView();
		if ((view != null) && (view.getParent() == this)) {
			if ((view instanceof PokerFlow)) {
				((PokerFlow) view).getFocusedRect(rect);
				offsetDescendantRectToMyCoords(view, rect);
			} else {
				view.getFocusedRect(rect);
				offsetDescendantRectToMyCoords(view, rect);
			}
		} else {
			super.getFocusedRect(rect);
		}

		if ((rect != null) && (!rect.isEmpty()) && (view != null)) {
			getRectofSelector(rect, this.mSelectedPosition - this.mFirstPosition, view.getWidth());
		}
	}

	private void getRectofSelector(Rect rect, int index, int width) {
		TransInfo transInfo = HorizontalPosInfo.getSelectPos(this.mSelectorOriginX, this.mSelectorOriginY, this.mDestX, this.mDestY, index, width);
		float x = transInfo.x;
		float y = transInfo.y;
		int w = rect.right - rect.left;
		int h = rect.bottom - rect.top;

		int l = (int) (x - w / 2);
		int t = (int) (y - h / 2);
		int r = l + w;
		int b = t + h;
		rect.set(l, t, r, b);
	}

	private void getRectofViewInStableStatus(Rect rect, int indexofView, int width, int height) {
		int index = getRelSelectedPosition() - this.mFirstPosition;

		View v = getChildAt(index);

		int originX = getCenterOfViewX(v);
		int originY = getCenterOfViewY(v);

		TransInfo transInfo = HorizontalPosInfo.getInitialPosInfo(originX, originY, this.mDestX, this.mDestY, index, indexofView, width);

		rect.set((int) transInfo.x - width / 2, (int) transInfo.y - height / 2, (int) transInfo.x + width / 2, (int) transInfo.y + height / 2);
	}

	private Rect getRectOfView(int indexofView) {
		View v = getChildAt(indexofView);
		return getRectOfView(v);
	}

	private void getRectOfPokerGroupItem(View v, Rect rect) {
		if (v == null) {
			return;
		}

		int l = v.getLeft();
		int r = v.getRight();

		int t = v.getTop();
		int b = v.getBottom();

		int xCenter = (l + r) / 2;
		int yCenter = (t + b) / 2;

		int width = r - l;
		int height = b - t;

		float xNewCenter = xCenter + v.getTranslationX();
		float yNewCenter = yCenter + v.getTranslationY();

		float newWidth = width * v.getScaleX();
		float newHeight = height * v.getScaleX();

		rect.left = ((int) (xNewCenter - newWidth / 2.0F));
		rect.right = ((int) (xNewCenter + newWidth / 2.0F));

		rect.top = ((int) (yNewCenter - newHeight / 2.0F));
		rect.bottom = ((int) (yNewCenter + newHeight / 2.0F));
	}

	private void getRectOfPokerFlow(View v, Rect rect) {
		if (v == null) {
			return;
		}

		int l = v.getLeft();
		int r = v.getRight();

		int t = v.getTop();
		int b = v.getBottom();

		int xCenter = (l + r) / 2;
		int yCenter = (t + b) / 2;

		int width = r - l;
		int height = b - t;

		View selectedView = ((PokerFlow) v).getSelectedView();
		if (selectedView != null) {
			height = selectedView.getBottom() - selectedView.getTop();
		}

		float xNewCenter = xCenter + v.getTranslationX();
		float yNewCenter = yCenter + v.getTranslationY();

		float newWidth = width * v.getScaleX();
		float newHeight = height * v.getScaleX();

		rect.left = ((int) (xNewCenter - newWidth / 2.0F));
		rect.right = ((int) (xNewCenter + newWidth / 2.0F));

		rect.top = ((int) (yNewCenter - newHeight / 2.0F));
		rect.bottom = ((int) (yNewCenter + newHeight / 2.0F));
	}

	private void getRectOfView(View v, Rect rect, boolean isVerticalPokerFlow) {
		if (isVerticalPokerFlow)
			getRectOfPokerFlow(v, rect);
		else
			getRectOfPokerGroupItem(v, rect);
	}

	private void getRectOfView(Rect rect, View v) {
		if ((v instanceof PokerFlow))
			getRectOfPokerFlow(v, rect);
		else
			getRectOfPokerGroupItem(v, rect);
	}

	private Rect getRectOfView(View v) {
		if (v != null) {
			Rect rect = new Rect();

			if ((v instanceof PokerFlow))
				getRectOfPokerFlow(v, rect);
			else {
				getRectOfPokerGroupItem(v, rect);
			}

			return rect;
		}

		return null;
	}

	public void addPokerFlow(SpinnerAdapter adapter) {
		if (this.mConnector == null) {
			this.mConnector = new PokerListConnector(this.mContext, this);
		}
		this.mConnector.addPokerFlow(adapter);
	}

	public void removeItem(int position) {
		if ((position >= 0) && (this.mItemCount > 0)) {
			position = position >= this.mItemCount ? this.mItemCount - 1 : position;
			this.mConnector.removeItem(position);
		}
	}

	public void cleartItems() {
		if (this.mConnector != null) {
			this.mConnector.clear();
			this.mConnector = null;
		}
		setPokerGroupAdapter(null);
	}

	public void addPokerItem(PokerItemAdapter item) {
		if (this.mConnector == null) {
			this.mConnector = new PokerListConnector(this.mContext, this);
		}
		this.mConnector.addPokerItemAdapter(item);
	}

	public int getPokerFlowItemCount(int position) {
		if (!this.mConnector.isPokerFlow(position)) {
			return -1;
		}
		PokerFlow pokerFlow = this.mConnector.getPokerFlow(position);
		return pokerFlow.getCount();
	}

	public void setAdapter(SpinnerAdapter adapter) {
		throw new RuntimeException("For PokerGroupView, use addPokerFlow(SpinnerAdapter adapter) or addPokerItem(PokerItemAdapter item) instead of setAdapter(SpinnerAdapter)");
	}

	public void setupPokerGroupAdapter() {
		if (this.mConnector != null) {
			setAnimationSelectedPosition(HorizontalPosInfo.ANI_SELECT_NEGATIVE_POSITION);
			setPokerGroupAdapter(this.mConnector);
		}
	}

	private void setPokerGroupAdapter(SpinnerAdapter adapter) {
		if (null != this.mAdapter) {
			this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);

			resetList();
		}

		this.mAdapter = adapter;

		this.mOldSelectedPosition = -1;
		this.mOldSelectedRowId = -9223372036854775808L;
		this.mRecycler.clear();

		if (this.mAdapter != null) {
			this.mOldItemCount = this.mItemCount;
			this.mAdapterHasStableIds = this.mAdapter.hasStableIds();
			this.mItemCount = this.mAdapter.getCount();
			checkFocus();

			this.mDataSetObserver = new AdapterDataSetObserver();
			this.mAdapter.registerDataSetObserver(this.mDataSetObserver);

			this.mRecycler.setViewTypeCount(this.mAdapter.getViewTypeCount());

			int position = this.mItemCount > 0 ? 0 : -1;
			if (position == -1)
				Log.d("PokerGroupView", "setPokerGroupAdapter position is INVALID_POSITION ");
			setSelectedPositionInt(position);
			setNextSelectedPositionInt(position);

			if (this.mItemCount == 0) {
				checkSelectionChanged();
			}
		} else {
			checkFocus();
			resetList();

			checkSelectionChanged();
		}

		requestLayout();
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public void setReflectedBitmapAdapter(ReflectedBitmapAdapter bitmapAdapter) {
		this.mBitmapAdapter = bitmapAdapter;
		if ((bitmapAdapter != null) && (this.mBitmapObserver != null)) {
			this.mBitmapAdapter.unregisterDataSetObserver(this.mBitmapObserver);
		}

		if (bitmapAdapter != null) {
			this.mBitmapObserver = new ReflectedBitmapObserver();
			bitmapAdapter.registerDataSetObserver(this.mBitmapObserver);
		}
	}

	public void notifyDataSetChanged() {
		if (this.mConnector != null) {
			this.mConnector.notifyDataSetChanged();
		}

		clearReflectBitmap();
	}

	public void notifyDataSetInvalidated() {
		if (this.mConnector != null) {
			this.mConnector.notifyDataSetInvalidated();
		}

		clearReflectBitmap();
	}

	void onSelectionChanged(int oldSelectedPosition, int newSelectedPosition, long oldSelectedRowId, long newSelectedRowId) {
		if (this.mInLayout) {
			return;
		}

		startAnimation(oldSelectedPosition, newSelectedPosition);
	}

	private void startAnimation(int oldSelectedPosition, int newSelectedPosition) {
		View newSelectedView = null;
		View oldSelectedView = null;
		if (oldSelectedPosition != -1) {
			oldSelectedView = getChildAt(oldSelectedPosition - this.mFirstPosition);
		}
		if (newSelectedPosition != -1) {
			newSelectedView = getChildAt(newSelectedPosition - this.mFirstPosition);
		}

		if ((newSelectedView != null) && (oldSelectedView != null) && (oldSelectedView != newSelectedView)) {
			startChangeAnimation(oldSelectedPosition, newSelectedPosition);
		}

		startPokerFlowZoomAnimation(oldSelectedView, newSelectedView);
	}

	private void startAnimationNotUseCheck(int oldSelectedPosition, int newSelectedPosition) {
		View newSelectedView = null;
		View oldSelectedView = null;
		if (oldSelectedPosition != -1) {
			oldSelectedView = getChildAt(oldSelectedPosition - this.mFirstPosition);
		}
		if (newSelectedPosition != -1) {
			newSelectedView = getChildAt(newSelectedPosition - this.mFirstPosition);
		}

		startChangeAnimation(oldSelectedPosition, newSelectedPosition);

		startPokerFlowZoomAnimation(oldSelectedView, newSelectedView);
	}

	private void startPokerFlowZoomAnimation(View oldSelectedView, View newSelectedView) {
		if (!this.mInLayout) {
			doZoomIn(oldSelectedView);
			doZoomOut(newSelectedView);
		}
	}

	private void doZoomIn(View oldSelectedView) {
		if ((oldSelectedView != null) && ((oldSelectedView instanceof PokerFlow))) {
			PokerFlow pokerflow = (PokerFlow) oldSelectedView;
			if (pokerflow.getPokerFlowZoomAnimationStatus() != 4)
				((PokerFlow) oldSelectedView).doZoomInAnimation();
		}
	}

	private void doZoomOut(View newSelectedView) {
		if ((newSelectedView != null) && ((newSelectedView instanceof PokerFlow))) {
			PokerFlow pokerflow = (PokerFlow) newSelectedView;
			if (pokerflow.getPokerFlowZoomAnimationStatus() != 2)
				((PokerFlow) newSelectedView).doZoomOutAnimation();
		}
	}

	private void setDest() {
		this.mDestX = ((getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft());
		this.mDestY = ((getHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop());
	}

	void layout(int delta, boolean animate) {
		setDest();

		super.layout(delta, animate);

		initPokerGroupViewItem();
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (isAnimating())
			this.needDelayLayout = true;
		else
			super.onLayout(changed, l, t, r, b);
	}

	private void initPokerGroupViewItem() {
		layoutPokerGroupViewItem();
	}

	protected void resetItemLayout(View selectedView) {
		if ((selectedView instanceof PokerFlow)) {
			this.mItemWidth = ((PokerFlow) selectedView).getItemWidth();
			this.mItemHeight = ((PokerFlow) selectedView).getItemHeight();
		} else {
			this.mItemWidth = selectedView.getWidth();
			this.mItemHeight = selectedView.getHeight();
		}
	}

	private void layoutPokerGroupViewItem() {
		int count = getChildCount();
		boolean isZoomed = false;

		if (this.mAdapter != null)
			Log.v("PokerGroupView", "intiititiiiiiiiiiiiiii " + getAdapterCount());
		else
			Log.v("PokerGroupView", "mAdapter is null ");
		int yv;
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child != null) {
				if ((child instanceof PokerFlow)) {
					isZoomed = (child == getSelectedView()) && (getAnimationSelectedPosition() != HorizontalPosInfo.ANI_SELECT_NEGATIVE_POSITION);

					((PokerFlow) child).initPokerFlowItemPos(isZoomed);
				}

				int originX = getCenterOfViewX(child);
				int originY = getCenterOfViewY(child);

				TransInfo transInfo = HorizontalPosInfo.getUniversalInitialPosInfo(originX, originY, this.mDestX, this.mDestY, getItemWidth(), getAdapterCount(),
						getAnimationSelectedPosition(), this.mFirstPosition + i);

				bindTransToView(transInfo, child);

				int xv = getCenterOfViewX(child);
				yv = getCenterOfViewY(child);
			}
		}
	}

	private void bindTransToView(TransInfo Info, View v) {
		if ((v != null) && (Info != null)) {
			v.setScaleX(Info.scaleX);
			v.setScaleY(Info.scaleY);
			v.setTranslationX(Info.x);
			v.setTranslationY(Info.y);
			v.setAlpha(1.0F);
		}
	}

	protected int getMaxWidth(View child, int end) {
		return this.mMaxItemCount * child.getWidth();
	}

	private int getAdapterCount() {
		if (this.mAdapter != null) {
			return this.mAdapter.getCount();
		}
		return -1;
	}

	private int getAnimationSelectedPosition() {
		return this.mSceneNewSelectedPosition;
	}

	private void setAnimationSelectedPosition(int position) {
		this.mSceneNewSelectedPosition = position;
	}

	private void addActorToScene(int oldSelectedPosition, int newSelectedPosition) {
		int originX = 0;
		int originY = 0;

		PosInfo pos = null;

		int count = getChildCount();

		for (int i = 0; i < count; i++) {
			View v = getChildAt(i);

			if (v != null) {
				originX = getCenterOfViewX(v);
				originY = getCenterOfViewY(v);

				if (Math.abs(newSelectedPosition - oldSelectedPosition) >= 1) {
					pos = HorizontalPosInfo.getUniversalAnimatorPosInfo(originX, originY, this.mDestX, this.mDestY, getItemWidth(), this.mFirstPosition + i, getAdapterCount(),
							oldSelectedPosition, newSelectedPosition);
				}

				if (pos != null) {
					this.mViewScene.addActor(new ViewActor(v, pos));
				}
			}
		}
	}

	private void addSelectorToSence(int oldSelectedPosition, int newSelectedPosition) {
		int originX = 0;
		int originY = 0;

		PosInfo pos = null;

		originX = (int) this.mSelectorOriginX;
		originY = (int) this.mSelectorOriginY;

		pos = HorizontalPosInfo
				.getUniversalSelectorPosInfo(originX, originY, this.mDestX, this.mDestY, getItemWidth(), getAdapterCount(), oldSelectedPosition, newSelectedPosition);

		if (pos != null)
			this.mViewScene.addActor(new SelectorActor(this.mSelectorTrans, pos));
	}

	private void playScene() {
		this.mViewScene.setDuration(this.mduration);

		this.mViewScene.setInterpolator(AnimationUtils.loadInterpolator(getContext(), 2114322434));

		this.mViewScene.addUpdateListener(new Scene.SceneUpdateListener() {
			public void OnSceneUpdate(ValueAnimator animation) {
				PokerGroupView.this.postInvalidate();
			}
		});
		this.mViewScene.addEndListener(new Scene.SceneEndListener() {
			public void OnSceneEnd(Animator animation) {
				int len = PokerGroupView.this.needDetachPosArray.length;
				int detachCount = 0;
				boolean needChangeFirstPosition = false;
				for (int i = 0; i < len; i++) {
					int pos = PokerGroupView.this.needDetachPosArray[i];
					if ((pos != -1) && (PokerGroupView.this.getChildCount() > PokerGroupView.this.mMaxItemCount)) {
						PokerGroupView.this.detachViewFromParent(pos);
						detachCount++;
						if (pos == 0) {
							needChangeFirstPosition = true;
						}

					}

				}

				if (needChangeFirstPosition)
					PokerGroupView.this.mFirstPosition += detachCount;
				PokerGroupView.this.clearDetachPosArray();

				if (PokerGroupView.this.needDelayLayout) {
					PokerGroupView.this.mDelayNotifyHandler.sendEmptyMessage(0);
				}
			}
		});
		this.mViewScene.start();
	}

	private void startChangeAnimation(int oldSelectedPosition, int newSelectedPosition) {
		if (!isAnimating()) {
			if (newSelectedPosition != this.mSceneNewSelectedPosition) {
				this.mViewScene.initial();

				if (oldSelectedPosition <= newSelectedPosition)
					setAniMoveDirection(1);
				else {
					setAniMoveDirection(0);
				}

				addActorToScene(this.mSceneNewSelectedPosition, newSelectedPosition);
				addSelectorToSence(this.mSceneNewSelectedPosition, newSelectedPosition);

				this.mSceneOldSelectedPosition = oldSelectedPosition;
				this.mSceneNewSelectedPosition = newSelectedPosition;
				setAnimationSelectedPosition(this.mSceneNewSelectedPosition);

				playScene();
			}
		}
	}

	public boolean setOnPokerFlowItemClickListener(int position, OnPokerFlowItemClickListener listener) {
		if (!this.mConnector.isPokerFlow(position)) {
			return false;
		}

		final int pos = position;
		PokerFlow pokerFlow = this.mConnector.getPokerFlow(pos);
		if (pokerFlow != null) {
			if (listener == null) {
				pokerFlow.setOnItemClickListener(null);
			} else {
				final OnPokerFlowItemClickListener lis = listener;
				pokerFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					int itemPos = pos;
					PokerGroupView.OnPokerFlowItemClickListener itemLis = lis;

					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						this.itemLis.onPokerFlowItemClick(parent, view, this.itemPos, position, this.itemPos, id);
					}
				});
			}
		}

		return true;
	}

	public boolean performItemClick(View view, int position, long id) {
		if ((view instanceof PokerFlow)) {
			PokerFlow item = (PokerFlow) view;
			if (item.mSelectedPosition != -1)
				return item.performItemClick(item.getSelectedView(), item.mSelectedPosition, item.getSelectedItemId());
		}
		return super.performItemClick(view, position, id);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 19:
		case 20:
			View view = getSelectedView();
			if ((view instanceof PokerFlow)) {
				this.handleKeyDownByPokerFlow = view.onKeyDown(keyCode, event);
				return this.handleKeyDownByPokerFlow;
			}

			if (this.mActionBar != null) {
				return true;
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 19:
		case 20:
			View view = getSelectedView();
			if ((view instanceof PokerFlow)) {
				this.handleKeyDownByPokerFlow = false;
				if (((PokerFlow) view).getChildCount() > 1) {
					return view.onKeyUp(keyCode, event);
				}

			}

			if (this.mActionBar != null) {
				return handleSpecialEventWidthActionbar(keyCode);
			}
			break;
		}

		return super.onKeyUp(keyCode, event);
	}

	protected boolean handleSpecialEventWidthActionbar(int keyCode) {
		int itemCount = this.mActionBar.getNavigationItemCount();
		int selectedIndex = this.mActionBar.getSelectedNavigationIndex();
		switch (keyCode) {
		case 20:
			selectedIndex = selectedIndex + 1 < itemCount ? selectedIndex + 1 : itemCount - 1;
			break;
		case 19:
			selectedIndex = selectedIndex - 1 > 0 ? selectedIndex - 1 : 0;
		}

		boolean handle = false;
		if (selectedIndex != this.mActionBar.getSelectedNavigationIndex()) {
			this.mActionBar.setSelectedNavigationItem(selectedIndex);
			handle = true;
		}
		return handle;
	}

	public void registerActionbar(ActionBar actionBar) {
		this.mActionBar = actionBar;
	}

	public boolean setOnPokerFlowItemSelectedListener(int position, OnPokerFlowItemSelectedListener listener) {
		if (!this.mConnector.isPokerFlow(position)) {
			return false;
		}

		final int pos = position;
		PokerFlow pokerFlow = this.mConnector.getPokerFlow(pos);
		if (pokerFlow != null) {
			if (listener == null) {
				pokerFlow.setOnItemSelectedListener(null);
			} else {
				final OnPokerFlowItemSelectedListener lis = listener;

				pokerFlow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					int itemPos = pos;
					PokerGroupView.OnPokerFlowItemSelectedListener itemLis = lis;

					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						this.itemLis.onItemFlowSelected(parent, view, this.itemPos, position, this.itemPos, id);
					}

					public void onNothingSelected(AdapterView<?> parent) {
						this.itemLis.onNothingSelected(parent, this.itemPos);
					}
				});
			}
		}

		return true;
	}

	public boolean setOnPokerFlowItemLongClickListener(int position, OnPokerFlowItemLongClickListener listener) {
		if (!this.mConnector.isPokerFlow(position)) {
			return false;
		}

		final int pos = position;
		PokerFlow pokerFlow = this.mConnector.getPokerFlow(pos);
		if (pokerFlow != null) {
			if (listener == null) {
				pokerFlow.setOnItemLongClickListener(null);
			} else {
				final OnPokerFlowItemLongClickListener lis = listener;
				pokerFlow.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					int itemPos = pos;
					PokerGroupView.OnPokerFlowItemLongClickListener itemLis = lis;

					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						return this.itemLis.onPokerFlowItemLongClick(parent, view, this.itemPos, position, this.itemPos, id);
					}
				});
			}

		}

		return true;
	}

	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		View mSelectedView = getSelectedView();
		if ((mSelectedView != null) && (isPokerFlow(mSelectedView))) {
			PokerFlow pokerflow = (PokerFlow) mSelectedView;
			if (pokerflow.getPokerFlowZoomAnimationStatus() == 2) {
				return false;
			}

		}

		return true;
	}

	public boolean onDown(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		int pos = getDownViewPosition((int) x, (int) y);
		if (!this.mGainFocus) {
			requestFocus();
		}

		if (pos != -1) {
			if (this.mDownTouchView != null) {
				this.mDownTouchView.setPressed(true);
			}
		}
		return true;
	}

	public boolean onSingleTapUp(MotionEvent e) {
		if ((this.mDownTouchPosition >= 0) && (!isAnimating())) {
			changeToDownTouchPosStatus();
			setSelectedPositionInt(this.mDownTouchPosition);

			if (this.mDownTouchPosition == this.mSelectedPosition) {
				performItemClick(this.mDownTouchView, this.mDownTouchPosition, this.mAdapter.getItemId(this.mDownTouchPosition));
			}

			return true;
		}

		return false;
	}

	private void initialPokerFlowAfterAttached(int attachCount, int startPos) {
		for (int i = 0; i < attachCount; i++) {
			View vAttached = getChildAt(startPos + i);

			if ((vAttached != null) && ((vAttached instanceof PokerFlow))) {
				((PokerFlow) vAttached).initialPokerFlowItemInCenter();
			}
		}
	}

	private void changeToDownTouchPosStatus() {
		int step = this.mDownTouchPosition - getAnimationSelectedPosition();
		int childCount = getChildCount();

		int attachCount = getNeedAttachViewCount();
		int detachCount = attachCount;

		clearDetachPosArray();
		if (step == 0) {
			return;
		}
		if (step > 0) {
			View last = getChildAt(childCount - 1);
			int lastVisiblePos = getLastVisiblePosition();
			View attached = addViewBackward(last, lastVisiblePos, attachCount);

			int childCountNew = getChildCount();

			initialPokerFlowAfterAttached(attachCount, childCountNew - attachCount);

			for (int i = 0; i < detachCount; i++) {
				this.needDetachPosArray[i] = i;
			}

		} else {
			View first = getChildAt(0);

			int firstPos = getFirstVisiblePosition();

			View attached = addViewFoward(first, firstPos, attachCount);

			this.mFirstPosition -= attachCount;

			initialPokerFlowAfterAttached(attachCount, 0);

			int childCountNew = getChildCount();

			for (int i = 0; i < detachCount; i++) {
				this.needDetachPosArray[i] = (childCountNew - 1 - i);
			}

		}

		startAnimationNotUseCheck(getAnimationSelectedPosition(), this.mDownTouchPosition);
	}

	private int getNeedAttachViewCount() {
		int count = this.mDownTouchPosition - getAnimationSelectedPosition();

		int tatalCount = getAdapterCount();
		if (tatalCount <= 0)
			return 0;
		int selectedStartPosition = HorizontalPosInfo.getVisableStartPosition(tatalCount, getAnimationSelectedPosition());
		int touchStartPosition = HorizontalPosInfo.getVisableStartPosition(tatalCount, this.mDownTouchPosition);

		if (count == 0)
			return 0;
		if (count > 0) {
			return touchStartPosition - selectedStartPosition;
		}

		return selectedStartPosition - touchStartPosition;
	}

	private void clearDetachPosArray() {
		int len = this.needDetachPosArray.length;
		for (int i = 0; i < len; i++)
			this.needDetachPosArray[i] = -1;
	}

	private int getDownViewPosition(int x, int y) {
		int selPos = this.mSelectedPosition - this.mFirstPosition;

		this.mDownTouchPosition = -1;
		this.mDownTouchView = null;
		int count = getChildCount();
		int order = -1;

		for (int i = 0; i < count; i++) {
			if (this.mUnCoverChildDrawingOrderList != null) {
				Integer orderInterger = (Integer) this.mUnCoverChildDrawingOrderList.get(i);
				if (orderInterger != null) {
					order = orderInterger.intValue();

					if (order >= count - 4) {
						View v = getChildAt(i);

						if (isInViewRect(v, x, y)) {
							this.mDownTouchPosition = (this.mFirstPosition + i);

							break;
						}
					}
				}
			}

		}

		return this.mDownTouchPosition;
	}

	private boolean isInViewRect(View v, int x, int y) {
		boolean isInView = false;
		if (v != null) {
			Rect rect = getRectOfView(v);
			isInView = rect.contains(x, y);

			this.mDownTouchView = v;
		}
		return isInView;
	}

	public void onShowPress(MotionEvent e) {
		super.onShowPress(e);
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return true;
	}

	public void onLongPress(MotionEvent e) {
		super.onLongPress(e);
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return true;
	}

	private int getDrawingOrder(int childCount, int selectedIndex, int i) {
		int subPoint = 0;
		int addPoint = 0;
		int result = childCount - 1;

		subPoint = selectedIndex;
		addPoint = selectedIndex;

		subPoint = selectedIndex;
		addPoint = selectedIndex;

		if (selectedIndex < 0) {
			return childCount - 1 - i;
		}

		if (selectedIndex > childCount - 1) {
			return i;
		}

		if (result == i) {
			return selectedIndex;
		}
		for (int v = 0; v <= childCount; v++) {
			subPoint--;
			if (subPoint >= 0) {
				result--;
				if (result == i) {
					return subPoint;
				}
			}
			addPoint++;
			if (addPoint <= childCount - 1) {
				result--;
				if (result == i) {
					return addPoint;
				}
			}
		}
		return i;
	}

	private int getAnimatorSelectPos() {
		int step = this.mSceneNewSelectedPosition - this.mSceneOldSelectedPosition;
		int sign = 1;
		if (step < 0) {
			step = -step;
			sign = -1;
		}

		float div = 1.0F / step;
		float value = getCurAnimatorValue();

		value *= step;

		return this.mSceneOldSelectedPosition + (int) (value + 0.5F) * sign;
	}

	protected int getChildDrawingOrder(int childCount, int i) {
		if (isAnimating()) {
			int aniPos = getAnimatorSelectPos();

			int drawOrderIndex = getDrawingOrder(childCount, aniPos - this.mFirstPosition, i);

			return drawOrderIndex;
		}
		int drawOrderIndex = getDrawingOrder(childCount, getAnimationSelectedPosition() - this.mFirstPosition, i);

		if (!isAnimating()) {
			this.mUnCoverChildDrawingOrderList.put(drawOrderIndex, Integer.valueOf(i));
		}
		return drawOrderIndex;
	}

	private int getRelSelectedPosition() {
		if (this.mSelectedPosition == -1)
			;
		return this.mSelectedPosition;
	}

	boolean arrowScrollImpl(int direction) {
		if (getChildCount() <= 0) {
			return false;
		}

		View selectedView = getSelectedView();
		int selectedPos = this.mSelectedPosition;

		int nextSelectedPosition = lookForSelectablePositionOnScreen(direction);

		boolean isCommonStatus = checkNeedScroll(direction, selectedPos, nextSelectedPosition);

		AbsHorizontalListView.ArrowScrollFocusResult focusResult = this.mItemsCanFocus ? arrowScrollFocused(direction) : null;
		if (focusResult != null) {
			nextSelectedPosition = focusResult.getSelectedPosition();
		}

		boolean needToRedraw = focusResult != null;
		if (nextSelectedPosition != -1) {
			handleNewSelectionChange(selectedView, direction, nextSelectedPosition, focusResult != null);
			setSelectedPositionInt(nextSelectedPosition);
			setNextSelectedPositionInt(nextSelectedPosition);
			selectedView = getSelectedView();
			selectedPos = nextSelectedPosition;
			if ((this.mItemsCanFocus) && (focusResult == null)) {
				View focused = getFocusedChild();
				if (focused != null) {
					focused.clearFocus();
				}
			}
			needToRedraw = true;
			checkSelectionChanged();
		}

		if ((this.mItemsCanFocus) && (focusResult == null) && (selectedView != null) && (selectedView.hasFocus())) {
			View focused = selectedView.findFocus();
			if ((!isViewAncestorOf(focused, this)) || (distanceToView(focused) > 0)) {
				focused.clearFocus();
			}

		}

		if ((nextSelectedPosition == -1) && (selectedView != null) && (!isViewAncestorOf(selectedView, this))) {
			selectedView = null;
			hideSelector();
		}

		if (needToRedraw) {
			if (!awakenScrollBars()) {
				invalidate();
			}
			return true;
		}

		return false;
	}

	private boolean checkNeedScroll(int direction, int selectedPos, int nextSelectedPosition) {
		int totalCount = this.mAdapter.getCount();
		int count = getChildCount();

		clearDetachPosArray();

		if (direction == 66) {
			int lastVisiblePos = getLastVisiblePosition();
			if ((nextSelectedPosition - this.mFirstPosition > 3) && (lastVisiblePos < totalCount - 1)) {
				View last = getChildAt(count - 1);
				View attached = addViewBackward(last, lastVisiblePos);
				if ((attached != null) && ((attached instanceof PokerFlow))) {
					((PokerFlow) attached).initialPokerFlowItemInCenter();
				}
				this.needDetachPosArray[0] = 0;
				return true;
			}
		} else if ((direction == 17) && (nextSelectedPosition - this.mFirstPosition < 3) && (this.mFirstPosition > 0)) {
			View first = getChildAt(0);

			View attached = addViewFoward(first, this.mFirstPosition);
			if ((attached != null) && ((attached instanceof PokerFlow))) {
				((PokerFlow) attached).initialPokerFlowItemInCenter();
			}
			this.mFirstPosition -= 1;
			this.needDetachPosArray[0] = (getChildCount() - 1);
			return true;
		}

		return false;
	}

	private int getCenterOfView(View v) {
		return v.getWidth() / 2 + v.getLeft();
	}

	private int getCenterOfViewX(View v) {
		return v.getWidth() / 2 + v.getLeft();
	}

	private int getCenterOfViewY(View v) {
		return v.getHeight() / 2 + v.getTop();
	}

	boolean moveNext() {
		boolean handled = false;
		if (!isAnimating())
			handled = super.moveNext();
		return handled;
	}

	boolean movePrevious() {
		boolean handled = false;
		if (!isAnimating())
			handled = super.movePrevious();
		else {
			return true;
		}
		return handled;
	}

	private void getRect(Rect rect, TransInfo info) {
		int dw = this.mSelectorBorderWidth;
		int dh = this.mSelectorBorderHeight;

		int wRect = getItemWidth();
		int hRect = getItemHeight();

		if ((wRect == 0) || (hRect == 0)) {
			return;
		}

		float x = info.x + this.mSelectorOriginX;
		float y = info.y + this.mSelectorOriginY;

		float scaleX = info.scaleX;
		float scaleY = info.scaleY;

		int wSelectorRect = wRect / 2 + dw;
		int hSelectorRect = hRect / 2 + dh;

		rect.left = ((int) (x - wSelectorRect * scaleX));
		rect.right = ((int) (x + wSelectorRect * scaleX));

		rect.top = ((int) (y - hSelectorRect * scaleY));
		rect.bottom = ((int) (y + hSelectorRect * scaleY));
	}

	public void setSelectorAlpha(float alpha) {
		this.mSelectorTrans.alpha = alpha;
	}

	private float SelectorAlphaInterpolate(float startAlpha, float endAlpha, float interpolateAlpha1, float interpolateTime1, float interpolateAlpha2, float interpolateTime2,
			float value) {
		float alpha = 0.0F;
		if (value < 0.0F) {
			alpha = startAlpha;
		}

		if (value > 1.0F) {
			alpha = endAlpha;
		}

		if ((value < interpolateTime1) && (value >= 0.0D)) {
			alpha = startAlpha + (value - 0.0F) * (interpolateAlpha1 - startAlpha) / (interpolateTime1 - 0.0F);
		}

		if ((value < interpolateTime2) && (value >= interpolateTime1)) {
			alpha = interpolateAlpha1 + (value - interpolateTime1) * (interpolateAlpha2 - interpolateAlpha1) / (interpolateTime2 - interpolateTime1);
		}

		if (value > interpolateTime2) {
			alpha = interpolateAlpha2 + (value - interpolateTime2) * (endAlpha - interpolateAlpha2) / (1.0F - interpolateTime2);
		}

		return alpha;
	}

	private float SelectorAlphaChange(float value) {
		float interpolateAlpha1 = 0.6F;
		float interpolateTime1 = 0.3F;

		float interpolateAlpha2 = 0.3F;
		float interpolateTime2 = 0.6F;

		return SelectorAlphaInterpolate(1.0F, 1.0F, interpolateAlpha1, interpolateTime1, interpolateAlpha2, interpolateTime2, value);
	}

	private void alphaElasticityAni(float value) {
		setSelectorAlpha(SelectorAlphaChange(value));
	}

	public void changeSelectorAlpha(float value, PokerFlow view) {
		alphaElasticityAni(value);

		Rect rect = this.mSelector.copyBounds();

		postInvalidate(rect.left, rect.top, rect.right, rect.bottom);
	}

	void renderSelector(Canvas canvas, TransInfo transInfo) {
		if (transInfo == null) {
			return;
		}

		Rect rect = new Rect();

		getRect(rect, transInfo);

		float alpha = transInfo.alpha;

		if (!rect.isEmpty()) {
			this.mSelector.setBounds(rect);
			this.mSelector.setAlpha((int) (alpha * 255.0F));
			this.mSelector.draw(canvas);
		}
	}

	protected void drawSelector(Canvas canvas) {
		if (this.mGainFocus)
			if (isAnimating()) {
				renderSelector(canvas, this.mSelectorTrans);
			} else {
				TransInfo transInfo = HorizontalPosInfo.getUniversalInitialSelectorPosInfo(this.mSelectorOriginX, this.mSelectorOriginY, this.mDestX, this.mDestY, getItemWidth(),
						getAdapterCount(), getAnimationSelectedPosition());

				if (getAnimationSelectedPosition() != HorizontalPosInfo.ANI_SELECT_NEGATIVE_POSITION) {
					transInfo.alpha = this.mSelectorTrans.alpha;
				}

				renderSelector(canvas, transInfo);
			}
	}

	private void setTextArray(ArrayList<String> textArray) {
		if ((textArray != null) && (textArray.size() > 0)) {
			if (this.mTextPaint == null) {
				this.mTextPaint = new Paint();
				this.mTextPaint.setTextSize(16);
				this.mTextPaint.setColor(Color.WHITE);
			}
			int count = textArray.size();
			this.mTextWidths = new int[count];
			String text = null;
			for (int i = 0; i < count; i++) {
				text = (String) textArray.get(i);
				if (text != null)
					this.mTextWidths[i] = ((int) this.mTextPaint.measureText(text));
			}
		}
	}

	private int getPositionForViewLocal(View v) {
		return this.mFirstPosition + indexOfChild(v);
	}

	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean isVerticalPokerFlow = isPokerFlow(child);

		getRectOfView(child, this.mViewRealRect, isVerticalPokerFlow);

		int index = getPositionForViewLocal(child);

		if (this.mIsDrawShadow) {
			drawChildShadow(canvas, child, index, this.mViewRealRect);
		}

		boolean handler = super.drawChild(canvas, child, drawingTime);

		if (this.mIsDrawReflectBitmap) {
			drawChildReflecting(canvas, child, index, this.mViewRealRect, isVerticalPokerFlow);
		}
		int position = index - this.mFirstPosition;

		drawChildText(canvas, child, this.mViewRealRect, isVerticalPokerFlow, position);

		return handler;
	}

	private boolean isPokerFlow(View v) {
		if ((v instanceof PokerFlow)) {
			return true;
		}
		return false;
	}

	private void drawChildText(Canvas canvas, View child, Rect rect, boolean isVerticalPokerFlow, int position) {
		if (isVerticalPokerFlow) {
			PokerFlow pokerFlow = (PokerFlow) child;
			int pokerFlowZoomAnimationStatus = pokerFlow.getPokerFlowZoomAnimationStatus();
			switch (pokerFlowZoomAnimationStatus) {
			case 0:
				drawPokerGroupItemText(canvas, child, this.textSpaceFromDown, rect, position);
				break;
			case 3:
				drawPokerGroupItemText(canvas, child, this.textSpaceFromDown, rect, position);
				break;
			case 2:
				drawPokerFlowItemText(canvas, pokerFlow, this.textSpaceFromUp, rect, position);
				break;
			case 1:
				break;
			case 4:
				drawPokerGroupItemText(canvas, child, this.textSpaceFromDown, rect, position);
			}
		} else {
			drawPokerGroupItemText(canvas, child, this.textSpaceFromDown, rect, position);
		}
	}

	private void drawPokerFlowItemText(Canvas canvas, PokerFlow child, int space, Rect rectInput, int position) {
		int selectedIndex = child.getSelectedItemPosition();
		ArrayList list = null;
		if (this.mPokerFlowTextDisplayList != null) {
			list = (ArrayList) this.mPokerFlowTextDisplayList.get(position + this.mFirstPosition);
		}

		if ((list != null) && (list.size() > 0) && (selectedIndex < list.size())) {
			String text = (String) list.get(selectedIndex);
			int textWidth = (int) this.mTextPaint.measureText(text);

			Rect rect = rectInput;

			int width = rect.right - rect.left;
			Rect singleMaskShadeRect = new Rect();
			int shadeWidth = this.mSingleMaskShade.getIntrinsicWidth();
			singleMaskShadeRect.left = (rect.left + width - (width + shadeWidth >> 1));

			singleMaskShadeRect.right = (rect.left + width - (width - shadeWidth >> 1));

			singleMaskShadeRect.bottom = rect.bottom;
			singleMaskShadeRect.top = (rect.bottom - this.mSingleMaskShade.getIntrinsicHeight());
			this.mSingleMaskShade.setBounds(singleMaskShadeRect);

			this.mSingleMaskShade.draw(canvas);
			drawText(canvas, text, rect, width, textWidth, space);
		}
	}

	private void drawPokerGroupItemText(Canvas canvas, View child, int space, Rect rectInput, int position) {
		Integer drawOrderIndex = (Integer) this.mUnCoverChildDrawingOrderList.get(position);
		int count = getChildCount();
		if (((drawOrderIndex != null) && (drawOrderIndex.intValue() >= count - 4)) || ((count <= 4) && (position >= 0) && (position < getCount()))) {
			Rect rect = rectInput;

			int width = rect.right - rect.left;
			String text = "";

			if (this.mPokerGroupTextDisplayList != null) {
				text = (String) this.mPokerGroupTextDisplayList.get(position + this.mFirstPosition);
			}

			if (this.mTextWidths != null)
				drawText(canvas, text, rect.left + width - (width + this.mTextWidths[(position + this.mFirstPosition)] >> 1), rect.bottom + space);
		}
	}

	private void drawText(Canvas canvas, String text, int x, int y) {
		canvas.drawText(text, x, y, this.mTextPaint);
	}

	private void drawText(Canvas canvas, String text, Rect rect, int width, int textWidth, int space) {
		if (width - textWidth > 48) {
			drawText(canvas, text, rect.left + width - (width + textWidth >> 1), rect.bottom + space);
		} else {
			TextUtils tu = new TextUtils();
			String ellipsisText = tu.ellipsis(this.mTextPaint, text, width, 48).toString();

			int displayWidth = (int) this.mTextPaint.measureText(ellipsisText);
			canvas.drawText(ellipsisText, rect.left + width - (width + displayWidth >> 1), rect.bottom + space, this.mTextPaint);
		}
	}

	private void drawChildReflecting(Canvas canvas, View child, int index, Rect rectInput, boolean isVerticalPokerFlow) {
		Bitmap bmp = findReflectBitmap(child, index);
		if (bmp == null) {
			return;
		}
		float yScale = 0.0F;

		this.mReflectingDstRect.set(rectInput);

		if (isUseReflectAdapter()) {
			int viewWidth = this.mReflectingDstRect.right - this.mReflectingDstRect.left;
			int bitmapWidth = bmp.getWidth();
			yScale = viewWidth * 1.0F / bitmapWidth;
		} else {
			yScale = child.getScaleY();
		}

		this.mReflectingDstRect.top = this.mReflectingDstRect.bottom;
		this.mReflectingDstRect.bottom = (this.mReflectingDstRect.top + (int) (bmp.getHeight() * yScale));

		if (bmp != null)
			if (isVerticalPokerFlow) {
				PokerFlow pokerFlow = (PokerFlow) child;

				float fraction = getCurAnimatorValue();

				if (isAnimating()) {
					Paint paint = this.mReflectingPaint;
					int alpha = 255;

					if ((pokerFlow.getPokerFlowZoomAnimationStatus() == 0) || (pokerFlow.getPokerFlowZoomAnimationStatus() == 4)) {
						this.mReflectingSrcRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
						canvas.drawBitmap(bmp, this.mReflectingSrcRect, this.mReflectingDstRect, null);
					} else {
						if (pokerFlow.getPokerFlowZoomAnimationStatus() == 1) {
							alpha = (int) (255.0F * (1.0F - fraction));
							paint.setAlpha(alpha);
						} else if (pokerFlow.getPokerFlowZoomAnimationStatus() == 3) {
							alpha = (int) (255.0F * fraction);
							paint.setAlpha(alpha);
						}

						this.mReflectingSrcRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
						canvas.drawBitmap(bmp, this.mReflectingSrcRect, this.mReflectingDstRect, paint);
					}

				} else if (pokerFlow.getPokerFlowZoomAnimationStatus() != 2) {
					this.mReflectingSrcRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
					canvas.drawBitmap(bmp, this.mReflectingSrcRect, this.mReflectingDstRect, null);
				}
			} else {
				this.mReflectingSrcRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
				canvas.drawBitmap(bmp, this.mReflectingSrcRect, this.mReflectingDstRect, null);
			}
	}

	public void setTextArrayOnPokerGroup(ArrayList<String> list) {
		this.mPokerGroupTextDisplayList = list;
		setTextArray(this.mPokerGroupTextDisplayList);
	}

	public void setTextArrayOnPokerFlow(int position, ArrayList<String> list) {
		if (this.mPokerFlowTextDisplayList == null) {
			this.mPokerFlowTextDisplayList = new SparseArray();
		}
		if ((list != null) && (list.size() > 0))
			this.mPokerFlowTextDisplayList.put(position, list);
	}

	public void disableTextDisplayOnPokerGroup(boolean disable) {
		this.mDisableTextDisplay = disable;
	}

	public void setOnLastItemMoveInPokerFlowListener(OnLastItemMoveInPokerFlowListener listener) {
		this.mOnLastItemMoveListener = listener;
	}

	public void setDrawReflectedImage(boolean isDraw) {
		this.mIsDrawReflectBitmap = isDraw;
	}

	private boolean isUseReflectAdapter() {
		if (this.mBitmapAdapter != null) {
			return true;
		}

		return false;
	}

	private Bitmap findReflectBitmap(View view, int index) {
		int position = index;

		if (isUseReflectAdapter()) {
			return this.mBitmapAdapter.getReflectedBitmapHorizontal(position);
		}

		return null;
	}

	public void clearReflectBitmap() {
		if (!isUseReflectAdapter())
			;
	}

	public void setDrawShadowImage(boolean isDraw) {
		this.mIsDrawShadow = isDraw;
	}

	private int getShadowWidth() {
		return (int) (getItemWidth() * this.mShadowRatio);
	}

	private int getShadowHeight() {
		return (int) (getItemHeight() * this.mShadowRatio);
	}

	private Rect getShadowDrawUpDownRect(boolean isLeft, Rect rect, View view) {
		Rect drawRect = new Rect(rect);
		int shadowHeight = getShadowHeight();
		if (isLeft) {
			drawRect.bottom = (drawRect.top + 1);
			drawRect.top = (drawRect.bottom - (int) (shadowHeight * view.getScaleY()));
		} else {
			drawRect.top = (drawRect.bottom - 1);
			drawRect.bottom += (int) (shadowHeight * view.getScaleY());
		}

		return drawRect;
	}

	private Rect getShadowDrawLeftRightRect(boolean isLeft, Rect rect, View view) {
		Rect drawRect = new Rect(rect);
		int shadowWidht = getShadowWidth();
		if (isLeft) {
			drawRect.right = (drawRect.left + 1);
			drawRect.left -= (int) (shadowWidht * view.getScaleX());
		} else {
			drawRect.left = (drawRect.right - 1);
			drawRect.right += (int) (shadowWidht * view.getScaleX());
		}

		return drawRect;
	}

	private void getShadowDrawLeftRect(Rect drawRect, Rect rect, View view) {
		drawRect.set(rect);
		int shadowWidht = getShadowWidth();

		drawRect.right = (drawRect.left + 1);
		drawRect.left -= (int) (shadowWidht * view.getScaleX());
	}

	private void getShadowDrawRightRect(Rect drawRect, Rect rect, View view) {
		drawRect.set(rect);

		int shadowWidht = getShadowWidth();

		drawRect.left = (drawRect.right - 1);
		drawRect.right += (int) (shadowWidht * view.getScaleX());
	}

	private Bitmap getShadowNextBitmap() {
		if (this.mShadowNextBitmap == null) {
			this.mShadowNextBitmap = loadShadowBitmap(2114191557);
		}
		return this.mShadowNextBitmap;
	}

	private Bitmap getShadowPreBitmap() {
		if (this.mShadowPreBitmap == null) {
			this.mShadowPreBitmap = loadShadowBitmap(2114191556);
		}
		return this.mShadowPreBitmap;
	}

	private NinePatch getShadowNextNinePatch() {
		if (this.mShadowNextNinePatch == null) {
			getShadowNextBitmap();
			this.mShadowNextNinePatch = new NinePatch(this.mShadowNextBitmap, this.mShadowNextBitmap.getNinePatchChunk(), null);
		}
		return this.mShadowNextNinePatch;
	}

	private NinePatch getShadowPreNinePatch() {
		if (this.mShadowPreNinePatch == null) {
			getShadowPreBitmap();
			this.mShadowPreNinePatch = new NinePatch(this.mShadowPreBitmap, this.mShadowPreBitmap.getNinePatchChunk(), null);
		}
		return this.mShadowPreNinePatch;
	}

	private Bitmap loadShadowBitmap(int id) {
		Resources res = getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, id);
		return bmp;
	}

	private boolean getShadowClipRect(Rect rect, int index) {
		View view = getChildAt(index);
		if (view == null) {
			return false;
		}
		getRectOfView(rect, view);

		return true;
	}

	public void setImageShadowWidthRadio(float radio) {
		this.mShadowRatio = radio;
	}

	private void setAniMoveDirection(int moveDir) {
		this.mAniMoveDirection = moveDir;
	}

	private int getAniMoveDirection() {
		return this.mAniMoveDirection;
	}

	private void drawChildShadowAniRunning(Canvas canvas, View child, int index, Rect rect) {
		int selIndex = getRelSelectedPosition();

		int OldSelIndex = selIndex - 1;

		boolean isPre = true;
		int alpha = 255;

		if (checkMoveToPre())
			OldSelIndex = selIndex + 1;
		else {
			OldSelIndex = selIndex - 1;
		}

		float value = getCurAnimatorValue();

		if (index == selIndex) {
			alpha = (int) (255.0F * value);

			if (checkMoveToPre()) {
				renderChildShadowPre(canvas, child, index, 255, rect);
				renderChildShadowNext(canvas, child, index, alpha, rect);
			} else {
				renderChildShadowPre(canvas, child, index, 255, rect);
				renderChildShadowNext(canvas, child, index, 255, rect);
			}
		} else if (index == OldSelIndex) {
			alpha = (int) (255.0F * (1.0F - value));

			if (checkMoveToPre()) {
				renderChildShadowPre(canvas, child, index, alpha, rect);
				renderChildShadowNext(canvas, child, index, 255, rect);
			} else {
				renderChildShadowPre(canvas, child, index, 255, rect);
				renderChildShadowNext(canvas, child, index, alpha, rect);
			}
		} else {
			if (selIndex < index)
				isPre = false;
			else {
				isPre = true;
			}
			if (isPre)
				renderChildShadowPre(canvas, child, index, 255, rect);
			else
				renderChildShadowNext(canvas, child, index, 255, rect);
		}
	}

	private void renderChildShadowPre(Canvas canvas, View child, int index, int alpha, Rect rect) {
		getShadowDrawLeftRect(this.mShadowDrawRect, rect, child);

		boolean isExist = getShadowClipRect(this.mShadowClipRect, index - this.mFirstPosition - 1);

		if (isExist) {
			NinePatch ninePatchPre = getShadowPreNinePatch();

			canvas.save();

			this.mShadowPaint.setAlpha(alpha);

			canvas.clipRect(this.mShadowClipRect);

			if (ninePatchPre != null) {
				ninePatchPre.draw(canvas, this.mShadowDrawRect, this.mShadowPaint);
			}

			canvas.restore();
		}
	}

	private void renderChildShadowNext(Canvas canvas, View child, int index, int alpha, Rect rect) {
		getShadowDrawRightRect(this.mShadowDrawRect, rect, child);

		boolean isExist = getShadowClipRect(this.mShadowClipRect, index - this.mFirstPosition + 1);

		if (isExist) {
			NinePatch ninePatchNext = getShadowNextNinePatch();

			canvas.save();

			this.mShadowPaint.setAlpha(alpha);

			canvas.clipRect(this.mShadowClipRect);
			if (ninePatchNext != null) {
				ninePatchNext.draw(canvas, this.mShadowDrawRect, this.mShadowPaint);
			}

			canvas.restore();
		}
	}

	private void renderChildShadowPre_UseShader(Canvas canvas, View child, int index, int alpha, Rect rect) {
		Rect rectDrawPre = getShadowDrawLeftRightRect(true, rect, child);

		boolean isExistRect = getShadowClipRect(this.mShadowClipRect, index - this.mFirstPosition - 1);

		if (isExistRect) {
			canvas.save();

			this.mShadowPaint = new Paint();

			LinearGradient shader = new LinearGradient(rectDrawPre.left, rectDrawPre.top, rectDrawPre.right, rectDrawPre.top, 0, -1073741824, Shader.TileMode.CLAMP);

			this.mShadowPaint.setShader(shader);

			canvas.clipRect(this.mShadowClipRect);

			canvas.drawRect(rectDrawPre, this.mShadowPaint);

			this.mShadowPaint.setShader(null);

			canvas.restore();
		}
	}

	private void renderChildShadowNext_UseShader(Canvas canvas, View child, int index, int alpha, Rect rect) {
		Rect rectDrawNext = getShadowDrawLeftRightRect(false, rect, child);

		boolean isExist = getShadowClipRect(this.mShadowClipRect, index - this.mFirstPosition + 1);

		if (isExist) {
			canvas.save();

			this.mShadowPaint = new Paint();

			LinearGradient shader = new LinearGradient(rectDrawNext.left, rectDrawNext.top, rectDrawNext.right, rectDrawNext.top, -1073741824, 0, Shader.TileMode.CLAMP);

			this.mShadowPaint.setShader(shader);

			canvas.clipRect(this.mShadowClipRect);

			canvas.drawRect(rectDrawNext, this.mShadowPaint);

			this.mShadowPaint.setShader(null);

			canvas.restore();
		}
	}

	private void drawChildShadowAniNotRunning(Canvas canvas, View child, int index, Rect rect) {
		boolean isPre = true;
		int selIndex = getRelSelectedPosition();

		if (selIndex < index)
			isPre = false;
		else {
			isPre = true;
		}

		if (selIndex == index) {
			renderChildShadowPre(canvas, child, index, 255, rect);
			renderChildShadowNext(canvas, child, index, 255, rect);
		} else if (isPre) {
			renderChildShadowPre(canvas, child, index, 255, rect);
		} else {
			renderChildShadowNext(canvas, child, index, 255, rect);
		}
	}

	private boolean checkMoveToPre() {
		return this.mAniMoveDirection == 0;
	}

	private float getCurAnimatorValue() {
		return this.mViewScene.getCurrentValue();
	}

	private void drawChildShadow(Canvas canvas, View child, int index, Rect rect) {
		if (!isAnimating())
			drawChildShadowAniNotRunning(canvas, child, index, rect);
		else
			drawChildShadowAniRunning(canvas, child, index, rect);
	}

	public int getSelectedItemPositionOnPokerFlow(int positionOfPokerGroup) {
		int position = 0;
		if ((this.mConnector != null) && (this.mConnector.isPokerFlow(positionOfPokerGroup))) {
			PokerFlow pokerFlow = this.mConnector.getPokerFlow(positionOfPokerGroup);
			if (pokerFlow != null) {
				return pokerFlow.getSelectedItemPosition();
			}
		}

		return position;
	}

	class ReflectedBitmapObserver extends DataSetObserver {
		ReflectedBitmapObserver() {
		}

		public void onChanged() {
		}
	}

	public static abstract interface OnLastItemMoveInPokerFlowListener {
		public abstract boolean OnLastItemMoveListener();
	}

	public static abstract interface OnPokerFlowItemLongClickListener {
		public abstract boolean onPokerFlowItemLongClick(AdapterView<?> paramAdapterView, View paramView, int paramInt1, int paramInt2, long paramLong1, long paramLong2);
	}

	public static abstract interface OnPokerFlowItemSelectedListener {
		public abstract void onItemFlowSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt1, int paramInt2, long paramLong1, long paramLong2);

		public abstract void onNothingSelected(AdapterView<?> paramAdapterView, int paramInt);
	}

	public static abstract interface OnPokerFlowItemClickListener {
		public abstract void onPokerFlowItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt1, int paramInt2, long paramLong1, long paramLong2);
	}

	class AdapterDataSetObserver extends DataSetObserver {
		private Parcelable mInstanceState = null;

		AdapterDataSetObserver() {
		}

		public void onChanged() {
			PokerGroupView.this.mDataChanged = true;
			PokerGroupView.this.mOldItemCount = PokerGroupView.this.mItemCount;
			PokerGroupView.this.mItemCount = PokerGroupView.this.getAdapter().getCount();

			if ((PokerGroupView.this.getAdapter().hasStableIds()) && (this.mInstanceState != null) && (PokerGroupView.this.mOldItemCount == 0)
					&& (PokerGroupView.this.mItemCount > 0)) {
				PokerGroupView.this.onRestoreInstanceState(this.mInstanceState);
				this.mInstanceState = null;
			} else {
				PokerGroupView.this.rememberSyncState();
			}
			if (PokerGroupView.this.isAnimating()) {
				PokerGroupView.this.needDelayLayout = true;
			} else {
				PokerGroupView.this.checkFocus();
				PokerGroupView.this.requestLayout();
			}
		}

		public void onInvalidated() {
			PokerGroupView.this.mDataChanged = true;
			if (PokerGroupView.this.getAdapter().hasStableIds()) {
				this.mInstanceState = PokerGroupView.this.onSaveInstanceState();
			}

			PokerGroupView.this.mOldItemCount = PokerGroupView.this.mItemCount;
			PokerGroupView.this.mItemCount = 0;
			PokerGroupView.this.mSelectedPosition = -1;
			PokerGroupView.this.mSelectedRowId = -9223372036854775808L;
			PokerGroupView.this.mNextSelectedPosition = -1;
			PokerGroupView.this.mNextSelectedRowId = -9223372036854775808L;
			PokerGroupView.this.mNeedSync = false;

			PokerGroupView.this.setAnimationSelectedPosition(HorizontalPosInfo.ANI_SELECT_NEGATIVE_POSITION);
			PokerGroupView.this.checkFocus();
			PokerGroupView.this.requestLayout();
		}

		public void clearSavedState() {
			this.mInstanceState = null;
		}
	}
}
