package second.test.joolmera;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author Jool
 *
 */
public class CameraActivity extends Activity {


	// Joolmera obj
	private Joolmera App;


	MyCameraSurface mSurface;
	String mRootPath;
	String mTempPath;

	static final String PICFOLDER = "Tri-mera";
	static final String TAG = "ModeEasyCameraActivity";

	private static final boolean D = true;

	public static final String TOAST = "toast";

	private BackPressCloseHandler backPressCloseHandler;

	private String CapturedFilePath ="";

	// ����� �޼����� ���� stringbuffer ��ü
	private StringBuffer mOutStringBuffer;

	private BluetoothChatService mChatService = null;

	// ������ �޾ƿ���
	private SensorManager mSM;
	private Sensor myGravity;
	private String sensorY;
	private String sensorZ;

	// �ĳ�� ���̾�α�
	ProgressDialog dialog ;

	private ByteArrayOutputStream receiveImage;
	private int sendingIndex = 1;
	private int sendingIndexMax = 1;

	private String filePath;

	private boolean isSendReceive = false;

	private SendingArrClass sendingArr[] = null;

	private class SendingArrClass{
		public byte[] arr;

		public void initSendingArrClass() {
			arr = new byte[DataConstants.sendingSize+8];
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//		Log.e("destroy", "CameraActivity �׾�Ф�");
		//		sendMessage("exit");
		//		modeEasyActivity.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		Log.d(TAG,"init ModeEasyCameraActivity");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ); 

		// Joolmera �޾ƿ�
		App = (Joolmera)getApplicationContext();


		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = App.getBluetoothChatService();
		mChatService.setHandler(mHandler);

		backPressCloseHandler = new BackPressCloseHandler(this);

		// ȭ�� ������ �ʰ�!
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mOutStringBuffer = new StringBuffer("");

		sendingArr = new SendingArrClass[100];
		for(int i=0; i<sendingArr.length; i++){
			sendingArr[i] = new SendingArrClass();
			sendingArr[i].initSendingArrClass();
		}


		// ������ �ޱ�
		mSM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		myGravity = mSM.getDefaultSensor(Sensor.TYPE_GRAVITY);


		mSurface = (MyCameraSurface)findViewById(R.id.previewFrame);


		mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/" + PICFOLDER;
		File fRoot = new File(mRootPath);
		if (fRoot.exists() == false) {
			if (fRoot.mkdir() == false) {
				Toast.makeText(this, "������ ������ ����� ����ϴ�.", 1).show();
				finish();
				return;
			}
		}

		mTempPath = mRootPath + "/temp";
		File fTemp = new File(mTempPath);
		if (fTemp.exists() == false) {
			if (fTemp.mkdir() == false) {
				Toast.makeText(this, "����� ����ϴ�.", 1).show();
				finish();
				return;
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mChatService.setHandler(mHandler);
	}

	@Override
	protected void onStart() {
		super.onStart();

	};

	@Override
	protected void onPause() {
		super.onPause();

	};

	public SensorEventListener mySensorListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {

			if(event.sensor.getType() == Sensor.TYPE_GRAVITY)
			{
				int Y = (int)(event.values[1]*10.0);
				int Z = (int)(event.values[2]*10.0);			

				sensorY = Integer.toString(Y);
				sensorZ = Integer.toString(Z);

				Log.d("MCU ���", "sensorY = " +sensorY);
				Log.d("MCU ���", "sensorZ = " +sensorZ);
			}

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	// ��Ŀ�� �����ϸ� �Կ� �㰡
	AutoFocusCallback mAutoFocus = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			//			mShutter.setEnabled(success);
			mSurface.mCamera.takePicture(mShutter, null, mPicture);

		}
	};

	ShutterCallback mShutter = new ShutterCallback(){

		@Override
		public void onShutter() {
			// TODO Auto-generated method stub

		}

	};

	public void removeFiles(String mPath)
	{
		//		String mPath = "/sdcard/kiswire/";
		File dir = new File(mPath);

		String[] children = dir.list();
		if (children != null) {
			for (int i=0; i<children.length; i++) {
				String filename = children[i];
				File f = new File(mPath + filename);

				if (f.exists()) {
					f.delete();
				}
			}//for
		}//if

	}//public void removeFiles()




	public void panoramaThread() {

		dialog = new ProgressDialog(this);
		dialog = new ProgressDialog(this);
		dialog.setTitle("Tri-mera");
		dialog.setMessage("�ĳ�� ���� �Դϴ�.");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
		new Thread(new Runnable() {
			public void run() {
				Panorama.makePanorama(DataConstants.CurrentPanoramaMode , DataConstants.FEATURE_SURF, DataConstants.CurrentPanoramaModeShotNumber-1,3, mTempPath, filePath);
				removeFiles(mRootPath+"/temp/");
				dialog.dismiss();
				panoramaHandler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	Handler panoramaHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(CameraActivity.this, "�ĳ�� ���� �Ϸ�Ǿ���ϴ�.", Toast.LENGTH_SHORT).show();
		};
	};
	// ���� ����.
	PictureCallback mPicture = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {

			if(isSendReceive == true){

				if(DataConstants.CurrentPanoramaMode==-1 || DataConstants.CurrentPanoramaModeShotNumber==-1){
					Toast.makeText(CameraActivity.this, "�ĳ�� �� ���� �߻��߽��ϴ�.", Toast.LENGTH_SHORT).show();
					Log.e("panorama", "�ĳ�� �� ����");
				}

				String panoramaFileName = String.format("tri_mera%d.jpg",DataConstants.panorama_count);		
				String panoramaPath = mTempPath + "/" + panoramaFileName;

				File file = new File(panoramaPath);
				try {
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(data);
					fos.flush();
					fos.close();
				} catch (Exception e) {
					return;
				}

				Toast.makeText(CameraActivity.this, DataConstants.panorama_count+"�� ° ������ ���� �Ǿ���ϴ�", 0).show();
				DataConstants.panorama_count++;

				if(DataConstants.panorama_count==DataConstants.CurrentPanoramaModeShotNumber){
					DataConstants.panorama_count = 1;

					Calendar calendar = Calendar.getInstance();
					String FileName = String.format("SH%02d%02d%02d-%02d%02d%02d.jpg", 
							calendar.get(Calendar.YEAR) % 100, calendar.get(Calendar.MONTH)+1, 
							calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 
							calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
					filePath = mRootPath + "/"+ FileName;

					Log.e("panorama", "DataConstants.CurrentPanoramaMode = "+DataConstants.CurrentPanoramaMode);
					Log.e("panorama", "DataConstants.CurrentPanoramaModeShotNumber = "+DataConstants.CurrentPanoramaModeShotNumber);
					Log.e("panorama", "mRootPath+/temp = "+mRootPath+"/temp");
					Log.e("panorama", "filePath = "+filePath);


					panoramaThread();
				}

				isSendReceive = false;


				camera.startPreview();


			}else{

				Log.d(TAG,"mPicture onPictureTaken");
				Calendar calendar = Calendar.getInstance();
				String FileName = String.format("SH%02d%02d%02d-%02d%02d%02d.jpg", 
						calendar.get(Calendar.YEAR) % 100, calendar.get(Calendar.MONTH)+1, 
						calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 
						calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
				String path = mRootPath + "/" + FileName;
				CapturedFilePath = path;

				File file = new File(path);
				try {
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(data);
					fos.flush();
					fos.close();
				} catch (Exception e) {

					return;
				}

				Toast.makeText(getApplicationContext(), "������ ���� �Ǿ���ϴ�", 0).show();

				// TODO: ����� �̹��� �ɰ���

				File imgFile = new  File(CapturedFilePath);
				Bitmap imageBitmap = null;
				if(imgFile.exists()){
					imageBitmap= BitmapFactory.decodeFile(imgFile.getAbsolutePath());
					if(imageBitmap==null)
						Toast.makeText(CameraActivity.this, "bitmap is null T-T", Toast.LENGTH_LONG).show();
				}
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 300, imageBitmap.getHeight()/(imageBitmap.getWidth()/300), true);
				imageBitmap.compress( CompressFormat.JPEG, 60, outputStream) ;  
				byte[] imageByte = outputStream.toByteArray() ;  

				int len;

				byte[] byteLen = new byte[4];
				byte[] sendByte = new byte[DataConstants.sendingSize];

				ByteArrayInputStream inputStream = new ByteArrayInputStream(imageByte);

				byteLen = DataConstants.intToByteArray(imageByte.length);

				receiveImage = new ByteArrayOutputStream();

				try {
					while((len=inputStream.read(sendByte))!=-1){
						sendingArr[sendingIndex].arr[0] = 6;
						sendingArr[sendingIndex].arr[1] = 26;
						sendingArr[sendingIndex].arr[2] = 18; 
						sendingArr[sendingIndex].arr[3] = (byte) sendingIndex;
						sendingArr[sendingIndex].arr[4] = byteLen[0];
						sendingArr[sendingIndex].arr[5] = byteLen[1];
						sendingArr[sendingIndex].arr[6] = byteLen[2];
						sendingArr[sendingIndex].arr[7] = byteLen[3];

						for(int eof=8; eof<len+8; eof++){
							sendingArr[sendingIndex].arr[eof] = sendByte[eof-8];
						}
						//						Log.d("read","�̹��� �ɰ������̴�! ������ "+sendingArr[sendingIndex].arr[3]+"��° �����̴�");

						receiveImage.write(sendingArr[sendingIndex].arr,8,sendingArr[sendingIndex].arr.length-8);
						sendingIndex++;
					}
				} catch (IOException e) { 
					//TODO: rano said good jam! 
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				byte[] getImage = receiveImage.toByteArray();
				ImageView test = (ImageView) findViewById(R.id.imageView_test);
				test.setImageBitmap(BitmapFactory.decodeByteArray(getImage, 0, getImage.length));

				Log.d("write","imageByte.length is "+ imageByte.length);
				//			Toast.makeText(CameraActivity.this, "�̹��� ����� �Ϸ�Ǿ���ϴ�!", Toast.LENGTH_SHORT).show();


				// TODO: �ڵ鷯 �θ���
				sendingIndexMax = sendingIndex;
				sendingIndex = 1;
				sendHandler.sendEmptyMessage(0);

				camera.startPreview();
			}

		}


	};


	private Handler sendHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {

			//			Log.d("read","�̹��� ������ ���̴�! ������ "+sendingArr[sendingIndex].arr[3]+"��° �����̴�");
			mChatService.write(sendingArr[sendingIndex].arr);
			sendingIndex++;
			if(sendingIndex > sendingIndexMax){
				sendingIndex = 1;
				sendingIndexMax = 1;
			} else {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						sendHandler.sendEmptyMessage(0);
					}
				}, 30);
			}

		}
	};

	//	class DelayThread extends Thread {
	//	    public void run() {
	//	       
	//	        }
	//	    }
	//	} 






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
				//				Toast.makeText(getApplicationContext(), "ī�޶� ��Ƽ��Ƽ ����Ʈ",
				//						Toast.LENGTH_SHORT).show();

				break;
			case DataConstants.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				if(readMessage.equals("capture"))
				{	
					mSurface.mCamera.autoFocus(mAutoFocus);
					DataConstants.flag_isImageTransfer_ing = true;
				}

				if(readMessage.equals("send"))
				{	
					Log.e("panorama","send ����");		
					mSurface.mCamera.autoFocus(mAutoFocus);	
					isSendReceive = true;
				}

				if(readMessage.equals("restart")){
					DataConstants.flag_isImageTransfer_ing = false;
				}

				if(readMessage.equals("py"))
				{	
					// �������� ����
					mSM.registerListener(mySensorListener, myGravity,
							SensorManager.SENSOR_DELAY_GAME);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							String message = "///"+sensorY+"///"+sensorZ;
							mChatService.write(message.getBytes());
							mSM.unregisterListener(mySensorListener);
							Log.d("MCU ���", "���µ� = "+message);
						}
					}, 100);				
				}

				if(readMessage.equals("6"))
				{	
					DataConstants.CurrentPanoramaMode = DataConstants.MODE_HORIZONTAL;
					DataConstants.CurrentPanoramaModeShotNumber = DataConstants.HORIZONTAL_LONG;
				}

				if(readMessage.equals("7"))
				{	
					DataConstants.CurrentPanoramaMode = DataConstants.MODE_VERTICAL;
					DataConstants.CurrentPanoramaModeShotNumber = DataConstants.VERTICAL_LONG;
				}


				if(readMessage.equals("exit")){
					finish();
				}

				break;
			case DataConstants.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;

			}
		}
	};


	public byte[] bitmapToByteArray( Bitmap $bitmap ) {  
		ByteArrayOutputStream stream = new ByteArrayOutputStream() ; 
		$bitmap = Bitmap.createScaledBitmap($bitmap, 160, $bitmap.getHeight()/($bitmap.getWidth()/160), true);
		$bitmap.compress( CompressFormat.JPEG, 30, stream) ;  
		byte[] byteArray = stream.toByteArray() ;  
		return byteArray ;  
	}  

	public  byte[] intToByteArray(int value) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte)(value >> 24);
		byteArray[1] = (byte)(value >> 16);
		byteArray[2] = (byte)(value >> 8);
		byteArray[3] = (byte)(value);
		return byteArray;
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		backPressCloseHandler.onBackPressed();
	}
}



/***
 * 
 * 
 * SurfaceView Class
 * 
 * @author Jool
 *
 */


class MyCameraSurface extends SurfaceView implements SurfaceHolder.Callback {
	SurfaceHolder mHolder;
	Camera mCamera;


	// Joolmera obj
	private Joolmera App;
	private BluetoothChatService mChatService = null;


	private Bitmap streamingImage;

	private int sendingImageIndex = 1;
	private int sendingImageIndexMax = 1;

	private TimerTask mTask;
	private Timer mTimer;

	private boolean flag_isSendOK = false;
	private boolean flag_isChangeOK = true;


	private SendingArrClass sendingArr[] = null;

	private class SendingArrClass{
		public byte[] arr;

		public void initSendingArrClass() {
			arr = new byte[DataConstants.sendingSize+8];
		}
	}



	public MyCameraSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	// ǥ�� ��� ī�޶� �����ϰ� �̸����� ����
	@SuppressWarnings("deprecation")
	public void surfaceCreated(SurfaceHolder holder) {

		mCamera = Camera.open();
		//		mCamera.setDisplayOrientation(90);
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			mCamera.release();
			mCamera = null;
		}		

		sendingArr = new SendingArrClass[100];
		for(int i=0; i<sendingArr.length; i++){
			sendingArr[i] = new SendingArrClass();
			sendingArr[i].initSendingArrClass();
		}

		// Joolmera �޾ƿ�
		App = (Joolmera) Joolmera.application.getApplicationContext();

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = App.getBluetoothChatService();


		mCamera.setPreviewCallback(new Camera.PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				// TODO Auto-generated method stub

				Camera.Parameters params = mCamera.getParameters();

				int w = params.getPreviewSize().width;
				int h = params.getPreviewSize().height;
				int format = params.getPreviewFormat ();

				//	Bitmap imageBitap = new Bitmap();


				YuvImage image = new YuvImage(data, format, w, h, null);

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				Rect area = new Rect(0, 0, w, h);
				image.compressToJpeg(area, 50, out);
				streamingImage = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());



				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				streamingImage = Bitmap.createScaledBitmap(streamingImage, 160, streamingImage.getHeight()/(streamingImage.getWidth()/160), true);
				streamingImage.compress( CompressFormat.JPEG, 30, outputStream) ;  
				byte[] imageByte = outputStream.toByteArray() ;  

				int len;

				byte[] byteLen = new byte[4];
				byte[] sendByte = new byte[DataConstants.sendingSize];

				ByteArrayInputStream inputStream = new ByteArrayInputStream(imageByte);

				byteLen = DataConstants.intToByteArray(imageByte.length);

				flag_isSendOK = false;

				if(flag_isChangeOK == true){
					try {
						while((len=inputStream.read(sendByte))!=-1){
							sendingArr[sendingImageIndex].arr[0] = 8;
							sendingArr[sendingImageIndex].arr[1] = 28;
							sendingArr[sendingImageIndex].arr[2] = 16; 
							sendingArr[sendingImageIndex].arr[3] = (byte) sendingImageIndex;
							sendingArr[sendingImageIndex].arr[4] = byteLen[0];
							sendingArr[sendingImageIndex].arr[5] = byteLen[1];
							sendingArr[sendingImageIndex].arr[6] = byteLen[2];
							sendingArr[sendingImageIndex].arr[7] = byteLen[3];

							for(int eof=8; eof<len+8; eof++){
								sendingArr[sendingImageIndex].arr[eof] = sendByte[eof-8];
							}

						}
					} catch (IOException e) { 
						//TODO: rano said good jam! 
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 

					// TODO: �ڵ鷯 �θ���
					sendingImageIndexMax = sendingImageIndex;
					sendingImageIndex = 1;
					flag_isSendOK = true;
					//				sendHandler.sendEmptyMessage(0);

				}
			}
		});

		mTask = new TimerTask() {
			@Override
			public void run() {
				//				Log.d("read","�̹��� ������ ���̴�! ������ "+sendingArr[sendingImageIndex].arr[3]+"��° �����̴�");
				if(flag_isSendOK == true && DataConstants.flag_isImageTransfer == false && DataConstants.flag_isImageTransfer_ing == false){
					flag_isChangeOK = false;
					mChatService.write(sendingArr[sendingImageIndex].arr);
					sendingImageIndex++;
					if(sendingImageIndex > sendingImageIndexMax){
						sendingImageIndex = 1;
						sendingImageIndexMax = 1;
					}
				}
				flag_isChangeOK = true;
			}
		};

		mTimer = new Timer();

		mTimer.schedule(mTask, 10, 10);
	}





	//	private Handler sendHandler = new Handler(){
	//		@Override
	//		public synchronized void handleMessage(Message msg) {
	//			Log.d("read","�̹��� ������ ���̴�! ������ "+sendingArr[sendingImageIndex].arr[3]+"��° �����̴�");
	//			mChatService.write(sendingArr[sendingImageIndex].arr);
	//			sendingImageIndex++;
	//			if(sendingImageIndex > sendingImageIndexMax){
	//				sendingImageIndex = 1;
	//				sendingImageIndexMax = 1;
	//			} else {
	//				new Handler().postDelayed(new Runnable() {
	//					@Override
	//					public void run() {
	//						sendHandler.sendEmptyMessage(0);
	//					}
	//				}, 10);
	//			}
	//		}
	//	};

	// ǥ�� �ı��� ī�޶� �ı��Ѵ�.
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mTimer.cancel();
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	// ǥ���� ũ�Ⱑ ������ �� ������ �̸����� ũ�⸦ ���� �����Ѵ�.
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		Camera.Parameters params = mCamera.getParameters();
		//		params.getSupportedPictureSizes();
		//		params.setPreviewSize(width, height);
		//		params.setPictureSize(width, height);

		//		params.setRotation(90);

		try{
			mCamera.setParameters(setSize(params));
		}catch(Exception e){
			Log.d("surfaceChanged","���� ���");
			e.getStackTrace();
		}
		mCamera.startPreview();
	}

	private Parameters setSize(Parameters parameters) {
		// TODO Auto-generated method stub

		Log.d("<<picture>>", "W:"+parameters.getPictureSize().width+"H:"+parameters.getPictureSize().height);
		Log.d("<<preview>>", "W:"+parameters.getPreviewSize().width+"H:"+parameters.getPreviewSize().height);

		int tempWidth = parameters.getPictureSize().width;
		int tempHeight = parameters.getPictureSize().height;
		int Result = 0;
		int Result2 = 0;
		int picSum = 0;
		int picSum2 = 0;
		int soin = 2;

		while(tempWidth >= soin && tempHeight >= soin){
			Result = tempWidth%soin;
			Result2 = tempHeight%soin;
			if(Result == 0 && Result2 == 0){
				picSum = tempWidth/soin;
				picSum2 = tempHeight/soin;
				System.out.println("PictureWidth :"+tempWidth+"/"+soin+"���:"+picSum+"������:"+Result);
				System.out.println("PictureHeight :"+tempHeight+"/"+soin+"���:"+picSum2+"������:"+Result2);
				tempWidth = picSum;
				tempHeight = picSum2;
			}else {
				soin++;
			}

		}
		System.out.println("������� "+picSum+":"+picSum2);

		List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
		for (Size size : previewSizeList){
			tempWidth = size.width;
			tempHeight = size.height;
			Result = 0;
			Result2 = 0;
			int preSum = 0;
			int preSum2 = 0;
			soin = 2;

			while(tempWidth >= soin && tempHeight >= soin){
				Result = tempWidth%soin;
				Result2 = tempHeight%soin;
				if(Result == 0 && Result2 == 0){
					preSum = tempWidth/soin;
					preSum2 = tempHeight/soin;
					System.out.println("PreviewWidth :"+tempWidth+"/"+soin+"���:"+preSum+"������:"+Result);
					System.out.println("PreviewHeight :"+tempHeight+"/"+soin+"���:"+preSum2+"������:"+Result2);
					tempWidth = preSum;
					tempHeight = preSum2;
				}else {
					soin++;
				}

			}
			System.out.println("������� "+preSum+":"+preSum2);
			if(picSum == preSum && picSum2 == preSum2){
				parameters.setPreviewSize(size.width, size.height);
				break;
			}
		}
		return parameters;
	}




}

