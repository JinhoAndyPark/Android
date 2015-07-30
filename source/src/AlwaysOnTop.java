package com.example.displaychoice;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;

//앱이 종료된 후에도 항상 해당 뷰가 떠 있어야 한다. 그래서 Activity에서 뷰를 추가하는 것이 아니라 Service에서 뷰를 추가해야됨.
public class AlwaysOnTop extends Service {
	SharedPreferences runCheck;
	SharedPreferences.Editor edit;
	Boolean flag = false;
	
	private float START_X, START_Y;
	private int PREV_X, PREV_Y;
	private int MAX_X = -1, MAX_Y = -1;
	
	private Button mPopupBtn; // 항상 보이게 할 버튼
	private WindowManager.LayoutParams mParams; // LayoutParams 객체. 뷰의 위치 및 크기 설정
	private WindowManager mWindowManager; // 윈도우매니저
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		runCheck = getSharedPreferences("hasRunBefore", MODE_PRIVATE);
		
		mPopupBtn = new Button(this); // 뷰 생성
		mPopupBtn.setBackgroundResource(R.drawable.btn_image); //버튼 이미지 설정
		mPopupBtn.setOnTouchListener(mViewTouchListener); //생성한 뷰에 터치 리스너 등록
		mPopupBtn.setOnClickListener(mViewClickListener); //생성한 뷰에 클릭 리스너 등록
		mPopupBtn.setOnLongClickListener(mViewLongClickListener); //생성한 뷰에 롱클릭 리스너 등록
		
		// 최상위 윈도우에 넣기 위한 설정
		// 이전에는 TYPE을 TYPE_SYSTEM_OVERLAY로 주었는데 이 경우 화면 전체를 대상으로 뷰를 넣지만 터치 이벤트를 받지는 못한다.
		// 그래서 TYPE을 TYPE_PHONE으로 설정하였다. 하지만 이 경우에는 뷰가 Focus를 가지고 있어 뷰 이외의 부분의 터치가 불가능하다.
		// 이를 해결하기 위해 FLAG 값으로 FLAG_NOT_FOCUSABLE을 주어 뷰가 포커스를 가지지 않아 뷰 이외의 부분의 터치 이벤트와 키 이벤트를 먹지 않아서 자연스럽게 동작하도록 함.
		mParams = new WindowManager.LayoutParams(
				90,
				90,
				WindowManager.LayoutParams.TYPE_PHONE, // 터치 이벤트 받을 수 있음
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // 포커스를 가지지 않음
				PixelFormat.TRANSPARENT); // 투명하게 설정
		mParams.gravity = Gravity.LEFT | Gravity.TOP; // 왼쪽 위에 위치하게 함.
		
		//재 실행시 이전 위치에 뷰 띄워주기 -> SharedPreferences 사용해서 실행여부 판단한 뒤 이전위치 저장한 뒤 표시
		if(runCheck.getBoolean("hasRun", false)) //최초 실행이 아닐때는 true return
		{
			mParams.x = runCheck.getInt("param_x", 0);
			mParams.y = runCheck.getInt("param_y", 0);
		}
		else
		{
			edit = runCheck.edit();
			edit.putBoolean("hasRun", true);
			edit.commit();
		}
		
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE); // 윈도우매니저
		mWindowManager.addView(mPopupBtn, mParams); // 윈도우에 뷰 넣기. 'SYSTEM_ALERT_WINDOW' permission 필요.
	}
	
	// 터치 리스너
	private OnTouchListener mViewTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN: //사용자 터치 다운이면
				//Log.d("test", "터치");
				if(MAX_X == -1)
					setMaxPosition();
				
				START_X = event.getRawX(); //터치 시작 X
				START_Y = event.getRawY(); //터치 시작 Y
				PREV_X = mParams.x; //뷰의 시작 X
				PREV_Y = mParams.y; //뷰의 시작 Y
				
            	break;
            	
			case MotionEvent.ACTION_MOVE:
				if(flag == true)
				{
					//Log.d("test", "이동");
					
					int x = (int)(event.getRawX() - START_X); //이동한 거리
					int y = (int)(event.getRawY() - START_Y); //이동한 거리
					
					//터치해서 이동한만큼 이동시킨다.
					mParams.x = PREV_X + x;
					mParams.y = PREV_Y + y;
					
					optimizePosition(); //뷰의 위치 최적화­
					mWindowManager.updateViewLayout(mPopupBtn, mParams); //뷰 업데이트
				}
				break;
			case MotionEvent.ACTION_UP:
				if(flag == true)
				{
					flag = false;
				}
				break;
        }
			
			return flag;
		}
	};
	
	// 클릭 리스너
	Button.OnClickListener mViewClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(AlwaysOnTop.this, AppListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	};
	
	// 롱클릭 리스너
	Button.OnLongClickListener mViewLongClickListener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			flag = true;
			
			return true;
		}
	};
	
	@Override
    public IBinder onBind(Intent intent) {
        return null;
    }
	
	@Override
	public void onDestroy() { // 서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
		if(mWindowManager != null)
		{
			if(mPopupBtn != null)
            {
            	mWindowManager.removeView(mPopupBtn);
            	
            	//뷰 위치 저장
            	edit = runCheck.edit();
        		edit.putInt("param_x", mParams.x);
        		edit.putInt("param_y", mParams.y);
        		edit.commit();
            }
        }
		
		super.onDestroy();
	}
	
	//뷰의 위치가 화면 안에 있게 최대값을 설정
	private void setMaxPosition() {
		DisplayMetrics matrix = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(matrix); //화면 정보를 가져와서­
		
		MAX_X = matrix.widthPixels - mPopupBtn.getWidth(); //x 최대값 설정
		MAX_Y = matrix.heightPixels - mPopupBtn.getHeight();	 //y 최대값 설정
	}
	
	//뷰의 위치가 화면 안에 있게 하기 위해서 검사하고 수정
	private void optimizePosition() {
		//최대값 넘어가지 않게 설정
		if(mParams.x >= MAX_X) mParams.x = MAX_X;
		if(mParams.y >= MAX_Y) mParams.y = MAX_Y;
		if(mParams.x <= 0) mParams.x = 0;
		if(mParams.y <= 0) mParams.y = 0;
	}
	
	//가로/세로모드 변경 시 최대값 다시 설정해 주어야함.
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setMaxPosition(); //최대값 다시 설정
		optimizePosition(); //뷰 위치 최적화
	}
}