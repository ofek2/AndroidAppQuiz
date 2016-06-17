package com.example.onlinequizchecker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.onlinequizchecker.LectMessageHandler.toByteArray;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * The Class LectQuizProgressController.
 * This class controls the quiz progress screen.
 * In the quiz progress screen the lecturer will monitor the quiz. 
 * 
 */
public class LectQuizProgressController {

	/** The activity. */
	private MainActivity activity;

	/** The students in class. */
	private ArrayList<String> studentsInClass;

	/** The list view. */
	public static ListView listView;

	/** The adapter. */
	private ArrayAdapter<String> adapter;

	/** The finish button. */
	public static Button finishBtn = null;

	/** The view quiz button. */
	private Button viewQuizBtn;

	/** The timer. */
	private CounterClass timer;

	/** The time period. */
	private int timePeriod;

	/** The time left text. */
	private TextView timeLeftText;

	/** The time left label. */
	private TextView timeLeftLbl;

	/** The time is up. */
	private boolean timeIsUp = false;

	/** The lock. */
	public static Object lock;

	/** The can finish. */
	private boolean canFinish;

	/** The start uploading. */
	private boolean startUploading;

	/** The finish button alert. */
	public static AlertDialog finishButtonAlert = null;

	/** The reconnection dialog. */
	public static AlertDialog reconnectionDialog = null;

	/**
	 * Instantiates a new lect quiz progress controller.
	 *
	 * @param activity
	 *            the activity
	 * @param timePeriod
	 *            the time period
	 */
	public LectQuizProgressController(MainActivity activity, int timePeriod) {
		this.activity = activity;
		studentsInClass = LectQuizInitiationController.studentsInClass;
		this.activity.setContentView(R.layout.lect_quizprogressview);
		listView = (ListView) activity.findViewById(R.id.studentsFinalListView);
		populateList(studentsInClass);
		finishBtn = (Button) activity.findViewById(R.id.finishBtn);
		finishBtn.setOnClickListener(new finishBtnListener());
		viewQuizBtn = (Button) activity.findViewById(R.id.viewQuizBtn);
		viewQuizBtn.setOnClickListener(new viewQuizBtnListener());

		this.timePeriod = timePeriod;
		timeLeftText = (TextView) this.activity.findViewById(R.id.timeLeftTxtLect);
		timeLeftLbl = (TextView) this.activity.findViewById(R.id.timeLeftLblLect);
		timer = new CounterClass(this.timePeriod * 60000, 1000);
		lock = new Object();
		canFinish = false;
		startUploading = false;
		finishButtonAlert = null;
		reconnectionDialog = null;
		// timer = new CounterClass(30000, 1000);
		timer.start();
	}

	/**
	 * Populate list with students.
	 *
	 * @param students
	 *            the students
	 */
	private void populateList(ArrayList<String> students) {
		// TODO Auto-generated method stub
		listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		listView.setTextFilterEnabled(true);
		adapter = new ArrayAdapter<String>(this.activity, android.R.layout.simple_list_item_multiple_choice, students);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new itemListener());

		// listview.setItemChecked(2,true);

	}

	/**
	 * {@link OnClickListener} for the view quiz button.
	 *
	 * @see viewQuizBtnEvent
	 */
	class viewQuizBtnListener implements View.OnClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new LectViewQuizController(activity, LectQuizProgressController.this, LectQuizInitiationController.course,
					LectQuizInitiationController.quiz);
		}

	}

	/**
	 * {@link OnItemClickListener} for the items in the list
	 *
	 * @see itemEvent
	 */
	class itemListener implements AdapterView.OnItemClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.
		 * widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub

			if (!listView.isItemChecked(position)) {
				listView.setItemChecked(position, true);
				Toast toast = Toast.makeText(activity.getApplicationContext(),
						(String) parent.getItemAtPosition(position) + " has finished", Toast.LENGTH_SHORT);
				toast.show();
			} else {
				listView.setItemChecked(position, false);
				if (listView.getChildAt(position)
						.getDrawingCacheBackgroundColor() == Constants.STUDENT_IS_MOVING_COLOR) {
					// Do something to enable the moving student's quiz.
					for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
						String studentId = (String) parent.getItemAtPosition(position);
						if (studentId
								.equals(ServerBT.mConnThreads.get(i).getStudentId())) {
							String message = "Enable Quiz";
							byte[] buffer = toByteArray(message);
							if (ServerBT.mConnThreads.get(i) != null)
								ServerBT.mConnThreads.get(i).write(buffer);
							else {
								Toast toast = Toast.makeText(activity.getApplicationContext(),
										(String) parent.getItemAtPosition(position) + " has disconnected",
										Toast.LENGTH_SHORT);
								toast.show();
							}
						}
					}
					listView.getChildAt(position).setBackgroundColor(0);
					listView.getChildAt(position).setDrawingCacheBackgroundColor(0);
				} else {
					Toast toast = Toast.makeText(activity.getApplicationContext(),
							(String) parent.getItemAtPosition(position) + " hasn't finished yet", Toast.LENGTH_SHORT);
					toast.show();
				}
			}

		}

	}

	/**
	 * {@link OnClickListener} for the finish button.
	 *
	 * @see finishBtnEvent
	 */
	class finishBtnListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// handle quiz ending.
			synchronized (lock) {
				if (!timeIsUp) {
					if (!isAllChecked()) {
						toFinish();
					} else {
						new LectUploadProgress(activity, timer);
					}
				} else {
					if (!startUploading) {
						if (!isAllChecked() && !canFinish) {
							Toast toast = Toast.makeText(activity.getApplicationContext(),
									"Reconnecting to the students", Toast.LENGTH_SHORT);
							toast.show();
						} else if (canFinish) {
							toFinish();
						}
					}
				}
			}
		}
	}

	/**
	 * To finish.
	 */
	private void toFinish() {
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(activity);

		builder.setTitle("Confirm");
		builder.setMessage("Do you want to continue?");

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing but close the dialog

				dialog.dismiss();
				startUploading = true;
				new LectUploadProgress(activity, timer);
			}
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// Do nothing
				dialog.dismiss();
			}
		});
		finishButtonAlert = builder.create();
		finishButtonAlert.show();
	}

	/**
	 * Checks if is all checked.
	 *
	 * @return true, if is all checked
	 */
	private boolean isAllChecked() {
		for (int i = 0; i < studentsInClass.size(); i++) {
			if (!listView.isItemChecked(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieve view.
	 */
	public void retrieveView() {
		activity.setContentView(R.layout.lect_quizprogressview);

		finishBtn = (Button) activity.findViewById(R.id.finishBtn);
		finishBtn.setOnClickListener(new finishBtnListener());
		viewQuizBtn = (Button) activity.findViewById(R.id.viewQuizBtn);
		viewQuizBtn.setOnClickListener(new viewQuizBtnListener());
		timeLeftLbl = (TextView) activity.findViewById(R.id.timeLeftLblLect);
		timeLeftText = (TextView) activity.findViewById(R.id.timeLeftTxtLect);
		ListView tempListView = (ListView) activity.findViewById(R.id.studentsFinalListView);
		tempListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		tempListView.setTextFilterEnabled(true);
		tempListView.setAdapter(adapter);
		for (int i = 0; i < adapter.getCount(); i++)
			tempListView.setItemChecked(i, listView.isItemChecked(i));
		tempListView.setOnItemClickListener(new itemListener());
		listView = tempListView;
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
		@SuppressLint("DefaultLocale")
		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub

			long millis = millisUntilFinished;
			String ms = String.format("%02d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(millis)
							- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
					TimeUnit.MILLISECONDS.toSeconds(millis)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
			System.out.println(ms);
			timeLeftText.setText(ms);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.CountDownTimer#onFinish()
		 */
		@Override
		public void onFinish() {
			synchronized (lock) {
				timeIsUp = true;
			}
			if (!canFinish) {
				if (!isAllChecked()) {
					showAlertDialog("Trying to reconnect to the students that\n have lost the connection");
					timeLeftLbl.setText("Reconnection ends in:");
					new CountDownTimer(15000, 1000) {
						@SuppressLint("DefaultLocale")
						public void onTick(long millisUntilFinished) {
							long millis = millisUntilFinished;
							String ms = String.format("%02d:%02d",
									TimeUnit.MILLISECONDS.toMinutes(millis)
											- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
									TimeUnit.MILLISECONDS.toSeconds(millis)
											- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
							System.out.println(ms);
							timeLeftText.setText(ms);
							if (isAllChecked()) {
								startUploading = true;
								cancel();
								new LectUploadProgress(activity, null);
							} else if (startUploading)
								cancel();
						}

						public void onFinish() {
							timeLeftText.setText("00:00");
							if (!startUploading) {
								showAlertDialog("Not all of the students have managed to submit their answers"
										+ ",\n please press finish to upload the existing answers");
								canFinish = true;
							}
						}
					}.start();

				} else {
					startUploading = true;
					new LectUploadProgress(activity, null);
				}
			}

		}

		/**
		 * Show alert dialog.
		 *
		 * @param message
		 *            the message
		 */
		public void showAlertDialog(String message) {
			if (reconnectionDialog != null) {
				reconnectionDialog.dismiss();
				reconnectionDialog = null;
			}
			reconnectionDialog = new AlertDialog.Builder(activity).create();
			reconnectionDialog.setTitle("Alert");
			reconnectionDialog.setMessage(message);
			reconnectionDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			reconnectionDialog.show();
		}
	}
}
