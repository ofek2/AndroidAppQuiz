package com.example.onlinequizchecker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.onlinequizchecker.LectMessageHandler.toByteArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.example.onlinequizchecker.StudQuizActivity.CounterClass;

import org.w3c.dom.Text;

/**
 * Created by 311165906 on 08/05/2016.
 */
public class LectQuizProgressController {
    private MainActivity activity;
    private ArrayList<String> studentsInClass;
    public static ListView listView;
    private ArrayAdapter<String> adapter;
    public static Button finishBtn = null;
    private Button viewQuizBtn;
	
	private CounterClass timer;
	
	private int timePeriod;
	
	private TextView timeLeftText;
	private TextView timeLeftLbl;
	private boolean timeIsUp = false;
	public static Object lock;
	private boolean canFinish;
	private boolean startUploading;
	public static AlertDialog finishButtonAlert = null;
	public static AlertDialog reconnectionDialog = null;
    public LectQuizProgressController(MainActivity activity,int timePeriod)
    {
        this.activity=activity;
        studentsInClass = LectQuizInitiationController.studentsInClass;
        this.activity.setContentView(R.layout.lect_quizprogressview);
        listView = (ListView)activity.findViewById(R.id.studentsFinalListView);
        populateList(studentsInClass);
        finishBtn = (Button)activity.findViewById(R.id.finishBtn);
        finishBtn.setOnClickListener(new finishBtnListener());
        viewQuizBtn = (Button)activity.findViewById(R.id.viewQuizBtn);
        viewQuizBtn.setOnClickListener(new viewQuizBtnListener());
        
    	this.timePeriod = timePeriod;
		timeLeftText = (TextView) this.activity.findViewById(R.id.timeLeftTxtLect);
		timeLeftLbl = (TextView) this.activity.findViewById(R.id.timeLeftLblLect);
//		timer = new CounterClass(this.timePeriod *60000, 1000);
		lock = new Object();
		canFinish = false;
		startUploading = false;
		finishButtonAlert = null;
		reconnectionDialog = null;
		timer = new CounterClass(30000, 1000);
		timer.start();
    }
    private void populateList(ArrayList<String> students) {
        // TODO Auto-generated method stub
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
        listView.setTextFilterEnabled(true);
        adapter = new ArrayAdapter<String>(this.activity,
                android.R.layout.simple_list_item_multiple_choice, students);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new itemListener());
        
        //listview.setItemChecked(2,true);

    }
    class viewQuizBtnListener implements View.OnClickListener
    {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new LectViewQuizController(activity, LectQuizProgressController.this,LectQuizInitiationController.course, LectQuizInitiationController.quiz);
		}
    	
    }
    class itemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub

            if (!listView.isItemChecked(position)){
                listView.setItemChecked(position, true);
                Toast toast = Toast.makeText(activity.getApplicationContext(), (String) parent.getItemAtPosition(position) + " has finished",
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                listView.setItemChecked(position, false);
                if(listView.getChildAt(position).getDrawingCacheBackgroundColor()== Constants.STUDENT_IS_MOVING_COLOR)
                {
                	// Do something to enable the moving student's quiz.
                	for(int i = 0;i<LectStudentRegListController.serverBT.mConnThreads.size();i++)
                	{
                		String studentId = (String) parent.getItemAtPosition(position);
                		if(studentId.equals(LectStudentRegListController.serverBT.mConnThreads.get(i).getStudentId()))
                		{
                			String message = "Enable Quiz";
                			byte[] buffer = toByteArray(message);
							if (LectStudentRegListController.serverBT.mConnThreads.get(i)!=null)
                				LectStudentRegListController.serverBT.mConnThreads.get(i).write(buffer);
							else
							{
								Toast toast = Toast.makeText(activity.getApplicationContext(), (String) parent.getItemAtPosition(position) + " has disconnected",
										Toast.LENGTH_SHORT);
								toast.show();
							}
                		}
                	}
                	listView.getChildAt(position).setBackgroundColor(0);
                	listView.getChildAt(position).setDrawingCacheBackgroundColor(0);
                }
                else
                {
                	Toast toast = Toast.makeText(activity.getApplicationContext(), (String) parent.getItemAtPosition(position)  + " hasn't finished yet",
                        Toast.LENGTH_SHORT);
                	toast.show();
                }
            }

        }

    }
    class finishBtnListener implements View.OnClickListener
    {
//    	private boolean canFinish = false;
        @Override
        public void onClick(View v) {
            // handle quiz ending.
//        	try {
        	synchronized (lock) {
            	if(!timeIsUp)
            	{
            			if(!isAllChecked())
            			{      				
//            				canFinish = false;
//            				Toast toast = Toast.makeText(activity.getApplicationContext(),  "Please wait for all students to finish their quiz",
//                                    Toast.LENGTH_LONG);
//                            toast.show();
                            toFinish();
            			}
            			else
            			{
//            				canFinish = true;  
            				new LectUploadProgress(activity,timer);
            			}          				
//            			new UploadFolderDB(activity.getApplicationContext().getFilesDir().getCanonicalPath(),activity).
//            				execute(activity.getFilelist().getCanonicalPath() + "/OnlineQuizChecker.zip", "/");
            	}
            	else
            	{
        			if(!startUploading)
        			{
            		if(!isAllChecked()&&!canFinish)
        			{
        				Toast toast = Toast.makeText(activity.getApplicationContext(),  "Reconnecting to the students",
                                Toast.LENGTH_SHORT);
                        toast.show();
        			}
        			else if (canFinish) {
        				toFinish();
						}       				
        			}
//        			else
//        				toFinish();
            	}
			}

//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
    }
    private void toFinish()
    {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity);

	    builder.setTitle("Confirm");
	    builder.setMessage("Do you want to continue?");
	    
	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	@Override
	        public void onClick(DialogInterface dialog, int which) {
	            // Do nothing but close the dialog
	        	
	            dialog.dismiss();
//	            canFinish = true;  

	            startUploading = true;
				new LectUploadProgress(activity,timer);
	        }
	    });

	    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

	        @Override
	        public void onClick(DialogInterface dialog, int which) {

	            // Do nothing
	            dialog.dismiss();
	        }
	    });
	    finishButtonAlert = builder.create();
	    finishButtonAlert.show();
    }

    private boolean isAllChecked()
    {
		for (int i = 0; i < studentsInClass.size(); i++) {
			if(!listView.isItemChecked(i))
			{
				return false;
			}
		}
		return true;
    }
    
    public void retrieveView()
    {
    	activity.setContentView(R.layout.lect_quizprogressview);
    	
        finishBtn = (Button)activity.findViewById(R.id.finishBtn);
        finishBtn.setOnClickListener(new finishBtnListener());
        viewQuizBtn = (Button)activity.findViewById(R.id.viewQuizBtn);
        viewQuizBtn.setOnClickListener(new viewQuizBtnListener());
		timeLeftLbl = (TextView) activity.findViewById(R.id.timeLeftLblLect);
		timeLeftText = (TextView) activity.findViewById(R.id.timeLeftTxtLect);
    	ListView tempListView = (ListView) activity.findViewById(R.id.studentsFinalListView);
    	tempListView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
    	tempListView.setTextFilterEnabled(true);
    	tempListView.setAdapter(adapter);
    	for(int i=0; i < adapter.getCount();i++)
    		tempListView.setItemChecked(i, listView.isItemChecked(i));
    	tempListView.setOnItemClickListener(new itemListener());
    	listView = tempListView;
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
			synchronized (lock) {
				timeIsUp = true;
			}
//			timeIsUp = true;
//			showAlertDialog("Trying to reconnect to the students that\n have lost the connection");
			if(!canFinish)
			{
			if(!isAllChecked())
			{
				/////////////////////////////////////
//				activity.setContentView(R.layout.stud_reconnection);
				showAlertDialog("Trying to reconnect to the students that\n have lost the connection");
				timeLeftLbl.setText("Reconnection ends in:");
				new CountDownTimer(15000, 1000) {
//					boolean canFinish = false;
					public void onTick(long millisUntilFinished) {
						long millis = millisUntilFinished;
						String ms = String.format("%02d:%02d",
								TimeUnit.MILLISECONDS.toMinutes(millis)
										- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
								TimeUnit.MILLISECONDS.toSeconds(millis)
										- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
						System.out.println(ms);
						timeLeftText.setText(ms);
						if(isAllChecked())
						{
//							canFinish = true;
//							alert.cancel();
							startUploading = true;
							cancel();
							new LectUploadProgress(activity,null);
						}
						else if(startUploading)
							cancel();
					}

					public void onFinish() {
						timeLeftText.setText("00:00");
						if(!startUploading) {
							showAlertDialog("Not all of the students have managed to submit their answers"
									+ ",\n please press finish to upload the existing answers");
							canFinish = true;
						}
					}
				}.start();
				//////////////////////////////////////////////////
				
			}
			else
			{
//				canFinish = true;
				startUploading = true;
				new LectUploadProgress(activity,null);
			}
			}
		

	}
	public void showAlertDialog(String message) {
		if(reconnectionDialog!=null)
		{
			reconnectionDialog.dismiss();
			reconnectionDialog = null;
		}
        reconnectionDialog = new AlertDialog.Builder(activity).create();
        reconnectionDialog.setTitle("Alert");
        reconnectionDialog.setMessage(message);
        reconnectionDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.addCategory(Intent.CATEGORY_HOME);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        activity.startActivity(intent);
                    }
                });

        reconnectionDialog.show();
    }
}
}
