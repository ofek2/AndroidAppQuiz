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
 * The Class MotionSensor.
 * This class controls the motion sensor events in the students phones.
 */
public class MotionSensor implements SensorEventListener {

	/** The sensor manager. */
	private SensorManager mSensorManager;

	/** The step detector. */
	private Sensor mStepDetector;

	/** The activity. */
	private MainActivity activity;

	/** The step count. */
	private float stepCount;

	/** The timer. */
	private final CounterClass timer;

	/** The BlueTooth client . */
	private ClientBT clientBT;

	/** The student id. */
	private CharSequence studentId;

	/**
	 * Instantiates a new motion sensor.
	 *
	 * @param activity
	 *            the activity
	 * @param clientBT
	 *            the BlueTooth client 
	 * @param studentId
	 *            the student id
	 */
	public MotionSensor(MainActivity activity, ClientBT clientBT, CharSequence studentId) {
		this.activity = activity;
		this.clientBT = clientBT;
		this.studentId = studentId;
		stepCount = 0;
		mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
		mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
		timer = new CounterClass(3000, 1000);
		timer.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.
	 * Sensor, int)
	 */
	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onSensorChanged(android.hardware.
	 * SensorEvent)
	 */
	@Override
	public final void onSensorChanged(SensorEvent event) {
		stepCount++;
	}

	/**
	 * The Class CounterClass.
	 */
	public class CounterClass extends CountDownTimer {

		/**
		 * Instantiates a new counter class.
		 *
		 * @param millisInFuture
		 *            the millis in future
		 * @param countDownInterval
		 *            the count down interval
		 */
		public CounterClass(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.CountDownTimer#onTick(long)
		 */
		@Override
		public void onTick(long millisUntilFinished) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.CountDownTimer#onFinish()
		 */
		@Override
		public void onFinish() {
			// -- Steps threshold -- //
			if (stepCount > 7) {
				String message = Constants.MOVING + "-" + studentId;
				byte[] buffer = toByteArray(message);
				// -- Disable a student's quiz -- //
				activity.setContentView(R.layout.stud_quizdisableview);
				if (clientBT.mConnectedThread != null)
					clientBT.mConnectedThread.write(buffer);
				stepCount = 0;
			}
			else
				timer.start();

		}

	}
	/**
	 * Gets the sensor manager.
	 *
	 * @return the sensor manager
	 */
	public SensorManager getSensorManager() {
		return mSensorManager;
	}
}