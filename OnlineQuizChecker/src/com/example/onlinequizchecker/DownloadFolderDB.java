package com.example.onlinequizchecker;

import android.os.AsyncTask;

public class DownloadFolderDB extends AsyncTask<String, Integer, Long>{
	private MainActivity activity;
	public DownloadFolderDB(MainActivity activity) {
		// TODO Auto-generated constructor stub
		super();
		this.activity = activity;
	}
	@Override
	protected Long doInBackground(String... params) {
		DropBoxSimple.downloadFolder(params[0], params[1]);
		return null;
		// TODO Auto-generated method stub
		
	}
	
	 protected void onPostExecute(Long result) {
		 new LectStudentRegistrationController(activity);
		 
     }
}
