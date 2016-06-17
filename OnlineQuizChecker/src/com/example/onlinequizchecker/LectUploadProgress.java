package com.example.onlinequizchecker;

import android.os.CountDownTimer;

import java.io.IOException;

/**
 * The Class LectUploadProgress. 
 * This class initiates the uploading progress. Uploads the files in the lecturer
 * phone to his Dropbox storage.
 */
public class LectUploadProgress {

	/** The activity. */
	private MainActivity activity;

	/**
	 * Instantiates a new lect upload progress.
	 *
	 * @param activity
	 *            the activity
	 * @param timer
	 *            the timer
	 */
	public LectUploadProgress(MainActivity activity, CountDownTimer timer) {
		this.activity = activity;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (LectMessageHandler.lecturerServiceStarted) {
			ClosingService.thread.interrupt();
			LectMessageHandler.lecturerServiceStarted = false;
		}
		if (LectQuizProgressController.finishBtn != null) {
			LectQuizProgressController.finishBtn.setOnClickListener(null);
			LectQuizProgressController.finishBtn = null;
		}
		if (LectQuizProgressController.reconnectionDialog != null) {
			LectQuizProgressController.reconnectionDialog.dismiss();
			LectQuizProgressController.reconnectionDialog = null;
		}
		if (LectQuizProgressController.finishButtonAlert != null) {
			LectQuizProgressController.finishButtonAlert.dismiss();
			LectQuizProgressController.finishButtonAlert = null;
		}

		this.activity.setContentView(R.layout.lect_uploadprogress);

		String path;
		try {
			path = activity.getApplicationContext().getFilesDir().getCanonicalPath();
			startUpload(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Start upload.
	 *
	 * @param path
	 *            the path to upload from
	 */
	private void startUpload(String path) {
		// TODO Auto-generated method stub

		try {
			new UploadFolderDB(path, activity, true, null)
					.execute(activity.getFilelist().getCanonicalPath() + "/" + Constants.APP_NAME + ".zip", "/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
