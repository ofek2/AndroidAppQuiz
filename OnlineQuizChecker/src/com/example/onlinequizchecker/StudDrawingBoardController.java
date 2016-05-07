package com.example.onlinequizchecker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class StudDrawingBoardController {
    private MainActivity activity;
    private DrawingView dv;
    private Button backBtn;
    private Button saveBtn;
    private Button cleanBtn;
    
    private StudQuizActivity previousController;
    private LectViewQuizController prevCont;
    private String questionNumber;
    private String coursePath;
    public StudDrawingBoardController(MainActivity activity,StudQuizActivity previousController,String questionNumber,String coursePath) {

       this.activity=activity;
       this.activity.setContentView(R.layout.stud_drawingboardview);
       this.previousController = previousController;
       this.questionNumber = questionNumber;
       this.coursePath = coursePath;
       
       dv = (DrawingView)this.activity.findViewById(R.id.drawingView);
       backBtn = (Button)this.activity.findViewById(R.id.backBtnDrawingView);
       saveBtn = (Button)this.activity.findViewById(R.id.saveBtn);
       cleanBtn = (Button)this.activity.findViewById(R.id.cleanBtn);
       
       backBtn.setOnClickListener(new backBtnListener());
       saveBtn.setOnClickListener(new saveBtnListener());
       cleanBtn.setOnClickListener(new cleanBtnListener());
   }
    public StudDrawingBoardController(MainActivity activity,LectViewQuizController previousController,String questionNumber,String coursePath) {

        this.activity=activity;
        this.activity.setContentView(R.layout.stud_drawingboardview);
        this.prevCont=previousController;
        this.questionNumber = questionNumber;
        this.coursePath = coursePath;
        
        dv = (DrawingView)this.activity.findViewById(R.id.drawingView);
        backBtn = (Button)this.activity.findViewById(R.id.backBtnDrawingView);
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
		// TODO Auto-generated method stub
//		prevCont.updateQuizAfterDrawing(questionNumber);
		previousController.updateQuizAfterDrawing(questionNumber);
	}
	   
   }
   class saveBtnListener implements View.OnClickListener
   {

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dv.saveDrawing(coursePath+"/SDraw"+questionNumber+".PNG");
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
