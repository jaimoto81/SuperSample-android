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

public class StartActivity extends Activity implements ActionResultDelegate {

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // QuickBlox application autorization
        Query.authorizeApp(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
        FlurryAgent.logEvent("run StartActivity");

    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    // QuickBlox queries callback
    @Override
    public void completedWithResult(QBQueryType queryType, RestResponse response) {
        // no internet connection
        if (response == null) {
            AlertManager.showServerError(this, "Please check your internet connection");
            return;
        }

        switch (queryType) {
            case QBQueryTypeGetAuthToken:
                if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus201) {
                    Store.getInstance().setAuthToken(response.getBody().findChild("token").getText());
               
                    // show main screen
                    Intent intent = new Intent();
		    intent.setClass(this, TabsActivity.class);
		    startActivity(intent);
		    finish();
                
                } else if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus422) {
                    String error = response.getBody().getChildren().get(0).getText();
                    AlertManager.showServerError(this, error);
                } else {
                    AlertManager.showServerError(this, "Oops! Something went wrong");
                }
                break;
        }
    }
}