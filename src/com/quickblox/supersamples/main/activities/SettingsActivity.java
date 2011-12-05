package com.quickblox.supersamples.main.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.flurry.android.FlurryAgent;
import com.google.android.maps.GeoPoint;

import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.helpers.AlertManager;
import com.quickblox.supersamples.main.helpers.ValidationManager;
import com.quickblox.supersamples.main.objects.MapOverlayItem;
import com.quickblox.supersamples.main.views.MapPopUp;
import com.quickblox.supersamples.main.views.ProfilePopUp;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.RestResponse;
import com.quickblox.supersamples.sdk.objects.XMLNode;

import com.quickblox.supersamples.main.activities.MapViewActivity.ShowAllUsers;
import com.quickblox.supersamples.main.definitions.Consts;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity implements ActionResultDelegate {

	private RelativeLayout relativeLayout;
	private Animation animation;
	RelativeLayout editProfilelayout;
	LinearLayout settingsLayout;
	private ImageView imageView;

	private CheckBox displayOfflineUsers;
	private CheckBox shareLocation;
	
	private static final String TAG = "Camera";
	Uri myPicture = null;

	private static final int GALLERY_REQUEST = 0; 
	private static final int CAMERA_REQUEST = 1; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 	setContentView(R.layout.settings);
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

		// remove the session
		case R.id.logout:
			
			// make query
			Query.performQueryAsync(QueryMethod.Get, QBQueries.LOGOUT_USER_QUERY,
					null, null, this, QBQueries.QBQueryType.QBQueryTypeLogoutUser);
				
			break;

		case R.id.from_gallery:
			
			Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, GALLERY_REQUEST);
			
			break;
		case R.id.make_photo:
			
			Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        startActivityForResult(i, CAMERA_REQUEST);
	        
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

			editProfilelayout.setVisibility(View.VISIBLE);
			windowAnimationAppear(editProfilelayout);

			break;
		case R.id.save_profile:
			break;
		case R.id.cancel_profile:

			settingsLayout.setVisibility(View.VISIBLE);
			editProfilelayout.setVisibility(View.INVISIBLE);

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
	

	@Override
	public void completedWithResult(QBQueryType queryType, RestResponse response) {

		switch (queryType) {

		case QBQueryTypeLogoutUser:
			if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus200) {
				
				// store current user
				Store.getInstance().setCurrentUser(null);
				
				Intent intent = new Intent();
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);
				finish();
				Log.i("completedWithResult",
						"The session is removed successfully");
			} else {
				Log.e("completedWithResult", "The session is NOT removed");
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case GALLERY_REQUEST:
			try {			
				if (data != null) {
					Uri currImageURI = data.getData();
					Bitmap thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currImageURI);
					imageView = (ImageView) findViewById(R.id.avatar);
					imageView.setImageBitmap(thumbnail);
				}
			}catch(NullPointerException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			break;

		case CAMERA_REQUEST:
			if (data != null) {
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				imageView = (ImageView) findViewById(R.id.avatar);
				imageView.setImageBitmap(photo.createScaledBitmap(photo, 80, 80, false));
			}
			break;
		}
	}
}
