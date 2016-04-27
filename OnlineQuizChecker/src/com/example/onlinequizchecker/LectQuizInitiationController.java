package com.example.onlinequizchecker;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class LectQuizInitiationController {
	private MainActivity activity;
	private String course;
	private String quiz;
	
	private EditText courseIDText;
	private EditText quizNameText;
	private Spinner timeSpinner;
	private Button startQuizBtn;
	public LectQuizInitiationController(MainActivity activity,String course,String quiz)
	{
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_quizinitiationview);
		this.course = course;
		this.quiz = quiz;
		initComponents();
	}
	private void initComponents() {
		// TODO Auto-generated method stub
		courseIDText = (EditText)activity.findViewById(R.id.courseIDTxt);
		quizNameText = (EditText)activity.findViewById(R.id.quizNameTxt);
		timeSpinner = (Spinner)activity.findViewById(R.id.timeSpinner);
		startQuizBtn = (Button)activity.findViewById(R.id.startQuizBtn);
		
		courseIDText.setText(course);
		quizNameText.setText(quiz);
		
		startQuizBtn.setOnClickListener(new startQuizBtnListener());
	}
	class startQuizBtnListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String selectedTimePeriodString = timeSpinner.getSelectedItem().toString().split("\\s+")[0];
			int selectedTimePeriodInt = Integer.valueOf(selectedTimePeriodString);
			startQuiz(selectedTimePeriodInt);
		}

		private void startQuiz(int time) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
