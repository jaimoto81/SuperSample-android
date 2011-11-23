package com.quickblox.supersamples.main.activities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.helpers.ValidateFieldsForm;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements ActionResultDelegate{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_view);
	}

	public void onClickButtons(View v) {
		switch (v.getId()) {
			case R.id.butNext:

				EditText editLogin = (EditText) findViewById(R.id.edit_login);
				EditText editPassword = (EditText) findViewById(R.id.edit_password);

				// validate fields
				int validationResult = ValidateFieldsForm.checkInputParameters(editLogin, editPassword);
				if(validationResult != ValidateFieldsForm.ALERT_OK){
					showDialog(validationResult);
					return;
				}


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
			case ValidateFieldsForm.ALERT_LOGIN:
				alertMessage = R.string.alert_login_blank;
				break;

			case ValidateFieldsForm.ALERT_PASSWORD:
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
		if(queryType == QBQueries.QBQueryType.QBQueryTypeLoginUser){
			if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus202) {
				//int extUserId = Integer.parseInt((response.getBody().findChild("external-user-id").getText()));
				//Log.i("EXTERNAL USER ID =", String.valueOf(extUserId));
				
				Toast.makeText(this, "Login was successful!",
						Toast.LENGTH_LONG).show();

				// store current user
				Store.getInstance().setCurrentUser(response.getBody().findChild("external-user-id").getText());
				
				// show main activity
			    Intent intent = new Intent();
				intent.setClass(this, TabsActivity.class);
				startActivity(intent);
				finish();
				
			} else
				Toast.makeText(this, "Login was unsuccessful",
						Toast.LENGTH_LONG).show();
		}
	}
}