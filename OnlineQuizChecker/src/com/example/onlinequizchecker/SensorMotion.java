package com.example.onlinequizchecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import static com.example.onlinequizchecker.LectMessageHandler.toByteArray;

/**
 * Created by 311165906 on 18/05/2016.
 */
public class SensorMotion implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mStepDetector;
    private MainActivity activity;
    private float stepCount;
    private final CounterClass timer;
    private ClientBT clientBT;
    private CharSequence studentId;
    public SensorMotion(MainActivity activity, ClientBT clientBT, CharSequence studentId) {
        this.activity = activity;
        this.clientBT = clientBT;
        this.studentId = studentId;
        stepCount = 0;
        mSensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        timer = new CounterClass(5000, 1000);
        timer.start();
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        stepCount++;
//        Toast.makeText(activity.getApplicationContext(), String.valueOf(stepCount),
//                Toast.LENGTH_LONG).show();
        // Do something with this sensor value.
    }

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            if (stepCount>2)
            {
                String message = Constants.MOVING+"-"+studentId;
		    	byte[] buffer = toByteArray(message);
//		    	showAlertDialog("Please return to your sit!");
		    	clientBT.mConnectedThread.write(buffer);
                stepCount = 0;
            }
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            // timeLeftText.setText("Completed.");
            Toast.makeText(activity.getApplicationContext(), String.valueOf(stepCount),
                    Toast.LENGTH_SHORT).show();
            stepCount = 0;
            timer.start();

        }


    }

    public void showAlertDialog(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                });

        alertDialog.show();
    }

//        mSensorManager.unregisterListener(this);

}