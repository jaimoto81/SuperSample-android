package com.quickblox.supersamples.main.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.flurry.android.FlurryAgent;
import com.quickblox.supersamples.main.definitions.Consts;
import com.quickblox.supersamples.main.views.QuizViewGroup;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.XMLNode;

import com.quickblox.supersamples.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class QuizActivity extends Activity implements OnClickListener {

	private ViewFlipper flipper = null;
	private ArrayList<Map<String, Object>> questions; 
	private QuizViewGroup quizViewGroup;

	private static int quizPower;
	
	public void onCreate(Bundle savedInstanceState) {

		Log.i("QuizActivity", "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);

		final Button butBackQuiz = (Button)findViewById(R.id.butBackQuiz);
		butBackQuiz.setOnClickListener(this);
		
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		
		addFlipping();
		
			
	}
	
	public void addFlipping ()
	{
		if (questions != null)
			questions = null;
		questions = new ArrayList<Map<String, Object>>();	
		
		Map <String, Object> currentQuestion = null;	
		ArrayList<String> answers = null;		
		String currentElementName = null;
		
		// Create imageButton "GO" and add it to the flipper
		LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
		ViewGroup layout1 = (ViewGroup)inflater.inflate(R.layout.begin_quiz, (ViewGroup)findViewById(R.id.go_layout));
		
		final ImageButton butGoQuiz = (ImageButton)layout1.findViewById(R.id.butGoQuiz);
		butGoQuiz.setOnClickListener(this);
		
		flipper.addView(layout1);

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
		
		// init of the Quiz's views
		for (Map<String, Object> question : questions) {
			quizViewGroup = new QuizViewGroup(this, question);
			quizViewGroup.getButAnswer().setOnClickListener(this); // each button "Answer" must shows next the QuizViewGroup
			flipper.addView(quizViewGroup.getQuizView());
		}
		
		setQuizPower(quizViewGroup.getScore());
		// Create layout with the quiz's results and add it to the flipper
		LayoutInflater inflater2 = (LayoutInflater) getLayoutInflater();
		ViewGroup layout2 = (ViewGroup) inflater2.inflate(R.layout.quiz_results, (ViewGroup)findViewById(R.id.quiz_results));

		//final TextView curScore = (TextView)layout2.findViewById(R.id.current_score);
		//curScore.setText(quizViewGroup.getScore());
		
		final Button butLeaderBoard = (Button)layout2.findViewById(R.id.butLeaderboard);
		butLeaderBoard.setOnClickListener(this);
		
		flipper.addView(layout2);
	}
	
	public void setQuizPower (int currentScore)
	{	
		String currentUserId = Store.getInstance().getCurrentUser().findChild("id").getText();
		
		// to get of the current total score from of the settings
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		int sumScore = 0;
		// if a user's key does not exist, then it will be create
		// if this preference does not exist, then currenScore is as defValue
		if (!settings.contains(currentUserId))	
			sumScore = settings.getInt(currentUserId, currentScore);
		else {		
			
			sumScore = settings.getInt(currentUserId, currentScore);	
			sumScore += currentScore;
		}
		
		// add and save a current score in the total score
		Editor editor = settings.edit();
		editor.putInt(currentUserId, sumScore);
		editor.commit();
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
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case 1: // IDs of the butAnswers
			quizViewGroup.setScore();

			// to begin Quiz
		case R.id.butGoQuiz:
			flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.go_next_in));
			flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.go_next_out));
			flipper.showNext();

			break;

		// to return in begin
		case R.id.butBackQuiz:
			flipper.removeAllViews();

			addFlipping();
			break;

		case R.id.butLeaderboard:
			
			
			break;
		}
		
		
		
	}	
}