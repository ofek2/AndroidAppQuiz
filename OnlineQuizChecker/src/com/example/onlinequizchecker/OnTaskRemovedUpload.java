package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;

import com.example.onlinequizchecker.LectStudentRegListController.quizSelectionBtnListener;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * The Class OnTaskRemovedUpload.
 */
public class OnTaskRemovedUpload extends AsyncTask<String, Integer, Long>{
	
	/** The path to delete. */
	private String pathToDelete;
	
	/** The application context. */
	private Context applicationContext;

	/**
	 * Instantiates a new on task removed upload.
	 *
	 * @param pathToDelete the path to delete
	 * @param applicationContext the application context
	 */
	public OnTaskRemovedUpload(String pathToDelete, Context applicationContext) {
		super();
		this.pathToDelete = pathToDelete;
		this.applicationContext = applicationContext;

	}

	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Long doInBackground(String... params) {
		
			DropBoxSimple.uploadFolder(new File(params[0]), params[1]);			
			return null;
		}
		// TODO Auto-generated method stub
		
	
	 /* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		protected void onPostExecute(Long result) {
//		 Toast toast = Toast.makeText(applicationContext,  "The files were successfully saved",
//                 Toast.LENGTH_SHORT);
//      toast.show();
//		 LectDownloadProgress.folderRecursiveDelete(new File(pathToDelete+"/"+Constants.APP_NAME));
//		 //////////////////////////////////
//         
////       close the connection to the students
////         LectStudentRegListController.serverBT.stop();/////////////////////////////////////////////////////////
//
//         //////////////////////////////////
//		 toast = Toast.makeText(applicationContext,  "The files were successfully saved",
//                    Toast.LENGTH_SHORT);
//         toast.show();
		 }
		
	 
    
}
