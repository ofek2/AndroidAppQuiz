package com.example.onlinequizchecker;

import android.os.AsyncTask;

public class DownloadFolderDB extends AsyncTask<String, Integer, Long>{
	public DownloadFolderDB() {
		// TODO Auto-generated constructor stub
		super();
	}
	@Override
	protected Long doInBackground(String... params) {
		DropBoxSimple.downloadFolder(params[0], params[1]);
		return null;
		// TODO Auto-generated method stub
		
	}

}
