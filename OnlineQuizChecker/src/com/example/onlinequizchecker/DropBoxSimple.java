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
 * The Class DropBoxSimple.
 */
public class DropBoxSimple {
    
    /** The Constant APP_KEY. */
    final static private String APP_KEY = "6uzu0uyprajxb0p";
    
    /** The Constant APP_SECRET. */
    final static private String APP_SECRET = "e7iwzdqp4rwtu88";

    /** The root path. */
    private static String rootPath;
    
    /** The Dropbox api component. */
    public static DropboxAPI<AndroidAuthSession> mDBApi;
    
    /** The activity. */
    private static MainActivity activity;
    
    /**
     * Instantiates a new drop box simple.
     *
     * @param activity the activity
     */
    public DropBoxSimple(MainActivity activity)

    {
    	
    	try {
			rootPath = new File(".").getCanonicalPath() + "\\"+Constants.APP_NAME;
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
    
    /**
     * Upload folder to Dropbox.
     *
     * @param file the folder to upload
     * @param path the upload path
     */
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
//					Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
				}
				for (File f : file.listFiles()) {
					uploadFolder(f, path);
				}
			}
				} catch (IOException | DropboxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//					Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
				}
		} else {
			
				FileInputStream in;
				try {
					in = new FileInputStream(file);
					mDBApi.putFileOverwrite(path + file.getName(), in, file.length(), null);
				} catch (FileNotFoundException | DropboxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//					Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
				}
			
				
			
		}

	}
    
    /**
     * Download folder from Dropbox.
     *
     * @param path the path to download the files to
     * @param dropPath the path to download from Dropbox
     */
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
//					Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
				}
				DropboxFileInfo info = mDBApi.getFile(dropPath, null, outputStream, null);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
//			Toast.makeText(activity.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
		}
	}
}
