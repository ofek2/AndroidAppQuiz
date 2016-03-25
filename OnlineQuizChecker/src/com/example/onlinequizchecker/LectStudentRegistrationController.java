package com.example.onlinequizchecker;

import android.app.ListActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by 311165906 on 10/03/2016.
 */
public class LectStudentRegistrationController  extends ListActivity{
    private MainActivity activity;

    public LectStudentRegistrationController(MainActivity activity) {
        this.activity = activity;
        this.activity.setContentView(R.layout.lect_studentregistrationview);

        // -- Display mode of the ListView
        ArrayList<String> students = new ArrayList<String>();
        ListView listview= (ListView)activity.findViewById(R.id.studentListView);
        //	listview.setChoiceMode(listview.CHOICE_MODE_NONE);
        //	listview.setChoiceMode(listview.CHOICE_MODE_SINGLE);
        listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);

        //--	text filtering
        listview.setTextFilterEnabled(true);

        listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, students));

    }
}
