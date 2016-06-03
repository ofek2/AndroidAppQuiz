package com.example.onlinequizchecker;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MainActivity extends Activity {
	private String userClassification;
	private boolean didDropboxAuth = false;
	private BroadcastReceiver blueToothReceiver = null;
	private File filelist;
	private boolean DropboxAuthRequest;
	
	public static String zipFilesPassword="12345";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(R.layout.lect_studentregistrationview);
//		 if(new DeviceUtils().isDeviceRooted()){
//		        showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
//		    }
		new MainController(this);
		userClassification = "";
		DropboxAuthRequest = false;


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override 
	public void onBackPressed(){ 
		if(userClassification.equals(Constants.STUDENT))
			Toast.makeText(getApplicationContext(),"You Are Not Allowed to Exit the App", Toast.LENGTH_SHORT).show();
		
		
	}
	
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
//	 class HelloService extends Service {
//		private Looper mServiceLooper;
//		private ServiceHandler mServiceHandler;
//
//		// Handler that receives messages from the thread
//		private final class ServiceHandler extends Handler {
//			public ServiceHandler(Looper looper) {
//				super(looper);
//			}
//			@Override
//			public void handleMessage(Message msg) {
//				// Normally we would do some work here, like download a file.
//				// For our sample, we just sleep for 5 seconds.
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// Restore interrupt status.
//					Thread.currentThread().interrupt();
//				}
//				// Stop the service using the startId, so that we don't stop
//				// the service in the middle of handling another job
//				stopSelf(msg.arg1);
//			}
//		}
//
//		@Override
//		public void onCreate() {
//			// Start up the thread running the service.  Note that we create a
//			// separate thread because the service normally runs in the process's
//			// main thread, which we don't want to block.  We also make it
//			// background priority so CPU-intensive work will not disrupt our UI.
//
//			HandlerThread thread = new HandlerThread("ServiceStartArguments",
//					10);
//			thread.start();
//
//			// Get the HandlerThread's Looper and use it for our Handler
//			mServiceLooper = thread.getLooper();
//			mServiceHandler = new ServiceHandler(mServiceLooper);
//		}
//
//		@Override
//		public int onStartCommand(Intent intent, int flags, int startId) {
//			Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
//
//			// For each start request, send a message to start a job and deliver the
//			// start ID so we know which request we're stopping when we finish the job
//			Message msg = mServiceHandler.obtainMessage();
//			msg.arg1 = startId;
//			mServiceHandler.sendMessage(msg);
//
//			// If we get killed, after returning from here, restart
//			return START_STICKY;
//		}
//
//		@Override
//		public IBinder onBind(Intent intent) {
//			// We don't provide binding, so return null
//			return null;
//		}
//
//		@Override
//		public void onDestroy() {
//			Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
//			if (blueToothReceiver != null) {
//				unregisterReceiver(blueToothReceiver);
//				StudLoginController.loginsuccedded = false;
//			}
//
//			if(getUserClassification().equals(Constants.STUDENT))
//			{
//				if (!StudQuizActivity.submited&&ClientBT.quizWasInitiated)
//				{
//					CharSequence studentId = StudQuizActivity.studentId;
//					zipProtectedFile.createZipFileFromSpecificFiles(zipFilesPassword, studentId, ClientBT.quizPathToZip + "/" + studentId + ".zip", ClientBT.quizPathToZip);
//					//-------Check this!!!! ---------
//					Toast.makeText(getApplicationContext(), "Your quiz was successfully saved on your storage",
//							Toast.LENGTH_LONG).show();
//					//-------------------------------
//				}
//			}
//
//
//			BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
//			mAdapter.disable();
//		}
//	}
//	@Override
//	protected void onTas() {
//		super.onStop();
//		if(isFinishing())
//		{
//			if (blueToothReceiver != null) {
//				unregisterReceiver(blueToothReceiver);
//				StudLoginController.loginsuccedded = false;
//			}
//
//			if(getUserClassification().equals(Constants.STUDENT))
//			{
//				if (!StudQuizActivity.submited&&ClientBT.quizWasInitiated)
//				{
//					CharSequence studentId = StudQuizActivity.studentId;
//					zipProtectedFile.createZipFileFromSpecificFiles(zipFilesPassword, studentId, ClientBT.quizPathToZip + "/" + studentId + ".zip", ClientBT.quizPathToZip);
//					//-------Check this!!!! ---------
//					Toast.makeText(getApplicationContext(), "Your quiz was successfully saved on your storage",
//							Toast.LENGTH_LONG).show();
//					//-------------------------------
//				}
//			}
//
//
//			BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
//			mAdapter.disable();
//		}
//
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (userClassification.equals(Constants.LECTURER)) {
			// LectStudentRegListController.listview.setItemChecked(0,true);
			LectStudentRegListController.serverBT.start(LectStudentRegListController.listview);
		}
	}

	protected void onResume() {
		super.onResume();
		if(DropboxAuthRequest)
		{
			if (DropBoxSimple.mDBApi.getSession().authenticationSuccessful()) {
				initiateLecturerView();
				DropboxAuthRequest=false;
			}
		}
		if (StudLoginController.loginPressed) {
			initiateStudentView();
		}

	}

	private void initiateLecturerView() {
		// TODO Auto-generated method stub
		try {
			// Required to complete auth, sets the access token on the session
			DropBoxSimple.mDBApi.getSession().finishAuthentication();
			didDropboxAuth = true;
			String path = getApplicationContext().getFilesDir().getCanonicalPath() + "/"+Constants.APP_NAME;
			// folderRecursiveDelete(new File(path));
			new LectDownloadProgress(this);
			String accessToken = DropBoxSimple.mDBApi.getSession().getOAuth2AccessToken();

		} catch (IllegalStateException | IOException e) {
			Log.i("DbAuthLog", "Error authenticating", e);
			Toast.makeText(this.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
		}
	}

	private void initiateStudentView() {
		// TODO Auto-generated method stub
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter.isEnabled()) {
			IntentFilter actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			IntentFilter actionDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			IntentFilter actionUuid = new IntentFilter(BluetoothDevice.ACTION_UUID);
			registerReceiver(blueToothReceiver, actionFoundFilter); // Don't
																	// forget to
																	// unregister
																	// during
																	// onDestroy
			registerReceiver(blueToothReceiver, actionUuid);
			registerReceiver(blueToothReceiver, actionDiscoveryFinishedFilter);
			StudLoginController.loginsuccedded = true;
			/*****///// *****//////******/////****////
			mBluetoothAdapter.startDiscovery();
		}
	}
	
	public void showAlertDialogAndExitApp(String message) {

	    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
	    alertDialog.setTitle("Alert");
	    alertDialog.setMessage(message);
	    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
	            new DialogInterface.OnClickListener() {
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
	
	public void hideKeyboard() {
	    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    //Find the currently focused view, so we can grab the correct window token from it.
	    View view = getCurrentFocus();
	    //If no view currently has focus, create a new one, just so we can grab a window token from it
	    if (view == null) {
	        view = new View(this);
	    }
	    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public String getUserClassification() {
		return userClassification;
	}

	public void setUserClassification(String userClassification) {
		this.userClassification = userClassification;
	}

	public void setBlueToothReceiver(BroadcastReceiver blueToothReceiver) {
		this.blueToothReceiver = blueToothReceiver;
	}
	public BroadcastReceiver getBlueToothReceiver() {
		return blueToothReceiver;
	}
	public File getFilelist() {
		return filelist;
	}

	public void setFilelist(File filelist) {
		this.filelist = filelist;
	}
	public void setDidDbxAuth(boolean b)
	{
		didDropboxAuth = b;
	}
	public void setDropboxAuthRequest(boolean dropboxAuthRequest) {
		DropboxAuthRequest = dropboxAuthRequest;
	}
}
