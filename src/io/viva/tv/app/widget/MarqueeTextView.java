package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MarqueeTextView extends TextView {
	public static final String TAG = "MarqueeTextView";
	private static final int MESSAGE_DRAW = 1;
	private float mTextLength = 0.0F;
	private float mViewWidth = 0.0F;
	private float mStep = 0.0F;
	private float mY = 0.0F;
	private float temp_view_plus_text_length = 0.0F;
	private float temp_view_plus_text_two_length = 0.0F;
	public boolean mIsStarting = false;
	private Paint mPaint = null;
	private String mText = "";
	private float mIntervel = 1.0F;
	private boolean mIsFirst = true;
	private boolean mMarqueeStart = false;
	private int mFirstDrawIntervel = 2000;
	private int mDrawIntervel = 20;
	private int mTextIntervel = 50;
	private int mTextColor = -1;
	boolean mIsInit = false;
	Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			MarqueeTextView.this.mMarqueeStart = true;
			MarqueeTextView.this.invalidate();
		}
	};

	public MarqueeTextView(Context paramContext) {
		super(paramContext);
	}

	public MarqueeTextView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public MarqueeTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public void onDraw(Canvas paramCanvas) {
		if ((!isStart()) || (this.temp_view_plus_text_length <= this.mViewWidth)) {
			init();
			if (this.temp_view_plus_text_length <= this.mViewWidth) {
				this.mPaint.setColor(this.mTextColor);
				paramCanvas.drawText(this.mText, Math.abs(this.temp_view_plus_text_length - this.mViewWidth) / 2.0F, this.mY, this.mPaint);
			} else {
				super.onDraw(paramCanvas);
			}
			return;
		}
		if (this.mIsFirst) {
			this.mIsFirst = false;
			drawText(paramCanvas, true);
			this.mHandler.sendEmptyMessageDelayed(1, this.mFirstDrawIntervel);
		} else if (this.mMarqueeStart) {
			drawText(paramCanvas, false);
			this.mHandler.sendEmptyMessageDelayed(1, this.mDrawIntervel);
		} else {
			drawText(paramCanvas, true);
		}
	}

	void setStart(boolean paramBoolean) {
		synchronized (this) {
			this.mIsStarting = paramBoolean;
		}
	}

	boolean isStart() {
		synchronized (this) {
			return this.mIsStarting;
		}
	}

	void drawText(Canvas paramCanvas, boolean paramBoolean) {
		paramCanvas.drawText(this.mText, this.temp_view_plus_text_length - this.mStep, this.mY, this.mPaint);
		if (this.mViewWidth + this.mStep - this.temp_view_plus_text_two_length > this.mTextIntervel)
			paramCanvas.drawText(this.mText, this.temp_view_plus_text_two_length - this.mStep + this.mTextIntervel, this.mY, this.mPaint);
		if (!paramBoolean) {
			this.mStep += this.mIntervel;
			float f = this.temp_view_plus_text_two_length - this.mStep + this.mTextIntervel;
			if (Math.abs(f) < this.mIntervel)
				this.mStep = (this.temp_view_plus_text_length + f);
		}
	}

	public void setTextColor(int paramInt) {
		this.mTextColor = paramInt;
	}

	public void setFirstDrawIntervel(int paramInt) {
		this.mFirstDrawIntervel = paramInt;
	}

	public void setDrawIntervel(int paramInt) {
		this.mDrawIntervel = paramInt;
	}

	public void setMarquee(int paramInt) {
		this.mIntervel = paramInt;
	}

	public void setText(String paramString) {
		stopMarquee();
		if (isStart())
			startMarquee();
		super.setText(paramString);
		this.mIsInit = false;
	}

	public void init() {
		if (this.mIsInit)
			return;
		this.mPaint = getPaint();
		this.mText = getText().toString();
		this.mTextLength = this.mPaint.measureText(this.mText);
		this.mViewWidth = getWidth();
		if (this.mViewWidth == 0.0F) {
			ViewGroup.LayoutParams localLayoutParams = getLayoutParams();
			if (localLayoutParams != null)
				this.mViewWidth = localLayoutParams.width;
		}
		this.mStep = this.mTextLength;
		this.temp_view_plus_text_length = this.mTextLength;
		this.temp_view_plus_text_two_length = (this.mTextLength * 2.0F);
		this.mY = (getTextSize() + getPaddingTop());
		this.mIsInit = true;
	}

	public Parcelable onSaveInstanceState() {
		Parcelable localParcelable = super.onSaveInstanceState();
		SavedState localSavedState = new SavedState(localParcelable);
		localSavedState.step = this.mStep;
		localSavedState.isStarting = isStart();
		return localSavedState;
	}

	public void onRestoreInstanceState(Parcelable paramParcelable) {
		if (!(paramParcelable instanceof SavedState)) {
			super.onRestoreInstanceState(paramParcelable);
			return;
		}
		SavedState localSavedState = (SavedState) paramParcelable;
		super.onRestoreInstanceState(localSavedState.getSuperState());
		this.mStep = localSavedState.step;
		setStart(localSavedState.isStarting);
		this.mTextColor = localSavedState.textColor;
	}

	public void startMarquee() {
		setStart(true);
		init();
		invalidate();
	}

	public void stopMarquee() {
		setStart(false);
		this.mMarqueeStart = false;
		this.mIsFirst = true;
		this.mStep = this.mTextLength;
		this.mHandler.removeMessages(1);
		invalidate();
	}

	public static class SavedState extends View.BaseSavedState {
		public boolean isStarting = false;
		public float step = 0.0F;
		public int textColor = -1;
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator() {
			public MarqueeTextView.SavedState[] newArray(int paramAnonymousInt) {
				return new MarqueeTextView.SavedState[paramAnonymousInt];
			}

			public MarqueeTextView.SavedState createFromParcel(Parcel paramAnonymousParcel) {
				return new MarqueeTextView.SavedState(paramAnonymousParcel);
			}
		};

		SavedState(Parcelable paramParcelable) {
			super(paramParcelable);
		}

		public void writeToParcel(Parcel paramParcel, int paramInt) {
			super.writeToParcel(paramParcel, paramInt);
			paramParcel.writeBooleanArray(new boolean[] { this.isStarting });
			paramParcel.writeFloat(this.step);
			paramParcel.writeInt(this.textColor);
		}

		private SavedState(Parcel paramParcel) {
			super(paramParcel);
			boolean[] arrayOfBoolean = new boolean[1];
			paramParcel.readBooleanArray(arrayOfBoolean);
			if ((arrayOfBoolean != null) && (arrayOfBoolean.length > 0))
				this.isStarting = arrayOfBoolean[0];
			this.step = paramParcel.readFloat();
			this.textColor = paramParcel.readInt();
		}
	}
}