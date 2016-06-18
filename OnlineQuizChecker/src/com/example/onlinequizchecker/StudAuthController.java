package com.example.onlinequizchecker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * The Class StudAuthController.
 * This class controls the students authorization process
 */
public class StudAuthController{
    
    /** The activity. */
    private MainActivity activity;
    
    /** The max discovery iteration. */
    public static int maxDiscoveryIteration;
    
    /** The Bluetooth adapter. */
    BluetoothAdapter mBluetoothAdapter;
    
    /** The Bluetooth device. */
    BluetoothDevice bluetoothDevice;
    
    /** The Bluetooth client. */
    public static ClientBT clientBT;
    
    /** The PIN code. */
    public static CharSequence PINcode;
    
    /** The student id. */
    public CharSequence studentId;
    
    /** The loginsuccedded. */
    public static boolean loginsuccedded;
    
    /** The application path. */
    public static String applicationPath;
    
    /** The label. */
    private TextView label;
    
    /** The m receiver. */
    private BroadcastReceiver mReceiver;
    
    /** The scan devices. */
    public static ArrayList<BluetoothDevice> scanDevices;
    
    /** The lecturer found. */
    public static boolean lecturerFound;
    
    /** The currently checking device. */
    public static boolean currentlyCheckingDevice;
    
    /** The student authorized. */
    public static boolean studentAuthorized;
    
    /** The student quiz activity. */
    private StudQuizActivity studentQuizActivity;
    
    /**
     * Instantiates a new stud auth controller.
     *
     * @param activity the activity
     * @param PINcode the PIN code
     * @param studentId the student id
     */
    public StudAuthController(MainActivity activity,CharSequence PINcode, CharSequence studentId){
        this.activity = activity;
        this.activity.hideKeyboard();
        StudAuthController.PINcode = PINcode;
        this.studentId = studentId;
        StudAuthController.scanDevices = new ArrayList<BluetoothDevice>();
        StudQuizActivity.submited = false;
        lecturerFound = false;
        currentlyCheckingDevice = false;
        loginsuccedded = false;
        if(StudAuthController.clientBT != null)
        {
        	StudAuthController.clientBT.stop();
        	StudAuthController.clientBT = null;
        }
        studentAuthorized = false;
        label = (TextView)this.activity.findViewById(R.id.waitingLbl);
        try {
            applicationPath = activity.getApplicationContext().getFilesDir().getCanonicalPath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        startAuth();
    }
    
    /**
     * Start authorization process.
     */
    public void startAuth()
    {
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    bluetoothDevice = device;
                    StudLoginController.loginPressed = false;
                    if (!isFound(device)&&lecturerFound==false)
                        clientBT.connect(device);
                }
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    if(currentlyCheckingDevice&&!lecturerFound) {
                        mBluetoothAdapter.startDiscovery();
                        currentlyCheckingDevice = false;
                    }
                   else if(!lecturerFound) {
                        if (mBluetoothAdapter.isDiscovering()) {
                            mBluetoothAdapter.cancelDiscovery();
                        }
                        activity.unregisterReceiver(activity.getBlueToothReceiver());
                        new StudLoginController(activity);
                        Toast.makeText(activity.getApplicationContext(), "The lecturer was not found",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        if(studentId.length()>0&&PINcode.length()>0)
        {
            clientBT = new ClientBT(studentId,new StudMessageHandler(activity, this),activity,
                    applicationPath);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            }
            if(loginsuccedded==false)
            {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    activity.startActivityForResult(enableBtIntent, 1);
                }
                else
                {
                    IntentFilter actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    IntentFilter actionDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    IntentFilter actionUuid = new IntentFilter(BluetoothDevice.ACTION_UUID);
                    activity.registerReceiver(mReceiver, actionFoundFilter); // Don't forget to unregister during onDestroy
                    activity.registerReceiver(mReceiver, actionUuid);
                    activity.registerReceiver(mReceiver, actionDiscoveryFinishedFilter);
                    loginsuccedded = true;
                }
            }
            mBluetoothAdapter.startDiscovery();
        }

        activity.setBlueToothReceiver(mReceiver);
    }

    
    /**
     * Checks if a device was found already.
     *
     * @param bluetoothDevice the Bluetooth device
     * @return true, if is found
     */
    private boolean isFound(BluetoothDevice bluetoothDevice)
    {
        for (int i=0;i<scanDevices.size();i++)
            if(scanDevices.get(i).getAddress().equals(bluetoothDevice.getAddress()))
                return true;
        return  false;
    }
    
    /**
     * Folder recursive delete.
     *
     * @param file the file
     */
    public static void folderRecursiveDelete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                folderRecursiveDelete(f);
            }
        }
       try{
           if(!file.getCanonicalPath().equals(applicationPath))
               file.delete();
       }
       catch (IOException e){
           ;
       }

    }
	
	/**
	 * Gets the student id.
	 *
	 * @return the student id
	 */
	public CharSequence getStudentId() {
		return studentId;
	}
	
	/**
	 * Sets the student id.
	 *
	 * @param studentId the new student id
	 */
	public void setStudentId(CharSequence studentId) {
		this.studentId = studentId;
	}
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public TextView getLabel() {
		return label;
	}
	
	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(TextView label) {
		this.label = label;
	}
	
	/**
	 * Gets the m receiver.
	 *
	 * @return the m receiver
	 */
	public BroadcastReceiver getmReceiver() {
		return mReceiver;
	}
	
	/**
	 * Sets the m receiver.
	 *
	 * @param mReceiver the new m receiver
	 */
	public void setmReceiver(BroadcastReceiver mReceiver) {
		this.mReceiver = mReceiver;
	}
	
	/**
	 * Gets the student quiz activity.
	 *
	 * @return the student quiz activity
	 */
	public StudQuizActivity getStudtentQuizActivity() {
		return studentQuizActivity;
	}
	
	/**
	 * Sets the student quiz activity.
	 *
	 * @param studtentQuizActivity the new student quiz activity
	 */
	public void setStudtentQuizActivity(StudQuizActivity studtentQuizActivity) {
		this.studentQuizActivity = studtentQuizActivity;
	}
    
}
