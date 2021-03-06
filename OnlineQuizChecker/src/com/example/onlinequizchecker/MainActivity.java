package com.example.onlinequizchecker;

import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * The Class MainActivity.
 */
public class MainActivity extends Activity {

	/** The user classification. */
	private String userClassification;

	/** The Bluetooth receiver. */
	private BroadcastReceiver blueToothReceiver = null;

	/** The filelist. */
	private File filelist;

	/** The Dropbox auth request. */
	private boolean DropboxAuthRequest;

	/** The zip files password. */
	public static String zipFilesPassword = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new MainController(this);
		userClassification = "";
		DropboxAuthRequest = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (userClassification.equals(Constants.STUDENT))
			Toast.makeText(getApplicationContext(), "You Are Not Allowed to Exit the App", Toast.LENGTH_SHORT).show();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (userClassification.equals(Constants.LECTURER)) {
			// LectStudentRegListController.listview.setItemChecked(0,true);
			LectStudentRegListController.serverBT.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		if (DropboxAuthRequest) {
			if (DropBoxSimple.mDBApi.getSession().authenticationSuccessful()) {
				initiateLecturerView();
				DropboxAuthRequest = false;
			}
		}
		if (StudLoginController.loginPressed) {
			initiateStudentView();
		}

	}

	/**
	 * Initiate lecturer view.
	 */
	private void initiateLecturerView() {
		// TODO Auto-generated method stub
		try {
			// Required to complete auth, sets the access token on the session
			DropBoxSimple.mDBApi.getSession().finishAuthentication();
			// folderRecursiveDelete(new File(path));
			new LectDownloadProgress(this);

		} catch (IllegalStateException e) {
			Log.i("DbAuthLog", "Error authenticating", e);
			Toast.makeText(this.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
		}
	}

	/**
	 * Initiate student view.
	 */
	private void initiateStudentView() {
		// TODO Auto-generated method stub
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter.isEnabled()) {
			IntentFilter actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			IntentFilter actionDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			IntentFilter actionUuid = new IntentFilter(BluetoothDevice.ACTION_UUID);
			registerReceiver(blueToothReceiver, actionFoundFilter); 
			registerReceiver(blueToothReceiver, actionUuid);
			registerReceiver(blueToothReceiver, actionDiscoveryFinishedFilter);
			StudLoginController.loginsuccedded = true;
			/*****///// *****//////******/////****////
			mBluetoothAdapter.startDiscovery();
		}
	}

	/**
	 * Show alert dialog and exit application.
	 *
	 * @param message
	 *            the message
	 */
	public void showAlertDialogAndExitApp(String message) {

		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
		alertDialog.setTitle("Alert");
		alertDialog.setMessage(message);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});

		alertDialog.show();
	}

	/**
	 * Hide keyboard.
	 */
	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		// Find the currently focused view, so we can grab the correct window
		// token from it.
		View view = getCurrentFocus();
		// If no view currently has focus, create a new one, just so we can grab
		// a window token from it
		if (view == null) {
			view = new View(this);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * Gets the user classification.
	 *
	 * @return the user classification
	 */
	public String getUserClassification() {
		return userClassification;
	}

	/**
	 * Sets the user classification.
	 *
	 * @param userClassification
	 *            the new user classification
	 */
	public void setUserClassification(String userClassification) {
		this.userClassification = userClassification;
	}

	/**
	 * Sets the BlueTooth receiver.
	 *
	 * @param blueToothReceiver
	 *            the new BlueTooth receiver
	 */
	public void setBlueToothReceiver(BroadcastReceiver blueToothReceiver) {
		this.blueToothReceiver = blueToothReceiver;
	}

	/**
	 * Gets the BlueTooth receiver.
	 *
	 * @return the BlueTooth receiver
	 */
	public BroadcastReceiver getBlueToothReceiver() {
		return blueToothReceiver;
	}

	/**
	 * Gets the filelist.
	 *
	 * @return the filelist
	 */
	public File getFilelist() {
		return filelist;
	}

	/**
	 * Sets the filelist.
	 *
	 * @param filelist
	 *            the new filelist
	 */
	public void setFilelist(File filelist) {
		this.filelist = filelist;
	}

	/**
	 * Sets the Dropbox auth request.
	 *
	 * @param dropboxAuthRequest
	 *            the new Dropbox auth request
	 */
	public void setDropboxAuthRequest(boolean dropboxAuthRequest) {
		DropboxAuthRequest = dropboxAuthRequest;
	}
}
