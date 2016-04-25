package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.example.onlinequizchecker.LectStudentRegListController.itemListener;
import com.example.onlinequizchecker.LectStudentRegListController.quizSelectionBtnListener;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class LectQuizSelectionController {
	private MainActivity activity;
	private ArrayList<String> quizzes;
	private Button chooseQuizBtn;
	private ListView listview;
	private String course;
	public LectQuizSelectionController(MainActivity activity,String course)
	{
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_quizselectionview);
		chooseQuizBtn = (Button)this.activity.findViewById(R.id.chooseQuizBtn);
		chooseQuizBtn.setOnClickListener(new chooseQuizBtnListener());
		listview = (ListView)this.activity.findViewById(R.id.quizzesListView);
		this.course = course;
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		quizzes = getQuizzesListFromDB();
		populateList(quizzes);
	}

	private ArrayList<String> getQuizzesListFromDB() {
		// TODO Auto-generated method stub
		ArrayList<String> quizzes = new ArrayList<>();
		File filelist;
		filelist = activity.getFilelist();
		try {
			File quizzesFolder = new File(filelist.getCanonicalFile()+"/"+course+"/Quizzes");
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

	private void populateList(ArrayList<String> quizzes) {
		// TODO Auto-generated method stub
		listview.setChoiceMode(listview.CHOICE_MODE_SINGLE);
		listview.setTextFilterEnabled(true);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity,
				android.R.layout.simple_list_item_single_choice, quizzes);
		listview.setAdapter(adapter);
		//listview.setItemChecked(2,true);

	}
	
	class chooseQuizBtnListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Send quiz to students
		}
		
	}
}
