package com.quickblox.supersamples.main.views;

import com.quickblox.supersamples.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class MapPopUp {
	View popup;
    boolean isVisible = false;
    ViewGroup parent;

    public MapPopUp(Activity ctx, ViewGroup parent) {
    	this.parent = parent;
    	popup = ctx.getLayoutInflater().inflate(R.layout.map_pop_up, parent, false);    
    }
    
    public View getView() {
    	return popup;
    }
    
    public void show() {      
    	hide();
		parent.addView(popup);	
		isVisible = true;		
		
    }
    
    
    public void hide() {
    	if (isVisible) {
    		isVisible = false;
    		((ViewGroup)popup.getParent()).removeView(popup);
    	}
    }
}