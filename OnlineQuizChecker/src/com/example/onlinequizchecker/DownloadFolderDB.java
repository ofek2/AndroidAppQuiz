package com.example.onlinequizchecker;

import java.io.File;

import android.os.AsyncTask;

public class DownloadFolderDB extends AsyncTask<String, Integer, Long>{
	private MainActivity activity;
	private String appFolder;
	public DownloadFolderDB(MainActivity activity) {
		// TODO Auto-generated constructor stub
		super();
		this.activity = activity;
	}
	@Override
	protected Long doInBackground(String... params) {
		appFolder = params[0];
		DropBoxSimple.downloadFolder(params[0], params[1]);
		return null;
		// TODO Auto-generated method stub
		
	}
	
	 protected void onPostExecute(Long result) {
		 File downloadedZipFile = new File(appFolder+"/"+Constants.APP_NAME+".zip");
		 if(downloadedZipFile.exists())
		 {
			 zipFileManager.unZipIt(appFolder+"/"+Constants.APP_NAME+".zip", appFolder+"/"+Constants.APP_NAME);
//			 downloadedZipFile.delete();
		 }
		 else
			 new File(appFolder+"/"+Constants.APP_NAME).mkdir(); //Dropbox folder is empty
		 
		 new LectCourseSelectionController(activity);
		 
     }
}
