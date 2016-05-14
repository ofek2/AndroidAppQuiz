package com.example.onlinequizchecker;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

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

    public LectQuizProgressController(MainActivity activity)
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
                Toast toast = Toast.makeText(activity.getApplicationContext(), (String) parent.getItemAtPosition(position)  + " hasn't finished yet",
                        Toast.LENGTH_SHORT);
                toast.show();
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
}
