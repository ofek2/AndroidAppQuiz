package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.CountDownTimer;
import android.webkit.JavascriptInterface;
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
	@JavascriptInterface
	public void loadQuiz() {
		// TODO Auto-generated method stub
//		File filelist = activity.getFilelist();
		// File quizFileToView = new
		// File(filelist.getCanonicalPath()+"/"+course+"/Quizzes/"+quiz+"/Form/"+quiz+".html");
//		File quizFileToView = new File(".");
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		webView.addJavascriptInterface(new JavaScriptInterface(),"Android");
		// webView.loadUrl("file:///android_asset/1.html");
		webView.setWebViewClient(new WebViewClient() {

			   public void onPageFinished(WebView view, String url) {
			        // do your stuff here
				   timer.start();
			    }
			});
		webView.loadUrl("file://" + quizPath);
		
	}
	final class JavaScriptInterface
	{
		public JavaScriptInterface(){
		}
		@JavascriptInterface
		public void getAnswer(String formName,String [] items,String qType)
		{
			File studentQuizFile = new File(quizPath);
			FileInputStream in;
			try {
				in = new FileInputStream(studentQuizFile);
				HtmlParser studentQuiz = new HtmlParser(in);
				NodeList forms = studentQuiz.document.getElementsByTagName("form");
				for(int i=0;i<forms.getLength();i++)
				{
					if(((Element)forms.item(i)).getAttribute("name").equals(formName))
					{
						switch (qType){
						case Constants.MULTIPLE_CHOICE:
						case Constants.SINGLE_CHOICE:
							NodeList itemsInCurrentFile = ((Element)studentQuiz.document.getElementsByTagName("form").item(i)).getElementsByTagName("input");
							
							for(int j=0;j<itemsInCurrentFile.getLength();j++)
							{
								if(items[j].equals("checked"))
								((Element)itemsInCurrentFile.item(j)).setAttribute("checked", "checked");
								else
									((Element)itemsInCurrentFile.item(j)).removeAttribute("checked");
							}
							break;
						
						case Constants.FREE_TEXT:
							Element textInCurrentFile = (Element) ((Element)forms.item(i)).getFirstChild();
						
							textInCurrentFile.setTextContent(items[0]);
							break;
						case Constants.FREE_DRAW:
							//Handle free draw question
							
							
							//
							break;
						}
						studentQuiz.writeHtml(quizPath);
						webView.loadUrl("file://" + quizPath);
					}
				}
			} catch (FileNotFoundException | TransformerException e) {e.printStackTrace();}
		
			
		}
	
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
