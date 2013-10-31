package io.viva.tv.ui.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

	private String TAG = "uidemo";
	private Button mDemo1Btn;
	private Button mDemo2Btn;
	private Button mDemo3Btn;
	private Button mDemo4Btn;
	private Button mDemo5Btn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDemo1Btn = (Button) findViewById(R.id.demo1);
		mDemo2Btn = (Button) findViewById(R.id.demo2);
		mDemo3Btn = (Button) findViewById(R.id.demo3);
		mDemo4Btn = (Button) findViewById(R.id.demo4);
		mDemo5Btn = (Button) findViewById(R.id.demo5);

		mDemo1Btn.setOnClickListener(this);
		mDemo2Btn.setOnClickListener(this);
		mDemo3Btn.setOnClickListener(this);
		mDemo4Btn.setOnClickListener(this);
		mDemo5Btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.demo1:
			startActivity(new Intent(MainActivity.this, FocusRelativelayoutDemo.class));
			break;
		case R.id.demo2:
			startActivity(new Intent(MainActivity.this, PageFocusRelativelayoutDemo.class));
			break;
		case R.id.demo3:
			startActivity(new Intent(MainActivity.this, PageFocusGridViewDemo.class));
			break;
		case R.id.demo4:
			intent = new Intent(MainActivity.this, FocusedGridViewDemo.class);
			intent.putExtra("hasCoverFlow", false);
			startActivity(intent);
			break;
		case R.id.demo5:
			intent = new Intent(MainActivity.this, FocusedGridViewDemo.class);
			intent.putExtra("hasCoverFlow", true);
			startActivity(intent);
			break;
		}
	}
}
