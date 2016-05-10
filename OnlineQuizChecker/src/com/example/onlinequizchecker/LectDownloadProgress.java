package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;

public class LectDownloadProgress {
	private MainActivity activity;
	public LectDownloadProgress(MainActivity activity)
	{
		this.activity=activity;
		this.activity.setContentView(R.layout.lect_downloadprogress);
		
		String path;
		try {
//			path = activity.getApplicationContext().getFilesDir().getCanonicalPath()+"/OnlineQuizChecker";
			path = activity.getApplicationContext().getFilesDir().getCanonicalPath();
			folderRecursiveDelete(new File(path+"/OnlineQuizChecker"));
			startDownload(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	
        
	}
	
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
	
	private void startDownload(String path) {
		// TODO Auto-generated method stub

		new DownloadFolderDB(activity).execute(path, "/");
		
	
	}
}
