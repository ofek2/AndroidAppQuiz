package com.example.onlinequizchecker;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 311165906 on 08/05/2016.
 */
public class LectQuizProgressController {
    private MainActivity activity;
    private ArrayList<String> students;
    public static ListView listView;
    private Button finishBtn;

    public LectQuizProgressController(MainActivity activity)
    {
        this.activity=activity;
        students = LectStudentRegListController.students;
        this.activity.setContentView(R.layout.lect_quizprogressview);
        listView = (ListView)activity.findViewById(R.id.studentsFinalListView);
        populateList(students);
        finishBtn = (Button)activity.findViewById(R.id.finishBtn);
        finishBtn.setOnClickListener(new finishBtnListener());
    }
    private void populateList(ArrayList<String> students) {
        // TODO Auto-generated method stub
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
        listView.setTextFilterEnabled(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity,
                android.R.layout.simple_list_item_multiple_choice, students);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new itemListener());

        //listview.setItemChecked(2,true);

    }
    class itemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub

            if (!listView.isItemChecked(position)){
                listView.setItemChecked(position, true);
                Toast toast = Toast.makeText(activity.getApplicationContext(), (String) parent.getItemAtPosition(position) + " has finished",
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                listView.setItemChecked(position, false);
                Toast toast = Toast.makeText(activity.getApplicationContext(), (String) parent.getItemAtPosition(position)  + " hasn't finished yet",
                        Toast.LENGTH_SHORT);
                toast.show();
            }

        }

    }
    class finishBtnListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            // handle quiz ending.
        }
    }
}
