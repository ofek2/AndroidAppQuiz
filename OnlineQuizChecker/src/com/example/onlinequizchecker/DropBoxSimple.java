package com.example.onlinequizchecker;




import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by 311165906 on 10/03/2016.
 */
public class DropBoxSimple {
    final static private String APP_KEY = "6uzu0uyprajxb0p";
    final static private String APP_SECRET = "e7iwzdqp4rwtu88";

    private static String rootPath;
    public static DropboxAPI<AndroidAuthSession> mDBApi;
    private static MainActivity activity;
    public DropBoxSimple(MainActivity activity)

    {
    	
    	try {
			rootPath = new File(".").getCanonicalPath() + "\\OnlineQuizChecker";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
			e.printStackTrace();
		}
        this.activity=activity;
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(activity);
        
    }
    public static void uploadFolder(File file, String path) {

		if (!file.exists())
			return;
		if (file.isDirectory()) {
			if (file.listFiles().length == 0)
				try {
					if (!file.getCanonicalPath().equals(rootPath))
						mDBApi.createFolder(path + file.getName());
				
			else {
				try {

					if (!file.getCanonicalPath().equals(rootPath))
						path = path + file.getName() + "/";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
				}
				for (File f : file.listFiles()) {
					uploadFolder(f, path);
				}
			}
				} catch (IOException | DropboxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
				}
		} else {
			
				FileInputStream in;
				try {
					in = new FileInputStream(file);
					mDBApi.putFileOverwrite(path + file.getName(), in, file.length(), null);
				} catch (FileNotFoundException | DropboxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
				}
			
				
			
		}

	}
    
    public static void downloadFolder(String path, String dropPath) {
		FileOutputStream outputStream=null;
		
		try {
			File file = new File(path);

			Entry existingFile = mDBApi.metadata(dropPath, 0, null, true, null);
			if (existingFile.isDir) {
				file.mkdir();
				for (Entry entry : existingFile.contents) {
					downloadFolder(path + "/" + entry.fileName(), dropPath + "/" + entry.fileName());
				}
			} else {
				try {
					outputStream = new FileOutputStream(file);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
				}
				DropboxFileInfo info = mDBApi.getFile(dropPath, null, outputStream, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
		}
	}

    public static ArrayList<String> getCoursesInfo()
    {
    	ArrayList<String> courses = new ArrayList<String>();
    	return null;
    	//mDBApi.
    }
}
