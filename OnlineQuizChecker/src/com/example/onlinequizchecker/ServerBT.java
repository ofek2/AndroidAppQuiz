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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/**
 * The Class ServerBT.
 * This class is the Bluetooth Server.
 * It is used for connecting to the Bluetooth clients 
 * (make a connection between lecturer and students)
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

	/** The BluetoothAdapter. */
	// Member fields
	private final BluetoothAdapter mAdapter;

	/** The handler. */
	private final Handler mHandler;

	/** The accept thread. */
	private AcceptThread mAcceptThread = null;

	/** The connected thread. */
	private ConnectedThread mConnectedThread;

	/** The state. */
	private int mState;

	/** The device addresses. */
	private ArrayList<String> mDeviceAddresses;

	/** The connected threads. */
	public static ArrayList<ConnectedThread> mConnThreads;

	/** The Bluetooth Sockets. */
	private ArrayList<BluetoothSocket> mSockets;

	/** The UUID. */
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
	/** The activity. */
	private MainActivity activity;

	/** The last position in connected thread list. */
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
	public int aorqbs;// Amount of received quizzes by students

	/**
	 * Constructor. Prepares a new BluetoothChat session. // * @param context
	 * The UI Activity Context // * @param handler A Handler to send messages
	 * back to the UI Activity
	 *
	 * @param activity
	 *            the activity
	 * @param mHandler
	 *            the m handler
	 * @param course
	 *            the course
	 */
	@SuppressLint("NewApi")
	public ServerBT(MainActivity activity, Handler mHandler, String course) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		this.course = course;
		this.activity = activity;
		try {
			applicationPath = activity.getFilelist().getCanonicalPath() + "/";
		} catch (IOException e) {
			e.printStackTrace();
		}
		initializePINCode();

		if (mAdapter == null) {
			// Device does not support Bluetooth
			/////////////////////

			//////////////
		}
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);


		mState = STATE_NONE;
		this.mHandler = mHandler;
		mDeviceAddresses = new ArrayList<String>();
		mConnThreads = new ArrayList<ConnectedThread>();
		mSockets = new ArrayList<BluetoothSocket>();
		lastPosInConnectedThreadList = 0;
		stoped = false;
		mUuid = UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc");
		aorqbs = 0;
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		activity.startActivityForResult(discoverableIntent, 1);
	}

	/**
	 * Initialize pin code.
	 */
	private void initializePINCode() {
		Random random = new Random();
		int randomNumber = random.nextInt(10);
		stringPinCode = String.valueOf(randomNumber);
		randomNumber = random.nextInt(10);
		stringPinCode = stringPinCode + String.valueOf(randomNumber);
		randomNumber = random.nextInt(10);
		stringPinCode = stringPinCode + String.valueOf(randomNumber);
		randomNumber = random.nextInt(10);
		stringPinCode = stringPinCode + String.valueOf(randomNumber);
		((TextView) activity.findViewById(R.id.PINCodeTxt)).setText(stringPinCode);
	}

	/**
	 * Set the current state of the chat connection.
	 *
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;
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
	 * @param listview
	 *            the listview
	 */
	public synchronized void start() {

		// Cancel any thread currently running a connection
		for (int i = 0; i < mConnThreads.size(); i++) {
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
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected // * @param
	 *            uuidPos
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

		if (D)
			Log.d(TAG, "connected");

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket, lastPosInConnectedThreadList);

		mConnThreads.add(mConnectedThread);
		lastPosInConnectedThreadList++;
		mConnectedThread.start();

		setState(STATE_CONNECTED);
	}

	/**
	 * Stop all threads.
	 */
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");
		stoped = true;
		for (int i = 0; i < mConnThreads.size(); i++) {
			if (mConnThreads.get(i) != null) {
				mConnThreads.get(i).connectedThread = null;
				mConnThreads.get(i).cancel();
				mConnThreads.set(i, null);
				mConnThreads.remove(i);
			} else
				mConnThreads.remove(i);
		}

		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		setState(STATE_NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner.
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
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or
	 * until cancelled).
	 */
	private class AcceptThread extends Thread {

		/** The server socket. */
		BluetoothServerSocket serverSocket = null;

		/**
		 * Run.
		 */
		public void run() {
			if (D)
				Log.d(TAG, "BEGIN mAcceptThread" + this);
			setName("AcceptThread");
			BluetoothSocket socket = null;
			try {
				serverSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, mUuid);
				while (true && serverSocket != null) {
					socket = serverSocket.accept();
					if (socket != null) {
						String address = socket.getRemoteDevice().getAddress();
						mSockets.add(socket);
						mDeviceAddresses.add(address);
						connected(socket, socket.getRemoteDevice());
					}
				}
				// }
			} catch (IOException e) {
				/////////////////////////// start accept thread
				if (!stoped) {
					if (LectQuizInitiationController.selectedTimePeriodInt == 0) {// The
																					// quiz
																					// was
																					// not
																					// started
																					// yet
						mHandler.obtainMessage(Constants.CONNECTION_LOST, 0, 0, null).sendToTarget();
					} else if (LectQuizInitiationController.selectedTimePeriodInt > 0) {
						mAcceptThread = new AcceptThread();
						mAcceptThread.start();
					}
				}
			}
			if (D)
				Log.i(TAG, "END mAcceptThread");
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
		private String studentId = "";

		/** The connected thread. */
		private ConnectedThread connectedThread;

		/**
		 * Instantiates a new connected thread.
		 *
		 * @param socket
		 *            the socket
		 * @param posInConnectedThreadList
		 *            the pos in connected thread list
		 */
		public ConnectedThread(BluetoothSocket socket, int posInConnectedThreadList) {
			// , int uuidPos) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			this.posInConnectedThreadList = posInConnectedThreadList;
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
		 * @param studentId
		 *            the new student id
		 */
		public void setStudentId(String studentId) {
			this.studentId = studentId;
		}

		/**
		 * Run.
		 */
		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;

			// Keep listening to the InputStream while connected
			connectedThread = mConnThreads.get(posInConnectedThreadList);
			while (true && connectedThread != null) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					String receivedMessage = new String(buffer, 0, bytes);

					if (studentIdentified) {
						String[] splited = receivedMessage.split("-");

						// If in quiz time, a student is trying to get out of
						// class with his phone
						if (splited[0].equals(Constants.MOVING)) {
							mHandler.obtainMessage(Constants.MOTION_SENSOR_TRIGGERED, 0, 0, splited[1]).sendToTarget();
						} else if (receivedMessage.equals("Received Quiz")) {
							aorqbs++;
							if (aorqbs == ServerBT.mConnThreads.size()) {
								for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
									ServerBT.mConnThreads.get(i).getMmOutStream().write(toByteArray("Start Quiz"));
								}
								mHandler.obtainMessage(Constants.QUIZ_INITIATION, 0, 0, null).sendToTarget();
							}
						} else {
							String fileSize = splited[0];
							studentsAnswersPath = activity.getFilelist().getCanonicalPath() + splited[1];
							byte[] readFile = new byte[Integer.valueOf(fileSize)];

							// Read zip file received from student
							int byteStartIndex = splited[1].length() + String.valueOf(fileSize).length() + 2;
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
								if (bIndex == Integer.valueOf(fileSize))
									break;
							}

							// Extract zip in lecturer phone
							ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(readFile));
							ZipEntry entry = null;
							new File(studentsAnswersPath).mkdir();
							new File(studentsAnswersPath + studentId + "/").mkdir();
							while ((entry = zipStream.getNextEntry()) != null) {

								String entryName = entry.getName();
								File file = new File(studentsAnswersPath + studentId + "/" + entryName);

								FileOutputStream out = new FileOutputStream(file);

								byte[] byteBuff = new byte[4096];
								int bytesRead = 0;
								while ((bytesRead = zipStream.read(byteBuff)) != -1) {
									out.write(byteBuff, 0, bytesRead);
								}

								out.close();
								zipStream.closeEntry();
							}
							zipStream.close();

							zipFileManager.createZipFile(new File(applicationPath),
									activity.getFilelist().getCanonicalPath() + "/" + Constants.APP_NAME + ".zip",
									true);
							String str = getStudentId();
							if (!LectQuizSelectionController.studentsAnswersPath.isEmpty())
								mHandler.obtainMessage(Constants.MESSAGE_READ, posInConnectedThreadList, -1, str)
										.sendToTarget();
							else
								mHandler.obtainMessage(Constants.BLINK_RECOVERY, posInConnectedThreadList, -1, str)
										.sendToTarget();

						}
					}
					// Send the obtained bytes to the UI Activity
					else {
						String[] splited = receivedMessage.split("-");
						String StudentId = splited[0];
						if (splited[1].equals(stringPinCode)) {
							if (LectStudentRegListController.studentPosInList(StudentId,
									LectStudentRegListController.students) != -1) {
								studentIdentified = true;
								setStudentId(StudentId);
								if (splited.length == 2) {
									byte[] msg = toByteArray("You have authorized-" + course);
									write(msg);
									// setStudentId(StudentId);
									mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, posInConnectedThreadList,
											StudentId).sendToTarget();
								}
						
							} else {
								byte[] msg = toByteArray("You have not registered to this course");
								write(msg);

							
								mConnThreads.remove(posInConnectedThreadList);
								updatePositions(posInConnectedThreadList);////
								lastPosInConnectedThreadList--;
								connectedThread = null;

							}
						} else {
							byte[] msg = toByteArray("The PIN code is not correct");
							write(msg);
							mConnThreads.remove(posInConnectedThreadList);
							updatePositions(posInConnectedThreadList);////
							lastPosInConnectedThreadList--;
							connectedThread = null;
						}
					}
				} catch (IOException e) {
					connectionLost();
				}

			}
		}

		/**
		 * Connection lost.
		 */
		private synchronized void connectionLost() {
			if (connectedThread != null) {
				mHandler.obtainMessage(Constants.CANCEL_MARK, 0, 0, studentId).sendToTarget();
				mConnThreads.remove(posInConnectedThreadList);
				updatePositions(posInConnectedThreadList);
				lastPosInConnectedThreadList--;
				connectedThread = null;
			}
			cancel();
		}

		/**
		 * Update positions.
		 *
		 * @param startFrom
		 *            the start from
		 */
		private void updatePositions(int startFrom) {
			for (int i = startFrom; i < mConnThreads.size(); i++) {
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
		 * @param charSequence
		 *            the char sequence
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
				// mConnThreads.set(posInConnectedThreadList,null);
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}
}