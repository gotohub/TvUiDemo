package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberPicker extends LinearLayout {
	public static final Formatter TWO_DIGIT_FORMATTER = new Formatter() {
		final StringBuilder mBuilder = new StringBuilder();
		final java.util.Formatter mFmt = new java.util.Formatter(this.mBuilder);
		final Object[] mArgs = new Object[1];

		public String toString(int value) {
			this.mArgs[0] = Integer.valueOf(value);
			this.mBuilder.delete(0, this.mBuilder.length());
			this.mFmt.format("%02d", this.mArgs);
			return this.mFmt.toString();
		}
	};
	private final Handler mHandler;
	private final Runnable mRunnable = new Runnable() {
		public void run() {
			if (NumberPicker.this.mIncrement) {
				NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent + 1);
				NumberPicker.this.mHandler.postDelayed(this, NumberPicker.this.mSpeed);
			} else if (NumberPicker.this.mDecrement) {
				NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent - 1);
				NumberPicker.this.mHandler.postDelayed(this, NumberPicker.this.mSpeed);
			}
		}
	};
	private final TextView mText;
	private final InputFilter mNumberInputFilter;
	private String[] mDisplayedValues;
	private int mStart;
	private int mEnd;
	private int mCurrent;
	private int mPrevious;
	private OnChangedListener mListener;
	private Formatter mFormatter;
	private long mSpeed = 300L;
	private boolean mIncrement;
	private boolean mDecrement;
	private static final char[] DIGIT_CHARACTERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	private NumberPickerButton mIncrementButton;
	private NumberPickerButton mDecrementButton;

	public NumberPicker(Context context) {
		this(context, null);
	}

	public NumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(1);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(2114453525, this, true);
		this.mHandler = new Handler();

		View.OnClickListener clickListener = new View.OnClickListener() {
			public void onClick(View v) {
				NumberPicker.this.validateInput(NumberPicker.this.mText);
				if (!NumberPicker.this.mText.hasFocus())
					NumberPicker.this.mText.requestFocus();
				NumberPicker.this.mText.invalidate();

				if (2114584630 == v.getId())
					NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent + 1);
				else if (2114584632 == v.getId())
					NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent - 1);
			}
		};
		View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					NumberPicker.this.validateInput(v);
				}
			}
		};
		View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				NumberPicker.this.mText.clearFocus();

				if (2114584630 == v.getId()) {
					NumberPicker.this.mIncrement = true;
					NumberPicker.this.mHandler.post(NumberPicker.this.mRunnable);
				} else if (2114584632 == v.getId()) {
					NumberPicker.this.mDecrement = true;
					NumberPicker.this.mHandler.post(NumberPicker.this.mRunnable);
				}
				return true;
			}
		};
		InputFilter inputFilter = new NumberPickerInputFilter();
		this.mNumberInputFilter = new NumberRangeKeyListener();
		this.mIncrementButton = ((NumberPickerButton) findViewById(2114584630));
		this.mIncrementButton.setOnClickListener(clickListener);
		this.mIncrementButton.setOnLongClickListener(longClickListener);
		this.mIncrementButton.setNumberPicker(this);
		this.mIncrementButton.setFocusable(false);

		this.mDecrementButton = ((NumberPickerButton) findViewById(2114584632));
		this.mDecrementButton.setOnClickListener(clickListener);
		this.mDecrementButton.setOnLongClickListener(longClickListener);
		this.mDecrementButton.setNumberPicker(this);
		this.mDecrementButton.setFocusable(false);

		setFocusable(false);

		this.mText = ((TextView) findViewById(2114584631));
		this.mText.setOnFocusChangeListener(focusListener);
		this.mText.setFilters(new InputFilter[] { inputFilter });
		this.mText.setFocusable(true);
		this.mText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					Log.d("Test", "---- has focused");
					NumberPicker.this.mIncrementButton.setSelected(true);
					NumberPicker.this.mDecrementButton.setSelected(true);
				} else {
					Log.d("Test", "---- has no focused");
					NumberPicker.this.mIncrementButton.setSelected(false);
					NumberPicker.this.mDecrementButton.setSelected(false);
				}
				int[] states = NumberPicker.this.mIncrementButton.getDrawableState();
				for (int i = 0; i < states.length; i++)
					Log.d("Test", "-- button states[" + i + "] = " + states[i]);
			}
		});
		this.mText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("Test", "==================keyCode = " + keyCode);
				int action = event.getAction();
				if (action == 0) {
					switch (keyCode) {
					case 19:
						NumberPicker.this.validateInput(NumberPicker.this.mText);
						NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent + 1);
						break;
					case 20:
						NumberPicker.this.validateInput(NumberPicker.this.mText);
						NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent - 1);
						break;
					case 7:
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
						NumberPicker.this.validateCurrentView(keyCode - 7);
					case 17:
					case 18:
					}
				}
				return false;
			}
		});
		int[] states = this.mIncrementButton.getDrawableState();
		for (int i = 0; i < states.length; i++) {
			Log.d("Test", "-- button states[" + i + "] = " + states[i]);
		}

		if (!isEnabled())
			setEnabled(false);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.mIncrementButton.setEnabled(enabled);
		this.mDecrementButton.setEnabled(enabled);
		this.mText.setEnabled(enabled);
	}

	public void setOnChangeListener(OnChangedListener listener) {
		this.mListener = listener;
	}

	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		Log.d("Test", "NumberPicker onFoucusChanged! gainFocus = " + gainFocus + " direction = " + direction);
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	public View focusSearch(View focused, int direction) {
		Log.d("Test", "NumberPicker focusSearch");
		if ((direction == 33) || (direction == 130)) {
			Log.d("Test", "Focus direction is up or down, search nothing");
			int[] states2 = this.mText.getDrawableState();
			for (int i = 0; i < states2.length; i++) {
				Log.d("Test", "-- states2[" + i + "] = " + states2[i]);
			}
			return null;
		}
		return super.focusSearch(focused, direction);
	}

	public void setFormatter(Formatter formatter) {
		this.mFormatter = formatter;
	}

	public void setRange(int start, int end) {
		setRange(start, end, null);
	}

	public void setRange(int start, int end, int defvalues) {
		this.mStart = start;
		this.mEnd = end;
		this.mCurrent = defvalues;
		updateView();
	}

	public void setRange(int start, int end, String[] displayedValues) {
		this.mDisplayedValues = displayedValues;
		this.mStart = start;
		this.mEnd = end;
		this.mCurrent = start;
		updateView();

		if (displayedValues != null) {
			this.mText.setRawInputType(524289);
		}
	}

	public void setCurrent(int current) {
		if ((current < this.mStart) || (current > this.mEnd)) {
			throw new IllegalArgumentException("current should be >= start and <= end");
		}

		this.mCurrent = current;
		updateView();
	}

	public void setSpeed(long speed) {
		this.mSpeed = speed;
	}

	private String formatNumber(int value) {
		return this.mFormatter != null ? this.mFormatter.toString(value) : String.valueOf(value);
	}

	protected void changeCurrent(int current) {
		if (current > this.mEnd)
			current = this.mStart;
		else if (current < this.mStart) {
			current = this.mEnd;
		}
		this.mPrevious = this.mCurrent;
		this.mCurrent = current;
		notifyChange();
		updateView();
	}

	private void notifyChange() {
		if (this.mListener != null)
			this.mListener.onChanged(this, this.mPrevious, this.mCurrent);
	}

	private void updateView() {
		if (this.mDisplayedValues == null)
			this.mText.setText(formatNumber(this.mCurrent));
		else
			this.mText.setText(this.mDisplayedValues[(this.mCurrent - this.mStart)]);
	}

	private void validateCurrentView(CharSequence str) {
		int val = getSelectedPos(str.toString());
		if ((val >= this.mStart) && (val <= this.mEnd) && (this.mCurrent != val)) {
			this.mPrevious = this.mCurrent;
			this.mCurrent = val;
			notifyChange();
		}

		updateView();
	}

	private void validateCurrentView(int val) {
		if ((val >= this.mStart) && (val <= this.mEnd) && (this.mCurrent != val)) {
			this.mPrevious = this.mCurrent;
			this.mCurrent = val;
			notifyChange();
		}

		updateView();
	}

	private void validateInput(View v) {
		String str = String.valueOf(((TextView) v).getText());
		if ("".equals(str)) {
			updateView();
		} else {
			validateCurrentView(str);
		}
	}

	public void cancelIncrement() {
		this.mIncrement = false;
	}

	public void cancelDecrement() {
		this.mDecrement = false;
	}

	private int getSelectedPos(String str) {
		if (this.mDisplayedValues == null) {
			try {
				return Integer.parseInt(str);
			} catch (NumberFormatException e) {
			}
		} else {
			for (int i = 0; i < this.mDisplayedValues.length; i++) {
				str = str.toLowerCase();
				if (this.mDisplayedValues[i].toLowerCase().startsWith(str)) {
					return this.mStart + i;
				}

			}

			try {
				return Integer.parseInt(str);
			} catch (NumberFormatException e) {
			}
		}
		return this.mStart;
	}

	public int getCurrent() {
		return this.mCurrent;
	}

	protected int getEndRange() {
		return this.mEnd;
	}

	protected int getBeginRange() {
		return this.mStart;
	}

	private class NumberRangeKeyListener extends NumberKeyListener {
		private NumberRangeKeyListener() {
		}

		public int getInputType() {
			return 2;
		}

		protected char[] getAcceptedChars() {
			return NumberPicker.DIGIT_CHARACTERS;
		}

		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			CharSequence filtered = super.filter(source, start, end, dest, dstart, dend);
			if (filtered == null) {
				filtered = source.subSequence(start, end);
			}

			String result = String.valueOf(dest.subSequence(0, dstart)) + filtered + dest.subSequence(dend, dest.length());

			if ("".equals(result)) {
				return result;
			}
			int val = NumberPicker.this.getSelectedPos(result);

			if (val > NumberPicker.this.mEnd) {
				return "";
			}
			return filtered;
		}
	}

	private class NumberPickerInputFilter implements InputFilter {
		private NumberPickerInputFilter() {
		}

		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (NumberPicker.this.mDisplayedValues == null) {
				return NumberPicker.this.mNumberInputFilter.filter(source, start, end, dest, dstart, dend);
			}
			CharSequence filtered = String.valueOf(source.subSequence(start, end));
			String result = String.valueOf(dest.subSequence(0, dstart)) + filtered + dest.subSequence(dend, dest.length());

			String str = String.valueOf(result).toLowerCase();
			for (String val : NumberPicker.this.mDisplayedValues) {
				val = val.toLowerCase();
				if (val.startsWith(str)) {
					return filtered;
				}
			}
			return "";
		}
	}

	public static abstract interface Formatter {
		public abstract String toString(int paramInt);
	}

	public static abstract interface OnChangedListener {
		public abstract void onChanged(NumberPicker paramNumberPicker, int paramInt1, int paramInt2);
	}
}
