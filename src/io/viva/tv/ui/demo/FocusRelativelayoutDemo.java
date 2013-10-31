package io.viva.tv.ui.demo;

import io.viva.tv.app.widget.FocusedBasePositionManager;
import io.viva.tv.app.widget.FocusedRelativeLayout;
import io.viva.tv.app.widget.ReflectImageView;
import android.app.Activity;
import android.os.Bundle;

public class FocusRelativelayoutDemo extends Activity {

	private FocusedRelativeLayout layout;
	private float mScaleValue = 1.1f;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_layout);

		layout = (FocusedRelativeLayout) findViewById(R.id.demo_focuse_layout);

		// 设置放大比例
		layout.setItemScaleValue(mScaleValue, mScaleValue);

		// 设置焦点框图片
		layout.setFocusResId(R.drawable.tui_bg_focus);
		layout.setFocusShadowResId(R.drawable.tui_grid_focus);

		// 设置动画类型
		layout.setFocusMode(FocusedBasePositionManager.FOCUS_STATIC_DRAW);

		// 设置帧率
		layout.setFrameRate(6);

		//设置横向滑动的类型 HORIZONTAL_SINGEL：屏幕外的单个view滑动出来 HORIZONTAL_FULL是屏幕外的view滑动到RelativeLayout的左边界
		layout.setHorizontalMode(FocusedRelativeLayout.HORIZONTAL_FULL);
		layout.setScaleMode(FocusedBasePositionManager.SCALED_FIXED_COEF);
		// 设置页面横向滚动后右边的padding
//		layout.setViewRight(20);

		// 设置padding
		layout.setManualPadding(0, 0, 0, 0);

		ReflectImageView demoImage0 = (ReflectImageView) findViewById(R.id.demo_image1);
		demoImage0.setImageResource(R.drawable.image2, 80);
		demoImage0.setText("image1");

		ReflectImageView demoImage1 = (ReflectImageView) findViewById(R.id.demo_image2);
		demoImage1.setText("image2");
		demoImage1.setImageResource(R.drawable.image2);

		ReflectImageView demoImage2 = (ReflectImageView) findViewById(R.id.demo_image3);
		demoImage2.setText("image3");
		demoImage2.setImageResource(R.drawable.image2, 80);

		ReflectImageView demoImage3 = (ReflectImageView) findViewById(R.id.demo_image4);
		demoImage3.setText("image4");
		demoImage3.setImageResource(R.drawable.image2, 80);

		ReflectImageView demoImage4 = (ReflectImageView) findViewById(R.id.demo_image5);
		demoImage4.setText("image5");
		demoImage4.setImageResource(R.drawable.image2);

		ReflectImageView demoImage5 = (ReflectImageView) findViewById(R.id.demo_image6);
		demoImage5.setText("image6");
		demoImage5.setImageResource(R.drawable.image2, 80);

		ReflectImageView demoImage6 = (ReflectImageView) findViewById(R.id.demo_image7);
		demoImage6.setText("image7");
		demoImage6.setImageResource(R.drawable.image2);

		ReflectImageView demoImage7 = (ReflectImageView) findViewById(R.id.demo_image8);
		demoImage7.setText("image8");
		demoImage7.setImageResource(R.drawable.image2, 80);

		ReflectImageView demoImage8 = (ReflectImageView) findViewById(R.id.demo_image9);
		demoImage8.setText("image9");
		demoImage8.setImageResource(R.drawable.image2, 80);

		ReflectImageView demoImage9 = (ReflectImageView) findViewById(R.id.demo_image10);
		demoImage9.setText("image10");
		demoImage9.setImageResource(R.drawable.image2, 80);

		ReflectImageView demoImage10 = (ReflectImageView) findViewById(R.id.demo_image11);
		demoImage10.setText("image11");
		demoImage10.setImageResource(R.drawable.image2);

		ReflectImageView demoImage11 = (ReflectImageView) findViewById(R.id.demo_image12);
		demoImage11.setText("image12");
		demoImage11.setImageResource(R.drawable.image2, 80);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (layout != null) {
			layout.release();
		}
	}
}
