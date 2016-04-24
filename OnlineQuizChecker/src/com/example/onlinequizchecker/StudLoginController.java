package com.example.onlinequizchecker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private char [] pinCode = new char[8];
    private int decimalPosInPinCode;
	CharSequence PINcode;
	public StudLoginController(MainActivity activity) {
		this.mainActivity = activity;
		((Button)activity.findViewById(R.id.loginBtn)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				loginPressed=true;
				// TODO Auto-generated method stub
				PINcode = ((TextView) mainActivity.findViewById(R.id.pinCodeTxt))
						.getText();
				final ClientBT clientBT = new ClientBT(mainActivity,PINcode);
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (mBluetoothAdapter == null) {
				    // Device does not support Bluetooth
				}
				if (!mBluetoothAdapter.isEnabled()) {
				    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				    mainActivity.startActivityForResult(enableBtIntent, 1);
				}

				// Create a BroadcastReceiver for ACTION_FOUND
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
								bluetoothDevice.fetchUuidsWithSdp();
								
							}
							else
								mBluetoothAdapter.cancelDiscovery();
						}
				    }

					private boolean deviceIsServer(BluetoothDevice device) 
					{
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
						
						return false;
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
}
