
package com.example.onlinequizchecker;


import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

public class MainController {
    private Button lecturerBtn;
    private Button studentBtn;
    private MainActivity activity;
    public MainController(MainActivity activity){
        this.activity=activity;
        this.activity.setUserClassification("");
        this.activity.setContentView(R.layout.main_view);
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

        @Override
        public void onClick(View v) {
//            activity.setUserClassification("Student");
            new StudLoginController(activity);
        }
    }
}
