package com.quickblox.supersamples.main.activities;

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
}