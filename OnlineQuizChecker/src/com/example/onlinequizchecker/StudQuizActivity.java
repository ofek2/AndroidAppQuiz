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
import android.bluetooth.BluetoothAdapter;
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
import android.util.TimingLogger;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
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
	private final CounterClass timer;
	
	/** The quiz path. */
	private String quizPath;
	
	/** The submit. */
	private Button submit;
	
	/** The student id. */
	private CharSequence studentId;
	
	/** The application path. */
	private String applicationPath;
	
	/** The client bt. */
	private ClientBT clientBT;
	
	/** The ttobj. */
	private TextToSpeech ttobj;
	
	/** The sensor motion. */
	private SensorMotion sensorMotion;

/** The submited. */
//	private BluetoothAdapter bluetoothAdapter;
	public static boolean submited;
//	private SensorManager mSensorManager;
//	private Sensor mSensor;
//	private TriggerEventListener mTriggerEventListener;
	
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
		submited = false;
//		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		webView = (WebView) this.activity.findViewById(R.id.quizWebView);
		timeLeftText = (TextView) this.activity.findViewById(R.id.timeLeftTxt);
		timer = new CounterClass(timePeriod*60000, 1000);
		submit.setOnClickListener(new submitBtnListener());
		
		initTextToSpeech();
//		initMotionSensor();
		sensorMotion = new SensorMotion(this.activity,clientBT,studentId);
		
		loadQuiz();
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
//	@SuppressLint("NewApi")
//	private void initMotionSensor() {
//		
//		mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
//		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//
////		mTriggerEventListener = new TriggerEventListener() {
////		    @Override
////		    public void onTrigger(TriggerEvent event) {
////		        // Do work
////		    	String message = Constants.MOVING+"-"+studentId;
////		    	byte[] buffer = toByteArray(message);
////		    	showAlertDialog("Please return to your sit!");
////		    	clientBT.mConnectedThread.write(buffer);
////		    	mSensorManager.requestTriggerSensor(mTriggerEventListener, mSensor);
////		    }
////		};
////
////		mSensorManager.requestTriggerSensor(mTriggerEventListener, mSensor);
/**
 * To byte array.
 *
 * @param charSequence the char sequence
 * @return the byte[]
 */
//	}
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
		
		/**
		 * Open drawing board.
		 *
		 * @param formName the form name
		 */
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

		    AlertDialog alert = builder.create();
		    alert.show();
			
		}
	}
	
	/**
	 * Submit quiz and exit.
	 */
	private void submitQuizAndExit()
	{
		timer.cancel();
		if(clientBT.mConnectedThread!=null)
		{
		zipFileManager.createZipFile(new File(ClientBT.quizPathToZip), applicationPath+"/"+studentId+".zip");
		FileInputStream fileInputStream=null;
        
        File file = new File(applicationPath+"/"+studentId+".zip");
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
	    submited = true;
	    if(clientBT.mConnectedThread!=null)
	    	clientBT.mConnectedThread.write(bFile);
//	    Toast.makeText(activity.getApplicationContext(), "Your quiz was successfully sent to your lecturer",
//				Toast.LENGTH_LONG).show();
	    sensorMotion.getSensorManager().unregisterListener(sensorMotion);
//		clientBT.stop();
//		bluetoothAdapter.disable();
//
//	    new MainController(activity);
		}
		else
			zipProtectedFile.createZipFile(activity.zipFilesPassword, applicationPath+"/"+studentId+".zip", ClientBT.quizPathToZip);
	}

}
