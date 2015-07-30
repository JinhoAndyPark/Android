package second.test.joolmera;

import android.util.Log;

public class DataConstants {

	/**
	 * Panorama ����
	 */
	public static boolean flag_isPanoramaselect=false;
	public static int panorama_count = 1 ;
	
	// ���� �ĳ�� ��� ����
	public static int CurrentPanoramaMode = -1;
	public static int CurrentPanoramaModeShotNumber = -1;
	
	
	public static final int FEATURE_SURF = 1; //default
	public static final int FEATURE_SIFT = 2;
    
	public static final int MODE_HORIZONTAL = 1; // default		//����
	public static final int MODE_VERTICAL = 2;					//����
	public static final int MODE_SPECIAL = 3;					//������ ����
    
	public static final int HORIZONTAL_SHORT = 6;				//���θ��    		// ��״� -1 �� ���� ex) 7 ->  6�� �� �ĳ�� ����
	public static final int HORIZONTAL_LONG = 6;
	public static final int VERTICAL_SHORT = 8;				//����
	public static final int VERTICAL_LONG = 8;
	
	
	/**
	 * �Կ��� ������ ���۵� ����(��Ʈ�ѷ��� ǥ��)���� ǥ��
	 */
	public static boolean flag_isImageTransfer = false;

	/**
	 * �������������� �÷���!
	 */
	public static boolean flag_isImageTransfer_ing = false;

	/**
	 * Bluetooth Service ����
	 */

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0;       // we're doing nothing
	public static final int STATE_LISTEN = 1;     // now listening for incoming connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 3;  // now connected to a remote device





	/**
	 * �ڵ鷯 ����
	 */

	// ������� ���񽺿��� �ڵ鷯�� ������ �޼��� ����
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int NDEF_SEND_COMPLETE = 10;
	
	public static final int MESSAGE_IMAGE = 6;
	public static final int MESSAGE_STREAM = 7;

	// ������� �ѹ��� ������ ����Ʈ ũ��
	public static final int sendingSize = 900;

	public static final String TOAST = "toast";

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String MCU_NAME = "mcu_name";

	
	
	public static void showData(byte[] data, String tag){
		String str = "";

		for(int i=0; i<data.length; i++){
			str+=String.format("%02X ",data[i]);
		}
		Log.d(tag,str);
	}

	public static  byte[] intToByteArray(int value) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte)(value >> 24);
		byteArray[1] = (byte)(value >> 16);
		byteArray[2] = (byte)(value >> 8);
		byteArray[3] = (byte)(value);
		return byteArray;
	}
	
	public static  int byteArrayToInt(byte bytes[], int offset) {
	return ((((int)bytes[offset] & 0xff) << 24) |
			(((int)bytes[offset+1] & 0xff) << 16) |
			(((int)bytes[offset+2] & 0xff) << 8) |
			(((int)bytes[offset+3] & 0xff)));
} 

}
