/**
*
* @author      Manuel Di Cerbo
* @copyright   Copyright (c) 2013, Manuel Di Cerbo, Nexus-Computing GmbH
*
* This file is part of SimpleAccessory.
*
* SimpleAccessory is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License version 2, as published by the
* Free Software Foundation.
*
* You may NOT assume that you can use any other version of the GPL.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to:
*
*      Free Software Foundation, Inc.
*      51 Franklin St, Fifth Floor
*      Boston, MA  02110-1301  USA
*
* The license for this software can also likely be found here:
* http://www.gnu.org/licenses/gpl-2.0.html
*/

package com.example.displaychoice;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

public class AccessoryEngine {

	private static final int BUFFER_SIZE = 1024;
	private final Context mContext;
	private final UsbManager mUsbManager;
	private final IEngineCallback mCallback;

	private final static String ACTION_ACCESSORY_DETACHED = "android.hardware.usb.action.USB_ACCESSORY_DETACHED";
	private static final String ACTION_USB_PERMISSION = "ch.nexuscomputing.simpleaccessory.USB_PERMISSION";
	private volatile boolean mAccessoryConnected = false;
	private final AtomicBoolean mQuit = new AtomicBoolean(false);

	private UsbAccessory mAccessory = null;

	private ParcelFileDescriptor mParcelFileDescriptor = null;
	private FileDescriptor mFileDescriptor = null;
	private FileInputStream mInputStream = null;
	private FileOutputStream mOutputStream = null;
	
	//int test = 0;

	public interface IEngineCallback {
		void onConnectionEstablished();

		void onDeviceDisconnected(); //분리될 때

		void onConnectionClosed();

		void onDataRecieved(byte[] data, int num);
	}

	public AccessoryEngine(Context applicationContext, IEngineCallback callback) {
		mContext = applicationContext;
		mCallback = callback;
		mUsbManager = (UsbManager) mContext
				.getSystemService(Context.USB_SERVICE);
		mContext.registerReceiver(mDetachedReceiver, new IntentFilter(
				ACTION_ACCESSORY_DETACHED));
	}

	public void onNewIntent(Intent intent) {
		if (mUsbManager.getAccessoryList() != null) {
			//Toast.makeText(mContext, "리스트있음", Toast.LENGTH_SHORT).show();
			mAccessory = mUsbManager.getAccessoryList()[0];
			connect();
		}
		else
		{
			Toast.makeText(mContext, "외부 LCD를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
			//Toast.makeText(mContext, "리스트없음", Toast.LENGTH_SHORT).show();
		}
	}

	private void connect() {
		if (mAccessory != null)
		{
			if(!mUsbManager.hasPermission(mAccessory)){
				//Toast.makeText(mContext, "퍼미션 없음", Toast.LENGTH_SHORT).show();
				mContext.registerReceiver(mPermissionReceiver, new IntentFilter(ACTION_USB_PERMISSION));
				PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
				mUsbManager.requestPermission(mAccessory, pi);
				return;
			}
			if (sAccessoryThread == null) {
				//Toast.makeText(mContext, "스레드 널", Toast.LENGTH_SHORT).show();
				sAccessoryThread = new Thread(mAccessoryRunnable, "Reader Thread");
				sAccessoryThread.start();
			}
			else
			{
				//Toast.makeText(mContext, "스레드 이미 시작", Toast.LENGTH_SHORT).show();
				//L.d("reader thread already started");
			}
		}
		else
		{
			//L.d("accessory is null");
		}
	}

	public void onDestroy() {
		// closeConnection();
		mQuit.set(true);
		mContext.unregisterReceiver(mDetachedReceiver);
		//Toast.makeText(mContext, "종료 test = " + test, Toast.LENGTH_SHORT).show();
	}

	private final BroadcastReceiver mDetachedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null
					&& ACTION_ACCESSORY_DETACHED.equals(intent.getAction())) {
				mCallback.onDeviceDisconnected();
			}
		}
	};
	
	private final BroadcastReceiver mPermissionReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			mContext.unregisterReceiver(mPermissionReceiver);
			 if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
				 connect();
			 }
		}
	};

	public void write(byte[] data) {
		
		//Toast.makeText(mContext, "test = " + test, Toast.LENGTH_SHORT).show();
		
		if (mAccessoryConnected && mOutputStream != null)
		{
			try {
				mOutputStream.write(data);
				//Toast.makeText(mContext, "write", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				//L.e("could not send data");
			}
		}
		else
		{
			Toast.makeText(mContext, "외부 LCD를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
		}
		
		if(mAccessoryConnected == false)
		{
			//Toast.makeText(mContext, "mAccessoryConnected is false", Toast.LENGTH_SHORT).show();
		}
		if(mOutputStream == null)
		{
			//Toast.makeText(mContext, "mOutputStream is null", Toast.LENGTH_SHORT).show();
		}
		/*else
		{
			Toast.makeText(mContext, "accessory not connected", Toast.LENGTH_SHORT).show();
			//L.d("accessory not connected");
		}*/
	}

	private static Thread sAccessoryThread;
	private final Runnable mAccessoryRunnable = new Runnable() {
		@Override
		public void run() {
			//L.d("open connection");
			//test = 1;
			mParcelFileDescriptor = mUsbManager.openAccessory(mAccessory);
			//test = 2;
			if (mParcelFileDescriptor == null) {
				//test = 3;
				//L.e("could not open accessory");
				mCallback.onConnectionClosed();
				return;
			}
			//test = 4;
			mFileDescriptor = mParcelFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(mFileDescriptor);
			mOutputStream = new FileOutputStream(mFileDescriptor);
			mCallback.onConnectionEstablished();
			mAccessoryConnected = true;
			
			//test = 5;

			byte[] buf = new byte[BUFFER_SIZE];
			while (!mQuit.get()) {
				try {
					int read = mInputStream.read(buf);
					mCallback.onDataRecieved(buf, read);
				} catch (Exception e) {
					//L.e("ex " + e.getMessage());
					break;
				}
			}
			//L.d("exiting reader thread");
			mCallback.onConnectionClosed();

			if (mParcelFileDescriptor != null) {
				try {
					mParcelFileDescriptor.close();
				} catch (IOException e) {
					//L.e("Unable to close ParcelFD");
				}
			}

			if (mInputStream != null) {
				try {
					mInputStream.close();
				} catch (IOException e) {
					//L.e("Unable to close InputStream");
				}
			}

			if (mOutputStream != null) {
				try {
					mOutputStream.close();
				} catch (IOException e) {
					//L.e("Unable to close OutputStream");
				}
			}

			mAccessoryConnected = false;
			mQuit.set(false);
			sAccessoryThread = null;
			//test = 9;
		}
	};
}