package com.quickblox.supersamples.main.views;

import com.quickblox.supersamples.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MapPopUp {
	View popup;
    boolean isVisible = false;
    ViewGroup parent;
    
    public MapPopUp(Activity ctx, ViewGroup parent) {
    	this.parent = parent;
    	popup = ctx.getLayoutInflater().inflate(R.layout.map_pop_up, parent, false);    
    	popup.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			hide();
    		}
    	});
    }
    
    public View getView() {
    	return popup;
    }
    
    public void show(boolean alignTop) {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
      
		if (alignTop) {
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lp.setMargins(0, 20, 0, 0);
		} else {
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			lp.setMargins(0, 0, 0, 60);
		}

		hide();
      
		parent.addView(popup, lp);
		isVisible = true;
    }
    
    public void hide() {
    	if (isVisible) {
    		isVisible = false;
    		((ViewGroup)popup.getParent()).removeView(popup);
    	}
    }
}