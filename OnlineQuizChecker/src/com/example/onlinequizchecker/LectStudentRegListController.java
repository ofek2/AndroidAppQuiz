package com.example.onlinequizchecker;

import java.util.ArrayList;

import android.app.ListActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LectStudentRegListController extends ListActivity{
	private MainActivity activity;
	private ListView listview;
	public LectStudentRegListController(MainActivity activity) {
		super();
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_studentreglist);
		listview = (ListView)activity.findViewById(R.id.studentListView);
		
		initView();
	
	}
	private void initView() {
		// TODO Auto-generated method stub
		ArrayList<String> students = getStudentsListFromDB();
		populateList(students);
	}
	
	private ArrayList<String> getStudentsListFromDB() {
		// TODO Auto-generated method stub
		ArrayList<String> students = new ArrayList<>();
		students.add("David");
		students.add("Asaf");
		students.add("Shlomo");
		students.add("Yakov");
		return students;
	}
	private void populateList(ArrayList<String> students) {
		// TODO Auto-generated method stub
		listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);
		listview.setTextFilterEnabled(true);
		
		listview.setAdapter(new ArrayAdapter<String>(this.activity, android.R.layout.simple_list_item_multiple_choice, students));
	}
	
}
