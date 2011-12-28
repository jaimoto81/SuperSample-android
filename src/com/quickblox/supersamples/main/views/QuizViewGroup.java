package com.quickblox.supersamples.main.views;

import java.util.ArrayList;
import java.util.Map;

import com.quickblox.supersamples.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
		quizView.setBackgroundResource(R.drawable.map_popup_shape);
		quizView.setGravity(Gravity.CENTER);

		textQuestion = new TextView(context);
		quizView.addView(textQuestion);

		radioGroup = new RadioGroup(context);
		radioGroup.setPadding(10, 0, 0, 10);

		quizView.addView(radioGroup);

		setTextQuestion(question.get("question").toString());

		// get a number (id) of a right answer
		rightAns = question.get("right_answer").toString();

		ArrayList<String> answers = (ArrayList<String>) question.get("answers");
		
		for (int i = 0; i < answers.size(); i++) {
			radioAnswer = new RadioButton(context);
			radioAnswer.setId(i);
			radioGroup.addView(radioAnswer);
			radioAnswer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// get an users's answer
					ansUserID = getUserAnswerById(v);
				}
			});
			setRadioAnswer(answers.get(i));
		}
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
		textQuestion.setTypeface(null, Typeface.BOLD);
		textQuestion.setPadding(0, 10, 0, 10);
		textQuestion.setGravity(Gravity.CENTER);
		textQuestion.setTextSize(18);
	}

	public RadioButton getRadioAnswer() {
		return radioAnswer;
	}

	public void setRadioAnswer(String answer) {
		radioAnswer.setTextColor(Color.BLACK);
		radioAnswer.setText(answer);
		radioAnswer.setTextSize(15);
	}

	public void setButAnswer() {
		butAnswer.setId(BUT_ANS_ID);
		butAnswer.setText(R.string.answer);
		butAnswer.setBackgroundResource(R.drawable.red_button);
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
		if (Integer.valueOf(rightAns) == ansUserID){
			++score;
		}

		ansUserID = -1;
	}

	public static int getCurrentScore() {
		return score;
	}

	public static void resetCurrentScore() {
		score = 0;
	}
}