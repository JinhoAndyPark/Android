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
 * <p>���� �� ID�� ���� �����ϴ� �� ����̽��� ID�� ���� ��� ���� �����Ѵ�.
 * <p>���� 2���� ���� ����̽��� 2���� �̺�Ʈ ����̽��� �����Ǿ� ������, �� ����̽��� �����ʹ� ������ �迭�� ����Ǿ� �ִ�.
 * <p>���� ����̽� ID ���� ���� ��ǰ�� �������� �ʴ´�.
 * ���� ���, 1�� ��� 2�� ���� �ִٰ� ���� �� ���� ����̽� ID�����δ� ��� ���� ����̽����� ������ �� ����.
 * ���� ��ǰ�� �����ϰ��� �ϴ� ��쿡�� ��ǰ�� ���� �߰����� ����, �� ��ǰ ��ȣ�� �ʿ��ϴ�.
 * �ֺ������ ��ǰ ��ȣ�� ��ó���� �ֺ���⸦ ����� �� ǥ�õǸ�, {@link org.roboid.robot.Device Device} Ŭ������ {@link org.roboid.robot.Device#getProductId() getProductId()} �޼ҵ带 ����Ͽ� ���� �� �ִ�.
 * <p>�̿� ���� ��ǰ�� �����ϴ� ���� �ֺ���⿡�� �ش�ȴ�.
 * �κ��̳� �׼��� ��쿡�� ������ ��ǰ�� �����Ƿ� �ش���� �ʴ´�.
 * <p>���� �����ϴ� ����̽��� �����͸� �б� ���ؼ��� �켱 {@link org.roboid.robot.Robot Robot} Ŭ������ {@link org.roboid.robot.Robot#findDeviceById(int, int) findDeviceById(int productId, int deviceId)} �޼ҵ带 ����Ͽ� ����̽� ��ü�� ���۷����� ���� �Ѵ�.
 * productId���� ���� ��ǰ ��ȣ�� �Է��ϰ� deviceId���� ����̽��� ID�� �Է��Ѵ�.
 * �̷��� ����� ����̽� ��ü�� ���� read �޼ҵ带 ����Ͽ� ����̽��� �����͸� ���� �� �ִ�.
 * <p>���ø����̼ǿ��� ����ϴ� ���� Ư�� ������ �����ؾ� �� ������ ���ٸ� ��ǰ�� �������� �ʰ� � ���̵��� ��������� �۾��ϴ� ���� �� ����.
 * ����ڰ� � ���� ������� �� �� ���� �����̴�.
 * �� ��쿡�� {@link org.roboid.robot.Device.DeviceDataChangedListener#onDeviceDataChanged(org.roboid.robot.Device, Object, long) onDeviceDataChanged} �ݹ� �޼ҵ带 ���� ����̽��� �����͸� ��� ���� �� �����ϴ�.
 * <p>�̿� ����, ���� ���� �� ����ϴ� ���ø����̼��� ��쿡�� ���ø����̼ǰ� ����� ��Ű���� �����Ͽ� Ư�� ��(���� ��, �Ķ� ��, �䳢 �׸� ��, ���� �׸� ��)���� �����ϰ� ����ڰ� ���� ������ �� �ְ� �ؾ� �ϸ�, �ݵ�� �Ʒ��� ���� ��ǰ ��ȣ�� �� ���� �����ؾ� �Ѵ�.
 * </p>
 * <pre class="prettyprint">
 * void someMethod(Robot robot)
 {
     Device device = robot.findDeviceById(1, Pen.EVENT_OID); // 1�� ���� OID �̺�Ʈ ����̽��� ��´�.
     int oid;
     if(device.e()) // 1�� ���� OID ���� ���ŵǾ����� Ȯ���Ѵ�.
         oid = device.read(); // 1�� ���� OID ���� �д´�.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int oid, oid1, oid2;
     // 1�� ��� 2�� ���� �������� �ʴ� ���
      switch(device.getId())
     {
     case Pen.EVENT_OID: // ����ڰ� ����ϴ� ���� OID ���� ���ŵǾ���.
         oid = ((int[])values)[0];
         break;
     }

     // 1�� ��� 2�� ���� �����ϴ� ���
      switch(device.getId())
     {
     case Pen.EVENT_OID:
         if(device.getProductId() == 1) // 1�� ���� OID ���� ���ŵǾ���.
             oid1 = ((int[])values)[0];
         else if(device.getProductId() == 2) // 2�� ���� OID ���� ���ŵǾ���.
             oid2 = ((int[])values)[0];
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
public final class Pen
{
	/**
	 * <p>���� �� ID�� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: "kr.robomation.peripheral.pen"
	 * </ul>
	 */
	public static final String ID = "kr.robomation.peripheral.pen";
	
	/**
	 * <p>��ȣ ���� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>��ȣ ���� ���� ����̽��� �����ʹ� ��� �κ� ��ü ���� ���׺� ���� ����� ��ȣ ���⸦ ��Ÿ����.
	 * ��ȣ�� ���Ⱑ Ŀ������ ���� Ŀ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x80100000
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
	public static final int SENSOR_SIGNAL = 0x80100000;
	/**
	 * <p>���͸� ���� ����̽��� ID�� ��Ÿ���� ���.
	 * <p>���͸� ���� ����̽��� �����ʹ� ���� ���͸� �ܷ��� %�� ��Ÿ����.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x80100001
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
	public static final int SENSOR_BATTERY = 0x80100001;
	/**
	 * <p>OID �̺�Ʈ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>OID �̺�Ʈ ����̽��� �����ʹ� ���� OID ���� ��Ÿ����.
	 * OID ���� ���� ���� ��쿡�� -1�� ���� ������.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x80100002
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
	public static final int EVENT_OID = 0x80100002;
	/**
	 * <p>��ư �̺�Ʈ ����̽��� ID�� ��Ÿ���� ���.
	 * <p>��ư �̺�Ʈ ����̽��� �����ʹ� ���� ��ư ���¸� ��Ÿ����.
	 * ��ư�� ������ 1, ������ ������ 0�� ���� ������.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 0x80100003
	 *     <li>����̽��� ������ �迭
	 *         <ul>
	 *             <li>������ ��: int [ ]
	 *             <li>�迭 ũ��: 1
	 *             <li>���� ����: 0 �Ǵ� 1 (0: ��ư�� ������ ���� ����, 1: ��ư�� ���� ����)
	 *             <li>�ʱ� ��: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_BUTTON = 0x80100003;
}