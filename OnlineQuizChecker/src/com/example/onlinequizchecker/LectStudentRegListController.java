package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ListActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

public class LectStudentRegListController extends ListActivity {
	private MainActivity activity;
	public static ListView listview;
	public static ServerBT serverBT;
	private File filelist;
	private String course;
	public LectStudentRegListController(MainActivity activity,String course) {
		super();
		this.course = course;
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_studentreglist);
		listview = (ListView) activity.findViewById(R.id.studentListView);
		initView();
		serverBT = new ServerBT(activity);/////////////////////////////
		
//		serverBT.start(listview);///////////////////////////////////


	}

	private void initView() {
		// TODO Auto-generated method stub
		ArrayList<String> students = getStudentsListFromDB();
		populateList(students);
	}

	private ArrayList<String> getStudentsListFromDB() {
		// TODO Auto-generated method stub
		ArrayList<String> students = new ArrayList<>();
		filelist = activity.getFilelist();
		try {
			File studentFolder = new File(filelist.getCanonicalFile()+"/"+course+"/Students");
			for(int i=0;i<studentFolder.list().length;i++)
			{
				students.add(studentFolder.list()[i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return students;
	}

	private void populateList(ArrayList<String> students) {
		// TODO Auto-generated method stub
		listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);
		listview.setTextFilterEnabled(true);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity,
				android.R.layout.simple_list_item_multiple_choice, students);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new itemListener());
		
		//listview.setItemChecked(2,true);

	}

	class itemListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			
			if (!listview.isItemChecked(position)){
				listview.setItemChecked(position, true);
				Toast toast = Toast.makeText(activity.getApplicationContext(), (String) parent.getItemAtPosition(position) + " is connected",
						Toast.LENGTH_SHORT);
				toast.show();
			} else {
				listview.setItemChecked(position, false);
				Toast toast = Toast.makeText(activity.getApplicationContext(), (String) parent.getItemAtPosition(position)  + " is not connected",
						Toast.LENGTH_SHORT);
				toast.show();
			}

		}

	}

}
