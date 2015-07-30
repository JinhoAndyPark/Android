package com.example.displaychoice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.displaychoice.AccessoryEngine.IEngineCallback;

public class FramebufferSend extends Service{
	
	private AccessoryEngine mEngine = null;
	byte[] buf = new byte[153600];
	//byte[] array = new byte[240*320*2];
	
	private MyThread DDThread = null;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//sendBroadcast(new Intent("com.example.displaychoice.stateON")); //QuickSetting 상태 표시를 위한 브로드캐스트
		//Toast.makeText(getApplicationContext(), "서비스 시작", Toast.LENGTH_SHORT).show();
	//	stringFromJNI(buf);
//		
//		for(int i=0; i<10; i++)
//		{
//			Toast.makeText(getApplicationContext(), i + " : " + buf[i], Toast.LENGTH_SHORT).show();
//		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
	
		//Toast.makeText(getApplicationContext(), "여기1", Toast.LENGTH_SHORT).show();
		onNewIntent(intent);
		
		if (mEngine != null)
		{
			
			if(DDThread == null)
			{
				DDThread = new MyThread();
				DDThread.start();
			}
		//	stringFromJNI(buf);
			//byte[] buf = new byte[153600];
			
			//Toast.makeText(getApplicationContext(), "시작", Toast.LENGTH_SHORT).show();
//			for(int j=0; j<30; j++)
//			{
//				if(flag == 0 && 10<=j && j<20)
//					
//				{
//					flag = 1;
//					
//					for(int i=0; i<153600; i++)
//					{
//						buf[i] = 80;
//				}
//				}
//				else if(flag == 1 & 20<=j)
//				{
//					flag =  2;
//					
//					for(int i=0; i<153600; i++)
//					{
//						buf[i] = 128;
//					}
//				}
//					
//					//if(buf[0]==111)
		
				
									
//					
//					if(j == 29)
//					{
//						flag = 0;
//						
//						for(int i=0; i<153600; i++)
//						{
//							buf[i] = 0;
//						}
//					}
//				
//				
//			}
//			//Toast.makeText(getApplicationContext(), "끝", Toast.LENGTH_SHORT).show();
		}
		
		return START_STICKY;
	}
	
	
	/*class MyThread extends Thread{
		
			public void run()
			{
			stringFromJNI(buf);
			mEngine.write(buf);
			}
		
	}*/
	
	
	class MyThread extends Thread {
		private boolean exit = false;
		
        @Override
        public void run() {
            super.run();
            
            while(!exit)
            {
            	try {
		            stringFromJNI(buf);
					mEngine.write(buf);
            	} catch(Exception e) {
            		
            	}
            }
            
        }
        
        public void destroy() {
        	exit = true;
        }
    }
	
	
	
	protected void onNewIntent(Intent intent) {
		//L.d("handling intent action: " + intent.getAction());
		if (mEngine == null) {
			//Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
			mEngine = new AccessoryEngine(getApplicationContext(), mCallback);
		}
		//Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();
		mEngine.onNewIntent(intent);
		//super.onNewIntent(intent);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		mEngine.onDestroy();
		mEngine = null; 
		super.onDestroy();
	}
	
	private final IEngineCallback mCallback = new IEngineCallback() {
		@Override
		public void onDeviceDisconnected() {
			//L.d("device physically disconnected");
			if(DDThread != null && DDThread.isAlive())
			{
				DDThread.destroy();
			}
			
			stopService(new Intent(getApplicationContext(), FramebufferSend.class));
			stopService(new Intent(getApplicationContext(), AlwaysOnTop.class));
			//sendBroadcast(new Intent("com.example.displaychoice.stateOFF")); //QuickSetting 상태 표시를 위한 브로드캐스트
			
			Toast.makeText(getApplicationContext(), "외부 LCD가 분리되었습니다.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onConnectionEstablished() {
			//L.d("device connected! ready to go!");
		}

		@Override
		public void onConnectionClosed() {
			//L.d("connection closed");
		}

		@Override
		public void onDataRecieved(byte[] data, int num) {
			//L.d("received %d bytes", num);
		}
	
	};
	
	public native int stringFromJNI(byte[] array);
	public native int unimplementedStringFromJNI();
	
	static {
        System.loadLibrary("hello-jni");
    }
}
