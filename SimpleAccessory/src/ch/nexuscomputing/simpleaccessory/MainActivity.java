package ch.nexuscomputing.simpleaccessory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import ch.nexuscomputing.simpleaccessory.AccessoryEngine.IEngineCallback;

public class MainActivity extends Activity {

	private AccessoryEngine mEngine = null;
	private ImageView mButtonImage = null;
	
	private TextView mBrightnessText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_main);
		mButtonImage = (ImageView) findViewById(R.id.ivButton);
		mButtonImage.setImageResource(R.drawable.button_y);
		
		mBrightnessText = (TextView) findViewById(R.id.tvBrightness);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		L.d("handling intent action: " + intent.getAction());
		if (mEngine == null) {
			mEngine = new AccessoryEngine(getApplicationContext(), mCallback);
		}
		mEngine.onNewIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onDestroy() {
		mEngine.onDestroy();
		mEngine = null;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	private final IEngineCallback mCallback = new IEngineCallback() {
		@Override
		public void onDeviceDisconnected() {
			L.d("device physically disconnected");
		}

		@Override
		public void onConnectionEstablished() {
			L.d("device connected! ready to go!");
		}

		@Override
		public void onConnectionClosed() {
			L.d("connection closed");
		}

		@Override
		public void onDataRecieved(byte[] data, int num) {
			if(num > 0){
				updateText((data[0]&0xFF)+"");
				if(data[0] == 1){
					updateResource(R.drawable.button_r);
				}else{
					updateResource(R.drawable.button_g);
				}
			}
		}
	};
	
	private void updateResource(final int id){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mButtonImage.setImageResource(id);
			}
		});
	}
	
	private void updateText(final String text){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mBrightnessText.setText(text);
			}
		});
	}

}
