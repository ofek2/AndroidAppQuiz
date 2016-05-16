package com.example.onlinequizchecker;


/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/**
 * Defines several constants used between {@link BluetoothChatService} and the UI.
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

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    // Question types for parser
    public static final String MULTIPLE_CHOICE = "Multiple Choice";
    public static final String SINGLE_CHOICE= "Single Choice";
    public static final String FREE_TEXT= "Free Text";
    public static final String FREE_DRAW= "Free Draw";
    
    //Motion sensor command
    public static final String MOVING= "Moving";
    
}
