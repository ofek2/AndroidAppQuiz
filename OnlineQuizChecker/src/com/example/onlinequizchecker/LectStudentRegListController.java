package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.example.onlinequizchecker.LectQuizProgressController.itemListener;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
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
	public static ServerBT serverBT;
	private File filelist;
	private String course;
	public static ArrayList<String> students;
	private Button quizSelectionBtn;
	private Button backBtn;
	private ArrayAdapter<String> adapter;
	private CharSequence PINCODE;
	
	public LectStudentRegListController(MainActivity activity,String course) {
		super();
		this.course = course;
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_studentreglist);
		listview = (ListView) activity.findViewById(R.id.studentListView);
		initView();
		serverBT = new ServerBT(activity,new LectMessageHandler());/////////////////////////////
		PINCODE =((TextView) activity.findViewById(R.id.PINCodeTxt)).getText();
//		serverBT.start(listview);///////////////////////////////////


	}

	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            FragmentActivity activity = getActivity();
            switch (msg.what) {
//                case Constants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case BluetoothChatService.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            mConversationArrayAdapter.clear();
//                            break;
//                        case BluetoothChatService.STATE_CONNECTING:
//                            setStatus(R.string.title_connecting);
//                            break;
//                        case BluetoothChatService.STATE_LISTEN:
//                        case BluetoothChatService.STATE_NONE:
//                            setStatus(R.string.title_not_connected);
//                            break;
//                    }
//                    break;
//                case Constants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
//                    break;
                case Constants.MESSAGE_READ:

					if (msg.arg2==-1)
						markPosInFinishList(studentPosInList((String)msg.obj,LectQuizInitiationController.studentsInClass));
					else {
//						byte[] readBuf = (byte[]) msg.obj;
//						// construct a string from the valid bytes in the buffer
//						String readMessage = new String(readBuf, 0, msg.arg1);
						String readMessage = (String)msg.obj;
//                    int pos = Integer.parseInt((Character.toString((char) readBuf[0])));
						if(!receivePos(studentPosInList(readMessage,students)))
						{
				            byte [] sendMsg = toByteArray("This id is already connected");
							serverBT.mConnThreads.get(msg.arg2).write(sendMsg);
						}
					}
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
//                case Constants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    if (null != activity) {
//                        Toast.makeText(activity, "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                case Constants.MESSAGE_TOAST:
//                    if (null != activity) {
//                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                    break;
            }
        }
    };

	
	
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

        @Override
        public void onClick(View v) {
          
            new LectQuizSelectionController(activity,LectStudentRegListController.this,course,0);
        }
    }
	class backBtnListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			boolean studentsAlreadyLoggedIn=false;
			for(int i=0;i<listview.getChildCount();i++)
				if(listview.getChildAt(i).isSelected())
					studentsAlreadyLoggedIn=true;
			if(studentsAlreadyLoggedIn)
			{
				Toast toast = Toast.makeText(activity.getApplicationContext(), "Can't go back, students have already connected"
						,Toast.LENGTH_SHORT);
				toast.show();
			}
			else
			new LectCourseSelectionController(activity);
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
