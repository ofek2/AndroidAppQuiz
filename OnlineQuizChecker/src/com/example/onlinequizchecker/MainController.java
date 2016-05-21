
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

public class MainController {
    private Button lecturerBtn;
    private Button studentBtn;
    private MainActivity activity;
    public MainController(MainActivity activity){
        this.activity=activity;
        this.activity.setUserClassification("");
        this.activity.setContentView(R.layout.main_view);
        this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
        this.activity.setDidDbxAuth(false);
        lecturerBtn = (Button) activity.findViewById(R.id.lecturerBtn);
        studentBtn = (Button) activity.findViewById(R.id.studentBtn);
        lecturerBtn.setOnClickListener(new lecturerBtnListener());
        studentBtn.setOnClickListener(new studentBtnListener());

       
    }
    class lecturerBtnListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
//            activity.setUserClassification("Lecturer");
            new LectDropBoxAuthController(activity);
           
        }
    }
    class studentBtnListener implements View.OnClickListener
    {

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
