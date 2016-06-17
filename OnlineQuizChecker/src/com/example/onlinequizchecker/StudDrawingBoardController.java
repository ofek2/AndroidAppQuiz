package com.example.onlinequizchecker;

import java.io.File;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * The Class StudDrawingBoardController.
 * This class controls the drawing board of a student during quiz time.
 */
public class StudDrawingBoardController {
    
    /** The activity. */
    private MainActivity activity;
    
    /** The drawing view. */
    private DrawingView dv;

    /** The save button. */
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
	* {@link OnClickListener} for the save button.
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
		dv.saveDrawing(coursePath+"/SDraw"+questionNumber+".PNG");
		previousController.updateQuizAfterDrawing(questionNumber);
		Toast.makeText(activity.getApplicationContext(), "Your drawing was successfully saved",
                 Toast.LENGTH_SHORT).show();
	}
	   
   }
   
   /**
    * {@link OnClickListener} for the clean button.
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
