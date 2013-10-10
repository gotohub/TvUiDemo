package io.viva.tv.ui.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {

	public MyLinearLayout(Context context) {
		super(context);
	}

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		MediaCoverFlow  mMediaCoverFlow = (MediaCoverFlow) findViewById(R.id.grid_item);
		if (mMediaCoverFlow == null) {
			return super.onKeyDown(keyCode, event);
		}
//		 switch (keyCode) {
//         case KeyEvent.KEYCODE_DPAD_LEFT:
//         case KeyEvent.KEYCODE_DPAD_RIGHT:
//            return true;
//         default:
//            break;
//         }
		return mMediaCoverFlow.onKeyDown(keyCode, event);
	}*/
	
	
}
