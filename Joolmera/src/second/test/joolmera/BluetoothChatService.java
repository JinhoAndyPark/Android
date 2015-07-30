/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package second.test.joolmera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;





/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothChatService {
	// Debugging
	private static final String TAG = "BluetoothChatService";
	private static final boolean D = true;

	// Name for the SDP record when creating server socket
	private static final String NAME = "BluetoothChatSecure";


	// Unique UUID for this application
	private static final UUID MY_UUID =
			UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


	// Member fields
	private final BluetoothAdapter mAdapter;
	private Handler mHandler;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;


	/**
	 * 듈
	 */
	private int imageByteLen = 0;
	private ByteArrayOutputStream ReceiveImage;



	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * @param context  The UI Activity Context
	 * @param handler  A Handler to send messages back to the UI Activity
	 */
	public BluetoothChatService() {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = DataConstants.STATE_NONE;
		ReceiveImage = new ByteArrayOutputStream();
	}

	public BluetoothChatService(Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = DataConstants.STATE_NONE;
		mHandler = handler;
	}

	public void setHandler(Handler handler){
		mHandler = handler;
	}   


	/**
	 * Set the current state of the chat connection
	 * @param state  An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(DataConstants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
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

		setState(DataConstants.STATE_LISTEN);

		// Start the thread to listen on a BluetoothServerSocket
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * @param device  The BluetoothDevice to connect
	 * @param secure Socket Security type - Secure (true) , Insecure (false)
	 */
	public synchronized void connect(BluetoothDevice device) {
		if (D) Log.d(TAG, "connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == DataConstants.STATE_CONNECTING) {
			if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(DataConstants.STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * @param socket  The BluetoothSocket on which the connection was made
	 * @param device  The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		if (D) Log.d(TAG, "connecte");

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		// Cancel the accept thread because we only want to connect to one device
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler.obtainMessage(DataConstants.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(DataConstants.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(DataConstants.STATE_CONNECTED);
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (D) Log.d(TAG, "stop");

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}


		setState(DataConstants.STATE_NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * @param out The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != DataConstants.STATE_CONNECTED) return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized

		r.write(out);
	}
	//	
	//	public void write(byte[] out1, byte[] out2, byte[] out3) {
	//		// Create temporary object
	//		ConnectedThread r;
	//		// Synchronize a copy of the ConnectedThread
	//		synchronized (this) {
	//			if (mState != DataConstants.STATE_CONNECTED) return;
	//			r = mConnectedThread;
	//		}
	//		// Perform the write unsynchronized
	//		DataConstants.showData(out,"show data write");
	//		r.write(out);
	//	}



	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(DataConstants.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(DataConstants.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();

	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(DataConstants.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(DataConstants.TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted
	 * (or until cancelled).
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;



			// Create a new listening server socket
			try {

				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME,MY_UUID);

			} catch (IOException e) {
				Log.e(TAG,"listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
			setName("AcceptThread");

			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mState != DataConstants.STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG,"accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BluetoothChatService.this) {
						switch (mState) {
						case DataConstants.STATE_LISTEN:
						case DataConstants.STATE_CONNECTING:
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice());
							// 듈
							//							DataConstants.address = socket.getRemoteDevice().getAddress();
							break;
						case DataConstants.STATE_NONE:
						case DataConstants.STATE_CONNECTED:
							// Either not ready or already connected. Terminate new socket.
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Could not close unwanted socket", e);
							}
							break;
						}
					}
				}
			}
			if (D) Log.i(TAG, "END mAcceptThread");

		}

		public void cancel() {
			if (D) Log.d(TAG, "cancel " + this);
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG,"close() of server failed", e);
			}
		}
	}


	/**
	 * This thread runs while attempting to make an outgoing connection
	 * with a device. It runs straight through; the connection either
	 * succeeds or fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);

			} catch (IOException e) {
				Log.e(TAG , "create() failed", e);
			}
			mmSocket = tmp;
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
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "unable to close() "  +
							" socket during connection failure", e2);
				}
				connectionFailed();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothChatService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect "  + " socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device.
	 * It handles all incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread: ");
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
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// 수정하기 듈
					byte[] buffer = new byte[1024];
					int bytes;

					// Read from the InputStream					
					bytes = mmInStream.read(buffer);

					
					// 스트리밍
					if(bytes > 2 && buffer[0]==8 && buffer[1]==28 && buffer[2]==16){
						
						/**
						 * 
						 */
						
//						Log.d("test","스트리밍ㅇㅇㅇㅇ");

						if(buffer[3] == 1){
							ReceiveImage.reset();
						}

						imageByteLen = DataConstants.byteArrayToInt(buffer, 4);

						if(buffer[3]<=imageByteLen/900){
							ReceiveImage.write(buffer,8, 900);		
							//							DataConstants.showData(ReceiveImage.toByteArray(), "show data read");
						}
						else{
							ReceiveImage.write(buffer,8,imageByteLen%900);	
							//							DataConstants.showData(ReceiveImage.toByteArray(), "show data read");
						}

						if(ReceiveImage.toByteArray().length >= imageByteLen){
							//						imageTransfer = false;
//							Log.d("read","image transfer End!!!");
//							Log.d("read","image size! = "+ReceiveImage.size());

							byte[] getImage = ReceiveImage.toByteArray();
//							Log.d("read","합쳤땅! ");

							//							DataConstants.showData(getImage,"show data :read");

							Bitmap capturedImage = BitmapFactory.decodeByteArray(getImage, 0, imageByteLen);

							//							imgModeEasyController.setImageBitmap(BitmapFactory.decodeByteArray(getImage, 0, getImage.length));

							ReceiveImage.reset();
							mHandler.obtainMessage(DataConstants.MESSAGE_STREAM, imageByteLen, -1, capturedImage)
							.sendToTarget();

						}else{
//							Log.d("read","아직 안합쳤땅 "+buffer[3]+"번 째 조각이다");
						}

					}

					
					// 사진전송
					if(bytes > 2 && buffer[0]==6 && buffer[1]==26 && buffer[2]==18){
						
						Log.d("test", "사진전송");

						if(buffer[3] == 1){
							ReceiveImage.reset();
//							Log.d("read", buffer[3]+"번째 조각이라 스트림 비우고 시작! ");
						}

						imageByteLen = DataConstants.byteArrayToInt(buffer, 4);
//						Log.d("read", "받아올 이미지의 총 크기 = " + imageByteLen);

//						Log.d("read","이미지 받는중이당! 지금은 "+buffer[3]+"번째 조각이당");

						if(buffer[3]<=imageByteLen/900){
							ReceiveImage.write(buffer,8, 900);		
//							Log.d("read", "1. 현재까지 받은 크기 = "+ReceiveImage.toByteArray().length);
							//							DataConstants.showData(ReceiveImage.toByteArray(), "show data read");
						}
						else{
							ReceiveImage.write(buffer,8,imageByteLen%900);	
//							Log.d("read", "2. 현재까지 받은 크기 = "+ReceiveImage.toByteArray().length);
							//							DataConstants.showData(ReceiveImage.toByteArray(), "show data read");
						}

						if(ReceiveImage.toByteArray().length >= imageByteLen){
							//						imageTransfer = false;
//							Log.d("read","image transfer End!!!");
//							Log.d("read","image size! = "+ReceiveImage.size());

							byte[] getImage = ReceiveImage.toByteArray();
//							Log.d("read","합쳤땅! ");

							//							DataConstants.showData(getImage,"show data :read");

							Bitmap capturedImage = BitmapFactory.decodeByteArray(getImage, 0, imageByteLen);

							//							imgModeEasyController.setImageBitmap(BitmapFactory.decodeByteArray(getImage, 0, getImage.length));

							ReceiveImage.reset();
							mHandler.obtainMessage(DataConstants.MESSAGE_IMAGE, imageByteLen, -1, capturedImage)
							.sendToTarget();

						}else{
//							Log.d("read","아직 안합쳤땅");
						}

					}else{
//						Log.d("read", "다른 메세지당");
						//						DataConstants.showData(buffer, "show data read");
					}

					// Send the obtained bytes to the UI Activity
					mHandler.obtainMessage(DataConstants.MESSAGE_READ, bytes, -1, buffer)
					.sendToTarget();
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					// Start the service over to restart listening mode
					BluetoothChatService.this.start();
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 * @param buffer  The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);

				//				DataConstants.showData(buffer,"show data write");
				// Share the sent message back to the UI Activity
				mHandler.obtainMessage(DataConstants.MESSAGE_WRITE, -1, -1, buffer)
				.sendToTarget();
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
