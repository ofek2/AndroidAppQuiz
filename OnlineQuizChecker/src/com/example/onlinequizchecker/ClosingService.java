package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class ClosingService extends Service {
    private boolean finished = false;
    public static Thread thread = null;




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
    	if(LectMessageHandler.lecturerServiceStarted)
        {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(30*60000+15000);
                    } catch (InterruptedException e) {
                    	stopSelf();
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    	else
    	{
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(StudQuizActivity.timePeriod*60000+15000);
                } catch (InterruptedException e) {
                	stopSelf();
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    	}
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent){
        finished = true;
//        if (blueToothReceiver != null) {
//            unregisterReceiver(blueToothReceiver);
//            StudLoginController.loginsuccedded = false;
//        }
        String zipFilesPassword = MainActivity.zipFilesPassword;
        if(!LectMessageHandler.lecturerServiceStarted)
        {
        if (!StudQuizActivity.submited&&ClientBT.quizWasInitiated)
        {
            CharSequence studentId = StudQuizActivity.studentId;
            zipProtectedFile.createZipFileFromSpecificFiles(zipFilesPassword, studentId, ClientBT.quizPathToZip + "/" + studentId + ".zip", ClientBT.quizPathToZip);
            //-------Check this!!!! ---------
            Toast.makeText(getApplicationContext(), "Your quiz was successfully saved on your storage",
                    Toast.LENGTH_LONG).show();
            //-------------------------------
        }
        }
        //-------lecturer!!!! ---------
        else
        {
			String path;
			try {
				path = getApplicationContext().getFilesDir().getCanonicalPath();
	        	new OnTaskRemovedUpload(path,getApplicationContext()).
				execute(path +"/"+Constants.APP_NAME + "/"+Constants.APP_NAME+".zip", "/");
//				DropBoxSimple.uploadFolder(new File(path +"/"+Constants.APP_NAME + "/"+Constants.APP_NAME+".zip"), "/");	//
//				 LectDownloadProgress.folderRecursiveDelete(new File(path+"/"+Constants.APP_NAME));//
				 //////////////////////////////////
		         
//		       close the connection to the students
//		         LectStudentRegListController.serverBT.stop();/////////////////////////////////////////////////////////

		         //////////////////////////////////
				 Toast toast = Toast.makeText(getApplicationContext(),  "The files were successfully saved",
		                    Toast.LENGTH_SHORT);
		         toast.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
        
        //-------------------------------
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter.disable();
    }

}