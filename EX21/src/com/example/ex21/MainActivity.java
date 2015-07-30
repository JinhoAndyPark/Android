package com.example.EX21;

import kr.robomation.physical.Albert;

import org.roboid.robot.Device;
import org.smartrobot.android.RobotActivity;

import android.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends RobotActivity {

	private Button		showButtun, show2btn, show3btn, Messagebtn;
	private ImageView 	imageview;
	private TextView 	msgText, msgText1, msgText2, msgText3, msgText4, msgText5, msgText6, msgText7;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		msgText		=	(TextView)findViewById(R.id.text);
		msgText1	=	(TextView)findViewById(R.id.text1);
		msgText2	=	(TextView)findViewById(R.id.text2);
		msgText3	=	(TextView)findViewById(R.id.text3);
		msgText4	=	(TextView)findViewById(R.id.text4);
		msgText5	=	(TextView)findViewById(R.id.text5);
		msgText6	=	(TextView)findViewById(R.id.text6);
		msgText7	=	(TextView)findViewById(R.id.text7);
		Messagebtn	=	(Button)findViewById(R.id.msgbtn);
		showButtun	=	(Button)findViewById(R.id.show);
		show2btn	=	(Button)findViewById(R.id.show2);
		show3btn	=	(Button)findViewById(R.id.show3);
		imageview	=	(ImageView)findViewById(R.id.image);
		
		showButtun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				imageview.setVisibility(View.VISIBLE);
//				startActivity(new Intent(MainActivity.this, SecondActivity.class));
				
				Intent intent = new Intent(MainActivity.this, SecondActivity.class);
				intent.putExtra("text", "1");
				startActivity(intent);
				
//				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")));
				
//				Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//				intent.putExtra(SearchManager.QUERY, "������б�");
//				startActivity(intent);
			}
		});
		show2btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, SecondActivity.class);
				intent.putExtra("text", "2");
				startActivity(intent);
			}
		});
		show3btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, SecondActivity.class);
				intent.putExtra("text", "3");
				startActivity(intent);
			}
		});
		
		Messagebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				handler.obtainMessage(0, "test").sendToTarget();

				
//				Message msg = handler.obtainMessage();
//				msg.what = 0;
//				msg.arg1 = 1;
//				handler.sendMessage(msg);
			}
		});
		
	}
		
	@Override
	public void onDeviceDataChanged(final Device device, final Object values, long timestamp) {
//		int[] data;
//		switch(device.getId()){
//			case Albert.SENSOR_LEFT_PROXIMITY:{
//				data = (int[])values;
//				msgText.setText("Left Proximity : " + data[0]);
//			} break;
//		}
		
		handler_albert.obtainMessage(
				device.getId(), values).sendToTarget();
		
//		runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				int[] data;
//				switch(device.getId()){
//					case Albert.SENSOR_LEFT_PROXIMITY:{
//						data = (int[])values;
//						msgText.setText("Left Proximity : " + data[0]);
//					} break;
//					
//				}
//			}
//		});
		
	}
	
	private Handler handler_albert = new Handler(){
		public void handleMessage(Message msg) {
			int[] values;
			switch(msg.what){
				case Albert.SENSOR_LEFT_PROXIMITY:{
					values = (int[])msg.obj;
					msgText.setText("Left Proximity : " 
							+ values[0] + values[1] + values[2]
							+ "," + values[3]);
				}break;
				case Albert.SENSOR_RIGHT_PROXIMITY:{
					values = (int[])msg.obj;
					msgText1.setText("Right Proximity : " + values[0] + values[1] + values[2]
							+ "," + values[3]);
				} break;
				case Albert.SENSOR_ACCELERATION:{
					values = (int[])msg.obj;
					msgText2.setText("Acceleration : " + values[0] + values[1] + values[2]);
				} break;
				case Albert.SENSOR_POSITION:{
					values = (int[])msg.obj;
					msgText3.setText("Position : " + values[0] + values[1]);
				} break;
				case Albert.SENSOR_ORIENTATION:{
					values = (int[])msg.obj;
					msgText4.setText("Orientation : " + values[0]);
				} break;
				case Albert.SENSOR_LIGHT:{
					values = (int[])msg.obj;
					msgText5.setText("Light : " + values[0]);
				} break;
				case Albert.SENSOR_TEMPERATURE:{
					values = (int[])msg.obj;
					msgText6.setText("Temperature : " + values[0]);
				} break;
				case Albert.SENSOR_BATTERY:{
					values = (int[])msg.obj;
					msgText7.setText("Battery : " + values[0]);
				} break;
			}
		};
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				msgText.setText("received : " + msg.obj);
//				msgText.setText("received : " + msg.arg1);
				break;
			}
		};
	};
}
