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
 * <p>���� �κ��� �� ID�� ���� �κ��� �����ϴ� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
 * <p>���� �κ��� 8���� ���� ����̽��� 8���� ������ ����̽�, 1���� Ŀ�ǵ� ����̽�, 2���� �̺�Ʈ ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� ������ �迭�� ����Ǿ� �ִ�.
 * <p>���� �κ��� �����ϴ� ����̽��� �����͸� �аų� ���� ���ؼ��� �켱 {@link org.roboid.robot.Robot Robot} Ŭ������ {@link org.roboid.robot.Robot#findDeviceById(int) findDeviceById(int deviceId)} �޼ҵ带 ����Ͽ� ����̽� ��ü�� ���۷����� ���� �Ѵ�.
 * �̷��� ����� ����̽� ��ü�� ���� read �Ǵ� write �޼ҵ带 ����Ͽ� ����̽��� �����͸� �аų� �� �� �ִ�.
 * {@link org.roboid.robot.Device.DeviceDataChangedListener#onDeviceDataChanged(org.roboid.robot.Device, Object, long) onDeviceDataChanged} �ݹ� �޼ҵ带 ���� ����̽��� �����͸� ���� ���� �ִ�.
 * </p>
 * <pre class="prettyprint">
 * void someMethod(Robot robot)
 {
     Device deviceLip = robot.findDeviceById(Alpha.EFFECTOR_LIP); // �� ������ ����̽��� ��´�.
     int lip = deviceLip.read(); // �� ũ�� ���� �д´�.

     Device deviceLeftProximity = robot.findDeviceById(Alpha.SENSOR_LEFT_PROXIMITY); // ���� ���� ���� ����̽��� ��´�.
     int[] leftProximity = new int[4];
     deviceLeftProximity.read(leftProximity); // ���� ���� ���� ���� �д´�.

     Device devicePosition = robot.findDeviceById(Alpha.SENSOR_POSITION); // ��ġ ���� ����̽��� ��´�.
     int positionX = devicePosition.read(0); // ��ġ �������� X ��ǥ ���� �д´�.
     int positionY = devicePosition.read(1); // ��ġ �������� Y ��ǥ ���� �д´�.

     Device deviceFrontOID = robot.findDeviceById(Alpha.EVENT_FRONT_OID); // ���� OID �̺�Ʈ ����̽��� ��´�.
     if(deviceFrontOID.e()) // ���� OID ���� ���ŵǾ����� Ȯ���Ѵ�.
     {
         int frontOID = deviceFrontOID.read(); // ���� OID ���� �д´�.
     }

     Device deviceLeftWheel = robot.findDeviceById(Alpha.EFFECTOR_LEFT_WHEEL); // ���� ���� ������ ����̽��� ��´�.
     deviceLeftWheel.write(25); // ���� ���� �ӵ� ���� ����.

     Device deviceLeftEye = robot.findDeviceById(Alpha.EFFECTOR_LEFT_EYE); // ���� �� ������ ����̽��� ��´�.
     int[] leftEye = new int[] { 255, 0, 0 };
     deviceLeftEye.write(leftEye); // ���� �� ���� ���� ����.

     Device devicePadSize = robot.findDeviceById(Alpha.COMMAND_PAD_SIZE); // �е� ũ�� Ŀ�ǵ� ����̽��� ��´�.
     devicePadSize.write(0, 108); // �е��� �� ���� ����.
     devicePadSize.write(1, 76); // �е��� ���� ���� ����.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int leftWheel, padWidth, padHeight, frontOID;
     int[] leftProximity = new int[4];
     switch(device.getId())
     {
     case Alpha.EFFECTOR_LEFT_WHEEL: // ���� ���� �ӵ� ���� ��´�.
         leftWheel = ((int[])values)[0];
         break;
     case Alpha.COMMAND_PAD_SIZE: // �е� ũ�� ���� ��´�.
         padWidth = ((int[])values)[0];
         padHeight = ((int[])values)[1];
         break;
     case Alpha.SENSOR_LEFT_PROXIMITY: // ���� ���� ���� ���� ��´�.
         System.arraycopy((int[])values, 0, leftProximity, 0, 4);
         break;
     case Alpha.EVENT_FRONT_OID: // ���� OID ���� ��´�.
         frontOID = ((int[])values)[0];
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
public final class Alpha
{
	/**
	 * <p>���� �κ��� �� ID�� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: "kr.robomation.physical.alpha"
	 * </ul>
	 */
	public static final String ID = "kr.robomation.physical.alpha";
	
	/**
	 * <p>����Ŀ ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>����Ŀ ������ ����̽��� �����ʹ� �κ��� ����Ŀ�� ��µǴ� �Ҹ�(PCM)�� ��Ÿ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100000
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
	public static final int EFFECTOR_SPEAKER = 0x00100000;
	/**
	 * <p>�Ҹ� ũ�� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>�Ҹ� ũ�� ������ ����̽��� �����ʹ� �κ��� ����Ŀ�� ��µǴ� �Ҹ��� ũ�⸦ ��Ÿ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100001
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
	public static final int EFFECTOR_VOLUME = 0x00100001;
	/**
	 * <p>�� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>�� ������ ����̽��� �����ʹ� �κ��� ���� ũ�⸦ ��Ÿ����.
	 * ���� �κ��� ���� ���� �ϵ���� ��ġ���� ���� ������ ��ġ�̸�, �� ���� �̿��Ͽ� ȭ�鿡 �׷����� ǥ���ϰų� LED�� �����̵��� �Ͽ� ���� �����̴� �Ͱ� ����� ȿ���� ��Ÿ�� �� �ִ�.
	 * Ŭ�� ������ ����� �� �� ���� ���� �� �ִ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100002
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
	public static final int EFFECTOR_LIP = 0x00100002;
	/**
	 * <p>���� ���� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� ���� ������ ����̽��� �����ʹ� ���� ������ �ӵ��� ��Ÿ����.
	 * ��� ���� ���� ���������� ȸ����, ���� ���� ���� ���������� ȸ���� �ǹ��Ѵ�.
	 * ��ȣ�� ������ ����ġ�� Ŭ���� �ӵ��� ��������.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100003
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
	public static final int EFFECTOR_LEFT_WHEEL = 0x00100003;
	/**
	 * <p>������ ���� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>������ ���� ������ ����̽��� �����ʹ� ������ ������ �ӵ��� ��Ÿ����.
	 * ��� ���� ���� ���������� ȸ����, ���� ���� ���� ���������� ȸ���� �ǹ��Ѵ�.
	 * ��ȣ�� ������ ����ġ�� Ŭ���� �ӵ��� ��������.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100004
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
	public static final int EFFECTOR_RIGHT_WHEEL = 0x00100004;
	/**
	 * <p>���� �� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� �� ������ ����̽��� �����ʹ� ���� ���� LED ������ ��Ÿ����.
	 * ũ�� 3�� ������ �迭���� ù ��° ���� RGB ������ R ��, �� ��° ���� G ��, �� ��° ���� B ���̴�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100005
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
	public static final int EFFECTOR_LEFT_EYE = 0x00100005;
	/**
	 * <p>������ �� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>������ �� ������ ����̽��� �����ʹ� ������ ���� LED ������ ��Ÿ����.
	 * ũ�� 3�� ������ �迭���� ù ��° ���� RGB ������ R ��, �� ��° ���� G ��, �� ��° ���� B ���̴�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100006
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
	public static final int EFFECTOR_RIGHT_EYE = 0x00100006;
	/**
	 * <p>���� ������ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� ������ ����̽��� �����ʹ� ���� �Ҹ��� �� ���̸� ��Ÿ����.
	 * ���� �Ҹ��� ���� ���ؼ��� 0�� �Է��ϸ� �ȴ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100007
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
	public static final int EFFECTOR_BUZZER = 0x00100007;
	/**
	 * <p>�е� ũ�� Ŀ�ǵ� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>�е� ũ�� Ŀ�ǵ� ����̽��� �����ʹ� ������̼� �е��� ũ�⸦ ��Ÿ����.
	 * ũ�� 2�� ������ �迭���� ù ��° ���� �е��� ���� ũ��, �� ��° ���� �е��� ���� ũ���̴�.
	 * ���� ũ��� ���� ũ���� ��(����)�� 40000�� ���� �� ����.
	 * ��, ���� ũ�Ⱑ 1�� ��쿡�� ���� ũ�Ⱑ 1 ~ 40000���� �����ϰ�, ���� ũ�Ⱑ 200�� ��쿡�� ���� ũ�Ⱑ 1 ~ 200���� �����ϴ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100008
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 2
	 *             <li>���� ����: 0 ~ 40000 (0: ��ȿ���� ���� ��)
	 *             <li>�ʱ� ��: 0 (��ȿ���� ���� ��)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int COMMAND_PAD_SIZE = 0x00100008;
	/**
	 * <p>���� ���� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� ���� ���� ����̽��� �����ʹ� ���� ���� ���� ���� ��Ÿ����.
	 * ���� ������ �� 10ms���� �����Ǵµ� ���ø����̼����� �� 40ms���� ���޵ǹǷ� ���� ���� 4���� ��Ƽ� �����Ѵ�.
	 * 10ms �������� ������ ���� �� 4���� ũ�� 4�� ������ �迭�� ���ʴ�� ��ϵǾ� ���޵ȴ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100009
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
	public static final int SENSOR_LEFT_PROXIMITY = 0x00100009;
	/**
	 * <p>������ ���� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>������ ���� ���� ����̽��� �����ʹ� ������ ���� ���� ���� ��Ÿ����.
	 * ���� ������ �� 10ms���� �����Ǵµ� ���ø����̼����� �� 40ms���� ���޵ǹǷ� ���� ���� 4���� ��Ƽ� �����Ѵ�.
	 * 10ms �������� ������ ���� �� 4���� ũ�� 4�� ������ �迭�� ���ʴ�� ��ϵǾ� ���޵ȴ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x0010000a
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
	public static final int SENSOR_RIGHT_PROXIMITY = 0x0010000a;
	/**
	 * <p>���ӵ� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���ӵ� ���� ����̽��� �����ʹ� �κ��� 3�� ���ӵ� ���� ���� ��Ÿ����.
	 * ũ�� 3�� ������ �迭���� ù ��° ���� X��, �� ��° ���� Y��, �� ��° ���� Z���� ���ӵ� ���̴�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x0010000b
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 3
	 *             <li>���� ����: -8192 ~ 8191
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_ACCELERATION = 0x0010000b;
	/**
	 * <p>��ġ ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>��ġ ���� ����̽��� �����ʹ� ������̼� �е� ������ �κ��� ��ġ�� ��Ÿ����.
	 * ũ�� 2�� ������ �迭���� ù ��° ���� X�� ��ǥ, �� ��° ���� Y�� ��ǥ ���̴�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x0010000c
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
	public static final int SENSOR_POSITION = 0x0010000c;
	/**
	 * <p>���� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� ���� ����̽��� �����ʹ� ������̼� �е� ������ �κ��� ������ ��Ÿ����.
	 * ������̼� �е��� X�� ������ 0���̴�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x0010000d
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: -179 ~ 180[<sup>o</sup>] �Ǵ� -200[<sup>o</sup>] (-200: ��ȿ���� ���� ��)
	 *             <li>�ʱ� ��: -200 (��ȿ���� ���� ��)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_ORIENTATION = 0x0010000d;
	/**
	 * <p>���� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� ���� ����̽��� �����ʹ� �κ��� ���� ���� ���� ��Ÿ����.
	 * ���� ���� ���� Ŀ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x0010000e
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
	public static final int SENSOR_LIGHT = 0x0010000e;
	/**
	 * <p>�µ� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>�µ� ���� ����̽��� �����ʹ� �κ� ������ �µ��� ��Ÿ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x0010000f
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: -40 ~ 88 [<sup>o</sup>C]
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_TEMPERATURE = 0x0010000f;
	/**
	 * <p>���͸� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���͸� ���� ����̽��� �����ʹ� �κ��� ���͸� �ܷ��� %�� ��Ÿ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100010
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
	public static final int SENSOR_BATTERY = 0x00100010;
	/**
	 * <p>���� OID �̺�Ʈ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� OID �̺�Ʈ ����̽��� �����ʹ� �κ��� �ٴ� �鿡 �ִ� ���� OID ���� ��Ÿ����.
	 * OID ���� ���� ���� ��쿡�� -1�� ���� ������.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100011
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: -1 ~ 65535 (-1: ��ȿ���� ���� ��)
	 *             <li>�ʱ� ��: -1 (��ȿ���� ���� ��)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_FRONT_OID = 0x00100011;
	/**
	 * <p>���� OID �̺�Ʈ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� OID �̺�Ʈ ����̽��� �����ʹ� �κ��� �ٴ� �鿡 �ִ� ���� OID ���� ��Ÿ����.
	 * OID ���� ���� ���� ��쿡�� -1�� ���� ������.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x00100012
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: -1 ~ 65535 (-1: ��ȿ���� ���� ��)
	 *             <li>�ʱ� ��: -1 (��ȿ���� ���� ��)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_BACK_OID = 0x00100012;
}