package com.quickblox.supersamples.main.activities;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.helpers.ValidateFieldsForm;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.helpers.LocationsXMLHandler;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.objects.LocationsList;
import com.quickblox.supersamples.sdk.objects.RestResponse;
import com.quickblox.supersamples.sdk.objects.XMLNode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements ActionResultDelegate{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_view);
	}

	public void onClickButtons(View v) {

		switch (v.getId()) {
			case R.id.butNext:
	
				EditText editFullName = (EditText) findViewById(R.id.edit_full_name);
				EditText editLogin = (EditText) findViewById(R.id.edit_login);
				EditText editEmail = (EditText) findViewById(R.id.edit_email);
				EditText editPassword = (EditText) findViewById(R.id.edit_password);
				EditText editRetypePass = (EditText) findViewById(R.id.edit_retype_pass);
	
				// validate fields
				int validationResult = ValidateFieldsForm.checkInputParameters(editLogin, editPassword, 
						editRetypePass, editFullName, editEmail);
				if(validationResult != ValidateFieldsForm.ALERT_OK){
					showDialog(validationResult);
					return;
				}
				
				//--------------------------------------------------------------------------------------
				
				// create entity
				List<NameValuePair> formparamsUser = new ArrayList<NameValuePair>();
				formparamsUser.add(new BasicNameValuePair("user[full_name]", editFullName.getText().toString()));
				formparamsUser.add(new BasicNameValuePair("user[email]", editEmail.getText().toString()));
				formparamsUser.add(new BasicNameValuePair("user[login]", editLogin.getText().toString()));
				formparamsUser.add(new BasicNameValuePair("user[password]", editPassword.getText().toString()));
				formparamsUser.add(new BasicNameValuePair("user[owner_id]", QBQueries.OWNER_ID));
				UrlEncodedFormEntity postEntityUser = null;
				try {
					postEntityUser = new UrlEncodedFormEntity(formparamsUser, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				// make query
				Query.makeQueryAsync(QueryMethod.Post, QBQueries.CREATE_USER_QUERY, postEntityUser, null, 
						this, QBQueries.QBQueryType.QBQueryTypeCreateUser);
				
				
				break;
	
			// exit to the Main Activity
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
		case ValidateFieldsForm.ALERT_FULLNAME:
			alertMessage = R.string.alert_fullname_blank;
			break;
			
		case ValidateFieldsForm.ALERT_EMAIL:
			alertMessage = R.string.alert_email_blank;
			break;
			
		case ValidateFieldsForm.ALERT_LOGIN:
			alertMessage = R.string.alert_login_blank;
			break;

		case ValidateFieldsForm.ALERT_PASSWORD:
			alertMessage = R.string.alert_password_blank;
			break;
			
		case ValidateFieldsForm.ALERT_PASSWORD_REPEAT:
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
		
		switch(queryType){
			case QBQueryTypeCreateUser:
				if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus201) {
					Toast.makeText(this, "On your email was send letter for the confirmation of the registration!",
							Toast.LENGTH_LONG).show();
					
					// create GeoUser
					
					// create entity for geoUser
					List<NameValuePair> formparamsGeoUser = new ArrayList<NameValuePair>();
					formparamsGeoUser.add(new BasicNameValuePair("user[name]", response.getBody().findChild("login").getText().toString()));
					formparamsGeoUser.add(new BasicNameValuePair("user[app_id]", QBQueries.APPLICATION_ID));
					UrlEncodedFormEntity postEntityGeoUser = null;
					try {
						postEntityGeoUser = new UrlEncodedFormEntity(formparamsGeoUser, "UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					
					// make query
					Query.makeQueryAsync(QueryMethod.Post, QBQueries.CREATE_GEOUSER_QUERY, postEntityGeoUser, null, 
							this, QBQueryType.QBQueryTypeCreateGeoUser);
					
				} else
					Toast.makeText(this, "User created unsuccessful",
							Toast.LENGTH_LONG).show();		
				break;
				
			case QBQueryTypeCreateGeoUser:
				if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus201){ 		
					Log.i("ID=", response.getBody().findChild("id").getText());
				}
				break;
		}
	}
}