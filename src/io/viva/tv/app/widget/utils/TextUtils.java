package io.viva.tv.app.widget.utils;

import android.graphics.Paint;
import android.util.Log;

public class TextUtils {
	private static final String TAG = "TextUtils";
	private static final Boolean DEBUG = Boolean.valueOf(false);

	public static final char[] mNarrowChar = { '!', '"', '\'', '(', ')', '*', ',', '.', '/', ':', ';', 'I', '[', '\\', ']', '^', '`', 'f', 'i', 'j', 'l', 'r', 't', '{', '|', '}' };

	public static final char[] mWideChar = { '%', '@', 'H', 'M', 'N', 'Q', 'W', 'm', 'w' };
	public static final String mNarrowTest = "I*r^/";
	public static final String mWideSetTest = "m@wWM";
	public static final String mNormalTest = "ABDOP";
	public static final String mNumberTest = "01234";
	public static final String mHanziTest = "云";
	private int mNarrowWidth;
	private int mWideWidth;
	private int mNormalWidth;
	private int mNumberWidth;
	private int mHanziWidth;
	private static final int NARROW = 1;
	private static final int WIDE = 2;
	private static final int NORMAL = 4;
	private static final int NUMBER = 8;
	private static final int HANZI = 16;
	private static final int ERROR = -1;
	private static final int MASK = 31;

	public void setCharsWidth(Paint paint) {
		setCharsWidth(paint, 31);
	}

	private void setCharsWidth(Paint paint, int type) {
		if (((type & 0x1) != 0) && (this.mNarrowWidth == 0)) {
			this.mNarrowWidth = ((int) (paint.measureText("I*r^/") / 5.0F));
			if (DEBUG.booleanValue())
				Log.d(TAG, "---- initial ---- mNarrowWidth = " + this.mNarrowWidth);
		}
		if (((type & 0x2) != 0) && (this.mWideWidth == 0)) {
			this.mWideWidth = ((int) (paint.measureText("m@wWM") / 5.0F));
			if (DEBUG.booleanValue())
				Log.d(TAG, "---- initial ---- mWideWidth = " + this.mWideWidth);
		}
		if (((type & 0x4) != 0) && (this.mNormalWidth == 0)) {
			this.mNormalWidth = ((int) (paint.measureText("ABDOP") / 5.0F));
			if (DEBUG.booleanValue())
				Log.d(TAG, "---- initial ---- mNormalWidth = " + this.mNormalWidth);
		}
		if (((type & 0x8) != 0) && (this.mNumberWidth == 0)) {
			this.mNumberWidth = ((int) (paint.measureText("01234") / 5.0F));
			if (DEBUG.booleanValue())
				Log.d(TAG, "---- initial ---- mNumberWidth = " + this.mNumberWidth);
		}
		if (((type & 0x10) != 0) && (this.mHanziWidth == 0)) {
			this.mHanziWidth = ((int) paint.measureText("云"));
			if (DEBUG.booleanValue())
				Log.d(TAG, "---- initial ---- mHanziWidth = " + this.mHanziWidth);
		}
	}

	private boolean isSubChar(char[] parent, Character child) {
		boolean isChild = false;

		int end = parent.length - 1;

		for (int i = 0; i < end; i++) {
			if (child.charValue() == parent[i]) {
				isChild = true;
				break;
			}
		}
		return isChild;
	}

	private int getCharType(Character ch) {
		if ((ch.charValue() >= '!') && (ch.charValue() <= '~')) {
			if (isSubChar(mNarrowChar, ch))
				return 1;
			if (isSubChar(mWideChar, ch))
				return 2;
			if ((ch.charValue() >= '0') && (ch.charValue() <= '9')) {
				return 8;
			}
			return 4;
		}
		if (((ch.charValue() >= 0) && (ch.charValue() < '!')) || ((ch.charValue() > '~') && (ch.charValue() <= 'þ'))) {
			return -1;
		}

		return 16;
	}

	public int getCharWidth(Character ch, int type) {
		int width = 0;
		switch (type) {
		case 1:
			return this.mNarrowWidth;
		case 2:
			return this.mWideWidth;
		case 4:
			return this.mNormalWidth;
		case 8:
			return this.mNumberWidth;
		case 16:
			return this.mHanziWidth;
		}

		return width;
	}

	public CharSequence ellipsis(Paint paint, CharSequence text, int width, int padding) {
		setCharsWidth(paint, getCharType(Character.valueOf('.')));
		setCharsWidth(paint, getCharType(Character.valueOf('云')));

		int desiredLength = width - padding - this.mNarrowWidth * 3;
		int start = desiredLength / this.mHanziWidth - 1;

		if (DEBUG.booleanValue())
			Log.d(TAG, "===== desiredLength = " + desiredLength + ", start = " + start);
		if (DEBUG.booleanValue())
			Log.d(TAG, "===== mNarrowWidth = " + this.mNarrowWidth + ", mHanziWidth = " + this.mHanziWidth);

		int startWidth = (int) paint.measureText(text, 0, start);
		int index = start;
		while (startWidth < desiredLength) {
			index++;
			Character ch = Character.valueOf(text.charAt(index));
			int type = getCharType(ch);
			setCharsWidth(paint, type);
			if (DEBUG.booleanValue())
				Log.d(TAG, "==== startWidth = " + startWidth);
			startWidth += getCharWidth(ch, type);
		}
		return text.subSequence(0, index) + "...";
	}
}
