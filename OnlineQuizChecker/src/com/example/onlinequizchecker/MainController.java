
package com.example.onlinequizchecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.NetworkStats.Bucket;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

/**
 * The Class MainController.
 * This class controls the main screen of the application.
 */
public class MainController {

	/** The lecturer button. */
	private Button lecturerBtn;

	/** The student button. */
	private Button studentBtn;

	/** The activity. */
	private MainActivity activity;

	/**
	 * Instantiates a new main controller.
	 *
	 * @param activity
	 *            the activity
	 */
	public MainController(MainActivity activity) {
		this.activity = activity;
		this.activity.setUserClassification("");
		this.activity.setContentView(R.layout.main_view);
		this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		lecturerBtn = (Button) this.activity.findViewById(R.id.lecturerBtn);
		studentBtn = (Button) this.activity.findViewById(R.id.studentBtn);
		lecturerBtn.setOnClickListener(new lecturerBtnListener());
		studentBtn.setOnClickListener(new studentBtnListener());
		LectMessageHandler.lecturerServiceStarted = false;
		if (LectStudentRegListController.serverBT != null) {
			LectStudentRegListController.serverBT.stop();
			LectStudentRegListController.serverBT = null;
		}

	}

	/**
	 * {@link OnClickListener} for the lecturer button.
	 *
	 * @see lecturerBtnEvent
	 */
	class lecturerBtnListener implements View.OnClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			// activity.setUserClassification("Lecturer");
			new LectDropBoxAuthController(activity);

		}
	}

	/**
	 * {@link OnClickListener} for the student button.
	 *
	 * @see studentBtnEvent
	 */
	class studentBtnListener implements View.OnClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			// activity.setUserClassification("Student");
			if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
				int permissionCheck = activity.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
				permissionCheck += activity.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
				if (permissionCheck != 0) {

					activity.requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_COARSE_LOCATION }, 1001); // Any
																					// number
				}
			}
			new StudLoginController(activity);
		}
	}
}
