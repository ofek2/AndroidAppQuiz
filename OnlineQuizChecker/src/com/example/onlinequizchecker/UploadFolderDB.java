package com.example.onlinequizchecker;

import java.io.File;

import android.os.AsyncTask;

public class UploadFolderDB extends AsyncTask<String, Integer, Long>{
	public UploadFolderDB() {
		// TODO Auto-generated constructor stub
		super();
	}
	@Override
	protected Long doInBackground(String... params) {
		DropBoxSimple.uploadFolder(new File(params[0]), params[1]);
		return null;
		// TODO Auto-generated method stub
		
	}

}
