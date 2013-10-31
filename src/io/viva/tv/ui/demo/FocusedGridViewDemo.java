package io.viva.tv.ui.demo;

import io.viva.tv.app.widget.FocusedBasePositionManager;
import io.viva.tv.app.widget.FocusedGridView;
import io.viva.tv.ui.demo.adapter.FocusedGridViewAdapter;
import io.viva.tv.ui.demo.view.MyLinearLayout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FocusedGridViewDemo extends Activity {
	
	FocusedGridView mGridView;
	FocusedGridViewAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.focusedgridview);
		
		mGridView = (FocusedGridView) findViewById(R.id.gridview);
		mGridView.setFrameRate(6);
		mGridView.setItemScaleValue(1.1f, 1.1f);
		mGridView.setFocusResId(R.drawable.tui_bg_focus);
		mGridView.setFocusShadowResId(R.drawable.tui_grid_focus);
		mGridView.setFocusViewId(R.id.grid_item);
		mAdapter = new FocusedGridViewAdapter(this, 156, 218);
		
		
		//设置带headerview
		Intent intent = getIntent();
		boolean hasCoverFlow = intent.getBooleanExtra("hasCoverFlow", false);
		if (hasCoverFlow) {
			mGridView.setHeaderPosition(0);
		}
		mAdapter.setHasCoverFlow(hasCoverFlow);
		
		mGridView.setAdapter(mAdapter);
		mGridView.setSelected(true);
		mGridView.setOnItemSelectedListener(new FocusedBasePositionManager.FocusItemSelectedListener() {

			@Override
			public void onItemSelected(View v, int paramInt, boolean paramBoolean, View paramView2) {
				if (paramBoolean) {
					if (v instanceof MyLinearLayout) {
						mAdapter.setHeaderViewFocus(true);
					}
				} else {
					if (v instanceof MyLinearLayout) {
						mAdapter.setHeaderViewFocus(false);
					}
				}
			}
		});
	}
}
