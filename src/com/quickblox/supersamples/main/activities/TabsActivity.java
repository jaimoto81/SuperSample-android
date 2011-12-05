package com.quickblox.supersamples.main.activities;




import com.flurry.android.FlurryAgent;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.definitions.Consts;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
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
	    
	    Resources res = getResources();
	    
	    // Map tab
	    intent = new Intent().setClass(this, MapViewActivity.class);
	    spec = tabs.newTabSpec("map").setIndicator("Map", res.getDrawable(R.drawable.map_tab)).setContent(intent);
	    tabs.addTab(spec);
	    
	    // Chat tab
	    intent = new Intent().setClass(this, ChatActivity.class);
	    spec = tabs.newTabSpec("chat").setIndicator("Chat", res.getDrawable(R.drawable.chat_tab)).setContent(intent);
	    tabs.addTab(spec);
	    
		// Quiz tab
	    intent = new Intent().setClass(this, QuizActivity.class);
	    spec = tabs.newTabSpec("quiz").setIndicator("Quiz", res.getDrawable(R.drawable.quiz_tab)).setContent(intent);
	    tabs.addTab(spec);
	    
		// Settings tab
	    intent = new Intent().setClass(this, SettingsActivity.class);
	    spec = tabs.newTabSpec("settings").setIndicator("Settings", res.getDrawable(R.drawable.settings_tab)).setContent(intent);
	    tabs.addTab(spec);
	    
	    tabs.setCurrentTab(0);	
	}
	
	public void onStart()
	{
	    super.onStart();
	    FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
	    FlurryAgent.logEvent("run TabsActivity");
	    
	}
	
	public void onStop()
	{
	    super.onStop();
	    FlurryAgent.onEndSession(this);
	}
}