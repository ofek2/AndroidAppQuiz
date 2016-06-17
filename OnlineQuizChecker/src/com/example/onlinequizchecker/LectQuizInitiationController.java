package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * The Class LectQuizInitiationController. 
 * This class controls the quiz initiation process.
 * It is used to send the quizzes to the students.
 */
public class LectQuizInitiationController {

	/** The activity. */
	private MainActivity activity;

	/** The previous controller. */
	private LectQuizSelectionController previousController;

	/** The course. */
	public static String course;

	/** The quiz. */
	public static String quiz;

	/** The course id text. */
	private EditText courseIDText;

	/** The quiz name text. */
	private EditText quizNameText;

	/** The time spinner. */
	private Spinner timeSpinner;

	/** The start quiz button. */
	private Button startQuizBtn;

	/** The back button. */
	private Button backBtn;

	/** The students in class. */
	public static ArrayList<String> studentsInClass;

	/** The selected time period int. */
	public static int selectedTimePeriodInt = 0;

	/**
	 * Instantiates a new lect quiz initiation controller.
	 *
	 * @param activity
	 *            the activity
	 * @param previousController
	 *            the previous controller
	 * @param course
	 *            the course
	 * @param quiz
	 *            the quiz
	 */
	public LectQuizInitiationController(MainActivity activity, LectQuizSelectionController previousController,
			String course, String quiz) {
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_quizinitiationview);
		this.previousController = previousController;
		this.course = course;
		this.quiz = quiz;
		initComponents();
	}

	/**
	 * Instantiates the components.
	 */
	private void initComponents() {
		// TODO Auto-generated method stub
		courseIDText = (EditText) activity.findViewById(R.id.courseIDTxt);
		quizNameText = (EditText) activity.findViewById(R.id.quizNameTxt);
		timeSpinner = (Spinner) activity.findViewById(R.id.timeSpinner);
		startQuizBtn = (Button) activity.findViewById(R.id.startQuizBtn);
		backBtn = (Button) activity.findViewById(R.id.backBtnQuizInit);
		selectedTimePeriodInt = 0;
		courseIDText.setText(course);
		quizNameText.setText(quiz);
		studentsInClass = new ArrayList<>();
		startQuizBtn.setOnClickListener(new startQuizBtnListener());
		backBtn.setOnClickListener(new backBtnListener());
	}

	/**
	 * {@link OnClickListener} for the back button
	 *
	 * @see backBtnEvent
	 */
	class backBtnListener implements View.OnClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			previousController.retrieveView();

		}

	}

	/**
	 * {@link OnClickListener} for the start button
	 *
	 * @see startQuizBtnEvent
	 */
	class startQuizBtnListener implements View.OnClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String selectedTimePeriodString = timeSpinner.getSelectedItem().toString().split("\\s+")[0];
			selectedTimePeriodInt = Integer.valueOf(selectedTimePeriodString);
			for (int i = 0; i < LectStudentRegListController.students.size(); i++) {
				if (LectStudentRegListController.listview.isItemChecked(i))
					studentsInClass.add(LectStudentRegListController.students.get(i));
			}
			startQuiz(selectedTimePeriodInt);
			if (!LectMessageHandler.lecturerServiceStarted) {
				LectMessageHandler.lecturerServiceStarted = true;
				Intent intent = new Intent(activity, ClosingService.class);
				activity.startService(intent);
			}
		}

		/**
		 * Turns charSequence to byte array.
		 *
		 * @param charSequence
		 *            the char sequence
		 * @return the byte[]
		 */
		private byte[] toByteArray(CharSequence charSequence) {
			if (charSequence == null) {
				return null;
			}
			byte[] bytesArray = new byte[charSequence.length()];
			for (int i = 0; i < bytesArray.length; i++) {
				bytesArray[i] = (byte) charSequence.charAt(i);
			}

			return bytesArray;
		}

		/**
		 * Start quiz.
		 *
		 * @param quizPeriod
		 *            the quiz period
		 */
		private void startQuiz(int quizPeriod) {
			// TODO Auto-generated method stub
			// new LectQuizProgressController(activity,quizPeriod);

			try {
				// ------------------------ Create zip file that contains all of
				// the quiz data -------------------------//
				String destinationZipFilePath = activity.getFilelist().getCanonicalPath() + "/" + course + "/Quizzes/"
						+ quiz + "/Form/" + quiz + ".zip";
				String directoryToBeZipped = activity.getFilelist().getCanonicalPath() + "/" + course + "/Quizzes/"
						+ quiz + "/Form";
				byte[] buffer = new byte[1024];
				FileOutputStream fos = new FileOutputStream(destinationZipFilePath);
				ZipOutputStream zos = new ZipOutputStream(fos);
				File dir = new File(directoryToBeZipped);
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().equals(quiz + ".html") || files[i].getName().startsWith("Question")) {
						System.out.println("Adding file: " + files[i].getName());
						FileInputStream fis = new FileInputStream(files[i]);
						// begin writing a new ZIP entry, positions the stream
						// to
						// the start of the entry data
						zos.putNextEntry(new ZipEntry(files[i].getName()));
						int length;
						while ((length = fis.read(buffer)) > -1) {
							zos.write(buffer, 0, length);
						}
						zos.closeEntry();
						// close the InputStream
						fis.close();
					}

				}
				// close the ZipOutputStream
				zos.close();

				// ------------------------ Create a message that contains the
				// zip file, course id, quiz name, and quiz time period
				// ----------//
				FileInputStream fileInputStream = null;
				String time = String.valueOf(quizPeriod);
				File file = new File(destinationZipFilePath);
				int fileSize = (int) file.length();
				byte[] bFile = new byte[fileSize + String.valueOf(fileSize).length() + course.length() + quiz.length()
						+ time.length() + 4];

				byte[] readFile = new byte[fileSize];
				// convert file into array of bytes
				fileInputStream = new FileInputStream(file);
				// fileInputStream.read(bFile);
				fileInputStream.read(readFile);
				fileInputStream.close();
				file.delete();
				int bIndex;
				for (bIndex = 0; bIndex < quiz.length(); bIndex++) {
					bFile[bIndex] = (byte) quiz.charAt(bIndex);
				}
				bFile[bIndex] = (byte) '-';
				bIndex++;

				for (int m = 0; m < course.length(); m++) {
					bFile[bIndex] = (byte) course.charAt(m);
					bIndex++;
				}
				bFile[bIndex] = (byte) '-';
				bIndex++;

				for (int j = 0; j < time.length(); j++) {
					bFile[bIndex] = (byte) time.charAt(j);
					bIndex++;
				}
				bFile[bIndex] = (byte) '-';
				bIndex++;
				for (int k = 0; k < String.valueOf(fileSize).length(); k++) {
					bFile[bIndex] = (byte) String.valueOf(fileSize).charAt(k);
					bIndex++;
				}
				bFile[bIndex] = (byte) '-';
				bIndex++;
				for (int l = 0; l < fileSize; l++) {
					bFile[bIndex] = readFile[l];
					bIndex++;
				}
				// ------ Send quizzes to the connected students --------
				for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
					ServerBT.mConnThreads.get(i).getMmOutStream().write(bFile);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
