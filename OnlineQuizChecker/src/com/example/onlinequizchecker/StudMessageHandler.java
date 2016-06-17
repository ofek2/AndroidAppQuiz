package com.example.onlinequizchecker;

import java.io.File;

import android.content.BroadcastReceiver;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * The Class StudMessageHandler.
 */
public class StudMessageHandler extends Handler {
	
	/** The activity. */
	private MainActivity activity;
	
	/** The stud auth controller. */
	private StudAuthController studAuthController;

	/**
	 * Instantiates a new stud message handler.
	 *
	 * @param activity the activity
	 * @param studAuthController the stud auth controller
	 */
	public StudMessageHandler(MainActivity activity, StudAuthController studAuthController) {
		this.activity = activity;
		this.studAuthController = studAuthController;
	}

	/* (non-Javadoc)
	 * @see android.os.Handler#handleMessage(android.os.Message)
	 */
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case Constants.MESSAGE_READ:
			byte[] readBuf = (byte[]) msg.obj;
			// construct a string from the valid bytes in the buffer
			String readMessage = new String(readBuf, 0, msg.arg1);
			if (readMessage.equals("You have not registered to this course")) {
				activity.unregisterReceiver(studAuthController.getmReceiver());

				new StudLoginController(activity);
				Toast.makeText(activity.getApplicationContext(), "You have not registered to this course",
						Toast.LENGTH_LONG).show();
			} else if (readMessage.equals("This id is already connected")) {
				activity.unregisterReceiver(studAuthController.getmReceiver());
				new StudLoginController(activity);
				Toast.makeText(activity.getApplicationContext(), "This id is already connected", Toast.LENGTH_LONG)
						.show();
			} else if (readMessage.equals("The PIN code is not correct")) {
				activity.unregisterReceiver(studAuthController.getmReceiver());
				new StudLoginController(activity);
				Toast.makeText(activity.getApplicationContext(), "The PIN code is not correct", Toast.LENGTH_LONG)
						.show();
			}
			break;
		case Constants.QUIZ_INITIATION:
			String quizPath = (String) msg.obj;

			int quizPeriod = msg.arg1;
			activity.setUserClassification(Constants.STUDENT);
			studAuthController
					.setStudtentQuizActivity(new StudQuizActivity(activity, quizPeriod, studAuthController.studentId,
							quizPath, studAuthController.applicationPath, studAuthController.clientBT));
			break;
		case Constants.STUDENT_AUTHORIZED:
			activity.unregisterReceiver(studAuthController.getmReceiver());
			studAuthController.getLabel().setText("Waiting for quiz initiation.");
			String course = (String) msg.obj;
			if (new File(studAuthController.applicationPath + "/" + course).exists()) {
				File[] quizzes = new File(studAuthController.applicationPath + "/" + course + "/Quizzes").listFiles();
				String recoveryPath;
				String recoveryZipPath;
				String pathToSend;
				outerloop: for (int i = 0; i < quizzes.length; i++) {
					recoveryPath = studAuthController.applicationPath + "/" + course + "/Quizzes/"
							+ quizzes[i].getName() + "/StudentsAnswers";
					File[] answersFiles = new File(studAuthController.applicationPath + "/" + course + "/Quizzes/"
							+ quizzes[i].getName() + "/StudentsAnswers").listFiles();
					for (int j = 0; j < answersFiles.length; j++) {
						if (answersFiles[j].getName().equals(studAuthController.getStudentId() + ".zip")) {
							recoveryZipPath = recoveryPath + "/" + studAuthController.getStudentId() + ".zip";
							pathToSend = "/" + course + "/Quizzes/" + quizzes[i].getName() + "/StudentsAnswers/";
							File[] files = new File(recoveryPath).listFiles();
							zipProtectedFile.unzipFile(activity.zipFilesPassword, recoveryZipPath, recoveryPath);
							new File(recoveryZipPath).delete();
							files = new File(recoveryPath).listFiles();
							zipFileManager.createZipFile(new File(recoveryPath), recoveryZipPath);
							byte[] byteArrayToSend = StudQuizActivity.zipToByteArray(
									recoveryPath + "/" + studAuthController.getStudentId() + ".zip", pathToSend);
							studAuthController.clientBT.mConnectedThread.write(byteArrayToSend);
							break outerloop;
						}
					}

				}
			}
			break;
		case Constants.CONNECTION_LOST:
			new StudLoginController(activity);
			Toast.makeText(activity.getApplicationContext(), "The connection with the lecturer was lost",
					Toast.LENGTH_LONG).show();
			break;
		case Constants.STUDENT_SUBMITED:
			Toast.makeText(activity.getApplicationContext(), "Your quiz was successfully sent to your lecturer",
					Toast.LENGTH_LONG).show();
			new MainController(activity);
			break;
		case Constants.ENABLE_QUIZ:
			if (studAuthController.getStudtentQuizActivity() != null)
				studAuthController.getStudtentQuizActivity().enableQuiz();
			break;

		}
	}
}
