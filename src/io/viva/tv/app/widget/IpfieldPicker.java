package io.viva.tv.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

class IpfieldPicker extends FrameLayout {
	private static final OnNumberChangedListener NO_OP_CHANGE_LISTENER = new OnNumberChangedListener() {
		public void onNumberChanged(IpfieldPicker ipfield, int hundred, int ten, int unit) {
		}
	};
	private boolean mIs2HundredBegin;
	private final NumberPicker mHundredSpinner;
	private final NumberPicker mTenSpinner;
	private final NumberPicker mUnitSpinner;
	private OnNumberChangedListener mOnNumberChangedListener;

	public IpfieldPicker(Context context) {
		this(context, null);
	}

	public IpfieldPicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IpfieldPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(2114453511, this, true);
		setFocusable(false);

		this.mHundredSpinner = ((NumberPicker) findViewById(2114584586));
		this.mHundredSpinner.setRange(0, 2);
		this.mHundredSpinner.setOnChangeListener(new NumberPicker.OnChangedListener() {
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				if (newVal == 2) {
					int ten = IpfieldPicker.this.mTenSpinner.getCurrent();
					IpfieldPicker.this.mTenSpinner.setRange(0, 5, ten % 6);
					if (IpfieldPicker.this.mTenSpinner.getCurrent() == 5) {
						int unit = IpfieldPicker.this.mUnitSpinner.getCurrent();
						IpfieldPicker.this.mUnitSpinner.setRange(0, 5, unit % 6);
					}
				} else {
					int ten = IpfieldPicker.this.mTenSpinner.getCurrent();
					int unit = IpfieldPicker.this.mUnitSpinner.getCurrent();
					IpfieldPicker.this.mTenSpinner.setRange(0, 9, ten);
					IpfieldPicker.this.mUnitSpinner.setRange(0, 9, unit);
				}
			}
		});
		this.mTenSpinner = ((NumberPicker) findViewById(2114584587));
		this.mTenSpinner.setRange(0, 9);
		this.mTenSpinner.setOnChangeListener(new NumberPicker.OnChangedListener() {
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				IpfieldPicker.this.updateHundredFlag();
				if (IpfieldPicker.this.mIs2HundredBegin) {
					IpfieldPicker.this.mTenSpinner.setRange(0, 5, IpfieldPicker.this.mTenSpinner.getCurrent() % 6);
					if (newVal == 5)
						IpfieldPicker.this.mUnitSpinner.setRange(0, 5, IpfieldPicker.this.mUnitSpinner.getCurrent() % 6);
					else {
						IpfieldPicker.this.mUnitSpinner.setRange(0, 9, IpfieldPicker.this.mUnitSpinner.getCurrent());
					}
				}

				IpfieldPicker.this.onNumberChanged();
			}
		});
		this.mUnitSpinner = ((NumberPicker) findViewById(2114584588));
		this.mUnitSpinner.setRange(0, 9);
		this.mUnitSpinner.setOnChangeListener(new NumberPicker.OnChangedListener() {
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				IpfieldPicker.this.updateHundredFlag();
				if ((IpfieldPicker.this.mIs2HundredBegin) && (IpfieldPicker.this.mTenSpinner.getCurrent() == 5)) {
					IpfieldPicker.this.mUnitSpinner.setRange(0, 5, newVal % 6);
				}
				IpfieldPicker.this.onNumberChanged();
			}
		});
		setOnNumberChangedListener(NO_OP_CHANGE_LISTENER);
	}

	private void updateHundredFlag() {
		this.mIs2HundredBegin = (this.mHundredSpinner.getCurrent() == 2);
	}

	private void onNumberChanged() {
		if (this.mOnNumberChangedListener != null)
			this.mOnNumberChangedListener.onNumberChanged(this, getCurrentHundred(), getCurrentTen(), getCurrentUnit());
	}

	public int getCurrentHundred() {
		return this.mHundredSpinner.getCurrent();
	}

	public int getCurrentTen() {
		return this.mTenSpinner.getCurrent();
	}

	public int getCurrentUnit() {
		return this.mUnitSpinner.getCurrent();
	}

	public int getCurrentTotal() {
		return this.mHundredSpinner.getCurrent() * 100 + this.mTenSpinner.getCurrent() * 10 + this.mUnitSpinner.getCurrent();
	}

	public void setOnNumberChangedListener(OnNumberChangedListener onNumberChangedListener) {
		this.mOnNumberChangedListener = onNumberChangedListener;
	}

	public void setCurrentTotal(int total) {
		if ((total < 0) || (total > 255)) {
			throw new IllegalArgumentException("current should be >= 0 and <= 255");
		}

		this.mHundredSpinner.setCurrent(total / 100);
		this.mTenSpinner.setCurrent(total / 10 % 10);
		this.mUnitSpinner.setCurrent(total % 10);
	}

	public void setCurrentNumber(int hundred, int ten, int unit) {
		if (2 == hundred) {
			ten %= 6;
			if (5 == ten) {
				unit %= 6;
			}
		}
		this.mHundredSpinner.setCurrent(hundred);
		this.mTenSpinner.setCurrent(ten);
		this.mUnitSpinner.setCurrent(unit);
	}

	public static abstract interface OnNumberChangedListener {
		public abstract void onNumberChanged(IpfieldPicker paramIpfieldPicker, int paramInt1, int paramInt2, int paramInt3);
	}
}
