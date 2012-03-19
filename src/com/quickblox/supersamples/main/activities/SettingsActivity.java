package com.quickblox.supersamples.main.activities;

import com.flurry.android.FlurryAgent;

import com.quickblox.supersamples.R;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.main.definitions.Consts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.RestResponse;

public class SettingsActivity extends Activity implements ActionResultDelegate {

    private Button signinLogoutButton;
    private Button signupButton;
    private ProgressBar logoutProgressBar;

    private static final int REQUEST_CODE_SIGN_IN = 101;
    private static final int REQUEST_CODE_SIGN_UP = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        signinLogoutButton = (Button) findViewById(R.id.signin_logout);
        signupButton = (Button) findViewById(R.id.signup);
        logoutProgressBar = (ProgressBar) findViewById(R.id.logout_progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
        FlurryAgent.logEvent("run SettingsActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Store.getInstance().getCurrentUser() == null) {
            signinLogoutButton.setText(R.string.signin);
            signupButton.setVisibility(View.VISIBLE);
        } else {
            signinLogoutButton.setText(R.string.logout);
            signupButton.setVisibility(View.INVISIBLE);
        }
    }

    public void onClickButtons(View v) {
        switch (v.getId()) {

            case R.id.signin_logout:

                // sign in
                if (Store.getInstance().getCurrentUser() == null) {
                    Intent intent = new Intent();
                    intent.setClass(this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SIGN_IN);

                // logout
                } else {
                    String query = QBQueries.LOGOUT_USER_QUERY + "?token=" + Store.getInstance().getAuthToken();

                    Query.performQueryAsync(QueryMethod.Get, query, null, null, this,
                            QBQueries.QBQueryType.QBQueryTypeLogoutUser);

                    logoutProgressBar.setVisibility(View.VISIBLE);
                }
                break;

            // remove the session
            case R.id.signup:
                Intent intent = new Intent();
                intent.setClass(this, RegistrationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SIGN_UP);

                break;
        }
    }

    public void completedWithResult(QBQueryType queryType, RestResponse response) {

        if (queryType == QBQueryType.QBQueryTypeLogoutUser) {
            logoutProgressBar.setVisibility(View.GONE);

            signinLogoutButton.setText(R.string.signin);
            signupButton.setVisibility(View.VISIBLE);
            Store.getInstance().setCurrentUser(null);

            Toast.makeText(this, "Logout was successful!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
