package com.quickblox.supersamples.main.activities;


import com.flurry.android.FlurryAgent;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.definitions.Consts;
import com.quickblox.supersamples.main.helpers.AlertManager;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.RestResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends Activity implements ActionResultDelegate{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// auth app
		Query.authorizeApp(this);
	}

	public void onStart()
	{
	    super.onStart();
	    FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
	    FlurryAgent.logEvent("run StartActivity");
	    
	}
	
	public void onStop()
	{
	    super.onStop();
	    FlurryAgent.onEndSession(this);
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
		}
	}

	@Override
	public void completedWithResult(QBQueryType queryType, RestResponse response) {
		// no internet connection
		if(response == null){
			AlertManager.showServerError(this, "Please check your internet connection");
			return;
		}
				
		switch (queryType){
		case QBQueryTypeGetAuthToken:
			if(response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus201){
				Store.getInstance().setAuthToken(response.getBody().findChild("token").getText());
			}else if(response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus422){
				String error = response.getBody().getChildren().get(0).getText();
				AlertManager.showServerError(this, error);
			}else{
				AlertManager.showServerError(this, "Oops! Something went wrong");
			}
			break;
		}
	}
}