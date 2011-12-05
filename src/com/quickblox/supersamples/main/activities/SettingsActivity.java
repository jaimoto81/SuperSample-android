package com.quickblox.supersamples.main.activities;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.flurry.android.FlurryAgent;

import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.helpers.ValidationManager;
import com.quickblox.supersamples.main.views.MapPopUp;
import com.quickblox.supersamples.main.views.ProfilePopUp;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.helpers.Query;

import com.quickblox.supersamples.main.definitions.Consts;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	private RelativeLayout relativeLayout;
	private Animation animation;
	RelativeLayout editProfilelayout;
	LinearLayout settingsLayout;

	private CheckBox displayOfflineUsers;
	private CheckBox shareLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);

	        TextView textview = new TextView(this);
	        textview.setText("Coming soon...");
	        setContentView(textview);
	    }

	public void onStart()
	{
	    super.onStart();
	    FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
	    FlurryAgent.logEvent("run SettingsActivity");
	    
	}

	public void onStop()
	{
	    super.onStop();
	    FlurryAgent.onEndSession(this);
	}

	public void onClickButtons(View v) {
		editProfilelayout = (RelativeLayout) findViewById(R.id.profile);
		settingsLayout = (LinearLayout) findViewById(R.id.settings_layout);

		switch (v.getId()) {

		case R.id.logout:
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
			finish();
			break;

		case R.id.from_gallery:
			break;
		case R.id.make_photo:
			break;

		case R.id.offline_users:
			displayOfflineUsers = (CheckBox) findViewById(R.id.offline_users);
			changeChecked(displayOfflineUsers);

			break;

		case R.id.share_location:
			shareLocation = (CheckBox) findViewById(R.id.share_location);
			changeChecked(shareLocation);

			break;
		case R.id.save:
			break;

		case R.id.edit_profile:
			// DropDownAnim anim = new DropDownAnim(editProfilelayout, 200,
			// true);
			// editProfilelayout.startAnimation(anim);

			editProfilelayout.setVisibility(View.VISIBLE);
			windowAnimationAppear(editProfilelayout);

			/*
			 * FrameLayout.LayoutParams lp = new
			 * FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
			 * 300); editProfilelayout.setLayoutParams(lp);
			 * editProfilelayout.setVisibility(View.VISIBLE);
			 * editProfilelayout.invalidate();
			 */
			break;
		case R.id.save_profile:
			break;
		case R.id.cancel_profile:

			settingsLayout.setVisibility(View.VISIBLE);
			editProfilelayout.setVisibility(View.INVISIBLE);

			// windowAnimationHide(editProfilelayout);
			break;
		}
	}

	public void changeChecked(CheckBox checkbox) {
		if (checkbox.isChecked())
			checkbox.setTextColor(Color.RED);
		else
			checkbox.setTextColor(Color.YELLOW);
	}

	public void windowAnimationExtended(View layout, int animationId) {
		Animation animation = AnimationUtils.loadAnimation(this, animationId);
		layout.startAnimation(animation);
	}

	public void windowAnimationAppear(View layout) {
		windowAnimationExtended(layout, R.anim.window_appear);
		layout.setVisibility(View.VISIBLE);
	}

	public void windowAnimationHide(View layout) {
		windowAnimationExtended(layout, R.anim.window_hide);
		layout.setVisibility(View.INVISIBLE);
	}

	public class DropDownAnim extends Animation {
		int targetHeight;
		View view;
		boolean down;

		public DropDownAnim(View view, int targetHeight, boolean down) {
			this.view = view;
			this.targetHeight = targetHeight;
			this.down = down;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			int newHeight;
			if (down) {
				newHeight = (int) (targetHeight * interpolatedTime);
			} else {
				newHeight = (int) (targetHeight * (1 - interpolatedTime));
			}
			view.getLayoutParams().height = newHeight;
			view.requestLayout();
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
				int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	}

}
