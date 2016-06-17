package com.example.onlinequizchecker;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

        import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


// TODO: Auto-generated Javadoc
/**
 * The Class ClientBT.
 * This class is the Bluetooth Client used to connect to the Bluetooth Server.
 */
public class ClientBT {
    
    /** The Constant TAG. */
    // Debugging
    private static final String TAG = "ClientBT";
    
    /** The Constant D. */
    private static final boolean D = true;

    /** The m adapter. */
    // Member fields
    private final BluetoothAdapter mAdapter;
    
    /** The m handler. */
    private final Handler mHandler;

/** The m connect thread. */
//    private AcceptThread mAcceptThread;
    public ConnectThread mConnectThread;
    
    /** The m connected thread. */
    public ConnectedThread mConnectedThread;
    
    /** The quiz path to zip. */
    public static String quizPathToZip;
    
    /** The m state. */
    private int mState;
     
    /** The uuids. */
    private ArrayList<UUID> mUuids;
	
	/** The student id. */
	private CharSequence studentId;
	
	/** The application path. */
	private String applicationPath;
	
	/** The main activity. */
	private MainActivity mainActivity;
	
	/** The lecturer device. */
	private BluetoothDevice lecturerDevice;
	
	/** The lecturer device uuid. */
	private UUID lecturerDeviceUuid;
	
	/** The path to send. */
	public static String pathToSend;
	
	/** The quiz was initiated. */
	public static boolean quizWasInitiated;
	
	/** The quiz period. */
	private  String quizPeriod;
    
    /** The Constant STATE_NONE. */
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    
    /** The Constant STATE_LISTEN. */
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    
    /** The Constant STATE_CONNECTING. */
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    
    /** The Constant STATE_CONNECTED. */
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * Constructor. Prepares a new BluetoothChat session.
     * //     * @param context  The UI Activity Context
     *
     * @param studentId the student id
     * @param mHandler the m handler
     * @param mainActivity //     * @param handler  A Handler to send messages back to the UI Activity
     * @param applicationPath the application path
     */
    public ClientBT(CharSequence studentId, Handler mHandler,MainActivity mainActivity, String applicationPath) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        this.studentId = studentId;
        this.mHandler = mHandler;
        this.mainActivity = mainActivity;
        this.applicationPath = applicationPath;
        mUuids = new ArrayList<UUID>();
        lecturerDevice = null;
        pathToSend = "";
        quizWasInitiated = false;

        mUuids.add(UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));

    }

    /**
     * Set the current state of the chat connection.
     *
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
//        mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
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
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to listen on a BluetoothServerSocket
//        if (mAcceptThread == null) {
//            mAcceptThread = new AcceptThread();
//            mAcceptThread.start();
//        }
//        setState(STATE_LISTEN);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}


            try {         
					mConnectThread = new ConnectThread(device, mUuids.get(0));
					setState(STATE_CONNECTING);
            } catch (Exception e) {
            }
        
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection.
     *
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        //Commented out all the cancellations of existing threads, since we want multiple connections.
        /*
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
         */
//        found=true;
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

//        // Send the name of the connected device back to the UI Activity
//        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
        
    }

    /**
     * Stop all threads.
     */
    @SuppressWarnings("deprecation")
	public synchronized void stop() {
//        if (D) Log.d(TAG, "stop");
//        if (mConnectThread.isAlive()) 
//        {
////        	mConnectThread.cancel();
//        	mConnectThread.destroy();
//        	mConnectThread = null;
//        }
        if (mConnectedThread != null)
        {
        	mConnectedThread.cancel();
//        	mConnectedThread.destroy();
        	mConnectedThread = null;
        }
//        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
    }


    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);
        // Commented out, because when trying to connect to all 7 UUIDs, failures will occur
        // for each that was tried and unsuccessful, resulting in multiple failure toasts.
        /*
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        */
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
//        mHandler.obtainMessage(Constants.CONNECTION_LOST, 0,
//                0, null).sendToTarget();
        if (mConnectedThread != null)
        {
        	mConnectedThread = null;
        	if(StudQuizActivity.submited==false){
        		if(quizWasInitiated)
        			while(mConnectedThread==null&&StudQuizActivity.submited==false)
        				mConnectThread = new ConnectThread(lecturerDevice, lecturerDeviceUuid);
        		else
                    mHandler.obtainMessage(Constants.CONNECTION_LOST, 0, 0, null)
                    .sendToTarget();
        	}
        	else
        	{
        		mAdapter.disable();
        		StudQuizActivity.submited = false;
        	}
//            mConnectedThread = null;
        }
        // Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothChat.TOAST, "Device connection was lost");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
    }



    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    class ConnectThread {

/** The mm socket. */
//            extends Thread {
        private BluetoothSocket mmSocket;
        
        /** The mm device. */
        private final BluetoothDevice mmDevice;
        
        /** The temp uuid. */
        private UUID tempUuid;

        /**
         * Instantiates a new connect thread.
         *
         * @param device the device
         * @param uuidToTry the uuid to try
         */
        public ConnectThread(BluetoothDevice device, UUID uuidToTry) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            tempUuid = uuidToTry;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {

                tmp = device.createInsecureRfcommSocketToServiceRecord(uuidToTry);
//                mAdapter.cancelDiscovery();//
//                StudAuthController.maxDiscoveryIteration=0;
//                mHandler.obtainMessage(Constants.STUDENT_AUTHORIZED, 0, 0, null)
//                        .sendToTarget();

                    mmSocket = tmp;//
                StudAuthController.currentlyCheckingDevice = true;
                mAdapter.cancelDiscovery();
                mmSocket.connect();
                StudAuthController.lecturerFound=true;
//                mAdapter.cancelDiscovery();
                lecturerDevice = mmDevice;
                lecturerDeviceUuid = uuidToTry;
                    connected(mmSocket, mmDevice);

            } catch (IOException e) {
            	if(!StudAuthController.lecturerFound)
                StudAuthController.scanDevices.add(mmDevice);
//                StudAuthController.currentlyCheckingDevice = false;
//                mAdapter.startDiscovery();
//                Log.e(TAG, "create() failed", e);
            }
//            mmSocket = tmp;
        }
//
//        public void run() {
//            Log.i(TAG, "BEGIN mConnectThread");
//            setName("ConnectThread");
//
//            // Always cancel discovery because it will slow down a connection
////            mAdapter.cancelDiscovery();
//
//            // Make a connection to the BluetoothSocket
////            int temp = 0;
//            try {
//                // This is a blocking call and will only return on a
//                // successful connection or an exception
////            	mAdapter.cancelDiscovery();
////            	temp = StudAuthController.maxDiscoveryIteration;
////                StudAuthController.maxDiscoveryIteration=0;
////                mAdapter.cancelDiscovery();
//                synchronized (this){
//                mmSocket.connect();}
//                StudAuthController.maxDiscoveryIteration=0;
//                mAdapter.cancelDiscovery();
////                notify();
//
////                mAdapter.cancelDiscovery();
////              mHandler.obtainMessage(Constants.UNREGISTER_RECEIVER, 0, 0, null)
////              .sendToTarget();
////                mHandler.obtainMessage(Constants.STUDENT_AUTHORIZED, 0, 0, null)
////                .sendToTarget();
//            } catch (IOException e) {
////            	notify();
////            	StudAuthController.maxDiscoveryIteration = temp;
////                if (tempUuid.toString().contentEquals(mUuids.get(6).toString())) {
////                    connectionFailed();
////                }
//                // Close the socket
////                try {
////                    mmSocket.close();
////                } catch (IOException e2) {
////                    Log.e(TAG, "unable to close() socket during connection failure", e2);
////                }
//                // Start the service over to restart listening mode
////                ClientBT.this.start();
//                return;
//            }
//
//            // Reset the ConnectThread because we're done
////            synchronized (BluetoothChatService.this) {
////                mConnectThread = null;
////            }
//
//            // Start the connected thread
//            connected(mmSocket, mmDevice);
//        }

        /**
 * Cancel.
 */
public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    class ConnectedThread extends Thread {
        
        /** The mm socket. */
        private final BluetoothSocket mmSocket;
        
        /** The mm in stream. */
        private final InputStream mmInStream;
		
		/** The mm out stream. */
		private final OutputStream mmOutStream;
		
		/** The course. */
		private String course;
		
        /**
         * Instantiates a new connected thread.
         *
         * @param socket the socket
         */
        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            String s = studentId.toString();
            byte [] msg;
            if(!StudAuthController.studentAuthorized)
            	msg = toByteArray(s+"-"+StudAuthController.PINcode);
            else
            	msg = toByteArray(s+"-"+StudAuthController.PINcode+"-"+Constants.RECONNECT);
            write(msg);
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
        
        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true&&mConnectedThread!=null) {
                try {
                    // Read from the InputStream
                	
//					while ((length = fis.read(buffer)) > 0) {
//						zos.write(buffer, 0, length);
//					}
                	
                    bytes = mmInStream.read(buffer);
                    
                    String receivedMessage = new String(buffer, 0, bytes);
                    
                    
                    if (receivedMessage.equals("You have not registered to this course")) {
                        // Send the obtained bytes to the UI Activity
                      mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                              .sendToTarget();
//                      cancel();
                      ClientBT.this.stop();
					}
                    else if (receivedMessage.startsWith("You have authorized")) {
                        // Send the obtained bytes to the UI Activity
                      String[] splited= receivedMessage.split("-");
                      StudAuthController.studentAuthorized = true;
                      mHandler.obtainMessage(Constants.STUDENT_AUTHORIZED, 0, 0, splited[1])
                      .sendToTarget();
					}
                    else if (receivedMessage.equals("The PIN code is not correct")) {
                        // Send the obtained bytes to the UI Activity
                      mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                              .sendToTarget();
//                      cancel();
                      ClientBT.this.stop();
					}
                    else if (receivedMessage.equals("This id is already connected")) {
                        // Send the obtained bytes to the UI Activity
                      mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                              .sendToTarget();
//                      cancel();
                      ClientBT.this.stop();
					}   
                    else if(receivedMessage.equals("Enable Quiz"))
                    {
                    	mHandler.obtainMessage(Constants.ENABLE_QUIZ, 0, 0, buffer)
                        .sendToTarget();
                    }
                    else if(receivedMessage.equals("Start Quiz"))
                    {
                        mHandler.obtainMessage(Constants.QUIZ_INITIATION, Integer.valueOf(quizPeriod),
                        		-1, quizPathToZip+"/"+studentId+".html").sendToTarget();
                    }
                    else
                    {
//                      StudAuthController.folderRecursiveDelete(new File(applicationPath+"/"));
//                    	receivedQuiz = true;
                    	String[] splited= receivedMessage.split("-");
                    	String quizName = splited[0];
                    	course = splited[1];
                    	quizPeriod = splited[2];
                    	String fileSize = splited[3];
                    	///////////////////////////
//                    	StudAuthController.folderRecursiveDelete(new File(applicationPath+"/"+course+"/"));
                    	
                    	///////////////////////////////////
                    	
                    	
                    	/////////////////////////////////
                    	byte[] readFile = new byte[Integer.valueOf(fileSize)];
//                    	String quizPath = applicationPath+"/"+course;
                    	String quizPath = applicationPath+"/"+course+"/Quizzes/"+ quizName + "/StudentsAnswers";
                    	pathToSend = "/"+course+"/Quizzes/"+ quizName + "/StudentsAnswers/";
                    	int byteStartIndex = String.valueOf(fileSize).length()+
                        course.length()+
                        quizName.length()+
                        quizPeriod.length()+
                        +4;
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
//                    	receivedQuiz = true;
                    	byte [] msg = toByteArray("Received Quiz");
                        write(msg);
                    	
                    	String zipFile = quizPath+quizName+".zip";

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
                    	new File(quizPath).mkdirs();
                    	while ((entry = zipStream.getNextEntry()) != null) {
                    		
                    	    String entryName = entry.getName();
                    	    if (entryName.endsWith(".html")) {
								entryName = studentId+".html";
							}
                    	    File file = new File(quizPath+"/"+entryName);
                    	    
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
                    	quizPathToZip = quizPath;
//      //                  mHandler.obtainMessage(Constants.QUIZ_INITIATION, Integer.valueOf(quizPeriod),
//      //                  		-1, quizPath+"/"+studentId+".html").sendToTarget();
                    	
//                    	new StudQuizActivity(mainActivity,Integer.valueOf(quizPeriod),quizPath+quizName+".html");
                    	
                    }

                } catch (IOException e) {
//                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

//        private void folderRecursiveDelete(File file) {
//            if (!file.exists())
//                return;
//            if (file.isDirectory()) {
//                for (File f : file.listFiles()) {
//                    folderRecursiveDelete(f);
//                }
//            }
//           try{
//               if(!file.getCanonicalPath().equals(applicationPath))
//                   file.delete();
//           }
//           catch (IOException e){
//               ;
//           }
//
//        }

//        public void unZipIt(String zipFile, String outputFolder){
//
//            byte[] buffer = new byte[1024];
//           	
//            try{
//           		
//           	//create output directory is not exists
//           	File folder = new File(outputFolder);
//           	if(!folder.exists()){
//           		folder.mkdir();
//           	}
//           		
//           	//get the zip file content
//           	ZipInputStream zis = 
//           		new ZipInputStream(new FileInputStream(zipFile));
//           	//get the zipped file list entry
//           	ZipEntry ze = zis.getNextEntry();
//           		
//           	while(ze!=null){
//           			
//           	   String fileName = ze.getName();
//                  File newFile = new File(outputFolder + File.separator + fileName);
//                       
//                  System.out.println("file unzip : "+ newFile.getAbsoluteFile());
//                       
//                   //create all non exists folders
//                   //else you will hit FileNotFoundException for compressed folder
//                   new File(newFile.getParent()).mkdirs();
//                     
//                   FileOutputStream fos = new FileOutputStream(newFile);             
//
//                   int len;
//                   while ((len = zis.read(buffer)) > 0) {
//              		fos.write(buffer, 0, len);
//                   }
//               		
//                   fos.close();   
//                   ze = zis.getNextEntry();
//           	}
//           	
//               zis.closeEntry();
//           	zis.close();
//           		
//           	System.out.println("Done");
//           		
//           }catch(IOException ex){
//              ex.printStackTrace(); 
//           }
//          }    
        
        
        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                if(StudQuizActivity.submited){
//                	StudAuthController.folderRecursiveDelete(new File(applicationPath+"/"+course+"/"));
                    mHandler.obtainMessage(Constants.STUDENT_SUBMITED, 0, 0, null)
                            .sendToTarget();
//                    StudQuizActivity.submited = false;
                }
//                if(StudAuthController.recovered)
//                	StudAuthController.folderRecursiveDelete(new File(applicationPath+"/"+course+"/"));

                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();
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
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
//    public ArrayList<UUID> getUuids() {
//		return mUuids;
//	}
}