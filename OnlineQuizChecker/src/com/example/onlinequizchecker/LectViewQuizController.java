package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.onlinequizchecker.StudQuizActivity.JavaScriptInterface;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class LectViewQuizController {
	private MainActivity activity;
	private WebView webView;
	private Button back;
	private File filelist;
	private String course;
	private int selectedIndex;
	private String quiz;
	private File quizFileToView;
	private LectViewQuizController temp;
	public LectViewQuizController(MainActivity activity,String course,String quiz, int selectedIndex)
	{
		temp=this;
		
		this.activity = activity;
		this.course = course;
		this.quiz=quiz;
		this.activity.setContentView(R.layout.lect_viewquiz);
		this.webView = (WebView)this.activity.findViewById(R.id.webView);
		this.back = (Button)this.activity.findViewById(R.id.backBtnDrawingView);
		this.selectedIndex = selectedIndex;
		back.setOnClickListener(new backBtnListener());
		loadQuiz(course,quiz);
	}
	private void loadQuiz(String course,String quiz) {
		// TODO Auto-generated method stub
		filelist = activity.getFilelist();
		try {
			quizFileToView = new File(filelist.getCanonicalPath()+"/"+course+"/Quizzes/"+quiz+"/Form/"+quiz+".html");
			WebSettings settings = webView.getSettings();
			settings.setJavaScriptEnabled(true);
			settings.setBuiltInZoomControls(true);
			settings.setDisplayZoomControls(false);
//			webView.loadUrl("file:///android_asset/1.html");
			webView.addJavascriptInterface(new JavaScriptInterface(quizFileToView.getCanonicalPath()),"Android");
			
			webView.loadUrl("file://"+quizFileToView.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	final class JavaScriptInterface
	{
		private String quizPath;
		public JavaScriptInterface(String quizPath){
			this.quizPath=quizPath;
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
		@JavascriptInterface
		public void openDrawingBoard(final String formName)
		{
			Utils.runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 try {
							new StudDrawingBoardController(activity,temp, formName, (filelist.getCanonicalPath()+"/"+course+"/Quizzes/"+quiz+"/Form"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			     }
			});
			
		}
		
	}
	 static class Utils {

	    public static void runOnUiThread(Runnable runnable){
	        final Handler UIHandler = new Handler(Looper.getMainLooper());
	        UIHandler .post(runnable);
	    } 
	}

	class backBtnListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
		
			new LectQuizSelectionController(activity, course,selectedIndex);
		}
		
	}
	public void updateQuizAfterDrawing(String qNumber)
	{
		activity.setContentView(R.layout.lect_viewquiz);
		back = (Button)activity.findViewById(R.id.backBtnDrawingView);
		webView = (WebView) activity.findViewById(R.id.webView);
		//timeLeftText = (TextView) activity.findViewById(R.id.timeLeftTxt);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
//		webView.loadUrl("file:///android_asset/1.html");
		try {
			webView.addJavascriptInterface(new JavaScriptInterface(quizFileToView.getCanonicalPath()),"Android");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		File studentQuizFile = quizFileToView;
		FileInputStream in;

			try {
				in = new FileInputStream(studentQuizFile);
				HtmlParser studentQuiz = new HtmlParser(in);
				NodeList studentDrawings = studentQuiz.document.getElementsByTagName("studentdrawing"+qNumber);
				Element studentDrawing = (Element)studentDrawings.item(0);
				if(studentDrawing.getChildNodes().getLength()==1) //because of the empty textNode we add to the tag..
				{
					Element img = studentQuiz.document.createElement("img");
					img.setAttribute("src", "SDraw"+qNumber+".PNG");
					studentDrawing.appendChild(img);
				}
				else
				{
					studentDrawing.removeChild(studentDrawing.getLastChild());
					Element img = studentQuiz.document.createElement("img");
					img.setAttribute("src", "SDraw"+qNumber+".PNG");
					studentDrawing.appendChild(img);
				}
				
				studentQuiz.writeHtml(studentQuizFile.getCanonicalPath());
				webView.loadUrl("file://" + studentQuizFile.getAbsolutePath());
			} catch (TransformerException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				
			
	}
}
