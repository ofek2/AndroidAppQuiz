package com.example.onlinequizchecker;

import java.io.File;

import android.os.AsyncTask;

/**
 * The Class DownloadFolderDB.
 * This class is used for downloading a lecturer data from his Dropbox account.
 */
public class DownloadFolderDB extends AsyncTask<String, Integer, Long>{
	
	/** The activity. */
	private MainActivity activity;
	
	/** The app folder. */
	private String appFolder;
	
	/**
	 * Instantiates a new download folder db.
	 *
	 * @param activity the activity
	 */
	public DownloadFolderDB(MainActivity activity) {
		// TODO Auto-generated constructor stub
		super();
		this.activity = activity;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Long doInBackground(String... params) {
		appFolder = params[0];
		DropBoxSimple.downloadFolder(params[0], params[1]);
		return null;
		// TODO Auto-generated method stub
		
	}
	
	 /* (non-Javadoc)
 	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
 	 */
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
