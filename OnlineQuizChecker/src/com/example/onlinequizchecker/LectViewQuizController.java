package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class LectViewQuizController {
	private MainActivity activity;
	private WebView webView;
	private Button back;
	private File filelist;
	public LectViewQuizController(MainActivity activity,String course,String quiz)
	{
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_viewquiz);
		this.webView = (WebView)this.activity.findViewById(R.id.webView);
		this.back = (Button)this.activity.findViewById(R.id.backBtn);
		back.setOnClickListener(new backBtnListener());
		loadQuiz(course,quiz);
	}
	private void loadQuiz(String course,String quiz) {
		// TODO Auto-generated method stub
		filelist = activity.getFilelist();
		try {
			File quizFileToView = new File(filelist.getCanonicalPath()+"/"+course+"/Quizzes/Form/"+quiz+".html");
			WebSettings settings = webView.getSettings();
			settings.setJavaScriptEnabled(true);
//			webView.loadUrl("file:///android_asset/1.html");

			webView.loadUrl("file:///"+quizFileToView.getAbsolutePath());;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class backBtnListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
		
			activity.setContentView(R.layout.lect_quizselectionview);
		}
		
	}
}
