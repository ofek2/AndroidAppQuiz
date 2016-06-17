package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;

import com.example.onlinequizchecker.LectStudentRegListController.quizSelectionBtnListener;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * The Class UploadFolderDB.
 * Uploading all of the data to the lecturer Dropbox account
 */
public class UploadFolderDB extends AsyncTask<String, Integer, Long>{
	
	/** The path to delete. */
	private String pathToDelete;
	
	/** The activity. */
	private MainActivity activity;
	
	/** The execute on post. */
	private boolean executeOnPost;
/** The lect student reg list controller. */
	private LectStudentRegListController lectStudentRegListController;
	
	/**
	 * Instantiates a new upload folder db.
	 *
	 * @param pathToDelete the path to delete
	 * @param activity the activity
	 * @param executeOnPost the execute on post
	 * @param lectStudentRegListController the lect student reg list controller
	 */
	public UploadFolderDB(String pathToDelete, MainActivity activity, boolean executeOnPost,
			LectStudentRegListController lectStudentRegListController) {
		super();
		this.pathToDelete = pathToDelete;
		this.activity = activity;
		this.executeOnPost = executeOnPost;
		this.lectStudentRegListController = lectStudentRegListController;
	}
	
	/* (non-Javadoc)
 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
 */
@Override
	protected Long doInBackground(String... params) {
		synchronized (LectStudentRegListController.lockA) {
			LectStudentRegListController.inRecovery = false;
			LectStudentRegListController.finishedUploadRecovery = false;
			DropBoxSimple.uploadFolder(new File(params[0]), params[1]);
			if(!executeOnPost)
			{
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
			}
			return null;
		}
		// TODO Auto-generated method stub
		
	}
	 
 	/* (non-Javadoc)
 	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
 	 */
 	protected void onPostExecute(Long result) {
		 synchronized (LectStudentRegListController.lockA) {
		 if(executeOnPost){
		 LectDownloadProgress.folderRecursiveDelete(new File(pathToDelete+"/"+Constants.APP_NAME));
		 activity.setUserClassification("");        
         LectStudentRegListController.serverBT.stop();
		 BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		 mAdapter.disable();
		 new MainController(activity);
		 Toast toast = Toast.makeText(activity.getApplicationContext(),  "The files were successfully saved",
                    Toast.LENGTH_SHORT);
         toast.show();
		 }
		 else
		 {
			 Toast toast = Toast.makeText(activity.getApplicationContext(), "Recovery finished"
		 	 ,Toast.LENGTH_SHORT);
			 toast.show();
			 LectStudentRegListController.finishedUploadRecovery = true;
			 try {
				File uploadedZipFile = new File(activity.getFilelist().getCanonicalPath() + "/"+Constants.APP_NAME+".zip");
				uploadedZipFile.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	 }
    }
}
