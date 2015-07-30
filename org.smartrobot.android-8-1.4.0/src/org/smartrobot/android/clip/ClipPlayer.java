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

package org.smartrobot.android.clip;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

/**
 * <p>�κ��̵� ��Ʃ����� ���� Ŭ�� ������ ���ų� �ݰ�, ��� �� �����ϴ� �޼ҵ带 �����Ѵ�.
 * </p>
 * <pre class="prettyprint">
 * public SampleActivity extends Activity
 {
     private ClipPlayer mClipPlayer;

     protected void onCreate(Bundle savedInstanceState)
     {
         super.onCreate(savedInstanceState);
         mClipPlayer = ClipPlayer.obtain(this, 0);
     }

     protected void onStart()
     {
         super.onStart();
         mClipPlayer.open("org.smartrobot.sample", R.raw.sample);
     }

     protected void onStop()
     {
         super.onStop();
         mClipPlayer.close();
     }

     public void onClick(View v)
     {
         switch(v.getId())
         {
         case R.id.play:
             mClipPlayer.play();
             break;
         case R.id.stop:
             mClipPlayer.stop();
             break;
         }
     }
 }</pre>
 * 
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public final class ClipPlayer
{
	private static final int ERROR_NONE = 0;
	/**
	 * <p>Ŭ�� ������� ID�� �߸� �Ǿ����� ��Ÿ���� ���� �ڵ� ���.
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
	 * <p>Ŭ�� ������ URL�� �߸� �Ǿ����� ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -3
	 * </ul>
	 */
	public static final int ERROR_INVALID_URL = -3;
	/**
	 * <p>��Ű�� �̸��� �߸� �Ǿ����� ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -4
	 * </ul>
	 */
	public static final int ERROR_INVALID_PACKAGE = -4;
	/**
	 * <p>Ŭ�� ������ ���ҽ� ID �Ǵ� ���ҽ� �̸��� �߸� �Ǿ����� ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -5
	 * </ul>
	 */
	public static final int ERROR_INVALID_RESOURCE = -5;
	/**
	 * <p>�߸��� Ŭ������ ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -6
	 * </ul>
	 */
	public static final int ERROR_INVALID_CLIP = -6;
	/**
	 * <p>Ŭ�� ������ �߸� �Ǿ����� ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -7
	 * </ul>
	 */
	public static final int ERROR_INVALID_FILE = -7;
	/**
	 * <p>Ŭ�� ������ ���ų� ���, �����ϴ� ������ �߸� �Ǿ����� ��Ÿ���� ���� �ڵ� ���.
	 * </p>
	 * <ul>
	 *     <li>��� ��: -8
	 * </ul>
	 */
	public static final int ERROR_ILLEGAL_STATE = -8;
	
	private static final int MSG_COMPLETION = 1;
	private static final int MSG_ERROR = 2;
	
	/**
	 * <p>Ŭ�� ������ ����� �Ϸ�Ǿ��� �� ȣ��Ǵ� �޼ҵ带 �����Ѵ�.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see ClipPlayer
	 */
	public interface OnCompletedListener
	{
		/**
		 * <p>Ŭ�� ������ ����� �Ϸ�Ǿ��� �� ȣ��ȴ�.
		 * </p>
		 * @param clipPlayer ����� �Ϸ�� Ŭ�� �����
		 */
		void onCompleted(ClipPlayer clipPlayer);
	}
	
	/**
	 * <p>Ŭ�� ������ ���ų� ���, �����ϴ� �������� ������ �߻����� �� ȣ��Ǵ� �޼ҵ带 �����Ѵ�.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see ClipPlayer
	 */
	public interface OnErrorListener
	{
		/**
		 * <p>Ŭ�� ������ ���ų� ���, �����ϴ� �������� ������ �߻����� �� ȣ��ȴ�.
		 * <p>���� �ڵ� ���� {@link ClipPlayer} Ŭ������ ���ǵǾ� �ִ�.
		 * </p>
		 * @param clipPlayer ������ �߻��� Ŭ�� �����
		 * @param errorCode ���� �ڵ� ��
		 */
		void onError(ClipPlayer clipPlayer, int errorCode);
	}
	
	private WeakReference<Context> mContext;
	OnCompletedListener mOnCompletedListener;
	OnErrorListener mOnErrorListener;
	static final SparseArray<ClipPlayer> mClipPlayers = new SparseArray<ClipPlayer>();
	final String mRequestCode;
	private final int mID;
	private boolean mOpen;
	boolean mPlaying;
	private BroadcastReceiver mBR;
	private final EventHandler mEventHandler;
	
	private ClipPlayer(String packageName, int id)
	{
		mID = id;
		mRequestCode = packageName + "_" + id;
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
	 * <p>�־��� ID�� ���� Ŭ�� ������� �ν��Ͻ��� ��´�.
	 * <p>context�� null�̸� null�� ��ȯ�Ѵ�.
	 * clipPlayerId�� Ŭ�� ����⸦ �����ϱ� ���� ����ڰ� �����ϴ� ID ���̴�.
	 * clipPlayerId�� ���� Ŭ�� ����� �ν��Ͻ��� �̹� �����ϴ� ��쿡�� ������ Ŭ�� ����� �ν��Ͻ��� ��ȯ�ϰ�, �������� ������ ���� �����Ͽ� ��ȯ�Ѵ�.
	 * </p>
	 * @param context ���ؽ�Ʈ
	 * @param clipPlayerId Ŭ�� ������� ID
	 * <p>
	 * @return Ŭ�� ������� �ν��Ͻ� �Ǵ� null
	 */
	public static ClipPlayer obtain(Context context, int clipPlayerId)
	{
		if(context == null) return null;
		String packageName = context.getPackageName();
		if(packageName == null) return null;
		ClipPlayer clipPlayer = null;
		synchronized(mClipPlayers)
		{
			clipPlayer = mClipPlayers.get(clipPlayerId);
			if(clipPlayer == null)
			{
				clipPlayer = new ClipPlayer(packageName, clipPlayerId);
				mClipPlayers.put(clipPlayerId, clipPlayer);
			}
		}
		clipPlayer.setContext(context);
		return clipPlayer;
	}
	
	/**
	 * <p>���� �ִ� ��� Ŭ�� ������ �ݴ´�.
	 * <p>Ŭ�� ������ ���� �Ŀ��� {@link #play()} Ȥ�� {@link #stop()} �޼ҵ带 ȣ���Ͽ� Ŭ�� ������ ����ϰų� ������ �� ����.
	 * ���ø����̼��� ����Ǳ� ���� �ݵ�� ������ ��� Ŭ�� ����⿡ ���� {@link #close()} Ȥ�� ClipPlayer.closeAll() �޼ҵ带 ȣ���Ͽ� Ŭ�� ������ �ݾƾ� �Ѵ�.
	 */
	public static void closeAll()
	{
		synchronized(mClipPlayers)
		{
			int sz = mClipPlayers.size();
			for(int i = 0; i < sz; ++i)
			{
				mClipPlayers.valueAt(i).release();
			}
			mClipPlayers.clear();
		}
	}
	
	/**
	 * <p>Ŭ�� ������� ID�� ��ȯ�Ѵ�.
	 * </p>
	 * @return Ŭ�� ������� ID
	 */
	public int getId()
	{
		return mID;
	}
	
	private Context getContext()
	{
		if(mContext == null) return null;
		return mContext.get();
	}
	
	void setContext(Context context)
	{
		Context applicationContext = context.getApplicationContext();
		if(applicationContext == null)
			mContext = new WeakReference<Context>(context);
		else
			mContext = new WeakReference<Context>(applicationContext);
	}
	
	/**
	 * <p>Ŭ�� ������ ����� �Ϸ�Ǿ��� �� ȣ��ǵ��� listener�� �����Ѵ�.
	 * </p>
	 * @param listener ������ ������
	 */
	public void setOnCompletedListener(OnCompletedListener listener)
	{
		mOnCompletedListener = listener;
	}
	
	/**
	 * <p>Ŭ�� ������ ���ų� ���, �����ϴ� �������� ������ �߻����� �� ȣ��ǵ��� listener�� �����Ѵ�.
	 * </p>
	 * @param listener ������ ������
	 */
	public void setOnErrorListener(OnErrorListener listener)
	{
		mOnErrorListener = listener;
	}
	
	/**
	 * <p>Ŭ�� ������ ����.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(ClipPlayer clipPlayer)
 {
     clipPlayer.open("http://www.sample.org/sample.mcs");
 }</pre>
	 * 
	 * @param url Ŭ�� ������ URL
	 * <p>
	 * @return �����ϸ� true, �ƴϸ� false
	 */
	public boolean open(String url)
	{
		if(url == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_URL);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mOpen) close();
		mOpen = true;
		
		synchronized(mClipPlayers)
		{
			ClipPlayer player = mClipPlayers.get(getId());
			if(player == null)
				mClipPlayers.put(getId(), this);
		}
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.CLIP_OPEN");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		intent.putExtra("roboid.intent.extra.CLIP_URL", url);
		context.sendBroadcast(intent);
		return true;
	}
	
	/**
	 * <p>Ŭ�� ������ ����.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(ClipPlayer clipPlayer)
 {
     clipPlayer.open("org.smartrobot.sample", R.raw.sample);
 }</pre>
	 * 
	 * @param packageName Ŭ�� ������ �ִ� ��Ű���� �̸�
	 * @param resid Ŭ�� ������ ���ҽ� ID
	 * <p>
	 * @return �����ϸ� true, �ƴϸ� false
	 */
	public boolean open(String packageName, int resid)
	{
		if(packageName == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_PACKAGE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mOpen) close();
		mOpen = true;
		
		synchronized(mClipPlayers)
		{
			ClipPlayer player = mClipPlayers.get(getId());
			if(player == null)
				mClipPlayers.put(getId(), this);
		}
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.CLIP_OPEN");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		intent.putExtra("roboid.intent.extra.CLIP_PACKAGE_NAME", packageName);
		intent.putExtra("roboid.intent.extra.CLIP_RESOURCE_ID", resid);
		context.sendBroadcast(intent);
		return true;
	}
	
	/**
	 * <p>Ŭ�� ������ ����.
	 * <p>Ŭ�� ����(mcs ����)�� raw ������ �־�� �ϸ�, resName�� Ŭ�� ������ Ȯ���ڸ� ������ ���� �̸��̴�.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(ClipPlayer clipPlayer)
 {
     clipPlayer.open("org.smartrobot.sample", "sample");
 }</pre>
	 * 
	 * @param packageName Ŭ�� ������ �ִ� ��Ű���� �̸�
	 * @param resName Ŭ�� ������ ���ҽ� �̸�
	 * <p>
	 * @return �����ϸ� true, �ƴϸ� false
	 */
	public boolean open(String packageName, String resName)
	{
		if(packageName == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_PACKAGE);
			return false;
		}
		if(resName == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_RESOURCE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mOpen) close();
		mOpen = true;
		
		synchronized(mClipPlayers)
		{
			ClipPlayer player = mClipPlayers.get(getId());
			if(player == null)
				mClipPlayers.put(getId(), this);
		}
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.CLIP_OPEN");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		intent.putExtra("roboid.intent.extra.CLIP_PACKAGE_NAME", packageName);
		intent.putExtra("roboid.intent.extra.CLIP_RESOURCE_ID", resName);
		context.sendBroadcast(intent);
		return true;
	}
	
	private void registerBroadcast(Context context)
	{
		if(mBR != null) return;
		mBR = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				String action = intent.getAction();
				if("roboid.intent.action.CLIP_COMPLETION".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_COMPLETION);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.CLIP_ERROR".equals(action))
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
		intentFilter.addAction("roboid.intent.action.CLIP_COMPLETION");
		intentFilter.addAction("roboid.intent.action.CLIP_ERROR");
		context.registerReceiver(mBR, intentFilter);
	}
	
	/**
	 * <p>Ŭ�� ������ �ݴ´�.
	 * <p>Ŭ�� ������ ���� �Ŀ��� {@link #play()} Ȥ�� {@link #stop()} �޼ҵ带 ȣ���Ͽ� Ŭ�� ������ ����ϰų� ������ �� ����.
	 * ���ø����̼��� ����Ǳ� ���� �ݵ�� ������ ��� Ŭ�� ����⿡ ���� close() Ȥ�� {@link #ClipPlayer.closeAll() ClipPlayer.closeAll()} �޼ҵ带 ȣ���Ͽ� Ŭ�� ������ �ݾƾ� �Ѵ�.
	 */
	public void close()
	{
		release();
		synchronized(mClipPlayers)
		{
			mClipPlayers.remove(getId());
		}
	}
	
	private void release()
	{
		if(mPlaying)
			stop();
		mOpen = false;

		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
		}
		else
		{
			if(mRequestCode != null)
			{
				Intent intent = new Intent("roboid.intent.action.CLIP_CLOSE");
				intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
				context.sendBroadcast(intent);
			}
			if(mBR != null)
			{
				context.unregisterReceiver(mBR);
				mBR = null;
			}
		}
	}
	
	/**
	 * <p>Ŭ�� ������ ����Ѵ�.
	 * <p>{@link #close()} Ȥ�� {@link #ClipPlayer.closeAll() ClipPlayer.closeAll()} �޼ҵ带 ȣ���Ͽ� Ŭ�� ������ ���� �Ŀ��� Ŭ�� ������ ������� �ʰ� false�� ��ȯ�Ѵ�.
	 * </p>
	 * @return �����ϸ� true, �ƴϸ� false
	 */
	public boolean play()
	{
		if(mOpen == false)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mPlaying)
			stop();
		mPlaying = true;
		
		Intent intent = new Intent("roboid.intent.action.CLIP_PLAY");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		context.sendBroadcast(intent);
		return true;
	}
	
	/**
	 * <p>Ŭ�� ������ ����� �����Ѵ�.
	 * <p>{@link #close()} Ȥ�� {@link #ClipPlayer.closeAll() ClipPlayer.closeAll()} �޼ҵ带 ȣ���Ͽ� Ŭ�� ������ ���� �Ŀ��� false�� ��ȯ�Ѵ�.
	 * </p>
	 * @return �����ϸ� true, �ƴϸ� false
	 */
	public boolean stop()
	{
		if(mOpen == false)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		mPlaying = false;
		
		Intent intent = new Intent("roboid.intent.action.CLIP_STOP");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		context.sendBroadcast(intent);
		return true;
	}
	
	private static class EventHandler extends Handler
	{
		private final ClipPlayer mClipPlayer;
		
		EventHandler(ClipPlayer clipPlayer)
		{
			super();
			mClipPlayer = clipPlayer;
		}
		
		EventHandler(ClipPlayer clipPlayer, Looper looper)
		{
			super(looper);
			mClipPlayer = clipPlayer;
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case MSG_COMPLETION:
				{
					if(mClipPlayer == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String requestCode = intent.getStringExtra("roboid.intent.extra.CLIP_REQUEST_CODE");
					if(requestCode == null || !requestCode.equals(mClipPlayer.mRequestCode))
						return;
					
					mClipPlayer.mPlaying = false;
					ClipPlayer.OnCompletedListener listener = mClipPlayer.mOnCompletedListener;
					if(listener != null)
						listener.onCompleted(mClipPlayer);
				}
				break;
			case MSG_ERROR:
				{
					if(mClipPlayer == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String requestCode = intent.getStringExtra("roboid.intent.extra.CLIP_REQUEST_CODE");
					if(requestCode == null || !requestCode.equals(mClipPlayer.mRequestCode))
						return;
					
					int errorCode = intent.getIntExtra("roboid.intent.extra.CLIP_ERROR", ERROR_NONE);
					if(errorCode != ERROR_NONE)
					{
						ClipPlayer.OnErrorListener listener = mClipPlayer.mOnErrorListener;
						if(listener != null)
							listener.onError(mClipPlayer, errorCode);
					}
				}
				break;
			}
		}
	}
}
