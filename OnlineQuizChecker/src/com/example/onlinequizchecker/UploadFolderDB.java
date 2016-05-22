package com.example.onlinequizchecker;

import java.io.File;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.widget.Toast;

public class UploadFolderDB extends AsyncTask<String, Integer, Long>{
	private String pathToDelete;
	private MainActivity activity;
	private boolean executeOnPost;
	public UploadFolderDB(String pathToDelete, MainActivity activity, boolean executeOnPost) {
		super();
		this.pathToDelete = pathToDelete;
		this.activity = activity;
		this.executeOnPost = executeOnPost;
	}
	@Override
	protected Long doInBackground(String... params) {
		DropBoxSimple.uploadFolder(new File(params[0]), params[1]);
		return null;
		// TODO Auto-generated method stub
		
	}
	 protected void onPostExecute(Long result) {
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
				Toast toast = Toast.makeText(activity.getApplicationContext(), "Recovery finished"
						,Toast.LENGTH_SHORT);
				toast.show();
		 }
     }
}
