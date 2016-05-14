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

public class ClientBT {
    // Debugging
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothChatMulti";

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
//    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    public static ConnectedThread mConnectedThread;
    public static String quizPathToZip;
    private int mState;

    private ArrayList<String> mDeviceAddresses;
    private ArrayList<ConnectedThread> mConnThreads;
    private ArrayList<BluetoothSocket> mSockets;
    /**
     * A bluetooth piconet can support up to 7 connections. This array holds 7 unique UUIDs.
     * When attempting to make a connection, the UUID on the client must match one that the server
     * is listening for. When accepting incoming connections server listens for all 7 UUIDs.
     * When trying to form an outgoing connection, the client tries each UUID one at a time.
     */
    private ArrayList<UUID> mUuids;



	private boolean found=false;
    private int stage=1;
	private CharSequence studentId;
	private String applicationPath;
	private MainActivity mainActivity;
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * Constructor. Prepares a new BluetoothChat session.
//     * @param context  The UI Activity Context
     * @param mHandler 
     * @param mainActivity 
//     * @param handler  A Handler to send messages back to the UI Activity
     */
    public ClientBT(CharSequence studentId, Handler mHandler,MainActivity mainActivity, String applicationPath) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        this.studentId = studentId;
        this.mHandler = mHandler;
        this.mainActivity = mainActivity;
        this.applicationPath = applicationPath;
        mDeviceAddresses = new ArrayList<String>();
        mConnThreads = new ArrayList<ConnectedThread>();
        mSockets = new ArrayList<BluetoothSocket>();
        mUuids = new ArrayList<UUID>();
        // 7 randomly-generated UUIDs. These must match on both server and client.
        mUuids.add(UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-c758-4868-aa19-7ac6b3475dfc"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-5a2c-4511-a074-77f199fd0834"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-51f3-4a7b-91cb-f638491d1412"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-4536-49ee-a475-7d96d09439e4"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-d8ad-448e-abdb-95ebba4a9b55"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-d0a4-4f40-ac38-917e0a9dee97"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-9c8a-4db7-81e4-c937564c86e0"));
//        
//    
//        mUuids.add(UUID.fromString("0000"+pincode+"-1d84-48fc-bb18-c1c7c014af68"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-6854-4964-8fd7-0a1b7b71e807"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-0cd9-4005-a818-b39504ea2a68"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-8fcb-4c1d-a295-e53efdfce330"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-9004-4792-a4ab-1b87a8e182c7"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-ace9-4489-aa11-68220bcf23b0"));
//        mUuids.add(UUID.fromString("0000"+pincode+"-33c1-41e8-a0d8-371c70e67a93"));
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
//        mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
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

        // Create a new thread and attempt to connect to each UUID one-by-one.
        int i;
        int j = 0;
        if (stage==1) {
			i=0;
//			j=7;
			j=1;
		}
        else if (stage==2) {
			i=7;
			j=14;
		}
        for (i = 0; i < j; i++) {
            try {
            	if(found==false)
            	{
					mConnectThread = new ConnectThread(device, mUuids.get(i));
					mConnectThread.start();
					setState(STATE_CONNECTING);
				}
            	else
            		break;
            } catch (Exception e) {
            }
        }
        
//        if(stage==1&&found==true)
//        {
//        	stop();
//        	stage=2;
//        	found=false;
//        	connect(device);
//        }
//        else if (stage==1&&found==false) {
//        	connect(device);
//		}
//        
        
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
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
     * Stop all threads
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
        setState(STATE_LISTEN);

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
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private UUID tempUuid;

        public ConnectThread(BluetoothDevice device, UUID uuidToTry) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            tempUuid = uuidToTry;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(uuidToTry);
                mAdapter.cancelDiscovery();//
                StudLoginController.maxDiscoveryIteration=0;
                mmSocket = tmp;//
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
//            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
//                if (tempUuid.toString().contentEquals(mUuids.get(6).toString())) {
//                    connectionFailed();
//                }
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // Start the service over to restart listening mode
//                ClientBT.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
//            synchronized (BluetoothChatService.this) {
//                mConnectThread = null;
//            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

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
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
		private final OutputStream mmOutStream;

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
            byte [] msg = toByteArray(studentId);
            write(msg);
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
                    else if (receivedMessage.equals("This id is already connected")) {
                        // Send the obtained bytes to the UI Activity
                      mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                              .sendToTarget();
//                      cancel();
                      ClientBT.this.stop();
					} 
                    else
                    {
                        folderRecursiveDelete(new File(applicationPath+"/"));
                    	String[] splited= receivedMessage.split("-");
                    	String quizName = splited[0];
                    	String course = splited[1];
                    	String quizPeriod = splited[2];
                    	String fileSize = splited[3];
                    	byte[] readFile = new byte[Integer.valueOf(fileSize)];
                    	String quizPath = applicationPath+"/"+course;
//                    			+"/Quizzes/"+
//                    			quizName + "/Form/";
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
                    	new File(quizPath).mkdir();
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
                    	
                    	File file = new File(zipFile);
                    	if(file.exists())
                    	{
                    		;
                    	}
//                    	zipFileManager.unZipIt(zipFile, quizPath);
                    	quizPathToZip = quizPath;
                        mHandler.obtainMessage(Constants.QUIZ_INITIATION, Integer.valueOf(quizPeriod),
                        		-1, quizPath+"/"+studentId+".html").sendToTarget();
                    	
//                    	new StudQuizActivity(mainActivity,Integer.valueOf(quizPeriod),quizPath+quizName+".html");
                    	
                    }

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        private void folderRecursiveDelete(File file) {
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

        public void unZipIt(String zipFile, String outputFolder){

            byte[] buffer = new byte[1024];
           	
            try{
           		
           	//create output directory is not exists
           	File folder = new File(outputFolder);
           	if(!folder.exists()){
           		folder.mkdir();
           	}
           		
           	//get the zip file content
           	ZipInputStream zis = 
           		new ZipInputStream(new FileInputStream(zipFile));
           	//get the zipped file list entry
           	ZipEntry ze = zis.getNextEntry();
           		
           	while(ze!=null){
           			
           	   String fileName = ze.getName();
                  File newFile = new File(outputFolder + File.separator + fileName);
                       
                  System.out.println("file unzip : "+ newFile.getAbsoluteFile());
                       
                   //create all non exists folders
                   //else you will hit FileNotFoundException for compressed folder
                   new File(newFile.getParent()).mkdirs();
                     
                   FileOutputStream fos = new FileOutputStream(newFile);             

                   int len;
                   while ((len = zis.read(buffer)) > 0) {
              		fos.write(buffer, 0, len);
                   }
               		
                   fos.close();   
                   ze = zis.getNextEntry();
           	}
           	
               zis.closeEntry();
           	zis.close();
           		
           	System.out.println("Done");
           		
           }catch(IOException ex){
              ex.printStackTrace(); 
           }
          }    
        
        
        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();
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
//    public ArrayList<UUID> getUuids() {
//		return mUuids;
//	}
}