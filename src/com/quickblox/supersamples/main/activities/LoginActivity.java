package com.quickblox.supersamples.main.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.flurry.android.FlurryAgent;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.definitions.Consts;
import com.quickblox.supersamples.main.helpers.AlertManager;
import com.quickblox.supersamples.main.helpers.ValidationManager;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.helpers.NumberToLetterConverter;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.RestResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements ActionResultDelegate {

    private ProgressBar queryProgressBar;
    private Facebook facebook;
    private JSONObject fbUserBody;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        facebook = new Facebook(getResources().getString(R.string.facebook_app_id));

        queryProgressBar = (ProgressBar) findViewById(R.id.queryLogin_progressBar);
    }

    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
        FlurryAgent.logEvent("run LoginActivity");

    }

    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    public void onClickButtons(View v) {
        switch (v.getId()) {
            // sign in
            case R.id.butNext:

                EditText editLogin = (EditText) findViewById(R.id.edit_login);
                EditText editPassword = (EditText) findViewById(R.id.edit_password);

                // validate fields
                int validationResult = ValidationManager.checkInputParameters(editLogin, editPassword);
                if (validationResult != ValidationManager.ALERT_OK) {
                    showDialog(validationResult);
                    return;
                }

                // sign in
                login(editLogin.getText().toString(), editPassword.getText().toString());

                break;

            // back
            case R.id.butBack:
                finish();

                break;

            // sign in via facebook
            case R.id.signInViaFacebook:


                queryProgressBar.setVisibility(View.VISIBLE);
                facebook.authorize(this, new String[]{}, new Facebook.DialogListener() {
                    @Override
                    public void onComplete(Bundle values) {

                        // get information about the currently logged in user
                        AsyncFacebookRunner asyncFacebookRunner = new AsyncFacebookRunner(facebook);
                        asyncFacebookRunner.request("me", new AsyncFacebookRunner.RequestListener() {
                            @Override
                            public void onComplete(String response, Object state) {

                                JSONObject user = null;
                                try {
                                    user = new JSONObject(response);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    return;
                                }

                                fbUserBody = user;

                                String id = null;
                                try {
                                    id = (String) user.get("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    return;
                                }

                                final String login = NumberToLetterConverter.convertNumbersToLetters(id);
                                final String password = String.valueOf(login.hashCode());

                                // sign in
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        login(login, password);
                                    }
                                });
                            }

                            @Override
                            public void onIOException(IOException e, Object state) {
                            }

                            @Override
                            public void onFileNotFoundException(FileNotFoundException e, Object state) {
                            }

                            @Override
                            public void onMalformedURLException(MalformedURLException e, Object state) {
                            }

                            @Override
                            public void onFacebookError(FacebookError e, Object state) {
                            }
                        });
                    }

                    @Override
                    public void onFacebookError(FacebookError error) {
                        queryProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(DialogError e) {
                        queryProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancel() {
                        queryProgressBar.setVisibility(View.INVISIBLE);
                    }
                });

                break;
        }
    }

    // sign in
    private void login(String login, String password) {
        // create entity
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("login", login));
        formparams.add(new BasicNameValuePair("password", password));
        formparams.add(new BasicNameValuePair("token", Store.getInstance().getAuthToken()));
        UrlEncodedFormEntity postEntity = null;
        try {
            postEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // make query
        Query.performQueryAsync(QueryMethod.Post, QBQueries.LOGIN_USER_QUERY, postEntity, null,
                this, QBQueries.QBQueryType.QBQueryTypeLoginUser);

        queryProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int alertMessage;

        switch (id) {
            case ValidationManager.ALERT_LOGIN:
                alertMessage = R.string.alert_login_blank;
                break;

            case ValidationManager.ALERT_PASSWORD:
                alertMessage = R.string.alert_password_blank;
                break;

            default:
                return null;
        }

        DialogInterface.OnClickListener doNothing = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        };

        builder.setMessage(alertMessage);
        builder.setCancelable(false);
        builder.setNeutralButton(R.string.ok, doNothing);

        return builder.create();
    }

    @Override
    public void completedWithResult(QBQueryType queryType, RestResponse response) {
        // no internet connection
        if (response == null) {
            queryProgressBar.setVisibility(View.GONE);
            AlertManager.showServerError(this, "Please check your internet connection");
            return;
        }

        switch (queryType) {
            case QBQueryTypeLoginUser:
                queryProgressBar.setVisibility(View.GONE);

                if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus202) {

                    // store current user
                    Store.getInstance().setCurrentUser(response.getBody());

                    Toast.makeText(this, "Login was successful!",
                            Toast.LENGTH_LONG).show();

                    finish();

                } else if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus401) {
                    if (fbUserBody != null) {
                        // Register FB user

                        String fullName = null;
                        try {
                            fullName = (String) fbUserBody.get("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                        String id = null;
                        try {
                            id = (String) fbUserBody.get("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                        String login = NumberToLetterConverter.convertNumbersToLetters(id);
                        String password = String.valueOf(login.hashCode());

                        List<NameValuePair> formparamsUser = new ArrayList<NameValuePair>();
                        formparamsUser.add(new BasicNameValuePair("user[full_name]", fullName));
                        formparamsUser.add(new BasicNameValuePair("user[login]", login));
                        formparamsUser.add(new BasicNameValuePair("user[password]", password));
                        formparamsUser.add(new BasicNameValuePair("user[facebook_id]", id));
                        formparamsUser.add(new BasicNameValuePair("token", Store.getInstance().getAuthToken()));

                        UrlEncodedFormEntity postEntityUser = null;
                        try {
                            postEntityUser = new UrlEncodedFormEntity(formparamsUser, "UTF-8");
                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                        }

                        // make query for creating a user
                        Query.performQueryAsync(QueryMethod.Post, QBQueries.CREATE_USER_QUERY, postEntityUser, null,
                                this, QBQueries.QBQueryType.QBQueryTypeCreateUser);

                        queryProgressBar.setVisibility(View.VISIBLE);

                        Log.w("LogibActivity", "Create FB User");

                    } else {
                        AlertManager.showServerError(this, "Unauthorized. Please check you login and password");
                    }

                    //validation error
                } else if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus422) {
                    String error = response.getBody().getChildren().get(0).getText();
                    AlertManager.showServerError(this, error);
                } else {
                    AlertManager.showServerError(this, "Oops!. Something went wrong");
                }
                break;

            case QBQueryTypeCreateUser:
                queryProgressBar.setVisibility(View.GONE);

                if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus201) {
                    String id = null;
                    try {
                        id = (String) fbUserBody.get("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }

                    String login = NumberToLetterConverter.convertNumbersToLetters(id);
                    String password = String.valueOf(login.hashCode());

                    // sign in
                    login(login, password);
                } else {
                    String error = response.getBody().getChildren().get(0).getText();
                    AlertManager.showServerError(this, error);
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
}