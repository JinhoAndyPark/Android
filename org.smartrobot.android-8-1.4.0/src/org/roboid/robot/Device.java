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

package org.roboid.robot;

/**
 * <p>����̽��� �����͸� �а� ���� �޼ҵ带 �����Ѵ�.
 * <p>����̽��� �����ʹ� �迭�� ����Ǿ� ������, �迭�� ������ ���� ũ��, ���� ������ ����̽��� ���� �ٸ���.
 * </p> 
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see Robot
 * @see Roboid
 * @see org.smartrobot.android.action.Action Action
 * @see Device.DeviceDataChangedListener
 */
public interface Device extends NamedElement
{
	/**
	 * <p>����̽��� �����Ͱ� ���ŵǾ��� �� ȣ��Ǵ� �޼ҵ带 �����Ѵ�.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Robot
	 * @see Roboid
	 * @see Device
	 */
	public interface DeviceDataChangedListener
	{
		/**
		 * <p>����̽��� �����Ͱ� ���ŵǾ��� �� ȣ��ȴ�.
		 * <p>values�� ������ �迭(int[]) �Ǵ� �Ǽ��� �迭(float[]), ��Ʈ�� �迭(String[])�� ������ ���� ������.
		 * </p>
		 * @param device �����Ͱ� ���ŵ� ����̽�
		 * @param values ����̽��� ������ �迭
		 * @param timestamp �����Ͱ� ���ŵ� �ð� (System.nanoTime() �޼ҵ�� ������ �ð�)
		 */
		void onDeviceDataChanged(Device device, Object values, long timestamp);
	}

	/**
	 * <p>����̽��� ID�� ��ȯ�Ѵ�.
	 * <p>�ֺ������ ��쿡�� ���� ��ǰ�� �������� �ʴ´�.
	 * </p>
	 * <pre class="prettyprint">
     * public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int accelerationY, dice;
     switch(device.getId()) // ����̽��� ID�� ��´�.
     {
     case Alpha.SENSOR_ACCELERATION: // ���� �κ��� ���ӵ� ���� ���� ���ŵǾ���.
         accelerationY = ((int[])values)[1];
         break;
     case Dice.EVENT_VALUE: // �ֻ����� �� ���� ���ŵǾ���. ��� �ֻ��������� �������� �ʴ´�.
         dice = ((int[])values)[0];
         break;
     }
 }</pre>
	 * 
	 * @return ����̽��� ID
	 * <p>
	 * @see Device.DeviceDataChangedListener
	 */
	int getId();
	/**
	 * <p>�ֺ������ ��ǰ ��ȣ�� ��ȯ�Ѵ�.
	 * <p>�κ��̳� �׼��� ��쿡�� 0�� ��ȯ�Ѵ�.
	 * </p>
	 * <pre class="prettyprint">
     * public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int dice1, dice2;
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
	 * @return �ֺ������ ��ǰ ��ȣ
	 * <p>
	 * @see Device.DeviceDataChangedListener
	 * @see kr.robomation.peripheral.Pen Pen
	 * @see kr.robomation.peripheral.Dice Dice
	 */
	int getProductId();
	/**
	 * <p>����̽��� ������ ���� ��ȯ�Ѵ�.
	 * <p>����̽��� ������ ���� ���� ��� ���� {@link DataType} Ŭ������ ���ǵǾ� �ִ�.
	 * </p>
	 * <pre class="prettyprint">
     * void someMethod(Robot robot)
 {
     Device device = robot.findDeviceById(Alpha.EFFECTOR_LEFT_WHEEL);
     if(device.getDataType() == DataType.INTEGER) // ������ ���� ������ �迭(int[])���� Ȯ���Ѵ�.
     {
         ...
     }
 }</pre>
     * 
	 * @return ����̽��� ������ ��
	 * <p>
	 * @see DataType#INTEGER
	 * @see DataType#FLOAT
	 * @see DataType#STRING
	 */
	int getDataType();
	/**
	 * <p>����̽��� ������ �迭 ũ�⸦ ��ȯ�Ѵ�.
	 * <p>���⼭�� ������ �迭 ũ��� �𵨿��� ������ �迭 ũ�⸦ �ǹ��Ѵ�.
	 * �迭 ũ�Ⱑ -1�� �����Ǿ� �ִ� ���� ������ �迭 ũ�Ⱑ �������̶�� �ǹ��̴�.
	 * �� ��쿡�� -1�� ��ȯ�Ѵ�.
	 * </p>
	 * @return ����̽��� ������ �迭 ũ��. ������ �迭 ũ�Ⱑ �������� ��쿡�� -1
	 */
	int getDataSize();
	/**
	 * <p>����̽��� �����Ͱ� ���ŵǾ����� Ȯ���Ѵ�.
	 * <p>���� �Ǵ� ������ ����̽��� �����Ͱ� ��� ���ŵǱ� ������ Ȯ���� �ʿ䰡 ������, Ŀ�ǵ� �Ǵ� �̺�Ʈ ����̽��� �����͸� �б� ���� �ݵ�� e() �޼ҵ带 ����Ͽ� �����Ͱ� ���ŵǾ����� Ȯ���ؾ� �Ѵ�.
	 * �׷��� ������ Ŀ�ǵ� �Ǵ� �̺�Ʈ�� �߻��� ������ �� ���� ���� ������ ���� �߻��� Ŀ�ǵ� �Ǵ� �̺�Ʈ�� �����Ͱ� �ƴ϶� ���ſ� �߻��Ͽ��� Ŀ�ǵ� �Ǵ� �̺�Ʈ�� �����͸� ���� ���� �ִ�.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Robot robot)
 {
     Device device = robot.findDeviceById(Alpha.EVENT_FRONT_OID); // ���� OID �̺�Ʈ ����̽��� ��´�.
     int oid;
     if(device.e()) // ���� OID ���� ���ŵǾ����� Ȯ���Ѵ�.
         oid = device.read(); // ���� OID ���� �д´�.
 }</pre>
	 *
	 * @return ����̽��� �����Ͱ� ���ŵǾ����� true, �ƴϸ� false
	 */
	boolean e();
	/**
	 * <p>����̽��� ������ �迭���� �ε��� 0�� ��ġ�� �ִ� �����͸� ���� ������ ��ȯ�Ѵ�.
	 * <p>����̽��� ������ �迭 ũ�Ⱑ 0�� ��쿡�� 0�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� 0�� ��ȯ�Ѵ�.
	 * </p>
	 * @return ����̽��� ������ �Ǵ� 0
	 */
	int read();
	/**
	 * <p>����̽��� ������ �迭���� �ε����� index�� ��ġ�� �ִ� �����͸� ���� ������ ��ȯ�Ѵ�.
	 * <p>index�� ����̽��� ������ �迭 ũ�⺸�� ũ�ų� ���� ���, Ȥ�� index�� ������ ��쿡�� 0�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� 0�� ��ȯ�Ѵ�.
	 * </p>
	 * @param index ����̽� ������ �迭�� �ε���
	 * <p>
	 * @return ����̽��� ������ �Ǵ� 0
	 */
	int read(int index);
	/**
	 * <p>����̽��� ������ �迭�� ������ �迭 data�� �����ϰ�, ������ �������� ������ ��ȯ�Ѵ�.
	 * <p>����̽��� ������ �迭 ũ�Ⱑ data �迭�� ũ�⺸�� ū ��쿡�� data �迭�� ũ�⸸ŭ�� �����Ѵ�.
	 * ����̽��� ������ �迭 ũ�Ⱑ data �迭�� ũ�⺸�� ���� ��쿡�� ����̽��� ������ �迭�� ��� ������ �� data �迭�� ������ �κ��� 0���� ä���.
	 * data �迭�� null�� ���, data �迭�� ũ�Ⱑ 0�� ���, ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� �������� �ʰ� 0�� ��ȯ�Ѵ�.
	 * </p>
	 * @param data ����̽��� ������ �迭�� ������ ������ �迭
	 * <p>
	 * @return ������ �������� ����
	 */
	int read(int[] data);
	/**
	 * <p>����̽��� ������ �迭���� �ε��� 0�� ��ġ�� �ִ� �����͸� �Ǽ� ������ ��ȯ�Ѵ�.
	 * <p>����̽��� ������ �迭 ũ�Ⱑ 0�� ��쿡�� 0.0�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� 0.0�� ��ȯ�Ѵ�.
	 * </p>
	 * @return ����̽��� ������ �Ǵ� 0.0
	 */
	float readFloat();
	/**
	 * <p>����̽��� ������ �迭���� �ε����� index�� ��ġ�� �ִ� �����͸� �Ǽ� ������ ��ȯ�Ѵ�.
	 * <p>index�� ����̽��� ������ �迭 ũ�⺸�� ũ�ų� ���� ���, Ȥ�� index�� ������ ��쿡�� 0.0�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� 0.0�� ��ȯ�Ѵ�.
	 * </p>
	 * @param index ����̽� ������ �迭�� �ε���
	 * <p>
	 * @return ����̽��� ������ �Ǵ� 0.0
	 */
	float readFloat(int index);
	/**
	 * <p>����̽��� ������ �迭�� �Ǽ��� �迭 data�� �����ϰ�, ������ �������� ������ ��ȯ�Ѵ�.
	 * <p>����̽��� ������ �迭 ũ�Ⱑ data �迭�� ũ�⺸�� ū ��쿡�� data �迭�� ũ�⸸ŭ�� �����Ѵ�.
	 * ����̽��� ������ �迭 ũ�Ⱑ data �迭�� ũ�⺸�� ���� ��쿡�� ����̽��� ������ �迭�� ��� ������ �� data �迭�� ������ �κ��� 0.0���� ä���.
	 * data �迭�� null�� ���, data �迭�� ũ�Ⱑ 0�� ���, ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� �������� �ʰ� 0�� ��ȯ�Ѵ�.
	 * </p>
	 * @param data ����̽��� ������ �迭�� ������ �Ǽ��� �迭
	 * <p>
	 * @return ������ �������� ����
	 */
	int readFloat(float[] data);
	/**
	 * <p>����̽��� ������ �迭���� �ε��� 0�� ��ġ�� �ִ� �����͸� ��Ʈ������ ��ȯ�Ѵ�.
	 * <p>����̽��� ������ �迭 ũ�Ⱑ 0�� ��쿡�� ""�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� �ƴ� ��쿡�� ""�� ��ȯ�Ѵ�.
	 * </p>
	 * @return ����̽��� ������ �Ǵ� ""
	 */
	String readString();
	/**
	 * <p>����̽��� ������ �迭���� �ε����� index�� ��ġ�� �ִ� �����͸� ��Ʈ������ ��ȯ�Ѵ�.
	 * <p>index�� ����̽��� ������ �迭 ũ�⺸�� ũ�ų� ���� ���, Ȥ�� index�� ������ ��쿡�� ""�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� �ƴ� ��쿡�� ""�� ��ȯ�Ѵ�.
	 * </p>
	 * @param index ����̽� ������ �迭�� �ε���
	 * <p>
	 * @return ����̽��� ������ �Ǵ� ""
	 */
	String readString(int index);
	/**
	 * <p>����̽��� ������ �迭�� ��Ʈ�� �迭 data�� �����ϰ�, ������ �������� ������ ��ȯ�Ѵ�.
	 * <p>����̽��� ������ �迭 ũ�Ⱑ data �迭�� ũ�⺸�� ū ��쿡�� data �迭�� ũ�⸸ŭ�� �����Ѵ�.
	 * ����̽��� ������ �迭 ũ�Ⱑ data �迭�� ũ�⺸�� ���� ��쿡�� ����̽��� ������ �迭�� ��� ������ �� data �迭�� ������ �κ��� ""���� ä���.
	 * data �迭�� null�� ���, data �迭�� ũ�Ⱑ 0�� ���, ����̽��� ������ ���� {@link DataType#STRING}�� �ƴ� ��쿡�� �������� �ʰ� 0�� ��ȯ�Ѵ�.
	 * </p>
	 * @param data ����̽��� ������ �迭�� ������ ��Ʈ�� �迭
	 * <p>
	 * @return ������ �������� ����
	 */
	int readString(String[] data);
	/**
	 * <p>����̽��� ������ �迭���� �ε��� 0�� ��ġ�� ���� �� data�� ����.
	 * <p>����̽��� ������ �迭 ũ�Ⱑ 0�� ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * </p>
	 * @param data ����̽��� �� ���� �� ������
	 * <p>
	 * @return �����ϸ� true, �����ϸ� false
	 */
	boolean write(int data);
	/**
	 * <p>����̽��� ������ �迭���� �ε����� index�� ��ġ�� ���� �� data�� ����.
	 * <p>index�� ����̽��� ������ �迭 ũ�⺸�� ũ�ų� ���� ���, Ȥ�� index�� ������ ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * </p>
	 * @param index ����̽� ������ �迭�� �ε���
	 * @param data ����̽��� �� ���� �� ������
	 * <p>
	 * @return �����ϸ� true, �����ϸ� false
	 */
	boolean write(int index, int data);
	/**
	 * <p>������ �迭 data�� ����̽��� ������ �迭�� �����ϰ�, ������ �������� ������ ��ȯ�Ѵ�.
	 * <p>data �迭�� ũ�Ⱑ ����̽��� ������ �迭 ũ�⺸�� ū ��쿡�� ����̽��� ������ �迭 ũ�⸸ŭ�� �����Ѵ�.
	 * data �迭�� ũ�Ⱑ ����̽��� ������ �迭 ũ�⺸�� ���� ��쿡�� data �迭�� ��� ������ �� ����̽� ������ �迭�� ������ �κ��� 0���� ä���.
	 * data �迭�� null�� ���, data �迭�� ũ�Ⱑ 0�� ���, ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� �������� �ʰ� 0�� ��ȯ�Ѵ�.
	 * ����̽��� ������ �迭 ũ�Ⱑ �������� ���(������ �迭 ũ�Ⱑ -1�� ������ ���)���� data �迭�� ũ�⸸ŭ ����̽��� ������ �迭�� ���� ���� data �迭�� �����Ѵ�.
	 * </p>
	 * @param data ����̽��� ������ �迭�� ������ ������ �迭
	 * <p>
	 * @return ������ �������� ����
	 */
	int write(int[] data);
	/**
	 * <p>����̽��� ������ �迭���� �ε��� 0�� ��ġ�� �Ǽ� �� data�� ����.
	 * <p>����̽��� ������ �迭 ũ�Ⱑ 0�� ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * </p>
	 * @param data ����̽��� �� �Ǽ� �� ������
	 * <p>
	 * @return �����ϸ� true, �����ϸ� false
	 */
	boolean writeFloat(float data);
	/**
	 * <p>����̽��� ������ �迭���� �ε����� index�� ��ġ�� �Ǽ� �� data�� ����.
	 * <p>index�� ����̽��� ������ �迭 ũ�⺸�� ũ�ų� ���� ���, Ȥ�� index�� ������ ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * </p>
	 * @param index ����̽� ������ �迭�� �ε���
	 * @param data ����̽��� �� �Ǽ� �� ������
	 * <p>
	 * @return �����ϸ� true, �����ϸ� false
	 */
	boolean writeFloat(int index, float data);
	/**
	 * <p>�Ǽ��� �迭 data�� ����̽��� ������ �迭�� �����ϰ�, ������ �������� ������ ��ȯ�Ѵ�.
	 * <p>data �迭�� ũ�Ⱑ ����̽��� ������ �迭 ũ�⺸�� ū ��쿡�� ����̽��� ������ �迭 ũ�⸸ŭ�� �����Ѵ�.
	 * data �迭�� ũ�Ⱑ ����̽��� ������ �迭 ũ�⺸�� ���� ��쿡�� data �迭�� ��� ������ �� ����̽� ������ �迭�� ������ �κ��� 0.0���� ä���.
	 * data �迭�� null�� ���, data �迭�� ũ�Ⱑ 0�� ���, ����̽��� ������ ���� {@link DataType#STRING}�� ��쿡�� �������� �ʰ� 0�� ��ȯ�Ѵ�.
	 * ����̽��� ������ �迭 ũ�Ⱑ �������� ���(������ �迭 ũ�Ⱑ -1�� ������ ���)���� data �迭�� ũ�⸸ŭ ����̽��� ������ �迭�� ���� ���� data �迭�� �����Ѵ�.
	 * </p>
	 * @param data ����̽��� ������ �迭�� ������ �Ǽ��� �迭
	 * <p>
	 * @return ������ �������� ����
	 */
	int writeFloat(float[] data);
	/**
	 * <p>����̽��� ������ �迭���� �ε��� 0�� ��ġ�� ��Ʈ�� data�� ����.
	 * <p>����̽��� ������ �迭 ũ�Ⱑ 0�� ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� �ƴ� ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * </p>
	 * @param data ����̽��� �� ��Ʈ�� ������
	 * <p>
	 * @return �����ϸ� true, �����ϸ� false
	 */
	boolean writeString(String data);
	/**
	 * <p>����̽��� ������ �迭���� �ε����� index�� ��ġ�� ��Ʈ�� data�� ����.
	 * <p>index�� ����̽��� ������ �迭 ũ�⺸�� ũ�ų� ���� ���, Ȥ�� index�� ������ ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * ����̽��� ������ ���� {@link DataType#STRING}�� �ƴ� ��쿡�� ���� �ʰ� false�� ��ȯ�Ѵ�.
	 * </p>
	 * @param index ����̽� ������ �迭�� �ε���
	 * @param data ����̽��� �� ��Ʈ�� ������
	 * <p>
	 * @return �����ϸ� true, �����ϸ� false
	 */
	boolean writeString(int index, String data);
	/**
	 * <p>��Ʈ�� �迭 data�� ����̽��� ������ �迭�� �����ϰ�, ������ �������� ������ ��ȯ�Ѵ�.
	 * <p>data �迭�� ũ�Ⱑ ����̽��� ������ �迭 ũ�⺸�� ū ��쿡�� ����̽��� ������ �迭 ũ�⸸ŭ�� �����Ѵ�.
	 * data �迭�� ũ�Ⱑ ����̽��� ������ �迭 ũ�⺸�� ���� ��쿡�� data �迭�� ��� ������ �� ����̽� ������ �迭�� ������ �κ��� ""���� ä���.
	 * data �迭�� null�� ���, data �迭�� ũ�Ⱑ 0�� ���, ����̽��� ������ ���� {@link DataType#STRING}�� �ƴ� ��쿡�� �������� �ʰ� 0�� ��ȯ�Ѵ�.
	 * ����̽��� ������ �迭 ũ�Ⱑ �������� ���(������ �迭 ũ�Ⱑ -1�� ������ ���)���� data �迭�� ũ�⸸ŭ ����̽��� ������ �迭�� ���� ���� data �迭�� �����Ѵ�.
	 * </p>
	 * @param data ����̽��� ������ �迭�� ������ ��Ʈ�� �迭
	 * <p>
	 * @return ������ �������� ����
	 */
	int writeString(String[] data);
}