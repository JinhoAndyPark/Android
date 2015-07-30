/*
 * Copyright (C) 2011 SmartRobot.ORG
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

package org.smartrobot.android;

import java.lang.ref.WeakReference;

import org.roboid.robot.Robot;
import org.smartrobot.android.ipc.dc;
import org.smartrobot.android.ipc.dr;
import org.smartrobot.android.ipc.rc;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

/**
 * <p>��ó�� �����ϰų� ���� �����ϴ� �޼ҵ带 �����Ѵ�.
 * <p>�κ��� �����ϴ� ����̽��� �����ʹ� {@link Callback#onActivated() onActivated()} �޼ҵ尡 ȣ��� ���ĺ��� {@link Callback#onDeactivated() onDeactivated()} �޼ҵ尡 ȣ��Ǳ� ������ �аų� �� �� �ִ�.
 * </p>
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public final class SmartRobot
{
	private static final int STATE_NONE = 0;
	private static final int STATE_QUIT = 5;
	
	private static final int MSG_ACK = 1;
	private static final int MSG_STATE = 2;
	private static final int MSG_CHANGE_ROBOT = 3;
	private static final int MSG_CHANGE_NAME = 4;
	
	private WeakReference<Context> mContext;
	Callback mCallback;
	private String mCurrentId;
	AbstractRobot mCurrentRobot;
	private PhysicalRoboid mCurrentRoboid;
	private boolean mActive;
	
	private rc mRobotControllerBinder;
	private final ServiceConnection mRobotControllerConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			mRobotControllerBinder = rc.Stub.asInterface(binder);
			try
			{
				mRobotControllerBinder.a(mSensoryDataChangedCallback);
				mRobotControllerBinder.c(mMotoringDataChangedCallback);
				mRobotControllerBinder.e(mMotoringDataRequestCallback);
			} catch (RemoteException e)
			{
			}
			
			if(mCallback != null)
				mCallback.onActivated();
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mRobotControllerBinder = null;
		}
	};
	private final dc.Stub mSensoryDataChangedCallback = new dc.Stub()
	{
		@Override
		public void a(byte[] a1, long a2) throws RemoteException
		{
			if(a1 != null && mCurrentRoboid != null)
				mCurrentRoboid.handleSensorySimulacrum(a1, a2);
		}

		@Override
		public void b(int b1, int b2, byte[] b3, long b4) throws RemoteException
		{
			if(b3 != null && mCurrentRoboid != null)
			{
				PeripheralRoboid peripheral = mCurrentRoboid.findPeripheral(b1, b2);
				if(peripheral == null)
				{
					peripheral = PeripheralFactory.create(b1, b2);
					if(peripheral == null) return;
					mCurrentRoboid.addPeripheral(peripheral);
				}
				if(peripheral != null)
					peripheral.handleSensorySimulacrum(b3, b4);
			}
		}
	};
	private final dc.Stub mMotoringDataChangedCallback = new dc.Stub()
	{
		@Override
		public void a(byte[] a1, long a2) throws RemoteException
		{
			if(a1 != null && mCurrentRoboid != null)
				mCurrentRoboid.handleMotoringSimulacrum(a1, a2);
			if(mCurrentRobot != null)
				mCurrentRobot.updateDeviceState();
			if(mCallback != null)
				mCallback.onExecute();
		}

		@Override
		public void b(int b1, int b2, byte[] b3, long b4) throws RemoteException
		{
		}
	};
	private final dr.Stub mMotoringDataRequestCallback = new dr.Stub()
	{
		@Override
		public byte[] a() throws RemoteException
		{
			if(mRobotControllerBinder == null) return null;
			if(mCurrentRoboid == null) return null;
			return mCurrentRoboid.encodeMotoringSimulacrum();
		}

		@Override
		public byte[] b(int b1, int b2) throws RemoteException
		{
			return null;
		}
	};
	private BroadcastReceiver mBR;
	private final EventHandler mEventHandler;
	private static SmartRobot INSTANCE = new SmartRobot();
	
	/**
	 * <p>�κ��� �����ֱ�� �̺�Ʈ�� ���� ȣ��Ǵ� �޼ҵ带 �����Ѵ�.
	 * <p>�κ��� �����ϴ� ����̽��� �����ʹ� {@link #onActivated()} �޼ҵ尡 ȣ��� ���ĺ��� {@link #onDeactivated()} �޼ҵ尡 ȣ��Ǳ� ������ �аų� �� �� �ִ�.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see SmartRobot
	 */
	public interface Callback
	{
		/**
		 * <p>{@link SmartRobot#activate(Context, Callback) SmartRobot.activate(Context, SmartRobot.Callback)} �޼ҵ带 ȣ���� �Ŀ� {@link org.roboid.robot.Robot Robot} �ν��Ͻ��� �����ǰų� {@link org.roboid.robot.Robot Robot} �ν��Ͻ��� ����Ǿ��� �� ȣ��ȴ�.
		 * <p>��, �κ��� �ν��Ͻ��� ���� �����Ǿ��� ��, ��ó���� �κ��� �����Ͽ� �ٸ� �κ� �ν��Ͻ��� ����Ǿ��� �� ȣ��ȴ�.
		 * </p>
		 * @param robot �κ��� �ν��Ͻ�
		 */
		void onInitialized(Robot robot);
		/**
		 * <p>{@link #onInitialized(Robot)} �޼ҵ尡 ȣ��� �Ŀ� ���ø����̼��� ��ó�� ����Ǿ��� �� ȣ��ȴ�.
		 * <p>onActivated() �޼ҵ尡 ȣ��� ���ĺ��� {@link #onDeactivated()} �޼ҵ尡 ȣ��Ǳ� ������ ����̽��� �����͸� �аų� �� �� ������, �� 20ms���� �ֱ������� {@link #onExecute()} �޼ҵ尡 ȣ��ȴ�.
		 */
		void onActivated();
		/**
		 * <p>{@link SmartRobot#deactivate()} �޼ҵ带 ȣ���� �� �Ǵ� ��ó�� ����Ǿ�, ���ø����̼��� ��ó���� ���� �����Ǿ��� �� ȣ��ȴ�.
		 * <p>onDeactivated() �޼ҵ尡 ȣ��� ���Ŀ��� ����̽��� �����͸� �аų� �� �� ����.
		 */
		void onDeactivated();
		/**
		 * <p>��ó�� ����Ǿ��� �� ȣ��ȴ�.
		 * <p>��ó�� ����� �� ���ø����̼ǵ� ���� ����Ǿ�� �ϹǷ� onDisposed() �޼ҵ忡�� ��Ƽ��Ƽ�� �ݰ� ���ҽ��� �����ϴ� ���� ���� �۾��� �ؾ� �Ѵ�.
		 */
		void onDisposed();
		/**
		 * <p>{@link #onActivated()} �޼ҵ尡 ȣ��� ���ĺ��� {@link #onDeactivated()} �޼ҵ尡 ȣ��Ǳ� ������ �� 20ms���� �ֱ������� ȣ��ȴ�.
		 * <p>onExecute() �޼ҵ忡���� �׻� ����̽��� �����͸� �аų� �� �� �ִ�.
		 */
		void onExecute();
		/**
		 * <p>�ϵ���� �κ��� ������� ���� ���°� ����Ǿ��� �� ȣ��ȴ�.
		 * <p>state�� ���� �� �ִ� ���� {@link org.roboid.robot.Robot Robot} �������̽��� ���ǵǾ� �ִ�.
		 * </p>
		 * @param state ������� ���� ����
		 * <p>
		 * @see org.roboid.robot.Robot#STATE_CONNECTING Robot.STATE_CONNECTING
		 * @see org.roboid.robot.Robot#STATE_CONNECTED Robot.STATE_CONNECTED
		 * @see org.roboid.robot.Robot#STATE_CONNECTION_LOST Robot.STATE_CONNECTION_LOST
		 * @see org.roboid.robot.Robot#STATE_DISCONNECTED Robot.STATE_DISCONNECTED
		 */
		void onStateChanged(int state);
		/**
		 * <p>��ó���� ���� ����� �κ��� �̸��� �������� �� ȣ��ȴ�.
		 * </p>
		 * @param name ����� �κ��� �̸�
		 */
		void onNameChanged(String name);
	}
	
	private SmartRobot()
	{
		Looper looper;
		if((looper = Looper.myLooper()) != null)
		{
			mEventHandler = new EventHandler(looper);
		}
		else if((looper = Looper.getMainLooper()) != null)
		{
			mEventHandler = new EventHandler(looper);
		}
		else
			mEventHandler = new EventHandler();
	}

	/**
	 * <p>��ó�� �����Ѵ�.
	 * <p>context�� callback�� null�� �ƴϾ�� �Ѵ�.
	 * </p>
	 * @param context ���ؽ�Ʈ
	 * @param callback �κ��� �����ֱ�� �̺�Ʈ�� ���� ȣ��Ǵ� �ݹ�
	 * <p>
	 * @return �����ϸ� true, �ƴϸ� false
	 */
	public static boolean activate(Context context, Callback callback)
	{
		if(context == null || callback == null) return false;
		INSTANCE.setContext(context);
		INSTANCE.setCallback(callback);
		return INSTANCE.doActivate();
	}
	
	/**
	 * <p>��ó���� ������ �����Ѵ�.
	 * </p>
	 */
	public static void deactivate()
	{
		INSTANCE.doDeactivate();
	}
	
	Context getContext()
	{
		if(mContext == null) return null;
		return mContext.get();
	}
	
	private void setContext(Context context)
	{
		Context applicationContext = context.getApplicationContext();
		if(applicationContext == null)
			mContext = new WeakReference<Context>(context);
		else
			mContext = new WeakReference<Context>(applicationContext);
	}
	
	private void setCallback(Callback callback)
	{
		mCallback = callback;
	}
	
	private boolean doActivate()
	{
		if(mActive) return true;
		Context context = getContext();
		if(context == null) return false;
		mActive = true;
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.ROBOT_REQ");
		intent.putExtra("roboid.intent.extra.PACKAGE_NAME", context.getPackageName());
		context.sendBroadcast(intent);
		return true;
	}
	
	private void registerBroadcast(Context context)
	{
		if(mBR != null) return;
		mBR = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				String action = intent.getAction();
				if("roboid.intent.action.ROBOT_ACK".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_ACK);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ROBOT_STATE".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_STATE);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ROBOT_CHANGE".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_CHANGE_ROBOT);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ROBOT_CHANGE_NAME".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_CHANGE_NAME);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("roboid.intent.action.ROBOT_ACK");
		intentFilter.addAction("roboid.intent.action.ROBOT_STATE");
		intentFilter.addAction("roboid.intent.action.ROBOT_CHANGE");
		intentFilter.addAction("roboid.intent.action.ROBOT_CHANGE_NAME");
		context.registerReceiver(mBR, intentFilter);
	}
	
	private void doDeactivate()
	{
		if(mActive == false) return;
		mActive = false;
		
		if(mEventHandler != null)
		{
			mEventHandler.removeMessages(MSG_ACK);
			mEventHandler.removeMessages(MSG_STATE);
			mEventHandler.removeMessages(MSG_CHANGE_ROBOT);
			mEventHandler.removeMessages(MSG_CHANGE_NAME);
		}
		disconnect();
		
		if(mBR != null)
		{
			Context context = getContext();
			if(context != null)
				context.unregisterReceiver(mBR);
			mBR = null;
		}
		mCurrentRobot = null;
		mCurrentRoboid = null;
		mCurrentId = null;
		mCallback = null;
	}
	
	boolean connect(String id, String name)
	{
		Context context = getContext();
		if(context == null) return false;
		if(id == null) return false;
		
		if(!id.equals(mCurrentId))
		{
			if(mCurrentRobot != null)
				mCurrentRobot.clearDeviceDataChangedListener();
			
			mCurrentRobot = RobotFactory.create(id);
			if(mCurrentRobot == null)
			{
				mCurrentId = null;
				mCurrentRoboid = null;
				return false;
			}
			else
			{
				mCurrentRoboid = mCurrentRobot.mRoboid;
				mCurrentRobot.setName(name);
			}
		}
		if(mCurrentRobot == null || mCurrentRoboid == null) return false;
		
		Intent intent = new Intent("roboid.intent.action.ROBOT");
		intent.addCategory(id);
		intent.putExtra("roboid.intent.extra.PACKAGE_NAME", context.getPackageName());
		try
		{
			context.bindService(intent, mRobotControllerConnection, Context.BIND_AUTO_CREATE);
		} catch (SecurityException e)
		{
			return false;
		}

		if(mCallback != null)
			mCallback.onInitialized(mCurrentRobot);
		mCurrentId = id;
		
		return true;
	}
	
	void disconnect()
	{
		try
		{
			if(mRobotControllerBinder != null)
			{
				mRobotControllerBinder.b(mSensoryDataChangedCallback);
				mRobotControllerBinder.d(mMotoringDataChangedCallback);
				mRobotControllerBinder.f(mMotoringDataRequestCallback);
				mRobotControllerBinder = null;
			}
			Context context = getContext();
			if(context != null)
				context.unbindService(mRobotControllerConnection);
		} catch (Exception e)
		{
		}
		
		if(mCallback != null)
			mCallback.onDeactivated();
	}
	
	private class EventHandler extends Handler
	{
		EventHandler()
		{
			super();
		}
		
		EventHandler(Looper looper)
		{
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case MSG_ACK:
				{
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					Context context = getContext();
					if(context == null) return;
					
					String packageName = intent.getStringExtra("roboid.intent.extra.PACKAGE_NAME");
					if(packageName == null || !packageName.equals(context.getPackageName())) return;
					
					String id = intent.getStringExtra("roboid.intent.extra.ROBOT_ID");
					String name = intent.getStringExtra("roboid.intent.extra.ROBOT_NAME");
					connect(id, name);
				}
				break;
			case MSG_STATE:
				{
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					int state = intent.getIntExtra("roboid.intent.extra.ROBOT_STATE", STATE_NONE);
					switch(state)
					{
					case STATE_NONE:
						break;
					case STATE_QUIT:
						{
							if(mCurrentRobot != null)
								mCurrentRobot.clearDeviceDataChangedListener();
							
							disconnect();
							SmartRobot.Callback callback = mCallback;
							if(callback != null)
								callback.onDisposed();
						}
						break;
					default:
						{
							SmartRobot.Callback callback = mCallback;
							if(callback != null)
								callback.onStateChanged(state);
						}
						break;
					}
				}
				break;
			case MSG_CHANGE_ROBOT:
				{
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String id = intent.getStringExtra("roboid.intent.extra.ROBOT_ID");
					String name = intent.getStringExtra("roboid.intent.extra.ROBOT_NAME");
					disconnect();
					connect(id, name);
				}
				break;
			case MSG_CHANGE_NAME:
				{
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String id = intent.getStringExtra("roboid.intent.extra.ROBOT_ID");
					String name = intent.getStringExtra("roboid.intent.extra.ROBOT_NAME");
					if(id != null && id.equals(mCurrentId))
					{
						if(mCurrentRobot != null)
							mCurrentRobot.setName(name);
						
						SmartRobot.Callback callback = mCallback;
						if(callback != null)
							callback.onNameChanged(name);
					}
				}
				break;
			}
		}
	}
}