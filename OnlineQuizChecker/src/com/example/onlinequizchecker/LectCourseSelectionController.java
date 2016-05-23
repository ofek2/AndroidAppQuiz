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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 311165906 on 10/03/2016.
 */
public class LectCourseSelectionController{
    private MainActivity activity;
    private Spinner spinner;
    private Button connectionBtn;
    private Button logoutBtn;
    public LectCourseSelectionController(MainActivity activity) {
        this.activity = activity;
        initView();
    }

	private void initView() {
		// TODO Auto-generated method stub
		ArrayList<String> courses = getCoursesFromDB();
		if (courses.size()==0) {
			new MainController(activity);
			Toast.makeText(activity.getApplicationContext(), "There are no appropriate courses to view",
			Toast.LENGTH_LONG).show();
		}
		else
		{
	        activity.setContentView(R.layout.lect_courseselectionview);
	        activity.setUserClassification("Lecturer");
	        spinner = (Spinner)activity.findViewById(R.id.coursesSpinner);
	        connectionBtn = (Button)activity.findViewById(R.id.connectionBtn);
	        connectionBtn.setOnClickListener(new connectionBtnListener());
	        logoutBtn = (Button)activity.findViewById(R.id.logoutBtn);
	        logoutBtn.setOnClickListener(new logoutBtnListener());
	        populateSpinner(courses);
		}
		
	}

	private ArrayList<String> getCoursesFromDB() {
		// TODO Auto-generated method stub
		
		File filelist;
		try {
			String path = activity.getApplicationContext().getFilesDir().getCanonicalPath()+"/"+Constants.APP_NAME;
			filelist = new File(path);
			activity.setFilelist(filelist);
			ArrayList<String> courses = new ArrayList<>();
			if(filelist.list()!=null)
			for (int i=0;i<filelist.list().length;i++)
			{
		     	try {
	    		String course = filelist.list()[i];
				File quizzesFolder = new File(filelist.getCanonicalPath()+"/"+course+"/Quizzes");
				File studentsFolder = new File(filelist.getCanonicalPath()+"/"+course+"/Students");
				if(quizzesFolder.list().length > 0 && studentsFolder.list().length > 0)
				{
					courses.add(course);
				}
				else
				{
					;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			return courses;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;
	
	}

	private void populateSpinner(ArrayList<String> courses) {
		// TODO Auto-generated method stub
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, 
		           R.layout.simple_spinner_item,courses);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
	}
	
	 class connectionBtnListener implements View.OnClickListener
     {
        @Override
        public void onClick(View v) {
          
            new LectStudentRegListController(activity,spinner.getSelectedItem().toString());
        }
     }
	 class logoutBtnListener implements View.OnClickListener
	 {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new MainController(activity);
		}
		 
	 }
}
