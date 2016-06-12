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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
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
	public static boolean loginsuccedded;
	private String applicationPath;
	private Button backBtn;
	public StudLoginController(MainActivity activity) {
		mainActivity = activity;
		mainActivity.hideKeyboard();
		loginsuccedded = false;
		try {
			mainActivity.setContentView(R.layout.stud_loginview);
		     mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); 
			applicationPath = mainActivity.getApplicationContext().getFilesDir().getCanonicalPath();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		backBtn =(Button) mainActivity.findViewById(R.id.backBtnStudLogin);
		backBtn.setOnClickListener(new backBtnListener());
		((Button)mainActivity.findViewById(R.id.loginBtn)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				loginPressed=true;
				// TODO Auto-generated method stub
				PINcode = ((TextView) mainActivity.findViewById(R.id.pinCodeTxt))
						.getText();
				studentId = ((TextView) mainActivity.findViewById(R.id.studentIdTxt))
						.getText();

				if(PINcode.length()== 0||studentId.length()==0)
				{
					Toast.makeText(mainActivity.getApplicationContext(), "Please fill all the fields",
							Toast.LENGTH_SHORT).show();
				}
				else {
					//zip password encryption
					CharSequence key = studentId;
					BigInteger password;
					BigInteger key_2 = new BigInteger(key.toString());
					BigInteger key_3 = new BigInteger(key.toString());
					key_2 = key_2.pow(2);
					key_3 = key_3.pow(3);
					password = key_2.shiftLeft(key_3.bitLength());
					password = password.add(key_3);					
					mainActivity.zipFilesPassword = password.toString(16);
					//
					mainActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mainActivity.setContentView(R.layout.stud_authorizationview);
						}
					});
					new StudAuthController(mainActivity, PINcode, studentId);
				}
				//.start();

			}
		});
	}
	
	class backBtnListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			new MainController(mainActivity);
		}
		
	}

}
