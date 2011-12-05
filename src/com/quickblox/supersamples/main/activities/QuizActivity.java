package com.quickblox.supersamples.main.activities;

import com.flurry.android.FlurryAgent;
import com.quickblox.supersamples.main.definitions.Consts;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class QuizActivity extends Activity{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i("QuizActivity", "onCreate");

        TextView textview = new TextView(this);
        textview.setText("Coming soon...");
        setContentView(textview);
    }
	
	public void onStart()
	{
	    super.onStart();
	    FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
	    FlurryAgent.logEvent("run QuizActivity");
	    
	}
	
	public void onStop()
	{
	    super.onStop();
	    FlurryAgent.onEndSession(this);
	}
}