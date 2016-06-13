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

public class OnTaskRemovedUpload extends AsyncTask<String, Integer, Long>{
	private String pathToDelete;
	private Context applicationContext;

	public OnTaskRemovedUpload(String pathToDelete, Context applicationContext) {
		super();
		this.pathToDelete = pathToDelete;
		this.applicationContext = applicationContext;

	}

	
	@Override
	protected Long doInBackground(String... params) {
		
			DropBoxSimple.uploadFolder(new File(params[0]), params[1]);			
			return null;
		}
		// TODO Auto-generated method stub
		
	
	 protected void onPostExecute(Long result) {
		 LectDownloadProgress.folderRecursiveDelete(new File(pathToDelete+"/"+Constants.APP_NAME));
		 //////////////////////////////////
         
//       close the connection to the students
//         LectStudentRegListController.serverBT.stop();/////////////////////////////////////////////////////////

         //////////////////////////////////
		 Toast toast = Toast.makeText(applicationContext,  "The files were successfully saved",
                    Toast.LENGTH_SHORT);
         toast.show();
		 }
		
	 
    
}
