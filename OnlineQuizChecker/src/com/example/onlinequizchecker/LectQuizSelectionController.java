package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.example.onlinequizchecker.LectStudentRegListController.itemListener;
import com.example.onlinequizchecker.LectStudentRegListController.quizSelectionBtnListener;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Class LectQuizSelectionController.
 * This class controls the quiz selection screen.
 * In the quiz selection screen the lecturer the quiz he wants to initiate.
 */
public class LectQuizSelectionController {
	
	/** The activity. */
	private MainActivity activity;
	
	/** The quizzes. */
	private ArrayList<String> quizzes;
	
	/** The choose quiz button. */
	private Button chooseQuizBtn;
	
	/** The view quiz button. */
	private Button viewQuizBtn;
	
	/** The back button. */
	private Button backBtn;
	
	/** The listview. */
	private ListView listview;
	
	/** The course. */
	private String course;
	
	/** The selected index. */
	private int selectedIndex;
	
	/** The adapter. */
	private ArrayAdapter<String> adapter;
	
	/** The students answers path. */
	public static String studentsAnswersPath;
	
	/** The previous controller. */
	private LectStudentRegListController previousController;
	
	/**
	 * Instantiates a new lect quiz selection controller.
	 *
	 * @param activity the activity
	 * @param previousController the previous controller
	 * @param course the course
	 * @param selectedIndex the selected index
	 */
	public LectQuizSelectionController(MainActivity activity,LectStudentRegListController previousController,String course,int selectedIndex)
	{
		this.activity = activity;
		this.previousController = previousController;
		this.activity.setContentView(R.layout.lect_quizselectionview);
		this.activity.hideKeyboard();
		chooseQuizBtn = (Button)this.activity.findViewById(R.id.chooseQuizBtn);
		chooseQuizBtn.setOnClickListener(new chooseQuizBtnListener());
		viewQuizBtn = (Button)this.activity.findViewById(R.id.viewQuizBtn);
		viewQuizBtn.setOnClickListener(new viewQuizBtnListener());
		listview = (ListView)this.activity.findViewById(R.id.quizzesListView);
		backBtn = (Button)this.activity.findViewById(R.id.backBtnQuizSelect);
		backBtn.setOnClickListener(new backBtnListener());
		this.selectedIndex = selectedIndex;
		this.course = course;
		studentsAnswersPath = "";
		initView();
	}
	
	/**
	 * Initiates the view.
	 */
	private void initView() {
		// TODO Auto-generated method stub
		quizzes = getQuizzesListFromDB();
		populateList(quizzes);
	}

	/**
	 * Gets the quizzes list from Dropbox.
	 *
	 * @return the quizzes list from Dropbox
	 */
	private ArrayList<String> getQuizzesListFromDB() {
		// TODO Auto-generated method stub
		ArrayList<String> quizzes = new ArrayList<>();
		File filelist;
		filelist = activity.getFilelist();
		try {
			File quizzesFolder = new File(filelist.getCanonicalPath()+"/"+course+"/Quizzes");
			if(quizzesFolder.list()!=null)
			for(int i=0;i<quizzesFolder.list().length;i++)
			{
				quizzes.add(quizzesFolder.list()[i]);
			}
			else{
				//Do something to notice the user that his quizzes folder is empty
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return quizzes;
	}

	/**
	 * Populate list with quizzes.
	 *
	 * @param quizzes the quizzes
	 */
	private void populateList(ArrayList<String> quizzes) {
		// TODO Auto-generated method stub
		listview.setChoiceMode(listview.CHOICE_MODE_SINGLE);
		listview.setTextFilterEnabled(true);
		adapter = new ArrayAdapter<String>(this.activity,
				android.R.layout.simple_list_item_single_choice, quizzes);
		listview.setAdapter(adapter);
		listview.setItemChecked(selectedIndex, true);
		listview.setOnItemClickListener(new itemListener());
	}
	
	/**
	 * {@link OnItemClickListener} for the items in the quizzes list.
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
			selectedIndex = position;	
		}

	}
	
	/**
	 * {@link OnClickListener} for the choose quiz button.
	 *
	 * @see chooseQuizBtnEvent
	 */
	class chooseQuizBtnListener implements View.OnClickListener
	{
		
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
		
			// Send quiz to students
			new LectQuizInitiationController(activity,LectQuizSelectionController.this,course,adapter.getItem(selectedIndex));
			try {
				studentsAnswersPath = activity.getFilelist().getCanonicalPath()+"/"+course+"/Quizzes/"+
						adapter.getItem(selectedIndex)+ "/StudentsAnswers/";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    
	/**
	 * {@link OnClickListener} for the view quiz button.
	 *
	 * @see viewQuizBtnEvent
	 */
	class viewQuizBtnListener implements View.OnClickListener
	{
		
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			new LectViewQuizController(activity, LectQuizSelectionController.this,course, adapter.getItem(selectedIndex),selectedIndex);
		}
		
	}
	
	/**
	 * {@link OnClickListener} for the back button.
	 *
	 * @see backBtnEvent
	 */
	class backBtnListener implements View.OnClickListener
	{

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			previousController.retrieveView();
		}
		
	}
	
	/**
	 * Retrieve view after returning from the quiz viewing screen.
	 */
	public void retrieveView()
	{
		activity.setContentView(R.layout.lect_quizselectionview);
		chooseQuizBtn = (Button)this.activity.findViewById(R.id.chooseQuizBtn);
		chooseQuizBtn.setOnClickListener(new chooseQuizBtnListener());
		viewQuizBtn = (Button)this.activity.findViewById(R.id.viewQuizBtn);
		viewQuizBtn.setOnClickListener(new viewQuizBtnListener());
		listview = (ListView)this.activity.findViewById(R.id.quizzesListView);
		backBtn = (Button)this.activity.findViewById(R.id.backBtnQuizSelect);
		backBtn.setOnClickListener(new backBtnListener());
		populateList(quizzes);
	}
	
}
