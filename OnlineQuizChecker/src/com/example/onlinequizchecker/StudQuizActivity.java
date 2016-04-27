package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.CountDownTimer;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class StudQuizActivity{
	private MainActivity activity;
	private WebView webView;
	private TextView timeLeftText;
	private final CounterClass timer;
	private String quizPath;
	public StudQuizActivity(MainActivity mainActivity, int timePeriod,String quizPath) {
		super();
		mainActivity.setContentView(R.layout.stud_quizview);
		webView = (WebView) mainActivity.findViewById(R.id.quizWebView);
		timeLeftText = (TextView) mainActivity.findViewById(R.id.timeLeftTxt);
		timer = new CounterClass(timePeriod*60000, 1000);
		this.quizPath = quizPath;
		loadQuiz();
	}

	private void loadQuiz() {
		// TODO Auto-generated method stub
//		File filelist = activity.getFilelist();
		// File quizFileToView = new
		// File(filelist.getCanonicalPath()+"/"+course+"/Quizzes/"+quiz+"/Form/"+quiz+".html");
//		File quizFileToView = new File(".");
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		// webView.loadUrl("file:///android_asset/1.html");
		webView.setWebViewClient(new WebViewClient() {

			   public void onPageFinished(WebView view, String url) {
			        // do your stuff here
				   timer.start();
			    }
			});
		webView.loadUrl("file://" + quizPath);
		
	}

	public class CounterClass extends CountDownTimer {

		public CounterClass(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub

			long millis = millisUntilFinished;
			String ms = String.format("%02d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(millis)
							- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
					TimeUnit.MILLISECONDS.toSeconds(millis)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
			System.out.println(ms);
			timeLeftText.setText(ms);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			// timeLeftText.setText("Completed.");
		}

	}
}
