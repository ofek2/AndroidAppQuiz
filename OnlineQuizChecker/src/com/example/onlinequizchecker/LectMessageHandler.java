package com.example.onlinequizchecker;

import java.util.ArrayList;
import java.util.Set;

import com.example.onlinequizchecker.LectStudentRegListController.quizSelectionBtnListener;
import com.example.onlinequizchecker.R.color;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

/**
 * The Class LectMessageHandler.
 * This class handles messages sent by the server
 * to initiate GUI events like changing screens 
 * and animations.
 */
public class LectMessageHandler extends Handler{
	
	/** The activity. */
	private MainActivity activity;
	
	/** The lect student reg list controller. */
	private LectStudentRegListController lectStudentRegListController;
	
	/** The in recovery mode. */
	public static int inRecoveryMode;
	
	/** The lock. */
	private Object lock;
	
	/** The lecturer service started. */
	public static boolean lecturerServiceStarted;
	
	/**
	 * Instantiates a new lect message handler.
	 *
	 * @param activity the activity
	 * @param lectStudentRegListController the lect student reg list controller
	 */
	public LectMessageHandler(MainActivity activity, LectStudentRegListController lectStudentRegListController)
	{
		super();
		this.activity = activity;
		this.lectStudentRegListController = lectStudentRegListController;
		lock = new Object();
		inRecoveryMode = 0;
	}
	
	/* (non-Javadoc)
	 * @see android.os.Handler#handleMessage(android.os.Message)
	 */
	@Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_READ:
            	//-------- Mark a student in the LectQuizProgress list indicating that this student has finished is quiz---------//
				if (msg.arg2==-1)
				{
					String receivedStudentId = (String)msg.obj;
					synchronized (LectQuizProgressController.lock) {
						markPosInFinishList(studentPosInList(receivedStudentId,LectQuizInitiationController.studentsInClass));
					}
//					markPosInFinishList(studentPosInList(receivedStudentId,LectQuizInitiationController.studentsInClass));
					ServerBT.mConnThreads.get(msg.arg1).cancel();
				}
				//-------- Check if a student is already connected from another phone ---------//	
				else {
					String readMessage = (String)msg.obj;
					synchronized (lock) {
						if(!LectStudentRegListController.receivePos(studentPosInList(readMessage,LectStudentRegListController.students)))
						{
							byte [] sendMsg = toByteArray("This id is already connected");
							LectStudentRegListController.serverBT.mConnThreads.get(msg.arg2).write(sendMsg);
						}
					}
				}

                break;
            //-------- Handle motion sensor triggered event ---------//
            case Constants.MOTION_SENSOR_TRIGGERED:
            	String receivedStudentId = (String)msg.obj;
            	int studentPos = studentPosInList(receivedStudentId, LectQuizInitiationController.studentsInClass);
            	markMovingStudentInFinishList(studentPos);
				break;
			//-------- cancel a mark of a student who lost his connection in the login process ---------//
			case Constants.CANCEL_MARK:
				receivedStudentId = (String)msg.obj;
				studentPos = studentPosInList(receivedStudentId, LectStudentRegListController.students);
				cancelMark(studentPos);
				break;
			//-------- Handle connection lost event ---------//
			case Constants.CONNECTION_LOST:
				new MainController(activity);
				break;
			//-------- Open quiz progress screen ---------//
			case Constants.QUIZ_INITIATION:
				new LectQuizProgressController(activity,LectQuizInitiationController.selectedTimePeriodInt);
				break;
			//-------- In a case of recovery, this will make the "Recovery" button blink ---------//
			case Constants.BLINK_RECOVERY:
//				Button recoveryBtn = (Button)activity.findViewById(R.id.RecoveryBtn);
				synchronized (LectStudentRegListController.lockA) {									
//				inRecoveryMode++;
				if(!LectStudentRegListController.inRecovery)
				{
				if(LectStudentRegListController.alert!=null&&LectStudentRegListController.alert.isShowing())
					LectStudentRegListController.alert.dismiss();
				if(!lecturerServiceStarted)
				{
					lecturerServiceStarted = true;
					Intent intent = new Intent(activity,ClosingService.class);
					activity.startService(intent);
				}
				LectStudentRegListController.inRecovery = true;
				LectStudentRegListController.recoveryPressed = false;
				Button quizSelectionBtn = (Button)activity.findViewById(R.id.quizSelectionBtn);
				quizSelectionBtn.setOnClickListener(new whileRecoveryBtnListener());
				Button backBtn = (Button)activity.findViewById(R.id.backBtnStudRegList);
				backBtn.setOnClickListener(new whileRecoveryBtnListener());
				 final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
				    animation.setDuration(500); // duration - half a second
				    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
				    animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
				    animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
				    Button recoveryBtn = (Button)activity.findViewById(R.id.RecoveryBtn);
				    recoveryBtn.setOnClickListener(lectStudentRegListController.new recoveryBtnListener());
				    recoveryBtn.startAnimation(animation);
				}
				}
				break;
        }
    }

	/**
	 *This class prevents any buttons to be clicked while the recovery is in progress
	 *
	 * @see whileRecoveryBtnEvent
	 */
	class whileRecoveryBtnListener implements View.OnClickListener
	{

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(activity.getApplicationContext(), "Recovery has to be finished",
					Toast.LENGTH_SHORT);
			toast.show();
		}
		
	}
	
    /**
     * Turns charSequence to byte array.
     *
     * @param charSequence the char sequence to turn to byte array
     * @return the byte[]
     */
    public static byte[] toByteArray(CharSequence charSequence) {
        if (charSequence == null) {
          return null;
        }
        byte[] bytesArray = new byte[charSequence.length()];
        for (int i = 0; i < bytesArray.length; i++) {
        	bytesArray[i] = (byte) charSequence.charAt(i);
        }

        return bytesArray;
    }
    
    /**
     * Returns the student position in the list(quiz progress list).
     *
     * @param Id the id
     * @param studentsInClass the students in class
     * @return the int
     */
    public static int studentPosInList(String Id,ArrayList<String> studentsInClass)
	{
		for (int i = 0; i < studentsInClass.size(); i++) {
			if (studentsInClass.get(i).equals(Id)) {
				return i;
			}
		}
		return -1;
	}
    
    /**
     * Mark position in finish list.
     *
     * @param pos the position to mark
     */
    private void markPosInFinishList(int pos)
	{
		LectQuizProgressController.listView.setItemChecked(pos, true);	
	}
	
	/**
	 * Cancel mark.
	 *
	 * @param pos the position to cancel
	 */
	private void cancelMark(int pos)
	{
		LectStudentRegListController.listview.setItemChecked(pos, false);
	}
    
    /**
     * Mark moving student in finish list.
     *
     * @param pos the position to mark
     */
    private void markMovingStudentInFinishList(int pos)
    {
    	LectQuizProgressController.listView.getChildAt(pos).setBackgroundColor(Constants.STUDENT_IS_MOVING_COLOR);
    	LectQuizProgressController.listView.getChildAt(pos).setDrawingCacheBackgroundColor(Constants.STUDENT_IS_MOVING_COLOR);
    	
    }
    
	
}
