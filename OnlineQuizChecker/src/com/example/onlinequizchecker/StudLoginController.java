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
	private int maxUuid=1;
	private int maxDiscoveryIteration = 10;
	public static boolean loginPressed = false;
	BluetoothAdapter mBluetoothAdapter;
	
	BluetoothDevice bluetoothDevice;
	
    private char [] randomOrderedMacCharacters = {'0','A','6','7','C','D','8','9','E','4','1','5','F','3','2','B'};
    private char [] macCharacters = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    
    private int decimalPosInPinCode;
    private ClientBT clientBT=null;
	private CharSequence PINcode;
	private CharSequence studentId;
	public static boolean loginsuccedded = false;
	private String applicationPath;
	public StudLoginController(MainActivity activity) {
		this.mainActivity = activity;
		try {
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
				
				final BroadcastReceiver mReceiver = new BroadcastReceiver() {
				    public void onReceive(Context context, Intent intent) {

				        String action = intent.getAction();
				        boolean matchingUuids = true;
				        // When discovery finds a device
//				        if (BluetoothDevice.ACTION_UUID.equals(action)) {
//				        	BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//				        	Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
//				        	Bundle b = intent.getExtras();
//						}
				        if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				            // Get the BluetoothDevice object from the Intent
				            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				            bluetoothDevice = device;
//				            device.fetchUuidsWithSdp();
//							ParcelUuid parcelUuids;
//							parcelUuids	= ((ParcelUuid)intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID));
//							String s = intent.getParcelableExtra(BluetoothDevice.EXTRA_PAIRING_KEY);
//				            Bundle b = intent.getExtras();
				            		//(BluetoothDevice.EXTRA_RSSI);
//				            String s = intent.getParcelableExtra(BluetoothDevice.EXTRA_RSSI);
							Toast.makeText(mainActivity.getApplicationContext(), device.getName(),
									Toast.LENGTH_SHORT).show();
							String name = device.getName();
							if(deviceIsServer(device))
							{
								mBluetoothAdapter.cancelDiscovery();
								maxDiscoveryIteration=0;
								loginPressed=false;
								clientBT.connect(device);
							}
								
//							if(device.getUuids()!=null) {
//							if(parcelUuids!=null) {
//								ParcelUuid[] parcelUuids = device.getUuids();
//								Toast.makeText(activity.getApplicationContext(), parcelUuids[0].getUuid().toString(),
//										Toast.LENGTH_LONG).show();
//							}
//				            for (int i = 0; i < maxUuid; i++) {
//								if(!parcelUuids[i].getUuid().equals(clientBT.getUuids().get(i)))
//									matchingUuids = false;
//							}
//				            if (matchingUuids) {
//								mBluetoothAdapter.cancelDiscovery();
//								clientBT.connect(device);
//							}
				            // Add the name and address to an array adapter to show in a ListView
//				            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				        }
						if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
						{
							if(maxDiscoveryIteration>0) {
								mBluetoothAdapter.startDiscovery();
								maxDiscoveryIteration--;
//								bluetoothDevice.fetchUuidsWithSdp();
								Toast.makeText(mainActivity.getApplicationContext(), "The PIN code is not correct",
								Toast.LENGTH_LONG).show();
								
							}
							else
								mBluetoothAdapter.cancelDiscovery();
						}
				    }

				    
				    
					private boolean deviceIsServer(BluetoothDevice device) 
					{
						char [] pinCode = new char[8];
						String macAddress = device.getAddress();
						int j = 0;
						for (int i = 0; i < macAddress.length(); i++) {
							
							int firstPos = findInArray(macAddress.charAt(i), randomOrderedMacCharacters);
							int secondPos = findInArray(macAddress.charAt(i + 1), randomOrderedMacCharacters);

							if (firstPos >= secondPos) {
								decimalPosInPinCode = firstPos - secondPos;
							} else
								decimalPosInPinCode = 16 - (secondPos - firstPos);
							pinCode[j] = macCharacters[decimalPosInPinCode];
							j++;
							i += 2;
						}
						pinCode[j] = macCharacters[findInArray(PINcode.charAt(7), randomOrderedMacCharacters)];
						pinCode[j+1] = PINcode.charAt(7);
						for (int i = 0; i < pinCode.length; i++) {
							if (pinCode[i]!=PINcode.charAt(i)) {
								return false;
							}
						}
						return true;
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
				clientBT = new ClientBT(studentId,mHandler,mainActivity,
						applicationPath);
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (mBluetoothAdapter == null) {
				    // Device does not support Bluetooth
				}
				if(loginsuccedded==false)
				{
				if (!mBluetoothAdapter.isEnabled()) {
				    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				    mainActivity.startActivityForResult(enableBtIntent, 1);
				}
				else
				{
					IntentFilter actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
					IntentFilter actionDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
					IntentFilter actionUuid = new IntentFilter(BluetoothDevice.ACTION_UUID);
					mainActivity.registerReceiver(mReceiver, actionFoundFilter); // Don't forget to unregister during onDestroy
					mainActivity.registerReceiver(mReceiver, actionUuid);
					mainActivity.registerReceiver(mReceiver, actionDiscoveryFinishedFilter);
					loginsuccedded = true;
					/*****/////*****//////******/////****////
					
				}
				}
				mBluetoothAdapter.startDiscovery();
				}
				else
					Toast.makeText(mainActivity.getApplicationContext(), "Please fill all fields",
					Toast.LENGTH_LONG).show();
				// Create a BroadcastReceiver for ACTION_FOUND

//				// Register the BroadcastReceiver
//				IntentFilter actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//				IntentFilter actionDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//				mainActivity.registerReceiver(mReceiver, actionFoundFilter); // Don't forget to unregister during onDestroy
//				mainActivity.registerReceiver(mReceiver, actionDiscoveryFinishedFilter);
				mainActivity.setBlueToothReceiver(mReceiver);
//				/*****/////*****//////******/////****////
//				mBluetoothAdapter.startDiscovery();
//				/*****/////*****//////******/////****////

			}
		});
	}
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            FragmentActivity activity = getActivity();
            switch (msg.what) {
//                case Constants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case BluetoothChatService.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            mConversationArrayAdapter.clear();
//                            break;
//                        case BluetoothChatService.STATE_CONNECTING:
//                            setStatus(R.string.title_connecting);
//                            break;
//                        case BluetoothChatService.STATE_LISTEN:
//                        case BluetoothChatService.STATE_NONE:
//                            setStatus(R.string.title_not_connected);
//                            break;
//                    }
//                    break;
//                case Constants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
//                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (readMessage.equals("You have not registered to this course"))
						Toast.makeText(mainActivity.getApplicationContext(), "You have not registered to this course",
						Toast.LENGTH_LONG).show();
                    else if (readMessage.equals("This id is already connected"))
						Toast.makeText(mainActivity.getApplicationContext(), "This id is already connected",
						Toast.LENGTH_LONG).show();
//                    int pos = Integer.parseInt((Character.toString((char) readBuf[0])));
//					receivePos(studentPosInList(readMessage));
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.QUIZ_INITIATION:
                    String quizPath = (String) msg.obj;
//                    int quizNamelength = msg.arg2;
                    int quizPeriod = msg.arg1;
                    mainActivity.setUserClassification("Student");
                    new StudQuizActivity(mainActivity,quizPeriod,studentId,quizPath,applicationPath,clientBT);
                    break;
//                case Constants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    if (null != activity) {
//                        Toast.makeText(activity, "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                case Constants.MESSAGE_TOAST:
//                    if (null != activity) {
//                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                    break;
            }
        }
    };
}
