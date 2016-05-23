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

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.example.onlinequizchecker.StudQuizActivity.CounterClass;

/**
 * Created by 311165906 on 08/05/2016.
 */
public class LectQuizProgressController {
    private MainActivity activity;
    private ArrayList<String> studentsInClass;
    public static ListView listView;
    private ArrayAdapter<String> adapter;
    private Button finishBtn;
    private Button viewQuizBtn;
	
	private CounterClass timer;
	
	private int timePeriod;
	
	private TextView timeLeftText;
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
//		timer = new CounterClass(this.timePeriod *60000, 1000);
		timer = new CounterClass(20000, 1000);
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
                			LectStudentRegListController.serverBT.mConnThreads.get(i).write(buffer);
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
    	private boolean canFinish = true;
        @Override
        public void onClick(View v) {
            // handle quiz ending.
//        	try {
        		for (int i = 0; i < studentsInClass.size(); i++) {
        			if(!listView.isItemChecked(i))
        			{
        				canFinish = false;
        				Toast toast = Toast.makeText(activity.getApplicationContext(),  "Please wait for all students to finish their quiz",
                                Toast.LENGTH_LONG);
                        toast.show();
        				break;
        			}
				}
        		if (canFinish) {
        			new LectUploadProgress(activity);
//        			new UploadFolderDB(activity.getApplicationContext().getFilesDir().getCanonicalPath(),activity).
//        				execute(activity.getFilelist().getCanonicalPath() + "/OnlineQuizChecker.zip", "/");
        			
        		}
        		canFinish = true;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
    }
    
    public void retrieveView()
    {
    	activity.setContentView(R.layout.lect_quizprogressview);
    	
        finishBtn = (Button)activity.findViewById(R.id.finishBtn);
        finishBtn.setOnClickListener(new finishBtnListener());
        viewQuizBtn = (Button)activity.findViewById(R.id.viewQuizBtn);
        viewQuizBtn.setOnClickListener(new viewQuizBtnListener());
        
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
			showAlertDialog("Time is up! press the finish button to end the quiz.");
			
		}

	}
	public void showAlertDialog(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.addCategory(Intent.CATEGORY_HOME);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        activity.startActivity(intent);
                    }
                });

        alertDialog.show();
    }
}
