package com.example.displaychoice;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class AppListActivity extends Activity{
	LinearLayout bg, rv;
	
	private ListView listview; //리스트뷰 선언
	MyAdapter adapter; //데이터를 연결할 Adapter
	ArrayList<AppData> m_data; //데이터를 담을 자료구조
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stopService(new Intent(this, AlwaysOnTop.class)); //이전 서비스 종료
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND); //뒷 배경 투명 처리
		
		bg = (LinearLayout)findViewById(R.id.background);
		rv = (LinearLayout)findViewById(R.id.realview);
		
		listview = (ListView)findViewById(R.id.applist);
		m_data = new ArrayList<AppData>(); //객체 생성
		adapter = new MyAdapter(this, m_data); //데이터를 받기위해 데이터어댑터 객체 선언
		
		listview.setAdapter(adapter); //리스트뷰에 어댑터 연결
		
		//설치된 어플 정보 불러오기
		PackageManager packagemanager = this.getPackageManager();
		
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		List<ResolveInfo>list = packagemanager.queryIntentActivities(mainIntent, 0);
		for(ResolveInfo info:list)
		{
			//Log.d("App Name", info.activityInfo.applicationInfo.loadLabel(packagemanager).toString());
			//Log.d("Package Name", info.activityInfo.packageName.toString());
			
			//ArrayAdapter를 통해서 ArrayList에 자료 저장
			AppData d = new AppData(info.activityInfo.loadIcon(packagemanager), info.activityInfo.applicationInfo.loadLabel(packagemanager).toString(), info.activityInfo.packageName.toString());
			m_data.add(d);
		}
		
		//리스트뷰 아이템 클릭
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				final AppData data = m_data.get(position);
				
				//Builder로 대화상자의 UI 꾸미기
				AlertDialog dialog = new AlertDialog.Builder(v.getContext()).setIcon(R.drawable.btn_image).setTitle("Display 선택")
				.setMessage("선택한 어플리케이션을 실행할 Display를 선택해주세요.")
				.setNegativeButton("스마트폰 LCD", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						Intent i = getPackageManager().getLaunchIntentForPackage(data.get_package());
						startActivity(i);
						
						finish();
					}
				})
				.setPositiveButton("외부 LCD", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						startService(new Intent(AppListActivity.this, FramebufferSend.class));
						finish();	
					}
				}).create(); //create()로 생성
				
				dialog.setCanceledOnTouchOutside(false); //바깥영역 터치 유무 설정
				dialog.show(); //대화상자 보여주기
			}
		});
		
		bg.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if(event.getRawX() > rv.getWidth())
					{
						finish();
					}
					
					break;
				}
				
				return true;
			}
		});
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		startService(new Intent(this, AlwaysOnTop.class)); //최상위 뷰 띄워주는 서비스 실행
	}
	
}