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
 * Created by 311573943 on 15/05/2016.
 */
public class StudAuthController{
    private MainActivity activity;
    private int maxUuid=1;
    public static int maxDiscoveryIteration;
    //public static boolean loginPressed = false;
    BluetoothAdapter mBluetoothAdapter;

    BluetoothDevice bluetoothDevice;

    private char [] randomOrderedMacCharacters = {'0','A','6','7','C','D','8','9','E','4','1','5','F','3','2','B'};
    private char [] macCharacters = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    private int decimalPosInPinCode;
    public static ClientBT clientBT;
    public static CharSequence PINcode;
    public CharSequence studentId;
    public static boolean loginsuccedded;
    public static String applicationPath;
    private TextView label;
    private BroadcastReceiver mReceiver;
    public static ArrayList<BluetoothDevice> scanDevices;
    public static boolean lecturerFound;
    public static boolean currentlyCheckingDevice;
    public static boolean studentAuthorized;
    private StudQuizActivity studtentQuizActivity;
//    public static boolean recovered;
    public StudAuthController(MainActivity activity,CharSequence PINcode, CharSequence studentId){
        this.activity = activity;
        this.activity.hideKeyboard();
//        this.activity.setContentView(R.layout.stud_authorizationview);
        this.PINcode = PINcode;
        this.studentId = studentId;
        this.scanDevices = new ArrayList<BluetoothDevice>();
        StudQuizActivity.submited = false;
        lecturerFound = false;
        currentlyCheckingDevice = false;
//        maxDiscoveryIteration = 3;
        loginsuccedded = false;
//        recovered = false;
        if(StudAuthController.clientBT != null)
        {
        	StudAuthController.clientBT.stop();
        	StudAuthController.clientBT = null;
        }
//        clientBT=null;
        studentAuthorized = false;
        label = (TextView)this.activity.findViewById(R.id.waitingLbl);
        try {
            applicationPath = activity.getApplicationContext().getFilesDir().getCanonicalPath();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        startAuth();
    }
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
//                    Toast.makeText(activity.getApplicationContext(), device.getName(),
//                            Toast.LENGTH_SHORT).show();
                    String name = device.getName();
//                    if(deviceIsServer(device))
//                    {
//								mBluetoothAdapter.cancelDiscovery();
//								maxDiscoveryIteration=0;
                    StudLoginController.loginPressed = false;////////////////////////////////////////////////////////////////
//                        scanDevices.add(device);
                    if (!isFound(device)&&lecturerFound==false)
                        clientBT.connect(device);
//                        try {
//							clientBT.mConnectThread.join();
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//                    }
                }
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                    if(maxDiscoveryIteration == 1)
//                    {
//                    	maxDiscoveryIteration=0;
//                        mBluetoothAdapter.cancelDiscovery();
//                    	activity.unregisterReceiver(activity.getBlueToothReceiver());
//                    	loginsuccedded = false;
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
//                        Toast.makeText(activity.getApplicationContext(), "The PIN code is not correct",
//                                Toast.LENGTH_LONG).show();
                        Toast.makeText(activity.getApplicationContext(), "The lecturer was not found",
                                Toast.LENGTH_LONG).show();
                    }
//                    }
//                    else if(maxDiscoveryIteration>0) {
//                    	if (mBluetoothAdapter.isDiscovering()) {
//                    		mBluetoothAdapter.cancelDiscovery();
//						}              	
//                    	for (int i = 0; i < scanDevices.size(); i++) {
//                    		if (maxDiscoveryIteration!=0) {
//                    			clientBT.connect(scanDevices.get(i));
//                    			try {
//									clientBT.mConnectThread.join();
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}
//                    		else
//                    			break;
//						}
//                    	if(maxDiscoveryIteration!=0)
//                    	{
//                    		scanDevices = new ArrayList<BluetoothDevice>();
//                    		if (!mBluetoothAdapter.isDiscovering()) {
//                    			mBluetoothAdapter.startDiscovery();
//							}
//                    		maxDiscoveryIteration--;
//                    	}
//                    	else
//                    		mBluetoothAdapter.cancelDiscovery();
//								bluetoothDevice.fetchUuidsWithSdp();

//                    }
//                    else
//                    {
//                    	activity.unregisterReceiver(activity.getBlueToothReceiver());
//                    }
//
//                }
                }
            }


//            private boolean deviceIsServer(BluetoothDevice device)
//            {
//                char [] pinCode = new char[8];
//                String macAddress = device.getAddress();
//                int j = 0;
//                if(PINcode.length()==8) {
//                    for (int i = 0; i < macAddress.length(); i++) {
//
//                        int firstPos = findInArray(macAddress.charAt(i), randomOrderedMacCharacters);
//                        int secondPos = findInArray(macAddress.charAt(i + 1), randomOrderedMacCharacters);
//
//                        if (firstPos >= secondPos) {
//                            decimalPosInPinCode = firstPos - secondPos;
//                        } else
//                            decimalPosInPinCode = 16 - (secondPos - firstPos);
//                        pinCode[j] = macCharacters[decimalPosInPinCode];
//                        j++;
//                        i += 2;
//                    }
//                    pinCode[j] = macCharacters[findInArray(PINcode.charAt(7), randomOrderedMacCharacters)];
//                    pinCode[j + 1] = PINcode.charAt(7);
//                    for (int i = 0; i < pinCode.length; i++) {
//                        if (pinCode[i] != PINcode.charAt(i)) {
//                            return false;
//                        }
//                    }
//                    return true;
//                }
//                else
//                return false;
//            }
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
            clientBT = new ClientBT(studentId,mHandler,activity,
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
//        else
//            Toast.makeText(activity.getApplicationContext(), "Please fill all fields",
//                    Toast.LENGTH_LONG).show();
        // Create a BroadcastReceiver for ACTION_FOUND

        activity.setBlueToothReceiver(mReceiver);
//				/*****/////*****//////******/////****////
//				mBluetoothAdapter.startDiscovery();
//				/*****/////*****//////******/////****////
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (readMessage.equals("You have not registered to this course"))
                    {
//                    	maxDiscoveryIteration=0;
//                        mBluetoothAdapter.cancelDiscovery();
                    	activity.unregisterReceiver(mReceiver);
//                    	loginsuccedded = false;
                    	new StudLoginController(activity);
                        Toast.makeText(activity.getApplicationContext(), "You have not registered to this course",
                                Toast.LENGTH_LONG).show();
                    }
                    else if (readMessage.equals("This id is already connected"))
                    {
//                    	maxDiscoveryIteration=0;
//                        mBluetoothAdapter.cancelDiscovery();
                    	activity.unregisterReceiver(mReceiver);
//                    	loginsuccedded = false;
                    	new StudLoginController(activity);
                        Toast.makeText(activity.getApplicationContext(), "This id is already connected",
                                Toast.LENGTH_LONG).show();
                    }
                    else if (readMessage.equals("The PIN code is not correct"))
                    {
//                    	maxDiscoveryIteration=0;
//                        mBluetoothAdapter.cancelDiscovery();
                    	activity.unregisterReceiver(mReceiver);
//                    	loginsuccedded = false;
                    	new StudLoginController(activity);
                        Toast.makeText(activity.getApplicationContext(), "The PIN code is not correct",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case Constants.QUIZ_INITIATION:
                    String quizPath = (String) msg.obj;

                    int quizPeriod = msg.arg1;
                    activity.setUserClassification(Constants.STUDENT);
                    studtentQuizActivity = new StudQuizActivity(activity,quizPeriod,studentId,quizPath,applicationPath,clientBT);
                    break;
                case Constants.STUDENT_AUTHORIZED:
                    activity.unregisterReceiver(mReceiver);
                    label.setText("Waiting for quiz initiation.");
                    String course = (String)msg.obj;
                    if(new File(applicationPath+"/"+course).exists())
                    {
                    	File[] quizzes = new File(applicationPath+"/"+course+"/Quizzes").listFiles();
                    	String recoveryPath;
                    	String recoveryZipPath;
                    	String pathToSend;
                    	outerloop:
                    	for (int i = 0; i < quizzes.length; i++) {
                    		recoveryPath = applicationPath+"/"+course+"/Quizzes/"+ quizzes[i].getName() + "/StudentsAnswers"; 
                        	File[] answersFiles = new File(applicationPath+"/"+course+"/Quizzes/"+ quizzes[i].getName() + "/StudentsAnswers").
                        			listFiles();
                        	for (int j = 0; j < answersFiles.length; j++) {
                        		if(answersFiles[j].getName().equals(studentId+".zip"))
    							{
                        		recoveryZipPath = recoveryPath+"/"+studentId+".zip";
                        		pathToSend = "/"+course+"/Quizzes/"+ quizzes[i].getName() + "/StudentsAnswers/";
                                File[] files = new File(recoveryPath).listFiles();
                            	zipProtectedFile.unzipFile(activity.zipFilesPassword,
                            			recoveryZipPath, recoveryPath);
                            	new File(recoveryZipPath).delete();
                                files = new File(recoveryPath).listFiles();
                            	zipFileManager.createZipFile(new File(recoveryPath), recoveryZipPath);
//                            	recovered = true;
                            	byte [] byteArrayToSend = StudQuizActivity.zipToByteArray(recoveryPath+"/"+studentId+".zip",pathToSend);
                            	clientBT.mConnectedThread.write(byteArrayToSend);	
                            	break outerloop;
    							}
							}
    						
                    	}
//  //                 	recoveryPath = applicationPath+"/"+course+"/Quizzes/"+ quizzes[0].getName() + "/StudentsAnswers"; 
//                    	File[] answersFiles = new File(applicationPath+"/"+course+"/Quizzes/"+ quizzes[0].getName() + "/StudentsAnswers").listFiles();
//						if(answersFiles[0].getName().equals(studentId+".zip"))
//							{
//                    		recoveryZipPath = recoveryPath+"/"+studentId+".zip";
//                    		pathToSend = "/"+course+"/Quizzes/"+ quizzes[0].getName() + "/StudentsAnswers/";
//                        	zipProtectedFile.unzipFile(activity.zipFilesPassword,
//                        			recoveryZipPath, recoveryPath);
//                        	new File(recoveryZipPath).delete();
//                        	zipFileManager.createZipFile(new File(recoveryPath), recoveryZipPath);
//                        	recovered = true;
//                        	byte [] byteArrayToSend = StudQuizActivity.zipToByteArray(recoveryPath+"/"+studentId+".zip",pathToSend);
//                        	clientBT.mConnectedThread.write(byteArrayToSend);	
//							}////
//                    		recoveryZipPath = recoveryPath+"/"+studentId+".zip";
//                    		pathToSend = "/"+course+"/Quizzes/"+ quizzes[0].getName() + "/StudentsAnswers/";
//                        	zipProtectedFile.unzipFile(activity.zipFilesPassword,
//                        			recoveryZipPath, recoveryPath);
//                        	new File(recoveryZipPath).delete();
//                        	zipFileManager.createZipFile(new File(recoveryPath), recoveryZipPath);
//                        	recovered = true;
//                        	byte [] byteArrayToSend = StudQuizActivity.zipToByteArray(recoveryPath+"/"+studentId+".zip",pathToSend);
//                        	clientBT.mConnectedThread.write(byteArrayToSend);
//						}
                    }
//                    Toast.makeText(activity.getApplicationContext(), studentId,
//                            Toast.LENGTH_LONG).show();
//                	activity.setContentView(R.layout.stud_loginview);
                    break;
                case Constants.CONNECTION_LOST:
                    new StudLoginController(activity);
                    Toast.makeText(activity.getApplicationContext(), "The connection with the lecturer was lost",
                            Toast.LENGTH_LONG).show();
                    break;
                case Constants.STUDENT_SUBMITED:
                
//                	folderRecursiveDelete(new File(applicationPath+"/"));
                    Toast.makeText(activity.getApplicationContext(), "Your quiz was successfully sent to your lecturer",
                            Toast.LENGTH_LONG).show();
                
//                    clientBT.stop();
//                    BluetoothAdapter.getDefaultAdapter().disable();

                    new MainController(activity);
                    break;
                case Constants.ENABLE_QUIZ:
                	if(studtentQuizActivity!=null)
                	studtentQuizActivity.enableQuiz();
                	break;

                   
//                case Constants.UNREGISTER_RECEIVER:
//                	activity.unregisterReceiver(mReceiver);
//                    break;
//                case Constants.REGISTER_RECEIVER:
//                    IntentFilter actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//                    IntentFilter actionDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//                    IntentFilter actionUuid = new IntentFilter(BluetoothDevice.ACTION_UUID);
//                    activity.registerReceiver(mReceiver, actionFoundFilter); // Don't forget to unregister during onDestroy
//                    activity.registerReceiver(mReceiver, actionUuid);
//                    activity.registerReceiver(mReceiver, actionDiscoveryFinishedFilter);
//                    break;
            }
        }
    };
    private boolean isFound(BluetoothDevice bluetoothDevice)
    {
        for (int i=0;i<scanDevices.size();i++)
            if(scanDevices.get(i).getAddress().equals(bluetoothDevice.getAddress()))
                return true;
        return  false;
    }
    
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
}
