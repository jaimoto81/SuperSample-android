package com.quickblox.supersamples.main.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class QuizActivity extends Activity{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Coming soon...");
        setContentView(textview);
    }
}