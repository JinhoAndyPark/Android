package com.example.unit23;

import kr.robomation.physical.Albert;

import org.roboid.robot.Device;
import org.roboid.robot.Robot;
import org.smartrobot.android.RobotActivity;

import com.example.ex23.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends RobotActivity {

	private final static String SENDMEG = "example.intent.action.Sensor";
	
	private Device leftEyeDevice;
	private Device rightEyeDevice;
	private Device leftWheelDevice;
	private Device rightWheelDevice;
	
	private Intent intent;
	
	int [] sensor_value = new int [2];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		registerReceiver(br, new IntentFilter(SENDMEG));
		startService(new Intent("example.android.SERVICE"));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(br);
		stopService(new Intent("example.android.SERVICE"));
	}
	
	@Override
	public void onInitialized(Robot robot) {
		super.onInitialized(robot);
		leftEyeDevice = robot.findDeviceById(Albert.EFFECTOR_LEFT_EYE);
		rightEyeDevice = robot.findDeviceById(Albert.EFFECTOR_RIGHT_EYE);
		leftWheelDevice = robot.findDeviceById(Albert.EFFECTOR_LEFT_WHEEL);
		rightWheelDevice = robot.findDeviceById(Albert.EFFECTOR_RIGHT_WHEEL);
	}
	
	
	private BroadcastReceiver br = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			if(action.equals(SENDMEG)){
				sensor_value = intent.getIntArrayExtra("Sensor_Value");
				
				sensor_value = intent.getIntArrayExtra("Sensor_Value");
				
				if( (sensor_value[0] < -3) &&( (sensor_value[1] > -2)&&(sensor_value[1] < 2) ) ){
					leftWheelDevice.write(+30);
					rightWheelDevice.write(+30);
				}
				else if( (sensor_value[0] > 3) &&( (sensor_value[1] > -2)&&(sensor_value[1] < 2) ) ){
					leftWheelDevice.write(-30);
					rightWheelDevice.write(-30);
				}
				else if( (sensor_value[1] < -3) &&( (sensor_value[0] > -2)&&(sensor_value[0] < 2) ) ){
					leftWheelDevice.write(-30);
					rightWheelDevice.write(+30);
				}
				else if( (sensor_value[1] > 3) &&( (sensor_value[0] > -2)&&(sensor_value[0] < 2) ) ){
					leftWheelDevice.write(+30);
					rightWheelDevice.write(-30);
				}
				else{
					leftWheelDevice.write(0);
					rightWheelDevice.write(0);
				}
			}
		}
	};
}
