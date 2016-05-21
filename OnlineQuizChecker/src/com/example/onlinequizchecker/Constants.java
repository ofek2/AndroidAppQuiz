package com.example.onlinequizchecker;

import android.graphics.Color;


/**
 * Defines several constants 
 */
public interface Constants {

	//Application name
	public static final String APP_NAME = "OnlineQuizChecker";
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int QUIZ_INITIATION = 6;
    public static final int STUDENT_AUTHORIZED = 7;
    public static final int MOTION_SENSOR_TRIGGERED = 8;
    public static final int CANCEL_MARK = 9;
    public static final int CONNECTION_LOST=10;
    public static final int STUDENT_SUBMITED=11;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    // Question types for parser
    public static final String MULTIPLE_CHOICE = "Multiple Choice";
    public static final String SINGLE_CHOICE= "Single Choice";
    public static final String FREE_TEXT= "Free Text";
    public static final String FREE_DRAW= "Free Draw";
    
    //Motion sensor command
    public static final String MOVING = "Moving";
    public static final int STUDENT_IS_MOVING_COLOR = Color.argb(180, 220, 0, 0); 
    
    //User Classification
    public static final String LECTURER = "Lecturer";
    public static final String STUDENT = "Student";
  
    
}
