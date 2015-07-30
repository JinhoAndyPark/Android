package com.example.unit23;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class Activity_Service extends Service {
	
	private SensorManager sensorManager;
	private Sensor accelerormeterSensor;
	
	Intent intent;
	
	@Override
	public void onCreate() {
		
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		
		intent = new Intent("example.intent.action.Sensor");
		sensorManager.registerListener(mysensorListener, accelerormeterSensor, SensorManager.SENSOR_DELAY_NORMAL);
		
//	
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		
		sensorManager.unregisterListener(mysensorListener);
		
		super.onDestroy();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public SensorEventListener mysensorListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			if( event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				int [] value = new int [2];
				value[0] = (int) event.values[0];
				value[1] = (int) event.values[1];
				

				
				intent.putExtra("Sensor_Value", value);
				sendBroadcast(intent);
			
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
}
