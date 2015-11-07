package com.lollipoper.tagimage;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.lollipoer.tagimage.R;
import com.lollipoper.tagimage.view.TAGImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TAGImageView tagImageView = (TAGImageView) findViewById(R.id.tag_image_view);
		// tagImageView.setTagName("你够了");
		// tagImageView.setTagTextColor(Color.BLACK);
		// tagImageView.setTagBGColor(Color.BLUE);
		// tagImageView.setTagDisY(500);
		// tagImageView.setTagTextSize(200);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
