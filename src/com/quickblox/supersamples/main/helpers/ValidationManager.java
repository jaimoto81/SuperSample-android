package com.quickblox.supersamples.main.helpers;

import java.util.regex.Pattern;
import android.widget.EditText;

public class ValidationManager {

	// a result of the validation of the fields from the form
	public static final int ALERT_OK = 0; // parameters is entered correctly
	public static final int ALERT_LOGIN = 1; // incorrect login
	public static final int ALERT_PASSWORD = 2; // incorrect password
	public static final int ALERT_PASSWORD_REPEAT = 3; // incorrect password
	public static final int ALERT_FULLNAME = 4; // incorrect fullname
	public static final int ALERT_EMAIL = 5; // incorrect email
	
	//  An email-validation using the regular expression
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
					+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
					+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");

	
	public static int checkInputParameters(EditText login, EditText pass) {

		String fieldLogin = login.getText().toString();
		String fieldPass = pass.getText().toString();

		if (fieldLogin.length() == 0) {
			return ALERT_LOGIN;
		}

		if (fieldPass.length() == 0) {
			return ALERT_PASSWORD;
		}

		return ALERT_OK;
	}

	public static int checkInputParameters(EditText login, EditText pass, EditText pass2,
			EditText fullname) {

		String fieldLogin = login.getText().toString();
		String fieldPass = pass.getText().toString();
		String fieldPass2 = pass2.getText().toString();
		String fieldFullname = fullname.getText().toString();

		if (fieldFullname.length() == 0) {
			return ALERT_FULLNAME;
		}

		if (fieldLogin.length() == 0) {
			return ALERT_LOGIN;
		}

		if (fieldPass.length() == 0) {
			return ALERT_PASSWORD;
		}
		
		if(!fieldPass.equals(fieldPass2)){
			return ALERT_PASSWORD_REPEAT;
		}

		return ALERT_OK;
	}
}