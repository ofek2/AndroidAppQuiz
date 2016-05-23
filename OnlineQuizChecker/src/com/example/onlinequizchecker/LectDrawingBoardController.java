package com.example.onlinequizchecker;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LectDrawingBoardController {
    private MainActivity activity;
    private DrawingView dv;
    private Button backBtn;
    private Button saveBtn;
    private Button cleanBtn;
    
    private LectViewQuizController previousController;
    private String questionNumber;
    private String coursePath;
    public LectDrawingBoardController(MainActivity activity,LectViewQuizController previousController,String questionNumber,String coursePath) {

       this.activity=activity;
       this.activity.setContentView(R.layout.stud_drawingboardview);
       this.activity.hideKeyboard();
       this.previousController = previousController;
       this.questionNumber = questionNumber;
       this.coursePath = coursePath;
       
       dv = (DrawingView)this.activity.findViewById(R.id.drawingView);
       
       if(new File(coursePath+"/SDraw"+questionNumber+".PNG").exists())
    	   dv.setPicture(this.coursePath+"/SDraw"+this.questionNumber+".PNG");
       backBtn = (Button)this.activity.findViewById(R.id.backBtnQuizInit);
       saveBtn = (Button)this.activity.findViewById(R.id.saveBtn);
       cleanBtn = (Button)this.activity.findViewById(R.id.cleanBtn);
       
       backBtn.setOnClickListener(new backBtnListener());
       saveBtn.setOnClickListener(new saveBtnListener());
       cleanBtn.setOnClickListener(new cleanBtnListener());
   }
    
   class backBtnListener implements View.OnClickListener
   {

	@Override
	public void onClick(View v) {
		previousController.updateQuizAfterDrawing(questionNumber);
	}
	   
   }
   class saveBtnListener implements View.OnClickListener
   {

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dv.saveDrawing(coursePath+"/SDraw"+questionNumber+".PNG");
		 Toast.makeText(activity.getApplicationContext(), "Your drawing was successfully saved",
                 Toast.LENGTH_SHORT).show();
	}
	   
   }
   class cleanBtnListener implements View.OnClickListener
   {

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dv.clearDrawing();
	}
	   
   }

}
