package com.quickblox.supersamples.main.views;

import com.quickblox.supersamples.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MapPopUp {
	RelativeLayout popupView;
    boolean isVisible = false;
    ViewGroup parent;

    public MapPopUp(Activity ctx, ViewGroup parent) {
    	this.parent = parent;
    	
    	// inflate view
    	popupView = (RelativeLayout)ctx.getLayoutInflater().inflate(R.layout.map_pop_up, parent, false);
    	
    	// init close button
		ImageButton butClosePopUp = (ImageButton) popupView.findViewById(R.id.mapPopup_close_button);
		butClosePopUp.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			hide();
    		}
    	});
    }
    
    public RelativeLayout getView() {
    	return popupView;
    }
    
    public void show() {      
    	hide();
    	
		parent.addView(popupView);	
		
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(300);
		popupView.startAnimation(animation);
		
		isVisible = true;		
    }
    
    public void hide() {
    	if (isVisible) {
    		isVisible = false;
    		((ViewGroup)popupView.getParent()).removeView(popupView);
    	}
    }
    
    public void setData(String userID, String userFullName, String userStatus){
    	TextView userLoginTextView = (TextView) popupView.findViewById(R.id.mapPopup_login);
    	TextView userFullNameTextView = (TextView) popupView.findViewById(R.id.mapPopup_fullname);
    	TextView userStatusTextView = (TextView) popupView.findViewById(R.id.text_status);
    	//TextView userBioTextView = (TextView) popupView.findViewById(R.id.text_bio);
    	
    	ProgressBar progressBar = (ProgressBar) popupView.findViewById(R.id.progressBar);
    	progressBar.setProgress(60);
    	
    	userLoginTextView.setText(userID);
    	
    	userFullNameTextView.setText(userFullName);
    	
    	if(userStatus == null){
    		userStatus = "<empty>";
    	}
    	userStatusTextView.setText(userStatus);
    	
    }
}