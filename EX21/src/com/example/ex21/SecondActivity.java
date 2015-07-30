package com.example.ex21;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends Activity {
	
	TextView textview;
	ImageView receive_image;
	
	int[] array = { R.drawable.a, R.drawable.b, R.drawable.c };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		textview		=	(TextView)findViewById(R.id.text);
		receive_image	=	(ImageView)findViewById(R.id.receive_image);
		
		Intent intent = getIntent();
		String text = intent.getStringExtra("text");
		
		receive_image.setVisibility(View.VISIBLE);
		receive_image.setImageResource(array[Integer.valueOf(text)-1]);	//(array[Integer.valueOf(text)-1]);
		
	}
}
