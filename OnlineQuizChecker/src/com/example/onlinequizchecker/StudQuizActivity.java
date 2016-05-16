package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.onlinequizchecker.ServerBT.ConnectedThread;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class StudQuizActivity{
	private MainActivity activity;
	private WebView webView;
	private TextView timeLeftText;
	private final CounterClass timer;
	private String quizPath;
	private Button submit;
	private CharSequence studentId;
	private String applicationPath;
	private ClientBT clientBT;
	private TextToSpeech ttobj;
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private TriggerEventListener mTriggerEventListener;
	
	public StudQuizActivity(MainActivity activity, int timePeriod,
			CharSequence studentId, String quizPath, String applicationPath, ClientBT clientBT) {
		super();
		this.activity = activity;
		this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);  
		this.activity.setContentView(R.layout.stud_quizview);
		this.quizPath = quizPath;
		this.studentId = studentId;
		this.applicationPath = applicationPath;
		this.clientBT = clientBT;
		this.submit = (Button)this.activity.findViewById(R.id.submitBtn);
		
		webView = (WebView) this.activity.findViewById(R.id.quizWebView);
		timeLeftText = (TextView) this.activity.findViewById(R.id.timeLeftTxt);
		timer = new CounterClass(timePeriod*60000, 1000);
		submit.setOnClickListener(new submitBtnListener());
		
		initTextToSpeech();
		initMotionSensor();
		
		
		loadQuiz();
	}
	
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
	@SuppressLint("NewApi")
	private void initMotionSensor() {
		
		mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);

		mTriggerEventListener = new TriggerEventListener() {
		    @Override
		    public void onTrigger(TriggerEvent event) {
		        // Do work
		    	String message = Constants.MOVING+"-"+studentId;
		    	byte[] buffer = toByteArray(message);
		    	showAlertDialog("Please return to your sit!");
		    	clientBT.mConnectedThread.write(buffer);
		    	mSensorManager.requestTriggerSensor(mTriggerEventListener, mSensor);
		    }
		};

		mSensorManager.requestTriggerSensor(mTriggerEventListener, mSensor);
	}
	  private byte[] toByteArray(CharSequence charSequence) {
          if (charSequence == null) {
            return null;
          }
          byte[] bytesArray = new byte[charSequence.length()];
          for (int i = 0; i < bytesArray.length; i++) {
          	bytesArray[i] = (byte) charSequence.charAt(i);
          }

          return bytesArray;
      }
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
		webView.addJavascriptInterface(new JavaScriptInterface(this),"Android");
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
		private StudQuizActivity controller;
		public JavaScriptInterface(StudQuizActivity controller){
			this.controller=controller;
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
							NodeList textArea = ((Element)studentQuiz.document.getElementsByTagName("form").item(i)).getElementsByTagName("textarea");

							Element textInCurrentFile = (Element)textArea.item(0);
						
							textInCurrentFile.setTextContent(items[0]);
							break;
						case Constants.FREE_DRAW:
							//Handle free draw question
							
							
							//
							break;
						}
						studentQuiz.writeHtml(quizPath);
//						webView.loadUrl("file://" + quizPath);
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
			    	 new StudDrawingBoardController(activity,controller, formName, clientBT.quizPathToZip);
			     }
			});
			//
		}
		@SuppressWarnings("deprecation")
		@JavascriptInterface
		public void listen(String toSpeak)
		{
			ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
		}
	}
	 static class Utils {

		    public static void runOnUiThread(Runnable runnable){
		        final Handler UIHandler = new Handler(Looper.getMainLooper());
		        UIHandler .post(runnable);
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
	public void updateQuizAfterDrawing(String qNumber)
	{
		activity.setContentView(R.layout.stud_quizview);
		webView = (WebView) activity.findViewById(R.id.quizWebView);
		timeLeftText = (TextView) activity.findViewById(R.id.timeLeftTxt);
		
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		webView.addJavascriptInterface(new JavaScriptInterface(this),"Android");
		// webView.loadUrl("file:///android_asset/1.html");
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
	
	class submitBtnListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {		
			zipFileManager.createZipFile(new File(ClientBT.quizPathToZip), applicationPath+"/"+studentId+".zip");
			FileInputStream fileInputStream=null;
	        
	        File file = new File(applicationPath+"/"+studentId+".zip");
			boolean shit = file.exists();
	        int fileSize = (int) file.length();
	        byte[] bFile = new byte[ String.valueOf(fileSize).length()+fileSize+1];
	        
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
			// fileInputStream.read(bFile);
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
		    for (int l = 0; l < fileSize; l++) {
				bFile[bIndex] = readFile[l];
				bIndex++;
			}
		    
		    clientBT.mConnectedThread.write(bFile);
		
		}
	}

}
