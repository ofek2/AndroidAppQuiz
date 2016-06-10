package com.example.onlinequizchecker;

import java.io.File;

import com.example.onlinequizchecker.LectStudentRegListController.quizSelectionBtnListener;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class UploadFolderDB extends AsyncTask<String, Integer, Long>{
	private String pathToDelete;
	private MainActivity activity;
	private boolean executeOnPost;
	private Object lockUpload;	
	private View RecoveryBtnView;
	private LectStudentRegListController lectStudentRegListController;
	public UploadFolderDB(String pathToDelete, MainActivity activity, boolean executeOnPost,
			LectStudentRegListController lectStudentRegListController) {
		super();
		this.pathToDelete = pathToDelete;
		this.activity = activity;
		this.executeOnPost = executeOnPost;
		this.lectStudentRegListController = lectStudentRegListController;
		lockUpload = new Object();
	}
	public void setRecoveyBtnView(View view)
	{
		RecoveryBtnView = view;
	}
	
	@Override
	protected Long doInBackground(String... params) {
		synchronized (LectStudentRegListController.lockA) {
			LectStudentRegListController.inRecovery = false;
//			RecoveryBtnView.clearAnimation();
			DropBoxSimple.uploadFolder(new File(params[0]), params[1]);
			if(!executeOnPost)
			{
//				RecoveryBtnView.clearAnimation();
//				 if(LectMessageHandler.inRecoveryMode<2)
//				 {
					Button recoveryBtn = (Button)activity.findViewById(R.id.RecoveryBtn);
					recoveryBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Toast toast = Toast.makeText(activity.getApplicationContext(), "There is no data to recover",
									Toast.LENGTH_SHORT);
							toast.show();
						}
					});
				 	Button quizSelectionBtn = (Button)activity.findViewById(R.id.quizSelectionBtn);
					quizSelectionBtn.setOnClickListener(lectStudentRegListController.new quizSelectionBtnListener());
					Button backBtn = (Button)activity.findViewById(R.id.backBtnStudRegList);
					backBtn.setOnClickListener(lectStudentRegListController.new backBtnListener());
//				 }
//				 LectMessageHandler.inRecoveryMode--;
				 Toast toast = Toast.makeText(activity.getApplicationContext(), "Recovery finished"
					 	 ,Toast.LENGTH_SHORT);
				 toast.show();
			}
			return null;
		}
		// TODO Auto-generated method stub
		
	}
	 protected void onPostExecute(Long result) {
		 synchronized (LectStudentRegListController.lockA) {
		 if(executeOnPost){
		 LectDownloadProgress.folderRecursiveDelete(new File(pathToDelete+"/"+Constants.APP_NAME));
		 activity.setUserClassification("");
		 //////////////////////////////////
         
//       close the connection to the students
         LectStudentRegListController.serverBT.stop();

         //////////////////////////////////
		 BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		 mAdapter.disable();
		 new MainController(activity);
		 Toast toast = Toast.makeText(activity.getApplicationContext(),  "The files were successfully saved",
                    Toast.LENGTH_SHORT);
         toast.show();
		 }
		 else
		 {
			 
//			 RecoveryBtnView.clearAnimation();
////			 if(LectMessageHandler.inRecoveryMode<2)
////			 {
//				Button recoveryBtn = (Button)activity.findViewById(R.id.RecoveryBtn);
//				recoveryBtn.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						Toast toast = Toast.makeText(activity.getApplicationContext(), "There is no data to recover",
//								Toast.LENGTH_SHORT);
//						toast.show();
//					}
//				});
//			 	Button quizSelectionBtn = (Button)activity.findViewById(R.id.quizSelectionBtn);
//				quizSelectionBtn.setOnClickListener(lectStudentRegListController.new quizSelectionBtnListener());
//				Button backBtn = (Button)activity.findViewById(R.id.backBtnStudRegList);
//				backBtn.setOnClickListener(lectStudentRegListController.new backBtnListener());
////			 }
////			 LectMessageHandler.inRecoveryMode--;
//			 Toast toast = Toast.makeText(activity.getApplicationContext(), "Recovery finished"
//				 	 ,Toast.LENGTH_SHORT);
//			 toast.show();
		}
	 }
    }
}
