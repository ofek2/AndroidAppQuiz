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

/**
 * The Class StudDrawingBoardController.
 */
public class StudDrawingBoardController {
    
    /** The activity. */
    private MainActivity activity;
    
    /** The dv. */
    private DrawingView dv;

/** The save button. */
//    private Button backBtn;
    private Button saveBtn;
    
    /** The clean button. */
    private Button cleanBtn;
    
    /** The previous controller. */
    private StudQuizActivity previousController;
    
    /** The question number. */
    private String questionNumber;
    
    /** The course path. */
    private String coursePath;
    
    /**
     * Instantiates a new stud drawing board controller.
     *
     * @param activity the activity
     * @param previousController the previous controller
     * @param questionNumber the question number
     * @param coursePath the course path
     */
    public StudDrawingBoardController(MainActivity activity,StudQuizActivity previousController,String questionNumber,String coursePath) {

       this.activity=activity;
       this.activity.setContentView(R.layout.stud_drawingboardview);
       this.activity.hideKeyboard();
       this.previousController = previousController;
       this.questionNumber = questionNumber;
       this.coursePath = coursePath;
       
       dv = (DrawingView)this.activity.findViewById(R.id.drawingView);
       if(new File(coursePath+"/SDraw"+questionNumber+".PNG").exists())
    	   dv.setPicture(this.coursePath+"/SDraw"+this.questionNumber+".PNG");

       saveBtn = (Button)this.activity.findViewById(R.id.saveBtn);
       cleanBtn = (Button)this.activity.findViewById(R.id.cleanBtn);

       saveBtn.setOnClickListener(new saveBtnListener());
       cleanBtn.setOnClickListener(new cleanBtnListener());
   }
   
   /**
    * The listener interface for receiving saveBtn events.
    * The class that is interested in processing a saveBtn
    * event implements this interface, and the object created
    * with that class is registered with a component using the
    * component's <code>addsaveBtnListener<code> method. When
    * the saveBtn event occurs, that object's appropriate
    * method is invoked.
    *
    * @see saveBtnEvent
    */
   class saveBtnListener implements View.OnClickListener
   {

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dv.saveDrawing(coursePath+"/SDraw"+questionNumber+".PNG");
		previousController.updateQuizAfterDrawing(questionNumber);
		Toast.makeText(activity.getApplicationContext(), "Your drawing was successfully saved",
                 Toast.LENGTH_SHORT).show();
	}
	   
   }
   
   /**
    * The listener interface for receiving cleanBtn events.
    * The class that is interested in processing a cleanBtn
    * event implements this interface, and the object created
    * with that class is registered with a component using the
    * component's <code>addcleanBtnListener<code> method. When
    * the cleanBtn event occurs, that object's appropriate
    * method is invoked.
    *
    * @see cleanBtnEvent
    */
   class cleanBtnListener implements View.OnClickListener
   {

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dv.clearDrawing();
	}
	   
   }

}
