package com.example.displaychoice;

import android.graphics.drawable.Drawable;

//리스트뷰에 뿌려줄 값들 저장하는 class
public class AppData {
	private Drawable app_image;
	private String app_name;
	private String app_package;
	
	//생성자
	public AppData(Drawable a_image, String a_name, String a_package)
	{
		app_image = a_image;
		app_name = a_name;
		app_package = a_package;
	}
	
	//get function
	public Drawable get_image()
	{
		return app_image;
	}
	
	public String get_name()
	{
		return app_name;
	}
	
	public String get_package()
	{
		return app_package;
	}
}