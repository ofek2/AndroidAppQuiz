package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;

import com.example.onlinequizchecker.LectStudentRegListController.quizSelectionBtnListener;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * The Class OnTaskRemovedUpload.
 * This class is an AsyncTask which uploads the lecturer's files in a case he exits 
 * the application during quiz time.
 */
public class OnTaskRemovedUpload extends AsyncTask<String, Integer, Long> {

	/** The path to delete. */
	private String pathToDelete;

	/** The application context. */
	private Context applicationContext;

	/**
	 * Instantiates a new on task removed upload.
	 *
	 * @param pathToDelete
	 *            the path to delete
	 * @param applicationContext
	 *            the application context
	 */
	public OnTaskRemovedUpload(String pathToDelete, Context applicationContext) {
		super();
		this.pathToDelete = pathToDelete;
		this.applicationContext = applicationContext;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Long doInBackground(String... params) {
		DropBoxSimple.uploadFolder(new File(params[0]), params[1]);
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	protected void onPostExecute(Long result) {
	}

}
