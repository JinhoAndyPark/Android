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

package kr.robomation.physical;


/**
 * <p>�˹�Ʈ �� �κ��� �� ID�� �˹�Ʈ �� �κ��� �����ϴ� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
 * <p>�˹�Ʈ �� �κ��� 4���� ���� ����̽��� 8���� ������ ����̽�, 1���� Ŀ�ǵ� ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� ������ �迭�� ����Ǿ� �ִ�.
 * <p>�˹�Ʈ �� �κ��� �����ϴ� ����̽��� �����͸� �аų� ���� ���ؼ��� �켱 {@link org.roboid.robot.Robot Robot} Ŭ������ {@link org.roboid.robot.Robot#findDeviceById(int) findDeviceById(int deviceId)} �޼ҵ带 ����Ͽ� ����̽� ��ü�� ���۷����� ���� �Ѵ�.
 * �̷��� ����� ����̽� ��ü�� ���� read �Ǵ� write �޼ҵ带 ����Ͽ� ����̽��� �����͸� �аų� �� �� �ִ�.
 * {@link org.roboid.robot.Device.DeviceDataChangedListener#onDeviceDataChanged(org.roboid.robot.Device, Object, long) onDeviceDataChanged} �ݹ� �޼ҵ带 ���� ����̽��� �����͸� ���� ���� �ִ�.
 * </p>
 * <pre class="prettyprint">
 * void someMethod(Robot robot)
 {
     Device deviceLip = robot.findDeviceById(AlbertPop.EFFECTOR_LIP); // �� ������ ����̽��� ��´�.
     int lip = deviceLip.read(); // �� ũ�� ���� �д´�.

     Device deviceLeftProximity = robot.findDeviceById(AlbertPop.SENSOR_LEFT_PROXIMITY); // ���� ���� ���� ����̽��� ��´�.
     int[] leftProximity = new int[4];
     deviceLeftProximity.read(leftProximity); // ���� ���� ���� ���� �д´�.

     Device deviceLeftWheel = robot.findDeviceById(AlbertPop.EFFECTOR_LEFT_WHEEL); // ���� ���� ������ ����̽��� ��´�.
     deviceLeftWheel.write(25); // ���� ���� �ӵ� ���� ����.

     Device deviceLeftEye = robot.findDeviceById(AlbertPop.EFFECTOR_LEFT_EYE); // ���� �� ������ ����̽��� ��´�.
     int[] leftEye = new int[] { 255, 0, 0 };
     deviceLeftEye.write(leftEye); // ���� �� ���� ���� ����.

     Device deviceFrontLED = robot.findDeviceById(AlbertPop.COMMAND_FRONT_LED); // ���� LED Ŀ�ǵ� ����̽��� ��´�.
     deviceFrontLED.write(1); // ���� LED�� �Ҵ�.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int leftWheel;
     int[] leftProximity = new int[4];
     switch(device.getId())
     {
     case AlbertPop.EFFECTOR_LEFT_WHEEL: // ���� ���� �ӵ� ���� ��´�.
         leftWheel = ((int[])values)[0];
         break;
     case AlbertPop.SENSOR_LEFT_PROXIMITY: // ���� ���� ���� ���� ��´�.
         System.arraycopy((int[])values, 0, leftProximity, 0, 4);
         break;
     }
 }</pre>
 *
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see org.roboid.robot.Robot Robot
 * @see org.roboid.robot.Device Device
 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
 */
public final class AlbertPop
{
	/**
	 * <p>�˹�Ʈ �� �κ��� �� ID�� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: "kr.robomation.physical.albert.pop"
	 * </ul>
	 */
	public static final String ID = "kr.robomation.physical.albert.pop";
	
	/**
	 * <p>����Ŀ ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>����Ŀ ������ ����̽��� �����ʹ� ����Ʈ ���� ����Ŀ�� ��µǴ� �Ҹ�(PCM)�� ��Ÿ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300000
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 480
	 *             <li>���� ����: -32768 ~ 32767
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_SPEAKER = 0x00300000;
	/**
	 * <p>�Ҹ� ũ�� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>�Ҹ� ũ�� ������ ����̽��� �����ʹ� ����Ʈ ���� ����Ŀ�� ��µǴ� �Ҹ��� ũ�⸦ ��Ÿ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300001
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: 0 ~ 300 [%]
	 *             <li>�ʱ� ��: 100
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_VOLUME = 0x00300001;
	/**
	 * <p>�� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>�� ������ ����̽��� �����ʹ� �κ��� ���� ũ�⸦ ��Ÿ����.
	 * �˹�Ʈ �� �κ��� ���� ���� �ϵ���� ��ġ���� ���� ������ ��ġ�̸�, �� ���� �̿��Ͽ� ȭ�鿡 �׷����� ǥ���ϰų� LED�� �����̵��� �Ͽ� ���� �����̴� �Ͱ� ����� ȿ���� ��Ÿ�� �� �ִ�.
	 * Ŭ�� ������ ����� �� �� ���� ���� �� �ִ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300002
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: 0 ~ 100 [%]
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_LIP = 0x00300002;
	/**
	 * <p>���� ���� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� ���� ������ ����̽��� �����ʹ� ���� ������ �ӵ��� ��Ÿ����.
	 * ��� ���� ���� ���������� ȸ����, ���� ���� ���� ���������� ȸ���� �ǹ��Ѵ�.
	 * ��ȣ�� ������ ����ġ�� Ŭ���� �ӵ��� ��������.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300003
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: -100 ~ 100 [%]
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_LEFT_WHEEL = 0x00300003;
	/**
	 * <p>������ ���� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>������ ���� ������ ����̽��� �����ʹ� ������ ������ �ӵ��� ��Ÿ����.
	 * ��� ���� ���� ���������� ȸ����, ���� ���� ���� ���������� ȸ���� �ǹ��Ѵ�.
	 * ��ȣ�� ������ ����ġ�� Ŭ���� �ӵ��� ��������.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300004
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: -100 ~ 100 [%]
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_RIGHT_WHEEL = 0x00300004;
	/**
	 * <p>���� �� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� �� ������ ����̽��� �����ʹ� ���� ���� LED ������ ��Ÿ����.
	 * ũ�� 3�� ������ �迭���� ù ��° ���� RGB ������ R ��, �� ��° ���� G ��, �� ��° ���� B ���̴�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300005
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 3
	 *             <li>���� ����: 0 ~ 255
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_LEFT_EYE = 0x00300005;
	/**
	 * <p>������ �� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>������ �� ������ ����̽��� �����ʹ� ������ ���� LED ������ ��Ÿ����.
	 * ũ�� 3�� ������ �迭���� ù ��° ���� RGB ������ R ��, �� ��° ���� G ��, �� ��° ���� B ���̴�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300006
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 3
	 *             <li>���� ����: 0 ~ 255
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_RIGHT_EYE = 0x00300006;
	/**
	 * <p>���� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� ������ ����̽��� �����ʹ� ���� �Ҹ��� �� ���̸� ��Ÿ����.
	 * ���� �Ҹ��� ���� ���ؼ��� 0�� �Է��ϸ� �ȴ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300007
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: 0 ~ 2500 [Hz] (0: off)
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_BUZZER = 0x00300007;
	/**
	 * <p>���� LED Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� LED Ŀ�ǵ� ����̽��� �����ʹ� ���� LED�� ���¸� ��Ÿ����.
	 * LED�� �ѱ� ���ؼ��� 1, ���� ���ؼ��� 0�� �Է��ϸ� �ȴ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300014
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: 0 �Ǵ� 1 (0: LED�� �� ����, 1: LED�� �� ����)
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int COMMAND_FRONT_LED = 0x00300014;
	/**
	 * <p>���� ���� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� ���� ���� ����̽��� �����ʹ� ���� ���� ���� ���� ��Ÿ����.
	 * ���� ������ �� 10ms���� �����Ǵµ� ���ø����̼����� �� 40ms���� ���޵ǹǷ� ���� ���� 4���� ��Ƽ� �����Ѵ�.
	 * 10ms �������� ������ ���� �� 4���� ũ�� 4�� ������ �迭�� ���ʴ�� ��ϵǾ� ���޵ȴ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300009
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 4
	 *             <li>���� ����: 0 ~ 255
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_LEFT_PROXIMITY = 0x00300009;
	/**
	 * <p>������ ���� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>������ ���� ���� ����̽��� �����ʹ� ������ ���� ���� ���� ��Ÿ����.
	 * ���� ������ �� 10ms���� �����Ǵµ� ���ø����̼����� �� 40ms���� ���޵ǹǷ� ���� ���� 4���� ��Ƽ� �����Ѵ�.
	 * 10ms �������� ������ ���� �� 4���� ũ�� 4�� ������ �迭�� ���ʴ�� ��ϵǾ� ���޵ȴ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x0030000a
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 4
	 *             <li>���� ����: 0 ~ 255
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_RIGHT_PROXIMITY = 0x0030000a;
	/**
	 * <p>���� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� ���� ����̽��� �����ʹ� �κ��� ���� ���� ���� ��Ÿ����.
	 * ���� ���� ���� Ŀ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x0030000e
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: 0 ~ 65535
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_LIGHT = 0x0030000e;
	/**
	 * <p>���͸� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���͸� ���� ����̽��� �����ʹ� �κ��� ���͸� �ܷ��� %�� ��Ÿ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00300010
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: 0 ~ 100 [%]
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_BATTERY = 0x00300010;
}