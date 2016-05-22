package com.example.onlinequizchecker;

import java.io.IOException;

public class LectUploadProgress {
	private MainActivity activity;
	public LectUploadProgress(MainActivity activity)
	{
		this.activity=activity;
		this.activity.setContentView(R.layout.lect_uploadprogress);
		
		String path;
		try {
			path = activity.getApplicationContext().getFilesDir().getCanonicalPath();
			startUpload(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	}
	
	private void startUpload(String path) {
		// TODO Auto-generated method stub

		try {
			new UploadFolderDB(path,activity,true).
				execute(activity.getFilelist().getCanonicalPath() + "/"+Constants.APP_NAME+".zip", "/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
}
