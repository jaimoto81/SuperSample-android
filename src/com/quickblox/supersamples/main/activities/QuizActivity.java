package com.quickblox.supersamples.main.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.flurry.android.FlurryAgent;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.definitions.Consts;
import com.quickblox.supersamples.main.views.QuizViewGroup;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.XMLNode;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class QuizActivity extends Activity {

	private ViewFlipper flipper = null;
	private ArrayList<Map<String, Object>> questions; 

	private QuizViewGroup quizViewGroup;
	
	private int score = 0;
	private int ansUserID = 0;
	private String rightAns = null;
	public void onCreate(Bundle savedInstanceState) {

		Log.i("QuizActivity", "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);

		flipper = (ViewFlipper) findViewById(R.id.flipper);
		
		questions = new ArrayList<Map<String, Object>>();	
		
		Map <String, Object> currentQuestion = null;	
		ArrayList<String> answers = null;
		
		String currentElementName = null;
		
		try {
            XmlPullParser parser = getResources().getXml(R.xml.quiz_questions);

            while (parser.getEventType()!= XmlPullParser.END_DOCUMENT) {
                switch(parser.getEventType()){
                	case  XmlPullParser.START_TAG:
                		currentElementName = parser.getName();
                		
                        if(currentElementName.equals("question")) {
                        	currentQuestion = new HashMap<String, Object>();
                        	currentQuestion.put("question", parser.getAttributeValue(0));
                        	currentQuestion.put("right_answer", parser.getAttributeValue(1));
                	
                        	answers = new ArrayList<String>();     	
                        } 
                		break;
                	case XmlPullParser.TEXT:
                		if(currentElementName.equals("answer")) {
                			answers.add(parser.getText());
                		}
                		break;
                	case XmlPullParser.END_TAG:
                        if(parser.getName().equals("question")) {
                        	currentQuestion.put("answers", answers);
                        	answers = null;
                        	
                        	questions.add(currentQuestion);
                        	currentQuestion = null;
                        }
                        break;
                }
                parser.next();
            }
        } catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Error loading XML document: " + e.toString(),
					4000).show();
		}
		for (Map<String, Object> question : questions) {
			
			rightAns = question.get("right_answer").toString();
			Log.i("rightAns", String.valueOf(rightAns));
			quizViewGroup = new QuizViewGroup(this, question);
			quizViewGroup.getRadioAnswer().setOnClickListener(radioButtonClick);
			flipper.addView(quizViewGroup.getQuizView());
		}
	}

	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
		FlurryAgent.logEvent("run QuizActivity");
	}

	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	public void onClickButtons(View v) {
		if (quizViewGroup.checkedAnswers(rightAns, ansUserID)) {
			score++;
		}
		
		flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.go_next_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.go_next_out));
		flipper.showNext();
		Log.i("score", String.valueOf(score));
	}
	
	OnClickListener radioButtonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			RadioButton rb = (RadioButton)v;
			ansUserID = quizViewGroup.getAnswerById(rb);
			Log.i("ansUserID", String.valueOf(ansUserID));
		}
	};
	
}