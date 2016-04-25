package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;

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
		loadQuiz(course,quiz);
	}
	private void loadQuiz(String course,String quiz) {
		// TODO Auto-generated method stub
		filelist = activity.getFilelist();
		try {
			File quizFileToView = new File("file:///"+filelist.getCanonicalFile()+"/"+course+"/Quizzes/Form/"+quiz+".html");
			webView.loadUrl(quizFileToView.getAbsolutePath());;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
