package com.quickblox.supersamples.main.views;

import java.util.ArrayList;
import java.util.Map;

import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.activities.QuizActivity;
import com.quickblox.supersamples.main.objects.MapOverlayItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class QuizViewGroup {

	private LinearLayout quizView;
	private TextView textQuestion;
	private RadioGroup radioGroup;
	private RadioButton radioAnswer;
	private Button butAnswer;
	
	private final static int BUT_ANS_ID = 1;
	private static int ansUserID = -1;
	private static int score = 0;
	
	private String rightAns = null;

	public QuizViewGroup(Context context, Map<String, Object> question) {

		quizView = new LinearLayout(context);
		quizView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		quizView.setOrientation(LinearLayout.VERTICAL);
		//quizView.setBackgroundColor(Color.WHITE);
		quizView.setBackgroundResource(R.drawable.map_popup_shape);
		quizView.setGravity(Gravity.CENTER);
		quizView.setPadding(30, 30, 200, 30);

		textQuestion = new TextView(context);
		quizView.addView(textQuestion);

		radioGroup = new RadioGroup(context);
		quizView.addView(radioGroup);

		setTextQuestion(question.get("question").toString());

		// get a number (id) of a right answer
		rightAns = question.get("right_answer").toString();

		for (int i = 0; i < ((ArrayList<String>) question.get("answers")).size(); i++) {
			radioAnswer = new RadioButton(context);
			radioAnswer.setId(i);
			radioGroup.addView(radioAnswer);
			radioAnswer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// get an users's answer
					QuizViewGroup.this.ansUserID = getUserAnswerById(v);
					Log.i("ansUserID", String.valueOf(ansUserID));
				}
			});
			setRadioAnswer(((ArrayList<String>) question.get("answers")).get(i));
		}

		butAnswer = new Button(context);
		quizView.addView(butAnswer);
		setButAnswer();
	}

	public View getQuizView() {
		return quizView;
	}

	public TextView getTextQuestion() {
		return textQuestion;
	}

	public void setTextQuestion(String text) {
		textQuestion.setTextColor(Color.BLACK);
		textQuestion.setText(text);
		textQuestion.setGravity(Gravity.CENTER);
		textQuestion.setTextSize(15);
	}

	public RadioButton getRadioAnswer() {
		return radioAnswer;
	}

	public void setRadioAnswer(String answer) {
		radioAnswer.setTextColor(Color.BLACK);
		radioAnswer.setText(answer);
		radioAnswer.setGravity(Gravity.CENTER);
		radioAnswer.setTextSize(15);
	}

	public void setButAnswer() {
		butAnswer.setId(BUT_ANS_ID);
		butAnswer.setText(R.string.answer);
		butAnswer.setBackgroundResource(R.drawable.red_button);
		butAnswer.setGravity(Gravity.CENTER);

	}

	public Button getButAnswer() {
		return butAnswer;
	}

	public int getUserAnswerById(View v) {
		return v.getId();
	}

	public String getRightAnswer() {
		return rightAns;
	}

	// to increment a score, if a user answered right
	public static void setCurrentScore(String rightAns) {
		
		Log.i("rightAns", String.valueOf(rightAns));
		
		if (Integer.valueOf(rightAns) == ansUserID)
			++score;
		
		ansUserID = -1;
	}

	public static int getCurrentScore() {
		return score;
	}
	
	public static void resetCurrentScore(){
		score = 0;
	}
}