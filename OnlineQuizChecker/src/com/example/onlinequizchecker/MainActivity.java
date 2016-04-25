package com.example.onlinequizchecker;

import java.io.File;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	 private String userClassification;
	 private boolean didDropboxAuth = false;
	 private BroadcastReceiver blueToothReceiver=null;
	 private File filelist;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
//	        setContentView(R.layout.lect_studentregistrationview);
	        new MainController(this);
	        userClassification = "";
	    }

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main,  menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        int id = item.getItemId();

	        //noinspection SimplifiableIfStatement
	        if (id == R.id.action_settings) {
	            return true;
	        }

	        return super.onOptionsItemSelected(item);
	    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (blueToothReceiver!=null)
		unregisterReceiver(blueToothReceiver);

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (userClassification.equals("Lecturer"))
		{
//			LectStudentRegListController.listview.setItemChecked(0,true);
			LectStudentRegListController.serverBT.start(LectStudentRegListController.listview);			
		}
	}
	protected void onResume() {
	        super.onResume();
	        if (userClassification.equals("Lecturer")&&!didDropboxAuth) {
	            if (DropBoxSimple.mDBApi.getSession().authenticationSuccessful()) {
	                try {
	                    // Required to complete auth, sets the access token on the session
	                    DropBoxSimple.mDBApi.getSession().finishAuthentication();
						didDropboxAuth = true;
	                    new LectStudentRegistrationController(this);
	                    String accessToken = DropBoxSimple.mDBApi.getSession().getOAuth2AccessToken();

	                } catch (IllegalStateException e) {
	                    Log.i("DbAuthLog", "Error authenticating", e);
	                    Toast.makeText(this.getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();//////////////////
	                }
	            }
			}
			if (StudLoginController.loginPressed)
				{
					BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					if (mBluetoothAdapter.isEnabled()) {
						IntentFilter actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
						IntentFilter actionDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
						IntentFilter actionUuid = new IntentFilter(BluetoothDevice.ACTION_UUID);
						registerReceiver(blueToothReceiver, actionFoundFilter); // Don't forget to unregister during onDestroy
						registerReceiver(blueToothReceiver, actionUuid);
						registerReceiver(blueToothReceiver, actionDiscoveryFinishedFilter);
						/*****/////*****//////******/////****////
						mBluetoothAdapter.startDiscovery();
					}
				}

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

		public File getFilelist() {
			return filelist;
		}

		public void setFilelist(File filelist) {
			this.filelist = filelist;
		}
}
