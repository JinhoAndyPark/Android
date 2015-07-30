package second.test.joolmera;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ModeHardControllerActivity extends Activity {

	// Debugging
	private static final String TAG = "ModeEasyControllerActivity";
	private static final boolean D = true;

	public static final String TOAST = "toast";


	// ����� �޼����� ���� stringbuffer ��ü
	private StringBuffer mOutStringBuffer;

	private Button btnUp;
	private Button btnDown;
	private Button btnLeft;
	private Button btnRight;
	private Button btnCapture;
	private Button btnPanorama;
	private Button btnPanoramaSelect;
	//	private boolean isPanoramaModeGaro = true;
	//	private Button btnPanoramaGaro;
	//	private Button btnPanoramaSero;
	private Button btnStabilize;
	//		private LinearLayout layoutModeEasyController;
	private ImageView imgModeHardController;
	private ImageView imgModeHardControllerPreview;

	private ImageView imgPanoramaGaro;
	private ImageView imgPanoramaSero;


	private BackPressCloseHandler backPressCloseHandler;

	private BluetoothChatService mChatService = null;
	private BluetoothChatService mMCUService = null;

	// Joolmera obj
	private Joolmera App;

	private boolean imageTransfer = false;
	private ByteArrayOutputStream ReceiveImage;

	private boolean isGaroPanoramaSelected = false;
	private boolean isSeroPanoramaSelected = false;	
	private TextView txtPanoramaGuide;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode_hard_controller);

		// Joolmera �޾ƿ�
		App = (Joolmera)getApplicationContext();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );

		imgModeHardController = (ImageView) findViewById(R.id.imageView_camera);
		imgModeHardControllerPreview = (ImageView) findViewById(R.id.imageView_preview);
		//	layoutModeEasyController = (LinearLayout) findViewById(R.id.layout_mode_easy);


		btnUp = (Button) findViewById(R.id.button_up);
		btnDown = (Button) findViewById(R.id.button_down);
		btnLeft = (Button) findViewById(R.id.button_left);
		btnRight = (Button) findViewById(R.id.button_right);
		btnCapture = (Button) findViewById(R.id.button_controller_capture);
		btnPanorama = (Button)findViewById(R. id.button_panorama);
		btnPanoramaSelect = (Button) findViewById(R.id.button_panorama_select);
		//		btnPanoramaGaro = (Button) findViewById(R.id.button_panorama_garo);
		//		btnPanoramaSero = (Button) findViewById(R.id.button_panorama_sero);
		btnStabilize = (Button) findViewById(R.id.button_stabilize);

		txtPanoramaGuide = (TextView) findViewById(R.id.textView_panorama_guide);

		imgPanoramaGaro = (ImageView) findViewById(R.id.imageView_panorama_garo);
		imgPanoramaSero = (ImageView) findViewById(R.id.imageView_panorama_sero);

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = App.getBluetoothChatService();
		mChatService.setHandler(androidHandler);
		mMCUService = App.getBluetoothMCUService();
		mMCUService.setHandler(mcuHandler);

		//��

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");

		// �ι� ���� �����ϴ� Ŭ����
		// 뒤로가기관련
		backPressCloseHandler = new BackPressCloseHandler(this);

		// MCU ��������

		String message = "5";
		sendMessage(message);

		btnCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(DataConstants.flag_isPanoramaselect == true && isGaroPanoramaSelected == true){
					String message = "6";
					sendMessage(message);
				}else if(DataConstants.flag_isPanoramaselect == true && isSeroPanoramaSelected == true){
					String message = "7";
					sendMessage(message);
				}else if (DataConstants.flag_isPanoramaselect == false){
					String message = "capture";
					sendMessage(message);
				}
			}
		});

		btnPanorama.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DataConstants.flag_isPanoramaselect = true;
				isGaroPanoramaSelected = true;
				isSeroPanoramaSelected = false;
				btnPanorama.setVisibility(View.GONE);	//image gone
				btnStabilize.setVisibility(View.GONE);
				btnPanoramaSelect.setVisibility(View.VISIBLE);	//panorama view
				imgPanoramaGaro.setVisibility(View.VISIBLE);
				//				btnPanoramaGaro.setVisibility(View.VISIBLE);
				//				btnPanoramaSero.setVisibility(View.VISIBLE);
			}
		});

		btnPanoramaSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isGaroPanoramaSelected){
					isGaroPanoramaSelected = false;
					isSeroPanoramaSelected = true;
				}else{
					isGaroPanoramaSelected = true;
					isSeroPanoramaSelected = false;
				}

				if(isGaroPanoramaSelected){
					imgPanoramaGaro.setVisibility(View.VISIBLE);
					imgPanoramaSero.setVisibility(View.GONE);
				}else{
					imgPanoramaGaro.setVisibility(View.GONE);
					imgPanoramaSero.setVisibility(View.VISIBLE);
				}

			}
		});

		//		btnPanoramaGaro.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				// TODO Auto-generated method stub
		//				isGaroPanoramaSelected = !isGaroPanoramaSelected;
		//				btnPanoramaGaro.setSelected(isGaroPanoramaSelected);
		//				
		//				if(isSeroPanoramaSelected == true){
		//					isSeroPanoramaSelected = !isSeroPanoramaSelected;
		//					imgPanoramaSero.setVisibility(View.GONE);
		//					txtPanoramaGuide.setVisibility(View.GONE);
		//				}
		//				
		//				
		//				if(isGaroPanoramaSelected == true){
		//					imgPanoramaGaro.setVisibility(View.VISIBLE);
		//					txtPanoramaGuide.setVisibility(View.VISIBLE);
		//				}else{
		//					imgPanoramaGaro.setVisibility(View.GONE);
		//					txtPanoramaGuide.setVisibility(View.GONE);
		//				}
		//			}
		//		});
		//
		//		btnPanoramaSero.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				// TODO Auto-generated method stub
		//				isSeroPanoramaSelected = !isSeroPanoramaSelected;
		//				btnPanoramaSero.setSelected(isSeroPanoramaSelected);
		//				
		//				if(isGaroPanoramaSelected == true){
		//					isGaroPanoramaSelected = !isGaroPanoramaSelected;
		//					imgPanoramaGaro.setVisibility(View.GONE);
		//					txtPanoramaGuide.setVisibility(View.GONE);
		//				}
		//				
		//				if(isSeroPanoramaSelected == true){
		//					imgPanoramaSero.setVisibility(View.VISIBLE);
		//					txtPanoramaGuide.setVisibility(View.VISIBLE);
		//				}else{
		//					imgPanoramaSero.setVisibility(View.GONE);
		//					txtPanoramaGuide.setVisibility(View.GONE);
		//				}
		//			}
		//		});

		btnStabilize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String message = "py";
				sendMessage(message);
			}
		});

		btnUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String message = "1";
				sendMessage(message);
			}
		});

		btnDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String message = "2";
				sendMessage(message);
			}
		});

		btnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String message = "3";
				sendMessage(message);
			}
		});

		btnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String message = "4";
				sendMessage(message);
			}
		});

	}



	public Bitmap byteArrayToBitmap( byte[] $byteArray ) {
		Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;  
		return bitmap ;  
	}  

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		backPressCloseHandler.onBackPressed();
		if(DataConstants.flag_isPanoramaselect==true){
			btnPanorama.setVisibility(View.VISIBLE);
			btnStabilize.setVisibility(View.VISIBLE);
			//			btnPanoramaGaro.setVisibility(View.GONE);
			//			imgPanoramaGaro.setVisibility(View.GONE);
			//			btnPanoramaGaro.setSelected(false);
			//			btnPanoramaSero.setVisibility(View.GONE);
			//			imgPanoramaSero.setVisibility(View.GONE);
			//			btnPanoramaSero.setSelected(false);
			btnPanoramaSelect.setVisibility(View.GONE);
			imgPanoramaGaro.setVisibility(View.GONE);
			imgPanoramaSero.setVisibility(View.GONE);
			isGaroPanoramaSelected = false;
			isSeroPanoramaSelected = false;

			DataConstants.flag_isPanoramaselect=false;
		}
		if(DataConstants.flag_isImageTransfer == true){
			DataConstants.flag_isImageTransfer = false;
			imgModeHardController.setVisibility(View.GONE);
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
			//���� ��ü�� �̿��Ͽ� ���!
			mChatService.write(send);
			mMCUService.write(send);


			// ��Ʈ������ �ʱ�ȭ
			mOutStringBuffer.setLength(0);
		}
	}



	// The Handler that gets information back from the BluetoothChatService
	private final Handler androidHandler = new Handler() {
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


				if(readMessage.matches("///.*///.*")){
					Log.d("MCU ���", "������ �޾Ҵ�!");
					Log.d("MCU ���", "readMessage = " + readMessage);				

					String[] result = readMessage.split("///");	
					String sendString = "8";
					Log.d("MCU ���", "result[0] = " + result[0]);
					Log.d("MCU ���", "result[1] = " + result[1]);
					Log.d("MCU ���", "result[2] = " + result[2]);

					int Y=0,Z=0;

					if(result[1] == null || result[2]==null)
						break;
					
					if(result[1] != null && result[2] != null){
						Y=Integer.parseInt(result[1]);
						Z=Integer.parseInt(result[2]);
						if(Y>=0){
							sendString += "0";
							if(Y <10){
								sendString += "0";
								sendString += String.valueOf(Y);
							}
							else{
								sendString += String.valueOf(Y);
							}
						}
						else{
							sendString += "1";
							Y *= -1;		
							if(Y <10){
								sendString += "0";
								sendString += String.valueOf(Y);
							}
							else{
								sendString += String.valueOf(Y);
							}
						}					

						if(Z>=0){
							sendString += "0";
							if(Z <10){
								sendString += "0";
								sendString += String.valueOf(Z);
							}
							else{
								sendString += String.valueOf(Z);
							}
						}
						else{
							sendString += "1";
							Z *= -1;		
							if(Z <10){
								sendString += "0";
								sendString += String.valueOf(Z);
							}
							else{
								sendString += String.valueOf(Z);
							}
						}

						sendString += "8";
						mMCUService.write(sendString.getBytes());

					}
				}

				break;

			case DataConstants.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			case DataConstants.MESSAGE_IMAGE:
				Bitmap capturedImage = (Bitmap) msg.obj;
				//				ByteArrayOutputStream ReceiveImage = (ByteArrayOutputStream) msg.obj;
				//				Log.d("controller", "�ڵ鷯���� �޾Ҷ�! �̹��� ������� = "+msg.arg1);
				//				DataConstants.showData(imgBuf, "controller read");
				DataConstants.flag_isImageTransfer = true;
				imgModeHardController.setImageBitmap(capturedImage);
				imgModeHardController.setVisibility(View.VISIBLE);
				Toast.makeText(getApplicationContext(), "������ �Կ��Ǿ���ϴ�.", Toast.LENGTH_SHORT).show();
				break;

			case DataConstants.MESSAGE_STREAM :
				if(DataConstants.flag_isImageTransfer  == false){
					imgModeHardControllerPreview.setVisibility(View.VISIBLE);
					Bitmap streamingImage = (Bitmap) msg.obj;
					imgModeHardControllerPreview.setImageBitmap(streamingImage);
					imgModeHardControllerPreview.invalidate();
				}
				break;


			}
		}
	};

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mcuHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DataConstants.MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);

				break;
			case DataConstants.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);

				if(readMessage.equals("s"))
				{	
					String message = "send";
					byte[] send = message.getBytes();
					mChatService.write(send);
				}
				
				if(readMessage.equals("py"))
				{	
					String message = "py";
					byte[] send = message.getBytes();
					mChatService.write(send);
				}
				break;

			case DataConstants.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;

			}
		}
	};



}
