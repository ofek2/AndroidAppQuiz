package com.example.onlinequizchecker;

import android.app.ListActivity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by 311165906 on 10/03/2016.
 */
public class LectStudentRegistrationController{
    private MainActivity activity;
    private Spinner spinner;
    private Button connectionBtn;
    public LectStudentRegistrationController(MainActivity activity) {
        this.activity = activity;
        this.activity.setContentView(R.layout.lect_studentregistrationview);
        spinner = (Spinner)activity.findViewById(R.id.coursesSpinner);
        connectionBtn = (Button)activity.findViewById(R.id.connectionBtn);
        connectionBtn.setOnClickListener(new connectionBtnListener());
        initView();
//        // -- Display mode of the ListView
//        ArrayList<String> students = new ArrayList<String>();
//        ListView listview= (ListView)activity.findViewById(R.id.studentListView);
//        //	listview.setChoiceMode(listview.CHOICE_MODE_NONE);
//        //	listview.setChoiceMode(listview.CHOICE_MODE_SINGLE);
//        listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);
//
//        //--	text filtering
//        listview.setTextFilterEnabled(true);
//
//        listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, students));
       
    }

	private void initView() {
		// TODO Auto-generated method stub
		ArrayList<String> courses = getCoursesFromDB();
		populateSpinner(courses);
	}

	private ArrayList<String> getCoursesFromDB() {
		// TODO Auto-generated method stub
		//DropBoxSimple.
		ArrayList<String> courses = new ArrayList<>();
		courses.add("314152,History");
		courses.add("314152,History");
		courses.add("314152,History");
		return courses;
	}

	private void populateSpinner(ArrayList<String> courses) {
		// TODO Auto-generated method stub
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, 
		           R.drawable.simple_spinner_item,courses);
	
		spinner.setAdapter(adapter);
		
	}
	
	 class connectionBtnListener implements View.OnClickListener
	    {

	        @Override
	        public void onClick(View v) {
	          
	            new LectStudentRegListController(activity);
	        }
	    }
}
