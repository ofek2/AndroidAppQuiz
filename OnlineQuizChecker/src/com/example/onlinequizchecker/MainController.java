
package com.example.onlinequizchecker;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.NetworkStats.Bucket;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * The Class MainController.
 */
public class MainController {
    
    /** The lecturer button. */
    private Button lecturerBtn;
    
    /** The student button. */
    private Button studentBtn;
    
    /** The activity. */
    private MainActivity activity;
    
    /**
     * Instantiates a new main controller.
     *
     * @param activity the activity
     */
    public MainController(MainActivity activity){
        this.activity=activity;
        this.activity.setUserClassification("");
        this.activity.setContentView(R.layout.main_view);
        this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
        this.activity.setDidDbxAuth(false);
        lecturerBtn = (Button) this.activity.findViewById(R.id.lecturerBtn);
        studentBtn = (Button) this.activity.findViewById(R.id.studentBtn);
        lecturerBtn.setOnClickListener(new lecturerBtnListener());
        studentBtn.setOnClickListener(new studentBtnListener());
        LectMessageHandler.lecturerServiceStarted = false;
//        ClientBT.quizWasInitiated = false;
        if(LectStudentRegListController.serverBT != null)
        {
        	LectStudentRegListController.serverBT.stop();
        	LectStudentRegListController.serverBT = null;
        }
       
    }
    
    /**
     * The listener interface for receiving lecturerBtn events.
     * The class that is interested in processing a lecturerBtn
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addlecturerBtnListener<code> method. When
     * the lecturerBtn event occurs, that object's appropriate
     * method is invoked.
     *
     * @see lecturerBtnEvent
     */
    class lecturerBtnListener implements View.OnClickListener
    {

        /* (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View v) {
//            activity.setUserClassification("Lecturer");
            new LectDropBoxAuthController(activity);
           
        }
    }
    
    /**
     * The listener interface for receiving studentBtn events.
     * The class that is interested in processing a studentBtn
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addstudentBtnListener<code> method. When
     * the studentBtn event occurs, that object's appropriate
     * method is invoked.
     *
     * @see studentBtnEvent
     */
    class studentBtnListener implements View.OnClickListener
    {

        /* (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @SuppressLint("NewApi")
		@Override
        public void onClick(View v) {
//            activity.setUserClassification("Student");
        	if(Build.VERSION.SDK_INT==Build.VERSION_CODES.M){
            int permissionCheck = activity.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += activity.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

            	activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            	}
        	}
            new StudLoginController(activity);
        }
    }
}
