package com.example.onlinequizchecker;

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
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
        		android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//    	Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }
    
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        // For each start request, send a message to start a job and deliver the
//        // start ID so we know which request we're stopping when we finish the job
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(30000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//
//
//        // If we get killed, after returning from here, restart
//        return START_STICKY;
//    }

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
        Toast.makeText(getApplicationContext(), "Your quiz was successfully saved on your storage",
                Toast.LENGTH_LONG).show();
        if (!StudQuizActivity.submited&&ClientBT.quizWasInitiated)
        {
            CharSequence studentId = StudQuizActivity.studentId;
            zipProtectedFile.createZipFileFromSpecificFiles(zipFilesPassword, studentId, ClientBT.quizPathToZip + "/" + studentId + ".zip", ClientBT.quizPathToZip);
            //-------Check this!!!! ---------
            Toast.makeText(getApplicationContext(), "Your quiz was successfully saved on your storage",
                    Toast.LENGTH_LONG).show();
            //-------------------------------
        }
        //-------lecturer!!!! ---------

        
        
        //-------------------------------
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter.disable();
    }

}