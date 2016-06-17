package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

/**
 * The Class LectViewQuizController.
 * This class controls the quiz viewing screen in the lecturer's phone.
 */
public class LectViewQuizController {

	/** The activity. */
	private MainActivity activity;

	/** The web view. */
	private WebView webView;

	/** The back button. */
	private Button back;

	/** The filelist. */
	private File filelist;

	/** The quiz file to view. */
	private File quizFileToView;

	/** The previous controller1. */
	private LectQuizSelectionController prevController1;

	/** The previous controller2. */
	private LectQuizProgressController prevController2;

	/** The previous listener. */
	private View.OnClickListener prevListener;

	/**
	 * Instantiates a new lect view quiz controller.
	 *
	 * @param activity
	 *            the activity
	 * @param previousController
	 *            the previous controller
	 * @param course
	 *            the course
	 * @param quiz
	 *            the quiz to view
	 * @param selectedIndex
	 *            the selected index
	 */
	public LectViewQuizController(MainActivity activity, LectQuizSelectionController previousController, String course,
			String quiz, int selectedIndex) {
		
		this.prevController1 = previousController;
		this.activity = activity;
		this.activity.hideKeyboard();
		this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.activity.setContentView(R.layout.lect_viewquiz);
		this.webView = (WebView) this.activity.findViewById(R.id.webView);
		this.back = (Button) this.activity.findViewById(R.id.backBtnLectViewQuiz);
		prevListener = new backBtnQuizSelectionListener();
		back.setOnClickListener(prevListener);
		loadQuiz(course, quiz);
	}

	/**
	 * Instantiates a new lect view quiz controller.
	 *
	 * @param activity
	 *            the activity
	 * @param previousController
	 *            the previous controller
	 * @param course
	 *            the course
	 * @param quiz
	 *            the quiz to view
	 */
	public LectViewQuizController(MainActivity activity, LectQuizProgressController previousController, String course,
			String quiz) {
		
		this.prevController2 = previousController;
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_viewquiz);
		this.webView = (WebView) this.activity.findViewById(R.id.webView);
		this.back = (Button) this.activity.findViewById(R.id.backBtnLectViewQuiz);
		prevListener = new backBtnQuizProgressListener();
		back.setOnClickListener(prevListener);
		loadQuiz(course, quiz);
	}

	/**
	 * Load quiz.
	 *
	 * @param course
	 *            the course
	 * @param quiz
	 *            the quiz to load
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void loadQuiz(String course, String quiz) {
		// TODO Auto-generated method stub
		filelist = activity.getFilelist();
		try {
			quizFileToView = new File(
					filelist.getCanonicalPath() + "/" + course + "/Quizzes/" + quiz + "/Form/" + quiz + ".html");
			WebSettings settings = webView.getSettings();
			settings.setJavaScriptEnabled(true);
			settings.setBuiltInZoomControls(true);
			settings.setDisplayZoomControls(false);
			webView.loadUrl("file://" + quizFileToView.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * {@link OnClickListener} for the back button in the quiz selection screen.
	 *
	 * @see backBtnQuizSelectionEvent
	 */
	class backBtnQuizSelectionListener implements View.OnClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
			prevController1.retrieveView();
		}

	}

	/**
	 * {@link OnClickListener} for the back button in the quiz progress screen.
	 *
	 * @see backBtnQuizProgressEvent
	 */
	class backBtnQuizProgressListener implements View.OnClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
			prevController2.retrieveView();
		}

	}
}
