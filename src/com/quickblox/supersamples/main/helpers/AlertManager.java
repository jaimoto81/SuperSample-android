package com.quickblox.supersamples.main.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertManager {
	public static void showServerError(Context ctx, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           }
		       });
		
		AlertDialog alert = builder.create();
		alert.show();
	}
}
