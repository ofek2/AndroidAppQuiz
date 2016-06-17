package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.example.onlinequizchecker.LectQuizProgressController.itemListener;
import com.example.onlinequizchecker.MainController.lecturerBtnListener;
import com.example.onlinequizchecker.R.color;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class LectStudentRegListController.
 * This class controls the students connection screen.
 * In the students connection screen the lecturer will allow his
 * students to make a connection for initiating a quiz or recover any
 * previous quizzes.
 */
public class LectStudentRegListController extends ListActivity {
	
	/** The activity. */
	private MainActivity activity;
	
	/** The listview. */
	public static ListView listview;
	
	/** The Bluetooth server. */
	public static ServerBT serverBT = null;
	
	/** The filelist. */
	private File filelist;
	
	/** The course. */
	private String course;
	
	/** The students. */
	public static ArrayList<String> students;
	
	/** The quiz selection button. */
	private Button quizSelectionBtn;
	
	/** The back button. */
	private Button backBtn;
	
	/** The recovery button. */
	private Button recoveryBtn;
	
	/** The adapter. */
	private ArrayAdapter<String> adapter;
	
	/** The PIN code. */
	private CharSequence PINCODE;
	
	/** The upload folder to Dropbox Asynctask. */
	private UploadFolderDB uploadFolderDB;
	
	/** The lock a. */
	public static Object lockA;
	
	/** The in recovery. */
	public static boolean inRecovery;
	
	/** The finished upload recovery. */
	public static boolean finishedUploadRecovery;
	
	/** The alert. */
	public static AlertDialog alert = null;
	
	/** The recovery pressed. */
	public static boolean recoveryPressed = false;

	/**
	 * Instantiates a new lect student reg list controller.
	 *
	 * @param activity the activity
	 * @param course the course
	 */
	public LectStudentRegListController(MainActivity activity, String course) {
		super();
		this.course = course;
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_studentreglist);
		listview = (ListView) this.activity.findViewById(R.id.studentListView);
		initView();
		LectQuizSelectionController.studentsAnswersPath = "";
		LectQuizInitiationController.selectedTimePeriodInt = 0;
		serverBT = new ServerBT(this.activity, new LectMessageHandler(this.activity, this), course);/////////////////////////////
		PINCODE = ((TextView) this.activity.findViewById(R.id.PINCodeTxt)).getText();
		lockA = new Object();
		inRecovery = false;
		finishedUploadRecovery = true;
		alert = null;
		recoveryPressed = false;

	}

	/**
	 * Turn charSequence to byte array.
	 *
	 * @param charSequence the char sequence
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
	 * Initiates the view.
	 */
	private void initView() {
		// TODO Auto-generated method stub
		students = getStudentsListFromDB();
		populateList(students);

		quizSelectionBtn = (Button) activity.findViewById(R.id.quizSelectionBtn);
		quizSelectionBtn.setOnClickListener(new quizSelectionBtnListener());
		backBtn = (Button) activity.findViewById(R.id.backBtnStudRegList);
		backBtn.setOnClickListener(new backBtnListener());
		recoveryBtn = (Button) activity.findViewById(R.id.RecoveryBtn);
		recoveryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(activity.getApplicationContext(), "There is no data to recover",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}

	/**
	 * Gets the students list from Dropbox.
	 *
	 * @return the students list from Dropbox
	 */
	private ArrayList<String> getStudentsListFromDB() {
		// TODO Auto-generated method stub
		students = new ArrayList<>();
		filelist = activity.getFilelist();
		try {
			File studentFolder = new File(filelist.getCanonicalPath() + "/" + course + "/Students");
			if (studentFolder.list() != null)
				for (int i = 0; i < studentFolder.list().length; i++) {
					students.add(studentFolder.list()[i].split("\\.")[0]);
				}
			else {
				// Do something to notice the user that his students folder is
				// empty
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return students;
	}

	/**
	 * Populate list with students.
	 *
	 * @param students the students
	 */
	private void populateList(ArrayList<String> students) {
		// TODO Auto-generated method stub
		listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);
		listview.setTextFilterEnabled(true);
		adapter = new ArrayAdapter<String>(this.activity, android.R.layout.simple_list_item_multiple_choice, students);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new itemListener());

		// listview.setItemChecked(2,true);

	}

	/**
	 * {@link OnItemClickListener} for the items in the list.
	 *
	 * @see itemEvent
	 */
	class itemListener implements OnItemClickListener {

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub

			if (!listview.isItemChecked(position)) {
				listview.setItemChecked(position, true);
				Toast toast = Toast.makeText(activity.getApplicationContext(),
						(String) parent.getItemAtPosition(position) + " is connected", Toast.LENGTH_SHORT);
				toast.show();
			} else {
				listview.setItemChecked(position, false);
				Toast toast = Toast.makeText(activity.getApplicationContext(),
						(String) parent.getItemAtPosition(position) + " is not connected", Toast.LENGTH_SHORT);
				toast.show();
			}

		}

	}

	/**
	 * {@link OnClickListener} for the quiz selection button.
	 *
	 * @see quizSelectionBtnEvent
	 */
	class quizSelectionBtnListener implements View.OnClickListener {
		
		/** The builder. */
		AlertDialog.Builder builder;

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			if (isChecked()) {
				builder = new AlertDialog.Builder(activity);

				builder.setTitle("Confirm");
				builder.setMessage("Are you sure?");

				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog

						dialog.dismiss();
						if (LectMessageHandler.lecturerServiceStarted) {
							ClosingService.thread.interrupt();
							LectMessageHandler.lecturerServiceStarted = false;
						}
						new LectQuizSelectionController(activity, LectStudentRegListController.this, course, 0);
					}
				});

				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						// Do nothing
						dialog.dismiss();
					}
				});
				alert = builder.create();
				alert.show();
			} else {
				Toast toast = Toast.makeText(activity.getApplicationContext(),
						"There must be at least one connected student to continue", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	/**
	 * {@link OnClickListener} for the back button.
	 *
	 * @see backBtnEvent
	 */
	class backBtnListener implements View.OnClickListener {
		
		/** The builder. */
		AlertDialog.Builder builder;

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			builder = new AlertDialog.Builder(activity);
			if (isChecked()) {
				builder.setTitle("Confirm");
				builder.setMessage("Some students have already connected,\nare you sure you want to go back?");
			} else {
				builder.setTitle("Confirm");
				builder.setMessage("Are you sure?");
			}
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Do nothing but close the dialog

					dialog.dismiss();
					if (serverBT != null) {
						serverBT.stop();
						serverBT = null;
					}
					if (LectMessageHandler.lecturerServiceStarted) {
						ClosingService.thread.interrupt();
						LectMessageHandler.lecturerServiceStarted = false;
					}
					new LectCourseSelectionController(activity);
				}
			});

			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					// Do nothing
					dialog.dismiss();
				}
			});
			alert = builder.create();
			alert.show();

		}

	}

	/**
	 * {@link OnClickListener} for the recovery button.
	 *
	 * @see recoveryBtnEvent
	 */
	class recoveryBtnListener implements View.OnClickListener {

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			synchronized (lockA) {
				try {
					if (finishedUploadRecovery && !recoveryPressed) {
						recoveryPressed = true;
						Toast toast = Toast.makeText(activity.getApplicationContext(),
								"Files are being uploaded to the database", Toast.LENGTH_SHORT);
						toast.show();
						uploadFolderDB = new UploadFolderDB(
								activity.getApplicationContext().getFilesDir().getCanonicalPath(), activity, false,
								LectStudentRegListController.this);
						uploadFolderDB.execute(
								activity.getFilelist().getCanonicalPath() + "/" + Constants.APP_NAME + ".zip", "/");
						v.clearAnimation();
					} else {
						Toast toast = Toast.makeText(activity.getApplicationContext(),
								"Wait for past recovery to be finished", Toast.LENGTH_SHORT);
						toast.show();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * Return the student position in the list.
	 *
	 * @param Id the id of the student
	 * @param studentsInClass the students in class list
	 * @return the position
	 */
	public static int studentPosInList(String Id, ArrayList<String> studentsInClass) {
		for (int i = 0; i < studentsInClass.size(); i++) {
			if (studentsInClass.get(i).equals(Id)) {
				return i;
			}
		}
		return -1;

		////////////////////////////////////////
		///// the student is not in the list
		////////////////////////////////////////

	}

	/**
	 * Checks if a student is checked.
	 *
	 * @return true, if is checked
	 */
	private boolean isChecked() {
		for (int i = 0; i < students.size(); i++) {
			if (listview.isItemChecked(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This function checks if an item in the list is checked
	 *
	 * @param pos the position to check
	 * @return true, if successful
	 */
	public static boolean receivePos(int pos) {
		if (!listview.isItemChecked(pos)) {
			listview.setItemChecked(pos, true);
			return true;
		} else
			return false;
	}

	/**
	 * Mark pos in finish list.
	 *
	 * @param pos the pos
	 */
	private void markPosInFinishList(int pos) {
		LectQuizProgressController.listView.setItemChecked(pos, true);
	}

	/**
	 * Retrieve view if the user goes back from quiz selection screen.
	 */
	public void retrieveView() {

		activity.setContentView(R.layout.lect_studentreglist);

		((TextView) activity.findViewById(R.id.PINCodeTxt)).setText(PINCODE);
		quizSelectionBtn = (Button) activity.findViewById(R.id.quizSelectionBtn);
		quizSelectionBtn.setOnClickListener(new quizSelectionBtnListener());
		backBtn = (Button) activity.findViewById(R.id.backBtnStudRegList);
		backBtn.setOnClickListener(new backBtnListener());
		recoveryBtn = (Button) activity.findViewById(R.id.RecoveryBtn);
		recoveryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(activity.getApplicationContext(), "There is no data to recover",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		});
		ListView tempListView = (ListView) activity.findViewById(R.id.studentListView);
		tempListView.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);
		tempListView.setTextFilterEnabled(true);
		tempListView.setAdapter(adapter);
		for (int i = 0; i < adapter.getCount(); i++)
			tempListView.setItemChecked(i, listview.isItemChecked(i));
		tempListView.setOnItemClickListener(new itemListener());
		listview = tempListView;
	}

}
