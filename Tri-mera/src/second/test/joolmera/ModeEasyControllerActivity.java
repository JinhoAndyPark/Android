package second.test.joolmera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ModeEasyControllerActivity extends Activity {

	// Debugging
	private static final String TAG = "ModeEasyControllerActivity";
	private static final boolean D = true;


	public static final String TOAST = "toast";


	// ����� �޼����� ���� stringbuffer ��ü
	private StringBuffer mOutStringBuffer;

	private Button btnCapture;

	//	private LinearLayout layoutModeEasyController;
	private ImageView imgModeEasyController;
	private ImageView imgModeEasyControllerPreview;


	private BackPressCloseHandler backPressCloseHandler;

	private BluetoothChatService mChatService = null;

	// Joolmera obj
	private Joolmera App;


	private int imageByteLen = 0;

	private ByteArrayOutputStream ReceiveImage;
	//	private byte[] totalReadData;
	//	private Bitmap capturedImage;

	//	int bufferlen;
	//	byte[] buffer;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		Log.e("destroy", "ModeEasyControllerActivity �׾����Ф�");
//		sendMessage("exit");
//		modeEasyActivity.finish();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode_easy_controller);

		// Joolmera �޾ƿ�
		App = (Joolmera)getApplicationContext();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );



		imgModeEasyController = (ImageView) findViewById(R.id.imageView_camera);
		//		layoutModeEasyController = (LinearLayout) findViewById(R.id.layout_mode_easy);
		imgModeEasyControllerPreview = (ImageView) findViewById(R.id.imageView_preview);

		btnCapture = (Button) findViewById(R.id.button_controller_capture);

		ReceiveImage = new ByteArrayOutputStream();


		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = App.getBluetoothChatService();
		mChatService.setHandler(mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");

		// �ι� ���� �����ϴ� Ŭ����
		backPressCloseHandler = new BackPressCloseHandler(this);

		btnCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String message = "capture";
				sendMessage(message);
			}
		});



	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mChatService.setHandler(mHandler);
	}




	public Bitmap byteArrayToBitmap( byte[] $byteArray ) {
		Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;  
		return bitmap ;  
	}  



	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		backPressCloseHandler.onBackPressed();
		if(DataConstants.flag_isImageTransfer == true){
			DataConstants.flag_isImageTransfer = false;
			imgModeEasyController.setVisibility(View.GONE);
			imgModeEasyControllerPreview.setVisibility(View.VISIBLE);
			imgModeEasyController.invalidate();
			imgModeEasyControllerPreview.invalidate();
		}
		DataConstants.flag_isImageTransfer_ing = false;
		sendMessage("restart");
	}

	/**
	 * Sends a message.
	 * @param message  A string of text to send.
	 */
	private void sendMessage(String message) {
		// ���� ���°� ������°� �ƴ϶�� �佺Ʈ�� ���!
		if (mChatService.getState() != DataConstants.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
			return;
		}

		// ���� �޼����� �ִٸ�
		if (message.length() > 0) {
			// �޼����� ����Ʈ�� �о��
			byte[] send = message.getBytes();
			//���� ��ü�� �̿��Ͽ� ����!
			mChatService.write(send);

			// ��Ʈ������ �ʱ�ȭ
			mOutStringBuffer.setLength(0);
		}
	}



	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DataConstants.MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				//				Toast.makeText(getApplicationContext(), "��Ʈ�ѷ� ��Ƽ��Ƽ ����Ʈ",
				//						Toast.LENGTH_SHORT).show();
				break;
			case DataConstants.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;

				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);

				if(readMessage.equals("exit")){
					finish();
				}
				break;

			case DataConstants.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			case DataConstants.MESSAGE_IMAGE:
				Bitmap capturedImage = (Bitmap) msg.obj;
				//				ByteArrayOutputStream ReceiveImage = (ByteArrayOutputStream) msg.obj;
				Log.d("controller", "�ڵ鷯���� �޾Ҷ�! �̹��� ������� = "+msg.arg1);
				//				DataConstants.showData(imgBuf, "controller read");
				DataConstants.flag_isImageTransfer = true;
				DataConstants.flag_isImageTransfer_ing = true;
				imgModeEasyController.setImageBitmap(capturedImage);
				imgModeEasyController.setVisibility(View.VISIBLE);
				imgModeEasyControllerPreview.setVisibility(View.GONE);
				imgModeEasyController.bringToFront();
				imgModeEasyController.invalidate();
				imgModeEasyControllerPreview.invalidate();
				Toast.makeText(getApplicationContext(), "������ �Կ��Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
				break;

			case DataConstants.MESSAGE_STREAM :

				if(DataConstants.flag_isImageTransfer  == false){
					imgModeEasyControllerPreview.setVisibility(View.VISIBLE);
					Bitmap streamingImage = (Bitmap) msg.obj;
					imgModeEasyControllerPreview.setImageBitmap(streamingImage);
					imgModeEasyControllerPreview.invalidate();
				}
				break;

			}
		}
	};
}
