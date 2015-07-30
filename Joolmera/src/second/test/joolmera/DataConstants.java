package second.test.joolmera;

import android.util.Log;

public class DataConstants {

	/**
	 * Panorama 관련
	 */
	public static boolean flag_isPanoramaselect=false;
	public static int panorama_count = 1 ;
	
	// 현재 파노라마 모드 상태
	public static int CurrentPanoramaMode = -1;
	public static int CurrentPanoramaModeShotNumber = -1;
	
	
	public static final int FEATURE_SURF = 1; //default
	public static final int FEATURE_SIFT = 2;
    
	public static final int MODE_HORIZONTAL = 1; // default		//가로
	public static final int MODE_VERTICAL = 2;					//세로
	public static final int MODE_SPECIAL = 3;					//비장의 무기
    
	public static final int HORIZONTAL_SHORT = 6;				//가로모드    		// 얘네는 -1 장 찍힘 ex) 7 ->  6장 찍어서 파노라마 생성
	public static final int HORIZONTAL_LONG = 6;
	public static final int VERTICAL_SHORT = 8;				//세로
	public static final int VERTICAL_LONG = 8;
	
	
	/**
	 * 촬영된 사진이 전송된 상태(컨트롤러에 표시)인지 표시
	 */
	public static boolean flag_isImageTransfer = false;

	/**
	 * 사진전송중인지 플래그!
	 */
	public static boolean flag_isImageTransfer_ing = false;

	/**
	 * Bluetooth Service 관련
	 */

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0;       // we're doing nothing
	public static final int STATE_LISTEN = 1;     // now listening for incoming connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 3;  // now connected to a remote device





	/**
	 * 핸들러 관련
	 */

	// 블루투스 서비스에서 핸들러에 보내온 메세지 종류
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int NDEF_SEND_COMPLETE = 10;
	
	public static final int MESSAGE_IMAGE = 6;
	public static final int MESSAGE_STREAM = 7;

	// 블루투스 한번에 보내는 바이트 크기
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
