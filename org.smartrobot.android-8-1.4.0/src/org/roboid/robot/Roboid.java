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
 * <p>�κ��̵带 �����ϴ� ����̽��� ã�ų� �����ʸ� ��� �� �����ϴ� �޼ҵ带 �����Ѵ�.
 * </p>
 * 
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see Robot
 * @see Device
 * @see Device.DeviceDataChangedListener
 */
public interface Roboid extends NamedElement
{
	/**
	 * <p>�κ��̵��� �� ID�� ��ȯ�Ѵ�.
	 * </p>
	 * @return �κ��̵��� �� ID
	 */
	String getId();
	/**
	 * <p>�ڽ� ����̽� �߿��� �̸��� name�� ����̽��� ã�� �ν��Ͻ��� ��ȯ�Ѵ�.
	 * <p>�̸��� name�� �ڽ� ����̽��� ������ null�� ��ȯ�Ѵ�.
	 * �� ��, �ڽ� �κ��̵忡 ���Ե� ����̽��� �̸��� "�ڽķκ��̵�.����̽�"�� ���� "."���� ���е� ������ ����Ѵ�.
	 * ��: "Pen1.Button"
	 * </p>
	 * <pre class="prettyprint">
     * void someMethod(Roboid alpha)
 {
     Device leftWheel = alpha.findDeviceByName("LeftWheel"); // ���� �κ��� ���� ���� ������ ����̽��� ã�´�.
     Device button = alpha.findDeviceByName("Pen1.Button"); // 1�� ���� ��ư �̺�Ʈ ����̽��� ã�´�.
 }</pre>
	 * 
	 * @param name ã�� ����̽��� �̸�
	 * <p>
	 * @return ����̽��� �ν��Ͻ� �Ǵ� null
	 */
	Device findDeviceByName(String name);
	/**
	 * <p>�ڽ� ����̽� �߿��� ID�� deviceId�� ����̽��� ã�� �ν��Ͻ��� ��ȯ�Ѵ�.
	 * <p>ID�� deviceId�� �ڽ� ����̽��� ������ null�� ��ȯ�Ѵ�.
	 * </p>
	 * <pre class="prettyprint">
     * void someMethod(Roboid alpha)
 {
     Device leftWheel = alpha.findDeviceById(Alpha.EFFECTOR_LEFT_WHEEL); // ���� �κ��� ���� ���� ������ ����̽��� ã�´�.
 }</pre>
	 * 
	 * @param deviceId ã�� ����̽��� ID
	 * <p>
	 * @return ����̽��� �ν��Ͻ� �Ǵ� null
	 */
	Device findDeviceById(int deviceId);
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
