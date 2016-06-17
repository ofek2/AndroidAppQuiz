package com.example.onlinequizchecker;

import java.io.IOException;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * The Class ClosingService.
 * This service is used for secure saving of the files in the student application
 * and uploading the files to the dropbox in the lecturer application when closing the application.
 */
public class ClosingService extends Service {
    
    /** The keep running thread. */
    public static Thread thread = null;




    /* (non-Javadoc)
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    	//lecturer
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
    	//student
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
        return START_STICKY;
    }

    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }
    
    /* (non-Javadoc)
     * @see android.app.Service#onTaskRemoved(android.content.Intent)
     */
    @Override
    public void onTaskRemoved(Intent rootIntent){
        String zipFilesPassword = MainActivity.zipFilesPassword;
        //student
        if(!LectMessageHandler.lecturerServiceStarted)
        {
        if (!StudQuizActivity.submited&&ClientBT.quizWasInitiated)
        {
            CharSequence studentId = StudQuizActivity.studentId;
            zipProtectedFile.createZipFileFromSpecificFiles(zipFilesPassword, studentId, ClientBT.quizPathToZip + "/" + studentId + ".zip", ClientBT.quizPathToZip);
            Toast.makeText(getApplicationContext(), "Your quiz was successfully saved on your storage",
                    Toast.LENGTH_LONG).show();
        }
        }
        //lecturer
        else
        {
			String path;
			try {
				path = getApplicationContext().getFilesDir().getCanonicalPath();
	        	new OnTaskRemovedUpload(path,getApplicationContext()).
				execute(path +"/"+Constants.APP_NAME + "/"+Constants.APP_NAME+".zip", "/");		         
				 Toast toast = Toast.makeText(getApplicationContext(),  "The files were successfully saved",
		                    Toast.LENGTH_SHORT);
		         toast.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }        
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter.disable();
    }

}