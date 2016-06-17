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

/**
 * The Class ServerBT.
 */
public class ServerBT {

	/** The Constant TAG. */
	// Debugging
	private static final String TAG = "ServerBT";
	
	/** The Constant D. */
	private static final boolean D = true;

	/** The Constant NAME. */
	// Name for the SDP record when creating server socket
	private static final String NAME = "Lecturer";

	/** The m adapter. */
	// Member fields
	private final BluetoothAdapter mAdapter;
	 
 	/** The m handler. */
 	private final Handler mHandler;
	
	/** The m accept thread. */
	private AcceptThread mAcceptThread = null;
	
	/** The m connected thread. */
	// private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	
	/** The m state. */
	private int mState;
	
	/** The stage. */
	private int stage = 1;
	
	/** The m device addresses. */
	private ArrayList<String> mDeviceAddresses;
	
	/** The m conn threads. */
	public static ArrayList<ConnectedThread> mConnThreads;
	
	/** The m sockets. */
	private ArrayList<BluetoothSocket> mSockets;
	
	/** The max uuid. */
	private int maxUuid = 1;
	/**
	 * A bluetooth piconet can support up to 7 connections. This array holds 7
	 * unique UUIDs. When attempting to make a connection, the UUID on the
	 * client must match one that the server is listening for. When accepting
	 * incoming connections server listens for all 7 UUIDs. When trying to form
	 * an outgoing connection, the client tries each UUID one at a time.
	 */
	private UUID mUuid;
	
	/** The course. */
	private String course;
	
	/** The Constant STATE_NONE. */
	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	
	/** The Constant STATE_LISTEN. */
	public static final int STATE_LISTEN = 1; // now listening for incoming
												
												/** The Constant STATE_CONNECTING. */
												// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													
													/** The Constant STATE_CONNECTED. */
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote
													// device

	/** The random ordered mac characters. */
													private char[] randomOrderedMacCharacters = { '0', 'A', '6', '7', 'C', 'D', '8', '9', 'E', '4', '1', '5', 'F', '3',
			'2', 'B' };
	
	/** The mac characters. */
	private char[] macCharacters = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	/** The pin code. */
	private char[] pinCode = new char[8];
	
	/** The decimal pos in pin code. */
	// private String hexaPosInPinCode;
	private int decimalPosInPinCode;
	
	/** The activity. */
	private MainActivity activity;
	
	/** The list view. */
	private ListView listView;
	
	/** The last pos in connected thread list. */
	private int lastPosInConnectedThreadList;
	
	/** The students answers path. */
	private String studentsAnswersPath;
	
	/** The application path. */
	private String applicationPath;
	
	/** The string pin code. */
	private String stringPinCode;
	
	/** The stoped. */
	private boolean stoped;
	
	/** The aorqbs. */
	public int aorqbs;//Amount of received quizzes by students
	
	/**
	 * Constructor. Prepares a new BluetoothChat session. // * @param context
	 * The UI Activity Context // * @param handler A Handler to send messages
	 * back to the UI Activity
	 *
	 * @param activity the activity
	 * @param mHandler the m handler
	 * @param course the course
	 */
	@SuppressLint("NewApi")
	public ServerBT(MainActivity activity, Handler mHandler,String course) {
		// , Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		this.course = course;
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
//		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//		activity.startActivityForResult(discoverableIntent,1);

		mState = STATE_NONE;
		 this.mHandler = mHandler;
		mDeviceAddresses = new ArrayList<String>();
		mConnThreads = new ArrayList<ConnectedThread>();
		mSockets = new ArrayList<BluetoothSocket>();
		lastPosInConnectedThreadList=0;
		stoped = false;
		mUuid = UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc");
		aorqbs = 0;
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		activity.startActivityForResult(discoverableIntent,1);
		// mUuids.add(UUID.fromString("2d64189d-5a2c-4511-a074-77f199fd0834"));
		// mUuids.add(UUID.fromString("e442e09a-51f3-4a7b-91cb-f638491d1412"));
		// mUuids.add(UUID.fromString("a81d6504-4536-49ee-a475-7d96d09439e4"));
		// mUuids.add(UUID.fromString("aa91eab1-d8ad-448e-abdb-95ebba4a9b55"));
		// mUuids.add(UUID.fromString("4d34da73-d0a4-4f40-ac38-917e0a9dee97"));
		// mUuids.add(UUID.fromString("5e14d4df-9c8a-4db7-81e4-c937564c86e0"));
	}

	/**
	 * Initialize pin code.
	 */
	private void initializePINCode() {
//		String macAddress = mAdapter.getAddress();
		String macAddress = android.provider.Settings.Secure.getString(activity.getApplicationContext().getContentResolver(), "bluetooth_address");
		
//		Toast.makeText(activity.getApplicationContext(), macAddress, Toast.LENGTH_SHORT).show();
//		int j = 0;
//		for (int i = 0; i < macAddress.length(); i++) {
//
//			int firstPos = findInArray(macAddress.charAt(i), randomOrderedMacCharacters);
//			int secondPos = findInArray(macAddress.charAt(i + 1), randomOrderedMacCharacters);
//
//			if (firstPos >= secondPos) {
//				decimalPosInPinCode = firstPos - secondPos;
//			} else
//				decimalPosInPinCode = 16 - (secondPos - firstPos);
//			pinCode[j] = macCharacters[decimalPosInPinCode];
//			j++;
//			i += 2;
//		}
//		Random random = new Random();
//		int randomNumber = random.nextInt(16);
//		pinCode[j] = macCharacters[randomNumber];
//		pinCode[j + 1] = randomOrderedMacCharacters[randomNumber];
//		String stringPinCode = String.valueOf(pinCode);
		Random random = new Random();
		int randomNumber = random.nextInt(10);
		stringPinCode = String.valueOf(randomNumber);
		randomNumber = random.nextInt(10);
		stringPinCode = stringPinCode+String.valueOf(randomNumber);
		randomNumber = random.nextInt(10);
		stringPinCode = stringPinCode+String.valueOf(randomNumber);
		randomNumber = random.nextInt(10);
		stringPinCode = stringPinCode+String.valueOf(randomNumber);
		((TextView) activity.findViewById(R.id.PINCodeTxt)).setText(stringPinCode);
	}

	/**
	 * Find in array.
	 *
	 * @param ch the ch
	 * @param array the array
	 * @return the int
	 */
	private int findInArray(char ch, char[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == ch)
				return i;
		}
		return -1;

	}

	/**
	 * Set the current state of the chat connection.
	 *
	 * @param state            An integer defining the current connection state
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
	 *
	 * @return the state
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 *
	 * @param listview the listview
	 */
	public synchronized void start(ListView listview) {
		///////////////////////////////////////////////////////////////
		this.listView = listview;
		///////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////
		// if (D) Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		// if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread
		// = null;}

		// Cancel any thread currently running a connection
		for (int i=0;i<mConnThreads.size();i++) {
			if (mConnThreads.get(i) != null) {
				mConnThreads.get(i).cancel();
				mConnThreads.remove(i);
			}
		}
		// Start the thread to listen on a BluetoothServerSocket
			if (mAcceptThread == null) {
				mAcceptThread = new AcceptThread();
				mAcceptThread.start();
			}

		setState(STATE_LISTEN);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection.
	 *
	 * @param socket            The BluetoothSocket on which the connection was made
	 * @param device            The BluetoothDevice that has been connected
	 * //	 * @param uuidPos
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
		mConnThreads.add(mConnectedThread);
		lastPosInConnectedThreadList++;
		mConnectedThread.start();
		// Add each connected thread to an array
//		if (mConnThreads.get(uuidPos) == null)
//			mConnThreads.add(uuidPos, mConnectedThread);
//		mConnThreads.add(mConnectedThread);
//		lastPosInConnectedThreadList++;
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
	 * Stop all threads.
	 */
	///////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////////
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");
		// if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread
		// = null;}
		stoped = true;
		for (int i=0;i<mConnThreads.size();i++) {
			if (mConnThreads.get(i) != null) {
				mConnThreads.get(i).connectedThread = null;
				mConnThreads.get(i).cancel();				
				mConnThreads.set(i,null);
				mConnThreads.remove(i);
			}
			else
				mConnThreads.remove(i);
		}
		
		 if (mAcceptThread != null)
		 {mAcceptThread.cancel(); mAcceptThread = null;}



		setState(STATE_NONE);
	}

	///////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////////
	/**
	 * Write to the ConnectedThread in an unsynchronized manner.
	 *
	 * @param out            The bytes to write
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
		
		/** The server socket. */
		BluetoothServerSocket serverSocket = null;
		
		/** The uuid pos. */
		private int uuidPos;

		/**
		 * Instantiates a new accept thread.
		 */
		public AcceptThread() {
			this.uuidPos = uuidPos;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
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
				serverSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, mUuid);
//				 listView.setItemChecked(1,true);
				// Toast.makeText(activity.getApplicationContext(), "shit2",
				// Toast.LENGTH_SHORT).show();
				while(true&&serverSocket!=null){
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
				///////////////////////////start accept thread
				if(!stoped)
				{
					if (LectQuizInitiationController.selectedTimePeriodInt==0) {//The quiz was not started yet
                		mHandler.obtainMessage(Constants.CONNECTION_LOST, 0,
								0, null).sendToTarget();
					}
					else if(LectQuizInitiationController.selectedTimePeriodInt>0)
					{
						mAcceptThread = new AcceptThread();
						mAcceptThread.start();
					}
				}
				// listView.setItemChecked(3,true);
//				Log.e(TAG, "accept() failed", e);
			}
			if (D)
				Log.i(TAG, "END mAcceptThread");
			// listView.setItemChecked(3,true);
		}

		/**
		 * Cancel.
		 */
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
		
		/** The mm socket. */
		private final BluetoothSocket mmSocket;
		
		/** The mm in stream. */
		private final InputStream mmInStream;
		
		/** The mm out stream. */
		private final OutputStream mmOutStream;
		
		/** The pos in connected thread list. */
		private int posInConnectedThreadList;
		
		/** The student identified. */
		private boolean studentIdentified = false;
		
		/** The student id. */
		private String studentId="";
		
		/** The connected thread. */
		private ConnectedThread connectedThread;
//		private int uuidPos;



		/**
 * Instantiates a new connected thread.
 *
 * @param socket the socket
 * @param posInConnectedThreadList the pos in connected thread list
 */
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
		
		/**
		 * Gets the student id.
		 *
		 * @return the student id
		 */
		public String getStudentId() {
			return studentId;
		}

		/**
		 * Sets the student id.
		 *
		 * @param studentId the new student id
		 */
		public void setStudentId(String studentId) {
			this.studentId = studentId;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@SuppressWarnings("deprecation")
		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;

			// Keep listening to the InputStream while connected
			connectedThread = mConnThreads.get(posInConnectedThreadList);
			while (true&&connectedThread!=null) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					String receivedMessage = new String(buffer, 0, bytes);

					if(studentIdentified)
					{
                    	String[] splited= receivedMessage.split("-");
                    	
                    	//If in quiz time, a student is trying to get out of class with his phone
                    	if(splited[0].equals(Constants.MOVING)) 
                    	{
                    		mHandler.obtainMessage(Constants.MOTION_SENSOR_TRIGGERED, 0,
    								0, splited[1]).sendToTarget();
                    	}
                    	else if(receivedMessage.equals("Received Quiz"))
                    	{
                    		aorqbs++;
							if (aorqbs == ServerBT.mConnThreads.size()) {
								for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
									ServerBT.mConnThreads.get(i).getMmOutStream().write(toByteArray("Start Quiz"));
								}								
	                    		mHandler.obtainMessage(Constants.QUIZ_INITIATION, 0,
	    								0, null).sendToTarget();								
							}
                    	}
                    	else
                    	{
                    		String fileSize = splited[0];
                    		studentsAnswersPath = activity.getFilelist().getCanonicalPath()+splited[1];
                    		byte[] readFile = new byte[Integer.valueOf(fileSize)];

                    		//Read zip file received from student
                    		int byteStartIndex = splited[1].length()+String.valueOf(fileSize).length()+2;
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
                    	
                    	//Extract zip in lecturer phone
	                    	ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(readFile));
	                    	ZipEntry entry = null;
//	                    	studentsAnswersPath = LectQuizSelectionController.studentsAnswersPath;
	                    	
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
							if(!LectQuizSelectionController.studentsAnswersPath.isEmpty())
								mHandler.obtainMessage(Constants.MESSAGE_READ, posInConnectedThreadList,
										-1, str).sendToTarget();
							else
								mHandler.obtainMessage(Constants.BLINK_RECOVERY, posInConnectedThreadList,
										-1, str).sendToTarget();
	                    	
                    	}
					}
					// Send the obtained bytes to the UI Activity
					else
					{
						String[] splited= receivedMessage.split("-");
						String StudentId= splited[0];
						if(splited[1].equals(stringPinCode))
						{
							if (LectStudentRegListController.studentPosInList(StudentId,LectStudentRegListController.students) != -1)
							{
								studentIdentified = true;
								setStudentId(StudentId);
								if(splited.length==2)
								{
								byte[] msg = toByteArray("You have authorized-"+course);
								write(msg);
//								setStudentId(StudentId);
								mHandler.obtainMessage(Constants.MESSAGE_READ,
										bytes, posInConnectedThreadList, StudentId)
										.sendToTarget();
								}
								// //////////////

								// write successful connection to the student

								// //////////////
							} 
							else {
								byte[] msg = toByteArray("You have not registered to this course");
								write(msg);

//								cancel();////
								mConnThreads.remove(posInConnectedThreadList);
								updatePoistions(posInConnectedThreadList);////
								lastPosInConnectedThreadList--;
								connectedThread = null;
								
								// connectedThread.destroy();

							}
						}
						else
						{
							byte[] msg = toByteArray("The PIN code is not correct");
							write(msg);

//							 cancel();/////
							mConnThreads.remove(posInConnectedThreadList);
							updatePoistions(posInConnectedThreadList);////
							lastPosInConnectedThreadList--;
							connectedThread = null;
						}
					}
				} catch (IOException e) {
					connectionLost();
////					Log.e(TAG, "disconnected", e);
////					connectionLost();
//					if (connectedThread != null)
//					{
////					mConnThreads.remove(connectedThread);
//					mHandler.obtainMessage(Constants.CANCEL_MARK,
//							0, 0, studentId)
//							.sendToTarget();
////					mConnThreads.set(posInConnectedThreadList,null);
//					mConnThreads.remove(posInConnectedThreadList);///
//					updatePoistions(posInConnectedThreadList);////
//					lastPosInConnectedThreadList--;///
//					connectedThread = null;
////					ServerBT.this.lastPosInConnectedThreadList--;
//					}
////					mHandler.obtainMessage(Constants.CANCEL_MARK,//////////////////////////////
////							0, 0, getStudentId())//////////////////////
////							.sendToTarget();/////////////////////////////
//
//					cancel();////////////////////////////////////////////////////////////
//
//					////////////////////
////					mAcceptThreads.remove(uuidPos);
////					mAcceptThreads.add(uuidPos, new AcceptThread(uuidPos));
////					mAcceptThreads.get(uuidPos).start();
//					////////////////////
//					break;
				}

			}
		}
		
		/**
		 * Connection lost.
		 */
		private synchronized void connectionLost() {
//			Log.e(TAG, "disconnected", e);
//			connectionLost();
			if (connectedThread != null)
			{
//			mConnThreads.remove(connectedThread);
			mHandler.obtainMessage(Constants.CANCEL_MARK,
					0, 0, studentId)
					.sendToTarget();
//			mConnThreads.set(posInConnectedThreadList,null);
			mConnThreads.remove(posInConnectedThreadList);///
			updatePoistions(posInConnectedThreadList);////
			lastPosInConnectedThreadList--;///
			connectedThread = null;
//			ServerBT.this.lastPosInConnectedThreadList--;
			}
//			mHandler.obtainMessage(Constants.CANCEL_MARK,//////////////////////////////
//					0, 0, getStudentId())//////////////////////
//					.sendToTarget();/////////////////////////////

			cancel();////////////////////////////////////////////////////////////

			////////////////////
//			mAcceptThreads.remove(uuidPos);
//			mAcceptThreads.add(uuidPos, new AcceptThread(uuidPos));
//			mAcceptThreads.get(uuidPos).start();
			////////////////////
		}
		
		/**
		 * Update poistions.
		 *
		 * @param startFrom the start from
		 */
		private void updatePoistions(int startFrom)
		{
			for (int i = startFrom; i<mConnThreads.size();i++) {
				mConnThreads.get(i).posInConnectedThreadList--; 
			}
		}
        
        /**
         * Gets the mm in stream.
         *
         * @return the mm in stream
         */
        public InputStream getMmInStream() {
			return mmInStream;
		}

		/**
		 * Gets the mm out stream.
		 *
		 * @return the mm out stream
		 */
		public OutputStream getMmOutStream() {
			return mmOutStream;
		}
		
        /**
         * To byte array.
         *
         * @param charSequence the char sequence
         * @return the byte[]
         */
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

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				mmSocket.close();
//				mConnThreads.set(posInConnectedThreadList,null);
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}
}