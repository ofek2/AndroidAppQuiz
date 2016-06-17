package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class StudQuizActivity.
 */
public class StudQuizActivity{
	
	/** The activity. */
	private MainActivity activity;
	
	/** The web view. */
	private WebView webView;
	
	/** The time left text. */
	private TextView timeLeftText;
	
	/** The timer. */
	private CounterClass timer;
	
	public static int timePeriod;
	
	/** The quiz path. */
	private String quizPath;
	
	/** The submit. */
	private Button submit;
	
	/** The student id. */
	public static CharSequence studentId;
	
	/** The client bt. */
	private ClientBT clientBT;
	
	/** The ttobj. */
	private TextToSpeech ttobj;
	
	/** The sensor motion. */
	private MotionSensor motionSensor;

	public static AlertDialog alert = null;

/** The submited. */
	public static boolean submited;
	
	/**
 * Instantiates a new stud quiz activity.
 *
 * @param activity the activity
 * @param timePeriod the time period
 * @param studentId the student id
 * @param quizPath the quiz path
 * @param applicationPath the application path
 * @param clientBT the client bt
 */
@SuppressWarnings("static-access")
public StudQuizActivity(MainActivity activity, int timePeriod,
			CharSequence studentId, String quizPath, String applicationPath, ClientBT clientBT) {
		super();
		this.activity = activity;
		this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);  
		this.activity.setContentView(R.layout.stud_quizview);
		this.quizPath = quizPath;
		this.studentId = studentId;
		this.clientBT = clientBT;
		this.submit = (Button)this.activity.findViewById(R.id.submitBtn);
		submited = false;
		alert = null;
		webView = (WebView) this.activity.findViewById(R.id.quizWebView);
		this.timePeriod = timePeriod;
		timeLeftText = (TextView) this.activity.findViewById(R.id.timeLeftTxt);
		timer = new CounterClass(this.timePeriod *60000, 1000);
		submit.setOnClickListener(new submitBtnListener());
		
		initTextToSpeech();
		motionSensor = new MotionSensor(this.activity,clientBT,studentId);
		
		loadQuiz(true);
	Intent intent = new Intent(activity,ClosingService.class);
	activity.startService(intent);
	
	}
	
	/**
	 * Inits the text to speech.
	 */
	private void initTextToSpeech() {
		
		ttobj=new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
			   @Override
			   public void onInit(int status) {
				   ttobj.setLanguage(Locale.US);
			   }
			}
			);
		ttobj.setSpeechRate(0.9f);
	}
	
	/**
	 * Show alert dialog.
	 *
	 * @param message the message
	 */
	public void showAlertDialog(String message) {

	    AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
	    alertDialog.setTitle("Alert");
	    alertDialog.setMessage(message);
	    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    dialog.dismiss();
	                    Intent intent = new Intent(Intent.ACTION_MAIN);
	                    intent.addCategory(Intent.CATEGORY_HOME);
	                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                    activity.startActivity(intent);
	                }
	            });

	    alertDialog.show();
	}
	
	/**
	 * Load quiz.
	 */
	@JavascriptInterface
	public void loadQuiz(boolean initializeTimer) {
		// TODO Auto-generated method stub
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		webView.addJavascriptInterface(new JavaScriptInterface(this),"Android");
		if(initializeTimer)
			timer.start();
		webView.loadUrl("file://" + quizPath);
		ClientBT.quizWasInitiated = true;
	}
	
	/**
	 * The Class JavaScriptInterface.
	 */
	final class JavaScriptInterface
	{
		
		/** The controller. */
		private StudQuizActivity controller;
		
		/**
		 * Instantiates a new java script interface.
		 *
		 * @param controller the controller
		 */
		public JavaScriptInterface(StudQuizActivity controller){
			this.controller=controller;
		}
		
		/**
		 * Gets the answer.
		 *
		 * @param formName the form name
		 * @param items the items
		 * @param qType the question type
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
							NodeList textArea = ((Element)studentQuiz.document.getElementsByTagName("form").item(i)).getElementsByTagName("textarea");

							Element textInCurrentFile = (Element)textArea.item(0);
						
							textInCurrentFile.setTextContent(items[0]);
							break;
						}
						studentQuiz.writeHtml(quizPath);
					}
				}
			} catch (FileNotFoundException | TransformerException e) {e.printStackTrace();}
		
			
		}
		
		/**
		 * Open drawing board.
		 *
		 * @param formName the form name
		 */
		@JavascriptInterface
		public void openDrawingBoard(final String formName)
		{
			Utils.runOnUiThread(new Runnable() {
			     @SuppressWarnings("static-access")
				@Override
			     public void run() {
			    	 new StudDrawingBoardController(activity,controller, formName, clientBT.quizPathToZip);
			     }
			});
		}
		
		/**
		 * Listen.
		 *
		 * @param toSpeak the text to speak
		 */
		@SuppressWarnings("deprecation")
		@JavascriptInterface
		public void listen(String toSpeak)
		{
			ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
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
	 * The Class CounterClass.
	 */
	public class CounterClass extends CountDownTimer {

		/**
		 * Instantiates a new counter class.
		 *
		 * @param millisInFuture the millis in future
		 * @param countDownInterval the count down interval
		 */
		public CounterClass(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		/* (non-Javadoc)
		 * @see android.os.CountDownTimer#onTick(long)
		 */
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

		/* (non-Javadoc)
		 * @see android.os.CountDownTimer#onFinish()
		 */
		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			submitQuizAndExit();
		}

	}
	
	/**
	 * Update quiz after drawing.
	 *
	 * @param qNumber the q number
	 */
	public void updateQuizAfterDrawing(String qNumber)
	{
		activity.setContentView(R.layout.stud_quizview);
		webView = (WebView) activity.findViewById(R.id.quizWebView);
		timeLeftText = (TextView) activity.findViewById(R.id.timeLeftTxt);
		webView.clearCache(true);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		webView.addJavascriptInterface(new JavaScriptInterface(this),"Android");
		this.submit = (Button)activity.findViewById(R.id.submitBtn);
		submit.setOnClickListener(new submitBtnListener());
		
		File studentQuizFile = new File(quizPath);
		FileInputStream in;

			try {
				in = new FileInputStream(studentQuizFile);
				HtmlParser studentQuiz = new HtmlParser(in);
				NodeList studentDrawings = studentQuiz.document.getElementsByTagName("studentdrawing"+qNumber);
				Element studentDrawing = (Element)studentDrawings.item(0);
				if(studentDrawing.getChildNodes().getLength()==1) //because of the empty textNode we add to the tag..
				{
					studentDrawing.removeChild(studentDrawing.getFirstChild());
					Element img = studentQuiz.document.createElement("img");
					img.setAttribute("src", "SDraw"+qNumber+".PNG");
					studentDrawing.appendChild(img);
				}
				studentQuiz.writeHtml(quizPath);
				webView.loadUrl("file://" + quizPath);
			} catch (FileNotFoundException | TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				
			
	}
	
	/**
	 * The listener interface for receiving submitBtn events.
	 * The class that is interested in processing a submitBtn
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addsubmitBtnListener<code> method. When
	 * the submitBtn event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see submitBtnEvent
	 */
	class submitBtnListener implements View.OnClickListener
	{
		private AlertDialog.Builder builder;
		public submitBtnListener() {
			builder = new AlertDialog.Builder(activity);

		    builder.setTitle("Confirm");
		    builder.setMessage("Are you sure you want to submit your quiz?");

		    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    	@Override
		        public void onClick(DialogInterface dialog, int which) {
		            // Do nothing but close the dialog		        	
		            dialog.dismiss();
		            submitQuizAndExit();
		        }
		    });

		    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            // Do nothing
		            dialog.dismiss();
		        }
		    });
			
		}
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
		    alert = builder.create();
		    alert.show();
			
		}
	}
	
	/**
	 * Submit quiz and exit.
	 */
	private void submitQuizAndExit()
	{
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
		if(clientBT.mConnectedThread!=null)
		{
			submited = true;
			ClientBT.quizWasInitiated = false;
			zipFileManager.createZipFile(new File(ClientBT.quizPathToZip), ClientBT.quizPathToZip+"/"+studentId+".zip",false);        
			submit.setOnClickListener(null);
			if(alert!=null)
				alert.dismiss();
	    	clientBT.mConnectedThread.write(zipToByteArray(ClientBT.quizPathToZip+"/"+studentId+".zip",ClientBT.pathToSend));
	    	ClosingService.thread.interrupt();
			motionSensor.getSensorManager().unregisterListener(motionSensor);
		}
		else
		{
			if(alert!=null)
				alert.dismiss();
			activity.setContentView(R.layout.stud_reconnection);
			new CountDownTimer(15000, 1000) {

				public void onTick(long millisUntilFinished) {
					if(clientBT.mConnectedThread!=null)
					{
						cancel();
						zipFileManager.createZipFile(new File(ClientBT.quizPathToZip), ClientBT.quizPathToZip + "/" + studentId + ".zip",false);
						submited = true;
						ClientBT.quizWasInitiated = false;
						clientBT.mConnectedThread.write(zipToByteArray(ClientBT.quizPathToZip+"/"+studentId+".zip",ClientBT.pathToSend));
						ClosingService.thread.interrupt();
						motionSensor.getSensorManager().unregisterListener(motionSensor);
					}
				}

				@SuppressWarnings("static-access")
				public void onFinish() {
					if(!submited) {
						zipProtectedFile.createZipFileFromSpecificFiles(activity.zipFilesPassword, studentId, ClientBT.quizPathToZip + "/" + studentId + ".zip", ClientBT.quizPathToZip);
						submited = true;
						ClientBT.quizWasInitiated = false;
						Toast.makeText(activity.getApplicationContext(), "Your quiz was successfully saved on your storage",
								Toast.LENGTH_LONG).show();
						ClosingService.thread.interrupt();
						motionSensor.getSensorManager().unregisterListener(motionSensor);						
						new MainController(activity);
					}
				}
			}.start();
		}
	}

	public static byte[] zipToByteArray(String quizPath,String pathToSend) {
		// TODO Auto-generated method stub
		FileInputStream fileInputStream=null;
        File file = new File(quizPath);
        int fileSize = (int) file.length();
        byte[] bFile = new byte[ pathToSend.length()+String.valueOf(fileSize).length()+fileSize+2];
        
        //convert file into array of bytes
        byte[] readFile = new byte[fileSize];
        //convert file into array of bytes
		try {
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(readFile);
			fileInputStream.close();
			file.delete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    int bIndex=0;
	    for (int k = 0; k < String.valueOf(fileSize).length(); k++) {
			bFile[bIndex] = (byte)String.valueOf(fileSize).charAt(k);
			bIndex++;
		}
	    bFile[bIndex] = (byte)'-';
	    bIndex++;
	    for (int j = 0; j < pathToSend.length(); j++) {
			bFile[bIndex] = (byte)pathToSend.charAt(j);
			bIndex++;
		}
	    bFile[bIndex] = (byte)'-';
	    bIndex++;
	    for (int l = 0; l < fileSize; l++) {
			bFile[bIndex] = readFile[l];
			bIndex++;
		}
	    return bFile;
	}
	public void enableQuiz()
	{
		activity.setContentView(R.layout.stud_quizview);
		this.submit = (Button)this.activity.findViewById(R.id.submitBtn);
		submited = false;
		webView = (WebView) this.activity.findViewById(R.id.quizWebView);
		webView.clearCache(true);
		timeLeftText = (TextView) this.activity.findViewById(R.id.timeLeftTxt);
		submit.setOnClickListener(new submitBtnListener());		
		initTextToSpeech();
		motionSensor = new MotionSensor(this.activity,clientBT,studentId);		
		loadQuiz(false);
	}
}
