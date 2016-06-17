package com.example.onlinequizchecker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * The Class StudAuthController.
 */
public class StudAuthController{
    
    /** The activity. */
    private MainActivity activity;
    
    /** The max discovery iteration. */
    public static int maxDiscoveryIteration;
    
    /** The m bluetooth adapter. */
    BluetoothAdapter mBluetoothAdapter;
    
    /** The bluetooth device. */
    BluetoothDevice bluetoothDevice;
    
    /** The client bt. */
    public static ClientBT clientBT;
    
    /** The PI ncode. */
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
    
    /** The studtent quiz activity. */
    private StudQuizActivity studtentQuizActivity;
    
    /**
     * Instantiates a new stud auth controller.
     *
     * @param activity the activity
     * @param PINcode the PI ncode
     * @param studentId the student id
     */
    public StudAuthController(MainActivity activity,CharSequence PINcode, CharSequence studentId){
        this.activity = activity;
        this.activity.hideKeyboard();
        this.PINcode = PINcode;
        this.studentId = studentId;
        this.scanDevices = new ArrayList<BluetoothDevice>();
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
     * Start auth.
     */
    public void startAuth()
    {
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                boolean matchingUuids = true;

                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    bluetoothDevice = device;
                    String name = device.getName();
                    StudLoginController.loginPressed = false;////////////////////////////////////////////////////////////////
//                        scanDevices.add(device);
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
            private int findInArray(char ch,char [] array)
            {
                for (int i = 0; i < array.length; i++) {
                    if(array[i]==ch)
                        return i;
                }
                return -1;

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
                    /*****/////*****//////******/////****////

                }
            }
            mBluetoothAdapter.startDiscovery();
        }

        activity.setBlueToothReceiver(mReceiver);
//				/*****/////*****//////******/////****////
//				mBluetoothAdapter.startDiscovery();
//				/*****/////*****//////******/////****////
    }
//    
//    /** The m handler. */
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case Constants.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    if (readMessage.equals("You have not registered to this course"))
//                    {
//                    	activity.unregisterReceiver(mReceiver);
////                    	loginsuccedded = false;
//                    	new StudLoginController(activity);
//                        Toast.makeText(activity.getApplicationContext(), "You have not registered to this course",
//                                Toast.LENGTH_LONG).show();
//                    }
//                    else if (readMessage.equals("This id is already connected"))
//                    {
////                    	maxDiscoveryIteration=0;
////                        mBluetoothAdapter.cancelDiscovery();
//                    	activity.unregisterReceiver(mReceiver);
////                    	loginsuccedded = false;
//                    	new StudLoginController(activity);
//                        Toast.makeText(activity.getApplicationContext(), "This id is already connected",
//                                Toast.LENGTH_LONG).show();
//                    }
//                    else if (readMessage.equals("The PIN code is not correct"))
//                    {
////                    	maxDiscoveryIteration=0;
////                        mBluetoothAdapter.cancelDiscovery();
//                    	activity.unregisterReceiver(mReceiver);
////                    	loginsuccedded = false;
//                    	new StudLoginController(activity);
//                        Toast.makeText(activity.getApplicationContext(), "The PIN code is not correct",
//                                Toast.LENGTH_LONG).show();
//                    }
//                    break;
//                case Constants.QUIZ_INITIATION:
//                    String quizPath = (String) msg.obj;
//
//                    int quizPeriod = msg.arg1;
//                    activity.setUserClassification(Constants.STUDENT);
//                    studtentQuizActivity = new StudQuizActivity(activity,quizPeriod,studentId,quizPath,applicationPath,clientBT);
//                    break;
//                case Constants.STUDENT_AUTHORIZED:
//                    activity.unregisterReceiver(mReceiver);
//                    label.setText("Waiting for quiz initiation.");
//                    String course = (String)msg.obj;
//                    if(new File(applicationPath+"/"+course).exists())
//                    {
//                    	File[] quizzes = new File(applicationPath+"/"+course+"/Quizzes").listFiles();
//                    	String recoveryPath;
//                    	String recoveryZipPath;
//                    	String pathToSend;
//                    	outerloop:
//                    	for (int i = 0; i < quizzes.length; i++) {
//                    		recoveryPath = applicationPath+"/"+course+"/Quizzes/"+ quizzes[i].getName() + "/StudentsAnswers"; 
//                        	File[] answersFiles = new File(applicationPath+"/"+course+"/Quizzes/"+ quizzes[i].getName() + "/StudentsAnswers").
//                        			listFiles();
//                        	for (int j = 0; j < answersFiles.length; j++) {
//                        		if(answersFiles[j].getName().equals(studentId+".zip"))
//    							{
//                        		recoveryZipPath = recoveryPath+"/"+studentId+".zip";
//                        		pathToSend = "/"+course+"/Quizzes/"+ quizzes[i].getName() + "/StudentsAnswers/";
//                                File[] files = new File(recoveryPath).listFiles();
//                            	zipProtectedFile.unzipFile(activity.zipFilesPassword,
//                            			recoveryZipPath, recoveryPath);
//                            	new File(recoveryZipPath).delete();
//                                files = new File(recoveryPath).listFiles();
//                            	zipFileManager.createZipFile(new File(recoveryPath), recoveryZipPath);
////                            	recovered = true;
//                            	byte [] byteArrayToSend = StudQuizActivity.zipToByteArray(recoveryPath+"/"+studentId+".zip",pathToSend);
//                            	clientBT.mConnectedThread.write(byteArrayToSend);	
//                            	break outerloop;
//    							}
//							}
//    						
//                    	}
////  //                 	recoveryPath = applicationPath+"/"+course+"/Quizzes/"+ quizzes[0].getName() + "/StudentsAnswers"; 
////                    	File[] answersFiles = new File(applicationPath+"/"+course+"/Quizzes/"+ quizzes[0].getName() + "/StudentsAnswers").listFiles();
////						if(answersFiles[0].getName().equals(studentId+".zip"))
////							{
////                    		recoveryZipPath = recoveryPath+"/"+studentId+".zip";
////                    		pathToSend = "/"+course+"/Quizzes/"+ quizzes[0].getName() + "/StudentsAnswers/";
////                        	zipProtectedFile.unzipFile(activity.zipFilesPassword,
////                        			recoveryZipPath, recoveryPath);
////                        	new File(recoveryZipPath).delete();
////                        	zipFileManager.createZipFile(new File(recoveryPath), recoveryZipPath);
////                        	recovered = true;
////                        	byte [] byteArrayToSend = StudQuizActivity.zipToByteArray(recoveryPath+"/"+studentId+".zip",pathToSend);
////                        	clientBT.mConnectedThread.write(byteArrayToSend);	
////							}////
////                    		recoveryZipPath = recoveryPath+"/"+studentId+".zip";
////                    		pathToSend = "/"+course+"/Quizzes/"+ quizzes[0].getName() + "/StudentsAnswers/";
////                        	zipProtectedFile.unzipFile(activity.zipFilesPassword,
////                        			recoveryZipPath, recoveryPath);
////                        	new File(recoveryZipPath).delete();
////                        	zipFileManager.createZipFile(new File(recoveryPath), recoveryZipPath);
////                        	recovered = true;
////                        	byte [] byteArrayToSend = StudQuizActivity.zipToByteArray(recoveryPath+"/"+studentId+".zip",pathToSend);
////                        	clientBT.mConnectedThread.write(byteArrayToSend);
////						}
//                    }
////                    Toast.makeText(activity.getApplicationContext(), studentId,
////                            Toast.LENGTH_LONG).show();
////                	activity.setContentView(R.layout.stud_loginview);
//                    break;
//                case Constants.CONNECTION_LOST:
//                    new StudLoginController(activity);
//                    Toast.makeText(activity.getApplicationContext(), "The connection with the lecturer was lost",
//                            Toast.LENGTH_LONG).show();
//                    break;
//                case Constants.STUDENT_SUBMITED:
//                
////                	folderRecursiveDelete(new File(applicationPath+"/"));
//                    Toast.makeText(activity.getApplicationContext(), "Your quiz was successfully sent to your lecturer",
//                            Toast.LENGTH_LONG).show();
//                
////                    clientBT.stop();
////                    BluetoothAdapter.getDefaultAdapter().disable();
//
//                    new MainController(activity);
//                    break;
//                case Constants.ENABLE_QUIZ:
//                	if(studtentQuizActivity!=null)
//                	studtentQuizActivity.enableQuiz();
//                	break;
//
//                   
////                case Constants.UNREGISTER_RECEIVER:
////                	activity.unregisterReceiver(mReceiver);
////                    break;
////                case Constants.REGISTER_RECEIVER:
////                    IntentFilter actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
////                    IntentFilter actionDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
////                    IntentFilter actionUuid = new IntentFilter(BluetoothDevice.ACTION_UUID);
////                    activity.registerReceiver(mReceiver, actionFoundFilter); // Don't forget to unregister during onDestroy
////                    activity.registerReceiver(mReceiver, actionUuid);
////                    activity.registerReceiver(mReceiver, actionDiscoveryFinishedFilter);
////                    break;
//            }
//        }
//    };
    
    /**
     * Checks if is found.
     *
     * @param bluetoothDevice the bluetooth device
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
	 * Gets the studtent quiz activity.
	 *
	 * @return the studtent quiz activity
	 */
	public StudQuizActivity getStudtentQuizActivity() {
		return studtentQuizActivity;
	}
	
	/**
	 * Sets the studtent quiz activity.
	 *
	 * @param studtentQuizActivity the new studtent quiz activity
	 */
	public void setStudtentQuizActivity(StudQuizActivity studtentQuizActivity) {
		this.studtentQuizActivity = studtentQuizActivity;
	}
    
}
