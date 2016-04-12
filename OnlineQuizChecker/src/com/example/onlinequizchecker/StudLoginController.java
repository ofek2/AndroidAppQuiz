package com.example.onlinequizchecker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by 311165906 on 10/03/2016.
 */
public class StudLoginController {
	private MainActivity mainActivity;
	public StudLoginController(MainActivity activity) {
		this.mainActivity = mainActivity;
		((Button)activity.findViewById(R.id.loginBtn)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String PINcode = (String) ((TextView)mainActivity.findViewById(R.id.pinCodeTxt))
						.getText();
				final ClientBT clientBT = new ClientBT(mainActivity);
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
				        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				            // Get the BluetoothDevice object from the Intent
				            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				            ParcelUuid [] parcelUuids=device.getUuids();
				            for (int i = 0; i < 7; i++) {
								if(!parcelUuids[i].getUuid().equals(clientBT.getUuids().get(i)))
									matchingUuids = false;
							}
				            if (matchingUuids) {
								clientBT.connect(device);
							}
				            // Add the name and address to an array adapter to show in a ListView
//				            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				        }
				    }
				};
				// Register the BroadcastReceiver
				IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
				mainActivity.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

			}
		});
	}
}
