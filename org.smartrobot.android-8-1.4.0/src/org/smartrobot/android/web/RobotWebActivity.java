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

package org.smartrobot.android.web;

import org.roboid.robot.Device;
import org.roboid.robot.Robot;
import org.smartrobot.android.SmartRobot;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * <p>{@link org.smartrobot.android.web.RobotWebView RobotWebView}�� ��ü ȭ������ ǥ���ϰ�, �κ��� �����ֱ�� �̺�Ʈ�� ó���ϴ� ���� �⺻���� �۾��� �س��� ��Ƽ��Ƽ.
 * <p>�κ��� �����ϴ� ����̽��� �����ʹ� {@link #onActivated()} �޼ҵ尡 ȣ��� ���ĺ��� {@link #onDeactivated()} �޼ҵ尡 ȣ��Ǳ� ������ �аų� �� �� �ִ�.
 * </p>
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
 * @see org.smartrobot.android.SmartRobot SmartRobot
 * @see org.smartrobot.android.SmartRobot.Callback SmartRobot.Callback
 */
public class RobotWebActivity extends Activity implements SmartRobot.Callback, Device.DeviceDataChangedListener
{
	private RobotWebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mWebView = new RobotWebView(this);
        setContentView(mWebView);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		SmartRobot.activate(getApplicationContext(), this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		SmartRobot.deactivate();
	}

	/**
	 * <p>���Ե� ���並 ��ȯ�Ѵ�.
	 * <p>����� onCreate �޼ҵ� ������ �����ȴ�.
	 * </p>
	 * @return ����
	 */
	protected WebView getWebView()
	{
		return mWebView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onInitialized(Robot robot)
	{
		robot.addDeviceDataChangedListener(this);
		mWebView.onInitialized(robot);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onActivated()
	{
		mWebView.onActivated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDeactivated()
	{
		mWebView.onDeactivated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisposed()
	{
		mWebView.onDisposed();
		finish();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onExecute()
	{
		mWebView.onExecute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStateChanged(int state)
	{
		mWebView.onStateChanged(state);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNameChanged(String name)
	{
		mWebView.onNameChanged(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDeviceDataChanged(Device device, Object values, long timestamp)
	{
	}
}