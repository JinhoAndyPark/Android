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

import org.roboid.robot.Device.DeviceDataChangedListener;

/**
 * <p>�κ��� �����ϴ� ����̽��� ã�ų� �����ʸ� ��� �� �����ϴ� �޼ҵ带 �����Ѵ�.
 * </p>
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see Roboid
 * @see Device
 * @see Device.DeviceDataChangedListener
 */
public interface Robot extends NamedElement
{
	/**
	 * <p>�ϵ���� �κ��� ������� ����� ���� ������ ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 1
	 * </ul>
	 */
	int STATE_CONNECTING = 1;
	/**
	 * <p>�ϵ���� �κ��� ������� ����� ����Ǿ����� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 2
	 * </ul>
	 */
	int STATE_CONNECTED = 2;
	/**
	 * <p>�ϵ���� �κ��� ������� ��� ������ ������������ ���������� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 3
	 * </ul>
	 */
	int STATE_CONNECTION_LOST = 3;
	/**
	 * <p>�ϵ���� �κ��� ������� ����� ���������� ����Ǿ����� ��Ÿ���� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: 4
	 * </ul>
	 */
	int STATE_DISCONNECTED = 4;
	
	/**
	 * <p>�κ��� ID�� ��ȯ�Ѵ�.
	 * </p>
	 * @return �κ��� ID
	 */
	String getId();
	/**
	 * <p>�ڽ� ����̽� �߿��� ID�� deviceId�� ����̽��� ã�� �ν��Ͻ��� ��ȯ�Ѵ�.
	 * <p>ID�� deviceId�� �ڽ� ����̽��� ������ null�� ��ȯ�Ѵ�.
	 * <p>�ֺ������ ��쿡�� {@link #findDeviceById(int, int) findDeviceById(int productId, int deviceId)} �޼ҵ带 ����ؾ� �Ѵ�.
	 * </p>
	 * <pre class="prettyprint">
     * void someMethod(Robot robot)
 {
     Device leftWheel = robot.findDeviceById(Alpha.EFFECTOR_LEFT_WHEEL); // ���� �κ��� ���� ���� ������ ����̽��� ã�´�.
 }</pre>
	 * 
	 * @param deviceId ã�� ����̽��� ID
	 * <p>
	 * @return ����̽��� �ν��Ͻ� �Ǵ� null
	 */
	Device findDeviceById(int deviceId);
	/**
	 * <p>��ǰ ��ȣ�� productId�� �ֺ������ �ڽ� ����̽� �߿��� ID�� deviceId�� ����̽��� ã�� �ν��Ͻ��� ��ȯ�Ѵ�.
	 * <p>ID�� deviceId�� �ڽ� ����̽��� ������ null�� ��ȯ�Ѵ�.
	 * <p>�κ��� ��쿡�� {@link #findDeviceById(int) findDeviceById(int deviceId)} �޼ҵ带 ����ؾ� �Ѵ�.
	 * </p>
	 * <pre class="prettyprint">
     * void someMethod(Robot robot)
 {
     Device button = robot.findDeviceById(1, Pen.EVENT_BUTTON); // 1�� ���� ��ư �̺�Ʈ ����̽��� ã�´�.
 }</pre>
	 * 
	 * @param productId ��ǰ ��ȣ
	 * @param deviceId ã�� ����̽��� ID
	 * <p>
	 * @return ����̽��� �ν��Ͻ� �Ǵ� null
	 */
	Device findDeviceById(int productId, int deviceId);
	/**
	 * <p>����̽��� �����Ͱ� ���ŵǾ��� �� ȣ��ǵ��� listener�� ����Ѵ�.
	 * </p>
	 * @param listener ����� ������
	 */
	void addDeviceDataChangedListener(DeviceDataChangedListener listener);
	/**
	 * <p>��ϵ� ������ ��Ͽ��� listener�� �����Ѵ�.
	 * </p>
	 * @param listener ������ ������
	 */
	void removeDeviceDataChangedListener(DeviceDataChangedListener listener);
	/**
	 * <p>��ϵ� ��� �����ʸ� �����Ѵ�.
	 */
	void clearDeviceDataChangedListener();
}
