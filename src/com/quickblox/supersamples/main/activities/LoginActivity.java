package com.quickblox.supersamples.main.activities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.flurry.android.FlurryAgent;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.helpers.AlertManager;
import com.quickblox.supersamples.main.helpers.ValidationManager;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.RestResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginActivity extends Activity implements ActionResultDelegate{

	private ProgressBar queryProgressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_view);
		
		queryProgressBar = (ProgressBar)findViewById(R.id.queryLogin_progressBar);
	}

	public void onStart()
	{
	    super.onStart();
	    FlurryAgent.onStartSession(this, "B6G7VFD3ZY767YUJA1J2");
	    FlurryAgent.logEvent("run LoginActivity");

	}
	
	public void onStop()
	{
	    super.onStop();
	    FlurryAgent.onEndSession(this);
	}
	
	public void onClickButtons(View v) {
		switch (v.getId()) {
			case R.id.butNext:

				EditText editLogin = (EditText) findViewById(R.id.edit_login);
				EditText editPassword = (EditText) findViewById(R.id.edit_password);

				// validate fields
				int validationResult = ValidationManager.checkInputParameters(editLogin, editPassword);
				if(validationResult != ValidationManager.ALERT_OK){
					showDialog(validationResult);
					return;
				}

				queryProgressBar.setVisibility(View.VISIBLE);
				
				// create entity
				List<NameValuePair> formparams = new ArrayList<NameValuePair>();
				formparams.add(new BasicNameValuePair("owner_id", QBQueries.OWNER_ID));
				formparams.add(new BasicNameValuePair("login", editLogin.getText().toString()));
				formparams.add(new BasicNameValuePair("password", editPassword.getText().toString()));
				UrlEncodedFormEntity postEntity = null;
				try {
					postEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				// make query
				Query.makeQueryAsync(QueryMethod.Post, QBQueries.LOGIN_USER_QUERY, postEntity, null, 
						this, QBQueries.QBQueryType.QBQueryTypeLoginUser);

				break;
			case R.id.butBack:

				Intent intent = new Intent();
				intent.setClass(this, StartActivity.class);
				startActivity(intent);
				finish();

				break;
		}
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
		if(response == null){
			queryProgressBar.setVisibility(View.GONE);
			AlertManager.showServerError(this, "Please check your internet connection");
			return;
		}
				
		switch(queryType){
			case QBQueryTypeLoginUser:
				if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus202) {
					
					// store current user
					Store.getInstance().setCurrentUser(response.getBody());
<<<<<<< HEAD
					
					// show main activity
				    Intent intent = new Intent();
					intent.setClass(this, TabsActivity.class);
					startActivity(intent);
					finish();
	
=======
	
					// get GeoUser
					// make query
					String geouserId = response.getBody().findChild("external-user-id").getText();
					Query.makeQueryAsync(QueryMethod.Get, String.format(QBQueries.GET_GEOUSER_QUERY_FORMAT, geouserId), null, null, 
							this, QBQueries.QBQueryType.QBQueryTypeGetGeoUser);
					
					// authentication error
>>>>>>> 4bc8f2f7e1f36ea0f841f4f50a34c3c0299f2952
				} else if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus401) {
					queryProgressBar.setVisibility(View.GONE);
				
					AlertManager.showServerError(this, "Unauthorized. Please check you login and password");
					
					//validation error
				} else if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus422) {
					queryProgressBar.setVisibility(View.GONE);
					
					String error = response.getBody().getChildren().get(0).getText();
					AlertManager.showServerError(this, error);
				}
			break;
		}
	}
}