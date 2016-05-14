package com.example.onlinequizchecker;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.dropbox.client2.SdkVersion;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ServerBT {

	// Debugging
	private static final String TAG = "BluetoothChatService";
	private static final boolean D = true;

	// Name for the SDP record when creating server socket
	private static final String NAME = "BluetoothChatMulti";

	// Member fields
	private final BluetoothAdapter mAdapter;
	 private final Handler mHandler;
	private ArrayList<AcceptThread> mAcceptThreads;
	// private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;
	private int stage = 1;
	private ArrayList<String> mDeviceAddresses;
	public static ArrayList<ConnectedThread> mConnThreads;
	private ArrayList<BluetoothSocket> mSockets;
	private int maxUuid = 1;
	/**
	 * A bluetooth piconet can support up to 7 connections. This array holds 7
	 * unique UUIDs. When attempting to make a connection, the UUID on the
	 * client must match one that the server is listening for. When accepting
	 * incoming connections server listens for all 7 UUIDs. When trying to form
	 * an outgoing connection, the client tries each UUID one at a time.
	 */
	private ArrayList<UUID> mUuids;

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
												// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote
													// device

	private char[] randomOrderedMacCharacters = { '0', 'A', '6', '7', 'C', 'D', '8', '9', 'E', '4', '1', '5', 'F', '3',
			'2', 'B' };
	private char[] macCharacters = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private char[] pinCode = new char[8];
	// private String hexaPosInPinCode;
	private int decimalPosInPinCode;
	private MainActivity activity;
	private ListView listView;
	private int lastPosInConnectedThreadList=0;
	private String studentsAnswersPath;
	private String applicationPath;
	/**
	 * Constructor. Prepares a new BluetoothChat session. // * @param context
	 * The UI Activity Context // * @param handler A Handler to send messages
	 * back to the UI Activity
	 * @param mHandler 
	 */
	@SuppressLint("NewApi")
	public ServerBT(MainActivity activity, Handler mHandler) {
		// , Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();

		this.activity = activity;
		try {
			applicationPath = activity.getFilelist().getCanonicalPath()+"/";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Toast.makeText(activity.getApplicationContext(), "shit",
		// Toast.LENGTH_SHORT).show();
		initializePINCode();

		if (mAdapter == null) {
			// Device does not support Bluetooth
			/////////////////////

			//////////////
		}
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

		// discoverableIntent.putExtra(BluetoothDevice.EXTRA_UUID,"b7746a40-c758-4868-aa19-7ac6b3475dfc");
		// discoverableIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_KEY,14242314);
		// discoverableIntent.putExtra(BluetoothAdapter.EXTRA_STATE,"634697");
		// discoverableIntent.putExtra(BluetoothDevice.EXTRA_NAME,"15");
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		activity.startActivityForResult(discoverableIntent,1);

		mState = STATE_NONE;
		 this.mHandler = mHandler;
		mDeviceAddresses = new ArrayList<String>();
		mConnThreads = new ArrayList<ConnectedThread>(maxUuid);
		mAcceptThreads = new ArrayList<AcceptThread>(maxUuid);
		for (int i = 0; i < maxUuid; i++) {
			mAcceptThreads.add(null);
		}
		mSockets = new ArrayList<BluetoothSocket>();
		mUuids = new ArrayList<UUID>();
		// 7 randomly-generated UUIDs. These must match on both server and
		// client.
		mUuids.add(UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));
		// mUuids.add(UUID.fromString("2d64189d-5a2c-4511-a074-77f199fd0834"));
		// mUuids.add(UUID.fromString("e442e09a-51f3-4a7b-91cb-f638491d1412"));
		// mUuids.add(UUID.fromString("a81d6504-4536-49ee-a475-7d96d09439e4"));
		// mUuids.add(UUID.fromString("aa91eab1-d8ad-448e-abdb-95ebba4a9b55"));
		// mUuids.add(UUID.fromString("4d34da73-d0a4-4f40-ac38-917e0a9dee97"));
		// mUuids.add(UUID.fromString("5e14d4df-9c8a-4db7-81e4-c937564c86e0"));
	}

	private void initializePINCode() {
//		String macAddress = mAdapter.getAddress();
		String macAddress = android.provider.Settings.Secure.getString(activity.getApplicationContext().getContentResolver(), "bluetooth_address");
		
		Toast.makeText(activity.getApplicationContext(), macAddress, Toast.LENGTH_SHORT).show();
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
		Random random = new Random();
		int randomNumber = random.nextInt(16);
		pinCode[j] = macCharacters[randomNumber];
		pinCode[j + 1] = randomOrderedMacCharacters[randomNumber];
		String stringPinCode = String.valueOf(pinCode);
		((TextView) activity.findViewById(R.id.PINCodeTxt)).setText(stringPinCode);
	}

	private int findInArray(char ch, char[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == ch)
				return i;
		}
		return -1;

	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		// mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state,
		// -1).sendToTarget();
	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 * 
	 * @param listview
	 */
	public synchronized void start(ListView listview) {
		this.listView = listview;
		// if (D) Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		// if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread
		// = null;}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to listen on a BluetoothServerSocket
		for (int i = 0; i < maxUuid; i++) {
			if (mAcceptThreads.get(i) == null) {
				mAcceptThreads.remove(i);
				mAcceptThreads.add(i, new AcceptThread(i));
				mAcceptThreads.get(i).start();
			}
		}

		setState(STATE_LISTEN);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
//	 * @param uuidPos
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
//			, int uuidPos) {
		if (D)
			Log.d(TAG, "connected");

		// Commented out all the cancellations of existing threads, since we
		// want multiple connections.
		/*
		 * // Cancel the thread that completed the connection if (mConnectThread
		 * != null) {mConnectThread.cancel(); mConnectThread = null;} // Cancel
		 * any thread currently running a connection if (mConnectedThread !=
		 * null) {mConnectedThread.cancel(); mConnectedThread = null;} // Cancel
		 * the accept thread because we only want to connect to one device if
		 * (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread =
		 * null;}
		 */

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket,lastPosInConnectedThreadList);
//				, uuidPos);
		mConnectedThread.start();
		// Add each connected thread to an array
//		if (mConnThreads.get(uuidPos) == null)
//			mConnThreads.add(uuidPos, mConnectedThread);
		mConnThreads.add(lastPosInConnectedThreadList,mConnectedThread);
		lastPosInConnectedThreadList++;
//		else {
//			mConnThreads.remove(uuidPos);
//			mConnThreads.add(uuidPos, mConnectedThread);
//		}
		// Send the name of the connected device back to the UI Activity
		// Message msg =
		// mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
		// Bundle bundle = new Bundle();
		// bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
		// msg.setData(bundle);
		// mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	/**
	 * Stop all threads
	 */
	///////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////////
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");
		// if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread
		// = null;}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		/////////////////////// if (mAcceptThread != null)
		/////////////////////// {mAcceptThread.cancel(); mAcceptThread = null;}
		for (int i = 0; i < maxUuid; i++) {
			if (mAcceptThreads.get(i) != null) {
				mAcceptThreads.get(i).cancel();
				mAcceptThreads.remove(i);
			}
		}
		setState(STATE_NONE);
	}

	///////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////////
	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// When writing, try to write out to all connected threads
		for (int i = 0; i < mConnThreads.size(); i++) {
			try {
				// Create temporary object
				ConnectedThread r;
				// Synchronize a copy of the ConnectedThread
				synchronized (this) {
					if (mState != STATE_CONNECTED)
						return;
					r = mConnThreads.get(i);
				}
				// Perform the write unsynchronized
				r.write(out);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		setState(STATE_LISTEN);
		// Commented out, because when trying to connect to all 7 UUIDs,
		// failures will occur
		// for each that was tried and unsuccessful, resulting in multiple
		// failure toasts.
		/*
		 * // Send a failure message back to the Activity Message msg =
		 * mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST); Bundle bundle =
		 * new Bundle(); bundle.putString(BluetoothChat.TOAST,
		 * "Unable to connect device"); msg.setData(bundle);
		 * mHandler.sendMessage(msg);
		 */
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		setState(STATE_LISTEN);

		// Send a failure message back to the Activity
		// Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
		// Bundle bundle = new Bundle();
		// bundle.putString(BluetoothChat.TOAST, "Device connection was lost");
		// msg.setData(bundle);
		// mHandler.sendMessage(msg);
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or
	 * until cancelled).
	 */
	private class AcceptThread extends Thread {
		BluetoothServerSocket serverSocket = null;
		private int uuidPos;

		public AcceptThread(int uuidPos) {
			this.uuidPos = uuidPos;
		}

		public void run() {
			if (D)
				Log.d(TAG, "BEGIN mAcceptThread" + this);
			setName("AcceptThread");
			BluetoothSocket socket = null;
			try {
				// Listen for all 7 UUIDs
				// for (int i = 0; i < 7; i++) {
				// Toast.makeText(activity.getApplicationContext(), "shit1",
				// Toast.LENGTH_SHORT).show();
				// listView.setItemChecked(1,true);
				serverSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, mUuids.get(uuidPos));
//				 listView.setItemChecked(1,true);
				// Toast.makeText(activity.getApplicationContext(), "shit2",
				// Toast.LENGTH_SHORT).show();
				while(true){
				socket = serverSocket.accept();
				if (socket != null) {
					String address = socket.getRemoteDevice().getAddress();
					mSockets.add(socket);
					mDeviceAddresses.add(address);
					connected(socket, socket.getRemoteDevice());
//							, uuidPos);
				}
				}
				// }
			} catch (IOException e) {
				// listView.setItemChecked(3,true);
				Log.e(TAG, "accept() failed", e);
			}
			if (D)
				Log.i(TAG, "END mAcceptThread");
			// listView.setItemChecked(3,true);
		}

		public void cancel() {
			if (D)
				Log.d(TAG, "cancel " + this);
			try {
				serverSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of server failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private int posInConnectedThreadList;
		private boolean studentIdentified = false;
		private String studentId="";
//		private int uuidPos;



		public ConnectedThread(BluetoothSocket socket, int posInConnectedThreadList){
//				, int uuidPos) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			this.posInConnectedThreadList = posInConnectedThreadList;
//			this.uuidPos = uuidPos;
			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}
		
		public String getStudentId() {
			return studentId;
		}

		public void setStudentId(String studentId) {
			this.studentId = studentId;
		}
		
		@SuppressWarnings("deprecation")
		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;

			// Keep listening to the InputStream while connected
			ConnectedThread connectedThread = mConnThreads.get(posInConnectedThreadList);
			while (true&&connectedThread!=null) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					String receivedMessage = new String(buffer, 0, bytes);
//					String studentId = new String(buffer, 0, bytes);
					
					////////////////////////////////////////
					/////check if the student is not in the list
					////////////////////////////////////////
					
					
//					int pos = Integer.parseInt((Character.toString((char) buffer[0])));
//					LectStudentRegListController.receivePos(pos);
					
					if(studentIdentified)
					{
                    	String[] splited= receivedMessage.split("-");
                    	String fileSize = splited[0];
                    	byte[] readFile = new byte[Integer.valueOf(fileSize)];


                    	int byteStartIndex = String.valueOf(fileSize).length()+1;
                    	int bIndex = 0;
                    	for (int i = byteStartIndex; i < bytes; i++) {
							readFile[bIndex] = buffer[i];
							bIndex++;
						}
                    	buffer = new byte[1024];
                    	while ((bytes = mmInStream.read(buffer)) > -1) {                   		
                    		for (int i = 0; i < bytes; i++) {
								readFile[bIndex] = buffer[i];
								bIndex++;
							}
                    		buffer = new byte[1024];
                    		if(bIndex==Integer.valueOf(fileSize))
                    			break;
                    	}
//                    	String zipFile = quizPath+quizName+".zip";

//                    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    	ZipOutputStream zos = new ZipOutputStream(baos);
//                    	ZipEntry entry = new ZipEntry(zipFile);
//                    	entry.setSize(readFile.length);
//                    	zos.putNextEntry(entry);
//                    	zos.write(readFile);
//                    	zos.closeEntry();
//                    	zos.close();
                    	
                    	
                    	ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(readFile));
                    	ZipEntry entry = null;
                    	studentsAnswersPath = LectQuizSelectionController.studentsAnswersPath;
                    	new File(studentsAnswersPath).mkdir();
                    	new File(studentsAnswersPath+studentId+"/").mkdir();
                    	while ((entry = zipStream.getNextEntry()) != null) {
                    		
                    	    String entryName = entry.getName();
                    	    File file = new File(studentsAnswersPath+studentId+"/"+entryName);
                    	    
                    	    FileOutputStream out = new FileOutputStream(file);

                    	    byte[] byteBuff = new byte[4096];
                    	    int bytesRead = 0;
                    	    while ((bytesRead = zipStream.read(byteBuff)) != -1)
                    	    {
                    	        out.write(byteBuff, 0, bytesRead);
                    	    }

                    	    out.close();
                    	    zipStream.closeEntry();
                    	}
                    	zipStream.close(); 
                    	
                    	PcZipFileManager.createZipFile(new File(applicationPath), activity.getFilelist().getCanonicalPath() + "/"+Constants.APP_NAME+".zip");
						String str = getStudentId();
                    	mHandler.obtainMessage(Constants.MESSAGE_READ, -1,
								-1, str).sendToTarget();
//  //////////////                  	new UploadFolderDB().execute(activity.getFilelist().getCanonicalPath() + "/OnlineQuizChecker.zip", "/");
//                	    ZipOutputStream fileOuputStream = 
//                                new ZipOutputStream(zipFile); 
//                	    fileOuputStream.write(readFile);
//                	    fileOuputStream.close();
                    	
//                    	File file = new File(zipFile);
//                    	if(file.exists())
//                    	{
//                    		;
//                    	}
//                    	zipFileManager.unZipIt(zipFile, quizPath);
						
					}
					// Send the obtained bytes to the UI Activity
					else
					{
						if(LectStudentRegListController.studentPosInList(receivedMessage,LectStudentRegListController.students)!=-1)
						{
							studentIdentified = true;
							setStudentId(receivedMessage);
							mHandler.obtainMessage(Constants.MESSAGE_READ, bytes,
									posInConnectedThreadList, buffer).sendToTarget();
					 ////////////////
					 
					 
					 //write successful connection  to the student
					 
					 
					 ////////////////
						}
						else
						{
			            byte [] msg = toByteArray("You have not registered to this course");
			            write(msg);
			            
//			            cancel();
			            mConnThreads.remove(posInConnectedThreadList);
			            lastPosInConnectedThreadList--;
			            connectedThread=null;
//			            connectedThread.destroy();
			            
						}
					}
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
//					connectionLost();
					cancel();
					////////////////////
//					mAcceptThreads.remove(uuidPos);
//					mAcceptThreads.add(uuidPos, new AcceptThread(uuidPos));
//					mAcceptThreads.get(uuidPos).start();
					////////////////////
					break;
				}

			}
		}
		
        public InputStream getMmInStream() {
			return mmInStream;
		}

		public OutputStream getMmOutStream() {
			return mmOutStream;
		}
		
        private byte[] toByteArray(CharSequence charSequence) {
            if (charSequence == null) {
              return null;
            }
            byte[] bytesArray = new byte[charSequence.length()];
            for (int i = 0; i < bytesArray.length; i++) {
            	bytesArray[i] = (byte) charSequence.charAt(i);
            }

            return bytesArray;
        }
        
		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);

				// Share the sent message back to the UI Activity
				// mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1,
				// buffer)
				// .sendToTarget();
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}
}