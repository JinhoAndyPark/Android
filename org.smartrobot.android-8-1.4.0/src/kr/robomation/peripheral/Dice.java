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

package kr.robomation.peripheral;

/**
 * <p>�ֻ����� �� ID�� �ֻ����� �����ϴ� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
 * <p>�ֻ����� 4���� ���� ����̽��� 2���� �̺�Ʈ ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� ������ �迭�� ����Ǿ� �ִ�.
 * <p>�ֻ����� ����̽� ID ���� ���� ��ǰ�� �������� �ʴ´�.
 * ���� ���, 1�� �ֻ����� 2�� �ֻ����� �ִٰ� ���� �� �ֻ����� ����̽� ID�����δ� ��� �ֻ����� ����̽����� ������ �� ����.
 * ���� ��ǰ�� �����ϰ��� �ϴ� ��쿡�� ��ǰ�� ���� �߰����� ����, �� ��ǰ ��ȣ�� �ʿ��ϴ�.
 * �ֺ������ ��ǰ ��ȣ�� ��ó���� �ֺ���⸦ ����� �� ǥ�õǸ�, {@link org.roboid.robot.Device Device} Ŭ������ {@link org.roboid.robot.Device#getProductId() getProductId()} �޼ҵ带 ����Ͽ� ���� �� �ִ�.
 * <p>�̿� ���� ��ǰ�� �����ϴ� ���� �ֺ���⿡�� �ش�ȴ�.
 * �κ��̳� �׼��� ��쿡�� ������ ��ǰ�� �����Ƿ� �ش���� �ʴ´�.
 * <p>�ֻ����� �����ϴ� ����̽��� �����͸� �б� ���ؼ��� �켱 {@link org.roboid.robot.Robot Robot} Ŭ������ {@link org.roboid.robot.Robot#findDeviceById(int, int) findDeviceById(int productId, int deviceId)} �޼ҵ带 ����Ͽ� ����̽� ��ü�� ���۷����� ���� �Ѵ�.
 * productId���� �ֻ����� ��ǰ ��ȣ�� �Է��ϰ� deviceId���� ����̽��� ID�� �Է��Ѵ�.
 * �̷��� ����� ����̽� ��ü�� ���� read �޼ҵ带 ����Ͽ� ����̽��� �����͸� ���� �� �ִ�.
 * <p>���ø����̼ǿ��� ����ϴ� �ֻ����� Ư�� �ֻ����� �����ؾ� �� ������ ���ٸ� ��ǰ�� �������� �ʰ� � �ֻ������� ��������� �۾��ϴ� ���� �� ����.
 * ����ڰ� � �ֻ����� ������� �� �� ���� �����̴�.
 * �� ��쿡�� {@link org.roboid.robot.Device.DeviceDataChangedListener#onDeviceDataChanged(org.roboid.robot.Device, Object, long) onDeviceDataChanged} �ݹ� �޼ҵ带 ���� ����̽��� �����͸� ��� ���� �� �����ϴ�.
 * <p>�̿� ����, �ֻ����� ���� �� ����ϴ� ���ø����̼��� ��쿡�� ���ø����̼ǰ� �ֻ������� ��Ű���� �����Ͽ� Ư�� �ֻ���(���� �ֻ���, �Ķ� �ֻ���, �䳢 �׸� �ֻ���, ���� �׸� �ֻ���)�� �����ϰ� ����ڰ� ���� ������ �� �ְ� �ؾ� �ϸ�, �ݵ�� �Ʒ��� ���� ��ǰ ��ȣ�� �� �ֻ����� �����ؾ� �Ѵ�.
 * </p>
 * <pre class="prettyprint">
 * void someMethod(Robot robot)
 {
     Device device = robot.findDeviceById(1, Dice.EVENT_VALUE); // 1�� �ֻ����� �� �̺�Ʈ ����̽��� ��´�.
     int dice;
     if(device.e()) // 1�� �ֻ����� �� ���� ���ŵǾ����� Ȯ���Ѵ�.
         dice = device.read(); // 1�� �ֻ����� �� ���� �д´�.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int dice, dice1, dice2;
     // 1�� �ֻ����� 2�� �ֻ����� �������� �ʴ� ���
      switch(device.getId())
     {
     case Dice.EVENT_VALUE: // ����ڰ� ����ϴ� �ֻ����� �� ���� ���ŵǾ���.
         dice = ((int[])values)[0];
         break;
     }

     // 1�� �ֻ����� 2�� �ֻ����� �����ϴ� ���
      switch(device.getId())
     {
     case Dice.EVENT_VALUE:
         if(device.getProductId() == 1) // 1�� �ֻ����� �� ���� ���ŵǾ���.
             dice1 = ((int[])values)[0];
         else if(device.getProductId() == 2) // 2�� �ֻ����� �� ���� ���ŵǾ���.
             dice2 = ((int[])values)[0];
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
public final class Dice
{
	/**
	 * <p>�ֻ����� �� ID�� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: "kr.robomation.peripheral.dice"
	 * </ul>
	 */
	public static final String ID = "kr.robomation.peripheral.dice";
	
	/**
	 * <p>��ȣ ���� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>��ȣ ���� ���� ����̽��� �����ʹ� �ֻ����� �κ� ��ü ���� ���׺� ���� ����� ��ȣ ���⸦ ��Ÿ����.
	 * ��ȣ�� ���Ⱑ Ŀ������ ���� Ŀ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x80200000
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: 0 ~ 255
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_SIGNAL = 0x80200000;
	/**
	 * <p>�µ� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>�µ� ���� ����̽��� �����ʹ� �ֻ��� ������ �µ��� ��Ÿ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x80200001
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
	public static final int SENSOR_TEMPERATURE = 0x80200001;
	/**
	 * <p>���͸� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���͸� ���� ����̽��� �����ʹ� �ֻ����� ���͸� �ܷ��� %�� ��Ÿ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x80200002
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
	public static final int SENSOR_BATTERY = 0x80200002;
	/**
	 * <p>���ӵ� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���ӵ� ���� ����̽��� �����ʹ� �ֻ����� 3�� ���ӵ� ���� ���� ��Ÿ����.
	 * ũ�� 3�� ������ �迭���� ù ��° ���� X��, �� ��° ���� Y��, �� ��° ���� Z���� ���ӵ� ���̴�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x80200003
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
	public static final int SENSOR_ACCELERATION = 0x80200003;
	/**
	 * <p>���� ���� �̺�Ʈ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���� ���� �̺�Ʈ ����̽��� �����ʹ� �ֻ����� ���� ���� ���� ��Ÿ����.
	 * �ֻ����� �������� ���� ������ ���� 1�� �����Ѵ�.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x80200004
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: -1 ~ 255 (-1: ��ȿ���� ���� ��)
	 *             <li>�ʱ� ��: -1 (��ȿ���� ���� ��)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_FALL = 0x80200004;
	/**
	 * <p>�ֻ��� �� �̺�Ʈ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>�ֻ��� �� �̺�Ʈ ����̽��� �����ʹ� �ֻ����� ���� ��Ÿ����.
	 * �ֻ����� ������ �������� ���� ���ϰ� ���� ���� 1 ~ 6�� ���� ������, �ֻ����� ������ �ִ� ��쿡�� -1 ~ -6�� ���� ������.
	 * ���� ���� ��� (�ֻ����� ������ �ִ� ���) ��ȣ�� ������ ����ġ�� ���� ����� ���� ���� ǥ���Ѵ�.
	 * ��, �ֻ����� ������ 3�� ��� ������ �������� ���� ���ϰ� ���� ���� 3, ������ ���� ���� -3�� ���� ������.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x80200005
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: -6 ~ 6 (0: ��ȿ���� ���� ��)
	 *             <li>�ʱ� ��: 0 (��ȿ���� ���� ��)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_VALUE = 0x80200005;
}