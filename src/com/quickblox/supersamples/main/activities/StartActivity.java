package com.quickblox.supersamples.main.activities;


import com.quickblox.supersamples.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	// Click on the buttons
	public void buttonOnClick(View v) {
		Intent intent = new Intent();

		switch (v.getId()) {
		case R.id.butLogin:
 
			intent.setClass(this, LoginActivity.class);

			startActivity(intent);
			finish();

			break;
		case R.id.butRegistration:

			intent.setClass(this, RegistrationActivity.class);

			startActivity(intent);
			finish();

			break;
		case R.id.butProcede:

			intent.setClass(this, TabsActivity.class);

			startActivity(intent);
			finish();

			break;
		}
	}
}