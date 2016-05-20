package com.example.onlinequizchecker;

import java.io.File;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.widget.Toast;

public class UploadFolderDB extends AsyncTask<String, Integer, Long>{
	private String pathToDelete;
	private MainActivity activity;
	public UploadFolderDB(String pathToDelete, MainActivity activity) {
		super();
		this.pathToDelete = pathToDelete;
		this.activity = activity;
	}
	@Override
	protected Long doInBackground(String... params) {
		DropBoxSimple.uploadFolder(new File(params[0]), params[1]);
		return null;
		// TODO Auto-generated method stub
		
	}
	 protected void onPostExecute(Long result) {
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
}
