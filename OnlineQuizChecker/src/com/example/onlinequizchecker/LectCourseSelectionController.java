package com.example.onlinequizchecker;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The Class LectCourseSelectionController.
 * This class controls the course selection screen.
 */
public class LectCourseSelectionController{
    
    /** The activity. */
    private MainActivity activity;
    
    /** The spinner. */
    private Spinner spinner;
    
    /** The connection button. */
    private Button connectionBtn;
    
    /** The logout button. */
    private Button logoutBtn;
    
    /**
     * Instantiates a new lecturer course selection controller.
     *
     * @param activity the activity
     */
    public LectCourseSelectionController(MainActivity activity) {
        this.activity = activity;
        initView();
    }

	/**
	 * Initializes the view.
	 */
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

	/**
	 * Gets the courses from Dropbox.
	 *
	 * @return the courses from Dropbox
	 */
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

	/**
	 * Populate courses spinner.
	 *
	 * @param courses the courses
	 */
	private void populateSpinner(ArrayList<String> courses) {
		// TODO Auto-generated method stub
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, 
		           R.layout.simple_spinner_item,courses);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
	}
	
	 /**
 	 * The listener interface for receiving connectionBtn events.
 	 * The class that is interested in processing a connectionBtn
 	 * event implements this interface, and the object created
 	 * with that class is registered with a component using the
 	 * component's <code>addconnectionBtnListener<code> method. When
 	 * the connectionBtn event occurs, that object's appropriate
 	 * method is invoked.
 	 *
 	 * @see connectionBtnEvent
 	 */
 	class connectionBtnListener implements View.OnClickListener
     {
        
        /* (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View v) {
          
            new LectStudentRegListController(activity,spinner.getSelectedItem().toString());
        }
     }
	 
 	/**
 	 * The listener interface for receiving logoutBtn events.
 	 * The class that is interested in processing a logoutBtn
 	 * event implements this interface, and the object created
 	 * with that class is registered with a component using the
 	 * component's <code>addlogoutBtnListener<code> method. When
 	 * the logoutBtn event occurs, that object's appropriate
 	 * method is invoked.
 	 *
 	 * @see logoutBtnEvent
 	 */
 	class logoutBtnListener implements View.OnClickListener
	 {

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new MainController(activity);
		}
		 
	 }
}
