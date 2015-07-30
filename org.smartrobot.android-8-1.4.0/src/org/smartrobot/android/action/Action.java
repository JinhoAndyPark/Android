/*
 * Copyright (C) 2011 SmartRobot.ORG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartrobot.android.action;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.roboid.robot.Device.DeviceDataChangedListener;
import org.roboid.robot.impl.RoboidImpl;
import org.smartrobot.android.ipc.ac;
import org.smartrobot.android.ipc.dc;
import org.smartrobot.android.ipc.dr;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

/**
 * <p>�׼� �ν��Ͻ��� ��ų� ����, ����, ����ϴ� �޼ҵ带 �����Ѵ�.
 * </p>
 * <pre class="prettyprint">
 * public SampleActivity extends Activity
 {
     private Action mAction;

     protected void onCreate(Bundle savedInstanceState)
     {
         super.onCreate(savedInstanceState);
         mAction = Action.obtain(this, Action.Microphone.ID);
     }

     protected void onDestroy()
     {
         super.onDestroy();
         mAction.dispose();
     }

     protected void onStart()
     {
         super.onStart();
         mAction.activate();
     }

     protected void onStop()
     {
         super.onStop();
         mAction.deactivate();
     }
 }</pre>
 * 
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see org.roboid.robot.Roboid Roboid
 * @see org.roboid.robot.Device Device
 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
 */
public abstract class Action extends RoboidImpl
{
	private static final int STATE_NONE = 0;
	/**
	 * <p>�׼��� �غ�� ���¸� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 1
	 * </ul>
	 */
	public static final int STATE_PREPARED = 1;
	/**
	 * <p>�׼��� ���۵� ���¸� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 2
	 * </ul>
	 */
	public static final int STATE_ACTIVATED = 2;
	/**
	 * <p>�׼��� ������ ���¸� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 3
	 * </ul>
	 */
	public static final int STATE_DEACTIVATED = 3;
	/**
	 * <p>�׼��� ���� ���¸� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 4
	 * </ul>
	 */
	public static final int STATE_DISPOSED = 4;
	
	private static final int ERROR_NONE = 0;
	/**
	 * <p>�׼��� ID�� �߸� �Ǿ����� ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -1
	 * </ul>
	 */
	public static final int ERROR_INVALID_ID = -1;
	/**
	 * <p>���ؽ�Ʈ�� �߸� �Ǿ����� ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -2
	 * </ul>
	 */
	public static final int ERROR_INVALID_CONTEXT = -2;
	/**
	 * <p>�׼� �ν��Ͻ��� ��ų� ����, �����ϴ� ������ �߸� �Ǿ����� ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -3
	 * </ul>
	 */
	public static final int ERROR_ILLEGAL_STATE = -3;
	/**
	 * <p>�������� �ʴ� �׼����� ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -4
	 * </ul>
	 */
	public static final int ERROR_NOT_SUPPORTED = -4;
	/**
	 * <p>���� ������ �׼� ������ �����Ͽ����� ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -5
	 * </ul>
	 */
	public static final int ERROR_SECURITY = -5;
	
	/**
	 * <p>���� ����ũ�� ���� ������ �ν��ϴ� �׼��� �� ID�� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
	 * <p>Action.VoiceRecognition�� 7���� Ŀ�ǵ� ����̽��� 1���� ���� ����̽�, 1���� �̺�Ʈ ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� ��Ʈ�� �迭 �Ǵ� ������ �迭, �Ǽ��� �迭�� ����Ǿ� �ִ�.
	 * <p>Action.VoiceRecognition�� ���� �ν��� �Ϸ�Ǹ� {@link Action.OnCompletedListener#onCompleted(Action) onCompleted(Action)} �޼ҵ带 ȣ���Ѵ�.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device deviceLanguageModel = action.findDeviceById(Action.VoiceRecognition.COMMAND_LANGUAGE_MODEL);
     deviceLanguageModel.writeString("free_form"); // ��� ���� ����.

     Device deviceLanguage = action.findDeviceById(Action.VoiceRecognition.COMMAND_LANGUAGE);
     deviceLanguage.writeString("ko"); // �������� ��� �ڵ带 ����.

     Device deviceCountry = action.findDeviceById(Action.VoiceRecognition.COMMAND_COUNTRY);
     deviceCountry.writeString("KR"); // �������� ���� �ڵ带 ����.

     Device deviceVariant = action.findDeviceById(Action.VoiceRecognition.COMMAND_VARIANT);
     deviceVariant.writeString("POSIX"); // �������� ����(���) �ڵ带 ����.

     Device deviceVisibility = action.findDeviceById(Action.VoiceRecognition.COMMAND_VISIBILITY);
     deviceVisibility.write(1); // ��ȭâ�� ��Ÿ���� �Ѵ�.

     Device devicePrompt = action.findDeviceById(Action.VoiceRecognition.COMMAND_PROMPT);
     devicePrompt.writeString("�����ϼ���"); // ��ȭâ�� ǥ���� ���ڿ��� ����.

     Device deviceMaxResults = action.findDeviceById(Action.VoiceRecognition.COMMAND_MAX_RESULTS);
     deviceMaxResults.write(5); // �ν� ��� �ĺ��� �ִ� 5���� �Ѵ�.

     Device deviceMicLevel = action.findDeviceById(Action.VoiceRecognition.SENSOR_MIC_LEVEL);
     float micLevel = deviceMicLevel.readFloat(); // ����ũ�� �Ҹ� ũ�� ���� �д´�.

     Device deviceText = action.findDeviceById(Action.VoiceRecognition.EVENT_TEXT);
     if(deviceText.e())
     {
         String[] results = new String[5];
         deviceText.readString(results); // �ν� ����� �д´�.
     }
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     float micLevel;
     String[] results;
     switch(device.getId())
     {
     case Action.VoiceRecognition.SENSOR_MIC_LEVEL: // ����ũ�� �Ҹ� ũ�� ���� ���ŵǾ���.
         micLevel = ((float[])values)[0]; // ����ũ�� �Ҹ� ũ�� ���� ��´�.
         break;
     case Action.VoiceRecognition.EVENT_TEXT: // ���� �ν� ��� ���� ���ŵǾ���.
         results = (String[])values; // ���� �ν� ��� ���� ��´�.
         break;
     }
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see Action.OnCompletedListener
	 * @see org.roboid.robot.Device Device
	 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
	 */
	public final class VoiceRecognition
	{
		/**
		 * <p>VoiceRecognition �׼��� �� ID�� ��Ÿ���� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: "org.smartrobot.android.action.voicerecognition"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.voicerecognition";
		/**
		 * <p>��� �� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>��� �� Ŀ�ǵ� ����̽��� �����ʹ� ���� �ν��� ��� ���� ��Ÿ����.
		 * "free_form" �Ǵ� "web_search" ���� �ϳ��̴�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40100000
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>�ʱ� ��: "free_form"
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_LANGUAGE_MODEL = 0x40100000;
		/**
		 * <p>��� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>��� Ŀ�ǵ� ����̽��� �����ʹ� ���� �ν��� �����Ͽ��� ��� �ڵ带 ��Ÿ����.
		 * �������� ������ �⺻ ��� �ڵ带 ����Ѵ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40100001
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>�ʱ� ��: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_LANGUAGE = 0x40100001;
		/**
		 * <p>���� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���� Ŀ�ǵ� ����̽��� �����ʹ� ���� �ν��� �����Ͽ��� ���� �ڵ带 ��Ÿ����.
		 * �������� ������ �⺻ ���� �ڵ带 ����Ѵ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40100002
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>�ʱ� ��: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_COUNTRY = 0x40100002;
		/**
		 * <p>����(���) Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>����(���) Ŀ�ǵ� ����̽��� �����ʹ� ���� �ν��� �����Ͽ��� ����(���) �ڵ带 ��Ÿ����.
		 * �������� ������ �⺻ ����(���) �ڵ带 ����Ѵ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40100003
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>�ʱ� ��: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_VARIANT = 0x40100003;
		/**
		 * <p>���̱� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���̱� Ŀ�ǵ� ����̽��� �����ʹ� ���� �ν� ��ȭâ�� ��Ÿ���� ���� �������� ���θ� ��Ÿ����.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40100004
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: 0 �Ǵ� 1 (0: ��ȭâ ���߱�, 1: ��ȭâ ���̱�)
		 *             <li>�ʱ� ��: 1
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_VISIBILITY = 0x40100004;
		/**
		 * <p>������Ʈ Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>������Ʈ Ŀ�ǵ� ����̽��� �����ʹ� ���� �ν� ��ȭâ�� ǥ���� ���ڿ��� ��Ÿ����.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40100005
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>�ʱ� ��: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_PROMPT = 0x40100005;
		/**
		 * <p>�ִ� ��� �� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>�ִ� ��� �� Ŀ�ǵ� ����̽��� �����ʹ� ���� �ν� ��� �ĺ��� �ִ� ������ ��Ÿ����.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40100006
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: 0 ~ 255
		 *             <li>�ʱ� ��: 5
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_MAX_RESULTS = 0x40100006;
		/**
		 * <p>���� ���� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���� ���� ����̽��� �����ʹ� ���� ����ũ�� ���� �Էµ� �Ҹ��� ũ��(RMS)�� ���ú��� ��Ÿ����.
		 * �Ҹ��� Ŀ������ ���� Ŀ����.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40100007
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: float [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: -100 ~ 100 [dB]
		 *             <li>�ʱ� ��: -100
		 *         </ul>
		 * </ul>
		 */
		public static final int SENSOR_MIC_LEVEL = 0x40100007;
		/**
		 * <p>���� �̺�Ʈ ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���� �̺�Ʈ ����̽��� �����ʹ� ���� �νĵ� ��� ���ڿ��� ��Ʈ�� �迭�� ��Ÿ����.
		 * ��Ʈ�� �迭�� ũ��� ���� �ν� ��� �ĺ��� �ִ� ������ ������ ���� ����.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40100008
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: -1
		 *             <li>�ʱ� ��: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int EVENT_TEXT = 0x40100008;

		/**
		 * <p>����ũ �Է¿��� ������ ���۵� ���¸� ��Ÿ���� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 101
		 * </ul>
		 */
		public static final int STATE_BEGINNING_OF_SPEECH = 101;
		/**
		 * <p>����ũ �Է¿��� ������ ���� ���¸� ��Ÿ���� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 103
		 * </ul>
		 */
		public static final int STATE_END_OF_SPEECH = 103;
		/**
		 * <p>����ũ �Է����� ������ �޾Ƶ��� �غ� �� ���¸� ��Ÿ���� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 105
		 * </ul>
		 */
		public static final int STATE_READY_FOR_SPEECH = 105;

		/**
		 * <p>��Ʈ��ũ �ð� �ʰ��� ��Ÿ���� ���� �ڵ� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: -101
		 * </ul>
		 */
		public static final int ERROR_NETWORK_TIMEOUT = -101;
		/**
		 * <p>��Ʈ��ũ�� ������ ������ ��Ÿ���� ���� �ڵ� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: -102
		 * </ul>
		 */
		public static final int ERROR_NETWORK = -102;
		/**
		 * <p>����ũ �Է¿� ������ ������ ��Ÿ���� ���� �ڵ� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: -103
		 * </ul>
		 */
		public static final int ERROR_AUDIO = -103;
		/**
		 * <p>���� �ν� ���� ���� ������ ������ ��Ÿ���� ���� �ڵ� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: -104
		 * </ul>
		 */
		public static final int ERROR_SERVER = -104;
		/**
		 * <p>���ø����̼� ���� ������ ������ ��Ÿ���� ���� �ڵ� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: -105
		 * </ul>
		 */
		public static final int ERROR_CLIENT = -105;
		/**
		 * <p>�־��� �ð� ���� ������ �Է����� �ʾ����� ��Ÿ���� ���� �ڵ� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: -106
		 * </ul>
		 */
		public static final int ERROR_SPEECH_TIMEOUT = -106;
		/**
		 * <p>�ν� ����� ������ ��Ÿ���� ���� �ڵ� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: -107
		 * </ul>
		 */
		public static final int ERROR_NO_MATCH = -107;
		/**
		 * <p>ó���� ���� �����Ͱ� �ʹ� ���� �Էµ� ���� �����͸� ó������ ������ ��Ÿ���� ���� �ڵ� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: -108
		 * </ul>
		 */
		public static final int ERROR_RECOGNIZER_BUSY = -108;
	}
	
	/**
	 * <p>���� ����ũ�� ���� ���� ���� �����ϴ� �׼��� �� ID�� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
	 * <p>Action.Microphone�� 1���� ���� ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� �Ǽ��� �迭�� ����Ǿ� �ִ�.
	 * <p>Action.Microphone�� {@link Action#activate() activate()} �޼ҵ�� �����Ͽ� {@link Action#deactivate() deactivate()} �޼ҵ�� ������ ������ ����ȴ�.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device deviceLevel = action.findDeviceById(Action.Microphone.SENSOR_LEVEL); // ���� ���� ����̽��� ��´�.
     float level = deviceLevel.readFloat(); // ���� ���� �д´�.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     float level;
     switch(device.getId())
     {
     case Action.Microphone.SENSOR_LEVEL: // ���� ���� ���ŵǾ���.
         level = ((float[])values)[0]; // ���� ���� ��´�.
         break;
     }
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see org.roboid.robot.Device Device
	 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
	 */
	public final class Microphone
	{
		/**
		 * <p>Microphone �׼��� �� ID�� ��Ÿ���� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: "org.smartrobot.android.action.microphone"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.microphone";
		/**
		 * <p>���� ���� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���� ���� ����̽��� �����ʹ� ���� ����ũ�� ���� �Էµ� �Ҹ��� �Ŀ��� ���ú��� ��Ÿ����.
		 * �Ŀ��� Ŀ������ ���� Ŀ����.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40200000
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: float [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: -100 ~ 0 [dB]
		 *             <li>�ʱ� ��: -100
		 *         </ul>
		 * </ul>
		 */
		public static final int SENSOR_LEVEL = 0x40200000;
	}
	
	/**
	 * <p>���� ����ũ�� �Է¹��� �Ҹ��� �κ��� ����Ŀ�� �������� �׼��� �� ID�� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
	 * <p>Action.WalkieTalkie�� 1���� ������ ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� ������ �迭�� ����Ǿ� �ִ�.
	 * <p>Action.WalkieTalkie�� {@link Action#activate() activate()} �޼ҵ�� �����Ͽ� {@link Action#deactivate() deactivate()} �޼ҵ�� ������ ������ ����ȴ�.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device deviceSensitivity = action.findDeviceById(Action.WalkieTalkie.EFFECTOR_SENSITIVITY);
     deviceSensitivity.write(20); // ����ũ ������ 20���� �Ѵ�.
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see org.roboid.robot.Device Device
	 */
	public final class WalkieTalkie
	{
		/**
		 * <p>WalkieTalkie �׼��� �� ID�� ��Ÿ���� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: "org.smartrobot.android.action.walkietalkie"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.walkietalkie";
		/**
		 * <p>���� ������ ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���� ������ ����̽��� �����ʹ� ���� ����ũ�� ���� �ԷµǴ� �Ҹ��� ������ ��Ÿ����.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40300000
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: 0 ~ 100 [%]
		 *             <li>�ʱ� ��: 20
		 *         </ul>
		 * </ul>
		 */
		public static final int EFFECTOR_SENSITIVITY = 0x40300000;
	}
	
	/**
	 * <p>���� ������ �߻���Ű�� �׼��� �� ID�� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
	 * <p>Action.Vibration�� 3���� Ŀ�ǵ� ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� ������ �迭�� ����Ǿ� �ִ�.
	 * <p>Action.Vibration�� {@link Action#activate() activate()} �޼ҵ�� �����Ͽ� {@link Action#deactivate() deactivate()} �޼ҵ�� ������ ������ ����ȴ�.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device deviceTime = action.findDeviceById(Action.Vibration.COMMAND_TIME);
     deviceTime.write(1000); // 1�ʰ� ������ ����Ѵ�.

     Device devicePattern = action.findDeviceById(Action.Vibration.COMMAND_PATTERN);
     int[] pattern = new int[] { 1000, 2000, 3000, 4000, 5000, 6000 }; // 1�� �Ŀ� 2�ʰ� ����, 3�� ���� 4�ʰ� ����, 5�� ���� 6�ʰ� ����
      devicePattern.write(pattern); // ���� ������ ����.

     Device deviceRepeat = action.findDeviceById(Action.Vibration.COMMAND_REPEAT);
     deviceRepeat.write(2); // ���� ������ �ε��� 2���� �������� �ݺ��Ѵ�.
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see org.roboid.robot.Device Device
	 */
	public final class Vibration
	{
		/**
		 * <p>Vibration �׼��� �� ID�� ��Ÿ���� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: "org.smartrobot.android.action.vibration"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.vibration";
		/**
		 * <p>�ð� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>�ð� Ŀ�ǵ� ����̽��� �����ʹ� ������ ���� �ð�(ms)�� ��Ÿ����.
		 * �ð� Ŀ�ǵ� ����̽��� �����͸� ���� ���� Ŀ�ǵ� ����̽��� �ݺ� Ŀ�ǵ� ����̽��� �����ʹ� ���õȴ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40400000
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: 0 ~ 65535 [ms]
		 *             <li>�ʱ� ��: 0
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_TIME = 0x40400000;
		/**
		 * <p>���� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���� Ŀ�ǵ� ����̽��� �����ʹ� ������ �Ѱų� ���� �ð� �������� ������ ������ ��Ÿ����.
		 * ������ ǥ���ϴ� ������ �迭���� ù ��° ���� ������ �ѱ� ���� ��ٸ��� �ð�(ms)�̴�.
		 * �� ��° ������ ������ �� ���¸� �����ϴ� �ð�(ms)�� ������ �� ���¸� �����ϴ� �ð�(ms)�� ������ �Է��Ѵ�.
		 * ���� Ŀ�ǵ� ����̽��� �����͸� ���� �ð� Ŀ�ǵ� ����̽��� �����ʹ� ���õȴ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40400001
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: -1
		 *             <li>���� ����: 0 ~ 65535 [ms]
		 *             <li>�ʱ� ��: 0
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_PATTERN = 0x40400001;
		/**
		 * <p>�ݺ� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>�ݺ� Ŀ�ǵ� ����̽��� �����ʹ� ���� ������ ��Ÿ���� ������ �迭���� �ݺ��� �κ��� ���� �ε����� ��Ÿ����.
		 * �ݺ����� ���� ��쿡�� -1�� �Է��ϸ� �ȴ�.
		 * �ݺ� Ŀ�ǵ� ����̽��� �����͸� ���� �ð� Ŀ�ǵ� ����̽��� �����ʹ� ���õȴ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40400002
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: -1 ~ 32767
		 *             <li>�ʱ� ��: -1
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_REPEAT = 0x40400002;
	}
	
	/**
	 * <p>������ �������� ����ϴ� �׼��� �� ID�� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
	 * <p>Action.Tts�� 7���� Ŀ�ǵ� ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� ��Ʈ�� �迭 �Ǵ� ������ �迭�� ����Ǿ� �ִ�.
	 * <p>Action.Tts�� ���� �ռ� ����� �Ϸ�Ǹ� {@link Action.OnCompletedListener#onCompleted(Action) onCompleted(Action)} �޼ҵ带 ȣ���Ѵ�.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device deviceLanguage = action.findDeviceById(Action.Tts.COMMAND_LANGUAGE);
     deviceLanguage.writeString("ko"); // �������� ��� �ڵ带 ����.

     Device deviceCountry = action.findDeviceById(Action.Tts.COMMAND_COUNTRY);
     deviceCountry.writeString("KR"); // �������� ���� �ڵ带 ����.

     Device deviceVariant = action.findDeviceById(Action.Tts.COMMAND_VARIANT);
     deviceVariant.writeString("POSIX"); // �������� ����(���) �ڵ带 ����.

     Device devicePitch = action.findDeviceById(Action.Tts.COMMAND_PITCH);
     devicePitch.write(100); // �� ���� ���� ����.

     Device deviceSpeechRate = action.findDeviceById(Action.Tts.COMMAND_SPEECH_RATE);
     deviceSpeechRate.write(100); // ���� ��� �ӵ� ���� ����.

     Device deviceText = action.findDeviceById(Action.Tts.COMMAND_TEXT);
     deviceText.writeString("�ȳ��ϼ���"); // ���� �ռ��� ���ڿ��� ����.
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see Action.OnCompletedListener
	 * @see org.roboid.robot.Device Device
	 */
	public final class Tts
	{
		/**
		 * <p>Tts �׼��� �� ID�� ��Ÿ���� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: "org.smartrobot.android.action.tts"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.tts";
		/**
		 * <p>���� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���� Ŀ�ǵ� ����̽��� �����ʹ� TTS ������ ��Ű�� �̸��� ��Ÿ����.
		 * �������� ������ �⺻ TTS ������ ����Ѵ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40500000
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>�ʱ� ��: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_ENGINE = 0x40500000;
		/**
		 * <p>��� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>��� Ŀ�ǵ� ����̽��� �����ʹ� TTS�� �����Ͽ��� ��� �ڵ带 ��Ÿ����.
		 * �������� ������ �⺻ ��� �ڵ带 ����Ѵ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40500001
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>�ʱ� ��: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_LANGUAGE = 0x40500001;
		/**
		 * <p>���� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���� Ŀ�ǵ� ����̽��� �����ʹ� TTS�� �����Ͽ��� ���� �ڵ带 ��Ÿ����.
		 * �������� ������ �⺻ ���� �ڵ带 ����Ѵ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40500002
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>�ʱ� ��: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_COUNTRY = 0x40500002;
		/**
		 * <p>����(���) Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>����(���) Ŀ�ǵ� ����̽��� �����ʹ� TTS�� �����Ͽ��� ����(���) �ڵ带 ��Ÿ����.
		 * �������� ������ �⺻ ����(���) �ڵ带 ����Ѵ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * <ul>
		 *     <li>��� ��: 0x40500003
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>�ʱ� ��: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_VARIANT = 0x40500003;
		/**
		 * <p>�� ���� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>�� ���� Ŀ�ǵ� ����̽��� �����ʹ� ��µǴ� ������ �� ���̸� ��Ÿ����.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * <ul>
		 *     <li>��� ��: 0x40500004
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: 0 ~ 300 [%]
		 *             <li>�ʱ� ��: 100
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_PITCH = 0x40500004;
		/**
		 * <p>�ӵ� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>�ӵ� Ŀ�ǵ� ����̽��� �����ʹ� ������ ��� �ӵ��� ��Ÿ����.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * <ul>
		 *     <li>��� ��: 0x40500005
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: 0 ~ 300 [%]
		 *             <li>�ʱ� ��: 100
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_SPEECH_RATE = 0x40500005;
		/**
		 * <p>���� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���� Ŀ�ǵ� ����̽��� �����ʹ� ���� �ռ��� ���ڿ��� ��Ÿ����.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40500006
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: String [ ]
		 *             <li>�迭 ũ��: -1
		 *             <li>�ʱ� ��: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_TEXT = 0x40500006;
		/**
		 * <p>�������� �ʴ� ������� ��Ÿ���� ���� �ڵ� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: -100
		 * </ul>
		 */
		public static final int ERROR_LANG_NOT_AVAILABLE = -100;
	}

	/**
	 * <p>�κ��� ���ϴ� ��ġ�� �������� �̵���Ű�� �׼��� �� ID�� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
	 * <p>Action.Navigation�� 6���� Ŀ�ǵ� ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� ������ �迭�� ����Ǿ� �ִ�.
	 * <p>Action.Navigation�� ��ǥ ��ġ�� ���⿡ �����ϸ� {@link Action.OnCompletedListener#onCompleted(Action) onCompleted(Action)} �޼ҵ带 ȣ���Ѵ�.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device devicePadSize = action.findDeviceById(Action.Navigation.COMMAND_PAD_SIZE);
     devicePadSize.write(0, 108); // �е��� �� ���� ����.
     devicePadSize.write(1, 76); // �е��� ���� ���� ����.

     Device deviceInitialPosition = action.findDeviceById(Action.Navigation.COMMAND_INITIAL_POSITION);
     deviceInitialPosition.write(0, 10); // �ʱ� ��ġ�� X ��ǥ ���� ����.
     deviceInitialPosition.write(1, 20); // �ʱ� ��ġ�� Y ��ǥ ���� ����.

     Device deviceWaypoints = action.findDeviceById(Action.Navigation.COMMAND_WAYPOINTS);
     int[] waypoints = new int[] { 50, 50, 90, 50 }; // �ʱ� ��ġ���� (50, 50) ��ġ�� �����Ͽ� (90, 50) ��ġ�� �̵��Ѵ�.
     deviceWaypoints.write(waypoints); // �߰� ��θ� �����Ͽ� �κ��� �̵��� ��ǥ ��ġ ���� ����.

     Device deviceFinalOrientation = action.findDeviceById(Action.Navigation.COMMAND_FINAL_ORIENTATION);
     deviceFinalOrientation.write(30); // ������ ��ġ���� �κ��� ���� ���� ���� ����.

     Device deviceMaxSpeed = action.findDeviceById(Action.Navigation.COMMAND_MAX_SPEED);
     deviceMaxSpeed.write(50); // �ִ� �̵� �ӵ� ���� ����.

     Device deviceCurvature = action.findDeviceById(Action.Navigation.COMMAND_CURVATURE);
     deviceCurvature.write(50); // �̵� ����� ��� ���� ����.
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see Action.OnCompletedListener
	 * @see org.roboid.robot.Device Device
	 */
	public final class Navigation
	{
		/**
		 * <p>Navigation �׼��� �� ID�� ��Ÿ���� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: "org.smartrobot.android.action.navigation"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.navigation";
		/**
		 * <p>�е� ũ�� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>�е� ũ�� Ŀ�ǵ� ����̽��� �����ʹ� ������̼� �е��� ũ�⸦ ��Ÿ����.
		 * ũ�� 2�� ������ �迭���� ù ��° ���� �е��� ���� ũ��, �� ��° ���� �е��� ���� ũ���̴�.
		 * ���� ũ��� ���� ũ���� ��(����)�� 40000�� ���� �� ����.
		 * ��, ���� ũ�Ⱑ 1�� ��쿡�� ���� ũ�Ⱑ 1 ~ 40000���� �����ϰ�, ���� ũ�Ⱑ 200�� ��쿡�� ���� ũ�Ⱑ 1 ~ 200���� �����ϴ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40600005
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 2
		 *             <li>���� ����: 0 ~ 40000 (0: ��ȿ���� ���� ��)
		 *             <li>�ʱ� ��: 0 (��ȿ���� ���� ��)
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_PAD_SIZE = 0x40600005;
		/**
		 * <p>�ʱ� ��ġ Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>�ʱ� ��ġ Ŀ�ǵ� ����̽��� �����ʹ� ������̼� �е� ������ �κ��� �ʱ� ��ġ�� ��Ÿ����.
		 * ũ�� 2�� ������ �迭���� ù ��° ���� X�� ��ǥ, �� ��° ���� Y�� ��ǥ ���̴�.
		 * �������� ������ �κ��� ���� ��ġ�� �ʱ� ��ġ�� ����Ѵ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40600000
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 2
		 *             <li>���� ����: 0 ~ 39999
		 *             <li>�ʱ� ��: 0
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_INITIAL_POSITION = 0x40600000;
		/**
		 * <p>����� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>����� Ŀ�ǵ� ����̽��� �����ʹ� �κ��� �̵��ϴ� �߰� ��ο� ������ ��ġ�� ��Ÿ����.
		 * ������ �迭�� ũ��� 2�� ����̾�� �ϸ�, Ȧ�� ��° ���� X�� ��ǥ, ¦�� ��° ���� Y�� ��ǥ ���̴�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40600001
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: -1
		 *             <li>���� ����: 0 ~ 39999
		 *             <li>�ʱ� ��: 0
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_WAYPOINTS = 0x40600001;
		/**
		 * <p>���� ���� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>���� ���� Ŀ�ǵ� ����̽��� �����ʹ� ������ ��ġ���� �κ��� ���� ������ ��Ÿ����.
		 * ������̼� �е��� X�� ������ 0���̴�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40600002
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: -179 ~ 180 [<sup>o</sup>]
		 *             <li>�ʱ� ��: 0
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_FINAL_ORIENTATION = 0x40600002;
		/**
		 * <p>�ִ� �ӵ� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>�ִ� �ӵ� Ŀ�ǵ� ����̽��� �����ʹ� �κ��� ��θ� ���� �̵��ϴ� �ִ� �ӵ��� ��Ÿ����.
		 * ���� Ŭ���� �̵� �ӵ��� ��������.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * <p>���� 1.3.0������ �������� �ʴ´�. ���� �������� ������ �����̴�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40600003
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: 0 ~ 100 [%]
		 *             <li>�ʱ� ��: 50
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_MAX_SPEED = 0x40600003;
		/**
		 * <p>��� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>��� Ŀ�ǵ� ����̽��� �����ʹ� �κ��� �̵��ϴ� ����� ���� ������ ��Ÿ����.
		 * ���� Ŭ���� ���� ��ȯ�� �ް��ϰ� �ϰ�, ���� �������� ���� ��ȯ�� �ε巴�� �Ѵ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * <p>���� 1.3.0������ �������� �ʴ´�. ���� �������� ������ �����̴�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40600004
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: 0 ~ 100 [%]
		 *             <li>�ʱ� ��: 50
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_CURVATURE = 0x40600004;
	}
	
	/**
	 * <p>������̼� �е� ������ OID �ڵ� ���� ��ġ ������ ��ȯ�ϴ� �׼��� �� ID�� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
	 * <p>Action.Localization�� 1���� ���� ����̽��� 2���� Ŀ�ǵ� ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� ������ �迭�� ����Ǿ� �ִ�.
	 * <p>Action.Localization�� {@link Action#activate() activate()} �޼ҵ�� �����Ͽ� {@link Action#deactivate() deactivate()} �޼ҵ�� ������ ������ ����ȴ�.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device devicePadSize = action.findDeviceById(Action.Localization.COMMAND_PAD_SIZE); // �е� ũ�� Ŀ�ǵ� ����̽��� ��´�.
     devicePadSize.write(0, 108); // �е��� �� ���� ����.
     devicePadSize.write(1, 76); // �е��� ���� ���� ����.

     Device deviceOID = action.findDeviceById(Action.Localization.COMMAND_OID); // OID Ŀ�ǵ� ����̽��� ��´�.
     deviceOID.write(12345); // OID ���� ����.

     Device devicePosition = action.findDeviceById(Action.Localization.SENSOR_POSITION); // ��ġ ���� ����̽��� ��´�.
     int positionX = devicePosition.read(0); // ��ġ �������� X ��ǥ ���� �д´�.
     int positionY = devicePosition.read(1); // ��ġ �������� Y ��ǥ ���� �д´�.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int x, y;
     switch(device.getId())
     {
     case Action.Localization.SENSOR_POSITION: // ��ġ ���� ��´�.
         x = ((int[])values)[0];
         y = ((int[])values)[1];
         break;
     }
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see org.roboid.robot.Device Device
	 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
	 */
	public final class Localization
	{
		/**
		 * <p>Localization �׼��� �� ID�� ��Ÿ���� ���.
		 * </p>
		 * <ul>
		 *     <li>��� ��: "org.smartrobot.android.action.localization"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.localization";
		/**
		 * <p>�е� ũ�� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>�е� ũ�� Ŀ�ǵ� ����̽��� �����ʹ� ������̼� �е��� ũ�⸦ ��Ÿ����.
		 * ũ�� 2�� ������ �迭���� ù ��° ���� �е��� ���� ũ��, �� ��° ���� �е��� ���� ũ���̴�.
		 * ���� ũ��� ���� ũ���� ��(����)�� 40000�� ���� �� ����.
		 * ��, ���� ũ�Ⱑ 1�� ��쿡�� ���� ũ�Ⱑ 1 ~ 40000���� �����ϰ�, ���� ũ�Ⱑ 200�� ��쿡�� ���� ũ�Ⱑ 1 ~ 200���� �����ϴ�.
		 * <p>{@link Action} Ŭ������ {@link Action#activate() activate()} �޼ҵ带 ȣ���ϱ� ���� �����ؾ� �Ѵ�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40700000
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 2
		 *             <li>���� ����: 0 ~ 40000 (0: ��ȿ���� ���� ��)
		 *             <li>�ʱ� ��: 0 (��ȿ���� ���� ��)
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_PAD_SIZE = 0x40700000;
		/**
		 * <p>OID Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>OID Ŀ�ǵ� ����̽��� �����ʹ� ������̼� �е��� OID �ڵ� ���� ��Ÿ����.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40700001
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 1
		 *             <li>���� ����: -1 ~ 65535 (-1: ��ȿ���� ���� ��)
		 *             <li>�ʱ� ��: -1 (��ȿ���� ���� ��)
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_OID = 0x40700001;
		/**
		 * <p>��ġ ���� ����̽��� ID�� ��Ÿ���� ���.
		 * <p>��ġ ���� ����̽��� �����ʹ� ������̼� �е� ������ OID �ڵ� ���� �ش��ϴ� ��ġ�� ��Ÿ����.
		 * ũ�� 2�� ������ �迭���� ù ��° ���� X�� ��ǥ, �� ��° ���� Y�� ��ǥ ���̴�.
		 * </p>
		 * <ul>
		 *     <li>��� ��: 0x40700002
		 *     <li>����̽��� ������ �迭
		 *         <ul>
		 *             <li>������ ��: int [ ]
		 *             <li>�迭 ũ��: 2
		 *             <li>���� ����: -1 ~ 39999 (-1: ��ȿ���� ���� ��)
		 *             <li>�ʱ� ��: -1 (��ȿ���� ���� ��)
		 *         </ul>
		 *     </li>
		 * </ul>
		 */
		public static final int SENSOR_POSITION = 0x40700002;
	}
	
	/**
	 * <p>�׼��� ���°� ����Ǿ��� �� ȣ��Ǵ� �޼ҵ带 �����Ѵ�.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 */
	public interface OnStateChangedListener
	{
		/**
		 * <p>�׼��� ���°� ����Ǿ��� �� ȣ��ȴ�.
		 * <p>���� ���� ���� {@link Action} Ŭ������ �����ϰ�, �׼ǿ� ���� �ٸ� ���� ���� �� �׼��� Ŭ������ �����ϱ� �ٶ���.
		 * </p>
		 * @param action ���°� ����� �׼�
		 * @param state �׼��� ���� ��
		 */
		void onStateChanged(Action action, int state);
	}
	
	/**
	 * <p>�׼��� ������ �Ϸ�Ǿ��� �� ȣ��Ǵ� �޼ҵ带 �����Ѵ�.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 */
	public interface OnCompletedListener
	{
		/**
		 * <p>�׼��� ������ �Ϸ�Ǿ��� �� ȣ��ȴ�.
		 * <p>�׼ǿ� ���� {@link Action.Navigation}�� ���� �־��� ����� �ϼ��Ͽ� ������ �Ϸ�Ǵ� �׼ǵ� �ְ�,
		 * {@link Action.Microphone}�� ���� {@link Action#deactivate() deactivate()} �޼ҵ带 ȣ���� ������ ��� ����Ǵ� �׼ǵ� �ִ�.
		 * {@link Action#deactivate() deactivate()} �޼ҵ带 ȣ���� ������ ��� ����Ǵ� �׼��� ��쿡�� onCompleted(Action) �޼ҵ尡 ȣ����� �ʰ�, ������ �Ϸ�Ǵ� �׼��� ��쿡�� �־��� ����� �ϼ��Ͽ��� �� onCompleted(Action) �޼ҵ尡 ȣ��ȴ�.
		 * onCompleted(Action) �޼ҵ��� ȣ�� ���δ� �� �׼��� Ŭ������ �����ϱ� �ٶ���.
		 * </p>
		 * @param action ������ �Ϸ�� �׼�
		 */
		void onCompleted(Action action);
	}
	
	/**
	 * <p>�׼� �ν��Ͻ��� ��ų� ����, ����, ����ϴ� �������� ������ �߻����� �� ȣ��Ǵ� �޼ҵ带 �����Ѵ�.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 */
	public interface OnErrorListener
	{
		/**
		 * <p>�׼� �ν��Ͻ��� ��ų� ����, ����, ����ϴ� �������� ������ �߻����� �� ȣ��ȴ�.
		 * <p>���� ���� �ڵ� ���� {@link Action} Ŭ������ �����ϰ�, �׼ǿ� ���� �ٸ� ���� �ڵ� ���� �� �׼��� Ŭ������ �����ϱ� �ٶ���.
		 * </p>
		 * @param action ������ �߻��� �׼�
		 * @param errorCode ���� �ڵ� ��
		 */
		void onError(Action action, int errorCode);
	}

	private static final int MSG_ACK = 1;
	private static final int MSG_STATE = 2;
	private static final int MSG_COMPLETION = 3;
	private static final int MSG_ERROR = 4;
	
	private static final HashMap<String, Action> mActions = new HashMap<String, Action>();
	private WeakReference<Context> mContext;
	OnStateChangedListener mOnStateChangedListener;
	OnCompletedListener mOnCompletedListener;
	OnErrorListener mOnErrorListener;
	private boolean mPrepared;
	private boolean mDisposed;
	boolean mActive;
	private final ArrayList<Intent> mIntents = new ArrayList<Intent>();
	private BroadcastReceiver mBR;
	private final EventHandler mEventHandler;
	
	private ac mActionBinder;
	private final ServiceConnection mActionConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			mActionBinder = ac.Stub.asInterface(binder);
			try
			{
				mActionBinder.a(mDataChangedCallback);
				mActionBinder.c(mDataRequestCallback);
			} catch (RemoteException e)
			{
			}
			
			mPrepared = true;
			Context context = getContext();
			if(context == null)
			{
				if(mOnErrorListener != null)
					mOnErrorListener.onError(Action.this, Action.ERROR_INVALID_CONTEXT);
			}
			else
			{
				for(Intent i : mIntents)
					context.sendBroadcast(i);
			}
			mIntents.clear();
			if(mOnStateChangedListener != null)
				mOnStateChangedListener.onStateChanged(Action.this, Action.STATE_PREPARED);
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mActionBinder = null;
		}
	};
	private final dc.Stub mDataChangedCallback = new dc.Stub()
	{
		@Override
		public void a(byte[] a1, long a2) throws RemoteException
		{
			if(a1 != null)
				handleSimulacrum(a1, a2);
			updateDeviceState();
		}

		@Override
		public void b(int b1, int b2, byte[] b3, long b4) throws RemoteException
		{
		}
	};
	private final dr.Stub mDataRequestCallback = new dr.Stub()
	{
		@Override
		public byte[] a() throws RemoteException
		{
			if(mActionBinder == null) return null;
			return encodeSimulacrum();
		}

		@Override
		public byte[] b(int b1, int b2) throws RemoteException
		{
			return null;
		}
	};
	
	Action(int size, int tag)
	{
		super(size, tag);
		
		Looper looper;
		if((looper = Looper.myLooper()) != null)
		{
			mEventHandler = new EventHandler(this, looper);
		}
		else if((looper = Looper.getMainLooper()) != null)
		{
			mEventHandler = new EventHandler(this, looper);
		}
		else
			mEventHandler = new EventHandler(this);
	}
	
	/**
	 * <p>�׼� ID�� ���� �׼� �ν��Ͻ��� ��´�.
	 * <p>context �Ǵ� actionId�� null�̸� null�� ��ȯ�Ѵ�.
	 * actionId�� �� �׼ǿ� ���ǵ� ID ���̴�.
	 * actionId�� ���� �׼� �ν��Ͻ��� �̹� �����ϴ� ��쿡�� ������ �׼� �ν��Ͻ��� ��ȯ�ϰ�, �������� ������ ���� �����Ͽ� ��ȯ�Ѵ�.
	 * �׼� ������ �����ϸ� null�� ��ȯ�Ѵ�.
	 * </p>
	 * @param context ���ؽ�Ʈ
	 * @param actionId �׼��� ID
	 * <p>
	 * @return �׼� �ν��Ͻ� �Ǵ� null
	 */
	public static Action obtain(Context context, String actionId)
	{
		if(context == null || actionId == null) return null;
		Action action = null;
		synchronized(mActions)
		{
			action = mActions.get(actionId);
			if(action == null)
			{
				action = ActionFactory.create(actionId);
				if(action != null)
				{
					mActions.put(actionId, action);
					action.setContext(context);
					action.registerBroadcast();
					Intent intent = new Intent("roboid.intent.action.ACTION_REQ");
					intent.putExtra("roboid.intent.extra.PACKAGE_NAME", context.getPackageName());
					intent.putExtra("roboid.intent.extra.ACTION_ID", actionId);
					context.sendBroadcast(intent);
				}
			}
			else
				action.setContext(context);
		}
		return action;
	}
	
	/**
	 * <p>������ ��� �׼��� ����Ѵ�.
	 * <p>�׼��� ����� ���Ŀ��� {@link #activate()} Ȥ�� {@link #deactivate()} �޼ҵ带 ȣ���Ͽ� �׼��� �����ϰų� ������ �� ����.
	 * ���ø����̼��� ����Ǳ� ���� �ݵ�� ������ ��� �׼ǿ� ���� {@link #dispose()} Ȥ�� Action.disposeAll() �޼ҵ带 ȣ���Ͽ� ������ �׼��� ����Ͽ��� �Ѵ�.
	 */
	public static void disposeAll()
	{
		synchronized(mActions)
		{
			for(Action action : mActions.values())
				action.release();
			mActions.clear();
		}
	}
	
	Context getContext()
	{
		if(mContext == null) return null;
		return mContext.get();
	}
	
	private void setContext(Context context)
	{
		Context applicationContext = context.getApplicationContext();
		if(applicationContext == null)
			mContext = new WeakReference<Context>(context);
		else
			mContext = new WeakReference<Context>(applicationContext);
	}
	
	/**
	 * <p>�׼��� ���°� ����Ǿ��� �� ȣ��ǵ��� listener�� �����Ѵ�.
	 * </p>
	 * @param listener ������ ������
	 */
	public void setOnStateChangedListener(OnStateChangedListener listener)
	{
		mOnStateChangedListener = listener;
	}
	
	/**
	 * <p>�׼��� ������ �Ϸ�Ǿ��� �� ȣ��ǵ��� listener�� �����Ѵ�.
	 * </p>
	 * @param listener ������ ������
	 */
	public void setOnCompletedListener(OnCompletedListener listener)
	{
		mOnCompletedListener = listener;
	}
	
	/**
	 * <p>�׼� �ν��Ͻ��� ��ų� ����, ����, ����ϴ� �������� ������ �߻����� �� ȣ��ǵ��� listener�� �����Ѵ�.
	 * </p>
	 * @param listener ������ ������
	 */
	public void setOnErrorListener(OnErrorListener listener)
	{
		mOnErrorListener = listener;
	}
	
	private void registerBroadcast()
	{
		if(mBR != null) return;
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_INVALID_CONTEXT);
			return;
		}

		mBR = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				String action = intent.getAction();
				if("roboid.intent.action.ACTION_ACK".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_ACK);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ACTION_STATE".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_STATE);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ACTION_COMPLETION".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_COMPLETION);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ACTION_ERROR".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_ERROR);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("roboid.intent.action.ACTION_ACK");
		intentFilter.addAction("roboid.intent.action.ACTION_STATE");
		intentFilter.addAction("roboid.intent.action.ACTION_COMPLETION");
		intentFilter.addAction("roboid.intent.action.ACTION_ERROR");
		context.registerReceiver(mBR, intentFilter);
	}
	
	boolean connect(Intent intent)
	{
		if(intent == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_NOT_SUPPORTED);
			return false;
		}
		
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_INVALID_CONTEXT);
			return false;
		}
		
		try
		{
			context.bindService(intent, mActionConnection, Context.BIND_AUTO_CREATE);
		} catch (SecurityException e)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_SECURITY);
		}
		return true;
	}

	void disconnect()
	{
		if(mActionBinder == null) return;
		
		try
		{
			mActionBinder.b(mDataChangedCallback);
			mActionBinder.d(mDataRequestCallback);
			mActionBinder = null;
			Context context = getContext();
			if(context == null)
			{
				if(mOnErrorListener != null)
					mOnErrorListener.onError(this, Action.ERROR_INVALID_CONTEXT);
			}
			else
				context.unbindService(mActionConnection);
		} catch (RemoteException e)
		{
		}
	}
	
	/**
	 * <p>�׼��� ����Ѵ�.
	 * <p>�׼��� ����� ���Ŀ��� {@link #activate()} Ȥ�� {@link #deactivate()} �޼ҵ带 ȣ���Ͽ� �׼��� �����ϰų� ������ �� ����.
	 * ���ø����̼��� ����Ǳ� ���� �ݵ�� ������ ��� �׼ǿ� ���� dispose() Ȥ�� {@link #Action.disposeAll() Action.disposeAll()} �޼ҵ带 ȣ���Ͽ� ������ �׼��� ����Ͽ��� �Ѵ�.
	 */
	public void dispose()
	{
		release();
		synchronized(mActions)
		{
			mActions.remove(getId());
		}
	}
	
	private void release()
	{
		if(mActive)
			deactivate();
		
		if(mEventHandler != null)
		{
			mEventHandler.removeMessages(MSG_ACK);
			mEventHandler.removeMessages(MSG_STATE);
			mEventHandler.removeMessages(MSG_COMPLETION);
			mEventHandler.removeMessages(MSG_ERROR);
		}
		disconnect();
		
		if(mBR != null)
		{
			Context context = getContext();
			if(context == null)
			{
				if(mOnErrorListener != null)
					mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			}
			else
				context.unregisterReceiver(mBR);
			mBR = null;
		}
		mPrepared = false;
		mDisposed = true;
		mIntents.clear();
		
		if(mOnStateChangedListener != null)
			mOnStateChangedListener.onStateChanged(this, STATE_DISPOSED);
		
		mOnStateChangedListener = null;
		mOnCompletedListener = null;
		mOnErrorListener = null;
	}
	
	/**
	 * <p>�׼��� ������ �����Ѵ�.
	 * <p>{@link #dispose()} Ȥ�� {@link #disposeAll() Action.disposeAll()} �޼ҵ带 ȣ���Ͽ� �׼��� ���� �Ŀ��� �׼��� ������� �ʰ� false�� ��ȯ�Ѵ�.
	 * </p>
	 * @return �����ϸ� true, �ƴϸ� false
	 */
	public boolean activate()
	{
		if(mDisposed)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mActive)
			deactivate();
		mActive = true;
		
		Intent intent = new Intent("roboid.intent.action.ACTION_ACTIVATE");
		intent.putExtra("roboid.intent.extra.ACTION_ID", getId());
		if(mPrepared)
			context.sendBroadcast(intent);
		else
			mIntents.add(intent);
		return true;
	}
	
	/**
	 * <p>�׼��� ������ �����Ѵ�.
	 * <p>{@link #dispose()} Ȥ�� {@link #disposeAll() Action.disposeAll()} �޼ҵ带 ȣ���Ͽ� �׼��� ���� �Ŀ��� false�� ��ȯ�Ѵ�.
	 * </p>
	 * @return �����ϸ� true, �ƴϸ� false
	 */
	public boolean deactivate()
	{
		if(mDisposed)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		mActive = false;
		
		Intent intent = new Intent("roboid.intent.action.ACTION_DEACTIVATE");
		intent.putExtra("roboid.intent.extra.ACTION_ID", getId());
		if(mPrepared)
			context.sendBroadcast(intent);
		else
			mIntents.add(intent);
		return true;
	}
	
	void handleSimulacrum(byte[] simulacrum, long timestamp)
	{
		if(decodeSimulacrum(simulacrum))
		{
			synchronized(mListeners)
			{
				for(DeviceDataChangedListener listener : mListeners)
					notifyDataChanged(listener, timestamp);
			}
		}
	}
	
	abstract boolean decodeSimulacrum(byte[] simulacrum);
	abstract void notifyDataChanged(DeviceDataChangedListener listener, long timestamp);
	
	byte[] encodeSimulacrum()
	{
		return null;
	}
	
	private static class EventHandler extends Handler
	{
		private final Action mAction;
		
		EventHandler(Action action)
		{
			super();
			mAction = action;
		}
		
		EventHandler(Action action, Looper looper)
		{
			super(looper);
			mAction = action;
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case MSG_ACK:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					Context context = mAction.getContext();
					if(context == null)
					{
						Action.OnErrorListener listener = mAction.mOnErrorListener;
						if(listener != null)
							listener.onError(mAction, Action.ERROR_INVALID_CONTEXT);
						return;
					}
					
					String packageName = intent.getStringExtra("roboid.intent.extra.PACKAGE_NAME");
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(packageName == null || !packageName.equals(context.getPackageName())) return;
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					Intent i = intent.getParcelableExtra("roboid.intent.extra.ACTION");
					mAction.connect(i);
				}
				break;
			case MSG_STATE:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					int state = intent.getIntExtra("roboid.intent.extra.ACTION_STATE", STATE_NONE);
					if(state != STATE_NONE)
					{
						Action.OnStateChangedListener listener = mAction.mOnStateChangedListener;
						if(listener != null)
							listener.onStateChanged(mAction, state);
					}
				}
				break;
			case MSG_COMPLETION:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					mAction.mActive = false;
					Action.OnCompletedListener listener = mAction.mOnCompletedListener;
					if(listener != null)
						listener.onCompleted(mAction);
				}
				break;
			case MSG_ERROR:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					int errorCode = intent.getIntExtra("roboid.intent.extra.ACTION_ERROR", ERROR_NONE);
					if(errorCode != ERROR_NONE)
					{
						Action.OnErrorListener listener = mAction.mOnErrorListener;
						if(listener != null)
							listener.onError(mAction, errorCode);
					}
				}
				break;
			}
		}
	}
}