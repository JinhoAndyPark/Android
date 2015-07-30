package second.test.joolmera;

import android.app.Application;

public class Joolmera extends Application{	//질문 application

	public static Joolmera application;
	
	private static BluetoothChatService mChatService = null;
	
	private static BluetoothChatService mMCUService = null;

	@Override
	public void onCreate() {
		//�� ���� �ʱ�ȭ
		mChatService = new BluetoothChatService();
		mMCUService = new BluetoothChatService();
		application = this;
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public BluetoothChatService getBluetoothChatService() {

		if (mChatService == null) {
			mChatService = new BluetoothChatService();
		}
		return mChatService;
	}
	
	public BluetoothChatService getBluetoothMCUService() {

		if (mMCUService == null) {
			mMCUService = new BluetoothChatService();
		}
		return mMCUService;
	}
}
