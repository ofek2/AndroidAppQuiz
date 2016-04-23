package com.example.onlinequizchecker;

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
	private ListView listview;
	private ServerBT serverBT;
	public LectStudentRegListController(MainActivity activity) {
		super();
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_studentreglist);
		listview = (ListView) activity.findViewById(R.id.studentListView);
		serverBT = new ServerBT(activity);/////////////////////////////
		initView();
		serverBT.start(listview);///////////////////////////////////


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
		students.add("David");
		students.add("Asaf");
		students.add("Shlomo");
		students.add("Yakov");
		students.add("David");
		students.add("Asaf");
		students.add("Shlomo");
		students.add("Yakov");
		students.add("David");
		students.add("Asaf");
		students.add("Shlomo");
		students.add("Yakov");
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
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity,
				android.R.layout.simple_list_item_multiple_choice, students);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new itemListener());
		
		 listview.setItemChecked(2,true);

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
