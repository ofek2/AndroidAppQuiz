package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;

/**
 * The Class LectDownloadProgress.
 * This class is used to initiate the download process from Dropbox to
 * the lecturer's phone.
 */
public class LectDownloadProgress {
	
	/** The activity. */
	private MainActivity activity;
	
	/**
	 * Instantiates a new lecturer download progress.
	 *
	 * @param activity the activity
	 */
	public LectDownloadProgress(MainActivity activity)
	{
		this.activity=activity;
		this.activity.setContentView(R.layout.lect_downloadprogress);
		
		String path;
		try {
			path = activity.getApplicationContext().getFilesDir().getCanonicalPath();
			folderRecursiveDelete(new File(path+"/"+Constants.APP_NAME));
			startDownload(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
	}
	
	/**
	 * Folder recursive delete.
	 *
	 * @param file the file
	 */
	public static void folderRecursiveDelete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
            	folderRecursiveDelete(f);
            }
        }
        file.delete();
    }
	
	/**
	 * Start download.
	 *
	 * @param path the path
	 */
	private void startDownload(String path) {
		// TODO Auto-generated method stub

		new DownloadFolderDB(activity).execute(path, "/");
		
	
	}
}
