package second.test.joolmera;


import java.io.IOException;
import java.nio.charset.Charset;

import org.andlib.ui.RippleView;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity  implements
CreateNdefMessageCallback, OnNdefPushCompleteCallback{

	private Context ctx = this;

	private final int MODE_MENU	= 0;
	private final int MODE_APP	= 1;
	private final int MODE_TRI	= 2;
	private int curMode;


	/* Widgets */
	private ActionBar ab;
	private ImageView ivDots[];

	private ViewPager viewPager;
	private VpAdapter adapter;

	private RippleView rvAppMode;
	private RippleView rvTriMode;
	private RippleView rvTriConnect;
	private RippleView rvControllerStart;


	/* Bluetooth & NFC */
	// Debugging
	private static final String TAG = "ModeHardActivity";
	private static final boolean D = true;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_MCU = 1;
	private static final int REQUEST_CONNECT_DEVICE_ANDROID = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	private String mConnectedMCUName = null;

	// ����� �޼����� ���� stringbuffer ��ü
	private StringBuffer mOutStringBuffer;

	// ������� �����
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothAdapter mMCUBluetoothAdapter = null;

	// Member object for the chat services
	private BluetoothChatService mChatService = null;
	private BluetoothChatService mMCUService = null;

	private Button btnConnectAndroid;
	private Button btnConnectOpen;
	private Button btnConnectMCU;

	//	private Button btnCamera;

	private TextView mStateTextView;

	// Joolmera obj
	private Joolmera App;

	// NFC ����� Dialog
	private AlertDialog dialog; 

	/**
	 * NFC Constant
	 */
	private boolean mResumed = false;
	private boolean mWriteMode = false;
	NfcAdapter mNfcAdapter;
	int bluetoothAddressLength = 16;
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mWriteTagFilters;
	IntentFilter[] mNdefExchangeFilters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//�׼ǹ� ���� background�� ä���
		//ref. http://stackoverflow.com/questions/13726214/transparent-actionbar-custom-tabcolor
		//android.util.AndroidRuntimeException: requestFeature() must be called before adding content
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_main);

		/* init codes */
		initActionbar();
		initLayout();
		setMode(MODE_MENU);

		/* set NFC */
		setNFC();		

		/* bluetooth */
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		// ���� ������� ����͸� �޾ƿ�
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mMCUBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Joolmera �޾ƿ�
		App = (Joolmera) getApplicationContext();

		// ����Ͱ� null �̸� ��������� ���������ʴ� �����
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		//start rippliing.
		mRippleMaker.sendEmptyMessageDelayed(0, 2000);
	}


	private void initActionbar() {
		ab = getActionBar(); 
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		ab.setBackgroundDrawable(new ColorDrawable(0xAA97e9bf));
		ab.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));    
	}

	private void initLayout() {
		ivDots = new ImageView[2];
		ivDots[0] = (ImageView)findViewById(R.id.ivCircle1);
		ivDots[1] = (ImageView)findViewById(R.id.ivCircle2);

		viewPager = (ViewPager)findViewById(R.id.viewPager);
		adapter = new VpAdapter(ctx);
		viewPager.setAdapter(adapter);

	}


	/* 2�ʸ��� 2���� RippleView�� �����ư��� Ripple ȿ�� ���� */
	private Handler mRippleMaker = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if(rvAppMode != null && rvTriMode != null) {
				switch(msg.what){
				case 0:
					rvAppMode.animateRipple(rvAppMode.getWidth()/2, rvAppMode.getHeight()/2);
					break;

				case 1:
					rvTriMode.animateRipple(rvTriMode.getWidth()/2, rvTriMode.getHeight()/2);
					break;
				}
			}

			if(curMode == MODE_MENU)
				mRippleMaker.sendEmptyMessageDelayed(msg.what == 0 ? 1 : 0, 1000);
		}
	};


	private void setMode(int mode) {
		curMode = mode;

		ivDots[0].setImageResource(R.drawable.guide_img_page_off);
		ivDots[1].setImageResource(R.drawable.guide_img_page_off);

		switch(curMode) 
		{
		case MODE_MENU:
			setSubTitle("���ϴ� ��带 �����ϼ���.");
			viewPager.setCurrentItem(0);

			ivDots[0].setImageResource(R.drawable.guide_img_page_on);
			mRippleMaker.sendEmptyMessageDelayed(0, 2000);
			break;

		case MODE_APP:
			setSubTitle("Not connected");
			viewPager.setCurrentItem(1);
			ivDots[1].setImageResource(R.drawable.guide_img_page_on);
			mRippleMaker.removeMessages(0);
			mRippleMaker.removeMessages(1);
			break;

		case MODE_TRI:
			setSubTitle("Not connected");
			viewPager.setCurrentItem(1);
			ivDots[1].setImageResource(R.drawable.guide_img_page_on);
			mRippleMaker.removeMessages(0);
			mRippleMaker.removeMessages(1);
			break;

		default:
			//nop
			break;
		}
	}


	private void setSubTitle(String str) {
		switch(curMode) 
		{
		case MODE_MENU:
			setTitle("Tri-mera", str);
			viewPager.setCurrentItem(0);

			mRippleMaker.sendEmptyMessageDelayed(0, 2000);
			onMainMenu();
			break;

		case MODE_APP:
			setTitle("App mode", str);
			viewPager.setCurrentItem(1);
			onAppMode();
			break;

		case MODE_TRI:
			setTitle("Tri mode", str);
			viewPager.setCurrentItem(1);
			onTriMode();
			break;

		default:
			//nop
			break;
		}
	}

	private void setTitle(String main, String sub) {
		ab.setTitle(main);
		ab.setSubtitle(sub);
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// ��������� �������� �ʴٸ� �Ѷ�� ��û��
		if (!mBluetoothAdapter.isEnabled()) {
			// ������� �Ѷ�� ����Ʈ �ۼ�
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);

			// ����Ʈ�� �̿��� ��Ƽ��Ƽ ����! -> ���� ��Ƽ��Ƽ�� ������� �޾ƿ� �� ���� onPause() ���°� ��!
			// ����� �޾ƿ��� onActivityResult()�� ����ȴ�!
			// �񵿱� ��û�̹Ƿ� ������ �ٷ� �������� �Ѿ!
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			// ������ ���������� ä�ü��񽺰� ���� �ȵƴٸ� �����Ѵ�!
			if (mChatService == null || mMCUService == null)
				setupCamera();
		}
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == DataConstants.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}

		if (mMCUService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mMCUService.getState() == DataConstants.STATE_NONE) {
				// Start the Bluetooth chat services
				mMCUService.start();
			}
		}

		mResumed = true;
		// Sticky notes received from Android
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) { // �߰ߵ�����
			// �̺�Ʈ

			Log.e(TAG, "ACTION_NDEF_DISCOVERED ");
			NdefMessage[] messages = getNdefMessages(getIntent());
			byte[] payload = messages[0].getRecords()[0].getPayload(); // ������ �޴�
			// �κ�
			setNoteBody(new String(payload)); // ���� �ϰ���� �۾� �ϴ� �κ�
			setIntent(new Intent()); // Consume this intent.
		}
		enableNdefExchangeMode();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
		mNfcAdapter.disableForegroundNdefPush(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (mMCUService != null)
			mMCUService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	/***************************************************************************************************************
	 * ������.
	 ****************************************************************************************************************/

	//��ư���� ������
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.btnAppMode:
				setMode(MODE_APP);
				break;

			case R.id.btnTriMode:
				setMode(MODE_TRI);
				break;

			case R.id.btnSearchBluetooth:
				Intent serverIntentAndroid = null;
				serverIntentAndroid = new Intent(ctx,DeviceListActivity.class);
				startActivityForResult(serverIntentAndroid,REQUEST_CONNECT_DEVICE_ANDROID);
				break;

			case R.id.btnAllowBluetooth:
				ensureDiscoverable();
				break;

			case R.id.btnNFC:
				if (!mNfcAdapter.isEnabled()){
					Toast.makeText(getApplicationContext(), "NFC ����� Ȱ��ȭ �� �� ������ �������ּ���.", Toast.LENGTH_LONG).show();
					startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
				}else{
					Toast.makeText(getApplicationContext(), "��⿡ �±��ϸ� ������ ����˴ϴ�.", Toast.LENGTH_LONG).show();
				}
				break;

			case R.id.btnTripod:
				Intent serverIntentMCU = null;
				serverIntentMCU = new Intent(ctx,DeviceListActivity.class);
				startActivityForResult(serverIntentMCU,REQUEST_CONNECT_DEVICE_MCU);
				break;

			case R.id.btnControllerStart:
				if(curMode == MODE_APP ){
					String message = "Camera";
					mChatService.write(message.getBytes());
					// ��Ʈ�ѷ� ��Ƽ��Ƽ ȣ��
					Intent intent_controller = new Intent(ctx, ModeEasyControllerActivity.class);
					startActivity(intent_controller);
				}else if(curMode == MODE_TRI && mMCUService.getState() == DataConstants.STATE_CONNECTED){
					String message = "Camera";
					mChatService.write(message.getBytes());
					// ��Ʈ�ѷ� ��Ƽ��Ƽ ȣ��
					Intent intent_controller = new Intent(ctx, ModeHardControllerActivity.class);
					startActivity(intent_controller);
				}
				else{
					Toast.makeText(getApplicationContext(), "��Ʈ�ѷ����� �ﰢ�뿬���� �������ּ���", Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	};

	private void onMainMenu() {

	}


	private void onAppMode() {
		// �ﰢ�뿬���ư �����
		rvTriConnect.setVisibility(View.INVISIBLE);

	}


	private void onTriMode() {
		// �ﰢ�뿬���ư ���̰� �ϱ�
		rvTriConnect.setVisibility(View.VISIBLE);

	}



	public void setNFC() {
		Log.e(TAG, "SetNFC");
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// Handle all of our received NFC intents in this activity.
		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Intent filters for reading a note from a tag or exchanging over p2p.
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}
		mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

		// Intent filters for writing to a tag
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		mWriteTagFilters = new IntentFilter[] { tagDetected };

		mNfcAdapter.setNdefPushMessageCallback(this, this);
		mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

	}

	// ä���� ���ʿ��� ���� �ʱ�ȭ������
	private void setupCamera() {
		Log.d(TAG, "setupChat()");

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = App.getBluetoothChatService();
		mChatService.setHandler(mAndroidHandler);

		mMCUService = App.getBluetoothMCUService();
		mMCUService.setHandler(mMCUHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}


	// �ٸ� ��⿡�� �ڽ��� ��⸦ 300�ʵ��� ã�� �� �ְ���
	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendMessage(String message) {
		// ���� ���°� ������°� �ƴ϶�� �佺Ʈ�� ���!
		if (mChatService.getState() != DataConstants.STATE_CONNECTED) {
			Toast.makeText(this, "���� ����� ���°� �ƴմϴ�.", Toast.LENGTH_SHORT).show();
			return;
		}

		// if (mMCUService.getState() != DataConstants.STATE_CONNECTED) {
		// Toast.makeText(this, "�ﰢ��� ����� ���°� �ƴմϴ�.",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }

		// ���� �޼����� �ִٸ�
		if (message.length() > 0) {
			// �޼����� ����Ʈ�� �о��
			byte[] send = message.getBytes();
			// ���� ��ü�� �̿��Ͽ� ����!
			mChatService.write(send);
			mMCUService.write(send);

			// ��Ʈ������ �ʱ�ȭ
			mOutStringBuffer.setLength(0);
		}
	}


	// The Handler that gets information back from the BluetoothChatService
	private final Handler mAndroidHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case DataConstants.NDEF_SEND_COMPLETE:
				Log.e(TAG, "Hnadler Message 1 Complete");
				break;

			case DataConstants.MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case DataConstants.STATE_CONNECTED:
					setSubTitle(getString(R.string.title_connected_to, mConnectedDeviceName));
					if(rvControllerStart != null){
						rvControllerStart.setVisibility(View.VISIBLE);
					}
					break;
				case DataConstants.STATE_CONNECTING:
					setSubTitle(getString(R.string.title_connecting));
					break;
				case DataConstants.STATE_LISTEN:
				case DataConstants.STATE_NONE:
					setSubTitle(getString(R.string.title_not_connected));
					if(rvControllerStart != null){
						rvControllerStart.setVisibility(View.GONE);
					}
					break;
				}
				break;
			case DataConstants.MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				// Toast.makeText(ModeHardActivity.this, "mAndroidHandler ����Ʈ",
				// Toast.LENGTH_SHORT).show();

				break;
			case DataConstants.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				// Toast.makeText(ModeHardActivity.this, "mAndroidHandler ����",
				// Toast.LENGTH_SHORT).show();

				if (readMessage.equals("Camera")) {
					// ī�޶� ��Ƽ��Ƽ ȣ��
					Intent intent_camera = new Intent(ctx,CameraActivity.class);
					// intent_camera.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					// intent_camera.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent_camera);
				}
				break;
			case DataConstants.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(
						DataConstants.DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case DataConstants.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(DataConstants.TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};


	// The Handler that gets information back from the BluetoothChatService
	private final Handler mMCUHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DataConstants.MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case DataConstants.STATE_CONNECTED:
					setSubTitle(getString(R.string.title_connected_to,
							mConnectedMCUName));
					String message = "Camera";
					mChatService.write(message.getBytes());

					// ��Ʈ�ѷ� ��Ƽ��Ƽ ȣ��
					Intent intent_controller = new Intent(ctx,ModeHardControllerActivity.class);
					// intent_controller.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					// intent_controller.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent_controller);

					break;
				case DataConstants.STATE_CONNECTING:
					setSubTitle(getString(R.string.title_connecting));
					break;
				case DataConstants.STATE_LISTEN:
				case DataConstants.STATE_NONE:
					setSubTitle(getString(R.string.title_not_connected));
					break;
				}
				break;
			case DataConstants.MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				// Toast.makeText(ModeHardActivity.this, "mMCUHandler ����Ʈ!!",
				// Toast.LENGTH_SHORT).show();

				break;
			case DataConstants.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				// Toast.makeText(ModeHardActivity.this, "mMCUHandler ����!!",
				// Toast.LENGTH_SHORT).show();

				break;
			case DataConstants.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedMCUName = msg.getData().getString(
						DataConstants.MCU_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedMCUName, Toast.LENGTH_SHORT)
						.show();
				break;
			case DataConstants.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(DataConstants.TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_MCU:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectMCU(data);
			}
			break;
		case REQUEST_CONNECT_DEVICE_ANDROID:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectAndroid(data);
			}
			break;

			// ����ڰ� ���� �Ѵ°��� ���� ���� ��!
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupCamera();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}


	private void connectAndroid(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device);
	}

	private void connectMCU(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mMCUBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mMCUService.connect(device);
	}

	/**
	 * NFC ADD
	 */

	@Override
	protected void onNewIntent(Intent intent) {
		// NDEF exchange mode
		if (!mWriteMode
				&& NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			NdefMessage[] messages = getNdefMessages(intent);
			byte[] payload = messages[0].getRecords()[0].getPayload(); // ������ �޴�
			// �κ�
			setNoteBody(new String(payload));
		}

		// Tag writing mode
		if (mWriteMode
				&& NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			writeTag(getNoteAsNdef(), detectedTag);
		}
	}

	private void setNoteBody(String body) {
		Log.e(TAG, "body : " + body);
		// 12 + 5 length;
		BluetoothDevice device;


		if(body.contains("MCU")){
			String temp = body.substring(3);
			Log.e(TAG,"subString body : " + temp +"(MCU)");
			device = mBluetoothAdapter.getRemoteDevice(temp);
			crateDialog("������� ����", "�ﰢ��� �����Ͻðڽ��ϱ�?",device);

		}else{
			Log.e(TAG,"subString body :" + body+"(Android)");
			device = mBluetoothAdapter.getRemoteDevice(body);
			mChatService.connect(device);
		}

	}

	public void crateDialog(String title, String message, final BluetoothDevice device) {

		if (dialog != null) {
			if (dialog.isShowing())
				dialog.dismiss();
			dialog = null;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title)
		// ���� ����
		.setMessage(message)
		// �޼��� ����
		.setCancelable(false)
		// �ڷ� ��ư Ŭ���� ��� ���� ����
		.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
			// Ȯ�� ��ư Ŭ���� ����
			public void onClick(DialogInterface dialog, int whichButton) {
				mMCUService.connect(device);
				dialog.cancel();
			}
		})
		.setNegativeButton("���", new DialogInterface.OnClickListener() {
			// ��� ��ư Ŭ���� ����
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});

		dialog = builder.create(); // �˸�â ��ü ����
		dialog.show(); // �˸�â ����

		dialog = builder.create();

	}

	private NdefMessage getNoteAsNdef() {
		byte[] textBytes = null;
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] { textRecord });
	}

	NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		Log.e(TAG, "getNdefMessages");
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			Log.d(TAG, "Unknown intent.");
			finish();
		}
		return msgs;
	}

	private void enableNdefExchangeMode() {
		mNfcAdapter.enableForegroundNdefPush(MainActivity.this,	getNoteAsNdef());
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mNdefExchangeFilters, null);
	}

	private void disableNdefExchangeMode() {
		mNfcAdapter.disableForegroundNdefPush(this);
		mNfcAdapter.disableForegroundDispatch(this);
	}

	private void enableTagWriteMode() {
		mWriteMode = true;
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		mWriteTagFilters = new IntentFilter[] { tagDetected };
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mWriteTagFilters, null);
	}

	private void disableTagWriteMode() {
		mWriteMode = false;
		mNfcAdapter.disableForegroundDispatch(this);
	}

	boolean writeTag(NdefMessage message, Tag tag) {
		int size = message.toByteArray().length;

		try {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				if (!ndef.isWritable()) {
					toast("Tag is read-only.");
					return false;
				}
				if (ndef.getMaxSize() < size) {
					toast("Tag capacity is " + ndef.getMaxSize()
							+ " bytes, message is " + size + " bytes.");
					return false;
				}

				ndef.writeNdefMessage(message);
				toast("Wrote message to pre-formatted tag.");
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						toast("Formatted tag and wrote message");
						return true;
					} catch (IOException e) {
						toast("Failed to format tag.");
						return false;
					}
				} else {
					toast("Tag doesn't support NDEF.");
					return false;
				}
			}
		} catch (Exception e) {
			toast("Failed to write tag");
		}

		return false;
	}

	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * NDEF Send
	 */

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {

		Log.e(TAG, "createNdefMessage");

		NdefMessage message = new NdefMessage(
				new NdefRecord[] { createTextRecord(mBluetoothAdapter
						.getAddress()), });

		return message;

	}

	public NdefRecord createTextRecord(String text) {

		Log.e(TAG, "createTextRecord:" + text);
		byte[] data = byteEncoding(text);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
				new byte[0], data);

	}

	public byte[] byteEncoding(String text) {
		Charset utfEncoding = Charset.forName("UTF-8");
		byte[] textBytes = text.getBytes(utfEncoding);
		byte[] data = new byte[textBytes.length];
		System.arraycopy(textBytes, 0, data, 0, textBytes.length);
		return data;
	}

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		mAndroidHandler.obtainMessage(DataConstants.NDEF_SEND_COMPLETE)
		.sendToTarget();
	}

	/****************************************************************************************************************/













	private class VpAdapter extends PagerAdapter
	{
		private Context ctx = null;
		private LayoutInflater inflater;

		public VpAdapter(Context context) {
			ctx = context;
			inflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public Object instantiateItem(View pager, int pos)
		{
			View view = null;
			switch(pos) {
			case 0:
				view = inflater.inflate(R.layout.item_btns, null);

				rvAppMode = (RippleView)view.findViewById(R.id.rvAppMode);
				rvTriMode = (RippleView)view.findViewById(R.id.rvTriMode);
				view.findViewById(R.id.btnAppMode).setOnClickListener(mOnClickListener);
				view.findViewById(R.id.btnTriMode).setOnClickListener(mOnClickListener);
				break;

			case 1:
				view = inflater.inflate(R.layout.item_bluetooth, null);
				view.findViewById(R.id.btnSearchBluetooth).setOnClickListener(mOnClickListener);
				view.findViewById(R.id.btnAllowBluetooth).setOnClickListener(mOnClickListener);
				view.findViewById(R.id.btnNFC).setOnClickListener(mOnClickListener);
				view.findViewById(R.id.btnTripod).setOnClickListener(mOnClickListener);
				view.findViewById(R.id.btnControllerStart).setOnClickListener(mOnClickListener);
				rvTriConnect = (RippleView) view.findViewById(R.id.rv_camara_connect);
				rvTriConnect.setOnClickListener(mOnClickListener);

				rvControllerStart = (RippleView) view.findViewById(R.id.rv_controller_start);
				rvControllerStart.setOnClickListener(mOnClickListener);

				break;
			}

			((ViewPager)pager).addView(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			return view;
		}

		// �� ��ü ����.
		@Override
		public void destroyItem(View pager, int position, Object view) {
			((ViewPager)pager).removeView((View) view);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			return false;
		}

		return true;
	}


	public void onBackPressed() {
		// TODO Auto-generated method stub
		switch(curMode) {
		case MODE_MENU:
			super.onBackPressed();
			break;

		case MODE_APP:
		case MODE_TRI:
			setMode(MODE_MENU);
			break;
		}
	} 
}
