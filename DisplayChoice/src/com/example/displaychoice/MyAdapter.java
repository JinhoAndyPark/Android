package com.example.displaychoice;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends ArrayAdapter<AppData>{
	
	//레이아웃 XML을 읽기 위한 객체
	private final LayoutInflater mInflater;
	
	public MyAdapter(Context context, ArrayList<AppData> object)
	{
		//상위 클래스의 초기화 과정
		//context, 0, 자료구조
		super(context, 0, object);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	//보여지는 스타일을 자신이 만든 xml로 보이기 위한 구문
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = null;
		
		//현재 리스트의 하나의 항목에 보일 컨트롤 얻기
		if(convertView == null)
		{
			//XML 레이아웃을 직접 읽어서 리스트뷰에 넣음.
			view = mInflater.inflate(R.layout.row, parent, false);
		}
		else
		{
			view = convertView;
		}
		
		//데이터를 받는다.
		final AppData data = this.getItem(position);
		
		//데이터가 있으면 화면 출력
		if(data != null)
		{
			((ImageView)view.findViewById(R.id.app_icon)).setImageDrawable(data.get_image());
			((TextView)view.findViewById(R.id.app_name)).setText(data.get_name());
			((TextView)view.findViewById(R.id.app_package)).setText(data.get_package());
		}
		
		return view;
	}
}