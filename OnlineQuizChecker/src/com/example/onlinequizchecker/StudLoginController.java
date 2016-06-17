package com.example.onlinequizchecker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;

/**
 * The Class StudLoginController.
 * This class controls the login screen.
 */
public class StudLoginController {
	
	/** The main activity. */
	private MainActivity mainActivity;
	
	/** The login pressed. */
	public static boolean loginPressed = false;
	
	/** The Bluetooth adapter. */
	BluetoothAdapter mBluetoothAdapter;
	
	/** The Bluetooth device. */
	BluetoothDevice bluetoothDevice;
	
    
	/** The PIN code. */
	private CharSequence PINcode;
	
	/** The student id. */
	private CharSequence studentId;
	
	/** The loginsuccedded. */
	public static boolean loginsuccedded;
	
	/** The back button. */
	private Button backBtn;
	
	/**
	 * Instantiates a new stud login controller.
	 *
	 * @param activity the activity
	 */
	public StudLoginController(MainActivity activity) {
		mainActivity = activity;
		mainActivity.hideKeyboard();
		loginsuccedded = false;
		try {
			mainActivity.setContentView(R.layout.stud_loginview);
		     mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); 
			mainActivity.getApplicationContext().getFilesDir().getCanonicalPath();
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
					MainActivity.zipFilesPassword = password.toString(16);
					//
					mainActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mainActivity.setContentView(R.layout.stud_authorizationview);
						}
					});
					new StudAuthController(mainActivity, PINcode, studentId);
				}
				

			}
		});
	}
	
	/**
	 * {@link OnClickListener} for the back button.
	 *
	 * @see backBtnEvent
	 */
	class backBtnListener implements View.OnClickListener
	{

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			new MainController(mainActivity);
		}
		
	}

}
