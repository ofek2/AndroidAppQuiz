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

public class LectStudentRegListController extends ListActivity {
	private MainActivity activity;
	public static ListView listview;
	public static ServerBT serverBT = null;
	private File filelist;
	private String course;
	public static ArrayList<String> students;
	private Button quizSelectionBtn;
	private Button backBtn;
	private Button recoveryBtn;
	private ArrayAdapter<String> adapter;
	private CharSequence PINCODE;
	private UploadFolderDB uploadFolderDB;
	public static Object lockA;
	public static boolean inRecovery;
	public LectStudentRegListController(MainActivity activity,String course) {
		super();
		this.course = course;
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_studentreglist);
		listview = (ListView) this.activity.findViewById(R.id.studentListView);
		initView();
		LectQuizSelectionController.studentsAnswersPath = "";
		LectQuizInitiationController.selectedTimePeriodInt=0;
		serverBT = new ServerBT(this.activity,new LectMessageHandler(this.activity,this),course);/////////////////////////////
		PINCODE =((TextView) this.activity.findViewById(R.id.PINCodeTxt)).getText();	
		lockA = new Object();
		inRecovery = false;
		try {
			uploadFolderDB = new UploadFolderDB(activity.getApplicationContext().getFilesDir().getCanonicalPath(),activity,false,
					LectStudentRegListController.this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		serverBT.start(listview);///////////////////////////////////


	}

	
	
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
    
	private void initView() {
		// TODO Auto-generated method stub
		students = getStudentsListFromDB();
		populateList(students);
	
		
		quizSelectionBtn = (Button)activity.findViewById(R.id.quizSelectionBtn);
		quizSelectionBtn.setOnClickListener(new quizSelectionBtnListener());
		backBtn = (Button)activity.findViewById(R.id.backBtnStudRegList);
		backBtn.setOnClickListener(new backBtnListener());
		recoveryBtn = (Button)activity.findViewById(R.id.RecoveryBtn);
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

	private ArrayList<String> getStudentsListFromDB() {
		// TODO Auto-generated method stub
		students = new ArrayList<>();
		filelist = activity.getFilelist();
		try {
			File studentFolder = new File(filelist.getCanonicalPath()+"/"+course+"/Students");
			if(studentFolder.list()!=null)
			for(int i=0;i<studentFolder.list().length;i++)
			{
				students.add(studentFolder.list()[i].split("\\.")[0]);
			}
			else{
				//Do something to notice the user that his students folder is empty
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
		adapter = new ArrayAdapter<String>(this.activity,
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
	class quizSelectionBtnListener implements View.OnClickListener
    {
		AlertDialog.Builder builder;
        @Override
        public void onClick(View v) {
        	if(isChecked())
        	{
        	builder = new AlertDialog.Builder(activity);

		    builder.setTitle("Confirm");
		    builder.setMessage("Are you sure?");

		    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    	@Override
		        public void onClick(DialogInterface dialog, int which) {
		            // Do nothing but close the dialog
		        	
		            dialog.dismiss();
		            new LectQuizSelectionController(activity,LectStudentRegListController.this,course,0);
		        }
		    });

		    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

		        @Override
		        public void onClick(DialogInterface dialog, int which) {

		            // Do nothing
		            dialog.dismiss();
		        }
		    });
		    AlertDialog alert = builder.create();
		    alert.show();
        	}
        	else
        	{
				Toast toast = Toast.makeText(activity.getApplicationContext(), "There must be at least one connected student to continue"
						,Toast.LENGTH_SHORT);
				toast.show();
        	}
        }
    }
	class backBtnListener implements View.OnClickListener
	{
		AlertDialog.Builder builder;
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			builder = new AlertDialog.Builder(activity);
			if (isChecked()) {
				builder.setTitle("Confirm");
				builder.setMessage("Some students have already connected,\nare you sure you want to go back?");
			} 
			else 
			{
				builder.setTitle("Confirm");
				builder.setMessage("Are you sure?");
			}
		    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    	@Override
		        public void onClick(DialogInterface dialog, int which) {
		            // Do nothing but close the dialog
		        	
		            dialog.dismiss();
					if(serverBT != null)
						serverBT.stop();
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
		    AlertDialog alert = builder.create();
		    alert.show();
			
		}
		
	}
	class recoveryBtnListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			synchronized (lockA) {							
			try {
				inRecovery = true;
//				boolean executeOnPost = false;
				///
				recoveryBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast toast = Toast.makeText(activity.getApplicationContext(), "Files are being uploaded to the database",
								Toast.LENGTH_SHORT);
						toast.show();
					}
				});
				Toast toast = Toast.makeText(activity.getApplicationContext(), "Files are being uploaded to the database"
						,Toast.LENGTH_SHORT);
				toast.show();
//				new UploadFolderDB(activity.getApplicationContext().getFilesDir().getCanonicalPath(),activity,executeOnPost,
//						LectStudentRegListController.this).
//				execute(activity.getFilelist().getCanonicalPath() + "/"+Constants.APP_NAME+".zip", "/");
				uploadFolderDB.setRecoveyBtnView(v);
				uploadFolderDB.execute(activity.getFilelist().getCanonicalPath() + "/"+Constants.APP_NAME+".zip", "/");
//				if (LectMessageHandler.inRecoveryMode<2) {
//					v.clearAnimation();
//				}
				v.clearAnimation();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
	}
	public static int studentPosInList(String Id,ArrayList<String> studentsInClass)
	{
		for (int i = 0; i < studentsInClass.size(); i++) {
			if (studentsInClass.get(i).equals(Id)) {
//				if(posInConnectedThreadList!=-1)
//				ServerBT.mConnThreads.get(posInConnectedThreadList).setStudentId(Id);
				return i;
			}
		}
		return -1;
		
		
		
		
		////////////////////////////////////////
		/////the student is not in the list
		////////////////////////////////////////
		
		
		
		
	}
	
    private boolean isChecked()
    {
		for (int i = 0; i < students.size(); i++) {
			if(listview.isItemChecked(i))
			{
				return true;
			}
		}
		return false;
    }
    
	public static boolean receivePos(int pos)
	{
		if(!listview.isItemChecked(pos)){
			listview.setItemChecked(pos, true);
			return true;
		}
		else
			return false;
	}

	private void markPosInFinishList(int pos)
	{
		LectQuizProgressController.listView.setItemChecked(pos, true);	
	}
	public void retrieveView()
    {
		
    	activity.setContentView(R.layout.lect_studentreglist);
    	
    	((TextView) activity.findViewById(R.id.PINCodeTxt)).setText(PINCODE);
    	quizSelectionBtn = (Button)activity.findViewById(R.id.quizSelectionBtn);
		quizSelectionBtn.setOnClickListener(new quizSelectionBtnListener());
		backBtn = (Button)activity.findViewById(R.id.backBtnStudRegList);
		backBtn.setOnClickListener(new backBtnListener());
		recoveryBtn = (Button)activity.findViewById(R.id.RecoveryBtn);
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
    	for(int i=0; i < adapter.getCount();i++)
    		tempListView.setItemChecked(i, listview.isItemChecked(i));
    	tempListView.setOnItemClickListener(new itemListener());
    	listview = tempListView;
    }

}
