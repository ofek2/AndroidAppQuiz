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
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

/**
 * The Class LectViewQuizController.
 */
public class LectViewQuizController {
	
	/** The activity. */
	private MainActivity activity;
	
	/** The web view. */
	private WebView webView;
	
	/** The back. */
	private Button back;
	
	/** The filelist. */
	private File filelist;
	
	/** The course. */
	private String course;
	
	/** The selected index. */
	private int selectedIndex;
	
	/** The quiz. */
	private String quiz;
	
	/** The quiz file to view. */
	private File quizFileToView;
	
	/** The temp. */
	private LectViewQuizController temp;
	
	/** The prev controller1. */
	private LectQuizSelectionController prevController1;
	
	/** The prev controller2. */
	private LectQuizProgressController prevController2;
	
	/** The prev listener. */
	private View.OnClickListener prevListener;
	
	/**
	 * Instantiates a new lect view quiz controller.
	 *
	 * @param activity the activity
	 * @param previousController the previous controller
	 * @param course the course
	 * @param quiz the quiz
	 * @param selectedIndex the selected index
	 */
	public LectViewQuizController(MainActivity activity,LectQuizSelectionController previousController,String course,String quiz, int selectedIndex)
	{
		temp=this;
		this.prevController1 = previousController;
		this.activity = activity;
		this.activity.hideKeyboard();
		this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
		this.course = course;
		this.quiz=quiz;
		this.activity.setContentView(R.layout.lect_viewquiz);
		this.webView = (WebView)this.activity.findViewById(R.id.webView);
		this.back = (Button)this.activity.findViewById(R.id.backBtnLectViewQuiz);
		this.selectedIndex = selectedIndex;
		prevListener = new backBtnQuizSelectionListener();
		back.setOnClickListener(prevListener);
		loadQuiz(course,quiz);
	}
	
	/**
	 * Instantiates a new lect view quiz controller.
	 *
	 * @param activity the activity
	 * @param previousController the previous controller
	 * @param course the course
	 * @param quiz the quiz
	 */
	public LectViewQuizController(MainActivity activity,LectQuizProgressController previousController,String course,String quiz)
	{
		temp=this;
		this.prevController2 = previousController;
		this.activity = activity;
		this.course = course;
		this.quiz=quiz;
		this.activity.setContentView(R.layout.lect_viewquiz);
		this.webView = (WebView)this.activity.findViewById(R.id.webView);
		this.back = (Button)this.activity.findViewById(R.id.backBtnLectViewQuiz);
		prevListener = new backBtnQuizProgressListener();
		back.setOnClickListener(prevListener);
		loadQuiz(course,quiz);
	}
	
	/**
	 * Load quiz.
	 *
	 * @param course the course
	 * @param quiz the quiz
	 */
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
			webView.clearCache(true);
			webView.addJavascriptInterface(new JavaScriptInterface(quizFileToView.getCanonicalPath()),"Android");
			
			webView.loadUrl("file://"+quizFileToView.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * The Class JavaScriptInterface.
	 */
	final class JavaScriptInterface
	{
		
		/** The quiz path. */
		private String quizPath;
		
		/**
		 * Instantiates a new java script interface.
		 *
		 * @param quizPath the quiz path
		 */
		public JavaScriptInterface(String quizPath){
			this.quizPath=quizPath;
		}
		
		/**
		 * Gets the answer.
		 *
		 * @param formName the form name
		 * @param items the items
		 * @param qType the q type
		 * @return the answer
		 */
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
	 
 	/**
 	 * The Class Utils.
 	 */
 	static class Utils {

	    /**
    	 * Run on ui thread.
    	 *
    	 * @param runnable the runnable
    	 */
    	public static void runOnUiThread(Runnable runnable){
	        final Handler UIHandler = new Handler(Looper.getMainLooper());
	        UIHandler .post(runnable);
	    } 
	}

	/**
	 * The listener interface for receiving backBtnQuizSelection events.
	 * The class that is interested in processing a backBtnQuizSelection
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addbackBtnQuizSelectionListener<code> method. When
	 * the backBtnQuizSelection event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see backBtnQuizSelectionEvent
	 */
	class backBtnQuizSelectionListener implements View.OnClickListener
	{
		
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING); 
			prevController1.retrieveView();
		}
		
	}
	
	/**
	 * The listener interface for receiving backBtnQuizProgress events.
	 * The class that is interested in processing a backBtnQuizProgress
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addbackBtnQuizProgressListener<code> method. When
	 * the backBtnQuizProgress event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see backBtnQuizProgressEvent
	 */
	class backBtnQuizProgressListener implements View.OnClickListener
	{
		
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
			prevController2.retrieveView();
		}
		
	}
}
