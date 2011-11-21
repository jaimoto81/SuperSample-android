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
	    
	    // Create an Intent to launch an MapActivity for the tab (to be reused)
	    intent = new Intent().setClass(this, MapViewActivity.class);
	    
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabs.newTabSpec("map").setIndicator("Map").setContent(intent);
	    tabs.addTab(spec);
	    
	    // Create an Intent to launch an ChatActivity for the tab (to be reused)
	    intent = new Intent().setClass(this, ChatActivity.class);
	    spec = tabs.newTabSpec("chat").setIndicator("Chat").setContent(intent);
	    tabs.addTab(spec);
	    
		 // Create an Intent to launch an QuizActivity for the tab (to be reused)
	    intent = new Intent().setClass(this, QuizActivity.class);
	    spec = tabs.newTabSpec("quiz").setIndicator("Quiz").setContent(intent);
	    tabs.addTab(spec);
	    
		 // Create an Intent to launch an SettingsActivity for the tab (to be reused)
	    intent = new Intent().setClass(this, SettingsActivity.class);
	    spec = tabs.newTabSpec("settings").setIndicator("Settings").setContent(intent);
	    tabs.addTab(spec);
	    
	    tabs.setCurrentTab(0);	
	}
}
