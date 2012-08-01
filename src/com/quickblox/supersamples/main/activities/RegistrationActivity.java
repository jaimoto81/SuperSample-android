package com.quickblox.supersamples.main.activities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.flurry.android.FlurryAgent;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.definitions.Consts;
import com.quickblox.supersamples.main.helpers.AlertManager;
import com.quickblox.supersamples.main.helpers.ValidationManager;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.RestResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements ActionResultDelegate{

	private EditText editFullName;
	private EditText editLogin;
	private EditText editPassword;
	private EditText editRetypePass;
	private ProgressBar queryProgressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_view);
		
		editFullName = (EditText) findViewById(R.id.edit_full_name);
		editLogin = (EditText) findViewById(R.id.edit_login);
		editPassword = (EditText) findViewById(R.id.edit_password);
		editRetypePass = (EditText) findViewById(R.id.edit_retype_pass);
		queryProgressBar = (ProgressBar)findViewById(R.id.queryRegistration_progressBar);
	}

	public void onStart()
	{
	    super.onStart();
	    FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
	    FlurryAgent.logEvent("run RegistrationActivity");
	    
	}
	
	public void onStop()
	{
	    super.onStop();
	    FlurryAgent.onEndSession(this);
	}
	
	public void onClickButtons(View v) {

		switch (v.getId()) {
			case R.id.butNext:
				
				// validate fields
				int validationResult = ValidationManager.checkInputParameters(editLogin, editPassword, 
						editRetypePass, editFullName);
				if(validationResult != ValidationManager.ALERT_OK){
					showDialog(validationResult);
					return;
				}
				
				queryProgressBar.setVisibility(View.VISIBLE);

				// Create User
				//
				// create entity
				List<NameValuePair> formparamsUser = new ArrayList<NameValuePair>();
				formparamsUser.add(new BasicNameValuePair("user[full_name]", editFullName.getText().toString()));

				formparamsUser.add(new BasicNameValuePair("user[login]", editLogin.getText().toString()));
				formparamsUser.add(new BasicNameValuePair("user[password]", editPassword.getText().toString()));
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

				break;

			// exit to the Main Activity
			case R.id.butBack:
				finish();
				break;
		}
	}

	// show dialog, if the fields is invalid 
	@Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		int alertMessage = -1;

		switch (id) {
		case ValidationManager.ALERT_FULLNAME:
			alertMessage = R.string.alert_fullname_blank;
			break;

		case ValidationManager.ALERT_LOGIN:
			alertMessage = R.string.alert_login_blank;
			break;

		case ValidationManager.ALERT_PASSWORD:
			alertMessage = R.string.alert_password_blank;
			break;

		case ValidationManager.ALERT_PASSWORD_REPEAT:
			alertMessage = R.string.alert_password_repeat;
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
		if(response == null){
			queryProgressBar.setVisibility(View.GONE);
			AlertManager.showServerError(this, "Please check your internet connection");
			return;
		}

		switch(queryType){
			case QBQueryTypeCreateUser:
				// OK
				if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus201) {
					
					Toast.makeText(this, "Registration successful. Please now sign in!",
							Toast.LENGTH_LONG).show();
						
					finish();

				// Validation error
				} else if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus422) {
					queryProgressBar.setVisibility(View.GONE);
					
					String error = response.getBody().getChildren().get(0).getText();
					AlertManager.showServerError(this, error);
				}
						
				break;
		}
	}
}