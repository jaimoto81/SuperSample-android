package com.quickblox.supersamples.main.activities;


import com.quickblox.supersamples.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class TabsActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView (R.layout.tabs_view);
		
		TabHost tabs = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    // Map tab
	    intent = new Intent().setClass(this, MapViewActivity.class);
	    spec = tabs.newTabSpec("map").setIndicator("Map").setContent(intent);
	    tabs.addTab(spec);
	    
	    // Chat tab
	    intent = new Intent().setClass(this, ChatActivity.class);
	    spec = tabs.newTabSpec("chat").setIndicator("Chat").setContent(intent);
	    tabs.addTab(spec);
	    
		// Quiz tab
	    intent = new Intent().setClass(this, QuizActivity.class);
	    spec = tabs.newTabSpec("quiz").setIndicator("Quiz").setContent(intent);
	    tabs.addTab(spec);
	    
		// Settings tab
	    intent = new Intent().setClass(this, SettingsActivity.class);
	    spec = tabs.newTabSpec("settings").setIndicator("Settings").setContent(intent);
	    tabs.addTab(spec);
	    
	    tabs.setCurrentTab(0);	
	}
}
