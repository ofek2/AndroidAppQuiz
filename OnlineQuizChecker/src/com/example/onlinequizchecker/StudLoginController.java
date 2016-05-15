package com.example.onlinequizchecker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 311165906 on 10/03/2016.
 */
public class StudLoginController {
	private MainActivity mainActivity;
	public static boolean loginPressed = false;
	BluetoothAdapter mBluetoothAdapter;
	
	BluetoothDevice bluetoothDevice;
	
    
	private CharSequence PINcode;
	private CharSequence studentId;
	public static boolean loginsuccedded = false;
	private String applicationPath;
	public StudLoginController(MainActivity activity) {
		this.mainActivity = activity;
		try {
			activity.setContentView(R.layout.stud_loginview);
			applicationPath = mainActivity.getApplicationContext().getFilesDir().getCanonicalPath();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		((Button)activity.findViewById(R.id.loginBtn)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				loginPressed=true;
				// TODO Auto-generated method stub
				PINcode = ((TextView) mainActivity.findViewById(R.id.pinCodeTxt))
						.getText();
				studentId = ((TextView) mainActivity.findViewById(R.id.studentIdTxt))
						.getText();
				new StudAuthController(mainActivity,PINcode,studentId);
			}
		});
	}

}
