package com.quickblox.supersamples.main.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.flurry.android.FlurryAgent;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.definitions.Consts;
import com.quickblox.supersamples.main.views.QuizViewGroup;
import com.quickblox.supersamples.sdk.helpers.Store;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizActivity extends Activity implements OnClickListener {

	private ViewFlipper flipper = null;
	private ArrayList<Map<String, Object>> questions;
	private ArrayList<QuizViewGroup> quizViewGroup;
	
	private static TextView correctAnswersText;
	private static TextView currentScoreText;
	private static TextView totalScoreText;
	
	private Button butAnswer;
	private Button butBackQuiz;

	private static int nubmerOfObject = 0;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);

		butAnswer = (Button) findViewById(R.id.butAns);
		butBackQuiz = (Button) findViewById(R.id.butBackQuiz);
		
		butAnswer.setOnClickListener(this);
		butBackQuiz.setOnClickListener(this);
		butAnswer.setOnClickListener(this);

		flipper = (ViewFlipper) findViewById(R.id.flipper);

		addFlipping();
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

	public void addFlipping() {
		
		if (questions != null){
			questions = null;
		}
		questions = new ArrayList<Map<String, Object>>();

		Map<String, Object> currentQuestion = null;
		ArrayList<String> answers = null;
		String currentElementName = null;

		// parse Questions
		try {
			XmlPullParser parser = getResources().getXml(R.xml.quiz_questions);

			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					currentElementName = parser.getName();

					if (currentElementName.equals("question")) {
						currentQuestion = new HashMap<String, Object>();
						currentQuestion.put("question",
								parser.getAttributeValue(0));
						currentQuestion.put("right_answer",
								parser.getAttributeValue(1));

						answers = new ArrayList<String>();
					}
					break;
				case XmlPullParser.TEXT:
					if (currentElementName.equals("answer")) {
						answers.add(parser.getText());
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("question")) {
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
		
		
		// Add Start view
		LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
		ViewGroup layout1 = (ViewGroup) inflater.inflate(R.layout.begin_quiz, (ViewGroup) findViewById(R.id.go_layout));
		final ImageButton butGoQuiz = (ImageButton) layout1.findViewById(R.id.butGoQuiz);
		butGoQuiz.setOnClickListener(this);
		flipper.addView(layout1);
		
		
		// Add questions views
		quizViewGroup = new ArrayList<QuizViewGroup>();
		for (int i = 0; i < questions.size(); i++) {
			QuizViewGroup quizGroup = new QuizViewGroup(this, questions.get(i));
			quizViewGroup.add(quizGroup);
			flipper.addView(quizViewGroup.get(i).getQuizView());
		}

		
		// Add Result view
		LayoutInflater inflater2 = (LayoutInflater) getLayoutInflater();
		ViewGroup layout2 = (ViewGroup) inflater2.inflate(
				R.layout.quiz_results,
				(ViewGroup) findViewById(R.id.quiz_results_layout));

		correctAnswersText = (TextView) layout2.findViewById(R.id.correct_ans);
		currentScoreText = (TextView) layout2.findViewById(R.id.current_score);
		totalScoreText = (TextView) layout2.findViewById(R.id.total_points);

		flipper.addView(layout2);
	}

	public void nextFlip (Context cnt, ViewFlipper vf){
		vf.setInAnimation(AnimationUtils.loadAnimation(cnt,
				R.anim.go_next_in));
		vf.setOutAnimation(AnimationUtils.loadAnimation(cnt,
				R.anim.go_next_out));
		vf.showNext();
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// start Quiz
		case R.id.butGoQuiz:
            if(Store.getInstance().getCurrentUser() == null){
                Toast.makeText(this, "You must login first. Go to Settings tab.",
                        Toast.LENGTH_LONG).show();
                return;
            }

			nextFlip(this, flipper);
			
			butAnswer.setVisibility(View.VISIBLE);
			butAnswer.setText(R.string.answer);
			butBackQuiz.setVisibility(View.VISIBLE);
			
			break;	
			
		// answer question
		case R.id.butAns:			
			// begin to count if user answered correctly
			QuizViewGroup.setCurrentScore(quizViewGroup.get(nubmerOfObject)
					.getRightAnswer());
			
			// setCurrentScore();
			if (nubmerOfObject < quizViewGroup.size()-1){
				quizViewGroup.get(nubmerOfObject++);
			}else{
				// add of a current score in a total score and to save a result in the settings
				QuizActivity.setQuizPower(this, QuizViewGroup.getCurrentScore());
				
				// set the correct answers, current score and total score in the Result's view
				correctAnswersText.setText(String.valueOf(QuizViewGroup.getCurrentScore()));
				currentScoreText.setText(String.valueOf(QuizViewGroup.getCurrentScore()));
				totalScoreText.setText(String.valueOf(QuizActivity.getQuizPower(this, QuizViewGroup.getCurrentScore())));
				
				//butAnswer.setText(R.string.but_leaderboard);
				butAnswer.setVisibility(View.GONE);
			}
			
			nextFlip(this, flipper);
			
			break;
			
		// Back	
		case R.id.butBackQuiz:
			flipper.removeAllViews();
			
			butAnswer.setVisibility(View.INVISIBLE);
			butBackQuiz.setVisibility(View.INVISIBLE);

			QuizViewGroup.resetCurrentScore();
			nubmerOfObject = 0;
			
			addFlipping();

			break;
		}
	}
	

	// Quiz power accessors
	public static int getQuizPower(Context context, int currentScore) {
		String key = Store.getInstance().getCurrentUser()
				.findChild("id").getText() + "_quizPower";

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		return settings.getInt(key, currentScore);
	}

	public static void setQuizPower(Context context, int currentScore) {
		int sumScore = 0;
		String key = Store.getInstance().getCurrentUser()
				.findChild("id").getText() + "_quizPower";

		// to get of the current total score from of the settings
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);

		// if a user's key does not exist, then it will be create
		// if this preference does not exist, then currenScore is as defValue
		if (!settings.contains(key))
			sumScore = settings.getInt(key, currentScore);
		else {
			sumScore = settings.getInt(key, currentScore);
			sumScore += currentScore;
		}

		// add and save a current score in the total score
		Editor editor = settings.edit();
		editor.putInt(key, sumScore);
		editor.commit();
	}
}