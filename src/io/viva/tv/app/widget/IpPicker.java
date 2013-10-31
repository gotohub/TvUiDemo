package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public class IpPicker extends FrameLayout {
	private final IpfieldPicker mFirstField;
	private final IpfieldPicker mSecondField;
	private final IpfieldPicker mThirdField;
	private final IpfieldPicker mLastField;

	public IpPicker(Context context) {
		this(context, null);
		setFocusable(false);
	}

	public IpPicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IpPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(2114453510, this, true);

		this.mFirstField = ((IpfieldPicker) findViewById(2114584581));
		this.mSecondField = ((IpfieldPicker) findViewById(2114584582));
		this.mThirdField = ((IpfieldPicker) findViewById(2114584583));
		this.mLastField = ((IpfieldPicker) findViewById(2114584584));
	}

	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		Log.d("Test", "IpPicker onFoucusChanged");
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	public View focusSearch(View focused, int direction) {
		Log.d("Test", "IpPicker focusSearch");
		return super.focusSearch(focused, direction);
	}

	public void setIp(String strip) {
		int[] ips = new int[4];
		int position1 = strip.indexOf(".");
		int position2 = strip.indexOf(".", position1 + 1);
		int position3 = strip.indexOf(".", position2 + 1);

		Log.d("Test", "====== position1 = " + position1 + "positon2 = " + position2 + "positon3 = " + position3);

		ips[0] = Integer.parseInt(strip.substring(0, position1));
		ips[1] = Integer.parseInt(strip.substring(position1 + 1, position2));
		ips[2] = Integer.parseInt(strip.substring(position2 + 1, position3));
		ips[3] = Integer.parseInt(strip.substring(position3 + 1));

		Log.d("Test", "ip[0] = " + ips[0] + "ip[1] = " + ips[1] + "ip[2] = " + ips[2] + "ip[3] = " + ips[3]);

		this.mFirstField.setCurrentTotal(ips[0]);
		this.mSecondField.setCurrentTotal(ips[1]);
		this.mThirdField.setCurrentTotal(ips[2]);
		this.mLastField.setCurrentTotal(ips[3]);
	}

	public void setIp(int first, int second, int third, int last) {
		this.mFirstField.setCurrentTotal(first);
		this.mSecondField.setCurrentTotal(second);
		this.mThirdField.setCurrentTotal(third);
		this.mLastField.setCurrentTotal(last);
	}

	public String getIp() {
		String ip1 = String.valueOf(this.mFirstField.getCurrentTotal());
		String ip2 = String.valueOf(this.mSecondField.getCurrentTotal());
		String ip3 = String.valueOf(this.mThirdField.getCurrentTotal());
		String ip4 = String.valueOf(this.mLastField.getCurrentTotal());
		return ip1 + "." + ip2 + "." + ip3 + "." + ip4;
	}
}
