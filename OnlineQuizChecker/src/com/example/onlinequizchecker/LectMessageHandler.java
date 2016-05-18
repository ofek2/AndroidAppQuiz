package com.example.onlinequizchecker;

import java.util.ArrayList;
import java.util.Set;

import com.example.onlinequizchecker.R.color;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

public class LectMessageHandler extends Handler{
	public LectMessageHandler()
	{
		super();
	}
	@Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_READ:
            	
				if (msg.arg2==-1)
				{
					String receivedStudentId = (String)msg.obj;
					markPosInFinishList(studentPosInList(receivedStudentId,LectQuizInitiationController.studentsInClass));
				}
					
				else {
					String readMessage = (String)msg.obj;
					if(!LectStudentRegListController.receivePos(studentPosInList(readMessage,LectStudentRegListController.students)))
					{
			            byte [] sendMsg = toByteArray("This id is already connected");
						LectStudentRegListController.serverBT.mConnThreads.get(msg.arg2).write(sendMsg);
					}
				}

                break;
            case Constants.MOTION_SENSOR_TRIGGERED:
            	String receivedStudentId = (String)msg.obj;
            	int studentPos = studentPosInList(receivedStudentId, LectQuizInitiationController.studentsInClass);
            	markMovingStudentInFinishList(studentPos);
        }
    }

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
    public static int studentPosInList(String Id,ArrayList<String> studentsInClass)
	{
		for (int i = 0; i < studentsInClass.size(); i++) {
			if (studentsInClass.get(i).equals(Id)) {
				return i;
			}
		}
		return -1;
	}
    private void markPosInFinishList(int pos)
	{
		LectQuizProgressController.listView.setItemChecked(pos, true);	
	}
    private void markMovingStudentInFinishList(int pos)
    {
    	LectQuizProgressController.listView.getChildAt(pos).setBackgroundColor(Color.argb(180, 220, 0, 0));
    	LectQuizProgressController.listView.getChildAt(pos).setDrawingCacheBackgroundColor(Color.argb(208, 220, 0, 0));
    	
    }
    
	
}
